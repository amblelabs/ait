package dev.amble.ait.client.models.exteriors.pixelconsistant;

import dev.amble.ait.api.tardis.link.v2.Linkable;
import dev.amble.ait.client.AITModClient;
import dev.amble.ait.client.models.exteriors.ExteriorModel;
import dev.amble.ait.client.tardis.ClientTardis;
import dev.amble.ait.client.util.ResourcePackUtil;
import dev.amble.ait.core.blockentities.ExteriorBlockEntity;
import dev.amble.ait.core.tardis.handler.DoorHandler;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.animation.Animation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;

public class ConsistantPoliceBoxModel extends ExteriorModel {
	private final ModelPart bone;


	public ConsistantPoliceBoxModel(ModelPart root) {
		this.bone = root.getChild("bone");
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		ModelPartData bone = modelPartData.addChild("bone", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 24.0F, 0.0F));

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

		ModelPartData allexteriors = TARDIS.addChild("allexteriors", ModelPartBuilder.create().uv(88, 28).cuboid(-10.0F, -35.0F, 8.0F, 20.0F, 33.0F, 0.0F, new Dilation(0.0F))
		.uv(0, 53).cuboid(-11.0F, -35.001F, -10.0F, 22.0F, 0.0F, 21.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData upperlip = allexteriors.addChild("upperlip", ModelPartBuilder.create().uv(38, 130).cuboid(-9.0F, -1.0F, 10.5F, 18.0F, 2.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -36.0F, 0.0F));

		ModelPartData cube_r3 = upperlip.addChild("cube_r3", ModelPartBuilder.create().uv(38, 130).cuboid(-9.0F, -1.0F, 10.5F, 18.0F, 2.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.5708F, 0.0F));

		ModelPartData cube_r4 = upperlip.addChild("cube_r4", ModelPartBuilder.create().uv(38, 130).cuboid(-9.0F, -1.0F, -0.5F, 18.0F, 2.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(11.0F, 0.0F, 0.0F, 0.0F, 1.5708F, 0.0F));

		ModelPartData cube_r5 = upperlip.addChild("cube_r5", ModelPartBuilder.create().uv(38, 130).cuboid(-9.0F, -0.05F, 0.0F, 18.0F, 2.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -0.95F, -9.6F, -3.1416F, 0.0F, 3.1416F));

		ModelPartData cube_r6 = upperlip.addChild("cube_r6", ModelPartBuilder.create().uv(38, 130).cuboid(-9.0F, -1.0F, -0.5F, 18.0F, 2.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, -11.0F, 0.0F, 3.1416F, 0.0F));

		ModelPartData pillars = allexteriors.addChild("pillars", ModelPartBuilder.create().uv(76, 125).cuboid(19.5F, -20.5F, -1.5F, 3.0F, 41.0F, 3.0F, new Dilation(0.0F))
		.uv(76, 125).mirrored().cuboid(19.5F, -20.5F, 19.5F, 3.0F, 41.0F, 3.0F, new Dilation(0.0F)).mirrored(false)
		.uv(76, 125).cuboid(-1.5F, -20.5F, 19.5F, 3.0F, 41.0F, 3.0F, new Dilation(0.0F))
		.uv(76, 125).mirrored().cuboid(-1.5F, -20.5F, -1.5F, 3.0F, 41.0F, 3.0F, new Dilation(0.0F)).mirrored(false)
		.uv(244, 3).mirrored().cuboid(-1.5F, -20.5F, -1.5F, 3.0F, 41.0F, 3.0F, new Dilation(0.01F)).mirrored(false)
		.uv(244, 3).cuboid(19.5F, -20.5F, -1.5F, 3.0F, 41.0F, 3.0F, new Dilation(0.01F)), ModelTransform.pivot(-10.5F, -21.0F, -10.5F));

		ModelPartData cube_r7 = pillars.addChild("cube_r7", ModelPartBuilder.create().uv(244, 3).cuboid(-1.5F, -20.5F, -1.5F, 3.0F, 41.0F, 3.0F, new Dilation(0.01F)), ModelTransform.of(21.0F, 0.0F, 21.0F, 0.0F, -1.5708F, 0.0F));

		ModelPartData cube_r8 = pillars.addChild("cube_r8", ModelPartBuilder.create().uv(244, 3).mirrored().cuboid(-1.5F, -20.5F, -1.5F, 3.0F, 41.0F, 3.0F, new Dilation(0.01F)).mirrored(false), ModelTransform.of(0.0F, 0.0F, 21.0F, 0.0F, 1.5708F, 0.0F));

		ModelPartData walls = allexteriors.addChild("walls", ModelPartBuilder.create().uv(79, 91).cuboid(-9.0F, -16.5F, 10.0F, 18.0F, 33.0F, 1.0F, new Dilation(0.0F))
		.uv(80, 74).cuboid(10.0F, -16.5F, -9.0F, 1.0F, 33.0F, 18.0F, new Dilation(0.0F))
		.uv(80, 74).mirrored().cuboid(-11.0F, -16.5F, -9.0F, 1.0F, 33.0F, 18.0F, new Dilation(0.0F)).mirrored(false)
		.uv(4, 53).cuboid(11.3F, -16.5F, -1.0F, 0.0F, 33.0F, 2.0F, new Dilation(0.0F))
		.uv(4, 53).cuboid(-11.3F, -16.5F, -1.0F, 0.0F, 33.0F, 2.0F, new Dilation(0.0F))
		.uv(50, 134).cuboid(-9.8F, -16.5F, -1.0F, 0.0F, 33.0F, 2.0F, new Dilation(0.0F))
		.uv(46, 134).mirrored().cuboid(9.8F, -16.5F, -1.0F, 0.0F, 33.0F, 2.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.pivot(0.0F, -18.5F, 0.0F));

		ModelPartData cube_r9 = walls.addChild("cube_r9", ModelPartBuilder.create().uv(4, 55).cuboid(-1.0F, -16.5F, -0.1F, 2.0F, 33.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 11.2F, 0.0F, 3.1416F, 0.0F));

		ModelPartData base = allexteriors.addChild("base", ModelPartBuilder.create().uv(0, 0).cuboid(-13.0F, -4.0F, -14.0F, 26.0F, 2.0F, 26.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 2.0F, 1.0F));

		ModelPartData coral = TARDIS.addChild("coral", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData sign9 = coral.addChild("sign9", ModelPartBuilder.create().uv(144, 126).cuboid(-10.0F, -2.5F, -13.0F, 20.0F, 5.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -38.3F, 0.0F));

		ModelPartData cube_r10 = sign9.addChild("cube_r10", ModelPartBuilder.create().uv(144, 126).cuboid(-10.0F, -2.5F, -13.0F, 20.0F, 5.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.5708F, 0.0F));

		ModelPartData cube_r11 = sign9.addChild("cube_r11", ModelPartBuilder.create().uv(144, 126).cuboid(-10.0F, -2.5F, -1.0F, 20.0F, 5.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 12.0F, 0.0F, 3.1416F, 0.0F));

		ModelPartData cube_r12 = sign9.addChild("cube_r12", ModelPartBuilder.create().uv(144, 126).cuboid(-10.0F, -2.5F, -1.0F, 20.0F, 5.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(12.0F, 0.0F, 0.0F, 0.0F, -1.5708F, 0.0F));

		ModelPartData roof9 = coral.addChild("roof9", ModelPartBuilder.create().uv(0, 28).cuboid(-11.0F, 13.5F, -11.0F, 22.0F, 3.0F, 22.0F, new Dilation(0.0F))
		.uv(0, 74).cuboid(-10.0F, 11.7F, -10.0F, 20.0F, 2.0F, 20.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -56.7F, 0.0F));

		ModelPartData lamp9 = coral.addChild("lamp9", ModelPartBuilder.create().uv(86, 66).cuboid(-1.0F, 6.7F, -1.9F, 3.0F, 4.0F, 3.0F, new Dilation(0.0F))
		.uv(86, 61).cuboid(-1.5F, 5.8F, -2.4F, 4.0F, 1.0F, 4.0F, new Dilation(0.0F))
		.uv(86, 61).cuboid(-1.5F, 10.7F, -2.4F, 4.0F, 1.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(-0.5F, -56.2F, 0.4F));

		ModelPartData cube_r13 = lamp9.addChild("cube_r13", ModelPartBuilder.create().uv(98, 66).cuboid(-1.5F, -0.1F, -1.5F, 3.0F, 1.0F, 3.0F, new Dilation(-0.1F)), ModelTransform.of(0.5F, 5.3F, -0.4F, 0.0F, 0.7854F, 0.0F));

		ModelPartData cube_r14 = lamp9.addChild("cube_r14", ModelPartBuilder.create().uv(76, 96).cuboid(-0.5F, -2.5F, 0.0F, 1.0F, 4.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(2.1464F, 9.2F, 1.2464F, 0.0F, -0.7854F, 0.0F));

		ModelPartData cube_r15 = lamp9.addChild("cube_r15", ModelPartBuilder.create().uv(76, 96).cuboid(-0.5F, -2.5F, 0.0F, 1.0F, 4.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(-1.1464F, 9.2F, 1.2464F, 0.0F, 0.7854F, 0.0F));

		ModelPartData cube_r16 = lamp9.addChild("cube_r16", ModelPartBuilder.create().uv(76, 96).cuboid(-0.5F, -2.5F, 0.0F, 1.0F, 4.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(2.1464F, 9.2F, -2.0465F, 0.0F, 0.7854F, 0.0F));

		ModelPartData cube_r17 = lamp9.addChild("cube_r17", ModelPartBuilder.create().uv(76, 96).cuboid(-0.5F, -2.5F, 0.0F, 1.0F, 4.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(-1.1464F, 9.2F, -2.0465F, 0.0F, -0.7854F, 0.0F));

		ModelPartData toyota = TARDIS.addChild("toyota", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData roof11 = toyota.addChild("roof11", ModelPartBuilder.create().uv(0, 231).cuboid(-11.0F, -2.5F, -11.0F, 22.0F, 3.0F, 22.0F, new Dilation(0.0F))
		.uv(67, 230).cuboid(-10.0F, -3.5F, -10.0F, 20.0F, 1.0F, 20.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -40.7F, 0.0F));

		ModelPartData signs11 = toyota.addChild("signs11", ModelPartBuilder.create().uv(0, 221).cuboid(-10.0F, -2.5F, -13.0F, 20.0F, 5.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -38.3F, 0.0F));

		ModelPartData cube_r18 = signs11.addChild("cube_r18", ModelPartBuilder.create().uv(0, 221).cuboid(-10.0F, -2.5F, -13.0F, 20.0F, 5.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.5708F, 0.0F));

		ModelPartData cube_r19 = signs11.addChild("cube_r19", ModelPartBuilder.create().uv(0, 221).cuboid(-10.0F, -2.5F, -1.0F, 20.0F, 5.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 12.0F, 0.0F, 3.1416F, 0.0F));

		ModelPartData cube_r20 = signs11.addChild("cube_r20", ModelPartBuilder.create().uv(0, 221).cuboid(-10.0F, -2.5F, -1.0F, 20.0F, 5.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(12.0F, 0.0F, 0.0F, 0.0F, -1.5708F, 0.0F));

		ModelPartData lamp11 = toyota.addChild("lamp11", ModelPartBuilder.create().uv(65, 207).cuboid(-2.0F, 2.05F, -2.0F, 4.0F, 1.0F, 4.0F, new Dilation(0.1F))
		.uv(65, 212).cuboid(-1.5F, -3.35F, -1.5F, 3.0F, 1.0F, 3.0F, new Dilation(-0.1F)), ModelTransform.pivot(0.0F, -47.25F, 0.0F));

		ModelPartData cube_r21 = lamp11.addChild("cube_r21", ModelPartBuilder.create().uv(61, 219).cuboid(0.0F, -2.65F, -2.0F, 0.0F, 5.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, -3.1416F, -0.7854F, 3.1416F));

		ModelPartData cube_r22 = lamp11.addChild("cube_r22", ModelPartBuilder.create().uv(65, 207).cuboid(-2.0F, -0.5F, -2.0F, 4.0F, 1.0F, 4.0F, new Dilation(0.0F))
		.uv(61, 219).cuboid(0.0F, -0.4F, -2.0F, 0.0F, 5.0F, 4.0F, new Dilation(0.0F))
		.uv(69, 219).cuboid(-1.5F, 0.4F, -1.5F, 3.0F, 4.0F, 3.0F, new Dilation(-0.02F)), ModelTransform.of(0.0F, -2.25F, 0.0F, 0.0F, -0.7854F, 0.0F));

		ModelPartData latest = TARDIS.addChild("latest", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData sign13 = latest.addChild("sign13", ModelPartBuilder.create().uv(78, 0).cuboid(-10.0F, -2.5F, -13.0F, 20.0F, 5.0F, 2.0F, new Dilation(0.0F))
		.uv(78, 0).cuboid(-10.0F, -2.5F, -13.0F, 20.0F, 5.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -37.9F, 0.0F));

		ModelPartData cube_r23 = sign13.addChild("cube_r23", ModelPartBuilder.create().uv(78, 0).cuboid(-10.0F, -2.5F, -13.0F, 20.0F, 5.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.5708F, 0.0F));

		ModelPartData cube_r24 = sign13.addChild("cube_r24", ModelPartBuilder.create().uv(78, 0).cuboid(-10.0F, -2.5F, -1.0F, 20.0F, 5.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 12.0F, 0.0F, 3.1416F, 0.0F));

		ModelPartData cube_r25 = sign13.addChild("cube_r25", ModelPartBuilder.create().uv(78, 0).cuboid(-10.0F, -2.5F, -1.0F, 20.0F, 5.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(12.0F, 0.0F, 0.0F, 0.0F, -1.5708F, 0.0F));

		ModelPartData cube_r26 = sign13.addChild("cube_r26", ModelPartBuilder.create().uv(214, 0).cuboid(-10.0F, -3.44F, -0.5F, 20.0F, 0.0F, 2.0F, new Dilation(0.0F))
		.uv(150, 249).cuboid(-10.0F, -3.45F, -1.5F, 20.0F, 5.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.95F, -9.6F, -3.1416F, 0.0F, 3.1416F));

		ModelPartData roof13 = latest.addChild("roof13", ModelPartBuilder.create().uv(98, 203).cuboid(-11.0F, -2.8F, -11.0F, 22.0F, 3.0F, 22.0F, new Dilation(0.0F))
		.uv(176, 225).cuboid(-10.0F, -3.5F, -10.0F, 20.0F, 1.0F, 20.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -40.1F, 0.0F));

		ModelPartData lamp13 = latest.addChild("lamp13", ModelPartBuilder.create().uv(239, 218).cuboid(-2.0F, 1.85F, -2.0F, 4.0F, 1.0F, 4.0F, new Dilation(0.0F))
		.uv(239, 223).cuboid(-1.5F, -4.45F, -1.5F, 3.0F, 1.0F, 3.0F, new Dilation(-0.1F)), ModelTransform.pivot(0.0F, -45.95F, 0.0F));

		ModelPartData cube_r27 = lamp13.addChild("cube_r27", ModelPartBuilder.create().uv(238, 233).mirrored().cuboid(0.0F, -2.65F, 1.4F, 0.0F, 5.0F, 1.0F, new Dilation(0.0F)).mirrored(false)
		.uv(240, 237).cuboid(-2.0F, -3.65F, -2.0F, 4.0F, 2.0F, 4.0F, new Dilation(0.0F))
		.uv(238, 233).cuboid(0.0F, -2.65F, -2.4F, 0.0F, 5.0F, 1.0F, new Dilation(0.0F))
		.uv(243, 230).cuboid(-1.5F, -1.85F, -1.5F, 3.0F, 4.0F, 3.0F, new Dilation(-0.1F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -0.7854F, 0.0F));

		ModelPartData cube_r28 = lamp13.addChild("cube_r28", ModelPartBuilder.create().uv(238, 233).cuboid(0.0F, -2.65F, 1.4F, 0.0F, 5.0F, 1.0F, new Dilation(0.0F))
		.uv(238, 233).mirrored().cuboid(0.0F, -2.65F, -2.4F, 0.0F, 5.0F, 1.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.7854F, 0.0F));
		return TexturedModelData.of(modelData, 256, 256);
	}

	@Override
	public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red,
					   float green, float blue, float alpha) {
		bone.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
	}

	@Override
	public ModelPart getPart() {
		return bone;
	}

	@Override
	public void renderWithAnimations(ClientTardis tardis, ExteriorBlockEntity exterior, ModelPart root, MatrixStack matrices,
									 VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float pAlpha) {
		matrices.push();
		if (ResourcePackUtil.isPixelConsistentPackActive()) {
			matrices.scale(1F, 1F, 1F);
			matrices.translate(0, -1.5f, 0);
		} else {
			matrices.scale(0.63F, 0.63F, 0.63F);
			matrices.translate(0, -1.5f, 0);
		}

		this.renderDoors(tardis, exterior, root, matrices, vertices, light, overlay, red, green, blue, pAlpha, false);

		super.renderWithAnimations(tardis, exterior, root, matrices, vertices, light, overlay, red, green, blue, pAlpha);
		matrices.pop();
	}

	@Override
	public <T extends Entity & Linkable> void renderEntity(T falling, ModelPart root, MatrixStack matrices,
														   VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
		if (!falling.isLinked())
			return;

		matrices.push();
		if (ResourcePackUtil.isPixelConsistentPackActive()) {
			matrices.scale(0.63F, 0.63F, 0.63F);
			matrices.translate(0, -1.5f, 0);
		} else {
			matrices.scale(0.63F, 0.63F, 0.63F);
			matrices.translate(0, -1.5f, 0);
		}

		DoorHandler door = falling.tardis().get().door();

		if (!AITModClient.CONFIG.animateDoors) {
			this.bone.getChild("Doors").getChild("left_door").yaw = (door.isLeftOpen() || door.isOpen()) ? -5F : 0.0F;
			this.bone.getChild("Doors").getChild("right_door").yaw = (door.isRightOpen() || door.areBothOpen())
					? 5F
					: 0.0F;
		} else {
			float maxRot = 90f;
			this.bone.getChild("Doors").getChild("left_door").yaw =(float) Math.toRadians(maxRot*door.getLeftRot());
			this.bone.getChild("Doors").getChild("right_door").yaw =(float) -Math.toRadians(maxRot*door.getRightRot());
		}

		super.renderEntity(falling, root, matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
		matrices.pop();
	}

	@Override
	public Animation getAnimationForDoorState(DoorHandler.AnimationDoorState state) {
		return Animation.Builder.create(0).build();
	}

	@Override
	public void renderDoors(ClientTardis tardis, ExteriorBlockEntity exterior, ModelPart root, MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float pAlpha, boolean isBOTI) {
		DoorHandler door = tardis.door();

		if (!AITModClient.CONFIG.animateDoors) {
			this.bone.getChild("Doors").getChild("left_door").yaw = (door.isLeftOpen() || door.isOpen()) ? -5F : 0.0F;
			this.bone.getChild("Doors").getChild("right_door").yaw = (door.isRightOpen() || door.areBothOpen())
					? 5F
					: 0.0F;
		} else {
			float maxRot = 90f;
			this.bone.getChild("Doors").getChild("left_door").yaw =(float) Math.toRadians(maxRot*door.getLeftRot());
			this.bone.getChild("Doors").getChild("right_door").yaw =(float) -Math.toRadians(maxRot*door.getRightRot());
		}

		if (isBOTI) {
			matrices.push();
			matrices.scale(0.63F, 0.63F, 0.63F);
			matrices.translate(0, 0f, -0.01);
			this.bone.getChild("Doors").render(matrices, vertices, light, overlay, red, green, blue, pAlpha);
			matrices.pop();
		}
	}
}