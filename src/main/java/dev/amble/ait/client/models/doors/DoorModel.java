package dev.amble.ait.client.models.doors;

import java.util.function.Function;

import dev.amble.ait.api.tardis.link.v2.block.AbstractLinkableBlockEntity;
import dev.amble.ait.client.AITModClient;
import dev.amble.ait.client.models.AnimatedModel;
import dev.amble.ait.client.tardis.ClientTardis;
import dev.amble.ait.core.blockentities.DoorBlockEntity;
import dev.amble.ait.core.tardis.handler.DoorHandler;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;

@SuppressWarnings("rawtypes")
public abstract class DoorModel extends SinglePartEntityModel implements AnimatedModel<DoorBlockEntity> {

    public static String TEXTURE_PATH = "textures/blockentities/exteriors/";

    public DoorModel() {
        this(RenderLayer::getEntityCutoutNoCull);
    }

    public DoorModel(Function<Identifier, RenderLayer> function) {
        super(function);
    }

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

    public static float leftRot(DoorHandler door, float tickDelta) {
        return rotation(door.getLeftTicks(), door.isLeftOpen(), tickDelta);
    }

    public static float rightRot(DoorHandler door, float tickDelta) {
        return rotation(door.getRightTicks(), door.isRightOpen(), tickDelta);
    }

    private static float rotation(int remaining, boolean opening, float tickDelta) {
        if (!AITModClient.CONFIG.animateDoors) {
            return opening ? 1.0f : 0.0f;
        }

        float left = Math.max((float) remaining - tickDelta, 0.0f);
        float t;

        if (opening) {
            t = 1.0f - (left / (float) DoorHandler.OPEN_TICKS);
        } else {
            t = 1.0f - (left / (float) DoorHandler.CLOSE_TICKS);
        }

        t = Math.max(0.0f, Math.min(1.0f, t));

        float v = t * t * (3.0f - 2.0f * t);

        if (opening) {
            return v;
        } else {
            return 1.0f - v;
        }
    }

    @Override
    public void setAngles(Entity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw,
            float headPitch) {
    }
}
