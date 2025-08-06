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

public class HartnellType extends ConsoleTypeSchema {
    public static final Identifier REFERENCE = AITMod.id("console/hartnell");
    private static final ControlTypes[] TYPES = new ControlTypes[]{
            new ControlTypes(new ThrottleControl(), EntityDimensions.changing(0.12499998f, 0.099999994f),
                    new Vector3f(-0.37109375f, 0.4999998565763235f, 0.9755859375f)), // 0
            new ControlTypes(new HandBrakeControl(), EntityDimensions.changing(0.125f, 0.099999994f),
                    new Vector3f(-0.041015625f, 0.4999998565763235f, 0.9755859375f)), // 1
            new ControlTypes(new AutoPilotControl(), EntityDimensions.changing(0.125f, 0.1f),
                    new Vector3f(-0.19687499664723873f, 0.47500018775463104f, 0.9875000109896064f)), // 2
            new ControlTypes(new FastReturnControl(), EntityDimensions.changing(0.075f, 0.075f),
                    new Vector3f(0.24960938468575478f, 0.6249999515712261f, 0.6625000154599547f)), // 3
            new ControlTypes(new DoorControl(), EntityDimensions.changing(0.1125f, 0.07499997f),
                    new Vector3f(0.7843749998137355f, 0.5249998569488525f, -0.5015625143423676f)), // 4
            new ControlTypes(new DoorLockControl(), EntityDimensions.changing(0.1125f, 0.07499997f),
                    new Vector3f(0.6601562481373549f, 0.5249997079372406f, -0.7000000029802322f)), // 4.5?
            new ControlTypes(new AntiGravsControl(), EntityDimensions.changing(0.1f, 0.0875f),
                    new Vector3f(0.10312499292194843f, 0.4875000938773155f, 0.9656250094994903f)), // 5
            new ControlTypes(new MonitorControl(), EntityDimensions.changing(0.20000002f, 0.1125f),
                    new Vector3f(0.7125000013038516f, 0.5375015260651708f, -0.1628906326368451f)), // 6
            new ControlTypes(new TelepathicControl(), EntityDimensions.changing(0.17500004f, 0.15f),
                    new Vector3f(-7.812418043613434E-4f, 0.4874999513849616f, -0.7267578281462193f)), // 7
            new ControlTypes(new LandTypeControl(), EntityDimensions.changing(0.20000002f, 0.099999994f),
                    new Vector3f(-0.34687500167638063f, 0.46500000916421413f, -0.9546875022351742f)), // 8
            new ControlTypes(new IncrementControl(), EntityDimensions.changing(0.125f, 0.0875f),
                    new Vector3f(-0.6753906365483999f, 0.4875000948086381f, -0.8363281516358256f)), // 9
            new ControlTypes(new XControl(), EntityDimensions.changing(0.074999996f, 0.099999994f),
                    new Vector3f(-0.5355468858033419f, 0.5875000013038516f, -0.3718750197440386f)), // 10
            new ControlTypes(new YControl(), EntityDimensions.changing(0.074999996f, 0.1f),
                    new Vector3f(-0.6503906389698386f, 0.5500000007450581f, -0.43632814567536116f)), // 11
            new ControlTypes(new ZControl(), EntityDimensions.changing(0.074999996f, 0.1f),
                    new Vector3f(-0.7750000171363354f, 0.5125000961124897f, -0.5113281467929482f)), // 12
            new ControlTypes(new RandomiserControl(), EntityDimensions.changing(0.06249999f, 0.125f),
                    new Vector3f(-0.9531250046566129f, 0.4624999985098839f, -0.3609375013038516f)), // 13
            new ControlTypes(new DirectionControl(), EntityDimensions.changing(0.1f, 0.1f),
                    new Vector3f(-1.1007812544703484f, 0.4750000946223736f, 0.150390625f)), // 14
            new ControlTypes(new SecurityControl(), EntityDimensions.changing(0.07499999f, 0.1f),
                    new Vector3f(0.5539062647148967f, 0.5749997571110725f, 0.3144531222060323f)),
            new ControlTypes(new HailMaryControl(), EntityDimensions.changing(0.07499999f, 0.1f),
                    new Vector3f(-0.7218750026077032f, 0.5749999508261681f, 0.10117187909781933f)), // 15
            new ControlTypes(new CloakControl(), EntityDimensions.changing(0.1125f, 0.1f),
                    new Vector3f(-0.03515623975545168f, 0.4750000908970833f, -1.0359375169500709f)),
            new ControlTypes(new SiegeModeControl(), EntityDimensions.changing(0.112500004f, 0.0875f),
                    new Vector3f(-0.43515624571591616f, 0.5749998092651367f, 0.5015625059604645f)), // 15
            new ControlTypes(new DimensionControl(), EntityDimensions.changing(0.099999994f, 0.099999994f),
                    new Vector3f(-0.9156250208616257f, 0.482499978505075f, 0.43984376080334187f)), // 16
            new ControlTypes(new RefuelerControl(), EntityDimensions.changing(0.1125f, 0.08749999f),
                    new Vector3f(0.30000001285225153f, 0.4874998088926077f, -1.042968776077032f)), // 17
            new ControlTypes(new HADSControl(), EntityDimensions.changing(0.074999996f, 0.1f),
                    new Vector3f(0.516015631146729f, 0.5750001845881343f, 0.40332030411809683f)),
            new ControlTypes(new PowerControl(), EntityDimensions.changing(0.125f, 0.1f),
                    new Vector3f(0.7906250040978193f, 0.5250002853572369f, 0.45234375074505806f)),
            new ControlTypes(new ConsolePortControl(), EntityDimensions.changing(0.16250001f, 0.0875f),
                    new Vector3f(-0.42500000912696123f, 0.6000001896172762f, 0.24140623956918716f)),
            new ControlTypes(new SaveWaypointControl(), EntityDimensions.changing(0.074999996f, 0.0875f),
                    new Vector3f(0.18749999906867743f, 0.47500019054859877f, 0.940820312127471f)),
            //new ControlTypes(new VisualiserControl(), EntityDimensions.changing(0.17500001f, 0.0875f),
                   // new Vector3f(-0.8250000150874257f, 0.47500014305114746f, 0.5783206885680556f)),
            new ControlTypes(new EngineOverloadControl(), EntityDimensions.changing(0.1125f, 0.1f),
                    new Vector3f(0.06484375149011612f, 0.47500014305114746f, -1.0359373092651367f)),
            new ControlTypes(new LoadWaypointControl(), EntityDimensions.changing(0.07499999f, 0.1f),
                    new Vector3f(-0.9609375260770321f, 0.4625005563721061f, -0.2228515725582838f)),
            new ControlTypes(new SonicPortControl(), EntityDimensions.changing(0.125f, 0.08749999f),
                    new Vector3f(-0.4359374921768904f, 0.6249999944120646f, -0.26015625055879354f)),
            new ControlTypes(new ElectricalDischargeControl(), EntityDimensions.changing(0.099999994f, 0.087499976f),
                    new Vector3f(0.7343742363154888f, 0.5249996185302734f, -0.5765621196478605f)),
            new ControlTypes(new ShieldsControl(), EntityDimensions.changing(0.099999994f, 0.07499999f),
                    new Vector3f(-0.5734374923631549f, 0.5374997593462467f, 0.662500019185245f)),};


    public HartnellType() {
        super(REFERENCE, "hartnell");
    }

    @Override
    public ControlTypes[] getControlTypes() {
        return TYPES;
    }

    @Override
    public ConsoleVariantSchema getDefaultVariant() {
        return ConsoleVariantRegistry.HARTNELL;
    }
}
