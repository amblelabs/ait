package dev.amble.ait.core.tardis.control.impl.waypoint;

import dev.amble.ait.AITMod;
import dev.amble.ait.core.AITSounds;
import dev.amble.ait.core.tardis.Tardis;
import dev.amble.ait.core.tardis.TardisDesktop;
import dev.amble.ait.core.tardis.control.Control;
import dev.amble.ait.core.tardis.handler.WaypointHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

public class SetWaypointControl extends Control {

    public SetWaypointControl() {
        super(AITMod.id("set_waypoint"));
    }

    @Override
    public Result runServer(Tardis tardis, ServerPlayerEntity player, ServerWorld world, BlockPos console, boolean leftClick) {
        super.runServer(tardis, player, world, console, leftClick);

        WaypointHandler waypoints = tardis.waypoint();



        if (!tardis.travel().handbrake()) {
            waypoints.gotoWaypoint();
            TardisDesktop.playSoundAtConsole(world, console, AITSounds.NAV_NOTIFICATION, SoundCategory.PLAYERS, 6f, 1);
        } else {
            player.sendMessage(Text.translatable("control.ait.set_waypoint.error"), true);
            TardisDesktop.playSoundAtConsole(world, console, SoundEvents.BLOCK_NOTE_BLOCK_BIT.value(), SoundCategory.PLAYERS, 6f, 0.1f);
        }


        //waypoints.spawnItem(console);

        return Result.SUCCESS;
    }
    @Override
    public SoundEvent getFallbackSound() {
        return AITSounds.SET_WAYPOINT;
    }
}
