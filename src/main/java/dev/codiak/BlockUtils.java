/* (C) TAMA Studios 2025 */
package dev.codiak;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.LightType;
import net.minecraft.world.World;

public class BlockUtils {
    public static BlockPos getRelativeBlockPos(BlockPos basePos, BlockPos offsetPos) {
        return new BlockPos(
                basePos.getX() + offsetPos.getX(),
                basePos.getY() + offsetPos.getY(),
                basePos.getZ() + offsetPos.getZ());
    }

    public static BlockPos fromChunkAndLocal(ChunkPos chunkPos, BlockPos localPos) {
        int worldX = (chunkPos.x << 4) + localPos.getX();
        int worldY = localPos.getY();
        int worldZ = (chunkPos.z << 4) + localPos.getZ();
        return new BlockPos(worldX, worldY, worldZ);
    }

    public static int getPackedLight(World level, BlockPos pos) {
        int sky = level.getLightLevel(pos);
        int block = level.getLightLevel(LightType.BLOCK, pos);
        return (MathHelper.clamp(block, 0, 15) << 4) | (MathHelper.clamp(sky, 0, 15) << 20);
    }

    public static int getLight(World level, BlockPos pos) {
        return MathHelper.clamp(level.getLightLevel(LightType.BLOCK, pos) + level.getLightLevel(pos), 0, 15);
    }
}
