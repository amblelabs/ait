package dev.amble.ait.client.models;

import dev.amble.ait.client.tardis.ClientTardis;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;

public interface AnimatedModel<T extends BlockEntity> {
	void renderWithAnimations(ClientTardis tardis, T linkableBlockEntity, ModelPart root, MatrixStack matrices,
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
