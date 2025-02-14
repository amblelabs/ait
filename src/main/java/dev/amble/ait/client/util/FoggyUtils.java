package dev.amble.ait.client.util;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.FogShape;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;

import dev.amble.ait.core.AITTags;
import dev.amble.ait.core.tardis.Tardis;

public class FoggyUtils {
    public static void overrideFog() {
        Tardis tardis = ClientTardisUtil.getCurrentTardis();

        if (tardis == null || tardis.getExterior() == null)
            return;

        if (ClientTardisUtil.isPlayerInATardis() && !tardis.isGrowth()
                && ClientTardisUtil.getAlarmDelta() != ClientTardisUtil.MAX_ALARM_DELTA_TICKS) {
            RenderSystem.setShaderFogStart(MathHelper.lerp(ClientTardisUtil.getAlarmDeltaForLerp(), -8, 10));
            RenderSystem.setShaderFogEnd(MathHelper.lerp(ClientTardisUtil.getAlarmDeltaForLerp(), 11, 32));
            RenderSystem.setShaderFogShape(FogShape.SPHERE);
            RenderSystem.setShaderFogColor(0.5f, 0, 0, 0.5f);
            MinecraftClient.getInstance().gameRenderer.getCamera().getSubmersionType();
        }

        if (ClientTardisUtil.isPlayerInATardis() && !tardis.isGrowth()
                && ClientTardisUtil.getPowerDelta() != ClientTardisUtil.MAX_POWER_DELTA_TICKS) {
            RenderSystem.setShaderFogStart(MathHelper.lerp(ClientTardisUtil.getPowerDeltaForLerp(), -8, 24));
            RenderSystem.setShaderFogEnd(MathHelper.lerp(ClientTardisUtil.getPowerDeltaForLerp(), 11, 32));
            RenderSystem.setShaderFogShape(FogShape.SPHERE);
            RenderSystem.setShaderFogColor(0, 0, 0, tardis.siege().isActive() ? 0.85f : 1f);
        }

        if (ClientTardisUtil.isPlayerInATardis() && tardis.crash().isToxic() && tardis.fuel().hasPower()) {
            RenderSystem
                    .setShaderFogStart(MathHelper.lerp(MinecraftClient.getInstance().getTickDelta() / 100f, -8, 24));
            RenderSystem.setShaderFogEnd(MathHelper.lerp(MinecraftClient.getInstance().getTickDelta() / 100f, 11, 32));
            RenderSystem.setShaderFogShape(FogShape.SPHERE);

            ItemStack stack = MinecraftClient.getInstance().player.getEquippedStack(EquipmentSlot.HEAD);

            RenderSystem.setShaderFogColor(0.2f, 0.2f, 0.2f,
                    stack.isIn(AITTags.Items.FULL_RESPIRATORS) ? 0.015f : 0.35f);
        }

        /*
         * if (!AITMod.AIT_CONFIG.DISABLE_LOYALTY_FOG() &&
         * ClientTardisUtil.isPlayerInATardis() && !tardis.crash().isToxic() &&
         * !tardis.alarm().enabled().get() && tardis.fuel().hasPower() ) {
         * RenderSystem.setShaderFogStart(MathHelper.lerp(MinecraftClient.getInstance().
         * getTickDelta() / 100f, -8, 24));
         * RenderSystem.setShaderFogEnd(MathHelper.lerp(MinecraftClient.getInstance().
         * getTickDelta() / 100f, 11, 32));
         * RenderSystem.setShaderFogShape(FogShape.CYLINDER);
         *
         * int loyaltyLevel =
         * tardis.loyalty().get(MinecraftClient.getInstance().player).level();
         * RenderSystem.setShaderFogColor(1 - loyaltyLevel / 100f, 0, loyaltyLevel /
         * 100f, 0.025f); }
         */

        /*
         * if(MinecraftClient.getInstance().world != null
         * &&MinecraftClient.getInstance().world.getRegistryKey() ==
         * AITDimensions.TIME_VORTEX_WORLD) {
         * RenderSystem.setShaderFogStart(MathHelper.lerp(MinecraftClient.getInstance().
         * getTickDelta() / 100f, -8, 24));
         * RenderSystem.setShaderFogEnd(MathHelper.lerp(MinecraftClient.getInstance().
         * getTickDelta() / 100f, 11, 32));
         * RenderSystem.setShaderFogShape(FogShape.SPHERE);
         * RenderSystem.setShaderFogColor(0.3f, 0.3f, 0.3f, 0.35f); }
         */
    }
}
