package dev.amble.ait.client.screens.widget;

import dev.amble.ait.core.devteam.DevTeam;
import dev.amble.ait.mixin.client.title.LogoDrawerAccessor;
import dev.amble.verification.BetaVerification;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.LogoDrawer;

public class AITLogoDrawer extends LogoDrawer {

    private final TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
    private final LogoDrawer parent;

    public AITLogoDrawer(LogoDrawer drawer) {
        super(((LogoDrawerAccessor) drawer).getIgnoreAlpha());
        this.parent = drawer;
    }

    @Override
    public void draw(DrawContext context, int screenWidth, float alpha) {
        this.draw(context, screenWidth, alpha, 30);
    }

    @Override
    public void draw(DrawContext context, int screenWidth, float alpha, int y) {
        context.setShaderColor(1, 1, 1, ((LogoDrawerAccessor) this).getIgnoreAlpha() ? 1 : alpha);

        int x = screenWidth / 2 - 121;
        context.drawTexture(LOGO_TEXTURE, x, y, 0, 0, 242, 42, 242, 42);

        context.setShaderColor(1, 1, 1, 1);

        if (DevTeam.VERIFY.getBuildType() != BetaVerification.BuildType.RELEASE) {
            String warningMessage = "⚠ WARNING! You are using an experimental version (" + DevTeam.VERIFY.getBranch() + "), please be cautious when testing!";

            int textWidth = this.textRenderer.getWidth(warningMessage);

            x = (screenWidth - textWidth) / 2;
            y = 10;

            int padding = 7;

            context.fill(0, y - padding, screenWidth, y + this.textRenderer.fontHeight + padding, 0xAA000000);
            context.drawText(this.textRenderer, warningMessage, x, y, 0xFFFF0000, true);
        }
    }

    public LogoDrawer getParent() {
        return parent;
    }
}
