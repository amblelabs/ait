package dev.amble.ait.core.tardis.control.impl.waypoint;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

import dev.amble.ait.AITMod;
import dev.amble.ait.core.tardis.Tardis;
import dev.amble.ait.core.tardis.control.Control;

public class EjectWaypointControl extends Control {

    public EjectWaypointControl() {
        super(AITMod.id("eject_waypoint"));
    }

    @Override
    public boolean runServer(Tardis tardis, ServerPlayerEntity player, ServerWorld world, BlockPos console, boolean leftClick) {
        tardis.waypoint().spawnItem(console);
        return false;
    }
}
