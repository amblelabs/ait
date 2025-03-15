package dev.amble.ait.client.models.exteriors;

import static dev.amble.ait.core.tardis.animation.ExteriorAnimation.*;

import java.util.function.Function;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.animation.Animation;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

import dev.amble.ait.api.link.v2.Linkable;
import dev.amble.ait.core.blockentities.ExteriorBlockEntity;
import dev.amble.ait.core.effects.ZeitonHighEffect;
import dev.amble.ait.core.tardis.Tardis;
import dev.amble.ait.core.tardis.handler.DoorHandler;
import dev.amble.ait.data.Loyalty;

@SuppressWarnings("rawtypes")
public abstract class ExteriorModel extends SinglePartEntityModel {

    public ExteriorModel() {
        this(RenderLayer::getEntityCutoutNoCull);
    }

    public ExteriorModel(Function<Identifier, RenderLayer> function) {
        super(function);
    }

    public void renderWithAnimations(ExteriorBlockEntity exterior, ModelPart root, MatrixStack matrices,
            VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        Tardis tardis = exterior.tardis().get();

        if (tardis == null)
            return;

        float newAlpha = alpha;

        if (tardis.cloak().cloaked().get() && !ZeitonHighEffect.isHigh(MinecraftClient.getInstance().player)) {
            PlayerEntity player = MinecraftClient.getInstance().player;

            if (!(tardis.loyalty().get(player).isOf(Loyalty.Type.COMPANION))) {
                newAlpha = 0f;

                root.render(matrices, vertices, light, overlay, red, green, blue, newAlpha);
                return;
            }

            if (isNearTardis(MinecraftClient.getInstance().player, tardis, MAX_CLOAK_DISTANCE)) {
                newAlpha = 1f - (float) (distanceFromTardis(player, tardis) / MAX_CLOAK_DISTANCE);

                if (alpha != 0.105f)
                    newAlpha = newAlpha * alpha;
            } else {
                newAlpha = 0f;
            }
        }

        root.render(matrices, vertices, light, overlay, red, green, blue, newAlpha);
    }

    public <T extends Entity & Linkable> void renderEntity(T falling, ModelPart root, MatrixStack matrices,
                                                           VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
        root.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
    }

    @Override
    public void setAngles(Entity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw,
            float headPitch) {
    }

    public abstract Animation getAnimationForDoorState(DoorHandler.AnimationDoorState state);

    public abstract void renderDoors(ExteriorBlockEntity exterior, ModelPart root, MatrixStack matrices,
                                     VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float pAlpha, boolean isBOTI);
}
