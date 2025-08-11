// Made with Blockbench 4.12.6
// Exported for Minecraft version 1.17+ for Yarn
// Paste this class into your mod and generate all required imports

package dev.amble.ait.client.models.decoration;

import net.minecraft.client.model.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.RotationAxis;

public class BrassStatueModel extends Model {

	private final ModelPart statue;


	public BrassStatueModel(ModelPart root) {
		super(RenderLayer::getEntityCutout);
		this.statue = root.getChild("statue");
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		ModelPartData statue = modelPartData.addChild("statue", ModelPartBuilder.create().uv(0, 0).cuboid(-6.1F, -42.2F, -0.3F, 2.0F, 4.0F, 2.0F, new Dilation(0.0F))
		.uv(56, 0).cuboid(-8.1F, -43.2F, -2.3F, 6.0F, 1.0F, 6.0F, new Dilation(0.0F))
		.uv(6, 67).cuboid(-6.0F, -1.0F, -6.0F, 12.0F, 1.0F, 12.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 24.0F, 0.0F));

		ModelPartData cube_r1 = statue.addChild("cube_r1", ModelPartBuilder.create().uv(63, 59).mirrored().cuboid(-4.0F, -5.5F, 0.0F, 8.0F, 8.0F, 0.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(-5.2716F, -45.7F, 0.6284F, 0.0F, -2.3562F, 0.0F));

		ModelPartData cube_r2 = statue.addChild("cube_r2", ModelPartBuilder.create().uv(63, 59).cuboid(-3.0F, -5.5F, 0.0F, 8.0F, 8.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(-5.9787F, -45.7F, -0.0787F, 0.0F, -0.7854F, 0.0F));

		ModelPartData Head = statue.addChild("Head", ModelPartBuilder.create().uv(0, 0).cuboid(-4.2371F, -13.6946F, -4.0748F, 8.0F, 8.0F, 8.0F, new Dilation(0.0F))
		.uv(32, 0).cuboid(-4.2371F, -13.6946F, -4.0748F, 8.0F, 8.0F, 8.0F, new Dilation(0.5F)), ModelTransform.of(0.4506F, -23.9803F, 1.4306F, 0.0F, 0.3054F, 0.0436F));

		ModelPartData body_front = statue.addChild("body_front", ModelPartBuilder.create().uv(56, 19).cuboid(-1.5F, -13.0F, 0.0F, 3.0F, 5.0F, 8.0F, new Dilation(0.3F)), ModelTransform.pivot(2.0F, 7.0F, -3.0F));

		ModelPartData body_back = body_front.addChild("body_back", ModelPartBuilder.create().uv(56, 32).cuboid(-1.5F, -2.5F, -2.0F, 3.0F, 5.0F, 8.0F, new Dilation(0.2F)), ModelTransform.of(-0.3597F, -11.3843F, 8.8473F, 0.5335F, -0.3455F, -0.0458F));

		ModelPartData dorsal_back = body_back.addChild("dorsal_back", ModelPartBuilder.create().uv(58, 21).cuboid(0.5598F, -9.6957F, 2.7396F, 0.0F, 2.0F, 3.0F, new Dilation(0.0F)), ModelTransform.pivot(-0.2544F, 5.3606F, -3.1761F));

		ModelPartData tailfin = body_back.addChild("tailfin", ModelPartBuilder.create(), ModelTransform.of(-0.2544F, 10.3606F, 4.8239F, 0.0F, -0.0873F, 0.0F));

		ModelPartData tailfin_r1 = tailfin.addChild("tailfin_r1", ModelPartBuilder.create().uv(76, 29).cuboid(0.0F, -3.0F, 0.0F, 0.0F, 5.0F, 6.0F, new Dilation(0.0F)), ModelTransform.of(0.3559F, -10.3606F, 1.1495F, 0.6981F, 0.0F, 0.0F));

		ModelPartData dorsal_front = body_front.addChild("dorsal_front", ModelPartBuilder.create().uv(84, 47).cuboid(0.0F, -10.0F, 0.0F, 0.0F, 2.0F, 2.0F, new Dilation(0.2F)), ModelTransform.pivot(0.0F, -5.0F, 6.0F));

		ModelPartData head2 = body_front.addChild("head2", ModelPartBuilder.create().uv(78, 19).cuboid(-1.0F, -10.0F, -3.0F, 2.0F, 4.0F, 3.0F, new Dilation(0.2F)), ModelTransform.pivot(0.0F, -3.0F, 0.0F));

		ModelPartData leftFin = body_front.addChild("leftFin", ModelPartBuilder.create(), ModelTransform.of(1.5F, -1.0F, 0.0F, 0.0F, 0.0F, 0.6109F));

		ModelPartData leftFin_r1 = leftFin.addChild("leftFin_r1", ModelPartBuilder.create().uv(58, 19).cuboid(-1.0F, -0.7F, -1.0F, 2.0F, 0.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(-3.5886F, -6.5532F, 1.0F, 0.0F, 0.0F, -0.829F));

		ModelPartData rightFin = body_front.addChild("rightFin", ModelPartBuilder.create(), ModelTransform.of(-1.5F, -1.0F, 0.0F, 0.0F, 0.0F, -0.6109F));

		ModelPartData rightFin_r1 = rightFin.addChild("rightFin_r1", ModelPartBuilder.create().uv(54, 19).cuboid(-1.0F, -0.4F, -1.0F, 2.0F, 0.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(3.5885F, -6.5532F, 1.0F, 0.0F, 0.0F, 0.5672F));

		ModelPartData LeftLeg = statue.addChild("LeftLeg", ModelPartBuilder.create().uv(16, 48).cuboid(-2.0F, -5.7F, -2.0F, 4.0F, 12.0F, 4.0F, new Dilation(0.0F))
		.uv(0, 48).cuboid(-2.0F, -5.7F, -2.0F, 4.0F, 12.0F, 4.0F, new Dilation(0.25F)), ModelTransform.pivot(1.9F, -12.0F, 0.0F));

		ModelPartData RightLeg = statue.addChild("RightLeg", ModelPartBuilder.create().uv(0, 16).cuboid(-2.1286F, -5.652F, -1.2729F, 4.0F, 12.0F, 4.0F, new Dilation(0.0F))
		.uv(0, 32).cuboid(-2.1286F, -5.652F, -1.2729F, 4.0F, 12.0F, 4.0F, new Dilation(0.25F)), ModelTransform.of(-1.8739F, -12.026F, 0.2977F, 0.134F, 0.2608F, 0.0233F));

		ModelPartData LeftArm = statue.addChild("LeftArm", ModelPartBuilder.create().uv(32, 48).cuboid(-1.6669F, -3.0197F, -1.3899F, 3.0F, 12.0F, 4.0F, new Dilation(0.0F))
		.uv(48, 48).cuboid(-1.6669F, -3.0197F, -1.3899F, 3.0F, 12.0F, 4.0F, new Dilation(0.25F)), ModelTransform.of(5.483F, -25.4305F, 0.9091F, 0.5078F, -0.2201F, 0.143F));

		ModelPartData RightArm = statue.addChild("RightArm", ModelPartBuilder.create().uv(40, 16).cuboid(-2.2486F, 3.6946F, -2.0F, 3.0F, 12.0F, 4.0F, new Dilation(0.0F))
		.uv(40, 32).cuboid(-2.2486F, 3.6946F, -2.0F, 3.0F, 12.0F, 4.0F, new Dilation(0.25F)), ModelTransform.of(-5.0F, -24.0F, 1.0F, 3.1416F, 0.0F, 0.0436F));

		ModelPartData Body = statue.addChild("Body", ModelPartBuilder.create().uv(16, 16).cuboid(-4.0F, -5.6783F, -2.4968F, 8.0F, 12.0F, 4.0F, new Dilation(0.0F))
		.uv(16, 32).cuboid(-4.0F, -5.6783F, -2.4968F, 8.0F, 12.0F, 4.0F, new Dilation(0.25F)), ModelTransform.of(0.0F, -23.9216F, 0.8966F, -0.0873F, 0.0F, 0.0F));
		return TexturedModelData.of(modelData, 128, 128);
	}

	public void setAngles(MatrixStack matrices, ModelTransformationMode renderMode, boolean left) {
		if (renderMode == ModelTransformationMode.FIXED)
			return;
		matrices.translate(0.5, -1.25f, -0.5);
		matrices.scale(0.6f, 0.6f, 0.6f);

		if (renderMode == ModelTransformationMode.GUI) {
			matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(22.5f));
			matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(45f));
			matrices.translate(-0.1, 1.4f, 0);
			matrices.scale(0.4f, 0.4f, 0.4f);
		}

		if (renderMode == ModelTransformationMode.HEAD) {
			matrices.translate(0, -0.725f, 0);
			matrices.scale(2.725f, 2.725f, 2.725f);
			matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180));
		}
	}
	@Override
	public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
		statue.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
	}

}