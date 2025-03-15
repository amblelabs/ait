package dev.amble.ait.client.models.machines;

import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;

public class AstralMapModel extends SinglePartEntityModel {
    private final ModelPart astral_map;
    public final ModelPart void_cube;

    public AstralMapModel(ModelPart root) {
        this.astral_map = root.getChild("astral_map");
        this.void_cube = this.astral_map.getChild("void_cube");

    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData astral_map = modelPartData.addChild("astral_map", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 24.0F, 0.0F));

        ModelPartData base = astral_map.addChild("base", ModelPartBuilder.create().uv(26, 35).cuboid(-3.0455F, -16.0F, -3.2895F, 6.0F, 16.0F, 1.0F, new Dilation(0.0F))
                .uv(26, 35).cuboid(-3.0455F, -16.0F, 6.1029F, 6.0F, 16.0F, 1.0F, new Dilation(0.0F))
                .uv(0, 0).cuboid(-7.5455F, -15.0F, -5.3895F, 16.0F, 0.0F, 14.0F, new Dilation(0.0F))
                .uv(0, 26).cuboid(-5.5455F, -0.2F, -2.3895F, 11.0F, 0.0F, 9.0F, new Dilation(0.0F))
                .uv(68, 36).cuboid(-4.0455F, -13.5F, 7.5918F, 8.0F, 3.0F, 1.0F, new Dilation(0.0F))
                .uv(68, 36).cuboid(-4.0455F, -13.5F, -5.2646F, 8.0F, 3.0F, 1.0F, new Dilation(0.0F))
                .uv(0, 40).cuboid(-0.0455F, -16.0F, -8.2895F, 0.0F, 16.0F, 5.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData cube_r1 = base.addChild("cube_r1", ModelPartBuilder.create().uv(5, 40).mirrored().cuboid(0.0F, -8.0F, -2.5F, 0.0F, 16.0F, 5.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(5.1201F, -8.0F, 8.352F, 0.0F, 1.0472F, 0.0F));

        ModelPartData cube_r2 = base.addChild("cube_r2", ModelPartBuilder.create().uv(5, 40).cuboid(0.0F, -8.0F, -2.5F, 0.0F, 16.0F, 5.0F, new Dilation(0.0F)), ModelTransform.of(-5.21F, -8.0F, 8.3537F, 0.0F, -1.0472F, 0.0F));

        ModelPartData cube_r3 = base.addChild("cube_r3", ModelPartBuilder.create().uv(68, 36).cuboid(-4.0F, -6.0F, -0.5F, 8.0F, 3.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(5.5215F, -7.5F, -1.5505F, 0.0F, -1.0472F, 0.0F));

        ModelPartData cube_r4 = base.addChild("cube_r4", ModelPartBuilder.create().uv(68, 36).cuboid(-4.0F, -6.0F, -0.5F, 8.0F, 3.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-5.6125F, -7.5F, -1.5505F, 0.0F, 1.0472F, 0.0F));

        ModelPartData cube_r5 = base.addChild("cube_r5", ModelPartBuilder.create().uv(68, 36).cuboid(-4.0F, -6.0F, -0.5F, 8.0F, 3.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-5.6125F, -7.5F, 4.8777F, 0.0F, -1.0472F, 0.0F));

        ModelPartData cube_r6 = base.addChild("cube_r6", ModelPartBuilder.create().uv(68, 36).cuboid(-4.0F, -6.0F, -0.5F, 8.0F, 3.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(5.5215F, -7.5F, 4.8777F, 0.0F, 1.0472F, 0.0F));

        ModelPartData cube_r7 = base.addChild("cube_r7", ModelPartBuilder.create().uv(26, 35).cuboid(-3.0F, -16.0F, 0.0F, 6.0F, 16.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(3.5885F, 0.0F, 4.0048F, 0.0F, 1.0472F, 0.0F));

        ModelPartData cube_r8 = base.addChild("cube_r8", ModelPartBuilder.create().uv(26, 35).cuboid(-3.0F, -16.0F, 0.0F, 6.0F, 16.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-3.6795F, 0.0F, 4.0048F, 0.0F, -1.0472F, 0.0F));

        ModelPartData cube_r9 = base.addChild("cube_r9", ModelPartBuilder.create().uv(26, 35).cuboid(-3.0F, -16.0F, -1.0F, 6.0F, 16.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-3.6795F, 0.0F, -0.1914F, 0.0F, 1.0472F, 0.0F));

        ModelPartData cube_r10 = base.addChild("cube_r10", ModelPartBuilder.create().uv(26, 35).cuboid(-3.0F, -16.0F, -1.0F, 6.0F, 16.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(3.5885F, 0.0F, -0.1914F, 0.0F, -1.0472F, 0.0F));

        ModelPartData top = astral_map.addChild("top", ModelPartBuilder.create().uv(0, 14).cuboid(-7.5455F, -27.9F, -4.1895F, 15.0F, 0.0F, 12.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData cube_r11 = top.addChild("cube_r11", ModelPartBuilder.create().uv(40, 78).cuboid(-0.8F, -5.5F, -0.5F, 2.0F, 11.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(3.7033F, -22.4397F, 8.0422F, 0.1618F, -1.0418F, -0.1735F));

        ModelPartData cube_r12 = top.addChild("cube_r12", ModelPartBuilder.create().uv(40, 78).cuboid(-1.2F, -5.5F, -0.5F, 2.0F, 11.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-3.7033F, -22.4397F, 8.0422F, 0.1618F, 1.0418F, 0.1735F));

        ModelPartData cube_r13 = top.addChild("cube_r13", ModelPartBuilder.create().uv(40, 78).cuboid(-1.1F, -5.5F, -0.5F, 2.0F, 11.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-3.7162F, -22.46F, -4.5449F, -0.1618F, -1.0418F, 0.1735F));

        ModelPartData cube_r14 = top.addChild("cube_r14", ModelPartBuilder.create().uv(40, 78).cuboid(-0.9F, -5.5F, -0.5F, 2.0F, 11.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(3.7162F, -22.46F, -4.5449F, -0.1618F, 1.0418F, -0.1735F));

        ModelPartData cube_r15 = top.addChild("cube_r15", ModelPartBuilder.create().uv(40, 78).cuboid(-8.0F, -5.5F, -0.5F, 2.0F, 11.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-0.4814F, -21.8444F, 1.8218F, 0.0F, 0.0F, 0.0873F));

        ModelPartData cube_r16 = top.addChild("cube_r16", ModelPartBuilder.create().uv(40, 78).cuboid(-1.0F, -5.5F, -0.5F, 2.0F, 11.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(7.4547F, -22.4545F, 1.7805F, 0.0F, 0.0F, -0.0873F));

        ModelPartData cube_r17 = top.addChild("cube_r17", ModelPartBuilder.create().uv(31, 26).cuboid(-4.0F, -6.0F, -0.5F, 8.0F, 3.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -11.129F, -5.0647F, 0.0873F, 0.0F, 0.0F));

        ModelPartData cube_r18 = top.addChild("cube_r18", ModelPartBuilder.create().uv(31, 26).cuboid(-4.0F, -6.0F, -0.5F, 8.0F, 3.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(5.946F, -11.129F, -1.6318F, 0.0873F, -1.0472F, 0.0F));

        ModelPartData cube_r19 = top.addChild("cube_r19", ModelPartBuilder.create().uv(31, 26).cuboid(-4.0F, -6.0F, -0.5F, 8.0F, 3.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-5.946F, -11.129F, -1.6318F, 0.0873F, 1.0472F, 0.0F));

        ModelPartData cube_r20 = top.addChild("cube_r20", ModelPartBuilder.create().uv(18, 69).cuboid(-4.0F, -6.0F, -0.5F, 8.0F, 3.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-5.946F, -11.129F, 5.2341F, -0.0873F, -1.0472F, 0.0F));

        ModelPartData cube_r21 = top.addChild("cube_r21", ModelPartBuilder.create().uv(18, 69).cuboid(-4.0F, -6.0F, -0.5F, 8.0F, 3.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -11.129F, 8.6671F, -0.0873F, 0.0F, 0.0F));

        ModelPartData cube_r22 = top.addChild("cube_r22", ModelPartBuilder.create().uv(18, 69).cuboid(-4.0F, -6.0F, -0.5F, 8.0F, 3.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(5.946F, -11.129F, 5.2341F, -0.0873F, 1.0472F, 0.0F));

        ModelPartData cube_r23 = top.addChild("cube_r23", ModelPartBuilder.create().uv(0, 61).cuboid(-4.0F, -6.0F, -0.5F, 8.0F, 11.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-6.0215F, -22.0F, 5.2777F, 0.0873F, -1.0472F, 0.0F));

        ModelPartData cube_r24 = top.addChild("cube_r24", ModelPartBuilder.create().uv(0, 61).cuboid(-4.0F, -6.0F, -0.5F, 8.0F, 11.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(6.0215F, -22.0F, 5.2777F, 0.0873F, 1.0472F, 0.0F));

        ModelPartData cube_r25 = top.addChild("cube_r25", ModelPartBuilder.create().uv(0, 61).cuboid(-4.0F, -6.0F, -0.5F, 8.0F, 11.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -22.0F, 8.7542F, 0.0873F, 0.0F, 0.0F));

        ModelPartData cube_r26 = top.addChild("cube_r26", ModelPartBuilder.create().uv(0, 61).cuboid(-4.0F, -6.0F, -0.5F, 8.0F, 11.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-6.0215F, -22.0F, -1.6754F, -0.0873F, 1.0472F, 0.0F));

        ModelPartData cube_r27 = top.addChild("cube_r27", ModelPartBuilder.create().uv(0, 61).cuboid(-4.0F, -6.0F, -0.5F, 8.0F, 11.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(6.0215F, -22.0F, -1.6754F, -0.0873F, -1.0472F, 0.0F));

        ModelPartData cube_r28 = top.addChild("cube_r28", ModelPartBuilder.create().uv(0, 61).cuboid(-4.0F, -6.0F, -0.5F, 8.0F, 11.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -22.0F, -5.1519F, -0.0873F, 0.0F, 0.0F));

        ModelPartData second = top.addChild("second", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData cube_r29 = second.addChild("cube_r29", ModelPartBuilder.create().uv(0, 61).cuboid(-4.0F, -6.0F, 0.5F, 8.0F, 11.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -22.0F, -5.1519F, -0.0873F, 0.0F, 0.0F));

        ModelPartData cube_r30 = second.addChild("cube_r30", ModelPartBuilder.create().uv(0, 61).cuboid(-4.0F, -6.0F, 0.5F, 8.0F, 11.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(6.0215F, -22.0F, -1.6754F, -0.0873F, -1.0472F, 0.0F));

        ModelPartData cube_r31 = second.addChild("cube_r31", ModelPartBuilder.create().uv(0, 61).cuboid(-4.0F, -6.0F, -1.5F, 8.0F, 11.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(6.0215F, -22.0F, 5.2777F, 0.0873F, 1.0472F, 0.0F));

        ModelPartData cube_r32 = second.addChild("cube_r32", ModelPartBuilder.create().uv(0, 61).cuboid(-4.0F, -6.0F, -1.5F, 8.0F, 11.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -22.0F, 8.7542F, 0.0873F, 0.0F, 0.0F));

        ModelPartData cube_r33 = second.addChild("cube_r33", ModelPartBuilder.create().uv(0, 61).cuboid(-4.0F, -6.0F, -1.5F, 8.0F, 11.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-6.0215F, -22.0F, 5.2777F, 0.0873F, -1.0472F, 0.0F));

        ModelPartData cube_r34 = second.addChild("cube_r34", ModelPartBuilder.create().uv(0, 61).cuboid(-4.0F, -6.0F, 0.5F, 8.0F, 11.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-6.0215F, -22.0F, -1.6754F, -0.0873F, 1.0472F, 0.0F));

        ModelPartData map_interface = top.addChild("map_interface", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData cube_r35 = map_interface.addChild("cube_r35", ModelPartBuilder.create().uv(0, 3).cuboid(0.0F, 0.7F, -0.8F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-0.2636F, -16.8117F, -10.5184F, 0.9553F, 0.5236F, 0.6155F));

        ModelPartData cube_r36 = map_interface.addChild("cube_r36", ModelPartBuilder.create().uv(0, 3).cuboid(0.0F, 0.1F, -0.9F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-2.1929F, -16.4375F, -10.0442F, 0.9553F, 0.5236F, 0.6155F));

        ModelPartData cube_r37 = map_interface.addChild("cube_r37", ModelPartBuilder.create().uv(0, 0).cuboid(-1.0F, -5.9F, -3.9F, 2.0F, 1.0F, 2.0F, new Dilation(-0.1F))
                .uv(0, 0).cuboid(-3.0F, -5.9F, -3.9F, 2.0F, 1.0F, 2.0F, new Dilation(-0.1F))
                .uv(0, 35).cuboid(-4.0F, -5.0F, -5.5F, 8.0F, 0.0F, 5.0F, new Dilation(0.0F))
                .uv(18, 77).cuboid(-4.0F, -6.0F, -0.5F, 8.0F, 3.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -14.1697F, -3.4024F, 0.7854F, 0.0F, 0.0F));

        ModelPartData cube_r38 = map_interface.addChild("cube_r38", ModelPartBuilder.create().uv(38, 60).cuboid(-4.0F, -6.0F, -0.5F, 8.0F, 10.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -22.0F, -7.1519F, -0.0873F, 0.0F, 0.0F));

        ModelPartData void_cube = astral_map.addChild("void_cube", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData cube_r39 = void_cube.addChild("cube_r39", ModelPartBuilder.create().uv(0, 78).cuboid(-3.0F, -6.0F, 1.0F, 6.0F, 11.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -22.0F, -5.1519F, -0.0873F, 0.0F, 0.0F));

        ModelPartData cube_r40 = void_cube.addChild("cube_r40", ModelPartBuilder.create().uv(0, 78).cuboid(-3.0F, -6.0F, 1.0F, 6.0F, 11.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(6.0215F, -22.0F, -1.6754F, -0.0873F, -1.0472F, 0.0F));

        ModelPartData cube_r41 = void_cube.addChild("cube_r41", ModelPartBuilder.create().uv(0, 78).cuboid(-3.0F, -6.0F, -1.0F, 6.0F, 11.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(6.0215F, -22.0F, 5.2777F, 0.0873F, 1.0472F, 0.0F));

        ModelPartData cube_r42 = void_cube.addChild("cube_r42", ModelPartBuilder.create().uv(0, 78).cuboid(-3.0F, -6.0F, -1.0F, 6.0F, 11.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -22.0F, 8.7542F, 0.0873F, 0.0F, 0.0F));

        ModelPartData cube_r43 = void_cube.addChild("cube_r43", ModelPartBuilder.create().uv(0, 78).cuboid(-3.0F, -6.0F, -1.0F, 6.0F, 11.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(-6.0215F, -22.0F, 5.2777F, 0.0873F, -1.0472F, 0.0F));

        ModelPartData cube_r44 = void_cube.addChild("cube_r44", ModelPartBuilder.create().uv(0, 78).cuboid(-3.0F, -6.0F, 1.0F, 6.0F, 11.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(-6.0215F, -22.0F, -1.6754F, -0.0873F, 1.0472F, 0.0F));
        return TexturedModelData.of(modelData, 128, 128);
    }

    @Override
    public ModelPart getPart() {
        return astral_map;
    }

    @Override
    public void setAngles(Entity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw,
                          float headPitch) {
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red,
                       float green, float blue, float alpha) {
        astral_map.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
    }
}