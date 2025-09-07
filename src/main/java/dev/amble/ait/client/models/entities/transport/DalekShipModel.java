package dev.amble.ait.client.models.entities.transport;

import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;

public class DalekShipModel extends SinglePartEntityModel {
    private final ModelPart ship;
    public DalekShipModel(ModelPart root) {
        this.ship = root.getChild("ship");
    }
    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData ship = modelPartData.addChild("ship", ModelPartBuilder.create(), ModelTransform.of(0.0F, 143.0F, 0.0F, -3.1416F, 0.0F, 3.1416F));

        ModelPartData sidetest = ship.addChild("sidetest", ModelPartBuilder.create(), ModelTransform.pivot(-10.5F, -48.0F, 91.5025F));

        ModelPartData cube_r1 = sidetest.addChild("cube_r1", ModelPartBuilder.create().uv(757, 440).cuboid(-35.75F, 6.5F, 0.0F, 71.0F, 6.0F, 2.0F, new Dilation(0.0F))
        .uv(721, 144).cuboid(-36.75F, -12.5F, 0.0F, 73.0F, 6.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(10.25F, -144.4436F, -222.5553F, 2.8798F, 0.0F, 3.1416F));

        ModelPartData cube_r2 = sidetest.addChild("cube_r2", ModelPartBuilder.create().uv(721, 144).mirrored().cuboid(-36.25F, -12.5F, 0.0F, 73.0F, 6.0F, 2.0F, new Dilation(0.0F)).mirrored(false)
        .uv(757, 440).mirrored().cuboid(-35.25F, 6.5F, 0.0F, 71.0F, 6.0F, 2.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(-102.87F, -144.4436F, -157.2454F, 2.8798F, -1.0472F, 3.1416F));

        ModelPartData cube_r3 = sidetest.addChild("cube_r3", ModelPartBuilder.create().uv(721, 144).mirrored().cuboid(-36.25F, -12.5F, 0.0F, 73.0F, 6.0F, 2.0F, new Dilation(0.0F)).mirrored(false)
        .uv(757, 440).mirrored().cuboid(-35.25F, 6.5F, 0.0F, 71.0F, 6.0F, 2.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(-54.8099F, -144.4436F, -205.1225F, 2.8798F, -0.5236F, -3.1416F));

        ModelPartData cube_r4 = sidetest.addChild("cube_r4", ModelPartBuilder.create().uv(757, 440).mirrored().cuboid(-35.25F, 6.5F, 0.0F, 71.0F, 6.0F, 2.0F, new Dilation(0.0F)).mirrored(false)
        .uv(721, 144).mirrored().cuboid(-36.25F, -12.5F, 0.0F, 73.0F, 6.0F, 2.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(-120.5528F, -144.4436F, -91.7525F, 0.0F, -1.5708F, -0.2618F));

        ModelPartData cube_r5 = sidetest.addChild("cube_r5", ModelPartBuilder.create().uv(757, 440).mirrored().cuboid(-35.25F, 6.5F, 0.0F, 71.0F, 6.0F, 2.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(-103.12F, -144.4436F, -26.1926F, -0.2618F, -1.0472F, 0.0F));

        ModelPartData cube_r6 = sidetest.addChild("cube_r6", ModelPartBuilder.create().uv(721, 144).mirrored().cuboid(-36.25F, -12.5F, 0.0F, 73.0F, 6.0F, 2.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(-103.12F, -144.4436F, -26.1926F, -0.2618F, -1.0472F, 0.0F));

        ModelPartData cube_r7 = sidetest.addChild("cube_r7", ModelPartBuilder.create().uv(757, 440).mirrored().cuboid(-35.25F, 6.5F, 0.0F, 71.0F, 6.0F, 2.0F, new Dilation(0.0F)).mirrored(false)
        .uv(721, 144).mirrored().cuboid(-36.25F, -12.5F, 0.0F, 73.0F, 6.0F, 2.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(-55.2429F, -144.4436F, 21.8675F, -0.2618F, -0.5236F, 0.0F));

        ModelPartData cube_r8 = sidetest.addChild("cube_r8", ModelPartBuilder.create().uv(757, 440).cuboid(-35.75F, 6.5F, 0.0F, 71.0F, 6.0F, 2.0F, new Dilation(0.0F))
        .uv(721, 144).cuboid(-36.75F, -12.5F, 0.0F, 73.0F, 6.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(75.8099F, -144.4436F, -205.1225F, 2.8798F, 0.5236F, 3.1416F));

        ModelPartData cube_r9 = sidetest.addChild("cube_r9", ModelPartBuilder.create().uv(757, 440).cuboid(-35.75F, 6.5F, 0.0F, 71.0F, 6.0F, 2.0F, new Dilation(0.0F))
        .uv(721, 144).cuboid(-36.75F, -12.5F, 0.0F, 73.0F, 6.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(123.87F, -144.4436F, -157.2454F, 2.8798F, 1.0472F, -3.1416F));

        ModelPartData cube_r10 = sidetest.addChild("cube_r10", ModelPartBuilder.create().uv(721, 144).cuboid(-36.75F, -12.5F, 0.0F, 73.0F, 6.0F, 2.0F, new Dilation(0.0F))
        .uv(757, 440).cuboid(-35.75F, 6.5F, 0.0F, 71.0F, 6.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(141.5528F, -144.4436F, -91.7525F, 0.0F, 1.5708F, 0.2618F));

        ModelPartData cube_r11 = sidetest.addChild("cube_r11", ModelPartBuilder.create().uv(721, 144).cuboid(-36.75F, -12.5F, 0.0F, 73.0F, 6.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(124.12F, -144.4436F, -26.1926F, -0.2618F, 1.0472F, 0.0F));

        ModelPartData cube_r12 = sidetest.addChild("cube_r12", ModelPartBuilder.create().uv(757, 440).cuboid(-35.75F, 6.5F, 0.0F, 71.0F, 6.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(124.12F, -144.4436F, -26.1926F, -0.2618F, 1.0472F, 0.0F));

        ModelPartData cube_r13 = sidetest.addChild("cube_r13", ModelPartBuilder.create().uv(721, 144).cuboid(-36.75F, -12.5F, 0.0F, 73.0F, 6.0F, 2.0F, new Dilation(0.0F))
        .uv(757, 440).cuboid(-35.75F, 6.5F, 0.0F, 71.0F, 6.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(76.2429F, -144.4436F, 21.8675F, -0.2618F, 0.5236F, 0.0F));

        ModelPartData cube_r14 = sidetest.addChild("cube_r14", ModelPartBuilder.create().uv(757, 440).cuboid(-35.75F, 6.5F, 0.0F, 71.0F, 6.0F, 2.0F, new Dilation(0.0F))
        .uv(721, 144).cuboid(-36.75F, -12.5F, 0.0F, 73.0F, 6.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(10.75F, -144.4436F, 39.5503F, -0.2618F, 0.0F, 0.0F));

        ModelPartData roofbase = sidetest.addChild("roofbase", ModelPartBuilder.create(), ModelTransform.pivot(10.5F, -170.9514F, -91.5025F));

        ModelPartData cube_r15 = roofbase.addChild("cube_r15", ModelPartBuilder.create().uv(753, 474).cuboid(-35.5F, -84.2223F, -5.75F, 71.0F, 37.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 53.4732F, 0.0F, 1.4835F, 0.5236F, 0.0F));

        ModelPartData cube_r16 = roofbase.addChild("cube_r16", ModelPartBuilder.create().uv(755, 512).cuboid(-35.5F, -84.2223F, 5.75F, 71.0F, 37.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 53.4732F, 0.0F, -1.4835F, 0.5236F, 0.0F));

        ModelPartData cube_r17 = roofbase.addChild("cube_r17", ModelPartBuilder.create().uv(753, 474).cuboid(-35.5F, -84.2223F, -5.75F, 71.0F, 37.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 53.4732F, 0.0F, 0.0F, 1.5708F, -1.4835F));

        ModelPartData cube_r18 = roofbase.addChild("cube_r18", ModelPartBuilder.create().uv(755, 512).cuboid(-35.5F, -84.2223F, 5.75F, 71.0F, 37.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 53.4732F, 0.0F, 0.0F, 1.5708F, 1.4835F));

        ModelPartData cube_r19 = roofbase.addChild("cube_r19", ModelPartBuilder.create().uv(755, 512).cuboid(-35.5F, -83.8348F, 10.1781F, 71.0F, 37.0F, 0.0F, new Dilation(0.0F))
        .uv(743, 643).cuboid(-35.5F, -83.8348F, 9.5781F, 71.0F, 37.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 49.0282F, 0.0F, 1.6581F, 0.5236F, -3.1416F));

        ModelPartData cube_r20 = roofbase.addChild("cube_r20", ModelPartBuilder.create().uv(753, 474).cuboid(-35.5F, -83.8348F, -10.1781F, 71.0F, 37.0F, 0.0F, new Dilation(0.0F))
        .uv(743, 605).cuboid(-35.5F, -83.8348F, -9.5781F, 71.0F, 37.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 49.0282F, 0.0F, -1.6581F, 0.5236F, 3.1416F));

        ModelPartData cube_r21 = roofbase.addChild("cube_r21", ModelPartBuilder.create().uv(847, 858).cuboid(-18.5F, -98.898F, 22.9903F, 36.0F, 18.0F, 0.0F, new Dilation(0.0F))
        .uv(0, 729).cuboid(-35.5F, -127.898F, 23.9903F, 71.0F, 47.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 49.0282F, 0.0F, 1.8326F, 0.5236F, 3.1416F));

        ModelPartData cube_r22 = roofbase.addChild("cube_r22", ModelPartBuilder.create().uv(721, 96).cuboid(-35.5F, -127.898F, -23.9903F, 71.0F, 47.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 49.0282F, 0.0F, -1.8326F, 0.5236F, 3.1416F));

        ModelPartData cube_r23 = roofbase.addChild("cube_r23", ModelPartBuilder.create().uv(681, 181).cuboid(-36.5F, -128.0363F, -24.2242F, 73.0F, 47.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 49.4176F, -0.0388F, 1.309F, -1.0472F, 0.0F));

        ModelPartData cube_r24 = roofbase.addChild("cube_r24", ModelPartBuilder.create().uv(584, 729).cuboid(-36.5F, -83.9075F, -9.3935F, 73.0F, 37.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 49.4176F, -0.0388F, 1.4835F, -1.0472F, 0.0F));

        ModelPartData cube_r25 = roofbase.addChild("cube_r25", ModelPartBuilder.create().uv(294, 681).cuboid(-36.5F, -127.9613F, 24.5041F, 73.0F, 47.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 49.4176F, -0.0388F, -1.309F, -1.0472F, 0.0F));

        ModelPartData cube_r26 = roofbase.addChild("cube_r26", ModelPartBuilder.create().uv(731, 681).cuboid(-36.5F, -83.8301F, 9.5868F, 73.0F, 37.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 49.4176F, -0.0388F, -1.4835F, -1.0472F, 0.0F));

        ModelPartData cube_r27 = roofbase.addChild("cube_r27", ModelPartBuilder.create().uv(681, 181).cuboid(-36.5F, -128.0363F, -24.2242F, 73.0F, 47.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 49.4176F, -0.0388F, -1.8326F, -1.0472F, 3.1416F));

        ModelPartData cube_r28 = roofbase.addChild("cube_r28", ModelPartBuilder.create().uv(584, 729).cuboid(-36.5F, -83.9075F, -9.3935F, 73.0F, 37.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 49.4176F, -0.0388F, -1.6581F, -1.0472F, 3.1416F));

        ModelPartData cube_r29 = roofbase.addChild("cube_r29", ModelPartBuilder.create().uv(294, 681).cuboid(-36.5F, -127.9613F, 24.5041F, 73.0F, 47.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 49.4176F, -0.0388F, 1.8326F, -1.0472F, -3.1416F));

        ModelPartData cube_r30 = roofbase.addChild("cube_r30", ModelPartBuilder.create().uv(731, 681).cuboid(-36.5F, -83.8301F, 9.3868F, 73.0F, 37.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 49.4176F, -0.0388F, 1.6581F, -1.0472F, -3.1416F));

        ModelPartData cube_r31 = roofbase.addChild("cube_r31", ModelPartBuilder.create().uv(294, 681).cuboid(-36.5F, -127.898F, 24.8903F, 73.0F, 47.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 49.0282F, 0.0F, 1.8326F, 0.0F, -3.1416F));

        ModelPartData cube_r32 = roofbase.addChild("cube_r32", ModelPartBuilder.create().uv(731, 681).cuboid(-36.5F, -83.8348F, 9.7781F, 73.0F, 37.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 49.0282F, 0.0F, 1.6581F, 0.0F, -3.1416F));

        ModelPartData cube_r33 = roofbase.addChild("cube_r33", ModelPartBuilder.create().uv(584, 729).cuboid(-36.5F, -83.8348F, -9.7781F, 73.0F, 37.0F, 0.0F, new Dilation(0.0F))
        .uv(290, 729).cuboid(-36.5F, -84.8348F, -8.5781F, 73.0F, 37.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 49.0282F, 0.0F, -1.6581F, 0.0F, 3.1416F));

        ModelPartData cube_r34 = roofbase.addChild("cube_r34", ModelPartBuilder.create().uv(644, 277).cuboid(-32.5F, -127.898F, 23.6903F, 73.0F, 47.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(3.0F, 49.0282F, 0.0F, 1.8326F, 0.0F, 3.1416F));

        ModelPartData cube_r35 = roofbase.addChild("cube_r35", ModelPartBuilder.create().uv(437, 729).cuboid(-35.5F, -84.8348F, 9.2781F, 73.0F, 37.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(3.0F, 49.0282F, 0.0F, 1.6581F, 0.0F, -3.1416F));

        ModelPartData cube_r36 = roofbase.addChild("cube_r36", ModelPartBuilder.create().uv(743, 605).cuboid(-35.5F, -83.8348F, -9.5781F, 71.0F, 37.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 49.0282F, 0.0F, -1.6581F, -0.5236F, 3.1416F));

        ModelPartData cube_r37 = roofbase.addChild("cube_r37", ModelPartBuilder.create().uv(721, 48).cuboid(-35.5F, -127.898F, 23.9903F, 71.0F, 47.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 49.0282F, 0.0F, 1.8326F, -0.5236F, 3.1416F));

        ModelPartData cube_r38 = roofbase.addChild("cube_r38", ModelPartBuilder.create().uv(743, 643).cuboid(-35.5F, -83.8348F, 9.5781F, 71.0F, 37.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 49.0282F, 0.0F, 1.6581F, -0.5236F, -3.1416F));

        ModelPartData cube_r39 = roofbase.addChild("cube_r39", ModelPartBuilder.create().uv(721, 96).cuboid(-35.5F, -127.898F, -23.9903F, 71.0F, 47.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 49.0282F, 0.0F, -1.8326F, -0.5236F, 3.1416F));

        ModelPartData cube_r40 = roofbase.addChild("cube_r40", ModelPartBuilder.create().uv(735, 567).cuboid(-36.5F, -84.8348F, -9.1781F, 71.0F, 37.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 49.0282F, 0.0F, -1.6581F, -1.0472F, 3.1416F));

        ModelPartData cube_r41 = roofbase.addChild("cube_r41", ModelPartBuilder.create().uv(588, 681).cuboid(-34.5F, -127.898F, 23.6903F, 71.0F, 47.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 49.0282F, 0.0F, 1.8326F, -1.0472F, 3.1416F));

        ModelPartData cube_r42 = roofbase.addChild("cube_r42", ModelPartBuilder.create().uv(731, 719).cuboid(-34.5F, -84.8348F, 9.2781F, 71.0F, 37.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 49.0282F, 0.0F, 1.6581F, -1.0472F, -3.1416F));

        ModelPartData cube_r43 = roofbase.addChild("cube_r43", ModelPartBuilder.create().uv(721, 0).cuboid(-36.5F, -127.898F, -23.6903F, 71.0F, 47.0F, 0.0F, new Dilation(0.005F)), ModelTransform.of(0.0F, 49.0282F, 0.0F, -1.8326F, -1.0472F, 3.1416F));

        ModelPartData cube_r44 = roofbase.addChild("cube_r44", ModelPartBuilder.create().uv(606, 474).cuboid(-35.5F, -83.8348F, -9.5781F, 73.0F, 37.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 49.0282F, 0.0F, 0.0F, -1.5708F, 1.4835F));

        ModelPartData cube_r45 = roofbase.addChild("cube_r45", ModelPartBuilder.create().uv(147, 681).cuboid(-39.5F, -127.898F, 23.9903F, 73.0F, 47.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 49.0282F, 0.0F, 0.0F, -1.5708F, -1.309F));

        ModelPartData cube_r46 = roofbase.addChild("cube_r46", ModelPartBuilder.create().uv(143, 729).cuboid(-39.5F, -83.8348F, 9.5781F, 73.0F, 37.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 49.0282F, 0.0F, 0.0F, -1.5708F, -1.4835F));

        ModelPartData cube_r47 = roofbase.addChild("cube_r47", ModelPartBuilder.create().uv(644, 229).cuboid(-35.5F, -127.898F, -23.9903F, 73.0F, 47.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 49.0282F, 0.0F, 0.0F, -1.5708F, 1.309F));

        ModelPartData cube_r48 = roofbase.addChild("cube_r48", ModelPartBuilder.create().uv(552, 885).cuboid(-14.5F, -103.448F, -19.2143F, 14.0F, 15.0F, 0.0F, new Dilation(0.0F))
        .uv(721, 0).cuboid(-37.5F, -135.448F, -20.2143F, 71.0F, 47.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(-5.1038F, 54.0501F, 4.1014F, 1.309F, -1.0472F, 0.0F));

        ModelPartData cube_r49 = roofbase.addChild("cube_r49", ModelPartBuilder.create().uv(735, 567).cuboid(-37.5F, -91.6776F, -5.0087F, 71.0F, 37.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(-5.1038F, 53.5788F, 4.1014F, 1.4835F, -1.0472F, 0.0F));

        ModelPartData cube_r50 = roofbase.addChild("cube_r50", ModelPartBuilder.create().uv(731, 719).cuboid(-33.5F, -91.6776F, 5.3087F, 71.0F, 37.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(5.1038F, 53.5788F, -4.1014F, -1.4835F, -1.0472F, 0.0F));

        ModelPartData cube_r51 = roofbase.addChild("cube_r51", ModelPartBuilder.create().uv(588, 681).cuboid(-33.5F, -135.448F, 20.5143F, 71.0F, 47.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(5.1038F, 54.0501F, -4.1014F, -1.309F, -1.0472F, 0.0F));

        ModelPartData cube_r52 = roofbase.addChild("cube_r52", ModelPartBuilder.create().uv(606, 426).cuboid(-37.5F, 88.3968F, 25.3374F, 75.0F, 47.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 4.0615F, 0.0F, 0.0F, -1.5708F, 1.309F));

        ModelPartData cube_r53 = roofbase.addChild("cube_r53", ModelPartBuilder.create().uv(302, 519).cuboid(-37.5F, 32.3647F, 9.7853F, 75.0F, 56.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 4.0615F, 0.0F, 0.0F, -1.5708F, 1.4835F));

        ModelPartData cube_r54 = roofbase.addChild("cube_r54", ModelPartBuilder.create().uv(453, 519).cuboid(-37.5F, 32.3647F, -9.7853F, 75.0F, 56.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 4.0615F, 0.0F, 0.0F, -1.5708F, -1.4835F));

        ModelPartData cube_r55 = roofbase.addChild("cube_r55", ModelPartBuilder.create().uv(0, 633).cuboid(-37.5F, 88.3968F, -25.3374F, 75.0F, 47.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 4.0615F, 0.0F, 0.0F, -1.5708F, -1.309F));

        ModelPartData cube_r56 = roofbase.addChild("cube_r56", ModelPartBuilder.create().uv(606, 426).mirrored().cuboid(-37.5F, 88.3968F, 25.3374F, 75.0F, 47.0F, 0.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(0.0F, 4.0615F, 0.0F, 1.309F, -0.5236F, 0.0F));

        ModelPartData cube_r57 = roofbase.addChild("cube_r57", ModelPartBuilder.create().uv(302, 519).mirrored().cuboid(-37.5F, 32.3647F, 9.7853F, 75.0F, 56.0F, 0.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(0.0F, 4.0615F, 0.0F, 1.4835F, -0.5236F, 0.0F));

        ModelPartData cube_r58 = roofbase.addChild("cube_r58", ModelPartBuilder.create().uv(453, 519).cuboid(-37.5F, 32.3647F, -9.7853F, 75.0F, 56.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 4.0615F, 0.0F, -1.4835F, -0.5236F, 0.0F));

        ModelPartData cube_r59 = roofbase.addChild("cube_r59", ModelPartBuilder.create().uv(0, 633).cuboid(-37.5F, 88.3968F, -25.3374F, 75.0F, 47.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 4.0615F, 0.0F, -1.309F, -0.5236F, 0.0F));

        ModelPartData cube_r60 = roofbase.addChild("cube_r60", ModelPartBuilder.create().uv(0, 633).cuboid(-37.5F, 88.3968F, -25.3374F, 75.0F, 47.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 4.0615F, 0.0F, -1.309F, 0.5236F, 0.0F));

        ModelPartData cube_r61 = roofbase.addChild("cube_r61", ModelPartBuilder.create().uv(453, 519).cuboid(-37.5F, 32.3647F, -9.7853F, 75.0F, 56.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 4.0615F, 0.0F, -1.4835F, 0.5236F, 0.0F));

        ModelPartData cube_r62 = roofbase.addChild("cube_r62", ModelPartBuilder.create().uv(606, 426).mirrored().cuboid(-37.5F, 88.3968F, 25.3374F, 75.0F, 47.0F, 0.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(0.0F, 4.0615F, 0.0F, 1.309F, 0.5236F, 0.0F));

        ModelPartData cube_r63 = roofbase.addChild("cube_r63", ModelPartBuilder.create().uv(530, 181).cuboid(-37.5F, 88.448F, 20.8143F, 75.0F, 47.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 1.309F, 0.5236F, 0.0F));

        ModelPartData cube_r64 = roofbase.addChild("cube_r64", ModelPartBuilder.create().uv(530, 181).cuboid(-37.5F, 88.448F, 20.8143F, 75.0F, 47.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.5708F, -1.309F));

        ModelPartData cube_r65 = roofbase.addChild("cube_r65", ModelPartBuilder.create().uv(530, 181).mirrored().cuboid(-37.5F, 88.448F, 20.8143F, 75.0F, 47.0F, 0.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.5708F, 1.309F));

        ModelPartData cube_r66 = roofbase.addChild("cube_r66", ModelPartBuilder.create().uv(147, 371).cuboid(-5.5F, 87.2027F, -25.9619F, 11.0F, 47.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 4.4116F, 0.0F, 1.8326F, 0.0F, 3.1416F));

        ModelPartData cube_r67 = roofbase.addChild("cube_r67", ModelPartBuilder.create().uv(692, 767).cuboid(-5.5F, 43.3861F, -10.4249F, 11.0F, 47.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 4.4116F, 0.0F, 1.6581F, 0.0F, 3.1416F));

        ModelPartData cube_r68 = roofbase.addChild("cube_r68", ModelPartBuilder.create().uv(692, 767).mirrored().cuboid(-5.5F, 43.3861F, 9.4249F, 11.0F, 47.0F, 1.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(0.0F, 4.4116F, 0.0F, -1.6581F, 0.0F, -3.1416F));

        ModelPartData cube_r69 = roofbase.addChild("cube_r69", ModelPartBuilder.create().uv(147, 371).mirrored().cuboid(-5.5F, 87.2027F, 24.9619F, 11.0F, 47.0F, 1.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(0.0F, 4.4116F, 0.0F, -1.8326F, 0.0F, -3.1416F));

        ModelPartData cube_r70 = roofbase.addChild("cube_r70", ModelPartBuilder.create().uv(692, 767).cuboid(-5.5F, -23.5F, -0.5F, 11.0F, 47.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-58.4538F, 0.3539F, 33.7483F, 1.6581F, 1.0472F, 3.1416F));

        ModelPartData cube_r71 = roofbase.addChild("cube_r71", ModelPartBuilder.create().uv(147, 371).cuboid(-5.5F, 88.448F, -21.3143F, 11.0F, 47.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -0.4F, 0.0F, 1.8326F, 1.0472F, 3.1416F));

        ModelPartData cube_r72 = roofbase.addChild("cube_r72", ModelPartBuilder.create().uv(692, 767).mirrored().cuboid(-5.5F, -23.5F, -0.5F, 11.0F, 47.0F, 1.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(58.4538F, 0.3539F, 33.7483F, 1.6581F, -1.0472F, -3.1416F));

        ModelPartData cube_r73 = roofbase.addChild("cube_r73", ModelPartBuilder.create().uv(147, 371).mirrored().cuboid(-5.5F, 88.648F, -21.3143F, 11.0F, 47.0F, 1.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(-0.1673F, -0.4518F, -0.0966F, 1.8326F, -1.0472F, -3.1416F));

        ModelPartData cube_r74 = roofbase.addChild("cube_r74", ModelPartBuilder.create().uv(692, 767).mirrored().cuboid(-5.5F, -23.5F, -0.5F, 11.0F, 47.0F, 1.0F, new Dilation(0.0F)).mirrored(false)
        .uv(692, 767).mirrored().cuboid(-5.5F, -23.5F, -0.5F, 11.0F, 47.0F, 1.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(58.4538F, 0.3539F, -33.7483F, -1.6581F, 1.0472F, -3.1416F));

        ModelPartData cube_r75 = roofbase.addChild("cube_r75", ModelPartBuilder.create().uv(147, 371).mirrored().cuboid(-5.5F, 88.448F, 20.3143F, 11.0F, 47.0F, 1.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(0.0F, -0.4F, 0.0F, -1.8326F, 1.0472F, -3.1416F));

        ModelPartData cube_r76 = roofbase.addChild("cube_r76", ModelPartBuilder.create().uv(692, 767).cuboid(-5.5F, -23.5F, -0.5F, 11.0F, 47.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-58.4538F, 0.3539F, -33.7483F, -1.6581F, -1.0472F, 3.1416F));

        ModelPartData cube_r77 = roofbase.addChild("cube_r77", ModelPartBuilder.create().uv(147, 371).mirrored().cuboid(-5.5F, 88.448F, 20.3143F, 11.0F, 47.0F, 1.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(0.0F, -0.4F, 0.0F, -1.8326F, -1.0472F, 3.1416F));

        ModelPartData cube_r78 = roofbase.addChild("cube_r78", ModelPartBuilder.create().uv(0, 464).cuboid(-36.5F, 88.448F, 20.8143F, 73.0F, 47.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, -1.8326F, -1.0472F, 3.1416F));

        ModelPartData cube_r79 = roofbase.addChild("cube_r79", ModelPartBuilder.create().uv(189, 370).cuboid(-36.5F, -1.1481F, -1.6163F, 73.0F, 92.0F, 0.0F, new Dilation(0.0F))
        .uv(147, 576).cuboid(-36.5F, 34.6776F, -3.6087F, 73.0F, 56.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(-0.151F, -1.5212F, 0.0872F, 1.6581F, 1.0472F, 3.1416F));

        ModelPartData cube_r80 = roofbase.addChild("cube_r80", ModelPartBuilder.create().uv(189, 370).cuboid(-36.5F, -1.1481F, -1.6163F, 73.0F, 92.0F, 0.0F, new Dilation(0.0F))
        .uv(441, 576).cuboid(-36.5F, 32.6776F, -4.2087F, 73.0F, 56.0F, 0.0F, new Dilation(0.0F))
        .uv(147, 576).cuboid(-36.5F, 34.6776F, -3.6087F, 73.0F, 56.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.4483F, -1.4606F, 0.2588F, 1.6581F, -1.0472F, -3.1416F));

        ModelPartData cube_r81 = roofbase.addChild("cube_r81", ModelPartBuilder.create().uv(0, 464).cuboid(-36.5F, 88.448F, 20.8143F, 73.0F, 47.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, -1.8326F, 1.0472F, -3.1416F));

        ModelPartData cube_r82 = roofbase.addChild("cube_r82", ModelPartBuilder.create().uv(294, 576).cuboid(-36.5F, 34.6776F, 5.6087F, 73.0F, 56.0F, 0.0F, new Dilation(0.0F))
        .uv(294, 576).cuboid(-36.5F, 34.6776F, 5.6087F, 73.0F, 56.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.4712F, 0.0F, -1.6581F, 1.0472F, -3.1416F));

        ModelPartData cube_r83 = roofbase.addChild("cube_r83", ModelPartBuilder.create().uv(294, 576).cuboid(-36.5F, 34.6776F, 5.6087F, 73.0F, 56.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(-0.0001F, 0.4713F, -0.0001F, -1.6581F, -1.0472F, 3.1416F));

        ModelPartData cube_r84 = roofbase.addChild("cube_r84", ModelPartBuilder.create().uv(441, 576).cuboid(-36.5F, 33.6776F, -4.2087F, 73.0F, 56.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.4483F, -1.4606F, 0.2588F, 1.6581F, 1.0472F, -3.1416F));

        ModelPartData cube_r85 = roofbase.addChild("cube_r85", ModelPartBuilder.create().uv(588, 576).cuboid(-36.5F, 32.6776F, 4.2087F, 73.0F, 56.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(-0.4483F, -1.4606F, -0.2588F, -1.6581F, 1.0472F, 3.1416F));

        ModelPartData cube_r86 = roofbase.addChild("cube_r86", ModelPartBuilder.create().uv(636, 341).cuboid(-36.5F, 88.448F, 19.4143F, 73.0F, 47.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(-0.4483F, -1.9319F, -0.2588F, -1.8326F, 1.0472F, 3.1416F));

        ModelPartData cube_r87 = roofbase.addChild("cube_r87", ModelPartBuilder.create().uv(596, 633).cuboid(-36.5F, 89.448F, -19.4143F, 73.0F, 47.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.4483F, -1.9319F, 0.2588F, 1.8326F, 1.0472F, -3.1416F));

        ModelPartData cube_r88 = roofbase.addChild("cube_r88", ModelPartBuilder.create().uv(441, 576).cuboid(-36.5F, 32.6776F, -4.2087F, 73.0F, 56.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.4483F, -1.4606F, 0.2588F, 1.6581F, 0.0F, -3.1416F));

        ModelPartData cube_r89 = roofbase.addChild("cube_r89", ModelPartBuilder.create().uv(588, 576).cuboid(-36.5F, 32.6776F, 4.2087F, 73.0F, 56.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(-0.4483F, -1.4606F, -0.2588F, -1.6581F, 0.0F, 3.1416F));

        ModelPartData cube_r90 = roofbase.addChild("cube_r90", ModelPartBuilder.create().uv(636, 341).cuboid(-36.5F, 88.448F, 19.4143F, 73.0F, 47.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(-0.4483F, -1.9319F, -0.2588F, -1.8326F, 0.0F, 3.1416F));

        ModelPartData cube_r91 = roofbase.addChild("cube_r91", ModelPartBuilder.create().uv(596, 633).cuboid(-36.5F, 88.448F, -19.4143F, 73.0F, 47.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.4483F, -1.9319F, 0.2588F, 1.8326F, 0.0F, -3.1416F));

        ModelPartData cube_r92 = roofbase.addChild("cube_r92", ModelPartBuilder.create().uv(588, 576).cuboid(-36.5F, 32.6776F, 4.2087F, 73.0F, 56.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(-0.4483F, -1.4606F, -0.2588F, -1.6581F, -1.0472F, 3.1416F));

        ModelPartData cube_r93 = roofbase.addChild("cube_r93", ModelPartBuilder.create().uv(636, 341).cuboid(-36.5F, 88.448F, 19.4143F, 73.0F, 47.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(-0.4483F, -1.9319F, -0.2588F, -1.8326F, -1.0472F, 3.1416F));

        ModelPartData cube_r94 = roofbase.addChild("cube_r94", ModelPartBuilder.create().uv(151, 519).mirrored().cuboid(-37.5F, 34.6776F, 5.6087F, 75.0F, 56.0F, 0.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(0.0F, 0.4712F, 0.0F, 0.0F, 1.5708F, -1.4835F));

        ModelPartData cube_r95 = roofbase.addChild("cube_r95", ModelPartBuilder.create().uv(151, 519).mirrored().cuboid(-37.5F, 34.6776F, 5.6087F, 75.0F, 56.0F, 0.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(0.0F, 0.4712F, 0.0F, 0.0F, -1.5708F, 1.4835F));

        ModelPartData cube_r96 = roofbase.addChild("cube_r96", ModelPartBuilder.create().uv(302, 519).mirrored().cuboid(-37.5F, 32.3647F, 9.7853F, 75.0F, 56.0F, 0.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(0.0F, 4.0615F, 0.0F, 1.4835F, 0.5236F, 0.0F));

        ModelPartData cube_r97 = roofbase.addChild("cube_r97", ModelPartBuilder.create().uv(151, 519).cuboid(-37.5F, 34.6776F, 5.6087F, 75.0F, 56.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.4712F, 0.0F, 1.4835F, 0.5236F, 0.0F));

        ModelPartData cube_r98 = roofbase.addChild("cube_r98", ModelPartBuilder.create().uv(0, 519).cuboid(-37.5F, 34.6776F, -3.6087F, 75.0F, 56.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(-0.0872F, -1.5212F, -0.151F, -1.4835F, 0.5236F, 0.0F));

        ModelPartData cube_r99 = roofbase.addChild("cube_r99", ModelPartBuilder.create().uv(151, 519).cuboid(-37.5F, 34.6776F, 5.6087F, 75.0F, 56.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.4712F, 0.0F, 1.4835F, -0.5236F, 0.0F));

        ModelPartData cube_r100 = roofbase.addChild("cube_r100", ModelPartBuilder.create().uv(0, 519).cuboid(-37.5F, 34.6776F, -3.6087F, 75.0F, 56.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0872F, -1.5212F, -0.151F, -1.4835F, -0.5236F, 0.0F));

        ModelPartData cube_r101 = roofbase.addChild("cube_r101", ModelPartBuilder.create().uv(294, 576).cuboid(-36.5F, -49.5F, 1.0F, 73.0F, 56.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 3.2167F, 84.2591F, 1.4835F, 0.0F, 0.0F));

        ModelPartData cube_r102 = roofbase.addChild("cube_r102", ModelPartBuilder.create().uv(0, 576).cuboid(-36.5F, -49.5F, 1.0F, 73.0F, 56.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 1.2242F, -84.4332F, -1.4835F, 0.0F, 0.0F));

        ModelPartData cube_r103 = roofbase.addChild("cube_r103", ModelPartBuilder.create().uv(302, 633).cuboid(-36.5F, -40.5F, 1.0F, 73.0F, 47.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 12.3032F, -130.2002F, -1.309F, 0.0F, 0.0F));

        ModelPartData cube_r104 = roofbase.addChild("cube_r104", ModelPartBuilder.create().uv(604, 519).cuboid(-37.5F, 88.448F, -18.8143F, 75.0F, 47.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.2588F, -1.9319F, -0.4483F, -1.309F, -0.5236F, 0.0F));

        ModelPartData cube_r105 = roofbase.addChild("cube_r105", ModelPartBuilder.create().uv(604, 519).cuboid(-37.5F, 88.448F, -18.8143F, 75.0F, 47.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(-0.2588F, -1.9319F, -0.4483F, -1.309F, 0.5236F, 0.0F));

        ModelPartData cube_r106 = roofbase.addChild("cube_r106", ModelPartBuilder.create().uv(596, 633).cuboid(-36.5F, 88.448F, -19.4143F, 73.0F, 47.0F, 0.0F, new Dilation(0.0F))
        .uv(449, 633).cuboid(-36.5F, 88.448F, -18.8143F, 73.0F, 47.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.4483F, -1.9319F, 0.2588F, 1.8326F, -1.0472F, -3.1416F));

        ModelPartData cube_r107 = roofbase.addChild("cube_r107", ModelPartBuilder.create().uv(449, 633).cuboid(-36.5F, 88.448F, -18.8143F, 73.0F, 47.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(-0.4483F, -1.9319F, 0.2588F, 1.8326F, 1.0472F, 3.1416F));

        ModelPartData cube_r108 = roofbase.addChild("cube_r108", ModelPartBuilder.create().uv(0, 464).cuboid(-36.5F, -40.5F, 1.0F, 73.0F, 47.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 14.2351F, 129.6825F, 1.309F, 0.0F, 0.0F));

        ModelPartData cube_r109 = roofbase.addChild("cube_r109", ModelPartBuilder.create().uv(530, 181).cuboid(-37.5F, 88.448F, 20.8143F, 75.0F, 47.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 1.309F, -0.5236F, 0.0F));

        ModelPartData underball = roofbase.addChild("underball", ModelPartBuilder.create().uv(757, 459).cuboid(-22.0F, -10.8722F, -53.4849F, 44.0F, 4.0F, 7.0F, new Dilation(0.0F))
        .uv(337, 181).cuboid(-24.0F, 10.5797F, -23.9291F, 48.0F, -1.0F, 48.0F, new Dilation(1.0F))
        .uv(810, 807).cuboid(-12.0F, 11.3297F, -11.9291F, 24.0F, 0.0F, 24.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 64.2703F, -0.0709F));

        ModelPartData cube_r110 = underball.addChild("cube_r110", ModelPartBuilder.create().uv(757, 459).mirrored().cuboid(-22.0F, -2.0F, -3.5F, 44.0F, 4.0F, 7.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(-49.6127F, -8.8722F, -0.3722F, 0.0F, 1.5708F, 0.0F));

        ModelPartData cube_r111 = underball.addChild("cube_r111", ModelPartBuilder.create().uv(757, 459).mirrored().cuboid(-22.0F, -2.0F, -3.5F, 44.0F, 4.0F, 7.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(-35.0815F, -8.8722F, 34.7093F, 0.0F, 2.3562F, 0.0F));

        ModelPartData cube_r112 = underball.addChild("cube_r112", ModelPartBuilder.create().uv(757, 459).mirrored().cuboid(-22.0F, -2.0F, -3.5F, 44.0F, 4.0F, 7.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(0.0F, -8.8722F, 49.2405F, 0.0F, 3.1416F, 0.0F));

        ModelPartData cube_r113 = underball.addChild("cube_r113", ModelPartBuilder.create().uv(757, 459).mirrored().cuboid(-22.0F, -2.0F, -3.5F, 44.0F, 4.0F, 7.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(35.0815F, -8.8722F, 34.7093F, 0.0F, -2.3562F, 0.0F));

        ModelPartData cube_r114 = underball.addChild("cube_r114", ModelPartBuilder.create().uv(757, 459).cuboid(-40.955F, -2.0F, -9.7656F, 44.0F, 4.0F, 7.0F, new Dilation(0.0F)), ModelTransform.of(-17.2478F, -8.8722F, -44.4264F, 0.0F, 0.7854F, 0.0F));

        ModelPartData cube_r115 = underball.addChild("cube_r115", ModelPartBuilder.create().uv(757, 459).cuboid(-21.0F, -6.0F, -5.0F, 44.0F, 4.0F, 7.0F, new Dilation(0.0F)), ModelTransform.of(48.1127F, -4.8722F, -1.3722F, 0.0F, -1.5708F, 0.0F));

        ModelPartData cube_r116 = underball.addChild("cube_r116", ModelPartBuilder.create().uv(757, 459).cuboid(-21.0F, -6.0F, -5.0F, 44.0F, 4.0F, 7.0F, new Dilation(0.0F)), ModelTransform.of(33.3137F, -4.8722F, -35.1001F, 0.0F, -0.7854F, 0.0F));

        ModelPartData cube_r117 = underball.addChild("cube_r117", ModelPartBuilder.create().uv(783, 367).cuboid(-24.0F, 0.5F, -1.0F, 48.0F, 12.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(36.9439F, -12.7703F, -36.873F, 0.6109F, -0.7854F, 0.0F));

        ModelPartData cube_r118 = underball.addChild("cube_r118", ModelPartBuilder.create().uv(860, 459).cuboid(-1.0769F, -43.6828F, -39.1356F, 2.0F, 12.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 2.122F, 0.0709F, -2.5307F, 1.1781F, -3.1416F));

        ModelPartData cube_r119 = underball.addChild("cube_r119", ModelPartBuilder.create().uv(445, 885).cuboid(-1.0769F, -45.2538F, -22.0792F, 2.0F, 27.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 2.122F, 0.0709F, -2.0944F, 1.1781F, -3.1416F));

        ModelPartData cube_r120 = underball.addChild("cube_r120", ModelPartBuilder.create().uv(581, 885).cuboid(-1.0769F, -26.8479F, -9.9942F, 2.0F, 27.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 2.122F, 0.0709F, -1.5708F, 1.1781F, -3.1416F));

        ModelPartData cube_r121 = underball.addChild("cube_r121", ModelPartBuilder.create().uv(860, 459).mirrored().cuboid(-0.9231F, -43.6828F, 37.1356F, 2.0F, 12.0F, 2.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(0.0F, 2.122F, 0.0709F, 2.5307F, 1.1781F, 3.1416F));

        ModelPartData cube_r122 = underball.addChild("cube_r122", ModelPartBuilder.create().uv(445, 885).mirrored().cuboid(-0.9231F, -45.2538F, 20.0792F, 2.0F, 27.0F, 2.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(0.0F, 2.122F, 0.0709F, 2.0944F, 1.1781F, 3.1416F));

        ModelPartData cube_r123 = underball.addChild("cube_r123", ModelPartBuilder.create().uv(581, 885).mirrored().cuboid(-0.9231F, -26.8479F, 7.9942F, 2.0F, 27.0F, 2.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(0.0F, 2.122F, 0.0709F, 1.5708F, 1.1781F, 3.1416F));

        ModelPartData cube_r124 = underball.addChild("cube_r124", ModelPartBuilder.create().uv(860, 459).cuboid(-1.0769F, -43.6828F, -39.1356F, 2.0F, 12.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 2.122F, 0.0709F, 0.6109F, 1.1781F, 0.0F));

        ModelPartData cube_r125 = underball.addChild("cube_r125", ModelPartBuilder.create().uv(445, 885).cuboid(-1.0769F, -45.2538F, -22.0792F, 2.0F, 27.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 2.122F, 0.0709F, 1.0472F, 1.1781F, 0.0F));

        ModelPartData cube_r126 = underball.addChild("cube_r126", ModelPartBuilder.create().uv(581, 885).cuboid(-1.0769F, -26.8479F, -9.9942F, 2.0F, 27.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 2.122F, 0.0709F, 1.5708F, 1.1781F, 0.0F));

        ModelPartData cube_r127 = underball.addChild("cube_r127", ModelPartBuilder.create().uv(860, 459).mirrored().cuboid(-0.9231F, -43.6828F, 37.1356F, 2.0F, 12.0F, 2.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(0.0F, 2.122F, 0.0709F, -0.6109F, 1.1781F, 0.0F));

        ModelPartData cube_r128 = underball.addChild("cube_r128", ModelPartBuilder.create().uv(445, 885).mirrored().cuboid(-0.9231F, -45.2538F, 20.0792F, 2.0F, 27.0F, 2.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(0.0F, 2.122F, 0.0709F, -1.0472F, 1.1781F, 0.0F));

        ModelPartData cube_r129 = underball.addChild("cube_r129", ModelPartBuilder.create().uv(581, 885).mirrored().cuboid(-0.9231F, -26.8479F, 7.9942F, 2.0F, 27.0F, 2.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(0.0F, 2.122F, 0.0709F, -1.5708F, 1.1781F, 0.0F));

        ModelPartData cube_r130 = underball.addChild("cube_r130", ModelPartBuilder.create().uv(860, 459).cuboid(-1.0769F, -43.6828F, -39.1356F, 2.0F, 12.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 2.122F, 0.0709F, 0.6109F, 0.3927F, 0.0F));

        ModelPartData cube_r131 = underball.addChild("cube_r131", ModelPartBuilder.create().uv(445, 885).cuboid(-1.0769F, -45.2538F, -22.0792F, 2.0F, 27.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 2.122F, 0.0709F, 1.0472F, 0.3927F, 0.0F));

        ModelPartData cube_r132 = underball.addChild("cube_r132", ModelPartBuilder.create().uv(581, 885).cuboid(-1.0769F, -26.8479F, -9.9942F, 2.0F, 27.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 2.122F, 0.0709F, 1.5708F, 0.3927F, 0.0F));

        ModelPartData cube_r133 = underball.addChild("cube_r133", ModelPartBuilder.create().uv(860, 459).mirrored().cuboid(-0.9231F, -43.6828F, 37.1356F, 2.0F, 12.0F, 2.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(0.0F, 2.122F, 0.0709F, -0.6109F, 0.3927F, 0.0F));

        ModelPartData cube_r134 = underball.addChild("cube_r134", ModelPartBuilder.create().uv(445, 885).mirrored().cuboid(-0.9231F, -45.2538F, 20.0792F, 2.0F, 27.0F, 2.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(0.0F, 2.122F, 0.0709F, -1.0472F, 0.3927F, 0.0F));

        ModelPartData cube_r135 = underball.addChild("cube_r135", ModelPartBuilder.create().uv(581, 885).mirrored().cuboid(-0.9231F, -26.8479F, 7.9942F, 2.0F, 27.0F, 2.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(0.0F, 2.122F, 0.0709F, -1.5708F, 0.3927F, 0.0F));

        ModelPartData cube_r136 = underball.addChild("cube_r136", ModelPartBuilder.create().uv(860, 459).mirrored().cuboid(-2.25F, -6.0F, -1.0F, 2.0F, 12.0F, 2.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(-19.0F, -6.8722F, 49.4084F, -0.6109F, -0.3927F, 0.0F));

        ModelPartData cube_r137 = underball.addChild("cube_r137", ModelPartBuilder.create().uv(445, 885).mirrored().cuboid(-1.0F, -6.0F, -1.0F, 2.0F, 27.0F, 2.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(-16.9715F, 0.7503F, 41.2448F, -1.0472F, -0.3927F, 0.0F));

        ModelPartData cube_r138 = underball.addChild("cube_r138", ModelPartBuilder.create().uv(581, 885).mirrored().cuboid(-1.0F, 8.0F, 22.0F, 2.0F, 27.0F, 2.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(-13.2646F, -11.8837F, 32.2956F, -1.5708F, -0.3927F, 0.0F));

        ModelPartData cube_r139 = underball.addChild("cube_r139", ModelPartBuilder.create().uv(860, 459).cuboid(0.25F, -6.0F, -1.0F, 2.0F, 12.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(19.0F, -6.8722F, -49.2666F, 0.6109F, -0.3927F, 0.0F));

        ModelPartData cube_r140 = underball.addChild("cube_r140", ModelPartBuilder.create().uv(581, 885).cuboid(-1.0F, 8.0F, -24.0F, 2.0F, 27.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(13.2646F, -11.8837F, -32.1538F, 1.5708F, -0.3927F, 0.0F));

        ModelPartData cube_r141 = underball.addChild("cube_r141", ModelPartBuilder.create().uv(445, 885).cuboid(-1.0F, -6.0F, -1.0F, 2.0F, 27.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(16.9715F, 0.7503F, -41.103F, 1.0472F, -0.3927F, 0.0F));

        ModelPartData cube_r142 = underball.addChild("cube_r142", ModelPartBuilder.create().uv(783, 367).cuboid(-24.0F, 0.5F, -1.0F, 48.0F, 12.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -12.7703F, -52.1757F, 0.6109F, 0.0F, 0.0F));

        ModelPartData cube_r143 = underball.addChild("cube_r143", ModelPartBuilder.create().uv(783, 341).cuboid(-24.0F, -42.4391F, 18.2854F, 48.0F, 25.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 3.4267F, 0.0709F, 0.0F, -1.5708F, -1.0472F));

        ModelPartData cube_r144 = underball.addChild("cube_r144", ModelPartBuilder.create().uv(773, 389).cuboid(-24.0F, -42.4391F, -18.2854F, 48.0F, 25.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 3.4267F, 0.0709F, 0.0F, -1.5708F, 1.0472F));

        ModelPartData cube_r145 = underball.addChild("cube_r145", ModelPartBuilder.create().uv(783, 341).cuboid(-24.0F, -42.4391F, 18.2854F, 48.0F, 25.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 3.4267F, 0.0709F, -1.0472F, -0.7854F, 0.0F));

        ModelPartData cube_r146 = underball.addChild("cube_r146", ModelPartBuilder.create().uv(773, 389).cuboid(-24.0F, -42.4391F, -18.2854F, 48.0F, 25.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 3.4267F, 0.0709F, 1.0472F, -0.7854F, 0.0F));

        ModelPartData cube_r147 = underball.addChild("cube_r147", ModelPartBuilder.create().uv(773, 389).cuboid(-24.0F, -42.4391F, -18.2854F, 48.0F, 25.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 3.4267F, 0.0709F, 1.0472F, 0.0F, 0.0F));

        ModelPartData cube_r148 = underball.addChild("cube_r148", ModelPartBuilder.create().uv(783, 341).mirrored().cuboid(-24.0F, -42.4391F, 18.2854F, 48.0F, 25.0F, 0.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(0.0F, 3.4267F, 0.0709F, -1.0472F, 0.0F, 0.0F));

        ModelPartData cube_r149 = underball.addChild("cube_r149", ModelPartBuilder.create().uv(783, 341).cuboid(-24.0F, -12.5F, 1.0F, 48.0F, 25.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(24.4452F, 3.4267F, 24.5161F, -1.0472F, 0.7854F, 0.0F));

        ModelPartData cube_r150 = underball.addChild("cube_r150", ModelPartBuilder.create().uv(773, 389).cuboid(-24.0F, -12.5F, -1.0F, 48.0F, 25.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(-24.4452F, 3.4267F, -24.3743F, 1.0472F, 0.7854F, 0.0F));

        ModelPartData cube_r151 = underball.addChild("cube_r151", ModelPartBuilder.create().uv(783, 367).cuboid(-24.0F, 0.5F, -1.0F, 48.0F, 12.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(-36.9439F, -12.7703F, -36.873F, 0.6109F, 0.7854F, 0.0F));

        ModelPartData cube_r152 = underball.addChild("cube_r152", ModelPartBuilder.create().uv(828, 206).cuboid(-24.0F, 0.5F, 1.0F, 48.0F, 12.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(-36.9439F, -12.7703F, 37.0148F, -0.6109F, -0.7854F, 0.0F));

        ModelPartData cube_r153 = underball.addChild("cube_r153", ModelPartBuilder.create().uv(783, 367).cuboid(-24.0F, 0.5F, -1.0F, 48.0F, 12.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(-52.2466F, -12.7703F, 0.0709F, 0.0F, 1.5708F, -0.6109F));

        ModelPartData cube_r154 = underball.addChild("cube_r154", ModelPartBuilder.create().uv(783, 367).cuboid(-24.0F, 0.5F, -1.0F, 48.0F, 12.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(52.2466F, -12.7703F, 0.0709F, 0.0F, -1.5708F, 0.6109F));

        ModelPartData cube_r155 = underball.addChild("cube_r155", ModelPartBuilder.create().uv(828, 206).cuboid(-24.0F, 0.5F, 1.0F, 48.0F, 12.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(36.9439F, -12.7703F, 37.0148F, -0.6109F, 0.7854F, 0.0F));

        ModelPartData cube_r156 = underball.addChild("cube_r156", ModelPartBuilder.create().uv(828, 206).cuboid(-24.0F, 0.5F, 1.0F, 48.0F, 12.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -12.7703F, 52.3175F, -0.6109F, 0.0F, 0.0F));

        ModelPartData circlecog6 = underball.addChild("circlecog6", ModelPartBuilder.create().uv(791, 229).cuboid(-12.0F, 2.0F, -12.0F, 24.0F, 0.0F, 24.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 10.3297F, 0.0709F));

        ModelPartData circlecog5 = roofbase.addChild("circlecog5", ModelPartBuilder.create().uv(791, 254).cuboid(-12.0F, 3.0F, -12.0F, 24.0F, 0.0F, 24.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 74.6F, 0.0F));

        ModelPartData gun2 = roofbase.addChild("gun2", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 74.6F, 0.0F));

        ModelPartData pipe = gun2.addChild("pipe", ModelPartBuilder.create().uv(646, 887).cuboid(-1.0F, 0.0F, -1.0F, 2.0F, 14.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData circle = gun2.addChild("circle", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 19.0F, 0.0F));

        ModelPartData cube_r157 = circle.addChild("cube_r157", ModelPartBuilder.create().uv(828, 181).cuboid(-11.0F, 0.0F, -12.0F, 24.0F, 0.0F, 24.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -1.0F, 0.0F, 0.7854F, 0.0F, 1.5708F));

        ModelPartData circle2 = gun2.addChild("circle2", ModelPartBuilder.create(), ModelTransform.of(0.0F, 19.0F, 0.0F, 0.0F, 0.0F, 0.9774F));

        ModelPartData cube_r158 = circle2.addChild("cube_r158", ModelPartBuilder.create().uv(791, 279).cuboid(-12.0F, 1.0F, -12.0F, 24.0F, 0.0F, 24.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -1.0F, 0.0F, 0.0F, 0.2182F, 0.0F));

        ModelPartData circle3 = gun2.addChild("circle3", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 19.0F, 0.0F));

        ModelPartData cube_r159 = circle3.addChild("cube_r159", ModelPartBuilder.create().uv(791, 279).cuboid(-12.0F, 1.0F, -12.0F, 24.0F, 0.0F, 24.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -1.0F, 0.0F, 0.0F, 0.2182F, 0.0F));

        ModelPartData circlecog = gun2.addChild("circlecog", ModelPartBuilder.create().uv(810, 782).cuboid(-12.0F, 0.0F, -12.0F, 24.0F, 0.0F, 24.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 11.0F, 0.0F));

        ModelPartData circlecog2 = gun2.addChild("circlecog2", ModelPartBuilder.create().uv(810, 757).cuboid(-12.0F, 9.0F, -12.0F, 24.0F, 0.0F, 24.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData circlecog3 = gun2.addChild("circlecog3", ModelPartBuilder.create().uv(793, 304).cuboid(-12.0F, 7.0F, -12.0F, 24.0F, 0.0F, 24.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData circlecog4 = gun2.addChild("circlecog4", ModelPartBuilder.create().uv(791, 279).cuboid(-12.0F, 5.0F, -12.0F, 24.0F, 0.0F, 24.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData bone2 = roofbase.addChild("bone2", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 41.1447F, 0.0F));

        ModelPartData cube_r160 = bone2.addChild("cube_r160", ModelPartBuilder.create().uv(0, 512).cuboid(-35.5F, -122.78F, 32.0949F, 71.0F, 6.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, -1.309F, 0.5236F, 0.0F));

        ModelPartData cube_r161 = bone2.addChild("cube_r161", ModelPartBuilder.create().uv(0, 512).cuboid(-35.5F, -122.78F, 32.0949F, 71.0F, 6.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, -1.309F, -0.5236F, 0.0F));

        ModelPartData cube_r162 = bone2.addChild("cube_r162", ModelPartBuilder.create().uv(0, 512).cuboid(-35.5F, -122.78F, 32.0949F, 71.0F, 6.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, -1.309F, -1.0472F, 0.0F));

        ModelPartData cube_r163 = bone2.addChild("cube_r163", ModelPartBuilder.create().uv(0, 512).cuboid(-35.5F, -122.78F, 32.0949F, 71.0F, 6.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.5708F, -1.309F));

        ModelPartData cube_r164 = bone2.addChild("cube_r164", ModelPartBuilder.create().uv(0, 512).cuboid(-35.5F, -124.898F, 24.1903F, 71.0F, 6.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 8.1835F, 0.0F, 1.8326F, -1.0472F, 3.1416F));

        ModelPartData cube_r165 = bone2.addChild("cube_r165", ModelPartBuilder.create().uv(606, 512).cuboid(-35.5F, -122.78F, -32.0949F, 71.0F, 6.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 1.309F, 0.5236F, 0.0F));

        ModelPartData cube_r166 = bone2.addChild("cube_r166", ModelPartBuilder.create().uv(606, 512).mirrored().cuboid(-35.5F, -122.78F, -32.0949F, 71.0F, 6.0F, 0.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(0.0F, 0.0F, 0.0F, 1.309F, 0.0F, 0.0F));

        ModelPartData cube_r167 = bone2.addChild("cube_r167", ModelPartBuilder.create().uv(606, 512).mirrored().cuboid(-35.5F, -122.78F, -32.0949F, 71.0F, 6.0F, 0.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(0.0F, 0.0F, 0.0F, 1.309F, -0.5236F, 0.0F));

        ModelPartData cube_r168 = bone2.addChild("cube_r168", ModelPartBuilder.create().uv(606, 512).mirrored().cuboid(-35.5F, -122.78F, -32.0949F, 71.0F, 6.0F, 0.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(0.0F, 0.0F, 0.0F, 1.309F, -1.0472F, 0.0F));

        ModelPartData cube_r169 = bone2.addChild("cube_r169", ModelPartBuilder.create().uv(606, 512).mirrored().cuboid(-35.5F, -122.78F, -32.0949F, 71.0F, 6.0F, 0.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.5708F, 1.309F));

        ModelPartData cube_r170 = bone2.addChild("cube_r170", ModelPartBuilder.create().uv(606, 512).mirrored().cuboid(-35.5F, -124.898F, -24.1903F, 71.0F, 6.0F, 0.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(0.0F, 8.1835F, 0.0F, -1.8326F, -1.0472F, -3.1416F));

        ModelPartData dome2 = roofbase.addChild("dome2", ModelPartBuilder.create().uv(483, 426).cuboid(-14.0F, -29.3F, -13.0F, 28.0F, 0.0F, 26.0F, new Dilation(0.0F))
        .uv(483, 453).mirrored().cuboid(-20.0F, -7.8512F, -48.2843F, 40.0F, 2.0F, 3.0F, new Dilation(0.001F)).mirrored(false)
        .uv(483, 453).cuboid(-20.0F, -7.8512F, 45.2843F, 40.0F, 2.0F, 3.0F, new Dilation(0.001F)), ModelTransform.pivot(0.0F, 5.0F, 0.0F));

        ModelPartData cube_r171 = dome2.addChild("cube_r171", ModelPartBuilder.create().uv(193, 833).mirrored().cuboid(-21.5F, 8.837F, 10.9496F, 43.0F, 25.0F, 0.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(0.0F, -23.1446F, 0.0F, -2.0071F, 0.7854F, -3.1416F));

        ModelPartData cube_r172 = dome2.addChild("cube_r172", ModelPartBuilder.create().uv(280, 833).mirrored().cuboid(-21.6F, 8.837F, -10.9496F, 43.0F, 25.0F, 0.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(0.0F, -23.1446F, 0.0F, 2.0071F, 0.7854F, 3.1416F));

        ModelPartData cube_r173 = dome2.addChild("cube_r173", ModelPartBuilder.create().uv(280, 833).cuboid(-21.6F, 8.837F, -10.9496F, 43.0F, 25.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -23.1446F, 0.0F, 0.0F, 1.5708F, 1.1345F));

        ModelPartData cube_r174 = dome2.addChild("cube_r174", ModelPartBuilder.create().uv(280, 833).cuboid(-21.5F, 8.837F, -10.9496F, 43.0F, 25.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -23.1446F, 0.0F, -1.1345F, 0.7854F, 0.0F));

        ModelPartData cube_r175 = dome2.addChild("cube_r175", ModelPartBuilder.create().uv(280, 833).cuboid(-21.5F, -12.5F, -1.0F, 43.0F, 25.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(-0.1F, -23.1446F, -23.5428F, -1.1345F, 0.0F, 0.0F));

        ModelPartData cube_r176 = dome2.addChild("cube_r176", ModelPartBuilder.create().uv(483, 453).cuboid(-19.0F, -1.0F, -2.0F, 40.0F, 2.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(-34.1421F, -6.8512F, 32.7279F, 0.0F, -0.7854F, 0.0F));

        ModelPartData cube_r177 = dome2.addChild("cube_r177", ModelPartBuilder.create().uv(483, 453).cuboid(-19.0F, -1.0F, -1.0F, 40.0F, 2.0F, 3.0F, new Dilation(0.001F)), ModelTransform.of(47.2843F, -6.8512F, -1.0F, 0.0F, -1.5708F, 0.0F));

        ModelPartData cube_r178 = dome2.addChild("cube_r178", ModelPartBuilder.create().uv(483, 453).cuboid(-19.0F, -1.0F, -2.0F, 40.0F, 2.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(32.7279F, -6.8512F, 34.1421F, 0.0F, 0.7854F, 0.0F));

        ModelPartData cube_r179 = dome2.addChild("cube_r179", ModelPartBuilder.create().uv(483, 453).mirrored().cuboid(-19.0F, -1.0F, -1.0F, 40.0F, 2.0F, 3.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(-34.1421F, -6.8512F, -32.7279F, 0.0F, 0.7854F, 0.0F));

        ModelPartData cube_r180 = dome2.addChild("cube_r180", ModelPartBuilder.create().uv(483, 453).mirrored().cuboid(-19.0F, -1.0F, -1.0F, 40.0F, 2.0F, 3.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(32.7279F, -6.8512F, -34.1421F, 0.0F, -0.7854F, 0.0F));

        ModelPartData cube_r181 = dome2.addChild("cube_r181", ModelPartBuilder.create().uv(483, 453).mirrored().cuboid(-19.0F, -1.0F, -2.0F, 40.0F, 2.0F, 3.0F, new Dilation(0.001F)).mirrored(false), ModelTransform.of(-47.2843F, -6.8512F, -1.0F, 0.0F, -1.5708F, 0.0F));

        ModelPartData cube_r182 = dome2.addChild("cube_r182", ModelPartBuilder.create().uv(106, 833).mirrored().cuboid(-21.5F, -10.5F, -1.0F, 43.0F, 25.0F, 0.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(-0.1F, -10.3512F, -41.6507F, -0.7418F, 0.0F, 0.0F));

        ModelPartData cube_r183 = dome2.addChild("cube_r183", ModelPartBuilder.create().uv(810, 832).mirrored().cuboid(-21.4F, -10.5F, 1.0F, 43.0F, 25.0F, 0.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(0.0F, -10.3512F, 41.6507F, 0.7418F, 0.0F, 0.0F));

        ModelPartData cube_r184 = dome2.addChild("cube_r184", ModelPartBuilder.create().uv(280, 833).cuboid(-21.6F, 8.837F, -10.9496F, 43.0F, 25.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -23.1446F, 0.0F, 2.0071F, -0.7854F, -3.1416F));

        ModelPartData cube_r185 = dome2.addChild("cube_r185", ModelPartBuilder.create().uv(280, 833).cuboid(-21.5F, 8.837F, -10.9496F, 43.0F, 25.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -23.1446F, -0.1F, 0.0F, -1.5708F, -1.1345F));

        ModelPartData cube_r186 = dome2.addChild("cube_r186", ModelPartBuilder.create().uv(193, 833).cuboid(-21.4F, -12.5F, 1.0F, 43.0F, 25.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -23.1446F, 23.5428F, 1.1345F, 0.0F, 0.0F));

        ModelPartData cube_r187 = dome2.addChild("cube_r187", ModelPartBuilder.create().uv(886, 596).mirrored().cuboid(-1.0931F, 27.4766F, -27.9334F, 2.0F, 17.0F, 2.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(0.0F, -20.6462F, 0.0F, -0.7418F, -1.1781F, 0.0F));

        ModelPartData cube_r188 = dome2.addChild("cube_r188", ModelPartBuilder.create().uv(436, 885).mirrored().cuboid(-1.0931F, 8.7074F, -13.7041F, 2.0F, 28.0F, 2.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(0.0F, -20.6462F, 0.0F, -1.1781F, -1.1781F, 0.0F));

        ModelPartData cube_r189 = dome2.addChild("cube_r189", ModelPartBuilder.create().uv(886, 596).cuboid(-0.9069F, 27.4766F, 25.9334F, 2.0F, 17.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -20.6462F, 0.0F, 0.7418F, -1.1781F, 0.0F));

        ModelPartData cube_r190 = dome2.addChild("cube_r190", ModelPartBuilder.create().uv(436, 885).cuboid(-0.9069F, 8.7074F, 11.7041F, 2.0F, 28.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -20.6462F, 0.0F, 1.1781F, -1.1781F, 0.0F));

        ModelPartData cube_r191 = dome2.addChild("cube_r191", ModelPartBuilder.create().uv(655, 887).cuboid(-0.9069F, 0.2889F, 7.3288F, 2.0F, 13.0F, 2.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, -20.6462F, 0.0F, 1.5708F, -1.1781F, 0.0F));

        ModelPartData cube_r192 = dome2.addChild("cube_r192", ModelPartBuilder.create().uv(637, 887).cuboid(-1.0931F, -0.7111F, -9.3288F, 2.0F, 14.0F, 2.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, -20.6462F, 0.0F, -1.5708F, -1.1781F, 0.0F));

        ModelPartData cube_r193 = dome2.addChild("cube_r193", ModelPartBuilder.create().uv(886, 596).mirrored().cuboid(-1.0931F, 27.4766F, -27.9334F, 2.0F, 17.0F, 2.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(0.0F, -20.6462F, 0.0F, -0.7418F, -0.3927F, 0.0F));

        ModelPartData cube_r194 = dome2.addChild("cube_r194", ModelPartBuilder.create().uv(436, 885).mirrored().cuboid(-1.0931F, 8.7074F, -13.7041F, 2.0F, 28.0F, 2.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(0.0F, -20.6462F, 0.0F, -1.1781F, -0.3927F, 0.0F));

        ModelPartData cube_r195 = dome2.addChild("cube_r195", ModelPartBuilder.create().uv(886, 596).cuboid(-0.9069F, 27.4766F, 25.9334F, 2.0F, 17.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -20.6462F, 0.0F, 0.7418F, -0.3927F, 0.0F));

        ModelPartData cube_r196 = dome2.addChild("cube_r196", ModelPartBuilder.create().uv(436, 885).cuboid(-0.9069F, 8.7074F, 11.7041F, 2.0F, 28.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -20.6462F, 0.0F, 1.1781F, -0.3927F, 0.0F));

        ModelPartData cube_r197 = dome2.addChild("cube_r197", ModelPartBuilder.create().uv(655, 887).cuboid(-0.9069F, 0.2889F, 7.3288F, 2.0F, 13.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -20.6462F, 0.0F, 1.5708F, -0.3927F, 0.0F));

        ModelPartData cube_r198 = dome2.addChild("cube_r198", ModelPartBuilder.create().uv(637, 887).cuboid(-1.0931F, -0.7111F, -9.3288F, 2.0F, 14.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -20.6462F, 0.0F, -1.5708F, -0.3927F, 0.0F));

        ModelPartData cube_r199 = dome2.addChild("cube_r199", ModelPartBuilder.create().uv(886, 596).mirrored().cuboid(-1.0931F, 27.4766F, -27.9334F, 2.0F, 17.0F, 2.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(0.0F, -20.6462F, 0.0F, -0.7418F, 0.3927F, 0.0F));

        ModelPartData cube_r200 = dome2.addChild("cube_r200", ModelPartBuilder.create().uv(436, 885).mirrored().cuboid(-1.0931F, 8.7074F, -13.7041F, 2.0F, 28.0F, 2.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(0.0F, -20.6462F, 0.0F, -1.1781F, 0.3927F, 0.0F));

        ModelPartData cube_r201 = dome2.addChild("cube_r201", ModelPartBuilder.create().uv(886, 596).cuboid(-0.9069F, 27.4766F, 25.9334F, 2.0F, 17.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -20.6462F, 0.0F, 0.7418F, 0.3927F, 0.0F));

        ModelPartData cube_r202 = dome2.addChild("cube_r202", ModelPartBuilder.create().uv(436, 885).cuboid(-0.9069F, 8.7074F, 11.7041F, 2.0F, 28.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -20.6462F, 0.0F, 1.1781F, 0.3927F, 0.0F));

        ModelPartData cube_r203 = dome2.addChild("cube_r203", ModelPartBuilder.create().uv(655, 887).cuboid(-0.9069F, 0.2889F, 7.3288F, 2.0F, 13.0F, 2.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, -20.6462F, 0.0F, 1.5708F, 0.3927F, 0.0F));

        ModelPartData cube_r204 = dome2.addChild("cube_r204", ModelPartBuilder.create().uv(637, 887).cuboid(-1.0931F, -0.7111F, -9.3288F, 2.0F, 14.0F, 2.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, -20.6462F, 0.0F, -1.5708F, 0.3927F, 0.0F));

        ModelPartData cube_r205 = dome2.addChild("cube_r205", ModelPartBuilder.create().uv(886, 596).cuboid(-0.9069F, 27.4766F, 25.9334F, 2.0F, 17.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -20.6462F, 0.0F, 0.7418F, 1.1781F, 0.0F));

        ModelPartData cube_r206 = dome2.addChild("cube_r206", ModelPartBuilder.create().uv(436, 885).cuboid(-0.9069F, 8.7074F, 11.7041F, 2.0F, 28.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -20.6462F, 0.0F, 1.1781F, 1.1781F, 0.0F));

        ModelPartData cube_r207 = dome2.addChild("cube_r207", ModelPartBuilder.create().uv(655, 887).cuboid(-0.9069F, 0.2889F, 7.3288F, 2.0F, 13.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -20.6462F, 0.0F, 1.5708F, 1.1781F, 0.0F));

        ModelPartData cube_r208 = dome2.addChild("cube_r208", ModelPartBuilder.create().uv(637, 887).cuboid(-1.0931F, -0.7111F, -9.3288F, 2.0F, 14.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -20.6462F, 0.0F, -1.5708F, 1.1781F, 0.0F));

        ModelPartData cube_r209 = dome2.addChild("cube_r209", ModelPartBuilder.create().uv(436, 885).mirrored().cuboid(-1.0931F, 8.7074F, -13.7041F, 2.0F, 28.0F, 2.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(0.0F, -20.6462F, 0.0F, -1.1781F, 1.1781F, 0.0F));

        ModelPartData cube_r210 = dome2.addChild("cube_r210", ModelPartBuilder.create().uv(886, 596).mirrored().cuboid(-1.0931F, 27.4766F, -27.9334F, 2.0F, 17.0F, 2.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(0.0F, -20.6462F, 0.0F, -0.7418F, 1.1781F, 0.0F));

        ModelPartData cube_r211 = dome2.addChild("cube_r211", ModelPartBuilder.create().uv(106, 833).mirrored().cuboid(-21.5F, -10.5F, -1.0F, 43.0F, 25.0F, 0.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(-29.4515F, -10.3512F, -29.4515F, -0.7418F, 0.7854F, 0.0F));

        ModelPartData cube_r212 = dome2.addChild("cube_r212", ModelPartBuilder.create().uv(106, 833).cuboid(-21.5F, -10.5F, -1.0F, 43.0F, 25.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(29.4515F, -10.3512F, -29.4515F, -0.7418F, -0.7854F, 0.0F));

        ModelPartData cube_r213 = dome2.addChild("cube_r213", ModelPartBuilder.create().uv(810, 832).cuboid(-21.5F, -10.5F, 1.0F, 43.0F, 25.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(41.6507F, -10.3512F, -0.1F, 0.0F, 1.5708F, -0.7418F));

        ModelPartData cube_r214 = dome2.addChild("cube_r214", ModelPartBuilder.create().uv(810, 832).cuboid(-21.4F, -10.5F, 1.0F, 43.0F, 25.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(29.4515F, -10.3512F, 29.4515F, 0.7418F, 0.7854F, 0.0F));

        ModelPartData cube_r215 = dome2.addChild("cube_r215", ModelPartBuilder.create().uv(810, 832).cuboid(-21.4F, -10.5F, 1.0F, 43.0F, 25.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(-29.4515F, -10.3512F, 29.4515F, 0.7418F, -0.7854F, 0.0F));

        ModelPartData cube_r216 = dome2.addChild("cube_r216", ModelPartBuilder.create().uv(810, 832).cuboid(-21.4F, -10.5F, 1.0F, 43.0F, 25.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(-41.6507F, -10.3512F, 0.0F, 0.0F, -1.5708F, 0.7418F));

        ModelPartData pairofballs = sidetest.addChild("pairofballs", ModelPartBuilder.create(), ModelTransform.of(10.5F, -116.1936F, -91.5025F, 0.0F, 2.0944F, 0.0F));

        ModelPartData pairofballs7 = pairofballs.addChild("pairofballs7", ModelPartBuilder.create(), ModelTransform.pivot(-90.0F, 2.0F, 0.0F));

        ModelPartData cube_r217 = pairofballs7.addChild("cube_r217", ModelPartBuilder.create().uv(636, 389).cuboid(-17.0F, 1.75F, -17.0F, 34.0F, 2.0F, 34.0F, new Dilation(0.0F))
        .uv(300, 463).cuboid(-19.0F, -15.25F, -19.0F, 38.0F, 17.0F, 38.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.7854F, 0.0F));

        ModelPartData pairofballs4 = pairofballs.addChild("pairofballs4", ModelPartBuilder.create(), ModelTransform.pivot(90.0F, 2.0F, 0.0F));

        ModelPartData cube_r218 = pairofballs4.addChild("cube_r218", ModelPartBuilder.create().uv(147, 463).cuboid(-19.0F, -15.25F, -19.0F, 38.0F, 17.0F, 38.0F, new Dilation(0.0F))
        .uv(636, 389).cuboid(-17.0F, 1.75F, -17.0F, 34.0F, 2.0F, 34.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -0.7854F, 0.0F));

        ModelPartData pairofballs2 = sidetest.addChild("pairofballs2", ModelPartBuilder.create(), ModelTransform.of(10.5F, -116.1936F, -91.5025F, 0.0F, 1.0472F, 0.0F));

        ModelPartData pairofballs8 = pairofballs2.addChild("pairofballs8", ModelPartBuilder.create(), ModelTransform.pivot(90.0F, 2.0F, 0.0F));

        ModelPartData cube_r219 = pairofballs8.addChild("cube_r219", ModelPartBuilder.create().uv(483, 370).cuboid(-19.0F, -15.25F, -19.0F, 38.0F, 17.0F, 38.0F, new Dilation(0.0F))
        .uv(636, 389).cuboid(-17.0F, 1.75F, -17.0F, 34.0F, 2.0F, 34.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -0.7854F, 0.0F));

        ModelPartData pairofballs5 = pairofballs2.addChild("pairofballs5", ModelPartBuilder.create(), ModelTransform.pivot(-90.0F, 0.0F, 0.0F));

        ModelPartData cube_r220 = pairofballs5.addChild("cube_r220", ModelPartBuilder.create().uv(636, 389).cuboid(-17.0F, 1.75F, -17.0F, 34.0F, 2.0F, 34.0F, new Dilation(0.0F))
        .uv(453, 463).cuboid(-19.0F, -15.25F, -19.0F, 38.0F, 17.0F, 38.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 2.0F, 0.0F, 0.0F, 0.7854F, 0.0F));

        ModelPartData pairofballs3 = sidetest.addChild("pairofballs3", ModelPartBuilder.create(), ModelTransform.pivot(10.5F, -116.1936F, -91.5025F));

        ModelPartData pairofballs9 = pairofballs3.addChild("pairofballs9", ModelPartBuilder.create(), ModelTransform.pivot(-90.0F, 2.0F, 0.0F));

        ModelPartData cube_r221 = pairofballs9.addChild("cube_r221", ModelPartBuilder.create().uv(636, 389).cuboid(-17.0F, 1.75F, -17.0F, 34.0F, 2.0F, 34.0F, new Dilation(0.0F))
        .uv(491, 229).cuboid(-19.0F, -15.25F, -19.0F, 38.0F, 17.0F, 38.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.7854F, 0.0F));

        ModelPartData pairofballs6 = pairofballs3.addChild("pairofballs6", ModelPartBuilder.create(), ModelTransform.pivot(90.0F, 2.0F, 0.0F));

        ModelPartData cube_r222 = pairofballs6.addChild("cube_r222", ModelPartBuilder.create().uv(491, 285).cuboid(-19.0F, -15.25F, -19.0F, 38.0F, 17.0F, 38.0F, new Dilation(0.0F))
        .uv(636, 389).cuboid(-17.0F, 1.75F, -17.0F, 34.0F, 2.0F, 34.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -0.7854F, 0.0F));

        ModelPartData bone = sidetest.addChild("bone", ModelPartBuilder.create(), ModelTransform.pivot(10.5F, -144.7024F, -91.8135F));

        ModelPartData cube_r223 = bone.addChild("cube_r223", ModelPartBuilder.create().uv(644, 325).mirrored().cuboid(-36.0F, -6.5F, -1.0F, 72.0F, 13.0F, 2.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(0.5F, 0.0F, 130.3979F, -0.2618F, 0.0F, 0.0F));

        ModelPartData cube_r224 = bone.addChild("cube_r224", ModelPartBuilder.create().uv(644, 325).cuboid(-35.75F, -6.5F, -2.0F, 72.0F, 13.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(-0.25F, 0.2588F, -130.7418F, 2.8798F, 0.0F, 3.1416F));

        ModelPartData cube_r225 = bone.addChild("cube_r225", ModelPartBuilder.create().uv(644, 325).mirrored().cuboid(-36.25F, -6.5F, -2.0F, 72.0F, 13.0F, 2.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(-113.37F, 0.2588F, -65.4319F, 2.8798F, -1.0472F, 3.1416F));

        ModelPartData cube_r226 = bone.addChild("cube_r226", ModelPartBuilder.create().uv(644, 325).mirrored().cuboid(-36.25F, -6.5F, -2.0F, 72.0F, 13.0F, 2.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(-65.3099F, 0.2588F, -113.309F, 2.8798F, -0.5236F, -3.1416F));

        ModelPartData cube_r227 = bone.addChild("cube_r227", ModelPartBuilder.create().uv(644, 325).mirrored().cuboid(-36.25F, -6.5F, -2.0F, 72.0F, 13.0F, 2.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(-131.0528F, 0.2588F, 0.061F, 0.0F, -1.5708F, -0.2618F));

        ModelPartData cube_r228 = bone.addChild("cube_r228", ModelPartBuilder.create().uv(644, 325).mirrored().cuboid(-36.25F, -6.5F, -2.0F, 72.0F, 13.0F, 2.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(-113.62F, 0.2588F, 65.6209F, -0.2618F, -1.0472F, 0.0F));

        ModelPartData cube_r229 = bone.addChild("cube_r229", ModelPartBuilder.create().uv(644, 325).mirrored().cuboid(-36.25F, -6.5F, -2.0F, 72.0F, 13.0F, 2.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(-65.7429F, 0.2588F, 113.681F, -0.2618F, -0.5236F, 0.0F));

        ModelPartData cube_r230 = bone.addChild("cube_r230", ModelPartBuilder.create().uv(644, 325).cuboid(-35.75F, -6.5F, -2.0F, 72.0F, 13.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(65.3099F, 0.2588F, -113.309F, 2.8798F, 0.5236F, 3.1416F));

        ModelPartData cube_r231 = bone.addChild("cube_r231", ModelPartBuilder.create().uv(644, 325).cuboid(-35.75F, -6.5F, -2.0F, 72.0F, 13.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(113.37F, 0.2588F, -65.4319F, 2.8798F, 1.0472F, -3.1416F));

        ModelPartData cube_r232 = bone.addChild("cube_r232", ModelPartBuilder.create().uv(644, 325).cuboid(-35.75F, -6.5F, -2.0F, 72.0F, 13.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(131.0528F, 0.2588F, 0.061F, 0.0F, 1.5708F, 0.2618F));

        ModelPartData cube_r233 = bone.addChild("cube_r233", ModelPartBuilder.create().uv(644, 325).cuboid(-35.75F, -6.5F, -2.0F, 72.0F, 13.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(113.62F, 0.2588F, 65.6209F, -0.2618F, 1.0472F, 0.0F));

        ModelPartData cube_r234 = bone.addChild("cube_r234", ModelPartBuilder.create().uv(644, 325).cuboid(-35.75F, -6.5F, -2.0F, 72.0F, 13.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(65.7429F, 0.2588F, 113.681F, -0.2618F, 0.5236F, 0.0F));

        ModelPartData lights = bone.addChild("lights", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 192.7024F, 0.311F));

        ModelPartData scifistuff = lights.addChild("scifistuff", ModelPartBuilder.create(), ModelTransform.pivot(-131.0528F, -192.4436F, -0.25F));

        ModelPartData cube_r235 = scifistuff.addChild("cube_r235", ModelPartBuilder.create().uv(491, 341).cuboid(-36.25F, -6.5F, 0.5F, 72.0F, 13.0F, 0.0F, new Dilation(0.0F))
        .uv(757, 449).mirrored().cuboid(-32.25F, -4.5F, 0.4F, 64.0F, 9.0F, 0.0F, new Dilation(0.0F)).mirrored(false)
        .uv(721, 153).cuboid(-36.25F, -6.5F, 0.8F, 72.0F, 13.0F, 0.0F, new Dilation(0.0F))
        .uv(721, 167).cuboid(-36.25F, -6.5F, 1.4F, 72.0F, 13.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.5708F, -0.2618F));

        ModelPartData scifistuff2 = lights.addChild("scifistuff2", ModelPartBuilder.create(), ModelTransform.pivot(131.0528F, -192.4436F, -0.25F));

        ModelPartData cube_r236 = scifistuff2.addChild("cube_r236", ModelPartBuilder.create().uv(491, 341).mirrored().cuboid(-35.75F, -6.5F, 0.5F, 72.0F, 13.0F, 0.0F, new Dilation(0.0F)).mirrored(false)
        .uv(757, 449).cuboid(-31.75F, -4.5F, 0.4F, 64.0F, 9.0F, 0.0F, new Dilation(0.0F))
        .uv(721, 153).mirrored().cuboid(-35.75F, -6.5F, 0.8F, 72.0F, 13.0F, 0.0F, new Dilation(0.0F)).mirrored(false)
        .uv(721, 167).mirrored().cuboid(-35.75F, -6.5F, 1.4F, 72.0F, 13.0F, 0.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.5708F, 0.2618F));

        ModelPartData lights2 = bone.addChild("lights2", ModelPartBuilder.create(), ModelTransform.of(0.0F, 192.7024F, 0.311F, 0.0F, 1.0472F, 0.0F));

        ModelPartData scifistuff3 = lights2.addChild("scifistuff3", ModelPartBuilder.create(), ModelTransform.pivot(-131.0528F, -192.4436F, -0.25F));

        ModelPartData cube_r237 = scifistuff3.addChild("cube_r237", ModelPartBuilder.create().uv(491, 341).cuboid(-36.25F, -6.5F, 0.5F, 72.0F, 13.0F, 0.0F, new Dilation(0.0F))
        .uv(757, 449).mirrored().cuboid(-32.25F, -4.5F, 0.4F, 64.0F, 9.0F, 0.0F, new Dilation(0.0F)).mirrored(false)
        .uv(721, 153).cuboid(-36.25F, -6.5F, 0.8F, 72.0F, 13.0F, 0.0F, new Dilation(0.0F))
        .uv(721, 167).cuboid(-36.25F, -6.5F, 1.4F, 72.0F, 13.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.5708F, -0.2618F));

        ModelPartData scifistuff4 = lights2.addChild("scifistuff4", ModelPartBuilder.create(), ModelTransform.pivot(131.0528F, -192.4436F, -0.25F));

        ModelPartData cube_r238 = scifistuff4.addChild("cube_r238", ModelPartBuilder.create().uv(491, 341).mirrored().cuboid(-35.75F, -6.5F, 0.5F, 72.0F, 13.0F, 0.0F, new Dilation(0.0F)).mirrored(false)
        .uv(757, 449).cuboid(-31.75F, -4.5F, 0.4F, 64.0F, 9.0F, 0.0F, new Dilation(0.0F))
        .uv(721, 153).mirrored().cuboid(-35.75F, -6.5F, 0.8F, 72.0F, 13.0F, 0.0F, new Dilation(0.0F)).mirrored(false)
        .uv(721, 167).mirrored().cuboid(-35.75F, -6.5F, 1.4F, 72.0F, 13.0F, 0.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.5708F, 0.2618F));

        ModelPartData lights3 = bone.addChild("lights3", ModelPartBuilder.create(), ModelTransform.of(0.0F, 192.7024F, 0.311F, 0.0F, 2.0944F, 0.0F));

        ModelPartData scifistuff5 = lights3.addChild("scifistuff5", ModelPartBuilder.create(), ModelTransform.pivot(-131.0528F, -192.4436F, -0.25F));

        ModelPartData cube_r239 = scifistuff5.addChild("cube_r239", ModelPartBuilder.create().uv(491, 341).cuboid(-36.25F, -6.5F, 0.5F, 72.0F, 13.0F, 0.0F, new Dilation(0.0F))
        .uv(757, 449).mirrored().cuboid(-32.25F, -4.5F, 0.4F, 64.0F, 9.0F, 0.0F, new Dilation(0.0F)).mirrored(false)
        .uv(721, 153).cuboid(-36.25F, -6.5F, 0.8F, 72.0F, 13.0F, 0.0F, new Dilation(0.0F))
        .uv(721, 167).cuboid(-36.25F, -6.5F, 1.4F, 72.0F, 13.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.5708F, -0.2618F));

        ModelPartData scifistuff6 = lights3.addChild("scifistuff6", ModelPartBuilder.create(), ModelTransform.pivot(131.0528F, -192.4436F, -0.25F));

        ModelPartData cube_r240 = scifistuff6.addChild("cube_r240", ModelPartBuilder.create().uv(491, 341).mirrored().cuboid(-35.75F, -6.5F, 0.5F, 72.0F, 13.0F, 0.0F, new Dilation(0.0F)).mirrored(false)
        .uv(757, 449).cuboid(-31.75F, -4.5F, 0.4F, 64.0F, 9.0F, 0.0F, new Dilation(0.0F))
        .uv(721, 153).mirrored().cuboid(-35.75F, -6.5F, 0.8F, 72.0F, 13.0F, 0.0F, new Dilation(0.0F)).mirrored(false)
        .uv(721, 167).mirrored().cuboid(-35.75F, -6.5F, 1.4F, 72.0F, 13.0F, 0.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.5708F, 0.2618F));

        ModelPartData scifistuffs = bone.addChild("scifistuffs", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 192.7024F, 0.311F));

        ModelPartData morescifistuff = scifistuffs.addChild("morescifistuff", ModelPartBuilder.create(), ModelTransform.pivot(-113.37F, -192.4436F, -65.7429F));

        ModelPartData cube_r241 = morescifistuff.addChild("cube_r241", ModelPartBuilder.create().uv(491, 355).cuboid(-36.25F, -6.5F, 0.9F, 72.0F, 13.0F, 0.0F, new Dilation(0.0F))
        .uv(755, 550).cuboid(-36.25F, -6.5F, 1.2F, 72.0F, 13.0F, 0.0F, new Dilation(0.0F))
        .uv(757, 426).cuboid(-36.25F, -6.5F, 1.9F, 72.0F, 13.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 2.8798F, -1.0472F, 3.1416F));

        ModelPartData morescifistuff2 = scifistuffs.addChild("morescifistuff2", ModelPartBuilder.create(), ModelTransform.pivot(113.37F, -192.4436F, 65.7429F));

        ModelPartData cube_r242 = morescifistuff2.addChild("cube_r242", ModelPartBuilder.create().uv(491, 355).mirrored().cuboid(-35.75F, -6.5F, -0.9F, 72.0F, 13.0F, 0.0F, new Dilation(0.0F)).mirrored(false)
        .uv(755, 550).mirrored().cuboid(-35.75F, -6.5F, -1.2F, 72.0F, 13.0F, 0.0F, new Dilation(0.0F)).mirrored(false)
        .uv(757, 426).mirrored().cuboid(-35.75F, -6.5F, -1.9F, 72.0F, 13.0F, 0.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(0.0F, 0.0F, 0.0F, -2.8798F, -1.0472F, -3.1416F));

        ModelPartData scifistuffs2 = bone.addChild("scifistuffs2", ModelPartBuilder.create(), ModelTransform.of(0.0F, 192.7024F, 0.311F, 0.0F, -1.0472F, 0.0F));

        ModelPartData morescifistuff3 = scifistuffs2.addChild("morescifistuff3", ModelPartBuilder.create(), ModelTransform.pivot(-113.37F, -192.4436F, -65.7429F));

        ModelPartData cube_r243 = morescifistuff3.addChild("cube_r243", ModelPartBuilder.create().uv(491, 355).cuboid(-36.25F, -6.5F, 0.9F, 72.0F, 13.0F, 0.0F, new Dilation(0.0F))
        .uv(755, 550).cuboid(-36.25F, -6.5F, 1.2F, 72.0F, 13.0F, 0.0F, new Dilation(0.0F))
        .uv(757, 426).cuboid(-36.25F, -6.5F, 1.9F, 72.0F, 13.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 2.8798F, -1.0472F, 3.1416F));

        ModelPartData morescifistuff4 = scifistuffs2.addChild("morescifistuff4", ModelPartBuilder.create(), ModelTransform.pivot(113.37F, -192.4436F, 65.7429F));

        ModelPartData cube_r244 = morescifistuff4.addChild("cube_r244", ModelPartBuilder.create().uv(491, 355).mirrored().cuboid(-35.75F, -6.5F, -0.9F, 72.0F, 13.0F, 0.0F, new Dilation(0.0F)).mirrored(false)
        .uv(755, 550).mirrored().cuboid(-35.75F, -6.5F, -1.2F, 72.0F, 13.0F, 0.0F, new Dilation(0.0F)).mirrored(false)
        .uv(757, 426).mirrored().cuboid(-35.75F, -6.5F, -1.9F, 72.0F, 13.0F, 0.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(0.0F, 0.0F, 0.0F, -2.8798F, -1.0472F, -3.1416F));

        ModelPartData scifistuffs3 = bone.addChild("scifistuffs3", ModelPartBuilder.create(), ModelTransform.of(0.0F, 192.7024F, 0.311F, 0.0F, -2.0944F, 0.0F));

        ModelPartData morescifistuff5 = scifistuffs3.addChild("morescifistuff5", ModelPartBuilder.create(), ModelTransform.pivot(-113.37F, -192.4436F, -65.7429F));

        ModelPartData cube_r245 = morescifistuff5.addChild("cube_r245", ModelPartBuilder.create().uv(491, 355).cuboid(-36.25F, -6.5F, 0.9F, 72.0F, 13.0F, 0.0F, new Dilation(0.0F))
        .uv(755, 550).cuboid(-36.25F, -6.5F, 1.2F, 72.0F, 13.0F, 0.0F, new Dilation(0.0F))
        .uv(757, 426).cuboid(-36.25F, -6.5F, 1.9F, 72.0F, 13.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 2.8798F, -1.0472F, 3.1416F));

        ModelPartData morescifistuff6 = scifistuffs3.addChild("morescifistuff6", ModelPartBuilder.create(), ModelTransform.pivot(113.37F, -192.4436F, 65.7429F));

        ModelPartData cube_r246 = morescifistuff6.addChild("cube_r246", ModelPartBuilder.create().uv(491, 355).mirrored().cuboid(-35.75F, -6.5F, -0.9F, 72.0F, 13.0F, 0.0F, new Dilation(0.0F)).mirrored(false)
        .uv(755, 550).mirrored().cuboid(-35.75F, -6.5F, -1.2F, 72.0F, 13.0F, 0.0F, new Dilation(0.0F)).mirrored(false)
        .uv(757, 426).mirrored().cuboid(-35.75F, -6.5F, -1.9F, 72.0F, 13.0F, 0.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(0.0F, 0.0F, 0.0F, -2.8798F, -1.0472F, -3.1416F));

        ModelPartData interior = ship.addChild("interior", ModelPartBuilder.create().uv(0, 0).cuboid(-90.0F, -170.0F, -90.0F, 180.0F, 0.0F, 180.0F, new Dilation(0.0F))
        .uv(147, 450).cuboid(-77.1953F, -213.3072F, 8.0F, 3.0F, 3.0F, 5.0F, new Dilation(0.0F))
        .uv(147, 450).cuboid(-77.1953F, -213.3072F, -16.0F, 3.0F, 3.0F, 5.0F, new Dilation(0.0F))
        .uv(147, 450).cuboid(-77.1953F, -213.3072F, -41.0F, 3.0F, 3.0F, 5.0F, new Dilation(0.0F))
        .uv(0, 181).cuboid(-84.0F, -217.0F, 37.0F, 168.0F, 47.0F, 0.0F, new Dilation(0.0F))
        .uv(486, 229).cuboid(-25.0F, -213.0F, 36.3F, 1.0F, 43.0F, 1.0F, new Dilation(0.0F))
        .uv(164, 450).cuboid(-27.0F, -214.0F, 35.0F, 5.0F, 3.0F, 2.0F, new Dilation(0.0F))
        .uv(486, 229).cuboid(-36.5F, -213.0F, -84.3F, 1.0F, 43.0F, 1.0F, new Dilation(0.0F))
        .uv(667, 567).cuboid(-37.5F, -190.0F, -84.0F, 3.0F, 3.0F, 1.0F, new Dilation(0.0F))
        .uv(486, 229).cuboid(70.0F, -213.0F, 36.3F, 1.0F, 43.0F, 1.0F, new Dilation(0.0F))
        .uv(164, 450).cuboid(68.0F, -214.0F, 35.0F, 5.0F, 3.0F, 2.0F, new Dilation(0.0F))
        .uv(189, 229).cuboid(-50.0F, -217.0F, -84.0F, 104.0F, 47.0F, 0.0F, new Dilation(0.0F))
        .uv(0, 229).cuboid(81.0F, -217.0F, -56.999F, 0.0F, 47.0F, 94.0F, new Dilation(0.0F))
        .uv(735, 605).cuboid(80.0F, -200.0F, -8.7122F, 1.0F, 15.0F, 2.0F, new Dilation(0.001F))
        .uv(886, 636).cuboid(66.0F, -197.0F, 36.0F, 2.0F, 17.0F, 2.0F, new Dilation(0.001F))
        .uv(0, 853).cuboid(68.2074F, -197.2064F, 38.0F, 0.0F, 17.0F, 25.0F, new Dilation(0.0F))
        .uv(886, 636).mirrored().cuboid(26.9584F, -197.0F, 36.0F, 2.0F, 17.0F, 2.0F, new Dilation(0.001F)).mirrored(false)
        .uv(876, 877).cuboid(27.1658F, -197.2064F, 38.0F, 0.0F, 17.0F, 14.0F, new Dilation(0.0F))
        .uv(876, 877).mirrored().cuboid(-27.1658F, -197.2064F, 38.0F, 0.0F, 17.0F, 14.0F, new Dilation(0.0F)).mirrored(false)
        .uv(0, 853).mirrored().cuboid(-68.2074F, -197.2064F, 38.0F, 0.0F, 17.0F, 25.0F, new Dilation(0.0F)).mirrored(false)
        .uv(886, 636).cuboid(-28.9584F, -197.0F, 36.0F, 2.0F, 17.0F, 2.0F, new Dilation(0.001F))
        .uv(886, 636).mirrored().cuboid(-68.0F, -197.0F, 36.0F, 2.0F, 17.0F, 2.0F, new Dilation(0.001F)).mirrored(false)
        .uv(864, 25).cuboid(-11.0F, -208.0373F, 28.7164F, 22.0F, 24.0F, 2.0F, new Dilation(0.0F))
        .uv(864, 52).cuboid(-11.0F, -208.0373F, 28.7164F, 22.0F, 24.0F, 2.0F, new Dilation(0.2F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData cube_r247 = interior.addChild("cube_r247", ModelPartBuilder.create().uv(189, 277).cuboid(-37.5F, -1.1481F, 3.6163F, 75.0F, 92.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -218.4802F, 0.0F, 1.4835F, -0.5236F, 0.0F));

        ModelPartData cube_r248 = interior.addChild("cube_r248", ModelPartBuilder.create().uv(189, 277).mirrored().cuboid(-37.5F, -1.1481F, 3.6163F, 75.0F, 92.0F, 0.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(0.0F, -218.4802F, 0.0F, 0.0F, -1.5708F, 1.4835F));

        ModelPartData cube_r249 = interior.addChild("cube_r249", ModelPartBuilder.create().uv(189, 277).cuboid(-37.5F, -1.1481F, 3.6163F, 75.0F, 92.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -218.4802F, 0.0F, 1.4835F, 0.5236F, 0.0F));

        ModelPartData cube_r250 = interior.addChild("cube_r250", ModelPartBuilder.create().uv(189, 277).mirrored().cuboid(-37.5F, -1.1481F, 3.6163F, 75.0F, 92.0F, 0.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(0.0F, -218.4802F, 0.0F, 0.0F, 1.5708F, -1.4835F));

        ModelPartData cube_r251 = interior.addChild("cube_r251", ModelPartBuilder.create().uv(340, 277).cuboid(-37.5F, -1.1481F, -1.6163F, 75.0F, 92.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0872F, -220.4726F, -0.151F, -1.4835F, -0.5236F, 0.0F));

        ModelPartData cube_r252 = interior.addChild("cube_r252", ModelPartBuilder.create().uv(336, 370).cuboid(-36.5F, -1.1481F, 3.6163F, 73.0F, 92.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -218.4802F, 0.0F, -1.6581F, 1.0472F, -3.1416F));

        ModelPartData cube_r253 = interior.addChild("cube_r253", ModelPartBuilder.create().uv(441, 681).cuboid(-36.5F, 88.9656F, 18.8825F, 73.0F, 47.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -218.9514F, 0.0F, -1.8326F, 1.0472F, -3.1416F));

        ModelPartData cube_r254 = interior.addChild("cube_r254", ModelPartBuilder.create().uv(151, 633).cuboid(-37.5F, 88.9656F, -16.8824F, 75.0F, 47.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.2588F, -220.8833F, -0.4483F, -1.309F, -0.5236F, 0.0F));

        ModelPartData cube_r255 = interior.addChild("cube_r255", ModelPartBuilder.create().uv(0, 371).cuboid(-36.5F, -85.3257F, 2.9924F, 73.0F, 92.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -217.7272F, -84.4332F, -1.4835F, 0.0F, 0.0F));

        ModelPartData cube_r256 = interior.addChild("cube_r256", ModelPartBuilder.create().uv(340, 277).cuboid(-37.5F, -1.1481F, -1.6163F, 75.0F, 92.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(-0.0872F, -220.4726F, -0.151F, -1.4835F, 0.5236F, 0.0F));

        ModelPartData cube_r257 = interior.addChild("cube_r257", ModelPartBuilder.create().uv(336, 370).cuboid(-36.5F, -1.1481F, 3.6163F, 73.0F, 92.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(-0.0001F, -218.4802F, -0.0001F, -1.6581F, -1.0472F, 3.1416F));

        ModelPartData cube_r258 = interior.addChild("cube_r258", ModelPartBuilder.create().uv(880, 361).mirrored().cuboid(5.9146F, 13.8533F, -6.2699F, 17.0F, 15.0F, 2.0F, new Dilation(0.2F)).mirrored(false), ModelTransform.of(-51.0F, -195.7959F, -61.6481F, 2.8798F, 0.0436F, -3.1416F));

        ModelPartData cube_r259 = interior.addChild("cube_r259", ModelPartBuilder.create().uv(864, 79).cuboid(-12.0F, -3.0237F, 54.0996F, 24.0F, 15.0F, 2.0F, new Dilation(0.0F))
        .uv(866, 153).cuboid(-12.0F, -3.0237F, 54.0996F, 24.0F, 15.0F, 2.0F, new Dilation(0.2F)), ModelTransform.of(-12.1349F, -194.9187F, -23.4958F, 2.8798F, -0.7854F, -3.1416F));

        ModelPartData cube_r260 = interior.addChild("cube_r260", ModelPartBuilder.create().uv(349, 867).cuboid(-12.0F, -13.751F, 54.5751F, 24.0F, 15.0F, 2.0F, new Dilation(0.0F))
        .uv(402, 867).cuboid(-12.0F, -13.751F, 54.5751F, 24.0F, 15.0F, 2.0F, new Dilation(0.2F)), ModelTransform.of(-12.1349F, -194.9187F, -23.4958F, -2.8798F, -0.7854F, 3.1416F));

        ModelPartData cube_r261 = interior.addChild("cube_r261", ModelPartBuilder.create().uv(864, 25).cuboid(-11.0F, -12.9187F, 53.0388F, 22.0F, 24.0F, 2.0F, new Dilation(0.0F))
        .uv(864, 52).cuboid(-11.0F, -12.9187F, 53.0388F, 22.0F, 24.0F, 2.0F, new Dilation(0.2F)), ModelTransform.of(-12.1349F, -194.9187F, -23.4958F, -3.1416F, -0.7854F, 3.1416F));

        ModelPartData cube_r262 = interior.addChild("cube_r262", ModelPartBuilder.create().uv(628, 885).cuboid(-31.6532F, -12.9187F, 44.2507F, 2.0F, 24.0F, 2.0F, new Dilation(0.001F)), ModelTransform.of(-12.1349F, -194.9187F, -23.4958F, -3.1416F, -1.1781F, 3.1416F));

        ModelPartData cube_r263 = interior.addChild("cube_r263", ModelPartBuilder.create().uv(886, 449).cuboid(-31.6532F, -1.7492F, 45.611F, 2.0F, 18.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(-12.1349F, -194.9187F, -23.4958F, 2.8798F, -1.1781F, -3.1416F));

        ModelPartData cube_r264 = interior.addChild("cube_r264", ModelPartBuilder.create().uv(886, 656).cuboid(-31.6532F, -17.0256F, 46.0865F, 2.0F, 17.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(-12.1349F, -194.9187F, -23.4958F, -2.8798F, -1.1781F, 3.1416F));

        ModelPartData cube_r265 = interior.addChild("cube_r265", ModelPartBuilder.create().uv(886, 449).mirrored().cuboid(29.6532F, -1.7492F, 45.611F, 2.0F, 18.0F, 2.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(-12.1349F, -194.9187F, -23.4958F, 2.8798F, -0.3927F, -3.1416F));

        ModelPartData cube_r266 = interior.addChild("cube_r266", ModelPartBuilder.create().uv(628, 885).mirrored().cuboid(29.6532F, -12.9187F, 44.2507F, 2.0F, 24.0F, 2.0F, new Dilation(0.001F)).mirrored(false), ModelTransform.of(-12.1349F, -194.9187F, -23.4958F, -3.1416F, -0.3927F, 3.1416F));

        ModelPartData cube_r267 = interior.addChild("cube_r267", ModelPartBuilder.create().uv(886, 656).mirrored().cuboid(29.6532F, -17.0256F, 46.0865F, 2.0F, 17.0F, 2.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(-12.1349F, -194.9187F, -23.4958F, -2.8798F, -0.3927F, -3.1416F));

        ModelPartData cube_r268 = interior.addChild("cube_r268", ModelPartBuilder.create().uv(147, 423).cuboid(-62.2823F, -12.9187F, 29.7259F, 17.0F, 24.0F, 2.0F, new Dilation(0.0F))
        .uv(692, 816).cuboid(-62.2823F, -12.9187F, 29.7259F, 17.0F, 24.0F, 2.0F, new Dilation(0.2F)), ModelTransform.of(-12.1349F, -194.9187F, -23.4958F, 0.0F, -1.5708F, 0.0F));

        ModelPartData cube_r269 = interior.addChild("cube_r269", ModelPartBuilder.create().uv(331, 885).cuboid(-62.2823F, -18.7849F, 32.0566F, 17.0F, 14.0F, 2.0F, new Dilation(0.0F))
        .uv(370, 885).cuboid(-62.2823F, -18.7849F, 32.0566F, 17.0F, 14.0F, 2.0F, new Dilation(0.2F)), ModelTransform.of(-12.1349F, -194.9187F, -23.4958F, 0.0F, -1.5708F, 0.2618F));

        ModelPartData cube_r270 = interior.addChild("cube_r270", ModelPartBuilder.create().uv(872, 133).cuboid(-62.2823F, 3.0101F, 31.5811F, 17.0F, 15.0F, 2.0F, new Dilation(0.0F))
        .uv(880, 361).cuboid(-62.2823F, 3.0101F, 31.5811F, 17.0F, 15.0F, 2.0F, new Dilation(0.2F)), ModelTransform.of(-12.1349F, -194.9187F, -23.4958F, 0.0F, -1.5708F, -0.2618F));

        ModelPartData cube_r271 = interior.addChild("cube_r271", ModelPartBuilder.create().uv(331, 885).mirrored().cuboid(45.2823F, -18.7849F, 32.0566F, 17.0F, 14.0F, 2.0F, new Dilation(0.0F)).mirrored(false)
        .uv(370, 885).mirrored().cuboid(45.2823F, -18.7849F, 32.0566F, 17.0F, 14.0F, 2.0F, new Dilation(0.2F)).mirrored(false), ModelTransform.of(-12.1349F, -194.9187F, -23.4958F, -2.8798F, 0.0F, 3.1416F));

        ModelPartData cube_r272 = interior.addChild("cube_r272", ModelPartBuilder.create().uv(692, 816).mirrored().cuboid(45.2823F, -12.9187F, 29.7259F, 17.0F, 24.0F, 2.0F, new Dilation(0.2F)).mirrored(false)
        .uv(147, 423).mirrored().cuboid(45.2823F, -12.9187F, 29.7259F, 17.0F, 24.0F, 2.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(-12.1349F, -194.9187F, -23.4958F, -3.1416F, 0.0F, 3.1416F));

        ModelPartData cube_r273 = interior.addChild("cube_r273", ModelPartBuilder.create().uv(872, 133).mirrored().cuboid(45.2823F, 3.0101F, 31.5811F, 17.0F, 15.0F, 2.0F, new Dilation(0.0F)).mirrored(false)
        .uv(880, 361).mirrored().cuboid(45.2823F, 3.0101F, 31.5811F, 17.0F, 15.0F, 2.0F, new Dilation(0.2F)).mirrored(false), ModelTransform.of(-12.1349F, -194.9187F, -23.4958F, 2.8798F, 0.0F, -3.1416F));

        ModelPartData cube_r274 = interior.addChild("cube_r274", ModelPartBuilder.create().uv(292, 884).cuboid(-22.748F, -28.1297F, -6.3683F, 17.0F, 14.0F, 2.0F, new Dilation(0.2F)), ModelTransform.of(0.0F, -196.0373F, 31.5876F, 0.2618F, 0.7854F, 0.0F));

        ModelPartData cube_r275 = interior.addChild("cube_r275", ModelPartBuilder.create().uv(864, 115).cuboid(-12.0F, -27.3342F, 0.3325F, 24.0F, 15.0F, 2.0F, new Dilation(0.2F)), ModelTransform.of(0.0F, -196.0373F, 31.5876F, 0.2618F, 0.0F, 0.0F));

        ModelPartData cube_r276 = interior.addChild("cube_r276", ModelPartBuilder.create().uv(292, 884).mirrored().cuboid(5.748F, -28.1297F, -6.3683F, 17.0F, 14.0F, 2.0F, new Dilation(0.2F)).mirrored(false), ModelTransform.of(0.0F, -196.0373F, 31.5876F, 0.2618F, -0.7854F, 0.0F));

        ModelPartData cube_r277 = interior.addChild("cube_r277", ModelPartBuilder.create().uv(692, 816).mirrored().cuboid(5.748F, -12.0F, -9.8084F, 17.0F, 24.0F, 2.0F, new Dilation(0.2F)).mirrored(false), ModelTransform.of(0.0F, -196.0373F, 31.5876F, 0.0F, -0.7854F, 0.0F));

        ModelPartData cube_r278 = interior.addChild("cube_r278", ModelPartBuilder.create().uv(692, 816).cuboid(-22.748F, -12.0F, -9.8084F, 17.0F, 24.0F, 2.0F, new Dilation(0.2F)), ModelTransform.of(0.0F, -196.0373F, 31.5876F, 0.0F, 0.7854F, 0.0F));

        ModelPartData cube_r279 = interior.addChild("cube_r279", ModelPartBuilder.create().uv(880, 361).cuboid(-22.748F, 14.1297F, -6.3683F, 17.0F, 15.0F, 2.0F, new Dilation(0.2F)), ModelTransform.of(0.0F, -196.0373F, 31.5876F, -0.2618F, 0.7854F, 0.0F));

        ModelPartData cube_r280 = interior.addChild("cube_r280", ModelPartBuilder.create().uv(866, 153).cuboid(-12.0F, 12.3342F, 0.3325F, 24.0F, 15.0F, 2.0F, new Dilation(0.2F)), ModelTransform.of(0.0F, -196.0373F, 31.5876F, -0.2618F, 0.0F, 0.0F));

        ModelPartData cube_r281 = interior.addChild("cube_r281", ModelPartBuilder.create().uv(880, 361).mirrored().cuboid(5.748F, 14.1297F, -6.3683F, 17.0F, 15.0F, 2.0F, new Dilation(0.2F)).mirrored(false), ModelTransform.of(0.0F, -196.0373F, 31.5876F, -0.2618F, -0.7854F, 0.0F));

        ModelPartData cube_r282 = interior.addChild("cube_r282", ModelPartBuilder.create().uv(872, 133).mirrored().cuboid(-11.0F, 11.5943F, 3.0939F, 17.0F, 15.0F, 2.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(18.7694F, -196.0373F, 36.5033F, -0.2618F, -0.7854F, 0.0F));

        ModelPartData cube_r283 = interior.addChild("cube_r283", ModelPartBuilder.create().uv(147, 423).mirrored().cuboid(-11.0F, -12.0F, -0.0124F, 17.0F, 24.0F, 2.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(18.7694F, -196.0373F, 36.5033F, 0.0F, -0.7854F, 0.0F));

        ModelPartData cube_r284 = interior.addChild("cube_r284", ModelPartBuilder.create().uv(513, 881).mirrored().cuboid(-11.0F, -25.5943F, 3.0939F, 17.0F, 14.0F, 2.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(18.7694F, -196.0373F, 36.5033F, 0.2618F, -0.7854F, 0.0F));

        ModelPartData cube_r285 = interior.addChild("cube_r285", ModelPartBuilder.create().uv(872, 133).cuboid(-6.0F, 11.5943F, 3.0939F, 17.0F, 15.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(-18.7694F, -196.0373F, 36.5033F, -0.2618F, 0.7854F, 0.0F));

        ModelPartData cube_r286 = interior.addChild("cube_r286", ModelPartBuilder.create().uv(513, 881).cuboid(-6.0F, -25.5943F, 3.0939F, 17.0F, 14.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(-18.7694F, -196.0373F, 36.5033F, 0.2618F, 0.7854F, 0.0F));

        ModelPartData cube_r287 = interior.addChild("cube_r287", ModelPartBuilder.create().uv(147, 423).cuboid(-6.0F, -12.0F, -0.0124F, 17.0F, 24.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(-18.7694F, -196.0373F, 36.5033F, 0.0F, 0.7854F, 0.0F));

        ModelPartData cube_r288 = interior.addChild("cube_r288", ModelPartBuilder.create().uv(886, 616).mirrored().cuboid(-1.0F, -11.0F, -1.0F, 2.0F, 17.0F, 2.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(11.5114F, -213.1258F, 27.9872F, 0.2618F, -0.3927F, 0.0F));

        ModelPartData cube_r289 = interior.addChild("cube_r289", ModelPartBuilder.create().uv(628, 885).mirrored().cuboid(0.5F, -12.0F, 0.0F, 2.0F, 24.0F, 2.0F, new Dilation(0.001F)).mirrored(false), ModelTransform.of(10.0F, -196.0373F, 27.7164F, 0.0F, -0.3927F, 0.0F));

        ModelPartData cube_r290 = interior.addChild("cube_r290", ModelPartBuilder.create().uv(886, 449).mirrored().cuboid(-1.0F, -13.0F, -1.0F, 2.0F, 18.0F, 2.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(12.2047F, -172.1874F, 26.3134F, -0.2618F, -0.3927F, 0.0F));

        ModelPartData cube_r291 = interior.addChild("cube_r291", ModelPartBuilder.create().uv(886, 616).cuboid(-1.0F, -11.0F, -1.0F, 2.0F, 17.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(-11.5114F, -213.1258F, 27.9872F, 0.2618F, 0.3927F, 0.0F));

        ModelPartData cube_r292 = interior.addChild("cube_r292", ModelPartBuilder.create().uv(886, 449).cuboid(-1.0F, -13.0F, -1.0F, 2.0F, 18.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(-12.2047F, -172.1874F, 26.3134F, -0.2618F, 0.3927F, 0.0F));

        ModelPartData cube_r293 = interior.addChild("cube_r293", ModelPartBuilder.create().uv(628, 885).cuboid(-2.5F, -12.0F, 0.0F, 2.0F, 24.0F, 2.0F, new Dilation(0.001F)), ModelTransform.of(-10.0F, -196.0373F, 27.7164F, 0.0F, 0.3927F, 0.0F));

        ModelPartData cube_r294 = interior.addChild("cube_r294", ModelPartBuilder.create().uv(864, 97).cuboid(-12.0F, -8.8294F, 7.6933F, 24.0F, 15.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -212.0065F, 19.6881F, 0.2618F, 0.0F, 0.0F));

        ModelPartData cube_r295 = interior.addChild("cube_r295", ModelPartBuilder.create().uv(864, 79).cuboid(-12.0F, -8.8294F, 7.6933F, 24.0F, 15.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -177.5F, 19.0F, -0.2618F, 0.0F, 0.0F));

        ModelPartData cube_r296 = interior.addChild("cube_r296", ModelPartBuilder.create().uv(886, 636).mirrored().cuboid(-24.724F, -8.1611F, -27.9607F, 2.0F, 17.0F, 2.0F, new Dilation(0.0F)).mirrored(false)
        .uv(886, 636).cuboid(14.3176F, -8.1611F, -27.9607F, 2.0F, 17.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(-15.2467F, -191.7117F, -111.0393F, -3.1416F, 0.0F, -2.3562F));

        ModelPartData cube_r297 = interior.addChild("cube_r297", ModelPartBuilder.create().uv(886, 636).mirrored().cuboid(-23.2533F, -5.2883F, -27.9607F, 2.0F, 17.0F, 2.0F, new Dilation(0.001F)).mirrored(false)
        .uv(886, 636).cuboid(15.7884F, -5.2883F, -27.9607F, 2.0F, 17.0F, 2.0F, new Dilation(0.001F)), ModelTransform.of(-15.2467F, -191.7117F, -111.0393F, -3.1416F, 0.0F, 3.1416F));

        ModelPartData cube_r298 = interior.addChild("cube_r298", ModelPartBuilder.create().uv(886, 636).mirrored().cuboid(-20.1819F, -4.2968F, -27.9607F, 2.0F, 17.0F, 2.0F, new Dilation(0.0F)).mirrored(false)
        .uv(886, 636).cuboid(18.8597F, -4.2968F, -27.9607F, 2.0F, 17.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(-15.2467F, -191.7117F, -111.0393F, 3.1416F, 0.0F, 2.3562F));

        ModelPartData cube_r299 = interior.addChild("cube_r299", ModelPartBuilder.create().uv(886, 636).mirrored().cuboid(-17.3091F, -5.7676F, -27.9607F, 2.0F, 17.0F, 2.0F, new Dilation(0.001F)).mirrored(false), ModelTransform.of(-15.2467F, -191.7117F, -111.0393F, -3.1416F, 0.0F, 1.5708F));

        ModelPartData cube_r300 = interior.addChild("cube_r300", ModelPartBuilder.create().uv(886, 636).mirrored().cuboid(-1.0F, -8.5F, -1.0F, 2.0F, 17.0F, 2.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(-61.2825F, -174.6967F, 37.0F, 0.0F, 0.0F, -0.7854F));

        ModelPartData cube_r301 = interior.addChild("cube_r301", ModelPartBuilder.create().uv(886, 636).mirrored().cuboid(-1.0F, -28.5F, -1.0F, 2.0F, 17.0F, 2.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(-75.4246F, -188.1612F, 37.0F, 0.0F, 0.0F, 0.7854F));

        ModelPartData cube_r302 = interior.addChild("cube_r302", ModelPartBuilder.create().uv(886, 636).mirrored().cuboid(-1.0F, -8.5F, -1.0F, 2.0F, 17.0F, 2.0F, new Dilation(0.001F)).mirrored(false), ModelTransform.of(-47.4792F, -208.0208F, 37.0F, 0.0F, 0.0F, 1.5708F));

        ModelPartData cube_r303 = interior.addChild("cube_r303", ModelPartBuilder.create().uv(886, 636).cuboid(-1.0F, -28.5F, -1.0F, 2.0F, 17.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(-19.5338F, -188.1612F, 37.0F, 0.0F, 0.0F, -0.7854F));

        ModelPartData cube_r304 = interior.addChild("cube_r304", ModelPartBuilder.create().uv(886, 636).cuboid(-1.0F, -8.5F, -1.0F, 2.0F, 17.0F, 2.0F, new Dilation(0.0F))
        .uv(874, 719).cuboid(0.7071F, -8.5F, 1.0F, 0.0F, 17.0F, 14.0F, new Dilation(0.0F)), ModelTransform.of(-33.6759F, -174.6967F, 37.0F, 0.0F, 0.0F, 0.7854F));

        ModelPartData cube_r305 = interior.addChild("cube_r305", ModelPartBuilder.create().uv(637, 843).cuboid(-22.6462F, -6.3742F, -19.0F, 0.0F, 17.0F, 26.0F, new Dilation(0.0F))
        .uv(870, 380).mirrored().cuboid(18.3954F, -6.3742F, -7.0F, 0.0F, 17.0F, 14.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(-22.2861F, -191.7117F, 70.4005F, -1.5708F, 0.7854F, -1.5708F));

        ModelPartData cube_r306 = interior.addChild("cube_r306", ModelPartBuilder.create().uv(690, 844).cuboid(-20.5208F, -5.4947F, -19.0F, 0.0F, 17.0F, 26.0F, new Dilation(0.0F))
        .uv(484, 867).mirrored().cuboid(20.5208F, -5.4947F, -7.0F, 0.0F, 17.0F, 14.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(-22.2861F, -191.7117F, 70.4005F, 0.0F, 1.5708F, 0.0F));

        ModelPartData cube_r307 = interior.addChild("cube_r307", ModelPartBuilder.create().uv(743, 844).cuboid(-18.396F, -6.3756F, -19.0F, 0.0F, 17.0F, 26.0F, new Dilation(0.0F))
        .uv(455, 867).mirrored().cuboid(22.6456F, -6.3756F, -7.0F, 0.0F, 17.0F, 14.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(-22.2861F, -191.7117F, 70.4005F, 1.5708F, 0.7854F, 1.5708F));

        ModelPartData cube_r308 = interior.addChild("cube_r308", ModelPartBuilder.create().uv(249, 859).mirrored().cuboid(-17.5165F, -8.501F, -14.0F, 0.0F, 17.0F, 21.0F, new Dilation(0.01F)).mirrored(false), ModelTransform.of(-22.2861F, -191.7117F, 70.4005F, 1.5708F, 0.0F, 1.5708F));

        ModelPartData cube_r309 = interior.addChild("cube_r309", ModelPartBuilder.create().uv(249, 859).mirrored().cuboid(0.5F, -9.0F, 1.0F, 0.0F, 17.0F, 21.0F, new Dilation(0.01F)).mirrored(false), ModelTransform.of(-48.1856F, -209.7282F, 37.0F, 0.0F, 0.0F, 1.5708F));

        ModelPartData cube_r310 = interior.addChild("cube_r310", ModelPartBuilder.create().uv(521, 835).mirrored().cuboid(-17.5165F, -8.501F, -14.0F, 0.0F, 17.0F, 28.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(-40.2469F, -191.7117F, 62.9609F, 0.7854F, 0.0F, 1.5708F));

        ModelPartData cube_r311 = interior.addChild("cube_r311", ModelPartBuilder.create().uv(143, 767).mirrored().cuboid(-18.396F, -6.3756F, -28.0F, 0.0F, 17.0F, 48.0F, new Dilation(0.0F)).mirrored(false)
        .uv(878, 564).mirrored().cuboid(22.6456F, -6.3756F, -7.0F, 0.0F, 17.0F, 14.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(-40.2469F, -191.7117F, 62.9609F, 0.6155F, 0.5236F, 0.9553F));

        ModelPartData cube_r312 = interior.addChild("cube_r312", ModelPartBuilder.create().uv(240, 767).mirrored().cuboid(-20.5208F, -5.4947F, -28.0F, 0.0F, 17.0F, 48.0F, new Dilation(0.0F)).mirrored(false)
        .uv(878, 681).mirrored().cuboid(20.5208F, -5.4947F, -7.0F, 0.0F, 17.0F, 14.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(-40.2469F, -191.7117F, 62.9609F, 0.0F, 0.7854F, 0.0F));

        ModelPartData cube_r313 = interior.addChild("cube_r313", ModelPartBuilder.create().uv(337, 767).mirrored().cuboid(-22.6462F, -6.3742F, -28.0F, 0.0F, 17.0F, 48.0F, new Dilation(0.0F)).mirrored(false)
        .uv(880, 329).mirrored().cuboid(18.3954F, -6.3742F, -7.0F, 0.0F, 17.0F, 14.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(-40.2469F, -191.7117F, 62.9609F, -0.6155F, 0.5236F, -0.9553F));

        ModelPartData cube_r314 = interior.addChild("cube_r314", ModelPartBuilder.create().uv(796, 858).mirrored().cuboid(1.0607F, -9.5607F, 1.0F, 0.0F, 17.0F, 25.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(-62.1967F, -172.6953F, 37.0F, 0.0F, 0.0F, -0.7854F));

        ModelPartData cube_r315 = interior.addChild("cube_r315", ModelPartBuilder.create().uv(51, 853).mirrored().cuboid(0.7071F, -28.5F, 1.0F, 0.0F, 17.0F, 25.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(-76.8388F, -189.5754F, 37.0F, 0.0F, 0.0F, 0.7854F));

        ModelPartData cube_r316 = interior.addChild("cube_r316", ModelPartBuilder.create().uv(847, 877).cuboid(0.0F, -27.7929F, 1.0F, 0.0F, 17.0F, 14.0F, new Dilation(0.0F)), ModelTransform.of(-19.5338F, -189.574F, 37.0F, 0.0F, 0.0F, -0.7854F));

        ModelPartData cube_r317 = interior.addChild("cube_r317", ModelPartBuilder.create().uv(455, 867).cuboid(-22.6456F, -6.3756F, -7.0F, 0.0F, 17.0F, 14.0F, new Dilation(0.0F))
        .uv(102, 859).cuboid(18.396F, -6.3756F, -19.0F, 0.0F, 17.0F, 24.0F, new Dilation(0.0F)), ModelTransform.of(22.2861F, -191.7117F, 70.4005F, 1.5708F, -0.7854F, -1.5708F));

        ModelPartData cube_r318 = interior.addChild("cube_r318", ModelPartBuilder.create().uv(484, 867).cuboid(-20.5208F, -5.4947F, -7.0F, 0.0F, 17.0F, 14.0F, new Dilation(0.0F))
        .uv(151, 859).cuboid(20.5208F, -5.4947F, -19.0F, 0.0F, 17.0F, 24.0F, new Dilation(0.0F)), ModelTransform.of(22.2861F, -191.7117F, 70.4005F, 0.0F, -1.5708F, 0.0F));

        ModelPartData cube_r319 = interior.addChild("cube_r319", ModelPartBuilder.create().uv(870, 380).cuboid(-18.3954F, -6.3742F, -7.0F, 0.0F, 17.0F, 14.0F, new Dilation(0.0F))
        .uv(200, 859).cuboid(22.6462F, -6.3742F, -19.0F, 0.0F, 17.0F, 24.0F, new Dilation(0.0F)), ModelTransform.of(22.2861F, -191.7117F, 70.4005F, -1.5708F, -0.7854F, 1.5708F));

        ModelPartData cube_r320 = interior.addChild("cube_r320", ModelPartBuilder.create().uv(249, 859).cuboid(17.5165F, -8.501F, -14.0F, 0.0F, 17.0F, 21.0F, new Dilation(0.01F)), ModelTransform.of(22.2861F, -191.7117F, 70.4005F, 1.5708F, 0.0F, -1.5708F));

        ModelPartData cube_r321 = interior.addChild("cube_r321", ModelPartBuilder.create().uv(878, 564).cuboid(-22.6456F, -6.3756F, -7.0F, 0.0F, 17.0F, 14.0F, new Dilation(0.0F))
        .uv(143, 767).cuboid(18.396F, -6.3756F, -28.0F, 0.0F, 17.0F, 48.0F, new Dilation(0.0F)), ModelTransform.of(40.2469F, -191.7117F, 62.9609F, 0.6155F, -0.5236F, -0.9553F));

        ModelPartData cube_r322 = interior.addChild("cube_r322", ModelPartBuilder.create().uv(878, 681).cuboid(-20.5208F, -5.4947F, -7.0F, 0.0F, 17.0F, 14.0F, new Dilation(0.0F))
        .uv(240, 767).cuboid(20.5208F, -5.4947F, -28.0F, 0.0F, 17.0F, 48.0F, new Dilation(0.0F)), ModelTransform.of(40.2469F, -191.7117F, 62.9609F, 0.0F, -0.7854F, 0.0F));

        ModelPartData cube_r323 = interior.addChild("cube_r323", ModelPartBuilder.create().uv(880, 329).cuboid(-18.3954F, -6.3742F, -7.0F, 0.0F, 17.0F, 14.0F, new Dilation(0.0F))
        .uv(337, 767).cuboid(22.6462F, -6.3742F, -28.0F, 0.0F, 17.0F, 48.0F, new Dilation(0.0F)), ModelTransform.of(40.2469F, -191.7117F, 62.9609F, -0.6155F, -0.5236F, 0.9553F));

        ModelPartData cube_r324 = interior.addChild("cube_r324", ModelPartBuilder.create().uv(521, 835).cuboid(17.5165F, -8.501F, -14.0F, 0.0F, 17.0F, 28.0F, new Dilation(0.0F)), ModelTransform.of(40.2469F, -191.7117F, 62.9609F, 0.7854F, 0.0F, -1.5708F));

        ModelPartData cube_r325 = interior.addChild("cube_r325", ModelPartBuilder.create().uv(874, 719).mirrored().cuboid(-0.7071F, -8.5F, 1.0F, 0.0F, 17.0F, 14.0F, new Dilation(0.0F)).mirrored(false)
        .uv(886, 636).mirrored().cuboid(-1.0F, -8.5F, -1.0F, 2.0F, 17.0F, 2.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(33.6759F, -174.6967F, 37.0F, 0.0F, 0.0F, -0.7854F));

        ModelPartData cube_r326 = interior.addChild("cube_r326", ModelPartBuilder.create().uv(847, 877).mirrored().cuboid(0.0F, -27.7929F, 1.0F, 0.0F, 17.0F, 14.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(19.5338F, -189.574F, 37.0F, 0.0F, 0.0F, 0.7854F));

        ModelPartData cube_r327 = interior.addChild("cube_r327", ModelPartBuilder.create().uv(886, 636).mirrored().cuboid(-1.0F, -28.5F, -1.0F, 2.0F, 17.0F, 2.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(19.5338F, -188.1612F, 37.0F, 0.0F, 0.0F, 0.7854F));

        ModelPartData cube_r328 = interior.addChild("cube_r328", ModelPartBuilder.create().uv(51, 853).cuboid(-0.7071F, -28.5F, 1.0F, 0.0F, 17.0F, 25.0F, new Dilation(0.0F)), ModelTransform.of(76.8388F, -189.5754F, 37.0F, 0.0F, 0.0F, -0.7854F));

        ModelPartData cube_r329 = interior.addChild("cube_r329", ModelPartBuilder.create().uv(886, 636).cuboid(-1.0F, -28.5F, -1.0F, 2.0F, 17.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(75.4246F, -188.1612F, 37.0F, 0.0F, 0.0F, -0.7854F));

        ModelPartData cube_r330 = interior.addChild("cube_r330", ModelPartBuilder.create().uv(796, 858).cuboid(-1.0607F, -9.5607F, 1.0F, 0.0F, 17.0F, 25.0F, new Dilation(0.0F)), ModelTransform.of(62.1967F, -172.6953F, 37.0F, 0.0F, 0.0F, 0.7854F));

        ModelPartData cube_r331 = interior.addChild("cube_r331", ModelPartBuilder.create().uv(886, 636).cuboid(-1.0F, -8.5F, -1.0F, 2.0F, 17.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(61.2825F, -174.6967F, 37.0F, 0.0F, 0.0F, 0.7854F));

        ModelPartData cube_r332 = interior.addChild("cube_r332", ModelPartBuilder.create().uv(249, 859).cuboid(-0.5F, -9.0F, 1.0F, 0.0F, 17.0F, 21.0F, new Dilation(0.01F)), ModelTransform.of(48.1856F, -209.7282F, 37.0F, 0.0F, 0.0F, -1.5708F));

        ModelPartData cube_r333 = interior.addChild("cube_r333", ModelPartBuilder.create().uv(886, 636).cuboid(-1.0F, -8.5F, -1.0F, 2.0F, 17.0F, 2.0F, new Dilation(0.001F)), ModelTransform.of(47.4792F, -208.0208F, 37.0F, 0.0F, 0.0F, -1.5708F));

        ModelPartData cube_r334 = interior.addChild("cube_r334", ModelPartBuilder.create().uv(735, 605).cuboid(-0.5F, 20.8744F, 11.3744F, 1.0F, 15.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(80.5F, -191.7175F, -31.5155F, 0.7854F, 0.0F, 0.0F));

        ModelPartData cube_r335 = interior.addChild("cube_r335", ModelPartBuilder.create().uv(735, 605).cuboid(-0.5F, -139.9404F, -94.6917F, 1.0F, 15.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(80.5F, -44.6967F, -30.1013F, -0.7854F, 0.0F, 0.0F));

        ModelPartData cube_r336 = interior.addChild("cube_r336", ModelPartBuilder.create().uv(735, 605).cuboid(-0.5F, -7.5F, -1.0F, 1.0F, 15.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(80.5F, -204.5962F, 21.4906F, -2.3562F, 0.0F, 0.0F));

        ModelPartData cube_r337 = interior.addChild("cube_r337", ModelPartBuilder.create().uv(735, 605).cuboid(-0.5F, -7.5F, -1.0F, 1.0F, 15.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(80.5F, -180.4038F, 21.4906F, 2.3562F, 0.0F, 0.0F));

        ModelPartData cube_r338 = interior.addChild("cube_r338", ModelPartBuilder.create().uv(735, 605).cuboid(-0.5F, -8.0F, -28.0F, 1.0F, 15.0F, 2.0F, new Dilation(0.001F)), ModelTransform.of(80.5F, -182.6066F, 8.8944F, -1.5708F, 0.0F, 0.0F));

        ModelPartData cube_r339 = interior.addChild("cube_r339", ModelPartBuilder.create().uv(735, 605).cuboid(-0.5F, -7.5F, -1.0F, 1.0F, 15.0F, 2.0F, new Dilation(0.001F)), ModelTransform.of(80.5F, -175.3934F, 9.3944F, 1.5708F, 0.0F, 0.0F));

        ModelPartData cube_r340 = interior.addChild("cube_r340", ModelPartBuilder.create().uv(735, 605).cuboid(-0.5F, -7.5F, -1.0F, 1.0F, 15.0F, 2.0F, new Dilation(0.001F)), ModelTransform.of(80.5F, -192.5F, 26.501F, 3.1416F, 0.0F, 0.0F));

        ModelPartData cube_r341 = interior.addChild("cube_r341", ModelPartBuilder.create().uv(398, 229).cuboid(-18.5F, -35.0F, -2.0F, 39.0F, 47.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(65.4957F, -182.0F, -69.5754F, 0.0F, -0.7854F, 0.0F));

        ModelPartData cube_r342 = interior.addChild("cube_r342", ModelPartBuilder.create().uv(667, 567).cuboid(-1.5F, -1.5F, -0.5F, 3.0F, 3.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(70.5F, -188.5F, 36.5F, 0.0F, 3.1416F, 0.0F));

        ModelPartData cube_r343 = interior.addChild("cube_r343", ModelPartBuilder.create().uv(164, 450).cuboid(-2.5F, -1.5F, -1.0F, 5.0F, 3.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(-36.0F, -212.5F, -83.0F, 0.0F, 3.1416F, 0.0F));

        ModelPartData cube_r344 = interior.addChild("cube_r344", ModelPartBuilder.create().uv(667, 567).cuboid(-1.5F, -1.5F, -0.5F, 3.0F, 3.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-24.5F, -188.5F, 36.5F, 0.0F, 3.1416F, 0.0F));

        ModelPartData cube_r345 = interior.addChild("cube_r345", ModelPartBuilder.create().uv(0, 777).cuboid(1.0F, -23.5F, 9.0F, 0.0F, 47.0F, 28.0F, new Dilation(0.0F))
        .uv(599, 426).cuboid(2.0F, -15.5F, 30.6568F, 1.0F, 31.0F, 2.0F, new Dilation(0.0F))
        .uv(726, 767).cuboid(2.5F, -15.5F, 15.0F, 0.0F, 31.0F, 1.0F, new Dilation(0.0F))
        .uv(592, 426).cuboid(2.0F, -15.5F, 13.0F, 1.0F, 31.0F, 2.0F, new Dilation(0.0F))
        .uv(177, 456).cuboid(2.0F, 1.5F, 9.0F, 1.0F, 3.0F, 3.0F, new Dilation(0.0F))
        .uv(486, 229).cuboid(1.4F, -18.5F, 10.0F, 1.0F, 43.0F, 1.0F, new Dilation(0.0F))
        .uv(731, 757).cuboid(2.0F, -23.5F, -5.0F, 0.0F, 47.0F, 39.0F, new Dilation(0.0F))
        .uv(0, 777).cuboid(1.0F, -23.5F, 34.0F, 0.0F, 47.0F, 28.0F, new Dilation(0.0F))
        .uv(599, 426).cuboid(2.0F, -15.5F, 55.6568F, 1.0F, 31.0F, 2.0F, new Dilation(0.0F))
        .uv(726, 767).cuboid(2.5F, -15.5F, 40.0F, 0.0F, 31.0F, 1.0F, new Dilation(0.0F))
        .uv(592, 426).cuboid(2.0F, -15.5F, 38.0F, 1.0F, 31.0F, 2.0F, new Dilation(0.0F))
        .uv(486, 229).cuboid(1.4F, -18.5F, 35.0F, 1.0F, 43.0F, 1.0F, new Dilation(0.0F))
        .uv(57, 777).cuboid(2.0F, -23.5F, 34.0F, 0.0F, 47.0F, 24.0F, new Dilation(0.0F))
        .uv(599, 426).cuboid(2.0F, -15.5F, 79.6568F, 1.0F, 31.0F, 2.0F, new Dilation(0.0F))
        .uv(726, 767).cuboid(2.5F, -15.5F, 64.0F, 0.0F, 31.0F, 1.0F, new Dilation(0.0F))
        .uv(592, 426).cuboid(2.0F, -15.5F, 62.0F, 1.0F, 31.0F, 2.0F, new Dilation(0.0F))
        .uv(0, 777).cuboid(1.0F, -23.5F, 58.0F, 0.0F, 47.0F, 28.0F, new Dilation(0.0F))
        .uv(486, 229).cuboid(1.4F, -18.5F, 59.0F, 1.0F, 43.0F, 1.0F, new Dilation(0.0F))
        .uv(177, 456).cuboid(2.0F, 1.5F, 58.0F, 1.0F, 3.0F, 3.0F, new Dilation(0.0F))
        .uv(635, 767).cuboid(2.0F, -23.5F, 58.0F, 0.0F, 47.0F, 28.0F, new Dilation(0.0F)), ModelTransform.of(-74.0F, -192.5F, -49.0F, 0.0F, 0.0F, -0.2182F));

        ModelPartData cube_r346 = interior.addChild("cube_r346", ModelPartBuilder.create().uv(584, 453).cuboid(-0.5F, 6.5F, -1.0F, 1.0F, 4.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(-76.368F, -214.732F, -27.8683F, -0.7854F, 0.0F, -0.2182F));

        ModelPartData cube_r347 = interior.addChild("cube_r347", ModelPartBuilder.create().uv(664, 887).cuboid(-0.5F, -12.0F, -1.0F, 1.0F, 14.0F, 2.0F, new Dilation(0.0F))
        .uv(578, 843).cuboid(0.0F, -11.0F, 3.0F, 0.0F, 12.0F, 29.0F, new Dilation(0.0F))
        .uv(726, 800).cuboid(0.0F, -12.0F, 1.0F, 0.0F, 14.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-75.3098F, -209.9588F, -31.1716F, -1.5708F, 0.0F, -0.2182F));

        ModelPartData cube_r348 = interior.addChild("cube_r348", ModelPartBuilder.create().uv(584, 453).cuboid(-0.5F, -1.5F, 11.0F, 1.0F, 4.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(-73.3071F, -200.9251F, -27.3033F, 0.7854F, 0.0F, -0.2182F));

        ModelPartData cube_r349 = interior.addChild("cube_r349", ModelPartBuilder.create().uv(577, 453).cuboid(-0.5F, -1.5F, -1.0F, 1.0F, 4.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(-68.1279F, -177.5633F, -18.1109F, -0.7854F, 0.0F, -0.2182F));

        ModelPartData cube_r350 = interior.addChild("cube_r350", ModelPartBuilder.create().uv(671, 887).cuboid(-0.5F, -12.0F, 33.0F, 1.0F, 14.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(-75.1676F, -209.3175F, -31.1716F, -1.5708F, 0.0F, -0.2182F));

        ModelPartData cube_r351 = interior.addChild("cube_r351", ModelPartBuilder.create().uv(570, 453).cuboid(-0.5F, -1.5F, -1.0F, 1.0F, 4.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(-68.1279F, -177.5633F, -34.2322F, 0.7854F, 0.0F, -0.2182F));

        ModelPartData cube_r352 = interior.addChild("cube_r352", ModelPartBuilder.create().uv(186, 423).cuboid(1.5F, -15.5F, 20.0F, 0.0F, 31.0F, 1.0F, new Dilation(0.0F))
        .uv(186, 423).cuboid(1.5F, -15.5F, 45.0F, 0.0F, 31.0F, 1.0F, new Dilation(0.0F))
        .uv(186, 423).cuboid(1.5F, -15.5F, 69.0F, 0.0F, 31.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-73.0237F, -192.7164F, -39.3432F, 0.0F, 0.0F, -0.2182F));

        ModelPartData cube_r353 = interior.addChild("cube_r353", ModelPartBuilder.create().uv(584, 453).cuboid(-0.5F, 6.5F, -1.0F, 1.0F, 4.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(-76.368F, -214.732F, -2.8683F, -0.7854F, 0.0F, -0.2182F));

        ModelPartData cube_r354 = interior.addChild("cube_r354", ModelPartBuilder.create().uv(664, 887).cuboid(-0.5F, -12.0F, -1.0F, 1.0F, 14.0F, 2.0F, new Dilation(0.0F))
        .uv(578, 843).cuboid(0.0F, -11.0F, 3.0F, 0.0F, 12.0F, 29.0F, new Dilation(0.0F))
        .uv(726, 800).cuboid(0.0F, -12.0F, 1.0F, 0.0F, 14.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-75.3098F, -209.9588F, -6.1716F, -1.5708F, 0.0F, -0.2182F));

        ModelPartData cube_r355 = interior.addChild("cube_r355", ModelPartBuilder.create().uv(584, 453).cuboid(-0.5F, -1.5F, 11.0F, 1.0F, 4.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(-73.3071F, -200.9251F, -2.3033F, 0.7854F, 0.0F, -0.2182F));

        ModelPartData cube_r356 = interior.addChild("cube_r356", ModelPartBuilder.create().uv(577, 453).cuboid(-0.5F, -1.5F, -1.0F, 1.0F, 4.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(-68.1279F, -177.5633F, 6.8891F, -0.7854F, 0.0F, -0.2182F));

        ModelPartData cube_r357 = interior.addChild("cube_r357", ModelPartBuilder.create().uv(671, 887).cuboid(-0.5F, -12.0F, 33.0F, 1.0F, 14.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(-75.1676F, -209.3175F, -6.1716F, -1.5708F, 0.0F, -0.2182F));

        ModelPartData cube_r358 = interior.addChild("cube_r358", ModelPartBuilder.create().uv(570, 453).cuboid(-0.5F, -1.5F, -1.0F, 1.0F, 4.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(-68.1279F, -177.5633F, -9.2322F, 0.7854F, 0.0F, -0.2182F));

        ModelPartData cube_r359 = interior.addChild("cube_r359", ModelPartBuilder.create().uv(177, 456).cuboid(-0.5F, -1.5F, -1.5F, 1.0F, 3.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(-70.9099F, -190.1122F, -13.5F, 0.0F, 0.0F, -0.2182F));

        ModelPartData cube_r360 = interior.addChild("cube_r360", ModelPartBuilder.create().uv(671, 887).cuboid(-0.5F, -12.0F, 33.0F, 1.0F, 14.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(-75.1676F, -209.3175F, 17.8284F, -1.5708F, 0.0F, -0.2182F));

        ModelPartData cube_r361 = interior.addChild("cube_r361", ModelPartBuilder.create().uv(678, 887).cuboid(-0.5F, 37.0F, 32.0F, 0.0F, 14.0F, 1.0F, new Dilation(0.0F))
        .uv(678, 887).cuboid(-0.5F, 12.0F, 32.0F, 0.0F, 14.0F, 1.0F, new Dilation(0.0F))
        .uv(678, 887).cuboid(-0.5F, -12.0F, 32.0F, 0.0F, 14.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-74.6795F, -209.4257F, 17.8284F, -1.5708F, 0.0F, -0.2182F));

        ModelPartData cube_r362 = interior.addChild("cube_r362", ModelPartBuilder.create().uv(578, 843).cuboid(0.0F, -11.0F, 3.0F, 0.0F, 12.0F, 29.0F, new Dilation(0.0F))
        .uv(726, 800).cuboid(0.0F, -12.0F, 1.0F, 0.0F, 14.0F, 1.0F, new Dilation(0.0F))
        .uv(664, 887).cuboid(-0.5F, -12.0F, -1.0F, 1.0F, 14.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(-75.3098F, -209.9588F, 17.8284F, -1.5708F, 0.0F, -0.2182F));

        ModelPartData cube_r363 = interior.addChild("cube_r363", ModelPartBuilder.create().uv(584, 453).cuboid(-0.5F, 6.5F, -1.0F, 1.0F, 4.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(-76.368F, -214.732F, 21.1317F, -0.7854F, 0.0F, -0.2182F));

        ModelPartData cube_r364 = interior.addChild("cube_r364", ModelPartBuilder.create().uv(577, 453).cuboid(-0.5F, -1.5F, -1.0F, 1.0F, 4.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(-68.1279F, -177.5633F, 30.8891F, -0.7854F, 0.0F, -0.2182F));

        ModelPartData cube_r365 = interior.addChild("cube_r365", ModelPartBuilder.create().uv(584, 453).cuboid(-0.5F, -1.5F, 11.0F, 1.0F, 4.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(-73.3071F, -200.9251F, 21.6967F, 0.7854F, 0.0F, -0.2182F));

        ModelPartData cube_r366 = interior.addChild("cube_r366", ModelPartBuilder.create().uv(570, 453).cuboid(-0.5F, -1.5F, -1.0F, 1.0F, 4.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(-68.1279F, -177.5633F, 14.7678F, 0.7854F, 0.0F, -0.2182F));

        ModelPartData dial = interior.addChild("dial", ModelPartBuilder.create(), ModelTransform.pivot(71.4511F, -191.113F, -20.4765F));

        ModelPartData dial2 = interior.addChild("dial2", ModelPartBuilder.create(), ModelTransform.pivot(71.4511F, -191.113F, -20.4765F));

        ModelPartData dial3 = interior.addChild("dial3", ModelPartBuilder.create(), ModelTransform.pivot(71.4511F, -191.113F, -20.4765F));

        ModelPartData control = interior.addChild("control", ModelPartBuilder.create(), ModelTransform.pivot(73.8256F, -176.0554F, -31.4765F));

        ModelPartData cube_r367 = control.addChild("cube_r367", ModelPartBuilder.create().uv(409, 885).cuboid(5.5F, -7.1467F, -1.7074F, 8.0F, 14.0F, 5.0F, new Dilation(-0.001F))
        .uv(604, 567).cuboid(-15.5F, -17.1467F, 5.5926F, 31.0F, 7.0F, 0.0F, new Dilation(-0.001F))
        .uv(367, 835).cuboid(-15.5F, -17.1467F, -1.7074F, 31.0F, 24.0F, 7.0F, new Dilation(-0.001F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.5708F, 0.3054F));

        ModelPartData cube_r368 = control.addChild("cube_r368", ModelPartBuilder.create().uv(590, 885).cuboid(-2.1F, -6.0554F, -1.8915F, 4.0F, 11.0F, 5.0F, new Dilation(0.0F))
        .uv(609, 885).cuboid(-2.1F, -6.0554F, -1.8915F, 4.0F, 11.0F, 5.0F, new Dilation(0.2F))
        .uv(796, 844).cuboid(-8.5F, -6.0554F, 1.0085F, 2.0F, 11.0F, 2.0F, new Dilation(0.0F))
        .uv(444, 835).cuboid(-15.5F, -17.9446F, -7.1085F, 31.0F, 24.0F, 7.0F, new Dilation(0.0F))
        .uv(773, 415).cuboid(-15.5F, 4.9446F, -0.8915F, 31.0F, 1.0F, 8.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.5708F, 0.0F));

        ModelPartData cube_r369 = control.addChild("cube_r369", ModelPartBuilder.create().uv(147, 459).cuboid(-1.0F, 4.5F, -1.0F, 2.0F, 1.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(4.4915F, -5.0554F, -7.5F, 0.0F, -1.5708F, 1.5708F));

        ModelPartData cube_r370 = control.addChild("cube_r370", ModelPartBuilder.create().uv(147, 459).cuboid(-1.0F, 4.5F, -1.0F, 2.0F, 1.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(4.4915F, -5.0554F, -10.5F, 0.0F, -1.5708F, 1.5708F));

        ModelPartData cube_r371 = control.addChild("cube_r371", ModelPartBuilder.create().uv(796, 844).cuboid(-8.5F, -6.0554F, 1.0085F, 2.0F, 11.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, -3.0F, 0.0F, -1.5708F, 0.0F));

        ModelPartData cube_r372 = control.addChild("cube_r372", ModelPartBuilder.create().uv(147, 420).cuboid(-0.5F, -0.5F, 0.0F, 3.0F, 1.0F, 0.0F, new Dilation(-0.001F))
        .uv(147, 420).cuboid(-0.5F, -11.5F, 0.0F, 3.0F, 1.0F, 0.0F, new Dilation(-0.001F))
        .uv(147, 420).cuboid(-0.5F, -22.5F, 0.0F, 3.0F, 1.0F, 0.0F, new Dilation(-0.001F)), ModelTransform.of(-2.3746F, -15.0577F, 11.0F, 1.5708F, 0.0F, -1.2654F));

        ModelPartData cube_r373 = control.addChild("cube_r373", ModelPartBuilder.create().uv(164, 456).cuboid(-2.5F, -2.5F, -1.0F, 5.0F, 5.0F, 1.0F, new Dilation(-0.001F)), ModelTransform.of(-1.8977F, -14.9073F, -11.0F, 1.5708F, -0.7854F, -1.2654F));

        ModelPartData cube_r374 = control.addChild("cube_r374", ModelPartBuilder.create().uv(667, 567).cuboid(-1.5F, -1.5F, -0.5F, 3.0F, 3.0F, 1.0F, new Dilation(-0.001F)), ModelTransform.of(-1.4209F, -14.7569F, -5.5F, 1.5708F, 0.0F, -1.2654F));

        ModelPartData cube_r375 = control.addChild("cube_r375", ModelPartBuilder.create().uv(667, 567).cuboid(-1.5F, -1.5F, -0.5F, 3.0F, 3.0F, 1.0F, new Dilation(-0.001F)), ModelTransform.of(-1.4209F, -14.7569F, 5.5F, 1.5708F, 0.0F, -1.2654F));

        ModelPartData cube_r376 = control.addChild("cube_r376", ModelPartBuilder.create().uv(164, 456).cuboid(-2.5F, -2.5F, -1.0F, 5.0F, 5.0F, 1.0F, new Dilation(-0.001F)), ModelTransform.of(-1.8977F, -14.9073F, 0.0F, 1.5708F, -0.7854F, -1.2654F));

        ModelPartData cube_r377 = control.addChild("cube_r377", ModelPartBuilder.create().uv(164, 456).cuboid(-2.5F, -2.5F, -1.0F, 5.0F, 5.0F, 1.0F, new Dilation(-0.001F)), ModelTransform.of(-1.8977F, -14.9073F, 11.0F, 1.5708F, -0.7854F, -1.2654F));

        ModelPartData control2 = interior.addChild("control2", ModelPartBuilder.create(), ModelTransform.of(51.7109F, -185.2224F, -32.8818F, 0.0F, 0.7854F, 0.0F));

        ModelPartData cube_r378 = control2.addChild("cube_r378", ModelPartBuilder.create().uv(409, 885).cuboid(5.5F, -7.1467F, -1.7074F, 8.0F, 14.0F, 5.0F, new Dilation(-0.001F))
        .uv(604, 567).cuboid(-15.5F, -17.1467F, 5.5926F, 31.0F, 7.0F, 0.0F, new Dilation(-0.001F))
        .uv(367, 835).cuboid(-15.5F, -17.1467F, -1.7074F, 31.0F, 24.0F, 7.0F, new Dilation(-0.001F)), ModelTransform.of(30.5862F, 9.166F, -16.1999F, 0.0F, -1.5708F, 0.3054F));

        ModelPartData cube_r379 = control2.addChild("cube_r379", ModelPartBuilder.create().uv(590, 885).cuboid(-2.1F, -6.0554F, -1.8915F, 4.0F, 11.0F, 5.0F, new Dilation(0.0F))
        .uv(609, 885).cuboid(-2.1F, -6.0554F, -1.8915F, 4.0F, 11.0F, 5.0F, new Dilation(0.2F))
        .uv(796, 844).cuboid(-8.5F, -6.0554F, 1.0085F, 2.0F, 11.0F, 2.0F, new Dilation(0.0F))
        .uv(444, 835).cuboid(-15.5F, -17.9446F, -7.1085F, 31.0F, 24.0F, 7.0F, new Dilation(0.0F))
        .uv(773, 415).cuboid(-15.5F, 4.9446F, -0.8915F, 31.0F, 1.0F, 8.0F, new Dilation(0.001F)), ModelTransform.of(30.5862F, 9.166F, -16.1999F, 0.0F, -1.5708F, 0.0F));

        ModelPartData cube_r380 = control2.addChild("cube_r380", ModelPartBuilder.create().uv(147, 459).cuboid(-1.0F, 4.5F, -1.0F, 2.0F, 1.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(35.0777F, 4.1107F, -23.6999F, 0.0F, -1.5708F, 1.5708F));

        ModelPartData cube_r381 = control2.addChild("cube_r381", ModelPartBuilder.create().uv(147, 459).cuboid(-1.0F, 4.5F, -1.0F, 2.0F, 1.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(35.0777F, 4.1107F, -26.6999F, 0.0F, -1.5708F, 1.5708F));

        ModelPartData cube_r382 = control2.addChild("cube_r382", ModelPartBuilder.create().uv(796, 844).cuboid(-8.5F, -6.0554F, 1.0085F, 2.0F, 11.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(30.5862F, 9.166F, -19.1999F, 0.0F, -1.5708F, 0.0F));

        ModelPartData cube_r383 = control2.addChild("cube_r383", ModelPartBuilder.create().uv(147, 420).cuboid(-0.5F, -0.5F, 0.0F, 3.0F, 1.0F, 0.0F, new Dilation(-0.001F))
        .uv(147, 420).cuboid(-0.5F, -11.5F, 0.0F, 3.0F, 1.0F, 0.0F, new Dilation(-0.001F))
        .uv(147, 420).cuboid(-0.5F, -22.5F, 0.0F, 3.0F, 1.0F, 0.0F, new Dilation(-0.001F)), ModelTransform.of(28.2116F, -5.8916F, -5.1999F, 1.5708F, 0.0F, -1.2654F));

        ModelPartData cube_r384 = control2.addChild("cube_r384", ModelPartBuilder.create().uv(164, 456).cuboid(-2.5F, -2.5F, -1.0F, 5.0F, 5.0F, 1.0F, new Dilation(-0.001F)), ModelTransform.of(28.6884F, -5.7413F, -27.1999F, 1.5708F, -0.7854F, -1.2654F));

        ModelPartData cube_r385 = control2.addChild("cube_r385", ModelPartBuilder.create().uv(667, 567).cuboid(-1.5F, -1.5F, -0.5F, 3.0F, 3.0F, 1.0F, new Dilation(-0.001F)), ModelTransform.of(29.1653F, -5.5909F, -21.6999F, 1.5708F, 0.0F, -1.2654F));

        ModelPartData cube_r386 = control2.addChild("cube_r386", ModelPartBuilder.create().uv(667, 567).cuboid(-1.5F, -1.5F, -0.5F, 3.0F, 3.0F, 1.0F, new Dilation(-0.001F)), ModelTransform.of(29.1653F, -5.5909F, -10.6999F, 1.5708F, 0.0F, -1.2654F));

        ModelPartData cube_r387 = control2.addChild("cube_r387", ModelPartBuilder.create().uv(164, 456).cuboid(-2.5F, -2.5F, -1.0F, 5.0F, 5.0F, 1.0F, new Dilation(-0.001F)), ModelTransform.of(28.6884F, -5.7413F, -16.1999F, 1.5708F, -0.7854F, -1.2654F));

        ModelPartData cube_r388 = control2.addChild("cube_r388", ModelPartBuilder.create().uv(164, 456).cuboid(-2.5F, -2.5F, -1.0F, 5.0F, 5.0F, 1.0F, new Dilation(-0.001F)), ModelTransform.of(28.6884F, -5.7413F, -5.1999F, 1.5708F, -0.7854F, -1.2654F));

        ModelPartData control3 = interior.addChild("control3", ModelPartBuilder.create(), ModelTransform.of(60.1015F, -185.2234F, -66.17F, 0.0F, 1.5708F, 0.0F));

        ModelPartData cube_r389 = control3.addChild("cube_r389", ModelPartBuilder.create().uv(409, 885).cuboid(5.5F, -7.1467F, -1.7074F, 8.0F, 14.0F, 5.0F, new Dilation(-0.001F))
        .uv(604, 567).cuboid(-15.5F, -17.1467F, 5.5926F, 31.0F, 7.0F, 0.0F, new Dilation(-0.001F))
        .uv(367, 835).cuboid(-15.5F, -17.1467F, -1.7074F, 31.0F, 24.0F, 7.0F, new Dilation(-0.001F)), ModelTransform.of(10.7364F, 9.167F, -32.7037F, 0.0F, -1.5708F, 0.3054F));

        ModelPartData cube_r390 = control3.addChild("cube_r390", ModelPartBuilder.create().uv(590, 885).cuboid(-2.1F, -6.0554F, -1.8915F, 4.0F, 11.0F, 5.0F, new Dilation(0.0F))
        .uv(609, 885).cuboid(-2.1F, -6.0554F, -1.8915F, 4.0F, 11.0F, 5.0F, new Dilation(0.2F))
        .uv(796, 844).cuboid(-8.5F, -6.0554F, 1.0085F, 2.0F, 11.0F, 2.0F, new Dilation(0.0F))
        .uv(444, 835).cuboid(-15.5F, -17.9446F, -7.1085F, 31.0F, 24.0F, 7.0F, new Dilation(0.0F))
        .uv(773, 415).cuboid(-15.5F, 4.9446F, -0.8915F, 31.0F, 1.0F, 8.0F, new Dilation(0.001F)), ModelTransform.of(10.7364F, 9.167F, -32.7037F, 0.0F, -1.5708F, 0.0F));

        ModelPartData cube_r391 = control3.addChild("cube_r391", ModelPartBuilder.create().uv(147, 459).cuboid(-1.0F, 4.5F, -1.0F, 2.0F, 1.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(15.2279F, 4.1116F, -40.2037F, 0.0F, -1.5708F, 1.5708F));

        ModelPartData cube_r392 = control3.addChild("cube_r392", ModelPartBuilder.create().uv(147, 459).cuboid(-1.0F, 4.5F, -1.0F, 2.0F, 1.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(15.2279F, 4.1116F, -43.2037F, 0.0F, -1.5708F, 1.5708F));

        ModelPartData cube_r393 = control3.addChild("cube_r393", ModelPartBuilder.create().uv(796, 844).cuboid(-8.5F, -6.0554F, 1.0085F, 2.0F, 11.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(10.7364F, 9.167F, -35.7037F, 0.0F, -1.5708F, 0.0F));

        ModelPartData cube_r394 = control3.addChild("cube_r394", ModelPartBuilder.create().uv(147, 420).cuboid(-0.5F, -0.5F, 0.0F, 3.0F, 1.0F, 0.0F, new Dilation(-0.001F))
        .uv(147, 420).cuboid(-0.5F, -11.5F, 0.0F, 3.0F, 1.0F, 0.0F, new Dilation(-0.001F))
        .uv(147, 420).cuboid(-0.5F, -22.5F, 0.0F, 3.0F, 1.0F, 0.0F, new Dilation(-0.001F)), ModelTransform.of(8.3618F, -5.8906F, -21.7037F, 1.5708F, 0.0F, -1.2654F));

        ModelPartData cube_r395 = control3.addChild("cube_r395", ModelPartBuilder.create().uv(164, 456).cuboid(-2.5F, -2.5F, -1.0F, 5.0F, 5.0F, 1.0F, new Dilation(-0.001F)), ModelTransform.of(8.8386F, -5.7403F, -43.7037F, 1.5708F, -0.7854F, -1.2654F));

        ModelPartData cube_r396 = control3.addChild("cube_r396", ModelPartBuilder.create().uv(667, 567).cuboid(-1.5F, -1.5F, -0.5F, 3.0F, 3.0F, 1.0F, new Dilation(-0.001F)), ModelTransform.of(9.3155F, -5.5899F, -38.2037F, 1.5708F, 0.0F, -1.2654F));

        ModelPartData cube_r397 = control3.addChild("cube_r397", ModelPartBuilder.create().uv(667, 567).cuboid(-1.5F, -1.5F, -0.5F, 3.0F, 3.0F, 1.0F, new Dilation(-0.001F)), ModelTransform.of(9.3155F, -5.5899F, -27.2037F, 1.5708F, 0.0F, -1.2654F));

        ModelPartData cube_r398 = control3.addChild("cube_r398", ModelPartBuilder.create().uv(164, 456).cuboid(-2.5F, -2.5F, -1.0F, 5.0F, 5.0F, 1.0F, new Dilation(-0.001F)), ModelTransform.of(8.8386F, -5.7403F, -32.7037F, 1.5708F, -0.7854F, -1.2654F));

        ModelPartData cube_r399 = control3.addChild("cube_r399", ModelPartBuilder.create().uv(164, 456).cuboid(-2.5F, -2.5F, -1.0F, 5.0F, 5.0F, 1.0F, new Dilation(-0.001F)), ModelTransform.of(8.8386F, -5.7403F, -21.7037F, 1.5708F, -0.7854F, -1.2654F));

        ModelPartData screenblob3 = interior.addChild("screenblob3", ModelPartBuilder.create().uv(434, 767).cuboid(-3.1F, -16.5F, -20.5F, 0.0F, 34.0F, 33.0F, new Dilation(0.0F)), ModelTransform.pivot(84.0F, -193.5F, 13.501F));

        ModelPartData screenblob2 = interior.addChild("screenblob2", ModelPartBuilder.create().uv(501, 767).cuboid(-3.2F, -16.5F, -20.5F, 0.0F, 34.0F, 33.0F, new Dilation(0.0F)), ModelTransform.pivot(84.0F, -195.5F, 13.501F));

        ModelPartData screenblob = interior.addChild("screenblob", ModelPartBuilder.create().uv(568, 767).cuboid(0.0F, -17.0F, -15.5F, 0.0F, 34.0F, 33.0F, new Dilation(0.0F)), ModelTransform.pivot(80.7F, -193.0F, 8.501F));

        ModelPartData beam = interior.addChild("beam", ModelPartBuilder.create(), ModelTransform.pivot(77.0749F, -192.7635F, -10.999F));

        ModelPartData cube_r400 = beam.addChild("cube_r400", ModelPartBuilder.create().uv(717, 767).cuboid(-2.0F, -23.5F, 0.0F, 4.0F, 47.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.5708F, -0.2182F));

        ModelPartData cube_r401 = beam.addChild("cube_r401", ModelPartBuilder.create().uv(477, 229).cuboid(-2.0F, -23.5F, 0.0F, 4.0F, 47.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.2182F));

        ModelPartData cube_r402 = beam.addChild("cube_r402", ModelPartBuilder.create().uv(172, 371).cuboid(-5.0F, -23.5F, -2.0F, 4.0F, 47.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(2.9289F, -0.6493F, 0.0F, 0.0F, 0.0F, -0.2182F));

        ModelPartData beam3 = interior.addChild("beam3", ModelPartBuilder.create(), ModelTransform.of(77.0749F, -192.7635F, -10.999F, 0.0F, 0.3927F, 0.0F));

        ModelPartData cube_r403 = beam3.addChild("cube_r403", ModelPartBuilder.create().uv(132, 777).cuboid(-2.0F, -23.5F, 0.0F, 4.0F, 44.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(17.3245F, 0.1293F, -40.9964F, 0.0F, -1.5708F, -0.2182F));

        ModelPartData cube_r404 = beam3.addChild("cube_r404", ModelPartBuilder.create().uv(123, 777).cuboid(-2.0F, -23.5F, 0.0F, 4.0F, 44.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(17.3245F, 0.1293F, -40.9964F, 0.0F, 0.0F, -0.2182F));

        ModelPartData cube_r405 = beam3.addChild("cube_r405", ModelPartBuilder.create().uv(106, 777).cuboid(-5.0F, -23.5F, -2.0F, 4.0F, 44.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(20.2534F, -0.52F, -40.9964F, 0.0F, 0.0F, -0.2182F));

        ModelPartData beam4 = interior.addChild("beam4", ModelPartBuilder.create(), ModelTransform.of(56.3445F, -192.6342F, -106.3179F, 0.0F, 1.2217F, 0.0F));

        ModelPartData cube_r406 = beam4.addChild("cube_r406", ModelPartBuilder.create().uv(717, 767).cuboid(-2.0F, -23.5F, 0.0F, 4.0F, 47.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(-25.3109F, -0.3457F, 3.1173F, 0.0F, -1.5708F, -0.2182F));

        ModelPartData cube_r407 = beam4.addChild("cube_r407", ModelPartBuilder.create().uv(477, 229).cuboid(-2.0F, -23.5F, 0.0F, 4.0F, 47.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(-25.3109F, -0.3457F, 3.1173F, 0.0F, 0.0F, -0.2182F));

        ModelPartData cube_r408 = beam4.addChild("cube_r408", ModelPartBuilder.create().uv(172, 371).cuboid(-5.0F, -23.5F, -2.0F, 4.0F, 47.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(-22.382F, -0.995F, 3.1173F, 0.0F, 0.0F, -0.2182F));

        ModelPartData beam2 = interior.addChild("beam2", ModelPartBuilder.create(), ModelTransform.pivot(77.0749F, -192.7635F, 30.001F));

        ModelPartData cube_r409 = beam2.addChild("cube_r409", ModelPartBuilder.create().uv(717, 767).cuboid(-2.0F, -23.5F, 0.0F, 4.0F, 47.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.5708F, -0.2182F));

        ModelPartData cube_r410 = beam2.addChild("cube_r410", ModelPartBuilder.create().uv(477, 229).cuboid(-2.0F, -23.5F, 0.0F, 4.0F, 47.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.2182F));

        ModelPartData cube_r411 = beam2.addChild("cube_r411", ModelPartBuilder.create().uv(172, 371).cuboid(-5.0F, -23.5F, -2.0F, 4.0F, 47.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(2.9289F, -0.6493F, 0.0F, 0.0F, 0.0F, -0.2182F));

        ModelPartData greencubecage = interior.addChild("greencubecage", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, -221.0F, 0.0F));

        ModelPartData cube_r412 = greencubecage.addChild("cube_r412", ModelPartBuilder.create().uv(864, 0).cuboid(-7.0F, -5.0F, -7.0F, 14.0F, 10.0F, 14.0F, new Dilation(0.3F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -0.7854F, 0.0F));

        ModelPartData greencube = interior.addChild("greencube", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, -221.0F, 0.0F));

        ModelPartData cube_r413 = greencube.addChild("cube_r413", ModelPartBuilder.create().uv(292, 859).cuboid(-7.0F, -5.0F, -7.0F, 14.0F, 10.0F, 14.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -0.7854F, 0.0F));

        ModelPartData frame = ship.addChild("frame", ModelPartBuilder.create().uv(735, 605).cuboid(-0.5F, -8.2825F, -24.8033F, 1.0F, 15.0F, 2.0F, new Dilation(0.001F)), ModelTransform.of(-40.0F, -185.7175F, 84.5155F, 0.0F, -1.5708F, 0.0F));

        ModelPartData cube_r414 = frame.addChild("cube_r414", ModelPartBuilder.create().uv(735, 605).cuboid(-0.5F, 20.8744F, -13.3744F, 1.0F, 15.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, -0.7854F, 0.0F, 0.0F));

        ModelPartData cube_r415 = frame.addChild("cube_r415", ModelPartBuilder.create().uv(735, 605).cuboid(-0.5F, -139.9404F, 92.6916F, 1.0F, 15.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 147.0208F, -1.4142F, 0.7854F, 0.0F, 0.0F));

        ModelPartData cube_r416 = frame.addChild("cube_r416", ModelPartBuilder.create().uv(735, 605).cuboid(-0.5F, -7.5F, -1.0F, 1.0F, 15.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -12.8787F, -53.0061F, 2.3562F, 0.0F, 0.0F));

        ModelPartData cube_r417 = frame.addChild("cube_r417", ModelPartBuilder.create().uv(735, 605).cuboid(-0.5F, -7.5F, -1.0F, 1.0F, 15.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 11.3137F, -53.0061F, -2.3562F, 0.0F, 0.0F));

        ModelPartData cube_r418 = frame.addChild("cube_r418", ModelPartBuilder.create().uv(735, 605).cuboid(-0.5F, -8.0F, 26.0F, 1.0F, 15.0F, 2.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 9.1109F, -40.4099F, 1.5708F, 0.0F, 0.0F));

        ModelPartData cube_r419 = frame.addChild("cube_r419", ModelPartBuilder.create().uv(735, 605).cuboid(-0.5F, -7.5F, -1.0F, 1.0F, 15.0F, 2.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, -0.7825F, -58.0165F, -3.1416F, 0.0F, 0.0F));

        ModelPartData door = ship.addChild("door", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, -167.8067F, 83.0F));

        ModelPartData cube_r420 = door.addChild("cube_r420", ModelPartBuilder.create().uv(0, 512).cuboid(-35.5F, -122.78F, 32.0949F, 71.0F, 6.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -10.0F, -83.0F, -1.309F, 0.0F, 0.0F));

        ModelPartData cube_r421 = door.addChild("cube_r421", ModelPartBuilder.create().uv(0, 681).cuboid(-36.5F, -127.898F, -22.9903F, 73.0F, 47.0F, 0.0F, new Dilation(0.005F))
        .uv(681, 181).cuboid(-36.5F, -127.898F, -24.5903F, 73.0F, 47.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -2.1165F, -83.0F, -1.8326F, 0.0F, 3.1416F));
        return TexturedModelData.of(modelData, 1024, 1024);
    }
    @Override
    public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
        this.ship.getChild("door").pitch = 0f;
        ship.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
    }

    @Override
    public ModelPart getPart() {
        return ship;
    }

    @Override
    public void setAngles(Entity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {

    }
}