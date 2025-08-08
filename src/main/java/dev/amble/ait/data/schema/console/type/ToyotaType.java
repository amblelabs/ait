package dev.amble.ait.data.schema.console.type;

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
import dev.amble.ait.core.tardis.control.impl.waypoint.LoadWaypointControl;
import dev.amble.ait.data.schema.console.ConsoleTypeSchema;
import dev.amble.ait.data.schema.console.ConsoleVariantSchema;
import dev.amble.ait.registry.impl.console.variant.ConsoleVariantRegistry;

public class ToyotaType extends ConsoleTypeSchema {
    public static final Identifier REFERENCE = AITMod.id("console/toyota");
    private static final ControlTypes[] TYPES = new ControlTypes[]{
            new ControlTypes(new ThrottleControl(), EntityDimensions.changing(0.21249999f, 0.20000002f),
                    new Vector3f(0.4406250063329935f, 0.5749999973922968f, 1.4359375312924385f)),
            new ControlTypes(new HandBrakeControl(), EntityDimensions.changing(0.20000002F, 0.15F),
                    new Vector3f(-0.5257812738418579f, 0.5624998137354851f, 1.4125000340864062f)),
            new ControlTypes(new AutoPilotControl(), EntityDimensions.changing(0.1125f, 0.1f),
                    new Vector3f(-0.3960937447845936f, 0.6500001922249794f, 1.053125012665987f)),
            new ControlTypes(new DoorControl(), EntityDimensions.changing(0.16250001f, 0.175f),
                    new Vector3f(-0.3773437738418579f, 0.5874998141080141f, -1.3984375316649675f)),
            new ControlTypes(new DoorLockControl(), EntityDimensions.changing(0.07499999f, 0.087499976f),
                    new Vector3f(0.34843752160668373f, 0.7124992394819856f, -0.9218750223517418f)),
            new ControlTypes(new AntiGravsControl(), EntityDimensions.changing(0.1125f, 0.099999994f),
                    new Vector3f(0.20234373770654202f, 0.7375000026077032f, -0.8312500212341547f)),
            new ControlTypes(new MonitorControl(), EntityDimensions.changing(0.43749985f, 0.16250001f),
                    new Vector3f(0.9867187589406967f, 0.5875017130747437f, -0.5656250175088644f)),
            new ControlTypes(new EngineOverloadControl(), EntityDimensions.changing(0.099999994f, 0.16250001f),
                    new Vector3f(1.41171913780272f, 0.5375015251338482f, -0.2406251858919859f)),
            new ControlTypes(new FastReturnControl(), EntityDimensions.changing(0.087500006f, 0.11250001f),
                    new Vector3f(0.6617187494412065f, 0.5750001911073923f, 1.4593750247731805f)),
            new ControlTypes(new ElectricalDischargeControl(), EntityDimensions.changing(0.07499999f, 0.0875f),
                    new Vector3f(0.0882810577750206f, 0.5750002861022949f, -1.4593753814697266f)),

            // Extra monitors
            new ControlTypes(new MonitorControl(), EntityDimensions.changing(0.43749988f, 0.3749999f),
                    new Vector3f(0.7257812460884452f, 1.0000016214326024f, -0.4624999985098839f)),
            new ControlTypes(new MonitorControl(), EntityDimensions.changing(0.43749988f, 0.3749999f),
                    new Vector3f(-0.7367187757045031f, 1.000001815147698f, 0.4625000152736902f)),

            // Siege mode extra thingy
            // new ControlTypes(new ShowSiegeModeLeverControl(),
            // EntityDimensions.changing(0f, 0f), new
            // Vector3f(0f, 0f,
            // 0f)),

           // new ControlTypes(new VisualiserControl(), EntityDimensions.changing(0.23750003f, 0.08749999f),
                //    new Vector3f(-1.2230468774214387f, 0.54999980609864f, 0.6007816372439265f)),
            new ControlTypes(new SecurityControl(), EntityDimensions.changing(0.07499999f, 0.16250001f),
                    new Vector3f(0.19140625931322575f, 0.7749997600913048f, 0.7519531287252903f)),
            new ControlTypes(new TelepathicControl(), EntityDimensions.changing(0.79999954f, 0.18750001f),
                    new Vector3f(0.925000018440187f, 0.5624998109415174f, 0.5390625204890966f)),
            new ControlTypes(new LandTypeControl(), EntityDimensions.changing(0.16250001f, 0.15f),
                    new Vector3f(-0.39921876695007086f, 0.6775001520290971f, -1.0687500098720193f)),
            new ControlTypes(new IncrementControl(), EntityDimensions.changing(0.1125f, 0.1f),
                    new Vector3f(-0.8875000132247806f, 0.6500005731359124f, -0.773437530733645f)),
            new ControlTypes(new XControl(), EntityDimensions.changing(0.08749999f, 0.08749999f),
                    new Vector3f(-0.7867187764495611f, 0.699999812990427f, -0.545312512665987f)),
            new ControlTypes(new YControl(), EntityDimensions.changing(0.0875f, 0.0875f),
                    new Vector3f(-1.046875030733645f, 0.649999812245369f, -0.43593750800937414f)),
            new ControlTypes(new ZControl(), EntityDimensions.changing(0.0875f, 0.0875f),
                    new Vector3f(-1.0875000320374966f, 0.6000000024214387f, -0.7234375094994903f)),
            new ControlTypes(new RandomiserControl(), EntityDimensions.changing(0.1375f, 0.07499999f),
                    new Vector3f(0.5851562554016709f, 0.5750001901760697f, -1.425000013783574f)),
            new ControlTypes(new DirectionControl(), EntityDimensions.changing(0.1125f, 0.1125f),
                    new Vector3f(0.5375000080093741f, 0.7500000055879354f, -0.6265625292435288f)),
            new ControlTypes(new HailMaryControl(), EntityDimensions.changing(0.1125f, 0.125f),
                    new Vector3f(-0.9585937466472387f, 0.5749996211379766f, -1.2625000290572643f)),
            new ControlTypes(new DimensionControl(), EntityDimensions.changing(0.1125f, 0.1125f),
                    new Vector3f(0.9117187727242708f, 0.5950000789016485f, -1.1015625288709998f)),
            new ControlTypes(new RefuelerControl(), EntityDimensions.changing(0.099999994f, 0.1125f),
                    new Vector3f(-1.5000000223517418f, 0.5749998092651367f, -0.30468748696148396f)),
            new ControlTypes(new PowerControl(), EntityDimensions.changing(0.17500001f, 0.17500001f),
                    new Vector3f(0.37656252086162567f, 0.5875001912936568f, -1.385937511920929f)),
            new ControlTypes(new SiegeModeControl(), EntityDimensions.changing(0.20000002f, 0.22500002f),
                    new Vector3f(-0.004687480628490448f, 0.13750028610229492f, 1.5132812839001417f)),
            new ControlTypes(new HADSControl(), EntityDimensions.changing(0.07499999f, 0.17500001f),
                    new Vector3f(-0.09531249571591616f, 0.7624998120591044f, 0.7687500193715096f)),
            new ControlTypes(new SaveWaypointControl(), EntityDimensions.changing(0.07499999f, 0.0875f),
                    new Vector3f(0.18828124087303877f, 0.5750003820285201f, -1.4593750229105353f)),
            new ControlTypes(new LoadWaypointControl(), EntityDimensions.changing(0.06249999f, 0.112500004f),
                    new Vector3f(0.15078124031424522f, 0.7000003838911653f, -1.0609375154599547f)),
            new ControlTypes(new ConsolePortControl(), EntityDimensions.changing(0.125f, 0.1125f),
                    new Vector3f(0.0015624891966581345f, 0.737500574439764f, -0.7953125154599547f)),
            new ControlTypes(new CloakControl(), EntityDimensions.changing(0.099999994f, 0.1125f),
                    new Vector3f(0.8273437675088644f, 0.7500003837049007f, -0.16093750949949026f)),
            new ControlTypes(new SonicPortControl(), EntityDimensions.changing(0.125f, 0.08749999f),
                    new Vector3f(-1.0605468843132257f, 0.7624998083338141f, 0.21328125707805157f)),
            new ControlTypes(new ShieldsControl(), EntityDimensions.changing(0.125f, 0.099999994f),
                    new Vector3f(-0.21093748696148396f, 0.737499762326479f, -0.8250000029802322f)),};

    public ToyotaType() {
        super(REFERENCE, "toyota");
    }

    @Override
    public ControlTypes[] getControlTypes() {
        return TYPES;
    }

    @Override
    public ConsoleVariantSchema getDefaultVariant() {
        return ConsoleVariantRegistry.TOYOTA;
    }
}
