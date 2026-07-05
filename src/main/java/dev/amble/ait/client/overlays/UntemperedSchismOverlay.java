package dev.amble.ait.client.overlays;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.amble.ait.AITMod;
import dev.amble.ait.core.blockentities.ExteriorBlockEntity;
import dev.amble.ait.core.blockentities.UntemperedSchismBlockEntity;
import dev.amble.ait.core.blocks.ExteriorBlock;
import dev.amble.ait.core.blocks.UntemperedSchismBlock;
import dev.amble.ait.core.tardis.Tardis;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.AxeItem;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;

import java.awt.*;

public class UntemperedSchismOverlay implements HudRenderCallback {
    @Override
    public void onHudRender(DrawContext drawContext, float delta) {
        MinecraftClient mc = MinecraftClient.getInstance();
        MatrixStack stack = drawContext.getMatrices();

        if (mc.player == null || mc.world == null)
            return;

        if (!mc.options.getPerspective().isFirstPerson())
            return;

        if (mc.crosshairTarget == null || mc.crosshairTarget.getType() != HitResult.Type.BLOCK)
            return;

        Block block = mc.player.getWorld()
                .getBlockState(((BlockHitResult) mc.crosshairTarget).getBlockPos())
                .getBlock();
        if (!(block instanceof UntemperedSchismBlock)) return;
        UntemperedSchismBlockEntity schism = (UntemperedSchismBlockEntity) mc.player.getWorld().getBlockEntity(((BlockHitResult) mc.crosshairTarget).getBlockPos());

        if (schism == null)
            return;

        String str = "AU: " + (int) schism.getCurrentFuel() + "/" + (int) schism.getMaxFuel();

        stack.push();
        stack.translate((float) drawContext.getScaledWindowWidth() / 2 - (mc.textRenderer.getWidth(str)/2),
                (float) drawContext.getScaledWindowHeight() / 2 - 12,
                -10);

        drawContext.drawText(mc.textRenderer,  str, 0, 0, Color.WHITE.getRGB(), false);

        RenderSystem.defaultBlendFunc();
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
        stack.pop();
    }
}