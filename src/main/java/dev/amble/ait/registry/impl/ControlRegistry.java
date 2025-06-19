package dev.amble.ait.registry.impl;

import java.util.Optional;

import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;

import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.SimpleRegistry;
import net.minecraft.util.Identifier;

import dev.amble.ait.AITMod;
import dev.amble.ait.core.tardis.control.Control;
import dev.amble.ait.core.tardis.control.impl.*;
import dev.amble.ait.core.tardis.control.impl.pos.IncrementControl;
import dev.amble.ait.core.tardis.control.impl.pos.XControl;
import dev.amble.ait.core.tardis.control.impl.pos.YControl;
import dev.amble.ait.core.tardis.control.impl.pos.ZControl;
import dev.amble.ait.core.tardis.control.impl.waypoint.MarkWaypointControl;
import dev.amble.ait.core.tardis.control.impl.waypoint.SetWaypointControl;

public class ControlRegistry {

    public static final SimpleRegistry<Control> REGISTRY = FabricRegistryBuilder
            .createSimple(RegistryKey.<Control>ofRegistry(AITMod.id("control"))).buildAndRegister();

    public static Control register(Control control) {
        return Registry.register(REGISTRY, control.id(), control);
    }

    /**
     * Finds a control by its ID ( name ) NOT its Identifier
     *
     * @param id
     *            the id to look for
     * @return the control found
     */
    public static Optional<Control> fromId(Identifier id) {
        return Optional.ofNullable(REGISTRY.get(id));
    }

    public static void init() {
        // Pain.

        register(new AntiGravsControl());
        register(new AutoPilotControl());
        register(new CloakControl());
        register(new DimensionControl());
        register(new DirectionControl());
        register(new DoorControl());
        register(new DoorLockControl());
        register(new FastReturnControl());
        register(new HADSControl());
        register(new HailMaryControl());
        register(new HandBrakeControl());
        register(new LandTypeControl());
        register(new MonitorControl());
        register(new PowerControl());
        register(new RandomiserControl());
        register(new RefuelerControl());
        register(new SecurityControl());
        register(new SiegeModeControl());
        register(new SonicPortControl());
        register(new TelepathicControl());
        register(new ThrottleControl());
        register(new VisualiserControl());
        register(new EngineOverloadControl());
        register(new ElectricalDischargeControl());

        // Waypoints
        register(new ConsolePortControl());
        register(new MarkWaypointControl());
        register(new SetWaypointControl());

        // Pos
        register(new IncrementControl());
        register(new XControl());
        register(new YControl());
        register(new ZControl());
    }
}
