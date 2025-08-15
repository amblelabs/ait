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
import dev.amble.ait.core.tardis.control.impl.waypoint.LoadWaypointControl;
import dev.amble.ait.core.tardis.control.impl.waypoint.SaveWaypointControl;
import dev.amble.ait.data.schema.console.ConsoleTypeSchema;
import dev.amble.ait.data.schema.console.ConsoleVariantSchema;
import dev.amble.ait.registry.impl.console.variant.ConsoleVariantRegistry;

public class HudolinType extends ConsoleTypeSchema {
    public static final Identifier REFERENCE = AITMod.id("console/hudolin");
    private static final ControlTypes[] TYPES = new ControlTypes[]{
            new ControlTypes(new HandBrakeControl(), EntityDimensions.changing(0.16249998f, 0.15f), // fin
                    new Vector3f(0.4054691446945071f, 0.4874999523162842f, -0.46132813952863216f)),
            new ControlTypes(new ThrottleControl(), EntityDimensions.changing(0.23750004f, 0.11249997f), // fin
                    new Vector3f(-0.48522224090993404f, 0.5308909425511956f, -0.2818084778264165f)),
            new ControlTypes(new AutoPilotControl(), EntityDimensions.changing(0.17500001f, 0.07499997f), // fin
                    new Vector3f(1.0226570200175047f, 0.3499996168538928f, -0.2769535193219781f)),
            new ControlTypes(new FastReturnControl(), EntityDimensions.changing(0.06249999f, 0.07499999f), // fin
                    new Vector3f(-0.03515738341957331f, 0.37499971222132444f, -0.8210930014029145f)),
            new ControlTypes(new DoorControl(), EntityDimensions.changing(0.06249999f, 0.07499997f), // fin
                    new Vector3f(-0.4273433657363057f, 0.5874999044463038f, 0.24804763961583376f)),
            new ControlTypes(new DoorLockControl(), EntityDimensions.changing(0.06249999f, 0.062499974f), // fin
                    new Vector3f(-0.5148452753201127f, 0.524999619461596f, 0.30078049656003714f)),
            new ControlTypes(new AntiGravsControl(), EntityDimensions.changing(0.1375f, 0.099999994f), // fin
                    new Vector3f(0.2515628803521395f, 0.38749999832361937f, -0.7601551050320268f)),
            new ControlTypes(new MonitorControl(), EntityDimensions.changing(0.22500002f, 0.125f), // fin
                    new Vector3f(0.010935974307358265f, 0.4625153550878167f, -0.5621101558208466f)),
            new ControlTypes(new SecurityControl(), EntityDimensions.changing(0.06249999f, 0.07499999f), // fin
                    new Vector3f(-0.09843711834400892f, 0.37499990221112967f, -0.8125003818422556f)),
            new ControlTypes(new TelepathicControl(), EntityDimensions.changing(0.100000024f, 0.099999994f), // fin
                    new Vector3f(-0.29999999329447746f, 0.38750033266842365f, 0.7484382623806596f)),
            new ControlTypes(new LandTypeControl(), EntityDimensions.changing(0.099999994f, 0.07499999f), // fin
                    new Vector3f(-0.7851566383615136f, 0.40249996073544025f, -0.23007811699062586f)),
            new ControlTypes(new IncrementControl(), EntityDimensions.changing(0.15f, 0.099999994f), // fin
                    new Vector3f(0.686719517223537f, 0.4375002821907401f, -0.1109375050291419f)),
            new ControlTypes(new XControl(), EntityDimensions.changing(0.06249999f, 0.06249999f), // fin
                    new Vector3f(-0.6101558646187186f, 0.46249980945140123f, 0.3539051078259945f)),
            new ControlTypes(new YControl(), EntityDimensions.changing(0.06249999f, 0.074999996f), // fin
                    new Vector3f(-0.660937880165875f, 0.412500380538404f, 0.37695388682186604f)),
            new ControlTypes(new ZControl(), EntityDimensions.changing(0.06249999f, 0.074999996f), // fin
                    new Vector3f(-0.710936738178134f, 0.37499971222132444f, 0.4132820088416338f)),
            new ControlTypes(new ElectricalDischargeControl(), EntityDimensions.changing(0.125f, 0.15f), // fin
                    new Vector3f(0.08593673445284367f, 0.4125152602791786f, 0.7128898622468114f)),
            new ControlTypes(new RandomiserControl(), EntityDimensions.changing(0.21250002f, 0.1125f), // Fin
                    new Vector3f(0.7843746459111571f, 0.32499961741268635f, -0.6988277575001121f)),
            new ControlTypes(new DirectionControl(), EntityDimensions.changing(0.1125f, 0.099999994f), // fin
                    new Vector3f(-0.6953121209517121f, 0.3625005688518286f, -0.3617179971188307f)),
            new ControlTypes(new HailMaryControl(), EntityDimensions.changing(0.08749999f, 0.08749999f), // fin
                    new Vector3f(0.5773441409692168f, 0.44999961741268635f, 0.33906211145222187f)),
            new ControlTypes(new CloakControl(), EntityDimensions.changing(0.08749999f, 0.099999994f), // fin
                    new Vector3f(0.6023445203900337f, 0.3625003332272172f, 0.5148437451571226f)),
            new ControlTypes(new SiegeModeControl(), EntityDimensions.changing(0.08749999f, 0.07499999f), // fin
                    new Vector3f(0.6789066297933459f, 0.4249999029561877f, 0.2894535129889846f)),
            new ControlTypes(new DimensionControl(), EntityDimensions.changing(0.15f, 0.16250001f), // fin
                    new Vector3f(-0.08281249180436134f, 0.4374995222315192f, 0.6757820174098015f)),
            new ControlTypes(new RefuelerControl(), EntityDimensions.changing(0.1375f, 0.099999994f), // fin
                    new Vector3f(-0.2492187637835741f, 0.38750038016587496f, -0.7554687662050128f)),
            new ControlTypes(new HADSControl(), EntityDimensions.changing(0.0875f, 0.07499999f), // fin
                    new Vector3f(0.5156250158324838f, 0.46249985694885254f, 0.45234450977295637f)),
            new ControlTypes(new PowerControl(), EntityDimensions.changing(0.1375f, 0.1375f), // fin
                    new Vector3f(-0.5359386596828699f, 0.3750006640329957f, 0.5656257756054401f)),
            new ControlTypes(new ConsolePortControl(), EntityDimensions.changing(0.15f, 0.125f), // fin
                    new Vector3f(-0.10078201442956924f, 0.3250004705041647f, 0.7671871352940798f)),
            new ControlTypes(new SaveWaypointControl(), EntityDimensions.changing(0.06249999f, 0.074999996f), // fin
                    new Vector3f(0.08749961853027344f, 0.37499961629509926f, -0.8210933683440089f)),
            new ControlTypes(new LoadWaypointControl(), EntityDimensions.changing(0.06249999f, 0.07499999f), // fin
                    new Vector3f(0.02656326163560152f, 0.3750002831220627f, -0.8226570151746273f)),
            new ControlTypes(new SonicPortControl(),EntityDimensions.changing(0.1375f, 0.15f), // fin
                    new Vector3f(-0.535156624391675f, 0.3375000460073352f, -0.586719136685133f)),
            new ControlTypes(new ShieldsControl(), EntityDimensions.changing(0.08749999f, 0.08749999f), // fin
                    new Vector3f(0.6789054861292243f, 0.37499995063990355f, 0.40195198357105255f)),
            new ControlTypes(new EngineOverloadControl(), EntityDimensions.changing(0.15f, 0.09999995f), // fin
            new Vector3f(-0.7523437542840838f, 0.3874997114762664f, 0.1757804937660694f)),
    };

    public HudolinType() {
        super(REFERENCE, "hudolin");
    }

    @Override
    public ControlTypes[] getControlTypes() {
        return TYPES;
    }

    @Override
    public ConsoleVariantSchema getDefaultVariant() {
        return ConsoleVariantRegistry.HUDOLIN;
    }
}
