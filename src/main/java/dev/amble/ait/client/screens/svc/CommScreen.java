package dev.amble.ait.client.screens.svc;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import dev.amble.ait.AITMod;
import dev.amble.ait.client.screens.TardisScreen;
import dev.amble.ait.client.tardis.ClientTardis;
import dev.amble.ait.compat.svc.CommsHandler;
import dev.amble.ait.core.tardis.comms.PhoneDirectory;
import dev.amble.ait.core.tardis.comms.TardisComms;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
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

    private static final Identifier KEYPAD_TEXTURE = AITMod.id("textures/gui/keypad.png");
    private static final int KEYPAD_GAP = 4;
    private static final int KEYPAD_WIDTH = 116;
    private static final int KEYPAD_HEIGHT = 138;
    private static final int KEYPAD_FIELD_Y = 11;
    private static final int KEYPAD_KEYS_Y = 37;
    private static final int KEYPAD_TEXT = 0x202020;
    private static final int KEYPAD_HINT = 0x4A4A4A;
    private static final char BACKSPACE = '<';
    private static final char DIAL = '\n';
    private static final char[] KEYS = { '1', '2', '3', '4', '5', '6', '7', '8', '9', BACKSPACE, '0', DIAL };

    private record Contact(UUID id, String name) {
    }

    private record Message(UUID id, String from, long time, int seconds) {
    }

    private record State(TardisComms.Status status, String other, boolean handset, String number, List<Contact> contacts,
                         List<Message> messages) {
    }

    @Nullable private static State latest;

    private boolean showingMessages = false;
    private int page = 0;
    private int ticks = 0;
    private boolean keypadOpen = false;
    private int copiedTicks;
    private final StringBuilder digits = new StringBuilder();
    private final RotaryClicks clicks = new RotaryClicks();
    @Nullable private PhoneKeyButton dialButton;

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
        String number = buf.readString();

        int contactCount = buf.readVarInt();
        List<Contact> contacts = new ArrayList<>(contactCount);

        for (int i = 0; i < contactCount; i++)
            contacts.add(new Contact(buf.readUuid(), buf.readString()));

        int messageCount = buf.readVarInt();
        List<Message> messages = new ArrayList<>(messageCount);

        for (int i = 0; i < messageCount; i++)
            messages.add(new Message(buf.readUuid(), buf.readString(), buf.readLong(), buf.readVarInt()));

        return new State(status, other, handset, number, contacts, messages);
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

        this.ticks++;

        if (this.copiedTicks > 0)
            this.copiedTicks--;
        this.clicks.tick();
        this.updateDialButton();
    }

    private int left() {
        return (this.width - WIDTH - (this.keypadOpen ? KEYPAD_GAP + KEYPAD_WIDTH : 0)) / 2;
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

        SpriteButton keypad = this.add(x, y + ROW_HEIGHT * 5, SIDE_WIDTH, Text.translatable(this.keypadOpen
                ? "screen.ait.comms.short.keypad_close" : "screen.ait.comms.short.keypad"), button -> {
                    this.keypadOpen = !this.keypadOpen;
                    this.rebuild();
                });
        keypad.setTooltip(Tooltip.of(Text.translatable("screen.ait.comms.keypad")));

        this.dialButton = null;

        if (this.keypadOpen)
            this.addKeypad(left + WIDTH + KEYPAD_GAP, top + (HEIGHT - KEYPAD_HEIGHT) / 2, state);

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

    private void addKeypad(int x, int y, State state) {
        int keysLeft = x + (KEYPAD_WIDTH - (PhoneKeyButton.PITCH * 2 + PhoneKeyButton.SIZE)) / 2;
        int keysTop = y + KEYPAD_KEYS_Y;

        for (int i = 0; i < KEYS.length; i++) {
            char key = KEYS[i];
            PhoneKeyButton button = this.addDrawableChild(new PhoneKeyButton(KEYPAD_TEXTURE,
                    keysLeft + (i % 3) * PhoneKeyButton.PITCH, keysTop + (i / 3) * PhoneKeyButton.PITCH,
                    key == DIAL ? Text.translatable("screen.ait.phone.dial") : Text.literal(String.valueOf(key)),
                    key == DIAL ? PhoneKeyButton.PHONE_ICON : null, pressed -> this.press(key)));

            if (key == BACKSPACE)
                button.setTooltip(Tooltip.of(Text.translatable("screen.ait.comms.backspace")));

            if (key == DIAL) {
                button.setTooltip(Tooltip.of(Text.translatable("screen.ait.phone.dial")));
                this.dialButton = button;
            }
        }

        this.updateDialButton();
    }

    private void updateDialButton() {
        State state = latest;

        if (this.dialButton != null)
            this.dialButton.active = state != null && state.status() == TardisComms.Status.IDLE
                    && !this.digits.isEmpty() && !this.clicks.turning();
    }

    private void press(char key) {
        switch (key) {
            case BACKSPACE -> {
                if (Screen.hasShiftDown())
                    this.digits.setLength(0);
                else if (!this.digits.isEmpty())
                    this.digits.setLength(this.digits.length() - 1);
            }
            case DIAL -> this.dial();
            default -> this.type(key);
        }

        this.updateDialButton();
    }

    private void type(char digit) {
        if (this.digits.length() >= PhoneDirectory.DIGITS)
            return;

        this.digits.append(digit);
        this.clicks.dial(digit);
    }

    private void dial() {
        if (this.tardis() == null || this.dialButton == null || !this.dialButton.active)
            return;

        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeUuid(this.tardis().getUuid());
        buf.writeString(this.digits.toString());
        ClientPlayNetworking.send(TardisComms.DIAL, buf);

        this.digits.setLength(0);
        this.updateDialButton();
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        if (this.keypadOpen && Character.isDigit(chr)) {
            this.type(chr);
            this.updateDialButton();
            return true;
        }

        return super.charTyped(chr, modifiers);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.keypadOpen && Screen.isPaste(keyCode)) {
            String pasted = PhoneDirectory.normalize(MinecraftClient.getInstance().keyboard.getClipboard());

            if (!pasted.isEmpty()) {
                this.digits.setLength(0);
                this.digits.append(pasted);
                RotaryClicks.click(1.2f);
                this.updateDialButton();
            }

            return true;
        }

        if (this.keypadOpen && keyCode == GLFW.GLFW_KEY_BACKSPACE) {
            this.press(BACKSPACE);
            return true;
        }

        if (this.keypadOpen && (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER)) {
            this.press(DIAL);
            return true;
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private boolean overNumber(double mouseX, double mouseY) {
        State state = latest;

        if (state == null || state.number().isEmpty())
            return false;

        int width = this.textRenderer.getWidth(Text.translatable("screen.ait.comms.own_number", state.number()));
        int right = this.left() + HEADER_X + 3 + HEADER_WIDTH - 6;
        int y = this.top() + HEADER_Y + 2;

        return mouseX >= right - width && mouseX < right && mouseY >= y - 1 && mouseY < y + this.textRenderer.fontHeight + 1;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        State state = latest;

        if (button == 0 && state != null && this.overNumber(mouseX, mouseY)) {
            MinecraftClient.getInstance().keyboard.setClipboard(state.number());
            RotaryClicks.click(1.2f);
            this.copiedTicks = 40;
            return true;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    private void renderKeypad(DrawContext context, int x, int y) {
        int fieldX = x + (KEYPAD_WIDTH - PhoneKeyButton.FIELD_WIDTH) / 2;
        int fieldY = y + KEYPAD_FIELD_Y;
        int textY = fieldY + (PhoneKeyButton.FIELD_HEIGHT - this.textRenderer.fontHeight) / 2 + 1;

        context.drawTexture(KEYPAD_TEXTURE, x, y, 0, 0, KEYPAD_WIDTH, KEYPAD_HEIGHT, 256, 256);
        context.drawTexture(KEYPAD_TEXTURE, fieldX, fieldY, PhoneKeyButton.FIELD_U, PhoneKeyButton.FIELD_V,
                PhoneKeyButton.FIELD_WIDTH, PhoneKeyButton.FIELD_HEIGHT, 256, 256);

        String shown = PhoneDirectory.format(this.digits.toString());

        if (shown.isEmpty()) {
            this.screenLine(context, Text.translatable("screen.ait.phone.enter_number"), fieldX + PhoneKeyButton.FIELD_PADDING, textY,
                    PhoneKeyButton.FIELD_WIDTH - PhoneKeyButton.FIELD_PADDING * 2, KEYPAD_HINT);
        } else {
            String cursor = this.digits.length() < PhoneDirectory.DIGITS && (this.ticks / 10) % 2 == 0 ? "_" : "";
            this.screenLine(context, Text.literal(shown + cursor), fieldX + PhoneKeyButton.FIELD_PADDING, textY,
                    PhoneKeyButton.FIELD_WIDTH - PhoneKeyButton.FIELD_PADDING * 2, KEYPAD_TEXT);
        }
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

    @Override
    public void removed() {
        super.removed();
        ClientPlayNetworking.send(TardisComms.CLOSE, PacketByteBufs.create());
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
        if (this.tardis() != null)
            CommsHandler.sendBroadcastToggle(this.tardis());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);

        int left = this.left();
        int top = this.top();

        context.drawTexture(TEXTURE, left, top, 0, TEXTURE_V, WIDTH, HEIGHT, 256, 256);

        if (this.keypadOpen)
            this.renderKeypad(context, left + WIDTH + KEYPAD_GAP, top + (HEIGHT - KEYPAD_HEIGHT) / 2);

        State state = latest;
        int textX = left + HEADER_X + 3;
        int textWidth = HEADER_WIDTH - 6;

        this.screenLine(context, this.title, textX, top + HEADER_Y + 2, textWidth, SCREEN_TEXT);

        if (state != null && !state.number().isEmpty()) {
            boolean hovered = this.overNumber(mouseX, mouseY);
            Text number = this.copiedTicks > 0 ? Text.translatable("screen.ait.comms.copied")
                    : Text.translatable("screen.ait.comms.own_number", state.number());

            if (hovered)
                number = number.copy().formatted(Formatting.UNDERLINE);

            int numberWidth = this.textRenderer.getWidth(number);

            context.drawText(this.textRenderer, number, textX + textWidth - numberWidth, top + HEADER_Y + 2,
                    hovered || this.copiedTicks > 0 ? SCREEN_TEXT : SCREEN_TEXT_DIM, false);
        }

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
