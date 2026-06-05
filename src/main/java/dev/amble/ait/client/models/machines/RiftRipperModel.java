package dev.amble.ait.client.models.machines;

import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;

public class RiftRipperModel extends SinglePartEntityModel {
	public final ModelPart base;
	public final ModelPart curve;
	public final ModelPart claws;
	public final ModelPart symbol;
	public RiftRipperModel(ModelPart root) {
		this.base = root.getChild("base");
		this.curve = this.base.getChild("curve");
		this.claws = this.base.getChild("claws");
		this.symbol = this.base.getChild("symbol");
	}
	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		ModelPartData base = modelPartData.addChild("base", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 24.0F, 0.0F));

		ModelPartData curve = base.addChild("curve", ModelPartBuilder.create().uv(0, 53).cuboid(-5.0F, -4.25F, 2.0F, 10.0F, 8.0F, 10.0F, new Dilation(0.0F))
				.uv(107, 24).cuboid(-3.0F, -3.25F, 12.0F, 6.0F, 6.0F, 2.0F, new Dilation(0.0F))
				.uv(0, 17).cuboid(15.4853F, -27.4853F, 2.0F, 8.0F, 27.0F, 8.0F, new Dilation(0.01F))
				.uv(33, 17).cuboid(-23.4853F, -27.4853F, 2.0F, 8.0F, 27.0F, 8.0F, new Dilation(0.01F)), ModelTransform.pivot(0.0F, -8.0F, -6.0F));

		ModelPartData cube_r1 = curve.addChild("cube_r1", ModelPartBuilder.create().uv(66, 34).cuboid(5.0F, -2.0F, -4.0F, 12.0F, 8.0F, 8.0F, new Dilation(0.015F)), ModelTransform.of(10.0503F, -38.1213F, 6.0F, 0.0F, 0.0F, 0.7854F));

		ModelPartData cube_r2 = curve.addChild("cube_r2", ModelPartBuilder.create().uv(66, 17).cuboid(-17.0F, -2.0F, -4.0F, 12.0F, 8.0F, 8.0F, new Dilation(0.015F)), ModelTransform.of(-10.0503F, -38.1213F, 6.0F, 0.0F, 0.0F, -0.7854F));

		ModelPartData cube_r3 = curve.addChild("cube_r3", ModelPartBuilder.create().uv(41, 53).cuboid(-17.0F, -6.0F, -4.0F, 12.0F, 8.0F, 8.0F, new Dilation(0.0F)), ModelTransform.of(-10.0503F, 10.1213F, 6.0F, 0.0F, 0.0F, 0.7854F));

		ModelPartData cube_r4 = curve.addChild("cube_r4", ModelPartBuilder.create().uv(53, 0).cuboid(5.0F, -6.0F, -4.0F, 12.0F, 8.0F, 8.0F, new Dilation(0.0F)), ModelTransform.of(10.0503F, 10.1213F, 6.0F, 0.0F, 0.0F, -0.7854F));

		ModelPartData cube_r5 = curve.addChild("cube_r5", ModelPartBuilder.create().uv(41, 70).cuboid(8.0F, -5.0F, -4.0F, 12.0F, 8.0F, 8.0F, new Dilation(-0.01F)), ModelTransform.of(-23.3231F, 6.0699F, 6.0F, 0.0038F, -0.0872F, -0.1747F));

		ModelPartData cube_r6 = curve.addChild("cube_r6", ModelPartBuilder.create().uv(0, 72).cuboid(-18.75F, -5.25F, -4.0F, 12.0F, 8.0F, 8.0F, new Dilation(-0.01F)), ModelTransform.of(22.0512F, 6.1546F, 6.0F, 0.0038F, 0.0872F, 0.1747F));

		ModelPartData cube_r7 = curve.addChild("cube_r7", ModelPartBuilder.create().uv(0, 0).cuboid(-12.0F, -8.0F, -1.0F, 24.0F, 14.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, -0.3491F, 0.0F, 0.0F));

		ModelPartData claws = base.addChild("claws", ModelPartBuilder.create(), ModelTransform.pivot(14.5355F, -38.5355F, 0.0F));

		ModelPartData cube_r8 = claws.addChild("cube_r8", ModelPartBuilder.create().uv(94, 4).cuboid(0.0F, -15.0F, 0.0F, 12.0F, 12.0F, 0.0F, new Dilation(0.005F)), ModelTransform.of(0.0F, 6.0F, 0.0F, 0.0F, 0.0F, -1.1345F));

		ModelPartData cube_r9 = claws.addChild("cube_r9", ModelPartBuilder.create().uv(94, 4).mirrored().cuboid(-12.0F, -16.0F, 0.0F, 12.0F, 12.0F, 0.0F, new Dilation(0.005F)).mirrored(false), ModelTransform.of(-29.0711F, 6.0F, 0.0F, 0.0F, 0.0F, 1.1345F));

		ModelPartData cube_r10 = claws.addChild("cube_r10", ModelPartBuilder.create().uv(94, 4).mirrored().cuboid(-6.0F, -13.0F, -1.0F, 12.0F, 12.0F, 0.0F, new Dilation(0.005F)).mirrored(false), ModelTransform.of(-29.0711F, 0.0F, 0.0F, 0.0F, 1.5708F, 1.1345F));

		ModelPartData cube_r11 = claws.addChild("cube_r11", ModelPartBuilder.create().uv(94, 4).cuboid(-6.0F, -12.0F, -1.0F, 12.0F, 12.0F, 0.0F, new Dilation(0.005F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.5708F, -1.1345F));

		ModelPartData symbol = base.addChild("symbol", ModelPartBuilder.create().uv(82, 51).cuboid(-11.0F, -13.0F, 0.0F, 22.0F, 22.0F, 0.0F, new Dilation(0.001F)), ModelTransform.pivot(0.0F, -22.0F, 0.0F));
		return TexturedModelData.of(modelData, 128, 128);
	}

	@Override
	public ModelPart getPart() {
		return base;
	}

	@Override
	public void setAngles(Entity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {

	}

	@Override
	public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
		this.base.getChild("symbol").visible = false;
		this.base.render(matrices, vertices, light, overlay, red, green, blue, alpha);
	}
}