package dev.amble.ait.client.models;

import dev.amble.ait.api.tardis.link.v2.block.AbstractLinkableBlockEntity;
import dev.amble.ait.client.tardis.ClientTardis;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;

public interface AnimatedModel {
	void renderWithAnimations(ClientTardis tardis, AbstractLinkableBlockEntity linkableBlockEntity, ModelPart root, MatrixStack matrices,
	                          VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float pAlpha, float tickDelta);

	default void render(MatrixStack stack, VertexConsumer buffer, int maxLightCoordinate, int defaultUv, float base, float base1, float base2, float v) {
		ModelPart root = getPart();
		root.render(stack, buffer, maxLightCoordinate, defaultUv, base, base1, base2, v);
	}

	/**
	 * @return the root model part
	 */
	ModelPart getPart();
}
