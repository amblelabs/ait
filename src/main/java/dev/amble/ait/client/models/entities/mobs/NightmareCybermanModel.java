package dev.amble.ait.client.models.entities.mobs;

import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;

import dev.amble.ait.core.entities.CybermanEntity;

public class NightmareCybermanModel<T extends CybermanEntity> extends SinglePartEntityModel<T> {
    private final ModelPart cyberman;
    private final ModelPart RightLeg;
    private final ModelPart LeftLeg;
    private final ModelPart topside;
    private final ModelPart Body;
    private final ModelPart head;
    private final ModelPart LeftArm;
    private final ModelPart RightArm;
    public NightmareCybermanModel(ModelPart root) {
        this.cyberman = root.getChild("cyberman");
        this.RightLeg = this.cyberman.getChild("RightLeg");
        this.LeftLeg = this.cyberman.getChild("LeftLeg");
        this.topside = this.cyberman.getChild("topside");
        this.Body = this.topside.getChild("Body");
        this.head = this.topside.getChild("head");
        this.LeftArm = this.topside.getChild("LeftArm");
        this.RightArm = this.topside.getChild("RightArm");
    }
    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData cyberman = modelPartData.addChild("cyberman", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 24.0F, 0.0F));

        ModelPartData RightLeg = cyberman.addChild("RightLeg", ModelPartBuilder.create().uv(48, 55).mirrored().cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new Dilation(0.0F)).mirrored(false)
        .uv(0, 48).cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new Dilation(0.1F)), ModelTransform.of(-2.0F, -12.0F, 0.0F, 0.0F, 0.0F, 0.0436F));

        ModelPartData LeftLeg = cyberman.addChild("LeftLeg", ModelPartBuilder.create().uv(48, 55).cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new Dilation(0.0F))
        .uv(0, 48).mirrored().cuboid(-1.9F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new Dilation(0.1F)).mirrored(false), ModelTransform.of(1.9F, -12.0F, 0.0F, 0.0F, 0.0F, -0.0436F));

        ModelPartData topside = cyberman.addChild("topside", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, -12.0F, 0.0F));

        ModelPartData Body = topside.addChild("Body", ModelPartBuilder.create().uv(32, 39).cuboid(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new Dilation(0.0F))
        .uv(56, 0).cuboid(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new Dilation(0.1F))
        .uv(56, 39).cuboid(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new Dilation(0.4F)), ModelTransform.pivot(0.0F, -12.0F, 0.0F));

        ModelPartData head = topside.addChild("head", ModelPartBuilder.create().uv(0, 0).cuboid(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new Dilation(0.0F))
        .uv(0, 16).cuboid(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new Dilation(0.01F))
        .uv(0, 32).cuboid(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new Dilation(0.11F))
        .uv(32, 0).cuboid(-6.0F, -10.0F, 0.0F, 12.0F, 10.0F, 0.0F, new Dilation(0.0F))
        .uv(24, 16).cuboid(-1.0F, -10.0F, -2.0F, 2.0F, 3.0F, 4.0F, new Dilation(0.0F))
        .uv(32, 23).cuboid(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new Dilation(0.5F)), ModelTransform.pivot(0.0F, -12.0F, 0.0F));

        ModelPartData LeftArm = topside.addChild("LeftArm", ModelPartBuilder.create().uv(48, 55).cuboid(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new Dilation(0.0F))
        .uv(16, 64).cuboid(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new Dilation(0.1F))
        .uv(0, 64).cuboid(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new Dilation(0.3F)), ModelTransform.of(5.0F, -10.0F, 0.0F, 0.0F, 0.0F, -0.1309F));

        ModelPartData RightArm = topside.addChild("RightArm", ModelPartBuilder.create().uv(48, 55).mirrored().cuboid(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new Dilation(0.0F)).mirrored(false)
        .uv(0, 0).cuboid(-4.0F, 4.0F, -1.0F, 1.0F, 5.0F, 2.0F, new Dilation(0.001F))
        .uv(16, 64).mirrored().cuboid(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new Dilation(0.1F)).mirrored(false)
        .uv(0, 64).mirrored().cuboid(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new Dilation(0.3F)).mirrored(false), ModelTransform.of(-5.0F, -10.0F, 0.0F, 0.0F, 0.0F, 0.1309F));

        ModelPartData RightArm_r1 = RightArm.addChild("RightArm_r1", ModelPartBuilder.create().uv(0, 16).cuboid(-0.5F, 0.5F, -1.0F, 1.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(-2.2351F, 2.1602F, 0.0F, 0.0F, 0.0F, 0.5672F));
        return TexturedModelData.of(modelData, 128, 128);
    }
    @Override
    public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
        cyberman.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
    }

    @Override
    public ModelPart getPart() {
        return cyberman;
    }

    @Override
    public void setAngles(CybermanEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {

    }

    public <T extends Entity> void renderEntity(T falling, ModelPart root, MatrixStack matrices,
                                                           VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
        root.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
    }
}