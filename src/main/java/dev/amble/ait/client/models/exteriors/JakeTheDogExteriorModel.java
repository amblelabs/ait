package dev.amble.ait.client.models.exteriors;

import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;

import dev.amble.ait.api.tardis.link.v2.Linkable;
import dev.amble.ait.client.tardis.ClientTardis;
import dev.amble.ait.core.blockentities.ExteriorBlockEntity;

public class JakeTheDogExteriorModel  extends SimpleExteriorModel {
    private final ModelPart jake;

    public JakeTheDogExteriorModel(ModelPart root) {
        this.jake = root.getChild("jake");
    }
    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData jake = modelPartData.addChild("jake", ModelPartBuilder.create().uv(0, 0).cuboid(-19.0F, -38.0F, -19.0F, 38.0F, 38.0F, 38.0F, new Dilation(0.0F))
        .uv(63, 124).cuboid(14.0F, -2.0F, -47.0F, 2.0F, 2.0F, 28.0F, new Dilation(0.0F))
        .uv(94, 76).cuboid(-8.0F, -36.0F, -21.0F, 16.0F, 4.0F, 2.0F, new Dilation(0.2F))
        .uv(52, 39).cuboid(-8.0F, -31.6F, -21.0F, 3.0F, 4.0F, 2.0F, new Dilation(0.2F))
        .uv(52, 39).mirrored().cuboid(5.0F, -31.6F, -21.0F, 3.0F, 4.0F, 2.0F, new Dilation(0.2F)).mirrored(false)
        .uv(102, 82).cuboid(9.5F, -37.0F, -20.5F, 9.0F, 9.0F, 2.0F, new Dilation(0.0F))
        .uv(102, 82).mirrored().cuboid(-18.5F, -37.0F, -20.5F, 9.0F, 9.0F, 2.0F, new Dilation(0.0F)).mirrored(false)
        .uv(13, 2).cuboid(-17.5F, -36.0F, -20.4F, 7.0F, 7.0F, 0.0F, new Dilation(0.2F))
        .uv(13, 2).mirrored().cuboid(10.5F, -36.0F, -20.4F, 7.0F, 7.0F, 0.0F, new Dilation(0.2F)).mirrored(false)
        .uv(63, 124).mirrored().cuboid(-16.0F, -2.0F, -47.0F, 2.0F, 2.0F, 28.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.pivot(0.0F, 24.0F, 0.0F));

        ModelPartData cube_r1 = jake.addChild("cube_r1", ModelPartBuilder.create().uv(87, 49).mirrored().cuboid(-19.0F, -2.0F, -9.0F, 2.0F, 2.0F, 9.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(18.0F, -3.0782F, 27.4572F, 0.3491F, 0.0F, 0.0F));

        ModelPartData cube_r2 = jake.addChild("cube_r2", ModelPartBuilder.create().uv(64, 125).mirrored().cuboid(-1.0F, -11.0F, -1.0F, 2.0F, 28.0F, 2.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(-20.0F, -20.0F, -10.0F, -0.6471F, 0.1059F, 0.139F));

        ModelPartData cube_r3 = jake.addChild("cube_r3", ModelPartBuilder.create().uv(64, 125).cuboid(-1.0F, -11.0F, -1.0F, 2.0F, 28.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(20.0F, -20.0F, -10.0F, -0.6471F, -0.1059F, -0.139F));

        ModelPartData door = jake.addChild("door", ModelPartBuilder.create().uv(5, 124).cuboid(-8.0F, -32.0F, -19.5F, 16.0F, 32.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));
        return TexturedModelData.of(modelData, 256, 256);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red,
                       float green, float blue, float alpha) {
        jake.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
    }

    @Override
    public void renderWithAnimations(ClientTardis tardis, ExteriorBlockEntity exterior, ModelPart root, MatrixStack matrices,
                                     VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float pAlpha) {
        matrices.push();
        matrices.translate(0, -1.5f, 0);
        jake.getChild("door").pivotZ = tardis.door().isOpen() ? 2.0f : 0f;

        super.renderWithAnimations(tardis, exterior, root, matrices, vertices, light, overlay, red, green, blue, pAlpha);
        matrices.pop();
    }

    @Override
    public <T extends Entity & Linkable> void renderEntity(T falling, ModelPart root, MatrixStack matrices,
                                                           VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
        matrices.push();

        matrices.translate(0, -1.5f, 0);

        super.renderEntity(falling, root, matrices, vertexConsumer, light, overlay, red, green, blue, alpha);

        matrices.pop();
    }

    @Override
    public ModelPart getPart() {
        return jake;
    }

    @Override
    public void renderDoors(ClientTardis tardis, ExteriorBlockEntity exterior, ModelPart root, MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float pAlpha, boolean isBOTI) {

    }
}