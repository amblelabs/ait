package dev.amble.ait.client.models.machines;

import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.entity.Entity;

public class ArtronCollectorModel extends SinglePartEntityModel {
	private final ModelPart main;
	private final ModelPart crystal;
	private final ModelPart metal;
	private final ModelPart Antenna;
	private final ModelPart Thing_3;
	private final ModelPart Thing_1;
	private final ModelPart Thing_4;
	private final ModelPart Thing_2;
	private final ModelPart Meter;
	private final ModelPart Light_1;
	private final ModelPart Light_2;
	private final ModelPart Light_4;
	private final ModelPart Light_3;
	public ArtronCollectorModel(ModelPart root) {
		this.main = root.getChild("main");
		this.crystal = this.main.getChild("crystal");
		this.metal = this.main.getChild("metal");
		this.Antenna = this.metal.getChild("Antenna");
		this.Thing_3 = this.metal.getChild("Thing 3");
		this.Thing_1 = this.metal.getChild("Thing 1");
		this.Thing_4 = this.metal.getChild("Thing 4");
		this.Thing_2 = this.metal.getChild("Thing 2");
		this.Meter = this.main.getChild("Meter");
		this.Light_1 = this.Meter.getChild("Light_1");
		this.Light_2 = this.Meter.getChild("Light_2");
		this.Light_4 = this.Meter.getChild("Light_4");
		this.Light_3 = this.Meter.getChild("Light_3");
	}
	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		ModelPartData main = modelPartData.addChild("main", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 24.0F, 0.0F));

		ModelPartData crystal = main.addChild("crystal", ModelPartBuilder.create().uv(40, 45).cuboid(-4.5F, -8.0299F, 3.75F, 8.0F, 18.0F, 0.0F, new Dilation(0.0F))
				.uv(0, 0).cuboid(-4.5F, -4.0299F, -4.5F, 8.0F, 14.0F, 8.0F, new Dilation(0.0F))
				.uv(32, 0).cuboid(-4.0F, -14.0299F, -4.0F, 7.0F, 10.0F, 7.0F, new Dilation(0.0F)), ModelTransform.pivot(0.5F, -9.9701F, 0.5F));

		ModelPartData cube_r1 = crystal.addChild("cube_r1", ModelPartBuilder.create().uv(64, 73).cuboid(-4.5F, -8.0297F, -0.0039F, 8.0F, 8.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(-4.75F, 0.0F, 0.0F, 0.0F, -1.5708F, -0.0436F));

		ModelPartData cube_r2 = crystal.addChild("cube_r2", ModelPartBuilder.create().uv(48, 73).cuboid(-4.5F, 1.3953F, 5.0193F, 8.0F, 8.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(-1.0F, 0.0F, 0.0F, 0.0F, -1.5708F, -0.1309F));

		ModelPartData cube_r3 = crystal.addChild("cube_r3", ModelPartBuilder.create().uv(0, 77).cuboid(-4.5F, -9.5145F, 4.1134F, 8.0F, 7.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(-1.0F, -5.0F, 0.0F, 0.0F, -1.5708F, 0.1309F));

		ModelPartData cube_r4 = crystal.addChild("cube_r4", ModelPartBuilder.create().uv(56, 47).cuboid(-4.5F, -8.0299F, 3.75F, 8.0F, 18.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(-1.0F, 0.0F, 0.0F, 0.0F, -1.5708F, 0.0F));

		ModelPartData cube_r5 = crystal.addChild("cube_r5", ModelPartBuilder.create().uv(80, 68).cuboid(-4.5F, -9.4764F, 3.0614F, 8.0F, 4.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(-1.0F, -3.0F, 0.0F, 0.0F, -1.5708F, -0.1309F));

		ModelPartData cube_r6 = crystal.addChild("cube_r6", ModelPartBuilder.create().uv(72, 56).cuboid(-4.0F, -8.2789F, -0.059F, 8.0F, 8.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(-4.7721F, 6.2543F, -0.5F, 0.0F, -1.5708F, -0.0873F));

		ModelPartData cube_r7 = crystal.addChild("cube_r7", ModelPartBuilder.create().uv(56, 29).cuboid(-3.5F, -8.0299F, 3.75F, 8.0F, 18.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.5708F, 0.0F));

		ModelPartData cube_r8 = crystal.addChild("cube_r8", ModelPartBuilder.create().uv(76, 7).cuboid(-3.5F, -9.5145F, 4.1134F, 8.0F, 7.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -5.0F, 0.0F, 0.0F, 1.5708F, -0.1309F));

		ModelPartData cube_r9 = crystal.addChild("cube_r9", ModelPartBuilder.create().uv(80, 64).cuboid(-3.5F, -9.4764F, 3.0614F, 8.0F, 4.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -3.0F, 0.0F, 0.0F, 1.5708F, 0.1309F));

		ModelPartData cube_r10 = crystal.addChild("cube_r10", ModelPartBuilder.create().uv(72, 48).cuboid(-4.0F, -8.2789F, -0.059F, 8.0F, 8.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(3.7721F, 6.2543F, -0.5F, 0.0F, 1.5708F, 0.0873F));

		ModelPartData cube_r11 = crystal.addChild("cube_r11", ModelPartBuilder.create().uv(72, 40).cuboid(-3.5F, -8.0297F, -0.0039F, 8.0F, 8.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(3.75F, 0.0F, 0.0F, 0.0F, 1.5708F, 0.0436F));

		ModelPartData cube_r12 = crystal.addChild("cube_r12", ModelPartBuilder.create().uv(72, 32).cuboid(-3.5F, 1.3953F, 5.0193F, 8.0F, 8.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.5708F, 0.1309F));

		ModelPartData cube_r13 = crystal.addChild("cube_r13", ModelPartBuilder.create().uv(72, 24).cuboid(-4.0F, -5.4698F, 4.3619F, 8.0F, 8.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(-0.5F, -2.7479F, -0.5F, 3.098F, 0.0F, 3.1416F));

		ModelPartData cube_r14 = crystal.addChild("cube_r14", ModelPartBuilder.create().uv(80, 72).cuboid(-4.0F, -9.7916F, 3.5242F, 8.0F, 4.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(-0.5F, -2.7479F, -0.5F, 3.0107F, 0.0F, 3.1416F));

		ModelPartData cube_r15 = crystal.addChild("cube_r15", ModelPartBuilder.create().uv(76, 0).cuboid(-4.0F, -11.6822F, 4.9031F, 8.0F, 7.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(-0.5F, -2.7479F, -0.5F, 0.1309F, 0.0F, 0.0F));

		ModelPartData cube_r16 = crystal.addChild("cube_r16", ModelPartBuilder.create().uv(40, 29).cuboid(-4.0F, -9.7916F, 3.5242F, 8.0F, 4.0F, 0.0F, new Dilation(0.0F))
				.uv(64, 16).cuboid(-4.0F, 4.0544F, 5.8737F, 8.0F, 8.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(-0.5F, -2.7479F, -0.5F, -0.1309F, 0.0F, 0.0F));

		ModelPartData cube_r17 = crystal.addChild("cube_r17", ModelPartBuilder.create().uv(64, 65).cuboid(-4.0F, 0.3166F, 4.9814F, 8.0F, 8.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(-0.5F, -2.7479F, -0.5F, -0.0873F, 0.0F, 0.0F));

		ModelPartData cube_r18 = crystal.addChild("cube_r18", ModelPartBuilder.create().uv(48, 65).cuboid(-4.0F, -5.4698F, 4.3619F, 8.0F, 8.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(-0.5F, -2.7479F, -0.5F, -0.0436F, 0.0F, 0.0F));

		ModelPartData cube_r19 = crystal.addChild("cube_r19", ModelPartBuilder.create().uv(60, 8).cuboid(-4.0F, 4.0544F, 5.8737F, 8.0F, 8.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(-0.5F, -2.7479F, -0.5F, 3.0107F, 0.0F, -3.1416F));

		ModelPartData cube_r20 = crystal.addChild("cube_r20", ModelPartBuilder.create().uv(60, 0).cuboid(-4.0F, 0.3166F, 4.9814F, 8.0F, 8.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(-0.5F, -2.7479F, -0.5F, 3.0543F, 0.0F, -3.1416F));

		ModelPartData cube_r21 = crystal.addChild("cube_r21", ModelPartBuilder.create().uv(80, 14).cuboid(-4.0F, -11.6822F, 4.9031F, 8.0F, 7.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(-0.5F, -2.7479F, -0.5F, -3.0107F, 0.0F, 3.1416F));

		ModelPartData cube_r22 = crystal.addChild("cube_r22", ModelPartBuilder.create().uv(24, 45).cuboid(-4.0F, -5.2821F, 4.25F, 8.0F, 18.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(-0.5F, -2.7479F, -0.5F, -3.1416F, 0.0F, -3.1416F));

		ModelPartData metal = main.addChild("metal", ModelPartBuilder.create().uv(0, 22).cuboid(-5.0F, 3.0F, -5.0F, 10.0F, 1.0F, 10.0F, new Dilation(0.0F))
				.uv(0, 33).cuboid(-8.0F, -2.0F, -3.0F, 6.0F, 6.0F, 6.0F, new Dilation(0.0F))
				.uv(24, 33).cuboid(2.0F, -2.0F, -3.0F, 6.0F, 6.0F, 6.0F, new Dilation(0.0F))
				.uv(40, 17).cuboid(-3.0F, -2.0F, -8.0F, 6.0F, 6.0F, 6.0F, new Dilation(0.0F))
				.uv(0, 45).cuboid(-3.0F, -2.0F, 2.0F, 6.0F, 6.0F, 6.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -4.0F, 0.0F));

		ModelPartData cube_r23 = metal.addChild("cube_r23", ModelPartBuilder.create().uv(36, 63).cuboid(3.8828F, -6.139F, -2.0F, 2.0F, 16.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -5.9088F, 0.0F, 1.5708F, -1.3963F, -1.5708F));

		ModelPartData cube_r24 = metal.addChild("cube_r24", ModelPartBuilder.create().uv(24, 63).cuboid(-5.8828F, -6.139F, -2.0F, 2.0F, 16.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -5.9088F, 0.0F, -1.5708F, -1.3963F, 1.5708F));

		ModelPartData cube_r25 = metal.addChild("cube_r25", ModelPartBuilder.create().uv(12, 57).cuboid(-1.0F, -13.0F, -2.0F, 2.0F, 16.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(6.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.1745F));

		ModelPartData cube_r26 = metal.addChild("cube_r26", ModelPartBuilder.create().uv(0, 57).cuboid(-1.0F, -13.0F, -2.0F, 2.0F, 16.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(-6.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.1745F));

		ModelPartData Antenna = metal.addChild("Antenna", ModelPartBuilder.create().uv(48, 33).cuboid(-0.5F, -2.9F, -0.5F, 1.0F, 5.0F, 1.0F, new Dilation(0.0F))
				.uv(38, 17).cuboid(0.75F, -1.9F, -0.5F, 0.0F, 4.0F, 1.0F, new Dilation(0.0F))
				.uv(52, 33).cuboid(-0.75F, -1.9F, -0.5F, 0.0F, 4.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -22.1F, 0.0F));

		ModelPartData cube_r27 = Antenna.addChild("cube_r27", ModelPartBuilder.create().uv(70, 24).cuboid(-0.75F, -2.0F, -0.5F, 0.0F, 4.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.1F, 0.0F, 0.0F, -1.5708F, 0.0F));

		ModelPartData cube_r28 = Antenna.addChild("cube_r28", ModelPartBuilder.create().uv(54, 38).cuboid(-0.75F, -2.0F, -0.5F, 0.0F, 4.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.1F, 0.0F, 0.0F, 1.5708F, 0.0F));

		ModelPartData Thing_3 = metal.addChild("Thing 3", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, -19.2639F, 3.4632F));

		ModelPartData cube_r29 = Thing_3.addChild("cube_r29", ModelPartBuilder.create().uv(32, 17).cuboid(-1.5F, -3.8824F, 0.4695F, 3.0F, 4.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 1.2576F, -0.971F, -0.4363F, 0.0F, 0.0F));

		ModelPartData Thing_1 = metal.addChild("Thing 1", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, -19.2318F, -3.5264F));

		ModelPartData cube_r30 = Thing_1.addChild("cube_r30", ModelPartBuilder.create().uv(80, 80).cuboid(-1.5F, -3.8824F, -0.5305F, 3.0F, 4.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 1.2318F, 1.0264F, 0.4363F, 0.0F, 0.0F));

		ModelPartData Thing_4 = metal.addChild("Thing 4", ModelPartBuilder.create(), ModelTransform.pivot(-3.4987F, -19.2318F, -0.0277F));

		ModelPartData cube_r31 = Thing_4.addChild("cube_r31", ModelPartBuilder.create().uv(64, 24).cuboid(-1.5F, -3.3811F, -3.4922F, 3.0F, 4.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(3.4987F, -0.4742F, 0.0F, 0.0F, 1.5708F, -0.4363F));

		ModelPartData Thing_2 = metal.addChild("Thing 2", ModelPartBuilder.create(), ModelTransform.pivot(3.25F, -19.206F, -0.0277F));

		ModelPartData cube_r32 = Thing_2.addChild("cube_r32", ModelPartBuilder.create().uv(48, 39).cuboid(-1.5F, -2.3322F, 0.1209F, 3.0F, 4.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.3845F, -0.7379F, 0.0F, 0.0F, 1.5708F, 0.4363F));

		ModelPartData Meter = main.addChild("Meter", ModelPartBuilder.create().uv(108, 0).cuboid(-2.1F, -5.0152F, -3.3263F, 5.0F, 10.0F, 5.0F, new Dilation(0.0F)), ModelTransform.of(-0.4F, -11.0F, -2.75F, -0.1745F, 0.0F, 0.0F));

		ModelPartData Light_1 = Meter.addChild("Light_1", ModelPartBuilder.create().uv(120, 126).cuboid(-0.5F, -0.25F, -1.525F, 4.0F, 2.0F, 0.0F, new Dilation(0.0F)), ModelTransform.pivot(-1.1F, 3.0022F, -1.9248F));

		ModelPartData Light_2 = Meter.addChild("Light_2", ModelPartBuilder.create().uv(120, 126).cuboid(-0.5F, -2.75F, -1.525F, 4.0F, 2.0F, 0.0F, new Dilation(0.0F)), ModelTransform.pivot(-1.1F, 3.0022F, -1.9248F));

		ModelPartData Light_4 = Meter.addChild("Light_4", ModelPartBuilder.create().uv(120, 126).cuboid(0.0F, -4.75F, 3.0F, 4.0F, 2.0F, 0.0F, new Dilation(0.0F)), ModelTransform.pivot(-1.6F, 0.0022F, -6.4498F));

		ModelPartData Light_3 = Meter.addChild("Light_3", ModelPartBuilder.create().uv(120, 126).cuboid(0.0F, -4.25F, 3.0F, 4.0F, 2.0F, 0.0F, new Dilation(0.0F)), ModelTransform.pivot(-1.6F, 2.0022F, -6.4498F));
		return TexturedModelData.of(modelData, 128, 128);
	}

	@Override
	public ModelPart getPart() {
		return main;
	}

	@Override
	public void setAngles(Entity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
	}
}