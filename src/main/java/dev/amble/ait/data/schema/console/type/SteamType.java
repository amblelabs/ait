package dev.amble.ait.data.schema.console.type;

import dev.amble.ait.core.tardis.control.impl.waypoint.LoadWaypointControl;
import org.joml.Vector3f;

import net.minecraft.entity.EntityDimensions;
import net.minecraft.util.Identifier;

import dev.amble.ait.AITMod;
import dev.amble.ait.core.tardis.control.ControlTypes;
import dev.amble.ait.core.tardis.control.impl.*;
import dev.amble.ait.core.tardis.control.impl.pos.IncrementControl;
import dev.amble.ait.core.tardis.control.impl.pos.XControl;
import dev.amble.ait.core.tardis.control.impl.pos.YControl;
import dev.amble.ait.core.tardis.control.impl.pos.ZControl;
import dev.amble.ait.core.tardis.control.impl.waypoint.SaveWaypointControl;
import dev.amble.ait.data.schema.console.ConsoleTypeSchema;
import dev.amble.ait.data.schema.console.ConsoleVariantSchema;
import dev.amble.ait.registry.impl.console.variant.ConsoleVariantRegistry;

public class SteamType extends ConsoleTypeSchema {
    public static final Identifier REFERENCE = AITMod.id("console/steam");
    private static final ControlTypes[] TYPES = new ControlTypes[]{
            new ControlTypes(new ThrottleControl(), EntityDimensions.changing(0.18749999f, 0.25000003f),
                    new Vector3f(0.23046875f, 0.3875000001862645f, -0.798828125f)),
            new ControlTypes(new HandBrakeControl(), EntityDimensions.changing(0.125f, 0.17500001f),
                    new Vector3f(-0.889062512665987f, 0.3999999985098839f, 0.25156248919665813f)),
            new ControlTypes(new AutoPilotControl(), EntityDimensions.changing(0.06249999f, 0.20000002f),
                    new Vector3f(-0.32031250186264515f, 0.4000003803521395f, -0.8367187771946192f)),
            new ControlTypes(new FastReturnControl(), EntityDimensions.changing(0.12500001f, 0.10000001f),
                    new Vector3f(-0.162500006146729f, 0.3749999962747097f, 0.9621093794703484f)),
            new ControlTypes(new DoorControl(), EntityDimensions.changing(0.1375f, 0.1625f),
                    new Vector3f(-0.6398437712341547f, 0.4249996170401573f, -0.6144531266763806f)),
            new ControlTypes(new DoorLockControl(), EntityDimensions.changing(0.06249999f, 0.2f),
                    new Vector3f(-0.2523437635973096f, 0.3999996166676283f, -0.8867187527939677f)),
         //   new ControlTypes(new VisualiserControl(), EntityDimensions.changing(0.18750001f, 0.1125f),
             //       new Vector3f(0.4398435605689883f, 0.350000093691051f, -0.7617183709517121f)),
            new ControlTypes(new EngineOverloadControl(), EntityDimensions.changing(0.20000002f, 0.1125f),
                    new Vector3f(0.9281248115003109f, 0.3499998077750206f, 0.25234413240104914f)),
            new ControlTypes(new AntiGravsControl(), EntityDimensions.changing(0.074999996f, 0.17500001f),
                    new Vector3f(0.7890625102445483f, 0.4375001899898052f, 0.002343735657632351f)),
            new ControlTypes(new MonitorControl(), EntityDimensions.changing(0.2875f, 0.29999998f),
                    new Vector3f(0.42343749571591616f, 0.4000013330951333f, 0.7378906384110451f)),
            new ControlTypes(new SecurityControl(), EntityDimensions.changing(0.07499999f, 0.1375f),
                    new Vector3f(0.778906268067658f, 0.4624997554346919f, 0.08945311885327101f)),
            new ControlTypes(new TelepathicControl(), EntityDimensions.changing(0.3125f, 0.125f),
                    new Vector3f(-0.38750000577419996f, 0.4375001899898052f, 0.6359375203028321f)),
            new ControlTypes(new LandTypeControl(), EntityDimensions.changing(0.1125f, 0.15f),
                    new Vector3f(0.6273437645286322f, 0.5025001531466842f, -0.005078110843896866f)),
            new ControlTypes(new IncrementControl(), EntityDimensions.changing(0.125f, 0.15f),
                    new Vector3f(0.586718768812716f, 0.4375001899898052f, -0.5984374964609742f)),
            new ControlTypes(new XControl(), EntityDimensions.changing(0.074999996f, 0.099999994f),
                    new Vector3f(0.35234376322478056f, 0.5749996183440089f, -0.40859375055879354f)),
            new ControlTypes(new YControl(), EntityDimensions.changing(0.074999996f, 0.1f),
                    new Vector3f(0.2640625135973096f, 0.5750001911073923f, -0.46054687537252903f)),
            new ControlTypes(new ZControl(), EntityDimensions.changing(0.074999996f, 0.1f),
                    new Vector3f(0.17656251415610313f, 0.5749998101964593f, -0.51171875f)),
            new ControlTypes(new RandomiserControl(), EntityDimensions.changing(0.16250001f, 0.16250001f),
                    new Vector3f(-0.39062499161809683f, 0.7249996224418283f, 0.0011718804016709328f)),
            new ControlTypes(new DirectionControl(), EntityDimensions.changing(0.1375f, 0.17500001f),
                    new Vector3f(-0.8890624968335032f, 0.38750038016587496f, -0.287109381519258f)),
            new ControlTypes(new HailMaryControl(), EntityDimensions.changing(0.07499999f, 0.1f),
                    new Vector3f(-0.72265625f, 0.4749998077750206f, 0.08906249981373549f)),
            new ControlTypes(new CloakControl(), EntityDimensions.changing(0.06249999f, 0.18750001f),
                    new Vector3f(-0.18515625223517418f, 0.4000003803521395f, -0.9226562483236194f)),
            new ControlTypes(new SiegeModeControl(), EntityDimensions.changing(0.074999996f, 0.1f),
                    new Vector3f(-0.19609374646097422f, 0.5749998092651367f, 0.45195312425494194f)),
            new ControlTypes(new DimensionControl(), EntityDimensions.changing(0.125f, 0.1125f),
                    new Vector3f(-0.7640624977648258f, 0.3700000746175647f, 0.6269531277939677f)),
            new ControlTypes(new RefuelerControl(), EntityDimensions.changing(0.07499999f, 0.16250001f),
                    new Vector3f(0.7882812572643161f, 0.45000019017606974f, -0.19296873733401299f)),
            new ControlTypes(new HADSControl(), EntityDimensions.changing(0.074999996f, 0.15f),
                    new Vector3f(0.7781250039115548f, 0.44999980740249157f, 0.18984374683350325f)),
            new ControlTypes(new PowerControl(), EntityDimensions.changing(0.125f, 0.1375f),
                    new Vector3f(-0.3109375163912773f, 0.4875005716457963f, -0.5468750149011612f)),
            new ControlTypes(new ConsolePortControl(), EntityDimensions.changing(0.16250001f, 0.0875f),
                    new Vector3f(-0.838281256146729f, 0.4000003784894943f, 0.0046874964609742165f)),
            new ControlTypes(new SaveWaypointControl(), EntityDimensions.changing(0.074999996f, 0.1f),
                    new Vector3f(-0.7125000134110451f, 0.47500038146972656f, 0.1789062386378646f)),
            new ControlTypes(new LoadWaypointControl(), EntityDimensions.changing(0.07499999f, 0.1f),
                    new Vector3f(-0.7109374962747097f, 0.47500057239085436f, -0.17265624925494194f)),
            new ControlTypes(new SonicPortControl(), EntityDimensions.changing(0.125f, 0.1125f),
                    new Vector3f(0.35234375298023224f, 0.4624999947845936f, -0.5992187550291419f)),
            new ControlTypes(new ElectricalDischargeControl(),EntityDimensions.changing(0.074999996f, 0.1f),
                    new Vector3f(-0.10859355796128511f, 0.5624998090788722f, 0.5019529350101948f)),
            new ControlTypes(new ShieldsControl(), EntityDimensions.changing(0.08749999f, 0.16250001f),
                    new Vector3f(0.7640625275671482f, 0.4374997578561306f, -0.09999999217689037f)),};

    public SteamType() {
        super(REFERENCE, "steam");
    }

    @Override
    public ControlTypes[] getControlTypes() {
        return TYPES;
    }

    @Override
    public ConsoleVariantSchema getDefaultVariant() {
        return ConsoleVariantRegistry.STEAM;
    }
}
