package dev.amble.ait.client.screens.widget;

import java.util.function.Consumer;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class PitchLadderWidget extends ClickableWidget {

    private static final float MIN = -90f;
    private static final float MAX = 90f;
    private static final float STEP = 5f;

    private final Consumer<Float> onChange;
    private final TextRenderer textRenderer;
    private float value;
    private float lastClickValue;
    private boolean fresh = true;

    public PitchLadderWidget(int x, int y, int width, int height, float initial,
                             TextRenderer textRenderer, Consumer<Float> onChange) {
        super(x, y, width, height, Text.literal("Pitch"));
        this.value = MathHelper.clamp(initial, MIN, MAX);
        this.lastClickValue = this.value;
        this.textRenderer = textRenderer;
        this.onChange = onChange;
    }

    public float getValue() {
        return this.value;
    }

    public void setValue(float v) {
        this.value = MathHelper.clamp(v, MIN, MAX);
    }

    private int valueToY(float v) {
        float t = (MAX - v) / (MAX - MIN);
        return this.getY() + (int) Math.round(t * (this.getHeight() - 1));
    }

    private float yToValue(double y) {
        double t = (y - this.getY()) / (double) (this.getHeight() - 1);
        return (float) MathHelper.clamp(MAX - t * (MAX - MIN), MIN, MAX);
    }

    private void updateFromMouse(double mouseY) {
        this.value = yToValue(mouseY);
        maybePlayClick();
        this.onChange.accept(this.value);
    }

    private void maybePlayClick() {
        float delta = this.value - this.lastClickValue;
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
        updateFromMouse(mouseY);
    }

    @Override
    protected void onDrag(double mouseX, double mouseY, double deltaX, double deltaY) {
        updateFromMouse(mouseY);
    }

    @Override
    public void renderButton(DrawContext context, int mouseX, int mouseY, float delta) {
        int barX = this.getX() + 10;
        int top = this.getY();
        int bottom = this.getY() + this.getHeight() - 1;
        int rangeColor = 0xFFAAAAAA;
        int labelColor = 0xFFCCCCCC;
        int accent = this.isSelected() ? 0xFFFFFFFF : 0xFFFFCC55;
        context.fill(barX, top, barX + 1, bottom + 1, rangeColor);

        for (int v = -90; v <= 90; v += 30) {
            int y = valueToY(v);
            int tickLen = (v == 0 || Math.abs(v) == 90) ? 5 : 3;
            context.fill(barX - tickLen, y, barX, y + 1, rangeColor);
            String label = (v > 0 ? "+" : "") + v;
            context.drawText(textRenderer, label, barX + 4, y - 3, labelColor, false);
        }

        int needleY = valueToY(this.value);
        int needleLeft = barX - 8;
        int needleRight = barX + 3;
        context.fill(needleLeft, needleY - 1, needleRight + 1, needleY + 2, accent);
        String readout = String.format("%+.0f", this.value);
        int readoutX = this.getX() + this.getWidth() - textRenderer.getWidth(readout);
        context.drawText(textRenderer, readout, readoutX, needleY - 3, accent, true);
        String title = "Pitch";
        context.drawText(textRenderer, title,
                this.getX() + (this.getWidth() - textRenderer.getWidth(title)) / 2,
                top - 11, 0xFFFFFFFF, false);
    }
    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {
        this.appendDefaultNarrations(builder);
    }
}
