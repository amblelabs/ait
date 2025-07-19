package dev.drtheo.gaslighter.api;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

import net.minecraft.block.BlockState;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public class FakeBlockEvents {

    public static final Event<Check> CHECK = EventFactory.createArrayBacked(Check.class, callbacks -> (player, state, pos) -> {
        for (Check check : callbacks) {
            check.check(player, state, pos);
        }
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
    public interface Check {
        void check(ServerPlayerEntity player, BlockState state, BlockPos pos);
    }

    @FunctionalInterface
    public interface Place {
        void onPlace(ServerWorld world, BlockState state, BlockPos pos);
    }

    @FunctionalInterface
    public interface Remove {
        void onRemove(ServerWorld world, BlockPos pos);
    }
}
