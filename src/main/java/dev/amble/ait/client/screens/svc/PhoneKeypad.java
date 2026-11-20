package dev.amble.ait.client.screens.svc;

import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

import dev.amble.ait.core.tardis.comms.PhoneDirectory;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Language;

public final class PhoneKeypad {

    private static final char BACKSPACE = '\b';
    private static final char DIAL = '\n';
    private static final char[] KEYS = { '1', '2', '3', '4', '5', '6', '7', '8', '9', BACKSPACE, '0', DIAL };

    public static final int KEYS_WIDTH = PhoneKeyButton.PITCH * 2 + PhoneKeyButton.SIZE;

    private final StringBuilder digits = new StringBuilder();
    private final RotaryClicks clicks = new RotaryClicks();
    private final BooleanSupplier canDial;
    private final Consumer<String> onDial;

    @Nullable private PhoneKeyButton dialButton;
    private int ticks;

    public PhoneKeypad(BooleanSupplier canDial, Consumer<String> onDial) {
        this.canDial = canDial;
        this.onDial = onDial;
    }

    public void addKeys(Identifier texture, int x, int y, UnaryOperator<PhoneKeyButton> adder) {
        this.dialButton = null;

        for (int i = 0; i < KEYS.length; i++) {
            char key = KEYS[i];
            PhoneKeyButton button = adder.apply(new PhoneKeyButton(texture, x + (i % 3) * PhoneKeyButton.PITCH,
                    y + (i / 3) * PhoneKeyButton.PITCH, label(key), key == DIAL ? PhoneKeyButton.PHONE_ICON : null,
                    pressed -> this.press(key)));

            if (key == BACKSPACE)
                button.setTooltip(Tooltip.of(Text.translatable("screen.ait.comms.backspace")));

            if (key == DIAL) {
                button.setTooltip(Tooltip.of(Text.translatable("screen.ait.phone.dial")));
                this.dialButton = button;
            }
        }

        this.update();
    }

    public void clearKeys() {
        this.dialButton = null;
    }

    private static Text label(char key) {
        return switch (key) {
            case BACKSPACE -> Text.literal("<");
            case DIAL -> Text.translatable("screen.ait.phone.dial");
            default -> Text.literal(String.valueOf(key));
        };
    }

    public void tick() {
        this.ticks++;
        this.clicks.tick();
        this.update();
    }

    public void update() {
        if (this.dialButton != null)
            this.dialButton.active = this.canDial.getAsBoolean() && !this.digits.isEmpty() && !this.clicks.turning();
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

        this.update();
    }

    private void type(char digit) {
        if (this.digits.length() >= PhoneDirectory.DIGITS)
            return;

        this.digits.append(digit);
        this.clicks.dial(digit);
    }

    private void dial() {
        if (this.dialButton == null || !this.dialButton.active)
            return;

        this.onDial.accept(this.digits.toString());
    }

    public void setDigits(String number) {
        this.digits.setLength(0);
        this.digits.append(PhoneDirectory.normalize(number));
        RotaryClicks.click(1.2f);
        this.update();
    }

    public void clear() {
        this.digits.setLength(0);
        this.update();
    }

    public boolean charTyped(char chr) {
        if (!Character.isDigit(chr))
            return false;

        this.type(chr);
        this.update();
        return true;
    }

    public boolean keyPressed(int keyCode) {
        if (Screen.isPaste(keyCode)) {
            String pasted = PhoneDirectory.normalize(MinecraftClient.getInstance().keyboard.getClipboard());

            if (!pasted.isEmpty())
                this.setDigits(pasted);

            return true;
        }

        if (keyCode == GLFW.GLFW_KEY_BACKSPACE) {
            this.press(BACKSPACE);
            return true;
        }

        if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER) {
            this.press(DIAL);
            return true;
        }

        return false;
    }

    public void renderField(DrawContext context, TextRenderer font, Identifier texture, int x, int y, boolean hint,
                            int textColor, int hintColor) {
        context.drawTexture(texture, x, y, PhoneKeyButton.FIELD_U, PhoneKeyButton.FIELD_V, PhoneKeyButton.FIELD_WIDTH,
                PhoneKeyButton.FIELD_HEIGHT, 256, 256);

        int textX = x + PhoneKeyButton.FIELD_PADDING;
        int textY = textY(font, y);
        int width = PhoneKeyButton.FIELD_WIDTH - PhoneKeyButton.FIELD_PADDING * 2;
        String shown = PhoneDirectory.format(this.digits.toString());

        if (shown.isEmpty()) {
            if (hint)
                context.drawText(font, Language.getInstance().reorder(font.trimToWidth(
                        Text.translatable("screen.ait.phone.enter_number"), width)), textX, textY, hintColor, false);

            return;
        }

        String cursor = this.digits.length() < PhoneDirectory.DIGITS && (this.ticks / 10) % 2 == 0 ? "_" : "";
        context.drawText(font, Language.getInstance().reorder(font.trimToWidth(Text.literal(shown + cursor), width)),
                textX, textY, textColor, false);
    }

    public static int textY(TextRenderer font, int fieldY) {
        return fieldY + (PhoneKeyButton.FIELD_HEIGHT - font.fontHeight) / 2 + 1;
    }
}
