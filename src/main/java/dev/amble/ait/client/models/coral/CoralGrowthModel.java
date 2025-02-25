package dev.amble.ait.client.models.coral;

import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;

// not to be confused with the exterior, this is only for the coral while its growing
@SuppressWarnings("rawtypes")
public class CoralGrowthModel extends SinglePartEntityModel {
    public final ModelPart coral;
    public final ModelPart one;
    public final ModelPart two;
    public final ModelPart three;
    public final ModelPart four;
    public final ModelPart five;
    public final ModelPart six;
    public final ModelPart seven;

    public CoralGrowthModel(ModelPart root) {
        this.coral = root.getChild("coral");
        this.one = this.coral.getChild("one");
        this.two = this.coral.getChild("two");
        this.three = this.coral.getChild("three");
        this.four = this.coral.getChild("four");
        this.five = this.coral.getChild("five");
        this.six = this.coral.getChild("six");
        this.seven = this.coral.getChild("seven");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData coral = modelPartData.addChild("coral", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 24.0F, 0.0F));

        ModelPartData one = coral.addChild("one", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData cube_r1 = one.addChild("cube_r1", ModelPartBuilder.create().uv(130, 137).cuboid(-8.0F, -4.0F, -12.0F, 16.0F, 0.0F, 16.0F, new Dilation(0.001F)), ModelTransform.of(2.0F, 2.5F, 0.0F, 0.0F, 1.5708F, 0.3491F));

        ModelPartData cube_r2 = one.addChild("cube_r2", ModelPartBuilder.create().uv(195, 137).cuboid(-8.0F, -4.0F, -12.0F, 16.0F, 0.0F, 16.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 2.5F, -2.0F, 2.7925F, 0.0F, -3.1416F));

        ModelPartData cube_r3 = one.addChild("cube_r3", ModelPartBuilder.create().uv(260, 137).cuboid(-8.0F, -4.0F, -12.0F, 16.0F, 0.0F, 16.0F, new Dilation(0.001F)), ModelTransform.of(-2.0F, 2.5F, 0.0F, 0.0F, -1.5708F, -0.3491F));

        ModelPartData cube_r4 = one.addChild("cube_r4", ModelPartBuilder.create().uv(325, 137).cuboid(-8.0F, -4.0F, -12.0F, 16.0F, 0.0F, 16.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 2.5F, 2.0F, -0.3491F, 0.0F, 0.0F));

        ModelPartData cube_r5 = one.addChild("cube_r5", ModelPartBuilder.create().uv(245, 43).cuboid(-12.0F, -42.0F, 0.0F, 24.0F, 42.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -0.7854F, 0.0F));

        ModelPartData cube_r6 = one.addChild("cube_r6", ModelPartBuilder.create().uv(294, 43).cuboid(-12.0F, -42.0F, 0.0F, 24.0F, 42.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.7854F, 0.0F));

        ModelPartData two = coral.addChild("two", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData cube_r7 = two.addChild("cube_r7", ModelPartBuilder.create().uv(390, 137).cuboid(-8.0F, -4.0F, -12.0F, 16.0F, 0.0F, 16.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.5708F, 0.7854F));

        ModelPartData cube_r8 = two.addChild("cube_r8", ModelPartBuilder.create().uv(455, 137).cuboid(-8.0F, -4.0F, -12.0F, 16.0F, 0.0F, 16.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 2.3562F, 0.0F, -3.1416F));

        ModelPartData cube_r9 = two.addChild("cube_r9", ModelPartBuilder.create().uv(520, 137).cuboid(-8.0F, -4.0F, -12.0F, 16.0F, 0.0F, 16.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.5708F, -0.7854F));

        ModelPartData cube_r10 = two.addChild("cube_r10", ModelPartBuilder.create().uv(0, 154).cuboid(-8.0F, -4.0F, -12.0F, 16.0F, 0.0F, 16.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 0.0F, 0.0F, -0.7854F, 0.0F, 0.0F));

        ModelPartData cube_r11 = two.addChild("cube_r11", ModelPartBuilder.create().uv(147, 43).cuboid(-12.0F, -42.0F, 0.0F, 24.0F, 42.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -0.7854F, 0.0F));

        ModelPartData cube_r12 = two.addChild("cube_r12", ModelPartBuilder.create().uv(196, 43).cuboid(-12.0F, -42.0F, 0.0F, 24.0F, 42.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.7854F, 0.0F));

        ModelPartData three = coral.addChild("three", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData cube_r13 = three.addChild("cube_r13", ModelPartBuilder.create().uv(390, 273).cuboid(-8.0F, -4.0F, -12.0F, 16.0F, 0.0F, 16.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.5708F, -0.7854F));

        ModelPartData cube_r14 = three.addChild("cube_r14", ModelPartBuilder.create().uv(455, 273).cuboid(-8.0F, -4.0F, -12.0F, 16.0F, 0.0F, 16.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 2.3562F, 0.0F, -3.1416F));

        ModelPartData cube_r15 = three.addChild("cube_r15", ModelPartBuilder.create().uv(520, 273).cuboid(-8.0F, -4.0F, -12.0F, 16.0F, 0.0F, 16.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.5708F, 0.7854F));

        ModelPartData cube_r16 = three.addChild("cube_r16", ModelPartBuilder.create().uv(0, 290).cuboid(-8.0F, -4.0F, -12.0F, 16.0F, 0.0F, 16.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 0.0F, 0.0F, -0.7854F, 0.0F, 0.0F));

        ModelPartData cube_r17 = three.addChild("cube_r17", ModelPartBuilder.create().uv(49, 43).cuboid(-12.0F, -42.0F, 0.0F, 24.0F, 42.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -0.7854F, 0.0F));

        ModelPartData cube_r18 = three.addChild("cube_r18", ModelPartBuilder.create().uv(98, 43).cuboid(-12.0F, -42.0F, 0.0F, 24.0F, 42.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.7854F, 0.0F));

        ModelPartData four = coral.addChild("four", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData cube_r19 = four.addChild("cube_r19", ModelPartBuilder.create().uv(455, 120).cuboid(-8.0F, -4.0F, -12.0F, 16.0F, 0.0F, 16.0F, new Dilation(0.001F)), ModelTransform.of(4.0F, 2.5F, 0.0F, 0.0F, 1.5708F, 0.3491F));

        ModelPartData cube_r20 = four.addChild("cube_r20", ModelPartBuilder.create().uv(520, 120).cuboid(-8.0F, -4.0F, -12.0F, 16.0F, 0.0F, 16.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 2.5F, -4.0F, 2.7925F, 0.0F, -3.1416F));

        ModelPartData cube_r21 = four.addChild("cube_r21", ModelPartBuilder.create().uv(0, 137).cuboid(-8.0F, -4.0F, -12.0F, 16.0F, 0.0F, 16.0F, new Dilation(0.001F)), ModelTransform.of(-4.0F, 2.5F, 0.0F, 0.0F, -1.5708F, -0.3491F));

        ModelPartData cube_r22 = four.addChild("cube_r22", ModelPartBuilder.create().uv(65, 137).cuboid(-8.0F, -4.0F, -12.0F, 16.0F, 0.0F, 16.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 2.5F, 4.0F, -0.3491F, 0.0F, 0.0F));

        ModelPartData cube_r23 = four.addChild("cube_r23", ModelPartBuilder.create().uv(50, 386).cuboid(-6.0F, -36.0F, -6.0F, 12.0F, 36.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 0.0F, 1.0F, -0.0873F, 0.0F, 0.0F));

        ModelPartData cube_r24 = four.addChild("cube_r24", ModelPartBuilder.create().uv(75, 386).cuboid(-6.0F, -36.0F, -6.0F, 12.0F, 36.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(1.0F, 0.0F, 0.0F, 0.0F, 1.5708F, 0.0873F));

        ModelPartData cube_r25 = four.addChild("cube_r25", ModelPartBuilder.create().uv(100, 386).cuboid(-6.0F, -36.0F, -6.0F, 12.0F, 36.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 0.0F, -1.0F, 3.0543F, 0.0F, -3.1416F));

        ModelPartData cube_r26 = four.addChild("cube_r26", ModelPartBuilder.create().uv(125, 386).cuboid(-6.0F, -36.0F, -6.0F, 12.0F, 36.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(-1.0F, 0.0F, 0.0F, 0.0F, -1.5708F, -0.0873F));

        ModelPartData cube_r27 = four.addChild("cube_r27", ModelPartBuilder.create().uv(0, 222).cuboid(-8.0F, -4.0F, -12.0F, 16.0F, 0.0F, 16.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, -19.0F, 2.0F, -0.7854F, 0.0F, 0.0F));

        ModelPartData cube_r28 = four.addChild("cube_r28", ModelPartBuilder.create().uv(520, 205).cuboid(-8.0F, -4.0F, -12.0F, 16.0F, 0.0F, 16.0F, new Dilation(0.001F)), ModelTransform.of(2.0F, -19.0F, 0.0F, 0.0F, 1.5708F, 0.7854F));

        ModelPartData cube_r29 = four.addChild("cube_r29", ModelPartBuilder.create().uv(455, 205).cuboid(-8.0F, -4.0F, -12.0F, 16.0F, 0.0F, 16.0F, new Dilation(0.001F)), ModelTransform.of(-2.0F, -19.0F, 0.0F, 0.0F, -1.5708F, -0.7854F));

        ModelPartData cube_r30 = four.addChild("cube_r30", ModelPartBuilder.create().uv(390, 205).cuboid(-8.0F, -4.0F, -12.0F, 16.0F, 0.0F, 16.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, -19.0F, -2.0F, 2.3562F, 0.0F, -3.1416F));

        ModelPartData cube_r31 = four.addChild("cube_r31", ModelPartBuilder.create().uv(260, 222).cuboid(-8.0F, -4.0F, -12.0F, 16.0F, 0.0F, 16.0F, new Dilation(0.001F)), ModelTransform.of(2.0F, -8.0F, 0.0F, 0.0F, 1.5708F, 0.7854F));

        ModelPartData cube_r32 = four.addChild("cube_r32", ModelPartBuilder.create().uv(195, 222).cuboid(-8.0F, -4.0F, -12.0F, 16.0F, 0.0F, 16.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, -8.0F, 2.0F, -0.7854F, 0.0F, 0.0F));

        ModelPartData cube_r33 = four.addChild("cube_r33", ModelPartBuilder.create().uv(130, 222).cuboid(-8.0F, -4.0F, -12.0F, 16.0F, 0.0F, 16.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, -8.0F, -2.0F, 2.3562F, 0.0F, -3.1416F));

        ModelPartData cube_r34 = four.addChild("cube_r34", ModelPartBuilder.create().uv(65, 222).cuboid(-8.0F, -4.0F, -12.0F, 16.0F, 0.0F, 16.0F, new Dilation(0.001F)), ModelTransform.of(-2.0F, -8.0F, 0.0F, 0.0F, -1.5708F, -0.7854F));

        ModelPartData cube_r35 = four.addChild("cube_r35", ModelPartBuilder.create().uv(130, 273).cuboid(-8.0F, -4.0F, -12.0F, 16.0F, 0.0F, 16.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 0.0F, 1.0F, -0.7854F, 0.0F, 0.0F));

        ModelPartData cube_r36 = four.addChild("cube_r36", ModelPartBuilder.create().uv(195, 273).cuboid(-8.0F, -4.0F, -12.0F, 16.0F, 0.0F, 16.0F, new Dilation(0.001F)), ModelTransform.of(1.0F, 0.0F, 0.0F, 0.0F, 1.5708F, 0.7854F));

        ModelPartData cube_r37 = four.addChild("cube_r37", ModelPartBuilder.create().uv(260, 273).cuboid(-8.0F, -4.0F, -12.0F, 16.0F, 0.0F, 16.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 0.0F, -1.0F, 2.3562F, 0.0F, -3.1416F));

        ModelPartData cube_r38 = four.addChild("cube_r38", ModelPartBuilder.create().uv(325, 273).cuboid(-8.0F, -4.0F, -12.0F, 16.0F, 0.0F, 16.0F, new Dilation(0.001F)), ModelTransform.of(-1.0F, 0.0F, 0.0F, 0.0F, -1.5708F, -0.7854F));

        ModelPartData cube_r39 = four.addChild("cube_r39", ModelPartBuilder.create().uv(390, 0).cuboid(-12.0F, -42.0F, 0.0F, 24.0F, 42.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -0.7854F, 0.0F));

        ModelPartData cube_r40 = four.addChild("cube_r40", ModelPartBuilder.create().uv(0, 43).cuboid(-12.0F, -42.0F, 0.0F, 24.0F, 42.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.7854F, 0.0F));

        ModelPartData five = coral.addChild("five", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData cube_r41 = five.addChild("cube_r41", ModelPartBuilder.create().uv(343, 43).cuboid(-8.0F, -4.0F, -12.0F, 16.0F, 0.0F, 16.0F, new Dilation(0.001F)), ModelTransform.of(8.0F, 3.0F, 0.0F, 0.0F, 1.5708F, 0.2618F));

        ModelPartData cube_r42 = five.addChild("cube_r42", ModelPartBuilder.create().uv(0, 86).cuboid(-8.0F, -4.0F, -12.0F, 16.0F, 0.0F, 16.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 3.0F, -8.0F, 2.8798F, 0.0F, -3.1416F));

        ModelPartData cube_r43 = five.addChild("cube_r43", ModelPartBuilder.create().uv(65, 86).cuboid(-8.0F, -4.0F, -12.0F, 16.0F, 0.0F, 16.0F, new Dilation(0.001F)), ModelTransform.of(-9.0F, 3.0F, 0.0F, 0.0F, -1.5708F, -0.2618F));

        ModelPartData cube_r44 = five.addChild("cube_r44", ModelPartBuilder.create().uv(130, 86).cuboid(-8.0F, -4.0F, -12.0F, 16.0F, 0.0F, 16.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 3.0F, 8.0F, -0.2618F, 0.0F, 0.0F));

        ModelPartData cube_r45 = five.addChild("cube_r45", ModelPartBuilder.create().uv(65, 205).cuboid(-8.0F, -4.0F, -12.0F, 16.0F, 0.0F, 16.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, -1.0F, -6.0F, 2.3562F, 0.0F, -3.1416F));

        ModelPartData cube_r46 = five.addChild("cube_r46", ModelPartBuilder.create().uv(0, 205).cuboid(-8.0F, -4.0F, -12.0F, 16.0F, 0.0F, 16.0F, new Dilation(0.001F)), ModelTransform.of(-6.0F, -1.0F, 0.0F, 0.0F, -1.5708F, -0.7854F));

        ModelPartData cube_r47 = five.addChild("cube_r47", ModelPartBuilder.create().uv(520, 188).cuboid(-8.0F, -4.0F, -12.0F, 16.0F, 0.0F, 16.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, -1.0F, 6.0F, -0.7854F, 0.0F, 0.0F));

        ModelPartData cube_r48 = five.addChild("cube_r48", ModelPartBuilder.create().uv(455, 188).cuboid(-8.0F, -4.0F, -12.0F, 16.0F, 0.0F, 16.0F, new Dilation(0.001F)), ModelTransform.of(6.0F, -1.0F, 0.0F, 0.0F, 1.5708F, 0.7854F));

        ModelPartData cube_r49 = five.addChild("cube_r49", ModelPartBuilder.create().uv(390, 188).cuboid(-8.0F, -4.0F, -12.0F, 16.0F, 0.0F, 16.0F, new Dilation(0.001F))
                .uv(130, 205).cuboid(-8.0F, -4.0F, -12.0F, 16.0F, 0.0F, 16.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, -26.0F, -2.0F, 2.3562F, 0.0F, -3.1416F));

        ModelPartData cube_r50 = five.addChild("cube_r50", ModelPartBuilder.create().uv(325, 188).cuboid(-8.0F, -4.0F, -12.0F, 16.0F, 0.0F, 16.0F, new Dilation(0.001F))
                .uv(325, 205).cuboid(-8.0F, -4.0F, -12.0F, 16.0F, 0.0F, 16.0F, new Dilation(0.001F)), ModelTransform.of(2.0F, -26.0F, 0.0F, 0.0F, 1.5708F, 0.7854F));

        ModelPartData cube_r51 = five.addChild("cube_r51", ModelPartBuilder.create().uv(260, 188).cuboid(-8.0F, -4.0F, -12.0F, 16.0F, 0.0F, 16.0F, new Dilation(0.001F))
                .uv(260, 205).cuboid(-8.0F, -4.0F, -12.0F, 16.0F, 0.0F, 16.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, -26.0F, 2.0F, -0.7854F, 0.0F, 0.0F));

        ModelPartData cube_r52 = five.addChild("cube_r52", ModelPartBuilder.create().uv(195, 188).cuboid(-8.0F, -4.0F, -12.0F, 16.0F, 0.0F, 16.0F, new Dilation(0.001F))
                .uv(195, 205).cuboid(-8.0F, -4.0F, -12.0F, 16.0F, 0.0F, 16.0F, new Dilation(0.001F)), ModelTransform.of(-2.0F, -26.0F, 0.0F, 0.0F, -1.5708F, -0.7854F));

        ModelPartData cube_r53 = five.addChild("cube_r53", ModelPartBuilder.create().uv(260, 256).cuboid(-8.0F, -6.9583F, -7.6559F, 16.0F, 0.0F, 16.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 3.0F, 0.0F, -0.4363F, -0.7854F, 0.0F));

        ModelPartData cube_r54 = five.addChild("cube_r54", ModelPartBuilder.create().uv(195, 256).cuboid(-8.0F, -6.9583F, -7.6559F, 16.0F, 0.0F, 16.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 3.0F, 0.0F, -0.4363F, 0.0F, 0.0F));

        ModelPartData cube_r55 = five.addChild("cube_r55", ModelPartBuilder.create().uv(455, 256).cuboid(-8.0F, -6.9583F, -7.6559F, 16.0F, 0.0F, 16.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 3.0F, 0.0F, 2.7053F, 0.7854F, 3.1416F));

        ModelPartData cube_r56 = five.addChild("cube_r56", ModelPartBuilder.create().uv(390, 256).cuboid(-8.0F, -6.9583F, -7.6559F, 16.0F, 0.0F, 16.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 3.0F, 0.0F, 0.0F, 1.5708F, 0.4363F));

        ModelPartData cube_r57 = five.addChild("cube_r57", ModelPartBuilder.create().uv(325, 256).cuboid(-8.0F, -6.9583F, -7.6559F, 16.0F, 0.0F, 16.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 3.0F, 0.0F, -0.4363F, 0.7854F, 0.0F));

        ModelPartData cube_r58 = five.addChild("cube_r58", ModelPartBuilder.create().uv(520, 256).cuboid(-8.0F, -6.9583F, -7.6559F, 16.0F, 0.0F, 16.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 3.0F, 0.0F, 2.7053F, 0.0F, 3.1416F));

        ModelPartData cube_r59 = five.addChild("cube_r59", ModelPartBuilder.create().uv(0, 273).cuboid(-8.0F, -6.9583F, -7.6559F, 16.0F, 0.0F, 16.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 3.0F, 0.0F, 2.7053F, -0.7854F, 3.1416F));

        ModelPartData cube_r60 = five.addChild("cube_r60", ModelPartBuilder.create().uv(65, 273).cuboid(-8.0F, -6.9583F, -7.6559F, 16.0F, 0.0F, 16.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 3.0F, 0.0F, 0.0F, -1.5708F, -0.4363F));

        ModelPartData cube_r61 = five.addChild("cube_r61", ModelPartBuilder.create().uv(226, 343).cuboid(-6.0F, -36.0F, -6.0F, 12.0F, 36.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(1.0F, 0.0F, 0.0F, 0.0F, -1.5708F, -0.0873F));

        ModelPartData cube_r62 = five.addChild("cube_r62", ModelPartBuilder.create().uv(130, 188).cuboid(-8.0F, -4.0F, -12.0F, 16.0F, 0.0F, 16.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, -14.0F, -2.0F, 2.3562F, 0.0F, -3.1416F));

        ModelPartData cube_r63 = five.addChild("cube_r63", ModelPartBuilder.create().uv(65, 188).cuboid(-8.0F, -4.0F, -12.0F, 16.0F, 0.0F, 16.0F, new Dilation(0.001F)), ModelTransform.of(2.0F, -14.0F, 0.0F, 0.0F, 1.5708F, 0.7854F));

        ModelPartData cube_r64 = five.addChild("cube_r64", ModelPartBuilder.create().uv(0, 188).cuboid(-8.0F, -4.0F, -12.0F, 16.0F, 0.0F, 16.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, -14.0F, 2.0F, -0.7854F, 0.0F, 0.0F));

        ModelPartData cube_r65 = five.addChild("cube_r65", ModelPartBuilder.create().uv(520, 171).cuboid(-8.0F, -4.0F, -12.0F, 16.0F, 0.0F, 16.0F, new Dilation(0.001F)), ModelTransform.of(-2.0F, -14.0F, 0.0F, 0.0F, -1.5708F, -0.7854F));

        ModelPartData cube_r66 = five.addChild("cube_r66", ModelPartBuilder.create().uv(251, 343).cuboid(-6.0F, -36.0F, -6.0F, 12.0F, 36.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(-1.0F, 0.0F, 0.0F, 0.0F, 1.5708F, 0.0873F));

        ModelPartData cube_r67 = five.addChild("cube_r67", ModelPartBuilder.create().uv(0, 386).cuboid(-6.0F, -36.0F, -6.0F, 12.0F, 36.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 0.0F, 1.0F, 3.0543F, 0.0F, -3.1416F));

        ModelPartData cube_r68 = five.addChild("cube_r68", ModelPartBuilder.create().uv(25, 386).cuboid(-6.0F, -36.0F, -6.0F, 12.0F, 36.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 0.0F, -1.0F, -0.0873F, 0.0F, 0.0F));

        ModelPartData cube_r69 = five.addChild("cube_r69", ModelPartBuilder.create().uv(292, 0).cuboid(-12.0F, -42.0F, 0.0F, 24.0F, 42.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -0.7854F, 0.0F));

        ModelPartData cube_r70 = five.addChild("cube_r70", ModelPartBuilder.create().uv(341, 0).cuboid(-12.0F, -42.0F, 0.0F, 24.0F, 42.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.7854F, 0.0F));

        ModelPartData six = coral.addChild("six", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData cube_r71 = six.addChild("cube_r71", ModelPartBuilder.create().uv(195, 86).cuboid(-8.0F, -4.0F, -12.0F, 16.0F, 0.0F, 16.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 3.0F, 11.0F, -0.2618F, 0.0F, 0.0F));

        ModelPartData cube_r72 = six.addChild("cube_r72", ModelPartBuilder.create().uv(260, 86).cuboid(-8.0F, -4.0F, -12.0F, 16.0F, 0.0F, 16.0F, new Dilation(0.001F)), ModelTransform.of(11.0F, 3.0F, 0.0F, 0.0F, 1.5708F, 0.2618F));

        ModelPartData cube_r73 = six.addChild("cube_r73", ModelPartBuilder.create().uv(325, 86).cuboid(-8.0F, -4.0F, -12.0F, 16.0F, 0.0F, 16.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 3.0F, -11.0F, 2.8798F, 0.0F, -3.1416F));

        ModelPartData cube_r74 = six.addChild("cube_r74", ModelPartBuilder.create().uv(390, 86).cuboid(-8.0F, -4.0F, -12.0F, 16.0F, 0.0F, 16.0F, new Dilation(0.001F)), ModelTransform.of(-11.0F, 3.0F, 0.0F, 0.0F, -1.5708F, -0.2618F));

        ModelPartData cube_r75 = six.addChild("cube_r75", ModelPartBuilder.create().uv(455, 171).cuboid(-8.0F, -4.0F, -12.0F, 16.0F, 0.0F, 16.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, -2.0F, -9.0F, 2.3562F, 0.0F, -3.1416F));

        ModelPartData cube_r76 = six.addChild("cube_r76", ModelPartBuilder.create().uv(390, 171).cuboid(-8.0F, -4.0F, -12.0F, 16.0F, 0.0F, 16.0F, new Dilation(0.001F)), ModelTransform.of(-9.0F, -2.0F, 0.0F, 0.0F, -1.5708F, -0.7854F));

        ModelPartData cube_r77 = six.addChild("cube_r77", ModelPartBuilder.create().uv(325, 171).cuboid(-8.0F, -4.0F, -12.0F, 16.0F, 0.0F, 16.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, -2.0F, 9.0F, -0.7854F, 0.0F, 0.0F));

        ModelPartData cube_r78 = six.addChild("cube_r78", ModelPartBuilder.create().uv(260, 171).cuboid(-8.0F, -4.0F, -12.0F, 16.0F, 0.0F, 16.0F, new Dilation(0.001F)), ModelTransform.of(9.0F, -2.0F, 0.0F, 0.0F, 1.5708F, 0.7854F));

        ModelPartData cube_r79 = six.addChild("cube_r79", ModelPartBuilder.create().uv(260, 154).cuboid(-8.0F, -4.0F, -12.0F, 16.0F, 0.0F, 16.0F, new Dilation(0.001F)), ModelTransform.of(9.0F, -28.0F, 0.0F, 0.0F, 1.5708F, 0.7854F));

        ModelPartData cube_r80 = six.addChild("cube_r80", ModelPartBuilder.create().uv(195, 154).cuboid(-8.0F, -4.0F, -12.0F, 16.0F, 0.0F, 16.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, -28.0F, 9.0F, -0.7854F, 0.0F, 0.0F));

        ModelPartData cube_r81 = six.addChild("cube_r81", ModelPartBuilder.create().uv(130, 154).cuboid(-8.0F, -4.0F, -12.0F, 16.0F, 0.0F, 16.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, -28.0F, -9.0F, 2.3562F, 0.0F, -3.1416F));

        ModelPartData cube_r82 = six.addChild("cube_r82", ModelPartBuilder.create().uv(65, 154).cuboid(-8.0F, -4.0F, -12.0F, 16.0F, 0.0F, 16.0F, new Dilation(0.001F)), ModelTransform.of(-9.0F, -28.0F, 0.0F, 0.0F, -1.5708F, -0.7854F));

        ModelPartData cube_r83 = six.addChild("cube_r83", ModelPartBuilder.create().uv(0, 171).cuboid(-8.0F, -4.0F, -12.0F, 16.0F, 0.0F, 16.0F, new Dilation(0.001F)), ModelTransform.of(9.0F, -15.0F, 0.0F, 0.0F, 1.5708F, 0.7854F));

        ModelPartData cube_r84 = six.addChild("cube_r84", ModelPartBuilder.create().uv(65, 171).cuboid(-8.0F, -4.0F, -12.0F, 16.0F, 0.0F, 16.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, -15.0F, -9.0F, 2.3562F, 0.0F, -3.1416F));

        ModelPartData cube_r85 = six.addChild("cube_r85", ModelPartBuilder.create().uv(130, 171).cuboid(-8.0F, -4.0F, -12.0F, 16.0F, 0.0F, 16.0F, new Dilation(0.001F)), ModelTransform.of(-9.0F, -15.0F, 0.0F, 0.0F, -1.5708F, -0.7854F));

        ModelPartData cube_r86 = six.addChild("cube_r86", ModelPartBuilder.create().uv(195, 171).cuboid(-8.0F, -4.0F, -12.0F, 16.0F, 0.0F, 16.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, -15.0F, 9.0F, -0.7854F, 0.0F, 0.0F));

        ModelPartData cube_r87 = six.addChild("cube_r87", ModelPartBuilder.create().uv(390, 154).cuboid(-8.0F, -4.0F, -12.0F, 16.0F, 0.0F, 16.0F, new Dilation(0.001F)), ModelTransform.of(9.0F, -37.0F, 0.0F, 0.0F, 1.5708F, 0.2618F));

        ModelPartData cube_r88 = six.addChild("cube_r88", ModelPartBuilder.create().uv(325, 154).cuboid(-8.0F, -4.0F, -12.0F, 16.0F, 0.0F, 16.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, -37.0F, -9.0F, 2.8798F, 0.0F, -3.1416F));

        ModelPartData cube_r89 = six.addChild("cube_r89", ModelPartBuilder.create().uv(520, 154).cuboid(-8.0F, -4.0F, -12.0F, 16.0F, 0.0F, 16.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, -37.0F, 9.0F, -0.2618F, 0.0F, 0.0F));

        ModelPartData cube_r90 = six.addChild("cube_r90", ModelPartBuilder.create().uv(455, 154).cuboid(-8.0F, -4.0F, -12.0F, 16.0F, 0.0F, 16.0F, new Dilation(0.001F)), ModelTransform.of(-9.0F, -37.0F, 0.0F, 0.0F, -1.5708F, -0.2618F));

        ModelPartData cube_r91 = six.addChild("cube_r91", ModelPartBuilder.create().uv(325, 222).cuboid(-8.0F, -6.9583F, -5.6559F, 16.0F, 0.0F, 16.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, -42.0F, 0.0F, 0.0F, -1.5708F, 2.7053F));

        ModelPartData cube_r92 = six.addChild("cube_r92", ModelPartBuilder.create().uv(390, 222).cuboid(-8.0F, -6.9583F, -5.6559F, 16.0F, 0.0F, 16.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, -42.0F, 0.0F, 2.7053F, -0.7854F, 0.0F));

        ModelPartData cube_r93 = six.addChild("cube_r93", ModelPartBuilder.create().uv(455, 222).cuboid(-8.0F, -6.9583F, -5.6559F, 16.0F, 0.0F, 16.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, -42.0F, 0.0F, 2.7053F, 0.0F, 0.0F));

        ModelPartData cube_r94 = six.addChild("cube_r94", ModelPartBuilder.create().uv(520, 222).cuboid(-8.0F, -6.9583F, -5.6559F, 16.0F, 0.0F, 16.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, -42.0F, 0.0F, -0.4363F, 0.7854F, -3.1416F));

        ModelPartData cube_r95 = six.addChild("cube_r95", ModelPartBuilder.create().uv(0, 239).cuboid(-8.0F, -6.9583F, -5.6559F, 16.0F, 0.0F, 16.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, -42.0F, 0.0F, 0.0F, 1.5708F, -2.7053F));

        ModelPartData cube_r96 = six.addChild("cube_r96", ModelPartBuilder.create().uv(65, 239).cuboid(-8.0F, -6.9583F, -5.6559F, 16.0F, 0.0F, 16.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, -42.0F, 0.0F, 2.7053F, 0.7854F, 0.0F));

        ModelPartData cube_r97 = six.addChild("cube_r97", ModelPartBuilder.create().uv(130, 239).cuboid(-8.0F, -6.9583F, -5.6559F, 16.0F, 0.0F, 16.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, -42.0F, 0.0F, -0.4363F, 0.0F, -3.1416F));

        ModelPartData cube_r98 = six.addChild("cube_r98", ModelPartBuilder.create().uv(195, 239).cuboid(-8.0F, -6.9583F, -5.6559F, 16.0F, 0.0F, 16.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, -42.0F, 0.0F, -0.4363F, -0.7854F, -3.1416F));

        ModelPartData cube_r99 = six.addChild("cube_r99", ModelPartBuilder.create().uv(260, 239).cuboid(-8.0F, -6.9583F, -5.6559F, 16.0F, 0.0F, 16.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 1.0F, 0.0F, -0.4363F, -0.7854F, 0.0F));

        ModelPartData cube_r100 = six.addChild("cube_r100", ModelPartBuilder.create().uv(325, 239).cuboid(-8.0F, -6.9583F, -5.6559F, 16.0F, 0.0F, 16.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 1.0F, 0.0F, -0.4363F, 0.0F, 0.0F));

        ModelPartData cube_r101 = six.addChild("cube_r101", ModelPartBuilder.create().uv(390, 239).cuboid(-8.0F, -6.9583F, -5.6559F, 16.0F, 0.0F, 16.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 1.0F, 0.0F, 2.7053F, 0.7854F, 3.1416F));

        ModelPartData cube_r102 = six.addChild("cube_r102", ModelPartBuilder.create().uv(455, 239).cuboid(-8.0F, -6.9583F, -5.6559F, 16.0F, 0.0F, 16.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 1.0F, 0.0F, 0.0F, 1.5708F, 0.4363F));

        ModelPartData cube_r103 = six.addChild("cube_r103", ModelPartBuilder.create().uv(520, 239).cuboid(-8.0F, -6.9583F, -5.6559F, 16.0F, 0.0F, 16.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 1.0F, 0.0F, -0.4363F, 0.7854F, 0.0F));

        ModelPartData cube_r104 = six.addChild("cube_r104", ModelPartBuilder.create().uv(0, 256).cuboid(-8.0F, -6.9583F, -5.6559F, 16.0F, 0.0F, 16.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 1.0F, 0.0F, 2.7053F, 0.0F, 3.1416F));

        ModelPartData cube_r105 = six.addChild("cube_r105", ModelPartBuilder.create().uv(65, 256).cuboid(-8.0F, -6.9583F, -5.6559F, 16.0F, 0.0F, 16.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 1.0F, 0.0F, 2.7053F, -0.7854F, 3.1416F));

        ModelPartData cube_r106 = six.addChild("cube_r106", ModelPartBuilder.create().uv(130, 256).cuboid(-8.0F, -6.9583F, -5.6559F, 16.0F, 0.0F, 16.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 1.0F, 0.0F, 0.0F, -1.5708F, -0.4363F));

        ModelPartData cube_r107 = six.addChild("cube_r107", ModelPartBuilder.create().uv(126, 343).cuboid(-6.0F, -36.0F, -6.0F, 12.0F, 36.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(1.0F, 0.0F, 0.0F, 0.0F, -1.5708F, -0.0873F));

        ModelPartData cube_r108 = six.addChild("cube_r108", ModelPartBuilder.create().uv(151, 343).cuboid(-6.0F, -36.0F, -6.0F, 12.0F, 36.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(-1.0F, 0.0F, 0.0F, 0.0F, 1.5708F, 0.0873F));

        ModelPartData cube_r109 = six.addChild("cube_r109", ModelPartBuilder.create().uv(176, 343).cuboid(-6.0F, -36.0F, -6.0F, 12.0F, 36.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 0.0F, 1.0F, 3.0543F, 0.0F, -3.1416F));

        ModelPartData cube_r110 = six.addChild("cube_r110", ModelPartBuilder.create().uv(201, 343).cuboid(-6.0F, -36.0F, -6.0F, 12.0F, 36.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 0.0F, -1.0F, -0.0873F, 0.0F, 0.0F));

        ModelPartData cube_r111 = six.addChild("cube_r111", ModelPartBuilder.create().uv(194, 0).cuboid(-12.0F, -42.0F, 0.0F, 24.0F, 42.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -0.7854F, 0.0F));

        ModelPartData cube_r112 = six.addChild("cube_r112", ModelPartBuilder.create().uv(243, 0).cuboid(-12.0F, -42.0F, 0.0F, 24.0F, 42.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.7854F, 0.0F));

        ModelPartData octagon2 = six.addChild("octagon2", ModelPartBuilder.create().uv(84, 343).cuboid(-4.9706F, -21.0F, -12.0F, 9.9411F, 42.0F, 0.0F, new Dilation(0.001F))
                .uv(42, 343).cuboid(-4.9706F, -21.0F, 12.0F, 9.9411F, 42.0F, 0.0F, new Dilation(0.001F))
                .uv(191, 290).cuboid(12.0F, -21.0F, -4.9706F, 0.0F, 42.0F, 9.9411F, new Dilation(0.001F))
                .uv(149, 290).cuboid(-12.0F, -21.0F, -4.9706F, 0.0F, 42.0F, 9.9411F, new Dilation(0.001F)), ModelTransform.pivot(0.0F, -21.0F, 0.0F));

        ModelPartData octagon_r1 = octagon2.addChild("octagon_r1", ModelPartBuilder.create().uv(170, 290).cuboid(-12.0F, -21.0F, -4.9706F, 0.0F, 42.0F, 9.9411F, new Dilation(0.001F))
                .uv(212, 290).cuboid(12.0F, -21.0F, -4.9706F, 0.0F, 42.0F, 9.9411F, new Dilation(0.001F))
                .uv(63, 343).cuboid(-4.9706F, -21.0F, 12.0F, 9.9411F, 42.0F, 0.0F, new Dilation(0.001F))
                .uv(105, 343).cuboid(-4.9706F, -21.0F, -12.0F, 9.9411F, 42.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -0.7854F, 0.0F));

        ModelPartData seven = coral.addChild("seven", ModelPartBuilder.create().uv(150, 386).cuboid(-5.0F, -35.0F, -6.0F, 10.0F, 34.0F, 0.0F, new Dilation(0.001F))
                .uv(229, 393).cuboid(-5.0F, -35.0F, -12.0F, 10.0F, 0.0F, 6.0F, new Dilation(0.001F))
                .uv(229, 386).cuboid(-5.0F, -1.0F, -12.0F, 10.0F, 0.0F, 6.0F, new Dilation(0.001F))
                .uv(205, 386).cuboid(-5.0F, -35.0F, -12.0F, 0.0F, 34.0F, 6.0F, new Dilation(0.001F))
                .uv(192, 386).cuboid(5.0F, -35.0F, -12.0F, 0.0F, 34.0F, 6.0F, new Dilation(0.001F))
                .uv(97, 0).cuboid(-12.0F, -39.0F, -12.0F, 24.0F, 0.0F, 24.0F, new Dilation(0.001F))
                .uv(0, 0).cuboid(-12.0F, 0.0F, -12.0F, 24.0F, 0.0F, 24.0F, new Dilation(0.001F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData cube_r113 = seven.addChild("cube_r113", ModelPartBuilder.create().uv(455, 103).cuboid(-8.0F, -4.0F, -12.0F, 16.0F, 0.0F, 16.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, -19.0F, 9.0F, -0.7854F, 0.0F, 0.0F));

        ModelPartData cube_r114 = seven.addChild("cube_r114", ModelPartBuilder.create().uv(390, 103).cuboid(-8.0F, -4.0F, -12.0F, 16.0F, 0.0F, 16.0F, new Dilation(0.001F)), ModelTransform.of(-9.0F, -19.0F, 0.0F, 0.0F, -1.5708F, -0.7854F));

        ModelPartData cube_r115 = seven.addChild("cube_r115", ModelPartBuilder.create().uv(260, 103).cuboid(-8.0F, -4.0F, -12.0F, 16.0F, 0.0F, 16.0F, new Dilation(0.001F)), ModelTransform.of(9.0F, -7.0F, 0.0F, 0.0F, 1.5708F, 0.7854F));

        ModelPartData cube_r116 = seven.addChild("cube_r116", ModelPartBuilder.create().uv(195, 103).cuboid(-8.0F, -4.0F, -12.0F, 16.0F, 0.0F, 16.0F, new Dilation(0.001F)), ModelTransform.of(-9.0F, -7.0F, 0.0F, 0.0F, -1.5708F, -0.7854F));

        ModelPartData cube_r117 = seven.addChild("cube_r117", ModelPartBuilder.create().uv(130, 103).cuboid(-8.0F, -4.0F, -12.0F, 16.0F, 0.0F, 16.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, -7.0F, 9.0F, -0.7854F, 0.0F, 0.0F));

        ModelPartData cube_r118 = seven.addChild("cube_r118", ModelPartBuilder.create().uv(325, 103).cuboid(-8.0F, -4.0F, -12.0F, 16.0F, 0.0F, 16.0F, new Dilation(0.001F)), ModelTransform.of(9.0F, -19.0F, 0.0F, 0.0F, 1.5708F, 0.7854F));

        ModelPartData cube_r119 = seven.addChild("cube_r119", ModelPartBuilder.create().uv(520, 103).cuboid(-8.0F, -4.0F, -12.0F, 16.0F, 0.0F, 16.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, -31.0F, 9.0F, -0.7854F, 0.0F, 0.0F));

        ModelPartData cube_r120 = seven.addChild("cube_r120", ModelPartBuilder.create().uv(0, 120).cuboid(-8.0F, -4.0F, -12.0F, 16.0F, 0.0F, 16.0F, new Dilation(0.001F)), ModelTransform.of(-9.0F, -31.0F, 0.0F, 0.0F, -1.5708F, -0.7854F));

        ModelPartData cube_r121 = seven.addChild("cube_r121", ModelPartBuilder.create().uv(65, 120).cuboid(-8.0F, -4.0F, -12.0F, 16.0F, 0.0F, 16.0F, new Dilation(0.001F)), ModelTransform.of(9.0F, -31.0F, 0.0F, 0.0F, 1.5708F, 0.7854F));

        ModelPartData cube_r122 = seven.addChild("cube_r122", ModelPartBuilder.create().uv(130, 120).cuboid(-8.0F, -4.0F, -12.0F, 16.0F, 0.0F, 16.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, -31.0F, -9.0F, 2.3562F, 0.0F, -3.1416F));

        ModelPartData cube_r123 = seven.addChild("cube_r123", ModelPartBuilder.create().uv(65, 103).cuboid(-8.0F, -4.0F, -12.0F, 16.0F, 0.0F, 16.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 3.0F, 12.0F, -0.2618F, 0.0F, 0.0F));

        ModelPartData cube_r124 = seven.addChild("cube_r124", ModelPartBuilder.create().uv(0, 103).cuboid(-8.0F, -4.0F, -12.0F, 16.0F, 0.0F, 16.0F, new Dilation(0.001F)), ModelTransform.of(12.0F, 3.0F, 0.0F, 0.0F, 1.5708F, 0.2618F));

        ModelPartData cube_r125 = seven.addChild("cube_r125", ModelPartBuilder.create().uv(520, 86).cuboid(-8.0F, -4.0F, -12.0F, 16.0F, 0.0F, 16.0F, new Dilation(0.001F)), ModelTransform.of(-12.0F, 3.0F, 0.0F, 0.0F, -1.5708F, -0.2618F));

        ModelPartData cube_r126 = seven.addChild("cube_r126", ModelPartBuilder.create().uv(455, 86).cuboid(-8.0F, -4.0F, -12.0F, 16.0F, 0.0F, 16.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 3.0F, -12.0F, 2.8798F, 0.0F, -3.1416F));

        ModelPartData cube_r127 = seven.addChild("cube_r127", ModelPartBuilder.create().uv(195, 120).cuboid(-8.0F, -4.0F, -12.0F, 16.0F, 0.0F, 16.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, -37.0F, 9.0F, -0.2618F, 0.0F, 0.0F));

        ModelPartData cube_r128 = seven.addChild("cube_r128", ModelPartBuilder.create().uv(260, 120).cuboid(-8.0F, -4.0F, -12.0F, 16.0F, 0.0F, 16.0F, new Dilation(0.001F)), ModelTransform.of(-9.0F, -37.0F, 0.0F, 0.0F, -1.5708F, -0.2618F));

        ModelPartData cube_r129 = seven.addChild("cube_r129", ModelPartBuilder.create().uv(325, 120).cuboid(-8.0F, -4.0F, -12.0F, 16.0F, 0.0F, 16.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, -37.0F, -9.0F, 2.8798F, 0.0F, -3.1416F));

        ModelPartData cube_r130 = seven.addChild("cube_r130", ModelPartBuilder.create().uv(390, 120).cuboid(-8.0F, -4.0F, -12.0F, 16.0F, 0.0F, 16.0F, new Dilation(0.001F)), ModelTransform.of(9.0F, -37.0F, 0.0F, 0.0F, 1.5708F, 0.2618F));

        ModelPartData bone9 = seven.addChild("bone9", ModelPartBuilder.create().uv(65, 290).cuboid(27.2752F, -21.0F, -0.8776F, 0.0F, 42.0F, 9.9411F, new Dilation(0.001F))
                .uv(21, 343).cuboid(10.3046F, -21.0F, 16.093F, 9.9411F, 42.0F, 0.0F, new Dilation(0.001F))
                .uv(107, 290).cuboid(3.2752F, -21.0F, -0.8776F, 0.0F, 42.0F, 9.9411F, new Dilation(0.001F))
                .uv(42, 427).cuboid(10.3046F, -21.0F, -7.907F, 9.9411F, 8.0F, 0.0F, new Dilation(0.001F))
                .uv(63, 427).cuboid(10.3046F, 20.0F, -7.907F, 9.9411F, 1.0F, 0.0F, new Dilation(0.001F)), ModelTransform.pivot(-15.2752F, -21.0F, -4.093F));

        ModelPartData octagon_r2 = bone9.addChild("octagon_r2", ModelPartBuilder.create().uv(171, 386).cuboid(-4.9706F, -13.0F, -12.0F, 9.9411F, 33.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.309F, 0.0F));

        ModelPartData octagon_r3 = bone9.addChild("octagon_r3", ModelPartBuilder.create().uv(233, 290).cuboid(-4.9706F, -21.0F, -12.0F, 9.9411F, 42.0F, 0.0F, new Dilation(0.001F))
                .uv(86, 290).cuboid(-12.0F, -21.0F, -4.9706F, 0.0F, 42.0F, 9.9411F, new Dilation(0.001F))
                .uv(128, 290).cuboid(12.0F, -21.0F, -4.9706F, 0.0F, 42.0F, 9.9411F, new Dilation(0.001F))
                .uv(0, 343).cuboid(-4.9706F, -21.0F, 12.0F, 9.9411F, 42.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(15.2752F, 0.0F, 4.093F, 0.0F, -0.7854F, 0.0F));

        ModelPartData octagon_r4 = bone9.addChild("octagon_r4", ModelPartBuilder.create().uv(0, 427).cuboid(-4.9706F, -21.0F, -12.0F, 9.9411F, 8.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(15.2752F, 37.6777F, -8.6141F, -0.7854F, 0.0F, 0.0F));

        ModelPartData octagon_r5 = bone9.addChild("octagon_r5", ModelPartBuilder.create().uv(21, 427).cuboid(-4.9706F, -21.0F, -12.0F, 9.9411F, 8.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(15.2752F, -6.636F, 15.4275F, 0.7854F, 0.0F, 0.0F));

        ModelPartData octagon_r6 = bone9.addChild("octagon_r6", ModelPartBuilder.create().uv(218, 386).cuboid(-12.0F, -13.0F, -4.9706F, 0.0F, 33.0F, 5.0F, new Dilation(0.001F)), ModelTransform.of(30.5514F, 0.0F, 0.0003F, 0.0F, -0.2618F, 0.0F));
        return TexturedModelData.of(modelData, 1024, 1024);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red,
            float green, float blue, float alpha) {
    }

    @Override
    public ModelPart getPart() {
        return coral;
    }

    @Override
    public void setAngles(Entity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw,
            float headPitch) {
    }
}
