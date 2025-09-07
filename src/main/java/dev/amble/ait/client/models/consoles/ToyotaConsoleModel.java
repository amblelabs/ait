// Made with Blockbench 4.9.3
package dev.amble.ait.client.models.consoles;

import dev.amble.lib.data.CachedDirectedGlobalPos;

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

import dev.amble.ait.client.animation.console.toyota.ToyotaAnimations;
import dev.amble.ait.client.tardis.ClientTardis;
import dev.amble.ait.core.blockentities.ConsoleBlockEntity;
import dev.amble.ait.core.tardis.Tardis;
import dev.amble.ait.core.tardis.control.impl.DirectionControl;
import dev.amble.ait.core.tardis.control.impl.pos.IncrementManager;
import dev.amble.ait.core.tardis.handler.FuelHandler;
import dev.amble.ait.core.tardis.handler.travel.TravelHandler;
import dev.amble.ait.core.tardis.handler.travel.TravelHandlerBase;
import dev.amble.ait.core.util.WorldUtil;

public class ToyotaConsoleModel extends SimpleConsoleModel {
    private final ModelPart toyota;

    public ToyotaConsoleModel(ModelPart root) {
        this.toyota = root.getChild("toyota");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData toyota = modelPartData.addChild("toyota", ModelPartBuilder.create(),
                ModelTransform.pivot(0.0F, 24.0F, 0.0F));

        ModelPartData panel1 = toyota.addChild("panel1", ModelPartBuilder.create().uv(107, 185).cuboid(-14.0F,
                -14.9306F, -25.1225F, 28.0F, 3.0F, 0.0F, new Dilation(0.001F)),
                ModelTransform.pivot(0.0F, -2.0F, 0.0F));

        ModelPartData cube_r1 = panel1.addChild("cube_r1",
                ModelPartBuilder.create().uv(62, 184).cuboid(-1.0F, 0.9F, -1.1F, 2.0F, 1.0F, 2.0F, new Dilation(0.0F)),
                ModelTransform.of(9.0F, -17.0F, -23.0F, 0.2618F, 0.0F, 0.0F));

        ModelPartData cube_r2 = panel1.addChild("cube_r2",
                ModelPartBuilder.create().uv(186, 16).cuboid(-3.0F, -1.0F, -1.0F, 3.0F, 2.0F, 2.0F, new Dilation(0.0F)),
                ModelTransform.of(8.0F, -17.5609F, -17.0F, 0.2618F, 0.0F, 0.0F));

        ModelPartData cube_r3 = panel1
                .addChild("cube_r3",
                        ModelPartBuilder.create().uv(149, 156).cuboid(-1.0F, 5.5F, -28.0F, 2.0F, 2.0F, 22.0F,
                                new Dilation(-0.001F)),
                        ModelTransform.of(0.0F, -11.9609F, 0.0F, -0.2618F, 0.5236F, 0.0F));

        ModelPartData cube_r4 = panel1.addChild("cube_r4",
                ModelPartBuilder.create().uv(25, 180).cuboid(-1.0F, -1.5F, -1.0F, 2.0F, 3.0F, 2.0F, new Dilation(0.0F)),
                ModelTransform.of(-14.0582F, -13.4805F, -24.3496F, 0.0F, 0.5236F, 0.0F));

        ModelPartData cube_r5 = panel1.addChild("cube_r5", ModelPartBuilder.create().uv(0, 181).cuboid(-1.0F, -7.5F,
                -28.0F, 2.0F, 2.0F, 20.0F, new Dilation(0.001F)),
                ModelTransform.of(0.0F, -15.0F, 0.0F, 0.2618F, 0.5236F, 0.0F));

        ModelPartData cube_r6 = panel1
                .addChild(
                        "cube_r6", ModelPartBuilder.create().uv(108, 83).cuboid(-14.0F, -4.0F, -1.0F, 28.0F, 18.0F,
                                0.0F, new Dilation(0.001F)),
                        ModelTransform.of(0.0F, -11.8613F, -21.0F, 1.309F, 0.0F, 0.0F));

        ModelPartData cube_r7 = panel1
                .addChild(
                        "cube_r7", ModelPartBuilder.create().uv(108, 102).cuboid(-14.0F, -14.0F, -1.0F, 28.0F, 18.0F,
                                0.0F, new Dilation(0.001F)),
                        ModelTransform.of(0.0F, -15.0F, -21.0F, -1.309F, 0.0F, 0.0F));

        ModelPartData controls = panel1.addChild("controls", ModelPartBuilder.create(),
                ModelTransform.pivot(0.0F, -11.9609F, 0.0F));

        ModelPartData cube_r8 = controls.addChild("cube_r8", ModelPartBuilder.create().uv(77, 184).cuboid(-1.0F, -0.5F,
                -1.75F, 2.0F, 1.0F, 2.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, -6.6222F, -11.8592F, 0.2618F, 0.0F, 0.0F));

        ModelPartData cube_r9 = controls
                .addChild("cube_r9",
                        ModelPartBuilder.create().uv(27, 138).cuboid(-7.0F, 0.35F, -1.0F, 8.0F, 0.0F, 2.0F,
                                new Dilation(0.001F)),
                        ModelTransform.of(3.0F, -3.9015F, -23.3038F, 0.2618F, 0.0F, 0.0F));

        ModelPartData cube_r10 = controls.addChild("cube_r10",
                ModelPartBuilder.create().uv(7, 194).cuboid(5.0F, -11.25F, -21.0F, 1.0F, 2.0F, 2.0F, new Dilation(0.0F))
                        .uv(142, 194).cuboid(-7.0F, -11.25F, -21.0F, 1.0F, 2.0F, 2.0F, new Dilation(0.0F)),
                ModelTransform.of(0.5F, -0.1F, 0.0F, 0.2618F, 0.0F, 0.0F));

        ModelPartData powerlights = controls.addChild("powerlights", ModelPartBuilder.create(),
                ModelTransform.pivot(-7.0F, -3.975F, -20.5665F));

        ModelPartData cube_r11 = powerlights.addChild("cube_r11",
                ModelPartBuilder.create().uv(199, 128)
                        .cuboid(-0.5F, -1.0F, -0.5F, 1.0F, 2.0F, 1.0F, new Dilation(-0.2F)).uv(50, 201)
                        .cuboid(-2.5F, -1.0F, -0.5F, 1.0F, 2.0F, 1.0F, new Dilation(-0.2F)),
                ModelTransform.of(14.0F, 0.0F, 0.0F, 0.2618F, 0.0F, 0.0F));

        ModelPartData powerlights2 = powerlights.addChild("powerlights2", ModelPartBuilder.create(),
                ModelTransform.pivot(1.0F, 0.9478F, -0.014F));

        ModelPartData rightlight1 = powerlights2.addChild("rightlight1", ModelPartBuilder.create(),
                ModelTransform.pivot(13.0F, 0.0522F, 0.014F));

        ModelPartData cube_r12 = rightlight1.addChild("cube_r12", ModelPartBuilder.create().uv(199, 103).cuboid(-0.5F,
                -1.0541F, -0.5F, 1.0F, 2.0F, 1.0F, new Dilation(-0.2F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.2618F, 0.0F, 0.0F));

        ModelPartData leftlight1 = powerlights2.addChild("leftlight1", ModelPartBuilder.create(),
                ModelTransform.pivot(13.0F, 0.0522F, 0.014F));

        ModelPartData cube_r13 = leftlight1.addChild("cube_r13", ModelPartBuilder.create().uv(0, 196).cuboid(-2.5F,
                -1.0541F, -0.5F, 1.0F, 2.0F, 1.0F, new Dilation(-0.2F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.2618F, 0.0F, 0.0F));

        ModelPartData dooropenlights = controls.addChild("dooropenlights", ModelPartBuilder.create(),
                ModelTransform.pivot(-7.0F, -3.975F, -20.5665F));

        ModelPartData cube_r14 = dooropenlights.addChild("cube_r14",
                ModelPartBuilder.create().uv(0, 204).cuboid(-0.5F, -1.0F, -0.5F, 1.0F, 2.0F, 1.0F, new Dilation(-0.2F))
                        .uv(203, 175).cuboid(-2.5F, -1.0F, -0.5F, 1.0F, 2.0F, 1.0F, new Dilation(-0.2F)),
                ModelTransform.of(2.0F, 0.0F, 0.0F, 0.2618F, 0.0F, 0.0F));

        ModelPartData doorlights2 = dooropenlights.addChild("doorlights2", ModelPartBuilder.create(),
                ModelTransform.pivot(1.0F, 0.9478F, -0.014F));

        ModelPartData cube_r15 = doorlights2.addChild("cube_r15",
                ModelPartBuilder.create().uv(55, 201)
                        .cuboid(-2.5F, -1.0541F, -0.5F, 1.0F, 2.0F, 1.0F, new Dilation(-0.2F)).uv(60, 201)
                        .cuboid(-0.5F, -1.0541F, -0.5F, 1.0F, 2.0F, 1.0F, new Dilation(-0.2F)),
                ModelTransform.of(1.0F, 0.0522F, 0.014F, 0.2618F, 0.0F, 0.0F));

        ModelPartData dooropen = controls.addChild("dooropen", ModelPartBuilder.create(),
                ModelTransform.pivot(-5.5F, -4.8F, -22.0F));

        ModelPartData cube_r16 = dooropen.addChild("cube_r16",
                ModelPartBuilder.create().uv(142, 178)
                        .cuboid(-6.5F, -10.75F, -20.5F, 2.0F, 1.0F, 1.0F, new Dilation(0.0F)).uv(193, 173)
                        .cuboid(-4.75F, -10.75F, -21.5F, 0.0F, 1.0F, 2.0F, new Dilation(0.001F)).uv(186, 193)
                        .cuboid(-6.25F, -10.75F, -21.5F, 0.0F, 1.0F, 2.0F, new Dilation(0.001F)).uv(181, 195)
                        .cuboid(-6.25F, -10.75F, -21.5F, 2.0F, 1.0F, 0.0F, new Dilation(0.001F)).uv(192, 110)
                        .cuboid(-6.0F, -10.75F, -24.5F, 1.0F, 1.0F, 3.0F, new Dilation(0.0F)),
                ModelTransform.of(5.0F, 4.7F, 22.0F, 0.2618F, 0.0F, 0.0F));

        ModelPartData power = controls.addChild("power", ModelPartBuilder.create(),
                ModelTransform.pivot(5.5F, -4.8F, -22.0F));

        ModelPartData cube_r17 = power.addChild("cube_r17",
                ModelPartBuilder.create().uv(180, 36)
                        .cuboid(4.5F, -10.75F, -20.5F, 2.0F, 1.0F, 1.0F, new Dilation(0.0F)).uv(182, 127)
                        .cuboid(4.75F, -10.75F, -21.5F, 0.0F, 1.0F, 2.0F, new Dilation(0.001F)).uv(97, 187)
                        .cuboid(6.25F, -10.75F, -21.5F, 0.0F, 1.0F, 2.0F, new Dilation(0.001F)).uv(167, 195)
                        .cuboid(4.25F, -10.75F, -21.5F, 2.0F, 1.0F, 0.0F, new Dilation(0.001F)).uv(192, 90)
                        .cuboid(5.0F, -10.75F, -24.5F, 1.0F, 1.0F, 3.0F, new Dilation(0.0F)),
                ModelTransform.of(-5.0F, 4.7F, 22.0F, 0.2618F, 0.0F, 0.0F));

        ModelPartData geigercounter = controls.addChild("geigercounter", ModelPartBuilder.create(),
                ModelTransform.of(-7.0F, -4.0F, -19.0F, 0.3365F, 0.6699F, 0.2139F));

        ModelPartData cube_r18 = geigercounter.addChild("cube_r18",
                ModelPartBuilder.create().uv(107, 189)
                        .cuboid(-1.5F, -4.0F, -1.5F, 3.0F, 2.0F, 3.0F, new Dilation(-0.4F)).uv(120, 189)
                        .cuboid(-1.5F, -4.0F, -1.5F, 3.0F, 2.0F, 3.0F, new Dilation(-0.2F)).uv(62, 191)
                        .cuboid(-1.5F, -4.0F, -1.5F, 3.0F, 2.0F, 3.0F, new Dilation(-0.3F)),
                ModelTransform.of(0.0F, 2.2109F, 0.75F, 0.0F, 0.0873F, 0.0F));

        ModelPartData needle = geigercounter
                .addChild(
                        "needle", ModelPartBuilder.create().uv(164, 181).cuboid(-1.7401F, -0.15F, -0.735F, 2.0F, 0.0F,
                                1.0F, new Dilation(0.0F)),
                        ModelTransform.of(0.5F, -1.2891F, 0.25F, 0.0F, 0.8727F, 0.0F));

        ModelPartData lockernob1 = controls
                .addChild("lockernob1",
                        ModelPartBuilder.create().uv(207, 112).cuboid(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F,
                                new Dilation(0.0F)),
                        ModelTransform.of(-3.0F, -3.9015F, -23.3038F, 0.2618F, 0.0F, 0.0F));

        ModelPartData lockernob2 = controls.addChild("lockernob2",
                ModelPartBuilder.create().uv(207, 91).cuboid(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(-1.5F, -3.9015F, -23.3038F, 0.2618F, 0.0F, 0.0F));

        ModelPartData lockernob3 = controls.addChild("lockernob3",
                ModelPartBuilder.create().uv(207, 88).cuboid(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, -3.9015F, -23.3038F, 0.2793F, 0.0F, 0.0F));

        ModelPartData lockernob4 = controls.addChild("lockernob4",
                ModelPartBuilder.create().uv(207, 75).cuboid(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(1.5F, -3.9015F, -23.3038F, 0.2618F, 0.0F, 0.0F));

        ModelPartData lockernob5 = controls.addChild("lockernob5",
                ModelPartBuilder.create().uv(207, 72).cuboid(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(3.0F, -3.9015F, -23.3038F, 0.2618F, 0.0F, 0.0F));

        ModelPartData faucettaps1 = controls.addChild("faucettaps1", ModelPartBuilder.create(),
                ModelTransform.of(-3.225F, -6.2617F, -13.3693F, 0.2618F, 0.0F, 0.0F));

        ModelPartData pivot2 = faucettaps1.addChild("pivot2",
                ModelPartBuilder.create().uv(128, 178)
                        .cuboid(-1.475F, -0.7975F, 0.0637F, 2.0F, 1.0F, 1.0F, new Dilation(0.0F)).uv(30, 198)
                        .cuboid(-0.525F, -0.556F, -0.4513F, 1.0F, 1.0F, 1.0F, new Dilation(-0.25F)),
                ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData faucettaps2 = controls.addChild("faucettaps2",
                ModelPartBuilder.create().uv(135, 178)
                        .cuboid(-0.475F, -0.7975F, 0.0637F, 2.0F, 1.0F, 1.0F, new Dilation(0.0F)).uv(201, 152)
                        .cuboid(-0.525F, -0.556F, -0.4513F, 1.0F, 1.0F, 1.0F, new Dilation(-0.25F)),
                ModelTransform.of(3.275F, -6.2617F, -13.3693F, 0.2618F, 0.0F, 0.0F));

        ModelPartData redknob = controls.addChild("redknob",
                ModelPartBuilder.create().uv(207, 62).cuboid(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, -4.0309F, -20.8209F, 0.2618F, 0.0F, 0.0F));

        ModelPartData largefaucettap = controls
                .addChild(
                        "largefaucettap", ModelPartBuilder.create().uv(15, 204).cuboid(0.25F, -0.7823F, -2.1733F, 1.0F,
                                2.0F, 1.0F, new Dilation(-0.2F)),
                        ModelTransform.of(-3.25F, -5.65F, -15.05F, 0.2618F, 0.0F, 0.0F));

        ModelPartData largefaucettaplever = largefaucettap.addChild("largefaucettaplever",
                ModelPartBuilder.create().uv(207, 59)
                        .cuboid(-0.5F, -0.5F, -0.55F, 1.0F, 1.0F, 1.0F, new Dilation(-0.2F)).uv(40, 207)
                        .cuboid(-0.5F, -0.5F, -1.15F, 1.0F, 1.0F, 1.0F, new Dilation(-0.2F)).uv(25, 198)
                        .cuboid(-0.5F, -0.5F, -1.75F, 1.0F, 1.0F, 1.0F, new Dilation(-0.2F)).uv(195, 158)
                        .cuboid(-0.5F, -0.5F, -2.95F, 1.0F, 1.0F, 1.0F, new Dilation(-0.2F)).uv(13, 198)
                        .cuboid(-0.5F, -0.5F, -2.35F, 1.0F, 1.0F, 1.0F, new Dilation(-0.2F)),
                ModelTransform.pivot(0.75F, -0.8823F, -1.6233F));

        ModelPartData largefaucettap2 = controls
                .addChild(
                        "largefaucettap2", ModelPartBuilder.create().uv(90, 201).cuboid(0.25F, -0.7823F, -2.1733F, 1.0F,
                                2.0F, 1.0F, new Dilation(-0.2F)),
                        ModelTransform.of(-2.25F, -5.65F, -15.05F, 0.2618F, 0.0F, 0.0F));

        ModelPartData largefaucettaplever2 = largefaucettap2.addChild("largefaucettaplever2",
                ModelPartBuilder.create().uv(205, 41)
                        .cuboid(-0.5F, -0.5F, -0.55F, 1.0F, 1.0F, 1.0F, new Dilation(-0.2F)).uv(205, 38)
                        .cuboid(-0.5F, -0.5F, -1.15F, 1.0F, 1.0F, 1.0F, new Dilation(-0.2F)).uv(122, 195)
                        .cuboid(-0.5F, -0.5F, -1.75F, 1.0F, 1.0F, 1.0F, new Dilation(-0.2F)).uv(117, 195)
                        .cuboid(-0.5F, -0.5F, -2.95F, 1.0F, 1.0F, 1.0F, new Dilation(-0.2F)).uv(112, 195)
                        .cuboid(-0.5F, -0.5F, -2.35F, 1.0F, 1.0F, 1.0F, new Dilation(-0.2F)),
                ModelTransform.pivot(0.75F, -0.8823F, -1.6233F));

        ModelPartData largefaucettap3 = controls
                .addChild(
                        "largefaucettap3", ModelPartBuilder.create().uv(85, 201).cuboid(0.25F, -0.7823F, -2.1733F, 1.0F,
                                2.0F, 1.0F, new Dilation(-0.2F)),
                        ModelTransform.of(-1.25F, -5.65F, -15.05F, 0.2618F, 0.0F, 0.0F));

        ModelPartData largefaucettaplever3 = largefaucettap3.addChild("largefaucettaplever3",
                ModelPartBuilder.create().uv(205, 31)
                        .cuboid(-0.5F, -0.5F, -0.55F, 1.0F, 1.0F, 1.0F, new Dilation(-0.2F)).uv(205, 28)
                        .cuboid(-0.5F, -0.5F, -1.15F, 1.0F, 1.0F, 1.0F, new Dilation(-0.2F)).uv(107, 195)
                        .cuboid(-0.5F, -0.5F, -1.75F, 1.0F, 1.0F, 1.0F, new Dilation(-0.2F)).uv(72, 191)
                        .cuboid(-0.5F, -0.5F, -2.95F, 1.0F, 1.0F, 1.0F, new Dilation(-0.2F)).uv(152, 189)
                        .cuboid(-0.5F, -0.5F, -2.35F, 1.0F, 1.0F, 1.0F, new Dilation(-0.2F)),
                ModelTransform.pivot(0.75F, -0.8823F, -1.6233F));

        ModelPartData largefaucettap4 = controls
                .addChild(
                        "largefaucettap4", ModelPartBuilder.create().uv(80, 201).cuboid(0.25F, -0.7823F, -2.1733F, 1.0F,
                                2.0F, 1.0F, new Dilation(-0.2F)),
                        ModelTransform.of(-0.25F, -5.65F, -15.05F, 0.2618F, 0.0F, 0.0F));

        ModelPartData largefaucettaplever4 = largefaucettap4.addChild("largefaucettaplever4",
                ModelPartBuilder.create().uv(205, 25)
                        .cuboid(-0.5F, -0.5F, -0.55F, 1.0F, 1.0F, 1.0F, new Dilation(-0.2F)).uv(204, 127)
                        .cuboid(-0.5F, -0.5F, -1.15F, 1.0F, 1.0F, 1.0F, new Dilation(-0.2F)).uv(130, 189)
                        .cuboid(-0.5F, -0.5F, -1.75F, 1.0F, 1.0F, 1.0F, new Dilation(-0.2F)).uv(117, 189)
                        .cuboid(-0.5F, -0.5F, -2.95F, 1.0F, 1.0F, 1.0F, new Dilation(-0.2F)).uv(189, 13)
                        .cuboid(-0.5F, -0.5F, -2.35F, 1.0F, 1.0F, 1.0F, new Dilation(-0.2F)),
                ModelTransform.pivot(0.75F, -0.8823F, -1.6233F));

        ModelPartData largefaucettap5 = controls
                .addChild(
                        "largefaucettap5", ModelPartBuilder.create().uv(75, 201).cuboid(0.25F, -0.7823F, -2.1733F, 1.0F,
                                2.0F, 1.0F, new Dilation(-0.2F)),
                        ModelTransform.of(0.75F, -5.65F, -15.05F, 0.2618F, 0.0F, 0.0F));

        ModelPartData largefaucettaplever5 = largefaucettap5.addChild("largefaucettaplever5",
                ModelPartBuilder.create().uv(204, 102)
                        .cuboid(-0.5F, -0.5F, -0.55F, 1.0F, 1.0F, 1.0F, new Dilation(-0.2F)).uv(40, 204)
                        .cuboid(-0.5F, -0.5F, -1.15F, 1.0F, 1.0F, 1.0F, new Dilation(-0.2F)).uv(185, 188)
                        .cuboid(-0.5F, -0.5F, -1.75F, 1.0F, 1.0F, 1.0F, new Dilation(-0.2F)).uv(171, 188)
                        .cuboid(-0.5F, -0.5F, -2.95F, 1.0F, 1.0F, 1.0F, new Dilation(-0.2F)).uv(185, 175)
                        .cuboid(-0.5F, -0.5F, -2.35F, 1.0F, 1.0F, 1.0F, new Dilation(-0.2F)),
                ModelTransform.pivot(0.75F, -0.8823F, -1.6233F));

        ModelPartData largefaucettap6 = controls
                .addChild(
                        "largefaucettap6", ModelPartBuilder.create().uv(65, 201).cuboid(0.25F, -0.7823F, -2.1733F, 1.0F,
                                2.0F, 1.0F, new Dilation(-0.2F)),
                        ModelTransform.of(1.75F, -5.65F, -15.05F, 0.2618F, 0.0F, 0.0F));

        ModelPartData largefaucettaplever6 = largefaucettap6.addChild("largefaucettaplever6",
                ModelPartBuilder.create().uv(35, 204)
                        .cuboid(-0.5F, -0.5F, -0.55F, 1.0F, 1.0F, 1.0F, new Dilation(-0.2F)).uv(30, 204)
                        .cuboid(-0.5F, -0.5F, -1.15F, 1.0F, 1.0F, 1.0F, new Dilation(-0.2F)).uv(184, 135)
                        .cuboid(-0.5F, -0.5F, -1.75F, 1.0F, 1.0F, 1.0F, new Dilation(-0.2F)).uv(184, 13)
                        .cuboid(-0.5F, -0.5F, -2.95F, 1.0F, 1.0F, 1.0F, new Dilation(-0.2F)).uv(171, 181)
                        .cuboid(-0.5F, -0.5F, -2.35F, 1.0F, 1.0F, 1.0F, new Dilation(-0.2F)),
                ModelTransform.pivot(0.75F, -0.8823F, -1.6233F));

        ModelPartData smalllockernob = controls.addChild("smalllockernob", ModelPartBuilder.create(),
                ModelTransform.of(-5.5F, -5.7927F, -14.7259F, 0.2618F, 0.0F, 0.0F));

        ModelPartData pivot3 = smalllockernob.addChild("pivot3",
                ModelPartBuilder.create().uv(5, 204).cuboid(-0.5F, -0.375F, -0.5F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F))
                        .uv(207, 56).cuboid(-0.5F, -1.125F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.2F)),
                ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData smallswitch = controls.addChild("smallswitch",
                ModelPartBuilder.create().uv(172, 142)
                        .cuboid(0.0F, -1.6136F, -0.5033F, 0.0F, 1.0F, 1.0F, new Dilation(0.001F)).uv(91, 153)
                        .cuboid(-1.0F, -2.4136F, -1.0033F, 2.0F, 1.0F, 2.0F, new Dilation(-0.2F)),
                ModelTransform.of(6.5F, -5.4356F, -16.9125F, 0.2618F, 0.0F, 0.0F));

        ModelPartData tinylever = controls.addChild("tinylever", ModelPartBuilder.create(),
                ModelTransform.pivot(9.25F, -4.1268F, -22.8931F));

        ModelPartData cube_r19 = tinylever.addChild("cube_r19", ModelPartBuilder.create().uv(165, 85).cuboid(-0.5F,
                -1.5088F, 0.0328F, 1.0F, 2.0F, 0.0F, new Dilation(0.001F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.2618F, 0.0F, 0.0F));

        ModelPartData tinytinyswitch = controls.addChild("tinytinyswitch", ModelPartBuilder.create().uv(69, 178)
                .cuboid(-0.5F, -1.0F, 0.0F, 1.0F, 1.0F, 0.0F, new Dilation(0.001F)),
                ModelTransform.pivot(-3.5F, -4.0F, -21.25F));

        ModelPartData tinytinyswitch2 = controls.addChild("tinytinyswitch2",
                ModelPartBuilder.create().uv(178, 6).cuboid(-0.5F, -1.0F, 0.0F, 1.0F, 1.0F, 0.0F, new Dilation(0.001F)),
                ModelTransform.pivot(-2.5F, -4.0F, -21.25F));

        ModelPartData tinytinyswitch3 = controls.addChild("tinytinyswitch3",
                ModelPartBuilder.create().uv(178, 0).cuboid(-0.5F, -1.0F, 0.0F, 1.0F, 1.0F, 0.0F, new Dilation(0.001F)),
                ModelTransform.pivot(-1.5F, -4.0F, -21.25F));

        ModelPartData tinytinyswitch4 = controls.addChild("tinytinyswitch4", ModelPartBuilder.create().uv(175, 65)
                .cuboid(-0.5F, -1.0F, 0.0F, 1.0F, 1.0F, 0.0F, new Dilation(0.001F)),
                ModelTransform.pivot(1.5F, -4.0F, -21.25F));

        ModelPartData tinytinyswitch5 = controls.addChild("tinytinyswitch5", ModelPartBuilder.create().uv(172, 110)
                .cuboid(-0.5F, -1.0F, 0.0F, 1.0F, 1.0F, 0.0F, new Dilation(0.001F)),
                ModelTransform.pivot(2.5F, -4.0F, -21.25F));

        ModelPartData tinytinyswitch6 = controls.addChild("tinytinyswitch6", ModelPartBuilder.create().uv(73, 157)
                .cuboid(-0.5F, -1.0F, 0.0F, 1.0F, 1.0F, 0.0F, new Dilation(0.001F)),
                ModelTransform.pivot(3.5F, -4.0F, -21.25F));

        ModelPartData panel1lights = panel1.addChild("panel1lights", ModelPartBuilder.create(),
                ModelTransform.pivot(0.0F, -15.9359F, -20.5665F));

        ModelPartData panel2 = toyota
                .addChild(
                        "panel2", ModelPartBuilder.create().uv(164, 184).cuboid(-14.0F, -14.9306F, -25.1225F, 28.0F,
                                3.0F, 0.0F, new Dilation(0.001F)),
                        ModelTransform.of(0.0F, -2.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData cube_r20 = panel2
                .addChild("cube_r20",
                        ModelPartBuilder.create().uv(175, 0).cuboid(-1.0F, -8.023F, -31.6235F, 2.0F, 2.0F, 22.0F,
                                new Dilation(-0.001F)),
                        ModelTransform.of(0.0F, 2.0391F, 0.0F, -0.2618F, 0.5236F, 0.0F));

        ModelPartData cube_r21 = panel2
                .addChild("cube_r21",
                        ModelPartBuilder.create().uv(40, 180).cuboid(-1.0F, -15.5F, -1.0F, 2.0F, 3.0F, 2.0F,
                                new Dilation(0.0F)),
                        ModelTransform.of(-14.0582F, 0.5195F, -24.3496F, 0.0F, 0.5236F, 0.0F));

        ModelPartData cube_r22 = panel2
                .addChild(
                        "cube_r22", ModelPartBuilder.create().uv(180, 25).cuboid(-1.0F, -21.023F, -24.3765F, 2.0F, 2.0F,
                                20.0F, new Dilation(0.001F)),
                        ModelTransform.of(0.0F, -1.0F, 0.0F, 0.2618F, 0.5236F, 0.0F));

        ModelPartData cube_r23 = panel2
                .addChild(
                        "cube_r23", ModelPartBuilder.create().uv(57, 0).cuboid(-14.0F, -7.6235F, 12.523F, 28.0F, 18.0F,
                                0.0F, new Dilation(0.001F)),
                        ModelTransform.of(0.0F, 2.1387F, -21.0F, 1.309F, 0.0F, 0.0F));

        ModelPartData cube_r24 = panel2.addChild("cube_r24",
                ModelPartBuilder.create().uv(104, 57)
                        .cuboid(-7.0F, -0.1235F, -14.523F, 14.0F, 0.0F, 2.0F, new Dilation(0.001F)).uv(0, 135)
                        .cuboid(-7.0F, -14.1235F, -14.523F, 14.0F, 0.0F, 2.0F, new Dilation(0.001F)).uv(0, 138)
                        .cuboid(-7.0F, -0.1235F, -14.523F, 14.0F, 0.0F, 0.0F, new Dilation(0.001F)).uv(51, 76)
                        .cuboid(7.0F, -14.1235F, -14.523F, 0.0F, 14.0F, 2.0F, new Dilation(0.001F)).uv(51, 93)
                        .cuboid(-7.0F, -14.1235F, -14.523F, 0.0F, 14.0F, 2.0F, new Dilation(0.001F)).uv(57, 142)
                        .cuboid(-7.0F, -14.1235F, -12.523F, 14.0F, 14.0F, 0.0F, new Dilation(0.001F)).uv(0, 161)
                        .cuboid(-14.0F, -17.6235F, -14.523F, 28.0F, 18.0F, 0.0F, new Dilation(0.001F)),
                ModelTransform.of(0.0F, -1.0F, -21.0F, -1.309F, 0.0F, 0.0F));

        ModelPartData controls3 = panel2.addChild("controls3", ModelPartBuilder.create(),
                ModelTransform.pivot(1.25F, -4.5F, -8.0F));

        ModelPartData gears = controls3.addChild("gears", ModelPartBuilder.create(),
                ModelTransform.of(1.25F, -8.75F, -14.5F, 2.618F, 0.0F, 3.1416F));

        ModelPartData largegear1 = gears.addChild("largegear1", ModelPartBuilder.create(),
                ModelTransform.of(0.5F, -0.9F, -1.75F, -1.309F, 0.0F, 0.0F));

        ModelPartData bone7 = largegear1.addChild("bone7",
                ModelPartBuilder.create().uv(207, 0).cuboid(-0.5F, 0.7F, -1.4F, 1.0F, 1.0F, 1.0F, new Dilation(-0.25F))
                        .uv(175, 85).cuboid(-0.5F, 0.7415F, -0.6353F, 2.0F, 1.0F, 0.0F, new Dilation(0.001F))
                        .uv(75, 194).cuboid(1.5F, 0.7074F, -0.6765F, 1.0F, 1.0F, 2.0F, new Dilation(0.001F)),
                ModelTransform.pivot(-2.0F, -1.2683F, -1.3528F));

        ModelPartData bone8 = bone7.addChild("bone8", ModelPartBuilder.create().uv(192, 69).cuboid(-2.0F, -3.0F, 0.9F,
                4.0F, 4.0F, 0.0F, new Dilation(0.001F)), ModelTransform.pivot(2.0F, 2.2774F, 0.396F));

        ModelPartData largegear2 = gears.addChild("largegear2", ModelPartBuilder.create(),
                ModelTransform.of(6.0F, 0.1F, -5.15F, -1.309F, 0.0F, 0.0F));

        ModelPartData bone6 = largegear2.addChild("bone6",
                ModelPartBuilder.create().uv(207, 0).cuboid(-0.5F, 0.7F, -1.4F, 1.0F, 1.0F, 1.0F, new Dilation(-0.25F))
                        .uv(175, 85).cuboid(-0.5F, 0.7415F, -0.6353F, 2.0F, 1.0F, 0.0F, new Dilation(0.001F))
                        .uv(75, 194).cuboid(1.5F, 0.7074F, -0.6765F, 1.0F, 1.0F, 2.0F, new Dilation(0.001F)),
                ModelTransform.pivot(-2.0F, -1.2683F, -1.3528F));

        ModelPartData bone9 = bone6.addChild("bone9", ModelPartBuilder.create().uv(192, 69).cuboid(-2.0F, -3.0F, 0.9F,
                4.0F, 4.0F, 0.0F, new Dilation(0.001F)), ModelTransform.pivot(2.0F, 2.2774F, 0.396F));

        ModelPartData largegear3 = gears.addChild("largegear3", ModelPartBuilder.create(),
                ModelTransform.of(0.5F, 1.1F, -9.05F, -1.309F, 0.0F, 0.0F));

        ModelPartData bone10 = largegear3.addChild("bone10",
                ModelPartBuilder.create().uv(207, 0).cuboid(-0.5F, 0.7F, -1.4F, 1.0F, 1.0F, 1.0F, new Dilation(-0.25F))
                        .uv(175, 85).cuboid(-0.5F, 0.7415F, -0.6353F, 2.0F, 1.0F, 0.0F, new Dilation(0.001F))
                        .uv(75, 194).cuboid(1.5F, 0.7074F, -0.6765F, 1.0F, 1.0F, 2.0F, new Dilation(0.001F)),
                ModelTransform.pivot(-2.0F, -1.2683F, -1.3528F));

        ModelPartData bone11 = bone10.addChild("bone11", ModelPartBuilder.create().uv(192, 69).cuboid(-2.0F, -3.0F,
                0.9F, 4.0F, 4.0F, 0.0F, new Dilation(0.001F)), ModelTransform.pivot(2.0F, 2.2774F, 0.396F));

        ModelPartData tinygear1 = gears.addChild("tinygear1", ModelPartBuilder.create(),
                ModelTransform.pivot(0.25F, -0.402F, -5.5029F));

        ModelPartData cube_r25 = tinygear1.addChild("cube_r25", ModelPartBuilder.create().uv(207, 19).cuboid(-1.0F,
                -1.0F, 0.0F, 2.0F, 2.0F, 0.0F, new Dilation(0.001F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, -1.309F, 0.0F, 0.0F));

        ModelPartData tinygear2 = gears.addChild("tinygear2", ModelPartBuilder.create(),
                ModelTransform.pivot(3.75F, 0.3392F, -8.3922F));

        ModelPartData cube_r26 = tinygear2.addChild("cube_r26", ModelPartBuilder.create().uv(207, 16).cuboid(-1.0F,
                -1.0F, 0.0F, 2.0F, 2.0F, 0.0F, new Dilation(0.001F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, -1.309F, 0.0F, 0.0F));

        ModelPartData tinygear3 = gears.addChild("tinygear3", ModelPartBuilder.create(),
                ModelTransform.pivot(3.75F, -1.2432F, -2.8137F));

        ModelPartData cube_r27 = tinygear3.addChild("cube_r27", ModelPartBuilder.create().uv(207, 13).cuboid(-1.0F,
                -1.0F, 0.0F, 2.0F, 2.0F, 0.0F, new Dilation(0.001F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, -1.309F, 0.0F, 0.0F));

        ModelPartData tapnobs = controls3.addChild("tapnobs",
                ModelPartBuilder.create().uv(121, 178)
                        .cuboid(-1.0F, -0.5F, -0.25F, 2.0F, 1.0F, 1.0F, new Dilation(0.0F)).uv(206, 148)
                        .cuboid(-0.5F, -0.5F, -0.75F, 1.0F, 1.0F, 1.0F, new Dilation(-0.25F)),
                ModelTransform.of(1.0F, -12.6228F, -9.958F, 0.2618F, 0.0F, 0.0F));

        ModelPartData tapnobs2 = controls3.addChild("tapnobs2",
                ModelPartBuilder.create().uv(114, 178)
                        .cuboid(-1.0F, -0.5F, -0.25F, 2.0F, 1.0F, 1.0F, new Dilation(0.0F)).uv(140, 206)
                        .cuboid(-0.5F, -0.5F, -0.75F, 1.0F, 1.0F, 1.0F, new Dilation(-0.25F)),
                ModelTransform.of(-2.5F, -13.3728F, -7.058F, 0.2618F, 0.0F, 0.0F));

        ModelPartData tapnobs3 = controls3.addChild("tapnobs3",
                ModelPartBuilder.create().uv(107, 178)
                        .cuboid(-1.0F, -0.5F, -0.25F, 2.0F, 1.0F, 1.0F, new Dilation(0.0F)).uv(206, 138)
                        .cuboid(-0.5F, -0.5F, -0.75F, 1.0F, 1.0F, 1.0F, new Dilation(-0.25F)),
                ModelTransform.of(-2.5F, -11.7728F, -12.808F, 0.2618F, 0.0F, 0.0F));

        ModelPartData keyhole = controls3.addChild("keyhole", ModelPartBuilder.create().uv(135, 202).cuboid(-0.5F,
                -1.0F, -0.5F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(-4.25F, -13.8F, -2.15F));

        ModelPartData tinytapnob = controls3.addChild("tinytapnob",
                ModelPartBuilder.create().uv(206, 135)
                        .cuboid(-0.5F, -0.9167F, -0.6667F, 1.0F, 1.0F, 1.0F, new Dilation(-0.25F)).uv(130, 202)
                        .cuboid(-0.5F, -0.1667F, -0.6667F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F)).uv(135, 206)
                        .cuboid(-0.5F, -0.9167F, -0.1667F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.pivot(2.0F, -14.6333F, -2.0833F));

        ModelPartData tinytapnob2 = controls3.addChild("tinytapnob2",
                ModelPartBuilder.create().uv(130, 206)
                        .cuboid(-0.5F, -0.9167F, -0.6667F, 1.0F, 1.0F, 1.0F, new Dilation(-0.25F)).uv(125, 202)
                        .cuboid(-0.5F, -0.1667F, -0.6667F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F)).uv(125, 206)
                        .cuboid(-0.5F, -0.9167F, -0.1667F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(-9.0F, -12.4333F, -12.0833F, 0.2618F, 0.0F, 0.0F));

        ModelPartData tinytapnob3 = controls3.addChild("tinytapnob3", ModelPartBuilder.create(),
                ModelTransform.of(-11.0F, -11.4512F, -15.2875F, 0.2618F, 0.0F, 0.0F));

        ModelPartData pivot4 = tinytapnob3.addChild("pivot4",
                ModelPartBuilder.create().uv(206, 7)
                        .cuboid(-0.5F, -0.75F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.25F)).uv(20, 204)
                        .cuboid(-0.5F, 0.0F, -0.5F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F)).uv(176, 174)
                        .cuboid(-1.0F, -1.25F, -1.0F, 2.0F, 1.0F, 2.0F, new Dilation(-0.25F)),
                ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData tinytapnob4 = controls3.addChild("tinytapnob4",
                ModelPartBuilder.create().uv(202, 17)
                        .cuboid(-0.5F, -0.3749F, -0.5F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F)).uv(175, 18)
                        .cuboid(-1.0F, -1.1251F, -1.0F, 2.0F, 1.0F, 2.0F, new Dilation(-0.25F)),
                ModelTransform.of(6.5F, -11.0891F, -15.1904F, 0.2618F, 0.0F, 0.0F));

        ModelPartData siegemode = controls3.addChild("siegemode",
                ModelPartBuilder.create().uv(104, 60)
                        .cuboid(-1.6226F, -9.1265F, -25.2794F, 3.0F, 0.0F, 2.0F, new Dilation(0.001F)).uv(137, 57)
                        .cuboid(-1.6226F, -10.1265F, -24.7794F, 3.0F, 1.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(-1.25F, 6.5F, 8.0F, 0.0F, -2.0944F, 0.0F));

        ModelPartData cube_r28 = siegemode
                .addChild("cube_r28",
                        ModelPartBuilder.create().uv(91, 147).cuboid(12.25F, -15.2617F, -17.7352F, 3.0F, 0.0F, 5.0F,
                                new Dilation(0.001F)),
                        ModelTransform.of(-13.8726F, -10.8765F, 0.0554F, 0.7854F, 0.0F, 0.0F));

        ModelPartData siegemodehandle = siegemode.addChild("siegemodehandle",
                ModelPartBuilder.create().uv(175, 60)
                        .cuboid(1.0F, -3.0333F, -0.5F, 0.0F, 3.0F, 1.0F, new Dilation(0.0F)).uv(183, 110)
                        .cuboid(-1.0F, -3.0333F, -0.5F, 0.0F, 3.0F, 1.0F, new Dilation(0.0F)).uv(42, 135)
                        .cuboid(-1.5F, -3.5333F, -0.5F, 3.0F, 1.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.pivot(-0.1226F, -9.5932F, -24.2794F));

        ModelPartData panel3 = toyota.addChild("panel3",
                ModelPartBuilder.create().uv(118, 60)
                        .cuboid(-13.8726F, -7.6806F, -17.4019F, 28.0F, 3.0F, 0.0F, new Dilation(0.001F)).uv(183, 115)
                        .cuboid(2.35F, -8.0962F, -17.6985F, 1.0F, 4.0F, 0.0F, new Dilation(0.001F)).uv(113, 31)
                        .cuboid(0.35F, -5.0962F, -17.6985F, 2.0F, 1.0F, 0.0F, new Dilation(0.001F)).uv(48, 138)
                        .cuboid(0.35F, -8.0962F, -17.6985F, 1.0F, 3.0F, 0.0F, new Dilation(0.001F)),
                ModelTransform.of(6.75F, -9.25F, 3.75F, 0.0F, -2.0944F, 0.0F));

        ModelPartData cube_r29 = panel3.addChild("cube_r29",
                ModelPartBuilder.create().uv(146, 57)
                        .cuboid(0.6F, -12.4F, -15.0F, 1.0F, 0.0F, 2.0F, new Dilation(0.001F)).uv(113, 46)
                        .cuboid(2.6F, -12.4F, -15.0F, 1.0F, 0.0F, 1.0F, new Dilation(0.0F)).uv(165, 148)
                        .cuboid(2.35F, -12.9F, -14.0F, 2.0F, 1.0F, 5.0F, new Dilation(0.0F)).uv(100, 153)
                        .cuboid(-4.85F, -13.9F, -12.35F, 0.0F, 2.0F, 3.0F, new Dilation(0.001F)).uv(165, 142)
                        .cuboid(-7.1F, -13.9F, -11.1F, 3.0F, 2.0F, 0.0F, new Dilation(0.001F)).uv(19, 139)
                        .cuboid(5.85F, -13.91F, -8.0F, 2.0F, 0.0F, 2.0F, new Dilation(0.0F)).uv(66, 58)
                        .cuboid(6.35F, -13.9F, -7.5F, 1.0F, 3.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(-0.25F, 0.0F, 0.0F, 0.2618F, 0.0F, 0.0F));

        ModelPartData cube_r30 = panel3
                .addChild("cube_r30",
                        ModelPartBuilder.create().uv(153, 35).cuboid(-1.0F, -8.023F, -31.6235F, 2.0F, 2.0F, 22.0F,
                                new Dilation(-0.001F)),
                        ModelTransform.of(0.1274F, 9.2891F, 7.7207F, -0.2618F, 0.5236F, 0.0F));

        ModelPartData cube_r31 = panel3.addChild("cube_r31",
                ModelPartBuilder.create().uv(171, 0).cuboid(-1.0F, -15.5F, -1.0F, 2.0F, 3.0F, 2.0F, new Dilation(0.0F)),
                ModelTransform.of(-13.9308F, 7.7695F, -16.6289F, 0.0F, 0.5236F, 0.0F));

        ModelPartData cube_r32 = panel3
                .addChild("cube_r32",
                        ModelPartBuilder.create().uv(176, 135).cuboid(-1.0F, -21.023F, -24.3765F, 2.0F, 2.0F, 20.0F,
                                new Dilation(0.001F)),
                        ModelTransform.of(0.1274F, 6.25F, 7.7207F, 0.2618F, 0.5236F, 0.0F));

        ModelPartData cube_r33 = panel3
                .addChild("cube_r33",
                        ModelPartBuilder.create().uv(0, 0).cuboid(-14.0F, -17.9761F, -0.0344F, 28.0F, 18.0F, 0.0F,
                                new Dilation(0.001F)),
                        ModelTransform.of(0.1274F, -0.0613F, -0.0293F, 1.309F, 0.0F, 0.0F));

        ModelPartData cube_r34 = panel3
                .addChild("cube_r34",
                        ModelPartBuilder.create().uv(108, 64).cuboid(-14.0F, -17.6235F, -14.523F, 28.0F, 18.0F, 0.0F,
                                new Dilation(0.001F)),
                        ModelTransform.of(0.1274F, 6.25F, -13.2793F, -1.309F, 0.0F, 0.0F));

        ModelPartData lights = panel3.addChild("lights", ModelPartBuilder.create(),
                ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData bone2 = lights.addChild("bone2", ModelPartBuilder.create(),
                ModelTransform.pivot(0.0F, 0.5F, -1.75F));

        ModelPartData cube_r35 = bone2.addChild("cube_r35",
                ModelPartBuilder.create().uv(113, 42).cuboid(6.1F, -12.9F, -4.0F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.2618F, 0.0F, 0.0F));

        ModelPartData lights2 = bone2.addChild("lights2", ModelPartBuilder.create(),
                ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData cube_r36 = lights2.addChild("cube_r36", ModelPartBuilder.create().uv(202, 123).cuboid(6.1F,
                -10.9681F, -4.5176F, 1.0F, 1.0F, 1.0F, new Dilation(0.01F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.2618F, 0.0F, 0.0F));

        ModelPartData bone = lights.addChild("bone", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData cube_r37 = bone.addChild("cube_r37", ModelPartBuilder.create().uv(100, 201).cuboid(6.1F, -12.9F,
                -4.0F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.2618F, 0.0F, 0.0F));

        ModelPartData lights1 = bone.addChild("lights1", ModelPartBuilder.create(),
                ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData cube_r38 = lights1.addChild("cube_r38", ModelPartBuilder.create().uv(25, 204).cuboid(6.1F,
                -10.9681F, -4.5176F, 1.0F, 1.0F, 1.0F, new Dilation(0.01F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.2618F, 0.0F, 0.0F));

        ModelPartData controls5 = panel3.addChild("controls5", ModelPartBuilder.create(),
                ModelTransform.pivot(0.1274F, 9.2891F, 7.7207F));

        ModelPartData sonicport = controls5.addChild("sonicport",
                ModelPartBuilder.create().uv(180, 39)
                        .cuboid(-7.0F, -20.0F, -18.0F, 3.0F, 2.0F, 3.0F, new Dilation(0.0F)).uv(171, 6)
                        .cuboid(-6.5F, -21.0F, -17.5F, 2.0F, 1.0F, 2.0F, new Dilation(0.0F)),
                ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData cube_r39 = sonicport
                .addChild("cube_r39",
                        ModelPartBuilder.create().uv(33, 135).cuboid(6.15F, -1.0F, 1.5974F, 4.0F, 2.0F, 0.0F,
                                new Dilation(0.0F)),
                        ModelTransform.of(-12.4067F, -20.5F, -11.8481F, 0.0F, 0.7854F, 0.0F));

        ModelPartData cube_r40 = sonicport
                .addChild(
                        "cube_r40", ModelPartBuilder.create().uv(165, 135).cuboid(0.35F, -1.0F, -0.95F, 0.0F, 2.0F,
                                4.0F, new Dilation(0.0F)),
                        ModelTransform.of(-6.5F, -20.5F, -17.0F, 0.0F, 0.7854F, 0.0F));

        ModelPartData speaker = controls5.addChild("speaker", ModelPartBuilder.create(),
                ModelTransform.pivot(4.5F, -19.6609F, -15.1033F));

        ModelPartData cube_r41 = speaker.addChild("cube_r41", ModelPartBuilder.create().uv(171, 13).cuboid(-3.0F, -0.1F,
                -1.9F, 4.0F, 0.0F, 4.0F, new Dilation(0.001F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.2618F, 0.0F, 0.0F));

        ModelPartData cube_r42 = speaker
                .addChild(
                        "cube_r42", ModelPartBuilder.create().uv(180, 149).cuboid(-2.25F, -1.0F, -2.15F, 3.0F, 2.0F,
                                3.0F, new Dilation(0.0F)),
                        ModelTransform.of(0.0F, 0.0F, 0.0F, 0.3622F, 0.7519F, 0.2533F));

        ModelPartData spinnything1 = controls5.addChild("spinnything1",
                ModelPartBuilder.create().uv(40, 186).cuboid(-1.0F, -1.0F, -0.5F, 2.0F, 2.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(3.0F, -21.2016F, -11.2982F, 0.2618F, 0.0F, 0.0F));

        ModelPartData spinnything2 = controls5.addChild("spinnything2",
                ModelPartBuilder.create().uv(25, 186).cuboid(-1.0F, -1.0F, -0.5F, 2.0F, 2.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, -21.2016F, -11.2982F, 0.2618F, 0.0F, 0.0F));

        ModelPartData spinnything3 = controls5
                .addChild("spinnything3",
                        ModelPartBuilder.create().uv(175, 148).cuboid(-1.0F, -1.0F, -0.5F, 2.0F, 2.0F, 1.0F,
                                new Dilation(0.0F)),
                        ModelTransform.of(-3.0F, -21.2016F, -11.2982F, 0.2618F, 0.0F, 0.0F));

        ModelPartData sliders = spinnything3.addChild("sliders",
                ModelPartBuilder.create().uv(207, 115)
                        .cuboid(0.75F, -21.4516F, -13.9982F, 1.0F, 1.0F, 1.0F, new Dilation(-0.25F)).uv(85, 205)
                        .cuboid(-0.25F, -21.4516F, -13.9982F, 1.0F, 1.0F, 1.0F, new Dilation(-0.25F)).uv(90, 205)
                        .cuboid(-1.25F, -21.4516F, -13.9982F, 1.0F, 1.0F, 1.0F, new Dilation(-0.25F)).uv(100, 205)
                        .cuboid(-2.25F, -21.4516F, -13.9982F, 1.0F, 1.0F, 1.0F, new Dilation(-0.25F)).uv(80, 205)
                        .cuboid(-4.25F, -21.4516F, -13.9982F, 1.0F, 1.0F, 1.0F, new Dilation(-0.25F)).uv(105, 205)
                        .cuboid(-3.25F, -21.4516F, -13.9982F, 1.0F, 1.0F, 1.0F, new Dilation(-0.25F)),
                ModelTransform.pivot(3.0F, 21.2016F, 11.2982F));

        ModelPartData slider1 = sliders.addChild("slider1", ModelPartBuilder.create().uv(50, 205).cuboid(-4.25F,
                -21.4516F, -14.9982F, 1.0F, 1.0F, 1.0F, new Dilation(-0.25F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData slider2 = sliders.addChild("slider2", ModelPartBuilder.create().uv(55, 205).cuboid(-3.25F,
                -21.4516F, -14.9982F, 1.0F, 1.0F, 1.0F, new Dilation(-0.25F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData slider3 = sliders.addChild("slider3", ModelPartBuilder.create().uv(60, 205).cuboid(-2.25F,
                -21.4516F, -14.9982F, 1.0F, 1.0F, 1.0F, new Dilation(-0.25F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData slider4 = sliders.addChild("slider4", ModelPartBuilder.create().uv(65, 205).cuboid(-1.25F,
                -21.4516F, -14.9982F, 1.0F, 1.0F, 1.0F, new Dilation(-0.25F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData slider5 = sliders.addChild("slider5", ModelPartBuilder.create().uv(75, 205).cuboid(-0.25F,
                -21.4516F, -14.9982F, 1.0F, 1.0F, 1.0F, new Dilation(-0.25F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData slider6 = sliders.addChild("slider6", ModelPartBuilder.create().uv(207, 165).cuboid(0.75F,
                -21.4516F, -14.9982F, 1.0F, 1.0F, 1.0F, new Dilation(-0.25F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData rotaryswitch1 = controls5.addChild("rotaryswitch1",
                ModelPartBuilder.create().uv(180, 142)
                        .cuboid(-0.75F, -0.0647F, -0.0085F, 2.0F, 0.0F, 1.0F, new Dilation(0.0F)).uv(205, 142)
                        .cuboid(-0.75F, -0.4353F, -0.4915F, 1.0F, 1.0F, 1.0F, new Dilation(-0.25F)),
                ModelTransform.of(5.6226F, -18.631F, -20.0501F, 0.2618F, 0.0F, 0.0F));

        ModelPartData rotaryswitch2 = controls5.addChild("rotaryswitch2",
                ModelPartBuilder.create().uv(165, 80)
                        .cuboid(-0.75F, 0.0F, -0.017F, 2.0F, 0.0F, 1.0F, new Dilation(0.0F)).uv(115, 205)
                        .cuboid(-0.75F, -0.3706F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.25F)),
                ModelTransform.of(5.6226F, -18.2427F, -21.499F, 0.2618F, 0.0F, 0.0F));

        ModelPartData rotaryswitch3 = controls5.addChild("rotaryswitch3",
                ModelPartBuilder.create().uv(66, 157)
                        .cuboid(-0.75F, 0.0F, -0.017F, 2.0F, 0.0F, 1.0F, new Dilation(0.0F)).uv(110, 205)
                        .cuboid(-0.75F, -0.3706F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.25F)),
                ModelTransform.of(5.6226F, -17.8545F, -22.9479F, 0.2618F, 0.0F, 0.0F));

        ModelPartData panel4 = toyota.addChild("panel4",
                ModelPartBuilder.create().uv(107, 181)
                        .cuboid(-14.0F, -14.9306F, -25.1225F, 28.0F, 3.0F, 0.0F, new Dilation(0.001F)).uv(201, 139)
                        .cuboid(-3.5F, -19.25F, -12.5F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F)).uv(201, 135)
                        .cuboid(-2.0F, -19.25F, -12.5F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F)).uv(115, 201)
                        .cuboid(-0.5F, -19.25F, -12.5F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F)).uv(110, 201)
                        .cuboid(1.0F, -19.25F, -12.5F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F)).uv(105, 201)
                        .cuboid(2.5F, -19.25F, -12.5F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, -2.0F, 0.0F, 0.0F, 3.1416F, 0.0F));

        ModelPartData cube_r43 = panel4.addChild("cube_r43",
                ModelPartBuilder.create().uv(186, 0).cuboid(-1.5F, -1.0F, -1.0F, 3.0F, 2.0F, 2.0F, new Dilation(0.0F))
                        .uv(186, 5).cuboid(-8.5F, -1.0F, -1.0F, 3.0F, 2.0F, 2.0F, new Dilation(0.0F)),
                ModelTransform.of(3.5F, -17.65F, -16.0F, 0.2618F, 0.0F, 0.0F));

        ModelPartData cube_r44 = panel4.addChild("cube_r44",
                ModelPartBuilder.create().uv(192, 98).cuboid(6.0F, -22.4F, -19.4F, 4.0F, 2.0F, 2.0F, new Dilation(0.0F))
                        .uv(86, 142).cuboid(-3.0F, -21.4F, -16.0F, 8.0F, 2.0F, 2.0F, new Dilation(0.0F)).uv(192, 123)
                        .cuboid(-9.0F, -21.9F, -18.9F, 3.0F, 1.0F, 3.0F, new Dilation(0.0F)),
                ModelTransform.of(-1.0F, 0.0F, 0.0F, 0.2618F, 0.0F, 0.0F));

        ModelPartData cube_r45 = panel4
                .addChild("cube_r45",
                        ModelPartBuilder.create().uv(165, 110).cuboid(-1.0F, -8.023F, -31.6235F, 2.0F, 2.0F, 22.0F,
                                new Dilation(-0.001F)),
                        ModelTransform.of(0.0F, 2.0391F, 0.0F, -0.2618F, 0.5236F, 0.0F));

        ModelPartData cube_r46 = panel4
                .addChild("cube_r46",
                        ModelPartBuilder.create().uv(77, 178).cuboid(-1.0F, -15.5F, -1.0F, 2.0F, 3.0F, 2.0F,
                                new Dilation(0.0F)),
                        ModelTransform.of(-14.0582F, 0.5195F, -24.3496F, 0.0F, 0.5236F, 0.0F));

        ModelPartData cube_r47 = panel4
                .addChild(
                        "cube_r47", ModelPartBuilder.create().uv(178, 161).cuboid(-1.0F, -21.023F, -24.3765F, 2.0F,
                                2.0F, 20.0F, new Dilation(0.001F)),
                        ModelTransform.of(0.0F, -1.0F, 0.0F, 0.2618F, 0.5236F, 0.0F));

        ModelPartData cube_r48 = panel4
                .addChild(
                        "cube_r48", ModelPartBuilder.create().uv(0, 57).cuboid(-14.0F, -7.6235F, 12.523F, 28.0F, 18.0F,
                                0.0F, new Dilation(0.001F)),
                        ModelTransform.of(0.0F, 2.1387F, -21.0F, 1.309F, 0.0F, 0.0F));

        ModelPartData cube_r49 = panel4
                .addChild(
                        "cube_r49", ModelPartBuilder.create().uv(114, 159).cuboid(-14.0F, -17.6235F, -14.523F, 28.0F,
                                18.0F, 0.0F, new Dilation(0.001F)),
                        ModelTransform.of(0.0F, -1.0F, -21.0F, -1.309F, 0.0F, 0.0F));

        ModelPartData controls4 = panel4.addChild("controls4", ModelPartBuilder.create(),
                ModelTransform.pivot(-1.0F, 0.0F, 0.0F));

        ModelPartData tinyswitchcover = controls4
                .addChild("tinyswitchcover",
                        ModelPartBuilder.create().uv(192, 103).cuboid(-1.0F, -1.0262F, -1.9978F, 1.0F, 1.0F, 2.0F,
                                new Dilation(0.0F)),
                        ModelTransform.of(12.0F, -15.6585F, -22.2118F, 0.2618F, 0.0F, 0.0F));

        ModelPartData tinyswitch = controls4.addChild("tinyswitch", ModelPartBuilder.create(),
                ModelTransform.pivot(11.5F, -15.1847F, -23.2096F));

        ModelPartData cube_r50 = tinyswitch.addChild("cube_r50", ModelPartBuilder.create().uv(105, 142).cuboid(1.25F,
                -1.15F, 0.0F, 1.0F, 1.0F, 0.0F, new Dilation(0.001F)),
                ModelTransform.of(-1.5F, 0.0F, 0.0F, 0.2618F, 0.0F, 0.0F));

        ModelPartData throttle = controls4.addChild("throttle", ModelPartBuilder.create(),
                ModelTransform.of(8.0F, -15.9197F, -23.197F, -0.7854F, 0.0F, 0.0F));

        ModelPartData cube_r51 = throttle.addChild("cube_r51",
                ModelPartBuilder.create().uv(174, 129)
                        .cuboid(-1.25F, -23.5F, -18.5F, 2.0F, 1.0F, 1.0F, new Dilation(0.0F)).uv(86, 178)
                        .cuboid(1.25F, -23.5F, -18.5F, 2.0F, 1.0F, 1.0F, new Dilation(0.0F)).uv(47, 186)
                        .cuboid(2.75F, -22.5F, -18.5F, 0.0F, 2.0F, 1.0F, new Dilation(0.001F)).uv(187, 138)
                        .cuboid(-0.75F, -22.5F, -18.5F, 0.0F, 2.0F, 1.0F, new Dilation(0.001F)).uv(113, 60)
                        .cuboid(-0.75F, -21.0F, -18.5F, 1.0F, 0.0F, 1.0F, new Dilation(0.001F)).uv(98, 153)
                        .cuboid(1.75F, -21.0F, -18.5F, 1.0F, 0.0F, 1.0F, new Dilation(0.001F)).uv(149, 194)
                        .cuboid(2.25F, -21.0F, -18.5F, 0.0F, 2.0F, 1.0F, new Dilation(0.001F)).uv(195, 161)
                        .cuboid(-0.25F, -21.0F, -18.5F, 0.0F, 2.0F, 1.0F, new Dilation(0.001F)),
                ModelTransform.of(-1.0F, 13.6722F, 22.2068F, 0.2618F, 0.0F, 0.0F));

        ModelPartData handbrake = controls4.addChild("handbrake", ModelPartBuilder.create(),
                ModelTransform.of(-7.45F, -16.4308F, -22.5199F, 0.2618F, 0.0F, 0.0F));

        ModelPartData pivot = handbrake.addChild("pivot", ModelPartBuilder.create(),
                ModelTransform.pivot(-0.05F, -0.25F, 0.0F));

        ModelPartData cube_r52 = pivot.addChild("cube_r52",
                ModelPartBuilder.create().uv(57, 157)
                        .cuboid(0.6F, -0.058F, -0.5647F, 3.0F, 0.0F, 1.0F, new Dilation(0.001F)).uv(142, 189)
                        .cuboid(-2.5F, 0.041F, -1.5647F, 3.0F, 1.0F, 3.0F, new Dilation(0.1F)),
                ModelTransform.of(1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F));

        ModelPartData tinyswitch2 = controls4.addChild("tinyswitch2",
                ModelPartBuilder.create().uv(54, 76).cuboid(0.25F, -1.0F, 0.0F, 1.0F, 1.0F, 0.0F, new Dilation(0.001F))
                        .uv(54, 93).cuboid(-0.75F, -0.983F, 0.0F, 1.0F, 1.0F, 0.0F, new Dilation(0.001F)),
                ModelTransform.of(-5.25F, -17.1495F, -17.0152F, 0.2618F, 0.0F, 0.0F));

        ModelPartData lockernob = controls4.addChild("lockernob", ModelPartBuilder.create(),
                ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData cube_r53 = lockernob.addChild("cube_r53", ModelPartBuilder.create().uv(202, 13).cuboid(-2.5F,
                -22.4F, -15.5F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.2618F, 0.0F, 0.0F));

        ModelPartData lockernob6 = controls4.addChild("lockernob6", ModelPartBuilder.create(),
                ModelTransform.pivot(1.5F, 0.0F, 0.0F));

        ModelPartData cube_r54 = lockernob6.addChild("cube_r54", ModelPartBuilder.create().uv(202, 4).cuboid(-2.0F,
                -22.4F, -15.5F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.2618F, 0.0F, 0.0F));

        ModelPartData lockernob7 = controls4.addChild("lockernob7", ModelPartBuilder.create(),
                ModelTransform.pivot(3.0F, 0.0F, 0.0F));

        ModelPartData cube_r55 = lockernob7.addChild("cube_r55", ModelPartBuilder.create().uv(202, 0).cuboid(-1.5F,
                -22.4F, -15.5F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.2618F, 0.0F, 0.0F));

        ModelPartData lockernob8 = controls4.addChild("lockernob8", ModelPartBuilder.create(),
                ModelTransform.pivot(5.0F, 0.0F, 0.0F));

        ModelPartData cube_r56 = lockernob8.addChild("cube_r56", ModelPartBuilder.create().uv(201, 148).cuboid(-1.5F,
                -22.4F, -15.5F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.2618F, 0.0F, 0.0F));

        ModelPartData rotatingclockthing = controls4.addChild("rotatingclockthing",
                ModelPartBuilder.create().uv(181, 188)
                        .cuboid(-1.5F, -1.5F, -1.5F, 0.0F, 3.0F, 3.0F, new Dilation(0.001F)).uv(167, 188)
                        .cuboid(0.0F, -1.5F, -1.5F, 0.0F, 3.0F, 3.0F, new Dilation(0.001F)).uv(180, 135)
                        .cuboid(1.5F, -1.5F, -1.5F, 0.0F, 3.0F, 3.0F, new Dilation(0.001F)),
                ModelTransform.of(1.0F, -17.5F, -16.5F, 0.2618F, 0.0F, 0.0F));

        ModelPartData coloredlever = controls4.addChild("coloredlever", ModelPartBuilder.create().uv(165, 135)
                .cuboid(-0.5F, -3.0F, 0.0F, 1.0F, 3.0F, 0.0F, new Dilation(0.001F)),
                ModelTransform.pivot(-1.75F, -19.25F, -12.0F));

        ModelPartData coloredlever2 = controls4.addChild("coloredlever2", ModelPartBuilder.create().uv(171, 13)
                .cuboid(-0.75F, -3.0F, 0.0F, 1.0F, 3.0F, 0.0F, new Dilation(0.001F)),
                ModelTransform.pivot(0.0F, -19.25F, -12.0F));

        ModelPartData coloredlever3 = controls4.addChild("coloredlever3", ModelPartBuilder.create().uv(183, 120)
                .cuboid(-0.75F, -3.0F, 0.0F, 1.0F, 3.0F, 0.0F, new Dilation(0.001F)),
                ModelTransform.pivot(1.5F, -19.25F, -12.0F));

        ModelPartData coloredlever4 = controls4.addChild("coloredlever4", ModelPartBuilder.create().uv(184, 101)
                .cuboid(-0.5F, -3.0F, 0.0F, 1.0F, 3.0F, 0.0F, new Dilation(0.001F)),
                ModelTransform.pivot(2.75F, -19.25F, -12.0F));

        ModelPartData coloredlever5 = controls4.addChild("coloredlever5", ModelPartBuilder.create().uv(32, 186)
                .cuboid(-0.75F, -3.0F, 0.0F, 1.0F, 3.0F, 0.0F, new Dilation(0.001F)),
                ModelTransform.pivot(4.5F, -19.25F, -12.0F));

        ModelPartData flightlights = panel4.addChild("flightlights", ModelPartBuilder.create(),
                ModelTransform.pivot(-6.0F, -15.9359F, -20.5665F));

        ModelPartData cube_r57 = flightlights.addChild("cube_r57",
                ModelPartBuilder.create().uv(195, 41).cuboid(-0.5F, -1.0F, -0.5F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F))
                        .uv(127, 195).cuboid(-2.5F, -1.0F, -0.5F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(14.0F, 0.0F, 0.0F, 0.2618F, 0.0F, 0.0F));

        ModelPartData flightlights2 = flightlights.addChild("flightlights2", ModelPartBuilder.create(),
                ModelTransform.pivot(1.0F, 0.9478F, -0.014F));

        ModelPartData leftlight = flightlights2.addChild("leftlight", ModelPartBuilder.create(),
                ModelTransform.pivot(13.0F, 0.0522F, 0.014F));

        ModelPartData cube_r58 = leftlight.addChild("cube_r58", ModelPartBuilder.create().uv(132, 194).cuboid(-2.5F,
                -1.0541F, -0.5F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.2618F, 0.0F, 0.0F));

        ModelPartData rightlight = flightlights2.addChild("rightlight", ModelPartBuilder.create(),
                ModelTransform.pivot(13.0F, 0.0522F, 0.014F));

        ModelPartData cube_r59 = rightlight.addChild("cube_r59", ModelPartBuilder.create().uv(194, 192).cuboid(-0.5F,
                -1.0541F, -0.5F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.2618F, 0.0F, 0.0F));

        ModelPartData handbrakelights = flightlights.addChild("handbrakelights", ModelPartBuilder.create(),
                ModelTransform.pivot(-11.0F, 0.25F, 0.25F));

        ModelPartData cube_r60 = handbrakelights.addChild("cube_r60",
                ModelPartBuilder.create().uv(14, 194).cuboid(-2.0F, -1.0F, -2.0F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F))
                        .uv(165, 155).cuboid(-5.5F, -1.0F, 1.25F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F)).uv(170, 135)
                        .cuboid(-4.0F, -1.0F, 0.75F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F)).uv(96, 194)
                        .cuboid(-2.5F, -1.0F, -0.5F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(14.0F, 0.0F, 0.0F, 0.2618F, 0.0F, 0.0F));

        ModelPartData handbrakelights2 = handbrakelights.addChild("handbrakelights2", ModelPartBuilder.create(),
                ModelTransform.pivot(1.0F, 0.9478F, -0.014F));

        ModelPartData red2 = handbrakelights2.addChild("red2", ModelPartBuilder.create(),
                ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData cube_r61 = red2
                .addChild(
                        "cube_r61", ModelPartBuilder.create().uv(113, 27).cuboid(-5.5F, -1.0541F, 1.25F, 1.0F, 2.0F,
                                1.0F, new Dilation(0.0F)),
                        ModelTransform.of(13.0F, 0.0522F, 0.014F, 0.2618F, 0.0F, 0.0F));

        ModelPartData red1 = handbrakelights2.addChild("red1", ModelPartBuilder.create(),
                ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData cube_r62 = red1
                .addChild(
                        "cube_r62", ModelPartBuilder.create().uv(175, 30).cuboid(-4.0F, -1.0541F, 0.75F, 1.0F, 2.0F,
                                1.0F, new Dilation(0.0F)),
                        ModelTransform.of(13.0F, 0.0522F, 0.014F, 0.2618F, 0.0F, 0.0F));

        ModelPartData yellow2 = handbrakelights2.addChild("yellow2", ModelPartBuilder.create(),
                ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData cube_r63 = yellow2
                .addChild(
                        "cube_r63", ModelPartBuilder.create().uv(186, 25).cuboid(-2.5F, -1.0541F, -0.5F, 1.0F, 2.0F,
                                1.0F, new Dilation(0.0F)),
                        ModelTransform.of(13.0F, 0.0522F, 0.014F, 0.2618F, 0.0F, 0.0F));

        ModelPartData yellow1 = handbrakelights2.addChild("yellow1", ModelPartBuilder.create(),
                ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData cube_r64 = yellow1
                .addChild(
                        "cube_r64", ModelPartBuilder.create().uv(158, 189).cuboid(-2.0F, -1.0541F, -2.0F, 1.0F, 2.0F,
                                1.0F, new Dilation(0.0F)),
                        ModelTransform.of(13.0F, 0.0522F, 0.014F, 0.2618F, 0.0F, 0.0F));

        ModelPartData yellow6 = panel4.addChild("yellow6", ModelPartBuilder.create(),
                ModelTransform.pivot(-14.0F, -15.7382F, -20.3305F));

        ModelPartData cube_r65 = yellow6
                .addChild(
                        "cube_r65", ModelPartBuilder.create().uv(51, 119).cuboid(-2.5F, -0.0881F, -0.7588F, 1.0F, 2.0F,
                                1.0F, new Dilation(0.0F)),
                        ModelTransform.of(19.0F, 0.8022F, -1.236F, 0.2618F, 0.0F, 0.0F));

        ModelPartData yellow5 = panel4.addChild("yellow5", ModelPartBuilder.create(),
                ModelTransform.pivot(-14.0F, -15.7382F, -20.3305F));

        ModelPartData cube_r66 = yellow5
                .addChild(
                        "cube_r66", ModelPartBuilder.create().uv(91, 147).cuboid(-2.5F, -0.0881F, -0.7588F, 1.0F, 2.0F,
                                1.0F, new Dilation(0.0F)),
                        ModelTransform.of(17.0F, 0.8022F, -1.236F, 0.2618F, 0.0F, 0.0F));

        ModelPartData yellow4 = panel4.addChild("yellow4", ModelPartBuilder.create(),
                ModelTransform.pivot(-16.0F, -15.7382F, -20.3305F));

        ModelPartData cube_r67 = yellow4
                .addChild(
                        "cube_r67", ModelPartBuilder.create().uv(103, 147).cuboid(-2.5F, -0.0881F, -0.7588F, 1.0F, 2.0F,
                                1.0F, new Dilation(0.0F)),
                        ModelTransform.of(17.0F, 0.8022F, -1.236F, 0.2618F, 0.0F, 0.0F));

        ModelPartData yellow3 = panel4.addChild("yellow3", ModelPartBuilder.create(),
                ModelTransform.pivot(-16.0F, -15.7382F, -20.3305F));

        ModelPartData cube_r68 = yellow3
                .addChild(
                        "cube_r68", ModelPartBuilder.create().uv(165, 148).cuboid(-2.5F, -0.0881F, -0.7588F, 1.0F, 2.0F,
                                1.0F, new Dilation(0.0F)),
                        ModelTransform.of(15.0F, 0.8022F, -1.236F, 0.2618F, 0.0F, 0.0F));

        ModelPartData panel5 = toyota
                .addChild(
                        "panel5", ModelPartBuilder.create().uv(180, 48).cuboid(-14.0F, -14.9306F, -25.1225F, 28.0F,
                                3.0F, 0.0F, new Dilation(0.001F)),
                        ModelTransform.of(0.0F, -2.0F, 0.0F, 0.0F, 2.0944F, 0.0F));

        ModelPartData cube_r69 = panel5
                .addChild("cube_r69",
                        ModelPartBuilder.create().uv(165, 60).cuboid(-1.0F, -8.023F, -31.6235F, 2.0F, 2.0F, 22.0F,
                                new Dilation(-0.001F)),
                        ModelTransform.of(0.0F, 2.0391F, 0.0F, -0.2618F, 0.5236F, 0.0F));

        ModelPartData cube_r70 = panel5
                .addChild("cube_r70",
                        ModelPartBuilder.create().uv(62, 178).cuboid(-1.0F, -15.5F, -1.0F, 2.0F, 3.0F, 2.0F,
                                new Dilation(0.0F)),
                        ModelTransform.of(-14.0582F, 0.5195F, -24.3496F, 0.0F, 0.5236F, 0.0F));

        ModelPartData cube_r71 = panel5
                .addChild(
                        "cube_r71", ModelPartBuilder.create().uv(37, 178).cuboid(-1.0F, -21.023F, -24.3765F, 2.0F, 2.0F,
                                20.0F, new Dilation(0.001F)),
                        ModelTransform.of(0.0F, -1.0F, 0.0F, 0.2618F, 0.5236F, 0.0F));

        ModelPartData cube_r72 = panel5
                .addChild(
                        "cube_r72", ModelPartBuilder.create().uv(0, 19).cuboid(-14.0F, -7.6235F, 12.523F, 28.0F, 18.0F,
                                0.0F, new Dilation(0.001F)),
                        ModelTransform.of(0.0F, 2.1387F, -21.0F, 1.309F, 0.0F, 0.0F));

        ModelPartData cube_r73 = panel5.addChild("cube_r73",
                ModelPartBuilder.create().uv(118, 38)
                        .cuboid(-14.0F, -17.6235F, -13.273F, 28.0F, 18.0F, 0.0F, new Dilation(0.001F)).uv(108, 121)
                        .cuboid(-14.0F, -17.6235F, -13.523F, 28.0F, 18.0F, 0.0F, new Dilation(0.001F)).uv(51, 123)
                        .cuboid(-14.0F, -17.6235F, -13.773F, 28.0F, 18.0F, 0.0F, new Dilation(0.001F)).uv(108, 140)
                        .cuboid(-14.0F, -17.6235F, -14.023F, 28.0F, 18.0F, 0.0F, new Dilation(0.001F)).uv(118, 19)
                        .cuboid(-14.0F, -17.6235F, -14.273F, 28.0F, 18.0F, 0.0F, new Dilation(0.001F)).uv(0, 142)
                        .cuboid(-14.0F, -17.6235F, -14.523F, 28.0F, 18.0F, 0.0F, new Dilation(0.001F)),
                ModelTransform.of(0.0F, -1.0F, -21.0F, -1.309F, 0.0F, 0.0F));

        ModelPartData panel6 = toyota
                .addChild("panel6",
                        ModelPartBuilder.create().uv(180, 52).cuboid(-9.3365F, -1.0376F, -6.4336F, 28.0F, 3.0F, 0.0F,
                                new Dilation(0.001F)),
                        ModelTransform.of(-18.5168F, -15.8931F, -5.3058F, 0.0F, 1.0472F, 0.0F));

        ModelPartData cube_r74 = panel6.addChild("cube_r74",
                ModelPartBuilder.create().uv(165, 85).cuboid(-1.0F, -8.023F, -31.6235F, 2.0F, 2.0F, 22.0F,
                        new Dilation(-0.001F)),
                ModelTransform.of(4.6635F, 15.9321F, 18.6889F, -0.2618F, 0.5236F, 0.0F));

        ModelPartData cube_r75 = panel6
                .addChild("cube_r75",
                        ModelPartBuilder.create().uv(178, 76).cuboid(-1.0F, -15.5F, -1.0F, 2.0F, 3.0F, 2.0F,
                                new Dilation(0.0F)),
                        ModelTransform.of(-9.3948F, 14.4126F, -5.6607F, 0.0F, 0.5236F, 0.0F));

        ModelPartData cube_r76 = panel6
                .addChild("cube_r76",
                        ModelPartBuilder.create().uv(82, 178).cuboid(-1.0F, -21.023F, -24.3765F, 2.0F, 2.0F, 20.0F,
                                new Dilation(0.001F)),
                        ModelTransform.of(4.6635F, 12.8931F, 18.6889F, 0.2618F, 0.5236F, 0.0F));

        ModelPartData cube_r77 = panel6
                .addChild("cube_r77",
                        ModelPartBuilder.create().uv(0, 38).cuboid(-14.0F, -7.6235F, 12.523F, 28.0F, 18.0F, 0.0F,
                                new Dilation(0.001F)),
                        ModelTransform.of(4.6635F, 16.0318F, -2.3111F, 1.309F, 0.0F, 0.0F));

        ModelPartData cube_r78 = panel6.addChild("cube_r78",
                ModelPartBuilder.create().uv(114, 0)
                        .cuboid(-14.0F, -17.6235F, -14.023F, 28.0F, 18.0F, 0.0F, new Dilation(0.001F)).uv(57, 159)
                        .cuboid(-14.0F, -17.6235F, -14.523F, 28.0F, 18.0F, 0.0F, new Dilation(0.001F)),
                ModelTransform.of(4.6635F, 12.8931F, -2.3111F, -1.309F, 0.0F, 0.0F));

        ModelPartData controls2 = panel6.addChild("controls2", ModelPartBuilder.create(),
                ModelTransform.pivot(4.6635F, 13.8931F, 18.6889F));

        ModelPartData gallifreyanball = controls2.addChild("gallifreyanball", ModelPartBuilder.create(),
                ModelTransform.pivot(8.0F, -15.9606F, -21.1224F));

        ModelPartData cube_r79 = gallifreyanball.addChild("cube_r79", ModelPartBuilder.create().uv(192, 85)
                .cuboid(-1.0F, -1.0F, -1.0F, 2.0F, 2.0F, 2.0F, new Dilation(-0.25F)),
                ModelTransform.of(0.0F, 0.0259F, -0.0966F, 0.2618F, 0.0F, 0.0F));

        ModelPartData gallifreyanball2 = controls2.addChild("gallifreyanball2", ModelPartBuilder.create(),
                ModelTransform.pivot(-8.0F, -15.9606F, -21.1224F));

        ModelPartData cube_r80 = gallifreyanball2.addChild("cube_r80", ModelPartBuilder.create().uv(192, 74)
                .cuboid(-1.0F, -1.0F, -1.0F, 2.0F, 2.0F, 2.0F, new Dilation(-0.25F)),
                ModelTransform.of(0.0F, 0.0259F, -0.0966F, 0.2618F, 0.0F, 0.0F));

        ModelPartData smallnob = controls2.addChild("smallnob",
                ModelPartBuilder.create().uv(35, 207).cuboid(-0.75F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                        .uv(104, 153).cuboid(-0.25F, -1.0F, 0.0F, 1.0F, 2.0F, 0.0F, new Dilation(0.001F)),
                ModelTransform.of(-4.25F, -18.7839F, -12.7475F, 0.3054F, 0.0F, 0.0F));

        ModelPartData smallnob2 = controls2.addChild("smallnob2", ModelPartBuilder.create(),
                ModelTransform.of(4.5F, -18.7839F, -12.7475F, 0.2618F, 0.0F, 0.0F));

        ModelPartData cube_r81 = smallnob2.addChild("cube_r81",
                ModelPartBuilder.create().uv(165, 64)
                        .cuboid(-0.75F, -1.0F, 0.0F, 1.0F, 2.0F, 0.0F, new Dilation(0.001F)).uv(30, 207)
                        .cuboid(-0.25F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(-0.25F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F));

        ModelPartData tinyswitches = controls2.addChild("tinyswitches", ModelPartBuilder.create(),
                ModelTransform.pivot(0.0F, -18.1802F, -12.5857F));

        ModelPartData tinyswitches1 = tinyswitches.addChild("tinyswitches1", ModelPartBuilder.create(),
                ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData cube_r82 = tinyswitches1
                .addChild("cube_r82",
                        ModelPartBuilder.create().uv(165, 129).cuboid(-9.0F, 3.0F, 4.5F, 4.0F, 2.0F, 0.0F,
                                new Dilation(0.001F)),
                        ModelTransform.of(7.0F, -2.8198F, -5.4143F, 0.2618F, 0.0F, 0.0F));

        ModelPartData smallnob3 = controls2
                .addChild("smallnob3",
                        ModelPartBuilder.create().uv(203, 171).cuboid(-0.5F, -1.0F, -0.5F, 1.0F, 2.0F, 1.0F,
                                new Dilation(0.0F)),
                        ModelTransform.of(-5.5F, -15.5606F, -22.6996F, 0.2618F, 0.0F, 0.0F));

        ModelPartData smallnob4 = controls2
                .addChild("smallnob4",
                        ModelPartBuilder.create().uv(203, 162).cuboid(-0.5F, -1.0F, -0.5F, 1.0F, 2.0F, 1.0F,
                                new Dilation(0.0F)),
                        ModelTransform.of(5.5F, -15.5606F, -22.6996F, 0.2618F, 0.0F, 0.0F));

        ModelPartData smallnob5 = controls2
                .addChild("smallnob5",
                        ModelPartBuilder.create().uv(203, 158).cuboid(-0.5F, -1.0F, -0.5F, 1.0F, 2.0F, 1.0F,
                                new Dilation(0.0F)),
                        ModelTransform.of(-3.5F, -15.3106F, -23.6996F, 0.2618F, 0.0F, 0.0F));

        ModelPartData smallnob6 = controls2
                .addChild("smallnob6",
                        ModelPartBuilder.create().uv(140, 202).cuboid(-0.5F, -1.0F, -0.5F, 1.0F, 2.0F, 1.0F,
                                new Dilation(0.0F)),
                        ModelTransform.of(3.5F, -15.3106F, -23.6996F, 0.2618F, 0.0F, 0.0F));

        ModelPartData gallifreyan = panel6.addChild("gallifreyan", ModelPartBuilder.create(),
                ModelTransform.pivot(18.5168F, 15.8931F, 5.3058F));

        ModelPartData middlegallifreyan = gallifreyan
                .addChild("middlegallifreyan",
                        ModelPartBuilder.create().uv(104, 49).cuboid(-3.0F, -3.0F, 0.0F, 6.0F, 6.0F, 0.0F,
                                new Dilation(0.001F)),
                        ModelTransform.of(-13.8533F, -18.4362F, -5.1547F, -1.309F, 0.0F, 0.0F));

        ModelPartData rightgallifreyan = gallifreyan
                .addChild("rightgallifreyan",
                        ModelPartBuilder.create().uv(194, 188).cuboid(-1.5F, -1.5F, 0.0F, 3.0F, 3.0F, 0.0F,
                                new Dilation(0.001F)),
                        ModelTransform.of(-9.1033F, -18.4362F, -5.1547F, -1.309F, 0.0F, 0.0F));

        ModelPartData leftgallifreyan = gallifreyan
                .addChild("leftgallifreyan",
                        ModelPartBuilder.create().uv(89, 194).cuboid(-1.5F, -1.5F, 0.0F, 3.0F, 3.0F, 0.0F,
                                new Dilation(0.001F)),
                        ModelTransform.of(-18.6033F, -18.4362F, -5.1547F, -1.309F, 0.0F, 0.0F));

        ModelPartData switchlights2 = panel6.addChild("switchlights2", ModelPartBuilder.create(),
                ModelTransform.pivot(-2.0865F, -1.0428F, -1.6276F));

        ModelPartData cube_r83 = switchlights2.addChild("cube_r83", ModelPartBuilder.create().uv(66, 44).cuboid(6.25F,
                -1.8591F, 7.8848F, 1.0F, 2.0F, 1.0F, new Dilation(-0.25F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.2618F, 0.0F, 0.0F));

        ModelPartData switchlights3 = switchlights2.addChild("switchlights3", ModelPartBuilder.create(),
                ModelTransform.pivot(1.0F, 0.9478F, -0.014F));

        ModelPartData cube_r84 = switchlights3
                .addChild(
                        "cube_r84", ModelPartBuilder.create().uv(66, 29).cuboid(4.25F, -1.9132F, 7.8848F, 1.0F, 2.0F,
                                1.0F, new Dilation(-0.25F)),
                        ModelTransform.of(1.0F, 0.0522F, 0.014F, 0.2618F, 0.0F, 0.0F));

        ModelPartData top = toyota.addChild("top",
                ModelPartBuilder.create().uv(57, 49)
                        .cuboid(-8.0F, -46.0F, -7.0F, 16.0F, 0.0F, 14.0F, new Dilation(0.001F)).uv(57, 34)
                        .cuboid(-8.0F, -84.0F, -7.0F, 16.0F, 0.0F, 14.0F, new Dilation(0.001F)),
                ModelTransform.pivot(0.0F, 23.0F, 0.0F));

        ModelPartData cube_r85 = top.addChild("cube_r85",
                ModelPartBuilder.create().uv(31, 214)
                        .cuboid(-7.75F, -27.0F, -4.5F, 2.0F, 1.0F, 9.0F, new Dilation(0.0F)).uv(0, 204)
                        .cuboid(-8.65F, -26.0F, -5.0F, 2.0F, 2.0F, 10.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, -20.0F, 0.0F, 0.0F, -0.5236F, 0.0F));

        ModelPartData cube_r86 = top.addChild("cube_r86",
                ModelPartBuilder.create().uv(212, 203)
                        .cuboid(-7.75F, -30.0F, -4.5F, 2.0F, 1.0F, 9.0F, new Dilation(0.0F)).uv(203, 158)
                        .cuboid(-8.65F, -29.0F, -5.0F, 2.0F, 2.0F, 10.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, -17.0F, 0.0F, 0.0F, 0.5236F, 0.0F));

        ModelPartData cube_r87 = top.addChild("cube_r87", ModelPartBuilder.create().uv(188, 212).cuboid(-7.75F, -30.0F,
                -4.5F, 2.0F, 1.0F, 9.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, -17.0F, 0.0F, 0.0F, 1.5708F, 0.0F));

        ModelPartData cube_r88 = top
                .addChild(
                        "cube_r88", ModelPartBuilder.create().uv(211, 190).cuboid(-7.75F, -30.0F, -4.5F, 2.0F, 1.0F,
                                9.0F, new Dilation(0.0F)),
                        ModelTransform.of(0.0F, -17.0F, 0.0F, -3.1416F, 0.5236F, 3.1416F));

        ModelPartData cube_r89 = top.addChild("cube_r89", ModelPartBuilder.create().uv(174, 209).cuboid(-7.75F, -30.0F,
                -4.5F, 2.0F, 1.0F, 9.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, -17.0F, 0.0F, 0.0F, -1.5708F, 0.0F));

        ModelPartData cube_r90 = top
                .addChild("cube_r90",
                        ModelPartBuilder.create().uv(151, 209).cuboid(-7.75F, -30.0F, -4.5F, 2.0F, 1.0F, 9.0F,
                                new Dilation(0.0F)),
                        ModelTransform.of(0.0F, -17.0F, 0.0F, -3.1416F, -0.5236F, 3.1416F));

        ModelPartData cube_r91 = top.addChild("cube_r91", ModelPartBuilder.create().uv(207, 75).cuboid(-8.65F, -31.0F,
                -5.0F, 2.0F, 2.0F, 10.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, -15.0F, 0.0F, 0.0F, 1.5708F, 0.0F));

        ModelPartData cube_r92 = top
                .addChild(
                        "cube_r92", ModelPartBuilder.create().uv(207, 59).cuboid(-8.65F, -31.0F, -5.0F, 2.0F, 2.0F,
                                10.0F, new Dilation(0.0F)),
                        ModelTransform.of(0.0F, -15.0F, 0.0F, -3.1416F, 0.5236F, 3.1416F));

        ModelPartData cube_r93 = top
                .addChild("cube_r93",
                        ModelPartBuilder.create().uv(15, 207).cuboid(-8.65F, -31.0F, -5.0F, 2.0F, 2.0F, 10.0F,
                                new Dilation(0.0F)),
                        ModelTransform.of(0.0F, -15.0F, 0.0F, -3.1416F, -0.5236F, 3.1416F));

        ModelPartData cube_r94 = top
                .addChild(
                        "cube_r94", ModelPartBuilder.create().uv(205, 25).cuboid(-8.65F, -31.0F, -5.0F, 2.0F, 2.0F,
                                10.0F, new Dilation(0.0F)),
                        ModelTransform.of(0.0F, -15.0F, 0.0F, 0.0F, -1.5708F, 0.0F));

        ModelPartData toprotor = top.addChild("toprotor", ModelPartBuilder.create(),
                ModelTransform.of(0.0F, -114.0F, 0.0F, 3.1416F, 0.0F, 0.0F));

        ModelPartData cube_r95 = toprotor
                .addChild(
                        "cube_r95", ModelPartBuilder.create().uv(77, 148).cuboid(-7.75F, -30.0F, -4.5F, 2.0F, 1.0F,
                                9.0F, new Dilation(0.0F)),
                        ModelTransform.of(0.0F, 0.0F, 0.0F, -3.1416F, -0.5236F, 3.1416F));

        ModelPartData cube_r96 = toprotor
                .addChild(
                        "cube_r96", ModelPartBuilder.create().uv(167, 188).cuboid(-7.75F, -30.0F, -4.5F, 2.0F, 1.0F,
                                9.0F, new Dilation(0.0F)),
                        ModelTransform.of(0.0F, 0.0F, 0.0F, -3.1416F, 0.5236F, 3.1416F));

        ModelPartData cube_r97 = toprotor
                .addChild(
                        "cube_r97", ModelPartBuilder.create().uv(165, 135).cuboid(-8.65F, -31.0F, -5.0F, 2.0F, 2.0F,
                                10.0F, new Dilation(0.0F)),
                        ModelTransform.of(0.0F, 2.0F, 0.0F, -3.1416F, 0.5236F, 3.1416F));

        ModelPartData cube_r98 = toprotor
                .addChild(
                        "cube_r98", ModelPartBuilder.create().uv(171, 0).cuboid(-8.65F, -31.0F, -5.0F, 2.0F, 2.0F,
                                10.0F, new Dilation(0.0F)),
                        ModelTransform.of(0.0F, 2.0F, 0.0F, -3.1416F, -0.5236F, 3.1416F));

        ModelPartData cube_r99 = toprotor.addChild("cube_r99", ModelPartBuilder.create().uv(194, 188).cuboid(-7.75F,
                -30.0F, -4.5F, 2.0F, 1.0F, 9.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.5708F, 0.0F));

        ModelPartData cube_r100 = toprotor.addChild("cube_r100", ModelPartBuilder.create().uv(62, 178).cuboid(-8.65F,
                -31.0F, -5.0F, 2.0F, 2.0F, 10.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 2.0F, 0.0F, 0.0F, -1.5708F, 0.0F));

        ModelPartData cube_r101 = toprotor.addChild("cube_r101",
                ModelPartBuilder.create().uv(207, 101)
                        .cuboid(-7.75F, -27.0F, -4.5F, 2.0F, 1.0F, 9.0F, new Dilation(0.0F)).uv(25, 180)
                        .cuboid(-8.65F, -26.0F, -5.0F, 2.0F, 2.0F, 10.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, -3.0F, 0.0F, 0.0F, -0.5236F, 0.0F));

        ModelPartData cube_r102 = toprotor.addChild("cube_r102",
                ModelPartBuilder.create().uv(208, 89)
                        .cuboid(-7.75F, -30.0F, -4.5F, 2.0F, 1.0F, 9.0F, new Dilation(0.0F)).uv(77, 181)
                        .cuboid(-8.65F, -29.0F, -5.0F, 2.0F, 2.0F, 10.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.5236F, 0.0F));

        ModelPartData cube_r103 = toprotor.addChild("cube_r103", ModelPartBuilder.create().uv(208, 114).cuboid(-7.75F,
                -30.0F, -4.5F, 2.0F, 1.0F, 9.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.5708F, 0.0F));

        ModelPartData cube_r104 = toprotor.addChild("cube_r104", ModelPartBuilder.create().uv(127, 189).cuboid(-8.65F,
                -31.0F, -5.0F, 2.0F, 2.0F, 10.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 2.0F, 0.0F, 0.0F, 1.5708F, 0.0F));

        ModelPartData monitors = top.addChild("monitors", ModelPartBuilder.create(),
                ModelTransform.pivot(0.0F, -47.0F, 0.0F));

        ModelPartData monitor1 = monitors.addChild("monitor1", ModelPartBuilder.create(),
                ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData cube_r105 = monitor1.addChild("cube_r105",
                ModelPartBuilder.create().uv(41, 193).cuboid(7.25F, -48.0F, -1.0F, 1.0F, 2.0F, 2.0F, new Dilation(0.0F))
                        .uv(179, 188).cuboid(14.0F, -54.4F, -6.0F, 1.0F, 8.0F, 12.0F, new Dilation(0.0F)).uv(113, 34)
                        .cuboid(14.5F, -53.4F, -7.0F, 0.0F, 6.0F, 1.0F, new Dilation(0.0F)).uv(99, 178)
                        .cuboid(14.5F, -53.4F, 6.0F, 0.0F, 6.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 47.0F, 0.0F, 0.0F, -0.5236F, 0.0F));

        ModelPartData cube_r106 = monitor1.addChild("cube_r106",
                ModelPartBuilder.create().uv(165, 104)
                        .cuboid(13.5287F, 7.75F, -0.6609F, 8.0F, 1.0F, 1.0F, new Dilation(0.0F)).uv(165, 126)
                        .cuboid(13.5287F, 7.75F, -0.6609F, 8.0F, 1.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(-7.25F, 0.0F, -4.0F, 0.281F, -0.4478F, -0.588F));

        ModelPartData gallifreyan2 = monitor1
                .addChild("gallifreyan2",
                        ModelPartBuilder.create().uv(180, 25).cuboid(0.0F, -2.5F, -2.5F, 0.0F, 5.0F, 5.0F,
                                new Dilation(0.001F)),
                        ModelTransform.of(13.0198F, -3.45F, 7.517F, 0.0F, -0.5236F, 0.0F));

        ModelPartData monitor2 = monitors.addChild("monitor2", ModelPartBuilder.create(),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 3.1416F, 0.0F));

        ModelPartData cube_r107 = monitor2.addChild("cube_r107",
                ModelPartBuilder.create().uv(34, 193).cuboid(7.25F, -48.0F, -1.0F, 1.0F, 2.0F, 2.0F, new Dilation(0.0F))
                        .uv(152, 188).cuboid(14.0F, -54.4F, -6.0F, 1.0F, 8.0F, 12.0F, new Dilation(0.0F)).uv(54, 110)
                        .cuboid(14.5F, -53.4F, -7.0F, 0.0F, 6.0F, 1.0F, new Dilation(0.0F)).uv(113, 19)
                        .cuboid(14.5F, -53.4F, 6.0F, 0.0F, 6.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 47.0F, 0.0F, 0.0F, -0.5236F, 0.0F));

        ModelPartData cube_r108 = monitor2.addChild("cube_r108",
                ModelPartBuilder.create().uv(0, 139)
                        .cuboid(13.5287F, 7.75F, -0.6609F, 8.0F, 1.0F, 1.0F, new Dilation(0.0F)).uv(165, 101)
                        .cuboid(13.5287F, 7.75F, -0.6609F, 8.0F, 1.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(-7.25F, 0.0F, -4.0F, 0.281F, -0.4478F, -0.588F));

        ModelPartData gallifreyan3 = monitor2
                .addChild("gallifreyan3",
                        ModelPartBuilder.create().uv(152, 189).cuboid(0.0F, -2.5F, -2.5F, 0.0F, 5.0F, 5.0F,
                                new Dilation(0.001F)),
                        ModelTransform.of(13.0198F, -3.45F, 7.517F, 0.0F, -0.5236F, 0.0F));

        ModelPartData bottom = toyota.addChild("bottom", ModelPartBuilder.create().uv(57, 19).cuboid(-8.0F, -27.0F,
                -7.0F, 16.0F, 0.0F, 14.0F, new Dilation(0.001F)), ModelTransform.pivot(0.0F, 27.0F, 0.0F));

        ModelPartData cube_r109 = bottom
                .addChild("cube_r109",
                        ModelPartBuilder.create().uv(218, 155).cuboid(0.0F, -9.0F, 6.5F, 0.0F, 7.0F, 3.0F,
                                new Dilation(0.001F)),
                        ModelTransform.of(0.0F, -27.0F, 0.0F, -3.1416F, -0.5236F, 3.1416F));

        ModelPartData cube_r110 = bottom.addChild("cube_r110",
                ModelPartBuilder.create().uv(92, 178).cuboid(0.0F, -9.0F, 6.5F, 0.0F, 7.0F, 3.0F, new Dilation(0.001F)),
                ModelTransform.of(0.0F, -27.0F, 0.0F, -3.1416F, 0.5236F, 3.1416F));

        ModelPartData cube_r111 = bottom.addChild("cube_r111", ModelPartBuilder.create().uv(183, 220).cuboid(0.0F,
                -9.0F, 6.5F, 0.0F, 7.0F, 3.0F, new Dilation(0.001F)),
                ModelTransform.of(0.0F, -27.0F, 0.0F, 0.0F, -0.5236F, 0.0F));

        ModelPartData cube_r112 = bottom.addChild("cube_r112", ModelPartBuilder.create().uv(176, 220).cuboid(0.0F,
                -9.0F, 6.5F, 0.0F, 7.0F, 3.0F, new Dilation(0.001F)),
                ModelTransform.of(0.0F, -27.0F, 0.0F, 0.0F, -1.5708F, 0.0F));

        ModelPartData cube_r113 = bottom.addChild("cube_r113",
                ModelPartBuilder.create().uv(221, 22).cuboid(0.0F, -9.0F, 6.5F, 0.0F, 7.0F, 3.0F, new Dilation(0.001F)),
                ModelTransform.of(0.0F, -27.0F, 0.0F, 0.0F, 0.5236F, 0.0F));

        ModelPartData cube_r114 = bottom.addChild("cube_r114", ModelPartBuilder.create().uv(221, 125).cuboid(0.0F,
                -9.0F, 6.5F, 0.0F, 7.0F, 3.0F, new Dilation(0.001F)),
                ModelTransform.of(0.0F, -27.0F, 0.0F, 0.0F, 1.5708F, 0.0F));

        ModelPartData cube_r115 = bottom
                .addChild(
                        "cube_r115", ModelPartBuilder.create().uv(146, 215).cuboid(-0.1F, -27.0F, -5.75F, 1.0F, 15.0F,
                                1.0F, new Dilation(0.0F)),
                        ModelTransform.of(-0.25F, -16.0F, 0.5F, 0.0F, 0.5236F, 0.0F));

        ModelPartData cube_r116 = bottom
                .addChild(
                        "cube_r116", ModelPartBuilder.create().uv(191, 25).cuboid(-0.5F, -27.0F, -6.0F, 1.0F, 15.0F,
                                1.0F, new Dilation(0.0F)),
                        ModelTransform.of(-0.25F, -16.0F, 0.5F, 0.0F, -0.5236F, 0.0F));

        ModelPartData cube_r117 = bottom
                .addChild("cube_r117",
                        ModelPartBuilder.create().uv(190, 158).cuboid(-0.9F, -27.0F, -5.25F, 1.0F, 15.0F, 1.0F,
                                new Dilation(0.0F)),
                        ModelTransform.of(-0.25F, -16.0F, 0.5F, -3.1416F, -0.5236F, 3.1416F));

        ModelPartData cube_r118 = bottom
                .addChild(
                        "cube_r118", ModelPartBuilder.create().uv(190, 135).cuboid(-0.9F, -27.0F, -5.75F, 1.0F, 15.0F,
                                1.0F, new Dilation(0.0F)),
                        ModelTransform.of(-0.25F, -16.0F, 0.5F, 0.0F, -1.5708F, 0.0F));

        ModelPartData cube_r119 = bottom
                .addChild(
                        "cube_r119", ModelPartBuilder.create().uv(185, 158).cuboid(-0.1F, -27.0F, -5.25F, 1.0F, 15.0F,
                                1.0F, new Dilation(0.0F)),
                        ModelTransform.of(-0.25F, -16.0F, 0.5F, 0.0F, 1.5708F, 0.0F));

        ModelPartData cube_r120 = bottom
                .addChild("cube_r120",
                        ModelPartBuilder.create().uv(50, 180).cuboid(-0.5F, -27.0F, -5.0F, 1.0F, 15.0F, 1.0F,
                                new Dilation(0.0F)),
                        ModelTransform.of(-0.25F, -16.0F, 0.5F, -3.1416F, 0.5236F, 3.1416F));

        ModelPartData cube_r121 = bottom.addChild("cube_r121", ModelPartBuilder.create().uv(20, 220).cuboid(-0.1F,
                -27.0F, -5.75F, 1.0F, 15.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, -16.0F, 0.5F, 0.0F, 1.0472F, 0.0F));

        ModelPartData cube_r122 = bottom.addChild("cube_r122", ModelPartBuilder.create().uv(15, 220).cuboid(-0.5F,
                -27.0F, -6.0F, 1.0F, 15.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, -16.0F, 0.5F, 0.0F, 0.0F, 0.0F));

        ModelPartData cube_r123 = bottom
                .addChild("cube_r123",
                        ModelPartBuilder.create().uv(10, 217).cuboid(-0.9F, -27.0F, -5.25F, 1.0F, 15.0F, 1.0F,
                                new Dilation(0.0F)),
                        ModelTransform.of(0.0F, -16.0F, 0.5F, -3.1416F, -1.0472F, 3.1416F));

        ModelPartData cube_r124 = bottom.addChild("cube_r124", ModelPartBuilder.create().uv(5, 217).cuboid(-0.9F,
                -27.0F, -5.75F, 1.0F, 15.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, -16.0F, 0.5F, 0.0F, -1.0472F, 0.0F));

        ModelPartData cube_r125 = bottom
                .addChild(
                        "cube_r125", ModelPartBuilder.create().uv(0, 217).cuboid(-0.1F, -27.0F, -5.25F, 1.0F, 15.0F,
                                1.0F, new Dilation(0.0F)),
                        ModelTransform.of(0.0F, -16.0F, 0.5F, 3.1416F, 1.0472F, 3.1416F));

        ModelPartData cube_r126 = bottom
                .addChild(
                        "cube_r126", ModelPartBuilder.create().uv(216, 125).cuboid(-0.5F, -27.0F, -5.0F, 1.0F, 15.0F,
                                1.0F, new Dilation(0.0F)),
                        ModelTransform.of(0.0F, -16.0F, 0.5F, -3.1416F, 0.0F, 3.1416F));

        ModelPartData cube_r127 = bottom.addChild("cube_r127",
                ModelPartBuilder.create().uv(192, 85)
                        .cuboid(-8.65F, -28.0F, -5.0F, 2.0F, 2.0F, 10.0F, new Dilation(0.0F)).uv(214, 179)
                        .cuboid(-7.75F, -29.0F, -4.5F, 2.0F, 1.0F, 9.0F, new Dilation(0.0F)).uv(135, 202)
                        .cuboid(-8.65F, -35.5F, -5.0F, 2.0F, 2.0F, 10.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, -1.0F, 0.0F, 0.0F, -0.5236F, 0.0F));

        ModelPartData cube_r128 = bottom.addChild("cube_r128", ModelPartBuilder.create().uv(192, 69).cuboid(-8.65F,
                -31.0F, -5.0F, 2.0F, 2.0F, 10.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 2.0F, 0.0F, 0.0F, -1.5708F, 0.0F));

        ModelPartData cube_r129 = bottom.addChild("cube_r129", ModelPartBuilder.create().uv(202, 214).cuboid(-7.75F,
                -30.0F, -4.5F, 2.0F, 1.0F, 9.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.5708F, 0.0F));

        ModelPartData cube_r130 = bottom
                .addChild(
                        "cube_r130", ModelPartBuilder.create().uv(192, 56).cuboid(-8.65F, -31.0F, -5.0F, 2.0F, 2.0F,
                                10.0F, new Dilation(0.0F)),
                        ModelTransform.of(0.0F, 2.0F, 0.0F, -3.1416F, -0.5236F, 3.1416F));

        ModelPartData cube_r131 = bottom
                .addChild(
                        "cube_r131", ModelPartBuilder.create().uv(123, 215).cuboid(-7.75F, -30.0F, -4.5F, 2.0F, 1.0F,
                                9.0F, new Dilation(0.0F)),
                        ModelTransform.of(0.0F, 0.0F, 0.0F, -3.1416F, -0.5236F, 3.1416F));

        ModelPartData cube_r132 = bottom
                .addChild(
                        "cube_r132", ModelPartBuilder.create().uv(54, 214).cuboid(-7.75F, -30.0F, -4.5F, 2.0F, 1.0F,
                                9.0F, new Dilation(0.0F)),
                        ModelTransform.of(0.0F, 0.0F, 0.0F, -3.1416F, 0.5236F, 3.1416F));

        ModelPartData cube_r133 = bottom
                .addChild(
                        "cube_r133", ModelPartBuilder.create().uv(35, 201).cuboid(-8.65F, -31.0F, -5.0F, 2.0F, 2.0F,
                                10.0F, new Dilation(0.0F)),
                        ModelTransform.of(0.0F, 2.0F, 0.0F, -3.1416F, 0.5236F, 3.1416F));

        ModelPartData cube_r134 = bottom.addChild("cube_r134", ModelPartBuilder.create().uv(192, 110).cuboid(-8.65F,
                -31.0F, -5.0F, 2.0F, 2.0F, 10.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 2.0F, 0.0F, 0.0F, 1.5708F, 0.0F));

        ModelPartData cube_r135 = bottom.addChild("cube_r135",
                ModelPartBuilder.create().uv(196, 199)
                        .cuboid(-8.65F, -31.0F, -5.0F, 2.0F, 2.0F, 10.0F, new Dilation(0.0F)).uv(100, 214)
                        .cuboid(-7.75F, -32.0F, -4.5F, 2.0F, 1.0F, 9.0F, new Dilation(0.0F)).uv(60, 201)
                        .cuboid(-8.65F, -38.5F, -5.0F, 2.0F, 2.0F, 10.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 2.0F, 0.0F, 0.0F, 0.5236F, 0.0F));

        ModelPartData cube_r136 = bottom.addChild("cube_r136", ModelPartBuilder.create().uv(77, 214).cuboid(-7.75F,
                -30.0F, -4.5F, 2.0F, 1.0F, 9.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.5708F, 0.0F));

        ModelPartData cube_r137 = bottom.addChild("cube_r137", ModelPartBuilder.create().uv(202, 0).cuboid(-8.65F,
                -31.0F, -5.0F, 2.0F, 2.0F, 10.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, -5.5F, 0.0F, 0.0F, -1.5708F, 0.0F));

        ModelPartData cube_r138 = bottom
                .addChild(
                        "cube_r138", ModelPartBuilder.create().uv(201, 135).cuboid(-8.65F, -31.0F, -5.0F, 2.0F, 2.0F,
                                10.0F, new Dilation(0.0F)),
                        ModelTransform.of(0.0F, -5.5F, 0.0F, -3.1416F, -0.5236F, 3.1416F));

        ModelPartData cube_r139 = bottom
                .addChild(
                        "cube_r139", ModelPartBuilder.create().uv(110, 201).cuboid(-8.65F, -31.0F, -5.0F, 2.0F, 2.0F,
                                10.0F, new Dilation(0.0F)),
                        ModelTransform.of(0.0F, -5.5F, 0.0F, -3.1416F, 0.5236F, 3.1416F));

        ModelPartData cube_r140 = bottom.addChild("cube_r140", ModelPartBuilder.create().uv(85, 201).cuboid(-8.65F,
                -31.0F, -5.0F, 2.0F, 2.0F, 10.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, -5.5F, 0.0F, 0.0F, 1.5708F, 0.0F));

        ModelPartData glass = toyota.addChild("glass", ModelPartBuilder.create().uv(34, 76).cuboid(-4.0F, -59.0F, -6.9F,
                8.0F, 58.0F, 0.0F, new Dilation(0.001F)), ModelTransform.pivot(0.0F, -2.0F, 0.0F));

        ModelPartData cube_r141 = glass.addChild("cube_r141", ModelPartBuilder.create().uv(0, 76).cuboid(-4.0F, -75.0F,
                -6.9F, 8.0F, 58.0F, 0.0F, new Dilation(0.001F)),
                ModelTransform.of(0.0F, 16.0F, 0.0F, -3.1416F, 0.0F, 3.1416F));

        ModelPartData cube_r142 = glass
                .addChild("cube_r142",
                        ModelPartBuilder.create().uv(74, 64).cuboid(-4.0F, -75.0F, -6.9F, 8.0F, 58.0F, 0.0F,
                                new Dilation(0.001F)),
                        ModelTransform.of(0.0F, 16.0F, 0.0F, -3.1416F, -1.0472F, 3.1416F));

        ModelPartData cube_r143 = glass
                .addChild("cube_r143",
                        ModelPartBuilder.create().uv(57, 64).cuboid(-4.0F, -75.0F, -6.9F, 8.0F, 58.0F, 0.0F,
                                new Dilation(0.001F)),
                        ModelTransform.of(0.0F, 16.0F, 0.0F, -3.1416F, 1.0472F, 3.1416F));

        ModelPartData cube_r144 = glass
                .addChild(
                        "cube_r144", ModelPartBuilder.create().uv(17, 76).cuboid(-4.0F, -75.0F, -6.9F, 8.0F, 58.0F,
                                0.0F, new Dilation(0.001F)),
                        ModelTransform.of(0.0F, 16.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData cube_r145 = glass.addChild("cube_r145", ModelPartBuilder.create().uv(91, 64).cuboid(-4.0F, -75.0F,
                -6.9F, 8.0F, 58.0F, 0.0F, new Dilation(0.001F)),
                ModelTransform.of(0.0F, 16.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData rotor = toyota.addChild("rotor", ModelPartBuilder.create(),
                ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData cube_r146 = rotor.addChild("cube_r146",
                ModelPartBuilder.create().uv(0, 180).cuboid(-1.0F, 7.0F, -5.25F, 2.0F, 13.0F, 2.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, -42.0F, 0.0F, 0.0F, -1.5708F, 0.0F));

        ModelPartData cube_r147 = rotor
                .addChild("cube_r147",
                        ModelPartBuilder.create().uv(178, 85).cuboid(-1.0F, 7.0F, -5.25F, 2.0F, 13.0F, 2.0F,
                                new Dilation(0.0F)),
                        ModelTransform.of(0.0F, -42.0F, 0.0F, 3.1416F, -0.5236F, -3.1416F));

        ModelPartData cube_r148 = rotor
                .addChild(
                        "cube_r148", ModelPartBuilder.create().uv(178, 60).cuboid(-1.0F, 7.0F, -5.25F, 2.0F, 13.0F,
                                2.0F, new Dilation(0.0F)),
                        ModelTransform.of(0.0F, -42.0F, 0.0F, 3.1416F, 0.5236F, -3.1416F));

        ModelPartData cube_r149 = rotor.addChild("cube_r149", ModelPartBuilder.create().uv(176, 158).cuboid(-1.0F, 7.0F,
                -5.25F, 2.0F, 13.0F, 2.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, -42.0F, 0.0F, 0.0F, 1.5708F, 0.0F));

        ModelPartData cube_r150 = rotor.addChild("cube_r150", ModelPartBuilder.create().uv(174, 110).cuboid(-1.0F, 7.0F,
                -5.25F, 2.0F, 13.0F, 2.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, -42.0F, 0.0F, 0.0F, 0.5236F, 0.0F));

        ModelPartData cube_r151 = rotor.addChild("cube_r151", ModelPartBuilder.create().uv(165, 110).cuboid(-1.0F, 7.0F,
                -5.25F, 2.0F, 13.0F, 2.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, -42.0F, 0.0F, 0.0F, -0.5236F, 0.0F));

        ModelPartData cube_r152 = rotor.addChild("cube_r152",
                ModelPartBuilder.create().uv(9, 180).cuboid(-1.0F, 8.0F, -5.25F, 2.0F, 11.0F, 2.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, -42.0F, 0.0F, 0.0F, 1.5708F, -3.1416F));

        ModelPartData cube_r153 = rotor
                .addChild(
                        "cube_r153", ModelPartBuilder.create().uv(104, 34).cuboid(-1.0F, 8.0F, -5.25F, 2.0F, 11.0F,
                                2.0F, new Dilation(0.0F)),
                        ModelTransform.of(0.0F, -42.0F, 0.0F, 0.0F, 0.5236F, 3.1416F));

        ModelPartData cube_r154 = rotor
                .addChild(
                        "cube_r154", ModelPartBuilder.create().uv(104, 19).cuboid(-1.0F, 8.0F, -5.25F, 2.0F, 11.0F,
                                2.0F, new Dilation(0.0F)),
                        ModelTransform.of(0.0F, -42.0F, 0.0F, 0.0F, -0.5236F, 3.1416F));

        ModelPartData cube_r155 = rotor.addChild("cube_r155",
                ModelPartBuilder.create().uv(57, 49).cuboid(-1.0F, 8.0F, -5.25F, 2.0F, 11.0F, 2.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, -42.0F, 0.0F, 0.0F, -1.5708F, -3.1416F));

        ModelPartData cube_r156 = rotor.addChild("cube_r156",
                ModelPartBuilder.create().uv(57, 34).cuboid(-1.0F, 8.0F, -5.25F, 2.0F, 11.0F, 2.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, -42.0F, 0.0F, -3.1416F, -0.5236F, 0.0F));

        ModelPartData cube_r157 = rotor.addChild("cube_r157",
                ModelPartBuilder.create().uv(57, 19).cuboid(-1.0F, 8.0F, -5.25F, 2.0F, 11.0F, 2.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, -42.0F, 0.0F, 3.1416F, 0.5236F, 0.0F));

        ModelPartData rotorlights = rotor.addChild("rotorlights", ModelPartBuilder.create(),
                ModelTransform.pivot(0.0F, -21.0F, 0.5F));

        ModelPartData cube_r158 = rotorlights
                .addChild("cube_r158",
                        ModelPartBuilder.create().uv(151, 220).cuboid(-0.533F, -8.0F, -4.75F, 1.0F, 15.0F, 1.0F,
                                new Dilation(0.0F)),
                        ModelTransform.of(0.0F, -21.0F, -0.5F, -3.1416F, 0.5236F, 3.1416F));

        ModelPartData cube_r159 = rotorlights.addChild("cube_r159", ModelPartBuilder.create().uv(25, 220)
                .cuboid(-0.533F, -8.0F, -4.75F, 1.0F, 15.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, -21.0F, -0.5F, 0.0F, 1.5708F, 0.0F));

        ModelPartData cube_r160 = rotorlights
                .addChild(
                        "cube_r160", ModelPartBuilder.create().uv(156, 220).cuboid(-0.467F, -8.0F, -4.75F, 1.0F, 15.0F,
                                1.0F, new Dilation(0.0F)),
                        ModelTransform.of(0.0F, -21.0F, -0.5F, 0.0F, -0.5236F, 0.0F));

        ModelPartData cube_r161 = rotorlights
                .addChild(
                        "cube_r161", ModelPartBuilder.create().uv(161, 220).cuboid(-0.467F, -8.0F, -4.75F, 1.0F, 15.0F,
                                1.0F, new Dilation(0.0F)),
                        ModelTransform.of(0.0F, -21.0F, -0.5F, 0.0F, -1.5708F, 0.0F));

        ModelPartData cube_r162 = rotorlights
                .addChild("cube_r162",
                        ModelPartBuilder.create().uv(171, 220).cuboid(-0.5F, -8.0F, -4.75F, 1.0F, 15.0F, 1.0F,
                                new Dilation(0.0F)),
                        ModelTransform.of(0.0F, -21.0F, -0.5F, -3.1416F, -0.5236F, 3.1416F));

        ModelPartData cube_r163 = rotorlights.addChild("cube_r163", ModelPartBuilder.create().uv(166, 220).cuboid(-0.5F,
                -8.0F, -4.75F, 1.0F, 15.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, -21.0F, -0.5F, 0.0F, 0.5236F, 0.0F));

        ModelPartData rotorgizmo = rotor.addChild("rotorgizmo",
                ModelPartBuilder.create().uv(165, 85)
                        .cuboid(-1.5F, -35.0F, -1.5F, 3.0F, 12.0F, 3.0F, new Dilation(0.0F)).uv(25, 193)
                        .cuboid(-1.0F, -37.0F, -1.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F)),
                ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData cube_r164 = rotorgizmo.addChild("cube_r164",
                ModelPartBuilder.create().uv(192, 115)
                        .cuboid(-1.0F, -37.0F, -1.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F)).uv(165, 64)
                        .cuboid(-1.5F, -35.0F, -1.5F, 3.0F, 12.0F, 3.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, -85.0F, 0.0F, -3.1416F, 0.0F, 0.0F));

        ModelPartData uppertimepiece = rotorgizmo.addChild("uppertimepiece", ModelPartBuilder.create(),
                ModelTransform.pivot(0.0F, -48.0F, 0.0F));

        ModelPartData cube_r165 = uppertimepiece.addChild("cube_r165",
                ModelPartBuilder.create().uv(66, 49).cuboid(-0.5F, -42.0F, 0.0F, 1.0F, 8.0F, 0.0F, new Dilation(0.001F))
                        .uv(66, 19).cuboid(0.0F, -42.0F, -0.5F, 0.0F, 8.0F, 1.0F, new Dilation(0.001F)),
                ModelTransform.of(0.0F, -37.0F, 0.0F, 3.1416F, 0.7854F, 0.0F));

        ModelPartData lowertimepiece = rotorgizmo.addChild("lowertimepiece", ModelPartBuilder.create(),
                ModelTransform.pivot(0.0F, -37.0F, 0.0F));

        ModelPartData cube_r166 = lowertimepiece.addChild("cube_r166",
                ModelPartBuilder.create().uv(66, 34).cuboid(0.0F, -42.0F, -0.5F, 0.0F, 8.0F, 1.0F, new Dilation(0.001F))
                        .uv(51, 110).cuboid(-0.5F, -42.0F, 0.0F, 1.0F, 8.0F, 0.0F, new Dilation(0.001F)),
                ModelTransform.of(0.0F, 37.0F, 0.0F, 0.0F, -0.7854F, 0.0F));
        return TexturedModelData.of(modelData, 256, 256);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red,
            float green, float blue, float alpha) {
        matrices.push();

        matrices.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(180f));

        toyota.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);

        matrices.pop();
    }

    @Override
    public void renderWithAnimations(ConsoleBlockEntity console, ClientTardis tardis, ModelPart root, MatrixStack matrices,
                                     VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float pAlpha) {
        matrices.push();
        matrices.translate(0.5f, -1.5f, -0.5f);

        matrices.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(180f));

        // Throttle Control
        ModelPart throttle = this.toyota.getChild("panel4").getChild("controls4").getChild("throttle");
        ModelPart throttleLights = this.toyota.getChild("panel4").getChild("flightlights").getChild("flightlights2");

        throttle.pitch = throttle.pitch + ((tardis.travel().speed() / (float) tardis.travel().maxSpeed().get()) * 1.5f);
        throttleLights.pivotY = !(tardis.travel().speed() > 0) ? throttleLights.pivotY + 1 : throttleLights.pivotY;

        // Handbrake Control and Lights
        ModelPart handbrake = this.toyota.getChild("panel4").getChild("controls4").getChild("handbrake")
                .getChild("pivot");
        handbrake.yaw = !tardis.travel().handbrake() ? handbrake.yaw - 1.57f : handbrake.yaw;
        ModelPart handbrakeLights = this.toyota.getChild("panel4").getChild("flightlights").getChild("handbrakelights")
                .getChild("handbrakelights2");

        handbrakeLights.pivotY = !tardis.travel().handbrake() ? handbrakeLights.pivotY + 1 : handbrakeLights.pivotY;

        // @TODO MONSTER THE ONE ON THE LEFT IS THE POWER NOT THE RIGHT SMH
        // Power Switch and Lights
        ModelPart power = this.toyota.getChild("panel1").getChild("controls").getChild("dooropen");
        power.pitch = tardis.fuel().hasPower() ? power.pitch : power.pitch - 1.55f;

        // Anti Gravity Control
        ModelPart antigravs = this.toyota.getChild("panel1").getChild("controls").getChild("faucettaps1")
                .getChild("pivot2");
        antigravs.yaw = tardis.travel().antigravs().get() ? antigravs.yaw - 1.58f : antigravs.yaw;

        ModelPart shield = this.toyota.getChild("panel1").getChild("controls").getChild("faucettaps2");
        shield.yaw = tardis.shields().shielded().get()
                ? shield.yaw - 1.58f
                : shield.yaw;

        // Door Locking Mechanism Control
        ModelPart doorlock = this.toyota.getChild("panel1").getChild("controls").getChild("smalllockernob")
                .getChild("pivot3");
        doorlock.yaw = tardis.door().locked() ? doorlock.yaw + 0.5f : doorlock.yaw;

        // Door Control
        ModelPart doorControl = this.toyota.getChild("panel1").getChild("controls").getChild("power");
        doorControl.pitch = tardis.door().isLeftOpen()
                ? doorControl.pitch - 1f
                : tardis.door().isRightOpen() ? doorControl.pitch - 1.55f : doorControl.pitch;
        ModelPart doorControlLights = this.toyota.getChild("panel1").getChild("controls").getChild("powerlights")
                .getChild("powerlights2");
        doorControlLights.pivotY = !(tardis.door().isOpen()) ? doorControlLights.pivotY : doorControlLights.pivotY + 1;

        // Alarm Control and Lights
        ModelPart alarms = this.toyota.getChild("panel4").getChild("controls4").getChild("coloredlever2");
        ModelPart alarmsLight = this.toyota.getChild("panel4").getChild("yellow3");
        alarmsLight.pivotY = (tardis.alarm().isEnabled()) ? alarmsLight.pivotY : alarmsLight.pivotY + 1;
        alarms.pitch = tardis.alarm().isEnabled() ? alarms.pitch + 1f : alarms.pitch;

        ModelPart security = this.toyota.getChild("panel4").getChild("controls4").getChild("coloredlever5");
        security.pitch = tardis.stats().security().get() ? security.pitch + 1f : security.pitch;

        // Auto Pilot Control
        ModelPart autopilot = this.toyota.getChild("panel4").getChild("controls4").getChild("tinyswitch2");
        ModelPart autopilotLight = this.toyota.getChild("panel4").getChild("yellow4");

        autopilot.pitch = tardis.travel().autopilot() ? autopilot.pitch + 1f : autopilot.pitch - 1f;
        autopilotLight.pivotY = tardis.travel().autopilot() ? autopilotLight.pivotY : autopilotLight.pivotY + 1;

        // Siege Mode Control
        ModelPart siegeMode = this.toyota.getChild("panel2").getChild("controls3").getChild("siegemode")
                .getChild("siegemodehandle");
        siegeMode.pitch = tardis.siege().isActive() ? siegeMode.pitch + 1.55f : siegeMode.pitch;

        // Fuel Gauge
        ModelPart fuelGauge = this.toyota.getChild("panel1").getChild("controls").getChild("geigercounter")
                .getChild("needle");
        fuelGauge.pivotX = fuelGauge.pivotX + 0.25f;
        fuelGauge.pivotZ = fuelGauge.pivotZ + 0.25f;
        fuelGauge.yaw = (float) (((tardis.getFuel() / FuelHandler.TARDIS_MAX_FUEL) * 2) - 1);

        // Refuel Light Warning
        ModelPart fuelWarning = this.toyota.getChild("panel4").getChild("yellow5");
        fuelWarning.pivotY = !(tardis.getFuel() > (tardis.getFuel() / 10))
                ? fuelWarning.pivotY
                : fuelWarning.pivotY + 1;

        // Ground Search Control
        ModelPart groundSearch = this.toyota.getChild("panel1").getChild("controls").getChild("smallswitch");
        groundSearch.pitch = tardis.travel().horizontalSearch().get()
                ? groundSearch.pitch + 1f
                : groundSearch.pitch - 0.75f; // FIXME use TravelHandler#horizontalSearch/#verticalSearch

        // Direction Control
        ModelPart direction = this.toyota.getChild("panel6").getChild("controls2").getChild("smallnob2");
        direction.pitch = direction.pitch + tardis.travel().destination().getRotation();

        // Increment Control
        ModelPart increment = this.toyota.getChild("panel2").getChild("controls3").getChild("gears")
                .getChild("largegear2");
        increment.yaw = IncrementManager.increment(tardis) >= 10
                ? IncrementManager.increment(tardis) >= 100
                        ? IncrementManager.increment(tardis) >= 1000
                                ? IncrementManager.increment(tardis) >= 10000
                                        ? increment.yaw + 1.5f
                                        : increment.yaw + 1.25f
                                : increment.yaw + 1f
                        : increment.yaw + 0.5f
                : increment.yaw;

        // Refuel Light
        ModelPart refuelLight = this.toyota.getChild("panel4").getChild("yellow6");
        refuelLight.pivotY = tardis.isRefueling() ? refuelLight.pivotY : refuelLight.pivotY + 1;

        // Fast Return Control
        // @TODO Loqor you need to make a toggleable thing for the fast return to be
        // able to do
        // something for the switch
        ModelPart fastReturnCover = this.toyota.getChild("panel4").getChild("controls4").getChild("tinyswitchcover");
        ModelPart fastReturnLever = this.toyota.getChild("panel4").getChild("controls4").getChild("tinyswitch");

        super.renderWithAnimations(console, tardis, root, matrices, vertices, light, overlay, red, green, blue, pAlpha);
        matrices.pop();
    }

    @Override
    public ModelPart getPart() {
        return toyota;
    }

    @Override
    public Animation getAnimationForState(TravelHandlerBase.State state) {
        return switch (state) {
            case MAT, DEMAT, FLIGHT -> ToyotaAnimations.CONSOLE_TOYOTA_FLIGHT;
            case LANDED -> ToyotaAnimations.CONSOLE_TOYOTA_IDLE;
            default -> Animation.Builder.create(0).build();
        };
    }

    @Override
    public void renderMonitorText(Tardis tardis, ConsoleBlockEntity entity, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        super.renderMonitorText(tardis, entity, matrices, vertexConsumers, light, overlay);

        MinecraftClient client = MinecraftClient.getInstance();
        TextRenderer renderer = client.textRenderer;
        TravelHandler travel = tardis.travel();
        CachedDirectedGlobalPos abpd = travel.destination();
        CachedDirectedGlobalPos abpp = travel.isLanded() || travel.getState() == TravelHandlerBase.State.MAT
                ? travel.position()
                : travel.getProgress();

        BlockPos abppPos = abpp.getPos();
        BlockPos abpdPos = abpd.getPos();
        matrices.push();
        // TODO dont forget to add variant.getConsoleTextPosition()!
        matrices.translate(0.5, 0.75, 0.5);
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180f));
        matrices.scale(0.005f, 0.005f, 0.005f);
        matrices.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(-60f));
        matrices.translate(-60f, -228, -188);
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
        matrices.translate(0.5, 0.75, 0.5);
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180f));
        matrices.scale(0.005f, 0.005f, 0.005f);
        matrices.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(120f));
        matrices.translate(-60f, -228, -188);
        renderer.drawWithOutline(Text.of("✛").asOrderedText(), 0, 40, 0x00F0FF, 0x000000,
                matrices.peek().getPositionMatrix(), vertexConsumers, 0xF000F0);
        renderer.drawWithOutline(Text.of(destinationPosText).asOrderedText(), 0, 48, 0xFFFFFF, 0x000000,
                matrices.peek().getPositionMatrix(), vertexConsumers, 0xF000F0);
        renderer.drawWithOutline(destinationDimensionText.asOrderedText(), 0, 56, 0xFFFFFF, 0x000000,
                matrices.peek().getPositionMatrix(), vertexConsumers, 0xF000F0);
        renderer.drawWithOutline(Text.of(destinationDirectionText).asOrderedText(), 0, 64, 0xFFFFFF, 0x000000,
                matrices.peek().getPositionMatrix(), vertexConsumers, 0xF000F0);
        matrices.pop();

        matrices.push();
        matrices.translate(0.5, 0.75, 0.5);
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180f));
        matrices.scale(0.015f, 0.015f, 0.015f);
        matrices.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(120f));
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-72.5f));
        String progressText = tardis.travel().getState() == TravelHandlerBase.State.LANDED
                ? "0%"
                : tardis.travel().getDurationAsPercentage() + "%";
        matrices.translate(0, 62, -49);
        /*renderer.drawWithOutline(Text.of("⏳").asOrderedText(), 0, 0, 0x00FF0F, 0x000000,
                matrices.peek().getPositionMatrix(), vertexConsumers, 0xF000F0);*/
        renderer.drawWithOutline(Text.of(progressText).asOrderedText(), 0 - renderer.getWidth(progressText) / 2, 0, 0xFFFFFF, 0x000000,
                matrices.peek().getPositionMatrix(), vertexConsumers, 0xF000F0);
        matrices.pop();
    }
}
