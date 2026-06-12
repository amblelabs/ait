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
        PLAY, STOP, SOUND_ON, SOUND_OFF
    }

    private Icon icon;
    private final Runnable onPress;

    public IconButtonWidget(int x, int y, int size, Icon icon, Runnable onPress) {
        super(x, y, size, size, Text.empty());
        this.icon = icon;
        this.onPress = onPress;
    }

    public void setIcon(Icon icon) {
        this.icon = icon;
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

        switch (this.icon) {
            case STOP -> context.fill(x, y, x + w, y + h, foreground);
            case PLAY -> {
                float mid = h / 2f;

                for (int i = 0; i < h; i++) {
                    int rowWidth = Math.round((1f - Math.abs(i + 0.5f - mid) / mid) * w);
                    context.fill(x, y + i, x + rowWidth, y + i + 1, foreground);
                }
            }
            case SOUND_ON, SOUND_OFF -> this.renderSpeaker(context, x, y, w, h, foreground, this.icon == Icon.SOUND_OFF);
        }
    }

    private void renderSpeaker(DrawContext context, int x, int y, int w, int h, int foreground, boolean muted) {
        int centerY = y + h / 2;
        int bodyHalf = Math.max(1, h / 6);
        int coneStart = x + Math.max(1, w / 3);

        context.fill(x, centerY - bodyHalf, coneStart, centerY + bodyHalf + 1, foreground);

        for (int cx = coneStart; cx < x + w; cx++) {
            int half = Math.round(((cx - coneStart) / (float) (x + w - coneStart)) * (h / 2f));
            context.fill(cx, centerY - half, cx + 1, centerY + half + 1, foreground);
        }

        if (muted) {
            int slash = 0xFFFF5555;

            for (int i = 0; i < Math.min(w, h); i++)
                context.fill(x + w - 1 - i, y + i, x + w - i, y + i + 1, slash);
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
