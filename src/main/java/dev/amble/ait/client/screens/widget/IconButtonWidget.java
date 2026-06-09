package dev.amble.ait.client.screens.widget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.PressableWidget;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class IconButtonWidget extends PressableWidget {

    public enum Icon {
        PLAY, STOP
    }

    private final Icon icon;
    private final Runnable onPress;

    public IconButtonWidget(int x, int y, int size, Icon icon, Runnable onPress) {
        super(x, y, size, size, Text.empty());
        this.icon = icon;
        this.onPress = onPress;
    }

    @Override
    public void onPress() {
        if (this.active && this.onPress != null)
            this.onPress.run();
    }

    @Override
    public void renderButton(DrawContext context, int mouseX, int mouseY, float delta) {
        if (!this.visible)
            return;

        int x = this.getX();
        int y = this.getY();
        int w = this.getWidth();
        int h = this.getHeight();

        boolean enabled = this.active;
        int foreground = enabled ? (this.isHovered() ? 0xFFFFFFFF : 0xFFCCCCCC) : 0xFF666666;

        if (this.icon == Icon.STOP) {
            context.fill(x, y, x + w, y + h, foreground);
            return;
        }

        float mid = h / 2f;

        for (int i = 0; i < h; i++) {
            int rowWidth = Math.round((1f - Math.abs(i + 0.5f - mid) / mid) * w);
            context.fill(x, y + i, x + rowWidth, y + i + 1, foreground);
        }
    }

    @Override
    public void playDownSound(SoundManager soundManager) {
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {
        this.appendDefaultNarrations(builder);
    }
}
