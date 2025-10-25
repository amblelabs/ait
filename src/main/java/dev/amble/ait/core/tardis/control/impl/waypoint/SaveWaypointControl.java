package dev.amble.ait.core.tardis.control.impl.waypoint;

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
import dev.amble.ait.core.world.TardisServerWorld;
import dev.amble.ait.data.Waypoint;
import dev.amble.lib.data.CachedDirectedGlobalPos;

public class SaveWaypointControl extends Control {

    public SaveWaypointControl() {
        super(AITMod.id("save_waypoint"));
    }

    @Override
    public Result runServer(Tardis tardis, ServerPlayerEntity player, ServerWorld world, BlockPos console, boolean leftClick) {
        super.runServer(tardis, player, world, console, leftClick);

        if (!tardis.waypoint().hasCartridge()) {
            player.sendMessage(Text.translatable("control.ait.load_waypoint.no_cartridge"), true);
            TardisDesktop.playSoundAtConsole(world, console, SoundEvents.BLOCK_NOTE_BLOCK_BIT.value(), SoundCategory.PLAYERS, 6f, 0.1f);
            return Result.SUCCESS;
        }

        CachedDirectedGlobalPos cached = tardis.travel().position();
        if (cached.getWorld() instanceof TardisServerWorld) {
            cached = CachedDirectedGlobalPos.create(TardisServerWorld.OVERWORLD, cached.getPos(), cached.getRotation());
        }
        tardis.waypoint().set(Waypoint.fromPos(cached), console, false);
        TardisDesktop.playSoundAtConsole(world, console, AITSounds.TARDIS_BLING, SoundCategory.PLAYERS, 6f, 1);
        return Result.SUCCESS;
    }

    @Override
    public SoundEvent getFallbackSound() {
        return AITSounds.SAVE_WAYPOINT;
    }
}
