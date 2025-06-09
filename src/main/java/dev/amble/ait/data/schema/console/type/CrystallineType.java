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

public class CrystallineType extends ConsoleTypeSchema {
    public static final Identifier REFERENCE = AITMod.id("console/crystalline");
    private static final ControlTypes[] TYPES = new ControlTypes[]{

            // done
            new ControlTypes(new ThrottleControl(), EntityDimensions.changing(0.18749999f, 0.20000002f),
                    new Vector3f(0.5164070203900337f, 0.9375000027939677f, -0.2984382575377822f)),
            // done
            new ControlTypes(new HandBrakeControl(), EntityDimensions.changing(0.17500001f, 0.20000002f),
                    new Vector3f(-0.5515623334795237f, 0.9375000577419996f, 0.32656213641166687f)),
            // done
            new ControlTypes(new AutoPilotControl(), EntityDimensions.changing(0.06249999f, 0.1f),
                    new Vector3f(-0.7203126903623343f, 0.5250001903623343f, -0.19921913743019104f)),
            // done
            new ControlTypes(new FastReturnControl(), EntityDimensions.changing(0.087500006f, 0.11250001f),
                    new Vector3f(0.6999998092651367f, 0.5750001911073923f, -3.908127546310425E-4f)),
            // done
            new ControlTypes(new DoorControl(), EntityDimensions.changing(0.125f, 0.124999985f),
                    new Vector3f(1.0601562475785613f, 0.38749957270920277f, 0.4980472531169653f)),

          //  new ControlTypes(new VisualiserControl(), EntityDimensions.changing(0.29999998f, 0.1125f),
                    //new Vector3f(0.36406249925494194f, 0.7499999087303877f, 0.5773441279307008f)),

            new ControlTypes(new EngineOverloadControl(),  EntityDimensions.changing(0.04999999f, 0.125f),
                    new Vector3f(0.6367189437150955f, 0.41250014305114746f, 0.8890623077750206f)),

            new ControlTypes(new ElectricalDischargeControl(),  EntityDimensions.changing(0.1125f, 0.16250001f),
                    new Vector3f(-0.44921932090073824f, 0.41249961871653795f, -0.7679687477648258f)),
            // done
            new ControlTypes(new DoorLockControl(), EntityDimensions.changing(0.099999994f, 0.14999999f),
                    new Vector3f(-0.9273435566574335f, 0.474999712780118f, 7.80859962105751E-4f)),
            // done
            new ControlTypes(new AntiGravsControl(), EntityDimensions.changing(0.08749999f, 0.1375f),
                    new Vector3f(0.41406268160790205f, 0.4749999986961484f, 0.8148437533527613f)),
            // done
            new ControlTypes(new MonitorControl(), EntityDimensions.changing(0.3874999f, 0.3874999f),
                    new Vector3f(0.873436744324863f, 1.0875012436881661f, 0.5253902422264218f)),
            // done
            new ControlTypes(new MonitorControl(), EntityDimensions.changing(0.3874999f, 0.3874999f),
                    new Vector3f(-0.9015632821246982f, 1.0625012433156371f, -0.5121097732335329f)),
            // done
            new ControlTypes(new SecurityControl(), EntityDimensions.changing(0.06249999f, 0.1f),
                    new Vector3f(-0.7210939396172762f, 0.5249999044463038f, -0.12304726243019104f)),
            // done
            new ControlTypes(new TelepathicControl(), EntityDimensions.changing(0.17500004f, 0.15f),
                    new Vector3f(4.6566128730773926E-9f, 0.8875002926215529f, 0.698437332175672f)),
            // done
            new ControlTypes(new LandTypeControl(), EntityDimensions.changing(0.099999994f, 0.16250001f),
                    new Vector3f(0.08984335884451866f, 0.9025001553818583f, -0.630077937617898f)),
            // done
            new ControlTypes(new IncrementControl(), EntityDimensions.changing(0.08749999f, 0.125f),
                    new Vector3f(0.4367189472541213f, 0.41250009275972843f, 0.9890624955296516f)),
            // done
            new ControlTypes(new XControl(), EntityDimensions.changing(0.08749999f, 0.099999994f),
                    new Vector3f(0.40234335977584124f, 0.5250000488013029f, -0.7085935743525624f)),
            // done
            new ControlTypes(new YControl(), EntityDimensions.changing(0.0875f, 0.1f),
                    new Vector3f(0.4640628732740879f, 0.48750028666108847f, -0.810546507127583f)),
            // done
            new ControlTypes(new ZControl(), EntityDimensions.changing(0.0875f, 0.1f),
                    new Vector3f(0.5140621103346348f, 0.4375002384185791f, -0.8992185713723302f)),
            // done
            new ControlTypes(new RandomiserControl(), EntityDimensions.changing(0.07499999f, 0.125f),
                    new Vector3f(0.6218751966953278f, 0.6125001907348633f, -0.2363279303535819f)),
            // done
            new ControlTypes(new DirectionControl(), EntityDimensions.changing(0.125f, 0.15f),
                    new Vector3f(-0.0015621185302734375f, 0.2124999975785613f, 1.4003906259313226f)),
            // done
            new ControlTypes(new HailMaryControl(), EntityDimensions.changing(0.099999994f, 0.1125f),
                    new Vector3f(-0.3851558566093445f, 0.4374998575076461f, 0.8640621211379766f)),
            // done
            new ControlTypes(new DimensionControl(), EntityDimensions.changing(0.21250002f, 0.125f),
                    new Vector3f(-0.5640623085200787f, 0.35750007536262274f, 0.9519527452066541f)),
            // done
            new ControlTypes(new RefuelerControl(), EntityDimensions.changing(0.1125f, 0.16250001f),
                    new Vector3f(-0.5367189487442374f, 0.3999998066574335f, -0.9179685553535819f)),
            // done
            new ControlTypes(new PowerControl(), EntityDimensions.changing(0.1125f, 0.125f),
                    new Vector3f(-0.9718753807246685f, 0.4125001886859536f, -0.46015643887221813f)),
            // done
            new ControlTypes(new SiegeModeControl(), EntityDimensions.changing(0.1125f, 0.18750001f),
                    new Vector3f(-0.6085935495793819f, 0.6499997675418854f, 0.014452925883233547f)),
            // done
            new ControlTypes(new HADSControl(), EntityDimensions.changing(0.125f, 0.16250001f),
                    new Vector3f(-0.2718746168538928f, 0.9250002913177013f, -0.4726562490686774f)),
            // done
            new ControlTypes(new MarkWaypointControl(), EntityDimensions.changing(0.07499999f, 0.074999996f),
                    new Vector3f(0.6382812475785613f, 0.7250008611008525f, 0.10156288184225559f)),
            // done
            new ControlTypes(new SetWaypointControl(), EntityDimensions.changing(0.07499999f, 0.074999996f),
                    new Vector3f(0.6382812475785613f, 0.7250003842636943f, -0.09843750111758709f)),
            // done
            new ControlTypes(new ConsolePortControl(), EntityDimensions.changing(0.07499999f, 0.07499999f),
                    new Vector3f(0.6382814394310117f, 0.7250005742534995f, 0.0015623057261109352f)),

            new ControlTypes(new CloakControl(), EntityDimensions.changing(0.17500001f, 0.08749999f),
                    new Vector3f(-0.3484373092651367f, 0.7125008599832654f, 0.5757814422249794f)),
            // done
            new ControlTypes(new SonicPortControl(), EntityDimensions.changing(0.125f, 0.125f),
                    new Vector3f(1.051953513175249f, 0.41249999683350325f, 7.810592651367188E-4f)),
            // done
            new ControlTypes(new ShieldsControl(), EntityDimensions.changing(0.17500001f, 0.1125f),
                    new Vector3f(-0.3359374860301614f, 0.5499997669830918f, -0.6000000005587935f)),};

    public CrystallineType() {
        super(REFERENCE, "crystalline");
    }

    @Override
    public ControlTypes[] getControlTypes() {
        return TYPES;
    }

    @Override
    public ConsoleVariantSchema getDefaultVariant() {
        return ConsoleVariantRegistry.CRYSTALLINE;
    }
}
