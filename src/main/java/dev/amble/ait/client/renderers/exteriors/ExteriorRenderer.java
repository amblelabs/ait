package dev.amble.ait.client.renderers.exteriors;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.amble.lib.data.CachedDirectedGlobalPos;

import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.RotationPropertyHelper;
import net.minecraft.util.profiler.Profiler;

import dev.amble.ait.AITMod;
import dev.amble.ait.api.tardis.TardisComponent;
import dev.amble.ait.client.boti.BOTI;
import dev.amble.ait.client.models.exteriors.ExteriorModel;
import dev.amble.ait.client.models.exteriors.SiegeModeModel;
import dev.amble.ait.client.models.machines.ShieldsModel;
import dev.amble.ait.client.renderers.AITRenderLayers;
import dev.amble.ait.client.tardis.ClientTardis;
import dev.amble.ait.client.util.ClientLightUtil;
import dev.amble.ait.core.blockentities.ExteriorBlockEntity;
import dev.amble.ait.core.blocks.ExteriorBlock;
import dev.amble.ait.core.tardis.Tardis;
import dev.amble.ait.core.tardis.handler.BiomeHandler;
import dev.amble.ait.core.tardis.handler.SiegeHandler;
import dev.amble.ait.core.tardis.handler.travel.TravelHandler;
import dev.amble.ait.data.datapack.DatapackConsole;
import dev.amble.ait.data.schema.exterior.ClientExteriorVariantSchema;
import dev.amble.ait.registry.impl.exterior.ClientExteriorVariantRegistry;

public class ExteriorRenderer<T extends ExteriorBlockEntity> implements BlockEntityRenderer<T> {

    private static final Identifier SHIELDS = AITMod.id("textures/environment/shields.png");

    private static final SiegeModeModel SIEGE_MODEL = new SiegeModeModel(
            SiegeModeModel.getTexturedModelData().createModel());
    private static final ShieldsModel SHIELDS_MODEL = new ShieldsModel(
            ShieldsModel.getTexturedModelData().createModel());

    private ClientExteriorVariantSchema variant;
    private ExteriorModel model;

    public ExteriorRenderer(BlockEntityRendererFactory.Context ctx) {
    }

    @Override
    public void render(T entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers,
            int light, int overlay) {
        Profiler profiler = entity.getWorld().getProfiler();
        profiler.push("exterior");

        profiler.push("find_tardis");

        if (!entity.isLinked())
            return;

        ClientTardis tardis = entity.tardis().get().asClient();

        profiler.swap("render");

        if (entity.getAlpha() > 0)
            this.renderExterior(profiler, tardis, entity, tickDelta, matrices, vertexConsumers, light, overlay);

        profiler.pop();

        profiler.pop();
    }

    private void renderExterior(Profiler profiler, ClientTardis tardis, T entity, float tickDelta, MatrixStack matrices,
                                VertexConsumerProvider vertexConsumers, int light, int overlay) {
        final float alpha = entity.getAlpha();
        RenderSystem.enableCull();
        RenderSystem.enableBlend();
        RenderSystem.enableDepthTest();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableBlend();
        RenderSystem.enableCull();

        SiegeHandler siege = tardis.siege();

        if (siege.isActive()) {
            profiler.push("siege");

            matrices.push();
            matrices.translate(0.5f, 0.5f, 0.5f);
            SIEGE_MODEL.renderWithAnimations(tardis, entity, SIEGE_MODEL.getPart(),
                    matrices,
                    vertexConsumers.getBuffer(AITRenderLayers.getEntityTranslucentCull(siege.texture().get())), light, overlay, 1, 1, 1, 1);

            matrices.pop();
            profiler.pop();
            return;
        }

        TravelHandler travel = tardis.travel();

        CachedDirectedGlobalPos exteriorPos = travel.position();

        if (exteriorPos == null) {
            profiler.pop();
            return;
        }

        this.updateModel(tardis);

        BlockState blockState = entity.getCachedState();
        int k = blockState.get(ExteriorBlock.ROTATION);
        float h = RotationPropertyHelper.toDegrees(k);

        matrices.push();
        matrices.translate(0.5f, 0.0f, 0.5f);

        Identifier texture = this.variant.texture();
        Identifier emission = this.variant.emission();

        if (MinecraftClient.getInstance().player == null) {
            profiler.pop();
            return;
        }

        float wrappedDegrees = MathHelper.wrapDegrees(MinecraftClient.getInstance().player.getHeadYaw() + h);

        if (this.variant.equals(ClientExteriorVariantRegistry.DOOM)) {
            texture = DoomConstants.getTextureForRotation(wrappedDegrees, tardis);
            emission = DoomConstants.getEmissionForRotation(DoomConstants.getTextureForRotation(wrappedDegrees, tardis),
                    tardis);
        }

        matrices.multiply(
                RotationAxis.NEGATIVE_Y.rotationDegrees(!this.variant.equals(ClientExteriorVariantRegistry.DOOM)
                        ? h + 180f
                        : MinecraftClient.getInstance().player.getHeadYaw() + 180f
                                + ((wrappedDegrees > -135 && wrappedDegrees < 135) ? 180f : 0f)));

        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180f));

        if (model == null) {
            profiler.pop();
            return;
        }

        this.applyNameTransforms(tardis, matrices, tardis.stats().getName());

        if (travel.antigravs().get() && tardis.flight().falling().get()) {
            float sinFunc = (float) Math.sin((MinecraftClient.getInstance().player.age / 400f * 220f) * 0.2f + 0.2f);
            matrices.translate(0, sinFunc, 0);
        }

        if (tardis.selfDestruct().isQueued())
            matrices.scale(0.7f, 0.7f, 0.7f);

        model.renderWithAnimations(tardis, entity, this.model.getPart(),
                matrices, vertexConsumers.getBuffer(AITRenderLayers.getEntityTranslucentCull(texture)), light, overlay, 1, 1,
                1, alpha);

        //System.out.println( variant.hasTransparentDoors());

        if ((tardis.door().getLeftRot() > 0 || variant.hasTransparentDoors()) && !tardis.isGrowth() && travel.isLanded())
            BOTI.EXTERIOR_RENDER_QUEUE.add(entity);
            //this.renderExteriorBoti(entity, variant, matrices, texture, model, BotiPortalModel.getTexturedModelData().createModel(), light);

        /*if (tardis.<OvergrownHandler>handler(TardisComponent.Id.OVERGROWN).overgrown().get()) {
            model.renderWithAnimations(entity, this.model.getPart(), matrices,
                    vertexConsumers.getBuffer(AITRenderLayers.getEntityTranslucentCull(
                            tardis.<OvergrownHandler>handler(TardisComponent.Id.OVERGROWN).getOvergrownTexture())),
                    light, overlay, 1, 1, 1, alpha);
        }*/

        profiler.push("emission");
        boolean alarms = tardis.alarm().enabled().get();


        if (alpha > 0.105f && emission != null && !emission.equals(DatapackConsole.EMPTY)) {
            float u;
            float t;
            float s;

            if ((tardis.stats().getName() != null && "partytardis".equals(tardis.stats().getName().toLowerCase()) ||(!tardis.extra().getInsertedDisc().isEmpty()))) {
                int m = 25;
                int n = MinecraftClient.getInstance().player.age / m + MinecraftClient.getInstance().player.getId();
                int o = DyeColor.values().length;
                int p = n % o;
                int q = (n + 1) % o;
                float r = ((float)(MinecraftClient.getInstance().player.age % m)) / m;
                float[] fs = SheepEntity.getRgbColor(DyeColor.byId(p));
                float[] gs = SheepEntity.getRgbColor(DyeColor.byId(q));
                s = fs[0] * (1f - r) + gs[0] * r;
                t = fs[1] * (1f - r) + gs[1] * r;
                u = fs[2] * (1f - r) + gs[2] * r;
            } else {
                float[] hs = new float[]{ 1.0f, 1.0f, 1.0f };
                s = hs[0];
                t = hs[1];
                u = hs[2];
            }

            float colorAlpha = 1 - alpha;

            boolean power = tardis.fuel().hasPower();

            float red = alarms ? !power ? 0.25f : s - colorAlpha : s - colorAlpha;
            float green = alarms ? !power ? 0.01f : 0.3f : t - colorAlpha;
            float blue = alarms ? !power ? 0.01f : 0.3f : u - colorAlpha;

            ClientLightUtil.renderEmissive((v, l) -> model.renderWithAnimations(
                    tardis, entity, this.model.getPart(), matrices, v, l, overlay, red, green, blue, alpha
            ), emission, vertexConsumers);
        }

        profiler.swap("biome");

        if (this.variant != ClientExteriorVariantRegistry.CORAL_GROWTH) {
            BiomeHandler handler = tardis.handler(TardisComponent.Id.BIOME);
            if (handler.getBiomeKey() != null) {
                Identifier biomeTexture = handler.getBiomeKey().get(this.variant.overrides());

                if (alpha > 0.105f && (biomeTexture != null && !texture.equals(biomeTexture))) {
                    model.renderWithAnimations(tardis, entity, this.model.getPart(),
                            matrices,
                            vertexConsumers.getBuffer(AITRenderLayers.tardisEmissiveCullZOffset(biomeTexture, false)), light, overlay, 1, 1, 1, alpha);
                }

            }
        }

        profiler.pop();
        matrices.pop();

        if (tardis.areVisualShieldsActive()) {
            profiler.push("shields");

            float delta = (tickDelta + MinecraftClient.getInstance().player.age) * 0.03f;
            VertexConsumer vertexConsumer = vertexConsumers
                    .getBuffer(RenderLayer.getEnergySwirl(SHIELDS, delta % 1.0F, (delta * 0.1F) % 1.0F));

            matrices.push();
            matrices.translate(0.5F, 0.0F, 0.5F);

            SHIELDS_MODEL.render(matrices, vertexConsumer, LightmapTextureManager.MAX_LIGHT_COORDINATE, OverlayTexture.DEFAULT_UV, 0f,
                    0.25f, 0.5f, alpha);

            matrices.pop();
            profiler.pop();
        }

        profiler.push("sonic");
        ItemStack stack = tardis.sonic().getExteriorSonic();

        if (stack == null || entity.getWorld() == null) {
            profiler.pop();
            return;
        }

        matrices.push();
        matrices.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(180f + h + this.variant.sonicItemRotations()[0]),
                (float) entity.getPos().toCenterPos().x - entity.getPos().getX(),
                (float) entity.getPos().toCenterPos().y - entity.getPos().getY(),
                (float) entity.getPos().toCenterPos().z - entity.getPos().getZ());
        matrices.translate(this.variant.sonicItemTranslations().x(), this.variant.sonicItemTranslations().y(),
                this.variant.sonicItemTranslations().z());
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(this.variant.sonicItemRotations()[1]));
        matrices.scale(0.9f, 0.9f, 0.9f);

        int lightAbove = WorldRenderer.getLightmapCoordinates(entity.getWorld(), entity.getPos().up());
        MinecraftClient.getInstance().getItemRenderer().renderItem(stack, ModelTransformationMode.GROUND, lightAbove,
                OverlayTexture.DEFAULT_UV, matrices, vertexConsumers, entity.getWorld(), 0);

        matrices.pop();
        profiler.pop();
    }

    private void updateModel(Tardis tardis) {
        if (tardis.getExterior() == null)
            return;
        ClientExteriorVariantSchema variant = tardis.getExterior().getVariant().getClient();

        if (this.variant != variant) {
            this.variant = variant;
            this.model = variant.model();
        }
    }

    private void applyNameTransforms(Tardis tardis, MatrixStack matrices, String name) {
        if (name.equalsIgnoreCase("grumm") || name.equalsIgnoreCase("dinnerbone")) {
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-90f));
            matrices.translate(0, tardis.stats().getYScale() + 0.25f, tardis.stats().getZScale() - 1.7f);
        }

        matrices.scale(tardis.stats().getXScale(), tardis.stats().getYScale(), tardis.stats().getZScale());
    }
}
