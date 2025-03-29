package dev.amble.ait.core.tardis.control.impl;

import dev.amble.ait.core.tardis.handler.travel.TravelHandlerBase;
import dev.drtheo.queue.api.ActionQueue;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;

import dev.amble.ait.AITMod;
import dev.amble.ait.core.AITItems;
import dev.amble.ait.core.AITSounds;
import dev.amble.ait.core.advancement.TardisCriterions;
import dev.amble.ait.core.tardis.Tardis;
import dev.amble.ait.core.tardis.control.Control;
import dev.amble.ait.core.tardis.handler.travel.TravelHandler;

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

        if (player.getMainHandStack().isOf(AITItems.MUG)) {
            /*
            This is an example of how to use the travel queue.
            This code enqueues a crash to be performed after dematerialization.
             */

            ActionQueue drinkAction = new ActionQueue();

            drinkAction.thenRun(() -> {
                travel.speed(travel.maxSpeed().get());
                travel.crash();
                TardisCriterions.BRAND_NEW.trigger(player);
            });

            if (state == TravelHandlerBase.State.LANDED) {
                boolean hadAutopilot = travel.autopilot();

                travel.autopilot(true);

                travel.dematerialize().ifPresent(tr -> {
                    tr.thenRun(drinkAction);
                    tardis.alarm().enable();
                });

                travel.autopilot(hadAutopilot);

                return Result.SUCCESS;
            }

            if (state == TravelHandlerBase.State.FLIGHT) {
                drinkAction.execute();

                return Result.SUCCESS;
            }
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
