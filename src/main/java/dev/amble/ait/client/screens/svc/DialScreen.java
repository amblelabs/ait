package dev.amble.ait.client.screens.svc;

import java.util.ArrayList;
import java.util.List;

import dev.amble.ait.core.tardis.comms.PhoneDirectory;
import dev.amble.ait.core.tardis.comms.RotaryPhones;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import org.lwjgl.glfw.GLFW;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

public class DialScreen extends Screen {

    private static final int WIDTH = 200;
    private static final int HEIGHT = 158;

    private static final int PAD = 8;
    private static final int KEY_WIDTH = 34;
    private static final int KEY_HEIGHT = 18;
    private static final int KEY_GAP = 4;
    private static final int PAD_WIDTH = KEY_WIDTH * 3 + KEY_GAP * 2;

    private static final int RECENT_X = PAD + PAD_WIDTH + 8;
    private static final int RECENT_WIDTH = WIDTH - RECENT_X - PAD;
    private static final int RECENT_HEIGHT = 16;

    private static final int FRAME = 0xFF120E0B;
    private static final int BODY = 0xFF2A211B;
    private static final int SCREEN = 0xFF0C150C;
    private static final int SCREEN_TEXT = 0x55FF55;
    private static final int SCREEN_TEXT_DIM = 0x2E8B2E;
    private static final int LABEL = 0xD8C8A8;

    private static final String[] KEYS = { "1", "2", "3", "4", "5", "6", "7", "8", "9", "C", "0", "<" };

    private record Recent(String number, String label, boolean outgoing) {
    }

    private final BlockPos pos;
    private final String ownNumber;
    private final List<Recent> recents;
    private int cooldown;

    private final StringBuilder digits = new StringBuilder();
    private int pulses;
    private int ticks;
    private ButtonWidget dialButton;

    private DialScreen(BlockPos pos, String ownNumber, int cooldown, List<Recent> recents) {
        super(Text.translatable("screen.ait.phone.title"));
        this.pos = pos;
        this.ownNumber = ownNumber;
        this.cooldown = cooldown;
        this.recents = recents;
    }

    public static void registerClient() {
        ClientPlayNetworking.registerGlobalReceiver(RotaryPhones.OPEN, (client, handler, buf, responseSender) -> {
            BlockPos pos = buf.readBlockPos();
            String number = buf.readString();
            int cooldown = buf.readVarInt();
            int count = buf.readVarInt();
            List<Recent> recents = new ArrayList<>(count);

            for (int i = 0; i < count; i++)
                recents.add(new Recent(buf.readString(), buf.readString(), buf.readBoolean()));

            client.execute(() -> client.setScreen(new DialScreen(pos, number, cooldown, recents)));
        });
    }

    private int left() {
        return (this.width - WIDTH) / 2;
    }

    private int top() {
        return (this.height - HEIGHT) / 2;
    }

    @Override
    protected void init() {
        int left = this.left();
        int top = this.top();
        int padTop = top + 46;

        for (int i = 0; i < KEYS.length; i++) {
            String key = KEYS[i];
            int x = left + PAD + (i % 3) * (KEY_WIDTH + KEY_GAP);
            int y = padTop + (i / 3) * (KEY_HEIGHT + KEY_GAP);

            this.addDrawableChild(ButtonWidget.builder(Text.literal(key), button -> this.press(key))
                    .dimensions(x, y, KEY_WIDTH, KEY_HEIGHT).build());
        }

        int dialY = padTop + 4 * (KEY_HEIGHT + KEY_GAP);

        this.dialButton = this.addDrawableChild(ButtonWidget.builder(Text.translatable("screen.ait.phone.dial"), button -> this.dial())
                .dimensions(left + PAD, dialY, PAD_WIDTH, KEY_HEIGHT).build());

        for (int i = 0; i < this.recents.size(); i++) {
            Recent recent = this.recents.get(i);
            Text text = Text.literal((recent.outgoing() ? "→ " : "← ") + PhoneDirectory.format(recent.number()));
            ButtonWidget button = ButtonWidget.builder(text, b -> this.setDigits(recent.number()))
                    .dimensions(left + RECENT_X, top + 34 + i * (RECENT_HEIGHT + 2), RECENT_WIDTH, RECENT_HEIGHT).build();

            Text tooltip = Text.translatable(recent.outgoing() ? "screen.ait.phone.outgoing" : "screen.ait.phone.incoming");

            if (!recent.label().isBlank())
                tooltip = Text.literal(recent.label()).append("\n").append(tooltip);

            button.setTooltip(Tooltip.of(tooltip));
            this.addDrawableChild(button);
        }

        this.updateDialButton();
    }

    @Override
    public void tick() {
        super.tick();
        this.ticks++;

        if (this.cooldown > 0) {
            this.cooldown--;
            this.updateDialButton();
        }

        if (this.pulses > 0) {
            this.pulses--;
            this.click(1.6f + (this.pulses % 2) * 0.1f);
            this.updateDialButton();
        }
    }

    private void updateDialButton() {
        if (this.dialButton == null)
            return;

        boolean ready = this.cooldown <= 0;

        this.dialButton.active = ready && !this.digits.isEmpty() && this.pulses <= 0;
        this.dialButton.setMessage(ready ? Text.translatable("screen.ait.phone.dial")
                : Text.translatable("screen.ait.phone.wait", (this.cooldown + 19) / 20));
    }

    private void press(String key) {
        switch (key) {
            case "C" -> this.digits.setLength(0);
            case "<" -> {
                if (!this.digits.isEmpty())
                    this.digits.setLength(this.digits.length() - 1);
            }
            default -> this.type(key.charAt(0));
        }

        this.updateDialButton();
    }

    private void type(char digit) {
        if (this.digits.length() >= PhoneDirectory.DIGITS)
            return;

        this.digits.append(digit);
        this.pulses += digit == '0' ? 10 : digit - '0';
        this.click(1.2f);
    }

    private void setDigits(String number) {
        this.digits.setLength(0);
        this.digits.append(PhoneDirectory.normalize(number));
        this.click(1.2f);
        this.updateDialButton();
    }

    private void click(float pitch) {
        MinecraftClient.getInstance().getSoundManager().play(
                PositionedSoundInstance.master(SoundEvents.BLOCK_COMPARATOR_CLICK, pitch, 0.35f));
    }

    private void dial() {
        if (this.digits.isEmpty() || this.cooldown > 0)
            return;

        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeBlockPos(this.pos);
        buf.writeString(this.digits.toString());
        ClientPlayNetworking.send(RotaryPhones.DIAL, buf);

        this.close();
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        if (Character.isDigit(chr)) {
            this.type(chr);
            this.updateDialButton();
            return true;
        }

        return super.charTyped(chr, modifiers);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_BACKSPACE) {
            this.press("<");
            return true;
        }

        if ((keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER) && this.dialButton.active) {
            this.dial();
            return true;
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);

        int left = this.left();
        int top = this.top();

        context.fill(left, top, left + WIDTH, top + HEIGHT, FRAME);
        context.fill(left + 2, top + 2, left + WIDTH - 2, top + HEIGHT - 2, BODY);

        context.drawText(this.textRenderer, this.title, left + PAD, top + PAD, LABEL, false);

        Text own = Text.translatable("screen.ait.phone.own_number", PhoneDirectory.format(this.ownNumber));
        context.drawText(this.textRenderer, own, left + WIDTH - PAD - this.textRenderer.getWidth(own), top + PAD, LABEL, false);

        int screenTop = top + 22;
        context.fill(left + PAD, screenTop, left + PAD + PAD_WIDTH, screenTop + 18, SCREEN);

        String shown = PhoneDirectory.format(this.digits.toString());

        if (shown.isEmpty()) {
            context.drawText(this.textRenderer, Text.translatable("screen.ait.phone.enter_number"), left + PAD + 4, screenTop + 5,
                    SCREEN_TEXT_DIM, false);
        } else {
            String cursor = this.digits.length() < PhoneDirectory.DIGITS && (this.ticks / 10) % 2 == 0 ? "_" : "";
            context.drawText(this.textRenderer, shown + cursor, left + PAD + 4, screenTop + 5, SCREEN_TEXT, false);
        }

        context.drawText(this.textRenderer, Text.translatable("screen.ait.phone.recent"), left + RECENT_X, top + 22, LABEL, false);

        if (this.recents.isEmpty())
            context.drawText(this.textRenderer, Text.translatable("screen.ait.phone.no_recent"), left + RECENT_X, top + 36,
                    0x8A7A60, false);

        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
