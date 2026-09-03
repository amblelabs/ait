package dev.amble.ait.client.models.consoles;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;

import dev.amble.ait.client.models.AnimatedModel;
import dev.amble.ait.client.tardis.ClientTardis;
import dev.amble.ait.core.blockentities.ConsoleBlockEntity;
import dev.amble.ait.core.tardis.handler.travel.TravelHandlerBase;

public interface ConsoleModel extends AnimatedModel<ConsoleBlockEntity> {
    void animateBlockEntity(ConsoleBlockEntity console, TravelHandlerBase.State state, boolean hasPower);

    /**
     * Draws the model again for a second render layer in the same frame, without re-running the
     * control state.
     *
     * <p>A console is drawn twice a frame, once on its base layer and once on its emission layer, and
     * the state a model applies to its parts does not depend on which layer is being drawn. Running it
     * twice was not only wasted work: {@code resetTransform} runs once a frame while the state ran per
     * layer, so a read-modify-write like {@code part.pivotY = part.pivotY + 1} landed twice and the
     * glow was drawn on a differently posed model than the geometry beneath it.
     *
     * <p>The default keeps the old behaviour for implementors that have no state to skip.
     */
    default void renderGeometryOnly(ClientTardis tardis, ConsoleBlockEntity console, ModelPart root,
            MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green,
            float blue, float alpha, float tickDelta) {
        this.renderWithAnimations(tardis, console, root, matrices, vertices, light, overlay, red, green, blue,
                alpha, tickDelta);
    }
}
