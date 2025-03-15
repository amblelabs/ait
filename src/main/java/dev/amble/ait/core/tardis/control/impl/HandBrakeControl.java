package dev.amble.ait.core.tardis.control.impl;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;

import dev.amble.ait.AITMod;
import dev.amble.ait.core.AITSounds;
import dev.amble.ait.core.blockentities.ConsoleBlockEntity;
import dev.amble.ait.core.engine.SubSystem;
import dev.amble.ait.core.engine.impl.EngineSystem;
import dev.amble.ait.core.tardis.Tardis;
import dev.amble.ait.core.tardis.control.Control;
import dev.amble.ait.core.tardis.handler.travel.TravelHandler;
import dev.amble.ait.core.tardis.handler.travel.TravelHandlerBase;
import dev.amble.ait.data.schema.console.variant.renaissance.*;

public class HandBrakeControl extends Control {

    private SoundEvent soundEvent = AITSounds.HANDBRAKE_UP;

    public HandBrakeControl() {
        super(AITMod.id("handbrake"));
    }

    @Override
    public boolean runServer(Tardis tardis, ServerPlayerEntity player, ServerWorld world, BlockPos console, boolean leftClick) {
        super.runServer(tardis, player, world, console, leftClick);

        if (tardis.isInDanger())
            return false;

        EngineSystem.Phaser phaser = tardis.subsystems().engine().phaser();

        if (phaser.isPhasing()) {
            phaser.cancel();
            return true;
        }

        boolean handbrake = !tardis.travel().handbrake();
        tardis.travel().handbrake(handbrake);

        if (tardis.isRefueling()) {
            tardis.setRefueling(false);
        }

        if (world.getBlockEntity(console) instanceof ConsoleBlockEntity consoleBlockEntity) {
            if (isRenaissanceVariant(consoleBlockEntity)) {
                this.soundEvent = handbrake ? AITSounds.RENAISSANCE_HANDBRAKE_ALT : AITSounds.RENAISSANCE_HANDBRAKE_ALTALT;
            } else {
                this.soundEvent = handbrake ? AITSounds.HANDBRAKE_DOWN : AITSounds.HANDBRAKE_UP;
            }
        }

        TravelHandler travel = tardis.travel();

        if (handbrake && travel.getState() == TravelHandlerBase.State.FLIGHT) {
            if (travel.autopilot()) {
                travel.stopHere();
                travel.rematerialize();
            } else {
                travel.crash();
            }
        }

        return true;
    }

    @Override
    public SoundEvent getSound() {
        return this.soundEvent;
    }

    @Override
    public boolean requiresPower() {
        return false;
    }

    @Override
    protected SubSystem.IdLike requiredSubSystem() {
        return null;
    }

    private boolean isRenaissanceVariant(ConsoleBlockEntity consoleBlockEntity) {
        return consoleBlockEntity.getVariant() instanceof RenaissanceTokamakVariant ||
                consoleBlockEntity.getVariant() instanceof RenaissanceVariant ||
                consoleBlockEntity.getVariant() instanceof RenaissanceIndustriousVariant ||
                consoleBlockEntity.getVariant() instanceof RenaissanceIdentityVariant ||
                consoleBlockEntity.getVariant() instanceof RenaissanceFireVariant;
    }
}
