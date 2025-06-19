package dev.amble.ait.core.tardis.util;

import java.text.NumberFormat;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import com.mojang.datafixers.util.Pair;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import org.jetbrains.annotations.NotNull;

import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.structure.Structure;

import dev.amble.ait.AITMod;

/**
 * @author TheBrightSpark, Loqor
 * @implNote This is pretty much copy-pasted from <a href=
 *           "https://github.com/thebrightspark/AsyncLocator/blob/1.20.x/Common/src/main/java/brightspark/asynclocator/AsyncLocator.java">...</a>
 */
public class AsyncLocatorUtil {

    public static ExecutorService LOCATING_EXECUTOR_SERVICE = null;

    public static void init() {
        ServerLifecycleEvents.SERVER_STOPPING.register(
                server -> AsyncLocatorUtil.shutdownExecutorService());

        ServerLifecycleEvents.SERVER_STARTING.register(
                server -> AsyncLocatorUtil.setupExecutorService());
    }

    public static void setupExecutorService() {
        shutdownExecutorService();

        int threads = Runtime.getRuntime().availableProcessors() / 2;
        AITMod.LOGGER.trace("Starting locating executor service with thread pool size of {}", threads);

        if (threads <= 0) {
            AITMod.LOGGER.error("Failed to start locating executor service: thread pool size is 0 or less - {}. Available Processors {}", threads, Runtime.getRuntime().availableProcessors());
            return;
        }

        LOCATING_EXECUTOR_SERVICE = Executors.newCachedThreadPool(new ThreadFactory() {
            private static final AtomicInteger poolNum = new AtomicInteger(1);
            private final AtomicInteger threadNum = new AtomicInteger(1);
            private final String namePrefix = AITMod.MOD_ID + "-" + poolNum.getAndIncrement() + "-thread-";

            @Override
            public Thread newThread(@NotNull Runnable r) {
                return new Thread(r, namePrefix + threadNum.getAndIncrement());
            }
        });
    }

    public static void shutdownExecutorService() {
        if (LOCATING_EXECUTOR_SERVICE != null) {
            AITMod.LOGGER.trace("Shutting down locating executor service");
            LOCATING_EXECUTOR_SERVICE.shutdown();
        }
    }

    /**
     * Queues a task to locate a feature using
     * {@link ServerWorld#locateStructure(TagKey, BlockPos, int, boolean)} and
     * returns a {@link LocateTask} with the futures for it.
     */
    public static LocateTask<BlockPos> locate(ServerWorld level, TagKey<Structure> structureTag, BlockPos pos,
            int searchRadius, boolean skipKnownStructures) {
        AITMod.LOGGER.trace("Creating locate task for {} in {} around {} within {} chunks", structureTag, level, pos,
                searchRadius);
        CompletableFuture<BlockPos> completableFuture = new CompletableFuture<>();
        Future<?> future = LOCATING_EXECUTOR_SERVICE.submit(
                () -> doLocateLevel(completableFuture, level, structureTag, pos, searchRadius, skipKnownStructures));
        return new LocateTask<>(level.getServer(), completableFuture, future);
    }

    /**
     * Queues a task to locate a feature using
     * {@link net.minecraft.world.gen.chunk.ChunkGenerator#locateStructure(ServerWorld, RegistryEntryList, BlockPos, int, boolean)}
     * and returns a {@link LocateTask} with the futures for it.
     */
    public static LocateTask<Pair<BlockPos, RegistryEntry<Structure>>> locate(ServerWorld level,
            RegistryEntryList<Structure> structureSet, BlockPos pos, int searchRadius, boolean skipKnownStructures) {
        AITMod.LOGGER.trace("Creating locate task for {} in {} around {} within {} chunks", structureSet, level, pos,
                searchRadius);
        CompletableFuture<Pair<BlockPos, RegistryEntry<Structure>>> completableFuture = new CompletableFuture<>();
        Future<?> future = LOCATING_EXECUTOR_SERVICE.submit(() -> doLocateChunkGenerator(completableFuture, level,
                structureSet, pos, searchRadius, skipKnownStructures));
        return new LocateTask<>(level.getServer(), completableFuture, future);
    }

    private static void doLocateLevel(CompletableFuture<BlockPos> completableFuture, ServerWorld level,
            TagKey<Structure> structureTag, BlockPos pos, int searchRadius, boolean skipExistingChunks) {
        AITMod.LOGGER.trace("Trying to locate {} in {} around {} within {} chunks", structureTag, level, pos,
                searchRadius);
        long start = System.nanoTime();
        BlockPos foundPos = level.locateStructure(structureTag, pos, searchRadius, skipExistingChunks);
        String time = NumberFormat.getNumberInstance().format(TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start));
        if (foundPos == null) {
            AITMod.LOGGER.trace("No {} found (took {}ms)", structureTag, time);
        } else {
            AITMod.LOGGER.trace("Found {} at {} (took {}ms)", structureTag, foundPos, time);
        }
        completableFuture.complete(foundPos);
    }

    private static void doLocateChunkGenerator(
            CompletableFuture<Pair<BlockPos, RegistryEntry<Structure>>> completableFuture, ServerWorld level,
            RegistryEntryList<Structure> structureSet, BlockPos pos, int searchRadius, boolean skipExistingChunks) {
        AITMod.LOGGER.info("Trying to locate {} in {} around {} within {} chunks", structureSet, level, pos,
                searchRadius);
        long start = System.nanoTime();
        Pair<BlockPos, RegistryEntry<Structure>> foundPair = level.getChunkManager().getChunkGenerator()
                .locateStructure(level, structureSet, pos, searchRadius, skipExistingChunks);
        String time = NumberFormat.getNumberInstance().format(TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start));
        if (foundPair == null) {
            AITMod.LOGGER.trace("No {} found (took {}ms)", structureSet, time);
        } else {
            AITMod.LOGGER.trace("Found {} at {} (took {}ms)", foundPair.getSecond().value().getClass().getSimpleName(),
                    foundPair.getFirst(), time);
        }
        completableFuture.complete(foundPair);
    }

    /**
     * Holder of the futures for an async locate task as well as providing some
     * helper functions. The completableFuture will be completed once the call to
     * {@link ServerWorld#locateStructure(TagKey, BlockPos, int, boolean)} has
     * completed, and will hold the result of it. The taskFuture is the future for
     * the {@link Runnable} itself in the executor service.
     */
    public record LocateTask<T>(MinecraftServer server, CompletableFuture<T> completableFuture, Future<?> taskFuture) {
        /**
         * Helper function that calls {@link CompletableFuture#thenAccept(Consumer)}
         * with the given action. Bear in mind that the action will be executed from the
         * task's thread. If you intend to change any game data, it's strongly advised
         * you use {@link #thenOnServerThread(Consumer)} instead so that it's queued and
         * executed on the main server thread instead.
         */
        public LocateTask<T> then(Consumer<T> action) {
            completableFuture.thenAccept(action);
            return this;
        }

        /**
         * Helper function that calls {@link CompletableFuture#thenAccept(Consumer)}
         * with the given action on the server thread.
         */
        public LocateTask<T> thenOnServerThread(Consumer<T> action) {
            completableFuture.thenAccept(pos -> server.submit(() -> action.accept(pos)));
            return this;
        }

        /**
         * Helper function that cancels both completableFuture and taskFuture.
         */
        public void cancel() {
            taskFuture.cancel(true);
            completableFuture.cancel(false);
        }
    }
}
