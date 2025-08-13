package dev.amble.ait.client.models.exteriors;

import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;

import dev.amble.ait.api.tardis.link.v2.Linkable;
import dev.amble.ait.client.tardis.ClientTardis;
import dev.amble.ait.core.blockentities.ExteriorBlockEntity;

public class EasterHeadModel extends SimpleExteriorModel {
    private final ModelPart head;

    public EasterHeadModel(ModelPart root) {
        this.head = root.getChild("head");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData head = modelPartData.addChild("head", ModelPartBuilder.create().uv(0, 0).cuboid(-12.0F, -1.0534F,
                -14.6026F, 24.0F, 14.0F, 24.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 11.0534F, 2.6026F));

        ModelPartData cube_r1 = head
                .addChild(
                        "cube_r1", ModelPartBuilder.create().uv(78, 57).cuboid(-9.0F, -2.0F, -11.5F, 18.0F, 19.0F,
                                15.0F, new Dilation(0.0F)),
                        ModelTransform.of(0.0F, -11.0534F, 9.3974F, -0.4363F, 0.0F, 0.0F));

        ModelPartData door = head.addChild("door", ModelPartBuilder.create().uv(0, 72)
                .cuboid(-10.0F, -22.7922F, -20.2482F, 20.0F, 23.0F, 20.0F, new Dilation(0.0F)).uv(81, 92)
                .cuboid(10.0F, -30.7922F, -10.2483F, 6.0F, 31.0F, 8.0F, new Dilation(0.0F)).uv(0, 39)
                .cuboid(-12.0F, -32.7922F, -22.2482F, 24.0F, 10.0F, 22.0F, new Dilation(0.0F)).uv(81, 92).mirrored()
                .cuboid(-16.0F, -30.7922F, -10.2483F, 6.0F, 31.0F, 8.0F, new Dilation(0.0F)).mirrored(false),
                ModelTransform.pivot(0.0F, -1.2612F, 9.6457F));

        ModelPartData cube_r2 = door.addChild("cube_r2",
                ModelPartBuilder.create().uv(0, 39).cuboid(5.0F, 12.0F, 2.0F, 3.0F, 8.0F, 4.0F, new Dilation(0.0F))
                        .uv(97, 0).cuboid(-5.0F, 0.0F, 0.0F, 10.0F, 20.0F, 7.0F, new Dilation(0.0F)).uv(0, 0)
                        .cuboid(-8.0F, 12.0F, 2.0F, 3.0F, 8.0F, 4.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, -22.7922F, -20.2482F, -0.2618F, 0.0F, 0.0F));
        return TexturedModelData.of(modelData, 256, 256);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red,
            float green, float blue, float alpha) {
        head.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
    }

    @Override
    public void renderWithAnimations(ClientTardis tardis, ExteriorBlockEntity exterior, ModelPart root, MatrixStack matrices,
                                     VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float pAlpha) {
        matrices.push();
        matrices.translate(0, -1.5f, 0);
        this.head.getChild("door").pitch = (tardis.door().isOpen()) ? -45f : 0f;

        super.renderWithAnimations(tardis, exterior, root, matrices, vertices, light, overlay, red, green, blue, pAlpha);
        matrices.pop();
    }

    @Override
    public <T extends Entity & Linkable> void renderEntity(T falling, ModelPart root, MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
        if (falling.tardis().isEmpty())
            return;

        matrices.push();
        matrices.translate(0, -1.5f, 0);
        this.head.getChild("door").pitch = (falling.tardis().get().door().isOpen()) ? -45f : 0f;

        super.renderEntity(falling, root, matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
        matrices.pop();
    }

    @Override
    public ModelPart getPart() {
        return head;
    }

    @Override
    public void renderDoors(ClientTardis tardis, ExteriorBlockEntity exterior, ModelPart root, MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float pAlpha, boolean isBOTI) {

    }
}
