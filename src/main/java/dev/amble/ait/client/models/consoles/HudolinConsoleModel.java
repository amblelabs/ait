package dev.amble.ait.client.models.consoles;

import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.animation.Animation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.RotationAxis;

import dev.amble.ait.api.tardis.TardisComponent;
import dev.amble.ait.client.animation.console.hudolin.HudolinAnimations;
import dev.amble.ait.client.tardis.ClientTardis;
import dev.amble.ait.core.blockentities.ConsoleBlockEntity;
import dev.amble.ait.core.tardis.control.impl.pos.IncrementManager;
import dev.amble.ait.core.tardis.handler.CloakHandler;
import dev.amble.ait.core.tardis.handler.travel.TravelHandlerBase;

public class HudolinConsoleModel extends ConsoleModel {
    public final ModelPart console;
    public final ModelPart pannel4;
    public final ModelPart fix;
    public final ModelPart control;
    public final ModelPart fix2;
    public final ModelPart control2;
    public final ModelPart fix3;
    public final ModelPart control3;
    public final ModelPart fix4;
    public final ModelPart control4;
    public final ModelPart fix5;
    public final ModelPart bone4;
    public final ModelPart fix12;
    public final ModelPart bone10;
    public final ModelPart fix7;
    public final ModelPart bone5;
    public final ModelPart fix8;
    public final ModelPart bone6;
    public final ModelPart fix9;
    public final ModelPart bone7;
    public final ModelPart fix10;
    public final ModelPart bone8;
    public final ModelPart fix11;
    public final ModelPart bone9;
    public final ModelPart spin2;
    public final ModelPart bone2;
    public final ModelPart lighton13;
    public final ModelPart off18;
    public final ModelPart lighton15;
    public final ModelPart off20;
    public final ModelPart lighton16;
    public final ModelPart off21;
    public final ModelPart lighton17;
    public final ModelPart off22;
    public final ModelPart lighton14;
    public final ModelPart off19;
    public final ModelPart lighton9;
    public final ModelPart off14;
    public final ModelPart lighton10;
    public final ModelPart off15;
    public final ModelPart lightson;
    public final ModelPart off13;
    public final ModelPart lighton11;
    public final ModelPart off16;
    public final ModelPart lighton12;
    public final ModelPart off17;
    public final ModelPart pannel3;
    public final ModelPart dematlever;
    public final ModelPart dematleveryay;
    public final ModelPart dial;
    public final ModelPart dialfix;
    public final ModelPart spin;
    public final ModelPart lighton4;
    public final ModelPart off5;
    public final ModelPart lighton5;
    public final ModelPart off6;
    public final ModelPart lighton6;
    public final ModelPart off7;
    public final ModelPart brightlighton3;
    public final ModelPart off10;
    public final ModelPart brightlighton2;
    public final ModelPart off9;
    public final ModelPart brightlighton;
    public final ModelPart off8;
    public final ModelPart pannel5;
    public final ModelPart fix13;
    public final ModelPart spin3;
    public final ModelPart fix14;
    public final ModelPart spin4;
    public final ModelPart fix16;
    public final ModelPart spin6;
    public final ModelPart fix17;
    public final ModelPart spin7;
    public final ModelPart fix15;
    public final ModelPart spin5;
    public final ModelPart blinkingon;
    public final ModelPart blinkingoff;
    public final ModelPart lighton7;
    public final ModelPart off11;
    public final ModelPart lighton8;
    public final ModelPart off12;
    public final ModelPart lighton18;
    public final ModelPart off23;
    public final ModelPart thing;
    public final ModelPart thing2;
    public final ModelPart bone11;
    public final ModelPart thing3;
    public final ModelPart fix18;
    public final ModelPart dial2;
    public final ModelPart fix19;
    public final ModelPart dial3;
    public final ModelPart fix20;
    public final ModelPart dial4;
    public final ModelPart fix22;
    public final ModelPart dial6;
    public final ModelPart fix21;
    public final ModelPart dial5;
    public final ModelPart dividers;
    public final ModelPart slide2;
    public final ModelPart slide;
    public final ModelPart fix6;
    public final ModelPart handbreak;
    public final ModelPart flightlighton;
    public final ModelPart off;
    public final ModelPart fix30;
    public final ModelPart thing10;
    public final ModelPart thing9;
    public final ModelPart thing8;
    public final ModelPart thing7;
    public final ModelPart thing6;
    public final ModelPart fix29;
    public final ModelPart thing5;
    public final ModelPart fix28;
    public final ModelPart thing4;
    public final ModelPart fix24;
    public final ModelPart dial7;
    public final ModelPart fix25;
    public final ModelPart bone3;
    public final ModelPart fix26;
    public final ModelPart bone12;
    public final ModelPart fix27;
    public final ModelPart bone13;
    public final ModelPart fix23;
    public final ModelPart spin8;
    public final ModelPart fix31;
    public final ModelPart dial8;
    public final ModelPart fix32;
    public final ModelPart dial9;
    public final ModelPart clock;
    public final ModelPart minute;
    public final ModelPart hour;
    public final ModelPart rotor;
    public final ModelPart bottom;
    public final ModelPart top2;
    public final ModelPart nature;
    public final ModelPart inside;
    public final ModelPart underdividers;
    public final ModelPart top;
    public final ModelPart bone;
    public final ModelPart flick4;
    public final ModelPart flick2;
    public final ModelPart flick3;
    public final ModelPart flick5;
    public final ModelPart lighton3;
    public final ModelPart off4;
    public final ModelPart lighton2;
    public final ModelPart off3;
    public final ModelPart lighton;
    public final ModelPart off2;
    public final ModelPart bone14;
    public final ModelPart bone15;
    public final ModelPart toolbox;
    public final ModelPart lid;
    public final ModelPart lid2;
    public final ModelPart hammer;
    public final ModelPart bone17;
    public final ModelPart bone16;
    public HudolinConsoleModel(ModelPart root) {
        this.console = root.getChild("console");
        this.pannel4 = this.console.getChild("pannel4");
        this.fix = this.pannel4.getChild("fix");
        this.control = this.fix.getChild("control");
        this.fix2 = this.pannel4.getChild("fix2");
        this.control2 = this.fix2.getChild("control2");
        this.fix3 = this.pannel4.getChild("fix3");
        this.control3 = this.fix3.getChild("control3");
        this.fix4 = this.pannel4.getChild("fix4");
        this.control4 = this.fix4.getChild("control4");
        this.fix5 = this.pannel4.getChild("fix5");
        this.bone4 = this.fix5.getChild("bone4");
        this.fix12 = this.pannel4.getChild("fix12");
        this.bone10 = this.fix12.getChild("bone10");
        this.fix7 = this.pannel4.getChild("fix7");
        this.bone5 = this.fix7.getChild("bone5");
        this.fix8 = this.pannel4.getChild("fix8");
        this.bone6 = this.fix8.getChild("bone6");
        this.fix9 = this.pannel4.getChild("fix9");
        this.bone7 = this.fix9.getChild("bone7");
        this.fix10 = this.pannel4.getChild("fix10");
        this.bone8 = this.fix10.getChild("bone8");
        this.fix11 = this.pannel4.getChild("fix11");
        this.bone9 = this.fix11.getChild("bone9");
        this.spin2 = this.pannel4.getChild("spin2");
        this.bone2 = this.spin2.getChild("bone2");
        this.lighton13 = this.console.getChild("lighton13");
        this.off18 = this.lighton13.getChild("off18");
        this.lighton15 = this.console.getChild("lighton15");
        this.off20 = this.lighton15.getChild("off20");
        this.lighton16 = this.console.getChild("lighton16");
        this.off21 = this.lighton16.getChild("off21");
        this.lighton17 = this.console.getChild("lighton17");
        this.off22 = this.lighton17.getChild("off22");
        this.lighton14 = this.console.getChild("lighton14");
        this.off19 = this.lighton14.getChild("off19");
        this.lighton9 = this.console.getChild("lighton9");
        this.off14 = this.lighton9.getChild("off14");
        this.lighton10 = this.console.getChild("lighton10");
        this.off15 = this.lighton10.getChild("off15");
        this.lightson = this.console.getChild("lightson");
        this.off13 = this.lightson.getChild("off13");
        this.lighton11 = this.console.getChild("lighton11");
        this.off16 = this.lighton11.getChild("off16");
        this.lighton12 = this.console.getChild("lighton12");
        this.off17 = this.lighton12.getChild("off17");
        this.pannel3 = this.console.getChild("pannel3");
        this.dematlever = this.pannel3.getChild("dematlever");
        this.dematleveryay = this.dematlever.getChild("dematleveryay");
        this.dial = this.pannel3.getChild("dial");
        this.dialfix = this.dial.getChild("dialfix");
        this.spin = this.pannel3.getChild("spin");
        this.lighton4 = this.console.getChild("lighton4");
        this.off5 = this.lighton4.getChild("off5");
        this.lighton5 = this.console.getChild("lighton5");
        this.off6 = this.lighton5.getChild("off6");
        this.lighton6 = this.console.getChild("lighton6");
        this.off7 = this.lighton6.getChild("off7");
        this.brightlighton3 = this.console.getChild("brightlighton3");
        this.off10 = this.brightlighton3.getChild("off10");
        this.brightlighton2 = this.console.getChild("brightlighton2");
        this.off9 = this.brightlighton2.getChild("off9");
        this.brightlighton = this.console.getChild("brightlighton");
        this.off8 = this.brightlighton.getChild("off8");
        this.pannel5 = this.console.getChild("pannel5");
        this.fix13 = this.pannel5.getChild("fix13");
        this.spin3 = this.fix13.getChild("spin3");
        this.fix14 = this.pannel5.getChild("fix14");
        this.spin4 = this.fix14.getChild("spin4");
        this.fix16 = this.pannel5.getChild("fix16");
        this.spin6 = this.fix16.getChild("spin6");
        this.fix17 = this.pannel5.getChild("fix17");
        this.spin7 = this.fix17.getChild("spin7");
        this.fix15 = this.pannel5.getChild("fix15");
        this.spin5 = this.fix15.getChild("spin5");
        this.blinkingon = this.console.getChild("blinkingon");
        this.blinkingoff = this.blinkingon.getChild("blinkingoff");
        this.lighton7 = this.console.getChild("lighton7");
        this.off11 = this.lighton7.getChild("off11");
        this.lighton8 = this.console.getChild("lighton8");
        this.off12 = this.lighton8.getChild("off12");
        this.lighton18 = this.console.getChild("lighton18");
        this.off23 = this.lighton18.getChild("off23");
        this.thing = this.console.getChild("thing");
        this.thing2 = this.console.getChild("thing2");
        this.bone11 = this.console.getChild("bone11");
        this.thing3 = this.console.getChild("thing3");
        this.fix18 = this.console.getChild("fix18");
        this.dial2 = this.fix18.getChild("dial2");
        this.fix19 = this.console.getChild("fix19");
        this.dial3 = this.fix19.getChild("dial3");
        this.fix20 = this.console.getChild("fix20");
        this.dial4 = this.fix20.getChild("dial4");
        this.fix22 = this.console.getChild("fix22");
        this.dial6 = this.fix22.getChild("dial6");
        this.fix21 = this.console.getChild("fix21");
        this.dial5 = this.fix21.getChild("dial5");
        this.dividers = this.console.getChild("dividers");
        this.slide2 = this.console.getChild("slide2");
        this.slide = this.console.getChild("slide");
        this.fix6 = this.console.getChild("fix6");
        this.handbreak = this.fix6.getChild("handbreak");
        this.flightlighton = this.console.getChild("flightlighton");
        this.off = this.flightlighton.getChild("off");
        this.fix30 = this.console.getChild("fix30");
        this.thing10 = this.fix30.getChild("thing10");
        this.thing9 = this.fix30.getChild("thing9");
        this.thing8 = this.fix30.getChild("thing8");
        this.thing7 = this.fix30.getChild("thing7");
        this.thing6 = this.fix30.getChild("thing6");
        this.fix29 = this.console.getChild("fix29");
        this.thing5 = this.fix29.getChild("thing5");
        this.fix28 = this.console.getChild("fix28");
        this.thing4 = this.fix28.getChild("thing4");
        this.fix24 = this.console.getChild("fix24");
        this.dial7 = this.fix24.getChild("dial7");
        this.fix25 = this.console.getChild("fix25");
        this.bone3 = this.fix25.getChild("bone3");
        this.fix26 = this.console.getChild("fix26");
        this.bone12 = this.fix26.getChild("bone12");
        this.fix27 = this.console.getChild("fix27");
        this.bone13 = this.fix27.getChild("bone13");
        this.fix23 = this.console.getChild("fix23");
        this.spin8 = this.fix23.getChild("spin8");
        this.fix31 = this.console.getChild("fix31");
        this.dial8 = this.fix31.getChild("dial8");
        this.fix32 = this.console.getChild("fix32");
        this.dial9 = this.fix32.getChild("dial9");
        this.clock = this.console.getChild("clock");
        this.minute = this.clock.getChild("minute");
        this.hour = this.clock.getChild("hour");
        this.rotor = this.console.getChild("rotor");
        this.bottom = this.rotor.getChild("bottom");
        this.top2 = this.rotor.getChild("top2");
        this.nature = this.console.getChild("nature");
        this.inside = this.nature.getChild("inside");
        this.underdividers = this.console.getChild("underdividers");
        this.top = this.console.getChild("top");
        this.bone = this.console.getChild("bone");
        this.flick4 = this.bone.getChild("flick4");
        this.flick2 = this.bone.getChild("flick2");
        this.flick3 = this.bone.getChild("flick3");
        this.flick5 = this.bone.getChild("flick5");
        this.lighton3 = this.bone.getChild("lighton3");
        this.off4 = this.lighton3.getChild("off4");
        this.lighton2 = this.bone.getChild("lighton2");
        this.off3 = this.lighton2.getChild("off3");
        this.lighton = this.bone.getChild("lighton");
        this.off2 = this.lighton.getChild("off2");
        this.bone14 = this.console.getChild("bone14");
        this.bone15 = this.bone14.getChild("bone15");
        this.toolbox = root.getChild("toolbox");
        this.lid = this.toolbox.getChild("lid");
        this.lid2 = this.toolbox.getChild("lid2");
        this.hammer = this.toolbox.getChild("hammer");
        this.bone17 = this.hammer.getChild("bone17");
        this.bone16 = this.bone17.getChild("bone16");
    }
    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData console = modelPartData.addChild("console", ModelPartBuilder.create().uv(21, 29).cuboid(-11.0F, -13.0F, -19.0526F, 22.0F, 1.0F, 14.0F, new Dilation(0.0F))
                .uv(36, 44).cuboid(1.0F, -13.5F, -18.0526F, 5.0F, 1.0F, 3.0F, new Dilation(0.0F))
                .uv(26, 26).cuboid(1.0F, -15.0F, -16.0526F, 1.0F, 2.0F, 1.0F, new Dilation(-0.25F))
                .uv(26, 26).cuboid(1.0F, -15.0F, -17.0526F, 1.0F, 2.0F, 1.0F, new Dilation(-0.25F))
                .uv(26, 26).cuboid(1.0F, -15.0F, -18.0526F, 1.0F, 2.0F, 1.0F, new Dilation(-0.25F))
                .uv(26, 26).cuboid(5.0F, -15.0F, -18.0526F, 1.0F, 2.0F, 1.0F, new Dilation(-0.25F))
                .uv(26, 26).cuboid(5.0F, -15.0F, -17.0526F, 1.0F, 2.0F, 1.0F, new Dilation(-0.25F))
                .uv(26, 26).cuboid(5.0F, -15.0F, -16.0526F, 1.0F, 2.0F, 1.0F, new Dilation(-0.25F))
                .uv(61, 49).cuboid(-10.5F, -12.1F, -18.1865F, 21.0F, 2.0F, 1.0F, new Dilation(0.0F))
                .uv(0, 0).cuboid(-3.0F, -19.6311F, -5.1962F, 6.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(0, 0).cuboid(-3.0F, -19.6311F, 4.1962F, 6.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(0, 23).cuboid(-3.0F, -1.6311F, -5.1962F, 6.0F, 2.0F, 5.0F, new Dilation(0.0F))
                .uv(4, 30).cuboid(-3.5F, -0.6311F, -6.0622F, 7.0F, 1.0F, 5.0F, new Dilation(0.001F))
                .uv(4, 30).cuboid(-3.5F, -0.6311F, 1.0622F, 7.0F, 1.0F, 5.0F, new Dilation(0.0F))
                .uv(50, 0).cuboid(-3.0F, -13.1311F, -5.1962F, 6.0F, 4.0F, 5.0F, new Dilation(0.0F))
                .uv(0, 3).cuboid(-2.5F, -21.2311F, -4.5278F, 5.0F, 3.0F, 1.0F, new Dilation(0.27F))
                .uv(0, 3).cuboid(-2.5F, -3.2311F, -4.5278F, 5.0F, 3.0F, 1.0F, new Dilation(0.27F))
                .uv(0, 40).cuboid(-2.5F, -46.5311F, -4.3301F, 5.0F, 25.0F, 0.0F, new Dilation(0.0F))
                .uv(0, 7).mirrored().cuboid(-2.5F, -9.3311F, -4.3301F, 5.0F, 6.0F, 0.0F, new Dilation(0.0F)).mirrored(false)
                .uv(69, 57).mirrored().cuboid(-2.0F, -9.3311F, -3.4641F, 4.0F, 6.0F, 1.0F, new Dilation(0.0F)).mirrored(false)
                .uv(69, 57).mirrored().cuboid(-2.0F, -9.3311F, 2.4641F, 4.0F, 6.0F, 1.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(0.0F, 23.6F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData cube_r1 = console.addChild("cube_r1", ModelPartBuilder.create().uv(45, 2).cuboid(-6.0F, -3.7926F, -12.188F, 2.0F, 0.0F, 2.0F, new Dilation(0.0F))
                .uv(25, 40).cuboid(2.0F, -3.7926F, -12.188F, 2.0F, 0.0F, 2.0F, new Dilation(0.0F))
                .uv(29, 68).cuboid(-2.5F, -3.4926F, -8.988F, 0.0F, 1.0F, 2.0F, new Dilation(0.0F))
                .uv(29, 68).cuboid(-3.0F, -3.4926F, -8.988F, 0.0F, 1.0F, 2.0F, new Dilation(0.0F))
                .uv(29, 68).cuboid(0.5F, -3.4926F, -8.988F, 0.0F, 1.0F, 2.0F, new Dilation(0.0F))
                .uv(30, 72).cuboid(-0.5F, -3.4926F, -9.488F, 1.0F, 1.0F, 0.0F, new Dilation(0.0F))
                .uv(30, 72).cuboid(0.0F, -3.4926F, -8.988F, 1.0F, 1.0F, 0.0F, new Dilation(0.0F))
                .uv(30, 72).cuboid(-3.5F, -3.4926F, -8.988F, 1.0F, 1.0F, 0.0F, new Dilation(0.0F))
                .uv(30, 72).cuboid(-3.0F, -3.4926F, -9.488F, 1.0F, 1.0F, 0.0F, new Dilation(0.0F))
                .uv(30, 72).cuboid(-0.5F, -3.4926F, -6.988F, 1.0F, 1.0F, 0.0F, new Dilation(0.0F))
                .uv(30, 72).cuboid(-3.0F, -3.4926F, -6.988F, 1.0F, 1.0F, 0.0F, new Dilation(0.0F))
                .uv(32, 70).cuboid(-1.5F, -3.4926F, -3.988F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(32, 70).cuboid(-0.5F, -3.4926F, -3.988F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(32, 70).cuboid(0.0F, -3.4926F, -4.488F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(32, 70).cuboid(-2.0F, -3.4926F, -4.488F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(32, 70).cuboid(-2.5F, -3.4926F, -9.488F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(32, 70).cuboid(0.5F, -3.4926F, -9.488F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(32, 70).cuboid(0.0F, -3.4926F, -9.488F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(34, 71).cuboid(-2.0F, -3.4926F, -6.488F, 2.0F, 1.0F, 0.0F, new Dilation(0.0F))
                .uv(34, 71).cuboid(-2.0F, -3.4926F, -9.988F, 2.0F, 1.0F, 0.0F, new Dilation(0.0F))
                .uv(34, 73).cuboid(-1.5F, -3.4926F, -3.488F, 1.0F, 1.0F, 0.0F, new Dilation(0.0F))
                .uv(34, 73).cuboid(-2.0F, -3.4926F, -4.488F, 2.0F, 1.0F, 0.0F, new Dilation(0.0F))
                .uv(33, 70).cuboid(-2.0F, -3.4926F, -5.988F, 2.0F, 1.0F, 0.0F, new Dilation(0.0F))
                .uv(33, 70).cuboid(-2.0F, -3.4926F, -9.488F, 2.0F, 1.0F, 0.0F, new Dilation(0.0F))
                .uv(32, 70).cuboid(-2.0F, -3.4926F, -9.488F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(32, 70).cuboid(-2.0F, -3.4926F, -9.988F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(30, 70).cuboid(0.0F, -3.4926F, -9.988F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(32, 70).cuboid(-2.0F, -3.4926F, -6.988F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(32, 70).cuboid(0.0F, -3.4926F, -6.988F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(32, 70).cuboid(0.5F, -3.4926F, -6.988F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(32, 70).cuboid(-2.5F, -3.4926F, -6.988F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(30, 70).cuboid(0.0F, -3.4926F, -6.488F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(30, 70).cuboid(-2.0F, -3.4926F, -6.488F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(30, 70).cuboid(-2.5F, -3.4926F, -6.988F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(30, 71).cuboid(-3.5F, -3.4926F, -6.988F, 1.0F, 1.0F, 0.0F, new Dilation(0.0F))
                .uv(30, 71).cuboid(-2.5F, -3.4926F, -3.988F, 1.0F, 1.0F, 0.0F, new Dilation(0.0F))
                .uv(30, 71).cuboid(-1.0F, -3.4926F, -3.988F, 1.0F, 1.0F, 0.0F, new Dilation(0.0F))
                .uv(30, 71).cuboid(0.0F, -3.4926F, -6.988F, 1.0F, 1.0F, 0.0F, new Dilation(0.0F))
                .uv(30, 71).cuboid(-0.5F, -3.4926F, -6.488F, 1.0F, 1.0F, 0.0F, new Dilation(0.0F))
                .uv(30, 71).cuboid(-3.0F, -3.4926F, -6.488F, 1.0F, 1.0F, 0.0F, new Dilation(0.0F))
                .uv(30, 71).cuboid(-3.0F, -3.4926F, -8.988F, 1.0F, 1.0F, 0.0F, new Dilation(0.0F))
                .uv(30, 71).cuboid(-0.5F, -3.4926F, -8.988F, 1.0F, 1.0F, 0.0F, new Dilation(0.0F))
                .uv(29, 68).cuboid(1.0F, -3.4926F, -8.988F, 0.0F, 1.0F, 2.0F, new Dilation(0.0F))
                .uv(9, 17).cuboid(-3.0F, -3.1926F, -4.488F, 4.0F, 0.0F, 1.0F, new Dilation(0.0F))
                .uv(18, 48).cuboid(-3.0F, -3.1926F, -10.488F, 4.0F, 0.0F, 5.0F, new Dilation(0.0F))
                .uv(49, 48).cuboid(-3.0F, -3.7926F, -12.988F, 4.0F, 1.0F, 2.0F, new Dilation(0.0F))
                .uv(10, 59).cuboid(-9.0F, -3.4926F, -13.488F, 16.0F, 0.0F, 11.0F, new Dilation(0.0F)), ModelTransform.of(1.1962F, -17.8746F, 0.4641F, 0.6109F, -1.0472F, 0.0F));

        ModelPartData cube_r2 = console.addChild("cube_r2", ModelPartBuilder.create().uv(35, 50).cuboid(-1.0F, 0.0F, -1.0F, 2.0F, 0.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(4.5169F, -17.6737F, -6.5921F, 0.7117F, -1.1162F, -0.1141F));

        ModelPartData cube_r3 = console.addChild("cube_r3", ModelPartBuilder.create().uv(43, 48).cuboid(-1.0F, 0.0F, -1.0F, 2.0F, 0.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(7.934F, -15.8082F, -4.4725F, 2.3855F, -0.422F, -2.4617F));

        ModelPartData cube_r4 = console.addChild("cube_r4", ModelPartBuilder.create().uv(37, 48).cuboid(-1.0F, 0.0F, -1.0F, 2.0F, 0.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(7.841F, -15.8077F, -4.6352F, -0.2534F, -1.1339F, 0.9583F));

        ModelPartData cube_r5 = console.addChild("cube_r5", ModelPartBuilder.create().uv(31, 48).cuboid(-1.0F, 0.0F, -1.0F, 2.0F, 0.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(7.9538F, -15.7539F, -4.5933F, 2.9671F, 1.0472F, 3.1416F));

        ModelPartData cube_r6 = console.addChild("cube_r6", ModelPartBuilder.create().uv(31, 48).cuboid(-1.0F, 0.0F, -1.0F, 2.0F, 0.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(7.8209F, -15.8614F, -4.5165F, 1.0472F, -1.0472F, 0.0F));

        ModelPartData cube_r7 = console.addChild("cube_r7", ModelPartBuilder.create().uv(22, 38).cuboid(2.5F, -0.6F, -0.8F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(10.6201F, -13.9415F, -6.9744F, 0.6109F, -1.0472F, 0.0F));

        ModelPartData cube_r8 = console.addChild("cube_r8", ModelPartBuilder.create().uv(22, 38).cuboid(1.5F, -0.6F, -0.8F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(22, 38).cuboid(0.5F, -0.6F, -0.8F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(10.6101F, -13.9415F, -6.9917F, 0.6109F, -1.0472F, 0.0F));

        ModelPartData cube_r9 = console.addChild("cube_r9", ModelPartBuilder.create().uv(22, 38).cuboid(-0.5F, -0.6F, -0.8F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(10.6106F, -13.9415F, -6.9909F, 0.6109F, -1.0472F, 0.0F));

        ModelPartData cube_r10 = console.addChild("cube_r10", ModelPartBuilder.create().uv(0, 7).mirrored().cuboid(-2.5F, -4.5F, -4.3301F, 5.0F, 6.0F, 0.0F, new Dilation(0.0F)).mirrored(false)
                .uv(0, 40).cuboid(-2.5F, -41.5F, -4.3301F, 5.0F, 25.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -5.0311F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData cube_r11 = console.addChild("cube_r11", ModelPartBuilder.create().uv(0, 7).mirrored().cuboid(-2.5F, -4.5F, -4.3301F, 5.0F, 6.0F, 0.0F, new Dilation(0.0F)).mirrored(false)
                .uv(0, 40).cuboid(-2.5F, -41.5F, -4.3301F, 5.0F, 25.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -5.0311F, 0.0F, 0.0F, -2.0944F, 0.0F));

        ModelPartData cube_r12 = console.addChild("cube_r12", ModelPartBuilder.create().uv(0, 7).cuboid(-2.5F, -4.5F, -3.6699F, 5.0F, 6.0F, 0.0F, new Dilation(0.0F))
                .uv(0, 40).mirrored().cuboid(-2.5F, -41.5F, -3.6699F, 5.0F, 25.0F, 0.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(0.0F, -5.0311F, 0.6603F, 0.0F, 3.1416F, 0.0F));

        ModelPartData cube_r13 = console.addChild("cube_r13", ModelPartBuilder.create().uv(0, 7).mirrored().cuboid(-2.5F, -4.5F, -4.3301F, 5.0F, 6.0F, 0.0F, new Dilation(0.0F)).mirrored(false)
                .uv(0, 40).cuboid(-2.5F, -41.5F, -4.3301F, 5.0F, 25.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -5.0311F, 0.0F, 0.0F, 2.0944F, 0.0F));

        ModelPartData cube_r14 = console.addChild("cube_r14", ModelPartBuilder.create().uv(0, 7).mirrored().cuboid(-2.5F, -4.5F, -4.3301F, 5.0F, 6.0F, 0.0F, new Dilation(0.0F)).mirrored(false)
                .uv(0, 40).cuboid(-2.5F, -41.5F, -4.3301F, 5.0F, 25.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -5.0311F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData cube_r15 = console.addChild("cube_r15", ModelPartBuilder.create().uv(69, 57).cuboid(-2.0F, -3.0F, -0.5F, 4.0F, 6.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(2.567F, -6.3311F, 1.4821F, 0.0F, 1.0472F, 0.0F));

        ModelPartData cube_r16 = console.addChild("cube_r16", ModelPartBuilder.create().uv(69, 57).cuboid(-2.0F, -3.0F, -0.5F, 4.0F, 6.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-2.567F, -6.3311F, 1.4821F, 0.0F, -1.0472F, 0.0F));

        ModelPartData cube_r17 = console.addChild("cube_r17", ModelPartBuilder.create().uv(69, 57).cuboid(-2.0F, -3.0F, -0.5F, 4.0F, 6.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-2.567F, -6.3311F, -1.4821F, 0.0F, 1.0472F, 0.0F));

        ModelPartData cube_r18 = console.addChild("cube_r18", ModelPartBuilder.create().uv(69, 57).mirrored().cuboid(-2.0F, -3.0F, -0.5F, 4.0F, 6.0F, 1.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(2.567F, -6.3311F, -1.4821F, 0.0F, -1.0472F, 0.0F));

        ModelPartData cube_r19 = console.addChild("cube_r19", ModelPartBuilder.create().uv(17, 0).cuboid(0.6314F, -1.6314F, -5.0301F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(17, 0).mirrored().cuboid(-0.5F, -0.5F, -5.0301F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)).mirrored(false)
                .uv(17, 0).mirrored().cuboid(-1.6314F, 0.6314F, -5.0301F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(0.0F, -2.5311F, 0.0F, 2.2555F, 0.6591F, 2.0344F));

        ModelPartData cube_r20 = console.addChild("cube_r20", ModelPartBuilder.create().uv(17, 0).mirrored().cuboid(-1.6314F, -1.6314F, 4.0301F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)).mirrored(false)
                .uv(17, 0).cuboid(-0.5F, -0.5F, 4.0301F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(17, 0).cuboid(0.6314F, 0.6314F, 4.0301F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -2.5311F, 0.0F, -2.2555F, 0.6591F, -2.0344F));

        ModelPartData cube_r21 = console.addChild("cube_r21", ModelPartBuilder.create().uv(17, 0).cuboid(0.6314F, -1.6314F, -5.0301F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(17, 0).mirrored().cuboid(-0.5F, -0.5F, -5.0301F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)).mirrored(false)
                .uv(17, 0).mirrored().cuboid(-1.6314F, 0.6314F, -5.0301F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(0.0F, -2.5311F, 0.0F, -2.2555F, -0.6591F, 2.0344F));

        ModelPartData cube_r22 = console.addChild("cube_r22", ModelPartBuilder.create().uv(17, 0).mirrored().cuboid(-1.6314F, -1.6314F, 4.0301F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)).mirrored(false)
                .uv(17, 0).cuboid(-0.5F, -0.5F, 4.0301F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(17, 0).cuboid(0.6314F, 0.6314F, 4.0301F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -2.5311F, 0.0F, 2.2555F, -0.6591F, -2.0344F));

        ModelPartData cube_r23 = console.addChild("cube_r23", ModelPartBuilder.create().uv(17, 0).mirrored().cuboid(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(-1.6F, -2.5311F, 4.5301F, 0.0F, 0.0F, -0.7854F));

        ModelPartData cube_r24 = console.addChild("cube_r24", ModelPartBuilder.create().uv(17, 0).cuboid(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -2.5311F, 4.5301F, 0.0F, 0.0F, -0.7854F));

        ModelPartData cube_r25 = console.addChild("cube_r25", ModelPartBuilder.create().uv(17, 0).cuboid(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(1.6F, -2.5311F, 4.5301F, 0.0F, 0.0F, -0.7854F));

        ModelPartData cube_r26 = console.addChild("cube_r26", ModelPartBuilder.create().uv(17, 0).cuboid(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(1.6F, -2.5311F, -4.5301F, 0.0F, 0.0F, 0.7854F));

        ModelPartData cube_r27 = console.addChild("cube_r27", ModelPartBuilder.create().uv(17, 0).mirrored().cuboid(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(0.0F, -2.5311F, -4.5301F, 0.0F, 0.0F, 0.7854F));

        ModelPartData cube_r28 = console.addChild("cube_r28", ModelPartBuilder.create().uv(17, 0).mirrored().cuboid(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(-1.6F, -2.5311F, -4.5301F, 0.0F, 0.0F, 0.7854F));

        ModelPartData cube_r29 = console.addChild("cube_r29", ModelPartBuilder.create().uv(0, 3).mirrored().cuboid(-2.5F, -1.5F, -4.3301F, 5.0F, 3.0F, 1.0F, new Dilation(0.27F)).mirrored(false), ModelTransform.of(-0.1712F, -1.7311F, -0.0988F, 0.0F, 1.0472F, 0.0F));

        ModelPartData cube_r30 = console.addChild("cube_r30", ModelPartBuilder.create().uv(0, 3).cuboid(-2.5F, -1.5F, -4.3301F, 5.0F, 3.0F, 1.0F, new Dilation(0.27F))
                .uv(0, 3).cuboid(-2.5F, -19.5F, -4.3301F, 5.0F, 3.0F, 1.0F, new Dilation(0.27F)), ModelTransform.of(0.1712F, -1.7311F, 0.0988F, 0.0F, -2.0944F, 0.0F));

        ModelPartData cube_r31 = console.addChild("cube_r31", ModelPartBuilder.create().uv(0, 3).mirrored().cuboid(-2.5F, -1.5F, -4.3301F, 5.0F, 3.0F, 1.0F, new Dilation(0.27F)).mirrored(false)
                .uv(0, 3).mirrored().cuboid(-2.5F, -19.5F, -4.3301F, 5.0F, 3.0F, 1.0F, new Dilation(0.27F)).mirrored(false), ModelTransform.of(-0.1712F, -1.7311F, 0.0988F, 0.0F, 2.0944F, 0.0F));

        ModelPartData cube_r32 = console.addChild("cube_r32", ModelPartBuilder.create().uv(0, 3).cuboid(-2.5F, -1.5F, -0.5F, 5.0F, 3.0F, 1.0F, new Dilation(0.27F)), ModelTransform.of(0.0F, -1.7311F, 4.0278F, 0.0F, 3.1416F, 0.0F));

        ModelPartData cube_r33 = console.addChild("cube_r33", ModelPartBuilder.create().uv(0, 3).cuboid(-2.5F, -1.5F, -4.3301F, 5.0F, 3.0F, 1.0F, new Dilation(0.27F)), ModelTransform.of(0.1712F, -1.7311F, -0.0988F, 0.0F, -1.0472F, 0.0F));

        ModelPartData cube_r34 = console.addChild("cube_r34", ModelPartBuilder.create().uv(0, 3).mirrored().cuboid(-1.628F, -1.5F, -5.0313F, 5.0F, 3.0F, 1.0F, new Dilation(0.271F)).mirrored(false), ModelTransform.of(0.0F, -19.7311F, 1.007F, 0.0F, 1.0472F, 0.0F));

        ModelPartData cube_r35 = console.addChild("cube_r35", ModelPartBuilder.create().uv(0, 3).cuboid(-2.5F, -1.5F, -3.5208F, 5.0F, 3.0F, 1.0F, new Dilation(0.271F)), ModelTransform.of(0.0F, -19.7311F, 1.007F, 0.0F, 3.1416F, 0.0F));

        ModelPartData cube_r36 = console.addChild("cube_r36", ModelPartBuilder.create().uv(76, 5).cuboid(-5.372F, -1.7F, -5.0313F, 9.0F, 0.0F, 7.0F, new Dilation(0.0F)), ModelTransform.of(-0.8901F, -19.7311F, 1.5208F, 0.0F, -1.0472F, 0.0F));

        ModelPartData cube_r37 = console.addChild("cube_r37", ModelPartBuilder.create().uv(0, 3).cuboid(-3.372F, -1.5F, -5.0313F, 5.0F, 3.0F, 1.0F, new Dilation(0.271F)), ModelTransform.of(0.0F, -19.7311F, 1.007F, 0.0F, -1.0472F, 0.0F));

        ModelPartData cube_r38 = console.addChild("cube_r38", ModelPartBuilder.create().uv(17, 0).cuboid(0.7728F, -1.7728F, -5.1301F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(17, 0).cuboid(0.7728F, -1.7728F, 4.1301F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -20.5311F, 0.0F, -2.2555F, -0.6591F, 2.0344F));

        ModelPartData cube_r39 = console.addChild("cube_r39", ModelPartBuilder.create().uv(17, 0).mirrored().cuboid(-0.5F, -0.5F, -5.1301F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)).mirrored(false)
                .uv(17, 0).mirrored().cuboid(-1.7728F, -1.7728F, -5.1301F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)).mirrored(false)
                .uv(17, 0).mirrored().cuboid(-1.7728F, -1.7728F, 4.1301F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)).mirrored(false)
                .uv(17, 0).mirrored().cuboid(-0.5F, -0.5F, 4.1301F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(0.0F, -20.5311F, 0.0F, 2.2555F, -0.6591F, -2.0344F));

        ModelPartData cube_r40 = console.addChild("cube_r40", ModelPartBuilder.create().uv(17, 0).cuboid(0.7728F, -1.7728F, -5.1301F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(17, 0).cuboid(0.7728F, -1.7728F, 4.1301F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -20.5311F, 0.0F, -0.8861F, -0.6591F, 1.1071F));

        ModelPartData cube_r41 = console.addChild("cube_r41", ModelPartBuilder.create().uv(17, 0).mirrored().cuboid(-0.5F, -0.5F, -5.1301F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)).mirrored(false)
                .uv(17, 0).mirrored().cuboid(-1.7728F, -1.7728F, -5.1301F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)).mirrored(false)
                .uv(17, 0).mirrored().cuboid(-1.7728F, -1.7728F, 4.1301F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)).mirrored(false)
                .uv(17, 0).mirrored().cuboid(-0.5F, -0.5F, 4.1301F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(0.0F, -20.5311F, 0.0F, 0.8861F, -0.6591F, -1.1071F));

        ModelPartData cube_r42 = console.addChild("cube_r42", ModelPartBuilder.create().uv(17, 0).mirrored().cuboid(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)).mirrored(false)
                .uv(17, 0).mirrored().cuboid(-0.5F, -0.5F, -9.7603F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(-1.8F, -20.5311F, 4.6301F, 0.0F, 0.0F, -0.7854F));

        ModelPartData cube_r43 = console.addChild("cube_r43", ModelPartBuilder.create().uv(17, 0).mirrored().cuboid(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)).mirrored(false)
                .uv(17, 0).mirrored().cuboid(-0.5F, -0.5F, -9.7603F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(0.0F, -20.5311F, 4.6301F, 0.0F, 0.0F, -0.7854F));

        ModelPartData cube_r44 = console.addChild("cube_r44", ModelPartBuilder.create().uv(17, 0).cuboid(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(17, 0).cuboid(-0.5F, -0.5F, -9.7603F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(1.8F, -20.5311F, 4.6301F, 0.0F, 0.0F, 0.7854F));

        ModelPartData cube_r45 = console.addChild("cube_r45", ModelPartBuilder.create().uv(50, 0).mirrored().cuboid(-3.0F, -2.0F, -5.5F, 6.0F, 4.0F, 5.0F, new Dilation(0.0F)).mirrored(false)
                .uv(0, 23).mirrored().cuboid(-3.0F, 9.5F, -5.5F, 6.0F, 2.0F, 5.0F, new Dilation(0.0F)).mirrored(false)
                .uv(0, 0).mirrored().cuboid(-3.0F, -8.5F, -5.5F, 6.0F, 1.0F, 1.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(0.2631F, -11.1311F, 0.1519F, 0.0F, 1.0472F, 0.0F));

        ModelPartData cube_r46 = console.addChild("cube_r46", ModelPartBuilder.create().uv(50, 0).cuboid(-3.0F, -2.0F, -5.5F, 6.0F, 4.0F, 5.0F, new Dilation(0.0F))
                .uv(0, 23).cuboid(-3.0F, 9.5F, -5.5F, 6.0F, 2.0F, 5.0F, new Dilation(0.0F))
                .uv(0, 0).cuboid(-3.0F, -8.5F, -5.5F, 6.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-0.2631F, -11.1311F, 0.1519F, 0.0F, -1.0472F, 0.0F));

        ModelPartData cube_r47 = console.addChild("cube_r47", ModelPartBuilder.create().uv(50, 0).cuboid(-3.0F, -2.0F, -5.5F, 6.0F, 4.0F, 5.0F, new Dilation(0.0F))
                .uv(0, 23).cuboid(-3.0F, 9.5F, -5.5F, 6.0F, 2.0F, 5.0F, new Dilation(0.0F))
                .uv(0, 0).cuboid(-3.0F, -8.5F, -5.5F, 6.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-0.2631F, -11.1311F, -0.1519F, 0.0F, -2.0944F, 0.0F));

        ModelPartData cube_r48 = console.addChild("cube_r48", ModelPartBuilder.create().uv(50, 0).mirrored().cuboid(-3.0F, -2.0F, -5.5F, 6.0F, 4.0F, 5.0F, new Dilation(0.0F)).mirrored(false)
                .uv(0, 23).mirrored().cuboid(-3.0F, 9.5F, -5.5F, 6.0F, 2.0F, 5.0F, new Dilation(0.0F)).mirrored(false)
                .uv(0, 0).mirrored().cuboid(-3.0F, -8.5F, -5.5F, 6.0F, 1.0F, 1.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(0.2631F, -11.1311F, -0.1519F, 0.0F, 2.0944F, 0.0F));

        ModelPartData cube_r49 = console.addChild("cube_r49", ModelPartBuilder.create().uv(50, 0).cuboid(-3.0F, -2.0F, -2.5F, 6.0F, 4.0F, 5.0F, new Dilation(0.0F))
                .uv(0, 23).cuboid(-3.0F, 9.5F, -2.5F, 6.0F, 2.0F, 5.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -11.1311F, 2.6961F, 0.0F, 3.1416F, 0.0F));

        ModelPartData cube_r50 = console.addChild("cube_r50", ModelPartBuilder.create().uv(4, 30).cuboid(-4.2712F, -0.5F, -6.5075F, 7.0F, 1.0F, 5.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, -0.1311F, -0.8905F, 0.0F, 2.0944F, 0.0F));

        ModelPartData cube_r51 = console.addChild("cube_r51", ModelPartBuilder.create().uv(4, 30).cuboid(-4.0F, 1.0F, -5.5F, 7.0F, 1.0F, 5.0F, new Dilation(0.0F)), ModelTransform.of(0.7369F, -1.6311F, 0.1519F, 0.0F, -1.0472F, 0.0F));

        ModelPartData cube_r52 = console.addChild("cube_r52", ModelPartBuilder.create().uv(4, 30).cuboid(-2.7288F, -0.5F, -6.5075F, 7.0F, 1.0F, 5.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, -0.1311F, -0.8905F, 0.0F, -2.0944F, 0.0F));

        ModelPartData cube_r53 = console.addChild("cube_r53", ModelPartBuilder.create().uv(4, 30).mirrored().cuboid(-4.0F, 1.0F, -5.5F, 7.0F, 1.0F, 5.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(-0.2369F, -1.6311F, -0.7141F, 0.0F, 1.0472F, 0.0F));

        ModelPartData cube_r54 = console.addChild("cube_r54", ModelPartBuilder.create().uv(79, 29).cuboid(-2.0F, -2.9926F, -5.488F, 2.0F, 0.0F, 2.0F, new Dilation(0.0F))
                .uv(79, 33).cuboid(-2.0F, -3.4926F, -5.488F, 2.0F, 1.0F, 0.0F, new Dilation(0.0F))
                .uv(79, 31).cuboid(-2.0F, -3.4926F, -3.488F, 2.0F, 1.0F, 0.0F, new Dilation(0.0F))
                .uv(79, 30).cuboid(-2.0F, -3.4926F, -5.488F, 0.0F, 1.0F, 2.0F, new Dilation(0.0F))
                .uv(79, 30).cuboid(0.0F, -3.4926F, -5.488F, 0.0F, 1.0F, 2.0F, new Dilation(0.0F))
                .uv(79, 28).cuboid(-4.0F, -2.9926F, -5.488F, 1.0F, 0.0F, 1.0F, new Dilation(0.0F))
                .uv(79, 30).cuboid(-4.0F, -3.4926F, -5.488F, 1.0F, 1.0F, 0.0F, new Dilation(0.0F))
                .uv(79, 29).cuboid(-4.0F, -3.4926F, -4.488F, 1.0F, 1.0F, 0.0F, new Dilation(0.0F))
                .uv(78, 27).cuboid(-4.0F, -3.4926F, -5.488F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(78, 27).cuboid(-3.0F, -3.4926F, -5.488F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(79, 28).cuboid(1.0F, -2.9926F, -5.488F, 1.0F, 0.0F, 1.0F, new Dilation(0.0F))
                .uv(79, 30).cuboid(1.0F, -3.4926F, -5.488F, 1.0F, 1.0F, 0.0F, new Dilation(0.0F))
                .uv(79, 29).cuboid(1.0F, -3.4926F, -4.488F, 1.0F, 1.0F, 0.0F, new Dilation(0.0F))
                .uv(78, 27).cuboid(1.0F, -3.4926F, -5.488F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(78, 27).cuboid(2.0F, -3.4926F, -5.488F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(78, 31).cuboid(1.0F, -3.1926F, -10.488F, 2.0F, 0.0F, 5.0F, new Dilation(0.0F))
                .uv(29, 35).cuboid(-4.5F, -3.6926F, -7.488F, 1.0F, 1.0F, 2.0F, new Dilation(0.0F))
                .uv(10, 39).cuboid(-4.5F, -3.6926F, -7.988F, 1.0F, 1.0F, 2.0F, new Dilation(0.0F))
                .uv(23, 29).cuboid(-3.0F, -3.6926F, -7.488F, 4.0F, 1.0F, 2.0F, new Dilation(0.0F))
                .uv(4, 36).cuboid(-3.0F, -3.6926F, -7.988F, 4.0F, 1.0F, 2.0F, new Dilation(0.0F))
                .uv(23, 29).cuboid(-3.0F, -3.6926F, -9.988F, 4.0F, 1.0F, 2.0F, new Dilation(0.0F))
                .uv(23, 32).cuboid(-3.0F, -3.6926F, -10.488F, 4.0F, 1.0F, 2.0F, new Dilation(0.0F))
                .uv(50, 13).cuboid(-7.0F, -3.6926F, -12.988F, 12.0F, 1.0F, 2.0F, new Dilation(0.0F))
                .uv(50, 10).cuboid(-7.0F, -3.6926F, -13.488F, 12.0F, 1.0F, 2.0F, new Dilation(0.0F))
                .uv(57, 17).cuboid(-9.0F, -3.4926F, -13.488F, 16.0F, 0.0F, 11.0F, new Dilation(0.0F)), ModelTransform.of(1.0F, -17.8746F, -0.8038F, 0.6109F, 0.0F, 0.0F));

        ModelPartData cube_r55 = console.addChild("cube_r55", ModelPartBuilder.create().uv(49, 83).cuboid(2.6F, -0.6F, -1.0F, 2.0F, 1.0F, 2.0F, new Dilation(-0.1F))
                .uv(49, 83).cuboid(1.2F, -0.6F, -2.0F, 2.0F, 1.0F, 2.0F, new Dilation(-0.1F))
                .uv(49, 83).cuboid(1.2F, -0.6F, 0.0F, 2.0F, 1.0F, 2.0F, new Dilation(-0.1F))
                .uv(49, 83).cuboid(-1.0F, -0.6F, -2.0F, 2.0F, 1.0F, 2.0F, new Dilation(-0.1F))
                .uv(49, 83).cuboid(-1.0F, -0.6F, 0.0F, 2.0F, 1.0F, 2.0F, new Dilation(-0.1F)), ModelTransform.of(-10.8429F, -14.4987F, -3.9507F, 0.6109F, 1.0472F, 0.0F));

        ModelPartData cube_r56 = console.addChild("cube_r56", ModelPartBuilder.create().uv(43, 70).cuboid(-4.0F, -3.6926F, -12.588F, 6.0F, 1.0F, 4.0F, new Dilation(0.0F))
                .uv(57, 25).cuboid(0.0F, -3.9926F, -6.088F, 2.0F, 1.0F, 2.0F, new Dilation(0.0F))
                .uv(57, 25).cuboid(-4.0F, -3.9926F, -6.088F, 2.0F, 1.0F, 2.0F, new Dilation(0.0F))
                .uv(53, 61).cuboid(3.0F, -3.6926F, -13.488F, 2.0F, 1.0F, 3.0F, new Dilation(0.0F))
                .uv(53, 61).cuboid(-7.0F, -3.6926F, -13.488F, 2.0F, 1.0F, 3.0F, new Dilation(0.0F))
                .uv(74, 47).cuboid(-1.8F, -3.2926F, -7.788F, 1.0F, 0.0F, 1.0F, new Dilation(0.0F))
                .uv(6, 6).cuboid(-9.0F, -3.4926F, -13.488F, 16.0F, 0.0F, 11.0F, new Dilation(0.0F)), ModelTransform.of(-0.1961F, -17.8746F, -1.2679F, 0.6109F, 1.0472F, 0.0F));

        ModelPartData cube_r57 = console.addChild("cube_r57", ModelPartBuilder.create().uv(54, 21).mirrored().cuboid(-4.0F, -3.7926F, -7.488F, 2.0F, 1.0F, 1.0F, new Dilation(0.0F)).mirrored(false)
                .uv(54, 21).cuboid(0.0F, -3.7926F, -7.488F, 2.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(55, 18).cuboid(-3.7F, -4.1926F, -9.588F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(55, 18).cuboid(-2.0F, -4.1926F, -9.588F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(55, 18).cuboid(-2.0F, -4.1926F, -10.988F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(55, 18).cuboid(-3.7F, -4.1926F, -10.988F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(67, 0).cuboid(-4.0F, -3.8926F, -12.488F, 6.0F, 1.0F, 4.0F, new Dilation(0.0F))
                .uv(23, 85).cuboid(1.0F, -3.4926F, -3.988F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(23, 85).cuboid(0.0F, -3.4926F, -5.988F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(23, 85).cuboid(-2.0F, -3.4926F, -5.988F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(23, 85).cuboid(-3.0F, -3.4926F, -5.488F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(23, 85).cuboid(1.0F, -3.4926F, -5.488F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(23, 85).cuboid(0.0F, -3.4926F, -3.488F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(23, 85).cuboid(-2.0F, -3.4926F, -3.488F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(23, 85).cuboid(-3.0F, -3.4926F, -3.988F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(23, 86).cuboid(-4.0F, -3.4926F, -3.988F, 1.0F, 1.0F, 0.0F, new Dilation(0.0F))
                .uv(23, 86).cuboid(-4.0F, -3.4926F, -4.988F, 1.0F, 1.0F, 0.0F, new Dilation(0.0F))
                .uv(23, 86).cuboid(0.5F, -3.4926F, -4.988F, 1.0F, 1.0F, 0.0F, new Dilation(0.0F))
                .uv(23, 86).cuboid(0.5F, -3.4926F, -3.988F, 1.0F, 1.0F, 0.0F, new Dilation(0.0F))
                .uv(23, 84).cuboid(1.5F, -3.4926F, -4.988F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(23, 84).cuboid(-3.5F, -3.4926F, -4.988F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(24, 85).cuboid(-3.0F, -3.4926F, -5.488F, 1.0F, 1.0F, 0.0F, new Dilation(0.0F))
                .uv(24, 85).cuboid(-3.0F, -3.4926F, -3.488F, 1.0F, 1.0F, 0.0F, new Dilation(0.0F))
                .uv(24, 85).cuboid(0.0F, -3.4926F, -3.488F, 1.0F, 1.0F, 0.0F, new Dilation(0.0F))
                .uv(24, 85).cuboid(0.0F, -3.4926F, -5.488F, 1.0F, 1.0F, 0.0F, new Dilation(0.0F))
                .uv(23, 85).cuboid(-2.0F, -3.4926F, -5.988F, 2.0F, 1.0F, 0.0F, new Dilation(0.0F))
                .uv(23, 85).cuboid(-2.0F, -3.4926F, -2.988F, 2.0F, 1.0F, 0.0F, new Dilation(0.0F))
                .uv(68, 44).cuboid(-3.5F, -3.2926F, -5.988F, 5.0F, 0.0F, 3.0F, new Dilation(0.0F))
                .uv(29, 56).cuboid(-6.5F, -3.4926F, -12.488F, 0.0F, 1.0F, 2.0F, new Dilation(0.0F))
                .uv(4, 34).cuboid(-6.5F, -3.4926F, -10.488F, 2.0F, 1.0F, 0.0F, new Dilation(0.0F))
                .uv(29, 56).cuboid(-5.0F, -3.4926F, -12.488F, 0.0F, 1.0F, 2.0F, new Dilation(0.0F))
                .uv(30, 58).cuboid(-6.0F, -3.4926F, -10.988F, 1.0F, 1.0F, 0.0F, new Dilation(0.0F))
                .uv(30, 57).cuboid(-5.5F, -3.4926F, -11.988F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(30, 58).cuboid(-6.0F, -3.4926F, -11.488F, 1.0F, 1.0F, 0.0F, new Dilation(0.0F))
                .uv(30, 57).cuboid(-6.0F, -3.4926F, -11.988F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(4, 39).cuboid(-6.5F, -3.4926F, -11.988F, 2.0F, 1.0F, 0.0F, new Dilation(0.0F))
                .uv(30, 58).cuboid(3.5F, -3.4926F, -11.488F, 1.0F, 1.0F, 0.0F, new Dilation(0.0F))
                .uv(30, 58).cuboid(3.5F, -3.4926F, -10.988F, 1.0F, 1.0F, 0.0F, new Dilation(0.0F))
                .uv(30, 57).cuboid(4.0F, -3.4926F, -11.988F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(30, 57).cuboid(3.5F, -3.4926F, -11.988F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(29, 56).cuboid(3.0F, -3.4926F, -12.488F, 0.0F, 1.0F, 2.0F, new Dilation(0.0F))
                .uv(29, 56).cuboid(4.5F, -3.4926F, -12.488F, 0.0F, 1.0F, 2.0F, new Dilation(0.0F))
                .uv(4, 39).cuboid(3.0F, -3.4926F, -11.988F, 2.0F, 1.0F, 0.0F, new Dilation(0.0F))
                .uv(4, 34).cuboid(3.0F, -3.4926F, -10.488F, 2.0F, 1.0F, 0.0F, new Dilation(0.0F))
                .uv(57, 23).cuboid(2.5F, -3.1926F, -12.488F, 2.0F, 0.0F, 2.0F, new Dilation(0.0F))
                .uv(57, 23).cuboid(-7.0F, -3.1926F, -12.488F, 2.0F, 0.0F, 2.0F, new Dilation(0.0F))
                .uv(0, 74).cuboid(-9.0F, -3.4926F, -13.488F, 16.0F, 0.0F, 11.0F, new Dilation(0.0F)), ModelTransform.of(-1.1962F, -17.8746F, -0.4641F, -2.5307F, 1.0472F, 3.1416F));

        ModelPartData cube_r58 = console.addChild("cube_r58", ModelPartBuilder.create().uv(20, 22).cuboid(0.4F, -0.1F, -1.4F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(20, 22).cuboid(-1.6F, -0.1F, -1.4F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(17, 27).cuboid(-1.6F, -0.1F, -1.4F, 2.0F, 1.0F, 0.0F, new Dilation(0.0F))
                .uv(17, 27).cuboid(-1.6F, -0.1F, -0.4F, 2.0F, 1.0F, 0.0F, new Dilation(0.0F))
                .uv(24, 56).cuboid(-1.6F, 0.3F, -1.4F, 2.0F, 0.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-10.173F, -15.1704F, 7.0281F, -2.5307F, 1.0472F, 3.1416F));

        ModelPartData cube_r59 = console.addChild("cube_r59", ModelPartBuilder.create().uv(18, 53).cuboid(-0.44F, 0.1F, -1.9F, 1.0F, 0.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(7.2421F, -16.6864F, 5.6823F, -2.5307F, -1.0472F, -3.1416F));

        ModelPartData cube_r60 = console.addChild("cube_r60", ModelPartBuilder.create().uv(18, 53).cuboid(-0.35F, 0.1F, -1.9F, 1.0F, 0.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(8.8921F, -16.6864F, 2.8244F, -2.5307F, -1.0472F, -3.1416F));

        ModelPartData cube_r61 = console.addChild("cube_r61", ModelPartBuilder.create().uv(18, 53).cuboid(-0.575F, 0.1F, -1.9F, 1.0F, 0.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(7.9796F, -16.6864F, 4.4049F, -2.5307F, -1.0472F, -3.1416F));

        ModelPartData cube_r62 = console.addChild("cube_r62", ModelPartBuilder.create().uv(20, 4).mirrored().cuboid(-0.5F, 0.0F, 1.2F, 1.0F, 0.0F, 1.0F, new Dilation(0.0F)).mirrored(false)
                .uv(20, 4).mirrored().cuboid(0.7F, 0.0F, 1.2F, 1.0F, 0.0F, 1.0F, new Dilation(0.0F)).mirrored(false)
                .uv(20, 4).mirrored().cuboid(0.7F, 0.0F, 0.0F, 1.0F, 0.0F, 1.0F, new Dilation(0.0F)).mirrored(false)
                .uv(20, 4).mirrored().cuboid(-0.5F, 0.0F, 0.0F, 1.0F, 0.0F, 1.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(11.6923F, -13.7319F, 6.3464F, -2.5307F, -1.0472F, -3.1416F));

        ModelPartData cube_r63 = console.addChild("cube_r63", ModelPartBuilder.create().uv(21, 17).mirrored().cuboid(-0.5F, 0.0F, 0.0F, 1.0F, 0.0F, 1.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(11.3416F, -14.0761F, 5.9707F, -2.5307F, -1.0472F, -3.1416F));

        ModelPartData cube_r64 = console.addChild("cube_r64", ModelPartBuilder.create().uv(21, 17).cuboid(0.5F, -1.0F, 0.25F, 1.0F, 0.0F, 1.0F, new Dilation(0.0F))
                .uv(21, 17).mirrored().cuboid(-0.75F, -1.0F, 1.0F, 1.0F, 0.0F, 1.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(10.5222F, -13.1135F, 6.6524F, -2.5307F, -1.0472F, -3.1416F));

        ModelPartData cube_r65 = console.addChild("cube_r65", ModelPartBuilder.create().uv(21, 17).cuboid(-0.5F, 0.0F, -1.0F, 1.0F, 0.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(11.8534F, -13.3591F, 7.1322F, -0.6109F, 1.0472F, 0.0F));

        ModelPartData cube_r66 = console.addChild("cube_r66", ModelPartBuilder.create().uv(57, 52).cuboid(-1.5F, -1.0F, -5.8F, 6.0F, 2.0F, 3.0F, new Dilation(0.0F))
                .uv(35, 52).cuboid(-3.5F, -1.0F, -2.0F, 7.0F, 2.0F, 4.0F, new Dilation(0.01F)), ModelTransform.of(7.2747F, -15.8427F, 4.2001F, -2.5307F, -1.0472F, -3.1416F));

        ModelPartData cube_r67 = console.addChild("cube_r67", ModelPartBuilder.create().uv(12, 14).cuboid(-4.0F, 0.5F, -1.5F, 0.0F, 1.0F, 2.0F, new Dilation(0.0F))
                .uv(12, 14).cuboid(-5.5F, 0.5F, -1.5F, 0.0F, 1.0F, 2.0F, new Dilation(0.0F))
                .uv(13, 14).cuboid(-5.5F, 0.5F, -2.0F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(13, 14).cuboid(-4.0F, 0.5F, -2.0F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(13, 14).cuboid(-5.5F, 0.5F, 0.0F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(13, 14).cuboid(-4.0F, 0.5F, 0.0F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(13, 15).cuboid(-4.5F, 0.5F, -1.5F, 1.0F, 1.0F, 0.0F, new Dilation(0.0F))
                .uv(13, 15).cuboid(-6.5F, 0.5F, -1.5F, 1.0F, 1.0F, 0.0F, new Dilation(0.0F))
                .uv(13, 15).cuboid(-6.5F, 0.5F, 0.0F, 1.0F, 1.0F, 0.0F, new Dilation(0.0F))
                .uv(13, 15).cuboid(-4.5F, 0.5F, 0.0F, 1.0F, 1.0F, 0.0F, new Dilation(0.0F))
                .uv(12, 14).cuboid(-3.5F, 0.5F, -1.5F, 0.0F, 1.0F, 2.0F, new Dilation(0.0F))
                .uv(12, 14).cuboid(-6.0F, 0.5F, -1.5F, 0.0F, 1.0F, 2.0F, new Dilation(0.0F))
                .uv(12, 14).cuboid(-6.0F, 0.5F, 0.0F, 2.0F, 1.0F, 0.0F, new Dilation(0.0F))
                .uv(12, 14).cuboid(-6.0F, 0.5F, -2.0F, 2.0F, 1.0F, 0.0F, new Dilation(0.0F))
                .uv(83, 63).cuboid(-6.0F, 0.5F, -1.5F, 2.0F, 1.0F, 0.0F, new Dilation(0.0F))
                .uv(83, 63).cuboid(-6.0F, 0.5F, 0.5F, 2.0F, 1.0F, 0.0F, new Dilation(0.0F))
                .uv(24, 53).cuboid(-6.0F, 0.8F, -2.0F, 3.0F, 0.0F, 3.0F, new Dilation(0.0F))
                .uv(69, 52).cuboid(-9.0F, 0.5F, -3.0F, 16.0F, 0.0F, 11.0F, new Dilation(0.0F)), ModelTransform.of(9.6197F, -15.1295F, 6.7086F, -2.5307F, -1.0472F, -3.1416F));

        ModelPartData cube_r68 = console.addChild("cube_r68", ModelPartBuilder.create().uv(15, 39).cuboid(-2.5F, -0.5F, 2.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.2F))
                .uv(9, 14).cuboid(-2.5F, -0.5F, 1.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.2F))
                .uv(51, 0).cuboid(-2.5F, -0.5F, 0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.2F))
                .uv(15, 39).cuboid(-2.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.2F))
                .uv(9, 14).cuboid(-2.5F, -0.5F, -1.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.2F))
                .uv(15, 39).cuboid(-2.5F, -0.5F, -2.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.2F))
                .uv(9, 14).cuboid(-2.5F, -0.5F, -3.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.2F))
                .uv(9, 14).cuboid(-0.5F, -0.5F, 1.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.2F))
                .uv(9, 14).cuboid(-0.5F, -0.5F, -3.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.2F))
                .uv(51, 0).cuboid(-0.5F, -0.5F, -2.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.2F))
                .uv(51, 0).cuboid(-0.5F, -0.5F, -1.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.2F))
                .uv(9, 14).cuboid(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.2F))
                .uv(15, 39).cuboid(-0.5F, -0.5F, 0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.2F))
                .uv(51, 0).cuboid(-0.5F, -0.5F, 2.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.2F)), ModelTransform.of(-1.0F, -15.7934F, 10.3422F, -2.5307F, 0.0F, -3.1416F));

        ModelPartData cube_r69 = console.addChild("cube_r69", ModelPartBuilder.create().uv(22, 36).cuboid(-0.5F, 0.0F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(22, 38).cuboid(0.0F, 0.0F, -0.5F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -17.6204F, 8.3021F, -1.5708F, -0.9599F, 1.5708F));

        ModelPartData cube_r70 = console.addChild("cube_r70", ModelPartBuilder.create().uv(22, 36).cuboid(-0.5F, 0.0F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(22, 38).cuboid(0.0F, 0.0F, -0.5F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -17.0469F, 9.1212F, -1.5708F, -0.9599F, 1.5708F));

        ModelPartData cube_r71 = console.addChild("cube_r71", ModelPartBuilder.create().uv(22, 36).cuboid(-0.5F, 0.0F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(22, 38).cuboid(0.0F, 0.0F, -0.5F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -16.4733F, 9.9404F, -1.5708F, -0.9599F, 1.5708F));

        ModelPartData cube_r72 = console.addChild("cube_r72", ModelPartBuilder.create().uv(22, 36).cuboid(-0.5F, 0.0F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(22, 38).cuboid(0.0F, 0.0F, -0.5F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -15.8997F, 10.7595F, -1.5708F, -0.9599F, 1.5708F));

        ModelPartData cube_r73 = console.addChild("cube_r73", ModelPartBuilder.create().uv(22, 36).cuboid(-0.5F, 0.0F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(22, 38).cuboid(0.0F, 0.0F, -0.5F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -15.3261F, 11.5787F, -1.5708F, -0.9599F, 1.5708F));

        ModelPartData cube_r74 = console.addChild("cube_r74", ModelPartBuilder.create().uv(22, 38).cuboid(0.0F, 0.0F, -0.5F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(22, 36).cuboid(-0.5F, 0.0F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -14.179F, 13.217F, -1.5708F, -0.9599F, 1.5708F));

        ModelPartData cube_r75 = console.addChild("cube_r75", ModelPartBuilder.create().uv(22, 38).cuboid(0.0F, 0.0F, -0.5F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(22, 36).cuboid(-0.5F, 0.0F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -14.7525F, 12.3978F, -1.5708F, -0.9599F, 1.5708F));

        ModelPartData cube_r76 = console.addChild("cube_r76", ModelPartBuilder.create().uv(62, 45).cuboid(3.5F, -0.2F, -1.5F, 2.0F, 1.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(1.0F, -15.1295F, 11.6852F, -2.5307F, 0.0F, -3.1416F));

        ModelPartData cube_r77 = console.addChild("cube_r77", ModelPartBuilder.create().uv(57, 57).cuboid(-6.0F, 0.2F, -2.0F, 3.0F, 1.0F, 3.0F, new Dilation(0.0F))
                .uv(0, 65).cuboid(-2.5F, 0.2F, -2.0F, 3.0F, 1.0F, 7.0F, new Dilation(0.0F))
                .uv(88, 75).cuboid(-3.5F, 0.5F, 7.0F, 1.0F, 1.0F, 0.0F, new Dilation(0.0F))
                .uv(24, 17).cuboid(0.5F, 0.8F, 6.0F, 1.0F, 0.0F, 1.0F, new Dilation(0.0F))
                .uv(24, 17).cuboid(-1.5F, 0.8F, 6.0F, 1.0F, 0.0F, 1.0F, new Dilation(0.0F))
                .uv(26, 17).cuboid(0.75F, 0.6F, 5.3F, 1.0F, 0.0F, 1.0F, new Dilation(0.0F))
                .uv(26, 17).cuboid(-1.25F, 0.6F, 5.3F, 1.0F, 0.0F, 1.0F, new Dilation(0.0F))
                .uv(26, 17).cuboid(-3.25F, 0.6F, 5.3F, 1.0F, 0.0F, 1.0F, new Dilation(0.0F))
                .uv(24, 17).cuboid(-3.5F, 0.8F, 6.0F, 1.0F, 0.0F, 1.0F, new Dilation(0.0F))
                .uv(13, 12).cuboid(-3.5F, 0.5F, 6.0F, 1.0F, 1.0F, 0.0F, new Dilation(0.0F))
                .uv(15, 0).cuboid(-2.5F, 0.5F, 6.0F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(15, 0).cuboid(-3.5F, 0.5F, 6.0F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(88, 75).cuboid(0.5F, 0.5F, 7.0F, 1.0F, 1.0F, 0.0F, new Dilation(0.0F))
                .uv(13, 12).cuboid(0.5F, 0.5F, 6.0F, 1.0F, 1.0F, 0.0F, new Dilation(0.0F))
                .uv(15, 0).cuboid(1.5F, 0.5F, 6.0F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(15, 0).cuboid(0.5F, 0.5F, 6.0F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(15, 0).cuboid(-1.5F, 0.5F, 6.0F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(15, 0).cuboid(-0.5F, 0.5F, 6.0F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(13, 12).cuboid(-1.5F, 0.5F, 6.0F, 1.0F, 1.0F, 0.0F, new Dilation(0.0F))
                .uv(88, 75).cuboid(-1.5F, 0.5F, 7.0F, 1.0F, 1.0F, 0.0F, new Dilation(0.0F))
                .uv(69, 64).cuboid(-9.0F, 0.5F, -3.0F, 16.0F, 0.0F, 11.0F, new Dilation(0.0F)), ModelTransform.of(-1.0F, -15.1295F, 11.6852F, -2.5307F, 0.0F, 3.1416F));

        ModelPartData cube_r78 = console.addChild("cube_r78", ModelPartBuilder.create().uv(61, 49).cuboid(-10.0F, -1.0F, -19.0526F, 21.0F, 2.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-1.0F, -11.1F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData cube_r79 = console.addChild("cube_r79", ModelPartBuilder.create().uv(61, 49).cuboid(-10.0F, -1.0F, -19.0526F, 21.0F, 2.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-0.5F, -11.1F, -0.866F, -3.1416F, -1.0472F, 3.1416F));

        ModelPartData cube_r80 = console.addChild("cube_r80", ModelPartBuilder.create().uv(61, 49).cuboid(-10.0F, -1.0F, -19.0526F, 21.0F, 2.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(1.0F, -11.1F, 0.0F, -3.1416F, 1.0472F, -3.1416F));

        ModelPartData cube_r81 = console.addChild("cube_r81", ModelPartBuilder.create().uv(61, 49).cuboid(-10.0F, -1.0F, -19.0526F, 21.0F, 2.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.5F, -11.1F, -0.866F, -3.1416F, 0.0F, 3.1416F));

        ModelPartData cube_r82 = console.addChild("cube_r82", ModelPartBuilder.create().uv(61, 49).cuboid(-10.0F, -1.0F, -19.0526F, 21.0F, 2.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.5F, -11.1F, 0.866F, 0.0F, 1.0472F, 0.0F));

        ModelPartData cube_r83 = console.addChild("cube_r83", ModelPartBuilder.create().uv(21, 29).cuboid(-11.0F, -0.5F, -16.0394F, 22.0F, 1.0F, 14.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, -12.5F, 3.0131F, -3.1416F, 0.0F, 3.1416F));

        ModelPartData cube_r84 = console.addChild("cube_r84", ModelPartBuilder.create().uv(21, 29).cuboid(-18.817F, -1.0F, -5.5131F, 22.0F, 1.0F, 14.0F, new Dilation(0.0F)), ModelTransform.of(7.817F, -12.0F, 13.5394F, -3.1416F, -1.0472F, 3.1416F));

        ModelPartData cube_r85 = console.addChild("cube_r85", ModelPartBuilder.create().uv(21, 29).cuboid(-13.6095F, -0.5F, -20.5591F, 22.0F, 1.0F, 14.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, -12.5F, 3.0131F, 0.0F, -1.0472F, 0.0F));

        ModelPartData cube_r86 = console.addChild("cube_r86", ModelPartBuilder.create().uv(21, 29).cuboid(-3.183F, -1.0F, -5.5131F, 22.0F, 1.0F, 14.0F, new Dilation(0.0F)), ModelTransform.of(-7.817F, -12.0F, 13.5394F, -3.1416F, 1.0472F, -3.1416F));

        ModelPartData cube_r87 = console.addChild("cube_r87", ModelPartBuilder.create().uv(21, 29).cuboid(-8.3905F, -0.5F, -20.5591F, 22.0F, 1.0F, 14.0F, new Dilation(0.001F)), ModelTransform.of(0.0F, -12.5F, 3.0131F, 0.0F, 1.0472F, 0.0F));

        ModelPartData cube_r88 = console.addChild("cube_r88", ModelPartBuilder.create().uv(27, 44).cuboid(-1.5F, -0.7F, -1.5F, 4.0F, 0.0F, 3.0F, new Dilation(0.0F))
                .uv(16, 40).cuboid(-1.5F, -0.6F, -1.5F, 4.0F, 0.0F, 3.0F, new Dilation(0.0F))
                .uv(18, 44).cuboid(-1.75F, -0.5F, -1.5F, 4.0F, 1.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(-4.65F, -13.0F, -16.1526F, 0.1484F, 0.0F, 0.0F));

        ModelPartData cube_r89 = console.addChild("cube_r89", ModelPartBuilder.create().uv(0, 13).cuboid(-2.0489F, -0.1101F, 0.0F, 1.0F, 1.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(3.3865F, -15.1844F, -17.5526F, 0.0F, 0.0F, 0.1309F));

        ModelPartData cube_r90 = console.addChild("cube_r90", ModelPartBuilder.create().uv(10, 7).cuboid(-1.2654F, 0.6152F, 0.0F, 1.0F, 1.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(3.3865F, -15.1844F, -17.5526F, 0.0F, 0.0F, 1.0908F));

        ModelPartData cube_r91 = console.addChild("cube_r91", ModelPartBuilder.create().uv(10, 8).cuboid(-0.6837F, 0.2297F, 0.0F, 1.0F, 1.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(3.3865F, -15.1844F, -17.5526F, 0.0F, 0.0F, 1.7017F));

        ModelPartData cube_r92 = console.addChild("cube_r92", ModelPartBuilder.create().uv(11, 2).cuboid(-0.7049F, -0.7436F, 0.0F, 1.0F, 1.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(3.3865F, -15.1844F, -17.5526F, 0.0F, 0.0F, 1.6144F));

        ModelPartData cube_r93 = console.addChild("cube_r93", ModelPartBuilder.create().uv(10, 11).cuboid(-1.689F, -1.5863F, 0.0F, 1.0F, 1.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(3.3865F, -15.1844F, -17.5526F, 0.0F, 0.0F, 2.5744F));

        ModelPartData cube_r94 = console.addChild("cube_r94", ModelPartBuilder.create().uv(13, 0).cuboid(-0.8491F, -1.5734F, 0.0F, 1.0F, 1.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(3.3865F, -15.1844F, -17.5526F, 0.0F, 0.0F, 1.8326F));

        ModelPartData cube_r95 = console.addChild("cube_r95", ModelPartBuilder.create().uv(0, 13).cuboid(-2.0489F, -0.1101F, 0.0F, 1.0F, 1.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(3.3865F, -15.1844F, -16.5526F, -3.1416F, 0.0F, 3.0107F));

        ModelPartData cube_r96 = console.addChild("cube_r96", ModelPartBuilder.create().uv(10, 7).cuboid(-1.2654F, 0.6152F, 0.0F, 1.0F, 1.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(3.3865F, -15.1844F, -16.5526F, -3.1416F, 0.0F, 2.0508F));

        ModelPartData cube_r97 = console.addChild("cube_r97", ModelPartBuilder.create().uv(10, 8).cuboid(-0.6837F, 0.2297F, 0.0F, 1.0F, 1.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(3.3865F, -15.1844F, -16.5526F, -3.1416F, 0.0F, 1.4399F));

        ModelPartData cube_r98 = console.addChild("cube_r98", ModelPartBuilder.create().uv(11, 2).cuboid(-0.7049F, -0.7436F, 0.0F, 1.0F, 1.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(3.3865F, -15.1844F, -16.5526F, 3.1416F, 0.0F, 1.5272F));

        ModelPartData cube_r99 = console.addChild("cube_r99", ModelPartBuilder.create().uv(10, 11).cuboid(-1.689F, -1.5863F, 0.0F, 1.0F, 1.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(3.3865F, -15.1844F, -16.5526F, -3.1416F, 0.0F, 0.5672F));

        ModelPartData cube_r100 = console.addChild("cube_r100", ModelPartBuilder.create().uv(13, 0).cuboid(-0.8491F, -1.5734F, 0.0F, 1.0F, 1.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(3.3865F, -15.1844F, -16.5526F, -3.1416F, 0.0F, 1.309F));

        ModelPartData cube_r101 = console.addChild("cube_r101", ModelPartBuilder.create().uv(10, 11).cuboid(-0.5F, -2.5F, 0.0F, 1.0F, 1.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(3.8984F, -16.5938F, -15.5526F, 0.0F, 0.0F, 2.5744F));

        ModelPartData cube_r102 = console.addChild("cube_r102", ModelPartBuilder.create().uv(13, 0).cuboid(-0.5F, -2.5F, 0.0F, 1.0F, 1.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(2.5818F, -15.7615F, -15.5526F, 0.0F, 0.0F, 1.8326F));

        ModelPartData cube_r103 = console.addChild("cube_r103", ModelPartBuilder.create().uv(11, 2).cuboid(-0.5F, -1.5F, 0.0F, 1.0F, 1.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(2.6398F, -15.4221F, -15.5526F, 0.0F, 0.0F, 1.6144F));

        ModelPartData cube_r104 = console.addChild("cube_r104", ModelPartBuilder.create().uv(10, 8).cuboid(-0.5F, -0.5F, 0.0F, 1.0F, 1.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(2.687F, -15.4618F, -15.5526F, 0.0F, 0.0F, 1.7017F));

        ModelPartData cube_r105 = console.addChild("cube_r105", ModelPartBuilder.create().uv(10, 7).cuboid(-0.5F, -0.5F, 0.0F, 1.0F, 1.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(2.0439F, -15.3483F, -15.5526F, 0.0F, 0.0F, 1.0908F));

        ModelPartData cube_r106 = console.addChild("cube_r106", ModelPartBuilder.create().uv(0, 13).cuboid(-0.5F, -0.5F, 0.0F, 1.0F, 1.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(1.8F, -15.0F, -15.5526F, 0.0F, 0.0F, 0.1309F));

        ModelPartData pannel4 = console.addChild("pannel4", ModelPartBuilder.create(), ModelTransform.pivot(-0.6F, -15.8997F, 10.7595F));

        ModelPartData fix = pannel4.addChild("fix", ModelPartBuilder.create(), ModelTransform.of(4.8F, 1.778F, 2.0315F, -2.5307F, 0.0F, -3.1416F));

        ModelPartData control = fix.addChild("control", ModelPartBuilder.create().uv(53, 54).cuboid(-0.5F, -0.6667F, -1.2F, 1.0F, 1.0F, 1.0F, new Dilation(-0.25F))
                .uv(41, 50).cuboid(-0.5F, -0.1667F, -0.55F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(53, 51).cuboid(-0.5F, -0.6667F, -1.45F, 1.0F, 1.0F, 2.0F, new Dilation(-0.25F)), ModelTransform.pivot(0.0F, -0.0246F, 0.3339F));

        ModelPartData fix2 = pannel4.addChild("fix2", ModelPartBuilder.create(), ModelTransform.of(3.3F, 1.778F, 2.0315F, -2.5307F, 0.0F, -3.1416F));

        ModelPartData control2 = fix2.addChild("control2", ModelPartBuilder.create().uv(53, 54).cuboid(-0.5F, -0.6667F, -1.2F, 1.0F, 1.0F, 1.0F, new Dilation(-0.25F))
                .uv(41, 50).cuboid(-0.5F, -0.1667F, -0.55F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(53, 51).cuboid(-0.5F, -0.6667F, -1.45F, 1.0F, 1.0F, 2.0F, new Dilation(-0.25F)), ModelTransform.pivot(0.0F, -0.0246F, 0.3339F));

        ModelPartData fix3 = pannel4.addChild("fix3", ModelPartBuilder.create(), ModelTransform.of(3.3F, 0.9176F, 0.8028F, -2.5307F, 0.0F, -3.1416F));

        ModelPartData control3 = fix3.addChild("control3", ModelPartBuilder.create().uv(53, 54).cuboid(-0.5F, -0.6667F, -1.2F, 1.0F, 1.0F, 1.0F, new Dilation(-0.25F))
                .uv(41, 50).cuboid(-0.5F, -0.1667F, -0.55F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(53, 51).cuboid(-0.5F, -0.6667F, -1.45F, 1.0F, 1.0F, 2.0F, new Dilation(-0.25F)), ModelTransform.pivot(0.0F, -0.0246F, 0.3339F));

        ModelPartData fix4 = pannel4.addChild("fix4", ModelPartBuilder.create(), ModelTransform.of(4.8F, 0.9176F, 0.8028F, -2.5307F, 0.0F, -3.1416F));

        ModelPartData control4 = fix4.addChild("control4", ModelPartBuilder.create().uv(53, 54).cuboid(-0.5F, -0.6667F, -1.2F, 1.0F, 1.0F, 1.0F, new Dilation(-0.25F))
                .uv(41, 50).cuboid(-0.5F, -0.1667F, -0.55F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(53, 51).cuboid(-0.5F, -0.6667F, -1.45F, 1.0F, 1.0F, 2.0F, new Dilation(-0.25F)), ModelTransform.pivot(0.0F, -0.0246F, 0.3339F));

        ModelPartData fix5 = pannel4.addChild("fix5", ModelPartBuilder.create(), ModelTransform.of(0.6F, 1.7207F, 2.4575F, -0.3927F, -0.9599F, 1.5708F));

        ModelPartData bone4 = fix5.addChild("bone4", ModelPartBuilder.create().uv(7, 13).cuboid(-0.5F, -1.1667F, 0.0F, 1.0F, 1.0F, 0.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.1667F, 0.0F));

        ModelPartData fix12 = pannel4.addChild("fix12", ModelPartBuilder.create(), ModelTransform.of(0.6F, 1.1471F, 1.6384F, -0.3927F, -0.9599F, 1.5708F));

        ModelPartData bone10 = fix12.addChild("bone10", ModelPartBuilder.create().uv(7, 13).cuboid(-0.5F, -1.1667F, 0.0F, 1.0F, 1.0F, 0.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.1667F, 0.0F));

        ModelPartData fix7 = pannel4.addChild("fix7", ModelPartBuilder.create(), ModelTransform.of(0.6F, 0.5736F, 0.8192F, -0.3927F, -0.9599F, 1.5708F));

        ModelPartData bone5 = fix7.addChild("bone5", ModelPartBuilder.create().uv(7, 13).cuboid(-0.5F, -1.1667F, 0.0F, 1.0F, 1.0F, 0.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.1667F, 0.0F));

        ModelPartData fix8 = pannel4.addChild("fix8", ModelPartBuilder.create(), ModelTransform.of(0.6F, 0.0F, 0.0F, -0.3927F, -0.9599F, 1.5708F));

        ModelPartData bone6 = fix8.addChild("bone6", ModelPartBuilder.create().uv(7, 13).cuboid(-0.5F, -1.1667F, 0.0F, 1.0F, 1.0F, 0.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.1667F, 0.0F));

        ModelPartData fix9 = pannel4.addChild("fix9", ModelPartBuilder.create(), ModelTransform.of(0.6F, -0.5736F, -0.8191F, -0.3927F, -0.9599F, 1.5708F));

        ModelPartData bone7 = fix9.addChild("bone7", ModelPartBuilder.create().uv(7, 13).cuboid(-0.5F, -1.1667F, 0.0F, 1.0F, 1.0F, 0.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.1667F, 0.0F));

        ModelPartData fix10 = pannel4.addChild("fix10", ModelPartBuilder.create(), ModelTransform.of(0.6F, -1.1472F, -1.6383F, -0.3927F, -0.9599F, 1.5708F));

        ModelPartData bone8 = fix10.addChild("bone8", ModelPartBuilder.create().uv(7, 13).cuboid(-0.5F, -1.1667F, 0.0F, 1.0F, 1.0F, 0.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.1667F, 0.0F));

        ModelPartData fix11 = pannel4.addChild("fix11", ModelPartBuilder.create(), ModelTransform.of(0.6F, -1.7208F, -2.4574F, -0.3927F, -0.9599F, 1.5708F));

        ModelPartData bone9 = fix11.addChild("bone9", ModelPartBuilder.create().uv(7, 13).cuboid(-0.5F, -1.1667F, 0.0F, 1.0F, 1.0F, 0.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.1667F, 0.0F));

        ModelPartData spin2 = pannel4.addChild("spin2", ModelPartBuilder.create(), ModelTransform.of(-2.875F, 0.0945F, 2.0092F, -2.5307F, 0.0F, -3.1416F));

        ModelPartData bone2 = spin2.addChild("bone2", ModelPartBuilder.create().uv(17, 23).cuboid(-0.475F, -0.325F, -0.5F, 1.0F, 3.0F, 1.0F, new Dilation(-0.25F))
                .uv(29, 23).cuboid(-0.575F, 0.175F, -0.5F, 3.0F, 0.0F, 1.0F, new Dilation(0.0F))
                .uv(17, 23).cuboid(1.425F, -1.425F, -0.5F, 1.0F, 2.0F, 1.0F, new Dilation(-0.25F))
                .uv(18, 51).cuboid(1.425F, -1.425F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.1F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData lighton13 = console.addChild("lighton13", ModelPartBuilder.create(), ModelTransform.pivot(-3.0F, -15.4164F, 11.0549F));

        ModelPartData cube_r107 = lighton13.addChild("cube_r107", ModelPartBuilder.create().uv(9, 14).cuboid(-1.5F, -0.4F, 2.1F, 1.0F, 1.0F, 1.0F, new Dilation(-0.1F)), ModelTransform.of(0.0F, 0.0F, 0.0F, -2.5307F, 0.0F, -3.1416F));

        ModelPartData off18 = lighton13.addChild("off18", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData cube_r108 = off18.addChild("cube_r108", ModelPartBuilder.create().uv(22, 71).cuboid(-1.5F, -0.4F, 2.1F, 1.0F, 1.0F, 1.0F, new Dilation(-0.09F)), ModelTransform.of(0.0F, 0.0F, 0.0F, -2.5307F, 0.0F, -3.1416F));

        ModelPartData lighton15 = console.addChild("lighton15", ModelPartBuilder.create(), ModelTransform.pivot(-3.0F, -15.4164F, 11.0549F));

        ModelPartData cube_r109 = lighton15.addChild("cube_r109", ModelPartBuilder.create().uv(15, 39).cuboid(-1.5F, -0.4F, 0.6F, 1.0F, 1.0F, 1.0F, new Dilation(-0.1F)), ModelTransform.of(0.0F, 0.0F, 0.0F, -2.5307F, 0.0F, -3.1416F));

        ModelPartData off20 = lighton15.addChild("off20", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData cube_r110 = off20.addChild("cube_r110", ModelPartBuilder.create().uv(38, 71).cuboid(-1.5F, -0.4F, 0.6F, 1.0F, 1.0F, 1.0F, new Dilation(-0.09F)), ModelTransform.of(0.0F, 0.0F, 0.0F, -2.5307F, 0.0F, -3.1416F));

        ModelPartData lighton16 = console.addChild("lighton16", ModelPartBuilder.create(), ModelTransform.pivot(-3.0F, -15.4164F, 11.0549F));

        ModelPartData cube_r111 = lighton16.addChild("cube_r111", ModelPartBuilder.create().uv(9, 14).cuboid(-0.5F, -0.4F, 0.6F, 1.0F, 1.0F, 1.0F, new Dilation(-0.1F)), ModelTransform.of(0.0F, 0.0F, 0.0F, -2.5307F, 0.0F, -3.1416F));

        ModelPartData off21 = lighton16.addChild("off21", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData cube_r112 = off21.addChild("cube_r112", ModelPartBuilder.create().uv(22, 71).cuboid(-0.5F, -0.4F, 0.6F, 1.0F, 1.0F, 1.0F, new Dilation(-0.09F)), ModelTransform.of(0.0F, 0.0F, 0.0F, -2.5307F, 0.0F, -3.1416F));

        ModelPartData lighton17 = console.addChild("lighton17", ModelPartBuilder.create(), ModelTransform.pivot(-3.0F, -15.4164F, 11.0549F));

        ModelPartData cube_r113 = lighton17.addChild("cube_r113", ModelPartBuilder.create().uv(15, 39).cuboid(0.5F, -0.4F, 0.6F, 1.0F, 1.0F, 1.0F, new Dilation(-0.1F)), ModelTransform.of(0.0F, 0.0F, 0.0F, -2.5307F, 0.0F, -3.1416F));

        ModelPartData off22 = lighton17.addChild("off22", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData cube_r114 = off22.addChild("cube_r114", ModelPartBuilder.create().uv(38, 71).cuboid(0.5F, -0.4F, 0.6F, 1.0F, 1.0F, 1.0F, new Dilation(-0.09F)), ModelTransform.of(0.0F, 0.0F, 0.0F, -2.5307F, 0.0F, -3.1416F));

        ModelPartData lighton14 = console.addChild("lighton14", ModelPartBuilder.create(), ModelTransform.pivot(-3.0F, -15.4164F, 11.0549F));

        ModelPartData cube_r115 = lighton14.addChild("cube_r115", ModelPartBuilder.create().uv(51, 0).cuboid(-0.1F, -0.4F, 2.1F, 1.0F, 1.0F, 1.0F, new Dilation(-0.1F)), ModelTransform.of(0.0F, 0.0F, 0.0F, -2.5307F, 0.0F, -3.1416F));

        ModelPartData off19 = lighton14.addChild("off19", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData cube_r116 = off19.addChild("cube_r116", ModelPartBuilder.create().uv(26, 71).cuboid(-0.1F, -0.4F, 2.1F, 1.0F, 1.0F, 1.0F, new Dilation(-0.09F)), ModelTransform.of(0.0F, 0.0F, 0.0F, -2.5307F, 0.0F, -3.1416F));

        ModelPartData lighton9 = console.addChild("lighton9", ModelPartBuilder.create(), ModelTransform.pivot(3.0F, -15.4164F, 11.0549F));

        ModelPartData cube_r117 = lighton9.addChild("cube_r117", ModelPartBuilder.create().uv(51, 0).cuboid(0.5F, -0.4F, 0.6F, 1.0F, 1.0F, 1.0F, new Dilation(-0.1F)), ModelTransform.of(0.0F, 0.0F, 0.0F, -2.5307F, 0.0F, 3.1416F));

        ModelPartData off14 = lighton9.addChild("off14", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData cube_r118 = off14.addChild("cube_r118", ModelPartBuilder.create().uv(26, 71).cuboid(0.5F, -0.4F, 0.6F, 1.0F, 1.0F, 1.0F, new Dilation(-0.09F)), ModelTransform.of(0.0F, 0.0F, 0.0F, -2.5307F, 0.0F, 3.1416F));

        ModelPartData lighton10 = console.addChild("lighton10", ModelPartBuilder.create(), ModelTransform.pivot(3.0F, -15.4164F, 11.0549F));

        ModelPartData cube_r119 = lighton10.addChild("cube_r119", ModelPartBuilder.create().uv(9, 14).cuboid(0.5F, -0.4F, 2.1F, 1.0F, 1.0F, 1.0F, new Dilation(-0.1F)), ModelTransform.of(0.0F, 0.0F, 0.0F, -2.5307F, 0.0F, 3.1416F));

        ModelPartData off15 = lighton10.addChild("off15", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData cube_r120 = off15.addChild("cube_r120", ModelPartBuilder.create().uv(22, 71).cuboid(0.5F, -0.4F, 2.1F, 1.0F, 1.0F, 1.0F, new Dilation(-0.09F)), ModelTransform.of(0.0F, 0.0F, 0.0F, -2.5307F, 0.0F, 3.1416F));

        ModelPartData lightson = console.addChild("lightson", ModelPartBuilder.create(), ModelTransform.pivot(3.0F, -15.4164F, 11.0549F));

        ModelPartData cube_r121 = lightson.addChild("cube_r121", ModelPartBuilder.create().uv(51, 0).cuboid(-0.9F, -0.4F, 2.1F, 1.0F, 1.0F, 1.0F, new Dilation(-0.1F)), ModelTransform.of(0.0F, 0.0F, 0.0F, -2.5307F, 0.0F, 3.1416F));

        ModelPartData off13 = lightson.addChild("off13", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData cube_r122 = off13.addChild("cube_r122", ModelPartBuilder.create().uv(26, 71).cuboid(-0.9F, -0.4F, 2.1F, 1.0F, 1.0F, 1.0F, new Dilation(-0.09F)), ModelTransform.of(0.0F, 0.0F, 0.0F, -2.5307F, 0.0F, 3.1416F));

        ModelPartData lighton11 = console.addChild("lighton11", ModelPartBuilder.create(), ModelTransform.pivot(3.0F, -15.4164F, 11.0549F));

        ModelPartData cube_r123 = lighton11.addChild("cube_r123", ModelPartBuilder.create().uv(15, 39).cuboid(-0.5F, -0.4F, 0.6F, 1.0F, 1.0F, 1.0F, new Dilation(-0.1F)), ModelTransform.of(0.0F, 0.0F, 0.0F, -2.5307F, 0.0F, 3.1416F));

        ModelPartData off16 = lighton11.addChild("off16", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData cube_r124 = off16.addChild("cube_r124", ModelPartBuilder.create().uv(38, 71).cuboid(-0.5F, -0.4F, 0.6F, 1.0F, 1.0F, 1.0F, new Dilation(-0.09F)), ModelTransform.of(0.0F, 0.0F, 0.0F, -2.5307F, 0.0F, 3.1416F));

        ModelPartData lighton12 = console.addChild("lighton12", ModelPartBuilder.create(), ModelTransform.pivot(3.0F, -15.4164F, 11.0549F));

        ModelPartData cube_r125 = lighton12.addChild("cube_r125", ModelPartBuilder.create().uv(9, 14).cuboid(-1.5F, -0.4F, 0.6F, 1.0F, 1.0F, 1.0F, new Dilation(-0.1F)), ModelTransform.of(0.0F, 0.0F, 0.0F, -2.5307F, 0.0F, 3.1416F));

        ModelPartData off17 = lighton12.addChild("off17", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData cube_r126 = off17.addChild("cube_r126", ModelPartBuilder.create().uv(22, 71).cuboid(-1.5F, -0.4F, 0.6F, 1.0F, 1.0F, 1.0F, new Dilation(-0.09F)), ModelTransform.of(0.0F, 0.0F, 0.0F, -2.5307F, 0.0F, 3.1416F));

        ModelPartData pannel3 = console.addChild("pannel3", ModelPartBuilder.create(), ModelTransform.of(10.0636F, -15.92F, 7.3113F, 0.0F, 1.0472F, 0.0F));

        ModelPartData cube_r127 = pannel3.addChild("cube_r127", ModelPartBuilder.create().uv(45, 50).cuboid(2.4F, 0.7409F, -2.5917F, 1.0F, 1.0F, 1.0F, new Dilation(-0.25F))
                .uv(45, 50).cuboid(2.4F, 0.7409F, -1.5917F, 1.0F, 1.0F, 1.0F, new Dilation(-0.25F))
                .uv(45, 50).cuboid(2.4F, 0.7409F, -0.5917F, 1.0F, 1.0F, 1.0F, new Dilation(-0.25F))
                .uv(45, 50).cuboid(-3.0F, 0.7409F, -0.5917F, 1.0F, 1.0F, 1.0F, new Dilation(-0.25F))
                .uv(45, 50).cuboid(-3.0F, 0.7409F, -1.5917F, 1.0F, 1.0F, 1.0F, new Dilation(-0.25F))
                .uv(45, 50).cuboid(-3.0F, 0.7409F, -2.5917F, 1.0F, 1.0F, 1.0F, new Dilation(-0.25F)), ModelTransform.of(0.0F, 0.0F, 0.0F, -2.5307F, 0.0F, -3.1416F));

        ModelPartData dematlever = pannel3.addChild("dematlever", ModelPartBuilder.create(), ModelTransform.of(1.425F, 2.5757F, -5.7202F, -0.5042F, 0.272F, 0.4531F));

        ModelPartData dematleveryay = dematlever.addChild("dematleveryay", ModelPartBuilder.create().uv(10, 59).cuboid(-0.625F, -6.15F, 0.0F, 1.0F, 4.0F, 0.0F, new Dilation(0.0F))
                .uv(12, 59).cuboid(-0.375F, -6.35F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData dial = pannel3.addChild("dial", ModelPartBuilder.create(), ModelTransform.of(0.7292F, 1.4842F, 0.1516F, -2.5307F, 0.0F, -3.1416F));

        ModelPartData dialfix = dial.addChild("dialfix", ModelPartBuilder.create().uv(51, 2).cuboid(-0.5F, -0.675F, -0.5F, 1.0F, 2.0F, 1.0F, new Dilation(-0.25F))
                .uv(18, 17).cuboid(-0.5F, -0.175F, -0.5F, 1.0F, 0.0F, 1.0F, new Dilation(0.0F))
                .uv(33, 53).cuboid(-0.2401F, 0.025F, -0.7F, 1.0F, 0.0F, 2.0F, new Dilation(0.0F))
                .uv(41, 50).cuboid(-0.5F, -0.675F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(-0.065F, 0.0F, -0.075F));

        ModelPartData spin = pannel3.addChild("spin", ModelPartBuilder.create().uv(41, 50).cuboid(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.1F)), ModelTransform.of(-1.7F, 1.8065F, 0.0678F, -2.5307F, 0.0F, -3.1416F));

        ModelPartData lighton4 = console.addChild("lighton4", ModelPartBuilder.create(), ModelTransform.pivot(7.2142F, -14.3594F, 10.285F));

        ModelPartData cube_r128 = lighton4.addChild("cube_r128", ModelPartBuilder.create().uv(9, 14).cuboid(-0.5F, -1.1F, -0.3F, 1.0F, 1.0F, 1.0F, new Dilation(-0.04F)), ModelTransform.of(0.0F, 0.0F, 0.0F, -2.5307F, -1.0472F, -3.1416F));

        ModelPartData off5 = lighton4.addChild("off5", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData cube_r129 = off5.addChild("cube_r129", ModelPartBuilder.create().uv(22, 71).cuboid(-0.5F, -1.1F, -0.3F, 1.0F, 1.0F, 1.0F, new Dilation(-0.03F)), ModelTransform.of(0.0F, 0.0F, 0.0F, -2.5307F, -1.0472F, -3.1416F));

        ModelPartData lighton5 = console.addChild("lighton5", ModelPartBuilder.create(), ModelTransform.pivot(7.2142F, -14.3594F, 10.285F));

        ModelPartData cube_r130 = lighton5.addChild("cube_r130", ModelPartBuilder.create().uv(15, 39).cuboid(-0.5F, -1.1F, -1.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.04F)), ModelTransform.of(0.0F, 0.0F, 0.0F, -2.5307F, -1.0472F, -3.1416F));

        ModelPartData off6 = lighton5.addChild("off6", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData cube_r131 = off6.addChild("cube_r131", ModelPartBuilder.create().uv(38, 71).cuboid(-0.5F, -1.1F, -1.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.03F)), ModelTransform.of(0.0F, 0.0F, 0.0F, -2.5307F, -1.0472F, -3.1416F));

        ModelPartData lighton6 = console.addChild("lighton6", ModelPartBuilder.create(), ModelTransform.pivot(7.2142F, -14.3594F, 10.285F));

        ModelPartData cube_r132 = lighton6.addChild("cube_r132", ModelPartBuilder.create().uv(51, 0).cuboid(-0.5F, -1.1F, -2.7F, 1.0F, 1.0F, 1.0F, new Dilation(-0.04F)), ModelTransform.of(0.0F, 0.0F, 0.0F, -2.5307F, -1.0472F, -3.1416F));

        ModelPartData off7 = lighton6.addChild("off7", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData cube_r133 = off7.addChild("cube_r133", ModelPartBuilder.create().uv(26, 71).cuboid(-0.5F, -1.1F, -2.7F, 1.0F, 1.0F, 1.0F, new Dilation(-0.03F)), ModelTransform.of(0.0F, 0.0F, 0.0F, -2.5307F, -1.0472F, -3.1416F));

        ModelPartData brightlighton3 = console.addChild("brightlighton3", ModelPartBuilder.create(), ModelTransform.pivot(5.6504F, -18.743F, 3.2622F));

        ModelPartData cube_r134 = brightlighton3.addChild("cube_r134", ModelPartBuilder.create().uv(14, 36).cuboid(1.5F, -0.6F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.09F)), ModelTransform.of(0.0F, 0.0F, 0.0F, -2.5307F, -1.0472F, -3.1416F));

        ModelPartData off10 = brightlighton3.addChild("off10", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData cube_r135 = off10.addChild("cube_r135", ModelPartBuilder.create().uv(10, 36).cuboid(1.5F, -0.6F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.1F)), ModelTransform.of(0.0F, 0.0F, 0.0F, -2.5307F, -1.0472F, -3.1416F));

        ModelPartData brightlighton2 = console.addChild("brightlighton2", ModelPartBuilder.create(), ModelTransform.pivot(5.6504F, -18.743F, 3.2622F));

        ModelPartData cube_r136 = brightlighton2.addChild("cube_r136", ModelPartBuilder.create().uv(14, 36).cuboid(-0.5F, -0.6F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.09F)), ModelTransform.of(0.0F, 0.0F, 0.0F, -2.5307F, -1.0472F, -3.1416F));

        ModelPartData off9 = brightlighton2.addChild("off9", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData cube_r137 = off9.addChild("cube_r137", ModelPartBuilder.create().uv(10, 36).cuboid(-0.5F, -0.6F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.1F)), ModelTransform.of(0.0F, 0.0F, 0.0F, -2.5307F, -1.0472F, -3.1416F));

        ModelPartData brightlighton = console.addChild("brightlighton", ModelPartBuilder.create(), ModelTransform.pivot(5.6504F, -18.743F, 3.2622F));

        ModelPartData cube_r138 = brightlighton.addChild("cube_r138", ModelPartBuilder.create().uv(14, 36).cuboid(-2.5F, -0.6F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.09F)), ModelTransform.of(0.0F, 0.0F, 0.0F, -2.5307F, -1.0472F, -3.1416F));

        ModelPartData off8 = brightlighton.addChild("off8", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData cube_r139 = off8.addChild("cube_r139", ModelPartBuilder.create().uv(10, 36).cuboid(-2.5F, -0.6F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.1F)), ModelTransform.of(0.0F, 0.0F, 0.0F, -2.5307F, -1.0472F, -3.1416F));

        ModelPartData pannel5 = console.addChild("pannel5", ModelPartBuilder.create(), ModelTransform.pivot(-9.2958F, -16.1044F, 6.9258F));

        ModelPartData fix13 = pannel5.addChild("fix13", ModelPartBuilder.create(), ModelTransform.of(-1.7725F, 0.6473F, -2.2675F, -2.5307F, 1.0472F, -3.1416F));

        ModelPartData spin3 = fix13.addChild("spin3", ModelPartBuilder.create().uv(55, 17).cuboid(-0.4752F, -0.9091F, -0.5143F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(-0.0249F, -0.091F, 0.0143F));

        ModelPartData cube_r140 = spin3.addChild("cube_r140", ModelPartBuilder.create().uv(18, 57).cuboid(-1.0F, 0.0F, -1.0F, 2.0F, 0.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(-0.0248F, -0.091F, 0.0143F, 0.0F, -0.7418F, 0.0F));

        ModelPartData fix14 = pannel5.addChild("fix14", ModelPartBuilder.create(), ModelTransform.of(-0.0219F, 0.0F, 0.0F, -2.5307F, 1.0472F, -3.1416F));

        ModelPartData spin4 = fix14.addChild("spin4", ModelPartBuilder.create().uv(55, 18).cuboid(-0.5F, -0.4F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.15F))
                .uv(58, 19).cuboid(-0.5F, -0.6F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(-0.8281F, 0.1F, -0.7F));

        ModelPartData fix16 = pannel5.addChild("fix16", ModelPartBuilder.create(), ModelTransform.of(0.9713F, -0.803F, -0.5734F, -2.5307F, 1.0472F, -3.1416F));

        ModelPartData spin6 = fix16.addChild("spin6", ModelPartBuilder.create().uv(55, 18).cuboid(-0.5F, -0.4F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.15F))
                .uv(58, 19).cuboid(-0.5F, -0.6F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(-0.8281F, 0.1F, -0.7F));

        ModelPartData fix17 = pannel5.addChild("fix17", ModelPartBuilder.create(), ModelTransform.of(0.1212F, -0.803F, -2.0457F, -2.5307F, 1.0472F, -3.1416F));

        ModelPartData spin7 = fix17.addChild("spin7", ModelPartBuilder.create().uv(55, 18).cuboid(-0.5F, -0.4F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.15F))
                .uv(58, 19).cuboid(-0.5F, -0.6F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(-0.8281F, 0.1F, -0.7F));

        ModelPartData fix15 = pannel5.addChild("fix15", ModelPartBuilder.create(), ModelTransform.of(-0.8719F, 0.0F, -1.4722F, -2.5307F, 1.0472F, -3.1416F));

        ModelPartData spin5 = fix15.addChild("spin5", ModelPartBuilder.create().uv(55, 18).cuboid(-0.5F, -0.4F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.15F))
                .uv(58, 19).cuboid(-0.5F, -0.6F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(-0.8281F, 0.1F, -0.7F));

        ModelPartData blinkingon = console.addChild("blinkingon", ModelPartBuilder.create(), ModelTransform.pivot(-1.1962F, -17.8746F, -0.4641F));

        ModelPartData cube_r141 = blinkingon.addChild("cube_r141", ModelPartBuilder.create().uv(18, 53).cuboid(-1.5F, -4.3926F, -7.488F, 1.0F, 1.0F, 1.0F, new Dilation(-0.1F)), ModelTransform.of(0.0F, 0.0F, 0.0F, -2.5307F, 1.0472F, 3.1416F));

        ModelPartData blinkingoff = blinkingon.addChild("blinkingoff", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData cube_r142 = blinkingoff.addChild("cube_r142", ModelPartBuilder.create().uv(39, 15).cuboid(-1.5F, -4.3926F, -7.488F, 1.0F, 1.0F, 1.0F, new Dilation(-0.09F)), ModelTransform.of(0.0F, 0.0F, 0.0F, -2.5307F, 1.0472F, 3.1416F));

        ModelPartData lighton7 = console.addChild("lighton7", ModelPartBuilder.create(), ModelTransform.pivot(-10.8219F, -14.1954F, -6.248F));

        ModelPartData cube_r143 = lighton7.addChild("cube_r143", ModelPartBuilder.create().uv(9, 14).cuboid(-5.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.1F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.6109F, 1.0472F, 0.0F));

        ModelPartData off11 = lighton7.addChild("off11", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData cube_r144 = off11.addChild("cube_r144", ModelPartBuilder.create().uv(22, 71).cuboid(-5.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.09F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.6109F, 1.0472F, 0.0F));

        ModelPartData lighton8 = console.addChild("lighton8", ModelPartBuilder.create(), ModelTransform.pivot(-10.8219F, -14.1954F, -6.248F));

        ModelPartData cube_r145 = lighton8.addChild("cube_r145", ModelPartBuilder.create().uv(9, 14).cuboid(4.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.1F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.6109F, 1.0472F, 0.0F));

        ModelPartData off12 = lighton8.addChild("off12", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData cube_r146 = off12.addChild("cube_r146", ModelPartBuilder.create().uv(22, 71).cuboid(4.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.09F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.6109F, 1.0472F, 0.0F));

        ModelPartData lighton18 = console.addChild("lighton18", ModelPartBuilder.create(), ModelTransform.pivot(-5.7142F, -18.3252F, -3.2991F));

        ModelPartData cube_r147 = lighton18.addChild("cube_r147", ModelPartBuilder.create().uv(51, 0).cuboid(-0.5F, -0.4F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.15F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.6109F, 1.0472F, 0.0F));

        ModelPartData off23 = lighton18.addChild("off23", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData cube_r148 = off23.addChild("cube_r148", ModelPartBuilder.create().uv(26, 71).cuboid(-0.5F, -0.4F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.14F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.6109F, 1.0472F, 0.0F));

        ModelPartData thing = console.addChild("thing", ModelPartBuilder.create().uv(18, 55).cuboid(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.25F)), ModelTransform.of(-7.7771F, -15.7687F, -7.3769F, 0.6109F, 1.0472F, 0.0F));

        ModelPartData thing2 = console.addChild("thing2", ModelPartBuilder.create().uv(18, 55).cuboid(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.25F)), ModelTransform.of(-8.2771F, -15.7687F, -6.5109F, 0.6109F, 1.0472F, 0.0F));

        ModelPartData bone11 = console.addChild("bone11", ModelPartBuilder.create().uv(18, 55).cuboid(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.25F)), ModelTransform.of(-10.4054F, -14.0479F, -7.7396F, 0.6109F, 1.0472F, 0.0F));

        ModelPartData thing3 = console.addChild("thing3", ModelPartBuilder.create().uv(18, 55).cuboid(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.25F)), ModelTransform.of(-9.9054F, -14.0479F, -8.6056F, 0.6109F, 1.0472F, 0.0F));

        ModelPartData fix18 = console.addChild("fix18", ModelPartBuilder.create(), ModelTransform.of(-10.3068F, -15.6453F, -3.7123F, 0.6109F, 1.0472F, 0.0F));

        ModelPartData dial2 = fix18.addChild("dial2", ModelPartBuilder.create().uv(0, 14).cuboid(-0.4116F, -0.7455F, -0.3857F, 1.0F, 1.0F, 0.0F, new Dilation(0.0F))
                .uv(0, 39).cuboid(-0.4116F, -0.2455F, -0.3857F, 1.0F, 0.0F, 1.0F, new Dilation(0.0F))
                .uv(25, 36).cuboid(-1.0116F, 0.0555F, -0.8857F, 1.0F, 0.0F, 1.0F, new Dilation(0.0F))
                .uv(14, 18).cuboid(-0.3116F, -0.2455F, -0.3857F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(14, 18).cuboid(0.5884F, -0.2455F, -0.3857F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(0, 14).cuboid(-0.4116F, -0.7455F, 0.1143F, 1.0F, 1.0F, 0.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData fix19 = console.addChild("fix19", ModelPartBuilder.create(), ModelTransform.of(-9.2068F, -15.6453F, -5.6176F, 0.6109F, 1.0472F, 0.0F));

        ModelPartData dial3 = fix19.addChild("dial3", ModelPartBuilder.create().uv(0, 14).cuboid(-0.4116F, -0.7455F, -0.3856F, 1.0F, 1.0F, 0.0F, new Dilation(0.0F))
                .uv(0, 39).cuboid(-0.4116F, -0.2455F, -0.3856F, 1.0F, 0.0F, 1.0F, new Dilation(0.0F))
                .uv(25, 36).cuboid(-1.0116F, 0.0555F, -0.8856F, 1.0F, 0.0F, 1.0F, new Dilation(0.0F))
                .uv(14, 18).cuboid(-0.3116F, -0.2455F, -0.3856F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(14, 18).cuboid(0.5884F, -0.2455F, -0.3856F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(0, 14).cuboid(-0.4116F, -0.7455F, 0.1144F, 1.0F, 1.0F, 0.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData fix20 = console.addChild("fix20", ModelPartBuilder.create(), ModelTransform.of(-10.6256F, -14.4981F, -6.4367F, 0.6109F, 1.0472F, 0.0F));

        ModelPartData dial4 = fix20.addChild("dial4", ModelPartBuilder.create().uv(0, 14).cuboid(-0.4116F, -0.7455F, -0.3857F, 1.0F, 1.0F, 0.0F, new Dilation(0.0F))
                .uv(0, 39).cuboid(-0.4116F, -0.2455F, -0.3857F, 1.0F, 0.0F, 1.0F, new Dilation(0.0F))
                .uv(25, 36).cuboid(-1.0116F, 0.0555F, -0.8857F, 1.0F, 0.0F, 1.0F, new Dilation(0.0F))
                .uv(14, 18).cuboid(-0.3116F, -0.2455F, -0.3857F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(14, 18).cuboid(0.5884F, -0.2455F, -0.3857F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(0, 14).cuboid(-0.4116F, -0.7455F, 0.1143F, 1.0F, 1.0F, 0.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData fix22 = console.addChild("fix22", ModelPartBuilder.create(), ModelTransform.of(-9.1662F, -15.0717F, -7.3262F, 0.6109F, 1.0472F, 0.0F));

        ModelPartData dial6 = fix22.addChild("dial6", ModelPartBuilder.create().uv(0, 14).cuboid(-0.4116F, -0.7455F, -0.3856F, 1.0F, 1.0F, 0.0F, new Dilation(0.0F))
                .uv(0, 39).cuboid(-0.4116F, -0.2455F, -0.3856F, 1.0F, 0.0F, 1.0F, new Dilation(0.0F))
                .uv(25, 36).cuboid(-1.0116F, 0.0555F, -0.8856F, 1.0F, 0.0F, 1.0F, new Dilation(0.0F))
                .uv(14, 18).cuboid(-0.3116F, -0.2455F, -0.3856F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(14, 18).cuboid(0.5884F, -0.2455F, -0.3856F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(0, 14).cuboid(-0.4116F, -0.7455F, 0.1144F, 1.0F, 1.0F, 0.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData fix21 = console.addChild("fix21", ModelPartBuilder.create(), ModelTransform.of(-11.7256F, -14.4981F, -4.5315F, 0.6109F, 1.0472F, 0.0F));

        ModelPartData dial5 = fix21.addChild("dial5", ModelPartBuilder.create().uv(0, 14).cuboid(-0.4116F, -0.7455F, -0.3856F, 1.0F, 1.0F, 0.0F, new Dilation(0.0F))
                .uv(0, 39).cuboid(-0.4116F, -0.2455F, -0.3856F, 1.0F, 0.0F, 1.0F, new Dilation(0.0F))
                .uv(25, 36).cuboid(-1.0116F, 0.0555F, -0.8856F, 1.0F, 0.0F, 1.0F, new Dilation(0.0F))
                .uv(14, 18).cuboid(-0.3116F, -0.2455F, -0.3856F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(14, 18).cuboid(0.5884F, -0.2455F, -0.3856F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(0, 14).cuboid(-0.4116F, -0.7455F, 0.1144F, 1.0F, 1.0F, 0.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData dividers = console.addChild("dividers", ModelPartBuilder.create(), ModelTransform.of(0.0F, -15.8671F, 0.0F, 0.0F, -0.5236F, 0.0F));

        ModelPartData cube_r149 = dividers.addChild("cube_r149", ModelPartBuilder.create().uv(54, 62).mirrored().cuboid(-0.5F, -6.3198F, 2.6353F, 1.0F, 2.0F, 13.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(0.0F, 0.2351F, 0.0F, 2.5744F, 0.0F, -3.1416F));

        ModelPartData cube_r150 = dividers.addChild("cube_r150", ModelPartBuilder.create().uv(54, 62).cuboid(-0.5F, -1.0F, -6.5F, 1.0F, 2.0F, 13.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.6567F, 10.563F, -0.5672F, 0.0F, 0.0F));

        ModelPartData cube_r151 = dividers.addChild("cube_r151", ModelPartBuilder.create().uv(54, 62).mirrored().cuboid(-0.5F, -6.3198F, 2.6353F, 1.0F, 2.0F, 13.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(0.0F, 0.2351F, 0.0F, 2.5744F, -1.0472F, -3.1416F));

        ModelPartData cube_r152 = dividers.addChild("cube_r152", ModelPartBuilder.create().uv(54, 62).cuboid(-0.5F, -1.0F, -6.5F, 1.0F, 2.0F, 13.0F, new Dilation(0.0F)), ModelTransform.of(9.1478F, 0.6567F, 5.2815F, -0.5672F, 1.0472F, 0.0F));

        ModelPartData cube_r153 = dividers.addChild("cube_r153", ModelPartBuilder.create().uv(54, 62).cuboid(-0.5F, -1.0F, -6.5F, 1.0F, 2.0F, 13.0F, new Dilation(0.0F)), ModelTransform.of(9.1478F, 0.6567F, -5.2815F, 2.5744F, 1.0472F, -3.1416F));

        ModelPartData cube_r154 = dividers.addChild("cube_r154", ModelPartBuilder.create().uv(54, 62).mirrored().cuboid(-0.5F, -0.2F, -6.0F, 1.0F, 2.0F, 13.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(-9.1549F, -0.2866F, 5.2856F, -0.5672F, -1.0472F, 0.0F));

        ModelPartData slide2 = console.addChild("slide2", ModelPartBuilder.create(), ModelTransform.pivot(-2.8F, -15.7237F, -9.9648F));

        ModelPartData cube_r155 = slide2.addChild("cube_r155", ModelPartBuilder.create().uv(16, 3).cuboid(0.2F, -0.2F, -0.25F, 1.0F, 1.0F, 1.0F, new Dilation(-0.25F)), ModelTransform.of(-0.7F, -0.1023F, -0.3769F, 0.6109F, 0.0F, 0.0F));

        ModelPartData slide = console.addChild("slide", ModelPartBuilder.create(), ModelTransform.pivot(-4.5F, -15.1501F, -10.784F));

        ModelPartData cube_r156 = slide.addChild("cube_r156", ModelPartBuilder.create().uv(0, 26).cuboid(-1.5F, -0.2F, -1.25F, 1.0F, 1.0F, 1.0F, new Dilation(-0.25F)), ModelTransform.of(1.0F, -0.6759F, 0.4423F, 0.6109F, 0.0F, 0.0F));

        ModelPartData fix6 = console.addChild("fix6", ModelPartBuilder.create(), ModelTransform.of(3.25F, -14.6157F, -8.6533F, -0.5672F, 0.0F, 0.0F));

        ModelPartData handbreak = fix6.addChild("handbreak", ModelPartBuilder.create(), ModelTransform.pivot(0.25F, -2.6098F, -0.527F));

        ModelPartData cube_r157 = handbreak.addChild("cube_r157", ModelPartBuilder.create().uv(30, 17).cuboid(-0.25F, 0.3681F, -0.3228F, 1.0F, 0.0F, 2.0F, new Dilation(0.0F))
                .uv(30, 17).cuboid(-0.25F, -0.6319F, -0.5228F, 0.0F, 1.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(-0.25F, 0.3598F, -0.923F, 1.6581F, 0.0F, 0.0F));

        ModelPartData cube_r158 = handbreak.addChild("cube_r158", ModelPartBuilder.create().uv(30, 17).cuboid(0.25F, -0.6319F, -0.5228F, 0.0F, 1.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(-0.25F, 0.3598F, -0.923F, 1.4399F, 0.0F, 0.0F));

        ModelPartData cube_r159 = handbreak.addChild("cube_r159", ModelPartBuilder.create().uv(26, 23).cuboid(-0.45F, -1.6653F, -0.5876F, 1.0F, 2.0F, 1.0F, new Dilation(-0.15F))
                .uv(17, 23).cuboid(-0.5F, -0.7653F, -0.5376F, 1.0F, 3.0F, 1.0F, new Dilation(-0.25F)), ModelTransform.of(-0.25F, 0.3598F, -0.923F, 0.6109F, 0.0F, 0.0F));

        ModelPartData flightlighton = console.addChild("flightlighton", ModelPartBuilder.create(), ModelTransform.pivot(4.9F, -14.7117F, -11.7588F));

        ModelPartData cube_r160 = flightlighton.addChild("cube_r160", ModelPartBuilder.create().uv(9, 14).cuboid(-0.5F, -0.5F, -0.2F, 1.0F, 1.0F, 1.0F, new Dilation(-0.01F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.6109F, 0.0F, 0.0F));

        ModelPartData off = flightlighton.addChild("off", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData cube_r161 = off.addChild("cube_r161", ModelPartBuilder.create().uv(22, 71).cuboid(-0.5F, -0.5F, -0.2F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.6109F, 0.0F, 0.0F));

        ModelPartData fix30 = console.addChild("fix30", ModelPartBuilder.create(), ModelTransform.pivot(-4.0F, -13.2492F, -12.6271F));

        ModelPartData thing10 = fix30.addChild("thing10", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData cube_r162 = thing10.addChild("cube_r162", ModelPartBuilder.create().uv(36, 3).cuboid(-2.0F, -0.5F, -0.5F, 4.0F, 1.0F, 1.0F, new Dilation(-0.04F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.6109F, 0.0F, 0.0F));

        ModelPartData thing9 = fix30.addChild("thing9", ModelPartBuilder.create(), ModelTransform.pivot(3.0F, 0.0F, 0.0F));

        ModelPartData cube_r163 = thing9.addChild("cube_r163", ModelPartBuilder.create().uv(41, 1).cuboid(2.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.04F)), ModelTransform.of(-3.0F, 0.0F, 0.0F, 0.6109F, 0.0F, 0.0F));

        ModelPartData thing8 = fix30.addChild("thing8", ModelPartBuilder.create(), ModelTransform.pivot(4.5F, 0.0F, 0.0F));

        ModelPartData cube_r164 = thing8.addChild("cube_r164", ModelPartBuilder.create().uv(41, 1).cuboid(4.0F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.04F)), ModelTransform.of(-4.5F, 0.0F, 0.0F, 0.6109F, 0.0F, 0.0F));

        ModelPartData thing7 = fix30.addChild("thing7", ModelPartBuilder.create(), ModelTransform.pivot(6.0F, 0.0F, 0.0F));

        ModelPartData cube_r165 = thing7.addChild("cube_r165", ModelPartBuilder.create().uv(41, 1).cuboid(5.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(-0.04F)), ModelTransform.of(-6.0F, 0.0F, 0.0F, 0.6109F, 0.0F, 0.0F));

        ModelPartData thing6 = fix30.addChild("thing6", ModelPartBuilder.create(), ModelTransform.pivot(8.5F, 0.0F, 0.0F));

        ModelPartData cube_r166 = thing6.addChild("cube_r166", ModelPartBuilder.create().uv(44, 0).cuboid(7.5F, -0.5F, -0.5F, 2.0F, 1.0F, 1.0F, new Dilation(-0.04F)), ModelTransform.of(-8.5F, 0.0F, 0.0F, 0.6109F, 0.0F, 0.0F));

        ModelPartData fix29 = console.addChild("fix29", ModelPartBuilder.create(), ModelTransform.of(0.0F, -16.7889F, -8.2692F, 2.1817F, 0.0F, 0.0F));

        ModelPartData thing5 = fix29.addChild("thing5", ModelPartBuilder.create().uv(25, 38).cuboid(-1.5F, -0.5F, -0.5F, 3.0F, 1.0F, 1.0F, new Dilation(-0.04F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData fix28 = console.addChild("fix28", ModelPartBuilder.create(), ModelTransform.of(-0.5F, -15.355F, -10.3171F, 2.1817F, 0.0F, 0.0F));

        ModelPartData thing4 = fix28.addChild("thing4", ModelPartBuilder.create().uv(25, 3).cuboid(-1.0F, -0.5F, -0.5F, 2.0F, 1.0F, 1.0F, new Dilation(-0.04F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData fix24 = console.addChild("fix24", ModelPartBuilder.create(), ModelTransform.pivot(1.0F, -15.4369F, -10.3744F));

        ModelPartData dial7 = fix24.addChild("dial7", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData cube_r167 = dial7.addChild("cube_r167", ModelPartBuilder.create().uv(33, 4).cuboid(0.0F, -0.5F, -0.5F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(32, 3).cuboid(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 2.1817F, 0.0F, 0.0F));

        ModelPartData fix25 = console.addChild("fix25", ModelPartBuilder.create(), ModelTransform.of(-2.3F, -13.7162F, -12.8319F, 2.1817F, 0.0F, 0.0F));

        ModelPartData bone3 = fix25.addChild("bone3", ModelPartBuilder.create().uv(33, 4).cuboid(0.0F, -0.5F, -0.5F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(32, 3).cuboid(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData fix26 = console.addChild("fix26", ModelPartBuilder.create(), ModelTransform.of(2.7F, -13.7162F, -12.8319F, 2.1817F, 0.0F, 0.0F));

        ModelPartData bone12 = fix26.addChild("bone12", ModelPartBuilder.create().uv(33, 4).cuboid(0.0F, -0.5F, -0.5F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(32, 3).cuboid(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData fix27 = console.addChild("fix27", ModelPartBuilder.create(), ModelTransform.of(5.2F, -13.7162F, -12.8319F, 2.1817F, 0.0F, 0.0F));

        ModelPartData bone13 = fix27.addChild("bone13", ModelPartBuilder.create().uv(33, 4).cuboid(0.0F, -0.5F, -0.5F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(32, 3).cuboid(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData fix23 = console.addChild("fix23", ModelPartBuilder.create(), ModelTransform.of(-3.25F, -16.8708F, -8.3265F, 2.1817F, 0.0F, 0.0F));

        ModelPartData spin8 = fix23.addChild("spin8", ModelPartBuilder.create().uv(32, 3).cuboid(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(33, 4).cuboid(0.0F, -0.5F, -0.5F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData fix31 = console.addChild("fix31", ModelPartBuilder.create(), ModelTransform.of(-2.3F, -17.3012F, -6.4915F, 0.6109F, 0.0F, 0.0F));

        ModelPartData dial8 = fix31.addChild("dial8", ModelPartBuilder.create().uv(10, 12).cuboid(-0.3F, -0.5F, -0.3F, 1.0F, 1.0F, 1.0F, new Dilation(-0.1F)), ModelTransform.pivot(-0.2F, 0.0F, -0.2F));

        ModelPartData fix32 = console.addChild("fix32", ModelPartBuilder.create(), ModelTransform.of(2.35F, -17.3012F, -6.4915F, 0.6109F, 0.0F, 0.0F));

        ModelPartData dial9 = fix32.addChild("dial9", ModelPartBuilder.create().uv(10, 12).mirrored().cuboid(-0.7F, -0.1F, -0.3F, 1.0F, 1.0F, 1.0F, new Dilation(-0.1F)).mirrored(false), ModelTransform.pivot(0.2F, -0.4F, -0.2F));

        ModelPartData clock = console.addChild("clock", ModelPartBuilder.create(), ModelTransform.of(0.25F, -18.1287F, -6.0943F, 0.6109F, 0.0F, 0.0F));

        ModelPartData minute = clock.addChild("minute", ModelPartBuilder.create().uv(11, 11).cuboid(-0.25F, 0.0F, -0.25F, 1.0F, 0.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(-0.25F, -0.05F, -0.25F));

        ModelPartData hour = clock.addChild("hour", ModelPartBuilder.create().uv(11, 11).cuboid(-0.25F, 0.0F, -0.25F, 1.0F, 0.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(-0.25F, 0.05F, -0.25F));

        ModelPartData rotor = console.addChild("rotor", ModelPartBuilder.create(), ModelTransform.pivot(-0.8079F, -25.4311F, 3.015F));

        ModelPartData bottom = rotor.addChild("bottom", ModelPartBuilder.create().uv(0, 73).cuboid(-0.5F, -5.817F, 2.6102F, 1.0F, 15.0F, 1.0F, new Dilation(0.0F))
                .uv(10, 63).cuboid(-2.0F, 0.6829F, 2.5262F, 4.0F, 1.0F, 1.0F, new Dilation(0.1F))
                .uv(79, 36).cuboid(-3.0F, 0.584F, -3.0484F, 6.0F, 0.0F, 6.0F, new Dilation(0.0F))
                .uv(79, 36).cuboid(-3.0F, 1.774F, -3.0484F, 6.0F, 0.0F, 6.0F, new Dilation(0.0F)), ModelTransform.pivot(0.8079F, -1.1829F, -3.0038F));

        ModelPartData cube_r168 = bottom.addChild("cube_r168", ModelPartBuilder.create().uv(43, 77).cuboid(-2.5F, -8.0F, 0.5F, 5.0F, 0.0F, 6.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 1.083F, 3.819F, 0.0F, 3.1416F, 0.0F));

        ModelPartData cube_r169 = bottom.addChild("cube_r169", ModelPartBuilder.create().uv(10, 63).mirrored().cuboid(-2.0F, -0.5F, -0.5F, 4.0F, 1.0F, 1.0F, new Dilation(0.1F)).mirrored(false), ModelTransform.of(0.0F, 1.1829F, -3.0484F, 0.0F, 3.1416F, 0.0F));

        ModelPartData cube_r170 = bottom.addChild("cube_r170", ModelPartBuilder.create().uv(10, 63).mirrored().cuboid(-2.0F, -0.5F, 2.4641F, 4.0F, 1.0F, 1.0F, new Dilation(0.1F)).mirrored(false), ModelTransform.of(0.0634F, 1.1829F, 0.0255F, 0.0F, 1.0472F, 0.0F));

        ModelPartData cube_r171 = bottom.addChild("cube_r171", ModelPartBuilder.create().uv(10, 63).mirrored().cuboid(-2.0F, -0.5F, -0.5F, 4.0F, 1.0F, 1.0F, new Dilation(0.1F)).mirrored(false), ModelTransform.of(2.6304F, 1.1829F, -1.5298F, 0.0F, 2.0944F, 0.0F));

        ModelPartData cube_r172 = bottom.addChild("cube_r172", ModelPartBuilder.create().uv(10, 63).cuboid(-2.0F, -0.5F, 2.4641F, 4.0F, 1.0F, 1.0F, new Dilation(0.1F)), ModelTransform.of(-0.0634F, 1.1829F, 0.0255F, 0.0F, -1.0472F, 0.0F));

        ModelPartData cube_r173 = bottom.addChild("cube_r173", ModelPartBuilder.create().uv(10, 63).cuboid(-2.0F, -0.5F, -0.5F, 4.0F, 1.0F, 1.0F, new Dilation(0.1F)), ModelTransform.of(-2.6304F, 1.1829F, -1.5298F, 0.0F, -2.0944F, 0.0F));

        ModelPartData cube_r174 = bottom.addChild("cube_r174", ModelPartBuilder.create().uv(0, 73).cuboid(-0.5F, -7.5F, 2.6213F, 1.0F, 15.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 1.6829F, -0.0111F, 0.0F, -1.5708F, 0.0F));

        ModelPartData cube_r175 = bottom.addChild("cube_r175", ModelPartBuilder.create().uv(0, 73).cuboid(-0.5F, -7.5F, 2.6213F, 1.0F, 15.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 1.6829F, -0.0111F, 0.0F, -0.7854F, 0.0F));

        ModelPartData cube_r176 = bottom.addChild("cube_r176", ModelPartBuilder.create().uv(0, 73).cuboid(-0.5F, -7.5F, 2.6213F, 1.0F, 15.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 1.6829F, -0.0111F, 0.0F, 0.7854F, 0.0F));

        ModelPartData cube_r177 = bottom.addChild("cube_r177", ModelPartBuilder.create().uv(0, 73).cuboid(-0.5F, -7.5F, 2.6213F, 1.0F, 15.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 1.6829F, -0.0111F, 0.0F, 1.5708F, 0.0F));

        ModelPartData cube_r178 = bottom.addChild("cube_r178", ModelPartBuilder.create().uv(0, 73).cuboid(-0.5F, -7.5F, 2.6213F, 1.0F, 15.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 1.6829F, -0.0111F, -3.1416F, 0.7854F, 3.1416F));

        ModelPartData cube_r179 = bottom.addChild("cube_r179", ModelPartBuilder.create().uv(0, 73).cuboid(-0.5F, -7.5F, 2.6213F, 1.0F, 15.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 1.6829F, -0.0111F, -3.1416F, 0.0F, 3.1416F));

        ModelPartData cube_r180 = bottom.addChild("cube_r180", ModelPartBuilder.create().uv(0, 73).cuboid(-0.5F, -7.5F, 2.6213F, 1.0F, 15.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 1.6829F, -0.0111F, -3.1416F, -0.7854F, 3.1416F));

        ModelPartData top2 = rotor.addChild("top2", ModelPartBuilder.create().uv(44, 86).cuboid(-3.0143F, 1.0911F, -3.0345F, 6.0F, 0.0F, 6.0F, new Dilation(0.0F))
                .uv(44, 86).cuboid(-3.0143F, -0.0989F, -3.0345F, 6.0F, 0.0F, 6.0F, new Dilation(0.0F))
                .uv(53, 67).cuboid(-2.0F, 0.0F, 2.5373F, 4.0F, 1.0F, 1.0F, new Dilation(0.1F)), ModelTransform.of(0.8079F, -17.5F, -3.015F, 0.0F, -0.3927F, 0.0F));

        ModelPartData cube_r181 = top2.addChild("cube_r181", ModelPartBuilder.create().uv(53, 67).mirrored().cuboid(-2.0F, -0.5F, -0.5F, 4.0F, 1.0F, 1.0F, new Dilation(0.1F)).mirrored(false), ModelTransform.of(0.0F, 0.5F, -3.0373F, 0.0F, 3.1416F, 0.0F));

        ModelPartData cube_r182 = top2.addChild("cube_r182", ModelPartBuilder.create().uv(53, 67).mirrored().cuboid(-2.0F, -0.5F, -0.5F, 4.0F, 1.0F, 1.0F, new Dilation(0.1F)).mirrored(false), ModelTransform.of(2.6304F, 0.5F, -1.5187F, 0.0F, 2.0944F, 0.0F));

        ModelPartData cube_r183 = top2.addChild("cube_r183", ModelPartBuilder.create().uv(53, 67).mirrored().cuboid(-2.0F, -0.4961F, 2.5373F, 4.0F, 1.0F, 1.0F, new Dilation(0.1F)).mirrored(false), ModelTransform.of(0.0F, 0.4961F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData cube_r184 = top2.addChild("cube_r184", ModelPartBuilder.create().uv(53, 67).cuboid(-2.0F, -0.4961F, 2.5373F, 4.0F, 1.0F, 1.0F, new Dilation(0.1F)), ModelTransform.of(0.0F, 0.4961F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData cube_r185 = top2.addChild("cube_r185", ModelPartBuilder.create().uv(53, 67).cuboid(-2.0F, -0.5F, -0.5F, 4.0F, 1.0F, 1.0F, new Dilation(0.1F)), ModelTransform.of(-2.6304F, 0.5F, -1.5187F, 0.0F, -2.0944F, 0.0F));

        ModelPartData cube_r186 = top2.addChild("cube_r186", ModelPartBuilder.create().uv(4, 73).cuboid(-0.5F, -8.0F, 2.6213F, 1.0F, 17.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -0.5F, 0.0F, 0.0F, -1.5708F, 3.1416F));

        ModelPartData cube_r187 = top2.addChild("cube_r187", ModelPartBuilder.create().uv(4, 73).cuboid(-0.5F, -8.0F, 2.6213F, 1.0F, 17.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -0.5F, 0.0F, -3.1416F, -0.7854F, 0.0F));

        ModelPartData cube_r188 = top2.addChild("cube_r188", ModelPartBuilder.create().uv(4, 73).cuboid(-0.5F, -8.0F, 2.6213F, 1.0F, 17.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -0.5F, 0.0F, -3.1416F, 0.0F, 0.0F));

        ModelPartData cube_r189 = top2.addChild("cube_r189", ModelPartBuilder.create().uv(4, 73).cuboid(-0.5F, -8.0F, 2.6213F, 1.0F, 17.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -0.5F, 0.0F, -3.1416F, 0.7854F, 0.0F));

        ModelPartData cube_r190 = top2.addChild("cube_r190", ModelPartBuilder.create().uv(4, 73).cuboid(-0.5F, -8.0F, 2.6213F, 1.0F, 17.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -0.5F, 0.0F, 0.0F, 1.5708F, -3.1416F));

        ModelPartData cube_r191 = top2.addChild("cube_r191", ModelPartBuilder.create().uv(4, 73).cuboid(-0.5F, -8.0F, 2.6213F, 1.0F, 17.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -0.5F, 0.0F, 0.0F, 0.7854F, 3.1416F));

        ModelPartData cube_r192 = top2.addChild("cube_r192", ModelPartBuilder.create().uv(4, 73).cuboid(-0.5F, -8.0F, 2.6213F, 1.0F, 17.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -0.5F, 0.0F, 0.0F, 0.0F, 3.1416F));

        ModelPartData cube_r193 = top2.addChild("cube_r193", ModelPartBuilder.create().uv(4, 73).cuboid(-0.5F, -8.0F, 2.6213F, 1.0F, 17.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -0.5F, 0.0F, 0.0F, -0.7854F, 3.1416F));

        ModelPartData nature = console.addChild("nature", ModelPartBuilder.create().uv(1, 101).cuboid(-0.5F, -5.817F, 2.6102F, 1.0F, 15.0F, 1.0F, new Dilation(0.0F))
                .uv(7, 121).cuboid(-2.0F, 0.6829F, 2.5262F, 4.0F, 1.0F, 1.0F, new Dilation(0.1F))
                .uv(14, 117).cuboid(-3.0F, 0.584F, -3.0484F, 6.0F, 0.0F, 6.0F, new Dilation(0.0F))
                .uv(14, 117).cuboid(-3.0F, 1.774F, -3.0484F, 6.0F, 0.0F, 6.0F, new Dilation(0.0F))
                .uv(8, 101).cuboid(-2.5F, -6.917F, -4.3412F, 5.0F, 16.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -26.614F, 0.0111F));

        ModelPartData cube_r194 = nature.addChild("cube_r194", ModelPartBuilder.create().uv(8, 101).cuboid(-2.5F, -10.5F, -4.3301F, 5.0F, 16.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 3.583F, -0.0111F, 0.0F, -1.0472F, 0.0F));

        ModelPartData cube_r195 = nature.addChild("cube_r195", ModelPartBuilder.create().uv(8, 101).cuboid(-2.5F, -10.5F, -4.3301F, 5.0F, 16.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 3.583F, -0.0111F, 0.0F, -2.0944F, 0.0F));

        ModelPartData cube_r196 = nature.addChild("cube_r196", ModelPartBuilder.create().uv(43, 93).cuboid(-2.5F, -8.0F, 0.5F, 5.0F, 0.0F, 6.0F, new Dilation(0.0F))
                .uv(8, 101).cuboid(-2.5F, -8.0F, -0.5F, 5.0F, 16.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 1.083F, 3.819F, 0.0F, 3.1416F, 0.0F));

        ModelPartData cube_r197 = nature.addChild("cube_r197", ModelPartBuilder.create().uv(8, 101).cuboid(-2.5F, -10.5F, -4.3301F, 5.0F, 16.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 3.583F, -0.0111F, 0.0F, 2.0944F, 0.0F));

        ModelPartData cube_r198 = nature.addChild("cube_r198", ModelPartBuilder.create().uv(8, 101).cuboid(-2.5F, -10.5F, -4.3301F, 5.0F, 16.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 3.583F, -0.0111F, 0.0F, 1.0472F, 0.0F));

        ModelPartData cube_r199 = nature.addChild("cube_r199", ModelPartBuilder.create().uv(7, 121).mirrored().cuboid(-2.0F, -0.5F, -0.5F, 4.0F, 1.0F, 1.0F, new Dilation(0.1F)).mirrored(false), ModelTransform.of(0.0F, 1.1829F, -3.0484F, 0.0F, 3.1416F, 0.0F));

        ModelPartData cube_r200 = nature.addChild("cube_r200", ModelPartBuilder.create().uv(7, 121).mirrored().cuboid(-2.0F, -0.5F, 2.4641F, 4.0F, 1.0F, 1.0F, new Dilation(0.1F)).mirrored(false), ModelTransform.of(0.0634F, 1.1829F, 0.0255F, 0.0F, 1.0472F, 0.0F));

        ModelPartData cube_r201 = nature.addChild("cube_r201", ModelPartBuilder.create().uv(7, 121).mirrored().cuboid(-2.0F, -0.5F, -0.5F, 4.0F, 1.0F, 1.0F, new Dilation(0.1F)).mirrored(false), ModelTransform.of(2.6304F, 1.1829F, -1.5298F, 0.0F, 2.0944F, 0.0F));

        ModelPartData cube_r202 = nature.addChild("cube_r202", ModelPartBuilder.create().uv(7, 121).cuboid(-2.0F, -0.5F, 2.4641F, 4.0F, 1.0F, 1.0F, new Dilation(0.1F)), ModelTransform.of(-0.0634F, 1.1829F, 0.0255F, 0.0F, -1.0472F, 0.0F));

        ModelPartData cube_r203 = nature.addChild("cube_r203", ModelPartBuilder.create().uv(7, 121).cuboid(-2.0F, -0.5F, -0.5F, 4.0F, 1.0F, 1.0F, new Dilation(0.1F)), ModelTransform.of(-2.6304F, 1.1829F, -1.5298F, 0.0F, -2.0944F, 0.0F));

        ModelPartData cube_r204 = nature.addChild("cube_r204", ModelPartBuilder.create().uv(1, 101).cuboid(-0.5F, -7.5F, 2.6213F, 1.0F, 15.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 1.6829F, -0.0111F, 0.0F, -1.5708F, 0.0F));

        ModelPartData cube_r205 = nature.addChild("cube_r205", ModelPartBuilder.create().uv(1, 101).cuboid(-0.5F, -7.5F, 2.6213F, 1.0F, 15.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 1.6829F, -0.0111F, 0.0F, -0.7854F, 0.0F));

        ModelPartData cube_r206 = nature.addChild("cube_r206", ModelPartBuilder.create().uv(1, 101).cuboid(-0.5F, -7.5F, 2.6213F, 1.0F, 15.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 1.6829F, -0.0111F, 0.0F, 0.7854F, 0.0F));

        ModelPartData cube_r207 = nature.addChild("cube_r207", ModelPartBuilder.create().uv(1, 101).cuboid(-0.5F, -7.5F, 2.6213F, 1.0F, 15.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 1.6829F, -0.0111F, 0.0F, 1.5708F, 0.0F));

        ModelPartData cube_r208 = nature.addChild("cube_r208", ModelPartBuilder.create().uv(1, 101).cuboid(-0.5F, -7.5F, 2.6213F, 1.0F, 15.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 1.6829F, -0.0111F, -3.1416F, 0.7854F, 3.1416F));

        ModelPartData cube_r209 = nature.addChild("cube_r209", ModelPartBuilder.create().uv(1, 101).cuboid(-0.5F, -7.5F, 2.6213F, 1.0F, 15.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 1.6829F, -0.0111F, -3.1416F, 0.0F, 3.1416F));

        ModelPartData cube_r210 = nature.addChild("cube_r210", ModelPartBuilder.create().uv(1, 101).cuboid(-0.5F, -7.5F, 2.6213F, 1.0F, 15.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 1.6829F, -0.0111F, -3.1416F, -0.7854F, 3.1416F));

        ModelPartData inside = nature.addChild("inside", ModelPartBuilder.create().uv(8, 85).cuboid(-2.5F, 8.5F, -4.3301F, 5.0F, 16.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -15.417F, -0.0111F));

        ModelPartData cube_r211 = inside.addChild("cube_r211", ModelPartBuilder.create().uv(8, 85).cuboid(-2.5F, -10.5F, -4.3301F, 5.0F, 16.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 19.0F, 0.0F, 0.0F, -2.0944F, 0.0F));

        ModelPartData cube_r212 = inside.addChild("cube_r212", ModelPartBuilder.create().uv(8, 85).cuboid(-2.5F, -10.5F, -4.3301F, 5.0F, 16.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 19.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData cube_r213 = inside.addChild("cube_r213", ModelPartBuilder.create().uv(8, 85).cuboid(-2.5F, -8.0F, -0.5F, 5.0F, 16.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 16.5F, 3.8301F, 0.0F, 3.1416F, 0.0F));

        ModelPartData cube_r214 = inside.addChild("cube_r214", ModelPartBuilder.create().uv(8, 85).cuboid(-2.5F, -10.5F, -4.3301F, 5.0F, 16.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 19.0F, 0.0F, 0.0F, 2.0944F, 0.0F));

        ModelPartData cube_r215 = inside.addChild("cube_r215", ModelPartBuilder.create().uv(8, 85).cuboid(-2.5F, -10.5F, -4.3301F, 5.0F, 16.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 19.0F, 0.0F, 0.0F, 1.0472F, 0.0F));

        ModelPartData underdividers = console.addChild("underdividers", ModelPartBuilder.create().uv(0, 30).cuboid(-0.5F, -7.0F, 4.6278F, 1.0F, 8.0F, 1.0F, new Dilation(0.0F))
                .uv(11, 8).mirrored().cuboid(-0.5F, -1.0F, -5.6278F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F)).mirrored(false)
                .uv(22, 23).cuboid(-0.5F, -56.45F, 5.6278F, 1.0F, 5.0F, 1.0F, new Dilation(0.0F))
                .uv(22, 23).cuboid(-0.5F, -60.45F, 5.6278F, 1.0F, 4.0F, 1.0F, new Dilation(0.0F))
                .uv(22, 23).cuboid(-0.5F, -61.45F, 5.6278F, 1.0F, 2.0F, 1.0F, new Dilation(0.1F)), ModelTransform.of(0.0F, -2.6311F, 0.0F, 0.0F, 0.5236F, 0.0F));

        ModelPartData cube_r216 = underdividers.addChild("cube_r216", ModelPartBuilder.create().uv(0, 23).mirrored().cuboid(-0.5F, 1.0569F, 0.175F, 1.0F, 2.0F, 1.0F, new Dilation(-0.001F)).mirrored(false), ModelTransform.of(8.0849F, 1.2922F, 4.6678F, 1.2654F, -1.0472F, 3.1416F));

        ModelPartData cube_r217 = underdividers.addChild("cube_r217", ModelPartBuilder.create().uv(0, 13).mirrored().cuboid(-1.0F, 1.2562F, -3.0287F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(8.602F, 0.5234F, 4.9664F, 1.8762F, 1.0472F, 0.0F));

        ModelPartData cube_r218 = underdividers.addChild("cube_r218", ModelPartBuilder.create().uv(21, 0).mirrored().cuboid(-0.5F, -1.0522F, -0.8966F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(8.4618F, 1.8688F, 4.8854F, 1.7017F, -1.0472F, -3.1416F));

        ModelPartData cube_r219 = underdividers.addChild("cube_r219", ModelPartBuilder.create().uv(12, 2).mirrored().cuboid(-0.5F, -4.3012F, -0.2325F, 1.0F, 4.0F, 1.0F, new Dilation(-0.001F)).mirrored(false), ModelTransform.of(8.0849F, 1.2922F, 4.6678F, 2.0508F, -1.0472F, 3.1416F));

        ModelPartData cube_r220 = underdividers.addChild("cube_r220", ModelPartBuilder.create().uv(0, 13).cuboid(-1.0F, 1.4066F, 0.5519F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 1.0234F, -9.9328F, -1.8762F, 0.0F, 0.0F));

        ModelPartData cube_r221 = underdividers.addChild("cube_r221", ModelPartBuilder.create().uv(0, 23).cuboid(-0.5F, 1.0569F, 0.175F, 1.0F, 2.0F, 1.0F, new Dilation(-0.001F)), ModelTransform.of(0.0F, 1.2922F, -9.3356F, -1.8762F, 0.0F, 0.0F));

        ModelPartData cube_r222 = underdividers.addChild("cube_r222", ModelPartBuilder.create().uv(21, 0).cuboid(-0.5F, -1.1025F, -0.0372F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 1.0234F, -9.9328F, -1.4399F, 0.0F, 0.0F));

        ModelPartData cube_r223 = underdividers.addChild("cube_r223", ModelPartBuilder.create().uv(12, 2).cuboid(-0.5F, -4.3012F, -0.2325F, 1.0F, 4.0F, 1.0F, new Dilation(-0.001F)), ModelTransform.of(0.0F, 1.2922F, -9.3356F, -1.0908F, 0.0F, 0.0F));

        ModelPartData cube_r224 = underdividers.addChild("cube_r224", ModelPartBuilder.create().uv(0, 13).cuboid(-1.0F, -0.0301F, -2.3954F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(-9.8294F, 0.7406F, 5.675F, 1.8762F, -1.0472F, 0.0F));

        ModelPartData cube_r225 = underdividers.addChild("cube_r225", ModelPartBuilder.create().uv(0, 23).cuboid(-0.5F, 1.0569F, 0.175F, 1.0F, 2.0F, 1.0F, new Dilation(-0.001F)), ModelTransform.of(-8.0849F, 1.2922F, 4.6678F, 1.2654F, 1.0472F, -3.1416F));

        ModelPartData cube_r226 = underdividers.addChild("cube_r226", ModelPartBuilder.create().uv(21, 0).cuboid(-0.5F, -1.0522F, -0.8966F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-8.4618F, 1.8688F, 4.8854F, 1.7017F, 1.0472F, -3.1416F));

        ModelPartData cube_r227 = underdividers.addChild("cube_r227", ModelPartBuilder.create().uv(12, 2).mirrored().cuboid(-0.5F, -4.3012F, -0.2325F, 1.0F, 4.0F, 1.0F, new Dilation(-0.001F)).mirrored(false), ModelTransform.of(-8.0849F, 1.2922F, 4.6678F, 2.0508F, 1.0472F, -3.1416F));

        ModelPartData cube_r228 = underdividers.addChild("cube_r228", ModelPartBuilder.create().uv(22, 23).cuboid(-0.5F, -52.1358F, -0.3535F, 1.0F, 2.0F, 1.0F, new Dilation(0.1F)), ModelTransform.of(-5.18F, -9.3142F, -2.9907F, -3.1416F, -1.0472F, 3.1416F));

        ModelPartData cube_r229 = underdividers.addChild("cube_r229", ModelPartBuilder.create().uv(22, 23).cuboid(-0.5F, -56.55F, 5.6278F, 1.0F, 2.0F, 1.0F, new Dilation(0.1F))
                .uv(22, 23).cuboid(-0.5F, -55.55F, 5.6278F, 1.0F, 4.0F, 1.0F, new Dilation(0.0F))
                .uv(22, 23).cuboid(-0.5F, -51.55F, 5.6278F, 1.0F, 5.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -4.9F, 0.0F, 0.0F, -1.0472F, 0.0F));

        ModelPartData cube_r230 = underdividers.addChild("cube_r230", ModelPartBuilder.create().uv(22, 23).mirrored().cuboid(-0.5F, -2.05F, -0.5F, 1.0F, 2.0F, 1.0F, new Dilation(0.1F)).mirrored(false)
                .uv(22, 23).mirrored().cuboid(-0.5F, -1.05F, -0.5F, 1.0F, 4.0F, 1.0F, new Dilation(0.0F)).mirrored(false)
                .uv(22, 23).mirrored().cuboid(-0.5F, 2.95F, -0.5F, 1.0F, 5.0F, 1.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(0.0F, -59.4F, -6.1278F, 0.0F, 3.1416F, 0.0F));

        ModelPartData cube_r231 = underdividers.addChild("cube_r231", ModelPartBuilder.create().uv(22, 23).cuboid(-0.5F, -52.0892F, -1.949F, 1.0F, 2.0F, 1.0F, new Dilation(0.1F)), ModelTransform.of(6.5616F, -9.3608F, -3.7884F, -3.1416F, 1.0472F, 3.1416F));

        ModelPartData cube_r232 = underdividers.addChild("cube_r232", ModelPartBuilder.create().uv(22, 23).cuboid(-0.5F, -57.45F, 5.4301F, 1.0F, 2.0F, 1.0F, new Dilation(0.1F))
                .uv(22, 23).cuboid(-0.5F, -56.45F, 5.4301F, 1.0F, 4.0F, 1.0F, new Dilation(0.0F))
                .uv(22, 23).cuboid(-0.5F, -52.45F, 5.4301F, 1.0F, 5.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.1712F, -4.0F, 0.0988F, 0.0F, 1.0472F, 0.0F));

        ModelPartData cube_r233 = underdividers.addChild("cube_r233", ModelPartBuilder.create().uv(22, 23).cuboid(-0.5F, -52.1358F, -0.3535F, 1.0F, 4.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-5.18F, -8.3142F, -2.9907F, -3.1416F, -1.0472F, 3.1416F));

        ModelPartData cube_r234 = underdividers.addChild("cube_r234", ModelPartBuilder.create().uv(22, 23).cuboid(-0.5F, -52.0892F, -1.949F, 1.0F, 4.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(6.5616F, -8.3608F, -3.7884F, -3.1416F, 1.0472F, 3.1416F));

        ModelPartData cube_r235 = underdividers.addChild("cube_r235", ModelPartBuilder.create().uv(22, 23).cuboid(-0.5F, -53.0892F, -1.949F, 1.0F, 5.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(6.5616F, -3.3608F, -3.7884F, -3.1416F, 1.0472F, 3.1416F));

        ModelPartData cube_r236 = underdividers.addChild("cube_r236", ModelPartBuilder.create().uv(22, 23).cuboid(-0.5F, -53.1358F, -0.3535F, 1.0F, 5.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-5.18F, -3.3142F, -2.9907F, -3.1416F, -1.0472F, 3.1416F));

        ModelPartData cube_r237 = underdividers.addChild("cube_r237", ModelPartBuilder.create().uv(11, 8).cuboid(-0.5F, -1.0F, -0.5F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-4.4408F, 0.0F, 2.5639F, 0.0F, 2.0944F, 0.0F));

        ModelPartData cube_r238 = underdividers.addChild("cube_r238", ModelPartBuilder.create().uv(10, 40).mirrored().cuboid(2.0128F, -19.0122F, 5.5155F, 1.0F, 16.0F, 3.0F, new Dilation(-0.001F)).mirrored(false), ModelTransform.of(-2.5128F, -5.9648F, -1.4508F, -1.0036F, 0.0F, 0.0F));

        ModelPartData cube_r239 = underdividers.addChild("cube_r239", ModelPartBuilder.create().uv(10, 40).mirrored().cuboid(-3.0128F, -19.0122F, 5.5155F, 1.0F, 16.0F, 3.0F, new Dilation(-0.001F)).mirrored(false), ModelTransform.of(-2.5128F, -5.9648F, -1.4508F, 2.138F, 1.0472F, 3.1416F));

        ModelPartData cube_r240 = underdividers.addChild("cube_r240", ModelPartBuilder.create().uv(0, 30).cuboid(-0.5F, -2.5392F, -2.949F, 1.0F, 8.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(6.5616F, -4.4608F, -3.7884F, -3.1416F, 1.0472F, 3.1416F));

        ModelPartData cube_r241 = underdividers.addChild("cube_r241", ModelPartBuilder.create().uv(10, 40).mirrored().cuboid(-0.5F, -15.3414F, 3.177F, 1.0F, 16.0F, 3.0F, new Dilation(-0.001F)).mirrored(false), ModelTransform.of(-2.5128F, -5.9648F, -1.4508F, 2.138F, -1.0472F, 3.1416F));

        ModelPartData cube_r242 = underdividers.addChild("cube_r242", ModelPartBuilder.create().uv(0, 30).cuboid(-0.5F, -2.5858F, -1.3535F, 1.0F, 8.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-5.18F, -4.4142F, -2.9907F, -3.1416F, -1.0472F, 3.1416F));

        ModelPartData cube_r243 = underdividers.addChild("cube_r243", ModelPartBuilder.create().uv(11, 8).mirrored().cuboid(-0.5F, -1.0F, -0.5F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(4.4408F, 0.0F, 2.5639F, 0.0F, -2.0944F, 0.0F));

        ModelPartData top = console.addChild("top", ModelPartBuilder.create().uv(0, 18).cuboid(-3.384F, 7.7F, -5.9952F, 6.0F, 4.0F, 1.0F, new Dilation(0.0F))
                .uv(15, 19).cuboid(-3.884F, 3.2F, -6.9344F, 7.0F, 3.0F, 1.0F, new Dilation(0.1F))
                .uv(25, 0).cuboid(-3.884F, 6.0F, -6.788F, 7.0F, 2.0F, 1.0F, new Dilation(-0.1F))
                .uv(34, 21).cuboid(-3.384F, -0.75F, -5.9952F, 6.0F, 4.0F, 4.0F, new Dilation(0.0F))
                .uv(34, 20).cuboid(-3.384F, -5.75F, -5.9952F, 6.0F, 5.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(0.384F, -58.2311F, 0.799F));

        ModelPartData cube_r244 = top.addChild("cube_r244", ModelPartBuilder.create().uv(57, 77).cuboid(-6.372F, -66.7F, -6.0313F, 11.0F, 0.0F, 9.0F, new Dilation(0.0F)), ModelTransform.of(-1.274F, 77.9621F, 0.7218F, 0.0F, -1.0472F, 0.0F));

        ModelPartData cube_r245 = top.addChild("cube_r245", ModelPartBuilder.create().uv(15, 18).cuboid(-3.5F, -0.75F, -0.5F, 7.0F, 0.0F, 1.0F, new Dilation(0.0F))
                .uv(15, 18).cuboid(-3.5F, 3.95F, -0.5F, 7.0F, 0.0F, 1.0F, new Dilation(0.0F))
                .uv(15, 18).cuboid(-3.5F, 6.95F, -0.5F, 7.0F, 0.0F, 1.0F, new Dilation(0.0F))
                .uv(15, 18).cuboid(-3.5F, 5.45F, -0.5F, 7.0F, 0.0F, 1.0F, new Dilation(0.0F))
                .uv(15, 18).cuboid(-3.5F, 14.1F, -0.5F, 7.0F, 0.0F, 1.0F, new Dilation(0.0F))
                .uv(15, 18).cuboid(-3.5F, 14.7F, -0.5F, 7.0F, 0.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-0.384F, -5.0F, 4.7631F, 0.0F, 3.1416F, 0.0F));

        ModelPartData cube_r246 = top.addChild("cube_r246", ModelPartBuilder.create().uv(15, 18).cuboid(-3.0F, -1.25F, -5.3301F, 7.0F, 0.0F, 1.0F, new Dilation(0.0F))
                .uv(15, 18).cuboid(-3.0F, 3.45F, -5.3301F, 7.0F, 0.0F, 1.0F, new Dilation(0.0F))
                .uv(15, 18).cuboid(-3.0F, 6.45F, -5.3301F, 7.0F, 0.0F, 1.0F, new Dilation(0.0F))
                .uv(15, 18).cuboid(-3.0F, 4.95F, -5.3301F, 7.0F, 0.0F, 1.0F, new Dilation(0.0F))
                .uv(15, 18).cuboid(-3.0F, 13.6F, -5.3301F, 7.0F, 0.0F, 1.0F, new Dilation(0.0F))
                .uv(15, 18).cuboid(-3.0F, 14.2F, -5.3301F, 7.0F, 0.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-0.7679F, -4.5F, 0.0F, 0.0F, 2.0944F, 0.0F));

        ModelPartData cube_r247 = top.addChild("cube_r247", ModelPartBuilder.create().uv(15, 18).cuboid(-3.0F, -1.25F, 4.3301F, 7.0F, 0.0F, 1.0F, new Dilation(0.0F))
                .uv(15, 18).cuboid(-3.0F, 3.45F, 4.3301F, 7.0F, 0.0F, 1.0F, new Dilation(0.0F))
                .uv(15, 18).cuboid(-3.0F, 6.45F, 4.3301F, 7.0F, 0.0F, 1.0F, new Dilation(0.0F))
                .uv(15, 18).cuboid(-3.0F, 4.95F, 4.3301F, 7.0F, 0.0F, 1.0F, new Dilation(0.0F))
                .uv(15, 18).cuboid(-3.0F, 13.6F, 4.3301F, 7.0F, 0.0F, 1.0F, new Dilation(0.0F))
                .uv(15, 18).cuboid(-3.0F, 14.2F, 4.3301F, 7.0F, 0.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-0.7679F, -4.5F, -1.5981F, 0.0F, -2.0944F, 0.0F));

        ModelPartData cube_r248 = top.addChild("cube_r248", ModelPartBuilder.create().uv(15, 18).cuboid(-3.5F, -0.75F, -0.5F, 7.0F, 0.0F, 1.0F, new Dilation(0.0F))
                .uv(15, 18).cuboid(-3.5F, 3.95F, -0.5F, 7.0F, 0.0F, 1.0F, new Dilation(0.0F))
                .uv(15, 18).cuboid(-3.5F, 6.95F, -0.5F, 7.0F, 0.0F, 1.0F, new Dilation(0.0F))
                .uv(15, 18).cuboid(-3.5F, 5.45F, -0.5F, 7.0F, 0.0F, 1.0F, new Dilation(0.0F))
                .uv(15, 18).cuboid(-3.5F, 14.1F, -0.5F, 7.0F, 0.0F, 1.0F, new Dilation(0.0F))
                .uv(15, 18).cuboid(-3.5F, 14.7F, -0.5F, 7.0F, 0.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-0.384F, -5.0F, -6.3612F, 0.0F, -3.1416F, 0.0F));

        ModelPartData cube_r249 = top.addChild("cube_r249", ModelPartBuilder.create().uv(15, 18).mirrored().cuboid(-4.0F, -1.25F, -5.3301F, 7.0F, 0.0F, 1.0F, new Dilation(0.0F)).mirrored(false)
                .uv(15, 18).mirrored().cuboid(-4.0F, 3.45F, -5.3301F, 7.0F, 0.0F, 1.0F, new Dilation(0.0F)).mirrored(false)
                .uv(15, 18).mirrored().cuboid(-4.0F, 6.45F, -5.3301F, 7.0F, 0.0F, 1.0F, new Dilation(0.0F)).mirrored(false)
                .uv(15, 18).mirrored().cuboid(-4.0F, 4.95F, -5.3301F, 7.0F, 0.0F, 1.0F, new Dilation(0.0F)).mirrored(false)
                .uv(15, 18).mirrored().cuboid(-4.0F, 13.6F, -5.3301F, 7.0F, 0.0F, 1.0F, new Dilation(0.0F)).mirrored(false)
                .uv(15, 18).mirrored().cuboid(-4.0F, 14.2F, -5.3301F, 7.0F, 0.0F, 1.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(0.0F, -4.5F, 0.0F, 0.0F, -2.0944F, 0.0F));

        ModelPartData cube_r250 = top.addChild("cube_r250", ModelPartBuilder.create().uv(15, 18).mirrored().cuboid(-4.0F, -1.25F, 4.3301F, 7.0F, 0.0F, 1.0F, new Dilation(0.0F)).mirrored(false)
                .uv(15, 18).mirrored().cuboid(-4.0F, 3.45F, 4.3301F, 7.0F, 0.0F, 1.0F, new Dilation(0.0F)).mirrored(false)
                .uv(15, 18).mirrored().cuboid(-4.0F, 6.45F, 4.3301F, 7.0F, 0.0F, 1.0F, new Dilation(0.0F)).mirrored(false)
                .uv(15, 18).mirrored().cuboid(-4.0F, 4.95F, 4.3301F, 7.0F, 0.0F, 1.0F, new Dilation(0.0F)).mirrored(false)
                .uv(15, 18).mirrored().cuboid(-4.0F, 13.6F, 4.3301F, 7.0F, 0.0F, 1.0F, new Dilation(0.0F)).mirrored(false)
                .uv(15, 18).mirrored().cuboid(-4.0F, 14.2F, 4.3301F, 7.0F, 0.0F, 1.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(0.0F, -4.5F, -1.5981F, 0.0F, 2.0944F, 0.0F));

        ModelPartData cube_r251 = top.addChild("cube_r251", ModelPartBuilder.create().uv(34, 20).cuboid(-2.5F, -2.25F, -4.3301F, 6.0F, 5.0F, 4.0F, new Dilation(0.0F))
                .uv(34, 21).cuboid(-2.5F, 2.75F, -4.3301F, 6.0F, 4.0F, 4.0F, new Dilation(0.0F))
                .uv(0, 18).cuboid(-2.5F, 11.2F, -4.3301F, 6.0F, 4.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.616F, -3.5F, -0.799F, 0.0F, -2.0944F, 0.0F));

        ModelPartData cube_r252 = top.addChild("cube_r252", ModelPartBuilder.create().uv(31, 17).cuboid(-2.5F, -2.25F, -4.3301F, 6.0F, 5.0F, 7.0F, new Dilation(0.0F))
                .uv(31, 18).cuboid(-2.5F, 2.75F, -4.3301F, 6.0F, 4.0F, 7.0F, new Dilation(0.0F))
                .uv(0, 18).mirrored().cuboid(-2.5F, 11.2F, -4.3301F, 6.0F, 4.0F, 1.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(-0.884F, -3.5F, 0.067F, 0.0F, 2.0944F, 0.0F));

        ModelPartData cube_r253 = top.addChild("cube_r253", ModelPartBuilder.create().uv(34, 20).mirrored().cuboid(-2.5F, -2.25F, -4.3301F, 6.0F, 5.0F, 4.0F, new Dilation(0.0F)).mirrored(false)
                .uv(34, 21).mirrored().cuboid(-2.5F, 2.75F, -4.3301F, 6.0F, 4.0F, 4.0F, new Dilation(0.0F)).mirrored(false)
                .uv(0, 18).mirrored().cuboid(-2.5F, 11.2F, -4.3301F, 6.0F, 4.0F, 1.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(-1.384F, -3.5F, -0.799F, 0.0F, 1.0472F, 0.0F));

        ModelPartData cube_r254 = top.addChild("cube_r254", ModelPartBuilder.create().uv(34, 20).cuboid(-3.0F, -2.25F, -2.0F, 6.0F, 5.0F, 4.0F, new Dilation(0.0F))
                .uv(34, 21).cuboid(-3.0F, 2.75F, -2.0F, 6.0F, 4.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(-0.384F, -3.5F, 2.3971F, 0.0F, 3.1416F, 0.0F));

        ModelPartData cube_r255 = top.addChild("cube_r255", ModelPartBuilder.create().uv(34, 20).cuboid(-2.5F, -2.25F, -4.3301F, 6.0F, 5.0F, 4.0F, new Dilation(0.0F))
                .uv(34, 21).cuboid(-2.5F, 2.75F, -4.3301F, 6.0F, 4.0F, 4.0F, new Dilation(0.0F))
                .uv(0, 18).cuboid(-2.5F, 11.2F, -4.3301F, 6.0F, 4.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.116F, -3.5F, -1.6651F, 0.0F, -1.0472F, 0.0F));

        ModelPartData cube_r256 = top.addChild("cube_r256", ModelPartBuilder.create().uv(29, 56).mirrored().cuboid(-0.5F, -0.5F, 5.289F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)).mirrored(false)
                .uv(29, 56).mirrored().cuboid(0.9849F, 0.9849F, 5.289F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)).mirrored(false)
                .uv(29, 56).mirrored().cuboid(0.9849F, 0.9849F, -6.289F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)).mirrored(false)
                .uv(29, 56).mirrored().cuboid(-0.5F, -0.5F, -6.289F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(-0.384F, 7.0F, -0.799F, 2.2555F, -0.6591F, -2.0344F));

        ModelPartData cube_r257 = top.addChild("cube_r257", ModelPartBuilder.create().uv(29, 56).cuboid(-1.9849F, 0.9849F, 5.289F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(29, 56).cuboid(-1.9849F, 0.9849F, -6.289F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-0.384F, 7.0F, -0.799F, -2.2555F, -0.6591F, 2.0344F));

        ModelPartData cube_r258 = top.addChild("cube_r258", ModelPartBuilder.create().uv(29, 56).mirrored().cuboid(-0.5F, -0.5F, 5.289F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)).mirrored(false)
                .uv(29, 56).mirrored().cuboid(0.9849F, 0.9849F, 5.289F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)).mirrored(false)
                .uv(29, 56).mirrored().cuboid(0.9849F, 0.9849F, -6.289F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)).mirrored(false)
                .uv(29, 56).mirrored().cuboid(-0.5F, -0.5F, -6.289F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(-0.384F, 7.0F, -0.799F, 0.8861F, -0.6591F, -1.1071F));

        ModelPartData cube_r259 = top.addChild("cube_r259", ModelPartBuilder.create().uv(29, 56).cuboid(-1.9849F, 0.9849F, 5.289F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(29, 56).cuboid(-1.9849F, 0.9849F, -6.289F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-0.384F, 7.0F, -0.799F, -0.8861F, -0.6591F, 1.1071F));

        ModelPartData cube_r260 = top.addChild("cube_r260", ModelPartBuilder.create().uv(29, 56).mirrored().cuboid(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)).mirrored(false)
                .uv(29, 56).mirrored().cuboid(-0.5F, -0.5F, -12.0779F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(-0.384F, 7.0F, 4.9899F, 0.0F, 0.0F, -0.7854F));

        ModelPartData cube_r261 = top.addChild("cube_r261", ModelPartBuilder.create().uv(29, 56).mirrored().cuboid(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)).mirrored(false)
                .uv(29, 56).mirrored().cuboid(-0.5F, -0.5F, -12.0779F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(1.716F, 7.0F, 4.9899F, 0.0F, 0.0F, -0.7854F));

        ModelPartData cube_r262 = top.addChild("cube_r262", ModelPartBuilder.create().uv(29, 56).cuboid(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(29, 56).cuboid(-0.5F, -0.5F, -12.0779F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-2.484F, 7.0F, 4.9899F, 0.0F, 0.0F, 0.7854F));

        ModelPartData cube_r263 = top.addChild("cube_r263", ModelPartBuilder.create().uv(25, 0).cuboid(-3.5F, -1.0F, -6.0622F, 7.0F, 2.0F, 1.0F, new Dilation(-0.1F)), ModelTransform.of(-0.4474F, 7.0F, -0.7624F, 0.0F, -1.0472F, 0.0F));

        ModelPartData cube_r264 = top.addChild("cube_r264", ModelPartBuilder.create().uv(25, 0).cuboid(-3.5F, -1.0F, -6.0622F, 7.0F, 2.0F, 1.0F, new Dilation(-0.1F)), ModelTransform.of(-0.4474F, 7.0F, -0.8356F, 0.0F, -2.0944F, 0.0F));

        ModelPartData cube_r265 = top.addChild("cube_r265", ModelPartBuilder.create().uv(25, 0).cuboid(-3.5F, -1.0F, -0.5F, 7.0F, 2.0F, 1.0F, new Dilation(-0.1F)), ModelTransform.of(-0.384F, 7.0F, 4.6899F, 0.0F, 3.1416F, 0.0F));

        ModelPartData cube_r266 = top.addChild("cube_r266", ModelPartBuilder.create().uv(25, 0).mirrored().cuboid(-3.5F, -1.0F, -6.0622F, 7.0F, 2.0F, 1.0F, new Dilation(-0.1F)).mirrored(false), ModelTransform.of(-0.3206F, 7.0F, -0.8356F, 0.0F, 2.0944F, 0.0F));

        ModelPartData cube_r267 = top.addChild("cube_r267", ModelPartBuilder.create().uv(25, 0).mirrored().cuboid(-3.5F, -1.0F, -6.0622F, 7.0F, 2.0F, 1.0F, new Dilation(-0.1F)).mirrored(false), ModelTransform.of(-0.3206F, 7.0F, -0.7624F, 0.0F, 1.0472F, 0.0F));

        ModelPartData cube_r268 = top.addChild("cube_r268", ModelPartBuilder.create().uv(15, 19).mirrored().cuboid(-3.5F, -1.5F, -6.0622F, 7.0F, 3.0F, 1.0F, new Dilation(0.1F)).mirrored(false), ModelTransform.of(-0.4474F, 4.7F, -0.8356F, 0.0F, 1.0472F, 0.0F));

        ModelPartData cube_r269 = top.addChild("cube_r269", ModelPartBuilder.create().uv(15, 19).mirrored().cuboid(-3.5F, -1.5F, -6.0622F, 7.0F, 3.0F, 1.0F, new Dilation(0.1F)).mirrored(false), ModelTransform.of(-0.4474F, 4.7F, -0.7624F, 0.0F, 2.0944F, 0.0F));

        ModelPartData cube_r270 = top.addChild("cube_r270", ModelPartBuilder.create().uv(15, 19).cuboid(-3.5F, -1.5F, -6.0622F, 7.0F, 3.0F, 1.0F, new Dilation(0.1F)), ModelTransform.of(-0.384F, 4.7F, -0.7258F, 0.0F, 3.1416F, 0.0F));

        ModelPartData cube_r271 = top.addChild("cube_r271", ModelPartBuilder.create().uv(15, 19).cuboid(-3.5F, -1.5F, -6.0622F, 7.0F, 3.0F, 1.0F, new Dilation(0.1F)), ModelTransform.of(-0.3206F, 4.7F, -0.7624F, 0.0F, -2.0944F, 0.0F));

        ModelPartData cube_r272 = top.addChild("cube_r272", ModelPartBuilder.create().uv(15, 19).cuboid(-3.5F, -1.5F, -6.0622F, 7.0F, 3.0F, 1.0F, new Dilation(0.1F)), ModelTransform.of(-0.3206F, 4.7F, -0.8356F, 0.0F, -1.0472F, 0.0F));

        ModelPartData cube_r273 = top.addChild("cube_r273", ModelPartBuilder.create().uv(29, 56).cuboid(-1.9142F, 0.9142F, 4.5961F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(29, 56).cuboid(-0.5F, -0.5F, 4.5961F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(29, 56).cuboid(0.9142F, -1.9142F, 4.5961F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(29, 56).cuboid(0.9142F, -1.9142F, -5.5961F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(29, 56).cuboid(-0.5F, -0.5F, -5.5961F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(29, 56).cuboid(-1.9142F, 0.9142F, -5.5961F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-0.384F, 10.8F, -0.799F, -2.2555F, -0.6591F, 2.0344F));

        ModelPartData cube_r274 = top.addChild("cube_r274", ModelPartBuilder.create().uv(29, 56).cuboid(-1.9142F, 0.9142F, 4.5961F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(29, 56).cuboid(-0.5F, -0.5F, 4.5961F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(29, 56).cuboid(0.9142F, -1.9142F, 4.5961F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(29, 56).cuboid(0.9142F, -1.9142F, -5.5961F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(29, 56).cuboid(-0.5F, -0.5F, -5.5961F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(29, 56).cuboid(-1.9142F, 0.9142F, -5.5961F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-0.384F, 10.8F, -0.799F, -0.8861F, -0.6591F, 1.1071F));

        ModelPartData cube_r275 = top.addChild("cube_r275", ModelPartBuilder.create().uv(29, 56).cuboid(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(29, 56).cuboid(-0.5F, -0.5F, -10.6923F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-2.384F, 10.8F, 4.2971F, 0.0F, 0.0F, 0.7854F));

        ModelPartData cube_r276 = top.addChild("cube_r276", ModelPartBuilder.create().uv(29, 56).cuboid(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(29, 56).cuboid(-0.5F, -0.5F, -10.6923F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-0.384F, 10.8F, 4.2971F, 0.0F, 0.0F, 0.7854F));

        ModelPartData cube_r277 = top.addChild("cube_r277", ModelPartBuilder.create().uv(29, 56).cuboid(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(29, 56).cuboid(-0.5F, -0.5F, -10.6923F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(1.616F, 10.8F, 4.2971F, 0.0F, 0.0F, 0.7854F));

        ModelPartData cube_r278 = top.addChild("cube_r278", ModelPartBuilder.create().uv(0, 18).cuboid(-3.0F, -2.0F, -0.5F, 6.0F, 4.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-0.384F, 9.7F, 3.8971F, 0.0F, 3.1416F, 0.0F));

        ModelPartData bone = console.addChild("bone", ModelPartBuilder.create(), ModelTransform.of(10.2685F, -14.6995F, -5.9718F, 0.0F, -1.0472F, 0.0F));

        ModelPartData cube_r279 = bone.addChild("cube_r279", ModelPartBuilder.create().uv(4, 30).mirrored().cuboid(4.7375F, -0.0412F, -0.7948F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)).mirrored(false)
                .uv(17, 23).cuboid(3.5375F, -0.4412F, -0.7948F, 1.0F, 2.0F, 1.0F, new Dilation(-0.25F))
                .uv(4, 30).cuboid(2.2375F, -0.0412F, -0.7948F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(16, 36).cuboid(3.0375F, -0.1412F, -1.2948F, 2.0F, 1.0F, 2.0F, new Dilation(0.0F))
                .uv(31, 20).cuboid(3.5375F, -0.0412F, -2.4948F, 1.0F, 1.0F, 2.0F, new Dilation(0.0F))
                .uv(4, 32).cuboid(-0.4625F, -0.0412F, 2.4052F, 1.0F, 1.0F, 1.0F, new Dilation(-0.25F))
                .uv(49, 44).cuboid(-1.9625F, -0.0412F, -2.5948F, 4.0F, 1.0F, 2.0F, new Dilation(0.0F))
                .uv(22, 36).cuboid(1.3075F, -0.4412F, -1.8948F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(22, 36).cuboid(0.2875F, -0.4412F, -1.8948F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(22, 36).cuboid(-0.7125F, -0.4412F, -1.8948F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(22, 36).cuboid(-1.7125F, -0.4412F, -1.8948F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(4, 30).cuboid(-5.7625F, -0.0412F, -0.7948F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(4, 30).mirrored().cuboid(-3.2625F, -0.0412F, -0.7948F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)).mirrored(false)
                .uv(17, 23).cuboid(-4.4625F, -0.4412F, -0.7948F, 1.0F, 2.0F, 1.0F, new Dilation(-0.25F))
                .uv(16, 36).cuboid(-4.9625F, -0.1412F, -1.2948F, 2.0F, 1.0F, 2.0F, new Dilation(0.0F))
                .uv(31, 20).cuboid(-4.4625F, -0.0412F, -2.4948F, 1.0F, 1.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.6109F, 0.0F, 0.0F));

        ModelPartData cube_r280 = bone.addChild("cube_r280", ModelPartBuilder.create().uv(31, 20).cuboid(-4.5375F, -0.0412F, -1.9052F, 1.0F, 1.0F, 2.0F, new Dilation(0.0F))
                .uv(31, 20).cuboid(3.4625F, -0.0412F, -1.9052F, 1.0F, 1.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 2.5307F, 0.0F, 3.1416F));

        ModelPartData flick4 = bone.addChild("flick4", ModelPartBuilder.create(), ModelTransform.of(-1.5F, 0.5F, -1.25F, 0.9163F, 0.0F, 0.0F));

        ModelPartData cube_r281 = flick4.addChild("cube_r281", ModelPartBuilder.create().uv(24, 38).cuboid(-1.7125F, -1.2412F, -1.3948F, 1.0F, 1.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(1.5F, -0.5F, 1.25F, 0.6109F, 0.0F, 0.0F));

        ModelPartData flick2 = bone.addChild("flick2", ModelPartBuilder.create(), ModelTransform.of(-0.5F, 0.5F, -1.25F, 0.9163F, 0.0F, 0.0F));

        ModelPartData cube_r282 = flick2.addChild("cube_r282", ModelPartBuilder.create().uv(24, 38).cuboid(-1.7125F, -1.2412F, -1.3948F, 1.0F, 1.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(1.5F, -0.5F, 1.25F, 0.6109F, 0.0F, 0.0F));

        ModelPartData flick3 = bone.addChild("flick3", ModelPartBuilder.create(), ModelTransform.of(0.5F, 0.5F, -1.25F, 0.9163F, 0.0F, 0.0F));

        ModelPartData cube_r283 = flick3.addChild("cube_r283", ModelPartBuilder.create().uv(24, 38).cuboid(-1.7125F, -1.2412F, -1.3948F, 1.0F, 1.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(1.5F, -0.5F, 1.25F, 0.6109F, 0.0F, 0.0F));

        ModelPartData flick5 = bone.addChild("flick5", ModelPartBuilder.create(), ModelTransform.of(1.5F, 0.5F, -1.25F, 0.9163F, 0.0F, 0.0F));

        ModelPartData cube_r284 = flick5.addChild("cube_r284", ModelPartBuilder.create().uv(24, 38).cuboid(-1.7125F, -1.2412F, -1.3948F, 1.0F, 1.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(1.5F, -0.5F, 1.25F, 0.6109F, 0.0F, 0.0F));

        ModelPartData lighton3 = bone.addChild("lighton3", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData cube_r285 = lighton3.addChild("cube_r285", ModelPartBuilder.create().uv(15, 39).cuboid(0.9375F, -0.4412F, -0.5948F, 1.0F, 1.0F, 1.0F, new Dilation(-0.04F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.6109F, 0.0F, 0.0F));

        ModelPartData off4 = lighton3.addChild("off4", ModelPartBuilder.create(), ModelTransform.pivot(2.8F, 1.0F, 0.0F));

        ModelPartData cube_r286 = off4.addChild("cube_r286", ModelPartBuilder.create().uv(38, 71).cuboid(-1.8625F, -0.4412F, -0.5948F, 1.0F, 1.0F, 1.0F, new Dilation(-0.03F)), ModelTransform.of(0.0F, -1.0F, 0.0F, 0.6109F, 0.0F, 0.0F));

        ModelPartData lighton2 = bone.addChild("lighton2", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData cube_r287 = lighton2.addChild("cube_r287", ModelPartBuilder.create().uv(15, 39).cuboid(-0.4625F, -0.4412F, -0.5948F, 1.0F, 1.0F, 1.0F, new Dilation(-0.04F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.6109F, 0.0F, 0.0F));

        ModelPartData off3 = lighton2.addChild("off3", ModelPartBuilder.create(), ModelTransform.pivot(1.4F, 1.0F, 0.0F));

        ModelPartData cube_r288 = off3.addChild("cube_r288", ModelPartBuilder.create().uv(38, 71).cuboid(-1.8625F, -0.4412F, -0.5948F, 1.0F, 1.0F, 1.0F, new Dilation(-0.03F)), ModelTransform.of(0.0F, -1.0F, 0.0F, 0.6109F, 0.0F, 0.0F));

        ModelPartData lighton = bone.addChild("lighton", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData cube_r289 = lighton.addChild("cube_r289", ModelPartBuilder.create().uv(9, 14).cuboid(-1.8625F, -0.4412F, -0.5948F, 1.0F, 1.0F, 1.0F, new Dilation(-0.04F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.6109F, 0.0F, 0.0F));

        ModelPartData off2 = lighton.addChild("off2", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 1.0F, 0.0F));

        ModelPartData cube_r290 = off2.addChild("cube_r290", ModelPartBuilder.create().uv(22, 71).cuboid(-1.8625F, -0.4412F, -0.5948F, 1.0F, 1.0F, 1.0F, new Dilation(-0.03F)), ModelTransform.of(0.0F, -1.0F, 0.0F, 0.6109F, 0.0F, 0.0F));

        ModelPartData bone14 = console.addChild("bone14", ModelPartBuilder.create(), ModelTransform.pivot(5.1369F, -17.8042F, -6.5191F));

        ModelPartData bone15 = bone14.addChild("bone15", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData cube_r291 = bone15.addChild("cube_r291", ModelPartBuilder.create().uv(50, 21).cuboid(0.4939F, -2.0403F, -0.3528F, 1.0F, 2.0F, 1.0F, new Dilation(-0.251F))
                .uv(50, 17).cuboid(0.4939F, -2.5403F, -1.3528F, 1.0F, 1.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -1.25F, -2.0F, 1.2198F, 0.0547F, -0.9943F));

        ModelPartData cube_r292 = bone15.addChild("cube_r292", ModelPartBuilder.create().uv(18, 48).cuboid(0.3491F, -0.901F, -0.3528F, 1.0F, 2.0F, 1.0F, new Dilation(-0.25F)), ModelTransform.of(0.0F, -1.25F, -2.0F, 1.2103F, -0.2319F, -0.8878F));

        ModelPartData cube_r293 = bone15.addChild("cube_r293", ModelPartBuilder.create().uv(34, 17).cuboid(0.6383F, -0.1859F, -0.3538F, 1.0F, 2.0F, 1.0F, new Dilation(-0.251F)), ModelTransform.of(0.0F, -1.25F, -2.0F, 1.061F, -0.7904F, -0.5961F));

        ModelPartData cube_r294 = bone15.addChild("cube_r294", ModelPartBuilder.create().uv(31, 50).cuboid(1.1679F, 0.5804F, -0.3528F, 1.0F, 2.0F, 1.0F, new Dilation(-0.25F)), ModelTransform.of(0.0F, -1.25F, -2.0F, 0.6413F, -1.128F, -0.094F));

        ModelPartData toolbox = modelPartData.addChild("toolbox", ModelPartBuilder.create().uv(62, 86).cuboid(-4.1786F, 2.8588F, -2.0509F, 9.0F, 0.0F, 4.0F, new Dilation(0.0F))
                .uv(68, 161).cuboid(-4.1786F, -0.1412F, 1.9491F, 9.0F, 3.0F, 0.0F, new Dilation(0.0F))
                .uv(68, 161).cuboid(-4.1786F, -0.1412F, -2.0509F, 9.0F, 3.0F, 0.0F, new Dilation(0.0F))
                .uv(13, 65).cuboid(4.8214F, -0.1412F, -2.0509F, 0.0F, 3.0F, 4.0F, new Dilation(0.0F))
                .uv(78, 160).cuboid(-4.1786F, -1.9412F, -0.5509F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(68, 159).cuboid(4.8214F, -1.9412F, -0.5509F, 0.0F, 1.0F, 1.0F, new Dilation(0.003F))
                .uv(31, 40).cuboid(4.8214F, -1.1412F, -1.0509F, 0.0F, 1.0F, 2.0F, new Dilation(0.0F))
                .uv(13, 65).cuboid(-4.1786F, -0.1412F, -2.0509F, 0.0F, 3.0F, 4.0F, new Dilation(0.0F))
                .uv(31, 40).cuboid(-4.1786F, -1.1412F, -1.0509F, 0.0F, 1.0F, 2.0F, new Dilation(0.0F))
                .uv(68, 159).cuboid(-4.1786F, -1.9412F, -0.5509F, 0.0F, 1.0F, 1.0F, new Dilation(0.003F)), ModelTransform.of(-12.0854F, 21.1412F, -7.4027F, 0.0F, 1.309F, 0.0F));

        ModelPartData cube_r295 = toolbox.addChild("cube_r295", ModelPartBuilder.create().uv(18, 43).cuboid(0.0F, -1.087F, -1.1637F, 0.0F, 3.0F, 1.0F, new Dilation(0.002F))
                .uv(18, 43).cuboid(9.0F, -1.087F, -1.1637F, 0.0F, 3.0F, 1.0F, new Dilation(0.002F)), ModelTransform.of(-4.1786F, -0.672F, 0.1271F, -0.7854F, 0.0F, 0.0F));

        ModelPartData cube_r296 = toolbox.addChild("cube_r296", ModelPartBuilder.create().uv(15, 7).cuboid(0.0F, -2.1637F, -0.913F, 0.0F, 3.0F, 1.0F, new Dilation(0.002F))
                .uv(15, 7).cuboid(9.0F, -2.1637F, -0.913F, 0.0F, 3.0F, 1.0F, new Dilation(0.002F)), ModelTransform.of(-4.1786F, -0.672F, 0.1271F, -2.3562F, 0.0F, 0.0F));

        ModelPartData lid = toolbox.addChild("lid", ModelPartBuilder.create(), ModelTransform.pivot(-1.4085F, -0.1422F, -2.0496F));

        ModelPartData cube_r297 = lid.addChild("cube_r297", ModelPartBuilder.create().uv(15, 11).cuboid(-3.0F, -1.5892F, -2.0142F, 0.0F, 2.0F, 1.0F, new Dilation(0.0F))
                .uv(23, 3).cuboid(3.0F, -2.0892F, -2.0142F, 0.0F, 2.0F, 1.0F, new Dilation(0.0F))
                .uv(15, 11).cuboid(0.0F, -1.5892F, -2.0142F, 0.0F, 2.0F, 1.0F, new Dilation(0.0F))
                .uv(78, 12).cuboid(-4.5F, -2.1892F, -2.0142F, 9.0F, 3.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(1.73F, 0.851F, 1.9988F, -0.7854F, 0.0F, 0.0F));

        ModelPartData lid2 = toolbox.addChild("lid2", ModelPartBuilder.create().uv(77, 47).cuboid(-4.5F, -1.8197F, -2.4697F, 9.0F, 1.0F, 1.0F, new Dilation(0.01F))
                .uv(80, 46).cuboid(-4.5F, -1.3197F, -2.4697F, 9.0F, 0.0F, 1.0F, new Dilation(0.01F))
                .uv(58, 16).cuboid(-1.5F, -3.8197F, -1.9697F, 3.0F, 2.0F, 0.0F, new Dilation(0.01F)), ModelTransform.pivot(0.3214F, -0.1215F, 1.9188F));

        ModelPartData cube_r298 = lid2.addChild("cube_r298", ModelPartBuilder.create().uv(15, 11).cuboid(-2.0F, -1.0F, -0.5F, 0.0F, 2.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -0.2318F, -0.8889F, -2.3562F, 0.0F, 0.0F));

        ModelPartData cube_r299 = lid2.addChild("cube_r299", ModelPartBuilder.create().uv(23, 2).cuboid(1.0F, -0.5868F, 1.0153F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(23, 2).cuboid(3.0F, -0.5868F, 1.0153F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(83, 0).cuboid(-4.5F, -2.1868F, 2.0153F, 9.0F, 3.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.8303F, -1.9697F, 0.7854F, 0.0F, 0.0F));

        ModelPartData hammer = toolbox.addChild("hammer", ModelPartBuilder.create(), ModelTransform.of(5.0656F, 1.3092F, -0.7743F, -1.5708F, -1.0472F, -3.1416F));

        ModelPartData bone17 = hammer.addChild("bone17", ModelPartBuilder.create(), ModelTransform.of(0.1628F, -0.8422F, -0.2597F, 0.0392F, 0.0192F, 1.0428F));

        ModelPartData cube_r300 = bone17.addChild("cube_r300", ModelPartBuilder.create().uv(112, 74).cuboid(-3.0F, -1.0F, 0.0F, 3.0F, 2.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(2.3F, -0.2508F, -0.0114F, 3.1378F, -0.1139F, 3.1333F));

        ModelPartData bone16 = bone17.addChild("bone16", ModelPartBuilder.create().uv(115, 63).cuboid(-0.5F, -0.5F, -0.5F, 6.0F, 1.0F, 1.0F, new Dilation(-0.1F))
                .uv(112, 56).cuboid(5.25F, -1.5F, -1.0F, 2.0F, 3.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(2.0F, -0.0008F, -0.0114F));
        return TexturedModelData.of(modelData, 256, 256);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red,
                       float green, float blue, float alpha) {
        matrices.push();

        matrices.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(180f));

        console.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
        toolbox.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);

        matrices.pop();
    }

    @Override
    public void renderWithAnimations(ConsoleBlockEntity console, ClientTardis tardis, ModelPart root, MatrixStack matrices,
                                     VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float pAlpha) {
        matrices.push();
        matrices.translate(0.5f, -1.5f, -0.5f);

        matrices.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(180f));

        // Throttle Control
        ModelPart throttle = this.dematleveryay;
        throttle.roll = throttle.roll - ((tardis.travel().speed() / (float) tardis.travel().maxSpeed().get()) * 1f);

        // Handbrake Control and Lights
        ModelPart handbrake = this.console.getChild("fix6");
        handbrake.pitch = !tardis.travel().handbrake() ? handbrake.pitch + 1.2f : handbrake.pitch;
//		ModelPart handbrakeLights = this.console.getChild("panel4").getChild("flightlights").getChild("handbrakelights")
//				.getChild("handbrakelights2");


        // Power Switch and Lights
        ModelPart power = this.console.getChild("pannel4").getChild("spin2").getChild("bone2");
        power.yaw = tardis.fuel().hasPower() ? power.yaw : power.yaw - 1.55f;

        // Anti Gravity Control
//		ModelPart antigravs = this.console.getChild("panel1").getChild("controls").getChild("faucettaps1")
//				.getChild("pivot2");
//		antigravs.yaw = tardis.travel().antigravs().get() ? antigravs.yaw - 1.58f : antigravs.yaw;
//
        // Door Locking Mechanism Control
        ModelPart doorlock = this.fix9;
        doorlock.pitch = tardis.door().locked() ? doorlock.pitch - 1.55f : doorlock.pitch;

        // Door Control
        ModelPart doorControl = this.fix11;
        doorControl.pitch = tardis.door().isRightOpen()
                ? doorControl.pitch - 1.55f
                : tardis.door().isLeftOpen() ? doorControl.pitch - 1f : doorControl.pitch;

        // Alarm Control and Lights
        ModelPart alarms = this.dial2;
        //ModelPart alarmsLight = this.console.getChild("panel4").getChild("yellow3");
        //alarmsLight.pivotY = (tardis.alarm().isEnabled()) ? alarmsLight.pivotY : alarmsLight.pivotY + 1;
        alarms.yaw = tardis.alarm().isEnabled() ? alarms.yaw + 1f : alarms.yaw - 0.9f;

        // Hail Mary
        ModelPart hailmary = this.dial3;
        hailmary.yaw = tardis.stats().hailMary().get() ? hailmary.yaw + 1f : hailmary.yaw - 0.9f;

        // Cloak
        ModelPart cloak = this.dial5;
        cloak.yaw = tardis.<CloakHandler>handler(TardisComponent.Id.CLOAK).cloaked().get() ? cloak.yaw + 1f : cloak.yaw - 0.9f;

        // Siege Mode
        ModelPart siege = this.dial6;
        siege.yaw = tardis.siege().isActive() ? siege.yaw + 1f : siege.yaw - 0.9f;

        // Shields
        ModelPart shields = this.dial4;
        shields.yaw = tardis.areVisualShieldsActive() ? shields.yaw + 1f : tardis.areShieldsActive() ? shields.yaw : shields.yaw - 0.9f;

        ModelPart security = this.flick5;
        security.pitch = tardis.stats().security().get() ? security.pitch - 2f : security.pitch;

//		// Ground Search Control
//		ModelPart groundSearch = this.console.getChild("panel1").getChild("controls").getChild("smallswitch");
//		groundSearch.pitch = tardis.travel().horizontalSearch().get()
//				? groundSearch.pitch + 1f
//				: groundSearch.pitch - 0.75f; // FIXME use TravelHandler#horizontalSearch/#verticalSearch
//
        // Direction Control
        ModelPart direction = this.dialfix;
        direction.yaw = direction.yaw + tardis.travel().destination().getRotation();

        // Increment Control
        ModelPart increment = this.slide2;
        ModelPart increment2 = this.slide;
        increment.pivotX = IncrementManager.increment(tardis) >= 10
                ? IncrementManager.increment(tardis) >= 100
                ? IncrementManager.increment(tardis) >= 1000
                ? IncrementManager.increment(tardis) >= 10000
                ? increment.pivotX - 1.5f
                : increment.pivotX - 1.25f
                : increment.pivotX - 1f
                : increment.pivotX - 0.5f
                : increment.pivotX - 1;
        increment2.pivotX = IncrementManager.increment(tardis) >= 10
                ? IncrementManager.increment(tardis) >= 100
                ? IncrementManager.increment(tardis) >= 1000
                ? IncrementManager.increment(tardis) >= 10000
                ? increment2.pivotX + 1.3f
                : increment2.pivotX + 1.15f
                : increment2.pivotX + 1f
                : increment2.pivotX + 0.2f
                : increment2.pivotX;

        // Hammer
        ModelPart hammer = this.toolbox.getChild("hammer");
        hammer.hidden = tardis.extra().getConsoleHammer() == null || tardis.extra().getConsoleHammer().isEmpty();

        super.renderWithAnimations(console, tardis, root, matrices, vertices, light, overlay, red, green, blue, pAlpha);
        toolbox.render(matrices, vertices, light, overlay, red, green, blue, pAlpha);

        matrices.pop();
    }

    @Override
    public ModelPart getPart() {
        return console;
    }

    @Override
    public Animation getAnimationForState(TravelHandlerBase.State state) {
        return switch (state) {
            case MAT, DEMAT, FLIGHT -> HudolinAnimations.FLIGHT;
            case LANDED -> HudolinAnimations.IDLE;
        };
    }
}