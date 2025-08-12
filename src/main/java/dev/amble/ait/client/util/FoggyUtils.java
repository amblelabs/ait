package dev.amble.ait.client.util;

import com.mojang.blaze3d.systems.RenderSystem;

import dev.amble.ait.client.AITModClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.FogShape;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.MathHelper;

import dev.amble.ait.core.AITDimensions;
import dev.amble.ait.core.AITTags;
import dev.amble.ait.core.tardis.Tardis;
import dev.amble.ait.module.planet.core.space.planet.Planet;
import dev.amble.ait.module.planet.core.space.planet.PlanetRegistry;
import dev.amble.ait.module.planet.core.space.system.Space;

public class FoggyUtils {
    public static void overrideFog() {
        MinecraftClient mc = MinecraftClient.getInstance();
        Tardis tardis = ClientTardisUtil.getCurrentTardis();

        if (mc.player != null && !mc.player.isSpectator() && mc.world != null && mc.world.getRegistryKey().equals(AITDimensions.SPACE)) {
            // EW WTF IS THAT
            for (Planet planet : Space.getInstance().getPlanets()) {
                if (planet != PlanetRegistry.getInstance().get(mc.world) && planet.render().position().distanceTo(mc.player.getPos()) < planet.render().radius()) {
                    MatrixStack stack = new MatrixStack();
                    stack.push();
                    stack.translate(0, 0, -2);
                    stack.scale(2000, 20000, 1);
                    for (int i = 0; i < 7; i++) {
                        MinecraftClient.getInstance().getItemRenderer().renderItem(new ItemStack(Items.WHITE_STAINED_GLASS_PANE),
                                ModelTransformationMode.GROUND, 0xf, OverlayTexture.DEFAULT_UV, stack, MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers(), mc.world, 0);
                    }
                    stack.pop();
                    RenderSystem
                            .setShaderFogStart(MathHelper.lerp(MinecraftClient.getInstance().getTickDelta() / 100f, 1, 1));
                    RenderSystem.setShaderFogEnd(MathHelper.lerp(MinecraftClient.getInstance().getTickDelta() / 100f, 1, 1));
                    RenderSystem.setShaderFogShape(FogShape.SPHERE);
                    RenderSystem.setShaderFogColor(planet.render().color().x(),
                            planet.render().color().y(),
                            planet.render().color().z(), 1f);
                }
            }
        }

        if (tardis == null)
            return;

        if (!tardis.isGrowth()
                && ClientTardisUtil.getAlarmDelta() != ClientTardisUtil.MAX_ALARM_DELTA_TICKS) {
            RenderSystem.setShaderFogStart(MathHelper.lerp(ClientTardisUtil.getAlarmDeltaForLerp(), -8, 10));
            RenderSystem.setShaderFogEnd(MathHelper.lerp(ClientTardisUtil.getAlarmDeltaForLerp(), 11, 32));
            RenderSystem.setShaderFogShape(FogShape.SPHERE);
            RenderSystem.setShaderFogColor(0.5f, 0, 0, 0.5f);
            MinecraftClient.getInstance().gameRenderer.getCamera().getSubmersionType();
        }

        if (tardis.isGrowth()
                || ClientTardisUtil.getPowerDelta() != ClientTardisUtil.MAX_POWER_DELTA_TICKS) {
            if (!AITModClient.CONFIG.powerOffDarkness) return;
            RenderSystem.setShaderFogStart(MathHelper.lerp(ClientTardisUtil.getPowerDeltaForLerp(), -8, 24));
            RenderSystem.setShaderFogEnd(MathHelper.lerp(ClientTardisUtil.getPowerDeltaForLerp(), 11, 32));
            RenderSystem.setShaderFogShape(FogShape.SPHERE);
            RenderSystem.setShaderFogColor(0, 0, 0, tardis.siege().isActive() ? 0.85f : 1f);
        }
        if (tardis.crash().isToxic() && tardis.fuel().hasPower()) {
            RenderSystem
                    .setShaderFogStart(MathHelper.lerp(MinecraftClient.getInstance().getTickDelta() / 100f, -8, 24));
            RenderSystem.setShaderFogEnd(MathHelper.lerp(MinecraftClient.getInstance().getTickDelta() / 100f, 11, 32));
            RenderSystem.setShaderFogShape(FogShape.SPHERE);

            ItemStack stack = mc.player.getEquippedStack(EquipmentSlot.HEAD);

            RenderSystem.setShaderFogColor(0.2f, 0.2f, 0.2f,
                    stack.isIn(AITTags.Items.FULL_RESPIRATORS) ? 0.015f : 0.35f);
        }

        if (!tardis.isGrowth()
                && ("partytardis".equalsIgnoreCase(String.valueOf(tardis.stats().getName()))
                || !tardis.extra().getInsertedDisc().isEmpty())) {

            final float[] rgb = ClientTardisUtil.getPartyColors();
            MinecraftClient minecraftClient = MinecraftClient.getInstance();

            RenderSystem.setShaderFogStart(MathHelper.lerp(minecraftClient.getTickDelta() / 100f, -8, 24));
            RenderSystem.setShaderFogEnd(MathHelper.lerp(minecraftClient.getTickDelta() / 100f, 20, 40));
            RenderSystem.setShaderFogShape(FogShape.SPHERE);
            RenderSystem.setShaderFogColor(rgb[0], rgb[1], rgb[2], 0.25f);
        }
    }
}
