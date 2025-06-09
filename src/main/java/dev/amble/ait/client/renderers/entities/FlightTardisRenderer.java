package dev.amble.ait.client.renderers.entities;

import dev.amble.ait.api.tardis.TardisComponent;
import dev.amble.ait.client.models.exteriors.ExteriorModel;
import dev.amble.ait.client.models.machines.ShieldsModel;
import dev.amble.ait.client.renderers.AITRenderLayers;
import dev.amble.ait.client.renderers.VortexUtil;
import dev.amble.ait.client.util.ClientLightUtil;
import dev.amble.ait.core.AITDimensions;
import dev.amble.ait.core.entities.FlightTardisEntity;
import dev.amble.ait.core.tardis.Tardis;
import dev.amble.ait.core.tardis.handler.BiomeHandler;
import dev.amble.ait.data.schema.exterior.ClientExteriorVariantSchema;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;

public class FlightTardisRenderer extends EntityRenderer<FlightTardisEntity> {

    private ExteriorModel model;
    private ClientExteriorVariantSchema variant;

    public FlightTardisRenderer(EntityRendererFactory.Context context) {
        super(context);
    }

    @Override
    public void render(FlightTardisEntity entity, float yaw, float tickDelta, MatrixStack matrices,
            VertexConsumerProvider vertexConsumers, int light) {
        if (!entity.isLinked())
            return;

        Tardis tardis = entity.tardis().get();

        if (tardis == null) return;

        this.updateModel(tardis);

        if (entity.getControllingPassenger() == null ||
                !(entity.getControllingPassenger() instanceof AbstractClientPlayerEntity player)) return;

        if (player.getVehicle() == null || player.getVehicle() != entity) return;

        Vec3d vec3d = entity.getRotationVec(tickDelta);
        Vec3d vec3d2 = entity.lerpVelocity(tickDelta);

        double d = vec3d2.horizontalLengthSquared();
        double e = vec3d.horizontalLengthSquared();

        matrices.push();
        if (tardis.door().isClosed() && !entity.groundCollision)
            matrices.translate(0, 0.25f * -vec3d2.getY(), 0);

        if (tardis.travel().position().getDimension() == AITDimensions.TIME_VORTEX_WORLD) {
            VortexUtil vortexUtil = tardis.stats().getVortexEffects().toUtil();
            matrices.push();
            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees((float) MinecraftClient.getInstance().player.age / 100 * 360f));
            matrices.translate(0, 0, 500);
            vortexUtil.renderVortex(matrices);
            vortexUtil.renderVortexLayer(matrices, 1.5f);
            vortexUtil.renderVortexLayer(matrices, 2.5f);
            matrices.pop();
        }

        if (d > 0.0 && e > 0.0) {
            double l = (vec3d2.x * vec3d.x + vec3d2.z * vec3d.z) / Math.sqrt(d * e);
            double m = vec3d2.x * vec3d.z - vec3d2.z * vec3d.x;
            double v = Math.signum(m) * Math.acos(l);
            matrices.multiply(RotationAxis.POSITIVE_Y.rotation((float) v));
        }

        boolean doorsClosed = tardis.door().isClosed();
        float deg = doorsClosed ? (float) (d * 22.5f) : (float) -d * 22.5f;

        if (!entity.verticalCollision && !doorsClosed) {
            this.model.getPart().setAngles((float) 0, 0, 0);
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180f));
        } else if (!entity.verticalCollision) {
            this.model.getPart().setAngles((float) 0, ((entity.getRotation(tickDelta)) * tardis.travel().speed()), 0);
        }

        /*if (!entity.verticalCollision)
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees((float) (2f * Math.cos(0.2f * (tickDelta + entity.age))) + deg));*/

        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(entity.verticalCollision ? 180f : (float) (2f * Math.sin(0.2f * (tickDelta + entity.age)) + 180f)));

        this.model.renderEntity(entity, this.model.getPart(), matrices, vertexConsumers.getBuffer(AITRenderLayers.getEntityTranslucentCull(getTexture(entity))), light, OverlayTexture.DEFAULT_UV, 1, 1, 1, 1);

        if (variant.emission() != null && tardis.fuel().hasPower()) {
            boolean alarms = tardis.alarm().enabled().get();

            float color = alarms ? 0.3f : 1f;

            ClientLightUtil.renderEmissivable(tardis.fuel().hasPower(), (v, l) -> model.renderEntity(
                    entity, this.model.getPart(), matrices, v, l, OverlayTexture.DEFAULT_UV, color, color, color, 1
            ), variant.emission(), vertexConsumers);
        }

        BiomeHandler biome = tardis.handler(TardisComponent.Id.BIOME);
        Identifier biomeTexture = biome.getBiomeKey().get(variant.overrides());

        if (biomeTexture != null && !this.getTexture(entity).equals(biomeTexture))
            model.renderEntity(entity, this.model.getPart(), matrices, vertexConsumers.getBuffer(AITRenderLayers.getEntityTranslucentCull(biomeTexture)), light, OverlayTexture.DEFAULT_UV, 1, 1, 1, 1);

        int maxLight = 0xF000F0;

        matrices.pop();
        if (tardis.areVisualShieldsActive()) {
            matrices.push();

            float delta = ((tickDelta + entity.age) * 0.03f);
            ShieldsModel shieldsModel = new ShieldsModel(ShieldsModel.getTexturedModelData().createModel());
            VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEnergySwirl(new Identifier("textures/misc/forcefield.png"), delta % 1.0F, (delta * 0.1F) % 1.0F));
            shieldsModel.render(matrices, vertexConsumer, maxLight, OverlayTexture.DEFAULT_UV, 0f, 0.25f, 0.5f, 1f);
            matrices.pop();
        }
    }

    private ExteriorModel getModel(Tardis tardis) {
        if (model == null)
            model = tardis.getExterior().getVariant().getClient().model();

        return model;
    }

    @Override
    public Identifier getTexture(FlightTardisEntity entity) {
        if (!entity.isLinked())
            return SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE; // random texture just so i dont crash

        return entity.tardis().get().getExterior().getVariant().getClient().texture();
    }

    private void updateModel(Tardis tardis) {
        ClientExteriorVariantSchema variant = tardis.getExterior().getVariant().getClient();

        if (this.variant != variant) {
            this.variant = variant;
            this.model = variant.model();
        }
    }
}
