package dev.amble.ait.client.models.decoration;

import dev.amble.ait.client.animation.decoration.RotaryPhoneAnimation;

import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.AnimationState;
import net.minecraft.entity.Entity;

public class RotaryPhoneModel extends SinglePartEntityModel<Entity> {
    private final ModelPart root;
    private final ModelPart bone;
    private final ModelPart phone;

    public RotaryPhoneModel(ModelPart root) {
        this.root = root;
        this.bone = root.getChild("bone");
        this.phone = this.bone.getChild("top").getChild("phone");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData bone = modelPartData.addChild("bone", ModelPartBuilder.create().uv(0, 0).cuboid(-5.0F, -2.0F, -6.0F, 10.0F, 2.0F, 12.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 24.0F, 0.0F));

        ModelPartData top = bone.addChild("top", ModelPartBuilder.create().uv(29, 15).cuboid(-4.0F, -4.0F, -2.0F, 8.0F, 4.0F, 7.0F, new Dilation(0.0F))
                .uv(0, 31).cuboid(-3.999F, -4.537F, 0.0213F, 8.0F, 1.0F, 5.0F, new Dilation(0.0F))
                .uv(2, 40).cuboid(-4.0F, -5.5F, 2.0F, 8.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -2.0F, 0.0F));

        ModelPartData cube_r1 = top.addChild("cube_r1", ModelPartBuilder.create().uv(32, 37).cuboid(-2.5F, -1.1F, -2.0F, 5.0F, 0.0F, 5.0F, new Dilation(0.001F))
                .uv(29, 27).cuboid(-4.0F, -1.0F, -3.0F, 8.0F, 2.0F, 7.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, -1.0F, -2.0F, 0.7854F, 0.0F, 0.0F));

        ModelPartData dial = top.addChild("dial", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 2.0F, 0.0F));

        ModelPartData cube_r2 = dial.addChild("cube_r2", ModelPartBuilder.create().uv(27, 37).cuboid(-2.5F, -1.6F, -2.0F, 5.0F, 1.0F, 5.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -3.0F, -2.0F, 0.7854F, 0.0F, 0.0F));

        ModelPartData phone = top.addChild("phone", ModelPartBuilder.create().uv(0, 48).cuboid(-1.5F, -1.0F, -0.4F, 10.0F, 7.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -5.0F, 2.5F));

        ModelPartData cube_r3 = phone.addChild("cube_r3", ModelPartBuilder.create().uv(0, 15).cuboid(-7.0F, -7.5F, 0.0F, 14.0F, 15.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -2.3126F));
        return TexturedModelData.of(modelData, 64, 64);
    }

    public void animate(AnimationState ring, float progress, boolean lifted) {
        this.root.traverse().forEach(ModelPart::resetTransform);
        this.updateAnimation(ring, RotaryPhoneAnimation.RING, progress);
        this.phone.visible = !lifted;
    }

    @Override
    public void setAngles(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
        this.bone.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
    }

    @Override
    public ModelPart getPart() {
        return this.root;
    }
}
