package dev.amble.ait.client.overlays;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;

import net.minecraft.block.AirBlock;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.RotationAxis;

import dev.amble.ait.AITMod;
import dev.amble.ait.core.AITItems;
import dev.amble.ait.core.AITTags;
import dev.amble.ait.core.item.SonicItem;

public class SonicOverlay implements HudRenderCallback {
    @Override
    public void onHudRender(DrawContext drawContext, float v) {
        MinecraftClient mc = MinecraftClient.getInstance();

        if (mc.player == null || mc.world == null)
            return;

        if (!mc.options.getPerspective().isFirstPerson())
            return;

        boolean isSonicInAnyHand = mc.player.getEquippedStack(EquipmentSlot.MAINHAND).getItem() == AITItems.SONIC_SCREWDRIVER ||
                mc.player.getEquippedStack(EquipmentSlot.OFFHAND).getItem() == AITItems.SONIC_SCREWDRIVER;

        if (!isSonicInAnyHand) {
            return;
        }

        if (playerIsLookingAtSonicInteractable(mc.crosshairTarget, mc.player)) {
            this.renderOverlay(drawContext,
                    AITMod.id("textures/gui/overlay/sonic_can_interact.png"));
        }

        // For drawing the elements on the side of the screen
        int centerX = /*drawContext.getScaledWindowWidth() / 2 */- 25;
        int centerY = drawContext.getScaledWindowHeight() / 2 - 25;
        int radius = 60; // Distance from center to each element
        // Use system time to animate rotation
        long time = System.currentTimeMillis();
        double rotation = (time % 4000) / 4000.0 * 2 * Math.PI; // 4 seconds per full rotation
        double oscillation = Math.cos(time / 250.0); // Fast oscillation
        double rotationTwo = Math.toRadians(oscillation * 90);

        MatrixStack stack = drawContext.getMatrices();

        stack.push();
        stack.translate(50f, 140, 100);
        stack.multiply(RotationAxis.NEGATIVE_Z.rotationDegrees(180f));
        stack.multiply(RotationAxis.NEGATIVE_Z.rotation((float) rotationTwo));
        drawContext.drawVerticalLine(0, 45, 0, 0xffffffff);
        stack.pop();

        stack.push();
        stack.translate(50, 0, 0);
        stack.translate((rotationTwo * 20), 390, 100);
        stack.multiply(RotationAxis.NEGATIVE_Z.rotationDegrees(180f));
        drawContext.drawVerticalLine(0, 45, 0 ,0xffffffff);
        stack.pop();

        stack.push();
        stack.translate(0, 0, 100f);
        drawContext.drawHorizontalLine(5, 95, 350, 0xffffffff);
        stack.pop();

        drawContext.fill(2, 92, 102, 142,
                ColorHelper.Argb.getArgb(255 / 2, 255 / 7, 255 / 7, 255 / 7));
        drawContext.fill(0, 90, 100, 140,
                ColorHelper.Argb.getArgb(255 / 2, 255 / 2, 255 / 2, 255 / 2));

        drawContext.fill(2, 342, 102, 392,
                ColorHelper.Argb.getArgb(255 / 2, 255 / 7, 255 / 7, 255 / 7));
        drawContext.fill(0, 340, 100, 390,
                ColorHelper.Argb.getArgb(255 / 2, 255 / 2, 255 / 2, 255 / 2));

        for (int i = 0; i < 6; i++) {
            double angle = Math.toRadians(60 * i - 60) + rotation; // Start at top, space 60 degrees apart, add rotation
            int x = centerX + (int) (radius * Math.cos(angle));
            int y = centerY + (int) (radius * Math.sin(angle));
            drawContext.fill(2 + x, 2 + y, 47 + x, 47 + y,
                    ColorHelper.Argb.getArgb(255 / 2, 255 / 7, 255 / 7, 255 / 7));
            drawContext.fill(0 + x, 0 + y, 45 + x, 45 + y,
                    ColorHelper.Argb.getArgb(255 / 2, 255 / 4, 255 / 4, 255 / 4));
        }
    }

    private boolean playerIsLookingAtSonicInteractable(HitResult crosshairTarget, PlayerEntity player) {
        if (player != null) {
            if (player.getMainHandStack().getItem() instanceof SonicItem) {
                ItemStack sonic = player.getMainHandStack();
                if (sonic == null)
                    return false;
                NbtCompound nbt = sonic.getOrCreateNbt();
                if (!nbt.contains(SonicItem.FUEL_KEY))
                    return false;
                if (crosshairTarget.getType() == HitResult.Type.BLOCK) {
                    Block block = player.getWorld().getBlockState(((BlockHitResult) crosshairTarget).getBlockPos())
                            .getBlock();
                    return !(block instanceof AirBlock) && nbt.getDouble(SonicItem.FUEL_KEY) > 0
                            && player.getWorld().getBlockState(((BlockHitResult) crosshairTarget).getBlockPos())
                            .isIn(AITTags.Blocks.SONIC_INTERACTABLE);
                }
            } else if (player.getOffHandStack().getItem() instanceof SonicItem) {
                ItemStack sonic = player.getOffHandStack();
                if (sonic == null)
                    return false;
                NbtCompound nbt = sonic.getOrCreateNbt();
                if (!nbt.contains(SonicItem.FUEL_KEY))
                    return false;
                if (crosshairTarget.getType() == HitResult.Type.BLOCK) {
                    Block block = player.getWorld().getBlockState(((BlockHitResult) crosshairTarget).getBlockPos())
                            .getBlock();
                    return !(block instanceof AirBlock) && nbt.getDouble(SonicItem.FUEL_KEY) > 0
                            && player.getWorld().getBlockState(((BlockHitResult) crosshairTarget).getBlockPos())
                            .isIn(AITTags.Blocks.SONIC_INTERACTABLE);
                }
            }
        }
        return false;
    }

    private void renderOverlay(DrawContext context, Identifier texture) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        context.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        context.drawTexture(texture, (context.getScaledWindowWidth() / 2) - 8,
                (context.getScaledWindowHeight() / 2) - 24, 0, 0.0F, 0.0F, 16, 16, 16, 16);

        RenderSystem.defaultBlendFunc();
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
        context.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }

}
