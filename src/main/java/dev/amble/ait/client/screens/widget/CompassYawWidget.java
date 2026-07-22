package dev.amble.ait.client.screens.widget;

import java.util.function.Consumer;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class CompassYawWidget extends ClickableWidget {

    private static final float STEP = 10f;

    private final Consumer<Float> onChange;
    private float value;
    private float lastClickValue;
    private boolean fresh = true;

    public CompassYawWidget(int x, int y, int size, float initial, Consumer<Float> onChange) {
        super(x, y, size, size, Text.translatable("screen.ait.environment_projector.yaw"));
        this.value = wrap(initial);
        this.lastClickValue = this.value;
        this.onChange = onChange;
    }

    public float getValue() {
        return this.value;
    }

    public void setValue(float v) {
        this.value = wrap(v);
    }

    private static float wrap(float v) {
        float w = v % 360f;
        if (w < 0) w += 360f;
        return w;
    }

    private int centerX() { return this.getX() + this.getWidth() / 2; }
    private int centerY() { return this.getY() + this.getHeight() / 2; }

    private void updateFromMouse(double mouseX, double mouseY) {
        double dx = mouseX - centerX();
        double dy = mouseY - centerY();
        if (dx == 0 && dy == 0) return;
        double angle = Math.toDegrees(Math.atan2(dx, -dy));
        if (angle < 0) angle += 360;
        this.value = (float) angle;
        maybePlayClick();
        this.onChange.accept(this.value);
    }

    private void maybePlayClick() {
        float delta = MathHelper.wrapDegrees(this.value - this.lastClickValue);
        if (this.fresh || Math.abs(delta) >= STEP) {
            playClick();
            this.lastClickValue = this.value;
            this.fresh = false;
        }
    }

    private void playClick() {
        MinecraftClient.getInstance().getSoundManager().play(
                PositionedSoundInstance.master(SoundEvents.BLOCK_LEVER_CLICK, 1.6f));
    }

    @Override
    public void playDownSound(SoundManager soundManager) {
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        this.fresh = true;
        updateFromMouse(mouseX, mouseY);
    }

    @Override
    protected void onDrag(double mouseX, double mouseY, double deltaX, double deltaY) {
        updateFromMouse(mouseX, mouseY);
    }

    @Override
    public void renderButton(DrawContext context, int mouseX, int mouseY, float delta) {
        int cx = centerX();
        int cy = centerY();
        int radius = this.getWidth() / 2 - 4;
        int accent = this.isSelected() ? 0xFFFFFFFF : 0xFFFFCC55;

        double rad = Math.toRadians(this.value);
        double sin = Math.sin(rad);
        double cos = -Math.cos(rad);

        int tipX = cx + (int) Math.round(sin * radius);
        int tipY = cy + (int) Math.round(cos * radius);
        int tailReach = (int) Math.round(radius * 0.4);
        int tailX = cx - (int) Math.round(sin * tailReach);
        int tailY = cy - (int) Math.round(cos * tailReach);

        drawThickLine(context, tailX, tailY, tipX, tipY, accent);

        double headLen = radius * 0.3;
        double wingA = Math.toRadians(this.value + 140);
        double wingB = Math.toRadians(this.value - 140);
        int wax = tipX + (int) Math.round(Math.sin(wingA) * headLen);
        int way = tipY - (int) Math.round(Math.cos(wingA) * headLen);
        int wbx = tipX + (int) Math.round(Math.sin(wingB) * headLen);
        int wby = tipY - (int) Math.round(Math.cos(wingB) * headLen);
        drawThickLine(context, wax, way, tipX, tipY, accent);
        drawThickLine(context, wbx, wby, tipX, tipY, accent);

        context.fill(cx - 2, cy - 2, cx + 3, cy + 3, accent);
    }

    private static void drawThickLine(DrawContext ctx, int x1, int y1, int x2, int y2, int color) {
        drawLine(ctx, x1, y1, x2, y2, color);
        drawLine(ctx, x1 + 1, y1, x2 + 1, y2, color);
        drawLine(ctx, x1, y1 + 1, x2, y2 + 1, color);
    }

    private static void drawLine(DrawContext ctx, int x1, int y1, int x2, int y2, int color) {
        int dx = Math.abs(x2 - x1), sx = x1 < x2 ? 1 : -1;
        int dy = -Math.abs(y2 - y1), sy = y1 < y2 ? 1 : -1;
        int err = dx + dy;
        int x = x1, y = y1;
        while (true) {
            ctx.fill(x, y, x + 1, y + 1, color);
            if (x == x2 && y == y2) break;
            int e2 = 2 * err;
            if (e2 >= dy) { err += dy; x += sx; }
            if (e2 <= dx) { err += dx; y += sy; }
        }
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {
        this.appendDefaultNarrations(builder);
    }
}
