package dev.amble.ait.client.models.wearables;

import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;

public class RespiratorModel extends EntityModel {

    public final ModelPart mask;

    public RespiratorModel(ModelPart root) {
        this.mask = root.getChild("mask");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData partData = modelData.getRoot();

        ModelPartData mask = partData.addChild("mask", ModelPartBuilder.create(),
                ModelTransform.pivot(0.0F, 24.0F, 0.0F));

        ModelPartData cube_r1 = mask.addChild("cube_r1",
                ModelPartBuilder.create().uv(23, 22).cuboid(5.5F, -1.5F, -1.5F, 2.0F, 3.0F, 4.0F, new Dilation(0.01F)),
                ModelTransform.of(0.0F, 0, 0.0F, 0.7418F, 0.829F, 0.0F));

        ModelPartData cube_r2 = mask.addChild("cube_r2",
                ModelPartBuilder.create().uv(25, 0).cuboid(5.5F, -1.5F, -2.5F, 2.0F, 3.0F, 4.0F, new Dilation(0.01F)),
                ModelTransform.of(0.0F, 0, 0.0F, 2.3998F, 0.829F, 3.1416F));

        ModelPartData cube_r3 = mask.addChild("cube_r3",
                ModelPartBuilder.create().uv(14, 17).cuboid(4.5F, -2.0F, -2.0F, 2.0F, 3.0F, 4.0F, new Dilation(0.0F))
                        .uv(0, 0).cuboid(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new Dilation(0.02F)).uv(0, 17)
                        .cuboid(2.5F, -8.5F, -4.5F, 2.0F, 9.0F, 9.0F, new Dilation(0.01F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.5708F, 0.0F));

        return TexturedModelData.of(modelData, 64, 64);
    }

    @Override
    public void setAngles(Entity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw,
            float headPitch) {
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green,
            float blue, float alpha) {
        mask.render(matrices, vertices, light, overlay, red, green, blue, alpha);
    }
}
