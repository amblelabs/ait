package dev.amble.ait.data.schema.console.type;

import dev.amble.ait.AITMod;
import dev.amble.ait.core.tardis.control.ControlTypes;
import dev.amble.ait.core.tardis.control.impl.*;
import dev.amble.ait.core.tardis.control.impl.pos.IncrementControl;
import dev.amble.ait.core.tardis.control.impl.pos.XControl;
import dev.amble.ait.core.tardis.control.impl.pos.YControl;
import dev.amble.ait.core.tardis.control.impl.pos.ZControl;
import dev.amble.ait.core.tardis.control.impl.waypoint.MarkWaypointControl;
import dev.amble.ait.core.tardis.control.impl.waypoint.SetWaypointControl;
import dev.amble.ait.data.schema.console.ConsoleTypeSchema;
import dev.amble.ait.data.schema.console.ConsoleVariantSchema;
import dev.amble.ait.registry.impl.console.variant.ConsoleVariantRegistry;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.util.Identifier;
import org.joml.Vector3f;

public class RenaissanceType extends ConsoleTypeSchema {
    public static final Identifier REFERENCE = AITMod.id("console/renaissance");
    private static final ControlTypes[] TYPES = new ControlTypes[]{


            new ControlTypes(new ThrottleControl(), EntityDimensions.changing(0.18749999f, 0.34999993f),
                    new Vector3f(0.37890701089054346f, 0.6249999953433871f, -0.860938080586493f)),

            new ControlTypes(new HandBrakeControl(), EntityDimensions.changing(0.08749999f, 0.15f),
                    new Vector3f(-0.05156230181455612f, 0.5374999940395355f, -1.398438097909093f)),

            new ControlTypes(new AutoPilotControl(), EntityDimensions.changing(0.18750001f, 0.03749999f),
                    new Vector3f(-0.3960939375683665f, 0.5249996166676283f, -1.3980470867827535f)),

            new ControlTypes(new FastReturnControl(), EntityDimensions.changing(0.16250001f, 0.074999996f),
                    new Vector3f(0.8507808716967702f, 0.637500380165875f, 0.48906250577419996f)),

            new ControlTypes(new DoorControl(), EntityDimensions.changing(0.06249999f, 0.21250002f),
                    new Vector3f(-0.9984386460855603f, 0.5750001911073923f, 0.6882816292345524f)),

            new ControlTypes(new ElectricalDischargeControl(), EntityDimensions.changing(0.08749999f, 0.07499999f),
                    new Vector3f(-1.1984382644295692f, 0.5500002857297659f, 0.4007816296070814f)),


            new ControlTypes(new DoorLockControl(), EntityDimensions.changing(0.06249999f, 0.21250002f),
                    new Vector3f(-1.0984386475756764f, 0.5750001911073923f, 0.5257816268131137f)),

            new ControlTypes(new AntiGravsControl(), EntityDimensions.changing(0.08750002f, 0.1125f),
                    new Vector3f(-0.0750007638707757f, 0.5375003814697266f, 1.260937507264316f)),

            new ControlTypes(new MonitorControl(), EntityDimensions.changing(0.275f, 0.33749995f),
                    new Vector3f(-0.8140632603317499f, 0.9250006675720215f, -0.012109756469726562f)),

            new ControlTypes(new SecurityControl(), EntityDimensions.changing(0.18750001f, 0.03749999f),
                    new Vector3f(0.3039060728624463f, 0.5249996166676283f, -1.3980470867827535f)),

            new ControlTypes(new TelepathicControl(), EntityDimensions.changing(0.17500004f, 0.08749999f),
                    new Vector3f(0.5625000083819032f, 0.7250007605180144f, -1.0765632893890142f)),

            new ControlTypes(new LandTypeControl(), EntityDimensions.changing(0.1375f, 0.099999994f),
                    new Vector3f(1.2890628930181265f, 0.47500000055879354f, -0.6367185553535819f)),

            new ControlTypes(new IncrementControl(), EntityDimensions.changing(0.06249999f, 0.21250002f),
                    new Vector3f(-1.0359386466443539f, 0.5500001907348633f, 0.6007816279307008f)),

            new ControlTypes(new XControl(), EntityDimensions.changing(0.15f, 0.099999994f),
                    new Vector3f(1.0640628896653652f, 0.6625000033527613f, -0.17421854846179485f)),

            new ControlTypes(new YControl(), EntityDimensions.changing(0.15f, 0.099999994f),
                    new Vector3f(0.9015628872439265f, 0.6625000033527613f, -0.3617185512557626f)),

            new ControlTypes(new ZControl(), EntityDimensions.changing(0.15f, 0.099999994f),
                    new Vector3f(0.8015628857538104f, 0.6625000033527613f, -0.6117185549810529f)),

            new ControlTypes(new RandomiserControl(), EntityDimensions.changing(0.15f, 0.099999994f),
                    new Vector3f(0.6390628833323717f, 0.6625000033527613f, -0.7867185575887561f)),

          //  new ControlTypes(new VisualiserControl(), EntityDimensions.changing(0.16250001f, 0.08749999f),
                //    new Vector3f(-1.0265632634982467f, 0.912500667385757f, -0.17460975889116526f)),

            new ControlTypes(new EngineOverloadControl(), EntityDimensions.changing(0.08750002f, 0.1125f),
                    new Vector3f(0.1874992409721017f, 0.5375003814697266f, 1.2609376907348633f)),


            new ControlTypes(new DirectionControl(), EntityDimensions.changing(0.1375f, 0.1375f),
                    new Vector3f(-0.4367179935798049f, 0.5874996175989509f, -1.0304685598239303f)),
            // not added
            new ControlTypes(new HailMaryControl(), EntityDimensions.changing(0.15f, 0.099999994f),
                    new Vector3f(1.3390628937631845f, 0.5000000009313226f, -0.4867185531184077f)),

            new ControlTypes(new DimensionControl(), EntityDimensions.changing(0.1375f, 0.099999994f),
                    new Vector3f(1.1140636447817087f, 0.4999998090788722f, -1.0117191327735782f)),

            new ControlTypes(new RefuelerControl(), EntityDimensions.changing(0.1375f, 0.1375f),
                    new Vector3f(7.816394791007042E-4f, 0.6499996222555637f, -0.9929687511175871f)),

            new ControlTypes(new RefreshmentControl(), EntityDimensions.changing(0.08750002f, 0.099999994f),
                    new Vector3f(-0.18750076554715633f, 0.5625003818422556f, 1.2484375070780516f)),

            new ControlTypes(new PowerControl(), EntityDimensions.changing(0.13750003f, 0.1375f),
                    new Vector3f(-0.37500076834112406f, 0.6500003831461072f, 1.1734375059604645f)),

            new ControlTypes(new SiegeModeControl(), EntityDimensions.changing(0.13750003f, 0.1375f),
                    new Vector3f(0.38749924302101135f, 0.6500003831461072f, 1.1734375059604645f)),

            new ControlTypes(new HADSControl(), EntityDimensions.changing(0.16250001f, 0.099999994f),
                    new Vector3f(-0.8859382709488273f, 0.5500005697831511f, 0.8382808724418283f)),

            new ControlTypes(new MarkWaypointControl(), EntityDimensions.changing(0.07499999f, 0.099999994f),
                    new Vector3f(-0.7617179919034243f, 0.599999618716538f, -0.717967982403934f)),

            new ControlTypes(new SetWaypointControl(), EntityDimensions.changing(0.07499999f, 0.099999994f),
                    new Vector3f(-0.9867179952561855f, 0.599999618716538f, -0.2929679760709405f)),

            new ControlTypes(new ConsolePortControl(), EntityDimensions.changing(0.1375f, 0.125f),
                    new Vector3f(-0.7367179915308952f, 0.599999618716538f, -0.43046797811985016f)),

            new ControlTypes(new CloakControl(), EntityDimensions.changing(0.099999994f, 0.049999997f),
                    new Vector3f(1.0007808739319444f, 0.5250003784894943f, 1.0765625145286322f)),

            new ControlTypes(new SonicPortControl(), EntityDimensions.changing(0.125f, 0.1125f),
                    new Vector3f(-0.5105457538738847f, 0.47499990556389093f, -1.3242180068045855f)),

            new ControlTypes(new FoodCreationControl(), EntityDimensions.changing(0.4124999f, 0.23750003f),
                    new Vector3f(-0.012500000186264515f, 0.5375007577240467f, 0.773436738178134f)),

            new ControlTypes(new ShieldsControl(), EntityDimensions.changing(0.17500001f, 0.099999994f),
                    new Vector3f(-1.1734386486932635f, 0.5000001899898052f, 0.6757816290482879f)),};


    public RenaissanceType() {
        super(REFERENCE, "renaissance");
    }

    @Override
    public ControlTypes[] getControlTypes() {
        return TYPES;
    }

    @Override
    public ConsoleVariantSchema getDefaultVariant() {
        return ConsoleVariantRegistry.RENAISSANCE;
    }
}
