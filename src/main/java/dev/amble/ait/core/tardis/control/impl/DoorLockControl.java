package dev.amble.ait.core.tardis.control.impl;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;

import dev.amble.ait.AITMod;
import dev.amble.ait.core.AITSounds;
import dev.amble.ait.core.blockentities.ConsoleBlockEntity;
import dev.amble.ait.core.tardis.Tardis;
import dev.amble.ait.core.tardis.control.Control;
import dev.amble.ait.data.schema.console.variant.renaissance.*;

public class DoorLockControl extends Control {

    private SoundEvent soundEvent = AITSounds.DOOR_LOCK;

    public DoorLockControl() {
        super(AITMod.id("door_lock"));
    }

    @Override
    public boolean runServer(Tardis tardis, ServerPlayerEntity player, ServerWorld world, BlockPos console, boolean leftClick) {
        super.runServer(tardis, player, world, console, leftClick);

        boolean isRenaissance = false;
        if (world.getBlockEntity(console) instanceof ConsoleBlockEntity consoleBlockEntity)
            isRenaissance = isRenaissanceVariant(consoleBlockEntity);

        this.soundEvent = isRenaissance ? AITSounds.RENAISSANCE_LOCK_ALT : AITSounds.DOOR_LOCK;

        tardis.door().interactToggleLock(player);
        return true;
    }

    @Override
    public SoundEvent getSound() {
        return this.soundEvent;
    }

    private boolean isRenaissanceVariant(ConsoleBlockEntity consoleBlockEntity) {
        return consoleBlockEntity.getVariant() instanceof RenaissanceTokamakVariant ||
                consoleBlockEntity.getVariant() instanceof RenaissanceVariant ||
                consoleBlockEntity.getVariant() instanceof RenaissanceIndustriousVariant ||
                consoleBlockEntity.getVariant() instanceof RenaissanceIdentityVariant ||
                consoleBlockEntity.getVariant() instanceof RenaissanceFireVariant;
    }
}
