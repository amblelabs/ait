package dev.amble.ait.client.screens.widget;

import java.util.function.Function;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class DynamicPressableTextWidget extends ButtonWidget {
    private final TextRenderer textRenderer;
    private final Function<DynamicPressableTextWidget, Text> text;

    private boolean leftClick = true;

    private Text cached;
    private Text hoverText;

    public DynamicPressableTextWidget(int x, int y, int width, int height,
            Function<DynamicPressableTextWidget, Text> text, ButtonWidget.PressAction onPress,
            TextRenderer textRenderer) {
        super(x, y, width, height, Text.empty(), onPress, DEFAULT_NARRATION_SUPPLIER);

        this.textRenderer = textRenderer;
        this.text = text;

        this.refresh();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!this.active || !this.visible)
            return false;

        if (!this.clicked(mouseX, mouseY))
            return false;

        this.leftClick = button == 0;

        this.playDownSound(MinecraftClient.getInstance().getSoundManager());
        this.onClick(mouseX, mouseY);
        return true;
    }

    @Override
    public void renderButton(DrawContext context, int mouseX, int mouseY, float delta) {
        Text text = this.isSelected() ? this.hoverText : this.cached;
        context.drawTextWithShadow(this.textRenderer, text, this.getX(), this.getY(),
                0xFFFFFF | MathHelper.ceil(this.alpha * 255.0f) << 24);
    }

    public boolean isLeftClick() {
        return leftClick;
    }

    public void refresh() {
        this.cached = this.text.apply(this);
        this.hoverText = Texts.setStyleIfAbsent(this.cached.copy(), Style.EMPTY.withUnderline(true));
    }
}
