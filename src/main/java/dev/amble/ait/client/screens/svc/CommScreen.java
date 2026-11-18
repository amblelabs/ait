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

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class CommScreen extends TardisScreen {

    private static final int PANEL_WIDTH = 340;
    private static final int PANEL_HEIGHT = 250;
    private static final int ROWS = 6;
    private static final int ROW_HEIGHT = 22;

    private record Contact(UUID id, String name) {
    }

    private record Message(UUID id, String from, long time, int seconds) {
    }

    private record State(TardisComms.Status status, String other, boolean handset, List<Contact> contacts,
                         List<Message> messages) {
    }

    @Nullable private static State latest;

    private int contactPage = 0;
    private int messagePage = 0;
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
        return (this.width - PANEL_WIDTH) / 2;
    }

    private int top() {
        return (this.height - PANEL_HEIGHT) / 2;
    }

    private void rebuild() {
        this.clearChildren();

        int left = this.left();
        int top = this.top();
        State state = latest;

        this.addDrawableChild(ButtonWidget.builder(Text.translatable("screen.ait.comms.broadcast"), button -> this.broadcast())
                .dimensions(left + PANEL_WIDTH - 110, top + 6, 104, 18).build());

        if (state == null)
            return;

        this.addDrawableChild(ButtonWidget.builder(Text.translatable(state.handset()
                                ? "screen.ait.comms.mode.handset" : "screen.ait.comms.mode.speaker"),
                        button -> this.action(TardisComms.Action.TOGGLE_MODE, null))
                .dimensions(left + PANEL_WIDTH - 110, top + 28, 104, 18).build());

        int actionY = top + 50;

        switch (state.status()) {
            case INCOMING -> {
                this.addDrawableChild(ButtonWidget.builder(Text.translatable("screen.ait.comms.answer").formatted(Formatting.GREEN),
                        button -> this.action(TardisComms.Action.ANSWER, null)).dimensions(left + 8, actionY, 80, 18).build());
                this.addDrawableChild(ButtonWidget.builder(Text.translatable("screen.ait.comms.decline").formatted(Formatting.RED),
                        button -> this.action(TardisComms.Action.DECLINE, null)).dimensions(left + 92, actionY, 80, 18).build());
            }
            case OUTGOING, CONNECTED -> this.addDrawableChild(ButtonWidget.builder(Text.translatable("screen.ait.comms.hang_up").formatted(Formatting.RED),
                    button -> this.action(TardisComms.Action.HANG_UP, null)).dimensions(left + 8, actionY, 80, 18).build());
            case RECORDING -> this.addDrawableChild(ButtonWidget.builder(Text.translatable("screen.ait.comms.finish_message"),
                    button -> this.action(TardisComms.Action.HANG_UP, null)).dimensions(left + 8, actionY, 120, 18).build());
            default -> { }
        }

        int listTop = top + 86;
        int columnWidth = PANEL_WIDTH / 2 - 12;
        boolean idle = state.status() == TardisComms.Status.IDLE;

        this.contactPage = clampPage(this.contactPage, state.contacts().size());

        for (int i = 0; i < ROWS; i++) {
            int index = this.contactPage * ROWS + i;

            if (index >= state.contacts().size())
                break;

            Contact contact = state.contacts().get(index);
            ButtonWidget button = ButtonWidget.builder(Text.literal(contact.name()),
                    widget -> this.action(TardisComms.Action.CALL, contact.id()))
                    .dimensions(left + 8, listTop + i * ROW_HEIGHT, columnWidth, 18).build();

            button.active = idle;
            this.addDrawableChild(button);
        }

        this.addPager(left + 8, listTop + ROWS * ROW_HEIGHT, state.contacts().size(), true);

        this.messagePage = clampPage(this.messagePage, state.messages().size());
        int messageLeft = left + PANEL_WIDTH / 2 + 4;

        for (int i = 0; i < ROWS; i++) {
            int index = this.messagePage * ROWS + i;

            if (index >= state.messages().size())
                break;

            Message message = state.messages().get(index);
            int y = listTop + i * ROW_HEIGHT;

            this.addDrawableChild(ButtonWidget.builder(Text.translatable("screen.ait.comms.message", message.from(), message.seconds()),
                    widget -> this.action(TardisComms.Action.PLAY, message.id()))
                    .dimensions(messageLeft, y, columnWidth - 22, 18).build());
            this.addDrawableChild(ButtonWidget.builder(Text.literal("x").formatted(Formatting.RED),
                    widget -> this.action(TardisComms.Action.DELETE, message.id()))
                    .dimensions(messageLeft + columnWidth - 20, y, 20, 18).build());
        }

        this.addPager(messageLeft, listTop + ROWS * ROW_HEIGHT, state.messages().size(), false);
    }

    private void addPager(int x, int y, int size, boolean contacts) {
        int pages = Math.max(1, (size + ROWS - 1) / ROWS);

        if (pages <= 1)
            return;

        int page = contacts ? this.contactPage : this.messagePage;

        ButtonWidget previous = ButtonWidget.builder(Text.literal("<"), button -> this.turnPage(contacts, -1))
                .dimensions(x, y, 20, 16).build();
        ButtonWidget next = ButtonWidget.builder(Text.literal(">"), button -> this.turnPage(contacts, 1))
                .dimensions(x + 24, y, 20, 16).build();

        previous.active = page > 0;
        next.active = page < pages - 1;

        this.addDrawableChild(previous);
        this.addDrawableChild(next);
    }

    private void turnPage(boolean contacts, int delta) {
        if (contacts)
            this.contactPage += delta;
        else
            this.messagePage += delta;

        this.rebuild();
    }

    private static int clampPage(int page, int size) {
        int pages = Math.max(1, (size + ROWS - 1) / ROWS);
        return Math.max(0, Math.min(page, pages - 1));
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

        context.fill(left, top, left + PANEL_WIDTH, top + PANEL_HEIGHT, 0xC0101018);
        context.drawBorder(left, top, PANEL_WIDTH, PANEL_HEIGHT, 0xFFFF9600);
        context.drawTextWithShadow(this.textRenderer, this.title, left + 8, top + 8, 0xFFFFFF);

        State state = latest;
        Text status = state == null ? Text.translatable("screen.ait.comms.loading") : statusText(state);
        context.drawTextWithShadow(this.textRenderer, status, left + 8, top + 30, 0xFFFFFF);

        int listTop = top + 86;
        context.drawTextWithShadow(this.textRenderer, Text.translatable("screen.ait.comms.contacts").formatted(Formatting.GOLD), left + 8, listTop - 12, 0xFFFFFF);
        context.drawTextWithShadow(this.textRenderer, Text.translatable("screen.ait.comms.messages").formatted(Formatting.GOLD), left + PANEL_WIDTH / 2 + 4, listTop - 12, 0xFFFFFF);

        if (state != null && state.contacts().isEmpty())
            context.drawTextWithShadow(this.textRenderer, Text.translatable("screen.ait.comms.no_contacts").formatted(Formatting.GRAY), left + 8, listTop + 4, 0xFFFFFF);

        if (state != null && state.messages().isEmpty())
            context.drawTextWithShadow(this.textRenderer, Text.translatable("screen.ait.comms.no_messages").formatted(Formatting.GRAY), left + PANEL_WIDTH / 2 + 4, listTop + 4, 0xFFFFFF);

        super.render(context, mouseX, mouseY, delta);
    }

    private static Text statusText(State state) {
        return switch (state.status()) {
            case IDLE -> Text.translatable("screen.ait.comms.status.idle").formatted(Formatting.GRAY);
            case OUTGOING -> Text.translatable("screen.ait.comms.status.outgoing", state.other()).formatted(Formatting.GOLD);
            case INCOMING -> Text.translatable("screen.ait.comms.status.incoming", state.other()).formatted(Formatting.GOLD);
            case CONNECTED -> Text.translatable("screen.ait.comms.status.connected", state.other()).formatted(Formatting.GREEN);
            case RECORDING -> Text.translatable("screen.ait.comms.status.recording", state.other()).formatted(Formatting.RED);
        };
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
