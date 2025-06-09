package dev.amble.ait.client.models.consoles;

import dev.amble.ait.client.animation.console.steam.SteamAnimations;
import dev.amble.ait.client.tardis.ClientTardis;
import dev.amble.ait.core.blockentities.ConsoleBlockEntity;
import dev.amble.ait.core.tardis.control.impl.pos.IncrementManager;
import dev.amble.ait.core.tardis.handler.travel.TravelHandlerBase;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.animation.Animation;
import net.minecraft.client.util.math.MatrixStack;

public class SteamConsoleModel extends ConsoleModel {
    private final ModelPart steam;

    public SteamConsoleModel(ModelPart root) {
        this.steam = root.getChild("steam");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData steam = modelPartData.addChild("steam", ModelPartBuilder.create(),
                ModelTransform.pivot(0.0F, 24.0F, 0.0F));

        ModelPartData base = steam.addChild("base", ModelPartBuilder.create(),
                ModelTransform.of(0.0F, -1.5F, 0.0F, 0.0F, -1.5708F, 0.0F));

        ModelPartData cube_r1 = base.addChild("cube_r1",
                ModelPartBuilder.create().uv(50, 14)
                        .cuboid(-8.0F, -1.3F, -13.85F, 16.0F, 0.0F, 14.0F, new Dilation(0.0F)).uv(0, 47)
                        .cuboid(-8.0F, -1.0F, -13.85F, 16.0F, 1.0F, 14.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.1309F, 0.0F, 0.0F));

        ModelPartData base2 = base.addChild("base2", ModelPartBuilder.create(),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData cube_r2 = base2.addChild("cube_r2",
                ModelPartBuilder.create().uv(50, 14)
                        .cuboid(-8.0F, -1.3F, -13.85F, 16.0F, 0.0F, 14.0F, new Dilation(0.0F)).uv(0, 47)
                        .cuboid(-8.0F, -1.0F, -13.85F, 16.0F, 1.0F, 14.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.1309F, 0.0F, 0.0F));

        ModelPartData base3 = base2.addChild("base3", ModelPartBuilder.create(),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData cube_r3 = base3.addChild("cube_r3",
                ModelPartBuilder.create().uv(50, 14)
                        .cuboid(-8.0F, -1.3F, -13.85F, 16.0F, 0.0F, 14.0F, new Dilation(0.0F)).uv(0, 47)
                        .cuboid(-8.0F, -1.0F, -13.85F, 16.0F, 1.0F, 14.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.1309F, 0.0F, 0.0F));

        ModelPartData base4 = base3.addChild("base4", ModelPartBuilder.create(),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData cube_r4 = base4.addChild("cube_r4",
                ModelPartBuilder.create().uv(50, 14)
                        .cuboid(-8.0F, -1.3F, -13.85F, 16.0F, 0.0F, 14.0F, new Dilation(0.0F)).uv(0, 47)
                        .cuboid(-8.0F, -1.0F, -13.85F, 16.0F, 1.0F, 14.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.1309F, 0.0F, 0.0F));

        ModelPartData base5 = base4.addChild("base5", ModelPartBuilder.create(),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData cube_r5 = base5.addChild("cube_r5",
                ModelPartBuilder.create().uv(50, 14)
                        .cuboid(-8.0F, -1.3F, -13.85F, 16.0F, 0.0F, 14.0F, new Dilation(0.0F)).uv(0, 47)
                        .cuboid(-8.0F, -1.0F, -13.85F, 16.0F, 1.0F, 14.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.1309F, 0.0F, 0.0F));

        ModelPartData base6 = base5.addChild("base6", ModelPartBuilder.create(),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData cube_r6 = base6.addChild("cube_r6",
                ModelPartBuilder.create().uv(50, 14)
                        .cuboid(-8.0F, -1.3F, -13.85F, 16.0F, 0.0F, 14.0F, new Dilation(0.0F)).uv(0, 47)
                        .cuboid(-8.0F, -1.0F, -13.85F, 16.0F, 1.0F, 14.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.1309F, 0.0F, 0.0F));

        ModelPartData dividers = steam.addChild("dividers",
                ModelPartBuilder.create().uv(97, 90)
                        .cuboid(-2.0F, -1.9742F, -15.9783F, 4.0F, 3.0F, 3.0F, new Dilation(0.01F)).uv(97, 14)
                        .cuboid(-1.0F, -14.75F, -20.0F, 2.0F, 3.0F, 5.0F, new Dilation(0.0F)).uv(0, 33)
                        .cuboid(-0.5F, -15.75F, -21.0F, 1.0F, 5.0F, 5.0F, new Dilation(0.001F)),
                ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData cube_r7 = dividers.addChild("cube_r7",
                ModelPartBuilder.create().uv(66, 61).cuboid(-2.0F, 0.0F, -14.0F, 2.0F, 2.0F, 14.0F, new Dilation(0.0F)),
                ModelTransform.of(1.0F, -11.4504F, -3.9606F, -0.1745F, 0.0F, 0.0F));

        ModelPartData cube_r8 = dividers.addChild("cube_r8",
                ModelPartBuilder.create().uv(72, 78)
                        .cuboid(-0.5F, -3.019F, -16.4358F, 1.0F, 2.0F, 12.0F, new Dilation(0.0F)).uv(44, 82)
                        .cuboid(-1.0F, -2.019F, -14.9358F, 2.0F, 4.0F, 10.0F, new Dilation(-0.01F)),
                ModelTransform.of(0.0F, -5.0F, 0.0F, 0.3491F, 0.0F, 0.0F));

        ModelPartData cube_r9 = dividers
                .addChild("cube_r9",
                        ModelPartBuilder.create().uv(0, 81).cuboid(0.0F, 4.25F, -10.75F, 2.0F, 2.0F, 11.0F,
                                new Dilation(0.001F)),
                        ModelTransform.of(-1.0F, -1.7004F, 0.2894F, -1.5708F, 0.0F, 0.0F));

        ModelPartData cube_r10 = dividers.addChild("cube_r10",
                ModelPartBuilder.create().uv(0, 63).cuboid(-1.5F, -3.0F, -13.0F, 4.0F, 3.0F, 14.0F, new Dilation(0.0F)),
                ModelTransform.of(-0.5F, -0.333F, -0.0495F, 0.1047F, 0.0F, 0.0F));

        ModelPartData cube_r11 = dividers.addChild("cube_r11",
                ModelPartBuilder.create().uv(23, 68).cuboid(-4.0F, -1.0F, 0.0F, 1.0F, 3.0F, 14.0F, new Dilation(0.0F))
                        .uv(63, 29).cuboid(-4.5F, 0.0F, 0.0F, 2.0F, 3.0F, 14.0F, new Dilation(0.0F)),
                ModelTransform.of(3.5F, -14.75F, -17.0F, 0.3491F, 0.0F, 0.0F));

        ModelPartData dividers2 = dividers.addChild("dividers2",
                ModelPartBuilder.create().uv(97, 90)
                        .cuboid(-2.0F, -1.9742F, -15.9783F, 4.0F, 3.0F, 3.0F, new Dilation(0.01F)).uv(97, 14)
                        .cuboid(-1.0F, -14.75F, -20.0F, 2.0F, 3.0F, 5.0F, new Dilation(0.0F)).uv(0, 33)
                        .cuboid(-0.5F, -15.75F, -21.0F, 1.0F, 5.0F, 5.0F, new Dilation(0.001F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData cube_r12 = dividers2.addChild("cube_r12",
                ModelPartBuilder.create().uv(66, 61).cuboid(-2.0F, 0.0F, -14.0F, 2.0F, 2.0F, 14.0F, new Dilation(0.0F)),
                ModelTransform.of(1.0F, -11.4504F, -3.9606F, -0.1745F, 0.0F, 0.0F));

        ModelPartData cube_r13 = dividers2.addChild("cube_r13",
                ModelPartBuilder.create().uv(72, 78)
                        .cuboid(-0.5F, -3.019F, -16.4358F, 1.0F, 2.0F, 12.0F, new Dilation(0.0F)).uv(44, 82)
                        .cuboid(-1.0F, -2.019F, -14.9358F, 2.0F, 4.0F, 10.0F, new Dilation(-0.01F)),
                ModelTransform.of(0.0F, -5.0F, 0.0F, 0.3491F, 0.0F, 0.0F));

        ModelPartData cube_r14 = dividers2
                .addChild("cube_r14",
                        ModelPartBuilder.create().uv(0, 81).cuboid(0.0F, 4.25F, -10.75F, 2.0F, 2.0F, 11.0F,
                                new Dilation(0.001F)),
                        ModelTransform.of(-1.0F, -1.7004F, 0.2894F, -1.5708F, 0.0F, 0.0F));

        ModelPartData cube_r15 = dividers2.addChild("cube_r15",
                ModelPartBuilder.create().uv(0, 63).cuboid(-1.5F, -3.0F, -13.0F, 4.0F, 3.0F, 14.0F, new Dilation(0.0F)),
                ModelTransform.of(-0.5F, -0.333F, -0.0495F, 0.1047F, 0.0F, 0.0F));

        ModelPartData cube_r16 = dividers2.addChild("cube_r16",
                ModelPartBuilder.create().uv(23, 68).cuboid(-4.0F, -1.0F, 0.0F, 1.0F, 3.0F, 14.0F, new Dilation(0.0F))
                        .uv(63, 29).cuboid(-4.5F, 0.0F, 0.0F, 2.0F, 3.0F, 14.0F, new Dilation(0.0F)),
                ModelTransform.of(3.5F, -14.75F, -17.0F, 0.3491F, 0.0F, 0.0F));

        ModelPartData dividers3 = dividers2.addChild("dividers3",
                ModelPartBuilder.create().uv(97, 90)
                        .cuboid(-2.0F, -1.9742F, -15.9783F, 4.0F, 3.0F, 3.0F, new Dilation(0.01F)).uv(97, 14)
                        .cuboid(-1.0F, -14.75F, -20.0F, 2.0F, 3.0F, 5.0F, new Dilation(0.0F)).uv(0, 33)
                        .cuboid(-0.5F, -15.75F, -21.0F, 1.0F, 5.0F, 5.0F, new Dilation(0.001F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData cube_r17 = dividers3.addChild("cube_r17",
                ModelPartBuilder.create().uv(66, 61).cuboid(-2.0F, 0.0F, -14.0F, 2.0F, 2.0F, 14.0F, new Dilation(0.0F)),
                ModelTransform.of(1.0F, -11.4504F, -3.9606F, -0.1745F, 0.0F, 0.0F));

        ModelPartData cube_r18 = dividers3.addChild("cube_r18",
                ModelPartBuilder.create().uv(72, 78)
                        .cuboid(-0.5F, -3.019F, -16.4358F, 1.0F, 2.0F, 12.0F, new Dilation(0.0F)).uv(44, 82)
                        .cuboid(-1.0F, -2.019F, -14.9358F, 2.0F, 4.0F, 10.0F, new Dilation(-0.01F)),
                ModelTransform.of(0.0F, -5.0F, 0.0F, 0.3491F, 0.0F, 0.0F));

        ModelPartData cube_r19 = dividers3
                .addChild("cube_r19",
                        ModelPartBuilder.create().uv(0, 81).cuboid(0.0F, 4.25F, -10.75F, 2.0F, 2.0F, 11.0F,
                                new Dilation(0.001F)),
                        ModelTransform.of(-1.0F, -1.7004F, 0.2894F, -1.5708F, 0.0F, 0.0F));

        ModelPartData cube_r20 = dividers3.addChild("cube_r20",
                ModelPartBuilder.create().uv(0, 63).cuboid(-1.5F, -3.0F, -13.0F, 4.0F, 3.0F, 14.0F, new Dilation(0.0F)),
                ModelTransform.of(-0.5F, -0.333F, -0.0495F, 0.1047F, 0.0F, 0.0F));

        ModelPartData cube_r21 = dividers3.addChild("cube_r21",
                ModelPartBuilder.create().uv(23, 68).cuboid(-4.0F, -1.0F, 0.0F, 1.0F, 3.0F, 14.0F, new Dilation(0.0F))
                        .uv(63, 29).cuboid(-4.5F, 0.0F, 0.0F, 2.0F, 3.0F, 14.0F, new Dilation(0.0F)),
                ModelTransform.of(3.5F, -14.75F, -17.0F, 0.3491F, 0.0F, 0.0F));

        ModelPartData dividers4 = dividers3.addChild("dividers4",
                ModelPartBuilder.create().uv(97, 90)
                        .cuboid(-2.0F, -1.9742F, -15.9783F, 4.0F, 3.0F, 3.0F, new Dilation(0.01F)).uv(97, 14)
                        .cuboid(-1.0F, -14.75F, -20.0F, 2.0F, 3.0F, 5.0F, new Dilation(0.0F)).uv(0, 33)
                        .cuboid(-0.5F, -15.75F, -21.0F, 1.0F, 5.0F, 5.0F, new Dilation(0.001F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData cube_r22 = dividers4.addChild("cube_r22",
                ModelPartBuilder.create().uv(66, 61).cuboid(-2.0F, 0.0F, -14.0F, 2.0F, 2.0F, 14.0F, new Dilation(0.0F)),
                ModelTransform.of(1.0F, -11.4504F, -3.9606F, -0.1745F, 0.0F, 0.0F));

        ModelPartData cube_r23 = dividers4.addChild("cube_r23",
                ModelPartBuilder.create().uv(72, 78)
                        .cuboid(-0.5F, -3.019F, -16.4358F, 1.0F, 2.0F, 12.0F, new Dilation(0.0F)).uv(44, 82)
                        .cuboid(-1.0F, -2.019F, -14.9358F, 2.0F, 4.0F, 10.0F, new Dilation(-0.01F)),
                ModelTransform.of(0.0F, -5.0F, 0.0F, 0.3491F, 0.0F, 0.0F));

        ModelPartData cube_r24 = dividers4
                .addChild("cube_r24",
                        ModelPartBuilder.create().uv(0, 81).cuboid(0.0F, 4.25F, -10.75F, 2.0F, 2.0F, 11.0F,
                                new Dilation(0.001F)),
                        ModelTransform.of(-1.0F, -1.7004F, 0.2894F, -1.5708F, 0.0F, 0.0F));

        ModelPartData cube_r25 = dividers4.addChild("cube_r25",
                ModelPartBuilder.create().uv(0, 63).cuboid(-1.5F, -3.0F, -13.0F, 4.0F, 3.0F, 14.0F, new Dilation(0.0F)),
                ModelTransform.of(-0.5F, -0.333F, -0.0495F, 0.1047F, 0.0F, 0.0F));

        ModelPartData cube_r26 = dividers4.addChild("cube_r26",
                ModelPartBuilder.create().uv(23, 68).cuboid(-4.0F, -1.0F, 0.0F, 1.0F, 3.0F, 14.0F, new Dilation(0.0F))
                        .uv(63, 29).cuboid(-4.5F, 0.0F, 0.0F, 2.0F, 3.0F, 14.0F, new Dilation(0.0F)),
                ModelTransform.of(3.5F, -14.75F, -17.0F, 0.3491F, 0.0F, 0.0F));

        ModelPartData dividers5 = dividers4.addChild("dividers5",
                ModelPartBuilder.create().uv(97, 90)
                        .cuboid(-2.0F, -1.9742F, -15.9783F, 4.0F, 3.0F, 3.0F, new Dilation(0.01F)).uv(97, 14)
                        .cuboid(-1.0F, -14.75F, -20.0F, 2.0F, 3.0F, 5.0F, new Dilation(0.0F)).uv(0, 33)
                        .cuboid(-0.5F, -15.75F, -21.0F, 1.0F, 5.0F, 5.0F, new Dilation(0.001F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData cube_r27 = dividers5.addChild("cube_r27",
                ModelPartBuilder.create().uv(66, 61).cuboid(-2.0F, 0.0F, -14.0F, 2.0F, 2.0F, 14.0F, new Dilation(0.0F)),
                ModelTransform.of(1.0F, -11.4504F, -3.9606F, -0.1745F, 0.0F, 0.0F));

        ModelPartData cube_r28 = dividers5.addChild("cube_r28",
                ModelPartBuilder.create().uv(72, 78)
                        .cuboid(-0.5F, -3.019F, -16.4358F, 1.0F, 2.0F, 12.0F, new Dilation(0.0F)).uv(44, 82)
                        .cuboid(-1.0F, -2.019F, -14.9358F, 2.0F, 4.0F, 10.0F, new Dilation(-0.01F)),
                ModelTransform.of(0.0F, -5.0F, 0.0F, 0.3491F, 0.0F, 0.0F));

        ModelPartData cube_r29 = dividers5
                .addChild("cube_r29",
                        ModelPartBuilder.create().uv(0, 81).cuboid(0.0F, 4.25F, -10.75F, 2.0F, 2.0F, 11.0F,
                                new Dilation(0.001F)),
                        ModelTransform.of(-1.0F, -1.7004F, 0.2894F, -1.5708F, 0.0F, 0.0F));

        ModelPartData cube_r30 = dividers5.addChild("cube_r30",
                ModelPartBuilder.create().uv(0, 63).cuboid(-1.5F, -3.0F, -13.0F, 4.0F, 3.0F, 14.0F, new Dilation(0.0F)),
                ModelTransform.of(-0.5F, -0.333F, -0.0495F, 0.1047F, 0.0F, 0.0F));

        ModelPartData cube_r31 = dividers5.addChild("cube_r31",
                ModelPartBuilder.create().uv(23, 68).cuboid(-4.0F, -1.0F, 0.0F, 1.0F, 3.0F, 14.0F, new Dilation(0.0F))
                        .uv(63, 29).cuboid(-4.5F, 0.0F, 0.0F, 2.0F, 3.0F, 14.0F, new Dilation(0.0F)),
                ModelTransform.of(3.5F, -14.75F, -17.0F, 0.3491F, 0.0F, 0.0F));

        ModelPartData dividers6 = dividers5.addChild("dividers6",
                ModelPartBuilder.create().uv(97, 90)
                        .cuboid(-2.0F, -1.9742F, -15.9783F, 4.0F, 3.0F, 3.0F, new Dilation(0.01F)).uv(97, 14)
                        .cuboid(-1.0F, -14.75F, -20.0F, 2.0F, 3.0F, 5.0F, new Dilation(0.0F)).uv(0, 33)
                        .cuboid(-0.5F, -15.75F, -21.0F, 1.0F, 5.0F, 5.0F, new Dilation(0.001F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData cube_r32 = dividers6.addChild("cube_r32",
                ModelPartBuilder.create().uv(66, 61).cuboid(-2.0F, 0.0F, -14.0F, 2.0F, 2.0F, 14.0F, new Dilation(0.0F)),
                ModelTransform.of(1.0F, -11.4504F, -3.9606F, -0.1745F, 0.0F, 0.0F));

        ModelPartData cube_r33 = dividers6.addChild("cube_r33",
                ModelPartBuilder.create().uv(72, 78)
                        .cuboid(-0.5F, -3.019F, -16.4358F, 1.0F, 2.0F, 12.0F, new Dilation(0.0F)).uv(44, 82)
                        .cuboid(-1.0F, -2.019F, -14.9358F, 2.0F, 4.0F, 10.0F, new Dilation(-0.01F)),
                ModelTransform.of(0.0F, -5.0F, 0.0F, 0.3491F, 0.0F, 0.0F));

        ModelPartData cube_r34 = dividers6
                .addChild("cube_r34",
                        ModelPartBuilder.create().uv(0, 81).cuboid(0.0F, 4.25F, -10.75F, 2.0F, 2.0F, 11.0F,
                                new Dilation(0.001F)),
                        ModelTransform.of(-1.0F, -1.7004F, 0.2894F, -1.5708F, 0.0F, 0.0F));

        ModelPartData cube_r35 = dividers6.addChild("cube_r35",
                ModelPartBuilder.create().uv(0, 63).cuboid(-1.5F, -3.0F, -13.0F, 4.0F, 3.0F, 14.0F, new Dilation(0.0F)),
                ModelTransform.of(-0.5F, -0.333F, -0.0495F, 0.1047F, 0.0F, 0.0F));

        ModelPartData cube_r36 = dividers6.addChild("cube_r36",
                ModelPartBuilder.create().uv(23, 68).cuboid(-4.0F, -1.0F, 0.0F, 1.0F, 3.0F, 14.0F, new Dilation(0.0F))
                        .uv(63, 29).cuboid(-4.5F, 0.0F, 0.0F, 2.0F, 3.0F, 14.0F, new Dilation(0.0F)),
                ModelTransform.of(3.5F, -14.75F, -17.0F, 0.3491F, 0.0F, 0.0F));

        ModelPartData panels = steam.addChild("panels", ModelPartBuilder.create(),
                ModelTransform.of(0.0F, -18.5F, 0.0F, 0.0F, -1.5708F, 0.0F));

        ModelPartData cube_r37 = panels.addChild("cube_r37", ModelPartBuilder.create().uv(47, 47).cuboid(-9.0F, -1.8F,
                -16.85F, 18.0F, 0.0F, 13.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.3927F, 0.0F, 0.0F));

        ModelPartData panels2 = panels.addChild("panels2", ModelPartBuilder.create(),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData cube_r38 = panels2.addChild("cube_r38",
                ModelPartBuilder.create().uv(43, 0)
                        .cuboid(-9.0F, -2.05F, -16.85F, 18.0F, 0.0F, 13.0F, new Dilation(0.0F)).uv(47, 47)
                        .cuboid(-9.0F, -1.8F, -16.85F, 18.0F, 0.0F, 13.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.3927F, 0.0F, 0.0F));

        ModelPartData panels3 = panels2.addChild("panels3", ModelPartBuilder.create(),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData cube_r39 = panels3.addChild("cube_r39",
                ModelPartBuilder.create().uv(0, 33)
                        .cuboid(-9.0F, -2.05F, -16.85F, 18.0F, 0.0F, 13.0F, new Dilation(0.0F)).uv(47, 47)
                        .cuboid(-9.0F, -1.8F, -16.85F, 18.0F, 0.0F, 13.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.3927F, 0.0F, 0.0F));

        ModelPartData panels4 = panels3.addChild("panels4", ModelPartBuilder.create(),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData cube_r40 = panels4.addChild("cube_r40", ModelPartBuilder.create().uv(47, 47).cuboid(-9.0F, -1.8F,
                -16.85F, 18.0F, 0.0F, 13.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.3927F, 0.0F, 0.0F));

        ModelPartData panels5 = panels4.addChild("panels5", ModelPartBuilder.create(),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData cube_r41 = panels5.addChild("cube_r41",
                ModelPartBuilder.create().uv(0, 19)
                        .cuboid(-9.0F, -2.05F, -16.85F, 18.0F, 0.0F, 13.0F, new Dilation(0.0F)).uv(47, 47)
                        .cuboid(-9.0F, -1.8F, -16.85F, 18.0F, 0.0F, 13.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.3927F, 0.0F, 0.0F));

        ModelPartData panels6 = panels5.addChild("panels6", ModelPartBuilder.create(),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData cube_r42 = panels6.addChild("cube_r42", ModelPartBuilder.create().uv(47, 47).cuboid(-9.0F, -1.8F,
                -16.85F, 18.0F, 0.0F, 13.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.3927F, 0.0F, 0.0F));

        ModelPartData rim = steam.addChild("rim", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 1.0F, 0.0F));

        ModelPartData cube_r43 = rim.addChild("cube_r43", ModelPartBuilder.create().uv(43, 61).cuboid(-0.75F, -1.0F,
                -9.0F, 2.0F, 2.0F, 18.0F, new Dilation(0.0F)),
                ModelTransform.of(16.0F, -14.0F, 0.0F, 0.0F, 0.0F, -0.3054F));

        ModelPartData rim2 = rim.addChild("rim2", ModelPartBuilder.create(),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData cube_r44 = rim2.addChild("cube_r44", ModelPartBuilder.create().uv(43, 61).cuboid(-0.75F, -1.0F,
                -9.0F, 2.0F, 2.0F, 18.0F, new Dilation(0.0F)),
                ModelTransform.of(16.0F, -14.0F, 0.0F, 0.0F, 0.0F, -0.3054F));

        ModelPartData rim3 = rim2.addChild("rim3", ModelPartBuilder.create(),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData cube_r45 = rim3.addChild("cube_r45", ModelPartBuilder.create().uv(43, 61).cuboid(-0.75F, -1.0F,
                -9.0F, 2.0F, 2.0F, 18.0F, new Dilation(0.0F)),
                ModelTransform.of(16.0F, -14.0F, 0.0F, 0.0F, 0.0F, -0.3054F));

        ModelPartData rim4 = rim3.addChild("rim4", ModelPartBuilder.create(),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData cube_r46 = rim4.addChild("cube_r46", ModelPartBuilder.create().uv(43, 61).cuboid(-0.75F, -1.0F,
                -9.0F, 2.0F, 2.0F, 18.0F, new Dilation(0.0F)),
                ModelTransform.of(16.0F, -14.0F, 0.0F, 0.0F, 0.0F, -0.3054F));

        ModelPartData rim5 = rim4.addChild("rim5", ModelPartBuilder.create(),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData cube_r47 = rim5.addChild("cube_r47", ModelPartBuilder.create().uv(43, 61).cuboid(-0.75F, -1.0F,
                -9.0F, 2.0F, 2.0F, 18.0F, new Dilation(0.0F)),
                ModelTransform.of(16.0F, -14.0F, 0.0F, 0.0F, 0.0F, -0.3054F));

        ModelPartData rim6 = rim5.addChild("rim6", ModelPartBuilder.create(),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData cube_r48 = rim6.addChild("cube_r48", ModelPartBuilder.create().uv(43, 61).cuboid(-0.75F, -1.0F,
                -9.0F, 2.0F, 2.0F, 18.0F, new Dilation(0.0F)),
                ModelTransform.of(16.0F, -14.0F, 0.0F, 0.0F, 0.0F, -0.3054F));

        ModelPartData rotor = steam.addChild("rotor", ModelPartBuilder.create(),
                ModelTransform.pivot(0.0F, 2.0F, 0.0F));

        ModelPartData bone2 = rotor.addChild("bone2",
                ModelPartBuilder.create().uv(82, 29).cuboid(3.25F, -22.0F, -1.0F, 1.0F, 4.0F, 2.0F, new Dilation(0.0F))
                        .uv(85, 65).cuboid(3.25F, -24.0F, -1.0F, 1.0F, 1.0F, 2.0F, new Dilation(0.0F)).uv(0, 11)
                        .cuboid(1.5F, -18.0F, -2.5F, 3.0F, 1.0F, 5.0F, new Dilation(0.0F)).uv(57, 19)
                        .cuboid(3.5F, -23.0F, -1.5F, 0.0F, 1.0F, 3.0F, new Dilation(0.0F)),
                ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData cube_r49 = bone2.addChild("cube_r49",
                ModelPartBuilder.create().uv(0, 81).cuboid(3.25F, -24.5F, -1.5F, 1.0F, 7.0F, 3.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.5236F, 0.0F));

        ModelPartData bone3 = bone2.addChild("bone3",
                ModelPartBuilder.create().uv(82, 29).cuboid(3.25F, -22.0F, -1.0F, 1.0F, 4.0F, 2.0F, new Dilation(0.0F))
                        .uv(85, 65).cuboid(3.25F, -24.0F, -1.0F, 1.0F, 1.0F, 2.0F, new Dilation(0.0F)).uv(0, 11)
                        .cuboid(1.5F, -18.0F, -2.5F, 3.0F, 1.0F, 5.0F, new Dilation(0.0F)).uv(57, 19)
                        .cuboid(3.5F, -23.0F, -1.5F, 0.0F, 1.0F, 3.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData cube_r50 = bone3.addChild("cube_r50",
                ModelPartBuilder.create().uv(0, 81).cuboid(3.25F, -24.5F, -1.5F, 1.0F, 7.0F, 3.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.5236F, 0.0F));

        ModelPartData bone4 = bone3.addChild("bone4",
                ModelPartBuilder.create().uv(82, 29).cuboid(3.25F, -22.0F, -1.0F, 1.0F, 4.0F, 2.0F, new Dilation(0.0F))
                        .uv(85, 65).cuboid(3.25F, -24.0F, -1.0F, 1.0F, 1.0F, 2.0F, new Dilation(0.0F)).uv(0, 11)
                        .cuboid(1.5F, -18.0F, -2.5F, 3.0F, 1.0F, 5.0F, new Dilation(0.0F)).uv(57, 19)
                        .cuboid(3.5F, -23.0F, -1.5F, 0.0F, 1.0F, 3.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData cube_r51 = bone4.addChild("cube_r51",
                ModelPartBuilder.create().uv(0, 81).cuboid(3.25F, -24.5F, -1.5F, 1.0F, 7.0F, 3.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.5236F, 0.0F));

        ModelPartData bone5 = bone4.addChild("bone5",
                ModelPartBuilder.create().uv(82, 29).cuboid(3.25F, -22.0F, -1.0F, 1.0F, 4.0F, 2.0F, new Dilation(0.0F))
                        .uv(85, 65).cuboid(3.25F, -24.0F, -1.0F, 1.0F, 1.0F, 2.0F, new Dilation(0.0F)).uv(0, 11)
                        .cuboid(1.5F, -18.0F, -2.5F, 3.0F, 1.0F, 5.0F, new Dilation(0.0F)).uv(57, 19)
                        .cuboid(3.5F, -23.0F, -1.5F, 0.0F, 1.0F, 3.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData cube_r52 = bone5.addChild("cube_r52",
                ModelPartBuilder.create().uv(0, 81).cuboid(3.25F, -24.5F, -1.5F, 1.0F, 7.0F, 3.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.5236F, 0.0F));

        ModelPartData bone6 = bone5.addChild("bone6",
                ModelPartBuilder.create().uv(82, 29).cuboid(3.25F, -22.0F, -1.0F, 1.0F, 4.0F, 2.0F, new Dilation(0.0F))
                        .uv(85, 65).cuboid(3.25F, -24.0F, -1.0F, 1.0F, 1.0F, 2.0F, new Dilation(0.0F)).uv(0, 11)
                        .cuboid(1.5F, -18.0F, -2.5F, 3.0F, 1.0F, 5.0F, new Dilation(0.0F)).uv(57, 19)
                        .cuboid(3.5F, -23.0F, -1.5F, 0.0F, 1.0F, 3.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData cube_r53 = bone6.addChild("cube_r53",
                ModelPartBuilder.create().uv(0, 81).cuboid(3.25F, -24.5F, -1.5F, 1.0F, 7.0F, 3.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.5236F, 0.0F));

        ModelPartData bone7 = bone6.addChild("bone7",
                ModelPartBuilder.create().uv(82, 29).cuboid(3.25F, -22.0F, -1.0F, 1.0F, 4.0F, 2.0F, new Dilation(0.0F))
                        .uv(85, 65).cuboid(3.25F, -24.0F, -1.0F, 1.0F, 1.0F, 2.0F, new Dilation(0.0F)).uv(0, 11)
                        .cuboid(1.5F, -18.0F, -2.5F, 3.0F, 1.0F, 5.0F, new Dilation(0.0F)).uv(57, 19)
                        .cuboid(3.5F, -23.0F, -1.5F, 0.0F, 1.0F, 3.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData cube_r54 = bone7.addChild("cube_r54",
                ModelPartBuilder.create().uv(0, 81).cuboid(3.25F, -24.5F, -1.5F, 1.0F, 7.0F, 3.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.5236F, 0.0F));

        ModelPartData hourglass = rotor.addChild("hourglass", ModelPartBuilder.create(),
                ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData base7 = hourglass.addChild("base7",
                ModelPartBuilder.create().uv(0, 19).cuboid(-0.4F, -1.0F, -1.5F, 3.0F, 7.0F, 3.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, -21.5F, 0.0F, 0.0F, 0.5236F, 0.0F));

        ModelPartData base8 = base7.addChild("base8",
                ModelPartBuilder.create().uv(0, 19).cuboid(-0.4F, -1.0F, -1.5F, 3.0F, 7.0F, 3.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData base9 = base8.addChild("base9",
                ModelPartBuilder.create().uv(0, 19).cuboid(-0.4F, -1.0F, -1.5F, 3.0F, 7.0F, 3.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData base10 = base9.addChild("base10",
                ModelPartBuilder.create().uv(0, 19).cuboid(-0.4F, -1.0F, -1.5F, 3.0F, 7.0F, 3.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData base11 = base10.addChild("base11",
                ModelPartBuilder.create().uv(0, 19).cuboid(-0.4F, -1.0F, -1.5F, 3.0F, 7.0F, 3.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData base12 = base11.addChild("base12",
                ModelPartBuilder.create().uv(0, 19).cuboid(-0.4F, -1.0F, -1.5F, 3.0F, 7.0F, 3.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData bone15 = base7.addChild("bone15",
                ModelPartBuilder.create().uv(61, 14).cuboid(-2.25F, -6.3F, -0.5F, 0.0F, 6.0F, 1.0F, new Dilation(0.0F))
                        .uv(61, 14).cuboid(2.25F, -6.3F, -0.5F, 0.0F, 6.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -0.5236F, 0.0F));

        ModelPartData spin = base7.addChild("spin",
                ModelPartBuilder.create().uv(103, 29).cuboid(-1.5F, 0.25F, -1.5F, 3.0F, 3.0F, 3.0F, new Dilation(-0.3F))
                        .uv(106, 39).cuboid(-1.5F, 2.65F, -1.5F, 3.0F, 1.0F, 3.0F, new Dilation(-0.1F)).uv(106, 0)
                        .cuboid(-1.5F, -3.6F, -1.5F, 3.0F, 1.0F, 3.0F, new Dilation(-0.1F)).uv(0, 103)
                        .cuboid(-1.5F, -3.0F, -1.5F, 3.0F, 3.0F, 3.0F, new Dilation(-0.3F)).uv(40, 74)
                        .cuboid(-2.0F, -3.5F, -2.0F, 4.0F, 0.0F, 4.0F, new Dilation(0.0F)).uv(9, 51)
                        .cuboid(1.65F, -4.0F, -0.5F, 0.0F, 8.0F, 1.0F, new Dilation(0.0F)).uv(94, 87)
                        .cuboid(1.0F, -0.5F, -0.5F, 2.0F, 1.0F, 1.0F, new Dilation(-0.3F)).uv(10, 103)
                        .cuboid(-3.0F, -0.5F, -0.5F, 2.0F, 1.0F, 1.0F, new Dilation(-0.3F)).uv(9, 51)
                        .cuboid(-1.65F, -4.0F, -0.5F, 0.0F, 8.0F, 1.0F, new Dilation(0.0F)).uv(40, 74)
                        .cuboid(-2.0F, 3.6F, -2.0F, 4.0F, 0.0F, 4.0F, new Dilation(0.0F)).uv(111, 66)
                        .cuboid(-0.5F, -0.4F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, -6.0F, 0.0F, 0.0F, -0.5236F, 0.0F));

        ModelPartData bottom = steam.addChild("bottom", ModelPartBuilder.create(),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 3.1416F, 0.0F));

        ModelPartData cube_r55 = bottom
                .addChild(
                        "cube_r55", ModelPartBuilder.create().uv(0, 0).cuboid(0.479F, -0.7185F, -9.0F, 12.0F, 0.0F,
                                18.0F, new Dilation(0.0F)),
                        ModelTransform.of(-4.0F, -11.0F, 0.0F, 0.0F, 0.0F, -2.9671F));

        ModelPartData bottom2 = bottom.addChild("bottom2", ModelPartBuilder.create(),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData cube_r56 = bottom2
                .addChild(
                        "cube_r56", ModelPartBuilder.create().uv(0, 0).cuboid(0.479F, -0.7185F, -9.0F, 12.0F, 0.0F,
                                18.0F, new Dilation(0.0F)),
                        ModelTransform.of(-4.0F, -11.0F, 0.0F, 0.0F, 0.0F, -2.9671F));

        ModelPartData bottom3 = bottom2.addChild("bottom3", ModelPartBuilder.create(),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData cube_r57 = bottom3
                .addChild(
                        "cube_r57", ModelPartBuilder.create().uv(0, 0).cuboid(0.479F, -0.7185F, -9.0F, 12.0F, 0.0F,
                                18.0F, new Dilation(0.0F)),
                        ModelTransform.of(-4.0F, -11.0F, 0.0F, 0.0F, 0.0F, -2.9671F));

        ModelPartData bottom4 = bottom3.addChild("bottom4", ModelPartBuilder.create(),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData cube_r58 = bottom4
                .addChild(
                        "cube_r58", ModelPartBuilder.create().uv(0, 0).cuboid(0.479F, -0.7185F, -9.0F, 12.0F, 0.0F,
                                18.0F, new Dilation(0.0F)),
                        ModelTransform.of(-4.0F, -11.0F, 0.0F, 0.0F, 0.0F, -2.9671F));

        ModelPartData bottom5 = bottom4.addChild("bottom5", ModelPartBuilder.create(),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData cube_r59 = bottom5
                .addChild(
                        "cube_r59", ModelPartBuilder.create().uv(0, 0).cuboid(0.479F, -0.7185F, -9.0F, 12.0F, 0.0F,
                                18.0F, new Dilation(0.0F)),
                        ModelTransform.of(-4.0F, -11.0F, 0.0F, 0.0F, 0.0F, -2.9671F));

        ModelPartData bottom6 = bottom5.addChild("bottom6", ModelPartBuilder.create(),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData cube_r60 = bottom6
                .addChild(
                        "cube_r60", ModelPartBuilder.create().uv(0, 0).cuboid(0.479F, -0.7185F, -9.0F, 12.0F, 0.0F,
                                18.0F, new Dilation(0.0F)),
                        ModelTransform.of(-4.0F, -11.0F, 0.0F, 0.0F, 0.0F, -2.9671F));

        ModelPartData bone8 = steam.addChild("bone8",
                ModelPartBuilder.create().uv(0, 47).cuboid(5.0F, -11.0F, -2.0F, 0.0F, 9.0F, 4.0F, new Dilation(0.0F)),
                ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData bone57 = bone8.addChild("bone57",
                ModelPartBuilder.create().uv(0, 47).cuboid(5.0F, -11.0F, -2.0F, 0.0F, 9.0F, 4.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData bone58 = bone57.addChild("bone58",
                ModelPartBuilder.create().uv(0, 47).cuboid(5.0F, -11.0F, -2.0F, 0.0F, 9.0F, 4.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData bone59 = bone58.addChild("bone59",
                ModelPartBuilder.create().uv(0, 47).cuboid(5.0F, -11.0F, -2.0F, 0.0F, 9.0F, 4.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData bone60 = bone59.addChild("bone60",
                ModelPartBuilder.create().uv(0, 47).cuboid(5.0F, -11.0F, -2.0F, 0.0F, 9.0F, 4.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData bone61 = bone60.addChild("bone61",
                ModelPartBuilder.create().uv(0, 47).cuboid(5.0F, -11.0F, -2.0F, 0.0F, 9.0F, 4.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData controls = steam.addChild("controls", ModelPartBuilder.create(),
                ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData panel_1 = controls.addChild("panel_1", ModelPartBuilder.create(),
                ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData rot = panel_1.addChild("rot", ModelPartBuilder.create(),
                ModelTransform.of(15.0F, -14.25F, 0.0F, 0.0F, 0.0F, 0.3927F));

        ModelPartData radio = rot.addChild("radio",
                ModelPartBuilder.create().uv(0, 63).cuboid(-1.0F, -4.0F, -2.0F, 2.0F, 5.0F, 4.0F, new Dilation(0.0F))
                        .uv(47, 47).cuboid(-1.0F, -4.0F, -2.0F, 2.0F, 5.0F, 4.0F, new Dilation(0.1F)).uv(43, 0)
                        .cuboid(-1.0F, -4.0F, -2.0F, 2.0F, 5.0F, 4.0F, new Dilation(0.2F)).uv(112, 69)
                        .cuboid(-0.5F, -3.5F, -1.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F)).uv(73, 61)
                        .cuboid(1.05F, -0.75F, -1.5F, 0.0F, 1.0F, 3.0F, new Dilation(0.0F)),
                ModelTransform.of(-9.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.6545F));

        ModelPartData light = rot.addChild("light",
                ModelPartBuilder.create().uv(85, 71).cuboid(-1.0F, -0.85F, -1.0F, 2.0F, 1.0F, 2.0F, new Dilation(-0.1F))
                        .uv(6, 81).cuboid(0.15F, -0.85F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.2F)).uv(108, 97)
                        .cuboid(-1.0F, -1.85F, -1.0F, 2.0F, 2.0F, 2.0F, new Dilation(-0.4F)).uv(19, 112)
                        .cuboid(-0.5F, -1.95F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.2F)).uv(74, 69)
                        .cuboid(-0.5F, -3.55F, -0.5F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.pivot(-6.0F, 0.0F, -2.5F));

        ModelPartData bone28 = light.addChild("bone28", ModelPartBuilder.create().uv(23, 68).cuboid(-0.5F, -3.55F,
                -0.5F, 1.0F, 2.0F, 1.0F, new Dilation(0.01F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData switch0 = light.addChild("switch0",
                ModelPartBuilder.create().uv(9, 44).cuboid(0.0F, -1.0F, 0.0F, 1.0F, 1.0F, 0.0F, new Dilation(0.0F)),
                ModelTransform.of(0.9F, -0.1F, 0.0F, 0.0F, 0.7854F, 0.0F));

        ModelPartData light2 = rot.addChild("light2",
                ModelPartBuilder.create().uv(85, 71).cuboid(-1.0F, -0.85F, -1.0F, 2.0F, 1.0F, 2.0F, new Dilation(-0.1F))
                        .uv(6, 81).cuboid(0.15F, -0.85F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.2F)).uv(108, 97)
                        .cuboid(-1.0F, -1.85F, -1.0F, 2.0F, 2.0F, 2.0F, new Dilation(-0.4F)).uv(19, 112)
                        .cuboid(-0.5F, -1.95F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.2F)).uv(74, 69)
                        .cuboid(-0.5F, -3.55F, -0.5F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.pivot(-6.0F, 0.0F, 2.5F));

        ModelPartData bone24 = light2.addChild("bone24", ModelPartBuilder.create().uv(23, 68).cuboid(-0.5F, -3.55F,
                -0.5F, 1.0F, 2.0F, 1.0F, new Dilation(0.01F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData switch1 = light2.addChild("switch1",
                ModelPartBuilder.create().uv(9, 44).cuboid(0.0F, -1.0F, 0.0F, 1.0F, 1.0F, 0.0F, new Dilation(0.0F)),
                ModelTransform.of(0.9F, -0.1F, 0.0F, 0.0F, 0.7854F, 0.0F));

        ModelPartData valve = rot.addChild("valve",
                ModelPartBuilder.create().uv(50, 111).cuboid(-0.5F, -1.0F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                        .uv(111, 44).cuboid(-0.5F, -1.25F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.1F)),
                ModelTransform.pivot(-6.0F, 0.25F, 0.0F));

        ModelPartData bone9 = valve.addChild("bone9",
                ModelPartBuilder.create().uv(12, 11).cuboid(-0.5F, -2.0F, -0.5F, 1.0F, 2.0F, 1.0F, new Dilation(-0.35F))
                        .uv(63, 29).cuboid(-1.0F, -1.8F, -1.0F, 2.0F, 1.0F, 2.0F, new Dilation(-0.25F)),
                ModelTransform.pivot(0.0F, -0.65F, 0.0F));

        ModelPartData switch5 = rot.addChild("switch5",
                ModelPartBuilder.create().uv(52, 105).cuboid(0.0F, -1.0F, -1.0F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.pivot(-1.0F, 0.5F, 0.5F));

        ModelPartData bone25 = switch5.addChild("bone25",
                ModelPartBuilder.create().uv(0, 47).cuboid(0.0F, -1.0F, -1.0F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(0.5F, -1.0F, -0.25F, 0.0F, 0.0F, -0.5672F));

        ModelPartData switch2 = rot.addChild("switch2",
                ModelPartBuilder.create().uv(52, 105).cuboid(0.0F, -1.0F, -1.0F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.pivot(-1.0F, 0.5F, 2.0F));

        ModelPartData bone11 = switch2.addChild("bone11",
                ModelPartBuilder.create().uv(0, 47).cuboid(0.0F, -1.0F, -1.0F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(0.5F, -1.0F, -0.25F, 0.0F, 0.0F, -0.5672F));

        ModelPartData switch3 = rot.addChild("switch3",
                ModelPartBuilder.create().uv(52, 105).cuboid(0.0F, -1.0F, -1.0F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.pivot(-1.0F, 0.5F, 3.5F));

        ModelPartData bone12 = switch3.addChild("bone12",
                ModelPartBuilder.create().uv(0, 47).cuboid(0.0F, -1.0F, -1.0F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(0.5F, -1.0F, -0.25F, 0.0F, 0.0F, -0.5672F));

        ModelPartData switch4 = rot.addChild("switch4",
                ModelPartBuilder.create().uv(52, 105).cuboid(0.0F, -1.0F, -1.0F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.pivot(-1.0F, 0.5F, 5.0F));

        ModelPartData bone13 = switch4.addChild("bone13",
                ModelPartBuilder.create().uv(0, 47).cuboid(0.0F, -1.0F, -1.0F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(0.5F, -1.0F, -0.25F, 0.0F, 0.0F, -0.5672F));

        ModelPartData switch17 = rot.addChild("switch17",
                ModelPartBuilder.create().uv(52, 105).cuboid(0.0F, -1.0F, -1.0F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.pivot(-1.0F, 0.5F, 6.5F));

        ModelPartData bone14 = switch17.addChild("bone14",
                ModelPartBuilder.create().uv(0, 47).cuboid(0.0F, -1.0F, -1.0F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(0.5F, -1.0F, -0.25F, 0.0F, 0.0F, -0.5672F));

        ModelPartData lever2 = rot.addChild("lever2",
                ModelPartBuilder.create().uv(47, 57).cuboid(-1.0F, -0.7F, -0.5F, 2.0F, 1.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.pivot(-3.0F, 0.25F, -1.5F));

        ModelPartData bone20 = lever2.addChild("bone20",
                ModelPartBuilder.create().uv(0, 19).cuboid(-0.5F, -2.25F, 0.0F, 1.0F, 2.0F, 0.0F, new Dilation(0.0F))
                        .uv(52, 10).cuboid(-0.75F, -3.25F, 0.0F, 1.0F, 1.0F, 0.0F, new Dilation(0.0F)).uv(97, 47)
                        .cuboid(-0.25F, -3.95F, -0.5F, 1.0F, 2.0F, 1.0F, new Dilation(-0.25F)),
                ModelTransform.of(0.0F, 0.25F, 0.0F, 0.0F, 0.0F, -0.5672F));

        ModelPartData lever3 = rot.addChild("lever3",
                ModelPartBuilder.create().uv(47, 57).cuboid(-1.0F, -0.7F, -0.5F, 2.0F, 1.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.pivot(-3.0F, 0.25F, 0.0F));

        ModelPartData bone19 = lever3.addChild("bone19",
                ModelPartBuilder.create().uv(0, 19).cuboid(-0.5F, -2.25F, 0.0F, 1.0F, 2.0F, 0.0F, new Dilation(0.0F))
                        .uv(52, 10).cuboid(-0.75F, -3.25F, 0.0F, 1.0F, 1.0F, 0.0F, new Dilation(0.0F)).uv(97, 47)
                        .cuboid(-0.25F, -3.95F, -0.5F, 1.0F, 2.0F, 1.0F, new Dilation(-0.25F)),
                ModelTransform.of(0.0F, 0.25F, 0.0F, 0.0F, 0.0F, 0.4363F));

        ModelPartData lever5 = rot.addChild("lever5",
                ModelPartBuilder.create().uv(47, 57).cuboid(-1.0F, -0.7F, -0.5F, 2.0F, 1.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.pivot(-3.0F, 0.25F, 1.5F));

        ModelPartData bone21 = lever5.addChild("bone21",
                ModelPartBuilder.create().uv(0, 19).cuboid(-0.5F, -2.25F, 0.0F, 1.0F, 2.0F, 0.0F, new Dilation(0.0F))
                        .uv(52, 10).cuboid(-0.75F, -3.25F, 0.0F, 1.0F, 1.0F, 0.0F, new Dilation(0.0F)).uv(97, 47)
                        .cuboid(-0.25F, -3.95F, -0.5F, 1.0F, 2.0F, 1.0F, new Dilation(-0.25F)),
                ModelTransform.of(0.0F, 0.25F, 0.0F, 0.0F, 0.0F, -0.5672F));

        ModelPartData lever = rot.addChild("lever",
                ModelPartBuilder.create().uv(47, 57).cuboid(-1.0F, -0.7F, -0.5F, 2.0F, 1.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.pivot(-3.0F, 0.25F, 3.0F));

        ModelPartData bone23 = lever.addChild("bone23",
                ModelPartBuilder.create().uv(0, 19).cuboid(-0.5F, -2.25F, 0.0F, 1.0F, 2.0F, 0.0F, new Dilation(0.0F))
                        .uv(52, 10).cuboid(-0.75F, -3.25F, 0.0F, 1.0F, 1.0F, 0.0F, new Dilation(0.0F)).uv(97, 47)
                        .cuboid(-0.25F, -3.95F, -0.5F, 1.0F, 2.0F, 1.0F, new Dilation(-0.25F)),
                ModelTransform.of(0.0F, 0.25F, 0.0F, 0.0F, 0.0F, 0.4363F));

        ModelPartData lever4 = rot.addChild("lever4",
                ModelPartBuilder.create().uv(47, 57).cuboid(-1.0F, -0.7F, -0.5F, 2.0F, 1.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.pivot(-3.0F, 0.25F, -3.0F));

        ModelPartData bone22 = lever4.addChild("bone22",
                ModelPartBuilder.create().uv(0, 19).cuboid(-0.5F, -2.25F, 0.0F, 1.0F, 2.0F, 0.0F, new Dilation(0.0F))
                        .uv(52, 10).cuboid(-0.75F, -3.25F, 0.0F, 1.0F, 1.0F, 0.0F, new Dilation(0.0F)).uv(97, 47)
                        .cuboid(-0.25F, -3.95F, -0.5F, 1.0F, 2.0F, 1.0F, new Dilation(-0.25F)),
                ModelTransform.of(0.0F, 0.25F, 0.0F, 0.0F, 0.0F, 0.4363F));

        ModelPartData stabilizer = rot.addChild("stabilizer",
                ModelPartBuilder.create().uv(43, 86).cuboid(-0.5F, -0.3F, -2.0F, 1.0F, 1.0F, 4.0F, new Dilation(0.0F)),
                ModelTransform.pivot(-0.5F, 0.0F, -4.0F));

        ModelPartData bone26 = stabilizer.addChild("bone26",
                ModelPartBuilder.create().uv(108, 52).cuboid(-0.5F, -0.75F, -2.0F, 1.0F, 0.0F, 4.0F, new Dilation(0.1F))
                        .uv(30, 68).cuboid(0.0F, -0.75F, -1.5F, 0.0F, 1.0F, 3.0F, new Dilation(0.1F)),
                ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData panel_2 = controls.addChild("panel_2", ModelPartBuilder.create(),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData rot2 = panel_2.addChild("rot2", ModelPartBuilder.create(),
                ModelTransform.of(15.0F, -14.25F, 0.0F, 0.0F, 0.0F, 0.3927F));

        ModelPartData typewriter = rot2.addChild("typewriter",
                ModelPartBuilder.create().uv(106, 61)
                        .cuboid(-6.0485F, -2.3697F, -3.0F, 5.0F, 3.0F, 1.0F, new Dilation(0.0F)).uv(111, 36)
                        .cuboid(-1.5485F, -2.3697F, -3.0F, 1.0F, 1.0F, 1.0F, new Dilation(-0.3F)).uv(9, 108)
                        .cuboid(-2.0485F, -2.3697F, -2.0F, 1.0F, 0.0F, 4.0F, new Dilation(0.0F)).uv(106, 47)
                        .cuboid(-6.0485F, -2.3697F, 2.0F, 5.0F, 3.0F, 1.0F, new Dilation(0.0F)).uv(0, 95)
                        .cuboid(-6.0485F, -2.3697F, -2.0F, 4.0F, 3.0F, 4.0F, new Dilation(0.0F)).uv(36, 100)
                        .cuboid(-5.2985F, -3.1197F, -2.5F, 1.0F, 1.0F, 5.0F, new Dilation(0.0F)).uv(33, 90)
                        .cuboid(-5.2985F, -3.1197F, -3.5F, 1.0F, 1.0F, 7.0F, new Dilation(-0.2F)).uv(21, 89)
                        .cuboid(-1.1985F, -0.3697F, -3.0F, 3.0F, 1.0F, 6.0F, new Dilation(0.0F)).uv(99, 71)
                        .cuboid(-4.0597F, -1.2718F, -2.0F, 3.0F, 1.0F, 4.0F, new Dilation(0.0F)),
                ModelTransform.pivot(2.0F, 0.0F, 0.0F));

        ModelPartData cube_r61 = typewriter.addChild("cube_r61",
                ModelPartBuilder.create().uv(50, 19).cuboid(-2.75F, -0.55F, 0.0F, 0.0F, 1.0F, 6.0F, new Dilation(0.0F))
                        .uv(66, 61).cuboid(-1.75F, -0.55F, 0.0F, 0.0F, 1.0F, 6.0F, new Dilation(0.0F)).uv(50, 19)
                        .cuboid(-0.75F, -0.55F, 0.0F, 0.0F, 1.0F, 6.0F, new Dilation(0.0F)).uv(23, 68)
                        .cuboid(-0.25F, -0.55F, 0.0F, 0.0F, 1.0F, 6.0F, new Dilation(0.0F)).uv(93, 0)
                        .cuboid(-3.0F, -0.55F, 0.0F, 3.0F, 0.0F, 6.0F, new Dilation(0.0F)),
                ModelTransform.of(1.8015F, -0.3697F, -3.0F, 0.0F, 0.0F, 0.1745F));

        ModelPartData cube_r62 = typewriter.addChild("cube_r62",
                ModelPartBuilder.create().uv(59, 82).cuboid(-3.0F, 0.0F, 0.0F, 3.0F, 1.0F, 6.0F, new Dilation(-0.001F)),
                ModelTransform.of(1.8015F, -0.3697F, -3.0F, 0.0F, 0.0F, 0.3054F));

        ModelPartData cube_r63 = typewriter.addChild("cube_r63",
                ModelPartBuilder.create().uv(44, 111).cuboid(-3.4F, -1.0F, 1.0F, 1.0F, 1.0F, 3.0F, new Dilation(0.0F)),
                ModelTransform.of(-0.1454F, -2.3697F, -0.0004F, 0.0F, -0.3927F, 0.0F));

        ModelPartData cube_r64 = typewriter
                .addChild("cube_r64",
                        ModelPartBuilder.create().uv(89, 110).cuboid(-3.4002F, -1.0F, -3.9993F, 1.0F, 1.0F, 3.0F,
                                new Dilation(0.0F)),
                        ModelTransform.of(-0.1454F, -2.3697F, -0.0004F, 0.0F, 0.3927F, 0.0F));

        ModelPartData cube_r65 = typewriter.addChild("cube_r65",
                ModelPartBuilder.create().uv(107, 14).cuboid(-2.0F, -1.0F, 0.0F, 2.0F, 1.0F, 3.0F, new Dilation(0.0F)),
                ModelTransform.of(-1.1454F, -1.8697F, -0.0004F, 0.0F, -0.3927F, 0.0F));

        ModelPartData cube_r66 = typewriter
                .addChild("cube_r66",
                        ModelPartBuilder.create().uv(36, 108).cuboid(-2.0002F, -1.0F, -2.9993F, 2.0F, 1.0F, 3.0F,
                                new Dilation(0.001F)),
                        ModelTransform.of(-1.1454F, -1.8697F, -0.0004F, 0.0F, 0.3927F, 0.0F));

        ModelPartData cube_r67 = typewriter
                .addChild("cube_r67",
                        ModelPartBuilder.create().uv(97, 47).cuboid(-1.25F, -1.1F, -3.0F, 1.0F, 1.0F, 6.0F,
                                new Dilation(-0.4F)),
                        ModelTransform.of(-5.0485F, -2.3697F, 0.0F, 0.0F, 0.0F, -0.4363F));

        ModelPartData cube_r68 = typewriter.addChild("cube_r68",
                ModelPartBuilder.create().uv(44, 105).cuboid(-0.55F, 1.9F, -2.5F, 1.0F, 0.0F, 5.0F, new Dilation(0.0F)),
                ModelTransform.of(-4.0485F, -3.6197F, 0.0F, 0.0F, 0.0F, 0.9163F));

        ModelPartData cube_r69 = typewriter.addChild("cube_r69",
                ModelPartBuilder.create().uv(66, 69).cuboid(0.0F, 0.001F, -5.0F, 1.0F, 0.0F, 5.0F, new Dilation(0.0F)),
                ModelTransform.of(-4.1572F, -3.0927F, 2.5F, 0.0F, 0.0F, 1.6057F));

        ModelPartData cube_r70 = typewriter.addChild("cube_r70",
                ModelPartBuilder.create().uv(98, 97).cuboid(0.0F, 0.001F, -5.0F, 2.0F, 0.0F, 5.0F, new Dilation(0.0F)),
                ModelTransform.of(-5.4627F, -3.8313F, 2.5F, 0.0F, 0.0F, 0.5149F));

        ModelPartData cube_r71 = typewriter.addChild("cube_r71",
                ModelPartBuilder.create().uv(101, 84).cuboid(0.0F, 0.001F, -5.0F, 1.0F, 0.0F, 5.0F, new Dilation(0.0F)),
                ModelTransform.of(-6.4553F, -3.9532F, 2.5F, 0.0F, 0.0F, 0.1222F));

        ModelPartData cube_r72 = typewriter.addChild("cube_r72",
                ModelPartBuilder.create().uv(50, 37).cuboid(-2.95F, 1.45F, -2.5F, 4.0F, 0.0F, 5.0F, new Dilation(0.0F))
                        .uv(26, 99).cuboid(-1.45F, 1.65F, -2.5F, 2.0F, 0.0F, 5.0F, new Dilation(0.0F)),
                ModelTransform.of(-4.0485F, -3.6197F, 0.0F, 0.0F, 0.0F, 0.7767F));

        ModelPartData cube_r73 = typewriter.addChild("cube_r73",
                ModelPartBuilder.create().uv(23, 63)
                        .cuboid(-0.7174F, -1.3479F, 5.0F, 1.0F, 2.0F, 0.0F, new Dilation(0.0F)).uv(26, 105)
                        .cuboid(-0.7174F, -1.3479F, 0.0F, 1.0F, 0.0F, 5.0F, new Dilation(0.0F)).uv(23, 63)
                        .cuboid(-0.7174F, -1.3479F, 0.0F, 1.0F, 2.0F, 0.0F, new Dilation(0.0F)),
                ModelTransform.of(-5.5485F, -3.1197F, -2.5F, 0.0F, 0.0F, -0.6545F));

        ModelPartData cube_r74 = typewriter.addChild("cube_r74",
                ModelPartBuilder.create().uv(64, 37).cuboid(-1.0F, 0.0F, 5.0F, 1.0F, 1.0F, 0.0F, new Dilation(0.0F))
                        .uv(64, 37).cuboid(-1.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.0F, new Dilation(0.0F)),
                ModelTransform.of(-5.2985F, -3.1197F, -2.5F, 0.0F, 0.0F, -0.4363F));

        ModelPartData bone27 = typewriter.addChild("bone27",
                ModelPartBuilder.create().uv(54, 57).cuboid(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.2F))
                        .uv(9, 30).cuboid(-0.25F, -0.5F, 0.0F, 1.0F, 1.0F, 0.0F, new Dilation(0.0F)),
                ModelTransform.of(-1.25F, -1.85F, 2.5F, -1.3526F, 0.0F, 0.0F));

        ModelPartData counter = rot2.addChild("counter",
                ModelPartBuilder.create().uv(69, 102).cuboid(-1.0F, -2.0F, -2.0F, 2.0F, 2.0F, 4.0F, new Dilation(0.0F))
                        .uv(72, 82).cuboid(-1.0F, -3.0F, -2.0F, 1.0F, 1.0F, 4.0F, new Dilation(0.0F)).uv(106, 9)
                        .cuboid(0.0F, -3.0F, -2.0F, 1.0F, 0.0F, 4.0F, new Dilation(0.001F)).uv(97, 14)
                        .cuboid(0.0F, -3.2F, -2.0F, 1.0F, 2.0F, 1.0F, new Dilation(-0.2F)).uv(97, 14)
                        .cuboid(0.0F, -3.2F, -1.0F, 1.0F, 2.0F, 1.0F, new Dilation(-0.2F)).uv(97, 14)
                        .cuboid(0.0F, -3.2F, 0.0F, 1.0F, 2.0F, 1.0F, new Dilation(-0.2F)).uv(97, 14)
                        .cuboid(0.0F, -3.2F, 1.0F, 1.0F, 2.0F, 1.0F, new Dilation(-0.2F)).uv(36, 63)
                        .cuboid(0.0F, -3.0F, -2.0F, 1.0F, 1.0F, 0.0F, new Dilation(0.0F)).uv(62, 29)
                        .cuboid(0.0F, -3.0F, 2.0F, 1.0F, 1.0F, 0.0F, new Dilation(0.0F)).uv(58, 16)
                        .cuboid(0.0F, -3.0F, 0.0F, 1.0F, 1.0F, 0.0F, new Dilation(0.0F)),
                ModelTransform.of(-9.0F, 0.75F, 0.0F, 0.0F, 0.0F, -0.6109F));

        ModelPartData valve3 = rot2.addChild("valve3",
                ModelPartBuilder.create().uv(82, 39).cuboid(-1.0F, -1.0F, -1.0F, 2.0F, 1.0F, 2.0F, new Dilation(0.0F))
                        .uv(56, 14).cuboid(-0.75F, -1.75F, -0.75F, 2.0F, 1.0F, 0.0F, new Dilation(0.0F)).uv(5, 47)
                        .cuboid(-1.0F, -1.75F, -1.0F, 2.0F, 1.0F, 2.0F, new Dilation(-0.3F)).uv(8, 33)
                        .cuboid(-0.75F, -1.75F, -1.25F, 0.0F, 1.0F, 2.0F, new Dilation(0.0F)).uv(0, 33)
                        .cuboid(0.75F, -1.75F, -0.75F, 0.0F, 1.0F, 2.0F, new Dilation(0.0F)).uv(57, 43)
                        .cuboid(-1.25F, -1.75F, 0.75F, 2.0F, 1.0F, 0.0F, new Dilation(0.0F)),
                ModelTransform.pivot(-0.75F, 0.25F, -5.5F));

        ModelPartData bone16 = valve3.addChild("bone16", ModelPartBuilder.create().uv(57, 29).cuboid(-0.65F, -0.2F,
                -0.25F, 1.0F, 0.0F, 1.0F, new Dilation(0.001F)), ModelTransform.pivot(0.0F, -1.25F, 0.0F));

        ModelPartData valve2 = rot2.addChild("valve2",
                ModelPartBuilder.create().uv(82, 39).cuboid(-1.0F, -1.0F, -1.0F, 2.0F, 1.0F, 2.0F, new Dilation(0.0F))
                        .uv(56, 14).cuboid(-0.75F, -1.75F, -0.75F, 2.0F, 1.0F, 0.0F, new Dilation(0.0F)).uv(5, 47)
                        .cuboid(-1.0F, -1.75F, -1.0F, 2.0F, 1.0F, 2.0F, new Dilation(-0.3F)).uv(8, 33)
                        .cuboid(-0.75F, -1.75F, -1.25F, 0.0F, 1.0F, 2.0F, new Dilation(0.0F)).uv(0, 33)
                        .cuboid(0.75F, -1.75F, -0.75F, 0.0F, 1.0F, 2.0F, new Dilation(0.0F)).uv(57, 43)
                        .cuboid(-1.25F, -1.75F, 0.75F, 2.0F, 1.0F, 0.0F, new Dilation(0.0F)),
                ModelTransform.pivot(-0.75F, 0.25F, 5.5F));

        ModelPartData bone17 = valve2.addChild("bone17", ModelPartBuilder.create().uv(57, 29).cuboid(-0.65F, -0.2F,
                -0.25F, 1.0F, 0.0F, 1.0F, new Dilation(0.001F)), ModelTransform.pivot(0.0F, -1.25F, 0.0F));

        ModelPartData panel_3 = controls.addChild("panel_3", ModelPartBuilder.create(),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 2.0944F, 0.0F));

        ModelPartData rot3 = panel_3.addChild("rot3", ModelPartBuilder.create(),
                ModelTransform.of(15.0F, -14.25F, 0.0F, 0.0F, 0.0F, 0.3927F));

        ModelPartData board = rot3.addChild("board",
                ModelPartBuilder.create().uv(85, 61).cuboid(-1.5F, -1.4F, -4.0F, 6.0F, 1.0F, 8.0F, new Dilation(0.0F))
                        .uv(82, 29).cuboid(-1.5F, -1.4F, -4.0F, 6.0F, 1.0F, 8.0F, new Dilation(0.1F)),
                ModelTransform.pivot(-5.0F, 1.0F, 0.0F));

        ModelPartData bone30 = board.addChild("bone30",
                ModelPartBuilder.create().uv(112, 25).cuboid(-1.0F, -1.0F, -1.0F, 2.0F, 1.0F, 2.0F, new Dilation(0.1F)),
                ModelTransform.of(1.0F, -0.75F, 2.25F, 0.0F, -0.3927F, 0.0F));

        ModelPartData button = rot3.addChild("button",
                ModelPartBuilder.create().uv(91, 39).cuboid(-1.0F, -1.0F, -1.0F, 2.0F, 1.0F, 2.0F, new Dilation(0.0F))
                        .uv(34, 86).cuboid(-1.0F, -1.0F, -1.0F, 2.0F, 1.0F, 2.0F, new Dilation(0.1F)),
                ModelTransform.pivot(-0.5F, 0.5F, -5.5F));

        ModelPartData bone31 = button.addChild("bone31", ModelPartBuilder.create().uv(106, 66).cuboid(-0.5F, -1.25F,
                -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.1F)), ModelTransform.pivot(0.0F, -0.2F, 0.0F));

        ModelPartData button2 = rot3.addChild("button2",
                ModelPartBuilder.create().uv(91, 39).cuboid(-1.0F, -1.0F, -1.0F, 2.0F, 1.0F, 2.0F, new Dilation(0.0F))
                        .uv(34, 86).cuboid(-1.0F, -1.0F, -1.0F, 2.0F, 1.0F, 2.0F, new Dilation(0.1F)),
                ModelTransform.pivot(-0.5F, 0.5F, 5.5F));

        ModelPartData bone10 = button2.addChild("bone10", ModelPartBuilder.create().uv(106, 66).cuboid(-0.5F, -1.25F,
                -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.1F)), ModelTransform.pivot(0.0F, -0.2F, 0.0F));

        ModelPartData light3 = rot3.addChild("light3",
                ModelPartBuilder.create().uv(26, 105).cuboid(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                        .uv(78, 102).cuboid(-0.5F, -1.75F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)).uv(9, 73)
                        .cuboid(-0.5F, -3.75F, -0.5F, 1.0F, 2.0F, 1.0F, new Dilation(-0.2F)).uv(93, 101)
                        .cuboid(-0.5F, -2.25F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.3F)).uv(16, 81)
                        .cuboid(-1.0F, -1.75F, -0.35F, 2.0F, 1.0F, 1.0F, new Dilation(-0.3F)).uv(30, 86)
                        .cuboid(-0.5F, -3.75F, -0.5F, 1.0F, 0.0F, 1.0F, new Dilation(0.0F)).uv(10, 19)
                        .cuboid(-0.75F, -3.75F, -0.5F, 1.0F, 2.0F, 0.0F, new Dilation(0.0F)).uv(10, 19)
                        .cuboid(-0.75F, -3.75F, 0.5F, 1.0F, 2.0F, 0.0F, new Dilation(0.0F)).uv(26, 101)
                        .cuboid(-0.5F, -1.0F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.2F)),
                ModelTransform.of(-10.25F, 0.0F, 0.0F, 0.0F, 0.0F, -0.3927F));

        ModelPartData light4 = rot3.addChild("light4",
                ModelPartBuilder.create().uv(26, 105).cuboid(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                        .uv(78, 102).cuboid(-0.5F, -1.75F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)).uv(9, 73)
                        .cuboid(-0.5F, -3.75F, -0.5F, 1.0F, 2.0F, 1.0F, new Dilation(-0.2F)).uv(93, 101)
                        .cuboid(-0.5F, -2.25F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.3F)).uv(16, 81)
                        .cuboid(-1.0F, -1.75F, -0.35F, 2.0F, 1.0F, 1.0F, new Dilation(-0.3F)).uv(30, 86)
                        .cuboid(-0.5F, -3.75F, -0.5F, 1.0F, 0.0F, 1.0F, new Dilation(0.0F)).uv(10, 19)
                        .cuboid(-0.75F, -3.75F, -0.5F, 1.0F, 2.0F, 0.0F, new Dilation(0.0F)).uv(10, 19)
                        .cuboid(-0.75F, -3.75F, 0.5F, 1.0F, 2.0F, 0.0F, new Dilation(0.0F)).uv(26, 101)
                        .cuboid(-0.5F, -1.0F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.2F)),
                ModelTransform.of(-9.75F, 0.0F, -1.5F, 0.0F, 0.0F, -0.3927F));

        ModelPartData light5 = rot3.addChild("light5",
                ModelPartBuilder.create().uv(26, 105).cuboid(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                        .uv(78, 102).cuboid(-0.5F, -1.75F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)).uv(9, 73)
                        .cuboid(-0.5F, -3.75F, -0.5F, 1.0F, 2.0F, 1.0F, new Dilation(-0.2F)).uv(93, 101)
                        .cuboid(-0.5F, -2.25F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.3F)).uv(16, 81)
                        .cuboid(-1.0F, -1.75F, -0.35F, 2.0F, 1.0F, 1.0F, new Dilation(-0.3F)).uv(30, 86)
                        .cuboid(-0.5F, -3.75F, -0.5F, 1.0F, 0.0F, 1.0F, new Dilation(0.0F)).uv(10, 19)
                        .cuboid(-0.75F, -3.75F, -0.5F, 1.0F, 2.0F, 0.0F, new Dilation(0.0F)).uv(10, 19)
                        .cuboid(-0.75F, -3.75F, 0.5F, 1.0F, 2.0F, 0.0F, new Dilation(0.0F)).uv(26, 101)
                        .cuboid(-0.5F, -1.0F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.2F)),
                ModelTransform.of(-9.75F, 0.0F, 1.5F, 0.0F, 0.0F, -0.3927F));

        ModelPartData switch6 = rot3.addChild("switch6",
                ModelPartBuilder.create().uv(52, 105).cuboid(0.0F, -1.0F, -1.0F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.pivot(-8.25F, 0.5F, 3.0F));

        ModelPartData bone33 = switch6.addChild("bone33",
                ModelPartBuilder.create().uv(0, 47).cuboid(0.0F, -1.0F, -1.0F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(0.5F, -1.0F, -0.25F, 0.0F, 0.0F, -0.5672F));

        ModelPartData switch7 = rot3.addChild("switch7",
                ModelPartBuilder.create().uv(52, 105).cuboid(0.0F, -1.0F, -1.0F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.pivot(-8.25F, 0.5F, 1.5F));

        ModelPartData bone34 = switch7.addChild("bone34",
                ModelPartBuilder.create().uv(0, 47).cuboid(0.0F, -1.0F, -1.0F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(0.5F, -1.0F, -0.25F, 0.0F, 0.0F, -0.5672F));

        ModelPartData panel_4 = controls.addChild("panel_4", ModelPartBuilder.create(),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 3.1416F, 0.0F));

        ModelPartData rot4 = panel_4.addChild("rot4", ModelPartBuilder.create(),
                ModelTransform.of(15.0F, -14.25F, 0.0F, 0.0F, 0.0F, 0.3927F));

        ModelPartData panel = rot4.addChild("panel",
                ModelPartBuilder.create().uv(87, 78).cuboid(0.0F, -1.0F, -3.5F, 2.0F, 1.0F, 7.0F, new Dilation(0.0F))
                        .uv(87, 78).cuboid(-2.5F, -1.0F, -3.5F, 2.0F, 1.0F, 7.0F, new Dilation(0.0F)),
                ModelTransform.pivot(-5.0F, 0.75F, 0.0F));

        ModelPartData switch8 = panel.addChild("switch8",
                ModelPartBuilder.create().uv(52, 105).cuboid(0.0F, -1.0F, -1.0F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.pivot(-2.0F, -0.25F, 3.0F));

        ModelPartData bone35 = switch8.addChild("bone35",
                ModelPartBuilder.create().uv(0, 47).cuboid(0.0F, -1.0F, -1.0F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(0.5F, -1.0F, -0.25F, 0.0F, 0.0F, -0.5672F));

        ModelPartData switch9 = panel.addChild("switch9",
                ModelPartBuilder.create().uv(52, 105).cuboid(0.0F, -1.0F, -1.0F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.pivot(-2.0F, -0.25F, 1.5F));

        ModelPartData bone36 = switch9.addChild("bone36",
                ModelPartBuilder.create().uv(0, 47).cuboid(0.0F, -1.0F, -1.0F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(0.5F, -1.0F, -0.25F, 0.0F, 0.0F, -0.5672F));

        ModelPartData switch10 = panel.addChild("switch10",
                ModelPartBuilder.create().uv(52, 105).cuboid(0.0F, -1.0F, -1.0F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.pivot(-2.0F, -0.25F, -2.0F));

        ModelPartData bone37 = switch10.addChild("bone37",
                ModelPartBuilder.create().uv(0, 47).cuboid(0.0F, -1.0F, -1.0F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(0.5F, -1.0F, -0.25F, 0.0F, 0.0F, -0.5672F));

        ModelPartData switch11 = panel.addChild("switch11",
                ModelPartBuilder.create().uv(52, 105).cuboid(0.0F, -1.0F, -1.0F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.pivot(-2.0F, -0.25F, -0.5F));

        ModelPartData bone38 = switch11.addChild("bone38",
                ModelPartBuilder.create().uv(0, 47).cuboid(0.0F, -1.0F, -1.0F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(0.5F, -1.0F, -0.25F, 0.0F, 0.0F, -0.5672F));

        ModelPartData button3 = panel.addChild("button3",
                ModelPartBuilder.create().uv(13, 95).cuboid(-0.5F, -2.0F, -0.5F, 1.0F, 2.0F, 1.0F, new Dilation(-0.3F))
                        .uv(109, 90).cuboid(-0.5F, -2.0F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.1F)),
                ModelTransform.pivot(1.0F, 0.0F, 2.75F));

        ModelPartData button4 = panel.addChild("button4",
                ModelPartBuilder.create().uv(86, 93).cuboid(-0.5F, -2.0F, -0.5F, 1.0F, 2.0F, 1.0F, new Dilation(-0.3F)),
                ModelTransform.pivot(1.0F, 0.0F, 1.5F));

        ModelPartData cube_r75 = button4.addChild("cube_r75", ModelPartBuilder.create().uv(109, 86).cuboid(-0.5F, -2.0F,
                -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.1F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.7854F, 0.0F));

        ModelPartData button5 = panel.addChild("button5",
                ModelPartBuilder.create().uv(93, 0).cuboid(-0.5F, -2.0F, -0.5F, 1.0F, 2.0F, 1.0F, new Dilation(-0.3F)),
                ModelTransform.pivot(1.0F, 0.0F, -1.5F));

        ModelPartData cube_r76 = button5.addChild("cube_r76", ModelPartBuilder.create().uv(109, 77).cuboid(-0.5F, -2.0F,
                -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.1F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.7854F, 0.0F));

        ModelPartData button6 = panel.addChild("button6",
                ModelPartBuilder.create().uv(34, 90).cuboid(-0.5F, -2.0F, -0.5F, 1.0F, 2.0F, 1.0F, new Dilation(-0.3F))
                        .uv(75, 109).cuboid(-0.5F, -2.0F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.1F)),
                ModelTransform.pivot(1.0F, 0.0F, -2.75F));

        ModelPartData valve4 = panel.addChild("valve4",
                ModelPartBuilder.create().uv(56, 14).cuboid(-0.75F, -2.0F, -0.75F, 2.0F, 1.0F, 0.0F, new Dilation(0.0F))
                        .uv(57, 43).cuboid(-1.25F, -2.0F, 0.75F, 2.0F, 1.0F, 0.0F, new Dilation(0.0F)).uv(0, 33)
                        .cuboid(0.75F, -2.0F, -0.75F, 0.0F, 1.0F, 2.0F, new Dilation(0.0F)).uv(8, 33)
                        .cuboid(-0.75F, -2.0F, -1.25F, 0.0F, 1.0F, 2.0F, new Dilation(0.0F)).uv(5, 47)
                        .cuboid(-1.0F, -2.0F, -1.0F, 2.0F, 1.0F, 2.0F, new Dilation(-0.3F)),
                ModelTransform.pivot(1.1F, 0.25F, 0.0F));

        ModelPartData bone39 = valve4.addChild("bone39", ModelPartBuilder.create().uv(57, 29).cuboid(-0.65F, 0.3F,
                -0.25F, 1.0F, 0.0F, 1.0F, new Dilation(0.001F)), ModelTransform.pivot(0.0F, -2.0F, 0.0F));

        ModelPartData globe = rot4.addChild("globe",
                ModelPartBuilder.create().uv(24, 111).cuboid(-1.0F, -1.75F, -1.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.1F))
                        .uv(0, 44).cuboid(-1.5F, -2.75F, -0.25F, 3.0F, 0.0F, 1.0F, new Dilation(0.0F)).uv(89, 108)
                        .cuboid(-1.5F, -5.75F, -0.25F, 0.0F, 3.0F, 1.0F, new Dilation(0.0F)).uv(0, 30)
                        .cuboid(-1.5F, -5.25F, -0.25F, 3.0F, 0.0F, 1.0F, new Dilation(0.0F)).uv(50, 19)
                        .cuboid(-0.5F, -5.75F, -0.5F, 1.0F, 4.0F, 1.0F, new Dilation(-0.25F)).uv(26, 97)
                        .cuboid(-0.5F, -2.65F, -0.5F, 1.0F, 2.0F, 1.0F, new Dilation(-0.05F)).uv(64, 90)
                        .cuboid(-0.5F, -5.35F, -0.5F, 1.0F, 0.0F, 1.0F, new Dilation(0.0F)).uv(59, 90)
                        .cuboid(0.75F, -2.7F, -0.5F, 1.0F, 0.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(-9.0F, 0.75F, 0.0F, 0.0F, 0.0F, -0.3927F));

        ModelPartData bone40 = globe.addChild("bone40",
                ModelPartBuilder.create().uv(83, 101).cuboid(-1.5F, -2.5F, -1.5F, 3.0F, 3.0F, 3.0F, new Dilation(-0.5F))
                        .uv(99, 77).cuboid(-1.5F, -2.5F, -1.5F, 3.0F, 3.0F, 3.0F, new Dilation(-0.45F)),
                ModelTransform.of(0.0F, -3.0F, 0.0F, 0.0F, -0.7854F, 0.0F));

        ModelPartData lever6 = rot4.addChild("lever6",
                ModelPartBuilder.create().uv(110, 57).cuboid(-1.25F, -1.0F, -2.0F, 2.0F, 1.0F, 2.0F, new Dilation(0.2F))
                        .uv(85, 61).cuboid(-0.75F, -1.7F, -2.0F, 1.0F, 1.0F, 2.0F, new Dilation(0.2F)),
                ModelTransform.pivot(-0.75F, 0.75F, 5.0F));

        ModelPartData bone41 = lever6.addChild("bone41",
                ModelPartBuilder.create().uv(78, 88).cuboid(-0.5F, -1.75F, -0.9F, 1.0F, 0.0F, 1.0F, new Dilation(0.0F))
                        .uv(78, 88).cuboid(-0.5F, -1.75F, -2.1F, 1.0F, 0.0F, 1.0F, new Dilation(0.0F)).uv(0, 63)
                        .cuboid(-0.5F, -1.75F, -2.1F, 1.0F, 2.0F, 0.0F, new Dilation(0.0F)).uv(0, 63)
                        .cuboid(-0.5F, -1.75F, 0.1F, 1.0F, 2.0F, 0.0F, new Dilation(0.0F)).uv(0, 63)
                        .cuboid(-0.5F, -1.75F, -1.1F, 1.0F, 2.0F, 0.0F, new Dilation(0.0F)).uv(0, 63)
                        .cuboid(-0.5F, -1.75F, -0.9F, 1.0F, 2.0F, 0.0F, new Dilation(0.0F)).uv(0, 110)
                        .cuboid(-0.5F, -2.559F, -2.5F, 1.0F, 1.0F, 3.0F, new Dilation(-0.2F)),
                ModelTransform.of(-0.25F, -1.25F, 0.0F, 0.0F, 0.0F, -0.829F));

        ModelPartData crank = rot4.addChild("crank",
                ModelPartBuilder.create().uv(105, 103)
                        .cuboid(-1.5F, -0.5F, -1.5F, 3.0F, 1.0F, 3.0F, new Dilation(-0.2F)).uv(0, 11)
                        .cuboid(-0.5F, -2.5F, -0.5F, 1.0F, 3.0F, 1.0F, new Dilation(-0.1F)).uv(44, 99)
                        .cuboid(-0.5F, -3.0F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.3F)).uv(36, 99)
                        .cuboid(-0.5F, -2.0F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.pivot(-1.0F, 0.0F, -4.5F));

        ModelPartData bone42 = crank.addChild("bone42",
                ModelPartBuilder.create().uv(64, 37).cuboid(-1.5F, -0.95F, -1.5F, 3.0F, 1.0F, 3.0F, new Dilation(-0.3F))
                        .uv(72, 29).cuboid(-1.5F, -2.3F, -0.5F, 1.0F, 2.0F, 1.0F, new Dilation(-0.35F)),
                ModelTransform.pivot(0.0F, -1.9F, 0.0F));

        ModelPartData vent2 = rot4.addChild("vent2",
                ModelPartBuilder.create().uv(50, 43).cuboid(-0.85F, 0.0F, -1.0F, 2.0F, 0.0F, 2.0F, new Dilation(0.0F))
                        .uv(97, 55).cuboid(-1.4F, -0.08F, -1.5F, 3.0F, 1.0F, 3.0F, new Dilation(-0.1F)),
                ModelTransform.pivot(-1.5F, -0.2F, 0.0F));

        ModelPartData cube_r77 = vent2.addChild("cube_r77",
                ModelPartBuilder.create().uv(66, 65).cuboid(-0.5F, 0.0F, 0.0F, 2.0F, 1.0F, 0.0F, new Dilation(0.0F))
                        .uv(23, 72).cuboid(-0.5F, 0.0F, -2.0F, 2.0F, 1.0F, 0.0F, new Dilation(0.0F)).uv(50, 29)
                        .cuboid(-0.5F, 0.0F, -2.0F, 2.0F, 0.0F, 2.0F, new Dilation(0.0F)),
                ModelTransform.of(-0.85F, -0.001F, 1.0F, 0.0F, 0.0F, -0.4363F));

        ModelPartData panel_5 = controls.addChild("panel_5", ModelPartBuilder.create(),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -2.0944F, 0.0F));

        ModelPartData rot5 = panel_5.addChild("rot5", ModelPartBuilder.create(),
                ModelTransform.of(15.0F, -14.25F, 0.0F, 0.0F, 0.0F, 0.3927F));

        ModelPartData clock = rot5.addChild("clock",
                ModelPartBuilder.create().uv(93, 7).cuboid(-2.0F, -1.0F, -2.0F, 4.0F, 1.0F, 4.0F, new Dilation(0.0F))
                        .uv(23, 63).cuboid(-1.75F, -1.1F, -1.75F, 4.0F, 0.0F, 4.0F, new Dilation(0.0F)).uv(110, 5)
                        .cuboid(-1.0F, -1.25F, -1.0F, 2.0F, 1.0F, 2.0F, new Dilation(0.01F)),
                ModelTransform.pivot(-9.0F, 0.0F, 0.0F));

        ModelPartData bone44 = clock.addChild("bone44", ModelPartBuilder.create().uv(59, 86).cuboid(-0.9F, -1.45F,
                -0.25F, 1.0F, 0.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(0.1F, 0.4F, 0.0F));

        ModelPartData lever7 = rot5.addChild("lever7",
                ModelPartBuilder.create().uv(87, 87).cuboid(-1.0F, -1.0F, -0.5F, 2.0F, 1.0F, 1.0F, new Dilation(0.0F))
                        .uv(94, 71).cuboid(-1.0F, -0.25F, -1.0F, 2.0F, 1.0F, 2.0F, new Dilation(0.2F)),
                ModelTransform.pivot(-5.25F, 0.25F, 0.0F));

        ModelPartData bone45 = lever7.addChild("bone45",
                ModelPartBuilder.create().uv(57, 19).cuboid(-0.25F, -2.0F, 0.0F, 1.0F, 2.0F, 0.0F, new Dilation(0.0F))
                        .uv(73, 112).cuboid(-0.5F, -2.5F, -1.0F, 1.0F, 1.0F, 2.0F, new Dilation(-0.25F)),
                ModelTransform.of(0.0F, -0.25F, 0.0F, 0.0F, 0.0F, -0.5061F));

        ModelPartData lever8 = rot5.addChild("lever8",
                ModelPartBuilder.create().uv(60, 98).cuboid(-1.0F, -2.0F, -2.0F, 2.0F, 2.0F, 4.0F, new Dilation(0.0F)),
                ModelTransform.pivot(-1.5F, 0.5F, -3.5F));

        ModelPartData bone46 = lever8.addChild("bone46",
                ModelPartBuilder.create().uv(43, 0).cuboid(-0.75F, -3.0F, -1.25F, 1.0F, 3.0F, 0.0F, new Dilation(0.0F))
                        .uv(97, 47).cuboid(-0.5F, -3.45F, -1.75F, 1.0F, 2.0F, 1.0F, new Dilation(-0.25F)),
                ModelTransform.of(0.0F, -1.0F, 0.0F, 0.0F, 0.0F, 0.8727F));

        ModelPartData bone47 = lever8.addChild("bone47",
                ModelPartBuilder.create().uv(43, 0).cuboid(-0.75F, -3.0F, -1.25F, 1.0F, 3.0F, 0.0F, new Dilation(0.0F))
                        .uv(97, 47).cuboid(-0.5F, -3.45F, -1.75F, 1.0F, 2.0F, 1.0F, new Dilation(-0.25F)),
                ModelTransform.of(0.0F, -1.0F, 1.25F, 0.0F, 0.0F, 0.8727F));

        ModelPartData bone48 = lever8.addChild("bone48",
                ModelPartBuilder.create().uv(43, 0).cuboid(-0.75F, -3.0F, -1.25F, 1.0F, 3.0F, 0.0F, new Dilation(0.0F))
                        .uv(97, 47).cuboid(-0.5F, -3.45F, -1.75F, 1.0F, 2.0F, 1.0F, new Dilation(-0.25F)),
                ModelTransform.of(0.0F, -1.0F, 2.5F, 0.0F, 0.0F, 0.8727F));

        ModelPartData crank2 = rot5.addChild("crank2",
                ModelPartBuilder.create().uv(105, 103)
                        .cuboid(-1.5F, -0.5F, -1.5F, 3.0F, 1.0F, 3.0F, new Dilation(-0.2F)).uv(0, 11)
                        .cuboid(-0.5F, -2.5F, -0.5F, 1.0F, 3.0F, 1.0F, new Dilation(-0.1F)).uv(44, 99)
                        .cuboid(-0.5F, -3.0F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.3F)).uv(36, 99)
                        .cuboid(-0.5F, -2.0F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.pivot(-1.75F, 0.0F, 4.0F));

        ModelPartData bone18 = crank2.addChild("bone18",
                ModelPartBuilder.create().uv(64, 37).cuboid(-1.5F, -0.95F, -1.5F, 3.0F, 1.0F, 3.0F, new Dilation(-0.3F))
                        .uv(72, 29).cuboid(0.5F, -2.3F, -0.5F, 1.0F, 2.0F, 1.0F, new Dilation(-0.35F)),
                ModelTransform.pivot(0.0F, -1.9F, 0.0F));

        ModelPartData light6 = rot5.addChild("light6",
                ModelPartBuilder.create().uv(85, 71).cuboid(-1.0F, -0.85F, -1.0F, 2.0F, 1.0F, 2.0F, new Dilation(-0.1F))
                        .uv(6, 81).cuboid(0.15F, -0.85F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.2F)).uv(108, 97)
                        .cuboid(-1.0F, -1.85F, -1.0F, 2.0F, 2.0F, 2.0F, new Dilation(-0.4F)).uv(19, 112)
                        .cuboid(-0.5F, -1.95F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.2F)).uv(66, 69)
                        .cuboid(-0.5F, -3.55F, -0.5F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.pivot(-6.0F, 0.0F, 2.5F));

        ModelPartData bone32 = light6.addChild("bone32", ModelPartBuilder.create().uv(66, 61).cuboid(-0.5F, -3.55F,
                -0.5F, 1.0F, 2.0F, 1.0F, new Dilation(0.01F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData switch12 = light6.addChild("switch12",
                ModelPartBuilder.create().uv(9, 44).cuboid(0.0F, -1.0F, 0.0F, 1.0F, 1.0F, 0.0F, new Dilation(0.0F)),
                ModelTransform.of(0.9F, -0.1F, 0.0F, 0.0F, 0.7854F, 0.0F));

        ModelPartData light7 = rot5.addChild("light7",
                ModelPartBuilder.create().uv(85, 71).cuboid(-1.0F, -0.85F, -1.0F, 2.0F, 1.0F, 2.0F, new Dilation(-0.1F))
                        .uv(6, 81).cuboid(0.15F, -0.85F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.2F)).uv(108, 97)
                        .cuboid(-1.0F, -1.85F, -1.0F, 2.0F, 2.0F, 2.0F, new Dilation(-0.4F)).uv(19, 112)
                        .cuboid(-0.5F, -1.95F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.2F)).uv(66, 69)
                        .cuboid(-0.5F, -3.55F, -0.5F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.pivot(-6.0F, 0.0F, -2.5F));

        ModelPartData bone29 = light7.addChild("bone29", ModelPartBuilder.create().uv(66, 61).cuboid(-0.5F, -3.55F,
                -0.5F, 1.0F, 2.0F, 1.0F, new Dilation(0.01F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData switch13 = light7.addChild("switch13",
                ModelPartBuilder.create().uv(9, 44).cuboid(0.0F, -1.0F, 0.0F, 1.0F, 1.0F, 0.0F, new Dilation(0.0F)),
                ModelTransform.of(0.9F, -0.1F, 0.0F, 0.0F, 0.7854F, 0.0F));

        ModelPartData valve5 = rot5.addChild("valve5",
                ModelPartBuilder.create().uv(82, 39).cuboid(-1.0F, -1.0F, -1.0F, 2.0F, 1.0F, 2.0F, new Dilation(0.0F))
                        .uv(56, 14).cuboid(-0.75F, -1.75F, -0.75F, 2.0F, 1.0F, 0.0F, new Dilation(0.0F)).uv(5, 47)
                        .cuboid(-1.0F, -1.75F, -1.0F, 2.0F, 1.0F, 2.0F, new Dilation(-0.3F)).uv(8, 33)
                        .cuboid(-0.75F, -1.75F, -1.25F, 0.0F, 1.0F, 2.0F, new Dilation(0.0F)).uv(0, 33)
                        .cuboid(0.75F, -1.75F, -0.75F, 0.0F, 1.0F, 2.0F, new Dilation(0.0F)).uv(57, 43)
                        .cuboid(-1.25F, -1.75F, 0.75F, 2.0F, 1.0F, 0.0F, new Dilation(0.0F)),
                ModelTransform.pivot(-1.5F, 0.25F, 0.5F));

        ModelPartData bone49 = valve5.addChild("bone49", ModelPartBuilder.create().uv(57, 29).cuboid(-0.65F, -0.2F,
                -0.25F, 1.0F, 0.0F, 1.0F, new Dilation(0.001F)), ModelTransform.pivot(0.0F, -1.25F, 0.0F));

        ModelPartData panel_6 = controls.addChild("panel_6", ModelPartBuilder.create(),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData rot6 = panel_6.addChild("rot6", ModelPartBuilder.create(),
                ModelTransform.of(15.0F, -14.25F, 0.0F, 0.0F, 0.0F, 0.3927F));

        ModelPartData lever9 = rot6.addChild("lever9",
                ModelPartBuilder.create().uv(43, 14).cuboid(-2.0F, 0.0F, -1.0F, 5.0F, 1.0F, 2.0F, new Dilation(0.0F))
                        .uv(53, 74).cuboid(-0.5F, -1.0F, 0.8F, 2.0F, 2.0F, 1.0F, new Dilation(-0.2F)).uv(53, 74)
                        .cuboid(-0.5F, -1.0F, -1.8F, 2.0F, 2.0F, 1.0F, new Dilation(-0.2F)).uv(44, 107)
                        .cuboid(1.5F, -0.25F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.1F)).uv(44, 107)
                        .cuboid(0.0F, -0.25F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.1F)).uv(44, 107)
                        .cuboid(-1.5F, -0.25F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.1F)).uv(19, 112)
                        .cuboid(0.0F, -0.75F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.2F)).uv(19, 112)
                        .cuboid(-1.5F, -0.75F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.2F)).uv(19, 112)
                        .cuboid(1.5F, -0.75F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.2F)).uv(59, 82)
                        .cuboid(1.5F, -2.5F, -0.5F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F)).uv(66, 69)
                        .cuboid(-1.5F, -2.5F, -0.5F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F)).uv(79, 82)
                        .cuboid(0.0F, -2.5F, -0.5F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.pivot(-3.5F, -0.6F, 3.0F));

        ModelPartData bone63 = lever9.addChild("bone63",
                ModelPartBuilder.create().uv(50, 37).cuboid(1.0F, -3.0F, -0.5F, 1.0F, 2.0F, 1.0F, new Dilation(0.01F)),
                ModelTransform.pivot(0.5F, 0.5F, 0.0F));

        ModelPartData bone43 = lever9.addChild("bone43",
                ModelPartBuilder.create().uv(66, 61).cuboid(-2.0F, -3.0F, -0.5F, 1.0F, 2.0F, 1.0F, new Dilation(0.01F)),
                ModelTransform.pivot(0.5F, 0.5F, 0.0F));

        ModelPartData bone62 = lever9.addChild("bone62",
                ModelPartBuilder.create().uv(9, 63).cuboid(-0.5F, -3.0F, -0.5F, 1.0F, 2.0F, 1.0F, new Dilation(0.01F)),
                ModelTransform.pivot(0.5F, 0.5F, 0.0F));

        ModelPartData bone50 = lever9.addChild("bone50",
                ModelPartBuilder.create().uv(56, 47).cuboid(-0.5F, -2.0F, 0.0F, 1.0F, 2.0F, 0.0F, new Dilation(0.0F))
                        .uv(87, 82).cuboid(-0.5F, -2.0F, 0.0F, 1.0F, 0.0F, 2.0F, new Dilation(0.0F)).uv(52, 0)
                        .cuboid(-0.5F, -4.0F, 2.0F, 1.0F, 2.0F, 0.0F, new Dilation(0.0F)).uv(41, 86)
                        .cuboid(-0.5F, -4.0F, 1.0F, 1.0F, 0.0F, 1.0F, new Dilation(0.0F)).uv(55, 97)
                        .cuboid(-0.5F, -4.5F, -2.0F, 1.0F, 1.0F, 3.0F, new Dilation(0.0F)),
                ModelTransform.of(0.5F, 0.0F, 1.3F, 0.0F, 0.0F, 1.0908F));

        ModelPartData panel2 = rot6.addChild("panel2",
                ModelPartBuilder.create().uv(86, 93).cuboid(-2.5F, -1.0F, -3.0F, 2.0F, 1.0F, 6.0F, new Dilation(0.0F)),
                ModelTransform.of(-6.0F, 0.75F, 0.0F, 0.0F, 0.0F, 0.2618F));

        ModelPartData switch14 = panel2.addChild("switch14",
                ModelPartBuilder.create().uv(52, 105).cuboid(0.0F, -1.0F, -1.0F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.pivot(-2.0F, -0.25F, -1.0F));

        ModelPartData bone51 = switch14.addChild("bone51",
                ModelPartBuilder.create().uv(0, 47).cuboid(0.0F, -1.0F, -1.0F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(0.5F, -1.0F, -0.25F, 0.0F, 0.0F, -0.5672F));

        ModelPartData switch15 = panel2.addChild("switch15",
                ModelPartBuilder.create().uv(52, 105).cuboid(0.0F, -1.0F, -1.0F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.pivot(-2.0F, -0.25F, 0.5F));

        ModelPartData bone52 = switch15.addChild("bone52",
                ModelPartBuilder.create().uv(0, 47).cuboid(0.0F, -1.0F, -1.0F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(0.5F, -1.0F, -0.25F, 0.0F, 0.0F, -0.5672F));

        ModelPartData switch16 = panel2.addChild("switch16",
                ModelPartBuilder.create().uv(52, 105).cuboid(0.0F, -1.0F, -1.0F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.pivot(-2.0F, -0.25F, 2.0F));

        ModelPartData bone53 = switch16.addChild("bone53",
                ModelPartBuilder.create().uv(0, 47).cuboid(0.0F, -1.0F, -1.0F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(0.5F, -1.0F, -0.25F, 0.0F, 0.0F, -0.5672F));

        ModelPartData lever10 = rot6.addChild("lever10",
                ModelPartBuilder.create().uv(97, 23).cuboid(-3.0F, -1.2F, -1.0F, 4.0F, 1.0F, 3.0F, new Dilation(-0.2F))
                        .uv(87, 78).cuboid(-1.5F, -1.75F, -0.5F, 1.0F, 1.0F, 2.0F, new Dilation(-0.2F)),
                ModelTransform.pivot(-1.5F, 0.5F, -3.75F));

        ModelPartData bone54 = lever10.addChild("bone54",
                ModelPartBuilder.create().uv(47, 47).cuboid(-0.75F, -1.25F, -0.9F, 1.0F, 2.0F, 0.0F, new Dilation(0.0F))
                        .uv(47, 47).cuboid(-0.75F, -1.25F, 0.9F, 1.0F, 2.0F, 0.0F, new Dilation(0.0F)).uv(109, 81)
                        .cuboid(-0.5F, -2.0F, -1.5F, 1.0F, 1.0F, 3.0F, new Dilation(-0.25F)).uv(34, 107)
                        .cuboid(-0.5F, -2.4F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.35F)).uv(97, 47)
                        .cuboid(-0.5F, -3.65F, -0.5F, 1.0F, 2.0F, 1.0F, new Dilation(-0.25F)),
                ModelTransform.of(-1.0F, -1.25F, 0.5F, 0.0F, 0.0F, 1.3963F));

        ModelPartData meter = rot6.addChild("meter",
                ModelPartBuilder.create().uv(109, 20).cuboid(-0.5F, -1.1F, -1.5F, 1.0F, 1.0F, 3.0F, new Dilation(0.0F))
                        .uv(109, 20).cuboid(1.0F, -1.1F, -1.5F, 1.0F, 1.0F, 3.0F, new Dilation(0.0F)).uv(43, 10)
                        .cuboid(-0.75F, -0.75F, -1.0F, 3.0F, 0.0F, 2.0F, new Dilation(0.0F)),
                ModelTransform.pivot(-1.75F, 0.75F, 0.0F));

        ModelPartData bone55 = meter.addChild("bone55",
                ModelPartBuilder.create().uv(25, 86).cuboid(-0.5F, 0.0F, -0.75F, 1.0F, 0.0F, 2.0F, new Dilation(0.0F)),
                ModelTransform.pivot(0.0F, -1.15F, 0.0F));

        ModelPartData bone56 = meter.addChild("bone56",
                ModelPartBuilder.create().uv(25, 86).cuboid(-0.5F, 0.0F, -0.75F, 1.0F, 0.0F, 2.0F, new Dilation(0.0F)),
                ModelTransform.pivot(1.5F, -1.15F, 0.0F));

        ModelPartData light8 = rot6.addChild("light8",
                ModelPartBuilder.create().uv(26, 105).cuboid(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                        .uv(78, 102).cuboid(-0.5F, -1.75F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)).uv(9, 73)
                        .cuboid(-0.5F, -3.75F, -0.5F, 1.0F, 2.0F, 1.0F, new Dilation(-0.2F)).uv(93, 101)
                        .cuboid(-0.5F, -2.25F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.3F)).uv(16, 81)
                        .cuboid(-1.0F, -1.75F, -0.35F, 2.0F, 1.0F, 1.0F, new Dilation(-0.3F)).uv(30, 86)
                        .cuboid(-0.5F, -3.75F, -0.5F, 1.0F, 0.0F, 1.0F, new Dilation(0.0F)).uv(10, 19)
                        .cuboid(-0.75F, -3.75F, -0.5F, 1.0F, 2.0F, 0.0F, new Dilation(0.0F)).uv(10, 19)
                        .cuboid(-0.75F, -3.75F, 0.5F, 1.0F, 2.0F, 0.0F, new Dilation(0.0F)).uv(26, 101)
                        .cuboid(-0.5F, -1.0F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.2F)),
                ModelTransform.of(-9.75F, 0.0F, -1.5F, 0.0F, 0.0F, -0.3927F));

        ModelPartData light9 = rot6.addChild("light9",
                ModelPartBuilder.create().uv(26, 105).cuboid(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                        .uv(78, 102).cuboid(-0.5F, -1.75F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)).uv(9, 73)
                        .cuboid(-0.5F, -3.75F, -0.5F, 1.0F, 2.0F, 1.0F, new Dilation(-0.2F)).uv(93, 101)
                        .cuboid(-0.5F, -2.25F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.3F)).uv(16, 81)
                        .cuboid(-1.0F, -1.75F, -0.35F, 2.0F, 1.0F, 1.0F, new Dilation(-0.3F)).uv(30, 86)
                        .cuboid(-0.5F, -3.75F, -0.5F, 1.0F, 0.0F, 1.0F, new Dilation(0.0F)).uv(10, 19)
                        .cuboid(-0.75F, -3.75F, -0.5F, 1.0F, 2.0F, 0.0F, new Dilation(0.0F)).uv(10, 19)
                        .cuboid(-0.75F, -3.75F, 0.5F, 1.0F, 2.0F, 0.0F, new Dilation(0.0F)).uv(26, 101)
                        .cuboid(-0.5F, -1.0F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.2F)),
                ModelTransform.of(-9.75F, 0.0F, 1.5F, 0.0F, 0.0F, -0.3927F));

        ModelPartData vent = rot6.addChild("vent",
                ModelPartBuilder.create().uv(50, 43).cuboid(-0.85F, 0.0F, -1.0F, 2.0F, 0.0F, 2.0F, new Dilation(0.0F))
                        .uv(97, 55).cuboid(-1.4F, -0.08F, -1.5F, 3.0F, 1.0F, 3.0F, new Dilation(-0.1F)),
                ModelTransform.pivot(-4.5F, -0.2F, 0.0F));

        ModelPartData cube_r78 = vent.addChild("cube_r78",
                ModelPartBuilder.create().uv(66, 65).cuboid(-0.5F, 0.0F, 0.0F, 2.0F, 1.0F, 0.0F, new Dilation(0.0F))
                        .uv(23, 72).cuboid(-0.5F, 0.0F, -2.0F, 2.0F, 1.0F, 0.0F, new Dilation(0.0F)).uv(50, 29)
                        .cuboid(-0.5F, 0.0F, -2.0F, 2.0F, 0.0F, 2.0F, new Dilation(0.0F)),
                ModelTransform.of(-0.85F, -0.001F, 1.0F, 0.0F, 0.0F, -0.4363F));

        ModelPartData pipes = steam.addChild("pipes", ModelPartBuilder.create(),
                ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData bone85 = pipes.addChild("bone85",
                ModelPartBuilder.create().uv(0, 73).cuboid(6.25F, -2.0F, -1.0F, 2.0F, 1.0F, 2.0F, new Dilation(0.2F))
                        .uv(80, 108).cuboid(6.25F, -5.0F, -1.0F, 2.0F, 3.0F, 2.0F, new Dilation(-0.2F)),
                ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData bone84 = bone85.addChild("bone84", ModelPartBuilder.create().uv(17, 97).cuboid(9.0F, -12.0F,
                -2.75F, 2.0F, 12.0F, 2.0F, new Dilation(-0.3F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData bone86 = bone85.addChild("bone86", ModelPartBuilder.create().uv(57, 105).cuboid(9.0F, -11.0F,
                -2.75F, 2.0F, 6.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData bone87 = bone85.addChild("bone87", ModelPartBuilder.create().uv(105, 108).cuboid(9.0F, -2.0F,
                -2.75F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData pipes2 = pipes.addChild("pipes2", ModelPartBuilder.create(),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData bone77 = pipes2.addChild("bone77", ModelPartBuilder.create(),
                ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData bone78 = bone77.addChild("bone78",
                ModelPartBuilder.create().uv(16, 84).cuboid(-1.0F, -2.5F, -1.0F, 2.0F, 5.0F, 2.0F, new Dilation(-0.3F)),
                ModelTransform.pivot(7.2F, -8.9F, -0.35F));

        ModelPartData bone79 = bone77.addChild("bone79",
                ModelPartBuilder.create().uv(96, 39).cuboid(-1.0F, -1.0F, -2.5F, 2.0F, 2.0F, 5.0F, new Dilation(-0.3F)),
                ModelTransform.pivot(7.2F, -6.0F, 1.15F));

        ModelPartData bone80 = bone77.addChild("bone80", ModelPartBuilder.create().uv(66, 109).cuboid(-1.0F, -1.0F,
                -1.0F, 2.0F, 2.0F, 2.0F, new Dilation(-0.3F)), ModelTransform.pivot(8.6F, -6.0F, 2.65F));

        ModelPartData bone81 = bone77.addChild("bone81",
                ModelPartBuilder.create().uv(45, 97).cuboid(-1.0F, -1.0F, -2.5F, 2.0F, 2.0F, 5.0F, new Dilation(-0.3F)),
                ModelTransform.pivot(10.0F, -6.0F, 1.15F));

        ModelPartData bone82 = bone77.addChild("bone82", ModelPartBuilder.create().uv(96, 103).cuboid(-1.0F, -3.5F,
                -1.0F, 2.0F, 7.0F, 2.0F, new Dilation(-0.3F)), ModelTransform.pivot(10.0F, -3.5F, -1.75F));

        ModelPartData bone83 = bone77.addChild("bone83", ModelPartBuilder.create().uv(105, 108).cuboid(-1.0F, -1.0F,
                -1.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(10.0F, -1.0F, -1.75F));

        ModelPartData pipes3 = pipes2.addChild("pipes3", ModelPartBuilder.create(),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData bone88 = pipes3.addChild("bone88",
                ModelPartBuilder.create().uv(0, 73).cuboid(6.25F, -2.0F, -1.0F, 2.0F, 1.0F, 2.0F, new Dilation(0.2F))
                        .uv(80, 108).cuboid(6.25F, -5.0F, -1.0F, 2.0F, 3.0F, 2.0F, new Dilation(-0.2F)),
                ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData bone89 = bone88.addChild("bone89", ModelPartBuilder.create().uv(17, 97).cuboid(-1.0F, -6.0F,
                -1.0F, 2.0F, 12.0F, 2.0F, new Dilation(-0.3F)), ModelTransform.pivot(10.0F, -6.0F, -1.75F));

        ModelPartData bone90 = bone88.addChild("bone90",
                ModelPartBuilder.create().uv(57, 105).cuboid(-1.0F, -3.0F, -1.0F, 2.0F, 6.0F, 2.0F, new Dilation(0.0F)),
                ModelTransform.pivot(10.0F, -8.0F, -1.75F));

        ModelPartData bone91 = bone88.addChild("bone91", ModelPartBuilder.create().uv(105, 108).cuboid(-1.0F, -1.0F,
                -1.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(10.0F, -1.0F, -1.75F));

        ModelPartData pipes4 = pipes3.addChild("pipes4", ModelPartBuilder.create(),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData bone = pipes4.addChild("bone", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData bone64 = bone.addChild("bone64",
                ModelPartBuilder.create().uv(16, 84).cuboid(-1.0F, -2.5F, -1.0F, 2.0F, 5.0F, 2.0F, new Dilation(-0.3F)),
                ModelTransform.pivot(7.2F, -8.9F, -0.35F));

        ModelPartData bone65 = bone.addChild("bone65",
                ModelPartBuilder.create().uv(96, 39).cuboid(-1.0F, -1.0F, -2.5F, 2.0F, 2.0F, 5.0F, new Dilation(-0.3F)),
                ModelTransform.pivot(7.2F, -6.0F, 1.15F));

        ModelPartData bone67 = bone.addChild("bone67", ModelPartBuilder.create().uv(66, 109).cuboid(-1.0F, -1.0F, -1.0F,
                2.0F, 2.0F, 2.0F, new Dilation(-0.3F)), ModelTransform.pivot(8.6F, -6.0F, 2.65F));

        ModelPartData bone66 = bone.addChild("bone66",
                ModelPartBuilder.create().uv(45, 97).cuboid(-1.0F, -1.0F, -2.5F, 2.0F, 2.0F, 5.0F, new Dilation(-0.3F)),
                ModelTransform.pivot(10.0F, -6.0F, 1.15F));

        ModelPartData bone68 = bone.addChild("bone68", ModelPartBuilder.create().uv(96, 103).cuboid(-1.0F, -3.5F, -1.0F,
                2.0F, 7.0F, 2.0F, new Dilation(-0.3F)), ModelTransform.pivot(10.0F, -3.5F, -1.75F));

        ModelPartData bone69 = bone.addChild("bone69", ModelPartBuilder.create().uv(105, 108).cuboid(-1.0F, -1.0F,
                -1.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(10.0F, -1.0F, -1.75F));

        ModelPartData pipes5 = pipes4.addChild("pipes5", ModelPartBuilder.create(),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData bone92 = pipes5.addChild("bone92",
                ModelPartBuilder.create().uv(0, 73).cuboid(6.25F, -2.0F, -1.0F, 2.0F, 1.0F, 2.0F, new Dilation(0.2F))
                        .uv(80, 108).cuboid(6.25F, -5.0F, -1.0F, 2.0F, 3.0F, 2.0F, new Dilation(-0.2F)),
                ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData bone93 = bone92.addChild("bone93", ModelPartBuilder.create().uv(17, 97).cuboid(-1.0F, -6.0F,
                -1.0F, 2.0F, 12.0F, 2.0F, new Dilation(-0.3F)), ModelTransform.pivot(10.0F, -6.0F, -1.75F));

        ModelPartData bone94 = bone92.addChild("bone94",
                ModelPartBuilder.create().uv(57, 105).cuboid(-1.0F, -3.0F, -1.0F, 2.0F, 6.0F, 2.0F, new Dilation(0.0F)),
                ModelTransform.pivot(10.0F, -8.0F, -1.75F));

        ModelPartData bone95 = bone92.addChild("bone95", ModelPartBuilder.create().uv(105, 108).cuboid(-1.0F, -1.0F,
                -1.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(10.0F, -1.0F, -1.75F));

        ModelPartData pipes6 = pipes5.addChild("pipes6", ModelPartBuilder.create(),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData bone70 = pipes6.addChild("bone70", ModelPartBuilder.create(),
                ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData bone71 = bone70.addChild("bone71",
                ModelPartBuilder.create().uv(16, 84).cuboid(-1.0F, -2.5F, -1.0F, 2.0F, 5.0F, 2.0F, new Dilation(-0.3F)),
                ModelTransform.pivot(7.2F, -8.9F, -0.35F));

        ModelPartData bone72 = bone70.addChild("bone72",
                ModelPartBuilder.create().uv(96, 39).cuboid(-1.0F, -1.0F, -2.5F, 2.0F, 2.0F, 5.0F, new Dilation(-0.3F)),
                ModelTransform.pivot(7.2F, -6.0F, 1.15F));

        ModelPartData bone73 = bone70.addChild("bone73", ModelPartBuilder.create().uv(66, 109).cuboid(-1.0F, -1.0F,
                -1.0F, 2.0F, 2.0F, 2.0F, new Dilation(-0.3F)), ModelTransform.pivot(8.6F, -6.0F, 2.65F));

        ModelPartData bone74 = bone70.addChild("bone74",
                ModelPartBuilder.create().uv(45, 97).cuboid(-1.0F, -1.0F, -2.5F, 2.0F, 2.0F, 5.0F, new Dilation(-0.3F)),
                ModelTransform.pivot(10.0F, -6.0F, 1.15F));

        ModelPartData bone75 = bone70.addChild("bone75", ModelPartBuilder.create().uv(96, 103).cuboid(-1.0F, -3.5F,
                -1.0F, 2.0F, 7.0F, 2.0F, new Dilation(-0.3F)), ModelTransform.pivot(10.0F, -3.5F, -1.75F));

        ModelPartData bone76 = bone70.addChild("bone76", ModelPartBuilder.create().uv(105, 108).cuboid(-1.0F, -1.0F,
                -1.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(10.0F, -1.0F, -1.75F));

        ModelPartData gears = steam.addChild("gears", ModelPartBuilder.create(),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.5236F, 0.0F));

        ModelPartData cube_r79 = gears.addChild("cube_r79",
                ModelPartBuilder.create().uv(0, 0).cuboid(-3.0F, -17.25F, -0.5F, 7.0F, 9.0F, 1.0F, new Dilation(-0.2F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.6545F));

        ModelPartData gears2 = gears.addChild("gears2", ModelPartBuilder.create(),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData cube_r80 = gears2.addChild("cube_r80",
                ModelPartBuilder.create().uv(0, 0).cuboid(-3.0F, -17.25F, -0.5F, 7.0F, 9.0F, 1.0F, new Dilation(-0.2F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.6545F));

        ModelPartData gears3 = gears2.addChild("gears3", ModelPartBuilder.create(),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData cube_r81 = gears3.addChild("cube_r81",
                ModelPartBuilder.create().uv(0, 0).cuboid(-3.0F, -17.25F, -0.5F, 7.0F, 9.0F, 1.0F, new Dilation(-0.2F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.6545F));

        ModelPartData gears4 = gears3.addChild("gears4", ModelPartBuilder.create(),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData cube_r82 = gears4.addChild("cube_r82", ModelPartBuilder.create().uv(50, 33).cuboid(-2.0F, -10.5F,
                -0.5F, 11.0F, 2.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.1745F));

        ModelPartData wheel = gears4.addChild("wheel",
                ModelPartBuilder.create().uv(69, 93).cuboid(-4.0F, -4.0F, 0.0F, 8.0F, 8.0F, 0.0F, new Dilation(0.0F))
                        .uv(40, 63).cuboid(-4.5F, -4.5F, -0.5F, 9.0F, 9.0F, 1.0F, new Dilation(-0.4F)),
                ModelTransform.of(9.0F, -8.0F, 0.0F, 0.0F, 0.0F, -2.5744F));

        ModelPartData gears5 = gears4.addChild("gears5", ModelPartBuilder.create(),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData cube_r83 = gears5.addChild("cube_r83",
                ModelPartBuilder.create().uv(0, 0).cuboid(-3.0F, -17.25F, -0.5F, 7.0F, 9.0F, 1.0F, new Dilation(-0.2F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.6545F));

        ModelPartData gears6 = gears5.addChild("gears6", ModelPartBuilder.create(),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData cube_r84 = gears6.addChild("cube_r84",
                ModelPartBuilder.create().uv(0, 0).cuboid(-3.0F, -17.25F, -0.5F, 7.0F, 9.0F, 1.0F, new Dilation(-0.2F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.6545F));
        return TexturedModelData.of(modelData, 128, 128);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red,
            float green, float blue, float alpha) {
        steam.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
    }

    @Override
    public void renderWithAnimations(ConsoleBlockEntity console, ClientTardis tardis, ModelPart root, MatrixStack matrices,
                                     VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float pAlpha) {
        matrices.push();
        matrices.translate(0.5f, -1.5f, -0.5f);

        ModelPart throttle = steam.getChild("controls").getChild("panel_6").getChild("rot6").getChild("lever9")
                .getChild("bone50");
        throttle.roll = throttle.roll - ((tardis.travel().speed() / (float) tardis.travel().maxSpeed().get()) * 1.5f);

        ModelPart increment = steam.getChild("controls").getChild("panel_6").getChild("rot6").getChild("lever10")
                .getChild("bone54");
        increment.roll = IncrementManager.increment(tardis) >= 10
                ? IncrementManager.increment(tardis) >= 100
                        ? IncrementManager.increment(tardis) >= 1000
                                ? IncrementManager.increment(tardis) >= 10000
                                        ? increment.roll - (1.3963F * 2)
                                        : increment.roll - (1.047225F * 2)
                                : increment.roll - (0.69815F * 2)
                        : increment.roll - 0.69815F
                : increment.roll;

        ModelPart alarms = steam.getChild("controls").getChild("panel_1").getChild("rot").getChild("lever4")
                .getChild("bone22");
        alarms.roll = tardis.alarm().enabled().get() ? 0.4363F : -0.5672F;

        ModelPart security = steam.getChild("controls").getChild("panel_1").getChild("rot").getChild("lever2")
                .getChild("bone20");
        security.roll = (tardis.stats().security().get() ? 0.4363F : -0.5672F);

        ModelPart antigrav = steam.getChild("controls").getChild("panel_1").getChild("rot").getChild("lever3")
                .getChild("bone19");
        antigrav.roll = (tardis.travel().antigravs().get() ? 0.4363F : -0.5672F);

        ModelPart shields = steam.getChild("controls").getChild("panel_1").getChild("rot").getChild("lever5")
                .getChild("bone21");
        shields.roll = tardis.shields().shielded().get()
                ? tardis.shields().visuallyShielded().get() ? 0.0F : 0.4363F
                : -0.5672F;

        ModelPart refueling = steam.getChild("controls").getChild("panel_1").getChild("rot").getChild("lever")
                .getChild("bone23");
        refueling.roll = (tardis.isRefueling() ? 0.4363F : -0.5672F);

        ModelPart handbrake = steam.getChild("controls").getChild("panel_4").getChild("rot4").getChild("lever6")
                .getChild("bone41");
        handbrake.roll = handbrake.roll + (tardis.travel().handbrake() ? -0f : 1.5f);

        ModelPart power = steam.getChild("controls").getChild("panel_5").getChild("rot5").getChild("lever7")
                .getChild("bone45");
        power.roll = power.roll + (tardis.fuel().hasPower() ? 0f : 1.5f);

        ModelPart landType = steam.getChild("controls").getChild("panel_1").getChild("rot").getChild("valve")
                .getChild("bone9");
        landType.pivotY = landType.pivotY + (tardis.travel().horizontalSearch().get() ? 0.5f : 0); // FIXME use
                                                                                                    // TravelHandler#horizontalSearch/#verticalSearch

        ModelPart direction = steam.getChild("controls").getChild("panel_4").getChild("rot4").getChild("crank")
                .getChild("bone42");
        direction.yaw = direction.yaw + (1.5708f * tardis.travel().destination().getRotation());

        ModelPart doorControl = steam.getChild("controls").getChild("panel_5").getChild("rot5").getChild("crank2")
                .getChild("bone18");
        doorControl.yaw = doorControl.yaw
                + (tardis.door().isOpen() ? tardis.door().isRightOpen() ? 1.5708f * 2f : 1.5708f : 0);

        ModelPart cloak = steam.getChild("controls").getChild("panel_5").getChild("rot5").getChild("lever8")
                .getChild("bone46");
        cloak.roll = cloak.roll - (tardis.cloak().cloaked().get() ? 1.5708f : 0);

        ModelPart doorLock = steam.getChild("controls").getChild("panel_5").getChild("rot5").getChild("lever8")
                .getChild("bone47");
        doorLock.roll = doorLock.roll - (tardis.door().locked() ? 1.5708f : 0);

        ModelPart autopilot = steam.getChild("controls").getChild("panel_5").getChild("rot5").getChild("lever8")
                .getChild("bone48");
        autopilot.roll = autopilot.roll - (tardis.travel().autopilot() ? 1.5708f : 0);

        super.renderWithAnimations(console, tardis, root, matrices, vertices, light, overlay, red, green, blue, pAlpha);
        matrices.pop();
    }

    @Override
    public Animation getAnimationForState(TravelHandlerBase.State state) {
        if (state == TravelHandlerBase.State.LANDED)
            return SteamAnimations.CONSOLE_STEAM_IDLE;

        return SteamAnimations.CONSOLE_STEAM_FLIGHT;
    }

    @Override
    public ModelPart getPart() {
        return steam;
    }
}
