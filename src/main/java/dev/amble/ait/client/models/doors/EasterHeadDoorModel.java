package dev.amble.ait.client.models.doors;


import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;

import dev.amble.ait.api.tardis.link.v2.block.AbstractLinkableBlockEntity;
import dev.amble.ait.client.tardis.ClientTardis;

public class EasterHeadDoorModel extends DoorModel {
    private final ModelPart bottom;

    public EasterHeadDoorModel(ModelPart root) {
        this.bottom = root.getChild("bottom");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData bottom = modelPartData.addChild("bottom", ModelPartBuilder.create(),
                ModelTransform.pivot(0.0F, 54.0F, 0.0F));

        bottom.addChild("cube_r1", ModelPartBuilder.create().uv(0, 0).cuboid(-12.0F, 30.0F, -12.0F, 24.0F, 14.0F, 24.0F,
                new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -3.1416F));
        bottom.addChild("door",
                ModelPartBuilder.create().uv(8, 1).mirrored()
                        .cuboid(-9.0F, -30.0F, -8.0F, 18.0F, 0.0F, 19.0F, new Dilation(0.005F)).mirrored(false),
                ModelTransform.pivot(0.0F, 0.0F, 0.0F));
        return TexturedModelData.of(modelData, 256, 256);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red,
            float green, float blue, float alpha) {
        bottom.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
    }

    @Override
    public void renderWithAnimations(ClientTardis tardis, AbstractLinkableBlockEntity linkableBlockEntity, ModelPart root, MatrixStack matrices,
                                     VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float pAlpha, float tickDelta) {
        matrices.push();
        matrices.translate(0, -1.5f, 0);

        this.bottom.pivotY = tardis.door().isOpen() ? 22 : 54;

        super.renderWithAnimations(tardis, linkableBlockEntity, root, matrices, vertices, light, overlay, red, green, blue, pAlpha, tickDelta);

        matrices.pop();
    }

    @Override
    public ModelPart getPart() {
        return bottom;
    }

}
