package dev.amble.ait.client.models.doors;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;

import dev.amble.ait.api.tardis.link.v2.block.AbstractLinkableBlockEntity;
import dev.amble.ait.client.models.AnimatedModel;
import dev.amble.ait.client.tardis.ClientTardis;
import dev.amble.ait.core.blockentities.DoorBlockEntity;
import dev.amble.lib.client.model.BlockEntityModel;

public abstract class DoorModel extends BlockEntityModel implements AnimatedModel<DoorBlockEntity> {

    public static String TEXTURE_PATH = "textures/blockentities/exteriors/";

    @Override
    public void renderWithAnimations(ClientTardis tardis, DoorBlockEntity linkableBlockEntity, ModelPart root, MatrixStack matrices,
                                     VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float pAlpha, float tickDelta) {
        renderWithAnimations(tardis, ((AbstractLinkableBlockEntity) linkableBlockEntity) , root, matrices, vertices, light, overlay, red, green, blue, pAlpha, tickDelta);
    }

    // Overloaded method for compatibility with older code
    public void renderWithAnimations(ClientTardis tardis, AbstractLinkableBlockEntity linkableBlockEntity, ModelPart root, MatrixStack matrices,
                                     VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float pAlpha, float tickDelta) {
        assert linkableBlockEntity instanceof DoorBlockEntity : "Expected DoorBlockEntity, got " + linkableBlockEntity.getClass().getSimpleName();

        root.render(matrices, vertices, light, overlay, red, green, blue, pAlpha);
    }
}
