package dev.amble.ait.core.tardis.control.impl.waypoint;

import dev.amble.ait.core.tardis.handler.travel.TravelHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

import dev.amble.ait.AITMod;
import dev.amble.ait.core.AITSounds;
import dev.amble.ait.core.tardis.Tardis;
import dev.amble.ait.core.tardis.TardisDesktop;
import dev.amble.ait.core.tardis.control.Control;
import dev.amble.ait.core.tardis.handler.WaypointHandler;

public class LoadWaypointControl extends Control {

    public LoadWaypointControl() {
        super(AITMod.id("load_waypoint"));
    }

    @Override
    public Result runServer(Tardis tardis, ServerPlayerEntity player, ServerWorld world, BlockPos console, boolean leftClick) {
        super.runServer(tardis, player, world, console, leftClick);

        if (!tardis.waypoint().hasCartridge() || !tardis.waypoint().isDisc()) {
            player.sendMessage(Text.translatable("control.ait.load_waypoint.no_cartridge"), true);
            TardisDesktop.playSoundAtConsole(world, console, SoundEvents.BLOCK_NOTE_BLOCK_BIT.value(), SoundCategory.PLAYERS, 6f, 0.1f);
            return Result.SUCCESS;
        }

        WaypointHandler waypoints = tardis.waypoint();

        if (waypoints.loadWaypoint()) {
            TardisDesktop.playSoundAtConsole(world, console, AITSounds.NAV_NOTIFICATION, SoundCategory.PLAYERS, 6f, 1);
            if (waypoints.isDisc()) {
                player.sendMessage(Text.translatable("control.ait.load_control_disc.loaded"), true);
                TravelHandler travel = tardis.travel();
                travel.handbrake(false);
                travel.autopilot(true);
                travel.speed(travel.maxSpeed().get());
                travel.setFlightTicks(travel.getTargetTicks() / 2);
            }
        } else {
            player.sendMessage(Text.translatable("control.ait.load_waypoint.error"), true);
            TardisDesktop.playSoundAtConsole(world, console, SoundEvents.BLOCK_NOTE_BLOCK_BIT.value(), SoundCategory.PLAYERS, 6f, 0.1f);
        }

        return Result.SUCCESS;
    }
    @Override
    public SoundEvent getFallbackSound() {
        return AITSounds.LOAD_WAYPOINT;
    }
}
