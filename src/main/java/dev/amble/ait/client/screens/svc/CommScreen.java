package dev.amble.ait.client.screens.svc;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import dev.amble.ait.AITMod;
import dev.amble.ait.client.screens.TardisScreen;
import dev.amble.ait.client.tardis.ClientTardis;
import dev.amble.ait.core.tardis.comms.TardisComms;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import org.jetbrains.annotations.Nullable;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class CommScreen extends TardisScreen {

    private static final Identifier TEXTURE = AITMod.id("textures/gui/tardis/monitor/call_gui.png");

    private static final int WIDTH = 256;
    private static final int HEIGHT = 166;
    private static final int TEXTURE_V = 0;

    private static final int HEADER_X = 12;
    private static final int HEADER_Y = 12;
    private static final int HEADER_WIDTH = 230;

    private static final int LIST_X = 28;
    private static final int LIST_Y = 62;
    private static final int LIST_WIDTH = 99;

    private static final int SIDE_X = 163;
    private static final int SIDE_Y = 62;
    private static final int SIDE_WIDTH = 63;

    private static final int PHONE_X = 135;
    private static final int ANSWER_Y = 72;
    private static final int HANG_UP_Y = 102;

    private static final int ROW_HEIGHT = 12;
    private static final int ROWS = 7;

    private static final int SCREEN_TEXT = 0x55FF55;
    private static final int SCREEN_TEXT_DIM = 0x2E8B2E;

    private record Contact(UUID id, String name) {
    }

    private record Message(UUID id, String from, long time, int seconds) {
    }

    private record State(TardisComms.Status status, String other, boolean handset, List<Contact> contacts,
                         List<Message> messages) {
    }

    @Nullable private static State latest;

    private boolean showingMessages = false;
    private int page = 0;
    private int ticks = 0;

    public CommScreen(ClientTardis tardis) {
        super(Text.translatable("screen.ait.comms.title"), tardis);
    }

    public static void registerClient() {
        ClientPlayNetworking.registerGlobalReceiver(TardisComms.STATE, (client, handler, buf, responseSender) -> {
            State state = read(buf);

            client.execute(() -> {
                latest = state;

                if (client.currentScreen instanceof CommScreen screen)
                    screen.rebuild();
            });
        });
    }

    private static State read(PacketByteBuf buf) {
        TardisComms.Status status = buf.readEnumConstant(TardisComms.Status.class);
        String other = buf.readString();
        boolean handset = buf.readBoolean();

        int contactCount = buf.readVarInt();
        List<Contact> contacts = new ArrayList<>(contactCount);

        for (int i = 0; i < contactCount; i++)
            contacts.add(new Contact(buf.readUuid(), buf.readString()));

        int messageCount = buf.readVarInt();
        List<Message> messages = new ArrayList<>(messageCount);

        for (int i = 0; i < messageCount; i++)
            messages.add(new Message(buf.readUuid(), buf.readString(), buf.readLong(), buf.readVarInt()));

        return new State(status, other, handset, contacts, messages);
    }

    @Override
    protected void init() {
        latest = null;
        this.request();
        this.rebuild();
    }

    @Override
    public void tick() {
        super.tick();

        if (++this.ticks % 20 == 0)
            this.request();
    }

    private int left() {
        return (this.width - WIDTH) / 2;
    }

    private int top() {
        return (this.height - HEIGHT) / 2;
    }

    private void rebuild() {
        this.clearChildren();

        State state = latest;

        if (state == null)
            return;

        int left = this.left();
        int top = this.top();

        int size = this.showingMessages ? state.messages().size() : state.contacts().size();
        int pages = Math.max(1, (size + ROWS - 1) / ROWS);
        this.page = Math.max(0, Math.min(this.page, pages - 1));

        boolean idle = state.status() == TardisComms.Status.IDLE;

        for (int i = 0; i < ROWS; i++) {
            int index = this.page * ROWS + i;

            if (index >= size)
                break;

            int x = left + LIST_X;
            int y = top + LIST_Y + i * ROW_HEIGHT;

            if (this.showingMessages) {
                Message message = state.messages().get(index);
                SpriteButton row = this.add(x, y, LIST_WIDTH, Text.translatable("screen.ait.comms.message", message.from(), message.seconds()),
                        button -> this.action(Screen.hasShiftDown() ? TardisComms.Action.DELETE : TardisComms.Action.PLAY, message.id()));

                row.setTooltip(Tooltip.of(Text.translatable("screen.ait.comms.message.tooltip")));
            } else {
                Contact contact = state.contacts().get(index);
                SpriteButton row = this.add(x, y, LIST_WIDTH, Text.literal(contact.name()),
                        button -> this.action(TardisComms.Action.CALL, contact.id()));

                row.setTooltip(Tooltip.of(Text.literal(contact.name())));
                row.active = idle;
            }
        }

        int x = left + SIDE_X;
        int y = top + SIDE_Y;

        this.add(x, y, SIDE_WIDTH, Text.translatable(this.showingMessages ? "screen.ait.comms.tab.calls" : "screen.ait.comms.tab.messages"),
                button -> {
                    this.showingMessages = !this.showingMessages;
                    this.page = 0;
                    this.rebuild();
                });

        SpriteButton up = this.add(x, y + ROW_HEIGHT, SIDE_WIDTH, Text.literal("<"), button -> this.turnPage(-1));
        SpriteButton down = this.add(x, y + ROW_HEIGHT * 2, SIDE_WIDTH, Text.literal(">"), button -> this.turnPage(1));

        up.active = this.page > 0;
        down.active = this.page < pages - 1;

        this.addModeButton(x, y + ROW_HEIGHT * 3, state);

        SpriteButton broadcast = this.add(x, y + ROW_HEIGHT * 4, SIDE_WIDTH, Text.translatable("screen.ait.comms.short.broadcast"),
                button -> this.broadcast());
        broadcast.setTooltip(Tooltip.of(Text.translatable("screen.ait.comms.broadcast")));

        TardisComms.Status status = state.status();

        PhoneButton answer = this.addDrawableChild(new PhoneButton(left + PHONE_X, top + ANSWER_Y, PhoneButton.ANSWER_V,
                Text.translatable("screen.ait.comms.answer"), button -> this.action(TardisComms.Action.ANSWER, null)));
        answer.active = status == TardisComms.Status.INCOMING;
        answer.setTooltip(Tooltip.of(Text.translatable("screen.ait.comms.answer")));

        TardisComms.Action hangUpAction = status == TardisComms.Status.INCOMING
                ? TardisComms.Action.DECLINE : TardisComms.Action.HANG_UP;
        Text hangUpText = Text.translatable(switch (status) {
            case INCOMING -> "screen.ait.comms.decline";
            case RECORDING -> "screen.ait.comms.finish_message";
            default -> "screen.ait.comms.hang_up";
        });

        PhoneButton hangUp = this.addDrawableChild(new PhoneButton(left + PHONE_X, top + HANG_UP_Y, PhoneButton.HANG_UP_V,
                hangUpText, button -> this.action(hangUpAction, null)));
        hangUp.active = status != TardisComms.Status.IDLE;
        hangUp.setTooltip(Tooltip.of(hangUpText));
    }

    private void addModeButton(int x, int y, State state) {
        SpriteButton mode = this.add(x, y, SIDE_WIDTH, Text.translatable(state.handset()
                ? "screen.ait.comms.short.handset" : "screen.ait.comms.short.speaker"),
                button -> this.action(TardisComms.Action.TOGGLE_MODE, null));

        mode.setTooltip(Tooltip.of(Text.translatable(state.handset()
                ? "screen.ait.comms.mode.handset" : "screen.ait.comms.mode.speaker")));
    }

    private SpriteButton add(int x, int y, int width, Text text, ButtonWidget.PressAction action) {
        return this.addDrawableChild(new SpriteButton(x, y, width, text, action));
    }

    private void turnPage(int delta) {
        this.page += delta;
        this.rebuild();
    }

    private void request() {
        if (this.tardis() == null)
            return;

        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeUuid(this.tardis().getUuid());
        ClientPlayNetworking.send(TardisComms.REQUEST, buf);
    }

    private void action(TardisComms.Action action, @Nullable UUID argument) {
        if (this.tardis() == null)
            return;

        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeUuid(this.tardis().getUuid());
        buf.writeEnumConstant(action);
        buf.writeBoolean(argument != null);

        if (argument != null)
            buf.writeUuid(argument);

        ClientPlayNetworking.send(TardisComms.ACTION, buf);
    }

    private void broadcast() {
        if (this.tardis() == null)
            return;

        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeUuid(this.tardis().getUuid());
        ClientPlayNetworking.send(AITMod.TARDIS_BROADCAST, buf);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);

        int left = this.left();
        int top = this.top();

        context.drawTexture(TEXTURE, left, top, 0, TEXTURE_V, WIDTH, HEIGHT, 256, 256);

        State state = latest;
        int textX = left + HEADER_X + 3;
        int textWidth = HEADER_WIDTH - 6;

        this.screenLine(context, this.title, textX, top + HEADER_Y + 2, textWidth, SCREEN_TEXT);

        if (state == null) {
            this.screenLine(context, Text.translatable("screen.ait.comms.loading"), textX, top + HEADER_Y + 14, textWidth, SCREEN_TEXT_DIM);
        } else {
            this.screenLine(context, statusText(state), textX, top + HEADER_Y + 14, textWidth, SCREEN_TEXT);
            this.screenLine(context, Text.translatable(this.showingMessages ? "screen.ait.comms.messages" : "screen.ait.comms.contacts"),
                    textX, top + HEADER_Y + 36, textWidth, SCREEN_TEXT_DIM);

            boolean empty = this.showingMessages ? state.messages().isEmpty() : state.contacts().isEmpty();

            if (empty)
                this.listNote(context, Text.translatable(this.showingMessages
                        ? "screen.ait.comms.no_messages" : "screen.ait.comms.no_contacts"), left, top);
        }

        super.render(context, mouseX, mouseY, delta);
    }

    private void screenLine(DrawContext context, Text text, int x, int y, int width, int color) {
        context.drawText(this.textRenderer, this.textRenderer.trimToWidth(text, width).getString(), x, y, color, false);
    }

    private void listNote(DrawContext context, Text text, int left, int top) {
        List<OrderedText> lines = this.textRenderer.wrapLines(text, LIST_WIDTH - 4);

        for (int i = 0; i < lines.size(); i++)
            context.drawText(this.textRenderer, lines.get(i), left + LIST_X + 2, top + LIST_Y + 2 + i * 10, 0x3F3F3F, false);
    }

    private static Text statusText(State state) {
        return switch (state.status()) {
            case IDLE -> Text.translatable("screen.ait.comms.status.idle");
            case OUTGOING -> Text.translatable("screen.ait.comms.status.outgoing", state.other());
            case INCOMING -> Text.translatable("screen.ait.comms.status.incoming", state.other());
            case CONNECTED -> Text.translatable("screen.ait.comms.status.connected", state.other());
            case RECORDING -> Text.translatable("screen.ait.comms.status.recording", state.other());
        };
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    private static final class PhoneButton extends ButtonWidget {

        private static final int U = 56;
        private static final int ANSWER_V = 200;
        private static final int HANG_UP_V = 221;
        private static final int SIZE_X = 20;
        private static final int SIZE_Y = 21;

        private final int v;

        private PhoneButton(int x, int y, int v, Text message, PressAction action) {
            super(x, y, SIZE_X, SIZE_Y, message, action, DEFAULT_NARRATION_SUPPLIER);
            this.v = v;
        }

        @Override
        protected void renderButton(DrawContext context, int mouseX, int mouseY, float delta) {
            int x = this.getX();
            int y = this.getY();

            context.drawTexture(TEXTURE, x, y, U, this.v, SIZE_X, SIZE_Y, 256, 256);

            if (!this.active)
                context.fill(x, y, x + SIZE_X, y + SIZE_Y, 0x99404040);
            else if (this.isSelected())
                context.fill(x, y, x + SIZE_X, y + SIZE_Y, 0x30FFFFFF);
        }
    }

    private static final class SpriteButton extends ButtonWidget {

        private static final int SPRITE_U = 2;
        private static final int SPRITE_V = 200;
        private static final int SPRITE_HOVER_V = 212;
        private static final int SPRITE_WIDTH = 52;
        private static final int CAP = 3;

        private SpriteButton(int x, int y, int width, Text message, PressAction action) {
            super(x, y, width, ROW_HEIGHT, message, action, DEFAULT_NARRATION_SUPPLIER);
        }

        @Override
        protected void renderButton(DrawContext context, int mouseX, int mouseY, float delta) {
            int v = this.active && this.isSelected() ? SPRITE_HOVER_V : SPRITE_V;
            int x = this.getX();
            int y = this.getY();
            int middle = this.width - CAP * 2;

            context.drawTexture(TEXTURE, x, y, SPRITE_U, v, CAP, this.height, 256, 256);
            context.drawTexture(TEXTURE, x + CAP, y, middle, this.height, SPRITE_U + CAP, v,
                    SPRITE_WIDTH - CAP * 2, this.height, 256, 256);
            context.drawTexture(TEXTURE, x + this.width - CAP, y, SPRITE_U + SPRITE_WIDTH - CAP, v, CAP, this.height, 256, 256);

            TextRenderer font = MinecraftClient.getInstance().textRenderer;
            String label = font.trimToWidth(this.getMessage(), this.width - 4).getString();
            int color = this.active ? 0x202020 : 0x7A7A7A;

            context.drawText(font, label, x + (this.width - font.getWidth(label)) / 2, y + 2, color, false);
        }
    }
}
