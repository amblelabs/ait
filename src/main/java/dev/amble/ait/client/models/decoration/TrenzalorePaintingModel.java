package dev.amble.ait.client.models.decoration;// Made with Blockbench 4.10.4
// Exported for Minecraft version 1.17+ for Yarn
// Paste this class into your mod and generate all required imports


import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;

public class TrenzalorePaintingModel extends SinglePartEntityModel {
    private final ModelPart painting;
    public TrenzalorePaintingModel(ModelPart root) {
        this.painting = root.getChild("painting");
    }
    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData painting = modelPartData.addChild("painting", ModelPartBuilder.create().uv(483, 551).cuboid(-15.25F, -22.25F, 41.5F, 80.0F, 64.0F, 0.0F, new Dilation(0.001F))
                .uv(0, 0).cuboid(-95.25F, -22.25F, -38.5F, 80.0F, 0.0F, 80.0F, new Dilation(0.001F))
                .uv(0, 162).cuboid(64.75F, -22.25F, -38.5F, 80.0F, 0.0F, 80.0F, new Dilation(0.001F))
                .uv(0, 81).cuboid(-15.25F, -22.25F, -38.5F, 80.0F, 0.0F, 80.0F, new Dilation(0.001F))
                .uv(321, 81).cuboid(64.75F, -22.25F, -118.5F, 80.0F, 0.0F, 80.0F, new Dilation(0.001F))
                .uv(321, 0).cuboid(-15.25F, -22.25F, -118.5F, 80.0F, 0.0F, 80.0F, new Dilation(0.001F))
                .uv(0, 243).cuboid(-95.25F, -22.25F, -118.5F, 80.0F, 0.0F, 80.0F, new Dilation(0.001F))
                .uv(321, 405).cuboid(-95.25F, 41.75F, -118.5F, 80.0F, 0.0F, 80.0F, new Dilation(0.001F))
                .uv(0, 405).cuboid(-15.25F, 41.75F, -118.5F, 80.0F, 0.0F, 80.0F, new Dilation(0.001F))
                .uv(321, 324).cuboid(64.75F, 41.75F, -118.5F, 80.0F, 0.0F, 80.0F, new Dilation(0.001F))
                .uv(0, 324).cuboid(64.75F, 41.75F, -38.5F, 80.0F, 0.0F, 80.0F, new Dilation(0.001F))
                .uv(321, 243).cuboid(-15.25F, 41.75F, -38.5F, 80.0F, 0.0F, 80.0F, new Dilation(0.001F))
                .uv(321, 162).cuboid(-95.25F, 41.75F, -38.5F, 80.0F, 0.0F, 80.0F, new Dilation(0.001F))
                .uv(483, 616).cuboid(-15.25F, -22.25F, 29.5F, 80.0F, 64.0F, 0.0F, new Dilation(0.001F))
                .uv(642, 130).cuboid(-15.25F, -21.05F, 14.5F, 80.0F, 64.0F, 0.0F, new Dilation(0.001F))
                .uv(642, 260).cuboid(-15.25F, -22.25F, -12.5F, 80.0F, 64.0F, 0.0F, new Dilation(0.001F))
                .uv(644, 551).cuboid(-43.25F, -22.25F, -13.5F, 80.0F, 64.0F, 0.0F, new Dilation(0.001F))
                .uv(161, 681).cuboid(-15.25F, -26.25F, -19.5F, 80.0F, 64.0F, 0.0F, new Dilation(0.001F))
                .uv(322, 746).cuboid(-15.25F, -22.25F, -38.5F, 80.0F, 64.0F, 0.0F, new Dilation(0.001F))
                .uv(803, 0).cuboid(-15.25F, -22.25F, -56.5F, 80.0F, 64.0F, 0.0F, new Dilation(0.001F))
                .uv(644, 681).cuboid(-15.25F, -30.25F, -6.5F, 80.0F, 64.0F, 0.0F, new Dilation(0.001F)), ModelTransform.pivot(-23.75F, -17.75F, 7.5F));

        ModelPartData cube_r1 = painting.addChild("cube_r1", ModelPartBuilder.create().uv(483, 681).cuboid(-39.0F, -64.0F, -1.0F, 80.0F, 64.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(93.0343F, 33.75F, -33.3701F, 0.0F, 0.7854F, 0.0F));

        ModelPartData cube_r2 = painting.addChild("cube_r2", ModelPartBuilder.create().uv(736, 455).cuboid(-39.0F, -64.0F, -1.0F, 80.0F, 64.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(-44.9485F, 33.75F, -34.7843F, 0.0F, -0.7854F, 0.0F));

        ModelPartData cube_r3 = painting.addChild("cube_r3", ModelPartBuilder.create().uv(483, 746).cuboid(-39.0F, -64.0F, -1.0F, 80.0F, 64.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(93.0343F, 41.75F, -83.3701F, 0.0F, 0.7854F, 0.0F));

        ModelPartData cube_r4 = painting.addChild("cube_r4", ModelPartBuilder.create().uv(644, 746).cuboid(-39.0F, -64.0F, -1.0F, 80.0F, 64.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(-44.9485F, 41.75F, -84.7843F, 0.0F, -0.7854F, 0.0F));

        ModelPartData cube_r5 = painting.addChild("cube_r5", ModelPartBuilder.create().uv(0, 746).cuboid(-39.0F, -64.0F, -1.0F, 80.0F, 64.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(93.0343F, 41.75F, -65.3701F, 0.0F, 0.7854F, 0.0F));

        ModelPartData cube_r6 = painting.addChild("cube_r6", ModelPartBuilder.create().uv(161, 746).cuboid(-39.0F, -64.0F, -1.0F, 80.0F, 64.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(-44.9485F, 41.75F, -66.7843F, 0.0F, -0.7854F, 0.0F));

        ModelPartData cube_r7 = painting.addChild("cube_r7", ModelPartBuilder.create().uv(0, 681).cuboid(-39.0F, -64.0F, -1.0F, 80.0F, 64.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(93.0343F, 37.75F, -46.3701F, 0.0F, 0.7854F, 0.0F));

        ModelPartData cube_r8 = painting.addChild("cube_r8", ModelPartBuilder.create().uv(322, 681).cuboid(-39.0F, -64.0F, -1.0F, 80.0F, 64.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(-44.9485F, 37.75F, -47.7843F, 0.0F, -0.7854F, 0.0F));

        ModelPartData cube_r9 = painting.addChild("cube_r9", ModelPartBuilder.create().uv(642, 390).cuboid(-39.0F, -64.0F, -16.0F, 80.0F, 64.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(93.0343F, 41.75F, -52.3701F, 0.0F, 0.7854F, 0.0F));

        ModelPartData cube_r10 = painting.addChild("cube_r10", ModelPartBuilder.create().uv(644, 616).cuboid(-39.0F, -64.0F, 3.0F, 80.0F, 64.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(-44.9485F, 41.75F, -53.7843F, 0.0F, -0.7854F, 0.0F));

        ModelPartData cube_r11 = painting.addChild("cube_r11", ModelPartBuilder.create().uv(803, 130).cuboid(-70.0F, -64.0F, -3.0F, 80.0F, 64.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(122.3175F, 41.75F, -108.0685F, 0.0F, 1.5708F, 0.0F));

        ModelPartData cube_r12 = painting.addChild("cube_r12", ModelPartBuilder.create().uv(642, 195).cuboid(-39.0F, -64.0F, -1.0F, 80.0F, 64.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(93.0343F, 41.75F, -39.3701F, 0.0F, 0.7854F, 0.0F));

        ModelPartData cube_r13 = painting.addChild("cube_r13", ModelPartBuilder.create().uv(803, 65).cuboid(-8.0F, -64.0F, -3.0F, 80.0F, 64.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(-72.8185F, 41.75F, -110.0685F, 0.0F, -1.5708F, 0.0F));

        ModelPartData cube_r14 = painting.addChild("cube_r14", ModelPartBuilder.create().uv(642, 325).cuboid(-39.0F, -64.0F, -1.0F, 80.0F, 64.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(-44.9485F, 41.75F, -40.7843F, 0.0F, -0.7854F, 0.0F));

        ModelPartData cube_r15 = painting.addChild("cube_r15", ModelPartBuilder.create().uv(642, 0).cuboid(-39.0F, -64.0F, -1.0F, 80.0F, 64.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(93.0343F, 42.95F, -12.3701F, 0.0F, 0.7854F, 0.0F));

        ModelPartData cube_r16 = painting.addChild("cube_r16", ModelPartBuilder.create().uv(642, 65).cuboid(-39.0F, -64.0F, -1.0F, 80.0F, 64.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(-44.9485F, 42.95F, -13.7843F, 0.0F, -0.7854F, 0.0F));

        ModelPartData cube_r17 = painting.addChild("cube_r17", ModelPartBuilder.create().uv(161, 616).cuboid(-39.0F, -64.0F, -1.0F, 80.0F, 64.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(93.0343F, 41.75F, 2.6299F, 0.0F, 0.7854F, 0.0F));

        ModelPartData cube_r18 = painting.addChild("cube_r18", ModelPartBuilder.create().uv(322, 616).cuboid(-39.0F, -64.0F, -1.0F, 80.0F, 64.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(-44.9485F, 41.75F, 1.2157F, 0.0F, -0.7854F, 0.0F));

        ModelPartData cube_r19 = painting.addChild("cube_r19", ModelPartBuilder.create().uv(0, 486).cuboid(-61.0F, -64.0F, -1.0F, 103.0F, 64.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(-72.8183F, 41.75F, -56.0678F, 0.0F, -1.5708F, 0.0F));

        ModelPartData cube_r20 = painting.addChild("cube_r20", ModelPartBuilder.create().uv(575, 486).cuboid(-39.0F, -64.0F, -1.0F, 80.0F, 64.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(-44.9485F, 41.75F, 13.2157F, 0.0F, -0.7854F, 0.0F));

        ModelPartData cube_r21 = painting.addChild("cube_r21", ModelPartBuilder.create().uv(207, 486).cuboid(-39.0F, -64.0F, -1.0F, 103.0F, 64.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(122.3185F, 41.75F, -54.0685F, 0.0F, 1.5708F, 0.0F));

        ModelPartData cube_r22 = painting.addChild("cube_r22", ModelPartBuilder.create().uv(0, 616).cuboid(-39.0F, -64.0F, -1.0F, 80.0F, 64.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(93.0343F, 41.75F, 14.6299F, 0.0F, 0.7854F, 0.0F));

        ModelPartData tardis = painting.addChild("tardis", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData cube_r23 = tardis.addChild("cube_r23", ModelPartBuilder.create().uv(322, 551).cuboid(-45.5F, -32.0F, -4.0F, 80.0F, 64.0F, 0.0F, new Dilation(0.001F))
                .uv(161, 551).cuboid(-45.5F, -32.0F, -7.0F, 80.0F, 64.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.7854F, 0.0F));

        ModelPartData cube_r24 = tardis.addChild("cube_r24", ModelPartBuilder.create().uv(0, 551).cuboid(-40.0F, -32.0F, 1.5F, 80.0F, 64.0F, 0.0F, new Dilation(0.001F))
                .uv(414, 486).cuboid(-40.0F, -32.0F, -1.5F, 80.0F, 64.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -0.7854F, 0.0F));

        ModelPartData roof = tardis.addChild("roof", ModelPartBuilder.create(), ModelTransform.pivot(-32.25F, 50.75F, 42.5F));

        ModelPartData cube_r25 = roof.addChild("cube_r25", ModelPartBuilder.create().uv(0, 1010).cuboid(-13.0F, -61.75F, -53.0F, 12.0F, 2.0F, 12.0F, new Dilation(0.001F))
                .uv(9, 925).cuboid(-8.5F, -64.75F, -48.5F, 3.0F, 3.0F, 3.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -0.7854F, 0.0F));

        ModelPartData plane = painting.addChild("plane", ModelPartBuilder.create().uv(803, 195).cuboid(-24.0F, -48.0F, -111.0F, 48.0F, 32.0F, 0.0F, new Dilation(0.001F)), ModelTransform.pivot(23.75F, 41.75F, -7.5F));
        return TexturedModelData.of(modelData, 1024, 1024);
    }
    @Override
    public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
        matrices.push();
        matrices.translate(0.5, 1, 5.5);
        painting.getChild("plane").visible = false;
        painting.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
        matrices.pop();
    }

    @Override
    public ModelPart getPart() {
        return painting;
    }

    @Override
    public void setAngles(Entity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {

    }
}