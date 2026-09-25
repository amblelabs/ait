package dev.amble.ait.compat.portal;

import java.util.Optional;
import java.util.function.BooleanSupplier;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public class PortalsAPI {

    public static Optional<VisualizerImpl> VISUALIZER = Optional.empty();
    public static BooleanSupplier RENDERING_PORTAL = () -> false;

    @FunctionalInterface
    public interface VisualizerImpl {
        void open(ServerPlayerEntity player, ServerWorld world, BlockPos pos);
    }
}
