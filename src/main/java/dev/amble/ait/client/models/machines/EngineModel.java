package dev.amble.ait.client.models.machines;

import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;

public class EngineModel extends SinglePartEntityModel {
    private final ModelPart bone;

    public EngineModel(ModelPart root) {
        this.bone = root.getChild("engine");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData engine = modelPartData.addChild("engine", ModelPartBuilder.create().uv(214, 84).cuboid(-24.0F, -16.0F, -24.0F, 48.0F, 16.0F, 48.0F, new Dilation(0.0F))
                .uv(62, 131).cuboid(-25.0F, -34.6863F, -10.8995F, 2.0F, 2.0F, 16.0F, new Dilation(0.0F))
                .uv(62, 158).cuboid(-23.0F, -34.6863F, -10.8995F, 7.0F, 2.0F, 2.0F, new Dilation(0.0F))
                .uv(62, 158).cuboid(-23.0F, -40.6863F, -10.8995F, 7.0F, 2.0F, 2.0F, new Dilation(0.0F))
                .uv(62, 131).cuboid(-25.0F, -40.6863F, -10.8995F, 2.0F, 2.0F, 16.0F, new Dilation(0.0F))
                .uv(62, 131).cuboid(-25.0F, -34.6863F, -10.8995F, 2.0F, 2.0F, 16.0F, new Dilation(0.0F))
                .uv(62, 158).cuboid(-23.0F, -34.6863F, -10.8995F, 7.0F, 2.0F, 2.0F, new Dilation(0.0F))
                .uv(0, 0).cuboid(-1.0F, -58.0F, -1.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F))
                .uv(23, 221).cuboid(-1.0F, -34.0F, 15.0F, 2.0F, 19.0F, 2.0F, new Dilation(0.0F))
                .uv(23, 221).cuboid(-4.0F, -34.0F, -20.0F, 2.0F, 18.0F, 2.0F, new Dilation(0.0F))
                .uv(0, 46).cuboid(-20.0F, -18.0F, -20.0F, 16.0F, 2.0F, 2.0F, new Dilation(0.0F))
                .uv(0, 46).cuboid(1.0F, -26.0F, 15.0F, 16.0F, 2.0F, 2.0F, new Dilation(0.0F))
                .uv(0, 46).cuboid(1.0F, -22.0F, 15.0F, 16.0F, 2.0F, 2.0F, new Dilation(0.0F))
                .uv(24, 64).cuboid(-19.9F, -61.0F, -20.0F, 2.0F, 43.0F, 2.0F, new Dilation(0.0F))
                .uv(144, 28).cuboid(-17.9F, -51.4F, -20.0F, 28.0F, 2.0F, 2.0F, new Dilation(0.0F))
                .uv(90, 232).cuboid(15.8782F, -51.4F, -12.2218F, 2.0F, 2.0F, 20.0F, new Dilation(0.0F))
                .uv(5, 46).cuboid(6.8782F, -51.4F, 5.7782F, 9.0F, 2.0F, 2.0F, new Dilation(0.0F))
                .uv(34, 229).cuboid(-22.9F, -61.0F, -20.0F, 3.0F, 2.0F, 2.0F, new Dilation(0.0F))
                .uv(92, 234).cuboid(-22.9F, -61.0F, -18.0F, 2.0F, 2.0F, 18.0F, new Dilation(0.0F))
                .uv(23, 221).cuboid(4.0F, -34.0F, -20.0F, 2.0F, 18.0F, 2.0F, new Dilation(0.0F))
                .uv(124, 121).cuboid(4.0F, -18.0F, -18.0F, 2.0F, 2.0F, 13.0F, new Dilation(0.0F))
                .uv(86, 125).cuboid(6.0F, -18.0F, -7.0F, 15.0F, 2.0F, 2.0F, new Dilation(0.0F))
                .uv(16, 64).cuboid(19.0F, -41.0F, -7.0F, 2.0F, 23.0F, 2.0F, new Dilation(0.0F))
                .uv(1, 191).cuboid(19.0F, -41.0F, -5.0F, 2.0F, 2.0F, 19.0F, new Dilation(0.0F))
                .uv(174, 3).cuboid(-11.0F, -72.0F, -6.0F, 27.0F, 11.0F, 14.0F, new Dilation(0.0F))
                .uv(40, 47).cuboid(0.5F, -55.0F, 1.0F, 3.0F, 17.0F, 3.0F, new Dilation(0.0F))
                .uv(48, 0).cuboid(0.5F, -62.1095F, 4.7073F, 3.0F, 7.0F, 3.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 24.0F, 0.0F));

        ModelPartData cube_r1 = engine.addChild("cube_r1", ModelPartBuilder.create().uv(0, 31).cuboid(0.5F, -5.0F, 1.0F, 3.0F, 3.0F, 6.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, -50.1032F, 2.4217F, 0.48F, 0.0F, 0.0F));

        ModelPartData cube_r2 = engine.addChild("cube_r2", ModelPartBuilder.create().uv(91, 233).cuboid(-1.0F, 6.0F, -18.0F, 2.0F, 2.0F, 19.0F, new Dilation(0.001F)), ModelTransform.of(16.0F, -26.364F, 19.9497F, -0.7854F, 0.0F, 0.0F));

        ModelPartData cube_r3 = engine.addChild("cube_r3", ModelPartBuilder.create().uv(94, 236).cuboid(-1.0F, 6.0F, -15.0F, 2.0F, 2.0F, 16.0F, new Dilation(0.001F)), ModelTransform.of(16.0F, -30.364F, 19.9497F, -0.7854F, 0.0F, 0.0F));

        ModelPartData cube_r4 = engine.addChild("cube_r4", ModelPartBuilder.create().uv(32, 64).cuboid(11.0F, -22.0F, 17.0F, 2.0F, 13.0F, 2.0F, new Dilation(0.0F))
                .uv(0, 107).cuboid(11.0F, -9.0F, 17.0F, 8.0F, 2.0F, 2.0F, new Dilation(0.0F))
                .uv(70, 212).cuboid(19.0F, -9.0F, -21.0F, 2.0F, 2.0F, 40.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -14.2598F, 2.235F, 0.0F, -1.5708F, 0.0F));

        ModelPartData cube_r5 = engine.addChild("cube_r5", ModelPartBuilder.create().uv(0, 190).cuboid(19.0F, -9.0F, -21.0F, 2.0F, 2.0F, 20.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, -18.217F, 15.7136F, -1.0908F, 0.0F, 0.0F));

        ModelPartData cube_r6 = engine.addChild("cube_r6", ModelPartBuilder.create().uv(0, 42).cuboid(-6.0F, -1.0F, -1.0F, 17.0F, 2.0F, 2.0F, new Dilation(0.001F)), ModelTransform.of(-17.4115F, -63.6223F, -1.0F, 0.0F, 0.0F, -0.6109F));

        ModelPartData cube_r7 = engine.addChild("cube_r7", ModelPartBuilder.create().uv(8, 0).cuboid(-17.9F, -19.4F, -36.0F, 11.0F, 2.0F, 2.0F, new Dilation(0.001F)), ModelTransform.of(-2.6986F, -32.0F, 18.1131F, 0.0F, -0.7854F, 0.0F));

        ModelPartData cube_r8 = engine.addChild("cube_r8", ModelPartBuilder.create().uv(62, 131).cuboid(-1.0F, -1.0F, -15.0F, 2.0F, 2.0F, 16.0F, new Dilation(0.0F))
                .uv(62, 131).cuboid(-1.0F, -1.0F, -15.0F, 2.0F, 2.0F, 16.0F, new Dilation(0.001F)), ModelTransform.of(-24.0F, -44.0F, 15.0F, 0.7854F, 0.0F, 0.0F));

        ModelPartData cube_r9 = engine.addChild("cube_r9", ModelPartBuilder.create().uv(62, 131).cuboid(-1.0F, -1.0F, -15.0F, 2.0F, 2.0F, 16.0F, new Dilation(0.001F)), ModelTransform.of(-24.0F, -50.0F, 15.0F, 0.7854F, 0.0F, 0.0F));

        ModelPartData artronpipe = engine.addChild("artronpipe", ModelPartBuilder.create().uv(28, 34).cuboid(-2.0F, -45.0F, 15.0F, 4.0F, 4.0F, 4.0F, new Dilation(0.0F))
                .uv(86, 113).cuboid(-24.0F, -49.0F, 15.0F, 26.0F, 4.0F, 4.0F, new Dilation(0.0F))
                .uv(0, 19).cuboid(-24.0F, -49.0F, 7.0F, 4.0F, 4.0F, 8.0F, new Dilation(0.0F))
                .uv(28, 19).cuboid(-24.0F, -54.0F, 3.0F, 4.0F, 11.0F, 4.0F, new Dilation(0.0F))
                .uv(184, 169).cuboid(-24.0F, -43.0F, -23.0F, 4.0F, 4.0F, 30.0F, new Dilation(0.0F))
                .uv(44, 0).cuboid(-22.5F, -39.2F, -17.0F, 1.0F, 40.0F, 1.0F, new Dilation(-0.1F))
                .uv(44, 0).cuboid(-15.5F, -16.2F, -23.0F, 1.0F, 17.0F, 1.0F, new Dilation(-0.1F))
                .uv(44, 0).cuboid(17.5F, -16.2F, -23.0F, 1.0F, 17.0F, 1.0F, new Dilation(-0.1F))
                .uv(44, 0).cuboid(17.5F, -16.2F, 2.0F, 1.0F, 17.0F, 1.0F, new Dilation(-0.1F))
                .uv(44, 0).cuboid(15.0F, -45.2F, -6.0F, 1.0F, 17.0F, 1.0F, new Dilation(-0.1F))
                .uv(44, 0).cuboid(-11.0F, -45.2F, -6.0F, 1.0F, 17.0F, 1.0F, new Dilation(-0.1F))
                .uv(44, 0).cuboid(-15.5F, -16.2F, 2.0F, 1.0F, 17.0F, 1.0F, new Dilation(-0.1F))
                .uv(44, 0).cuboid(-22.5F, -39.2F, -17.0F, 1.0F, 0.0F, 1.0F, new Dilation(-0.1F))
                .uv(44, 0).cuboid(-22.5F, -39.2F, -8.0F, 1.0F, 40.0F, 1.0F, new Dilation(-0.1F))
                .uv(44, 0).cuboid(-22.5F, -39.2F, -8.0F, 1.0F, 0.0F, 1.0F, new Dilation(0.0F))
                .uv(44, 0).cuboid(-22.5F, -39.2F, 0.0F, 1.0F, 40.0F, 1.0F, new Dilation(-0.1F))
                .uv(44, 0).cuboid(-22.5F, -39.2F, 0.0F, 1.0F, 0.0F, 1.0F, new Dilation(0.0F))
                .uv(44, 0).cuboid(-7.5F, -45.2F, 16.5F, 1.0F, 46.0F, 1.0F, new Dilation(-0.1F))
                .uv(44, 0).cuboid(-7.5F, -45.2F, 16.5F, 1.0F, 0.0F, 1.0F, new Dilation(0.0F))
                .uv(44, 0).cuboid(-22.5F, -45.2F, 16.5F, 1.0F, 6.0F, 1.0F, new Dilation(-0.1F))
                .uv(44, 0).cuboid(-22.5F, -45.2F, 16.5F, 1.0F, 0.0F, 1.0F, new Dilation(0.0F))
                .uv(0, 64).cuboid(-24.0F, -39.0F, -23.0F, 4.0F, 39.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -16.0F, 0.0F));

        ModelPartData cube_r10 = artronpipe.addChild("cube_r10", ModelPartBuilder.create().uv(44, 0).cuboid(-0.5F, -13.0F, -0.5F, 1.0F, 26.0F, 1.0F, new Dilation(-0.1F))
                .uv(44, 0).cuboid(25.5F, -13.0F, -0.5F, 1.0F, 26.0F, 1.0F, new Dilation(-0.1F)), ModelTransform.of(-10.5F, -32.7F, 5.5F, -0.1309F, 0.0F, 0.0F));

        ModelPartData thingy = engine.addChild("thingy", ModelPartBuilder.create().uv(0, 113).cuboid(-1.0F, 24.7128F, -22.0F, 35.0F, 2.0F, 16.0F, new Dilation(0.0F))
                .uv(214, 28).cuboid(-0.8F, -1.2872F, -28.0F, 0.0F, 28.0F, 28.0F, new Dilation(0.0F))
                .uv(40, 64).mirrored().cuboid(-1.0F, -1.0F, -22.0F, 35.0F, 2.0F, 16.0F, new Dilation(0.0F)).mirrored(false)
                .uv(0, 4).cuboid(9.0F, -10.0F, -17.0F, 15.0F, 9.0F, 6.0F, new Dilation(0.0F))
                .uv(62, 149).cuboid(10.0F, -10.0F, -14.0F, 13.0F, 9.0F, 0.0F, new Dilation(0.0F)), ModelTransform.pivot(-15.0F, -48.3991F, 4.1005F));

        ModelPartData cube_r11 = thingy.addChild("cube_r11", ModelPartBuilder.create().uv(98, 131).cuboid(-2.0F, -0.5F, -0.5F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(98, 131).cuboid(-1.0F, -0.5F, -0.5F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(98, 131).cuboid(2.0F, -0.5F, -0.5F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(98, 131).cuboid(1.0F, -0.5F, -0.5F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(98, 131).cuboid(0.0F, -0.5F, -0.5F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(16.5F, -9.3F, -14.0F, 0.7854F, 0.0F, 0.0F));

        ModelPartData cube_r12 = thingy.addChild("cube_r12", ModelPartBuilder.create().uv(40, 64).cuboid(-1.0F, -1.0F, -22.0F, 35.0F, 2.0F, 16.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 18.5526F, 4.134F, -1.0472F, 0.0F, 0.0F));

        ModelPartData cube_r13 = thingy.addChild("cube_r13", ModelPartBuilder.create().uv(40, 64).cuboid(-1.0F, -1.0F, -22.0F, 35.0F, 2.0F, 16.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 31.409F, -9.866F, -2.0944F, 0.0F, 0.0F));

        ModelPartData cube_r14 = thingy.addChild("cube_r14", ModelPartBuilder.create().uv(170, 203).cuboid(0.0F, -14.0F, -14.0F, 0.0F, 28.0F, 28.0F, new Dilation(0.0F)), ModelTransform.of(33.9F, 12.7128F, -14.0F, 0.0F, 3.1416F, 0.0F));

        ModelPartData cube_r15 = thingy.addChild("cube_r15", ModelPartBuilder.create().uv(0, 113).cuboid(-1.0F, -1.0F, -22.0F, 35.0F, 2.0F, 16.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 31.409F, -18.134F, -1.0472F, 0.0F, 0.0F));

        ModelPartData cube_r16 = thingy.addChild("cube_r16", ModelPartBuilder.create().uv(48, 10).cuboid(-1.0F, -1.0F, -22.0F, 35.0F, 2.0F, 16.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 18.5526F, -32.134F, -2.0944F, 0.0F, 0.0F));

        ModelPartData huontank = engine.addChild("huontank", ModelPartBuilder.create().uv(158, 108).cuboid(-23.0F, -55.6F, 9.0F, 14.0F, 22.0F, 14.0F, new Dilation(0.0F))
                .uv(0, 170).cuboid(-23.0F, -47.6F, 9.0F, 14.0F, 6.0F, 14.0F, new Dilation(0.5F))
                .uv(124, 169).cuboid(-23.5F, -56.6F, 8.5F, 15.0F, 4.0F, 15.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData artrontank = engine.addChild("artrontank", ModelPartBuilder.create().uv(0, 131).cuboid(-1.1F, -24.0F, -14.0F, 16.0F, 24.0F, 15.0F, new Dilation(0.0F))
                .uv(62, 169).cuboid(-1.1F, -15.0F, -14.0F, 16.0F, 6.0F, 15.0F, new Dilation(0.5F))
                .uv(144, 64).cuboid(-16.0F, -18.0F, -18.0F, 31.0F, 40.0F, 4.0F, new Dilation(0.5F))
                .uv(158, 149).cuboid(-1.6F, -25.0F, -14.5F, 17.0F, 4.0F, 16.0F, new Dilation(0.0F)), ModelTransform.pivot(-5.0F, -34.0F, 22.0F));
        return TexturedModelData.of(modelData, 512, 512);
    }

    @Override
    public ModelPart getPart() {
        return bone;
    }

    @Override
    public void setAngles(Entity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw,
            float headPitch) {
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red,
            float green, float blue, float alpha) {
        bone.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
    }
}
