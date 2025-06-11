package dev.amble.ait.client.renderers.exteriors;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.amble.ait.client.util.ClientTardisUtil;
import dev.amble.lib.data.CachedDirectedGlobalPos;
import org.joml.Vector3f;

import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.RotationPropertyHelper;
import net.minecraft.util.math.Vec3d;
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

    public ExteriorRenderer(BlockEntityRendererFactory.Context ctx) {}

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

        this.updateModel(tardis);

        if (tardis.travel().getAlpha() > 0)
            this.renderExterior(profiler, tardis, entity, tickDelta, matrices, vertexConsumers, light, overlay);

        if ((tardis.door().getLeftRot() > 0 || variant.hasTransparentDoors()) && !tardis.isGrowth() && tardis.travel().isLanded() &&
        !tardis.siege().isActive())
            BOTI.EXTERIOR_RENDER_QUEUE.add(entity);

        profiler.pop();

        profiler.pop();
    }

    private void renderExterior(Profiler profiler, ClientTardis tardis, T entity, float tickDelta, MatrixStack matrices,
                                VertexConsumerProvider vertexConsumers, int light, int overlay) {
        final float alpha = tardis.travel().getAlpha(tickDelta);
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

        BlockState blockState = entity.getCachedState();
        int k = blockState.get(ExteriorBlock.ROTATION);
        float h = RotationPropertyHelper.toDegrees(k);

        matrices.push();

        // adjust based off animation position
        Vector3f animPositionOffset = travel.getAnimationPosition(tickDelta);
        matrices.translate(animPositionOffset.x(), animPositionOffset.y(), animPositionOffset.z());

        matrices.translate(0.5f, 0.0f, 0.5f);

        // adjust based off animation rotation
        Vector3f animRotationOffset = travel.getAnimationRotation(tickDelta);
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(animRotationOffset.z()));
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(animRotationOffset.y()));
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(animRotationOffset.x()));

        this.applyNameTransforms(tardis, matrices, tardis.stats().getName(), tickDelta);

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

        if (travel.antigravs().get() && tardis.flight().falling().get()) {
            float sinFunc = (float) Math.sin((MinecraftClient.getInstance().player.age / 400f * 220f) * 0.2f + 0.2f);
            matrices.translate(0, sinFunc, 0);
        }

        model.renderWithAnimations(tardis, entity, this.model.getPart(),
                matrices, vertexConsumers.getBuffer(AITRenderLayers.getEntityTranslucentCull(texture)), light, overlay, 1, 1,
                1, alpha);

        profiler.push("emission");

        boolean alarms = tardis.alarm().isEnabled();
        boolean power = tardis.fuel().hasPower();

        // the emission should only render IF
        //  1) the alpha of the exterior is higher than a certain threshold
        //  2) there is an emissive texture
        //  3) there's power OR alarms on
        if (alpha > 0.105f && emission != null && (power || alarms)
                && !emission.equals(DatapackConsole.EMPTY)) {
            float u = 1;
            float t = 1;
            float s = 1;

            if ("partytardis".equals(tardis.stats().getName()) ||
                    !tardis.extra().getInsertedDisc().isEmpty()) {
                final float[] rgb = ClientTardisUtil.getPartyColors();

                u = rgb[0];
                t = rgb[1];
                s = rgb[2];
            } else if (tardis.sonic().getExteriorSonic() != null) {
                float time = MinecraftClient.getInstance().player.age + MinecraftClient.getInstance().getTickDelta();
                float progress = (float)((Math.sin(time * 0.03) + 1) / 2.0f);

                final float FROM_R = 1.0f, FROM_G = 1.0f, FROM_B = 1.0f;
                final float TO_R = 0.3f, TO_G = 0.3f, TO_B = 1.0f;

                s = FROM_R * (1f - progress) + TO_R * progress;
                t = FROM_G * (1f - progress) + TO_G * progress;
                u = FROM_B * (1f - progress) + TO_B * progress;
            }

            float colorAlpha = 1 - alpha;

            float red = alarms
                    ? !power ? 0.25f : s - colorAlpha
                    : s - colorAlpha;

            float green = alarms
                    ? !power ? 0.01f : 0.3f
                    : t - colorAlpha;

            float blue = alarms
                    ? !power ? 0.01f : 0.3f
                    : u - colorAlpha;

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

    private void applyNameTransforms(Tardis tardis, MatrixStack matrices, String name, float delta) {
        Vector3f scale = tardis.travel().getScale(delta);

        if (name.equalsIgnoreCase("grumm") || name.equalsIgnoreCase("dinnerbone")) {
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-90f));
            matrices.translate(0, scale.y + 0.25f, scale.z - 1.7f);
        }

        matrices.scale(scale.x, scale.y, scale.z);
    }

    @Override
    public boolean rendersOutsideBoundingBox(ExteriorBlockEntity exteriorBlockEntity) {
        return true;
    }

    @Override
    public int getRenderDistance() {
        return 256;
    }

    @Override
    public boolean isInRenderDistance(ExteriorBlockEntity exteriorBlockEntity, Vec3d vec3d) {
        return Vec3d.ofCenter(exteriorBlockEntity.getPos()).multiply(1.0, 0.0, 1.0).isInRange(vec3d.multiply(1.0, 0.0, 1.0), this.getRenderDistance());
    }
}
