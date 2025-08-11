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

public class CoralSeatModel extends Model {
	private final ModelPart chair;

	public CoralSeatModel(ModelPart root) {
		super(RenderLayer::getEntityCutout);
		this.chair = root.getChild("chair");
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		ModelPartData chair = modelPartData.addChild("chair", ModelPartBuilder.create().uv(0, 25).cuboid(-8.0F, -9.5F, -8.0F, 16.0F, 2.0F, 16.0F, new Dilation(0.0F))
		.uv(48, 92).cuboid(-6.0F, -8.0F, -6.0F, 12.0F, 4.0F, 12.0F, new Dilation(0.0F))
		.uv(68, 20).cuboid(-6.0F, -5.0F, -9.0F, 12.0F, 0.0F, 3.0F, new Dilation(0.0F))
		.uv(68, 20).cuboid(-6.0F, -5.0F, -9.0F, 12.0F, 0.0F, 3.0F, new Dilation(0.0F))
		.uv(64, 25).cuboid(-9.0F, -8.5F, -7.0F, 18.0F, 0.0F, 14.0F, new Dilation(0.0F))
		.uv(0, 0).cuboid(-22.0F, -11.5F, 8.4F, 44.0F, 3.0F, 0.0F, new Dilation(0.0F))
		.uv(68, 3).cuboid(-5.0F, 1.0F, -5.0F, 10.0F, 1.0F, 10.0F, new Dilation(0.0F))
		.uv(0, 79).cuboid(-1.0F, -4.0F, -1.0F, 2.0F, 5.0F, 2.0F, new Dilation(0.1F)), ModelTransform.pivot(0.0F, 22.0F, 0.0F));

		ModelPartData cube_r1 = chair.addChild("cube_r1", ModelPartBuilder.create().uv(1, 99).cuboid(-4.0F, -11.5F, -0.5F, 8.0F, 5.0F, 0.0F, new Dilation(0.0F))
		.uv(0, 98).cuboid(-4.0F, -6.5F, -0.5F, 8.0F, 0.0F, 1.0F, new Dilation(0.0F))
		.uv(0, 104).cuboid(-4.0F, -11.5F, -0.5F, 0.0F, 5.0F, 1.0F, new Dilation(0.0F))
		.uv(0, 88).cuboid(-1.0F, -6.4F, -1.0F, 2.0F, 8.0F, 2.0F, new Dilation(0.1F)), ModelTransform.of(0.55F, -1.5F, -2.6F, 1.5708F, -0.2618F, 0.0F));

		ModelPartData cube_r2 = chair.addChild("cube_r2", ModelPartBuilder.create().uv(0, 104).cuboid(4.0F, -11.5F, -0.5F, 0.0F, 5.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.5503F, -1.5F, -2.601F, 1.5708F, -0.2618F, 0.0F));

		ModelPartData cube_r3 = chair.addChild("cube_r3", ModelPartBuilder.create().uv(68, 14).mirrored().cuboid(-8.0F, -6.674F, 1.4744F, 16.0F, 2.0F, 4.0F, new Dilation(0.0F)).mirrored(false)
		.uv(64, 53).mirrored().cuboid(-8.0F, -4.674F, 1.4744F, 16.0F, 8.0F, 4.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(-17.0F, -13.7171F, 3.7186F, -0.0417F, -0.0436F, -0.0437F));

		ModelPartData cube_r4 = chair.addChild("cube_r4", ModelPartBuilder.create().uv(0, 43).mirrored().cuboid(-8.0F, 4.5171F, -11.7186F, 16.0F, 2.0F, 16.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(-17.0F, -13.7171F, 3.7186F, 0.0019F, -0.0436F, -0.0437F));

		ModelPartData cube_r5 = chair.addChild("cube_r5", ModelPartBuilder.create().uv(0, 66).cuboid(-8.0F, -6.674F, 1.4744F, 16.0F, 2.0F, 4.0F, new Dilation(0.0F))
		.uv(64, 53).cuboid(-8.0F, -4.674F, 1.4744F, 16.0F, 8.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(17.0F, -13.7171F, 3.7186F, -0.0417F, 0.0436F, 0.0437F));

		ModelPartData cube_r6 = chair.addChild("cube_r6", ModelPartBuilder.create().uv(0, 110).cuboid(-8.0F, 4.5171F, -11.7186F, 16.0F, 2.0F, 16.0F, new Dilation(0.0F)), ModelTransform.of(17.0F, -13.7171F, 3.7186F, 0.0019F, 0.0436F, 0.0437F));

		ModelPartData cube_r7 = chair.addChild("cube_r7", ModelPartBuilder.create().uv(10, 91).cuboid(-6.5F, -0.5F, 1.5F, 2.0F, 1.0F, 1.0F, new Dilation(0.001F))
		.uv(18, 92).cuboid(-4.5F, -0.5F, -4.5F, 9.0F, 1.0F, 9.0F, new Dilation(0.001F)), ModelTransform.of(-3.5F, -14.5049F, 10.4169F, 1.3109F, -0.1623F, -0.546F));

		ModelPartData cube_r8 = chair.addChild("cube_r8", ModelPartBuilder.create().uv(14, 80).cuboid(-6.0F, 2.2F, -4.0F, 5.0F, 0.0F, 5.0F, new Dilation(0.001F))
		.uv(45, 75).cuboid(-8.0F, 2.1F, -9.0F, 16.0F, 0.0F, 18.0F, new Dilation(0.001F))
		.uv(0, 3).cuboid(-8.0F, -2.0F, -9.0F, 16.0F, 4.0F, 18.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, -16.7774F, 8.1975F, 1.2654F, 0.0F, 0.0F));
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
			matrices.translate(-0.1, 1.1f, 0);
			matrices.scale(0.55f, 0.55f, 0.55f);
		}

		if (renderMode == ModelTransformationMode.HEAD) {
			matrices.translate(0, -0.725f, 0);
			matrices.scale(2.725f, 2.725f, 2.725f);
			matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180));
		}
	}

	@Override
	public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
		chair.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
	}

}