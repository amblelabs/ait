package dev.amble.ait.client.models.consoles;

import java.util.function.Function;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.animation.Animation;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;

import dev.amble.ait.client.AITModClient;
import dev.amble.ait.client.tardis.ClientTardis;
import dev.amble.ait.core.blockentities.ConsoleBlockEntity;
import dev.amble.ait.core.tardis.Tardis;
import dev.amble.ait.core.tardis.handler.travel.TravelHandlerBase;

@SuppressWarnings("rawtypes")
public abstract class ConsoleModel extends SinglePartEntityModel {

    public ConsoleModel() {
        this(RenderLayer::getEntityCutoutNoCull);
    }

    public ConsoleModel(Function<Identifier, RenderLayer> function) {
        super(function);
    }

    public void animateBlockEntity(ConsoleBlockEntity console, TravelHandlerBase.State state, boolean hasPower) {
        // fyi, this is directly referencing camel animation code, its just specific
        // according to
        // the
        // block entity that
        // is being used
        // to detect different states. - Loqor
        this.getPart().traverse().forEach(ModelPart::resetTransform);

        if (hasPower && AITModClient.CONFIG.animateConsole)
            this.updateAnimation(console.ANIM_STATE, this.getAnimationForState(state), console.getAge());
    }

    public void renderWithAnimations(ConsoleBlockEntity console, ClientTardis tardis, ModelPart root, MatrixStack matrices,
                                     VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float pAlpha) {
        root.render(matrices, vertices, light, overlay, red, green, blue, pAlpha);
    }

    @Override
    public void setAngles(Entity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw,
            float headPitch) {
    }

    public abstract Animation getAnimationForState(TravelHandlerBase.State state);

    public void renderMonitorText(Tardis tardis, ConsoleBlockEntity entity, MatrixStack matrices,
                                  VertexConsumerProvider vertexConsumers, int light, int overlay) {
        // do nothing !! (i fucking hate programming) todo - @Loqor you added this feature ur implementing it for the rest
    }
}
