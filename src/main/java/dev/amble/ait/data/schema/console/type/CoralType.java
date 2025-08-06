package dev.amble.ait.data.schema.console.type;

import dev.amble.ait.core.tardis.control.impl.waypoint.LoadWaypointControl;
import dev.amble.ait.core.tardis.control.impl.waypoint.SaveWaypointControl;
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
import dev.amble.ait.data.schema.console.ConsoleTypeSchema;
import dev.amble.ait.data.schema.console.ConsoleVariantSchema;
import dev.amble.ait.registry.impl.console.variant.ConsoleVariantRegistry;

public class CoralType extends ConsoleTypeSchema {
    public static final Identifier REFERENCE = AITMod.id("console/coral");
    private static final ControlTypes[] TYPES = new ControlTypes[]{
            new ControlTypes(new ThrottleControl(), EntityDimensions.changing(0.18749999f, 0.16250001f),
                    new Vector3f(0.016406255774199963f, 0.7500000037252903f, -0.6609375244006515f)),
            new ControlTypes(new HandBrakeControl(), EntityDimensions.changing(0.16250001f, 0.22500002f),
                    new Vector3f(1.0734375165775418f, 0.2624999964609742f, -0.8734375275671482f)),
            new ControlTypes(new AutoPilotControl(), EntityDimensions.changing(0.06249999f, 0.1f),
                    new Vector3f(-0.7453125081956387f, 0.5500003825873137f, 0.20078123826533556f)),
            new ControlTypes(new FastReturnControl(), EntityDimensions.changing(0.12500001f, 0.11250001f),
                    new Vector3f(0.7000000067055225f, 0.5499999988824129f, -0.20039063785225153f)),
            new ControlTypes(new DoorControl(), EntityDimensions.changing(0.1375f, 0.14999999f),
                    new Vector3f(1.222656256519258f, 0.2624996146187186f, 0.7230468932539225f)),
            new ControlTypes(new DoorLockControl(), EntityDimensions.changing(0.099999994f, 0.09999998f),
                    new Vector3f(-1.1023437762632966f, 0.5499996189028025f, 0.5757812689989805f)),
            new ControlTypes(new AntiGravsControl(), EntityDimensions.changing(0.17500001f, 0.16250001f),
                    new Vector3f(1.026562513783574f, 0.5625001918524504f, 0.5898437444120646f)),
            new ControlTypes(new MonitorControl(), EntityDimensions.changing(0.36249992f, 0.31249997f),
                    new Vector3f(0.3859374951571226f, 0.8250013394281268f, 0.6128906365483999f)),
            new ControlTypes(new SecurityControl(), EntityDimensions.changing(0.06249999f, 0.1f),
                    new Vector3f(-0.7960937554016709f, 0.537499756552279f, 0.27695312164723873f)),
            new ControlTypes(new TelepathicControl(), EntityDimensions.changing(0.17500004f, 0.15f),
                    new Vector3f(-0.3125000046566129f, 0.45000019017606974f, -0.8390625016763806f)),
            new ControlTypes(new LandTypeControl(), EntityDimensions.changing(0.23750003f, 0.099999994f),
                    new Vector3f(0.7398437662050128f, 0.7150001563131809f, -0.40507811680436134f)),
            new ControlTypes(new EngineOverloadControl(), EntityDimensions.changing(0.1375f, 0.1f),
                    new Vector3f(-0.6828126898035407f, 0.6250001918524504f, 0.06328086648136377f)),
            //new ControlTypes(new VisualiserControl(), EntityDimensions.changing(0.1375f, 0.16250001f),
                   // new Vector3f(0.7140626860782504f, 0.3749999972060323f, 0.8023437531664968f)),
            new ControlTypes(new IncrementControl(), EntityDimensions.changing(0.1125f, 0.112500004f),
                    new Vector3f(-7.812399417161942E-4f, 0.5875001922249794f, 1.2890625316649675f)),
            new ControlTypes(new XControl(), EntityDimensions.changing(0.099999994f, 0.099999994f),
                    new Vector3f(0.9898437727242708f, 0.450000093318522f, 0.3039062600582838f)),
            new ControlTypes(new YControl(), EntityDimensions.changing(0.112500004f, 0.1f),
                    new Vector3f(1.0140625247731805f, 0.45000018924474716f, 0.10195313300937414f)),
            new ControlTypes(new ZControl(), EntityDimensions.changing(0.1f, 0.1f),
                    new Vector3f(1.0640625273808837f, 0.43750028498470783f, -0.08671874366700649f)),
            new ControlTypes(new RandomiserControl(), EntityDimensions.changing(0.125f, 0.125f),
                    new Vector3f(0.2218750175088644f, 0.6125000976026058f, -0.49882812704890966f)),
            new ControlTypes(new DirectionControl(), EntityDimensions.changing(0.15f, 0.15f),
                    new Vector3f(-0.0015624836087226868f, 0.37499990314245224f, 1.337890642695129f)),
            new ControlTypes(new HailMaryControl(), EntityDimensions.changing(0.1125f, 0.1125f),
                    new Vector3f(-1.185156256891787f, 0.3999998066574335f, 0.6890625087544322f)),
            new ControlTypes(new DimensionControl(), EntityDimensions.changing(0.21250002f, 0.125f),
                    new Vector3f(-0.6140624955296516f, 0.42000007536262274f, 0.8394531309604645f)),
            new ControlTypes(new RefuelerControl(), EntityDimensions.changing(0.125f, 0.099999994f),
                    new Vector3f(7.812455296516418E-4f, 0.5749997152015567f, -1.1804687520489097f)),
            new ControlTypes(new PowerControl(), EntityDimensions.changing(0.15f, 0.16250001f),
                    new Vector3f(-1.0218750229105353f, 0.5500002857297659f, -0.5851562647148967f)),
            new ControlTypes(new SiegeModeControl(), EntityDimensions.changing(0.1125f, 0.29999998f),
                    new Vector3f(-1.258593762293458f, 0.24999980442225933f, 0.5769531261175871f)),
            new ControlTypes(new HADSControl(), EntityDimensions.changing(0.125f, 0.125f),
                    new Vector3f(-0.3843750134110451f, 0.5750000923871994f, -0.5351562639698386f)),
            new ControlTypes(new SaveWaypointControl(), EntityDimensions.changing(0.07499999f, 0.074999996f),
                    new Vector3f(0.80078125f, 0.537500761449337f, 0.07656250149011612f)),
            new ControlTypes(new LoadWaypointControl(), EntityDimensions.changing(0.07499999f, 0.074999996f),
                    new Vector3f(0.8007812770083547f, 0.5375005733221769f, -0.02343749813735485f)),
            new ControlTypes(new ConsolePortControl(), EntityDimensions.changing(0.1375f, 0.1125f),
                    new Vector3f(0.7257812488824129f, 0.5875006653368473f, 0.2390625039115548f)),
            new ControlTypes(new CloakControl(), EntityDimensions.changing(0.1375f, 0.1375f),
                    new Vector3f(-0.3484375160187483f, 0.600000855512917f, 0.4757812535390258f)),
            new ControlTypes(new SonicPortControl(), EntityDimensions.changing(0.125f, 0.08749999f),
                    new Vector3f(0.6019531404599547f, 0.6249998062849045f, 7.812539115548134E-4f)),
            new ControlTypes(new ElectricalDischargeControl(), EntityDimensions.changing(0.07499999f, 0.1f),
                    new Vector3f(-1.0578126963227987f, 0.39999999664723873f, -0.22421894501894712f)),
            new ControlTypes(new ShieldsControl(), EntityDimensions.changing(0.099999994f, 0.21250002f),
                    new Vector3f(-1.2734375027939677f, 0.1874997541308403f, -0.5624999990686774f)),
            new ControlTypes(new HammerHangerControl(), EntityDimensions.changing(0.15f, 0.42499986f),
                    new Vector3f(1.2765621216967702f, -0.05000000726431608f, -0.5242195194587111f)),};

    public CoralType() {
        super(REFERENCE, "coral");
    }

    @Override
    public ControlTypes[] getControlTypes() {
        return TYPES;
    }

    @Override
    public ConsoleVariantSchema getDefaultVariant() {
        return ConsoleVariantRegistry.CORAL;
    }
}
