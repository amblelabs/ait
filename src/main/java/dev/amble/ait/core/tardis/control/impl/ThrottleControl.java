package dev.amble.ait.core.tardis.control.impl;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;

import dev.amble.ait.core.AITItems;
import dev.amble.ait.core.AITSounds;
import dev.amble.ait.core.advancement.TardisCriterions;
import dev.amble.ait.core.tardis.Tardis;
import dev.amble.ait.core.tardis.control.Control;
import dev.amble.ait.core.tardis.handler.travel.TravelHandler;

public class ThrottleControl extends Control {
    public ThrottleControl() {
        super("throttle");
    }

    @Override
    public boolean runServer(Tardis tardis, ServerPlayerEntity player, ServerWorld world, BlockPos console,
            boolean leftClick) {
        if (tardis.sequence().hasActiveSequence() && tardis.sequence().controlPartOfSequence(this)) {
            this.addToControlSequence(tardis, player, console);
            return false;
        }

        if (tardis.isInDanger())
            return false;

        TravelHandler travel = tardis.travel();

        if (player.getMainHandStack().isOf(AITItems.MUG)) {
            travel.crash();

            TardisCriterions.BRAND_NEW.trigger(player);
        }

        if (!leftClick) {
            if (player.isSneaking()) {
                travel.speed(travel.maxSpeed().get());
            } else {
                travel.increaseSpeed();
            }
        } else {
            if (player.isSneaking()) {
                travel.speed(0);
            } else {
                travel.decreaseSpeed();
            }
        }

        if (travel.getState() == TravelHandler.State.DEMAT)
            tardis.sequence().setActivePlayer(player);

        return true;
    }

    @Override
    public SoundEvent getSound() {
        return AITSounds.DEMAT_LEVER_PULL;
    }

    @Override
    public boolean requiresPower() {
        return false;
    }
}
