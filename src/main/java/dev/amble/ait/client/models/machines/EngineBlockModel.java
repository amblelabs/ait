package dev.amble.ait.client.models.machines;

import dev.amble.ait.core.blockentities.EngineBlockEntity;
import dev.amble.ait.core.engine.SubSystem;
import dev.amble.ait.core.tardis.Tardis;

import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.animation.Animation;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;

public class EngineBlockModel extends SinglePartEntityModel {
	private final ModelPart engine;
	private final ModelPart bone28;
	private final ModelPart bone46;
	private final ModelPart bone45;
	private final ModelPart bone;
	private final ModelPart bone15;
	private final ModelPart bone17;
	private final ModelPart bone5;
	private final ModelPart bone6;
	private final ModelPart bone20;
	private final ModelPart bone22;
	private final ModelPart magic_box;
	private final ModelPart bone19;
	private final ModelPart bone11;
	private final ModelPart bone40;
	private final ModelPart bone24;
	private final ModelPart bone26;
	private final ModelPart bone12;
	private final ModelPart bone35;
	private final ModelPart bone36;
	private final ModelPart bone14;
	private final ModelPart bone33;
	private final ModelPart bone34;
	private final ModelPart bone13;
	private final ModelPart bone38;
	private final ModelPart bone37;
	private final ModelPart bone39;
	private final ModelPart bone16;
	private final ModelPart bone18;
	private final ModelPart bone21;
	private final ModelPart bone41;
	private final ModelPart bone23;
	private final ModelPart bone25;
	private final ModelPart bone27;
	private final ModelPart bone29;
	private final ModelPart bone31;
	private final ModelPart bone9;
	private final ModelPart bone32;
	private final ModelPart bone42;
	private final ModelPart bone43;
	private final ModelPart bone44;
	private final ModelPart bone47;
	private final ModelPart bone48;
	private final ModelPart bone49;
	private final ModelPart bone2;
	private final ModelPart bone50;
	private final ModelPart bone7;
	private final ModelPart bone10;
	public EngineBlockModel(ModelPart root) {
		this.engine = root.getChild("engine");
		this.bone28 = this.engine.getChild("bone28");
		this.bone46 = this.engine.getChild("bone46");
		this.bone45 = this.bone46.getChild("bone45");
		this.bone = this.engine.getChild("bone");
		this.bone15 = this.engine.getChild("bone15");
		this.bone17 = this.engine.getChild("bone17");
		this.bone5 = this.engine.getChild("bone5");
		this.bone6 = this.engine.getChild("bone6");
		this.bone20 = this.engine.getChild("bone20");
		this.bone22 = this.engine.getChild("bone22");
		this.magic_box = this.engine.getChild("magic_box");
		this.bone19 = this.engine.getChild("bone19");
		this.bone11 = this.engine.getChild("bone11");
		this.bone40 = this.bone11.getChild("bone40");
		this.bone24 = this.engine.getChild("bone24");
		this.bone26 = this.engine.getChild("bone26");
		this.bone12 = this.engine.getChild("bone12");
		this.bone35 = this.bone12.getChild("bone35");
		this.bone36 = this.bone12.getChild("bone36");
		this.bone14 = this.engine.getChild("bone14");
		this.bone33 = this.bone14.getChild("bone33");
		this.bone34 = this.bone14.getChild("bone34");
		this.bone13 = this.engine.getChild("bone13");
		this.bone38 = this.bone13.getChild("bone38");
		this.bone37 = this.bone13.getChild("bone37");
		this.bone39 = this.bone13.getChild("bone39");
		this.bone16 = this.engine.getChild("bone16");
		this.bone18 = this.engine.getChild("bone18");
		this.bone21 = this.engine.getChild("bone21");
		this.bone41 = this.bone21.getChild("bone41");
		this.bone23 = this.bone21.getChild("bone23");
		this.bone25 = this.engine.getChild("bone25");
		this.bone27 = this.engine.getChild("bone27");
		this.bone29 = this.engine.getChild("bone29");
		this.bone31 = this.engine.getChild("bone31");
		this.bone9 = this.engine.getChild("bone9");
		this.bone32 = this.engine.getChild("bone32");
		this.bone42 = this.engine.getChild("bone42");
		this.bone43 = this.bone42.getChild("bone43");
		this.bone44 = this.engine.getChild("bone44");
		this.bone47 = this.engine.getChild("bone47");
		this.bone48 = this.engine.getChild("bone48");
		this.bone49 = this.engine.getChild("bone49");
		this.bone2 = this.engine.getChild("bone2");
		this.bone50 = this.bone2.getChild("bone50");
		this.bone7 = this.engine.getChild("bone7");
		this.bone10 = this.engine.getChild("bone10");
	}
	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		ModelPartData engine = modelPartData.addChild("engine", ModelPartBuilder.create().uv(89, 70).cuboid(-2.3217F, 24.2334F, -18.6961F, 16.0F, 5.0F, 26.0F, new Dilation(0.0F))
				.uv(0, 47).cuboid(-18.3217F, 41.3084F, -14.6961F, 48.0F, 4.0F, 18.0F, new Dilation(0.0F))
				.uv(167, 173).cuboid(-1.3217F, 6.2334F, -12.6961F, 14.0F, 2.0F, 14.0F, new Dilation(0.0F))
				.uv(174, 64).cuboid(0.6783F, -15.7666F, -10.6961F, 10.0F, 22.0F, 10.0F, new Dilation(0.0F))
				.uv(133, 47).cuboid(-2.3217F, -16.7666F, -13.6961F, 16.0F, 0.0F, 16.0F, new Dilation(0.0F))
				.uv(198, 35).cuboid(0.6783F, -34.7666F, -10.6961F, 10.0F, 17.0F, 10.0F, new Dilation(0.0F))
				.uv(197, 126).cuboid(0.6783F, -23.7666F, -12.6961F, 10.0F, 4.0F, 14.0F, new Dilation(0.0F))
				.uv(0, 204).cuboid(0.6783F, -30.7666F, -12.6961F, 10.0F, 4.0F, 14.0F, new Dilation(0.0F))
				.uv(215, 82).cuboid(-1.3217F, -23.7666F, -10.6961F, 14.0F, 4.0F, 10.0F, new Dilation(0.0F))
				.uv(49, 218).cuboid(-1.3217F, -30.7666F, -10.6961F, 14.0F, 4.0F, 10.0F, new Dilation(0.0F))
				.uv(130, 129).cuboid(-3.3217F, 40.3084F, 3.3039F, 18.0F, 5.0F, 15.0F, new Dilation(0.0F))
				.uv(113, 27).cuboid(-3.3217F, 41.3084F, -29.6961F, 18.0F, 4.0F, 15.0F, new Dilation(0.0F))
				.uv(293, 80).cuboid(-4.3217F, 22.2334F, -8.6961F, 4.0F, 2.0F, 6.0F, new Dilation(0.0F))
				.uv(295, 267).cuboid(11.6783F, 22.2334F, -8.6961F, 4.0F, 2.0F, 6.0F, new Dilation(0.0F))
				.uv(236, 150).cuboid(-3.3217F, 40.2334F, 14.8039F, 18.0F, 2.0F, 4.0F, new Dilation(0.0F))
				.uv(209, 97).cuboid(-18.8967F, 40.2334F, -14.6961F, 4.0F, 2.0F, 18.0F, new Dilation(0.0F))
				.uv(139, 211).cuboid(26.2533F, 40.2334F, -14.6961F, 4.0F, 2.0F, 18.0F, new Dilation(0.0F))
				.uv(236, 157).cuboid(-3.3217F, 40.2334F, -30.4461F, 18.0F, 2.0F, 4.0F, new Dilation(0.0F))
				.uv(174, 97).cuboid(1.6783F, -4.7666F, -15.7461F, 8.0F, 1.0F, 2.0F, new Dilation(0.0F))
				.uv(290, 199).cuboid(1.6783F, -4.7666F, 2.2539F, 8.0F, 1.0F, 2.0F, new Dilation(0.0F))
				.uv(300, 101).cuboid(1.6783F, 0.2334F, 2.2539F, 8.0F, 1.0F, 2.0F, new Dilation(0.0F))
				.uv(301, 47).cuboid(1.6783F, 0.2334F, -15.7461F, 8.0F, 1.0F, 2.0F, new Dilation(0.0F))
				.uv(298, 140).cuboid(-4.3217F, -4.7666F, -9.7461F, 1.0F, 1.0F, 8.0F, new Dilation(0.0F))
				.uv(298, 150).cuboid(14.6783F, -4.7666F, -9.7461F, 1.0F, 1.0F, 8.0F, new Dilation(0.0F))
				.uv(298, 160).cuboid(14.6783F, 0.2334F, -9.7461F, 1.0F, 1.0F, 8.0F, new Dilation(0.0F))
				.uv(263, 298).cuboid(-4.3217F, 0.2334F, -9.7461F, 1.0F, 1.0F, 8.0F, new Dilation(0.0F))
				.uv(295, 276).cuboid(2.6783F, 22.2334F, -15.6961F, 6.0F, 2.0F, 4.0F, new Dilation(0.0F))
				.uv(184, 211).cuboid(-7.3217F, 24.2334F, -13.6961F, 5.0F, 5.0F, 16.0F, new Dilation(0.0F))
				.uv(215, 0).cuboid(13.6783F, 24.2334F, -13.6961F, 5.0F, 5.0F, 16.0F, new Dilation(0.0F))
				.uv(298, 170).cuboid(2.6783F, 22.2334F, 0.3039F, 6.0F, 2.0F, 4.0F, new Dilation(0.0F))
				.uv(210, 245).cuboid(-14.3217F, -20.7666F, -8.6961F, 14.0F, 4.0F, 6.0F, new Dilation(0.0F))
				.uv(246, 123).cuboid(11.6783F, -20.7666F, -8.6961F, 14.0F, 4.0F, 6.0F, new Dilation(0.0F))
				.uv(287, 118).cuboid(2.6783F, 13.2334F, -35.1961F, 6.0F, 4.0F, 6.0F, new Dilation(0.0F))
				.uv(287, 129).cuboid(29.4283F, 13.2334F, -8.6961F, 6.0F, 4.0F, 6.0F, new Dilation(0.0F))
				.uv(289, 59).cuboid(2.6783F, 13.2334F, 17.8039F, 6.0F, 4.0F, 6.0F, new Dilation(0.0F))
				.uv(69, 289).cuboid(-23.8217F, 13.2334F, -8.6961F, 6.0F, 4.0F, 6.0F, new Dilation(0.0F)), ModelTransform.pivot(-5.6783F, -21.2334F, 5.6961F));

		ModelPartData cube_r1 = engine.addChild("cube_r1", ModelPartBuilder.create().uv(120, 253).cuboid(0.0F, -2.0F, -2.0F, 16.0F, 3.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(-14.3217F, -21.7666F, -5.6961F, 0.0F, 0.0F, -0.48F));

		ModelPartData cube_r2 = engine.addChild("cube_r2", ModelPartBuilder.create().uv(239, 54).cuboid(-16.0F, -2.0F, -2.0F, 16.0F, 3.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(25.6783F, -21.7666F, -5.6961F, 0.0F, 0.0F, 0.48F));

		ModelPartData cube_r3 = engine.addChild("cube_r3", ModelPartBuilder.create().uv(45, 233).cuboid(-8.0F, -18.0F, -1.0F, 16.0F, 18.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(5.6783F, 41.2334F, 17.3039F, 2.4871F, 0.0F, 3.1416F));

		ModelPartData cube_r4 = engine.addChild("cube_r4", ModelPartBuilder.create().uv(0, 241).cuboid(-2.5F, -18.4604F, -27.1274F, 5.0F, 30.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(5.6783F, 12.2254F, -5.8461F, 2.8362F, 0.0F, 3.1416F));

		ModelPartData cube_r5 = engine.addChild("cube_r5", ModelPartBuilder.create().uv(139, 261).cuboid(-2.5F, -9.626F, -27.9582F, 5.0F, 20.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(5.7283F, 12.0254F, -5.6961F, 0.4363F, 0.0F, 0.0F));

		ModelPartData cube_r6 = engine.addChild("cube_r6", ModelPartBuilder.create().uv(101, 239).cuboid(-2.5F, -18.4604F, -27.1274F, 5.0F, 30.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(5.6783F, 12.2254F, -5.5961F, -0.3054F, 0.0F, 0.0F));

		ModelPartData cube_r7 = engine.addChild("cube_r7", ModelPartBuilder.create().uv(120, 261).cuboid(-2.5F, -9.626F, -27.9582F, 5.0F, 20.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(5.6533F, 12.0254F, -5.9461F, -2.7053F, 0.0F, -3.1416F));

		ModelPartData cube_r8 = engine.addChild("cube_r8", ModelPartBuilder.create().uv(82, 239).cuboid(-2.5F, -18.4604F, -27.1274F, 5.0F, 30.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(5.6783F, 12.2254F, -5.8461F, 2.8362F, 0.0F, -3.1416F));

		ModelPartData cube_r9 = engine.addChild("cube_r9", ModelPartBuilder.create().uv(258, 97).cuboid(-2.5F, -9.626F, -27.9582F, 5.0F, 20.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(5.6783F, 12.0254F, -5.6461F, 0.0F, -1.5708F, 0.4363F));

		ModelPartData cube_r10 = engine.addChild("cube_r10", ModelPartBuilder.create().uv(191, 233).cuboid(-2.5F, -18.4604F, -27.1274F, 5.0F, 30.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(5.6783F, 12.2254F, -5.5961F, 0.0F, -1.5708F, -0.3054F));

		ModelPartData cube_r11 = engine.addChild("cube_r11", ModelPartBuilder.create().uv(231, 256).cuboid(-2.5F, -9.626F, -27.9582F, 5.0F, 20.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(5.6783F, 12.0254F, -5.7461F, 0.0F, 1.5708F, -0.4363F));

		ModelPartData cube_r12 = engine.addChild("cube_r12", ModelPartBuilder.create().uv(251, 245).cuboid(-20.0F, -2.0F, -3.0F, 14.0F, 3.0F, 6.0F, new Dilation(0.0F))
				.uv(246, 134).cuboid(6.0F, -2.0F, -3.0F, 14.0F, 3.0F, 6.0F, new Dilation(0.0F)), ModelTransform.of(5.6783F, -18.7666F, -5.6961F, 0.0F, 1.5708F, 0.0F));

		ModelPartData cube_r13 = engine.addChild("cube_r13", ModelPartBuilder.create().uv(172, 233).cuboid(-2.5F, -18.4604F, -27.1274F, 5.0F, 30.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(5.6783F, 12.2254F, -5.5961F, 0.0F, 1.5708F, 0.3054F));

		ModelPartData cube_r14 = engine.addChild("cube_r14", ModelPartBuilder.create().uv(272, 204).cuboid(-2.0F, 0.5F, -9.0F, 4.0F, 2.0F, 10.0F, new Dilation(0.0F)), ModelTransform.of(-2.2217F, 21.7584F, -7.9711F, 0.0F, -0.7854F, 0.0F));

		ModelPartData cube_r15 = engine.addChild("cube_r15", ModelPartBuilder.create().uv(48, 272).cuboid(-2.0F, -1.0F, -5.0F, 4.0F, 2.0F, 10.0F, new Dilation(0.0F)), ModelTransform.of(10.7499F, 23.2584F, -0.6245F, 0.0F, -0.7854F, 0.0F));

		ModelPartData cube_r16 = engine.addChild("cube_r16", ModelPartBuilder.create().uv(287, 0).cuboid(-2.0F, -2.5F, -5.0F, 4.0F, 5.0F, 7.0F, new Dilation(0.0F)), ModelTransform.of(13.7533F, 26.7584F, 4.5039F, 0.0F, -0.7854F, 0.0F));

		ModelPartData cube_r17 = engine.addChild("cube_r17", ModelPartBuilder.create().uv(286, 285).cuboid(-2.0F, -2.5F, -5.0F, 4.0F, 5.0F, 7.0F, new Dilation(0.0F)), ModelTransform.of(-2.3967F, 26.7584F, 4.5039F, 0.0F, 0.7854F, 0.0F));

		ModelPartData cube_r18 = engine.addChild("cube_r18", ModelPartBuilder.create().uv(113, 286).cuboid(-2.0F, -2.5F, -5.0F, 4.0F, 5.0F, 7.0F, new Dilation(0.0F)), ModelTransform.of(-4.4967F, 26.7584F, -13.7961F, 0.0F, -0.7854F, 0.0F));

		ModelPartData cube_r19 = engine.addChild("cube_r19", ModelPartBuilder.create().uv(263, 285).cuboid(-2.0F, -2.5F, -5.0F, 4.0F, 5.0F, 7.0F, new Dilation(0.0F)), ModelTransform.of(15.8533F, 26.7584F, -13.7961F, 0.0F, 0.7854F, 0.0F));

		ModelPartData cube_r20 = engine.addChild("cube_r20", ModelPartBuilder.create().uv(302, 109).cuboid(-4.0F, -0.5F, -1.0F, 8.0F, 1.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(12.0533F, 0.7584F, -12.1211F, 0.0F, -0.7854F, 0.0F));

		ModelPartData cube_r21 = engine.addChild("cube_r21", ModelPartBuilder.create().uv(302, 105).cuboid(-4.0F, -0.5F, -1.0F, 8.0F, 1.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(-0.6967F, 0.7584F, 0.8789F, 0.0F, -0.7854F, 0.0F));

		ModelPartData cube_r22 = engine.addChild("cube_r22", ModelPartBuilder.create().uv(301, 211).cuboid(-4.0F, -0.5F, -1.0F, 8.0F, 1.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(-0.6967F, 0.7584F, -12.1211F, 0.0F, 0.7854F, 0.0F));

		ModelPartData cube_r23 = engine.addChild("cube_r23", ModelPartBuilder.create().uv(301, 207).cuboid(-4.0F, -0.5F, -1.0F, 8.0F, 1.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(12.1783F, -4.2666F, 0.7539F, 0.0F, 0.7854F, 0.0F));

		ModelPartData cube_r24 = engine.addChild("cube_r24", ModelPartBuilder.create().uv(301, 203).cuboid(-4.0F, -0.5F, -1.0F, 8.0F, 1.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(12.1783F, -4.2666F, -12.2461F, 0.0F, -0.7854F, 0.0F));

		ModelPartData cube_r25 = engine.addChild("cube_r25", ModelPartBuilder.create().uv(301, 55).cuboid(-4.0F, -0.5F, -1.0F, 8.0F, 1.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(-0.8217F, -4.2666F, -12.2461F, 0.0F, 0.7854F, 0.0F));

		ModelPartData cube_r26 = engine.addChild("cube_r26", ModelPartBuilder.create().uv(301, 51).cuboid(-4.0F, -0.5F, -1.0F, 8.0F, 1.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(-0.8217F, -4.2666F, 0.7539F, 0.0F, -0.7854F, 0.0F));

		ModelPartData cube_r27 = engine.addChild("cube_r27", ModelPartBuilder.create().uv(301, 43).cuboid(-4.0F, -0.5F, -1.0F, 8.0F, 1.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(12.0533F, 0.7584F, 0.8789F, 0.0F, 0.7854F, 0.0F));

		ModelPartData cube_r28 = engine.addChild("cube_r28", ModelPartBuilder.create().uv(80, 300).cuboid(-2.0F, -13.0F, -2.0F, 2.0F, 13.0F, 3.0F, new Dilation(0.0F))
				.uv(282, 298).cuboid(-10.0F, -13.0F, -2.0F, 2.0F, 13.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(10.6783F, 6.4584F, -10.5961F, 0.3054F, 0.0F, 0.0F));

		ModelPartData cube_r29 = engine.addChild("cube_r29", ModelPartBuilder.create().uv(69, 300).cuboid(0.0F, -13.0F, -2.0F, 2.0F, 13.0F, 3.0F, new Dilation(0.0F))
				.uv(0, 299).cuboid(8.0F, -13.0F, -2.0F, 2.0F, 13.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(0.6783F, 6.4584F, 0.2789F, -0.3054F, 0.0F, 0.0F));

		ModelPartData cube_r30 = engine.addChild("cube_r30", ModelPartBuilder.create().uv(299, 217).cuboid(0.0F, -13.0F, -2.0F, 2.0F, 13.0F, 3.0F, new Dilation(0.0F))
				.uv(293, 298).cuboid(0.0F, -13.0F, 5.0F, 2.0F, 13.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(-1.3217F, 6.4584F, -8.5961F, 0.0F, 0.0F, -0.3054F));

		ModelPartData cube_r31 = engine.addChild("cube_r31", ModelPartBuilder.create().uv(122, 299).cuboid(-2.0F, -13.0F, -2.0F, 2.0F, 13.0F, 3.0F, new Dilation(0.0F))
				.uv(111, 299).cuboid(-2.0F, -13.0F, -9.0F, 2.0F, 13.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(12.6783F, 6.4584F, -1.5961F, 0.0F, 0.0F, 0.3054F));

		ModelPartData cube_r32 = engine.addChild("cube_r32", ModelPartBuilder.create().uv(56, 285).cuboid(-2.0F, -19.0F, -2.0F, 3.0F, 19.0F, 3.0F, new Dilation(0.0F))
				.uv(100, 274).cuboid(-17.0F, -19.0F, -2.0F, 3.0F, 19.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(13.6783F, 41.4584F, -28.8461F, -0.6545F, 0.0F, 0.0F));

		ModelPartData cube_r33 = engine.addChild("cube_r33", ModelPartBuilder.create().uv(19, 272).cuboid(-2.0F, 0.5F, -9.0F, 4.0F, 2.0F, 10.0F, new Dilation(0.0F)), ModelTransform.of(13.5783F, 21.7584F, -7.9711F, 0.0F, 0.7854F, 0.0F));

		ModelPartData cube_r34 = engine.addChild("cube_r34", ModelPartBuilder.create().uv(269, 164).cuboid(-2.0F, -1.0F, -5.0F, 4.0F, 2.0F, 10.0F, new Dilation(0.0F)), ModelTransform.of(0.6068F, 23.2584F, -0.6245F, 0.0F, 0.7854F, 0.0F));

		ModelPartData cube_r35 = engine.addChild("cube_r35", ModelPartBuilder.create().uv(13, 285).cuboid(-2.0F, -7.0F, -2.0F, 4.0F, 14.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(0.6783F, -24.7666F, -0.6961F, 0.0F, 2.3562F, 0.0F));

		ModelPartData cube_r36 = engine.addChild("cube_r36", ModelPartBuilder.create().uv(225, 281).cuboid(-2.0F, -7.0F, -2.0F, 4.0F, 14.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(10.6783F, -24.7666F, -10.6961F, 0.0F, -0.7854F, 0.0F));

		ModelPartData cube_r37 = engine.addChild("cube_r37", ModelPartBuilder.create().uv(208, 281).cuboid(-2.0F, -7.0F, -2.0F, 4.0F, 14.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(0.6783F, -24.7666F, -10.6961F, 0.0F, 0.7854F, 0.0F));

		ModelPartData cube_r38 = engine.addChild("cube_r38", ModelPartBuilder.create().uv(281, 144).cuboid(-2.0F, -7.0F, -2.0F, 4.0F, 14.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(10.6783F, -24.7666F, -0.6961F, 0.0F, -2.3562F, 0.0F));

		ModelPartData cube_r39 = engine.addChild("cube_r39", ModelPartBuilder.create().uv(210, 256).cuboid(-7.0F, -16.0F, 2.0F, 2.0F, 16.0F, 8.0F, new Dilation(0.0F)), ModelTransform.of(5.4283F, 4.4834F, -11.6961F, 0.0F, 0.0F, -0.2618F));

		ModelPartData cube_r40 = engine.addChild("cube_r40", ModelPartBuilder.create().uv(251, 255).cuboid(5.0F, -16.0F, 2.0F, 2.0F, 16.0F, 8.0F, new Dilation(0.0F)), ModelTransform.of(5.9283F, 4.4834F, -11.6961F, 0.0F, 0.0F, 0.2618F));

		ModelPartData cube_r41 = engine.addChild("cube_r41", ModelPartBuilder.create().uv(187, 268).cuboid(-4.0F, -16.0F, -1.0F, 8.0F, 16.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(5.6783F, 5.9834F, 0.3039F, -0.2618F, 0.0F, 0.0F));

		ModelPartData cube_r42 = engine.addChild("cube_r42", ModelPartBuilder.create().uv(65, 109).cuboid(-4.0F, -16.0F, -1.0F, 8.0F, 16.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(5.6783F, 5.9834F, -11.6961F, 0.2618F, 0.0F, 0.0F));

		ModelPartData cube_r43 = engine.addChild("cube_r43", ModelPartBuilder.create().uv(65, 129).cuboid(-8.0F, -3.5F, -4.0F, 11.0F, 5.0F, 21.0F, new Dilation(0.0F)), ModelTransform.of(15.4283F, 43.7834F, -24.6961F, 0.0F, 0.7854F, 0.0F));

		ModelPartData cube_r44 = engine.addChild("cube_r44", ModelPartBuilder.create().uv(113, 0).cuboid(-2.0F, -3.5F, -4.0F, 11.0F, 5.0F, 21.0F, new Dilation(0.0F)), ModelTransform.of(-14.0217F, 43.7834F, 4.7789F, 0.0F, 0.7854F, 0.0F));

		ModelPartData cube_r45 = engine.addChild("cube_r45", ModelPartBuilder.create().uv(0, 109).cuboid(-3.0F, -3.5F, -4.0F, 11.0F, 5.0F, 21.0F, new Dilation(0.0F)), ModelTransform.of(-4.0717F, 43.7834F, -24.6961F, 0.0F, -0.7854F, 0.0F));

		ModelPartData cube_r46 = engine.addChild("cube_r46", ModelPartBuilder.create().uv(89, 102).cuboid(-9.0F, -3.5F, -4.0F, 11.0F, 5.0F, 21.0F, new Dilation(0.0F)), ModelTransform.of(25.3783F, 43.7834F, 4.7789F, 0.0F, -0.7854F, 0.0F));

		ModelPartData cube_r47 = engine.addChild("cube_r47", ModelPartBuilder.create().uv(277, 90).cuboid(-4.5F, -6.7901F, -1.5109F, 9.0F, 12.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(25.1783F, 35.4834F, -5.6961F, 0.0F, -1.5708F, -0.6981F));

		ModelPartData cube_r48 = engine.addChild("cube_r48", ModelPartBuilder.create().uv(77, 274).cuboid(-4.5F, -6.7901F, -1.5109F, 9.0F, 12.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(-13.8217F, 35.4834F, -5.6961F, 0.0F, 1.5708F, 0.6981F));

		ModelPartData cube_r49 = engine.addChild("cube_r49", ModelPartBuilder.create().uv(272, 270).cuboid(-4.5F, -13.0F, -1.5F, 9.0F, 12.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(5.6783F, 40.2334F, 17.8039F, 2.4435F, 0.0F, 3.1416F));

		ModelPartData cube_r50 = engine.addChild("cube_r50", ModelPartBuilder.create().uv(272, 255).cuboid(-4.5F, -13.0F, -1.5F, 9.0F, 12.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(5.6783F, 40.2334F, -29.1961F, -0.6981F, 0.0F, 0.0F));

		ModelPartData cube_r51 = engine.addChild("cube_r51", ModelPartBuilder.create().uv(264, 15).cuboid(-3.0F, 5.25F, -22.85F, 6.0F, 6.0F, 8.0F, new Dilation(0.0F)), ModelTransform.of(5.6783F, 45.2334F, -5.6961F, 2.4435F, 0.0F, -3.1416F));

		ModelPartData cube_r52 = engine.addChild("cube_r52", ModelPartBuilder.create().uv(261, 189).cuboid(-3.0F, 5.25F, -22.85F, 6.0F, 6.0F, 8.0F, new Dilation(0.0F)), ModelTransform.of(5.6783F, 45.2334F, -5.6961F, 0.0F, -1.5708F, -0.6981F));

		ModelPartData cube_r53 = engine.addChild("cube_r53", ModelPartBuilder.create().uv(260, 62).cuboid(-3.0F, 5.25F, -22.85F, 6.0F, 6.0F, 8.0F, new Dilation(0.0F)), ModelTransform.of(5.6783F, 45.2334F, -5.6961F, -0.6981F, 0.0F, 0.0F));

		ModelPartData cube_r54 = engine.addChild("cube_r54", ModelPartBuilder.create().uv(258, 0).cuboid(-3.0F, 5.25F, -22.85F, 6.0F, 6.0F, 8.0F, new Dilation(0.0F)), ModelTransform.of(5.6783F, 45.2334F, -5.6961F, 0.0F, 1.5708F, 0.6981F));

		ModelPartData cube_r55 = engine.addChild("cube_r55", ModelPartBuilder.create().uv(158, 281).cuboid(-1.5F, -9.5F, -1.5F, 3.0F, 19.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(-1.8217F, 33.5009F, 11.874F, 2.4871F, 0.0F, 3.1416F));

		ModelPartData cube_r56 = engine.addChild("cube_r56", ModelPartBuilder.create().uv(250, 280).cuboid(-1.5F, -9.5F, -1.5F, 3.0F, 19.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(13.1783F, 33.5009F, 11.874F, 2.4871F, 0.0F, -3.1416F));

		ModelPartData cube_r57 = engine.addChild("cube_r57", ModelPartBuilder.create().uv(135, 232).cuboid(-8.0F, -18.0F, -1.0F, 16.0F, 18.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(5.6783F, 41.2334F, 17.3039F, 2.4871F, 0.0F, -3.1416F));

		ModelPartData cube_r58 = engine.addChild("cube_r58", ModelPartBuilder.create().uv(98, 218).cuboid(-8.0F, -18.0F, -1.0F, 16.0F, 18.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(5.6783F, 41.2334F, -28.6961F, -0.6545F, 0.0F, 0.0F));

		ModelPartData cube_r59 = engine.addChild("cube_r59", ModelPartBuilder.create().uv(55, 180).cuboid(0.0F, -18.0F, -8.0F, 1.0F, 18.0F, 16.0F, new Dilation(0.0F)), ModelTransform.of(29.6783F, 40.7334F, -5.6961F, -3.1416F, 0.0F, 2.4871F));

		ModelPartData cube_r60 = engine.addChild("cube_r60", ModelPartBuilder.create().uv(180, 0).cuboid(-1.0F, -18.0F, -8.0F, 1.0F, 18.0F, 16.0F, new Dilation(0.0F)), ModelTransform.of(-17.3217F, 41.2334F, -5.6961F, 0.0F, 0.0F, 0.6545F));

		ModelPartData bone28 = engine.addChild("bone28", ModelPartBuilder.create().uv(304, 298).cuboid(-3.0F, -1.8333F, -1.0F, 6.0F, 3.0F, 2.0F, new Dilation(0.0F))
				.uv(28, 308).cuboid(-2.0F, 1.1667F, -1.0F, 4.0F, 5.0F, 2.0F, new Dilation(0.0F))
				.uv(300, 89).cuboid(-4.0F, -4.8333F, -1.0F, 8.0F, 3.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, -2.9234F, 0.7854F, -3.1416F));

		ModelPartData bone46 = engine.addChild("bone46", ModelPartBuilder.create(), ModelTransform.of(-9.3217F, 45.2334F, 8.3039F, 0.0F, 2.3562F, 0.0F));

		ModelPartData bone45 = bone46.addChild("bone45", ModelPartBuilder.create(), ModelTransform.of(-0.7647F, -3.7141F, -2.25F, -0.5236F, 0.0F, 0.0F));

		ModelPartData bone = engine.addChild("bone", ModelPartBuilder.create(), ModelTransform.of(-12.3217F, 45.2334F, -22.6961F, 0.0F, 0.7854F, 0.0F));

		ModelPartData bone15 = engine.addChild("bone15", ModelPartBuilder.create(), ModelTransform.of(-1.5717F, 4.2334F, -12.9461F, 0.2618F, 0.0F, -0.2618F));

		ModelPartData bone17 = engine.addChild("bone17", ModelPartBuilder.create(), ModelTransform.of(-1.5717F, 4.2334F, 1.5539F, -0.2618F, 0.0F, -0.2618F));

		ModelPartData bone5 = engine.addChild("bone5", ModelPartBuilder.create(), ModelTransform.of(-17.3217F, 45.2334F, -14.6961F, 0.0F, 1.5708F, 0.0F));

		ModelPartData cube_r61 = bone5.addChild("cube_r61", ModelPartBuilder.create().uv(171, 281).cuboid(-2.0F, -22.0642F, -4.5712F, 3.0F, 19.0F, 3.0F, new Dilation(0.0F))
				.uv(0, 276).cuboid(13.0F, -22.0642F, -4.5712F, 3.0F, 19.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(-16.0F, 0.225F, 0.175F, -0.6545F, 0.0F, 0.0F));

		ModelPartData bone6 = engine.addChild("bone6", ModelPartBuilder.create(), ModelTransform.of(28.6783F, 45.2334F, -14.6961F, 0.0F, -1.5708F, 0.0F));

		ModelPartData cube_r62 = bone6.addChild("cube_r62", ModelPartBuilder.create().uv(30, 285).cuboid(-1.0F, -22.0642F, -4.5712F, 3.0F, 19.0F, 3.0F, new Dilation(0.0F))
				.uv(43, 285).cuboid(14.0F, -22.0642F, -4.5712F, 3.0F, 19.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(1.0F, 0.225F, 0.175F, -0.6545F, 0.0F, 0.0F));

		ModelPartData bone20 = engine.addChild("bone20", ModelPartBuilder.create(), ModelTransform.of(-9.3217F, 44.2334F, -29.1961F, 0.0F, 0.7854F, 0.0F));

		ModelPartData bone22 = engine.addChild("bone22", ModelPartBuilder.create(), ModelTransform.of(-9.3217F, 44.2334F, -29.1961F, 0.0F, 0.7854F, 0.0F));

		ModelPartData magic_box = engine.addChild("magic_box", ModelPartBuilder.create(), ModelTransform.of(5.6783F, 14.2334F, -5.6961F, 0.7854F, 0.0F, 0.0F));

		ModelPartData cube_r63 = magic_box.addChild("cube_r63", ModelPartBuilder.create().uv(0, 316).cuboid(-6.0F, -2.0F, -4.0F, 6.0F, 6.0F, 6.0F, new Dilation(0.25F))
				.uv(19, 241).cuboid(-6.0F, -2.0F, -4.0F, 6.0F, 6.0F, 6.0F, new Dilation(0.0F)), ModelTransform.of(1.5F, -1.75F, 0.0F, 0.0F, 0.0F, -0.7854F));

		ModelPartData bone19 = engine.addChild("bone19", ModelPartBuilder.create().uv(216, 190).cuboid(6.0F, -7.0286F, -6.0188F, 10.0F, 6.0F, 12.0F, new Dilation(0.0F))
				.uv(0, 70).cuboid(-6.0F, -7.0286F, -15.9938F, 12.0F, 6.0F, 32.0F, new Dilation(0.0F))
				.uv(215, 63).cuboid(-16.0F, -7.0286F, -6.0188F, 10.0F, 6.0F, 12.0F, new Dilation(0.0F)), ModelTransform.pivot(5.6783F, -3.7381F, -5.7023F));

		ModelPartData cube_r64 = bone19.addChild("cube_r64", ModelPartBuilder.create().uv(187, 150).cuboid(-5.0F, -4.0F, -5.0F, 10.0F, 6.0F, 14.0F, new Dilation(0.0F)), ModelTransform.of(-8.9F, -2.9786F, 6.0812F, 0.0F, 0.7854F, 0.0F));

		ModelPartData cube_r65 = bone19.addChild("cube_r65", ModelPartBuilder.create().uv(0, 183).cuboid(-5.0F, -4.0F, -5.0F, 10.0F, 6.0F, 14.0F, new Dilation(0.0F)), ModelTransform.of(-6.1F, -2.9786F, -8.8938F, 0.0F, -0.7854F, 0.0F));

		ModelPartData cube_r66 = bone19.addChild("cube_r66", ModelPartBuilder.create().uv(90, 197).cuboid(-5.0F, -4.0F, -5.0F, 10.0F, 6.0F, 14.0F, new Dilation(0.0F)), ModelTransform.of(6.1F, -2.9786F, -8.8938F, 0.0F, 0.7854F, 0.0F));

		ModelPartData cube_r67 = bone19.addChild("cube_r67", ModelPartBuilder.create().uv(167, 190).cuboid(-5.0F, -4.0F, -5.0F, 10.0F, 6.0F, 14.0F, new Dilation(0.0F)), ModelTransform.of(8.9F, -2.9786F, 6.0812F, 0.0F, -0.7854F, 0.0F));

		ModelPartData bone11 = engine.addChild("bone11", ModelPartBuilder.create().uv(0, 136).cuboid(-20.0F, -6.8364F, -8.039F, 12.0F, 6.0F, 16.0F, new Dilation(0.0F))
				.uv(325, 0).cuboid(-25.0F, -2.8364F, -0.039F, 22.0F, 25.0F, 0.0F, new Dilation(0.0F))
				.uv(325, 29).mirrored().cuboid(3.0F, -2.8364F, -0.039F, 22.0F, 25.0F, 0.0F, new Dilation(0.0F)).mirrored(false)
				.uv(0, 0).cuboid(-8.0F, -6.8364F, -20.039F, 16.0F, 6.0F, 40.0F, new Dilation(0.0F))
				.uv(184, 303).cuboid(-2.0F, -24.8364F, -2.039F, 4.0F, 1.0F, 4.0F, new Dilation(0.0F))
				.uv(120, 239).cuboid(-1.0F, -32.8364F, -1.039F, 2.0F, 9.0F, 2.0F, new Dilation(0.0F))
				.uv(11, 304).cuboid(-2.0F, -27.8364F, -2.039F, 4.0F, 1.0F, 4.0F, new Dilation(0.0F))
				.uv(150, 304).cuboid(-2.0F, -30.8364F, -2.039F, 4.0F, 1.0F, 4.0F, new Dilation(0.0F))
				.uv(130, 150).cuboid(8.0F, -6.8364F, -8.039F, 12.0F, 6.0F, 16.0F, new Dilation(0.0F)), ModelTransform.pivot(5.6783F, -10.9303F, -5.6571F));

		ModelPartData cube_r68 = bone11.addChild("cube_r68", ModelPartBuilder.create().uv(112, 173).cuboid(-5.0F, -4.0F, -11.0F, 10.0F, 6.0F, 17.0F, new Dilation(0.0F)), ModelTransform.of(12.225F, -2.7864F, -8.714F, 0.0F, 0.7854F, 0.0F));

		ModelPartData cube_r69 = bone11.addChild("cube_r69", ModelPartBuilder.create().uv(0, 159).cuboid(-5.0F, -4.0F, -11.0F, 10.0F, 6.0F, 17.0F, new Dilation(0.0F)), ModelTransform.of(-8.675F, -2.7864F, 12.186F, 0.0F, 0.7854F, 0.0F));

		ModelPartData cube_r70 = bone11.addChild("cube_r70", ModelPartBuilder.create().uv(57, 156).cuboid(-5.0F, -4.0F, -11.0F, 10.0F, 6.0F, 17.0F, new Dilation(0.0F)), ModelTransform.of(-12.225F, -2.7864F, -8.714F, 0.0F, -0.7854F, 0.0F));

		ModelPartData cube_r71 = bone11.addChild("cube_r71", ModelPartBuilder.create().uv(154, 102).cuboid(-5.0F, -4.0F, -11.0F, 10.0F, 6.0F, 17.0F, new Dilation(0.0F)), ModelTransform.of(8.675F, -2.7864F, 12.186F, 0.0F, -0.7854F, 0.0F));

		ModelPartData bone40 = bone11.addChild("bone40", ModelPartBuilder.create(), ModelTransform.pivot(-11.0F, -26.8364F, -14.039F));

		ModelPartData bone24 = engine.addChild("bone24", ModelPartBuilder.create().uv(197, 145).cuboid(-14.25F, -0.75F, 5.0F, 22.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(-12.3217F, 40.9834F, -28.4461F, 0.0F, 0.7854F, 0.0F));

		ModelPartData bone26 = engine.addChild("bone26", ModelPartBuilder.create().uv(209, 118).cuboid(-6.25F, -0.775F, 1.0F, 22.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(-12.3217F, 40.9834F, 5.5539F, 0.0F, -0.7854F, 0.0F));

		ModelPartData bone12 = engine.addChild("bone12", ModelPartBuilder.create().uv(201, 300).cuboid(-3.1471F, -11.3617F, -2.0F, 4.0F, 3.0F, 4.0F, new Dilation(0.0F))
				.uv(280, 43).cuboid(-1.1471F, -13.3617F, -5.0F, 0.0F, 5.0F, 10.0F, new Dilation(0.0F))
				.uv(48, 254).cuboid(-5.1471F, -8.3617F, -3.0F, 8.0F, 11.0F, 6.0F, new Dilation(0.0F)), ModelTransform.of(22.9283F, -17.0166F, -5.7711F, 3.1416F, 0.0F, -2.5307F));

		ModelPartData cube_r72 = bone12.addChild("cube_r72", ModelPartBuilder.create().uv(180, 35).cuboid(-3.1471F, -5.8415F, -3.1585F, 4.0F, 7.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -6.0F, -2.0F, 0.7854F, 0.0F, 0.0F));

		ModelPartData cube_r73 = bone12.addChild("cube_r73", ModelPartBuilder.create().uv(94, 297).cuboid(-3.1471F, -5.8415F, -0.8415F, 4.0F, 7.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -6.0F, 2.0F, -0.7854F, 0.0F, 0.0F));

		ModelPartData bone35 = bone12.addChild("bone35", ModelPartBuilder.create(), ModelTransform.pivot(-1.1471F, -7.5437F, 5.182F));

		ModelPartData cube_r74 = bone35.addChild("cube_r74", ModelPartBuilder.create().uv(293, 70).cuboid(-4.1471F, -4.8415F, -1.8415F, 6.0F, 3.0F, 6.0F, new Dilation(0.0F)), ModelTransform.of(1.1471F, 1.5437F, -3.182F, -0.7854F, 0.0F, 0.0F));

		ModelPartData bone36 = bone12.addChild("bone36", ModelPartBuilder.create(), ModelTransform.pivot(-1.1471F, -7.8972F, -5.5355F));

		ModelPartData cube_r75 = bone36.addChild("cube_r75", ModelPartBuilder.create().uv(293, 13).cuboid(-4.1471F, -4.8415F, -4.1585F, 6.0F, 3.0F, 6.0F, new Dilation(0.0F)), ModelTransform.of(1.1471F, 1.8972F, 3.5355F, 0.7854F, 0.0F, 0.0F));

		ModelPartData bone14 = engine.addChild("bone14", ModelPartBuilder.create().uv(19, 254).cuboid(-2.8529F, -8.3617F, -3.0F, 8.0F, 11.0F, 6.0F, new Dilation(0.0F))
				.uv(90, 180).cuboid(1.1471F, -13.3617F, -5.0F, 0.0F, 5.0F, 10.0F, new Dilation(0.0F))
				.uv(218, 300).cuboid(-0.8529F, -11.3617F, -2.0F, 4.0F, 3.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(-11.5717F, -17.0166F, -5.7711F, 3.1416F, 0.0F, 2.5307F));

		ModelPartData cube_r76 = bone14.addChild("cube_r76", ModelPartBuilder.create().uv(296, 177).cuboid(-0.8529F, -5.8415F, -0.8415F, 4.0F, 7.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -6.0F, 2.0F, -0.7854F, 0.0F, 0.0F));

		ModelPartData cube_r77 = bone14.addChild("cube_r77", ModelPartBuilder.create().uv(210, 233).cuboid(-0.8529F, -5.8415F, -3.1585F, 4.0F, 7.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -6.0F, -2.0F, 0.7854F, 0.0F, 0.0F));

		ModelPartData bone33 = bone14.addChild("bone33", ModelPartBuilder.create(), ModelTransform.pivot(1.1471F, -7.8972F, -5.5355F));

		ModelPartData cube_r78 = bone33.addChild("cube_r78", ModelPartBuilder.create().uv(290, 189).cuboid(-1.8529F, -4.8415F, -4.1585F, 6.0F, 3.0F, 6.0F, new Dilation(0.0F)), ModelTransform.of(-1.1471F, 1.8972F, 3.5355F, 0.7854F, 0.0F, 0.0F));

		ModelPartData bone34 = bone14.addChild("bone34", ModelPartBuilder.create(), ModelTransform.pivot(1.1471F, -7.5437F, 5.182F));

		ModelPartData cube_r79 = bone34.addChild("cube_r79", ModelPartBuilder.create().uv(292, 241).cuboid(-1.8529F, -4.8415F, -1.8415F, 6.0F, 3.0F, 6.0F, new Dilation(0.0F)), ModelTransform.of(-1.1471F, 1.5437F, -3.182F, -0.7854F, 0.0F, 0.0F));

		ModelPartData bone13 = engine.addChild("bone13", ModelPartBuilder.create().uv(264, 77).cuboid(-8.0F, -4.1111F, -6.0F, 2.0F, 0.0F, 12.0F, new Dilation(0.0F))
				.uv(133, 64).cuboid(-8.0F, -4.1111F, -8.0F, 16.0F, 0.0F, 2.0F, new Dilation(0.0F))
				.uv(236, 164).cuboid(8.0F, -4.1111F, -3.0F, 6.0F, 0.0F, 6.0F, new Dilation(0.0F))
				.uv(158, 268).cuboid(6.0F, -4.1111F, -6.0F, 2.0F, 0.0F, 12.0F, new Dilation(0.0F))
				.uv(133, 67).cuboid(-8.0F, -4.1111F, 6.0F, 16.0F, 0.0F, 2.0F, new Dilation(0.0F))
				.uv(112, 156).cuboid(-4.0F, -10.1111F, -6.0F, 8.0F, 15.0F, 0.0F, new Dilation(0.0F))
				.uv(136, 286).cuboid(-4.0F, -10.1111F, 6.0F, 8.0F, 15.0F, 0.0F, new Dilation(0.0F))
				.uv(293, 23).cuboid(-14.0F, -4.1111F, -3.0F, 6.0F, 0.0F, 6.0F, new Dilation(0.0F)), ModelTransform.pivot(5.6783F, 18.3445F, -5.6961F));

		ModelPartData cube_r80 = bone13.addChild("cube_r80", ModelPartBuilder.create().uv(235, 300).cuboid(-1.5F, -7.5F, 0.0F, 3.0F, 15.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(5.0F, -2.6111F, -5.0F, 0.0F, -0.7854F, 0.0F));

		ModelPartData cube_r81 = bone13.addChild("cube_r81", ModelPartBuilder.create().uv(242, 281).cuboid(-1.5F, -7.5F, 0.0F, 3.0F, 15.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(5.0F, -2.6111F, 5.0F, 0.0F, 0.7854F, 0.0F));

		ModelPartData cube_r82 = bone13.addChild("cube_r82", ModelPartBuilder.create().uv(295, 251).cuboid(-4.0F, -7.5F, -6.0F, 8.0F, 15.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -2.6111F, 0.0F, 0.0F, -1.5708F, 0.0F));

		ModelPartData cube_r83 = bone13.addChild("cube_r83", ModelPartBuilder.create().uv(184, 287).cuboid(-4.0F, -7.5F, -6.0F, 8.0F, 15.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -2.6111F, 0.0F, 0.0F, 1.5708F, 0.0F));

		ModelPartData cube_r84 = bone13.addChild("cube_r84", ModelPartBuilder.create().uv(242, 297).cuboid(-1.5F, -7.5F, 0.0F, 3.0F, 15.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(-5.0F, -2.6111F, 5.0F, 0.0F, -0.7854F, 0.0F));

		ModelPartData cube_r85 = bone13.addChild("cube_r85", ModelPartBuilder.create().uv(57, 136).cuboid(-1.5F, -7.5F, 0.0F, 3.0F, 15.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(-5.0F, -2.6111F, -5.0F, 0.0F, 0.7854F, 0.0F));

		ModelPartData bone38 = bone13.addChild("bone38", ModelPartBuilder.create().uv(239, 32).cuboid(-5.0F, 0.0F, -5.0F, 10.0F, 0.0F, 10.0F, new Dilation(0.0F))
				.uv(277, 105).cuboid(-3.0F, -3.0F, -3.0F, 6.0F, 6.0F, 6.0F, new Dilation(0.0F)), ModelTransform.pivot(-19.0F, -4.1611F, 0.0F));

		ModelPartData bone37 = bone13.addChild("bone37", ModelPartBuilder.create().uv(280, 30).cuboid(-3.0F, -3.0F, -3.0F, 6.0F, 6.0F, 6.0F, new Dilation(0.0F))
				.uv(239, 43).cuboid(-5.0F, 0.0F, -5.0F, 10.0F, 0.0F, 10.0F, new Dilation(0.0F)), ModelTransform.pivot(19.0F, -4.2111F, 0.0F));

		ModelPartData bone39 = bone13.addChild("bone39", ModelPartBuilder.create(), ModelTransform.pivot(19.0F, -4.1111F, 0.0F));

		ModelPartData bone16 = engine.addChild("bone16", ModelPartBuilder.create(), ModelTransform.of(12.9283F, 4.2334F, 1.5539F, -0.2618F, 0.0F, 0.2618F));

		ModelPartData bone18 = engine.addChild("bone18", ModelPartBuilder.create(), ModelTransform.of(12.9283F, 4.2334F, -12.9461F, 0.2618F, 0.0F, 0.2618F));

		ModelPartData bone21 = engine.addChild("bone21", ModelPartBuilder.create(), ModelTransform.of(10.6241F, 38.2032F, -13.485F, 0.0F, -0.7854F, 0.0F));

		ModelPartData cube_r86 = bone21.addChild("cube_r86", ModelPartBuilder.create().uv(139, 197).cuboid(-5.5F, -12.3736F, -3.6492F, 11.0F, 9.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(2.0F, 5.0302F, -13.2189F, -0.5672F, 0.0F, 0.0F));

		ModelPartData bone41 = bone21.addChild("bone41", ModelPartBuilder.create(), ModelTransform.of(2.0F, -2.9964F, 29.4416F, 0.5672F, 0.0F, 0.0F));

		ModelPartData cube_r87 = bone41.addChild("cube_r87", ModelPartBuilder.create().uv(269, 177).cuboid(-5.5F, -12.3736F, 0.6492F, 11.0F, 9.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 7.5266F, 1.3395F, 0.0F, 3.1416F, 0.0F));

		ModelPartData bone23 = bone21.addChild("bone23", ModelPartBuilder.create(), ModelTransform.of(-4.0F, 6.0302F, -18.2189F, 0.0F, -0.7854F, 0.0F));

		ModelPartData bone25 = engine.addChild("bone25", ModelPartBuilder.create().uv(215, 22).cuboid(-7.75F, -0.75F, 5.0F, 22.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(23.6783F, 40.9834F, -28.4461F, 0.0F, -0.7854F, 0.0F));

		ModelPartData bone27 = engine.addChild("bone27", ModelPartBuilder.create().uv(215, 27).cuboid(-15.75F, -0.775F, 1.0F, 22.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(23.6783F, 40.9834F, 5.5539F, 0.0F, 0.7854F, 0.0F));

		ModelPartData bone29 = engine.addChild("bone29", ModelPartBuilder.create(), ModelTransform.of(10.4283F, 5.9834F, -10.6961F, 0.2182F, -0.7854F, 0.0F));

		ModelPartData bone31 = engine.addChild("bone31", ModelPartBuilder.create().uv(249, 303).cuboid(-1.8358F, -5.0F, -0.8284F, 4.0F, 5.0F, 2.0F, new Dilation(0.0F))
				.uv(246, 144).cuboid(-3.8358F, -11.0F, -0.8284F, 8.0F, 3.0F, 2.0F, new Dilation(0.0F))
				.uv(133, 302).cuboid(-2.8358F, -8.0F, -0.8284F, 6.0F, 3.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(10.4283F, 5.9834F, -10.6961F, 0.2182F, -0.7854F, 0.0F));

		ModelPartData bone9 = engine.addChild("bone9", ModelPartBuilder.create().uv(305, 30).cuboid(-2.0F, 1.1667F, -1.0F, 4.0F, 5.0F, 2.0F, new Dilation(0.0F))
				.uv(299, 234).cuboid(-4.0F, -4.8333F, -1.0F, 8.0F, 3.0F, 2.0F, new Dilation(0.0F))
				.uv(167, 304).cuboid(-3.0F, -1.8333F, -1.0F, 6.0F, 3.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(11.3567F, 0.0F, 0.0F, -2.9234F, -0.7854F, 3.1416F));

		ModelPartData bone32 = engine.addChild("bone32", ModelPartBuilder.create(), ModelTransform.of(-0.0717F, 5.9834F, -10.6961F, 0.2182F, 0.7854F, 0.0F));

		ModelPartData bone42 = engine.addChild("bone42", ModelPartBuilder.create(), ModelTransform.of(0.7326F, 38.2032F, -13.485F, 0.0F, 0.7854F, 0.0F));

		ModelPartData cube_r88 = bone42.addChild("cube_r88", ModelPartBuilder.create().uv(272, 229).cuboid(-5.5F, -12.3736F, -3.6492F, 11.0F, 9.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(-2.0F, 5.0302F, -13.2189F, -0.5672F, 0.0F, 0.0F));

		ModelPartData bone43 = bone42.addChild("bone43", ModelPartBuilder.create(), ModelTransform.of(-2.0F, -2.9964F, 29.4416F, 0.5672F, 0.0F, 0.0F));

		ModelPartData cube_r89 = bone43.addChild("cube_r89", ModelPartBuilder.create().uv(272, 217).cuboid(-5.5F, -12.3736F, 0.6492F, 11.0F, 9.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 7.5266F, 1.3395F, 0.0F, -3.1416F, 0.0F));

		ModelPartData bone44 = engine.addChild("bone44", ModelPartBuilder.create(), ModelTransform.of(20.6783F, 44.2334F, -29.1961F, 0.0F, -0.7854F, 0.0F));

		ModelPartData bone47 = engine.addChild("bone47", ModelPartBuilder.create(), ModelTransform.pivot(22.6783F, 45.2334F, 6.3039F));

		ModelPartData bone48 = engine.addChild("bone48", ModelPartBuilder.create(), ModelTransform.of(22.6783F, 45.2334F, -20.6961F, 0.0F, 0.7854F, 0.0F));

		ModelPartData bone49 = engine.addChild("bone49", ModelPartBuilder.create(), ModelTransform.pivot(22.6783F, 45.2334F, -20.6961F));

		ModelPartData cube_r90 = bone49.addChild("cube_r90", ModelPartBuilder.create().uv(227, 209).cuboid(-1.4667F, -3.6251F, -8.1936F, 22.0F, 17.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(-17.0643F, -11.8833F, 1.3749F, -0.5236F, -0.7854F, 0.0F));

		ModelPartData cube_r91 = bone49.addChild("cube_r91", ModelPartBuilder.create().uv(224, 171).cuboid(-11.0F, -20.4641F, -3.0F, 22.0F, 17.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(-32.6893F, 0.1029F, 30.7678F, 2.618F, 0.7854F, 3.1416F));

		ModelPartData bone2 = engine.addChild("bone2", ModelPartBuilder.create(), ModelTransform.of(20.6783F, 45.2334F, 8.3039F, 0.0F, -2.3562F, 0.0F));

		ModelPartData bone50 = bone2.addChild("bone50", ModelPartBuilder.create().uv(0, 223).cuboid(-11.2647F, -17.5F, -1.0F, 22.0F, 17.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.7647F, -3.7141F, -2.25F, -0.5236F, 0.0F, 0.0F));

		ModelPartData cube_r92 = bone50.addChild("cube_r92", ModelPartBuilder.create().uv(227, 227).cuboid(-14.8765F, -1.0132F, -13.2176F, 22.0F, 17.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(-3.9164F, -20.3759F, 18.9537F, 2.0944F, 0.0F, -3.1416F));

		ModelPartData bone7 = engine.addChild("bone7", ModelPartBuilder.create(), ModelTransform.of(23.6783F, 45.2334F, -22.6961F, 0.0F, -0.7854F, 0.0F));

		ModelPartData bone10 = engine.addChild("bone10", ModelPartBuilder.create().uv(41, 308).cuboid(-2.1642F, -5.0F, -0.8284F, 4.0F, 5.0F, 2.0F, new Dilation(0.0F))
				.uv(300, 95).cuboid(-4.1642F, -11.0F, -0.8284F, 8.0F, 3.0F, 2.0F, new Dilation(0.0F))
				.uv(304, 304).cuboid(-3.1642F, -8.0F, -0.8284F, 6.0F, 3.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.9283F, 5.9834F, -10.6961F, 0.2182F, 0.7854F, 0.0F));
		return TexturedModelData.of(modelData, 512, 512);
	}

	@Override
	public void setAngles(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
	}

	@Override
	public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
	}

	public void render(Tardis tardis, MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red,
					   float green, float blue, float alpha) {
		engine.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
	}

	public void animate(EngineBlockEntity engine, Animation animation, float progress, float speed) {
		this.getPart().traverse().forEach(ModelPart::resetTransform);
		this.updateAnimation(engine.ANIM_STATE, animation, progress, speed);
	}

	@Override
	public ModelPart getPart() {
		return engine;
	}
}