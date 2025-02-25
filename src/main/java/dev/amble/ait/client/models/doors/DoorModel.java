package dev.amble.ait.client.models.doors;

import java.util.function.Function;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.animation.Animation;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;

import dev.amble.ait.api.link.v2.block.AbstractLinkableBlockEntity;
import dev.amble.ait.core.tardis.handler.DoorHandler;

@SuppressWarnings("rawtypes")
public abstract class DoorModel extends SinglePartEntityModel {

    public static String TEXTURE_PATH = "textures/blockentities/exteriors/";

    public DoorModel() {
        this(RenderLayer::getEntityCutoutNoCull);
    }

    public DoorModel(Function<Identifier, RenderLayer> function) {
        super(function);
    }

    public void renderWithAnimations(AbstractLinkableBlockEntity linkableBlockEntity, ModelPart root, MatrixStack matrices,
                                     VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float pAlpha) {
        if (linkableBlockEntity.tardis().isEmpty())
            return;

        root.render(matrices, vertices, light, overlay, red, green, blue, pAlpha);
    }

    @Override
    public void setAngles(Entity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw,
            float headPitch) {
    }

    public abstract Animation getAnimationForDoorState(DoorHandler.AnimationDoorState state);
}
