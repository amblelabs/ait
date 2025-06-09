package dev.amble.ait.core.tardis.control.impl;

import dev.amble.ait.AITMod;
import dev.amble.ait.core.AITSounds;
import dev.amble.ait.core.tardis.Tardis;
import dev.amble.ait.core.tardis.control.Control;
import dev.amble.ait.core.tardis.handler.travel.TravelHandler;
import dev.amble.ait.core.tardis.handler.travel.TravelHandlerBase;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;

public class ThrottleControl extends Control {

    public ThrottleControl() {
        super(AITMod.id("throttle"));
    }

    @Override
    public Result runServer(Tardis tardis, ServerPlayerEntity player, ServerWorld world, BlockPos console, boolean leftClick) {
        super.runServer(tardis, player, world, console, leftClick);

        if (tardis.isInDanger())
            return Result.FAILURE;

        TravelHandler travel = tardis.travel();
        TravelHandlerBase.State state = travel.getState();

        if (TelepathicControl.isLiquid(player.getMainHandStack())) {
            return TelepathicControl.spillLiquid(tardis, world, console, player);
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


        return player.isSneaking() ? Result.SUCCESS_ALT : Result.SUCCESS;
    }

    @Override
    public SoundEvent getFallbackSound() {
        return AITSounds.THROTTLE_PULL;
    }

    @Override
    public boolean requiresPower() {
        return false;
    }
}
