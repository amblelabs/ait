package dev.amble.ait.client.models.machines;

import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.animation.Animation;
import net.minecraft.client.util.math.MatrixStack;

import dev.amble.ait.client.animation.machines.ToyotaSpinningRotorAnimation;
import dev.amble.ait.core.blockentities.ToyotaSpinningRotorBlockEntity;
import dev.amble.ait.core.tardis.handler.travel.TravelHandlerBase;


public class ToyotaSpinningRotorModel extends SpinningRotorModel {
    private final ModelPart whirlagig;
    public ToyotaSpinningRotorModel(ModelPart root) {
        this.whirlagig = root.getChild("whirlagig");
    }
    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData whirlagig = modelPartData.addChild("whirlagig", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 22.5F, 0.0F));

        ModelPartData unspinnable = whirlagig.addChild("unspinnable", ModelPartBuilder.create().uv(0, 0).cuboid(-9.0F, -30.0F, -8.0F, 18.0F, 0.0F, 16.0F, new Dilation(0.0F))
                .uv(326, 0).cuboid(-31.0F, -68.75F, -31.0F, 62.0F, 0.0F, 62.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 30.5F, 0.0F));

        ModelPartData cube_r1 = unspinnable.addChild("cube_r1", ModelPartBuilder.create().uv(68, 0).cuboid(-9.55F, -31.0F, -5.5F, 2.0F, 2.0F, 11.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, -3.1416F, -0.5236F, 3.1416F));

        ModelPartData cube_r2 = unspinnable.addChild("cube_r2", ModelPartBuilder.create().uv(68, 0).cuboid(-9.55F, -31.0F, -5.5F, 2.0F, 2.0F, 11.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, -3.1416F, 0.5236F, 3.1416F));

        ModelPartData cube_r3 = unspinnable.addChild("cube_r3", ModelPartBuilder.create().uv(68, 0).cuboid(-9.55F, -31.0F, -5.5F, 2.0F, 2.0F, 11.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.5708F, 0.0F));

        ModelPartData cube_r4 = unspinnable.addChild("cube_r4", ModelPartBuilder.create().uv(68, 0).cuboid(-9.55F, -31.0F, -5.5F, 2.0F, 2.0F, 11.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.5236F, 0.0F));

        ModelPartData cube_r5 = unspinnable.addChild("cube_r5", ModelPartBuilder.create().uv(68, 0).cuboid(-9.55F, -28.0F, -5.5F, 2.0F, 2.0F, 11.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -3.0F, 0.0F, 0.0F, -0.5236F, 0.0F));

        ModelPartData cube_r6 = unspinnable.addChild("cube_r6", ModelPartBuilder.create().uv(68, 0).cuboid(-8.55F, -31.0F, -5.5F, 2.0F, 2.0F, 11.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, -1.0F, 0.0F, -1.5708F, 0.0F));

        ModelPartData adapter = whirlagig.addChild("adapter", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData bone2 = adapter.addChild("bone2", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData bone = bone2.addChild("bone", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData cube_r7 = bone.addChild("cube_r7", ModelPartBuilder.create().uv(162, 328).cuboid(-1.0F, -19.1366F, -5.275F, 2.0F, 11.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 1.0472F, 0.0F, 0.0F));

        ModelPartData bone3 = bone2.addChild("bone3", ModelPartBuilder.create(), ModelTransform.of(0.0F, 0.5F, 0.0F, 0.0F, 0.2618F, 0.0F));

        ModelPartData cube_r8 = bone3.addChild("cube_r8", ModelPartBuilder.create().uv(322, 268).cuboid(-4.0F, -19.1366F, -4.275F, 8.0F, 11.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 1.0472F, 0.0F, 0.0F));

        ModelPartData bone128 = adapter.addChild("bone128", ModelPartBuilder.create(), ModelTransform.of(0.0F, 0.0F, 0.0F, -3.1416F, 0.0F, 3.1416F));

        ModelPartData bone129 = bone128.addChild("bone129", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData cube_r9 = bone129.addChild("cube_r9", ModelPartBuilder.create().uv(162, 328).cuboid(-1.0F, -19.1366F, -5.275F, 2.0F, 11.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 1.0472F, 0.0F, 0.0F));

        ModelPartData bone130 = bone128.addChild("bone130", ModelPartBuilder.create(), ModelTransform.of(0.0F, 0.5F, 0.0F, 0.0F, 0.2618F, 0.0F));

        ModelPartData cube_r10 = bone130.addChild("cube_r10", ModelPartBuilder.create().uv(322, 268).cuboid(-4.0F, -19.1366F, -4.275F, 8.0F, 11.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 1.0472F, 0.0F, 0.0F));

        ModelPartData bone26 = adapter.addChild("bone26", ModelPartBuilder.create(), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.5236F, 0.0F));

        ModelPartData bone27 = bone26.addChild("bone27", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData cube_r11 = bone27.addChild("cube_r11", ModelPartBuilder.create().uv(162, 328).cuboid(-1.0F, -19.1366F, -5.275F, 2.0F, 11.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 1.0472F, 0.0F, 0.0F));

        ModelPartData bone28 = bone26.addChild("bone28", ModelPartBuilder.create(), ModelTransform.of(0.0F, 0.5F, 0.0F, 0.0F, 0.2618F, 0.0F));

        ModelPartData cube_r12 = bone28.addChild("cube_r12", ModelPartBuilder.create().uv(322, 268).cuboid(-4.0F, -19.1366F, -4.275F, 8.0F, 11.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 1.0472F, 0.0F, 0.0F));

        ModelPartData bone131 = adapter.addChild("bone131", ModelPartBuilder.create(), ModelTransform.of(0.0F, 0.0F, 0.0F, -3.1416F, -0.5236F, 3.1416F));

        ModelPartData bone132 = bone131.addChild("bone132", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData cube_r13 = bone132.addChild("cube_r13", ModelPartBuilder.create().uv(162, 328).cuboid(-1.0F, -19.1366F, -5.275F, 2.0F, 11.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 1.0472F, 0.0F, 0.0F));

        ModelPartData bone133 = bone131.addChild("bone133", ModelPartBuilder.create(), ModelTransform.of(0.0F, 0.5F, 0.0F, 0.0F, 0.2618F, 0.0F));

        ModelPartData cube_r14 = bone133.addChild("cube_r14", ModelPartBuilder.create().uv(322, 268).cuboid(-4.0F, -19.1366F, -4.275F, 8.0F, 11.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 1.0472F, 0.0F, 0.0F));

        ModelPartData bone29 = adapter.addChild("bone29", ModelPartBuilder.create(), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData bone30 = bone29.addChild("bone30", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData cube_r15 = bone30.addChild("cube_r15", ModelPartBuilder.create().uv(162, 328).cuboid(-1.0F, -19.1366F, -5.275F, 2.0F, 11.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 1.0472F, 0.0F, 0.0F));

        ModelPartData bone31 = bone29.addChild("bone31", ModelPartBuilder.create(), ModelTransform.of(0.0F, 0.5F, 0.0F, 0.0F, 0.2618F, 0.0F));

        ModelPartData cube_r16 = bone31.addChild("cube_r16", ModelPartBuilder.create().uv(322, 268).cuboid(-4.0F, -19.1366F, -4.275F, 8.0F, 11.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 1.0472F, 0.0F, 0.0F));

        ModelPartData bone134 = adapter.addChild("bone134", ModelPartBuilder.create(), ModelTransform.of(0.0F, 0.0F, 0.0F, -3.1416F, -1.0472F, 3.1416F));

        ModelPartData bone135 = bone134.addChild("bone135", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData cube_r17 = bone135.addChild("cube_r17", ModelPartBuilder.create().uv(162, 328).cuboid(-1.0F, -19.1366F, -5.275F, 2.0F, 11.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 1.0472F, 0.0F, 0.0F));

        ModelPartData bone136 = bone134.addChild("bone136", ModelPartBuilder.create(), ModelTransform.of(0.0F, 0.5F, 0.0F, 0.0F, 0.2618F, 0.0F));

        ModelPartData cube_r18 = bone136.addChild("cube_r18", ModelPartBuilder.create().uv(322, 268).cuboid(-4.0F, -19.1366F, -4.275F, 8.0F, 11.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 1.0472F, 0.0F, 0.0F));

        ModelPartData bone32 = adapter.addChild("bone32", ModelPartBuilder.create(), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.5708F, 0.0F));

        ModelPartData bone33 = bone32.addChild("bone33", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData cube_r19 = bone33.addChild("cube_r19", ModelPartBuilder.create().uv(162, 328).cuboid(-1.0F, -19.1366F, -5.275F, 2.0F, 11.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 1.0472F, 0.0F, 0.0F));

        ModelPartData bone34 = bone32.addChild("bone34", ModelPartBuilder.create(), ModelTransform.of(0.0F, 0.5F, 0.0F, 0.0F, 0.2618F, 0.0F));

        ModelPartData cube_r20 = bone34.addChild("cube_r20", ModelPartBuilder.create().uv(322, 268).cuboid(-4.0F, -19.1366F, -4.275F, 8.0F, 11.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 1.0472F, 0.0F, 0.0F));

        ModelPartData bone137 = adapter.addChild("bone137", ModelPartBuilder.create(), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.5708F, 0.0F));

        ModelPartData bone138 = bone137.addChild("bone138", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData cube_r21 = bone138.addChild("cube_r21", ModelPartBuilder.create().uv(162, 328).cuboid(-1.0F, -19.1366F, -5.275F, 2.0F, 11.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 1.0472F, 0.0F, 0.0F));

        ModelPartData bone139 = bone137.addChild("bone139", ModelPartBuilder.create(), ModelTransform.of(0.0F, 0.5F, 0.0F, 0.0F, 0.2618F, 0.0F));

        ModelPartData cube_r22 = bone139.addChild("cube_r22", ModelPartBuilder.create().uv(322, 268).cuboid(-4.0F, -19.1366F, -4.275F, 8.0F, 11.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 1.0472F, 0.0F, 0.0F));

        ModelPartData bone35 = adapter.addChild("bone35", ModelPartBuilder.create(), ModelTransform.of(0.0F, 0.0F, 0.0F, -3.1416F, 1.0472F, -3.1416F));

        ModelPartData bone36 = bone35.addChild("bone36", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData cube_r23 = bone36.addChild("cube_r23", ModelPartBuilder.create().uv(162, 328).cuboid(-1.0F, -19.1366F, -5.275F, 2.0F, 11.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 1.0472F, 0.0F, 0.0F));

        ModelPartData bone37 = bone35.addChild("bone37", ModelPartBuilder.create(), ModelTransform.of(0.0F, 0.5F, 0.0F, 0.0F, 0.2618F, 0.0F));

        ModelPartData cube_r24 = bone37.addChild("cube_r24", ModelPartBuilder.create().uv(322, 268).cuboid(-4.0F, -19.1366F, -4.275F, 8.0F, 11.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 1.0472F, 0.0F, 0.0F));

        ModelPartData bone140 = adapter.addChild("bone140", ModelPartBuilder.create(), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone141 = bone140.addChild("bone141", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData cube_r25 = bone141.addChild("cube_r25", ModelPartBuilder.create().uv(162, 328).cuboid(-1.0F, -19.1366F, -5.275F, 2.0F, 11.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 1.0472F, 0.0F, 0.0F));

        ModelPartData bone142 = bone140.addChild("bone142", ModelPartBuilder.create(), ModelTransform.of(0.0F, 0.5F, 0.0F, 0.0F, 0.2618F, 0.0F));

        ModelPartData cube_r26 = bone142.addChild("cube_r26", ModelPartBuilder.create().uv(322, 268).cuboid(-4.0F, -19.1366F, -4.275F, 8.0F, 11.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 1.0472F, 0.0F, 0.0F));

        ModelPartData bone41 = adapter.addChild("bone41", ModelPartBuilder.create(), ModelTransform.of(0.0F, 0.0F, 0.0F, -3.1416F, 0.5236F, -3.1416F));

        ModelPartData bone42 = bone41.addChild("bone42", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData cube_r27 = bone42.addChild("cube_r27", ModelPartBuilder.create().uv(162, 328).cuboid(-1.0F, -19.1366F, -5.275F, 2.0F, 11.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 1.0472F, 0.0F, 0.0F));

        ModelPartData bone43 = bone41.addChild("bone43", ModelPartBuilder.create(), ModelTransform.of(0.0F, 0.5F, 0.0F, 0.0F, 0.2618F, 0.0F));

        ModelPartData cube_r28 = bone43.addChild("cube_r28", ModelPartBuilder.create().uv(322, 268).cuboid(-4.0F, -19.1366F, -4.275F, 8.0F, 11.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 1.0472F, 0.0F, 0.0F));

        ModelPartData bone143 = adapter.addChild("bone143", ModelPartBuilder.create(), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -0.5236F, 0.0F));

        ModelPartData bone144 = bone143.addChild("bone144", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData cube_r29 = bone144.addChild("cube_r29", ModelPartBuilder.create().uv(162, 328).cuboid(-1.0F, -19.1366F, -5.275F, 2.0F, 11.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 1.0472F, 0.0F, 0.0F));

        ModelPartData bone145 = bone143.addChild("bone145", ModelPartBuilder.create(), ModelTransform.of(0.0F, 0.5F, 0.0F, 0.0F, 0.2618F, 0.0F));

        ModelPartData cube_r30 = bone145.addChild("cube_r30", ModelPartBuilder.create().uv(322, 268).cuboid(-4.0F, -19.1366F, -4.275F, 8.0F, 11.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 1.0472F, 0.0F, 0.0F));

        ModelPartData ring1 = whirlagig.addChild("ring1", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData bone5 = ring1.addChild("bone5", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData bone6 = bone5.addChild("bone6", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData cube_r31 = bone6.addChild("cube_r31", ModelPartBuilder.create().uv(308, 307).cuboid(-1.0F, -8.0F, 0.0F, 2.0F, 10.0F, 12.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -5.0F, -19.2102F, 1.5708F, 0.0F, 0.0F));

        ModelPartData bone7 = bone5.addChild("bone7", ModelPartBuilder.create(), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.2618F, 0.0F));

        ModelPartData cube_r32 = bone7.addChild("cube_r32", ModelPartBuilder.create().uv(270, 198).cuboid(-6.0F, -7.0F, 0.5F, 12.0F, 10.0F, 11.0F, new Dilation(0.25F))
                .uv(240, 22).cuboid(-6.0F, -7.0F, 0.5F, 12.0F, 10.0F, 11.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -5.0F, -19.2102F, 1.5708F, 0.0F, 0.0F));

        ModelPartData bone110 = ring1.addChild("bone110", ModelPartBuilder.create(), ModelTransform.of(0.0F, 0.0F, 0.0F, -3.1416F, 0.0F, 3.1416F));

        ModelPartData bone111 = bone110.addChild("bone111", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData cube_r33 = bone111.addChild("cube_r33", ModelPartBuilder.create().uv(308, 307).cuboid(-1.0F, -8.0F, 0.0F, 2.0F, 10.0F, 12.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -5.0F, -19.2102F, 1.5708F, 0.0F, 0.0F));

        ModelPartData bone112 = bone110.addChild("bone112", ModelPartBuilder.create(), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.2618F, 0.0F));

        ModelPartData cube_r34 = bone112.addChild("cube_r34", ModelPartBuilder.create().uv(92, 284).cuboid(-6.0F, -7.0F, 0.5F, 12.0F, 10.0F, 11.0F, new Dilation(0.25F))
                .uv(240, 22).cuboid(-6.0F, -7.0F, 0.5F, 12.0F, 10.0F, 11.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -5.0F, -19.2102F, 1.5708F, 0.0F, 0.0F));

        ModelPartData bone14 = ring1.addChild("bone14", ModelPartBuilder.create(), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.5236F, 0.0F));

        ModelPartData bone15 = bone14.addChild("bone15", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData cube_r35 = bone15.addChild("cube_r35", ModelPartBuilder.create().uv(308, 307).cuboid(-1.0F, -8.0F, 0.0F, 2.0F, 10.0F, 12.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -5.0F, -19.2102F, 1.5708F, 0.0F, 0.0F));

        ModelPartData bone16 = bone14.addChild("bone16", ModelPartBuilder.create(), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.2618F, 0.0F));

        ModelPartData cube_r36 = bone16.addChild("cube_r36", ModelPartBuilder.create().uv(230, 263).cuboid(-6.0F, -7.0F, 0.5F, 12.0F, 10.0F, 11.0F, new Dilation(0.25F))
                .uv(240, 22).cuboid(-6.0F, -7.0F, 0.5F, 12.0F, 10.0F, 11.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -5.0F, -19.2102F, 1.5708F, 0.0F, 0.0F));

        ModelPartData bone113 = ring1.addChild("bone113", ModelPartBuilder.create(), ModelTransform.of(0.0F, 0.0F, 0.0F, -3.1416F, -0.5236F, 3.1416F));

        ModelPartData bone114 = bone113.addChild("bone114", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData cube_r37 = bone114.addChild("cube_r37", ModelPartBuilder.create().uv(308, 307).cuboid(-1.0F, -8.0F, 0.0F, 2.0F, 10.0F, 12.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -5.0F, -19.2102F, 1.5708F, 0.0F, 0.0F));

        ModelPartData bone115 = bone113.addChild("bone115", ModelPartBuilder.create(), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.2618F, 0.0F));

        ModelPartData cube_r38 = bone115.addChild("cube_r38", ModelPartBuilder.create().uv(138, 284).cuboid(-6.0F, -7.0F, 0.5F, 12.0F, 10.0F, 11.0F, new Dilation(0.25F))
                .uv(240, 22).cuboid(-6.0F, -7.0F, 0.5F, 12.0F, 10.0F, 11.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -5.0F, -19.2102F, 1.5708F, 0.0F, 0.0F));

        ModelPartData bone17 = ring1.addChild("bone17", ModelPartBuilder.create(), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData bone18 = bone17.addChild("bone18", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData cube_r39 = bone18.addChild("cube_r39", ModelPartBuilder.create().uv(308, 307).cuboid(-1.0F, -8.0F, 0.0F, 2.0F, 10.0F, 12.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -5.0F, -19.2102F, 1.5708F, 0.0F, 0.0F));

        ModelPartData bone19 = bone17.addChild("bone19", ModelPartBuilder.create(), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.2618F, 0.0F));

        ModelPartData cube_r40 = bone19.addChild("cube_r40", ModelPartBuilder.create().uv(184, 263).cuboid(-6.0F, -7.0F, 0.5F, 12.0F, 10.0F, 11.0F, new Dilation(0.25F))
                .uv(240, 22).cuboid(-6.0F, -7.0F, 0.5F, 12.0F, 10.0F, 11.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -5.0F, -19.2102F, 1.5708F, 0.0F, 0.0F));

        ModelPartData bone116 = ring1.addChild("bone116", ModelPartBuilder.create(), ModelTransform.of(0.0F, 0.0F, 0.0F, -3.1416F, -1.0472F, 3.1416F));

        ModelPartData bone117 = bone116.addChild("bone117", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData cube_r41 = bone117.addChild("cube_r41", ModelPartBuilder.create().uv(308, 307).cuboid(-1.0F, -8.0F, 0.0F, 2.0F, 10.0F, 12.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -5.0F, -19.2102F, 1.5708F, 0.0F, 0.0F));

        ModelPartData bone118 = bone116.addChild("bone118", ModelPartBuilder.create(), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.2618F, 0.0F));

        ModelPartData cube_r42 = bone118.addChild("cube_r42", ModelPartBuilder.create().uv(46, 279).cuboid(-6.0F, -7.0F, 0.5F, 12.0F, 10.0F, 11.0F, new Dilation(0.25F))
                .uv(240, 22).cuboid(-6.0F, -7.0F, 0.5F, 12.0F, 10.0F, 11.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -5.0F, -19.2102F, 1.5708F, 0.0F, 0.0F));

        ModelPartData bone20 = ring1.addChild("bone20", ModelPartBuilder.create(), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.5708F, 0.0F));

        ModelPartData bone21 = bone20.addChild("bone21", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData cube_r43 = bone21.addChild("cube_r43", ModelPartBuilder.create().uv(308, 307).cuboid(-1.0F, -8.0F, 0.0F, 2.0F, 10.0F, 12.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -5.0F, -19.2102F, 1.5708F, 0.0F, 0.0F));

        ModelPartData bone22 = bone20.addChild("bone22", ModelPartBuilder.create(), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.2618F, 0.0F));

        ModelPartData cube_r44 = bone22.addChild("cube_r44", ModelPartBuilder.create().uv(138, 263).cuboid(-6.0F, -7.0F, 0.5F, 12.0F, 10.0F, 11.0F, new Dilation(0.25F))
                .uv(240, 22).cuboid(-6.0F, -7.0F, 0.5F, 12.0F, 10.0F, 11.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -5.0F, -19.2102F, 1.5708F, 0.0F, 0.0F));

        ModelPartData bone119 = ring1.addChild("bone119", ModelPartBuilder.create(), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.5708F, 0.0F));

        ModelPartData bone120 = bone119.addChild("bone120", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData cube_r45 = bone120.addChild("cube_r45", ModelPartBuilder.create().uv(308, 307).cuboid(-1.0F, -8.0F, 0.0F, 2.0F, 10.0F, 12.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -5.0F, -19.2102F, 1.5708F, 0.0F, 0.0F));

        ModelPartData bone121 = bone119.addChild("bone121", ModelPartBuilder.create(), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.2618F, 0.0F));

        ModelPartData cube_r46 = bone121.addChild("cube_r46", ModelPartBuilder.create().uv(0, 279).cuboid(-6.0F, -7.0F, 0.5F, 12.0F, 10.0F, 11.0F, new Dilation(0.25F))
                .uv(240, 22).cuboid(-6.0F, -7.0F, 0.5F, 12.0F, 10.0F, 11.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -5.0F, -19.2102F, 1.5708F, 0.0F, 0.0F));

        ModelPartData bone23 = ring1.addChild("bone23", ModelPartBuilder.create(), ModelTransform.of(0.0F, 0.0F, 0.0F, -3.1416F, 1.0472F, 3.1416F));

        ModelPartData bone24 = bone23.addChild("bone24", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData cube_r47 = bone24.addChild("cube_r47", ModelPartBuilder.create().uv(308, 307).cuboid(-1.0F, -8.0F, 0.0F, 2.0F, 10.0F, 12.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -5.0F, -19.2102F, 1.5708F, 0.0F, 0.0F));

        ModelPartData bone25 = bone23.addChild("bone25", ModelPartBuilder.create(), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.2618F, 0.0F));

        ModelPartData cube_r48 = bone25.addChild("cube_r48", ModelPartBuilder.create().uv(92, 263).cuboid(-6.0F, -7.0F, 0.5F, 12.0F, 10.0F, 11.0F, new Dilation(0.25F))
                .uv(240, 22).cuboid(-6.0F, -7.0F, 0.5F, 12.0F, 10.0F, 11.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -5.0F, -19.2102F, 1.5708F, 0.0F, 0.0F));

        ModelPartData bone122 = ring1.addChild("bone122", ModelPartBuilder.create(), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone123 = bone122.addChild("bone123", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData cube_r49 = bone123.addChild("cube_r49", ModelPartBuilder.create().uv(308, 307).cuboid(-1.0F, -8.0F, 0.0F, 2.0F, 10.0F, 12.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -5.0F, -19.2102F, 1.5708F, 0.0F, 0.0F));

        ModelPartData bone124 = bone122.addChild("bone124", ModelPartBuilder.create(), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.2618F, 0.0F));

        ModelPartData cube_r50 = bone124.addChild("cube_r50", ModelPartBuilder.create().uv(276, 263).cuboid(-6.0F, -7.0F, 0.5F, 12.0F, 10.0F, 11.0F, new Dilation(0.25F))
                .uv(240, 22).cuboid(-6.0F, -7.0F, 0.5F, 12.0F, 10.0F, 11.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -5.0F, -19.2102F, 1.5708F, 0.0F, 0.0F));

        ModelPartData bone38 = ring1.addChild("bone38", ModelPartBuilder.create(), ModelTransform.of(0.0F, 0.0F, 0.0F, -3.1416F, 0.5236F, 3.1416F));

        ModelPartData bone39 = bone38.addChild("bone39", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData cube_r51 = bone39.addChild("cube_r51", ModelPartBuilder.create().uv(308, 307).cuboid(-1.0F, -8.0F, 0.0F, 2.0F, 10.0F, 12.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -5.0F, -19.2102F, 1.5708F, 0.0F, 0.0F));

        ModelPartData bone40 = bone38.addChild("bone40", ModelPartBuilder.create(), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.2618F, 0.0F));

        ModelPartData cube_r52 = bone40.addChild("cube_r52", ModelPartBuilder.create().uv(46, 258).cuboid(-6.0F, -7.0F, 0.5F, 12.0F, 10.0F, 11.0F, new Dilation(0.25F))
                .uv(240, 22).cuboid(-6.0F, -7.0F, 0.5F, 12.0F, 10.0F, 11.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -5.0F, -19.2102F, 1.5708F, 0.0F, 0.0F));

        ModelPartData bone125 = ring1.addChild("bone125", ModelPartBuilder.create(), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -0.5236F, 0.0F));

        ModelPartData bone126 = bone125.addChild("bone126", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData cube_r53 = bone126.addChild("cube_r53", ModelPartBuilder.create().uv(308, 307).cuboid(-1.0F, -8.0F, 0.0F, 2.0F, 10.0F, 12.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -5.0F, -19.2102F, 1.5708F, 0.0F, 0.0F));

        ModelPartData bone127 = bone125.addChild("bone127", ModelPartBuilder.create(), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.2618F, 0.0F));

        ModelPartData cube_r54 = bone127.addChild("cube_r54", ModelPartBuilder.create().uv(270, 219).cuboid(-6.0F, -7.0F, 0.5F, 12.0F, 10.0F, 11.0F, new Dilation(0.25F))
                .uv(240, 22).cuboid(-6.0F, -7.0F, 0.5F, 12.0F, 10.0F, 11.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -5.0F, -19.2102F, 1.5708F, 0.0F, 0.0F));

        ModelPartData ring3 = ring1.addChild("ring3", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, -15.0F, 0.0F));

        ModelPartData bone11 = ring3.addChild("bone11", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, -7.0F, 0.0F));

        ModelPartData bone12 = bone11.addChild("bone12", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, -15.0F));

        ModelPartData cube_r55 = bone12.addChild("cube_r55", ModelPartBuilder.create().uv(292, 240).cuboid(-1.0F, -8.0F, 0.0F, 2.0F, 11.0F, 12.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -5.0F, -19.2103F, 1.5708F, 0.0F, 0.0F));

        ModelPartData bone13 = bone11.addChild("bone13", ModelPartBuilder.create(), ModelTransform.of(-3.6882F, 0.0F, -13.7644F, 0.0F, 0.2618F, 0.0F));

        ModelPartData cube_r56 = bone13.addChild("cube_r56", ModelPartBuilder.create().uv(124, 132).cuboid(-10.0F, -7.0F, 0.5F, 20.0F, 11.0F, 11.0F, new Dilation(0.25F))
                .uv(124, 0).cuboid(-10.0F, -7.0F, 0.5F, 20.0F, 11.0F, 11.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -5.0F, -19.2103F, 1.5708F, 0.0F, 0.0F));

        ModelPartData bone74 = ring3.addChild("bone74", ModelPartBuilder.create(), ModelTransform.of(0.0F, -7.0F, 0.0F, -3.1416F, 0.0F, 3.1416F));

        ModelPartData bone75 = bone74.addChild("bone75", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, -15.0F));

        ModelPartData cube_r57 = bone75.addChild("cube_r57", ModelPartBuilder.create().uv(292, 240).cuboid(-1.0F, -8.0F, 0.0F, 2.0F, 11.0F, 12.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -5.0F, -19.2103F, 1.5708F, 0.0F, 0.0F));

        ModelPartData bone76 = bone74.addChild("bone76", ModelPartBuilder.create(), ModelTransform.of(-3.6882F, 0.0F, -13.7644F, 0.0F, 0.2618F, 0.0F));

        ModelPartData cube_r58 = bone76.addChild("cube_r58", ModelPartBuilder.create().uv(124, 44).cuboid(-10.0F, -7.0F, 0.5F, 20.0F, 11.0F, 11.0F, new Dilation(0.25F))
                .uv(124, 0).cuboid(-10.0F, -7.0F, 0.5F, 20.0F, 11.0F, 11.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -5.0F, -19.2103F, 1.5708F, 0.0F, 0.0F));

        ModelPartData bone59 = ring3.addChild("bone59", ModelPartBuilder.create(), ModelTransform.of(0.0F, -7.0F, 0.0F, 0.0F, 0.5236F, 0.0F));

        ModelPartData bone60 = bone59.addChild("bone60", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, -15.0F));

        ModelPartData cube_r59 = bone60.addChild("cube_r59", ModelPartBuilder.create().uv(292, 240).cuboid(-1.0F, -8.0F, 0.0F, 2.0F, 11.0F, 12.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -5.0F, -19.2103F, 1.5708F, 0.0F, 0.0F));

        ModelPartData bone61 = bone59.addChild("bone61", ModelPartBuilder.create(), ModelTransform.of(-3.6882F, 0.0F, -13.7644F, 0.0F, 0.2618F, 0.0F));

        ModelPartData cube_r60 = bone61.addChild("cube_r60", ModelPartBuilder.create().uv(0, 148).cuboid(-10.0F, -7.0F, 0.5F, 20.0F, 11.0F, 11.0F, new Dilation(0.25F))
                .uv(124, 0).cuboid(-10.0F, -7.0F, 0.5F, 20.0F, 11.0F, 11.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -5.0F, -19.2103F, 1.5708F, 0.0F, 0.0F));

        ModelPartData bone77 = ring3.addChild("bone77", ModelPartBuilder.create(), ModelTransform.of(0.0F, -7.0F, 0.0F, -3.1416F, -0.5236F, 3.1416F));

        ModelPartData bone78 = bone77.addChild("bone78", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, -15.0F));

        ModelPartData cube_r61 = bone78.addChild("cube_r61", ModelPartBuilder.create().uv(292, 240).cuboid(-1.0F, -8.0F, 0.0F, 2.0F, 11.0F, 12.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -5.0F, -19.2103F, 1.5708F, 0.0F, 0.0F));

        ModelPartData bone79 = bone77.addChild("bone79", ModelPartBuilder.create(), ModelTransform.of(-3.6882F, 0.0F, -13.7644F, 0.0F, 0.2618F, 0.0F));

        ModelPartData cube_r62 = bone79.addChild("cube_r62", ModelPartBuilder.create().uv(124, 66).cuboid(-10.0F, -7.0F, 0.5F, 20.0F, 11.0F, 11.0F, new Dilation(0.25F))
                .uv(124, 0).cuboid(-10.0F, -7.0F, 0.5F, 20.0F, 11.0F, 11.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -5.0F, -19.2103F, 1.5708F, 0.0F, 0.0F));

        ModelPartData bone62 = ring3.addChild("bone62", ModelPartBuilder.create(), ModelTransform.of(0.0F, -7.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData bone63 = bone62.addChild("bone63", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, -15.0F));

        ModelPartData cube_r63 = bone63.addChild("cube_r63", ModelPartBuilder.create().uv(292, 240).cuboid(-1.0F, -8.0F, 0.0F, 2.0F, 11.0F, 12.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -5.0F, -19.2103F, 1.5708F, 0.0F, 0.0F));

        ModelPartData bone64 = bone62.addChild("bone64", ModelPartBuilder.create(), ModelTransform.of(-3.6882F, 0.0F, -13.7644F, 0.0F, 0.2618F, 0.0F));

        ModelPartData cube_r64 = bone64.addChild("cube_r64", ModelPartBuilder.create().uv(62, 148).cuboid(-10.0F, -7.0F, 0.5F, 20.0F, 11.0F, 11.0F, new Dilation(0.25F))
                .uv(124, 0).cuboid(-10.0F, -7.0F, 0.5F, 20.0F, 11.0F, 11.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -5.0F, -19.2103F, 1.5708F, 0.0F, 0.0F));

        ModelPartData bone80 = ring3.addChild("bone80", ModelPartBuilder.create(), ModelTransform.of(0.0F, -7.0F, 0.0F, -3.1416F, -1.0472F, 3.1416F));

        ModelPartData bone81 = bone80.addChild("bone81", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, -15.0F));

        ModelPartData cube_r65 = bone81.addChild("cube_r65", ModelPartBuilder.create().uv(292, 240).cuboid(-1.0F, -8.0F, 0.0F, 2.0F, 11.0F, 12.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -5.0F, -19.2103F, 1.5708F, 0.0F, 0.0F));

        ModelPartData bone82 = bone80.addChild("bone82", ModelPartBuilder.create(), ModelTransform.of(-3.6882F, 0.0F, -13.7644F, 0.0F, 0.2618F, 0.0F));

        ModelPartData cube_r66 = bone82.addChild("cube_r66", ModelPartBuilder.create().uv(124, 88).cuboid(-10.0F, -7.0F, 0.5F, 20.0F, 11.0F, 11.0F, new Dilation(0.25F))
                .uv(124, 0).cuboid(-10.0F, -7.0F, 0.5F, 20.0F, 11.0F, 11.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -5.0F, -19.2103F, 1.5708F, 0.0F, 0.0F));

        ModelPartData bone65 = ring3.addChild("bone65", ModelPartBuilder.create(), ModelTransform.of(0.0F, -7.0F, 0.0F, 0.0F, 1.5708F, 0.0F));

        ModelPartData bone66 = bone65.addChild("bone66", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, -15.0F));

        ModelPartData cube_r67 = bone66.addChild("cube_r67", ModelPartBuilder.create().uv(292, 240).cuboid(-1.0F, -8.0F, 0.0F, 2.0F, 11.0F, 12.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -5.0F, -19.2103F, 1.5708F, 0.0F, 0.0F));

        ModelPartData bone67 = bone65.addChild("bone67", ModelPartBuilder.create(), ModelTransform.of(-3.6882F, 0.0F, -13.7644F, 0.0F, 0.2618F, 0.0F));

        ModelPartData cube_r68 = bone67.addChild("cube_r68", ModelPartBuilder.create().uv(124, 154).cuboid(-10.0F, -7.0F, 0.5F, 20.0F, 11.0F, 11.0F, new Dilation(0.25F))
                .uv(124, 0).cuboid(-10.0F, -7.0F, 0.5F, 20.0F, 11.0F, 11.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -5.0F, -19.2103F, 1.5708F, 0.0F, 0.0F));

        ModelPartData bone83 = ring3.addChild("bone83", ModelPartBuilder.create(), ModelTransform.of(0.0F, -7.0F, 0.0F, 0.0F, -1.5708F, 0.0F));

        ModelPartData bone84 = bone83.addChild("bone84", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, -15.0F));

        ModelPartData cube_r69 = bone84.addChild("cube_r69", ModelPartBuilder.create().uv(292, 240).cuboid(-1.0F, -8.0F, 0.0F, 2.0F, 11.0F, 12.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -5.0F, -19.2103F, 1.5708F, 0.0F, 0.0F));

        ModelPartData bone85 = bone83.addChild("bone85", ModelPartBuilder.create(), ModelTransform.of(-3.6882F, 0.0F, -13.7644F, 0.0F, 0.2618F, 0.0F));

        ModelPartData cube_r70 = bone85.addChild("cube_r70", ModelPartBuilder.create().uv(124, 110).cuboid(-10.0F, -7.0F, 0.5F, 20.0F, 11.0F, 11.0F, new Dilation(0.25F))
                .uv(124, 0).cuboid(-10.0F, -7.0F, 0.5F, 20.0F, 11.0F, 11.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -5.0F, -19.2103F, 1.5708F, 0.0F, 0.0F));

        ModelPartData bone68 = ring3.addChild("bone68", ModelPartBuilder.create(), ModelTransform.of(0.0F, -7.0F, 0.0F, 3.1416F, 1.0472F, 3.1416F));

        ModelPartData bone69 = bone68.addChild("bone69", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, -15.0F));

        ModelPartData cube_r71 = bone69.addChild("cube_r71", ModelPartBuilder.create().uv(292, 240).cuboid(-1.0F, -8.0F, 0.0F, 2.0F, 11.0F, 12.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -5.0F, -19.2103F, 1.5708F, 0.0F, 0.0F));

        ModelPartData bone70 = bone68.addChild("bone70", ModelPartBuilder.create(), ModelTransform.of(-3.6882F, 0.0F, -13.7644F, 0.0F, 0.2618F, 0.0F));

        ModelPartData cube_r72 = bone70.addChild("cube_r72", ModelPartBuilder.create().uv(0, 170).cuboid(-10.0F, -7.0F, 0.5F, 20.0F, 11.0F, 11.0F, new Dilation(0.25F))
                .uv(124, 0).cuboid(-10.0F, -7.0F, 0.5F, 20.0F, 11.0F, 11.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -5.0F, -19.2103F, 1.5708F, 0.0F, 0.0F));

        ModelPartData bone86 = ring3.addChild("bone86", ModelPartBuilder.create(), ModelTransform.of(0.0F, -7.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone87 = bone86.addChild("bone87", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, -15.0F));

        ModelPartData cube_r73 = bone87.addChild("cube_r73", ModelPartBuilder.create().uv(292, 240).cuboid(-1.0F, -8.0F, 0.0F, 2.0F, 11.0F, 12.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -5.0F, -19.2103F, 1.5708F, 0.0F, 0.0F));

        ModelPartData bone88 = bone86.addChild("bone88", ModelPartBuilder.create(), ModelTransform.of(-3.6882F, 0.0F, -13.7644F, 0.0F, 0.2618F, 0.0F));

        ModelPartData cube_r74 = bone88.addChild("cube_r74", ModelPartBuilder.create().uv(0, 126).cuboid(-10.0F, -7.0F, 0.5F, 20.0F, 11.0F, 11.0F, new Dilation(0.25F))
                .uv(124, 0).cuboid(-10.0F, -7.0F, 0.5F, 20.0F, 11.0F, 11.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -5.0F, -19.2103F, 1.5708F, 0.0F, 0.0F));

        ModelPartData bone71 = ring3.addChild("bone71", ModelPartBuilder.create(), ModelTransform.of(0.0F, -7.0F, 0.0F, 3.1416F, 0.5236F, 3.1416F));

        ModelPartData bone72 = bone71.addChild("bone72", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, -15.0F));

        ModelPartData cube_r75 = bone72.addChild("cube_r75", ModelPartBuilder.create().uv(292, 240).cuboid(-1.0F, -8.0F, 0.0F, 2.0F, 11.0F, 12.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -5.0F, -19.2103F, 1.5708F, 0.0F, 0.0F));

        ModelPartData bone73 = bone71.addChild("bone73", ModelPartBuilder.create(), ModelTransform.of(-3.6882F, 0.0F, -13.7644F, 0.0F, 0.2618F, 0.0F));

        ModelPartData cube_r76 = bone73.addChild("cube_r76", ModelPartBuilder.create().uv(62, 170).cuboid(-10.0F, -7.0F, 0.5F, 20.0F, 11.0F, 11.0F, new Dilation(0.25F))
                .uv(124, 0).cuboid(-10.0F, -7.0F, 0.5F, 20.0F, 11.0F, 11.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -5.0F, -19.2103F, 1.5708F, 0.0F, 0.0F));

        ModelPartData bone89 = ring3.addChild("bone89", ModelPartBuilder.create(), ModelTransform.of(0.0F, -7.0F, 0.0F, 0.0F, -0.5236F, 0.0F));

        ModelPartData bone90 = bone89.addChild("bone90", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, -15.0F));

        ModelPartData cube_r77 = bone90.addChild("cube_r77", ModelPartBuilder.create().uv(292, 240).cuboid(-1.0F, -8.0F, 0.0F, 2.0F, 11.0F, 12.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -5.0F, -19.2103F, 1.5708F, 0.0F, 0.0F));

        ModelPartData bone91 = bone89.addChild("bone91", ModelPartBuilder.create(), ModelTransform.of(-3.6882F, 0.0F, -13.7644F, 0.0F, 0.2618F, 0.0F));

        ModelPartData cube_r78 = bone91.addChild("cube_r78", ModelPartBuilder.create().uv(62, 126).cuboid(-10.0F, -7.0F, 0.5F, 20.0F, 11.0F, 11.0F, new Dilation(0.25F))
                .uv(124, 0).cuboid(-10.0F, -7.0F, 0.5F, 20.0F, 11.0F, 11.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -5.0F, -19.2103F, 1.5708F, 0.0F, 0.0F));

        ModelPartData ring2 = whirlagig.addChild("ring2", ModelPartBuilder.create(), ModelTransform.of(0.0F, -4.0F, 0.0F, 0.0F, 0.2618F, 0.0F));

        ModelPartData bone8 = ring2.addChild("bone8", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, -7.0F, 0.0F));

        ModelPartData bone9 = bone8.addChild("bone9", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, -8.0F));

        ModelPartData cube_r79 = bone9.addChild("cube_r79", ModelPartBuilder.create().uv(140, 305).cuboid(-1.0F, -8.0F, 0.0F, 2.0F, 11.0F, 12.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -5.0F, -19.2102F, 1.5708F, 0.0F, 0.0F));

        ModelPartData bone10 = bone8.addChild("bone10", ModelPartBuilder.create(), ModelTransform.of(-1.9411F, 0.0F, -7.2444F, 0.0F, 0.2618F, 0.0F));

        ModelPartData cube_r80 = bone10.addChild("cube_r80", ModelPartBuilder.create().uv(216, 220).cuboid(-8.0F, -7.0F, 0.5F, 16.0F, 11.0F, 11.0F, new Dilation(0.25F))
                .uv(124, 176).cuboid(-8.0F, -7.0F, 0.5F, 16.0F, 11.0F, 11.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -5.0F, -19.2103F, 1.5708F, 0.0F, 0.0F));

        ModelPartData bone92 = ring2.addChild("bone92", ModelPartBuilder.create(), ModelTransform.of(0.0F, -7.0F, 0.0F, -3.1416F, 0.0F, 3.1416F));

        ModelPartData bone93 = bone92.addChild("bone93", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, -8.0F));

        ModelPartData cube_r81 = bone93.addChild("cube_r81", ModelPartBuilder.create().uv(140, 305).cuboid(-1.0F, -8.0F, 0.0F, 2.0F, 11.0F, 12.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -5.0F, -19.2102F, 1.5708F, 0.0F, 0.0F));

        ModelPartData bone94 = bone92.addChild("bone94", ModelPartBuilder.create(), ModelTransform.of(-1.9411F, 0.0F, -7.2444F, 0.0F, 0.2618F, 0.0F));

        ModelPartData cube_r82 = bone94.addChild("cube_r82", ModelPartBuilder.create().uv(162, 198).cuboid(-8.0F, -7.0F, 0.5F, 16.0F, 11.0F, 11.0F, new Dilation(0.25F))
                .uv(124, 176).cuboid(-8.0F, -7.0F, 0.5F, 16.0F, 11.0F, 11.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -5.0F, -19.2103F, 1.5708F, 0.0F, 0.0F));

        ModelPartData bone44 = ring2.addChild("bone44", ModelPartBuilder.create(), ModelTransform.of(0.0F, -7.0F, 0.0F, 0.0F, 0.5236F, 0.0F));

        ModelPartData bone45 = bone44.addChild("bone45", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, -8.0F));

        ModelPartData cube_r83 = bone45.addChild("cube_r83", ModelPartBuilder.create().uv(140, 305).cuboid(-1.0F, -8.0F, 0.0F, 2.0F, 11.0F, 12.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -5.0F, -19.2102F, 1.5708F, 0.0F, 0.0F));

        ModelPartData bone46 = bone44.addChild("bone46", ModelPartBuilder.create(), ModelTransform.of(-1.9411F, 0.0F, -7.2444F, 0.0F, 0.2618F, 0.0F));

        ModelPartData cube_r84 = bone46.addChild("cube_r84", ModelPartBuilder.create().uv(232, 176).cuboid(-8.0F, -7.0F, 0.5F, 16.0F, 11.0F, 11.0F, new Dilation(0.25F))
                .uv(124, 176).cuboid(-8.0F, -7.0F, 0.5F, 16.0F, 11.0F, 11.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -5.0F, -19.2103F, 1.5708F, 0.0F, 0.0F));

        ModelPartData bone95 = ring2.addChild("bone95", ModelPartBuilder.create(), ModelTransform.of(0.0F, -7.0F, 0.0F, -3.1416F, -0.5236F, 3.1416F));

        ModelPartData bone96 = bone95.addChild("bone96", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, -8.0F));

        ModelPartData cube_r85 = bone96.addChild("cube_r85", ModelPartBuilder.create().uv(140, 305).cuboid(-1.0F, -8.0F, 0.0F, 2.0F, 11.0F, 12.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -5.0F, -19.2102F, 1.5708F, 0.0F, 0.0F));

        ModelPartData bone97 = bone95.addChild("bone97", ModelPartBuilder.create(), ModelTransform.of(-1.9411F, 0.0F, -7.2444F, 0.0F, 0.2618F, 0.0F));

        ModelPartData cube_r86 = bone97.addChild("cube_r86", ModelPartBuilder.create().uv(0, 214).cuboid(-8.0F, -7.0F, 0.5F, 16.0F, 11.0F, 11.0F, new Dilation(0.25F))
                .uv(124, 176).cuboid(-8.0F, -7.0F, 0.5F, 16.0F, 11.0F, 11.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -5.0F, -19.2103F, 1.5708F, 0.0F, 0.0F));

        ModelPartData bone47 = ring2.addChild("bone47", ModelPartBuilder.create(), ModelTransform.of(0.0F, -7.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData bone48 = bone47.addChild("bone48", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, -8.0F));

        ModelPartData cube_r87 = bone48.addChild("cube_r87", ModelPartBuilder.create().uv(140, 305).cuboid(-1.0F, -8.0F, 0.0F, 2.0F, 11.0F, 12.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -5.0F, -19.2102F, 1.5708F, 0.0F, 0.0F));

        ModelPartData bone49 = bone47.addChild("bone49", ModelPartBuilder.create(), ModelTransform.of(-1.9411F, 0.0F, -7.2444F, 0.0F, 0.2618F, 0.0F));

        ModelPartData cube_r88 = bone49.addChild("cube_r88", ModelPartBuilder.create().uv(0, 236).cuboid(-8.0F, -7.0F, 0.5F, 16.0F, 11.0F, 11.0F, new Dilation(0.25F))
                .uv(124, 176).cuboid(-8.0F, -7.0F, 0.5F, 16.0F, 11.0F, 11.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -5.0F, -19.2103F, 1.5708F, 0.0F, 0.0F));

        ModelPartData bone98 = ring2.addChild("bone98", ModelPartBuilder.create(), ModelTransform.of(0.0F, -7.0F, 0.0F, -3.1416F, -1.0472F, 3.1416F));

        ModelPartData bone99 = bone98.addChild("bone99", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, -8.0F));

        ModelPartData cube_r89 = bone99.addChild("cube_r89", ModelPartBuilder.create().uv(140, 305).cuboid(-1.0F, -8.0F, 0.0F, 2.0F, 11.0F, 12.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -5.0F, -19.2102F, 1.5708F, 0.0F, 0.0F));

        ModelPartData bone100 = bone98.addChild("bone100", ModelPartBuilder.create(), ModelTransform.of(-1.9411F, 0.0F, -7.2444F, 0.0F, 0.2618F, 0.0F));

        ModelPartData cube_r90 = bone100.addChild("cube_r90", ModelPartBuilder.create().uv(54, 214).cuboid(-8.0F, -7.0F, 0.5F, 16.0F, 11.0F, 11.0F, new Dilation(0.25F))
                .uv(124, 176).cuboid(-8.0F, -7.0F, 0.5F, 16.0F, 11.0F, 11.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -5.0F, -19.2103F, 1.5708F, 0.0F, 0.0F));

        ModelPartData bone50 = ring2.addChild("bone50", ModelPartBuilder.create(), ModelTransform.of(0.0F, -7.0F, 0.0F, 0.0F, 1.5708F, 0.0F));

        ModelPartData bone51 = bone50.addChild("bone51", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, -8.0F));

        ModelPartData cube_r91 = bone51.addChild("cube_r91", ModelPartBuilder.create().uv(140, 305).cuboid(-1.0F, -8.0F, 0.0F, 2.0F, 11.0F, 12.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -5.0F, -19.2102F, 1.5708F, 0.0F, 0.0F));

        ModelPartData bone52 = bone50.addChild("bone52", ModelPartBuilder.create(), ModelTransform.of(-1.9411F, 0.0F, -7.2444F, 0.0F, 0.2618F, 0.0F));

        ModelPartData cube_r92 = bone52.addChild("cube_r92", ModelPartBuilder.create().uv(54, 236).cuboid(-8.0F, -7.0F, 0.5F, 16.0F, 11.0F, 11.0F, new Dilation(0.25F))
                .uv(124, 176).cuboid(-8.0F, -7.0F, 0.5F, 16.0F, 11.0F, 11.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -5.0F, -19.2103F, 1.5708F, 0.0F, 0.0F));

        ModelPartData bone101 = ring2.addChild("bone101", ModelPartBuilder.create(), ModelTransform.of(0.0F, -7.0F, 0.0F, 0.0F, -1.5708F, 0.0F));

        ModelPartData bone102 = bone101.addChild("bone102", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, -8.0F));

        ModelPartData cube_r93 = bone102.addChild("cube_r93", ModelPartBuilder.create().uv(140, 305).cuboid(-1.0F, -8.0F, 0.0F, 2.0F, 11.0F, 12.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -5.0F, -19.2102F, 1.5708F, 0.0F, 0.0F));

        ModelPartData bone103 = bone101.addChild("bone103", ModelPartBuilder.create(), ModelTransform.of(-1.9411F, 0.0F, -7.2444F, 0.0F, 0.2618F, 0.0F));

        ModelPartData cube_r94 = bone103.addChild("cube_r94", ModelPartBuilder.create().uv(216, 198).cuboid(-8.0F, -7.0F, 0.5F, 16.0F, 11.0F, 11.0F, new Dilation(0.25F))
                .uv(124, 176).cuboid(-8.0F, -7.0F, 0.5F, 16.0F, 11.0F, 11.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -5.0F, -19.2103F, 1.5708F, 0.0F, 0.0F));

        ModelPartData bone53 = ring2.addChild("bone53", ModelPartBuilder.create(), ModelTransform.of(0.0F, -7.0F, 0.0F, -3.1416F, 1.0472F, 3.1416F));

        ModelPartData bone54 = bone53.addChild("bone54", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, -8.0F));

        ModelPartData cube_r95 = bone54.addChild("cube_r95", ModelPartBuilder.create().uv(140, 305).cuboid(-1.0F, -8.0F, 0.0F, 2.0F, 11.0F, 12.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -5.0F, -19.2102F, 1.5708F, 0.0F, 0.0F));

        ModelPartData bone55 = bone53.addChild("bone55", ModelPartBuilder.create(), ModelTransform.of(-1.9411F, 0.0F, -7.2444F, 0.0F, 0.2618F, 0.0F));

        ModelPartData cube_r96 = bone55.addChild("cube_r96", ModelPartBuilder.create().uv(240, 0).cuboid(-8.0F, -7.0F, 0.5F, 16.0F, 11.0F, 11.0F, new Dilation(0.25F))
                .uv(124, 176).cuboid(-8.0F, -7.0F, 0.5F, 16.0F, 11.0F, 11.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -5.0F, -19.2103F, 1.5708F, 0.0F, 0.0F));

        ModelPartData bone104 = ring2.addChild("bone104", ModelPartBuilder.create(), ModelTransform.of(0.0F, -7.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone105 = bone104.addChild("bone105", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, -8.0F));

        ModelPartData cube_r97 = bone105.addChild("cube_r97", ModelPartBuilder.create().uv(140, 305).cuboid(-1.0F, -8.0F, 0.0F, 2.0F, 11.0F, 12.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -5.0F, -19.2102F, 1.5708F, 0.0F, 0.0F));

        ModelPartData bone106 = bone104.addChild("bone106", ModelPartBuilder.create(), ModelTransform.of(-1.9411F, 0.0F, -7.2444F, 0.0F, 0.2618F, 0.0F));

        ModelPartData cube_r98 = bone106.addChild("cube_r98", ModelPartBuilder.create().uv(108, 220).cuboid(-8.0F, -7.0F, 0.5F, 16.0F, 11.0F, 11.0F, new Dilation(0.25F))
                .uv(124, 176).cuboid(-8.0F, -7.0F, 0.5F, 16.0F, 11.0F, 11.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -5.0F, -19.2103F, 1.5708F, 0.0F, 0.0F));

        ModelPartData bone56 = ring2.addChild("bone56", ModelPartBuilder.create(), ModelTransform.of(0.0F, -7.0F, 0.0F, -3.1416F, 0.5236F, 3.1416F));

        ModelPartData bone57 = bone56.addChild("bone57", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, -8.0F));

        ModelPartData cube_r99 = bone57.addChild("cube_r99", ModelPartBuilder.create().uv(140, 305).cuboid(-1.0F, -8.0F, 0.0F, 2.0F, 11.0F, 12.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -5.0F, -19.2102F, 1.5708F, 0.0F, 0.0F));

        ModelPartData bone58 = bone56.addChild("bone58", ModelPartBuilder.create(), ModelTransform.of(-1.9411F, 0.0F, -7.2444F, 0.0F, 0.2618F, 0.0F));

        ModelPartData cube_r100 = bone58.addChild("cube_r100", ModelPartBuilder.create().uv(108, 198).cuboid(-8.0F, -7.0F, 0.5F, 16.0F, 11.0F, 11.0F, new Dilation(0.25F))
                .uv(124, 176).cuboid(-8.0F, -7.0F, 0.5F, 16.0F, 11.0F, 11.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -5.0F, -19.2103F, 1.5708F, 0.0F, 0.0F));

        ModelPartData bone107 = ring2.addChild("bone107", ModelPartBuilder.create(), ModelTransform.of(0.0F, -7.0F, 0.0F, 0.0F, -0.5236F, 0.0F));

        ModelPartData bone108 = bone107.addChild("bone108", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, -8.0F));

        ModelPartData cube_r101 = bone108.addChild("cube_r101", ModelPartBuilder.create().uv(140, 305).cuboid(-1.0F, -8.0F, 0.0F, 2.0F, 11.0F, 12.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -5.0F, -19.2102F, 1.5708F, 0.0F, 0.0F));

        ModelPartData bone109 = bone107.addChild("bone109", ModelPartBuilder.create(), ModelTransform.of(-1.9411F, 0.0F, -7.2444F, 0.0F, 0.2618F, 0.0F));

        ModelPartData cube_r102 = bone109.addChild("cube_r102", ModelPartBuilder.create().uv(162, 220).cuboid(-8.0F, -7.0F, 0.5F, 16.0F, 11.0F, 11.0F, new Dilation(0.25F))
                .uv(124, 176).cuboid(-8.0F, -7.0F, 0.5F, 16.0F, 11.0F, 11.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -5.0F, -19.2103F, 1.5708F, 0.0F, 0.0F));
        return TexturedModelData.of(modelData, 512, 512);
    }

    @Override
    public Animation getAnimationForState(TravelHandlerBase.State state) {
        return switch(state) {
            case DEMAT -> ToyotaSpinningRotorAnimation.STOP;
            case MAT -> ToyotaSpinningRotorAnimation.START_UP;
            case FLIGHT -> ToyotaSpinningRotorAnimation.FLIGHT;
            default -> Animation.Builder.create(0).build();
        };
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
        whirlagig.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
    }

    @Override
    public void renderWithAnimations(ToyotaSpinningRotorBlockEntity console, ModelPart root, MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float pAlpha) {
        super.renderWithAnimations(console, root, matrices, vertices, light, overlay, red, green, blue, pAlpha);
    }

    @Override
    public ModelPart getPart() {
        return whirlagig;
    }

}