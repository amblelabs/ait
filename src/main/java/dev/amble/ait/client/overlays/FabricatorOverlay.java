package dev.amble.ait.client.overlays;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import dev.amble.ait.AITMod;
import dev.amble.ait.core.blockentities.FabricatorBlockEntity;
import dev.amble.ait.core.blocks.FabricatorBlock;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.RotationAxis;

public class FabricatorOverlay implements HudRenderCallback {
    @Override
    public void onHudRender(DrawContext drawContext, float v) {
        MinecraftClient mc = MinecraftClient.getInstance();
        MatrixStack stack = drawContext.getMatrices();

        if (mc.player == null || mc.world == null)
            return;

        if (!mc.options.getPerspective().isFirstPerson())
            return;

        if (mc.crosshairTarget == null) return;

        if (mc.crosshairTarget.getType() == HitResult.Type.BLOCK) {
            Block block = mc.player.getWorld().getBlockState(((BlockHitResult) mc.crosshairTarget).getBlockPos())
                    .getBlock();
            if (block instanceof FabricatorBlock) {
                BlockEntity entity = mc.player.getWorld().getBlockEntity(((BlockHitResult) mc.crosshairTarget).getBlockPos());
                if (entity instanceof FabricatorBlockEntity fabricatorBlockEntity) {
                    stack.push();
                    stack.translate(((float) drawContext.getScaledWindowWidth() / 2), ((float) drawContext.getScaledWindowHeight() / 2), -20);
                    stack.scale(0.75f, 0.75f, 0.75f);
                    stack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(((float) mc.player.age / 200.0f) * 360f));
                    stack.translate(-((float) 83 / 2), -((float) 83 / 2), 0);
                    RenderSystem.disableDepthTest();
                    RenderSystem.depthMask(false);
                    drawContext.setShaderColor(1.0F, 1.0F, 1.0F, 0.8f);
                    RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.ONE_MINUS_DST_COLOR,
                            GlStateManager.DstFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SrcFactor.ONE,
                            GlStateManager.DstFactor.ZERO);
                    drawContext.drawTexture(AITMod.id("textures/gui/tardis/monitor/security_menu.png"), 0,
                            0, 0, 0, 138, 83, 83, 256, 256);
                    RenderSystem.setShaderColor(1, 1, 1, 1);
                    RenderSystem.defaultBlendFunc();
                    RenderSystem.depthMask(true);
                    RenderSystem.enableDepthTest();
                    stack.pop();
                    ItemStack fabricatorItemStack = fabricatorBlockEntity.getShowcaseStack();
                    float centerX = (float) drawContext.getScaledWindowWidth() / 2 - 8f;
                    float centerY = (float) drawContext.getScaledWindowHeight() / 2 - 8f;
                    double angleStep = 2 * Math.PI / fabricatorItemStack.getCount();

                    for (int i = 0; i < fabricatorItemStack.getCount(); i++) {
                        double angle = i * angleStep - Math.PI / 2;
                        int x = (int) (centerX + Math.cos(angle) * 32);
                        int y = (int) (centerY + Math.sin(angle) * 32);

                        stack.push();
                        stack.translate(x, y, -10);
                        RenderSystem.setShaderColor(0, 0, 0, 0.5f);
                        stack.push();
                        stack.translate(0, 0, -12);
                        drawContext.drawItem(fabricatorItemStack, 1, 1);
                        stack.pop();
                        RenderSystem.setShaderColor(1, 1, 1, 1);
                        drawContext.drawItem(fabricatorItemStack, 0, 0);
                        stack.pop();
                    }
                }
            }
        }
    }
}
