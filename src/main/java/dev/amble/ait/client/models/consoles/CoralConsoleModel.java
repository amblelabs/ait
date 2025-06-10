package dev.amble.ait.client.models.consoles;

import dev.amble.lib.data.CachedDirectedGlobalPos;
import dev.amble.lib.data.DirectedGlobalPos;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.animation.Animation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RotationAxis;

import dev.amble.ait.api.tardis.TardisComponent;
import dev.amble.ait.client.animation.console.coral.CoralAnimations;
import dev.amble.ait.client.tardis.ClientTardis;
import dev.amble.ait.core.blockentities.ConsoleBlockEntity;
import dev.amble.ait.core.tardis.Tardis;
import dev.amble.ait.core.tardis.control.impl.DirectionControl;
import dev.amble.ait.core.tardis.control.impl.pos.IncrementManager;
import dev.amble.ait.core.tardis.handler.FuelHandler;
import dev.amble.ait.core.tardis.handler.WaypointHandler;
import dev.amble.ait.core.tardis.handler.travel.TravelHandler;
import dev.amble.ait.core.tardis.handler.travel.TravelHandlerBase;
import dev.amble.ait.core.util.WorldUtil;
import dev.amble.ait.registry.impl.console.variant.ConsoleVariantRegistry;

public class CoralConsoleModel extends ConsoleModel {
    public static final Animation EMPTY_ANIM = Animation.Builder.create(1).build(); // temporary animation bc rn we have
                                                                                    // none

    private final ModelPart console;

    public CoralConsoleModel(ModelPart root) {
        this.console = root.getChild("console");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData console = modelPartData.addChild("console", ModelPartBuilder.create(),
                ModelTransform.pivot(0.0F, 26.0F, 0.0F));

        ModelPartData tubes2 = console.addChild("tubes2",
                ModelPartBuilder.create().uv(80, 43)
                        .cuboid(16.65F, -12.0F, -5.0F, 2.0F, 2.0F, 10.0F, new Dilation(0.001F)).uv(80, 43)
                        .cuboid(14.65F, -10.5F, -5.0F, 2.0F, 2.0F, 10.0F, new Dilation(0.001F)).uv(80, 43)
                        .cuboid(12.65F, -9.0F, -5.0F, 2.0F, 2.0F, 10.0F, new Dilation(0.001F)).uv(105, 43)
                        .cuboid(-0.35F, -7.5F, -5.0F, 13.0F, 2.0F, 10.0F, new Dilation(0.0F)),
                ModelTransform.pivot(0.0F, -0.5F, 0.0F));

        ModelPartData cube_r1 = tubes2.addChild("cube_r1", ModelPartBuilder.create().uv(113, 148).cuboid(16.65F, -16.0F,
                4.0F, 2.0F, 2.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-4.0F, 7.0F, 0.0F, 0.0F, 0.5236F, 0.0F));

        ModelPartData cube_r2 = tubes2.addChild("cube_r2", ModelPartBuilder.create().uv(120, 148).cuboid(16.65F, -16.0F,
                -5.0F, 2.0F, 2.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(-4.0F, 7.0F, 0.0F, 0.0F, -0.5236F, 0.0F));

        ModelPartData cube_r3 = tubes2.addChild("cube_r3", ModelPartBuilder.create().uv(113, 0).cuboid(16.65F, -16.0F,
                -5.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F)),
                ModelTransform.of(-2.0F, 5.5F, 0.0F, 0.0F, -0.5236F, 0.0F));

        ModelPartData cube_r4 = tubes2.addChild("cube_r4", ModelPartBuilder.create().uv(105, 130).cuboid(16.65F, -16.0F,
                3.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(-2.0F, 5.5F, 0.0F, 0.0F, 0.5236F, 0.0F));

        ModelPartData cube_r5 = tubes2.addChild("cube_r5", ModelPartBuilder.create().uv(70, 128).cuboid(16.65F, -16.0F,
                -5.0F, 2.0F, 2.0F, 3.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 4.0F, 0.0F, 0.0F, -0.5236F, 0.0F));

        ModelPartData cube_r6 = tubes2.addChild("cube_r6", ModelPartBuilder.create().uv(132, 34).cuboid(16.65F, -16.0F,
                2.0F, 2.0F, 2.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 4.0F, 0.0F, 0.0F, 0.5236F, 0.0F));

        ModelPartData tubes3 = tubes2.addChild("tubes3",
                ModelPartBuilder.create().uv(80, 43)
                        .cuboid(16.65F, -12.0F, -5.0F, 2.0F, 2.0F, 10.0F, new Dilation(0.001F)).uv(80, 43)
                        .cuboid(14.65F, -10.5F, -5.0F, 2.0F, 2.0F, 10.0F, new Dilation(0.001F)).uv(80, 43)
                        .cuboid(12.65F, -9.0F, -5.0F, 2.0F, 2.0F, 10.0F, new Dilation(0.001F)).uv(105, 43)
                        .cuboid(-0.35F, -7.5F, -5.0F, 13.0F, 2.0F, 10.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData cube_r7 = tubes3.addChild("cube_r7", ModelPartBuilder.create().uv(113, 148).cuboid(16.65F, -16.5F,
                4.0F, 2.0F, 2.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-4.0F, 7.5F, 0.0F, 0.0F, 0.5236F, 0.0F));

        ModelPartData cube_r8 = tubes3.addChild("cube_r8", ModelPartBuilder.create().uv(120, 148).cuboid(16.65F, -16.5F,
                -5.0F, 2.0F, 2.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(-4.0F, 7.5F, 0.0F, 0.0F, -0.5236F, 0.0F));

        ModelPartData cube_r9 = tubes3.addChild("cube_r9", ModelPartBuilder.create().uv(113, 0).cuboid(16.65F, -16.25F,
                -5.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F)),
                ModelTransform.of(-2.0F, 5.75F, 0.0F, 0.0F, -0.5236F, 0.0F));

        ModelPartData cube_r10 = tubes3.addChild("cube_r10", ModelPartBuilder.create().uv(105, 130).cuboid(16.65F,
                -16.25F, 3.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F)),
                ModelTransform.of(-2.0F, 5.75F, 0.0F, 0.0F, 0.5236F, 0.0F));

        ModelPartData cube_r11 = tubes3.addChild("cube_r11", ModelPartBuilder.create().uv(70, 128).cuboid(16.65F,
                -16.0F, -5.0F, 2.0F, 2.0F, 3.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 4.0F, 0.0F, 0.0F, -0.5236F, 0.0F));

        ModelPartData cube_r12 = tubes3.addChild("cube_r12", ModelPartBuilder.create().uv(132, 34).cuboid(16.65F,
                -16.0F, 2.0F, 2.0F, 2.0F, 3.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 4.0F, 0.0F, 0.0F, 0.5236F, 0.0F));

        ModelPartData tubes4 = tubes3.addChild("tubes4",
                ModelPartBuilder.create().uv(80, 43)
                        .cuboid(16.65F, -12.0F, -5.0F, 2.0F, 2.0F, 10.0F, new Dilation(0.001F)).uv(80, 43)
                        .cuboid(14.65F, -10.5F, -5.0F, 2.0F, 2.0F, 10.0F, new Dilation(0.001F)).uv(80, 43)
                        .cuboid(12.65F, -9.0F, -5.0F, 2.0F, 2.0F, 10.0F, new Dilation(0.001F)).uv(105, 43)
                        .cuboid(-0.35F, -7.5F, -5.0F, 13.0F, 2.0F, 10.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData cube_r13 = tubes4.addChild("cube_r13", ModelPartBuilder.create().uv(113, 148).cuboid(16.65F,
                -16.5F, 4.0F, 2.0F, 2.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(-4.0F, 7.5F, 0.0F, 0.0F, 0.5236F, 0.0F));

        ModelPartData cube_r14 = tubes4.addChild("cube_r14", ModelPartBuilder.create().uv(120, 148).cuboid(16.65F,
                -16.5F, -5.0F, 2.0F, 2.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(-4.0F, 7.5F, 0.0F, 0.0F, -0.5236F, 0.0F));

        ModelPartData cube_r15 = tubes4.addChild("cube_r15", ModelPartBuilder.create().uv(113, 0).cuboid(16.65F,
                -16.25F, -5.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F)),
                ModelTransform.of(-2.0F, 5.75F, 0.0F, 0.0F, -0.5236F, 0.0F));

        ModelPartData cube_r16 = tubes4.addChild("cube_r16", ModelPartBuilder.create().uv(105, 130).cuboid(16.65F,
                -16.25F, 3.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F)),
                ModelTransform.of(-2.0F, 5.75F, 0.0F, 0.0F, 0.5236F, 0.0F));

        ModelPartData cube_r17 = tubes4.addChild("cube_r17", ModelPartBuilder.create().uv(70, 128).cuboid(16.65F,
                -16.0F, -5.0F, 2.0F, 2.0F, 3.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 4.0F, 0.0F, 0.0F, -0.5236F, 0.0F));

        ModelPartData cube_r18 = tubes4.addChild("cube_r18", ModelPartBuilder.create().uv(132, 34).cuboid(16.65F,
                -16.0F, 2.0F, 2.0F, 2.0F, 3.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 4.0F, 0.0F, 0.0F, 0.5236F, 0.0F));

        ModelPartData tubes5 = tubes4.addChild("tubes5",
                ModelPartBuilder.create().uv(80, 43)
                        .cuboid(16.65F, -12.0F, -5.0F, 2.0F, 2.0F, 10.0F, new Dilation(0.001F)).uv(80, 43)
                        .cuboid(14.65F, -10.5F, -5.0F, 2.0F, 2.0F, 10.0F, new Dilation(0.001F)).uv(80, 43)
                        .cuboid(12.65F, -9.0F, -5.0F, 2.0F, 2.0F, 10.0F, new Dilation(0.001F)).uv(105, 43)
                        .cuboid(-0.35F, -7.5F, -5.0F, 13.0F, 2.0F, 10.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData cube_r19 = tubes5.addChild("cube_r19", ModelPartBuilder.create().uv(113, 148).cuboid(16.65F,
                -16.5F, 4.0F, 2.0F, 2.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(-4.0F, 7.5F, 0.0F, 0.0F, 0.5236F, 0.0F));

        ModelPartData cube_r20 = tubes5.addChild("cube_r20", ModelPartBuilder.create().uv(120, 148).cuboid(16.65F,
                -16.5F, -5.0F, 2.0F, 2.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(-4.0F, 7.5F, 0.0F, 0.0F, -0.5236F, 0.0F));

        ModelPartData cube_r21 = tubes5.addChild("cube_r21", ModelPartBuilder.create().uv(113, 0).cuboid(16.65F,
                -16.25F, -5.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F)),
                ModelTransform.of(-2.0F, 5.75F, 0.0F, 0.0F, -0.5236F, 0.0F));

        ModelPartData cube_r22 = tubes5.addChild("cube_r22", ModelPartBuilder.create().uv(105, 130).cuboid(16.65F,
                -16.25F, 3.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F)),
                ModelTransform.of(-2.0F, 5.75F, 0.0F, 0.0F, 0.5236F, 0.0F));

        ModelPartData cube_r23 = tubes5.addChild("cube_r23", ModelPartBuilder.create().uv(70, 128).cuboid(16.65F,
                -16.0F, -5.0F, 2.0F, 2.0F, 3.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 4.0F, 0.0F, 0.0F, -0.5236F, 0.0F));

        ModelPartData cube_r24 = tubes5.addChild("cube_r24", ModelPartBuilder.create().uv(132, 34).cuboid(16.65F,
                -16.0F, 2.0F, 2.0F, 2.0F, 3.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 4.0F, 0.0F, 0.0F, 0.5236F, 0.0F));

        ModelPartData tubes6 = tubes5.addChild("tubes6",
                ModelPartBuilder.create().uv(80, 43)
                        .cuboid(16.65F, -12.0F, -5.0F, 2.0F, 2.0F, 10.0F, new Dilation(0.001F)).uv(80, 43)
                        .cuboid(14.65F, -10.5F, -5.0F, 2.0F, 2.0F, 10.0F, new Dilation(0.001F)).uv(80, 43)
                        .cuboid(12.65F, -9.0F, -5.0F, 2.0F, 2.0F, 10.0F, new Dilation(0.001F)).uv(105, 43)
                        .cuboid(-0.35F, -7.5F, -5.0F, 13.0F, 2.0F, 10.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData cube_r25 = tubes6.addChild("cube_r25", ModelPartBuilder.create().uv(113, 148).cuboid(16.65F,
                -16.5F, 4.0F, 2.0F, 2.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(-4.0F, 7.5F, 0.0F, 0.0F, 0.5236F, 0.0F));

        ModelPartData cube_r26 = tubes6.addChild("cube_r26", ModelPartBuilder.create().uv(120, 148).cuboid(16.65F,
                -16.5F, -5.0F, 2.0F, 2.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(-4.0F, 7.5F, 0.0F, 0.0F, -0.5236F, 0.0F));

        ModelPartData cube_r27 = tubes6.addChild("cube_r27", ModelPartBuilder.create().uv(113, 0).cuboid(16.65F,
                -16.25F, -5.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F)),
                ModelTransform.of(-2.0F, 5.75F, 0.0F, 0.0F, -0.5236F, 0.0F));

        ModelPartData cube_r28 = tubes6.addChild("cube_r28", ModelPartBuilder.create().uv(105, 130).cuboid(16.65F,
                -16.25F, 3.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F)),
                ModelTransform.of(-2.0F, 5.75F, 0.0F, 0.0F, 0.5236F, 0.0F));

        ModelPartData cube_r29 = tubes6.addChild("cube_r29", ModelPartBuilder.create().uv(70, 128).cuboid(16.65F,
                -16.0F, -5.0F, 2.0F, 2.0F, 3.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 4.0F, 0.0F, 0.0F, -0.5236F, 0.0F));

        ModelPartData cube_r30 = tubes6.addChild("cube_r30", ModelPartBuilder.create().uv(132, 34).cuboid(16.65F,
                -16.0F, 2.0F, 2.0F, 2.0F, 3.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 4.0F, 0.0F, 0.0F, 0.5236F, 0.0F));

        ModelPartData tubes7 = tubes6.addChild("tubes7",
                ModelPartBuilder.create().uv(80, 43)
                        .cuboid(16.65F, -12.0F, -5.0F, 2.0F, 2.0F, 10.0F, new Dilation(0.001F)).uv(80, 43)
                        .cuboid(14.65F, -10.5F, -5.0F, 2.0F, 2.0F, 10.0F, new Dilation(0.001F)).uv(80, 43)
                        .cuboid(12.65F, -9.0F, -5.0F, 2.0F, 2.0F, 10.0F, new Dilation(0.001F)).uv(105, 43)
                        .cuboid(-0.35F, -7.5F, -5.0F, 13.0F, 2.0F, 10.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData cube_r31 = tubes7.addChild("cube_r31", ModelPartBuilder.create().uv(113, 148).cuboid(16.65F,
                -16.5F, 4.0F, 2.0F, 2.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(-4.0F, 7.5F, 0.0F, 0.0F, 0.5236F, 0.0F));

        ModelPartData cube_r32 = tubes7.addChild("cube_r32", ModelPartBuilder.create().uv(120, 148).cuboid(16.65F,
                -16.5F, -5.0F, 2.0F, 2.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(-4.0F, 7.5F, 0.0F, 0.0F, -0.5236F, 0.0F));

        ModelPartData cube_r33 = tubes7.addChild("cube_r33", ModelPartBuilder.create().uv(113, 0).cuboid(16.65F,
                -16.25F, -5.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F)),
                ModelTransform.of(-2.0F, 5.75F, 0.0F, 0.0F, -0.5236F, 0.0F));

        ModelPartData cube_r34 = tubes7.addChild("cube_r34", ModelPartBuilder.create().uv(105, 130).cuboid(16.65F,
                -16.25F, 3.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F)),
                ModelTransform.of(-2.0F, 5.75F, 0.0F, 0.0F, 0.5236F, 0.0F));

        ModelPartData cube_r35 = tubes7.addChild("cube_r35", ModelPartBuilder.create().uv(70, 128).cuboid(16.65F,
                -16.0F, -5.0F, 2.0F, 2.0F, 3.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 4.0F, 0.0F, 0.0F, -0.5236F, 0.0F));

        ModelPartData cube_r36 = tubes7.addChild("cube_r36", ModelPartBuilder.create().uv(132, 34).cuboid(16.65F,
                -16.0F, 2.0F, 2.0F, 2.0F, 3.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 4.0F, 0.0F, 0.0F, 0.5236F, 0.0F));

        ModelPartData pillars = console.addChild("pillars", ModelPartBuilder.create().uv(133, 79).cuboid(-2.0F,
                -15.5884F, -22.7409F, 4.0F, 2.0F, 5.0F, new Dilation(0.001F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData cube_r37 = pillars
                .addChild("cube_r37",
                        ModelPartBuilder.create().uv(70, 119).cuboid(-2.0F, 1.75F, -23.0F, 4.0F, 6.0F, 2.0F,
                                new Dilation(0.001F)),
                        ModelTransform.of(0.0F, -0.404F, -0.4858F, -1.4835F, 0.0F, 0.0F));

        ModelPartData cube_r38 = pillars
                .addChild(
                        "cube_r38", ModelPartBuilder.create().uv(44, 134).cuboid(-2.0F, 4.75F, -23.75F, 4.0F, 6.0F,
                                2.0F, new Dilation(0.0F)),
                        ModelTransform.of(0.0F, -0.4821F, -0.433F, -1.3526F, 0.0F, 0.0F));

        ModelPartData cube_r39 = pillars
                .addChild("cube_r39",
                        ModelPartBuilder.create().uv(19, 135).cuboid(-2.0F, 2.0F, -26.0F, 4.0F, 6.0F, 2.0F,
                                new Dilation(0.001F)),
                        ModelTransform.of(0.0F, -0.4888F, -0.4121F, -1.0036F, 0.0F, 0.0F));

        ModelPartData cube_r40 = pillars.addChild("cube_r40", ModelPartBuilder.create().uv(125, 9).cuboid(-2.0F, -3.75F,
                -27.0F, 4.0F, 3.0F, 5.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, -0.4488F, -0.3723F, -0.5672F, 0.0F, 0.0F));

        ModelPartData cube_r41 = pillars
                .addChild(
                        "cube_r41", ModelPartBuilder.create().uv(132, 25).cuboid(-2.0F, -24.0F, -11.0F, 4.0F, 3.0F,
                                5.0F, new Dilation(0.0F)),
                        ModelTransform.of(0.0F, 0.7427F, -0.5684F, 0.5672F, 0.0F, 0.0F));

        ModelPartData cube_r42 = pillars
                .addChild(
                        "cube_r42", ModelPartBuilder.create().uv(91, 130).cuboid(-2.0F, -23.25F, -5.25F, 4.0F, 6.0F,
                                5.0F, new Dilation(0.001F)),
                        ModelTransform.of(0.0F, 0.7785F, -0.4404F, 0.829F, 0.0F, 0.0F));

        ModelPartData cube_r43 = pillars
                .addChild("cube_r43",
                        ModelPartBuilder.create().uv(133, 70).cuboid(-2.0F, -11.0F, -7.5F, 4.0F, 3.0F, 5.0F,
                                new Dilation(-0.001F)),
                        ModelTransform.of(0.0F, 0.8486F, -0.0846F, 0.5672F, 0.0F, 0.0F));

        ModelPartData cube_r44 = pillars.addChild("cube_r44", ModelPartBuilder.create().uv(128, 94).cuboid(-2.0F,
                -18.0F, -3.0F, 4.0F, 7.0F, 5.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.8622F, -0.2399F, 0.9599F, 0.0F, 0.0F));

        ModelPartData cube_r45 = pillars.addChild("cube_r45", ModelPartBuilder.create().uv(29, 19).cuboid(-2.0F, -9.0F,
                -12.0F, 4.0F, 13.0F, 4.0F, new Dilation(0.001F)),
                ModelTransform.of(0.0F, 1.0F, 0.0F, -0.3491F, 0.0F, 0.0F));

        ModelPartData pillars2 = pillars
                .addChild(
                        "pillars2", ModelPartBuilder.create().uv(133, 79).cuboid(-2.0F, -15.5884F, -22.7409F, 4.0F,
                                2.0F, 5.0F, new Dilation(0.001F)),
                        ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData cube_r46 = pillars2
                .addChild("cube_r46",
                        ModelPartBuilder.create().uv(70, 119).cuboid(-2.0F, 1.75F, -23.0F, 4.0F, 6.0F, 2.0F,
                                new Dilation(0.001F)),
                        ModelTransform.of(0.0F, -0.404F, -0.4858F, -1.4835F, 0.0F, 0.0F));

        ModelPartData cube_r47 = pillars2
                .addChild(
                        "cube_r47", ModelPartBuilder.create().uv(44, 134).cuboid(-2.0F, 4.75F, -23.75F, 4.0F, 6.0F,
                                2.0F, new Dilation(0.0F)),
                        ModelTransform.of(0.0F, -0.4821F, -0.433F, -1.3526F, 0.0F, 0.0F));

        ModelPartData cube_r48 = pillars2
                .addChild("cube_r48",
                        ModelPartBuilder.create().uv(19, 135).cuboid(-2.0F, 2.0F, -26.0F, 4.0F, 6.0F, 2.0F,
                                new Dilation(0.001F)),
                        ModelTransform.of(0.0F, -0.4888F, -0.4121F, -1.0036F, 0.0F, 0.0F));

        ModelPartData cube_r49 = pillars2.addChild("cube_r49", ModelPartBuilder.create().uv(125, 9).cuboid(-2.0F,
                -3.75F, -27.0F, 4.0F, 3.0F, 5.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, -0.4488F, -0.3723F, -0.5672F, 0.0F, 0.0F));

        ModelPartData cube_r50 = pillars2
                .addChild(
                        "cube_r50", ModelPartBuilder.create().uv(132, 25).cuboid(-2.0F, -24.0F, -11.0F, 4.0F, 3.0F,
                                5.0F, new Dilation(0.0F)),
                        ModelTransform.of(0.0F, 0.7427F, -0.5684F, 0.5672F, 0.0F, 0.0F));

        ModelPartData cube_r51 = pillars2
                .addChild(
                        "cube_r51", ModelPartBuilder.create().uv(91, 130).cuboid(-2.0F, -23.25F, -5.25F, 4.0F, 6.0F,
                                5.0F, new Dilation(0.001F)),
                        ModelTransform.of(0.0F, 0.7785F, -0.4404F, 0.829F, 0.0F, 0.0F));

        ModelPartData cube_r52 = pillars2
                .addChild("cube_r52",
                        ModelPartBuilder.create().uv(133, 70).cuboid(-2.0F, -11.0F, -7.5F, 4.0F, 3.0F, 5.0F,
                                new Dilation(-0.001F)),
                        ModelTransform.of(0.0F, 0.8486F, -0.0846F, 0.5672F, 0.0F, 0.0F));

        ModelPartData cube_r53 = pillars2.addChild("cube_r53", ModelPartBuilder.create().uv(128, 94).cuboid(-2.0F,
                -18.0F, -3.0F, 4.0F, 7.0F, 5.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.8622F, -0.2399F, 0.9599F, 0.0F, 0.0F));

        ModelPartData cube_r54 = pillars2.addChild("cube_r54", ModelPartBuilder.create().uv(29, 19).cuboid(-2.0F, -9.0F,
                -12.0F, 4.0F, 13.0F, 4.0F, new Dilation(0.001F)),
                ModelTransform.of(0.0F, 1.0F, 0.0F, -0.3491F, 0.0F, 0.0F));

        ModelPartData pillars3 = pillars2
                .addChild(
                        "pillars3", ModelPartBuilder.create().uv(133, 79).cuboid(-2.0F, -15.5884F, -22.7409F, 4.0F,
                                2.0F, 5.0F, new Dilation(0.001F)),
                        ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData cube_r55 = pillars3
                .addChild("cube_r55",
                        ModelPartBuilder.create().uv(70, 119).cuboid(-2.0F, 1.75F, -23.0F, 4.0F, 6.0F, 2.0F,
                                new Dilation(0.001F)),
                        ModelTransform.of(0.0F, -0.404F, -0.4858F, -1.4835F, 0.0F, 0.0F));

        ModelPartData cube_r56 = pillars3
                .addChild(
                        "cube_r56", ModelPartBuilder.create().uv(44, 134).cuboid(-2.0F, 4.75F, -23.75F, 4.0F, 6.0F,
                                2.0F, new Dilation(0.0F)),
                        ModelTransform.of(0.0F, -0.4821F, -0.433F, -1.3526F, 0.0F, 0.0F));

        ModelPartData cube_r57 = pillars3
                .addChild("cube_r57",
                        ModelPartBuilder.create().uv(19, 135).cuboid(-2.0F, 2.0F, -26.0F, 4.0F, 6.0F, 2.0F,
                                new Dilation(0.001F)),
                        ModelTransform.of(0.0F, -0.4888F, -0.4121F, -1.0036F, 0.0F, 0.0F));

        ModelPartData cube_r58 = pillars3.addChild("cube_r58", ModelPartBuilder.create().uv(125, 9).cuboid(-2.0F,
                -3.75F, -27.0F, 4.0F, 3.0F, 5.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, -0.4488F, -0.3723F, -0.5672F, 0.0F, 0.0F));

        ModelPartData cube_r59 = pillars3
                .addChild(
                        "cube_r59", ModelPartBuilder.create().uv(132, 25).cuboid(-2.0F, -24.0F, -11.0F, 4.0F, 3.0F,
                                5.0F, new Dilation(0.0F)),
                        ModelTransform.of(0.0F, 0.7427F, -0.5684F, 0.5672F, 0.0F, 0.0F));

        ModelPartData cube_r60 = pillars3
                .addChild(
                        "cube_r60", ModelPartBuilder.create().uv(91, 130).cuboid(-2.0F, -23.25F, -5.25F, 4.0F, 6.0F,
                                5.0F, new Dilation(0.001F)),
                        ModelTransform.of(0.0F, 0.7785F, -0.4404F, 0.829F, 0.0F, 0.0F));

        ModelPartData cube_r61 = pillars3
                .addChild("cube_r61",
                        ModelPartBuilder.create().uv(133, 70).cuboid(-2.0F, -11.0F, -7.5F, 4.0F, 3.0F, 5.0F,
                                new Dilation(-0.001F)),
                        ModelTransform.of(0.0F, 0.8486F, -0.0846F, 0.5672F, 0.0F, 0.0F));

        ModelPartData cube_r62 = pillars3.addChild("cube_r62", ModelPartBuilder.create().uv(128, 94).cuboid(-2.0F,
                -18.0F, -3.0F, 4.0F, 7.0F, 5.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.8622F, -0.2399F, 0.9599F, 0.0F, 0.0F));

        ModelPartData cube_r63 = pillars3.addChild("cube_r63", ModelPartBuilder.create().uv(29, 19).cuboid(-2.0F, -9.0F,
                -12.0F, 4.0F, 13.0F, 4.0F, new Dilation(0.001F)),
                ModelTransform.of(0.0F, 1.0F, 0.0F, -0.3491F, 0.0F, 0.0F));

        ModelPartData pillars4 = pillars3
                .addChild(
                        "pillars4", ModelPartBuilder.create().uv(133, 79).cuboid(-2.0F, -15.5884F, -22.7409F, 4.0F,
                                2.0F, 5.0F, new Dilation(0.001F)),
                        ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData cube_r64 = pillars4
                .addChild("cube_r64",
                        ModelPartBuilder.create().uv(70, 119).cuboid(-2.0F, 1.75F, -23.0F, 4.0F, 6.0F, 2.0F,
                                new Dilation(0.001F)),
                        ModelTransform.of(0.0F, -0.404F, -0.4858F, -1.4835F, 0.0F, 0.0F));

        ModelPartData cube_r65 = pillars4
                .addChild(
                        "cube_r65", ModelPartBuilder.create().uv(44, 134).cuboid(-2.0F, 4.75F, -23.75F, 4.0F, 6.0F,
                                2.0F, new Dilation(0.0F)),
                        ModelTransform.of(0.0F, -0.4821F, -0.433F, -1.3526F, 0.0F, 0.0F));

        ModelPartData cube_r66 = pillars4
                .addChild("cube_r66",
                        ModelPartBuilder.create().uv(19, 135).cuboid(-2.0F, 2.0F, -26.0F, 4.0F, 6.0F, 2.0F,
                                new Dilation(0.001F)),
                        ModelTransform.of(0.0F, -0.4888F, -0.4121F, -1.0036F, 0.0F, 0.0F));

        ModelPartData cube_r67 = pillars4.addChild("cube_r67", ModelPartBuilder.create().uv(125, 9).cuboid(-2.0F,
                -3.75F, -27.0F, 4.0F, 3.0F, 5.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, -0.4488F, -0.3723F, -0.5672F, 0.0F, 0.0F));

        ModelPartData cube_r68 = pillars4
                .addChild(
                        "cube_r68", ModelPartBuilder.create().uv(132, 25).cuboid(-2.0F, -24.0F, -11.0F, 4.0F, 3.0F,
                                5.0F, new Dilation(0.0F)),
                        ModelTransform.of(0.0F, 0.7427F, -0.5684F, 0.5672F, 0.0F, 0.0F));

        ModelPartData cube_r69 = pillars4
                .addChild(
                        "cube_r69", ModelPartBuilder.create().uv(91, 130).cuboid(-2.0F, -23.25F, -5.25F, 4.0F, 6.0F,
                                5.0F, new Dilation(0.001F)),
                        ModelTransform.of(0.0F, 0.7785F, -0.4404F, 0.829F, 0.0F, 0.0F));

        ModelPartData cube_r70 = pillars4
                .addChild("cube_r70",
                        ModelPartBuilder.create().uv(133, 70).cuboid(-2.0F, -11.0F, -7.5F, 4.0F, 3.0F, 5.0F,
                                new Dilation(-0.001F)),
                        ModelTransform.of(0.0F, 0.8486F, -0.0846F, 0.5672F, 0.0F, 0.0F));

        ModelPartData cube_r71 = pillars4.addChild("cube_r71", ModelPartBuilder.create().uv(128, 94).cuboid(-2.0F,
                -18.0F, -3.0F, 4.0F, 7.0F, 5.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.8622F, -0.2399F, 0.9599F, 0.0F, 0.0F));

        ModelPartData cube_r72 = pillars4.addChild("cube_r72", ModelPartBuilder.create().uv(29, 19).cuboid(-2.0F, -9.0F,
                -12.0F, 4.0F, 13.0F, 4.0F, new Dilation(0.001F)),
                ModelTransform.of(0.0F, 1.0F, 0.0F, -0.3491F, 0.0F, 0.0F));

        ModelPartData pillars5 = pillars4
                .addChild(
                        "pillars5", ModelPartBuilder.create().uv(133, 79).cuboid(-2.0F, -15.5884F, -22.7409F, 4.0F,
                                2.0F, 5.0F, new Dilation(0.001F)),
                        ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData cube_r73 = pillars5
                .addChild("cube_r73",
                        ModelPartBuilder.create().uv(70, 119).cuboid(-2.0F, 1.75F, -23.0F, 4.0F, 6.0F, 2.0F,
                                new Dilation(0.001F)),
                        ModelTransform.of(0.0F, -0.404F, -0.4858F, -1.4835F, 0.0F, 0.0F));

        ModelPartData cube_r74 = pillars5
                .addChild(
                        "cube_r74", ModelPartBuilder.create().uv(44, 134).cuboid(-2.0F, 4.75F, -23.75F, 4.0F, 6.0F,
                                2.0F, new Dilation(0.0F)),
                        ModelTransform.of(0.0F, -0.4821F, -0.433F, -1.3526F, 0.0F, 0.0F));

        ModelPartData cube_r75 = pillars5
                .addChild("cube_r75",
                        ModelPartBuilder.create().uv(19, 135).cuboid(-2.0F, 2.0F, -26.0F, 4.0F, 6.0F, 2.0F,
                                new Dilation(0.001F)),
                        ModelTransform.of(0.0F, -0.4888F, -0.4121F, -1.0036F, 0.0F, 0.0F));

        ModelPartData cube_r76 = pillars5.addChild("cube_r76", ModelPartBuilder.create().uv(125, 9).cuboid(-2.0F,
                -3.75F, -27.0F, 4.0F, 3.0F, 5.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, -0.4488F, -0.3723F, -0.5672F, 0.0F, 0.0F));

        ModelPartData cube_r77 = pillars5
                .addChild(
                        "cube_r77", ModelPartBuilder.create().uv(132, 25).cuboid(-2.0F, -24.0F, -11.0F, 4.0F, 3.0F,
                                5.0F, new Dilation(0.0F)),
                        ModelTransform.of(0.0F, 0.7427F, -0.5684F, 0.5672F, 0.0F, 0.0F));

        ModelPartData cube_r78 = pillars5
                .addChild(
                        "cube_r78", ModelPartBuilder.create().uv(91, 130).cuboid(-2.0F, -23.25F, -5.25F, 4.0F, 6.0F,
                                5.0F, new Dilation(0.001F)),
                        ModelTransform.of(0.0F, 0.7785F, -0.4404F, 0.829F, 0.0F, 0.0F));

        ModelPartData cube_r79 = pillars5
                .addChild("cube_r79",
                        ModelPartBuilder.create().uv(133, 70).cuboid(-2.0F, -11.0F, -7.5F, 4.0F, 3.0F, 5.0F,
                                new Dilation(-0.001F)),
                        ModelTransform.of(0.0F, 0.8486F, -0.0846F, 0.5672F, 0.0F, 0.0F));

        ModelPartData cube_r80 = pillars5.addChild("cube_r80", ModelPartBuilder.create().uv(128, 94).cuboid(-2.0F,
                -18.0F, -3.0F, 4.0F, 7.0F, 5.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.8622F, -0.2399F, 0.9599F, 0.0F, 0.0F));

        ModelPartData cube_r81 = pillars5.addChild("cube_r81", ModelPartBuilder.create().uv(29, 19).cuboid(-2.0F, -9.0F,
                -12.0F, 4.0F, 13.0F, 4.0F, new Dilation(0.001F)),
                ModelTransform.of(0.0F, 1.0F, 0.0F, -0.3491F, 0.0F, 0.0F));

        ModelPartData pillars6 = pillars5
                .addChild(
                        "pillars6", ModelPartBuilder.create().uv(133, 79).cuboid(-2.0F, -15.5884F, -22.7409F, 4.0F,
                                2.0F, 5.0F, new Dilation(0.001F)),
                        ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData cube_r82 = pillars6
                .addChild("cube_r82",
                        ModelPartBuilder.create().uv(70, 119).cuboid(-2.0F, 1.75F, -23.0F, 4.0F, 6.0F, 2.0F,
                                new Dilation(0.001F)),
                        ModelTransform.of(0.0F, -0.404F, -0.4858F, -1.4835F, 0.0F, 0.0F));

        ModelPartData cube_r83 = pillars6
                .addChild(
                        "cube_r83", ModelPartBuilder.create().uv(44, 134).cuboid(-2.0F, 4.75F, -23.75F, 4.0F, 6.0F,
                                2.0F, new Dilation(0.0F)),
                        ModelTransform.of(0.0F, -0.4821F, -0.433F, -1.3526F, 0.0F, 0.0F));

        ModelPartData cube_r84 = pillars6
                .addChild("cube_r84",
                        ModelPartBuilder.create().uv(19, 135).cuboid(-2.0F, 2.0F, -26.0F, 4.0F, 6.0F, 2.0F,
                                new Dilation(0.001F)),
                        ModelTransform.of(0.0F, -0.4888F, -0.4121F, -1.0036F, 0.0F, 0.0F));

        ModelPartData cube_r85 = pillars6.addChild("cube_r85", ModelPartBuilder.create().uv(125, 9).cuboid(-2.0F,
                -3.75F, -27.0F, 4.0F, 3.0F, 5.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, -0.4488F, -0.3723F, -0.5672F, 0.0F, 0.0F));

        ModelPartData cube_r86 = pillars6
                .addChild(
                        "cube_r86", ModelPartBuilder.create().uv(132, 25).cuboid(-2.0F, -24.0F, -11.0F, 4.0F, 3.0F,
                                5.0F, new Dilation(0.0F)),
                        ModelTransform.of(0.0F, 0.7427F, -0.5684F, 0.5672F, 0.0F, 0.0F));

        ModelPartData cube_r87 = pillars6
                .addChild(
                        "cube_r87", ModelPartBuilder.create().uv(91, 130).cuboid(-2.0F, -23.25F, -5.25F, 4.0F, 6.0F,
                                5.0F, new Dilation(0.001F)),
                        ModelTransform.of(0.0F, 0.7785F, -0.4404F, 0.829F, 0.0F, 0.0F));

        ModelPartData cube_r88 = pillars6
                .addChild("cube_r88",
                        ModelPartBuilder.create().uv(133, 70).cuboid(-2.0F, -11.0F, -7.5F, 4.0F, 3.0F, 5.0F,
                                new Dilation(-0.001F)),
                        ModelTransform.of(0.0F, 0.8486F, -0.0846F, 0.5672F, 0.0F, 0.0F));

        ModelPartData cube_r89 = pillars6.addChild("cube_r89", ModelPartBuilder.create().uv(128, 94).cuboid(-2.0F,
                -18.0F, -3.0F, 4.0F, 7.0F, 5.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.8622F, -0.2399F, 0.9599F, 0.0F, 0.0F));

        ModelPartData cube_r90 = pillars6.addChild("cube_r90", ModelPartBuilder.create().uv(29, 19).cuboid(-2.0F, -9.0F,
                -12.0F, 4.0F, 13.0F, 4.0F, new Dilation(0.001F)),
                ModelTransform.of(0.0F, 1.0F, 0.0F, -0.3491F, 0.0F, 0.0F));

        ModelPartData rim = console.addChild("rim", ModelPartBuilder.create().uv(91, 113).cuboid(18.5F, -16.0F, -5.5F,
                2.0F, 5.0F, 11.0F, new Dilation(0.001F)), ModelTransform.pivot(0.0F, -1.0F, 0.0F));

        ModelPartData cube_r91 = rim.addChild("cube_r91",
                ModelPartBuilder.create().uv(80, 5).cuboid(18.5F, -16.0F, -5.5F, 2.0F, 5.0F, 5.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -0.5236F, 0.0F));

        ModelPartData cube_r92 = rim.addChild("cube_r92",
                ModelPartBuilder.create().uv(29, 38).cuboid(18.5F, -16.0F, -0.5F, 2.0F, 5.0F, 6.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.5236F, 0.0F));

        ModelPartData rim2 = rim.addChild("rim2", ModelPartBuilder.create().uv(91, 113).cuboid(18.5F, -16.0F, -5.5F,
                2.0F, 5.0F, 11.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData cube_r93 = rim2.addChild("cube_r93",
                ModelPartBuilder.create().uv(80, 5).cuboid(18.5F, -16.0F, -5.5F, 2.0F, 5.0F, 5.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -0.5236F, 0.0F));

        ModelPartData cube_r94 = rim2.addChild("cube_r94",
                ModelPartBuilder.create().uv(29, 38).cuboid(18.5F, -16.0F, -0.5F, 2.0F, 5.0F, 6.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.5236F, 0.0F));

        ModelPartData rim3 = rim2.addChild("rim3", ModelPartBuilder.create().uv(91, 113).cuboid(18.5F, -16.0F, -5.5F,
                2.0F, 5.0F, 11.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData cube_r95 = rim3.addChild("cube_r95",
                ModelPartBuilder.create().uv(80, 5).cuboid(18.5F, -16.0F, -5.5F, 2.0F, 5.0F, 5.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -0.5236F, 0.0F));

        ModelPartData cube_r96 = rim3.addChild("cube_r96",
                ModelPartBuilder.create().uv(29, 38).cuboid(18.5F, -16.0F, -0.5F, 2.0F, 5.0F, 6.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.5236F, 0.0F));

        ModelPartData rim4 = rim3.addChild("rim4", ModelPartBuilder.create().uv(91, 113).cuboid(18.5F, -16.0F, -5.5F,
                2.0F, 5.0F, 11.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData cube_r97 = rim4.addChild("cube_r97",
                ModelPartBuilder.create().uv(80, 5).cuboid(18.5F, -16.0F, -5.5F, 2.0F, 5.0F, 5.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -0.5236F, 0.0F));

        ModelPartData cube_r98 = rim4.addChild("cube_r98",
                ModelPartBuilder.create().uv(29, 38).cuboid(18.5F, -16.0F, -0.5F, 2.0F, 5.0F, 6.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.5236F, 0.0F));

        ModelPartData rim5 = rim4.addChild("rim5", ModelPartBuilder.create().uv(91, 113).cuboid(18.5F, -16.0F, -5.5F,
                2.0F, 5.0F, 11.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData cube_r99 = rim5.addChild("cube_r99",
                ModelPartBuilder.create().uv(80, 5).cuboid(18.5F, -16.0F, -5.5F, 2.0F, 5.0F, 5.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -0.5236F, 0.0F));

        ModelPartData cube_r100 = rim5.addChild("cube_r100",
                ModelPartBuilder.create().uv(29, 38).cuboid(18.5F, -16.0F, -0.5F, 2.0F, 5.0F, 6.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.5236F, 0.0F));

        ModelPartData rim6 = rim5.addChild("rim6", ModelPartBuilder.create().uv(91, 113).cuboid(18.5F, -16.0F, -5.5F,
                2.0F, 5.0F, 11.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData cube_r101 = rim6.addChild("cube_r101",
                ModelPartBuilder.create().uv(80, 5).cuboid(18.5F, -16.0F, -5.5F, 2.0F, 5.0F, 5.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -0.5236F, 0.0F));

        ModelPartData cube_r102 = rim6.addChild("cube_r102",
                ModelPartBuilder.create().uv(29, 38).cuboid(18.5F, -16.0F, -0.5F, 2.0F, 5.0F, 6.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.5236F, 0.0F));

        ModelPartData panels = console.addChild("panels", ModelPartBuilder.create(),
                ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData net_r1 = panels.addChild("net_r1", ModelPartBuilder.create().uv(62, 58).cuboid(-4.0F, -22.0F,
                -9.0F, 16.0F, 0.0F, 18.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, -0.1F, 0.0F, 0.0F, 0.0F, 0.3927F));

        ModelPartData cube_r103 = panels.addChild("cube_r103", ModelPartBuilder.create().uv(29, 38).cuboid(-4.0F,
                -21.5F, -9.0F, 16.0F, 0.0F, 18.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.3927F));

        ModelPartData panels2 = panels.addChild("panels2", ModelPartBuilder.create(),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData growth_r1 = panels2.addChild("growth_r1", ModelPartBuilder.create().uv(29, 19).cuboid(-4.0F,
                -22.0F, -9.0F, 16.0F, 0.0F, 18.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, -0.1F, 0.0F, 0.0F, 0.0F, 0.3927F));

        ModelPartData cube_r104 = panels2.addChild("cube_r104", ModelPartBuilder.create().uv(29, 38).cuboid(-4.0F,
                -21.5F, -9.0F, 16.0F, 0.0F, 18.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.3927F));

        ModelPartData panels3 = panels2.addChild("panels3", ModelPartBuilder.create(),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData growth_r2 = panels3.addChild("growth_r2", ModelPartBuilder.create().uv(29, 19).cuboid(-4.0F,
                -22.0F, -9.0F, 16.0F, 0.0F, 18.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, -0.1F, 0.0F, 0.0F, 0.0F, 0.3927F));

        ModelPartData cube_r105 = panels3.addChild("cube_r105", ModelPartBuilder.create().uv(29, 38).cuboid(-4.0F,
                -21.5F, -9.0F, 16.0F, 0.0F, 18.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.3927F));

        ModelPartData panels4 = panels3.addChild("panels4", ModelPartBuilder.create(),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData wires_r1 = panels4.addChild("wires_r1",
                ModelPartBuilder.create().uv(29, 0)
                        .cuboid(-3.75F, -23.0F, -9.0F, 16.0F, 0.0F, 18.0F, new Dilation(0.0F)).uv(62, 58)
                        .cuboid(-4.0F, -22.0F, -9.0F, 16.0F, 0.0F, 18.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, -0.1F, 0.0F, 0.0F, 0.0F, 0.3927F));

        ModelPartData cube_r106 = panels4.addChild("cube_r106", ModelPartBuilder.create().uv(29, 38).cuboid(-4.0F,
                -21.5F, -9.0F, 16.0F, 0.0F, 18.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.3927F));

        ModelPartData panels5 = panels4.addChild("panels5", ModelPartBuilder.create(),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData cover_r1 = panels5.addChild("cover_r1", ModelPartBuilder.create().uv(11, 57).cuboid(-4.0F, -22.0F,
                -9.0F, 16.0F, 0.0F, 18.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, -0.1F, 0.0F, 0.0F, 0.0F, 0.3927F));

        ModelPartData cube_r107 = panels5.addChild("cube_r107", ModelPartBuilder.create().uv(29, 38).cuboid(-4.0F,
                -21.5F, -9.0F, 16.0F, 0.0F, 18.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.3927F));

        ModelPartData panels6 = panels5.addChild("panels6", ModelPartBuilder.create(),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData growth_r3 = panels6.addChild("growth_r3", ModelPartBuilder.create().uv(29, 19).cuboid(-4.0F,
                -22.0F, -9.0F, 16.0F, 0.0F, 18.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, -0.1F, 0.0F, 0.0F, 0.0F, 0.3927F));

        ModelPartData cube_r108 = panels6.addChild("cube_r108", ModelPartBuilder.create().uv(29, 38).cuboid(-4.0F,
                -21.5F, -9.0F, 16.0F, 0.0F, 18.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.3927F));

        ModelPartData bone = console.addChild("bone",
                ModelPartBuilder.create().uv(15, 0).cuboid(-0.1F, -23.5F, -4.0F, 7.0F, 3.0F, 8.0F, new Dilation(0.0F)),
                ModelTransform.pivot(0.0F, 0.25F, 0.0F));

        ModelPartData bone2 = bone.addChild("bone2",
                ModelPartBuilder.create().uv(15, 0).cuboid(-0.1F, -23.5F, -4.0F, 7.0F, 3.0F, 8.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData bone3 = bone2.addChild("bone3",
                ModelPartBuilder.create().uv(15, 0).cuboid(-0.1F, -23.5F, -4.0F, 7.0F, 3.0F, 8.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData bone4 = bone3.addChild("bone4",
                ModelPartBuilder.create().uv(15, 0).cuboid(-0.1F, -23.5F, -4.0F, 7.0F, 3.0F, 8.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData bone5 = bone4.addChild("bone5",
                ModelPartBuilder.create().uv(15, 0).cuboid(-0.1F, -23.5F, -4.0F, 7.0F, 3.0F, 8.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData bone6 = bone5.addChild("bone6",
                ModelPartBuilder.create().uv(15, 0).cuboid(-0.1F, -23.5F, -4.0F, 7.0F, 3.0F, 8.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData controls = console.addChild("controls", ModelPartBuilder.create(),
                ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData ctrl_1 = controls.addChild("ctrl_1", ModelPartBuilder.create(),
                ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData bone13 = ctrl_1.addChild("bone13", ModelPartBuilder.create(),
                ModelTransform.of(19.0F, -17.0F, 0.0F, 0.0F, 0.0F, -1.1781F));

        ModelPartData panel = bone13.addChild("panel", ModelPartBuilder.create().uv(44, 119).cuboid(-1.0F, -4.0F, -5.0F,
                1.0F, 4.0F, 10.0F, new Dilation(0.0F)), ModelTransform.of(-0.25F, -1.25F, -2.0F, -0.1745F, 0.0F, 0.0F));

        ModelPartData switch0 = panel.addChild("switch0", ModelPartBuilder.create(),
                ModelTransform.of(0.0F, -1.0F, -3.5F, 0.0F, 0.0F, 0.7854F));

        ModelPartData cube_r109 = switch0.addChild("cube_r109",
                ModelPartBuilder.create().uv(65, 145).cuboid(0.0F, -0.5F, -1.0F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, -0.5F, 0.5F, 0.0F, 0.0F, -3.1416F));

        ModelPartData switch2 = panel.addChild("switch2", ModelPartBuilder.create(),
                ModelTransform.of(0.0F, -1.0F, -2.5F, 0.0F, 0.0F, 0.7854F));

        ModelPartData cube_r110 = switch2.addChild("cube_r110",
                ModelPartBuilder.create().uv(91, 142).cuboid(0.0F, -0.5F, -1.0F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, -0.5F, 0.5F, 0.0F, 0.0F, -3.1416F));

        ModelPartData switch3 = panel.addChild("switch3", ModelPartBuilder.create(),
                ModelTransform.of(0.0F, -1.0F, -0.5F, 0.0F, 0.0F, 0.7854F));

        ModelPartData cube_r111 = switch3.addChild("cube_r111",
                ModelPartBuilder.create().uv(6, 141).cuboid(0.0F, -0.5F, -1.0F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, -0.5F, 0.5F, 0.0F, 0.0F, -3.1416F));

        ModelPartData switch4 = panel.addChild("switch4", ModelPartBuilder.create(),
                ModelTransform.of(0.0F, -1.0F, 0.5F, 0.0F, 0.0F, 0.7854F));

        ModelPartData cube_r112 = switch4.addChild("cube_r112",
                ModelPartBuilder.create().uv(92, 99).cuboid(0.0F, -0.5F, -1.0F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, -0.5F, 0.5F, 0.0F, 0.0F, -3.1416F));

        ModelPartData switch29 = panel.addChild("switch29", ModelPartBuilder.create(),
                ModelTransform.of(0.0F, -1.0F, 1.5F, 0.0F, 0.0F, 0.7854F));

        ModelPartData cube_r113 = switch29.addChild("cube_r113",
                ModelPartBuilder.create().uv(92, 99).cuboid(0.0F, -0.5F, -1.0F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, -0.5F, 0.5F, 0.0F, 0.0F, -3.1416F));

        ModelPartData switch5 = panel.addChild("switch5", ModelPartBuilder.create(),
                ModelTransform.of(0.0F, -1.0F, 2.5F, 0.0F, 0.0F, 0.7854F));

        ModelPartData cube_r114 = switch5.addChild("cube_r114",
                ModelPartBuilder.create().uv(0, 141).cuboid(0.0F, -0.5F, -1.0F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, -0.5F, 0.5F, 0.0F, 0.0F, -3.1416F));

        ModelPartData switch6 = panel.addChild("switch6", ModelPartBuilder.create(),
                ModelTransform.of(0.0F, -1.0F, 3.5F, 0.0F, 0.0F, 0.7854F));

        ModelPartData cube_r115 = switch6.addChild("cube_r115",
                ModelPartBuilder.create().uv(140, 87).cuboid(0.0F, -0.5F, -1.0F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, -0.5F, 0.5F, 0.0F, 0.0F, -3.1416F));

        ModelPartData remote = bone13.addChild("remote", ModelPartBuilder.create().uv(110, 133).cuboid(-1.0F, -2.0F,
                -2.0F, 1.0F, 2.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(-0.5F, -5.5F, -0.5F));

        ModelPartData cube_r116 = remote.addChild("cube_r116",
                ModelPartBuilder.create().uv(107, 0).cuboid(0.0F, -1.0F, 0.0F, 0.0F, 2.0F, 5.0F, new Dilation(0.0F)),
                ModelTransform.of(-0.3F, -1.0F, -2.5F, 0.0F, 0.0F, -3.1416F));

        ModelPartData button = remote.addChild("button",
                ModelPartBuilder.create().uv(153, 89).cuboid(0.0F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, new Dilation(-0.3F)),
                ModelTransform.pivot(-0.5F, -0.9F, -1.6F));

        ModelPartData button2 = remote.addChild("button2",
                ModelPartBuilder.create().uv(153, 83).cuboid(0.0F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, new Dilation(-0.3F)),
                ModelTransform.pivot(-0.5F, -0.9F, -0.85F));

        ModelPartData button3 = remote.addChild("button3",
                ModelPartBuilder.create().uv(68, 153).cuboid(0.0F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, new Dilation(-0.3F)),
                ModelTransform.pivot(-0.5F, -0.9F, -0.1F));

        ModelPartData button4 = remote.addChild("button4",
                ModelPartBuilder.create().uv(153, 58).cuboid(0.0F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, new Dilation(-0.3F)),
                ModelTransform.pivot(-0.5F, -0.9F, 0.65F));

        ModelPartData button5 = remote.addChild("button5",
                ModelPartBuilder.create().uv(46, 153).cuboid(0.0F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, new Dilation(-0.3F)),
                ModelTransform.pivot(-0.5F, -0.15F, -1.6F));

        ModelPartData button6 = remote.addChild("button6",
                ModelPartBuilder.create().uv(41, 153).cuboid(0.0F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, new Dilation(-0.3F)),
                ModelTransform.pivot(-0.5F, -0.15F, -0.85F));

        ModelPartData button7 = remote.addChild("button7",
                ModelPartBuilder.create().uv(153, 27).cuboid(0.0F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, new Dilation(-0.3F)),
                ModelTransform.pivot(-0.5F, -0.15F, -0.1F));

        ModelPartData button8 = remote.addChild("button8",
                ModelPartBuilder.create().uv(153, 18).cuboid(0.0F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, new Dilation(-0.3F)),
                ModelTransform.pivot(-0.5F, -0.15F, 0.65F));

        ModelPartData port = bone13.addChild("port",
                ModelPartBuilder.create().uv(150, 107).cuboid(-1.8F, -2.5F, -1.5F, 1.0F, 3.0F, 1.0F, new Dilation(0.0F))
                        .uv(142, 21).cuboid(-0.8F, -1.0F, -1.5F, 1.0F, 0.0F, 3.0F, new Dilation(0.0F)).uv(63, 150)
                        .cuboid(-1.8F, -2.5F, 0.5F, 1.0F, 3.0F, 1.0F, new Dilation(0.0F)).uv(153, 15)
                        .cuboid(-1.8F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)).uv(15, 153)
                        .cuboid(-1.8F, -2.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.pivot(0.5F, -9.0F, 0.0F));

        ModelPartData cube_r117 = port.addChild("cube_r117",
                ModelPartBuilder.create().uv(133, 79).cuboid(-1.0F, -5.0F, 0.0F, 2.0F, 4.0F, 0.0F, new Dilation(0.0F)),
                ModelTransform.of(-0.25F, 0.0F, -1.0F, -0.1309F, 0.0F, 0.0F));

        ModelPartData cube_r118 = port.addChild("cube_r118",
                ModelPartBuilder.create().uv(127, 148).cuboid(-1.0F, -5.0F, 0.0F, 2.0F, 4.0F, 0.0F, new Dilation(0.0F)),
                ModelTransform.of(-0.25F, 0.0F, 1.0F, 0.1309F, 0.0F, 0.0F));

        ModelPartData cube_r119 = port.addChild("cube_r119",
                ModelPartBuilder.create().uv(115, 9).cuboid(0.0F, -2.0F, -0.5F, 0.0F, 4.0F, 4.0F, new Dilation(0.0F)),
                ModelTransform.of(-1.3F, -1.0F, -1.5F, 0.0F, 0.0F, -3.1416F));

        ModelPartData compass = bone13.addChild("compass",
                ModelPartBuilder.create().uv(144, 87).cuboid(-2.0F, -1.0F, -1.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.4F))
                        .uv(70, 148).cuboid(-0.55F, -1.0F, -1.0F, 1.0F, 2.0F, 2.0F, new Dilation(0.2F)),
                ModelTransform.pivot(1.0F, -3.0F, 5.0F));

        ModelPartData cube_r120 = compass.addChild("cube_r120",
                ModelPartBuilder.create().uv(73, 62).cuboid(0.0F, -1.25F, -1.75F, 0.0F, 3.0F, 3.0F, new Dilation(0.0F)),
                ModelTransform.of(-1.8F, -2.25F, -0.75F, 0.0F, 0.0F, -3.1416F));

        ModelPartData needle = compass.addChild("needle", ModelPartBuilder.create().uv(64, 119).cuboid(-0.25F, -1.5F,
                -0.25F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, -0.3054F, 0.0F, 0.0F));

        ModelPartData insert = bone13.addChild("insert",
                ModelPartBuilder.create().uv(58, 150).cuboid(-1.0F, -1.5F, -0.5F, 1.0F, 3.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, -8.25F, -3.75F, -0.3054F, 0.0F, 0.0F));

        ModelPartData bone96 = insert.addChild("bone96",
                ModelPartBuilder.create().uv(43, 148).cuboid(-1.0F, -3.0F, 0.0F, 2.0F, 3.0F, 1.0F, new Dilation(-0.2F)),
                ModelTransform.pivot(0.25F, 1.5F, -0.5F));

        ModelPartData box = bone13.addChild("box",
                ModelPartBuilder.create().uv(148, 19).cuboid(-1.0F, -1.0F, -1.0F, 1.0F, 2.0F, 2.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, -8.0F, 3.25F, -0.8727F, 0.0F, 0.0F));

        ModelPartData bone109 = box.addChild("bone109", ModelPartBuilder.create().uv(10, 153).cuboid(-0.5F, -0.5F,
                -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.3F)), ModelTransform.pivot(-0.5F, 1.2F, 0.0F));

        ModelPartData ctrl_2 = controls.addChild("ctrl_2", ModelPartBuilder.create(),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData bone7 = ctrl_2.addChild("bone7",
                ModelPartBuilder.create().uv(148, 7).cuboid(-1.0F, -3.5F, -4.5F, 1.0F, 2.0F, 2.0F, new Dilation(0.0F)),
                ModelTransform.of(19.0F, -17.0F, 0.0F, 0.0F, 0.0F, -1.1781F));

        ModelPartData cube_r121 = bone7.addChild("cube_r121",
                ModelPartBuilder.create().uv(95, 38).cuboid(0.0F, -0.5F, -0.5F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(-0.8F, -9.0F, 2.5F, 0.0F, 0.0F, -3.1416F));

        ModelPartData cube_r122 = bone7.addChild("cube_r122",
                ModelPartBuilder.create().uv(95, 38).cuboid(0.0F, -0.5F, -0.5F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(-0.8F, -9.5F, 0.5F, 0.0F, 0.0F, -3.1416F));

        ModelPartData cube_r123 = bone7.addChild("cube_r123",
                ModelPartBuilder.create().uv(95, 38).cuboid(0.0F, -0.5F, -0.5F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(-0.8F, -6.0F, -0.5F, 0.0F, 0.0F, -3.1416F));

        ModelPartData cube_r124 = bone7.addChild("cube_r124",
                ModelPartBuilder.create().uv(95, 38).cuboid(0.0F, -0.5F, -0.5F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(-0.8F, -5.5F, 3.0F, 0.0F, 0.0F, -3.1416F));

        ModelPartData cube_r125 = bone7.addChild("cube_r125",
                ModelPartBuilder.create().uv(140, 34).cuboid(0.0F, -0.5F, -0.5F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(-0.8F, -4.0F, 2.5F, 0.0F, 0.0F, -3.1416F));

        ModelPartData cube_r126 = bone7.addChild("cube_r126",
                ModelPartBuilder.create().uv(73, 62).cuboid(0.0F, -1.5F, -1.5F, 0.0F, 3.0F, 3.0F, new Dilation(0.0F)),
                ModelTransform.of(-0.8F, -9.0F, -2.0F, 0.0F, 0.0F, -3.1416F));

        ModelPartData cube_r127 = bone7.addChild("cube_r127",
                ModelPartBuilder.create().uv(73, 62).cuboid(0.0F, -1.5F, -1.5F, 0.0F, 3.0F, 3.0F, new Dilation(0.0F)),
                ModelTransform.of(-0.8F, -3.5F, 0.0F, 0.0F, 0.0F, -3.1416F));

        ModelPartData ball = bone7.addChild("ball",
                ModelPartBuilder.create().uv(16, 144).cuboid(-1.0F, -1.0F, -1.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F))
                        .uv(144, 14).cuboid(-1.0F, -1.0F, -1.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.1F)),
                ModelTransform.pivot(-0.8F, -2.5F, 3.5F));

        ModelPartData ball2 = bone7.addChild("ball2",
                ModelPartBuilder.create().uv(16, 144).cuboid(-1.0F, -1.25F, -1.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F))
                        .uv(144, 14).cuboid(-1.0F, -1.25F, -1.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.1F)),
                ModelTransform.pivot(-0.8F, -11.25F, 0.0F));

        ModelPartData knob = bone7.addChild("knob",
                ModelPartBuilder.create().uv(25, 144).cuboid(-1.0F, -1.0F, -1.0F, 2.0F, 2.0F, 2.0F, new Dilation(-0.3F))
                        .uv(143, 147).cuboid(0.25F, -1.0F, -1.0F, 1.0F, 2.0F, 2.0F, new Dilation(-0.5F)),
                ModelTransform.of(-0.75F, -6.5F, 1.25F, 0.9163F, 0.0F, 0.0F));

        ModelPartData wires = bone7.addChild("wires", ModelPartBuilder.create().uv(127, 137).cuboid(-1.2F, -2.0F, -0.5F,
                2.0F, 4.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -7.0F, -5.0F, -0.5236F, 0.0F, 0.0F));

        ModelPartData cube_r128 = wires.addChild("cube_r128",
                ModelPartBuilder.create().uv(96, 147).cuboid(0.0F, -2.0F, 1.0F, 0.0F, 4.0F, 2.0F, new Dilation(0.0F)),
                ModelTransform.of(-0.7F, 0.0F, 0.5F, 0.0F, 0.0F, -3.1416F));

        ModelPartData spring = bone7.addChild("spring",
                ModelPartBuilder.create().uv(80, 29).cuboid(0.0F, -2.0F, -2.0F, 1.0F, 4.0F, 1.0F, new Dilation(0.0F))
                        .uv(135, 133).cuboid(0.0F, 1.0F, -1.0F, 1.0F, 1.0F, 4.0F, new Dilation(0.0F)).uv(143, 136)
                        .cuboid(0.0F, -2.0F, -1.0F, 1.0F, 1.0F, 3.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, -2.0F, -4.0F, -0.3491F, 0.0F, 0.0F));

        ModelPartData cube_r129 = spring.addChild("cube_r129",
                ModelPartBuilder.create().uv(80, 29).cuboid(-0.1846F, -0.5858F, -2.4021F, 0.0F, 1.0F, 6.0F,
                        new Dilation(0.0F)),
                ModelTransform.of(-0.4975F, -3.0811F, 3.2527F, -0.7897F, 0.0924F, 3.0488F));

        ModelPartData ctrl_3 = controls.addChild("ctrl_3", ModelPartBuilder.create(),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 2.0944F, 0.0F));

        ModelPartData bone14 = ctrl_3.addChild("bone14", ModelPartBuilder.create(),
                ModelTransform.of(19.0F, -17.0F, 0.0F, 0.0F, 0.0F, -1.1781F));

        ModelPartData panel2 = bone14.addChild("panel2",
                ModelPartBuilder.create().uv(59, 126).cuboid(-1.0F, -2.0F, -4.0F, 1.0F, 4.0F, 8.0F, new Dilation(0.0F)),
                ModelTransform.of(-0.5F, -2.5F, -2.0F, -0.0436F, 0.0F, 0.0F));

        ModelPartData switch7 = panel2.addChild("switch7", ModelPartBuilder.create(),
                ModelTransform.of(0.0F, 1.5F, -3.0F, 0.0F, 0.0F, 0.7854F));

        ModelPartData cube_r130 = switch7.addChild("cube_r130",
                ModelPartBuilder.create().uv(139, 130).cuboid(0.0F, 0.0F, -0.5F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -3.1416F));

        ModelPartData switch8 = panel2.addChild("switch8", ModelPartBuilder.create(),
                ModelTransform.of(0.0F, 1.5F, -2.0F, 0.0F, 0.0F, 0.7854F));

        ModelPartData cube_r131 = switch8.addChild("cube_r131",
                ModelPartBuilder.create().uv(124, 137).cuboid(0.0F, 0.0F, -0.5F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -3.1416F));

        ModelPartData switch9 = panel2.addChild("switch9", ModelPartBuilder.create(),
                ModelTransform.of(0.0F, 1.5F, 0.0F, 0.0F, 0.0F, 0.7854F));

        ModelPartData cube_r132 = switch9.addChild("cube_r132",
                ModelPartBuilder.create().uv(135, 70).cuboid(0.0F, 0.0F, -0.5F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -3.1416F));

        ModelPartData switch10 = panel2.addChild("switch10", ModelPartBuilder.create(),
                ModelTransform.of(0.0F, 1.5F, 1.0F, 0.0F, 0.0F, 0.7854F));

        ModelPartData cube_r133 = switch10.addChild("cube_r133",
                ModelPartBuilder.create().uv(133, 87).cuboid(0.0F, 0.0F, -0.5F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -3.1416F));

        ModelPartData switch11 = panel2.addChild("switch11", ModelPartBuilder.create(),
                ModelTransform.of(0.0F, 1.5F, 3.0F, 0.0F, 0.0F, 0.7854F));

        ModelPartData cube_r134 = switch11.addChild("cube_r134",
                ModelPartBuilder.create().uv(132, 34).cuboid(0.0F, 0.0F, -0.5F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -3.1416F));

        ModelPartData switch12 = panel2.addChild("switch12", ModelPartBuilder.create(),
                ModelTransform.of(0.0F, 0.5F, -3.0F, 0.0F, 0.0F, 0.7854F));

        ModelPartData cube_r135 = switch12.addChild("cube_r135",
                ModelPartBuilder.create().uv(131, 110).cuboid(0.0F, 0.0F, -0.5F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -3.1416F));

        ModelPartData switch13 = panel2.addChild("switch13", ModelPartBuilder.create(),
                ModelTransform.of(0.0F, 0.5F, -2.0F, 0.0F, 0.0F, 0.7854F));

        ModelPartData cube_r136 = switch13.addChild("cube_r136",
                ModelPartBuilder.create().uv(114, 130).cuboid(0.0F, 0.0F, -0.5F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -3.1416F));

        ModelPartData switch14 = panel2.addChild("switch14", ModelPartBuilder.create(),
                ModelTransform.of(0.0F, 0.5F, 0.0F, 0.0F, 0.0F, 0.7854F));

        ModelPartData cube_r137 = switch14.addChild("cube_r137",
                ModelPartBuilder.create().uv(128, 119).cuboid(0.0F, 0.0F, -0.5F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -3.1416F));

        ModelPartData switch15 = panel2.addChild("switch15", ModelPartBuilder.create(),
                ModelTransform.of(0.0F, 0.5F, 1.0F, 0.0F, 0.0F, 0.7854F));

        ModelPartData cube_r138 = switch15.addChild("cube_r138",
                ModelPartBuilder.create().uv(78, 128).cuboid(0.0F, 0.0F, -0.5F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -3.1416F));

        ModelPartData switch16 = panel2.addChild("switch16", ModelPartBuilder.create(),
                ModelTransform.of(0.0F, 0.5F, 3.0F, 0.0F, 0.0F, 0.7854F));

        ModelPartData cube_r139 = switch16.addChild("cube_r139",
                ModelPartBuilder.create().uv(70, 128).cuboid(0.0F, 0.0F, -0.5F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -3.1416F));

        ModelPartData switch17 = panel2.addChild("switch17", ModelPartBuilder.create(),
                ModelTransform.of(0.0F, -0.5F, -3.0F, 0.0F, 0.0F, 0.7854F));

        ModelPartData cube_r140 = switch17.addChild("cube_r140",
                ModelPartBuilder.create().uv(20, 126).cuboid(0.0F, 0.0F, -0.5F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -3.1416F));

        ModelPartData switch18 = panel2.addChild("switch18", ModelPartBuilder.create(),
                ModelTransform.of(0.0F, -0.5F, -2.0F, 0.0F, 0.0F, 0.7854F));

        ModelPartData cube_r141 = switch18.addChild("cube_r141",
                ModelPartBuilder.create().uv(12, 126).cuboid(0.0F, 0.0F, -0.5F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -3.1416F));

        ModelPartData switch19 = panel2.addChild("switch19", ModelPartBuilder.create(),
                ModelTransform.of(0.0F, -0.5F, 0.0F, 0.0F, 0.0F, 0.7854F));

        ModelPartData cube_r142 = switch19.addChild("cube_r142",
                ModelPartBuilder.create().uv(4, 126).cuboid(0.0F, 0.0F, -0.5F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -3.1416F));

        ModelPartData switch20 = panel2.addChild("switch20", ModelPartBuilder.create(),
                ModelTransform.of(0.0F, -0.5F, 1.0F, 0.0F, 0.0F, 0.7854F));

        ModelPartData cube_r143 = switch20.addChild("cube_r143",
                ModelPartBuilder.create().uv(0, 126).cuboid(0.0F, 0.0F, -0.5F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -3.1416F));

        ModelPartData switch21 = panel2.addChild("switch21", ModelPartBuilder.create(),
                ModelTransform.of(0.0F, -0.5F, 3.0F, 0.0F, 0.0F, 0.7854F));

        ModelPartData cube_r144 = switch21.addChild("cube_r144",
                ModelPartBuilder.create().uv(99, 121).cuboid(0.0F, 0.0F, -0.5F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -3.1416F));

        ModelPartData wiggles = bone14.addChild("wiggles",
                ModelPartBuilder.create().uv(147, 97).cuboid(0.0F, -1.0F, -1.0F, 1.0F, 2.0F, 2.0F, new Dilation(0.0F)),
                ModelTransform.of(-1.0F, -7.0F, 2.75F, 0.2618F, 0.0F, 0.0F));

        ModelPartData cube_r145 = wiggles.addChild("cube_r145",
                ModelPartBuilder.create().uv(73, 69).cuboid(0.0F, -3.0F, -1.0F, 1.0F, 3.0F, 2.0F, new Dilation(0.0F)),
                ModelTransform.of(1.1F, 0.0F, 2.0F, 0.2618F, 0.0F, 0.0F));

        ModelPartData cube_r146 = wiggles.addChild("cube_r146",
                ModelPartBuilder.create().uv(25, 152).cuboid(0.0F, -3.0F, 0.0F, 0.0F, 3.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(1.099F, 0.2588F, 1.0341F, 2.8798F, 0.0F, 3.1416F));

        ModelPartData cube_r147 = wiggles.addChild("cube_r147",
                ModelPartBuilder.create().uv(133, 87).cuboid(1.0F, -1.0F, -3.0F, 1.0F, 2.0F, 4.0F, new Dilation(0.0F)),
                ModelTransform.of(-0.6F, 0.0F, -1.0F, -0.2618F, 0.0F, 0.0F));

        ModelPartData cube_r148 = wiggles.addChild("cube_r148",
                ModelPartBuilder.create().uv(95, 38).cuboid(0.0F, 2.0F, -1.0F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(0.2F, -2.35F, -4.0F, -2.8798F, 0.0F, 3.1416F));

        ModelPartData cube_r149 = wiggles.addChild("cube_r149",
                ModelPartBuilder.create().uv(138, 144).cuboid(0.0F, -2.0F, -1.0F, 0.0F, 5.0F, 2.0F, new Dilation(0.0F)),
                ModelTransform.of(0.2F, -0.5F, 0.0F, 0.0F, 3.1416F, 0.0F));

        ModelPartData ball3 = bone14.addChild("ball3",
                ModelPartBuilder.create().uv(143, 142).cuboid(-1.0F, -1.0F, -1.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F))
                        .uv(100, 143).cuboid(-1.0F, -1.0F, -1.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.1F)),
                ModelTransform.pivot(-0.8F, -10.5F, -1.0F));

        ModelPartData cork = bone14.addChild("cork",
                ModelPartBuilder.create().uv(57, 119).cuboid(-1.5F, -2.0F, -2.5F, 1.0F, 2.0F, 4.0F, new Dilation(0.0F))
                        .uv(153, 6).cuboid(-1.0F, -1.5F, -2.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.2F)).uv(153, 6)
                        .cuboid(-1.0F, -1.5F, 0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.2F)).uv(150, 42)
                        .cuboid(-1.0F, -1.5F, -0.75F, 2.0F, 1.0F, 1.0F, new Dilation(-0.2F)).uv(147, 114)
                        .cuboid(-0.4F, -2.0F, -1.5F, 1.0F, 2.0F, 2.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, -1.5F, 5.5F, 0.1309F, 0.0F, 0.0F));

        ModelPartData cube_r150 = cork.addChild("cube_r150",
                ModelPartBuilder.create().uv(139, 119).cuboid(0.0F, -0.5F, -4.0F, 0.0F, 1.0F, 4.0F, new Dilation(0.0F)),
                ModelTransform.of(-0.4F, -1.0F, 1.5F, 0.0F, 0.0F, -3.1416F));

        ModelPartData dial = bone14.addChild("dial", ModelPartBuilder.create(),
                ModelTransform.of(-0.6F, -6.75F, -3.75F, 0.7854F, 0.0F, 0.0F));

        ModelPartData cube_r151 = dial.addChild("cube_r151",
                ModelPartBuilder.create().uv(80, 19).cuboid(0.0F, -1.5F, -1.5F, 0.0F, 3.0F, 3.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -3.1416F));

        ModelPartData ctrl_4 = controls.addChild("ctrl_4", ModelPartBuilder.create(),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 3.1416F, 0.0F));

        ModelPartData bone15 = ctrl_4.addChild("bone15", ModelPartBuilder.create(),
                ModelTransform.of(19.0F, -17.0F, 0.0F, 0.0F, 0.0F, -1.1781F));

        ModelPartData phone = bone15.addChild("phone",
                ModelPartBuilder.create().uv(40, 38).cuboid(-0.5F, -2.0F, -0.5F, 2.0F, 4.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.pivot(0.0F, -8.5F, -2.0F));

        ModelPartData cube_r152 = phone.addChild("cube_r152",
                ModelPartBuilder.create().uv(66, 139).cuboid(-1.0F, -2.0F, -1.0F, 2.0F, 3.0F, 2.0F, new Dilation(0.0F)),
                ModelTransform.of(-0.25F, 0.5F, 0.0F, 0.0F, 0.0F, -0.2182F));

        ModelPartData keypad = bone15.addChild("keypad",
                ModelPartBuilder.create().uv(118, 137)
                        .cuboid(-0.05F, -1.5F, -1.5F, 1.0F, 3.0F, 3.0F, new Dilation(-0.2F)).uv(75, 136)
                        .cuboid(0.05F, -1.25F, -1.75F, 1.0F, 3.0F, 3.0F, new Dilation(-0.2F)),
                ModelTransform.of(-1.0F, -3.5F, 3.0F, -0.2618F, 0.0F, 0.0F));

        ModelPartData cube_r153 = keypad.addChild("cube_r153",
                ModelPartBuilder.create().uv(0, 126).cuboid(0.0F, -1.5F, -1.5F, 0.0F, 3.0F, 3.0F, new Dilation(0.0F)),
                ModelTransform.of(0.2F, 0.0F, 0.0F, 0.0F, 0.0F, -3.1416F));

        ModelPartData cube_r154 = keypad.addChild("cube_r154",
                ModelPartBuilder.create().uv(88, 19).cuboid(0.0F, -0.5F, -0.5F, 0.0F, 2.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(0.9F, 0.0F, -0.5F, 0.0F, 0.0F, -3.1416F));

        ModelPartData valve = bone15.addChild("valve",
                ModelPartBuilder.create().uv(128, 119).cuboid(0.2F, 0.0F, -1.5F, 2.0F, 0.0F, 3.0F, new Dilation(0.0F))
                        .uv(118, 137).cuboid(-0.05F, -1.5F, -1.5F, 1.0F, 3.0F, 3.0F, new Dilation(-0.2F)).uv(73, 143)
                        .cuboid(0.2F, -1.0F, -1.0F, 2.0F, 2.0F, 2.0F, new Dilation(-0.4F)).uv(147, 102)
                        .cuboid(1.2F, -1.0F, -1.0F, 1.0F, 2.0F, 2.0F, new Dilation(-0.3F)),
                ModelTransform.pivot(-1.0F, -8.5F, 1.0F));

        ModelPartData cube_r155 = valve.addChild("cube_r155",
                ModelPartBuilder.create().uv(120, 9).cuboid(0.0F, -0.5F, -0.5F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(1.95F, -0.25F, 0.25F, 0.0F, 0.0F, -3.1416F));

        ModelPartData cube_r156 = valve.addChild("cube_r156",
                ModelPartBuilder.create().uv(0, 126).cuboid(0.0F, -1.5F, -1.5F, 0.0F, 3.0F, 3.0F, new Dilation(0.0F)),
                ModelTransform.of(0.2F, 0.0F, 0.0F, 0.0F, 0.0F, -3.1416F));

        ModelPartData keypad2 = bone15.addChild("keypad2",
                ModelPartBuilder.create().uv(118, 137)
                        .cuboid(-0.05F, -1.5F, -1.5F, 1.0F, 3.0F, 3.0F, new Dilation(-0.2F)).uv(125, 9)
                        .cuboid(0.0F, -1.75F, -1.25F, 1.0F, 3.0F, 1.0F, new Dilation(-0.2F)),
                ModelTransform.pivot(-1.0F, -4.0F, -0.5F));

        ModelPartData cube_r157 = keypad2.addChild("cube_r157",
                ModelPartBuilder.create().uv(29, 19).cuboid(0.0F, -0.5F, -0.5F, 0.0F, 2.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(0.45F, -1.75F, 1.0F, 0.0F, 0.0F, -3.1416F));

        ModelPartData cube_r158 = keypad2.addChild("cube_r158",
                ModelPartBuilder.create().uv(80, 38).cuboid(0.0F, -0.5F, -0.5F, 0.0F, 2.0F, 1.0F, new Dilation(0.0F))
                        .uv(80, 38).cuboid(0.0F, -0.5F, -1.25F, 0.0F, 2.0F, 1.0F, new Dilation(0.0F)).uv(32, 141)
                        .cuboid(0.0F, -1.5F, -2.0F, 0.0F, 3.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(0.45F, 2.25F, 1.0F, 0.0F, 0.0F, -3.1416F));

        ModelPartData cube_r159 = keypad2.addChild("cube_r159",
                ModelPartBuilder.create().uv(0, 126).cuboid(0.0F, -1.5F, -1.5F, 0.0F, 3.0F, 3.0F, new Dilation(0.0F)),
                ModelTransform.of(0.2F, 0.0F, 0.0F, 0.0F, 0.0F, -3.1416F));

        ModelPartData button47 = keypad2.addChild("button47", ModelPartBuilder.create().uv(0, 153).cuboid(-0.75F, -0.5F,
                -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.2F)), ModelTransform.pivot(0.95F, 0.75F, -0.75F));

        ModelPartData switch22 = keypad2.addChild("switch22", ModelPartBuilder.create(),
                ModelTransform.of(0.75F, 0.0F, 0.0F, 0.0F, 0.0F, 0.7854F));

        ModelPartData cube_r160 = switch22.addChild("cube_r160",
                ModelPartBuilder.create().uv(57, 119).cuboid(0.0F, -0.5F, 0.0F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, -0.5F, -0.25F, 0.0F, 0.0F, -3.1416F));

        ModelPartData switch23 = keypad2.addChild("switch23", ModelPartBuilder.create(),
                ModelTransform.of(0.75F, 0.0F, 0.75F, 0.0F, 0.0F, 0.7854F));

        ModelPartData cube_r161 = switch23.addChild("cube_r161",
                ModelPartBuilder.create().uv(50, 119).cuboid(0.0F, -0.5F, 0.0F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, -0.5F, -0.25F, 0.0F, 0.0F, -3.1416F));

        ModelPartData knob4 = keypad2.addChild("knob4",
                ModelPartBuilder.create().uv(153, 3).cuboid(0.0F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.1F))
                        .uv(80, 16).cuboid(0.75F, 0.0F, -0.5F, 1.0F, 0.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.75F, 0.5F, 0.4363F, 0.0F, 0.0F));

        ModelPartData knob2 = bone15.addChild("knob2", ModelPartBuilder.create(),
                ModelTransform.pivot(-1.0F, -2.0F, -3.5F));

        ModelPartData cube_r162 = knob2.addChild("cube_r162",
                ModelPartBuilder.create().uv(119, 56).cuboid(0.0F, -1.0F, -1.0F, 0.0F, 2.0F, 2.0F, new Dilation(0.0F)),
                ModelTransform.of(0.2F, 0.0F, 0.0F, 0.0F, 0.0F, -3.1416F));

        ModelPartData bone16 = knob2.addChild("bone16",
                ModelPartBuilder.create().uv(44, 143).cuboid(0.0F, -1.0F, -1.0F, 2.0F, 2.0F, 2.0F, new Dilation(-0.4F)),
                ModelTransform.pivot(-0.8F, 0.0F, 0.0F));

        ModelPartData button9 = bone15.addChild("button9", ModelPartBuilder.create(),
                ModelTransform.pivot(-1.0F, -4.5F, -3.5F));

        ModelPartData cube_r163 = button9.addChild("cube_r163",
                ModelPartBuilder.create().uv(44, 119).cuboid(0.0F, -1.0F, -1.0F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(0.3F, 0.75F, -0.5F, 0.0F, 0.0F, -3.1416F));

        ModelPartData cube_r164 = button9.addChild("cube_r164",
                ModelPartBuilder.create().uv(119, 56).cuboid(0.0F, -1.0F, -1.0F, 0.0F, 2.0F, 2.0F, new Dilation(0.0F)),
                ModelTransform.of(0.2F, 0.0F, 0.0F, 0.0F, 0.0F, -3.1416F));

        ModelPartData bone18 = button9.addChild("bone18", ModelPartBuilder.create().uv(146, 152).cuboid(-0.5F, -0.5F,
                -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.2F)), ModelTransform.pivot(0.2F, 0.0F, 0.0F));

        ModelPartData knob3 = bone15.addChild("knob3", ModelPartBuilder.create(),
                ModelTransform.pivot(-1.0F, -2.5F, 5.75F));

        ModelPartData cube_r165 = knob3.addChild("cube_r165",
                ModelPartBuilder.create().uv(119, 56).cuboid(0.0F, -1.0F, -1.0F, 0.0F, 2.0F, 2.0F, new Dilation(0.0F)),
                ModelTransform.of(0.2F, 0.0F, 0.0F, 0.0F, 0.0F, -3.1416F));

        ModelPartData bone17 = knob3.addChild("bone17",
                ModelPartBuilder.create().uv(142, 92).cuboid(0.0F, -1.0F, -1.0F, 2.0F, 2.0F, 2.0F, new Dilation(-0.4F)),
                ModelTransform.pivot(-0.8F, 0.0F, 0.0F));

        ModelPartData tube = bone15.addChild("tube",
                ModelPartBuilder.create().uv(112, 113).cuboid(-1.0F, -1.0F, 0.0F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F))
                        .uv(91, 130).cuboid(-1.0F, -1.0F, -1.0F, 1.0F, 3.0F, 1.0F, new Dilation(-0.3F)).uv(5, 153)
                        .cuboid(-1.0F, -1.0F, -0.6F, 1.0F, 1.0F, 1.0F, new Dilation(-0.3F)).uv(85, 16)
                        .cuboid(-1.0F, 0.75F, -1.0F, 1.0F, 0.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.pivot(0.0F, -7.0F, -4.5F));

        ModelPartData switch24 = bone15.addChild("switch24", ModelPartBuilder.create().uv(57, 139).cuboid(-0.8F, -1.0F,
                -1.0F, 2.0F, 3.0F, 2.0F, new Dilation(-0.4F)), ModelTransform.pivot(-1.25F, -8.0F, 3.25F));

        ModelPartData bone19 = switch24.addChild("bone19", ModelPartBuilder.create().uv(142, 130).cuboid(0.4F, -1.0F,
                -1.0F, 2.0F, 2.0F, 2.0F, new Dilation(-0.7F)), ModelTransform.pivot(-0.8F, 0.0F, 0.0F));

        ModelPartData switch25 = bone15.addChild("switch25", ModelPartBuilder.create().uv(57, 139).cuboid(-0.8F, -1.0F,
                -1.0F, 2.0F, 3.0F, 2.0F, new Dilation(-0.4F)), ModelTransform.pivot(-1.25F, -7.25F, 4.5F));

        ModelPartData bone20 = switch25.addChild("bone20", ModelPartBuilder.create().uv(142, 130).cuboid(0.4F, -1.0F,
                -1.0F, 2.0F, 2.0F, 2.0F, new Dilation(-0.7F)), ModelTransform.pivot(-0.8F, 0.0F, 0.0F));

        ModelPartData ctrl_5 = controls.addChild("ctrl_5", ModelPartBuilder.create(),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -2.0944F, 0.0F));

        ModelPartData bone21 = ctrl_5.addChild("bone21",
                ModelPartBuilder.create().uv(95, 38).cuboid(0.8F, 2.0F, 1.5F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F))
                        .uv(95, 38).cuboid(0.8F, 7.5F, 3.0F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F)).uv(95, 38)
                        .cuboid(0.8F, 2.0F, 3.0F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(19.0F, -17.0F, 0.0F, 0.0F, 0.0F, 1.9635F));

        ModelPartData keypad3 = bone21.addChild("keypad3",
                ModelPartBuilder.create().uv(107, 113).cuboid(0.8F, -2.0F, -2.0F, 0.0F, 4.0F, 4.0F, new Dilation(0.0F))
                        .uv(44, 119).cuboid(0.8F, -1.5F, -1.5F, 1.0F, 3.0F, 3.0F, new Dilation(0.3F)),
                ModelTransform.pivot(0.0F, 5.25F, -2.5F));

        ModelPartData button10 = keypad3.addChild("button10", ModelPartBuilder.create().uv(152, 144).cuboid(-0.5F,
                -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.2F)), ModelTransform.pivot(0.625F, 1.2F, -1.2F));

        ModelPartData button11 = keypad3.addChild("button11", ModelPartBuilder.create().uv(141, 152).cuboid(-0.5F,
                -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.2F)), ModelTransform.pivot(0.625F, 1.2F, -0.4F));

        ModelPartData button12 = keypad3.addChild("button12", ModelPartBuilder.create().uv(152, 138).cuboid(-0.5F,
                -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.2F)), ModelTransform.pivot(0.625F, 1.2F, 0.4F));

        ModelPartData button13 = keypad3.addChild("button13", ModelPartBuilder.create().uv(106, 153).cuboid(-0.5F,
                -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.2F)), ModelTransform.pivot(0.625F, 1.2F, 1.2F));

        ModelPartData button14 = keypad3.addChild("button14", ModelPartBuilder.create().uv(96, 154).cuboid(-0.5F, -0.5F,
                -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.2F)), ModelTransform.pivot(0.625F, 0.4F, -1.2F));

        ModelPartData button15 = keypad3.addChild("button15", ModelPartBuilder.create().uv(136, 152).cuboid(-0.5F,
                -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.2F)), ModelTransform.pivot(0.625F, 0.4F, -0.4F));

        ModelPartData button16 = keypad3.addChild("button16", ModelPartBuilder.create().uv(131, 152).cuboid(-0.5F,
                -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.2F)), ModelTransform.pivot(0.625F, 0.4F, 0.4F));

        ModelPartData button17 = keypad3.addChild("button17", ModelPartBuilder.create().uv(122, 152).cuboid(-0.5F,
                -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.2F)), ModelTransform.pivot(0.625F, 0.4F, 1.2F));

        ModelPartData button18 = keypad3.addChild("button18", ModelPartBuilder.create().uv(117, 152).cuboid(-0.5F,
                -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.2F)), ModelTransform.pivot(0.625F, -0.4F, -1.2F));

        ModelPartData button19 = keypad3.addChild("button19", ModelPartBuilder.create().uv(152, 113).cuboid(-0.5F,
                -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.2F)), ModelTransform.pivot(0.625F, -0.4F, -0.4F));

        ModelPartData button20 = keypad3.addChild("button20", ModelPartBuilder.create().uv(112, 152).cuboid(-0.5F,
                -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.2F)), ModelTransform.pivot(0.625F, -0.4F, 0.4F));

        ModelPartData button21 = keypad3.addChild("button21", ModelPartBuilder.create().uv(153, 101).cuboid(-0.5F,
                -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.2F)), ModelTransform.pivot(0.625F, -0.4F, 1.2F));

        ModelPartData button22 = keypad3.addChild("button22", ModelPartBuilder.create().uv(91, 154).cuboid(-0.5F, -0.5F,
                -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.2F)), ModelTransform.pivot(0.625F, -1.2F, -1.2F));

        ModelPartData button23 = keypad3.addChild("button23", ModelPartBuilder.create().uv(154, 70).cuboid(-0.5F, -0.5F,
                -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.2F)), ModelTransform.pivot(0.625F, -1.2F, -0.4F));

        ModelPartData button24 = keypad3.addChild("button24", ModelPartBuilder.create().uv(154, 45).cuboid(-0.5F, -0.5F,
                -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.2F)), ModelTransform.pivot(0.625F, -1.2F, 0.4F));

        ModelPartData button25 = keypad3.addChild("button25", ModelPartBuilder.create().uv(154, 98).cuboid(-0.5F, -0.5F,
                -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.2F)), ModelTransform.pivot(0.625F, -1.2F, 1.2F));

        ModelPartData keypad4 = bone21.addChild("keypad4",
                ModelPartBuilder.create().uv(107, 113).cuboid(0.8F, -2.0F, -2.0F, 0.0F, 4.0F, 4.0F, new Dilation(0.0F))
                        .uv(44, 119).cuboid(0.8F, -1.5F, -1.5F, 1.0F, 3.0F, 3.0F, new Dilation(0.3F)),
                ModelTransform.pivot(0.0F, 5.25F, 2.5F));

        ModelPartData button26 = keypad4.addChild("button26", ModelPartBuilder.create().uv(101, 152).cuboid(-0.5F,
                -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.2F)), ModelTransform.pivot(0.625F, 1.2F, -1.2F));

        ModelPartData button27 = keypad4.addChild("button27", ModelPartBuilder.create().uv(152, 95).cuboid(-0.5F, -0.5F,
                -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.2F)), ModelTransform.pivot(0.625F, 1.2F, -0.4F));

        ModelPartData button28 = keypad4.addChild("button28", ModelPartBuilder.create().uv(152, 76).cuboid(-0.5F, -0.5F,
                -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.2F)), ModelTransform.pivot(0.625F, 1.2F, 0.4F));

        ModelPartData button29 = keypad4.addChild("button29", ModelPartBuilder.create().uv(30, 154).cuboid(-0.5F, -0.5F,
                -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.2F)), ModelTransform.pivot(0.625F, 1.2F, 1.2F));

        ModelPartData button30 = keypad4.addChild("button30",
                ModelPartBuilder.create().uv(154, 0).cuboid(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.2F)),
                ModelTransform.pivot(0.625F, 0.4F, -1.2F));

        ModelPartData button31 = keypad4.addChild("button31", ModelPartBuilder.create().uv(76, 152).cuboid(-0.5F, -0.5F,
                -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.2F)), ModelTransform.pivot(0.625F, 0.4F, -0.4F));

        ModelPartData button32 = keypad4.addChild("button32", ModelPartBuilder.create().uv(152, 73).cuboid(-0.5F, -0.5F,
                -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.2F)), ModelTransform.pivot(0.625F, 0.4F, 0.4F));

        ModelPartData button33 = keypad4.addChild("button33", ModelPartBuilder.create().uv(152, 67).cuboid(-0.5F, -0.5F,
                -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.2F)), ModelTransform.pivot(0.625F, 0.4F, 1.2F));

        ModelPartData button34 = keypad4.addChild("button34", ModelPartBuilder.create().uv(152, 62).cuboid(-0.5F, -0.5F,
                -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.2F)), ModelTransform.pivot(0.625F, -0.4F, -1.2F));

        ModelPartData button35 = keypad4.addChild("button35", ModelPartBuilder.create().uv(152, 53).cuboid(-0.5F, -0.5F,
                -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.2F)), ModelTransform.pivot(0.625F, -0.4F, -0.4F));

        ModelPartData button36 = keypad4.addChild("button36", ModelPartBuilder.create().uv(53, 152).cuboid(-0.5F, -0.5F,
                -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.2F)), ModelTransform.pivot(0.625F, -0.4F, 0.4F));

        ModelPartData button37 = keypad4.addChild("button37", ModelPartBuilder.create().uv(36, 152).cuboid(-0.5F, -0.5F,
                -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.2F)), ModelTransform.pivot(0.625F, -0.4F, 1.2F));

        ModelPartData button38 = keypad4.addChild("button38", ModelPartBuilder.create().uv(151, 153).cuboid(-0.5F,
                -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.2F)), ModelTransform.pivot(0.625F, -1.2F, -1.2F));

        ModelPartData button39 = keypad4.addChild("button39", ModelPartBuilder.create().uv(153, 121).cuboid(-0.5F,
                -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.2F)), ModelTransform.pivot(0.625F, -1.2F, -0.4F));

        ModelPartData button40 = keypad4.addChild("button40", ModelPartBuilder.create().uv(153, 118).cuboid(-0.5F,
                -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.2F)), ModelTransform.pivot(0.625F, -1.2F, 0.4F));

        ModelPartData button41 = keypad4.addChild("button41", ModelPartBuilder.create().uv(154, 79).cuboid(-0.5F, -0.5F,
                -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.2F)), ModelTransform.pivot(0.625F, -1.2F, 1.2F));

        ModelPartData ring = bone21.addChild("ring",
                ModelPartBuilder.create().uv(80, 19).cuboid(0.8F, -2.0F, -1.0F, 0.0F, 3.0F, 3.0F, new Dilation(0.0F))
                        .uv(14, 149).cuboid(0.0F, -0.5F, -0.5F, 1.0F, 1.0F, 2.0F, new Dilation(-0.2F)).uv(90, 8)
                        .cuboid(0.0F, -0.5F, -0.2F, 1.0F, 1.0F, 0.0F, new Dilation(0.0F)).uv(87, 47).cuboid(0.0F, -0.5F,
                                1.2F, 1.0F, 1.0F, 0.0F, new Dilation(0.0F)),
                ModelTransform.pivot(0.0F, 9.5F, 0.5F));

        ModelPartData knob5 = bone21.addChild("knob5",
                ModelPartBuilder.create().uv(119, 56).cuboid(0.2F, -1.0F, -1.0F, 0.0F, 2.0F, 2.0F, new Dilation(0.0F)),
                ModelTransform.pivot(0.6F, 8.5F, -2.25F));

        ModelPartData bone22 = knob5.addChild("bone22", ModelPartBuilder.create(),
                ModelTransform.pivot(-0.8F, 0.0F, 0.0F));

        ModelPartData cube_r166 = bone22.addChild("cube_r166", ModelPartBuilder.create().uv(142, 92).cuboid(-2.0F,
                -1.0F, -1.0F, 2.0F, 2.0F, 2.0F, new Dilation(-0.4F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -3.1416F));

        ModelPartData keyboard = bone21.addChild("keyboard", ModelPartBuilder.create().uv(119, 101).cuboid(-1.0F, -1.0F,
                -3.0F, 1.0F, 2.0F, 6.0F, new Dilation(0.0F)), ModelTransform.pivot(1.0F, 12.0F, 0.0F));

        ModelPartData switch26 = keyboard.addChild("switch26",
                ModelPartBuilder.create().uv(115, 9).cuboid(0.0F, -0.5F, -0.5F, 0.0F, 1.0F, 1.0F, new Dilation(0.001F)),
                ModelTransform.pivot(-1.0F, 0.5F, 0.0F));

        ModelPartData switch27 = keyboard.addChild("switch27", ModelPartBuilder.create().uv(107, 113).cuboid(0.0F,
                -0.5F, -0.5F, 0.0F, 1.0F, 1.0F, new Dilation(0.001F)), ModelTransform.pivot(-1.0F, 0.5F, -1.25F));

        ModelPartData switch28 = keyboard.addChild("switch28", ModelPartBuilder.create().uv(91, 113).cuboid(0.0F, -0.5F,
                -0.5F, 0.0F, 1.0F, 1.0F, new Dilation(0.001F)), ModelTransform.pivot(-1.0F, 0.5F, 1.25F));

        ModelPartData tubes = bone21.addChild("tubes",
                ModelPartBuilder.create().uv(29, 38).cuboid(0.0F, -2.0F, 0.0F, 1.0F, 4.0F, 1.0F, new Dilation(0.0F))
                        .uv(93, 19).cuboid(0.0F, -2.0F, -1.0F, 1.0F, 2.0F, 1.0F, new Dilation(-0.2F)).uv(91, 142)
                        .cuboid(0.3F, 0.0F, 0.0F, 1.0F, 1.0F, 3.0F, new Dilation(-0.3F)).uv(9, 0)
                        .cuboid(0.0F, -4.0F, 0.0F, 1.0F, 9.0F, 1.0F, new Dilation(-0.3F)).uv(113, 56)
                        .cuboid(0.0F, -4.0F, -1.0F, 1.0F, 3.0F, 1.0F, new Dilation(-0.3F)).uv(113, 70)
                        .cuboid(-0.25F, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F)).uv(92, 107)
                        .cuboid(-0.35F, 0.25F, 0.25F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.pivot(0.0F, 4.0F, -6.0F));

        ModelPartData wiggles2 = bone21.addChild("wiggles2",
                ModelPartBuilder.create().uv(138, 144).cuboid(0.8F, -2.5F, -1.0F, 0.0F, 5.0F, 2.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 4.0F, 5.75F, -0.2182F, 0.0F, 0.0F));

        ModelPartData cube_r167 = wiggles2.addChild("cube_r167",
                ModelPartBuilder.create().uv(107, 0).cuboid(0.0F, -3.0F, 0.0F, 1.0F, 3.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(-1.102F, -0.4824F, 1.75F, 2.8798F, 0.0F, 0.0F));

        ModelPartData cube_r168 = wiggles2.addChild("cube_r168",
                ModelPartBuilder.create().uv(81, 134).cuboid(0.0F, -3.0F, 0.0F, 0.0F, 3.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(-0.101F, -0.7412F, 0.7841F, 2.8798F, 0.0F, 0.0F));

        ModelPartData cube_r169 = wiggles2.addChild("cube_r169",
                ModelPartBuilder.create().uv(0, 149).cuboid(1.0F, -1.0F, -2.0F, 1.0F, 1.0F, 2.0F, new Dilation(0.0F)),
                ModelTransform.of(-1.1F, 0.25F, -0.75F, -0.2618F, 0.0F, 0.0F));

        ModelPartData cube_r170 = wiggles2.addChild("cube_r170",
                ModelPartBuilder.create().uv(147, 97).cuboid(-1.0F, -1.0F, -1.0F, 1.0F, 2.0F, 2.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -3.1416F));

        ModelPartData copper = bone21.addChild("copper",
                ModelPartBuilder.create().uv(147, 79)
                        .cuboid(0.0F, -1.0F, -1.0F, 1.0F, 2.0F, 2.0F, new Dilation(-0.001F)).uv(133, 144)
                        .cuboid(0.8F, -2.5F, -1.0F, 0.0F, 5.0F, 2.0F, new Dilation(0.0F)).uv(28, 149)
                        .cuboid(-0.2F, -2.5F, -1.0F, 1.0F, 5.0F, 0.0F, new Dilation(0.0F)).uv(50, 148)
                        .cuboid(-0.2F, -2.5F, 1.0F, 1.0F, 5.0F, 0.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 1.75F, -1.5F, -1.4399F, 0.0F, 0.0F));

        ModelPartData ctrl_6 = controls.addChild("ctrl_6", ModelPartBuilder.create(),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone23 = ctrl_6.addChild("bone23",
                ModelPartBuilder.create().uv(95, 38).cuboid(0.8F, 4.0F, -2.0F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F))
                        .uv(95, 38).cuboid(0.8F, 6.75F, 0.75F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(19.0F, -17.0F, 0.0F, 0.0F, 0.0F, 1.9635F));

        ModelPartData crystal = bone23.addChild("crystal",
                ModelPartBuilder.create().uv(80, 19).cuboid(0.8F, -1.5F, -1.5F, 0.0F, 3.0F, 3.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 2.0F, -1.0F, 1.5708F, 0.0F, 0.0F));

        ModelPartData bone24 = crystal.addChild("bone24", ModelPartBuilder.create(),
                ModelTransform.of(1.45F, 0.0F, 0.25F, 0.0F, -0.6109F, 0.0F));

        ModelPartData cube_r171 = bone24.addChild("cube_r171",
                ModelPartBuilder.create().uv(142, 48).cuboid(-1.0F, -1.0F, -1.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.7854F));

        ModelPartData box2 = bone23.addChild("box2",
                ModelPartBuilder.create().uv(115, 30).cuboid(-1.0F, -2.0F, -1.0F, 2.0F, 5.0F, 2.0F, new Dilation(0.0F))
                        .uv(38, 0).cuboid(-1.0F, -2.0F, -1.0F, 2.0F, 5.0F, 2.0F, new Dilation(-0.2F)).uv(136, 61)
                        .cuboid(-1.5F, 2.75F, -1.0F, 3.0F, 2.0F, 2.0F, new Dilation(-0.5F)).uv(136, 56)
                        .cuboid(-1.5F, -3.75F, -1.0F, 3.0F, 2.0F, 2.0F, new Dilation(-0.5F)).uv(108, 140)
                        .cuboid(-2.0F, 2.75F, -1.0F, 2.0F, 2.0F, 2.0F, new Dilation(-0.7F)).uv(140, 107)
                        .cuboid(-2.0F, -3.75F, -1.0F, 2.0F, 2.0F, 2.0F, new Dilation(-0.7F)).uv(0, 0)
                        .cuboid(-1.0F, -4.5F, -1.0F, 2.0F, 10.0F, 2.0F, new Dilation(-0.7F)),
                ModelTransform.of(0.0F, 6.0F, 0.0F, -0.3491F, 0.0F, 0.0F));

        ModelPartData ball4 = bone23.addChild("ball4",
                ModelPartBuilder.create().uv(142, 43).cuboid(-1.0F, -1.0F, -1.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F))
                        .uv(142, 38).cuboid(-1.0F, -1.0F, -1.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.1F)),
                ModelTransform.pivot(1.2F, 11.0F, 1.0F));

        ModelPartData lights = bone23.addChild("lights", ModelPartBuilder.create().uv(91, 113).cuboid(-1.0F, -2.5653F,
                -1.9957F, 1.0F, 5.0F, 3.0F, new Dilation(0.0F)),
                ModelTransform.of(1.5F, 6.5F, -3.25F, 0.1309F, 0.0F, 0.0F));

        ModelPartData button42 = lights.addChild("button42", ModelPartBuilder.create().uv(20, 152).cuboid(-0.25F, -0.5F,
                -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.1F)), ModelTransform.pivot(-1.0F, 1.4347F, -0.4957F));

        ModelPartData bone25 = button42.addChild("bone25", ModelPartBuilder.create().uv(151, 130).cuboid(-1.0F, -0.5F,
                -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(0.4F, 0.0F, 0.0F));

        ModelPartData bone10 = bone25.addChild("bone10", ModelPartBuilder.create().uv(31, 151).cuboid(-1.0F, -0.5F,
                -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.01F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData button43 = lights.addChild("button43", ModelPartBuilder.create().uv(151, 92).cuboid(-0.25F, -0.5F,
                -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.1F)), ModelTransform.pivot(-1.0F, -0.0653F, -0.4957F));

        ModelPartData bone26 = button43.addChild("bone26",
                ModelPartBuilder.create().uv(151, 86).cuboid(-1.0F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.pivot(0.4F, 0.0F, 0.0F));

        ModelPartData bone11 = bone26.addChild("bone11", ModelPartBuilder.create().uv(151, 30).cuboid(-1.0F, -0.5F,
                -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.01F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData button44 = lights.addChild("button44", ModelPartBuilder.create().uv(151, 50).cuboid(-0.25F, -0.5F,
                -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.1F)), ModelTransform.pivot(-1.0F, -1.5653F, -0.4957F));

        ModelPartData bone27 = button44.addChild("bone27",
                ModelPartBuilder.create().uv(151, 37).cuboid(-1.0F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.pivot(0.4F, 0.0F, 0.0F));

        ModelPartData bone12 = bone27.addChild("bone12", ModelPartBuilder.create().uv(151, 12).cuboid(-1.0F, -0.5F,
                -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.01F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData tube2 = bone23.addChild("tube2",
                ModelPartBuilder.create().uv(53, 144).cuboid(0.0F, -3.0F, 0.0F, 1.0F, 6.0F, 1.0F, new Dilation(0.0F))
                        .uv(91, 147).cuboid(0.0F, -2.5F, 0.0F, 1.0F, 5.0F, 1.0F, new Dilation(0.1F)).uv(146, 109)
                        .cuboid(0.8F, 1.4F, -1.0F, 0.0F, 1.0F, 3.0F, new Dilation(0.0F)).uv(31, 146)
                        .cuboid(0.8F, -2.4F, -1.0F, 0.0F, 1.0F, 3.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 6.0F, 4.0F, -0.5236F, 0.0F, 0.0F));

        ModelPartData cylinder = bone23.addChild("cylinder",
                ModelPartBuilder.create().uv(9, 141).cuboid(-2.0F, -1.0F, -1.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F))
                        .uv(36, 149).cuboid(-3.0F, 1.0F, -1.0F, 1.0F, 0.0F, 2.0F, new Dilation(0.0F)).uv(7, 11)
                        .cuboid(-3.0F, -1.0F, -1.0F, 1.0F, 0.0F, 2.0F, new Dilation(0.0F)).uv(84, 107)
                        .cuboid(-3.0F, -1.0F, -1.0F, 1.0F, 2.0F, 0.0F, new Dilation(0.0F)).uv(102, 0)
                        .cuboid(-3.0F, -1.0F, 1.0F, 1.0F, 2.0F, 0.0F, new Dilation(0.0F)),
                ModelTransform.pivot(1.0F, 2.0F, -4.0F));

        ModelPartData cube_r172 = cylinder.addChild("cube_r172",
                ModelPartBuilder.create().uv(73, 62).cuboid(0.0F, -1.25F, -2.75F, 0.0F, 3.0F, 3.0F, new Dilation(0.0F)),
                ModelTransform.of(-0.2F, 0.5F, -0.75F, 0.2618F, 0.0F, 0.0F));

        ModelPartData pedal = bone23.addChild("pedal",
                ModelPartBuilder.create().uv(32, 135).cuboid(0.0F, -2.0F, -0.5F, 0.0F, 4.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.pivot(0.6F, 3.0F, 3.0F));

        ModelPartData bone28 = pedal.addChild("bone28",
                ModelPartBuilder.create().uv(134, 24).cuboid(0.0F, 0.0F, -0.5F, 0.0F, 4.0F, 1.0F, new Dilation(0.0F))
                        .uv(81, 128).cuboid(-0.1F, 0.25F, -0.25F, 0.0F, 4.0F, 1.0F, new Dilation(0.0F)).uv(29, 16)
                        .cuboid(0.0F, 3.0F, -0.5F, 2.0F, 0.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, -2.0F, 0.0F, 0.0F, 0.0F, 0.5236F));

        ModelPartData wires2 = bone23.addChild("wires2",
                ModelPartBuilder.create().uv(115, 9).cuboid(0.0F, -4.0F, -5.0F, 0.0F, 11.0F, 9.0F, new Dilation(0.0F)),
                ModelTransform.pivot(0.5F, 3.0F, 4.0F));

        ModelPartData p_ctrl_1 = controls.addChild("p_ctrl_1", ModelPartBuilder.create(),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.5236F, 0.0F));

        ModelPartData bone29 = p_ctrl_1.addChild("bone29", ModelPartBuilder.create(),
                ModelTransform.of(19.0F, -20.0F, 0.0F, 0.0F, 0.0F, 2.0071F));

        ModelPartData lever = bone29.addChild("lever",
                ModelPartBuilder.create().uv(134, 18)
                        .cuboid(-2.0F, -2.0F, 1.0F, 4.0F, 4.0F, 1.0F, new Dilation(-0.201F)).uv(147, 0)
                        .cuboid(-1.0F, -1.0F, -1.8F, 2.0F, 2.0F, 1.0F, new Dilation(-0.201F)).uv(138, 0)
                        .cuboid(-1.0F, -1.5F, -0.2F, 2.0F, 3.0F, 2.0F, new Dilation(-0.201F)),
                ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData cube_r173 = lever
                .addChild(
                        "cube_r173", ModelPartBuilder.create().uv(139, 125).cuboid(-1.0F, -1.0F, -1.0F, 2.0F, 2.0F,
                                2.0F, new Dilation(-0.001F)),
                        ModelTransform.of(0.75F, 0.0F, 0.0F, 0.0F, 0.0F, 0.8727F));

        ModelPartData bone30 = lever.addChild("bone30",
                ModelPartBuilder.create().uv(41, 12).cuboid(-1.85F, -0.6F, -1.0F, 2.0F, 1.0F, 0.0F, new Dilation(0.0F))
                        .uv(95, 26).cuboid(-1.85F, -0.6F, -1.0F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F)).uv(150, 150)
                        .cuboid(-3.35F, -0.6F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)).uv(36, 16)
                        .cuboid(-3.35F, -0.6F, -0.5F, 2.0F, 1.0F, 0.0F, new Dilation(0.0F)),
                ModelTransform.of(0.1F, 0.1F, 0.0F, 0.0F, 0.0F, -0.9599F));

        ModelPartData bone8 = lever.addChild("bone8",
                ModelPartBuilder.create().uv(26, 73).cuboid(-1.25F, -0.6F, -0.9F, 1.0F, 1.0F, 0.0F, new Dilation(0.0F))
                        .uv(95, 26).cuboid(-1.25F, -0.6F, -0.9F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F)).uv(150, 150)
                        .cuboid(-2.75F, -0.6F, -0.4F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)).uv(36, 16)
                        .cuboid(-2.75F, -0.6F, -0.4F, 2.0F, 1.0F, 0.0F, new Dilation(0.0F)),
                ModelTransform.of(0.1F, 0.1F, 0.0F, 0.0F, 0.0F, 0.829F));

        ModelPartData crank = bone29.addChild("crank",
                ModelPartBuilder.create().uv(147, 68).cuboid(-1.0F, -1.0F, -1.0F, 1.0F, 2.0F, 2.0F, new Dilation(0.0F)),
                ModelTransform.of(2.75F, -5.0F, 0.0F, 0.0F, 0.0F, 0.8727F));

        ModelPartData bone32 = crank.addChild("bone32",
                ModelPartBuilder.create().uv(146, 25).cuboid(-1.0F, -0.5F, -0.5F, 0.0F, 1.0F, 3.0F, new Dilation(0.0F))
                        .uv(77, 57).cuboid(-2.0F, -0.5F, 2.5F, 1.0F, 1.0F, 0.0F, new Dilation(0.0F)),
                ModelTransform.of(-0.1F, 0.0F, 0.0F, -0.6981F, 0.0F, 0.0F));

        ModelPartData box3 = bone29.addChild("box3",
                ModelPartBuilder.create().uv(115, 30).cuboid(-1.0F, -2.0F, -1.0F, 2.0F, 5.0F, 2.0F, new Dilation(0.0F))
                        .uv(38, 0).cuboid(-1.0F, -2.0F, -1.0F, 2.0F, 5.0F, 2.0F, new Dilation(-0.2F)).uv(136, 61)
                        .cuboid(-1.5F, 2.75F, -1.0F, 3.0F, 2.0F, 2.0F, new Dilation(-0.5F)).uv(136, 56)
                        .cuboid(-1.5F, -3.75F, -1.0F, 3.0F, 2.0F, 2.0F, new Dilation(-0.5F)).uv(108, 140)
                        .cuboid(-2.0F, 2.75F, -1.0F, 2.0F, 2.0F, 2.0F, new Dilation(-0.7F)).uv(140, 107)
                        .cuboid(-2.0F, -3.75F, -1.0F, 2.0F, 2.0F, 2.0F, new Dilation(-0.7F)).uv(0, 0)
                        .cuboid(-1.0F, -4.5F, -1.0F, 2.0F, 10.0F, 2.0F, new Dilation(-0.7F)),
                ModelTransform.of(0.0F, 7.25F, -0.5F, 0.0F, 0.0F, -0.2618F));

        ModelPartData bone39 = bone29.addChild("bone39",
                ModelPartBuilder.create().uv(147, 56).cuboid(-1.0F, -1.5F, -0.5F, 2.0F, 1.0F, 1.0F, new Dilation(0.0F))
                        .uv(110, 145).cuboid(0.25F, -1.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.1F)),
                ModelTransform.of(0.0F, 6.25F, 1.25F, 0.0F, 0.0F, -0.2618F));

        ModelPartData wires3 = bone29.addChild("wires3", ModelPartBuilder.create(),
                ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData cube_r174 = wires3
                .addChild(
                        "cube_r174", ModelPartBuilder.create().uv(84, 77).cuboid(-1.0F, -8.0F, -2.45F, 24.0F, 21.0F,
                                0.0F, new Dilation(0.0F)),
                        ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0873F, 0.0F, -0.1309F));

        ModelPartData cube_r175 = wires3
                .addChild(
                        "cube_r175", ModelPartBuilder.create().uv(35, 99).cuboid(-1.0F, -6.0F, -2.25F, 24.0F, 19.0F,
                                0.0F, new Dilation(0.0F)),
                        ModelTransform.of(0.0F, 0.0F, 0.0F, -0.0436F, 0.0F, -0.1309F));

        ModelPartData bone61 = bone29.addChild("bone61",
                ModelPartBuilder.create().uv(148, 126)
                        .cuboid(-1.65F, -1.0F, -1.0F, 1.0F, 1.0F, 2.0F, new Dilation(0.0F)).uv(101, 148)
                        .cuboid(-1.65F, -1.0F, -1.0F, 1.0F, 1.0F, 2.0F, new Dilation(0.1F)),
                ModelTransform.of(2.0F, 9.0F, 1.75F, 1.5708F, 0.0F, -0.2182F));

        ModelPartData p_ctrl_2 = controls.addChild("p_ctrl_2", ModelPartBuilder.create(),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.5708F, 0.0F));

        ModelPartData bone33 = p_ctrl_2.addChild("bone33", ModelPartBuilder.create(),
                ModelTransform.of(19.0F, -20.0F, 0.0F, 0.0F, 0.0F, 2.0071F));

        ModelPartData valve2 = bone33.addChild("valve2",
                ModelPartBuilder.create().uv(21, 66).cuboid(0.0F, -1.5F, -1.5F, 0.0F, 3.0F, 3.0F, new Dilation(0.0F))
                        .uv(80, 5).cuboid(-0.1F, 0.0F, 0.0F, 0.0F, 2.0F, 2.0F, new Dilation(0.0F)),
                ModelTransform.of(1.05F, 8.0F, 0.0F, 0.0F, 0.0F, -0.2182F));

        ModelPartData meter = bone33.addChild("meter", ModelPartBuilder.create(),
                ModelTransform.of(1.0F, -3.0F, 0.0F, 0.0F, 0.0F, 0.5672F));

        ModelPartData bone35 = meter.addChild("bone35",
                ModelPartBuilder.create().uv(19, 126)
                        .cuboid(-1.6F, -2.0F, -2.0F, 3.0F, 4.0F, 4.0F, new Dilation(-0.95F)).uv(62, 57)
                        .cuboid(-0.5F, -1.0F, -1.0F, 0.0F, 2.0F, 2.0F, new Dilation(0.0F)),
                ModelTransform.of(0.25F, -1.0F, 0.0F, -0.7854F, 0.0F, 0.0F));

        ModelPartData bone9 = bone35.addChild("bone9",
                ModelPartBuilder.create().uv(42, 14).cuboid(0.0F, -0.75F, -1.0F, 0.0F, 1.0F, 2.0F, new Dilation(0.0F)),
                ModelTransform.of(-0.6F, 0.0F, 0.0F, -0.1745F, 0.0F, 0.0F));

        ModelPartData bone31 = bone33.addChild("bone31",
                ModelPartBuilder.create().uv(119, 99).cuboid(0.0F, -2.0F, 0.1F, 1.0F, 6.0F, 1.0F, new Dilation(-0.1F))
                        .uv(119, 99).cuboid(0.0F, -2.0F, 1.1F, 1.0F, 6.0F, 1.0F, new Dilation(-0.1F)).uv(119, 99)
                        .cuboid(0.0F, -2.0F, -0.9F, 1.0F, 6.0F, 1.0F, new Dilation(-0.1F)).uv(119, 99)
                        .cuboid(0.0F, -2.0F, -1.9F, 1.0F, 6.0F, 1.0F, new Dilation(-0.1F)).uv(95, 42)
                        .cuboid(0.0F, -2.0F, -1.15F, 0.0F, 6.0F, 1.0F, new Dilation(0.0F)).uv(95, 42)
                        .cuboid(0.0F, -2.0F, -0.15F, 0.0F, 6.0F, 1.0F, new Dilation(0.0F)).uv(95, 8)
                        .cuboid(-0.75F, -3.0F, -0.15F, 0.0F, 7.0F, 1.0F, new Dilation(0.0F)).uv(21, 73)
                        .cuboid(-0.75F, 4.0F, -0.15F, 1.0F, 0.0F, 1.0F, new Dilation(0.0F)).uv(21, 73)
                        .cuboid(-0.75F, -2.0F, -0.15F, 1.0F, 0.0F, 1.0F, new Dilation(0.0F)).uv(95, 42)
                        .cuboid(0.0F, -2.0F, 0.85F, 0.0F, 6.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.pivot(-0.25F, 1.0F, -0.1F));

        ModelPartData bone34 = bone31.addChild("bone34",
                ModelPartBuilder.create().uv(0, 141).cuboid(-0.65F, -0.5F, -1.4F, 1.0F, 1.0F, 3.0F, new Dilation(0.0F))
                        .uv(84, 99).cuboid(-0.8F, -0.25F, -0.15F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.pivot(0.0F, -0.5F, 0.0F));

        ModelPartData crank2 = bone31.addChild("crank2",
                ModelPartBuilder.create().uv(124, 144).cuboid(-1.0F, 0.0F, -1.0F, 2.0F, 1.0F, 2.0F, new Dilation(0.0F))
                        .uv(22, 12).cuboid(-1.0F, 0.0F, -0.25F, 2.0F, 0.0F, 1.0F, new Dilation(0.0F)).uv(95, 50)
                        .cuboid(-1.0F, -1.0F, -0.75F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(-0.75F, -2.6F, 0.1F, 0.0F, -0.6981F, 0.0F));

        ModelPartData wires4 = bone33.addChild("wires4", ModelPartBuilder.create(),
                ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData cube_r176 = wires4
                .addChild(
                        "cube_r176", ModelPartBuilder.create().uv(62, 57).cuboid(2.25F, -4.05F, -3.4F, 0.0F, 11.0F,
                                5.0F, new Dilation(0.0F)),
                        ModelTransform.of(0.0F, 0.0F, 0.0F, 0.1309F, 0.2618F, -0.1309F));

        ModelPartData p_ctrl_3 = controls.addChild("p_ctrl_3", ModelPartBuilder.create(),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 2.618F, 0.0F));

        ModelPartData bone36 = p_ctrl_3.addChild("bone36", ModelPartBuilder.create(),
                ModelTransform.of(19.0F, -20.0F, 0.0F, 0.0F, 0.0F, 2.0071F));

        ModelPartData cube_r177 = bone36.addChild("cube_r177", ModelPartBuilder.create().uv(136, 139).cuboid(-2.75F,
                9.0F, -1.0F, 2.0F, 2.0F, 2.0F, new Dilation(-0.2F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.3054F));

        ModelPartData box4 = bone36.addChild("box4",
                ModelPartBuilder.create().uv(115, 30).cuboid(-1.0F, -2.0F, -0.5F, 2.0F, 5.0F, 2.0F, new Dilation(0.0F))
                        .uv(38, 0).cuboid(-1.0F, -2.0F, -0.5F, 2.0F, 5.0F, 2.0F, new Dilation(-0.2F)).uv(136, 61)
                        .cuboid(-1.5F, 2.75F, -0.5F, 3.0F, 2.0F, 2.0F, new Dilation(-0.5F)).uv(136, 56)
                        .cuboid(-1.5F, -3.75F, -0.5F, 3.0F, 2.0F, 2.0F, new Dilation(-0.5F)).uv(108, 140)
                        .cuboid(-2.0F, 2.75F, -0.5F, 2.0F, 2.0F, 2.0F, new Dilation(-0.7F)).uv(140, 107)
                        .cuboid(-2.0F, -3.75F, -0.5F, 2.0F, 2.0F, 2.0F, new Dilation(-0.7F)).uv(0, 0)
                        .cuboid(-1.0F, -4.5F, -0.5F, 2.0F, 10.0F, 2.0F, new Dilation(-0.7F)),
                ModelTransform.of(-0.5F, 2.25F, 0.0F, 0.0F, 0.0F, -0.0436F));

        ModelPartData bone37 = bone36.addChild("bone37", ModelPartBuilder.create().uv(139, 9).cuboid(-0.75F, -1.0F,
                -1.0F, 2.0F, 2.0F, 2.0F, new Dilation(-0.3F)),
                ModelTransform.of(1.0F, -4.0F, 0.0F, 0.0F, 0.0F, 0.3491F));

        ModelPartData cube_r178 = bone37.addChild("cube_r178",
                ModelPartBuilder.create().uv(84, 107).cuboid(-0.5F, 0.0F, -1.5F, 2.0F, 0.0F, 3.0F, new Dilation(0.0F)),
                ModelTransform.of(0.25F, 0.0F, 0.0F, 0.7854F, 0.0F, 0.0F));

        ModelPartData cube_r179 = bone37.addChild("cube_r179",
                ModelPartBuilder.create().uv(84, 107).cuboid(-0.5F, 0.0F, -1.5F, 2.0F, 0.0F, 3.0F, new Dilation(0.0F)),
                ModelTransform.of(0.25F, 0.0F, 0.0F, -0.7854F, 0.0F, 0.0F));

        ModelPartData bone38 = bone36.addChild("bone38", ModelPartBuilder.create().uv(139, 9).cuboid(-0.75F, -1.0F,
                -1.0F, 2.0F, 2.0F, 2.0F, new Dilation(-0.3F)),
                ModelTransform.of(0.0F, -1.0F, -0.9F, 0.0F, 0.0F, 0.1745F));

        ModelPartData cube_r180 = bone38.addChild("cube_r180",
                ModelPartBuilder.create().uv(84, 107).cuboid(-0.5F, 0.0F, -1.5F, 2.0F, 0.0F, 3.0F, new Dilation(0.0F)),
                ModelTransform.of(0.25F, 0.0F, 0.0F, 0.7854F, 0.0F, 0.0F));

        ModelPartData cube_r181 = bone38.addChild("cube_r181",
                ModelPartBuilder.create().uv(84, 107).cuboid(-0.5F, 0.0F, -1.5F, 2.0F, 0.0F, 3.0F, new Dilation(0.0F)),
                ModelTransform.of(0.25F, 0.0F, 0.0F, -0.7854F, 0.0F, 0.0F));

        ModelPartData wires5 = bone36.addChild("wires5", ModelPartBuilder.create(),
                ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData cube_r182 = wires5
                .addChild(
                        "cube_r182", ModelPartBuilder.create().uv(35, 77).cuboid(-1.5F, -5.0F, 3.55F, 24.0F, 21.0F,
                                0.0F, new Dilation(0.0F)),
                        ModelTransform.of(0.0F, 0.0F, 0.0F, -0.0396F, 0.0496F, -0.3493F));

        ModelPartData circuit = wires5.addChild("circuit",
                ModelPartBuilder.create().uv(80, 38).cuboid(1.25F, -3.0F, -2.0F, 0.0F, 4.0F, 4.0F, new Dilation(0.0F))
                        .uv(150, 24).cuboid(0.5F, -1.0F, -2.1F, 2.0F, 1.0F, 1.0F, new Dilation(-0.3F)).uv(150, 24)
                        .cuboid(0.5F, -1.0F, -1.4F, 2.0F, 1.0F, 1.0F, new Dilation(-0.3F)).uv(150, 24)
                        .cuboid(0.5F, -1.7F, -2.1F, 2.0F, 1.0F, 1.0F, new Dilation(-0.3F)).uv(150, 24)
                        .cuboid(0.5F, -1.7F, -1.4F, 2.0F, 1.0F, 1.0F, new Dilation(-0.3F)).uv(150, 24)
                        .cuboid(0.5F, -2.4F, -2.1F, 2.0F, 1.0F, 1.0F, new Dilation(-0.3F)).uv(150, 24)
                        .cuboid(0.5F, -2.4F, -1.4F, 2.0F, 1.0F, 1.0F, new Dilation(-0.3F)).uv(150, 24)
                        .cuboid(0.5F, -3.1F, -2.1F, 2.0F, 1.0F, 1.0F, new Dilation(-0.3F)).uv(150, 24)
                        .cuboid(0.5F, -3.1F, -1.4F, 2.0F, 1.0F, 1.0F, new Dilation(-0.3F)),
                ModelTransform.of(-0.25F, 7.95F, 0.0F, 0.0F, 0.0F, -0.2182F));

        ModelPartData button45 = circuit.addChild("button45", ModelPartBuilder.create().uv(150, 33).cuboid(-0.75F,
                -0.5F, -0.5F, 2.0F, 1.0F, 1.0F, new Dilation(-0.2F)), ModelTransform.pivot(1.25F, 0.25F, -1.25F));

        ModelPartData pully2 = bone36.addChild("pully2",
                ModelPartBuilder.create().uv(90, 5).cuboid(-2.0F, -1.0F, 0.0F, 4.0F, 1.0F, 1.0F, new Dilation(0.0F))
                        .uv(118, 124).cuboid(2.0F, -0.75F, 0.5F, 10.0F, 12.0F, 0.0F, new Dilation(0.0F)).uv(101, 5)
                        .cuboid(1.75F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, new Dilation(-0.2F)),
                ModelTransform.pivot(7.0F, -4.75F, 2.0F));

        ModelPartData bone44 = pully2.addChild("bone44",
                ModelPartBuilder.create().uv(136, 119).cuboid(-2.5F, -0.5F, -0.5F, 2.0F, 1.0F, 1.0F, new Dilation(0.0F))
                        .uv(44, 126).cuboid(-3.0F, -0.5F, -0.5F, 3.0F, 1.0F, 1.0F, new Dilation(-0.2F)).uv(29, 53)
                        .cuboid(-1.0F, -0.5F, -0.5F, 5.0F, 1.0F, 1.0F, new Dilation(-0.4F)),
                ModelTransform.pivot(-1.75F, -0.5F, 0.5F));

        ModelPartData handbrake = bone36.addChild("handbrake", ModelPartBuilder.create().uv(29, 160).cuboid(-3.25F,
                -0.75F, 0.0F, 4.0F, 2.0F, 0.0F, new Dilation(0.0F)),
                ModelTransform.of(4.0F, -5.0F, -2.1F, 0.0F, 0.0F, 0.1745F));

        ModelPartData cube_r183 = handbrake.addChild("cube_r183", ModelPartBuilder.create().uv(55, 155).cuboid(0.5F,
                -4.25F, -0.5F, 2.0F, 1.0F, 1.0F, new Dilation(-0.2F)),
                ModelTransform.of(-4.5855F, 4.6435F, 0.0F, 0.0F, 0.0F, -0.5236F));

        ModelPartData cube_r184 = handbrake.addChild("cube_r184",
                ModelPartBuilder.create().uv(45, 159).cuboid(-6.0F, -3.0F, 0.0F, 3.0F, 1.0F, 0.0F, new Dilation(0.0F)),
                ModelTransform.of(0.8481F, 0.3481F, 0.0F, 0.0F, 0.0F, -0.5236F));

        ModelPartData p_ctrl_4 = controls.addChild("p_ctrl_4", ModelPartBuilder.create(),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -2.618F, 0.0F));

        ModelPartData bone41 = p_ctrl_4.addChild("bone41", ModelPartBuilder.create(),
                ModelTransform.of(19.0F, -20.0F, 0.0F, 0.0F, 0.0F, 2.0071F));

        ModelPartData lever2 = bone41.addChild("lever2",
                ModelPartBuilder.create().uv(134, 18)
                        .cuboid(-2.0F, -2.0F, 1.0F, 4.0F, 4.0F, 1.0F, new Dilation(-0.201F)).uv(147, 0)
                        .cuboid(-1.0F, -1.0F, -1.8F, 2.0F, 2.0F, 1.0F, new Dilation(-0.201F)).uv(138, 0)
                        .cuboid(-1.0F, -1.5F, -0.2F, 2.0F, 3.0F, 2.0F, new Dilation(-0.201F)),
                ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData cube_r185 = lever2
                .addChild(
                        "cube_r185", ModelPartBuilder.create().uv(139, 125).cuboid(-1.0F, -1.0F, -1.0F, 2.0F, 2.0F,
                                2.0F, new Dilation(-0.001F)),
                        ModelTransform.of(0.75F, 0.0F, 0.0F, 0.0F, 0.0F, 0.8727F));

        ModelPartData bone42 = lever2.addChild("bone42",
                ModelPartBuilder.create().uv(41, 12).cuboid(-1.85F, -0.6F, -1.0F, 2.0F, 1.0F, 0.0F, new Dilation(0.0F))
                        .uv(95, 26).cuboid(-1.85F, -0.6F, -1.0F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F)).uv(150, 150)
                        .cuboid(-3.35F, -0.6F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)).uv(36, 16)
                        .cuboid(-3.35F, -0.6F, -0.5F, 2.0F, 1.0F, 0.0F, new Dilation(0.0F)),
                ModelTransform.of(0.1F, 0.1F, 0.0F, 0.0F, 0.0F, -0.9599F));

        ModelPartData bone43 = lever2.addChild("bone43",
                ModelPartBuilder.create().uv(26, 73).cuboid(-1.25F, -0.6F, -0.9F, 1.0F, 1.0F, 0.0F, new Dilation(0.0F))
                        .uv(95, 26).cuboid(-1.25F, -0.6F, -0.9F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F)).uv(150, 150)
                        .cuboid(-2.75F, -0.6F, -0.4F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)).uv(36, 16)
                        .cuboid(-2.75F, -0.6F, -0.4F, 2.0F, 1.0F, 0.0F, new Dilation(0.0F)),
                ModelTransform.of(0.1F, 0.1F, 0.0F, 0.0F, 0.0F, 0.829F));

        ModelPartData pully = bone41.addChild("pully",
                ModelPartBuilder.create().uv(90, 5).cuboid(-2.0F, -1.0F, 0.0F, 4.0F, 1.0F, 1.0F, new Dilation(0.0F))
                        .uv(118, 124).cuboid(2.0F, -0.75F, 0.5F, 10.0F, 12.0F, 0.0F, new Dilation(0.0F)).uv(101, 5)
                        .cuboid(1.75F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, new Dilation(-0.2F)),
                ModelTransform.pivot(7.0F, -4.75F, 2.0F));

        ModelPartData bone47 = pully.addChild("bone47",
                ModelPartBuilder.create().uv(136, 119).cuboid(-2.5F, -0.5F, -0.5F, 2.0F, 1.0F, 1.0F, new Dilation(0.0F))
                        .uv(44, 126).cuboid(-3.0F, -0.5F, -0.5F, 3.0F, 1.0F, 1.0F, new Dilation(-0.2F)).uv(29, 53)
                        .cuboid(-1.0F, -0.5F, -0.5F, 5.0F, 1.0F, 1.0F, new Dilation(-0.4F)),
                ModelTransform.pivot(-1.75F, -0.5F, 0.5F));

        ModelPartData light = bone41.addChild("light",
                ModelPartBuilder.create().uv(149, 135).cuboid(-1.0F, -1.5F, -0.5F, 2.0F, 1.0F, 1.0F, new Dilation(0.0F))
                        .uv(110, 145).cuboid(0.75F, -1.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.4F)),
                ModelTransform.of(-0.25F, 6.25F, 0.0F, 0.0F, 0.0F, -0.2618F));

        ModelPartData bone45 = light.addChild("bone45", ModelPartBuilder.create().uv(21, 149).cuboid(-1.0F, -2.0F,
                -1.0F, 2.0F, 1.0F, 1.0F, new Dilation(0.01F)), ModelTransform.pivot(0.0F, 0.5F, 0.5F));

        ModelPartData bone46 = bone41.addChild("bone46", ModelPartBuilder.create(),
                ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData cube_r186 = bone46.addChild("cube_r186",
                ModelPartBuilder.create().uv(101, 148).cuboid(-1.65F, 8.0F, -1.0F, 1.0F, 1.0F, 2.0F, new Dilation(0.1F))
                        .uv(148, 126).cuboid(-1.65F, 8.0F, -1.0F, 1.0F, 1.0F, 2.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.2182F));

        ModelPartData wires6 = bone41.addChild("wires6", ModelPartBuilder.create(),
                ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData cube_r187 = wires6.addChild("cube_r187",
                ModelPartBuilder.create().uv(29, 12).cuboid(-3.0F, 8.0F, -3.0F, 4.0F, 0.0F, 3.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.3927F));

        ModelPartData p_ctrl_5 = controls.addChild("p_ctrl_5", ModelPartBuilder.create(),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.5708F, 0.0F));

        ModelPartData bone49 = p_ctrl_5.addChild("bone49", ModelPartBuilder.create(),
                ModelTransform.of(19.0F, -20.0F, 0.0F, 0.0F, 0.0F, 2.0071F));

        ModelPartData ring2 = bone49.addChild("ring2", ModelPartBuilder.create().uv(21, 66).cuboid(0.3347F, -1.4957F,
                -1.5F, 0.0F, 3.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.1309F));

        ModelPartData switch30 = ring2.addChild("switch30", ModelPartBuilder.create().uv(144, 119).cuboid(-0.85F, 0.0F,
                -1.0F, 2.0F, 1.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(0.2847F, -1.2457F, 0.0F));

        ModelPartData bone50 = bone49.addChild("bone50", ModelPartBuilder.create(),
                ModelTransform.pivot(0.0F, 0.5F, 0.0F));

        ModelPartData cube_r188 = bone50.addChild("cube_r188",
                ModelPartBuilder.create().uv(101, 148).cuboid(-0.15F, 2.0F, -1.5F, 1.0F, 1.0F, 2.0F, new Dilation(0.1F))
                        .uv(148, 126).cuboid(-0.15F, 2.0F, -1.5F, 1.0F, 1.0F, 2.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.1309F));

        ModelPartData lever3 = bone49.addChild("lever3",
                ModelPartBuilder.create().uv(84, 99).cuboid(-0.5F, -2.0F, -1.0F, 2.0F, 4.0F, 3.0F, new Dilation(0.0F))
                        .uv(42, 19).cuboid(-0.2F, -2.0F, 1.4F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F)).uv(15, 0)
                        .cuboid(-1.5F, -4.0F, -1.0F, 3.0F, 6.0F, 0.0F, new Dilation(0.0F)).uv(85, 38)
                        .cuboid(-1.5F, 2.0F, -1.0F, 3.0F, 0.0F, 3.0F, new Dilation(0.0F)).uv(15, 0)
                        .cuboid(-1.5F, -4.0F, 1.51F, 3.0F, 6.0F, 0.0F, new Dilation(0.0F)),
                ModelTransform.of(0.75F, 9.25F, -0.5F, 0.0F, 0.0F, -0.3054F));

        ModelPartData cube_r189 = lever3
                .addChild(
                        "cube_r189", ModelPartBuilder.create().uv(12, 126).cuboid(-0.2695F, 0.1749F, -1.0F, 2.0F, 0.0F,
                                3.0F, new Dilation(0.0F)),
                        ModelTransform.of(-0.4053F, -2.3064F, 0.0F, 0.0F, 0.0F, -0.6981F));

        ModelPartData meter2 = lever3.addChild("meter2", ModelPartBuilder.create().uv(115, 144).cuboid(-1.0F, -0.75F,
                -1.0F, 2.0F, 1.0F, 2.0F, new Dilation(0.0F)),
                ModelTransform.of(0.55F, -2.15F, 0.25F, 0.0F, 0.0F, -0.6981F));

        ModelPartData bone51 = meter2.addChild("bone51", ModelPartBuilder.create().uv(143, 34).cuboid(-1.25F, -0.25F,
                -0.55F, 2.0F, 1.0F, 2.0F, new Dilation(-0.4F)),
                ModelTransform.of(0.0F, -1.0F, 0.0F, 0.0F, -0.6109F, 0.0F));

        ModelPartData bone52 = lever3.addChild("bone52",
                ModelPartBuilder.create().uv(15, 12).cuboid(-2.7F, -0.5F, 0.0F, 3.0F, 1.0F, 0.0F, new Dilation(0.0F))
                        .uv(85, 38).cuboid(-2.7F, -0.5F, 0.0F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, -1.0F, 1.9F, 0.0F, 0.0F, -0.2182F));

        ModelPartData lights2 = lever3.addChild("lights2", ModelPartBuilder.create(),
                ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData bone53 = lights2.addChild("bone53",
                ModelPartBuilder.create().uv(150, 147).cuboid(0.0F, -1.0F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.2F))
                        .uv(150, 141).cuboid(0.25F, -1.0F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.1F)),
                ModelTransform.pivot(-1.0F, 1.75F, -0.25F));

        ModelPartData bone57 = bone53.addChild("bone57", ModelPartBuilder.create().uv(150, 47).cuboid(0.0F, -1.0F,
                -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.18F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData bone56 = lights2.addChild("bone56",
                ModelPartBuilder.create().uv(150, 147).cuboid(0.0F, -1.0F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.2F))
                        .uv(150, 141).cuboid(0.25F, -1.0F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.1F)),
                ModelTransform.pivot(-1.0F, 0.75F, -0.25F));

        ModelPartData bone60 = bone56.addChild("bone60", ModelPartBuilder.create().uv(150, 47).cuboid(0.0F, -1.0F,
                -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.18F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData bone54 = lights2.addChild("bone54",
                ModelPartBuilder.create().uv(150, 147).cuboid(0.0F, -1.0F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.2F))
                        .uv(150, 141).cuboid(0.25F, -1.0F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.1F)),
                ModelTransform.pivot(-1.0F, 1.75F, 0.75F));

        ModelPartData bone58 = bone54.addChild("bone58", ModelPartBuilder.create().uv(108, 150).cuboid(0.0F, -1.0F,
                -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.18F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData bone55 = lights2.addChild("bone55",
                ModelPartBuilder.create().uv(150, 147).cuboid(0.0F, -1.0F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.2F))
                        .uv(150, 141).cuboid(0.25F, -1.0F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.1F)),
                ModelTransform.pivot(-1.0F, 0.75F, 0.75F));

        ModelPartData bone59 = bone55.addChild("bone59", ModelPartBuilder.create().uv(108, 150).cuboid(0.0F, -1.0F,
                -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.18F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData pully3 = bone49.addChild("pully3",
                ModelPartBuilder.create().uv(5, 156).cuboid(-3.0F, -1.0F, 0.0F, 5.0F, 1.0F, 1.0F, new Dilation(0.0F))
                        .uv(118, 124).cuboid(2.0F, -0.75F, 0.5F, 10.0F, 12.0F, 0.0F, new Dilation(0.0F)).uv(101, 5)
                        .cuboid(1.75F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, new Dilation(-0.2F)),
                ModelTransform.pivot(7.0F, -4.75F, -3.0F));

        ModelPartData bone48 = pully3.addChild("bone48",
                ModelPartBuilder.create().uv(0, 156).cuboid(-1.25F, -1.0F, -0.5F, 1.0F, 2.0F, 1.0F, new Dilation(-0.2F))
                        .uv(29, 53).cuboid(-1.0F, -0.5F, -0.5F, 5.0F, 1.0F, 1.0F, new Dilation(-0.4F)),
                ModelTransform.pivot(-2.75F, -0.5F, 0.5F));

        ModelPartData p_ctrl_6 = controls.addChild("p_ctrl_6", ModelPartBuilder.create(),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -0.5236F, 0.0F));

        ModelPartData bone62 = p_ctrl_6.addChild("bone62", ModelPartBuilder.create(),
                ModelTransform.of(19.0F, -20.0F, 0.0F, 0.0F, 0.0F, 2.0071F));

        ModelPartData bone63 = bone62.addChild("bone63", ModelPartBuilder.create().uv(139, 9).cuboid(-0.75F, -1.0F,
                -1.0F, 2.0F, 2.0F, 2.0F, new Dilation(-0.3F)),
                ModelTransform.of(-0.5F, 1.5F, -0.9F, 0.0F, 0.0F, 0.1745F));

        ModelPartData cube_r190 = bone63.addChild("cube_r190",
                ModelPartBuilder.create().uv(84, 107).cuboid(-0.5F, 0.0F, -1.5F, 2.0F, 0.0F, 3.0F, new Dilation(0.0F)),
                ModelTransform.of(0.25F, 0.0F, 0.0F, 0.7854F, 0.0F, 0.0F));

        ModelPartData cube_r191 = bone63.addChild("cube_r191",
                ModelPartBuilder.create().uv(84, 107).cuboid(-0.5F, 0.0F, -1.5F, 2.0F, 0.0F, 3.0F, new Dilation(0.0F)),
                ModelTransform.of(0.25F, 0.0F, 0.0F, -0.7854F, 0.0F, 0.0F));

        ModelPartData bone64 = bone62.addChild("bone64",
                ModelPartBuilder.create().uv(147, 56).cuboid(-1.0F, -1.5F, -0.5F, 2.0F, 1.0F, 1.0F, new Dilation(0.0F))
                        .uv(110, 145).cuboid(0.25F, -1.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.1F)),
                ModelTransform.of(1.0F, 10.75F, 0.0F, 0.0F, 0.0F, -0.2618F));

        ModelPartData bone65 = bone62.addChild("bone65",
                ModelPartBuilder.create().uv(113, 63).cuboid(-2.0F, -2.0F, -1.0F, 3.0F, 2.0F, 2.0F, new Dilation(0.0F))
                        .uv(68, 57).cuboid(-2.0F, -2.0F, -1.0F, 3.0F, 2.0F, 2.0F, new Dilation(0.1F)),
                ModelTransform.of(0.5F, -2.0F, 0.0F, 0.0F, 0.0F, 0.3054F));

        ModelPartData panel3 = bone62.addChild("panel3",
                ModelPartBuilder.create().uv(9, 146).cuboid(0.0F, -1.5F, 0.0F, 0.0F, 4.0F, 2.0F, new Dilation(0.0F))
                        .uv(84, 19).cuboid(0.0F, -0.75F, -1.25F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F)).uv(80, 19)
                        .cuboid(-0.1F, -0.5F, -1.0F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(0.35F, 0.0F, 0.0F, 0.0F, 0.0F, 0.1309F));

        ModelPartData button46 = panel3.addChild("button46",
                ModelPartBuilder.create().uv(97, 142).cuboid(-0.2F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.pivot(-0.25F, 1.75F, 0.75F));

        ModelPartData bow = bone62.addChild("bow",
                ModelPartBuilder.create().uv(80, 0).cuboid(0.0F, 0.5F, -1.75F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(0.05F, 5.0F, 0.0F, 0.0F, 0.0F, -0.2182F));

        ModelPartData cube_r192 = bow.addChild("cube_r192",
                ModelPartBuilder.create().uv(21, 66).cuboid(0.0F, -0.5F, -3.25F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 1.0F, 1.5F, -0.6109F, 0.0F, 0.0F));

        ModelPartData cube_r193 = bow.addChild("cube_r193",
                ModelPartBuilder.create().uv(25, 66).cuboid(0.0F, -0.5F, -3.25F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 1.0F, 1.5F, 0.6109F, 0.0F, 0.0F));

        ModelPartData cube_r194 = bow.addChild("cube_r194",
                ModelPartBuilder.create().uv(73, 62).cuboid(0.0F, -0.5F, -3.25F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 1.0F, 1.5F, 0.3054F, 0.0F, 0.0F));

        ModelPartData cube_r195 = bow.addChild("cube_r195",
                ModelPartBuilder.create().uv(77, 62).cuboid(0.0F, -0.5F, -3.25F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 1.0F, 1.5F, -0.3054F, 0.0F, 0.0F));

        ModelPartData bone68 = bow.addChild("bone68",
                ModelPartBuilder.create().uv(30, 126)
                        .cuboid(-0.25F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.01F)).uv(106, 145)
                        .cuboid(0.0F, -0.25F, -3.0F, 0.0F, 1.0F, 3.0F, new Dilation(0.0F)).uv(30, 126)
                        .cuboid(-0.25F, -0.5F, -3.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.01F)),
                ModelTransform.of(0.0F, 1.0F, 1.5F, 0.2182F, 0.0F, 0.0F));

        ModelPartData bone69 = bow.addChild("bone69",
                ModelPartBuilder.create().uv(42, 51).cuboid(0.0F, -1.25F, -1.25F, 0.0F, 2.0F, 2.0F, new Dilation(0.0F)),
                ModelTransform.of(0.25F, 2.0F, -0.5F, -0.6109F, 0.0F, 0.0F));

        ModelPartData bone70 = bow.addChild("bone70",
                ModelPartBuilder.create().uv(42, 51).cuboid(0.0F, -1.25F, -1.25F, 0.0F, 2.0F, 2.0F, new Dilation(0.0F)),
                ModelTransform.of(0.25F, 0.75F, 0.9F, 0.2182F, 0.0F, 0.0F));

        ModelPartData bone73 = bow.addChild("bone73", ModelPartBuilder.create(),
                ModelTransform.of(0.25F, 0.0F, -0.85F, 1.0472F, 0.0F, 0.0F));

        ModelPartData cube_r196 = bone73.addChild("cube_r196",
                ModelPartBuilder.create().uv(42, 51).cuboid(0.0F, -1.25F, -1.25F, 0.0F, 2.0F, 2.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.829F, 0.0F, 0.0F));

        ModelPartData bone71 = bow.addChild("bone71",
                ModelPartBuilder.create().uv(29, 12).cuboid(0.0F, -0.5F, -0.5F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(0.15F, 0.25F, -0.1F, 0.4363F, 0.0F, 0.0F));

        ModelPartData bone72 = bow.addChild("bone72",
                ModelPartBuilder.create().uv(29, 12).cuboid(0.0F, -0.5F, -0.5F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(0.15F, 1.75F, 0.25F, -0.2618F, 0.0F, 0.0F));

        ModelPartData wires7 = bone62.addChild("wires7", ModelPartBuilder.create(),
                ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData cube_r197 = wires7.addChild("cube_r197",
                ModelPartBuilder.create().uv(80, 19).cuboid(-3.5F, 0.25F, 5.0F, 0.0F, 2.0F, 7.0F, new Dilation(0.0F)),
                ModelTransform.of(-0.019F, 0.5F, -0.1021F, -0.0496F, 0.7779F, 0.0956F));

        ModelPartData cube_r198 = wires7.addChild("cube_r198",
                ModelPartBuilder.create().uv(88, 19).cuboid(0.0F, 0.25F, 2.0F, 0.0F, 2.0F, 4.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.5F, 0.0F, -0.0359F, 0.1676F, 0.1245F));

        ModelPartData cube_r199 = wires7.addChild("cube_r199", ModelPartBuilder.create().uv(107, 113).cuboid(-1.0F,
                -0.1F, 0.8F, 5.0F, 0.0F, 10.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, -0.48F, 0.0F, 0.0F));

        ModelPartData hammer = bone62.addChild("hammer", ModelPartBuilder.create().uv(77, 148).cuboid(4.0F, -3.0F, -3.25F, 1.0F, 1.0F, 2.0F, new Dilation(-0.3F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.4363F));

        ModelPartData bone40 = hammer.addChild("bone40", ModelPartBuilder.create().uv(29, 50).cuboid(-0.5F, -0.5F, -0.5F, 6.0F, 1.0F, 1.0F, new Dilation(-0.1F))
                .uv(80, 47).cuboid(5.25F, -1.5F, -1.0F, 2.0F, 3.0F, 2.0F, new Dilation(0.0F))
                .uv(12, 130).cuboid(-2.7F, -1.25F, 0.0F, 3.0F, 2.0F, 0.0F, new Dilation(0.0F)), ModelTransform.pivot(6.5F, -2.5F, -2.5F));

        ModelPartData handbrake2 = bone62.addChild("handbrake2", ModelPartBuilder.create(),
                ModelTransform.of(4.25F, -3.65F, 1.9F, 0.0F, 0.0F, -0.4363F));

        ModelPartData cube_r200 = handbrake2.addChild("cube_r200", ModelPartBuilder.create().uv(18, 155).cuboid(-0.9F,
                -1.5F, -1.2F, 2.0F, 1.0F, 2.0F, new Dilation(-0.1F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -0.7854F, 0.0F));

        ModelPartData bone102 = handbrake2.addChild("bone102", ModelPartBuilder.create().uv(38, 156).cuboid(-2.7071F,
                -0.5F, -0.7071F, 3.0F, 1.0F, 1.0F, new Dilation(0.05F)),
                ModelTransform.of(1.0F, -1.0F, 0.0F, 0.0F, 0.829F, 0.0F));

        ModelPartData cube_r201 = bone102.addChild("cube_r201",
                ModelPartBuilder.create().uv(27, 157).cuboid(-5.0F, -0.5F, 0.4F, 4.0F, 1.0F, 1.0F, new Dilation(-0.2F)),
                ModelTransform.of(-0.7071F, 0.0F, -0.7071F, 0.0F, -0.2182F, 0.0F));

        ModelPartData roof = console.addChild("roof",
                ModelPartBuilder.create().uv(113, 56)
                        .cuboid(-10.75F, -29.75F, -6.0F, 5.0F, 1.0F, 12.0F, new Dilation(0.5F)).uv(84, 99)
                        .cuboid(-11.0F, -33.0F, -6.0F, 11.0F, 1.0F, 12.0F, new Dilation(0.81F)).uv(113, 56)
                        .cuboid(-10.4F, -27.0F, -6.0F, 5.0F, 1.0F, 12.0F, new Dilation(0.0F)).uv(128, 107)
                        .cuboid(-6.65F, -31.25F, -3.5F, 2.0F, 4.0F, 7.0F, new Dilation(0.8F)),
                ModelTransform.pivot(0.0F, -48.25F, 0.0F));

        ModelPartData top2 = roof.addChild("top2",
                ModelPartBuilder.create().uv(113, 56)
                        .cuboid(-10.75F, -29.75F, -6.0F, 5.0F, 1.0F, 12.0F, new Dilation(0.5F)).uv(84, 99)
                        .cuboid(-11.0F, -33.0F, -6.0F, 11.0F, 1.0F, 12.0F, new Dilation(0.81F)).uv(113, 56)
                        .cuboid(-10.4F, -27.0F, -6.0F, 5.0F, 1.0F, 12.0F, new Dilation(0.0F)).uv(128, 107)
                        .cuboid(-6.65F, -31.25F, -3.5F, 2.0F, 4.0F, 7.0F, new Dilation(0.8F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData top3 = top2.addChild("top3",
                ModelPartBuilder.create().uv(113, 56)
                        .cuboid(-10.75F, -29.75F, -6.0F, 5.0F, 1.0F, 12.0F, new Dilation(0.5F)).uv(84, 99)
                        .cuboid(-11.0F, -33.0F, -6.0F, 11.0F, 1.0F, 12.0F, new Dilation(0.81F)).uv(113, 56)
                        .cuboid(-10.4F, -27.0F, -6.0F, 5.0F, 1.0F, 12.0F, new Dilation(0.0F)).uv(128, 107)
                        .cuboid(-6.65F, -31.25F, -3.5F, 2.0F, 4.0F, 7.0F, new Dilation(0.8F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData top4 = top3.addChild("top4",
                ModelPartBuilder.create().uv(113, 56)
                        .cuboid(-10.75F, -29.75F, -6.0F, 5.0F, 1.0F, 12.0F, new Dilation(0.5F)).uv(84, 99)
                        .cuboid(-11.0F, -33.0F, -6.0F, 11.0F, 1.0F, 12.0F, new Dilation(0.81F)).uv(113, 56)
                        .cuboid(-10.4F, -27.0F, -6.0F, 5.0F, 1.0F, 12.0F, new Dilation(0.0F)).uv(128, 107)
                        .cuboid(-6.65F, -31.25F, -3.5F, 2.0F, 4.0F, 7.0F, new Dilation(0.8F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData top5 = top4.addChild("top5",
                ModelPartBuilder.create().uv(113, 56)
                        .cuboid(-10.75F, -29.75F, -6.0F, 5.0F, 1.0F, 12.0F, new Dilation(0.5F)).uv(84, 99)
                        .cuboid(-11.0F, -33.0F, -6.0F, 11.0F, 1.0F, 12.0F, new Dilation(0.81F)).uv(113, 56)
                        .cuboid(-10.4F, -27.0F, -6.0F, 5.0F, 1.0F, 12.0F, new Dilation(0.0F)).uv(128, 107)
                        .cuboid(-6.65F, -31.25F, -3.5F, 2.0F, 4.0F, 7.0F, new Dilation(0.8F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData top6 = top5.addChild("top6",
                ModelPartBuilder.create().uv(113, 56)
                        .cuboid(-10.75F, -29.75F, -6.0F, 5.0F, 1.0F, 12.0F, new Dilation(0.5F)).uv(84, 99)
                        .cuboid(-11.0F, -33.0F, -6.0F, 11.0F, 1.0F, 12.0F, new Dilation(0.8F)).uv(113, 56)
                        .cuboid(-10.4F, -27.0F, -6.0F, 5.0F, 1.0F, 12.0F, new Dilation(0.0F)).uv(128, 107)
                        .cuboid(-6.65F, -31.25F, -3.5F, 2.0F, 4.0F, 7.0F, new Dilation(0.8F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData wires8 = roof.addChild("wires8", ModelPartBuilder.create(),
                ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData cube_r202 = wires8.addChild("cube_r202",
                ModelPartBuilder.create().uv(77, 22).cuboid(21.25F, -2.5F, -1.5F, 3.0F, 0.0F, 3.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0556F, -0.1186F, -0.8323F));

        ModelPartData cube_r203 = wires8.addChild("cube_r203", ModelPartBuilder.create().uv(0, 76).cuboid(-8.0F, -28.0F,
                -8.0F, 17.0F, 49.0F, 0.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 2.618F, 0.0F));

        ModelPartData cube_r204 = wires8.addChild("cube_r204", ModelPartBuilder.create().uv(0, 76).cuboid(-8.0F, -28.0F,
                -8.0F, 17.0F, 49.0F, 0.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -0.3491F, 0.0F));

        ModelPartData cube_r205 = wires8.addChild("cube_r205",
                ModelPartBuilder.create().uv(0, 0).cuboid(0.0F, -32.4F, 6.0F, 0.0F, 51.0F, 14.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -2.7053F, 0.0F));

        ModelPartData cube_r206 = wires8.addChild("cube_r206",
                ModelPartBuilder.create().uv(0, 0).cuboid(0.0F, -32.5F, 6.0F, 0.0F, 51.0F, 14.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.3963F, 0.0F));

        ModelPartData cube_r207 = wires8.addChild("cube_r207",
                ModelPartBuilder.create().uv(0, 0).cuboid(0.0F, -32.5F, 6.0F, 0.0F, 51.0F, 14.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -0.2618F, 0.0F));

        ModelPartData top = console.addChild("top",
                ModelPartBuilder.create().uv(0, 127).cuboid(-6.05F, -29.0F, -3.5F, 2.0F, 6.0F, 7.0F, new Dilation(0.0F))
                        .uv(115, 30).cuboid(-8.8F, -29.0F, -5.0F, 3.0F, 1.0F, 10.0F, new Dilation(0.2F)).uv(115, 30)
                        .cuboid(-8.65F, -31.0F, -5.0F, 3.0F, 1.0F, 10.0F, new Dilation(0.0F)),
                ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData top8 = top.addChild("top8",
                ModelPartBuilder.create().uv(0, 127).cuboid(-6.05F, -29.0F, -3.5F, 2.0F, 6.0F, 7.0F, new Dilation(0.0F))
                        .uv(115, 30).cuboid(-8.8F, -29.0F, -5.0F, 3.0F, 1.0F, 10.0F, new Dilation(0.2F)).uv(115, 30)
                        .cuboid(-8.65F, -31.0F, -5.0F, 3.0F, 1.0F, 10.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData top9 = top8.addChild("top9",
                ModelPartBuilder.create().uv(0, 127).cuboid(-6.05F, -29.0F, -3.5F, 2.0F, 6.0F, 7.0F, new Dilation(0.0F))
                        .uv(115, 30).cuboid(-8.8F, -29.0F, -5.0F, 3.0F, 1.0F, 10.0F, new Dilation(0.2F)).uv(115, 30)
                        .cuboid(-8.65F, -31.0F, -5.0F, 3.0F, 1.0F, 10.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData top10 = top9.addChild("top10",
                ModelPartBuilder.create().uv(0, 127).cuboid(-6.05F, -29.0F, -3.5F, 2.0F, 6.0F, 7.0F, new Dilation(0.0F))
                        .uv(115, 30).cuboid(-8.8F, -29.0F, -5.0F, 3.0F, 1.0F, 10.0F, new Dilation(0.2F)).uv(115, 30)
                        .cuboid(-8.65F, -31.0F, -5.0F, 3.0F, 1.0F, 10.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData top11 = top10.addChild("top11",
                ModelPartBuilder.create().uv(0, 127).cuboid(-6.05F, -29.0F, -3.5F, 2.0F, 6.0F, 7.0F, new Dilation(0.0F))
                        .uv(115, 30).cuboid(-8.8F, -29.0F, -5.0F, 3.0F, 1.0F, 10.0F, new Dilation(0.2F)).uv(115, 30)
                        .cuboid(-8.65F, -31.0F, -5.0F, 3.0F, 1.0F, 10.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData top12 = top11.addChild("top12",
                ModelPartBuilder.create().uv(0, 127).cuboid(-6.05F, -29.0F, -3.5F, 2.0F, 6.0F, 7.0F, new Dilation(0.0F))
                        .uv(115, 30).cuboid(-8.8F, -29.0F, -5.0F, 3.0F, 1.0F, 10.0F, new Dilation(0.2F)).uv(115, 30)
                        .cuboid(-8.65F, -31.0F, -5.0F, 3.0F, 1.0F, 10.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData monitor = top.addChild("monitor", ModelPartBuilder.create(),
                ModelTransform.of(0.0F, 0.15F, 0.0F, 0.0F, -0.5236F, 0.0F));

        ModelPartData bone75 = monitor.addChild("bone75",
                ModelPartBuilder.create().uv(113, 70).cuboid(-4.5F, 4.0F, 2.0F, 9.0F, 1.0F, 3.0F, new Dilation(0.0F))
                        .uv(0, 146).cuboid(1.5F, 3.25F, 2.75F, 3.0F, 1.0F, 1.0F, new Dilation(-0.2F)).uv(57, 126)
                        .cuboid(1.5F, 3.25F, 2.75F, 3.0F, 1.0F, 1.0F, new Dilation(-0.1F)).uv(80, 0)
                        .cuboid(-4.5F, -4.0F, 4.0F, 9.0F, 1.0F, 3.0F, new Dilation(0.0F)).uv(0, 66)
                        .cuboid(-4.5F, -3.0F, 4.0F, 9.0F, 7.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, -27.0F, -17.0F, -0.2182F, 0.0F, 0.0F));

        ModelPartData cube_r208 = bone75.addChild("cube_r208",
                ModelPartBuilder.create().uv(146, 123).cuboid(2.0F, 3.5F, 2.5F, 2.0F, 0.0F, 2.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, -0.1529F, -1.4052F, 1.0036F, 0.0F, 0.0F));

        ModelPartData cube_r209 = bone75.addChild("cube_r209",
                ModelPartBuilder.create().uv(145, 4).cuboid(2.0F, 3.5F, 2.5F, 2.0F, 0.0F, 2.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.5821F, 0.5771F, 1.6144F, 0.0F, 0.0F));

        ModelPartData cube_r210 = bone75.addChild("cube_r210",
                ModelPartBuilder.create().uv(145, 59).cuboid(-4.0F, 4.25F, 0.0F, 2.0F, 0.0F, 2.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.5821F, 0.2771F, 0.7854F, 0.0F, 0.0F));

        ModelPartData cube_r211 = bone75.addChild("cube_r211",
                ModelPartBuilder.create().uv(145, 64).cuboid(-4.0F, 4.25F, 0.0F, 2.0F, 0.0F, 2.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.25F, 0.2618F, 0.0F, 0.0F));

        ModelPartData screen = bone75.addChild("screen", ModelPartBuilder.create(),
                ModelTransform.pivot(0.0F, 27.0F, 17.0F));

        ModelPartData bone66 = screen.addChild("bone66",
                ModelPartBuilder.create().uv(87, 29).cuboid(-2.25F, -2.25F, 0.0F, 5.0F, 5.0F, 0.0F, new Dilation(0.0F)),
                ModelTransform.of(0.75F, -26.75F, -13.2F, 0.0F, 0.0F, -0.2618F));

        ModelPartData bone67 = screen.addChild("bone67",
                ModelPartBuilder.create().uv(118, 5).cuboid(-0.75F, -0.75F, 0.0F, 2.0F, 2.0F, 0.0F, new Dilation(0.0F)),
                ModelTransform.of(-2.25F, -28.25F, -13.1F, 0.0F, 0.0F, 1.8326F));

        ModelPartData bone74 = screen.addChild("bone74", ModelPartBuilder.create().uv(97, 113).cuboid(-0.75F, -0.75F,
                0.0F, 2.0F, 2.0F, 0.0F, new Dilation(0.0F)),
                ModelTransform.of(-2.25F, -25.75F, -13.1F, 0.0F, 0.0F, 1.6581F));

        ModelPartData bone116 = screen.addChild("bone116",
                ModelPartBuilder.create().uv(0, 160).cuboid(-3.5F, 2.0F, 3.95F, 2.0F, 1.0F, 0.0F, new Dilation(0.0F)),
                ModelTransform.pivot(0.0F, -27.0F, -17.0F));

        ModelPartData bone76 = monitor.addChild("bone76", ModelPartBuilder.create().uv(65, 145).cuboid(0.0F, -0.75F,
                -2.75F, 0.0F, 1.0F, 3.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, -26.75F, -10.0F, -0.8727F, 0.0F, 0.0F));

        ModelPartData bone77 = monitor.addChild("bone77", ModelPartBuilder.create().uv(113, 56).cuboid(-0.1F, -0.75F,
                -0.25F, 0.0F, 1.0F, 5.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, -26.75F, -10.0F, -0.2182F, 0.0F, 0.0F));

        ModelPartData rotor = console.addChild("rotor", ModelPartBuilder.create(),
                ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData top7 = rotor.addChild("top7", ModelPartBuilder.create(),
                ModelTransform.pivot(0.0F, -17.0F, 0.0F));

        ModelPartData bone103 = top7.addChild("bone103", ModelPartBuilder.create().uv(115, 0).cuboid(-6.9F, -31.0F,
                -4.0F, 7.0F, 0.0F, 8.0F, new Dilation(0.005F)), ModelTransform.pivot(0.0F, -25.0F, 0.0F));

        ModelPartData bone104 = bone103.addChild("bone104", ModelPartBuilder.create().uv(115, 0).cuboid(-6.9F, -31.0F,
                -4.0F, 7.0F, 0.0F, 8.0F, new Dilation(0.006F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData bone105 = bone104.addChild("bone105", ModelPartBuilder.create().uv(115, 0).cuboid(-6.9F, -31.0F,
                -4.0F, 7.0F, 0.0F, 8.0F, new Dilation(0.005F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData bone106 = bone105.addChild("bone106", ModelPartBuilder.create().uv(115, 0).cuboid(-6.9F, -31.0F,
                -4.0F, 7.0F, 0.0F, 8.0F, new Dilation(0.006F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData bone107 = bone106.addChild("bone107", ModelPartBuilder.create().uv(115, 0).cuboid(-6.9F, -31.0F,
                -4.0F, 7.0F, 0.0F, 8.0F, new Dilation(0.005F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData bone108 = bone107.addChild("bone108", ModelPartBuilder.create().uv(115, 0).cuboid(-6.9F, -31.0F,
                -4.0F, 7.0F, 0.0F, 8.0F, new Dilation(0.006F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData bone110 = top7.addChild("bone110", ModelPartBuilder.create().uv(115, 0).cuboid(-6.9F, -31.0F,
                -4.0F, 7.0F, 0.0F, 8.0F, new Dilation(0.005F)), ModelTransform.pivot(0.0F, -20.0F, 0.0F));

        ModelPartData bone111 = bone110.addChild("bone111", ModelPartBuilder.create().uv(115, 0).cuboid(-6.9F, -31.0F,
                -4.0F, 7.0F, 0.0F, 8.0F, new Dilation(0.006F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData bone112 = bone111.addChild("bone112", ModelPartBuilder.create().uv(115, 0).cuboid(-6.9F, -31.0F,
                -4.0F, 7.0F, 0.0F, 8.0F, new Dilation(0.005F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData bone113 = bone112.addChild("bone113", ModelPartBuilder.create().uv(115, 0).cuboid(-6.9F, -31.0F,
                -4.0F, 7.0F, 0.0F, 8.0F, new Dilation(0.006F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData bone114 = bone113.addChild("bone114", ModelPartBuilder.create().uv(115, 0).cuboid(-6.9F, -31.0F,
                -4.0F, 7.0F, 0.0F, 8.0F, new Dilation(0.005F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData bone115 = bone114.addChild("bone115", ModelPartBuilder.create().uv(115, 0).cuboid(-6.9F, -31.0F,
                -4.0F, 7.0F, 0.0F, 8.0F, new Dilation(0.006F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData bone84 = top7.addChild("bone84", ModelPartBuilder.create().uv(35, 119).cuboid(3.0F, -59.0F, -1.0F,
                2.0F, 28.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData bone85 = bone84.addChild("bone85", ModelPartBuilder.create().uv(35, 119).cuboid(3.0F, -59.0F,
                -1.0F, 2.0F, 28.0F, 2.0F, new Dilation(-0.1F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData bone86 = bone85.addChild("bone86", ModelPartBuilder.create().uv(35, 119).cuboid(3.0F, -59.0F,
                -1.0F, 2.0F, 28.0F, 2.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData bone87 = bone86.addChild("bone87", ModelPartBuilder.create().uv(35, 119).cuboid(3.0F, -59.0F,
                -1.0F, 2.0F, 28.0F, 2.0F, new Dilation(-0.1F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData bone88 = bone87.addChild("bone88", ModelPartBuilder.create().uv(35, 119).cuboid(3.0F, -59.0F,
                -1.0F, 2.0F, 28.0F, 2.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData bone89 = bone88.addChild("bone89", ModelPartBuilder.create().uv(35, 119).cuboid(3.0F, -59.0F,
                -1.0F, 2.0F, 28.0F, 2.0F, new Dilation(-0.1F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData bottom = rotor.addChild("bottom", ModelPartBuilder.create(),
                ModelTransform.pivot(0.0F, -2.0F, 0.0F));

        ModelPartData bone117 = bottom.addChild("bone117",
                ModelPartBuilder.create().uv(115, 0).cuboid(-6.9F, 3.0F, -4.0F, 7.0F, 0.0F, 8.0F, new Dilation(0.005F)),
                ModelTransform.pivot(0.0F, -38.0F, 0.0F));

        ModelPartData bone118 = bone117.addChild("bone118",
                ModelPartBuilder.create().uv(115, 0).cuboid(-6.9F, 3.0F, -4.0F, 7.0F, 0.0F, 8.0F, new Dilation(0.006F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData bone119 = bone118.addChild("bone119",
                ModelPartBuilder.create().uv(115, 0).cuboid(-6.9F, 3.0F, -4.0F, 7.0F, 0.0F, 8.0F, new Dilation(0.005F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData bone120 = bone119.addChild("bone120",
                ModelPartBuilder.create().uv(115, 0).cuboid(-6.9F, 3.0F, -4.0F, 7.0F, 0.0F, 8.0F, new Dilation(0.006F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData bone121 = bone120.addChild("bone121",
                ModelPartBuilder.create().uv(115, 0).cuboid(-6.9F, 3.0F, -4.0F, 7.0F, 0.0F, 8.0F, new Dilation(0.005F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData bone122 = bone121.addChild("bone122",
                ModelPartBuilder.create().uv(115, 0).cuboid(-6.9F, 3.0F, -4.0F, 7.0F, 0.0F, 8.0F, new Dilation(0.006F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData bone90 = bottom.addChild("bone90",
                ModelPartBuilder.create().uv(115, 0).cuboid(-6.9F, 3.0F, -4.0F, 7.0F, 0.0F, 8.0F, new Dilation(0.005F)),
                ModelTransform.pivot(0.0F, -33.0F, 0.0F));

        ModelPartData bone91 = bone90.addChild("bone91",
                ModelPartBuilder.create().uv(115, 0).cuboid(-6.9F, 3.0F, -4.0F, 7.0F, 0.0F, 8.0F, new Dilation(0.006F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData bone92 = bone91.addChild("bone92",
                ModelPartBuilder.create().uv(115, 0).cuboid(-6.9F, 3.0F, -4.0F, 7.0F, 0.0F, 8.0F, new Dilation(0.005F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData bone93 = bone92.addChild("bone93",
                ModelPartBuilder.create().uv(115, 0).cuboid(-6.9F, 3.0F, -4.0F, 7.0F, 0.0F, 8.0F, new Dilation(0.006F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData bone94 = bone93.addChild("bone94",
                ModelPartBuilder.create().uv(115, 0).cuboid(-6.9F, 3.0F, -4.0F, 7.0F, 0.0F, 8.0F, new Dilation(0.005F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData bone95 = bone94.addChild("bone95",
                ModelPartBuilder.create().uv(115, 0).cuboid(-6.9F, 3.0F, -4.0F, 7.0F, 0.0F, 8.0F, new Dilation(0.006F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData bone78 = bottom.addChild("bone78", ModelPartBuilder.create().uv(35, 119).cuboid(3.0F, -55.0F,
                -1.0F, 2.0F, 28.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData bone79 = bone78.addChild("bone79", ModelPartBuilder.create().uv(35, 119).cuboid(3.0F, -55.0F,
                -1.0F, 2.0F, 28.0F, 2.0F, new Dilation(-0.1F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData bone80 = bone79.addChild("bone80", ModelPartBuilder.create().uv(35, 119).cuboid(3.0F, -55.0F,
                -1.0F, 2.0F, 28.0F, 2.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData bone81 = bone80.addChild("bone81", ModelPartBuilder.create().uv(35, 119).cuboid(3.0F, -55.0F,
                -1.0F, 2.0F, 28.0F, 2.0F, new Dilation(-0.1F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData bone82 = bone81.addChild("bone82", ModelPartBuilder.create().uv(35, 119).cuboid(3.0F, -55.0F,
                -1.0F, 2.0F, 28.0F, 2.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData bone83 = bone82.addChild("bone83", ModelPartBuilder.create().uv(35, 119).cuboid(3.0F, -55.0F,
                -1.0F, 2.0F, 28.0F, 2.0F, new Dilation(-0.1F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData glass = console.addChild("glass", ModelPartBuilder.create().uv(98, 0).cuboid(-6.95F, -75.0F,
                -4.0F, 0.0F, 45.0F, 8.0F, new Dilation(0.05F)), ModelTransform.pivot(0.0F, 0.75F, 0.0F));

        ModelPartData cube_r212 = glass.addChild("cube_r212", ModelPartBuilder.create().uv(84, 113).cuboid(-10.9F,
                -75.0F, 0.0F, 3.0F, 45.0F, 0.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, -0.5F, 0.0F, 0.0F, 0.5236F, 0.0F));

        ModelPartData glass2 = glass.addChild("glass2", ModelPartBuilder.create().uv(98, 0).cuboid(-6.95F, -75.0F,
                -4.0F, 0.0F, 45.0F, 8.0F, new Dilation(0.05F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData cube_r213 = glass2.addChild("cube_r213", ModelPartBuilder.create().uv(84, 113).cuboid(-10.9F,
                -75.5F, 0.0F, 3.0F, 45.0F, 0.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.5236F, 0.0F));

        ModelPartData glass3 = glass2.addChild("glass3", ModelPartBuilder.create().uv(98, 0).cuboid(-6.95F, -75.0F,
                -4.0F, 0.0F, 45.0F, 8.0F, new Dilation(0.05F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData cube_r214 = glass3.addChild("cube_r214", ModelPartBuilder.create().uv(84, 113).cuboid(-10.9F,
                -75.5F, 0.0F, 3.0F, 45.0F, 0.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.5236F, 0.0F));

        ModelPartData glass4 = glass3.addChild("glass4", ModelPartBuilder.create().uv(98, 0).cuboid(-6.95F, -75.0F,
                -4.0F, 0.0F, 45.0F, 8.0F, new Dilation(0.05F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData cube_r215 = glass4.addChild("cube_r215", ModelPartBuilder.create().uv(84, 113).cuboid(-10.9F,
                -75.5F, 0.0F, 3.0F, 45.0F, 0.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.5236F, 0.0F));

        ModelPartData glass5 = glass4.addChild("glass5", ModelPartBuilder.create().uv(98, 0).cuboid(-6.95F, -75.0F,
                -4.0F, 0.0F, 45.0F, 8.0F, new Dilation(0.05F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData cube_r216 = glass5.addChild("cube_r216", ModelPartBuilder.create().uv(84, 113).cuboid(-10.9F,
                -75.5F, 0.0F, 3.0F, 45.0F, 0.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.5236F, 0.0F));

        ModelPartData glass6 = glass5.addChild("glass6", ModelPartBuilder.create().uv(98, 0).cuboid(-6.95F, -75.0F,
                -4.0F, 0.0F, 45.0F, 8.0F, new Dilation(0.05F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData cube_r217 = glass6.addChild("cube_r217", ModelPartBuilder.create().uv(84, 113).cuboid(-10.9F,
                -75.5F, 0.0F, 3.0F, 45.0F, 0.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.5236F, 0.0F));

        ModelPartData rings = console.addChild("rings", ModelPartBuilder.create().uv(58, 145).cuboid(-7.25F, -26.0F,
                -1.0F, 1.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.1745F, 0.0F));

        ModelPartData rings2 = rings.addChild("rings2", ModelPartBuilder.create().uv(58, 145).cuboid(-7.25F, -26.0F,
                -1.0F, 1.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.4363F, 0.0F));

        ModelPartData rings3 = rings2.addChild("rings3", ModelPartBuilder.create().uv(58, 145).cuboid(-7.25F, -26.0F,
                -1.0F, 1.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.4363F, 0.0F));

        ModelPartData rings4 = rings3.addChild("rings4", ModelPartBuilder.create().uv(58, 145).cuboid(-7.25F, -26.0F,
                -1.0F, 1.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.4363F, 0.0F));

        ModelPartData rings5 = rings4.addChild("rings5", ModelPartBuilder.create().uv(58, 145).cuboid(-7.25F, -26.0F,
                -1.0F, 1.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.4363F, 0.0F));
        return TexturedModelData.of(modelData, 256, 256);
    }

    @Override
    public Animation getAnimationForState(TravelHandlerBase.State state) {
        return switch (state) {
            default -> CoralAnimations.CORAL_CONSOLE_INFLIGHT_ANIMATION;
            case MAT -> CoralAnimations.CORAL_CONSOLE_REMAT_ANIMATION;
            case DEMAT -> CoralAnimations.CORAL_CONSOLE_DEMAT_ANIMATION;
            case LANDED -> CoralAnimations.CONSOLE_CORAL_IDLE_ANIMATION;
        };
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red,
            float green, float blue, float alpha) {
        console.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
    }

    @Override
    public void renderWithAnimations(ConsoleBlockEntity console, ClientTardis tardis, ModelPart root, MatrixStack matrices,
                                     VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float pAlpha) {
        matrices.push();
        matrices.translate(0.5f, -1.5f, -0.5f);

        ModelPart controls = this.console.getChild("controls");

        this.console.getChild("rotor").getChild("top7").visible = !console.getVariant()
                .equals(ConsoleVariantRegistry.CORAL_WHITE);

        // Fuel Gauge
        controls.getChild("ctrl_1").getChild("bone13").getChild("compass")
                .getChild("needle").pitch = -(float) (((tardis.getFuel() / FuelHandler.TARDIS_MAX_FUEL) * 2) - 1);

        ModelPart fuelLowWarningLight = controls.getChild("p_ctrl_4").getChild("bone41").getChild("light")
                .getChild("bone45");
        // Low Fuel Light
        fuelLowWarningLight.visible = (tardis.getFuel() <= (FuelHandler.TARDIS_MAX_FUEL / 10));

        // Anti-gravs Lever
        controls.getChild("p_ctrl_1").getChild("bone29").getChild("lever").getChild("bone8").roll = tardis.travel()
                .antigravs().get() ? 0.829F : 0.829F - 1.5f;

        // Door Control
        ModelPart doorControl = controls.getChild("p_ctrl_1").getChild("bone29").getChild("crank").getChild("bone32");

        if (tardis.door().isLeftOpen()) {
            doorControl.pitch = -0.6981F - 0.8f;
        } else if (tardis.door().isRightOpen()) {
            doorControl.pitch = -0.6981F - 1.5f;
        }

        // Power Lever
        controls.getChild("p_ctrl_4").getChild("bone41").getChild("lever2").getChild("bone43").roll = tardis.fuel()
                .hasPower()
                        ? controls.getChild("p_ctrl_4").getChild("bone41").getChild("lever2").getChild("bone43").roll
                        : controls.getChild("p_ctrl_4").getChild("bone41").getChild("lever2").getChild("bone43").roll
                                - 1.5f;
        controls.getChild("p_ctrl_4").getChild("bone41").getChild("lever2").getChild("bone42").roll = tardis.fuel()
                .hasPower()
                        ? controls.getChild("p_ctrl_4").getChild("bone41").getChild("lever2").getChild("bone42").roll
                        : controls.getChild("p_ctrl_4").getChild("bone41").getChild("lever2").getChild("bone42").roll
                                + 0.5f;

        // Throttle
        ModelPart throttle = controls.getChild("p_ctrl_5").getChild("bone49").getChild("lever3").getChild("bone52");
        throttle.roll = throttle.roll + (tardis.travel().speed() / (float) tardis.travel().maxSpeed().get());

        // Increment
        ModelPart increment = controls.getChild("p_ctrl_2").getChild("bone33").getChild("bone31").getChild("crank2");
        ModelPart incrementTwo = controls.getChild("p_ctrl_2").getChild("bone33").getChild("bone31").getChild("bone34");

        increment.yaw = IncrementManager.increment(tardis) >= 10
                ? IncrementManager.increment(tardis) >= 100
                        ? IncrementManager.increment(tardis) >= 1000
                                ? IncrementManager.increment(tardis) >= 10000
                                        ? increment.yaw + 1.5f
                                        : increment.yaw + 1.25f
                                : increment.yaw + 1f
                        : increment.yaw + 0.5f
                : increment.yaw;
        incrementTwo.pivotY = IncrementManager.increment(tardis) >= 10
                ? IncrementManager.increment(tardis) >= 100
                        ? IncrementManager.increment(tardis) >= 1000
                                ? IncrementManager.increment(tardis) >= 10000
                                        ? incrementTwo.pivotY + 3f
                                        : incrementTwo.pivotY + 2f
                                : incrementTwo.pivotY + 1f
                        : incrementTwo.pivotY + 0.5f
                : incrementTwo.pivotY;

        // Refueler
        ModelPart refueler = controls.getChild("p_ctrl_5").getChild("bone49").getChild("ring2").getChild("switch30");
        refueler.pivotY = tardis.isRefueling() ? refueler.pivotY + 1 : refueler.pivotY;

        // Waypoint
        controls.getChild("ctrl_1").getChild("bone13").getChild("insert").getChild("bone96").visible = tardis
                .<WaypointHandler>handler(TardisComponent.Id.WAYPOINTS).hasCartridge();

        // Handbrake
        controls.getChild("p_ctrl_6").getChild("bone62").getChild("handbrake2")
                .getChild("bone102").yaw = !tardis.travel().handbrake() ? 0.829F : 0.829F + 0.75f;

        // Siege Mode
        ModelPart siege = controls.getChild("p_ctrl_3").getChild("bone36").getChild("handbrake");
        siege.roll = tardis.siege().isActive() ? siege.roll + 0.45f : siege.roll;

        // Shields
        ModelPart shield = controls.getChild("p_ctrl_4").getChild("bone41").getChild("pully").getChild("bone47");
        shield.pivotX = tardis.shields().shielded().get()
                ? shield.pivotX - 1
                : shield.pivotX;

        // Autopilot
        ModelPart autopilot = controls.getChild("ctrl_4").getChild("bone15").getChild("switch24").getChild("bone19");
        autopilot.pivotY = tardis.travel().autopilot() ? autopilot.pivotY + 1 : autopilot.pivotY;

        ModelPart security = controls.getChild("ctrl_4").getChild("bone15").getChild("switch25").getChild("bone20");
        security.pivotY = tardis.stats().security().get() ? security.pivotY + 1 : security.pivotY;

        // Ground Searching
        ModelPart groundSearch = controls.getChild("p_ctrl_6").getChild("bone62").getChild("bow").getChild("bone68");
        groundSearch.pitch = tardis.travel().horizontalSearch().get() ? 0.2182F - 0.5f : 0.2182F;

        // Hammer
        ModelPart hammer = controls.getChild("p_ctrl_6").getChild("bone62").getChild("hammer").getChild("bone40");
        hammer.hidden = tardis.extra().getConsoleHammer() == null || tardis.extra().getConsoleHammer().isEmpty();

        super.renderWithAnimations(console, tardis, root, matrices, vertices, light, overlay, red, green, blue, pAlpha);
        matrices.pop();
    }

    @Override
    public void renderMonitorText(Tardis tardis, ConsoleBlockEntity entity, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        super.renderMonitorText(tardis, entity, matrices, vertexConsumers, light, overlay);

        MinecraftClient client = MinecraftClient.getInstance();
        TextRenderer renderer = client.textRenderer;
        TravelHandler travel = tardis.travel();
        DirectedGlobalPos abpd = travel.getState() == TravelHandlerBase.State.FLIGHT
                ? travel.getProgress()
                : travel.position();
        CachedDirectedGlobalPos dabpd = travel.destination();
        CachedDirectedGlobalPos abpp = travel.isLanded() || travel.getState() != TravelHandlerBase.State.MAT
                ? travel.getProgress()
                : travel.position();

        BlockPos abppPos = abpp.getPos();
        BlockPos abpdPos = abpd.getPos();
        matrices.push();
        // TODO dont forget to add variant.getConsoleTextPosition()!
        matrices.translate(1.85, 0.60, 0.85);
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(160f +11f));
        matrices.scale(0.005f, 0.005f, 0.005f);
        matrices.multiply(RotationAxis.NEGATIVE_Z.rotationDegrees(4));
        matrices.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(30f));
        matrices.translate(-240f, -228, -5f);
        String positionPosText = " " + abppPos.getX() + ", " + abppPos.getY() + ", " + abppPos.getZ();
        Text positionDimensionText = WorldUtil.worldText(abpp.getDimension());
        String positionDirectionText = " " + DirectionControl.rotationToDirection(abpp.getRotation()).toUpperCase();
        String destinationPosText = " " + abpdPos.getX() + ", " + abpdPos.getY() + ", " + abpdPos.getZ();
        Text destinationDimensionText = WorldUtil.worldText(abpd.getDimension(), false);
        String destinationDirectionText = " " + DirectionControl.rotationToDirection(abpd.getRotation()).toUpperCase();
        renderer.drawWithOutline(Text.of("❌").asOrderedText(), 0, 40, 0xF00F00, 0x000000,
                matrices.peek().getPositionMatrix(), vertexConsumers, 0xF000F0);
        renderer.drawWithOutline(Text.of(positionPosText).asOrderedText(), 0, 48, 0xFFFFFF, 0x000000,
                matrices.peek().getPositionMatrix(), vertexConsumers, 0xF000F0);
        renderer.drawWithOutline(positionDimensionText.asOrderedText(), 0, 56, 0xFFFFFF, 0x000000,
                matrices.peek().getPositionMatrix(), vertexConsumers, 0xF000F0);
        renderer.drawWithOutline(Text.of(positionDirectionText).asOrderedText(), 0, 64, 0xFFFFFF, 0x000000,
                matrices.peek().getPositionMatrix(), vertexConsumers, 0xF000F0);
        matrices.pop();

        matrices.push();
        matrices.translate(0.81, 1.25, 0.93);
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(160f +11f));
        matrices.scale(0.007f, 0.007f, 0.007f);
        matrices.multiply(RotationAxis.NEGATIVE_Z.rotationDegrees(4f));
        matrices.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(30f));
        String progressText = tardis.travel().getState() == TravelHandlerBase.State.LANDED
                ? "0%"
                : tardis.travel().getDurationAsPercentage() + "%";
        matrices.translate(0, -38, -52);
        renderer.drawWithOutline(Text.of(progressText).asOrderedText(),
                0 - renderer.getWidth(progressText) / 2, 0, 0xffffff, 04,
                matrices.peek().getPositionMatrix(), vertexConsumers, 0xF000F0);
        matrices.pop();

    }

    @Override
    public ModelPart getPart() {
        return console;
    }
}
