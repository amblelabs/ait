package dev.amble.ait.core.util;

import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

public class SafePos {

    private final BlockBox shape;
    private final BlockPos currentPos;

    private final World world;
    private final Chunk[] chunkCache = new Chunk[4];

    public SafePos(World world, BlockBox shape, BlockPos currentPos) {
        this.world = world;
        this.shape = shape;
        this.currentPos = currentPos;
    }

    public static void findSafeTopY(ServerWorld world, BlockPos pos) {

    }

    private int getAbsoluteMinX() {
        return shape.getMinX() + currentPos.getX();
    }

    private int getAbsoluteMinY() {
        return shape.getMinY() + currentPos.getX();
    }

    private int getAbsoluteMinZ() {
        return shape.getMinZ() + currentPos.getX();
    }

    private int getAbsoluteMaxX() {
        return shape.getMaxX() + currentPos.getX();
    }

    private int getAbsoluteMaxY() {
        return shape.getMaxY() + currentPos.getY();
    }

    private int getAbsoluteMaxZ() {
        return shape.getMaxZ() + currentPos.getZ();
    }

    private int getChunkIndex(int x, int z) {
        int chunkX = ChunkSectionPos.getSectionCoord(x - this.currentPos.getX());
        int chunkZ = ChunkSectionPos.getSectionCoord(z - this.currentPos.getZ());

        if (chunkX > 0)
            return chunkZ > 0 ? 0 : 3;

        return chunkZ > 0 ? 1 : 2;
    }

    private Chunk getChunk(int x, int z) {
        int i = getChunkIndex(x, z);
        Chunk chunk = this.chunkCache[i];

        if (chunk == null) {
            chunk = this.world.getChunk(
                    ChunkSectionPos.getSectionCoord(x),
                    ChunkSectionPos.getSectionCoord(z)
            );

            this.chunkCache[i] = chunk;
        }

        return chunk;
    }

    private BlockState getBlock(BlockPos pos) {
        return this.getChunk(pos.getX(), pos.getZ()).getBlockState(pos);
    }

    private boolean spaceFree() {
        int minX = this.getAbsoluteMinX();
        int minZ = this.getAbsoluteMinZ();

        int minY = this.getAbsoluteMinY();
        int maxY = this.getAbsoluteMaxY();

        int maxX = this.getAbsoluteMaxX();
        int maxZ = this.getAbsoluteMaxZ();

        for (BlockPos pos : BlockPos.iterate(minX, minY, minZ, maxX, maxY, maxZ)) {
            if (this.getBlock(pos).blocksMovement())
                return false;
        }

        return true;
    }

    private boolean standsOnGround() {
        int minX = this.getAbsoluteMinX();
        int minZ = this.getAbsoluteMinZ();

        int maxX = this.getAbsoluteMaxX();
        int maxZ = this.getAbsoluteMaxZ();

        int y = shape.getMinY() + currentPos.getY() - 1;

        for (BlockPos pos : BlockPos.iterate(minX, y, minZ, maxX, y, maxZ)) {
            if (this.getBlock(pos).blocksMovement())
                return true;
        }

        return false;
    }
}
