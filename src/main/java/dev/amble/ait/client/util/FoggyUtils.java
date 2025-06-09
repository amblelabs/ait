package dev.amble.ait.client.util;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.amble.ait.core.AITDimensions;
import dev.amble.ait.core.AITTags;
import dev.amble.ait.core.tardis.Tardis;
import dev.amble.ait.module.planet.core.space.planet.Planet;
import dev.amble.ait.module.planet.core.space.planet.PlanetRegistry;
import dev.amble.ait.module.planet.core.space.system.Space;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.FogShape;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.MathHelper;

public class FoggyUtils {
    public static void overrideFog() {
        MinecraftClient mc = MinecraftClient.getInstance();
        Tardis tardis = ClientTardisUtil.getCurrentTardis();

        if (mc.player != null && !mc.player.isSpectator() && mc.world != null && mc.world.getRegistryKey().equals(AITDimensions.SPACE)) {
            for (Planet planet : Space.getInstance().getPlanets()) {
                //System.out.println(planet + ":" + (planet.render().position().distanceTo(mc.player.getPos()) /*< planet.render().radius()*/) + ":" + planet.render().radius());
                if (planet != PlanetRegistry.getInstance().get(mc.world) && planet.render().position().distanceTo(mc.player.getPos()) < planet.render().radius()) {
                    //System.out.println(planet);
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

            ItemStack stack = mc.player.getEquippedStack(EquipmentSlot.HEAD);

            RenderSystem.setShaderFogColor(0.2f, 0.2f, 0.2f,
                    stack.isIn(AITTags.Items.FULL_RESPIRATORS) ? 0.015f : 0.35f);
        }

        if (ClientTardisUtil.isPlayerInATardis()
                && !tardis.isGrowth()
                && ("partytardis".equalsIgnoreCase(String.valueOf(tardis.stats().getName()))
                || !tardis.extra().getInsertedDisc().isEmpty())) {

            MinecraftClient minecraftClient = MinecraftClient.getInstance();

            int m = 25;
            int n = minecraftClient.player.age / m + minecraftClient.player.getId();
            int o = DyeColor.values().length;
            int p = n % o;
            int q = (n + 1) % o;
            float rProgress = ((float)(minecraftClient.player.age % m)) / m;
            float[] fs = SheepEntity.getRgbColor(DyeColor.byId(p));
            float[] gs = SheepEntity.getRgbColor(DyeColor.byId(q));
            float red = fs[0] * (1f - rProgress) + gs[0] * rProgress;
            float green = fs[1] * (1f - rProgress) + gs[1] * rProgress;
            float blue = fs[2] * (1f - rProgress) + gs[2] * rProgress;

            RenderSystem.setShaderFogStart(MathHelper.lerp(minecraftClient.getTickDelta() / 100f, -8, 24));
            RenderSystem.setShaderFogEnd(MathHelper.lerp(minecraftClient.getTickDelta() / 100f, 20, 40));
            RenderSystem.setShaderFogShape(FogShape.SPHERE);
            RenderSystem.setShaderFogColor(red, green, blue, 0.25f);
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
