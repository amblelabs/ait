package dev.amble.ait.client.models.consoles;

import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.animation.Animation;
import net.minecraft.client.util.math.MatrixStack;

import dev.amble.ait.client.animation.console.hudolin.HudolinAnimations;
import dev.amble.ait.core.blockentities.ConsoleBlockEntity;
import dev.amble.ait.core.tardis.Tardis;
import dev.amble.ait.core.tardis.handler.travel.TravelHandlerBase;

public class HudolinConsoleModel extends ConsoleModel {
    private final ModelPart console;

    public HudolinConsoleModel(ModelPart root) {
        this.console = root.getChild("console");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData console = modelPartData.addChild("console", ModelPartBuilder.create(),
                ModelTransform.pivot(0.0F, 24.0F, 0.0F));

        ModelPartData base_console = console.addChild("base_console", ModelPartBuilder.create(),
                ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData rotor = base_console.addChild("rotor", ModelPartBuilder.create(),
                ModelTransform.pivot(0.0F, -1.0F, 0.0F));

        ModelPartData rotor_bottom_spikes = rotor.addChild("rotor_bottom_spikes", ModelPartBuilder.create().uv(38, 67)
                .cuboid(-0.5F, -16.5F, 2.3484F, 1.0F, 12.0F, 2.0F, new Dilation(0.0F)),
                ModelTransform.pivot(0.0F, -19.5F, 0.0F));

        ModelPartData bone176 = rotor_bottom_spikes.addChild("bone176", ModelPartBuilder.create().uv(39, 30)
                .cuboid(-0.5F, -13.5F, 2.3484F, 1.0F, 9.0F, 2.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone204 = bone176.addChild("bone204", ModelPartBuilder.create(),
                ModelTransform.pivot(-0.5F, -1.5F, -1.0F));

        ModelPartData bone177 = bone176.addChild("bone177", ModelPartBuilder.create().uv(38, 67).cuboid(-0.5F, -16.5F,
                2.3484F, 1.0F, 12.0F, 2.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone178 = bone177.addChild("bone178", ModelPartBuilder.create().uv(39, 30).cuboid(-0.5F, -13.5F,
                2.3484F, 1.0F, 9.0F, 2.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone179 = bone178.addChild("bone179", ModelPartBuilder.create().uv(38, 67).cuboid(-0.5F, -16.5F,
                2.3484F, 1.0F, 12.0F, 2.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone180 = bone179.addChild("bone180", ModelPartBuilder.create().uv(39, 30).cuboid(-0.5F, -13.5F,
                2.3484F, 1.0F, 9.0F, 2.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData rotor_bottom_ring = rotor.addChild("rotor_bottom_ring",
                ModelPartBuilder.create().uv(0, 74).cuboid(-1.5F, -4.5F, 2.7234F, 3.0F, 1.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, -26.5F, 0.0F, 0.0F, -0.5236F, 0.0F));

        ModelPartData bone171 = rotor_bottom_ring.addChild("bone171",
                ModelPartBuilder.create().uv(0, 74).cuboid(-1.5F, -4.5F, 2.7234F, 3.0F, 1.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone172 = bone171.addChild("bone172",
                ModelPartBuilder.create().uv(0, 74).cuboid(-1.5F, -4.5F, 2.7234F, 3.0F, 1.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone173 = bone172.addChild("bone173",
                ModelPartBuilder.create().uv(0, 74).cuboid(-1.5F, -4.5F, 2.7234F, 3.0F, 1.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone174 = bone173.addChild("bone174",
                ModelPartBuilder.create().uv(0, 74).cuboid(-1.5F, -4.5F, 2.7234F, 3.0F, 1.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone175 = bone174.addChild("bone175",
                ModelPartBuilder.create().uv(0, 74).cuboid(-1.5F, -4.5F, 2.7234F, 3.0F, 1.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData rotor_top_spikes = rotor
                .addChild(
                        "rotor_top_spikes", ModelPartBuilder.create().uv(28, 42).cuboid(-0.5F, -16.5F, 2.3484F, 1.0F,
                                12.0F, 2.0F, new Dilation(0.0F)),
                        ModelTransform.of(0.0F, -32.5F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData bone161 = rotor_top_spikes.addChild("bone161", ModelPartBuilder.create().uv(0, 30).cuboid(-0.5F,
                -16.5F, 2.3484F, 1.0F, 9.0F, 2.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData bone162 = bone161.addChild("bone162", ModelPartBuilder.create().uv(28, 42).cuboid(-0.5F, -16.5F,
                2.3484F, 1.0F, 12.0F, 2.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData bone163 = bone162.addChild("bone163", ModelPartBuilder.create().uv(0, 30).cuboid(-0.5F, -16.5F,
                2.3484F, 1.0F, 9.0F, 2.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData bone164 = bone163.addChild("bone164", ModelPartBuilder.create().uv(28, 42).cuboid(-0.5F, -16.5F,
                2.3484F, 1.0F, 12.0F, 2.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData bone165 = bone164.addChild("bone165", ModelPartBuilder.create().uv(0, 30).cuboid(-0.5F, -16.5F,
                2.3484F, 1.0F, 9.0F, 2.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData rotor_top_ring = rotor.addChild("rotor_top_ring",
                ModelPartBuilder.create().uv(0, 74).cuboid(-1.5F, -4.5F, 2.7234F, 3.0F, 1.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, -38.5F, 0.0F, 0.0F, -0.5236F, 0.0F));

        ModelPartData bone166 = rotor_top_ring.addChild("bone166",
                ModelPartBuilder.create().uv(0, 74).cuboid(-1.5F, -4.5F, 2.7234F, 3.0F, 1.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone167 = bone166.addChild("bone167",
                ModelPartBuilder.create().uv(0, 74).cuboid(-1.5F, -4.5F, 2.7234F, 3.0F, 1.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone168 = bone167.addChild("bone168",
                ModelPartBuilder.create().uv(0, 74).cuboid(-1.5F, -4.5F, 2.7234F, 3.0F, 1.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone169 = bone168.addChild("bone169",
                ModelPartBuilder.create().uv(0, 74).cuboid(-1.5F, -4.5F, 2.7234F, 3.0F, 1.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone170 = bone169.addChild("bone170",
                ModelPartBuilder.create().uv(0, 74).cuboid(-1.5F, -4.5F, 2.7234F, 3.0F, 1.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData plinth = base_console.addChild("plinth", ModelPartBuilder.create(),
                ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData plinth_supports = plinth.addChild("plinth_supports", ModelPartBuilder.create(),
                ModelTransform.of(0.0F, -4.5F, 0.0F, 0.0F, -0.5236F, 0.0F));

        ModelPartData bone108 = plinth_supports.addChild("bone108", ModelPartBuilder.create().uv(38, 30).cuboid(-0.499F,
                -3.0F, -0.5F, 1.0F, 3.0F, 12.0F, new Dilation(0.025F)),
                ModelTransform.of(0.0F, -2.25F, 5.863F, 0.6981F, 0.0F, 0.0F));

        ModelPartData bone109 = plinth_supports.addChild("bone109", ModelPartBuilder.create(),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone110 = bone109.addChild("bone110", ModelPartBuilder.create().uv(38, 30).cuboid(-0.499F, -3.0F,
                -0.5F, 1.0F, 3.0F, 12.0F, new Dilation(0.025F)),
                ModelTransform.of(0.0F, -2.25F, 5.863F, 0.6981F, 0.0F, 0.0F));

        ModelPartData bone111 = bone109.addChild("bone111", ModelPartBuilder.create(),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone112 = bone111.addChild("bone112", ModelPartBuilder.create().uv(38, 30).cuboid(-0.499F, -3.0F,
                -0.5F, 1.0F, 3.0F, 12.0F, new Dilation(0.025F)),
                ModelTransform.of(0.0F, -2.25F, 5.863F, 0.6981F, 0.0F, 0.0F));

        ModelPartData bone113 = bone111.addChild("bone113", ModelPartBuilder.create(),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone114 = bone113.addChild("bone114", ModelPartBuilder.create().uv(38, 30).cuboid(-0.499F, -3.0F,
                -0.5F, 1.0F, 3.0F, 12.0F, new Dilation(0.025F)),
                ModelTransform.of(0.0F, -2.25F, 5.863F, 0.6981F, 0.0F, 0.0F));

        ModelPartData bone115 = bone113.addChild("bone115", ModelPartBuilder.create(),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone116 = bone115.addChild("bone116", ModelPartBuilder.create().uv(38, 30).cuboid(-0.499F, -3.0F,
                -0.5F, 1.0F, 3.0F, 12.0F, new Dilation(0.025F)),
                ModelTransform.of(0.0F, -2.25F, 5.863F, 0.6981F, 0.0F, 0.0F));

        ModelPartData bone117 = bone115.addChild("bone117", ModelPartBuilder.create(),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone118 = bone117.addChild("bone118", ModelPartBuilder.create().uv(38, 30).cuboid(-0.499F, -3.0F,
                -0.5F, 1.0F, 3.0F, 12.0F, new Dilation(0.025F)),
                ModelTransform.of(0.0F, -2.25F, 5.863F, 0.6981F, 0.0F, 0.0F));

        ModelPartData plinth_supports2 = plinth.addChild("plinth_supports2", ModelPartBuilder.create(),
                ModelTransform.of(0.0F, -5.25F, 0.0F, 0.0F, -0.5236F, 0.0F));

        ModelPartData bone192 = plinth_supports2.addChild("bone192",
                ModelPartBuilder.create().uv(7, 15).cuboid(-0.499F, -3.0F, 4.5F, 1.0F, 3.0F, 2.0F, new Dilation(0.025F))
                        .uv(62, 50).cuboid(-0.499F, 0.0F, -0.5F, 1.0F, 1.0F, 7.0F, new Dilation(0.025F)),
                ModelTransform.of(0.0F, 0.0F, 5.863F, -0.7854F, 0.0F, 0.0F));

        ModelPartData bone194 = plinth_supports2.addChild("bone194", ModelPartBuilder.create(),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -2.0944F, 0.0F));

        ModelPartData bone195 = bone194.addChild("bone195",
                ModelPartBuilder.create().uv(7, 15).cuboid(-0.499F, -3.0F, 4.5F, 1.0F, 3.0F, 2.0F, new Dilation(0.025F))
                        .uv(62, 50).cuboid(-0.499F, 0.0F, -0.5F, 1.0F, 1.0F, 7.0F, new Dilation(0.025F)),
                ModelTransform.of(0.0F, 0.0F, 5.863F, -0.7854F, 0.0F, 0.0F));

        ModelPartData bone196 = bone194.addChild("bone196", ModelPartBuilder.create(),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -2.0944F, 0.0F));

        ModelPartData bone197 = bone196.addChild("bone197",
                ModelPartBuilder.create().uv(7, 15).cuboid(-0.499F, -3.0F, 4.5F, 1.0F, 3.0F, 2.0F, new Dilation(0.025F))
                        .uv(62, 50).cuboid(-0.499F, 0.0F, -0.5F, 1.0F, 1.0F, 7.0F, new Dilation(0.025F)),
                ModelTransform.of(0.0F, 0.0F, 5.863F, -0.7854F, 0.0F, 0.0F));

        ModelPartData bone = plinth.addChild("bone",
                ModelPartBuilder.create().uv(52, 30).cuboid(-3.5F, -1.0F, 0.06F, 7.0F, 1.0F, 7.0F, new Dilation(0.0F)),
                ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData bone2 = bone.addChild("bone2",
                ModelPartBuilder.create().uv(52, 30).cuboid(-3.5F, -1.0F, 0.06F, 7.0F, 1.0F, 7.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone3 = bone2.addChild("bone3",
                ModelPartBuilder.create().uv(52, 30).cuboid(-3.5F, -1.0F, 0.06F, 7.0F, 1.0F, 7.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone4 = bone3.addChild("bone4",
                ModelPartBuilder.create().uv(52, 30).cuboid(-3.5F, -1.0F, 0.06F, 7.0F, 1.0F, 7.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone5 = bone4.addChild("bone5",
                ModelPartBuilder.create().uv(52, 30).cuboid(-3.5F, -1.0F, 0.06F, 7.0F, 1.0F, 7.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone6 = bone5.addChild("bone6",
                ModelPartBuilder.create().uv(52, 30).cuboid(-3.5F, -1.0F, 0.06F, 7.0F, 1.0F, 7.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone7 = plinth.addChild("bone7",
                ModelPartBuilder.create().uv(0, 56).cuboid(-0.5F, -1.0F, 6.863F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -0.5236F, 0.0F));

        ModelPartData bone8 = bone7.addChild("bone8",
                ModelPartBuilder.create().uv(0, 56).cuboid(-0.5F, -1.0F, 6.863F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone9 = bone8.addChild("bone9",
                ModelPartBuilder.create().uv(0, 56).cuboid(-0.5F, -1.0F, 6.863F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone10 = bone9.addChild("bone10",
                ModelPartBuilder.create().uv(0, 56).cuboid(-0.5F, -1.0F, 6.863F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone11 = bone10.addChild("bone11",
                ModelPartBuilder.create().uv(0, 56).cuboid(-0.5F, -1.0F, 6.863F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone12 = bone11.addChild("bone12",
                ModelPartBuilder.create().uv(0, 56).cuboid(-0.5F, -1.0F, 6.863F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone13 = plinth.addChild("bone13", ModelPartBuilder.create().uv(49, 15).cuboid(-3.0F, -3.0F,
                -0.815F, 6.0F, 3.0F, 7.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -1.0F, 0.0F));

        ModelPartData bone14 = bone13.addChild("bone14", ModelPartBuilder.create().uv(49, 15).cuboid(-3.0F, -3.0F,
                -0.815F, 6.0F, 3.0F, 7.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone15 = bone14.addChild("bone15", ModelPartBuilder.create().uv(49, 15).cuboid(-3.0F, -3.0F,
                -0.815F, 6.0F, 3.0F, 7.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone16 = bone15.addChild("bone16", ModelPartBuilder.create().uv(49, 15).cuboid(-3.0F, -3.0F,
                -0.815F, 6.0F, 3.0F, 7.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone17 = bone16.addChild("bone17", ModelPartBuilder.create().uv(49, 15).cuboid(-3.0F, -3.0F,
                -0.815F, 6.0F, 3.0F, 7.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone18 = bone17.addChild("bone18", ModelPartBuilder.create().uv(49, 15).cuboid(-3.0F, -3.0F,
                -0.815F, 6.0F, 3.0F, 7.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone89 = plinth.addChild("bone89", ModelPartBuilder.create().uv(0, 42).cuboid(-3.0F, -10.0F,
                -0.815F, 6.0F, 7.0F, 7.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -54.0F, 0.0F));

        ModelPartData bone90 = bone89.addChild("bone90", ModelPartBuilder.create().uv(0, 42).cuboid(-3.0F, -10.0F,
                -0.815F, 6.0F, 7.0F, 7.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone91 = bone90.addChild("bone91", ModelPartBuilder.create().uv(0, 42).cuboid(-3.0F, -10.0F,
                -0.815F, 6.0F, 7.0F, 7.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone92 = bone91.addChild("bone92", ModelPartBuilder.create().uv(0, 42).cuboid(-3.0F, -10.0F,
                -0.815F, 6.0F, 7.0F, 7.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone93 = bone92.addChild("bone93", ModelPartBuilder.create().uv(0, 42).cuboid(-3.0F, -10.0F,
                -0.815F, 6.0F, 7.0F, 7.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone94 = bone93.addChild("bone94", ModelPartBuilder.create().uv(0, 42).cuboid(-3.0F, -10.0F,
                -0.815F, 6.0F, 7.0F, 7.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone137 = plinth.addChild("bone137", ModelPartBuilder.create().uv(57, 38).cuboid(-3.0F, -5.0F,
                -0.815F, 6.0F, 2.0F, 7.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -49.0F, 0.0F));

        ModelPartData bone138 = bone137.addChild("bone138", ModelPartBuilder.create().uv(57, 38).cuboid(-3.0F, -5.0F,
                -0.815F, 6.0F, 2.0F, 7.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone139 = bone138.addChild("bone139", ModelPartBuilder.create().uv(57, 38).cuboid(-3.0F, -5.0F,
                -0.815F, 6.0F, 2.0F, 7.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone140 = bone139.addChild("bone140", ModelPartBuilder.create().uv(57, 38).cuboid(-3.0F, -5.0F,
                -0.815F, 6.0F, 2.0F, 7.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone141 = bone140.addChild("bone141", ModelPartBuilder.create().uv(57, 38).cuboid(-3.0F, -5.0F,
                -0.815F, 6.0F, 2.0F, 7.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone142 = bone141.addChild("bone142", ModelPartBuilder.create().uv(57, 38).cuboid(-3.0F, -5.0F,
                -0.815F, 6.0F, 2.0F, 7.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone19 = plinth.addChild("bone19",
                ModelPartBuilder.create().uv(0, 15).cuboid(-0.5F, -3.0F, 5.863F, 1.0F, 3.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, -1.0F, 0.0F, 0.0F, -0.5236F, 0.0F));

        ModelPartData bone20 = bone19.addChild("bone20",
                ModelPartBuilder.create().uv(0, 15).cuboid(-0.5F, -3.0F, 5.863F, 1.0F, 3.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone21 = bone20.addChild("bone21",
                ModelPartBuilder.create().uv(0, 15).cuboid(-0.5F, -3.0F, 5.863F, 1.0F, 3.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone22 = bone21.addChild("bone22",
                ModelPartBuilder.create().uv(0, 15).cuboid(-0.5F, -3.0F, 5.863F, 1.0F, 3.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone23 = bone22.addChild("bone23",
                ModelPartBuilder.create().uv(0, 15).cuboid(-0.5F, -3.0F, 5.863F, 1.0F, 3.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone24 = bone23.addChild("bone24",
                ModelPartBuilder.create().uv(0, 15).cuboid(-0.5F, -3.0F, 5.863F, 1.0F, 3.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone149 = plinth.addChild("bone149",
                ModelPartBuilder.create().uv(49, 18).cuboid(-0.5F, -2.0F, 5.863F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, -52.0F, 0.0F, 0.0F, -0.5236F, 0.0F));

        ModelPartData bone150 = bone149.addChild("bone150",
                ModelPartBuilder.create().uv(49, 18).cuboid(-0.5F, -2.0F, 5.863F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone151 = bone150.addChild("bone151",
                ModelPartBuilder.create().uv(49, 18).cuboid(-0.5F, -2.0F, 5.863F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone152 = bone151.addChild("bone152",
                ModelPartBuilder.create().uv(49, 18).cuboid(-0.5F, -2.0F, 5.863F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone153 = bone152.addChild("bone153",
                ModelPartBuilder.create().uv(49, 18).cuboid(-0.5F, -2.0F, 5.863F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone154 = bone153.addChild("bone154",
                ModelPartBuilder.create().uv(49, 18).cuboid(-0.5F, -2.0F, 5.863F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone155 = plinth.addChild("bone155",
                ModelPartBuilder.create().uv(45, 30).cuboid(-0.5F, -7.0F, 5.863F, 1.0F, 7.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, -57.0F, 0.0F, 0.0F, -0.5236F, 0.0F));

        ModelPartData bone156 = bone155.addChild("bone156",
                ModelPartBuilder.create().uv(45, 30).cuboid(-0.5F, -7.0F, 5.863F, 1.0F, 7.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone157 = bone156.addChild("bone157",
                ModelPartBuilder.create().uv(45, 30).cuboid(-0.5F, -7.0F, 5.863F, 1.0F, 7.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone158 = bone157.addChild("bone158",
                ModelPartBuilder.create().uv(45, 30).cuboid(-0.5F, -7.0F, 5.863F, 1.0F, 7.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone159 = bone158.addChild("bone159",
                ModelPartBuilder.create().uv(45, 30).cuboid(-0.5F, -7.0F, 5.863F, 1.0F, 7.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone160 = bone159.addChild("bone160",
                ModelPartBuilder.create().uv(45, 30).cuboid(-0.5F, -7.0F, 5.863F, 1.0F, 7.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone25 = plinth.addChild("bone25",
                ModelPartBuilder.create().uv(34, 43).cuboid(-0.5F, -8.0F, 3.863F, 1.0F, 8.0F, 2.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, -4.0F, 0.0F, 0.0F, -0.5236F, 0.0F));

        ModelPartData bone26 = bone25.addChild("bone26",
                ModelPartBuilder.create().uv(34, 43).cuboid(-0.5F, -8.0F, 3.863F, 1.0F, 8.0F, 2.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone27 = bone26.addChild("bone27",
                ModelPartBuilder.create().uv(34, 43).cuboid(-0.5F, -8.0F, 3.863F, 1.0F, 8.0F, 2.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone28 = bone27.addChild("bone28",
                ModelPartBuilder.create().uv(34, 43).cuboid(-0.5F, -8.0F, 3.863F, 1.0F, 8.0F, 2.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone29 = bone28.addChild("bone29",
                ModelPartBuilder.create().uv(34, 43).cuboid(-0.5F, -8.0F, 3.863F, 1.0F, 8.0F, 2.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone30 = bone29.addChild("bone30",
                ModelPartBuilder.create().uv(34, 43).cuboid(-0.5F, -8.0F, 3.863F, 1.0F, 8.0F, 2.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone31 = plinth.addChild("bone31",
                ModelPartBuilder.create().uv(0, 0).cuboid(-2.5F, -8.0F, 3.363F, 5.0F, 8.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.pivot(0.0F, -4.0F, 0.0F));

        ModelPartData bone32 = bone31.addChild("bone32",
                ModelPartBuilder.create().uv(0, 0).cuboid(-2.5F, -8.0F, 3.363F, 5.0F, 8.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone33 = bone32.addChild("bone33",
                ModelPartBuilder.create().uv(0, 0).cuboid(-2.5F, -8.0F, 3.363F, 5.0F, 8.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone34 = bone33.addChild("bone34",
                ModelPartBuilder.create().uv(0, 0).cuboid(-2.5F, -8.0F, 3.363F, 5.0F, 8.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone35 = bone34.addChild("bone35",
                ModelPartBuilder.create().uv(0, 0).cuboid(-2.5F, -8.0F, 3.363F, 5.0F, 8.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone36 = bone35.addChild("bone36",
                ModelPartBuilder.create().uv(0, 0).cuboid(-2.5F, -8.0F, 3.363F, 5.0F, 8.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone96 = plinth.addChild("bone96",
                ModelPartBuilder.create().uv(0, 57).cuboid(-3.0F, -3.0F, -0.815F, 6.0F, 2.0F, 7.0F, new Dilation(0.0F)),
                ModelTransform.pivot(0.0F, -11.0F, 0.0F));

        ModelPartData bone97 = bone96.addChild("bone97", ModelPartBuilder.create().uv(0, 57).cuboid(-3.0F, -3.025F,
                -0.815F, 6.0F, 2.0F, 7.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone98 = bone97.addChild("bone98",
                ModelPartBuilder.create().uv(0, 57).cuboid(-3.0F, -3.0F, -0.815F, 6.0F, 2.0F, 7.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone99 = bone98.addChild("bone99", ModelPartBuilder.create().uv(0, 57).cuboid(-3.0F, -3.025F,
                -0.815F, 6.0F, 2.0F, 7.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone100 = bone99.addChild("bone100",
                ModelPartBuilder.create().uv(0, 57).cuboid(-3.0F, -3.0F, -0.815F, 6.0F, 2.0F, 7.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone101 = bone100.addChild("bone101", ModelPartBuilder.create().uv(0, 57).cuboid(-3.0F, -3.025F,
                -0.815F, 6.0F, 2.0F, 7.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone102 = plinth.addChild("bone102",
                ModelPartBuilder.create().uv(49, 15).cuboid(-0.5F, -4.0F, 5.863F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, -10.0F, 0.0F, 0.0F, -0.5236F, 0.0F));

        ModelPartData bone103 = bone102.addChild("bone103",
                ModelPartBuilder.create().uv(49, 15).cuboid(-0.5F, -4.0F, 5.863F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone104 = bone103.addChild("bone104",
                ModelPartBuilder.create().uv(49, 15).cuboid(-0.5F, -4.0F, 5.863F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone105 = bone104.addChild("bone105",
                ModelPartBuilder.create().uv(49, 15).cuboid(-0.5F, -4.0F, 5.863F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone106 = bone105.addChild("bone106",
                ModelPartBuilder.create().uv(49, 15).cuboid(-0.5F, -4.0F, 5.863F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone107 = bone106.addChild("bone107",
                ModelPartBuilder.create().uv(49, 15).cuboid(-0.5F, -4.0F, 5.863F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData panels = base_console.addChild("panels", ModelPartBuilder.create(),
                ModelTransform.pivot(0.0F, -10.0F, 0.0F));

        ModelPartData bone68 = panels
                .addChild(
                        "bone68", ModelPartBuilder.create().uv(0, 30).cuboid(-7.0F, 0.025F, -10.925F, 14.0F, 1.0F,
                                11.0F, new Dilation(0.0F)),
                        ModelTransform.of(0.0F, -6.0F, 13.6987F, -0.6807F, 0.0F, 0.0F));

        ModelPartData bone69 = bone68.addChild("bone69",
                ModelPartBuilder.create().uv(28, 45).cuboid(-1.0F, 0.0F, -4.0F, 1.0F, 1.0F, 12.0F, new Dilation(0.0F)),
                ModelTransform.of(4.6219F, 0.0055F, -7.2982F, 0.0F, 0.4232F, 0.0F));

        ModelPartData bone70 = bone68.addChild("bone70",
                ModelPartBuilder.create().uv(14, 44).cuboid(0.0F, 0.0F, -4.0F, 1.0F, 1.0F, 12.0F, new Dilation(0.0F)),
                ModelTransform.of(-4.6219F, 0.0055F, -7.2982F, 0.0F, -0.4232F, 0.0F));

        ModelPartData bone52 = panels.addChild("bone52", ModelPartBuilder.create(),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone49 = bone52
                .addChild(
                        "bone49", ModelPartBuilder.create().uv(0, 30).cuboid(-7.0F, 0.025F, -10.925F, 14.0F, 1.0F,
                                11.0F, new Dilation(0.0F)),
                        ModelTransform.of(0.0F, -6.0F, 13.6987F, -0.6807F, 0.0F, 0.0F));

        ModelPartData bone50 = bone49.addChild("bone50",
                ModelPartBuilder.create().uv(28, 45).cuboid(-1.0F, 0.0F, -4.0F, 1.0F, 1.0F, 12.0F, new Dilation(0.0F)),
                ModelTransform.of(4.6219F, 0.0055F, -7.2982F, 0.0F, 0.4232F, 0.0F));

        ModelPartData bone51 = bone49.addChild("bone51",
                ModelPartBuilder.create().uv(14, 44).cuboid(0.0F, 0.0F, -4.0F, 1.0F, 1.0F, 12.0F, new Dilation(0.0F)),
                ModelTransform.of(-4.6219F, 0.0055F, -7.2982F, 0.0F, -0.4232F, 0.0F));

        ModelPartData bone53 = bone52.addChild("bone53", ModelPartBuilder.create(),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone54 = bone53
                .addChild(
                        "bone54", ModelPartBuilder.create().uv(0, 30).cuboid(-7.0F, 0.025F, -10.925F, 14.0F, 1.0F,
                                11.0F, new Dilation(0.0F)),
                        ModelTransform.of(0.0F, -6.0F, 13.6987F, -0.6807F, 0.0F, 0.0F));

        ModelPartData bone55 = bone54.addChild("bone55",
                ModelPartBuilder.create().uv(28, 45).cuboid(-1.0F, 0.0F, -4.0F, 1.0F, 1.0F, 12.0F, new Dilation(0.0F)),
                ModelTransform.of(4.6219F, 0.0055F, -7.2982F, 0.0F, 0.4232F, 0.0F));

        ModelPartData bone56 = bone54.addChild("bone56",
                ModelPartBuilder.create().uv(14, 44).cuboid(0.0F, 0.0F, -4.0F, 1.0F, 1.0F, 12.0F, new Dilation(0.0F)),
                ModelTransform.of(-4.6219F, 0.0055F, -7.2982F, 0.0F, -0.4232F, 0.0F));

        ModelPartData bone57 = bone53.addChild("bone57", ModelPartBuilder.create(),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone58 = bone57
                .addChild(
                        "bone58", ModelPartBuilder.create().uv(0, 30).cuboid(-7.0F, 0.025F, -10.925F, 14.0F, 1.0F,
                                11.0F, new Dilation(0.0F)),
                        ModelTransform.of(0.0F, -6.0F, 13.6987F, -0.6807F, 0.0F, 0.0F));

        ModelPartData bone59 = bone58.addChild("bone59",
                ModelPartBuilder.create().uv(28, 45).cuboid(-1.0F, 0.0F, -4.0F, 1.0F, 1.0F, 12.0F, new Dilation(0.0F)),
                ModelTransform.of(4.6219F, 0.0055F, -7.2982F, 0.0F, 0.4232F, 0.0F));

        ModelPartData bone60 = bone58.addChild("bone60",
                ModelPartBuilder.create().uv(14, 44).cuboid(0.0F, 0.0F, -4.0F, 1.0F, 1.0F, 12.0F, new Dilation(0.0F)),
                ModelTransform.of(-4.6219F, 0.0055F, -7.2982F, 0.0F, -0.4232F, 0.0F));

        ModelPartData bone61 = bone57.addChild("bone61", ModelPartBuilder.create(),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone62 = bone61
                .addChild(
                        "bone62", ModelPartBuilder.create().uv(0, 30).cuboid(-7.0F, 0.025F, -10.925F, 14.0F, 1.0F,
                                11.0F, new Dilation(0.0F)),
                        ModelTransform.of(0.0F, -6.0F, 13.6987F, -0.6807F, 0.0F, 0.0F));

        ModelPartData bone63 = bone62.addChild("bone63",
                ModelPartBuilder.create().uv(28, 45).cuboid(-1.0F, 0.0F, -4.0F, 1.0F, 1.0F, 12.0F, new Dilation(0.0F)),
                ModelTransform.of(4.6219F, 0.0055F, -7.2982F, 0.0F, 0.4232F, 0.0F));

        ModelPartData bone64 = bone62.addChild("bone64",
                ModelPartBuilder.create().uv(14, 44).cuboid(0.0F, 0.0F, -4.0F, 1.0F, 1.0F, 12.0F, new Dilation(0.0F)),
                ModelTransform.of(-4.6219F, 0.0055F, -7.2982F, 0.0F, -0.4232F, 0.0F));

        ModelPartData bone65 = bone61.addChild("bone65", ModelPartBuilder.create(),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone66 = bone65
                .addChild(
                        "bone66", ModelPartBuilder.create().uv(0, 30).cuboid(-7.0F, 0.025F, -10.925F, 14.0F, 1.0F,
                                11.0F, new Dilation(0.0F)),
                        ModelTransform.of(0.0F, -6.0F, 13.6987F, -0.6807F, 0.0F, 0.0F));

        ModelPartData bone67 = bone66.addChild("bone67",
                ModelPartBuilder.create().uv(28, 45).cuboid(-1.0F, 0.0F, -4.0F, 1.0F, 1.0F, 12.0F, new Dilation(0.0F)),
                ModelTransform.of(4.6219F, 0.0055F, -7.2982F, 0.0F, 0.4232F, 0.0F));

        ModelPartData bone95 = bone66.addChild("bone95",
                ModelPartBuilder.create().uv(14, 44).cuboid(0.0F, 0.0F, -4.0F, 1.0F, 1.0F, 12.0F, new Dilation(0.0F)),
                ModelTransform.of(-4.6219F, 0.0055F, -7.2982F, 0.0F, -0.4232F, 0.0F));

        ModelPartData bone209 = base_console.addChild("bone209", ModelPartBuilder.create(),
                ModelTransform.pivot(0.0F, -1.0F, 0.0F));

        ModelPartData bone43 = bone209.addChild("bone43", ModelPartBuilder.create(),
                ModelTransform.pivot(0.0F, -9.0F, 0.0F));

        ModelPartData bone71 = bone43.addChild("bone71",
                ModelPartBuilder.create().uv(0, 0).cuboid(-10.0F, 0.0F, -13.0F, 20.0F, 2.0F, 13.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, -6.0F, 18.316F, 0.0044F, 0.0F, 0.0F));

        ModelPartData bone38 = bone43.addChild("bone38", ModelPartBuilder.create(),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone72 = bone38.addChild("bone72",
                ModelPartBuilder.create().uv(0, 0).cuboid(-10.0F, 0.0F, -13.0F, 20.0F, 2.0F, 13.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, -6.0F, 18.316F, 0.0044F, 0.0F, 0.0F));

        ModelPartData bone39 = bone38.addChild("bone39", ModelPartBuilder.create(),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone40 = bone39.addChild("bone40",
                ModelPartBuilder.create().uv(0, 0).cuboid(-10.0F, 0.0F, -13.0F, 20.0F, 2.0F, 13.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, -6.0F, 18.316F, 0.0044F, 0.0F, 0.0F));

        ModelPartData bone41 = bone39.addChild("bone41", ModelPartBuilder.create(),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone42 = bone41.addChild("bone42",
                ModelPartBuilder.create().uv(0, 0).cuboid(-10.0F, 0.0F, -13.0F, 20.0F, 2.0F, 13.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, -6.0F, 18.316F, 0.0044F, 0.0F, 0.0F));

        ModelPartData bone73 = bone41.addChild("bone73", ModelPartBuilder.create(),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone74 = bone73.addChild("bone74",
                ModelPartBuilder.create().uv(0, 0).cuboid(-10.0F, 0.0F, -13.0F, 20.0F, 2.0F, 13.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, -6.0F, 18.316F, 0.0044F, 0.0F, 0.0F));

        ModelPartData bone75 = bone73.addChild("bone75", ModelPartBuilder.create(),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone76 = bone75.addChild("bone76",
                ModelPartBuilder.create().uv(0, 0).cuboid(-10.0F, 0.0F, -13.0F, 20.0F, 2.0F, 13.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, -6.0F, 18.316F, 0.0044F, 0.0F, 0.0F));

        ModelPartData bone181 = bone209.addChild("bone181", ModelPartBuilder.create(),
                ModelTransform.pivot(0.0F, -8.0F, 0.0F));

        ModelPartData bone182 = bone181.addChild("bone182", ModelPartBuilder.create().uv(0, 15).cuboid(-9.0F, -1.0F,
                -13.0F, 18.0F, 2.0F, 13.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, -5.0F, 16.316F, 0.2182F, 0.0F, 0.0F));

        ModelPartData bone183 = bone181.addChild("bone183", ModelPartBuilder.create(),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone193 = bone183.addChild("bone193", ModelPartBuilder.create().uv(0, 15).cuboid(-9.0F, -1.0F,
                -13.0F, 18.0F, 2.0F, 13.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, -5.0F, 16.316F, 0.2182F, 0.0F, 0.0F));

        ModelPartData bone184 = bone183.addChild("bone184", ModelPartBuilder.create(),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone185 = bone184.addChild("bone185", ModelPartBuilder.create().uv(0, 15).cuboid(-9.0F, -1.0F,
                -13.0F, 18.0F, 2.0F, 13.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, -5.0F, 16.316F, 0.2182F, 0.0F, 0.0F));

        ModelPartData bone186 = bone184.addChild("bone186", ModelPartBuilder.create(),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone187 = bone186.addChild("bone187", ModelPartBuilder.create().uv(0, 15).cuboid(-9.0F, -1.0F,
                -13.0F, 18.0F, 2.0F, 13.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, -5.0F, 16.316F, 0.2182F, 0.0F, 0.0F));

        ModelPartData bone188 = bone186.addChild("bone188", ModelPartBuilder.create(),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone189 = bone188.addChild("bone189", ModelPartBuilder.create().uv(0, 15).cuboid(-9.0F, -1.0F,
                -13.0F, 18.0F, 2.0F, 13.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, -5.0F, 16.316F, 0.2182F, 0.0F, 0.0F));

        ModelPartData bone190 = bone188.addChild("bone190", ModelPartBuilder.create(),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone191 = bone190.addChild("bone191", ModelPartBuilder.create().uv(0, 15).cuboid(-9.0F, -1.0F,
                -13.0F, 18.0F, 2.0F, 13.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, -5.0F, 16.316F, 0.2182F, 0.0F, 0.0F));

        ModelPartData bone37 = bone209.addChild("bone37", ModelPartBuilder.create().uv(45, 38).cuboid(-0.5F, -6.0F,
                19.863F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, -9.0F, 0.0F, 0.0F, -0.5236F, 0.0F));

        ModelPartData bone44 = bone37.addChild("bone44", ModelPartBuilder.create().uv(45, 38).cuboid(-0.5F, -6.0F,
                19.863F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone45 = bone44.addChild("bone45", ModelPartBuilder.create().uv(45, 38).cuboid(-0.5F, -6.0F,
                19.863F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone46 = bone45.addChild("bone46", ModelPartBuilder.create().uv(45, 38).cuboid(-0.5F, -6.0F,
                19.863F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone47 = bone46.addChild("bone47", ModelPartBuilder.create().uv(45, 38).cuboid(-0.5F, -6.0F,
                19.863F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone48 = bone47.addChild("bone48", ModelPartBuilder.create().uv(45, 38).cuboid(-0.5F, -6.0F,
                19.863F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData centre_pillar = base_console.addChild("centre_pillar", ModelPartBuilder.create(),
                ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData bone119 = centre_pillar.addChild("bone119", ModelPartBuilder.create().uv(53, 0).cuboid(-3.0F,
                -2.975F, -0.815F, 6.0F, 2.0F, 7.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -20.0F, 0.0F));

        ModelPartData bone120 = bone119.addChild("bone120", ModelPartBuilder.create().uv(53, 0).cuboid(-3.0F, -2.975F,
                -0.815F, 6.0F, 2.0F, 7.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone121 = bone120.addChild("bone121", ModelPartBuilder.create().uv(53, 0).cuboid(-3.0F, -2.975F,
                -0.815F, 6.0F, 2.0F, 7.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone122 = bone121.addChild("bone122", ModelPartBuilder.create().uv(53, 0).cuboid(-3.0F, -2.975F,
                -0.815F, 6.0F, 2.0F, 7.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone123 = bone122.addChild("bone123", ModelPartBuilder.create().uv(53, 0).cuboid(-3.0F, -2.975F,
                -0.815F, 6.0F, 2.0F, 7.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone124 = bone123.addChild("bone124", ModelPartBuilder.create().uv(53, 0).cuboid(-3.0F, -2.975F,
                -0.815F, 6.0F, 2.0F, 7.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone77 = centre_pillar.addChild("bone77", ModelPartBuilder.create().uv(26, 58).cuboid(-2.0F,
                -2.975F, -2.35F, 4.0F, 2.0F, 7.0F, new Dilation(0.25F)), ModelTransform.pivot(0.0F, -21.75F, 0.0F));

        ModelPartData bone78 = bone77.addChild("bone78", ModelPartBuilder.create().uv(26, 58).cuboid(-2.0F, -2.975F,
                -2.35F, 4.0F, 2.0F, 7.0F, new Dilation(0.25F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone79 = bone78.addChild("bone79", ModelPartBuilder.create().uv(26, 58).cuboid(-2.0F, -2.975F,
                -2.35F, 4.0F, 2.0F, 7.0F, new Dilation(0.25F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone80 = bone79.addChild("bone80", ModelPartBuilder.create().uv(26, 58).cuboid(-2.0F, -2.975F,
                -2.35F, 4.0F, 2.0F, 7.0F, new Dilation(0.25F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone81 = bone80.addChild("bone81", ModelPartBuilder.create().uv(26, 58).cuboid(-2.0F, -2.975F,
                -2.35F, 4.0F, 2.0F, 7.0F, new Dilation(0.25F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone82 = bone81.addChild("bone82", ModelPartBuilder.create().uv(26, 58).cuboid(-2.0F, -2.975F,
                -2.35F, 4.0F, 2.0F, 7.0F, new Dilation(0.25F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone131 = centre_pillar.addChild("bone131", ModelPartBuilder.create().uv(47, 51).cuboid(-2.0F,
                -8.0F, -2.35F, 4.0F, 7.0F, 7.0F, new Dilation(0.25F)), ModelTransform.pivot(0.0F, -49.25F, 0.0F));

        ModelPartData bone132 = bone131.addChild("bone132", ModelPartBuilder.create().uv(47, 51).cuboid(-2.0F, -8.0F,
                -2.35F, 4.0F, 7.0F, 7.0F, new Dilation(0.25F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone133 = bone132.addChild("bone133", ModelPartBuilder.create().uv(47, 51).cuboid(-2.0F, -8.0F,
                -2.35F, 4.0F, 7.0F, 7.0F, new Dilation(0.25F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone134 = bone133.addChild("bone134", ModelPartBuilder.create().uv(47, 51).cuboid(-2.0F, -8.0F,
                -2.35F, 4.0F, 7.0F, 7.0F, new Dilation(0.25F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone135 = bone134.addChild("bone135", ModelPartBuilder.create().uv(47, 51).cuboid(-2.0F, -8.0F,
                -2.35F, 4.0F, 7.0F, 7.0F, new Dilation(0.25F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone136 = bone135.addChild("bone136", ModelPartBuilder.create().uv(47, 51).cuboid(-2.0F, -8.0F,
                -2.35F, 4.0F, 7.0F, 7.0F, new Dilation(0.25F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone125 = centre_pillar.addChild("bone125",
                ModelPartBuilder.create().uv(6, 38).cuboid(-0.5F, -4.0F, 5.863F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, -19.0F, 0.0F, 0.0F, -0.5236F, 0.0F));

        ModelPartData bone126 = bone125.addChild("bone126",
                ModelPartBuilder.create().uv(6, 38).cuboid(-0.5F, -4.0F, 5.863F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone127 = bone126.addChild("bone127",
                ModelPartBuilder.create().uv(6, 38).cuboid(-0.5F, -4.0F, 5.863F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone128 = bone127.addChild("bone128",
                ModelPartBuilder.create().uv(6, 38).cuboid(-0.5F, -4.0F, 5.863F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone129 = bone128.addChild("bone129",
                ModelPartBuilder.create().uv(6, 38).cuboid(-0.5F, -4.0F, 5.863F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone130 = bone129.addChild("bone130",
                ModelPartBuilder.create().uv(6, 38).cuboid(-0.5F, -4.0F, 5.863F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone83 = centre_pillar.addChild("bone83",
                ModelPartBuilder.create().uv(9, 21).cuboid(-0.5F, -6.0F, 4.368F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, -19.0F, 0.0F, 0.0F, -0.5236F, 0.0F));

        ModelPartData bone84 = bone83.addChild("bone84",
                ModelPartBuilder.create().uv(9, 21).cuboid(-0.5F, -6.0F, 4.368F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone85 = bone84.addChild("bone85",
                ModelPartBuilder.create().uv(9, 21).cuboid(-0.5F, -6.0F, 4.368F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone86 = bone85.addChild("bone86",
                ModelPartBuilder.create().uv(9, 21).cuboid(-0.5F, -6.0F, 4.368F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone87 = bone86.addChild("bone87",
                ModelPartBuilder.create().uv(9, 21).cuboid(-0.5F, -6.0F, 4.368F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone88 = bone87.addChild("bone88",
                ModelPartBuilder.create().uv(9, 21).cuboid(-0.5F, -6.0F, 4.368F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone143 = centre_pillar.addChild("bone143",
                ModelPartBuilder.create().uv(6, 30).cuboid(-0.5F, -11.0F, 4.368F, 1.0F, 7.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, -46.0F, 0.0F, 0.0F, -0.5236F, 0.0F));

        ModelPartData bone144 = bone143.addChild("bone144",
                ModelPartBuilder.create().uv(6, 30).cuboid(-0.5F, -11.0F, 4.368F, 1.0F, 7.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone145 = bone144.addChild("bone145",
                ModelPartBuilder.create().uv(6, 30).cuboid(-0.5F, -11.0F, 4.368F, 1.0F, 7.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone146 = bone145.addChild("bone146",
                ModelPartBuilder.create().uv(6, 30).cuboid(-0.5F, -11.0F, 4.368F, 1.0F, 7.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone147 = bone146.addChild("bone147",
                ModelPartBuilder.create().uv(6, 30).cuboid(-0.5F, -11.0F, 4.368F, 1.0F, 7.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone148 = bone147.addChild("bone148",
                ModelPartBuilder.create().uv(6, 30).cuboid(-0.5F, -11.0F, 4.368F, 1.0F, 7.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData controls = console.addChild("controls", ModelPartBuilder.create(),
                ModelTransform.pivot(0.0F, -10.0F, 0.0F));

        ModelPartData north = controls.addChild("north", ModelPartBuilder.create(),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 3.1416F, 0.0F));

        ModelPartData bone198 = north.addChild("bone198",
                ModelPartBuilder.create().uv(67, 69)
                        .cuboid(-1.5F, -5.9618F, 5.182F, 3.0F, 1.0F, 3.0F, new Dilation(0.0F)).uv(0, 25)
                        .cuboid(-2.0F, -5.8868F, 2.932F, 4.0F, 1.0F, 2.0F, new Dilation(0.0F)).uv(55, 73)
                        .cuboid(-1.0F, -6.2118F, 5.682F, 2.0F, 1.0F, 2.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, -9.51F, 0.0F, -0.6807F, 0.0F, 0.0F));

        ModelPartData control = bone198.addChild("control", ModelPartBuilder.create().uv(49, 25).cuboid(-4.0F, -5.475F,
                11.8487F, 6.0F, 1.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(1.0F, -0.8868F, -1.4167F));

        ModelPartData bone202 = control.addChild("bone202", ModelPartBuilder.create(),
                ModelTransform.of(-0.5F, -5.225F, 12.3487F, 0.6807F, 0.0F, 0.0F));

        ModelPartData bone213 = bone202.addChild("bone213",
                ModelPartBuilder.create().uv(27, 84).cuboid(0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.pivot(1.0F, -0.3F, 0.0F));

        ModelPartData bone215 = bone202.addChild("bone215",
                ModelPartBuilder.create().uv(49, 85).cuboid(0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.pivot(0.0F, -0.3F, 0.0F));

        ModelPartData bone220 = bone202.addChild("bone220",
                ModelPartBuilder.create().uv(27, 84).cuboid(0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.pivot(-3.0F, -0.3F, 0.0F));

        ModelPartData bone222 = bone202.addChild("bone222",
                ModelPartBuilder.create().uv(49, 85).cuboid(0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.pivot(-1.5F, -0.3F, 0.0F));

        ModelPartData control2 = bone198.addChild("control2",
                ModelPartBuilder.create().uv(55, 33).cuboid(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.pivot(3.6F, -5.9618F, 8.432F));

        ModelPartData control3 = bone198.addChild("control3", ModelPartBuilder.create().uv(55, 33).cuboid(-0.5F,
                -4.575F, 7.5987F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.pivot(-3.6F, -1.8868F, 0.3333F));

        ModelPartData control4 = bone198.addChild("control4", ModelPartBuilder.create().uv(55, 35).cuboid(-2.5F,
                -5.325F, 8.5987F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.pivot(2.0F, -1.6368F, -2.4167F));

        ModelPartData bone199 = bone198.addChild("bone199",
                ModelPartBuilder.create().uv(73, 33)
                        .cuboid(-0.5F, -8.745F, 11.035F, 2.0F, 1.0F, 2.0F, new Dilation(0.0F)).uv(24, 67)
                        .cuboid(-0.75F, -8.72F, 9.535F, 2.0F, 1.0F, 5.0F, new Dilation(0.0F)).uv(44, 72)
                        .cuboid(1.5F, -8.72F, 10.785F, 1.0F, 1.0F, 3.0F, new Dilation(0.0F)).uv(72, 0)
                        .cuboid(-1.5F, -8.72F, 10.785F, 1.0F, 1.0F, 3.0F, new Dilation(0.0F)),
                ModelTransform.pivot(3.1F, 2.7832F, -3.603F));

        ModelPartData bone200 = bone198.addChild("bone200",
                ModelPartBuilder.create().uv(73, 33)
                        .cuboid(-0.5F, -8.745F, 11.035F, 2.0F, 1.0F, 2.0F, new Dilation(0.0F)).uv(24, 67)
                        .cuboid(-0.75F, -8.72F, 9.535F, 2.0F, 1.0F, 5.0F, new Dilation(0.0F)).uv(44, 72)
                        .cuboid(1.5F, -8.72F, 10.785F, 1.0F, 1.0F, 3.0F, new Dilation(0.0F)).uv(72, 0)
                        .cuboid(-1.5F, -8.72F, 10.785F, 1.0F, 1.0F, 3.0F, new Dilation(0.0F)),
                ModelTransform.pivot(-4.1F, 2.7832F, -3.603F));

        ModelPartData north_left = controls.addChild("north_left", ModelPartBuilder.create(),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 2.0944F, 0.0F));

        ModelPartData bone210 = north_left.addChild("bone210",
                ModelPartBuilder.create().uv(73, 30)
                        .cuboid(-1.0F, -6.8868F, 3.682F, 2.0F, 1.0F, 2.0F, new Dilation(0.0F)).uv(53, 9)
                        .cuboid(-4.0F, -5.8868F, 6.182F, 8.0F, 1.0F, 3.0F, new Dilation(0.0F)).uv(69, 58)
                        .cuboid(0.0F, -5.8868F, 9.432F, 3.0F, 1.0F, 3.0F, new Dilation(0.0F)).uv(45, 65)
                        .cuboid(-6.0F, -6.1368F, 9.432F, 6.0F, 1.0F, 3.0F, new Dilation(0.0F)).uv(49, 72)
                        .cuboid(3.0F, -6.3868F, 9.932F, 2.0F, 1.0F, 2.0F, new Dilation(0.0F)).uv(55, 31)
                        .cuboid(-2.0F, -6.1368F, 10.432F, 1.0F, 1.0F, 1.0F, new Dilation(0.25F)).uv(53, 2)
                        .cuboid(-2.5F, -6.6368F, 4.182F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)).uv(53, 2)
                        .cuboid(1.5F, -6.6368F, 4.182F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, -9.51F, 0.0F, -0.6807F, 0.0F, 0.0F));

        ModelPartData control5 = bone210.addChild("control5", ModelPartBuilder.create().uv(53, 4).cuboid(-3.5F, -5.0F,
                10.5987F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -1.6368F, -3.4167F));

        ModelPartData control6 = bone210.addChild("control6", ModelPartBuilder.create().uv(34, 53).cuboid(1.5F, -5.25F,
                9.5987F, 1.0F, 1.0F, 1.0F, new Dilation(-0.25F)), ModelTransform.pivot(-3.5F, -1.7868F, 0.8333F));

        ModelPartData control7 = bone210.addChild("control7",
                ModelPartBuilder.create().uv(52, 30).cuboid(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(4.0F, -6.3868F, 10.932F, 0.0F, -0.7854F, 0.0F));

        ModelPartData control8 = bone210.addChild("control8",
                ModelPartBuilder.create().uv(78, 1).cuboid(3.25F, -5.0F, 7.5987F, 1.0F, 1.0F, 3.0F, new Dilation(0.0F)),
                ModelTransform.pivot(-1.0F, -0.9118F, -1.4167F));

        ModelPartData south = controls.addChild("south", ModelPartBuilder.create(),
                ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData bone214 = south.addChild("bone214",
                ModelPartBuilder.create().uv(49, 72)
                        .cuboid(3.5F, -6.3868F, 10.432F, 2.0F, 1.0F, 2.0F, new Dilation(0.0F)).uv(49, 72).mirrored()
                        .cuboid(-5.5F, -6.3868F, 10.432F, 2.0F, 1.0F, 2.0F, new Dilation(0.0F)).mirrored(false)
                        .uv(63, 65).cuboid(-2.5F, -6.1368F, 8.932F, 5.0F, 1.0F, 3.0F, new Dilation(0.0F)).uv(19, 42)
                        .cuboid(-2.0F, -5.8868F, 7.432F, 4.0F, 1.0F, 1.0F, new Dilation(0.0F)).uv(61, 73)
                        .cuboid(-5.25F, -5.8868F, 8.182F, 3.0F, 1.0F, 1.0F, new Dilation(0.0F)).uv(29, 73)
                        .cuboid(2.75F, -5.8868F, 8.182F, 3.0F, 1.0F, 1.0F, new Dilation(0.0F)).uv(42, 45)
                        .cuboid(-2.5F, -5.8868F, 5.932F, 5.0F, 1.0F, 1.0F, new Dilation(0.0F)).uv(19, 46)
                        .cuboid(-1.0F, -5.8868F, 4.182F, 2.0F, 1.0F, 1.0F, new Dilation(0.25F)),
                ModelTransform.of(0.0F, -9.51F, 0.0F, -0.6807F, 0.0F, 0.0F));

        ModelPartData control_lever2 = bone214.addChild("control_lever2",
                ModelPartBuilder.create().uv(0, 45)
                        .cuboid(-1.75F, -0.625F, -0.75F, 2.0F, 1.0F, 1.0F, new Dilation(0.0F)).uv(52, 20)
                        .cuboid(-0.5F, -0.375F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.25F)),
                ModelTransform.of(-1.25F, -6.5118F, 10.182F, 0.0F, -0.6981F, 0.0F));

        ModelPartData control10 = bone214.addChild("control10",
                ModelPartBuilder.create().uv(52, 17)
                        .cuboid(2.25F, -5.75F, 7.8487F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)).uv(52, 17)
                        .cuboid(3.75F, -5.5F, 7.8487F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)).uv(52, 17)
                        .cuboid(3.75F, -5.5F, 9.3487F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.pivot(-2.5F, -1.1368F, 1.3333F));

        ModelPartData control11 = bone214.addChild("control11", ModelPartBuilder.create().uv(52, 30).cuboid(2.0F,
                -5.25F, 9.0987F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(2.0F, -1.6368F, 1.8333F));

        ModelPartData control12 = bone214.addChild("control12",
                ModelPartBuilder.create().uv(52, 30).mirrored()
                        .cuboid(-3.0F, -5.25F, 9.0987F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)).mirrored(false),
                ModelTransform.pivot(-2.0F, -1.6368F, 1.8333F));

        ModelPartData south_right = controls.addChild("south_right", ModelPartBuilder.create(),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData bone216 = south_right.addChild("bone216",
                ModelPartBuilder.create().uv(71, 49)
                        .cuboid(-4.0F, -5.8868F, 6.432F, 1.0F, 1.0F, 3.0F, new Dilation(0.0F)).uv(17, 71)
                        .cuboid(3.0F, -5.8868F, 6.432F, 1.0F, 1.0F, 3.0F, new Dilation(0.0F)).uv(72, 4)
                        .cuboid(-1.0F, -6.6118F, 4.432F, 2.0F, 1.0F, 2.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, -9.51F, 0.0F, -0.6807F, 0.0F, 0.0F));

        ModelPartData bone221 = bone216.addChild("bone221",
                ModelPartBuilder.create().uv(62, 25).cuboid(2.0F, -4.5F, 3.8487F, 5.0F, 1.0F, 4.0F, new Dilation(0.0F)),
                ModelTransform.pivot(-4.5F, -1.6368F, 3.8333F));

        ModelPartData control_lever3 = bone221.addChild("control_lever3",
                ModelPartBuilder.create().uv(32, 42).cuboid(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                        .uv(19, 44).cuboid(-1.75F, -0.75F, -0.75F, 2.0F, 1.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(3.0F, -4.5F, 6.8487F, 0.0F, 0.48F, 0.0F));

        ModelPartData control13 = bone221.addChild("control13",
                ModelPartBuilder.create().uv(0, 47).cuboid(5.25F, -5.0F, 6.3487F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                        .uv(0, 47).cuboid(4.0F, -5.0F, 6.3487F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.pivot(0.25F, 0.0F, -2.0F));

        ModelPartData control14 = bone221.addChild("control14",
                ModelPartBuilder.create().uv(0, 47).cuboid(2.5F, -5.0F, 6.3487F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                        .uv(0, 47).cuboid(4.0F, -5.0F, 6.3487F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.pivot(1.5F, 0.0F, 0.0F));

        ModelPartData bone217 = bone216.addChild("bone217",
                ModelPartBuilder.create().uv(72, 9).cuboid(-1.0F, -0.5F, -1.0F, 2.0F, 1.0F, 2.0F, new Dilation(0.0F)),
                ModelTransform.of(4.5F, -5.8868F, 10.432F, 0.0F, 0.7854F, 0.0F));

        ModelPartData bone218 = bone216.addChild("bone218",
                ModelPartBuilder.create().uv(72, 9).mirrored()
                        .cuboid(-1.0F, -0.5F, -1.0F, 2.0F, 1.0F, 2.0F, new Dilation(0.0F)).mirrored(false),
                ModelTransform.of(-4.5F, -5.8868F, 10.432F, 0.0F, -0.7854F, 0.0F));

        ModelPartData bone219 = bone216.addChild("bone219",
                ModelPartBuilder.create().uv(0, 42).cuboid(-0.5F, 0.0F, 0.0F, 1.0F, 1.0F, 2.0F, new Dilation(0.0F))
                        .uv(0, 42).cuboid(-5.5F, 0.0F, 0.0F, 1.0F, 1.0F, 2.0F, new Dilation(0.0F)),
                ModelTransform.of(2.5F, -5.8618F, 4.432F, 0.3927F, 0.0F, 0.0F));

        ModelPartData south_left = controls.addChild("south_left", ModelPartBuilder.create(),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData bone211 = south_left.addChild("bone211",
                ModelPartBuilder.create().uv(71, 53)
                        .cuboid(-1.0F, -6.6368F, 3.932F, 2.0F, 2.0F, 2.0F, new Dilation(-0.25F)).uv(71, 53)
                        .cuboid(-3.0F, -6.3868F, 3.932F, 2.0F, 2.0F, 2.0F, new Dilation(-0.25F)).uv(71, 53)
                        .cuboid(1.0F, -6.3868F, 3.932F, 2.0F, 2.0F, 2.0F, new Dilation(-0.25F)).uv(19, 57)
                        .cuboid(-0.5F, -6.1368F, 6.682F, 2.0F, 1.0F, 5.0F, new Dilation(0.0F)).uv(0, 9)
                        .cuboid(-5.5F, -6.6368F, 9.682F, 3.0F, 1.0F, 3.0F, new Dilation(0.0F)).uv(23, 73)
                        .cuboid(-5.0F, -7.6368F, 10.182F, 2.0F, 1.0F, 2.0F, new Dilation(0.0F)).uv(52, 38)
                        .cuboid(2.25F, -5.8868F, 9.432F, 3.0F, 1.0F, 3.0F, new Dilation(0.0F)).uv(42, 50)
                        .cuboid(1.75F, -5.8868F, 5.932F, 3.0F, 1.0F, 3.0F, new Dilation(0.0F)).uv(0, 21)
                        .cuboid(-4.75F, -5.8868F, 5.932F, 3.0F, 1.0F, 3.0F, new Dilation(0.0F)).uv(0, 15)
                        .cuboid(-1.5F, -5.8868F, 6.432F, 1.0F, 1.0F, 5.0F, new Dilation(0.0F)).uv(52, 34)
                        .cuboid(2.75F, -6.6368F, 9.832F, 1.0F, 1.0F, 1.0F, new Dilation(-0.25F)).uv(52, 34)
                        .cuboid(3.85F, -6.6368F, 9.832F, 1.0F, 1.0F, 1.0F, new Dilation(-0.25F)).uv(52, 34)
                        .cuboid(3.85F, -6.6368F, 10.932F, 1.0F, 1.0F, 1.0F, new Dilation(-0.25F)),
                ModelTransform.of(0.0F, -9.51F, 0.0F, -0.6807F, 0.0F, 0.0F));

        ModelPartData control_lever = bone211.addChild("control_lever",
                ModelPartBuilder.create().uv(69, 73).cuboid(-2.5F, -0.5F, -0.5F, 3.0F, 1.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(-4.0F, -7.3868F, 11.182F, 0.0F, 0.7418F, 0.0F));

        ModelPartData control9 = bone211.addChild("control9",
                ModelPartBuilder.create().uv(52, 32)
                        .cuboid(0.25F, -5.75F, 8.8487F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)).uv(52, 32)
                        .cuboid(0.25F, -5.75F, 11.3487F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.pivot(-0.5F, -0.8868F, -1.4167F));

        ModelPartData bone212 = bone211.addChild("bone212",
                ModelPartBuilder.create().uv(53, 0).cuboid(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(3.25F, -6.1368F, 11.432F, 0.0F, 0.3491F, 0.0F));

        ModelPartData north_right = controls.addChild("north_right", ModelPartBuilder.create(),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -2.0944F, 0.0F));

        ModelPartData bone203 = north_right.addChild("bone203",
                ModelPartBuilder.create().uv(72, 4)
                        .cuboid(-1.0F, -6.6118F, 3.932F, 2.0F, 1.0F, 2.0F, new Dilation(0.0F)).uv(68, 19)
                        .cuboid(-3.25F, -6.1118F, 6.932F, 5.0F, 1.0F, 2.0F, new Dilation(0.0F)).uv(68, 16)
                        .cuboid(-3.0F, -6.3618F, 9.182F, 5.0F, 1.0F, 2.0F, new Dilation(0.0F)).uv(42, 47)
                        .cuboid(-6.0F, -5.9118F, 10.932F, 12.0F, 1.0F, 2.0F, new Dilation(0.0F)).uv(78, 5)
                        .cuboid(-4.75F, -5.8868F, 7.182F, 1.0F, 1.0F, 4.0F, new Dilation(0.0F)).uv(55, 69)
                        .cuboid(2.5F, -5.9118F, 8.682F, 3.0F, 1.0F, 3.0F, new Dilation(0.0F)).uv(10, 71)
                        .cuboid(2.25F, -5.8868F, 6.932F, 3.0F, 1.0F, 2.0F, new Dilation(0.0F)).uv(68, 13)
                        .cuboid(-2.5F, -5.8868F, 4.682F, 5.0F, 1.0F, 2.0F, new Dilation(0.0F)).uv(72, 4)
                        .cuboid(-3.25F, -6.3618F, 3.682F, 2.0F, 1.0F, 2.0F, new Dilation(-0.25F)).uv(72, 4)
                        .cuboid(1.25F, -6.3618F, 3.682F, 2.0F, 1.0F, 2.0F, new Dilation(-0.25F)),
                ModelTransform.of(0.0F, -9.51F, 0.0F, -0.6807F, 0.0F, 0.0F));

        ModelPartData bone201 = bone203.addChild("bone201", ModelPartBuilder.create(),
                ModelTransform.of(0.0F, -5.4118F, 11.932F, 0.6807F, 0.0F, 0.0F));

        ModelPartData control15 = bone201.addChild("control15", ModelPartBuilder.create().uv(13, 67).cuboid(-1.5F,
                -4.975F, 7.5987F, 5.0F, 1.0F, 3.0F, new Dilation(0.0F)), ModelTransform.pivot(3.5F, 4.975F, -5.8487F));

        ModelPartData bone205 = control15.addChild("bone205",
                ModelPartBuilder.create().uv(69, 62).cuboid(-2.5F, -1.0F, -1.0F, 5.0F, 1.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(1.0F, -4.975F, 8.0987F, 0.6109F, 0.0F, 0.0F));

        ModelPartData bone206 = control15.addChild("bone206",
                ModelPartBuilder.create().uv(68, 47).cuboid(-2.5F, -1.0F, -1.0F, 5.0F, 1.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(1.0F, -4.975F, 9.0987F, 0.6109F, 0.0F, 0.0F));

        ModelPartData bone207 = control15.addChild("bone207",
                ModelPartBuilder.create().uv(42, 54).cuboid(-2.75F, -1.0F, -1.0F, 5.0F, 1.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(1.0F, -4.975F, 10.0987F, 0.6109F, 0.0F, 0.0F));

        ModelPartData control16 = bone201.addChild("control16",
                ModelPartBuilder.create().uv(0, 70).cuboid(-1.5F, -0.75F, -1.25F, 3.0F, 2.0F, 2.0F, new Dilation(0.0F))
                        .uv(0, 66).cuboid(-2.5F, 1.0F, -1.5F, 5.0F, 1.0F, 3.0F, new Dilation(0.0F)),
                ModelTransform.pivot(-3.5F, -1.75F, 3.25F));

        ModelPartData control17 = bone203.addChild("control17", ModelPartBuilder.create().uv(9, 9).cuboid(-5.5F,
                -5.975F, 8.5987F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(0.5F, -1.3868F, 1.0833F));

        ModelPartData bone223 = bone203.addChild("bone223", ModelPartBuilder.create(),
                ModelTransform.pivot(2.5F, -1.3868F, 1.5833F));
        return TexturedModelData.of(modelData, 128, 128);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red,
            float green, float blue, float alpha) {
        console.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
    }

    @Override
    public void renderWithAnimations(ConsoleBlockEntity console, ModelPart root, MatrixStack matrices,
            VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float pAlpha) {
        Tardis tardis = console.tardis().get();

        if (tardis == null)
            return;

        matrices.push();
        matrices.translate(0.5f, -1.5f, -0.5f);
        super.renderWithAnimations(console, root, matrices, vertices, light, overlay, red, green, blue, pAlpha);
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
        return console;
    }
}
