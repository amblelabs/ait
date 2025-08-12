package dev.amble.ait.client.models.decoration;

import net.minecraft.client.model.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.RotationAxis;

public class CopperRingsModel extends Model {

    private final ModelPart rings3;

    public CopperRingsModel(ModelPart root) {
        super(RenderLayer::getEntityCutout);
        this.rings3 = root.getChild("rings3");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData rings3 = modelPartData.addChild("rings3", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 34.5F, 0.0F));

        ModelPartData ring6 = rings3.addChild("ring6", ModelPartBuilder.create().uv(36, 28).cuboid(-18.75F, -26.9F, -5.0F, 2.0F, 44.0F, 10.0F, new Dilation(0.001F)), ModelTransform.pivot(0.0F, 0.9F, 0.0F));

        ModelPartData cube_r1 = ring6.addChild("cube_r1", ModelPartBuilder.create().uv(36, 28).cuboid(-18.75F, -26.9F, -5.0F, 2.0F, 44.0F, 10.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData cube_r2 = ring6.addChild("cube_r2", ModelPartBuilder.create().uv(36, 28).cuboid(-18.75F, -26.9F, -5.0F, 2.0F, 44.0F, 10.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -0.5236F, 0.0F));

        ModelPartData cube_r3 = ring6.addChild("cube_r3", ModelPartBuilder.create().uv(36, 28).cuboid(-18.75F, -26.9F, -5.0F, 2.0F, 44.0F, 10.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.5236F, 0.0F));

        ModelPartData cube_r4 = ring6.addChild("cube_r4", ModelPartBuilder.create().uv(36, 28).cuboid(-18.75F, -26.9F, -5.0F, 2.0F, 44.0F, 10.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData cube_r5 = ring6.addChild("cube_r5", ModelPartBuilder.create().uv(36, 28).cuboid(-18.75F, -26.9F, -5.0F, 2.0F, 44.0F, 10.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.5708F, 0.0F));

        ModelPartData cube_r6 = ring6.addChild("cube_r6", ModelPartBuilder.create().uv(36, 28).cuboid(-18.75F, -26.9F, -5.0F, 2.0F, 44.0F, 10.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 0.0F, 0.0F, -3.1416F, 1.0472F, -3.1416F));

        ModelPartData cube_r7 = ring6.addChild("cube_r7", ModelPartBuilder.create().uv(36, 28).cuboid(-18.75F, -26.9F, -5.0F, 2.0F, 44.0F, 10.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 0.0F, 0.0F, -3.1416F, 0.5236F, -3.1416F));

        ModelPartData cube_r8 = ring6.addChild("cube_r8", ModelPartBuilder.create().uv(36, 28).cuboid(-18.75F, -26.9F, -5.0F, 2.0F, 44.0F, 10.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 0.0F, 0.0F, -3.1416F, 0.0F, 3.1416F));

        ModelPartData cube_r9 = ring6.addChild("cube_r9", ModelPartBuilder.create().uv(36, 28).cuboid(-18.75F, -26.9F, -5.0F, 2.0F, 44.0F, 10.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -2.618F, 0.0F));

        ModelPartData cube_r10 = ring6.addChild("cube_r10", ModelPartBuilder.create().uv(36, 28).cuboid(-18.75F, -26.9F, -5.0F, 2.0F, 44.0F, 10.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -2.0944F, 0.0F));

        ModelPartData cube_r11 = ring6.addChild("cube_r11", ModelPartBuilder.create().uv(36, 28).cuboid(-18.75F, -26.9F, -5.0F, 2.0F, 44.0F, 10.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.5708F, 0.0F));

        ModelPartData ring7 = rings3.addChild("ring7", ModelPartBuilder.create().uv(29, 21).cuboid(-31.75F, -30.4F, -8.5F, 2.0F, 45.0F, 17.0F, new Dilation(0.001F)), ModelTransform.pivot(0.0F, 4.4F, 0.0F));

        ModelPartData cube_r12 = ring7.addChild("cube_r12", ModelPartBuilder.create().uv(29, 21).cuboid(-31.75F, -30.4F, -8.5F, 2.0F, 45.0F, 17.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData cube_r13 = ring7.addChild("cube_r13", ModelPartBuilder.create().uv(29, 21).cuboid(-31.75F, -30.4F, -8.5F, 2.0F, 45.0F, 17.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -0.5236F, 0.0F));

        ModelPartData cube_r14 = ring7.addChild("cube_r14", ModelPartBuilder.create().uv(29, 21).cuboid(-31.75F, -30.4F, -8.5F, 2.0F, 45.0F, 17.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.5236F, 0.0F));

        ModelPartData cube_r15 = ring7.addChild("cube_r15", ModelPartBuilder.create().uv(29, 21).cuboid(-31.75F, -30.4F, -8.5F, 2.0F, 45.0F, 17.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData cube_r16 = ring7.addChild("cube_r16", ModelPartBuilder.create().uv(29, 21).cuboid(-31.75F, -30.4F, -8.5F, 2.0F, 45.0F, 17.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.5708F, 0.0F));

        ModelPartData cube_r17 = ring7.addChild("cube_r17", ModelPartBuilder.create().uv(29, 21).cuboid(-31.75F, -30.4F, -8.5F, 2.0F, 45.0F, 17.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 0.0F, 0.0F, -3.1416F, 1.0472F, -3.1416F));

        ModelPartData cube_r18 = ring7.addChild("cube_r18", ModelPartBuilder.create().uv(29, 21).cuboid(-31.75F, -30.4F, -8.5F, 2.0F, 45.0F, 17.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 0.0F, 0.0F, -3.1416F, 0.5236F, -3.1416F));

        ModelPartData cube_r19 = ring7.addChild("cube_r19", ModelPartBuilder.create().uv(29, 21).cuboid(-31.75F, -30.4F, -8.5F, 2.0F, 45.0F, 17.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 0.0F, 0.0F, -3.1416F, 0.0F, 3.1416F));

        ModelPartData cube_r20 = ring7.addChild("cube_r20", ModelPartBuilder.create().uv(29, 21).cuboid(-31.75F, -30.4F, -8.5F, 2.0F, 45.0F, 17.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -2.618F, 0.0F));

        ModelPartData cube_r21 = ring7.addChild("cube_r21", ModelPartBuilder.create().uv(29, 21).cuboid(-31.75F, -30.4F, -8.5F, 2.0F, 45.0F, 17.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -2.0944F, 0.0F));

        ModelPartData cube_r22 = ring7.addChild("cube_r22", ModelPartBuilder.create().uv(29, 21).cuboid(-31.75F, -30.4F, -8.5F, 2.0F, 45.0F, 17.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.5708F, 0.0F));

        ModelPartData ring8 = rings3.addChild("ring8", ModelPartBuilder.create().uv(23, 15).cuboid(-43.0F, -25.4F, -11.5F, 2.0F, 46.0F, 23.0F, new Dilation(0.001F)), ModelTransform.pivot(0.0F, -0.6F, 0.0F));

        ModelPartData cube_r23 = ring8.addChild("cube_r23", ModelPartBuilder.create().uv(23, 15).cuboid(-43.0F, -32.4F, -11.5F, 2.0F, 46.0F, 23.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 7.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData cube_r24 = ring8.addChild("cube_r24", ModelPartBuilder.create().uv(23, 15).cuboid(-43.0F, -32.4F, -11.5F, 2.0F, 46.0F, 23.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 7.0F, 0.0F, 0.0F, -0.5236F, 0.0F));

        ModelPartData cube_r25 = ring8.addChild("cube_r25", ModelPartBuilder.create().uv(23, 15).cuboid(-43.0F, -32.4F, -11.5F, 2.0F, 46.0F, 23.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 7.0F, 0.0F, 0.0F, 0.5236F, 0.0F));

        ModelPartData cube_r26 = ring8.addChild("cube_r26", ModelPartBuilder.create().uv(23, 15).cuboid(-43.0F, -32.4F, -11.5F, 2.0F, 46.0F, 23.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 7.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData cube_r27 = ring8.addChild("cube_r27", ModelPartBuilder.create().uv(23, 15).cuboid(-43.0F, -32.4F, -11.5F, 2.0F, 46.0F, 23.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 7.0F, 0.0F, 0.0F, 1.5708F, 0.0F));

        ModelPartData cube_r28 = ring8.addChild("cube_r28", ModelPartBuilder.create().uv(23, 15).cuboid(-43.0F, -32.4F, -11.5F, 2.0F, 46.0F, 23.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 7.0F, 0.0F, -3.1416F, 1.0472F, -3.1416F));

        ModelPartData cube_r29 = ring8.addChild("cube_r29", ModelPartBuilder.create().uv(23, 15).cuboid(-43.0F, -32.4F, -11.5F, 2.0F, 46.0F, 23.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 7.0F, 0.0F, -3.1416F, 0.5236F, -3.1416F));

        ModelPartData cube_r30 = ring8.addChild("cube_r30", ModelPartBuilder.create().uv(23, 15).cuboid(-43.0F, -32.4F, -11.5F, 2.0F, 46.0F, 23.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 7.0F, 0.0F, -3.1416F, 0.0F, 3.1416F));

        ModelPartData cube_r31 = ring8.addChild("cube_r31", ModelPartBuilder.create().uv(23, 15).cuboid(-43.0F, -32.4F, -11.5F, 2.0F, 46.0F, 23.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 7.0F, 0.0F, 0.0F, -2.618F, 0.0F));

        ModelPartData cube_r32 = ring8.addChild("cube_r32", ModelPartBuilder.create().uv(23, 15).cuboid(-43.0F, -32.4F, -11.5F, 2.0F, 46.0F, 23.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 7.0F, 0.0F, 0.0F, -2.0944F, 0.0F));

        ModelPartData cube_r33 = ring8.addChild("cube_r33", ModelPartBuilder.create().uv(23, 15).cuboid(-43.0F, -32.4F, -11.5F, 2.0F, 46.0F, 23.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 7.0F, 0.0F, 0.0F, -1.5708F, 0.0F));

        ModelPartData ring9 = rings3.addChild("ring9", ModelPartBuilder.create().uv(16, 8).cuboid(-55.55F, -27.4F, -15.0F, 2.0F, 47.0F, 30.0F, new Dilation(0.001F)), ModelTransform.pivot(0.0F, 1.4F, 0.0F));

        ModelPartData cube_r34 = ring9.addChild("cube_r34", ModelPartBuilder.create().uv(16, 8).cuboid(-55.55F, -34.4F, -15.0F, 2.0F, 47.0F, 30.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 7.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData cube_r35 = ring9.addChild("cube_r35", ModelPartBuilder.create().uv(16, 8).cuboid(-55.55F, -34.4F, -15.0F, 2.0F, 47.0F, 30.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 7.0F, 0.0F, 0.0F, -0.5236F, 0.0F));

        ModelPartData cube_r36 = ring9.addChild("cube_r36", ModelPartBuilder.create().uv(16, 8).cuboid(-55.55F, -34.4F, -15.0F, 2.0F, 47.0F, 30.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 7.0F, 0.0F, 0.0F, 0.5236F, 0.0F));

        ModelPartData cube_r37 = ring9.addChild("cube_r37", ModelPartBuilder.create().uv(16, 8).cuboid(-55.55F, -34.4F, -15.0F, 2.0F, 47.0F, 30.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 7.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData cube_r38 = ring9.addChild("cube_r38", ModelPartBuilder.create().uv(16, 8).cuboid(-55.55F, -34.4F, -15.0F, 2.0F, 47.0F, 30.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 7.0F, 0.0F, 0.0F, 1.5708F, 0.0F));

        ModelPartData cube_r39 = ring9.addChild("cube_r39", ModelPartBuilder.create().uv(16, 8).cuboid(-55.55F, -34.4F, -15.0F, 2.0F, 47.0F, 30.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 7.0F, 0.0F, -3.1416F, 1.0472F, -3.1416F));

        ModelPartData cube_r40 = ring9.addChild("cube_r40", ModelPartBuilder.create().uv(16, 8).cuboid(-55.55F, -34.4F, -15.0F, 2.0F, 47.0F, 30.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 7.0F, 0.0F, -3.1416F, 0.5236F, -3.1416F));

        ModelPartData cube_r41 = ring9.addChild("cube_r41", ModelPartBuilder.create().uv(16, 8).cuboid(-55.55F, -34.4F, -15.0F, 2.0F, 47.0F, 30.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 7.0F, 0.0F, -3.1416F, 0.0F, 3.1416F));

        ModelPartData cube_r42 = ring9.addChild("cube_r42", ModelPartBuilder.create().uv(16, 8).cuboid(-55.55F, -34.4F, -15.0F, 2.0F, 47.0F, 30.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 7.0F, 0.0F, 0.0F, -2.618F, 0.0F));

        ModelPartData cube_r43 = ring9.addChild("cube_r43", ModelPartBuilder.create().uv(16, 8).cuboid(-55.55F, -34.4F, -15.0F, 2.0F, 47.0F, 30.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 7.0F, 0.0F, 0.0F, -2.0944F, 0.0F));

        ModelPartData cube_r44 = ring9.addChild("cube_r44", ModelPartBuilder.create().uv(16, 8).cuboid(-55.55F, -34.4F, -15.0F, 2.0F, 47.0F, 30.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 7.0F, 0.0F, 0.0F, -1.5708F, 0.0F));

        ModelPartData ring10 = rings3.addChild("ring10", ModelPartBuilder.create().uv(10, 2).cuboid(-67.0F, -29.4F, -18.0F, 2.0F, 48.0F, 36.0F, new Dilation(0.001F)), ModelTransform.pivot(0.0F, 3.4F, 0.0F));

        ModelPartData cube_r45 = ring10.addChild("cube_r45", ModelPartBuilder.create().uv(10, 2).cuboid(-67.0F, -36.4F, -18.0F, 2.0F, 48.0F, 36.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 7.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData cube_r46 = ring10.addChild("cube_r46", ModelPartBuilder.create().uv(10, 2).cuboid(-67.0F, -36.4F, -18.0F, 2.0F, 48.0F, 36.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 7.0F, 0.0F, 0.0F, -0.5236F, 0.0F));

        ModelPartData cube_r47 = ring10.addChild("cube_r47", ModelPartBuilder.create().uv(10, 2).cuboid(-67.0F, -36.4F, -18.0F, 2.0F, 48.0F, 36.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 7.0F, 0.0F, 0.0F, 0.5236F, 0.0F));

        ModelPartData cube_r48 = ring10.addChild("cube_r48", ModelPartBuilder.create().uv(10, 2).cuboid(-67.0F, -36.4F, -18.0F, 2.0F, 48.0F, 36.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 7.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData cube_r49 = ring10.addChild("cube_r49", ModelPartBuilder.create().uv(10, 2).cuboid(-67.0F, -36.4F, -18.0F, 2.0F, 48.0F, 36.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 7.0F, 0.0F, 0.0F, 1.5708F, 0.0F));

        ModelPartData cube_r50 = ring10.addChild("cube_r50", ModelPartBuilder.create().uv(10, 2).cuboid(-67.0F, -36.4F, -18.0F, 2.0F, 48.0F, 36.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 7.0F, 0.0F, -3.1416F, 1.0472F, -3.1416F));

        ModelPartData cube_r51 = ring10.addChild("cube_r51", ModelPartBuilder.create().uv(10, 2).cuboid(-67.0F, -36.4F, -18.0F, 2.0F, 48.0F, 36.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 7.0F, 0.0F, -3.1416F, 0.5236F, -3.1416F));

        ModelPartData cube_r52 = ring10.addChild("cube_r52", ModelPartBuilder.create().uv(10, 2).cuboid(-67.0F, -36.4F, -18.0F, 2.0F, 48.0F, 36.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 7.0F, 0.0F, -3.1416F, 0.0F, 3.1416F));

        ModelPartData cube_r53 = ring10.addChild("cube_r53", ModelPartBuilder.create().uv(10, 2).cuboid(-67.0F, -36.4F, -18.0F, 2.0F, 48.0F, 36.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 7.0F, 0.0F, 0.0F, -2.618F, 0.0F));

        ModelPartData cube_r54 = ring10.addChild("cube_r54", ModelPartBuilder.create().uv(10, 2).cuboid(-67.0F, -36.4F, -18.0F, 2.0F, 48.0F, 36.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 7.0F, 0.0F, 0.0F, -2.0944F, 0.0F));

        ModelPartData cube_r55 = ring10.addChild("cube_r55", ModelPartBuilder.create().uv(10, 2).cuboid(-67.0F, -36.4F, -18.0F, 2.0F, 48.0F, 36.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 7.0F, 0.0F, 0.0F, -1.5708F, 0.0F));
        return TexturedModelData.of(modelData, 1024, 1024);
    }

    public void setAngles(MatrixStack matrices, ModelTransformationMode renderMode, boolean left) {
        if (renderMode == ModelTransformationMode.FIXED)
            return;
        matrices.translate(0.5, -0.7f, -0.5);
        matrices.scale(0.1f, 0.1f, 0.1f);

        if (renderMode == ModelTransformationMode.GUI) {
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(22.5f));
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(45f));
            matrices.translate(0, 0f, 0);
            matrices.scale(1f, 1f, 1f);
        }

        if (renderMode == ModelTransformationMode.HEAD) {
            matrices.translate(0, -0.725f, 0);
            matrices.scale(0.1f, 10f, 0.1f);
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180));
        }

    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
        rings3.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
    }
}