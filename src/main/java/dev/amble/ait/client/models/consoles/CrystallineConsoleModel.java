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

import dev.amble.ait.client.animation.console.crystalline.CrystallineAnimations;
import dev.amble.ait.client.tardis.ClientTardis;
import dev.amble.ait.core.blockentities.ConsoleBlockEntity;
import dev.amble.ait.core.tardis.Tardis;
import dev.amble.ait.core.tardis.control.impl.DirectionControl;
import dev.amble.ait.core.tardis.control.impl.pos.IncrementManager;
import dev.amble.ait.core.tardis.handler.FuelHandler;
import dev.amble.ait.core.tardis.handler.travel.TravelHandler;
import dev.amble.ait.core.tardis.handler.travel.TravelHandlerBase;
import dev.amble.ait.core.util.WorldUtil;

public class CrystallineConsoleModel extends ConsoleModel {
    private final ModelPart console;
    public CrystallineConsoleModel(ModelPart root) {
        this.console = root.getChild("console");
    }
    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData console = modelPartData.addChild("console", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 24.0F, 0.0F));

        ModelPartData pannel7 = console.addChild("pannel7", ModelPartBuilder.create().uv(0, 184).cuboid(5.5F, -25.3F, -3.5F, 2.0F, 6.0F, 7.0F, new Dilation(0.0F))
                .uv(186, 16).cuboid(7.3F, -24.5F, -4.5F, 2.0F, 0.0F, 9.0F, new Dilation(0.001F))
                .uv(53, 211).cuboid(6.3189F, -24.7F, -2.0F, 2.0F, 1.0F, 4.0F, new Dilation(0.01F))
                .uv(122, 185).cuboid(5.1F, -25.3F, -3.5F, 2.0F, 6.0F, 7.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 2.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData cube_r1 = pannel7.addChild("cube_r1", ModelPartBuilder.create().uv(189, 211).cuboid(-1.0F, -0.5F, -2.5F, 2.0F, 1.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(8.0F, -24.2F, 4.0F, 0.0F, 0.2618F, 0.0F));

        ModelPartData cube_r2 = pannel7.addChild("cube_r2", ModelPartBuilder.create().uv(147, 178).cuboid(-1.0F, -0.5F, -0.5F, 2.0F, 1.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(8.0F, -24.2F, -4.0F, 0.0F, -0.2618F, 0.0F));

        ModelPartData pillars56 = pannel7.addChild("pillars56", ModelPartBuilder.create(), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -2.0944F, 0.0F));

        ModelPartData pillars57 = pillars56.addChild("pillars57", ModelPartBuilder.create().uv(130, 102).cuboid(-0.999F, -24.7252F, -11.2925F, 2.0F, 1.0F, 3.0F, new Dilation(0.1F))
                .uv(60, 110).cuboid(-1.0F, -22.0637F, -9.2598F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F))
                .uv(95, 183).cuboid(-1.0F, -20.8637F, -9.2598F, 2.0F, 21.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData cube_r3 = pillars57.addChild("cube_r3", ModelPartBuilder.create().uv(174, 213).cuboid(-1.0F, -2.6472F, -2.6383F, 2.0F, 3.0F, 2.0F, new Dilation(0.01F)), ModelTransform.of(0.0F, -0.1842F, -9.915F, -0.9599F, 0.0F, 0.0F));

        ModelPartData cube_r4 = pillars57.addChild("cube_r4", ModelPartBuilder.create().uv(35, 93).cuboid(-13.3F, -15.05F, -20.45F, 2.0F, 1.0F, 15.0F, new Dilation(0.01F)), ModelTransform.of(4.4261F, 0.4143F, -11.6486F, 0.0F, 1.0472F, 0.0F));

        ModelPartData cube_r5 = pillars57.addChild("cube_r5", ModelPartBuilder.create().uv(216, 27).cuboid(-0.1F, -0.2F, -10.3F, 1.0F, 1.0F, 1.0F, new Dilation(-0.3F))
                .uv(216, 27).cuboid(-1.0F, -0.2F, -10.3F, 1.0F, 1.0F, 1.0F, new Dilation(-0.3F))
                .uv(137, 213).cuboid(-1.0F, -0.2F, -9.8F, 2.0F, 1.0F, 3.0F, new Dilation(-0.1F)), ModelTransform.of(0.001F, -19.3098F, -5.0285F, -0.7854F, 0.0F, 0.0F));

        ModelPartData cube_r6 = pillars57.addChild("cube_r6", ModelPartBuilder.create().uv(25, 114).cuboid(-1.0F, -0.5F, -1.5F, 0.0F, 6.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(2.2279F, -24.3252F, -9.9853F, 0.0F, 0.0873F, 0.0F));

        ModelPartData cube_r7 = pillars57.addChild("cube_r7", ModelPartBuilder.create().uv(209, 201).cuboid(-1.0F, -0.5351F, -0.0031F, 2.0F, 2.0F, 4.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, -24.495F, -8.7766F, -1.1345F, 0.0F, 0.0F));

        ModelPartData cube_r8 = pillars57.addChild("cube_r8", ModelPartBuilder.create().uv(84, 215).cuboid(22.7F, -3.7873F, -41.3554F, 2.0F, 3.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(-23.7F, 8.2565F, 12.0063F, -0.5672F, 0.0F, 0.0F));

        ModelPartData spinnio = pillars57.addChild("spinnio", ModelPartBuilder.create().uv(183, 213).cuboid(-1.1167F, -1.2167F, -0.1667F, 2.0F, 2.0F, 0.0F, new Dilation(0.001F))
                .uv(215, 132).cuboid(-0.3167F, -0.0167F, -1.4667F, 1.0F, 1.0F, 2.0F, new Dilation(-0.65F))
                .uv(215, 132).cuboid(-0.5667F, -0.7667F, -0.3667F, 1.0F, 1.0F, 2.0F, new Dilation(-0.23F)), ModelTransform.of(-0.1085F, -16.2678F, -21.9433F, -0.3486F, 0.1757F, -0.0873F));

        ModelPartData pillars58 = pillars57.addChild("pillars58", ModelPartBuilder.create(), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData pillars59 = pillars58.addChild("pillars59", ModelPartBuilder.create(), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData pillars60 = pillars59.addChild("pillars60", ModelPartBuilder.create(), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData pillars61 = pillars56.addChild("pillars61", ModelPartBuilder.create().uv(35, 89).cuboid(-0.999F, -24.7252F, -10.2925F, 2.0F, 1.0F, 2.0F, new Dilation(0.0F))
                .uv(82, 76).cuboid(-1.0F, -14.6347F, -22.4402F, 2.0F, 1.0F, 15.0F, new Dilation(0.01F))
                .uv(60, 110).cuboid(-1.0F, -22.0637F, -9.2598F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F))
                .uv(95, 183).cuboid(-1.0F, -20.8637F, -9.2598F, 2.0F, 21.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData cube_r9 = pillars61.addChild("cube_r9", ModelPartBuilder.create().uv(209, 201).cuboid(-1.0F, -0.5351F, -0.0031F, 2.0F, 2.0F, 4.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, -24.495F, -8.7766F, -1.1345F, 0.0F, 0.0F));

        ModelPartData pillars62 = pillars61.addChild("pillars62", ModelPartBuilder.create(), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData pillars63 = pillars62.addChild("pillars63", ModelPartBuilder.create(), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData pillars64 = pillars63.addChild("pillars64", ModelPartBuilder.create(), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData rim13 = pannel7.addChild("rim13", ModelPartBuilder.create().uv(25, 128).cuboid(18.5F, -14.3F, -5.5F, 2.0F, 1.0F, 11.0F, new Dilation(0.001F))
                .uv(173, 111).cuboid(19.5F, -13.5F, -5.5F, 0.0F, 1.0F, 11.0F, new Dilation(0.0F))
                .uv(141, 196).cuboid(19.0F, -13.3F, 1.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.1F))
                .uv(141, 196).cuboid(19.0F, -13.3F, -2.9F, 1.0F, 1.0F, 1.0F, new Dilation(-0.1F)), ModelTransform.pivot(0.0F, -1.0F, 0.0F));

        ModelPartData cube_r10 = rim13.addChild("cube_r10", ModelPartBuilder.create().uv(141, 196).cuboid(20.0F, -15.8F, -5.7F, 1.0F, 1.0F, 1.0F, new Dilation(-0.1F))
                .uv(141, 196).cuboid(20.0F, -15.8F, -4.4F, 1.0F, 1.0F, 1.0F, new Dilation(-0.1F))
                .uv(141, 196).cuboid(20.0F, -15.8F, -3.1F, 1.0F, 1.0F, 1.0F, new Dilation(-0.1F))
                .uv(171, 146).cuboid(20.5F, -16.0F, -5.5F, 0.0F, 1.0F, 5.0F, new Dilation(0.0F)), ModelTransform.of(-1.0F, 2.5F, 0.0F, 0.0F, -0.5236F, 0.0F));

        ModelPartData cube_r11 = rim13.addChild("cube_r11", ModelPartBuilder.create().uv(141, 196).cuboid(20.0F, -15.8F, 3.2F, 1.0F, 1.0F, 1.0F, new Dilation(-0.1F))
                .uv(141, 196).cuboid(20.0F, -15.8F, 1.9F, 1.0F, 1.0F, 1.0F, new Dilation(-0.1F))
                .uv(141, 196).cuboid(20.0F, -15.8F, 4.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.1F)), ModelTransform.of(-1.0F, 2.5F, 0.0F, 0.0F, 0.5236F, 0.0F));

        ModelPartData cube_r12 = rim13.addChild("cube_r12", ModelPartBuilder.create().uv(91, 207).cuboid(19.7F, -15.8F, 1.2F, 0.0F, 1.0F, 6.0F, new Dilation(0.0F)), ModelTransform.of(-1.1607F, 2.3F, -1.8854F, 0.0F, 0.5236F, 0.0F));

        ModelPartData cube_r13 = rim13.addChild("cube_r13", ModelPartBuilder.create().uv(206, 103).cuboid(18.5F, -16.0F, -5.5F, 2.0F, 1.0F, 5.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 1.7F, 0.0F, 0.0F, -0.5236F, 0.0F));

        ModelPartData cube_r14 = rim13.addChild("cube_r14", ModelPartBuilder.create().uv(205, 157).cuboid(18.5F, -16.0F, -0.5F, 2.0F, 1.0F, 6.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 1.7F, 0.0F, 0.0F, 0.5236F, 0.0F));

        ModelPartData rim14 = pannel7.addChild("rim14", ModelPartBuilder.create(), ModelTransform.pivot(-11.2F, -11.4F, 0.0F));

        ModelPartData panels7 = pannel7.addChild("panels7", ModelPartBuilder.create().uv(53, 199).cuboid(11.2348F, -20.4107F, 1.2F, 1.0F, 2.0F, 1.0F, new Dilation(-0.2F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData cube_r15 = panels7.addChild("cube_r15", ModelPartBuilder.create().uv(215, 132).cuboid(-2.5775F, -0.875F, 14.5775F, 1.0F, 1.0F, 2.0F, new Dilation(-0.23F)), ModelTransform.of(5.9965F, -17.0097F, 22.6784F, 0.0F, 2.618F, 0.0F));

        ModelPartData cube_r16 = panels7.addChild("cube_r16", ModelPartBuilder.create().uv(188, 216).cuboid(5.8F, -22.0F, -2.0F, 1.0F, 0.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(2.2F, -0.8F, -0.4F, 0.0F, 0.0F, 0.4363F));

        ModelPartData cube_r17 = panels7.addChild("cube_r17", ModelPartBuilder.create().uv(216, 49).cuboid(-0.5F, -0.1F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.05F)), ModelTransform.of(17.0806F, -17.8044F, -1.9F, 3.1416F, 0.0F, 0.4363F));

        ModelPartData cube_r18 = panels7.addChild("cube_r18", ModelPartBuilder.create().uv(216, 46).cuboid(-0.3F, 0.16F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.1F)), ModelTransform.of(17.3341F, -18.3482F, -1.9F, 0.0F, -0.7854F, 0.4363F));

        ModelPartData cube_r19 = panels7.addChild("cube_r19", ModelPartBuilder.create().uv(193, 187).cuboid(-1.2F, -0.5F, -2.0F, 1.0F, 1.0F, 2.0F, new Dilation(-0.3F)), ModelTransform.of(16.8754F, -17.3277F, -1.9F, 1.5708F, 0.0F, -1.117F));

        ModelPartData cube_r20 = panels7.addChild("cube_r20", ModelPartBuilder.create().uv(216, 46).cuboid(-0.3F, 0.16F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.1F)), ModelTransform.of(14.8341F, -19.7482F, -0.8F, 0.0F, -0.7854F, 0.4363F));

        ModelPartData cube_r21 = panels7.addChild("cube_r21", ModelPartBuilder.create().uv(193, 187).cuboid(-1.2F, -0.5F, -2.0F, 1.0F, 1.0F, 2.0F, new Dilation(-0.3F)), ModelTransform.of(14.3754F, -18.7277F, -0.8F, 1.5708F, 0.0F, -1.117F));

        ModelPartData cube_r22 = panels7.addChild("cube_r22", ModelPartBuilder.create().uv(209, 215).cuboid(-0.5F, -1.6F, -1.5F, 1.0F, 1.0F, 2.0F, new Dilation(-0.15F)), ModelTransform.of(14.5444F, -19.0902F, 0.3F, 1.5708F, 0.0F, 0.4363F));

        ModelPartData cube_r23 = panels7.addChild("cube_r23", ModelPartBuilder.create().uv(216, 49).cuboid(-0.5F, -0.1F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.05F)), ModelTransform.of(14.5806F, -19.2044F, -0.8F, 3.1416F, 0.0F, 0.4363F));

        ModelPartData cube_r24 = panels7.addChild("cube_r24", ModelPartBuilder.create().uv(188, 216).cuboid(5.8F, -22.0F, -2.0F, 1.0F, 0.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-0.3F, -2.2F, 0.7F, 0.0F, 0.0F, 0.4363F));

        ModelPartData cube_r25 = panels7.addChild("cube_r25", ModelPartBuilder.create().uv(36, 195).cuboid(7.8F, -23.1F, 1.05F, 1.0F, 2.0F, 1.0F, new Dilation(-0.2F))
                .uv(177, 139).cuboid(7.8F, -21.8F, 1.05F, 1.0F, 3.0F, 1.0F, new Dilation(-0.1F))
                .uv(177, 139).cuboid(3.8F, -22.2F, 1.05F, 1.0F, 3.0F, 1.0F, new Dilation(-0.2F)), ModelTransform.of(0.0F, -1.0F, 0.2F, 0.0F, 0.0F, 0.4363F));

        ModelPartData spinnio_r1 = panels7.addChild("spinnio_r1", ModelPartBuilder.create().uv(117, 83).cuboid(-1.0F, -1.0F, -3.0F, 2.0F, 2.0F, 6.0F, new Dilation(-0.2F)), ModelTransform.of(12.1348F, -20.9107F, 0.0F, 0.0F, 0.0F, 1.2217F));

        ModelPartData cube_r26 = panels7.addChild("cube_r26", ModelPartBuilder.create().uv(209, 19).cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 0.0F, 4.0F, new Dilation(0.001F)), ModelTransform.of(12.6613F, -19.9284F, 3.4F, 0.2967F, 0.0F, 0.5934F));

        ModelPartData cube_r27 = panels7.addChild("cube_r27", ModelPartBuilder.create().uv(0, 212).cuboid(-0.5F, 0.0F, -3.0F, 1.0F, 0.0F, 5.0F, new Dilation(0.001F)), ModelTransform.of(11.4603F, -20.2706F, -4.0F, 0.3142F, 0.0F, 0.4363F));

        ModelPartData cube_r28 = panels7.addChild("cube_r28", ModelPartBuilder.create().uv(209, 14).cuboid(-2.0F, 0.1F, -2.0F, 4.0F, 0.0F, 4.0F, new Dilation(0.001F)), ModelTransform.of(10.3893F, -22.8843F, 0.25F, 0.0F, 0.0F, 0.4363F));

        ModelPartData cube_r29 = panels7.addChild("cube_r29", ModelPartBuilder.create().uv(13, 213).cuboid(-1.5F, -1.0F, -1.5F, 3.0F, 2.0F, 3.0F, new Dilation(-0.2F)), ModelTransform.of(9.2836F, -22.1862F, 0.25F, 0.0F, 0.7854F, 0.4363F));

        ModelPartData cube_r30 = panels7.addChild("cube_r30", ModelPartBuilder.create().uv(141, 185).cuboid(-1.1F, -25.0F, -0.25F, 1.0F, 4.0F, 1.0F, new Dilation(-0.1F)), ModelTransform.of(-0.4F, 0.0F, 0.0F, 0.0F, 0.0F, 0.4363F));

        ModelPartData cube_r31 = panels7.addChild("cube_r31", ModelPartBuilder.create().uv(0, 19).cuboid(-5.2F, -22.2F, -9.0F, 16.0F, 0.0F, 18.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.4363F));

        ModelPartData cube_r32 = panels7.addChild("cube_r32", ModelPartBuilder.create().uv(178, 92).cuboid(-4.0F, 0.0F, -1.8F, 8.0F, 0.0F, 5.0F, new Dilation(0.001F)), ModelTransform.of(15.4953F, -16.9867F, -4.5F, 0.0523F, 0.784F, 0.4667F));

        ModelPartData cube_r33 = panels7.addChild("cube_r33", ModelPartBuilder.create().uv(166, 185).cuboid(-4.0F, 0.0F, -2.5F, 8.0F, 0.0F, 5.0F, new Dilation(0.001F)), ModelTransform.of(15.6953F, -16.9867F, 4.5F, -0.0523F, -0.784F, 0.4667F));

        ModelPartData cube_r34 = panels7.addChild("cube_r34", ModelPartBuilder.create().uv(0, 0).cuboid(-4.0F, -21.5F, -9.0F, 16.0F, 0.0F, 18.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.3927F));

        ModelPartData handle = panels7.addChild("handle", ModelPartBuilder.create(), ModelTransform.pivot(13.1947F, -19.1215F, 1.75F));

        ModelPartData cube_r35 = handle.addChild("cube_r35", ModelPartBuilder.create().uv(210, 43).cuboid(3.8F, -22.0F, 1.05F, 1.0F, 0.0F, 2.0F, new Dilation(0.001F)), ModelTransform.of(-13.1947F, 18.1215F, -1.55F, 0.0F, 0.0F, 0.4363F));

        ModelPartData cube1 = panels7.addChild("cube1", ModelPartBuilder.create(), ModelTransform.pivot(14.3331F, -18.6371F, -0.7F));

        ModelPartData cube1_r1 = cube1.addChild("cube1_r1", ModelPartBuilder.create().uv(209, 215).cuboid(-0.5F, -0.5F, -2.0F, 1.0F, 1.0F, 2.0F, new Dilation(-0.2F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 3.1416F, 0.0F, 0.4363F));

        ModelPartData cube2 = panels7.addChild("cube2", ModelPartBuilder.create(), ModelTransform.pivot(17.0444F, -17.6902F, -0.8F));

        ModelPartData cube2_r1 = cube2.addChild("cube2_r1", ModelPartBuilder.create().uv(209, 215).cuboid(-0.5F, -1.6F, -1.5F, 1.0F, 1.0F, 2.0F, new Dilation(-0.15F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 1.5708F, 0.0F, 0.4363F));

        ModelPartData bone4 = panels7.addChild("bone4", ModelPartBuilder.create(), ModelTransform.pivot(16.8331F, -17.2371F, -2.0F));

        ModelPartData cube_r36 = bone4.addChild("cube_r36", ModelPartBuilder.create().uv(209, 215).cuboid(-0.5F, -0.5F, -2.0F, 1.0F, 1.0F, 2.0F, new Dilation(-0.2F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 3.1416F, 0.0F, 0.4363F));

        ModelPartData power = panels7.addChild("power", ModelPartBuilder.create(), ModelTransform.pivot(15.0228F, -16.9847F, 10.9994F));

        ModelPartData cube_r37 = power.addChild("cube_r37", ModelPartBuilder.create().uv(215, 132).cuboid(-2.3275F, -0.125F, 13.4775F, 1.0F, 1.0F, 2.0F, new Dilation(-0.65F))
                .uv(183, 213).cuboid(-3.1275F, -1.325F, 14.7775F, 2.0F, 2.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(-9.0262F, -0.025F, 11.679F, 0.0F, 2.618F, 0.0F));

        ModelPartData under7 = pannel7.addChild("under7", ModelPartBuilder.create().uv(0, 76).cuboid(6.9F, -14.3F, -5.5F, 12.0F, 1.0F, 11.0F, new Dilation(0.001F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData cube_r38 = under7.addChild("cube_r38", ModelPartBuilder.create().uv(117, 76).cuboid(9.5F, -16.0F, -5.5F, 11.0F, 1.0F, 5.0F, new Dilation(0.0F)), ModelTransform.of(-1.6F, 1.7F, 0.0F, 0.0F, -0.5236F, 0.0F));

        ModelPartData cube_r39 = under7.addChild("cube_r39", ModelPartBuilder.create().uv(138, 147).cuboid(9.5F, -16.0F, 0.5F, 11.0F, 1.0F, 5.0F, new Dilation(0.0F)), ModelTransform.of(-1.6F, 1.7F, 0.0F, 0.0F, 0.5236F, 0.0F));

        ModelPartData pannel2 = console.addChild("pannel2", ModelPartBuilder.create().uv(0, 184).cuboid(5.5F, -25.3F, -3.5F, 2.0F, 6.0F, 7.0F, new Dilation(0.0F))
                .uv(186, 16).cuboid(7.3F, -24.5F, -4.5F, 2.0F, 0.0F, 9.0F, new Dilation(0.001F))
                .uv(53, 211).cuboid(6.3189F, -24.7F, -2.0F, 2.0F, 1.0F, 4.0F, new Dilation(0.01F))
                .uv(122, 185).cuboid(5.1F, -25.3F, -3.5F, 2.0F, 6.0F, 7.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 2.0F, 0.0F, 0.0F, 2.0944F, 0.0F));

        ModelPartData cube_r40 = pannel2.addChild("cube_r40", ModelPartBuilder.create().uv(189, 211).cuboid(-1.0F, -0.5F, -2.5F, 2.0F, 1.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(8.0F, -24.2F, 4.0F, 0.0F, 0.2618F, 0.0F));

        ModelPartData cube_r41 = pannel2.addChild("cube_r41", ModelPartBuilder.create().uv(147, 178).cuboid(-1.0F, -0.5F, -0.5F, 2.0F, 1.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(8.0F, -24.2F, -4.0F, 0.0F, -0.2618F, 0.0F));

        ModelPartData pillars2 = pannel2.addChild("pillars2", ModelPartBuilder.create(), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -2.0944F, 0.0F));

        ModelPartData pillars3 = pillars2.addChild("pillars3", ModelPartBuilder.create().uv(130, 102).cuboid(-0.999F, -24.7252F, -11.2925F, 2.0F, 1.0F, 3.0F, new Dilation(0.1F))
                .uv(215, 187).cuboid(-0.999F, -26.5252F, -9.2925F, 2.0F, 2.0F, 1.0F, new Dilation(0.001F))
                .uv(215, 191).cuboid(-0.999F, -26.5252F, 8.2925F, 2.0F, 2.0F, 1.0F, new Dilation(0.001F))
                .uv(60, 110).cuboid(-1.0F, -22.0637F, -9.2598F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F))
                .uv(95, 183).cuboid(-1.0F, -20.8637F, -9.2598F, 2.0F, 21.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData cube_r42 = pillars3.addChild("cube_r42", ModelPartBuilder.create().uv(209, 201).cuboid(-1.0F, -0.5351F, -0.0031F, 2.0F, 2.0F, 4.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, -24.495F, -8.7766F, -1.1345F, 0.0F, 0.0F));

        ModelPartData cube_r43 = pillars3.addChild("cube_r43", ModelPartBuilder.create().uv(69, 150).cuboid(-1.2F, -4.6049F, -6.876F, 2.0F, 10.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, -12.7212F, -12.7214F, -1.2654F, 0.0F, 0.0F));

        ModelPartData cube_r44 = pillars3.addChild("cube_r44", ModelPartBuilder.create().uv(84, 215).cuboid(21.0F, -3.5261F, -42.1375F, 2.0F, 3.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(-21.999F, 8.4485F, 12.8146F, -0.5672F, 0.0F, 0.0F));

        ModelPartData leveer = pillars3.addChild("leveer", ModelPartBuilder.create(), ModelTransform.pivot(0.001F, -26.5205F, -8.2399F));

        ModelPartData cube_r45 = leveer.addChild("cube_r45", ModelPartBuilder.create().uv(216, 0).cuboid(-1.0F, 0.5F, -0.5F, 2.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(141, 121).cuboid(-1.0F, -1.5F, 0.0F, 2.0F, 3.0F, 1.0F, new Dilation(0.0F))
                .uv(52, 128).cuboid(-1.0F, -1.5F, -0.5F, 2.0F, 3.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -0.4547F, -1.5687F, 0.9599F, 0.0F, 0.0F));

        ModelPartData leveer2 = pillars3.addChild("leveer2", ModelPartBuilder.create(), ModelTransform.pivot(0.001F, -26.4752F, 8.2086F));

        ModelPartData cube_r46 = leveer2.addChild("cube_r46", ModelPartBuilder.create().uv(216, 3).cuboid(-1.0F, 0.5F, -0.5F, 2.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(215, 127).cuboid(-1.0F, -1.5F, -1.0F, 2.0F, 3.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -0.5F, 1.6F, -0.9599F, 0.0F, 0.0F));

        ModelPartData cube_r47 = leveer2.addChild("cube_r47", ModelPartBuilder.create().uv(148, 213).cuboid(-1.0F, -3.25F, -0.5F, 2.0F, 3.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.5038F, 0.1665F, -0.9599F, 0.0F, 0.0F));

        ModelPartData pillars4 = pillars3.addChild("pillars4", ModelPartBuilder.create(), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData pillars5 = pillars4.addChild("pillars5", ModelPartBuilder.create(), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData pillars6 = pillars5.addChild("pillars6", ModelPartBuilder.create(), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData pillars7 = pillars2.addChild("pillars7", ModelPartBuilder.create().uv(35, 89).cuboid(-0.999F, -24.7252F, -10.2925F, 2.0F, 1.0F, 2.0F, new Dilation(0.0F))
                .uv(47, 76).cuboid(-1.001F, -14.6268F, -22.4484F, 2.0F, 1.0F, 15.0F, new Dilation(0.01F))
                .uv(60, 110).cuboid(-1.0F, -22.0637F, -9.2598F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F))
                .uv(95, 183).cuboid(-1.0F, -20.8637F, -9.2598F, 2.0F, 21.0F, 2.0F, new Dilation(0.0F))
                .uv(17, 204).cuboid(-1.0F, -1.8637F, -15.2598F, 2.0F, 2.0F, 6.0F, new Dilation(-0.01F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData cube_r48 = pillars7.addChild("cube_r48", ModelPartBuilder.create().uv(66, 211).cuboid(-1.0F, -2.6472F, -2.6383F, 2.0F, 5.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.4737F, -13.1113F, -1.4399F, 0.0F, 0.0F));

        ModelPartData cube_r49 = pillars7.addChild("cube_r49", ModelPartBuilder.create().uv(209, 201).cuboid(-1.0F, -0.5351F, -0.0031F, 2.0F, 2.0F, 4.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, -24.495F, -8.7766F, -1.1345F, 0.0F, 0.0F));

        ModelPartData cube_r50 = pillars7.addChild("cube_r50", ModelPartBuilder.create().uv(69, 150).cuboid(-1.2F, -4.6049F, -6.876F, 2.0F, 10.0F, 0.0F, new Dilation(0.001F))
                .uv(200, 201).cuboid(-1.0F, -8.6049F, -6.676F, 2.0F, 15.0F, 2.0F, new Dilation(0.01F)), ModelTransform.of(0.0F, -12.7212F, -12.7214F, -1.2654F, 0.0F, 0.0F));

        ModelPartData pillars8 = pillars7.addChild("pillars8", ModelPartBuilder.create(), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData pillars9 = pillars8.addChild("pillars9", ModelPartBuilder.create(), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData pillars10 = pillars9.addChild("pillars10", ModelPartBuilder.create(), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone65 = pannel2.addChild("bone65", ModelPartBuilder.create(), ModelTransform.pivot(4.0F, 0.7F, 16.0F));

        ModelPartData cube_r51 = bone65.addChild("cube_r51", ModelPartBuilder.create().uv(156, 199).cuboid(-3.5F, -17.5F, -21.6F, 2.0F, 17.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -0.5236F, 0.0F));

        ModelPartData cube_r52 = bone65.addChild("cube_r52", ModelPartBuilder.create().uv(113, 214).cuboid(-1.0F, -1.0F, 0.0F, 2.0F, 2.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(12.0577F, -17.5624F, -9.6158F, 1.5272F, 1.0472F, 0.0F));

        ModelPartData cube_r53 = bone65.addChild("cube_r53", ModelPartBuilder.create().uv(113, 214).cuboid(-0.5005F, 1.9782F, 0.0F, 2.0F, 2.0F, 0.0F, new Dilation(0.001F))
                .uv(104, 214).cuboid(-0.5005F, 3.9782F, -1.0F, 2.0F, 3.0F, 2.0F, new Dilation(-0.002F)), ModelTransform.of(9.4621F, -17.1933F, -11.1144F, 0.5232F, -0.0218F, -1.533F));

        ModelPartData cube_r54 = bone65.addChild("cube_r54", ModelPartBuilder.create().uv(186, 26).cuboid(-0.7691F, 5.5565F, -1.0F, 2.0F, 1.0F, 2.0F, new Dilation(-0.001F)), ModelTransform.of(12.3888F, -22.7473F, -9.4247F, 0.2605F, -0.4595F, -0.5412F));

        ModelPartData cube_r55 = bone65.addChild("cube_r55", ModelPartBuilder.create().uv(156, 199).cuboid(19.6F, -17.5F, -3.5F, 2.0F, 17.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(-4.0F, 0.0F, -16.0F, 0.0F, -0.5236F, 0.0F));

        ModelPartData cube_r56 = bone65.addChild("cube_r56", ModelPartBuilder.create().uv(93, 215).cuboid(-1.0F, 0.0F, -1.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(-1.4333F, -9.6213F, -27.5081F, -1.5708F, 1.0472F, 0.0F));

        ModelPartData cube_r57 = bone65.addChild("cube_r57", ModelPartBuilder.create().uv(75, 215).cuboid(-1.0F, 0.0F, -1.0F, 2.0F, 3.0F, 2.0F, new Dilation(-0.001F)), ModelTransform.of(-2.0456F, -9.9142F, -27.8617F, 0.7854F, 1.0472F, 0.0F));

        ModelPartData cube_r58 = bone65.addChild("cube_r58", ModelPartBuilder.create().uv(75, 133).cuboid(-23.0F, 0.5F, -8.0F, 2.0F, 6.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(16.6F, -9.0F, -42.5F, 0.0F, 1.0472F, 0.0F));

        ModelPartData cube_r59 = bone65.addChild("cube_r59", ModelPartBuilder.create().uv(215, 119).mirrored().cuboid(-1.0F, -1.5F, -1.0F, 2.0F, 1.0F, 2.0F, new Dilation(-0.001F)).mirrored(false), ModelTransform.of(6.0217F, -1.7808F, -15.4433F, -3.1416F, 0.1745F, -3.1416F));

        ModelPartData cube_r60 = bone65.addChild("cube_r60", ModelPartBuilder.create().uv(85, 115).mirrored().cuboid(-1.0F, -1.5F, -1.0F, 2.0F, 7.0F, 2.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(5.7056F, -8.1054F, -13.6504F, -2.7925F, 0.1745F, -3.1416F));

        ModelPartData cube_r61 = bone65.addChild("cube_r61", ModelPartBuilder.create().uv(93, 215).mirrored().cuboid(-1.0F, -1.5F, -1.0F, 2.0F, 2.0F, 2.0F, new Dilation(-0.001F)).mirrored(false), ModelTransform.of(5.6332F, -9.1237F, -13.2395F, -1.9635F, 0.1745F, -3.1416F));

        ModelPartData cube_r62 = bone65.addChild("cube_r62", ModelPartBuilder.create().uv(75, 215).mirrored().cuboid(-1.0F, -6.5F, -1.0F, 2.0F, 3.0F, 2.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(6.067F, -9.6213F, -15.7F, -1.5708F, 0.1745F, -3.1416F));

        ModelPartData cube_r63 = bone65.addChild("cube_r63", ModelPartBuilder.create().uv(156, 199).cuboid(19.6F, -17.5F, -3.5F, 2.0F, 17.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(-20.0F, 0.0F, -18.0F, 0.0F, -0.5236F, 0.0F));

        ModelPartData cube_r64 = bone65.addChild("cube_r64", ModelPartBuilder.create().uv(75, 133).mirrored().cuboid(-10.0F, -3.0F, -1.0F, 2.0F, 6.0F, 2.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(8.0217F, -5.6213F, -11.4919F, 0.0F, 0.5236F, 0.0F));

        ModelPartData cube_r65 = bone65.addChild("cube_r65", ModelPartBuilder.create().uv(165, 205).mirrored().cuboid(-1.0F, 0.0F, -1.0F, 2.0F, 13.0F, 2.0F, new Dilation(-0.001F)).mirrored(false), ModelTransform.of(-5.8347F, -9.6213F, -3.4919F, -1.5708F, 1.0472F, -3.1416F));

        ModelPartData cube_r66 = bone65.addChild("cube_r66", ModelPartBuilder.create().uv(93, 215).mirrored().cuboid(-1.0F, 0.0F, -1.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(-6.5667F, -9.6213F, -4.4919F, 1.5708F, 1.0472F, 0.0F));

        ModelPartData cube_r67 = bone65.addChild("cube_r67", ModelPartBuilder.create().uv(75, 215).mirrored().cuboid(-1.0F, 0.0F, -1.0F, 2.0F, 3.0F, 2.0F, new Dilation(-0.001F)).mirrored(false), ModelTransform.of(-5.9544F, -9.9142F, -4.1383F, -0.7854F, 1.0472F, 0.0F));

        ModelPartData cube_r68 = bone65.addChild("cube_r68", ModelPartBuilder.create().uv(75, 133).mirrored().cuboid(21.0F, 0.5F, 6.0F, 2.0F, 6.0F, 2.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(-24.6F, -9.0F, 10.5F, 0.0F, 1.0472F, 0.0F));

        ModelPartData cube_r69 = bone65.addChild("cube_r69", ModelPartBuilder.create().uv(156, 199).cuboid(1.5F, -17.5F, 19.6F, 2.0F, 17.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(-9.0F, 0.0F, -32.0F, 0.0F, -0.5236F, 0.0F));

        ModelPartData cube_r70 = bone65.addChild("cube_r70", ModelPartBuilder.create().uv(215, 119).cuboid(-1.0F, -1.5F, -1.0F, 2.0F, 1.0F, 2.0F, new Dilation(-0.001F)), ModelTransform.of(-14.0217F, -1.7808F, -16.5567F, 3.1416F, 0.1745F, 3.1416F));

        ModelPartData cube_r71 = bone65.addChild("cube_r71", ModelPartBuilder.create().uv(93, 215).cuboid(-1.0F, -1.5F, -1.0F, 2.0F, 2.0F, 2.0F, new Dilation(-0.001F)), ModelTransform.of(-13.6332F, -9.1237F, -18.7605F, 1.9635F, 0.1745F, 3.1416F));

        ModelPartData cube_r72 = bone65.addChild("cube_r72", ModelPartBuilder.create().uv(85, 115).cuboid(-1.0F, -1.5F, -1.0F, 2.0F, 7.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(-13.7056F, -8.1054F, -18.3496F, 2.7925F, 0.1745F, 3.1416F));

        ModelPartData cube_r73 = bone65.addChild("cube_r73", ModelPartBuilder.create().uv(75, 215).cuboid(-1.0F, -6.5F, -1.0F, 2.0F, 3.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(-14.067F, -9.6213F, -16.3F, 1.5708F, 0.1745F, 3.1416F));

        ModelPartData cube_r74 = bone65.addChild("cube_r74", ModelPartBuilder.create().uv(165, 205).cuboid(-1.0F, 0.0F, -1.0F, 2.0F, 13.0F, 2.0F, new Dilation(-0.001F)), ModelTransform.of(-2.1653F, -9.6213F, -28.5081F, 1.5708F, 1.0472F, 3.1416F));

        ModelPartData cube_r75 = bone65.addChild("cube_r75", ModelPartBuilder.create().uv(75, 133).cuboid(8.0F, -3.0F, -1.0F, 2.0F, 6.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(-16.0217F, -5.6213F, -20.5081F, 0.0F, 0.5236F, 0.0F));

        ModelPartData cube_r76 = bone65.addChild("cube_r76", ModelPartBuilder.create().uv(156, 199).cuboid(-21.6F, -17.5F, 1.5F, 2.0F, 17.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(12.0F, 0.0F, -14.0F, 0.0F, -0.5236F, 0.0F));

        ModelPartData rim2 = pannel2.addChild("rim2", ModelPartBuilder.create().uv(25, 128).cuboid(18.5F, -14.3F, -5.5F, 2.0F, 1.0F, 11.0F, new Dilation(0.001F))
                .uv(173, 111).cuboid(19.5F, -13.5F, -5.5F, 0.0F, 1.0F, 11.0F, new Dilation(0.0F))
                .uv(141, 196).cuboid(19.0F, -13.3F, 1.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.1F))
                .uv(141, 196).cuboid(19.0F, -13.3F, -2.9F, 1.0F, 1.0F, 1.0F, new Dilation(-0.1F)), ModelTransform.pivot(0.0F, -1.0F, 0.0F));

        ModelPartData cube_r77 = rim2.addChild("cube_r77", ModelPartBuilder.create().uv(141, 196).cuboid(20.0F, -15.8F, -5.7F, 1.0F, 1.0F, 1.0F, new Dilation(-0.1F))
                .uv(141, 196).cuboid(20.0F, -15.8F, -4.4F, 1.0F, 1.0F, 1.0F, new Dilation(-0.1F))
                .uv(141, 196).cuboid(20.0F, -15.8F, -3.1F, 1.0F, 1.0F, 1.0F, new Dilation(-0.1F))
                .uv(171, 146).cuboid(19.7F, -15.8F, -3.8F, 0.0F, 1.0F, 5.0F, new Dilation(0.0F)), ModelTransform.of(-1.0F, 2.5F, 0.0F, 0.0F, -0.5236F, 0.0F));

        ModelPartData cube_r78 = rim2.addChild("cube_r78", ModelPartBuilder.create().uv(141, 196).cuboid(20.0F, -15.8F, 3.2F, 1.0F, 1.0F, 1.0F, new Dilation(-0.1F))
                .uv(141, 196).cuboid(20.0F, -15.8F, 1.9F, 1.0F, 1.0F, 1.0F, new Dilation(-0.1F))
                .uv(141, 196).cuboid(20.0F, -15.8F, 4.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.1F))
                .uv(91, 207).cuboid(20.5F, -16.0F, -0.5F, 0.0F, 1.0F, 6.0F, new Dilation(0.0F)), ModelTransform.of(-1.0F, 2.5F, 0.0F, 0.0F, 0.5236F, 0.0F));

        ModelPartData cube_r79 = rim2.addChild("cube_r79", ModelPartBuilder.create().uv(161, 47).cuboid(18.6F, -12.6F, -4.5F, 4.0F, 0.0F, 4.0F, new Dilation(0.001F))
                .uv(206, 103).cuboid(18.5F, -16.0F, -5.5F, 2.0F, 1.0F, 5.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 1.7F, 0.0F, 0.0F, -0.5236F, 0.0F));

        ModelPartData cube_r80 = rim2.addChild("cube_r80", ModelPartBuilder.create().uv(205, 157).cuboid(18.5F, -16.0F, -0.5F, 2.0F, 1.0F, 6.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 1.7F, 0.0F, 0.0F, 0.5236F, 0.0F));

        ModelPartData rim3 = pannel2.addChild("rim3", ModelPartBuilder.create(), ModelTransform.pivot(-11.2F, -11.4F, 0.0F));

        ModelPartData panels2 = pannel2.addChild("panels2", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData cube_r81 = panels2.addChild("cube_r81", ModelPartBuilder.create().uv(36, 190).cuboid(0.0F, -25.2F, 2.2F, 1.0F, 3.0F, 1.0F, new Dilation(0.0F))
                .uv(162, 111).cuboid(0.5F, -25.2F, -2.8F, 0.0F, 3.0F, 5.0F, new Dilation(0.001F))
                .uv(36, 190).cuboid(0.0F, -25.2F, -3.2F, 1.0F, 3.0F, 1.0F, new Dilation(0.0F))
                .uv(201, 40).cuboid(-5.2F, -22.6F, 0.25F, 10.0F, 0.0F, 2.0F, new Dilation(0.001F))
                .uv(199, 74).cuboid(-5.2F, -22.6F, -2.25F, 10.0F, 0.0F, 2.0F, new Dilation(0.001F))
                .uv(75, 142).cuboid(8.3F, -22.6F, 0.75F, 2.0F, 0.0F, 2.0F, new Dilation(0.001F))
                .uv(25, 125).cuboid(8.3F, -22.6F, -2.25F, 2.0F, 0.0F, 2.0F, new Dilation(0.001F))
                .uv(106, 125).cuboid(4.8F, -22.6F, 0.75F, 5.0F, 0.0F, 1.0F, new Dilation(0.001F))
                .uv(148, 144).cuboid(-5.2F, -22.6F, -0.25F, 16.0F, 0.0F, 1.0F, new Dilation(0.001F))
                .uv(0, 38).cuboid(-5.2F, -22.2F, -9.0F, 16.0F, 0.0F, 18.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.4363F));

        ModelPartData cube_r82 = panels2.addChild("cube_r82", ModelPartBuilder.create().uv(174, 205).cuboid(-0.5F, -0.5F, -3.0F, 1.0F, 1.0F, 6.0F, new Dilation(-0.2F)), ModelTransform.of(10.8918F, -22.1745F, -0.2F, 0.0F, 0.0F, 1.2217F));

        ModelPartData cube_r83 = panels2.addChild("cube_r83", ModelPartBuilder.create().uv(53, 207).cuboid(-1.6F, -0.2F, -2.3F, 1.0F, 2.0F, 1.0F, new Dilation(-0.2F))
                .uv(130, 98).cuboid(2.8F, -0.8F, -1.2F, 1.0F, 2.0F, 1.0F, new Dilation(-0.2F))
                .uv(166, 183).cuboid(3.9F, -0.4F, -2.0F, 1.0F, 0.0F, 1.0F, new Dilation(0.0F))
                .uv(130, 98).cuboid(3.7F, -0.8F, -2.2F, 1.0F, 2.0F, 1.0F, new Dilation(-0.2F))
                .uv(166, 183).cuboid(2.1F, -0.4F, -2.8F, 1.0F, 0.0F, 1.0F, new Dilation(0.0F))
                .uv(130, 98).cuboid(1.9F, -0.8F, -2.2F, 1.0F, 2.0F, 1.0F, new Dilation(-0.2F))
                .uv(157, 98).cuboid(-4.2F, -0.8F, -3.8F, 1.0F, 2.0F, 1.0F, new Dilation(-0.2F))
                .uv(157, 98).cuboid(-4.2F, -0.8F, -0.7F, 1.0F, 2.0F, 1.0F, new Dilation(-0.2F))
                .uv(215, 195).cuboid(-0.2F, -0.5F, -1.3F, 1.0F, 1.0F, 2.0F, new Dilation(-0.2F)), ModelTransform.of(14.85F, -18.122F, 1.75F, 0.0F, 0.0F, 0.4363F));

        ModelPartData cube_r84 = panels2.addChild("cube_r84", ModelPartBuilder.create().uv(166, 183).cuboid(0.4F, 0.0F, -0.6F, 1.0F, 0.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(18.1911F, -17.0054F, 0.45F, 0.0F, -1.5708F, 0.4363F));

        ModelPartData cube_r85 = panels2.addChild("cube_r85", ModelPartBuilder.create().uv(205, 83).cuboid(-2.5F, 0.0F, -1.9F, 5.0F, 0.0F, 5.0F, new Dilation(0.001F)), ModelTransform.of(17.1576F, -16.5975F, 3.5F, 0.0F, -0.3927F, 0.3927F));

        ModelPartData cube_r86 = panels2.addChild("cube_r86", ModelPartBuilder.create().uv(205, 77).cuboid(-2.5F, 0.0F, -3.1F, 5.0F, 0.0F, 5.0F, new Dilation(0.001F)), ModelTransform.of(17.1576F, -16.5975F, -3.5F, 0.0F, 0.3927F, 0.3927F));

        ModelPartData cube_r87 = panels2.addChild("cube_r87", ModelPartBuilder.create().uv(69, 57).cuboid(-1.0F, -21.5F, -9.0F, 13.0F, 0.0F, 18.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.3927F));

        ModelPartData under2 = pannel2.addChild("under2", ModelPartBuilder.create().uv(0, 76).cuboid(7.4572F, -14.3F, -4.4278F, 12.0F, 1.0F, 11.0F, new Dilation(0.001F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData cube_r88 = under2.addChild("cube_r88", ModelPartBuilder.create().uv(117, 76).cuboid(9.5F, -16.0F, -5.4F, 11.0F, 1.0F, 5.0F, new Dilation(0.0F)), ModelTransform.of(-1.6F, 1.7F, 0.0F, 0.0F, -0.5236F, 0.0F));

        ModelPartData cube_r89 = under2.addChild("cube_r89", ModelPartBuilder.create().uv(0, 106).cuboid(9.5F, -16.0F, 0.7F, 11.0F, 1.0F, 6.0F, new Dilation(0.0F)), ModelTransform.of(-1.6F, 1.7F, 0.0F, 0.0F, 0.5236F, 0.0F));

        ModelPartData pannel3 = console.addChild("pannel3", ModelPartBuilder.create().uv(0, 184).cuboid(5.5F, -25.3F, -3.5F, 2.0F, 6.0F, 7.0F, new Dilation(0.0F))
                .uv(186, 16).cuboid(7.3F, -24.5F, -4.5F, 2.0F, 0.0F, 9.0F, new Dilation(0.001F))
                .uv(53, 211).cuboid(6.3189F, -24.7F, -2.0F, 2.0F, 1.0F, 4.0F, new Dilation(0.01F))
                .uv(122, 185).cuboid(5.1F, -25.3F, -3.5F, 2.0F, 6.0F, 7.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 2.0F, 0.0F, 0.0F, 3.1416F, 0.0F));

        ModelPartData cube_r90 = pannel3.addChild("cube_r90", ModelPartBuilder.create().uv(215, 132).cuboid(5.375F, -5.975F, 13.15F, 1.0F, 1.0F, 2.0F, new Dilation(-0.23F))
                .uv(183, 213).cuboid(4.825F, -6.425F, 13.35F, 2.0F, 2.0F, 0.0F, new Dilation(0.0F))
                .uv(215, 132).cuboid(5.625F, -5.225F, 12.05F, 1.0F, 1.0F, 2.0F, new Dilation(-0.65F)), ModelTransform.of(20.4881F, -17.0097F, 5.6734F, 0.0F, -2.618F, 0.0F));

        ModelPartData cube_r91 = pannel3.addChild("cube_r91", ModelPartBuilder.create().uv(189, 211).cuboid(-1.0F, -0.5F, -2.5F, 2.0F, 1.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(8.0F, -24.2F, 4.0F, 0.0F, 0.2618F, 0.0F));

        ModelPartData cube_r92 = pannel3.addChild("cube_r92", ModelPartBuilder.create().uv(147, 178).cuboid(-1.0F, -0.5F, -0.5F, 2.0F, 1.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(8.0F, -24.2F, -4.0F, 0.0F, -0.2618F, 0.0F));

        ModelPartData pillars11 = pannel3.addChild("pillars11", ModelPartBuilder.create(), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -2.0944F, 0.0F));

        ModelPartData pillars12 = pillars11.addChild("pillars12", ModelPartBuilder.create().uv(130, 102).cuboid(-0.999F, -24.7252F, -11.2925F, 2.0F, 1.0F, 3.0F, new Dilation(0.1F))
                .uv(207, 207).cuboid(-0.449F, -25.9252F, -19.9925F, 1.0F, 1.0F, 7.0F, new Dilation(0.0F))
                .uv(207, 207).mirrored().cuboid(-0.449F, -26.6752F, 12.7425F, 1.0F, 1.0F, 7.0F, new Dilation(0.0F)).mirrored(false)
                .uv(60, 110).cuboid(-1.0F, -22.0637F, -9.2598F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F))
                .uv(95, 183).cuboid(-1.0F, -20.8637F, -9.2598F, 2.0F, 21.0F, 2.0F, new Dilation(0.0F))
                .uv(17, 204).cuboid(-1.0F, -1.8637F, -15.2598F, 2.0F, 2.0F, 6.0F, new Dilation(-0.01F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData cube_r93 = pillars12.addChild("cube_r93", ModelPartBuilder.create().uv(66, 211).cuboid(-1.0F, -2.6472F, -2.6383F, 2.0F, 5.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.4737F, -13.1113F, -1.4399F, 0.0F, 0.0F));

        ModelPartData cube_r94 = pillars12.addChild("cube_r94", ModelPartBuilder.create().uv(128, 213).cuboid(0.0F, -1.7049F, -0.124F, 0.0F, 2.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(0.001F, -24.7203F, 11.1165F, 0.2182F, 0.0F, 0.0F));

        ModelPartData cube_r95 = pillars12.addChild("cube_r95", ModelPartBuilder.create().uv(119, 213).cuboid(0.0F, -1.7049F, -3.376F, 0.0F, 2.0F, 4.0F, new Dilation(0.001F)), ModelTransform.of(0.001F, -24.7203F, -11.6165F, 0.2618F, 0.0F, 0.0F));

        ModelPartData cube_r96 = pillars12.addChild("cube_r96", ModelPartBuilder.create().uv(209, 201).cuboid(-1.0F, -0.5351F, -0.0031F, 2.0F, 2.0F, 4.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, -24.495F, -8.7766F, -1.1345F, 0.0F, 0.0F));

        ModelPartData cube_r97 = pillars12.addChild("cube_r97", ModelPartBuilder.create().uv(84, 215).cuboid(21.0F, -3.5261F, -42.1375F, 2.0F, 3.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(-21.999F, 8.4485F, 12.8146F, -0.5672F, 0.0F, 0.0F));

        ModelPartData pillars13 = pillars12.addChild("pillars13", ModelPartBuilder.create(), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData pillars14 = pillars13.addChild("pillars14", ModelPartBuilder.create(), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData pillars15 = pillars14.addChild("pillars15", ModelPartBuilder.create(), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData pillars16 = pillars11.addChild("pillars16", ModelPartBuilder.create().uv(35, 89).cuboid(-0.999F, -24.7252F, -10.2925F, 2.0F, 1.0F, 2.0F, new Dilation(0.0F))
                .uv(60, 110).cuboid(-1.0F, -22.0637F, -9.2598F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F))
                .uv(95, 183).cuboid(-1.0F, -20.8637F, -9.2598F, 2.0F, 21.0F, 2.0F, new Dilation(0.0F))
                .uv(17, 204).cuboid(-1.0F, -1.8637F, -15.2598F, 2.0F, 2.0F, 6.0F, new Dilation(-0.01F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData cube_r98 = pillars16.addChild("cube_r98", ModelPartBuilder.create().uv(66, 211).cuboid(-1.0F, -2.6472F, -2.6383F, 2.0F, 5.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.4737F, -13.1113F, -1.4399F, 0.0F, 0.0F));

        ModelPartData cube_r99 = pillars16.addChild("cube_r99", ModelPartBuilder.create().uv(174, 213).cuboid(-1.0F, -2.6472F, -2.6383F, 2.0F, 3.0F, 2.0F, new Dilation(0.01F)), ModelTransform.of(0.0F, -0.1842F, -9.915F, -0.9599F, 0.0F, 0.0F));

        ModelPartData cube_r100 = pillars16.addChild("cube_r100", ModelPartBuilder.create().uv(209, 201).cuboid(-1.0F, -0.5351F, -0.0031F, 2.0F, 2.0F, 4.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, -24.495F, -8.7766F, -1.1345F, 0.0F, 0.0F));

        ModelPartData cube_r101 = pillars16.addChild("cube_r101", ModelPartBuilder.create().uv(69, 150).cuboid(-1.2F, -4.6049F, -6.876F, 2.0F, 10.0F, 0.0F, new Dilation(0.001F))
                .uv(200, 201).cuboid(-1.0F, -8.6049F, -6.676F, 2.0F, 15.0F, 2.0F, new Dilation(0.01F)), ModelTransform.of(0.0F, -12.7212F, -12.7214F, -1.2654F, 0.0F, 0.0F));

        ModelPartData pillars17 = pillars16.addChild("pillars17", ModelPartBuilder.create(), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData pillars18 = pillars17.addChild("pillars18", ModelPartBuilder.create(), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData pillars19 = pillars18.addChild("pillars19", ModelPartBuilder.create(), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData rim4 = pannel3.addChild("rim4", ModelPartBuilder.create().uv(25, 128).cuboid(18.5F, -14.3F, -5.5F, 2.0F, 1.0F, 11.0F, new Dilation(0.001F))
                .uv(173, 111).cuboid(19.5F, -13.5F, -5.5F, 0.0F, 1.0F, 11.0F, new Dilation(0.0F))
                .uv(141, 196).cuboid(19.0F, -13.3F, 1.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.1F))
                .uv(141, 196).cuboid(19.0F, -13.3F, -2.9F, 1.0F, 1.0F, 1.0F, new Dilation(-0.1F)), ModelTransform.pivot(0.0F, -1.0F, 0.0F));

        ModelPartData cube_r102 = rim4.addChild("cube_r102", ModelPartBuilder.create().uv(141, 196).cuboid(20.0F, -15.8F, -5.7F, 1.0F, 1.0F, 1.0F, new Dilation(-0.1F))
                .uv(141, 196).cuboid(20.0F, -15.8F, -4.4F, 1.0F, 1.0F, 1.0F, new Dilation(-0.1F))
                .uv(141, 196).cuboid(20.0F, -15.8F, -3.1F, 1.0F, 1.0F, 1.0F, new Dilation(-0.1F))
                .uv(171, 146).cuboid(20.5F, -16.0F, -5.5F, 0.0F, 1.0F, 5.0F, new Dilation(0.0F)), ModelTransform.of(-1.0F, 2.5F, 0.0F, 0.0F, -0.5236F, 0.0F));

        ModelPartData cube_r103 = rim4.addChild("cube_r103", ModelPartBuilder.create().uv(141, 196).cuboid(20.0F, -15.8F, 3.2F, 1.0F, 1.0F, 1.0F, new Dilation(-0.1F))
                .uv(141, 196).cuboid(20.0F, -15.8F, 1.9F, 1.0F, 1.0F, 1.0F, new Dilation(-0.1F))
                .uv(141, 196).cuboid(20.0F, -15.8F, 4.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.1F))
                .uv(91, 207).cuboid(20.5F, -16.0F, -0.5F, 0.0F, 1.0F, 6.0F, new Dilation(0.0F)), ModelTransform.of(-1.0F, 2.5F, 0.0F, 0.0F, 0.5236F, 0.0F));

        ModelPartData cube_r104 = rim4.addChild("cube_r104", ModelPartBuilder.create().uv(206, 103).cuboid(18.5F, -16.0F, -5.5F, 2.0F, 1.0F, 5.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 1.7F, 0.0F, 0.0F, -0.5236F, 0.0F));

        ModelPartData cube_r105 = rim4.addChild("cube_r105", ModelPartBuilder.create().uv(205, 157).cuboid(18.5F, -16.0F, -0.5F, 2.0F, 1.0F, 6.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 1.7F, 0.0F, 0.0F, 0.5236F, 0.0F));

        ModelPartData rim5 = pannel3.addChild("rim5", ModelPartBuilder.create(), ModelTransform.pivot(-11.2F, -11.4F, 0.0F));

        ModelPartData panels3 = pannel3.addChild("panels3", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData cube_r106 = panels3.addChild("cube_r106", ModelPartBuilder.create().uv(216, 46).cuboid(-0.3F, 0.16F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.2F)), ModelTransform.of(11.6341F, -19.5482F, -1.0F, 0.0F, -0.7854F, 0.4363F));

        ModelPartData cube_r107 = panels3.addChild("cube_r107", ModelPartBuilder.create().uv(216, 61).cuboid(5.55F, -21.9F, -1.75F, 1.0F, 0.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-3.5F, -2.0F, 0.5F, 0.0F, 0.0F, 0.4363F));

        ModelPartData cube_r108 = panels3.addChild("cube_r108", ModelPartBuilder.create().uv(216, 52).cuboid(-0.5F, -0.4F, 1.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.2F))
                .uv(216, 52).cuboid(-0.5F, -0.4F, 0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.2F))
                .uv(216, 49).cuboid(-0.5F, -0.1F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.2F)), ModelTransform.of(11.3806F, -19.0044F, -1.0F, 3.1416F, 0.0F, 0.4363F));

        ModelPartData cube_r109 = panels3.addChild("cube_r109", ModelPartBuilder.create().uv(216, 46).cuboid(-1.8056F, 0.2086F, 0.0104F, 1.0F, 1.0F, 1.0F, new Dilation(-0.1F))
                .uv(216, 46).cuboid(-1.7011F, 0.1572F, -1.3795F, 1.0F, 1.0F, 1.0F, new Dilation(-0.05F)), ModelTransform.of(9.4904F, -17.3898F, -14.8793F, 0.3829F, 0.2028F, 0.31F));

        ModelPartData cube_r110 = panels3.addChild("cube_r110", ModelPartBuilder.create().uv(188, 216).cuboid(-0.8688F, 0.2972F, -2.1126F, 1.0F, 0.0F, 1.0F, new Dilation(-0.05F))
                .uv(188, 216).cuboid(-1.9255F, 0.3486F, -1.2037F, 1.0F, 0.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(9.4904F, -17.3898F, -14.8793F, 0.6319F, 0.9025F, 0.7505F));

        ModelPartData cube_r111 = panels3.addChild("cube_r111", ModelPartBuilder.create().uv(216, 49).cuboid(-1.9255F, -0.7486F, 0.2037F, 1.0F, 1.0F, 1.0F, new Dilation(0.05F))
                .uv(216, 49).cuboid(-0.8688F, -0.6972F, 1.1126F, 1.0F, 1.0F, 1.0F, new Dilation(-0.05F)), ModelTransform.of(9.4904F, -17.3898F, -14.8793F, -2.5097F, 0.9025F, 0.7505F));

        ModelPartData cube_r112 = panels3.addChild("cube_r112", ModelPartBuilder.create().uv(216, 46).cuboid(-0.3F, 0.16F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.1F)), ModelTransform.of(14.0341F, -18.8482F, -0.1F, 0.0F, -0.7854F, 0.4363F));

        ModelPartData cube_r113 = panels3.addChild("cube_r113", ModelPartBuilder.create().uv(216, 49).cuboid(-0.5F, -0.1F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.05F)), ModelTransform.of(13.7806F, -18.3044F, -0.1F, 3.1416F, 0.0F, 0.4363F));

        ModelPartData cube_r114 = panels3.addChild("cube_r114", ModelPartBuilder.create().uv(188, 216).cuboid(5.8F, -22.0F, -2.0F, 1.0F, 0.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-1.1F, -1.3F, 1.4F, 0.0F, 0.0F, 0.4363F));

        ModelPartData cube_r115 = panels3.addChild("cube_r115", ModelPartBuilder.create().uv(188, 216).cuboid(5.8F, -22.0F, -2.0F, 1.0F, 0.0F, 1.0F, new Dilation(-0.05F)), ModelTransform.of(0.0F, -1.0F, 2.5F, 0.0F, 0.0F, 0.4363F));

        ModelPartData cube_r116 = panels3.addChild("cube_r116", ModelPartBuilder.create().uv(216, 49).cuboid(-0.5F, -0.1F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.05F)), ModelTransform.of(14.8806F, -18.0044F, 1.0F, 3.1416F, 0.0F, 0.4363F));

        ModelPartData cube_r117 = panels3.addChild("cube_r117", ModelPartBuilder.create().uv(216, 46).cuboid(-0.3F, 0.16F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.05F)), ModelTransform.of(15.1341F, -18.5482F, 1.0F, 0.0F, -0.7854F, 0.4363F));

        ModelPartData cube_r118 = panels3.addChild("cube_r118", ModelPartBuilder.create().uv(36, 190).cuboid(1.5F, -23.7F, -0.6F, 1.0F, 3.0F, 1.0F, new Dilation(-0.3F))
                .uv(36, 190).cuboid(5.2F, -24.4F, -0.5F, 1.0F, 3.0F, 1.0F, new Dilation(-0.1F))
                .uv(96, 145).cuboid(3.2F, -24.2F, -1.3F, 3.0F, 0.0F, 1.0F, new Dilation(0.001F))
                .uv(216, 36).cuboid(5.2F, -24.0F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(44, 89).cuboid(9.8F, -22.8F, -0.5F, 0.0F, 1.0F, 1.0F, new Dilation(0.001F))
                .uv(138, 55).cuboid(5.8F, -22.3F, -0.25F, 5.0F, 0.0F, 1.0F, new Dilation(0.001F))
                .uv(0, 57).cuboid(-5.2F, -22.2F, -9.0F, 16.0F, 0.0F, 18.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.4363F));

        ModelPartData cube_r119 = panels3.addChild("cube_r119", ModelPartBuilder.create().uv(178, 77).cuboid(-2.5F, 0.1F, -4.0F, 5.0F, 0.0F, 8.0F, new Dilation(0.001F)), ModelTransform.of(13.0914F, -19.2731F, -0.1188F, -0.0873F, 0.1396F, 0.4363F));

        ModelPartData cube_r120 = panels3.addChild("cube_r120", ModelPartBuilder.create().uv(178, 92).cuboid(-4.0F, 0.0F, -2.5F, 8.0F, 0.0F, 5.0F, new Dilation(0.001F)), ModelTransform.of(15.6708F, -17.1216F, -4.4F, 0.0F, 0.7854F, 0.3927F));

        ModelPartData cube_r121 = panels3.addChild("cube_r121", ModelPartBuilder.create().uv(166, 185).cuboid(-4.1508F, 0.0F, -2.5F, 8.0F, 0.0F, 5.0F, new Dilation(0.001F)), ModelTransform.of(15.8101F, -17.0639F, 4.4F, 0.0F, -0.7854F, 0.3927F));

        ModelPartData cube_r122 = panels3.addChild("cube_r122", ModelPartBuilder.create().uv(141, 185).cuboid(-0.8536F, -0.9F, -0.5F, 1.0F, 4.0F, 1.0F, new Dilation(-0.1F)), ModelTransform.of(10.2617F, -21.9462F, 0.25F, 0.0F, 0.0F, 0.4363F));

        ModelPartData cube_r123 = panels3.addChild("cube_r123", ModelPartBuilder.create().uv(13, 213).cuboid(-1.75F, -1.1F, -1.75F, 3.0F, 2.0F, 3.0F, new Dilation(-0.2F)), ModelTransform.of(10.2617F, -21.9462F, 0.25F, 0.0F, 0.7854F, 0.4363F));

        ModelPartData cube_r124 = panels3.addChild("cube_r124", ModelPartBuilder.create().uv(209, 14).cuboid(-1.6464F, -1.1F, -2.0F, 4.0F, 0.0F, 4.0F, new Dilation(0.001F)), ModelTransform.of(10.2617F, -21.9462F, 0.25F, 0.0F, 0.0F, 0.4363F));

        ModelPartData cube_r125 = panels3.addChild("cube_r125", ModelPartBuilder.create().uv(0, 0).cuboid(-4.0F, -21.5F, -9.0F, 16.0F, 0.0F, 18.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.3927F));

        ModelPartData button = panels3.addChild("button", ModelPartBuilder.create(), ModelTransform.pivot(9.4904F, -17.3898F, -14.8793F));

        ModelPartData cube_r126 = button.addChild("cube_r126", ModelPartBuilder.create().uv(36, 190).cuboid(-0.9883F, -1.4441F, -1.2403F, 1.0F, 3.0F, 1.0F, new Dilation(-0.1F))
                .uv(216, 36).cuboid(-0.9883F, -1.0441F, -1.2403F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.6319F, 0.9025F, 0.7505F));

        ModelPartData under3 = pannel3.addChild("under3", ModelPartBuilder.create().uv(138, 154).cuboid(18.8F, -14.6F, -5.5F, 0.0F, 3.0F, 11.0F, new Dilation(0.001F))
                .uv(138, 154).mirrored().cuboid(-18.8F, -14.6F, -5.5F, 0.0F, 3.0F, 11.0F, new Dilation(0.001F)).mirrored(false)
                .uv(0, 76).cuboid(6.9F, -14.3F, -5.5F, 12.0F, 1.0F, 11.0F, new Dilation(0.001F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData cube_r127 = under3.addChild("cube_r127", ModelPartBuilder.create().uv(117, 76).cuboid(9.5F, -16.0F, -5.5F, 11.0F, 1.0F, 5.0F, new Dilation(0.0F)), ModelTransform.of(-1.6F, 1.7F, 0.0F, 0.0F, -0.5236F, 0.0F));

        ModelPartData cube_r128 = under3.addChild("cube_r128", ModelPartBuilder.create().uv(138, 147).cuboid(9.5F, -16.0F, 0.5F, 11.0F, 1.0F, 5.0F, new Dilation(0.0F))
                .uv(186, 153).cuboid(20.4F, -16.3F, -3.5F, 0.0F, 3.0F, 9.0F, new Dilation(0.001F)), ModelTransform.of(-1.6F, 1.7F, 0.0F, 0.0F, 0.5236F, 0.0F));

        ModelPartData cube_r129 = under3.addChild("cube_r129", ModelPartBuilder.create().uv(138, 154).mirrored().cuboid(-20.5F, -16.0F, -5.5F, 0.0F, 3.0F, 11.0F, new Dilation(0.001F)).mirrored(false), ModelTransform.of(-0.7986F, 1.699F, -1.3853F, 0.0F, 2.0944F, 0.0F));

        ModelPartData cube_r130 = under3.addChild("cube_r130", ModelPartBuilder.create().uv(186, 153).mirrored().cuboid(-20.4F, -16.3F, -5.5F, 0.0F, 3.0F, 9.0F, new Dilation(0.001F)).mirrored(false), ModelTransform.of(1.6F, 1.7F, 0.0F, 0.0F, 0.5236F, 0.0F));

        ModelPartData cube_r131 = under3.addChild("cube_r131", ModelPartBuilder.create().uv(186, 153).mirrored().cuboid(-20.4F, -16.3F, -3.5F, 0.0F, 3.0F, 9.0F, new Dilation(0.001F)).mirrored(false), ModelTransform.of(1.6F, 1.7F, 0.0F, 0.0F, -0.5236F, 0.0F));

        ModelPartData cube_r132 = under3.addChild("cube_r132", ModelPartBuilder.create().uv(138, 154).cuboid(20.4F, -16.3F, -5.5F, 0.0F, 3.0F, 11.0F, new Dilation(0.001F)), ModelTransform.of(0.7986F, 1.699F, -1.3853F, 0.0F, -2.0944F, 0.0F));

        ModelPartData cube_r133 = under3.addChild("cube_r133", ModelPartBuilder.create().uv(187, 98).cuboid(20.4F, -16.3F, -5.5F, 0.0F, 3.0F, 9.0F, new Dilation(0.001F)), ModelTransform.of(0.7986F, 1.699F, 1.3853F, 0.0F, 1.5708F, 0.0F));

        ModelPartData cube_r134 = under3.addChild("cube_r134", ModelPartBuilder.create().uv(187, 98).cuboid(20.4F, -16.3F, -3.5F, 0.0F, 3.0F, 9.0F, new Dilation(0.001F)), ModelTransform.of(0.7986F, 1.699F, -1.3853F, 0.0F, -1.5708F, 0.0F));

        ModelPartData cube_r135 = under3.addChild("cube_r135", ModelPartBuilder.create().uv(186, 153).cuboid(20.4F, -16.3F, -5.5F, 0.0F, 3.0F, 9.0F, new Dilation(0.001F)), ModelTransform.of(-1.6F, 1.7F, 0.0F, 0.0F, -0.5236F, 0.0F));

        ModelPartData cube_r136 = under3.addChild("cube_r136", ModelPartBuilder.create().uv(138, 154).cuboid(20.4F, -16.3F, -5.5F, 0.0F, 3.0F, 11.0F, new Dilation(0.001F)), ModelTransform.of(0.7986F, 1.699F, 1.3853F, 0.0F, 2.0944F, 0.0F));

        ModelPartData cube_r137 = under3.addChild("cube_r137", ModelPartBuilder.create().uv(138, 154).cuboid(20.4F, -16.3F, -5.5F, 0.0F, 3.0F, 11.0F, new Dilation(0.001F)), ModelTransform.of(-0.7986F, 1.699F, 1.3853F, 0.0F, 1.0472F, 0.0F));

        ModelPartData rolly = pannel3.addChild("rolly", ModelPartBuilder.create(), ModelTransform.pivot(15.7721F, -17.818F, -7.2115F));

        ModelPartData cube_r138 = rolly.addChild("cube_r138", ModelPartBuilder.create().uv(183, 213).cuboid(-3.475F, -2.025F, 13.35F, 2.0F, 2.0F, 0.0F, new Dilation(0.0F))
                .uv(215, 132).cuboid(-2.925F, -1.575F, 13.15F, 1.0F, 1.0F, 2.0F, new Dilation(-0.23F))
                .uv(215, 132).cuboid(-2.675F, -0.825F, 12.05F, 1.0F, 1.0F, 2.0F, new Dilation(-0.65F)), ModelTransform.of(4.716F, 0.8083F, 12.8849F, 0.0F, -2.618F, 0.0F));

        ModelPartData pannel4 = console.addChild("pannel4", ModelPartBuilder.create().uv(0, 184).cuboid(5.5F, -25.3F, -3.5F, 2.0F, 6.0F, 7.0F, new Dilation(0.0F))
                .uv(186, 16).cuboid(7.3F, -24.5F, -4.5F, 2.0F, 0.0F, 9.0F, new Dilation(0.001F))
                .uv(53, 211).cuboid(6.3189F, -24.7F, -2.0F, 2.0F, 1.0F, 4.0F, new Dilation(0.01F))
                .uv(122, 185).cuboid(5.1F, -25.3F, -3.5F, 2.0F, 6.0F, 7.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 2.0F, 0.0F, 0.0F, -2.0944F, 0.0F));

        ModelPartData cube_r139 = pannel4.addChild("cube_r139", ModelPartBuilder.create().uv(215, 110).cuboid(-1.8F, -0.75F, -1.125F, 2.0F, 2.0F, 2.0F, new Dilation(-0.2F))
                .uv(21, 132).cuboid(-0.8F, -1.45F, -0.375F, 0.0F, 4.0F, 1.0F, new Dilation(0.001F)), ModelTransform.of(9.9771F, -26.1061F, 0.125F, 0.0F, 0.0F, 0.6109F));

        ModelPartData cube_r140 = pannel4.addChild("cube_r140", ModelPartBuilder.create().uv(189, 211).cuboid(-1.0F, -0.5F, -2.5F, 2.0F, 1.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(8.0F, -24.2F, 4.0F, 0.0F, 0.2618F, 0.0F));

        ModelPartData cube_r141 = pannel4.addChild("cube_r141", ModelPartBuilder.create().uv(147, 178).cuboid(-1.0F, -0.5F, -0.5F, 2.0F, 1.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(8.0F, -24.2F, -4.0F, 0.0F, -0.2618F, 0.0F));

        ModelPartData pillars20 = pannel4.addChild("pillars20", ModelPartBuilder.create(), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -2.0944F, 0.0F));

        ModelPartData pillars21 = pillars20.addChild("pillars21", ModelPartBuilder.create().uv(130, 102).cuboid(-0.999F, -24.7252F, -11.2925F, 2.0F, 1.0F, 3.0F, new Dilation(0.1F))
                .uv(216, 30).cuboid(-1.999F, -24.8252F, -10.5925F, 1.0F, 1.0F, 1.0F, new Dilation(-0.1F))
                .uv(60, 110).cuboid(-1.0F, -22.0637F, -9.2598F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F))
                .uv(95, 183).cuboid(-1.0F, -20.8637F, -9.2598F, 2.0F, 21.0F, 2.0F, new Dilation(0.0F))
                .uv(17, 204).cuboid(-1.0F, -1.8637F, -15.2598F, 2.0F, 2.0F, 6.0F, new Dilation(-0.01F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData cube_r142 = pillars21.addChild("cube_r142", ModelPartBuilder.create().uv(209, 201).cuboid(-1.0F, -0.5351F, -0.0031F, 2.0F, 2.0F, 4.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, -24.495F, -8.7766F, -1.1345F, 0.0F, 0.0F));

        ModelPartData cube_r143 = pillars21.addChild("cube_r143", ModelPartBuilder.create().uv(200, 201).cuboid(-1.0F, -8.6049F, -6.676F, 2.0F, 15.0F, 2.0F, new Dilation(0.01F)), ModelTransform.of(0.0F, -12.7212F, -12.7214F, -1.2654F, 0.0F, 0.0F));

        ModelPartData tardis = pillars21.addChild("tardis", ModelPartBuilder.create(), ModelTransform.pivot(-1.499F, -26.2252F, -10.0925F));

        ModelPartData cube_r144 = tardis.addChild("cube_r144", ModelPartBuilder.create().uv(216, 33).cuboid(-0.5F, -1.0F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.35F))
                .uv(53, 203).cuboid(-0.5F, -0.5F, -0.5F, 1.0F, 2.0F, 1.0F, new Dilation(-0.1F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -0.7854F, 0.0F));

        ModelPartData pillars22 = pillars21.addChild("pillars22", ModelPartBuilder.create(), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData pillars23 = pillars22.addChild("pillars23", ModelPartBuilder.create(), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData pillars24 = pillars23.addChild("pillars24", ModelPartBuilder.create(), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData pillars25 = pillars20.addChild("pillars25", ModelPartBuilder.create().uv(35, 89).cuboid(-0.999F, -24.7252F, -10.2925F, 2.0F, 1.0F, 2.0F, new Dilation(0.0F))
                .uv(82, 76).cuboid(-1.0F, -14.6347F, -22.4402F, 2.0F, 1.0F, 15.0F, new Dilation(0.01F))
                .uv(60, 110).cuboid(-1.0F, -22.0637F, -9.2598F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F))
                .uv(95, 183).cuboid(-1.0F, -20.8637F, -9.2598F, 2.0F, 21.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData cube_r145 = pillars25.addChild("cube_r145", ModelPartBuilder.create().uv(174, 213).cuboid(-1.0F, -2.6472F, -2.6383F, 2.0F, 3.0F, 2.0F, new Dilation(0.01F)), ModelTransform.of(0.0F, -0.1842F, -9.915F, -0.9599F, 0.0F, 0.0F));

        ModelPartData cube_r146 = pillars25.addChild("cube_r146", ModelPartBuilder.create().uv(209, 201).cuboid(-1.0F, -0.5351F, -0.0031F, 2.0F, 2.0F, 4.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, -24.495F, -8.7766F, -1.1345F, 0.0F, 0.0F));

        ModelPartData cube_r147 = pillars25.addChild("cube_r147", ModelPartBuilder.create().uv(69, 150).cuboid(-1.2F, -4.6049F, -6.876F, 2.0F, 10.0F, 0.0F, new Dilation(0.001F))
                .uv(200, 201).cuboid(-1.0F, -8.6049F, -6.676F, 2.0F, 15.0F, 2.0F, new Dilation(0.01F)), ModelTransform.of(0.0F, -12.7212F, -12.7214F, -1.2654F, 0.0F, 0.0F));

        ModelPartData pillars26 = pillars25.addChild("pillars26", ModelPartBuilder.create(), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData pillars27 = pillars26.addChild("pillars27", ModelPartBuilder.create(), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData pillars28 = pillars27.addChild("pillars28", ModelPartBuilder.create(), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData rim6 = pannel4.addChild("rim6", ModelPartBuilder.create().uv(25, 128).cuboid(18.5F, -14.3F, -5.5F, 2.0F, 1.0F, 11.0F, new Dilation(0.001F))
                .uv(173, 111).cuboid(19.5F, -13.5F, -5.5F, 0.0F, 1.0F, 11.0F, new Dilation(0.0F))
                .uv(141, 196).cuboid(19.0F, -13.3F, 1.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.1F))
                .uv(141, 196).cuboid(19.0F, -13.3F, -2.9F, 1.0F, 1.0F, 1.0F, new Dilation(-0.1F)), ModelTransform.pivot(0.0F, -1.0F, 0.0F));

        ModelPartData cube_r148 = rim6.addChild("cube_r148", ModelPartBuilder.create().uv(141, 196).cuboid(20.0F, -15.8F, -5.7F, 1.0F, 1.0F, 1.0F, new Dilation(-0.1F))
                .uv(141, 196).cuboid(20.0F, -15.8F, -4.4F, 1.0F, 1.0F, 1.0F, new Dilation(-0.1F))
                .uv(141, 196).cuboid(20.0F, -15.8F, -3.1F, 1.0F, 1.0F, 1.0F, new Dilation(-0.1F))
                .uv(171, 146).cuboid(20.5F, -16.0F, -5.5F, 0.0F, 1.0F, 5.0F, new Dilation(0.0F)), ModelTransform.of(-1.0F, 2.5F, 0.0F, 0.0F, -0.5236F, 0.0F));

        ModelPartData cube_r149 = rim6.addChild("cube_r149", ModelPartBuilder.create().uv(141, 196).cuboid(20.0F, -15.8F, 3.2F, 1.0F, 1.0F, 1.0F, new Dilation(-0.1F))
                .uv(141, 196).cuboid(20.0F, -15.8F, 1.9F, 1.0F, 1.0F, 1.0F, new Dilation(-0.1F))
                .uv(141, 196).cuboid(20.0F, -15.8F, 4.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.1F))
                .uv(91, 207).cuboid(20.5F, -16.0F, -0.5F, 0.0F, 1.0F, 6.0F, new Dilation(0.0F)), ModelTransform.of(-1.0F, 2.5F, 0.0F, 0.0F, 0.5236F, 0.0F));

        ModelPartData cube_r150 = rim6.addChild("cube_r150", ModelPartBuilder.create().uv(206, 103).cuboid(18.5F, -16.0F, -5.5F, 2.0F, 1.0F, 5.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 1.7F, 0.0F, 0.0F, -0.5236F, 0.0F));

        ModelPartData cube_r151 = rim6.addChild("cube_r151", ModelPartBuilder.create().uv(205, 157).cuboid(18.5F, -16.0F, -0.5F, 2.0F, 1.0F, 6.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 1.7F, 0.0F, 0.0F, 0.5236F, 0.0F));

        ModelPartData rim7 = pannel4.addChild("rim7", ModelPartBuilder.create(), ModelTransform.pivot(-11.2F, -11.4F, 0.0F));

        ModelPartData panels4 = pannel4.addChild("panels4", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData cube_r152 = panels4.addChild("cube_r152", ModelPartBuilder.create().uv(36, 190).cuboid(-0.3085F, -1.0893F, -3.2F, 1.0F, 3.0F, 1.0F, new Dilation(0.0F))
                .uv(162, 111).cuboid(0.1915F, -1.0893F, -2.8F, 0.0F, 3.0F, 5.0F, new Dilation(0.001F))
                .uv(36, 190).cuboid(-0.3085F, -1.0893F, 2.2F, 1.0F, 3.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(10.3692F, -21.5213F, 0.0F, -3.1416F, 0.0F, -2.7053F));

        ModelPartData cube_r153 = panels4.addChild("cube_r153", ModelPartBuilder.create().uv(174, 205).cuboid(-0.7813F, -1.0521F, -3.2F, 1.0F, 1.0F, 6.0F, new Dilation(-0.2F)), ModelTransform.of(10.3692F, -21.5213F, 0.0F, -3.1416F, 0.0F, 2.7925F));

        ModelPartData cube_r154 = panels4.addChild("cube_r154", ModelPartBuilder.create().uv(199, 180).cuboid(-2.5F, 0.0F, -3.0F, 5.0F, 0.0F, 6.0F, new Dilation(0.001F)), ModelTransform.of(18.7594F, -15.8577F, 0.0F, 0.0524F, 0.0F, 0.4363F));

        ModelPartData cube_r155 = panels4.addChild("cube_r155", ModelPartBuilder.create().uv(205, 89).cuboid(-2.0F, 0.0F, -2.5F, 4.0F, 0.0F, 5.0F, new Dilation(0.001F)), ModelTransform.of(12.0043F, -19.118F, 1.0F, 0.1047F, 0.0F, 0.4363F));

        ModelPartData cube_r156 = panels4.addChild("cube_r156", ModelPartBuilder.create().uv(39, 213).cuboid(0.8F, -23.2F, -1.5F, 2.0F, 1.0F, 3.0F, new Dilation(0.0F))
                .uv(215, 136).cuboid(3.8F, -22.7F, -0.5F, 2.0F, 1.0F, 1.0F, new Dilation(-0.1F))
                .uv(69, 0).cuboid(-5.2F, -22.2F, -9.0F, 16.0F, 0.0F, 18.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.4363F));

        ModelPartData cube_r157 = panels4.addChild("cube_r157", ModelPartBuilder.create().uv(215, 136).cuboid(-1.0F, -0.5F, -0.5F, 2.0F, 1.0F, 1.0F, new Dilation(-0.4F)), ModelTransform.of(13.9324F, -18.0915F, 1.0F, -3.1416F, 0.0F, -2.7053F));

        ModelPartData cube_r158 = panels4.addChild("cube_r158", ModelPartBuilder.create().uv(215, 136).cuboid(-2.3F, -0.5F, -0.5F, 2.0F, 1.0F, 1.0F, new Dilation(-0.4F)), ModelTransform.of(13.9324F, -18.0915F, -0.3F, 0.0F, 1.5708F, 0.4363F));

        ModelPartData cube_r159 = panels4.addChild("cube_r159", ModelPartBuilder.create().uv(204, 145).cuboid(-2.5F, 0.0F, -2.5F, 5.0F, 0.0F, 5.0F, new Dilation(0.001F)), ModelTransform.of(16.1572F, -16.7954F, -4.5F, 0.0F, 0.3927F, 0.3927F));

        ModelPartData cube_r160 = panels4.addChild("cube_r160", ModelPartBuilder.create().uv(204, 139).cuboid(-2.5F, 0.0F, -2.5F, 5.0F, 0.0F, 5.0F, new Dilation(0.001F)), ModelTransform.of(16.1572F, -16.7954F, 4.5F, 0.0F, -0.3927F, 0.3927F));

        ModelPartData cube_r161 = panels4.addChild("cube_r161", ModelPartBuilder.create().uv(0, 0).cuboid(-4.0F, -21.5F, -9.0F, 16.0F, 0.0F, 18.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.3927F));

        ModelPartData under4 = pannel4.addChild("under4", ModelPartBuilder.create().uv(0, 76).cuboid(6.9F, -14.3F, -5.5F, 12.0F, 1.0F, 11.0F, new Dilation(0.001F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData cube_r162 = under4.addChild("cube_r162", ModelPartBuilder.create().uv(117, 76).cuboid(9.5F, -16.0F, -5.5F, 11.0F, 1.0F, 5.0F, new Dilation(0.0F)), ModelTransform.of(-1.6F, 1.7F, 0.0F, 0.0F, -0.5236F, 0.0F));

        ModelPartData cube_r163 = under4.addChild("cube_r163", ModelPartBuilder.create().uv(138, 147).cuboid(9.5F, -16.0F, 0.5F, 11.0F, 1.0F, 5.0F, new Dilation(0.0F)), ModelTransform.of(-1.6F, 1.7F, 0.0F, 0.0F, 0.5236F, 0.0F));

        ModelPartData pannel5 = console.addChild("pannel5", ModelPartBuilder.create().uv(0, 184).cuboid(5.5F, -25.3F, -3.5F, 2.0F, 6.0F, 7.0F, new Dilation(0.0F))
                .uv(186, 16).cuboid(7.3F, -24.5F, -4.5F, 2.0F, 0.0F, 9.0F, new Dilation(0.001F))
                .uv(53, 211).cuboid(6.3189F, -24.7F, -2.0F, 2.0F, 1.0F, 4.0F, new Dilation(0.01F))
                .uv(122, 185).cuboid(5.1F, -25.3F, -3.5F, 2.0F, 6.0F, 7.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 2.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData cube_r164 = pannel5.addChild("cube_r164", ModelPartBuilder.create().uv(215, 123).cuboid(-1.0F, -0.5F, -1.0F, 2.0F, 1.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(8.5189F, -24.4F, 0.0F, 0.0F, -0.7854F, 0.0F));

        ModelPartData cube_r165 = pannel5.addChild("cube_r165", ModelPartBuilder.create().uv(189, 211).cuboid(-1.0F, -0.5F, -2.5F, 2.0F, 1.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(8.0F, -24.2F, 4.0F, 0.0F, 0.2618F, 0.0F));

        ModelPartData cube_r166 = pannel5.addChild("cube_r166", ModelPartBuilder.create().uv(147, 178).cuboid(-1.0F, -0.5F, -0.5F, 2.0F, 1.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(8.0F, -24.2F, -4.0F, 0.0F, -0.2618F, 0.0F));

        ModelPartData pillars29 = pannel5.addChild("pillars29", ModelPartBuilder.create(), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -2.0944F, 0.0F));

        ModelPartData pillars30 = pillars29.addChild("pillars30", ModelPartBuilder.create().uv(130, 102).cuboid(-0.999F, -24.7252F, -11.2925F, 2.0F, 1.0F, 3.0F, new Dilation(0.1F))
                .uv(0, 89).cuboid(-1.0F, -14.6347F, -22.4402F, 2.0F, 1.0F, 15.0F, new Dilation(0.01F))
                .uv(60, 110).cuboid(-1.0F, -22.0637F, -9.2598F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F))
                .uv(95, 183).cuboid(-1.0F, -20.8637F, -9.2598F, 2.0F, 21.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData cube_r167 = pillars30.addChild("cube_r167", ModelPartBuilder.create().uv(209, 201).cuboid(-1.0F, -0.5351F, -0.0031F, 2.0F, 2.0F, 4.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, -24.495F, -8.7766F, -1.1345F, 0.0F, 0.0F));

        ModelPartData pillars31 = pillars30.addChild("pillars31", ModelPartBuilder.create(), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData pillars32 = pillars31.addChild("pillars32", ModelPartBuilder.create(), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData pillars33 = pillars32.addChild("pillars33", ModelPartBuilder.create(), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData pillars34 = pillars29.addChild("pillars34", ModelPartBuilder.create().uv(44, 147).cuboid(-0.6F, -23.4886F, -9.354F, 2.0F, 1.0F, 1.0F, new Dilation(-0.3F))
                .uv(35, 89).cuboid(-0.999F, -24.7252F, -10.2925F, 2.0F, 1.0F, 2.0F, new Dilation(0.0F))
                .uv(82, 76).cuboid(-1.0F, -14.6347F, -22.4402F, 2.0F, 1.0F, 15.0F, new Dilation(0.01F))
                .uv(60, 110).cuboid(-1.0F, -22.0637F, -9.2598F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F))
                .uv(95, 183).cuboid(-1.0F, -20.8637F, -9.2598F, 2.0F, 21.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData cube_r168 = pillars34.addChild("cube_r168", ModelPartBuilder.create().uv(66, 211).cuboid(-1.0F, -2.6472F, -2.6383F, 2.0F, 5.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.4737F, -13.1113F, -1.4399F, 0.0F, 0.0F));

        ModelPartData cube_r169 = pillars34.addChild("cube_r169", ModelPartBuilder.create().uv(174, 213).cuboid(-1.0F, -2.6472F, -2.6383F, 2.0F, 3.0F, 2.0F, new Dilation(0.01F)), ModelTransform.of(0.0F, -0.1842F, -9.915F, -0.9599F, 0.0F, 0.0F));

        ModelPartData cube_r170 = pillars34.addChild("cube_r170", ModelPartBuilder.create().uv(209, 201).cuboid(-1.0F, -0.5351F, -0.0031F, 2.0F, 2.0F, 4.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, -24.495F, -8.7766F, -1.1345F, 0.0F, 0.0F));

        ModelPartData cube_r171 = pillars34.addChild("cube_r171", ModelPartBuilder.create().uv(69, 150).cuboid(-1.2F, -4.6049F, -6.876F, 2.0F, 10.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, -12.7212F, -12.7214F, -1.2654F, 0.0F, 0.0F));

        ModelPartData cube_r172 = pillars34.addChild("cube_r172", ModelPartBuilder.create().uv(84, 215).cuboid(21.0F, -3.5261F, -42.1375F, 2.0F, 3.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(-21.999F, 8.4485F, 12.8146F, -0.5672F, 0.0F, 0.0F));

        ModelPartData toggle = pillars34.addChild("toggle", ModelPartBuilder.create().uv(44, 147).cuboid(-1.2F, -0.5F, -0.5F, 2.0F, 1.0F, 1.0F, new Dilation(-0.45F)), ModelTransform.of(1.0F, -22.9886F, -8.854F, 0.0F, 0.0F, -0.3927F));

        ModelPartData pillars35 = pillars34.addChild("pillars35", ModelPartBuilder.create(), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData pillars36 = pillars35.addChild("pillars36", ModelPartBuilder.create(), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData pillars37 = pillars36.addChild("pillars37", ModelPartBuilder.create(), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData rim8 = pannel5.addChild("rim8", ModelPartBuilder.create().uv(25, 128).cuboid(18.5F, -14.3F, -5.5F, 2.0F, 1.0F, 11.0F, new Dilation(0.001F))
                .uv(173, 111).cuboid(19.5F, -13.5F, -5.5F, 0.0F, 1.0F, 11.0F, new Dilation(0.0F))
                .uv(141, 196).cuboid(19.0F, -13.3F, 1.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.1F))
                .uv(141, 196).cuboid(19.0F, -13.3F, -2.9F, 1.0F, 1.0F, 1.0F, new Dilation(-0.1F)), ModelTransform.pivot(0.0F, -1.0F, 0.0F));

        ModelPartData cube_r173 = rim8.addChild("cube_r173", ModelPartBuilder.create().uv(141, 196).cuboid(20.0F, -15.8F, -5.7F, 1.0F, 1.0F, 1.0F, new Dilation(-0.1F))
                .uv(141, 196).cuboid(20.0F, -15.8F, -4.4F, 1.0F, 1.0F, 1.0F, new Dilation(-0.1F))
                .uv(141, 196).cuboid(20.0F, -15.8F, -3.1F, 1.0F, 1.0F, 1.0F, new Dilation(-0.1F))
                .uv(171, 146).cuboid(20.5F, -16.0F, -5.5F, 0.0F, 1.0F, 5.0F, new Dilation(0.0F)), ModelTransform.of(-1.0F, 2.5F, 0.0F, 0.0F, -0.5236F, 0.0F));

        ModelPartData cube_r174 = rim8.addChild("cube_r174", ModelPartBuilder.create().uv(141, 196).cuboid(20.0F, -15.8F, 3.2F, 1.0F, 1.0F, 1.0F, new Dilation(-0.1F))
                .uv(141, 196).cuboid(20.0F, -15.8F, 1.9F, 1.0F, 1.0F, 1.0F, new Dilation(-0.1F))
                .uv(141, 196).cuboid(20.0F, -15.8F, 4.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.1F))
                .uv(91, 207).cuboid(20.5F, -16.0F, -0.5F, 0.0F, 1.0F, 6.0F, new Dilation(0.0F)), ModelTransform.of(-1.0F, 2.5F, 0.0F, 0.0F, 0.5236F, 0.0F));

        ModelPartData cube_r175 = rim8.addChild("cube_r175", ModelPartBuilder.create().uv(206, 103).cuboid(18.5F, -16.0F, -5.5F, 2.0F, 1.0F, 5.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 1.7F, 0.0F, 0.0F, -0.5236F, 0.0F));

        ModelPartData cube_r176 = rim8.addChild("cube_r176", ModelPartBuilder.create().uv(205, 157).cuboid(18.5F, -16.0F, -0.5F, 2.0F, 1.0F, 6.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 1.7F, 0.0F, 0.0F, 0.5236F, 0.0F));

        ModelPartData rim9 = pannel5.addChild("rim9", ModelPartBuilder.create(), ModelTransform.pivot(-11.2F, -11.4F, 0.0F));

        ModelPartData panels5 = pannel5.addChild("panels5", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData cube_r177 = panels5.addChild("cube_r177", ModelPartBuilder.create().uv(206, 95).cuboid(-0.5F, -0.5F, -2.6F, 1.0F, 1.0F, 6.0F, new Dilation(0.0F)), ModelTransform.of(9.8653F, -20.4464F, 0.4F, 0.0F, 0.0873F, 0.4363F));

        ModelPartData cube_r178 = panels5.addChild("cube_r178", ModelPartBuilder.create().uv(126, 166).cuboid(-1.5F, -0.5F, -0.5F, 3.0F, 1.0F, 1.0F, new Dilation(-0.001F)), ModelTransform.of(14.85F, -18.122F, 0.0F, 0.0F, 0.0F, 0.4363F));

        ModelPartData cube_r179 = panels5.addChild("cube_r179", ModelPartBuilder.create().uv(189, 205).cuboid(6.8F, -23.2F, -2.0F, 1.0F, 1.0F, 4.0F, new Dilation(0.0F))
                .uv(189, 205).cuboid(5.3F, -23.2F, -2.0F, 1.0F, 1.0F, 4.0F, new Dilation(0.0F))
                .uv(216, 9).cuboid(8.6F, -23.0F, -2.3F, 1.0F, 1.0F, 1.0F, new Dilation(-0.9F))
                .uv(216, 6).cuboid(1.8F, -23.2F, -2.3F, 1.0F, 1.0F, 1.0F, new Dilation(-0.9F))
                .uv(216, 9).cuboid(8.6F, -23.2F, 1.3F, 1.0F, 1.0F, 1.0F, new Dilation(-0.9F))
                .uv(216, 65).cuboid(-2.0F, -22.5F, -1.45F, 1.0F, 0.0F, 1.0F, new Dilation(0.0F))
                .uv(216, 65).cuboid(-0.8F, -22.5F, 3.55F, 1.0F, 0.0F, 1.0F, new Dilation(0.0F))
                .uv(205, 151).cuboid(-3.8F, -22.5F, -1.45F, 4.0F, 0.0F, 5.0F, new Dilation(0.0F))
                .uv(216, 6).cuboid(-1.8F, -22.7F, -0.45F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(216, 6).cuboid(1.8F, -23.2F, 1.3F, 1.0F, 1.0F, 1.0F, new Dilation(-0.9F))
                .uv(189, 205).cuboid(3.8F, -23.2F, -2.0F, 1.0F, 1.0F, 4.0F, new Dilation(0.0F))
                .uv(95, 93).cuboid(-5.2F, -22.7F, -2.0F, 16.0F, 0.0F, 4.0F, new Dilation(0.001F))
                .uv(69, 19).cuboid(-5.2F, -22.2F, -9.0F, 16.0F, 0.0F, 18.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.4363F));

        ModelPartData cube_r180 = panels5.addChild("cube_r180", ModelPartBuilder.create().uv(216, 169).cuboid(-0.5F, 0.0F, -2.5F, 1.0F, 0.0F, 1.0F, new Dilation(0.001F))
                .uv(216, 167).cuboid(-0.5F, 0.0F, -1.5F, 1.0F, 0.0F, 1.0F, new Dilation(0.001F)), ModelTransform.of(9.2064F, -23.2925F, -3.5109F, -0.8727F, 0.0F, 0.4363F));

        ModelPartData cube_r181 = panels5.addChild("cube_r181", ModelPartBuilder.create().uv(216, 165).cuboid(-0.5F, 0.0F, -1.5F, 1.0F, 0.0F, 1.0F, new Dilation(0.001F)), ModelTransform.of(8.8194F, -22.4625F, -3.0824F, -1.0472F, 0.0F, 0.4363F));

        ModelPartData cube_r182 = panels5.addChild("cube_r182", ModelPartBuilder.create().uv(216, 71).cuboid(-0.5F, 0.0F, -0.5F, 1.0F, 0.0F, 1.0F, new Dilation(0.001F)), ModelTransform.of(8.8039F, -22.4292F, -3.1615F, -1.2217F, 0.0F, 0.4363F));

        ModelPartData cube_r183 = panels5.addChild("cube_r183", ModelPartBuilder.create().uv(216, 69).cuboid(-0.5F, 0.0F, -0.5F, 1.0F, 0.0F, 1.0F, new Dilation(0.001F)), ModelTransform.of(8.4321F, -21.632F, -2.7035F, -0.9599F, 0.0F, 0.4363F));

        ModelPartData cube_r184 = panels5.addChild("cube_r184", ModelPartBuilder.create().uv(216, 67).cuboid(-0.5F, 0.0F, -0.5F, 1.0F, 0.0F, 1.0F, new Dilation(0.001F)), ModelTransform.of(8.2042F, -21.1432F, -1.9332F, -0.2618F, 0.0F, 0.4363F));

        ModelPartData cube_r185 = panels5.addChild("cube_r185", ModelPartBuilder.create().uv(166, 185).cuboid(-3.7F, 0.0F, -2.5F, 8.0F, 0.0F, 5.0F, new Dilation(0.001F)), ModelTransform.of(15.657F, -16.8943F, 4.0F, 0.0F, -0.7854F, 0.3927F));

        ModelPartData cube_r186 = panels5.addChild("cube_r186", ModelPartBuilder.create().uv(178, 86).cuboid(-3.7F, 0.0F, -2.5F, 8.0F, 0.0F, 5.0F, new Dilation(0.001F)), ModelTransform.of(15.657F, -16.8943F, -4.0F, 0.0F, 0.7854F, 0.3927F));

        ModelPartData cube_r187 = panels5.addChild("cube_r187", ModelPartBuilder.create().uv(0, 0).cuboid(-4.0F, -21.5F, -9.0F, 16.0F, 0.0F, 18.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.3927F));

        ModelPartData under5 = pannel5.addChild("under5", ModelPartBuilder.create().uv(0, 76).cuboid(6.9F, -14.3F, -5.5F, 12.0F, 1.0F, 11.0F, new Dilation(0.001F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData cube_r188 = under5.addChild("cube_r188", ModelPartBuilder.create().uv(117, 76).cuboid(9.5F, -16.0F, -5.5F, 11.0F, 1.0F, 5.0F, new Dilation(0.0F)), ModelTransform.of(-1.6F, 1.7F, 0.0F, 0.0F, -0.5236F, 0.0F));

        ModelPartData cube_r189 = under5.addChild("cube_r189", ModelPartBuilder.create().uv(138, 147).cuboid(9.5F, -16.0F, 0.5F, 11.0F, 1.0F, 5.0F, new Dilation(0.0F)), ModelTransform.of(-1.6F, 1.7F, 0.0F, 0.0F, 0.5236F, 0.0F));

        ModelPartData pannel6 = console.addChild("pannel6", ModelPartBuilder.create().uv(0, 184).cuboid(5.5F, -25.3F, -3.5F, 2.0F, 6.0F, 7.0F, new Dilation(0.0F))
                .uv(186, 16).cuboid(7.3F, -24.5F, -4.5F, 2.0F, 0.0F, 9.0F, new Dilation(0.001F))
                .uv(53, 211).cuboid(6.3189F, -24.7F, -2.0F, 2.0F, 1.0F, 4.0F, new Dilation(0.01F))
                .uv(122, 185).cuboid(5.1F, -25.3F, -3.5F, 2.0F, 6.0F, 7.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 2.0F, 0.0F));

        ModelPartData cube_r190 = pannel6.addChild("cube_r190", ModelPartBuilder.create().uv(204, 145).cuboid(-2.5F, 0.0F, -2.5F, 5.0F, 0.0F, 5.0F, new Dilation(0.001F)), ModelTransform.of(16.1572F, -16.7954F, -4.5F, 0.0F, 0.3927F, 0.3927F));

        ModelPartData cube_r191 = pannel6.addChild("cube_r191", ModelPartBuilder.create().uv(204, 139).cuboid(-2.5F, 0.0F, -2.5F, 5.0F, 0.0F, 5.0F, new Dilation(0.001F)), ModelTransform.of(16.1572F, -16.7954F, 4.5F, 0.0F, -0.3927F, 0.3927F));

        ModelPartData cube_r192 = pannel6.addChild("cube_r192", ModelPartBuilder.create().uv(36, 190).cuboid(-0.3085F, -1.0893F, -3.2F, 1.0F, 3.0F, 1.0F, new Dilation(0.0F))
                .uv(36, 190).cuboid(-0.3085F, -1.0893F, 2.2F, 1.0F, 3.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(10.2692F, -21.5213F, 0.0F, -3.1416F, 0.0F, -2.7053F));

        ModelPartData cube_r193 = pannel6.addChild("cube_r193", ModelPartBuilder.create().uv(174, 205).cuboid(-0.7813F, -1.0521F, -3.2F, 1.0F, 1.0F, 6.0F, new Dilation(-0.2F)), ModelTransform.of(10.2692F, -21.5213F, 0.0F, -3.1416F, 0.0F, 2.7925F));

        ModelPartData cube_r194 = pannel6.addChild("cube_r194", ModelPartBuilder.create().uv(189, 211).cuboid(-1.0F, -0.5F, -2.5F, 2.0F, 1.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(8.0F, -24.2F, 4.0F, 0.0F, 0.2618F, 0.0F));

        ModelPartData cube_r195 = pannel6.addChild("cube_r195", ModelPartBuilder.create().uv(147, 178).cuboid(-1.0F, -0.5F, -0.5F, 2.0F, 1.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(8.0F, -24.2F, -4.0F, 0.0F, -0.2618F, 0.0F));

        ModelPartData pillars38 = pannel6.addChild("pillars38", ModelPartBuilder.create(), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -2.0944F, 0.0F));

        ModelPartData pillars39 = pillars38.addChild("pillars39", ModelPartBuilder.create().uv(130, 102).cuboid(-0.999F, -24.7252F, -11.2925F, 2.0F, 1.0F, 3.0F, new Dilation(0.1F))
                .uv(216, 24).cuboid(-0.499F, -25.2252F, -11.0925F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(216, 24).mirrored().cuboid(-0.501F, -25.2252F, 10.0925F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)).mirrored(false)
                .uv(147, 169).cuboid(-2.749F, -32.7252F, -9.7925F, 6.0F, 8.0F, 0.0F, new Dilation(0.0F))
                .uv(147, 169).cuboid(-2.749F, -32.7252F, 9.7925F, 6.0F, 8.0F, 0.0F, new Dilation(0.001F))
                .uv(60, 110).cuboid(-1.0F, -22.0637F, -9.2598F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F))
                .uv(95, 183).cuboid(-1.0F, -20.8637F, -9.2598F, 2.0F, 21.0F, 2.0F, new Dilation(0.0F))
                .uv(17, 204).cuboid(-1.0F, -1.8637F, -15.2598F, 2.0F, 2.0F, 6.0F, new Dilation(-0.01F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData cube_r196 = pillars39.addChild("cube_r196", ModelPartBuilder.create().uv(66, 211).cuboid(-1.0F, -2.6472F, -2.6383F, 2.0F, 5.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.4737F, -13.1113F, -1.4399F, 0.0F, 0.0F));

        ModelPartData cube_r197 = pillars39.addChild("cube_r197", ModelPartBuilder.create().uv(174, 213).cuboid(-1.0F, -2.6472F, -2.6383F, 2.0F, 3.0F, 2.0F, new Dilation(0.01F)), ModelTransform.of(0.0F, -0.1842F, -9.915F, -0.9599F, 0.0F, 0.0F));

        ModelPartData cube_r198 = pillars39.addChild("cube_r198", ModelPartBuilder.create().uv(44, 141).mirrored().cuboid(0.5798F, -1.25F, -2.2431F, 0.0F, 2.0F, 3.0F, new Dilation(0.001F)).mirrored(false), ModelTransform.of(8.7403F, -24.9752F, -5.0462F, -3.1416F, 1.3963F, 3.1416F));

        ModelPartData cube_r199 = pillars39.addChild("cube_r199", ModelPartBuilder.create().uv(216, 24).mirrored().cuboid(-0.501F, -0.25F, 0.0F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(8.7403F, -24.9752F, -5.0462F, -3.1416F, 1.0472F, 3.1416F));

        ModelPartData cube_r200 = pillars39.addChild("cube_r200", ModelPartBuilder.create().uv(44, 141).cuboid(-0.5798F, -1.25F, -2.2431F, 0.0F, 2.0F, 3.0F, new Dilation(0.001F)), ModelTransform.of(8.7403F, -24.9752F, -5.0462F, -3.1416F, 0.6981F, 3.1416F));

        ModelPartData cube_r201 = pillars39.addChild("cube_r201", ModelPartBuilder.create().uv(44, 141).cuboid(-0.5719F, -1.25F, -1.9234F, 0.0F, 2.0F, 3.0F, new Dilation(0.001F)), ModelTransform.of(8.4241F, -24.9752F, 4.9985F, 0.0F, 1.3963F, 0.0F));

        ModelPartData cube_r202 = pillars39.addChild("cube_r202", ModelPartBuilder.create().uv(44, 141).mirrored().cuboid(0.7914F, -1.25F, -2.0032F, 0.0F, 2.0F, 3.0F, new Dilation(0.001F)).mirrored(false), ModelTransform.of(8.4241F, -24.9752F, 4.9985F, 0.0F, 0.6981F, 0.0F));

        ModelPartData cube_r203 = pillars39.addChild("cube_r203", ModelPartBuilder.create().uv(216, 24).mirrored().cuboid(-0.3842F, -0.25F, 0.2978F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(8.4241F, -24.9752F, 4.9985F, 0.0F, 1.0472F, 0.0F));

        ModelPartData cube_r204 = pillars39.addChild("cube_r204", ModelPartBuilder.create().uv(216, 24).mirrored().cuboid(-0.6175F, -0.25F, 0.2982F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(-7.7403F, -24.9752F, -4.3962F, -3.1416F, -1.0472F, 3.1416F));

        ModelPartData cube_r205 = pillars39.addChild("cube_r205", ModelPartBuilder.create().uv(44, 141).cuboid(-0.7913F, -1.25F, -2.0027F, 0.0F, 2.0F, 3.0F, new Dilation(0.001F)), ModelTransform.of(-7.7403F, -24.9752F, -4.3962F, -3.1416F, -1.3963F, 3.1416F));

        ModelPartData cube_r206 = pillars39.addChild("cube_r206", ModelPartBuilder.create().uv(44, 141).mirrored().cuboid(0.5723F, -1.25F, -1.9231F, 0.0F, 2.0F, 3.0F, new Dilation(0.001F)).mirrored(false), ModelTransform.of(-7.7403F, -24.9752F, -4.3962F, -3.1416F, -0.6981F, 3.1416F));

        ModelPartData cube_r207 = pillars39.addChild("cube_r207", ModelPartBuilder.create().uv(44, 141).cuboid(-0.5719F, -1.25F, -1.9234F, 0.0F, 2.0F, 3.0F, new Dilation(0.001F)), ModelTransform.of(-8.5408F, -24.9752F, 4.7962F, 0.0F, -0.6981F, 0.0F));

        ModelPartData cube_r208 = pillars39.addChild("cube_r208", ModelPartBuilder.create().uv(216, 24).mirrored().cuboid(-0.3842F, -0.25F, 0.2978F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(-8.5408F, -24.9752F, 4.7962F, 0.0F, -1.0472F, 0.0F));

        ModelPartData cube_r209 = pillars39.addChild("cube_r209", ModelPartBuilder.create().uv(44, 141).mirrored().cuboid(0.7914F, -1.25F, -2.0032F, 0.0F, 2.0F, 3.0F, new Dilation(0.001F)).mirrored(false), ModelTransform.of(-8.5408F, -24.9752F, 4.7962F, 0.0F, -1.3963F, 0.0F));

        ModelPartData cube_r210 = pillars39.addChild("cube_r210", ModelPartBuilder.create().uv(44, 141).mirrored().cuboid(0.7914F, -1.25F, -2.0032F, 0.0F, 2.0F, 3.0F, new Dilation(0.001F)).mirrored(false), ModelTransform.of(-0.1168F, -24.9752F, 9.7947F, 0.0F, -0.3491F, 0.0F));

        ModelPartData cube_r211 = pillars39.addChild("cube_r211", ModelPartBuilder.create().uv(44, 141).cuboid(-0.5719F, -1.25F, -1.9234F, 0.0F, 2.0F, 3.0F, new Dilation(0.001F)), ModelTransform.of(-0.1168F, -24.9752F, 9.7947F, 0.0F, 0.3491F, 0.0F));

        ModelPartData cube_r212 = pillars39.addChild("cube_r212", ModelPartBuilder.create().uv(44, 141).mirrored().cuboid(0.0F, -1.0F, -1.5F, 0.0F, 2.0F, 3.0F, new Dilation(0.001F)).mirrored(false), ModelTransform.of(0.799F, -25.2252F, -9.5925F, 0.0F, 0.3491F, 0.0F));

        ModelPartData cube_r213 = pillars39.addChild("cube_r213", ModelPartBuilder.create().uv(44, 141).cuboid(0.0F, -1.0F, -1.5F, 0.0F, 2.0F, 3.0F, new Dilation(0.001F)), ModelTransform.of(-0.799F, -25.2252F, -9.5925F, 0.0F, -0.3491F, 0.0F));

        ModelPartData cube_r214 = pillars39.addChild("cube_r214", ModelPartBuilder.create().uv(209, 201).cuboid(-1.0F, -0.5351F, -0.0031F, 2.0F, 2.0F, 4.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, -24.495F, -8.7766F, -1.1345F, 0.0F, 0.0F));

        ModelPartData cube_r215 = pillars39.addChild("cube_r215", ModelPartBuilder.create().uv(69, 150).cuboid(-1.2F, -4.6049F, -6.876F, 2.0F, 10.0F, 0.0F, new Dilation(0.001F))
                .uv(200, 201).cuboid(-1.0F, -8.6049F, -6.676F, 2.0F, 15.0F, 2.0F, new Dilation(0.01F)), ModelTransform.of(0.0F, -12.7212F, -12.7214F, -1.2654F, 0.0F, 0.0F));

        ModelPartData cube_r216 = pillars39.addChild("cube_r216", ModelPartBuilder.create().uv(84, 215).cuboid(21.0F, -3.5261F, -42.1375F, 2.0F, 3.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(-21.999F, 8.4485F, 12.8146F, -0.5672F, 0.0F, 0.0F));

        ModelPartData glassofhour = pillars39.addChild("glassofhour", ModelPartBuilder.create().uv(69, 161).cuboid(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(195, 26).cuboid(-0.5F, -2.5F, -0.5F, 1.0F, 2.0F, 1.0F, new Dilation(0.2F)), ModelTransform.pivot(-0.049F, -29.1252F, -9.7925F));

        ModelPartData cube_r217 = glassofhour.addChild("cube_r217", ModelPartBuilder.create().uv(195, 26).cuboid(-0.5F, -1.0F, -0.5F, 1.0F, 2.0F, 1.0F, new Dilation(0.2F)), ModelTransform.of(0.0F, 1.5F, 0.0F, 0.0F, 0.0F, -3.1416F));

        ModelPartData hourglass = pillars39.addChild("hourglass", ModelPartBuilder.create().uv(69, 161).cuboid(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(195, 26).cuboid(-0.5F, -2.5F, -0.5F, 1.0F, 2.0F, 1.0F, new Dilation(0.2F)), ModelTransform.pivot(-0.049F, -29.1252F, 9.7925F));

        ModelPartData cube_r218 = hourglass.addChild("cube_r218", ModelPartBuilder.create().uv(195, 26).cuboid(-0.5F, -1.0F, -0.5F, 1.0F, 2.0F, 1.0F, new Dilation(0.2F)), ModelTransform.of(0.0F, 1.5F, 0.0F, 0.0F, 0.0F, -3.1416F));

        ModelPartData pillars40 = pillars39.addChild("pillars40", ModelPartBuilder.create(), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData pillars41 = pillars40.addChild("pillars41", ModelPartBuilder.create(), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData pillars42 = pillars41.addChild("pillars42", ModelPartBuilder.create(), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData pillars43 = pillars38.addChild("pillars43", ModelPartBuilder.create().uv(35, 89).cuboid(-0.999F, -24.7252F, -10.2925F, 2.0F, 1.0F, 2.0F, new Dilation(0.0F))
                .uv(60, 110).cuboid(-1.0F, -22.0637F, -9.2598F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F))
                .uv(95, 183).cuboid(-1.0F, -20.8637F, -9.2598F, 2.0F, 21.0F, 2.0F, new Dilation(0.0F))
                .uv(17, 204).cuboid(-1.0F, -1.8637F, -15.2598F, 2.0F, 2.0F, 6.0F, new Dilation(-0.01F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData cube_r219 = pillars43.addChild("cube_r219", ModelPartBuilder.create().uv(66, 211).cuboid(-1.0F, -2.6472F, -2.6383F, 2.0F, 5.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.4737F, -13.1113F, -1.4399F, 0.0F, 0.0F));

        ModelPartData cube_r220 = pillars43.addChild("cube_r220", ModelPartBuilder.create().uv(174, 213).cuboid(-1.0F, -2.6472F, -2.6383F, 2.0F, 3.0F, 2.0F, new Dilation(0.01F)), ModelTransform.of(0.0F, -0.1842F, -9.915F, -0.9599F, 0.0F, 0.0F));

        ModelPartData cube_r221 = pillars43.addChild("cube_r221", ModelPartBuilder.create().uv(209, 201).cuboid(-1.0F, -0.5351F, -0.0031F, 2.0F, 2.0F, 4.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, -24.495F, -8.7766F, -1.1345F, 0.0F, 0.0F));

        ModelPartData cube_r222 = pillars43.addChild("cube_r222", ModelPartBuilder.create().uv(69, 150).cuboid(-1.2F, -4.6049F, -6.876F, 2.0F, 10.0F, 0.0F, new Dilation(0.001F))
                .uv(200, 201).cuboid(-1.0F, -8.6049F, -6.676F, 2.0F, 15.0F, 2.0F, new Dilation(0.01F)), ModelTransform.of(0.0F, -12.7212F, -12.7214F, -1.2654F, 0.0F, 0.0F));

        ModelPartData cube_r223 = pillars43.addChild("cube_r223", ModelPartBuilder.create().uv(84, 215).cuboid(21.0F, -3.5261F, -42.1375F, 2.0F, 3.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(-21.999F, 8.4485F, 12.8146F, -0.5672F, 0.0F, 0.0F));

        ModelPartData pillars44 = pillars43.addChild("pillars44", ModelPartBuilder.create(), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData pillars45 = pillars44.addChild("pillars45", ModelPartBuilder.create(), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData pillars46 = pillars45.addChild("pillars46", ModelPartBuilder.create(), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData rim10 = pannel6.addChild("rim10", ModelPartBuilder.create().uv(25, 128).cuboid(18.5F, -14.3F, -5.5F, 2.0F, 1.0F, 11.0F, new Dilation(0.001F))
                .uv(173, 111).cuboid(19.5F, -13.5F, -5.5F, 0.0F, 1.0F, 11.0F, new Dilation(0.0F))
                .uv(141, 196).cuboid(19.0F, -13.3F, 1.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.1F))
                .uv(141, 196).cuboid(19.0F, -13.3F, -2.9F, 1.0F, 1.0F, 1.0F, new Dilation(-0.1F)), ModelTransform.pivot(0.0F, -1.0F, 0.0F));

        ModelPartData cube_r224 = rim10.addChild("cube_r224", ModelPartBuilder.create().uv(141, 196).cuboid(20.0F, -15.8F, -5.7F, 1.0F, 1.0F, 1.0F, new Dilation(-0.1F))
                .uv(141, 196).cuboid(20.0F, -15.8F, -4.4F, 1.0F, 1.0F, 1.0F, new Dilation(-0.1F))
                .uv(141, 196).cuboid(20.0F, -15.8F, -3.1F, 1.0F, 1.0F, 1.0F, new Dilation(-0.1F))
                .uv(171, 146).cuboid(20.5F, -16.0F, -5.5F, 0.0F, 1.0F, 5.0F, new Dilation(0.0F)), ModelTransform.of(-1.0F, 2.5F, 0.0F, 0.0F, -0.5236F, 0.0F));

        ModelPartData cube_r225 = rim10.addChild("cube_r225", ModelPartBuilder.create().uv(141, 196).cuboid(20.0F, -15.8F, 3.2F, 1.0F, 1.0F, 1.0F, new Dilation(-0.1F))
                .uv(141, 196).cuboid(20.0F, -15.8F, 1.9F, 1.0F, 1.0F, 1.0F, new Dilation(-0.1F))
                .uv(141, 196).cuboid(20.0F, -15.8F, 4.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.1F))
                .uv(91, 207).cuboid(20.5F, -16.0F, -0.5F, 0.0F, 1.0F, 6.0F, new Dilation(0.0F)), ModelTransform.of(-1.0F, 2.5F, 0.0F, 0.0F, 0.5236F, 0.0F));

        ModelPartData cube_r226 = rim10.addChild("cube_r226", ModelPartBuilder.create().uv(206, 103).cuboid(18.5F, -16.0F, -5.5F, 2.0F, 1.0F, 5.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 1.7F, 0.0F, 0.0F, -0.5236F, 0.0F));

        ModelPartData cube_r227 = rim10.addChild("cube_r227", ModelPartBuilder.create().uv(205, 157).cuboid(18.5F, -16.0F, -0.5F, 2.0F, 1.0F, 6.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 1.7F, 0.0F, 0.0F, 0.5236F, 0.0F));

        ModelPartData rim11 = pannel6.addChild("rim11", ModelPartBuilder.create(), ModelTransform.pivot(-11.2F, -11.4F, 0.0F));

        ModelPartData panels6 = pannel6.addChild("panels6", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData cube_r228 = panels6.addChild("cube_r228", ModelPartBuilder.create().uv(216, 46).cuboid(-0.3902F, -0.2722F, -0.4969F, 1.0F, 1.0F, 1.0F, new Dilation(-0.1F)), ModelTransform.of(10.3166F, -20.3286F, -0.4573F, 0.0F, -0.7854F, 0.4363F));

        ModelPartData cube_r229 = panels6.addChild("cube_r229", ModelPartBuilder.create().uv(188, 216).cuboid(-0.5659F, -0.1322F, -0.5616F, 1.0F, 0.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(10.3166F, -20.3286F, -0.4573F, 0.0F, 0.0F, 0.4363F));

        ModelPartData cube_r230 = panels6.addChild("cube_r230", ModelPartBuilder.create().uv(216, 49).cuboid(-0.5659F, -0.2678F, -0.4384F, 1.0F, 1.0F, 1.0F, new Dilation(0.05F)), ModelTransform.of(10.3166F, -20.3286F, -0.4573F, -3.1416F, 0.0F, 0.4363F));

        ModelPartData cube_r231 = panels6.addChild("cube_r231", ModelPartBuilder.create().uv(216, 46).cuboid(-0.3902F, -0.2722F, -0.4969F, 1.0F, 1.0F, 1.0F, new Dilation(-0.11F)), ModelTransform.of(15.7166F, -18.6286F, 1.3427F, 0.0F, -0.7854F, 1.6755F));

        ModelPartData cube_r232 = panels6.addChild("cube_r232", ModelPartBuilder.create().uv(188, 216).cuboid(-0.5659F, -0.1322F, -0.5616F, 1.0F, 0.0F, 1.0F, new Dilation(-0.11F)), ModelTransform.of(15.7166F, -18.6286F, 1.3427F, 0.0F, 0.0F, 1.6755F));

        ModelPartData cube_r233 = panels6.addChild("cube_r233", ModelPartBuilder.create().uv(216, 49).cuboid(-0.5659F, -0.2678F, -0.4384F, 1.0F, 1.0F, 1.0F, new Dilation(-0.1F)), ModelTransform.of(15.7166F, -18.6286F, 1.3427F, -3.1416F, 0.0F, 1.6755F));

        ModelPartData cube_r234 = panels6.addChild("cube_r234", ModelPartBuilder.create().uv(216, 46).cuboid(-0.3902F, -0.2722F, -0.4969F, 1.0F, 1.0F, 1.0F, new Dilation(-0.1F)), ModelTransform.of(10.3166F, -20.3286F, 0.7427F, 0.0F, -0.7854F, 0.4363F));

        ModelPartData cube_r235 = panels6.addChild("cube_r235", ModelPartBuilder.create().uv(188, 216).cuboid(-0.5659F, -0.1322F, -0.5616F, 1.0F, 0.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(10.3166F, -20.3286F, 0.7427F, 0.0F, 0.0F, 0.4363F));

        ModelPartData cube_r236 = panels6.addChild("cube_r236", ModelPartBuilder.create().uv(216, 49).cuboid(-0.5659F, -0.2678F, -0.4384F, 1.0F, 1.0F, 1.0F, new Dilation(0.05F)), ModelTransform.of(10.3166F, -20.3286F, 0.7427F, -3.1416F, 0.0F, 0.4363F));

        ModelPartData cube_r237 = panels6.addChild("cube_r237", ModelPartBuilder.create().uv(141, 191).cuboid(-8.1F, 3.4F, 3.1F, 1.0F, 3.0F, 1.0F, new Dilation(-0.2F)), ModelTransform.of(18.4576F, -21.621F, 0.1084F, 0.0F, 0.0F, 0.4363F));

        ModelPartData cube_r238 = panels6.addChild("cube_r238", ModelPartBuilder.create().uv(216, 6).cuboid(-0.5F, -0.1F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.7F)), ModelTransform.of(17.1275F, -18.2737F, 0.0F, 0.0F, 0.0F, 0.4363F));

        ModelPartData cube_r239 = panels6.addChild("cube_r239", ModelPartBuilder.create().uv(183, 216).cuboid(0.1F, 0.0F, -0.4F, 1.0F, 0.0F, 1.0F, new Dilation(0.001F)), ModelTransform.of(11.1678F, -19.6752F, 2.359F, -0.8879F, 0.6199F, -0.6598F));

        ModelPartData cube_r240 = panels6.addChild("cube_r240", ModelPartBuilder.create().uv(216, 177).cuboid(0.1F, 0.0F, -0.4F, 1.0F, 0.0F, 1.0F, new Dilation(0.001F)), ModelTransform.of(11.8176F, -20.2194F, 1.8022F, -0.4629F, 0.9593F, -0.0779F));

        ModelPartData cube_r241 = panels6.addChild("cube_r241", ModelPartBuilder.create().uv(216, 175).cuboid(0.1F, 0.0F, -0.4F, 1.0F, 0.0F, 1.0F, new Dilation(0.001F)), ModelTransform.of(12.3958F, -20.2801F, 0.9868F, -0.2114F, 1.0177F, 0.2224F));

        ModelPartData cube_r242 = panels6.addChild("cube_r242", ModelPartBuilder.create().uv(216, 173).cuboid(-0.1F, 0.0F, -0.4F, 1.0F, 0.0F, 1.0F, new Dilation(0.001F)), ModelTransform.of(13.0691F, -20.1258F, 0.0042F, -0.1885F, 0.667F, 0.2627F));

        ModelPartData cube_r243 = panels6.addChild("cube_r243", ModelPartBuilder.create().uv(216, 171).cuboid(-0.3F, 0.0F, -0.5F, 1.0F, 0.0F, 1.0F, new Dilation(0.001F)), ModelTransform.of(13.9343F, -19.794F, -0.5101F, -0.0616F, 0.4284F, 0.5039F));

        ModelPartData cube_r244 = panels6.addChild("cube_r244", ModelPartBuilder.create().uv(216, 63).cuboid(-0.4F, 0.0F, -0.5F, 1.0F, 0.0F, 1.0F, new Dilation(0.001F)), ModelTransform.of(14.7904F, -19.2165F, -0.88F, -0.0097F, 0.218F, 0.7407F));

        ModelPartData cube_r245 = panels6.addChild("cube_r245", ModelPartBuilder.create().uv(216, 55).cuboid(-0.4F, 0.0F, -0.5F, 1.0F, 0.0F, 1.0F, new Dilation(0.001F)), ModelTransform.of(8.9878F, -21.1758F, 3.6349F, 3.123F, 1.0386F, -2.7136F));

        ModelPartData cube_r246 = panels6.addChild("cube_r246", ModelPartBuilder.create().uv(171, 183).cuboid(-1.3F, 0.0F, -0.7F, 1.0F, 0.0F, 1.0F, new Dilation(0.001F)), ModelTransform.of(8.5657F, -21.3572F, 1.7778F, 3.1063F, 1.3003F, -2.7316F));

        ModelPartData cube_r247 = panels6.addChild("cube_r247", ModelPartBuilder.create().uv(216, 59).cuboid(-1.3F, 0.0F, -0.5F, 1.0F, 0.0F, 1.0F, new Dilation(0.001F)), ModelTransform.of(9.4101F, -20.9519F, 1.4406F, 0.0116F, 0.6194F, 0.4508F));

        ModelPartData cube_r248 = panels6.addChild("cube_r248", ModelPartBuilder.create().uv(216, 57).cuboid(-0.4F, 0.0F, -0.5F, 1.0F, 0.0F, 1.0F, new Dilation(0.001F)), ModelTransform.of(9.5325F, -20.8948F, 1.5494F, 0.0098F, 0.2703F, 0.4466F));

        ModelPartData cube_r249 = panels6.addChild("cube_r249", ModelPartBuilder.create().uv(216, 12).cuboid(-1.4F, 0.0F, -0.5F, 1.0F, 0.0F, 1.0F, new Dilation(0.001F)), ModelTransform.of(11.3479F, -20.0295F, 1.3858F, 0.0094F, 0.0085F, 0.4441F));

        ModelPartData cube_r250 = panels6.addChild("cube_r250", ModelPartBuilder.create().uv(215, 199).cuboid(-0.5F, 0.0F, -0.5F, 1.0F, 0.0F, 1.0F, new Dilation(0.001F)), ModelTransform.of(11.3966F, -20.0059F, 1.3436F, 0.0095F, 0.0958F, 0.4449F));

        ModelPartData cube_r251 = panels6.addChild("cube_r251", ModelPartBuilder.create().uv(209, 24).cuboid(-0.5F, 0.0F, -0.5F, 1.0F, 0.0F, 1.0F, new Dilation(0.001F)), ModelTransform.of(12.2319F, -19.607F, 1.1972F, 0.0097F, 0.218F, 0.4461F));

        ModelPartData cube_r252 = panels6.addChild("cube_r252", ModelPartBuilder.create().uv(204, 14).cuboid(-0.5F, 0.0F, -0.5F, 1.0F, 0.0F, 1.0F, new Dilation(0.001F)), ModelTransform.of(13.1127F, -19.1858F, 0.981F, 0.0097F, 0.218F, 0.4461F));

        ModelPartData cube_r253 = panels6.addChild("cube_r253", ModelPartBuilder.create().uv(199, 14).cuboid(-0.5F, 0.0F, -0.5F, 1.0F, 0.0F, 1.0F, new Dilation(0.001F)), ModelTransform.of(13.9934F, -18.7645F, 0.7648F, 0.0097F, 0.218F, 0.4461F));

        ModelPartData cube_r254 = panels6.addChild("cube_r254", ModelPartBuilder.create().uv(193, 185).cuboid(-2.4F, 0.2F, -0.5F, 1.0F, 0.0F, 1.0F, new Dilation(0.001F)), ModelTransform.of(16.6689F, -17.8225F, 0.1372F, 0.0F, 0.2182F, 0.4014F));

        ModelPartData cube_r255 = panels6.addChild("cube_r255", ModelPartBuilder.create().uv(78, 181).cuboid(-0.5F, 0.2F, -0.5F, 1.0F, 0.0F, 1.0F, new Dilation(0.001F)), ModelTransform.of(15.8807F, -18.157F, 0.45F, 0.0F, 0.0F, 0.4014F));

        ModelPartData cube_r256 = panels6.addChild("cube_r256", ModelPartBuilder.create().uv(176, 183).cuboid(-0.5F, 0.0F, -0.5F, 1.0F, 0.0F, 1.0F, new Dilation(0.001F)), ModelTransform.of(15.5807F, -18.457F, -1.0F, 0.0F, 0.0F, 0.7854F));

        ModelPartData cube_r257 = panels6.addChild("cube_r257", ModelPartBuilder.create().uv(150, 81).cuboid(-0.5F, 0.0F, -0.5F, 1.0F, 0.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(16.3877F, -17.892F, -1.0F, 0.0F, 0.0F, 0.4363F));

        ModelPartData cube_r258 = panels6.addChild("cube_r258", ModelPartBuilder.create().uv(201, 43).cuboid(6.8F, -23.3F, -1.0F, 2.0F, 0.0F, 2.0F, new Dilation(0.001F))
                .uv(215, 115).cuboid(6.8F, -23.2F, -1.0F, 2.0F, 1.0F, 2.0F, new Dilation(0.0F))
                .uv(216, 6).cuboid(1.5F, -23.0F, 2.1F, 1.0F, 1.0F, 1.0F, new Dilation(-0.19F))
                .uv(216, 6).cuboid(3.5F, -23.0F, -1.0F, 1.0F, 1.0F, 1.0F, new Dilation(-0.19F))
                .uv(148, 135).cuboid(-1.2F, -22.6F, -4.0F, 6.0F, 0.0F, 8.0F, new Dilation(0.001F))
                .uv(69, 38).cuboid(-5.2F, -22.2F, -9.0F, 16.0F, 0.0F, 18.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.4363F));

        ModelPartData cube_r259 = panels6.addChild("cube_r259", ModelPartBuilder.create().uv(84, 164).cuboid(-2.0F, 0.0F, -0.75F, 4.0F, 0.0F, 1.0F, new Dilation(0.001F)), ModelTransform.of(17.3999F, -16.4916F, 0.5F, 0.0F, 0.0F, 0.48F));

        ModelPartData cube_r260 = panels6.addChild("cube_r260", ModelPartBuilder.create().uv(26, 213).cuboid(-0.5F, -0.2F, -2.5F, 1.0F, 0.0F, 5.0F, new Dilation(0.001F)), ModelTransform.of(15.7576F, -18.3503F, 2.5F, 0.0873F, 0.0F, 0.1745F));

        ModelPartData cube_r261 = panels6.addChild("cube_r261", ModelPartBuilder.create().uv(138, 53).cuboid(-3.1F, 0.0F, 3.5F, 6.0F, 0.0F, 1.0F, new Dilation(0.001F)), ModelTransform.of(15.8561F, -18.333F, -3.5F, 0.0F, 0.0F, 0.1745F));

        ModelPartData cube_r262 = panels6.addChild("cube_r262", ModelPartBuilder.create().uv(0, 0).cuboid(-4.0F, -21.5F, -9.0F, 16.0F, 0.0F, 18.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.3927F));

        ModelPartData button2 = panels6.addChild("button2", ModelPartBuilder.create(), ModelTransform.pivot(18.4576F, -21.621F, 0.1084F));

        ModelPartData cube_r263 = button2.addChild("cube_r263", ModelPartBuilder.create().uv(36, 190).cuboid(-6.4F, 3.4F, -0.6F, 1.0F, 3.0F, 1.0F, new Dilation(-0.1F))
                .uv(216, 36).cuboid(-6.4F, 3.8F, -0.6F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.4363F));

        ModelPartData under6 = pannel6.addChild("under6", ModelPartBuilder.create().uv(0, 76).cuboid(6.9F, -14.3F, -5.5F, 12.0F, 1.0F, 11.0F, new Dilation(0.001F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData cube_r264 = under6.addChild("cube_r264", ModelPartBuilder.create().uv(117, 76).cuboid(9.5F, -16.0F, -5.5F, 11.0F, 1.0F, 5.0F, new Dilation(0.0F)), ModelTransform.of(-1.6F, 1.7F, 0.0F, 0.0F, -0.5236F, 0.0F));

        ModelPartData cube_r265 = under6.addChild("cube_r265", ModelPartBuilder.create().uv(138, 147).cuboid(9.5F, -16.0F, 0.5F, 11.0F, 1.0F, 5.0F, new Dilation(0.0F)), ModelTransform.of(-1.6F, 1.7F, 0.0F, 0.0F, 0.5236F, 0.0F));

        ModelPartData bone7 = console.addChild("bone7", ModelPartBuilder.create().uv(95, 98).cuboid(-1.1F, -20.5F, -4.0F, 9.0F, 0.0F, 8.0F, new Dilation(0.001F)), ModelTransform.pivot(0.0F, 22.25F, 0.0F));

        ModelPartData bone8 = bone7.addChild("bone8", ModelPartBuilder.create().uv(95, 98).cuboid(-1.1F, -20.5F, -4.0F, 9.0F, 0.0F, 8.0F, new Dilation(0.0019F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData bone9 = bone8.addChild("bone9", ModelPartBuilder.create().uv(95, 98).cuboid(-1.1F, -20.5F, -4.0F, 9.0F, 0.0F, 8.0F, new Dilation(0.002F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData bone10 = bone9.addChild("bone10", ModelPartBuilder.create().uv(95, 98).cuboid(-1.1F, -20.5F, -4.0F, 9.0F, 0.0F, 8.0F, new Dilation(0.0015F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData bone11 = bone10.addChild("bone11", ModelPartBuilder.create().uv(95, 98).cuboid(-1.1F, -20.5F, -4.0F, 9.0F, 0.0F, 8.0F, new Dilation(0.0016F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData bone12 = bone11.addChild("bone12", ModelPartBuilder.create().uv(95, 98).cuboid(-1.1F, -20.5F, -4.0F, 9.0F, 0.0F, 8.0F, new Dilation(0.0017F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData rotor = console.addChild("rotor", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, -20.0F, 0.0F));

        ModelPartData bone13 = rotor.addChild("bone13", ModelPartBuilder.create().uv(120, 107).cuboid(3.9F, -12.0F, -3.5F, 3.0F, 12.0F, 7.0F, new Dilation(0.0F)), ModelTransform.pivot(-1.0F, -1.75F, 0.0F));

        ModelPartData bone14 = bone13.addChild("bone14", ModelPartBuilder.create().uv(0, 151).cuboid(3.4811F, -9.0F, -2.7744F, 3.0F, 9.0F, 7.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData bone15 = bone14.addChild("bone15", ModelPartBuilder.create().uv(180, 0).cuboid(3.6433F, -8.0F, -2.7744F, 2.0F, 8.0F, 7.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData bone16 = bone15.addChild("bone16", ModelPartBuilder.create().uv(157, 81).cuboid(2.2244F, -9.0F, -3.5F, 3.0F, 9.0F, 7.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData bone17 = bone16.addChild("bone17", ModelPartBuilder.create().uv(0, 132).cuboid(2.6433F, -11.0F, -4.2256F, 3.0F, 11.0F, 7.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData bone18 = bone17.addChild("bone18", ModelPartBuilder.create().uv(105, 166).cuboid(3.4811F, -8.0F, -4.2256F, 3.0F, 8.0F, 7.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData bone19 = rotor.addChild("bone19", ModelPartBuilder.create().uv(70, 93).cuboid(0.9F, -11.0F, -3.0F, 6.0F, 15.0F, 6.0F, new Dilation(0.0F)), ModelTransform.pivot(-1.5F, -8.75F, 0.0F));

        ModelPartData bone20 = bone19.addChild("bone20", ModelPartBuilder.create().uv(95, 107).cuboid(0.0481F, -10.0F, -1.5244F, 6.0F, 11.0F, 6.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData bone21 = bone20.addChild("bone21", ModelPartBuilder.create().uv(21, 175).cuboid(0.3442F, -8.0F, -1.5244F, 4.0F, 8.0F, 6.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData bone22 = bone21.addChild("bone22", ModelPartBuilder.create().uv(42, 181).cuboid(1.4923F, -10.0F, -3.0F, 2.0F, 11.0F, 6.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData bone23 = bone22.addChild("bone23", ModelPartBuilder.create().uv(132, 57).cuboid(0.3442F, -9.0F, -4.4756F, 4.0F, 12.0F, 6.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData bone24 = bone23.addChild("bone24", ModelPartBuilder.create().uv(52, 133).cuboid(1.0481F, -10.0F, -4.4756F, 5.0F, 10.0F, 6.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData bone25 = rotor.addChild("bone25", ModelPartBuilder.create().uv(148, 121).cuboid(-6.5311F, 18.0F, -7.8122F, 7.0F, 8.0F, 5.0F, new Dilation(0.02F))
                .uv(104, 200).cuboid(-8.1913F, 18.0F, -7.8122F, 2.0F, 8.0F, 5.0F, new Dilation(0.0F)), ModelTransform.pivot(4.0234F, -40.25F, 5.3122F));

        ModelPartData cube_r266 = bone25.addChild("cube_r266", ModelPartBuilder.create().uv(59, 197).cuboid(-7.0F, -2.5F, 3.5F, 3.0F, 8.0F, 5.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 20.5F, 0.0F, 0.0F, -2.0944F, 0.0F));

        ModelPartData cube_r267 = bone25.addChild("cube_r267", ModelPartBuilder.create().uv(183, 191).cuboid(-2.0F, -2.5F, 3.5F, 3.0F, 8.0F, 5.0F, new Dilation(0.01F)), ModelTransform.of(-0.3301F, 20.5F, 0.5718F, 0.0F, -2.0944F, 0.0F));

        ModelPartData cube_r268 = bone25.addChild("cube_r268", ModelPartBuilder.create().uv(59, 197).cuboid(-9.0F, -2.5F, -2.5F, 3.0F, 8.0F, 5.0F, new Dilation(0.01F)), ModelTransform.of(-1.5263F, 20.5F, -1.2679F, 0.0F, -1.0472F, 0.0F));

        ModelPartData cube_r269 = bone25.addChild("cube_r269", ModelPartBuilder.create().uv(183, 191).cuboid(-2.0F, -2.5F, -2.5F, 3.0F, 8.0F, 5.0F, new Dilation(0.0F)), ModelTransform.of(-2.1962F, 20.5F, -2.4282F, 0.0F, -1.0472F, 0.0F));

        ModelPartData rotor2 = rotor.addChild("rotor2", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData bone26 = rotor2.addChild("bone26", ModelPartBuilder.create().uv(85, 125).cuboid(3.9F, -12.0F, -3.5F, 3.0F, 12.0F, 7.0F, new Dilation(0.5F))
                .uv(127, 127).cuboid(4.1F, -12.0F, -3.5F, 3.0F, 12.0F, 7.0F, new Dilation(0.4F))
                .uv(106, 127).cuboid(3.5F, -12.0F, -3.5F, 3.0F, 12.0F, 7.0F, new Dilation(0.6F)), ModelTransform.pivot(-1.0F, -1.75F, 0.0F));

        ModelPartData bone27 = bone26.addChild("bone27", ModelPartBuilder.create().uv(21, 158).cuboid(3.4811F, -9.0F, -2.7744F, 3.0F, 9.0F, 7.0F, new Dilation(0.5F))
                .uv(161, 30).cuboid(3.6811F, -9.0F, -2.7744F, 3.0F, 9.0F, 7.0F, new Dilation(0.4F))
                .uv(84, 166).cuboid(2.9811F, -9.0F, -2.7744F, 3.0F, 9.0F, 7.0F, new Dilation(0.6F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData bone28 = bone27.addChild("bone28", ModelPartBuilder.create().uv(59, 181).cuboid(3.6433F, -8.0F, -2.7744F, 2.0F, 8.0F, 7.0F, new Dilation(0.5F))
                .uv(147, 183).cuboid(3.2433F, -8.0F, -2.7744F, 2.0F, 8.0F, 7.0F, new Dilation(0.5F))
                .uv(182, 30).cuboid(3.9433F, -8.0F, -2.7744F, 2.0F, 8.0F, 7.0F, new Dilation(0.4F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData bone29 = bone28.addChild("bone29", ModelPartBuilder.create().uv(159, 0).cuboid(2.2244F, -9.0F, -3.5F, 3.0F, 9.0F, 7.0F, new Dilation(0.5F))
                .uv(63, 164).cuboid(1.8244F, -9.0F, -3.5F, 3.0F, 9.0F, 7.0F, new Dilation(0.6F))
                .uv(42, 164).cuboid(2.6244F, -9.0F, -3.5F, 3.0F, 9.0F, 7.0F, new Dilation(0.4F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData bone30 = bone29.addChild("bone30", ModelPartBuilder.create().uv(136, 83).cuboid(2.6433F, -11.0F, -4.2256F, 3.0F, 11.0F, 7.0F, new Dilation(0.5F))
                .uv(117, 147).cuboid(2.9433F, -11.0F, -4.2256F, 3.0F, 11.0F, 7.0F, new Dilation(0.4F))
                .uv(96, 147).cuboid(2.2433F, -11.0F, -4.2256F, 3.0F, 11.0F, 7.0F, new Dilation(0.6F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData bone31 = bone30.addChild("bone31", ModelPartBuilder.create().uv(161, 167).cuboid(3.4811F, -8.0F, -4.2256F, 3.0F, 8.0F, 7.0F, new Dilation(0.5F))
                .uv(126, 169).cuboid(3.6811F, -8.0F, -4.2256F, 3.0F, 8.0F, 7.0F, new Dilation(0.4F))
                .uv(0, 168).cuboid(2.9811F, -8.0F, -4.2256F, 3.0F, 8.0F, 7.0F, new Dilation(0.6F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData bone32 = rotor2.addChild("bone32", ModelPartBuilder.create().uv(161, 17).cuboid(0.8F, -11.0F, -3.0F, 6.0F, 6.0F, 6.0F, new Dilation(0.5F))
                .uv(162, 98).cuboid(1.0F, -10.9F, -3.0F, 6.0F, 6.0F, 6.0F, new Dilation(0.4F))
                .uv(161, 154).cuboid(0.6F, -11.0F, -3.0F, 6.0F, 6.0F, 6.0F, new Dilation(0.5F)), ModelTransform.pivot(-1.5F, -8.75F, 0.0F));

        ModelPartData bone33 = bone32.addChild("bone33", ModelPartBuilder.create().uv(35, 110).cuboid(-0.0519F, -10.0F, -1.5244F, 6.0F, 11.0F, 6.0F, new Dilation(0.5F))
                .uv(60, 115).cuboid(0.1481F, -10.0F, -1.5244F, 6.0F, 11.0F, 6.0F, new Dilation(0.4F))
                .uv(0, 114).cuboid(-0.3519F, -10.0F, -1.5244F, 6.0F, 11.0F, 6.0F, new Dilation(0.6F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData bone34 = bone33.addChild("bone34", ModelPartBuilder.create().uv(177, 124).cuboid(0.3442F, -8.0F, -1.5244F, 4.0F, 8.0F, 6.0F, new Dilation(0.5F))
                .uv(178, 62).cuboid(-0.0558F, -8.0F, -1.5244F, 4.0F, 8.0F, 6.0F, new Dilation(0.5F))
                .uv(178, 47).cuboid(0.7442F, -8.0F, -1.5244F, 4.0F, 8.0F, 6.0F, new Dilation(0.4F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData bone35 = bone34.addChild("bone35", ModelPartBuilder.create().uv(105, 182).cuboid(1.4923F, -10.0F, -3.0F, 2.0F, 11.0F, 6.0F, new Dilation(0.5F))
                .uv(78, 183).cuboid(1.8923F, -10.0F, -3.0F, 2.0F, 11.0F, 6.0F, new Dilation(0.4F))
                .uv(182, 167).cuboid(1.0923F, -10.0F, -3.0F, 2.0F, 11.0F, 6.0F, new Dilation(0.6F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData bone36 = bone35.addChild("bone36", ModelPartBuilder.create().uv(138, 0).cuboid(0.3442F, -9.0F, -4.4756F, 4.0F, 12.0F, 6.0F, new Dilation(0.5F))
                .uv(75, 145).cuboid(0.7442F, -9.0F, -4.4756F, 4.0F, 12.0F, 6.0F, new Dilation(0.4F))
                .uv(141, 102).cuboid(-0.0558F, -9.0F, -4.4756F, 4.0F, 12.0F, 6.0F, new Dilation(0.6F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData bone37 = bone36.addChild("bone37", ModelPartBuilder.create().uv(138, 19).cuboid(1.0481F, -10.0F, -4.4756F, 5.0F, 10.0F, 6.0F, new Dilation(0.5F))
                .uv(21, 141).cuboid(1.2481F, -10.0F, -4.4756F, 5.0F, 10.0F, 6.0F, new Dilation(0.4F))
                .uv(138, 36).cuboid(0.6481F, -10.0F, -4.4756F, 5.0F, 10.0F, 6.0F, new Dilation(0.6F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData bone38 = rotor2.addChild("bone38", ModelPartBuilder.create().uv(44, 150).cuboid(-6.5311F, 18.0F, -7.8122F, 7.0F, 8.0F, 5.0F, new Dilation(0.5F))
                .uv(153, 67).cuboid(-6.2311F, 18.0F, -7.8122F, 7.0F, 8.0F, 5.0F, new Dilation(0.4F))
                .uv(153, 53).cuboid(-6.7311F, 18.0F, -7.8122F, 7.0F, 8.0F, 5.0F, new Dilation(0.6F))
                .uv(200, 187).cuboid(-8.1913F, 18.0F, -7.8122F, 2.0F, 8.0F, 5.0F, new Dilation(0.5F))
                .uv(201, 26).cuboid(-7.7913F, 18.0F, -7.8122F, 2.0F, 8.0F, 5.0F, new Dilation(0.6F))
                .uv(76, 201).cuboid(-8.3913F, 18.0F, -7.8122F, 2.0F, 8.0F, 5.0F, new Dilation(0.4F)), ModelTransform.pivot(4.0234F, -40.25F, 5.3122F));

        ModelPartData cube_r270 = bone38.addChild("cube_r270", ModelPartBuilder.create().uv(36, 199).cuboid(-6.6F, -2.5F, 3.5F, 3.0F, 8.0F, 5.0F, new Dilation(0.6F))
                .uv(199, 46).cuboid(-7.3F, -2.5F, 3.5F, 3.0F, 8.0F, 5.0F, new Dilation(0.4F))
                .uv(199, 0).cuboid(-7.0F, -2.5F, 3.5F, 3.0F, 8.0F, 5.0F, new Dilation(0.5F)), ModelTransform.of(0.0F, 20.5F, 0.0F, 0.0F, -2.0944F, 0.0F));

        ModelPartData cube_r271 = bone38.addChild("cube_r271", ModelPartBuilder.create().uv(19, 190).cuboid(-2.4F, -2.5F, 3.5F, 3.0F, 8.0F, 5.0F, new Dilation(0.6F))
                .uv(198, 125).cuboid(-2.0F, -2.5F, 3.5F, 3.0F, 8.0F, 5.0F, new Dilation(0.5F)), ModelTransform.of(-0.3301F, 20.5F, 0.5718F, 0.0F, -2.0944F, 0.0F));

        ModelPartData cube_r272 = bone38.addChild("cube_r272", ModelPartBuilder.create().uv(139, 199).cuboid(-1.4F, -4.0F, -2.5F, 3.0F, 8.0F, 5.0F, new Dilation(0.4F)), ModelTransform.of(-5.3763F, 22.0F, -2.688F, 0.0F, -2.0944F, 0.0F));

        ModelPartData cube_r273 = bone38.addChild("cube_r273", ModelPartBuilder.create().uv(199, 166).cuboid(-9.2F, -2.5F, -2.5F, 3.0F, 8.0F, 5.0F, new Dilation(0.4F))
                .uv(166, 191).cuboid(-8.6F, -2.5F, -2.5F, 3.0F, 8.0F, 5.0F, new Dilation(0.6F))
                .uv(198, 111).cuboid(-9.0F, -2.5F, -2.5F, 3.0F, 8.0F, 5.0F, new Dilation(0.5F)), ModelTransform.of(-1.5263F, 20.5F, -1.2679F, 0.0F, -1.0472F, 0.0F));

        ModelPartData cube_r274 = bone38.addChild("cube_r274", ModelPartBuilder.create().uv(199, 60).cuboid(-1.7F, -2.4F, -2.5F, 3.0F, 8.0F, 5.0F, new Dilation(0.4F))
                .uv(122, 199).cuboid(-2.4F, -2.5F, -2.5F, 3.0F, 8.0F, 5.0F, new Dilation(0.6F))
                .uv(0, 198).cuboid(-2.0F, -2.5F, -2.5F, 3.0F, 8.0F, 5.0F, new Dilation(0.5F)), ModelTransform.of(-2.1962F, 20.5F, -2.4282F, 0.0F, -1.0472F, 0.0F));

        ModelPartData rotor3 = rotor.addChild("rotor3", ModelPartBuilder.create(), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -3.1416F));

        ModelPartData bone39 = rotor3.addChild("bone39", ModelPartBuilder.create().uv(120, 107).cuboid(3.9F, -8.5F, -3.5F, 3.0F, 12.0F, 7.0F, new Dilation(0.0F)), ModelTransform.pivot(-1.0F, -1.75F, 0.0F));

        ModelPartData bone40 = bone39.addChild("bone40", ModelPartBuilder.create().uv(0, 151).cuboid(3.4811F, -5.5F, -2.7744F, 3.0F, 9.0F, 7.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData bone41 = bone40.addChild("bone41", ModelPartBuilder.create().uv(180, 0).cuboid(3.6433F, -4.5F, -2.7744F, 2.0F, 8.0F, 7.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData bone42 = bone41.addChild("bone42", ModelPartBuilder.create().uv(157, 81).cuboid(2.2244F, -5.5F, -3.5F, 3.0F, 9.0F, 7.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData bone43 = bone42.addChild("bone43", ModelPartBuilder.create().uv(0, 132).cuboid(2.6433F, -7.5F, -4.2256F, 3.0F, 11.0F, 7.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData bone44 = bone43.addChild("bone44", ModelPartBuilder.create().uv(105, 166).cuboid(3.4811F, -4.5F, -4.2256F, 3.0F, 8.0F, 7.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData bone45 = rotor3.addChild("bone45", ModelPartBuilder.create().uv(70, 93).cuboid(0.9F, -7.5F, -3.0F, 6.0F, 15.0F, 6.0F, new Dilation(0.0F)), ModelTransform.pivot(-1.5F, -8.75F, 0.0F));

        ModelPartData bone46 = bone45.addChild("bone46", ModelPartBuilder.create().uv(95, 107).cuboid(0.0481F, -6.5F, -1.5244F, 6.0F, 11.0F, 6.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData bone47 = bone46.addChild("bone47", ModelPartBuilder.create().uv(21, 175).cuboid(0.3442F, -4.5F, -1.5244F, 4.0F, 8.0F, 6.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData bone48 = bone47.addChild("bone48", ModelPartBuilder.create().uv(42, 181).cuboid(1.4923F, -6.5F, -3.0F, 2.0F, 11.0F, 6.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData bone49 = bone48.addChild("bone49", ModelPartBuilder.create().uv(132, 57).cuboid(0.3442F, -5.5F, -4.4756F, 4.0F, 12.0F, 6.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData bone50 = bone49.addChild("bone50", ModelPartBuilder.create().uv(52, 133).cuboid(1.0481F, -6.5F, -4.4756F, 5.0F, 10.0F, 6.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData bone51 = rotor3.addChild("bone51", ModelPartBuilder.create().uv(148, 121).cuboid(-6.5311F, 21.5F, -7.8122F, 7.0F, 8.0F, 5.0F, new Dilation(0.02F))
                .uv(104, 200).cuboid(-8.1913F, 21.5F, -7.8122F, 2.0F, 8.0F, 5.0F, new Dilation(0.0F)), ModelTransform.pivot(4.0234F, -40.25F, 5.3122F));

        ModelPartData cube_r275 = bone51.addChild("cube_r275", ModelPartBuilder.create().uv(59, 197).cuboid(-7.0F, 1.0F, 3.5F, 3.0F, 8.0F, 5.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 20.5F, 0.0F, 0.0F, -2.0944F, 0.0F));

        ModelPartData cube_r276 = bone51.addChild("cube_r276", ModelPartBuilder.create().uv(183, 191).cuboid(-2.0F, 1.0F, 3.5F, 3.0F, 8.0F, 5.0F, new Dilation(0.01F)), ModelTransform.of(-0.3301F, 20.5F, 0.5718F, 0.0F, -2.0944F, 0.0F));

        ModelPartData cube_r277 = bone51.addChild("cube_r277", ModelPartBuilder.create().uv(59, 197).cuboid(-9.0F, 1.0F, -2.5F, 3.0F, 8.0F, 5.0F, new Dilation(0.01F)), ModelTransform.of(-1.5263F, 20.5F, -1.2679F, 0.0F, -1.0472F, 0.0F));

        ModelPartData cube_r278 = bone51.addChild("cube_r278", ModelPartBuilder.create().uv(183, 191).cuboid(-2.0F, 1.0F, -2.5F, 3.0F, 8.0F, 5.0F, new Dilation(0.0F)), ModelTransform.of(-2.1962F, 20.5F, -2.4282F, 0.0F, -1.0472F, 0.0F));

        ModelPartData rotor4 = rotor3.addChild("rotor4", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData bone52 = rotor4.addChild("bone52", ModelPartBuilder.create().uv(85, 125).cuboid(3.9F, -8.5F, -3.5F, 3.0F, 12.0F, 7.0F, new Dilation(0.5F))
                .uv(127, 127).cuboid(4.1F, -8.5F, -3.5F, 3.0F, 12.0F, 7.0F, new Dilation(0.4F))
                .uv(106, 127).cuboid(3.5F, -8.5F, -3.5F, 3.0F, 12.0F, 7.0F, new Dilation(0.6F)), ModelTransform.pivot(-1.0F, -1.75F, 0.0F));

        ModelPartData bone53 = bone52.addChild("bone53", ModelPartBuilder.create().uv(21, 158).cuboid(3.4811F, -5.5F, -2.7744F, 3.0F, 9.0F, 7.0F, new Dilation(0.5F))
                .uv(161, 30).cuboid(3.6811F, -5.5F, -2.7744F, 3.0F, 9.0F, 7.0F, new Dilation(0.4F))
                .uv(84, 166).cuboid(2.9811F, -5.5F, -2.7744F, 3.0F, 9.0F, 7.0F, new Dilation(0.6F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData bone54 = bone53.addChild("bone54", ModelPartBuilder.create().uv(59, 181).cuboid(3.6433F, -4.5F, -2.7744F, 2.0F, 8.0F, 7.0F, new Dilation(0.5F))
                .uv(147, 183).cuboid(3.2433F, -4.5F, -2.7744F, 2.0F, 8.0F, 7.0F, new Dilation(0.5F))
                .uv(182, 30).cuboid(3.9433F, -4.5F, -2.7744F, 2.0F, 8.0F, 7.0F, new Dilation(0.4F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData bone55 = bone54.addChild("bone55", ModelPartBuilder.create().uv(159, 0).cuboid(2.2244F, -5.5F, -3.5F, 3.0F, 9.0F, 7.0F, new Dilation(0.5F))
                .uv(63, 164).cuboid(1.8244F, -5.5F, -3.5F, 3.0F, 9.0F, 7.0F, new Dilation(0.6F))
                .uv(42, 164).cuboid(2.6244F, -5.5F, -3.5F, 3.0F, 9.0F, 7.0F, new Dilation(0.4F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData bone56 = bone55.addChild("bone56", ModelPartBuilder.create().uv(136, 83).cuboid(2.6433F, -7.5F, -4.2256F, 3.0F, 11.0F, 7.0F, new Dilation(0.5F))
                .uv(117, 147).cuboid(2.9433F, -7.5F, -4.2256F, 3.0F, 11.0F, 7.0F, new Dilation(0.4F))
                .uv(96, 147).cuboid(2.2433F, -7.5F, -4.2256F, 3.0F, 11.0F, 7.0F, new Dilation(0.6F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData bone57 = bone56.addChild("bone57", ModelPartBuilder.create().uv(161, 167).cuboid(3.4811F, -4.5F, -4.2256F, 3.0F, 8.0F, 7.0F, new Dilation(0.5F))
                .uv(126, 169).cuboid(3.6811F, -4.5F, -4.2256F, 3.0F, 8.0F, 7.0F, new Dilation(0.4F))
                .uv(0, 168).cuboid(2.9811F, -4.5F, -4.2256F, 3.0F, 8.0F, 7.0F, new Dilation(0.6F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData bone58 = rotor4.addChild("bone58", ModelPartBuilder.create().uv(161, 17).cuboid(0.8F, -7.5F, -3.0F, 6.0F, 6.0F, 6.0F, new Dilation(0.5F))
                .uv(162, 98).cuboid(1.0F, -7.4F, -3.0F, 6.0F, 6.0F, 6.0F, new Dilation(0.4F))
                .uv(161, 154).cuboid(0.6F, -7.5F, -3.0F, 6.0F, 6.0F, 6.0F, new Dilation(0.5F)), ModelTransform.pivot(-1.5F, -8.75F, 0.0F));

        ModelPartData bone59 = bone58.addChild("bone59", ModelPartBuilder.create().uv(35, 110).cuboid(-0.0519F, -6.5F, -1.5244F, 6.0F, 11.0F, 6.0F, new Dilation(0.5F))
                .uv(60, 115).cuboid(0.1481F, -6.5F, -1.5244F, 6.0F, 11.0F, 6.0F, new Dilation(0.4F))
                .uv(0, 114).cuboid(-0.3519F, -6.5F, -1.5244F, 6.0F, 11.0F, 6.0F, new Dilation(0.6F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData bone60 = bone59.addChild("bone60", ModelPartBuilder.create().uv(177, 124).cuboid(0.3442F, -4.5F, -1.5244F, 4.0F, 8.0F, 6.0F, new Dilation(0.5F))
                .uv(178, 62).cuboid(-0.0558F, -4.5F, -1.5244F, 4.0F, 8.0F, 6.0F, new Dilation(0.5F))
                .uv(178, 47).cuboid(0.7442F, -4.5F, -1.5244F, 4.0F, 8.0F, 6.0F, new Dilation(0.4F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData bone61 = bone60.addChild("bone61", ModelPartBuilder.create().uv(105, 182).cuboid(1.4923F, -6.5F, -3.0F, 2.0F, 11.0F, 6.0F, new Dilation(0.5F))
                .uv(78, 183).cuboid(1.8923F, -6.5F, -3.0F, 2.0F, 11.0F, 6.0F, new Dilation(0.4F))
                .uv(182, 167).cuboid(1.0923F, -6.5F, -3.0F, 2.0F, 11.0F, 6.0F, new Dilation(0.6F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData bone62 = bone61.addChild("bone62", ModelPartBuilder.create().uv(138, 0).cuboid(0.3442F, -5.5F, -4.4756F, 4.0F, 12.0F, 6.0F, new Dilation(0.5F))
                .uv(75, 145).cuboid(0.7442F, -5.5F, -4.4756F, 4.0F, 12.0F, 6.0F, new Dilation(0.4F))
                .uv(141, 102).cuboid(-0.0558F, -5.5F, -4.4756F, 4.0F, 12.0F, 6.0F, new Dilation(0.6F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData bone63 = bone62.addChild("bone63", ModelPartBuilder.create().uv(138, 19).cuboid(1.0481F, -6.5F, -4.4756F, 5.0F, 10.0F, 6.0F, new Dilation(0.5F))
                .uv(21, 141).cuboid(1.2481F, -6.5F, -4.4756F, 5.0F, 10.0F, 6.0F, new Dilation(0.4F))
                .uv(138, 36).cuboid(0.6481F, -6.5F, -4.4756F, 5.0F, 10.0F, 6.0F, new Dilation(0.6F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData bone64 = rotor4.addChild("bone64", ModelPartBuilder.create().uv(44, 150).cuboid(-6.5311F, 21.5F, -7.8122F, 7.0F, 8.0F, 5.0F, new Dilation(0.5F))
                .uv(153, 67).cuboid(-6.2311F, 21.5F, -7.8122F, 7.0F, 8.0F, 5.0F, new Dilation(0.4F))
                .uv(153, 53).cuboid(-6.7311F, 21.5F, -7.8122F, 7.0F, 8.0F, 5.0F, new Dilation(0.6F))
                .uv(200, 187).cuboid(-8.1913F, 21.5F, -7.8122F, 2.0F, 8.0F, 5.0F, new Dilation(0.5F))
                .uv(201, 26).cuboid(-7.7913F, 21.5F, -7.8122F, 2.0F, 8.0F, 5.0F, new Dilation(0.6F))
                .uv(76, 201).cuboid(-8.3913F, 21.5F, -7.8122F, 2.0F, 8.0F, 5.0F, new Dilation(0.4F)), ModelTransform.pivot(4.0234F, -40.25F, 5.3122F));

        ModelPartData cube_r279 = bone64.addChild("cube_r279", ModelPartBuilder.create().uv(36, 199).cuboid(-6.6F, 1.0F, 3.5F, 3.0F, 8.0F, 5.0F, new Dilation(0.6F))
                .uv(199, 46).cuboid(-7.3F, 1.0F, 3.5F, 3.0F, 8.0F, 5.0F, new Dilation(0.4F))
                .uv(199, 0).cuboid(-7.0F, 1.0F, 3.5F, 3.0F, 8.0F, 5.0F, new Dilation(0.5F)), ModelTransform.of(0.0F, 20.5F, 0.0F, 0.0F, -2.0944F, 0.0F));

        ModelPartData cube_r280 = bone64.addChild("cube_r280", ModelPartBuilder.create().uv(19, 190).cuboid(-2.4F, 1.0F, 3.5F, 3.0F, 8.0F, 5.0F, new Dilation(0.6F))
                .uv(198, 125).cuboid(-2.0F, 1.0F, 3.5F, 3.0F, 8.0F, 5.0F, new Dilation(0.5F)), ModelTransform.of(-0.3301F, 20.5F, 0.5718F, 0.0F, -2.0944F, 0.0F));

        ModelPartData cube_r281 = bone64.addChild("cube_r281", ModelPartBuilder.create().uv(139, 199).cuboid(-1.4F, -0.5F, -2.5F, 3.0F, 8.0F, 5.0F, new Dilation(0.4F)), ModelTransform.of(-5.3763F, 22.0F, -2.688F, 0.0F, -2.0944F, 0.0F));

        ModelPartData cube_r282 = bone64.addChild("cube_r282", ModelPartBuilder.create().uv(199, 166).cuboid(-9.2F, 1.0F, -2.5F, 3.0F, 8.0F, 5.0F, new Dilation(0.4F))
                .uv(166, 191).cuboid(-8.6F, 1.0F, -2.5F, 3.0F, 8.0F, 5.0F, new Dilation(0.6F))
                .uv(198, 111).cuboid(-9.0F, 1.0F, -2.5F, 3.0F, 8.0F, 5.0F, new Dilation(0.5F)), ModelTransform.of(-1.5263F, 20.5F, -1.2679F, 0.0F, -1.0472F, 0.0F));

        ModelPartData cube_r283 = bone64.addChild("cube_r283", ModelPartBuilder.create().uv(199, 60).cuboid(-1.7F, 1.1F, -2.5F, 3.0F, 8.0F, 5.0F, new Dilation(0.4F))
                .uv(122, 199).cuboid(-2.4F, 1.0F, -2.5F, 3.0F, 8.0F, 5.0F, new Dilation(0.6F))
                .uv(0, 198).cuboid(-2.0F, 1.0F, -2.5F, 3.0F, 8.0F, 5.0F, new Dilation(0.5F)), ModelTransform.of(-2.1962F, 20.5F, -2.4282F, 0.0F, -1.0472F, 0.0F));
        return TexturedModelData.of(modelData, 256, 256);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
        console.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
    }

    @Override
    public void renderWithAnimations(ConsoleBlockEntity console, ClientTardis tardis, ModelPart root, MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float pAlpha) {
        matrices.push();
        matrices.translate(0.5f, -1.5f, -0.5f);

        // Throttle Control
        ModelPart throttle = this.console.getChild("pannel2").getChild("pillars2").getChild("pillars3").getChild("leveer2");

        throttle.pitch = throttle.pitch - (tardis.travel().speed() / (float) tardis.travel().maxSpeed().get());

        // Handbrake Control and Lights
        ModelPart handbrake = this.console.getChild("pannel2").getChild("pillars2").getChild("pillars3").getChild("leveer");
        handbrake.pitch = tardis.travel().handbrake() ? handbrake.pitch + 1 : handbrake.pitch;

        // @TODO MONSTER THE ONE ON THE LEFT IS THE POWER NOT THE RIGHT SMH
        // Power Switch and Lights
        ModelPart power = this.console.getChild("pannel3").getChild("rolly");
        power.roll = tardis.fuel().hasPower() ? power.roll : power.roll - 1.55f;

        // Anti Gravity Control
        ModelPart antigravs = this.console.getChild("pannel7").getChild("panels7").getChild("cube1");
        antigravs.yaw = tardis.travel().antigravs().get() ? antigravs.yaw - 1.58f : antigravs.yaw;

        // Increment Control
        ModelPart increment = this.console.getChild("pannel7").getChild("panels7").getChild("bone4");
        increment.roll = increment.roll - (IncrementManager.increment(tardis) / 1000f);

//        ModelPart shield = this.console.getChild("panel1").getChild("controls").getChild("faucettaps2");
//        shield.yaw = tardis.shields().shielded().get()
//                ? shield.yaw - 1.58f
//                : shield.yaw;
//
//        // Door Locking Mechanism Control
//        ModelPart doorlock = this.console.getChild("panel1").getChild("controls").getChild("smalllockernob")
//                .getChild("pivot3");
//        doorlock.yaw = tardis.door().locked() ? doorlock.yaw + 0.5f : doorlock.yaw;
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
        // Fuel Gauge
        ModelPart fuelGauge = this.console.getChild("pannel3").getChild("panels3").getChild("button");
        fuelGauge.pivotX = fuelGauge.pivotX + 0.25f;
        fuelGauge.pivotZ = fuelGauge.pivotZ + 0.25f;
        fuelGauge.yaw = (float) (((tardis.getFuel() / FuelHandler.TARDIS_MAX_FUEL) * 2) - 1);

//
//        // Ground Search Control
//        ModelPart groundSearch = this.console.getChild("panel1").getChild("controls").getChild("smallswitch");
//        groundSearch.pitch = tardis.travel().horizontalSearch().get()
//                ? groundSearch.pitch + 1f
//                : groundSearch.pitch - 0.75f; // FIXME use TravelHandler#horizontalSearch/#verticalSearch
//
        // Direction Control
        ModelPart direction = this.console.getChild("pannel7").getChild("pillars56").getChild("pillars57").getChild("spinnio");
        direction.roll = direction.roll + tardis.travel().destination().getRotation();
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
//        // Refuel Light
//        ModelPart refuelLight = this.console.getChild("panel4").getChild("yellow6");
//        refuelLight.pivotY = tardis.isRefueling() ? refuelLight.pivotY : refuelLight.pivotY + 1;
//
//        // Fast Return Control
//        // @TODO Loqor you need to make a toggleable thing for the fast return to be
//        // able to do
//        // something for the switch
//        ModelPart fastReturnCover = this.console.getChild("panel4").getChild("controls4").getChild("tinyswitchcover");
//        ModelPart fastReturnLever = this.console.getChild("panel4").getChild("controls4").getChild("tinyswitch");


        super.renderWithAnimations(console, tardis, root, matrices, vertices, light, overlay, red, green, blue, pAlpha);
        matrices.pop();
    }

    @Override
    public Animation getAnimationForState(TravelHandlerBase.State state) {
        return switch (state) {
            default -> CrystallineAnimations.CRYSTALLINE_FLIGHT;
            case LANDED -> CrystallineAnimations.CRYSTALLINE_IDLE;
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
        matrices.translate(0.5, 0.75, 0.5);
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180f));
        matrices.scale(0.005f, 0.005f, 0.005f);
        matrices.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(-30f));
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
        matrices.translate(0.5, 0.75, 0.5);
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180f));
        matrices.scale(0.005f, 0.005f, 0.005f);
        matrices.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(150f));
        matrices.translate(-240f, -240, -5f);
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
        matrices.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(150f));
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-20.5f));
        String progressText = tardis.travel().getState() == TravelHandlerBase.State.LANDED
                ? "0%"
                : tardis.travel().getDurationAsPercentage() + "%";
        matrices.translate(0, -38, -52);
        /*renderer.drawWithOutline(Text.of("⏳").asOrderedText(), 0, 0, 0x00FF0F, 0x000000,
                matrices.peek().getPositionMatrix(), vertexConsumers, 0xF000F0);*/
        matrices.translate(0 - entity.getWorld().random.nextFloat() * 0.4, 0 + entity.getWorld().random.nextFloat() * 0.4, 0 - entity.getWorld().random.nextFloat() * 0.4);
        renderer.drawWithOutline(Text.of(progressText).asOrderedText(), 0 - renderer.getWidth(progressText) / 2, 0, 0xffffff, 0x03cffc,
                matrices.peek().getPositionMatrix(), vertexConsumers, 0xF000F0);
        matrices.pop();
    }
}