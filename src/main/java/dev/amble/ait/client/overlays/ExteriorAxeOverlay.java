package dev.amble.ait.client.overlays;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.amble.ait.AITMod;
import dev.amble.ait.core.blockentities.ExteriorBlockEntity;
import dev.amble.ait.core.blocks.ExteriorBlock;
import dev.amble.ait.core.tardis.Tardis;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.AxeItem;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;

public class ExteriorAxeOverlay implements HudRenderCallback {
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
        if (!(block instanceof ExteriorBlock)) return;
        ExteriorBlockEntity exterior = (ExteriorBlockEntity) mc.player.getWorld().getBlockEntity(((BlockHitResult) mc.crosshairTarget).getBlockPos());

        if (exterior == null || !exterior.isLinked())
            return;

        Tardis tardis = exterior.tardis().get();

        if (tardis == null)
            return;

        if (!tardis.siege().isActive() && !tardis.isGrowth()
                && !tardis.fuel().hasPower() && tardis.door().locked()
                && !(mc.player.getMainHandStack().getItem() instanceof AxeItem)) {
            stack.push();
            stack.translate((float) drawContext.getScaledWindowWidth() / 2 - 8f,
                    (float) drawContext.getScaledWindowHeight() / 2 - 8f,
                    -10);
            drawContext.drawTexture(AITMod.id("textures/gui/overlay/axe_door.png"), 2, -4, 0, 0, 16, 16, 16, 16);
            RenderSystem.defaultBlendFunc();
            RenderSystem.depthMask(true);
            RenderSystem.enableDepthTest();
            stack.pop();
        }
    }
}