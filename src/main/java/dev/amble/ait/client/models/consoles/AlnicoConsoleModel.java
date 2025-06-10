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

import dev.amble.ait.client.animation.console.alnico.AlnicoAnimations;
import dev.amble.ait.client.tardis.ClientTardis;
import dev.amble.ait.core.blockentities.ConsoleBlockEntity;
import dev.amble.ait.core.tardis.Tardis;
import dev.amble.ait.core.tardis.control.impl.DirectionControl;
import dev.amble.ait.core.tardis.control.impl.pos.IncrementManager;
import dev.amble.ait.core.tardis.handler.FuelHandler;
import dev.amble.ait.core.tardis.handler.travel.TravelHandler;
import dev.amble.ait.core.tardis.handler.travel.TravelHandlerBase;
import dev.amble.ait.core.util.WorldUtil;

public class AlnicoConsoleModel extends ConsoleModel {
    private final ModelPart alnico;

    public AlnicoConsoleModel(ModelPart root) {
        this.alnico = root.getChild("alnico");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData alnico = modelPartData.addChild("alnico", ModelPartBuilder.create(),
                ModelTransform.pivot(0.0F, 12.0F, 0.0F));

        ModelPartData section1 = alnico.addChild("section1", ModelPartBuilder.create(),
                ModelTransform.pivot(0.0F, 12.0F, 0.0F));

        ModelPartData desktop = section1.addChild("desktop", ModelPartBuilder.create().uv(45, 21).cuboid(-9.5F, -13.25F,
                -27.65F, 19.0F, 4.0F, 3.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 2.0F));

        ModelPartData cube_r1 = desktop.addChild("cube_r1",
                ModelPartBuilder.create().uv(156, 29)
                        .cuboid(-8.0F, -4.0F, -8.0F, 1.0F, 0.0F, 1.0F, new Dilation(0.001F)).uv(93, 157)
                        .cuboid(7.0F, -4.0F, -8.0F, 1.0F, 0.0F, 1.0F, new Dilation(0.001F)).uv(106, 143)
                        .cuboid(6.0F, -4.0F, -8.0F, 1.0F, 2.0F, 6.0F, new Dilation(0.001F)).uv(121, 143)
                        .cuboid(-7.0F, -4.0F, -8.0F, 1.0F, 2.0F, 6.0F, new Dilation(0.001F)).uv(65, 0)
                        .cuboid(-6.0F, -4.0F, -8.0F, 12.0F, 2.0F, 8.0F, new Dilation(0.0F)).uv(148, 155)
                        .cuboid(-5.0F, -4.0F, 0.0F, 1.0F, 2.0F, 2.0F, new Dilation(0.0F)).uv(155, 155)
                        .cuboid(4.0F, -4.0F, 0.0F, 1.0F, 2.0F, 2.0F, new Dilation(0.0F)).uv(0, 86)
                        .cuboid(-9.0F, -4.0F, -10.0F, 18.0F, 2.0F, 2.0F, new Dilation(0.0F)).uv(0, 127)
                        .cuboid(-4.0F, -4.0F, 0.0F, 8.0F, 2.0F, 4.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, -12.0F, -14.0F, 0.2618F, 0.0F, 0.0F));

        ModelPartData pillars = section1.addChild("pillars", ModelPartBuilder.create().uv(111, 91).cuboid(-6.0F, -19.0F,
                -12.0F, 12.0F, 6.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 2.0F));

        ModelPartData cube_r2 = pillars.addChild("cube_r2",
                ModelPartBuilder.create().uv(66, 87).cuboid(-2.0F, -21.0F, 0.9F, 2.0F, 20.0F, 5.0F, new Dilation(0.0F)),
                ModelTransform.of(10.9554F, -14.2676F, -27.1301F, -1.8326F, -0.3491F, 0.0F));

        ModelPartData cube_r3 = pillars.addChild("cube_r3",
                ModelPartBuilder.create().uv(96, 91).cuboid(-2.0F, -21.0F, 0.9F, 2.0F, 20.0F, 5.0F, new Dilation(0.0F)),
                ModelTransform.of(12.0F, -13.75F, -30.0F, -1.309F, -0.3491F, 0.0F));

        ModelPartData cube_r4 = pillars
                .addChild("cube_r4",
                        ModelPartBuilder.create().uv(81, 91).cuboid(-1.0F, -10.0F, -2.5F, 2.0F, 20.0F, 5.0F,
                                new Dilation(0.0F)),
                        ModelTransform.of(-6.6827F, -8.1365F, -18.3146F, -1.8326F, 0.3491F, 0.0F));

        ModelPartData cube_r5 = pillars.addChild("cube_r5",
                ModelPartBuilder.create().uv(28, 99).cuboid(0.0F, -21.0F, 0.9F, 2.0F, 20.0F, 5.0F, new Dilation(0.0F)),
                ModelTransform.of(-12.0F, -13.75F, -30.0F, -1.309F, 0.3491F, 0.0F));

        ModelPartData top = section1.addChild("top", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData cube_r6 = top.addChild("cube_r6",
                ModelPartBuilder.create().uv(90, 21).cuboid(-4.0F, -12.4F, -27.0F, 8.0F, 2.0F, 3.0F, new Dilation(0.0F))
                        .uv(148, 59).cuboid(-1.5F, -19.0F, -12.0F, 3.0F, 4.0F, 3.0F, new Dilation(0.05F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.5236F, 0.0F));

        ModelPartData cube_r7 = top.addChild("cube_r7",
                ModelPartBuilder.create().uv(0, 0).cuboid(-4.0F, -4.0F, -28.9F, 8.0F, 2.0F, 18.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, -0.2618F, 0.5236F, 0.0F));

        ModelPartData cube_r8 = top.addChild("cube_r8", ModelPartBuilder.create().uv(35, 0).cuboid(-3.0F, -18.0F,
                -22.0F, 6.0F, 0.0F, 17.0F, new Dilation(0.001F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.2618F, 0.5236F, 0.0F));

        ModelPartData bottom = section1.addChild("bottom",
                ModelPartBuilder.create().uv(0, 91).cuboid(-7.0F, -7.0F, -15.0F, 14.0F, 7.0F, 2.0F, new Dilation(0.0F)),
                ModelTransform.pivot(0.0F, 0.0F, 2.0F));

        ModelPartData cube_r9 = bottom.addChild("cube_r9", ModelPartBuilder.create().uv(116, 16).cuboid(-2.5F, -9.0F,
                -19.0F, 5.0F, 9.0F, 5.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, -2.0F, 0.0F, 0.5236F, 0.0F));

        ModelPartData cube_r10 = bottom.addChild("cube_r10",
                ModelPartBuilder.create().uv(147, 77).cuboid(8.0F, -3.0F, -1.0F, 3.0F, 3.0F, 4.0F, new Dilation(0.0F))
                        .uv(147, 147).cuboid(-11.0F, -3.0F, -1.0F, 3.0F, 3.0F, 4.0F, new Dilation(0.0F)).uv(0, 21)
                        .cuboid(-8.0F, -3.0F, -1.0F, 16.0F, 3.0F, 12.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, -9.0F, -24.7F, -0.2618F, 0.0F, 0.0F));

        ModelPartData controls = section1.addChild("controls", ModelPartBuilder.create(),
                ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData biglever = controls.addChild("biglever",
                ModelPartBuilder.create().uv(23, 151).cuboid(-1.0F, -2.0F, -3.0F, 2.0F, 2.0F, 4.0F, new Dilation(0.0F))
                        .uv(106, 11).cuboid(-2.0F, -1.0F, -2.0F, 4.0F, 2.0F, 2.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, -16.25F, -11.0F, 0.2618F, 0.0F, 0.0F));

        ModelPartData bigleverlights = biglever.addChild("bigleverlights", ModelPartBuilder.create(),
                ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData red1 = bigleverlights.addChild("red1", ModelPartBuilder.create().uv(80, 117).cuboid(-1.0F, -2.05F,
                -3.0F, 1.0F, 0.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(0.5F, 0.0F, 0.0F));

        ModelPartData yellow1 = bigleverlights.addChild("yellow1", ModelPartBuilder.create().uv(115, 41).cuboid(-0.5F,
                -2.05F, -1.5F, 1.0F, 0.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData green1 = bigleverlights.addChild("green1",
                ModelPartBuilder.create().uv(90, 27).cuboid(-0.5F, -2.05F, 0.0F, 1.0F, 0.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData bone = biglever.addChild("bone",
                ModelPartBuilder.create().uv(27, 142)
                        .cuboid(-1.5F, -3.8F, -0.5F, 0.0F, 4.0F, 1.0F, new Dilation(0.001F)).uv(113, 21)
                        .cuboid(1.5F, -3.8F, -0.5F, 0.0F, 4.0F, 1.0F, new Dilation(0.001F)).uv(155, 141)
                        .cuboid(-1.5F, -3.8F, -0.5F, 1.0F, 0.0F, 1.0F, new Dilation(0.001F)).uv(98, 0)
                        .cuboid(-0.5F, -6.8F, -0.5F, 1.0F, 3.0F, 1.0F, new Dilation(0.0F)).uv(141, 151)
                        .cuboid(0.5F, -3.8F, -0.5F, 1.0F, 0.0F, 1.0F, new Dilation(0.001F)),
                ModelTransform.pivot(0.0F, -0.2F, -1.0F));

        ModelPartData tinyswitch = controls.addChild("tinyswitch",
                ModelPartBuilder.create().uv(155, 11).cuboid(10.0F, -1.0F, -3.0F, 2.0F, 1.0F, 2.0F, new Dilation(0.0F)),
                ModelTransform.of(-8.0F, -15.95F, -12.55F, 0.2618F, 0.0F, 0.0F));

        ModelPartData bone3 = tinyswitch.addChild("bone3",
                ModelPartBuilder.create().uv(128, 104).cuboid(-1.0F, -0.75F, 0.0F, 2.0F, 1.0F, 1.0F, new Dilation(0.0F))
                        .uv(157, 73).cuboid(-0.5F, -0.75F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.25F)),
                ModelTransform.pivot(11.0F, -1.0F, -2.0F));

        ModelPartData tinyswitch2 = controls.addChild("tinyswitch2",
                ModelPartBuilder.create().uv(155, 7).cuboid(10.0F, -1.0F, -3.0F, 2.0F, 1.0F, 2.0F, new Dilation(0.0F)),
                ModelTransform.of(-14.0F, -15.95F, -12.55F, 0.2618F, 0.0F, 0.0F));

        ModelPartData bone2 = tinyswitch2.addChild("bone2",
                ModelPartBuilder.create().uv(128, 4).cuboid(-1.0F, -0.75F, 0.0F, 2.0F, 1.0F, 1.0F, new Dilation(0.0F))
                        .uv(157, 56).cuboid(-0.5F, -0.75F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.25F)),
                ModelTransform.pivot(11.0F, -1.0F, -2.0F));

        ModelPartData sideswitches = controls
                .addChild("sideswitches",
                        ModelPartBuilder.create().uv(128, 100).cuboid(-2.0F, -1.0F, -3.0F, 3.0F, 2.0F, 7.0F,
                                new Dilation(0.0F)),
                        ModelTransform.of(-7.5F, -14.7806F, -20.0173F, 0.2472F, 0.3594F, 0.0043F));

        ModelPartData sideswitch1 = sideswitches.addChild("sideswitch1",
                ModelPartBuilder.create().uv(154, 21).cuboid(-1.0F, -3.0F, -1.0F, 2.0F, 1.0F, 2.0F, new Dilation(0.0F))
                        .uv(68, 151).cuboid(-1.0F, -2.0F, 0.0F, 2.0F, 1.0F, 0.0F, new Dilation(0.001F)),
                ModelTransform.pivot(-0.5F, 0.0F, 2.5F));

        ModelPartData sideswitch2 = sideswitches.addChild("sideswitch2",
                ModelPartBuilder.create().uv(125, 152).cuboid(-1.0F, -3.0F, -1.0F, 2.0F, 1.0F, 2.0F, new Dilation(0.0F))
                        .uv(85, 151).cuboid(-1.0F, -2.0F, 0.0F, 2.0F, 1.0F, 0.0F, new Dilation(0.001F)),
                ModelTransform.pivot(-0.5F, 0.0F, -1.5F));

        ModelPartData geiger1 = controls.addChild("geiger1",
                ModelPartBuilder.create().uv(0, 151).cuboid(-1.0F, -1.0F, -2.0F, 2.0F, 2.0F, 4.0F, new Dilation(0.0F)),
                ModelTransform.pivot(4.25F, -14.0F, -18.0F));

        ModelPartData needle1 = geiger1.addChild("needle1", ModelPartBuilder.create().uv(110, 21).cuboid(-0.5F, -2.0F,
                -0.02F, 1.0F, 2.0F, 0.0F, new Dilation(0.001F)), ModelTransform.pivot(-3.0F, 1.0F, -2.0F));

        ModelPartData geiger2 = controls.addChild("geiger2",
                ModelPartBuilder.create().uv(87, 150).cuboid(-1.0F, -1.0F, -2.0F, 2.0F, 2.0F, 4.0F, new Dilation(0.0F)),
                ModelTransform.pivot(1.75F, -14.0F, -18.0F));

        ModelPartData needle2 = geiger2.addChild("needle2", ModelPartBuilder.create().uv(10, 101).cuboid(-0.5F, -2.0F,
                -0.02F, 1.0F, 2.0F, 0.0F, new Dilation(0.001F)), ModelTransform.pivot(2.0F, 1.0F, -2.0F));

        ModelPartData multiswitchpanel = controls.addChild("multiswitchpanel",
                ModelPartBuilder.create().uv(27, 135).cuboid(-3.0F, -3.0F, -1.0F, 6.0F, 3.0F, 3.0F, new Dilation(0.0F)),
                ModelTransform.pivot(0.0F, -8.25F, -26.65F));

        ModelPartData longswitch1 = multiswitchpanel.addChild("longswitch1",
                ModelPartBuilder.create().uv(90, 157).cuboid(0.0F, -2.5F, -0.5F, 0.0F, 3.0F, 1.0F, new Dilation(0.001F))
                        .uv(17, 157).cuboid(-0.5F, -3.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.pivot(-2.25F, -2.5F, -0.5F));

        ModelPartData longswitch2 = multiswitchpanel.addChild("longswitch2",
                ModelPartBuilder.create().uv(87, 157).cuboid(0.0F, -2.5F, -0.5F, 0.0F, 3.0F, 1.0F, new Dilation(0.001F))
                        .uv(35, 157).cuboid(-0.5F, -3.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.pivot(-0.75F, -2.5F, -0.5F));

        ModelPartData longswitch3 = multiswitchpanel.addChild("longswitch3",
                ModelPartBuilder.create().uv(40, 157).cuboid(0.0F, -2.5F, -0.5F, 0.0F, 3.0F, 1.0F, new Dilation(0.001F))
                        .uv(47, 157).cuboid(-0.5F, -3.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.pivot(0.75F, -2.5F, -0.5F));

        ModelPartData longswitch4 = multiswitchpanel.addChild("longswitch4",
                ModelPartBuilder.create().uv(65, 156).cuboid(0.0F, -2.5F, -0.5F, 0.0F, 3.0F, 1.0F, new Dilation(0.001F))
                        .uv(52, 157).cuboid(-0.5F, -3.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.pivot(2.25F, -2.5F, -0.5F));

        ModelPartData fliplever1 = controls.addChild("fliplever1",
                ModelPartBuilder.create().uv(151, 90).cuboid(-1.0F, -2.0F, -1.0F, 2.0F, 2.0F, 3.0F, new Dilation(0.0F)),
                ModelTransform.of(-12.15F, -12.25F, -21.0F, 0.0F, 0.5236F, 0.0F));

        ModelPartData bone5 = fliplever1.addChild("bone5",
                ModelPartBuilder.create().uv(100, 150).cuboid(-0.5F, -7.0F, -1.0F, 1.0F, 8.0F, 2.0F, new Dilation(0.0F))
                        .uv(113, 52).cuboid(-0.5F, -7.0F, 1.0F, 0.0F, 2.0F, 1.0F, new Dilation(0.001F)).uv(118, 119)
                        .cuboid(-1.0F, -6.5F, 1.5F, 2.0F, 1.0F, 1.0F, new Dilation(0.0F)).uv(10, 101)
                        .cuboid(0.0F, -10.5F, 1.5F, 0.0F, 5.0F, 3.0F, new Dilation(0.001F)).uv(111, 100)
                        .cuboid(0.5F, -7.0F, 1.0F, 0.0F, 2.0F, 1.0F, new Dilation(0.001F)).uv(0, 120)
                        .cuboid(0.0F, -9.0F, 0.0F, 0.0F, 2.0F, 1.0F, new Dilation(0.001F)).uv(133, 59)
                        .cuboid(-0.5F, -13.0F, 0.0F, 1.0F, 4.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.pivot(0.0F, -1.0F, 0.5F));

        ModelPartData bell = controls.addChild("bell",
                ModelPartBuilder.create().uv(0, 21).cuboid(0.0F, -7.0F, -3.0F, 0.0F, 7.0F, 4.0F, new Dilation(0.001F)),
                ModelTransform.of(9.5F, -14.0F, -24.0F, 0.2618F, -0.3491F, 0.0F));

        ModelPartData bone4 = bell.addChild("bone4", ModelPartBuilder.create(),
                ModelTransform.pivot(0.0F, -6.0F, -3.0F));

        ModelPartData cube_r11 = bone4.addChild("cube_r11",
                ModelPartBuilder.create().uv(38, 99).cuboid(-1.5F, -3.0F, -6.0F, 3.0F, 1.0F, 3.0F, new Dilation(0.0F))
                        .uv(151, 51).cuboid(-1.0F, -5.0F, -5.5F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 6.0F, 3.0F, -0.2618F, 0.0F, 0.0F));

        ModelPartData dial5 = controls.addChild("dial5",
                ModelPartBuilder.create().uv(156, 96).cuboid(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                        .uv(155, 126).cuboid(-0.5F, -0.5F, -0.9F, 1.0F, 1.0F, 1.0F, new Dilation(-0.25F)).uv(83, 87)
                        .cuboid(-0.75F, -0.75F, -0.25F, 1.0F, 1.0F, 0.0F, new Dilation(0.001F)),
                ModelTransform.of(-3.0F, -13.75F, -21.15F, -1.309F, 0.0F, 0.0F));

        ModelPartData dial6 = controls.addChild("dial6",
                ModelPartBuilder.create().uv(152, 114).cuboid(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                        .uv(22, 152).cuboid(-0.5F, -0.5F, -0.9F, 1.0F, 1.0F, 1.0F, new Dilation(-0.25F)).uv(70, 0)
                        .cuboid(-0.75F, -0.75F, -0.25F, 1.0F, 1.0F, 0.0F, new Dilation(0.001F)),
                ModelTransform.of(-1.25F, -13.75F, -21.15F, -1.309F, 0.0F, 0.0F));

        ModelPartData section2 = alnico.addChild("section2", ModelPartBuilder.create(),
                ModelTransform.of(0.0F, 12.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData desktop2 = section2.addChild("desktop2", ModelPartBuilder.create().uv(22, 37).cuboid(-9.5F,
                -13.25F, -27.65F, 19.0F, 4.0F, 3.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 2.0F));

        ModelPartData cube_r12 = desktop2.addChild("cube_r12",
                ModelPartBuilder.create().uv(146, 59).cuboid(-8.0F, -4.0F, -8.0F, 1.0F, 0.0F, 1.0F, new Dilation(0.0F))
                        .uv(106, 147).cuboid(7.0F, -4.0F, -8.0F, 1.0F, 0.0F, 1.0F, new Dilation(0.0F)).uv(142, 93)
                        .cuboid(6.0F, -4.0F, -8.0F, 1.0F, 2.0F, 6.0F, new Dilation(0.0F)).uv(143, 35)
                        .cuboid(-7.0F, -4.0F, -8.0F, 1.0F, 2.0F, 6.0F, new Dilation(0.0F)).uv(55, 59)
                        .cuboid(-6.0F, -4.0F, -8.0F, 12.0F, 2.0F, 8.0F, new Dilation(0.0F)).uv(141, 154)
                        .cuboid(-5.0F, -4.0F, 0.0F, 1.0F, 2.0F, 2.0F, new Dilation(0.0F)).uv(155, 136)
                        .cuboid(4.0F, -4.0F, 0.0F, 1.0F, 2.0F, 2.0F, new Dilation(0.0F)).uv(53, 82)
                        .cuboid(-9.0F, -4.0F, -10.0F, 18.0F, 2.0F, 2.0F, new Dilation(0.0F)).uv(117, 126)
                        .cuboid(-4.0F, -4.0F, 0.0F, 8.0F, 2.0F, 4.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, -12.0F, -14.0F, 0.2618F, 0.0F, 0.0F));

        ModelPartData pillars2 = section2.addChild("pillars2", ModelPartBuilder.create().uv(111, 91).cuboid(-6.0F,
                -19.0F, -12.0F, 12.0F, 6.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 2.0F));

        ModelPartData cube_r13 = pillars2.addChild("cube_r13",
                ModelPartBuilder.create().uv(66, 87).cuboid(-2.0F, -21.0F, 0.9F, 2.0F, 20.0F, 5.0F, new Dilation(0.0F)),
                ModelTransform.of(10.9554F, -14.2676F, -27.1301F, -1.8326F, -0.3491F, 0.0F));

        ModelPartData cube_r14 = pillars2.addChild("cube_r14",
                ModelPartBuilder.create().uv(96, 91).cuboid(-2.0F, -21.0F, 0.9F, 2.0F, 20.0F, 5.0F, new Dilation(0.0F)),
                ModelTransform.of(12.0F, -13.75F, -30.0F, -1.309F, -0.3491F, 0.0F));

        ModelPartData cube_r15 = pillars2
                .addChild("cube_r15",
                        ModelPartBuilder.create().uv(81, 91).cuboid(-1.0F, -10.0F, -2.5F, 2.0F, 20.0F, 5.0F,
                                new Dilation(0.0F)),
                        ModelTransform.of(-6.6827F, -8.1365F, -18.3146F, -1.8326F, 0.3491F, 0.0F));

        ModelPartData cube_r16 = pillars2.addChild("cube_r16",
                ModelPartBuilder.create().uv(28, 99).cuboid(0.0F, -21.0F, 0.9F, 2.0F, 20.0F, 5.0F, new Dilation(0.0F)),
                ModelTransform.of(-12.0F, -13.75F, -30.0F, -1.309F, 0.3491F, 0.0F));

        ModelPartData top2 = section2.addChild("top2", ModelPartBuilder.create(),
                ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData cube_r17 = top2.addChild("cube_r17",
                ModelPartBuilder.create().uv(90, 21).cuboid(-4.0F, -12.4F, -27.0F, 8.0F, 2.0F, 3.0F, new Dilation(0.0F))
                        .uv(148, 59).cuboid(-1.5F, -19.0F, -12.0F, 3.0F, 4.0F, 3.0F, new Dilation(0.05F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.5236F, 0.0F));

        ModelPartData cube_r18 = top2.addChild("cube_r18",
                ModelPartBuilder.create().uv(0, 0).cuboid(-4.0F, -4.0F, -28.9F, 8.0F, 2.0F, 18.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, -0.2618F, 0.5236F, 0.0F));

        ModelPartData cube_r19 = top2.addChild("cube_r19", ModelPartBuilder.create().uv(35, 0).cuboid(-3.0F, -18.0F,
                -22.0F, 6.0F, 0.0F, 17.0F, new Dilation(0.001F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.2618F, 0.5236F, 0.0F));

        ModelPartData bottom2 = section2.addChild("bottom2",
                ModelPartBuilder.create().uv(0, 91).cuboid(-7.0F, -7.0F, -15.0F, 14.0F, 7.0F, 2.0F, new Dilation(0.0F)),
                ModelTransform.pivot(0.0F, 0.0F, 2.0F));

        ModelPartData cube_r20 = bottom2.addChild("cube_r20", ModelPartBuilder.create().uv(116, 16).cuboid(-2.5F, -9.0F,
                -19.0F, 5.0F, 9.0F, 5.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, -2.0F, 0.0F, 0.5236F, 0.0F));

        ModelPartData cube_r21 = bottom2.addChild("cube_r21",
                ModelPartBuilder.create().uv(147, 77).cuboid(8.0F, -3.0F, -1.0F, 3.0F, 3.0F, 4.0F, new Dilation(0.0F))
                        .uv(147, 147).cuboid(-11.0F, -3.0F, -1.0F, 3.0F, 3.0F, 4.0F, new Dilation(0.0F)).uv(0, 21)
                        .cuboid(-8.0F, -3.0F, -1.0F, 16.0F, 3.0F, 12.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, -9.0F, -24.7F, -0.2618F, 0.0F, 0.0F));

        ModelPartData controls2 = section2.addChild("controls2", ModelPartBuilder.create(),
                ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData computer = controls2.addChild("computer",
                ModelPartBuilder.create().uv(53, 70).cuboid(-4.0F, -0.25F, -4.95F, 8.0F, 2.0F, 9.0F, new Dilation(0.0F))
                        .uv(56, 113).cuboid(-3.0F, -4.25F, -2.95F, 6.0F, 4.0F, 6.0F, new Dilation(0.0F)).uv(62, 99)
                        .cuboid(-3.0F, -4.25F, -3.95F, 0.0F, 2.0F, 1.0F, new Dilation(0.001F)).uv(53, 99)
                        .cuboid(3.0F, -4.25F, -3.95F, 0.0F, 2.0F, 1.0F, new Dilation(0.001F)).uv(0, 15)
                        .cuboid(-3.0F, -4.25F, -3.95F, 6.0F, 0.0F, 1.0F, new Dilation(0.001F)),
                ModelTransform.pivot(0.0F, -16.25F, -13.95F));

        ModelPartData computernob = computer.addChild("computernob",
                ModelPartBuilder.create().uv(12, 157).cuboid(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                        .uv(134, 156).cuboid(-0.5F, -0.5F, -0.9F, 1.0F, 1.0F, 1.0F, new Dilation(-0.25F)).uv(38, 91)
                        .cuboid(-0.75F, -0.75F, -0.25F, 1.0F, 1.0F, 0.0F, new Dilation(0.001F)),
                ModelTransform.pivot(2.5F, 0.75F, -4.95F));

        ModelPartData pumpswitch1 = controls2.addChild("pumpswitch1", ModelPartBuilder.create().uv(145, 138)
                .cuboid(-1.0F, -1.0F, -2.4F, 2.0F, 2.0F, 5.0F, new Dilation(0.0F)),
                ModelTransform.pivot(-6.0F, -13.0F, -24.25F));

        ModelPartData bone6 = pumpswitch1.addChild("bone6",
                ModelPartBuilder.create().uv(156, 85).cuboid(-1.0F, -1.0F, -2.0F, 2.0F, 2.0F, 1.0F, new Dilation(0.0F))
                        .uv(148, 9).cuboid(0.0F, -0.5F, -1.0F, 0.0F, 1.0F, 6.0F, new Dilation(0.001F)).uv(36, 145)
                        .cuboid(-0.5F, 0.0F, -1.0F, 1.0F, 0.0F, 6.0F, new Dilation(0.001F)),
                ModelTransform.pivot(0.0F, 0.0F, -2.4F));

        ModelPartData pumpswitch2 = controls2.addChild("pumpswitch2",
                ModelPartBuilder.create().uv(144, 21).cuboid(-1.0F, -1.0F, -2.4F, 2.0F, 2.0F, 5.0F, new Dilation(0.0F)),
                ModelTransform.pivot(6.0F, -13.0F, -24.25F));

        ModelPartData bone7 = pumpswitch2.addChild("bone7",
                ModelPartBuilder.create().uv(80, 156).cuboid(-1.0F, -1.0F, -2.0F, 2.0F, 2.0F, 1.0F, new Dilation(0.0F))
                        .uv(145, 111).cuboid(0.0F, -0.5F, -1.0F, 0.0F, 1.0F, 6.0F, new Dilation(0.001F)).uv(46, 67)
                        .cuboid(-0.5F, 0.0F, -1.0F, 1.0F, 0.0F, 6.0F, new Dilation(0.001F)),
                ModelTransform.pivot(0.0F, 0.0F, -2.4F));

        ModelPartData dial1 = controls2.addChild("dial1",
                ModelPartBuilder.create().uv(129, 156).cuboid(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                        .uv(124, 156).cuboid(-0.5F, -0.5F, -0.9F, 1.0F, 1.0F, 1.0F, new Dilation(-0.25F)).uv(31, 91)
                        .cuboid(-0.75F, -0.75F, -0.25F, 1.0F, 1.0F, 0.0F, new Dilation(0.001F)),
                ModelTransform.pivot(-3.0F, -11.25F, -25.65F));

        ModelPartData dial2 = controls2.addChild("dial2",
                ModelPartBuilder.create().uv(151, 96).cuboid(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                        .uv(32, 151).cuboid(-0.5F, -0.5F, -0.9F, 1.0F, 1.0F, 1.0F, new Dilation(-0.25F)).uv(69, 18)
                        .cuboid(-0.75F, -0.75F, -0.25F, 1.0F, 1.0F, 0.0F, new Dilation(0.001F)),
                ModelTransform.pivot(-1.0F, -11.25F, -25.65F));

        ModelPartData dial3 = controls2.addChild("dial3",
                ModelPartBuilder.create().uv(9, 151).cuboid(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                        .uv(76, 149).cuboid(-0.5F, -0.5F, -0.9F, 1.0F, 1.0F, 1.0F, new Dilation(-0.25F)).uv(49, 67)
                        .cuboid(-0.75F, -0.75F, -0.25F, 1.0F, 1.0F, 0.0F, new Dilation(0.001F)),
                ModelTransform.pivot(1.0F, -11.25F, -25.65F));

        ModelPartData dial4 = controls2.addChild("dial4",
                ModelPartBuilder.create().uv(45, 148).cuboid(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                        .uv(148, 44).cuboid(-0.5F, -0.5F, -0.9F, 1.0F, 1.0F, 1.0F, new Dilation(-0.25F)).uv(60, 59)
                        .cuboid(-0.75F, -0.75F, -0.25F, 1.0F, 1.0F, 0.0F, new Dilation(0.001F)),
                ModelTransform.pivot(3.0F, -11.25F, -25.65F));

        ModelPartData waypointcatridge = controls2.addChild("waypointcatridge",
                ModelPartBuilder.create().uv(92, 130).cuboid(-1.5F, -4.0F, -1.0F, 3.0F, 4.0F, 6.0F, new Dilation(0.0F)),
                ModelTransform.of(-12.0F, -12.4F, -20.9F, 0.0F, 0.5236F, 0.0F));

        ModelPartData toastlever = waypointcatridge.addChild("toastlever",
                ModelPartBuilder.create().uv(65, 5).cuboid(-1.0F, -0.5F, -1.0F, 2.0F, 1.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.pivot(0.0F, -3.0F, -1.0F));

        ModelPartData toast1 = waypointcatridge.addChild("toast1", ModelPartBuilder.create().uv(69, 149).cuboid(-0.5F,
                -3.75F, -2.0F, 1.0F, 4.0F, 4.0F, new Dilation(-0.25F)), ModelTransform.pivot(-0.75F, -1.25F, 2.0F));

        ModelPartData toast2 = waypointcatridge.addChild("toast2", ModelPartBuilder.create().uv(47, 148).cuboid(-0.5F,
                -3.75F, -2.0F, 1.0F, 4.0F, 4.0F, new Dilation(-0.25F)), ModelTransform.pivot(0.75F, -1.25F, 2.0F));

        ModelPartData section3 = alnico.addChild("section3", ModelPartBuilder.create(),
                ModelTransform.of(0.0F, 12.0F, 0.0F, 0.0F, -2.0944F, 0.0F));

        ModelPartData desktop3 = section3.addChild("desktop3", ModelPartBuilder.create().uv(45, 21).cuboid(-9.5F,
                -13.25F, -27.65F, 19.0F, 4.0F, 3.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 2.0F));

        ModelPartData cube_r22 = desktop3.addChild("cube_r22",
                ModelPartBuilder.create().uv(144, 29).cuboid(-8.0F, -4.0F, -8.0F, 1.0F, 0.0F, 1.0F, new Dilation(0.0F))
                        .uv(91, 145).cuboid(7.0F, -4.0F, -8.0F, 1.0F, 0.0F, 1.0F, new Dilation(0.0F)).uv(142, 50)
                        .cuboid(6.0F, -4.0F, -8.0F, 1.0F, 2.0F, 6.0F, new Dilation(0.0F)).uv(142, 68)
                        .cuboid(-7.0F, -4.0F, -8.0F, 1.0F, 2.0F, 6.0F, new Dilation(0.0F)).uv(59, 37)
                        .cuboid(-6.0F, -4.0F, -8.0F, 12.0F, 2.0F, 8.0F, new Dilation(0.0F)).uv(140, 93)
                        .cuboid(-5.0F, -4.0F, 0.0F, 1.0F, 2.0F, 2.0F, new Dilation(0.0F)).uv(80, 151)
                        .cuboid(4.0F, -4.0F, 0.0F, 1.0F, 2.0F, 2.0F, new Dilation(0.0F)).uv(80, 16)
                        .cuboid(-9.0F, -4.0F, -10.0F, 18.0F, 2.0F, 2.0F, new Dilation(0.0F)).uv(39, 124)
                        .cuboid(-4.0F, -4.0F, 0.0F, 8.0F, 2.0F, 4.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, -12.0F, -14.0F, 0.2618F, 0.0F, 0.0F));

        ModelPartData pillars3 = section3.addChild("pillars3", ModelPartBuilder.create().uv(111, 91).cuboid(-6.0F,
                -19.0F, -12.0F, 12.0F, 6.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 2.0F));

        ModelPartData cube_r23 = pillars3.addChild("cube_r23",
                ModelPartBuilder.create().uv(66, 87).cuboid(-2.0F, -21.0F, 0.9F, 2.0F, 20.0F, 5.0F, new Dilation(0.0F)),
                ModelTransform.of(10.9554F, -14.2676F, -27.1301F, -1.8326F, -0.3491F, 0.0F));

        ModelPartData cube_r24 = pillars3.addChild("cube_r24",
                ModelPartBuilder.create().uv(96, 91).cuboid(-2.0F, -21.0F, 0.9F, 2.0F, 20.0F, 5.0F, new Dilation(0.0F)),
                ModelTransform.of(12.0F, -13.75F, -30.0F, -1.309F, -0.3491F, 0.0F));

        ModelPartData cube_r25 = pillars3
                .addChild("cube_r25",
                        ModelPartBuilder.create().uv(81, 91).cuboid(-1.0F, -10.0F, -2.5F, 2.0F, 20.0F, 5.0F,
                                new Dilation(0.0F)),
                        ModelTransform.of(-6.6827F, -8.1365F, -18.3146F, -1.8326F, 0.3491F, 0.0F));

        ModelPartData cube_r26 = pillars3.addChild("cube_r26",
                ModelPartBuilder.create().uv(28, 99).cuboid(0.0F, -21.0F, 0.9F, 2.0F, 20.0F, 5.0F, new Dilation(0.0F)),
                ModelTransform.of(-12.0F, -13.75F, -30.0F, -1.309F, 0.3491F, 0.0F));

        ModelPartData top3 = section3.addChild("top3", ModelPartBuilder.create(),
                ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData cube_r27 = top3.addChild("cube_r27",
                ModelPartBuilder.create().uv(90, 21).cuboid(-4.0F, -12.4F, -27.0F, 8.0F, 2.0F, 3.0F, new Dilation(0.0F))
                        .uv(148, 59).cuboid(-1.5F, -19.0F, -12.0F, 3.0F, 4.0F, 3.0F, new Dilation(0.05F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.5236F, 0.0F));

        ModelPartData cube_r28 = top3.addChild("cube_r28",
                ModelPartBuilder.create().uv(0, 0).cuboid(-4.0F, -4.0F, -28.9F, 8.0F, 2.0F, 18.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, -0.2618F, 0.5236F, 0.0F));

        ModelPartData cube_r29 = top3.addChild("cube_r29", ModelPartBuilder.create().uv(35, 0).cuboid(-3.0F, -18.0F,
                -22.0F, 6.0F, 0.0F, 17.0F, new Dilation(0.001F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.2618F, 0.5236F, 0.0F));

        ModelPartData bottom3 = section3.addChild("bottom3",
                ModelPartBuilder.create().uv(0, 91).cuboid(-7.0F, -7.0F, -15.0F, 14.0F, 7.0F, 2.0F, new Dilation(0.0F)),
                ModelTransform.pivot(0.0F, 0.0F, 2.0F));

        ModelPartData cube_r30 = bottom3.addChild("cube_r30", ModelPartBuilder.create().uv(116, 16).cuboid(-2.5F, -9.0F,
                -19.0F, 5.0F, 9.0F, 5.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, -2.0F, 0.0F, 0.5236F, 0.0F));

        ModelPartData cube_r31 = bottom3.addChild("cube_r31",
                ModelPartBuilder.create().uv(147, 77).cuboid(8.0F, -3.0F, -1.0F, 3.0F, 3.0F, 4.0F, new Dilation(0.0F))
                        .uv(147, 147).cuboid(-11.0F, -3.0F, -1.0F, 3.0F, 3.0F, 4.0F, new Dilation(0.0F)).uv(0, 21)
                        .cuboid(-8.0F, -3.0F, -1.0F, 16.0F, 3.0F, 12.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, -9.0F, -24.7F, -0.2618F, 0.0F, 0.0F));

        ModelPartData controls3 = section3.addChild("controls3", ModelPartBuilder.create(),
                ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData telepathiccircuit = controls3.addChild("telepathiccircuit", ModelPartBuilder.create(),
                ModelTransform.of(0.0F, -14.5967F, -18.0647F, 0.2618F, 0.0F, 0.0F));

        ModelPartData crystal = telepathiccircuit.addChild("crystal",
                ModelPartBuilder.create().uv(0, 101).cuboid(0.0F, -9.0F, -4.5F, 0.0F, 9.0F, 9.0F, new Dilation(0.001F)),
                ModelTransform.pivot(0.0F, 0.0F, 1.2F));

        ModelPartData cube_r32 = crystal.addChild("cube_r32",
                ModelPartBuilder.create().uv(43, 99).cuboid(0.0F, -9.0F, -4.5F, 0.0F, 9.0F, 9.0F, new Dilation(0.001F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.5708F, 0.0F));

        ModelPartData lowerlever = controls3.addChild("lowerlever",
                ModelPartBuilder.create().uv(137, 21).cuboid(-1.5F, -1.0F, -1.0F, 3.0F, 2.0F, 2.0F, new Dilation(0.0F)),
                ModelTransform.pivot(6.0F, -11.25F, -26.6F));

        ModelPartData bone8 = lowerlever.addChild("bone8",
                ModelPartBuilder.create().uv(13, 79).cuboid(0.0F, -1.5F, -0.5F, 0.0F, 2.0F, 1.0F, new Dilation(0.001F))
                        .uv(91, 141).cuboid(-0.5F, -3.5F, -0.5F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.pivot(-0.75F, -0.5F, -0.5F));

        ModelPartData bone9 = lowerlever.addChild("bone9",
                ModelPartBuilder.create().uv(0, 79).cuboid(0.0F, -1.5F, -0.5F, 0.0F, 2.0F, 1.0F, new Dilation(0.001F))
                        .uv(137, 74).cuboid(-0.5F, -3.5F, -0.5F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.pivot(0.75F, -0.5F, -0.5F));

        ModelPartData lowerlever2 = controls3.addChild("lowerlever2",
                ModelPartBuilder.create().uv(122, 47).cuboid(-1.5F, -1.0F, -1.0F, 3.0F, 2.0F, 2.0F, new Dilation(0.0F)),
                ModelTransform.pivot(-6.0F, -11.25F, -26.6F));

        ModelPartData bone10 = lowerlever2.addChild("bone10",
                ModelPartBuilder.create().uv(64, 36).cuboid(0.0F, -1.5F, -0.5F, 0.0F, 2.0F, 1.0F, new Dilation(0.001F))
                        .uv(133, 69).cuboid(-0.5F, -3.5F, -0.5F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.pivot(-0.75F, -0.5F, -0.5F));

        ModelPartData bone11 = lowerlever2.addChild("bone11",
                ModelPartBuilder.create().uv(0, 21).cuboid(0.0F, -1.5F, -0.5F, 0.0F, 2.0F, 1.0F, new Dilation(0.001F))
                        .uv(109, 65).cuboid(-0.5F, -3.5F, -0.5F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.pivot(0.75F, -0.5F, -0.5F));

        ModelPartData geiger = controls3.addChild("geiger",
                ModelPartBuilder.create().uv(151, 67).cuboid(-1.0F, -2.0F, -1.0F, 3.0F, 3.0F, 2.0F, new Dilation(0.0F))
                        .uv(43, 118).cuboid(-1.0F, -2.0F, -1.0F, 3.0F, 3.0F, 2.0F, new Dilation(0.2F)),
                ModelTransform.pivot(-0.5F, -10.75F, -26.6F));

        ModelPartData needle = geiger.addChild("needle", ModelPartBuilder.create().uv(48, 99).cuboid(-0.25F, -2.0F,
                -0.02F, 1.0F, 2.0F, 0.0F, new Dilation(0.001F)), ModelTransform.pivot(0.5F, 0.25F, -1.0F));

        ModelPartData siegemode = controls3.addChild("siegemode",
                ModelPartBuilder.create().uv(53, 99).cuboid(-1.0F, -2.0F, 0.0F, 2.0F, 3.0F, 4.0F, new Dilation(0.0F))
                        .uv(136, 143).cuboid(-0.5F, -1.25F, -4.5F, 1.0F, 1.0F, 6.0F, new Dilation(0.0F)),
                ModelTransform.of(-12.0F, -12.9F, -20.833F, 0.0F, 0.5236F, 0.0F));

        ModelPartData cube_r33 = siegemode.addChild("cube_r33", ModelPartBuilder.create().uv(151, 44).cuboid(-1.0F,
                0.0F, -3.0F, 2.0F, 3.0F, 3.0F, new Dilation(-0.001F)),
                ModelTransform.of(0.0F, -2.0F, 0.0F, 1.0036F, 0.0F, 0.0F));

        ModelPartData lever = siegemode.addChild("lever",
                ModelPartBuilder.create().uv(142, 117)
                        .cuboid(-0.6F, -3.5F, -0.5F, 0.0F, 4.0F, 1.0F, new Dilation(0.001F)).uv(30, 142)
                        .cuboid(0.6F, -3.5F, -0.5F, 0.0F, 4.0F, 1.0F, new Dilation(0.001F)).uv(22, 45)
                        .cuboid(-0.5F, -8.0F, -0.997F, 1.0F, 5.0F, 2.0F, new Dilation(0.0F)),
                ModelTransform.pivot(0.0F, -0.75F, -3.5F));

        ModelPartData sideswitches2 = controls3.addChild("sideswitches2",
                ModelPartBuilder.create().uv(128, 4).cuboid(-2.0F, -1.0F, -3.0F, 3.0F, 2.0F, 7.0F, new Dilation(0.0F)),
                ModelTransform.of(-7.5F, -14.7806F, -20.0173F, 0.2472F, 0.3594F, 0.0043F));

        ModelPartData sideswitch3 = sideswitches2.addChild("sideswitch3",
                ModelPartBuilder.create().uv(151, 17).cuboid(-1.0F, -3.0F, -1.0F, 2.0F, 1.0F, 2.0F, new Dilation(0.0F))
                        .uv(133, 50).cuboid(-1.0F, -2.0F, 0.0F, 2.0F, 1.0F, 0.0F, new Dilation(0.001F)),
                ModelTransform.pivot(-0.5F, 0.0F, 2.5F));

        ModelPartData sideswitch4 = sideswitches2.addChild("sideswitch4",
                ModelPartBuilder.create().uv(142, 129).cuboid(-1.0F, -3.0F, -1.0F, 2.0F, 1.0F, 2.0F, new Dilation(0.0F))
                        .uv(112, 130).cuboid(-1.0F, -2.0F, 0.0F, 2.0F, 1.0F, 0.0F, new Dilation(0.001F)),
                ModelTransform.pivot(-0.5F, 0.0F, -1.5F));

        ModelPartData sideswitches5 = controls3.addChild("sideswitches5",
                ModelPartBuilder.create().uv(25, 125).cuboid(-1.0F, -1.0F, -3.0F, 3.0F, 2.0F, 7.0F, new Dilation(0.0F)),
                ModelTransform.of(7.5F, -14.7806F, -20.0173F, 0.2472F, -0.3594F, -0.0043F));

        ModelPartData sideswitch9 = sideswitches5.addChild("sideswitch9",
                ModelPartBuilder.create().uv(142, 102).cuboid(-1.0F, -3.0F, -1.0F, 2.0F, 1.0F, 2.0F, new Dilation(0.0F))
                        .uv(25, 130).cuboid(-1.0F, -2.0F, 0.0F, 2.0F, 1.0F, 0.0F, new Dilation(0.001F)),
                ModelTransform.pivot(0.5F, 0.0F, 2.5F));

        ModelPartData sideswitch10 = sideswitches5.addChild("sideswitch10",
                ModelPartBuilder.create().uv(142, 77).cuboid(-1.0F, -3.0F, -1.0F, 2.0F, 1.0F, 2.0F, new Dilation(0.0F))
                        .uv(128, 117).cuboid(-1.0F, -2.0F, 0.0F, 2.0F, 1.0F, 0.0F, new Dilation(0.001F)),
                ModelTransform.pivot(0.5F, 0.0F, -1.5F));

        ModelPartData section4 = alnico.addChild("section4", ModelPartBuilder.create(),
                ModelTransform.of(0.0F, 12.0F, 0.0F, 0.0F, 3.1416F, 0.0F));

        ModelPartData desktop4 = section4.addChild("desktop4", ModelPartBuilder.create().uv(45, 21).cuboid(-9.5F,
                -13.25F, -27.65F, 19.0F, 4.0F, 3.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 2.0F));

        ModelPartData cube_r34 = desktop4.addChild("cube_r34",
                ModelPartBuilder.create().uv(142, 4).cuboid(-8.0F, -4.0F, -8.0F, 1.0F, 0.0F, 1.0F, new Dilation(0.0F))
                        .uv(142, 9).cuboid(7.0F, -4.0F, -8.0F, 1.0F, 0.0F, 1.0F, new Dilation(0.0F)).uv(142, 0)
                        .cuboid(6.0F, -4.0F, -8.0F, 1.0F, 2.0F, 6.0F, new Dilation(0.0F)).uv(27, 142)
                        .cuboid(-7.0F, -4.0F, -8.0F, 1.0F, 2.0F, 6.0F, new Dilation(0.0F)).uv(22, 56)
                        .cuboid(-6.0F, -4.0F, -8.0F, 12.0F, 2.0F, 8.0F, new Dilation(0.0F)).uv(88, 50)
                        .cuboid(-5.0F, -4.0F, 0.0F, 1.0F, 2.0F, 2.0F, new Dilation(0.0F)).uv(21, 118)
                        .cuboid(4.0F, -4.0F, 0.0F, 1.0F, 2.0F, 2.0F, new Dilation(0.0F)).uv(79, 70)
                        .cuboid(-9.0F, -4.0F, -10.0F, 18.0F, 2.0F, 2.0F, new Dilation(0.0F)).uv(121, 119)
                        .cuboid(-4.0F, -4.0F, 0.0F, 8.0F, 2.0F, 4.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, -12.0F, -14.0F, 0.2618F, 0.0F, 0.0F));

        ModelPartData pillars4 = section4.addChild("pillars4", ModelPartBuilder.create().uv(111, 91).cuboid(-6.0F,
                -19.0F, -12.0F, 12.0F, 6.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 2.0F));

        ModelPartData cube_r35 = pillars4.addChild("cube_r35",
                ModelPartBuilder.create().uv(66, 87).cuboid(-2.0F, -21.0F, 0.9F, 2.0F, 20.0F, 5.0F, new Dilation(0.0F)),
                ModelTransform.of(10.9554F, -14.2676F, -27.1301F, -1.8326F, -0.3491F, 0.0F));

        ModelPartData cube_r36 = pillars4.addChild("cube_r36",
                ModelPartBuilder.create().uv(96, 91).cuboid(-2.0F, -21.0F, 0.9F, 2.0F, 20.0F, 5.0F, new Dilation(0.0F)),
                ModelTransform.of(12.0F, -13.75F, -30.0F, -1.309F, -0.3491F, 0.0F));

        ModelPartData cube_r37 = pillars4
                .addChild("cube_r37",
                        ModelPartBuilder.create().uv(81, 91).cuboid(-1.0F, -10.0F, -2.5F, 2.0F, 20.0F, 5.0F,
                                new Dilation(0.0F)),
                        ModelTransform.of(-6.6827F, -8.1365F, -18.3146F, -1.8326F, 0.3491F, 0.0F));

        ModelPartData cube_r38 = pillars4.addChild("cube_r38",
                ModelPartBuilder.create().uv(28, 99).cuboid(0.0F, -21.0F, 0.9F, 2.0F, 20.0F, 5.0F, new Dilation(0.0F)),
                ModelTransform.of(-12.0F, -13.75F, -30.0F, -1.309F, 0.3491F, 0.0F));

        ModelPartData top4 = section4.addChild("top4", ModelPartBuilder.create(),
                ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData cube_r39 = top4.addChild("cube_r39",
                ModelPartBuilder.create().uv(90, 21).cuboid(-4.0F, -12.4F, -27.0F, 8.0F, 2.0F, 3.0F, new Dilation(0.0F))
                        .uv(148, 59).cuboid(-1.5F, -19.0F, -12.0F, 3.0F, 4.0F, 3.0F, new Dilation(0.05F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.5236F, 0.0F));

        ModelPartData cube_r40 = top4.addChild("cube_r40",
                ModelPartBuilder.create().uv(0, 0).cuboid(-4.0F, -4.0F, -28.9F, 8.0F, 2.0F, 18.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, -0.2618F, 0.5236F, 0.0F));

        ModelPartData cube_r41 = top4.addChild("cube_r41", ModelPartBuilder.create().uv(35, 0).cuboid(-3.0F, -18.0F,
                -22.0F, 6.0F, 0.0F, 17.0F, new Dilation(0.001F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.2618F, 0.5236F, 0.0F));

        ModelPartData bottom4 = section4.addChild("bottom4",
                ModelPartBuilder.create().uv(0, 91).cuboid(-7.0F, -7.0F, -15.0F, 14.0F, 7.0F, 2.0F, new Dilation(0.0F)),
                ModelTransform.pivot(0.0F, 0.0F, 2.0F));

        ModelPartData cube_r42 = bottom4.addChild("cube_r42", ModelPartBuilder.create().uv(116, 16).cuboid(-2.5F, -9.0F,
                -19.0F, 5.0F, 9.0F, 5.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, -2.0F, 0.0F, 0.5236F, 0.0F));

        ModelPartData cube_r43 = bottom4.addChild("cube_r43",
                ModelPartBuilder.create().uv(147, 77).cuboid(8.0F, -3.0F, -1.0F, 3.0F, 3.0F, 4.0F, new Dilation(0.0F))
                        .uv(147, 147).cuboid(-11.0F, -3.0F, -1.0F, 3.0F, 3.0F, 4.0F, new Dilation(0.0F)).uv(0, 21)
                        .cuboid(-8.0F, -3.0F, -1.0F, 16.0F, 3.0F, 12.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, -9.0F, -24.7F, -0.2618F, 0.0F, 0.0F));

        ModelPartData controls4 = section4.addChild("controls4", ModelPartBuilder.create(),
                ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData biglever2 = controls4.addChild("biglever2",
                ModelPartBuilder.create().uv(149, 119)
                        .cuboid(8.75F, -19.25F, 3.0F, 2.0F, 2.0F, 4.0F, new Dilation(0.0F)).uv(58, 87)
                        .cuboid(7.75F, -18.25F, 4.0F, 4.0F, 2.0F, 2.0F, new Dilation(0.0F)),
                ModelTransform.of(-20.75F, 5.5F, -16.5F, 0.2618F, 0.5236F, 0.0F));

        ModelPartData bone12 = biglever2.addChild("bone12",
                ModelPartBuilder.create().uv(9, 25).cuboid(-1.5F, -3.5F, -0.5F, 0.0F, 4.0F, 1.0F, new Dilation(0.001F))
                        .uv(62, 107).cuboid(1.5F, -3.5F, -0.5F, 0.0F, 4.0F, 1.0F, new Dilation(0.001F)).uv(99, 117)
                        .cuboid(-1.5F, -3.5F, -0.5F, 1.0F, 0.0F, 1.0F, new Dilation(0.001F)).uv(13, 10)
                        .cuboid(-0.5F, -6.5F, -0.5F, 1.0F, 3.0F, 1.0F, new Dilation(0.0F)).uv(104, 117)
                        .cuboid(0.5F, -3.5F, -0.5F, 1.0F, 0.0F, 1.0F, new Dilation(0.001F)),
                ModelTransform.pivot(9.75F, -17.75F, 5.0F));

        ModelPartData bigleverlights2 = biglever2.addChild("bigleverlights2", ModelPartBuilder.create(),
                ModelTransform.pivot(9.75F, -17.25F, 6.0F));

        ModelPartData red2 = bigleverlights2.addChild("red2",
                ModelPartBuilder.create().uv(88, 48).cuboid(-1.0F, -2.05F, -3.0F, 1.0F, 0.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.pivot(0.5F, 0.0F, 0.0F));

        ModelPartData yellow2 = bigleverlights2.addChild("yellow2",
                ModelPartBuilder.create().uv(53, 29).cuboid(-0.5F, -2.05F, -1.5F, 1.0F, 0.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData green2 = bigleverlights2.addChild("green2",
                ModelPartBuilder.create().uv(20, 37).cuboid(-0.5F, -2.05F, 0.0F, 1.0F, 0.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData tinyswitch3 = controls4.addChild("tinyswitch3", ModelPartBuilder.create(),
                ModelTransform.of(-3.0F, -14.25F, -14.0F, -0.2618F, 0.0F, 0.0F));

        ModelPartData cube_r44 = tinyswitch3.addChild("cube_r44",
                ModelPartBuilder.create().uv(147, 85).cuboid(-1.0F, -2.0F, -1.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.2618F, 0.0F, 0.0F));

        ModelPartData bone16 = tinyswitch3.addChild("bone16", ModelPartBuilder.create(),
                ModelTransform.pivot(0.0F, -1.9F, -0.5F));

        ModelPartData cube_r45 = bone16.addChild("cube_r45", ModelPartBuilder.create().uv(96, 150).cuboid(-0.75F, -3.0F,
                0.0F, 2.0F, 1.0F, 0.0F, new Dilation(0.001F)),
                ModelTransform.of(0.0F, 1.9F, 0.5F, 0.2618F, 0.0F, 0.0F));

        ModelPartData tinyswitch4 = controls4.addChild("tinyswitch4", ModelPartBuilder.create(),
                ModelTransform.of(0.0F, -14.25F, -14.0F, -0.2618F, 0.0F, 0.0F));

        ModelPartData cube_r46 = tinyswitch4.addChild("cube_r46",
                ModelPartBuilder.create().uv(138, 50).cuboid(-1.0F, -2.0F, -1.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.2618F, 0.0F, 0.0F));

        ModelPartData bone13 = tinyswitch4.addChild("bone13", ModelPartBuilder.create(),
                ModelTransform.pivot(0.0F, -1.9F, -0.5F));

        ModelPartData cube_r47 = bone13.addChild("cube_r47", ModelPartBuilder.create().uv(149, 136).cuboid(-0.75F,
                -3.0F, 0.0F, 2.0F, 1.0F, 0.0F, new Dilation(0.001F)),
                ModelTransform.of(0.0F, 1.9F, 0.5F, 0.2618F, 0.0F, 0.0F));

        ModelPartData tinyswitch5 = controls4.addChild("tinyswitch5", ModelPartBuilder.create(),
                ModelTransform.of(3.0F, -14.25F, -14.0F, -0.2618F, 0.0F, 0.0F));

        ModelPartData cube_r48 = tinyswitch5.addChild("cube_r48",
                ModelPartBuilder.create().uv(23, 125).cuboid(-1.0F, -2.0F, -1.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.2618F, 0.0F, 0.0F));

        ModelPartData bone14 = tinyswitch5.addChild("bone14", ModelPartBuilder.create(),
                ModelTransform.pivot(0.0F, -1.9F, -0.5F));

        ModelPartData cube_r49 = bone14.addChild("cube_r49", ModelPartBuilder.create().uv(149, 109).cuboid(-0.75F,
                -3.0F, 0.0F, 2.0F, 1.0F, 0.0F, new Dilation(0.001F)),
                ModelTransform.of(0.0F, 1.9F, 0.5F, 0.2618F, 0.0F, 0.0F));

        ModelPartData tinylight = controls4.addChild("tinylight",
                ModelPartBuilder.create().uv(152, 109)
                        .cuboid(-1.0F, -0.925F, -1.0F, 2.0F, 2.0F, 2.0F, new Dilation(-0.4F)).uv(36, 142)
                        .cuboid(-1.0F, -0.075F, -1.0F, 2.0F, 0.0F, 2.0F, new Dilation(0.001F)),
                ModelTransform.of(-3.0F, -14.975F, -16.2F, 0.2618F, 0.0F, 0.0F));

        ModelPartData bone87 = tinylight.addChild("bone87", ModelPartBuilder.create().uv(107, 152).cuboid(-7.0F, -1.0F,
                -3.7F, 2.0F, 2.0F, 2.0F, new Dilation(-0.38F)), ModelTransform.pivot(6.0F, 0.075F, 2.7F));

        ModelPartData tinylight2 = controls4.addChild("tinylight2",
                ModelPartBuilder.create().uv(36, 152)
                        .cuboid(-1.0F, -0.925F, -1.0F, 2.0F, 2.0F, 2.0F, new Dilation(-0.4F)).uv(105, 130)
                        .cuboid(-1.0F, -0.075F, -1.0F, 2.0F, 0.0F, 2.0F, new Dilation(0.001F)),
                ModelTransform.of(0.0F, -14.975F, -16.2F, 0.2618F, 0.0F, 0.0F));

        ModelPartData bone15 = tinylight2.addChild("bone15", ModelPartBuilder.create().uv(152, 35).cuboid(-7.0F, -1.0F,
                -3.7F, 2.0F, 2.0F, 2.0F, new Dilation(-0.38F)), ModelTransform.pivot(6.0F, 0.075F, 2.7F));

        ModelPartData tinylight3 = controls4.addChild("tinylight3",
                ModelPartBuilder.create().uv(13, 152)
                        .cuboid(-1.0F, -0.925F, -1.0F, 2.0F, 2.0F, 2.0F, new Dilation(-0.4F)).uv(71, 18)
                        .cuboid(-1.0F, -0.075F, -1.0F, 2.0F, 0.0F, 2.0F, new Dilation(0.001F)),
                ModelTransform.of(3.0F, -14.975F, -16.2F, 0.2618F, 0.0F, 0.0F));

        ModelPartData bone17 = tinylight3.addChild("bone17", ModelPartBuilder.create().uv(134, 151).cuboid(-7.0F, -1.0F,
                -3.7F, 2.0F, 2.0F, 2.0F, new Dilation(-0.38F)), ModelTransform.pivot(6.0F, 0.075F, 2.7F));

        ModelPartData keyboard = controls4.addChild("keyboard", ModelPartBuilder.create().uv(113, 52).cuboid(-5.0F,
                -2.0F, -3.0F, 10.0F, 2.0F, 4.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, -13.25F, -19.0F, 0.2618F, 0.0F, 0.0F));

        ModelPartData wrench1 = controls4.addChild("wrench1",
                ModelPartBuilder.create().uv(94, 117).cuboid(-1.0F, -2.0F, -1.0F, 1.0F, 1.0F, 2.0F, new Dilation(0.0F)),
                ModelTransform.pivot(-6.5F, -10.25F, -26.6F));

        ModelPartData bone18 = wrench1.addChild("bone18",
                ModelPartBuilder.create().uv(22, 56).cuboid(-1.5F, -1.5F, 0.0F, 3.0F, 7.0F, 0.0F, new Dilation(0.001F)),
                ModelTransform.pivot(-0.5F, -1.5F, 0.0F));

        ModelPartData wrench2 = controls4.addChild("wrench2",
                ModelPartBuilder.create().uv(92, 41).cuboid(-1.0F, -2.0F, -1.0F, 1.0F, 1.0F, 2.0F, new Dilation(0.0F)),
                ModelTransform.pivot(-2.5F, -10.25F, -26.6F));

        ModelPartData bone19 = wrench2.addChild("bone19",
                ModelPartBuilder.create().uv(0, 101).cuboid(-1.5F, -1.5F, 0.0F, 3.0F, 8.0F, 0.0F, new Dilation(0.001F)),
                ModelTransform.pivot(-0.5F, -1.5F, 0.0F));

        ModelPartData wrench3 = controls4.addChild("wrench3",
                ModelPartBuilder.create().uv(33, 91).cuboid(-1.0F, -2.0F, -1.0F, 1.0F, 1.0F, 2.0F, new Dilation(0.0F)),
                ModelTransform.pivot(3.5F, -10.25F, -26.6F));

        ModelPartData bone20 = wrench3.addChild("bone20", ModelPartBuilder.create().uv(58, 151).cuboid(-1.5F, -1.5F,
                0.0F, 3.0F, 9.0F, 0.0F, new Dilation(0.001F)), ModelTransform.pivot(-0.5F, -1.5F, 0.0F));

        ModelPartData wrench4 = controls4.addChild("wrench4",
                ModelPartBuilder.create().uv(5, 21).cuboid(-1.0F, -2.0F, -1.0F, 1.0F, 1.0F, 2.0F, new Dilation(0.0F)),
                ModelTransform.pivot(7.5F, -10.25F, -26.6F));

        ModelPartData bone21 = wrench4.addChild("bone21",
                ModelPartBuilder.create().uv(44, 0).cuboid(-1.5F, -1.5F, 0.0F, 3.0F, 10.0F, 0.0F, new Dilation(0.001F)),
                ModelTransform.pivot(-0.5F, -1.5F, 0.0F));

        ModelPartData section5 = alnico.addChild("section5", ModelPartBuilder.create(),
                ModelTransform.of(0.0F, 12.0F, 0.0F, 0.0F, 2.0944F, 0.0F));

        ModelPartData desktop5 = section5.addChild("desktop5", ModelPartBuilder.create().uv(45, 21).cuboid(-9.5F,
                -13.25F, -27.65F, 19.0F, 4.0F, 3.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 2.0F));

        ModelPartData cube_r50 = desktop5.addChild("cube_r50",
                ModelPartBuilder.create().uv(137, 26).cuboid(-8.0F, -4.0F, -8.0F, 1.0F, 0.0F, 1.0F, new Dilation(0.0F))
                        .uv(137, 78).cuboid(7.0F, -4.0F, -8.0F, 1.0F, 0.0F, 1.0F, new Dilation(0.0F)).uv(140, 120)
                        .cuboid(6.0F, -4.0F, -8.0F, 1.0F, 2.0F, 6.0F, new Dilation(0.0F)).uv(91, 141)
                        .cuboid(-7.0F, -4.0F, -8.0F, 1.0F, 2.0F, 6.0F, new Dilation(0.0F)).uv(55, 48)
                        .cuboid(-6.0F, -4.0F, -8.0F, 12.0F, 2.0F, 8.0F, new Dilation(0.0F)).uv(65, 0)
                        .cuboid(-5.0F, -4.0F, 0.0F, 1.0F, 2.0F, 2.0F, new Dilation(0.0F)).uv(22, 67)
                        .cuboid(4.0F, -4.0F, 0.0F, 1.0F, 2.0F, 2.0F, new Dilation(0.0F)).uv(65, 11)
                        .cuboid(-9.0F, -4.0F, -10.0F, 18.0F, 2.0F, 2.0F, new Dilation(0.0F)).uv(0, 120)
                        .cuboid(-4.0F, -4.0F, 0.0F, 8.0F, 2.0F, 4.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, -12.0F, -14.0F, 0.2618F, 0.0F, 0.0F));

        ModelPartData pillars5 = section5.addChild("pillars5", ModelPartBuilder.create().uv(111, 91).cuboid(-6.0F,
                -19.0F, -12.0F, 12.0F, 6.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 2.0F));

        ModelPartData cube_r51 = pillars5.addChild("cube_r51",
                ModelPartBuilder.create().uv(66, 87).cuboid(-2.0F, -21.0F, 0.9F, 2.0F, 20.0F, 5.0F, new Dilation(0.0F)),
                ModelTransform.of(10.9554F, -14.2676F, -27.1301F, -1.8326F, -0.3491F, 0.0F));

        ModelPartData cube_r52 = pillars5.addChild("cube_r52",
                ModelPartBuilder.create().uv(96, 91).cuboid(-2.0F, -21.0F, 0.9F, 2.0F, 20.0F, 5.0F, new Dilation(0.0F)),
                ModelTransform.of(12.0F, -13.75F, -30.0F, -1.309F, -0.3491F, 0.0F));

        ModelPartData cube_r53 = pillars5
                .addChild("cube_r53",
                        ModelPartBuilder.create().uv(81, 91).cuboid(-1.0F, -10.0F, -2.5F, 2.0F, 20.0F, 5.0F,
                                new Dilation(0.0F)),
                        ModelTransform.of(-6.6827F, -8.1365F, -18.3146F, -1.8326F, 0.3491F, 0.0F));

        ModelPartData cube_r54 = pillars5.addChild("cube_r54",
                ModelPartBuilder.create().uv(28, 99).cuboid(0.0F, -21.0F, 0.9F, 2.0F, 20.0F, 5.0F, new Dilation(0.0F)),
                ModelTransform.of(-12.0F, -13.75F, -30.0F, -1.309F, 0.3491F, 0.0F));

        ModelPartData top5 = section5.addChild("top5", ModelPartBuilder.create(),
                ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData cube_r55 = top5.addChild("cube_r55",
                ModelPartBuilder.create().uv(90, 21).cuboid(-4.0F, -12.4F, -27.0F, 8.0F, 2.0F, 3.0F, new Dilation(0.0F))
                        .uv(148, 59).cuboid(-1.5F, -19.0F, -12.0F, 3.0F, 4.0F, 3.0F, new Dilation(0.05F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.5236F, 0.0F));

        ModelPartData cube_r56 = top5.addChild("cube_r56",
                ModelPartBuilder.create().uv(0, 0).cuboid(-4.0F, -4.0F, -28.9F, 8.0F, 2.0F, 18.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, -0.2618F, 0.5236F, 0.0F));

        ModelPartData cube_r57 = top5.addChild("cube_r57", ModelPartBuilder.create().uv(35, 0).cuboid(-3.0F, -18.0F,
                -22.0F, 6.0F, 0.0F, 17.0F, new Dilation(0.001F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.2618F, 0.5236F, 0.0F));

        ModelPartData bottom5 = section5.addChild("bottom5",
                ModelPartBuilder.create().uv(0, 91).cuboid(-7.0F, -7.0F, -15.0F, 14.0F, 7.0F, 2.0F, new Dilation(0.0F)),
                ModelTransform.pivot(0.0F, 0.0F, 2.0F));

        ModelPartData cube_r58 = bottom5.addChild("cube_r58", ModelPartBuilder.create().uv(116, 16).cuboid(-2.5F, -9.0F,
                -19.0F, 5.0F, 9.0F, 5.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, -2.0F, 0.0F, 0.5236F, 0.0F));

        ModelPartData cube_r59 = bottom5.addChild("cube_r59",
                ModelPartBuilder.create().uv(147, 77).cuboid(8.0F, -3.0F, -1.0F, 3.0F, 3.0F, 4.0F, new Dilation(0.0F))
                        .uv(147, 147).cuboid(-11.0F, -3.0F, -1.0F, 3.0F, 3.0F, 4.0F, new Dilation(0.0F)).uv(0, 21)
                        .cuboid(-8.0F, -3.0F, -1.0F, 16.0F, 3.0F, 12.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, -9.0F, -24.7F, -0.2618F, 0.0F, 0.0F));

        ModelPartData controls5 = section5.addChild("controls5", ModelPartBuilder.create(),
                ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData taperecorder = controls5.addChild("taperecorder",
                ModelPartBuilder.create().uv(111, 75).cuboid(-5.0F, -1.0F, -3.0F, 10.0F, 2.0F, 5.0F, new Dilation(0.0F))
                        .uv(98, 0).cuboid(-1.0F, -2.0F, -3.0F, 2.0F, 1.0F, 5.0F, new Dilation(0.001F)),
                ModelTransform.of(0.0F, -16.5F, -14.0F, 0.2618F, 0.0F, 0.0F));

        ModelPartData bone30 = taperecorder.addChild("bone30", ModelPartBuilder.create(),
                ModelTransform.of(2.3579F, -1.5F, -1.265F, 0.0F, -0.3491F, 0.0F));

        ModelPartData bone32 = bone30.addChild("bone32",
                ModelPartBuilder.create().uv(88, 65).cuboid(-1.5F, -0.5F, 0.0F, 3.0F, 1.0F, 0.0F, new Dilation(0.001F)),
                ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData bone29 = taperecorder.addChild("bone29", ModelPartBuilder.create(),
                ModelTransform.of(-2.4095F, -1.5F, -1.265F, 0.0F, 0.3491F, 0.0F));

        ModelPartData bone31 = bone29.addChild("bone31",
                ModelPartBuilder.create().uv(22, 72).cuboid(-1.5F, -0.5F, 0.0F, 3.0F, 1.0F, 0.0F, new Dilation(0.001F)),
                ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData bone22 = taperecorder.addChild("bone22",
                ModelPartBuilder.create().uv(120, 69).cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 1.0F, 4.0F, new Dilation(0.0F)),
                ModelTransform.of(-3.5F, -2.0F, 0.5F, 0.0F, -1.5708F, 0.0F));

        ModelPartData bone23 = taperecorder.addChild("bone23",
                ModelPartBuilder.create().uv(0, 79).cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 1.0F, 4.0F, new Dilation(0.0F)),
                ModelTransform.pivot(3.5F, -2.0F, 0.5F));

        ModelPartData geiger4 = controls5.addChild("geiger4", ModelPartBuilder.create().uv(149, 129).cuboid(-1.0F,
                -1.0F, -2.0F, 2.0F, 2.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(-4.25F, -14.0F, -18.0F));

        ModelPartData needle4 = geiger4.addChild("needle4", ModelPartBuilder.create().uv(38, 99).cuboid(-0.5F, -2.0F,
                -0.02F, 1.0F, 2.0F, 0.0F, new Dilation(0.001F)), ModelTransform.pivot(0.0F, 1.0F, -2.0F));

        ModelPartData geiger3 = controls5.addChild("geiger3", ModelPartBuilder.create().uv(149, 102).cuboid(-1.0F,
                -1.0F, -2.0F, 2.0F, 2.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(4.25F, -14.0F, -18.0F));

        ModelPartData needle3 = geiger3.addChild("needle3", ModelPartBuilder.create().uv(55, 45).cuboid(-0.5F, -2.0F,
                -0.02F, 1.0F, 2.0F, 0.0F, new Dilation(0.001F)), ModelTransform.pivot(0.0F, 1.0F, -2.0F));

        ModelPartData tinyswitch6 = controls5.addChild("tinyswitch6", ModelPartBuilder.create().uv(9, 0).cuboid(7.4378F,
                -3.4749F, -12.2364F, 2.0F, 1.0F, 2.0F, new Dilation(0.0F)),
                ModelTransform.pivot(-11.5F, -10.7F, -12.55F));

        ModelPartData bone24 = tinyswitch6.addChild("bone24",
                ModelPartBuilder.create().uv(55, 70)
                        .cuboid(-1.0622F, -0.7749F, -0.0364F, 2.0F, 1.0F, 1.0F, new Dilation(0.0F)).uv(88, 79)
                        .cuboid(-0.5622F, -0.7749F, -0.5364F, 1.0F, 1.0F, 1.0F, new Dilation(-0.25F)),
                ModelTransform.pivot(8.5F, -3.45F, -11.2F));

        ModelPartData tinyswitch7 = controls5.addChild("tinyswitch7", ModelPartBuilder.create().uv(101, 37).cuboid(6.5F,
                -3.4749F, -12.2364F, 2.0F, 1.0F, 2.0F, new Dilation(0.0F)),
                ModelTransform.pivot(-7.5F, -10.7F, -12.55F));

        ModelPartData bone25 = tinyswitch7.addChild("bone25",
                ModelPartBuilder.create().uv(115, 31)
                        .cuboid(-1.0F, -0.7249F, 0.0136F, 2.0F, 1.0F, 1.0F, new Dilation(0.0F)).uv(28, 101)
                        .cuboid(-0.5F, -0.7249F, -0.4864F, 1.0F, 1.0F, 1.0F, new Dilation(-0.25F)),
                ModelTransform.pivot(7.5F, -3.5F, -11.25F));

        ModelPartData tinyswitch9 = controls5.addChild("tinyswitch9", ModelPartBuilder.create().uv(92, 37)
                .cuboid(-9.4378F, -3.4749F, -12.2364F, 2.0F, 1.0F, 2.0F, new Dilation(0.0F)),
                ModelTransform.pivot(11.5F, -10.7F, -12.55F));

        ModelPartData bone26 = tinyswitch9.addChild("bone26",
                ModelPartBuilder.create().uv(58, 92)
                        .cuboid(-0.9378F, -0.7749F, -0.0364F, 2.0F, 1.0F, 1.0F, new Dilation(0.0F)).uv(14, 101)
                        .cuboid(-0.4378F, -0.7749F, -0.5364F, 1.0F, 1.0F, 1.0F, new Dilation(-0.25F)),
                ModelTransform.pivot(-8.5F, -3.45F, -11.2F));

        ModelPartData tinylight4 = controls5.addChild("tinylight4",
                ModelPartBuilder.create().uv(119, 11)
                        .cuboid(2.0F, -2.2699F, -6.0191F, 2.0F, 2.0F, 2.0F, new Dilation(-0.4F)).uv(62, 18)
                        .cuboid(2.0F, -1.4199F, -6.0191F, 2.0F, 0.0F, 2.0F, new Dilation(0.001F)),
                ModelTransform.pivot(-9.0F, -11.925F, -19.1F));

        ModelPartData bone27 = tinylight4.addChild("bone27", ModelPartBuilder.create().uv(91, 91).cuboid(-4.0F,
                -2.3449F, -8.7191F, 2.0F, 2.0F, 2.0F, new Dilation(-0.38F)), ModelTransform.pivot(6.0F, 0.075F, 2.7F));

        ModelPartData tinylight5 = controls5.addChild("tinylight5",
                ModelPartBuilder.create().uv(76, 87)
                        .cuboid(-4.0F, -2.2699F, -6.0191F, 2.0F, 2.0F, 2.0F, new Dilation(-0.4F)).uv(53, 18)
                        .cuboid(-4.0F, -1.4199F, -6.0191F, 2.0F, 0.0F, 2.0F, new Dilation(0.001F)),
                ModelTransform.pivot(9.0F, -11.925F, -19.1F));

        ModelPartData bone28 = tinylight5.addChild("bone28", ModelPartBuilder.create().uv(53, 74).cuboid(2.0F, -2.3449F,
                -8.7191F, 2.0F, 2.0F, 2.0F, new Dilation(-0.38F)), ModelTransform.pivot(-6.0F, 0.075F, 2.7F));

        ModelPartData cassetteplayer = controls5.addChild("cassetteplayer",
                ModelPartBuilder.create().uv(96, 65).cuboid(-2.0F, 0.0F, -1.0F, 4.0F, 2.0F, 2.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, -15.0306F, -20.0173F, 0.2618F, 0.0F, 0.0F));

        ModelPartData refueler = controls5.addChild("refueler",
                ModelPartBuilder.create().uv(134, 28).cuboid(-2.0F, -5.0F, -1.0F, 3.0F, 9.0F, 3.0F, new Dilation(0.0F))
                        .uv(106, 91).cuboid(-1.75F, -6.0F, -0.5F, 0.0F, 1.0F, 2.0F, new Dilation(0.001F)).uv(60, 48)
                        .cuboid(-2.25F, -6.0F, -0.5F, 1.0F, 1.0F, 0.0F, new Dilation(0.001F)).uv(60, 18)
                        .cuboid(-2.25F, -6.0F, 1.5F, 1.0F, 1.0F, 0.0F, new Dilation(0.001F)).uv(27, 45)
                        .cuboid(0.25F, -6.0F, 1.5F, 1.0F, 1.0F, 0.0F, new Dilation(0.001F)).uv(81, 92)
                        .cuboid(0.75F, -6.0F, -0.5F, 0.0F, 1.0F, 2.0F, new Dilation(0.001F)).uv(49, 11)
                        .cuboid(0.25F, -6.0F, -0.5F, 1.0F, 1.0F, 0.0F, new Dilation(0.001F)),
                ModelTransform.of(-11.0F, -13.4F, -20.083F, 1.309F, 0.5236F, 0.0F));

        ModelPartData gasknob = refueler.addChild("gasknob",
                ModelPartBuilder.create().uv(118, 69).cuboid(-0.5F, -0.8F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.2F))
                        .uv(106, 75).cuboid(-1.0F, -1.4F, -1.0F, 2.0F, 1.0F, 2.0F, new Dilation(-0.2F)),
                ModelTransform.pivot(-0.5F, -5.0F, 0.5F));

        ModelPartData multiswitchpanel2 = controls5.addChild("multiswitchpanel2",
                ModelPartBuilder.create().uv(132, 14).cuboid(-3.0F, -3.0F, -1.0F, 6.0F, 3.0F, 3.0F, new Dilation(0.0F)),
                ModelTransform.pivot(0.0F, -8.25F, -26.65F));

        ModelPartData longswitch5 = multiswitchpanel2.addChild("longswitch5",
                ModelPartBuilder.create().uv(44, 156).cuboid(0.0F, -2.5F, -0.5F, 0.0F, 3.0F, 1.0F, new Dilation(0.001F))
                        .uv(145, 146).cuboid(-0.5F, -3.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.pivot(-2.25F, -2.5F, -0.5F));

        ModelPartData longswitch6 = multiswitchpanel2.addChild("longswitch6",
                ModelPartBuilder.create().uv(65, 151).cuboid(0.0F, -2.5F, -0.5F, 0.0F, 3.0F, 1.0F, new Dilation(0.001F))
                        .uv(36, 145).cuboid(-0.5F, -3.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.pivot(-0.75F, -2.5F, -0.5F));

        ModelPartData longswitch7 = multiswitchpanel2.addChild("longswitch7",
                ModelPartBuilder.create().uv(149, 9).cuboid(0.0F, -2.5F, -0.5F, 0.0F, 3.0F, 1.0F, new Dilation(0.001F))
                        .uv(143, 44).cuboid(-0.5F, -3.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.pivot(0.75F, -2.5F, -0.5F));

        ModelPartData longswitch8 = multiswitchpanel2.addChild("longswitch8",
                ModelPartBuilder.create().uv(141, 82).cuboid(0.0F, -2.5F, -0.5F, 0.0F, 3.0F, 1.0F, new Dilation(0.001F))
                        .uv(105, 133).cuboid(-0.5F, -3.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.pivot(2.25F, -2.5F, -0.5F));

        ModelPartData computernob2 = controls5.addChild("computernob2",
                ModelPartBuilder.create().uv(87, 21).cuboid(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                        .uv(86, 34).cuboid(-0.5F, -0.5F, -0.9F, 1.0F, 1.0F, 1.0F, new Dilation(-0.25F)).uv(9, 31)
                        .cuboid(-0.75F, -0.75F, -0.25F, 1.0F, 1.0F, 0.0F, new Dilation(0.001F)),
                ModelTransform.of(0.0F, -15.7F, -16.9F, 0.2618F, 0.0F, 0.0F));

        ModelPartData section6 = alnico.addChild("section6", ModelPartBuilder.create(),
                ModelTransform.of(0.0F, 12.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData desktop6 = section6.addChild("desktop6", ModelPartBuilder.create().uv(45, 21).cuboid(-9.5F,
                -13.25F, -27.65F, 19.0F, 4.0F, 3.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 2.0F));

        ModelPartData cube_r60 = desktop6.addChild("cube_r60",
                ModelPartBuilder.create().uv(133, 117).cuboid(-8.0F, -4.0F, -8.0F, 1.0F, 0.0F, 1.0F, new Dilation(0.0F))
                        .uv(134, 41).cuboid(7.0F, -4.0F, -8.0F, 1.0F, 0.0F, 1.0F, new Dilation(0.0F)).uv(134, 41)
                        .cuboid(6.0F, -4.0F, -8.0F, 1.0F, 2.0F, 6.0F, new Dilation(0.0F)).uv(138, 84)
                        .cuboid(-7.0F, -4.0F, -8.0F, 1.0F, 2.0F, 6.0F, new Dilation(0.0F)).uv(22, 45)
                        .cuboid(-6.0F, -4.0F, -8.0F, 12.0F, 2.0F, 8.0F, new Dilation(0.0F)).uv(55, 48)
                        .cuboid(-5.0F, -4.0F, 0.0F, 1.0F, 2.0F, 2.0F, new Dilation(0.0F)).uv(55, 59)
                        .cuboid(4.0F, -4.0F, 0.0F, 1.0F, 2.0F, 2.0F, new Dilation(0.0F)).uv(57, 29)
                        .cuboid(-9.0F, -4.0F, -10.0F, 18.0F, 2.0F, 2.0F, new Dilation(0.0F)).uv(119, 83)
                        .cuboid(-4.0F, -4.0F, 0.0F, 8.0F, 2.0F, 4.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, -12.0F, -14.0F, 0.2618F, 0.0F, 0.0F));

        ModelPartData pillars6 = section6.addChild("pillars6", ModelPartBuilder.create().uv(111, 91).cuboid(-6.0F,
                -19.0F, -12.0F, 12.0F, 6.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 2.0F));

        ModelPartData cube_r61 = pillars6.addChild("cube_r61",
                ModelPartBuilder.create().uv(66, 87).cuboid(-2.0F, -21.0F, 0.9F, 2.0F, 20.0F, 5.0F, new Dilation(0.0F)),
                ModelTransform.of(10.9554F, -14.2676F, -27.1301F, -1.8326F, -0.3491F, 0.0F));

        ModelPartData cube_r62 = pillars6.addChild("cube_r62",
                ModelPartBuilder.create().uv(96, 91).cuboid(-2.0F, -21.0F, 0.9F, 2.0F, 20.0F, 5.0F, new Dilation(0.0F)),
                ModelTransform.of(12.0F, -13.75F, -30.0F, -1.309F, -0.3491F, 0.0F));

        ModelPartData cube_r63 = pillars6
                .addChild("cube_r63",
                        ModelPartBuilder.create().uv(81, 91).cuboid(-1.0F, -10.0F, -2.5F, 2.0F, 20.0F, 5.0F,
                                new Dilation(0.0F)),
                        ModelTransform.of(-6.6827F, -8.1365F, -18.3146F, -1.8326F, 0.3491F, 0.0F));

        ModelPartData cube_r64 = pillars6.addChild("cube_r64",
                ModelPartBuilder.create().uv(28, 99).cuboid(0.0F, -21.0F, 0.9F, 2.0F, 20.0F, 5.0F, new Dilation(0.0F)),
                ModelTransform.of(-12.0F, -13.75F, -30.0F, -1.309F, 0.3491F, 0.0F));

        ModelPartData top6 = section6.addChild("top6", ModelPartBuilder.create(),
                ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData cube_r65 = top6.addChild("cube_r65",
                ModelPartBuilder.create().uv(90, 21).cuboid(-4.0F, -12.4F, -27.0F, 8.0F, 2.0F, 3.0F, new Dilation(0.0F))
                        .uv(148, 59).cuboid(-1.5F, -19.0F, -12.0F, 3.0F, 4.0F, 3.0F, new Dilation(0.05F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.5236F, 0.0F));

        ModelPartData cube_r66 = top6.addChild("cube_r66",
                ModelPartBuilder.create().uv(0, 0).cuboid(-4.0F, -4.0F, -28.9F, 8.0F, 2.0F, 18.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, -0.2618F, 0.5236F, 0.0F));

        ModelPartData cube_r67 = top6.addChild("cube_r67", ModelPartBuilder.create().uv(35, 0).cuboid(-3.0F, -18.0F,
                -22.0F, 6.0F, 0.0F, 17.0F, new Dilation(0.001F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.2618F, 0.5236F, 0.0F));

        ModelPartData bottom6 = section6.addChild("bottom6",
                ModelPartBuilder.create().uv(0, 91).cuboid(-7.0F, -7.0F, -15.0F, 14.0F, 7.0F, 2.0F, new Dilation(0.0F)),
                ModelTransform.pivot(0.0F, 0.0F, 2.0F));

        ModelPartData cube_r68 = bottom6.addChild("cube_r68", ModelPartBuilder.create().uv(116, 16).cuboid(-2.5F, -9.0F,
                -19.0F, 5.0F, 9.0F, 5.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, -2.0F, 0.0F, 0.5236F, 0.0F));

        ModelPartData cube_r69 = bottom6.addChild("cube_r69",
                ModelPartBuilder.create().uv(147, 77).cuboid(8.0F, -3.0F, -1.0F, 3.0F, 3.0F, 4.0F, new Dilation(0.0F))
                        .uv(147, 147).cuboid(-11.0F, -3.0F, -1.0F, 3.0F, 3.0F, 4.0F, new Dilation(0.0F)).uv(0, 21)
                        .cuboid(-8.0F, -3.0F, -1.0F, 16.0F, 3.0F, 12.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, -9.0F, -24.7F, -0.2618F, 0.0F, 0.0F));

        ModelPartData controls6 = section6.addChild("controls6", ModelPartBuilder.create(),
                ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData randomizer3 = controls6.addChild("randomizer3",
                ModelPartBuilder.create().uv(71, 124).cuboid(-1.0F, -1.5F, -0.5F, 2.0F, 3.0F, 2.0F, new Dilation(0.0F)),
                ModelTransform.of(-12.0F, -12.9F, -20.833F, 0.0F, 0.5236F, 0.0F));

        ModelPartData sideswitches3 = controls6.addChild("sideswitches3",
                ModelPartBuilder.create().uv(57, 124).cuboid(-2.0F, -1.0F, -3.0F, 3.0F, 2.0F, 7.0F, new Dilation(0.0F)),
                ModelTransform.of(-7.5F, -14.7806F, -20.0173F, 0.2472F, 0.3594F, 0.0043F));

        ModelPartData sideswitch5 = sideswitches3.addChild("sideswitch5",
                ModelPartBuilder.create().uv(137, 0).cuboid(-1.0F, -3.0F, -1.0F, 2.0F, 1.0F, 2.0F, new Dilation(0.0F))
                        .uv(128, 14).cuboid(-1.0F, -2.0F, 0.0F, 2.0F, 1.0F, 0.0F, new Dilation(0.001F)),
                ModelTransform.pivot(-0.5F, 0.0F, 2.5F));

        ModelPartData sideswitch6 = sideswitches3.addChild("sideswitch6",
                ModelPartBuilder.create().uv(128, 0).cuboid(-1.0F, -3.0F, -1.0F, 2.0F, 1.0F, 2.0F, new Dilation(0.0F))
                        .uv(118, 83).cuboid(-1.0F, -2.0F, 0.0F, 2.0F, 1.0F, 0.0F, new Dilation(0.001F)),
                ModelTransform.pivot(-0.5F, 0.0F, -1.5F));

        ModelPartData geiger5 = controls6.addChild("geiger5",
                ModelPartBuilder.create().uv(151, 0).cuboid(-1.0F, -2.0F, -1.0F, 3.0F, 3.0F, 2.0F, new Dilation(0.0F))
                        .uv(100, 141).cuboid(-1.0F, -2.0F, -1.0F, 3.0F, 3.0F, 2.0F, new Dilation(0.2F)),
                ModelTransform.pivot(-0.5F, -10.75F, -26.6F));

        ModelPartData needle5 = geiger5.addChild("needle5", ModelPartBuilder.create().uv(45, 29).cuboid(-0.25F, -2.0F,
                -0.02F, 1.0F, 2.0F, 0.0F, new Dilation(0.001F)), ModelTransform.pivot(0.5F, 0.25F, -1.0F));

        ModelPartData geiger6 = controls6.addChild("geiger6",
                ModelPartBuilder.create().uv(147, 29).cuboid(-1.0F, -2.0F, -1.0F, 3.0F, 3.0F, 2.0F, new Dilation(0.0F))
                        .uv(115, 143).cuboid(-1.0F, -2.0F, -1.0F, 3.0F, 3.0F, 2.0F, new Dilation(0.2F)),
                ModelTransform.pivot(-4.5F, -10.75F, -26.6F));

        ModelPartData needle6 = geiger6.addChild("needle6", ModelPartBuilder.create().uv(45, 21).cuboid(-0.25F, -2.0F,
                -0.02F, 1.0F, 2.0F, 0.0F, new Dilation(0.001F)), ModelTransform.pivot(0.5F, 0.25F, -1.0F));

        ModelPartData geiger7 = controls6.addChild("geiger7",
                ModelPartBuilder.create().uv(130, 143).cuboid(-1.0F, -2.0F, -1.0F, 3.0F, 3.0F, 2.0F, new Dilation(0.0F))
                        .uv(122, 31).cuboid(-1.0F, -2.0F, -1.0F, 3.0F, 3.0F, 2.0F, new Dilation(0.2F)),
                ModelTransform.pivot(3.5F, -10.75F, -26.6F));

        ModelPartData needle7 = geiger7.addChild("needle7", ModelPartBuilder.create().uv(15, 15).cuboid(-0.25F, -2.0F,
                -0.02F, 1.0F, 2.0F, 0.0F, new Dilation(0.001F)), ModelTransform.pivot(0.5F, 0.25F, -1.0F));

        ModelPartData cashregister = controls6.addChild("cashregister",
                ModelPartBuilder.create().uv(91, 27).cuboid(-4.0F, -2.0F, -6.0F, 8.0F, 2.0F, 7.0F, new Dilation(0.0F))
                        .uv(98, 119).cuboid(-4.0F, -9.0F, -2.0F, 8.0F, 7.0F, 3.0F, new Dilation(0.0F)).uv(128, 110)
                        .cuboid(-4.0F, -5.0F, -5.0F, 8.0F, 3.0F, 3.0F, new Dilation(0.0F)),
                ModelTransform.pivot(0.0F, -13.0F, -15.0F));

        ModelPartData registerlever = cashregister.addChild("registerlever",
                ModelPartBuilder.create().uv(44, 11).cuboid(0.0F, -1.0F, -1.0F, 1.0F, 2.0F, 2.0F, new Dilation(0.0F))
                        .uv(0, 0).cuboid(0.5F, -3.0F, -0.5F, 0.0F, 2.0F, 1.0F, new Dilation(0.001F)).uv(108, 0)
                        .cuboid(0.0F, -5.0F, -0.5F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.pivot(4.0F, -3.5F, -3.5F));

        ModelPartData registerswitches = cashregister.addChild("registerswitches",
                ModelPartBuilder.create().uv(75, 113).cuboid(0.0F, -2.0F, -1.0F, 0.0F, 3.0F, 2.0F, new Dilation(0.001F))
                        .uv(124, 100).cuboid(-1.0F, -2.5F, -1.0F, 2.0F, 1.0F, 2.0F, new Dilation(0.0F)),
                ModelTransform.pivot(-2.75F, -4.5F, -4.0F));

        ModelPartData registerswitches2 = cashregister.addChild("registerswitches2",
                ModelPartBuilder.create().uv(46, 67).cuboid(0.0F, -2.0F, -1.0F, 0.0F, 3.0F, 2.0F, new Dilation(0.001F))
                        .uv(111, 83).cuboid(-1.0F, -2.5F, -1.0F, 2.0F, 1.0F, 2.0F, new Dilation(0.0F)),
                ModelTransform.pivot(-0.25F, -4.5F, -4.0F));

        ModelPartData registerswitches3 = cashregister.addChild("registerswitches3",
                ModelPartBuilder.create().uv(13, 4).cuboid(0.0F, -2.0F, -1.0F, 0.0F, 3.0F, 2.0F, new Dilation(0.001F))
                        .uv(110, 37).cuboid(-1.0F, -2.5F, -1.0F, 2.0F, 1.0F, 2.0F, new Dilation(0.0F)),
                ModelTransform.pivot(2.25F, -4.5F, -4.0F));

        ModelPartData column = alnico.addChild("column",
                ModelPartBuilder.create().uv(89, 52).cuboid(-4.0F, -72.0F, -6.93F, 8.0F, 5.0F, 7.0F, new Dilation(0.0F))
                        .uv(130, 133).cuboid(-4.0F, -80.0F, -7.93F, 8.0F, 8.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.pivot(0.0F, 12.0F, 0.0F));

        ModelPartData cube_r70 = column.addChild("cube_r70", ModelPartBuilder.create().uv(139, 59).cuboid(-1.0F, -80.0F,
                -10.0F, 2.0F, 12.0F, 2.0F, new Dilation(0.01F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.5708F, 0.0F));

        ModelPartData cube_r71 = column.addChild("cube_r71",
                ModelPartBuilder.create().uv(130, 133)
                        .cuboid(-4.0F, -31.0F, -7.93F, 8.0F, 8.0F, 1.0F, new Dilation(0.0F)).uv(89, 52)
                        .cuboid(-4.0F, -23.0F, -6.93F, 8.0F, 5.0F, 7.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, -49.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData cube_r72 = column.addChild("cube_r72",
                ModelPartBuilder.create().uv(22, 67).cuboid(-4.0F, 0.0F, -6.93F, 8.0F, 11.0F, 7.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, -26.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData cube_r73 = column.addChild("cube_r73", ModelPartBuilder.create().uv(17, 37).cuboid(0.0F, -32.0F,
                -9.0F, 0.0F, 46.0F, 2.0F, new Dilation(0.001F)),
                ModelTransform.of(0.0F, -36.0F, 0.0F, 0.0F, 1.5708F, 0.0F));

        ModelPartData cube_r74 = column.addChild("cube_r74",
                ModelPartBuilder.create().uv(0, 0).cuboid(-1.0F, -37.0F, -11.0F, 2.0F, 10.0F, 4.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 12.0F, 0.0F, 0.0F, 1.5708F, 0.0F));

        ModelPartData cube_r75 = column
                .addChild(
                        "cube_r75", ModelPartBuilder.create().uv(139, 59).cuboid(-1.0F, -80.0F, -10.0F, 2.0F, 12.0F,
                                2.0F, new Dilation(0.01F)),
                        ModelTransform.of(0.0F, 0.0F, 0.0F, 3.1416F, 0.5236F, -3.1416F));

        ModelPartData cube_r76 = column.addChild("cube_r76",
                ModelPartBuilder.create().uv(130, 133)
                        .cuboid(-4.0F, -31.0F, -7.93F, 8.0F, 8.0F, 1.0F, new Dilation(0.0F)).uv(89, 52)
                        .cuboid(-4.0F, -23.0F, -6.93F, 8.0F, 5.0F, 7.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, -49.0F, 0.0F, -3.1416F, 1.0472F, 3.1416F));

        ModelPartData cube_r77 = column.addChild("cube_r77",
                ModelPartBuilder.create().uv(22, 67).cuboid(-4.0F, 0.0F, -6.93F, 8.0F, 11.0F, 7.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, -26.0F, 0.0F, -3.1416F, 1.0472F, 3.1416F));

        ModelPartData cube_r78 = column
                .addChild("cube_r78",
                        ModelPartBuilder.create().uv(17, 37).cuboid(0.0F, -32.0F, -9.0F, 0.0F, 46.0F, 2.0F,
                                new Dilation(0.001F)),
                        ModelTransform.of(0.0F, -36.0F, 0.0F, 3.1416F, 0.5236F, -3.1416F));

        ModelPartData cube_r79 = column.addChild("cube_r79",
                ModelPartBuilder.create().uv(0, 0).cuboid(-1.0F, -37.0F, -11.0F, 2.0F, 10.0F, 4.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 12.0F, 0.0F, -3.1416F, 0.5236F, 3.1416F));

        ModelPartData cube_r80 = column
                .addChild(
                        "cube_r80", ModelPartBuilder.create().uv(139, 59).cuboid(-1.0F, -80.0F, -10.0F, 2.0F, 12.0F,
                                2.0F, new Dilation(0.01F)),
                        ModelTransform.of(0.0F, 0.0F, 0.0F, 3.1416F, -0.5236F, -3.1416F));

        ModelPartData cube_r81 = column.addChild("cube_r81",
                ModelPartBuilder.create().uv(130, 133)
                        .cuboid(-4.0F, -31.0F, -7.93F, 8.0F, 8.0F, 1.0F, new Dilation(0.0F)).uv(89, 52)
                        .cuboid(-4.0F, -23.0F, -6.93F, 8.0F, 5.0F, 7.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, -49.0F, 0.0F, -3.1416F, 0.0F, 3.1416F));

        ModelPartData cube_r82 = column.addChild("cube_r82",
                ModelPartBuilder.create().uv(22, 67).cuboid(-4.0F, 0.0F, -6.93F, 8.0F, 11.0F, 7.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, -26.0F, 0.0F, -3.1416F, 0.0F, 3.1416F));

        ModelPartData cube_r83 = column
                .addChild("cube_r83",
                        ModelPartBuilder.create().uv(17, 37).cuboid(0.0F, -32.0F, -9.0F, 0.0F, 46.0F, 2.0F,
                                new Dilation(0.001F)),
                        ModelTransform.of(0.0F, -36.0F, 0.0F, 3.1416F, -0.5236F, -3.1416F));

        ModelPartData cube_r84 = column.addChild("cube_r84",
                ModelPartBuilder.create().uv(0, 0).cuboid(-1.0F, -37.0F, -11.0F, 2.0F, 10.0F, 4.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 12.0F, 0.0F, -3.1416F, -0.5236F, 3.1416F));

        ModelPartData cube_r85 = column.addChild("cube_r85", ModelPartBuilder.create().uv(139, 59).cuboid(-1.0F, -80.0F,
                -10.0F, 2.0F, 12.0F, 2.0F, new Dilation(0.01F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.5708F, 0.0F));

        ModelPartData cube_r86 = column.addChild("cube_r86",
                ModelPartBuilder.create().uv(130, 133)
                        .cuboid(-4.0F, -31.0F, -7.93F, 8.0F, 8.0F, 1.0F, new Dilation(0.0F)).uv(89, 52)
                        .cuboid(-4.0F, -23.0F, -6.93F, 8.0F, 5.0F, 7.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, -49.0F, 0.0F, -3.1416F, -1.0472F, 3.1416F));

        ModelPartData cube_r87 = column.addChild("cube_r87",
                ModelPartBuilder.create().uv(22, 67).cuboid(-4.0F, 0.0F, -6.93F, 8.0F, 11.0F, 7.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, -26.0F, 0.0F, -3.1416F, -1.0472F, 3.1416F));

        ModelPartData cube_r88 = column.addChild("cube_r88", ModelPartBuilder.create().uv(17, 37).cuboid(0.0F, -32.0F,
                -9.0F, 0.0F, 46.0F, 2.0F, new Dilation(0.001F)),
                ModelTransform.of(0.0F, -36.0F, 0.0F, 0.0F, -1.5708F, 0.0F));

        ModelPartData cube_r89 = column.addChild("cube_r89",
                ModelPartBuilder.create().uv(0, 0).cuboid(-1.0F, -37.0F, -11.0F, 2.0F, 10.0F, 4.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 12.0F, 0.0F, 0.0F, -1.5708F, 0.0F));

        ModelPartData cube_r90 = column.addChild("cube_r90", ModelPartBuilder.create().uv(139, 59).cuboid(-1.0F, -80.0F,
                -10.0F, 2.0F, 12.0F, 2.0F, new Dilation(0.01F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -0.5236F, 0.0F));

        ModelPartData cube_r91 = column.addChild("cube_r91",
                ModelPartBuilder.create().uv(130, 133)
                        .cuboid(-4.0F, -31.0F, -7.93F, 8.0F, 8.0F, 1.0F, new Dilation(0.0F)).uv(89, 52)
                        .cuboid(-4.0F, -23.0F, -6.93F, 8.0F, 5.0F, 7.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, -49.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData cube_r92 = column.addChild("cube_r92",
                ModelPartBuilder.create().uv(22, 67).cuboid(-4.0F, 0.0F, -6.93F, 8.0F, 11.0F, 7.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, -26.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData cube_r93 = column.addChild("cube_r93", ModelPartBuilder.create().uv(17, 37).cuboid(0.0F, -32.0F,
                -9.0F, 0.0F, 46.0F, 2.0F, new Dilation(0.001F)),
                ModelTransform.of(0.0F, -36.0F, 0.0F, 0.0F, -0.5236F, 0.0F));

        ModelPartData cube_r94 = column.addChild("cube_r94",
                ModelPartBuilder.create().uv(0, 0).cuboid(-1.0F, -37.0F, -11.0F, 2.0F, 10.0F, 4.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 12.0F, 0.0F, 0.0F, -0.5236F, 0.0F));

        ModelPartData cube_r95 = column.addChild("cube_r95", ModelPartBuilder.create().uv(17, 37).cuboid(0.0F, -32.0F,
                -9.0F, 0.0F, 46.0F, 2.0F, new Dilation(0.001F)),
                ModelTransform.of(0.0F, -36.0F, 0.0F, 0.0F, 0.5236F, 0.0F));

        ModelPartData cube_r96 = column.addChild("cube_r96",
                ModelPartBuilder.create().uv(0, 0).cuboid(-1.0F, -37.0F, -11.0F, 2.0F, 10.0F, 4.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 12.0F, 0.0F, 0.0F, 0.5236F, 0.0F));

        ModelPartData cube_r97 = column.addChild("cube_r97", ModelPartBuilder.create().uv(139, 59).cuboid(-1.0F, -80.0F,
                -10.0F, 2.0F, 12.0F, 2.0F, new Dilation(0.01F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.5236F, 0.0F));

        ModelPartData cube_r98 = column.addChild("cube_r98",
                ModelPartBuilder.create().uv(22, 67).cuboid(-4.0F, 0.0F, -6.93F, 8.0F, 11.0F, 7.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, -26.0F, 0.0F, 0.0F, 0.0F, 0.0F));

        ModelPartData glass = column.addChild("glass", ModelPartBuilder.create().uv(0, 37).cuboid(-4.0F, -67.0F, -6.93F,
                8.0F, 41.0F, 0.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData cube_r99 = glass.addChild("cube_r99", ModelPartBuilder.create().uv(0, 37).cuboid(-4.0F, -67.0F,
                -6.93F, 8.0F, 41.0F, 0.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData cube_r100 = glass
                .addChild(
                        "cube_r100", ModelPartBuilder.create().uv(0, 37).cuboid(-4.0F, -67.0F, -6.93F, 8.0F, 41.0F,
                                0.0F, new Dilation(0.0F)),
                        ModelTransform.of(0.0F, 0.0F, 0.0F, -3.1416F, 1.0472F, 3.1416F));

        ModelPartData cube_r101 = glass
                .addChild(
                        "cube_r101", ModelPartBuilder.create().uv(0, 37).cuboid(-4.0F, -67.0F, -6.93F, 8.0F, 41.0F,
                                0.0F, new Dilation(0.0F)),
                        ModelTransform.of(0.0F, 0.0F, 0.0F, -3.1416F, 0.0F, 3.1416F));

        ModelPartData cube_r102 = glass
                .addChild(
                        "cube_r102", ModelPartBuilder.create().uv(0, 37).cuboid(-4.0F, -67.0F, -6.93F, 8.0F, 41.0F,
                                0.0F, new Dilation(0.0F)),
                        ModelTransform.of(0.0F, 0.0F, 0.0F, -3.1416F, -1.0472F, 3.1416F));

        ModelPartData cube_r103 = glass.addChild("cube_r103", ModelPartBuilder.create().uv(0, 37).cuboid(-4.0F, -67.0F,
                -6.93F, 8.0F, 41.0F, 0.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData timerotor = column.addChild("timerotor", ModelPartBuilder.create(),
                ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData bottomgizmo = timerotor.addChild("bottomgizmo",
                ModelPartBuilder.create().uv(111, 100)
                        .cuboid(-2.0F, -14.0F, -2.0F, 4.0F, 14.0F, 4.0F, new Dilation(0.0F)).uv(33, 87)
                        .cuboid(-4.0F, -4.0F, -4.0F, 8.0F, 3.0F, 8.0F, new Dilation(0.0F)).uv(106, 0)
                        .cuboid(-3.5F, -8.0F, -3.5F, 7.0F, 3.0F, 7.0F, new Dilation(0.0F)).uv(115, 37)
                        .cuboid(-3.0F, -12.0F, -3.0F, 6.0F, 3.0F, 6.0F, new Dilation(0.0F)).uv(0, 134)
                        .cuboid(-6.0F, -16.0F, 0.0F, 4.0F, 16.0F, 0.0F, new Dilation(0.001F)).uv(19, 101)
                        .cuboid(2.0F, -16.0F, 0.0F, 4.0F, 16.0F, 0.0F, new Dilation(0.001F)).uv(82, 136)
                        .cuboid(-1.0F, -19.0F, -1.0F, 2.0F, 12.0F, 2.0F, new Dilation(0.0F)),
                ModelTransform.pivot(0.0F, -26.0F, 0.0F));

        ModelPartData cube_r104 = bottomgizmo.addChild("cube_r104",
                ModelPartBuilder.create().uv(46, 131)
                        .cuboid(2.0F, -42.0F, 0.0F, 4.0F, 16.0F, 0.0F, new Dilation(0.001F)).uv(35, 0)
                        .cuboid(-6.0F, -42.0F, 0.0F, 4.0F, 16.0F, 0.0F, new Dilation(0.001F)),
                ModelTransform.of(0.0F, 26.0F, 0.0F, 0.0F, -1.5708F, 0.0F));

        ModelPartData topgizmo = timerotor.addChild("topgizmo",
                ModelPartBuilder.create().uv(81, 117)
                        .cuboid(-2.0F, -14.0F, -2.0F, 4.0F, 14.0F, 4.0F, new Dilation(0.0F)).uv(86, 79)
                        .cuboid(-4.0F, -4.0F, -4.0F, 8.0F, 3.0F, 8.0F, new Dilation(0.0F)).uv(93, 41)
                        .cuboid(-3.5F, -8.0F, -3.5F, 7.0F, 3.0F, 7.0F, new Dilation(0.0F)).uv(114, 59)
                        .cuboid(-3.0F, -12.0F, -3.0F, 6.0F, 3.0F, 6.0F, new Dilation(0.0F)).uv(55, 134)
                        .cuboid(-6.0F, -16.0F, 0.0F, 4.0F, 16.0F, 0.0F, new Dilation(0.001F)).uv(64, 134)
                        .cuboid(2.0F, -16.0F, 0.0F, 4.0F, 16.0F, 0.0F, new Dilation(0.001F)).uv(73, 134)
                        .cuboid(-1.0F, -19.0F, -1.0F, 2.0F, 12.0F, 2.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, -67.0F, 0.0F, 3.1416F, 0.0F, 0.0F));

        ModelPartData cube_r105 = topgizmo.addChild("cube_r105",
                ModelPartBuilder.create().uv(9, 134).cuboid(2.0F, -42.0F, 0.0F, 4.0F, 16.0F, 0.0F, new Dilation(0.001F))
                        .uv(18, 135).cuboid(-6.0F, -42.0F, 0.0F, 4.0F, 16.0F, 0.0F, new Dilation(0.001F)),
                ModelTransform.of(0.0F, 26.0F, 0.0F, 0.0F, -1.5708F, 0.0F));

        ModelPartData holographicmonitor = alnico.addChild("holographicmonitor", ModelPartBuilder.create(),
                ModelTransform.pivot(0.0F, 12.0F, 0.0F));

        ModelPartData monitor = holographicmonitor.addChild("monitor",
                ModelPartBuilder.create().uv(57, 34)
                        .cuboid(-6.5F, 0.0F, -13.25F, 13.0F, 0.0F, 1.0F, new Dilation(0.001F)).uv(79, 75)
                        .cuboid(-5.5F, -0.5F, -15.25F, 11.0F, 1.0F, 2.0F, new Dilation(0.0F)),
                ModelTransform.pivot(0.0F, -34.0F, 0.0F));

        ModelPartData monitorhandle = monitor.addChild("monitorhandle",
                ModelPartBuilder.create().uv(116, 152)
                        .cuboid(-0.975F, -1.0F, -1.0335F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F)).uv(43, 104)
                        .cuboid(-0.975F, -0.5F, -6.0335F, 2.0F, 1.0F, 2.0F, new Dilation(0.0F)).uv(45, 29)
                        .cuboid(-0.975F, 0.0F, -4.0335F, 2.0F, 0.0F, 3.0F, new Dilation(0.001F)),
                ModelTransform.of(-4.5F, 0.0F, -7.75F, 0.0F, 0.5236F, 0.0F));

        ModelPartData monitor2 = holographicmonitor.addChild("monitor2",
                ModelPartBuilder.create().uv(57, 34)
                        .cuboid(-6.5F, 0.0F, -13.25F, 13.0F, 0.0F, 1.0F, new Dilation(0.001F)).uv(79, 75)
                        .cuboid(-5.5F, -0.5F, -15.25F, 11.0F, 1.0F, 2.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, -34.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData monitorhandle2 = monitor2.addChild("monitorhandle2",
                ModelPartBuilder.create().uv(116, 152)
                        .cuboid(-0.975F, -1.0F, -1.0335F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F)).uv(43, 104)
                        .cuboid(-0.975F, -0.5F, -6.0335F, 2.0F, 1.0F, 2.0F, new Dilation(0.0F)).uv(45, 29)
                        .cuboid(-0.975F, 0.0F, -4.0335F, 2.0F, 0.0F, 3.0F, new Dilation(0.001F)),
                ModelTransform.of(-4.5F, 0.0F, -7.75F, 0.0F, 0.5236F, 0.0F));

        ModelPartData monitor3 = holographicmonitor.addChild("monitor3",
                ModelPartBuilder.create().uv(57, 34)
                        .cuboid(-6.5F, 0.0F, -13.25F, 13.0F, 0.0F, 1.0F, new Dilation(0.001F)).uv(79, 75)
                        .cuboid(-5.5F, -0.5F, -15.25F, 11.0F, 1.0F, 2.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, -34.0F, 0.0F, 0.0F, -2.0944F, 0.0F));

        ModelPartData monitorhandle3 = monitor3.addChild("monitorhandle3",
                ModelPartBuilder.create().uv(116, 152)
                        .cuboid(-0.975F, -1.0F, -1.0335F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F)).uv(43, 104)
                        .cuboid(-0.975F, -0.5F, -6.0335F, 2.0F, 1.0F, 2.0F, new Dilation(0.0F)).uv(45, 29)
                        .cuboid(-0.975F, 0.0F, -4.0335F, 2.0F, 0.0F, 3.0F, new Dilation(0.001F)),
                ModelTransform.of(-4.5F, 0.0F, -7.75F, 0.0F, 0.5236F, 0.0F));

        ModelPartData monitor4 = holographicmonitor.addChild("monitor4",
                ModelPartBuilder.create().uv(57, 34)
                        .cuboid(-6.5F, 0.0F, -13.25F, 13.0F, 0.0F, 1.0F, new Dilation(0.001F)).uv(79, 75)
                        .cuboid(-5.5F, -0.5F, -15.25F, 11.0F, 1.0F, 2.0F, new Dilation(0.0F)).uv(111, 133)
                        .cuboid(-4.5F, 0.5F, -14.25F, 9.0F, 9.0F, 0.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, -34.0F, 0.0F, 0.0F, 3.1416F, 0.0F));

        ModelPartData monitorhandle4 = monitor4.addChild("monitorhandle4",
                ModelPartBuilder.create().uv(116, 152)
                        .cuboid(-0.975F, -1.0F, -1.0335F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F)).uv(43, 104)
                        .cuboid(-0.975F, -0.5F, -6.0335F, 2.0F, 1.0F, 2.0F, new Dilation(0.0F)).uv(45, 29)
                        .cuboid(-0.975F, 0.0F, -4.0335F, 2.0F, 0.0F, 3.0F, new Dilation(0.001F)),
                ModelTransform.of(-4.5F, 0.0F, -7.75F, 0.0F, 0.5236F, 0.0F));

        ModelPartData monitor5 = holographicmonitor.addChild("monitor5",
                ModelPartBuilder.create().uv(57, 34)
                        .cuboid(-6.5F, 0.0F, -13.25F, 13.0F, 0.0F, 1.0F, new Dilation(0.001F)).uv(79, 75)
                        .cuboid(-5.5F, -0.5F, -15.25F, 11.0F, 1.0F, 2.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, -34.0F, 0.0F, 0.0F, 2.0944F, 0.0F));

        ModelPartData monitorhandle5 = monitor5.addChild("monitorhandle5",
                ModelPartBuilder.create().uv(116, 152)
                        .cuboid(-0.975F, -1.0F, -1.0335F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F)).uv(43, 104)
                        .cuboid(-0.975F, -0.5F, -6.0335F, 2.0F, 1.0F, 2.0F, new Dilation(0.0F)).uv(45, 29)
                        .cuboid(-0.975F, 0.0F, -4.0335F, 2.0F, 0.0F, 3.0F, new Dilation(0.001F)),
                ModelTransform.of(-4.5F, 0.0F, -7.75F, 0.0F, 0.5236F, 0.0F));

        ModelPartData monitor6 = holographicmonitor.addChild("monitor6",
                ModelPartBuilder.create().uv(57, 34)
                        .cuboid(-6.5F, 0.0F, -13.25F, 13.0F, 0.0F, 1.0F, new Dilation(0.001F)).uv(79, 75)
                        .cuboid(-5.5F, -0.5F, -15.25F, 11.0F, 1.0F, 2.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, -34.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData monitorhandle6 = monitor6.addChild("monitorhandle6",
                ModelPartBuilder.create().uv(116, 152)
                        .cuboid(-0.975F, -1.0F, -1.0335F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F)).uv(43, 104)
                        .cuboid(-0.975F, -0.5F, -6.0335F, 2.0F, 1.0F, 2.0F, new Dilation(0.0F)).uv(45, 29)
                        .cuboid(-0.975F, 0.0F, -4.0335F, 2.0F, 0.0F, 3.0F, new Dilation(0.001F)),
                ModelTransform.of(-4.5F, 0.0F, -7.75F, 0.0F, 0.5236F, 0.0F));
        return TexturedModelData.of(modelData, 256, 256);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red,
            float green, float blue, float alpha) {
        alnico.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
    }

    @Override
    public void renderWithAnimations(ConsoleBlockEntity console, ClientTardis tardis, ModelPart root, MatrixStack matrices,
                                     VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float pAlpha) {
        matrices.push();
        matrices.translate(0.5f, -1.5f, -0.5f);

        ModelPart throttle = alnico.getChild("section1").getChild("controls").getChild("fliplever1").getChild("bone5");
        throttle.pitch = throttle.pitch + ((tardis.travel().speed() / (float) tardis.travel().maxSpeed().get()) * 1.5f);

        ModelPart handbrake = alnico.getChild("section1").getChild("controls").getChild("biglever").getChild("bone");
        handbrake.pitch = !tardis.travel().handbrake() ? handbrake.pitch - 0.9f : handbrake.pitch + 0.9f;

        ModelPart power = alnico.getChild("section4").getChild("controls4").getChild("biglever2").getChild("bone12");
        power.pitch = !tardis.fuel().hasPower() ? power.pitch - 0.9f : power.pitch + 0.9f;

        ModelPart autoPilot = alnico.getChild("section1").getChild("controls").getChild("multiswitchpanel")
                .getChild("longswitch1");
        autoPilot.pitch = tardis.travel().autopilot() ? autoPilot.pitch + 0.5f : autoPilot.pitch;

        ModelPart security = alnico.getChild("section1").getChild("controls").getChild("multiswitchpanel")
                .getChild("longswitch4");
        security.pitch = tardis.stats().security().get() ? security.pitch + 0.5f : security.pitch;

        ModelPart siegeMode = alnico.getChild("section3").getChild("controls3").getChild("siegemode").getChild("lever");
        siegeMode.pitch = tardis.siege().isActive() ? siegeMode.pitch + 0.9f : siegeMode.pitch;

        ModelPart refueler = alnico.getChild("section5").getChild("controls5").getChild("refueler").getChild("gasknob");
        refueler.yaw = !tardis.isRefueling() ? refueler.yaw - 0.7854f : refueler.yaw;

        ModelPart fuelGauge = alnico.getChild("section3").getChild("controls3").getChild("geiger").getChild("needle");
        fuelGauge.roll = (float) (((tardis.getFuel() / FuelHandler.TARDIS_MAX_FUEL) * 2) - 1);

        ModelPart increment = alnico.getChild("section5").getChild("controls5").getChild("multiswitchpanel2")
                .getChild("longswitch5");
        increment.pitch = IncrementManager.increment(tardis) >= 10
                ? IncrementManager.increment(tardis) >= 100
                        ? IncrementManager.increment(tardis) >= 1000
                                ? IncrementManager.increment(tardis) >= 10000
                                        ? increment.pitch + 1.5f
                                        : increment.pitch + 1.25f
                                : increment.pitch + 1f
                        : increment.pitch + 0.5f
                : increment.pitch;

        ModelPart shield = alnico.getChild("section5").getChild("controls5").getChild("multiswitchpanel2")
                .getChild("longswitch8");
        shield.pitch = tardis.shields().shielded().get()
                ? shield.pitch + 1f
                : shield.pitch;

        ModelPart landtype = alnico.getChild("section1").getChild("controls").getChild("tinyswitch2").getChild("bone2");
        landtype.yaw = landtype.yaw + ((tardis.travel().horizontalSearch().get() ? 1.5708f : 0)); // FIXME use
                                                                                                    // TravelHandler#horizontalSearch/#verticalSearch

        ModelPart antigravs = alnico.getChild("section1").getChild("controls").getChild("tinyswitch").getChild("bone3");
        antigravs.yaw = antigravs.yaw + (tardis.travel().antigravs().get() ? 1.5708f : 0);

        ModelPart doorControl = alnico.getChild("section5").getChild("controls5").getChild("tinyswitch6")
                .getChild("bone24");
        doorControl.yaw = doorControl.yaw
                + (tardis.door().isOpen() ? tardis.door().isRightOpen() ? 1.5708f * 2f : 1.5708f : 0);

        ModelPart doorLock = alnico.getChild("section5").getChild("controls5").getChild("tinyswitch7")
                .getChild("bone25");
        doorLock.yaw = doorLock.yaw + (tardis.door().locked() ? 1.5708f : 0);

        ModelPart toast1 = alnico.getChild("section2").getChild("controls2").getChild("waypointcatridge")
                .getChild("toast1");
        ModelPart toastlever = alnico.getChild("section2").getChild("controls2").getChild("waypointcatridge")
                .getChild("toastlever");
        ModelPart toast2 = alnico.getChild("section2").getChild("controls2").getChild("waypointcatridge")
                .getChild("toast2");
        toast1.visible = tardis.waypoint().hasCartridge();
        toastlever.pivotY = toastlever.pivotY + (!tardis.waypoint().hasCartridge() ? 2f : 0);
        toast2.visible = tardis.waypoint().hasCartridge();

        ModelPart direction = alnico.getChild("section5").getChild("controls5").getChild("tinyswitch9")
                .getChild("bone26");
        direction.yaw += (1.5708f * tardis.travel().destination().getRotation());

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
        matrices.translate(1.86, 0.47, 0.30);
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180f));
        matrices.scale(0.004f, 0.004f, 0.004f);
        matrices.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(60f));
        matrices.translate(-240f, -228, 5f);
        String positionPosText = " " + abppPos.getX() + ", " + abppPos.getY() + ", " + abppPos.getZ();
        Text positionDimensionText = WorldUtil.worldText(abpp.getDimension());
        String positionDirectionText = " " + DirectionControl.rotationToDirection(abpp.getRotation()).toUpperCase();
        String destinationPosText = " " + abpdPos.getX() + ", " + abpdPos.getY() + ", " + abpdPos.getZ();
        Text destinationDimensionText = WorldUtil.worldText(abpd.getDimension(), false);
        String destinationDirectionText = " " + DirectionControl.rotationToDirection(abpd.getRotation()).toUpperCase();
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
        matrices.translate(0.41, 1.40, 0.38);
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(150f));
        matrices.scale(0.015f, 0.015f, 0.015f);
        matrices.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(180f));
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-30.5f));
        String progressText = tardis.travel().getState() == TravelHandlerBase.State.LANDED
                ? "0"
                : tardis.travel().getDurationAsPercentage() + " ";
        matrices.translate(0, -38, -52);
        renderer.drawWithOutline(Text.of(progressText).asOrderedText(), 0 - renderer.getWidth(progressText) / 2, 0, 0xffffff, 0x03cffc,
                matrices.peek().getPositionMatrix(), vertexConsumers, 0xF000F0);
        matrices.pop();
    }

    @Override
    public ModelPart getPart() {
        return alnico;
    }

    @Override
    public Animation getAnimationForState(TravelHandlerBase.State state) {
        return switch (state) {
            case FLIGHT, MAT, DEMAT -> AlnicoAnimations.CONSOLE_ALNICO_FLIGHT;
            case LANDED -> AlnicoAnimations.CONSOLE_ALNICO_IDLE;
            default -> Animation.Builder.create(0).build();
        };
    }
}
