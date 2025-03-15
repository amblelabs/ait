package dev.amble.ait.core.tardis.control.impl.waypoint;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;

import dev.amble.ait.AITMod;
import dev.amble.ait.core.AITSounds;
import dev.amble.ait.core.tardis.Tardis;
import dev.amble.ait.core.tardis.control.Control;

public class EjectWaypointControl extends Control {

    public EjectWaypointControl() {
        super(AITMod.id("eject_waypoint"));
    }

    @Override
    public Result runServer(Tardis tardis, ServerPlayerEntity player, ServerWorld world, BlockPos console, boolean leftClick) {
        tardis.waypoint().spawnItem(console);

        return Result.SUCCESS;
    }
    @Override
    public SoundEvent getFallbackSound() {
        return AITSounds.SET_WAYPOINT;
    }
}
