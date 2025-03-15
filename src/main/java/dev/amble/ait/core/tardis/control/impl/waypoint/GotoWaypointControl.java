package dev.amble.ait.core.tardis.control.impl.waypoint;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;

import dev.amble.ait.AITMod;
import dev.amble.ait.core.AITSounds;
import dev.amble.ait.core.tardis.Tardis;
import dev.amble.ait.core.tardis.control.Control;

public class GotoWaypointControl extends Control {

    public GotoWaypointControl() {
        super(AITMod.id("goto_waypoint"));
    }

    @Override
    public boolean runServer(Tardis tardis, ServerPlayerEntity player, ServerWorld world, BlockPos pos, boolean leftClick) {
        tardis.waypoint().gotoWaypoint();
        return true;
    }

    @Override
    public SoundEvent getSound() {
        return AITSounds.SET_WAYPOINT;
    }
}
