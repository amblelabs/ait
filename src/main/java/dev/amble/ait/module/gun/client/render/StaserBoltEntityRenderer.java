package dev.amble.ait.module.gun.client.render;

import dev.amble.ait.AITMod;
import dev.amble.ait.client.models.entities.projectiles.StaserBoltEntityModel;
import dev.amble.ait.module.gun.core.entity.StaserBoltEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;

@Environment(value=EnvType.CLIENT)
public class StaserBoltEntityRenderer
        extends EntityRenderer<StaserBoltEntity> {

    public static final Identifier TEXTURE = AITMod.id("textures/entity/projectiles/staser_bolt.png");
    public StaserBoltEntityModel model = new StaserBoltEntityModel(StaserBoltEntityModel.getTexturedModelData().createModel());

    public StaserBoltEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
    }

    @Override
    public void render(StaserBoltEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        matrices.push();
        matrices.scale(1.5f, 1.5f, 1.5f);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(entity.getYaw()));
        matrices.multiply(RotationAxis.NEGATIVE_X.rotationDegrees(entity.getPitch()));
        matrices.translate(0, -1.125f, 0);
        model.render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntityTranslucentEmissive(TEXTURE)), light, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
        matrices.pop();
    }

    @Override
    public Identifier getTexture(StaserBoltEntity entity) {
        return TEXTURE;
    }
}
