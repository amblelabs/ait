package dev.amble.ait.client.models.machines;

import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;

public class ZeitonCageModel extends SinglePartEntityModel {
    private final ModelPart cage;
    private final ModelPart cube;
    public ZeitonCageModel(ModelPart root) {
        this.cage = root.getChild("cage");
        this.cube = this.cage.getChild("cube");
    }
    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData cage = modelPartData.addChild("cage", ModelPartBuilder.create().uv(64, 0).cuboid(-16.0F, -2.0F, 0.0F, 16.0F, 2.0F, 16.0F, new Dilation(0.0F)), ModelTransform.pivot(8.0F, 24.0F, -8.0F));

        ModelPartData cube_r1 = cage.addChild("cube_r1", ModelPartBuilder.create().uv(64, 18).cuboid(-1.0F, -9.0F, 0.0F, 3.0F, 9.0F, 0.0F, new Dilation(0.01F)), ModelTransform.of(-2.0F, -2.0F, 0.0F, -0.7854F, 0.0F, 0.0F));

        ModelPartData cube_r2 = cage.addChild("cube_r2", ModelPartBuilder.create().uv(64, 18).mirrored().cuboid(-2.0F, -9.0F, 0.0F, 3.0F, 9.0F, 0.0F, new Dilation(0.01F)).mirrored(false), ModelTransform.of(-14.0F, -2.0F, 0.0F, -0.7854F, 0.0F, 0.0F));

        ModelPartData cube_r3 = cage.addChild("cube_r3", ModelPartBuilder.create().uv(64, 18).mirrored().cuboid(-2.0F, -9.0F, 0.0F, 3.0F, 9.0F, 0.0F, new Dilation(0.01F)).mirrored(false), ModelTransform.of(-14.0F, -2.0F, 16.0F, 0.7854F, 0.0F, 0.0F));

        ModelPartData cube_r4 = cage.addChild("cube_r4", ModelPartBuilder.create().uv(64, 18).cuboid(-1.0F, -9.0F, 0.0F, 3.0F, 9.0F, 0.0F, new Dilation(0.01F)), ModelTransform.of(-2.0F, -2.0F, 16.0F, 0.7854F, 0.0F, 0.0F));

        ModelPartData cube = cage.addChild("cube", ModelPartBuilder.create(), ModelTransform.pivot(-8.0F, -13.0F, 8.0F));

        ModelPartData cube_r5 = cube.addChild("cube_r5", ModelPartBuilder.create().uv(0, 64).cuboid(-8.0F, -1.0F, -8.0F, 16.0F, 3.0F, 16.0F, new Dilation(0.0F))
        .uv(0, 0).cuboid(-8.0F, -8.0F, -8.0F, 16.0F, 16.0F, 16.0F, new Dilation(-1.0F))
        .uv(0, 32).cuboid(-8.0F, -8.0F, -8.0F, 16.0F, 16.0F, 16.0F, new Dilation(-0.5F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -0.7854F, 0.0F));
        return TexturedModelData.of(modelData, 128, 128);
    }
    @Override
    public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
        cage.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
    }

    @Override
    public ModelPart getPart() {
        return cage;
    }

    @Override
    public void setAngles(Entity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {

    }
}