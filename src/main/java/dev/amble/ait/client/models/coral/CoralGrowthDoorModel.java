package dev.amble.ait.client.models.coral;

import dev.amble.ait.api.tardis.link.v2.block.AbstractLinkableBlockEntity;
import dev.amble.ait.client.models.doors.DoorModel;
import dev.amble.ait.client.tardis.ClientTardis;
import dev.amble.ait.core.tardis.handler.DoorHandler;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.animation.Animation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.RotationAxis;

public class CoralGrowthDoorModel extends DoorModel {
    private final ModelPart coral;

    public CoralGrowthDoorModel(ModelPart root) {
        this.coral = root.getChild("coral");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData coral = modelPartData.addChild("coral", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 24.0F, -12.0F));

        ModelPartData seven = coral.addChild("seven", ModelPartBuilder.create().uv(150, 386).cuboid(-5.0F, -35.0F, -6.0F, 10.0F, 34.0F, 0.0F, new Dilation(0.001F))
                .uv(229, 393).cuboid(-5.0F, -35.0F, -12.0F, 10.0F, 0.0F, 6.0F, new Dilation(0.001F))
                .uv(229, 386).cuboid(-5.0F, -1.0F, -12.0F, 10.0F, 0.0F, 6.0F, new Dilation(0.001F))
                .uv(205, 386).cuboid(-5.0F, -35.0F, -12.0F, 0.0F, 34.0F, 6.0F, new Dilation(0.001F))
                .uv(192, 386).cuboid(5.0F, -35.0F, -12.0F, 0.0F, 34.0F, 6.0F, new Dilation(0.001F))
                .uv(97, 0).cuboid(-12.0F, -39.0F, -12.0F, 24.0F, 0.0F, 24.0F, new Dilation(0.001F))
                .uv(0, 0).cuboid(-12.0F, 0.0F, -12.0F, 24.0F, 0.0F, 24.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 3.1416F, 0.0F));

        ModelPartData cube_r1 = seven.addChild("cube_r1", ModelPartBuilder.create().uv(455, 103).cuboid(-8.0F, -4.0F, -12.0F, 16.0F, 0.0F, 16.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, -19.0F, 9.0F, -0.7854F, 0.0F, 0.0F));

        ModelPartData cube_r2 = seven.addChild("cube_r2", ModelPartBuilder.create().uv(390, 103).cuboid(-8.0F, -4.0F, -12.0F, 16.0F, 0.0F, 16.0F, new Dilation(0.001F)), ModelTransform.of(-9.0F, -19.0F, 0.0F, 0.0F, -1.5708F, -0.7854F));

        ModelPartData cube_r3 = seven.addChild("cube_r3", ModelPartBuilder.create().uv(260, 103).cuboid(-8.0F, -4.0F, -12.0F, 16.0F, 0.0F, 16.0F, new Dilation(0.001F)), ModelTransform.of(9.0F, -7.0F, 0.0F, 0.0F, 1.5708F, 0.7854F));

        ModelPartData cube_r4 = seven.addChild("cube_r4", ModelPartBuilder.create().uv(195, 103).cuboid(-8.0F, -4.0F, -12.0F, 16.0F, 0.0F, 16.0F, new Dilation(0.001F)), ModelTransform.of(-9.0F, -7.0F, 0.0F, 0.0F, -1.5708F, -0.7854F));

        ModelPartData cube_r5 = seven.addChild("cube_r5", ModelPartBuilder.create().uv(130, 103).cuboid(-8.0F, -4.0F, -12.0F, 16.0F, 0.0F, 16.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, -7.0F, 9.0F, -0.7854F, 0.0F, 0.0F));

        ModelPartData cube_r6 = seven.addChild("cube_r6", ModelPartBuilder.create().uv(325, 103).cuboid(-8.0F, -4.0F, -12.0F, 16.0F, 0.0F, 16.0F, new Dilation(0.001F)), ModelTransform.of(9.0F, -19.0F, 0.0F, 0.0F, 1.5708F, 0.7854F));

        ModelPartData cube_r7 = seven.addChild("cube_r7", ModelPartBuilder.create().uv(520, 103).cuboid(-8.0F, -4.0F, -12.0F, 16.0F, 0.0F, 16.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, -31.0F, 9.0F, -0.7854F, 0.0F, 0.0F));

        ModelPartData cube_r8 = seven.addChild("cube_r8", ModelPartBuilder.create().uv(0, 120).cuboid(-8.0F, -4.0F, -12.0F, 16.0F, 0.0F, 16.0F, new Dilation(0.001F)), ModelTransform.of(-9.0F, -31.0F, 0.0F, 0.0F, -1.5708F, -0.7854F));

        ModelPartData cube_r9 = seven.addChild("cube_r9", ModelPartBuilder.create().uv(65, 120).cuboid(-8.0F, -4.0F, -12.0F, 16.0F, 0.0F, 16.0F, new Dilation(0.001F)), ModelTransform.of(9.0F, -31.0F, 0.0F, 0.0F, 1.5708F, 0.7854F));

        ModelPartData cube_r10 = seven.addChild("cube_r10", ModelPartBuilder.create().uv(130, 120).cuboid(-8.0F, -4.0F, -12.0F, 16.0F, 0.0F, 16.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, -31.0F, -9.0F, 2.3562F, 0.0F, -3.1416F));

        ModelPartData cube_r11 = seven.addChild("cube_r11", ModelPartBuilder.create().uv(65, 103).cuboid(-8.0F, -4.0F, -12.0F, 16.0F, 0.0F, 16.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 3.0F, 12.0F, -0.2618F, 0.0F, 0.0F));

        ModelPartData cube_r12 = seven.addChild("cube_r12", ModelPartBuilder.create().uv(0, 103).cuboid(-8.0F, -4.0F, -12.0F, 16.0F, 0.0F, 16.0F, new Dilation(0.001F)), ModelTransform.of(12.0F, 3.0F, 0.0F, 0.0F, 1.5708F, 0.2618F));

        ModelPartData cube_r13 = seven.addChild("cube_r13", ModelPartBuilder.create().uv(520, 86).cuboid(-8.0F, -4.0F, -12.0F, 16.0F, 0.0F, 16.0F, new Dilation(0.001F)), ModelTransform.of(-12.0F, 3.0F, 0.0F, 0.0F, -1.5708F, -0.2618F));

        ModelPartData cube_r14 = seven.addChild("cube_r14", ModelPartBuilder.create().uv(455, 86).cuboid(-8.0F, -4.0F, -12.0F, 16.0F, 0.0F, 16.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 3.0F, -12.0F, 2.8798F, 0.0F, -3.1416F));

        ModelPartData cube_r15 = seven.addChild("cube_r15", ModelPartBuilder.create().uv(195, 120).cuboid(-8.0F, -4.0F, -12.0F, 16.0F, 0.0F, 16.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, -37.0F, 9.0F, -0.2618F, 0.0F, 0.0F));

        ModelPartData cube_r16 = seven.addChild("cube_r16", ModelPartBuilder.create().uv(260, 120).cuboid(-8.0F, -4.0F, -12.0F, 16.0F, 0.0F, 16.0F, new Dilation(0.001F)), ModelTransform.of(-9.0F, -37.0F, 0.0F, 0.0F, -1.5708F, -0.2618F));

        ModelPartData cube_r17 = seven.addChild("cube_r17", ModelPartBuilder.create().uv(325, 120).cuboid(-8.0F, -4.0F, -12.0F, 16.0F, 0.0F, 16.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, -37.0F, -9.0F, 2.8798F, 0.0F, -3.1416F));

        ModelPartData cube_r18 = seven.addChild("cube_r18", ModelPartBuilder.create().uv(390, 120).cuboid(-8.0F, -4.0F, -12.0F, 16.0F, 0.0F, 16.0F, new Dilation(0.001F)), ModelTransform.of(9.0F, -37.0F, 0.0F, 0.0F, 1.5708F, 0.2618F));

        ModelPartData bone9 = seven.addChild("bone9", ModelPartBuilder.create().uv(65, 290).cuboid(27.2752F, -21.0F, -0.8776F, 0.0F, 42.0F, 9.9411F, new Dilation(0.001F))
                .uv(21, 343).cuboid(10.3046F, -21.0F, 16.093F, 9.9411F, 42.0F, 0.0F, new Dilation(0.001F))
                .uv(107, 290).cuboid(3.2752F, -21.0F, -0.8776F, 0.0F, 42.0F, 9.9411F, new Dilation(0.001F))
                .uv(42, 427).cuboid(10.3046F, -21.0F, -7.907F, 9.9411F, 8.0F, 0.0F, new Dilation(0.001F))
                .uv(63, 427).cuboid(10.3046F, 20.0F, -7.907F, 9.9411F, 1.0F, 0.0F, new Dilation(0.001F)), ModelTransform.pivot(-15.2752F, -21.0F, -4.093F));

        ModelPartData octagon_r1 = bone9.addChild("octagon_r1", ModelPartBuilder.create().uv(171, 386).cuboid(-4.9706F, -13.0F, -12.0F, 9.9411F, 33.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.309F, 0.0F));

        ModelPartData octagon_r2 = bone9.addChild("octagon_r2", ModelPartBuilder.create().uv(233, 290).cuboid(-4.9706F, -21.0F, -12.0F, 9.9411F, 42.0F, 0.0F, new Dilation(0.001F))
                .uv(86, 290).cuboid(-12.0F, -21.0F, -4.9706F, 0.0F, 42.0F, 9.9411F, new Dilation(0.001F))
                .uv(128, 290).cuboid(12.0F, -21.0F, -4.9706F, 0.0F, 42.0F, 9.9411F, new Dilation(0.001F))
                .uv(0, 343).cuboid(-4.9706F, -21.0F, 12.0F, 9.9411F, 42.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(15.2752F, 0.0F, 4.093F, 0.0F, -0.7854F, 0.0F));

        ModelPartData octagon_r3 = bone9.addChild("octagon_r3", ModelPartBuilder.create().uv(0, 427).cuboid(-4.9706F, -21.0F, -12.0F, 9.9411F, 8.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(15.2752F, 37.6777F, -8.6141F, -0.7854F, 0.0F, 0.0F));

        ModelPartData octagon_r4 = bone9.addChild("octagon_r4", ModelPartBuilder.create().uv(21, 427).cuboid(-4.9706F, -21.0F, -12.0F, 9.9411F, 8.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(15.2752F, -6.636F, 15.4275F, 0.7854F, 0.0F, 0.0F));

        ModelPartData octagon_r5 = bone9.addChild("octagon_r5", ModelPartBuilder.create().uv(218, 386).cuboid(-12.0F, -13.0F, -4.9706F, 0.0F, 33.0F, 5.0F, new Dilation(0.001F)), ModelTransform.of(30.5514F, 0.0F, 0.0003F, 0.0F, -0.2618F, 0.0F));
        return TexturedModelData.of(modelData, 1024, 1024);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red,
            float green, float blue, float alpha) {
        matrices.push();
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180f));
        coral.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
        matrices.pop();
    }

    @Override
    public void renderWithAnimations(ClientTardis tardis, AbstractLinkableBlockEntity door, ModelPart root, MatrixStack matrices,
                                     VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float pAlpha) {
        matrices.push();
        matrices.translate(0, -1.5f, 0f);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180f));

        super.renderWithAnimations(tardis, door, root, matrices, vertices, light, overlay, red, green, blue, pAlpha);

        matrices.pop();
    }

    @Override
    public Animation getAnimationForDoorState(DoorHandler.AnimationDoorState state) {
        return Animation.Builder.create(0).build();
    }

    @Override
    public ModelPart getPart() {
        return coral;
    }
}
