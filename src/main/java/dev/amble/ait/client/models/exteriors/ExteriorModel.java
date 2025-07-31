package dev.amble.ait.client.models.exteriors;

import dev.amble.ait.api.tardis.link.v2.Linkable;
import dev.amble.ait.api.tardis.link.v2.block.AbstractLinkableBlockEntity;
import dev.amble.ait.client.models.AnimatedModel;
import dev.amble.ait.client.tardis.ClientTardis;
import dev.amble.ait.core.blockentities.ExteriorBlockEntity;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;

public interface ExteriorModel extends AnimatedModel {
	<T extends Entity & Linkable> void renderEntity(T falling, ModelPart root, MatrixStack matrices,
	                                                VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha);

	void renderDoors(ClientTardis tardis, ExteriorBlockEntity exterior, ModelPart root, MatrixStack matrices,
	                                 VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float pAlpha, boolean isBOTI);

	@Override
	default void renderWithAnimations(ClientTardis tardis, AbstractLinkableBlockEntity linkableBlockEntity, ModelPart root, MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float pAlpha, float tickDelta) {
		if (linkableBlockEntity instanceof ExteriorBlockEntity be) {
			renderWithAnimations(tardis, be, root, matrices, vertices, light, overlay, red, green, blue, pAlpha);
		}
	}

	void renderWithAnimations(ClientTardis tardis, ExteriorBlockEntity linkableBlockEntity, ModelPart root, MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float pAlpha);

	default void render(MatrixStack stack, VertexConsumer buffer, int maxLightCoordinate, int defaultUv, float base, float base1, float base2, float v) {
		ModelPart root = getPart();
		root.render(stack, buffer, maxLightCoordinate, defaultUv, base, base1, base2, v);
	}
}
