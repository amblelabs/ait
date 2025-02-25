package dev.amble.ait.core.tardis.control.impl;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;

import dev.amble.ait.core.AITSounds;
import dev.amble.ait.core.engine.SubSystem;
import dev.amble.ait.core.tardis.Tardis;
import dev.amble.ait.core.tardis.control.Control;

public class AutoPilotControl extends Control {

    public AutoPilotControl() {
        // ☸ ?
        super("protocol_116");
    }

    @Override
    public boolean runServer(Tardis tardis, ServerPlayerEntity player, ServerWorld world, BlockPos console, boolean leftClick) {
        if (tardis.sequence().hasActiveSequence() && tardis.sequence().controlPartOfSequence(this)) {
            this.addToControlSequence(tardis, player, console);
            return false;
        }

        // Toggle autopilot state
        boolean autopilot = tardis.travel().autopilot();
        tardis.travel().autopilot(!autopilot);
        return true;
    }

    @Override
    protected SubSystem.IdLike requiredSubSystem() {
        return SubSystem.Id.STABILISERS;
    }

    @Override
    public SoundEvent getSound() {
        return AITSounds.PROTOCOL_116_ON; // You may still want to toggle sounds here if necessary
    }
}
