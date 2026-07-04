package dev.amble.ait.client.models.machines;

import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.entity.Entity;

public class UntemperedSchismModel extends SinglePartEntityModel {
    private final ModelPart Schism;
    private final ModelPart schismRing;
    private final ModelPart ring;
    private final ModelPart OuterRing;
    private final ModelPart Outer_Ring2;
    private final ModelPart InnerRing;
    private final ModelPart RingFrame;
    private final ModelPart SchismBase;
    private final ModelPart bone9;
    private final ModelPart bone10;
    public UntemperedSchismModel(ModelPart root) {
        this.Schism = root.getChild("Schism");
        this.schismRing = this.Schism.getChild("schism Ring");
        this.ring = this.schismRing.getChild("ring");
        this.OuterRing = this.ring.getChild("Outer Ring");
        this.Outer_Ring2 = this.ring.getChild("Outer_Ring2");
        this.InnerRing = this.ring.getChild("Inner Ring");
        this.RingFrame = this.schismRing.getChild("RingFrame");
        this.SchismBase = this.Schism.getChild("Schism Base");
        this.bone9 = this.SchismBase.getChild("bone9");
        this.bone10 = this.SchismBase.getChild("bone10");
    }
    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData Schism = modelPartData.addChild("Schism", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 21.0F, 0.0F));

        ModelPartData schismRing = Schism.addChild("schism Ring", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData ring = schismRing.addChild("ring", ModelPartBuilder.create(), ModelTransform.of(0.0F, -44.511F, 0.0F, 0.0F, 0.0F, 0.3927F));

        ModelPartData OuterRing = ring.addChild("Outer Ring", ModelPartBuilder.create().uv(0, 21).cuboid(-18.0F, -43.489F, -8.0F, 36.0F, 8.0F, 16.0F, new Dilation(0.01F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData cube_r1 = OuterRing.addChild("cube_r1", ModelPartBuilder.create().uv(0, 21).cuboid(-18.0F, -16.0F, -8.0F, 36.0F, 8.0F, 16.0F, new Dilation(0.0F)), ModelTransform.of(19.4142F, -19.4473F, 0.0F, 0.0F, 0.0F, 0.7854F));

        ModelPartData cube_r2 = OuterRing.addChild("cube_r2", ModelPartBuilder.create().uv(0, 21).cuboid(-18.0F, -16.0F, -8.0F, 36.0F, 8.0F, 16.0F, new Dilation(0.01F)), ModelTransform.of(27.4558F, -0.0331F, 0.0F, 0.0F, 0.0F, 1.5708F));

        ModelPartData cube_r3 = OuterRing.addChild("cube_r3", ModelPartBuilder.create().uv(0, 21).cuboid(-18.0F, -4.0F, -8.0F, 36.0F, 8.0F, 16.0F, new Dilation(0.01F)), ModelTransform.of(27.8995F, 27.9547F, 0.0F, 0.0F, 0.0F, 2.3562F));

        ModelPartData cube_r4 = OuterRing.addChild("cube_r4", ModelPartBuilder.create().uv(0, 21).cuboid(-18.0F, -4.0F, -8.0F, 36.0F, 8.0F, 16.0F, new Dilation(0.01F)), ModelTransform.of(0.0F, 39.511F, 0.0F, 0.0F, 0.0F, -3.1416F));

        ModelPartData cube_r5 = OuterRing.addChild("cube_r5", ModelPartBuilder.create().uv(0, 21).cuboid(-18.0F, -4.0F, -8.0F, 36.0F, 8.0F, 16.0F, new Dilation(0.0F)), ModelTransform.of(-27.8995F, 27.9547F, 0.0F, 0.0F, 0.0F, -2.3562F));

        ModelPartData cube_r6 = OuterRing.addChild("cube_r6", ModelPartBuilder.create().uv(0, 21).cuboid(-18.0F, -16.0F, -8.0F, 36.0F, 8.0F, 16.0F, new Dilation(0.01F)), ModelTransform.of(-27.4558F, -0.0331F, 0.0F, 0.0F, 0.0F, -1.5708F));

        ModelPartData cube_r7 = OuterRing.addChild("cube_r7", ModelPartBuilder.create().uv(0, 21).cuboid(-18.0F, -16.0F, -8.0F, 36.0F, 8.0F, 16.0F, new Dilation(0.0F)), ModelTransform.of(-19.4142F, -19.4473F, 0.0F, 0.0F, 0.0F, -0.7854F));

        ModelPartData Outer_Ring2 = ring.addChild("Outer_Ring2", ModelPartBuilder.create(), ModelTransform.pivot(1.1342F, -0.6264F, 0.0F));

        ModelPartData cube_r8 = Outer_Ring2.addChild("cube_r8", ModelPartBuilder.create().uv(93, 83).cuboid(-20.0F, -19.0F, -4.0F, 38.0F, 7.0F, 8.0F, new Dilation(0.01F)), ModelTransform.of(19.7312F, -18.3502F, -0.01F, 0.0F, 0.0F, 0.7854F));

        ModelPartData cube_r9 = Outer_Ring2.addChild("cube_r9", ModelPartBuilder.create().uv(0, 45).cuboid(-21.0F, -19.0F, -4.0F, 39.0F, 7.0F, 8.0F, new Dilation(0.0F)), ModelTransform.of(26.8842F, 1.9527F, 0.0F, 0.0F, 0.0F, 1.5708F));

        ModelPartData cube_r10 = Outer_Ring2.addChild("cube_r10", ModelPartBuilder.create().uv(0, 45).cuboid(-21.0F, -7.0F, -4.0F, 39.0F, 7.0F, 8.0F, new Dilation(0.01F)), ModelTransform.of(26.0811F, 29.8622F, 0.0F, 0.0F, 0.0F, 2.3562F));

        ModelPartData cube_r11 = Outer_Ring2.addChild("cube_r11", ModelPartBuilder.create().uv(0, 45).mirrored().cuboid(-21.0F, -7.0F, -4.0F, 39.0F, 7.0F, 8.0F, new Dilation(0.01F)).mirrored(false), ModelTransform.of(-2.7071F, 40.544F, 0.0F, 0.0F, 0.0F, -3.1416F));

        ModelPartData cube_r12 = Outer_Ring2.addChild("cube_r12", ModelPartBuilder.create().uv(0, 45).cuboid(-21.0F, -7.0F, -4.0F, 39.0F, 7.0F, 8.0F, new Dilation(0.0F)), ModelTransform.of(-30.6166F, 27.7551F, -0.01F, 0.0F, 0.0F, -2.3562F));

        ModelPartData cube_r13 = Outer_Ring2.addChild("cube_r13", ModelPartBuilder.create().uv(92, 83).mirrored().cuboid(-21.0F, -19.0F, -4.0F, 38.0F, 7.0F, 8.0F, new Dilation(0.01F)).mirrored(false), ModelTransform.of(-29.2843F, -1.0331F, 0.0F, 0.0F, 0.0F, -1.5708F));

        ModelPartData InnerRing = ring.addChild("Inner Ring", ModelPartBuilder.create(), ModelTransform.pivot(25.5069F, 25.562F, -0.9558F));

        ModelPartData cube_r14 = InnerRing.addChild("cube_r14", ModelPartBuilder.create().uv(0, 64).mirrored().cuboid(-18.0F, -5.0F, -5.0F, 36.0F, 10.0F, 10.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.8727F, 0.0F, 2.3562F));

        ModelPartData cube_r15 = InnerRing.addChild("cube_r15", ModelPartBuilder.create().uv(0, 64).mirrored().cuboid(-18.0F, -5.0F, -4.5F, 36.0F, 10.0F, 10.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(-25.5069F, 10.1823F, 2.2331F, -2.2689F, 0.0F, 0.0F));

        ModelPartData cube_r16 = InnerRing.addChild("cube_r16", ModelPartBuilder.create().uv(0, 64).cuboid(-18.0F, -5.0F, -5.0F, 36.0F, 10.0F, 10.0F, new Dilation(0.0F)), ModelTransform.of(-51.0137F, 0.0F, 1.9117F, -2.2689F, 0.0F, 0.7854F));

        ModelPartData cube_r17 = InnerRing.addChild("cube_r17", ModelPartBuilder.create().uv(0, 64).cuboid(-18.0F, -5.0F, -5.0F, 36.0F, 10.0F, 10.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 1.9117F, -2.2689F, 0.0F, -0.7854F));

        ModelPartData cube_r18 = InnerRing.addChild("cube_r18", ModelPartBuilder.create().uv(0, 64).cuboid(-18.0F, -5.0F, -4.5F, 36.0F, 10.0F, 10.0F, new Dilation(0.0F)), ModelTransform.of(-25.5069F, 10.1823F, -0.3214F, 0.8727F, 0.0F, 3.1416F));

        ModelPartData cube_r19 = InnerRing.addChild("cube_r19", ModelPartBuilder.create().uv(0, 64).mirrored().cuboid(-18.0F, -5.0F, -5.0F, 36.0F, 10.0F, 10.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(-51.0137F, 0.0F, 0.0F, 0.8727F, 0.0F, -2.3562F));

        ModelPartData cube_r20 = InnerRing.addChild("cube_r20", ModelPartBuilder.create().uv(0, 64).mirrored().cuboid(-18.0F, -5.0F, -5.5F, 36.0F, 10.0F, 10.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(-25.5069F, -61.2843F, 2.304F, -0.8727F, 0.0F, 0.0F));

        ModelPartData cube_r21 = InnerRing.addChild("cube_r21", ModelPartBuilder.create().uv(0, 64).cuboid(-18.0F, -1.0F, -8.0F, 36.0F, 10.0F, 10.0F, new Dilation(0.0F)), ModelTransform.of(-25.5069F, -66.5366F, -1.1358F, 0.8727F, 0.0F, 0.0F));

        ModelPartData cube_r22 = InnerRing.addChild("cube_r22", ModelPartBuilder.create().uv(0, 64).mirrored().cuboid(-18.0F, 1.0F, -8.0F, 36.0F, 10.0F, 10.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(4.3521F, -55.4541F, -2.6679F, 0.8727F, 0.0F, 0.7854F));

        ModelPartData cube_r23 = InnerRing.addChild("cube_r23", ModelPartBuilder.create().uv(0, 64).cuboid(-18.0F, 1.0F, -2.0F, 36.0F, 10.0F, 10.0F, new Dilation(0.0F)), ModelTransform.of(4.3521F, -55.4541F, 4.5796F, -0.8727F, 0.0F, 0.7854F));

        ModelPartData cube_r24 = InnerRing.addChild("cube_r24", ModelPartBuilder.create().uv(0, 64).cuboid(-18.0F, 1.0F, -2.0F, 36.0F, 10.0F, 10.0F, new Dilation(0.0F)), ModelTransform.of(-55.3658F, -55.4541F, 4.5796F, -0.8727F, 0.0F, -0.7854F));

        ModelPartData cube_r25 = InnerRing.addChild("cube_r25", ModelPartBuilder.create().uv(0, 64).mirrored().cuboid(-18.0F, 1.0F, -8.0F, 36.0F, 10.0F, 10.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(-55.3658F, -55.4541F, -2.6679F, 0.8727F, 0.0F, -0.7854F));

        ModelPartData cube_r26 = InnerRing.addChild("cube_r26", ModelPartBuilder.create().uv(0, 64).mirrored().cuboid(-18.0F, 0.0F, -2.0F, 36.0F, 10.0F, 10.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(-67.091F, -25.5952F, 3.8135F, -0.8727F, 0.0F, -1.5708F));

        ModelPartData cube_r27 = InnerRing.addChild("cube_r27", ModelPartBuilder.create().uv(0, 64).cuboid(-18.0F, 0.0F, -8.0F, 36.0F, 10.0F, 10.0F, new Dilation(0.0F)), ModelTransform.of(-67.091F, -25.5952F, -1.9019F, 0.8727F, 0.0F, -1.5708F));

        ModelPartData cube_r28 = InnerRing.addChild("cube_r28", ModelPartBuilder.create().uv(0, 64).mirrored().cuboid(-18.0F, 0.0F, -2.0F, 36.0F, 10.0F, 10.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(16.0774F, -25.5952F, 3.8135F, -0.8727F, 0.0F, 1.5708F));

        ModelPartData cube_r29 = InnerRing.addChild("cube_r29", ModelPartBuilder.create().uv(0, 64).cuboid(-18.0F, 0.0F, -8.0F, 36.0F, 10.0F, 10.0F, new Dilation(0.0F)), ModelTransform.of(16.0774F, -25.5952F, -1.9019F, 0.8727F, 0.0F, 1.5708F));

        ModelPartData RingFrame = schismRing.addChild("RingFrame", ModelPartBuilder.create().uv(104, 21).cuboid(11.4142F, 57.9584F, -9.0F, 16.0F, 6.0F, 18.0F, new Dilation(0.0F))
        .uv(104, 21).cuboid(11.4142F, -26.0416F, -9.0F, 16.0F, 6.0F, 18.0F, new Dilation(0.0F)), ModelTransform.pivot(-19.4142F, -63.3584F, 0.0F));

        ModelPartData cube_r30 = RingFrame.addChild("cube_r30", ModelPartBuilder.create().uv(104, 21).mirrored().cuboid(-8.0F, -18.0F, -9.0F, 16.0F, 6.0F, 18.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(38.8284F, 0.0F, 0.0F, 0.0F, 0.0F, 0.7854F));

        ModelPartData cube_r31 = RingFrame.addChild("cube_r31", ModelPartBuilder.create().uv(104, 21).mirrored().cuboid(-8.0F, -18.0F, -9.0F, 16.0F, 6.0F, 18.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(46.87F, 19.4142F, 0.0F, 0.0F, 0.0F, 1.5708F));

        ModelPartData cube_r32 = RingFrame.addChild("cube_r32", ModelPartBuilder.create().uv(104, 21).mirrored().cuboid(-8.0F, 11.0F, -9.0F, 16.0F, 6.0F, 18.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(38.8284F, 38.9167F, 0.0F, 0.0F, 0.0F, -0.7854F));

        ModelPartData cube_r33 = RingFrame.addChild("cube_r33", ModelPartBuilder.create().uv(104, 21).cuboid(-8.0F, 11.0F, -9.0F, 16.0F, 6.0F, 18.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 38.9167F, 0.0F, 0.0F, 0.0F, 0.7854F));

        ModelPartData cube_r34 = RingFrame.addChild("cube_r34", ModelPartBuilder.create().uv(104, 21).cuboid(-8.0F, -18.0F, -9.0F, 16.0F, 6.0F, 18.0F, new Dilation(0.0F)), ModelTransform.of(-8.0416F, 19.4142F, 0.0F, 0.0F, 0.0F, -1.5708F));

        ModelPartData cube_r35 = RingFrame.addChild("cube_r35", ModelPartBuilder.create().uv(104, 21).cuboid(-8.0F, -18.0F, -9.0F, 16.0F, 6.0F, 18.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.7854F));

        ModelPartData SchismBase = Schism.addChild("Schism Base", ModelPartBuilder.create().uv(0, 0).cuboid(-4.0F, -9.0F, 15.0F, 76.0F, 9.0F, 12.0F, new Dilation(0.003F)), ModelTransform.pivot(-34.0F, 3.0F, -13.0F));

        ModelPartData bone9 = SchismBase.addChild("bone9", ModelPartBuilder.create().uv(0, 104).cuboid(-4.0F, -9.0F, -5.0F, 12.0F, 9.0F, 20.0F, new Dilation(0.002F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData cube_r36 = bone9.addChild("cube_r36", ModelPartBuilder.create().uv(92, 98).cuboid(-7.0F, -4.5F, -12.0F, 13.0F, 9.0F, 24.0F, new Dilation(0.0F)), ModelTransform.of(9.3873F, -4.5F, 8.3445F, 0.0F, 0.5672F, 0.0F));

        ModelPartData bone10 = SchismBase.addChild("bone10", ModelPartBuilder.create().uv(0, 104).mirrored().cuboid(-8.0F, -9.0F, -5.0F, 12.0F, 9.0F, 20.0F, new Dilation(0.002F)).mirrored(false), ModelTransform.pivot(68.0F, 0.0F, 0.0F));

        ModelPartData cube_r37 = bone10.addChild("cube_r37", ModelPartBuilder.create().uv(92, 98).mirrored().cuboid(-6.0F, -4.5F, -12.0F, 13.0F, 9.0F, 24.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(-9.3873F, -4.5F, 8.3445F, 0.0F, -0.5672F, 0.0F));
        return TexturedModelData.of(modelData, 256, 256);
    }

    @Override
    public void setAngles(Entity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
    }

    @Override
    public ModelPart getPart() {
        return Schism;
    }
}