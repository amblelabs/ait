package dev.amble.ait.client.models.machines;

import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;

public class ArtronCollectorModel extends SinglePartEntityModel {
    public ModelPart collector;

    public ArtronCollectorModel(ModelPart root) {
        this.collector = root.getChild("collector");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData collector = modelPartData.addChild("collector",
                ModelPartBuilder.create().uv(17, 13).cuboid(-1.0F, -16.1F, -1.0F, 2.0F, 16.0F, 2.0F, new Dilation(0.0F))
                        .uv(0, 0).cuboid(-1.0F, -20.1F, -1.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F)).uv(0, 0)
                        .cuboid(-5.0F, -0.1F, -6.0F, 10.0F, 0.0F, 12.0F, new Dilation(0.005F)).uv(13, 31)
                        .cuboid(-0.5F, -29.0F, -0.5F, 1.0F, 13.0F, 1.0F, new Dilation(0.0F)).uv(26, 19)
                        .cuboid(-2.5F, -12.0F, -2.5F, 5.0F, 0.0F, 5.0F, new Dilation(0.005F)).uv(22, 30)
                        .cuboid(-2.0F, -13.0F, -2.0F, 4.0F, 0.0F, 4.0F, new Dilation(0.005F)).uv(26, 25)
                        .cuboid(-2.0F, -1.0F, -2.0F, 4.0F, 0.0F, 4.0F, new Dilation(0.005F)).uv(26, 13)
                        .cuboid(-2.5F, -2.0F, -2.5F, 5.0F, 0.0F, 5.0F, new Dilation(0.005F)).uv(0, 13)
                        .cuboid(-2.0F, -11.0F, -2.0F, 4.0F, 8.0F, 4.0F, new Dilation(0.0F)),
                ModelTransform.pivot(0.0F, 24.0F, 0.0F));

        ModelPartData battery_levels = collector.addChild("battery_levels", ModelPartBuilder.create(),
                ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData b_1 = battery_levels.addChild("b_1",
                ModelPartBuilder.create().uv(0, 26).cuboid(-1.0F, -5.0F, -2.0F, 2.0F, 1.0F, 4.0F, new Dilation(0.01F)),
                ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData b_2 = battery_levels.addChild("b_2",
                ModelPartBuilder.create().uv(0, 26).cuboid(-1.0F, -6.0F, -2.0F, 2.0F, 2.0F, 4.0F, new Dilation(0.01F)),
                ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData b_3 = battery_levels.addChild("b_3",
                ModelPartBuilder.create().uv(0, 26).cuboid(-1.0F, -7.0F, -2.0F, 2.0F, 3.0F, 4.0F, new Dilation(0.01F)),
                ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData b_4 = battery_levels.addChild("b_4",
                ModelPartBuilder.create().uv(0, 26).cuboid(-1.0F, -8.0F, -2.0F, 2.0F, 4.0F, 4.0F, new Dilation(0.01F)),
                ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData b_5 = battery_levels.addChild("b_5",
                ModelPartBuilder.create().uv(0, 26).cuboid(-1.0F, -9.0F, -2.0F, 2.0F, 5.0F, 4.0F, new Dilation(0.01F)),
                ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData b_6 = battery_levels.addChild("b_6",
                ModelPartBuilder.create().uv(0, 26).cuboid(-1.0F, -10.0F, -2.0F, 2.0F, 6.0F, 4.0F, new Dilation(0.01F)),
                ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData spinner = collector.addChild("spinner", ModelPartBuilder.create(),
                ModelTransform.pivot(0.0F, -20.0F, 0.0F));

        ModelPartData cube_r1 = spinner.addChild("cube_r1", ModelPartBuilder.create().uv(33, 0).cuboid(-0.5F, -8.0F,
                -0.25F, 1.0F, 8.0F, 0.0F, new Dilation(0.005F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.7854F, 0.0F, 0.0F));

        ModelPartData bone3 = spinner.addChild("bone3", ModelPartBuilder.create(),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 2.0944F, 0.0F));

        ModelPartData cube_r2 = bone3.addChild("cube_r2", ModelPartBuilder.create().uv(18, 32).cuboid(-0.5F, -8.0F,
                -0.25F, 1.0F, 8.0F, 0.0F, new Dilation(0.005F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.7854F, 0.0F, 0.0F));

        ModelPartData bone4 = bone3.addChild("bone4", ModelPartBuilder.create(),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 2.0944F, 0.0F));

        ModelPartData cube_r3 = bone4.addChild("cube_r3",
                ModelPartBuilder.create().uv(9, 0).cuboid(-0.5F, -8.0F, -0.25F, 1.0F, 8.0F, 0.0F, new Dilation(0.005F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.7854F, 0.0F, 0.0F));
        return TexturedModelData.of(modelData, 64, 64);
    }

    @Override
    public ModelPart getPart() {
        return collector;
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green,
            float blue, float alpha) {
        collector.render(matrices, vertices, light, overlay, red, green, blue, alpha);
    }

    @Override
    public void setAngles(Entity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw,
            float headPitch) {
    }
}
