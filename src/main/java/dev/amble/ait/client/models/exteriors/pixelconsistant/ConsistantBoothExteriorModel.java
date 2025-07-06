// Made with Blockbench 4.12.5
// Exported for Minecraft version 1.17+ for Yarn
// Paste this class into your mod and generate all required imports

package dev.amble.ait.client.models.exteriors.pixelconsistant;

import dev.amble.ait.api.tardis.link.v2.Linkable;
import dev.amble.ait.client.AITModClient;
import dev.amble.ait.client.models.exteriors.ExteriorModel;
import dev.amble.ait.client.tardis.ClientTardis;
import dev.amble.ait.core.blockentities.ExteriorBlockEntity;
import dev.amble.ait.core.tardis.handler.DoorHandler;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.animation.Animation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;

public class ConsistantBoothExteriorModel extends ExteriorModel {
	private final ModelPart box;

	public ConsistantBoothExteriorModel(ModelPart root) {
		this.box = root.getChild("box");
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		ModelPartData box = modelPartData.addChild("box", ModelPartBuilder.create().uv(76, 57).cuboid(-7.5F, -18.5F, -9.5F, 15.0F, 2.0F, 2.0F, new Dilation(0.0F))
		.uv(0, 74).cuboid(-9.0F, -21.5F, -9.0F, 18.0F, 3.0F, 18.0F, new Dilation(0.1F))
		.uv(0, 22).cuboid(-9.5F, -28.5F, -9.5F, 19.0F, 7.0F, 19.0F, new Dilation(0.0F))
		.uv(0, 48).cuboid(-9.5F, -28.5F, -9.5F, 19.0F, 7.0F, 19.0F, new Dilation(0.25F))
		.uv(0, 0).cuboid(-10.5F, 16.5F, -10.5F, 21.0F, 1.0F, 21.0F, new Dilation(0.0F))
		.uv(60, 95).cuboid(7.5F, -18.5F, -9.5F, 2.0F, 35.0F, 2.0F, new Dilation(0.0F))
		.uv(76, 61).cuboid(7.5F, -18.5F, -9.5F, 2.0F, 2.0F, 2.0F, new Dilation(0.1F))
		.uv(76, 61).mirrored().cuboid(-9.5F, -18.5F, -9.5F, 2.0F, 2.0F, 2.0F, new Dilation(0.1F)).mirrored(false)
		.uv(60, 95).mirrored().cuboid(-9.5F, -18.5F, -9.5F, 2.0F, 35.0F, 2.0F, new Dilation(0.0F)).mirrored(false)
		.uv(60, 95).mirrored().cuboid(7.5F, -18.5F, 7.5F, 2.0F, 35.0F, 2.0F, new Dilation(0.0F)).mirrored(false)
		.uv(60, 95).cuboid(-9.5F, -18.5F, 7.5F, 2.0F, 35.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 6.5F, 0.0F));

		ModelPartData cube_r1 = box.addChild("cube_r1", ModelPartBuilder.create().uv(76, 57).cuboid(-8.0F, -1.0F, -1.0F, 15.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(-8.5F, -17.5F, -0.5F, 0.0F, 1.5708F, 0.0F));

		ModelPartData cube_r2 = box.addChild("cube_r2", ModelPartBuilder.create().uv(76, 57).cuboid(-6.5F, -1.0F, -9.5F, 15.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -17.5F, -1.0F, 0.0F, -1.5708F, 0.0F));

		ModelPartData cube_r3 = box.addChild("cube_r3", ModelPartBuilder.create().uv(76, 57).cuboid(-7.5F, -1.0F, -1.0F, 15.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -17.5F, 8.5F, 0.0F, 3.1416F, 0.0F));

		ModelPartData cube_r4 = box.addChild("cube_r4", ModelPartBuilder.create().uv(76, 61).mirrored().cuboid(-9.5F, -1.0F, -9.5F, 2.0F, 2.0F, 2.0F, new Dilation(0.1F)).mirrored(false), ModelTransform.of(0.0F, -17.5F, 0.0F, 0.0F, 1.5708F, 0.0F));

		ModelPartData cube_r5 = box.addChild("cube_r5", ModelPartBuilder.create().uv(76, 61).cuboid(7.5F, -1.0F, -9.5F, 2.0F, 2.0F, 2.0F, new Dilation(0.1F))
		.uv(76, 22).cuboid(-7.5F, 1.0F, -10.0F, 15.0F, 33.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -17.5F, 0.0F, 0.0F, -1.5708F, 0.0F));

		ModelPartData cube_r6 = box.addChild("cube_r6", ModelPartBuilder.create().uv(30, 95).cuboid(-7.5F, -16.5F, 5.0F, 15.0F, 33.0F, 0.0F, new Dilation(0.0F))
		.uv(106, 61).cuboid(-7.5F, -16.5F, -1.0F, 15.0F, 33.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 9.0F, 0.0F, 3.1416F, 0.0F));

		ModelPartData cube_r7 = box.addChild("cube_r7", ModelPartBuilder.create().uv(0, 95).mirrored().cuboid(-7.5F, -16.5F, 0.0F, 15.0F, 33.0F, 0.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(9.0F, 0.0F, 0.0F, 0.0F, 1.5708F, 0.0F));

		ModelPartData cube_r8 = box.addChild("cube_r8", ModelPartBuilder.create().uv(0, 95).cuboid(-8.0F, -16.5F, 0.0F, 15.0F, 33.0F, 0.0F, new Dilation(0.0F))
		.uv(76, 22).cuboid(-8.0F, -16.5F, -1.0F, 15.0F, 33.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(-9.0F, 0.0F, -0.5F, 0.0F, 1.5708F, 0.0F));

		ModelPartData door = box.addChild("door", ModelPartBuilder.create().uv(72, 74).cuboid(-0.1F, -16.0F, -1.0F, 15.0F, 33.0F, 2.0F, new Dilation(0.0F))
		.uv(76, 65).cuboid(13.9F, -3.0F, -2.0F, 1.0F, 3.0F, 1.0F, new Dilation(0.0F))
		.uv(0, 95).cuboid(-0.1F, -16.0F, 0.0F, 15.0F, 33.0F, 0.0F, new Dilation(0.0F)), ModelTransform.pivot(-7.4F, -0.5F, -9.0F));
		return TexturedModelData.of(modelData, 256, 256);
	}
	@Override
	public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red,
					   float green, float blue, float alpha) {
		box.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
	}

	@Override
	public void renderWithAnimations(ClientTardis tardis, ExteriorBlockEntity exterior, ModelPart root, MatrixStack matrices,
									 VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float pAlpha) {
		matrices.push();
		matrices.scale(1f, 1f, 1f);
		matrices.translate(0, -1.5f, 0);
		this.renderDoors(tardis, exterior, root, matrices, vertices, light, overlay, red, green, blue, pAlpha, false);

		super.renderWithAnimations(tardis, exterior, root, matrices, vertices, light, overlay, red, green, blue, pAlpha);
		matrices.pop();
	}

	@Override
	public Animation getAnimationForDoorState(DoorHandler.AnimationDoorState state) {
		return Animation.Builder.create(0).build();
	}

	@Override
	public void renderDoors(ClientTardis tardis, ExteriorBlockEntity exterior, ModelPart root, MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float pAlpha, boolean isBOTI) {
		if (!AITModClient.CONFIG.animateDoors)
			this.box.getChild("door").yaw = tardis.door().isOpen() ? 1.575F : 0.0F;
		else {
			float maxRot = 90f;
			this.box.getChild("door").yaw = (float) Math.toRadians(maxRot * tardis.door().getLeftRot());
		}

		if (isBOTI) {
			matrices.push();
			matrices.scale(1f, 1f, 1f);
			matrices.translate(0, -1.5f, 0);
			this.box.getChild("door").render(matrices, vertices, light, overlay, red, green, blue, pAlpha);
			matrices.pop();
		}
	}

	@Override
	public <T extends Entity & Linkable> void renderEntity(T falling, ModelPart root, MatrixStack matrices,
														   VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
		if (falling.tardis().isEmpty())
			return;

		matrices.push();
		if (!AITModClient.CONFIG.animateDoors)
			this.box.getChild("door").yaw = falling.tardis().get().door().isOpen() ? 1.575F : 0.0F;
		else {
			float maxRot = 90f;
			this.box.getChild("door").yaw = (float) Math.toRadians(maxRot * falling.tardis().get().door().getLeftRot());
		}
		matrices.scale(1f, 1f, 1f);
		matrices.translate(0, -1.5f, 0);

		super.renderEntity(falling, root, matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
		matrices.pop();
	}

	@Override
	public ModelPart getPart() {
		return box;
	}
}
