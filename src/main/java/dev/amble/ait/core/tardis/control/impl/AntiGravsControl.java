package dev.amble.ait.core.tardis.control.impl;

import dev.amble.ait.api.ForcedTickableWorld;
import dev.amble.lib.data.CachedDirectedGlobalPos;

import dev.drtheo.scheduler.api.TimeUnit;
import dev.drtheo.scheduler.api.common.Scheduler;
import dev.drtheo.scheduler.api.common.TaskStage;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.*;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import dev.amble.ait.AITMod;
import dev.amble.ait.core.AITBlocks;
import dev.amble.ait.core.AITSounds;
import dev.amble.ait.core.engine.SubSystem;
import dev.amble.ait.core.tardis.Tardis;
import dev.amble.ait.core.tardis.control.Control;
import net.minecraft.util.math.ChunkPos;

public class AntiGravsControl extends Control {
    public static final Identifier ID = AITMod.id("antigravs");

    public AntiGravsControl() {
        super(ID);
    }

    @Override
    public Result runServer(Tardis tardis, ServerPlayerEntity player, ServerWorld world, BlockPos console, boolean leftClick) {
        super.runServer(tardis, player, world, console, leftClick);

        tardis.travel().antigravs().toggle();

        CachedDirectedGlobalPos globalPos = tardis.travel().position();
        ServerWorld targetWorld = globalPos.getWorld();
        BlockPos pos = globalPos.getPos();
        ChunkPos chunkPos = new ChunkPos(pos);

        targetWorld.getChunkManager().markForUpdate(pos);
        targetWorld.scheduleBlockTick(pos, targetWorld.getBlockState(pos).getBlock(), 2);

        Scheduler.get().runTaskLater(() -> {
            //targetWorld.getChunkManager().markForUpdate(pos);
            System.out.println(targetWorld);
            System.out.println(pos);
            System.out.println(world);
            //targetWorld.scheduleBlockTick(pos, targetWorld.getBlockState(pos).getBlock(), 2);
        }, TaskStage.START_SERVER_TICK, TimeUnit.TICKS, 1);

        return tardis.travel().antigravs().get() ? Result.SUCCESS : Result.SUCCESS_ALT;
    }

    @Override
    public SoundEvent getFallbackSound() {
        return AITSounds.ANTI_GRAVS;
    }

    @Override
    protected SubSystem.IdLike requiredSubSystem() {
        return SubSystem.Id.GRAVITATIONAL;
    }
}
