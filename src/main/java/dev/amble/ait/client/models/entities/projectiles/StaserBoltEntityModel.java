package dev.amble.ait.client.models.entities.projectiles;

import dev.amble.ait.module.gun.core.entity.StaserBoltEntity;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.client.util.math.MatrixStack;

public class StaserBoltEntityModel extends SinglePartEntityModel<StaserBoltEntity> {
    private final ModelPart bone;
    public StaserBoltEntityModel(ModelPart root) {
        this.bone = root.getChild("bone");
    }
    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData bone = modelPartData.addChild("bone", ModelPartBuilder.create().uv(0, 12).cuboid(0.0F, -1.0F, -5.0F, 0.0F, 2.0F, 10.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 21.0F, 0.0F, 0.0F, 0.0F, -0.7854F));

        ModelPartData cube_r1 = bone.addChild("cube_r1", ModelPartBuilder.create().uv(0, 12).cuboid(0.0F, -1.0F, -5.0F, 0.0F, 2.0F, 10.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -1.5708F));
        return TexturedModelData.of(modelData, 32, 32);
    }
    @Override
    public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
        bone.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
    }

    @Override
    public ModelPart getPart() {
        return bone;
    }

    @Override
    public void setAngles(StaserBoltEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {

    }
}