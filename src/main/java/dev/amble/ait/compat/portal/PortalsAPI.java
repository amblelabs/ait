package dev.amble.ait.compat.portal;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

import java.util.Optional;

public class PortalsAPI {

    public static Optional<VisualizerImpl> VISUALIZER = Optional.empty();

    @FunctionalInterface
    public interface VisualizerImpl {
        void open(ServerPlayerEntity player, ServerWorld world, BlockPos pos);
    }
}
