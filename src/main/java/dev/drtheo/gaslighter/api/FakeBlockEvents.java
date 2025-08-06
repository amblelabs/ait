package dev.drtheo.gaslighter.api;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

import net.minecraft.block.BlockState;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;

public class FakeBlockEvents {

    public static final Event<Interact> INTERACT = EventFactory.createArrayBacked(Interact.class, callbacks -> (player, hand, pos) -> {
        Action action = Action.CONTINUE;
        for (Interact check : callbacks) {
            action = check.check(player, hand, pos);

            if (action != Action.CONTINUE)
                break;
        }

        return action.asResult();
    });

    public static final Event<Check> CHECK = EventFactory.createArrayBacked(Check.class, callbacks -> (player, hand, state, pos) -> {
        Action action = Action.CONTINUE;
        for (Check check : callbacks) {
            action = check.check(player, hand, state, pos);

            if (action != Action.CONTINUE)
                break;
        }

        return action.asResult();
    });

    public static final Event<Place> PLACED = EventFactory.createArrayBacked(Place.class, callbacks -> (world, state, pos) -> {
        for (Place place : callbacks) {
            place.onPlace(world, state, pos);
        }
    });

    public static final Event<Remove> REMOVED = EventFactory.createArrayBacked(Remove.class, callbacks -> (world, pos) -> {
        for (Remove place : callbacks) {
            place.onRemove(world, pos);
        }
    });

    @FunctionalInterface
    public interface Interact {
        Action check(ServerPlayerEntity player, Hand hand, BlockPos pos);
    }

    @FunctionalInterface
    public interface Check {
        Action check(ServerPlayerEntity player, Hand hand, BlockState state, BlockPos pos);
    }

    @FunctionalInterface
    public interface Place {
        void onPlace(ServerWorld world, BlockState state, BlockPos pos);
    }

    @FunctionalInterface
    public interface Remove {
        void onRemove(ServerWorld world, BlockPos pos);
    }

    public enum Action {
        REMOVE,
        STAY,
        CONTINUE;

        public boolean shouldRemove() {
            return this == REMOVE;
        }

        public Action asResult() {
            return this == CONTINUE ? STAY : this;
        }
    }
}
