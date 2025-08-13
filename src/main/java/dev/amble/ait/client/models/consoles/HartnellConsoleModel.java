package dev.amble.ait.client.models.consoles;

import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.animation.Animation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import dev.amble.ait.AITMod;
import dev.amble.ait.api.tardis.TardisComponent;
import dev.amble.ait.client.animation.console.hartnell.HartnellAnimations;
import dev.amble.ait.client.tardis.ClientTardis;
import dev.amble.ait.core.blockentities.ConsoleBlockEntity;
import dev.amble.ait.core.tardis.control.impl.pos.IncrementManager;
import dev.amble.ait.core.tardis.handler.CloakHandler;
import dev.amble.ait.core.tardis.handler.FuelHandler;
import dev.amble.ait.core.tardis.handler.travel.TravelHandlerBase;

// Made with Blockbench 4.9.2
// Exported for Minecraft version 1.17+ for Yarn
// Paste this class into your mod and generate all required imports
public class HartnellConsoleModel extends SimpleConsoleModel {

    public static final Identifier TEXTURE = new Identifier(AITMod.MOD_ID,
            "textures/blockentities/consoles/hartnell_console.png");
    public static final Identifier EMISSION = new Identifier(AITMod.MOD_ID,
            "textures/blockentities/consoles/hartnell_console_emission.png");

    private final ModelPart bone;

    public HartnellConsoleModel(ModelPart root) {
        this.bone = root.getChild("bone");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData bone = modelPartData.addChild("bone", ModelPartBuilder.create(),
                ModelTransform.of(0.0F, 24.0F, 0.0F, 0.0F, 1.5708F, 0.0F));

        ModelPartData base = bone.addChild("base", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData bone19 = base.addChild("bone19", ModelPartBuilder.create().uv(56, 34).cuboid(-0.475F, -1.0F,
                -5.5F, 10.0F, 1.0F, 11.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData bone3 = bone19.addChild("bone3", ModelPartBuilder.create().uv(56, 34).cuboid(-0.475F, -1.0F,
                -5.5F, 10.0F, 1.0F, 11.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone4 = bone3.addChild("bone4", ModelPartBuilder.create().uv(56, 34).cuboid(-0.475F, -1.0F, -5.5F,
                10.0F, 1.0F, 11.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone5 = bone4.addChild("bone5", ModelPartBuilder.create().uv(56, 34).cuboid(-0.475F, -1.0F, -5.5F,
                10.0F, 1.0F, 11.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone6 = bone5.addChild("bone6", ModelPartBuilder.create().uv(56, 34).cuboid(-0.475F, -1.0F, -5.5F,
                10.0F, 1.0F, 11.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone7 = bone6.addChild("bone7", ModelPartBuilder.create().uv(56, 34).cuboid(-0.475F, -1.0F, -5.5F,
                10.0F, 1.0F, 11.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone8 = base.addChild("bone8", ModelPartBuilder.create().uv(67, 47).cuboid(6.25F, -16.0F, -0.5F,
                4.0F, 14.0F, 1.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 1.75F, 0.0F, 0.0F, -0.5236F, 0.0F));

        ModelPartData cube_r1 = bone8.addChild("cube_r1",
                ModelPartBuilder.create().uv(0, 21).cuboid(1.0F, -22.0F, -0.5F, 4.0F, 14.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(-1.9319F, -14.4824F, 0.0F, 0.0F, 0.0F, 1.309F));

        ModelPartData bone9 = bone8.addChild("bone9", ModelPartBuilder.create().uv(67, 47).cuboid(6.25F, -16.0F, -0.5F,
                4.0F, 14.0F, 1.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData cube_r2 = bone9.addChild("cube_r2",
                ModelPartBuilder.create().uv(0, 21).cuboid(1.0F, -22.0F, -0.5F, 4.0F, 14.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(-1.9319F, -14.4824F, 0.0F, 0.0F, 0.0F, 1.309F));

        ModelPartData bone10 = bone9.addChild("bone10", ModelPartBuilder.create().uv(67, 47).cuboid(6.25F, -16.0F,
                -0.5F, 4.0F, 14.0F, 1.0F, new Dilation(0.001F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData cube_r3 = bone10.addChild("cube_r3",
                ModelPartBuilder.create().uv(0, 21).cuboid(1.0F, -22.0F, -0.5F, 4.0F, 14.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(-1.9319F, -14.4824F, 0.0F, 0.0F, 0.0F, 1.309F));

        ModelPartData bone11 = bone10.addChild("bone11", ModelPartBuilder.create().uv(67, 47).cuboid(6.25F, -16.0F,
                -0.5F, 4.0F, 14.0F, 1.0F, new Dilation(0.001F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData cube_r4 = bone11.addChild("cube_r4",
                ModelPartBuilder.create().uv(0, 21).cuboid(1.0F, -22.0F, -0.5F, 4.0F, 14.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(-1.9319F, -14.4824F, 0.0F, 0.0F, 0.0F, 1.309F));

        ModelPartData bone12 = bone11.addChild("bone12", ModelPartBuilder.create().uv(67, 47).cuboid(6.25F, -16.0F,
                -0.5F, 4.0F, 14.0F, 1.0F, new Dilation(0.001F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData cube_r5 = bone12.addChild("cube_r5",
                ModelPartBuilder.create().uv(0, 21).cuboid(1.0F, -22.0F, -0.5F, 4.0F, 14.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(-1.9319F, -14.4824F, 0.0F, 0.0F, 0.0F, 1.309F));

        ModelPartData bone13 = bone12.addChild("bone13", ModelPartBuilder.create().uv(67, 47).cuboid(6.25F, -16.0F,
                -0.5F, 4.0F, 14.0F, 1.0F, new Dilation(0.001F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData cube_r6 = bone13.addChild("cube_r6",
                ModelPartBuilder.create().uv(0, 21).cuboid(1.0F, -22.0F, -0.5F, 4.0F, 14.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(-1.9319F, -14.4824F, 0.0F, 0.0F, 0.0F, 1.309F));

        ModelPartData bone14 = base.addChild("bone14", ModelPartBuilder.create(),
                ModelTransform.pivot(0.0F, 1.0F, 0.0F));

        ModelPartData cube_r7 = bone14.addChild("cube_r7", ModelPartBuilder.create().uv(0, 42).cuboid(-0.0282F,
                -4.6997F, -5.0F, 0.0F, 5.0F, 10.0F, new Dilation(0.0F)),
                ModelTransform.of(9.0F, -2.0F, 0.0F, 0.0F, 0.0F, -0.7418F));

        ModelPartData bone2 = bone14.addChild("bone2", ModelPartBuilder.create(),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData cube_r8 = bone2.addChild("cube_r8", ModelPartBuilder.create().uv(0, 42).cuboid(-0.0282F, -4.6997F,
                -5.0F, 0.0F, 5.0F, 10.0F, new Dilation(0.0F)),
                ModelTransform.of(9.0F, -2.0F, 0.0F, 0.0F, 0.0F, -0.7418F));

        ModelPartData bone15 = bone2.addChild("bone15", ModelPartBuilder.create(),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData cube_r9 = bone15.addChild("cube_r9", ModelPartBuilder.create().uv(0, 42).cuboid(-0.0282F,
                -4.6997F, -5.0F, 0.0F, 5.0F, 10.0F, new Dilation(0.0F)),
                ModelTransform.of(9.0F, -2.0F, 0.0F, 0.0F, 0.0F, -0.7418F));

        ModelPartData bone16 = bone15.addChild("bone16", ModelPartBuilder.create(),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData cube_r10 = bone16.addChild("cube_r10", ModelPartBuilder.create().uv(0, 42).cuboid(-0.0282F,
                -4.6997F, -5.0F, 0.0F, 5.0F, 10.0F, new Dilation(0.0F)),
                ModelTransform.of(9.0F, -2.0F, 0.0F, 0.0F, 0.0F, -0.7418F));

        ModelPartData bone17 = bone16.addChild("bone17", ModelPartBuilder.create(),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData cube_r11 = bone17.addChild("cube_r11", ModelPartBuilder.create().uv(0, 42).cuboid(-0.0282F,
                -4.6997F, -5.0F, 0.0F, 5.0F, 10.0F, new Dilation(0.0F)),
                ModelTransform.of(9.0F, -2.0F, 0.0F, 0.0F, 0.0F, -0.7418F));

        ModelPartData bone18 = bone17.addChild("bone18", ModelPartBuilder.create(),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData cube_r12 = bone18.addChild("cube_r12", ModelPartBuilder.create().uv(0, 42).cuboid(-0.0282F,
                -4.6997F, -5.0F, 0.0F, 5.0F, 10.0F, new Dilation(0.0F)),
                ModelTransform.of(9.0F, -2.0F, 0.0F, 0.0F, 0.0F, -0.7418F));

        ModelPartData rim = bone.addChild("rim", ModelPartBuilder.create().uv(0, 42).cuboid(17.185F, -17.0F, -10.5F,
                1.0F, 2.0F, 21.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 1.4F, 0.0F));

        ModelPartData bone27 = rim.addChild("bone27", ModelPartBuilder.create().uv(0, 42).cuboid(17.185F, -17.0F,
                -10.5F, 1.0F, 2.0F, 21.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone28 = bone27.addChild("bone28", ModelPartBuilder.create().uv(0, 42).cuboid(17.185F, -17.0F,
                -10.5F, 1.0F, 2.0F, 21.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone29 = bone28.addChild("bone29", ModelPartBuilder.create().uv(0, 42).cuboid(17.185F, -17.0F,
                -10.5F, 1.0F, 2.0F, 21.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone30 = bone29.addChild("bone30", ModelPartBuilder.create().uv(0, 42).cuboid(17.185F, -17.0F,
                -10.5F, 1.0F, 2.0F, 21.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone31 = bone30.addChild("bone31", ModelPartBuilder.create().uv(0, 42).cuboid(17.185F, -17.0F,
                -10.5F, 1.0F, 2.0F, 21.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bottom = bone.addChild("bottom", ModelPartBuilder.create().uv(0, 21).cuboid(5.25F, -16.0F, -10.0F,
                12.0F, 0.0F, 20.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 2.0F, 0.0F));

        ModelPartData bone51 = bottom.addChild("bone51", ModelPartBuilder.create().uv(0, 21).cuboid(5.25F, -16.0F,
                -10.0F, 12.0F, 0.0F, 20.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone52 = bone51.addChild("bone52", ModelPartBuilder.create().uv(0, 21).cuboid(5.25F, -16.0F,
                -10.0F, 12.0F, 0.0F, 20.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone53 = bone52.addChild("bone53", ModelPartBuilder.create().uv(0, 21).cuboid(5.25F, -16.0F,
                -10.0F, 12.0F, 0.0F, 20.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone54 = bone53.addChild("bone54", ModelPartBuilder.create().uv(0, 21).cuboid(5.25F, -16.0F,
                -10.0F, 12.0F, 0.0F, 20.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone55 = bone54.addChild("bone55", ModelPartBuilder.create().uv(0, 21).cuboid(5.25F, -16.0F,
                -10.0F, 12.0F, 0.0F, 20.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone32 = bone.addChild("bone32", ModelPartBuilder.create(),
                ModelTransform.of(0.0F, 1.0F, 0.0F, 0.0F, -0.5236F, 0.0F));

        ModelPartData cube_r13 = bone32.addChild("cube_r13",
                ModelPartBuilder.create().uv(47, 13).cuboid(-9.6F, -0.75F, 0.0F, 14.0F, 1.0F, 0.0F, new Dilation(0.3F)),
                ModelTransform.of(16.0F, -17.0F, 0.0F, 0.0F, 0.0F, 0.3142F));

        ModelPartData bone20 = bone32.addChild("bone20", ModelPartBuilder.create(),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData cube_r14 = bone20.addChild("cube_r14",
                ModelPartBuilder.create().uv(47, 13).cuboid(-9.6F, -0.75F, 0.0F, 14.0F, 1.0F, 0.0F, new Dilation(0.3F)),
                ModelTransform.of(16.0F, -17.0F, 0.0F, 0.0F, 0.0F, 0.3142F));

        ModelPartData bone21 = bone20.addChild("bone21", ModelPartBuilder.create(),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData cube_r15 = bone21.addChild("cube_r15",
                ModelPartBuilder.create().uv(47, 13).cuboid(-9.6F, -0.75F, 0.0F, 14.0F, 1.0F, 0.0F, new Dilation(0.3F)),
                ModelTransform.of(16.0F, -17.0F, 0.0F, 0.0F, 0.0F, 0.3142F));

        ModelPartData bone22 = bone21.addChild("bone22", ModelPartBuilder.create(),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData cube_r16 = bone22.addChild("cube_r16",
                ModelPartBuilder.create().uv(47, 13).cuboid(-9.6F, -0.75F, 0.0F, 14.0F, 1.0F, 0.0F, new Dilation(0.3F)),
                ModelTransform.of(16.0F, -17.0F, 0.0F, 0.0F, 0.0F, 0.3142F));

        ModelPartData bone23 = bone22.addChild("bone23", ModelPartBuilder.create(),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData cube_r17 = bone23.addChild("cube_r17",
                ModelPartBuilder.create().uv(47, 13).cuboid(-9.6F, -0.75F, 0.0F, 14.0F, 1.0F, 0.0F, new Dilation(0.3F)),
                ModelTransform.of(16.0F, -17.0F, 0.0F, 0.0F, 0.0F, 0.3142F));

        ModelPartData bone24 = bone23.addChild("bone24", ModelPartBuilder.create(),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData cube_r18 = bone24.addChild("cube_r18",
                ModelPartBuilder.create().uv(47, 13).cuboid(-9.6F, -0.75F, 0.0F, 14.0F, 1.0F, 0.0F, new Dilation(0.3F)),
                ModelTransform.of(16.0F, -17.0F, 0.0F, 0.0F, 0.0F, 0.3142F));

        ModelPartData panels = bone.addChild("panels", ModelPartBuilder.create(),
                ModelTransform.pivot(0.0F, -2.0F, 0.0F));

        ModelPartData p_1 = panels.addChild("p_1", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData bone38 = p_1.addChild("bone38", ModelPartBuilder.create().uv(0, 0).cuboid(-11.25F, -0.95F, -10.0F,
                13.0F, 0.0F, 20.0F, new Dilation(0.0F)), ModelTransform.of(16.0F, -13.0F, 0.0F, 0.0F, 0.0F, 0.2618F));

        ModelPartData bone36 = bone38.addChild("bone36", ModelPartBuilder.create(),
                ModelTransform.pivot(-16.0F, 13.0F, 0.0F));

        ModelPartData bone37 = bone36.addChild("bone37",
                ModelPartBuilder.create().uv(45, 21).cuboid(6.7F, -14.15F, -2.0F, 2.0F, 0.0F, 3.0F, new Dilation(0.0F))
                        .uv(45, 21).cuboid(11.2F, -14.15F, -2.0F, 2.0F, 0.0F, 3.0F, new Dilation(0.0F)),
                ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData sl_switch_1 = bone37.addChild("sl_switch_1", ModelPartBuilder.create(),
                ModelTransform.pivot(0.0F, 2.0F, 0.0F));

        ModelPartData cube_r19 = sl_switch_1.addChild("cube_r19",
                ModelPartBuilder.create().uv(53, 68).cuboid(-0.8F, -0.65F, 1.05F, 2.0F, 1.0F, 1.0F, new Dilation(0.1F)),
                ModelTransform.of(14.9F, -15.5F, 0.0F, 0.0F, 0.0F, 0.0F));

        ModelPartData bone33 = sl_switch_1.addChild("bone33", ModelPartBuilder.create(),
                ModelTransform.pivot(14.9F, -15.3F, 0.0F));

        ModelPartData cube_r20 = bone33.addChild("cube_r20",
                ModelPartBuilder.create().uv(14, 18).cuboid(-0.8F, -1.0F, 1.05F, 1.0F, 0.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F));

        ModelPartData sl_switch_2 = bone37.addChild("sl_switch_2", ModelPartBuilder.create(),
                ModelTransform.pivot(0.0F, 2.0F, 0.0F));

        ModelPartData cube_r21 = sl_switch_2.addChild("cube_r21",
                ModelPartBuilder.create().uv(32, 68).cuboid(-0.8F, -0.65F, 1.05F, 2.0F, 1.0F, 1.0F, new Dilation(0.1F)),
                ModelTransform.of(14.9F, -15.5F, 1.5F, 0.0F, 0.0F, 0.0F));

        ModelPartData bone34 = sl_switch_2.addChild("bone34", ModelPartBuilder.create(),
                ModelTransform.pivot(14.9F, -15.3F, 1.5F));

        ModelPartData cube_r22 = bone34.addChild("cube_r22",
                ModelPartBuilder.create().uv(14, 18).cuboid(-0.8F, -1.0F, 1.05F, 1.0F, 0.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F));

        ModelPartData sl_switch_3 = bone37.addChild("sl_switch_3", ModelPartBuilder.create(),
                ModelTransform.pivot(0.0F, 2.0F, 0.0F));

        ModelPartData cube_r23 = sl_switch_3.addChild("cube_r23",
                ModelPartBuilder.create().uv(39, 68).cuboid(-0.8F, -0.65F, 1.05F, 2.0F, 1.0F, 1.0F, new Dilation(0.1F)),
                ModelTransform.of(14.9F, -15.5F, 3.0F, 0.0F, 0.0F, 0.0F));

        ModelPartData bone35 = sl_switch_3.addChild("bone35", ModelPartBuilder.create(),
                ModelTransform.pivot(14.9F, -15.3F, 3.0F));

        ModelPartData cube_r24 = bone35.addChild("cube_r24",
                ModelPartBuilder.create().uv(14, 18).cuboid(-0.8F, -1.0F, 1.05F, 1.0F, 0.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F));

        ModelPartData sl_switch_4 = bone37.addChild("sl_switch_4", ModelPartBuilder.create(),
                ModelTransform.pivot(0.0F, 2.0F, 0.0F));

        ModelPartData cube_r25 = sl_switch_4.addChild("cube_r25",
                ModelPartBuilder.create().uv(32, 68).cuboid(-0.8F, -0.65F, 1.05F, 2.0F, 1.0F, 1.0F, new Dilation(0.1F)),
                ModelTransform.of(14.9F, -15.5F, 4.5F, 0.0F, 0.0F, 0.0F));

        ModelPartData bone47 = sl_switch_4.addChild("bone47", ModelPartBuilder.create(),
                ModelTransform.pivot(14.9F, -15.3F, 4.5F));

        ModelPartData cube_r26 = bone47.addChild("cube_r26",
                ModelPartBuilder.create().uv(14, 18).cuboid(-0.8F, -1.0F, 1.05F, 1.0F, 0.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F));

        ModelPartData ind_lamp_1 = bone37.addChild("ind_lamp_1",
                ModelPartBuilder.create().uv(56, 42).cuboid(11.2F, -16.15F, -4.7F, 2.0F, 0.0F, 2.0F, new Dilation(0.0F))
                        .uv(45, 25).cuboid(11.2F, -17.0F, -4.7F, 2.0F, 2.0F, 2.0F, new Dilation(-0.4F)),
                ModelTransform.pivot(0.0F, 2.0F, 0.0F));

        ModelPartData bone91 = ind_lamp_1.addChild("bone91", ModelPartBuilder.create().uv(11, 42).cuboid(-4.8F, -1.0F,
                -4.7F, 2.0F, 2.0F, 2.0F, new Dilation(-0.38F)), ModelTransform.pivot(16.0F, -16.0F, 0.0F));

        ModelPartData ind_lamp_2 = bone37.addChild("ind_lamp_2",
                ModelPartBuilder.create().uv(45, 25).cuboid(9.0F, -17.0F, -3.7F, 2.0F, 2.0F, 2.0F, new Dilation(-0.4F))
                        .uv(56, 42).cuboid(9.0F, -16.15F, -3.7F, 2.0F, 0.0F, 2.0F, new Dilation(0.0F)),
                ModelTransform.pivot(0.0F, 2.0F, 0.0F));

        ModelPartData bone93 = ind_lamp_2.addChild("bone93", ModelPartBuilder.create().uv(11, 42).cuboid(-7.0F, -1.0F,
                -3.7F, 2.0F, 2.0F, 2.0F, new Dilation(-0.38F)), ModelTransform.pivot(16.0F, -16.0F, 0.0F));

        ModelPartData ind_lamp_3 = bone37.addChild("ind_lamp_3",
                ModelPartBuilder.create().uv(56, 42).cuboid(9.0F, -16.15F, -1.5F, 2.0F, 0.0F, 2.0F, new Dilation(0.0F))
                        .uv(45, 25).cuboid(9.0F, -17.0F, -1.5F, 2.0F, 2.0F, 2.0F, new Dilation(-0.4F)),
                ModelTransform.pivot(0.0F, 2.0F, 0.0F));

        ModelPartData bone92 = ind_lamp_3.addChild("bone92", ModelPartBuilder.create().uv(11, 42).cuboid(-7.0F, -1.0F,
                -1.5F, 2.0F, 2.0F, 2.0F, new Dilation(-0.38F)), ModelTransform.pivot(16.0F, -16.0F, 0.0F));

        ModelPartData ind_lamp_4 = bone37.addChild("ind_lamp_4",
                ModelPartBuilder.create().uv(56, 42).cuboid(9.0F, -16.15F, 0.7F, 2.0F, 0.0F, 2.0F, new Dilation(0.0F))
                        .uv(45, 25).cuboid(9.0F, -17.0F, 0.7F, 2.0F, 2.0F, 2.0F, new Dilation(-0.4F)),
                ModelTransform.pivot(0.0F, 2.0F, 0.0F));

        ModelPartData bone94 = ind_lamp_4.addChild("bone94",
                ModelPartBuilder.create().uv(11, 42).cuboid(-7.0F, -1.0F, 0.7F, 2.0F, 2.0F, 2.0F, new Dilation(-0.38F)),
                ModelTransform.pivot(16.0F, -16.0F, 0.0F));

        ModelPartData ind_lamp_5 = bone37.addChild("ind_lamp_5",
                ModelPartBuilder.create().uv(56, 42).cuboid(11.2F, -16.15F, 1.7F, 2.0F, 0.0F, 2.0F, new Dilation(0.0F))
                        .uv(45, 25).cuboid(11.2F, -17.0F, 1.7F, 2.0F, 2.0F, 2.0F, new Dilation(-0.4F)),
                ModelTransform.pivot(0.0F, 2.0F, 0.0F));

        ModelPartData bone95 = ind_lamp_5.addChild("bone95",
                ModelPartBuilder.create().uv(11, 42).cuboid(-4.8F, -1.0F, 1.7F, 2.0F, 2.0F, 2.0F, new Dilation(-0.38F)),
                ModelTransform.pivot(16.0F, -16.0F, 0.0F));

        ModelPartData m_lever_1 = bone37.addChild("m_lever_1",
                ModelPartBuilder.create().uv(0, 38).cuboid(13.35F, -16.15F, -7.0F, 4.0F, 0.0F, 2.0F, new Dilation(0.0F))
                        .uv(45, 34).cuboid(13.35F, -17.4F, -7.0F, 4.0F, 2.0F, 2.0F, new Dilation(-0.4F)),
                ModelTransform.pivot(0.0F, 2.0F, 0.0F));

        ModelPartData bone45 = m_lever_1.addChild("bone45",
                ModelPartBuilder.create().uv(17, 0).cuboid(-0.6F, -1.5F, 0.0F, 1.0F, 1.0F, 0.0F, new Dilation(0.0F)),
                ModelTransform.of(15.6F, -16.35F, -6.0F, 0.0F, 0.0F, -0.4363F));

        ModelPartData cube_r27 = bone45.addChild("cube_r27",
                ModelPartBuilder.create().uv(10, 61).cuboid(-0.6F, -1.5F, -0.4F, 1.0F, 0.0F, 1.0F, new Dilation(0.2F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -0.7854F, 0.0F));

        ModelPartData st_switch = bone37.addChild("st_switch",
                ModelPartBuilder.create().uv(47, 16)
                        .cuboid(13.95F, -16.15F, -4.8F, 3.0F, 0.0F, 3.0F, new Dilation(0.0F)).uv(24, 55)
                        .cuboid(13.95F, -16.85F, -4.75F, 3.0F, 1.0F, 3.0F, new Dilation(-0.3F)),
                ModelTransform.pivot(0.0F, 2.0F, 0.0F));

        ModelPartData bone26 = st_switch.addChild("bone26",
                ModelPartBuilder.create().uv(0, 13).cuboid(0.0F, -1.0F, 0.0F, 2.0F, 1.0F, 0.0F, new Dilation(0.0F)),
                ModelTransform.of(15.5F, -16.55F, -3.25F, 0.0F, -0.5236F, 0.0F));

        ModelPartData m_lever_2 = bone37.addChild("m_lever_2",
                ModelPartBuilder.create().uv(0, 38).cuboid(13.35F, -16.15F, -1.6F, 4.0F, 0.0F, 2.0F, new Dilation(0.0F))
                        .uv(45, 34).cuboid(13.35F, -17.4F, -1.6F, 4.0F, 2.0F, 2.0F, new Dilation(-0.4F)),
                ModelTransform.pivot(0.0F, 2.0F, 0.0F));

        ModelPartData bone46 = m_lever_2.addChild("bone46",
                ModelPartBuilder.create().uv(17, 0).cuboid(-0.6F, -1.5F, 0.0F, 1.0F, 1.0F, 0.0F, new Dilation(0.0F)),
                ModelTransform.of(15.6F, -16.35F, -0.6F, 0.0F, 0.0F, -0.4363F));

        ModelPartData cube_r28 = bone46.addChild("cube_r28",
                ModelPartBuilder.create().uv(10, 61).cuboid(-0.6F, -1.5F, -0.4F, 1.0F, 0.0F, 1.0F, new Dilation(0.2F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -0.7854F, 0.0F));

        ModelPartData fastreturn = bone37.addChild("fastreturn",
                ModelPartBuilder.create().uv(11, 26).cuboid(8.2F, -17.3F, 3.0F, 2.0F, 2.0F, 2.0F, new Dilation(-0.401F))
                        .uv(24, 42).cuboid(8.2F, -17.0F, 3.0F, 3.0F, 2.0F, 2.0F, new Dilation(-0.4F)),
                ModelTransform.pivot(0.0F, 2.0F, 0.0F));

        ModelPartData bone25 = fastreturn.addChild("bone25",
                ModelPartBuilder.create().uv(0, 47).cuboid(-1.0F, -2.0F, -1.0F, 2.0F, 2.0F, 2.0F, new Dilation(-0.6F)),
                ModelTransform.pivot(9.75F, -15.9F, 4.0F));

        ModelPartData p_2 = panels.addChild("p_2", ModelPartBuilder.create(),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData bone48 = p_2
                .addChild(
                        "bone48", ModelPartBuilder.create().uv(58, 47).cuboid(-11.25F, -0.95F, -10.0F, 13.0F, 0.0F,
                                20.0F, new Dilation(0.0F)),
                        ModelTransform.of(16.0F, -13.0F, 0.0F, 0.0F, 0.0F, 0.2618F));

        ModelPartData bone49 = bone48.addChild("bone49", ModelPartBuilder.create(),
                ModelTransform.pivot(-16.0F, 13.0F, 0.0F));

        ModelPartData bone50 = bone49.addChild("bone50",
                ModelPartBuilder.create().uv(54, 35).cuboid(6.7F, -14.15F, -2.0F, 2.0F, 0.0F, 4.0F, new Dilation(0.0F)),
                ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData s_lever = bone50.addChild("s_lever", ModelPartBuilder.create(),
                ModelTransform.pivot(0.0F, 2.0F, 0.0F));

        ModelPartData cube_r29 = s_lever.addChild("cube_r29",
                ModelPartBuilder.create().uv(66, 63).cuboid(0.2F, -0.65F, -6.95F, 2.0F, 1.0F, 1.0F, new Dilation(0.1F)),
                ModelTransform.of(9.4F, -15.5F, 2.0F, 0.0F, 0.0F, 0.0F));

        ModelPartData bone61 = s_lever.addChild("bone61", ModelPartBuilder.create().uv(50, 39).cuboid(-1.0501F, 0.0F,
                -0.2F, 1.0F, 0.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(10.65F, -16.25F, -4.5F, 0.0F, 0.0F, 0.7418F));

        ModelPartData sl_switch_5 = bone50.addChild("sl_switch_5", ModelPartBuilder.create(),
                ModelTransform.pivot(0.0F, 2.0F, 0.0F));

        ModelPartData cube_r30 = sl_switch_5.addChild("cube_r30",
                ModelPartBuilder.create().uv(9, 58).cuboid(-0.8F, -0.65F, 1.05F, 2.0F, 1.0F, 1.0F, new Dilation(0.1F)),
                ModelTransform.of(9.4F, -15.5F, 2.0F, 0.0F, 0.0F, 0.0F));

        ModelPartData bone56 = sl_switch_5.addChild("bone56", ModelPartBuilder.create(),
                ModelTransform.pivot(9.4F, -15.3F, 2.0F));

        ModelPartData cube_r31 = bone56.addChild("cube_r31",
                ModelPartBuilder.create().uv(14, 18).cuboid(-0.8F, -1.0F, 1.05F, 1.0F, 0.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F));

        ModelPartData sl_switch_6 = bone50.addChild("sl_switch_6", ModelPartBuilder.create(),
                ModelTransform.pivot(0.0F, 2.0F, 0.0F));

        ModelPartData cube_r32 = sl_switch_6.addChild("cube_r32",
                ModelPartBuilder.create().uv(39, 68).cuboid(-0.8F, -0.65F, 1.05F, 2.0F, 1.0F, 1.0F, new Dilation(0.1F)),
                ModelTransform.of(12.9F, -15.5F, 3.0F, 0.0F, 0.0F, 0.0F));

        ModelPartData bone57 = sl_switch_6.addChild("bone57", ModelPartBuilder.create(),
                ModelTransform.pivot(12.9F, -15.3F, 3.0F));

        ModelPartData cube_r33 = bone57.addChild("cube_r33",
                ModelPartBuilder.create().uv(14, 18).cuboid(-0.8F, -1.0F, 1.05F, 1.0F, 0.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F));

        ModelPartData sl_switch_7 = bone50.addChild("sl_switch_7", ModelPartBuilder.create(),
                ModelTransform.pivot(0.0F, 2.0F, 0.0F));

        ModelPartData cube_r34 = sl_switch_7.addChild("cube_r34",
                ModelPartBuilder.create().uv(9, 58).cuboid(-0.8F, -0.65F, 1.05F, 2.0F, 1.0F, 1.0F, new Dilation(0.1F)),
                ModelTransform.of(12.9F, -15.5F, 4.5F, 0.0F, 0.0F, 0.0F));

        ModelPartData bone58 = sl_switch_7.addChild("bone58", ModelPartBuilder.create(),
                ModelTransform.pivot(12.9F, -15.3F, 4.5F));

        ModelPartData cube_r35 = bone58.addChild("cube_r35",
                ModelPartBuilder.create().uv(14, 18).cuboid(-0.8F, -1.0F, 1.05F, 1.0F, 0.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F));

        ModelPartData s_crank_1 = bone50.addChild("s_crank_1", ModelPartBuilder.create(),
                ModelTransform.pivot(0.0F, 2.0F, 0.0F));

        ModelPartData bone59 = s_crank_1.addChild("bone59",
                ModelPartBuilder.create().uv(0, 58).cuboid(-1.05F, 0.05F, -1.0F, 2.0F, 0.0F, 2.0F, new Dilation(0.0F))
                        .uv(64, 67).cuboid(-0.3F, -0.45F, -0.25F, 2.0F, 1.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(15.85F, -16.2F, -6.7F, 0.0F, 1.0908F, 0.0F));

        ModelPartData s_crank_2 = bone50.addChild("s_crank_2", ModelPartBuilder.create(),
                ModelTransform.pivot(0.0F, 2.0F, 0.0F));

        ModelPartData bone60 = s_crank_2.addChild("bone60",
                ModelPartBuilder.create().uv(0, 58).cuboid(-1.05F, 0.05F, -1.0F, 2.0F, 0.0F, 2.0F, new Dilation(0.0F))
                        .uv(64, 67).cuboid(-0.3F, -0.45F, -0.25F, 2.0F, 1.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.pivot(15.85F, -16.2F, -3.7F));

        ModelPartData ind_lamp_6 = bone50.addChild("ind_lamp_6",
                ModelPartBuilder.create().uv(56, 42).cuboid(12.2F, -16.15F, -4.7F, 2.0F, 0.0F, 2.0F, new Dilation(0.0F))
                        .uv(45, 25).cuboid(12.2F, -17.0F, -4.7F, 2.0F, 2.0F, 2.0F, new Dilation(-0.4F)),
                ModelTransform.pivot(0.0F, 2.0F, 0.0F));

        ModelPartData bone86 = ind_lamp_6.addChild("bone86", ModelPartBuilder.create().uv(11, 42).cuboid(-4.8F, -1.0F,
                -4.7F, 2.0F, 2.0F, 2.0F, new Dilation(-0.38F)), ModelTransform.pivot(17.0F, -16.0F, 0.0F));

        ModelPartData ind_lamp_7 = bone50.addChild("ind_lamp_7",
                ModelPartBuilder.create().uv(45, 25)
                        .cuboid(10.0F, -17.0F, -2.95F, 2.0F, 2.0F, 2.0F, new Dilation(-0.4F)).uv(56, 42)
                        .cuboid(10.0F, -16.15F, -2.95F, 2.0F, 0.0F, 2.0F, new Dilation(0.0F)),
                ModelTransform.pivot(0.0F, 2.0F, 0.0F));

        ModelPartData bone87 = ind_lamp_7.addChild("bone87", ModelPartBuilder.create().uv(11, 42).cuboid(-7.0F, -1.0F,
                -3.7F, 2.0F, 2.0F, 2.0F, new Dilation(-0.38F)), ModelTransform.pivot(17.0F, -16.0F, 0.75F));

        ModelPartData ind_lamp_8 = bone50.addChild("ind_lamp_8",
                ModelPartBuilder.create().uv(56, 42)
                        .cuboid(12.15F, -16.15F, -1.5F, 2.0F, 0.0F, 2.0F, new Dilation(0.0F)).uv(45, 25)
                        .cuboid(12.15F, -17.0F, -1.5F, 2.0F, 2.0F, 2.0F, new Dilation(-0.4F)),
                ModelTransform.pivot(0.0F, 2.0F, 0.0F));

        ModelPartData bone88 = ind_lamp_8.addChild("bone88", ModelPartBuilder.create().uv(11, 42).cuboid(-7.0F, -1.0F,
                -1.5F, 2.0F, 2.0F, 2.0F, new Dilation(-0.38F)), ModelTransform.pivot(19.15F, -16.0F, 0.0F));

        ModelPartData ind_lamp_9 = bone50.addChild("ind_lamp_9",
                ModelPartBuilder.create().uv(56, 42)
                        .cuboid(10.0F, -16.15F, -0.05F, 2.0F, 0.0F, 2.0F, new Dilation(0.0F)).uv(45, 25)
                        .cuboid(10.0F, -17.0F, -0.05F, 2.0F, 2.0F, 2.0F, new Dilation(-0.4F)),
                ModelTransform.pivot(0.0F, 2.0F, 0.0F));

        ModelPartData bone89 = ind_lamp_9.addChild("bone89",
                ModelPartBuilder.create().uv(11, 42).cuboid(-7.0F, -1.0F, 0.7F, 2.0F, 2.0F, 2.0F, new Dilation(-0.38F)),
                ModelTransform.pivot(17.0F, -16.0F, -0.75F));

        ModelPartData ind_lamp_10 = bone50.addChild("ind_lamp_10",
                ModelPartBuilder.create().uv(56, 42).cuboid(12.2F, -16.15F, 1.7F, 2.0F, 0.0F, 2.0F, new Dilation(0.0F))
                        .uv(45, 25).cuboid(12.2F, -17.0F, 1.7F, 2.0F, 2.0F, 2.0F, new Dilation(-0.4F)),
                ModelTransform.pivot(0.0F, 2.0F, 0.0F));

        ModelPartData bone90 = ind_lamp_10.addChild("bone90",
                ModelPartBuilder.create().uv(11, 42).cuboid(-4.8F, -1.0F, 1.7F, 2.0F, 2.0F, 2.0F, new Dilation(-0.38F)),
                ModelTransform.pivot(17.0F, -16.0F, 0.0F));

        ModelPartData misc_ctrl = bone50.addChild("misc_ctrl",
                ModelPartBuilder.create().uv(0, 13).cuboid(14.7F, -16.15F, -2.3F, 2.0F, 0.0F, 5.0F, new Dilation(0.0F)),
                ModelTransform.pivot(0.0F, 2.0F, 0.0F));

        ModelPartData bone62 = misc_ctrl.addChild("bone62",
                ModelPartBuilder.create().uv(0, 42).cuboid(-1.0F, 0.0F, -1.0F, 2.0F, 2.0F, 2.0F, new Dilation(-0.4F)),
                ModelTransform.of(15.7F, -17.0F, -1.3F, 0.0F, -0.7854F, 0.0F));

        ModelPartData bone63 = misc_ctrl.addChild("bone63",
                ModelPartBuilder.create().uv(16, 31).cuboid(0.0F, -0.85F, -0.5F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(15.25F, -16.15F, 0.5F, 0.0F, 0.0F, -0.7418F));

        ModelPartData bone65 = misc_ctrl.addChild("bone65",
                ModelPartBuilder.create().uv(16, 31).cuboid(0.0F, -0.85F, -0.5F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(16.25F, -16.15F, 0.5F, 0.0F, 0.0F, -0.7418F));

        ModelPartData bone64 = misc_ctrl.addChild("bone64",
                ModelPartBuilder.create().uv(16, 31).cuboid(0.0F, -0.85F, -0.5F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(15.25F, -16.15F, 1.5F, 0.0F, 0.0F, -0.7418F));

        ModelPartData bone80 = misc_ctrl.addChild("bone80",
                ModelPartBuilder.create().uv(16, 31).cuboid(0.0F, -0.85F, -0.5F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(15.25F, -16.15F, 2.5F, 0.0F, 0.0F, -0.7418F));

        ModelPartData bone66 = misc_ctrl.addChild("bone66",
                ModelPartBuilder.create().uv(16, 31).cuboid(0.0F, -0.85F, -0.5F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(16.25F, -16.15F, 1.5F, 0.0F, 0.0F, -0.7418F));

        ModelPartData bone81 = misc_ctrl.addChild("bone81",
                ModelPartBuilder.create().uv(16, 31).cuboid(0.0F, -0.85F, -0.5F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(16.25F, -16.15F, 2.5F, 0.0F, 0.0F, -0.7418F));

        ModelPartData sym_lamp = bone50.addChild("sym_lamp",
                ModelPartBuilder.create().uv(10, 5)
                        .cuboid(14.4121F, -16.85F, 3.2879F, 2.0F, 1.0F, 2.0F, new Dilation(-0.3F)).uv(0, 5)
                        .cuboid(14.4121F, -16.85F, 3.2879F, 2.0F, 1.0F, 2.0F, new Dilation(-0.1F)),
                ModelTransform.pivot(0.0F, 2.0F, 0.0F));

        ModelPartData bone97 = sym_lamp.addChild("bone97", ModelPartBuilder.create().uv(0, 79).cuboid(-1.2879F, -0.85F,
                -1.7121F, 2.0F, 1.0F, 2.0F, new Dilation(-0.28F)), ModelTransform.pivot(15.7F, -16.0F, 5.0F));

        ModelPartData p_3 = panels.addChild("p_3", ModelPartBuilder.create(),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 2.0944F, 0.0F));

        ModelPartData bone67 = p_3
                .addChild(
                        "bone67", ModelPartBuilder.create().uv(58, 68).cuboid(-11.25F, -0.95F, -10.0F, 13.0F, 0.0F,
                                20.0F, new Dilation(0.0F)),
                        ModelTransform.of(16.0F, -13.0F, 0.0F, 0.0F, 0.0F, 0.2618F));

        ModelPartData bone68 = bone67.addChild("bone68", ModelPartBuilder.create(),
                ModelTransform.pivot(-16.0F, 13.0F, 0.0F));

        ModelPartData bone69 = bone68.addChild("bone69",
                ModelPartBuilder.create().uv(73, 21).cuboid(6.7F, -14.15F, -2.0F, 2.0F, 0.0F, 4.0F, new Dilation(0.0F)),
                ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData s_lever_2 = bone69.addChild("s_lever_2", ModelPartBuilder.create(),
                ModelTransform.pivot(0.0F, 2.0F, 0.0F));

        ModelPartData cube_r36 = s_lever_2.addChild("cube_r36",
                ModelPartBuilder.create().uv(66, 63).cuboid(0.2F, -0.65F, -6.95F, 2.0F, 1.0F, 1.0F, new Dilation(0.1F)),
                ModelTransform.of(8.9F, -15.5F, 5.5F, 0.0F, 0.0F, 0.0F));

        ModelPartData bone70 = s_lever_2.addChild("bone70", ModelPartBuilder.create().uv(50, 39).cuboid(-1.0501F, 0.0F,
                -0.45F, 1.0F, 0.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(10.15F, -16.25F, -0.75F, 0.0F, 0.0F, 0.7418F));

        ModelPartData s_lever_3 = bone69.addChild("s_lever_3", ModelPartBuilder.create(),
                ModelTransform.pivot(0.0F, 2.0F, 0.0F));

        ModelPartData cube_r37 = s_lever_3.addChild("cube_r37",
                ModelPartBuilder.create().uv(66, 63).cuboid(0.2F, -0.65F, -6.95F, 2.0F, 1.0F, 1.0F, new Dilation(0.1F)),
                ModelTransform.of(11.25F, -15.5F, 5.5F, 0.0F, 0.0F, 0.0F));

        ModelPartData bone76 = s_lever_3.addChild("bone76", ModelPartBuilder.create().uv(11, 50).cuboid(-1.0501F, 0.0F,
                -0.45F, 1.0F, 0.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(12.5F, -16.25F, -0.75F, 0.0F, 0.0F, 0.7418F));

        ModelPartData s_lever_4 = bone69.addChild("s_lever_4", ModelPartBuilder.create(),
                ModelTransform.pivot(0.0F, 2.0F, 0.0F));

        ModelPartData cube_r38 = s_lever_4.addChild("cube_r38",
                ModelPartBuilder.create().uv(66, 63).cuboid(0.2F, -0.65F, -6.95F, 2.0F, 1.0F, 1.0F, new Dilation(0.1F)),
                ModelTransform.of(13.55F, -15.5F, 5.5F, 0.0F, 0.0F, 0.0F));

        ModelPartData bone77 = s_lever_4.addChild("bone77", ModelPartBuilder.create().uv(50, 39).cuboid(-1.0501F, 0.0F,
                -0.45F, 1.0F, 0.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(14.8F, -16.25F, -0.75F, 0.0F, 0.0F, 0.7418F));

        ModelPartData s_lever_5 = bone69.addChild("s_lever_5", ModelPartBuilder.create(),
                ModelTransform.pivot(0.0F, 2.0F, 0.0F));

        ModelPartData cube_r39 = s_lever_5.addChild("cube_r39",
                ModelPartBuilder.create().uv(66, 63).cuboid(0.2F, -0.65F, -6.95F, 2.0F, 1.0F, 1.0F, new Dilation(0.1F)),
                ModelTransform.of(13.55F, -15.5F, 7.0F, 0.0F, 0.0F, 0.0F));

        ModelPartData bone71 = s_lever_5.addChild("bone71", ModelPartBuilder.create().uv(11, 50).cuboid(-1.0501F, 0.0F,
                -0.45F, 1.0F, 0.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(14.8F, -16.25F, 0.75F, 0.0F, 0.0F, 0.7418F));

        ModelPartData sl_switch_9 = bone69.addChild("sl_switch_9", ModelPartBuilder.create(),
                ModelTransform.pivot(0.0F, 2.0F, 0.0F));

        ModelPartData cube_r40 = sl_switch_9.addChild("cube_r40",
                ModelPartBuilder.create().uv(9, 58).cuboid(-0.8F, -0.65F, 1.05F, 2.0F, 1.0F, 1.0F, new Dilation(0.1F)),
                ModelTransform.of(14.9F, -15.5F, 3.0F, 0.0F, 0.0F, 0.0F));

        ModelPartData bone72 = sl_switch_9.addChild("bone72", ModelPartBuilder.create(),
                ModelTransform.pivot(14.9F, -15.3F, 3.0F));

        ModelPartData cube_r41 = bone72.addChild("cube_r41",
                ModelPartBuilder.create().uv(14, 18).cuboid(-0.8F, -1.0F, 1.05F, 1.0F, 0.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F));

        ModelPartData sl_switch_10 = bone69.addChild("sl_switch_10", ModelPartBuilder.create(),
                ModelTransform.pivot(0.0F, 2.0F, 0.0F));

        ModelPartData cube_r42 = sl_switch_10.addChild("cube_r42",
                ModelPartBuilder.create().uv(39, 68).cuboid(-0.8F, -0.65F, 1.05F, 2.0F, 1.0F, 1.0F, new Dilation(0.1F)),
                ModelTransform.of(14.9F, -15.5F, 4.5F, 0.0F, 0.0F, 0.0F));

        ModelPartData bone73 = sl_switch_10.addChild("bone73", ModelPartBuilder.create(),
                ModelTransform.pivot(14.9F, -15.3F, 4.5F));

        ModelPartData cube_r43 = bone73.addChild("cube_r43",
                ModelPartBuilder.create().uv(14, 18).cuboid(-0.8F, -1.0F, 1.05F, 1.0F, 0.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F));

        ModelPartData s_crank_3 = bone69.addChild("s_crank_3", ModelPartBuilder.create(),
                ModelTransform.pivot(0.0F, 2.0F, 0.0F));

        ModelPartData bone74 = s_crank_3.addChild("bone74",
                ModelPartBuilder.create().uv(0, 58).cuboid(-1.05F, 0.05F, -1.0F, 2.0F, 0.0F, 2.0F, new Dilation(0.0F))
                        .uv(64, 67).cuboid(-0.3F, -0.45F, -0.25F, 2.0F, 1.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(15.6F, -16.2F, -6.2F, 0.0F, -1.4399F, 0.0F));

        ModelPartData s_crank_4 = bone69.addChild("s_crank_4", ModelPartBuilder.create(),
                ModelTransform.pivot(0.0F, 2.0F, 0.0F));

        ModelPartData bone75 = s_crank_4.addChild("bone75",
                ModelPartBuilder.create().uv(0, 58).cuboid(-1.05F, 0.05F, -1.0F, 2.0F, 0.0F, 2.0F, new Dilation(0.0F))
                        .uv(64, 67).cuboid(-0.3F, -0.45F, -0.25F, 2.0F, 1.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(12.85F, -16.2F, -5.2F, 0.0F, -0.6981F, 0.0F));

        ModelPartData s_crank_5 = bone69.addChild("s_crank_5", ModelPartBuilder.create(),
                ModelTransform.pivot(0.0F, 2.0F, 0.0F));

        ModelPartData bone78 = s_crank_5.addChild("bone78",
                ModelPartBuilder.create().uv(0, 58).cuboid(-1.05F, 0.05F, -1.0F, 2.0F, 0.0F, 2.0F, new Dilation(0.0F))
                        .uv(64, 67).cuboid(-0.3F, -0.45F, -0.25F, 2.0F, 1.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(10.35F, -16.2F, 1.3F, 0.0F, -0.4363F, 0.0F));

        ModelPartData ind_lamp_11 = bone69.addChild("ind_lamp_11",
                ModelPartBuilder.create().uv(56, 42).cuboid(9.2F, -16.15F, -3.7F, 2.0F, 0.0F, 2.0F, new Dilation(0.0F))
                        .uv(45, 25).cuboid(9.2F, -17.0F, -3.7F, 2.0F, 2.0F, 2.0F, new Dilation(-0.4F)),
                ModelTransform.pivot(0.0F, 2.0F, 0.0F));

        ModelPartData bone82 = ind_lamp_11.addChild("bone82", ModelPartBuilder.create().uv(11, 42).cuboid(-4.8F, -1.0F,
                -4.7F, 2.0F, 2.0F, 2.0F, new Dilation(-0.38F)), ModelTransform.pivot(14.0F, -16.0F, 1.0F));

        ModelPartData ind_lamp_12 = bone69.addChild("ind_lamp_12",
                ModelPartBuilder.create().uv(56, 42)
                        .cuboid(11.45F, -16.15F, -3.7F, 2.0F, 0.0F, 2.0F, new Dilation(0.0F)).uv(45, 25)
                        .cuboid(11.45F, -17.0F, -3.7F, 2.0F, 2.0F, 2.0F, new Dilation(-0.4F)),
                ModelTransform.pivot(0.0F, 2.0F, 0.0F));

        ModelPartData bone83 = ind_lamp_12.addChild("bone83", ModelPartBuilder.create().uv(11, 42).cuboid(-4.8F, -1.0F,
                -4.7F, 2.0F, 2.0F, 2.0F, new Dilation(-0.38F)), ModelTransform.pivot(16.25F, -16.0F, 1.0F));

        ModelPartData ind_lamp_13 = bone69.addChild("ind_lamp_13",
                ModelPartBuilder.create().uv(56, 42).cuboid(13.7F, -16.15F, -3.7F, 2.0F, 0.0F, 2.0F, new Dilation(0.0F))
                        .uv(45, 25).cuboid(13.7F, -17.0F, -3.7F, 2.0F, 2.0F, 2.0F, new Dilation(-0.4F)),
                ModelTransform.pivot(0.0F, 2.0F, 0.0F));

        ModelPartData bone84 = ind_lamp_13.addChild("bone84", ModelPartBuilder.create().uv(11, 42).cuboid(-4.8F, -1.0F,
                -4.7F, 2.0F, 2.0F, 2.0F, new Dilation(-0.38F)), ModelTransform.pivot(18.5F, -16.0F, 1.0F));

        ModelPartData ind_lamp_14 = bone69.addChild("ind_lamp_14",
                ModelPartBuilder.create().uv(56, 42)
                        .cuboid(12.45F, -16.15F, 1.55F, 2.0F, 0.0F, 2.0F, new Dilation(0.0F)).uv(45, 25)
                        .cuboid(12.45F, -17.0F, 1.55F, 2.0F, 2.0F, 2.0F, new Dilation(-0.4F)),
                ModelTransform.pivot(0.0F, 2.0F, 0.0F));

        ModelPartData bone85 = ind_lamp_14.addChild("bone85", ModelPartBuilder.create().uv(11, 42).cuboid(-4.8F, -1.0F,
                -4.7F, 2.0F, 2.0F, 2.0F, new Dilation(-0.38F)), ModelTransform.pivot(17.25F, -16.0F, 6.25F));

        ModelPartData sym_lamp2 = bone69.addChild("sym_lamp2",
                ModelPartBuilder.create().uv(10, 5)
                        .cuboid(9.4121F, -16.85F, 2.8879F, 2.0F, 1.0F, 2.0F, new Dilation(-0.3F)).uv(0, 5)
                        .cuboid(9.4121F, -16.85F, 2.8879F, 2.0F, 1.0F, 2.0F, new Dilation(-0.1F)),
                ModelTransform.pivot(0.0F, 2.0F, 0.0F));

        ModelPartData bone96 = sym_lamp2.addChild("bone96", ModelPartBuilder.create().uv(0, 79).cuboid(-1.2879F, -0.85F,
                -1.7121F, 2.0F, 1.0F, 2.0F, new Dilation(-0.28F)), ModelTransform.pivot(10.7F, -16.0F, 4.6F));

        ModelPartData ctrl_switch = bone69.addChild("ctrl_switch", ModelPartBuilder.create().uv(47, 8).cuboid(14.95F,
                -16.15F, 1.55F, 2.0F, 0.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 2.0F, 0.0F));

        ModelPartData bone79 = ctrl_switch.addChild("bone79", ModelPartBuilder.create().uv(11, 31).cuboid(-1.0501F,
                0.0F, -0.45F, 1.0F, 0.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(16.05F, -16.15F, 2.75F, 0.0F, 0.0F, 0.7418F));

        ModelPartData p_4 = panels.addChild("p_4", ModelPartBuilder.create(),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 3.1416F, 0.0F));

        ModelPartData bone98 = p_4
                .addChild(
                        "bone98", ModelPartBuilder.create().uv(58, 89).cuboid(-11.25F, -0.95F, -10.0F, 13.0F, 0.0F,
                                20.0F, new Dilation(0.0F)),
                        ModelTransform.of(16.0F, -13.0F, 0.0F, 0.0F, 0.0F, 0.2618F));

        ModelPartData bone99 = bone98.addChild("bone99", ModelPartBuilder.create(),
                ModelTransform.pivot(-16.0F, 13.0F, 0.0F));

        ModelPartData bone100 = bone99.addChild("bone100",
                ModelPartBuilder.create().uv(73, 26).cuboid(6.7F, -14.15F, -2.0F, 2.0F, 0.0F, 4.0F, new Dilation(0.0F)),
                ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData ind_lamp_16 = bone100.addChild("ind_lamp_16",
                ModelPartBuilder.create().uv(56, 42)
                        .cuboid(12.95F, -16.15F, -4.2F, 2.0F, 0.0F, 2.0F, new Dilation(0.0F)).uv(45, 25)
                        .cuboid(12.95F, -17.0F, -4.2F, 2.0F, 2.0F, 2.0F, new Dilation(-0.4F)),
                ModelTransform.pivot(0.0F, 2.0F, 0.0F));

        ModelPartData bone111 = ind_lamp_16.addChild("bone111", ModelPartBuilder.create().uv(11, 42).cuboid(-4.8F,
                -1.0F, -4.7F, 2.0F, 2.0F, 2.0F, new Dilation(-0.38F)), ModelTransform.pivot(17.75F, -16.0F, 0.5F));

        ModelPartData ind_lamp_15 = bone100.addChild("ind_lamp_15",
                ModelPartBuilder.create().uv(56, 42)
                        .cuboid(12.95F, -16.15F, -1.95F, 2.0F, 0.0F, 2.0F, new Dilation(0.0F)).uv(45, 25)
                        .cuboid(12.95F, -17.0F, -1.95F, 2.0F, 2.0F, 2.0F, new Dilation(-0.4F)),
                ModelTransform.pivot(0.0F, 2.0F, 0.0F));

        ModelPartData bone101 = ind_lamp_15.addChild("bone101", ModelPartBuilder.create().uv(11, 42).cuboid(-4.8F,
                -1.0F, -4.7F, 2.0F, 2.0F, 2.0F, new Dilation(-0.38F)), ModelTransform.pivot(17.75F, -16.0F, 2.75F));

        ModelPartData ind_lamp_17 = bone100.addChild("ind_lamp_17",
                ModelPartBuilder.create().uv(56, 42).cuboid(12.95F, -16.15F, 0.3F, 2.0F, 0.0F, 2.0F, new Dilation(0.0F))
                        .uv(45, 25).cuboid(12.95F, -17.0F, 0.3F, 2.0F, 2.0F, 2.0F, new Dilation(-0.4F)),
                ModelTransform.pivot(0.0F, 2.0F, 0.0F));

        ModelPartData bone102 = ind_lamp_17.addChild("bone102", ModelPartBuilder.create().uv(11, 42).cuboid(-4.8F,
                -1.0F, -4.7F, 2.0F, 2.0F, 2.0F, new Dilation(-0.38F)), ModelTransform.pivot(17.75F, -16.0F, 5.0F));

        ModelPartData ind_lamp_18 = bone100.addChild("ind_lamp_18",
                ModelPartBuilder.create().uv(56, 42)
                        .cuboid(12.95F, -16.15F, 2.55F, 2.0F, 0.0F, 2.0F, new Dilation(0.0F)).uv(45, 25)
                        .cuboid(12.95F, -17.0F, 2.55F, 2.0F, 2.0F, 2.0F, new Dilation(-0.4F)),
                ModelTransform.pivot(0.0F, 2.0F, 0.0F));

        ModelPartData bone103 = ind_lamp_18.addChild("bone103", ModelPartBuilder.create().uv(11, 42).cuboid(-4.8F,
                -1.0F, -4.7F, 2.0F, 2.0F, 2.0F, new Dilation(-0.38F)), ModelTransform.pivot(17.75F, -16.0F, 7.25F));

        ModelPartData ctrl_panel_2 = bone100.addChild("ctrl_panel_2", ModelPartBuilder.create().uv(0, 79).cuboid(15.2F,
                -16.15F, -5.95F, 2.0F, 0.0F, 9.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 2.0F, 0.0F));

        ModelPartData bone115 = ctrl_panel_2.addChild("bone115", ModelPartBuilder.create().uv(11, 31).cuboid(-1.0501F,
                0.0F, -0.45F, 1.0F, 0.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(16.3F, -16.15F, -3.25F, 0.0F, 0.0F, 0.7418F));

        ModelPartData bone104 = ctrl_panel_2.addChild("bone104", ModelPartBuilder.create().uv(11, 31).cuboid(-1.0501F,
                0.0F, -0.45F, 1.0F, 0.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(16.3F, -16.15F, -2.25F, 0.0F, 0.0F, 0.7418F));

        ModelPartData bone105 = ctrl_panel_2.addChild("bone105", ModelPartBuilder.create().uv(11, 31).cuboid(-1.0501F,
                0.0F, -0.45F, 1.0F, 0.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(16.3F, -16.15F, -0.75F, 0.0F, 0.0F, 0.7418F));

        ModelPartData bone106 = ctrl_panel_2.addChild("bone106",
                ModelPartBuilder.create().uv(0, 83).cuboid(-0.5F, -0.9F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.2F))
                        .uv(0, 86).cuboid(-1.0F, -0.9F, 0.0F, 2.0F, 1.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.pivot(16.2F, -16.0F, -5.0F));

        ModelPartData bone107 = ctrl_panel_2.addChild("bone107",
                ModelPartBuilder.create().uv(0, 83).cuboid(-0.5F, -0.9F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.2F))
                        .uv(0, 86).cuboid(-1.0F, -0.9F, 0.0F, 2.0F, 1.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.pivot(16.2F, -16.0F, 2.0F));

        ModelPartData bone108 = ctrl_panel_2.addChild("bone108",
                ModelPartBuilder.create().uv(0, 83).cuboid(-0.5F, -0.9F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.2F))
                        .uv(0, 86).cuboid(-1.0F, -0.9F, 0.0F, 2.0F, 1.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.pivot(16.2F, -16.0F, 0.5F));

        ModelPartData m_sensor_1 = bone100.addChild("m_sensor_1",
                ModelPartBuilder.create().uv(0, 0).cuboid(10.5F, -16.5F, -2.15F, 2.0F, 1.0F, 2.0F, new Dilation(0.0F))
                        .uv(10, 0).cuboid(10.5F, -16.5F, -2.15F, 2.0F, 1.0F, 2.0F, new Dilation(0.1F)),
                ModelTransform.pivot(0.0F, 2.0F, 0.0F));

        ModelPartData m_sensor_2 = bone100.addChild("m_sensor_2",
                ModelPartBuilder.create().uv(0, 0).cuboid(10.5F, -16.5F, 0.15F, 2.0F, 1.0F, 2.0F, new Dilation(0.0F))
                        .uv(10, 0).cuboid(10.5F, -16.5F, 0.15F, 2.0F, 1.0F, 2.0F, new Dilation(0.1F)),
                ModelTransform.pivot(0.0F, 2.0F, 0.0F));

        ModelPartData m_meter_1 = bone100.addChild("m_meter_1",
                ModelPartBuilder.create().uv(78, 13).cuboid(-1.5F, -1.0F, -1.5F, 3.0F, 2.0F, 3.0F, new Dilation(-0.4F))
                        .uv(88, 31).cuboid(-1.5F, -1.0F, -1.5F, 3.0F, 2.0F, 3.0F, new Dilation(-0.3F)).uv(88, 37)
                        .cuboid(-1.5F, -1.0F, -1.5F, 3.0F, 2.0F, 3.0F, new Dilation(-0.2F)).uv(88, 37)
                        .cuboid(-1.5F, -0.25F, -1.5F, 3.0F, 0.0F, 3.0F, new Dilation(0.0F)),
                ModelTransform.of(11.0F, -13.75F, 4.25F, 0.0F, -0.7854F, 0.0F));

        ModelPartData bone109 = m_meter_1.addChild("bone109", ModelPartBuilder.create(),
                ModelTransform.pivot(0.5F, -0.6F, -0.5F));

        ModelPartData cube_r44 = bone109.addChild("cube_r44",
                ModelPartBuilder.create().uv(78, 0).cuboid(-1.0F, -2.05F, -0.75F, 2.0F, 0.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(-0.5F, 2.0F, 0.5F, 0.0F, 0.7854F, 0.0F));

        ModelPartData m_meter_2 = bone100.addChild("m_meter_2",
                ModelPartBuilder.create().uv(78, 13).cuboid(-1.5F, -1.0F, -1.5F, 3.0F, 2.0F, 3.0F, new Dilation(-0.4F))
                        .uv(88, 31).cuboid(-1.5F, -1.0F, -1.5F, 3.0F, 2.0F, 3.0F, new Dilation(-0.3F)).uv(88, 37)
                        .cuboid(-1.5F, -1.0F, -1.5F, 3.0F, 2.0F, 3.0F, new Dilation(-0.2F)).uv(88, 37)
                        .cuboid(-1.5F, -0.25F, -1.5F, 3.0F, 0.0F, 3.0F, new Dilation(0.0F)),
                ModelTransform.of(11.0F, -13.75F, -4.25F, 0.0F, -0.7854F, 0.0F));

        ModelPartData bone110 = m_meter_2.addChild("bone110", ModelPartBuilder.create(),
                ModelTransform.pivot(0.5F, -0.6F, -0.5F));

        ModelPartData cube_r45 = bone110.addChild("cube_r45",
                ModelPartBuilder.create().uv(78, 0).cuboid(-1.0F, -2.05F, -0.75F, 2.0F, 0.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(-0.5F, 2.0F, 0.5F, 0.0F, 0.7854F, 0.0F));

        ModelPartData s_knob = bone100.addChild("s_knob", ModelPartBuilder.create(),
                ModelTransform.pivot(0.0F, 2.0F, 0.0F));

        ModelPartData bone129 = s_knob.addChild("bone129",
                ModelPartBuilder.create().uv(0, 42).cuboid(-1.0F, 0.0F, -1.0F, 2.0F, 2.0F, 2.0F, new Dilation(-0.4F)),
                ModelTransform.of(15.5F, -16.75F, 6.25F, 0.0F, -0.7854F, 0.0F));

        ModelPartData p_5 = panels.addChild("p_5", ModelPartBuilder.create(),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -2.0944F, 0.0F));

        ModelPartData bone112 = p_5
                .addChild(
                        "bone112", ModelPartBuilder.create().uv(58, 110).cuboid(-11.25F, -0.95F, -10.0F, 13.0F, 0.0F,
                                20.0F, new Dilation(0.0F)),
                        ModelTransform.of(16.0F, -13.0F, 0.0F, 0.0F, 0.0F, 0.2618F));

        ModelPartData bone113 = bone112.addChild("bone113", ModelPartBuilder.create(),
                ModelTransform.pivot(-16.0F, 13.0F, 0.0F));

        ModelPartData bone114 = bone113.addChild("bone114",
                ModelPartBuilder.create().uv(73, 21).cuboid(6.7F, -14.15F, -2.0F, 2.0F, 0.0F, 4.0F, new Dilation(0.0F)),
                ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData ind_lamp_19 = bone114.addChild("ind_lamp_19",
                ModelPartBuilder.create().uv(56, 42).cuboid(6.7F, -16.15F, 2.1F, 2.0F, 0.0F, 2.0F, new Dilation(0.0F))
                        .uv(45, 25).cuboid(6.7F, -17.0F, 2.1F, 2.0F, 2.0F, 2.0F, new Dilation(-0.4F)),
                ModelTransform.pivot(0.0F, 2.0F, 0.0F));

        ModelPartData bone116 = ind_lamp_19.addChild("bone116", ModelPartBuilder.create().uv(11, 42).cuboid(-4.8F,
                -1.0F, -4.7F, 2.0F, 2.0F, 2.0F, new Dilation(-0.38F)), ModelTransform.pivot(11.5F, -16.0F, 6.8F));

        ModelPartData ind_lamp_20 = bone114.addChild("ind_lamp_20", ModelPartBuilder.create().uv(45, 25).cuboid(13.4F,
                -17.0F, -1.95F, 2.0F, 2.0F, 2.0F, new Dilation(-0.4F)), ModelTransform.pivot(0.0F, 2.0F, 0.0F));

        ModelPartData bone117 = ind_lamp_20.addChild("bone117", ModelPartBuilder.create().uv(11, 42).cuboid(-4.8F,
                -1.0F, -4.7F, 2.0F, 2.0F, 2.0F, new Dilation(-0.38F)), ModelTransform.pivot(18.2F, -16.0F, 2.75F));

        ModelPartData ind_lamp_21 = bone114.addChild("ind_lamp_21", ModelPartBuilder.create().uv(45, 25).cuboid(13.45F,
                -17.0F, 5.0F, 2.0F, 2.0F, 2.0F, new Dilation(-0.4F)), ModelTransform.pivot(0.0F, 2.0F, 0.0F));

        ModelPartData bone118 = ind_lamp_21.addChild("bone118", ModelPartBuilder.create().uv(11, 42).cuboid(-5.3F,
                -1.0F, -4.7F, 2.0F, 2.0F, 2.0F, new Dilation(-0.38F)), ModelTransform.pivot(18.75F, -16.0F, 9.7F));

        ModelPartData ctrl_panel_3 = bone114.addChild("ctrl_panel_3", ModelPartBuilder.create().uv(0, 79).cuboid(13.45F,
                -16.15F, -1.95F, 2.0F, 0.0F, 9.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 2.0F, 0.0F));

        ModelPartData bone120 = ctrl_panel_3.addChild("bone120", ModelPartBuilder.create().uv(11, 31).cuboid(-1.0501F,
                0.0F, -0.45F, 1.0F, 0.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(14.55F, -16.15F, 1.65F, 0.0F, 0.0F, 0.7418F));

        ModelPartData bone121 = ctrl_panel_3.addChild("bone121", ModelPartBuilder.create().uv(11, 31).cuboid(-1.0501F,
                0.0F, -0.45F, 1.0F, 0.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(14.55F, -16.15F, 2.65F, 0.0F, 0.0F, 0.7418F));

        ModelPartData bone119 = ctrl_panel_3.addChild("bone119", ModelPartBuilder.create().uv(11, 31).cuboid(-1.0501F,
                0.0F, -0.45F, 1.0F, 0.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(14.55F, -16.15F, 3.65F, 0.0F, 0.0F, 0.7418F));

        ModelPartData bone123 = ctrl_panel_3.addChild("bone123",
                ModelPartBuilder.create().uv(0, 83).cuboid(-0.5F, -0.9F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.2F))
                        .uv(0, 86).cuboid(-1.0F, -0.9F, 0.0F, 2.0F, 1.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.pivot(14.45F, -16.0F, 0.4F));

        ModelPartData bone125 = ctrl_panel_3.addChild("bone125",
                ModelPartBuilder.create().uv(0, 83).cuboid(-0.5F, -0.9F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.2F))
                        .uv(0, 86).cuboid(-1.0F, -0.9F, 0.0F, 2.0F, 1.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.pivot(14.45F, -16.0F, 4.25F));

        ModelPartData m_meter_3 = bone114.addChild("m_meter_3",
                ModelPartBuilder.create().uv(78, 13).cuboid(-1.5F, -1.0F, -1.5F, 3.0F, 2.0F, 3.0F, new Dilation(-0.4F))
                        .uv(88, 31).cuboid(-1.5F, -1.0F, -1.5F, 3.0F, 2.0F, 3.0F, new Dilation(-0.3F)).uv(88, 37)
                        .cuboid(-1.5F, -1.0F, -1.5F, 3.0F, 2.0F, 3.0F, new Dilation(-0.2F)).uv(88, 37)
                        .cuboid(-1.5F, -0.25F, -1.5F, 3.0F, 0.0F, 3.0F, new Dilation(0.0F)),
                ModelTransform.of(11.0F, -13.75F, 4.25F, 0.0F, -0.7854F, 0.0F));

        ModelPartData bone126 = m_meter_3.addChild("bone126", ModelPartBuilder.create(),
                ModelTransform.pivot(0.5F, -0.6F, -0.5F));

        ModelPartData cube_r46 = bone126.addChild("cube_r46",
                ModelPartBuilder.create().uv(78, 0).cuboid(-1.0F, -2.05F, -0.75F, 2.0F, 0.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(-0.5F, 2.0F, 0.5F, 0.0F, 0.7854F, 0.0F));

        ModelPartData m_meter_4 = bone114.addChild("m_meter_4",
                ModelPartBuilder.create().uv(78, 13).cuboid(-1.5F, -1.0F, -1.5F, 3.0F, 2.0F, 3.0F, new Dilation(-0.4F))
                        .uv(88, 31).cuboid(-1.5F, -1.0F, -1.5F, 3.0F, 2.0F, 3.0F, new Dilation(-0.3F)).uv(88, 37)
                        .cuboid(-1.5F, -1.0F, -1.5F, 3.0F, 2.0F, 3.0F, new Dilation(-0.2F)).uv(88, 37)
                        .cuboid(-1.5F, -0.25F, -1.5F, 3.0F, 0.0F, 3.0F, new Dilation(0.0F)),
                ModelTransform.of(11.0F, -13.75F, 0.25F, 0.0F, -0.7854F, 0.0F));

        ModelPartData bone127 = m_meter_4.addChild("bone127", ModelPartBuilder.create(),
                ModelTransform.pivot(0.5F, -0.6F, -0.5F));

        ModelPartData cube_r47 = bone127.addChild("cube_r47",
                ModelPartBuilder.create().uv(78, 0).cuboid(-1.0F, -2.05F, -0.75F, 2.0F, 0.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(-0.5F, 2.0F, 0.5F, 0.0F, 0.7854F, 0.0F));

        ModelPartData sym_lamp3 = bone114.addChild("sym_lamp3",
                ModelPartBuilder.create().uv(10, 5)
                        .cuboid(13.4121F, -16.85F, -7.1121F, 2.0F, 1.0F, 2.0F, new Dilation(-0.3F)).uv(0, 5)
                        .cuboid(13.4121F, -16.85F, -7.1121F, 2.0F, 1.0F, 2.0F, new Dilation(-0.1F)),
                ModelTransform.pivot(0.0F, 2.0F, 0.0F));

        ModelPartData bone122 = sym_lamp3.addChild("bone122", ModelPartBuilder.create().uv(0, 79).cuboid(-1.2879F,
                -0.85F, -1.7121F, 2.0F, 1.0F, 2.0F, new Dilation(-0.28F)), ModelTransform.pivot(14.7F, -16.0F, -5.4F));

        ModelPartData sl_switch_8 = bone114.addChild("sl_switch_8", ModelPartBuilder.create(),
                ModelTransform.pivot(0.0F, 2.0F, 0.0F));

        ModelPartData cube_r48 = sl_switch_8.addChild("cube_r48",
                ModelPartBuilder.create().uv(9, 58).cuboid(-0.8F, -0.65F, 1.05F, 2.0F, 1.0F, 1.0F, new Dilation(0.1F)),
                ModelTransform.of(14.9F, -15.5F, -5.75F, 0.0F, 0.0F, 0.0F));

        ModelPartData bone124 = sl_switch_8.addChild("bone124", ModelPartBuilder.create(),
                ModelTransform.pivot(14.9F, -15.3F, -5.75F));

        ModelPartData cube_r49 = bone124.addChild("cube_r49",
                ModelPartBuilder.create().uv(14, 18).cuboid(-0.8F, -1.0F, 1.05F, 1.0F, 0.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F));

        ModelPartData sl_switch_11 = bone114.addChild("sl_switch_11", ModelPartBuilder.create(),
                ModelTransform.pivot(0.0F, 2.0F, 0.0F));

        ModelPartData cube_r50 = sl_switch_11.addChild("cube_r50",
                ModelPartBuilder.create().uv(9, 58).cuboid(-0.8F, -0.65F, 1.05F, 2.0F, 1.0F, 1.0F, new Dilation(0.1F)),
                ModelTransform.of(14.9F, -15.5F, -4.25F, 0.0F, 0.0F, 0.0F));

        ModelPartData bone128 = sl_switch_11.addChild("bone128", ModelPartBuilder.create(),
                ModelTransform.pivot(14.9F, -15.5F, -4.25F));

        ModelPartData cube_r51 = bone128.addChild("cube_r51",
                ModelPartBuilder.create().uv(14, 18).cuboid(-0.8F, -0.8F, 1.05F, 1.0F, 0.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F));

        ModelPartData bone130 = bone114.addChild("bone130",
                ModelPartBuilder.create().uv(0, 89).cuboid(-2.0F, -1.0F, -1.0F, 3.0F, 1.0F, 2.0F, new Dilation(0.2F))
                        .uv(0, 93).cuboid(-2.0F, -1.0F, -1.0F, 3.0F, 1.0F, 2.0F, new Dilation(0.1F)),
                ModelTransform.of(11.25F, -13.5F, -3.5F, 0.0F, 0.0F, -0.2618F));

        ModelPartData bone131 = bone130.addChild("bone131",
                ModelPartBuilder.create().uv(0, 96).cuboid(0.95F, -2.0F, -0.75F, 0.0F, 2.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.pivot(0.2F, 0.5F, 0.0F));

        ModelPartData p_6 = panels.addChild("p_6", ModelPartBuilder.create(),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone132 = p_6
                .addChild(
                        "bone132", ModelPartBuilder.create().uv(58, 131).cuboid(-11.25F, -0.95F, -10.0F, 13.0F, 0.0F,
                                20.0F, new Dilation(0.0F)),
                        ModelTransform.of(16.0F, -13.0F, 0.0F, 0.0F, 0.0F, 0.2618F));

        ModelPartData bone133 = bone132.addChild("bone133", ModelPartBuilder.create(),
                ModelTransform.pivot(-16.0F, 13.0F, 0.0F));

        ModelPartData bone134 = bone133.addChild("bone134",
                ModelPartBuilder.create().uv(54, 35).cuboid(6.7F, -14.15F, -2.0F, 2.0F, 0.0F, 4.0F, new Dilation(0.0F)),
                ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData ind_lamp_22 = bone134.addChild("ind_lamp_22",
                ModelPartBuilder.create().uv(56, 42).cuboid(8.7F, -16.15F, 2.25F, 2.0F, 0.0F, 2.0F, new Dilation(0.0F))
                        .uv(45, 25).cuboid(8.7F, -17.0F, 2.25F, 2.0F, 2.0F, 2.0F, new Dilation(-0.4F)),
                ModelTransform.pivot(0.0F, 2.0F, 0.0F));

        ModelPartData bone135 = ind_lamp_22.addChild("bone135", ModelPartBuilder.create().uv(11, 42).cuboid(-4.8F,
                -1.0F, -4.7F, 2.0F, 2.0F, 2.0F, new Dilation(-0.38F)), ModelTransform.pivot(13.5F, -16.0F, 6.95F));

        ModelPartData ind_lamp_25 = bone134.addChild("ind_lamp_25",
                ModelPartBuilder.create().uv(56, 42).cuboid(8.7F, -16.15F, -4.25F, 2.0F, 0.0F, 2.0F, new Dilation(0.0F))
                        .uv(45, 25).cuboid(8.7F, -17.0F, -4.25F, 2.0F, 2.0F, 2.0F, new Dilation(-0.4F)),
                ModelTransform.pivot(0.0F, 2.0F, 0.0F));

        ModelPartData bone138 = ind_lamp_25.addChild("bone138", ModelPartBuilder.create().uv(11, 42).cuboid(-4.8F,
                -1.0F, -4.7F, 2.0F, 2.0F, 2.0F, new Dilation(-0.38F)), ModelTransform.pivot(13.5F, -16.0F, 0.45F));

        ModelPartData ind_lamp_23 = bone134.addChild("ind_lamp_23",
                ModelPartBuilder.create().uv(56, 42)
                        .cuboid(11.45F, -16.15F, 3.75F, 2.0F, 0.0F, 2.0F, new Dilation(0.0F)).uv(45, 25)
                        .cuboid(11.45F, -17.0F, 3.75F, 2.0F, 2.0F, 2.0F, new Dilation(-0.4F)),
                ModelTransform.pivot(0.0F, 2.0F, 0.0F));

        ModelPartData bone136 = ind_lamp_23.addChild("bone136", ModelPartBuilder.create().uv(11, 42).cuboid(-4.8F,
                -1.0F, -4.7F, 2.0F, 2.0F, 2.0F, new Dilation(-0.38F)), ModelTransform.pivot(16.25F, -16.0F, 8.45F));

        ModelPartData ind_lamp_26 = bone134.addChild("ind_lamp_26",
                ModelPartBuilder.create().uv(56, 42)
                        .cuboid(11.45F, -16.15F, -5.75F, 2.0F, 0.0F, 2.0F, new Dilation(0.0F)).uv(45, 25)
                        .cuboid(11.45F, -17.0F, -5.75F, 2.0F, 2.0F, 2.0F, new Dilation(-0.4F)),
                ModelTransform.pivot(0.0F, 2.0F, 0.0F));

        ModelPartData bone139 = ind_lamp_26.addChild("bone139", ModelPartBuilder.create().uv(11, 42).cuboid(-4.8F,
                -1.0F, -4.7F, 2.0F, 2.0F, 2.0F, new Dilation(-0.38F)), ModelTransform.pivot(16.25F, -16.0F, -1.05F));

        ModelPartData ind_lamp_24 = bone134.addChild("ind_lamp_24",
                ModelPartBuilder.create().uv(56, 42).cuboid(14.2F, -16.15F, 5.25F, 2.0F, 0.0F, 2.0F, new Dilation(0.0F))
                        .uv(45, 25).cuboid(14.2F, -17.0F, 5.25F, 2.0F, 2.0F, 2.0F, new Dilation(-0.4F)),
                ModelTransform.pivot(0.0F, 2.0F, 0.0F));

        ModelPartData bone137 = ind_lamp_24.addChild("bone137", ModelPartBuilder.create().uv(11, 42).cuboid(-4.8F,
                -1.0F, -4.7F, 2.0F, 2.0F, 2.0F, new Dilation(-0.38F)), ModelTransform.pivot(19.0F, -16.0F, 9.95F));

        ModelPartData ind_lamp_27 = bone134.addChild("ind_lamp_27",
                ModelPartBuilder.create().uv(56, 42)
                        .cuboid(14.2F, -16.15F, -7.25F, 2.0F, 0.0F, 2.0F, new Dilation(0.0F)).uv(45, 25)
                        .cuboid(14.2F, -17.0F, -7.25F, 2.0F, 2.0F, 2.0F, new Dilation(-0.4F)),
                ModelTransform.pivot(0.0F, 2.0F, 0.0F));

        ModelPartData bone140 = ind_lamp_27.addChild("bone140", ModelPartBuilder.create().uv(11, 42).cuboid(-4.8F,
                -1.0F, -4.7F, 2.0F, 2.0F, 2.0F, new Dilation(-0.38F)), ModelTransform.pivot(19.0F, -16.0F, -2.55F));

        ModelPartData sym_lamp4 = bone134.addChild("sym_lamp4",
                ModelPartBuilder.create().uv(10, 5)
                        .cuboid(14.4121F, -16.85F, -4.8621F, 2.0F, 1.0F, 2.0F, new Dilation(-0.3F)).uv(0, 5)
                        .cuboid(14.4121F, -16.85F, -4.8621F, 2.0F, 1.0F, 2.0F, new Dilation(-0.1F)),
                ModelTransform.pivot(0.0F, 2.0F, 0.0F));

        ModelPartData bone145 = sym_lamp4.addChild("bone145", ModelPartBuilder.create().uv(0, 79).cuboid(-1.2879F,
                -0.85F, -1.7121F, 2.0F, 1.0F, 2.0F, new Dilation(-0.28F)), ModelTransform.pivot(15.7F, -16.0F, -3.15F));

        ModelPartData sym_lamp5 = bone134.addChild("sym_lamp5",
                ModelPartBuilder.create().uv(10, 5)
                        .cuboid(14.4121F, -16.85F, 2.8879F, 2.0F, 1.0F, 2.0F, new Dilation(-0.3F)).uv(0, 5)
                        .cuboid(14.4121F, -16.85F, 2.8879F, 2.0F, 1.0F, 2.0F, new Dilation(-0.1F)),
                ModelTransform.pivot(0.0F, 2.0F, 0.0F));

        ModelPartData bone141 = sym_lamp5.addChild("bone141", ModelPartBuilder.create().uv(0, 79).cuboid(-1.2879F,
                -0.85F, -1.7121F, 2.0F, 1.0F, 2.0F, new Dilation(-0.28F)), ModelTransform.pivot(15.7F, -16.0F, 4.6F));

        ModelPartData m_lever_3 = bone134.addChild("m_lever_3",
                ModelPartBuilder.create().uv(0, 38).cuboid(12.1F, -16.15F, -1.0F, 4.0F, 0.0F, 2.0F, new Dilation(0.0F))
                        .uv(45, 34).cuboid(12.1F, -17.4F, -1.0F, 4.0F, 2.0F, 2.0F, new Dilation(-0.4F)),
                ModelTransform.pivot(0.0F, 2.0F, 0.0F));

        ModelPartData bone142 = m_lever_3.addChild("bone142",
                ModelPartBuilder.create().uv(17, 0).cuboid(-0.6F, -1.5F, 0.0F, 1.0F, 1.0F, 0.0F, new Dilation(0.0F)),
                ModelTransform.of(14.35F, -16.35F, 0.0F, 0.0F, 0.0F, -0.4363F));

        ModelPartData cube_r52 = bone142.addChild("cube_r52",
                ModelPartBuilder.create().uv(10, 61).cuboid(-0.6F, -1.5F, -0.4F, 1.0F, 0.0F, 1.0F, new Dilation(0.2F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -0.7854F, 0.0F));

        ModelPartData s_lever_6 = bone134.addChild("s_lever_6", ModelPartBuilder.create(),
                ModelTransform.pivot(0.0F, 2.0F, 0.0F));

        ModelPartData cube_r53 = s_lever_6.addChild("cube_r53",
                ModelPartBuilder.create().uv(66, 63).cuboid(0.2F, -0.65F, -6.95F, 2.0F, 1.0F, 1.0F, new Dilation(0.1F)),
                ModelTransform.of(8.9F, -15.5F, 5.0F, 0.0F, 0.0F, 0.0F));

        ModelPartData bone143 = s_lever_6.addChild("bone143", ModelPartBuilder.create().uv(50, 39).cuboid(-1.0501F,
                0.0F, -0.45F, 1.0F, 0.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(10.15F, -16.25F, -1.25F, 0.0F, 0.0F, 0.7418F));

        ModelPartData s_lever_7 = bone134.addChild("s_lever_7", ModelPartBuilder.create(),
                ModelTransform.pivot(0.0F, 2.0F, 0.0F));

        ModelPartData cube_r54 = s_lever_7.addChild("cube_r54",
                ModelPartBuilder.create().uv(66, 63).cuboid(0.2F, -0.65F, -6.95F, 2.0F, 1.0F, 1.0F, new Dilation(0.1F)),
                ModelTransform.of(8.9F, -15.5F, 6.45F, 0.0F, 0.0F, 0.0F));

        ModelPartData bone144 = s_lever_7.addChild("bone144", ModelPartBuilder.create().uv(11, 50).cuboid(-1.0501F,
                0.0F, -0.45F, 1.0F, 0.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(10.15F, -16.25F, 0.2F, 0.0F, 0.0F, 0.7418F));

        ModelPartData s_lever_8 = bone134.addChild("s_lever_8", ModelPartBuilder.create(),
                ModelTransform.pivot(0.0F, 2.0F, 0.0F));

        ModelPartData cube_r55 = s_lever_8.addChild("cube_r55",
                ModelPartBuilder.create().uv(66, 63).cuboid(0.2F, -0.65F, -6.95F, 2.0F, 1.0F, 1.0F, new Dilation(0.1F)),
                ModelTransform.of(8.9F, -15.5F, 7.95F, 0.0F, 0.0F, 0.0F));

        ModelPartData bone146 = s_lever_8.addChild("bone146", ModelPartBuilder.create().uv(50, 39).cuboid(-1.0501F,
                0.0F, -0.45F, 1.0F, 0.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(10.15F, -16.25F, 1.7F, 0.0F, 0.0F, 0.7418F));

        ModelPartData s_crank_6 = bone134.addChild("s_crank_6", ModelPartBuilder.create(),
                ModelTransform.pivot(0.0F, 2.0F, 0.0F));

        ModelPartData bone147 = s_crank_6.addChild("bone147",
                ModelPartBuilder.create().uv(0, 58).cuboid(-1.05F, 0.05F, -1.0F, 2.0F, 0.0F, 2.0F, new Dilation(0.0F))
                        .uv(64, 67).cuboid(-0.3F, -0.45F, -0.25F, 2.0F, 1.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.pivot(13.1F, -16.2F, -2.45F));

        ModelPartData s_crank_7 = bone134.addChild("s_crank_7", ModelPartBuilder.create(),
                ModelTransform.pivot(0.0F, 2.0F, 0.0F));

        ModelPartData bone148 = s_crank_7.addChild("bone148",
                ModelPartBuilder.create().uv(0, 58).cuboid(-1.05F, 0.05F, -1.0F, 2.0F, 0.0F, 2.0F, new Dilation(0.0F))
                        .uv(64, 67).cuboid(-0.3F, -0.45F, -0.25F, 2.0F, 1.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.pivot(13.1F, -16.2F, 2.45F));

        ModelPartData bone39 = bone.addChild("bone39", ModelPartBuilder.create().uv(43, 68).cuboid(5.0F, -18.75F, -3.5F,
                1.0F, 19.0F, 7.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -1.5F, 0.0F));

        ModelPartData cube_r56 = bone39.addChild("cube_r56",
                ModelPartBuilder.create().uv(0, 0).cuboid(6.0F, -18.75F, -4.5F, 0.0F, 3.0F, 9.0F, new Dilation(0.0F)),
                ModelTransform.of(7.632F, 0.8688F, 0.0F, 0.0F, 0.0F, -0.3927F));

        ModelPartData bone40 = bone39.addChild("bone40", ModelPartBuilder.create().uv(43, 68).cuboid(5.0F, -18.75F,
                -3.5F, 1.0F, 19.0F, 7.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData cube_r57 = bone40.addChild("cube_r57",
                ModelPartBuilder.create().uv(0, 0).cuboid(6.0F, -18.75F, -4.5F, 0.0F, 3.0F, 9.0F, new Dilation(0.0F)),
                ModelTransform.of(7.632F, 0.8688F, 0.0F, 0.0F, 0.0F, -0.3927F));

        ModelPartData bone41 = bone40.addChild("bone41", ModelPartBuilder.create().uv(43, 68).cuboid(5.0F, -18.75F,
                -3.5F, 1.0F, 19.0F, 7.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData cube_r58 = bone41.addChild("cube_r58",
                ModelPartBuilder.create().uv(0, 0).cuboid(6.0F, -18.75F, -4.5F, 0.0F, 3.0F, 9.0F, new Dilation(0.0F)),
                ModelTransform.of(7.632F, 0.8688F, 0.0F, 0.0F, 0.0F, -0.3927F));

        ModelPartData bone42 = bone41.addChild("bone42", ModelPartBuilder.create().uv(43, 68).cuboid(5.0F, -18.75F,
                -3.5F, 1.0F, 19.0F, 7.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData cube_r59 = bone42.addChild("cube_r59",
                ModelPartBuilder.create().uv(0, 0).cuboid(6.0F, -18.75F, -4.5F, 0.0F, 3.0F, 9.0F, new Dilation(0.0F)),
                ModelTransform.of(7.632F, 0.8688F, 0.0F, 0.0F, 0.0F, -0.3927F));

        ModelPartData bone43 = bone42.addChild("bone43", ModelPartBuilder.create().uv(43, 68).cuboid(5.0F, -18.75F,
                -3.5F, 1.0F, 19.0F, 7.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData cube_r60 = bone43.addChild("cube_r60",
                ModelPartBuilder.create().uv(0, 0).cuboid(6.0F, -18.75F, -4.5F, 0.0F, 3.0F, 9.0F, new Dilation(0.0F)),
                ModelTransform.of(7.632F, 0.8688F, 0.0F, 0.0F, 0.0F, -0.3927F));

        ModelPartData bone44 = bone43.addChild("bone44", ModelPartBuilder.create().uv(43, 68).cuboid(5.0F, -18.75F,
                -3.5F, 1.0F, 19.0F, 7.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData cube_r61 = bone44.addChild("cube_r61",
                ModelPartBuilder.create().uv(0, 0).cuboid(6.0F, -18.75F, -4.5F, 0.0F, 3.0F, 9.0F, new Dilation(0.0F)),
                ModelTransform.of(7.632F, 0.8688F, 0.0F, 0.0F, 0.0F, -0.3927F));

        ModelPartData rotor = bone.addChild("rotor", ModelPartBuilder.create(),
                ModelTransform.pivot(0.0F, -19.5F, 0.0F));

        ModelPartData glass = rotor.addChild("glass", ModelPartBuilder.create().uv(18, 79).cuboid(-0.8F, -10.75F, -3.0F,
                6.0F, 13.0F, 6.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -1.0F, 0.0F));

        ModelPartData bone150 = glass.addChild("bone150", ModelPartBuilder.create().uv(18, 79).cuboid(-0.8F, -10.75F,
                -3.0F, 6.0F, 13.0F, 6.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone151 = bone150.addChild("bone151", ModelPartBuilder.create().uv(18, 79).cuboid(-0.8F, -10.75F,
                -3.0F, 6.0F, 13.0F, 6.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone152 = bone151.addChild("bone152", ModelPartBuilder.create().uv(18, 79).cuboid(-0.8F, -10.75F,
                -3.0F, 6.0F, 13.0F, 6.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone153 = bone152.addChild("bone153", ModelPartBuilder.create().uv(18, 79).cuboid(-0.8F, -10.75F,
                -3.0F, 6.0F, 13.0F, 6.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone154 = bone153.addChild("bone154", ModelPartBuilder.create().uv(18, 79).cuboid(-0.8F, -10.75F,
                -3.0F, 6.0F, 13.0F, 6.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData compass = rotor.addChild("compass", ModelPartBuilder.create(),
                ModelTransform.pivot(0.0F, -1.0F, 0.0F));

        ModelPartData net = compass.addChild("net",
                ModelPartBuilder.create().uv(0, 105).cuboid(2.6F, -8.75F, -1.5F, 0.0F, 7.0F, 3.0F, new Dilation(0.0F))
                        .uv(0, 157).cuboid(2.35F, -8.75F, -1.5F, 0.0F, 4.0F, 3.0F, new Dilation(0.0F)).uv(-3, 116)
                        .cuboid(-1.4F, -1.75F, -1.5F, 4.0F, 0.0F, 3.0F, new Dilation(0.0F)),
                ModelTransform.pivot(0.0F, 1.0F, 0.0F));

        ModelPartData bone149 = net.addChild("bone149",
                ModelPartBuilder.create().uv(0, 105).cuboid(2.6F, -8.75F, -1.5F, 0.0F, 7.0F, 3.0F, new Dilation(0.0F))
                        .uv(-3, 116).cuboid(-1.4F, -1.75F, -1.5F, 4.0F, 0.0F, 3.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone155 = bone149.addChild("bone155",
                ModelPartBuilder.create().uv(0, 105).cuboid(2.6F, -8.75F, -1.5F, 0.0F, 7.0F, 3.0F, new Dilation(0.0F))
                        .uv(-3, 116).cuboid(-1.4F, -1.75F, -1.5F, 4.0F, 0.0F, 3.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone156 = bone155.addChild("bone156",
                ModelPartBuilder.create().uv(0, 105).cuboid(2.6F, -8.75F, -1.5F, 0.0F, 7.0F, 3.0F, new Dilation(0.0F))
                        .uv(0, 157).cuboid(2.35F, -8.75F, -1.5F, 0.0F, 4.0F, 3.0F, new Dilation(0.0F)).uv(-3, 116)
                        .cuboid(-1.4F, -1.75F, -1.5F, 4.0F, 0.0F, 3.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone157 = bone156.addChild("bone157",
                ModelPartBuilder.create().uv(0, 105).cuboid(2.6F, -8.75F, -1.5F, 0.0F, 7.0F, 3.0F, new Dilation(0.0F))
                        .uv(-3, 116).cuboid(-1.4F, -1.75F, -1.5F, 4.0F, 0.0F, 3.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone158 = bone157.addChild("bone158",
                ModelPartBuilder.create().uv(0, 105).cuboid(2.6F, -8.75F, -1.5F, 0.0F, 7.0F, 3.0F, new Dilation(0.0F))
                        .uv(-3, 116).cuboid(-1.4F, -1.75F, -1.5F, 4.0F, 0.0F, 3.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData nav = compass.addChild("nav",
                ModelPartBuilder.create().uv(0, 120).cuboid(1.0F, -4.25F, -1.0F, 2.0F, 2.0F, 2.0F, new Dilation(-0.3F))
                        .uv(0, 120).cuboid(-3.0F, -4.25F, -1.0F, 2.0F, 2.0F, 2.0F, new Dilation(-0.3F)).uv(0, 125)
                        .cuboid(-2.5F, -10.0F, 0.0F, 5.0F, 9.0F, 0.0F, new Dilation(0.0F)).uv(0, 145)
                        .cuboid(-0.5F, -9.5F, -0.5F, 1.0F, 11.0F, 1.0F, new Dilation(-0.3F)),
                ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData cube_r62 = nav.addChild("cube_r62",
                ModelPartBuilder.create().uv(0, 135).cuboid(-2.5F, -10.0F, 0.0F, 5.0F, 9.0F, 0.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.5708F, 0.0F));

        ModelPartData bone159 = nav.addChild("bone159", ModelPartBuilder.create(),
                ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData cube_r63 = bone159.addChild("cube_r63",
                ModelPartBuilder.create().uv(0, 158).cuboid(-1.0F, -2.0F, 0.0F, 2.0F, 1.0F, 0.0F, new Dilation(0.0F)),
                ModelTransform.of(1.25F, -5.1213F, -1.3713F, -0.7854F, 0.0F, 0.0F));

        ModelPartData cube_r64 = bone159.addChild("cube_r64",
                ModelPartBuilder.create().uv(0, 158).cuboid(-1.0F, -2.0F, 0.0F, 2.0F, 1.0F, 0.0F, new Dilation(0.0F)),
                ModelTransform.of(1.25F, -4.4142F, 0.75F, 0.7854F, 0.0F, 0.0F));

        ModelPartData cube_r65 = bone159.addChild("cube_r65",
                ModelPartBuilder.create().uv(0, 158).cuboid(-1.0F, -2.0F, 0.0F, 2.0F, 1.0F, 0.0F, new Dilation(0.0F)),
                ModelTransform.of(1.25F, -3.7071F, -1.3713F, -0.7854F, 0.0F, 0.0F));

        ModelPartData cube_r66 = bone159.addChild("cube_r66",
                ModelPartBuilder.create().uv(0, 158).cuboid(-1.0F, -2.0F, 0.0F, 2.0F, 1.0F, 0.0F, new Dilation(0.0F)),
                ModelTransform.of(1.25F, -3.0F, 0.75F, 0.7854F, 0.0F, 0.0F));

        ModelPartData bone160 = nav.addChild("bone160", ModelPartBuilder.create(),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 3.1416F, 0.0F));

        ModelPartData cube_r67 = bone160.addChild("cube_r67",
                ModelPartBuilder.create().uv(0, 158).cuboid(-1.0F, -2.0F, 0.0F, 2.0F, 1.0F, 0.0F, new Dilation(0.0F)),
                ModelTransform.of(1.25F, -5.1213F, -1.3713F, -0.7854F, 0.0F, 0.0F));

        ModelPartData cube_r68 = bone160.addChild("cube_r68",
                ModelPartBuilder.create().uv(0, 158).cuboid(-1.0F, -2.0F, 0.0F, 2.0F, 1.0F, 0.0F, new Dilation(0.0F)),
                ModelTransform.of(1.25F, -4.4142F, 0.75F, 0.7854F, 0.0F, 0.0F));

        ModelPartData cube_r69 = bone160.addChild("cube_r69",
                ModelPartBuilder.create().uv(0, 158).cuboid(-1.0F, -2.0F, 0.0F, 2.0F, 1.0F, 0.0F, new Dilation(0.0F)),
                ModelTransform.of(1.25F, -3.7071F, -1.3713F, -0.7854F, 0.0F, 0.0F));

        ModelPartData cube_r70 = bone160.addChild("cube_r70",
                ModelPartBuilder.create().uv(0, 158).cuboid(-1.0F, -2.0F, 0.0F, 2.0F, 1.0F, 0.0F, new Dilation(0.0F)),
                ModelTransform.of(1.25F, -3.0F, 0.75F, 0.7854F, 0.0F, 0.0F));

        ModelPartData frame = rotor.addChild("frame",
                ModelPartBuilder.create().uv(14, 103).cuboid(4.35F, -9.0F, -2.5F, 0.0F, 8.0F, 5.0F, new Dilation(0.0F))
                        .uv(11, 116).cuboid(-0.65F, -0.75F, -2.5F, 5.0F, 6.0F, 5.0F, new Dilation(0.0F)).uv(20, 108)
                        .cuboid(-4.5F, -1.5F, -2.5F, 9.0F, 0.0F, 5.0F, new Dilation(0.0F)),
                ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData bone161 = frame.addChild("bone161",
                ModelPartBuilder.create().uv(14, 103).cuboid(4.35F, -9.0F, -2.5F, 0.0F, 8.0F, 5.0F, new Dilation(0.0F))
                        .uv(11, 116).cuboid(-0.65F, -0.75F, -2.5F, 5.0F, 6.0F, 5.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone162 = bone161.addChild("bone162",
                ModelPartBuilder.create().uv(14, 103).cuboid(4.35F, -9.0F, -2.5F, 0.0F, 8.0F, 5.0F, new Dilation(0.0F))
                        .uv(11, 116).cuboid(-0.65F, -0.75F, -2.5F, 5.0F, 6.0F, 5.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone163 = bone162.addChild("bone163",
                ModelPartBuilder.create().uv(14, 103).cuboid(4.35F, -9.0F, -2.5F, 0.0F, 8.0F, 5.0F, new Dilation(0.0F))
                        .uv(11, 116).cuboid(-0.65F, -0.75F, -2.5F, 5.0F, 6.0F, 5.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone164 = bone163.addChild("bone164",
                ModelPartBuilder.create().uv(14, 103).cuboid(4.35F, -9.0F, -2.5F, 0.0F, 8.0F, 5.0F, new Dilation(0.0F))
                        .uv(11, 116).cuboid(-0.65F, -0.75F, -2.5F, 5.0F, 6.0F, 5.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone165 = bone164.addChild("bone165",
                ModelPartBuilder.create().uv(14, 103).cuboid(4.35F, -9.0F, -2.5F, 0.0F, 8.0F, 5.0F, new Dilation(0.0F))
                        .uv(11, 116).cuboid(-0.65F, -0.75F, -2.5F, 5.0F, 6.0F, 5.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData lights = frame.addChild("lights", ModelPartBuilder.create(),
                ModelTransform.pivot(0.0F, -1.0F, 0.0F));

        ModelPartData light_1 = lights.addChild("light_1",
                ModelPartBuilder.create().uv(3, 67).cuboid(2.9F, -7.75F, 0.0F, 2.0F, 7.0F, 0.0F, new Dilation(0.0F)),
                ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData bone166 = light_1.addChild("bone166",
                ModelPartBuilder.create().uv(0, 67).cuboid(2.9F, -7.75F, 0.05F, 1.0F, 7.0F, 0.0F, new Dilation(0.0F)),
                ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData bone169 = light_1.addChild("bone169",
                ModelPartBuilder.create().uv(0, 67).cuboid(2.9F, -7.75F, -0.05F, 1.0F, 7.0F, 0.0F, new Dilation(0.0F)),
                ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData light_2 = lights.addChild("light_2",
                ModelPartBuilder.create().uv(3, 67).cuboid(2.9F, -7.75F, 0.0F, 2.0F, 7.0F, 0.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -2.0944F, 0.0F));

        ModelPartData bone167 = light_2.addChild("bone167",
                ModelPartBuilder.create().uv(0, 67).cuboid(2.9F, -7.75F, 0.05F, 1.0F, 7.0F, 0.0F, new Dilation(0.0F)),
                ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData bone170 = light_2.addChild("bone170",
                ModelPartBuilder.create().uv(0, 67).cuboid(2.9F, -7.75F, -0.05F, 1.0F, 7.0F, 0.0F, new Dilation(0.0F)),
                ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData light_3 = lights.addChild("light_3",
                ModelPartBuilder.create().uv(3, 67).cuboid(2.9F, -7.75F, 0.0F, 2.0F, 7.0F, 0.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 2.0944F, 0.0F));

        ModelPartData bone168 = light_3.addChild("bone168",
                ModelPartBuilder.create().uv(0, 67).cuboid(2.9F, -7.75F, 0.05F, 1.0F, 7.0F, 0.0F, new Dilation(0.0F)),
                ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData bone171 = light_3.addChild("bone171",
                ModelPartBuilder.create().uv(0, 67).cuboid(2.9F, -7.75F, -0.05F, 1.0F, 7.0F, 0.0F, new Dilation(0.0F)),
                ModelTransform.pivot(0.0F, 0.0F, 0.0F));
        return TexturedModelData.of(modelData, 256, 256);
    }

    @Override
    public Animation getAnimationForState(TravelHandlerBase.State state) {
        if (state == TravelHandlerBase.State.LANDED)
            return HartnellAnimations.HARTNELL_IDLE_ANIMATION;

        return HartnellAnimations.HARTNELL_INFLIGHT_ANIMATION;
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red,
            float green, float blue, float alpha) {
        bone.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
    }

    @Override
    public void renderWithAnimations(ConsoleBlockEntity console, ClientTardis tardis, ModelPart root, MatrixStack matrices,
                                     VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float pAlpha) {
        matrices.push();
        matrices.translate(0.5f, -1.5f, -0.5f);

        this.bone.getChild("panels").getChild("p_4").getChild("bone98").getChild("bone99").getChild("bone100")
                .getChild("m_meter_2")
                .getChild("bone110").yaw = (float) (((tardis.getFuel() / FuelHandler.TARDIS_MAX_FUEL) * 2) - 1);
        ModelPart fuelLowWarningLight = this.bone.getChild("panels").getChild("p_3").getChild("bone67")
                .getChild("bone68").getChild("bone69").getChild("sym_lamp2").getChild("bone96");
        // Low Fuel Light
        if (!(tardis.getFuel() <= (FuelHandler.TARDIS_MAX_FUEL / 10))) {
            fuelLowWarningLight.pivotY = fuelLowWarningLight.pivotY + 1;
        }

        // X Control Movement
        ModelPart xControl = this.bone.getChild("panels").getChild("p_3").getChild("bone67").getChild("bone68")
                .getChild("bone69").getChild("s_lever_2").getChild("bone70");
        ModelPart xControlLight = this.bone.getChild("panels").getChild("p_3").getChild("bone67").getChild("bone68")
                .getChild("bone69").getChild("ind_lamp_11").getChild("bone82");
        BlockPos destination = tardis.travel().destination().getPos();

        if (destination.getX() < 0) {
            xControl.roll = xControl.roll + 1.575f;
            xControlLight.pivotY = xControlLight.pivotY + 1;
        }


        // Y Control Movement
        ModelPart yControl = this.bone.getChild("panels").getChild("p_3").getChild("bone67").getChild("bone68")
                .getChild("bone69").getChild("s_lever_3").getChild("bone76");
        ModelPart yControlLight = this.bone.getChild("panels").getChild("p_3").getChild("bone67").getChild("bone68")
                .getChild("bone69").getChild("ind_lamp_12").getChild("bone83");

        if (destination.getY() < 0) {
            yControl.roll = yControl.roll + 1.575f;
            yControlLight.pivotY = yControlLight.pivotY + 1;
        }

        // Z Control Movement
        ModelPart zControl = this.bone.getChild("panels").getChild("p_3").getChild("bone67").getChild("bone68")
                .getChild("bone69").getChild("s_lever_4").getChild("bone77");
        ModelPart zControlLight = this.bone.getChild("panels").getChild("p_3").getChild("bone67").getChild("bone68")
                .getChild("bone69").getChild("ind_lamp_13").getChild("bone84");

        if (destination.getZ() < 0) {
            zControl.roll = zControl.roll + 1.575f;
            zControlLight.pivotY = zControlLight.pivotY + 1;
        }

        // Fast Return Movements
        ModelPart fastReturn = this.bone.getChild("panels").getChild("p_1").getChild("bone38").getChild("bone36")
                .getChild("bone37").getChild("fastreturn").getChild("bone25");
        if (tardis.travel().destination().equals(tardis.travel().previousPosition())) {
            fastReturn.pivotY = fastReturn.pivotY + 0.25f;
        }

        // Throttle Control Movements
        ModelPart throttle = this.bone.getChild("panels").getChild("p_1").getChild("bone38").getChild("bone36")
                .getChild("bone37").getChild("m_lever_1").getChild("bone45");
        throttle.roll = throttle.roll + (tardis.travel().speed() / (float) tardis.travel().maxSpeed().get());

        // Handbrake Control Movements
        ModelPart handbrake = this.bone.getChild("panels").getChild("p_1").getChild("bone38").getChild("bone36")
                .getChild("bone37").getChild("m_lever_2").getChild("bone46");
        handbrake.roll = tardis.travel().handbrake() ? handbrake.roll + 1 : handbrake.roll;

        // Power Control Movements
        ModelPart powerControl = this.bone.getChild("panels").getChild("p_6").getChild("bone132").getChild("bone133")
                .getChild("bone134").getChild("m_lever_3").getChild("bone142");
        ModelPart rotor = this.bone.getChild("rotor");

        powerControl.roll = tardis.fuel().hasPower() ? powerControl.roll + 1 : powerControl.roll;
        rotor.pivotY = !tardis.fuel().hasPower() ? rotor.pivotY + 5 : rotor.pivotY;

        // Door Control Movements
        ModelPart doorControl = this.bone.getChild("panels").getChild("p_5").getChild("bone112").getChild("bone113")
                .getChild("bone114").getChild("ctrl_panel_3").getChild("bone123");
        ModelPart doorControlLight = this.bone.getChild("panels").getChild("p_5").getChild("bone112")
                .getChild("bone113").getChild("bone114").getChild("ind_lamp_20").getChild("bone117");
        if (tardis.door().isLeftOpen()) {
            doorControl.yaw = doorControl.yaw + 1.575f;
            doorControlLight.pivotY = doorControlLight.pivotY + 1;
        } else if (tardis.door().isRightOpen()) {
            doorControl.yaw = doorControl.yaw + 3.15f;
            doorControlLight.pivotY = doorControlLight.pivotY + 1;
        }

        // Door Lock Control Movement
        ModelPart doorLock = this.bone.getChild("panels").getChild("p_5").getChild("bone112").getChild("bone113")
                .getChild("bone114").getChild("ctrl_panel_3").getChild("bone125");
        ModelPart doorLockLight = this.bone.getChild("panels").getChild("p_5").getChild("bone112").getChild("bone113")
                .getChild("bone114").getChild("ind_lamp_21").getChild("bone118");
        doorLock.yaw = tardis.door().locked() ? doorLock.yaw + 1.575f : doorLock.yaw;
        doorLockLight.pivotY = tardis.door().locked() ? doorLockLight.pivotY : doorLockLight.pivotY + 1;

        // Refueler Control Movements
        ModelPart refueler = this.bone.getChild("panels").getChild("p_4").getChild("bone98").getChild("bone99")
                .getChild("bone100").getChild("ctrl_panel_2").getChild("bone106");
        ModelPart refuelerLight = this.bone.getChild("panels").getChild("p_4").getChild("bone98").getChild("bone99")
                .getChild("bone100").getChild("ind_lamp_16").getChild("bone111");
        refueler.yaw = tardis.isRefueling() ? refueler.yaw + 1.575f : refueler.yaw;
        refuelerLight.pivotY = tardis.isRefueling() ? refuelerLight.pivotY : refuelerLight.pivotY + 1;

        ModelPart cloak = this.bone.getChild("panels").getChild("p_4").getChild("bone98").getChild("bone99")
                .getChild("bone100").getChild("ctrl_panel_2").getChild("bone108");
        ModelPart cloakLight = this.bone.getChild("panels").getChild("p_4").getChild("bone98").getChild("bone99")
                .getChild("bone100").getChild("ind_lamp_15").getChild("bone101");
        cloak.yaw = tardis.<CloakHandler>handler(TardisComponent.Id.CLOAK).cloaked().get()
                ? cloak.yaw + 1.575f
                : cloak.yaw;
        cloakLight.pivotY = tardis.<CloakHandler>handler(TardisComponent.Id.CLOAK).cloaked().get()
                ? cloakLight.pivotY
                : cloakLight.pivotY + 1;

        // Ground Search Control Movements
        ModelPart groundSearch = this.bone.getChild("panels").getChild("p_4").getChild("bone98").getChild("bone99")
                .getChild("bone100").getChild("s_knob");
        groundSearch.pivotZ = !tardis.travel().horizontalSearch().get()
                ? groundSearch.pivotZ - 1.5f
                : groundSearch.pivotZ; // FIXME use TravelHandler#horizontalSearch/#verticalSearch

        // Hail Mary Control Movements
        ModelPart hailMary = this.bone.getChild("panels").getChild("p_2").getChild("bone48").getChild("bone49")
                .getChild("bone50").getChild("s_lever").getChild("bone61");
        hailMary.roll = tardis.stats().hailMary().get() ? hailMary.roll + 1.75f : hailMary.roll;
        ModelPart hailMaryWarningLight = this.bone.getChild("panels").getChild("p_2").getChild("bone48")
                .getChild("bone49").getChild("bone50").getChild("sym_lamp").getChild("bone97");
        hailMaryWarningLight.pivotY = tardis.stats().hailMary().get()
                ? hailMaryWarningLight.pivotY
                : hailMaryWarningLight.pivotY + 1;

        // Hads Alarm Control Movements
        ModelPart hadsAlarms = this.bone.getChild("panels").getChild("p_6").getChild("bone132").getChild("bone133")
                .getChild("bone134").getChild("s_lever_6").getChild("bone143");
        ModelPart hadsAlarmsLightsOne = this.bone.getChild("panels").getChild("p_6").getChild("bone132")
                .getChild("bone133").getChild("bone134").getChild("sym_lamp4").getChild("bone145");
        ModelPart hadsAlarmsLightsTwo = this.bone.getChild("panels").getChild("p_6").getChild("bone132")
                .getChild("bone133").getChild("bone134").getChild("sym_lamp5").getChild("bone141");
        if (tardis.alarm().isEnabled()) {
            hadsAlarms.roll = hadsAlarms.roll + 1.75f;
        } else {
            hadsAlarmsLightsOne.pivotY = hadsAlarmsLightsOne.pivotY + 1;
            hadsAlarmsLightsTwo.pivotY = hadsAlarmsLightsTwo.pivotY + 1;
        }

        ModelPart security = this.bone.getChild("panels").getChild("p_6").getChild("bone132").getChild("bone133")
                .getChild("bone134").getChild("s_lever_7").getChild("bone144");
        security.roll = (tardis.stats().security().get()) ? security.roll + 1.75f : security.roll;

        // Increment Control Movements
        ModelPart increment = this.bone.getChild("panels").getChild("p_3").getChild("bone67").getChild("bone68")
                .getChild("bone69").getChild("s_crank_3").getChild("bone74");
        increment.yaw = IncrementManager.increment(tardis) >= 10
                ? IncrementManager.increment(tardis) >= 100
                        ? IncrementManager.increment(tardis) >= 1000
                                ? IncrementManager.increment(tardis) >= 10000
                                        ? increment.yaw + 2f
                                        : increment.yaw + 1.5f
                                : increment.yaw + 1f
                        : increment.yaw + 0.5f
                : increment.yaw;

        // Direction Control Movements
        ModelPart direction = this.bone.getChild("panels").getChild("p_2").getChild("bone48").getChild("bone49")
                .getChild("bone50").getChild("s_crank_1").getChild("bone59");
        direction.yaw += (0.5f * tardis.travel().destination().getRotation());

        // Anti Grav Control Movements
        ModelPart antiGrav = this.bone.getChild("panels").getChild("p_1").getChild("bone38").getChild("bone36")
                .getChild("bone37").getChild("sl_switch_1").getChild("bone33");
        antiGrav.pivotX = !tardis.travel().antigravs().get() ? antiGrav.pivotX : antiGrav.pivotX + 1;

        ModelPart shield = this.bone.getChild("panels").getChild("p_2").getChild("bone48").getChild("bone49")
                .getChild("bone50").getChild("sl_switch_6").getChild("bone57");
        shield.pivotX = tardis.shields().shielded().get()
                ? tardis.shields().visuallyShielded().get()
                        ? shield.pivotX + 0.5f
                        : shield.pivotX + 1
                : shield.pivotX;

        ModelPart siegeProtocol = this.bone.getChild("panels").getChild("p_2").getChild("bone48").getChild("bone49")
                .getChild("bone50").getChild("sl_switch_5").getChild("bone56");
        siegeProtocol.pivotX = !tardis.siege().isActive() ? siegeProtocol.pivotX : siegeProtocol.pivotX + 1;

        // Auto Pilot Control Movements
        ModelPart autoPilot = this.bone.getChild("panels").getChild("p_1").getChild("bone38").getChild("bone36")
                .getChild("bone37").getChild("st_switch").getChild("bone26");
        autoPilot.yaw = !tardis.travel().autopilot() ? autoPilot.yaw + 1 : autoPilot.yaw;
        super.renderWithAnimations(console, tardis, root, matrices, vertices, light, overlay, red, green, blue, pAlpha);
        matrices.pop();
    }

    @Override
    public ModelPart getPart() {
        return bone;
    }
}
