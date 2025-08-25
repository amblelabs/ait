package dev.amble.ait.client.models.exteriors;

import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.RotationAxis;

import dev.amble.ait.api.tardis.link.v2.Linkable;
import dev.amble.ait.client.tardis.ClientTardis;
import dev.amble.ait.core.blockentities.ExteriorBlockEntity;
import dev.amble.ait.core.tardis.Tardis;

public class PipeExteriorModel extends SimpleExteriorModel {
    private final ModelPart tardis;
    public PipeExteriorModel(ModelPart root) {
        this.tardis = root.getChild("tardis");
    }
    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData tardis = modelPartData.addChild("tardis", ModelPartBuilder.create().uv(0, 0).cuboid(-7.5F, -14.5F, -7.0F, 15.0F, 7.0F, 15.0F, new Dilation(0.0F))
        .uv(0, 22).cuboid(-6.5F, -8.0F, -6.0F, 13.0F, 8.0F, 13.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -22.5F, -0.5F, 0.0F, 0.0F, -3.1416F));
        return TexturedModelData.of(modelData, 128, 128);
    }
    @Override
    public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
        matrices.push();
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180f));
        tardis.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
        matrices.pop();
    }

    @Override
    public void renderWithAnimations(ClientTardis tardis, ExteriorBlockEntity exterior, ModelPart root, MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        matrices.push();
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180f));

        this.renderDoors(tardis, exterior, root, matrices, vertices, light, overlay, red, green, blue, alpha, false);

        super.renderWithAnimations(tardis, exterior, root, matrices, vertices, light, overlay, red, green, blue, alpha);
        matrices.pop();
    }

    @Override
    public <T extends Entity & Linkable> void renderEntity(T falling, ModelPart root, MatrixStack matrices,
                                                           VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
        matrices.push();
        //matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180f));
        Tardis tardis = falling.tardis().get();

        if (tardis == null) return;

        this.tardis.pivotY = !tardis.door().isOpen() ? 22F : 8f;

        super.renderEntity(falling, root, matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
        matrices.pop();
    }

    @Override
    public ModelPart getPart() {
        return tardis;
    }

    @Override
    public void renderDoors(ClientTardis tardis, ExteriorBlockEntity exterior, ModelPart root, MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float pAlpha, boolean isBOTI) {
        this.tardis.pivotY = !tardis.door().isOpen() ? -14F : 0;
    }
}