package dev.amble.ait.core.tardis.control.impl.waypoint;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;

import dev.amble.ait.core.AITSounds;
import dev.amble.ait.core.tardis.Tardis;
import dev.amble.ait.core.tardis.control.Control;
import dev.amble.ait.core.tardis.handler.WaypointHandler;
import dev.amble.ait.data.Waypoint;

public class MarkWaypointControl extends Control {
    public MarkWaypointControl() {
        super("mark_waypoint");
    }

    @Override
    public boolean runServer(Tardis tardis, ServerPlayerEntity player, ServerWorld world, BlockPos console) {
        WaypointHandler handler = tardis.waypoint();

        handler.set(Waypoint.fromPos(tardis.travel().position()), console, true);

        return true;
    }

    @Override
    public SoundEvent getSound() {
        return AITSounds.MARK_WAYPOINT;
    }
}
