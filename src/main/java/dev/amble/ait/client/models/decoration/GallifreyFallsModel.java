package dev.amble.ait.client.models.decoration;// Made with Blockbench 4.10.4
// Exported for Minecraft version 1.17+ for Yarn
// Paste this class into your mod and generate all required imports


import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;

public class GallifreyFallsModel extends SinglePartEntityModel {
    private final ModelPart painting;
    public GallifreyFallsModel(ModelPart root) {
        this.painting = root.getChild("painting");
    }
    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData painting = modelPartData.addChild("painting", ModelPartBuilder.create().uv(689, 65).cuboid(-39.0F, -64.0F, 49.0F, 80.0F, 64.0F, 0.0F, new Dilation(0.0F))
        .uv(322, 682).cuboid(-39.0F, -64.0F, 37.0F, 80.0F, 64.0F, 0.0F, new Dilation(0.0F))
        .uv(644, 667).cuboid(-39.0F, -64.0F, 22.0F, 80.0F, 64.0F, 0.0F, new Dilation(0.0F))
        .uv(482, 244).cuboid(-39.0F, -64.0F, 8.0F, 80.0F, 64.0F, 0.0F, new Dilation(0.0F))
        .uv(482, 163).cuboid(-39.0F, -64.0F, 5.0F, 80.0F, 64.0F, 0.0F, new Dilation(0.0F))
        .uv(643, 342).cuboid(-39.0F, -64.0F, -5.0F, 80.0F, 64.0F, 0.0F, new Dilation(0.0F))
        .uv(483, 617).cuboid(-39.0F, -64.0F, -18.0F, 80.0F, 64.0F, 0.0F, new Dilation(0.0F))
        .uv(0, 616).cuboid(-39.0F, -64.0F, -12.0F, 80.0F, 64.0F, 0.0F, new Dilation(0.0F))
        .uv(482, 406).cuboid(-39.0F, -64.0F, 1.0F, 80.0F, 64.0F, 0.0F, new Dilation(0.0F))
        .uv(322, 552).cuboid(-39.0F, -64.0F, -31.0F, 80.0F, 64.0F, 0.0F, new Dilation(0.0F))
        .uv(483, 487).cuboid(-39.0F, -64.0F, -49.0F, 80.0F, 64.0F, 0.0F, new Dilation(0.0F))
        .uv(241, 406).cuboid(-39.0F, -64.0F, -31.0F, 80.0F, 0.0F, 80.0F, new Dilation(0.0F))
        .uv(241, 325).cuboid(-119.0F, -64.0F, -31.0F, 80.0F, 0.0F, 80.0F, new Dilation(0.0F))
        .uv(0, 405).cuboid(41.0F, -64.0F, -31.0F, 80.0F, 0.0F, 80.0F, new Dilation(0.0F))
        .uv(0, 243).cuboid(-119.0F, -64.0F, -111.0F, 80.0F, 0.0F, 80.0F, new Dilation(0.0F))
        .uv(241, 244).cuboid(-39.0F, -64.0F, -111.0F, 80.0F, 0.0F, 80.0F, new Dilation(0.0F))
        .uv(0, 324).cuboid(41.0F, -64.0F, -111.0F, 80.0F, 0.0F, 80.0F, new Dilation(0.0F))
        .uv(0, 0).cuboid(-119.0F, 0.0F, -31.0F, 80.0F, 0.0F, 80.0F, new Dilation(0.0F))
        .uv(0, 81).cuboid(-39.0F, 0.0F, -31.0F, 80.0F, 0.0F, 80.0F, new Dilation(0.0F))
        .uv(0, 162).cuboid(41.0F, 0.0F, -31.0F, 80.0F, 0.0F, 80.0F, new Dilation(0.0F))
        .uv(241, 1).cuboid(41.0F, 0.0F, -111.0F, 80.0F, 0.0F, 80.0F, new Dilation(0.0F))
        .uv(241, 82).cuboid(-39.0F, 0.0F, -111.0F, 80.0F, 0.0F, 80.0F, new Dilation(0.0F))
        .uv(241, 163).cuboid(-119.0F, 0.0F, -111.0F, 80.0F, 0.0F, 80.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 40.0F, 103.0F));

        ModelPartData cube_r1 = painting.addChild("cube_r1", ModelPartBuilder.create().uv(643, 212).cuboid(-70.0F, -64.0F, -3.0F, 80.0F, 64.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(98.5675F, 0.0F, -100.5685F, 0.0F, 1.5708F, 0.0F));

        ModelPartData cube_r2 = painting.addChild("cube_r2", ModelPartBuilder.create().uv(643, 407).cuboid(-8.0F, -64.0F, -3.0F, 80.0F, 64.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(-96.5685F, 0.0F, -102.5685F, 0.0F, -1.5708F, 0.0F));

        ModelPartData cube_r3 = painting.addChild("cube_r3", ModelPartBuilder.create().uv(322, 487).cuboid(-39.0F, -64.0F, -1.0F, 80.0F, 64.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(-68.6985F, 0.0F, -77.2843F, 0.0F, -0.7854F, 0.0F));

        ModelPartData cube_r4 = painting.addChild("cube_r4", ModelPartBuilder.create().uv(161, 487).cuboid(-39.0F, -64.0F, -1.0F, 80.0F, 64.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(69.2843F, 0.0F, -75.8701F, 0.0F, 0.7854F, 0.0F));

        ModelPartData cube_r5 = painting.addChild("cube_r5", ModelPartBuilder.create().uv(161, 552).cuboid(-39.0F, -64.0F, -1.0F, 80.0F, 64.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(-68.6985F, 0.0F, -59.2843F, 0.0F, -0.7854F, 0.0F));

        ModelPartData cube_r6 = painting.addChild("cube_r6", ModelPartBuilder.create().uv(0, 551).cuboid(-39.0F, -64.0F, -1.0F, 80.0F, 64.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(69.2843F, 0.0F, -57.8701F, 0.0F, 0.7854F, 0.0F));

        ModelPartData cube_r7 = painting.addChild("cube_r7", ModelPartBuilder.create().uv(0, 486).cuboid(-39.0F, -64.0F, -1.0F, 80.0F, 64.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(-68.6985F, 0.0F, -27.2843F, 0.0F, -0.7854F, 0.0F));

        ModelPartData cube_r8 = painting.addChild("cube_r8", ModelPartBuilder.create().uv(482, 325).cuboid(-39.0F, -64.0F, -1.0F, 80.0F, 64.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(69.2843F, 0.0F, -25.8701F, 0.0F, 0.7854F, 0.0F));

        ModelPartData cube_r9 = painting.addChild("cube_r9", ModelPartBuilder.create().uv(161, 617).cuboid(-39.0F, -64.0F, -1.0F, 80.0F, 64.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(-68.6985F, 0.0F, -40.2843F, 0.0F, -0.7854F, 0.0F));

        ModelPartData cube_r10 = painting.addChild("cube_r10", ModelPartBuilder.create().uv(483, 552).cuboid(-39.0F, -64.0F, -1.0F, 80.0F, 64.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(69.2843F, 0.0F, -38.8701F, 0.0F, 0.7854F, 0.0F));

        ModelPartData cube_r11 = painting.addChild("cube_r11", ModelPartBuilder.create().uv(643, 147).cuboid(-39.0F, -64.0F, -1.0F, 80.0F, 64.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(-68.6985F, 0.0F, -46.2843F, 0.0F, -0.7854F, 0.0F));

        ModelPartData cube_r12 = painting.addChild("cube_r12", ModelPartBuilder.create().uv(322, 617).cuboid(-39.0F, -64.0F, -1.0F, 80.0F, 64.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(69.2843F, 0.0F, -44.8701F, 0.0F, 0.7854F, 0.0F));

        ModelPartData cube_r13 = painting.addChild("cube_r13", ModelPartBuilder.create().uv(644, 472).cuboid(-39.0F, -64.0F, -1.0F, 80.0F, 64.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(-68.6985F, 0.0F, -33.2843F, 0.0F, -0.7854F, 0.0F));

        ModelPartData cube_r14 = painting.addChild("cube_r14", ModelPartBuilder.create().uv(643, 277).cuboid(-39.0F, -64.0F, -1.0F, 80.0F, 64.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(69.2843F, 0.0F, -31.8701F, 0.0F, 0.7854F, 0.0F));

        ModelPartData cube_r15 = painting.addChild("cube_r15", ModelPartBuilder.create().uv(482, 82).cuboid(-39.0F, -64.0F, -1.0F, 103.0F, 64.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(98.5685F, 0.0F, -46.5685F, 0.0F, 1.5708F, 0.0F));

        ModelPartData cube_r16 = painting.addChild("cube_r16", ModelPartBuilder.create().uv(482, 0).cuboid(-61.0F, -64.0F, -1.0F, 103.0F, 64.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(-96.5682F, 0.0F, -48.5678F, 0.0F, -1.5708F, 0.0F));

        ModelPartData cube_r17 = painting.addChild("cube_r17", ModelPartBuilder.create().uv(644, 602).cuboid(-39.0F, -64.0F, -1.0F, 80.0F, 64.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(-68.6985F, 0.0F, -6.2843F, 0.0F, -0.7854F, 0.0F));

        ModelPartData cube_r18 = painting.addChild("cube_r18", ModelPartBuilder.create().uv(644, 537).cuboid(-39.0F, -64.0F, -1.0F, 80.0F, 64.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(69.2843F, 0.0F, -4.8701F, 0.0F, 0.7854F, 0.0F));

        ModelPartData cube_r19 = painting.addChild("cube_r19", ModelPartBuilder.create().uv(161, 682).cuboid(-39.0F, -64.0F, -1.0F, 80.0F, 64.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(-68.6985F, 0.0F, 8.7157F, 0.0F, -0.7854F, 0.0F));

        ModelPartData cube_r20 = painting.addChild("cube_r20", ModelPartBuilder.create().uv(0, 681).cuboid(-39.0F, -64.0F, -1.0F, 80.0F, 64.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(69.2843F, 0.0F, 10.1299F, 0.0F, 0.7854F, 0.0F));

        ModelPartData cube_r21 = painting.addChild("cube_r21", ModelPartBuilder.create().uv(689, 0).cuboid(-39.0F, -64.0F, -1.0F, 80.0F, 64.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(69.2843F, 0.0F, 22.1299F, 0.0F, 0.7854F, 0.0F));

        ModelPartData cube_r22 = painting.addChild("cube_r22", ModelPartBuilder.create().uv(483, 682).cuboid(-39.0F, -64.0F, -1.0F, 80.0F, 64.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(-68.6985F, 0.0F, 20.7157F, 0.0F, -0.7854F, 0.0F));

        ModelPartData clipframe = painting.addChild("clipframe", ModelPartBuilder.create().uv(644, 732).cuboid(-24.0F, -48.0F, -111.0F, 48.0F, 32.0F, 0.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));
        return TexturedModelData.of(modelData, 1024, 1024);
    }
    @Override
    public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
        painting.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
    }

    @Override
    public ModelPart getPart() {
        return painting;
    }

    @Override
    public void setAngles(Entity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {

    }
}