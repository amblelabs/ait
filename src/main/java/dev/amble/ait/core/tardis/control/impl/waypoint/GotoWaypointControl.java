package dev.amble.ait.core.tardis.control.impl.waypoint;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;

import dev.amble.ait.core.AITSounds;
import dev.amble.ait.core.tardis.Tardis;
import dev.amble.ait.core.tardis.control.Control;

public class GotoWaypointControl extends Control {
    public GotoWaypointControl() {
        super("goto_waypoint");
    }

    @Override
    public boolean runServer(Tardis tardis, ServerPlayerEntity player, ServerWorld world, BlockPos pos) {
        tardis.waypoint().gotoWaypoint();
        return true;
    }

    @Override
    public SoundEvent getSound() {
        return AITSounds.SET_WAYPOINT;
    }
}
