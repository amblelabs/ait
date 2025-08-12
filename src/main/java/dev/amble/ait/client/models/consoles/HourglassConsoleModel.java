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
import dev.amble.ait.core.tardis.handler.travel.TravelHandler;
import dev.amble.ait.core.tardis.handler.travel.TravelHandlerBase;
import dev.amble.ait.core.util.WorldUtil;

public class HourglassConsoleModel extends SimpleConsoleModel {

    private final ModelPart console;

    public HourglassConsoleModel(ModelPart root) {

        this.console = root.getChild("Root");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData Root = modelPartData.addChild("Root", ModelPartBuilder.create().uv(1, 73).cuboid(-9.0F, -87.975F, -9.0F, 18.0F, 0.0F, 18.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 24.0F, 0.0F));

        ModelPartData ConsoleCondom_r1 = Root.addChild("ConsoleCondom_r1", ModelPartBuilder.create().uv(1, 73).cuboid(-9.0F, 0.0F, -9.0F, 18.0F, 0.0F, 18.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -0.25F, 0.0F, 3.1416F, 0.0F, 0.0F));

        ModelPartData Rotor = Root.addChild("Rotor", ModelPartBuilder.create().uv(30, 241).cuboid(-4.0F, -59.0F, -7.0F, 8.0F, 0.0F, 14.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -16.0F, 0.0F));

        ModelPartData cube_r1 = Rotor.addChild("cube_r1", ModelPartBuilder.create().uv(30, 241).cuboid(-4.0F, -1.0F, -7.0F, 8.0F, 0.0F, 14.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -58.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData cube_r2 = Rotor.addChild("cube_r2", ModelPartBuilder.create().uv(30, 241).cuboid(-4.0F, -1.0F, -7.0F, 8.0F, 0.0F, 14.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -58.0F, 0.0F, 0.0F, -2.0944F, 0.0F));

        ModelPartData RotorGlass = Rotor.addChild("RotorGlass", ModelPartBuilder.create().uv(113, 154).cuboid(-4.55F, -23.5F, 7.8F, 9.05F, 47.0F, 0.0F, new Dilation(0.0F))
                .uv(107, 189).cuboid(8.9699F, -24.0F, 0.0043F, 2.0F, 48.0F, 0.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -35.5F, 0.0F));

        ModelPartData GlassFlangeF_r1 = RotorGlass.addChild("GlassFlangeF_r1", ModelPartBuilder.create().uv(107, 189).cuboid(8.9699F, -24.5F, 0.0043F, 2.0F, 48.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.5F, 0.0F, 0.0F, -2.0944F, 0.0F));

        ModelPartData GlassFlangeE_r1 = RotorGlass.addChild("GlassFlangeE_r1", ModelPartBuilder.create().uv(107, 189).cuboid(8.9699F, -24.5F, 0.0043F, 2.0F, 48.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.5F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData GlassFlangeC_r1 = RotorGlass.addChild("GlassFlangeC_r1", ModelPartBuilder.create().uv(107, 189).cuboid(8.9699F, -24.5F, 0.0043F, 2.0F, 48.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.5F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData GlassFlangeB_r1 = RotorGlass.addChild("GlassFlangeB_r1", ModelPartBuilder.create().uv(107, 189).cuboid(8.9699F, -24.5F, 0.0043F, 2.0F, 48.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.5F, 0.0F, 0.0F, 2.0944F, 0.0F));

        ModelPartData GlassFlangeA_r1 = RotorGlass.addChild("GlassFlangeA_r1", ModelPartBuilder.create().uv(107, 189).cuboid(8.9699F, -24.5F, 0.0043F, 2.0F, 48.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.5F, 0.0F, 0.0F, 3.1416F, 0.0F));

        ModelPartData RotorGlassF_r1 = RotorGlass.addChild("RotorGlassF_r1", ModelPartBuilder.create().uv(113, 154).cuboid(-4.525F, -23.5F, 7.8F, 9.05F, 47.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(-0.025F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData RotorGlassE_r1 = RotorGlass.addChild("RotorGlassE_r1", ModelPartBuilder.create().uv(113, 154).cuboid(-4.525F, -23.5F, 7.8F, 9.05F, 47.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(-0.025F, 0.0F, 0.0F, 0.0F, 2.0944F, 0.0F));

        ModelPartData RotorGlassD_r1 = RotorGlass.addChild("RotorGlassD_r1", ModelPartBuilder.create().uv(113, 154).cuboid(-4.525F, -23.5F, 7.8F, 9.05F, 47.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(-0.025F, 0.0F, 0.0F, 0.0F, 3.1416F, 0.0F));

        ModelPartData RotorGlassC_r1 = RotorGlass.addChild("RotorGlassC_r1", ModelPartBuilder.create().uv(113, 154).cuboid(-4.525F, -23.5F, 7.8F, 9.05F, 47.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(-0.025F, 0.0F, 0.0F, 0.0F, -2.0944F, 0.0F));

        ModelPartData RotorGlassB_r1 = RotorGlass.addChild("RotorGlassB_r1", ModelPartBuilder.create().uv(113, 154).cuboid(-4.525F, -23.5F, 7.8F, 9.05F, 47.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(-0.025F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData RotorBaseLow = Rotor.addChild("RotorBaseLow", ModelPartBuilder.create().uv(83, 236).cuboid(-5.5F, -3.0F, -9.5F, 11.0F, 7.0F, 0.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -6.0F, 0.0F));

        ModelPartData VEntF_r1 = RotorBaseLow.addChild("VEntF_r1", ModelPartBuilder.create().uv(83, 236).cuboid(-5.5F, -3.0F, -9.5F, 11.0F, 7.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, -3.1416F, -1.0472F, 3.1416F));

        ModelPartData VentE_r1 = RotorBaseLow.addChild("VentE_r1", ModelPartBuilder.create().uv(136, 238).cuboid(-5.5F, -3.0F, -9.5F, 11.0F, 7.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData VentB_r1 = RotorBaseLow.addChild("VentB_r1", ModelPartBuilder.create().uv(0, 238).cuboid(-5.5F, -3.0F, -9.5F, 11.0F, 7.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 0.0F, 0.0F, -3.1416F, 1.0472F, 3.1416F));

        ModelPartData VentC_r1 = RotorBaseLow.addChild("VentC_r1", ModelPartBuilder.create().uv(136, 238).cuboid(-5.5F, -3.0F, -9.5F, 11.0F, 7.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData RotorBaseHigh = Rotor.addChild("RotorBaseHigh", ModelPartBuilder.create().uv(26, 171).cuboid(-5.75F, -4.0F, -9.95F, 11.5F, 4.0F, 10.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -8.0F, 0.0F));

        ModelPartData RotorBottom_high5_r1 = RotorBaseHigh.addChild("RotorBottom_high5_r1", ModelPartBuilder.create().uv(164, 0).cuboid(-5.75F, -4.0F, -9.95F, 11.5F, 4.0F, 10.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, -3.1416F, -1.0472F, 3.1416F));

        ModelPartData RotorBottom_high4_r1 = RotorBaseHigh.addChild("RotorBottom_high4_r1", ModelPartBuilder.create().uv(164, 89).cuboid(-5.75F, -4.0F, -9.95F, 11.5F, 4.0F, 10.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, -3.1416F, 0.0F, 3.1416F));

        ModelPartData RotorBottom_high3_r1 = RotorBaseHigh.addChild("RotorBottom_high3_r1", ModelPartBuilder.create().uv(170, 159).cuboid(-5.75F, -4.0F, -9.95F, 11.5F, 4.0F, 10.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, -3.1416F, 1.0472F, 3.1416F));

        ModelPartData RotorBottom_high2_r1 = RotorBaseHigh.addChild("RotorBottom_high2_r1", ModelPartBuilder.create().uv(170, 159).cuboid(-5.75F, -4.0F, -9.95F, 11.5F, 4.0F, 10.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData RotorBottom_high6_r1 = RotorBaseHigh.addChild("RotorBottom_high6_r1", ModelPartBuilder.create().uv(69, 173).cuboid(-5.75F, -4.0F, -9.95F, 11.5F, 4.0F, 10.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData RotorCentre = Rotor.addChild("RotorCentre", ModelPartBuilder.create().uv(27, 107).cuboid(-1.0F, -31.0F, 2.0F, 2.0F, 64.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -36.5F, 0.0F));

        ModelPartData cube_r3 = RotorCentre.addChild("cube_r3", ModelPartBuilder.create().uv(95, 106).cuboid(-1.0F, -32.0F, -1.0F, 2.0F, 64.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(2.5981F, 1.0F, 1.5F, 0.0F, 1.0472F, 0.0F));

        ModelPartData cube_r4 = RotorCentre.addChild("cube_r4", ModelPartBuilder.create().uv(104, 106).cuboid(-1.0F, -32.0F, -1.0F, 2.0F, 64.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(2.5981F, 1.0F, -1.5F, 0.0F, 2.0944F, 0.0F));

        ModelPartData cube_r5 = RotorCentre.addChild("cube_r5", ModelPartBuilder.create().uv(0, 107).cuboid(-1.0F, -32.0F, -1.0F, 2.0F, 64.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 1.0F, -3.0F, 0.0F, 3.1416F, 0.0F));

        ModelPartData cube_r6 = RotorCentre.addChild("cube_r6", ModelPartBuilder.create().uv(9, 107).cuboid(-1.0F, -32.0F, -1.0F, 2.0F, 64.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(-2.5981F, 1.0F, -1.5F, 0.0F, -2.0944F, 0.0F));

        ModelPartData cube_r7 = RotorCentre.addChild("cube_r7", ModelPartBuilder.create().uv(18, 107).cuboid(-1.0F, -32.0F, -1.0F, 2.0F, 64.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(-2.5981F, 1.0F, 1.5F, 0.0F, -1.0472F, 0.0F));

        ModelPartData planes = RotorCentre.addChild("planes", ModelPartBuilder.create().uv(194, 221).cuboid(-2.8673F, 5.9999F, -5.0F, 5.7735F, 0.0F, 10.0F, new Dilation(0.0F))
                .uv(52, 222).cuboid(-2.8673F, -6.0001F, -5.0F, 5.7735F, 0.0F, 10.0F, new Dilation(0.0F))
                .uv(71, 19).cuboid(-3.1559F, -18.4991F, -5.5F, 6.1754F, -0.001F, 11.0F, new Dilation(0.0F))
                .uv(71, 0).cuboid(-3.1559F, 18.5009F, -5.5F, 6.1754F, -0.001F, 11.0F, new Dilation(0.0F))
                .uv(54, 211).cuboid(-2.8673F, -17.5001F, -5.0F, 5.7735F, 0.0F, 10.0F, new Dilation(0.0F))
                .uv(206, 210).cuboid(-2.8673F, 17.4999F, -5.0F, 5.7735F, 0.0F, 10.0F, new Dilation(0.0F))
                .uv(176, 105).cuboid(-3.7333F, -3.0001F, -6.5F, 7.5056F, 0.0F, 13.0F, new Dilation(0.0F))
                .uv(170, 174).cuboid(-3.7333F, 2.9999F, -6.5F, 7.5056F, 0.0F, 13.0F, new Dilation(0.0F))
                .uv(35, 155).cuboid(-4.3106F, -0.0001F, -7.5F, 8.6603F, 0.0F, 15.0F, new Dilation(0.0F)), ModelTransform.pivot(-0.0195F, 1.0001F, 0.0F));

        ModelPartData cube_r8 = planes.addChild("cube_r8", ModelPartBuilder.create().uv(71, 36).cuboid(-3.0877F, 0.0005F, -5.5F, 6.1754F, -0.001F, 11.0F, new Dilation(0.0F))
                .uv(71, 54).cuboid(-3.0877F, 37.0005F, -5.5F, 6.1754F, -0.001F, 11.0F, new Dilation(0.0F)), ModelTransform.of(-0.0341F, -18.4996F, -0.0591F, 0.0F, -1.0472F, 0.0F));

        ModelPartData cube_r9 = planes.addChild("cube_r9", ModelPartBuilder.create().uv(112, 211).cuboid(-2.8868F, 0.0F, -5.0F, 5.7735F, 0.0F, 10.0F, new Dilation(0.0F))
                .uv(0, 220).cuboid(-2.8868F, 35.0F, -5.0F, 5.7735F, 0.0F, 10.0F, new Dilation(0.0F))
                .uv(176, 121).cuboid(-3.7528F, 14.5F, -6.5F, 7.5055F, 0.0F, 13.0F, new Dilation(0.0F))
                .uv(180, 15).cuboid(-3.7528F, 20.5F, -6.5F, 7.5055F, 0.0F, 13.0F, new Dilation(0.0F))
                .uv(163, 53).cuboid(-4.3301F, 17.5F, -7.5F, 8.6603F, 0.0F, 15.0F, new Dilation(0.0F))
                .uv(222, 107).cuboid(-2.8868F, 11.5F, -5.0F, 5.7735F, 0.0F, 10.0F, new Dilation(0.0F))
                .uv(21, 222).cuboid(-2.8868F, 23.5F, -5.0F, 5.7735F, 0.0F, 10.0F, new Dilation(0.0F)), ModelTransform.of(0.0097F, -17.5001F, 0.0169F, 0.0F, -1.0472F, 0.0F));

        ModelPartData cube_r10 = planes.addChild("cube_r10", ModelPartBuilder.create().uv(71, 72).cuboid(-3.0877F, 0.0005F, -5.5F, 6.1754F, -0.001F, 11.0F, new Dilation(0.0F))
                .uv(84, 19).cuboid(-3.0877F, 37.0005F, -5.5F, 6.1754F, -0.001F, 11.0F, new Dilation(0.0F)), ModelTransform.of(0.0341F, -18.4996F, -0.0591F, -3.1416F, -1.0472F, 3.1416F));

        ModelPartData cube_r11 = planes.addChild("cube_r11", ModelPartBuilder.create().uv(133, 220).cuboid(-2.8867F, 0.0F, -5.0F, 5.7735F, 0.0F, 10.0F, new Dilation(0.0F))
                .uv(175, 220).cuboid(-2.8867F, 35.0F, -5.0F, 5.7735F, 0.0F, 10.0F, new Dilation(0.0F)), ModelTransform.of(-0.0097F, -17.5001F, 0.0169F, -3.1416F, -1.0472F, 3.1416F));

        ModelPartData cube_r12 = planes.addChild("cube_r12", ModelPartBuilder.create().uv(17, 186).cuboid(-3.7528F, 0.0F, -6.5F, 7.5055F, 0.0F, 13.0F, new Dilation(0.0F)), ModelTransform.of(-0.0097F, -3.0001F, 0.0169F, -3.1416F, -1.0472F, 3.1416F));

        ModelPartData cube_r13 = planes.addChild("cube_r13", ModelPartBuilder.create().uv(45, 188).cuboid(-3.7528F, 4.0F, -6.5F, 7.5055F, 0.0F, 13.0F, new Dilation(0.0F)), ModelTransform.of(-0.0097F, -1.0001F, 0.0169F, -3.1416F, -1.0472F, 3.1416F));

        ModelPartData cube_r14 = planes.addChild("cube_r14", ModelPartBuilder.create().uv(163, 71).cuboid(-4.3301F, 0.0F, -7.5F, 8.6602F, 0.0F, 15.0F, new Dilation(0.0F)), ModelTransform.of(-0.0097F, -0.0001F, 0.0169F, -3.1416F, -1.0472F, 3.1416F));

        ModelPartData cube_r15 = planes.addChild("cube_r15", ModelPartBuilder.create().uv(112, 222).cuboid(-2.8867F, 0.0F, -5.0F, 5.7735F, 0.0F, 10.0F, new Dilation(0.0F)), ModelTransform.of(-0.0097F, -6.0001F, 0.0169F, -3.1416F, -1.0472F, 3.1416F));

        ModelPartData cube_r16 = planes.addChild("cube_r16", ModelPartBuilder.create().uv(222, 38).cuboid(-2.8867F, 8.0F, -5.0F, 5.7735F, 0.0F, 10.0F, new Dilation(0.0F)), ModelTransform.of(-0.0097F, -2.0001F, 0.0169F, -3.1416F, -1.0472F, 3.1416F));

        ModelPartData Centrifuge_Low = Rotor.addChild("Centrifuge_Low", ModelPartBuilder.create(), ModelTransform.of(0.0F, -14.0F, 0.0F, 0.0F, -0.5236F, 0.0F));

        ModelPartData low_a = Centrifuge_Low.addChild("low_a", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData bone = low_a.addChild("bone", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 2.6168F, 5.3303F));

        ModelPartData cube_r17 = bone.addChild("cube_r17", ModelPartBuilder.create().uv(74, 202).cuboid(-1.0F, -1.25F, -1.0F, 2.0F, 2.5F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, -0.3927F, 0.0F, 0.0F));

        ModelPartData cube_r18 = bone.addChild("cube_r18", ModelPartBuilder.create().uv(0, 203).cuboid(-0.75F, -2.0F, -0.75F, 1.5F, 4.0F, 1.5F, new Dilation(0.0F)), ModelTransform.of(0.0F, -2.5407F, 1.0524F, -0.3927F, 0.0F, 0.0F));

        ModelPartData Artron = bone.addChild("Artron", ModelPartBuilder.create().uv(6, 203).cuboid(-0.75F, -2.0F, -0.75F, 1.5F, 4.0F, 1.5F, new Dilation(-0.025F)), ModelTransform.of(0.0F, -2.5407F, 1.0524F, -0.3927F, 0.0F, 0.0F));

        ModelPartData low_b = Centrifuge_Low.addChild("low_b", ModelPartBuilder.create(), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bonec = low_b.addChild("bonec", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 2.6168F, 5.3303F));

        ModelPartData cube_r19 = bonec.addChild("cube_r19", ModelPartBuilder.create().uv(29, 200).cuboid(-1.0F, -1.25F, -1.0F, 2.0F, 2.5F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, -0.3927F, 0.0F, 0.0F));

        ModelPartData cube_r20 = bonec.addChild("cube_r20", ModelPartBuilder.create().uv(0, 203).cuboid(-0.75F, -2.0F, -0.75F, 1.5F, 4.0F, 1.5F, new Dilation(0.0F)), ModelTransform.of(0.0F, -2.5407F, 1.0524F, -0.3927F, 0.0F, 0.0F));

        ModelPartData Artron2 = bonec.addChild("Artron2", ModelPartBuilder.create().uv(6, 203).cuboid(-0.75F, -2.0F, -0.75F, 1.5F, 4.0F, 1.5F, new Dilation(-0.025F)), ModelTransform.of(0.0F, -2.5407F, 1.0524F, -0.3927F, 0.0F, 0.0F));

        ModelPartData low_c = Centrifuge_Low.addChild("low_c", ModelPartBuilder.create(), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -2.0944F, 0.0F));

        ModelPartData boned = low_c.addChild("boned", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 2.6168F, 5.3303F));

        ModelPartData cube_r21 = boned.addChild("cube_r21", ModelPartBuilder.create().uv(0, 198).cuboid(-1.0F, -1.25F, -1.0F, 2.0F, 2.5F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, -0.3927F, 0.0F, 0.0F));

        ModelPartData cube_r22 = boned.addChild("cube_r22", ModelPartBuilder.create().uv(0, 203).cuboid(-0.75F, -2.0F, -0.75F, 1.5F, 4.0F, 1.5F, new Dilation(0.0F)), ModelTransform.of(0.0F, -2.5407F, 1.0524F, -0.3927F, 0.0F, 0.0F));

        ModelPartData Artron3 = boned.addChild("Artron3", ModelPartBuilder.create().uv(6, 203).cuboid(-0.75F, -2.0F, -0.75F, 1.5F, 4.0F, 1.5F, new Dilation(-0.025F)), ModelTransform.of(0.0F, -2.5407F, 1.0524F, -0.3927F, 0.0F, 0.0F));

        ModelPartData low_d = Centrifuge_Low.addChild("low_d", ModelPartBuilder.create(), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 3.1416F, 0.0F));

        ModelPartData bonee = low_d.addChild("bonee", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 2.6168F, 5.3303F));

        ModelPartData cube_r23 = bonee.addChild("cube_r23", ModelPartBuilder.create().uv(175, 193).cuboid(-1.0F, -1.25F, -1.0F, 2.0F, 2.5F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, -0.3927F, 0.0F, 0.0F));

        ModelPartData cube_r24 = bonee.addChild("cube_r24", ModelPartBuilder.create().uv(0, 203).cuboid(-0.75F, -2.0F, -0.75F, 1.5F, 4.0F, 1.5F, new Dilation(0.0F)), ModelTransform.of(0.0F, -2.5407F, 1.0524F, -0.3927F, 0.0F, 0.0F));

        ModelPartData Artron12 = bonee.addChild("Artron12", ModelPartBuilder.create().uv(6, 203).cuboid(-0.75F, -2.0F, -0.75F, 1.5F, 4.0F, 1.5F, new Dilation(-0.025F)), ModelTransform.of(0.0F, -2.5407F, 1.0524F, -0.3927F, 0.0F, 0.0F));

        ModelPartData low_e = Centrifuge_Low.addChild("low_e", ModelPartBuilder.create(), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 2.0944F, 0.0F));

        ModelPartData bonef = low_e.addChild("bonef", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 2.6168F, 5.3303F));

        ModelPartData cube_r25 = bonef.addChild("cube_r25", ModelPartBuilder.create().uv(0, 203).cuboid(-0.75F, -2.0F, -0.75F, 1.5F, 4.0F, 1.5F, new Dilation(0.0F)), ModelTransform.of(0.0F, -2.5407F, 1.0524F, -0.3927F, 0.0F, 0.0F));

        ModelPartData cube_r26 = bonef.addChild("cube_r26", ModelPartBuilder.create().uv(74, 193).cuboid(-1.0F, -1.25F, -1.0F, 2.0F, 2.5F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, -0.3927F, 0.0F, 0.0F));

        ModelPartData Artron4 = bonef.addChild("Artron4", ModelPartBuilder.create().uv(6, 203).cuboid(-0.75F, -2.0F, -0.75F, 1.5F, 4.0F, 1.5F, new Dilation(-0.025F)), ModelTransform.of(0.0F, -2.5407F, 1.0524F, -0.3927F, 0.0F, 0.0F));

        ModelPartData low_thelastone = Centrifuge_Low.addChild("low_thelastone", ModelPartBuilder.create(), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData bonefu = low_thelastone.addChild("bonefu", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 2.6168F, 5.3303F));

        ModelPartData cube_r27 = bonefu.addChild("cube_r27", ModelPartBuilder.create().uv(46, 191).cuboid(-1.0F, -1.25F, -1.0F, 2.0F, 2.5F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, -0.3927F, 0.0F, 0.0F));

        ModelPartData cube_r28 = bonefu.addChild("cube_r28", ModelPartBuilder.create().uv(0, 203).cuboid(-0.75F, -2.0F, -0.75F, 1.5F, 4.0F, 1.5F, new Dilation(0.0F)), ModelTransform.of(0.0F, -2.5407F, 1.0524F, -0.3927F, 0.0F, 0.0F));

        ModelPartData Artron5 = bonefu.addChild("Artron5", ModelPartBuilder.create().uv(6, 203).cuboid(-0.75F, -2.0F, -0.75F, 1.5F, 4.0F, 1.5F, new Dilation(-0.025F)), ModelTransform.of(0.0F, -2.5407F, 1.0524F, -0.3927F, 0.0F, 0.0F));

        ModelPartData Centrifuge_High = Rotor.addChild("Centrifuge_High", ModelPartBuilder.create(), ModelTransform.of(0.0F, -57.0F, 0.0F, 0.0F, 0.5236F, -3.1416F));

        ModelPartData low_f = Centrifuge_High.addChild("low_f", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData bone8 = low_f.addChild("bone8", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 2.6168F, 5.3303F));

        ModelPartData cube_r29 = bone8.addChild("cube_r29", ModelPartBuilder.create().uv(175, 188).cuboid(-1.0F, -1.25F, -1.0F, 2.0F, 2.5F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, -0.3927F, 0.0F, 0.0F));

        ModelPartData cube_r30 = bone8.addChild("cube_r30", ModelPartBuilder.create().uv(0, 203).cuboid(-0.75F, -2.0F, -0.75F, 1.5F, 4.0F, 1.5F, new Dilation(0.0F)), ModelTransform.of(0.0F, -2.5407F, 1.0524F, -0.3927F, 0.0F, 0.0F));

        ModelPartData Artron6 = bone8.addChild("Artron6", ModelPartBuilder.create().uv(6, 203).cuboid(-0.75F, -2.0F, -0.75F, 1.5F, 4.0F, 1.5F, new Dilation(-0.025F)), ModelTransform.of(0.0F, -2.5407F, 1.0524F, -0.3927F, 0.0F, 0.0F));

        ModelPartData low_g = Centrifuge_High.addChild("low_g", ModelPartBuilder.create(), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone9 = low_g.addChild("bone9", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 2.6168F, 5.3303F));

        ModelPartData cube_r31 = bone9.addChild("cube_r31", ModelPartBuilder.create().uv(74, 188).cuboid(-1.0F, -1.25F, -1.0F, 2.0F, 2.5F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, -0.3927F, 0.0F, 0.0F));

        ModelPartData cube_r32 = bone9.addChild("cube_r32", ModelPartBuilder.create().uv(0, 203).cuboid(-0.75F, -2.0F, -0.75F, 1.5F, 4.0F, 1.5F, new Dilation(0.0F)), ModelTransform.of(0.0F, -2.5407F, 1.0524F, -0.3927F, 0.0F, 0.0F));

        ModelPartData Artron7 = bone9.addChild("Artron7", ModelPartBuilder.create().uv(6, 203).cuboid(-0.75F, -2.0F, -0.75F, 1.5F, 4.0F, 1.5F, new Dilation(-0.025F)), ModelTransform.of(0.0F, -2.5407F, 1.0524F, -0.3927F, 0.0F, 0.0F));

        ModelPartData low_j = Centrifuge_High.addChild("low_j", ModelPartBuilder.create(), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -2.0944F, 0.0F));

        ModelPartData bonek = low_j.addChild("bonek", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 2.6168F, 5.3303F));

        ModelPartData cube_r33 = bonek.addChild("cube_r33", ModelPartBuilder.create().uv(46, 186).cuboid(-1.0F, -1.25F, -1.0F, 2.0F, 2.5F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, -0.3927F, 0.0F, 0.0F));

        ModelPartData cube_r34 = bonek.addChild("cube_r34", ModelPartBuilder.create().uv(0, 203).cuboid(-0.75F, -2.0F, -0.75F, 1.5F, 4.0F, 1.5F, new Dilation(0.0F)), ModelTransform.of(0.0F, -2.5407F, 1.0524F, -0.3927F, 0.0F, 0.0F));

        ModelPartData Artron8 = bonek.addChild("Artron8", ModelPartBuilder.create().uv(6, 203).cuboid(-0.75F, -2.0F, -0.75F, 1.5F, 4.0F, 1.5F, new Dilation(-0.025F)), ModelTransform.of(0.0F, -2.5407F, 1.0524F, -0.3927F, 0.0F, 0.0F));

        ModelPartData low_k = Centrifuge_High.addChild("low_k", ModelPartBuilder.create(), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 3.1416F, 0.0F));

        ModelPartData bonel = low_k.addChild("bonel", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 2.6168F, 5.3303F));

        ModelPartData cube_r35 = bonel.addChild("cube_r35", ModelPartBuilder.create().uv(170, 181).cuboid(-1.0F, -1.25F, -1.0F, 2.0F, 2.5F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, -0.3927F, 0.0F, 0.0F));

        ModelPartData cube_r36 = bonel.addChild("cube_r36", ModelPartBuilder.create().uv(0, 203).cuboid(-0.75F, -2.0F, -0.75F, 1.5F, 4.0F, 1.5F, new Dilation(0.0F)), ModelTransform.of(0.0F, -2.5407F, 1.0524F, -0.3927F, 0.0F, 0.0F));

        ModelPartData Artron9 = bonel.addChild("Artron9", ModelPartBuilder.create().uv(6, 203).cuboid(-0.75F, -2.0F, -0.75F, 1.5F, 4.0F, 1.5F, new Dilation(-0.025F)), ModelTransform.of(0.0F, -2.5407F, 1.0524F, -0.3927F, 0.0F, 0.0F));

        ModelPartData low_l = Centrifuge_High.addChild("low_l", ModelPartBuilder.create(), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 2.0944F, 0.0F));

        ModelPartData bonen = low_l.addChild("bonen", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 2.6168F, 5.3303F));

        ModelPartData cube_r37 = bonen.addChild("cube_r37", ModelPartBuilder.create().uv(16, 181).cuboid(-1.0F, -1.25F, -1.0F, 2.0F, 2.5F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, -0.3927F, 0.0F, 0.0F));

        ModelPartData cube_r38 = bonen.addChild("cube_r38", ModelPartBuilder.create().uv(0, 203).cuboid(-0.75F, -2.0F, -0.75F, 1.5F, 4.0F, 1.5F, new Dilation(0.0F)), ModelTransform.of(0.0F, -2.5407F, 1.0524F, -0.3927F, 0.0F, 0.0F));

        ModelPartData Artron10 = bonen.addChild("Artron10", ModelPartBuilder.create().uv(6, 203).cuboid(-0.75F, -2.0F, -0.75F, 1.5F, 4.0F, 1.5F, new Dilation(-0.025F)), ModelTransform.of(0.0F, -2.5407F, 1.0524F, -0.3927F, 0.0F, 0.0F));

        ModelPartData low_n = Centrifuge_High.addChild("low_n", ModelPartBuilder.create(), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData bonem = low_n.addChild("bonem", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 2.6168F, 5.3303F));

        ModelPartData cube_r39 = bonem.addChild("cube_r39", ModelPartBuilder.create().uv(180, 22).cuboid(-1.0F, -1.25F, -1.0F, 2.0F, 2.5F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, -0.3927F, 0.0F, 0.0F));

        ModelPartData cube_r40 = bonem.addChild("cube_r40", ModelPartBuilder.create().uv(0, 203).cuboid(-0.75F, -2.0F, -0.75F, 1.5F, 4.0F, 1.5F, new Dilation(0.0F)), ModelTransform.of(0.0F, -2.5407F, 1.0524F, -0.3927F, 0.0F, 0.0F));

        ModelPartData Artron11 = bonem.addChild("Artron11", ModelPartBuilder.create().uv(6, 203).cuboid(-0.75F, -2.0F, -0.75F, 1.5F, 4.0F, 1.5F, new Dilation(-0.025F)), ModelTransform.of(0.0F, -2.5407F, 1.0524F, -0.3927F, 0.0F, 0.0F));

        ModelPartData RotorTopArm = Rotor.addChild("RotorTopArm", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, -66.9405F, 0.0F));

        ModelPartData RotortopA = Rotor.addChild("RotortopA", ModelPartBuilder.create().uv(56, 233).cuboid(-5.0F, 1.0F, -10.0F, 10.0F, 2.0F, 3.0F, new Dilation(0.0F))
                .uv(60, 240).cuboid(-5.0F, 3.0F, -8.0F, 10.0F, 2.0F, 1.0F, new Dilation(0.0F))
                .uv(224, 69).cuboid(-5.5F, -2.0F, -11.0F, 11.0F, 2.0F, 4.0F, new Dilation(0.0F))
                .uv(208, 18).cuboid(-6.0F, -5.0F, -12.0F, 12.0F, 2.0F, 5.0F, new Dilation(0.0F))
                .uv(203, 140).cuboid(-6.5F, -8.0F, -13.0F, 13.0F, 2.0F, 6.0F, new Dilation(0.0F))
                .uv(0, 36).cuboid(-4.05F, -8.0F, -7.0F, 8.1F, 11.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -64.0F, 0.0F, -3.1416F, 0.0F, 3.1416F));

        ModelPartData RotorArm_r1 = RotortopA.addChild("RotorArm_r1", ModelPartBuilder.create().uv(197, 53).cuboid(9.8138F, -8.0F, -0.957F, 4.0F, 14.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -4.9405F, 0.0F, 0.5442F, 0.9509F, 0.628F));

        ModelPartData RotortopB = Rotor.addChild("RotortopB", ModelPartBuilder.create().uv(56, 233).cuboid(-5.0F, 1.0F, -10.0F, 10.0F, 2.0F, 3.0F, new Dilation(0.0F))
                .uv(60, 240).cuboid(-5.0F, 3.0F, -8.0F, 10.0F, 2.0F, 1.0F, new Dilation(0.0F))
                .uv(224, 69).cuboid(-5.5F, -2.0F, -11.0F, 11.0F, 2.0F, 4.0F, new Dilation(0.0F))
                .uv(208, 18).cuboid(-6.0F, -5.0F, -12.0F, 12.0F, 2.0F, 5.0F, new Dilation(0.0F))
                .uv(203, 140).cuboid(-6.5F, -8.0F, -13.0F, 13.0F, 2.0F, 6.0F, new Dilation(0.0F))
                .uv(0, 36).cuboid(-4.05F, -8.0F, -7.0F, 8.1F, 11.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -64.0F, 0.0F, -3.1416F, -1.0472F, 3.1416F));

        ModelPartData RotorArm_r2 = RotortopB.addChild("RotorArm_r2", ModelPartBuilder.create().uv(197, 53).cuboid(9.8138F, -8.0F, -0.957F, 4.0F, 14.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -4.9405F, 0.0F, 0.5442F, 0.9509F, 0.628F));

        ModelPartData RotortopC = Rotor.addChild("RotortopC", ModelPartBuilder.create().uv(56, 233).cuboid(-5.0F, 1.0F, -10.0F, 10.0F, 2.0F, 3.0F, new Dilation(0.0F))
                .uv(60, 240).cuboid(-5.0F, 3.0F, -8.0F, 10.0F, 2.0F, 1.0F, new Dilation(0.0F))
                .uv(224, 69).cuboid(-5.5F, -2.0F, -11.0F, 11.0F, 2.0F, 4.0F, new Dilation(0.0F))
                .uv(208, 18).cuboid(-6.0F, -5.0F, -12.0F, 12.0F, 2.0F, 5.0F, new Dilation(0.0F))
                .uv(203, 140).cuboid(-6.5F, -8.0F, -13.0F, 13.0F, 2.0F, 6.0F, new Dilation(0.0F))
                .uv(0, 36).cuboid(-4.05F, -8.0F, -7.0F, 8.1F, 11.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -64.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData RotorArm_r3 = RotortopC.addChild("RotorArm_r3", ModelPartBuilder.create().uv(197, 53).cuboid(9.8138F, -8.0F, -0.957F, 4.0F, 14.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -4.9405F, 0.0F, 0.5442F, 0.9509F, 0.628F));

        ModelPartData RotortopD = Rotor.addChild("RotortopD", ModelPartBuilder.create().uv(56, 233).cuboid(-5.0F, 1.0F, -10.0F, 10.0F, 2.0F, 3.0F, new Dilation(0.0F))
                .uv(60, 240).cuboid(-5.0F, 3.0F, -8.0F, 10.0F, 2.0F, 1.0F, new Dilation(0.0F))
                .uv(224, 69).cuboid(-5.5F, -2.0F, -11.0F, 11.0F, 2.0F, 4.0F, new Dilation(0.0F))
                .uv(208, 18).cuboid(-6.0F, -5.0F, -12.0F, 12.0F, 2.0F, 5.0F, new Dilation(0.0F))
                .uv(203, 140).cuboid(-6.5F, -8.0F, -13.0F, 13.0F, 2.0F, 6.0F, new Dilation(0.0F))
                .uv(0, 36).cuboid(-4.05F, -8.0F, -7.0F, 8.1F, 11.0F, 0.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -64.0F, 0.0F));

        ModelPartData RotorArm_r4 = RotortopD.addChild("RotorArm_r4", ModelPartBuilder.create().uv(197, 53).cuboid(9.8138F, -8.0F, -0.957F, 4.0F, 14.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -4.9405F, 0.0F, 0.5442F, 0.9509F, 0.628F));

        ModelPartData RotortopE = Rotor.addChild("RotortopE", ModelPartBuilder.create().uv(56, 233).cuboid(-5.0F, 1.0F, -10.0F, 10.0F, 2.0F, 3.0F, new Dilation(0.0F))
                .uv(60, 240).cuboid(-5.0F, 3.0F, -8.0F, 10.0F, 2.0F, 1.0F, new Dilation(0.0F))
                .uv(224, 69).cuboid(-5.5F, -2.0F, -11.0F, 11.0F, 2.0F, 4.0F, new Dilation(0.0F))
                .uv(208, 18).cuboid(-6.0F, -5.0F, -12.0F, 12.0F, 2.0F, 5.0F, new Dilation(0.0F))
                .uv(203, 140).cuboid(-6.5F, -8.0F, -13.0F, 13.0F, 2.0F, 6.0F, new Dilation(0.0F))
                .uv(0, 36).cuboid(-4.05F, -8.0F, -7.0F, 8.1F, 11.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -64.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData RotorArm_r5 = RotortopE.addChild("RotorArm_r5", ModelPartBuilder.create().uv(197, 53).cuboid(9.8138F, -8.0F, -0.957F, 4.0F, 14.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -4.9405F, 0.0F, 0.5442F, 0.9509F, 0.628F));

        ModelPartData RotortopF = Rotor.addChild("RotortopF", ModelPartBuilder.create().uv(56, 233).cuboid(-5.0F, 1.0F, -10.0F, 10.0F, 2.0F, 3.0F, new Dilation(0.0F))
                .uv(60, 240).cuboid(-5.0F, 3.0F, -8.0F, 10.0F, 2.0F, 1.0F, new Dilation(0.0F))
                .uv(224, 69).cuboid(-5.5F, -2.0F, -11.0F, 11.0F, 2.0F, 4.0F, new Dilation(0.0F))
                .uv(208, 18).cuboid(-6.0F, -5.0F, -12.0F, 12.0F, 2.0F, 5.0F, new Dilation(0.0F))
                .uv(203, 140).cuboid(-6.5F, -8.0F, -13.0F, 13.0F, 2.0F, 6.0F, new Dilation(0.0F))
                .uv(0, 36).cuboid(-4.05F, -8.0F, -7.0F, 8.1F, 11.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -64.0F, 0.0F, 3.1416F, 1.0472F, 3.1416F));

        ModelPartData RotorArm_r6 = RotortopF.addChild("RotorArm_r6", ModelPartBuilder.create().uv(197, 53).cuboid(9.8138F, -8.0F, -0.957F, 4.0F, 14.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -4.9405F, 0.0F, 0.5442F, 0.9509F, 0.628F));

        ModelPartData UpperRail = Rotor.addChild("UpperRail", ModelPartBuilder.create().uv(42, 106).cuboid(-7.9419F, -0.6196F, 11.2359F, 15.0F, 1.0F, 0.0F, new Dilation(0.001F))
                .uv(42, 105).cuboid(-7.9419F, -0.6196F, 12.2359F, 15.0F, 1.0F, 0.0F, new Dilation(0.001F))
                .uv(37, 107).cuboid(-7.9419F, 0.1304F, 7.2359F, 15.0F, 0.0F, 5.0F, new Dilation(0.001F))
                .uv(49, 101).cuboid(3.0581F, -0.3696F, 10.7359F, 1.0F, 1.0F, 2.0F, new Dilation(0.0F))
                .uv(49, 101).cuboid(-4.9419F, -0.3696F, 10.7359F, 1.0F, 1.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(0.3913F, -52.3794F, 0.0F));

        ModelPartData HangPoint_r1 = UpperRail.addChild("HangPoint_r1", ModelPartBuilder.create().uv(49, 101).cuboid(-3.5F, -0.5F, 10.7359F, 1.0F, 1.0F, 2.0F, new Dilation(0.0F))
                .uv(49, 101).cuboid(2.5F, -0.5F, 10.7359F, 1.0F, 1.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(-0.4419F, 0.1304F, 0.0F, 0.0F, 2.0944F, 0.0F));

        ModelPartData ArmPoint_r1 = UpperRail.addChild("ArmPoint_r1", ModelPartBuilder.create().uv(54, 97).cuboid(-1.0F, -1.0F, -1.5F, 2.0F, 2.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(-10.6055F, 0.1304F, -5.868F, 0.0F, 2.618F, 0.0F));

        ModelPartData UpperRail_r1 = UpperRail.addChild("UpperRail_r1", ModelPartBuilder.create().uv(37, 107).cuboid(-7.5F, 0.0F, 7.2359F, 15.0F, 0.0F, 5.0F, new Dilation(0.001F))
                .uv(42, 106).cuboid(-7.5F, -0.75F, 11.2359F, 15.0F, 1.0F, 0.0F, new Dilation(0.001F))
                .uv(42, 105).cuboid(-7.5F, -0.75F, 12.2359F, 15.0F, 1.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(-0.4419F, 0.1304F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData UpperRail_r2 = UpperRail.addChild("UpperRail_r2", ModelPartBuilder.create().uv(37, 107).cuboid(-7.5F, 0.0F, 7.2359F, 15.0F, 0.0F, 5.0F, new Dilation(0.001F)), ModelTransform.of(-0.4419F, 0.1304F, 0.0F, -3.1416F, 1.0472F, 3.1416F));

        ModelPartData UpperRail_r3 = UpperRail.addChild("UpperRail_r3", ModelPartBuilder.create().uv(37, 107).cuboid(-7.5F, 0.0F, 7.2359F, 15.0F, 0.0F, 5.0F, new Dilation(0.001F)), ModelTransform.of(-0.4419F, 0.1304F, 0.0F, 0.0F, 3.1416F, 0.0F));

        ModelPartData UpperRailEdgeSmall_r1 = UpperRail.addChild("UpperRailEdgeSmall_r1", ModelPartBuilder.create().uv(42, 106).cuboid(-7.5F, -0.75F, 11.2359F, 15.0F, 1.0F, 0.0F, new Dilation(0.001F))
                .uv(42, 105).cuboid(-7.5F, -0.75F, 12.2359F, 15.0F, 1.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(-0.4419F, 0.1304F, 0.0F, -3.1416F, 0.0F, 3.1416F));

        ModelPartData UpperRailEdgeLarge_r1 = UpperRail.addChild("UpperRailEdgeLarge_r1", ModelPartBuilder.create().uv(42, 105).cuboid(-7.5F, -0.75F, 12.2359F, 15.0F, 1.0F, 0.0F, new Dilation(0.001F))
                .uv(42, 106).cuboid(-7.5F, -0.75F, 11.2359F, 15.0F, 1.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(-0.4419F, 0.1304F, 0.0F, -3.1416F, 1.0472F, -3.1416F));

        ModelPartData UpperRailEdgeLarge_r2 = UpperRail.addChild("UpperRailEdgeLarge_r2", ModelPartBuilder.create().uv(42, 105).cuboid(-7.5F, -0.75F, 12.2359F, 15.0F, 1.0F, 0.0F, new Dilation(0.001F))
                .uv(42, 106).cuboid(-7.5F, -0.75F, 11.2359F, 15.0F, 1.0F, 0.0F, new Dilation(0.001F))
                .uv(37, 107).cuboid(-7.5F, 0.0F, 7.2359F, 15.0F, 0.0F, 5.0F, new Dilation(0.001F)), ModelTransform.of(-0.4419F, 0.1304F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData UpperRailEdgeLarge_r3 = UpperRail.addChild("UpperRailEdgeLarge_r3", ModelPartBuilder.create().uv(42, 105).cuboid(-7.5F, -0.75F, 12.2359F, 15.0F, 1.0F, 0.0F, new Dilation(0.001F))
                .uv(42, 106).cuboid(-7.5F, -0.75F, 11.2359F, 15.0F, 1.0F, 0.0F, new Dilation(0.001F))
                .uv(37, 107).cuboid(-7.5F, 0.0F, 7.2359F, 15.0F, 0.0F, 5.0F, new Dilation(0.001F)), ModelTransform.of(-0.4419F, 0.1304F, 0.0F, 0.0F, -2.0944F, 0.0F));

        ModelPartData HangBar = UpperRail.addChild("HangBar", ModelPartBuilder.create().uv(53, 56).cuboid(3.65F, 0.25F, 0.0F, 1.0F, 9.0F, 0.0F, new Dilation(0.0F))
                .uv(75, 72).cuboid(3.4F, 9.25F, -1.0F, 1.0F, 1.0F, 2.0F, new Dilation(0.0F))
                .uv(75, 72).cuboid(-4.6F, 9.25F, -1.0F, 1.0F, 1.0F, 2.0F, new Dilation(0.0F))
                .uv(53, 56).cuboid(-4.35F, 0.25F, 0.0F, 1.0F, 9.0F, 0.0F, new Dilation(0.0F))
                .uv(55, 80).cuboid(-3.6F, 9.75F, -0.5F, 7.0F, 0.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(-0.3419F, 0.3804F, 11.7359F));

        ModelPartData HangCloth = HangBar.addChild("HangCloth", ModelPartBuilder.create(), ModelTransform.pivot(0.4F, 9.75F, 0.5F));

        ModelPartData HangCloth_r1 = HangCloth.addChild("HangCloth_r1", ModelPartBuilder.create().uv(56, 81).cuboid(-3.0F, 0.0F, 0.0F, 6.0F, 6.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 3.1416F, 0.0F));

        ModelPartData HangCloth2 = HangBar.addChild("HangCloth2", ModelPartBuilder.create().uv(56, 73).cuboid(-3.0F, 0.0F, 0.0F, 6.0F, 5.0F, 0.0F, new Dilation(0.0F)), ModelTransform.pivot(0.4F, 9.75F, -0.5F));

        ModelPartData HangWrench = HangBar.addChild("HangWrench", ModelPartBuilder.create().uv(36, 21).cuboid(0.0F, -1.0F, -1.0F, 0.0F, 7.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(-2.85F, 9.75F, 0.0F));

        ModelPartData MonitorArm = UpperRail.addChild("MonitorArm", ModelPartBuilder.create(), ModelTransform.of(-10.6055F, 0.1304F, -5.868F, 0.0503F, -0.5214F, -0.1007F));

        ModelPartData Joint_r1 = MonitorArm.addChild("Joint_r1", ModelPartBuilder.create().uv(55, 92).cuboid(-1.0F, -1.0F, -1.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.001F))
                .uv(67, 91).cuboid(-0.5F, -12.0F, -0.5F, 1.0F, 12.0F, 1.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 12.5F, 0.0F, 0.0F, 3.1416F, 0.0F));

        ModelPartData Joint = MonitorArm.addChild("Joint", ModelPartBuilder.create(), ModelTransform.of(0.0F, 12.5F, 0.0F, 0.0F, 0.0F, 0.4363F));

        ModelPartData Arm_r1 = Joint.addChild("Arm_r1", ModelPartBuilder.create().uv(73, 89).cuboid(-0.5F, 12.5F, -0.5F, 0.0F, 6.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-0.5F, -6.5F, 0.25F, 0.0F, 3.1416F, 0.0F));

        ModelPartData Arm_r2 = Joint.addChild("Arm_r2", ModelPartBuilder.create().uv(72, 97).cuboid(-0.5F, 12.5F, -0.5F, 1.0F, 6.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -12.5F, 0.0F, 0.0F, 3.1416F, 0.0F));

        ModelPartData Arm = Joint.addChild("Arm", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 12.0F, 0.0F));

        ModelPartData Joint_r2 = Arm.addChild("Joint_r2", ModelPartBuilder.create().uv(55, 92).cuboid(-1.0F, -1.0F, -1.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F))
                .uv(72, 97).cuboid(-0.5F, -6.0F, -0.5F, 1.0F, 6.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 3.1416F, 0.0F));

        ModelPartData HangMonitor = Arm.addChild("HangMonitor", ModelPartBuilder.create().uv(65, 80).cuboid(3.5468F, 5.5387F, -4.0F, 1.0F, 1.0F, 8.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.4363F));

        ModelPartData Arm_r3 = HangMonitor.addChild("Arm_r3", ModelPartBuilder.create().uv(36, 116).cuboid(-5.5F, -0.5F, -3.0F, 10.0F, 1.0F, 7.0F, new Dilation(0.0F)), ModelTransform.of(-0.4532F, 6.2887F, -0.5F, 0.0F, 1.5708F, 0.0F));

        ModelPartData Arm_r4 = HangMonitor.addChild("Arm_r4", ModelPartBuilder.create().uv(57, 88).cuboid(-1.5F, 29.5F, -1.5F, 2.0F, 1.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(-0.4532F, -24.7113F, -0.5F, 0.0F, 3.1416F, 0.0F));

        ModelPartData Arm_r5 = HangMonitor.addChild("Arm_r5", ModelPartBuilder.create().uv(49, 93).cuboid(-0.5F, 24.5F, -0.5F, 1.0F, 6.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -24.5F, 0.0F, 0.0F, 3.1416F, 0.0F));

        ModelPartData Controls = Root.addChild("Controls", ModelPartBuilder.create(), ModelTransform.pivot(-6.5F, -17.0745F, 21.896F));

        ModelPartData Speaker_r1 = Controls.addChild("Speaker_r1", ModelPartBuilder.create().uv(36, 60).cuboid(-2.0F, -1.875F, -2.0F, 5.0F, 2.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(-6.2844F, -3.181F, -19.1337F, -0.517F, 0.0869F, -1.4193F));

        ModelPartData Throttle = Controls.addChild("Throttle", ModelPartBuilder.create(), ModelTransform.of(0.0F, 0.0966F, -0.0259F, -1.5708F, 1.309F, -1.5708F));

        ModelPartData Base = Throttle.addChild("Base", ModelPartBuilder.create().uv(204, 112).cuboid(-2.5F, -3.6F, -2.0F, 1.5F, 3.0F, 1.5F, new Dilation(0.0F))
                .uv(204, 107).cuboid(-4.5F, -3.6F, -2.0F, 1.5F, 3.0F, 1.5F, new Dilation(0.0F))
                .uv(203, 149).cuboid(-0.5F, -3.6F, -2.0F, 1.5F, 3.0F, 1.5F, new Dilation(0.0F))
                .uv(0, 87).cuboid(-5.0F, -0.65F, -2.5F, 6.5F, 0.65F, 2.5F, new Dilation(0.0F)), ModelTransform.pivot(2.0F, 0.0F, 1.0F));

        ModelPartData Handle = Throttle.addChild("Handle", ModelPartBuilder.create().uv(176, 105).cuboid(-0.75F, -5.9625F, -0.7085F, 1.5F, 1.5F, 5.0F, new Dilation(0.0F))
                .uv(39, 239).cuboid(-0.749F, -4.9615F, -0.7075F, 2.0F, 2.0F, 0.0F, new Dilation(0.001F))
                .uv(39, 245).cuboid(-0.749F, -2.251F, 0.001F, 2.0F, 2.0F, 0.0F, new Dilation(0.001F))
                .uv(0, 220).cuboid(-1.5F, -1.5F, -0.75F, 3.0F, 3.0F, 2.0F, new Dilation(-0.75F))
                .uv(0, 225).cuboid(-1.5F, -1.5F, -0.25F, 3.0F, 3.0F, 2.0F, new Dilation(-0.75F)), ModelTransform.pivot(0.25F, -0.35F, -2.0F));

        ModelPartData cube_r41 = Handle.addChild("cube_r41", ModelPartBuilder.create().uv(44, 239).cuboid(-0.5F, -1.5F, 0.5F, 2.0F, 1.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(-0.249F, -1.5463F, 0.0F, 0.7854F, 0.0F, 0.0F));

        ModelPartData Hourglass = Controls.addChild("Hourglass", ModelPartBuilder.create().uv(17, 18).cuboid(-7.0F, -4.8F, 5.325F, 2.0F, 4.0F, 1.0F, new Dilation(0.0F))
                .uv(30, 24).cuboid(-7.0F, -3.3F, 5.075F, 2.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(15, 9).cuboid(-7.0F, -1.3F, 5.075F, 2.0F, 1.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(12.5124F, -1.5846F, -42.0374F));

        ModelPartData glassbit = Hourglass.addChild("glassbit", ModelPartBuilder.create().uv(24, 1).cuboid(-0.5F, -1.25F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(20, 15).cuboid(-0.5F, 0.25F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(29, 12).cuboid(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.25F)), ModelTransform.pivot(-6.0F, -3.05F, 6.075F));

        ModelPartData Handbrake2 = Controls.addChild("Handbrake2", ModelPartBuilder.create(), ModelTransform.pivot(14.0124F, -1.5846F, -42.0374F));

        ModelPartData cube_r42 = Handbrake2.addChild("cube_r42", ModelPartBuilder.create().uv(20, 205).cuboid(-1.0F, -1.0F, -1.0F, 2.0F, 2.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(-0.5F, 1.0006F, -1.8024F, 0.0F, -0.2618F, 1.5708F));

        ModelPartData Handleb = Handbrake2.addChild("Handleb", ModelPartBuilder.create().uv(0, 11).cuboid(-1.0F, -0.25F, -0.25F, -1.5F, 0.5F, 0.5F, new Dilation(0.0F))
                .uv(26, 174).cuboid(-5.0F, -0.5F, -0.5F, 3.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-0.5F, 1.1086F, -1.2462F, 0.0F, 0.9163F, 1.5708F));

        ModelPartData SmallNixie = Handbrake2.addChild("SmallNixie", ModelPartBuilder.create(), ModelTransform.pivot(-0.25F, 0.7253F, -4.5095F));

        ModelPartData Case_r1 = SmallNixie.addChild("Case_r1", ModelPartBuilder.create().uv(203, 154).cuboid(-1.0F, 0.75F, -0.75F, 1.5F, 1.0F, 1.5F, new Dilation(0.1F))
                .uv(223, 156).cuboid(-1.0F, 0.5F, -0.75F, 1.5F, 1.0F, 1.5F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.2618F, 0.0F, 0.0F));

        ModelPartData BlinkingNixie = Handbrake2.addChild("BlinkingNixie", ModelPartBuilder.create(), ModelTransform.pivot(-17.75F, 0.7253F, -4.5095F));

        ModelPartData Case_r2 = BlinkingNixie.addChild("Case_r2", ModelPartBuilder.create().uv(203, 154).cuboid(-1.0F, 0.75F, -0.75F, 1.5F, 1.0F, 1.5F, new Dilation(0.1F))
                .uv(211, 156).cuboid(-1.0F, 0.5F, -0.75F, 1.5F, 1.0F, 1.5F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.2618F, 0.0F, 0.0F));

        ModelPartData HoloGlobe = Controls.addChild("HoloGlobe", ModelPartBuilder.create(), ModelTransform.of(12.4767F, 16.1977F, -21.796F, 3.1416F, 0.0F, 2.8798F));

        ModelPartData Emitter = HoloGlobe.addChild("Emitter", ModelPartBuilder.create().uv(202, 39).cuboid(-1.75F, -0.7F, -1.7F, 3.5F, 2.0F, 3.5F, new Dilation(0.0F))
                .uv(164, 95).cuboid(-1.0F, -1.3F, -0.95F, 2.0F, 1.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(22.0F, -24.7F, -0.05F));

        ModelPartData Globe = Emitter.addChild("Globe", ModelPartBuilder.create().uv(170, 174).cuboid(-1.5F, -1.5F, -1.5F, 3.0F, 3.0F, 3.0F, new Dilation(0.0F))
                .uv(16, 174).cuboid(-1.5F, -1.5F, -1.5F, 3.0F, 3.0F, 3.0F, new Dilation(0.1F)), ModelTransform.of(-0.483F, -4.1706F, 0.05F, 0.0F, 0.0F, -0.2618F));

        ModelPartData SonicPort = HoloGlobe.addChild("SonicPort", ModelPartBuilder.create().uv(176, 88).cuboid(0.0F, -1.05F, 0.0F, 0.0F, 0.0F, 0.0F, new Dilation(0.001F))
                .uv(202, 39).cuboid(-1.75F, -0.45F, -1.75F, 3.5F, 2.0F, 3.5F, new Dilation(0.0F))
                .uv(164, 86).cuboid(-1.0F, -1.05F, -1.0F, 2.0F, 1.0F, 2.0F, new Dilation(0.0F))
                .uv(172, 86).cuboid(-1.0F, -1.05F, -1.0F, 2.0F, 1.0F, 2.0F, new Dilation(0.001F)), ModelTransform.of(-20.8949F, -13.1122F, 0.2F, 0.0F, 0.0F, -0.5236F));

        ModelPartData cube_r43 = SonicPort.addChild("cube_r43", ModelPartBuilder.create().uv(22, 246).cuboid(-2.25F, -1.0F, 0.0F, 4.0F, 2.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, -1.0F, 0.0F, 0.0F, 0.7854F, 0.0F));

        ModelPartData cube_r44 = SonicPort.addChild("cube_r44", ModelPartBuilder.create().uv(22, 246).cuboid(-2.25F, -1.0F, 0.0F, 4.0F, 2.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, -1.0F, 0.0F, 0.0F, -0.7854F, 0.0F));

        ModelPartData Coordinates = Controls.addChild("Coordinates", ModelPartBuilder.create(), ModelTransform.pivot(6.5F, 17.0745F, -21.896F));

        ModelPartData YEAR = Coordinates.addChild("YEAR", ModelPartBuilder.create().uv(58, 3).cuboid(-1.5F, -0.5F, -0.5F, 3.0F, 1.0F, 1.0F, new Dilation(0.05F))
                .uv(41, 7).cuboid(-1.5F, -0.5F, -0.5F, 3.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-1.0F, -18.0551F, 18.4862F, 1.309F, 0.0F, 0.0F));

        ModelPartData MONTH = Coordinates.addChild("MONTH", ModelPartBuilder.create().uv(57, 5).cuboid(-2.0F, -0.5F, -0.5F, 4.0F, 1.0F, 1.0F, new Dilation(0.05F))
                .uv(50, 7).cuboid(-2.0F, -0.5F, -0.5F, 4.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(3.0F, -18.0551F, 18.4862F, 1.309F, 0.0F, 0.0F));

        ModelPartData PLANET = Coordinates.addChild("PLANET", ModelPartBuilder.create().uv(66, 1).cuboid(-3.5F, -0.5F, -0.5F, 7.0F, 1.0F, 1.0F, new Dilation(0.05F))
                .uv(66, 3).cuboid(-3.5F, -0.5F, -0.5F, 7.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(4.0F, -17.2786F, 21.3839F, 1.309F, 0.0F, 0.0F));

        ModelPartData DAY = Coordinates.addChild("DAY", ModelPartBuilder.create().uv(59, 1).cuboid(-1.0F, -0.5F, -0.5F, 2.0F, 1.0F, 1.0F, new Dilation(0.05F))
                .uv(48, 5).cuboid(-1.0F, -0.5F, -0.5F, 2.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(6.5F, -18.0551F, 18.4862F, 1.309F, 0.0F, 0.0F));

        ModelPartData X = Coordinates.addChild("X", ModelPartBuilder.create().uv(13, 246).cuboid(-1.0F, -0.5F, -1.0F, 2.0F, 1.0F, 2.0F, new Dilation(0.0F))
                .uv(5, 246).cuboid(-1.0F, -0.5F, -1.0F, 2.0F, 1.0F, 2.0F, new Dilation(0.1F))
                .uv(10, 16).cuboid(-0.5F, -0.75F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.05F)), ModelTransform.pivot(3.0F, -19.2F, 15.025F));

        ModelPartData Y = Coordinates.addChild("Y", ModelPartBuilder.create().uv(13, 246).cuboid(-1.0F, -0.5F, -1.0F, 2.0F, 1.0F, 2.0F, new Dilation(0.0F))
                .uv(5, 246).cuboid(-1.0F, -0.5F, -1.0F, 2.0F, 1.0F, 2.0F, new Dilation(0.1F))
                .uv(6, 16).cuboid(-0.5F, -0.75F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.05F)), ModelTransform.pivot(0.0F, -19.2F, 15.025F));

        ModelPartData Z = Coordinates.addChild("Z", ModelPartBuilder.create().uv(13, 246).cuboid(-1.0F, -0.5F, -1.0F, 2.0F, 1.0F, 2.0F, new Dilation(0.0F))
                .uv(5, 246).cuboid(-1.0F, -0.5F, -1.0F, 2.0F, 1.0F, 2.0F, new Dilation(0.1F))
                .uv(14, 16).cuboid(-0.5F, -0.75F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.05F)), ModelTransform.pivot(-3.0F, -19.2F, 15.025F));

        ModelPartData SeigeMode = Controls.addChild("SeigeMode", ModelPartBuilder.create(), ModelTransform.pivot(30.1277F, 6.3271F, -17.1999F));

        ModelPartData Cable_r1 = SeigeMode.addChild("Cable_r1", ModelPartBuilder.create().uv(27, 200).cuboid(0.0F, -4.0F, -6.5F, 0.0F, 8.0F, 13.0F, new Dilation(0.0F)), ModelTransform.of(1.033F, 0.0F, -2.8563F, 0.0F, -0.0436F, 0.0F));

        ModelPartData Lever = SeigeMode.addChild("Lever", ModelPartBuilder.create(), ModelTransform.pivot(0.1737F, -0.6898F, -10.121F));

        ModelPartData cube_r45 = Lever.addChild("cube_r45", ModelPartBuilder.create().uv(102, 173).cuboid(-1.0F, -1.0F, -1.75F, 2.0F, 2.0F, 3.5F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, -0.2174F, 0.4802F, -0.4461F));

        ModelPartData Switch2 = Lever.addChild("Switch2", ModelPartBuilder.create().uv(48, 197).cuboid(2.0F, -0.5F, -1.75F, 0.5F, 1.0F, 3.5F, new Dilation(0.0F))
                .uv(82, 70).cuboid(0.5625F, -0.25F, -1.2578F, 1.5F, 0.5F, 0.5F, new Dilation(0.0F))
                .uv(82, 16).cuboid(0.5625F, -0.25F, 0.7672F, 1.5F, 0.5F, 0.5F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.5236F, 0.0F));

        ModelPartData Monitor = Controls.addChild("Monitor", ModelPartBuilder.create().uv(217, 221).cuboid(-5.0F, 2.0F, -1.625F, 10.0F, 2.0F, 5.0F, new Dilation(0.0F)), ModelTransform.of(16.351F, -11.9255F, -27.5835F, 0.0F, 2.0944F, 0.0F));

        ModelPartData keyboardflightpanel_r1 = Monitor.addChild("keyboardflightpanel_r1", ModelPartBuilder.create().uv(229, 217).cuboid(-4.0F, -0.5F, -1.0F, 8.0F, 1.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 3.0F, 4.375F, 0.0F, 3.1416F, 0.0F));

        ModelPartData hologram = Monitor.addChild("hologram", ModelPartBuilder.create().uv(68, 155).cuboid(-6.0F, -4.0F, 0.0F, 12.0F, 8.0F, 0.0F, new Dilation(0.001F))
                .uv(80, 155).cuboid(6.0F, 4.0F, 0.0F, 0.0F, 0.0F, 0.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -3.0F, -0.875F));

        ModelPartData bars = hologram.addChild("bars", ModelPartBuilder.create(), ModelTransform.pivot(5.25F, 3.5F, 0.025F));

        ModelPartData bar = bars.addChild("bar", ModelPartBuilder.create().uv(15, 66).cuboid(-0.25F, -3.0F, 0.0F, 0.5F, 3.0F, 0.0F, new Dilation(0.001F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData barb = bars.addChild("barb", ModelPartBuilder.create().uv(16, 48).cuboid(-0.25F, -3.0F, 0.0F, 0.5F, 3.0F, 0.0F, new Dilation(0.001F)), ModelTransform.pivot(-1.0F, 0.0F, 0.0F));

        ModelPartData barc = bars.addChild("barc", ModelPartBuilder.create().uv(16, 30).cuboid(-0.25F, -3.0F, 0.0F, 0.5F, 3.0F, 0.0F, new Dilation(0.001F)), ModelTransform.pivot(-2.0F, 0.0F, 0.0F));

        ModelPartData bard = bars.addChild("bard", ModelPartBuilder.create().uv(16, 12).cuboid(-0.25F, -3.0F, 0.0F, 0.5F, 3.0F, 0.0F, new Dilation(0.001F)), ModelTransform.pivot(-3.0F, 0.0F, 0.0F));

        ModelPartData spincircleb = hologram.addChild("spincircleb", ModelPartBuilder.create().uv(30, 206).cuboid(-2.25F, -2.25F, 0.0F, 5.0F, 5.0F, 0.0F, new Dilation(0.001F)), ModelTransform.pivot(-0.75F, -1.25F, 0.025F));

        ModelPartData spincircle = hologram.addChild("spincircle", ModelPartBuilder.create().uv(72, 176).cuboid(-1.75F, -1.75F, 0.0F, 3.5F, 3.5F, 0.0F, new Dilation(0.001F)), ModelTransform.pivot(-3.75F, 1.75F, 0.025F));

        ModelPartData TinySwitches = Controls.addChild("TinySwitches", ModelPartBuilder.create(), ModelTransform.pivot(6.5F, 0.3245F, -22.396F));

        ModelPartData AutoPilot = TinySwitches.addChild("AutoPilot", ModelPartBuilder.create().uv(54, 4).cuboid(-0.25F, 0.0F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(3.5F, 0.0F, 25.25F));

        ModelPartData cube_r46 = AutoPilot.addChild("cube_r46", ModelPartBuilder.create().uv(55, 6).cuboid(-0.5F, -1.0F, 0.0F, 1.0F, 1.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.3927F, 0.0F, 0.0F));

        ModelPartData TinySwitch = TinySwitches.addChild("TinySwitch", ModelPartBuilder.create().uv(54, 4).cuboid(-0.25F, 0.0F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(5.0F, 0.0F, 25.25F));

        ModelPartData cube_r47 = TinySwitch.addChild("cube_r47", ModelPartBuilder.create().uv(55, 6).cuboid(-0.5F, -1.0F, 0.0F, 1.0F, 1.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.3927F, 0.0F, 0.0F));

        ModelPartData TinySwitchB = TinySwitches.addChild("TinySwitchB", ModelPartBuilder.create().uv(54, 4).cuboid(-0.25F, 0.0F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(6.5F, 0.0F, 25.25F));

        ModelPartData cube_r48 = TinySwitchB.addChild("cube_r48", ModelPartBuilder.create().uv(55, 6).cuboid(-0.5F, -1.0F, 0.0F, 1.0F, 1.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.3927F, 0.0F, 0.0F));

        ModelPartData IsomorphicControls = TinySwitches.addChild("IsomorphicControls", ModelPartBuilder.create().uv(54, 4).cuboid(-0.25F, 0.0F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(8.0F, 0.0F, 25.25F));

        ModelPartData cube_r49 = IsomorphicControls.addChild("cube_r49", ModelPartBuilder.create().uv(55, 6).cuboid(-0.5F, -1.0F, 0.0F, 1.0F, 1.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.3927F, 0.0F, 0.0F));

        ModelPartData Refueler_animatesfuelguage = TinySwitches.addChild("Refueler_animatesfuelguage", ModelPartBuilder.create().uv(54, 4).cuboid(-0.25F, 0.0F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(9.5F, 0.0F, 25.25F));

        ModelPartData cube_r50 = Refueler_animatesfuelguage.addChild("cube_r50", ModelPartBuilder.create().uv(55, 6).cuboid(-0.5F, -1.0F, 0.0F, 1.0F, 1.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.3927F, 0.0F, 0.0F));

        ModelPartData TinySwitch5 = TinySwitches.addChild("TinySwitch5", ModelPartBuilder.create().uv(54, 4).cuboid(-0.25F, 0.0F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(11.0F, 0.0F, 25.25F));

        ModelPartData cube_r51 = TinySwitch5.addChild("cube_r51", ModelPartBuilder.create().uv(55, 6).cuboid(-0.5F, -1.0F, 0.0F, 1.0F, 1.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.3927F, 0.0F, 0.0F));

        ModelPartData TinySwitches2 = Controls.addChild("TinySwitches2", ModelPartBuilder.create(), ModelTransform.of(0.7237F, -2.6755F, -9.9008F, 0.0F, 2.0944F, 0.0F));

        ModelPartData CloakMode = TinySwitches2.addChild("CloakMode", ModelPartBuilder.create().uv(54, 4).cuboid(-0.25F, 0.0F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(3.5F, 0.0F, 24.75F));

        ModelPartData cube_r52 = CloakMode.addChild("cube_r52", ModelPartBuilder.create().uv(55, 6).cuboid(-0.5F, -1.0F, 0.0F, 1.0F, 1.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.3927F, 0.0F, 0.0F));

        ModelPartData AntiGravs = TinySwitches2.addChild("AntiGravs", ModelPartBuilder.create().uv(54, 4).cuboid(-0.25F, 0.0F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(8.0F, 0.0F, 24.75F));

        ModelPartData cube_r53 = AntiGravs.addChild("cube_r53", ModelPartBuilder.create().uv(55, 6).cuboid(-0.5F, -1.0F, 0.0F, 1.0F, 1.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.3927F, 0.0F, 0.0F));

        ModelPartData TinySwitchc = TinySwitches2.addChild("TinySwitchc", ModelPartBuilder.create().uv(54, 4).cuboid(-0.25F, 0.0F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(6.5F, 0.0F, 24.75F));

        ModelPartData cube_r54 = TinySwitchc.addChild("cube_r54", ModelPartBuilder.create().uv(55, 6).cuboid(-0.5F, -1.0F, 0.0F, 1.0F, 1.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.3927F, 0.0F, 0.0F));

        ModelPartData TinySwitchd = TinySwitches2.addChild("TinySwitchd", ModelPartBuilder.create().uv(54, 4).cuboid(-0.25F, 0.0F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(5.0F, 0.0F, 24.75F));

        ModelPartData cube_r55 = TinySwitchd.addChild("cube_r55", ModelPartBuilder.create().uv(55, 6).cuboid(-0.5F, -1.0F, 0.0F, 1.0F, 1.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.3927F, 0.0F, 0.0F));

        ModelPartData TinySwitchf = TinySwitches2.addChild("TinySwitchf", ModelPartBuilder.create().uv(54, 4).cuboid(-0.25F, 0.0F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(9.5F, 0.0F, 24.75F));

        ModelPartData cube_r56 = TinySwitchf.addChild("cube_r56", ModelPartBuilder.create().uv(55, 6).cuboid(-0.5F, -1.0F, 0.0F, 1.0F, 1.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.3927F, 0.0F, 0.0F));

        ModelPartData HailMary = TinySwitches2.addChild("HailMary", ModelPartBuilder.create().uv(54, 4).cuboid(-0.25F, 0.0F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(11.0F, 0.0F, 24.75F));

        ModelPartData cube_r57 = HailMary.addChild("cube_r57", ModelPartBuilder.create().uv(55, 6).cuboid(-0.5F, -1.0F, 0.0F, 1.0F, 1.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.3927F, 0.0F, 0.0F));

        ModelPartData TelepathicCircuits = Controls.addChild("TelepathicCircuits", ModelPartBuilder.create(), ModelTransform.of(-5.0295F, -2.9228F, -15.2395F, 0.0F, -1.0472F, 0.0F));

        ModelPartData TelepathicCircuits_r1 = TelepathicCircuits.addChild("TelepathicCircuits_r1", ModelPartBuilder.create().uv(23, 14).cuboid(-0.5F, -2.75F, 0.5F, 1.0F, 0.0F, 1.0F, new Dilation(0.0F))
                .uv(25, 14).cuboid(-0.5F, -3.0F, -1.5F, 1.0F, 3.0F, 1.0F, new Dilation(0.0F))
                .uv(25, 14).cuboid(-0.5F, -3.0F, 0.5F, 1.0F, 3.0F, 1.0F, new Dilation(0.0F))
                .uv(23, 14).cuboid(-0.5F, -2.75F, -1.5F, 1.0F, 0.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, -1.5708F, 0.3927F, -1.5708F));

        ModelPartData TelepathicCircuits_r2 = TelepathicCircuits.addChild("TelepathicCircuits_r2", ModelPartBuilder.create().uv(18, 23).cuboid(-1.575F, -0.275F, -2.5F, 3.0F, 2.0F, 5.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.1426F, 0.7824F, -1.5708F, 0.3927F, -1.5708F));

        ModelPartData DoorLock = Controls.addChild("DoorLock", ModelPartBuilder.create(), ModelTransform.of(-19.0913F, -0.3852F, -16.6471F, -0.2618F, -1.0472F, 0.0F));

        ModelPartData DoorLock_r1 = DoorLock.addChild("DoorLock_r1", ModelPartBuilder.create().uv(24, 16).cuboid(0.0F, -1.0F, -1.0F, 0.0F, 2.0F, 2.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 0.0F, 0.0F, -0.7854F, 0.0F, 0.0F));

        ModelPartData Whirlygig = Controls.addChild("Whirlygig", ModelPartBuilder.create(), ModelTransform.of(-13.6134F, -1.7535F, -27.4484F, 0.0F, 1.0472F, 0.0F));

        ModelPartData Whirlygig_r1 = Whirlygig.addChild("Whirlygig_r1", ModelPartBuilder.create().uv(55, 22).cuboid(-1.0F, -1.0F, -1.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.05F)), ModelTransform.of(0.0F, 0.0F, 0.0F, -1.5708F, -0.7854F, 3.1416F));

        ModelPartData Whirlygig_r2 = Whirlygig.addChild("Whirlygig_r2", ModelPartBuilder.create().uv(55, 22).cuboid(-1.0F, -1.0F, -1.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.05F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.7854F));

        ModelPartData Whirlygig_r3 = Whirlygig.addChild("Whirlygig_r3", ModelPartBuilder.create().uv(55, 22).cuboid(-1.0F, -1.0F, -1.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.05F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 1.5708F, -0.7854F, -1.5708F));

        ModelPartData Dimension = Controls.addChild("Dimension", ModelPartBuilder.create(), ModelTransform.of(-18.8161F, 0.3745F, -26.6973F, -3.1416F, -1.0472F, 3.1416F));

        ModelPartData cube_r58 = Dimension.addChild("cube_r58", ModelPartBuilder.create().uv(13, 3).cuboid(-1.0F, 0.0F, -5.0F, 3.0F, 0.0F, 4.0F, new Dilation(0.0F))
                .uv(5, 246).cuboid(-1.0F, -0.5F, -1.0F, 2.0F, 1.0F, 2.0F, new Dilation(0.1F))
                .uv(13, 246).cuboid(-1.0F, -0.5F, -1.0F, 2.0F, 1.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, -0.3927F, 0.0F, 0.0F));

        ModelPartData Button = Dimension.addChild("Button", ModelPartBuilder.create().uv(14, 16).cuboid(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.05F)), ModelTransform.of(0.0F, -0.231F, 0.0957F, -0.3927F, 0.0F, 0.0F));

        ModelPartData Power = Controls.addChild("Power", ModelPartBuilder.create(), ModelTransform.of(-13.246F, 0.1884F, -33.2963F, -3.1416F, -1.0472F, 3.1416F));

        ModelPartData hinge_r1 = Power.addChild("hinge_r1", ModelPartBuilder.create().uv(26, 256).cuboid(-0.5F, -0.5F, -1.5F, 1.0F, 1.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(-4.0F, 0.0F, 0.0F, -1.5708F, 1.309F, -1.5708F));

        ModelPartData Switch = Power.addChild("Switch", ModelPartBuilder.create(), ModelTransform.of(-4.0F, -0.483F, 0.1294F, -0.2618F, 0.0F, 0.0F));

        ModelPartData Switch_r1 = Switch.addChild("Switch_r1", ModelPartBuilder.create().uv(32, 252).cuboid(-0.5F, -0.5F, -2.0F, 3.0F, 1.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.5708F, 0.0F));

        ModelPartData Handle_r1 = Switch.addChild("Handle_r1", ModelPartBuilder.create().uv(31, 244).cuboid(-0.5F, -0.5F, 2.5F, 1.0F, 1.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 3.1416F, 0.0F, 3.1416F));

        ModelPartData Waypoint = Controls.addChild("Waypoint", ModelPartBuilder.create(), ModelTransform.of(-9.0885F, -0.6755F, -30.896F, -3.1416F, -1.0472F, 3.1416F));

        ModelPartData FastReturn_r1 = Waypoint.addChild("FastReturn_r1", ModelPartBuilder.create().uv(24, 240).cuboid(2.0F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(24, 238).cuboid(-3.0F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(24, 242).cuboid(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -0.9782F, -2.4845F, -2.8798F, 0.0F, 3.1416F));

        ModelPartData Load_r1 = Waypoint.addChild("Load_r1", ModelPartBuilder.create().uv(115, 241).cuboid(-0.575F, -0.275F, -3.5F, 2.0F, 2.0F, 7.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -1.6046F, -4.1045F, -1.5708F, 0.1309F, -1.5708F));

        ModelPartData LandType = Controls.addChild("LandType", ModelPartBuilder.create().uv(29, 1).cuboid(-0.75F, -1.0F, 0.0F, 1.0F, 1.0F, 0.0F, new Dilation(0.001F))
                .uv(29, 18).cuboid(-0.75F, -1.75F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.25F))
                .uv(30, 15).cuboid(-1.25F, -1.75F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.25F))
                .uv(29, 18).cuboid(-0.25F, -1.75F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.25F))
                .uv(30, 15).cuboid(0.25F, -1.75F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.25F)), ModelTransform.of(24.3819F, 0.5694F, -39.437F, -2.3562F, 1.0472F, 3.1416F));

        ModelPartData Alarms = Controls.addChild("Alarms", ModelPartBuilder.create(), ModelTransform.of(27.5228F, 0.3745F, -34.0335F, -3.1416F, 1.0472F, 3.1416F));

        ModelPartData cube_r59 = Alarms.addChild("cube_r59", ModelPartBuilder.create().uv(5, 246).cuboid(-1.0F, -0.5F, -1.0F, 2.0F, 1.0F, 2.0F, new Dilation(0.1F))
                .uv(13, 246).cuboid(-1.0F, -0.5F, -1.0F, 2.0F, 1.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, -0.3927F, 0.0F, 0.0F));

        ModelPartData Button2 = Alarms.addChild("Button2", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData Button_r1 = Button2.addChild("Button_r1", ModelPartBuilder.create().uv(10, 16).cuboid(-0.5F, -0.75F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.05F)), ModelTransform.of(0.0F, 0.0F, 0.0F, -0.3927F, 0.0F, 0.0F));

        ModelPartData Cogwheela = Alarms.addChild("Cogwheela", ModelPartBuilder.create().uv(38, -4).cuboid(0.0F, -2.25F, -2.75F, 0.0F, 5.0F, 5.0F, new Dilation(0.001F)), ModelTransform.of(4.25F, -1.3274F, -5.6377F, -0.2618F, 0.0F, 0.0F));

        ModelPartData Cogwheelb = Alarms.addChild("Cogwheelb", ModelPartBuilder.create().uv(38, -4).cuboid(0.0F, -2.25F, -2.75F, 0.0F, 5.0F, 5.0F, new Dilation(0.001F)), ModelTransform.of(5.25F, -1.3274F, -5.6377F, -0.2618F, 0.0F, 0.0F));

        ModelPartData Cogwheelc = Alarms.addChild("Cogwheelc", ModelPartBuilder.create().uv(38, -4).cuboid(0.0F, -2.25F, -2.75F, 0.0F, 5.0F, 5.0F, new Dilation(0.001F)), ModelTransform.of(6.25F, -1.3274F, -5.6377F, -0.2618F, 0.0F, 0.0F));

        ModelPartData Extras = Root.addChild("Extras", ModelPartBuilder.create().uv(204, 112).cuboid(10.5F, -5.0284F, -36.4759F, 1.5F, 3.0F, 1.5F, new Dilation(0.0F))
                .uv(204, 112).cuboid(0.5F, -5.0284F, -36.4759F, 1.5F, 3.0F, 1.5F, new Dilation(0.0F)), ModelTransform.pivot(-6.5F, -17.0745F, 21.896F));

        ModelPartData SuperDangerousButtonThatsAlwaysDisabled = Extras.addChild("SuperDangerousButtonThatsAlwaysDisabled", ModelPartBuilder.create(), ModelTransform.of(-14.5228F, 0.3745F, -9.7585F, 0.0F, -1.0472F, 0.0F));

        ModelPartData cube_r60 = SuperDangerousButtonThatsAlwaysDisabled.addChild("cube_r60", ModelPartBuilder.create().uv(5, 246).cuboid(-1.0F, -0.5F, -1.0F, 2.0F, 1.0F, 2.0F, new Dilation(0.1F))
                .uv(29, 21).cuboid(-0.5F, -0.75F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.05F))
                .uv(13, 246).cuboid(-1.0F, -0.5F, -1.0F, 2.0F, 1.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, -0.3927F, 0.0F, 0.0F));

        ModelPartData cube_r61 = SuperDangerousButtonThatsAlwaysDisabled.addChild("cube_r61", ModelPartBuilder.create().uv(24, 7).cuboid(-1.5F, -1.25F, -1.525F, 3.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.5236F, 0.0F, 0.0F));

        ModelPartData Hourglassb = Extras.addChild("Hourglassb", ModelPartBuilder.create().uv(17, 18).cuboid(-7.0F, -4.8F, 5.325F, 2.0F, 4.0F, 1.0F, new Dilation(0.0F))
                .uv(30, 24).cuboid(-7.0F, -3.3F, 5.075F, 2.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(15, 9).cuboid(-7.0F, -1.3F, 5.075F, 2.0F, 1.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(20.9368F, -1.5846F, -6.6184F, -3.1416F, -1.0472F, 3.1416F));

        ModelPartData glassbitb = Hourglassb.addChild("glassbitb", ModelPartBuilder.create().uv(24, 1).cuboid(-0.5F, -1.25F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(20, 15).cuboid(-0.5F, 0.25F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(29, 12).cuboid(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.25F)), ModelTransform.pivot(-6.0F, -3.05F, 6.075F));

        ModelPartData BarDial = Extras.addChild("BarDial", ModelPartBuilder.create(), ModelTransform.pivot(9.0F, -1.0183F, -38.8207F));

        ModelPartData BarDial_r1 = BarDial.addChild("BarDial_r1", ModelPartBuilder.create().uv(174, 112).cuboid(-2.0F, -1.0F, -1.5F, 4.0F, 2.0F, 3.0F, new Dilation(0.001F))
                .uv(177, 118).cuboid(-2.0F, -1.0F, -1.3F, 4.0F, 2.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, -0.1309F, 0.0F, 0.0F));

        ModelPartData DialBar = BarDial.addChild("DialBar", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData DialBar_r1 = DialBar.addChild("DialBar_r1", ModelPartBuilder.create().uv(175, 118).cuboid(-2.25F, -0.75F, -1.4F, 1.0F, 1.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 0.0F, 0.0F, -0.1309F, 0.0F, 0.0F));

        ModelPartData Phone = Extras.addChild("Phone", ModelPartBuilder.create(), ModelTransform.pivot(-12.1976F, -0.937F, -17.4518F));

        ModelPartData cube_r62 = Phone.addChild("cube_r62", ModelPartBuilder.create().uv(23, 64).cuboid(-2.5F, -1.0F, -1.0F, 5.0F, 1.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, -0.1483F, 0.504F, -0.3001F));

        ModelPartData CableA = Phone.addChild("CableA", ModelPartBuilder.create(), ModelTransform.pivot(-4.2947F, 0.8111F, 2.4795F));

        ModelPartData Cable_r2 = CableA.addChild("Cable_r2", ModelPartBuilder.create().uv(24, 62).cuboid(-5.0F, 0.0F, -0.5F, 5.0F, 0.0F, 1.0F, new Dilation(0.001F)), ModelTransform.of(2.0913F, -0.6471F, -1.2074F, -0.1483F, 0.504F, -0.3001F));

        ModelPartData CableB = CableA.addChild("CableB", ModelPartBuilder.create(), ModelTransform.pivot(-2.6778F, 0.9059F, 2.1234F));

        ModelPartData Cable_r3 = CableB.addChild("Cable_r3", ModelPartBuilder.create().uv(24, 61).cuboid(-2.0F, 0.0F, -1.5F, 2.0F, 0.0F, 1.0F, new Dilation(0.001F)), ModelTransform.of(0.8365F, -0.2588F, -0.483F, -0.1719F, 0.4971F, -0.3492F));

        ModelPartData CableC = CableB.addChild("CableC", ModelPartBuilder.create(), ModelTransform.of(-1.076F, 0.3007F, 0.0439F, -0.1719F, 0.4971F, -0.3492F));

        ModelPartData Cable_r4 = CableC.addChild("Cable_r4", ModelPartBuilder.create().uv(24, 61).cuboid(-2.0F, 0.0F, -0.5F, 2.0F, 0.0F, 1.0F, new Dilation(0.001F)), ModelTransform.of(-0.0009F, 0.0436F, -0.5F, 0.0F, 0.0F, -0.7854F));

        ModelPartData CableD = CableC.addChild("CableD", ModelPartBuilder.create(), ModelTransform.pivot(-1.4454F, 0.5262F, 0.8345F));

        ModelPartData Cable_r5 = CableD.addChild("Cable_r5", ModelPartBuilder.create().uv(24, 61).cuboid(-2.0F, 0.0F, -1.0F, 2.0F, 0.0F, 1.0F, new Dilation(0.001F)), ModelTransform.of(9.6704F, 9.1211F, -1.3345F, 0.0F, 0.0F, 2.7489F));

        ModelPartData Cable_r6 = CableD.addChild("Cable_r6", ModelPartBuilder.create().uv(24, 61).cuboid(-2.0F, 0.0F, -1.0F, 2.0F, 0.0F, 1.0F, new Dilation(0.001F)), ModelTransform.of(7.6704F, 9.1211F, -1.3345F, 0.0F, 0.0F, -3.1416F));

        ModelPartData Cable_r7 = CableD.addChild("Cable_r7", ModelPartBuilder.create().uv(24, 61).cuboid(-2.0F, 0.0F, -1.0F, 2.0F, 0.0F, 1.0F, new Dilation(0.001F)), ModelTransform.of(5.8226F, 8.3558F, -1.3345F, 0.0F, 0.0F, -2.7489F));

        ModelPartData Cable_r8 = CableD.addChild("Cable_r8", ModelPartBuilder.create().uv(24, 62).cuboid(-5.0F, 0.0F, -1.0F, 5.0F, 0.0F, 1.0F, new Dilation(0.001F)), ModelTransform.of(1.8559F, 5.3119F, -1.3345F, 0.0F, 0.0F, -2.4871F));

        ModelPartData Cable_r9 = CableD.addChild("Cable_r9", ModelPartBuilder.create().uv(28, 61).cuboid(-3.0F, 0.0F, -1.0F, 3.0F, 0.0F, 1.0F, new Dilation(0.001F)), ModelTransform.of(0.0296F, 2.9319F, -1.3345F, 0.0F, 0.0F, -2.2253F));

        ModelPartData Cable_r10 = CableD.addChild("Cable_r10", ModelPartBuilder.create().uv(24, 61).cuboid(-2.0F, 0.0F, -0.5F, 2.0F, 0.0F, 1.0F, new Dilation(0.001F)), ModelTransform.of(0.0296F, 0.9319F, -1.3345F, 0.0F, 0.0F, -1.5708F));

        ModelPartData Dials = Root.addChild("Dials", ModelPartBuilder.create(), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData cube_r63 = Dials.addChild("cube_r63", ModelPartBuilder.create().uv(206, 108).cuboid(-1.0F, -2.0F, -5.5F, 2.5F, 5.0F, 11.5F, new Dilation(0.0F)), ModelTransform.of(-9.2469F, -25.8251F, 5.0229F, 0.3554F, 0.3931F, 0.7696F));

        ModelPartData cube_r64 = Dials.addChild("cube_r64", ModelPartBuilder.create().uv(140, 202).cuboid(-11.5F, -24.5F, -5.5F, 2.0F, 6.5F, 11.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.5236F, 0.0F));

        ModelPartData Speedometer = Dials.addChild("Speedometer", ModelPartBuilder.create(), ModelTransform.of(-10.5598F, -26.4444F, 6.1222F, -0.8727F, -1.0472F, 0.0F));

        ModelPartData cube_r65 = Speedometer.addChild("cube_r65", ModelPartBuilder.create().uv(82, 65).cuboid(-1.5F, -0.4F, -1.5F, 3.0F, 0.5F, 3.0F, new Dilation(0.0F))
                .uv(82, 83).cuboid(-1.5F, -0.65F, -1.5F, 3.0F, 0.25F, 3.0F, new Dilation(0.0F))
                .uv(163, 48).cuboid(-1.5F, -0.9F, -1.5F, 3.0F, 0.25F, 3.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.5833F, 0.0F, 0.0F, -0.7854F, 0.0F));

        ModelPartData hand = Speedometer.addChild("hand", ModelPartBuilder.create().uv(12, 47).cuboid(-0.25F, 0.1F, -1.75F, 0.5F, 0.0F, 2.0F, new Dilation(0.001F)), ModelTransform.pivot(0.0F, 0.0333F, 1.0F));

        ModelPartData fuel_guage = Dials.addChild("fuel_guage", ModelPartBuilder.create(), ModelTransform.of(-11.8195F, -26.1231F, 3.1742F, -0.8727F, -1.0472F, 0.0F));

        ModelPartData cube_r66 = fuel_guage.addChild("cube_r66", ModelPartBuilder.create().uv(81, 170).cuboid(-1.45F, -0.4F, -1.05F, 2.5F, 0.5F, 2.5F, new Dilation(0.0F))
                .uv(170, 165).cuboid(-1.45F, -0.65F, -1.05F, 2.5F, 0.25F, 2.5F, new Dilation(0.0F))
                .uv(173, 15).cuboid(-1.45F, -0.9F, -1.05F, 2.5F, 0.25F, 2.5F, new Dilation(0.0F)), ModelTransform.of(0.2828F, 0.5833F, 0.0F, 0.0F, -0.7854F, 0.0F));

        ModelPartData handb = fuel_guage.addChild("handb", ModelPartBuilder.create().uv(13, 30).cuboid(-0.25F, 0.0F, -1.0F, 0.5F, 0.0F, 1.5F, new Dilation(0.001F)), ModelTransform.pivot(0.0078F, 0.0833F, 0.75F));

        ModelPartData Pressure = Dials.addChild("Pressure", ModelPartBuilder.create(), ModelTransform.of(-8.4906F, -26.1231F, 8.9401F, -0.8727F, -1.0472F, 0.0F));

        ModelPartData cube_r67 = Pressure.addChild("cube_r67", ModelPartBuilder.create().uv(81, 103).cuboid(-1.45F, -0.4F, -1.05F, 2.5F, 0.5F, 2.5F, new Dilation(0.0F))
                .uv(89, 102).cuboid(-1.45F, -0.65F, -1.05F, 2.5F, 0.25F, 2.5F, new Dilation(0.0F))
                .uv(163, 15).cuboid(-1.45F, -0.9F, -1.05F, 2.5F, 0.25F, 2.5F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.5833F, 0.0F, 0.0F, -0.7854F, 0.0F));

        ModelPartData handc = Pressure.addChild("handc", ModelPartBuilder.create().uv(18, 13).cuboid(-0.25F, 0.0F, -1.0F, 0.5F, 0.0F, 1.5F, new Dilation(0.001F)), ModelTransform.pivot(-0.275F, 0.0833F, 0.75F));

        ModelPartData Arms = Root.addChild("Arms", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, -24.0F, 0.0F));

        ModelPartData Arma = Arms.addChild("Arma", ModelPartBuilder.create().uv(198, 179).cuboid(14.0F, -1.0F, -1.1F, 19.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.2618F));

        ModelPartData cube_r68 = Arma.addChild("cube_r68", ModelPartBuilder.create().uv(164, 89).cuboid(-1.0F, -2.0F, -1.0F, 2.0F, 4.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(32.5258F, 1.0941F, -0.1F, 0.0F, 0.0F, -0.2618F));

        ModelPartData cube_r69 = Arma.addChild("cube_r69", ModelPartBuilder.create().uv(82, 47).cuboid(-4.0F, -2.0F, -1.1F, 5.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(14.134F, 1.2321F, 0.0F, 0.0F, 0.0F, -0.5236F));

        ModelPartData cube_r70 = Arma.addChild("cube_r70", ModelPartBuilder.create().uv(202, 236).cuboid(-1.0F, -3.0F, -1.1F, 10.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(17.0549F, 11.4011F, -0.2F, 3.1416F, 0.0F, 1.0472F));

        ModelPartData cube_r71 = Arma.addChild("cube_r71", ModelPartBuilder.create().uv(197, 79).cuboid(-1.0F, -2.0F, -1.1F, 20.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(33.1434F, 3.2671F, -0.2F, 3.1416F, 0.0F, 2.618F));

        ModelPartData Doesathing = Arma.addChild("Doesathing", ModelPartBuilder.create().uv(35, 7).cuboid(-0.5F, -0.1667F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(27.1385F, 0.286F, 3.5046F, -2.8798F, -1.0472F, 2.8798F));

        ModelPartData boneb = Doesathing.addChild("boneb", ModelPartBuilder.create().uv(45, 0).cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 0.0F, 4.0F, new Dilation(0.001F))
                .uv(32, 4).cuboid(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.25F))
                .uv(32, 2).cuboid(-0.25F, -1.75F, 0.75F, 1.0F, 2.0F, 1.0F, new Dilation(-0.25F)), ModelTransform.pivot(0.0F, -0.1667F, 0.0F));

        ModelPartData Doesathingthesequel = Arma.addChild("Doesathingthesequel", ModelPartBuilder.create().uv(35, 7).cuboid(-0.5F, -0.1667F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(22.8885F, 0.536F, 4.7046F, -2.8798F, -1.0472F, 2.8798F));

        ModelPartData Clockwise = Doesathingthesequel.addChild("Clockwise", ModelPartBuilder.create().uv(45, 0).cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 0.0F, 4.0F, new Dilation(0.001F))
                .uv(32, 4).cuboid(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.25F)), ModelTransform.pivot(0.0F, -0.1667F, 0.0F));

        ModelPartData Arm2 = Arms.addChild("Arm2", ModelPartBuilder.create().uv(198, 174).cuboid(14.0F, -1.0F, -1.1F, 19.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, -0.4214F, -0.9909F, 0.492F));

        ModelPartData cube_r72 = Arm2.addChild("cube_r72", ModelPartBuilder.create().uv(187, 19).cuboid(-1.5F, -1.0F, 0.0F, 2.0F, 2.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(34.4577F, 0.5764F, -0.101F, 0.0F, 0.0F, -0.2618F));

        ModelPartData cube_r73 = Arm2.addChild("cube_r73", ModelPartBuilder.create().uv(164, 89).cuboid(-1.0F, -2.0F, -1.0F, 2.0F, 4.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(32.5258F, 1.0941F, -0.1F, 0.0F, 0.0F, -0.2618F));

        ModelPartData cube_r74 = Arm2.addChild("cube_r74", ModelPartBuilder.create().uv(82, 11).cuboid(-2.5F, -1.0F, -1.0F, 5.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(12.3349F, 1.116F, -0.1F, 0.0F, 0.0F, -0.5236F));

        ModelPartData cube_r75 = Arm2.addChild("cube_r75", ModelPartBuilder.create().uv(236, 199).cuboid(-5.5F, -1.0F, -1.0F, 10.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(17.5729F, 16.2982F, -0.1F, 3.1416F, 0.0F, 1.0472F));

        ModelPartData cube_r76 = Arm2.addChild("cube_r76", ModelPartBuilder.create().uv(189, 193).cuboid(-10.0F, -1.0F, -1.0F, 20.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(24.8491F, 6.9011F, -0.1F, 3.1416F, 0.0F, 2.618F));

        ModelPartData HAMMERTIME = Arm2.addChild("HAMMERTIME", ModelPartBuilder.create(), ModelTransform.of(34.2162F, 0.6411F, -0.101F, 0.0F, 0.0F, -0.2618F));

        ModelPartData cube_r77 = HAMMERTIME.addChild("cube_r77", ModelPartBuilder.create().uv(65, 13).cuboid(-6.5F, -1.5F, -1.0F, 2.0F, 3.0F, 2.0F, new Dilation(0.0F))
                .uv(65, 10).cuboid(-4.5F, -0.5F, -0.5F, 5.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 2.5F, 0.0F, 0.0F, 0.0F, -1.5708F));

        ModelPartData cube_r78 = HAMMERTIME.addChild("cube_r78", ModelPartBuilder.create().uv(72, 10).cuboid(0.0F, -0.75F, -0.5F, 0.0F, 2.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, -1.5708F, 0.0F, 0.0F));

        ModelPartData Increment = Arm2.addChild("Increment", ModelPartBuilder.create().uv(14, 0).cuboid(-1.5F, -0.1667F, -1.5F, 3.0F, 0.0F, 3.0F, new Dilation(0.001F))
                .uv(35, 7).cuboid(-0.5F, -0.1667F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(32, 4).cuboid(-0.5F, -0.6667F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.25F))
                .uv(32, 2).cuboid(-0.25F, -1.9167F, 0.75F, 1.0F, 2.0F, 1.0F, new Dilation(-0.25F)), ModelTransform.pivot(19.0F, -1.3333F, -0.1F));

        ModelPartData Arm3 = Arms.addChild("Arm3", ModelPartBuilder.create().uv(197, 92).cuboid(14.0F, -1.0F, -1.1F, 19.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, -2.7202F, -0.9909F, 2.6496F));

        ModelPartData cube_r79 = Arm3.addChild("cube_r79", ModelPartBuilder.create().uv(164, 89).cuboid(-1.0F, -2.0F, -1.0F, 2.0F, 4.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(32.5258F, 1.0941F, -0.1F, 0.0F, 0.0F, -0.2618F));

        ModelPartData cube_r80 = Arm3.addChild("cube_r80", ModelPartBuilder.create().uv(0, 66).cuboid(-4.0F, -2.0F, -1.1F, 5.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(14.134F, 1.2321F, 0.0F, 0.0F, 0.0F, -0.5236F));

        ModelPartData cube_r81 = Arm3.addChild("cube_r81", ModelPartBuilder.create().uv(236, 194).cuboid(-1.0F, -3.0F, -1.1F, 10.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(17.0549F, 11.4011F, -0.2F, 3.1416F, 0.0F, 1.0472F));

        ModelPartData cube_r82 = Arm3.addChild("cube_r82", ModelPartBuilder.create().uv(189, 188).cuboid(-1.0F, -2.0F, -1.1F, 20.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(33.1434F, 3.2671F, -0.2F, 3.1416F, 0.0F, 2.618F));

        ModelPartData Arm4 = Arms.addChild("Arm4", ModelPartBuilder.create().uv(197, 87).cuboid(14.0F, -1.0F, -1.1F, 19.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 3.1416F, 0.0F, 2.8798F));

        ModelPartData cube_r83 = Arm4.addChild("cube_r83", ModelPartBuilder.create().uv(164, 89).cuboid(-1.0F, -2.0F, -1.0F, 2.0F, 4.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(32.5258F, 1.0941F, -0.1F, 0.0F, 0.0F, -0.2618F));

        ModelPartData cube_r84 = Arm4.addChild("cube_r84", ModelPartBuilder.create().uv(0, 48).cuboid(-4.0F, -2.0F, -1.1F, 5.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(14.134F, 1.2321F, 0.0F, 0.0F, 0.0F, -0.5236F));

        ModelPartData cube_r85 = Arm4.addChild("cube_r85", ModelPartBuilder.create().uv(236, 157).cuboid(-1.0F, -3.0F, -1.1F, 10.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(17.0549F, 11.4011F, -0.2F, 3.1416F, 0.0F, 1.0472F));

        ModelPartData cube_r86 = Arm4.addChild("cube_r86", ModelPartBuilder.create().uv(180, 29).cuboid(-1.0F, -2.0F, -1.1F, 20.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(33.1434F, 3.2671F, -0.2F, 3.1416F, 0.0F, 2.618F));

        ModelPartData Arm5 = Arms.addChild("Arm5", ModelPartBuilder.create().uv(197, 5).cuboid(14.0F, -1.0F, -1.1F, 19.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 2.7202F, 0.9909F, 2.6496F));

        ModelPartData cube_r87 = Arm5.addChild("cube_r87", ModelPartBuilder.create().uv(164, 89).cuboid(-1.0F, -2.0F, -1.0F, 2.0F, 4.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(32.5258F, 1.0941F, -0.1F, 0.0F, 0.0F, -0.2618F));

        ModelPartData cube_r88 = Arm5.addChild("cube_r88", ModelPartBuilder.create().uv(0, 30).cuboid(-4.0F, -2.0F, -1.1F, 5.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(14.134F, 1.2321F, 0.0F, 0.0F, 0.0F, -0.5236F));

        ModelPartData cube_r89 = Arm5.addChild("cube_r89", ModelPartBuilder.create().uv(236, 140).cuboid(-1.0F, -3.0F, -1.1F, 10.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(17.0549F, 11.4011F, -0.2F, 3.1416F, 0.0F, 1.0472F));

        ModelPartData cube_r90 = Arm5.addChild("cube_r90", ModelPartBuilder.create().uv(179, 48).cuboid(-1.0F, -2.0F, -1.1F, 20.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(33.1434F, 3.2671F, -0.2F, 3.1416F, 0.0F, 2.618F));

        ModelPartData ExteriorFacing = Arm5.addChild("ExteriorFacing", ModelPartBuilder.create().uv(14, 0).cuboid(-1.5F, -0.1667F, -1.5F, 3.0F, 0.0F, 3.0F, new Dilation(0.001F))
                .uv(35, 7).cuboid(-0.5F, -0.1667F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(32, 4).cuboid(-0.5F, -0.6667F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.25F))
                .uv(32, 2).cuboid(-0.25F, -1.9167F, 0.75F, 1.0F, 2.0F, 1.0F, new Dilation(-0.25F)), ModelTransform.of(29.25F, -1.3333F, -0.1F, 0.0F, -1.5708F, 0.0F));

        ModelPartData Arm6 = Arms.addChild("Arm6", ModelPartBuilder.create().uv(197, 0).cuboid(14.0F, -1.0F, -1.1F, 19.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.4214F, 0.9909F, 0.492F));

        ModelPartData cube_r91 = Arm6.addChild("cube_r91", ModelPartBuilder.create().uv(164, 89).cuboid(-1.0F, -2.0F, -1.0F, 2.0F, 4.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(32.5258F, 1.0941F, -0.1F, 0.0F, 0.0F, -0.2618F));

        ModelPartData cube_r92 = Arm6.addChild("cube_r92", ModelPartBuilder.create().uv(0, 12).cuboid(-4.0F, -2.0F, -1.1F, 5.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(14.134F, 1.2321F, 0.0F, 0.0F, 0.0F, -0.5236F));

        ModelPartData cube_r93 = Arm6.addChild("cube_r93", ModelPartBuilder.create().uv(234, 189).cuboid(-1.0F, -3.0F, -1.1F, 10.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(17.0549F, 11.4011F, -0.2F, 3.1416F, 0.0F, 1.0472F));

        ModelPartData cube_r94 = Arm6.addChild("cube_r94", ModelPartBuilder.create().uv(178, 135).cuboid(-1.0F, -2.0F, -1.1F, 20.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(33.1434F, 3.2671F, -0.2F, 3.1416F, 0.0F, 2.618F));

        ModelPartData Cables = Root.addChild("Cables", ModelPartBuilder.create(), ModelTransform.pivot(21.6277F, -8.7474F, 12.6961F));

        ModelPartData Cable_r11 = Cables.addChild("Cable_r11", ModelPartBuilder.create().uv(68, 232).cuboid(0.0F, -4.5F, -6.5F, 0.0F, 9.0F, 13.0F, new Dilation(0.001F)), ModelTransform.of(-15.1055F, 0.5F, 0.2925F, 0.0F, -0.6545F, 0.0F));

        ModelPartData Cable_r12 = Cables.addChild("Cable_r12", ModelPartBuilder.create().uv(175, 219).cuboid(0.0F, -4.5F, -6.5F, 0.0F, 9.0F, 13.0F, new Dilation(0.001F)), ModelTransform.of(-25.5318F, 0.25F, -36.2804F, 0.0F, 1.789F, 0.0F));

        ModelPartData Cable_r13 = Cables.addChild("Cable_r13", ModelPartBuilder.create().uv(68, 232).cuboid(0.0F, -4.5F, -6.5F, 0.0F, 9.0F, 13.0F, new Dilation(0.001F)), ModelTransform.of(-39.4732F, 0.75F, 2.7279F, 0.0F, 0.7418F, 0.0F));

        ModelPartData Cable_r14 = Cables.addChild("Cable_r14", ModelPartBuilder.create().uv(175, 188).cuboid(0.0F, -4.5F, -6.5F, 0.0F, 9.0F, 13.0F, new Dilation(0.001F)), ModelTransform.of(-29.5895F, 3.0F, 1.2521F, 0.0F, 1.0472F, 0.0F));

        ModelPartData Cable_r15 = Cables.addChild("Cable_r15", ModelPartBuilder.create().uv(175, 188).cuboid(0.0F, -4.5F, -6.5F, 0.0F, 9.0F, 13.0F, new Dilation(0.001F)), ModelTransform.of(-39.8498F, 1.25F, -25.4476F, 0.0F, 2.5744F, 0.0F));

        ModelPartData Cable_r16 = Cables.addChild("Cable_r16", ModelPartBuilder.create().uv(68, 232).cuboid(0.0F, -3.5F, -7.5F, 0.0F, 9.0F, 13.0F, new Dilation(0.001F)), ModelTransform.of(-13.4675F, 0.5F, -31.529F, 0.0F, 1.789F, 0.0F));

        ModelPartData Cable_r17 = Cables.addChild("Cable_r17", ModelPartBuilder.create().uv(175, 188).cuboid(0.0F, -4.5F, -6.5F, 0.0F, 9.0F, 13.0F, new Dilation(0.001F)), ModelTransform.of(-2.8505F, 1.0F, -25.0991F, 0.0F, 0.6981F, 0.0F));

        ModelPartData Cable_r18 = Cables.addChild("Cable_r18", ModelPartBuilder.create().uv(175, 188).cuboid(0.0F, -4.5F, -6.5F, 0.0F, 9.0F, 13.0F, new Dilation(0.001F)), ModelTransform.of(-0.3481F, 0.5F, -5.3971F, 0.0F, -0.5236F, 0.0F));

        ModelPartData Cable_r19 = Cables.addChild("Cable_r19", ModelPartBuilder.create().uv(197, 56).cuboid(0.0F, -4.5F, -6.5F, 0.0F, 9.0F, 13.0F, new Dilation(0.001F)), ModelTransform.of(-22.2648F, 0.5F, 11.6834F, 0.0F, -1.5708F, 0.0F));

        ModelPartData Cable_r20 = Cables.addChild("Cable_r20", ModelPartBuilder.create().uv(197, 56).cuboid(4.0F, -6.5F, -9.5F, 0.0F, 9.0F, 13.0F, new Dilation(0.001F)), ModelTransform.of(-16.125F, 2.5F, 3.506F, 0.0F, -1.309F, 0.0F));

        ModelPartData Cable_r21 = Cables.addChild("Cable_r21", ModelPartBuilder.create().uv(202, 61).cuboid(0.0F, -4.5F, -6.75F, 0.0F, 9.0F, 8.0F, new Dilation(0.001F)), ModelTransform.of(-19.5344F, 2.0F, -0.5249F, 0.0F, 3.0543F, 0.0F));

        ModelPartData Cable_r22 = Cables.addChild("Cable_r22", ModelPartBuilder.create().uv(181, 224).cuboid(0.0F, -4.5F, 0.5F, 0.0F, 9.0F, 8.0F, new Dilation(0.001F)), ModelTransform.of(-38.8645F, 2.0F, -4.5038F, 0.0F, 2.0944F, 0.0F));

        ModelPartData Cable_r23 = Cables.addChild("Cable_r23", ModelPartBuilder.create().uv(6, 180).cuboid(-4.25F, -5.0F, -1.5F, 1.0F, 9.0F, 8.0F, new Dilation(0.001F)), ModelTransform.of(-33.6857F, 2.0F, -25.4739F, 0.0F, 1.0472F, 0.0F));

        ModelPartData Cable_r24 = Cables.addChild("Cable_r24", ModelPartBuilder.create().uv(181, 224).cuboid(0.0F, -4.5F, -4.0F, 0.0F, 9.0F, 8.0F, new Dilation(0.001F)), ModelTransform.of(-26.002F, 2.0F, -26.4854F, 0.0F, 0.1309F, 0.0F));

        ModelPartData Cable_r25 = Cables.addChild("Cable_r25", ModelPartBuilder.create().uv(202, 61).cuboid(0.0F, -4.5F, -4.0F, 0.0F, 9.0F, 8.0F, new Dilation(0.001F)), ModelTransform.of(-7.5507F, 2.0F, -15.802F, 0.0F, -0.8727F, 0.0F));

        ModelPartData Cable_r26 = Cables.addChild("Cable_r26", ModelPartBuilder.create().uv(197, 56).cuboid(0.0F, -4.5F, -6.5F, 0.0F, 9.0F, 13.0F, new Dilation(0.001F)), ModelTransform.of(-4.7686F, 2.5F, -11.5616F, 0.0F, -0.0873F, 0.0F));

        ModelPartData Cable_r27 = Cables.addChild("Cable_r27", ModelPartBuilder.create().uv(0, 198).cuboid(-0.75F, -4.0F, -6.5F, 0.0F, 8.0F, 13.0F, new Dilation(0.001F)), ModelTransform.of(-21.6235F, 2.75F, -27.9629F, 0.0F, 1.4399F, 0.0F));

        ModelPartData Cable_r28 = Cables.addChild("Cable_r28", ModelPartBuilder.create().uv(0, 198).cuboid(0.0F, -4.25F, -6.5F, 0.0F, 8.0F, 13.0F, new Dilation(0.001F)), ModelTransform.of(-44.4464F, 0.25F, -20.9324F, 0.0F, 2.7925F, 0.0F));

        ModelPartData Cable_r29 = Cables.addChild("Cable_r29", ModelPartBuilder.create().uv(0, 198).cuboid(0.0F, -4.0F, -6.5F, 0.0F, 8.0F, 13.0F, new Dilation(0.001F)), ModelTransform.of(-10.4297F, 0.75F, -27.851F, 0.0F, 1.0472F, 0.0F));

        ModelPartData Cable_r30 = Cables.addChild("Cable_r30", ModelPartBuilder.create().uv(0, 198).cuboid(0.0F, -4.0F, -6.5F, 0.0F, 8.0F, 13.0F, new Dilation(0.001F)), ModelTransform.of(-8.4584F, 3.0F, -5.799F, 0.0F, -0.7854F, 0.0F));

        ModelPartData Cable_r31 = Cables.addChild("Cable_r31", ModelPartBuilder.create().uv(258, 224).cuboid(0.0F, -4.5F, -4.0F, 0.0F, 9.0F, 8.0F, new Dilation(0.001F)), ModelTransform.of(-25.9354F, 1.25F, -31.9378F, 0.0F, 1.3963F, 0.0F));

        ModelPartData Cable_r32 = Cables.addChild("Cable_r32", ModelPartBuilder.create().uv(258, 224).cuboid(0.0F, -4.5F, -4.0F, 0.0F, 9.0F, 8.0F, new Dilation(0.001F)), ModelTransform.of(-15.0241F, 0.0F, -35.3731F, 0.0F, 1.4835F, 0.0F));

        ModelPartData Cable_r33 = Cables.addChild("Cable_r33", ModelPartBuilder.create().uv(258, 224).cuboid(2.75F, -5.75F, -4.0F, 0.0F, 9.0F, 8.0F, new Dilation(0.001F)), ModelTransform.of(-41.0813F, 3.25F, -12.1173F, 0.0F, 0.0873F, 0.0F));

        ModelPartData Cable_r34 = Cables.addChild("Cable_r34", ModelPartBuilder.create().uv(258, 224).cuboid(0.0F, -4.5F, -4.0F, 0.0F, 9.0F, 8.0F, new Dilation(0.001F)), ModelTransform.of(-9.4622F, 3.25F, -21.4604F, 0.0F, 0.829F, 0.0F));

        ModelPartData Cable_r35 = Cables.addChild("Cable_r35", ModelPartBuilder.create().uv(258, 215).cuboid(0.0F, -4.5F, -4.0F, 0.0F, 9.0F, 8.0F, new Dilation(0.001F)), ModelTransform.of(-1.7935F, 1.75F, -18.6646F, 0.0F, 0.3491F, 0.0F));

        ModelPartData Cable_r36 = Cables.addChild("Cable_r36", ModelPartBuilder.create().uv(258, 215).cuboid(-1.0F, -4.5F, -4.0F, 0.0F, 9.0F, 8.0F, new Dilation(0.001F)), ModelTransform.of(-32.7803F, 3.0F, -22.9239F, 0.0F, -0.7418F, 0.0F));

        ModelPartData Cable_r37 = Cables.addChild("Cable_r37", ModelPartBuilder.create().uv(258, 215).cuboid(-1.25F, -4.5F, -4.0F, 0.0F, 9.0F, 8.0F, new Dilation(0.001F)), ModelTransform.of(-47.2801F, 0.5F, -6.8191F, 0.0F, -2.3126F, 0.0F));

        ModelPartData Cable_r38 = Cables.addChild("Cable_r38", ModelPartBuilder.create().uv(258, 215).cuboid(0.0F, -4.5F, -4.0F, 0.0F, 9.0F, 8.0F, new Dilation(0.001F)), ModelTransform.of(-34.4945F, 3.25F, -6.5269F, 0.0F, -2.618F, 0.0F));

        ModelPartData Cable_r39 = Cables.addChild("Cable_r39", ModelPartBuilder.create().uv(258, 215).cuboid(0.0F, -4.5F, -4.0F, 0.0F, 9.0F, 8.0F, new Dilation(0.001F)), ModelTransform.of(-18.4945F, 3.25F, 3.4731F, 0.0F, -1.5708F, 0.0F));

        ModelPartData Cable_r40 = Cables.addChild("Cable_r40", ModelPartBuilder.create().uv(189, 198).cuboid(0.0F, -4.0F, -6.5F, 0.0F, 8.0F, 13.0F, new Dilation(0.001F)), ModelTransform.of(-28.8542F, 1.5F, 5.7084F, 0.0F, -1.7017F, 0.0F));

        ModelPartData Cable_r41 = Cables.addChild("Cable_r41", ModelPartBuilder.create().uv(189, 198).cuboid(0.0F, -4.0F, -6.5F, 0.0F, 8.0F, 13.0F, new Dilation(0.001F)), ModelTransform.of(-6.7164F, 0.0F, 6.6504F, 0.0F, -0.7418F, 0.0F));

        ModelPartData Cable_r42 = Cables.addChild("Cable_r42", ModelPartBuilder.create().uv(0, 174).cuboid(0.0F, -3.25F, -7.5F, 0.0F, 8.0F, 15.0F, new Dilation(0.001F)), ModelTransform.of(-38.0909F, 1.0F, -3.2995F, 0.0F, 0.6109F, 0.0F));

        ModelPartData Cable_r43 = Cables.addChild("Cable_r43", ModelPartBuilder.create().uv(0, 174).cuboid(0.0F, -4.0F, -7.5F, 0.0F, 8.0F, 15.0F, new Dilation(0.001F)), ModelTransform.of(-42.8109F, 1.0F, -11.3522F, 0.0F, 0.1745F, 0.0F));

        ModelPartData Cable_r44 = Cables.addChild("Cable_r44", ModelPartBuilder.create().uv(67, 243).cuboid(0.0F, -4.0F, -7.5F, 0.0F, 8.0F, 15.0F, new Dilation(0.001F)), ModelTransform.of(-34.0472F, 3.0F, -20.89F, 0.0F, -0.3054F, 0.0F));

        ModelPartData Cable_r45 = Cables.addChild("Cable_r45", ModelPartBuilder.create().uv(0, 174).cuboid(0.0F, -4.0F, -7.5F, 0.0F, 8.0F, 15.0F, new Dilation(0.001F)), ModelTransform.of(-30.8535F, 1.0F, -29.6467F, 0.0F, -1.0472F, 0.0F));

        ModelPartData Cable_r46 = Cables.addChild("Cable_r46", ModelPartBuilder.create().uv(0, 174).cuboid(0.0F, -4.0F, -7.5F, 0.0F, 8.0F, 15.0F, new Dilation(0.001F)), ModelTransform.of(-4.872F, 1.5F, -2.84F, 0.0F, -0.1309F, 0.0F));

        ModelPartData Panels = Root.addChild("Panels", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, -16.0F, 0.0F));

        ModelPartData PanelA = Panels.addChild("PanelA", ModelPartBuilder.create().uv(113, 150).cuboid(-15.0F, 0.0F, 25.5F, 30.0F, 1.0F, 2.0F, new Dilation(0.0F))
                .uv(113, 146).cuboid(-15.0F, 2.0F, 25.5F, 30.0F, 1.0F, 2.0F, new Dilation(0.0F))
                .uv(163, 46).cuboid(-15.0F, 1.0F, 26.5F, 30.0F, 1.0F, 0.0F, new Dilation(0.0F))
                .uv(204, 112).cuboid(8.75F, -4.5279F, 20.6201F, 1.5F, 3.0F, 1.5F, new Dilation(0.0F))
                .uv(15, 9).cuboid(8.5F, -1.5279F, 20.3701F, 2.0F, 1.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData cube_r95 = PanelA.addChild("cube_r95", ModelPartBuilder.create().uv(243, 101).cuboid(-6.0F, 0.0F, -11.5F, 12.0F, 0.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -2.0F, 0.0F, 0.0F, 3.1416F, 0.0F));

        ModelPartData Panel_r1 = PanelA.addChild("Panel_r1", ModelPartBuilder.create().uv(80, 36).cuboid(-16.0F, 0.0F, -28.6F, 32.0F, 0.0F, 16.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, -7.0F, 0.0F, -2.8798F, 0.0F, 3.1416F));

        ModelPartData PanelPitEdge_r1 = PanelA.addChild("PanelPitEdge_r1", ModelPartBuilder.create().uv(0, 233).cuboid(-6.5F, -1.0F, -0.5F, 13.0F, 3.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -3.4233F, 12.0858F, -0.3927F, 0.0F, 0.0F));

        ModelPartData PanelBottomGrill = PanelA.addChild("PanelBottomGrill", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 7.3113F, 11.2405F));

        ModelPartData PanelBottomBit_r1 = PanelBottomGrill.addChild("PanelBottomBit_r1", ModelPartBuilder.create().uv(35, 68).cuboid(-6.0F, -0.5F, -1.0F, 12.0F, 1.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.7418F, 0.0F, 0.0F));

        ModelPartData PanelBottomBit_r2 = PanelBottomGrill.addChild("PanelBottomBit_r2", ModelPartBuilder.create().uv(41, 68).cuboid(-7.0F, -0.5F, -1.0F, 14.0F, 1.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -0.4511F, 1.4306F, 0.7418F, 0.0F, 0.0F));

        ModelPartData PanelBottomBit_r3 = PanelBottomGrill.addChild("PanelBottomBit_r3", ModelPartBuilder.create().uv(38, 68).cuboid(-8.0F, -0.5F, -1.0F, 16.0F, 1.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -0.9021F, 2.8612F, 0.7418F, 0.0F, 0.0F));

        ModelPartData PanelBottomBit_r4 = PanelBottomGrill.addChild("PanelBottomBit_r4", ModelPartBuilder.create().uv(35, 68).cuboid(-9.0F, -0.5F, -1.0F, 18.0F, 1.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -1.3532F, 4.2917F, 0.7418F, 0.0F, 0.0F));

        ModelPartData PanelBottomBit_r5 = PanelBottomGrill.addChild("PanelBottomBit_r5", ModelPartBuilder.create().uv(32, 68).cuboid(-10.0F, -0.5F, -1.0F, 20.0F, 1.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -1.8042F, 5.7223F, 0.7418F, 0.0F, 0.0F));

        ModelPartData PanelBottomBit_r6 = PanelBottomGrill.addChild("PanelBottomBit_r6", ModelPartBuilder.create().uv(29, 68).cuboid(-11.0F, -0.5F, -1.0F, 22.0F, 1.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -2.2553F, 7.1529F, 0.7418F, 0.0F, 0.0F));

        ModelPartData PanelBottomBit_r7 = PanelBottomGrill.addChild("PanelBottomBit_r7", ModelPartBuilder.create().uv(26, 68).cuboid(-12.0F, -0.5F, -1.0F, 24.0F, 1.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -2.7064F, 8.5834F, 0.7418F, 0.0F, 0.0F));

        ModelPartData PanelBottomBit_r8 = PanelBottomGrill.addChild("PanelBottomBit_r8", ModelPartBuilder.create().uv(23, 68).cuboid(-13.0F, -0.5F, -1.0F, 26.0F, 1.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -3.1574F, 10.014F, 0.7418F, 0.0F, 0.0F));

        ModelPartData PanelBottomBit_r9 = PanelBottomGrill.addChild("PanelBottomBit_r9", ModelPartBuilder.create().uv(20, 68).cuboid(-14.0F, -0.5F, -1.0F, 28.0F, 1.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -3.6085F, 11.4446F, 0.7418F, 0.0F, 0.0F));

        ModelPartData PanelBottomBit_r10 = PanelBottomGrill.addChild("PanelBottomBit_r10", ModelPartBuilder.create().uv(20, 68).cuboid(-14.0F, -0.5F, -1.0F, 28.0F, 1.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -4.0595F, 12.8752F, 0.7418F, 0.0F, 0.0F));

        ModelPartData PanelBottomBit_r11 = PanelBottomGrill.addChild("PanelBottomBit_r11", ModelPartBuilder.create().uv(17, 68).cuboid(-15.0F, -0.5F, -1.0F, 30.0F, 1.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -4.5106F, 14.3057F, 0.7418F, 0.0F, 0.0F));

        ModelPartData empty = PanelA.addChild("empty", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, -1.5648F, 0.0F));

        ModelPartData empty2 = PanelA.addChild("empty2", ModelPartBuilder.create(), ModelTransform.of(0.0F, -1.5648F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData empty3 = PanelA.addChild("empty3", ModelPartBuilder.create(), ModelTransform.of(0.0F, -1.5648F, 0.0F, -3.1416F, 1.0472F, 3.1416F));

        ModelPartData empty4 = PanelA.addChild("empty4", ModelPartBuilder.create(), ModelTransform.of(0.0F, -1.5648F, 0.0F, -3.1416F, 0.0F, 3.1416F));

        ModelPartData empty5 = PanelA.addChild("empty5", ModelPartBuilder.create(), ModelTransform.of(0.0F, -1.5648F, 0.0F, -3.1416F, -1.0472F, 3.1416F));

        ModelPartData empty6 = PanelA.addChild("empty6", ModelPartBuilder.create(), ModelTransform.of(0.0F, -1.5648F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData KnobD = PanelA.addChild("KnobD", ModelPartBuilder.create(), ModelTransform.of(1.0F, -0.4815F, 24.2715F, -0.2618F, 0.0F, 0.0F));

        ModelPartData Knob_r1 = KnobD.addChild("Knob_r1", ModelPartBuilder.create().uv(59, 51).cuboid(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.5708F, 0.0F));

        ModelPartData Knob_r2 = KnobD.addChild("Knob_r2", ModelPartBuilder.create().uv(60, 53).cuboid(-0.25F, -0.75F, 0.0F, 1.0F, 1.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 3.1416F, 0.0F, 3.1416F));

        ModelPartData KnobE = PanelA.addChild("KnobE", ModelPartBuilder.create().uv(59, 51).cuboid(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-1.0F, -0.3521F, 24.7545F, -0.6109F, -1.1026F, 0.5585F));

        ModelPartData Knob_r3 = KnobE.addChild("Knob_r3", ModelPartBuilder.create().uv(60, 53).cuboid(-0.25F, -0.75F, 0.0F, 1.0F, 1.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 3.1416F, 0.0F, 3.1416F));

        ModelPartData KnobF = PanelA.addChild("KnobF", ModelPartBuilder.create(), ModelTransform.of(-1.5F, -0.9663F, 22.8485F, -1.5708F, 1.309F, -1.5708F));

        ModelPartData Knob_r4 = KnobF.addChild("Knob_r4", ModelPartBuilder.create().uv(59, 51).cuboid(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(60, 53).cuboid(-0.25F, -0.75F, 0.0F, 1.0F, 1.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 3.1416F, 0.0F, 3.1416F));

        ModelPartData KnobG = PanelA.addChild("KnobG", ModelPartBuilder.create(), ModelTransform.of(-3.5F, -0.7075F, 23.8144F, -2.5307F, -1.1026F, 2.5831F));

        ModelPartData Knob_r5 = KnobG.addChild("Knob_r5", ModelPartBuilder.create().uv(59, 51).cuboid(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -0.0873F, 0.0F));

        ModelPartData Knob_r6 = KnobG.addChild("Knob_r6", ModelPartBuilder.create().uv(60, 53).cuboid(-0.25F, -0.75F, 0.0F, 1.0F, 1.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 3.1416F, 0.0F, 3.1416F));

        ModelPartData KnobH = PanelA.addChild("KnobH", ModelPartBuilder.create(), ModelTransform.of(-2.5F, -1.3874F, 20.8908F, -2.5307F, 1.1026F, -2.5831F));

        ModelPartData Knob_r7 = KnobH.addChild("Knob_r7", ModelPartBuilder.create().uv(59, 51).cuboid(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 3.1416F, 0.0F));

        ModelPartData Knob_r8 = KnobH.addChild("Knob_r8", ModelPartBuilder.create().uv(60, 53).cuboid(-0.25F, -0.75F, 0.0F, 1.0F, 1.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 3.1416F, 0.0F, 3.1416F));

        ModelPartData PanelB = Panels.addChild("PanelB", ModelPartBuilder.create().uv(113, 150).cuboid(-15.0F, 0.0F, 25.5F, 30.0F, 1.0F, 2.0F, new Dilation(0.0F))
                .uv(113, 146).cuboid(-15.0F, 2.0F, 25.5F, 30.0F, 1.0F, 2.0F, new Dilation(0.0F))
                .uv(163, 46).cuboid(-15.0F, 1.0F, 26.5F, 30.0F, 1.0F, 0.0F, new Dilation(0.0F))
                .uv(15, 9).cuboid(6.5F, -0.7279F, -25.3299F, 2.0F, 1.0F, 2.0F, new Dilation(0.0F))
                .uv(224, 112).cuboid(6.75F, -3.7279F, -25.0799F, 1.5F, 3.0F, 1.5F, new Dilation(0.0F))
                .uv(210, 149).cuboid(-9.75F, -3.2279F, -25.0799F, 1.5F, 3.0F, 1.5F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData Slider_r1 = PanelB.addChild("Slider_r1", ModelPartBuilder.create().uv(41, 22).cuboid(-5.0F, -0.25F, -5.0F, 1.0F, 1.0F, 1.0F, new Dilation(-0.25F))
                .uv(44, 14).cuboid(-2.5F, -0.25F, -4.0F, 1.0F, 1.0F, 1.0F, new Dilation(-0.25F)), ModelTransform.of(3.25F, -2.8582F, 16.3673F, -2.8798F, 0.0F, 3.1416F));

        ModelPartData underPanel_r1 = PanelB.addChild("underPanel_r1", ModelPartBuilder.create().uv(82, 1).cuboid(-16.1F, 0.1683F, -7.8981F, 32.0F, 0.0F, 17.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 5.1317F, 18.8981F, 2.8362F, 0.0F, 3.1416F));

        ModelPartData cube_r96 = PanelB.addChild("cube_r96", ModelPartBuilder.create().uv(243, 101).cuboid(-6.0F, 0.0F, -11.5F, 12.0F, 0.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -2.0F, 0.0F, 0.0F, 3.1416F, 0.0F));

        ModelPartData Panel_r2 = PanelB.addChild("Panel_r2", ModelPartBuilder.create().uv(1, 31).cuboid(-16.0F, 0.0F, -28.6F, 32.0F, 0.0F, 16.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, -7.0F, 0.0F, -2.8798F, 0.0F, 3.1416F));

        ModelPartData PanelPitEdge_r2 = PanelB.addChild("PanelPitEdge_r2", ModelPartBuilder.create().uv(0, 254).cuboid(-6.5F, -1.0F, -0.5F, 13.0F, 3.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -3.4233F, 12.0858F, -0.3927F, 0.0F, 0.0F));

        ModelPartData CupHolder = PanelB.addChild("CupHolder", ModelPartBuilder.create().uv(66, 29).cuboid(-1.0F, -0.8F, 1.2F, 2.0F, 2.0F, 0.0F, new Dilation(0.001F))
                .uv(55, 26).cuboid(-1.0F, -0.8F, -3.8F, 2.0F, 1.0F, 3.0F, new Dilation(0.0F))
                .uv(63, 25).cuboid(1.0F, -0.8F, -0.8F, 0.0F, 1.0F, 2.0F, new Dilation(0.001F))
                .uv(70, 29).cuboid(-0.5F, 1.2F, 0.2F, 1.0F, 0.0F, 1.0F, new Dilation(0.001F))
                .uv(68, 25).cuboid(-1.0F, -0.8F, -0.8F, 0.0F, 1.0F, 2.0F, new Dilation(0.001F)), ModelTransform.pivot(7.5F, 1.8F, 28.8F));

        ModelPartData Cup = CupHolder.addChild("Cup", ModelPartBuilder.create().uv(64, 22).cuboid(-1.0F, -1.05F, -0.8F, 2.0F, 2.0F, 2.0F, new Dilation(-0.25F))
                .uv(73, 22).cuboid(-1.0F, -2.65F, -0.8F, 2.0F, 2.0F, 2.0F, new Dilation(-0.15F))
                .uv(72, 18).cuboid(-1.0F, -2.8F, -0.8F, 2.0F, 1.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.45F, 0.0F));

        ModelPartData CupHolder_r1 = Cup.addChild("CupHolder_r1", ModelPartBuilder.create().uv(74, 27).cuboid(-0.25F, -1.0F, 0.0F, 1.0F, 2.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -3.8F, 0.2F, 0.0F, -0.3927F, 0.0F));

        ModelPartData empty7 = PanelB.addChild("empty7", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, -1.5648F, 0.0F));

        ModelPartData empty8 = PanelB.addChild("empty8", ModelPartBuilder.create(), ModelTransform.of(0.0F, -1.5648F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData empty9 = PanelB.addChild("empty9", ModelPartBuilder.create(), ModelTransform.of(0.0F, -1.5648F, 0.0F, -3.1416F, 1.0472F, 3.1416F));

        ModelPartData emptyg = PanelB.addChild("emptyg", ModelPartBuilder.create(), ModelTransform.of(0.0F, -1.5648F, 0.0F, -3.1416F, 0.0F, 3.1416F));

        ModelPartData emptyt = PanelB.addChild("emptyt", ModelPartBuilder.create(), ModelTransform.of(0.0F, -1.5648F, 0.0F, -3.1416F, -1.0472F, 3.1416F));

        ModelPartData emptyh = PanelB.addChild("emptyh", ModelPartBuilder.create(), ModelTransform.of(0.0F, -1.5648F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData PanelBottomGrillb = PanelB.addChild("PanelBottomGrillb", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 7.3113F, 11.2405F));

        ModelPartData PanelBottomBit_r12 = PanelBottomGrillb.addChild("PanelBottomBit_r12", ModelPartBuilder.create().uv(35, 68).cuboid(-6.0F, -0.5F, -1.0F, 12.0F, 1.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.7418F, 0.0F, 0.0F));

        ModelPartData PanelBottomBit_r13 = PanelBottomGrillb.addChild("PanelBottomBit_r13", ModelPartBuilder.create().uv(41, 68).cuboid(-7.0F, -0.5F, -1.0F, 14.0F, 1.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -0.4511F, 1.4306F, 0.7418F, 0.0F, 0.0F));

        ModelPartData PanelBottomBit_r14 = PanelBottomGrillb.addChild("PanelBottomBit_r14", ModelPartBuilder.create().uv(38, 68).cuboid(-8.0F, -0.5F, -1.0F, 16.0F, 1.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -0.9021F, 2.8612F, 0.7418F, 0.0F, 0.0F));

        ModelPartData PanelBottomBit_r15 = PanelBottomGrillb.addChild("PanelBottomBit_r15", ModelPartBuilder.create().uv(35, 68).cuboid(-9.0F, -0.5F, -1.0F, 18.0F, 1.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -1.3532F, 4.2917F, 0.7418F, 0.0F, 0.0F));

        ModelPartData PanelBottomBit_r16 = PanelBottomGrillb.addChild("PanelBottomBit_r16", ModelPartBuilder.create().uv(32, 68).cuboid(-10.0F, -0.5F, -1.0F, 20.0F, 1.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -1.8042F, 5.7223F, 0.7418F, 0.0F, 0.0F));

        ModelPartData PanelBottomBit_r17 = PanelBottomGrillb.addChild("PanelBottomBit_r17", ModelPartBuilder.create().uv(29, 68).cuboid(-11.0F, -0.5F, -1.0F, 22.0F, 1.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -2.2553F, 7.1529F, 0.7418F, 0.0F, 0.0F));

        ModelPartData PanelBottomBit_r18 = PanelBottomGrillb.addChild("PanelBottomBit_r18", ModelPartBuilder.create().uv(26, 68).cuboid(-12.0F, -0.5F, -1.0F, 24.0F, 1.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -2.7064F, 8.5834F, 0.7418F, 0.0F, 0.0F));

        ModelPartData PanelBottomBit_r19 = PanelBottomGrillb.addChild("PanelBottomBit_r19", ModelPartBuilder.create().uv(23, 68).cuboid(-13.0F, -0.5F, -1.0F, 26.0F, 1.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -3.1574F, 10.014F, 0.7418F, 0.0F, 0.0F));

        ModelPartData PanelBottomBit_r20 = PanelBottomGrillb.addChild("PanelBottomBit_r20", ModelPartBuilder.create().uv(20, 68).cuboid(-14.0F, -0.5F, -1.0F, 28.0F, 1.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -3.6085F, 11.4446F, 0.7418F, 0.0F, 0.0F));

        ModelPartData PanelBottomBit_r21 = PanelBottomGrillb.addChild("PanelBottomBit_r21", ModelPartBuilder.create().uv(20, 68).cuboid(-14.0F, -0.5F, -1.0F, 28.0F, 1.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -4.0595F, 12.8752F, 0.7418F, 0.0F, 0.0F));

        ModelPartData PanelBottomBit_r22 = PanelBottomGrillb.addChild("PanelBottomBit_r22", ModelPartBuilder.create().uv(17, 68).cuboid(-15.0F, -0.5F, -1.0F, 30.0F, 1.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -4.5106F, 14.3057F, 0.7418F, 0.0F, 0.0F));

        ModelPartData KnobL_Doors = PanelB.addChild("KnobL_Doors", ModelPartBuilder.create(), ModelTransform.of(5.5F, -2.2932F, 17.51F, -0.2618F, 0.0F, 0.0F));

        ModelPartData Knob_r9 = KnobL_Doors.addChild("Knob_r9", ModelPartBuilder.create().uv(59, 51).cuboid(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 3.1416F, 0.0F, 3.1416F));

        ModelPartData Knob_r10 = KnobL_Doors.addChild("Knob_r10", ModelPartBuilder.create().uv(60, 53).cuboid(-0.25F, -0.75F, 0.0F, 1.0F, 1.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.5708F, 0.0F));

        ModelPartData KnobM = PanelB.addChild("KnobM", ModelPartBuilder.create(), ModelTransform.of(5.5F, -3.0697F, 14.6123F, -2.5307F, 1.1026F, -2.5831F));

        ModelPartData Knob_r11 = KnobM.addChild("Knob_r11", ModelPartBuilder.create().uv(59, 51).cuboid(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(60, 53).cuboid(-0.25F, -0.75F, 0.0F, 1.0F, 1.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 3.1416F, 0.0F, 3.1416F));

        ModelPartData KnobN = PanelB.addChild("KnobN", ModelPartBuilder.create(), ModelTransform.of(3.5F, -2.9075F, 15.6041F, -0.3622F, 0.7519F, -0.2533F));

        ModelPartData Knob_r12 = KnobN.addChild("Knob_r12", ModelPartBuilder.create().uv(59, 51).cuboid(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(60, 53).cuboid(-0.25F, -0.75F, 0.0F, 1.0F, 1.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 3.1416F, 0.0F, 3.1416F));

        ModelPartData KnobO = PanelB.addChild("KnobO", ModelPartBuilder.create(), ModelTransform.of(7.0F, -2.6815F, 16.0611F, -0.2823F, 0.3786F, -0.1068F));

        ModelPartData Knob_r13 = KnobO.addChild("Knob_r13", ModelPartBuilder.create().uv(59, 51).cuboid(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.5708F, 0.0F));

        ModelPartData Knob_r14 = KnobO.addChild("Knob_r14", ModelPartBuilder.create().uv(60, 53).cuboid(-0.25F, -0.75F, 0.0F, 1.0F, 1.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 3.1416F, 0.0F, 3.1416F));

        ModelPartData KnobP = PanelB.addChild("KnobP", ModelPartBuilder.create(), ModelTransform.of(8.0F, -2.2604F, 18.0189F, -1.5708F, 1.309F, -1.5708F));

        ModelPartData Knob_r15 = KnobP.addChild("Knob_r15", ModelPartBuilder.create().uv(59, 51).cuboid(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(60, 53).cuboid(-0.25F, -0.75F, 0.0F, 1.0F, 1.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 3.1416F, 0.0F, 3.1416F));

        ModelPartData PanelC = Panels.addChild("PanelC", ModelPartBuilder.create().uv(113, 150).cuboid(-15.0F, 0.0F, 25.5F, 30.0F, 1.0F, 2.0F, new Dilation(0.0F))
                .uv(113, 146).cuboid(-15.0F, 2.0F, 25.5F, 30.0F, 1.0F, 2.0F, new Dilation(0.0F))
                .uv(163, 46).cuboid(-15.0F, 1.0F, 26.5F, 30.0F, 1.0F, 0.0F, new Dilation(0.0F))
                .uv(204, 107).cuboid(3.5F, -7.0279F, -14.5799F, 1.5F, 3.0F, 1.5F, new Dilation(0.0F))
                .uv(15, 9).cuboid(3.25F, -4.0279F, -14.8299F, 2.0F, 1.0F, 2.0F, new Dilation(0.0F))
                .uv(15, 9).cuboid(-5.25F, -4.0279F, -14.8299F, 2.0F, 1.0F, 2.0F, new Dilation(0.0F))
                .uv(204, 107).cuboid(-5.0F, -7.0279F, -14.5799F, 1.5F, 3.0F, 1.5F, new Dilation(0.0F))
                .uv(15, 9).cuboid(-7.75F, -2.7029F, -18.0049F, 2.0F, 1.0F, 2.0F, new Dilation(0.0F))
                .uv(210, 149).cuboid(-7.5F, -5.7029F, -17.7549F, 1.5F, 3.0F, 1.5F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -2.0944F, 0.0F));

        ModelPartData PanelPitEdge_r3 = PanelC.addChild("PanelPitEdge_r3", ModelPartBuilder.create().uv(0, 233).cuboid(-6.5F, -1.0F, -0.5F, 13.0F, 3.0F, 1.0F, new Dilation(0.0F))
                .uv(0, 250).cuboid(-6.5F, -1.0F, -0.5F, 13.0F, 3.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -3.4233F, 12.0858F, -0.3927F, 0.0F, 0.0F));

        ModelPartData underPanel_r2 = PanelC.addChild("underPanel_r2", ModelPartBuilder.create().uv(82, 1).cuboid(-16.1F, 0.1683F, -7.8981F, 32.0F, 0.0F, 17.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 5.1317F, 18.8981F, 2.8362F, 0.0F, 3.1416F));

        ModelPartData cube_r97 = PanelC.addChild("cube_r97", ModelPartBuilder.create().uv(243, 101).cuboid(-6.0F, 0.0F, -11.5F, 12.0F, 0.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -2.0F, 0.0F, 0.0F, 3.1416F, 0.0F));

        ModelPartData whirlygigcables_r1 = PanelC.addChild("whirlygigcables_r1", ModelPartBuilder.create().uv(60, 55).cuboid(-8.75F, 0.25F, -24.35F, 7.0F, 0.0F, 7.0F, new Dilation(0.001F))
                .uv(60, 48).cuboid(-8.75F, 0.75F, -24.35F, 7.0F, 0.0F, 7.0F, new Dilation(0.001F))
                .uv(80, 53).cuboid(-16.0F, 0.0F, -28.6F, 32.0F, 0.0F, 16.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, -7.0F, 0.0F, -2.8798F, 0.0F, 3.1416F));

        ModelPartData WireRed = PanelC.addChild("WireRed", ModelPartBuilder.create(), ModelTransform.pivot(4.75F, 0.0129F, 27.651F));

        ModelPartData Panel3 = WireRed.addChild("Panel3", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData Panel_r3 = Panel3.addChild("Panel_r3", ModelPartBuilder.create().uv(50, 26).cuboid(-0.75F, 0.0F, -1.0F, 1.0F, 0.0F, 1.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, -0.3871F, -2.9233F, 3.0107F, 0.0F, 3.1416F));

        ModelPartData Panel2 = Panel3.addChild("Panel2", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData Panel_r4 = Panel2.addChild("Panel_r4", ModelPartBuilder.create().uv(49, 27).cuboid(-0.75F, 0.0F, -2.0F, 1.0F, 0.0F, 2.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, -0.5176F, -1.9319F, -2.8798F, 0.0F, 3.1416F));

        ModelPartData Panel = Panel2.addChild("Panel", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData Panel_r5 = Panel.addChild("Panel_r5", ModelPartBuilder.create().uv(50, 30).cuboid(-0.75F, 0.0F, -1.0F, 1.0F, 0.0F, 1.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 0.5F, 0.866F, -2.3562F, 0.0F, 3.1416F));

        ModelPartData Panel_r6 = Panel.addChild("Panel_r6", ModelPartBuilder.create().uv(50, 29).cuboid(-0.75F, 0.0F, -1.0F, 1.0F, 0.0F, 1.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 0.0F, 0.0F, -2.618F, 0.0F, 3.1416F));

        ModelPartData WireYelo = PanelC.addChild("WireYelo", ModelPartBuilder.create(), ModelTransform.pivot(4.0F, 0.3129F, 27.901F));

        ModelPartData Panel4 = WireYelo.addChild("Panel4", ModelPartBuilder.create(), ModelTransform.of(0.0F, -0.6952F, -2.038F, 0.0436F, 0.0F, 0.0F));

        ModelPartData Panel_r7 = Panel4.addChild("Panel_r7", ModelPartBuilder.create().uv(48, 26).cuboid(-0.75F, 0.0F, -1.0F, 1.0F, 0.0F, 1.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 0.1129F, -0.9233F, 3.0107F, 0.0F, 3.1416F));

        ModelPartData Panel5 = Panel4.addChild("Panel5", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.5F, 2.0F));

        ModelPartData Panel_r8 = Panel5.addChild("Panel_r8", ModelPartBuilder.create().uv(47, 27).cuboid(-0.75F, 0.0F, -2.0F, 1.0F, 0.0F, 2.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, -0.5176F, -1.9319F, -2.8798F, 0.0F, 3.1416F));

        ModelPartData Panel6 = Panel5.addChild("Panel6", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData Panel_r9 = Panel6.addChild("Panel_r9", ModelPartBuilder.create().uv(48, 30).cuboid(-0.75F, 0.0F, -1.0F, 1.0F, 0.0F, 1.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 0.5F, 0.866F, -2.1817F, 0.0F, 3.1416F));

        ModelPartData Panel_r10 = Panel6.addChild("Panel_r10", ModelPartBuilder.create().uv(48, 29).cuboid(-0.75F, 0.0F, -1.0F, 1.0F, 0.0F, 1.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 0.0F, 0.0F, -2.618F, 0.0F, 3.1416F));

        ModelPartData WireBlue = PanelC.addChild("WireBlue", ModelPartBuilder.create(), ModelTransform.of(3.0F, -0.3371F, 24.651F, 0.0436F, 0.0F, 0.0F));

        ModelPartData Panel7 = WireBlue.addChild("Panel7", ModelPartBuilder.create(), ModelTransform.of(0.0F, -0.1952F, 0.962F, 0.0436F, 0.0F, 0.0F));

        ModelPartData Panel_r11 = Panel7.addChild("Panel_r11", ModelPartBuilder.create().uv(46, 26).cuboid(-0.75F, 0.0F, -1.0F, 1.0F, 0.0F, 1.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 0.1129F, -0.9233F, 3.0107F, 0.0F, 3.1416F));

        ModelPartData Panel8 = Panel7.addChild("Panel8", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.5F, 2.0F));

        ModelPartData Panel_r12 = Panel8.addChild("Panel_r12", ModelPartBuilder.create().uv(46, 27).cuboid(-0.75F, 0.0F, -1.0F, 1.0F, 0.0F, 1.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, -0.5176F, -1.9319F, -3.0543F, 0.0F, 3.1416F));

        ModelPartData Panel_r13 = Panel8.addChild("Panel_r13", ModelPartBuilder.create().uv(46, 28).cuboid(-0.75F, 0.0F, -1.0F, 1.0F, 0.0F, 1.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, -0.4302F, -0.9339F, -2.7925F, 0.0F, 3.1416F));

        ModelPartData Panel9 = Panel8.addChild("Panel9", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData Panel_r14 = Panel9.addChild("Panel_r14", ModelPartBuilder.create().uv(46, 30).cuboid(-0.75F, 0.0F, -1.0F, 1.0F, 0.0F, 1.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 0.5225F, 0.8017F, -1.9635F, 0.0F, 3.1416F));

        ModelPartData Panel_r15 = Panel9.addChild("Panel_r15", ModelPartBuilder.create().uv(46, 29).cuboid(-0.75F, 0.0F, -1.0F, 1.0F, 0.0F, 1.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, -0.0874F, 0.0075F, -2.4871F, 0.0F, 3.1416F));

        ModelPartData WireBlak = PanelC.addChild("WireBlak", ModelPartBuilder.create(), ModelTransform.of(2.25F, -0.2372F, 24.6554F, -0.0436F, 0.0F, 0.0F));

        ModelPartData Panel10 = WireBlak.addChild("Panel10", ModelPartBuilder.create(), ModelTransform.of(0.0F, -0.1952F, 0.962F, 0.0436F, 0.0F, 0.0F));

        ModelPartData Panel_r16 = Panel10.addChild("Panel_r16", ModelPartBuilder.create().uv(52, 26).cuboid(-0.75F, 0.0F, -1.0F, 1.0F, 0.0F, 1.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 0.1129F, -0.9233F, 3.0107F, 0.0F, 3.1416F));

        ModelPartData Panel11 = Panel10.addChild("Panel11", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.5F, 2.0F));

        ModelPartData Panel_r17 = Panel11.addChild("Panel_r17", ModelPartBuilder.create().uv(52, 27).cuboid(-0.75F, 0.0F, -1.0F, 1.0F, 0.0F, 1.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, -0.5176F, -1.9319F, -3.0543F, 0.0F, 3.1416F));

        ModelPartData Panel_r18 = Panel11.addChild("Panel_r18", ModelPartBuilder.create().uv(52, 28).cuboid(-0.75F, 0.0F, -1.0F, 1.0F, 0.0F, 1.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, -0.4302F, -0.9339F, -2.7925F, 0.0F, 3.1416F));

        ModelPartData Panel12 = Panel11.addChild("Panel12", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData Panel_r19 = Panel12.addChild("Panel_r19", ModelPartBuilder.create().uv(51, 29).cuboid(-0.75F, 0.0F, -2.0F, 1.0F, 0.0F, 2.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, -0.0874F, 0.0075F, -2.4871F, 0.0F, 3.1416F));

        ModelPartData GearA = PanelC.addChild("GearA", ModelPartBuilder.create(), ModelTransform.of(7.25F, -0.6118F, 21.9761F, -0.2618F, 0.0F, 0.0F));

        ModelPartData Gear = GearA.addChild("Gear", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData Gear_r1 = Gear.addChild("Gear_r1", ModelPartBuilder.create().uv(51, 56).cuboid(-2.25F, 0.0F, -2.75F, 5.0F, 0.0F, 5.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 3.1416F, 0.0F, 3.1416F));

        ModelPartData GearB = PanelC.addChild("GearB", ModelPartBuilder.create(), ModelTransform.of(3.25F, -1.2589F, 19.5613F, -0.2618F, 0.0F, 0.0F));

        ModelPartData GearC = GearB.addChild("GearC", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData Gear_r2 = GearC.addChild("Gear_r2", ModelPartBuilder.create().uv(51, 56).cuboid(-2.25F, 0.0F, -2.75F, 5.0F, 0.0F, 5.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 3.1416F, 0.0F, 3.1416F));

        ModelPartData Pistons = PanelC.addChild("Pistons", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, -7.0F, 0.0F));

        ModelPartData Piston = Pistons.addChild("Piston", ModelPartBuilder.create(), ModelTransform.of(-7.5F, 4.6246F, 21.1228F, -0.2618F, 0.0F, 0.0F));

        ModelPartData Piston_r1 = Piston.addChild("Piston_r1", ModelPartBuilder.create().uv(53, 50).cuboid(-0.5F, -1.5F, -0.5F, 1.0F, 3.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 3.1416F, 0.0F, 3.1416F));

        ModelPartData Piston2 = Pistons.addChild("Piston2", ModelPartBuilder.create(), ModelTransform.of(-7.5F, 4.1069F, 19.191F, -0.2618F, 0.0F, 0.0F));

        ModelPartData Piston2_r1 = Piston2.addChild("Piston2_r1", ModelPartBuilder.create().uv(53, 50).cuboid(-0.5F, -1.5F, -0.5F, 1.0F, 3.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 3.1416F, 0.0F, 3.1416F));

        ModelPartData KnobA = PanelC.addChild("KnobA", ModelPartBuilder.create(), ModelTransform.of(-7.5F, -0.6109F, 23.7886F, -0.2618F, 0.0F, 0.0F));

        ModelPartData Knob_r16 = KnobA.addChild("Knob_r16", ModelPartBuilder.create().uv(59, 51).cuboid(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(60, 53).cuboid(-0.25F, -0.75F, 0.0F, 1.0F, 1.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 3.1416F, 0.0F, 3.1416F));

        ModelPartData Knobby = PanelC.addChild("Knobby", ModelPartBuilder.create(), ModelTransform.of(-9.5F, -0.4487F, 24.7804F, -2.5307F, -1.1026F, 2.5831F));

        ModelPartData Knob_r17 = Knobby.addChild("Knob_r17", ModelPartBuilder.create().uv(59, 51).cuboid(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.5708F, 0.0F));

        ModelPartData Knob_r18 = Knobby.addChild("Knob_r18", ModelPartBuilder.create().uv(60, 53).cuboid(-0.25F, -0.75F, 0.0F, 1.0F, 1.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 3.1416F, 0.0F, 3.1416F));

        ModelPartData KnobC = PanelC.addChild("KnobC", ModelPartBuilder.create(), ModelTransform.of(-10.0F, -0.9663F, 22.8485F, -0.3622F, 0.7519F, -0.2533F));

        ModelPartData Knob_r19 = KnobC.addChild("Knob_r19", ModelPartBuilder.create().uv(59, 51).cuboid(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(60, 53).cuboid(-0.25F, -0.75F, 0.0F, 1.0F, 1.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 3.1416F, 0.0F, 3.1416F));

        ModelPartData SmallNixie2 = PanelC.addChild("SmallNixie2", ModelPartBuilder.create(), ModelTransform.pivot(8.0124F, -0.5971F, -24.9848F));

        ModelPartData Case_r3 = SmallNixie2.addChild("Case_r3", ModelPartBuilder.create().uv(203, 154).cuboid(-0.75F, 0.75F, -0.75F, 1.5F, 1.0F, 1.5F, new Dilation(0.1F))
                .uv(217, 156).cuboid(-0.75F, 0.5F, -0.75F, 1.5F, 1.0F, 1.5F, new Dilation(0.0F)), ModelTransform.of(0.0F, -1.0867F, -0.2912F, 0.2618F, 0.0F, 0.0F));

        ModelPartData PanelBottomGrill3 = PanelC.addChild("PanelBottomGrill3", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 7.3113F, 11.2405F));

        ModelPartData PanelBottomBit_r23 = PanelBottomGrill3.addChild("PanelBottomBit_r23", ModelPartBuilder.create().uv(35, 68).cuboid(-6.0F, -0.5F, -1.0F, 12.0F, 1.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.7418F, 0.0F, 0.0F));

        ModelPartData PanelBottomBit_r24 = PanelBottomGrill3.addChild("PanelBottomBit_r24", ModelPartBuilder.create().uv(41, 68).cuboid(-7.0F, -0.5F, -1.0F, 14.0F, 1.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -0.4511F, 1.4306F, 0.7418F, 0.0F, 0.0F));

        ModelPartData PanelBottomBit_r25 = PanelBottomGrill3.addChild("PanelBottomBit_r25", ModelPartBuilder.create().uv(38, 68).cuboid(-8.0F, -0.5F, -1.0F, 16.0F, 1.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -0.9021F, 2.8612F, 0.7418F, 0.0F, 0.0F));

        ModelPartData PanelBottomBit_r26 = PanelBottomGrill3.addChild("PanelBottomBit_r26", ModelPartBuilder.create().uv(35, 68).cuboid(-9.0F, -0.5F, -1.0F, 18.0F, 1.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -1.3532F, 4.2917F, 0.7418F, 0.0F, 0.0F));

        ModelPartData PanelBottomBit_r27 = PanelBottomGrill3.addChild("PanelBottomBit_r27", ModelPartBuilder.create().uv(32, 68).cuboid(-10.0F, -0.5F, -1.0F, 20.0F, 1.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -1.8042F, 5.7223F, 0.7418F, 0.0F, 0.0F));

        ModelPartData PanelBottomBit_r28 = PanelBottomGrill3.addChild("PanelBottomBit_r28", ModelPartBuilder.create().uv(29, 68).cuboid(-11.0F, -0.5F, -1.0F, 22.0F, 1.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -2.2553F, 7.1529F, 0.7418F, 0.0F, 0.0F));

        ModelPartData PanelBottomBit_r29 = PanelBottomGrill3.addChild("PanelBottomBit_r29", ModelPartBuilder.create().uv(26, 68).cuboid(-12.0F, -0.5F, -1.0F, 24.0F, 1.0F, 2.0F, new Dilation(0.0F))
                .uv(26, 68).cuboid(-12.0F, -0.5F, -1.0F, 24.0F, 1.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -2.7064F, 8.5834F, 0.7418F, 0.0F, 0.0F));

        ModelPartData PanelBottomBit_r30 = PanelBottomGrill3.addChild("PanelBottomBit_r30", ModelPartBuilder.create().uv(23, 68).cuboid(-13.0F, -0.5F, -1.0F, 26.0F, 1.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -3.1574F, 10.014F, 0.7418F, 0.0F, 0.0F));

        ModelPartData PanelBottomBit_r31 = PanelBottomGrill3.addChild("PanelBottomBit_r31", ModelPartBuilder.create().uv(20, 68).cuboid(-14.0F, -0.5F, -1.0F, 28.0F, 1.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -3.6085F, 11.4446F, 0.7418F, 0.0F, 0.0F));

        ModelPartData PanelBottomBit_r32 = PanelBottomGrill3.addChild("PanelBottomBit_r32", ModelPartBuilder.create().uv(20, 68).cuboid(-14.0F, -0.5F, -1.0F, 28.0F, 1.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -4.0595F, 12.8752F, 0.7418F, 0.0F, 0.0F));

        ModelPartData PanelBottomBit_r33 = PanelBottomGrill3.addChild("PanelBottomBit_r33", ModelPartBuilder.create().uv(17, 68).cuboid(-15.0F, -0.5F, -1.0F, 30.0F, 1.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -4.5106F, 14.3057F, 0.7418F, 0.0F, 0.0F));

        ModelPartData MagLevEngine = PanelBottomGrill3.addChild("MagLevEngine", ModelPartBuilder.create(), ModelTransform.pivot(2.5F, 0.5257F, 8.7174F));

        ModelPartData MagLevMid_r1 = MagLevEngine.addChild("MagLevMid_r1", ModelPartBuilder.create().uv(65, 112).cuboid(-1.5F, -1.5F, -1.5F, 3.0F, 1.0F, 3.0F, new Dilation(0.0F))
                .uv(75, 108).cuboid(-2.0F, -3.5F, -2.0F, 4.0F, 2.0F, 4.0F, new Dilation(0.0F))
                .uv(64, 117).cuboid(-2.0F, -0.5F, -2.0F, 4.0F, 1.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.5236F, 0.0F, 0.0F));

        ModelPartData PullLver = PanelC.addChild("PullLver", ModelPartBuilder.create(), ModelTransform.of(-1.0F, -2.1906F, 19.9706F, 0.6545F, 0.0F, 0.0F));

        ModelPartData Base_r1 = PullLver.addChild("Base_r1", ModelPartBuilder.create().uv(17, 57).cuboid(-0.5F, -0.5F, -1.5F, 1.0F, 1.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.3536F, -0.3536F, -2.3562F, 0.0F, 3.1416F));

        ModelPartData LeverGripC = PullLver.addChild("LeverGripC", ModelPartBuilder.create(), ModelTransform.of(0.0F, 0.0F, 0.0F, -1.5708F, 0.0F, 0.0F));

        ModelPartData Levergrip_r1 = LeverGripC.addChild("Levergrip_r1", ModelPartBuilder.create().uv(17, 65).cuboid(0.0F, -3.0F, 0.5F, 0.0F, 2.0F, 1.0F, new Dilation(0.001F))
                .uv(20, 62).cuboid(-0.5F, -3.75F, -0.5F, 1.0F, 2.0F, 1.0F, new Dilation(-0.25F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 3.1416F, 0.0F, 3.1416F));

        ModelPartData Lever_r1 = LeverGripC.addChild("Lever_r1", ModelPartBuilder.create().uv(17, 61).cuboid(0.0F, -2.5F, 0.0F, 0.0F, 3.0F, 1.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 0.0326F, 0.2479F, 3.1416F, 0.0F, 3.1416F));

        ModelPartData PullLver2 = PanelC.addChild("PullLver2", ModelPartBuilder.create(), ModelTransform.of(0.25F, -2.1906F, 19.9706F, 0.6545F, 0.0F, 0.0F));

        ModelPartData Base_r2 = PullLver2.addChild("Base_r2", ModelPartBuilder.create().uv(17, 57).cuboid(-0.5F, -0.5F, -1.5F, 1.0F, 1.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.3536F, -0.3536F, -2.3562F, 0.0F, 3.1416F));

        ModelPartData LeverGripC2 = PullLver2.addChild("LeverGripC2", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData Levergrip_r2 = LeverGripC2.addChild("Levergrip_r2", ModelPartBuilder.create().uv(17, 65).cuboid(0.0F, -3.0F, 0.5F, 0.0F, 2.0F, 1.0F, new Dilation(0.001F))
                .uv(20, 62).cuboid(-0.5F, -3.75F, -0.5F, 1.0F, 2.0F, 1.0F, new Dilation(-0.25F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 3.1416F, 0.0F, 3.1416F));

        ModelPartData Lever_r2 = LeverGripC2.addChild("Lever_r2", ModelPartBuilder.create().uv(17, 61).cuboid(0.0F, -2.5F, 0.0F, 0.0F, 3.0F, 1.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 0.0326F, 0.2479F, 3.1416F, 0.0F, 3.1416F));

        ModelPartData PullLver3 = PanelC.addChild("PullLver3", ModelPartBuilder.create(), ModelTransform.of(1.5F, -2.1906F, 19.9706F, 0.6545F, 0.0F, 0.0F));

        ModelPartData Base_r3 = PullLver3.addChild("Base_r3", ModelPartBuilder.create().uv(17, 57).cuboid(-0.5F, -0.5F, -1.5F, 1.0F, 1.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.3536F, -0.3536F, -2.3562F, 0.0F, 3.1416F));

        ModelPartData LeverGripC3 = PullLver3.addChild("LeverGripC3", ModelPartBuilder.create(), ModelTransform.of(0.0F, 0.0F, 0.0F, -1.5708F, 0.0F, 0.0F));

        ModelPartData Levergrip_r3 = LeverGripC3.addChild("Levergrip_r3", ModelPartBuilder.create().uv(17, 65).cuboid(0.0F, -3.0F, 0.5F, 0.0F, 2.0F, 1.0F, new Dilation(0.001F))
                .uv(20, 62).cuboid(-0.5F, -3.75F, -0.5F, 1.0F, 2.0F, 1.0F, new Dilation(-0.25F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 3.1416F, 0.0F, 3.1416F));

        ModelPartData Lever_r3 = LeverGripC3.addChild("Lever_r3", ModelPartBuilder.create().uv(17, 61).cuboid(0.0F, -2.5F, 0.0F, 0.0F, 3.0F, 1.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 0.0326F, 0.2479F, 3.1416F, 0.0F, 3.1416F));

        ModelPartData PanelD = Panels.addChild("PanelD", ModelPartBuilder.create().uv(113, 150).cuboid(-15.0F, 0.0F, 25.5F, 30.0F, 2.0F, 2.0F, new Dilation(0.0F))
                .uv(113, 146).cuboid(-15.0F, 2.0F, 25.5F, 30.0F, 1.0F, 2.0F, new Dilation(0.0F))
                .uv(163, 46).cuboid(-15.0F, 1.0F, 26.5F, 30.0F, 1.0F, 0.0F, new Dilation(0.0F))
                .uv(35, 10).cuboid(-5.0F, 0.5F, 27.0F, 10.0F, 0.0F, 2.0F, new Dilation(0.001F))
                .uv(43, 18).cuboid(-3.75F, 0.975F, 26.35F, 1.0F, 1.0F, 2.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 3.1416F, 0.0F));

        ModelPartData underPanel_r3 = PanelD.addChild("underPanel_r3", ModelPartBuilder.create().uv(82, 1).cuboid(-16.1F, 0.1683F, -7.8981F, 32.0F, 0.0F, 17.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 5.1317F, 18.8981F, 2.8362F, 0.0F, 3.1416F));

        ModelPartData cube_r98 = PanelD.addChild("cube_r98", ModelPartBuilder.create().uv(243, 101).cuboid(-6.0F, 0.0F, -11.5F, 12.0F, 0.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -2.0F, 0.0F, 0.0F, 3.1416F, 0.0F));

        ModelPartData Panel_r20 = PanelD.addChild("Panel_r20", ModelPartBuilder.create().uv(82, 87).cuboid(-16.0F, 0.0F, -28.6F, 32.0F, 0.0F, 16.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, -7.0F, 0.0F, -2.8798F, 0.0F, 3.1416F));

        ModelPartData PanelPitEdge_r4 = PanelD.addChild("PanelPitEdge_r4", ModelPartBuilder.create().uv(0, 233).cuboid(-6.5F, -1.0F, -0.5F, 13.0F, 3.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -3.4233F, 12.0858F, -0.3927F, 0.0F, 0.0F));

        ModelPartData RADAR = PanelD.addChild("RADAR", ModelPartBuilder.create(), ModelTransform.of(4.0F, -0.6851F, 20.6699F, -0.2618F, 0.0F, 0.0F));

        ModelPartData radarback_r1 = RADAR.addChild("radarback_r1", ModelPartBuilder.create().uv(13, 95).cuboid(-5.0F, -0.55F, -5.0F, 10.0F, 0.0F, 10.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 3.1416F, 0.0F, 3.1416F));

        ModelPartData radarscan = RADAR.addChild("radarscan", ModelPartBuilder.create(), ModelTransform.pivot(0.25F, -0.63F, 0.2146F));

        ModelPartData radarscan_r1 = radarscan.addChild("radarscan_r1", ModelPartBuilder.create().uv(39, 99).cuboid(-1.75F, 0.0F, 0.0F, 2.0F, 0.0F, 5.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 0.0F, 0.25F, 3.1416F, 0.0F, 3.1416F));

        ModelPartData emptyf = PanelD.addChild("emptyf", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, -1.5648F, 0.0F));

        ModelPartData empty20 = PanelD.addChild("empty20", ModelPartBuilder.create(), ModelTransform.of(0.0F, -1.5648F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData emptyu = PanelD.addChild("emptyu", ModelPartBuilder.create(), ModelTransform.of(0.0F, -1.5648F, 0.0F, -3.1416F, 1.0472F, 3.1416F));

        ModelPartData empty22 = PanelD.addChild("empty22", ModelPartBuilder.create(), ModelTransform.of(0.0F, -1.5648F, 0.0F, -3.1416F, 0.0F, 3.1416F));

        ModelPartData empty23 = PanelD.addChild("empty23", ModelPartBuilder.create(), ModelTransform.of(0.0F, -1.5648F, 0.0F, -3.1416F, -1.0472F, 3.1416F));

        ModelPartData empty24 = PanelD.addChild("empty24", ModelPartBuilder.create(), ModelTransform.of(0.0F, -1.5648F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData Hammer = PanelD.addChild("Hammer", ModelPartBuilder.create().uv(48, 20).cuboid(-0.625F, -1.25F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.001F))
                .uv(48, 20).cuboid(0.625F, -1.25F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.001F))
                .uv(52, 20).cuboid(0.125F, -1.25F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.25F)), ModelTransform.of(3.25F, 0.725F, 28.475F, 0.0F, -1.5708F, 0.0F));

        ModelPartData cube_r99 = Hammer.addChild("cube_r99", ModelPartBuilder.create().uv(48, 20).cuboid(-0.5F, 0.0F, -0.5F, 1.0F, 0.0F, 1.0F, new Dilation(0.001F)), ModelTransform.of(-1.125F, -1.25F, 0.0F, 0.0F, 3.1416F, 0.0F));

        ModelPartData cube_r100 = Hammer.addChild("cube_r100", ModelPartBuilder.create().uv(41, 25).cuboid(0.0F, -1.5F, 0.0F, 1.0F, 3.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(0.125F, 0.25F, 0.0F, 0.0F, 0.0F, -3.1416F));

        ModelPartData cube_r101 = Hammer.addChild("cube_r101", ModelPartBuilder.create().uv(52, 14).cuboid(-0.5F, -1.5F, -0.5F, 1.0F, 3.0F, 1.0F, new Dilation(0.001F)), ModelTransform.of(-0.125F, 3.25F, 0.0F, 0.0F, 1.5708F, 0.0F));

        ModelPartData Screwdriver = PanelD.addChild("Screwdriver", ModelPartBuilder.create().uv(36, 19).cuboid(-0.75F, -0.525F, 0.0F, 1.0F, 3.0F, 1.0F, new Dilation(0.001F))
                .uv(41, 25).cuboid(-0.5F, 2.475F, 0.5F, 1.0F, 3.0F, 0.0F, new Dilation(0.001F)), ModelTransform.pivot(-1.0F, -2.0F, 27.85F));

        ModelPartData ScrewdriverB = PanelD.addChild("ScrewdriverB", ModelPartBuilder.create().uv(43, 24).cuboid(-0.75F, -0.525F, 0.0F, 1.0F, 3.0F, 1.0F, new Dilation(0.001F))
                .uv(41, 24).cuboid(-0.5F, 2.475F, 0.5F, 1.0F, 4.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(0.5F, -1.9004F, 27.6087F, 0.0873F, 0.0F, 0.0F));

        ModelPartData Wrench = PanelD.addChild("Wrench", ModelPartBuilder.create().uv(36, 23).cuboid(-1.0F, -1.5F, 0.0F, 2.0F, 7.0F, 0.0F, new Dilation(0.001F)), ModelTransform.pivot(-3.25F, 1.475F, 27.6F));

        ModelPartData PanelBottomGrill4 = PanelD.addChild("PanelBottomGrill4", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 7.3113F, 11.2405F));

        ModelPartData PanelBottomBit_r34 = PanelBottomGrill4.addChild("PanelBottomBit_r34", ModelPartBuilder.create().uv(35, 68).cuboid(-6.0F, -0.5F, -1.0F, 12.0F, 1.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.7418F, 0.0F, 0.0F));

        ModelPartData PanelBottomBit_r35 = PanelBottomGrill4.addChild("PanelBottomBit_r35", ModelPartBuilder.create().uv(41, 68).cuboid(-7.0F, -0.5F, -1.0F, 14.0F, 1.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -0.4511F, 1.4306F, 0.7418F, 0.0F, 0.0F));

        ModelPartData PanelBottomBit_r36 = PanelBottomGrill4.addChild("PanelBottomBit_r36", ModelPartBuilder.create().uv(38, 68).cuboid(-8.0F, -0.5F, -1.0F, 16.0F, 1.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -0.9021F, 2.8612F, 0.7418F, 0.0F, 0.0F));

        ModelPartData PanelBottomBit_r37 = PanelBottomGrill4.addChild("PanelBottomBit_r37", ModelPartBuilder.create().uv(35, 68).cuboid(-9.0F, -0.5F, -1.0F, 18.0F, 1.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -1.3532F, 4.2917F, 0.7418F, 0.0F, 0.0F));

        ModelPartData PanelBottomBit_r38 = PanelBottomGrill4.addChild("PanelBottomBit_r38", ModelPartBuilder.create().uv(32, 68).cuboid(-10.0F, -0.5F, -1.0F, 20.0F, 1.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -1.8042F, 5.7223F, 0.7418F, 0.0F, 0.0F));

        ModelPartData PanelBottomBit_r39 = PanelBottomGrill4.addChild("PanelBottomBit_r39", ModelPartBuilder.create().uv(29, 68).cuboid(-11.0F, -0.5F, -1.0F, 22.0F, 1.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -2.2553F, 7.1529F, 0.7418F, 0.0F, 0.0F));

        ModelPartData PanelBottomBit_r40 = PanelBottomGrill4.addChild("PanelBottomBit_r40", ModelPartBuilder.create().uv(26, 68).cuboid(-12.0F, -0.5F, -1.0F, 24.0F, 1.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -2.7064F, 8.5834F, 0.7418F, 0.0F, 0.0F));

        ModelPartData PanelBottomBit_r41 = PanelBottomGrill4.addChild("PanelBottomBit_r41", ModelPartBuilder.create().uv(23, 68).cuboid(-13.0F, -0.5F, -1.0F, 26.0F, 1.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -3.1574F, 10.014F, 0.7418F, 0.0F, 0.0F));

        ModelPartData PanelBottomBit_r42 = PanelBottomGrill4.addChild("PanelBottomBit_r42", ModelPartBuilder.create().uv(20, 68).cuboid(-14.0F, -0.5F, -1.0F, 28.0F, 1.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -3.6085F, 11.4446F, 0.7418F, 0.0F, 0.0F));

        ModelPartData PanelBottomBit_r43 = PanelBottomGrill4.addChild("PanelBottomBit_r43", ModelPartBuilder.create().uv(20, 68).cuboid(-14.0F, -0.5F, -1.0F, 28.0F, 1.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -4.0595F, 12.8752F, 0.7418F, 0.0F, 0.0F));

        ModelPartData PanelBottomBit_r44 = PanelBottomGrill4.addChild("PanelBottomBit_r44", ModelPartBuilder.create().uv(17, 68).cuboid(-15.0F, -0.5F, -1.0F, 30.0F, 1.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -4.5106F, 14.3057F, 0.7418F, 0.0F, 0.0F));

        ModelPartData Randomiser2 = PanelD.addChild("Randomiser2", ModelPartBuilder.create(), ModelTransform.pivot(-1.5F, -1.1045F, 22.8996F));

        ModelPartData Randomiser_r1 = Randomiser2.addChild("Randomiser_r1", ModelPartBuilder.create().uv(36, 125).cuboid(-2.5F, -1.5F, -3.0F, 5.0F, 3.0F, 6.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 3.1416F, 0.0F));

        ModelPartData KnobI = Randomiser2.addChild("KnobI", ModelPartBuilder.create(), ModelTransform.of(-8.5F, 0.2676F, 0.4319F, -1.5708F, 1.309F, -1.5708F));

        ModelPartData Knob_r20 = KnobI.addChild("Knob_r20", ModelPartBuilder.create().uv(59, 51).cuboid(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 3.1416F, 0.0F));

        ModelPartData Knob_r21 = KnobI.addChild("Knob_r21", ModelPartBuilder.create().uv(60, 53).cuboid(-0.25F, -0.75F, 0.0F, 1.0F, 1.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 3.1416F, 0.0F, 3.1416F));

        ModelPartData Knobq = Randomiser2.addChild("Knobq", ModelPartBuilder.create(), ModelTransform.of(0.0F, -0.2828F, -2.0088F, -0.3622F, -0.7519F, 0.2533F));

        ModelPartData Knob_r22 = Knobq.addChild("Knob_r22", ModelPartBuilder.create().uv(59, 51).cuboid(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.5708F, 0.0F));

        ModelPartData Knob_r23 = Knobq.addChild("Knob_r23", ModelPartBuilder.create().uv(60, 53).cuboid(-0.25F, -0.75F, 0.0F, 1.0F, 1.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 3.1416F, 0.0F, 3.1416F));

        ModelPartData Knobr = Randomiser2.addChild("Knobr", ModelPartBuilder.create(), ModelTransform.of(-1.0F, 0.2348F, -0.077F, -2.8593F, 0.3786F, -3.0348F));

        ModelPartData Knob_r24 = Knobr.addChild("Knob_r24", ModelPartBuilder.create().uv(59, 51).cuboid(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.5708F, 0.0F));

        ModelPartData Knob_r25 = Knobr.addChild("Knob_r25", ModelPartBuilder.create().uv(60, 53).cuboid(-0.25F, -0.75F, 0.0F, 1.0F, 1.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 3.1416F, 0.0F, 3.1416F));

        ModelPartData Knobs = Randomiser2.addChild("Knobs", ModelPartBuilder.create(), ModelTransform.of(1.0F, 0.623F, 1.3719F, -2.5307F, 1.1026F, -2.5831F));

        ModelPartData Knob_r26 = Knobs.addChild("Knob_r26", ModelPartBuilder.create().uv(59, 51).cuboid(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 3.1416F, 0.0F));

        ModelPartData Knob_r27 = Knobs.addChild("Knob_r27", ModelPartBuilder.create().uv(60, 53).cuboid(-0.25F, -0.75F, 0.0F, 1.0F, 1.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 3.1416F, 0.0F, 3.1416F));

        ModelPartData Knobt = Randomiser2.addChild("Knobt", ModelPartBuilder.create(), ModelTransform.of(-1.5F, -0.25F, -1.5F, -1.5708F, 1.309F, -1.5708F));

        ModelPartData Knob_r28 = Knobt.addChild("Knob_r28", ModelPartBuilder.create().uv(59, 51).cuboid(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 3.1416F, 0.0F));

        ModelPartData Knob_r29 = Knobt.addChild("Knob_r29", ModelPartBuilder.create().uv(60, 53).cuboid(-0.25F, -0.75F, 0.0F, 1.0F, 1.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 3.1416F, 0.0F, 3.1416F));

        ModelPartData Knobu = Randomiser2.addChild("Knobu", ModelPartBuilder.create(), ModelTransform.of(-0.5F, 0.6559F, 1.8807F, -0.3622F, 0.7519F, -0.2533F));

        ModelPartData Knob_r30 = Knobu.addChild("Knob_r30", ModelPartBuilder.create().uv(59, 51).cuboid(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 3.1416F, 0.0F));

        ModelPartData Knob_r31 = Knobu.addChild("Knob_r31", ModelPartBuilder.create().uv(60, 53).cuboid(-0.25F, -0.75F, 0.0F, 1.0F, 1.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 3.1416F, 0.0F, 3.1416F));

        ModelPartData KnobJ = PanelD.addChild("KnobJ", ModelPartBuilder.create(), ModelTransform.of(-11.5F, -0.3521F, 24.7545F, -2.8593F, -0.3786F, 3.0348F));

        ModelPartData Knob_r32 = KnobJ.addChild("Knob_r32", ModelPartBuilder.create().uv(59, 51).cuboid(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.5708F, 0.0F));

        ModelPartData Knob_r33 = KnobJ.addChild("Knob_r33", ModelPartBuilder.create().uv(60, 53).cuboid(-0.25F, -0.75F, 0.0F, 1.0F, 1.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 3.1416F, 0.0F, 3.1416F));

        ModelPartData KnobIK = PanelD.addChild("KnobIK", ModelPartBuilder.create(), ModelTransform.of(-9.0F, -0.3193F, 25.2633F, -0.2823F, -0.3786F, 0.1068F));

        ModelPartData Knob_r34 = KnobIK.addChild("Knob_r34", ModelPartBuilder.create().uv(59, 51).cuboid(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.5708F, 0.0F));

        ModelPartData Knob_r35 = KnobIK.addChild("Knob_r35", ModelPartBuilder.create().uv(60, 53).cuboid(-0.25F, -0.75F, 0.0F, 1.0F, 1.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 3.1416F, 0.0F, 3.1416F));

        ModelPartData SmallNixie4 = PanelD.addChild("SmallNixie4", ModelPartBuilder.create(), ModelTransform.pivot(10.2624F, -1.9337F, -24.651F));

        ModelPartData Case_r4 = SmallNixie4.addChild("Case_r4", ModelPartBuilder.create().uv(203, 154).cuboid(-1.0F, 0.75F, -0.75F, 1.5F, 1.0F, 1.5F, new Dilation(0.1F))
                .uv(223, 156).cuboid(-1.0F, 0.5F, -0.75F, 1.5F, 1.0F, 1.5F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.2618F, 0.0F, 0.0F));

        ModelPartData PanelE = Panels.addChild("PanelE", ModelPartBuilder.create().uv(113, 150).cuboid(-15.0F, 0.0F, 25.5F, 30.0F, 1.0F, 2.0F, new Dilation(0.0F))
                .uv(113, 146).cuboid(-15.0F, 2.0F, 25.5F, 30.0F, 1.0F, 2.0F, new Dilation(0.0F))
                .uv(163, 46).cuboid(-15.0F, 1.0F, 26.5F, 30.0F, 1.0F, 0.0F, new Dilation(0.0F))
                .uv(204, 107).cuboid(-7.0F, -3.1529F, -25.3299F, 1.5F, 3.0F, 1.5F, new Dilation(0.0F))
                .uv(203, 149).cuboid(-9.5F, -3.1529F, -25.3299F, 1.5F, 3.0F, 1.5F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 2.0944F, 0.0F));

        ModelPartData PanelPitEdge_r5 = PanelE.addChild("PanelPitEdge_r5", ModelPartBuilder.create().uv(0, 233).cuboid(-6.5F, -1.0F, -0.5F, 13.0F, 3.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -3.4233F, 12.0858F, -0.3927F, 0.0F, 0.0F));

        ModelPartData cube_r102 = PanelE.addChild("cube_r102", ModelPartBuilder.create().uv(243, 101).cuboid(-6.0F, 0.0F, -11.5F, 12.0F, 0.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -2.0F, 0.0F, 0.0F, 3.1416F, 0.0F));

        ModelPartData underPanel_r4 = PanelE.addChild("underPanel_r4", ModelPartBuilder.create().uv(82, 1).cuboid(-16.1F, 0.1683F, -7.8981F, 32.0F, 0.0F, 17.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 5.1317F, 18.8981F, 2.8362F, 0.0F, 3.1416F));

        ModelPartData Panel_r21 = PanelE.addChild("Panel_r21", ModelPartBuilder.create().uv(80, 70).cuboid(-16.0F, 0.0F, -28.6F, 32.0F, 0.0F, 16.0F, new Dilation(0.001F))
                .uv(40, 18).cuboid(5.75F, -0.5F, -18.85F, 1.0F, 1.0F, 1.0F, new Dilation(-0.25F))
                .uv(41, 22).cuboid(4.75F, -0.5F, -20.35F, 1.0F, 1.0F, 1.0F, new Dilation(-0.25F))
                .uv(44, 14).cuboid(3.75F, -0.5F, -18.35F, 1.0F, 1.0F, 1.0F, new Dilation(-0.25F))
                .uv(40, 20).cuboid(1.75F, -0.5F, -19.35F, 1.0F, 1.0F, 1.0F, new Dilation(-0.25F))
                .uv(52, 20).cuboid(2.75F, -0.5F, -21.35F, 1.0F, 1.0F, 1.0F, new Dilation(-0.25F)), ModelTransform.of(0.0F, -7.0F, 0.0F, -2.8798F, 0.0F, 3.1416F));

        ModelPartData MonitorC_r1 = PanelE.addChild("MonitorC_r1", ModelPartBuilder.create().uv(33, 12).cuboid(-1.5F, -0.675F, -1.1F, 3.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(1.0F, -2.186F, 17.9662F, 2.6616F, 0.0F, 3.1416F));

        ModelPartData emptyiyfut = PanelE.addChild("emptyiyfut", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, -1.5648F, 0.0F));

        ModelPartData emptyafdh = PanelE.addChild("emptyafdh", ModelPartBuilder.create(), ModelTransform.of(0.0F, -1.5648F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData emptyryuk = PanelE.addChild("emptyryuk", ModelPartBuilder.create(), ModelTransform.of(0.0F, -1.5648F, 0.0F, -3.1416F, 1.0472F, 3.1416F));

        ModelPartData emptywve = PanelE.addChild("emptywve", ModelPartBuilder.create(), ModelTransform.of(0.0F, -1.5648F, 0.0F, -3.1416F, 0.0F, 3.1416F));

        ModelPartData emptyuh = PanelE.addChild("emptyuh", ModelPartBuilder.create(), ModelTransform.of(0.0F, -1.5648F, 0.0F, -3.1416F, -1.0472F, 3.1416F));

        ModelPartData emptygvcyfu = PanelE.addChild("emptygvcyfu", ModelPartBuilder.create(), ModelTransform.of(0.0F, -1.5648F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData PanelBottomGrillE = PanelE.addChild("PanelBottomGrillE", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 7.3113F, 11.2405F));

        ModelPartData PanelBottomBit_r45 = PanelBottomGrillE.addChild("PanelBottomBit_r45", ModelPartBuilder.create().uv(35, 68).cuboid(-6.0F, -0.5F, -1.0F, 12.0F, 1.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.7418F, 0.0F, 0.0F));

        ModelPartData PanelBottomBit_r46 = PanelBottomGrillE.addChild("PanelBottomBit_r46", ModelPartBuilder.create().uv(41, 68).cuboid(-7.0F, -0.5F, -1.0F, 14.0F, 1.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -0.4511F, 1.4306F, 0.7418F, 0.0F, 0.0F));

        ModelPartData PanelBottomBit_r47 = PanelBottomGrillE.addChild("PanelBottomBit_r47", ModelPartBuilder.create().uv(38, 68).cuboid(-8.0F, -0.5F, -1.0F, 16.0F, 1.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -0.9021F, 2.8612F, 0.7418F, 0.0F, 0.0F));

        ModelPartData PanelBottomBit_r48 = PanelBottomGrillE.addChild("PanelBottomBit_r48", ModelPartBuilder.create().uv(35, 68).cuboid(-9.0F, -0.5F, -1.0F, 18.0F, 1.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -1.3532F, 4.2917F, 0.7418F, 0.0F, 0.0F));

        ModelPartData PanelBottomBit_r49 = PanelBottomGrillE.addChild("PanelBottomBit_r49", ModelPartBuilder.create().uv(32, 68).cuboid(-10.0F, -0.5F, -1.0F, 20.0F, 1.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -1.8042F, 5.7223F, 0.7418F, 0.0F, 0.0F));

        ModelPartData PanelBottomBit_r50 = PanelBottomGrillE.addChild("PanelBottomBit_r50", ModelPartBuilder.create().uv(29, 68).cuboid(-11.0F, -0.5F, -1.0F, 22.0F, 1.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -2.2553F, 7.1529F, 0.7418F, 0.0F, 0.0F));

        ModelPartData PanelBottomBit_r51 = PanelBottomGrillE.addChild("PanelBottomBit_r51", ModelPartBuilder.create().uv(26, 68).cuboid(-12.0F, -0.5F, -1.0F, 24.0F, 1.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -2.7064F, 8.5834F, 0.7418F, 0.0F, 0.0F));

        ModelPartData PanelBottomBit_r52 = PanelBottomGrillE.addChild("PanelBottomBit_r52", ModelPartBuilder.create().uv(23, 68).cuboid(-13.0F, -0.5F, -1.0F, 26.0F, 1.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -3.1574F, 10.014F, 0.7418F, 0.0F, 0.0F));

        ModelPartData PanelBottomBit_r53 = PanelBottomGrillE.addChild("PanelBottomBit_r53", ModelPartBuilder.create().uv(20, 68).cuboid(-14.0F, -0.5F, -1.0F, 28.0F, 1.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -3.6085F, 11.4446F, 0.7418F, 0.0F, 0.0F));

        ModelPartData PanelBottomBit_r54 = PanelBottomGrillE.addChild("PanelBottomBit_r54", ModelPartBuilder.create().uv(20, 68).cuboid(-14.0F, -0.5F, -1.0F, 28.0F, 1.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -4.0595F, 12.8752F, 0.7418F, 0.0F, 0.0F));

        ModelPartData PanelBottomBit_r55 = PanelBottomGrillE.addChild("PanelBottomBit_r55", ModelPartBuilder.create().uv(17, 68).cuboid(-15.0F, -0.5F, -1.0F, 30.0F, 1.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -4.5106F, 14.3057F, 0.7418F, 0.0F, 0.0F));

        ModelPartData PanelF = Panels.addChild("PanelF", ModelPartBuilder.create().uv(113, 150).cuboid(-15.0F, 0.0F, 25.5F, 30.0F, 1.0F, 2.0F, new Dilation(0.0F))
                .uv(113, 146).cuboid(-15.0F, 2.0F, 25.5F, 30.0F, 1.0F, 2.0F, new Dilation(0.0F))
                .uv(163, 46).cuboid(-15.0F, 1.0F, 26.5F, 30.0F, 1.0F, 0.0F, new Dilation(0.0F))
                .uv(224, 112).cuboid(-5.75F, -7.0279F, -14.5799F, 1.5F, 3.0F, 1.5F, new Dilation(0.0F))
                .uv(15, 9).cuboid(-6.0F, -4.0279F, -14.8299F, 2.0F, 1.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData underPanel_r5 = PanelF.addChild("underPanel_r5", ModelPartBuilder.create().uv(82, 1).cuboid(-16.1F, 0.1683F, -7.8981F, 32.0F, 0.0F, 17.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 5.1317F, 18.8981F, 2.8362F, 0.0F, 3.1416F));

        ModelPartData cube_r103 = PanelF.addChild("cube_r103", ModelPartBuilder.create().uv(243, 101).cuboid(-6.0F, 0.0F, -11.5F, 12.0F, 0.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -2.0F, 0.0F, 0.0F, 3.1416F, 0.0F));

        ModelPartData Panel_r22 = PanelF.addChild("Panel_r22", ModelPartBuilder.create().uv(99, 19).cuboid(-16.0F, 0.0F, -28.6F, 32.0F, 0.0F, 16.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, -7.0F, 0.0F, -2.8798F, 0.0F, 3.1416F));

        ModelPartData PanelPitEdge_r6 = PanelF.addChild("PanelPitEdge_r6", ModelPartBuilder.create().uv(0, 233).cuboid(-6.5F, -1.0F, -0.5F, 13.0F, 3.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -3.4233F, 12.0858F, -0.3927F, 0.0F, 0.0F));

        ModelPartData BlinkingLights = PanelF.addChild("BlinkingLights", ModelPartBuilder.create(), ModelTransform.of(0.0F, -1.2531F, 22.375F, -0.1309F, 0.0F, 0.0F));

        ModelPartData BLsmd_r1 = BlinkingLights.addChild("BLsmd_r1", ModelPartBuilder.create().uv(45, 52).cuboid(2.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(45, 52).cuboid(2.5F, -0.5F, 4.0F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(45, 52).cuboid(1.0F, -0.5F, 7.0F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(45, 52).cuboid(-0.5F, -0.5F, 7.0F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(45, 52).cuboid(-2.0F, -0.5F, 5.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(45, 52).cuboid(-0.5F, -0.5F, 5.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(45, 52).cuboid(-0.5F, -0.5F, 4.0F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(45, 52).cuboid(1.0F, -0.5F, 2.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(45, 52).cuboid(1.0F, -0.5F, 1.0F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(45, 52).cuboid(-0.5F, -0.5F, 1.0F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(45, 52).cuboid(-2.0F, -0.5F, 1.0F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(45, 52).cuboid(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(3.75F, -0.2038F, -0.7218F, -1.5708F, -1.309F, 1.5708F));

        ModelPartData BLl_r1 = BlinkingLights.addChild("BLl_r1", ModelPartBuilder.create().uv(45, 49).cuboid(4.0F, -0.5F, 2.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(45, 49).cuboid(4.0F, -0.5F, 1.0F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(45, 49).cuboid(-0.5F, -0.5F, 4.0F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(45, 49).cuboid(-0.5F, -0.5F, 2.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(45, 49).cuboid(1.0F, -0.5F, 2.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(45, 49).cuboid(-0.5F, -0.5F, 7.0F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(45, 49).cuboid(4.0F, -0.5F, 7.0F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(45, 49).cuboid(4.0F, -0.5F, 5.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(45, 49).cuboid(2.5F, -0.5F, 5.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(45, 49).cuboid(2.5F, -0.5F, 4.0F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(45, 49).cuboid(2.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(45, 49).cuboid(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(3.75F, -0.592F, -2.1707F, -1.5708F, -1.309F, 1.5708F));

        ModelPartData BlinkyLightPanel_r1 = BlinkingLights.addChild("BlinkyLightPanel_r1", ModelPartBuilder.create().uv(16, 48).cuboid(-5.0F, -0.5F, -3.5F, 10.0F, 1.0F, 7.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.2318F, -0.0621F, -2.8798F, 0.0F, 3.1416F));

        ModelPartData Shields = BlinkingLights.addChild("Shields", ModelPartBuilder.create(), ModelTransform.pivot(-8.0F, 0.2318F, -0.0621F));

        ModelPartData Base_r4 = Shields.addChild("Base_r4", ModelPartBuilder.create().uv(17, 57).cuboid(-0.5F, -0.5F, -1.5F, 1.0F, 1.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, -3.0107F, 0.0F, 3.1416F));

        ModelPartData Shieldlever = Shields.addChild("Shieldlever", ModelPartBuilder.create(), ModelTransform.of(0.0F, -0.4957F, 0.0653F, 0.6545F, 0.0F, 0.0F));

        ModelPartData Levergrip_r4 = Shieldlever.addChild("Levergrip_r4", ModelPartBuilder.create().uv(17, 65).cuboid(0.0F, -3.0F, 0.5F, 0.0F, 2.0F, 1.0F, new Dilation(0.001F))
                .uv(20, 62).cuboid(-0.5F, -3.75F, -0.5F, 1.0F, 2.0F, 1.0F, new Dilation(-0.25F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 3.1416F, 0.0F, 3.1416F));

        ModelPartData Lever_r4 = Shieldlever.addChild("Lever_r4", ModelPartBuilder.create().uv(17, 61).cuboid(0.0F, -2.5F, 0.0F, 0.0F, 3.0F, 1.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 0.0326F, 0.2479F, 3.1416F, 0.0F, 3.1416F));

        ModelPartData TheBestLever = BlinkingLights.addChild("TheBestLever", ModelPartBuilder.create(), ModelTransform.pivot(-9.5F, 0.2318F, -0.0621F));

        ModelPartData Bass_r1 = TheBestLever.addChild("Bass_r1", ModelPartBuilder.create().uv(17, 57).cuboid(-0.5F, -0.5F, -1.5F, 1.0F, 1.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, -3.0107F, 0.0F, 3.1416F));

        ModelPartData Levergrip2 = TheBestLever.addChild("Levergrip2", ModelPartBuilder.create(), ModelTransform.of(0.0F, -0.4957F, 0.0653F, -0.9163F, 0.0F, 0.0F));

        ModelPartData Levergrip_r5 = Levergrip2.addChild("Levergrip_r5", ModelPartBuilder.create().uv(17, 65).cuboid(0.0F, -3.0F, 0.5F, 0.0F, 2.0F, 1.0F, new Dilation(0.001F))
                .uv(20, 62).cuboid(-0.5F, -3.75F, -0.5F, 1.0F, 2.0F, 1.0F, new Dilation(-0.25F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 3.1416F, 0.0F, 3.1416F));

        ModelPartData Lever_r5 = Levergrip2.addChild("Lever_r5", ModelPartBuilder.create().uv(17, 61).cuboid(0.0F, -2.5F, 0.0F, 0.0F, 3.0F, 1.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 0.0326F, 0.2479F, 3.1416F, 0.0F, 3.1416F));

        ModelPartData RandomLever = BlinkingLights.addChild("RandomLever", ModelPartBuilder.create(), ModelTransform.pivot(-6.5F, 0.2318F, -0.0621F));

        ModelPartData Based_r1 = RandomLever.addChild("Based_r1", ModelPartBuilder.create().uv(17, 57).cuboid(-0.5F, -0.5F, -1.5F, 1.0F, 1.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, -3.0107F, 0.0F, 3.1416F));

        ModelPartData Levergrip = RandomLever.addChild("Levergrip", ModelPartBuilder.create(), ModelTransform.of(0.0F, -0.4957F, 0.0653F, 0.6545F, 0.0F, 0.0F));

        ModelPartData Levergrip_r6 = Levergrip.addChild("Levergrip_r6", ModelPartBuilder.create().uv(17, 65).cuboid(0.0F, -3.0F, 0.5F, 0.0F, 2.0F, 1.0F, new Dilation(0.001F))
                .uv(20, 62).cuboid(-0.5F, -3.75F, -0.5F, 1.0F, 2.0F, 1.0F, new Dilation(-0.25F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 3.1416F, 0.0F, 3.1416F));

        ModelPartData Lever_r6 = Levergrip.addChild("Lever_r6", ModelPartBuilder.create().uv(17, 61).cuboid(0.0F, -2.5F, 0.0F, 0.0F, 3.0F, 1.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 0.0326F, 0.2479F, 3.1416F, 0.0F, 3.1416F));

        ModelPartData emptyf_1 = PanelF.addChild("emptyf_1", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, -1.5648F, 0.0F));

        ModelPartData emptyfx = PanelF.addChild("emptyfx", ModelPartBuilder.create(), ModelTransform.of(0.0F, -1.5648F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData emptyuivyf = PanelF.addChild("emptyuivyf", ModelPartBuilder.create(), ModelTransform.of(0.0F, -1.5648F, 0.0F, -3.1416F, 1.0472F, 3.1416F));

        ModelPartData emptysbfdt = PanelF.addChild("emptysbfdt", ModelPartBuilder.create(), ModelTransform.of(0.0F, -1.5648F, 0.0F, -3.1416F, 0.0F, 3.1416F));

        ModelPartData emptygv = PanelF.addChild("emptygv", ModelPartBuilder.create(), ModelTransform.of(0.0F, -1.5648F, 0.0F, -3.1416F, -1.0472F, 3.1416F));

        ModelPartData emptyngcmb = PanelF.addChild("emptyngcmb", ModelPartBuilder.create(), ModelTransform.of(0.0F, -1.5648F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData PanelBottomGrillf = PanelF.addChild("PanelBottomGrillf", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 7.3113F, 11.2405F));

        ModelPartData PanelBottomBit_r56 = PanelBottomGrillf.addChild("PanelBottomBit_r56", ModelPartBuilder.create().uv(35, 68).cuboid(-6.0F, -0.5F, -1.0F, 12.0F, 1.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.7418F, 0.0F, 0.0F));

        ModelPartData PanelBottomBit_r57 = PanelBottomGrillf.addChild("PanelBottomBit_r57", ModelPartBuilder.create().uv(41, 68).cuboid(-7.0F, -0.5F, -1.0F, 14.0F, 1.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -0.4511F, 1.4306F, 0.7418F, 0.0F, 0.0F));

        ModelPartData PanelBottomBit_r58 = PanelBottomGrillf.addChild("PanelBottomBit_r58", ModelPartBuilder.create().uv(38, 68).cuboid(-8.0F, -0.5F, -1.0F, 16.0F, 1.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -0.9021F, 2.8612F, 0.7418F, 0.0F, 0.0F));

        ModelPartData PanelBottomBit_r59 = PanelBottomGrillf.addChild("PanelBottomBit_r59", ModelPartBuilder.create().uv(35, 68).cuboid(-9.0F, -0.5F, -1.0F, 18.0F, 1.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -1.3532F, 4.2917F, 0.7418F, 0.0F, 0.0F));

        ModelPartData PanelBottomBit_r60 = PanelBottomGrillf.addChild("PanelBottomBit_r60", ModelPartBuilder.create().uv(32, 68).cuboid(-10.0F, -0.5F, -1.0F, 20.0F, 1.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -1.8042F, 5.7223F, 0.7418F, 0.0F, 0.0F));

        ModelPartData PanelBottomBit_r61 = PanelBottomGrillf.addChild("PanelBottomBit_r61", ModelPartBuilder.create().uv(29, 68).cuboid(-11.0F, -0.5F, -1.0F, 22.0F, 1.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -2.2553F, 7.1529F, 0.7418F, 0.0F, 0.0F));

        ModelPartData PanelBottomBit_r62 = PanelBottomGrillf.addChild("PanelBottomBit_r62", ModelPartBuilder.create().uv(26, 68).cuboid(-12.0F, -0.5F, -1.0F, 24.0F, 1.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -2.7064F, 8.5834F, 0.7418F, 0.0F, 0.0F));

        ModelPartData PanelBottomBit_r63 = PanelBottomGrillf.addChild("PanelBottomBit_r63", ModelPartBuilder.create().uv(23, 68).cuboid(-13.0F, -0.5F, -1.0F, 26.0F, 1.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -3.1574F, 10.014F, 0.7418F, 0.0F, 0.0F));

        ModelPartData PanelBottomBit_r64 = PanelBottomGrillf.addChild("PanelBottomBit_r64", ModelPartBuilder.create().uv(20, 68).cuboid(-14.0F, -0.5F, -1.0F, 28.0F, 1.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -3.6085F, 11.4446F, 0.7418F, 0.0F, 0.0F));

        ModelPartData PanelBottomBit_r65 = PanelBottomGrillf.addChild("PanelBottomBit_r65", ModelPartBuilder.create().uv(20, 68).cuboid(-14.0F, -0.5F, -1.0F, 28.0F, 1.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -4.0595F, 12.8752F, 0.7418F, 0.0F, 0.0F));

        ModelPartData PanelBottomBit_r66 = PanelBottomGrillf.addChild("PanelBottomBit_r66", ModelPartBuilder.create().uv(17, 68).cuboid(-15.0F, -0.5F, -1.0F, 30.0F, 1.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -4.5106F, 14.3057F, 0.7418F, 0.0F, 0.0F));

        ModelPartData SmallNixie5 = PanelF.addChild("SmallNixie5", ModelPartBuilder.create(), ModelTransform.pivot(0.0124F, -1.9337F, -23.351F));

        ModelPartData Case_r5 = SmallNixie5.addChild("Case_r5", ModelPartBuilder.create().uv(203, 154).cuboid(-1.0F, 0.75F, -0.75F, 1.5F, 1.0F, 1.5F, new Dilation(0.1F))
                .uv(229, 156).cuboid(-1.0F, 0.5F, -0.75F, 1.5F, 1.0F, 1.5F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.2618F, 0.0F, 0.0F));

        ModelPartData SmallNixie6 = PanelF.addChild("SmallNixie6", ModelPartBuilder.create(), ModelTransform.pivot(0.0124F, -1.4337F, -25.351F));

        ModelPartData Case_r6 = SmallNixie6.addChild("Case_r6", ModelPartBuilder.create().uv(203, 154).cuboid(-1.0F, 0.75F, -0.75F, 1.5F, 1.0F, 1.5F, new Dilation(0.1F))
                .uv(223, 156).cuboid(-1.0F, 0.5F, -0.75F, 1.5F, 1.0F, 1.5F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.2618F, 0.0F, 0.0F));

        ModelPartData ConsoleBase = Root.addChild("ConsoleBase", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData Sidea = ConsoleBase.addChild("Sidea", ModelPartBuilder.create().uv(139, 231).cuboid(-5.5F, -7.0F, -10.5F, 11.0F, 2.0F, 4.0F, new Dilation(0.0F))
                .uv(213, 149).cuboid(-6.0F, -4.0F, -11.5F, 12.0F, 2.0F, 5.0F, new Dilation(0.0F))
                .uv(204, 98).cuboid(-6.5F, -1.0F, -12.5F, 13.0F, 2.0F, 6.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -1.0F, 0.0F));

        ModelPartData cube_r104 = Sidea.addChild("cube_r104", ModelPartBuilder.create().uv(245, 107).cuboid(-6.0F, -9.0F, 0.0F, 12.0F, 9.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 1.0F, -11.75F, -0.2618F, 0.0F, 0.0F));

        ModelPartData Side2 = ConsoleBase.addChild("Side2", ModelPartBuilder.create().uv(139, 231).cuboid(-5.5F, -7.0F, -10.5F, 11.0F, 2.0F, 4.0F, new Dilation(0.0F))
                .uv(213, 149).cuboid(-6.0F, -4.0F, -11.5F, 12.0F, 2.0F, 5.0F, new Dilation(0.0F))
                .uv(204, 98).cuboid(-6.5F, -1.0F, -12.5F, 13.0F, 2.0F, 6.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -1.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData cube_r105 = Side2.addChild("cube_r105", ModelPartBuilder.create().uv(245, 107).cuboid(-6.0F, -9.0F, 0.0F, 12.0F, 9.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 1.0F, -11.75F, -0.2618F, 0.0F, 0.0F));

        ModelPartData Side3 = ConsoleBase.addChild("Side3", ModelPartBuilder.create().uv(139, 231).cuboid(-5.5F, -7.0F, -10.5F, 11.0F, 2.0F, 4.0F, new Dilation(0.0F))
                .uv(213, 149).cuboid(-6.0F, -4.0F, -11.5F, 12.0F, 2.0F, 5.0F, new Dilation(0.0F))
                .uv(204, 98).cuboid(-6.5F, -1.0F, -12.5F, 13.0F, 2.0F, 6.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -1.0F, 0.0F, 0.0F, -2.0944F, 0.0F));

        ModelPartData cube_r106 = Side3.addChild("cube_r106", ModelPartBuilder.create().uv(245, 107).cuboid(-6.0F, -9.0F, 0.0F, 12.0F, 9.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 1.0F, -11.75F, -0.2618F, 0.0F, 0.0F));

        ModelPartData Side4 = ConsoleBase.addChild("Side4", ModelPartBuilder.create().uv(139, 231).cuboid(-5.5F, -7.0F, -10.5F, 11.0F, 2.0F, 4.0F, new Dilation(0.0F))
                .uv(213, 149).cuboid(-6.0F, -4.0F, -11.5F, 12.0F, 2.0F, 5.0F, new Dilation(0.0F))
                .uv(204, 98).cuboid(-6.5F, -1.0F, -12.5F, 13.0F, 2.0F, 6.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -1.0F, 0.0F, 0.0F, 3.1416F, 0.0F));

        ModelPartData cube_r107 = Side4.addChild("cube_r107", ModelPartBuilder.create().uv(245, 107).cuboid(-6.0F, -9.0F, 0.0F, 12.0F, 9.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 1.0F, -11.75F, -0.2618F, 0.0F, 0.0F));

        ModelPartData Side5 = ConsoleBase.addChild("Side5", ModelPartBuilder.create().uv(139, 231).cuboid(-5.5F, -7.0F, -10.5F, 11.0F, 2.0F, 4.0F, new Dilation(0.0F))
                .uv(213, 149).cuboid(-6.0F, -4.0F, -11.5F, 12.0F, 2.0F, 5.0F, new Dilation(0.0F))
                .uv(204, 98).cuboid(-6.5F, -1.0F, -12.5F, 13.0F, 2.0F, 6.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -1.0F, 0.0F, 0.0F, 2.0944F, 0.0F));

        ModelPartData cube_r108 = Side5.addChild("cube_r108", ModelPartBuilder.create().uv(245, 107).cuboid(-6.0F, -9.0F, 0.0F, 12.0F, 9.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 1.0F, -11.75F, -0.2618F, 0.0F, 0.0F));

        ModelPartData Side6 = ConsoleBase.addChild("Side6", ModelPartBuilder.create().uv(139, 231).cuboid(-5.5F, -7.0F, -10.5F, 11.0F, 2.0F, 4.0F, new Dilation(0.0F))
                .uv(213, 149).cuboid(-6.0F, -4.0F, -11.5F, 12.0F, 2.0F, 5.0F, new Dilation(0.0F))
                .uv(204, 98).cuboid(-6.5F, -1.0F, -12.5F, 13.0F, 2.0F, 6.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -1.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData cube_r109 = Side6.addChild("cube_r109", ModelPartBuilder.create().uv(245, 107).cuboid(-6.0F, -9.0F, 0.0F, 12.0F, 9.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 1.0F, -11.75F, -0.2618F, 0.0F, 0.0F));
        return TexturedModelData.of(modelData, 512, 512);
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
        //ModelPart throttle = this.console.getChild("throttle");
        //throttle.pitch = throttle.pitch + ((tardis.travel().speed() / (float) tardis.travel().maxSpeed().get()) * 1.5f);

        // Handbrake Control and Lights
        //ModelPart handbrake = this.console.getChild("bone7").getChild("panelf").getChild("handbrake");
        //handbrake.pitch = !tardis.travel().handbrake() ? handbrake.pitch - -0.57f : handbrake.pitch;


//
//        // Anti Gravity Control
//        ModelPart antigravs = this.console.getChild("panel1").getChild("controls").getChild("faucettaps1")
//                .getChild("pivot2");
//        antigravs.yaw = tardis.travel().antigravs().get() ? antigravs.yaw - 1.58f : antigravs.yaw;
//
//        ModelPart shield = this.console.getChild("panel1").getChild("controls").getChild("faucettaps2");
//        shield.yaw = tardis.shields().shielded().get()
//                ? shield.yaw - 1.58f
//                : shield.yaw;
//
        // Door Locking Mechanism Control
        //ModelPart doorlock = this.console.getChild("bone7").getChild("panelf5").getChild("doors");
        //doorlock.pitch = tardis.door().locked() ? doorlock.pitch + 0.5f : doorlock.pitch;
//
//        // Door Control
//        ModelPart doorControl = this.console.getChild("panel1").getChild("controls").getChild("power");
//        doorControl.pitch = tardis.door().isLeftOpen()
//                ? doorControl.pitch - 1f
//                : tardis.door().isRightOpen() ? doorControl.pitch - 1.55f : doorControl.pitch;
//        ModelPart doorControlLights = this.console.getChild("panel1").getChild("controls").getChild("powerlights")
//                .getChild("powerlights2");
//        doorControlLights.pivotY = !(tardis.door().isOpen()) ? doorControlLights.pivotY : doorControlLights.pivotY + 1;
//
//        // Alarm Control and Lights
//        ModelPart alarms = this.console.getChild("panel4").getChild("controls4").getChild("coloredlever2");
//        ModelPart alarmsLight = this.console.getChild("panel4").getChild("yellow3");
//        alarmsLight.pivotY = (tardis.alarm().enabled().get()) ? alarmsLight.pivotY : alarmsLight.pivotY + 1;
//        alarms.pitch = tardis.alarm().enabled().get() ? alarms.pitch + 1f : alarms.pitch;
//
//        ModelPart security = this.console.getChild("panel4").getChild("controls4").getChild("coloredlever5");
//        security.pitch = tardis.stats().security().get() ? security.pitch + 1f : security.pitch;
//
//        // Auto Pilot Control
//        ModelPart autopilot = this.console.getChild("panel4").getChild("controls4").getChild("tinyswitch2");
//        ModelPart autopilotLight = this.console.getChild("panel4").getChild("yellow4");
//
//        autopilot.pitch = tardis.travel().autopilot() ? autopilot.pitch + 1f : autopilot.pitch - 1f;
//        autopilotLight.pivotY = tardis.travel().autopilot() ? autopilotLight.pivotY : autopilotLight.pivotY + 1;
//
//        // Siege Mode Control
//        ModelPart siegeMode = this.console.getChild("panel2").getChild("controls3").getChild("siegemode")
//                .getChild("siegemodehandle");
//        siegeMode.pitch = tardis.siege().isActive() ? siegeMode.pitch + 1.55f : siegeMode.pitch;
//
//        // Fuel Gauge
//        ModelPart fuelGauge = this.console.getChild("panel1").getChild("controls").getChild("geigercounter")
//                .getChild("needle");
//        fuelGauge.pivotX = fuelGauge.pivotX + 0.25f;
//        fuelGauge.pivotZ = fuelGauge.pivotZ + 0.25f;
//        fuelGauge.yaw = (float) (((tardis.getFuel() / FuelHandler.TARDIS_MAX_FUEL) * 2) - 1);
//
//        // Refuel Light Warning
//        ModelPart fuelWarning = this.console.getChild("panel4").getChild("yellow5");
//        fuelWarning.pivotY = !(tardis.getFuel() > (tardis.getFuel() / 10))
//                ? fuelWarning.pivotY
//                : fuelWarning.pivotY + 1;
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
//        // Increment Control
//        ModelPart increment = this.console.getChild("panel2").getChild("controls3").getChild("gears")
//                .getChild("largegear2");
//        increment.yaw = IncrementManager.increment(tardis) >= 10
//                ? IncrementManager.increment(tardis) >= 100
//                ? IncrementManager.increment(tardis) >= 1000
//                ? IncrementManager.increment(tardis) >= 10000
//                ? increment.yaw + 1.5f
//                : increment.yaw + 1.25f
//                : increment.yaw + 1f
//                : increment.yaw + 0.5f
//                : increment.yaw;
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