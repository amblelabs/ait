package dev.amble.ait.client.overlays;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;

import net.minecraft.block.AirBlock;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;

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

        if ((mc.player.getEquippedStack(EquipmentSlot.MAINHAND).getItem() == AITItems.SONIC_SCREWDRIVER
                || mc.player.getEquippedStack(EquipmentSlot.OFFHAND).getItem() == AITItems.SONIC_SCREWDRIVER)
                && playerIsLookingAtSonicInteractable(mc.crosshairTarget, mc.player)) {
            this.renderOverlay(drawContext,
                    AITMod.id("textures/gui/overlay/sonic_can_interact.png"));
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
