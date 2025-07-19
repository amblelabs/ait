package dev.amble.ait.client.models.consoles;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.client.util.math.MatrixStack;

import dev.amble.ait.core.entities.ConsoleControlEntity;

@Environment(value = EnvType.CLIENT)
public class ControlModel extends SinglePartEntityModel<ConsoleControlEntity> {
    private final ModelPart root;

    public ControlModel(ModelPart root) {
        this.root = root.getChild("root");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData root = modelPartData.addChild("root",
                ModelPartBuilder.create().uv(9, 0).cuboid(-1.0F, -2.0F, -1.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F))
                        .uv(0, 0).cuboid(-1.0F, -11.0F, -1.0F, 2.0F, 8.0F, 2.0F, new Dilation(0.0F)),
                ModelTransform.pivot(0.0F, 24.0F, 0.0F));
        return TexturedModelData.of(modelData, 32, 32);
    }

    public static TexturedModelData getNotModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData root = modelPartData.addChild("root",
                ModelPartBuilder.create().uv(0, 0).cuboid(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, new Dilation(0.0F))
                        .uv(0, 0).cuboid(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, new Dilation(0.0F)),
                ModelTransform.pivot(0.0F, 0.0F, 0.0F));
        return TexturedModelData.of(modelData, 32, 32);
    }

    @Override
    public ModelPart getPart() {
        return this.root;
    }

    @Override
    public void setAngles(ConsoleControlEntity controlEntity, float f, float g, float h, float i, float j) {
        this.root.visible = !controlEntity.isPartOfSequence();
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green,
            float blue, float alpha) {
        this.root.render(matrices, vertices, light, overlay, red, green, blue, alpha);
    }
}
