package dev.amble.ait.client.renderers.sky;

import dev.amble.ait.client.models.machines.AstralMapModel;
import dev.amble.ait.core.blockentities.SkyBlockEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;

public class SkyBlockEntityRenderer<T extends SkyBlockEntity> implements BlockEntityRenderer<T> {
	public SkyBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
	}

	@Override
	public void render(T entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
		// todo - render sky :(
	}
}
