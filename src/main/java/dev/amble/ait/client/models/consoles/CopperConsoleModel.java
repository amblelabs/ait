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

import dev.amble.ait.client.animation.console.copper.CopperAnimations;
import dev.amble.ait.client.tardis.ClientTardis;
import dev.amble.ait.core.blockentities.ConsoleBlockEntity;
import dev.amble.ait.core.tardis.Tardis;
import dev.amble.ait.core.tardis.control.impl.DirectionControl;
import dev.amble.ait.core.tardis.handler.FuelHandler;
import dev.amble.ait.core.tardis.handler.travel.TravelHandler;
import dev.amble.ait.core.tardis.handler.travel.TravelHandlerBase;
import dev.amble.ait.core.util.WorldUtil;

public class CopperConsoleModel extends SimpleConsoleModel {
    private final ModelPart copper;
    public CopperConsoleModel(ModelPart root) {
        this.copper = root.getChild("copper");
    }
    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData copper = modelPartData.addChild("copper", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 22.0F, 0.0F));

        ModelPartData desktop = copper.addChild("desktop", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData rim = desktop.addChild("rim", ModelPartBuilder.create().uv(51, 50).cuboid(18.0F, -5.0F, -8.0F, 2.0F, 5.0F, 16.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -8.0F, 0.0F));

        ModelPartData panels = desktop.addChild("panels", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 1.0F, 0.0F));

        ModelPartData rot = panels.addChild("rot", ModelPartBuilder.create().uv(28, 30).cuboid(-10.0F, 0.0F, -7.0F, 10.0F, 0.0F, 14.0F, new Dilation(0.0F))
                .uv(28, 0).cuboid(-10.0F, -0.2F, -7.0F, 10.0F, 0.0F, 14.0F, new Dilation(0.0F))
                .uv(28, 15).cuboid(-9.5F, -0.6F, -7.0F, 10.0F, 0.0F, 14.0F, new Dilation(0.0F)), ModelTransform.of(19.25F, -14.0F, 0.0F, 0.0F, 0.0F, 0.5672F));

        ModelPartData desktop2 = desktop.addChild("desktop2", ModelPartBuilder.create(), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData rim2 = desktop2.addChild("rim2", ModelPartBuilder.create().uv(51, 50).cuboid(18.0F, -5.0F, -8.0F, 2.0F, 5.0F, 16.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -8.0F, 0.0F));

        ModelPartData panels8 = desktop2.addChild("panels8", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 1.0F, 0.0F));

        ModelPartData rot2 = panels8.addChild("rot2", ModelPartBuilder.create().uv(28, 30).cuboid(-10.0F, 0.0F, -7.0F, 10.0F, 0.0F, 14.0F, new Dilation(0.0F))
                .uv(28, 0).cuboid(-10.0F, -0.2F, -7.0F, 10.0F, 0.0F, 14.0F, new Dilation(0.0F))
                .uv(28, 15).cuboid(-9.5F, -0.6F, -7.0F, 10.0F, 0.0F, 14.0F, new Dilation(0.0F)), ModelTransform.of(19.25F, -14.0F, 0.0F, 0.0F, 0.0F, 0.5672F));

        ModelPartData desktop3 = desktop2.addChild("desktop3", ModelPartBuilder.create(), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData rim3 = desktop3.addChild("rim3", ModelPartBuilder.create().uv(51, 50).cuboid(18.0F, -5.0F, -8.0F, 2.0F, 5.0F, 16.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -8.0F, 0.0F));

        ModelPartData panels9 = desktop3.addChild("panels9", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 1.0F, 0.0F));

        ModelPartData rot3 = panels9.addChild("rot3", ModelPartBuilder.create().uv(28, 30).cuboid(-10.0F, 0.0F, -7.0F, 10.0F, 0.0F, 14.0F, new Dilation(0.0F))
                .uv(28, 0).cuboid(-10.0F, -0.2F, -7.0F, 10.0F, 0.0F, 14.0F, new Dilation(0.0F))
                .uv(28, 15).cuboid(-9.5F, -0.6F, -7.0F, 10.0F, 0.0F, 14.0F, new Dilation(0.0F)), ModelTransform.of(19.25F, -14.0F, 0.0F, 0.0F, 0.0F, 0.5672F));

        ModelPartData cables4 = rot3.addChild("cables4", ModelPartBuilder.create().uv(0, 120).cuboid(-0.25F, 0.5F, -6.5F, 2.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(77, 12).cuboid(-1.5F, 1.0F, 5.25F, 4.0F, 0.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(1.5351F, 7.9126F, 0.0F, 0.0F, 0.0F, 2.3562F));

        ModelPartData cube_r1 = cables4.addChild("cube_r1", ModelPartBuilder.create().uv(100, 96).cuboid(-1.0F, 0.0F, 0.0F, 3.0F, 0.0F, 6.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, -0.1745F, 0.0F, 0.0F));

        ModelPartData cube_r2 = cables4.addChild("cube_r2", ModelPartBuilder.create().uv(101, 33).cuboid(-1.0F, 0.0F, -6.0F, 3.0F, 0.0F, 6.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.1745F, 0.0F, 0.0F));

        ModelPartData desktop4 = desktop3.addChild("desktop4", ModelPartBuilder.create(), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData rim4 = desktop4.addChild("rim4", ModelPartBuilder.create().uv(51, 50).cuboid(18.0F, -5.0F, -8.0F, 2.0F, 5.0F, 16.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -8.0F, 0.0F));

        ModelPartData panels10 = desktop4.addChild("panels10", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 1.0F, 0.0F));

        ModelPartData rot4 = panels10.addChild("rot4", ModelPartBuilder.create().uv(28, 30).cuboid(-10.0F, 0.0F, -7.0F, 10.0F, 0.0F, 14.0F, new Dilation(0.0F))
                .uv(28, 0).cuboid(-10.0F, -0.2F, -7.0F, 10.0F, 0.0F, 14.0F, new Dilation(0.0F))
                .uv(28, 15).cuboid(-9.5F, -0.6F, -7.0F, 10.0F, 0.0F, 14.0F, new Dilation(0.0F)), ModelTransform.of(19.25F, -14.0F, 0.0F, 0.0F, 0.0F, 0.5672F));

        ModelPartData bone123 = rot4.addChild("bone123", ModelPartBuilder.create(), ModelTransform.of(-4.0F, 0.0F, 6.4F, 0.0F, -0.48F, 0.0F));

        ModelPartData cube_r3 = bone123.addChild("cube_r3", ModelPartBuilder.create().uv(156, 116).cuboid(-3.8F, -1.9F, -1.0F, 3.0F, 2.0F, 2.0F, new Dilation(-0.4001F)), ModelTransform.of(-4.583F, -0.5072F, -0.6219F, 0.0F, 0.48F, 0.0436F));

        ModelPartData cube_r4 = bone123.addChild("cube_r4", ModelPartBuilder.create().uv(59, 232).cuboid(-4.8F, -1.9F, -1.0F, 4.0F, 2.0F, 2.0F, new Dilation(-0.4F))
                .uv(218, 143).cuboid(-1.5F, -1.9F, -1.0F, 3.0F, 2.0F, 2.0F, new Dilation(-0.1F)), ModelTransform.of(-1.5274F, -0.3737F, 0.0F, 0.0F, 0.0F, 0.0436F));

        ModelPartData cube_r5 = bone123.addChild("cube_r5", ModelPartBuilder.create().uv(191, 140).cuboid(-1.5F, -1.9F, -1.0F, 3.0F, 2.0F, 2.0F, new Dilation(-0.4F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.4363F));

        ModelPartData bone125 = bone123.addChild("bone125", ModelPartBuilder.create(), ModelTransform.of(-4.583F, -0.5072F, -0.6219F, 0.0F, 0.48F, 0.0436F));

        ModelPartData cube_r6 = bone125.addChild("cube_r6", ModelPartBuilder.create().uv(165, 4).cuboid(-13.8F, -1.9F, -1.0F, 13.0F, 2.0F, 2.0F, new Dilation(-0.4F)), ModelTransform.of(-29.4929F, -38.3724F, -1.3642F, 0.0F, 0.0F, 0.9599F));

        ModelPartData cube_r7 = bone125.addChild("cube_r7", ModelPartBuilder.create().uv(165, 4).cuboid(-33.8F, -1.9F, -1.0F, 33.0F, 2.0F, 2.0F, new Dilation(-0.4F)), ModelTransform.of(-13.5515F, -10.5405F, -1.3642F, 0.0F, 0.0F, 1.0472F));

        ModelPartData cube_r8 = bone125.addChild("cube_r8", ModelPartBuilder.create().uv(213, 69).cuboid(-5.8F, -1.9F, -1.0F, 5.0F, 2.0F, 2.0F, new Dilation(-0.4001F)), ModelTransform.of(-12.4151F, -6.7424F, -1.3642F, 0.0F, 0.0F, 1.2217F));

        ModelPartData cube_r9 = bone125.addChild("cube_r9", ModelPartBuilder.create().uv(222, 33).cuboid(-5.8F, -1.9F, -1.0F, 5.0F, 2.0F, 2.0F, new Dilation(-0.4F)), ModelTransform.of(-8.5998F, -4.2717F, -1.3642F, 0.0F, 0.0F, 0.6981F));

        ModelPartData cube_r10 = bone125.addChild("cube_r10", ModelPartBuilder.create().uv(209, 12).cuboid(-5.8F, -1.9F, -1.0F, 5.0F, 2.0F, 2.0F, new Dilation(-0.3998F)), ModelTransform.of(-5.7891F, -1.9133F, 0.4465F, 0.0F, -0.3491F, 0.6981F));

        ModelPartData cube_r11 = bone125.addChild("cube_r11", ModelPartBuilder.create().uv(144, 216).cuboid(-5.8F, -1.9F, -1.0F, 5.0F, 2.0F, 2.0F, new Dilation(-0.3999F)), ModelTransform.of(-2.6735F, 0.7011F, -0.0001F, 0.0F, 0.0F, 0.6981F));

        ModelPartData bone124 = bone123.addChild("bone124", ModelPartBuilder.create(), ModelTransform.of(-8.0F, 0.0F, 0.0F, 0.0F, 0.48F, 0.0F));

        ModelPartData desktop5 = desktop4.addChild("desktop5", ModelPartBuilder.create(), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData rim5 = desktop5.addChild("rim5", ModelPartBuilder.create().uv(51, 50).cuboid(18.0F, -5.0F, -8.0F, 2.0F, 5.0F, 16.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -8.0F, 0.0F));

        ModelPartData panels11 = desktop5.addChild("panels11", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 1.0F, 0.0F));

        ModelPartData rot5 = panels11.addChild("rot5", ModelPartBuilder.create().uv(28, 30).cuboid(-10.0F, 0.0F, -7.0F, 10.0F, 0.0F, 14.0F, new Dilation(0.0F))
                .uv(28, 0).cuboid(-10.0F, -0.2F, -7.0F, 10.0F, 0.0F, 14.0F, new Dilation(0.0F))
                .uv(28, 15).cuboid(-9.5F, -0.6F, -7.0F, 10.0F, 0.0F, 14.0F, new Dilation(0.0F))
                .uv(129, 172).cuboid(-11.6F, -0.7F, -4.2F, 2.0F, 0.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(19.25F, -14.0F, 0.0F, 0.0F, 0.0F, 0.5672F));

        ModelPartData cube_r12 = rot5.addChild("cube_r12", ModelPartBuilder.create().uv(117, 189).cuboid(-15.8F, -0.65F, 0.725F, 3.0F, 0.0F, 3.0F, new Dilation(0.0F))
                .uv(122, 196).cuboid(-15.3F, -0.95F, 2.5F, 2.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(150, 180).cuboid(-14.7F, -0.6F, -0.275F, 6.0F, 0.0F, 4.0F, new Dilation(0.0F))
                .uv(137, 167).cuboid(-11.8F, -0.55F, -4.95F, 3.0F, 0.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(-5.4842F, 5.6504F, 0.75F, 0.0F, 0.0F, 0.7418F));

        ModelPartData cube_r13 = rot5.addChild("cube_r13", ModelPartBuilder.create().uv(122, 137).cuboid(-8.8F, -0.95F, -2.25F, 5.0F, 0.0F, 3.0F, new Dilation(0.0F))
                .uv(124, 149).cuboid(-7.8F, -0.35F, -4.85F, 1.0F, 0.0F, 1.0F, new Dilation(0.0F))
                .uv(124, 149).cuboid(-7.8F, -0.35F, -3.55F, 1.0F, 0.0F, 1.0F, new Dilation(0.0F))
                .uv(118, 146).cuboid(-6.5F, -0.35F, -3.55F, 2.0F, 0.0F, 1.0F, new Dilation(0.0F))
                .uv(118, 146).cuboid(-6.5F, -0.35F, -4.85F, 2.0F, 0.0F, 1.0F, new Dilation(0.0F))
                .uv(124, 143).cuboid(-7.8F, -0.25F, -3.55F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(118, 140).cuboid(-6.5F, -0.25F, -3.55F, 2.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(118, 140).cuboid(-6.5F, -0.25F, -4.85F, 2.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(124, 143).cuboid(-7.8F, -0.25F, -4.85F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(122, 131).cuboid(-8.8F, -0.85F, -2.25F, 5.0F, 1.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(-9.4F, 1.25F, 0.75F, 0.0F, 0.0F, 0.7418F));

        ModelPartData bone120 = rot5.addChild("bone120", ModelPartBuilder.create(), ModelTransform.pivot(-5.4842F, 5.6504F, 0.75F));

        ModelPartData Cassette = rot5.addChild("Cassette", ModelPartBuilder.create().uv(92, 138).cuboid(-0.5F, 6.0F, -5.0F, 3.0F, 2.0F, 5.0F, new Dilation(0.0F))
                .uv(91, 131).cuboid(0.0F, 6.5F, -4.5F, 2.0F, 2.0F, 4.0F, new Dilation(0.0F))
                .uv(105, 133).cuboid(0.0F, 6.4F, -4.5F, 2.0F, 0.0F, 2.0F, new Dilation(0.0F))
                .uv(105, 133).cuboid(0.0F, 6.4F, -2.5F, 2.0F, 0.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(-7.3F, -8.6F, 2.5F));

        ModelPartData cube_r14 = Cassette.addChild("cube_r14", ModelPartBuilder.create().uv(98, 147).cuboid(-0.5F, 0.0F, -0.5F, 1.0F, 0.0F, 1.0F, new Dilation(0.0F))
                .uv(98, 147).cuboid(-0.5F, 0.0F, -5.5F, 1.0F, 0.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 7.0F, 0.0F, 0.0F, 0.0F, 0.7854F));

        ModelPartData cube_r15 = Cassette.addChild("cube_r15", ModelPartBuilder.create().uv(98, 147).cuboid(-0.5F, 0.0F, -0.5F, 1.0F, 0.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(2.0F, 6.5F, 0.0F, 0.0F, 0.0F, 0.7854F));

        ModelPartData cube_r16 = Cassette.addChild("cube_r16", ModelPartBuilder.create().uv(98, 147).cuboid(-0.5F, 0.0F, -0.5F, 1.0F, 0.0F, 1.0F, new Dilation(0.0F))
                .uv(98, 147).cuboid(-0.5F, 0.0F, -5.5F, 1.0F, 0.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(2.0F, 7.0F, 0.0F, 0.0F, 0.0F, 2.2689F));

        ModelPartData cube_r17 = Cassette.addChild("cube_r17", ModelPartBuilder.create().uv(98, 147).cuboid(-0.5F, 0.0F, -0.5F, 1.0F, 0.0F, 1.0F, new Dilation(0.0F))
                .uv(98, 147).cuboid(-0.5F, 0.0F, -5.5F, 1.0F, 0.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(1.0F, 7.0F, 0.0F, 0.0F, 0.0F, 2.2689F));

        ModelPartData cube_r18 = Cassette.addChild("cube_r18", ModelPartBuilder.create().uv(98, 147).cuboid(-0.5F, 0.0F, -0.5F, 1.0F, 0.0F, 1.0F, new Dilation(0.0F))
                .uv(98, 147).cuboid(-0.5F, 0.0F, -5.5F, 1.0F, 0.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(1.0F, 7.0F, 0.0F, 0.0F, 0.0F, 0.7854F));

        ModelPartData cube_r19 = Cassette.addChild("cube_r19", ModelPartBuilder.create().uv(98, 147).cuboid(-0.5F, 0.0F, -0.5F, 1.0F, 0.0F, 1.0F, new Dilation(0.0F))
                .uv(98, 147).cuboid(-0.5F, 0.0F, -5.5F, 1.0F, 0.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 7.0F, 0.0F, 0.0F, 0.0F, 2.2689F));

        ModelPartData cube_r20 = Cassette.addChild("cube_r20", ModelPartBuilder.create().uv(98, 147).cuboid(-0.5F, 0.0F, -0.5F, 1.0F, 0.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(2.0F, 7.0F, -5.0F, 0.0F, 0.0F, 0.7854F));

        ModelPartData bone118 = Cassette.addChild("bone118", ModelPartBuilder.create(), ModelTransform.pivot(7.3F, 7.85F, -1.75F));

        ModelPartData cube_r21 = bone118.addChild("cube_r21", ModelPartBuilder.create().uv(135, 152).cuboid(-10.8F, -0.95F, -5.25F, 2.0F, 0.0F, 3.0F, new Dilation(0.0F))
                .uv(135, 148).cuboid(-10.8F, -0.85F, -5.25F, 3.0F, 1.0F, 3.0F, new Dilation(0.0F))
                .uv(135, 158).cuboid(-10.8F, -0.45F, -2.25F, 2.0F, 0.0F, 1.0F, new Dilation(0.0F))
                .uv(136, 161).cuboid(-10.8F, -0.35F, -2.25F, 2.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -0.2618F, 0.0F));

        ModelPartData bone119 = bone118.addChild("bone119", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData cube_r22 = bone119.addChild("cube_r22", ModelPartBuilder.create().uv(129, 158).cuboid(-0.5F, 0.0F, -1.0F, 1.0F, 0.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(-6.0307F, -0.1284F, -5.2912F, -0.2618F, 0.0F, 1.5708F));

        ModelPartData cube_r23 = bone119.addChild("cube_r23", ModelPartBuilder.create().uv(129, 160).cuboid(-0.5F, 0.0F, -1.0F, 1.0F, 0.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(-6.4679F, -0.8388F, -5.4083F, -0.1128F, -0.2368F, 0.4498F));

        ModelPartData cube_r24 = bone119.addChild("cube_r24", ModelPartBuilder.create().uv(129, 156).cuboid(-0.5F, 0.0F, -1.0F, 1.0F, 0.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(-7.3881F, -1.05F, -5.6549F, 0.0F, -0.2618F, 0.0F));

        ModelPartData bone117 = Cassette.addChild("bone117", ModelPartBuilder.create().uv(110, 160).cuboid(-3.6F, -0.05F, -4.55F, 3.0F, 0.0F, 3.0F, new Dilation(0.001F))
                .uv(123, 169).cuboid(-4.1F, -0.35F, -4.05F, 3.0F, 0.0F, 3.0F, new Dilation(0.0F))
                .uv(127, 166).cuboid(-3.1F, -1.675F, -4.05F, 2.0F, 1.0F, 2.0F, new Dilation(0.0F))
                .uv(131, 170).cuboid(-2.6F, -2.175F, -3.55F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(124, 160).cuboid(-2.6F, -1.575F, -3.55F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F))
                .uv(118, 165).cuboid(-3.85F, -0.65F, -4.3F, 3.0F, 0.0F, 3.0F, new Dilation(0.0F)), ModelTransform.pivot(7.3F, 7.85F, -2.15F));

        ModelPartData typewriter = rot5.addChild("typewriter", ModelPartBuilder.create().uv(63, 146).cuboid(-4.5F, -2.6F, -1.0F, 5.0F, 3.0F, 6.0F, new Dilation(0.001F))
                .uv(47, 146).cuboid(-4.5F, -3.6F, -1.0F, 2.0F, 2.0F, 6.0F, new Dilation(0.0F))
                .uv(55, 164).cuboid(-3.5F, -3.6F, -1.0F, 1.0F, 1.0F, 6.0F, new Dilation(0.0F))
                .uv(61, 134).cuboid(0.5F, -1.6F, -1.0F, 2.0F, 2.0F, 6.0F, new Dilation(0.0F))
                .uv(63, 158).cuboid(0.5F, -0.7F, -1.0F, 2.0F, 0.0F, 6.0F, new Dilation(0.0F))
                .uv(63, 158).cuboid(0.5F, -1.2F, -1.0F, 2.0F, 0.0F, 6.0F, new Dilation(0.0F))
                .uv(63, 158).cuboid(0.5F, -1.1F, -0.6F, 2.0F, 0.0F, 6.0F, new Dilation(0.0F))
                .uv(47, 158).cuboid(-4.5F, -1.7F, -1.0F, 7.0F, 0.0F, 6.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -0.75F, 0.75F));

        ModelPartData bone116 = rot5.addChild("bone116", ModelPartBuilder.create().uv(72, 125).cuboid(-8.15F, -1.5F, 4.7F, 1.0F, 1.0F, 1.0F, new Dilation(-0.3F))
                .uv(72, 125).cuboid(-8.75F, -1.5F, 4.7F, 1.0F, 1.0F, 1.0F, new Dilation(-0.3F))
                .uv(72, 125).cuboid(-9.35F, -1.5F, 4.7F, 1.0F, 1.0F, 1.0F, new Dilation(-0.3F))
                .uv(72, 125).cuboid(-9.35F, -1.5F, 5.8F, 1.0F, 1.0F, 1.0F, new Dilation(-0.3F))
                .uv(72, 125).cuboid(-8.75F, -1.5F, 5.8F, 1.0F, 1.0F, 1.0F, new Dilation(-0.3F))
                .uv(72, 125).cuboid(-8.15F, -1.5F, 5.8F, 1.0F, 1.0F, 1.0F, new Dilation(-0.3F))
                .uv(63, 27).cuboid(-9.25F, -0.8F, 4.7F, 2.0F, 0.0F, 1.0F, new Dilation(0.0F))
                .uv(63, 27).cuboid(-9.25F, -0.8F, 5.8F, 2.0F, 0.0F, 1.0F, new Dilation(0.0F))
                .uv(28, 156).cuboid(-0.85F, -0.9F, 5.3F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(3.0F, -0.75F, 3.4F, -0.0227F, -0.4795F, -0.0492F));

        ModelPartData cube_r25 = bone116.addChild("cube_r25", ModelPartBuilder.create().uv(175, 85).cuboid(0.5F, -1.85F, -0.8F, 2.0F, 0.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-4.25F, 0.925F, 6.35F, 0.0087F, 0.0F, 0.0F));

        ModelPartData cube_r26 = bone116.addChild("cube_r26", ModelPartBuilder.create().uv(49, 125).cuboid(1.0F, -1.65F, -0.3F, 1.0F, 1.0F, 1.0F, new Dilation(-0.1F)), ModelTransform.of(-7.75F, 0.45F, 5.1F, 0.0087F, 0.0F, 0.0F));

        ModelPartData cube_r27 = bone116.addChild("cube_r27", ModelPartBuilder.create().uv(40, 130).cuboid(1.0F, -1.15F, -0.3F, 1.0F, 0.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-6.75F, 0.45F, 6.1F, 0.0087F, 0.0F, 0.0F));

        ModelPartData cube_r28 = bone116.addChild("cube_r28", ModelPartBuilder.create().uv(175, 63).cuboid(0.0F, -1.65F, -1.3F, 3.0F, 1.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(-4.25F, 0.75F, 6.1F, 0.0087F, 0.0F, 0.0F));

        ModelPartData desktop6 = desktop5.addChild("desktop6", ModelPartBuilder.create(), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData rim6 = desktop6.addChild("rim6", ModelPartBuilder.create().uv(51, 50).cuboid(18.0F, -5.0F, -8.0F, 2.0F, 5.0F, 16.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -8.0F, 0.0F));

        ModelPartData panels12 = desktop6.addChild("panels12", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 1.0F, 0.0F));

        ModelPartData rot6 = panels12.addChild("rot6", ModelPartBuilder.create().uv(28, 30).cuboid(-10.0F, 0.0F, -7.0F, 10.0F, 0.0F, 14.0F, new Dilation(0.0F))
                .uv(28, 0).cuboid(-10.0F, -0.2F, -7.0F, 10.0F, 0.0F, 14.0F, new Dilation(0.0F))
                .uv(28, 15).cuboid(-9.5F, -0.6F, -7.0F, 10.0F, 0.0F, 14.0F, new Dilation(0.0F))
                .uv(219, 150).cuboid(-4.2F, -0.725F, -6.1F, 5.0F, 0.0F, 11.0F, new Dilation(0.0F))
                .uv(219, 253).cuboid(-3.3F, -0.8F, 1.0F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(19.25F, -14.0F, 0.0F, 0.0F, 0.0F, 0.6109F));

        ModelPartData cube_r29 = rot6.addChild("cube_r29", ModelPartBuilder.create().uv(203, 201).cuboid(-1.0F, -1.1F, 0.2F, 2.0F, 0.0F, 1.0F, new Dilation(0.0F))
                .uv(203, 195).cuboid(-1.7F, -1.2F, -0.8F, 2.0F, 1.0F, 1.0F, new Dilation(-0.1F))
                .uv(203, 201).cuboid(-1.8F, -1.1F, -1.3F, 2.0F, 0.0F, 1.0F, new Dilation(0.0F))
                .uv(199, 215).cuboid(-5.3F, -3.6F, -0.3F, 3.0F, 3.0F, 2.0F, new Dilation(0.0F))
                .uv(201, 207).cuboid(-5.8F, -1.1F, -1.3F, 3.0F, 0.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 8.7F, 0.0F, -0.48F, -0.0873F));

        ModelPartData cube_r30 = rot6.addChild("cube_r30", ModelPartBuilder.create().uv(211, 200).cuboid(-1.5791F, -1.6171F, -0.5691F, 1.0F, 1.0F, 1.0F, new Dilation(-0.1F)), ModelTransform.of(-1.6026F, 0.6591F, 7.5787F, -0.3069F, -0.3751F, 0.6259F));

        ModelPartData cube_r31 = rot6.addChild("cube_r31", ModelPartBuilder.create().uv(221, 250).cuboid(-0.925F, -1.5F, 0.175F, 1.0F, 2.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(-3.05F, -1.3F, 1.5F, -3.1416F, 0.7854F, 3.1416F));

        ModelPartData cube_r32 = rot6.addChild("cube_r32", ModelPartBuilder.create().uv(221, 250).cuboid(-0.575F, -1.5F, 0.175F, 1.0F, 2.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(-3.05F, -1.3F, 1.5F, 0.0F, 0.7854F, 0.0F));

        ModelPartData cube_r33 = rot6.addChild("cube_r33", ModelPartBuilder.create().uv(204, 251).cuboid(-1.225F, -1.425F, 1.875F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-1.2F, -0.3F, 1.6F, 0.0F, -0.4363F, 0.0F));

        ModelPartData cube_r34 = rot6.addChild("cube_r34", ModelPartBuilder.create().uv(245, 176).cuboid(6.9F, -4.225F, -4.35F, 2.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(226, 160).cuboid(2.0F, -3.725F, -7.1F, 5.0F, 0.0F, 11.0F, new Dilation(0.0F)), ModelTransform.of(-3.3993F, -0.2272F, 1.0F, 0.0F, 0.0F, 0.9599F));

        ModelPartData cube_r35 = rot6.addChild("cube_r35", ModelPartBuilder.create().uv(211, 244).cuboid(-2.0F, -0.7F, -0.1F, 3.0F, 1.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(-1.2F, 0.0F, 1.0F, 0.0F, -0.4363F, 0.0F));

        ModelPartData dial4 = rot6.addChild("dial4", ModelPartBuilder.create().uv(67, 96).cuboid(0.05F, -1.25F, -1.25F, 0.0F, 2.0F, 2.0F, new Dilation(0.0F))
                .uv(123, 114).cuboid(-0.45F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(2.0868F, 0.8499F, 9.0F, 0.0F, 0.0F, -0.6109F));

        ModelPartData lever9 = rot6.addChild("lever9", ModelPartBuilder.create().uv(224, 125).cuboid(-1.0F, -2.0F, -1.5F, 4.0F, 2.0F, 3.0F, new Dilation(-0.29F))
                .uv(226, 118).cuboid(-0.6F, -1.75F, -1.1F, 1.0F, 0.0F, 1.0F, new Dilation(0.0F))
                .uv(233, 182).cuboid(0.0F, -2.0F, -1.2F, 2.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(224, 136).cuboid(-1.0F, -2.05F, 0.0F, 4.0F, 2.0F, 2.0F, new Dilation(-0.3F))
                .uv(224, 136).cuboid(-1.0F, -2.05F, -0.5F, 4.0F, 2.0F, 2.0F, new Dilation(-0.3F)), ModelTransform.of(-3.6555F, -0.2059F, -6.4786F, -0.0227F, 0.4795F, -0.0492F));

        ModelPartData bone130 = lever9.addChild("bone130", ModelPartBuilder.create().uv(158, 109).cuboid(-0.5F, -0.5F, -1.0F, 1.0F, 1.0F, 2.0F, new Dilation(-0.2F)), ModelTransform.pivot(2.0F, -2.0F, 0.6F));

        ModelPartData bone131 = lever9.addChild("bone131", ModelPartBuilder.create().uv(193, 234).cuboid(-0.5F, -2.1F, -0.5F, 1.0F, 2.0F, 1.0F, new Dilation(-0.3F))
                .uv(160, 126).cuboid(-0.5F, -2.6F, -1.0F, 1.0F, 1.0F, 2.0F, new Dilation(-0.2F)), ModelTransform.of(1.0F, -1.5F, -0.7F, 0.0F, 0.0F, 0.3927F));

        ModelPartData bone132 = rot6.addChild("bone132", ModelPartBuilder.create(), ModelTransform.of(0.0F, 0.3F, 0.0F, 0.0F, 0.48F, 0.0F));

        ModelPartData cube_r36 = bone132.addChild("cube_r36", ModelPartBuilder.create().uv(151, 44).cuboid(-0.5F, -1.2F, -0.5F, 1.0F, 0.0F, 1.0F, new Dilation(0.0F))
                .uv(150, 47).cuboid(-1.0F, -1.1F, -1.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F))
                .uv(142, 132).cuboid(-1.5F, -0.5F, -1.5F, 3.0F, 1.0F, 3.0F, new Dilation(-0.2F)), ModelTransform.of(-4.9F, -1.0F, -7.5F, 0.0F, 0.0F, -0.0436F));

        ModelPartData bone136 = rot6.addChild("bone136", ModelPartBuilder.create(), ModelTransform.pivot(-2.5F, -4.0F, 0.5F));

        ModelPartData bone138 = rot6.addChild("bone138", ModelPartBuilder.create(), ModelTransform.pivot(-2.4F, -4.0F, 0.3F));

        ModelPartData bone157 = bone138.addChild("bone157", ModelPartBuilder.create(), ModelTransform.pivot(1.8F, 2.0F, -2.9F));

        ModelPartData cube_r37 = bone157.addChild("cube_r37", ModelPartBuilder.create().uv(116, 228).cuboid(-0.8F, -2.0F, -0.9F, 1.0F, 4.0F, 1.0F, new Dilation(-0.2F)), ModelTransform.of(-0.825F, 1.225F, 0.025F, 0.0F, -1.5708F, 0.0F));

        ModelPartData bone142 = rot6.addChild("bone142", ModelPartBuilder.create().uv(245, 18).cuboid(-1.1125F, -0.4375F, -1.25F, 2.0F, 1.0F, 2.0F, new Dilation(-0.1F))
                .uv(244, 7).cuboid(-0.8625F, -0.3375F, -1.0F, 2.0F, 1.0F, 2.0F, new Dilation(0.0F))
                .uv(248, 12).cuboid(-0.8625F, -0.3875F, -1.0F, 2.0F, 1.0F, 2.0F, new Dilation(-0.1F)), ModelTransform.pivot(-5.3375F, -0.1625F, -1.45F));

        ModelPartData cube_r38 = bone142.addChild("cube_r38", ModelPartBuilder.create().uv(155, 0).cuboid(-0.75F, 0.0F, -0.25F, 1.0F, 0.0F, 1.0F, new Dilation(0.001F)), ModelTransform.of(0.0875F, -0.3375F, 0.0F, 0.0F, -1.5708F, 0.0F));

        ModelPartData bone140 = rot6.addChild("bone140", ModelPartBuilder.create().uv(156, 61).cuboid(-1.5F, -0.35F, -1.5F, 3.0F, 1.0F, 3.0F, new Dilation(0.0F)), ModelTransform.pivot(-5.7F, -0.15F, 1.4F));

        ModelPartData bone141 = rot6.addChild("bone141", ModelPartBuilder.create(), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -0.48F, 0.0F));

        ModelPartData cube_r39 = bone141.addChild("cube_r39", ModelPartBuilder.create().uv(99, 245).cuboid(-5.6F, -2.9F, 6.475F, 2.0F, 2.0F, 2.0F, new Dilation(-0.2F))
                .uv(169, 151).cuboid(-6.375F, -2.725F, 6.25F, 3.0F, 1.0F, 3.0F, new Dilation(0.0F))
                .uv(151, 73).cuboid(-5.6F, -1.875F, 6.475F, 2.0F, 0.0F, 2.0F, new Dilation(0.0F))
                .uv(151, 73).cuboid(-5.6F, -1.075F, 6.475F, 2.0F, 0.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, -0.0436F, -0.0008F, -0.0611F));

        ModelPartData cables3 = rot6.addChild("cables3", ModelPartBuilder.create().uv(0, 120).cuboid(-0.25F, 0.5F, -6.5F, 2.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(77, 12).cuboid(-1.5F, 1.0F, 5.25F, 4.0F, 0.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(1.75F, 8.25F, 0.0F, 0.0F, 0.0F, 2.3562F));

        ModelPartData cube_r40 = cables3.addChild("cube_r40", ModelPartBuilder.create().uv(100, 96).cuboid(-1.0F, 0.0F, 0.0F, 3.0F, 0.0F, 6.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, -0.1745F, 0.0F, 0.0F));

        ModelPartData cube_r41 = cables3.addChild("cube_r41", ModelPartBuilder.create().uv(101, 33).cuboid(-1.0F, 0.0F, -6.0F, 3.0F, 0.0F, 6.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.1745F, 0.0F, 0.0F));

        ModelPartData bone143 = rot6.addChild("bone143", ModelPartBuilder.create().uv(202, 188).cuboid(-1.0F, -2.0F, -1.475F, 2.0F, 0.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-1.4611F, 0.9F, 9.0941F, 0.0F, -1.4399F, 0.0F));

        ModelPartData cube_r42 = bone143.addChild("cube_r42", ModelPartBuilder.create().uv(202, 188).cuboid(-1.0F, 0.0F, -0.5F, 2.0F, 0.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-7.9367F, -1.1655F, 1.3586F, 2.9062F, 0.9173F, 1.3049F));

        ModelPartData cube_r43 = bone143.addChild("cube_r43", ModelPartBuilder.create().uv(202, 188).cuboid(1.0F, 0.0F, -0.5F, 2.0F, 0.0F, 1.0F, new Dilation(0.0F))
                .uv(202, 188).cuboid(-1.0F, 0.0F, -0.5F, 2.0F, 0.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-8.5333F, -2.371F, 4.8574F, 2.6122F, 1.2861F, 0.9815F));

        ModelPartData cube_r44 = bone143.addChild("cube_r44", ModelPartBuilder.create().uv(202, 188).cuboid(-1.0F, 0.0F, -0.5F, 2.0F, 0.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-7.7597F, -2.73F, 6.1633F, -1.8321F, 0.3535F, 3.0074F));

        ModelPartData cube_r45 = bone143.addChild("cube_r45", ModelPartBuilder.create().uv(202, 188).cuboid(-1.0F, 0.0F, -0.5F, 2.0F, 0.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-6.3154F, -2.454F, 6.811F, -1.5708F, 0.4363F, -2.618F));

        ModelPartData cube_r46 = bone143.addChild("cube_r46", ModelPartBuilder.create().uv(202, 188).cuboid(-1.0F, 0.0F, -0.5F, 2.0F, 0.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-4.6945F, -1.5182F, 6.9747F, -1.5708F, -0.2618F, -2.618F));

        ModelPartData cube_r47 = bone143.addChild("cube_r47", ModelPartBuilder.create().uv(202, 188).cuboid(2.5F, 0.0F, -0.5F, 2.0F, 0.0F, 1.0F, new Dilation(0.0F))
                .uv(202, 188).cuboid(0.5F, 0.0F, -0.5F, 2.0F, 0.0F, 1.0F, new Dilation(0.0F))
                .uv(202, 188).cuboid(-1.5F, 0.0F, -0.5F, 2.0F, 0.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-0.9852F, 0.6234F, 3.676F, -1.5708F, -0.7418F, -2.618F));

        ModelPartData cube_r48 = bone143.addChild("cube_r48", ModelPartBuilder.create().uv(202, 188).cuboid(-1.5F, 0.0F, -0.5F, 2.0F, 0.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.2216F, 1.3201F, 2.2537F, -1.5708F, -0.9599F, -2.618F));

        ModelPartData cube_r49 = bone143.addChild("cube_r49", ModelPartBuilder.create().uv(202, 188).cuboid(-1.5F, 0.0F, -0.5F, 2.0F, 0.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.9667F, 1.7503F, 0.525F, 0.0F, -1.5708F, 2.0944F));

        ModelPartData cube_r50 = bone143.addChild("cube_r50", ModelPartBuilder.create().uv(202, 188).cuboid(-1.0F, 0.0F, -0.5F, 2.0F, 0.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(1.7167F, 0.4512F, -0.975F, 0.0F, 0.0F, 2.0944F));

        ModelPartData cube_r51 = bone143.addChild("cube_r51", ModelPartBuilder.create().uv(202, 186).cuboid(-1.0F, -2.0F, -1.475F, 2.0F, 0.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0213F, 0.0105F, 0.0F, 0.0F, 0.0F, 0.9163F));

        ModelPartData bone133 = rot6.addChild("bone133", ModelPartBuilder.create().uv(191, 222).cuboid(-0.5F, 0.0F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-1.9F, -2.0F, -2.4F, 0.0F, 0.7854F, 0.0F));

        ModelPartData cube_r52 = bone133.addChild("cube_r52", ModelPartBuilder.create().uv(189, 224).cuboid(-0.5F, 0.0F, -1.5F, 1.0F, 0.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 1.5F, 0.25F, 0.0F, 1.5708F, 1.5708F));

        ModelPartData cube_r53 = bone133.addChild("cube_r53", ModelPartBuilder.create().uv(189, 224).cuboid(-0.5F, 0.0F, -1.5F, 1.0F, 0.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(-0.25F, 1.5F, 0.0F, -1.5708F, 0.0F, 0.0F));

        ModelPartData bone134 = rot6.addChild("bone134", ModelPartBuilder.create().uv(191, 222).cuboid(-0.5F, 0.0F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-2.9F, -2.0F, -3.4F, 0.0F, 0.7854F, 0.0F));

        ModelPartData cube_r54 = bone134.addChild("cube_r54", ModelPartBuilder.create().uv(189, 224).cuboid(-0.5F, 0.0F, -1.5F, 1.0F, 0.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 1.5F, 0.25F, 0.0F, 1.5708F, 1.5708F));

        ModelPartData cube_r55 = bone134.addChild("cube_r55", ModelPartBuilder.create().uv(189, 224).cuboid(-0.5F, 0.0F, -1.5F, 1.0F, 0.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(-0.25F, 1.5F, 0.0F, -1.5708F, 0.0F, 0.0F));

        ModelPartData bone135 = rot6.addChild("bone135", ModelPartBuilder.create().uv(191, 222).cuboid(-0.5F, 0.0F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-2.9F, -2.0F, -1.4F, 0.0F, 0.7854F, 0.0F));

        ModelPartData cube_r56 = bone135.addChild("cube_r56", ModelPartBuilder.create().uv(189, 224).cuboid(-0.5F, 0.0F, -1.5F, 1.0F, 0.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 1.5F, 0.25F, 0.0F, 1.5708F, 1.5708F));

        ModelPartData cube_r57 = bone135.addChild("cube_r57", ModelPartBuilder.create().uv(189, 224).cuboid(-0.5F, 0.0F, -1.5F, 1.0F, 0.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(-0.25F, 1.5F, 0.0F, -1.5708F, 0.0F, 0.0F));

        ModelPartData bone150 = rot6.addChild("bone150", ModelPartBuilder.create().uv(15, 218).cuboid(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(11, 206).cuboid(-0.5F, -3.3F, 0.3F, 1.0F, 1.0F, 1.0F, new Dilation(-0.1F)), ModelTransform.of(-13.8792F, -2.6779F, -1.5F, 0.0F, 0.0F, -0.6109F));

        ModelPartData bone151 = bone150.addChild("bone151", ModelPartBuilder.create().uv(14, 224).cuboid(-0.5F, -1.0F, 0.0F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-0.25F, -1.45F, 0.3F, -0.3054F, 0.0F, 0.0F));

        ModelPartData pillars = copper.addChild("pillars", ModelPartBuilder.create().uv(0, 63).cuboid(18.5F, -13.5F, -10.5F, 2.0F, 6.0F, 3.0F, new Dilation(0.0F))
                .uv(28, 30).cuboid(18.5F, -13.5F, 7.5F, 2.0F, 6.0F, 3.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData cube_r58 = pillars.addChild("cube_r58", ModelPartBuilder.create().uv(63, 0).cuboid(-8.0F, -2.0F, 1.0F, 9.0F, 2.0F, 9.0F, new Dilation(0.0F)), ModelTransform.of(8.0941F, -18.3532F, -5.5F, 0.0F, 0.0F, 1.309F));

        ModelPartData cube_r59 = pillars.addChild("cube_r59", ModelPartBuilder.create().uv(31, 66).cuboid(-1.0F, -2.0F, 1.0F, 5.0F, 7.0F, 9.0F, new Dilation(0.001F)), ModelTransform.of(8.0941F, -18.3532F, -5.5F, 0.0F, 0.0F, 0.5236F));

        ModelPartData cube_r60 = pillars.addChild("cube_r60", ModelPartBuilder.create().uv(28, 0).cuboid(7.5948F, -10.45F, -3.2638F, 3.0F, 10.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 2.45F, 0.0F, 0.0F, -0.48F, 0.0F));

        ModelPartData cube_r61 = pillars.addChild("cube_r61", ModelPartBuilder.create().uv(28, 15).cuboid(7.5948F, -10.45F, 0.2638F, 3.0F, 10.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 2.45F, 0.0F, 0.0F, 0.48F, 0.0F));

        ModelPartData cube_r62 = pillars.addChild("cube_r62", ModelPartBuilder.create().uv(0, 100).cuboid(-5.0F, -13.0F, -1.0F, 5.0F, 13.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(20.0014F, -13.538F, 8.366F, 0.4899F, -0.1932F, -1.1492F));

        ModelPartData cube_r63 = pillars.addChild("cube_r63", ModelPartBuilder.create().uv(63, 0).cuboid(-3.0F, -6.0F, -1.0F, 3.0F, 6.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(20.0383F, -7.5F, 8.387F, 0.0F, -0.48F, 0.0F));

        ModelPartData cube_r64 = pillars.addChild("cube_r64", ModelPartBuilder.create().uv(100, 103).cuboid(0.0F, -13.0F, 0.0F, 4.0F, 13.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(20.5F, -7.5F, 7.5F, 0.4819F, 0.0851F, -1.7445F));

        ModelPartData cube_r65 = pillars.addChild("cube_r65", ModelPartBuilder.create().uv(101, 71).cuboid(-5.0F, -13.0F, -1.0F, 5.0F, 13.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(20.5F, -13.5F, 10.5F, 0.4899F, -0.1932F, -1.1492F));

        ModelPartData cube_r66 = pillars.addChild("cube_r66", ModelPartBuilder.create().uv(51, 96).cuboid(0.0F, -13.0F, -1.0F, 4.0F, 13.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(20.5F, -7.5F, 10.5F, 0.4835F, 0.116F, -1.7282F));

        ModelPartData cube_r67 = pillars.addChild("cube_r67", ModelPartBuilder.create().uv(28, 45).cuboid(-1.0F, 0.0F, 0.0F, 1.0F, 6.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(10.5948F, 0.6969F, 1.8362F, 0.4784F, -0.0403F, -1.2752F));

        ModelPartData cube_r68 = pillars.addChild("cube_r68", ModelPartBuilder.create().uv(63, 30).cuboid(-1.0F, 0.0F, -3.0F, 1.0F, 6.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(10.5948F, 0.6969F, -1.8362F, -0.4784F, 0.0403F, -1.2752F));

        ModelPartData cube_r69 = pillars.addChild("cube_r69", ModelPartBuilder.create().uv(82, 101).cuboid(0.0F, -13.0F, -3.0F, 1.0F, 13.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(20.5F, -7.5F, 10.5F, 0.48F, 0.0F, -1.789F));

        ModelPartData cube_r70 = pillars.addChild("cube_r70", ModelPartBuilder.create().uv(106, 40).cuboid(0.0F, -13.0F, -1.0F, 4.0F, 13.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(20.5F, -7.5F, -7.5F, -0.4812F, -0.0697F, -1.7526F));

        ModelPartData cube_r71 = pillars.addChild("cube_r71", ModelPartBuilder.create().uv(63, 15).cuboid(-3.0F, -6.0F, 0.0F, 3.0F, 6.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(20.0383F, -7.5F, -8.387F, 0.0F, 0.48F, 0.0F));

        ModelPartData cube_r72 = pillars.addChild("cube_r72", ModelPartBuilder.create().uv(21, 93).cuboid(-6.0F, -13.0F, 0.0F, 6.0F, 13.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(20.0014F, -13.538F, -8.366F, -0.4899F, 0.1932F, -1.1492F));

        ModelPartData cube_r73 = pillars.addChild("cube_r73", ModelPartBuilder.create().uv(108, 11).cuboid(0.0F, -13.0F, 0.0F, 4.0F, 13.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(20.5F, -7.5F, -10.5F, -0.4835F, -0.116F, -1.7282F));

        ModelPartData cube_r74 = pillars.addChild("cube_r74", ModelPartBuilder.create().uv(13, 105).cuboid(0.0F, -13.0F, 0.0F, 1.0F, 13.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(20.5F, -7.5F, -10.5F, -0.48F, 0.0F, -1.789F));

        ModelPartData cube_r75 = pillars.addChild("cube_r75", ModelPartBuilder.create().uv(91, 101).cuboid(-1.0F, -13.0F, -3.0F, 1.0F, 13.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(20.5F, -13.5F, 10.5F, 0.48F, 0.0F, -1.0472F));

        ModelPartData cube_r76 = pillars.addChild("cube_r76", ModelPartBuilder.create().uv(36, 96).cuboid(-6.0F, -13.0F, 0.0F, 6.0F, 13.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(20.5F, -13.5F, -10.5F, -0.4899F, 0.1932F, -1.1492F));

        ModelPartData cube_r77 = pillars.addChild("cube_r77", ModelPartBuilder.create().uv(22, 108).cuboid(-1.0F, -13.0F, 0.0F, 1.0F, 13.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(20.5F, -13.5F, -10.5F, -0.48F, 0.0F, -1.0472F));

        ModelPartData cube_r78 = pillars.addChild("cube_r78", ModelPartBuilder.create().uv(72, 48).cuboid(-3.0F, -6.0F, -1.0F, 3.0F, 6.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(20.5F, -7.5F, 10.5F, 0.0F, -0.48F, 0.0F));

        ModelPartData cube_r79 = pillars.addChild("cube_r79", ModelPartBuilder.create().uv(91, 0).cuboid(-3.0F, -6.0F, 0.0F, 3.0F, 6.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(20.5F, -7.5F, -10.5F, 0.0F, 0.48F, 0.0F));

        ModelPartData pillars2 = pillars.addChild("pillars2", ModelPartBuilder.create().uv(0, 63).cuboid(18.5F, -13.5F, -10.5F, 2.0F, 6.0F, 3.0F, new Dilation(0.0F))
                .uv(27, 13).cuboid(18.5866F, -10.5F, -9.55F, 3.0F, 1.0F, 1.0F, new Dilation(-0.3F))
                .uv(30, 28).cuboid(20.4866F, -10.5F, -9.55F, 1.0F, 1.0F, 1.0F, new Dilation(-0.2F))
                .uv(28, 30).cuboid(18.5F, -13.5F, 7.5F, 2.0F, 6.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData cube_r80 = pillars2.addChild("cube_r80", ModelPartBuilder.create().uv(63, 15).cuboid(-6.0F, -2.0F, 1.0F, 7.0F, 2.0F, 9.0F, new Dilation(0.0F)), ModelTransform.of(8.0941F, -18.3532F, -5.5F, 0.0F, 0.0F, 1.309F));

        ModelPartData cube_r81 = pillars2.addChild("cube_r81", ModelPartBuilder.create().uv(31, 66).cuboid(-1.0F, -2.0F, 1.0F, 5.0F, 7.0F, 9.0F, new Dilation(0.001F)), ModelTransform.of(8.0941F, -18.3532F, -5.5F, 0.0F, 0.0F, 0.5236F));

        ModelPartData cube_r82 = pillars2.addChild("cube_r82", ModelPartBuilder.create().uv(72, 11).cuboid(9.3948F, -5.45F, -2.7638F, 2.0F, 2.0F, 2.0F, new Dilation(-0.4F))
                .uv(71, 32).cuboid(10.6948F, -6.45F, -2.7638F, 0.0F, 4.0F, 2.0F, new Dilation(0.0F))
                .uv(28, 0).cuboid(7.5948F, -10.45F, -3.2638F, 3.0F, 10.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 2.45F, 0.0F, 0.0F, -0.48F, 0.0F));

        ModelPartData cube_r83 = pillars2.addChild("cube_r83", ModelPartBuilder.create().uv(65, 71).cuboid(-1.0F, -1.0F, -1.0F, 1.0F, 2.0F, 2.0F, new Dilation(-0.25F)), ModelTransform.of(10.7443F, -3.0F, -3.6047F, -0.7854F, 0.48F, 0.0F));

        ModelPartData cube_r84 = pillars2.addChild("cube_r84", ModelPartBuilder.create().uv(69, 26).cuboid(10.6948F, -6.45F, 0.7638F, 0.0F, 2.0F, 2.0F, new Dilation(0.0F))
                .uv(28, 15).cuboid(7.5948F, -10.45F, 0.2638F, 3.0F, 10.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 2.45F, 0.0F, 0.0F, 0.48F, 0.0F));

        ModelPartData cube_r85 = pillars2.addChild("cube_r85", ModelPartBuilder.create().uv(0, 100).cuboid(-5.0F, -13.0F, -1.0F, 5.0F, 13.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(20.0014F, -13.538F, 8.366F, 0.4899F, -0.1932F, -1.1492F));

        ModelPartData cube_r86 = pillars2.addChild("cube_r86", ModelPartBuilder.create().uv(63, 0).cuboid(-3.0F, -6.0F, -1.0F, 3.0F, 6.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(20.0383F, -7.5F, 8.387F, 0.0F, -0.48F, 0.0F));

        ModelPartData cube_r87 = pillars2.addChild("cube_r87", ModelPartBuilder.create().uv(100, 103).cuboid(0.0F, -13.0F, 0.0F, 4.0F, 13.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(20.5F, -7.5F, 7.5F, 0.4819F, 0.0851F, -1.7445F));

        ModelPartData cube_r88 = pillars2.addChild("cube_r88", ModelPartBuilder.create().uv(101, 71).cuboid(-5.0F, -13.0F, -1.0F, 5.0F, 13.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(20.5F, -13.5F, 10.5F, 0.4899F, -0.1932F, -1.1492F));

        ModelPartData cube_r89 = pillars2.addChild("cube_r89", ModelPartBuilder.create().uv(51, 96).cuboid(0.0F, -13.0F, -1.0F, 4.0F, 13.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(20.5F, -7.5F, 10.5F, 0.4835F, 0.116F, -1.7282F));

        ModelPartData cube_r90 = pillars2.addChild("cube_r90", ModelPartBuilder.create().uv(28, 45).cuboid(-1.0F, 0.0F, 0.0F, 1.0F, 6.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(10.5948F, 0.6969F, 1.8362F, 0.4784F, -0.0403F, -1.2752F));

        ModelPartData cube_r91 = pillars2.addChild("cube_r91", ModelPartBuilder.create().uv(63, 30).cuboid(-1.0F, 0.0F, -3.0F, 1.0F, 6.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(10.5948F, 0.6969F, -1.8362F, -0.4784F, 0.0403F, -1.2752F));

        ModelPartData cube_r92 = pillars2.addChild("cube_r92", ModelPartBuilder.create().uv(82, 101).cuboid(0.0F, -13.0F, -3.0F, 1.0F, 13.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(20.5F, -7.5F, 10.5F, 0.48F, 0.0F, -1.789F));

        ModelPartData cube_r93 = pillars2.addChild("cube_r93", ModelPartBuilder.create().uv(106, 40).cuboid(0.0F, -13.0F, -1.0F, 4.0F, 13.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(20.5F, -7.5F, -7.5F, -0.4812F, -0.0697F, -1.7526F));

        ModelPartData cube_r94 = pillars2.addChild("cube_r94", ModelPartBuilder.create().uv(63, 15).cuboid(-3.0F, -6.0F, 0.0F, 3.0F, 6.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(20.0383F, -7.5F, -8.387F, 0.0F, 0.48F, 0.0F));

        ModelPartData cube_r95 = pillars2.addChild("cube_r95", ModelPartBuilder.create().uv(21, 93).cuboid(-6.0F, -13.0F, 0.0F, 6.0F, 13.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(20.0014F, -13.538F, -8.366F, -0.4899F, 0.1932F, -1.1492F));

        ModelPartData cube_r96 = pillars2.addChild("cube_r96", ModelPartBuilder.create().uv(108, 11).cuboid(0.0F, -13.0F, 0.0F, 4.0F, 13.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(20.5F, -7.5F, -10.5F, -0.4835F, -0.116F, -1.7282F));

        ModelPartData cube_r97 = pillars2.addChild("cube_r97", ModelPartBuilder.create().uv(13, 105).cuboid(0.0F, -13.0F, 0.0F, 1.0F, 13.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(20.5F, -7.5F, -10.5F, -0.48F, 0.0F, -1.789F));

        ModelPartData cube_r98 = pillars2.addChild("cube_r98", ModelPartBuilder.create().uv(91, 101).cuboid(-1.0F, -13.0F, -3.0F, 1.0F, 13.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(20.5F, -13.5F, 10.5F, 0.48F, 0.0F, -1.0472F));

        ModelPartData cube_r99 = pillars2.addChild("cube_r99", ModelPartBuilder.create().uv(36, 96).cuboid(-6.0F, -13.0F, 0.0F, 6.0F, 13.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(20.5F, -13.5F, -10.5F, -0.4899F, 0.1932F, -1.1492F));

        ModelPartData cube_r100 = pillars2.addChild("cube_r100", ModelPartBuilder.create().uv(22, 108).cuboid(-1.0F, -13.0F, 0.0F, 1.0F, 13.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(20.5F, -13.5F, -10.5F, -0.48F, 0.0F, -1.0472F));

        ModelPartData cube_r101 = pillars2.addChild("cube_r101", ModelPartBuilder.create().uv(72, 48).cuboid(-3.0F, -6.0F, -1.0F, 3.0F, 6.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(20.5F, -7.5F, 10.5F, 0.0F, -0.48F, 0.0F));

        ModelPartData cube_r102 = pillars2.addChild("cube_r102", ModelPartBuilder.create().uv(91, 0).cuboid(-3.0F, -6.0F, 0.0F, 3.0F, 6.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(20.5F, -7.5F, -10.5F, 0.0F, 0.48F, 0.0F));

        ModelPartData cube_r103 = pillars2.addChild("cube_r103", ModelPartBuilder.create().uv(233, 18).cuboid(-1.5F, -0.5F, -0.5F, 3.0F, 1.0F, 1.0F, new Dilation(-0.31F)), ModelTransform.of(18.5422F, -2.7757F, -7.861F, 0.4099F, -0.3944F, 2.3174F));

        ModelPartData cube_r104 = pillars2.addChild("cube_r104", ModelPartBuilder.create().uv(233, 18).cuboid(2.3F, -0.5F, -0.2F, 3.0F, 1.0F, 1.0F, new Dilation(-0.31F)), ModelTransform.of(13.3056F, -0.4992F, -6.4681F, 0.479F, -0.7216F, -2.5504F));

        ModelPartData cube_r105 = pillars2.addChild("cube_r105", ModelPartBuilder.create().uv(233, 18).cuboid(-1.5F, -0.5F, -0.5F, 3.0F, 1.0F, 1.0F, new Dilation(-0.32F)), ModelTransform.of(12.7F, -1.3063F, -5.1195F, 0.475F, -0.5409F, -2.9837F));

        ModelPartData cube_r106 = pillars2.addChild("cube_r106", ModelPartBuilder.create().uv(233, 18).cuboid(-1.5F, -0.5F, -0.5F, 3.0F, 1.0F, 1.0F, new Dilation(-0.31F)), ModelTransform.of(14.7249F, -1.3035F, -6.1852F, 0.3211F, -0.4004F, 3.0565F));

        ModelPartData cube_r107 = pillars2.addChild("cube_r107", ModelPartBuilder.create().uv(233, 18).cuboid(-1.5F, -0.5F, -0.5F, 3.0F, 1.0F, 1.0F, new Dilation(-0.32F)), ModelTransform.of(16.8255F, -1.7326F, -7.067F, 0.1247F, -0.347F, 2.878F));

        ModelPartData cube_r108 = pillars2.addChild("cube_r108", ModelPartBuilder.create().uv(233, 18).cuboid(-1.5F, -0.5F, -0.5F, 3.0F, 1.0F, 1.0F, new Dilation(-0.32F)), ModelTransform.of(19.7269F, -4.5673F, -8.5853F, 0.3124F, -0.2079F, 1.9842F));

        ModelPartData cube_r109 = pillars2.addChild("cube_r109", ModelPartBuilder.create().uv(233, 18).cuboid(-1.5F, -0.5F, -0.5F, 3.0F, 1.0F, 1.0F, new Dilation(-0.31F)), ModelTransform.of(20.6425F, -6.7213F, -8.9233F, 0.2533F, -0.067F, 1.955F));

        ModelPartData cube_r110 = pillars2.addChild("cube_r110", ModelPartBuilder.create().uv(233, 18).cuboid(-1.7F, 0.2F, -0.6F, 3.0F, 1.0F, 1.0F, new Dilation(-0.32F)), ModelTransform.of(21.4935F, -8.8201F, -8.9412F, 0.0F, 0.0F, 1.3526F));

        ModelPartData pillars3 = pillars2.addChild("pillars3", ModelPartBuilder.create().uv(0, 63).cuboid(18.5F, -13.5F, -10.5F, 2.0F, 6.0F, 3.0F, new Dilation(0.0F))
                .uv(136, 225).cuboid(18.7F, -13.5F, -10.5F, 2.0F, 6.0F, 3.0F, new Dilation(0.0F))
                .uv(201, 87).cuboid(19.85F, -8.9615F, -10.1787F, 1.0F, 3.0F, 1.0F, new Dilation(0.0F))
                .uv(28, 30).cuboid(18.5F, -13.5F, 7.5F, 2.0F, 6.0F, 3.0F, new Dilation(0.0F))
                .uv(216, 152).cuboid(19.8F, -11.5F, 8.0F, 1.0F, 2.0F, 2.0F, new Dilation(0.0F))
                .uv(161, 127).cuboid(20.2F, -11.0F, 8.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.2F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData cube_r111 = pillars3.addChild("cube_r111", ModelPartBuilder.create().uv(31, 66).cuboid(-1.0F, -2.0F, 1.0F, 5.0F, 7.0F, 9.0F, new Dilation(0.001F)), ModelTransform.of(8.0941F, -18.3532F, -5.5F, 0.0F, 0.0F, 0.5236F));

        ModelPartData cube_r112 = pillars3.addChild("cube_r112", ModelPartBuilder.create().uv(63, 0).cuboid(-8.0F, -2.0F, 1.0F, 9.0F, 2.0F, 9.0F, new Dilation(0.0F)), ModelTransform.of(8.0941F, -18.3532F, -5.5F, 0.0F, 0.0F, 1.309F));

        ModelPartData cube_r113 = pillars3.addChild("cube_r113", ModelPartBuilder.create().uv(128, 19).cuboid(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.2F)), ModelTransform.of(10.8683F, -3.0F, 4.2329F, -0.48F, 0.0F, 1.5708F));

        ModelPartData cube_r114 = pillars3.addChild("cube_r114", ModelPartBuilder.create().uv(128, 19).cuboid(-1.5F, -0.5F, -1.0F, 1.0F, 1.0F, 1.0F, new Dilation(-0.2F))
                .uv(128, 19).cuboid(-0.5F, -0.3F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(11.0991F, -2.0F, 3.7894F, -0.48F, 0.0F, 1.5708F));

        ModelPartData cube_r115 = pillars3.addChild("cube_r115", ModelPartBuilder.create().uv(125, 18).cuboid(8.5948F, -6.45F, -2.7638F, 3.0F, 3.0F, 2.0F, new Dilation(0.0F))
                .uv(28, 0).cuboid(7.5948F, -10.45F, -3.2638F, 3.0F, 10.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 2.45F, 0.0F, 0.0F, -0.48F, 0.0F));

        ModelPartData cube_r116 = pillars3.addChild("cube_r116", ModelPartBuilder.create().uv(28, 15).cuboid(7.5948F, -10.45F, 0.2638F, 3.0F, 10.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 2.45F, 0.0F, 0.0F, 0.48F, 0.0F));

        ModelPartData cube_r117 = pillars3.addChild("cube_r117", ModelPartBuilder.create().uv(216, 152).cuboid(-0.5F, -1.0F, -1.0F, 1.0F, 2.0F, 2.0F, new Dilation(-0.2F)), ModelTransform.of(20.6F, -10.5F, 9.0F, -1.5708F, 0.0F, 0.0F));

        ModelPartData cube_r118 = pillars3.addChild("cube_r118", ModelPartBuilder.create().uv(0, 100).cuboid(-5.0F, -13.0F, -1.0F, 5.0F, 13.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(20.0014F, -13.538F, 8.366F, 0.4899F, -0.1932F, -1.1492F));

        ModelPartData cube_r119 = pillars3.addChild("cube_r119", ModelPartBuilder.create().uv(63, 0).cuboid(-3.0F, -6.0F, -1.0F, 3.0F, 6.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(20.0383F, -7.5F, 8.387F, 0.0F, -0.48F, 0.0F));

        ModelPartData cube_r120 = pillars3.addChild("cube_r120", ModelPartBuilder.create().uv(100, 103).cuboid(0.0F, -13.0F, 0.0F, 4.0F, 13.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(20.5F, -7.5F, 7.5F, 0.4819F, 0.0851F, -1.7445F));

        ModelPartData cube_r121 = pillars3.addChild("cube_r121", ModelPartBuilder.create().uv(101, 71).cuboid(-5.0F, -13.0F, -1.0F, 5.0F, 13.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(20.5F, -13.5F, 10.5F, 0.4899F, -0.1932F, -1.1492F));

        ModelPartData cube_r122 = pillars3.addChild("cube_r122", ModelPartBuilder.create().uv(51, 96).cuboid(0.0F, -13.0F, -1.0F, 4.0F, 13.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(20.5F, -7.5F, 10.5F, 0.4835F, 0.116F, -1.7282F));

        ModelPartData cube_r123 = pillars3.addChild("cube_r123", ModelPartBuilder.create().uv(28, 45).cuboid(-1.0F, 0.0F, 0.0F, 1.0F, 6.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(10.5948F, 0.6969F, 1.8362F, 0.4784F, -0.0403F, -1.2752F));

        ModelPartData cube_r124 = pillars3.addChild("cube_r124", ModelPartBuilder.create().uv(63, 30).cuboid(-1.0F, 0.0F, -3.0F, 1.0F, 6.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(10.5948F, 0.6969F, -1.8362F, -0.4784F, 0.0403F, -1.2752F));

        ModelPartData cube_r125 = pillars3.addChild("cube_r125", ModelPartBuilder.create().uv(82, 101).cuboid(0.0F, -13.0F, -3.0F, 1.0F, 13.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(20.5F, -7.5F, 10.5F, 0.48F, 0.0F, -1.789F));

        ModelPartData cube_r126 = pillars3.addChild("cube_r126", ModelPartBuilder.create().uv(106, 40).cuboid(0.0F, -13.0F, -1.0F, 4.0F, 13.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(20.5F, -7.5F, -7.5F, -0.4812F, -0.0697F, -1.7526F));

        ModelPartData cube_r127 = pillars3.addChild("cube_r127", ModelPartBuilder.create().uv(63, 15).cuboid(-3.0F, -6.0F, 0.0F, 3.0F, 6.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(20.0383F, -7.5F, -8.387F, 0.0F, 0.48F, 0.0F));

        ModelPartData cube_r128 = pillars3.addChild("cube_r128", ModelPartBuilder.create().uv(21, 93).cuboid(-6.0F, -13.0F, 0.0F, 6.0F, 13.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(20.0014F, -13.538F, -8.366F, -0.4899F, 0.1932F, -1.1492F));

        ModelPartData cube_r129 = pillars3.addChild("cube_r129", ModelPartBuilder.create().uv(108, 11).cuboid(0.0F, -13.0F, 0.0F, 4.0F, 13.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(20.5F, -7.5F, -10.5F, -0.4835F, -0.116F, -1.7282F));

        ModelPartData cube_r130 = pillars3.addChild("cube_r130", ModelPartBuilder.create().uv(34, 28).cuboid(-0.5F, -4.7F, 1.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(13, 105).cuboid(0.0F, -13.0F, 0.0F, 1.0F, 13.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(20.5F, -7.5F, -10.5F, -0.48F, 0.0F, -1.789F));

        ModelPartData cube_r131 = pillars3.addChild("cube_r131", ModelPartBuilder.create().uv(91, 101).cuboid(-1.0F, -13.0F, -3.0F, 1.0F, 13.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(20.5F, -13.5F, 10.5F, 0.48F, 0.0F, -1.0472F));

        ModelPartData cube_r132 = pillars3.addChild("cube_r132", ModelPartBuilder.create().uv(36, 96).cuboid(-6.0F, -13.0F, 0.0F, 6.0F, 13.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(20.5F, -13.5F, -10.5F, -0.4899F, 0.1932F, -1.1492F));

        ModelPartData cube_r133 = pillars3.addChild("cube_r133", ModelPartBuilder.create().uv(22, 108).cuboid(-1.0F, -13.0F, 0.0F, 1.0F, 13.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(20.5F, -13.5F, -10.5F, -0.48F, 0.0F, -1.0472F));

        ModelPartData cube_r134 = pillars3.addChild("cube_r134", ModelPartBuilder.create().uv(72, 48).cuboid(-3.0F, -6.0F, -1.0F, 3.0F, 6.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(20.5F, -7.5F, 10.5F, 0.0F, -0.48F, 0.0F));

        ModelPartData cube_r135 = pillars3.addChild("cube_r135", ModelPartBuilder.create().uv(91, 0).cuboid(-3.0F, -6.0F, 0.0F, 3.0F, 6.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(20.5F, -7.5F, -10.5F, 0.0F, 0.48F, 0.0F));

        ModelPartData cube_r136 = pillars3.addChild("cube_r136", ModelPartBuilder.create().uv(185, 97).cuboid(0.0F, 0.0F, -1.0F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(19.9F, -10.9373F, -9.2343F, 0.0F, -0.1745F, 0.0F));

        ModelPartData cube_r137 = pillars3.addChild("cube_r137", ModelPartBuilder.create().uv(211, 92).cuboid(0.5F, 0.2F, -1.4F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(19.9F, -5.4373F, -9.2343F, -1.5708F, 0.0F, 0.0F));

        ModelPartData cube_r138 = pillars3.addChild("cube_r138", ModelPartBuilder.create().uv(211, 92).cuboid(0.05F, -1.4F, -0.1F, 0.0F, 2.0F, 1.0F, new Dilation(0.001F)), ModelTransform.of(21.45F, -6.5187F, -9.5179F, -2.2689F, 0.0F, 0.0F));

        ModelPartData cube_r139 = pillars3.addChild("cube_r139", ModelPartBuilder.create().uv(213, 97).cuboid(1.6F, -1.2F, -1.6F, 0.0F, 2.0F, 1.0F, new Dilation(0.0F))
                .uv(211, 92).cuboid(1.7F, -1.2F, -1.6F, 0.0F, 2.0F, 1.0F, new Dilation(0.001F)), ModelTransform.of(19.9F, -10.9373F, -9.2343F, -1.1345F, 0.0F, 0.0F));

        ModelPartData cube_r140 = pillars3.addChild("cube_r140", ModelPartBuilder.create().uv(211, 92).cuboid(0.5F, 0.2F, -1.4F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(19.9F, -10.9373F, -9.2343F, -1.5708F, 0.0F, 0.0F));

        ModelPartData cube_r141 = pillars3.addChild("cube_r141", ModelPartBuilder.create().uv(185, 97).cuboid(0.0F, -1.0F, -1.0F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(19.9F, -10.9373F, -9.2343F, -1.5708F, -0.1745F, 0.0F));

        ModelPartData cube_r142 = pillars3.addChild("cube_r142", ModelPartBuilder.create().uv(211, 87).cuboid(-0.2F, -0.3F, 0.2F, 0.0F, 2.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(21.0F, -8.2373F, -8.4343F, -0.2618F, 0.0F, 0.0F));

        ModelPartData cube_r143 = pillars3.addChild("cube_r143", ModelPartBuilder.create().uv(202, 82).cuboid(0.0F, -1.3F, 0.2F, 0.0F, 2.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(21.65F, -11.2611F, -8.7474F, 0.7854F, 0.0F, 0.0F));

        ModelPartData handbrake = pillars3.addChild("handbrake", ModelPartBuilder.create(), ModelTransform.pivot(23.5F, -10.5F, -9.0F));

        ModelPartData cube_r144 = handbrake.addChild("cube_r144", ModelPartBuilder.create().uv(201, 87).cuboid(-0.65F, -1.7F, -0.1F, 1.0F, 3.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-2.5F, 1.8385F, 2.1213F, -0.5672F, 0.0F, 0.0F));

        ModelPartData cube_r145 = handbrake.addChild("cube_r145", ModelPartBuilder.create().uv(202, 95).cuboid(-4.2F, -2.1F, -0.5F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -0.7854F, -1.5708F));

        ModelPartData cube_r146 = handbrake.addChild("cube_r146", ModelPartBuilder.create().uv(202, 99).cuboid(-3.5F, -1.8F, 0.5F, 1.0F, 6.0F, 3.0F, new Dilation(0.0F))
                .uv(194, 96).cuboid(-3.0F, -1.8F, -0.5F, 1.0F, 6.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, -0.7854F, 0.0F, 0.0F));

        ModelPartData cube_r147 = handbrake.addChild("cube_r147", ModelPartBuilder.create().uv(212, 92).cuboid(2.8F, -0.8F, 0.35F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(211, 92).cuboid(2.5F, 0.2F, -1.4F, 2.0F, 1.0F, 1.0F, new Dilation(0.001F)), ModelTransform.of(-5.5F, 0.2121F, 0.7778F, -2.3562F, 0.0F, 0.0F));

        ModelPartData lampthingy = pillars3.addChild("lampthingy", ModelPartBuilder.create(), ModelTransform.pivot(18.082F, -5.4667F, -6.844F));

        ModelPartData cube_r148 = lampthingy.addChild("cube_r148", ModelPartBuilder.create().uv(35, 13).cuboid(-0.51F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.1F)), ModelTransform.of(0.7717F, 0.3667F, -0.4017F, -0.7854F, 0.48F, 0.0F));

        ModelPartData cube_r149 = lampthingy.addChild("cube_r149", ModelPartBuilder.create().uv(59, 49).cuboid(13.5948F, -6.45F, 1.2638F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F))
                .uv(87, 92).cuboid(12.4948F, -7.55F, 0.7638F, 2.0F, 2.0F, 2.0F, new Dilation(-0.4F)), ModelTransform.of(-13.432F, 5.8167F, 5.0038F, 0.0F, 0.48F, 0.0F));

        ModelPartData pillars4 = pillars3.addChild("pillars4", ModelPartBuilder.create().uv(0, 63).cuboid(18.5F, -13.5F, -10.5F, 2.0F, 6.0F, 3.0F, new Dilation(0.0F))
                .uv(28, 30).cuboid(18.5F, -13.5F, 7.5F, 2.0F, 6.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData cube_r150 = pillars4.addChild("cube_r150", ModelPartBuilder.create().uv(186, 41).cuboid(-2.0F, -3.6F, -1.5F, 2.0F, 2.0F, 3.0F, new Dilation(-0.2F))
                .uv(183, 23).cuboid(-2.5F, -2.0F, -2.5F, 3.0F, 1.0F, 5.0F, new Dilation(0.0F)), ModelTransform.of(11.3107F, -21.8033F, 0.0F, 0.0F, 0.0F, 0.2182F));

        ModelPartData cube_r151 = pillars4.addChild("cube_r151", ModelPartBuilder.create().uv(184, 14).cuboid(2.7F, -1.0F, -1.1F, 1.0F, 2.0F, 2.0F, new Dilation(-0.1F))
                .uv(187, 32).cuboid(1.75F, -0.5F, -0.6F, 1.0F, 1.0F, 1.0F, new Dilation(-0.05F))
                .uv(187, 29).cuboid(-0.2F, -0.5F, -0.6F, 3.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(8.8796F, -24.6981F, -0.35F, 0.0F, 1.5708F, 1.789F));

        ModelPartData cube_r152 = pillars4.addChild("cube_r152", ModelPartBuilder.create().uv(187, 27).cuboid(0.4F, -0.5F, -0.8F, 3.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(8.8796F, -24.6981F, -0.35F, 0.0F, 0.0F, 1.789F));

        ModelPartData cube_r153 = pillars4.addChild("cube_r153", ModelPartBuilder.create().uv(63, 15).cuboid(-6.0F, -2.0F, 1.0F, 7.0F, 2.0F, 9.0F, new Dilation(0.0F)), ModelTransform.of(8.0941F, -18.3532F, -5.5F, 0.0F, 0.0F, 1.309F));

        ModelPartData cube_r154 = pillars4.addChild("cube_r154", ModelPartBuilder.create().uv(31, 66).cuboid(-1.0F, -2.0F, 1.0F, 5.0F, 7.0F, 9.0F, new Dilation(0.001F)), ModelTransform.of(8.0941F, -18.3532F, -5.5F, 0.0F, 0.0F, 0.5236F));

        ModelPartData cube_r155 = pillars4.addChild("cube_r155", ModelPartBuilder.create().uv(85, 55).cuboid(-0.5F, -0.3F, -1.7F, 1.0F, 1.0F, 3.0F, new Dilation(-0.3F)), ModelTransform.of(10.5244F, -2.3F, 3.3775F, 0.9599F, -0.48F, 0.0F));

        ModelPartData cube_r156 = pillars4.addChild("cube_r156", ModelPartBuilder.create().uv(73, 26).cuboid(10.6948F, -5.55F, -2.7638F, 0.0F, 2.0F, 2.0F, new Dilation(0.0F))
                .uv(28, 0).cuboid(7.5948F, -10.45F, -3.2638F, 3.0F, 10.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 2.45F, 0.0F, 0.0F, -0.48F, 0.0F));

        ModelPartData cube_r157 = pillars4.addChild("cube_r157", ModelPartBuilder.create().uv(28, 15).cuboid(7.5948F, -10.45F, 0.2638F, 3.0F, 10.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 2.45F, 0.0F, 0.0F, 0.48F, 0.0F));

        ModelPartData cube_r158 = pillars4.addChild("cube_r158", ModelPartBuilder.create().uv(0, 100).cuboid(-5.0F, -13.0F, -1.0F, 5.0F, 13.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(20.0014F, -13.538F, 8.366F, 0.4899F, -0.1932F, -1.1492F));

        ModelPartData cube_r159 = pillars4.addChild("cube_r159", ModelPartBuilder.create().uv(63, 0).cuboid(-3.0F, -6.0F, -1.0F, 3.0F, 6.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(20.0383F, -7.5F, 8.387F, 0.0F, -0.48F, 0.0F));

        ModelPartData cube_r160 = pillars4.addChild("cube_r160", ModelPartBuilder.create().uv(100, 103).cuboid(0.0F, -13.0F, 0.0F, 4.0F, 13.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(20.5F, -7.5F, 7.5F, 0.4819F, 0.0851F, -1.7445F));

        ModelPartData cube_r161 = pillars4.addChild("cube_r161", ModelPartBuilder.create().uv(101, 71).cuboid(-5.0F, -13.0F, -1.0F, 5.0F, 13.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(20.5F, -13.5F, 10.5F, 0.4899F, -0.1932F, -1.1492F));

        ModelPartData cube_r162 = pillars4.addChild("cube_r162", ModelPartBuilder.create().uv(51, 96).cuboid(0.0F, -13.0F, -1.0F, 4.0F, 13.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(20.5F, -7.5F, 10.5F, 0.4835F, 0.116F, -1.7282F));

        ModelPartData cube_r163 = pillars4.addChild("cube_r163", ModelPartBuilder.create().uv(28, 45).cuboid(-1.0F, 0.0F, 0.0F, 1.0F, 6.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(10.5948F, 0.6969F, 1.8362F, 0.4784F, -0.0403F, -1.2752F));

        ModelPartData cube_r164 = pillars4.addChild("cube_r164", ModelPartBuilder.create().uv(63, 30).cuboid(-1.0F, 0.0F, -3.0F, 1.0F, 6.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(10.5948F, 0.6969F, -1.8362F, -0.4784F, 0.0403F, -1.2752F));

        ModelPartData cube_r165 = pillars4.addChild("cube_r165", ModelPartBuilder.create().uv(82, 101).cuboid(0.0F, -13.0F, -3.0F, 1.0F, 13.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(20.5F, -7.5F, 10.5F, 0.48F, 0.0F, -1.789F));

        ModelPartData cube_r166 = pillars4.addChild("cube_r166", ModelPartBuilder.create().uv(106, 40).cuboid(0.0F, -13.0F, -1.0F, 4.0F, 13.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(20.5F, -7.5F, -7.5F, -0.4812F, -0.0697F, -1.7526F));

        ModelPartData cube_r167 = pillars4.addChild("cube_r167", ModelPartBuilder.create().uv(63, 15).cuboid(-3.0F, -6.0F, 0.0F, 3.0F, 6.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(20.0383F, -7.5F, -8.387F, 0.0F, 0.48F, 0.0F));

        ModelPartData cube_r168 = pillars4.addChild("cube_r168", ModelPartBuilder.create().uv(21, 93).cuboid(-6.0F, -13.0F, 0.0F, 6.0F, 13.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(20.0014F, -13.538F, -8.366F, -0.4899F, 0.1932F, -1.1492F));

        ModelPartData cube_r169 = pillars4.addChild("cube_r169", ModelPartBuilder.create().uv(108, 11).cuboid(0.0F, -13.0F, 0.0F, 4.0F, 13.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(20.5F, -7.5F, -10.5F, -0.4835F, -0.116F, -1.7282F));

        ModelPartData cube_r170 = pillars4.addChild("cube_r170", ModelPartBuilder.create().uv(13, 105).cuboid(0.0F, -13.0F, 0.0F, 1.0F, 13.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(20.5F, -7.5F, -10.5F, -0.48F, 0.0F, -1.789F));

        ModelPartData cube_r171 = pillars4.addChild("cube_r171", ModelPartBuilder.create().uv(91, 101).cuboid(-1.0F, -13.0F, -3.0F, 1.0F, 13.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(20.5F, -13.5F, 10.5F, 0.48F, 0.0F, -1.0472F));

        ModelPartData cube_r172 = pillars4.addChild("cube_r172", ModelPartBuilder.create().uv(36, 96).cuboid(-6.0F, -13.0F, 0.0F, 6.0F, 13.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(20.5F, -13.5F, -10.5F, -0.4899F, 0.1932F, -1.1492F));

        ModelPartData cube_r173 = pillars4.addChild("cube_r173", ModelPartBuilder.create().uv(22, 108).cuboid(-1.0F, -13.0F, 0.0F, 1.0F, 13.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(20.5F, -13.5F, -10.5F, -0.48F, 0.0F, -1.0472F));

        ModelPartData cube_r174 = pillars4.addChild("cube_r174", ModelPartBuilder.create().uv(72, 48).cuboid(-3.0F, -6.0F, -1.0F, 3.0F, 6.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(20.5F, -7.5F, 10.5F, 0.0F, -0.48F, 0.0F));

        ModelPartData cube_r175 = pillars4.addChild("cube_r175", ModelPartBuilder.create().uv(91, 0).cuboid(-3.0F, -6.0F, 0.0F, 3.0F, 6.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(20.5F, -7.5F, -10.5F, 0.0F, 0.48F, 0.0F));

        ModelPartData bone127 = pillars4.addChild("bone127", ModelPartBuilder.create(), ModelTransform.pivot(8.8796F, -24.6981F, -0.35F));

        ModelPartData bone122 = pillars4.addChild("bone122", ModelPartBuilder.create().uv(158, 141).cuboid(-1.0F, -2.0F, -1.0F, 2.0F, 3.0F, 2.0F, new Dilation(-0.7F)), ModelTransform.pivot(7.835F, -24.212F, 0.15F));

        ModelPartData bone121 = pillars4.addChild("bone121", ModelPartBuilder.create().uv(245, 18).cuboid(-1.1125F, -0.4375F, -1.25F, 2.0F, 1.0F, 2.0F, new Dilation(-0.1F))
                .uv(244, 7).cuboid(-0.8625F, -0.3375F, -1.0F, 2.0F, 1.0F, 2.0F, new Dilation(0.0F))
                .uv(248, 12).cuboid(-0.8625F, -0.3875F, -1.0F, 2.0F, 1.0F, 2.0F, new Dilation(-0.1F)), ModelTransform.of(8.0125F, -26.2625F, 0.15F, 0.0F, 0.0F, 1.5708F));

        ModelPartData fuel_gauge = bone121.addChild("fuel_gauge", ModelPartBuilder.create(), ModelTransform.pivot(0.0875F, 1.2625F, 0.0F));

        ModelPartData cube_r176 = fuel_gauge.addChild("cube_r176", ModelPartBuilder.create().uv(155, 0).cuboid(-0.75F, 0.0F, -0.25F, 1.0F, 0.0F, 1.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, -1.6F, 0.0F, 0.0F, -1.5708F, 0.0F));

        ModelPartData pillars5 = pillars4.addChild("pillars5", ModelPartBuilder.create().uv(0, 63).cuboid(18.5F, -13.5F, -10.5F, 2.0F, 6.0F, 3.0F, new Dilation(0.0F))
                .uv(28, 30).cuboid(18.5F, -13.5F, 7.5F, 2.0F, 6.0F, 3.0F, new Dilation(0.0F))
                .uv(238, 78).cuboid(18.6F, -13.5F, 7.3F, 2.0F, 6.0F, 3.0F, new Dilation(0.0F))
                .uv(28, 154).cuboid(20.0F, -12.5F, 7.9F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(49, 128).cuboid(20.0F, -11.3F, 9.2F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F))
                .uv(30, 169).cuboid(19.7F, -11.1F, -8.6F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F))
                .uv(51, 132).cuboid(20.6F, -11.3F, -10.2F, 0.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData cube_r177 = pillars5.addChild("cube_r177", ModelPartBuilder.create().uv(157, 222).cuboid(-5.0F, -2.05F, 6.0F, 6.0F, 0.0F, 4.0F, new Dilation(0.0F))
                .uv(63, 0).cuboid(-8.0F, -2.0F, 1.0F, 9.0F, 2.0F, 9.0F, new Dilation(0.0F)), ModelTransform.of(8.0941F, -18.3532F, -5.5F, 0.0F, 0.0F, 1.309F));

        ModelPartData cube_r178 = pillars5.addChild("cube_r178", ModelPartBuilder.create().uv(31, 66).cuboid(-1.0F, -2.0F, 1.0F, 5.0F, 7.0F, 9.0F, new Dilation(0.001F)), ModelTransform.of(8.0941F, -18.3532F, -5.5F, 0.0F, 0.0F, 0.5236F));

        ModelPartData cube_r179 = pillars5.addChild("cube_r179", ModelPartBuilder.create().uv(28, 0).cuboid(7.5948F, -10.45F, -3.2638F, 3.0F, 10.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 2.45F, 0.0F, 0.0F, -0.48F, 0.0F));

        ModelPartData cube_r180 = pillars5.addChild("cube_r180", ModelPartBuilder.create().uv(28, 15).cuboid(7.5948F, -10.45F, 0.2638F, 3.0F, 10.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 2.45F, 0.0F, 0.0F, 0.48F, 0.0F));

        ModelPartData cube_r181 = pillars5.addChild("cube_r181", ModelPartBuilder.create().uv(30, 163).cuboid(-0.5F, -1.0F, -0.5F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(20.2F, -8.2F, 8.8F, 1.5708F, 0.0F, 0.7854F));

        ModelPartData cube_r182 = pillars5.addChild("cube_r182", ModelPartBuilder.create().uv(30, 163).cuboid(-1.05F, 15.1F, -2.0F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(20.75F, -10.0F, -7.3F, 1.5708F, 0.0F, 0.0F));

        ModelPartData cube_r183 = pillars5.addChild("cube_r183", ModelPartBuilder.create().uv(30, 160).cuboid(-0.75F, 0.9F, 0.9F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(20.75F, -9.8F, -7.3F, -1.5708F, 0.0F, 0.0F));

        ModelPartData cube_r184 = pillars5.addChild("cube_r184", ModelPartBuilder.create().uv(0, 100).cuboid(-5.0F, -13.0F, -1.0F, 5.0F, 13.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(20.0014F, -13.538F, 8.366F, 0.4899F, -0.1932F, -1.1492F));

        ModelPartData cube_r185 = pillars5.addChild("cube_r185", ModelPartBuilder.create().uv(63, 0).cuboid(-3.0F, -6.0F, -1.0F, 3.0F, 6.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(20.0383F, -7.5F, 8.387F, 0.0F, -0.48F, 0.0F));

        ModelPartData cube_r186 = pillars5.addChild("cube_r186", ModelPartBuilder.create().uv(100, 103).cuboid(0.0F, -13.0F, 0.0F, 4.0F, 13.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(20.5F, -7.5F, 7.5F, 0.4819F, 0.0851F, -1.7445F));

        ModelPartData cube_r187 = pillars5.addChild("cube_r187", ModelPartBuilder.create().uv(101, 71).cuboid(-5.0F, -13.0F, -1.0F, 5.0F, 13.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(20.5F, -13.5F, 10.5F, 0.4899F, -0.1932F, -1.1492F));

        ModelPartData cube_r188 = pillars5.addChild("cube_r188", ModelPartBuilder.create().uv(51, 96).cuboid(0.0F, -13.0F, -1.0F, 4.0F, 13.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(20.5F, -7.5F, 10.5F, 0.4835F, 0.116F, -1.7282F));

        ModelPartData cube_r189 = pillars5.addChild("cube_r189", ModelPartBuilder.create().uv(28, 45).cuboid(-1.0F, 0.0F, 0.0F, 1.0F, 6.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(10.5948F, 0.6969F, 1.8362F, 0.4784F, -0.0403F, -1.2752F));

        ModelPartData cube_r190 = pillars5.addChild("cube_r190", ModelPartBuilder.create().uv(63, 30).cuboid(-1.0F, 0.0F, -3.0F, 1.0F, 6.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(10.5948F, 0.6969F, -1.8362F, -0.4784F, 0.0403F, -1.2752F));

        ModelPartData cube_r191 = pillars5.addChild("cube_r191", ModelPartBuilder.create().uv(82, 101).cuboid(0.0F, -13.0F, -3.0F, 1.0F, 13.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(20.5F, -7.5F, 10.5F, 0.48F, 0.0F, -1.789F));

        ModelPartData cube_r192 = pillars5.addChild("cube_r192", ModelPartBuilder.create().uv(106, 40).cuboid(0.0F, -13.0F, -1.0F, 4.0F, 13.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(20.5F, -7.5F, -7.5F, -0.4812F, -0.0697F, -1.7526F));

        ModelPartData cube_r193 = pillars5.addChild("cube_r193", ModelPartBuilder.create().uv(63, 15).cuboid(-3.0F, -6.0F, 0.0F, 3.0F, 6.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(20.0383F, -7.5F, -8.387F, 0.0F, 0.48F, 0.0F));

        ModelPartData cube_r194 = pillars5.addChild("cube_r194", ModelPartBuilder.create().uv(21, 93).cuboid(-6.0F, -13.0F, 0.0F, 6.0F, 13.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(20.0014F, -13.538F, -8.366F, -0.4899F, 0.1932F, -1.1492F));

        ModelPartData cube_r195 = pillars5.addChild("cube_r195", ModelPartBuilder.create().uv(108, 11).cuboid(0.0F, -13.0F, 0.0F, 4.0F, 13.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(20.5F, -7.5F, -10.5F, -0.4835F, -0.116F, -1.7282F));

        ModelPartData cube_r196 = pillars5.addChild("cube_r196", ModelPartBuilder.create().uv(13, 105).cuboid(0.0F, -13.0F, 0.0F, 1.0F, 13.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(20.5F, -7.5F, -10.5F, -0.48F, 0.0F, -1.789F));

        ModelPartData cube_r197 = pillars5.addChild("cube_r197", ModelPartBuilder.create().uv(168, 205).cuboid(0.05F, -15.0F, -2.95F, 0.0F, 15.0F, 3.0F, new Dilation(0.0F))
                .uv(91, 101).cuboid(-1.0F, -13.0F, -3.0F, 1.0F, 13.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(20.5F, -13.5F, 10.5F, 0.48F, 0.0F, -1.0472F));

        ModelPartData cube_r198 = pillars5.addChild("cube_r198", ModelPartBuilder.create().uv(36, 96).cuboid(-6.0F, -13.0F, 0.0F, 6.0F, 13.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(20.5F, -13.5F, -10.5F, -0.4899F, 0.1932F, -1.1492F));

        ModelPartData cube_r199 = pillars5.addChild("cube_r199", ModelPartBuilder.create().uv(22, 108).cuboid(-1.0F, -13.0F, 0.0F, 1.0F, 13.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(20.5F, -13.5F, -10.5F, -0.48F, 0.0F, -1.0472F));

        ModelPartData cube_r200 = pillars5.addChild("cube_r200", ModelPartBuilder.create().uv(72, 48).cuboid(-3.0F, -6.0F, -1.0F, 3.0F, 6.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(20.5F, -7.5F, 10.5F, 0.0F, -0.48F, 0.0F));

        ModelPartData cube_r201 = pillars5.addChild("cube_r201", ModelPartBuilder.create().uv(91, 0).cuboid(-3.0F, -6.0F, 0.0F, 3.0F, 6.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(20.5F, -7.5F, -10.5F, 0.0F, 0.48F, 0.0F));

        ModelPartData bone115 = pillars5.addChild("bone115", ModelPartBuilder.create(), ModelTransform.pivot(20.5F, -13.5F, -10.5F));

        ModelPartData cube_r202 = bone115.addChild("cube_r202", ModelPartBuilder.create().uv(134, 64).cuboid(-0.6F, -4.0F, 0.5F, 1.0F, 2.0F, 2.0F, new Dilation(0.0F))
                .uv(135, 68).cuboid(-0.3F, -3.5F, 1.0F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(134, 64).cuboid(-0.6F, -7.0F, 0.5F, 1.0F, 2.0F, 2.0F, new Dilation(0.0F))
                .uv(135, 68).cuboid(-0.3F, -6.5F, 1.0F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, -0.48F, 0.0F, -1.0472F));

        ModelPartData bone126 = pillars5.addChild("bone126", ModelPartBuilder.create().uv(39, 141).cuboid(-0.5F, -3.0F, -0.75F, 1.0F, 3.0F, 2.0F, new Dilation(0.0F))
                .uv(45, 140).cuboid(0.5F, -3.0F, -0.75F, 1.0F, 1.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(20.6F, -10.025F, -9.45F, 0.6981F, 0.0F, 0.0F));

        ModelPartData pillars6 = pillars5.addChild("pillars6", ModelPartBuilder.create().uv(0, 63).cuboid(18.5F, -13.5F, -10.5F, 2.0F, 6.0F, 3.0F, new Dilation(0.0F))
                .uv(212, 193).cuboid(20.6F, -11.0F, -9.5F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(215, 189).cuboid(20.6F, -11.25F, -9.0F, 1.0F, 1.0F, 0.0F, new Dilation(0.0F))
                .uv(28, 30).cuboid(18.5F, -13.5F, 7.5F, 2.0F, 6.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData cube_r203 = pillars6.addChild("cube_r203", ModelPartBuilder.create().uv(22, 108).cuboid(-1.0F, -13.0F, 0.0F, 1.0F, 13.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(20.5F, -13.5F, -10.5F, -0.48F, 0.0F, -1.0472F));

        ModelPartData cube_r204 = pillars6.addChild("cube_r204", ModelPartBuilder.create().uv(136, 204).cuboid(1.2F, -0.1F, -1.0F, 0.0F, 2.0F, 2.0F, new Dilation(0.0F))
                .uv(130, 203).cuboid(1.2F, -1.1F, -1.0F, 0.0F, 4.0F, 2.0F, new Dilation(0.0F))
                .uv(126, 212).cuboid(-0.9F, -1.1F, -1.0F, 2.0F, 4.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(9.0F, -23.0F, 0.0F, 0.0F, 0.0F, -0.2618F));

        ModelPartData cube_r205 = pillars6.addChild("cube_r205", ModelPartBuilder.create().uv(63, 15).cuboid(-6.0F, -2.0F, 1.0F, 7.0F, 2.0F, 9.0F, new Dilation(0.0F)), ModelTransform.of(8.0941F, -18.3532F, -5.5F, 0.0F, 0.0F, 1.309F));

        ModelPartData cube_r206 = pillars6.addChild("cube_r206", ModelPartBuilder.create().uv(31, 66).cuboid(-1.0F, -2.0F, 1.0F, 5.0F, 7.0F, 9.0F, new Dilation(0.001F)), ModelTransform.of(8.0941F, -18.3532F, -5.5F, 0.0F, 0.0F, 0.5236F));

        ModelPartData cube_r207 = pillars6.addChild("cube_r207", ModelPartBuilder.create().uv(28, 0).cuboid(7.5948F, -10.45F, -3.2638F, 3.0F, 10.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 2.45F, 0.0F, 0.0F, -0.48F, 0.0F));

        ModelPartData cube_r208 = pillars6.addChild("cube_r208", ModelPartBuilder.create().uv(28, 15).cuboid(7.5948F, -10.45F, 0.2638F, 3.0F, 10.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 2.45F, 0.0F, 0.0F, 0.48F, 0.0F));

        ModelPartData cube_r209 = pillars6.addChild("cube_r209", ModelPartBuilder.create().uv(0, 100).cuboid(-5.0F, -13.0F, -1.0F, 5.0F, 13.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(20.0014F, -13.538F, 8.366F, 0.4899F, -0.1932F, -1.1492F));

        ModelPartData cube_r210 = pillars6.addChild("cube_r210", ModelPartBuilder.create().uv(63, 0).cuboid(-3.0F, -6.0F, -1.0F, 3.0F, 6.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(20.0383F, -7.5F, 8.387F, 0.0F, -0.48F, 0.0F));

        ModelPartData cube_r211 = pillars6.addChild("cube_r211", ModelPartBuilder.create().uv(100, 103).cuboid(0.0F, -13.0F, 0.0F, 4.0F, 13.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(20.5F, -7.5F, 7.5F, 0.4819F, 0.0851F, -1.7445F));

        ModelPartData cube_r212 = pillars6.addChild("cube_r212", ModelPartBuilder.create().uv(101, 71).cuboid(-5.0F, -13.0F, -1.0F, 5.0F, 13.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(20.5F, -13.5F, 10.5F, 0.4899F, -0.1932F, -1.1492F));

        ModelPartData cube_r213 = pillars6.addChild("cube_r213", ModelPartBuilder.create().uv(51, 96).cuboid(0.0F, -13.0F, -1.0F, 4.0F, 13.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(20.5F, -7.5F, 10.5F, 0.4835F, 0.116F, -1.7282F));

        ModelPartData cube_r214 = pillars6.addChild("cube_r214", ModelPartBuilder.create().uv(28, 45).cuboid(-1.0F, 0.0F, 0.0F, 1.0F, 6.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(10.5948F, 0.6969F, 1.8362F, 0.4784F, -0.0403F, -1.2752F));

        ModelPartData cube_r215 = pillars6.addChild("cube_r215", ModelPartBuilder.create().uv(63, 30).cuboid(-1.0F, 0.0F, -3.0F, 1.0F, 6.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(10.5948F, 0.6969F, -1.8362F, -0.4784F, 0.0403F, -1.2752F));

        ModelPartData cube_r216 = pillars6.addChild("cube_r216", ModelPartBuilder.create().uv(82, 101).cuboid(0.0F, -13.0F, -3.0F, 1.0F, 13.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(20.5F, -7.5F, 10.5F, 0.48F, 0.0F, -1.789F));

        ModelPartData cube_r217 = pillars6.addChild("cube_r217", ModelPartBuilder.create().uv(106, 40).cuboid(0.0F, -13.0F, -1.0F, 4.0F, 13.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(20.5F, -7.5F, -7.5F, -0.4812F, -0.0697F, -1.7526F));

        ModelPartData cube_r218 = pillars6.addChild("cube_r218", ModelPartBuilder.create().uv(63, 15).cuboid(-3.0F, -6.0F, 0.0F, 3.0F, 6.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(20.0383F, -7.5F, -8.387F, 0.0F, 0.48F, 0.0F));

        ModelPartData cube_r219 = pillars6.addChild("cube_r219", ModelPartBuilder.create().uv(21, 93).cuboid(-6.0F, -13.0F, 0.0F, 6.0F, 13.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(20.0014F, -13.538F, -8.366F, -0.4899F, 0.1932F, -1.1492F));

        ModelPartData cube_r220 = pillars6.addChild("cube_r220", ModelPartBuilder.create().uv(108, 11).cuboid(0.0F, -13.0F, 0.0F, 4.0F, 13.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(20.5F, -7.5F, -10.5F, -0.4835F, -0.116F, -1.7282F));

        ModelPartData cube_r221 = pillars6.addChild("cube_r221", ModelPartBuilder.create().uv(13, 105).cuboid(0.0F, -13.0F, 0.0F, 1.0F, 13.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(20.5F, -7.5F, -10.5F, -0.48F, 0.0F, -1.789F));

        ModelPartData cube_r222 = pillars6.addChild("cube_r222", ModelPartBuilder.create().uv(224, 191).cuboid(-0.975F, -4.3F, -0.9F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(91, 101).cuboid(-1.0F, -13.0F, -3.0F, 1.0F, 13.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(20.5F, -13.5F, 10.5F, 0.48F, 0.0F, -1.0472F));

        ModelPartData cube_r223 = pillars6.addChild("cube_r223", ModelPartBuilder.create().uv(36, 96).cuboid(-6.0F, -13.0F, 0.0F, 6.0F, 13.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(20.5F, -13.5F, -10.5F, -0.4899F, 0.1932F, -1.1492F));

        ModelPartData cube_r224 = pillars6.addChild("cube_r224", ModelPartBuilder.create().uv(72, 48).cuboid(-3.0F, -6.0F, -1.0F, 3.0F, 6.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(20.5F, -7.5F, 10.5F, 0.0F, -0.48F, 0.0F));

        ModelPartData cube_r225 = pillars6.addChild("cube_r225", ModelPartBuilder.create().uv(91, 0).cuboid(-3.0F, -6.0F, 0.0F, 3.0F, 6.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(20.5F, -7.5F, -10.5F, 0.0F, 0.48F, 0.0F));

        ModelPartData cube_r226 = pillars6.addChild("cube_r226", ModelPartBuilder.create().uv(215, 185).cuboid(2.6F, -1.25F, -9.0F, 1.0F, 1.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(19.852F, -8.3092F, 0.0F, 0.0F, 0.0F, -0.7418F));

        ModelPartData bone152 = pillars6.addChild("bone152", ModelPartBuilder.create(), ModelTransform.pivot(11.0761F, -22.7756F, -3.2F));

        ModelPartData cube_r227 = bone152.addChild("cube_r227", ModelPartBuilder.create().uv(216, 39).cuboid(-4.0F, -3.5F, 1.8F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(220, 43).cuboid(-4.5F, -3.0F, 1.3F, 2.0F, 1.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(-2.982F, 4.4225F, -2.3F, 0.0F, 0.0F, 1.309F));

        ModelPartData cube_r228 = bone152.addChild("cube_r228", ModelPartBuilder.create().uv(212, 25).cuboid(2.5F, -0.5F, -1.0F, 0.0F, 1.0F, 2.0F, new Dilation(0.0F))
                .uv(212, 25).cuboid(-2.5F, -0.5F, -1.0F, 0.0F, 1.0F, 2.0F, new Dilation(0.0F))
                .uv(216, 27).cuboid(-2.5F, -0.5F, -1.0F, 5.0F, 1.0F, 0.0F, new Dilation(0.0F))
                .uv(216, 27).cuboid(-2.5F, -0.5F, 1.0F, 5.0F, 1.0F, 0.0F, new Dilation(0.0F))
                .uv(212, 25).cuboid(-2.5F, 0.5F, -1.0F, 5.0F, 0.0F, 2.0F, new Dilation(0.0F))
                .uv(213, 20).cuboid(-2.5F, 0.0F, -0.75F, 5.0F, 0.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.309F));

        ModelPartData bone153 = pillars6.addChild("bone153", ModelPartBuilder.create(), ModelTransform.pivot(11.0761F, -22.7756F, 3.2F));

        ModelPartData cube_r229 = bone153.addChild("cube_r229", ModelPartBuilder.create().uv(216, 39).cuboid(-4.0F, -3.5F, 1.8F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(220, 43).cuboid(-4.5F, -3.0F, 1.3F, 2.0F, 1.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(-2.982F, 4.4225F, -2.3F, 0.0F, 0.0F, 1.309F));

        ModelPartData cube_r230 = bone153.addChild("cube_r230", ModelPartBuilder.create().uv(216, 27).cuboid(-2.5F, -0.5F, 1.0F, 5.0F, 1.0F, 0.0F, new Dilation(0.0F))
                .uv(216, 27).cuboid(-2.5F, -0.5F, -1.0F, 5.0F, 1.0F, 0.0F, new Dilation(0.0F))
                .uv(212, 25).cuboid(2.5F, -0.5F, -1.0F, 0.0F, 1.0F, 2.0F, new Dilation(0.0F))
                .uv(212, 25).cuboid(-2.5F, -0.5F, -1.0F, 0.0F, 1.0F, 2.0F, new Dilation(0.0F))
                .uv(212, 25).cuboid(-2.5F, 0.5F, -1.0F, 5.0F, 0.0F, 2.0F, new Dilation(0.0F))
                .uv(213, 20).cuboid(-2.5F, 0.0F, -0.75F, 5.0F, 0.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.309F));

        ModelPartData bone147 = pillars6.addChild("bone147", ModelPartBuilder.create(), ModelTransform.pivot(9.8F, -20.9F, 0.0F));

        ModelPartData bone146 = bone147.addChild("bone146", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData bone145 = bone147.addChild("bone145", ModelPartBuilder.create(), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -1.5708F));

        ModelPartData bottom = copper.addChild("bottom", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData rotor = bottom.addChild("rotor", ModelPartBuilder.create().uv(67, 98).cuboid(4.2F, -9.0F, -3.0F, 1.0F, 7.0F, 6.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -6.0F, 0.0F));

        ModelPartData base = bottom.addChild("base", ModelPartBuilder.create().uv(28, 45).cuboid(-0.35F, -8.0F, -5.0F, 9.0F, 10.0F, 10.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData panels7 = bottom.addChild("panels7", ModelPartBuilder.create().uv(70, 72).cuboid(8.0F, -11.0F, -2.0F, 11.0F, 2.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.5236F, 0.0F));

        ModelPartData cube_r231 = panels7.addChild("cube_r231", ModelPartBuilder.create().uv(0, 63).cuboid(-1.0F, 0.0F, -7.0F, 1.0F, 11.0F, 14.0F, new Dilation(0.0F)), ModelTransform.of(16.4545F, -8.0F, 9.5F, -0.5087F, -0.1298F, 1.3428F));

        ModelPartData cube_r232 = panels7.addChild("cube_r232", ModelPartBuilder.create().uv(76, 27).cuboid(-11.0F, -0.999F, -4.0F, 11.0F, 1.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(19.0F, -9.0F, 2.0F, 0.0F, 0.0F, -0.2182F));

        ModelPartData cube_r233 = panels7.addChild("cube_r233", ModelPartBuilder.create().uv(76, 33).cuboid(-11.0F, -2.0F, -4.0F, 11.0F, 1.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(18.3156F, -9.1197F, 2.0F, 0.0F, 0.0F, 0.3491F));

        ModelPartData bottom2 = bottom.addChild("bottom2", ModelPartBuilder.create(), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData rotor2 = bottom2.addChild("rotor2", ModelPartBuilder.create().uv(67, 98).cuboid(4.2F, -9.0F, -3.0F, 1.0F, 7.0F, 6.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -6.0F, 0.0F));

        ModelPartData base2 = bottom2.addChild("base2", ModelPartBuilder.create().uv(28, 45).cuboid(-0.35F, -8.0F, -5.0F, 9.0F, 10.0F, 10.0F, new Dilation(0.0F))
                .uv(125, 94).cuboid(8.25F, -6.0F, -2.0F, 2.0F, 3.0F, 4.0F, new Dilation(0.0F))
                .uv(122, 116).cuboid(8.65F, -4.0F, -1.5F, 2.0F, 1.0F, 3.0F, new Dilation(-0.3F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData panels2 = bottom2.addChild("panels2", ModelPartBuilder.create().uv(70, 72).cuboid(8.0F, -11.0F, -2.0F, 11.0F, 2.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.5236F, 0.0F));

        ModelPartData cube_r234 = panels2.addChild("cube_r234", ModelPartBuilder.create().uv(0, 63).cuboid(-1.0F, 0.0F, -7.0F, 1.0F, 11.0F, 14.0F, new Dilation(0.0F)), ModelTransform.of(16.4545F, -8.0F, 9.5F, -0.5087F, -0.1298F, 1.3428F));

        ModelPartData cube_r235 = panels2.addChild("cube_r235", ModelPartBuilder.create().uv(76, 27).cuboid(-11.0F, -0.999F, -4.0F, 11.0F, 1.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(19.0F, -9.0F, 2.0F, 0.0F, 0.0F, -0.2182F));

        ModelPartData cube_r236 = panels2.addChild("cube_r236", ModelPartBuilder.create().uv(76, 33).cuboid(-11.0F, -2.0F, -4.0F, 11.0F, 1.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(18.3156F, -9.1197F, 2.0F, 0.0F, 0.0F, 0.3491F));

        ModelPartData bottom3 = bottom2.addChild("bottom3", ModelPartBuilder.create(), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData rotor3 = bottom3.addChild("rotor3", ModelPartBuilder.create().uv(67, 98).cuboid(4.2F, -9.0F, -3.0F, 1.0F, 7.0F, 6.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -6.0F, 0.0F));

        ModelPartData base3 = bottom3.addChild("base3", ModelPartBuilder.create().uv(28, 45).cuboid(-0.35F, -8.0F, -5.0F, 9.0F, 10.0F, 10.0F, new Dilation(0.0F))
                .uv(117, 60).cuboid(9.65F, 2.0F, -1.0F, 6.0F, 0.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData cube_r237 = base3.addChild("cube_r237", ModelPartBuilder.create().uv(62, 43).cuboid(-3.0F, 0.0F, -1.0F, 6.0F, 0.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(12.7209F, 1.3507F, 0.0F, 0.0F, 0.0F, 0.2182F));

        ModelPartData panels3 = bottom3.addChild("panels3", ModelPartBuilder.create().uv(70, 72).cuboid(8.0F, -11.0F, -2.0F, 11.0F, 2.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.5236F, 0.0F));

        ModelPartData cube_r238 = panels3.addChild("cube_r238", ModelPartBuilder.create().uv(0, 63).cuboid(-1.0F, 0.0F, -7.0F, 1.0F, 11.0F, 14.0F, new Dilation(0.0F)), ModelTransform.of(16.4545F, -8.0F, 9.5F, -0.5087F, -0.1298F, 1.3428F));

        ModelPartData cube_r239 = panels3.addChild("cube_r239", ModelPartBuilder.create().uv(76, 27).cuboid(-11.0F, -0.999F, -4.0F, 11.0F, 1.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(19.0F, -9.0F, 2.0F, 0.0F, 0.0F, -0.2182F));

        ModelPartData cube_r240 = panels3.addChild("cube_r240", ModelPartBuilder.create().uv(76, 33).cuboid(-11.0F, -2.0F, -4.0F, 11.0F, 1.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(18.3156F, -9.1197F, 2.0F, 0.0F, 0.0F, 0.3491F));

        ModelPartData bottom4 = bottom3.addChild("bottom4", ModelPartBuilder.create(), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData rotor4 = bottom4.addChild("rotor4", ModelPartBuilder.create().uv(67, 98).cuboid(4.2F, -9.0F, -3.0F, 1.0F, 7.0F, 6.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -6.0F, 0.0F));

        ModelPartData base4 = bottom4.addChild("base4", ModelPartBuilder.create().uv(28, 45).cuboid(-0.35F, -8.0F, -5.0F, 9.0F, 10.0F, 10.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData panels4 = bottom4.addChild("panels4", ModelPartBuilder.create().uv(70, 72).cuboid(8.0F, -11.0F, -2.0F, 11.0F, 2.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.5236F, 0.0F));

        ModelPartData cube_r241 = panels4.addChild("cube_r241", ModelPartBuilder.create().uv(0, 63).cuboid(-1.0F, 0.0F, -7.0F, 1.0F, 11.0F, 14.0F, new Dilation(0.0F)), ModelTransform.of(16.4545F, -8.0F, 9.5F, -0.5087F, -0.1298F, 1.3428F));

        ModelPartData cube_r242 = panels4.addChild("cube_r242", ModelPartBuilder.create().uv(76, 27).cuboid(-11.0F, -0.999F, -4.0F, 11.0F, 1.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(19.0F, -9.0F, 2.0F, 0.0F, 0.0F, -0.2182F));

        ModelPartData cube_r243 = panels4.addChild("cube_r243", ModelPartBuilder.create().uv(76, 33).cuboid(-11.0F, -2.0F, -4.0F, 11.0F, 1.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(18.3156F, -9.1197F, 2.0F, 0.0F, 0.0F, 0.3491F));

        ModelPartData bottom5 = bottom4.addChild("bottom5", ModelPartBuilder.create(), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData rotor5 = bottom5.addChild("rotor5", ModelPartBuilder.create().uv(67, 98).cuboid(4.2F, -9.0F, -3.0F, 1.0F, 7.0F, 6.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -6.0F, 0.0F));

        ModelPartData base5 = bottom5.addChild("base5", ModelPartBuilder.create().uv(28, 45).cuboid(-0.35F, -8.0F, -5.0F, 9.0F, 10.0F, 10.0F, new Dilation(0.0F))
                .uv(68, 78).cuboid(10.15F, 1.0F, -1.0F, 4.0F, 1.0F, 2.0F, new Dilation(0.4F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData cube_r244 = base5.addChild("cube_r244", ModelPartBuilder.create().uv(16, 74).cuboid(-2.0F, -0.5F, -1.0F, 4.0F, 1.0F, 2.0F, new Dilation(0.35F)), ModelTransform.of(11.9988F, 0.8482F, 0.0F, 0.0F, 0.0F, 0.2618F));

        ModelPartData panels5 = bottom5.addChild("panels5", ModelPartBuilder.create().uv(70, 72).cuboid(8.0F, -11.0F, -2.0F, 11.0F, 2.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.5236F, 0.0F));

        ModelPartData cube_r245 = panels5.addChild("cube_r245", ModelPartBuilder.create().uv(0, 63).cuboid(-1.0F, 0.0F, -7.0F, 1.0F, 11.0F, 14.0F, new Dilation(0.0F)), ModelTransform.of(16.4545F, -8.0F, 9.5F, -0.5087F, -0.1298F, 1.3428F));

        ModelPartData cube_r246 = panels5.addChild("cube_r246", ModelPartBuilder.create().uv(76, 27).cuboid(-11.0F, -0.999F, -4.0F, 11.0F, 1.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(19.0F, -9.0F, 2.0F, 0.0F, 0.0F, -0.2182F));

        ModelPartData cube_r247 = panels5.addChild("cube_r247", ModelPartBuilder.create().uv(76, 33).cuboid(-11.0F, -2.0F, -4.0F, 11.0F, 1.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(18.3156F, -9.1197F, 2.0F, 0.0F, 0.0F, 0.3491F));

        ModelPartData bottom6 = bottom5.addChild("bottom6", ModelPartBuilder.create(), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData rotor6 = bottom6.addChild("rotor6", ModelPartBuilder.create().uv(67, 98).cuboid(4.2F, -9.0F, -3.0F, 1.0F, 7.0F, 6.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -6.0F, 0.0F));

        ModelPartData base6 = bottom6.addChild("base6", ModelPartBuilder.create().uv(28, 45).cuboid(-0.35F, -8.0F, -5.0F, 9.0F, 10.0F, 10.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData pump3 = base6.addChild("pump3", ModelPartBuilder.create().uv(117, 60).cuboid(9.65F, 2.0F, -1.0F, 6.0F, 0.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData cube_r248 = pump3.addChild("cube_r248", ModelPartBuilder.create().uv(56, 43).mirrored().cuboid(-3.0F, 0.0F, -1.0F, 6.0F, 0.0F, 2.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(12.7209F, 1.3507F, 0.0F, 3.1416F, 0.0F, 0.2182F));

        ModelPartData panels6 = bottom6.addChild("panels6", ModelPartBuilder.create().uv(70, 72).cuboid(8.0F, -11.0F, -2.0F, 11.0F, 2.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.5236F, 0.0F));

        ModelPartData cube_r249 = panels6.addChild("cube_r249", ModelPartBuilder.create().uv(0, 63).cuboid(-1.0F, 0.0F, -7.0F, 1.0F, 11.0F, 14.0F, new Dilation(0.0F)), ModelTransform.of(16.4545F, -8.0F, 9.5F, -0.5087F, -0.1298F, 1.3428F));

        ModelPartData cube_r250 = panels6.addChild("cube_r250", ModelPartBuilder.create().uv(76, 27).cuboid(-11.0F, -0.999F, -4.0F, 11.0F, 1.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(19.0F, -9.0F, 2.0F, 0.0F, 0.0F, -0.2182F));

        ModelPartData cube_r251 = panels6.addChild("cube_r251", ModelPartBuilder.create().uv(76, 33).cuboid(-11.0F, -2.0F, -4.0F, 11.0F, 1.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(18.3156F, -9.1197F, 2.0F, 0.0F, 0.0F, 0.3491F));

        ModelPartData bone144 = bottom5.addChild("bone144", ModelPartBuilder.create().uv(192, 120).cuboid(21.2F, -11.2F, -2.0F, 1.0F, 1.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.5236F, 0.0F));

        ModelPartData cube_r252 = bone144.addChild("cube_r252", ModelPartBuilder.create().uv(184, 113).cuboid(-0.2172F, -0.2172F, -2.0F, 1.0F, 1.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(21.7F, -10.5F, 0.0F, 0.0F, 0.0F, 0.7854F));

        ModelPartData bone155 = bottom5.addChild("bone155", ModelPartBuilder.create(), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.5236F, 0.0F));

        ModelPartData bone154 = bone155.addChild("bone154", ModelPartBuilder.create().uv(219, 174).cuboid(1.0F, -2.2F, -1.1F, 1.0F, 6.0F, 1.0F, new Dilation(-0.1F))
                .uv(219, 162).cuboid(1.0F, -2.9F, -1.1F, 1.0F, 2.0F, 1.0F, new Dilation(-0.3F))
                .uv(219, 174).cuboid(1.0F, -4.3F, -1.1F, 1.0F, 2.0F, 1.0F, new Dilation(-0.1F)), ModelTransform.of(14.0F, -16.0F, 0.0F, 0.0F, 0.0F, 0.48F));

        ModelPartData bone156 = bone155.addChild("bone156", ModelPartBuilder.create().uv(219, 174).cuboid(1.0F, -2.2F, -1.1F, 1.0F, 6.0F, 1.0F, new Dilation(-0.1F))
                .uv(219, 162).cuboid(1.0F, -2.9F, -1.1F, 1.0F, 2.0F, 1.0F, new Dilation(-0.3F))
                .uv(219, 174).cuboid(1.0F, -4.3F, -1.1F, 1.0F, 2.0F, 1.0F, new Dilation(-0.1F)), ModelTransform.of(14.0F, -16.0F, 1.2F, 0.0F, 0.0F, 0.48F));

        ModelPartData bone160 = bone155.addChild("bone160", ModelPartBuilder.create().uv(223, 169).cuboid(4.9F, -2.7F, -1.2F, 1.0F, 3.0F, 1.0F, new Dilation(0.0F))
                .uv(223, 169).cuboid(4.9F, -3.3F, -1.2F, 1.0F, 3.0F, 1.0F, new Dilation(-0.2F))
                .uv(235, 192).cuboid(5.15F, -4.675F, -0.675F, 1.0F, 2.0F, 0.0F, new Dilation(0.0F))
                .uv(217, 166).cuboid(4.9F, -1.425F, -1.2F, 1.0F, 1.0F, 1.0F, new Dilation(0.025F))
                .uv(215, 158).cuboid(3.9F, -1.425F, -2.175F, 3.0F, 1.0F, 1.0F, new Dilation(0.025F)), ModelTransform.of(14.0F, -16.0F, 0.0F, 0.0F, 0.0F, 0.4363F));

        ModelPartData bone161 = bone160.addChild("bone161", ModelPartBuilder.create().uv(232, 186).cuboid(4.15F, -4.675F, -0.675F, 2.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(235, 178).cuboid(4.15F, -5.175F, -0.675F, 2.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(226, 180).cuboid(4.15F, -4.675F, -0.175F, 2.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(0.25F, 0.0F, -0.75F));

        ModelPartData bone162 = bone160.addChild("bone162", ModelPartBuilder.create().uv(232, 186).cuboid(-1.0F, -0.3333F, -0.6667F, 2.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(235, 178).cuboid(-1.0F, -0.8333F, -0.6667F, 2.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(226, 180).cuboid(-1.0F, -0.3333F, -0.1667F, 2.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(5.9126F, -1.9417F, -0.3164F, 0.0F, -0.7854F, 0.0F));

        ModelPartData controls = copper.addChild("controls", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData panel_1 = controls.addChild("panel_1", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData rot7 = panel_1.addChild("rot7", ModelPartBuilder.create(), ModelTransform.of(20.0F, -13.0F, 0.0F, 0.0F, 0.0F, 0.5672F));

        ModelPartData atomic_acc = rot7.addChild("atomic_acc", ModelPartBuilder.create().uv(55, 117).cuboid(-1.0F, -1.0F, -1.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F))
                .uv(67, 125).cuboid(-0.5F, -1.25F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-4.0482F, 0.2624F, 0.0F, 0.0F, 0.0F, -0.5236F));

        ModelPartData bone2 = atomic_acc.addChild("bone2", ModelPartBuilder.create().uv(125, 24).cuboid(-0.5F, -1.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(119, 25).cuboid(-0.5F, -1.5F, -1.5F, 1.0F, 1.0F, 2.0F, new Dilation(-0.3F)), ModelTransform.pivot(0.0F, -1.0F, 0.0F));

        ModelPartData cube_r253 = bone2.addChild("cube_r253", ModelPartBuilder.create().uv(119, 17).cuboid(-0.5F, -0.5F, -1.5F, 1.0F, 1.0F, 2.0F, new Dilation(-0.3F)), ModelTransform.of(0.0F, -1.0F, 0.0F, 1.5708F, 0.0F, 0.0F));

        ModelPartData cube_r254 = bone2.addChild("cube_r254", ModelPartBuilder.create().uv(119, 21).cuboid(-0.5F, -0.5F, -1.5F, 1.0F, 1.0F, 2.0F, new Dilation(-0.3F)), ModelTransform.of(0.0F, -1.0F, 0.0F, 1.5708F, 0.0F, 3.1416F));

        ModelPartData cube_r255 = bone2.addChild("cube_r255", ModelPartBuilder.create().uv(119, 25).cuboid(-0.5F, -0.5F, -1.5F, 1.0F, 1.0F, 2.0F, new Dilation(-0.3F)), ModelTransform.of(0.0F, -1.0F, 0.0F, 0.0F, 3.1416F, 0.0F));

        ModelPartData cube_r256 = bone2.addChild("cube_r256", ModelPartBuilder.create().uv(119, 21).cuboid(-0.5F, -0.5F, -1.5F, 1.0F, 1.0F, 2.0F, new Dilation(-0.3F)), ModelTransform.of(0.0F, -1.0F, 0.0F, 0.0F, 1.5708F, 0.0F));

        ModelPartData cube_r257 = bone2.addChild("cube_r257", ModelPartBuilder.create().uv(119, 21).cuboid(-0.5F, -0.5F, -1.5F, 1.0F, 1.0F, 2.0F, new Dilation(-0.3F)), ModelTransform.of(0.0F, -1.0F, 0.0F, 0.0F, -1.5708F, 0.0F));

        ModelPartData button = rot7.addChild("button", ModelPartBuilder.create().uv(22, 125).cuboid(-0.5F, -1.0F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.1F)), ModelTransform.pivot(-1.75F, 0.6F, 0.0F));

        ModelPartData bone3 = button.addChild("bone3", ModelPartBuilder.create().uv(125, 16).cuboid(-0.5F, -1.55F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.2F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData button2 = rot7.addChild("button2", ModelPartBuilder.create().uv(9, 125).cuboid(-0.5F, -1.0F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(115, 124).cuboid(-0.5F, -1.4F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.2F)), ModelTransform.pivot(-2.25F, 0.5F, -2.0F));

        ModelPartData bone4 = button2.addChild("bone4", ModelPartBuilder.create().uv(125, 108).cuboid(-0.5F, -1.75F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.4F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData keyboard = rot7.addChild("keyboard", ModelPartBuilder.create().uv(23, 83).cuboid(-2.0F, -1.0F, -4.0F, 5.0F, 1.0F, 8.0F, new Dilation(-0.3F))
                .uv(41, 0).cuboid(0.0F, -0.6F, 0.0F, 1.0F, 4.0F, 0.0F, new Dilation(-0.3F))
                .uv(72, 89).cuboid(-1.5F, -1.25F, -3.5F, 4.0F, 1.0F, 7.0F, new Dilation(-0.2F))
                .uv(88, 62).cuboid(-1.5F, -1.55F, -3.5F, 4.0F, 1.0F, 7.0F, new Dilation(-0.4F))
                .uv(17, 66).cuboid(-1.5F, -1.65F, -3.5F, 4.0F, 1.0F, 7.0F, new Dilation(-0.4F)), ModelTransform.pivot(3.35F, 2.5F, 0.0F));

        ModelPartData cube_r258 = keyboard.addChild("cube_r258", ModelPartBuilder.create().uv(41, 0).cuboid(0.0F, -0.7F, 0.0F, 1.0F, 4.0F, 0.0F, new Dilation(-0.3F)), ModelTransform.of(-2.3301F, 2.0173F, 0.0F, 0.0F, 0.0F, -1.0036F));

        ModelPartData cube_r259 = keyboard.addChild("cube_r259", ModelPartBuilder.create().uv(41, 2).cuboid(-1.8F, 2.4F, 0.0F, 1.0F, 1.0F, 0.0F, new Dilation(-0.25F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.5672F));

        ModelPartData cash_reg = rot7.addChild("cash_reg", ModelPartBuilder.create().uv(53, 83).cuboid(-0.9F, -2.0F, -1.0F, 1.0F, 2.0F, 2.0F, new Dilation(0.3F))
                .uv(105, 124).cuboid(-0.2F, -0.7F, -1.4F, 1.0F, 1.0F, 1.0F, new Dilation(-0.3F)), ModelTransform.of(2.0F, 2.0F, -9.0F, 0.0F, 0.0F, -0.5672F));

        ModelPartData cube_r260 = cash_reg.addChild("cube_r260", ModelPartBuilder.create().uv(119, 13).cuboid(-0.15F, -1.25F, -1.0F, 1.0F, 1.0F, 2.0F, new Dilation(-0.2F)), ModelTransform.of(-0.5F, -0.5F, 0.0F, 0.0F, 0.0F, 0.7854F));

        ModelPartData pump = rot7.addChild("pump", ModelPartBuilder.create().uv(117, 44).cuboid(-1.0F, -1.0F, -1.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.9661F, 2.03F, -5.0F, 0.0F, -1.0036F, -0.5672F));

        ModelPartData bone5 = pump.addChild("bone5", ModelPartBuilder.create().uv(117, 39).cuboid(-0.5F, -0.5F, -2.5F, 1.0F, 1.0F, 3.0F, new Dilation(-0.3F))
                .uv(13, 100).cuboid(-0.5F, -0.5F, -2.3F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData pump2 = rot7.addChild("pump2", ModelPartBuilder.create().uv(117, 44).cuboid(-1.0F, -1.0F, -1.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.9661F, 2.03F, 5.0F, 0.0F, -0.5672F, -0.5672F));

        ModelPartData bone6 = pump2.addChild("bone6", ModelPartBuilder.create().uv(117, 39).cuboid(-0.5F, -0.5F, -2.5F, 1.0F, 1.0F, 3.0F, new Dilation(-0.3F))
                .uv(13, 100).cuboid(-0.5F, -0.5F, -2.3F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.5708F, 0.0F));

        ModelPartData lever = rot7.addChild("lever", ModelPartBuilder.create().uv(118, 78).cuboid(-1.0F, -2.0F, -1.0F, 2.0F, 1.0F, 2.0F, new Dilation(0.1F))
                .uv(54, 125).cuboid(0.85F, -1.9F, -1.1F, 1.0F, 1.0F, 1.0F, new Dilation(-0.2F))
                .uv(54, 125).cuboid(0.85F, -1.9F, 0.1F, 1.0F, 1.0F, 1.0F, new Dilation(-0.2F))
                .uv(59, 125).cuboid(-1.9F, -1.7F, -1.25F, 1.0F, 1.0F, 1.0F, new Dilation(-0.2F))
                .uv(59, 125).cuboid(-1.9F, -1.7F, 0.25F, 1.0F, 1.0F, 1.0F, new Dilation(-0.2F)), ModelTransform.of(-3.5F, 0.5F, -6.9F, 0.0F, 0.48F, 0.0F));

        ModelPartData bone7 = lever.addChild("bone7", ModelPartBuilder.create().uv(0, 63).cuboid(-0.75F, -0.75F, 0.0F, 1.0F, 1.0F, 0.0F, new Dilation(0.001F))
                .uv(100, 124).cuboid(-0.5F, -0.5F, -0.65F, 1.0F, 1.0F, 1.0F, new Dilation(-0.3F))
                .uv(95, 124).cuboid(-0.5F, -3.0F, -0.9F, 1.0F, 1.0F, 1.0F, new Dilation(-0.2F)), ModelTransform.of(0.5F, -1.5F, 1.15F, 0.0F, 0.0F, 0.3927F));

        ModelPartData cube_r261 = bone7.addChild("cube_r261", ModelPartBuilder.create().uv(38, 0).cuboid(-1.0F, -2.0F, -0.001F, 1.0F, 2.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(0.25F, -0.75F, 0.001F, 0.2618F, 0.0F, 0.0F));

        ModelPartData bone8 = lever.addChild("bone8", ModelPartBuilder.create().uv(0, 63).cuboid(-0.75F, -0.75F, 0.0F, 1.0F, 1.0F, 0.0F, new Dilation(0.001F))
                .uv(95, 124).cuboid(-0.5F, -3.0F, -0.1F, 1.0F, 1.0F, 1.0F, new Dilation(-0.2F))
                .uv(100, 124).cuboid(-0.5F, -0.5F, -0.35F, 1.0F, 1.0F, 1.0F, new Dilation(-0.3F)), ModelTransform.of(0.5F, -1.5F, -1.15F, 0.0F, 0.0F, 0.3927F));

        ModelPartData cube_r262 = bone8.addChild("cube_r262", ModelPartBuilder.create().uv(38, 0).cuboid(-1.0F, -2.0F, -0.001F, 1.0F, 2.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(0.25F, -0.75F, 0.001F, -0.2618F, 0.0F, 0.0F));

        ModelPartData bell = rot7.addChild("bell", ModelPartBuilder.create().uv(28, 40).cuboid(-2.0F, -2.0F, -1.0F, 4.0F, 1.0F, 2.0F, new Dilation(0.0F))
                .uv(104, 63).cuboid(-7.75F, -1.5F, -1.8F, 6.0F, 0.0F, 3.0F, new Dilation(0.0F))
                .uv(103, 26).cuboid(-7.75F, -1.8F, -1.8F, 6.0F, 0.0F, 3.0F, new Dilation(0.0F))
                .uv(63, 27).cuboid(-5.0F, -1.55F, -1.4F, 2.0F, 0.0F, 1.0F, new Dilation(0.0F))
                .uv(72, 125).cuboid(-5.1F, -2.25F, -1.4F, 1.0F, 1.0F, 1.0F, new Dilation(-0.3F))
                .uv(72, 125).cuboid(-4.5F, -2.25F, -1.4F, 1.0F, 1.0F, 1.0F, new Dilation(-0.3F))
                .uv(72, 125).cuboid(-3.9F, -2.25F, -1.4F, 1.0F, 1.0F, 1.0F, new Dilation(-0.3F))
                .uv(72, 125).cuboid(-4.5F, -2.25F, -0.3F, 1.0F, 1.0F, 1.0F, new Dilation(-0.3F))
                .uv(72, 125).cuboid(-5.1F, -2.25F, -0.3F, 1.0F, 1.0F, 1.0F, new Dilation(-0.3F))
                .uv(72, 125).cuboid(-3.9F, -2.25F, -0.3F, 1.0F, 1.0F, 1.0F, new Dilation(-0.3F))
                .uv(63, 27).cuboid(-5.0F, -1.55F, -0.3F, 2.0F, 0.0F, 1.0F, new Dilation(0.0F))
                .uv(49, 125).cuboid(-6.05F, -2.15F, -0.3F, 1.0F, 1.0F, 1.0F, new Dilation(-0.1F))
                .uv(88, 101).cuboid(2.45F, -1.85F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(8, 63).cuboid(2.45F, -2.05F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.1F))
                .uv(36, 30).cuboid(2.7F, -2.25F, -0.25F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(67, 101).cuboid(1.7F, -1.85F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.2F))
                .uv(118, 57).cuboid(-2.0F, -2.5F, -1.0F, 2.0F, 1.0F, 2.0F, new Dilation(-0.2F))
                .uv(118, 57).cuboid(0.0F, -2.5F, -1.0F, 2.0F, 1.0F, 2.0F, new Dilation(-0.2F)), ModelTransform.of(-5.0F, 0.75F, 6.1F, 0.0227F, -0.4795F, -0.0492F));

        ModelPartData bone9 = bell.addChild("bone9", ModelPartBuilder.create().uv(79, 84).cuboid(-1.65F, -0.1F, -0.65F, 2.0F, 1.0F, 1.0F, new Dilation(-0.2F)), ModelTransform.of(-1.0F, -2.5F, 0.0F, 0.0F, -0.1745F, 0.0F));

        ModelPartData bone10 = bell.addChild("bone10", ModelPartBuilder.create().uv(79, 84).cuboid(-1.65F, -0.1F, -0.65F, 2.0F, 1.0F, 1.0F, new Dilation(-0.2F)), ModelTransform.of(1.0F, -2.5F, 0.0F, 0.0F, 1.2217F, 0.0F));

        ModelPartData bone11 = rot7.addChild("bone11", ModelPartBuilder.create().uv(98, 89).cuboid(-0.75F, -1.1F, -1.0F, 1.0F, 1.0F, 2.0F, new Dilation(0.1F))
                .uv(42, 88).cuboid(-3.05F, -1.55F, 0.5F, 2.0F, 1.0F, 1.0F, new Dilation(-0.2F))
                .uv(42, 88).cuboid(-2.25F, -1.55F, 1.1F, 2.0F, 1.0F, 1.0F, new Dilation(-0.2F))
                .uv(47, 120).cuboid(0.25F, -0.9F, -1.0F, 1.0F, 0.0F, 2.0F, new Dilation(0.0F))
                .uv(16, 89).cuboid(-0.75F, -1.35F, -1.0F, 1.0F, 1.0F, 2.0F, new Dilation(-0.1F))
                .uv(124, 86).cuboid(-2.15F, -1.15F, -0.55F, 1.0F, 1.0F, 1.0F, new Dilation(-0.2F))
                .uv(103, 11).cuboid(-0.75F, -2.55F, -1.0F, 1.0F, 2.0F, 1.0F, new Dilation(-0.35F))
                .uv(103, 11).cuboid(-0.75F, -2.55F, 0.0F, 1.0F, 2.0F, 1.0F, new Dilation(-0.35F))
                .uv(90, 124).cuboid(-0.75F, -2.55F, -1.0F, 1.0F, 1.0F, 1.0F, new Dilation(-0.3F))
                .uv(90, 124).cuboid(-0.75F, -2.55F, 0.0F, 1.0F, 1.0F, 1.0F, new Dilation(-0.3F)), ModelTransform.of(-7.0F, 0.5F, -5.1F, 0.0F, 0.0F, -0.0436F));

        ModelPartData bone12 = rot7.addChild("bone12", ModelPartBuilder.create().uv(79, 79).cuboid(-1.8107F, 0.2016F, -4.0F, 5.0F, 1.0F, 8.0F, new Dilation(0.0F))
                .uv(86, 48).cuboid(-1.8107F, 0.4016F, -4.0F, 5.0F, 0.0F, 8.0F, new Dilation(0.0F))
                .uv(85, 124).cuboid(-1.55F, -0.1F, -1.75F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-12.55F, -2.15F, 0.0F, 0.0F, 0.0F, 0.5236F));

        ModelPartData bow = bone12.addChild("bow", ModelPartBuilder.create().uv(81, 180).cuboid(0.0F, 0.5F, -1.75F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-3.3395F, -0.5538F, 1.0F, -1.7017F, 0.0F, 2.0508F));

        ModelPartData cube_r263 = bow.addChild("cube_r263", ModelPartBuilder.create().uv(21, 66).cuboid(0.0F, -0.5F, -3.25F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 1.0F, 1.5F, -0.6109F, 0.0F, 0.0F));

        ModelPartData cube_r264 = bow.addChild("cube_r264", ModelPartBuilder.create().uv(70, 207).cuboid(0.0F, -0.5F, -3.25F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 1.0F, 1.5F, 0.6109F, 0.0F, 0.0F));

        ModelPartData cube_r265 = bow.addChild("cube_r265", ModelPartBuilder.create().uv(78, 190).cuboid(0.0F, -0.5F, -3.25F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 1.0F, 1.5F, 0.3054F, 0.0F, 0.0F));

        ModelPartData cube_r266 = bow.addChild("cube_r266", ModelPartBuilder.create().uv(112, 142).cuboid(0.0F, -0.5F, -3.25F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 1.0F, 1.5F, -0.3054F, 0.0F, 0.0F));

        ModelPartData bone137 = bow.addChild("bone137", ModelPartBuilder.create(), ModelTransform.of(0.0F, 1.0F, 1.5F, 0.2182F, 0.0F, 0.0F));

        ModelPartData cube_r267 = bone137.addChild("cube_r267", ModelPartBuilder.create().uv(100, 213).cuboid(0.2F, -0.75F, -1.3F, 0.0F, 1.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -1.0F, -1.5F, -1.0036F, 0.0F, 0.0F));

        ModelPartData cube_r268 = bone137.addChild("cube_r268", ModelPartBuilder.create().uv(100, 213).cuboid(0.3F, -0.05F, -1.3F, 0.0F, 1.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -1.0F, -1.5F, -0.48F, 0.0F, 0.0F));

        ModelPartData cube_r269 = bone137.addChild("cube_r269", ModelPartBuilder.create().uv(84, 216).cuboid(0.125F, -0.45F, -1.7F, 0.0F, 1.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -1.0F, -1.5F, 1.1345F, 0.0F, 0.0F));

        ModelPartData cube_r270 = bone137.addChild("cube_r270", ModelPartBuilder.create().uv(100, 213).cuboid(0.1F, 0.65F, -2.1F, 0.0F, 1.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -1.0F, -1.5F, 0.1745F, 0.0F, 0.0F));

        ModelPartData cube_r271 = bone137.addChild("cube_r271", ModelPartBuilder.create().uv(100, 213).cuboid(0.1F, 1.35F, -2.1F, 0.0F, 1.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -1.0F, -1.5F, 0.4363F, 0.0F, 0.0F));

        ModelPartData cube_r272 = bone137.addChild("cube_r272", ModelPartBuilder.create().uv(72, 221).cuboid(-0.25F, -2.1F, 1.9F, 1.0F, 5.0F, 1.0F, new Dilation(0.0F))
                .uv(74, 207).cuboid(-0.25F, -0.1F, 1.0F, 1.0F, 1.0F, 1.0F, new Dilation(-0.01F)), ModelTransform.of(0.0F, -1.0F, -1.5F, -0.4363F, 0.0F, 0.0F));

        ModelPartData bone139 = bow.addChild("bone139", ModelPartBuilder.create(), ModelTransform.of(0.25F, 2.0F, -0.5F, -0.6109F, 0.0F, 0.0F));

        ModelPartData bone148 = bow.addChild("bone148", ModelPartBuilder.create(), ModelTransform.of(0.25F, 0.75F, 0.9F, 0.2182F, 0.0F, 0.0F));

        ModelPartData bone149 = bow.addChild("bone149", ModelPartBuilder.create(), ModelTransform.of(0.25F, 0.0F, -0.85F, 1.0472F, 0.0F, 0.0F));

        ModelPartData bone158 = bow.addChild("bone158", ModelPartBuilder.create(), ModelTransform.of(0.15F, 0.25F, -0.1F, 0.4363F, 0.0F, 0.0F));

        ModelPartData bone159 = bow.addChild("bone159", ModelPartBuilder.create(), ModelTransform.of(0.15F, 1.75F, 0.25F, -0.2618F, 0.0F, 0.0F));

        ModelPartData valve = rot7.addChild("valve", ModelPartBuilder.create().uv(80, 124).cuboid(-0.5F, -2.0F, 0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(17, 124).cuboid(0.25F, -2.0F, 0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.2F))
                .uv(76, 121).cuboid(-0.5F, -1.25F, 0.5F, 1.0F, 2.0F, 1.0F, new Dilation(-0.2F)), ModelTransform.of(-17.75F, -4.25F, 0.5F, 0.0F, 0.0F, -0.6981F));

        ModelPartData bone13 = valve.addChild("bone13", ModelPartBuilder.create().uv(120, 63).cuboid(-1.05F, 0.2071F, 0.2071F, 2.0F, 1.0F, 1.0F, new Dilation(-0.4F))
                .uv(124, 4).cuboid(-0.05F, 0.2071F, 0.2071F, 1.0F, 1.0F, 1.0F, new Dilation(-0.15F))
                .uv(100, 0).cuboid(0.7F, -0.5429F, -0.5429F, 0.0F, 2.0F, 2.0F, new Dilation(0.0F))
                .uv(125, 43).cuboid(0.15F, 0.2071F, 0.2071F, 1.0F, 1.0F, 1.0F, new Dilation(-0.25F)), ModelTransform.of(1.0F, -1.5F, 0.0F, 0.7854F, 0.0F, 0.0F));

        ModelPartData valve2 = rot7.addChild("valve2", ModelPartBuilder.create().uv(80, 124).cuboid(-0.5F, -2.3007F, 0.4537F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(17, 124).cuboid(0.25F, -2.3007F, 0.4537F, 1.0F, 1.0F, 1.0F, new Dilation(-0.2F))
                .uv(76, 121).cuboid(-0.5F, -1.5507F, 0.4537F, 1.0F, 2.0F, 1.0F, new Dilation(-0.2F)), ModelTransform.of(-17.5F, -4.0F, 2.0F, -0.3054F, 0.0F, -0.6981F));

        ModelPartData bone14 = valve2.addChild("bone14", ModelPartBuilder.create().uv(120, 63).cuboid(-1.05F, 0.1756F, 0.2373F, 2.0F, 1.0F, 1.0F, new Dilation(-0.4F))
                .uv(124, 4).cuboid(-0.05F, 0.1756F, 0.2373F, 1.0F, 1.0F, 1.0F, new Dilation(-0.15F))
                .uv(100, 0).cuboid(0.7F, -0.5744F, -0.5127F, 0.0F, 2.0F, 2.0F, new Dilation(0.0F))
                .uv(125, 43).cuboid(0.15F, 0.1756F, 0.2373F, 1.0F, 1.0F, 1.0F, new Dilation(-0.25F)), ModelTransform.of(1.0F, -1.5F, 0.0F, 1.0472F, 0.0F, 0.0F));

        ModelPartData cables2 = rot7.addChild("cables2", ModelPartBuilder.create().uv(0, 120).cuboid(-0.25F, 0.5F, -6.5F, 2.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(77, 12).cuboid(-1.5F, 1.0F, 5.25F, 4.0F, 0.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(1.0F, 8.25F, 0.0F, 0.0F, 0.0F, 2.3562F));

        ModelPartData cube_r273 = cables2.addChild("cube_r273", ModelPartBuilder.create().uv(100, 96).cuboid(-1.0F, 0.0F, 0.0F, 3.0F, 0.0F, 6.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, -0.1745F, 0.0F, 0.0F));

        ModelPartData cube_r274 = cables2.addChild("cube_r274", ModelPartBuilder.create().uv(101, 33).cuboid(-1.0F, 0.0F, -6.0F, 3.0F, 0.0F, 6.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.1745F, 0.0F, 0.0F));

        ModelPartData panel_2 = controls.addChild("panel_2", ModelPartBuilder.create(), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData rot8 = panel_2.addChild("rot8", ModelPartBuilder.create(), ModelTransform.of(20.0F, -13.0F, 0.0F, 0.0F, 0.0F, 0.5672F));

        ModelPartData cube_r275 = rot8.addChild("cube_r275", ModelPartBuilder.create().uv(57, 48).cuboid(0.1F, 1.5F, -2.0F, 0.0F, 2.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.5672F));

        ModelPartData lever2 = rot8.addChild("lever2", ModelPartBuilder.create().uv(76, 98).cuboid(-1.0F, -2.5F, -1.0F, 2.0F, 3.0F, 2.0F, new Dilation(0.0F))
                .uv(60, 75).cuboid(-1.0F, -2.5F, -1.0F, 2.0F, 3.0F, 2.0F, new Dilation(0.001F))
                .uv(117, 5).cuboid(-0.75F, -2.75F, -1.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.1F))
                .uv(74, 116).cuboid(-0.5F, -2.0F, -1.5F, 1.0F, 1.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(-5.5F, -0.35F, 5.75F, 0.0F, -0.48F, 0.0F));

        ModelPartData lights2 = lever2.addChild("lights2", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData light = lights2.addChild("light", ModelPartBuilder.create().uv(86, 52).cuboid(0.0F, 0.0F, -1.0F, 1.0F, 1.0F, 2.0F, new Dilation(0.03F)), ModelTransform.pivot(0.0F, -1.5F, 0.0F));

        ModelPartData light2 = lights2.addChild("light2", ModelPartBuilder.create().uv(86, 52).cuboid(0.0F, -1.0F, -1.0F, 1.0F, 1.0F, 2.0F, new Dilation(0.03F)), ModelTransform.pivot(0.0F, -1.5F, 0.0F));

        ModelPartData light3 = lights2.addChild("light3", ModelPartBuilder.create().uv(86, 52).cuboid(0.0F, 0.0F, -1.0F, 1.0F, 1.0F, 2.0F, new Dilation(0.03F)), ModelTransform.of(0.0F, -1.5F, 0.0F, 0.0F, 0.0F, -1.5708F));

        ModelPartData light4 = lights2.addChild("light4", ModelPartBuilder.create().uv(86, 52).cuboid(0.0F, -1.0F, -1.0F, 1.0F, 1.0F, 2.0F, new Dilation(0.03F)), ModelTransform.of(0.0F, -1.5F, 0.0F, 0.0F, 0.0F, -1.5708F));

        ModelPartData light5 = lights2.addChild("light5", ModelPartBuilder.create().uv(86, 52).cuboid(0.0F, 0.0F, -1.0F, 1.0F, 1.0F, 2.0F, new Dilation(0.03F)), ModelTransform.of(0.0F, -1.5F, 0.0F, 0.0F, 0.0F, -3.1416F));

        ModelPartData bone29 = lever2.addChild("bone29", ModelPartBuilder.create().uv(28, 30).cuboid(-0.75F, -2.25F, -1.25F, 1.0F, 2.0F, 0.0F, new Dilation(0.0F))
                .uv(88, 65).cuboid(-0.5F, -3.05F, -1.85F, 1.0F, 1.0F, 2.0F, new Dilation(-0.2F)), ModelTransform.of(0.0F, -1.5F, 0.0F, 0.0F, 0.0F, -1.309F));

        ModelPartData bone30 = lever2.addChild("bone30", ModelPartBuilder.create().uv(88, 65).cuboid(-0.5F, -3.05F, -0.2F, 1.0F, 1.0F, 2.0F, new Dilation(-0.2F))
                .uv(28, 30).cuboid(-0.75F, -2.25F, 1.25F, 1.0F, 2.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -1.5F, 0.0F, 0.0F, 0.0F, -1.2217F));

        ModelPartData disc = rot8.addChild("disc", ModelPartBuilder.create().uv(7, 120).cuboid(-1.75F, 0.3F, -1.0F, 1.0F, 0.0F, 2.0F, new Dilation(0.0F))
                .uv(117, 116).cuboid(-2.6F, 0.2F, -1.5F, 1.0F, 0.0F, 3.0F, new Dilation(0.0F))
                .uv(121, 123).cuboid(1.85F, 0.15F, -2.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.1F)), ModelTransform.pivot(-4.0F, -0.5F, 0.0F));

        ModelPartData cube_r276 = disc.addChild("cube_r276", ModelPartBuilder.create().uv(117, 116).cuboid(-2.6F, 0.0F, -1.5F, 1.0F, 0.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.2F, 0.0F, -3.1416F, 1.0472F, 3.1416F));

        ModelPartData cube_r277 = disc.addChild("cube_r277", ModelPartBuilder.create().uv(117, 116).cuboid(-2.6F, 0.0F, -1.5F, 1.0F, 0.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.2F, 0.0F, -3.1416F, -1.0472F, 3.1416F));

        ModelPartData cube_r278 = disc.addChild("cube_r278", ModelPartBuilder.create().uv(117, 116).cuboid(-2.6F, 0.0F, -1.5F, 1.0F, 0.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.2F, 0.0F, -3.1416F, 0.0F, 3.1416F));

        ModelPartData cube_r279 = disc.addChild("cube_r279", ModelPartBuilder.create().uv(117, 116).cuboid(-2.6F, 0.0F, -1.5F, 1.0F, 0.0F, 3.0F, new Dilation(0.0F))
                .uv(7, 120).cuboid(-1.75F, 0.1F, -1.0F, 1.0F, 0.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.2F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData cube_r280 = disc.addChild("cube_r280", ModelPartBuilder.create().uv(117, 116).cuboid(-2.6F, 0.0F, -1.5F, 1.0F, 0.0F, 3.0F, new Dilation(0.0F))
                .uv(7, 120).cuboid(-1.75F, 0.1F, -1.0F, 1.0F, 0.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.2F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData cube_r281 = disc.addChild("cube_r281", ModelPartBuilder.create().uv(7, 120).cuboid(-1.75F, 0.0F, -1.0F, 1.0F, 0.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.3F, 0.0F, -3.1416F, 1.0472F, 3.1416F));

        ModelPartData cube_r282 = disc.addChild("cube_r282", ModelPartBuilder.create().uv(7, 120).cuboid(-1.75F, 0.0F, -1.0F, 1.0F, 0.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.3F, 0.0F, -3.1416F, -1.0472F, 3.1416F));

        ModelPartData cube_r283 = disc.addChild("cube_r283", ModelPartBuilder.create().uv(7, 120).cuboid(-1.75F, 0.0F, -1.0F, 1.0F, 0.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.3F, 0.0F, -3.1416F, 0.0F, 3.1416F));

        ModelPartData bone31 = disc.addChild("bone31", ModelPartBuilder.create().uv(28, 45).cuboid(-1.0F, -1.0F, 0.0F, 1.0F, 1.0F, 0.0F, new Dilation(0.001F))
                .uv(70, 12).cuboid(-0.5F, 0.0F, -0.5F, 1.0F, 0.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(1.75F, 0.05F, 0.0F, 0.0F, 3.1416F, 0.0F));

        ModelPartData bone34 = disc.addChild("bone34", ModelPartBuilder.create().uv(28, 45).cuboid(-1.0F, -1.0F, 0.0F, 1.0F, 1.0F, 0.0F, new Dilation(0.001F))
                .uv(70, 12).cuboid(-0.5F, 0.0F, -0.5F, 1.0F, 0.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-1.75F, 0.05F, -1.0F, 0.0F, -0.4363F, 0.0F));

        ModelPartData bone35 = disc.addChild("bone35", ModelPartBuilder.create().uv(28, 45).cuboid(-1.0F, -1.0F, 0.0F, 1.0F, 1.0F, 0.0F, new Dilation(0.001F))
                .uv(70, 12).cuboid(-0.5F, 0.0F, -0.5F, 1.0F, 0.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-1.75F, 0.05F, 1.0F, 0.0F, 0.4363F, 0.0F));

        ModelPartData bone32 = disc.addChild("bone32", ModelPartBuilder.create().uv(28, 45).cuboid(-1.0F, -1.0F, 0.0F, 1.0F, 1.0F, 0.0F, new Dilation(0.001F))
                .uv(70, 12).cuboid(-0.5F, 0.0F, -0.5F, 1.0F, 0.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.05F, -1.75F, 0.0F, -1.5708F, 0.0F));

        ModelPartData bone33 = disc.addChild("bone33", ModelPartBuilder.create().uv(28, 45).cuboid(-1.0F, -1.0F, 0.0F, 1.0F, 1.0F, 0.0F, new Dilation(0.001F))
                .uv(70, 12).cuboid(-0.5F, 0.0F, -0.5F, 1.0F, 0.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.05F, 1.75F, 0.0F, 1.5708F, 0.0F));

        ModelPartData dial = rot8.addChild("dial", ModelPartBuilder.create().uv(67, 96).cuboid(0.0F, -1.25F, -1.25F, 0.0F, 2.0F, 2.0F, new Dilation(0.0F))
                .uv(123, 114).cuboid(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(1.25F, 0.9F, 9.0F, 0.0F, 0.0F, -0.5672F));

        ModelPartData crank = rot8.addChild("crank", ModelPartBuilder.create().uv(28, 15).cuboid(-0.2F, -0.5F, -0.5F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(1.2687F, 0.6717F, -9.75F, 0.0F, 0.0F, -0.5672F));

        ModelPartData bone36 = crank.addChild("bone36", ModelPartBuilder.create().uv(40, 120).cuboid(-0.6F, -0.5F, -0.5F, 2.0F, 1.0F, 1.0F, new Dilation(-0.3F))
                .uv(123, 92).cuboid(0.8F, 0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.3F))
                .uv(11, 66).cuboid(1.1F, -0.25F, -0.25F, 0.0F, 2.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-0.25F, 0.0F, 0.0F, -0.2182F, 0.0F, 0.0F));

        ModelPartData crank2 = rot8.addChild("crank2", ModelPartBuilder.create().uv(28, 15).cuboid(-0.2F, -0.5F, -0.5F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(1.2687F, 0.6717F, -8.25F, 0.0F, 0.0F, -0.5672F));

        ModelPartData bone37 = crank2.addChild("bone37", ModelPartBuilder.create().uv(40, 120).cuboid(-0.6F, -0.5F, -0.5F, 2.0F, 1.0F, 1.0F, new Dilation(-0.3F))
                .uv(123, 92).cuboid(0.8F, 0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.3F))
                .uv(11, 66).cuboid(1.1F, -0.25F, -0.25F, 0.0F, 2.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-1.0F, 0.0F, 0.0F, 0.0873F, 0.0F, 0.0F));

        ModelPartData button3 = rot8.addChild("button3", ModelPartBuilder.create().uv(63, 40).cuboid(-0.5F, 0.0F, -0.5F, 1.0F, 0.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(-2.0F, -0.25F, -4.0F));

        ModelPartData bone38 = button3.addChild("bone38", ModelPartBuilder.create().uv(123, 75).cuboid(-0.5F, -0.75F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.2F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData bone128 = bone38.addChild("bone128", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData cables = rot8.addChild("cables", ModelPartBuilder.create().uv(0, 120).cuboid(-0.25F, 0.5F, -6.5F, 2.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(77, 12).cuboid(-1.5F, 1.0F, 5.25F, 4.0F, 0.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(1.0F, 8.25F, 0.0F, 0.0F, 0.0F, 2.3562F));

        ModelPartData cube_r284 = cables.addChild("cube_r284", ModelPartBuilder.create().uv(100, 96).cuboid(-1.0F, 0.0F, 0.0F, 3.0F, 0.0F, 6.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, -0.1745F, 0.0F, 0.0F));

        ModelPartData cube_r285 = cables.addChild("cube_r285", ModelPartBuilder.create().uv(101, 33).cuboid(-1.0F, 0.0F, -6.0F, 3.0F, 0.0F, 6.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.1745F, 0.0F, 0.0F));

        ModelPartData lever3 = rot8.addChild("lever3", ModelPartBuilder.create().uv(110, 119).cuboid(-1.0F, -1.0F, -1.0F, 2.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(110, 119).cuboid(-7.0F, -1.0F, -1.0F, 2.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(119, 100).cuboid(-1.0F, -1.8F, -1.0F, 2.0F, 1.0F, 1.0F, new Dilation(-0.1F))
                .uv(51, 72).cuboid(-6.0F, -1.7F, -1.0F, 6.0F, 1.0F, 1.0F, new Dilation(-0.3F))
                .uv(119, 100).cuboid(-7.0F, -1.8F, -1.0F, 2.0F, 1.0F, 1.0F, new Dilation(-0.1F))
                .uv(63, 123).cuboid(-6.5F, -2.05F, -1.0F, 1.0F, 1.0F, 1.0F, new Dilation(-0.2F))
                .uv(63, 123).cuboid(-0.5F, -2.05F, -1.0F, 1.0F, 1.0F, 1.0F, new Dilation(-0.2F))
                .uv(0, 115).cuboid(-4.0F, -2.5F, -1.5F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F))
                .uv(123, 69).cuboid(-3.5F, -2.0F, 0.25F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-2.5F, 0.0F, -6.75F, -0.0182F, 0.4796F, -0.0393F));

        ModelPartData bone39 = lever3.addChild("bone39", ModelPartBuilder.create().uv(70, 38).cuboid(0.0F, -0.75F, 0.0F, 0.0F, 1.0F, 2.0F, new Dilation(0.001F))
                .uv(45, 123).cuboid(-0.5F, -0.5F, 1.75F, 1.0F, 1.0F, 1.0F, new Dilation(-0.2F)), ModelTransform.of(-3.0F, -1.5F, 1.0F, 0.48F, 0.0F, 0.0F));

        ModelPartData cube_r286 = bone39.addChild("cube_r286", ModelPartBuilder.create().uv(70, 38).cuboid(0.0F, -0.75F, 0.25F, 0.0F, 1.0F, 2.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 0.0F, -0.25F, 0.0F, 0.0F, -1.5708F));

        ModelPartData panel = rot8.addChild("panel", ModelPartBuilder.create().uv(49, 111).cuboid(-1.0F, -0.7F, -3.0F, 3.0F, 1.0F, 4.0F, new Dilation(0.0F))
                .uv(111, 103).cuboid(-0.75F, -1.0F, -2.8F, 3.0F, 1.0F, 4.0F, new Dilation(-0.2F)), ModelTransform.of(-13.0F, -0.5F, 2.5F, 0.0F, 0.0F, 0.48F));

        ModelPartData cube_r287 = panel.addChild("cube_r287", ModelPartBuilder.create().uv(83, 39).cuboid(-2.75F, -0.4F, -4.2F, 4.0F, 0.0F, 5.0F, new Dilation(0.0F))
                .uv(104, 57).cuboid(-2.75F, -0.3F, -4.2F, 4.0F, 0.0F, 5.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.2618F));

        ModelPartData dial2 = panel.addChild("dial2", ModelPartBuilder.create().uv(40, 123).cuboid(-0.5F, -1.0F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.1F))
                .uv(34, 45).cuboid(-0.5F, -1.75F, 0.0F, 1.0F, 1.0F, 0.0F, new Dilation(0.0F)), ModelTransform.pivot(1.25F, -0.1F, -2.25F));

        ModelPartData dial3 = panel.addChild("dial3", ModelPartBuilder.create().uv(40, 123).cuboid(-0.5F, -1.0F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.2F))
                .uv(34, 45).cuboid(-0.5F, -1.75F, 0.0F, 1.0F, 1.0F, 0.0F, new Dilation(0.0F)), ModelTransform.pivot(1.25F, -0.1F, 0.25F));

        ModelPartData light15 = panel.addChild("light15", ModelPartBuilder.create().uv(123, 39).cuboid(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.2F)), ModelTransform.pivot(-0.5F, -0.75F, -2.4F));

        ModelPartData bone40 = light15.addChild("bone40", ModelPartBuilder.create().uv(123, 33).cuboid(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.19F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData light16 = panel.addChild("light16", ModelPartBuilder.create().uv(123, 39).cuboid(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.2F)), ModelTransform.pivot(-0.5F, -0.75F, -1.5F));

        ModelPartData bone41 = light16.addChild("bone41", ModelPartBuilder.create().uv(123, 33).cuboid(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.19F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData light17 = panel.addChild("light17", ModelPartBuilder.create().uv(123, 39).cuboid(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.2F)), ModelTransform.pivot(-0.5F, -0.75F, -0.6F));

        ModelPartData bone42 = light17.addChild("bone42", ModelPartBuilder.create().uv(123, 33).cuboid(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.19F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData light18 = panel.addChild("light18", ModelPartBuilder.create().uv(123, 39).cuboid(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.2F)), ModelTransform.pivot(-0.5F, -0.75F, 0.3F));

        ModelPartData bone43 = light18.addChild("bone43", ModelPartBuilder.create().uv(123, 33).cuboid(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.19F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData fluid_reservoir = rot8.addChild("fluid_reservoir", ModelPartBuilder.create().uv(42, 83).cuboid(0.0F, -1.0F, -1.0F, 1.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(-15.0F, -2.0F, -2.5F, 0.0F, 0.0F, -0.829F));

        ModelPartData cube_r288 = fluid_reservoir.addChild("cube_r288", ModelPartBuilder.create().uv(121, 0).cuboid(-1.2549F, -2.1001F, -0.5F, 1.0F, 2.0F, 1.0F, new Dilation(-0.0999F))
                .uv(5, 123).cuboid(-0.5049F, -2.6001F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.2F))
                .uv(71, 121).cuboid(-0.5049F, -2.1001F, -0.5F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(3.75F, -1.0F, 0.0F, 0.0F, 0.0F, 0.3491F));

        ModelPartData cube_r289 = fluid_reservoir.addChild("cube_r289", ModelPartBuilder.create().uv(101, 118).cuboid(-0.5546F, -0.5912F, -0.5F, 3.0F, 1.0F, 1.0F, new Dilation(-0.1F)), ModelTransform.of(1.75F, -1.25F, 0.0F, 0.0F, 0.0F, -0.3054F));

        ModelPartData cube_r290 = fluid_reservoir.addChild("cube_r290", ModelPartBuilder.create().uv(119, 10).cuboid(0.35F, -1.45F, -0.5F, 3.0F, 1.0F, 1.0F, new Dilation(-0.099F))
                .uv(0, 123).cuboid(0.75F, -0.85F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.3F))
                .uv(0, 123).cuboid(2.0F, -0.85F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.3F))
                .uv(72, 56).cuboid(0.0F, -0.45F, -0.5F, 4.0F, 1.0F, 1.0F, new Dilation(-0.1F)), ModelTransform.of(0.75F, 0.0F, 0.0F, 0.0F, 0.0F, -0.1309F));

        ModelPartData cube_r291 = fluid_reservoir.addChild("cube_r291", ModelPartBuilder.create().uv(122, 120).cuboid(-0.8158F, -0.9721F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.2F)), ModelTransform.of(4.75F, 0.0F, 0.0F, 0.0F, 0.0F, -0.5672F));

        ModelPartData needle = rot8.addChild("needle", ModelPartBuilder.create().uv(39, 33).cuboid(0.0F, -3.0F, -0.25F, 0.0F, 3.0F, 1.0F, new Dilation(0.001F))
                .uv(111, 122).cuboid(-0.5F, -2.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.1F)), ModelTransform.pivot(-4.0F, 0.0F, 0.0F));

        ModelPartData cube_r292 = needle.addChild("cube_r292", ModelPartBuilder.create().uv(39, 33).cuboid(0.0F, -3.0F, -0.25F, 0.0F, 3.0F, 1.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.5708F, 0.0F));

        ModelPartData panel_3 = controls.addChild("panel_3", ModelPartBuilder.create(), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 2.0944F, 0.0F));

        ModelPartData rot9 = panel_3.addChild("rot9", ModelPartBuilder.create(), ModelTransform.of(20.0F, -13.0F, 0.0F, 0.0F, 0.0F, 0.5672F));

        ModelPartData meter = rot9.addChild("meter", ModelPartBuilder.create().uv(111, 114).cuboid(-1.0F, -1.0F, -1.0F, 2.0F, 2.0F, 2.0F, new Dilation(-0.2F))
                .uv(79, 79).cuboid(-0.15F, -1.0F, -1.0F, 1.0F, 2.0F, 2.0F, new Dilation(-0.2F)), ModelTransform.of(-14.0F, -1.0F, -3.25F, 0.0F, 0.0F, -0.829F));

        ModelPartData bone15 = meter.addChild("bone15", ModelPartBuilder.create().uv(28, 0).cuboid(0.85F, -1.0F, -0.25F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-0.1F, 0.25F, 0.0F, 0.6109F, 0.0F, 0.0F));

        ModelPartData meter2 = rot9.addChild("meter2", ModelPartBuilder.create().uv(111, 114).cuboid(-1.0F, -1.0F, -1.0F, 2.0F, 2.0F, 2.0F, new Dilation(-0.2F))
                .uv(79, 79).cuboid(-0.15F, -1.0F, -1.0F, 1.0F, 2.0F, 2.0F, new Dilation(-0.2F)), ModelTransform.of(-14.0F, -1.0F, -1.15F, 0.0F, 0.0F, -0.829F));

        ModelPartData bone16 = meter2.addChild("bone16", ModelPartBuilder.create().uv(28, 0).cuboid(0.85F, -1.0F, -0.25F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-0.1F, 0.25F, 0.0F, 0.6109F, 0.0F, 0.0F));

        ModelPartData meter3 = rot9.addChild("meter3", ModelPartBuilder.create().uv(111, 114).cuboid(-1.0F, -1.0F, -1.0F, 2.0F, 2.0F, 2.0F, new Dilation(-0.2F))
                .uv(79, 79).cuboid(-0.15F, -1.0F, -1.0F, 1.0F, 2.0F, 2.0F, new Dilation(-0.2F)), ModelTransform.of(-14.0F, -1.0F, 1.15F, 0.0F, 0.0F, -0.829F));

        ModelPartData bone17 = meter3.addChild("bone17", ModelPartBuilder.create().uv(28, 0).cuboid(0.85F, -1.0F, -0.25F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-0.1F, 0.25F, 0.0F, 0.6109F, 0.0F, 0.0F));

        ModelPartData meter4 = rot9.addChild("meter4", ModelPartBuilder.create().uv(111, 114).cuboid(-1.0F, -1.0F, -1.0F, 2.0F, 2.0F, 2.0F, new Dilation(-0.2F))
                .uv(79, 79).cuboid(-0.15F, -1.0F, -1.0F, 1.0F, 2.0F, 2.0F, new Dilation(-0.2F)), ModelTransform.of(-14.0F, -1.0F, 3.25F, 0.0F, 0.0F, -0.829F));

        ModelPartData bone18 = meter4.addChild("bone18", ModelPartBuilder.create().uv(28, 0).cuboid(0.85F, -1.0F, -0.25F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-0.1F, 0.25F, 0.0F, 0.6109F, 0.0F, 0.0F));

        ModelPartData bone19 = rot9.addChild("bone19", ModelPartBuilder.create().uv(114, 33).cuboid(-1.0F, -2.0F, -1.0F, 2.0F, 1.0F, 4.0F, new Dilation(0.2F))
                .uv(27, 141).cuboid(0.2F, -2.2F, -3.25F, 1.0F, 1.0F, 2.0F, new Dilation(0.0F))
                .uv(18, 149).cuboid(0.2F, -2.225F, -3.25F, 1.0F, 0.0F, 2.0F, new Dilation(0.0F))
                .uv(24, 139).cuboid(0.2F, -2.25F, -3.25F, 1.0F, 0.0F, 2.0F, new Dilation(0.0F))
                .uv(18, 142).cuboid(-0.8F, -2.15F, -2.75F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(17, 138).cuboid(-1.0F, -2.15F, 3.65F, 2.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(18, 142).cuboid(-2.0F, -2.15F, -2.75F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(13, 122).cuboid(-0.5F, -3.0F, 0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.2F))
                .uv(86, 121).cuboid(-0.5F, -3.5F, 0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.2F))
                .uv(113, 51).cuboid(-1.25F, -2.1F, -0.75F, 2.0F, 1.0F, 4.0F, new Dilation(0.2F)), ModelTransform.of(-17.0F, -1.75F, -1.0F, 0.0F, 0.0F, 0.7418F));

        ModelPartData cube_r293 = bone19.addChild("cube_r293", ModelPartBuilder.create().uv(124, 74).cuboid(-3.0F, 0.0F, -2.6F, 6.0F, 1.0F, 5.0F, new Dilation(0.0F)), ModelTransform.of(2.0F, -2.6F, 3.0F, 0.0F, 0.0F, -0.3054F));

        ModelPartData cube_r294 = bone19.addChild("cube_r294", ModelPartBuilder.create().uv(19, 143).cuboid(-0.5F, -0.5F, -0.2F, 1.0F, 1.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(-0.9F, -1.65F, -1.85F, -2.3562F, 0.0F, 0.0F));

        ModelPartData cube_r295 = bone19.addChild("cube_r295", ModelPartBuilder.create().uv(19, 143).cuboid(-0.5F, -0.5F, -0.2F, 1.0F, 1.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(-0.9F, -1.65F, -2.65F, -0.7854F, 0.0F, 0.0F));

        ModelPartData vent = rot9.addChild("vent", ModelPartBuilder.create().uv(0, 73).cuboid(-3.0F, -1.501F, -1.0F, 4.0F, 0.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(-2.0F, 0.851F, 7.75F, 0.0227F, -0.4795F, -0.0492F));

        ModelPartData cube_r296 = vent.addChild("cube_r296", ModelPartBuilder.create().uv(97, 57).cuboid(-2.9F, 0.05F, -1.0F, 3.0F, 1.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.5F, -1.601F, 0.0F, 0.0F, 0.0F, 0.288F));

        ModelPartData levers = rot9.addChild("levers", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, -1.0F));

        ModelPartData lever4 = levers.addChild("lever4", ModelPartBuilder.create().uv(109, 89).cuboid(-1.0F, -0.25F, -1.0F, 2.0F, 1.0F, 2.0F, new Dilation(-0.2F)), ModelTransform.pivot(-3.55F, -0.15F, 2.0F));

        ModelPartData bone20 = lever4.addChild("bone20", ModelPartBuilder.create().uv(63, 30).cuboid(0.0F, -1.0F, 0.0F, 1.0F, 1.0F, 0.0F, new Dilation(0.001F))
                .uv(125, 56).cuboid(-0.25F, -1.3F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.25F)), ModelTransform.pivot(-0.5F, 0.0F, -0.4F));

        ModelPartData lever5 = levers.addChild("lever5", ModelPartBuilder.create().uv(109, 89).cuboid(-1.0F, -0.25F, -1.0F, 2.0F, 1.0F, 2.0F, new Dilation(-0.2F)), ModelTransform.of(-2.75F, -0.15F, 3.25F, 0.0F, 0.7854F, 0.0F));

        ModelPartData bone21 = lever5.addChild("bone21", ModelPartBuilder.create().uv(63, 30).cuboid(0.0F, -1.0F, 0.0F, 1.0F, 1.0F, 0.0F, new Dilation(0.001F))
                .uv(125, 56).cuboid(-0.25F, -1.3F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.25F)), ModelTransform.pivot(-0.5F, 0.0F, -0.4F));

        ModelPartData lever6 = levers.addChild("lever6", ModelPartBuilder.create().uv(109, 89).cuboid(-1.0F, -0.25F, -1.0F, 2.0F, 1.0F, 2.0F, new Dilation(-0.2F)), ModelTransform.of(-1.25F, -0.15F, 3.5F, 0.0F, 1.5708F, 0.0F));

        ModelPartData bone22 = lever6.addChild("bone22", ModelPartBuilder.create().uv(63, 30).cuboid(0.0F, -1.0F, 0.0F, 1.0F, 1.0F, 0.0F, new Dilation(0.001F))
                .uv(125, 56).cuboid(-0.25F, -1.3F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.25F)), ModelTransform.pivot(-0.5F, 0.0F, -0.4F));

        ModelPartData needle2 = rot9.addChild("needle2", ModelPartBuilder.create().uv(62, 50).cuboid(-0.5F, 0.0F, -0.5F, 1.0F, 0.0F, 1.0F, new Dilation(0.0F))
                .uv(38, 15).cuboid(-0.25F, -2.6F, 0.0F, 1.0F, 2.0F, 0.0F, new Dilation(0.001F))
                .uv(122, 103).cuboid(-0.5F, -0.8F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.2F)), ModelTransform.pivot(-2.0F, -0.2F, -3.5F));

        ModelPartData cube_r297 = needle2.addChild("cube_r297", ModelPartBuilder.create().uv(38, 15).cuboid(-0.25F, -2.6F, 0.0F, 1.0F, 2.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.5708F, 0.0F));

        ModelPartData spinny = rot9.addChild("spinny", ModelPartBuilder.create().uv(67, 91).cuboid(-1.0F, -2.0F, -1.0F, 3.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(-6.25F, 1.75F, 0.0F, 0.0F, 0.0F, -0.5672F));

        ModelPartData bone24 = spinny.addChild("bone24", ModelPartBuilder.create().uv(117, 120).cuboid(-0.5F, -1.0F, -0.5F, 1.0F, 2.0F, 1.0F, new Dilation(-0.1F))
                .uv(69, 30).cuboid(-0.5F, -0.7F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.05F))
                .uv(69, 30).cuboid(-0.5F, -1.2F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.05F)), ModelTransform.pivot(1.0F, -2.0F, 0.0F));

        ModelPartData bone26 = bone24.addChild("bone26", ModelPartBuilder.create().uv(36, 93).cuboid(-0.5F, -1.0F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.1F))
                .uv(112, 0).cuboid(-1.0F, -1.5F, -1.0F, 2.0F, 2.0F, 2.0F, new Dilation(-0.3F))
                .uv(86, 48).cuboid(-0.5F, -1.0F, -1.0F, 1.0F, 1.0F, 2.0F, new Dilation(-0.2F))
                .uv(53, 88).cuboid(-0.5F, -1.0F, -1.5F, 1.0F, 1.0F, 3.0F, new Dilation(-0.4F)), ModelTransform.pivot(0.0F, -0.6F, 0.0F));

        ModelPartData bone27 = bone26.addChild("bone27", ModelPartBuilder.create().uv(53, 122).cuboid(-0.5F, -1.4F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.04F))
                .uv(122, 49).cuboid(-0.5F, -1.85F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.24F))
                .uv(122, 52).cuboid(-0.5F, -1.25F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -0.25F, 0.0F));

        ModelPartData stabilizers = rot9.addChild("stabilizers", ModelPartBuilder.create().uv(122, 72).cuboid(-0.5F, -1.0F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(-2.0F, 0.5F, 4.5F));

        ModelPartData bone25 = stabilizers.addChild("bone25", ModelPartBuilder.create().uv(122, 66).cuboid(-0.5F, -0.75F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.1F))
                .uv(58, 122).cuboid(-0.5F, -1.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.2F)), ModelTransform.pivot(0.0F, -1.0F, 0.0F));

        ModelPartData lever7 = rot9.addChild("lever7", ModelPartBuilder.create().uv(118, 89).cuboid(-1.0F, 0.0F, -0.75F, 2.0F, 0.0F, 2.0F, new Dilation(0.001F))
                .uv(96, 121).cuboid(0.0F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, new Dilation(0.1F)), ModelTransform.pivot(-6.25F, -0.5F, -6.0F));

        ModelPartData bone96 = lever7.addChild("bone96", ModelPartBuilder.create().uv(83, 118).cuboid(-0.5F, -0.5F, -0.5F, 3.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(76, 39).cuboid(2.75F, -0.5F, -0.5F, 3.0F, 1.0F, 1.0F, new Dilation(-0.1F))
                .uv(91, 121).cuboid(2.25F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.2F)), ModelTransform.pivot(0.5F, -0.5F, 0.5F));

        ModelPartData lever10 = rot9.addChild("lever10", ModelPartBuilder.create().uv(118, 82).cuboid(-1.4F, -0.5F, -0.5F, 2.0F, 1.0F, 2.0F, new Dilation(0.0F))
                .uv(125, 28).cuboid(-0.9F, -1.5F, 0.0F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(64, 119).cuboid(-1.0F, -2.5F, 0.0F, 2.0F, 2.0F, 1.0F, new Dilation(-0.3F)), ModelTransform.pivot(-8.8F, 0.0F, -4.4F));

        ModelPartData bone97 = lever10.addChild("bone97", ModelPartBuilder.create().uv(39, 38).cuboid(-0.25F, -2.75F, 0.0F, 1.0F, 3.0F, 0.0F, new Dilation(0.0F))
                .uv(81, 121).cuboid(-0.5F, -2.75F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.2F)), ModelTransform.pivot(-0.35F, -1.5F, 0.5F));

        ModelPartData button4 = rot9.addChild("button4", ModelPartBuilder.create().uv(-2, 137).cuboid(-1.0F, -2.0F, -1.0F, 2.0F, 0.0F, 2.0F, new Dilation(0.0F))
                .uv(176, 135).cuboid(2.0F, -2.0F, 1.0F, 2.0F, 0.0F, 2.0F, new Dilation(0.0F))
                .uv(145, 120).cuboid(-0.65F, -3.0F, 1.75F, 3.0F, 1.0F, 1.0F, new Dilation(-0.0999F))
                .uv(144, 106).cuboid(0.225F, -3.5F, 1.25F, 1.0F, 2.0F, 2.0F, new Dilation(-0.0999F))
                .uv(141, 87).cuboid(3.05F, -4.525F, 1.75F, 2.0F, 1.0F, 1.0F, new Dilation(-0.2F))
                .uv(151, 94).cuboid(0.6168F, -4.5204F, 1.95F, 3.0F, 1.0F, 1.0F, new Dilation(-0.3F))
                .uv(178, 120).cuboid(2.25F, -3.0F, 1.75F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(0, 147).cuboid(-0.75F, -2.5F, -0.25F, 1.0F, 1.0F, 1.0F, new Dilation(-0.1F)), ModelTransform.pivot(-9.5F, 1.5F, 3.25F));

        ModelPartData cube_r298 = button4.addChild("cube_r298", ModelPartBuilder.create().uv(154, 100).cuboid(-2.0F, -0.5F, -0.5F, 3.0F, 1.0F, 1.0F, new Dilation(-0.2F)), ModelTransform.of(-3.9677F, -2.1402F, 1.7373F, 0.2042F, -0.3378F, -0.5585F));

        ModelPartData cube_r299 = button4.addChild("cube_r299", ModelPartBuilder.create().uv(181, 129).cuboid(-1.0F, -0.5F, -0.5F, 2.0F, 1.0F, 1.0F, new Dilation(-0.2F)), ModelTransform.of(-2.727F, -2.4999F, 2.2511F, 0.0F, -0.3927F, 0.0F));

        ModelPartData cube_r300 = button4.addChild("cube_r300", ModelPartBuilder.create().uv(166, 99).cuboid(-1.0F, -1.5F, -0.5F, 2.0F, 1.0F, 1.0F, new Dilation(-0.1999F)), ModelTransform.of(0.1812F, -1.5F, 2.2307F, 0.0F, 0.0436F, 0.0F));

        ModelPartData cube_r301 = button4.addChild("cube_r301", ModelPartBuilder.create().uv(178, 106).cuboid(-1.65F, -3.0F, 1.75F, 2.0F, 1.0F, 1.0F, new Dilation(-0.2F)), ModelTransform.of(-1.1174F, 0.0F, 0.0715F, 0.0F, 0.1745F, 0.0F));

        ModelPartData cube_r302 = button4.addChild("cube_r302", ModelPartBuilder.create().uv(138, 102).cuboid(-0.2F, 0.0F, -0.5F, 2.0F, 1.0F, 1.0F, new Dilation(-0.3F))
                .uv(138, 102).cuboid(-0.2F, -1.6F, -0.5F, 2.0F, 1.0F, 1.0F, new Dilation(-0.3F)), ModelTransform.of(1.6168F, -4.0204F, 2.25F, 0.0F, 0.0F, 1.5708F));

        ModelPartData bone98 = button4.addChild("bone98", ModelPartBuilder.create().uv(0, 150).cuboid(-1.0F, -2.0F, 0.0F, 1.0F, 1.0F, 1.0F, new Dilation(-0.1F)), ModelTransform.pivot(0.25F, -0.4F, -0.25F));

        ModelPartData lever11 = rot9.addChild("lever11", ModelPartBuilder.create().uv(0, 158).cuboid(-1.0F, -2.0F, -1.0F, 2.0F, 1.0F, 2.0F, new Dilation(0.0F))
                .uv(0, 192).cuboid(-0.5F, -2.4F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.1F)), ModelTransform.pivot(-1.25F, 1.3F, -8.0F));

        ModelPartData bone99 = lever11.addChild("bone99", ModelPartBuilder.create().uv(15, 176).cuboid(-0.25F, -2.0F, 0.0F, 1.0F, 2.0F, 0.0F, new Dilation(0.0F))
                .uv(14, 193).cuboid(-0.5F, -2.4F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.2F)), ModelTransform.of(0.0F, -2.0F, 0.0F, 0.0F, 0.0F, 0.1309F));

        ModelPartData panel_4 = controls.addChild("panel_4", ModelPartBuilder.create(), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 3.1416F, 0.0F));

        ModelPartData rot16 = panel_4.addChild("rot16", ModelPartBuilder.create(), ModelTransform.of(20.0F, -13.0F, 0.0F, 0.0F, 0.0F, 0.5672F));

        ModelPartData spinny2 = rot16.addChild("spinny2", ModelPartBuilder.create().uv(67, 91).cuboid(-1.0F, -2.0F, -1.0F, 3.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(-6.25F, 1.75F, 0.0F, 0.0F, 0.0F, -0.5672F));

        ModelPartData bone23 = spinny2.addChild("bone23", ModelPartBuilder.create().uv(52, 203).cuboid(-0.5F, -1.5F, -1.1F, 1.0F, 2.0F, 1.0F, new Dilation(-0.3F))
                .uv(30, 215).cuboid(-1.0F, -2.9F, -1.1F, 2.0F, 2.0F, 1.0F, new Dilation(-0.3F))
                .uv(30, 215).cuboid(-1.0F, -2.9F, 0.1F, 2.0F, 2.0F, 1.0F, new Dilation(-0.3F))
                .uv(36, 186).cuboid(-0.5F, -2.4F, -1.1F, 1.0F, 1.0F, 1.0F, new Dilation(-0.2F))
                .uv(36, 186).cuboid(-0.5F, -2.4F, 0.1F, 1.0F, 1.0F, 1.0F, new Dilation(-0.2F))
                .uv(52, 203).cuboid(-0.5F, -1.5F, 0.1F, 1.0F, 2.0F, 1.0F, new Dilation(-0.3F))
                .uv(36, 233).cuboid(-1.0F, -0.75F, -1.0F, 2.0F, 1.0F, 2.0F, new Dilation(-0.1F)), ModelTransform.of(1.0F, -2.15F, 0.0F, 0.0F, 0.3054F, 0.0F));

        ModelPartData bone100 = bone23.addChild("bone100", ModelPartBuilder.create().uv(87, 238).cuboid(-0.75F, -0.5F, -0.5F, 2.0F, 1.0F, 1.0F, new Dilation(-0.2F)), ModelTransform.of(0.0F, -1.9F, 0.0F, 0.0F, 0.0F, -0.3054F));

        ModelPartData crank3 = rot16.addChild("crank3", ModelPartBuilder.create().uv(190, 77).cuboid(-1.0F, -2.0F, -1.0F, 2.0F, 1.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 2.0F, -9.0F, 0.0F, 0.0F, 1.0036F));

        ModelPartData bone101 = crank3.addChild("bone101", ModelPartBuilder.create().uv(211, 55).cuboid(-0.5F, -2.0F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(182, 55).cuboid(-0.75F, -2.1F, -0.25F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(204, 40).cuboid(1.5F, -3.0F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.2F))
                .uv(167, 39).cuboid(0.5F, -1.75F, -0.25F, 2.0F, 0.0F, 1.0F, new Dilation(0.0F))
                .uv(158, 84).cuboid(2.0F, -2.75F, -0.25F, 0.0F, 1.0F, 1.0F, new Dilation(0.001F)), ModelTransform.pivot(0.0F, -0.5F, 0.0F));

        ModelPartData lever8 = rot16.addChild("lever8", ModelPartBuilder.create().uv(224, 125).cuboid(-1.0F, -2.0F, -1.5F, 4.0F, 2.0F, 3.0F, new Dilation(-0.29F))
                .uv(226, 118).cuboid(-0.6F, -1.75F, -1.1F, 1.0F, 0.0F, 1.0F, new Dilation(0.0F))
                .uv(233, 182).cuboid(0.0F, -2.0F, -1.2F, 2.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(224, 136).cuboid(-1.0F, -2.05F, 0.0F, 4.0F, 2.0F, 2.0F, new Dilation(-0.3F))
                .uv(224, 136).cuboid(-1.0F, -2.05F, -0.5F, 4.0F, 2.0F, 2.0F, new Dilation(-0.3F)), ModelTransform.of(-6.5F, 0.25F, -5.25F, -0.0227F, 0.4795F, -0.0492F));

        ModelPartData bone102 = lever8.addChild("bone102", ModelPartBuilder.create().uv(158, 109).cuboid(-0.5F, -0.5F, -1.0F, 1.0F, 1.0F, 2.0F, new Dilation(-0.2F)), ModelTransform.pivot(2.0F, -2.0F, 0.6F));

        ModelPartData bone103 = lever8.addChild("bone103", ModelPartBuilder.create().uv(193, 234).cuboid(-0.5F, -2.1F, -0.5F, 1.0F, 2.0F, 1.0F, new Dilation(-0.3F))
                .uv(160, 126).cuboid(-0.5F, -2.6F, -1.0F, 1.0F, 1.0F, 2.0F, new Dilation(-0.2F)), ModelTransform.of(1.0F, -1.5F, -0.7F, 0.0F, 0.0F, 0.3927F));

        ModelPartData bone104 = rot16.addChild("bone104", ModelPartBuilder.create().uv(223, 92).cuboid(-1.75F, 0.0F, -1.75F, 3.0F, 0.0F, 3.0F, new Dilation(0.0F))
                .uv(223, 105).cuboid(-1.0F, 0.0F, -4.5F, 2.0F, 0.0F, 2.0F, new Dilation(0.0F))
                .uv(210, 109).cuboid(-1.75F, -0.5F, -1.75F, 3.0F, 0.0F, 3.0F, new Dilation(0.001F))
                .uv(206, 132).cuboid(0.25F, -0.75F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.25F))
                .uv(206, 132).cuboid(-0.5F, -0.75F, -1.25F, 1.0F, 1.0F, 1.0F, new Dilation(-0.25F))
                .uv(206, 132).cuboid(-0.5F, -0.75F, 0.25F, 1.0F, 1.0F, 1.0F, new Dilation(-0.25F))
                .uv(206, 132).cuboid(-1.25F, -0.75F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.25F))
                .uv(206, 132).cuboid(-1.5F, -1.0F, -0.75F, 1.0F, 1.0F, 1.0F, new Dilation(-0.25F))
                .uv(206, 132).cuboid(-0.25F, -1.0F, -1.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.25F))
                .uv(206, 132).cuboid(0.5F, -1.0F, -0.25F, 1.0F, 1.0F, 1.0F, new Dilation(-0.25F))
                .uv(206, 132).cuboid(-0.75F, -1.0F, 0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.25F)), ModelTransform.pivot(-2.0F, -0.2F, 0.0F));

        ModelPartData meter5 = rot16.addChild("meter5", ModelPartBuilder.create().uv(248, 12).cuboid(-0.95F, -1.65F, -1.0F, 2.0F, 1.0F, 2.0F, new Dilation(-0.1F))
                .uv(244, 7).cuboid(-0.95F, -1.6F, -1.0F, 2.0F, 1.0F, 2.0F, new Dilation(0.0F))
                .uv(245, 18).cuboid(-1.2F, -1.7F, -1.25F, 2.0F, 1.0F, 2.0F, new Dilation(-0.1F)), ModelTransform.of(-9.2F, 0.0F, -3.95F, 0.0F, 0.48F, 0.0F));

        ModelPartData cube_r303 = meter5.addChild("cube_r303", ModelPartBuilder.create().uv(248, 41).cuboid(-1.0F, -0.75F, -1.0F, 2.0F, 1.0F, 2.0F, new Dilation(0.2F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.1745F));

        ModelPartData bone105 = meter5.addChild("bone105", ModelPartBuilder.create().uv(155, 0).cuboid(-0.75F, 0.0F, -0.25F, 1.0F, 0.0F, 1.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, -1.6F, 0.0F, 0.0F, -0.48F, 0.0F));

        ModelPartData button5 = rot16.addChild("button5", ModelPartBuilder.create().uv(154, 254).cuboid(-0.5F, -1.25F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.09F))
                .uv(155, 238).cuboid(-0.5F, -1.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.05F)), ModelTransform.pivot(-1.95F, -1.0F, -7.75F));

        ModelPartData cube_r304 = button5.addChild("cube_r304", ModelPartBuilder.create().uv(135, 252).cuboid(-0.5F, -0.25F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.2F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0436F));

        ModelPartData bone107 = button5.addChild("bone107", ModelPartBuilder.create().uv(154, 253).cuboid(-0.5F, -1.25F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.1F)), ModelTransform.pivot(0.0F, -0.8F, 0.0F));

        ModelPartData button6 = rot16.addChild("button6", ModelPartBuilder.create(), ModelTransform.pivot(-1.95F, -0.75F, 7.75F));

        ModelPartData cube_r305 = button6.addChild("cube_r305", ModelPartBuilder.create().uv(135, 252).cuboid(-0.5F, -0.25F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.2F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0436F));

        ModelPartData bone108 = button6.addChild("bone108", ModelPartBuilder.create().uv(155, 248).cuboid(-0.5F, -1.25F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.1F)), ModelTransform.pivot(0.0F, 0.2F, 0.0F));

        ModelPartData t_switch = rot16.addChild("t_switch", ModelPartBuilder.create().uv(94, 174).cuboid(-1.0F, -2.0F, -1.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.2F))
                .uv(96, 191).cuboid(-1.0F, -4.0F, -1.0F, 2.0F, 2.0F, 2.0F, new Dilation(-0.2F))
                .uv(96, 199).cuboid(-1.0F, -5.0F, -0.95F, 1.0F, 2.0F, 1.0F, new Dilation(-0.31F))
                .uv(104, 202).cuboid(-1.0F, -5.25F, -0.95F, 1.0F, 1.0F, 1.0F, new Dilation(-0.25F))
                .uv(95, 185).cuboid(-1.05F, -3.75F, -0.75F, 1.0F, 2.0F, 2.0F, new Dilation(-0.2F)), ModelTransform.of(0.75F, 1.5F, 9.0F, 0.0F, 0.0F, 1.0036F));

        ModelPartData bone106 = t_switch.addChild("bone106", ModelPartBuilder.create().uv(104, 202).cuboid(-0.5F, -1.5F, -0.95F, 1.0F, 1.0F, 1.0F, new Dilation(-0.25F))
                .uv(96, 199).cuboid(-0.5F, -1.25F, -0.95F, 1.0F, 2.0F, 1.0F, new Dilation(-0.31F)), ModelTransform.of(-0.75F, -3.35F, 0.5F, 0.0F, 0.0F, -1.5708F));

        ModelPartData middle_1 = controls.addChild("middle_1", ModelPartBuilder.create(), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.5236F, 0.0F));

        ModelPartData rot10 = middle_1.addChild("rot10", ModelPartBuilder.create(), ModelTransform.of(21.0F, -13.0F, 0.0F, 0.0F, 0.0F, 0.48F));

        ModelPartData coil = rot10.addChild("coil", ModelPartBuilder.create().uv(111, 109).cuboid(-3.5F, -2.0F, -1.0F, 5.0F, 2.0F, 2.0F, new Dilation(-0.3F))
                .uv(114, 73).cuboid(-1.0F, -2.0F, -1.0F, 2.0F, 2.0F, 2.0F, new Dilation(-0.1F))
                .uv(114, 73).cuboid(-3.0F, -2.0F, -1.0F, 2.0F, 2.0F, 2.0F, new Dilation(-0.1F))
                .uv(17, 66).cuboid(2.0F, -2.0F, -1.0F, 1.0F, 2.0F, 2.0F, new Dilation(-0.2F))
                .uv(17, 66).cuboid(-5.0F, -2.0F, -1.0F, 1.0F, 2.0F, 2.0F, new Dilation(-0.2F))
                .uv(0, 89).cuboid(1.5F, -2.0F, -1.0F, 0.0F, 2.0F, 2.0F, new Dilation(0.001F))
                .uv(0, 89).cuboid(-3.5F, -2.0F, -1.0F, 0.0F, 2.0F, 2.0F, new Dilation(0.001F))
                .uv(107, 30).cuboid(-5.0F, -1.5F, -0.5F, 8.0F, 1.0F, 1.0F, new Dilation(-0.3F)), ModelTransform.pivot(-1.5F, -0.5F, 0.0F));

        ModelPartData middle_2 = controls.addChild("middle_2", ModelPartBuilder.create(), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.5708F, 0.0F));

        ModelPartData rot11 = middle_2.addChild("rot11", ModelPartBuilder.create(), ModelTransform.of(21.0F, -13.0F, 0.0F, 0.0F, 0.0F, 0.48F));

        ModelPartData coil2 = rot11.addChild("coil2", ModelPartBuilder.create().uv(111, 109).cuboid(-3.5F, -2.0F, -1.0F, 5.0F, 2.0F, 2.0F, new Dilation(-0.3F))
                .uv(114, 73).cuboid(-1.0F, -2.0F, -1.0F, 2.0F, 2.0F, 2.0F, new Dilation(-0.1F))
                .uv(114, 73).cuboid(-3.0F, -2.0F, -1.0F, 2.0F, 2.0F, 2.0F, new Dilation(-0.1F))
                .uv(17, 66).cuboid(2.0F, -2.0F, -1.0F, 1.0F, 2.0F, 2.0F, new Dilation(-0.2F))
                .uv(17, 66).cuboid(-5.0F, -2.0F, -1.0F, 1.0F, 2.0F, 2.0F, new Dilation(-0.2F))
                .uv(0, 89).cuboid(1.5F, -2.0F, -1.0F, 0.0F, 2.0F, 2.0F, new Dilation(0.001F))
                .uv(0, 89).cuboid(-3.5F, -2.0F, -1.0F, 0.0F, 2.0F, 2.0F, new Dilation(0.001F))
                .uv(107, 30).cuboid(-5.0F, -1.5F, -0.5F, 8.0F, 1.0F, 1.0F, new Dilation(-0.3F)), ModelTransform.pivot(-1.5F, -0.5F, 0.0F));

        ModelPartData middle_3 = controls.addChild("middle_3", ModelPartBuilder.create(), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 2.618F, 0.0F));

        ModelPartData rot12 = middle_3.addChild("rot12", ModelPartBuilder.create(), ModelTransform.of(21.0F, -13.0F, 0.0F, 0.0F, 0.0F, 0.48F));

        ModelPartData coil3 = rot12.addChild("coil3", ModelPartBuilder.create().uv(111, 109).cuboid(-3.5F, -2.0F, -1.0F, 5.0F, 2.0F, 2.0F, new Dilation(-0.3F))
                .uv(114, 73).cuboid(-1.0F, -2.0F, -1.0F, 2.0F, 2.0F, 2.0F, new Dilation(-0.1F))
                .uv(114, 73).cuboid(-3.0F, -2.0F, -1.0F, 2.0F, 2.0F, 2.0F, new Dilation(-0.1F))
                .uv(17, 66).cuboid(2.0F, -2.0F, -1.0F, 1.0F, 2.0F, 2.0F, new Dilation(-0.2F))
                .uv(17, 66).cuboid(-5.0F, -2.0F, -1.0F, 1.0F, 2.0F, 2.0F, new Dilation(-0.2F))
                .uv(0, 89).cuboid(1.5F, -2.0F, -1.0F, 0.0F, 2.0F, 2.0F, new Dilation(0.001F))
                .uv(0, 89).cuboid(-3.5F, -2.0F, -1.0F, 0.0F, 2.0F, 2.0F, new Dilation(0.001F))
                .uv(107, 30).cuboid(-5.0F, -1.5F, -0.5F, 8.0F, 1.0F, 1.0F, new Dilation(-0.3F)), ModelTransform.pivot(-1.5F, -0.5F, 0.0F));

        ModelPartData middle_4 = controls.addChild("middle_4", ModelPartBuilder.create(), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -2.618F, 0.0F));

        ModelPartData rot13 = middle_4.addChild("rot13", ModelPartBuilder.create(), ModelTransform.of(21.0F, -13.0F, 0.0F, 0.0F, 0.0F, 0.48F));

        ModelPartData coil4 = rot13.addChild("coil4", ModelPartBuilder.create().uv(111, 109).cuboid(-3.5F, -2.0F, -1.0F, 5.0F, 2.0F, 2.0F, new Dilation(-0.3F))
                .uv(114, 73).cuboid(-1.0F, -2.0F, -1.0F, 2.0F, 2.0F, 2.0F, new Dilation(-0.1F))
                .uv(114, 73).cuboid(-3.0F, -2.0F, -1.0F, 2.0F, 2.0F, 2.0F, new Dilation(-0.1F))
                .uv(17, 66).cuboid(2.0F, -2.0F, -1.0F, 1.0F, 2.0F, 2.0F, new Dilation(-0.2F))
                .uv(17, 66).cuboid(-5.0F, -2.0F, -1.0F, 1.0F, 2.0F, 2.0F, new Dilation(-0.2F))
                .uv(0, 89).cuboid(1.5F, -2.0F, -1.0F, 0.0F, 2.0F, 2.0F, new Dilation(0.001F))
                .uv(0, 89).cuboid(-3.5F, -2.0F, -1.0F, 0.0F, 2.0F, 2.0F, new Dilation(0.001F))
                .uv(107, 30).cuboid(-5.0F, -1.5F, -0.5F, 8.0F, 1.0F, 1.0F, new Dilation(-0.3F)), ModelTransform.pivot(-1.5F, -0.5F, 0.0F));

        ModelPartData middle_5 = controls.addChild("middle_5", ModelPartBuilder.create(), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.5708F, 0.0F));

        ModelPartData rot14 = middle_5.addChild("rot14", ModelPartBuilder.create(), ModelTransform.of(21.0F, -13.0F, 0.0F, 0.0F, 0.0F, 0.48F));

        ModelPartData middle_6 = controls.addChild("middle_6", ModelPartBuilder.create(), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -0.5236F, 0.0F));

        ModelPartData rot15 = middle_6.addChild("rot15", ModelPartBuilder.create(), ModelTransform.of(21.0F, -13.0F, 0.0F, 0.0F, 0.0F, 0.48F));

        ModelPartData coil6 = rot15.addChild("coil6", ModelPartBuilder.create().uv(111, 109).cuboid(-3.5F, -2.0F, -1.0F, 5.0F, 2.0F, 2.0F, new Dilation(-0.3F))
                .uv(114, 73).cuboid(-1.0F, -2.0F, -1.0F, 2.0F, 2.0F, 2.0F, new Dilation(-0.1F))
                .uv(114, 73).cuboid(-3.0F, -2.0F, -1.0F, 2.0F, 2.0F, 2.0F, new Dilation(-0.1F))
                .uv(17, 66).cuboid(2.0F, -2.0F, -1.0F, 1.0F, 2.0F, 2.0F, new Dilation(-0.2F))
                .uv(17, 66).cuboid(-5.0F, -2.0F, -1.0F, 1.0F, 2.0F, 2.0F, new Dilation(-0.2F))
                .uv(0, 89).cuboid(1.5F, -2.0F, -1.0F, 0.0F, 2.0F, 2.0F, new Dilation(0.001F))
                .uv(0, 89).cuboid(-3.5F, -2.0F, -1.0F, 0.0F, 2.0F, 2.0F, new Dilation(0.001F))
                .uv(107, 30).cuboid(-5.0F, -1.5F, -0.5F, 8.0F, 1.0F, 1.0F, new Dilation(-0.3F)), ModelTransform.pivot(-1.5F, -0.5F, 0.0F));

        ModelPartData column = copper.addChild("column", ModelPartBuilder.create(), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.5708F, 0.0F));

        ModelPartData wood = column.addChild("wood", ModelPartBuilder.create(), ModelTransform.of(3.25F, -24.0F, -5.25F, 0.0785F, -0.5236F, 0.0F));

        ModelPartData bone = wood.addChild("bone", ModelPartBuilder.create().uv(0, 0).cuboid(-3.5F, -86.0F, -6.05F, 7.0F, 61.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(-0.0981F, 31.0F, 5.8301F));

        ModelPartData bone28 = wood.addChild("bone28", ModelPartBuilder.create().uv(17, 0).cuboid(-0.5F, -86.0F, -6.05F, 4.0F, 61.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-0.0981F, 31.0F, 5.8301F, 0.0F, 1.0472F, 0.0F));

        ModelPartData bone44 = wood.addChild("bone44", ModelPartBuilder.create().uv(17, 0).cuboid(-3.5F, -86.0F, -6.05F, 4.0F, 61.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-0.0981F, 31.0F, 5.8301F, 0.0F, -1.0472F, 0.0F));

        ModelPartData wood2 = column.addChild("wood2", ModelPartBuilder.create(), ModelTransform.of(-3.25F, -24.0F, 5.25F, -3.0631F, 0.5236F, -3.1416F));

        ModelPartData bone47 = wood2.addChild("bone47", ModelPartBuilder.create().uv(0, 0).cuboid(-3.5F, -86.0F, -6.05F, 7.0F, 61.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(-0.0981F, 31.0F, 5.8301F));

        ModelPartData bone48 = wood2.addChild("bone48", ModelPartBuilder.create().uv(17, 0).cuboid(-0.5F, -86.0F, -6.05F, 4.0F, 61.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-0.0981F, 31.0F, 5.8301F, 0.0F, 1.0472F, 0.0F));

        ModelPartData bone49 = wood2.addChild("bone49", ModelPartBuilder.create().uv(17, 0).cuboid(-3.5F, -86.0F, -6.05F, 4.0F, 61.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-0.0981F, 31.0F, 5.8301F, 0.0F, -1.0472F, 0.0F));

        ModelPartData rings = column.addChild("rings", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData bone51 = rings.addChild("bone51", ModelPartBuilder.create().uv(40, 111).cuboid(4.15F, -27.0F, -3.0F, 1.0F, 2.0F, 6.0F, new Dilation(0.0F))
                .uv(19, 165).cuboid(4.3F, -77.0F, -2.5F, 0.0F, 64.0F, 5.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -2.0F, 0.0F));

        ModelPartData bone52 = bone51.addChild("bone52", ModelPartBuilder.create().uv(40, 111).cuboid(4.15F, -27.0F, -3.0F, 1.0F, 2.0F, 6.0F, new Dilation(0.0F))
                .uv(19, 165).cuboid(4.3F, -77.0F, -2.5F, 0.0F, 64.0F, 5.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData bone53 = bone52.addChild("bone53", ModelPartBuilder.create().uv(40, 111).cuboid(4.15F, -27.0F, -3.0F, 1.0F, 2.0F, 6.0F, new Dilation(0.0F))
                .uv(19, 165).cuboid(4.3F, -77.0F, -2.5F, 0.0F, 64.0F, 5.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData bone54 = bone53.addChild("bone54", ModelPartBuilder.create().uv(40, 111).cuboid(4.15F, -27.0F, -3.0F, 1.0F, 2.0F, 6.0F, new Dilation(0.0F))
                .uv(19, 165).cuboid(4.3F, -77.0F, -2.5F, 0.0F, 64.0F, 5.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData bone55 = bone54.addChild("bone55", ModelPartBuilder.create().uv(40, 111).cuboid(4.15F, -27.0F, -3.0F, 1.0F, 2.0F, 6.0F, new Dilation(0.0F))
                .uv(19, 165).cuboid(4.3F, -77.0F, -2.5F, 0.0F, 64.0F, 5.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData bone56 = bone55.addChild("bone56", ModelPartBuilder.create().uv(40, 111).cuboid(4.15F, -27.0F, -3.0F, 1.0F, 2.0F, 6.0F, new Dilation(0.0F))
                .uv(19, 165).cuboid(4.3F, -77.0F, -2.5F, 0.0F, 64.0F, 5.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData bone109 = rings.addChild("bone109", ModelPartBuilder.create().uv(40, 111).cuboid(4.15F, -27.0F, -3.0F, 1.0F, 2.0F, 6.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 5.0F, 0.0F));

        ModelPartData bone110 = bone109.addChild("bone110", ModelPartBuilder.create().uv(40, 111).cuboid(4.15F, -27.0F, -3.0F, 1.0F, 2.0F, 6.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData bone111 = bone110.addChild("bone111", ModelPartBuilder.create().uv(40, 111).cuboid(4.15F, -27.0F, -3.0F, 1.0F, 2.0F, 6.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData bone112 = bone111.addChild("bone112", ModelPartBuilder.create().uv(40, 111).cuboid(4.15F, -27.0F, -3.0F, 1.0F, 2.0F, 6.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData bone113 = bone112.addChild("bone113", ModelPartBuilder.create().uv(40, 111).cuboid(4.15F, -27.0F, -3.0F, 1.0F, 2.0F, 6.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData bone114 = bone113.addChild("bone114", ModelPartBuilder.create().uv(40, 111).cuboid(4.15F, -27.0F, -3.0F, 1.0F, 2.0F, 6.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData bone57 = rings.addChild("bone57", ModelPartBuilder.create().uv(99, 86).cuboid(5.0F, -27.0F, -3.5F, 1.0F, 2.0F, 7.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -19.0F, 0.0F));

        ModelPartData bone58 = bone57.addChild("bone58", ModelPartBuilder.create().uv(99, 86).cuboid(5.0F, -27.0F, -3.5F, 1.0F, 2.0F, 7.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData bone59 = bone58.addChild("bone59", ModelPartBuilder.create().uv(99, 86).cuboid(5.0F, -27.0F, -3.5F, 1.0F, 2.0F, 7.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData bone60 = bone59.addChild("bone60", ModelPartBuilder.create().uv(99, 86).cuboid(5.0F, -27.0F, -3.5F, 1.0F, 2.0F, 7.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData bone61 = bone60.addChild("bone61", ModelPartBuilder.create().uv(99, 86).cuboid(5.0F, -27.0F, -3.5F, 1.0F, 2.0F, 7.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData bone62 = bone61.addChild("bone62", ModelPartBuilder.create().uv(99, 86).cuboid(5.0F, -27.0F, -3.5F, 1.0F, 2.0F, 7.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData bone63 = rings.addChild("bone63", ModelPartBuilder.create().uv(87, 90).cuboid(5.9F, -27.0F, -4.0F, 1.0F, 2.0F, 8.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -34.0F, 0.0F));

        ModelPartData bone64 = bone63.addChild("bone64", ModelPartBuilder.create().uv(87, 90).cuboid(5.9F, -27.0F, -4.0F, 1.0F, 2.0F, 8.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData bone65 = bone64.addChild("bone65", ModelPartBuilder.create().uv(87, 90).cuboid(5.9F, -27.0F, -4.0F, 1.0F, 2.0F, 8.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData bone66 = bone65.addChild("bone66", ModelPartBuilder.create().uv(87, 90).cuboid(5.9F, -27.0F, -4.0F, 1.0F, 2.0F, 8.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData bone67 = bone66.addChild("bone67", ModelPartBuilder.create().uv(87, 90).cuboid(5.9F, -27.0F, -4.0F, 1.0F, 2.0F, 8.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData bone68 = bone67.addChild("bone68", ModelPartBuilder.create().uv(87, 90).cuboid(5.9F, -27.0F, -4.0F, 1.0F, 2.0F, 8.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData bone69 = rings.addChild("bone69", ModelPartBuilder.create().uv(41, 84).cuboid(6.75F, -27.0F, -4.5F, 1.0F, 2.0F, 9.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -49.0F, 0.0F));

        ModelPartData bone70 = bone69.addChild("bone70", ModelPartBuilder.create().uv(41, 84).cuboid(6.75F, -27.0F, -4.5F, 1.0F, 2.0F, 9.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData bone71 = bone70.addChild("bone71", ModelPartBuilder.create().uv(41, 84).cuboid(6.75F, -27.0F, -4.5F, 1.0F, 2.0F, 9.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData bone72 = bone71.addChild("bone72", ModelPartBuilder.create().uv(41, 84).cuboid(6.75F, -27.0F, -4.5F, 1.0F, 2.0F, 9.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData bone73 = bone72.addChild("bone73", ModelPartBuilder.create().uv(41, 84).cuboid(6.75F, -27.0F, -4.5F, 1.0F, 2.0F, 9.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData bone74 = bone73.addChild("bone74", ModelPartBuilder.create().uv(41, 84).cuboid(6.75F, -27.0F, -4.5F, 1.0F, 2.0F, 9.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData bone50 = rings.addChild("bone50", ModelPartBuilder.create().uv(109, 80).cuboid(4.2F, -27.0F, -3.0F, 1.0F, 2.0F, 6.0F, new Dilation(0.0F))
                .uv(63, 12).cuboid(4.2F, -26.5F, 0.0F, 3.0F, 1.0F, 0.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -49.0F, 0.0F));

        ModelPartData bone79 = bone50.addChild("bone79", ModelPartBuilder.create().uv(109, 80).cuboid(4.2F, -27.0F, -3.0F, 1.0F, 2.0F, 6.0F, new Dilation(0.0F))
                .uv(63, 12).cuboid(4.2F, -26.5F, 0.0F, 3.0F, 1.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData bone80 = bone79.addChild("bone80", ModelPartBuilder.create().uv(109, 80).cuboid(4.2F, -27.0F, -3.0F, 1.0F, 2.0F, 6.0F, new Dilation(0.0F))
                .uv(63, 12).cuboid(4.2F, -26.5F, 0.0F, 3.0F, 1.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData bone81 = bone80.addChild("bone81", ModelPartBuilder.create().uv(109, 80).cuboid(4.2F, -27.0F, -3.0F, 1.0F, 2.0F, 6.0F, new Dilation(0.0F))
                .uv(63, 12).cuboid(4.2F, -26.5F, 0.0F, 3.0F, 1.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData bone82 = bone81.addChild("bone82", ModelPartBuilder.create().uv(109, 80).cuboid(4.2F, -27.0F, -3.0F, 1.0F, 2.0F, 6.0F, new Dilation(0.0F))
                .uv(63, 12).cuboid(4.2F, -26.5F, 0.0F, 3.0F, 1.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData bone83 = bone82.addChild("bone83", ModelPartBuilder.create().uv(109, 80).cuboid(4.2F, -27.0F, -3.0F, 1.0F, 2.0F, 6.0F, new Dilation(0.0F))
                .uv(63, 12).cuboid(4.2F, -26.5F, 0.0F, 3.0F, 1.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData bone45 = rings.addChild("bone45", ModelPartBuilder.create().uv(72, 48).cuboid(8.5F, -27.0F, -5.5F, 1.0F, 2.0F, 11.0F, new Dilation(0.0F))
                .uv(74, 112).cuboid(6.5F, -26.5F, -3.5F, 2.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -19.0F, 0.0F));

        ModelPartData bone46 = bone45.addChild("bone46", ModelPartBuilder.create().uv(72, 48).cuboid(8.5F, -27.0F, -5.5F, 1.0F, 2.0F, 11.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData bone75 = bone46.addChild("bone75", ModelPartBuilder.create().uv(72, 48).cuboid(8.5F, -27.0F, -5.5F, 1.0F, 2.0F, 11.0F, new Dilation(0.0F))
                .uv(74, 112).cuboid(6.5F, -26.5F, 2.5F, 2.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData bone76 = bone75.addChild("bone76", ModelPartBuilder.create().uv(72, 48).cuboid(8.5F, -27.0F, -5.5F, 1.0F, 2.0F, 11.0F, new Dilation(0.0F))
                .uv(74, 112).cuboid(6.5F, -26.5F, -3.5F, 2.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData bone77 = bone76.addChild("bone77", ModelPartBuilder.create().uv(72, 48).cuboid(8.5F, -27.0F, -5.5F, 1.0F, 2.0F, 11.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData bone78 = bone77.addChild("bone78", ModelPartBuilder.create().uv(72, 48).cuboid(8.5F, -27.0F, -5.5F, 1.0F, 2.0F, 11.0F, new Dilation(0.0F))
                .uv(74, 112).cuboid(6.5F, -26.5F, 2.5F, 2.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData rotor7 = column.addChild("rotor7", ModelPartBuilder.create().uv(0, 89).cuboid(-2.5F, -31.0F, -2.5F, 5.0F, 5.0F, 5.0F, new Dilation(0.5F))
                .uv(114, 67).cuboid(-1.0F, -34.5F, -1.0F, 2.0F, 3.0F, 2.0F, new Dilation(0.1F))
                .uv(33, 66).cuboid(-0.5F, -48.5F, -0.5F, 1.0F, 5.0F, 1.0F, new Dilation(-0.1F))
                .uv(62, 91).cuboid(-0.5F, -25.5F, -0.5F, 1.0F, 22.0F, 1.0F, new Dilation(-0.1F))
                .uv(106, 121).cuboid(-0.5F, -49.75F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(113, 93).cuboid(-1.5F, -37.5F, -1.5F, 3.0F, 3.0F, 3.0F, new Dilation(0.2F))
                .uv(113, 93).cuboid(-1.5F, -40.65F, -1.5F, 3.0F, 3.0F, 3.0F, new Dilation(0.0F))
                .uv(97, 40).cuboid(-1.0F, -44.0F, -1.0F, 2.0F, 1.0F, 2.0F, new Dilation(-0.2F))
                .uv(97, 40).cuboid(-1.0F, -49.0F, -1.0F, 2.0F, 1.0F, 2.0F, new Dilation(-0.2F))
                .uv(113, 93).cuboid(-1.5F, -43.4F, -1.5F, 3.0F, 3.0F, 3.0F, new Dilation(-0.2F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData top = column.addChild("top", ModelPartBuilder.create().uv(87, 12).cuboid(-2.5F, -62.0F, -2.5F, 5.0F, 5.0F, 5.0F, new Dilation(0.0F))
                .uv(31, 111).cuboid(-1.0F, -75.0F, -1.0F, 2.0F, 13.0F, 2.0F, new Dilation(0.3F))
                .uv(96, 23).cuboid(-1.0F, -75.0F, -1.0F, 2.0F, 1.0F, 2.0F, new Dilation(0.6F)), ModelTransform.pivot(0.0F, -3.5F, 0.0F));

        ModelPartData horn = column.addChild("horn", ModelPartBuilder.create().uv(28, 108).cuboid(0.0F, -2.0F, 0.0F, 3.0F, 2.0F, 0.0F, new Dilation(0.0F))
                .uv(17, 63).cuboid(4.0F, -6.0F, -0.9F, 4.0F, 1.0F, 1.0F, new Dilation(-0.1F))
                .uv(101, 121).cuboid(6.75F, -6.75F, 0.1F, 2.0F, 2.0F, 0.0F, new Dilation(0.0F))
                .uv(88, 89).cuboid(4.0F, -7.0F, 0.0F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F))
                .uv(40, 111).cuboid(4.0F, -8.75F, 0.0F, 1.0F, 2.0F, 1.0F, new Dilation(-0.1F)), ModelTransform.of(6.0F, -27.0F, -2.0F, 0.0252F, 0.523F, 0.0504F));

        ModelPartData cube_r306 = horn.addChild("cube_r306", ModelPartBuilder.create().uv(64, 112).cuboid(-0.6F, -2.75F, -1.0F, 3.0F, 3.0F, 3.0F, new Dilation(0.0F))
                .uv(100, 0).cuboid(2.4F, -3.75F, -2.0F, 3.0F, 5.0F, 5.0F, new Dilation(0.0F))
                .uv(60, 72).cuboid(5.5F, -5.75F, -4.0F, 0.0F, 9.0F, 9.0F, new Dilation(0.001F)), ModelTransform.of(4.5F, -8.0F, 0.0F, 0.0F, 0.0F, 0.1309F));

        ModelPartData cube_r307 = horn.addChild("cube_r307", ModelPartBuilder.create().uv(62, 48).cuboid(1.0F, 0.0F, 0.0F, 2.0F, 1.0F, 0.0F, new Dilation(0.0F))
                .uv(57, 48).cuboid(0.0F, -2.0F, 0.0F, 1.0F, 3.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(5.1492F, -3.3736F, 0.0F, 0.0F, 0.0F, -1.5708F));

        ModelPartData cube_r308 = horn.addChild("cube_r308", ModelPartBuilder.create().uv(92, 118).cuboid(0.0F, -2.0F, 0.0F, 4.0F, 2.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(3.0F, 0.0F, 0.0F, 0.0F, 0.0F, -1.0036F));

        ModelPartData rack = column.addChild("rack", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData bone86 = rack.addChild("bone86", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData bone84 = bone86.addChild("bone84", ModelPartBuilder.create(), ModelTransform.of(0.0F, 2.0F, 0.0F, 0.0F, 0.5236F, 0.0F));

        ModelPartData cube_r309 = bone84.addChild("cube_r309", ModelPartBuilder.create().uv(63, 30).cuboid(0.0F, -0.5F, -0.25F, 0.0F, 1.0F, 12.0F, new Dilation(0.0F)), ModelTransform.of(-2.0F, -46.0F, 9.0F, 0.0436F, 0.0F, 0.0F));

        ModelPartData cube_r310 = bone84.addChild("cube_r310", ModelPartBuilder.create().uv(63, 30).cuboid(0.0F, 0.0F, -0.25F, 0.0F, 1.0F, 12.0F, new Dilation(0.0F)), ModelTransform.of(-2.0F, -48.0F, 9.0F, -0.0436F, 0.0F, 0.0F));

        ModelPartData bone85 = bone86.addChild("bone85", ModelPartBuilder.create(), ModelTransform.of(0.0F, 2.0F, 0.0F, 0.0F, -1.5708F, 0.0F));

        ModelPartData cube_r311 = bone85.addChild("cube_r311", ModelPartBuilder.create().uv(63, 30).cuboid(0.0F, -0.5F, -0.25F, 0.0F, 1.0F, 12.0F, new Dilation(0.0F)), ModelTransform.of(2.0F, -46.0F, 9.0F, 0.0436F, 0.0F, 0.0F));

        ModelPartData cube_r312 = bone85.addChild("cube_r312", ModelPartBuilder.create().uv(63, 30).cuboid(0.0F, 0.0F, -0.25F, 0.0F, 1.0F, 12.0F, new Dilation(0.0F)), ModelTransform.of(2.0F, -48.0F, 9.0F, -0.0436F, 0.0F, 0.0F));

        ModelPartData bone87 = bone86.addChild("bone87", ModelPartBuilder.create().uv(72, 62).cuboid(-12.0F, -46.0F, 20.75F, 11.0F, 2.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.5236F, 0.0F));

        ModelPartData bone88 = bone87.addChild("bone88", ModelPartBuilder.create().uv(57, 45).cuboid(-12.0F, -46.0F, 20.75F, 24.0F, 2.0F, 0.0F, new Dilation(0.0F))
                .uv(139, 15).cuboid(-8.0F, -46.5F, 20.25F, 16.0F, 3.0F, 1.0F, new Dilation(-0.3F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone89 = bone88.addChild("bone89", ModelPartBuilder.create().uv(72, 62).cuboid(1.0F, -46.0F, 20.75F, 11.0F, 2.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone90 = rack.addChild("bone90", ModelPartBuilder.create(), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 3.1416F, 0.0F));

        ModelPartData bone91 = bone90.addChild("bone91", ModelPartBuilder.create(), ModelTransform.of(0.0F, 2.0F, 0.0F, 0.0F, 0.5236F, 0.0F));

        ModelPartData cube_r313 = bone91.addChild("cube_r313", ModelPartBuilder.create().uv(63, 30).cuboid(0.0F, -0.5F, -0.25F, 0.0F, 1.0F, 12.0F, new Dilation(0.0F)), ModelTransform.of(-2.0F, -46.0F, 9.0F, 0.0436F, 0.0F, 0.0F));

        ModelPartData cube_r314 = bone91.addChild("cube_r314", ModelPartBuilder.create().uv(63, 30).cuboid(0.0F, 0.0F, -0.25F, 0.0F, 1.0F, 12.0F, new Dilation(0.0F)), ModelTransform.of(-2.0F, -48.0F, 9.0F, -0.0436F, 0.0F, 0.0F));

        ModelPartData bone92 = bone90.addChild("bone92", ModelPartBuilder.create(), ModelTransform.of(0.0F, 2.0F, 0.0F, 0.0F, -1.5708F, 0.0F));

        ModelPartData cube_r315 = bone92.addChild("cube_r315", ModelPartBuilder.create().uv(63, 30).cuboid(0.0F, -0.5F, -0.25F, 0.0F, 1.0F, 12.0F, new Dilation(0.0F)), ModelTransform.of(2.0F, -46.0F, 9.0F, 0.0436F, 0.0F, 0.0F));

        ModelPartData cube_r316 = bone92.addChild("cube_r316", ModelPartBuilder.create().uv(63, 30).cuboid(0.0F, 0.0F, -0.25F, 0.0F, 1.0F, 12.0F, new Dilation(0.0F)), ModelTransform.of(2.0F, -48.0F, 9.0F, -0.0436F, 0.0F, 0.0F));

        ModelPartData bone93 = bone90.addChild("bone93", ModelPartBuilder.create().uv(72, 62).cuboid(-12.0F, -46.0F, 20.75F, 11.0F, 2.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.5236F, 0.0F));

        ModelPartData bone94 = bone93.addChild("bone94", ModelPartBuilder.create().uv(57, 45).cuboid(-12.0F, -46.0F, 20.75F, 24.0F, 2.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone95 = bone94.addChild("bone95", ModelPartBuilder.create().uv(72, 62).cuboid(1.0F, -46.0F, 20.75F, 11.0F, 2.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData scanner = rack.addChild("scanner", ModelPartBuilder.create(), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -0.5236F, 0.0F));

        ModelPartData monitor = scanner.addChild("monitor", ModelPartBuilder.create().uv(163, 183).cuboid(-6.0F, -5.0F, -2.0F, 12.0F, 10.0F, 4.0F, new Dilation(0.0F))
                .uv(155, 160).cuboid(-6.0F, -5.0F, -2.0F, 12.0F, 10.0F, 4.0F, new Dilation(-0.2F)), ModelTransform.of(0.0F, -31.0F, 20.75F, -0.3491F, 0.0F, 0.0F));

        ModelPartData redstreaks = monitor.addChild("redstreaks", ModelPartBuilder.create().uv(234, 60).cuboid(-4.0F, -2.5F, 1.825F, 8.0F, 6.0F, 0.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData screenplanets = monitor.addChild("screenplanets", ModelPartBuilder.create().uv(233, 141).cuboid(-4.0F, -2.5F, 1.9F, 8.0F, 6.0F, 0.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData planettopleft = screenplanets.addChild("planettopleft", ModelPartBuilder.create().uv(244, 117).cuboid(-1.0F, -0.5F, 0.0F, 2.0F, 1.0F, 0.0F, new Dilation(0.0F)), ModelTransform.pivot(3.0F, -1.0F, 1.9F));

        ModelPartData planetlowleft = screenplanets.addChild("planetlowleft", ModelPartBuilder.create().uv(244, 117).cuboid(-1.0F, -0.5F, 0.0F, 2.0F, 1.0F, 0.0F, new Dilation(0.0F)), ModelTransform.pivot(2.5F, 2.5F, 1.9F));

        ModelPartData planettopright = screenplanets.addChild("planettopright", ModelPartBuilder.create().uv(244, 117).cuboid(-1.0F, -0.5F, 0.0F, 2.0F, 1.0F, 0.0F, new Dilation(0.0F)), ModelTransform.pivot(-2.5F, -1.5F, 1.9F));

        ModelPartData bigplant = screenplanets.addChild("bigplant", ModelPartBuilder.create().uv(241, 133).cuboid(-2.0F, -2.0F, 0.0F, 4.0F, 4.0F, 0.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.5F, 1.9F));

        ModelPartData rack2 = scanner.addChild("rack2", ModelPartBuilder.create().uv(220, 209).cuboid(-8.0F, -40.0F, 20.0F, 16.0F, 14.0F, 0.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 2.0F, 0.75F));

        ModelPartData cube_r317 = rack2.addChild("cube_r317", ModelPartBuilder.create().uv(220, 231).cuboid(-8.0F, -0.5F, 0.0F, 9.0F, 1.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -46.25F, 19.9F, 0.0F, 0.0F, -0.2182F));

        ModelPartData cube_r318 = rack2.addChild("cube_r318", ModelPartBuilder.create().uv(220, 231).cuboid(-1.0F, -0.5F, 0.0F, 9.0F, 1.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -46.25F, 19.9F, 0.0F, 0.0F, 0.2182F));

        ModelPartData cube_r319 = rack2.addChild("cube_r319", ModelPartBuilder.create().uv(220, 231).cuboid(0.0F, -0.5F, 0.0F, 8.0F, 1.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -39.25F, 19.9F, 0.0F, 0.0F, -0.2182F));

        ModelPartData cube_r320 = rack2.addChild("cube_r320", ModelPartBuilder.create().uv(220, 231).cuboid(-8.0F, -0.5F, 0.0F, 8.0F, 1.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -39.25F, 19.9F, 0.0F, 0.0F, 0.2182F));

        ModelPartData cube_r321 = rack2.addChild("cube_r321", ModelPartBuilder.create().uv(220, 231).cuboid(-8.0F, -0.5F, 0.0F, 16.0F, 1.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -42.75F, 19.9F, 0.0F, 0.0F, -0.2182F));

        ModelPartData cube_r322 = rack2.addChild("cube_r322", ModelPartBuilder.create().uv(220, 231).cuboid(-8.0F, -0.5F, 0.0F, 16.0F, 1.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -42.75F, 19.9F, 0.0F, 0.0F, 0.2182F));

        ModelPartData cube_r323 = rack2.addChild("cube_r323", ModelPartBuilder.create().uv(220, 231).cuboid(-8.0F, -0.5F, 0.0F, 15.0F, 1.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -44.25F, 19.9F, 0.0F, 0.0F, -0.2182F));

        ModelPartData cube_r324 = rack2.addChild("cube_r324", ModelPartBuilder.create().uv(220, 231).cuboid(-7.0F, -0.5F, 0.0F, 15.0F, 1.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -44.25F, 19.9F, 0.0F, 0.0F, 0.2182F));

        ModelPartData cube_r325 = rack2.addChild("cube_r325", ModelPartBuilder.create().uv(220, 231).cuboid(-8.0F, -0.5F, 0.0F, 16.0F, 1.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -40.75F, 19.9F, 0.0F, 0.0F, 0.2182F));

        ModelPartData cube_r326 = rack2.addChild("cube_r326", ModelPartBuilder.create().uv(220, 231).cuboid(-8.0F, -0.5F, 0.0F, 16.0F, 1.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -40.75F, 19.9F, 0.0F, 0.0F, -0.2182F));
        return TexturedModelData.of(modelData, 256, 256);
    }
    @Override
    public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red,
                       float green, float blue, float alpha) {
        matrices.push();
        matrices.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(150f));

        copper.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
        matrices.pop();
    }

    @Override
    public void renderWithAnimations(ConsoleBlockEntity console, ClientTardis tardis, ModelPart root, MatrixStack matrices,
                                     VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float pAlpha) {
        matrices.push();
        matrices.translate(0.5f, -1.52f, -0.5f);
        matrices.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(150f));

        // fuel meter
        this.copper.getChild("pillars").getChild("pillars2").getChild("pillars3").getChild("pillars4").getChild("bone121").getChild("fuel_gauge").yaw = (float) (((tardis.getFuel() / FuelHandler.TARDIS_MAX_FUEL) * 3));

        // direction
        this.copper.getChild("pillars").getChild("pillars2").getChild("pillars3").getChild("pillars4").getChild("pillars5").getChild("bone126").pitch -= (float) (tardis.travel().destination().getRotation() * (1.5708f / 4f));

        // handbrake
        this.copper.getChild("pillars").getChild("pillars2").getChild("pillars3").getChild("handbrake").pitch += tardis.travel().handbrake() ? -1.0f : 0;

        // throttle
        ModelPart throttle1 = this.copper.getChild("controls").getChild("panel_2").getChild("rot8").getChild("lever2").getChild("bone29");
        ModelPart throttle2 = this.copper.getChild("controls").getChild("panel_2").getChild("rot8").getChild("lever2").getChild("bone30");

        ModelPart lights = this.copper.getChild("controls").getChild("panel_2").getChild("rot8").getChild("lever2").getChild("lights2").getChild("light");
        ModelPart lights2 = this.copper.getChild("controls").getChild("panel_2").getChild("rot8").getChild("lever2").getChild("lights2").getChild("light2");
        ModelPart lights3 = this.copper.getChild("controls").getChild("panel_2").getChild("rot8").getChild("lever2").getChild("lights2").getChild("light3");
        ModelPart lights4 = this.copper.getChild("controls").getChild("panel_2").getChild("rot8").getChild("lever2").getChild("lights2").getChild("light4");
        ModelPart lights5 = this.copper.getChild("controls").getChild("panel_2").getChild("rot8").getChild("lever2").getChild("lights2").getChild("light5");


        // this is horrible. good lord its horrible.
        float speedAmount = (tardis.travel().speed() / (float) tardis.travel().maxSpeed().get()) * 2f;
        float clampedSpeedAmount = (Math.min(Math.max(tardis.travel().speed() / (float) tardis.travel().maxSpeed().get(), 0), 5) * 5);
        throttle1.roll = throttle1.roll + (speedAmount);
        throttle2.roll = throttle2.roll + (speedAmount);
        lights.visible = clampedSpeedAmount >= 5;
        lights2.visible = clampedSpeedAmount >= 4;
        lights3.visible = clampedSpeedAmount >= 3;
        lights4.visible = clampedSpeedAmount >= 2;
        lights5.visible = clampedSpeedAmount >= 1;

        // cloak
        ModelPart cloak = this.copper.getChild("desktop").getChild("desktop2").getChild("desktop3").getChild("desktop4").getChild("desktop5").getChild("desktop6").getChild("panels12").getChild("rot6").getChild("bone150");
        cloak.roll += tardis.cloak().cloaked().get() ? 2f : 0.0f;

        // power
        ModelPart power = this.copper.getChild("desktop").getChild("desktop2").getChild("desktop3").getChild("desktop4").getChild("desktop5").getChild("desktop6").getChild("panels12").getChild("rot6").getChild("lever9").getChild("bone131");
        power.roll += tardis.fuel().hasPower() ? 0f : -1.0f;

        // siege mode
        ModelPart wibblyLever = this.copper.getChild("controls").getChild("panel_3").getChild("rot9").getChild("lever7").getChild("bone96");
        wibblyLever.roll += tardis.siege().isActive() ? -2.0f : 0;

        // security control
        ModelPart securityControl = this.copper.getChild("controls").getChild("panel_3").getChild("rot9").getChild("lever10").getChild("bone97");
        securityControl.roll += tardis.stats().security().get() ? 0.75f : 0;

        // stabilisers
        ModelPart stabilisers = this.copper.getChild("controls").getChild("panel_3").getChild("rot9").getChild("stabilizers").getChild("bone25");
        stabilisers.pivotY += tardis.travel().autopilot() ? 0.8f : 0;

        // door open?
        ModelPart doorControl = this.copper.getChild("controls").getChild("panel_2").getChild("rot8").getChild("crank").getChild("bone36");
        doorControl.pitch += !tardis.door().isRightOpen() ? tardis.door().isLeftOpen() ? -0.5f : 0.0f : -1.0f;

        // door locked?
        ModelPart doorLock = this.copper.getChild("controls").getChild("panel_4").getChild("rot16").getChild("crank3").getChild("bone101");
        doorLock.yaw += tardis.door().locked() ? 1.575f : 0.0f;

        // shields
        ModelPart shields = this.copper.getChild("controls").getChild("panel_4").getChild("rot16").getChild("t_switch").getChild("bone106");
        shields.pivotY += tardis.shields().shielded().get()
                ? 0.8f : 0;
        shields.pivotZ += tardis.shields().visuallyShielded().get() ? -0.4f : 0;

        // antigravs
        ModelPart antigravs = this.copper.getChild("controls").getChild("panel_4").getChild("rot16").getChild("lever8").getChild("bone103");
        antigravs.roll += tardis.travel().antigravs().get() ? 0f : -1f;

        super.renderWithAnimations(console, tardis, root, matrices, vertices, light, overlay, red, green, blue, pAlpha);
        matrices.pop();
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
        matrices.translate(-0.58, 1.28, 1.5f);
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180f));
        matrices.scale(0.005f, 0.005f, 0.005f);
        matrices.multiply(RotationAxis.NEGATIVE_Z.rotationDegrees(22.5f));
        matrices.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(-90f));
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
        matrices.translate(-0.45, 1.8, 0.66f);
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180f));
        matrices.scale(0.007f, 0.007f, 0.007f);
        matrices.multiply(RotationAxis.NEGATIVE_Z.rotationDegrees(22.5f));
        matrices.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(-90f));
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
        return copper;
    }

    @Override
    public Animation getAnimationForState(TravelHandlerBase.State state) {
        if (state.equals(TravelHandlerBase.State.LANDED)) {
            return CopperAnimations.COPPER_IDLE;
        }

        return CopperAnimations.COPPER_FLIGHT;
    }
}