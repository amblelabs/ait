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
import net.minecraft.entity.EntityDimensions;
import net.minecraft.util.Identifier;
import org.joml.Vector3f;

public class CopperType extends ConsoleTypeSchema {
    public static final Identifier REFERENCE = AITMod.id("console/copper");
    private static final ControlTypes[] TYPES = new ControlTypes[]{
            new ControlTypes(new ThrottleControl(), EntityDimensions.changing(0.17499998f, 0.23750003f),
                    new Vector3f(-0.359375f, 0.6250000949949026f, -1.0265655517578125f)),
            new ControlTypes(new HandBrakeControl(), EntityDimensions.changing(0.18750001f, 0.25000003f),
                    new Vector3f(1.4242187812924385f, 0.19999971240758896f, -0.1749969320371747f)),
            new ControlTypes(new AutoPilotControl(), EntityDimensions.changing(0.06249999f, 0.15f),
                    new Vector3f(0.8664093101397157f, 0.4625002359971404f, -0.8343749968335032f)),
            new ControlTypes(new FastReturnControl(), EntityDimensions.changing(0.075f, 0.075f),
                    new Vector3f(0.2742156982421875f, 0.7500002384185791f, -0.865631103515625f)),
            new ControlTypes(new DoorControl(), EntityDimensions.changing(0.08749999f, 0.11249998f),
                    new Vector3f(0.6101531982421875f, 0.2999997138977051f, -1.310943603515625f)),
            new ControlTypes(new DoorLockControl(), EntityDimensions.changing(0.099999994f, 0.14999999f),
                    new Vector3f(0.8484344482421875f, 0.2624993324279785f, 1.1281280517578125f)),
            new ControlTypes(new AntiGravsControl(), EntityDimensions.changing(0.16250001f, 0.16250001f),
                    new Vector3f(0.6773468013852835f, 0.6250000009313226f, 0.8187561025843024f)),
            new ControlTypes(new MonitorControl(), EntityDimensions.changing(0.57499975f, 0.3874999f),
                    new Vector3f(-1.2503906358033419f, 1.3125017127022147f, 0.008984364569187164f)),
            new ControlTypes(new TelepathicControl(), EntityDimensions.changing(0.34999996f, 0.20000002f),
                    new Vector3f(0.17499694786965847f, 0.46249985694885254f, 1.189068604260683f)),
          //  new ControlTypes(new VisualiserControl(), EntityDimensions.changing(0.125f, 0.33749995f),
                  //  new Vector3f(-0.6742191342636943f, 0.4750005714595318f, 1.1015651691704988f)),
            new ControlTypes(new EngineOverloadControl(), EntityDimensions.changing(0.099999994f, 0.17500001f),
                    new Vector3f(0.8148471852764487f, 0.5249999985098839f, 1.0312561066821218f)),
            new ControlTypes(new LandTypeControl(), EntityDimensions.changing(0.15f, 0.17500001f),
                    new Vector3f(-0.48671876825392246f, 1.065000157803297f, -0.26874999795109034f)),
            new ControlTypes(new IncrementControl(), EntityDimensions.changing(0.06249999f, 0.125f),
                    new Vector3f(1.1125000165775418f, 0.4875005707144737f, -0.385937524959445f)),
            new ControlTypes(new XControl(), EntityDimensions.changing(0.06249999f, 0.08749999f),
                    new Vector3f(0.9132812488824129f, 0.5374998105689883f, -0.570312513038516f)),
            new ControlTypes(new YControl(), EntityDimensions.changing(0.06249999f, 0.0875f),
                    new Vector3f(0.8781249979510903f, 0.5249998103827238f, -0.6609375113621354f)),
            new ControlTypes(new ZControl(), EntityDimensions.changing(0.062499996f, 0.0875f),
                    new Vector3f(0.9249999979510903f, 0.47500000055879354f, -0.7359375096857548f)),
            new ControlTypes(new RandomiserControl(), EntityDimensions.changing(0.1125f, 0.125f),
                    new Vector3f(-0.9523376412689686f, 0.2625002386048436f, -0.9750061128288507f)),
            new ControlTypes(new DirectionControl(), EntityDimensions.changing(0.1125f, 0.1375f),
                    new Vector3f(-0.5999938948079944f, 0.24999994970858097f, 1.310931397601962f)),
            new ControlTypes(new HailMaryControl(), EntityDimensions.changing(0.099999994f, 0.125f),
                    new Vector3f(-1.2335998537018895f, 0.5249995710328221f, -0.17499999981373549f)),
            new ControlTypes(new DimensionControl(), EntityDimensions.changing(0.31249997f, 0.125f),
                    new Vector3f(-1.1757812583819032f, 0.17000007256865501f, -0.6765625225380063f)),
            new ControlTypes(new RefuelerControl(), EntityDimensions.changing(0.16250001f, 0.17500001f),
                    new Vector3f(0.14999695774167776f, 1.0749997105449438f, -0.6296875057742f)),
            new ControlTypes(new PowerControl(), EntityDimensions.changing(0.15f, 0.16250001f),
                    new Vector3f(-1.1609344482421875f, 0.5500001907348633f, 0.1640625f)),
            new ControlTypes(new SiegeModeControl(), EntityDimensions.changing(0.17500001f, 0.18750001f),
                    new Vector3f(1.017181414179504f, 0.6000000443309546f, -0.18436888977885246f)),
            new ControlTypes(new HADSControl(), EntityDimensions.changing(0.125f, 0.125f),
                    new Vector3f(-0.85780946072191f, 0.7249998077750206f, 0.18124390859156847f)),
            new ControlTypes(new MarkWaypointControl(), EntityDimensions.changing(0.16250001f, 0.125f),
                    new Vector3f(-0.32421875f, 0.7375003350898623f, 0.8281219471246004f)),
            new ControlTypes(new SetWaypointControl(), EntityDimensions.changing(0.099999994f, 0.0875f),
                    new Vector3f(-0.48671875055879354f, 0.5250004287809134f, 1.176565553061664f)),
            new ControlTypes(new ConsolePortControl(), EntityDimensions.changing(0.22500002f, 0.1375f),
                    new Vector3f(-0.0109405517578125f, 0.7125006690621376f, 0.8796844482421875f)),
            new ControlTypes(new CloakControl(), EntityDimensions.changing(0.1125f, 0.18750001f),
                    new Vector3f(-0.5476501472294331f, 1.0375003349035978f, 0.23906249925494194f)),
            new ControlTypes(new ShieldsControl(), EntityDimensions.changing(0.1375f, 0.16250001f),
                    new Vector3f(1.514459267258644f, 0.29999975580722094f, 0.21327821072191f)),
            new ControlTypes(new SonicPortControl(), EntityDimensions.changing(0.1375f, 0.1375f),
                    new Vector3f(0.9898498551920056f, 0.4750000424683094f, 0.5812560999765992f)),
            new ControlTypes(new ElectricalDischargeControl(), EntityDimensions.changing(0.08749999f, 0.18750001f),
                    new Vector3f(-0.8484344435855746f, 0.4625001894310117f, 0.7390625085681677f)),
            new ControlTypes(new SecurityControl(), EntityDimensions.changing(0.099999994f, 0.18750001f),
                    new Vector3f(0.8171813935041428f, 0.7625001901760697f, -0.1843719482421875f)),

            //CONSOLE UNDERSIDE CONTROLS
            new ControlTypes(new ThrottleControl(), EntityDimensions.changing(0.2375f, 0.23750003f),
                    new Vector3f(0.015625005587935448f, -0.4750000163912773f, 0.7984344754368067f)),
            new ControlTypes(new HandBrakeControl(), EntityDimensions.changing(0.3624999f, 0.23750003f),
                    new Vector3f(-0.7218750054016709f, -0.5000000167638063f, 0.44843447022140026f)),
            new ControlTypes(new HandBrakeControl(), EntityDimensions.changing(0.3624999f, 0.23750003f),
                    new Vector3f(0.7656250167638063f, -0.48750001657754183f, -0.4515655431896448f)),
    };

    public CopperType() {
        super(REFERENCE, "copper");
    }

    @Override
    public ControlTypes[] getControlTypes() {
        return TYPES;
    }
}
