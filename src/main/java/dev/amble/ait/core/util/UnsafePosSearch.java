package dev.amble.ait.core.util;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import dev.amble.ait.AITMod;
import dev.amble.ait.core.AITTags;
import dev.amble.lib.data.CachedDirectedGlobalPos;
import org.jetbrains.annotations.Nullable;

import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Heightmap;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;

/**
 * Performs a small, synchronous search for deliberately hazardous landing
 * positions. It loads only the destination chunk and never expands chunk
 * generation to cover the full search radius.
 */
public final class UnsafePosSearch {

    private UnsafePosSearch() {
    }

    public static Optional<Result> find(CachedDirectedGlobalPos globalPos, boolean gravityDisabled) {
        ServerWorld world = globalPos.getWorld();

        if (world == null)
            return Optional.empty();

        // Materialization needs this chunk anyway. Loading only the destination
        // chunk makes remote unsafe searches useful without generating a wide area.
        world.getChunk(globalPos.getPos());

        Random random = world.getRandom();
        SearchSettings settings = SearchSettings.fromConfig();
        Hazard[] hazards = gravityDisabled
                ? new Hazard[]{Hazard.LAVA, Hazard.HOSTILE, Hazard.VOID}
                : new Hazard[]{Hazard.LAVA, Hazard.HOSTILE};

        shuffle(hazards, random);

        for (Hazard hazard : hazards) {
            Optional<SearchResult> result = switch (hazard) {
                case LAVA -> findLava(world, globalPos.getPos(), random, settings)
                        .map(pos -> new SearchResult(pos, null));
                case HOSTILE -> findHostile(world, globalPos.getPos(), random, settings)
                        .map(hostile -> new SearchResult(hostile.getBlockPos(), hostile.getUuid()));
                case VOID -> findVoid(world, globalPos.getPos(), random, settings)
                        .map(pos -> new SearchResult(pos, null));
            };

            if (result.isPresent())
                return result.map(found -> new Result(globalPos.pos(found.position()), found.hostileId()));
        }

        if (gravityDisabled)
            return findHighAir(world, globalPos.getPos(), random, settings)
                    .map(pos -> new Result(globalPos.pos(pos), null));

        return Optional.empty();
    }

    private static Optional<BlockPos> findLava(ServerWorld world, BlockPos center, Random random,
                                                SearchSettings settings) {
        int minY = world.getBottomY() + 2;
        int maxY = world.getTopY() - 3;

        if (minY > maxY)
            return Optional.empty();

        for (int attempt = 0; attempt < settings.columnAttempts(); attempt++) {
            BlockPos column = randomColumn(center, random, attempt, settings);
            Chunk chunk = getLoadedChunk(world, column);

            if (chunk == null)
                continue;

            int surfaceY = chunk.sampleHeightmap(Heightmap.Type.WORLD_SURFACE,
                    column.getX() & 15, column.getZ() & 15);
            Optional<BlockPos> surface = lavaLandingInColumn(chunk, column, surfaceY, minY, maxY);

            if (surface.isPresent())
                return surface;

            int fromY = Math.min(maxY, center.getY() + settings.verticalRadius());
            int toY = Math.max(minY, center.getY() - settings.verticalRadius());

            for (int y = fromY; y >= toY; y--) {
                Optional<BlockPos> result = lavaLandingInColumn(chunk, column, y, minY, maxY);

                if (result.isPresent())
                    return result;
            }
        }

        return Optional.empty();
    }

    private static Optional<BlockPos> lavaLandingInColumn(Chunk chunk, BlockPos column, int lavaY,
                                                           int minY, int maxY) {
        if (lavaY < minY || lavaY > maxY)
            return Optional.empty();

        BlockPos lava = new BlockPos(column.getX(), lavaY, column.getZ());

        if (!chunk.getBlockState(lava).getFluidState().isIn(FluidTags.LAVA))
            return Optional.empty();

        BlockPos above = lava.up();

        if (above.getY() <= maxY && hasExteriorSpace(chunk, above))
            return Optional.of(above);

        BlockPos below = lava.down(2);

        if (below.getY() >= minY && hasExteriorSpace(chunk, below))
            return Optional.of(below);

        return Optional.empty();
    }

    private static Optional<HostileEntity> findHostile(ServerWorld world, BlockPos center, Random random,
                                                        SearchSettings settings) {
        Box searchBox = new Box(center).expand(settings.horizontalRadius());
        List<HostileEntity> hostiles = world.getEntitiesByClass(HostileEntity.class, searchBox,
                entity -> entity.isAlive() && !entity.isRemoved()
                        && !entity.getType().isIn(AITTags.EntityTypes.BOSS));

        if (hostiles.isEmpty())
            return Optional.empty();

        int offset = random.nextInt(hostiles.size());
        int attempts = Math.min(settings.hostileAttempts(), hostiles.size());

        for (int i = 0; i < attempts; i++) {
            HostileEntity hostile = hostiles.get((offset + i) % hostiles.size());
            BlockPos pos = hostile.getBlockPos();
            Chunk chunk = getLoadedChunk(world, pos);

            if (chunk != null && hasExteriorSpace(chunk, pos))
                return Optional.of(hostile);
        }

        return Optional.empty();
    }

    private static Optional<BlockPos> findVoid(ServerWorld world, BlockPos center, Random random,
                                                SearchSettings settings) {
        int minY = world.getBottomY();
        int landingY = Math.max(minY + Math.max(1, settings.voidHeight()),
                Math.min(center.getY(), world.getTopY() - 3));

        for (int attempt = 0; attempt < settings.columnAttempts(); attempt++) {
            BlockPos column = randomColumn(center, random, attempt, settings);
            Chunk chunk = getLoadedChunk(world, column);

            if (chunk == null)
                continue;

            int surfaceY = chunk.sampleHeightmap(Heightmap.Type.WORLD_SURFACE,
                    column.getX() & 15, column.getZ() & 15);

            if (surfaceY > minY)
                continue;

            BlockPos result = new BlockPos(column.getX(), landingY, column.getZ());

            if (hasExteriorSpace(chunk, result))
                return Optional.of(result);
        }

        return Optional.empty();
    }

    private static Optional<BlockPos> findHighAir(ServerWorld world, BlockPos center, Random random,
                                                   SearchSettings settings) {
        int y = world.getTopY() - Math.max(1, settings.buildLimitMargin());

        if (y <= world.getBottomY())
            return Optional.empty();

        for (int attempt = 0; attempt < settings.columnAttempts(); attempt++) {
            BlockPos column = randomColumn(center, random, attempt, settings);
            Chunk chunk = getLoadedChunk(world, column);

            if (chunk == null)
                continue;

            BlockPos result = new BlockPos(column.getX(), y, column.getZ());

            if (hasExteriorSpace(chunk, result))
                return Optional.of(result);
        }

        return Optional.empty();
    }

    private static BlockPos randomColumn(BlockPos center, Random random, int attempt, SearchSettings settings) {
        if (attempt == 0)
            return center;

        if (attempt < settings.localColumnAttempts()) {
            int minX = Math.floorDiv(center.getX(), 16) * 16;
            int minZ = Math.floorDiv(center.getZ(), 16) * 16;
            return new BlockPos(minX + random.nextInt(16), center.getY(), minZ + random.nextInt(16));
        }

        int x;
        int z;
        int radius = settings.horizontalRadius();

        do {
            x = random.nextInt(radius * 2 + 1) - radius;
            z = random.nextInt(radius * 2 + 1) - radius;
        } while (x * x + z * z > radius * radius);

        return center.add(x, 0, z);
    }

    private static Chunk getLoadedChunk(ServerWorld world, BlockPos pos) {
        ChunkPos chunkPos = new ChunkPos(pos);
        return world.getChunkManager().getChunk(chunkPos.x, chunkPos.z, ChunkStatus.FULL, false);
    }

    private static boolean hasExteriorSpace(Chunk chunk, BlockPos pos) {
        if (pos.getY() < chunk.getBottomY() || pos.getY() + 1 >= chunk.getTopY())
            return false;

        return chunk.getBlockState(pos).isAir() && chunk.getBlockState(pos.up()).isAir();
    }

    private static void shuffle(Hazard[] hazards, Random random) {
        for (int i = hazards.length - 1; i > 0; i--) {
            int target = random.nextInt(i + 1);
            Hazard value = hazards[i];
            hazards[i] = hazards[target];
            hazards[target] = value;
        }
    }

    private record SearchSettings(int horizontalRadius, int verticalRadius, int columnAttempts,
                                  int localColumnAttempts, int hostileAttempts, int voidHeight,
                                  int buildLimitMargin) {

        private static SearchSettings fromConfig() {
            return new SearchSettings(AITMod.CONFIG.temperamentUnsafeLandingHorizontalRadius,
                    AITMod.CONFIG.temperamentUnsafeLandingVerticalRadius,
                    AITMod.CONFIG.temperamentUnsafeLandingColumnAttempts,
                    AITMod.CONFIG.temperamentUnsafeLandingLocalColumnAttempts,
                    AITMod.CONFIG.temperamentUnsafeLandingHostileAttempts,
                    AITMod.CONFIG.temperamentUnsafeLandingVoidHeight,
                    AITMod.CONFIG.temperamentUnsafeLandingBuildLimitMargin);
        }
    }

    public record Result(CachedDirectedGlobalPos position, @Nullable UUID hostileId) {
    }

    private record SearchResult(BlockPos position, @Nullable UUID hostileId) {
    }

    private enum Hazard {
        LAVA,
        HOSTILE,
        VOID
    }
}
