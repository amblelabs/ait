package dev.amble.ait.compat.portal;

import java.util.Optional;
import java.util.function.BiPredicate;

import dev.amble.ait.core.tardis.Tardis;

import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public class PortalsAPI {

    public static Optional<VisualizerImpl> VISUALIZER = Optional.empty();
    public static BiPredicate<Tardis, Entity> BEHIND_EXTERIOR = (tardis, entity) -> false;

    @FunctionalInterface
    public interface VisualizerImpl {
        void open(ServerPlayerEntity player, ServerWorld world, BlockPos pos);
    }
}
