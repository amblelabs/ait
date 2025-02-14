package dev.amble.ait.module.gun.client;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import dev.amble.ait.AITMod;
import dev.amble.ait.module.gun.core.item.StaserRifleItem;

public class ScopeOverlay implements HudRenderCallback {

    private static final Identifier SPYGLASS_SCOPE = AITMod.id("textures/gui/overlay/scope.png");
    private int scaledWidth, scaledHeight;
    private float spyglassScale;

    @Override
    public void onHudRender(DrawContext drawContext, float v) {
        this.scaledWidth = drawContext.getScaledWindowWidth();
        this.scaledHeight = drawContext.getScaledWindowHeight();

        MinecraftClient mc = MinecraftClient.getInstance();

        if(mc.player == null) return;

        if(mc.player.getMainHandStack().getItem() instanceof StaserRifleItem && mc.options.getPerspective().isFirstPerson()) {
            if (mc.options.useKey.isPressed()) {
                float f = MinecraftClient.getInstance().getLastFrameDuration();
                this.spyglassScale = MathHelper.lerp(0.5f * f, this.spyglassScale, 1.125f);
                this.renderSpyglassOverlay(drawContext, this.spyglassScale);
            }
        }

        if (mc.player == null)
            return;
    }

    private void renderSpyglassOverlay(DrawContext context, float scale) {
        float f;
        float g = f = (float)Math.min(this.scaledWidth, this.scaledHeight);
        float h = Math.min((float)this.scaledWidth / f, (float)this.scaledHeight / g) * scale;
        int i = MathHelper.floor(f * h);
        int j = MathHelper.floor(g * h);
        int k = (this.scaledWidth - i) / 2;
        int l = (this.scaledHeight - j) / 2;
        int m = k + i;
        int n = l + j;
        context.drawTexture(SPYGLASS_SCOPE, k, l, -100, 0.0f, 0.0f, i, j, i, j);
        context.fill(RenderLayer.getGuiOverlay(), 0, n, this.scaledWidth, this.scaledHeight, -90, -16777216);
        context.fill(RenderLayer.getGuiOverlay(), 0, 0, this.scaledWidth, l, -90, -16777216);
        context.fill(RenderLayer.getGuiOverlay(), 0, l, k, n, -90, -16777216);
        context.fill(RenderLayer.getGuiOverlay(), m, l, this.scaledWidth, n, -90, -16777216);
    }
}
