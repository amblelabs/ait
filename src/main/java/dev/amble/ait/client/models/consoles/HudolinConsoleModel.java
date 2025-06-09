package dev.amble.ait.client.models.consoles;

import dev.amble.ait.client.animation.console.hudolin.HudolinAnimations;
import dev.amble.ait.client.tardis.ClientTardis;
import dev.amble.ait.core.blockentities.ConsoleBlockEntity;
import dev.amble.ait.core.tardis.Tardis;
import dev.amble.ait.core.tardis.control.impl.DirectionControl;
import dev.amble.ait.core.tardis.handler.travel.TravelHandler;
import dev.amble.ait.core.tardis.handler.travel.TravelHandlerBase;
import dev.amble.ait.core.util.WorldUtil;
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

public class HudolinConsoleModel extends ConsoleModel {
    private final ModelPart hudolin;

    public HudolinConsoleModel(ModelPart root) {
        this.hudolin = root.getChild("hudolin");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData hudolin = modelPartData.addChild("hudolin", ModelPartBuilder.create().uv(0, -5).cuboid(0.0F, -63.0F, 5.0F, 0.0F, 12.0F, 5.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 24.0F, 0.0F));

        ModelPartData cube_r1 = hudolin.addChild("cube_r1", ModelPartBuilder.create().uv(0, -5).cuboid(0.0F, -63.0F, 5.0F, 0.0F, 12.0F, 5.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.5708F, 0.0F));

        ModelPartData cube_r2 = hudolin.addChild("cube_r2", ModelPartBuilder.create().uv(0, -5).cuboid(0.0F, -63.0F, 5.0F, 0.0F, 12.0F, 5.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, -3.1416F, 0.0F, 3.1416F));

        ModelPartData cube_r3 = hudolin.addChild("cube_r3", ModelPartBuilder.create().uv(0, -5).cuboid(0.0F, -63.0F, 5.0F, 0.0F, 12.0F, 5.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.5708F, 0.0F));

        ModelPartData bone = hudolin.addChild("bone", ModelPartBuilder.create().uv(0, 0).cuboid(-10.5F, -16.0F, -18.25F, 21.0F, 1.0F, 18.0F, new Dilation(0.085F))
                .uv(0, 19).cuboid(-10.0F, -15.0F, -17.35F, 20.0F, 1.0F, 18.0F, new Dilation(0.041F))
                .uv(78, 0).cuboid(-3.0F, -22.925F, -5.25F, 6.0F, 1.0F, 5.0F, new Dilation(0.0725F))
                .uv(64, 66).cuboid(-3.5F, -57.0F, -6.0F, 7.0F, 3.0F, 5.0F, new Dilation(-0.081F))
                .uv(0, 72).cuboid(-3.5F, -52.9F, -6.0F, 7.0F, 2.0F, 5.0F, new Dilation(-0.086F))
                .uv(58, 38).cuboid(-3.5F, -64.0F, -6.0F, 7.0F, 5.0F, 6.0F, new Dilation(-0.085F))
                .uv(64, 58).cuboid(-4.0F, -59.95F, -6.95F, 8.0F, 3.0F, 5.0F, new Dilation(0.026F))
                .uv(64, 58).cuboid(-4.0F, -61.0F, -6.95F, 8.0F, 0.0F, 5.0F, new Dilation(0.001F))
                .uv(64, 58).cuboid(-4.0F, -62.0F, -6.95F, 8.0F, 0.0F, 5.0F, new Dilation(0.001F))
                .uv(64, 58).cuboid(-4.0F, -63.0F, -6.95F, 8.0F, 0.0F, 5.0F, new Dilation(0.001F))
                .uv(26, 65).cuboid(-3.0F, -24.75F, -5.0F, 6.0F, 2.0F, 6.0F, new Dilation(-0.27F))
                .uv(0, 81).cuboid(-2.25F, -24.25F, -5.0F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(0, 81).cuboid(-0.5F, -24.25F, -5.0F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(0, 81).cuboid(1.25F, -24.25F, -5.0F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(49, 92).cuboid(-3.5F, -51.2F, -5.9F, 7.0F, 2.0F, 7.0F, new Dilation(-0.225F))
                .uv(24, 73).cuboid(-3.5F, -54.5F, -5.85F, 7.0F, 2.0F, 5.0F, new Dilation(-0.29F))
                .uv(76, 19).cuboid(-3.0F, -14.0F, -5.25F, 6.0F, 2.0F, 5.0F, new Dilation(0.0F))
                .uv(76, 26).cuboid(-3.0F, -3.0F, -5.25F, 6.0F, 2.0F, 5.0F, new Dilation(0.075F))
                .uv(0, 79).cuboid(-2.5F, -6.0F, -4.75F, 5.0F, 3.0F, 4.0F, new Dilation(0.0F))
                .uv(54, 50).cuboid(-3.5F, -1.0F, -6.05F, 7.0F, 1.0F, 7.0F, new Dilation(-0.019F))
                .uv(88, 10).cuboid(-2.5F, -12.0F, -4.325F, 5.0F, 6.0F, 0.0F, new Dilation(0.0F))
                .uv(54, 58).cuboid(-2.5F, -49.4F, -4.325F, 5.0F, 25.0F, 0.0F, new Dilation(-0.001F))
                .uv(0, 81).cuboid(-1.5F, -5.0F, -5.0F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(0, 81).cuboid(0.5F, -5.0F, -5.0F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(0, 81).cuboid(-2.75F, -50.75F, -5.9F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(0, 81).cuboid(-0.5F, -50.75F, -5.9F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(0, 81).cuboid(1.75F, -50.75F, -5.9F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(0, 81).cuboid(-2.75F, -56.0F, -6.25F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(0, 81).cuboid(-0.5F, -56.0F, -6.25F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(0, 81).cuboid(1.75F, -56.0F, -6.25F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData cube_r4 = bone.addChild("cube_r4", ModelPartBuilder.create().uv(15, 50).mirrored().cuboid(-5.5F, -0.125F, 1.4F, 3.0F, 0.0F, 5.0F, new Dilation(0.001F)).mirrored(false)
                .uv(60, 0).cuboid(-5.25F, -0.225F, 1.7F, 3.0F, 0.0F, 5.0F, new Dilation(0.001F))
                .uv(15, 55).cuboid(2.25F, -0.225F, 1.7F, 3.0F, 0.0F, 5.0F, new Dilation(0.001F))
                .uv(15, 50).cuboid(2.5F, -0.125F, 1.4F, 3.0F, 0.0F, 5.0F, new Dilation(0.001F))
                .uv(82, 104).cuboid(-8.5F, -0.025F, 0.0F, 17.0F, 0.0F, 12.0F, new Dilation(0.001F))
                .uv(30, 53).cuboid(-2.5F, -0.4F, 0.3F, 5.0F, 1.0F, 0.0F, new Dilation(0.001F))
                .uv(42, 54).cuboid(-2.75F, -0.1F, 0.5F, 5.0F, 1.0F, 2.0F, new Dilation(0.0F))
                .uv(26, 50).cuboid(-2.5F, -0.4F, -0.2F, 5.0F, 1.0F, 2.0F, new Dilation(0.001F))
                .uv(98, 18).cuboid(-8.5F, 0.0F, 0.0F, 17.0F, 0.0F, 12.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, -16.0F, -14.5F, 0.6109F, 0.0F, 0.0F));

        ModelPartData cube_r5 = bone.addChild("cube_r5", ModelPartBuilder.create().uv(76, 33).cuboid(-2.5F, -0.1F, -2.5F, 5.0F, 0.0F, 5.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, -18.9498F, -10.4616F, 0.6109F, 0.0F, 0.0F));

        ModelPartData cube_r6 = bone.addChild("cube_r6", ModelPartBuilder.create().uv(10, 0).cuboid(-0.7F, -0.5F, 0.0F, 1.0F, 1.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, -17.3108F, -14.1971F, 0.6109F, 0.0F, 0.0F));

        ModelPartData hail_mary = bone.addChild("hail_mary", ModelPartBuilder.create(), ModelTransform.pivot(-1.2F, -17.3108F, -14.1971F));

        ModelPartData cube_r7 = hail_mary.addChild("cube_r7", ModelPartBuilder.create().uv(10, 0).cuboid(-0.5F, -0.5F, 0.0F, 1.0F, 1.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.6981F, 0.0F, 0.0F));

        ModelPartData cube_r8 = hail_mary.addChild("cube_r8", ModelPartBuilder.create().uv(10, 0).cuboid(-0.5F, -0.5F, 0.0F, 1.0F, 1.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(-1.0F, 0.0F, 0.0F, 0.48F, 0.0F, 0.0F));

        ModelPartData cloak = bone.addChild("cloak", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, -17.3108F, -14.1971F));

        ModelPartData cube_r9 = cloak.addChild("cube_r9", ModelPartBuilder.create().uv(10, 0).cuboid(1.3F, -0.5F, 0.0F, 1.0F, 1.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.6109F, 0.0F, 0.0F));

        ModelPartData cube_r10 = cloak.addChild("cube_r10", ModelPartBuilder.create().uv(10, 0).cuboid(-0.5F, -0.5F, 0.0F, 1.0F, 1.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(0.8F, 0.0F, 0.0F, 0.5236F, 0.0F, 0.0F));

        ModelPartData monitordontask = bone.addChild("monitordontask", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, -16.0F, -14.5F));

        ModelPartData cube_r11 = monitordontask.addChild("cube_r11", ModelPartBuilder.create().uv(110, 128).cuboid(-2.5F, -1.5F, -2.5F, 5.0F, 4.0F, 4.0F, new Dilation(-0.1F))
                .uv(92, 128).cuboid(-2.5F, -1.5F, -2.5F, 5.0F, 4.0F, 4.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, -3.0317F, 3.981F, 1.3963F, 0.0F, 0.0F));

        ModelPartData cube_r12 = monitordontask.addChild("cube_r12", ModelPartBuilder.create().uv(14, 10).cuboid(-0.5F, -0.7F, 4.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.6109F, 0.0F, 0.0F));

        ModelPartData cube_r13 = monitordontask.addChild("cube_r13", ModelPartBuilder.create().uv(75, 50).cuboid(-2.5F, 0.0F, -2.5F, 5.0F, 0.0F, 5.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, -2.9498F, 4.0384F, 0.6109F, 0.0F, 0.0F));

        ModelPartData lamp = bone.addChild("lamp", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, -16.0F, -14.5F));

        ModelPartData cube_r14 = lamp.addChild("cube_r14", ModelPartBuilder.create().uv(6, 27).cuboid(-3.0F, -1.7F, 8.0F, 1.0F, 2.0F, 1.0F, new Dilation(-0.25F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.6109F, 0.0F, 0.0F));

        ModelPartData cube_r15 = lamp.addChild("cube_r15", ModelPartBuilder.create().uv(13, 19).cuboid(-0.5F, -1.0F, -0.5F, 1.0F, 2.0F, 1.0F, new Dilation(-0.251F)), ModelTransform.of(-2.6564F, -6.6186F, 5.7422F, 0.5996F, 0.1245F, -0.1796F));

        ModelPartData cube_r16 = lamp.addChild("cube_r16", ModelPartBuilder.create().uv(9, 27).cuboid(-0.5F, -1.0F, -1.5F, 1.0F, 1.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(-5.1482F, -7.8988F, 4.8458F, 0.1504F, 0.5944F, -1.3065F));

        ModelPartData cube_r17 = lamp.addChild("cube_r17", ModelPartBuilder.create().uv(12, 34).cuboid(-0.5F, -1.0F, -0.5F, 1.0F, 2.0F, 1.0F, new Dilation(-0.251F)), ModelTransform.of(-4.66F, -7.8102F, 4.9078F, 0.1504F, 0.5944F, -1.3065F));

        ModelPartData cube_r18 = lamp.addChild("cube_r18", ModelPartBuilder.create().uv(12, 31).cuboid(-0.5F, -1.0F, -0.5F, 1.0F, 2.0F, 1.0F, new Dilation(-0.25F)), ModelTransform.of(-3.3492F, -7.3926F, 5.2002F, 0.3368F, 0.5198F, -0.9568F));

        ModelPartData bone3 = bone.addChild("bone3", ModelPartBuilder.create(), ModelTransform.of(0.0F, -16.0F, 0.0F, 0.0F, 0.5236F, 0.0F));

        ModelPartData cube_r19 = bone3.addChild("cube_r19", ModelPartBuilder.create().uv(87, 66).cuboid(-1.5F, -2.0F, -3.0F, 2.0F, 2.0F, 3.0F, new Dilation(0.001F)), ModelTransform.of(0.5F, 15.2426F, -8.3284F, 0.2618F, 0.0F, 0.0F));

        ModelPartData cube_r20 = bone3.addChild("cube_r20", ModelPartBuilder.create().uv(18, 80).cuboid(-0.5F, 0.0F, -5.0F, 1.0F, 1.0F, 6.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 11.0F, -5.5F, 0.7854F, 0.0F, 0.0F));

        ModelPartData cube_r21 = bone3.addChild("cube_r21", ModelPartBuilder.create().uv(76, 77).cuboid(-0.5F, 0.0F, 0.0F, 1.0F, 1.0F, 3.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 13.0F, -5.5F, 1.5708F, 0.0F, 0.0F));

        ModelPartData cube_r22 = bone3.addChild("cube_r22", ModelPartBuilder.create().uv(0, 50).cuboid(-0.5F, 0.0F, 0.0F, 1.0F, 1.0F, 13.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, -16.75F, 0.5498F, 0.0F, 0.0F));

        ModelPartData bone9 = hudolin.addChild("bone9", ModelPartBuilder.create().uv(0, 0).cuboid(-10.5F, -16.0F, -18.25F, 21.0F, 1.0F, 18.0F, new Dilation(0.085F))
                .uv(0, 19).cuboid(-10.0F, -15.0F, -17.35F, 20.0F, 1.0F, 18.0F, new Dilation(0.041F))
                .uv(78, 0).cuboid(-3.0F, -22.925F, -5.25F, 6.0F, 1.0F, 5.0F, new Dilation(0.0725F))
                .uv(64, 66).cuboid(-3.5F, -57.0F, -6.0F, 7.0F, 3.0F, 5.0F, new Dilation(-0.081F))
                .uv(0, 72).cuboid(-3.5F, -52.9F, -6.0F, 7.0F, 2.0F, 5.0F, new Dilation(-0.086F))
                .uv(58, 38).cuboid(-3.5F, -64.0F, -6.0F, 7.0F, 5.0F, 6.0F, new Dilation(-0.085F))
                .uv(64, 58).cuboid(-4.0F, -59.95F, -6.95F, 8.0F, 3.0F, 5.0F, new Dilation(0.026F))
                .uv(64, 58).cuboid(-4.0F, -61.0F, -6.95F, 8.0F, 0.0F, 5.0F, new Dilation(0.0F))
                .uv(64, 58).cuboid(-4.0F, -62.0F, -6.95F, 8.0F, 0.0F, 5.0F, new Dilation(0.0F))
                .uv(64, 58).cuboid(-4.0F, -63.0F, -6.95F, 8.0F, 0.0F, 5.0F, new Dilation(0.0F))
                .uv(26, 65).cuboid(-3.0F, -24.75F, -5.0F, 6.0F, 2.0F, 6.0F, new Dilation(-0.27F))
                .uv(49, 92).cuboid(-3.5F, -51.2F, -5.9F, 7.0F, 2.0F, 7.0F, new Dilation(-0.225F))
                .uv(0, 81).cuboid(-2.75F, -50.75F, -5.9F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(0, 81).cuboid(-0.5F, -50.75F, -5.9F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(0, 81).cuboid(1.75F, -50.75F, -5.9F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(24, 73).cuboid(-3.5F, -54.5F, -5.85F, 7.0F, 2.0F, 5.0F, new Dilation(-0.29F))
                .uv(76, 19).cuboid(-3.0F, -14.0F, -5.25F, 6.0F, 2.0F, 5.0F, new Dilation(0.0F))
                .uv(76, 26).cuboid(-3.0F, -3.0F, -5.25F, 6.0F, 2.0F, 5.0F, new Dilation(0.075F))
                .uv(0, 79).cuboid(-2.5F, -6.0F, -4.75F, 5.0F, 3.0F, 4.0F, new Dilation(0.0F))
                .uv(0, 81).cuboid(-1.5F, -5.0F, -5.0F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(0, 81).cuboid(0.5F, -5.0F, -5.0F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(54, 50).cuboid(-3.5F, -1.0F, -6.05F, 7.0F, 1.0F, 7.0F, new Dilation(-0.019F))
                .uv(78, 10).cuboid(-2.5F, -12.0F, -4.325F, 5.0F, 6.0F, 0.0F, new Dilation(0.0F))
                .uv(54, 58).cuboid(-2.5F, -49.4F, -4.325F, 5.0F, 25.0F, 0.0F, new Dilation(-0.001F))
                .uv(0, 81).cuboid(1.25F, -24.25F, -5.0F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(0, 81).cuboid(-0.5F, -24.25F, -5.0F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(0, 81).cuboid(-2.25F, -24.25F, -5.0F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(0, 81).cuboid(-2.75F, -56.0F, -6.25F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(0, 81).cuboid(-0.5F, -56.0F, -6.25F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(0, 81).cuboid(1.75F, -56.0F, -6.25F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(0, 0).cuboid(-1.0F, -2.0F, -1.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 3.1416F, 0.0F));

        ModelPartData cube_r23 = bone9.addChild("cube_r23", ModelPartBuilder.create().uv(36, 140).cuboid(-8.5F, -0.125F, 0.0F, 17.0F, 0.0F, 12.0F, new Dilation(0.001F))
                .uv(14, 116).cuboid(-8.5F, -0.075F, 0.0F, 17.0F, 0.0F, 12.0F, new Dilation(0.001F))
                .uv(8, 40).cuboid(-0.5F, -0.8F, 6.1F, 1.0F, 1.0F, 1.0F, new Dilation(0.001F))
                .uv(0, 128).cuboid(-8.5F, 0.1F, 0.0F, 17.0F, 0.0F, 12.0F, new Dilation(0.001F))
                .uv(110, 42).cuboid(-8.5F, 0.0F, 0.0F, 17.0F, 0.0F, 12.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, -16.0F, -14.5F, 0.6109F, 0.0F, 0.0F));

        ModelPartData p19 = bone9.addChild("p19", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, -16.0F, -14.5F));

        ModelPartData cube_r24 = p19.addChild("cube_r24", ModelPartBuilder.create().uv(0, 40).cuboid(1.25F, -0.4F, 2.225F, 1.0F, 1.0F, 1.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.6109F, 0.0F, 0.0F));

        ModelPartData mark_waypoint = bone9.addChild("mark_waypoint", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, -16.0F, -14.5F));

        ModelPartData cube_r25 = mark_waypoint.addChild("cube_r25", ModelPartBuilder.create().uv(0, 40).cuboid(-1.0F, -0.4F, 1.7F, 1.0F, 1.0F, 1.0F, new Dilation(0.001F))
                .uv(0, 40).cuboid(-2.7F, -0.4F, 1.7F, 1.0F, 1.0F, 1.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.6109F, 0.0F, 0.0F));

        ModelPartData cube_r26 = mark_waypoint.addChild("cube_r26", ModelPartBuilder.create().uv(0, 40).cuboid(-1.2F, 0.1F, -1.25F, 1.0F, 1.0F, 1.0F, new Dilation(-0.1F))
                .uv(0, 40).cuboid(0.5F, 0.1F, -1.25F, 1.0F, 1.0F, 1.0F, new Dilation(-0.1F)), ModelTransform.of(-1.5F, -2.4293F, 1.9003F, 0.6109F, 0.0F, 0.0F));

        ModelPartData set_waypoint = bone9.addChild("set_waypoint", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, -16.0F, -14.5F));

        ModelPartData cube_r27 = set_waypoint.addChild("cube_r27", ModelPartBuilder.create().uv(0, 40).cuboid(-3.2F, -0.4F, 3.2F, 1.0F, 1.0F, 1.0F, new Dilation(0.001F))
                .uv(0, 40).cuboid(-1.5F, -0.4F, 3.2F, 1.0F, 1.0F, 1.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.6109F, 0.0F, 0.0F));

        ModelPartData cube_r28 = set_waypoint.addChild("cube_r28", ModelPartBuilder.create().uv(0, 40).cuboid(-1.7F, 0.1F, 0.25F, 1.0F, 1.0F, 1.0F, new Dilation(-0.1F))
                .uv(0, 40).cuboid(1.0F, 0.5F, 0.25F, 1.0F, 1.0F, 1.0F, new Dilation(-0.1F))
                .uv(0, 40).cuboid(0.0F, 0.1F, 0.25F, 1.0F, 1.0F, 1.0F, new Dilation(-0.1F))
                .uv(0, 40).cuboid(1.0F, 0.0F, 0.25F, 1.0F, 1.0F, 1.0F, new Dilation(-0.2F)), ModelTransform.of(-1.5F, -2.4293F, 1.9003F, 0.6109F, 0.0F, 0.0F));

        ModelPartData bone10 = bone9.addChild("bone10", ModelPartBuilder.create(), ModelTransform.of(0.0F, -16.0F, 0.0F, 0.0F, 0.5236F, 0.0F));

        ModelPartData donottexture_r1 = bone10.addChild("donottexture_r1", ModelPartBuilder.create().uv(42, 57).cuboid(-1.5F, -1.0F, 12.0F, 2.0F, 5.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.5F, 14.025F, -13.5F, 1.5708F, 0.0F, 0.0F));

        ModelPartData donottexture_r2 = bone10.addChild("donottexture_r2", ModelPartBuilder.create().uv(28, 50).cuboid(-0.5F, 0.0F, 0.0F, 1.0F, 3.0F, 12.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 10.0F, -5.5F, 2.3998F, 0.0F, 0.0F));

        ModelPartData cube_r29 = bone10.addChild("cube_r29", ModelPartBuilder.create().uv(64, 74).cuboid(-0.5F, 0.0F, 0.0F, 1.0F, 1.0F, 9.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 13.0F, -5.5F, 1.5708F, 0.0F, 0.0F));

        ModelPartData cube_r30 = bone10.addChild("cube_r30", ModelPartBuilder.create().uv(0, 50).cuboid(-0.5F, 0.0F, 0.0F, 1.0F, 1.0F, 13.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, -16.75F, 0.5498F, 0.0F, 0.0F));

        ModelPartData antigravs = bone9.addChild("antigravs", ModelPartBuilder.create().uv(2, 1).cuboid(-5.75F, -18.0F, -13.25F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData land_type = bone9.addChild("land_type", ModelPartBuilder.create().uv(2, 1).cuboid(4.75F, -18.0F, -13.25F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData waypoint_disc_port_whatever = bone9.addChild("waypoint_disc_port_whatever", ModelPartBuilder.create().uv(1, 1).cuboid(-0.75F, -22.5F, -7.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData bone5 = hudolin.addChild("bone5", ModelPartBuilder.create().uv(0, 0).cuboid(-10.5F, -16.0F, -18.25F, 21.0F, 1.0F, 18.0F, new Dilation(0.085F))
                .uv(0, 19).cuboid(2.2F, -16.2F, -18.15F, 5.0F, 1.0F, 3.0F, new Dilation(0.0F))
                .uv(14, 3).cuboid(2.2F, -17.4F, -18.15F, 1.0F, 2.0F, 1.0F, new Dilation(-0.3F))
                .uv(14, 3).cuboid(2.2F, -17.4F, -17.15F, 1.0F, 2.0F, 1.0F, new Dilation(-0.3F))
                .uv(14, 3).cuboid(2.2F, -17.4F, -16.15F, 1.0F, 2.0F, 1.0F, new Dilation(-0.3F))
                .uv(14, 3).cuboid(6.2F, -17.4F, -16.15F, 1.0F, 2.0F, 1.0F, new Dilation(-0.3F))
                .uv(14, 3).cuboid(6.2F, -17.4F, -18.15F, 1.0F, 2.0F, 1.0F, new Dilation(-0.3F))
                .uv(14, 3).cuboid(6.2F, -17.4F, -17.15F, 1.0F, 2.0F, 1.0F, new Dilation(-0.3F))
                .uv(0, 19).cuboid(-10.0F, -15.0F, -17.35F, 20.0F, 1.0F, 18.0F, new Dilation(0.041F))
                .uv(78, 0).cuboid(-3.0F, -22.925F, -5.25F, 6.0F, 1.0F, 5.0F, new Dilation(0.0725F))
                .uv(64, 66).cuboid(-3.5F, -57.0F, -6.0F, 7.0F, 3.0F, 5.0F, new Dilation(-0.085F))
                .uv(0, 81).cuboid(-2.75F, -56.0F, -6.25F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(0, 81).cuboid(-0.5F, -56.0F, -6.25F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(0, 81).cuboid(1.75F, -56.0F, -6.25F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(0, 72).cuboid(-3.5F, -52.9F, -6.0F, 7.0F, 2.0F, 5.0F, new Dilation(-0.086F))
                .uv(58, 38).cuboid(-3.5F, -64.0F, -6.0F, 7.0F, 5.0F, 6.0F, new Dilation(-0.085F))
                .uv(64, 58).cuboid(-4.0F, -59.95F, -6.95F, 8.0F, 3.0F, 5.0F, new Dilation(0.026F))
                .uv(64, 58).cuboid(-4.0F, -61.0F, -6.95F, 8.0F, 0.0F, 5.0F, new Dilation(0.0F))
                .uv(64, 58).cuboid(-4.0F, -62.0F, -6.95F, 8.0F, 0.0F, 5.0F, new Dilation(0.0F))
                .uv(64, 58).cuboid(-4.0F, -63.0F, -6.95F, 8.0F, 0.0F, 5.0F, new Dilation(0.0F))
                .uv(26, 65).cuboid(-3.0F, -24.75F, -5.0F, 6.0F, 2.0F, 6.0F, new Dilation(-0.27F))
                .uv(49, 92).cuboid(-3.5F, -51.2F, -5.9F, 7.0F, 2.0F, 7.0F, new Dilation(-0.225F))
                .uv(24, 73).cuboid(-3.5F, -54.5F, -5.85F, 7.0F, 2.0F, 5.0F, new Dilation(-0.29F))
                .uv(76, 19).cuboid(-3.0F, -14.0F, -5.25F, 6.0F, 2.0F, 5.0F, new Dilation(0.0F))
                .uv(76, 26).cuboid(-3.0F, -3.0F, -5.25F, 6.0F, 2.0F, 5.0F, new Dilation(0.075F))
                .uv(0, 79).cuboid(-2.5F, -6.0F, -4.75F, 5.0F, 3.0F, 4.0F, new Dilation(0.0F))
                .uv(54, 50).cuboid(-3.5F, -1.0F, -6.05F, 7.0F, 1.0F, 7.0F, new Dilation(-0.019F))
                .uv(78, 10).cuboid(-2.5F, -12.0F, -4.325F, 5.0F, 6.0F, 0.0F, new Dilation(0.0F))
                .uv(54, 58).cuboid(-2.5F, -49.4F, -4.325F, 5.0F, 25.0F, 0.0F, new Dilation(-0.001F))
                .uv(0, 81).cuboid(-1.5F, -5.0F, -5.0F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(0, 81).cuboid(0.5F, -5.0F, -5.0F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(0, 81).cuboid(1.25F, -24.25F, -5.0F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(0, 81).cuboid(-0.5F, -24.25F, -5.0F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(0, 81).cuboid(-2.25F, -24.25F, -5.0F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(0, 81).cuboid(-2.75F, -50.75F, -5.9F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(0, 81).cuboid(-0.5F, -50.75F, -5.9F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(0, 81).cuboid(1.75F, -50.75F, -5.9F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData cube_r31 = bone5.addChild("cube_r31", ModelPartBuilder.create().uv(48, 116).cuboid(-8.5F, -0.025F, 0.0F, 17.0F, 0.0F, 12.0F, new Dilation(0.001F))
                .uv(0, 14).cuboid(-2.5F, 0.1F, 5.0F, 5.0F, 0.0F, 2.0F, new Dilation(0.001F))
                .uv(0, 16).cuboid(-1.5F, -0.1F, 0.0F, 3.0F, 0.0F, 2.0F, new Dilation(0.001F))
                .uv(60, 6).cuboid(-6.5F, 0.1F, 0.0F, 13.0F, 0.0F, 2.0F, new Dilation(0.001F))
                .uv(98, 6).cuboid(-8.5F, 0.0F, 0.0F, 17.0F, 0.0F, 12.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, -16.0F, -14.5F, 0.6109F, 0.0F, 0.0F));

        ModelPartData cube_r32 = bone5.addChild("cube_r32", ModelPartBuilder.create().uv(10, 5).mirrored().cuboid(-0.5F, -0.4F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.19F)).mirrored(false), ModelTransform.of(-2.5F, -20.5658F, -7.0504F, 0.7805F, 0.6178F, 0.5208F));

        ModelPartData cube_r33 = bone5.addChild("cube_r33", ModelPartBuilder.create().uv(10, 5).cuboid(-0.5F, -0.4F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.19F))
                .uv(24, 27).mirrored().cuboid(-0.5F, -0.7F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.2F)).mirrored(false), ModelTransform.of(2.5F, -20.5658F, -7.0504F, 0.7805F, -0.6178F, -0.5208F));

        ModelPartData cube_r34 = bone5.addChild("cube_r34", ModelPartBuilder.create().uv(0, 12).mirrored().cuboid(-0.7F, 1.6F, -0.25F, 1.0F, 0.0F, 1.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(-2.5F, -22.368F, -8.3123F, 1.5708F, 0.9599F, 1.5708F));

        ModelPartData cube_r35 = bone5.addChild("cube_r35", ModelPartBuilder.create().uv(0, 12).cuboid(-0.3F, 1.6F, -0.25F, 1.0F, 0.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(2.5F, -22.368F, -8.3123F, 1.5708F, -0.9599F, -1.5708F));

        ModelPartData cube_r36 = bone5.addChild("cube_r36", ModelPartBuilder.create().uv(10, 3).cuboid(-0.5F, -0.7F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.2F)), ModelTransform.of(-2.5F, -20.5658F, -7.0504F, 0.7805F, -0.6178F, -0.5208F));

        ModelPartData cube_r37 = bone5.addChild("cube_r37", ModelPartBuilder.create().uv(10, 7).cuboid(-1.0F, -0.5F, -1.0F, 2.0F, 1.0F, 2.0F, new Dilation(0.0F))
                .uv(10, 0).cuboid(-1.0F, -0.6F, -1.0F, 2.0F, 1.0F, 2.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, -21.2164F, -6.4556F, 0.7805F, 0.6178F, 0.5208F));

        ModelPartData cube_r38 = bone5.addChild("cube_r38", ModelPartBuilder.create().uv(4, 12).cuboid(-3.2F, -0.2F, -1.7F, 1.0F, 1.0F, 1.0F, new Dilation(-0.27F))
                .uv(4, 12).cuboid(-0.3F, -0.2F, -1.3F, 1.0F, 1.0F, 1.0F, new Dilation(-0.27F))
                .uv(4, 12).cuboid(-0.6F, -0.2F, -0.8F, 1.0F, 1.0F, 1.0F, new Dilation(-0.27F))
                .uv(4, 12).cuboid(-0.3F, -0.2F, -0.3F, 1.0F, 1.0F, 1.0F, new Dilation(-0.27F)), ModelTransform.of(-3.0F, -18.4171F, -11.9198F, 0.6109F, 0.0F, 0.0F));

        ModelPartData cube_r39 = bone5.addChild("cube_r39", ModelPartBuilder.create().uv(12, 12).cuboid(-3.5F, 0.1F, -6.3F, 1.0F, 1.0F, 2.0F, new Dilation(-0.3F))
                .uv(12, 12).cuboid(4.75F, 0.1F, -6.3F, 1.0F, 1.0F, 2.0F, new Dilation(-0.3F))
                .uv(12, 12).cuboid(5.75F, 0.1F, -6.3F, 1.0F, 1.0F, 2.0F, new Dilation(-0.3F))
                .uv(12, 12).cuboid(8.75F, 0.1F, -6.3F, 1.0F, 1.0F, 2.0F, new Dilation(-0.3F)), ModelTransform.of(-3.0F, -20.051F, -9.9719F, 0.6109F, 0.0F, 0.0F));

        ModelPartData cube_r40 = bone5.addChild("cube_r40", ModelPartBuilder.create().uv(12, 12).cuboid(4.0F, -0.5F, -1.0F, 1.0F, 1.0F, 2.0F, new Dilation(-0.3F))
                .uv(12, 12).cuboid(-0.5F, -0.5F, -1.0F, 1.0F, 1.0F, 2.0F, new Dilation(-0.3F)), ModelTransform.of(2.75F, -16.5196F, -13.9692F, 2.5307F, 0.0F, 3.1416F));

        ModelPartData cube_r41 = bone5.addChild("cube_r41", ModelPartBuilder.create().uv(12, 12).cuboid(4.3F, 0.1F, -4.0F, 1.0F, 1.0F, 2.0F, new Dilation(-0.3F)), ModelTransform.of(-3.0F, -19.851F, -9.8719F, 0.6109F, 0.0F, 0.0F));

        ModelPartData cube_r42 = bone5.addChild("cube_r42", ModelPartBuilder.create().uv(0, 23).cuboid(-1.5F, -0.5F, -1.5F, 3.0F, 1.0F, 3.0F, new Dilation(0.01F)), ModelTransform.of(-5.3F, -15.7F, -16.35F, 0.0873F, 0.0F, 0.0F));

        ModelPartData telepathic_circuits = bone5.addChild("telepathic_circuits", ModelPartBuilder.create(), ModelTransform.pivot(5.6236F, -16.8831F, -15.5F));

        ModelPartData cube_r43 = telepathic_circuits.addChild("cube_r43", ModelPartBuilder.create().uv(12, 25).cuboid(-0.9F, -1.4F, -0.5F, 2.0F, 1.0F, 1.0F, new Dilation(-0.2F))
                .uv(0, 27).cuboid(-0.9F, -0.8F, -0.5F, 2.0F, 1.0F, 1.0F, new Dilation(-0.2F))
                .uv(12, 23).cuboid(-0.9F, -0.2F, -0.5F, 2.0F, 1.0F, 1.0F, new Dilation(-0.2F))
                .uv(12, 25).cuboid(-0.9F, -1.4F, -2.5F, 2.0F, 1.0F, 1.0F, new Dilation(-0.2F))
                .uv(0, 27).cuboid(-0.9F, -0.8F, -2.5F, 2.0F, 1.0F, 1.0F, new Dilation(-0.2F))
                .uv(12, 23).cuboid(-0.9F, -0.2F, -2.5F, 2.0F, 1.0F, 1.0F, new Dilation(-0.2F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 2.138F));

        ModelPartData cube_r44 = telepathic_circuits.addChild("cube_r44", ModelPartBuilder.create().uv(0, 27).cuboid(-0.9F, -0.2F, -0.5F, 2.0F, 1.0F, 1.0F, new Dilation(-0.2F))
                .uv(0, 27).cuboid(-0.9F, -0.2F, -2.5F, 2.0F, 1.0F, 1.0F, new Dilation(-0.2F)), ModelTransform.of(-0.3111F, -0.2366F, 0.0F, 0.0F, 0.0F, 1.8326F));

        ModelPartData cube_r45 = telepathic_circuits.addChild("cube_r45", ModelPartBuilder.create().uv(12, 23).cuboid(-0.9F, -0.2F, -0.5F, 2.0F, 1.0F, 1.0F, new Dilation(-0.2F))
                .uv(12, 23).cuboid(-0.9F, -0.2F, -2.5F, 2.0F, 1.0F, 1.0F, new Dilation(-0.2F)), ModelTransform.of(-0.831F, -0.3787F, 0.0F, 0.0F, 0.0F, 1.7453F));

        ModelPartData cube_r46 = telepathic_circuits.addChild("cube_r46", ModelPartBuilder.create().uv(0, 27).cuboid(-0.9F, -0.2F, -0.5F, 2.0F, 1.0F, 1.0F, new Dilation(-0.2F))
                .uv(0, 27).cuboid(-0.9F, -0.2F, -2.5F, 2.0F, 1.0F, 1.0F, new Dilation(-0.2F)), ModelTransform.of(-1.1192F, -0.4961F, 0.0F, 0.0F, 0.0F, 1.309F));

        ModelPartData cube_r47 = telepathic_circuits.addChild("cube_r47", ModelPartBuilder.create().uv(12, 25).mirrored().cuboid(-0.8978F, -1.9689F, -0.5F, 2.0F, 1.0F, 1.0F, new Dilation(-0.2F)).mirrored(false), ModelTransform.of(-1.1975F, -0.0822F, -1.0F, 0.0294F, 0.0188F, -2.1378F));

        ModelPartData cube_r48 = telepathic_circuits.addChild("cube_r48", ModelPartBuilder.create().uv(12, 23).cuboid(-0.9F, -0.2F, -0.5F, 2.0F, 1.0F, 1.0F, new Dilation(-0.2F))
                .uv(12, 23).cuboid(-0.9F, -0.2F, -2.5F, 2.0F, 1.0F, 1.0F, new Dilation(-0.2F)), ModelTransform.of(-1.5841F, -0.3825F, 0.0F, 0.0F, 0.0F, 1.1345F));

        ModelPartData cube_r49 = telepathic_circuits.addChild("cube_r49", ModelPartBuilder.create().uv(0, 27).cuboid(-0.9F, -0.2F, -0.5F, 2.0F, 1.0F, 1.0F, new Dilation(-0.2F))
                .uv(0, 27).cuboid(-0.9F, -0.2F, -2.5F, 2.0F, 1.0F, 1.0F, new Dilation(-0.2F)), ModelTransform.of(-1.9976F, -0.208F, 0.0F, 0.0F, 0.0F, 0.9163F));

        ModelPartData cube_r50 = telepathic_circuits.addChild("cube_r50", ModelPartBuilder.create().uv(12, 23).cuboid(-0.9F, -0.2F, -0.5F, 2.0F, 1.0F, 1.0F, new Dilation(-0.2F))
                .uv(12, 23).cuboid(-0.9F, -0.2F, -2.5F, 2.0F, 1.0F, 1.0F, new Dilation(-0.2F)), ModelTransform.of(-2.2236F, -0.2169F, 0.0F, 0.0F, 0.0F, 0.2618F));

        ModelPartData cube_r51 = telepathic_circuits.addChild("cube_r51", ModelPartBuilder.create().uv(12, 23).mirrored().cuboid(-0.5978F, -1.3689F, -0.5F, 2.0F, 1.0F, 1.0F, new Dilation(-0.2F)).mirrored(false)
                .uv(0, 27).mirrored().cuboid(-0.5978F, -0.769F, -0.5F, 2.0F, 1.0F, 1.0F, new Dilation(-0.2F)).mirrored(false), ModelTransform.of(-1.1975F, 0.2178F, -1.0F, 0.0294F, 0.0188F, -2.1378F));

        ModelPartData cube_r52 = telepathic_circuits.addChild("cube_r52", ModelPartBuilder.create().uv(12, 23).mirrored().cuboid(-0.6442F, -0.5319F, -0.5F, 2.0F, 1.0F, 1.0F, new Dilation(-0.2F)).mirrored(false), ModelTransform.of(-1.1975F, 0.2178F, -1.0F, 0.0337F, 0.009F, -1.8324F));

        ModelPartData cube_r53 = telepathic_circuits.addChild("cube_r53", ModelPartBuilder.create().uv(0, 27).mirrored().cuboid(-0.6252F, -0.0336F, -0.5F, 2.0F, 1.0F, 1.0F, new Dilation(-0.2F)).mirrored(false), ModelTransform.of(-1.1975F, 0.2178F, -1.0F, 0.0344F, 0.0061F, -1.7452F));

        ModelPartData cube_r54 = telepathic_circuits.addChild("cube_r54", ModelPartBuilder.create().uv(12, 23).mirrored().cuboid(-0.4114F, -0.0019F, -0.5F, 2.0F, 1.0F, 1.0F, new Dilation(-0.2F)).mirrored(false), ModelTransform.of(-1.1975F, 0.2178F, -1.0F, 0.0337F, -0.009F, -1.3091F));

        ModelPartData cube_r55 = telepathic_circuits.addChild("cube_r55", ModelPartBuilder.create().uv(0, 27).mirrored().cuboid(-0.2939F, 0.3448F, -0.5F, 2.0F, 1.0F, 1.0F, new Dilation(-0.2F)).mirrored(false), ModelTransform.of(-1.1975F, 0.2178F, -1.0F, 0.0316F, -0.0147F, -1.1347F));

        ModelPartData cube_r56 = telepathic_circuits.addChild("cube_r56", ModelPartBuilder.create().uv(12, 23).mirrored().cuboid(-0.0818F, 0.5917F, -0.5F, 2.0F, 1.0F, 1.0F, new Dilation(-0.2F)).mirrored(false), ModelTransform.of(-1.1975F, 0.2178F, -1.0F, 0.0277F, -0.0212F, -0.9166F));

        ModelPartData cube_r57 = telepathic_circuits.addChild("cube_r57", ModelPartBuilder.create().uv(12, 23).mirrored().cuboid(0.4103F, -0.1419F, -0.5F, 2.0F, 1.0F, 1.0F, new Dilation(-0.2F)).mirrored(false), ModelTransform.of(-1.1975F, 0.2178F, -1.0F, 0.009F, -0.0337F, -0.262F));

        ModelPartData randomiser = bone5.addChild("randomiser", ModelPartBuilder.create(), ModelTransform.pivot(-5.3F, -15.7F, -16.35F));

        ModelPartData cube_r58 = randomiser.addChild("cube_r58", ModelPartBuilder.create().uv(0, 33).cuboid(-1.5F, -0.6F, -1.5F, 3.0F, 0.0F, 3.0F, new Dilation(0.0F))
                .uv(0, 30).cuboid(-1.5F, -0.7F, -1.5F, 3.0F, 0.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0873F, 0.0F, 0.0F));

        ModelPartData doors = bone5.addChild("doors", ModelPartBuilder.create(), ModelTransform.pivot(-3.0F, -19.851F, -9.8719F));

        ModelPartData cube_r59 = doors.addChild("cube_r59", ModelPartBuilder.create().uv(12, 12).cuboid(-0.5F, 0.1F, -1.0F, 1.0F, 1.0F, 2.0F, new Dilation(-0.2F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.6109F, 0.0F, 0.0F));

        ModelPartData autopilot = bone5.addChild("autopilot", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, -17.6207F, -11.9425F));

        ModelPartData cube_r60 = autopilot.addChild("cube_r60", ModelPartBuilder.create().uv(8, 16).cuboid(-1.5F, -0.5F, -0.5F, 3.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 1.3526F, 0.0F, 0.0F));

        ModelPartData dimension = bone5.addChild("dimension", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, -19.3845F, -9.503F));

        ModelPartData cube_r61 = dimension.addChild("cube_r61", ModelPartBuilder.create().uv(8, 16).cuboid(-1.5F, -0.5F, -0.5F, 3.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 1.3526F, 0.0F, 0.0F));

        ModelPartData y = bone5.addChild("y", ModelPartBuilder.create(), ModelTransform.pivot(0.25F, -16.2298F, -13.7689F));

        ModelPartData cube_r62 = y.addChild("cube_r62", ModelPartBuilder.create().uv(8, 16).cuboid(-1.5F, -0.5F, -0.5F, 3.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 1.3526F, 0.0F, 0.0F));

        ModelPartData z = bone5.addChild("z", ModelPartBuilder.create(), ModelTransform.pivot(4.75F, -16.2298F, -13.7689F));

        ModelPartData cube_r63 = z.addChild("cube_r63", ModelPartBuilder.create().uv(8, 16).cuboid(-1.0F, -0.5F, -0.5F, 2.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 1.3526F, 0.0F, 0.0F));

        ModelPartData x = bone5.addChild("x", ModelPartBuilder.create(), ModelTransform.pivot(-4.0F, -16.2298F, -13.7689F));

        ModelPartData cube_r64 = x.addChild("cube_r64", ModelPartBuilder.create().uv(8, 16).cuboid(-1.5F, -0.5F, -0.5F, 3.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 1.3526F, 0.0F, 0.0F));

        ModelPartData alarms = bone5.addChild("alarms", ModelPartBuilder.create(), ModelTransform.pivot(4.8F, -17.4831F, -12.7307F));

        ModelPartData cube_r65 = alarms.addChild("cube_r65", ModelPartBuilder.create().uv(8, 12).cuboid(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.2F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.7805F, -0.6178F, -0.5208F));

        ModelPartData fuel_gauge = bone5.addChild("fuel_gauge", ModelPartBuilder.create(), ModelTransform.pivot(-0.25F, -21.9702F, -6.2509F));

        ModelPartData cube_r66 = fuel_gauge.addChild("cube_r66", ModelPartBuilder.create().uv(10, 10).cuboid(-0.5F, 0.0F, -1.0F, 1.0F, 0.0F, 2.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.6109F, 0.0F, 0.0F));

        ModelPartData throttle = bone5.addChild("throttle", ModelPartBuilder.create(), ModelTransform.pivot(3.25F, -16.4923F, -8.7409F));

        ModelPartData cube_r67 = throttle.addChild("cube_r67", ModelPartBuilder.create().uv(32, 59).cuboid(-0.5F, -5.5F, -0.5F, 1.0F, 2.0F, 1.0F, new Dilation(-0.25F))
                .uv(32, 55).cuboid(-0.5F, -5.0F, -0.5F, 1.0F, 3.0F, 1.0F, new Dilation(-0.3F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.6109F, 0.0F, 0.0F));

        ModelPartData bone6 = bone5.addChild("bone6", ModelPartBuilder.create(), ModelTransform.of(0.0F, -16.0F, 0.0F, 0.0F, 0.5236F, 0.0F));

        ModelPartData donottexture_r3 = bone6.addChild("donottexture_r3", ModelPartBuilder.create().uv(42, 57).cuboid(-1.5F, -1.0F, 12.0F, 2.0F, 5.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.5F, 14.125F, -13.5F, 1.5708F, 0.0F, 0.0F));

        ModelPartData donottexture_r4 = bone6.addChild("donottexture_r4", ModelPartBuilder.create().uv(28, 50).cuboid(-0.5F, 0.0F, 0.0F, 1.0F, 3.0F, 12.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 10.0F, -5.5F, 2.3998F, 0.0F, 0.0F));

        ModelPartData cube_r68 = bone6.addChild("cube_r68", ModelPartBuilder.create().uv(64, 74).cuboid(-0.5F, 0.0F, 0.0F, 1.0F, 1.0F, 9.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 13.0F, -5.5F, 1.5708F, 0.0F, 0.0F));

        ModelPartData cube_r69 = bone6.addChild("cube_r69", ModelPartBuilder.create().uv(0, 50).cuboid(-0.5F, 0.0F, 0.0F, 1.0F, 1.0F, 13.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, -16.75F, 0.5498F, 0.0F, 0.0F));

        ModelPartData bone11 = hudolin.addChild("bone11", ModelPartBuilder.create().uv(0, 0).cuboid(-10.5F, -16.0F, -18.25F, 21.0F, 1.0F, 18.0F, new Dilation(0.085F))
                .uv(0, 19).cuboid(-10.0F, -15.0F, -17.35F, 20.0F, 1.0F, 18.0F, new Dilation(0.041F))
                .uv(78, 0).cuboid(-3.0F, -22.925F, -5.25F, 6.0F, 1.0F, 5.0F, new Dilation(0.0725F))
                .uv(64, 66).cuboid(-3.5F, -57.0F, -6.0F, 7.0F, 3.0F, 5.0F, new Dilation(-0.081F))
                .uv(0, 72).cuboid(-3.5F, -52.9F, -6.0F, 7.0F, 2.0F, 5.0F, new Dilation(-0.086F))
                .uv(58, 38).cuboid(-3.5F, -64.0F, -6.0F, 7.0F, 5.0F, 6.0F, new Dilation(-0.085F))
                .uv(64, 58).cuboid(-4.0F, -59.95F, -6.95F, 8.0F, 3.0F, 5.0F, new Dilation(0.026F))
                .uv(64, 58).cuboid(-4.0F, -61.0F, -6.95F, 8.0F, 0.0F, 5.0F, new Dilation(0.001F))
                .uv(64, 58).cuboid(-4.0F, -62.0F, -6.95F, 8.0F, 0.0F, 5.0F, new Dilation(0.001F))
                .uv(64, 58).cuboid(-4.0F, -63.0F, -6.95F, 8.0F, 0.0F, 5.0F, new Dilation(0.001F))
                .uv(26, 65).cuboid(-3.0F, -24.75F, -5.0F, 6.0F, 2.0F, 6.0F, new Dilation(-0.27F))
                .uv(49, 92).cuboid(-3.5F, -51.2F, -5.9F, 7.0F, 2.0F, 7.0F, new Dilation(-0.225F))
                .uv(24, 73).cuboid(-3.5F, -54.5F, -5.85F, 7.0F, 2.0F, 5.0F, new Dilation(-0.29F))
                .uv(76, 19).cuboid(-3.0F, -14.0F, -5.25F, 6.0F, 2.0F, 5.0F, new Dilation(0.0F))
                .uv(76, 26).cuboid(-3.0F, -3.0F, -5.25F, 6.0F, 2.0F, 5.0F, new Dilation(0.075F))
                .uv(0, 79).cuboid(-2.5F, -6.0F, -4.75F, 5.0F, 3.0F, 4.0F, new Dilation(0.0F))
                .uv(54, 50).cuboid(-3.5F, -1.0F, -6.05F, 7.0F, 1.0F, 7.0F, new Dilation(-0.019F))
                .uv(88, 10).cuboid(-2.5F, -12.0F, -4.325F, 5.0F, 6.0F, 0.0F, new Dilation(0.0F))
                .uv(54, 58).cuboid(-2.5F, -49.4F, -4.325F, 5.0F, 25.0F, 0.0F, new Dilation(-0.001F))
                .uv(0, 81).cuboid(-1.5F, -5.0F, -5.0F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(0, 81).cuboid(0.5F, -5.0F, -5.0F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(0, 81).cuboid(1.25F, -24.25F, -5.0F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(0, 81).cuboid(-0.5F, -24.25F, -5.0F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(0, 81).cuboid(-2.25F, -24.25F, -5.0F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(0, 81).cuboid(-2.75F, -50.75F, -5.9F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(0, 81).cuboid(-0.5F, -50.75F, -5.9F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(0, 81).cuboid(1.75F, -50.75F, -5.9F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(0, 81).cuboid(-2.75F, -56.0F, -6.25F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(0, 81).cuboid(-0.5F, -56.0F, -6.25F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(0, 81).cuboid(1.75F, -56.0F, -6.25F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -2.0944F, 0.0F));

        ModelPartData cube_r70 = bone11.addChild("cube_r70", ModelPartBuilder.create().uv(14, 104).cuboid(-8.5F, -0.025F, 0.0F, 17.0F, 0.0F, 12.0F, new Dilation(0.001F))
                .uv(0, 13).cuboid(-4.3F, -0.2F, 4.1F, 1.0F, 0.0F, 1.0F, new Dilation(0.001F))
                .uv(0, 13).cuboid(-4.3F, -0.2F, 2.1F, 1.0F, 0.0F, 1.0F, new Dilation(0.001F))
                .uv(0, 13).cuboid(-5.75F, -0.2F, 2.1F, 1.0F, 0.0F, 1.0F, new Dilation(0.001F))
                .uv(0, 13).cuboid(-5.75F, -0.2F, 4.0F, 1.0F, 0.0F, 1.0F, new Dilation(0.001F))
                .uv(100, 92).cuboid(-8.25F, -0.1F, -0.3F, 17.0F, 0.0F, 12.0F, new Dilation(0.001F))
                .uv(0, 44).cuboid(2.5F, -0.4F, 1.0F, 3.0F, 1.0F, 3.0F, new Dilation(0.001F))
                .uv(100, 79).cuboid(-8.5F, 0.0F, 0.0F, 17.0F, 0.0F, 12.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, -16.0F, -14.5F, 0.6109F, 0.0F, 0.0F));

        ModelPartData cube_r71 = bone11.addChild("cube_r71", ModelPartBuilder.create().uv(0, 42).mirrored().cuboid(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.001F)).mirrored(false), ModelTransform.of(0.0F, -21.2852F, -6.6033F, 0.7805F, 0.6178F, 0.5208F));

        ModelPartData cube_r72 = bone11.addChild("cube_r72", ModelPartBuilder.create().uv(0, 42).mirrored().cuboid(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.001F)).mirrored(false), ModelTransform.of(2.0F, -21.2852F, -6.6033F, 0.7805F, 0.6178F, 0.5208F));

        ModelPartData cube_r73 = bone11.addChild("cube_r73", ModelPartBuilder.create().uv(0, 42).cuboid(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.001F)), ModelTransform.of(-2.0F, -21.2852F, -6.6033F, 0.7805F, -0.6178F, -0.5208F));

        ModelPartData cube_r74 = bone11.addChild("cube_r74", ModelPartBuilder.create().uv(128, 91).cuboid(-0.5F, -0.5F, 0.0F, 1.0F, 1.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, -18.9907F, -11.1006F, 0.6104F, -0.025F, 0.0357F));

        ModelPartData cube_r75 = bone11.addChild("cube_r75", ModelPartBuilder.create().uv(128, 91).mirrored().cuboid(-0.5F, -0.5F, 0.0F, 1.0F, 1.0F, 0.0F, new Dilation(0.001F)).mirrored(false), ModelTransform.of(0.0F, -17.2699F, -13.5581F, 0.6104F, 0.025F, -0.0357F));

        ModelPartData cube_r76 = bone11.addChild("cube_r76", ModelPartBuilder.create().uv(128, 91).cuboid(-0.5F, -0.5F, 0.0F, 1.0F, 1.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, -17.8435F, -12.7389F, 0.6104F, -0.025F, 0.0357F));

        ModelPartData cube_r77 = bone11.addChild("cube_r77", ModelPartBuilder.create().uv(128, 91).mirrored().cuboid(-0.5F, -0.5F, 0.0F, 1.0F, 1.0F, 0.0F, new Dilation(0.001F)).mirrored(false), ModelTransform.of(0.0F, -18.4171F, -11.9198F, 0.6104F, 0.025F, -0.0357F));

        ModelPartData handbrake = bone11.addChild("handbrake", ModelPartBuilder.create(), ModelTransform.pivot(4.0F, -17.6797F, -12.6242F));

        ModelPartData cube_r78 = handbrake.addChild("cube_r78", ModelPartBuilder.create().uv(0, 36).cuboid(-0.5F, -0.5F, -0.5F, 3.0F, 0.0F, 1.0F, new Dilation(0.002F))
                .uv(0, 40).cuboid(1.5F, -1.4F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.1F))
                .uv(3, 46).cuboid(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.6109F, 0.0F, 0.0F));

        ModelPartData power = bone11.addChild("power", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, -19.5643F, -10.2814F));

        ModelPartData cube_r79 = power.addChild("cube_r79", ModelPartBuilder.create().uv(128, 91).mirrored().cuboid(-0.5F, -0.5F, 0.0F, 1.0F, 1.0F, 0.0F, new Dilation(0.001F)).mirrored(false), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.6104F, 0.025F, -0.0357F));

        ModelPartData cube_r80 = power.addChild("cube_r80", ModelPartBuilder.create().uv(128, 91).cuboid(-0.5F, -0.5F, 0.0F, 1.0F, 1.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, -0.5736F, 0.8191F, 0.6104F, -0.025F, 0.0357F));

        ModelPartData cube_r81 = power.addChild("cube_r81", ModelPartBuilder.create().uv(128, 91).mirrored().cuboid(-0.5F, -0.5F, 0.0F, 1.0F, 1.0F, 0.0F, new Dilation(0.001F)).mirrored(false), ModelTransform.of(0.0F, -1.1471F, 1.6383F, 0.6104F, 0.025F, -0.0357F));

        ModelPartData siege = bone11.addChild("siege", ModelPartBuilder.create(), ModelTransform.pivot(-5.0F, -17.9584F, -11.3544F));

        ModelPartData cube_r82 = siege.addChild("cube_r82", ModelPartBuilder.create().uv(0, 40).cuboid(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.7805F, -0.6178F, -0.5208F));

        ModelPartData cube_r83 = siege.addChild("cube_r83", ModelPartBuilder.create().uv(0, 40).cuboid(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 1.0898F, -1.5564F, 0.7805F, -0.6178F, -0.5208F));

        ModelPartData cube_r84 = siege.addChild("cube_r84", ModelPartBuilder.create().uv(0, 40).cuboid(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.001F)), ModelTransform.of(1.5F, 1.0898F, -1.5564F, 0.7596F, 0.5865F, 0.4839F));

        ModelPartData cube_r85 = siege.addChild("cube_r85", ModelPartBuilder.create().uv(0, 40).cuboid(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.001F)), ModelTransform.of(1.5F, 0.0F, 0.0F, 0.7805F, 0.6178F, 0.5208F));

        ModelPartData bone12 = bone11.addChild("bone12", ModelPartBuilder.create(), ModelTransform.of(0.0F, -16.0F, 0.0F, 0.0F, 0.5236F, 0.0F));

        ModelPartData cube_r86 = bone12.addChild("cube_r86", ModelPartBuilder.create().uv(87, 66).cuboid(-0.5F, -2.0F, -3.0F, 2.0F, 2.0F, 3.0F, new Dilation(0.001F)), ModelTransform.of(-0.5F, 15.2426F, -8.3284F, 0.2618F, 0.0F, 0.0F));

        ModelPartData cube_r87 = bone12.addChild("cube_r87", ModelPartBuilder.create().uv(18, 80).cuboid(-0.5F, 0.0F, -5.0F, 1.0F, 1.0F, 6.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 11.0F, -5.5F, 0.7854F, 0.0F, 0.0F));

        ModelPartData cube_r88 = bone12.addChild("cube_r88", ModelPartBuilder.create().uv(76, 77).cuboid(-0.5F, 0.0F, -1.0F, 1.0F, 1.0F, 3.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 12.0F, -5.5F, 1.5708F, 0.0F, 0.0F));

        ModelPartData cube_r89 = bone12.addChild("cube_r89", ModelPartBuilder.create().uv(0, 50).cuboid(-0.5F, 0.0F, 0.0F, 1.0F, 1.0F, 13.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, -16.75F, 0.5498F, 0.0F, 0.0F));

        ModelPartData bone7 = hudolin.addChild("bone7", ModelPartBuilder.create().uv(0, 0).cuboid(-10.5F, -16.0F, -18.25F, 21.0F, 1.0F, 18.0F, new Dilation(0.085F))
                .uv(0, 19).cuboid(-10.0F, -15.0F, -17.35F, 20.0F, 1.0F, 18.0F, new Dilation(0.041F))
                .uv(78, 0).cuboid(-3.0F, -22.925F, -5.25F, 6.0F, 1.0F, 5.0F, new Dilation(0.0725F))
                .uv(64, 66).cuboid(-3.5F, -57.0F, -6.0F, 7.0F, 3.0F, 5.0F, new Dilation(-0.085F))
                .uv(0, 72).cuboid(-3.5F, -52.9F, -6.0F, 7.0F, 2.0F, 5.0F, new Dilation(-0.086F))
                .uv(58, 38).cuboid(-3.5F, -64.0F, -6.0F, 7.0F, 5.0F, 6.0F, new Dilation(-0.085F))
                .uv(64, 58).cuboid(-4.0F, -59.95F, -6.95F, 8.0F, 3.0F, 5.0F, new Dilation(0.026F))
                .uv(64, 58).cuboid(-4.0F, -61.0F, -6.95F, 8.0F, 0.0F, 5.0F, new Dilation(0.001F))
                .uv(64, 58).cuboid(-4.0F, -62.0F, -6.95F, 8.0F, 0.0F, 5.0F, new Dilation(0.001F))
                .uv(64, 58).cuboid(-4.0F, -63.0F, -6.95F, 8.0F, 0.0F, 5.0F, new Dilation(0.001F))
                .uv(26, 65).cuboid(-3.0F, -24.75F, -5.0F, 6.0F, 2.0F, 6.0F, new Dilation(-0.27F))
                .uv(49, 92).cuboid(-3.5F, -51.2F, -5.9F, 7.0F, 2.0F, 7.0F, new Dilation(-0.225F))
                .uv(24, 73).cuboid(-3.5F, -54.5F, -5.85F, 7.0F, 2.0F, 5.0F, new Dilation(-0.29F))
                .uv(76, 19).cuboid(-3.0F, -14.0F, -5.25F, 6.0F, 2.0F, 5.0F, new Dilation(0.0F))
                .uv(76, 26).cuboid(-3.0F, -3.0F, -5.25F, 6.0F, 2.0F, 5.0F, new Dilation(0.075F))
                .uv(0, 79).cuboid(-2.5F, -6.0F, -4.75F, 5.0F, 3.0F, 4.0F, new Dilation(0.0F))
                .uv(54, 50).cuboid(-3.5F, -1.0F, -6.05F, 7.0F, 1.0F, 7.0F, new Dilation(-0.019F))
                .uv(88, 10).cuboid(-2.5F, -12.0F, -4.325F, 5.0F, 6.0F, 0.0F, new Dilation(0.0F))
                .uv(54, 58).cuboid(-2.5F, -49.4F, -4.325F, 5.0F, 25.0F, 0.0F, new Dilation(-0.001F))
                .uv(0, 81).cuboid(-1.5F, -5.0F, -5.0F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(0, 81).cuboid(0.5F, -5.0F, -5.0F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(0, 81).cuboid(1.25F, -24.25F, -5.0F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(0, 81).cuboid(-0.5F, -24.25F, -5.0F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(0, 81).cuboid(-2.25F, -24.25F, -5.0F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(0, 81).cuboid(-2.75F, -50.75F, -5.9F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(0, 81).cuboid(-0.5F, -50.75F, -5.9F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(0, 81).cuboid(1.75F, -50.75F, -5.9F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(0, 81).cuboid(-2.75F, -56.0F, -6.25F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(0, 81).cuboid(-0.5F, -56.0F, -6.25F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(0, 81).cuboid(1.75F, -56.0F, -6.25F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(1, 119).cuboid(2.5F, -16.5F, -16.5F, 5.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(2, 121).cuboid(2.0F, -17.5F, -16.0F, 6.0F, 1.0F, 0.0F, new Dilation(0.0F))
                .uv(1, 122).cuboid(2.0F, -17.0F, -16.5F, 6.0F, 0.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 2.0944F, 0.0F));

        ModelPartData cube_r90 = bone7.addChild("cube_r90", ModelPartBuilder.create().uv(1, 122).cuboid(-3.0F, 0.5F, -0.5F, 6.0F, 0.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(5.0F, -17.5F, -16.0F, -1.5708F, 0.0F, 0.0F));

        ModelPartData cube_r91 = bone7.addChild("cube_r91", ModelPartBuilder.create().uv(1, 122).cuboid(-3.0F, 0.5F, -0.5F, 6.0F, 0.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(5.0F, -17.5F, -16.0F, 1.5708F, 0.0F, 0.0F));

        ModelPartData cube_r92 = bone7.addChild("cube_r92", ModelPartBuilder.create().uv(10, 1).cuboid(2.0F, -0.825F, 3.0F, 1.0F, 1.0F, 0.0F, new Dilation(0.001F))
                .uv(48, 104).cuboid(-8.5F, -0.025F, 0.0F, 17.0F, 0.0F, 12.0F, new Dilation(0.001F))
                .uv(0, 48).mirrored().cuboid(1.25F, -0.4F, 6.7F, 2.0F, 1.0F, 2.0F, new Dilation(0.2F)).mirrored(false)
                .uv(0, 48).cuboid(-3.25F, -0.4F, 6.7F, 2.0F, 1.0F, 2.0F, new Dilation(0.2F))
                .uv(46, 128).cuboid(-7.65F, -0.1F, 0.0F, 17.0F, 0.0F, 12.0F, new Dilation(0.001F))
                .uv(0, 38).cuboid(-8.5F, 0.0F, 0.0F, 17.0F, 0.0F, 12.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, -16.0F, -14.5F, 0.6109F, 0.0F, 0.0F));

        ModelPartData sonic_port = bone7.addChild("sonic_port", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, -16.0F, -14.5F));

        ModelPartData cube_r93 = sonic_port.addChild("cube_r93", ModelPartBuilder.create().uv(4, 42).cuboid(-0.5F, -0.3F, 7.0F, 1.0F, 1.0F, 1.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.6109F, 0.0F, 0.0F));

        ModelPartData bone8 = bone7.addChild("bone8", ModelPartBuilder.create(), ModelTransform.of(0.0F, -16.0F, 0.0F, 0.0F, 0.5236F, 0.0F));

        ModelPartData cube_r94 = bone8.addChild("cube_r94", ModelPartBuilder.create().uv(88, 66).cuboid(-0.5F, -2.0F, -3.0F, 2.0F, 2.0F, 3.0F, new Dilation(0.001F)), ModelTransform.of(-0.5F, 15.2426F, -8.3284F, 0.2618F, 0.0F, 0.0F));

        ModelPartData cube_r95 = bone8.addChild("cube_r95", ModelPartBuilder.create().uv(18, 80).cuboid(-0.5F, 0.0F, -5.0F, 1.0F, 1.0F, 6.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 11.0F, -5.5F, 0.7854F, 0.0F, 0.0F));

        ModelPartData cube_r96 = bone8.addChild("cube_r96", ModelPartBuilder.create().uv(70, 80).cuboid(-0.5F, 0.0F, 0.0F, 1.0F, 1.0F, 3.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 13.0F, -5.5F, 1.5708F, 0.0F, 0.0F));

        ModelPartData cube_r97 = bone8.addChild("cube_r97", ModelPartBuilder.create().uv(0, 50).cuboid(-0.5F, 0.0F, 0.0F, 1.0F, 1.0F, 13.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, -16.75F, 0.5498F, 0.0F, 0.0F));

        ModelPartData door_lock = bone7.addChild("door_lock", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, -16.0F, -14.5F));

        ModelPartData cube_r98 = door_lock.addChild("cube_r98", ModelPartBuilder.create().uv(10, 1).cuboid(0.5F, -0.825F, 4.5F, 1.0F, 1.0F, 0.0F, new Dilation(0.001F))
                .uv(10, 1).cuboid(-1.0F, -0.825F, 4.5F, 1.0F, 1.0F, 0.0F, new Dilation(0.001F))
                .uv(10, 1).cuboid(-2.5F, -0.825F, 4.5F, 1.0F, 1.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.6109F, 0.0F, 0.0F));

        ModelPartData shields = bone7.addChild("shields", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, -16.0F, -14.5F));

        ModelPartData cube_r99 = shields.addChild("cube_r99", ModelPartBuilder.create().uv(10, 1).cuboid(-1.0F, -0.825F, 2.0F, 1.0F, 1.0F, 0.0F, new Dilation(0.001F))
                .uv(10, 1).cuboid(-2.5F, -0.825F, 2.0F, 1.0F, 1.0F, 0.0F, new Dilation(0.001F))
                .uv(10, 1).cuboid(0.5F, -0.825F, 2.0F, 1.0F, 1.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.6109F, 0.0F, 0.0F));

        ModelPartData bone13 = hudolin.addChild("bone13", ModelPartBuilder.create().uv(0, 0).cuboid(-10.5F, -16.0F, -18.25F, 21.0F, 1.0F, 18.0F, new Dilation(0.085F))
                .uv(0, 19).cuboid(-10.0F, -15.0F, -17.35F, 20.0F, 1.0F, 18.0F, new Dilation(0.041F))
                .uv(78, 0).cuboid(-3.0F, -22.925F, -5.25F, 6.0F, 1.0F, 5.0F, new Dilation(0.0725F))
                .uv(64, 66).cuboid(-3.5F, -57.0F, -6.0F, 7.0F, 3.0F, 5.0F, new Dilation(-0.081F))
                .uv(0, 72).cuboid(-3.5F, -52.9F, -6.0F, 7.0F, 2.0F, 5.0F, new Dilation(-0.086F))
                .uv(58, 38).cuboid(-3.5F, -64.0F, -6.0F, 7.0F, 6.0F, 6.0F, new Dilation(-0.085F))
                .uv(64, 58).cuboid(-4.0F, -59.95F, -6.95F, 8.0F, 3.0F, 5.0F, new Dilation(0.026F))
                .uv(64, 58).cuboid(-4.0F, -61.0F, -6.95F, 8.0F, 0.0F, 5.0F, new Dilation(0.0F))
                .uv(64, 58).cuboid(-4.0F, -62.0F, -6.95F, 8.0F, 0.0F, 5.0F, new Dilation(0.0F))
                .uv(64, 58).cuboid(-4.0F, -63.0F, -6.95F, 8.0F, 0.0F, 5.0F, new Dilation(0.0F))
                .uv(26, 65).cuboid(-3.0F, -24.75F, -5.0F, 6.0F, 2.0F, 6.0F, new Dilation(-0.27F))
                .uv(49, 92).cuboid(-3.5F, -51.2F, -5.9F, 7.0F, 2.0F, 7.0F, new Dilation(-0.225F))
                .uv(24, 73).cuboid(-3.5F, -54.5F, -5.85F, 7.0F, 2.0F, 5.0F, new Dilation(-0.29F))
                .uv(76, 19).cuboid(-3.0F, -14.0F, -5.25F, 6.0F, 2.0F, 5.0F, new Dilation(0.0F))
                .uv(76, 26).cuboid(-3.0F, -3.0F, -5.25F, 6.0F, 2.0F, 5.0F, new Dilation(0.075F))
                .uv(0, 79).cuboid(-2.5F, -6.0F, -4.75F, 5.0F, 3.0F, 4.0F, new Dilation(0.0F))
                .uv(54, 50).cuboid(-3.5F, -1.0F, -6.05F, 7.0F, 1.0F, 7.0F, new Dilation(-0.019F))
                .uv(78, 10).cuboid(-2.5F, -12.0F, -4.325F, 5.0F, 6.0F, 0.0F, new Dilation(0.0F))
                .uv(54, 58).cuboid(-2.5F, -49.4F, -4.325F, 5.0F, 25.0F, 0.0F, new Dilation(-0.001F))
                .uv(0, 81).cuboid(-1.5F, -5.0F, -5.0F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(0, 81).cuboid(0.5F, -5.0F, -5.0F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(0, 81).cuboid(1.25F, -24.25F, -5.0F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(0, 81).cuboid(-0.5F, -24.25F, -5.0F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(0, 81).cuboid(-2.25F, -24.25F, -5.0F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(0, 81).cuboid(-2.75F, -50.75F, -5.9F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(0, 81).cuboid(-0.5F, -50.75F, -5.9F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(0, 81).cuboid(1.75F, -50.75F, -5.9F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(0, 81).cuboid(-2.75F, -56.0F, -6.25F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(0, 81).cuboid(-0.5F, -56.0F, -6.25F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(0, 81).cuboid(1.75F, -56.0F, -6.25F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData cube_r100 = bone13.addChild("cube_r100", ModelPartBuilder.create().uv(0, 38).mirrored().cuboid(-0.5F, -0.625F, 10.1F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)).mirrored(false)
                .uv(0, 38).mirrored().cuboid(1.4F, -0.625F, 10.1F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)).mirrored(false)
                .uv(4, 38).cuboid(-2.4F, -0.625F, 10.1F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(98, 58).cuboid(-8.55F, 0.075F, 0.0F, 17.0F, 0.0F, 12.0F, new Dilation(0.001F))
                .uv(82, 116).cuboid(-8.5F, -0.025F, 0.0F, 17.0F, 0.0F, 12.0F, new Dilation(0.001F))
                .uv(4, 40).cuboid(0.25F, -0.2F, 2.5F, 1.0F, 0.0F, 2.0F, new Dilation(0.001F))
                .uv(60, 8).cuboid(-0.5F, -0.1F, 1.0F, 5.0F, 0.0F, 4.0F, new Dilation(0.001F))
                .uv(98, 30).cuboid(-8.5F, 0.0F, 0.0F, 17.0F, 0.0F, 12.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, -16.0F, -14.5F, 0.6109F, 0.0F, 0.0F));

        ModelPartData cube_r101 = bone13.addChild("cube_r101", ModelPartBuilder.create().uv(8, 38).cuboid(2.3F, 0.9F, -3.7F, 1.0F, 1.0F, 1.0F, new Dilation(-0.3F))
                .uv(8, 38).cuboid(2.3F, 0.9F, -4.3F, 1.0F, 1.0F, 1.0F, new Dilation(-0.3F))
                .uv(8, 38).cuboid(2.3F, 0.9F, -4.9F, 1.0F, 1.0F, 1.0F, new Dilation(-0.3F))
                .uv(8, 38).cuboid(2.3F, 0.9F, -5.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.3F))
                .uv(8, 38).cuboid(2.3F, 0.9F, -6.1F, 1.0F, 1.0F, 1.0F, new Dilation(-0.3F))
                .uv(8, 38).cuboid(2.3F, 0.9F, -6.7F, 1.0F, 1.0F, 1.0F, new Dilation(-0.3F))
                .uv(8, 38).cuboid(-3.3F, 0.9F, -6.7F, 1.0F, 1.0F, 1.0F, new Dilation(-0.3F))
                .uv(8, 38).cuboid(-3.3F, 0.9F, -6.1F, 1.0F, 1.0F, 1.0F, new Dilation(-0.3F))
                .uv(8, 38).cuboid(-3.3F, 0.9F, -5.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.3F))
                .uv(8, 38).cuboid(-3.3F, 0.9F, -4.9F, 1.0F, 1.0F, 1.0F, new Dilation(-0.3F))
                .uv(8, 38).cuboid(-3.3F, 0.9F, -4.3F, 1.0F, 1.0F, 1.0F, new Dilation(-0.3F))
                .uv(8, 38).cuboid(-3.3F, 0.9F, -3.7F, 1.0F, 1.0F, 1.0F, new Dilation(-0.3F)), ModelTransform.of(2.0F, -21.5101F, -8.8972F, 0.6109F, 0.0F, 0.0F));

        ModelPartData cube_r102 = bone13.addChild("cube_r102", ModelPartBuilder.create().uv(0, 40).cuboid(-0.5F, -0.8F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.15F)), ModelTransform.of(3.0F, -17.2569F, -11.7278F, 0.7805F, -0.6178F, -0.5208F));

        ModelPartData refueler = bone13.addChild("refueler", ModelPartBuilder.create(), ModelTransform.pivot(1.0F, -17.5569F, -11.9278F));

        ModelPartData cube_r103 = refueler.addChild("cube_r103", ModelPartBuilder.create().uv(0, 40).cuboid(-0.5F, -0.8F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.15F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.7805F, -0.6178F, -0.5208F));

        ModelPartData increment = bone13.addChild("increment", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, -19.0527F, -7.1765F));

        ModelPartData cube_r104 = increment.addChild("cube_r104", ModelPartBuilder.create().uv(32, 59).cuboid(-0.5F, -3.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.2F))
                .uv(32, 55).cuboid(-0.5F, -3.1F, -0.5F, 1.0F, 2.0F, 1.0F, new Dilation(-0.3F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.6109F, 0.0F, 0.0F));

        ModelPartData bone14 = bone13.addChild("bone14", ModelPartBuilder.create(), ModelTransform.of(0.0F, -16.0F, 0.0F, 0.0F, 0.5236F, 0.0F));

        ModelPartData donottexture_r5 = bone14.addChild("donottexture_r5", ModelPartBuilder.create().uv(42, 57).cuboid(-1.5F, -1.0F, 12.0F, 2.0F, 5.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.5F, 14.025F, -13.5F, 1.5708F, 0.0F, 0.0F));

        ModelPartData donottexture_r6 = bone14.addChild("donottexture_r6", ModelPartBuilder.create().uv(28, 50).cuboid(-0.5F, 0.0F, 0.0F, 1.0F, 3.0F, 12.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 10.0F, -5.5F, 2.3998F, 0.0F, 0.0F));

        ModelPartData cube_r105 = bone14.addChild("cube_r105", ModelPartBuilder.create().uv(64, 74).cuboid(-0.5F, 0.0F, 0.0F, 1.0F, 1.0F, 9.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 13.0F, -5.5F, 1.5708F, 0.0F, 0.0F));

        ModelPartData cube_r106 = bone14.addChild("cube_r106", ModelPartBuilder.create().uv(0, 50).cuboid(-0.5F, 0.0F, 0.0F, 1.0F, 1.0F, 13.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, -16.75F, 0.5498F, 0.0F, 0.0F));

        ModelPartData rotation = bone13.addChild("rotation", ModelPartBuilder.create().uv(1, 1).cuboid(-4.25F, -18.25F, -12.75F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData rotor = hudolin.addChild("rotor", ModelPartBuilder.create().uv(84, 40).cuboid(-2.0F, 12.5F, -3.5F, 4.0F, 6.0F, 4.0F, new Dilation(0.05F)), ModelTransform.pivot(0.0F, -24.5F, 0.0F));

        ModelPartData cube_r107 = rotor.addChild("cube_r107", ModelPartBuilder.create().uv(84, 40).cuboid(-2.0F, -11.0F, -3.5F, 4.0F, 6.0F, 4.0F, new Dilation(0.05F)), ModelTransform.of(0.0F, 23.5F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData cube_r108 = rotor.addChild("cube_r108", ModelPartBuilder.create().uv(84, 40).cuboid(-2.0F, -11.0F, -3.5F, 4.0F, 6.0F, 4.0F, new Dilation(0.05F)), ModelTransform.of(0.0F, 23.5F, 0.0F, 3.1416F, -1.0472F, -3.1416F));

        ModelPartData cube_r109 = rotor.addChild("cube_r109", ModelPartBuilder.create().uv(84, 40).cuboid(-2.0F, -11.0F, -3.5F, 4.0F, 6.0F, 4.0F, new Dilation(0.05F)), ModelTransform.of(0.0F, 23.5F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData cube_r110 = rotor.addChild("cube_r110", ModelPartBuilder.create().uv(84, 40).cuboid(-2.0F, -11.0F, -3.5F, 4.0F, 6.0F, 4.0F, new Dilation(0.05F)), ModelTransform.of(0.0F, 23.5F, 0.0F, 3.1416F, 1.0472F, 3.1416F));

        ModelPartData cube_r111 = rotor.addChild("cube_r111", ModelPartBuilder.create().uv(84, 40).cuboid(-2.0F, -11.0F, -3.5F, 4.0F, 6.0F, 4.0F, new Dilation(0.05F)), ModelTransform.of(0.0F, 23.5F, 0.0F, 3.1416F, 0.0F, 3.1416F));

        ModelPartData bone2 = rotor.addChild("bone2", ModelPartBuilder.create().uv(98, 53).cuboid(-2.0F, -5.975F, -3.5F, 4.0F, 1.0F, 4.0F, new Dilation(0.001F)), ModelTransform.pivot(0.0F, -0.5F, 0.0F));

        ModelPartData cube_r112 = bone2.addChild("cube_r112", ModelPartBuilder.create().uv(98, 53).cuboid(-2.0F, -6.0F, -3.5F, 4.0F, 1.0F, 4.0F, new Dilation(0.002F)), ModelTransform.of(0.0F, 0.025F, 0.0F, 3.1416F, 1.0472F, 3.1416F));

        ModelPartData cube_r113 = bone2.addChild("cube_r113", ModelPartBuilder.create().uv(98, 52).cuboid(-2.0F, -6.0F, -3.5F, 4.0F, 1.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.025F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData cube_r114 = bone2.addChild("cube_r114", ModelPartBuilder.create().uv(98, 53).cuboid(-2.0F, -6.0F, -3.5F, 4.0F, 1.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.025F, 0.0F, 3.1416F, 0.0F, 3.1416F));

        ModelPartData cube_r115 = bone2.addChild("cube_r115", ModelPartBuilder.create().uv(98, 53).cuboid(-2.0F, -6.0F, -3.5F, 4.0F, 1.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.025F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData cube_r116 = bone2.addChild("cube_r116", ModelPartBuilder.create().uv(98, 53).cuboid(-2.0F, -6.0F, -3.5F, 4.0F, 1.0F, 4.0F, new Dilation(0.003F)), ModelTransform.of(0.0F, 0.025F, 0.0F, -3.1416F, -1.0472F, 3.1416F));

        ModelPartData cube_r117 = bone2.addChild("cube_r117", ModelPartBuilder.create().uv(8, 86).cuboid(-0.5F, -7.0F, -3.5F, 1.0F, 14.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -4.475F, 0.0F, 3.1416F, 0.5236F, 0.0F));

        ModelPartData cube_r118 = bone2.addChild("cube_r118", ModelPartBuilder.create().uv(8, 86).cuboid(-0.5F, -7.0F, -3.5F, 1.0F, 14.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -4.475F, 0.0F, -3.1416F, -0.5236F, 0.0F));

        ModelPartData cube_r119 = bone2.addChild("cube_r119", ModelPartBuilder.create().uv(8, 86).cuboid(-0.5F, -7.0F, -3.5F, 1.0F, 14.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -4.475F, 0.0F, 0.0F, 1.5708F, 3.1416F));

        ModelPartData cube_r120 = bone2.addChild("cube_r120", ModelPartBuilder.create().uv(8, 86).cuboid(-0.5F, -7.0F, -3.5F, 1.0F, 14.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -4.475F, 0.0F, 0.0F, -1.5708F, -3.1416F));

        ModelPartData cube_r121 = bone2.addChild("cube_r121", ModelPartBuilder.create().uv(8, 86).cuboid(-0.5F, -7.0F, -3.5F, 1.0F, 14.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -4.475F, 0.0F, 0.0F, 0.5236F, -3.1416F));

        ModelPartData cube_r122 = bone2.addChild("cube_r122", ModelPartBuilder.create().uv(8, 86).cuboid(-0.5F, -7.0F, -3.5F, 1.0F, 14.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -4.475F, 0.0F, 0.0F, -0.5236F, -3.1416F));

        ModelPartData cube_r123 = bone2.addChild("cube_r123", ModelPartBuilder.create().uv(0, 86).cuboid(-0.5F, -7.0F, -0.5F, 1.0F, 14.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(2.1651F, -4.475F, 1.25F, 3.1416F, 1.0472F, 0.0F));

        ModelPartData cube_r124 = bone2.addChild("cube_r124", ModelPartBuilder.create().uv(0, 86).cuboid(-0.5F, -7.0F, -0.5F, 1.0F, 14.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -4.475F, 2.5F, 3.1416F, 0.0F, 0.0F));

        ModelPartData cube_r125 = bone2.addChild("cube_r125", ModelPartBuilder.create().uv(0, 86).cuboid(-0.5F, -7.0F, -0.5F, 1.0F, 14.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(2.1651F, -4.475F, -1.25F, 0.0F, 1.0472F, -3.1416F));

        ModelPartData cube_r126 = bone2.addChild("cube_r126", ModelPartBuilder.create().uv(0, 86).cuboid(-0.5F, -7.0F, -0.5F, 1.0F, 14.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-2.1651F, -4.475F, 1.25F, 3.1416F, -1.0472F, 0.0F));

        ModelPartData cube_r127 = bone2.addChild("cube_r127", ModelPartBuilder.create().uv(0, 86).cuboid(-0.5F, -7.0F, -0.5F, 1.0F, 14.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-2.1651F, -4.475F, -1.25F, 0.0F, -1.0472F, -3.1416F));

        ModelPartData cube_r128 = bone2.addChild("cube_r128", ModelPartBuilder.create().uv(0, 86).cuboid(-0.5F, -7.0F, -0.5F, 1.0F, 14.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -4.475F, -2.5F, 0.0F, 0.0F, -3.1416F));

        ModelPartData glass = bone2.addChild("glass", ModelPartBuilder.create().uv(47, 38).cuboid(-2.5F, 0.0F, -4.325F, 5.0F, 13.0F, 0.0F, new Dilation(-0.001F)), ModelTransform.pivot(0.0F, -11.5F, 0.0F));

        ModelPartData glass_r1 = glass.addChild("glass_r1", ModelPartBuilder.create().uv(44, 58).cuboid(-2.5F, 0.3F, 0.0F, 5.0F, 4.0F, 0.0F, new Dilation(0.002F)), ModelTransform.of(0.0223F, 0.003F, 0.0094F, 1.5708F, -1.0472F, 3.1416F));

        ModelPartData glass_r2 = glass.addChild("glass_r2", ModelPartBuilder.create().uv(44, 58).cuboid(-2.5F, 0.299F, 0.0F, 5.0F, 4.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(0.0225F, 0.0F, -0.013F, -1.5708F, -1.0472F, 0.0F));

        ModelPartData glass_r3 = glass.addChild("glass_r3", ModelPartBuilder.create().uv(44, 58).cuboid(-2.5F, 0.3F, 0.0F, 5.0F, 4.0F, 0.0F, new Dilation(0.002F)), ModelTransform.of(-0.0193F, 0.003F, 0.0146F, 1.5708F, 1.0472F, 3.1416F));

        ModelPartData glass_r4 = glass.addChild("glass_r4", ModelPartBuilder.create().uv(44, 58).cuboid(-2.5F, 0.299F, 0.0F, 5.0F, 4.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(0.002F, 0.002F, 0.026F, 1.5708F, 0.0F, 3.1416F));

        ModelPartData glass_r5 = glass.addChild("glass_r5", ModelPartBuilder.create().uv(44, 58).cuboid(-2.5F, 0.299F, 0.0F, 5.0F, 4.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(-0.0225F, 0.0F, -0.013F, -1.5708F, 1.0472F, 0.0F));

        ModelPartData glass_r6 = glass.addChild("glass_r6", ModelPartBuilder.create().uv(44, 58).cuboid(-2.5F, 0.3F, 0.0F, 5.0F, 4.0F, 0.0F, new Dilation(0.002F)), ModelTransform.of(-0.001F, 0.001F, -0.024F, -1.5708F, 0.0F, 0.0F));

        ModelPartData glass_r7 = glass.addChild("glass_r7", ModelPartBuilder.create().uv(47, 38).cuboid(-2.5F, -10.0F, -4.325F, 5.0F, 13.0F, 0.0F, new Dilation(-0.001F)), ModelTransform.of(0.0F, 10.0F, 0.0F, -3.1416F, -1.0472F, 3.1416F));

        ModelPartData glass_r8 = glass.addChild("glass_r8", ModelPartBuilder.create().uv(47, 38).cuboid(-2.5F, -10.0F, -4.325F, 5.0F, 13.0F, 0.0F, new Dilation(-0.001F)), ModelTransform.of(0.0F, 10.0F, 0.0F, -3.1416F, 0.0F, 3.1416F));

        ModelPartData glass_r9 = glass.addChild("glass_r9", ModelPartBuilder.create().uv(47, 38).cuboid(-2.5F, -10.0F, -4.325F, 5.0F, 13.0F, 0.0F, new Dilation(-0.001F)), ModelTransform.of(0.0F, 10.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData glass_r10 = glass.addChild("glass_r10", ModelPartBuilder.create().uv(47, 38).cuboid(-2.5F, -10.0F, -4.325F, 5.0F, 13.0F, 0.0F, new Dilation(-0.001F)), ModelTransform.of(0.0F, 10.0F, 0.0F, 0.0F, 2.0944F, 0.0F));

        ModelPartData glass_r11 = glass.addChild("glass_r11", ModelPartBuilder.create().uv(47, 38).cuboid(-2.5F, -10.0F, -4.325F, 5.0F, 13.0F, 0.0F, new Dilation(-0.001F)), ModelTransform.of(0.0F, 10.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData bone4 = rotor.addChild("bone4", ModelPartBuilder.create().uv(4, 86).cuboid(-0.5F, -9.525F, -3.0F, 1.0F, 14.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -17.5F, 0.0F, 0.0F, -1.5708F, 0.0F));

        ModelPartData cube_r129 = bone4.addChild("cube_r129", ModelPartBuilder.create().uv(98, 48).cuboid(-2.0F, -6.0F, -3.5F, 4.0F, 1.0F, 4.0F, new Dilation(0.003F)), ModelTransform.of(0.0F, 3.975F, 0.0F, 0.0F, 1.5708F, 0.0F));

        ModelPartData cube_r130 = bone4.addChild("cube_r130", ModelPartBuilder.create().uv(98, 48).cuboid(-2.0F, -6.0F, -3.5F, 4.0F, 1.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 3.975F, 0.0F, 0.0F, 0.5236F, 0.0F));

        ModelPartData cube_r131 = bone4.addChild("cube_r131", ModelPartBuilder.create().uv(98, 48).cuboid(-2.0F, -6.0F, -3.5F, 4.0F, 1.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 3.975F, 0.0F, 3.1416F, 0.5236F, 3.1416F));

        ModelPartData cube_r132 = bone4.addChild("cube_r132", ModelPartBuilder.create().uv(98, 48).cuboid(-2.0F, -6.0F, -3.5F, 4.0F, 1.0F, 4.0F, new Dilation(0.002F)), ModelTransform.of(0.0F, 3.975F, 0.0F, 0.0F, -0.5236F, 0.0F));

        ModelPartData cube_r133 = bone4.addChild("cube_r133", ModelPartBuilder.create().uv(98, 48).cuboid(-2.0F, -6.0F, -3.5F, 4.0F, 1.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 3.975F, 0.0F, 0.0F, -1.5708F, 0.0F));

        ModelPartData cube_r134 = bone4.addChild("cube_r134", ModelPartBuilder.create().uv(98, 48).cuboid(-2.0F, -6.0F, -3.5F, 4.0F, 1.0F, 4.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 3.975F, 0.0F, -3.1416F, -0.5236F, 3.1416F));

        ModelPartData cube_r135 = bone4.addChild("cube_r135", ModelPartBuilder.create().uv(4, 86).cuboid(-0.5F, -11.0F, -3.0F, 1.0F, 14.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 1.475F, 0.0F, -3.1416F, -1.0472F, 3.1416F));

        ModelPartData cube_r136 = bone4.addChild("cube_r136", ModelPartBuilder.create().uv(4, 86).cuboid(-0.5F, -11.0F, -3.0F, 1.0F, 14.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 1.475F, 0.0F, -3.1416F, 0.0F, 3.1416F));

        ModelPartData cube_r137 = bone4.addChild("cube_r137", ModelPartBuilder.create().uv(4, 86).cuboid(-0.5F, -11.0F, -3.0F, 1.0F, 14.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 1.475F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData cube_r138 = bone4.addChild("cube_r138", ModelPartBuilder.create().uv(4, 86).cuboid(-0.5F, -11.0F, -3.0F, 1.0F, 14.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 1.475F, 0.0F, 0.0F, 2.0944F, 0.0F));

        ModelPartData cube_r139 = bone4.addChild("cube_r139", ModelPartBuilder.create().uv(4, 86).cuboid(-0.5F, -11.0F, -3.0F, 1.0F, 14.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 1.475F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData glass2 = bone4.addChild("glass2", ModelPartBuilder.create().uv(59, 20).cuboid(-2.5F, 0.0F, -4.3F, 5.0F, 12.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 4.0F, 0.0F, 3.1416F, -0.5236F, 0.0F));

        ModelPartData glass_r12 = glass2.addChild("glass_r12", ModelPartBuilder.create().uv(59, 32).cuboid(-2.5F, 0.3F, 0.0F, 5.0F, 4.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 1.5708F, -1.0472F, 3.1416F));

        ModelPartData glass_r13 = glass2.addChild("glass_r13", ModelPartBuilder.create().uv(59, 32).cuboid(-2.5F, 0.299F, 0.0F, 5.0F, 4.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 0.0F, 0.0F, -1.5708F, -1.0472F, 0.0F));

        ModelPartData glass_r14 = glass2.addChild("glass_r14", ModelPartBuilder.create().uv(59, 32).cuboid(-2.5F, 0.3F, 0.0F, 5.0F, 4.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 1.5708F, 1.0472F, 3.1416F));

        ModelPartData glass_r15 = glass2.addChild("glass_r15", ModelPartBuilder.create().uv(59, 32).cuboid(-2.5F, 0.299F, 0.0F, 5.0F, 4.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 1.5708F, 0.0F, 3.1416F));

        ModelPartData glass_r16 = glass2.addChild("glass_r16", ModelPartBuilder.create().uv(59, 32).cuboid(-2.5F, 0.299F, 0.0F, 5.0F, 4.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 0.0F, 0.0F, -1.5708F, 1.0472F, 0.0F));

        ModelPartData glass_r17 = glass2.addChild("glass_r17", ModelPartBuilder.create().uv(59, 32).cuboid(-2.5F, 0.3F, 0.0F, 5.0F, 4.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 0.0F, 0.0F, -1.5708F, 0.0F, 0.0F));

        ModelPartData glass_r18 = glass2.addChild("glass_r18", ModelPartBuilder.create().uv(59, 20).cuboid(-2.5F, -10.0F, -4.3F, 5.0F, 12.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 10.0F, 0.0F, -3.1416F, -1.0472F, 3.1416F));

        ModelPartData glass_r19 = glass2.addChild("glass_r19", ModelPartBuilder.create().uv(59, 20).cuboid(-2.5F, -10.0F, -4.3F, 5.0F, 12.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 10.0F, 0.0F, -3.1416F, 0.0F, 3.1416F));

        ModelPartData glass_r20 = glass2.addChild("glass_r20", ModelPartBuilder.create().uv(59, 20).cuboid(-2.5F, -10.0F, -4.3F, 5.0F, 12.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 10.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData glass_r21 = glass2.addChild("glass_r21", ModelPartBuilder.create().uv(59, 20).cuboid(-2.5F, -10.0F, -4.3F, 5.0F, 12.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 10.0F, 0.0F, 0.0F, 2.0944F, 0.0F));

        ModelPartData glass_r22 = glass2.addChild("glass_r22", ModelPartBuilder.create().uv(59, 20).cuboid(-2.5F, -10.0F, -4.3F, 5.0F, 12.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 10.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData bone15 = rotor.addChild("bone15", ModelPartBuilder.create().uv(19, 73).cuboid(-1.0F, -1.0F, -1.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -12.5F, 0.0F));

        ModelPartData bone181 = hudolin.addChild("bone181", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, -10.5F, 0.0F));

        ModelPartData bone182 = bone181.addChild("bone182", ModelPartBuilder.create().uv(0, 88).cuboid(-9.0F, -1.0F, -13.0F, 18.0F, 2.0F, 13.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -5.0F, 16.316F, 0.2182F, 0.0F, 0.0F));

        ModelPartData bone183 = bone181.addChild("bone183", ModelPartBuilder.create(), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone193 = bone183.addChild("bone193", ModelPartBuilder.create().uv(0, 88).cuboid(-9.0F, -1.0F, -13.0F, 18.0F, 2.0F, 13.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -5.0F, 16.316F, 0.2182F, 0.0F, 0.0F));

        ModelPartData bone184 = bone183.addChild("bone184", ModelPartBuilder.create(), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone185 = bone184.addChild("bone185", ModelPartBuilder.create().uv(0, 88).cuboid(-9.0F, -1.0F, -13.0F, 18.0F, 2.0F, 13.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -5.0F, 16.316F, 0.2182F, 0.0F, 0.0F));

        ModelPartData bone186 = bone184.addChild("bone186", ModelPartBuilder.create(), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone187 = bone186.addChild("bone187", ModelPartBuilder.create().uv(0, 88).cuboid(-9.0F, -1.0F, -13.0F, 18.0F, 2.0F, 13.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -5.0F, 16.316F, 0.2182F, 0.0F, 0.0F));

        ModelPartData bone188 = bone186.addChild("bone188", ModelPartBuilder.create(), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone189 = bone188.addChild("bone189", ModelPartBuilder.create().uv(0, 88).cuboid(-9.0F, -1.0F, -13.0F, 18.0F, 2.0F, 13.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -5.0F, 16.316F, 0.2182F, 0.0F, 0.0F));

        ModelPartData bone190 = bone188.addChild("bone190", ModelPartBuilder.create(), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone191 = bone190.addChild("bone191", ModelPartBuilder.create().uv(0, 88).cuboid(-9.0F, -1.0F, -13.0F, 18.0F, 2.0F, 13.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -5.0F, 16.316F, 0.2182F, 0.0F, 0.0F));

        ModelPartData rotorouro3 = modelPartData.addChild("rotorouro3", ModelPartBuilder.create().uv(84, 40).cuboid(-2.0F, 12.5F, -3.5F, 4.0F, 6.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -0.5F, 0.0F));

        ModelPartData cube_r140 = rotorouro3.addChild("cube_r140", ModelPartBuilder.create().uv(84, 40).cuboid(-2.0F, -11.0F, -3.5F, 4.0F, 6.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 23.5F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData cube_r141 = rotorouro3.addChild("cube_r141", ModelPartBuilder.create().uv(84, 40).cuboid(-2.0F, -11.0F, -3.5F, 4.0F, 6.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 23.5F, 0.0F, 3.1416F, -1.0472F, -3.1416F));

        ModelPartData cube_r142 = rotorouro3.addChild("cube_r142", ModelPartBuilder.create().uv(84, 40).cuboid(-2.0F, -11.0F, -3.5F, 4.0F, 6.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 23.5F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData cube_r143 = rotorouro3.addChild("cube_r143", ModelPartBuilder.create().uv(84, 40).cuboid(-2.0F, -11.0F, -3.5F, 4.0F, 6.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 23.5F, 0.0F, 3.1416F, 1.0472F, 3.1416F));

        ModelPartData cube_r144 = rotorouro3.addChild("cube_r144", ModelPartBuilder.create().uv(84, 40).cuboid(-2.0F, -11.0F, -3.5F, 4.0F, 6.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 23.5F, 0.0F, 3.1416F, 0.0F, 3.1416F));

        ModelPartData rotorouro = rotorouro3.addChild("rotorouro", ModelPartBuilder.create().uv(82, 74).cuboid(-2.0F, -5.5F, -3.5F, 4.0F, 1.0F, 4.0F, new Dilation(0.001F)), ModelTransform.pivot(0.0F, 0.5F, 0.0F));

        ModelPartData cube_r145 = rotorouro.addChild("cube_r145", ModelPartBuilder.create().uv(82, 74).cuboid(-2.0F, -6.0F, -3.5F, 4.0F, 1.0F, 4.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 0.5F, 0.0F, 3.1416F, 1.0472F, 3.1416F));

        ModelPartData cube_r146 = rotorouro.addChild("cube_r146", ModelPartBuilder.create().uv(82, 74).cuboid(-2.0F, -6.0F, -3.5F, 4.0F, 1.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.5F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData cube_r147 = rotorouro.addChild("cube_r147", ModelPartBuilder.create().uv(82, 74).cuboid(-2.0F, -6.0F, -3.5F, 4.0F, 1.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.5F, 0.0F, 3.1416F, 0.0F, 3.1416F));

        ModelPartData cube_r148 = rotorouro.addChild("cube_r148", ModelPartBuilder.create().uv(82, 74).cuboid(-2.0F, -6.0F, -3.5F, 4.0F, 1.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.5F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData cube_r149 = rotorouro.addChild("cube_r149", ModelPartBuilder.create().uv(82, 74).cuboid(-2.0F, -6.0F, -3.5F, 4.0F, 1.0F, 4.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 0.5F, 0.0F, -3.1416F, -1.0472F, 3.1416F));

        ModelPartData cube_r150 = rotorouro.addChild("cube_r150", ModelPartBuilder.create().uv(8, 103).cuboid(-0.5F, -7.0F, -3.5F, 1.0F, 14.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -4.5F, 0.0F, 3.1416F, 0.5236F, 0.0F));

        ModelPartData cube_r151 = rotorouro.addChild("cube_r151", ModelPartBuilder.create().uv(8, 103).cuboid(-0.5F, -7.0F, -3.5F, 1.0F, 14.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -4.5F, 0.0F, -3.1416F, -0.5236F, 0.0F));

        ModelPartData cube_r152 = rotorouro.addChild("cube_r152", ModelPartBuilder.create().uv(8, 103).cuboid(-0.5F, -7.0F, -3.5F, 1.0F, 14.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -4.5F, 0.0F, 0.0F, 1.5708F, 3.1416F));

        ModelPartData cube_r153 = rotorouro.addChild("cube_r153", ModelPartBuilder.create().uv(8, 103).cuboid(-0.5F, -7.0F, -3.5F, 1.0F, 14.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -4.5F, 0.0F, 0.0F, -1.5708F, -3.1416F));

        ModelPartData cube_r154 = rotorouro.addChild("cube_r154", ModelPartBuilder.create().uv(8, 103).cuboid(-0.5F, -7.0F, -3.5F, 1.0F, 14.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -4.5F, 0.0F, 0.0F, 0.5236F, -3.1416F));

        ModelPartData cube_r155 = rotorouro.addChild("cube_r155", ModelPartBuilder.create().uv(8, 103).cuboid(-0.5F, -7.0F, -3.5F, 1.0F, 14.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -4.5F, 0.0F, 0.0F, -0.5236F, -3.1416F));

        ModelPartData cube_r156 = rotorouro.addChild("cube_r156", ModelPartBuilder.create().uv(0, 103).cuboid(-0.5F, -7.0F, -0.5F, 1.0F, 14.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(2.1651F, -4.5F, 1.25F, 3.1416F, 1.0472F, 0.0F));

        ModelPartData cube_r157 = rotorouro.addChild("cube_r157", ModelPartBuilder.create().uv(0, 103).cuboid(-0.5F, -7.0F, -0.5F, 1.0F, 14.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -4.5F, 2.5F, 3.1416F, 0.0F, 0.0F));

        ModelPartData cube_r158 = rotorouro.addChild("cube_r158", ModelPartBuilder.create().uv(0, 103).cuboid(-0.5F, -7.0F, -0.5F, 1.0F, 14.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(2.1651F, -4.5F, -1.25F, 0.0F, 1.0472F, -3.1416F));

        ModelPartData cube_r159 = rotorouro.addChild("cube_r159", ModelPartBuilder.create().uv(0, 103).cuboid(-0.5F, -7.0F, -0.5F, 1.0F, 14.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-2.1651F, -4.5F, 1.25F, 3.1416F, -1.0472F, 0.0F));

        ModelPartData cube_r160 = rotorouro.addChild("cube_r160", ModelPartBuilder.create().uv(0, 103).cuboid(-0.5F, -7.0F, -0.5F, 1.0F, 14.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-2.1651F, -4.5F, -1.25F, 0.0F, -1.0472F, -3.1416F));

        ModelPartData cube_r161 = rotorouro.addChild("cube_r161", ModelPartBuilder.create().uv(0, 103).cuboid(-0.5F, -7.0F, -0.5F, 1.0F, 14.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -4.5F, -2.5F, 0.0F, 0.0F, -3.1416F));

        ModelPartData glass3 = rotorouro.addChild("glass3", ModelPartBuilder.create().uv(70, 87).cuboid(-2.5F, 0.0F, -4.3F, 5.0F, 12.0F, 0.0F, new Dilation(0.001F)), ModelTransform.pivot(0.0F, -11.0F, 0.0F));

        ModelPartData glass_r23 = glass3.addChild("glass_r23", ModelPartBuilder.create().uv(80, 95).cuboid(-2.5F, 0.3F, 0.0F, 5.0F, 4.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 1.5708F, -1.0472F, 3.1416F));

        ModelPartData glass_r24 = glass3.addChild("glass_r24", ModelPartBuilder.create().uv(80, 95).cuboid(-2.5F, 0.299F, 0.0F, 5.0F, 4.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 0.0F, 0.0F, -1.5708F, -1.0472F, 0.0F));

        ModelPartData glass_r25 = glass3.addChild("glass_r25", ModelPartBuilder.create().uv(80, 95).cuboid(-2.5F, 0.3F, 0.0F, 5.0F, 4.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 1.5708F, 1.0472F, 3.1416F));

        ModelPartData glass_r26 = glass3.addChild("glass_r26", ModelPartBuilder.create().uv(80, 95).cuboid(-2.5F, 0.299F, 0.0F, 5.0F, 4.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 1.5708F, 0.0F, 3.1416F));

        ModelPartData glass_r27 = glass3.addChild("glass_r27", ModelPartBuilder.create().uv(80, 95).cuboid(-2.5F, 0.299F, 0.0F, 5.0F, 4.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 0.0F, 0.0F, -1.5708F, 1.0472F, 0.0F));

        ModelPartData glass_r28 = glass3.addChild("glass_r28", ModelPartBuilder.create().uv(80, 95).cuboid(-2.5F, 0.3F, 0.0F, 5.0F, 4.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 0.0F, 0.0F, -1.5708F, 0.0F, 0.0F));

        ModelPartData glass_r29 = glass3.addChild("glass_r29", ModelPartBuilder.create().uv(70, 87).cuboid(-2.5F, -10.0F, -4.3F, 5.0F, 12.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 10.0F, 0.0F, -3.1416F, -1.0472F, 3.1416F));

        ModelPartData glass_r30 = glass3.addChild("glass_r30", ModelPartBuilder.create().uv(70, 87).cuboid(-2.5F, -10.0F, -4.3F, 5.0F, 12.0F, 0.0F, new Dilation(-0.01F)), ModelTransform.of(0.0F, 10.0F, 0.0F, -3.1416F, 0.0F, 3.1416F));

        ModelPartData glass_r31 = glass3.addChild("glass_r31", ModelPartBuilder.create().uv(70, 87).cuboid(-2.5F, -10.0F, -4.3F, 5.0F, 12.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 10.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData glass_r32 = glass3.addChild("glass_r32", ModelPartBuilder.create().uv(70, 87).cuboid(-2.5F, -10.0F, -4.3F, 5.0F, 12.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 10.0F, 0.0F, 0.0F, 2.0944F, 0.0F));

        ModelPartData glass_r33 = glass3.addChild("glass_r33", ModelPartBuilder.create().uv(70, 87).cuboid(-2.5F, -10.0F, -4.3F, 5.0F, 12.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 10.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData rotorouro2 = rotorouro3.addChild("rotorouro2", ModelPartBuilder.create().uv(4, 103).cuboid(-0.5F, -9.5F, -3.0F, 1.0F, 14.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -18.5F, 0.0F, 0.0F, -1.5708F, 0.0F));

        ModelPartData cube_r162 = rotorouro2.addChild("cube_r162", ModelPartBuilder.create().uv(98, 74).cuboid(-2.0F, -6.0F, -3.5F, 4.0F, 1.0F, 4.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 3.5F, 0.0F, 0.0F, 1.5708F, 0.0F));

        ModelPartData cube_r163 = rotorouro2.addChild("cube_r163", ModelPartBuilder.create().uv(98, 74).cuboid(-2.0F, -6.0F, -3.5F, 4.0F, 1.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 3.5F, 0.0F, 0.0F, 0.5236F, 0.0F));

        ModelPartData cube_r164 = rotorouro2.addChild("cube_r164", ModelPartBuilder.create().uv(98, 74).cuboid(-2.0F, -6.0F, -3.5F, 4.0F, 1.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 3.5F, 0.0F, 3.1416F, 0.5236F, 3.1416F));

        ModelPartData cube_r165 = rotorouro2.addChild("cube_r165", ModelPartBuilder.create().uv(98, 74).cuboid(-2.0F, -6.0F, -3.5F, 4.0F, 1.0F, 4.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 3.5F, 0.0F, 0.0F, -0.5236F, 0.0F));

        ModelPartData cube_r166 = rotorouro2.addChild("cube_r166", ModelPartBuilder.create().uv(98, 74).cuboid(-2.0F, -6.0F, -3.5F, 4.0F, 1.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 3.5F, 0.0F, 0.0F, -1.5708F, 0.0F));

        ModelPartData cube_r167 = rotorouro2.addChild("cube_r167", ModelPartBuilder.create().uv(98, 74).cuboid(-2.0F, -6.0F, -3.5F, 4.0F, 1.0F, 4.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 3.5F, 0.0F, -3.1416F, -0.5236F, 3.1416F));

        ModelPartData cube_r168 = rotorouro2.addChild("cube_r168", ModelPartBuilder.create().uv(4, 103).cuboid(-0.5F, -11.0F, -3.0F, 1.0F, 14.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 1.5F, 0.0F, -3.1416F, -1.0472F, 3.1416F));

        ModelPartData cube_r169 = rotorouro2.addChild("cube_r169", ModelPartBuilder.create().uv(4, 103).cuboid(-0.5F, -11.0F, -3.0F, 1.0F, 14.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 1.5F, 0.0F, -3.1416F, 0.0F, 3.1416F));

        ModelPartData cube_r170 = rotorouro2.addChild("cube_r170", ModelPartBuilder.create().uv(4, 103).cuboid(-0.5F, -11.0F, -3.0F, 1.0F, 14.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 1.5F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData cube_r171 = rotorouro2.addChild("cube_r171", ModelPartBuilder.create().uv(4, 103).cuboid(-0.5F, -11.0F, -3.0F, 1.0F, 14.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 1.5F, 0.0F, 0.0F, 2.0944F, 0.0F));

        ModelPartData cube_r172 = rotorouro2.addChild("cube_r172", ModelPartBuilder.create().uv(4, 103).cuboid(-0.5F, -11.0F, -3.0F, 1.0F, 14.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 1.5F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData glass4 = rotorouro2.addChild("glass4", ModelPartBuilder.create().uv(90, 83).cuboid(-2.5F, 0.0F, -4.3F, 5.0F, 12.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 4.0F, 0.0F, 3.1416F, -0.5236F, 0.0F));

        ModelPartData glass_r34 = glass4.addChild("glass_r34", ModelPartBuilder.create().uv(90, 95).cuboid(-2.5F, 0.3F, 0.0F, 5.0F, 4.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 1.5708F, -1.0472F, 3.1416F));

        ModelPartData glass_r35 = glass4.addChild("glass_r35", ModelPartBuilder.create().uv(90, 95).cuboid(-2.5F, 0.299F, 0.0F, 5.0F, 4.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 0.0F, 0.0F, -1.5708F, -1.0472F, 0.0F));

        ModelPartData glass_r36 = glass4.addChild("glass_r36", ModelPartBuilder.create().uv(90, 95).cuboid(-2.5F, 0.3F, 0.0F, 5.0F, 4.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 1.5708F, 1.0472F, 3.1416F));

        ModelPartData glass_r37 = glass4.addChild("glass_r37", ModelPartBuilder.create().uv(90, 95).cuboid(-2.5F, 0.299F, 0.0F, 5.0F, 4.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 1.5708F, 0.0F, 3.1416F));

        ModelPartData glass_r38 = glass4.addChild("glass_r38", ModelPartBuilder.create().uv(90, 95).cuboid(-2.5F, 0.299F, 0.0F, 5.0F, 4.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 0.0F, 0.0F, -1.5708F, 1.0472F, 0.0F));

        ModelPartData glass_r39 = glass4.addChild("glass_r39", ModelPartBuilder.create().uv(90, 95).cuboid(-2.5F, 0.3F, 0.0F, 5.0F, 4.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 0.0F, 0.0F, -1.5708F, 0.0F, 0.0F));

        ModelPartData glass_r40 = glass4.addChild("glass_r40", ModelPartBuilder.create().uv(90, 83).cuboid(-2.5F, -10.0F, -4.3F, 5.0F, 12.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 10.0F, 0.0F, -3.1416F, -1.0472F, 3.1416F));

        ModelPartData glass_r41 = glass4.addChild("glass_r41", ModelPartBuilder.create().uv(90, 83).cuboid(-2.5F, -10.0F, -4.3F, 5.0F, 12.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 10.0F, 0.0F, -3.1416F, 0.0F, 3.1416F));

        ModelPartData glass_r42 = glass4.addChild("glass_r42", ModelPartBuilder.create().uv(90, 83).cuboid(-2.5F, -10.0F, -4.3F, 5.0F, 12.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 10.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData glass_r43 = glass4.addChild("glass_r43", ModelPartBuilder.create().uv(90, 83).cuboid(-2.5F, -10.0F, -4.3F, 5.0F, 12.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 10.0F, 0.0F, 0.0F, 2.0944F, 0.0F));

        ModelPartData glass_r44 = glass4.addChild("glass_r44", ModelPartBuilder.create().uv(90, 83).cuboid(-2.5F, -10.0F, -4.3F, 5.0F, 12.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 10.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData bone16 = rotorouro3.addChild("bone16", ModelPartBuilder.create().uv(14, 79).cuboid(-1.0F, -1.0F, -1.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -12.5F, 0.0F));
        return TexturedModelData.of(modelData, 256, 256);
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
        matrices.translate(0.26, 0.6675, 1.62);
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180f));
        matrices.scale(0.003f, 0.003f, 0.003f);
        matrices.multiply(RotationAxis.NEGATIVE_Z.rotationDegrees(0));
        matrices.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(-60f));
        matrices.translate(-240f, -228, -5f);
        String positionPosText = " " + abppPos.getX() + ", " + abppPos.getY() + ", " + abppPos.getZ();
        Text positionDimensionText = WorldUtil.worldText(abpp.getDimension());
        String positionDirectionText = " " + DirectionControl.rotationToDirection(abpp.getRotation()).toUpperCase();
        String destinationPosText = " " + abpdPos.getX() + ", " + abpdPos.getY() + ", " + abpdPos.getZ();
        Text destinationDimensionText = WorldUtil.worldText(abpd.getDimension(), false);
        String destinationDirectionText = " " + DirectionControl.rotationToDirection(abpd.getRotation()).toUpperCase();
        renderer.drawWithOutline(positionDimensionText.asOrderedText(), -43 - renderer.getWidth(positionDimensionText) / 2, 76, 0xFFFFFF, 0x000000,
                matrices.peek().getPositionMatrix(), vertexConsumers, 0xF000F0);
        matrices.pop();
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red,
            float green, float blue, float alpha) {
        hudolin.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
    }

    @Override
    public void renderWithAnimations(ConsoleBlockEntity console, ClientTardis tardis, ModelPart root, MatrixStack matrices,
                                     VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float pAlpha) {
        matrices.push();
        matrices.translate(0.5f, -1.5f, -0.5f);
        super.renderWithAnimations(console, tardis, root, matrices, vertices, light, overlay, red, green, blue, pAlpha);
        matrices.pop();
    }

    @Override
    public Animation getAnimationForState(TravelHandlerBase.State state) {
        if (state == TravelHandlerBase.State.LANDED)
            return Animation.Builder.create(0).build();

        return HudolinAnimations.HUDOLIN_FLIGHT;
    }

    @Override
    public ModelPart getPart() {
        return hudolin;
    }
}
