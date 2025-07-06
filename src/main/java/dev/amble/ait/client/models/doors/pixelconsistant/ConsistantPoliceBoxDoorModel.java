package dev.amble.ait.client.models.doors.pixelconsistant;

import dev.amble.ait.api.tardis.link.v2.block.AbstractLinkableBlockEntity;
import dev.amble.ait.client.AITModClient;
import dev.amble.ait.client.models.doors.DoorModel;
import dev.amble.ait.client.tardis.ClientTardis;
import dev.amble.ait.client.util.ResourcePackUtil;
import dev.amble.ait.core.tardis.handler.DoorHandler;
import net.minecraft.client.model.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.animation.Animation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.RotationAxis;

// Made with Blockbench 4.12.5
// Exported for Minecraft version 1.17+ for Yarn
// Paste this class into your mod and generate all required imports
public class ConsistantPoliceBoxDoorModel extends DoorModel {
	private final ModelPart bone;

	public ConsistantPoliceBoxDoorModel(ModelPart root) {
		super(RenderLayer::getEntityCutoutNoCull);
		this.bone = root.getChild("bone");
	}
	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		ModelPartData bone = modelPartData.addChild("bone", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 26.0F, 9.0F));

		ModelPartData Doors = bone.addChild("Doors", ModelPartBuilder.create(), ModelTransform.pivot(-1.0F, -3.0F, -0.2F));

		ModelPartData left_door = Doors.addChild("left_door", ModelPartBuilder.create().uv(128, 25).cuboid(-9.0F, -17.0F, -1.0F, 9.0F, 33.0F, 1.0F, new Dilation(0.0F))
		.uv(2, 53).cuboid(-9.0F, -17.0F, -1.3F, 1.0F, 33.0F, 0.0F, new Dilation(0.0F))
		.uv(0, 0).cuboid(-7.9F, -1.0F, -1.7F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F))
		.uv(4, 3).cuboid(-7.9F, -1.2F, -1.7F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(10.0F, -15.0F, -9.8F));

		ModelPartData cube_r1 = left_door.addChild("cube_r1", ModelPartBuilder.create().uv(46, 136).cuboid(-0.55F, -14.75F, -0.05F, 1.0F, 33.0F, 0.0F, new Dilation(0.0F))
		.uv(4, 3).cuboid(-2.45F, 1.05F, -0.45F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F))
		.uv(0, 0).cuboid(-2.45F, -2.75F, -0.45F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-9.45F, -2.25F, 0.15F, -3.1416F, 0.0F, 3.1416F));

		ModelPartData right_door = Doors.addChild("right_door", ModelPartBuilder.create().uv(124, 126).cuboid(0.0F, -17.0F, -1.0F, 9.0F, 33.0F, 1.0F, new Dilation(0.0F))
		.uv(10, 0).cuboid(1.0F, -8.0F, 0.0F, 6.0F, 7.0F, 2.0F, new Dilation(0.0F))
		.uv(0, 53).cuboid(8.0F, -17.0F, -1.3F, 1.0F, 33.0F, 0.0F, new Dilation(0.0F))
		.uv(0, 3).cuboid(6.1F, -5.5F, -1.4F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F))
		.uv(4, 0).cuboid(1.0F, -4.5F, -1.4F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(-8.0F, -15.0F, -9.8F));

		ModelPartData cube_r2 = right_door.addChild("cube_r2", ModelPartBuilder.create().uv(48, 136).cuboid(-1.55F, -14.75F, -0.05F, 1.0F, 33.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(8.55F, -2.25F, 0.15F, -3.1416F, 0.0F, 3.1416F));

		ModelPartData phone = right_door.addChild("phone", ModelPartBuilder.create().uv(0, 12).cuboid(-0.5F, 0.3F, -0.8F, 2.0F, 2.0F, 1.0F, new Dilation(-0.1F))
		.uv(0, 8).cuboid(0.5F, -1.0F, -0.5F, 2.0F, 3.0F, 1.0F, new Dilation(0.0F))
		.uv(6, 8).cuboid(-0.5F, -1.0F, -0.5F, 1.0F, 2.0F, 1.0F, new Dilation(-0.1F)), ModelTransform.pivot(3.0F, -5.5F, 0.5F));

		ModelPartData TARDIS = bone.addChild("TARDIS", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData allexteriors = TARDIS.addChild("allexteriors", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData upperlip = allexteriors.addChild("upperlip", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, -36.0F, 0.0F));

		ModelPartData cube_r3 = upperlip.addChild("cube_r3", ModelPartBuilder.create().uv(38, 130).cuboid(-9.0F, -0.05F, 0.0F, 18.0F, 2.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -0.95F, -9.6F, -3.1416F, 0.0F, 3.1416F));

		ModelPartData cube_r4 = upperlip.addChild("cube_r4", ModelPartBuilder.create().uv(38, 130).cuboid(-9.0F, -1.0F, -0.5F, 18.0F, 2.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, -11.0F, 0.0F, 3.1416F, 0.0F));

		ModelPartData pillars = allexteriors.addChild("pillars", ModelPartBuilder.create().uv(76, 125).cuboid(19.5F, -20.0F, -1.5F, 3.0F, 39.0F, 3.0F, new Dilation(0.0F))
		.uv(76, 125).mirrored().cuboid(-1.5F, -20.0F, -1.5F, 3.0F, 39.0F, 3.0F, new Dilation(0.0F)).mirrored(false)
		.uv(244, 3).mirrored().cuboid(-1.5F, -20.5F, -1.5F, 3.0F, 39.0F, 3.0F, new Dilation(0.01F)).mirrored(false)
		.uv(244, 3).cuboid(19.5F, -20.5F, -1.5F, 3.0F, 39.0F, 3.0F, new Dilation(0.01F)), ModelTransform.pivot(-10.5F, -21.0F, -10.5F));

		ModelPartData walls = allexteriors.addChild("walls", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, -18.5F, 0.0F));

		ModelPartData base = allexteriors.addChild("base", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 2.0F, 1.0F));

		ModelPartData coral = TARDIS.addChild("coral", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData sign9 = coral.addChild("sign9", ModelPartBuilder.create().uv(144, 126).cuboid(-10.0F, -2.5F, -13.2F, 20.0F, 5.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -38.3F, 0.0F));

		ModelPartData roof9 = coral.addChild("roof9", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, -56.7F, 0.0F));

		ModelPartData lamp9 = coral.addChild("lamp9", ModelPartBuilder.create(), ModelTransform.pivot(-0.5F, -56.2F, 0.4F));

		ModelPartData toyota = TARDIS.addChild("toyota", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData roof11 = toyota.addChild("roof11", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, -40.7F, 0.0F));

		ModelPartData signs11 = toyota.addChild("signs11", ModelPartBuilder.create().uv(0, 221).cuboid(-10.0F, -2.5F, -13.0F, 20.0F, 5.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -38.3F, 0.0F));

		ModelPartData lamp11 = toyota.addChild("lamp11", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, -47.25F, 0.0F));

		ModelPartData latest = TARDIS.addChild("latest", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData sign13 = latest.addChild("sign13", ModelPartBuilder.create().uv(78, 0).cuboid(-10.0F, -2.5F, -13.0F, 20.0F, 5.0F, 2.0F, new Dilation(0.0F))
		.uv(78, 0).cuboid(-10.0F, -2.5F, -13.0F, 20.0F, 5.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -37.9F, 0.0F));

		ModelPartData cube_r5 = sign13.addChild("cube_r5", ModelPartBuilder.create().uv(214, 0).cuboid(-10.0F, -3.44F, -0.5F, 20.0F, 0.0F, 2.0F, new Dilation(0.0F))
		.uv(150, 249).cuboid(-10.0F, -3.45F, -1.5F, 20.0F, 5.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.95F, -9.6F, -3.1416F, 0.0F, 3.1416F));
		return TexturedModelData.of(modelData, 256, 256);
	}

	@Override
	public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red,
					   float green, float blue, float alpha) {
		bone.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
	}

	@Override
	public void renderWithAnimations(ClientTardis tardis, AbstractLinkableBlockEntity doorEntity, ModelPart root, MatrixStack matrices,
									 VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float pAlpha) {
		DoorHandler door = tardis.door();

		if (!AITModClient.CONFIG.animateDoors) {
			this.bone.getChild("Doors").getChild("left_door").yaw = (door.isLeftOpen() || door.isOpen()) ? -5F : 0.0F;
			this.bone.getChild("Doors").getChild("right_door").yaw = (door.isRightOpen() || door.areBothOpen())
					? 5F
					: 0.0F;
		} else {
			float maxRot = 90f;
			this.bone.getChild("Doors").getChild("left_door").yaw = (float) Math.toRadians(maxRot*door.getLeftRot());
			this.bone.getChild("Doors").getChild("right_door").yaw = (float) -Math.toRadians(maxRot*door.getRightRot());
		}

		matrices.push();
		if (ResourcePackUtil.isPixelConsistentPackActive()) {
			matrices.scale(1F, 1F, 1F);
			matrices.translate(0, -1.5, 0.35);
		} else {
			matrices.scale(0.63F, 0.63F, 0.63F);
			matrices.translate(0, -1.5, 0.35);
		}
		matrices.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(180));

		super.renderWithAnimations(tardis, doorEntity, root, matrices, vertices, light, overlay, red, green, blue, pAlpha);
		matrices.pop();
	}

	@Override
	public Animation getAnimationForDoorState(DoorHandler.AnimationDoorState state) {
		return Animation.Builder.create(0).build();
	}

	@Override
	public ModelPart getPart() {
		return bone;
	}
}