package dev.amble.ait.client.models.exteriors; // Made with Blockbench 4.10.1
// Exported for Minecraft version 1.17+ for Yarn
// Paste this class into your mod and generate all required imports

import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.animation.Animation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;

import dev.amble.ait.api.tardis.link.v2.Linkable;
import dev.amble.ait.client.tardis.ClientTardis;
import dev.amble.ait.core.blockentities.ExteriorBlockEntity;
import dev.amble.ait.core.tardis.handler.DoorHandler;

public class GeometricExteriorModel extends ExteriorModel {
    private final ModelPart geometric;

    public GeometricExteriorModel(ModelPart root) {
        this.geometric = root.getChild("geometric");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData geometric = modelPartData.addChild("geometric", ModelPartBuilder.create(),
                ModelTransform.pivot(0.0F, 24.0F, 0.0F));

        ModelPartData body = geometric.addChild("body",
                ModelPartBuilder.create().uv(0, 0)
                        .cuboid(-16.0F, -44.0F, -8.0F, 32.0F, 44.0F, 24.0F, new Dilation(0.05F)).uv(49, 69)
                        .cuboid(8.0F, -40.0F, -8.0F, 0.0F, 40.0F, 24.0F, new Dilation(0.0F)).uv(0, 69)
                        .cuboid(-8.0F, -40.0F, -8.0F, 0.0F, 40.0F, 24.0F, new Dilation(0.0F)),
                ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData door = geometric.addChild("door",
                ModelPartBuilder.create().uv(98, 97)
                        .cuboid(-8.0F, -40.0F, -8.0F, 16.0F, 40.0F, 2.0F, new Dilation(0.0F)).uv(113, 0)
                        .cuboid(-8.0F, -38.0F, 15.75F, 16.0F, 36.0F, 0.0F, new Dilation(0.0F)).uv(98, 72)
                        .cuboid(-8.0F, -2.0F, -6.0F, 16.0F, 2.0F, 22.0F, new Dilation(0.0F)).uv(91, 47)
                        .cuboid(-8.0F, -40.0F, -6.0F, 16.0F, 2.0F, 22.0F, new Dilation(0.0F)),
                ModelTransform.pivot(0.0F, 0.0F, 0.0F));
        return TexturedModelData.of(modelData, 256, 256);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red,
            float green, float blue, float alpha) {
        geometric.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
    }

    @Override
    public ModelPart getPart() {
        return geometric;
    }

    @Override
    public void renderWithAnimations(ClientTardis tardis, ExteriorBlockEntity exterior, ModelPart root, MatrixStack matrices,
                                     VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float pAlpha) {
        matrices.push();
        matrices.scale(1F, 1F, 1F);
        matrices.translate(0, -1.5f, 0);

        this.renderDoors(tardis, exterior, root, matrices, vertices, light, overlay, red, green, blue, pAlpha, false);

        super.renderWithAnimations(tardis, exterior, root, matrices, vertices, light, overlay, red, green, blue, pAlpha);

        matrices.pop();
    }

    @Override
    public <T extends Entity & Linkable> void renderEntity(T falling, ModelPart root, MatrixStack matrices,
                                                           VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
        matrices.push();
        matrices.scale(1F, 1F, 1F);
        matrices.translate(0, -1.5f, 0);

        super.renderEntity(falling, root, matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
        matrices.pop();
    }

    @Override
    public Animation getAnimationForDoorState(DoorHandler.AnimationDoorState state) {
        return Animation.Builder.create(0).build();
    }

    @Override
    public void renderDoors(ClientTardis tardis, ExteriorBlockEntity exterior, ModelPart root, MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float pAlpha, boolean isBOTI) {
        DoorHandler door = tardis.door();

        this.geometric.getChild("door").pivotZ = door.isOpen() ? -16f : 0f;

        if (isBOTI) {
            matrices.push();
            matrices.scale(1F, 1F, 1F);
            matrices.translate(0f, 0f, -4f);
            this.geometric.getChild("door").render(matrices, vertices, light, overlay, red, green, blue, pAlpha);
            matrices.pop();
        }
    }
}
