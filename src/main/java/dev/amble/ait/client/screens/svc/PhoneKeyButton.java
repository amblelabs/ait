package dev.amble.ait.client.screens.svc;

import dev.amble.ait.AITMod;
import org.jetbrains.annotations.Nullable;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class PhoneKeyButton extends ButtonWidget {

    public static final Identifier PHONE_ICON = AITMod.id("textures/item/phone.png");
    public static final int SIZE = 20;
    public static final int PITCH = 22;
    public static final int FIELD_U = 28;
    public static final int FIELD_V = 143;
    public static final int FIELD_WIDTH = 84;
    public static final int FIELD_HEIGHT = 24;

    private static final int KEY_U = 1;
    private static final int KEY_V = 146;
    private static final int KEY_PRESSED_V = 166;
    private static final int ICON_SIZE = 16;
    private static final int TEXT = 0x3A3A3A;
    private static final int TEXT_INACTIVE = 0x8A8A8A;

    private final Identifier texture;
    @Nullable private final Identifier icon;

    public PhoneKeyButton(Identifier texture, int x, int y, Text message, @Nullable Identifier icon, PressAction action) {
        super(x, y, SIZE, SIZE, message, action, DEFAULT_NARRATION_SUPPLIER);
        this.texture = texture;
        this.icon = icon;
    }

    @Override
    protected void renderButton(DrawContext context, int mouseX, int mouseY, float delta) {
        int x = this.getX();
        int y = this.getY();
        boolean pressed = this.active && this.isSelected();

        context.drawTexture(this.texture, x, y, KEY_U, pressed ? KEY_PRESSED_V : KEY_V, SIZE, SIZE, 256, 256);

        if (this.icon != null) {
            int inset = (SIZE - ICON_SIZE) / 2;

            context.drawTexture(this.icon, x + inset, y + inset, 0, 0, ICON_SIZE, ICON_SIZE, ICON_SIZE, ICON_SIZE);
            return;
        }

        TextRenderer font = MinecraftClient.getInstance().textRenderer;
        int width = font.getWidth(this.getMessage());

        context.drawText(font, this.getMessage(), x + (SIZE - width + 1) / 2, y + (SIZE - font.fontHeight) / 2 + 1,
                this.active ? TEXT : TEXT_INACTIVE, false);
    }
}
