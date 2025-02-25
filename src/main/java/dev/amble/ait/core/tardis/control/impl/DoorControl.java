package dev.amble.ait.core.tardis.control.impl;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;

import dev.amble.ait.core.AITSounds;
import dev.amble.ait.core.blockentities.ConsoleBlockEntity;
import dev.amble.ait.core.tardis.Tardis;
import dev.amble.ait.core.tardis.control.Control;
import dev.amble.ait.data.schema.console.variant.renaissance.*;

public class DoorControl extends Control {

    private SoundEvent soundEvent = SoundEvents.BLOCK_STONE_BUTTON_CLICK_OFF;

    public DoorControl() {
        super("door_control");
    }

    @Override
    public boolean runServer(Tardis tardis, ServerPlayerEntity player, ServerWorld world, BlockPos console) {
        if (tardis.sequence().hasActiveSequence() && tardis.sequence().controlPartOfSequence(this)) {
            this.addToControlSequence(tardis, player, console);
            return false;
        }

        boolean isRenaissance = false;
        if (world.getBlockEntity(console) instanceof ConsoleBlockEntity consoleBlockEntity) {
            isRenaissance = isRenaissanceVariant(consoleBlockEntity);
        }

        if (isRenaissance) {
            this.soundEvent = !tardis.door().isOpen()
                    ? AITSounds.RENAISSANCE_DOOR_ALT
                    : AITSounds.RENAISSANCE_DOOR_ALTALT;
        } else {
            this.soundEvent = !tardis.door().isOpen()
                    ? AITSounds.DOOR_CONTROL
                    : AITSounds.DOOR_CONTROLALT;
        }

        tardis.door().interact(world, player.getBlockPos(), player);
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
