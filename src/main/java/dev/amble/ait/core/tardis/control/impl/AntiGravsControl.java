package dev.amble.ait.core.tardis.control.impl;

import dev.amble.lib.data.CachedDirectedGlobalPos;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;

import dev.amble.ait.AITMod;
import dev.amble.ait.core.AITBlocks;
import dev.amble.ait.core.AITSounds;
import dev.amble.ait.core.blockentities.ConsoleBlockEntity;
import dev.amble.ait.core.engine.SubSystem;
import dev.amble.ait.core.tardis.Tardis;
import dev.amble.ait.core.tardis.control.Control;
import dev.amble.ait.data.schema.console.variant.renaissance.*;

public class AntiGravsControl extends Control {

    private SoundEvent soundEvent = AITSounds.ANTI_GRAVS;

    public AntiGravsControl() {
        super(AITMod.id("antigravs"));
    }

    @Override
    public boolean runServer(Tardis tardis, ServerPlayerEntity player, ServerWorld world, BlockPos console, boolean leftClick) {
        super.runServer(tardis, player, world, console, leftClick);

        boolean isRenaissance = false;

        if (world.getBlockEntity(console) instanceof ConsoleBlockEntity consoleBlockEntity)
            isRenaissance = isRenaissanceVariant(consoleBlockEntity);

        this.soundEvent = isRenaissance ? AITSounds.RENAISSANCE_ANTI_GRAV_ALT : AITSounds.ANTI_GRAVS;

        tardis.travel().antigravs().toggle();

        CachedDirectedGlobalPos globalPos = tardis.travel().position();
        ServerWorld targetWorld = globalPos.getWorld();
        BlockPos pos = globalPos.getPos();

        targetWorld.getChunkManager().markForUpdate(pos);
        world.scheduleBlockTick(pos, AITBlocks.EXTERIOR_BLOCK, 2);
        return true;
    }

    @Override
    public SoundEvent getSound() {
        return this.soundEvent;
    }

    @Override
    protected SubSystem.IdLike requiredSubSystem() {
        return SubSystem.Id.GRAVITATIONAL;
    }

    private boolean isRenaissanceVariant(ConsoleBlockEntity consoleBlockEntity) {
        return consoleBlockEntity.getVariant() instanceof RenaissanceTokamakVariant ||
                consoleBlockEntity.getVariant() instanceof RenaissanceVariant ||
                consoleBlockEntity.getVariant() instanceof RenaissanceIndustriousVariant ||
                consoleBlockEntity.getVariant() instanceof RenaissanceIdentityVariant ||
                consoleBlockEntity.getVariant() instanceof RenaissanceFireVariant;
    }
}
