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

import dev.amble.ait.client.animation.console.renaissance.RenaissanceAnimation;
import dev.amble.ait.client.tardis.ClientTardis;
import dev.amble.ait.core.blockentities.ConsoleBlockEntity;
import dev.amble.ait.core.tardis.Tardis;
import dev.amble.ait.core.tardis.control.impl.DirectionControl;
import dev.amble.ait.core.tardis.control.impl.pos.IncrementManager;
import dev.amble.ait.core.tardis.handler.travel.TravelHandler;
import dev.amble.ait.core.tardis.handler.travel.TravelHandlerBase;
import dev.amble.ait.core.util.WorldUtil;

public class RenaissanceConsoleModel extends ConsoleModel {

    private final ModelPart console;

    public RenaissanceConsoleModel(ModelPart root) {

        this.console = root.getChild("bone7");
    }
    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData bone7 = modelPartData.addChild("bone7", ModelPartBuilder.create(), ModelTransform.of(0.0F, 28.0F, 0.0F, 0.0F, -1.5708F, 0.0F));

        ModelPartData panelf = bone7.addChild("panelf", ModelPartBuilder.create().uv(107, 181).cuboid(-14.0F, -15.9306F, -25.1225F, 28.0F, 1.0F, 0.0F, new Dilation(0.001F))
                .uv(0, 255).cuboid(-14.0F, -15.4306F, -24.1225F, 28.0F, 1.0F, 0.0F, new Dilation(0.001F))
                .uv(26, 100).cuboid(-5.5F, -22.0F, -9.5F, 11.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(24, 100).cuboid(-0.5F, -21.5F, -8.5F, 1.0F, 0.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -4.0F, 0.0F, 0.0F, 1.5708F, 0.0F));

        ModelPartData cube_r1 = panelf.addChild("cube_r1", ModelPartBuilder.create().uv(177, 121).cuboid(-0.5F, 29.2534F, -21.9032F, 1.0F, 1.0F, 11.0F, new Dilation(-0.001F)), ModelTransform.of(-0.0644F, -37.5334F, -0.1136F, -0.2618F, 0.5236F, 0.0F));

        ModelPartData cube_r2 = panelf.addChild("cube_r2", ModelPartBuilder.create().uv(26, 181).cuboid(-0.5F, 21.5366F, -29.1165F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -37.517F, 0.0F, 0.0F, 0.5236F, 0.0F));

        ModelPartData cube_r3 = panelf.addChild("cube_r3", ModelPartBuilder.create().uv(32, 27).cuboid(-0.5F, 19.3203F, -8.5587F, 1.0F, 1.0F, 4.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, -37.517F, 0.0F, -0.2443F, 0.5236F, 0.0F));

        ModelPartData cube_r4 = panelf.addChild("cube_r4", ModelPartBuilder.create().uv(179, 122).cuboid(-0.5F, 13.8677F, -33.3321F, 1.0F, 1.0F, 10.0F, new Dilation(0.001F)), ModelTransform.of(-0.065F, -37.4993F, -0.1105F, 0.2443F, 0.5236F, 0.0F));

        ModelPartData cube_r5 = panelf.addChild("cube_r5", ModelPartBuilder.create().uv(0, 247).cuboid(-14.0F, 24.1225F, 23.0864F, 28.0F, 1.0F, 0.0F, new Dilation(0.001F))
                .uv(0, 248).cuboid(-14.0F, 24.1225F, 22.0864F, 28.0F, 1.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, -37.517F, 0.0F, -1.5708F, 0.0F, 0.0F));

        ModelPartData cube_r6 = panelf.addChild("cube_r6", ModelPartBuilder.create().uv(107, 181).cuboid(-14.0F, -23.5864F, -25.1225F, 28.0F, 1.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, -37.517F, 0.0F, 0.0F, 0.0F, -3.1416F));

        ModelPartData cube_r7 = panelf.addChild("cube_r7", ModelPartBuilder.create().uv(66, 240).cuboid(-10.0F, -25.9158F, 16.4292F, 20.0F, 4.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, -37.517F, 0.0F, 3.1416F, 0.0F, 0.0F));

        ModelPartData cube_r8 = panelf.addChild("cube_r8", ModelPartBuilder.create().uv(34, 10).cuboid(-14.0F, -18.1619F, -29.2849F, 28.0F, 9.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, -37.517F, 0.0F, 1.309F, 0.0F, 0.0F));

        ModelPartData cube_r9 = panelf.addChild("cube_r9", ModelPartBuilder.create().uv(183, 241).cuboid(-8.0F, -6.2872F, 19.353F, 16.0F, 3.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, -37.517F, 0.0F, 1.8326F, 0.0F, -3.1416F));

        ModelPartData cube_r10 = panelf.addChild("cube_r10", ModelPartBuilder.create().uv(66, 244).cuboid(-10.0F, -16.8951F, 21.2658F, 20.0F, 5.0F, 0.0F, new Dilation(0.001F))
                .uv(29, 227).cuboid(-10.0F, -17.3951F, 21.5158F, 20.0F, 6.0F, 1.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, -37.517F, 0.0F, 1.5708F, 0.0F, -3.1416F));

        ModelPartData cube_r11 = panelf.addChild("cube_r11", ModelPartBuilder.create().uv(0, 238).cuboid(-11.0F, 19.5158F, 17.3951F, 22.0F, 2.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, -37.517F, 0.0F, 3.1416F, 0.0F, -3.1416F));

        ModelPartData cube_r12 = panelf.addChild("cube_r12", ModelPartBuilder.create().uv(0, 10).cuboid(-5.1325F, -29.6577F, 14.2237F, 1.0F, 5.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, -37.517F, 0.0F, 1.2707F, -0.504F, -2.9933F));

        ModelPartData cube_r13 = panelf.addChild("cube_r13", ModelPartBuilder.create().uv(2, 10).cuboid(4.1325F, -29.6577F, 14.2237F, 1.0F, 5.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, -37.517F, 0.0F, 1.2707F, 0.504F, 2.9933F));

        ModelPartData cube_r14 = panelf.addChild("cube_r14", ModelPartBuilder.create().uv(26, 17).cuboid(8.2F, -25.1535F, 14.0487F, 1.0F, 1.0F, 1.0F, new Dilation(-0.1F))
                .uv(26, 17).cuboid(7.2F, -25.1535F, 14.0487F, 1.0F, 1.0F, 1.0F, new Dilation(-0.1F))
                .uv(26, 17).cuboid(6.2F, -25.1535F, 14.0487F, 1.0F, 1.0F, 1.0F, new Dilation(-0.1F))
                .uv(26, 17).cuboid(5.2F, -25.1535F, 14.0487F, 1.0F, 1.0F, 1.0F, new Dilation(-0.1F))
                .uv(26, 17).cuboid(-0.3F, -28.1535F, 13.6487F, 1.0F, 1.0F, 1.0F, new Dilation(-0.2F))
                .uv(26, 17).cuboid(-2.2F, -28.1535F, 13.7487F, 1.0F, 1.0F, 1.0F, new Dilation(-0.2F))
                .uv(56, 37).cuboid(-11.0F, -28.3535F, 14.2487F, 22.0F, 5.0F, 0.0F, new Dilation(0.001F))
                .uv(92, 89).cuboid(-14.0F, -21.8535F, 15.4487F, 28.0F, 14.0F, 0.0F, new Dilation(0.001F))
                .uv(90, 4).cuboid(-14.0F, -21.8535F, 15.8487F, 28.0F, 14.0F, 0.0F, new Dilation(0.001F))
                .uv(0, 82).cuboid(-14.0F, -21.8535F, 16.3487F, 28.0F, 14.0F, 0.0F, new Dilation(0.001F))
                .uv(67, 97).cuboid(-8.0F, -21.8535F, 14.3487F, 2.0F, 2.0F, 3.0F, new Dilation(0.001F))
                .uv(67, 97).cuboid(6.0F, -21.8535F, 14.3487F, 2.0F, 2.0F, 3.0F, new Dilation(0.001F))
                .uv(0, 0).cuboid(-14.0F, -29.8535F, 14.3487F, 28.0F, 8.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, -37.517F, 0.0F, 1.309F, 0.0F, -3.1416F));

        ModelPartData cube_r15 = panelf.addChild("cube_r15", ModelPartBuilder.create().uv(0, 16).cuboid(21.7359F, -12.694F, 13.4487F, 1.0F, 0.0F, 1.0F, new Dilation(0.0F))
                .uv(0, 16).cuboid(21.0288F, -13.4011F, 13.4487F, 1.0F, 0.0F, 1.0F, new Dilation(0.0F))
                .uv(0, 16).cuboid(23.1501F, -11.2798F, 13.4487F, 1.0F, 0.0F, 1.0F, new Dilation(0.0F))
                .uv(0, 16).cuboid(22.443F, -11.9869F, 13.4487F, 1.0F, 0.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -37.517F, 0.0F, 1.2086F, 0.7519F, 2.8883F));

        ModelPartData cube_r16 = panelf.addChild("cube_r16", ModelPartBuilder.create().uv(29, 17).cuboid(8.8F, -28.4183F, 9.726F, 1.0F, 0.0F, 1.0F, new Dilation(0.001F))
                .uv(0, 29).cuboid(7.8F, -28.4183F, 9.726F, 1.0F, 0.0F, 1.0F, new Dilation(0.001F))
                .uv(29, 17).cuboid(6.8F, -28.4183F, 9.726F, 1.0F, 0.0F, 1.0F, new Dilation(0.001F))
                .uv(0, 29).cuboid(5.8F, -28.4183F, 9.726F, 1.0F, 0.0F, 1.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, -37.517F, 0.0F, 1.1781F, 0.0F, -3.1416F));

        ModelPartData cube_r17 = panelf.addChild("cube_r17", ModelPartBuilder.create().uv(4, 10).cuboid(-16.0F, 22.517F, -2.0F, 11.0F, 3.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -37.517F, 0.0F, 0.0F, -1.5708F, 0.0F));

        ModelPartData telepathic_circuits = panelf.addChild("telepathic_circuits", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, -37.517F, 0.0F));

        ModelPartData cube_r18 = telepathic_circuits.addChild("cube_r18", ModelPartBuilder.create().uv(56, 116).cuboid(7.5F, -22.0535F, 11.6237F, 0.0F, 2.0F, 1.0F, new Dilation(0.0F))
                .uv(53, 116).cuboid(7.5F, -20.0535F, 11.6237F, 3.0F, 0.0F, 1.0F, new Dilation(0.0F))
                .uv(56, 116).cuboid(10.5F, -22.0535F, 11.6237F, 0.0F, 2.0F, 1.0F, new Dilation(0.0F))
                .uv(53, 116).cuboid(7.5F, -22.0535F, 11.6237F, 3.0F, 0.0F, 1.0F, new Dilation(0.0F))
                .uv(54, 117).cuboid(7.5F, -22.0535F, 12.6237F, 3.0F, 2.0F, 0.0F, new Dilation(0.0F))
                .uv(55, 116).cuboid(8.5F, -21.5535F, 12.6487F, 1.0F, 1.0F, 1.0F, new Dilation(0.001F))
                .uv(42, 31).cuboid(7.4F, -22.0535F, 12.6237F, 3.0F, 2.0F, 0.0F, new Dilation(0.001F))
                .uv(42, 31).cuboid(5.6F, -22.0535F, 12.6237F, 3.0F, 2.0F, 0.0F, new Dilation(0.001F))
                .uv(67, 96).cuboid(8.5F, -21.4535F, 13.5487F, 1.0F, 1.0F, 4.0F, new Dilation(-0.1F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 1.309F, 0.0F, -3.1416F));

        ModelPartData p116 = panelf.addChild("p116", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, -37.517F, 0.0F));

        ModelPartData cube_r19 = p116.addChild("cube_r19", ModelPartBuilder.create().uv(56, 42).cuboid(-11.3F, -28.3535F, 14.1487F, 11.0F, 5.0F, 0.0F, new Dilation(0.001F))
                .uv(56, 47).cuboid(-11.3F, -28.3535F, 14.0487F, 11.0F, 5.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 1.309F, 0.0F, -3.1416F));

        ModelPartData p19 = panelf.addChild("p19", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, -37.517F, 0.0F));

        ModelPartData cube_r20 = p19.addChild("cube_r20", ModelPartBuilder.create().uv(56, 42).mirrored().cuboid(-1.1F, -28.3535F, 14.1487F, 11.0F, 5.0F, 0.0F, new Dilation(0.001F)).mirrored(false)
                .uv(56, 47).mirrored().cuboid(-1.1F, -28.3535F, 14.0487F, 11.0F, 5.0F, 0.0F, new Dilation(0.001F)).mirrored(false), ModelTransform.of(0.0F, 0.0F, 0.0F, 1.309F, 0.0F, -3.1416F));

        ModelPartData sonic_port = panelf.addChild("sonic_port", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, -37.517F, 0.0F));

        ModelPartData cube_r21 = sonic_port.addChild("cube_r21", ModelPartBuilder.create().uv(28, 27).cuboid(-9.2F, -26.6535F, 14.1487F, 2.0F, 2.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 1.309F, 0.0F, -3.1416F));

        ModelPartData handbrake = panelf.addChild("handbrake", ModelPartBuilder.create(), ModelTransform.pivot(0.8F, -16.614F, -21.9927F));

        ModelPartData cube_r22 = handbrake.addChild("cube_r22", ModelPartBuilder.create().uv(62, 28).cuboid(-0.5F, -0.5F, -2.35F, 1.0F, 1.0F, 4.0F, new Dilation(-0.31F))
                .uv(27, 75).cuboid(-0.5F, -0.5F, -2.35F, 1.0F, 1.0F, 1.0F, new Dilation(-0.1F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 1.309F, 0.0F, -3.1416F));

        ModelPartData bone50 = panelf.addChild("bone50", ModelPartBuilder.create(), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.5708F, 0.0F));

        ModelPartData twist5 = panelf.addChild("twist5", ModelPartBuilder.create(), ModelTransform.of(-7.4023F, -18.7395F, -16.5545F, 0.196F, -0.6286F, -0.3257F));

        ModelPartData cube_r23 = twist5.addChild("cube_r23", ModelPartBuilder.create().uv(0, 32).cuboid(-0.5F, -0.6F, -0.7F, 1.0F, 1.0F, 1.0F, new Dilation(-0.25F))
                .uv(30, 12).cuboid(-0.3F, -0.6F, -0.19F, 1.0F, 1.0F, 1.0F, new Dilation(-0.25F)), ModelTransform.of(0.0955F, -0.083F, 0.0987F, 1.2012F, 0.7534F, 2.8782F));

        ModelPartData cube_r24 = twist5.addChild("cube_r24", ModelPartBuilder.create().uv(0, 30).cuboid(-0.5F, -0.4F, 0.0F, 1.0F, 1.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(-0.1188F, 0.0028F, -0.0234F, 1.2012F, 0.7534F, 2.8782F));

        ModelPartData rotation = panelf.addChild("rotation", ModelPartBuilder.create(), ModelTransform.of(6.8023F, -18.7395F, -16.5545F, 0.196F, 0.6286F, 0.3257F));

        ModelPartData cube_r25 = rotation.addChild("cube_r25", ModelPartBuilder.create().uv(0, 32).mirrored().cuboid(-0.5F, -0.6F, -0.7F, 1.0F, 1.0F, 1.0F, new Dilation(-0.25F)).mirrored(false)
                .uv(30, 12).mirrored().cuboid(-0.7F, -0.6F, -0.19F, 1.0F, 1.0F, 1.0F, new Dilation(-0.25F)).mirrored(false), ModelTransform.of(-0.0955F, -0.083F, 0.0987F, 1.176F, -0.7428F, -2.8888F));

        ModelPartData cube_r26 = rotation.addChild("cube_r26", ModelPartBuilder.create().uv(0, 30).mirrored().cuboid(-0.5F, -0.4F, 0.0F, 1.0F, 1.0F, 0.0F, new Dilation(0.001F)).mirrored(false), ModelTransform.of(0.1188F, 0.0028F, -0.0234F, 1.176F, -0.7428F, -2.8888F));

        ModelPartData panelf2 = bone7.addChild("panelf2", ModelPartBuilder.create().uv(29, 234).cuboid(-14.0F, -15.9306F, -25.1225F, 28.0F, 1.0F, 0.0F, new Dilation(0.001F))
                .uv(0, 255).cuboid(-14.0F, -15.4306F, -24.1225F, 28.0F, 1.0F, 0.0F, new Dilation(0.001F))
                .uv(26, 100).cuboid(-5.5F, -22.0F, -9.5F, 11.0F, 1.0F, 1.0F, new Dilation(0.01F))
                .uv(24, 100).cuboid(-0.5F, -21.5F, -8.5F, 1.0F, 0.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -4.0F, 0.0F, 0.0F, 2.618F, 0.0F));

        ModelPartData cube_r27 = panelf2.addChild("cube_r27", ModelPartBuilder.create().uv(90, 4).cuboid(-14.0F, -21.8535F, 15.8487F, 28.0F, 14.0F, 0.0F, new Dilation(0.001F))
                .uv(92, 89).cuboid(-14.0F, -21.8535F, 15.4487F, 28.0F, 14.0F, 0.0F, new Dilation(0.001F))
                .uv(0, 82).cuboid(-14.0F, -21.8535F, 16.3487F, 28.0F, 14.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, -37.517F, 0.0F, 1.2939F, 0.0002F, -3.1329F));

        ModelPartData cube_r28 = panelf2.addChild("cube_r28", ModelPartBuilder.create().uv(66, 240).cuboid(-10.0F, -25.9158F, 16.4292F, 20.0F, 4.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, -37.517F, 0.0F, -3.1416F, 0.0F, 0.0F));

        ModelPartData cube_r29 = panelf2.addChild("cube_r29", ModelPartBuilder.create().uv(29, 227).cuboid(-10.0F, -17.3951F, 21.5158F, 20.0F, 6.0F, 1.0F, new Dilation(0.0F))
                .uv(66, 244).cuboid(-10.0F, -16.8951F, 21.2658F, 20.0F, 5.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, -37.517F, 0.0F, 1.5708F, 0.0F, 3.1416F));

        ModelPartData cube_r30 = panelf2.addChild("cube_r30", ModelPartBuilder.create().uv(0, 238).cuboid(-11.0F, 19.5158F, 17.3951F, 22.0F, 2.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, -37.517F, 0.0F, 3.1416F, 0.0F, 3.1416F));

        ModelPartData cube_r31 = panelf2.addChild("cube_r31", ModelPartBuilder.create().uv(177, 121).cuboid(-0.5F, 29.2534F, -21.9032F, 1.0F, 1.0F, 11.0F, new Dilation(-0.001F)), ModelTransform.of(-0.0644F, -37.5334F, -0.1136F, -0.2618F, 0.5236F, 0.0F));

        ModelPartData cube_r32 = panelf2.addChild("cube_r32", ModelPartBuilder.create().uv(26, 181).cuboid(-0.5F, 21.5366F, -29.1165F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -37.517F, 0.0F, 0.0F, 0.5236F, 0.0F));

        ModelPartData cube_r33 = panelf2.addChild("cube_r33", ModelPartBuilder.create().uv(32, 27).cuboid(-0.5F, 19.3203F, -8.5587F, 1.0F, 1.0F, 4.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, -37.517F, 0.0F, -0.2443F, 0.5236F, 0.0F));

        ModelPartData cube_r34 = panelf2.addChild("cube_r34", ModelPartBuilder.create().uv(179, 122).cuboid(-0.5F, 13.8677F, -33.3321F, 1.0F, 1.0F, 10.0F, new Dilation(0.001F)), ModelTransform.of(-0.065F, -37.4993F, -0.1105F, 0.2443F, 0.5236F, 0.0F));

        ModelPartData cube_r35 = panelf2.addChild("cube_r35", ModelPartBuilder.create().uv(0, 247).cuboid(-14.0F, 24.1225F, 23.0864F, 28.0F, 1.0F, 0.0F, new Dilation(0.001F))
                .uv(0, 248).cuboid(-14.0F, 24.1225F, 22.0864F, 28.0F, 1.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, -37.517F, 0.0F, -1.5708F, 0.0F, 0.0F));

        ModelPartData cube_r36 = panelf2.addChild("cube_r36", ModelPartBuilder.create().uv(107, 181).cuboid(-14.0F, -23.5864F, -25.1225F, 28.0F, 1.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, -37.517F, 0.0F, 0.0F, 0.0F, 3.1416F));

        ModelPartData cube_r37 = panelf2.addChild("cube_r37", ModelPartBuilder.create().uv(34, 10).cuboid(-14.0F, -18.1619F, -29.2849F, 28.0F, 9.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, -37.517F, 0.0F, 1.309F, 0.0F, 0.0F));

        ModelPartData cube_r38 = panelf2.addChild("cube_r38", ModelPartBuilder.create().uv(183, 241).cuboid(-8.0F, -6.2872F, 19.353F, 16.0F, 3.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, -37.517F, 0.0F, 1.8326F, 0.0F, 3.1416F));

        ModelPartData cube_r39 = panelf2.addChild("cube_r39", ModelPartBuilder.create().uv(24, 32).cuboid(-29.7737F, -7.7976F, 14.3237F, 5.0F, 1.0F, 1.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, -37.517F, 0.0F, 1.0788F, -0.9909F, -2.7202F));

        ModelPartData cube_r40 = panelf2.addChild("cube_r40", ModelPartBuilder.create().uv(24, 34).cuboid(24.7737F, -7.7976F, 14.3237F, 5.0F, 1.0F, 1.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, -37.517F, 0.0F, 1.0788F, 0.9909F, 2.7202F));

        ModelPartData cube_r41 = panelf2.addChild("cube_r41", ModelPartBuilder.create().uv(32, 8).cuboid(-6.5F, -25.8535F, 14.3237F, 13.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(36, 34).mirrored().cuboid(8.1F, -28.2535F, 13.3487F, 3.0F, 3.0F, 1.0F, new Dilation(0.001F)).mirrored(false)
                .uv(38, 27).cuboid(8.15F, -28.2535F, 14.2487F, 3.0F, 3.0F, 1.0F, new Dilation(0.1F))
                .uv(38, 27).cuboid(-11.1F, -28.2535F, 14.2487F, 3.0F, 3.0F, 1.0F, new Dilation(0.1F))
                .uv(36, 34).cuboid(-11.1F, -28.2535F, 13.3487F, 3.0F, 3.0F, 1.0F, new Dilation(0.001F))
                .uv(43, 110).cuboid(-2.8F, -29.2035F, 14.0487F, 1.0F, 2.0F, 1.0F, new Dilation(-0.2F))
                .uv(43, 110).cuboid(-1.8F, -29.2035F, 14.0487F, 1.0F, 2.0F, 1.0F, new Dilation(-0.2F))
                .uv(43, 110).cuboid(-0.7F, -29.2035F, 14.0487F, 1.0F, 2.0F, 1.0F, new Dilation(-0.2F))
                .uv(43, 110).cuboid(0.2F, -29.2035F, 14.0487F, 1.0F, 2.0F, 1.0F, new Dilation(-0.2F))
                .uv(26, 17).cuboid(-7.25F, -29.9535F, 14.0487F, 1.0F, 1.0F, 1.0F, new Dilation(-0.2F))
                .uv(26, 17).mirrored().cuboid(6.25F, -29.9535F, 14.0487F, 1.0F, 1.0F, 1.0F, new Dilation(-0.2F)).mirrored(false)
                .uv(0, 38).cuboid(-14.0F, -29.8535F, 14.3487F, 28.0F, 8.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, -37.517F, 0.0F, 1.309F, 0.0F, 3.1416F));

        ModelPartData cube_r42 = panelf2.addChild("cube_r42", ModelPartBuilder.create().uv(56, 235).cuboid(-8.0F, -27.9969F, 16.9828F, 16.0F, 5.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, -37.517F, 0.0F, 1.3963F, 0.0F, 3.1416F));

        ModelPartData cube_r43 = panelf2.addChild("cube_r43", ModelPartBuilder.create().uv(4, 10).cuboid(-16.0F, 22.517F, -2.0F, 11.0F, 3.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -37.517F, 0.0F, 0.0F, -1.5708F, 0.0F));

        ModelPartData x = panelf2.addChild("x", ModelPartBuilder.create(), ModelTransform.of(-6.0F, -19.3267F, -14.7377F, -0.1308F, -0.0057F, -0.0433F));

        ModelPartData cube_r44 = x.addChild("cube_r44", ModelPartBuilder.create().uv(110, 73).cuboid(-0.5061F, -1.5F, -0.4939F, 1.0F, 4.0F, 1.0F, new Dilation(-0.25F))
                .uv(116, 70).cuboid(-1.0061F, -1.5F, -0.9939F, 2.0F, 1.0F, 2.0F, new Dilation(-0.25F)), ModelTransform.of(0.0F, 0.5767F, 0.2377F, 0.5299F, 0.7119F, 0.3655F));

        ModelPartData cube_r45 = x.addChild("cube_r45", ModelPartBuilder.create().uv(107, 70).cuboid(-1.5F, -0.99F, -1.5F, 3.0F, 0.0F, 3.0F, new Dilation(0.0F))
                .uv(110, 68).cuboid(-0.5F, -1.0F, 1.65F, 1.0F, 1.0F, 1.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 0.5767F, 0.2377F, 0.3927F, 0.0F, 0.0F));

        ModelPartData cube_r46 = x.addChild("cube_r46", ModelPartBuilder.create().uv(114, 68).cuboid(-2.0F, -1.0F, 0.75F, 4.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.5767F, 0.2377F, 0.6918F, -0.9275F, -0.5853F));

        ModelPartData cube_r47 = x.addChild("cube_r47", ModelPartBuilder.create().uv(114, 68).cuboid(-2.0F, -1.0F, 0.75F, 4.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.5767F, 0.2377F, 0.6918F, 0.9275F, 0.5853F));

        ModelPartData cube_r48 = x.addChild("cube_r48", ModelPartBuilder.create().uv(114, 68).cuboid(-2.0F, -1.0F, 0.75F, 4.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.5767F, 0.2377F, 2.7489F, 0.0F, 3.1416F));

        ModelPartData cube_r49 = x.addChild("cube_r49", ModelPartBuilder.create().uv(110, 68).cuboid(-0.5F, -1.0F, 1.65F, 1.0F, 1.0F, 1.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 0.5767F, 0.2377F, 2.4498F, 0.9275F, 2.5563F));

        ModelPartData cube_r50 = x.addChild("cube_r50", ModelPartBuilder.create().uv(110, 68).cuboid(-0.5F, -1.0F, 1.65F, 1.0F, 1.0F, 1.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 0.5767F, 0.2377F, 2.4498F, -0.9275F, -2.5563F));

        ModelPartData y = panelf2.addChild("y", ModelPartBuilder.create(), ModelTransform.of(-2.0F, -19.5759F, -13.9897F, 0.0872F, -0.0019F, 0.0436F));

        ModelPartData cube_r51 = y.addChild("cube_r51", ModelPartBuilder.create().uv(110, 73).cuboid(-0.5F, -0.5F, -0.5F, 1.0F, 4.0F, 1.0F, new Dilation(-0.25F))
                .uv(116, 73).cuboid(-1.0F, -0.5F, -1.0F, 2.0F, 1.0F, 2.0F, new Dilation(-0.25F)), ModelTransform.of(0.0F, -0.3447F, -0.1509F, 0.5299F, 0.7119F, 0.3655F));

        ModelPartData cube_r52 = y.addChild("cube_r52", ModelPartBuilder.create().uv(107, 70).cuboid(-1.5F, -0.99F, -1.5F, 3.0F, 0.0F, 3.0F, new Dilation(0.0F))
                .uv(110, 68).cuboid(-0.5F, -1.0F, 1.65F, 1.0F, 1.0F, 1.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 0.5759F, 0.2397F, 2.7489F, 0.0F, -3.1416F));

        ModelPartData cube_r53 = y.addChild("cube_r53", ModelPartBuilder.create().uv(114, 68).cuboid(-2.0F, -1.0F, 0.75F, 4.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.5759F, 0.2397F, 2.4498F, 0.9275F, 2.5563F));

        ModelPartData cube_r54 = y.addChild("cube_r54", ModelPartBuilder.create().uv(114, 68).cuboid(-2.0F, -1.0F, 0.75F, 4.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.5759F, 0.2397F, 2.4498F, -0.9275F, -2.5563F));

        ModelPartData cube_r55 = y.addChild("cube_r55", ModelPartBuilder.create().uv(114, 68).cuboid(-2.0F, -1.0F, 0.75F, 4.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.5759F, 0.2397F, 0.3927F, 0.0F, 0.0F));

        ModelPartData cube_r56 = y.addChild("cube_r56", ModelPartBuilder.create().uv(110, 68).cuboid(-0.5F, -1.0F, 1.65F, 1.0F, 1.0F, 1.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 0.5759F, 0.2397F, 0.6918F, -0.9275F, -0.5853F));

        ModelPartData cube_r57 = y.addChild("cube_r57", ModelPartBuilder.create().uv(110, 68).cuboid(-0.5F, -1.0F, 1.65F, 1.0F, 1.0F, 1.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 0.5759F, 0.2397F, 0.6918F, 0.9275F, 0.5853F));

        ModelPartData z = panelf2.addChild("z", ModelPartBuilder.create(), ModelTransform.of(2.0F, -19.3267F, -14.7377F, -0.0436F, -0.0019F, -0.0436F));

        ModelPartData cube_r58 = z.addChild("cube_r58", ModelPartBuilder.create().uv(110, 73).cuboid(-0.5061F, -1.5F, -0.4939F, 1.0F, 4.0F, 1.0F, new Dilation(-0.25F))
                .uv(116, 70).cuboid(-1.0061F, -1.5F, -0.9939F, 2.0F, 1.0F, 2.0F, new Dilation(-0.25F)), ModelTransform.of(0.0F, 0.5767F, 0.2377F, 0.5299F, 0.7119F, 0.3655F));

        ModelPartData cube_r59 = z.addChild("cube_r59", ModelPartBuilder.create().uv(107, 70).cuboid(-1.5F, -0.99F, -1.5F, 3.0F, 0.0F, 3.0F, new Dilation(0.0F))
                .uv(110, 68).cuboid(-0.5F, -1.0F, 1.65F, 1.0F, 1.0F, 1.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 0.5767F, 0.2377F, 0.3927F, 0.0F, 0.0F));

        ModelPartData cube_r60 = z.addChild("cube_r60", ModelPartBuilder.create().uv(114, 68).cuboid(-2.0F, -1.0F, 0.75F, 4.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.5767F, 0.2377F, 0.6918F, -0.9275F, -0.5853F));

        ModelPartData cube_r61 = z.addChild("cube_r61", ModelPartBuilder.create().uv(114, 68).cuboid(-2.0F, -1.0F, 0.75F, 4.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.5767F, 0.2377F, 0.6918F, 0.9275F, 0.5853F));

        ModelPartData cube_r62 = z.addChild("cube_r62", ModelPartBuilder.create().uv(114, 68).cuboid(-2.0F, -1.0F, 0.75F, 4.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.5767F, 0.2377F, 2.7489F, 0.0F, 3.1416F));

        ModelPartData cube_r63 = z.addChild("cube_r63", ModelPartBuilder.create().uv(110, 68).cuboid(-0.5F, -1.0F, 1.65F, 1.0F, 1.0F, 1.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 0.5767F, 0.2377F, 2.4498F, 0.9275F, 2.5563F));

        ModelPartData cube_r64 = z.addChild("cube_r64", ModelPartBuilder.create().uv(110, 68).cuboid(-0.5F, -1.0F, 1.65F, 1.0F, 1.0F, 1.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 0.5767F, 0.2377F, 2.4498F, -0.9275F, -2.5563F));

        ModelPartData randomiser = panelf2.addChild("randomiser", ModelPartBuilder.create(), ModelTransform.of(6.0F, -19.5759F, -13.9897F, 0.0869F, -0.0076F, 0.0869F));

        ModelPartData cube_r65 = randomiser.addChild("cube_r65", ModelPartBuilder.create().uv(110, 73).cuboid(-0.5F, -0.5F, -0.5F, 1.0F, 4.0F, 1.0F, new Dilation(-0.25F))
                .uv(116, 76).cuboid(-1.0F, -0.5F, -1.0F, 2.0F, 1.0F, 2.0F, new Dilation(-0.25F)), ModelTransform.of(0.0F, -0.3447F, -0.1509F, 0.5299F, 0.7119F, 0.3655F));

        ModelPartData cube_r66 = randomiser.addChild("cube_r66", ModelPartBuilder.create().uv(107, 70).cuboid(-1.5F, -0.99F, -1.5F, 3.0F, 0.0F, 3.0F, new Dilation(0.0F))
                .uv(110, 68).cuboid(-0.5F, -1.0F, 1.65F, 1.0F, 1.0F, 1.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 0.5759F, 0.2397F, 2.7489F, 0.0F, -3.1416F));

        ModelPartData cube_r67 = randomiser.addChild("cube_r67", ModelPartBuilder.create().uv(114, 68).cuboid(-2.0F, -1.0F, 0.75F, 4.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.5759F, 0.2397F, 2.4498F, 0.9275F, 2.5563F));

        ModelPartData cube_r68 = randomiser.addChild("cube_r68", ModelPartBuilder.create().uv(114, 68).cuboid(-2.0F, -1.0F, 0.75F, 4.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.5759F, 0.2397F, 2.4498F, -0.9275F, -2.5563F));

        ModelPartData cube_r69 = randomiser.addChild("cube_r69", ModelPartBuilder.create().uv(114, 68).cuboid(-2.0F, -1.0F, 0.75F, 4.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.5759F, 0.2397F, 0.3927F, 0.0F, 0.0F));

        ModelPartData cube_r70 = randomiser.addChild("cube_r70", ModelPartBuilder.create().uv(110, 68).cuboid(-0.5F, -1.0F, 1.65F, 1.0F, 1.0F, 1.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 0.5759F, 0.2397F, 0.6918F, -0.9275F, -0.5853F));

        ModelPartData cube_r71 = randomiser.addChild("cube_r71", ModelPartBuilder.create().uv(110, 68).cuboid(-0.5F, -1.0F, 1.65F, 1.0F, 1.0F, 1.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 0.5759F, 0.2397F, 0.6918F, 0.9275F, 0.5853F));

        ModelPartData land_type = panelf2.addChild("land_type", ModelPartBuilder.create(), ModelTransform.pivot(-0.25F, -37.517F, 0.0F));

        ModelPartData cube_r72 = land_type.addChild("cube_r72", ModelPartBuilder.create().uv(45, 35).cuboid(0.75F, -29.7035F, 13.7487F, 1.0F, 1.0F, 1.0F, new Dilation(-0.28F))
                .uv(45, 35).cuboid(1.25F, -29.7035F, 13.7487F, 1.0F, 1.0F, 1.0F, new Dilation(-0.28F))
                .uv(44, 34).cuboid(1.25F, -29.0535F, 13.7487F, 1.0F, 1.0F, 2.0F, new Dilation(-0.28F))
                .uv(44, 34).cuboid(1.25F, -27.7535F, 13.7487F, 1.0F, 1.0F, 2.0F, new Dilation(-0.28F))
                .uv(44, 34).cuboid(1.25F, -28.4035F, 13.7487F, 1.0F, 1.0F, 2.0F, new Dilation(-0.28F))
                .uv(44, 34).cuboid(0.75F, -28.4035F, 13.7487F, 1.0F, 1.0F, 2.0F, new Dilation(-0.28F))
                .uv(44, 34).cuboid(0.75F, -27.7535F, 13.7487F, 1.0F, 1.0F, 2.0F, new Dilation(-0.28F))
                .uv(44, 34).cuboid(0.75F, -29.0535F, 13.7487F, 1.0F, 1.0F, 2.0F, new Dilation(-0.28F))
                .uv(44, 34).cuboid(0.75F, -27.0535F, 13.7487F, 1.0F, 1.0F, 2.0F, new Dilation(-0.28F))
                .uv(44, 34).cuboid(1.25F, -27.0535F, 13.7487F, 1.0F, 1.0F, 2.0F, new Dilation(-0.28F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 1.309F, 0.0F, 3.1416F));

        ModelPartData dimension = panelf2.addChild("dimension", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, -37.517F, 0.0F));

        ModelPartData cube_r73 = dimension.addChild("cube_r73", ModelPartBuilder.create().uv(26, 17).cuboid(-1.5F, -1.5F, -0.75F, 1.0F, 1.0F, 1.0F, new Dilation(-0.1F)), ModelTransform.of(4.0F, 21.0895F, -22.1695F, 1.309F, 0.0F, 3.1416F));

        ModelPartData cube_r74 = dimension.addChild("cube_r74", ModelPartBuilder.create().uv(26, 17).cuboid(-1.5F, -1.5F, -0.75F, 1.0F, 1.0F, 1.0F, new Dilation(-0.1F)), ModelTransform.of(5.0F, 21.0895F, -22.1695F, 1.309F, 0.0F, 3.1416F));

        ModelPartData cube_r75 = dimension.addChild("cube_r75", ModelPartBuilder.create().uv(26, 17).cuboid(-1.5F, -1.5F, -0.75F, 1.0F, 1.0F, 1.0F, new Dilation(-0.1F)), ModelTransform.of(3.0F, 21.0895F, -22.1695F, 1.309F, 0.0F, 3.1416F));

        ModelPartData bone57 = dimension.addChild("bone57", ModelPartBuilder.create(), ModelTransform.pivot(4.0F, 21.1068F, -23.2001F));

        ModelPartData cube_r76 = bone57.addChild("cube_r76", ModelPartBuilder.create().uv(1, 16).cuboid(0.0F, -0.5F, -0.5F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -0.6279F, -0.1682F, 1.2086F, -0.7519F, -2.8883F));

        ModelPartData bone58 = dimension.addChild("bone58", ModelPartBuilder.create(), ModelTransform.pivot(5.0F, 21.1068F, -23.2001F));

        ModelPartData cube_r77 = bone58.addChild("cube_r77", ModelPartBuilder.create().uv(1, 16).cuboid(0.0F, -0.5F, -0.5F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -0.6279F, -0.1682F, -1.2086F, 0.7519F, 0.2533F));

        ModelPartData bone59 = dimension.addChild("bone59", ModelPartBuilder.create(), ModelTransform.pivot(6.0F, 21.1068F, -23.2001F));

        ModelPartData cube_r78 = bone59.addChild("cube_r78", ModelPartBuilder.create().uv(1, 16).cuboid(0.0F, -0.5F, -0.5F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -0.6279F, -0.1682F, 1.2086F, 0.7519F, 2.8883F));

        ModelPartData p813 = panelf2.addChild("p813", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, -37.517F, 0.0F));

        ModelPartData cube_r79 = p813.addChild("cube_r79", ModelPartBuilder.create().uv(4, 12).cuboid(2.5F, -28.3724F, 13.533F, 1.0F, 1.0F, 1.0F, new Dilation(-0.1F))
                .uv(4, 12).cuboid(3.5F, -28.3724F, 13.533F, 1.0F, 1.0F, 1.0F, new Dilation(-0.1F))
                .uv(4, 12).cuboid(4.5F, -28.3724F, 13.533F, 1.0F, 1.0F, 1.0F, new Dilation(-0.1F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 1.309F, 0.0F, 3.1416F));

        ModelPartData monitor = panelf2.addChild("monitor", ModelPartBuilder.create(), ModelTransform.of(0.0F, -25.517F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData cube_r80 = monitor.addChild("cube_r80", ModelPartBuilder.create().uv(76, 81).cuboid(34.5F, -2.0F, -4.0F, 0.0F, 4.0F, 8.0F, new Dilation(0.0F)), ModelTransform.of(-20.5F, 0.0F, -0.2F, 0.0151F, 0.0001F, -0.0087F));

        ModelPartData cube_r81 = monitor.addChild("cube_r81", ModelPartBuilder.create().uv(24, 54).cuboid(-0.5F, -2.0F, 3.0F, 1.0F, 2.0F, 1.0F, new Dilation(-0.001F)), ModelTransform.of(13.7F, 1.7321F, -0.1641F, 0.5387F, 0.0001F, -0.0087F));

        ModelPartData cube_r82 = monitor.addChild("cube_r82", ModelPartBuilder.create().uv(24, 54).cuboid(-0.5F, -2.0F, 3.0F, 1.0F, 2.0F, 1.0F, new Dilation(-0.001F)), ModelTransform.of(13.7F, -0.025F, -1.1891F, -0.5085F, 0.0001F, -0.0087F));

        ModelPartData cube_r83 = monitor.addChild("cube_r83", ModelPartBuilder.create().uv(28, 36).cuboid(-0.5F, -2.0F, 3.0F, 1.0F, 1.0F, 1.0F, new Dilation(-0.01F)), ModelTransform.of(13.7F, 1.7321F, -0.7F, 0.0151F, 0.0001F, -0.0087F));

        ModelPartData cube_r84 = monitor.addChild("cube_r84", ModelPartBuilder.create().uv(50, 28).cuboid(-0.5F, -2.0F, -4.0F, 1.0F, 2.0F, 1.0F, new Dilation(-0.001F)), ModelTransform.of(13.7F, -0.05F, 0.7641F, 0.5387F, 0.0001F, -0.0087F));

        ModelPartData cube_r85 = monitor.addChild("cube_r85", ModelPartBuilder.create().uv(0, 34).cuboid(-0.5F, -2.0F, -4.0F, 1.0F, 1.0F, 1.0F, new Dilation(-0.01F)), ModelTransform.of(13.7F, 1.7321F, 0.3F, 0.0151F, 0.0001F, -0.0087F));

        ModelPartData cube_r86 = monitor.addChild("cube_r86", ModelPartBuilder.create().uv(50, 28).cuboid(-0.5F, -2.0F, -4.0F, 1.0F, 2.0F, 1.0F, new Dilation(-0.001F)), ModelTransform.of(13.7F, 1.7321F, -0.2359F, -0.5085F, 0.0001F, -0.0087F));

        ModelPartData cube_r87 = monitor.addChild("cube_r87", ModelPartBuilder.create().uv(19, 99).cuboid(-0.5F, -0.75F, -1.0F, 1.0F, 1.0F, 2.0F, new Dilation(0.15F)), ModelTransform.of(10.7626F, 3.8011F, -0.1358F, -0.0001F, 0.0151F, -1.5795F));

        ModelPartData cube_r88 = monitor.addChild("cube_r88", ModelPartBuilder.create().uv(29, 54).cuboid(-5.0F, -0.5F, -0.5F, 5.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(13.578F, -0.113F, -0.1948F, 0.0086F, 0.0124F, -0.9686F));

        ModelPartData cube_r89 = monitor.addChild("cube_r89", ModelPartBuilder.create().uv(78, 43).cuboid(-2.5F, 0.1F, -2.0F, 5.0F, 0.0F, 4.0F, new Dilation(0.0F))
                .uv(28, 78).cuboid(-2.5F, 0.0F, -2.0F, 5.0F, 0.0F, 4.0F, new Dilation(0.0F))
                .uv(76, 20).cuboid(-2.5F, 0.2F, -2.0F, 5.0F, 0.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(17.0743F, 2.8694F, -3.3271F, 0.5704F, -0.2509F, -0.0175F));

        ModelPartData cube_r90 = monitor.addChild("cube_r90", ModelPartBuilder.create().uv(14, 78).cuboid(-2.5F, 0.0F, -2.0F, 5.0F, 0.0F, 4.0F, new Dilation(0.0F))
                .uv(0, 78).cuboid(-2.5F, 0.1F, -2.0F, 5.0F, 0.0F, 4.0F, new Dilation(0.0F))
                .uv(76, 72).cuboid(-2.5F, 0.2F, -2.0F, 5.0F, 0.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(17.0743F, 2.8694F, 2.9271F, -0.5392F, 0.2512F, -0.0097F));

        ModelPartData cube_r91 = monitor.addChild("cube_r91", ModelPartBuilder.create().uv(36, 32).cuboid(36.0F, 1.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(76, 0).cuboid(34.0F, 2.0F, -2.0F, 5.0F, 0.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(-19.8F, 0.0F, -0.2F, 0.0151F, 0.0001F, -0.0087F));

        ModelPartData cube_r92 = monitor.addChild("cube_r92", ModelPartBuilder.create().uv(75, 77).cuboid(34.0F, -2.0F, -4.0F, 1.0F, 4.0F, 8.0F, new Dilation(0.0F)), ModelTransform.of(-20.8F, 0.25F, -0.2F, 0.0151F, 0.0001F, -0.0087F));

        ModelPartData symbol = monitor.addChild("symbol", ModelPartBuilder.create(), ModelTransform.pivot(14.0237F, -0.3013F, -0.2023F));

        ModelPartData cube_r93 = symbol.addChild("cube_r93", ModelPartBuilder.create().uv(82, 93).cuboid(-0.5F, -2.0F, -2.0F, 1.0F, 4.0F, 4.0F, new Dilation(-0.5F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0151F, 0.0001F, -0.0087F));

        ModelPartData panelf3 = bone7.addChild("panelf3", ModelPartBuilder.create().uv(107, 181).cuboid(-14.0F, -15.9306F, -25.1225F, 28.0F, 1.0F, 0.0F, new Dilation(0.001F))
                .uv(0, 255).cuboid(-14.0F, -15.4306F, -24.1225F, 28.0F, 1.0F, 0.0F, new Dilation(0.001F))
                .uv(26, 100).cuboid(-5.5F, -22.0F, -9.5F, 11.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(24, 100).cuboid(-0.5F, -21.5F, -8.5F, 1.0F, 0.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -4.0F, 0.0F, 0.0F, -2.618F, 0.0F));

        ModelPartData cube_r94 = panelf3.addChild("cube_r94", ModelPartBuilder.create().uv(90, 4).cuboid(-14.0F, -21.8535F, 15.8487F, 28.0F, 14.0F, 0.0F, new Dilation(0.001F))
                .uv(92, 89).cuboid(-14.0F, -21.8535F, 15.4487F, 28.0F, 14.0F, 0.0F, new Dilation(0.001F))
                .uv(0, 82).cuboid(-14.0F, -21.8535F, 16.3487F, 28.0F, 14.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, -37.517F, 0.0F, 1.2939F, 0.0003F, -3.1154F));

        ModelPartData cube_r95 = panelf3.addChild("cube_r95", ModelPartBuilder.create().uv(66, 240).cuboid(-10.0F, -25.9158F, 16.4292F, 20.0F, 4.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, -37.517F, 0.0F, -3.1416F, 0.0F, 0.0F));

        ModelPartData cube_r96 = panelf3.addChild("cube_r96", ModelPartBuilder.create().uv(29, 227).cuboid(-10.0F, -17.3951F, 21.5158F, 20.0F, 6.0F, 1.0F, new Dilation(0.001F))
                .uv(66, 244).cuboid(-10.0F, -16.8951F, 21.2658F, 20.0F, 5.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, -37.517F, 0.0F, 1.5708F, 0.0F, -3.1416F));

        ModelPartData cube_r97 = panelf3.addChild("cube_r97", ModelPartBuilder.create().uv(0, 236).cuboid(-11.0F, 19.5158F, 17.3951F, 22.0F, 2.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, -37.517F, 0.0F, 3.1416F, 0.0F, -3.1416F));

        ModelPartData cube_r98 = panelf3.addChild("cube_r98", ModelPartBuilder.create().uv(177, 121).cuboid(-0.5F, 29.2534F, -21.9032F, 1.0F, 1.0F, 11.0F, new Dilation(-0.001F)), ModelTransform.of(-0.0644F, -37.5334F, -0.1136F, -0.2618F, 0.5236F, 0.0F));

        ModelPartData cube_r99 = panelf3.addChild("cube_r99", ModelPartBuilder.create().uv(24, 155).cuboid(-0.475F, 21.5366F, -28.1165F, 0.0F, 4.0F, 9.0F, new Dilation(0.0F))
                .uv(26, 181).cuboid(-0.5F, 21.5366F, -29.1165F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -37.517F, 0.0F, 0.0F, 0.5236F, 0.0F));

        ModelPartData cube_r100 = panelf3.addChild("cube_r100", ModelPartBuilder.create().uv(32, 27).cuboid(-0.5F, 19.3203F, -8.5587F, 1.0F, 1.0F, 4.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, -37.517F, 0.0F, -0.2443F, 0.5236F, 0.0F));

        ModelPartData cube_r101 = panelf3.addChild("cube_r101", ModelPartBuilder.create().uv(179, 122).cuboid(-0.5F, 13.8677F, -33.3321F, 1.0F, 1.0F, 10.0F, new Dilation(0.001F)), ModelTransform.of(-0.065F, -37.4993F, -0.1105F, 0.2443F, 0.5236F, 0.0F));

        ModelPartData cube_r102 = panelf3.addChild("cube_r102", ModelPartBuilder.create().uv(0, 247).cuboid(-14.0F, 24.1225F, 23.0864F, 28.0F, 1.0F, 0.0F, new Dilation(0.001F))
                .uv(0, 248).cuboid(-14.0F, 24.1225F, 22.0864F, 28.0F, 1.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, -37.517F, 0.0F, -1.5708F, 0.0F, 0.0F));

        ModelPartData cube_r103 = panelf3.addChild("cube_r103", ModelPartBuilder.create().uv(107, 181).cuboid(-14.0F, -23.5864F, -25.1225F, 28.0F, 1.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, -37.517F, 0.0F, 0.0F, 0.0F, -3.1416F));

        ModelPartData cube_r104 = panelf3.addChild("cube_r104", ModelPartBuilder.create().uv(34, 10).cuboid(-14.0F, -18.1619F, -29.2849F, 28.0F, 9.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, -37.517F, 0.0F, 1.309F, 0.0F, 0.0F));

        ModelPartData cube_r105 = panelf3.addChild("cube_r105", ModelPartBuilder.create().uv(183, 241).cuboid(-8.0F, -6.2872F, 19.353F, 16.0F, 3.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, -37.517F, 0.0F, 1.8326F, 0.0F, -3.1416F));

        ModelPartData cube_r106 = panelf3.addChild("cube_r106", ModelPartBuilder.create().uv(0, 27).cuboid(-29.9615F, -5.5966F, 14.3237F, 12.0F, 1.0F, 4.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, -37.517F, 0.0F, 1.0788F, -0.9909F, -2.7202F));

        ModelPartData cube_r107 = panelf3.addChild("cube_r107", ModelPartBuilder.create().uv(0, 27).mirrored().cuboid(17.9615F, -5.5966F, 14.3237F, 12.0F, 1.0F, 4.0F, new Dilation(0.001F)).mirrored(false), ModelTransform.of(0.0F, -37.517F, 0.0F, 1.0788F, 0.9909F, 2.7202F));

        ModelPartData cube_r108 = panelf3.addChild("cube_r108", ModelPartBuilder.create().uv(0, 32).cuboid(-5.0F, -18.8535F, 14.3237F, 10.0F, 1.0F, 4.0F, new Dilation(0.0F))
                .uv(62, 163).cuboid(-14.0F, -29.8535F, 14.3487F, 28.0F, 8.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, -37.517F, 0.0F, 1.309F, 0.0F, -3.1416F));

        ModelPartData cube_r109 = panelf3.addChild("cube_r109", ModelPartBuilder.create().uv(0, 15).mirrored().cuboid(1.3F, -23.495F, 16.6652F, 1.0F, 1.0F, 0.0F, new Dilation(0.0F)).mirrored(false)
                .uv(0, 15).mirrored().cuboid(0.8F, -24.495F, 16.6652F, 1.0F, 1.0F, 0.0F, new Dilation(0.0F)).mirrored(false)
                .uv(23, 8).cuboid(0.9F, -25.495F, 16.6652F, 1.0F, 1.0F, 0.0F, new Dilation(0.0F))
                .uv(0, 15).cuboid(-1.9F, -25.495F, 16.6652F, 1.0F, 1.0F, 0.0F, new Dilation(0.0F))
                .uv(2, 15).cuboid(-2.4F, -24.495F, 16.6652F, 1.0F, 1.0F, 0.0F, new Dilation(0.0F))
                .uv(0, 15).cuboid(-1.7F, -23.495F, 16.6652F, 1.0F, 1.0F, 0.0F, new Dilation(0.0F))
                .uv(56, 52).cuboid(-4.0F, -19.695F, 16.0652F, 8.0F, 3.0F, 1.0F, new Dilation(0.001F))
                .uv(58, 124).cuboid(-3.25F, -21.745F, 16.6652F, 6.0F, 1.0F, 0.0F, new Dilation(0.0F))
                .uv(72, 27).cuboid(-10.0F, -26.995F, 16.7652F, 20.0F, 10.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, -37.517F, 0.0F, 1.3963F, 0.0F, -3.1416F));

        ModelPartData cube_r110 = panelf3.addChild("cube_r110", ModelPartBuilder.create().uv(4, 10).cuboid(-16.0F, 22.517F, -2.0F, 11.0F, 3.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -37.517F, 0.0F, 0.0F, -1.5708F, 0.0F));

        ModelPartData fast_return = panelf3.addChild("fast_return", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, -37.517F, 0.0F));

        ModelPartData cube_r111 = fast_return.addChild("cube_r111", ModelPartBuilder.create().uv(30, 10).cuboid(-3.5F, -19.195F, 15.3652F, 1.0F, 1.0F, 1.0F, new Dilation(-0.2F))
                .uv(30, 10).mirrored().cuboid(2.5F, -19.195F, 15.3652F, 1.0F, 1.0F, 1.0F, new Dilation(-0.2F)).mirrored(false)
                .uv(25, 8).cuboid(1.2F, -18.195F, 15.2652F, 1.0F, 0.0F, 1.0F, new Dilation(0.0F))
                .uv(25, 8).cuboid(-0.8F, -18.195F, 15.2652F, 1.0F, 0.0F, 1.0F, new Dilation(0.0F))
                .uv(25, 8).cuboid(0.2F, -18.195F, 15.2652F, 1.0F, 0.0F, 1.0F, new Dilation(0.0F))
                .uv(25, 8).cuboid(-1.8F, -18.195F, 15.2652F, 1.0F, 0.0F, 1.0F, new Dilation(0.0F))
                .uv(25, 8).cuboid(-2.8F, -18.195F, 15.2652F, 1.0F, 0.0F, 1.0F, new Dilation(0.0F))
                .uv(24, 9).cuboid(1.2F, -19.195F, 15.0652F, 1.0F, 0.0F, 1.0F, new Dilation(0.0F))
                .uv(27, 9).cuboid(0.2F, -19.195F, 15.0652F, 1.0F, 0.0F, 1.0F, new Dilation(0.0F))
                .uv(24, 9).cuboid(-2.8F, -19.195F, 15.0652F, 1.0F, 0.0F, 1.0F, new Dilation(0.0F))
                .uv(28, 8).cuboid(-1.8F, -19.195F, 15.0652F, 1.0F, 0.0F, 1.0F, new Dilation(0.0F))
                .uv(24, 9).cuboid(-0.8F, -19.195F, 15.0652F, 1.0F, 0.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 1.3963F, 0.0F, -3.1416F));

        ModelPartData p3 = panelf3.addChild("p3", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, -37.517F, 0.0F));

        ModelPartData cube_r112 = p3.addChild("cube_r112", ModelPartBuilder.create().uv(26, 17).cuboid(22.6189F, -13.1537F, 16.2152F, 1.0F, 1.0F, 1.0F, new Dilation(-0.1F))
                .uv(26, 17).cuboid(22.1239F, -14.2143F, 16.2152F, 1.0F, 1.0F, 1.0F, new Dilation(-0.1F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 1.3264F, 0.7703F, 2.9697F));

        ModelPartData adaptive = panelf3.addChild("adaptive", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, -37.517F, 0.0F));

        ModelPartData cube_r113 = adaptive.addChild("cube_r113", ModelPartBuilder.create().uv(26, 17).mirrored().cuboid(-23.6189F, -13.1537F, 16.2152F, 1.0F, 1.0F, 1.0F, new Dilation(-0.1F)).mirrored(false)
                .uv(26, 17).mirrored().cuboid(-23.1239F, -14.2143F, 16.2152F, 1.0F, 1.0F, 1.0F, new Dilation(-0.1F)).mirrored(false), ModelTransform.of(0.0F, 0.0F, 0.0F, 1.3264F, -0.7703F, -2.9697F));

        ModelPartData panelf4 = bone7.addChild("panelf4", ModelPartBuilder.create().uv(118, 20).cuboid(-14.0F, -15.4306F, -23.1225F, 28.0F, 9.0F, 0.0F, new Dilation(0.001F))
                .uv(120, 29).cuboid(-13.0F, -15.4306F, -22.1225F, 26.0F, 9.0F, 0.0F, new Dilation(0.001F))
                .uv(122, 38).cuboid(-12.0F, -15.4306F, -21.1225F, 24.0F, 9.0F, 0.0F, new Dilation(0.001F))
                .uv(124, 47).cuboid(-11.0F, -15.4306F, -20.1225F, 22.0F, 9.0F, 0.0F, new Dilation(0.001F))
                .uv(126, 56).cuboid(-10.0F, -15.4306F, -19.1225F, 20.0F, 9.0F, 0.0F, new Dilation(0.001F))
                .uv(107, 181).cuboid(-14.0F, -15.9306F, -25.1225F, 28.0F, 1.0F, 0.0F, new Dilation(0.001F))
                .uv(26, 100).cuboid(-5.5F, -22.0F, -9.5F, 11.0F, 1.0F, 1.0F, new Dilation(0.01F))
                .uv(24, 100).cuboid(-0.5F, -21.5F, -8.5F, 1.0F, 0.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -4.0F, 0.0F, 0.0F, -1.5708F, 0.0F));

        ModelPartData cube_r114 = panelf4.addChild("cube_r114", ModelPartBuilder.create().uv(90, 4).cuboid(-14.0F, -21.8535F, 15.8487F, 28.0F, 14.0F, 0.0F, new Dilation(0.001F))
                .uv(92, 89).cuboid(-14.0F, -21.8535F, 15.4487F, 28.0F, 14.0F, 0.0F, new Dilation(0.001F))
                .uv(0, 82).cuboid(-14.0F, -21.8535F, 16.3487F, 28.0F, 14.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, -37.517F, 0.0F, 1.309F, 0.0F, -3.1067F));

        ModelPartData cube_r115 = panelf4.addChild("cube_r115", ModelPartBuilder.create().uv(29, 227).cuboid(-10.0F, -17.3951F, 21.5158F, 20.0F, 6.0F, 1.0F, new Dilation(0.0F))
                .uv(66, 244).cuboid(-10.0F, -16.8951F, 21.2658F, 20.0F, 5.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, -37.517F, 0.0F, 1.5708F, 0.0F, 3.1416F));

        ModelPartData cube_r116 = panelf4.addChild("cube_r116", ModelPartBuilder.create().uv(0, 238).cuboid(-11.0F, 19.5158F, 17.3951F, 22.0F, 2.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, -37.517F, 0.0F, 3.1416F, 0.0F, 3.1416F));

        ModelPartData cube_r117 = panelf4.addChild("cube_r117", ModelPartBuilder.create().uv(177, 121).cuboid(-0.5F, 29.2534F, -21.9032F, 1.0F, 1.0F, 11.0F, new Dilation(-0.001F)), ModelTransform.of(-0.0644F, -37.5334F, -0.1136F, -0.2618F, 0.5236F, 0.0F));

        ModelPartData cube_r118 = panelf4.addChild("cube_r118", ModelPartBuilder.create().uv(24, 155).cuboid(0.475F, 21.5366F, -28.1165F, 0.0F, 4.0F, 9.0F, new Dilation(0.0F))
                .uv(26, 181).cuboid(-0.5F, 21.5366F, -29.1165F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -37.517F, 0.0F, 0.0F, 0.5236F, 0.0F));

        ModelPartData cube_r119 = panelf4.addChild("cube_r119", ModelPartBuilder.create().uv(32, 27).cuboid(-0.5F, 19.3203F, -8.5587F, 1.0F, 1.0F, 4.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, -37.517F, 0.0F, -0.2443F, 0.5236F, 0.0F));

        ModelPartData cube_r120 = panelf4.addChild("cube_r120", ModelPartBuilder.create().uv(179, 122).cuboid(-0.5F, 13.8677F, -33.3321F, 1.0F, 1.0F, 10.0F, new Dilation(0.001F)), ModelTransform.of(-0.0632F, -37.4993F, -0.1115F, 0.2443F, 0.5236F, 0.0F));

        ModelPartData cube_r121 = panelf4.addChild("cube_r121", ModelPartBuilder.create().uv(34, 10).cuboid(-14.0F, -25.1225F, -22.0864F, 28.0F, 9.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, -37.517F, 0.0F, 1.5708F, 0.0F, 0.0F));

        ModelPartData cube_r122 = panelf4.addChild("cube_r122", ModelPartBuilder.create().uv(24, 146).cuboid(5.0F, -10.7115F, 21.5141F, 2.0F, 2.0F, 2.0F, new Dilation(0.15F))
                .uv(0, 54).cuboid(5.0F, -12.7115F, 21.5141F, 2.0F, 1.0F, 2.0F, new Dilation(0.25F))
                .uv(24, 146).cuboid(-7.0F, -10.7115F, 21.5141F, 2.0F, 2.0F, 2.0F, new Dilation(0.15F))
                .uv(0, 54).cuboid(-7.0F, -12.7115F, 21.5141F, 2.0F, 1.0F, 2.0F, new Dilation(0.25F)), ModelTransform.of(0.0F, -37.517F, 0.0F, 1.8326F, 0.0F, 3.1416F));

        ModelPartData cube_r123 = panelf4.addChild("cube_r123", ModelPartBuilder.create().uv(11, 240).cuboid(-4.0F, -28.3535F, 14.3237F, 8.0F, 5.0F, 0.0F, new Dilation(0.001F))
                .uv(50, 34).cuboid(-7.5F, -27.3535F, 13.3487F, 2.0F, 3.0F, 1.0F, new Dilation(0.001F))
                .uv(50, 34).cuboid(5.5F, -27.3535F, 13.3487F, 2.0F, 3.0F, 1.0F, new Dilation(0.001F))
                .uv(50, 34).cuboid(5.5F, -27.3535F, 13.3487F, 2.0F, 3.0F, 1.0F, new Dilation(0.001F))
                .uv(0, 46).cuboid(-14.0F, -29.8535F, 14.3487F, 28.0F, 8.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, -37.517F, 0.0F, 1.309F, 0.0F, 3.1416F));

        ModelPartData cube_r124 = panelf4.addChild("cube_r124", ModelPartBuilder.create().uv(4, 10).cuboid(-16.0F, 22.517F, -2.0F, 11.0F, 3.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -37.517F, 0.0F, 0.0F, -1.5708F, 0.0F));

        ModelPartData bone61 = panelf4.addChild("bone61", ModelPartBuilder.create(), ModelTransform.pivot(3.0F, -17.499F, -19.8488F));

        ModelPartData cube_r125 = bone61.addChild("cube_r125", ModelPartBuilder.create().uv(26, 17).cuboid(-3.5F, -24.8535F, 13.6987F, 1.0F, 1.0F, 1.0F, new Dilation(0.001F)), ModelTransform.of(-3.0F, -20.018F, 19.8488F, 1.309F, 0.0F, 3.1416F));

        ModelPartData bone60 = panelf4.addChild("bone60", ModelPartBuilder.create(), ModelTransform.pivot(-3.0F, -17.499F, -19.8488F));

        ModelPartData cube_r126 = bone60.addChild("cube_r126", ModelPartBuilder.create().uv(26, 17).cuboid(2.5F, -24.8535F, 13.6987F, 1.0F, 1.0F, 1.0F, new Dilation(0.001F)), ModelTransform.of(3.0F, -20.018F, 19.8488F, 1.309F, 0.0F, 3.1416F));

        ModelPartData antigravs = panelf4.addChild("antigravs", ModelPartBuilder.create(), ModelTransform.pivot(-1.25F, -17.6629F, -20.2032F));

        ModelPartData cube_r127 = antigravs.addChild("cube_r127", ModelPartBuilder.create().uv(27, 75).cuboid(0.75F, -25.1535F, 13.4487F, 1.0F, 1.0F, 1.0F, new Dilation(-0.1F)), ModelTransform.of(1.25F, -19.8542F, 20.2032F, 1.309F, 0.0F, 3.1416F));

        ModelPartData p1913 = panelf4.addChild("p1913", ModelPartBuilder.create(), ModelTransform.pivot(4.0F, -17.9647F, -17.8813F));

        ModelPartData cube_r128 = p1913.addChild("cube_r128", ModelPartBuilder.create().uv(54, 70).cuboid(-7.0F, -14.2115F, 21.5141F, 2.0F, 1.0F, 2.0F, new Dilation(0.001F))
                .uv(56, 63).cuboid(-7.0F, -13.7115F, 21.5141F, 2.0F, 5.0F, 2.0F, new Dilation(0.001F)), ModelTransform.of(-4.0F, -19.5523F, 17.8813F, 1.8326F, 0.0F, 3.1416F));

        ModelPartData power = panelf4.addChild("power", ModelPartBuilder.create(), ModelTransform.pivot(4.0F, -17.9647F, -17.8813F));

        ModelPartData cube_r129 = power.addChild("cube_r129", ModelPartBuilder.create().uv(54, 70).cuboid(5.0F, -14.2115F, 21.5141F, 2.0F, 1.0F, 2.0F, new Dilation(0.001F))
                .uv(56, 63).cuboid(5.0F, -13.7115F, 21.5141F, 2.0F, 5.0F, 2.0F, new Dilation(0.001F)), ModelTransform.of(-4.0F, -19.5523F, 17.8813F, 1.8326F, 0.0F, 3.1416F));

        ModelPartData panelf5 = bone7.addChild("panelf5", ModelPartBuilder.create().uv(107, 181).cuboid(-14.0F, -15.9306F, -25.1225F, 28.0F, 1.0F, 0.0F, new Dilation(0.001F))
                .uv(0, 255).cuboid(-14.0F, -15.4306F, -24.1225F, 28.0F, 1.0F, 0.0F, new Dilation(0.001F))
                .uv(26, 100).cuboid(-5.5F, -22.0F, -9.5F, 11.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(24, 100).cuboid(-0.5F, -21.5F, -8.5F, 1.0F, 0.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -4.0F, 0.0F, 0.0F, -0.5236F, 0.0F));

        ModelPartData cube_r130 = panelf5.addChild("cube_r130", ModelPartBuilder.create().uv(90, 4).cuboid(-14.0F, -21.8535F, 15.8487F, 28.0F, 14.0F, 0.0F, new Dilation(0.001F))
                .uv(0, 82).cuboid(-14.0F, -21.8535F, 16.3487F, 28.0F, 14.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, -37.517F, 0.0F, 1.3241F, -0.0003F, -3.1154F));

        ModelPartData cube_r131 = panelf5.addChild("cube_r131", ModelPartBuilder.create().uv(92, 89).cuboid(-14.0F, -7.0F, 0.0F, 28.0F, 14.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(-0.4906F, -18.9154F, -10.6313F, 1.3067F, -0.0067F, -3.1408F));

        ModelPartData cube_r132 = panelf5.addChild("cube_r132", ModelPartBuilder.create().uv(66, 240).cuboid(-10.0F, -25.9158F, 16.4292F, 20.0F, 4.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, -37.517F, 0.0F, 3.1416F, 0.0F, 0.0F));

        ModelPartData cube_r133 = panelf5.addChild("cube_r133", ModelPartBuilder.create().uv(29, 227).cuboid(-10.0F, -17.3951F, 21.5158F, 20.0F, 6.0F, 1.0F, new Dilation(0.001F))
                .uv(66, 244).cuboid(-10.0F, -16.8951F, 21.2658F, 20.0F, 5.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, -37.517F, 0.0F, 1.5708F, 0.0F, -3.1416F));

        ModelPartData cube_r134 = panelf5.addChild("cube_r134", ModelPartBuilder.create().uv(0, 236).cuboid(-11.0F, 19.5158F, 17.3951F, 22.0F, 2.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, -37.517F, 0.0F, -3.1416F, 0.0F, -3.1416F));

        ModelPartData cube_r135 = panelf5.addChild("cube_r135", ModelPartBuilder.create().uv(177, 121).cuboid(-0.5F, 29.2534F, -21.9032F, 1.0F, 1.0F, 11.0F, new Dilation(-0.001F)), ModelTransform.of(-0.0644F, -37.5334F, -0.1136F, -0.2618F, 0.5236F, 0.0F));

        ModelPartData cube_r136 = panelf5.addChild("cube_r136", ModelPartBuilder.create().uv(26, 181).cuboid(-0.5F, 21.5366F, -29.1165F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -37.517F, 0.0F, 0.0F, 0.5236F, 0.0F));

        ModelPartData cube_r137 = panelf5.addChild("cube_r137", ModelPartBuilder.create().uv(32, 27).cuboid(-0.5F, 19.3203F, -8.5587F, 1.0F, 1.0F, 4.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, -37.517F, 0.0F, -0.2443F, 0.5236F, 0.0F));

        ModelPartData cube_r138 = panelf5.addChild("cube_r138", ModelPartBuilder.create().uv(179, 122).cuboid(-0.5F, 13.8677F, -33.3321F, 1.0F, 1.0F, 10.0F, new Dilation(0.001F)), ModelTransform.of(-0.065F, -37.4993F, -0.1105F, 0.2443F, 0.5236F, 0.0F));

        ModelPartData cube_r139 = panelf5.addChild("cube_r139", ModelPartBuilder.create().uv(0, 247).cuboid(-14.0F, 24.1225F, 23.0864F, 28.0F, 1.0F, 0.0F, new Dilation(0.001F))
                .uv(0, 248).cuboid(-14.0F, 24.1225F, 22.0864F, 28.0F, 1.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, -37.517F, 0.0F, -1.5708F, 0.0F, 0.0F));

        ModelPartData cube_r140 = panelf5.addChild("cube_r140", ModelPartBuilder.create().uv(107, 181).cuboid(-14.0F, -23.5864F, -25.1225F, 28.0F, 1.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, -37.517F, 0.0F, 0.0F, 0.0F, -3.1416F));

        ModelPartData cube_r141 = panelf5.addChild("cube_r141", ModelPartBuilder.create().uv(34, 10).cuboid(-14.0F, -18.1619F, -29.2849F, 28.0F, 9.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, -37.517F, 0.0F, 1.309F, 0.0F, 0.0F));

        ModelPartData cube_r142 = panelf5.addChild("cube_r142", ModelPartBuilder.create().uv(183, 241).cuboid(-8.0F, -6.2872F, 19.353F, 16.0F, 3.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, -37.517F, 0.0F, 1.8326F, 0.0F, -3.1416F));

        ModelPartData cube_r143 = panelf5.addChild("cube_r143", ModelPartBuilder.create().uv(0, 27).mirrored().cuboid(17.9615F, -5.5966F, 14.3237F, 12.0F, 1.0F, 4.0F, new Dilation(0.001F)).mirrored(false), ModelTransform.of(0.0F, -37.517F, 0.0F, 1.0788F, 0.9909F, 2.7202F));

        ModelPartData cube_r144 = panelf5.addChild("cube_r144", ModelPartBuilder.create().uv(0, 27).cuboid(-29.9615F, -5.5966F, 14.3237F, 12.0F, 1.0F, 4.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, -37.517F, 0.0F, 1.0788F, -0.9909F, -2.7202F));

        ModelPartData cube_r145 = panelf5.addChild("cube_r145", ModelPartBuilder.create().uv(0, 32).cuboid(-5.0F, -18.8535F, 14.3237F, 10.0F, 1.0F, 4.0F, new Dilation(0.0F))
                .uv(0, 19).cuboid(-14.0F, -29.8535F, 14.3487F, 28.0F, 8.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, -37.517F, 0.0F, 1.309F, 0.0F, -3.1416F));

        ModelPartData cube_r146 = panelf5.addChild("cube_r146", ModelPartBuilder.create().uv(8, 54).cuboid(-2.0F, -22.995F, 15.7652F, 4.0F, 2.0F, 1.0F, new Dilation(0.001F))
                .uv(26, 17).cuboid(-3.8F, -22.495F, 16.2652F, 1.0F, 1.0F, 1.0F, new Dilation(0.001F))
                .uv(26, 17).cuboid(-6.2F, -22.495F, 16.2652F, 1.0F, 1.0F, 1.0F, new Dilation(0.001F))
                .uv(0, 27).cuboid(2.0F, -22.495F, 15.9652F, 1.0F, 1.0F, 1.0F, new Dilation(-0.2F))
                .uv(26, 17).cuboid(-2.1F, -24.995F, 16.4652F, 1.0F, 1.0F, 1.0F, new Dilation(0.2F))
                .uv(26, 17).cuboid(-0.525F, -19.495F, 16.2652F, 1.0F, 1.0F, 1.0F, new Dilation(0.001F))
                .uv(26, 17).cuboid(1.475F, -19.495F, 16.2652F, 1.0F, 1.0F, 1.0F, new Dilation(0.001F))
                .uv(26, 17).cuboid(3.475F, -19.495F, 16.2652F, 1.0F, 1.0F, 1.0F, new Dilation(0.001F))
                .uv(26, 17).cuboid(-2.525F, -19.495F, 16.2652F, 1.0F, 1.0F, 1.0F, new Dilation(0.001F))
                .uv(26, 17).cuboid(-4.525F, -19.495F, 16.2652F, 1.0F, 1.0F, 1.0F, new Dilation(0.001F))
                .uv(145, 232).cuboid(-7.5F, -27.245F, 16.2652F, 5.0F, 4.0F, 1.0F, new Dilation(-0.25F))
                .uv(143, 242).cuboid(-10.0F, -26.995F, 16.7652F, 20.0F, 10.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, -37.517F, 0.0F, 1.3963F, 0.0F, -3.1416F));

        ModelPartData cube_r147 = panelf5.addChild("cube_r147", ModelPartBuilder.create().uv(26, 17).cuboid(-0.35F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.2F)), ModelTransform.of(0.1843F, -16.2392F, -20.8577F, 1.3963F, 0.0F, -3.1416F));

        ModelPartData cube_r148 = panelf5.addChild("cube_r148", ModelPartBuilder.create().uv(23, 8).cuboid(-0.7F, 2.5F, 0.0F, 1.0F, 1.0F, 0.0F, new Dilation(0.001F))
                .uv(0, 15).cuboid(-0.2F, 1.5F, 0.0F, 1.0F, 1.0F, 0.0F, new Dilation(0.001F))
                .uv(2, 15).mirrored().cuboid(-0.3F, 0.5F, 0.0F, 1.0F, 1.0F, 0.0F, new Dilation(0.001F)).mirrored(false)
                .uv(0, 15).cuboid(-0.7F, -0.5F, 0.0F, 1.0F, 1.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(-7.3164F, -16.2798F, -21.8958F, 0.0F, -1.3963F, -1.5708F));

        ModelPartData cube_r149 = panelf5.addChild("cube_r149", ModelPartBuilder.create().uv(0, 16).cuboid(-0.5F, 0.0F, -0.5F, 1.0F, 0.0F, 1.0F, new Dilation(0.001F)), ModelTransform.of(-5.3286F, -17.7269F, -16.5699F, 1.2889F, 0.8967F, 2.919F));

        ModelPartData cube_r150 = panelf5.addChild("cube_r150", ModelPartBuilder.create().uv(0, 16).cuboid(-0.5F, 0.0F, -0.5F, 1.0F, 0.0F, 1.0F, new Dilation(0.001F)), ModelTransform.of(-4.3286F, -17.7356F, -16.57F, 1.3902F, -0.2577F, -3.0951F));

        ModelPartData cube_r151 = panelf5.addChild("cube_r151", ModelPartBuilder.create().uv(4, 10).cuboid(-16.0F, 22.517F, -2.0F, 11.0F, 3.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -37.517F, 0.0F, 0.0F, -1.5708F, 0.0F));

        ModelPartData rwf = panelf5.addChild("rwf", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, -37.517F, 0.0F));

        ModelPartData cube_r152 = rwf.addChild("cube_r152", ModelPartBuilder.create().uv(21, 17).cuboid(3.55F, -23.245F, 16.4652F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(21, 17).cuboid(5.0F, -23.245F, 16.4652F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 1.3963F, 0.0F, -3.1416F));

        ModelPartData cube_r153 = rwf.addChild("cube_r153", ModelPartBuilder.create().uv(26, 17).cuboid(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.2F)), ModelTransform.of(-1.6F, 20.961F, -21.1769F, 1.3963F, 0.0F, -3.1416F));

        ModelPartData bone56 = rwf.addChild("bone56", ModelPartBuilder.create(), ModelTransform.pivot(0.0343F, 21.2779F, -20.8577F));

        ModelPartData cube_r154 = bone56.addChild("cube_r154", ModelPartBuilder.create().uv(1, 16).cuboid(0.0F, -1.0F, -0.4F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0157F, -1.0497F, -0.1946F, -1.3264F, -0.7703F, -0.1719F));

        ModelPartData bone54 = rwf.addChild("bone54", ModelPartBuilder.create(), ModelTransform.pivot(-1.6F, 20.961F, -21.1769F));

        ModelPartData cube_r155 = bone54.addChild("cube_r155", ModelPartBuilder.create().uv(1, 16).cuboid(0.0F, -1.0F, -0.5F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -0.9356F, -0.165F, 1.3264F, -0.7703F, -2.9697F));

        ModelPartData bone55 = rwf.addChild("bone55", ModelPartBuilder.create(), ModelTransform.pivot(1.6F, 20.961F, -21.1769F));

        ModelPartData cube_r156 = bone55.addChild("cube_r156", ModelPartBuilder.create().uv(1, 16).cuboid(0.0F, -1.0F, -0.5F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -0.9356F, -0.165F, -1.3264F, 0.7703F, 0.1719F));

        ModelPartData alarms = panelf5.addChild("alarms", ModelPartBuilder.create(), ModelTransform.pivot(4.5F, -17.1871F, -18.7496F));

        ModelPartData cube_r157 = alarms.addChild("cube_r157", ModelPartBuilder.create().uv(26, 17).cuboid(-5.0F, -22.495F, 16.2652F, 1.0F, 1.0F, 1.0F, new Dilation(0.001F)), ModelTransform.of(-4.5F, -20.3299F, 18.7496F, 1.3963F, 0.0F, -3.1416F));

        ModelPartData increment = panelf5.addChild("increment", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, -17.5318F, -18.8104F));

        ModelPartData cube_r158 = increment.addChild("cube_r158", ModelPartBuilder.create().uv(53, 77).cuboid(-0.5F, -0.5F, -2.75F, 1.0F, 1.0F, 4.0F, new Dilation(-0.2F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 1.3963F, 0.0F, -3.1416F));

        ModelPartData door_lock = panelf5.addChild("door_lock", ModelPartBuilder.create(), ModelTransform.pivot(-1.5F, -17.5318F, -18.8104F));

        ModelPartData cube_r159 = door_lock.addChild("cube_r159", ModelPartBuilder.create().uv(64, 63).cuboid(-0.5F, -0.5F, -3.25F, 1.0F, 1.0F, 4.0F, new Dilation(-0.2F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 1.3963F, 0.0F, -3.1416F));

        ModelPartData doors = panelf5.addChild("doors", ModelPartBuilder.create(), ModelTransform.pivot(1.5F, -17.2856F, -18.767F));

        ModelPartData cube_r160 = doors.addChild("cube_r160", ModelPartBuilder.create().uv(64, 63).cuboid(-0.5F, -0.5F, -3.5F, 1.0F, 1.0F, 4.0F, new Dilation(-0.2F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 1.3963F, 0.0F, -3.1416F));

        ModelPartData thingy_for_later_use = panelf5.addChild("thingy_for_later_use", ModelPartBuilder.create(), ModelTransform.pivot(-7.5475F, -17.9618F, -14.144F));

        ModelPartData cube_r161 = thingy_for_later_use.addChild("cube_r161", ModelPartBuilder.create().uv(193, 127).cuboid(-0.5F, -3.0F, 12.0F, 2.0F, 1.0F, 2.0F, new Dilation(0.001F))
                .uv(191, 124).cuboid(-0.75F, -2.0F, 11.25F, 3.0F, 0.0F, 3.0F, new Dilation(0.001F))
                .uv(201, 127).cuboid(-0.5F, -4.0F, 12.0F, 2.0F, 2.0F, 2.0F, new Dilation(-0.2F))
                .uv(201, 127).cuboid(-0.5F, -3.0F, 12.0F, 2.0F, 1.0F, 2.0F, new Dilation(0.05F))
                .uv(199, 124).cuboid(0.0F, -4.5F, 12.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.1F))
                .uv(193, 127).cuboid(-0.5F, -4.0F, 12.0F, 2.0F, 2.0F, 2.0F, new Dilation(-0.25F))
                .uv(201, 124).cuboid(-0.5F, -4.25F, 12.0F, 2.0F, 1.0F, 2.0F, new Dilation(-0.15F)), ModelTransform.of(-6.9461F, 2.9643F, -10.9595F, 0.0F, 0.5236F, 0.0F));

        ModelPartData panelf6 = bone7.addChild("panelf6", ModelPartBuilder.create().uv(107, 181).cuboid(-14.0F, -15.9306F, -25.1225F, 28.0F, 1.0F, 0.0F, new Dilation(0.001F))
                .uv(0, 255).cuboid(-14.0F, -15.4306F, -24.1225F, 28.0F, 1.0F, 0.0F, new Dilation(0.001F))
                .uv(26, 100).cuboid(-5.5F, -22.0F, -9.5F, 11.0F, 1.0F, 1.0F, new Dilation(0.01F))
                .uv(24, 100).cuboid(-0.5F, -21.5F, -8.5F, 1.0F, 0.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -4.0F, 0.0F, 0.0F, 0.5236F, 0.0F));

        ModelPartData cube_r162 = panelf6.addChild("cube_r162", ModelPartBuilder.create().uv(90, 4).cuboid(-14.0F, -21.8535F, 15.8487F, 28.0F, 14.0F, 0.0F, new Dilation(0.001F))
                .uv(92, 89).cuboid(-14.0F, -21.8535F, 15.4487F, 28.0F, 14.0F, 0.0F, new Dilation(0.001F))
                .uv(0, 82).cuboid(-14.0F, -21.8535F, 16.3487F, 28.0F, 14.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, -37.517F, 0.0F, 1.3241F, -0.0002F, -3.1329F));

        ModelPartData cube_r163 = panelf6.addChild("cube_r163", ModelPartBuilder.create().uv(66, 240).cuboid(-10.0F, -25.9158F, 16.4292F, 20.0F, 4.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, -37.517F, 0.0F, 3.1416F, 0.0F, 0.0F));

        ModelPartData cube_r164 = panelf6.addChild("cube_r164", ModelPartBuilder.create().uv(29, 227).cuboid(-10.0F, -17.3951F, 21.5158F, 20.0F, 6.0F, 1.0F, new Dilation(0.0F))
                .uv(66, 244).cuboid(-10.0F, -16.8951F, 21.2658F, 20.0F, 5.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, -37.517F, 0.0F, 1.5708F, 0.0F, 3.1416F));

        ModelPartData cube_r165 = panelf6.addChild("cube_r165", ModelPartBuilder.create().uv(0, 238).cuboid(-11.0F, 19.5158F, 17.3951F, 22.0F, 2.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, -37.517F, 0.0F, -3.1416F, 0.0F, 3.1416F));

        ModelPartData cube_r166 = panelf6.addChild("cube_r166", ModelPartBuilder.create().uv(177, 121).cuboid(-0.5F, 29.2534F, -21.9032F, 1.0F, 1.0F, 11.0F, new Dilation(-0.001F)), ModelTransform.of(-0.0644F, -37.5334F, -0.1136F, -0.2618F, 0.5236F, 0.0F));

        ModelPartData cube_r167 = panelf6.addChild("cube_r167", ModelPartBuilder.create().uv(26, 181).cuboid(-0.5F, 21.5366F, -29.1165F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -37.517F, 0.0F, 0.0F, 0.5236F, 0.0F));

        ModelPartData cube_r168 = panelf6.addChild("cube_r168", ModelPartBuilder.create().uv(32, 27).cuboid(-0.5F, 19.3203F, -8.5587F, 1.0F, 1.0F, 4.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, -37.517F, 0.0F, -0.2443F, 0.5236F, 0.0F));

        ModelPartData cube_r169 = panelf6.addChild("cube_r169", ModelPartBuilder.create().uv(179, 122).cuboid(-0.5F, 13.8677F, -33.3321F, 1.0F, 1.0F, 10.0F, new Dilation(0.001F)), ModelTransform.of(-0.065F, -37.4993F, -0.1105F, 0.2443F, 0.5236F, 0.0F));

        ModelPartData cube_r170 = panelf6.addChild("cube_r170", ModelPartBuilder.create().uv(0, 247).cuboid(-14.0F, 24.1225F, 23.0864F, 28.0F, 1.0F, 0.0F, new Dilation(0.001F))
                .uv(0, 248).cuboid(-14.0F, 24.1225F, 22.0864F, 28.0F, 1.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, -37.517F, 0.0F, -1.5708F, 0.0F, 0.0F));

        ModelPartData cube_r171 = panelf6.addChild("cube_r171", ModelPartBuilder.create().uv(107, 181).cuboid(-14.0F, -23.5864F, -25.1225F, 28.0F, 1.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, -37.517F, 0.0F, 0.0F, 0.0F, 3.1416F));

        ModelPartData cube_r172 = panelf6.addChild("cube_r172", ModelPartBuilder.create().uv(34, 10).cuboid(-14.0F, -18.1619F, -29.2849F, 28.0F, 9.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, -37.517F, 0.0F, 1.309F, 0.0F, 0.0F));

        ModelPartData cube_r173 = panelf6.addChild("cube_r173", ModelPartBuilder.create().uv(183, 241).cuboid(-8.0F, -6.2872F, 19.353F, 16.0F, 3.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, -37.517F, 0.0F, 1.8326F, 0.0F, 3.1416F));

        ModelPartData cube_r174 = panelf6.addChild("cube_r174", ModelPartBuilder.create().uv(65, 112).cuboid(-29.4795F, -4.1636F, 14.3237F, 13.0F, 1.0F, 1.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, -37.517F, 0.0F, 1.0788F, -0.9909F, -2.7202F));

        ModelPartData cube_r175 = panelf6.addChild("cube_r175", ModelPartBuilder.create().uv(65, 112).cuboid(16.4795F, -4.1636F, 14.3237F, 13.0F, 1.0F, 1.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, -37.517F, 0.0F, 1.0788F, 0.9909F, 2.7202F));

        ModelPartData cube_r176 = panelf6.addChild("cube_r176", ModelPartBuilder.create().uv(0, 8).cuboid(-5.5F, -16.8535F, 14.3237F, 11.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(0, 37).cuboid(-5.0F, -24.8535F, 13.3487F, 10.0F, 0.0F, 1.0F, new Dilation(0.0F))
                .uv(63, 77).cuboid(-2.75F, -29.8535F, 14.2487F, 6.0F, 14.0F, 0.0F, new Dilation(0.001F))
                .uv(20, 74).cuboid(-8.0F, -26.3535F, 13.8487F, 2.0F, 2.0F, 1.0F, new Dilation(-0.25F))
                .uv(0, 57).cuboid(-14.0F, -29.8535F, 14.3487F, 28.0F, 14.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, -37.517F, 0.0F, 1.309F, 0.0F, 3.1416F));

        ModelPartData cube_r177 = panelf6.addChild("cube_r177", ModelPartBuilder.create().uv(32, 36).cuboid(-0.5F, -1.0F, -0.5F, 1.0F, 0.0F, 1.0F, new Dilation(0.0F))
                .uv(32, 36).cuboid(-0.5F, -1.75F, -0.5F, 1.0F, 0.0F, 1.0F, new Dilation(0.0F))
                .uv(32, 36).cuboid(-0.5F, -2.5F, -0.5F, 1.0F, 0.0F, 1.0F, new Dilation(0.0F))
                .uv(32, 36).cuboid(-0.5F, -3.25F, -0.5F, 1.0F, 0.0F, 1.0F, new Dilation(0.0F))
                .uv(32, 36).cuboid(-0.5F, -4.0F, -0.5F, 1.0F, 0.0F, 1.0F, new Dilation(0.0F))
                .uv(32, 36).cuboid(-0.5F, -4.75F, -0.5F, 1.0F, 0.0F, 1.0F, new Dilation(0.0F))
                .uv(32, 36).cuboid(-0.5F, 1.0F, -0.5F, 1.0F, 0.0F, 1.0F, new Dilation(0.0F))
                .uv(32, 36).cuboid(-0.5F, 1.75F, -0.5F, 1.0F, 0.0F, 1.0F, new Dilation(0.0F))
                .uv(32, 36).cuboid(-0.5F, 2.5F, -0.5F, 1.0F, 0.0F, 1.0F, new Dilation(0.0F))
                .uv(32, 36).cuboid(-0.5F, 3.25F, -0.5F, 1.0F, 0.0F, 1.0F, new Dilation(0.0F))
                .uv(32, 36).cuboid(-0.5F, 4.0F, -0.5F, 1.0F, 0.0F, 1.0F, new Dilation(0.0F))
                .uv(32, 36).cuboid(-0.5F, 4.75F, -0.5F, 1.0F, 0.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -17.3195F, -21.8712F, 0.0F, 1.309F, 1.5708F));

        ModelPartData cube_r178 = panelf6.addChild("cube_r178", ModelPartBuilder.create().uv(25, 17).mirrored().cuboid(-12.5F, -0.5F, 0.0F, 1.0F, 1.0F, 0.0F, new Dilation(0.001F)).mirrored(false)
                .uv(25, 17).cuboid(-0.5F, -0.5F, 0.0F, 1.0F, 1.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(-6.1688F, -18.0812F, -16.4526F, 1.309F, 0.0F, 3.1416F));

        ModelPartData cube_r179 = panelf6.addChild("cube_r179", ModelPartBuilder.create().uv(4, 10).cuboid(-16.0F, 22.517F, -2.0F, 11.0F, 3.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -37.517F, 0.0F, 0.0F, -1.5708F, 0.0F));

        ModelPartData waypoint_slot = panelf6.addChild("waypoint_slot", ModelPartBuilder.create(), ModelTransform.pivot(-0.1584F, -19.261F, -13.5796F));

        ModelPartData cube_r180 = waypoint_slot.addChild("cube_r180", ModelPartBuilder.create().uv(18, 54).cuboid(-1.0F, -1.0F, -0.45F, 2.0F, 2.0F, 1.0F, new Dilation(-0.3F)), ModelTransform.of(0.15F, 0.0F, 0.0F, 1.2086F, 0.7519F, 2.8883F));

        ModelPartData thing = panelf6.addChild("thing", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, -37.517F, 0.0F));

        ModelPartData cube_r181 = thing.addChild("cube_r181", ModelPartBuilder.create().uv(20, 71).cuboid(-7.5F, -25.8535F, 12.6987F, 1.0F, 1.0F, 2.0F, new Dilation(-0.25F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 1.309F, 0.0F, 3.1416F));

        ModelPartData cube_r182 = thing.addChild("cube_r182", ModelPartBuilder.create().uv(26, 72).cuboid(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.15F)), ModelTransform.of(7.0F, 19.0694F, -21.1382F, 1.1781F, 0.0F, 3.1416F));

        ModelPartData cube_r183 = thing.addChild("cube_r183", ModelPartBuilder.create().uv(26, 74).cuboid(-0.5F, -0.5F, -1.75F, 1.0F, 1.0F, 2.0F, new Dilation(-0.25F)), ModelTransform.of(7.0F, 19.0694F, -21.1382F, 1.0472F, 0.0F, 3.1416F));

        ModelPartData mark_waypoint = panelf6.addChild("mark_waypoint", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, -37.517F, 0.0F));

        ModelPartData cube_r184 = mark_waypoint.addChild("cube_r184", ModelPartBuilder.create().uv(101, 112).cuboid(-1.25F, 1.25F, -1.1F, 2.0F, 1.0F, 0.0F, new Dilation(0.001F))
                .uv(100, 113).cuboid(-1.0F, 1.0F, -1.0F, 2.0F, 2.0F, 1.0F, new Dilation(0.001F))
                .uv(107, 117).cuboid(-1.0F, -1.5F, -0.5F, 2.0F, 4.0F, 2.0F, new Dilation(0.001F)), ModelTransform.of(-3.8F, 19.1276F, -15.9462F, 0.9163F, 0.0F, 3.1416F));

        ModelPartData cube_r185 = mark_waypoint.addChild("cube_r185", ModelPartBuilder.create().uv(101, 107).cuboid(-1.0F, 0.0F, 0.0F, 1.0F, 2.0F, 1.0F, new Dilation(0.001F)), ModelTransform.of(-4.3797F, 17.7701F, -10.251F, 2.9228F, -0.0435F, 3.1226F));

        ModelPartData cube_r186 = mark_waypoint.addChild("cube_r186", ModelPartBuilder.create().uv(101, 110).cuboid(-1.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-4.4261F, 17.1978F, -11.0697F, 2.1811F, -0.0435F, 3.1226F));

        ModelPartData cube_r187 = mark_waypoint.addChild("cube_r187", ModelPartBuilder.create().uv(101, 110).cuboid(-1.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, new Dilation(0.001F)), ModelTransform.of(-4.4711F, 17.1121F, -12.065F, 1.6575F, -0.0435F, 3.1226F));

        ModelPartData cube_r188 = mark_waypoint.addChild("cube_r188", ModelPartBuilder.create().uv(101, 110).cuboid(-0.5F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-3.8872F, 17.0208F, -13.014F, 1.6577F, 0.0869F, 3.134F));

        ModelPartData cube_r189 = mark_waypoint.addChild("cube_r189", ModelPartBuilder.create().uv(101, 110).cuboid(-0.5F, 0.0F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.001F)), ModelTransform.of(-3.8F, 17.6057F, -13.9628F, 1.4832F, 0.0869F, 3.134F));

        ModelPartData bone63 = mark_waypoint.addChild("bone63", ModelPartBuilder.create(), ModelTransform.pivot(-3.8F, 18.7309F, -16.2506F));

        ModelPartData cube_r190 = bone63.addChild("cube_r190", ModelPartBuilder.create().uv(26, 17).cuboid(-0.5F, -0.5F, -1.0F, 1.0F, 1.0F, 1.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 0.3967F, 0.3044F, 0.9163F, 0.0F, 3.1416F));

        ModelPartData set_waypoint = panelf6.addChild("set_waypoint", ModelPartBuilder.create(), ModelTransform.pivot(1.3755F, -19.6406F, -14.0295F));

        ModelPartData cube_r191 = set_waypoint.addChild("cube_r191", ModelPartBuilder.create().uv(101, 112).cuboid(-1.25F, 1.25F, -1.1F, 2.0F, 1.0F, 0.0F, new Dilation(0.001F))
                .uv(100, 113).cuboid(-1.0F, 1.0F, -1.0F, 2.0F, 2.0F, 1.0F, new Dilation(0.001F))
                .uv(99, 117).cuboid(-1.0F, -1.5F, -0.5F, 2.0F, 4.0F, 2.0F, new Dilation(0.001F)), ModelTransform.of(2.4245F, 1.2512F, -1.9167F, 0.9163F, 0.0F, 3.1416F));

        ModelPartData cube_r192 = set_waypoint.addChild("cube_r192", ModelPartBuilder.create().uv(101, 107).cuboid(-1.0F, 0.0F, 0.0F, 1.0F, 2.0F, 1.0F, new Dilation(0.001F)), ModelTransform.of(1.8448F, -0.1063F, 3.7786F, 2.9228F, -0.0435F, 3.1226F));

        ModelPartData cube_r193 = set_waypoint.addChild("cube_r193", ModelPartBuilder.create().uv(101, 110).cuboid(-1.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(1.7983F, -0.6786F, 2.9598F, 2.1811F, -0.0435F, 3.1226F));

        ModelPartData cube_r194 = set_waypoint.addChild("cube_r194", ModelPartBuilder.create().uv(101, 110).cuboid(-1.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, new Dilation(0.001F)), ModelTransform.of(1.7534F, -0.7644F, 1.9645F, 1.6575F, -0.0435F, 3.1226F));

        ModelPartData cube_r195 = set_waypoint.addChild("cube_r195", ModelPartBuilder.create().uv(101, 110).cuboid(-0.5F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(2.3373F, -0.8557F, 1.0155F, 1.6577F, 0.0869F, 3.134F));

        ModelPartData cube_r196 = set_waypoint.addChild("cube_r196", ModelPartBuilder.create().uv(101, 110).cuboid(-0.5F, 0.0F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.001F)), ModelTransform.of(2.4245F, -0.2707F, 0.0667F, 1.4832F, 0.0869F, 3.134F));

        ModelPartData bone62 = set_waypoint.addChild("bone62", ModelPartBuilder.create(), ModelTransform.pivot(2.4245F, 0.8545F, -2.2211F));

        ModelPartData cube_r197 = bone62.addChild("cube_r197", ModelPartBuilder.create().uv(26, 17).cuboid(-0.5F, -0.5F, -1.0F, 1.0F, 1.0F, 1.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 0.3967F, 0.3044F, 0.9163F, 0.0F, 3.1416F));

        ModelPartData twist = panelf6.addChild("twist", ModelPartBuilder.create(), ModelTransform.pivot(-8.4583F, -17.7197F, -19.7158F));

        ModelPartData cube_r198 = twist.addChild("cube_r198", ModelPartBuilder.create().uv(0, 32).cuboid(-0.5F, -0.6F, -0.7F, 1.0F, 1.0F, 1.0F, new Dilation(-0.25F))
                .uv(30, 12).cuboid(-0.3F, -0.6F, -0.19F, 1.0F, 1.0F, 1.0F, new Dilation(-0.25F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 1.2086F, 0.7519F, 2.8883F));

        ModelPartData cube_r199 = twist.addChild("cube_r199", ModelPartBuilder.create().uv(0, 30).cuboid(-0.5F, -0.4F, 0.0F, 1.0F, 1.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(-0.2143F, 0.0858F, -0.1221F, 1.2086F, 0.7519F, 2.8883F));

        ModelPartData twist3 = panelf6.addChild("twist3", ModelPartBuilder.create(), ModelTransform.pivot(8.5417F, -17.6197F, -19.6658F));

        ModelPartData cube_r200 = twist3.addChild("cube_r200", ModelPartBuilder.create().uv(0, 32).cuboid(-0.5F, -0.6F, -0.7F, 1.0F, 1.0F, 1.0F, new Dilation(-0.25F))
                .uv(30, 12).cuboid(-0.3F, -0.6F, -0.19F, 1.0F, 1.0F, 1.0F, new Dilation(-0.25F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 1.2086F, 0.7519F, 2.8883F));

        ModelPartData cube_r201 = twist3.addChild("cube_r201", ModelPartBuilder.create().uv(0, 30).cuboid(-0.5F, -0.4F, 0.0F, 1.0F, 1.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(-0.2143F, 0.0859F, -0.1221F, 1.2086F, 0.7519F, 2.8883F));

        ModelPartData twist4 = panelf6.addChild("twist4", ModelPartBuilder.create(), ModelTransform.pivot(8.8917F, -17.2697F, -21.2658F));

        ModelPartData cube_r202 = twist4.addChild("cube_r202", ModelPartBuilder.create().uv(0, 32).cuboid(-0.5F, -0.6F, -0.7F, 1.0F, 1.0F, 1.0F, new Dilation(-0.25F))
                .uv(30, 12).cuboid(-0.3F, -0.6F, -0.19F, 1.0F, 1.0F, 1.0F, new Dilation(-0.25F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 1.2086F, 0.7519F, 2.8883F));

        ModelPartData cube_r203 = twist4.addChild("cube_r203", ModelPartBuilder.create().uv(0, 30).cuboid(-0.5F, -0.4F, 0.0F, 1.0F, 1.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(-0.2143F, 0.0859F, -0.1221F, 1.2086F, 0.7519F, 2.8883F));

        ModelPartData twist2 = panelf6.addChild("twist2", ModelPartBuilder.create(), ModelTransform.of(-9.0538F, -16.3367F, -21.3145F, 0.0668F, -0.6516F, -0.1098F));

        ModelPartData cube_r204 = twist2.addChild("cube_r204", ModelPartBuilder.create().uv(0, 32).cuboid(-0.5F, -0.6F, -0.7F, 1.0F, 1.0F, 1.0F, new Dilation(-0.25F))
                .uv(30, 12).cuboid(-0.3F, -0.6F, -0.19F, 1.0F, 1.0F, 1.0F, new Dilation(-0.25F)), ModelTransform.of(0.0955F, -1.083F, 0.0987F, 1.2109F, 0.7538F, 2.901F));

        ModelPartData cube_r205 = twist2.addChild("cube_r205", ModelPartBuilder.create().uv(0, 30).cuboid(-0.5F, -0.4F, 0.0F, 1.0F, 1.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(-0.1188F, -0.9972F, -0.0234F, 1.2109F, 0.7538F, 2.901F));

        ModelPartData rotor = bone7.addChild("rotor", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData rotorrings = rotor.addChild("rotorrings", ModelPartBuilder.create().uv(38, 117).cuboid(0.0F, -14.0F, -1.0F, 0.0F, 24.0F, 2.0F, new Dilation(0.0F))
                .uv(45, 160).cuboid(-1.5F, -3.5F, -1.5F, 3.0F, 3.0F, 3.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -46.0F, 0.0F));

        ModelPartData cube_r206 = rotorrings.addChild("cube_r206", ModelPartBuilder.create().uv(42, 117).cuboid(0.0F, -18.483F, -1.0F, 0.0F, 24.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 4.483F, 0.0F, 0.0F, 1.5708F, 0.0F));

        ModelPartData bone4 = rotorrings.addChild("bone4", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 4.0F, 0.0F));

        ModelPartData cube_r207 = bone4.addChild("cube_r207", ModelPartBuilder.create().uv(207, 232).cuboid(-2.0F, 5.517F, -3.0F, 4.0F, 0.0F, 3.0F, new Dilation(0.01F))
                .uv(207, 232).cuboid(-2.0F, -18.483F, -3.0F, 4.0F, 0.0F, 3.0F, new Dilation(0.01F))
                .uv(217, 233).cuboid(-2.0F, 1.517F, -3.5F, 4.0F, 0.0F, 4.0F, new Dilation(0.01F))
                .uv(217, 233).cuboid(-2.0F, 3.517F, -3.5F, 4.0F, 0.0F, 4.0F, new Dilation(0.01F))
                .uv(217, 233).cuboid(-2.0F, -14.483F, -3.5F, 4.0F, 0.0F, 4.0F, new Dilation(0.01F))
                .uv(217, 233).cuboid(-2.0F, -16.483F, -3.5F, 4.0F, 0.0F, 4.0F, new Dilation(0.01F))
                .uv(244, 247).cuboid(-2.0F, -0.483F, -4.0F, 4.0F, 0.0F, 4.0F, new Dilation(0.01F))
                .uv(244, 247).cuboid(-2.0F, -2.483F, -4.0F, 4.0F, 0.0F, 4.0F, new Dilation(0.01F))
                .uv(244, 247).cuboid(-2.0F, -10.483F, -4.0F, 4.0F, 0.0F, 4.0F, new Dilation(0.01F))
                .uv(244, 247).cuboid(-2.0F, -12.483F, -4.0F, 4.0F, 0.0F, 4.0F, new Dilation(0.01F))
                .uv(233, 251).cuboid(-2.5F, -4.483F, -4.5F, 5.0F, 0.0F, 5.0F, new Dilation(0.01F))
                .uv(234, 116).cuboid(0.0F, -18.983F, -5.5F, 0.0F, 25.0F, 11.0F, new Dilation(0.01F))
                .uv(212, 243).cuboid(-2.5F, -6.483F, -4.5F, 5.0F, 0.0F, 5.0F, new Dilation(0.01F))
                .uv(233, 251).cuboid(-2.5F, -8.483F, -4.5F, 5.0F, 0.0F, 5.0F, new Dilation(0.01F)), ModelTransform.of(0.0F, 0.483F, 0.0F, 0.0F, 1.5708F, 0.0F));

        ModelPartData cube_r208 = bone4.addChild("cube_r208", ModelPartBuilder.create().uv(234, 141).cuboid(0.0F, -18.983F, -5.5F, 0.0F, 25.0F, 11.0F, new Dilation(0.01F)), ModelTransform.of(0.0F, 0.483F, 0.0F, 3.1416F, 1.0472F, -3.1416F));

        ModelPartData cube_r209 = bone4.addChild("cube_r209", ModelPartBuilder.create().uv(234, 141).cuboid(0.0F, -18.983F, -5.5F, 0.0F, 25.0F, 11.0F, new Dilation(0.01F)), ModelTransform.of(0.0F, 0.483F, 0.0F, 3.1416F, 0.0F, 3.1416F));

        ModelPartData cube_r210 = bone4.addChild("cube_r210", ModelPartBuilder.create().uv(234, 141).cuboid(0.0F, -18.983F, -5.5F, 0.0F, 25.0F, 11.0F, new Dilation(0.01F)), ModelTransform.of(0.0F, 0.483F, 0.0F, -3.1416F, -1.0472F, 3.1416F));

        ModelPartData cube_r211 = bone4.addChild("cube_r211", ModelPartBuilder.create().uv(234, 116).cuboid(0.0F, -18.983F, -5.5F, 0.0F, 25.0F, 11.0F, new Dilation(0.01F)), ModelTransform.of(0.0F, 0.483F, 0.0F, -3.1416F, -0.5236F, -3.1416F));

        ModelPartData cube_r212 = bone4.addChild("cube_r212", ModelPartBuilder.create().uv(234, 116).cuboid(0.0F, -18.983F, -5.5F, 0.0F, 25.0F, 11.0F, new Dilation(0.01F)), ModelTransform.of(0.0F, 0.483F, 0.0F, -3.1416F, 0.5236F, 3.1416F));

        ModelPartData bone45 = rotorrings.addChild("bone45", ModelPartBuilder.create(), ModelTransform.of(0.0F, 4.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData cube_r213 = bone45.addChild("cube_r213", ModelPartBuilder.create().uv(207, 232).cuboid(-2.0F, 5.517F, -3.0F, 4.0F, 0.0F, 3.0F, new Dilation(0.01F))
                .uv(207, 232).cuboid(-2.0F, -18.483F, -3.0F, 4.0F, 0.0F, 3.0F, new Dilation(0.01F))
                .uv(217, 233).cuboid(-2.0F, 1.517F, -3.5F, 4.0F, 0.0F, 4.0F, new Dilation(0.01F))
                .uv(217, 233).cuboid(-2.0F, 3.517F, -3.5F, 4.0F, 0.0F, 4.0F, new Dilation(0.01F))
                .uv(217, 233).cuboid(-2.0F, -14.483F, -3.5F, 4.0F, 0.0F, 4.0F, new Dilation(0.01F))
                .uv(217, 233).cuboid(-2.0F, -16.483F, -3.5F, 4.0F, 0.0F, 4.0F, new Dilation(0.01F))
                .uv(244, 247).cuboid(-2.0F, -0.483F, -4.0F, 4.0F, 0.0F, 4.0F, new Dilation(0.01F))
                .uv(244, 247).cuboid(-2.0F, -2.483F, -4.0F, 4.0F, 0.0F, 4.0F, new Dilation(0.01F))
                .uv(244, 247).cuboid(-2.0F, -10.483F, -4.0F, 4.0F, 0.0F, 4.0F, new Dilation(0.01F))
                .uv(244, 247).cuboid(-2.0F, -12.483F, -4.0F, 4.0F, 0.0F, 4.0F, new Dilation(0.01F))
                .uv(233, 251).cuboid(-2.5F, -4.483F, -4.5F, 5.0F, 0.0F, 5.0F, new Dilation(0.01F))
                .uv(212, 243).cuboid(-2.5F, -6.483F, -4.5F, 5.0F, 0.0F, 5.0F, new Dilation(0.01F))
                .uv(233, 251).cuboid(-2.5F, -8.483F, -4.5F, 5.0F, 0.0F, 5.0F, new Dilation(0.01F)), ModelTransform.of(0.0F, 0.483F, 0.0F, 0.0F, 1.5708F, 0.0F));

        ModelPartData bone46 = rotorrings.addChild("bone46", ModelPartBuilder.create(), ModelTransform.of(0.0F, 4.0F, 0.0F, 0.0F, 2.0944F, 0.0F));

        ModelPartData cube_r214 = bone46.addChild("cube_r214", ModelPartBuilder.create().uv(207, 232).cuboid(-2.0F, 5.517F, -3.0F, 4.0F, 0.0F, 3.0F, new Dilation(0.01F))
                .uv(207, 232).cuboid(-2.0F, -18.483F, -3.0F, 4.0F, 0.0F, 3.0F, new Dilation(0.01F))
                .uv(217, 233).cuboid(-2.0F, 1.517F, -3.5F, 4.0F, 0.0F, 4.0F, new Dilation(0.01F))
                .uv(217, 233).cuboid(-2.0F, 3.517F, -3.5F, 4.0F, 0.0F, 4.0F, new Dilation(0.01F))
                .uv(217, 233).cuboid(-2.0F, -14.483F, -3.5F, 4.0F, 0.0F, 4.0F, new Dilation(0.01F))
                .uv(217, 233).cuboid(-2.0F, -16.483F, -3.5F, 4.0F, 0.0F, 4.0F, new Dilation(0.01F))
                .uv(244, 247).cuboid(-2.0F, -0.483F, -4.0F, 4.0F, 0.0F, 4.0F, new Dilation(0.01F))
                .uv(244, 247).cuboid(-2.0F, -2.483F, -4.0F, 4.0F, 0.0F, 4.0F, new Dilation(0.01F))
                .uv(244, 247).cuboid(-2.0F, -10.483F, -4.0F, 4.0F, 0.0F, 4.0F, new Dilation(0.01F))
                .uv(244, 247).cuboid(-2.0F, -12.483F, -4.0F, 4.0F, 0.0F, 4.0F, new Dilation(0.01F))
                .uv(233, 251).cuboid(-2.5F, -4.483F, -4.5F, 5.0F, 0.0F, 5.0F, new Dilation(0.01F))
                .uv(212, 243).cuboid(-2.5F, -6.483F, -4.5F, 5.0F, 0.0F, 5.0F, new Dilation(0.01F))
                .uv(233, 251).cuboid(-2.5F, -8.483F, -4.5F, 5.0F, 0.0F, 5.0F, new Dilation(0.01F)), ModelTransform.of(0.0F, 0.483F, 0.0F, 0.0F, 1.5708F, 0.0F));

        ModelPartData bone47 = rotorrings.addChild("bone47", ModelPartBuilder.create(), ModelTransform.of(0.0F, 4.0F, 0.0F, 0.0F, 3.1416F, 0.0F));

        ModelPartData cube_r215 = bone47.addChild("cube_r215", ModelPartBuilder.create().uv(207, 232).cuboid(-2.0F, 5.517F, -3.0F, 4.0F, 0.0F, 3.0F, new Dilation(0.01F))
                .uv(207, 232).cuboid(-2.0F, -18.483F, -3.0F, 4.0F, 0.0F, 3.0F, new Dilation(0.01F))
                .uv(217, 233).cuboid(-2.0F, 1.517F, -3.5F, 4.0F, 0.0F, 4.0F, new Dilation(0.01F))
                .uv(217, 233).cuboid(-2.0F, 3.517F, -3.5F, 4.0F, 0.0F, 4.0F, new Dilation(0.01F))
                .uv(217, 233).cuboid(-2.0F, -14.483F, -3.5F, 4.0F, 0.0F, 4.0F, new Dilation(0.01F))
                .uv(217, 233).cuboid(-2.0F, -16.483F, -3.5F, 4.0F, 0.0F, 4.0F, new Dilation(0.01F))
                .uv(244, 247).cuboid(-2.0F, -0.483F, -4.0F, 4.0F, 0.0F, 4.0F, new Dilation(0.01F))
                .uv(244, 247).cuboid(-2.0F, -2.483F, -4.0F, 4.0F, 0.0F, 4.0F, new Dilation(0.01F))
                .uv(244, 247).cuboid(-2.0F, -10.483F, -4.0F, 4.0F, 0.0F, 4.0F, new Dilation(0.01F))
                .uv(244, 247).cuboid(-2.0F, -12.483F, -4.0F, 4.0F, 0.0F, 4.0F, new Dilation(0.01F))
                .uv(233, 251).cuboid(-2.5F, -4.483F, -4.5F, 5.0F, 0.0F, 5.0F, new Dilation(0.01F))
                .uv(212, 243).cuboid(-2.5F, -6.483F, -4.5F, 5.0F, 0.0F, 5.0F, new Dilation(0.01F))
                .uv(233, 251).cuboid(-2.5F, -8.483F, -4.5F, 5.0F, 0.0F, 5.0F, new Dilation(0.01F)), ModelTransform.of(0.0F, 0.483F, 0.0F, 0.0F, 1.5708F, 0.0F));

        ModelPartData bone48 = rotorrings.addChild("bone48", ModelPartBuilder.create(), ModelTransform.of(0.0F, 4.0F, 0.0F, 0.0F, -2.0944F, 0.0F));

        ModelPartData cube_r216 = bone48.addChild("cube_r216", ModelPartBuilder.create().uv(207, 232).cuboid(-2.0F, 5.517F, -3.0F, 4.0F, 0.0F, 3.0F, new Dilation(0.01F))
                .uv(207, 232).cuboid(-2.0F, -18.483F, -3.0F, 4.0F, 0.0F, 3.0F, new Dilation(0.01F))
                .uv(217, 233).cuboid(-2.0F, 1.517F, -3.5F, 4.0F, 0.0F, 4.0F, new Dilation(0.01F))
                .uv(217, 233).cuboid(-2.0F, 3.517F, -3.5F, 4.0F, 0.0F, 4.0F, new Dilation(0.01F))
                .uv(217, 233).cuboid(-2.0F, -14.483F, -3.5F, 4.0F, 0.0F, 4.0F, new Dilation(0.01F))
                .uv(217, 233).cuboid(-2.0F, -16.483F, -3.5F, 4.0F, 0.0F, 4.0F, new Dilation(0.01F))
                .uv(244, 247).cuboid(-2.0F, -0.483F, -4.0F, 4.0F, 0.0F, 4.0F, new Dilation(0.01F))
                .uv(244, 247).cuboid(-2.0F, -2.483F, -4.0F, 4.0F, 0.0F, 4.0F, new Dilation(0.01F))
                .uv(244, 247).cuboid(-2.0F, -10.483F, -4.0F, 4.0F, 0.0F, 4.0F, new Dilation(0.01F))
                .uv(244, 247).cuboid(-2.0F, -12.483F, -4.0F, 4.0F, 0.0F, 4.0F, new Dilation(0.01F))
                .uv(233, 251).cuboid(-2.5F, -4.483F, -4.5F, 5.0F, 0.0F, 5.0F, new Dilation(0.01F))
                .uv(212, 243).cuboid(-2.5F, -6.483F, -4.5F, 5.0F, 0.0F, 5.0F, new Dilation(0.01F))
                .uv(233, 251).cuboid(-2.5F, -8.483F, -4.5F, 5.0F, 0.0F, 5.0F, new Dilation(0.01F)), ModelTransform.of(0.0F, 0.483F, 0.0F, 0.0F, 1.5708F, 0.0F));

        ModelPartData bone49 = rotorrings.addChild("bone49", ModelPartBuilder.create(), ModelTransform.of(0.0F, 4.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData cube_r217 = bone49.addChild("cube_r217", ModelPartBuilder.create().uv(207, 232).cuboid(-2.0F, 5.517F, -3.0F, 4.0F, 0.0F, 3.0F, new Dilation(0.01F))
                .uv(207, 232).cuboid(-2.0F, -18.483F, -3.0F, 4.0F, 0.0F, 3.0F, new Dilation(0.01F))
                .uv(217, 233).cuboid(-2.0F, 1.517F, -3.5F, 4.0F, 0.0F, 4.0F, new Dilation(0.01F))
                .uv(217, 233).cuboid(-2.0F, 3.517F, -3.5F, 4.0F, 0.0F, 4.0F, new Dilation(0.01F))
                .uv(217, 233).cuboid(-2.0F, -14.483F, -3.5F, 4.0F, 0.0F, 4.0F, new Dilation(0.01F))
                .uv(217, 233).cuboid(-2.0F, -16.483F, -3.5F, 4.0F, 0.0F, 4.0F, new Dilation(0.01F))
                .uv(244, 247).cuboid(-2.0F, -0.483F, -4.0F, 4.0F, 0.0F, 4.0F, new Dilation(0.01F))
                .uv(244, 247).cuboid(-2.0F, -2.483F, -4.0F, 4.0F, 0.0F, 4.0F, new Dilation(0.01F))
                .uv(244, 247).cuboid(-2.0F, -10.483F, -4.0F, 4.0F, 0.0F, 4.0F, new Dilation(0.01F))
                .uv(244, 247).cuboid(-2.0F, -12.483F, -4.0F, 4.0F, 0.0F, 4.0F, new Dilation(0.01F))
                .uv(233, 251).cuboid(-2.5F, -4.483F, -4.5F, 5.0F, 0.0F, 5.0F, new Dilation(0.01F))
                .uv(212, 243).cuboid(-2.5F, -6.483F, -4.5F, 5.0F, 0.0F, 5.0F, new Dilation(0.01F))
                .uv(233, 251).cuboid(-2.5F, -8.483F, -4.5F, 5.0F, 0.0F, 5.0F, new Dilation(0.01F)), ModelTransform.of(0.0F, 0.483F, 0.0F, 0.0F, 1.5708F, 0.0F));

        ModelPartData bone51 = rotorrings.addChild("bone51", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, -13.5F, 0.0F));

        ModelPartData cube_r218 = bone51.addChild("cube_r218", ModelPartBuilder.create().uv(50, 129).cuboid(-1.5F, 18.483F, -2.6F, 3.0F, 3.0F, 0.0F, new Dilation(0.01F)), ModelTransform.of(0.0F, 17.983F, 0.0F, 0.0F, 1.0472F, 3.1416F));

        ModelPartData cube_r219 = bone51.addChild("cube_r219", ModelPartBuilder.create().uv(50, 129).cuboid(-1.5F, 18.483F, -2.6F, 3.0F, 3.0F, 0.0F, new Dilation(0.01F)), ModelTransform.of(0.0F, 17.983F, 0.0F, -3.1416F, 1.0472F, 0.0F));

        ModelPartData cube_r220 = bone51.addChild("cube_r220", ModelPartBuilder.create().uv(50, 129).cuboid(-1.5F, 18.483F, -2.6F, 3.0F, 3.0F, 0.0F, new Dilation(0.01F)), ModelTransform.of(0.0F, 17.983F, 0.0F, -3.1416F, 0.0F, 0.0F));

        ModelPartData cube_r221 = bone51.addChild("cube_r221", ModelPartBuilder.create().uv(50, 129).cuboid(-1.5F, 18.483F, -2.6F, 3.0F, 3.0F, 0.0F, new Dilation(0.01F)), ModelTransform.of(0.0F, 17.983F, 0.0F, -3.1416F, -1.0472F, 0.0F));

        ModelPartData cube_r222 = bone51.addChild("cube_r222", ModelPartBuilder.create().uv(50, 129).cuboid(-1.5F, 18.483F, -2.6F, 3.0F, 3.0F, 0.0F, new Dilation(0.01F)), ModelTransform.of(0.0F, 17.983F, 0.0F, 0.0F, -1.0472F, -3.1416F));

        ModelPartData cube_r223 = bone51.addChild("cube_r223", ModelPartBuilder.create().uv(50, 129).cuboid(-1.5F, 18.483F, -2.6F, 3.0F, 3.0F, 0.0F, new Dilation(0.01F)), ModelTransform.of(0.0F, 17.983F, 0.0F, 0.0F, 0.0F, -3.1416F));

        ModelPartData bone52 = rotorrings.addChild("bone52", ModelPartBuilder.create(), ModelTransform.of(0.0F, 13.5F, 0.0F, 0.0F, 0.0F, -3.1416F));

        ModelPartData cube_r224 = bone52.addChild("cube_r224", ModelPartBuilder.create().uv(50, 129).cuboid(-1.5F, 5.517F, -2.6F, 3.0F, 3.0F, 0.0F, new Dilation(0.01F)), ModelTransform.of(0.0F, 9.017F, 0.0F, 0.0F, 1.0472F, 3.1416F));

        ModelPartData cube_r225 = bone52.addChild("cube_r225", ModelPartBuilder.create().uv(50, 129).cuboid(-1.5F, 5.517F, -2.6F, 3.0F, 3.0F, 0.0F, new Dilation(0.01F)), ModelTransform.of(0.0F, 9.017F, 0.0F, -3.1416F, 1.0472F, 0.0F));

        ModelPartData cube_r226 = bone52.addChild("cube_r226", ModelPartBuilder.create().uv(50, 129).cuboid(-1.5F, 5.517F, -2.6F, 3.0F, 3.0F, 0.0F, new Dilation(0.01F)), ModelTransform.of(0.0F, 9.017F, 0.0F, 3.1416F, 0.0F, 0.0F));

        ModelPartData cube_r227 = bone52.addChild("cube_r227", ModelPartBuilder.create().uv(50, 129).cuboid(-1.5F, 5.517F, -2.6F, 3.0F, 3.0F, 0.0F, new Dilation(0.01F)), ModelTransform.of(0.0F, 9.017F, 0.0F, -3.1416F, -1.0472F, 0.0F));

        ModelPartData cube_r228 = bone52.addChild("cube_r228", ModelPartBuilder.create().uv(50, 129).cuboid(-1.5F, 5.517F, -2.6F, 3.0F, 3.0F, 0.0F, new Dilation(0.01F)), ModelTransform.of(0.0F, 9.017F, 0.0F, 0.0F, -1.0472F, -3.1416F));

        ModelPartData cube_r229 = bone52.addChild("cube_r229", ModelPartBuilder.create().uv(50, 129).cuboid(-1.5F, 5.517F, -2.6F, 3.0F, 3.0F, 0.0F, new Dilation(0.01F)), ModelTransform.of(0.0F, 9.017F, 0.0F, 0.0F, 0.0F, -3.1416F));

        ModelPartData rotorlight = rotorrings.addChild("rotorlight", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, -0.5F, 0.0F));

        ModelPartData cube_r230 = rotorlight.addChild("cube_r230", ModelPartBuilder.create().uv(251, 177).cuboid(-0.5F, -18.483F, -0.5F, 1.0F, 24.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 4.983F, 0.0F, 0.0F, 0.7854F, 0.0F));

        ModelPartData rotorf = rotor.addChild("rotorf", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, -4.0F, 0.0F));

        ModelPartData cube_r231 = rotorf.addChild("cube_r231", ModelPartBuilder.create().uv(251, 210).cuboid(-0.5F, -8.517F, -0.85F, 1.0F, 30.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -37.517F, 0.0F, 0.0F, -1.0472F, 3.1416F));

        ModelPartData bone33 = rotorf.addChild("bone33", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 4.0F, 0.0F));

        ModelPartData cube_r232 = bone33.addChild("cube_r232", ModelPartBuilder.create().uv(144, 110).cuboid(16.983F, 6.3336F, -2.0F, 8.0F, 0.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -41.517F, 0.0F, 0.0F, 0.0F, 1.5708F));

        ModelPartData bone34 = rotorf.addChild("bone34", ModelPartBuilder.create(), ModelTransform.of(0.0F, 4.0F, 0.0F, 0.0F, 0.5236F, 0.0F));

        ModelPartData cube_r233 = bone34.addChild("cube_r233", ModelPartBuilder.create().uv(144, 110).cuboid(16.983F, 6.3336F, -2.0F, 8.0F, 0.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -41.517F, 0.0F, 0.0F, 0.0F, 1.5708F));

        ModelPartData bone35 = rotorf.addChild("bone35", ModelPartBuilder.create(), ModelTransform.pivot(-6.7024F, -24.9653F, 0.0F));

        ModelPartData cube_r234 = bone35.addChild("cube_r234", ModelPartBuilder.create().uv(68, 134).cuboid(0.3165F, -9.5874F, -3.0F, 5.0F, 0.0F, 6.0F, new Dilation(0.0F))
                .uv(60, 249).cuboid(-0.803F, 22.9373F, -3.0F, 6.0F, 0.0F, 6.0F, new Dilation(0.0F)), ModelTransform.of(6.7024F, -12.5518F, 0.0F, 0.0F, 0.0F, -3.1416F));

        ModelPartData cube_r235 = bone35.addChild("cube_r235", ModelPartBuilder.create().uv(87, 226).cuboid(-35.2353F, 0.2301F, -4.0F, 1.0F, 1.0F, 8.0F, new Dilation(0.0F))
                .uv(62, 225).cuboid(-35.4815F, 1.1367F, -4.5F, 12.0F, 0.0F, 9.0F, new Dilation(0.0F)), ModelTransform.of(6.7024F, -12.5518F, 0.0F, 0.0F, 0.0F, 1.3963F));

        ModelPartData cube_r236 = bone35.addChild("cube_r236", ModelPartBuilder.create().uv(116, 231).cuboid(-26.7142F, -12.3461F, -1.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(6.7024F, -12.5518F, 0.0F, 0.0F, 0.0F, 0.9599F));

        ModelPartData cube_r237 = bone35.addChild("cube_r237", ModelPartBuilder.create().uv(112, 231).cuboid(-13.9008F, -25.5895F, -1.0F, 1.0F, 0.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(6.7024F, -12.5518F, 0.0F, 0.0F, 0.0F, 0.2618F));

        ModelPartData cube_r238 = bone35.addChild("cube_r238", ModelPartBuilder.create().uv(106, 231).cuboid(-30.1146F, 7.246F, -1.0F, 3.0F, 2.0F, 2.0F, new Dilation(0.001F))
                .uv(144, 104).cuboid(10.0F, 4.4674F, -3.0F, 7.0F, 0.0F, 6.0F, new Dilation(0.0F)), ModelTransform.of(6.7024F, -12.5518F, 0.0F, 0.0F, 0.0F, 1.6581F));

        ModelPartData glass_r1 = bone35.addChild("glass_r1", ModelPartBuilder.create().uv(-6, 249).cuboid(-23.4274F, 5.197F, -3.0F, 33.0F, 0.0F, 6.0F, new Dilation(0.0F)), ModelTransform.of(6.7024F, -12.5518F, 0.0F, 0.0F, 0.0F, 1.5708F));

        ModelPartData lightblinkythingidk3 = bone35.addChild("lightblinkythingidk3", ModelPartBuilder.create().uv(62, 216).cuboid(-35.4815F, 1.0367F, -4.5F, 12.0F, 0.0F, 9.0F, new Dilation(0.0F)), ModelTransform.of(6.7024F, -12.5518F, 0.0F, 0.0F, 0.0F, 1.3963F));

        ModelPartData bone = bone35.addChild("bone", ModelPartBuilder.create(), ModelTransform.of(0.6888F, 2.1584F, 0.0F, 0.0F, 0.0F, -0.2618F));

        ModelPartData cube_r239 = bone.addChild("cube_r239", ModelPartBuilder.create().uv(221, 249).cuboid(6.4928F, 8.8136F, -1.0F, 0.0F, 2.0F, 2.0F, new Dilation(0.0F))
                .uv(210, 250).cuboid(6.7428F, 8.3136F, -1.5F, 4.0F, 3.0F, 3.0F, new Dilation(0.0F))
                .uv(40, 239).cuboid(8.7428F, 7.8136F, -2.0F, 6.0F, 4.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(8.969F, -13.3557F, 0.0F, 0.0F, 0.0F, 1.4835F));

        ModelPartData lamp1 = bone.addChild("lamp1", ModelPartBuilder.create(), ModelTransform.pivot(-0.0453F, -3.7908F, 0.0F));

        ModelPartData cube_r240 = lamp1.addChild("cube_r240", ModelPartBuilder.create().uv(210, 223).cuboid(-2.0F, -1.5F, -1.5F, 4.0F, 3.0F, 3.0F, new Dilation(-0.1F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.4835F));

        ModelPartData bone36 = rotorf.addChild("bone36", ModelPartBuilder.create(), ModelTransform.of(-3.4641F, 0.0F, -2.0F, 0.0F, -0.5236F, 0.0F));

        ModelPartData cube_r241 = bone36.addChild("cube_r241", ModelPartBuilder.create().uv(141, 154).cuboid(20.3191F, 2.3253F, -2.5F, 3.0F, 2.0F, 5.0F, new Dilation(0.0F)), ModelTransform.of(4.0F, -37.517F, 0.0F, 2.0944F, 0.0F, -1.5708F));

        ModelPartData cube_r242 = bone36.addChild("cube_r242", ModelPartBuilder.create().uv(141, 154).cuboid(7.4309F, 2.3253F, -2.5F, 3.0F, 2.0F, 5.0F, new Dilation(0.0F)), ModelTransform.of(4.0F, -37.517F, 0.0F, 0.0F, 0.0F, 1.5708F));

        ModelPartData cube_r243 = bone36.addChild("cube_r243", ModelPartBuilder.create().uv(20, 142).cuboid(-0.7247F, -9.5559F, -0.5F, 7.0F, 0.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(4.0F, -37.517F, 0.0F, 0.0F, 0.0F, -3.1416F));

        ModelPartData cube_r244 = bone36.addChild("cube_r244", ModelPartBuilder.create().uv(144, 135).cuboid(15.062F, 4.42F, -1.5F, 3.0F, 1.0F, 3.0F, new Dilation(0.0F))
                .uv(142, 133).cuboid(10.062F, 4.42F, -0.5F, 5.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(4.0F, -37.517F, 0.0F, 0.0F, 0.0F, 1.6581F));

        ModelPartData cube_r245 = bone36.addChild("cube_r245", ModelPartBuilder.create().uv(138, 147).cuboid(-35.3903F, 1.3033F, -0.5F, 12.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(4.0F, -37.517F, 0.0F, 0.0F, 0.0F, 1.3963F));

        ModelPartData bone37 = bone36.addChild("bone37", ModelPartBuilder.create(), ModelTransform.pivot(0.0747F, -30.0862F, 0.0F));

        ModelPartData cube_r246 = bone37.addChild("cube_r246", ModelPartBuilder.create().uv(212, 238).cuboid(3.6809F, 3.9253F, -1.5F, 5.0F, 0.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(3.9253F, -7.4309F, 0.0F, 0.0F, 0.0F, 1.5708F));

        ModelPartData bone39 = bone36.addChild("bone39", ModelPartBuilder.create(), ModelTransform.pivot(0.0747F, -57.8362F, 0.0F));

        ModelPartData cube_r247 = bone39.addChild("cube_r247", ModelPartBuilder.create().uv(212, 238).mirrored().cuboid(-21.5691F, 3.9253F, -1.5F, 5.0F, 0.0F, 3.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(3.9253F, 20.3191F, 0.0F, 0.0F, 0.0F, 1.5708F));

        ModelPartData rotorf2 = rotor.addChild("rotorf2", ModelPartBuilder.create(), ModelTransform.of(0.0F, -4.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData cube_r248 = rotorf2.addChild("cube_r248", ModelPartBuilder.create().uv(253, 210).cuboid(-0.5F, -8.517F, -0.85F, 1.0F, 30.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -37.517F, 0.0F, 0.0F, -1.0472F, -3.1416F));

        ModelPartData bone2 = rotorf2.addChild("bone2", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 4.0F, 0.0F));

        ModelPartData cube_r249 = bone2.addChild("cube_r249", ModelPartBuilder.create().uv(144, 110).cuboid(16.983F, 6.3336F, -2.0F, 8.0F, 0.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -41.517F, 0.0F, 0.0F, 0.0F, 1.5708F));

        ModelPartData bone3 = rotorf2.addChild("bone3", ModelPartBuilder.create(), ModelTransform.of(0.0F, 4.0F, 0.0F, 0.0F, 0.5236F, 0.0F));

        ModelPartData cube_r250 = bone3.addChild("cube_r250", ModelPartBuilder.create().uv(144, 110).cuboid(16.983F, 6.3336F, -2.0F, 8.0F, 0.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -41.517F, 0.0F, 0.0F, 0.0F, 1.5708F));

        ModelPartData bone5 = rotorf2.addChild("bone5", ModelPartBuilder.create(), ModelTransform.pivot(-6.7024F, -24.9653F, 0.0F));

        ModelPartData cube_r251 = bone5.addChild("cube_r251", ModelPartBuilder.create().uv(68, 134).cuboid(0.3165F, -9.5773F, -3.0F, 5.0F, 0.0F, 6.0F, new Dilation(0.0F))
                .uv(60, 249).cuboid(-0.803F, 22.9274F, -3.0F, 6.0F, 0.0F, 6.0F, new Dilation(0.0F)), ModelTransform.of(6.7024F, -12.5518F, 0.0F, 0.0F, 0.0F, 3.1416F));

        ModelPartData cube_r252 = bone5.addChild("cube_r252", ModelPartBuilder.create().uv(87, 226).cuboid(-35.2353F, 0.2301F, -4.0F, 1.0F, 1.0F, 8.0F, new Dilation(0.0F))
                .uv(151, 231).cuboid(-35.4815F, 1.1367F, -4.5F, 12.0F, 0.0F, 9.0F, new Dilation(0.0F)), ModelTransform.of(6.7024F, -12.5518F, 0.0F, 0.0F, 0.0F, 1.3963F));

        ModelPartData glass_r2 = bone5.addChild("glass_r2", ModelPartBuilder.create().uv(-6, 249).cuboid(-23.4274F, 5.197F, -3.0F, 33.0F, 0.0F, 6.0F, new Dilation(0.0F)), ModelTransform.of(6.7024F, -12.5518F, 0.0F, 0.0F, 0.0F, 1.5708F));

        ModelPartData cube_r253 = bone5.addChild("cube_r253", ModelPartBuilder.create().uv(144, 104).cuboid(10.0F, 4.4674F, -3.0F, 7.0F, 0.0F, 6.0F, new Dilation(0.0F)), ModelTransform.of(6.7024F, -12.5518F, 0.0F, 0.0F, 0.0F, 1.6581F));

        ModelPartData bone6 = bone5.addChild("bone6", ModelPartBuilder.create(), ModelTransform.of(0.6888F, 2.1584F, 0.0F, 0.0F, 0.0F, -0.2618F));

        ModelPartData cube_r254 = bone6.addChild("cube_r254", ModelPartBuilder.create().uv(221, 249).cuboid(6.4928F, 8.8136F, -1.0F, 0.0F, 2.0F, 2.0F, new Dilation(0.0F))
                .uv(210, 250).cuboid(6.7428F, 8.3136F, -1.5F, 4.0F, 3.0F, 3.0F, new Dilation(0.0F))
                .uv(40, 239).cuboid(8.7428F, 7.8136F, -2.0F, 6.0F, 4.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(8.969F, -13.3557F, 0.0F, 0.0F, 0.0F, 1.4835F));

        ModelPartData lamp2 = bone6.addChild("lamp2", ModelPartBuilder.create(), ModelTransform.pivot(-0.0453F, -3.7908F, 0.0F));

        ModelPartData cube_r255 = lamp2.addChild("cube_r255", ModelPartBuilder.create().uv(210, 223).cuboid(-2.0F, -1.5F, -1.5F, 4.0F, 3.0F, 3.0F, new Dilation(-0.1F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.4835F));

        ModelPartData bone8 = rotorf2.addChild("bone8", ModelPartBuilder.create(), ModelTransform.of(-3.4641F, 0.0F, -2.0F, 0.0F, -0.5236F, 0.0F));

        ModelPartData cube_r256 = bone8.addChild("cube_r256", ModelPartBuilder.create().uv(141, 154).cuboid(20.3191F, 2.3253F, -2.5F, 3.0F, 2.0F, 5.0F, new Dilation(0.0F)), ModelTransform.of(4.0F, -37.517F, 0.0F, -2.0944F, 0.0F, -1.5708F));

        ModelPartData cube_r257 = bone8.addChild("cube_r257", ModelPartBuilder.create().uv(141, 154).cuboid(7.4309F, 2.3253F, -2.5F, 3.0F, 2.0F, 5.0F, new Dilation(0.0F)), ModelTransform.of(4.0F, -37.517F, 0.0F, 0.0F, 0.0F, 1.5708F));

        ModelPartData cube_r258 = bone8.addChild("cube_r258", ModelPartBuilder.create().uv(20, 142).cuboid(-0.7247F, -9.5559F, -0.5F, 7.0F, 0.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(4.0F, -37.517F, 0.0F, 0.0F, 0.0F, 3.1416F));

        ModelPartData cube_r259 = bone8.addChild("cube_r259", ModelPartBuilder.create().uv(144, 135).cuboid(15.062F, 4.42F, -1.5F, 3.0F, 1.0F, 3.0F, new Dilation(0.0F))
                .uv(142, 133).cuboid(10.062F, 4.42F, -0.5F, 5.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(4.0F, -37.517F, 0.0F, 0.0F, 0.0F, 1.6581F));

        ModelPartData cube_r260 = bone8.addChild("cube_r260", ModelPartBuilder.create().uv(138, 147).cuboid(-35.3903F, 1.3033F, -0.5F, 12.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(4.0F, -37.517F, 0.0F, 0.0F, 0.0F, 1.3963F));

        ModelPartData bone9 = bone8.addChild("bone9", ModelPartBuilder.create(), ModelTransform.pivot(0.0747F, -30.0862F, 0.0F));

        ModelPartData cube_r261 = bone9.addChild("cube_r261", ModelPartBuilder.create().uv(212, 238).cuboid(3.6809F, 3.9253F, -1.5F, 5.0F, 0.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(3.9253F, -7.4309F, 0.0F, 0.0F, 0.0F, 1.5708F));

        ModelPartData bone10 = bone8.addChild("bone10", ModelPartBuilder.create(), ModelTransform.pivot(0.0747F, -57.8362F, 0.0F));

        ModelPartData cube_r262 = bone10.addChild("cube_r262", ModelPartBuilder.create().uv(212, 238).mirrored().cuboid(-21.5691F, 3.9253F, -1.5F, 5.0F, 0.0F, 3.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(3.9253F, 20.3191F, 0.0F, 0.0F, 0.0F, 1.5708F));

        ModelPartData rotorf3 = rotor.addChild("rotorf3", ModelPartBuilder.create(), ModelTransform.of(0.0F, -4.0F, 0.0F, 0.0F, 2.0944F, 0.0F));

        ModelPartData cube_r263 = rotorf3.addChild("cube_r263", ModelPartBuilder.create().uv(251, 210).cuboid(-0.5F, -8.517F, -0.85F, 1.0F, 30.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -37.517F, 0.0F, 0.0F, -1.0472F, 3.1416F));

        ModelPartData bone11 = rotorf3.addChild("bone11", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 4.0F, 0.0F));

        ModelPartData cube_r264 = bone11.addChild("cube_r264", ModelPartBuilder.create().uv(144, 110).cuboid(16.983F, 6.3336F, -2.0F, 8.0F, 0.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -41.517F, 0.0F, 0.0F, 0.0F, 1.5708F));

        ModelPartData bone12 = rotorf3.addChild("bone12", ModelPartBuilder.create(), ModelTransform.of(0.0F, 4.0F, 0.0F, 0.0F, 0.5236F, 0.0F));

        ModelPartData cube_r265 = bone12.addChild("cube_r265", ModelPartBuilder.create().uv(144, 110).cuboid(16.983F, 6.3336F, -2.0F, 8.0F, 0.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -41.517F, 0.0F, 0.0F, 0.0F, 1.5708F));

        ModelPartData bone13 = rotorf3.addChild("bone13", ModelPartBuilder.create(), ModelTransform.pivot(-6.7024F, -24.9653F, 0.0F));

        ModelPartData cube_r266 = bone13.addChild("cube_r266", ModelPartBuilder.create().uv(68, 134).cuboid(0.3165F, -9.5874F, -3.0F, 5.0F, 0.0F, 6.0F, new Dilation(0.0F))
                .uv(60, 249).cuboid(-0.803F, 22.9373F, -3.0F, 6.0F, 0.0F, 6.0F, new Dilation(0.0F)), ModelTransform.of(6.7024F, -12.5518F, 0.0F, 0.0F, 0.0F, 3.1416F));

        ModelPartData cube_r267 = bone13.addChild("cube_r267", ModelPartBuilder.create().uv(87, 226).cuboid(-35.2353F, 0.2301F, -4.0F, 1.0F, 1.0F, 8.0F, new Dilation(0.0F))
                .uv(62, 225).cuboid(-35.4815F, 1.1367F, -4.5F, 12.0F, 0.0F, 9.0F, new Dilation(0.0F)), ModelTransform.of(6.7024F, -12.5518F, 0.0F, 0.0F, 0.0F, 1.3963F));

        ModelPartData cube_r268 = bone13.addChild("cube_r268", ModelPartBuilder.create().uv(116, 231).cuboid(-26.7142F, -12.3461F, -1.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(6.7024F, -12.5518F, 0.0F, 0.0F, 0.0F, 0.9599F));

        ModelPartData cube_r269 = bone13.addChild("cube_r269", ModelPartBuilder.create().uv(112, 231).cuboid(-13.9008F, -25.5895F, -1.0F, 1.0F, 0.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(6.7024F, -12.5518F, 0.0F, 0.0F, 0.0F, 0.2618F));

        ModelPartData cube_r270 = bone13.addChild("cube_r270", ModelPartBuilder.create().uv(106, 231).cuboid(-30.1146F, 7.246F, -1.0F, 3.0F, 2.0F, 2.0F, new Dilation(0.001F))
                .uv(144, 104).cuboid(10.0F, 4.4674F, -3.0F, 7.0F, 0.0F, 6.0F, new Dilation(0.0F)), ModelTransform.of(6.7024F, -12.5518F, 0.0F, 0.0F, 0.0F, 1.6581F));

        ModelPartData glass_r3 = bone13.addChild("glass_r3", ModelPartBuilder.create().uv(-6, 249).cuboid(-23.4274F, 5.197F, -3.0F, 33.0F, 0.0F, 6.0F, new Dilation(0.0F)), ModelTransform.of(6.7024F, -12.5518F, 0.0F, 0.0F, 0.0F, 1.5708F));

        ModelPartData bone14 = bone13.addChild("bone14", ModelPartBuilder.create(), ModelTransform.of(0.6888F, 2.1584F, 0.0F, 0.0F, 0.0F, -0.2618F));

        ModelPartData cube_r271 = bone14.addChild("cube_r271", ModelPartBuilder.create().uv(221, 249).cuboid(6.4928F, 8.8136F, -1.0F, 0.0F, 2.0F, 2.0F, new Dilation(0.0F))
                .uv(210, 250).cuboid(6.7428F, 8.3136F, -1.5F, 4.0F, 3.0F, 3.0F, new Dilation(0.0F))
                .uv(40, 239).cuboid(8.7428F, 7.8136F, -2.0F, 6.0F, 4.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(8.969F, -13.3557F, 0.0F, 0.0F, 0.0F, 1.4835F));

        ModelPartData lamp3 = bone14.addChild("lamp3", ModelPartBuilder.create(), ModelTransform.pivot(-0.0453F, -3.7908F, 0.0F));

        ModelPartData cube_r272 = lamp3.addChild("cube_r272", ModelPartBuilder.create().uv(210, 223).cuboid(-2.0F, -1.5F, -1.5F, 4.0F, 3.0F, 3.0F, new Dilation(-0.1F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.4835F));

        ModelPartData lightblinkythingidk2 = bone13.addChild("lightblinkythingidk2", ModelPartBuilder.create().uv(62, 216).cuboid(-35.4815F, 1.0367F, -4.5F, 12.0F, 0.0F, 9.0F, new Dilation(0.0F)), ModelTransform.of(6.7024F, -12.5518F, 0.0F, 0.0F, 0.0F, 1.3963F));

        ModelPartData bone15 = rotorf3.addChild("bone15", ModelPartBuilder.create(), ModelTransform.of(-3.4641F, 0.0F, -2.0F, 0.0F, -0.5236F, 0.0F));

        ModelPartData cube_r273 = bone15.addChild("cube_r273", ModelPartBuilder.create().uv(141, 154).cuboid(20.3191F, 2.3253F, -2.5F, 3.0F, 2.0F, 5.0F, new Dilation(0.0F)), ModelTransform.of(4.0F, -37.517F, 0.0F, 0.0F, 0.0F, -1.5708F));

        ModelPartData cube_r274 = bone15.addChild("cube_r274", ModelPartBuilder.create().uv(141, 154).cuboid(7.4309F, 2.3253F, -2.5F, 3.0F, 2.0F, 5.0F, new Dilation(0.0F)), ModelTransform.of(4.0F, -37.517F, 0.0F, 0.0F, 0.0F, 1.5708F));

        ModelPartData cube_r275 = bone15.addChild("cube_r275", ModelPartBuilder.create().uv(20, 142).cuboid(-0.7247F, -9.5559F, -0.5F, 7.0F, 0.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(4.0F, -37.517F, 0.0F, 0.0F, 0.0F, -3.1416F));

        ModelPartData cube_r276 = bone15.addChild("cube_r276", ModelPartBuilder.create().uv(144, 135).cuboid(15.062F, 4.42F, -1.5F, 3.0F, 1.0F, 3.0F, new Dilation(0.0F))
                .uv(142, 133).cuboid(10.062F, 4.42F, -0.5F, 5.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(4.0F, -37.517F, 0.0F, 0.0F, 0.0F, 1.6581F));

        ModelPartData cube_r277 = bone15.addChild("cube_r277", ModelPartBuilder.create().uv(138, 147).cuboid(-35.3903F, 1.3033F, -0.5F, 12.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(4.0F, -37.517F, 0.0F, 0.0F, 0.0F, 1.3963F));

        ModelPartData bone16 = bone15.addChild("bone16", ModelPartBuilder.create(), ModelTransform.pivot(0.0747F, -30.0862F, 0.0F));

        ModelPartData cube_r278 = bone16.addChild("cube_r278", ModelPartBuilder.create().uv(212, 238).cuboid(3.6809F, 3.9253F, -1.5F, 5.0F, 0.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(3.9253F, -7.4309F, 0.0F, 0.0F, 0.0F, 1.5708F));

        ModelPartData bone17 = bone15.addChild("bone17", ModelPartBuilder.create(), ModelTransform.pivot(0.0747F, -57.8362F, 0.0F));

        ModelPartData cube_r279 = bone17.addChild("cube_r279", ModelPartBuilder.create().uv(212, 238).mirrored().cuboid(-21.5691F, 3.9253F, -1.5F, 5.0F, 0.0F, 3.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(3.9253F, 20.3191F, 0.0F, 0.0F, 0.0F, 1.5708F));

        ModelPartData rotorf4 = rotor.addChild("rotorf4", ModelPartBuilder.create(), ModelTransform.of(0.0F, -4.0F, 0.0F, 0.0F, 3.1416F, 0.0F));

        ModelPartData cube_r280 = rotorf4.addChild("cube_r280", ModelPartBuilder.create().uv(253, 210).cuboid(-0.5F, -8.517F, -0.85F, 1.0F, 30.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -37.517F, 0.0F, 0.0F, -1.0472F, 3.1416F));

        ModelPartData bone18 = rotorf4.addChild("bone18", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 4.0F, 0.0F));

        ModelPartData cube_r281 = bone18.addChild("cube_r281", ModelPartBuilder.create().uv(56, 56).cuboid(31.5679F, -2.683F, -2.91F, 7.0F, 1.0F, 6.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -41.517F, 0.0F, -1.5708F, -0.1309F, 1.5708F));

        ModelPartData cube_r282 = bone18.addChild("cube_r282", ModelPartBuilder.create().uv(26, 71).cuboid(-38.5597F, 1.621F, -2.9364F, 7.0F, 1.0F, 6.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -41.517F, 0.0F, 0.7811F, 0.0924F, -1.6636F));

        ModelPartData cube_r283 = bone18.addChild("cube_r283", ModelPartBuilder.create().uv(42, 27).cuboid(-30.483F, 6.3326F, -3.0F, 1.0F, 1.0F, 6.0F, new Dilation(0.0001F)), ModelTransform.of(0.0F, -41.517F, 0.0F, 3.1416F, 0.0F, -1.5708F));

        ModelPartData cube_r284 = bone18.addChild("cube_r284", ModelPartBuilder.create().uv(42, 27).cuboid(-30.483F, 6.2426F, -3.09F, 1.0F, 1.0F, 6.0F, new Dilation(0.0001F))
                .uv(50, 27).cuboid(-31.483F, -6.0357F, -2.383F, 3.0F, 0.0F, 5.0F, new Dilation(0.0001F)), ModelTransform.of(0.0F, -41.517F, 0.0F, -1.5708F, 0.0F, -1.5708F));

        ModelPartData cube_r285 = bone18.addChild("cube_r285", ModelPartBuilder.create().uv(42, 27).cuboid(-30.483F, 6.3063F, -3.0636F, 1.0F, 1.0F, 6.0F, new Dilation(0.0F))
                .uv(50, 27).cuboid(-31.483F, -6.1184F, -2.4173F, 3.0F, 0.0F, 5.0F, new Dilation(0.0001F)), ModelTransform.of(0.0F, -41.517F, 0.0F, -2.3562F, 0.0F, -1.5708F));

        ModelPartData cube_r286 = bone18.addChild("cube_r286", ModelPartBuilder.create().uv(42, 27).cuboid(-30.483F, 6.3063F, -2.9364F, 1.0F, 1.0F, 6.0F, new Dilation(0.0F))
                .uv(50, 27).cuboid(-31.483F, 5.953F, -2.5827F, 3.0F, 0.0F, 5.0F, new Dilation(0.0001F)), ModelTransform.of(0.0F, -41.517F, 0.0F, 2.3562F, 0.0F, -1.5708F));

        ModelPartData cube_r287 = bone18.addChild("cube_r287", ModelPartBuilder.create().uv(42, 27).cuboid(-30.483F, 6.2426F, -2.91F, 1.0F, 1.0F, 6.0F, new Dilation(0.0001F))
                .uv(50, 27).cuboid(-31.483F, -6.0357F, -2.617F, 3.0F, 0.0F, 5.0F, new Dilation(0.0001F)), ModelTransform.of(0.0F, -41.517F, 0.0F, 1.5708F, 0.0F, -1.5708F));

        ModelPartData cube_r288 = bone18.addChild("cube_r288", ModelPartBuilder.create().uv(42, 27).cuboid(-30.483F, 6.179F, -2.9364F, 1.0F, 1.0F, 6.0F, new Dilation(0.0F))
                .uv(50, 27).cuboid(-31.483F, -5.953F, -2.5827F, 3.0F, 0.0F, 5.0F, new Dilation(0.0001F)), ModelTransform.of(0.0F, -41.517F, 0.0F, 0.7854F, 0.0F, -1.5708F));

        ModelPartData cube_r289 = bone18.addChild("cube_r289", ModelPartBuilder.create().uv(42, 27).cuboid(-30.483F, 6.179F, -3.0636F, 1.0F, 1.0F, 6.0F, new Dilation(0.0F))
                .uv(50, 27).cuboid(-31.483F, 6.1184F, -2.4173F, 3.0F, 0.0F, 5.0F, new Dilation(0.0001F)), ModelTransform.of(0.0F, -41.517F, 0.0F, -0.7854F, 0.0F, -1.5708F));

        ModelPartData cube_r290 = bone18.addChild("cube_r290", ModelPartBuilder.create().uv(50, 27).cuboid(-31.483F, -5.9187F, -2.5F, 3.0F, 0.0F, 5.0F, new Dilation(0.0001F))
                .uv(50, 27).cuboid(-31.483F, 6.1527F, -2.5F, 3.0F, 0.0F, 5.0F, new Dilation(0.0001F))
                .uv(42, 27).cuboid(-30.483F, 6.1527F, -3.0F, 1.0F, 1.0F, 6.0F, new Dilation(0.0001F)), ModelTransform.of(0.0F, -41.517F, 0.0F, 0.0F, 0.0F, -1.5708F));

        ModelPartData cube_r291 = bone18.addChild("cube_r291", ModelPartBuilder.create().uv(26, 71).cuboid(-38.5563F, 1.5948F, -3.0F, 7.0F, 1.0F, 6.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -41.517F, 0.0F, 0.0F, 0.0F, -1.7017F));

        ModelPartData cube_r292 = bone18.addChild("cube_r292", ModelPartBuilder.create().uv(0, 71).cuboid(31.5681F, -2.6843F, -3.0903F, 7.0F, 1.0F, 6.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -41.517F, 0.0F, 1.5708F, 0.1309F, 1.5708F));

        ModelPartData cube_r293 = bone18.addChild("cube_r293", ModelPartBuilder.create().uv(26, 71).cuboid(-38.5598F, 1.6212F, -3.0643F, 7.0F, 1.0F, 6.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -41.517F, 0.0F, -0.7811F, -0.0924F, -1.6636F));

        ModelPartData cube_r294 = bone18.addChild("cube_r294", ModelPartBuilder.create().uv(56, 56).cuboid(31.5765F, -2.7481F, -2.9364F, 7.0F, 1.0F, 6.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -41.517F, 0.0F, -2.3605F, -0.0924F, 1.6636F));

        ModelPartData cube_r295 = bone18.addChild("cube_r295", ModelPartBuilder.create().uv(56, 56).cuboid(31.5799F, -2.7742F, -3.0F, 7.0F, 1.0F, 6.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -41.517F, 0.0F, 3.1416F, 0.0F, 1.7017F));

        ModelPartData cube_r296 = bone18.addChild("cube_r296", ModelPartBuilder.create().uv(56, 20).cuboid(20.885F, -10.5102F, -2.91F, 7.0F, 1.0F, 6.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -41.517F, 0.0F, -1.5708F, 0.1309F, 1.5708F));

        ModelPartData cube_r297 = bone18.addChild("cube_r297", ModelPartBuilder.create().uv(56, 20).cuboid(20.8848F, -10.5114F, -3.0903F, 7.0F, 1.0F, 6.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -41.517F, 0.0F, 1.5708F, -0.1309F, 1.5708F));

        ModelPartData cube_r298 = bone18.addChild("cube_r298", ModelPartBuilder.create().uv(56, 0).cuboid(-27.8931F, 9.4484F, -3.0643F, 7.0F, 1.0F, 6.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -41.517F, 0.0F, -0.7811F, 0.0924F, -1.478F));

        ModelPartData cube_r299 = bone18.addChild("cube_r299", ModelPartBuilder.create().uv(56, 0).cuboid(-27.8966F, 9.422F, -3.0F, 7.0F, 1.0F, 6.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -41.517F, 0.0F, 0.0F, 0.0F, -1.4399F));

        ModelPartData cube_r300 = bone18.addChild("cube_r300", ModelPartBuilder.create().uv(56, 0).cuboid(-27.8932F, 9.4481F, -2.9364F, 7.0F, 1.0F, 6.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -41.517F, 0.0F, 0.7811F, -0.0924F, -1.478F));

        ModelPartData cube_r301 = bone18.addChild("cube_r301", ModelPartBuilder.create().uv(56, 20).cuboid(20.8764F, -10.5752F, -2.9364F, 7.0F, 1.0F, 6.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -41.517F, 0.0F, -2.3605F, 0.0924F, 1.478F));

        ModelPartData cube_r302 = bone18.addChild("cube_r302", ModelPartBuilder.create().uv(56, 70).cuboid(31.5764F, 1.7478F, -2.9357F, 7.0F, 1.0F, 6.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -41.517F, 0.0F, -0.7811F, 0.0924F, 1.6636F));

        ModelPartData cube_r303 = bone18.addChild("cube_r303", ModelPartBuilder.create().uv(56, 20).cuboid(20.8765F, -10.5749F, -3.0643F, 7.0F, 1.0F, 6.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -41.517F, 0.0F, 2.3605F, -0.0924F, 1.478F));

        ModelPartData cube_r304 = bone18.addChild("cube_r304", ModelPartBuilder.create().uv(56, 20).cuboid(20.873F, -10.6013F, -3.0F, 7.0F, 1.0F, 6.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -41.517F, 0.0F, -3.1416F, 0.0F, 1.4399F));

        ModelPartData cube_r305 = bone18.addChild("cube_r305", ModelPartBuilder.create().uv(144, 110).cuboid(16.983F, 6.3336F, -2.0F, 8.0F, 0.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -41.517F, 0.0F, 0.0F, 0.0F, 1.5708F));

        ModelPartData bone19 = rotorf4.addChild("bone19", ModelPartBuilder.create(), ModelTransform.of(0.0F, 4.0F, 0.0F, 0.0F, 0.5236F, 0.0F));

        ModelPartData cube_r306 = bone19.addChild("cube_r306", ModelPartBuilder.create().uv(144, 110).cuboid(16.983F, 6.3336F, -2.0F, 8.0F, 0.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -41.517F, 0.0F, 0.0F, 0.0F, 1.5708F));

        ModelPartData bone20 = rotorf4.addChild("bone20", ModelPartBuilder.create(), ModelTransform.pivot(-6.7024F, -24.9653F, 0.0F));

        ModelPartData cube_r307 = bone20.addChild("cube_r307", ModelPartBuilder.create().uv(68, 134).cuboid(0.3165F, -9.5773F, -3.0F, 5.0F, 0.0F, 6.0F, new Dilation(0.0F))
                .uv(60, 249).cuboid(-0.803F, 22.9274F, -3.0F, 6.0F, 0.0F, 6.0F, new Dilation(0.0F)), ModelTransform.of(6.7024F, -12.5518F, 0.0F, 0.0F, 0.0F, -3.1416F));

        ModelPartData cube_r308 = bone20.addChild("cube_r308", ModelPartBuilder.create().uv(87, 226).cuboid(-35.2353F, 0.2301F, -4.0F, 1.0F, 1.0F, 8.0F, new Dilation(0.0F))
                .uv(151, 231).cuboid(-35.4815F, 1.1367F, -4.5F, 12.0F, 0.0F, 9.0F, new Dilation(0.0F)), ModelTransform.of(6.7024F, -12.5518F, 0.0F, 0.0F, 0.0F, 1.3963F));

        ModelPartData glass_r4 = bone20.addChild("glass_r4", ModelPartBuilder.create().uv(-6, 249).cuboid(-23.4274F, 5.197F, -3.0F, 33.0F, 0.0F, 6.0F, new Dilation(0.0F)), ModelTransform.of(6.7024F, -12.5518F, 0.0F, 0.0F, 0.0F, 1.5708F));

        ModelPartData cube_r309 = bone20.addChild("cube_r309", ModelPartBuilder.create().uv(144, 104).cuboid(10.0F, 4.4674F, -3.0F, 7.0F, 0.0F, 6.0F, new Dilation(0.0F)), ModelTransform.of(6.7024F, -12.5518F, 0.0F, 0.0F, 0.0F, 1.6581F));

        ModelPartData bone21 = bone20.addChild("bone21", ModelPartBuilder.create(), ModelTransform.of(0.6888F, 2.1584F, 0.0F, 0.0F, 0.0F, -0.2618F));

        ModelPartData cube_r310 = bone21.addChild("cube_r310", ModelPartBuilder.create().uv(221, 249).cuboid(6.4928F, 8.8136F, -1.0F, 0.0F, 2.0F, 2.0F, new Dilation(0.0F))
                .uv(210, 250).cuboid(6.7428F, 8.3136F, -1.5F, 4.0F, 3.0F, 3.0F, new Dilation(0.0F))
                .uv(40, 239).cuboid(8.7428F, 7.8136F, -2.0F, 6.0F, 4.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(8.969F, -13.3557F, 0.0F, 0.0F, 0.0F, 1.4835F));

        ModelPartData lamp4 = bone21.addChild("lamp4", ModelPartBuilder.create(), ModelTransform.pivot(-0.0453F, -3.7908F, 0.0F));

        ModelPartData cube_r311 = lamp4.addChild("cube_r311", ModelPartBuilder.create().uv(210, 223).cuboid(-2.0F, -1.5F, -1.5F, 4.0F, 3.0F, 3.0F, new Dilation(-0.1F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.4835F));

        ModelPartData bone22 = rotorf4.addChild("bone22", ModelPartBuilder.create(), ModelTransform.of(-3.4641F, 0.0F, -2.0F, 0.0F, -0.5236F, 0.0F));

        ModelPartData cube_r312 = bone22.addChild("cube_r312", ModelPartBuilder.create().uv(141, 154).cuboid(20.3191F, 2.3253F, -2.5F, 3.0F, 2.0F, 5.0F, new Dilation(0.0F)), ModelTransform.of(4.0F, -37.517F, 0.0F, 2.0944F, 0.0F, -1.5708F));

        ModelPartData cube_r313 = bone22.addChild("cube_r313", ModelPartBuilder.create().uv(141, 154).cuboid(7.4309F, 2.3253F, -2.5F, 3.0F, 2.0F, 5.0F, new Dilation(0.0F)), ModelTransform.of(4.0F, -37.517F, 0.0F, 0.0F, 0.0F, 1.5708F));

        ModelPartData cube_r314 = bone22.addChild("cube_r314", ModelPartBuilder.create().uv(20, 142).cuboid(-0.7247F, -9.5559F, -0.5F, 7.0F, 0.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(4.0F, -37.517F, 0.0F, 0.0F, 0.0F, 3.1416F));

        ModelPartData cube_r315 = bone22.addChild("cube_r315", ModelPartBuilder.create().uv(144, 135).cuboid(15.062F, 4.42F, -1.5F, 3.0F, 1.0F, 3.0F, new Dilation(0.0F))
                .uv(142, 133).cuboid(10.062F, 4.42F, -0.5F, 5.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(4.0F, -37.517F, 0.0F, 0.0F, 0.0F, 1.6581F));

        ModelPartData cube_r316 = bone22.addChild("cube_r316", ModelPartBuilder.create().uv(138, 147).cuboid(-35.3903F, 1.3033F, -0.5F, 12.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(4.0F, -37.517F, 0.0F, 0.0F, 0.0F, 1.3963F));

        ModelPartData bone23 = bone22.addChild("bone23", ModelPartBuilder.create(), ModelTransform.pivot(0.0747F, -30.0862F, 0.0F));

        ModelPartData cube_r317 = bone23.addChild("cube_r317", ModelPartBuilder.create().uv(212, 238).cuboid(3.6809F, 3.9253F, -1.5F, 5.0F, 0.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(3.9253F, -7.4309F, 0.0F, 0.0F, 0.0F, 1.5708F));

        ModelPartData bone24 = bone22.addChild("bone24", ModelPartBuilder.create(), ModelTransform.pivot(0.0747F, -57.8362F, 0.0F));

        ModelPartData cube_r318 = bone24.addChild("cube_r318", ModelPartBuilder.create().uv(212, 238).mirrored().cuboid(-21.5691F, 3.9253F, -1.5F, 5.0F, 0.0F, 3.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(3.9253F, 20.3191F, 0.0F, 0.0F, 0.0F, 1.5708F));

        ModelPartData rotorf5 = rotor.addChild("rotorf5", ModelPartBuilder.create(), ModelTransform.of(0.0F, -4.0F, 0.0F, 0.0F, -2.0944F, 0.0F));

        ModelPartData cube_r319 = rotorf5.addChild("cube_r319", ModelPartBuilder.create().uv(251, 210).cuboid(-0.5F, -8.517F, -0.85F, 1.0F, 30.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -37.517F, 0.0F, 0.0F, -1.0472F, 3.1416F));

        ModelPartData bone25 = rotorf5.addChild("bone25", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 4.0F, 0.0F));

        ModelPartData cube_r320 = bone25.addChild("cube_r320", ModelPartBuilder.create().uv(144, 110).cuboid(16.983F, 6.3336F, -2.0F, 8.0F, 0.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -41.517F, 0.0F, 0.0F, 0.0F, 1.5708F));

        ModelPartData bone26 = rotorf5.addChild("bone26", ModelPartBuilder.create(), ModelTransform.of(0.0F, 4.0F, 0.0F, 0.0F, 0.5236F, 0.0F));

        ModelPartData cube_r321 = bone26.addChild("cube_r321", ModelPartBuilder.create().uv(144, 110).cuboid(16.983F, 6.3336F, -2.0F, 8.0F, 0.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -41.517F, 0.0F, 0.0F, 0.0F, 1.5708F));

        ModelPartData bone27 = rotorf5.addChild("bone27", ModelPartBuilder.create(), ModelTransform.pivot(-6.7024F, -24.9653F, 0.0F));

        ModelPartData cube_r322 = bone27.addChild("cube_r322", ModelPartBuilder.create().uv(68, 134).cuboid(0.3165F, -9.5874F, -3.0F, 5.0F, 0.0F, 6.0F, new Dilation(0.0F))
                .uv(60, 249).cuboid(-0.803F, 22.9373F, -3.0F, 6.0F, 0.0F, 6.0F, new Dilation(0.0F)), ModelTransform.of(6.7024F, -12.5518F, 0.0F, 0.0F, 0.0F, -3.1416F));

        ModelPartData cube_r323 = bone27.addChild("cube_r323", ModelPartBuilder.create().uv(87, 226).cuboid(-35.2353F, 0.2301F, -4.0F, 1.0F, 1.0F, 8.0F, new Dilation(0.0F))
                .uv(62, 225).cuboid(-35.4815F, 1.1367F, -4.5F, 12.0F, 0.0F, 9.0F, new Dilation(0.0F)), ModelTransform.of(6.7024F, -12.5518F, 0.0F, 0.0F, 0.0F, 1.3963F));

        ModelPartData cube_r324 = bone27.addChild("cube_r324", ModelPartBuilder.create().uv(116, 231).cuboid(-26.7142F, -12.3461F, -1.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(6.7024F, -12.5518F, 0.0F, 0.0F, 0.0F, 0.9599F));

        ModelPartData cube_r325 = bone27.addChild("cube_r325", ModelPartBuilder.create().uv(112, 231).cuboid(-13.9008F, -25.5895F, -1.0F, 1.0F, 0.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(6.7024F, -12.5518F, 0.0F, 0.0F, 0.0F, 0.2618F));

        ModelPartData cube_r326 = bone27.addChild("cube_r326", ModelPartBuilder.create().uv(106, 231).cuboid(-30.1146F, 7.246F, -1.0F, 3.0F, 2.0F, 2.0F, new Dilation(0.001F))
                .uv(144, 104).cuboid(10.0F, 4.4674F, -3.0F, 7.0F, 0.0F, 6.0F, new Dilation(0.0F)), ModelTransform.of(6.7024F, -12.5518F, 0.0F, 0.0F, 0.0F, 1.6581F));

        ModelPartData glass_r5 = bone27.addChild("glass_r5", ModelPartBuilder.create().uv(-6, 249).cuboid(-23.4274F, 5.197F, -3.0F, 33.0F, 0.0F, 6.0F, new Dilation(0.0F)), ModelTransform.of(6.7024F, -12.5518F, 0.0F, 0.0F, 0.0F, 1.5708F));

        ModelPartData bone28 = bone27.addChild("bone28", ModelPartBuilder.create(), ModelTransform.of(0.6888F, 2.1584F, 0.0F, 0.0F, 0.0F, -0.2618F));

        ModelPartData cube_r327 = bone28.addChild("cube_r327", ModelPartBuilder.create().uv(221, 249).cuboid(6.4928F, 8.8136F, -1.0F, 0.0F, 2.0F, 2.0F, new Dilation(0.0F))
                .uv(210, 250).cuboid(6.7428F, 8.3136F, -1.5F, 4.0F, 3.0F, 3.0F, new Dilation(0.0F))
                .uv(40, 239).cuboid(8.7428F, 7.8136F, -2.0F, 6.0F, 4.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(8.969F, -13.3557F, 0.0F, 0.0F, 0.0F, 1.4835F));

        ModelPartData lamp5 = bone28.addChild("lamp5", ModelPartBuilder.create(), ModelTransform.pivot(-0.0453F, -3.7908F, 0.0F));

        ModelPartData cube_r328 = lamp5.addChild("cube_r328", ModelPartBuilder.create().uv(210, 223).cuboid(-2.0F, -1.5F, -1.5F, 4.0F, 3.0F, 3.0F, new Dilation(-0.1F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.4835F));

        ModelPartData lightblinkythingidk = bone27.addChild("lightblinkythingidk", ModelPartBuilder.create().uv(62, 216).cuboid(-35.4815F, 1.0367F, -4.5F, 12.0F, 0.0F, 9.0F, new Dilation(0.0F)), ModelTransform.of(6.7024F, -12.5518F, 0.0F, 0.0F, 0.0F, 1.3963F));

        ModelPartData bone29 = rotorf5.addChild("bone29", ModelPartBuilder.create(), ModelTransform.of(-3.4641F, 0.0F, -2.0F, 0.0F, -0.5236F, 0.0F));

        ModelPartData cube_r329 = bone29.addChild("cube_r329", ModelPartBuilder.create().uv(141, 154).cuboid(20.3191F, 2.3253F, -2.5F, 3.0F, 2.0F, 5.0F, new Dilation(0.0F)), ModelTransform.of(4.0F, -37.517F, 0.0F, -2.0944F, 0.0F, -1.5708F));

        ModelPartData cube_r330 = bone29.addChild("cube_r330", ModelPartBuilder.create().uv(141, 154).cuboid(7.4309F, 2.3253F, -2.5F, 3.0F, 2.0F, 5.0F, new Dilation(0.0F)), ModelTransform.of(4.0F, -37.517F, 0.0F, 0.0F, 0.0F, 1.5708F));

        ModelPartData cube_r331 = bone29.addChild("cube_r331", ModelPartBuilder.create().uv(20, 142).cuboid(-0.7247F, -9.5559F, -0.5F, 7.0F, 0.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(4.0F, -37.517F, 0.0F, 0.0F, 0.0F, -3.1416F));

        ModelPartData cube_r332 = bone29.addChild("cube_r332", ModelPartBuilder.create().uv(144, 135).cuboid(15.062F, 4.42F, -1.5F, 3.0F, 1.0F, 3.0F, new Dilation(0.0F))
                .uv(142, 133).cuboid(10.062F, 4.42F, -0.5F, 5.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(4.0F, -37.517F, 0.0F, 0.0F, 0.0F, 1.6581F));

        ModelPartData cube_r333 = bone29.addChild("cube_r333", ModelPartBuilder.create().uv(138, 147).cuboid(-35.3903F, 1.3033F, -0.5F, 12.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(4.0F, -37.517F, 0.0F, 0.0F, 0.0F, 1.3963F));

        ModelPartData bone30 = bone29.addChild("bone30", ModelPartBuilder.create(), ModelTransform.pivot(0.0747F, -30.0862F, 0.0F));

        ModelPartData cube_r334 = bone30.addChild("cube_r334", ModelPartBuilder.create().uv(212, 238).cuboid(3.6809F, 3.9253F, -1.5F, 5.0F, 0.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(3.9253F, -7.4309F, 0.0F, 0.0F, 0.0F, 1.5708F));

        ModelPartData bone31 = bone29.addChild("bone31", ModelPartBuilder.create(), ModelTransform.pivot(0.0747F, -57.8362F, 0.0F));

        ModelPartData cube_r335 = bone31.addChild("cube_r335", ModelPartBuilder.create().uv(212, 238).mirrored().cuboid(-21.5691F, 3.9253F, -1.5F, 5.0F, 0.0F, 3.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(3.9253F, 20.3191F, 0.0F, 0.0F, 0.0F, 1.5708F));

        ModelPartData rotorf6 = rotor.addChild("rotorf6", ModelPartBuilder.create(), ModelTransform.of(0.0F, -4.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData cube_r336 = rotorf6.addChild("cube_r336", ModelPartBuilder.create().uv(253, 210).cuboid(-0.5F, -8.517F, -0.85F, 1.0F, 30.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -37.517F, 0.0F, 0.0F, -1.0472F, 3.1416F));

        ModelPartData bone32 = rotorf6.addChild("bone32", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 4.0F, 0.0F));

        ModelPartData cube_r337 = bone32.addChild("cube_r337", ModelPartBuilder.create().uv(144, 110).cuboid(16.983F, 6.3336F, -2.0F, 8.0F, 0.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -41.517F, 0.0F, 0.0F, 0.0F, 1.5708F));

        ModelPartData bone38 = rotorf6.addChild("bone38", ModelPartBuilder.create(), ModelTransform.of(0.0F, 4.0F, 0.0F, 0.0F, 0.5236F, 0.0F));

        ModelPartData cube_r338 = bone38.addChild("cube_r338", ModelPartBuilder.create().uv(144, 110).cuboid(16.983F, 6.3336F, -2.0F, 8.0F, 0.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -41.517F, 0.0F, 0.0F, 0.0F, 1.5708F));

        ModelPartData bone40 = rotorf6.addChild("bone40", ModelPartBuilder.create(), ModelTransform.pivot(-6.7024F, -24.9653F, 0.0F));

        ModelPartData cube_r339 = bone40.addChild("cube_r339", ModelPartBuilder.create().uv(68, 134).cuboid(0.3165F, -9.5773F, -3.0F, 5.0F, 0.0F, 6.0F, new Dilation(0.0F))
                .uv(60, 249).cuboid(-0.803F, 22.9274F, -3.0F, 6.0F, 0.0F, 6.0F, new Dilation(0.0F)), ModelTransform.of(6.7024F, -12.5518F, 0.0F, 0.0F, 0.0F, -3.1416F));

        ModelPartData cube_r340 = bone40.addChild("cube_r340", ModelPartBuilder.create().uv(87, 226).cuboid(-35.2353F, 0.2301F, -4.0F, 1.0F, 1.0F, 8.0F, new Dilation(0.0F))
                .uv(151, 231).cuboid(-35.4815F, 1.1367F, -4.5F, 12.0F, 0.0F, 9.0F, new Dilation(0.0F)), ModelTransform.of(6.7024F, -12.5518F, 0.0F, 0.0F, 0.0F, 1.3963F));

        ModelPartData glass_r6 = bone40.addChild("glass_r6", ModelPartBuilder.create().uv(-6, 249).cuboid(-23.4274F, 5.197F, -3.0F, 33.0F, 0.0F, 6.0F, new Dilation(0.0F)), ModelTransform.of(6.7024F, -12.5518F, 0.0F, 0.0F, 0.0F, 1.5708F));

        ModelPartData cube_r341 = bone40.addChild("cube_r341", ModelPartBuilder.create().uv(144, 104).cuboid(10.0F, 4.4674F, -3.0F, 7.0F, 0.0F, 6.0F, new Dilation(0.0F)), ModelTransform.of(6.7024F, -12.5518F, 0.0F, 0.0F, 0.0F, 1.6581F));

        ModelPartData bone41 = bone40.addChild("bone41", ModelPartBuilder.create(), ModelTransform.of(0.6888F, 2.1584F, 0.0F, 0.0F, 0.0F, -0.2618F));

        ModelPartData cube_r342 = bone41.addChild("cube_r342", ModelPartBuilder.create().uv(221, 249).cuboid(6.4928F, 8.8136F, -1.0F, 0.0F, 2.0F, 2.0F, new Dilation(0.0F))
                .uv(210, 250).cuboid(6.7428F, 8.3136F, -1.5F, 4.0F, 3.0F, 3.0F, new Dilation(0.0F))
                .uv(40, 239).cuboid(8.7428F, 7.8136F, -2.0F, 6.0F, 4.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(8.969F, -13.3557F, 0.0F, 0.0F, 0.0F, 1.4835F));

        ModelPartData lamp6 = bone41.addChild("lamp6", ModelPartBuilder.create(), ModelTransform.pivot(-0.0453F, -3.7908F, 0.0F));

        ModelPartData cube_r343 = lamp6.addChild("cube_r343", ModelPartBuilder.create().uv(210, 223).cuboid(-2.0F, -1.5F, -1.5F, 4.0F, 3.0F, 3.0F, new Dilation(-0.1F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.4835F));

        ModelPartData bone42 = rotorf6.addChild("bone42", ModelPartBuilder.create(), ModelTransform.of(-3.4641F, 0.0F, -2.0F, 0.0F, -0.5236F, 0.0F));

        ModelPartData cube_r344 = bone42.addChild("cube_r344", ModelPartBuilder.create().uv(141, 154).cuboid(20.3191F, 2.3253F, -2.5F, 3.0F, 2.0F, 5.0F, new Dilation(0.0F)), ModelTransform.of(4.0F, -37.517F, 0.0F, 0.0F, 0.0F, -1.5708F));

        ModelPartData cube_r345 = bone42.addChild("cube_r345", ModelPartBuilder.create().uv(141, 154).cuboid(7.4309F, 2.3253F, -2.5F, 3.0F, 2.0F, 5.0F, new Dilation(0.0F)), ModelTransform.of(4.0F, -37.517F, 0.0F, 0.0F, 0.0F, 1.5708F));

        ModelPartData cube_r346 = bone42.addChild("cube_r346", ModelPartBuilder.create().uv(20, 142).cuboid(-0.7247F, -9.5559F, -0.5F, 7.0F, 0.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(4.0F, -37.517F, 0.0F, 0.0F, 0.0F, 3.1416F));

        ModelPartData cube_r347 = bone42.addChild("cube_r347", ModelPartBuilder.create().uv(144, 135).cuboid(15.062F, 4.42F, -1.5F, 3.0F, 1.0F, 3.0F, new Dilation(0.0F))
                .uv(142, 133).cuboid(10.062F, 4.42F, -0.5F, 5.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(4.0F, -37.517F, 0.0F, 0.0F, 0.0F, 1.6581F));

        ModelPartData cube_r348 = bone42.addChild("cube_r348", ModelPartBuilder.create().uv(138, 147).cuboid(-35.3903F, 1.3033F, -0.5F, 12.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(4.0F, -37.517F, 0.0F, 0.0F, 0.0F, 1.3963F));

        ModelPartData bone43 = bone42.addChild("bone43", ModelPartBuilder.create(), ModelTransform.pivot(0.0747F, -30.0862F, 0.0F));

        ModelPartData cube_r349 = bone43.addChild("cube_r349", ModelPartBuilder.create().uv(212, 238).cuboid(3.6809F, 3.9253F, -1.5F, 5.0F, 0.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(3.9253F, -7.4309F, 0.0F, 0.0F, 0.0F, 1.5708F));

        ModelPartData bone44 = bone42.addChild("bone44", ModelPartBuilder.create(), ModelTransform.pivot(0.0747F, -57.8362F, 0.0F));

        ModelPartData cube_r350 = bone44.addChild("cube_r350", ModelPartBuilder.create().uv(212, 238).mirrored().cuboid(-21.5691F, 3.9253F, -1.5F, 5.0F, 0.0F, 3.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(3.9253F, 20.3191F, 0.0F, 0.0F, 0.0F, 1.5708F));

        ModelPartData coffeemachinetwo = bone7.addChild("coffeemachinetwo", ModelPartBuilder.create(), ModelTransform.of(0.0F, -21.8117F, 0.0F, 0.0F, 1.5708F, 0.0F));

        ModelPartData coffeemachine = coffeemachinetwo.addChild("coffeemachine", ModelPartBuilder.create(), ModelTransform.of(0.0F, 0.0F, 13.0F, 0.0F, 1.5708F, 0.0F));

        ModelPartData cube_r351 = coffeemachine.addChild("cube_r351", ModelPartBuilder.create().uv(198, 248).cuboid(7.5535F, -21.7053F, 8.4047F, 2.0F, 4.0F, 4.0F, new Dilation(0.15F))
                .uv(225, 248).cuboid(7.4535F, -21.7053F, 8.4047F, 2.0F, 4.0F, 4.0F, new Dilation(0.01F)), ModelTransform.of(12.0248F, -19.7053F, -0.0248F, 0.0F, 1.0472F, -3.1416F));

        ModelPartData cube_r352 = coffeemachine.addChild("cube_r352", ModelPartBuilder.create().uv(198, 248).cuboid(-4.4465F, -21.7053F, 8.3799F, 2.0F, 4.0F, 4.0F, new Dilation(0.16F))
                .uv(225, 248).cuboid(-4.5465F, -21.7053F, 8.3799F, 2.0F, 4.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(12.0248F, -19.7053F, -0.0248F, -3.1416F, 1.0472F, 0.0F));

        ModelPartData cube_r353 = coffeemachine.addChild("cube_r353", ModelPartBuilder.create().uv(198, 248).cuboid(-10.425F, -21.7053F, -2.0248F, 2.0F, 4.0F, 4.0F, new Dilation(0.15F))
                .uv(225, 248).cuboid(-10.525F, -21.7053F, -2.0248F, 2.0F, 4.0F, 4.0F, new Dilation(0.01F)), ModelTransform.of(12.0248F, -19.7053F, -0.0248F, 3.1416F, 0.0F, 0.0F));

        ModelPartData cube_r354 = coffeemachine.addChild("cube_r354", ModelPartBuilder.create().uv(198, 248).cuboid(-4.4035F, -21.7053F, -12.4047F, 2.0F, 4.0F, 4.0F, new Dilation(0.16F))
                .uv(225, 248).cuboid(-4.5035F, -21.7053F, -12.4047F, 2.0F, 4.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(12.0248F, -19.7053F, -0.0248F, -3.1416F, -1.0472F, 0.0F));

        ModelPartData cube_r355 = coffeemachine.addChild("cube_r355", ModelPartBuilder.create().uv(198, 248).cuboid(7.5965F, -21.7053F, -12.3799F, 2.0F, 4.0F, 4.0F, new Dilation(0.15F))
                .uv(225, 248).cuboid(7.4965F, -21.7053F, -12.3799F, 2.0F, 4.0F, 4.0F, new Dilation(0.01F)), ModelTransform.of(12.0248F, -19.7053F, -0.0248F, 0.0F, -1.0472F, -3.1416F));

        ModelPartData cube_r356 = coffeemachine.addChild("cube_r356", ModelPartBuilder.create().uv(198, 248).cuboid(13.575F, -21.7053F, -1.9752F, 2.0F, 4.0F, 4.0F, new Dilation(0.16F))
                .uv(225, 248).cuboid(13.475F, -21.7053F, -1.9752F, 2.0F, 4.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(12.0248F, -19.7053F, -0.0248F, 0.0F, 0.0F, -3.1416F));

        ModelPartData top = coffeemachine.addChild("top", ModelPartBuilder.create(), ModelTransform.pivot(0.0248F, 0.0F, 0.0F));

        ModelPartData cube_r357 = top.addChild("cube_r357", ModelPartBuilder.create().uv(107, 247).cuboid(9.45F, -21.9553F, -2.4752F, 5.0F, 4.0F, 5.0F, new Dilation(0.0F)), ModelTransform.of(12.0F, -19.7053F, -0.0248F, 0.0F, 0.0F, -3.1416F));

        ModelPartData bone53 = bone7.addChild("bone53", ModelPartBuilder.create().uv(213, 63).cuboid(-7.5F, -34.483F, -6.5F, 15.0F, 0.0F, 13.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -41.517F, 0.0F, 0.0F, 1.5708F, 0.0F));

        ModelPartData cube_r358 = bone53.addChild("cube_r358", ModelPartBuilder.create().uv(214, 34).cuboid(-7.5F, 0.0F, -6.5F, 14.0F, 0.0F, 14.0F, new Dilation(0.0F)), ModelTransform.of(0.4F, 28.267F, -0.4F, 0.0F, 0.0F, 0.0175F));

        ModelPartData cube_r359 = bone53.addChild("cube_r359", ModelPartBuilder.create().uv(237, 52).cuboid(-2.5F, 0.0F, -3.5F, 5.0F, 0.0F, 7.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -20.483F, 0.0F, 0.0F, 0.0F, 0.0175F));

        ModelPartData cube_r360 = bone53.addChild("cube_r360", ModelPartBuilder.create().uv(237, 52).cuboid(-2.5F, 0.0F, -3.5F, 5.0F, 0.0F, 7.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 7.517F, 0.0F, 0.0F, 0.0F, 0.0175F));

        ModelPartData cube_r361 = bone53.addChild("cube_r361", ModelPartBuilder.create().uv(211, 48).cuboid(-7.5F, 0.0F, -7.5F, 15.0F, 0.0F, 15.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 37.517F, 0.0F, 0.0F, 0.0F, 0.0175F));

        ModelPartData cube_r362 = bone53.addChild("cube_r362", ModelPartBuilder.create().uv(230, 237).cuboid(1.5F, -2.0F, -2.0F, 3.0F, 4.0F, 4.0F, new Dilation(0.15F))
                .uv(230, 229).cuboid(-4.5F, -2.0F, -2.0F, 3.0F, 4.0F, 4.0F, new Dilation(0.15F)), ModelTransform.of(0.0F, 19.017F, -14.0F, -0.3927F, 0.0F, -0.0175F));

        ModelPartData cube_r363 = bone53.addChild("cube_r363", ModelPartBuilder.create().uv(250, 205).cuboid(-1.0F, 21.927F, -8.1568F, 2.0F, 2.0F, 1.0F, new Dilation(0.1F))
                .uv(250, 202).cuboid(-1.0F, 21.927F, -8.1568F, 2.0F, 2.0F, 1.0F, new Dilation(0.0F))
                .uv(224, 221).cuboid(-4.5F, 20.927F, -7.6568F, 9.0F, 4.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, -0.3927F, 0.0F, 0.0F));

        ModelPartData cube_r364 = bone53.addChild("cube_r364", ModelPartBuilder.create().uv(240, 229).cuboid(-5.0F, 11.2119F, -21.2118F, 1.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.3927F, 0.0F, 0.0F));

        ModelPartData throttle = bone53.addChild("throttle", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 19.017F, -14.0F));

        ModelPartData cube_r365 = throttle.addChild("cube_r365", ModelPartBuilder.create().uv(4, 10).cuboid(-5.75F, -6.1568F, -18.877F, 1.0F, 1.0F, 1.0F, new Dilation(-0.2F))
                .uv(233, 245).cuboid(-5.75F, -6.1568F, -21.077F, 1.0F, 1.0F, 3.0F, new Dilation(-0.15F))
                .uv(240, 239).cuboid(-5.5F, -6.1568F, -23.427F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(240, 236).cuboid(-5.25F, -6.4068F, -22.927F, 0.0F, 1.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -19.017F, 14.0F, 1.1781F, 0.0F, 0.0F));

        ModelPartData refueler = bone53.addChild("refueler", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 17.9647F, -16.5407F));

        ModelPartData cube_r366 = refueler.addChild("cube_r366", ModelPartBuilder.create().uv(252, 208).cuboid(15.7119F, 15.7119F, -8.4068F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -17.9647F, 16.5407F, -0.2849F, 0.274F, 0.7459F));
        return TexturedModelData.of(modelData, 256, 256);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
        matrices.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(180f));
        console.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
    }

    @Override
    public void renderWithAnimations(ConsoleBlockEntity console, ClientTardis tardis, ModelPart root, MatrixStack matrices,
                                     VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float pAlpha) {
        matrices.push();
        matrices.translate(0.5f, -1.5f, -0.5f);

        matrices.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(180f));

        // Throttle Control
        ModelPart throttle = this.console.getChild("bone53").getChild("throttle");
        throttle.pitch = throttle.pitch + ((tardis.travel().speed() / (float) tardis.travel().maxSpeed().get()) * 1.5f);

        // Handbrake Control
        ModelPart handbrake = this.console.getChild("panelf").getChild("handbrake");
        handbrake.pitch = !tardis.travel().handbrake() ? handbrake.pitch - -0.57f : handbrake.pitch;

        // Anti Gravity Control
        ModelPart antigravs = this.console.getChild("panelf4").getChild("antigravs");
        antigravs.yaw = tardis.travel().antigravs().get() ? antigravs.yaw + 1f : antigravs.yaw;
        antigravs.roll = tardis.travel().antigravs().get() ? antigravs.roll + 0.2f : antigravs.roll;

        // Door Locking Mechanism Control
        ModelPart doorlock = this.console.getChild("panelf5").getChild("door_lock");
        doorlock.pitch = tardis.door().locked() ? doorlock.pitch + 0.5f : doorlock.pitch;

        // Door Control
        ModelPart doorControl = this.console.getChild("panelf5").getChild("doors");
        doorControl.pitch = tardis.door().isLeftOpen()
                ? doorControl.pitch - 0.5f
                : tardis.door().isRightOpen() ? doorControl.pitch - 1.55f : doorControl.pitch;

        // Alarm Control and Lights
        ModelPart alarms = this.console.getChild("panelf5").getChild("alarms");
        alarms.yaw = tardis.alarm().enabled().get() ? alarms.yaw + 0.2f : alarms.yaw;

        // Shields
        ModelPart shields = this.console.getChild("panelf5").getChild("rwf").getChild("bone56");
        shields.yaw = tardis.shields().shielded().get() ? shields.yaw + 1.85f : shields.yaw;

        // Refueler
        ModelPart refuel = this.console.getChild("bone53").getChild("refueler");
        refuel.roll = tardis.isRefueling() ? refuel.roll + 0.6f : refuel.roll;
        refuel.yaw = tardis.isRefueling() ? refuel.yaw + 0.1f : refuel.yaw;

        ModelPart direction = this.console.getChild("panelf").getChild("rotation");
        direction.pitch = direction.pitch + tardis.travel().destination().getRotation();

//
//        // Ground Search Control
//        ModelPart groundSearch = this.console.getChild("panel1").getChild("controls").getChild("smallswitch");
//        groundSearch.pitch = tardis.travel().horizontalSearch().get()
//                ? groundSearch.pitch + 1f
//                : groundSearch.pitch - 0.75f; // FIXME use TravelHandler#horizontalSearch/#verticalSearch
//
//        // Direction Control
//        ModelPart direction = this.console.getChild("panel6").getChild("controls2").getChild("smallnob2");
//        direction.pitch = direction.pitch + tardis.travel().destination().getRotation();
//
        // Increment Control
        ModelPart increment = this.console.getChild("panelf5").getChild("increment");
        increment.pitch = increment.pitch + ((IncrementManager.increment(tardis) / (float) IncrementManager.increment(tardis)) * 0.5f);
//
//        ModelPart fastReturnCover = this.console.getChild("panel4").getChild("controls4").getChild("tinyswitchcover");
//        ModelPart fastReturnLever = this.console.getChild("panel4").getChild("controls4").getChild("tinyswitch");

        super.renderWithAnimations(console, tardis, root, matrices, vertices, light, overlay, red, green, blue, pAlpha);
        matrices.pop();
    }

    @Override
    public Animation getAnimationForState(TravelHandlerBase.State state) {
        return switch (state) {
            default -> RenaissanceAnimation.FLIGHT;
            case LANDED -> RenaissanceAnimation.IDLE;
        };
    }

    @Override
    public ModelPart getPart() {
        return console;
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
        matrices.translate(-0.365, 1.01, 1.18);
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180f));
        matrices.scale(0.0035f, 0.0035f, 0.0035f);
        matrices.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(270f));
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
        matrices.translate(-0.17, 1.535, 0.565);
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180f));
        matrices.scale(0.0040f, 0.0040f, 0.0040f);
        matrices.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(270f));
        String progressText = tardis.travel().getState() == TravelHandlerBase.State.LANDED
                ? "0%"
                : tardis.travel().getDurationAsPercentage() + "%";
        matrices.translate(0, -38, -52);
        renderer.drawWithOutline(Text.of(progressText).asOrderedText(), 0 - renderer.getWidth(progressText) / 2, 0, 0xffffff, 0x03cffc,
                matrices.peek().getPositionMatrix(), vertexConsumers, 0xF000F0);
        matrices.pop();
    }
}