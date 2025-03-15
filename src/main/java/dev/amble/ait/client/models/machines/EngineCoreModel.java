package dev.amble.ait.client.models.machines; // Made with Blockbench 4.10.4
// Exported for Minecraft version 1.17+ for Yarn
// Paste this class into your mod and generate all required imports

import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;

public class EngineCoreModel extends SinglePartEntityModel {
    private final ModelPart core;

    public EngineCoreModel(ModelPart root) {
        this.core = root.getChild("core");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData core = modelPartData.addChild("core", ModelPartBuilder.create(),
                ModelTransform.pivot(0.0F, 24.0F, 0.0F));

        ModelPartData cube_r1 = core
                .addChild("cube_r1",
                        ModelPartBuilder.create().uv(0, 0).cuboid(-16.0F, -16.0F, -16.0F, 32.0F, 32.0F, 32.0F,
                                new Dilation(0.0F)),
                        ModelTransform.of(0.0F, -16.0F, 0.0F, -0.7854F, -0.7854F, 0.7854F));
        return TexturedModelData.of(modelData, 128, 128);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red,
            float green, float blue, float alpha) {
        core.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
    }

    @Override
    public ModelPart getPart() {
        return core;
    }

    @Override
    public void setAngles(Entity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw,
            float headPitch) {
    }
}
