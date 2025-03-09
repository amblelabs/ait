package dev.amble.ait.module.planet.client.models.wearables;

import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;

public class SpacesuitModel extends EntityModel<LivingEntity> {
    public final ModelPart bone;
    public final ModelPart RightLeg;
    public final ModelPart right_leg_pant;
    public final ModelPart RightFoot;
    public final ModelPart LeftLeg;
    public final ModelPart left_leg_pant;
    public final ModelPart LeftFoot;
    public final ModelPart RightArm;
    public final ModelPart LeftArm;
    public final ModelPart Body;
    public final ModelPart bodyreal;
    public final ModelPart flagsize2;
    public final ModelPart flagsize1;
    public final ModelPart agencylogo1;
    public final ModelPart wirex;
    public final ModelPart wirex2;
    public final ModelPart wirex3;
    public final ModelPart wirex4;
    public final ModelPart wirex5;
    public final ModelPart wirex6;
    public final ModelPart Head;
    public SpacesuitModel(ModelPart root) {
        this.bone = root.getChild("bone");
        this.RightLeg = this.bone.getChild("RightLeg");
        this.right_leg_pant = this.RightLeg.getChild("right_leg_pant");
        this.RightFoot = this.RightLeg.getChild("RightFoot");
        this.LeftLeg = this.bone.getChild("LeftLeg");
        this.left_leg_pant = this.LeftLeg.getChild("left_leg_pant");
        this.LeftFoot = this.LeftLeg.getChild("LeftFoot");
        this.RightArm = this.bone.getChild("RightArm");
        this.LeftArm = this.bone.getChild("LeftArm");
        this.Body = this.bone.getChild("Body");
        this.bodyreal = this.Body.getChild("bodyreal");
        this.flagsize2 = this.bodyreal.getChild("flagsize2");
        this.flagsize1 = this.bodyreal.getChild("flagsize1");
        this.agencylogo1 = this.flagsize1.getChild("agencylogo1");
        this.wirex = this.bodyreal.getChild("wirex");
        this.wirex2 = this.bodyreal.getChild("wirex2");
        this.wirex3 = this.bodyreal.getChild("wirex3");
        this.wirex4 = this.bodyreal.getChild("wirex4");
        this.wirex5 = this.bodyreal.getChild("wirex5");
        this.wirex6 = this.bodyreal.getChild("wirex6");
        this.Head = this.bone.getChild("Head");
    }
    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData bone = modelPartData.addChild("bone", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 24.0F, 0.0F));

        ModelPartData RightLeg = bone.addChild("RightLeg", ModelPartBuilder.create(), ModelTransform.pivot(-1.9F, -12.0F, 0.0F));

        ModelPartData right_leg_pant = RightLeg.addChild("right_leg_pant", ModelPartBuilder.create().uv(50, 17).cuboid(-3.9F, -4.75F, -2.0F, 4.0F, 8.0F, 4.0F, new Dilation(0.25F))
                .uv(58, 0).cuboid(-3.9F, -5.0F, -2.0F, 4.0F, 12.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(1.9F, 4.75F, 0.0F));

        ModelPartData RightFoot = RightLeg.addChild("RightFoot", ModelPartBuilder.create().uv(75, 0).cuboid(-3.9F, -4.0F, -2.0F, 4.0F, 4.0F, 4.0F, new Dilation(0.25F))
                .uv(54, 34).cuboid(-3.9F, -4.0F, -2.0F, 4.0F, 4.0F, 4.0F, new Dilation(0.5F)), ModelTransform.pivot(1.9F, 11.75F, 0.0F));

        ModelPartData LeftLeg = bone.addChild("LeftLeg", ModelPartBuilder.create(), ModelTransform.pivot(1.9F, -12.125F, 0.0F));

        ModelPartData left_leg_pant = LeftLeg.addChild("left_leg_pant", ModelPartBuilder.create().uv(58, 0).mirrored().cuboid(-0.1F, -12.25F, -2.0F, 4.0F, 12.0F, 4.0F, new Dilation(0.0F)).mirrored(false)
                .uv(50, 17).mirrored().cuboid(-0.1F, -12.0F, -2.0F, 4.0F, 8.0F, 4.0F, new Dilation(0.25F)).mirrored(false), ModelTransform.pivot(-1.9F, 12.125F, 0.0F));

        ModelPartData LeftFoot = LeftLeg.addChild("LeftFoot", ModelPartBuilder.create().uv(75, 0).mirrored().cuboid(-0.1F, -4.0F, -2.0F, 4.0F, 4.0F, 4.0F, new Dilation(0.25F)).mirrored(false)
                .uv(54, 34).mirrored().cuboid(-0.1F, -4.0F, -2.0F, 4.0F, 4.0F, 4.0F, new Dilation(0.5F)).mirrored(false), ModelTransform.pivot(-1.9F, 11.875F, 0.0F));

        ModelPartData RightArm = bone.addChild("RightArm", ModelPartBuilder.create().uv(47, 47).mirrored().cuboid(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new Dilation(0.0F)).mirrored(false)
                .uv(30, 47).mirrored().cuboid(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new Dilation(0.25F)).mirrored(false), ModelTransform.pivot(-5.0F, -22.0F, 0.0F));

        ModelPartData LeftArm = bone.addChild("LeftArm", ModelPartBuilder.create().uv(47, 47).cuboid(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new Dilation(0.0F))
                .uv(30, 47).cuboid(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new Dilation(0.25F))
                .uv(122, 1).cuboid(3.3F, -0.65F, -1.5F, 0.0F, 2.0F, 3.0F, new Dilation(0.0F))
                .uv(122, 11).cuboid(3.3F, -1.15F, -1.5F, 0.0F, 3.0F, 3.0F, new Dilation(0.0F))
                .uv(122, 62).cuboid(3.1F, -1.15F, -1.53F, 0.0F, 3.0F, 3.0F, new Dilation(-0.3F))
                .uv(122, 66).cuboid(3.12F, -1.15F, -1.53F, 0.0F, 3.0F, 3.0F, new Dilation(-0.2F)), ModelTransform.pivot(5.0F, -22.0F, 0.0F));

        ModelPartData Body = bone.addChild("Body", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, -24.0F, 0.0F));

        ModelPartData bodyreal = Body.addChild("bodyreal", ModelPartBuilder.create().uv(33, 0).cuboid(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new Dilation(0.0F))
                .uv(79, 14).cuboid(-4.0F, -3.0F, 2.0F, 8.0F, 15.0F, 4.0F, new Dilation(0.5F))
                .uv(79, 35).cuboid(-4.0F, -3.0F, 2.0F, 8.0F, 15.0F, 4.0F, new Dilation(0.4F))
                .uv(0, 5).mirrored().cuboid(-3.0F, 8.0F, -3.0F, 2.0F, 2.0F, 1.0F, new Dilation(-0.2F)).mirrored(false)
                .uv(0, 5).cuboid(1.0F, 8.0F, -3.0F, 2.0F, 2.0F, 1.0F, new Dilation(-0.2F))
                .uv(2, 2).cuboid(1.0F, 6.0F, -3.0F, 2.0F, 2.0F, 1.0F, new Dilation(-0.2F))
                .uv(2, 2).cuboid(1.0F, 4.0F, -3.0F, 2.0F, 2.0F, 1.0F, new Dilation(-0.2F))
                .uv(2, 2).mirrored().cuboid(-3.0F, 4.0F, -3.0F, 2.0F, 2.0F, 1.0F, new Dilation(-0.2F)).mirrored(false)
                .uv(2, 2).mirrored().cuboid(-3.0F, 6.0F, -3.0F, 2.0F, 2.0F, 1.0F, new Dilation(-0.2F)).mirrored(false)
                .uv(18, 1).cuboid(-2.0F, 0.7F, -2.8F, 4.0F, 3.0F, 1.0F, new Dilation(0.1F))
                .uv(29, 30).cuboid(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new Dilation(0.275F))
                .uv(1, 35).cuboid(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new Dilation(0.4F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData BodyLayer_r1 = bodyreal.addChild("BodyLayer_r1", ModelPartBuilder.create().uv(40, 65).cuboid(-6.0F, 0.0F, -2.0F, 7.0F, 4.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(2.5F, 0.25F, 0.0F, 0.3491F, 0.0F, 0.0F));

        ModelPartData flagsize2 = bodyreal.addChild("flagsize2", ModelPartBuilder.create().uv(116, 24).cuboid(-3.0F, -27.4F, 6.5F, 6.0F, 3.0F, 0.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 24.0F, 0.0F));

        ModelPartData flagsize1 = bodyreal.addChild("flagsize1", ModelPartBuilder.create().uv(118, 0).cuboid(-2.5F, -27.4F, 6.5F, 5.0F, 3.0F, 0.0F, new Dilation(0.0F))
                .uv(116, 20).mirrored().cuboid(-0.7F, -27.8F, 6.12F, 4.0F, 4.0F, 0.0F, new Dilation(-0.4F)).mirrored(false)
                .uv(116, 24).cuboid(-3.3F, -27.8F, 6.12F, 4.0F, 4.0F, 0.0F, new Dilation(-0.4F)), ModelTransform.pivot(0.0F, 24.0F, 0.0F));

        ModelPartData agencylogo1 = flagsize1.addChild("agencylogo1", ModelPartBuilder.create().uv(124, 11).mirrored().cuboid(-3.9F, -22.9F, -2.3F, 2.0F, 2.0F, 0.0F, new Dilation(0.0F)).mirrored(false)
                .uv(124, 37).cuboid(-3.9F, -22.4F, -2.31F, 2.0F, 1.0F, 0.0F, new Dilation(0.0F))
                .uv(124, 11).cuboid(-1.0F, -22.9F, 6.5F, 2.0F, 2.0F, 0.0F, new Dilation(0.0F))
                .uv(124, 37).cuboid(-1.0F, -22.4F, 6.51F, 2.0F, 1.0F, 0.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData Body_r1 = agencylogo1.addChild("Body_r1", ModelPartBuilder.create().uv(92, 73).cuboid(-1.0F, -0.5F, 0.0F, 2.0F, 1.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -21.9F, 6.51F, 0.0F, 0.0F, 1.5708F));

        ModelPartData Body_r2 = agencylogo1.addChild("Body_r2", ModelPartBuilder.create().uv(124, 7).cuboid(-1.0F, -0.5F, 0.0F, 2.0F, 1.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -21.9F, 6.51F, 0.0F, 0.0F, 0.7854F));

        ModelPartData Body_r3 = agencylogo1.addChild("Body_r3", ModelPartBuilder.create().uv(124, 7).cuboid(-1.0F, -0.5F, 0.0F, 2.0F, 1.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(-2.9F, -21.9F, -2.31F, 0.0F, 0.0F, 2.3126F));

        ModelPartData wirex = bodyreal.addChild("wirex", ModelPartBuilder.create(), ModelTransform.pivot(4.5036F, 7.2208F, 0.6127F));

        ModelPartData Body_r4 = wirex.addChild("Body_r4", ModelPartBuilder.create().uv(4, 15).cuboid(1.0F, -0.5F, -0.5F, 2.0F, 1.0F, 1.0F, new Dilation(-0.1F)), ModelTransform.of(0.0F, 0.0F, 0.0F, -3.1416F, -0.9599F, -2.3562F));

        ModelPartData Body_r5 = wirex.addChild("Body_r5", ModelPartBuilder.create().uv(4, 15).mirrored().cuboid(-1.0F, -0.5F, -0.5F, 2.0F, 1.0F, 1.0F, new Dilation(-0.1F)).mirrored(false), ModelTransform.of(-0.2626F, -0.2626F, 0.148F, -3.1416F, -1.2217F, -2.3562F));

        ModelPartData Body_r6 = wirex.addChild("Body_r6", ModelPartBuilder.create().uv(4, 15).cuboid(1.0F, -0.5F, -0.5F, 2.0F, 1.0F, 1.0F, new Dilation(-0.1F)), ModelTransform.of(-0.3272F, -0.3272F, -3.3839F, 0.0F, -1.4399F, 0.7854F));

        ModelPartData Body_r7 = wirex.addChild("Body_r7", ModelPartBuilder.create().uv(4, 15).cuboid(0.0F, -0.7F, -0.3F, 2.0F, 1.0F, 1.0F, new Dilation(-0.1F)), ModelTransform.of(-2.5036F, -2.2208F, -3.1127F, 0.0F, 0.0F, 0.7854F));

        ModelPartData Body_r8 = wirex.addChild("Body_r8", ModelPartBuilder.create().uv(4, 15).mirrored().cuboid(-1.0F, -0.5F, -0.5F, 2.0F, 1.0F, 1.0F, new Dilation(-0.1F)).mirrored(false), ModelTransform.of(-0.634F, -0.634F, -2.4918F, 0.0F, -0.5672F, 0.7854F));

        ModelPartData wirex2 = bodyreal.addChild("wirex2", ModelPartBuilder.create(), ModelTransform.pivot(4.5036F, 9.2208F, 0.6127F));

        ModelPartData Body_r9 = wirex2.addChild("Body_r9", ModelPartBuilder.create().uv(4, 15).cuboid(1.0F, -0.5F, -0.5F, 2.0F, 1.0F, 1.0F, new Dilation(-0.1F)), ModelTransform.of(0.0F, 0.0F, 0.0F, -3.1416F, -0.9599F, -2.3562F));

        ModelPartData Body_r10 = wirex2.addChild("Body_r10", ModelPartBuilder.create().uv(4, 15).mirrored().cuboid(-1.0F, -0.5F, -0.5F, 2.0F, 1.0F, 1.0F, new Dilation(-0.1F)).mirrored(false), ModelTransform.of(-0.2626F, -0.2626F, 0.148F, -3.1416F, -1.2217F, -2.3562F));

        ModelPartData Body_r11 = wirex2.addChild("Body_r11", ModelPartBuilder.create().uv(4, 15).cuboid(1.0F, -0.5F, -0.5F, 2.0F, 1.0F, 1.0F, new Dilation(-0.1F)), ModelTransform.of(-0.3272F, -0.3272F, -3.3839F, 0.0F, -1.4399F, 0.7854F));

        ModelPartData Body_r12 = wirex2.addChild("Body_r12", ModelPartBuilder.create().uv(4, 15).cuboid(0.0F, -0.7F, -0.3F, 2.0F, 1.0F, 1.0F, new Dilation(-0.1F)), ModelTransform.of(-2.5036F, -2.2208F, -3.1127F, 0.0F, 0.0F, 0.7854F));

        ModelPartData Body_r13 = wirex2.addChild("Body_r13", ModelPartBuilder.create().uv(4, 15).mirrored().cuboid(-1.0F, -0.5F, -0.5F, 2.0F, 1.0F, 1.0F, new Dilation(-0.1F)).mirrored(false), ModelTransform.of(-0.634F, -0.634F, -2.4918F, 0.0F, -0.5672F, 0.7854F));

        ModelPartData wirex3 = bodyreal.addChild("wirex3", ModelPartBuilder.create(), ModelTransform.pivot(4.5036F, 11.2208F, 0.6127F));

        ModelPartData Body_r14 = wirex3.addChild("Body_r14", ModelPartBuilder.create().uv(4, 15).cuboid(1.0F, -0.5F, -0.5F, 2.0F, 1.0F, 1.0F, new Dilation(-0.1F)), ModelTransform.of(0.0F, 0.0F, 0.0F, -3.1416F, -0.9599F, -2.3562F));

        ModelPartData Body_r15 = wirex3.addChild("Body_r15", ModelPartBuilder.create().uv(4, 15).mirrored().cuboid(-1.0F, -0.5F, -0.5F, 2.0F, 1.0F, 1.0F, new Dilation(-0.1F)).mirrored(false), ModelTransform.of(-0.2626F, -0.2626F, 0.148F, -3.1416F, -1.2217F, -2.3562F));

        ModelPartData Body_r16 = wirex3.addChild("Body_r16", ModelPartBuilder.create().uv(4, 15).cuboid(1.0F, -0.5F, -0.5F, 2.0F, 1.0F, 1.0F, new Dilation(-0.1F)), ModelTransform.of(-0.3272F, -0.3272F, -3.3839F, 0.0F, -1.4399F, 0.7854F));

        ModelPartData Body_r17 = wirex3.addChild("Body_r17", ModelPartBuilder.create().uv(4, 15).cuboid(0.0F, -0.7F, -0.3F, 2.0F, 1.0F, 1.0F, new Dilation(-0.1F)), ModelTransform.of(-2.5036F, -2.2208F, -3.1127F, 0.0F, 0.0F, 0.7854F));

        ModelPartData Body_r18 = wirex3.addChild("Body_r18", ModelPartBuilder.create().uv(4, 15).mirrored().cuboid(-1.0F, -0.5F, -0.5F, 2.0F, 1.0F, 1.0F, new Dilation(-0.1F)).mirrored(false), ModelTransform.of(-0.634F, -0.634F, -2.4918F, 0.0F, -0.5672F, 0.7854F));

        ModelPartData wirex4 = bodyreal.addChild("wirex4", ModelPartBuilder.create(), ModelTransform.pivot(-4.5036F, 9.2208F, 0.6127F));

        ModelPartData Body_r19 = wirex4.addChild("Body_r19", ModelPartBuilder.create().uv(4, 15).cuboid(-1.0F, -0.5F, -0.5F, 2.0F, 1.0F, 1.0F, new Dilation(-0.1F)), ModelTransform.of(0.634F, -0.634F, -2.4918F, 0.0F, 0.5672F, -0.7854F));

        ModelPartData Body_r20 = wirex4.addChild("Body_r20", ModelPartBuilder.create().uv(4, 15).cuboid(-1.0F, -0.5F, -0.5F, 2.0F, 1.0F, 1.0F, new Dilation(-0.1F)), ModelTransform.of(0.2626F, -0.2626F, 0.148F, -3.1416F, 1.2217F, 2.3562F));

        ModelPartData Body_r21 = wirex4.addChild("Body_r21", ModelPartBuilder.create().uv(4, 15).mirrored().cuboid(-3.0F, -0.5F, -0.5F, 2.0F, 1.0F, 1.0F, new Dilation(-0.1F)).mirrored(false), ModelTransform.of(0.0F, 0.0F, 0.0F, -3.1416F, 0.9599F, 2.3562F));

        ModelPartData Body_r22 = wirex4.addChild("Body_r22", ModelPartBuilder.create().uv(4, 15).mirrored().cuboid(-3.0F, -0.5F, -0.5F, 2.0F, 1.0F, 1.0F, new Dilation(-0.1F)).mirrored(false), ModelTransform.of(0.3272F, -0.3272F, -3.3839F, 0.0F, 1.4399F, -0.7854F));

        ModelPartData Body_r23 = wirex4.addChild("Body_r23", ModelPartBuilder.create().uv(4, 15).mirrored().cuboid(-2.0F, -0.7F, -0.3F, 2.0F, 1.0F, 1.0F, new Dilation(-0.1F)).mirrored(false), ModelTransform.of(2.5036F, -2.2208F, -3.1127F, 0.0F, 0.0F, -0.7854F));

        ModelPartData wirex5 = bodyreal.addChild("wirex5", ModelPartBuilder.create(), ModelTransform.pivot(-4.5036F, 7.2208F, 0.6127F));

        ModelPartData Body_r24 = wirex5.addChild("Body_r24", ModelPartBuilder.create().uv(4, 15).cuboid(-1.0F, -0.5F, -0.5F, 2.0F, 1.0F, 1.0F, new Dilation(-0.1F)), ModelTransform.of(0.634F, -0.634F, -2.4918F, 0.0F, 0.5672F, -0.7854F));

        ModelPartData Body_r25 = wirex5.addChild("Body_r25", ModelPartBuilder.create().uv(4, 15).cuboid(-1.0F, -0.5F, -0.5F, 2.0F, 1.0F, 1.0F, new Dilation(-0.1F)), ModelTransform.of(0.2626F, -0.2626F, 0.148F, -3.1416F, 1.2217F, 2.3562F));

        ModelPartData Body_r26 = wirex5.addChild("Body_r26", ModelPartBuilder.create().uv(4, 15).mirrored().cuboid(-3.0F, -0.5F, -0.5F, 2.0F, 1.0F, 1.0F, new Dilation(-0.1F)).mirrored(false), ModelTransform.of(0.0F, 0.0F, 0.0F, -3.1416F, 0.9599F, 2.3562F));

        ModelPartData Body_r27 = wirex5.addChild("Body_r27", ModelPartBuilder.create().uv(4, 15).mirrored().cuboid(-3.0F, -0.5F, -0.5F, 2.0F, 1.0F, 1.0F, new Dilation(-0.1F)).mirrored(false), ModelTransform.of(0.3272F, -0.3272F, -3.3839F, 0.0F, 1.4399F, -0.7854F));

        ModelPartData Body_r28 = wirex5.addChild("Body_r28", ModelPartBuilder.create().uv(4, 15).mirrored().cuboid(-2.0F, -0.7F, -0.3F, 2.0F, 1.0F, 1.0F, new Dilation(-0.1F)).mirrored(false), ModelTransform.of(2.5036F, -2.2208F, -3.1127F, 0.0F, 0.0F, -0.7854F));

        ModelPartData wirex6 = bodyreal.addChild("wirex6", ModelPartBuilder.create(), ModelTransform.pivot(-4.5036F, 11.2208F, 0.6127F));

        ModelPartData Body_r29 = wirex6.addChild("Body_r29", ModelPartBuilder.create().uv(4, 15).cuboid(-1.0F, -0.5F, -0.5F, 2.0F, 1.0F, 1.0F, new Dilation(-0.1F)), ModelTransform.of(0.634F, -0.634F, -2.4918F, 0.0F, 0.5672F, -0.7854F));

        ModelPartData Body_r30 = wirex6.addChild("Body_r30", ModelPartBuilder.create().uv(4, 15).cuboid(-1.0F, -0.5F, -0.5F, 2.0F, 1.0F, 1.0F, new Dilation(-0.1F)), ModelTransform.of(0.2626F, -0.2626F, 0.148F, -3.1416F, 1.2217F, 2.3562F));

        ModelPartData Body_r31 = wirex6.addChild("Body_r31", ModelPartBuilder.create().uv(4, 15).mirrored().cuboid(-3.0F, -0.5F, -0.5F, 2.0F, 1.0F, 1.0F, new Dilation(-0.1F)).mirrored(false), ModelTransform.of(0.0F, 0.0F, 0.0F, -3.1416F, 0.9599F, 2.3562F));

        ModelPartData Body_r32 = wirex6.addChild("Body_r32", ModelPartBuilder.create().uv(4, 15).mirrored().cuboid(-3.0F, -0.5F, -0.5F, 2.0F, 1.0F, 1.0F, new Dilation(-0.1F)).mirrored(false), ModelTransform.of(0.3272F, -0.3272F, -3.3839F, 0.0F, 1.4399F, -0.7854F));

        ModelPartData Body_r33 = wirex6.addChild("Body_r33", ModelPartBuilder.create().uv(4, 15).mirrored().cuboid(-2.0F, -0.7F, -0.3F, 2.0F, 1.0F, 1.0F, new Dilation(-0.1F)).mirrored(false), ModelTransform.of(2.5036F, -2.2208F, -3.1127F, 0.0F, 0.0F, -0.7854F));

        ModelPartData Head = bone.addChild("Head", ModelPartBuilder.create().uv(0, 63).cuboid(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new Dilation(0.0F))
                .uv(0, 17).cuboid(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new Dilation(0.2F)), ModelTransform.pivot(0.0F, -24.0F, 0.0F));
        return TexturedModelData.of(modelData, 128, 128);
    }
    @Override
    public void setAngles(LivingEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
    }
    @Override
    public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
        bone.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
    }
}