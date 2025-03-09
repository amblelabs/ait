package dev.amble.ait.core.tardis.control.impl.waypoint;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

import dev.amble.ait.core.tardis.Tardis;
import dev.amble.ait.core.tardis.control.Control;
import dev.amble.ait.core.tardis.handler.WaypointHandler;

public class SetWaypointControl extends Control {
    public SetWaypointControl() {
        super("set_waypoint");
    }

    @Override
    public boolean runServer(Tardis tardis, ServerPlayerEntity player, ServerWorld world, BlockPos console) {
        WaypointHandler waypoints = tardis.waypoint();

        if (tardis.sequence().hasActiveSequence() && tardis.sequence().controlPartOfSequence(this)) {
            this.addToControlSequence(tardis, player, console);
            return false;
        }

        waypoints.setDestination();
        waypoints.spawnItem(console);
        return true;
    }
}
