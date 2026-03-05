package dev.amble.ait.core.blocks;

import dev.amble.ait.core.AITEntityTypes;
import dev.amble.ait.core.entities.RiftEntity;
import dev.amble.ait.core.world.RiftChunkManager;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import org.jetbrains.annotations.Nullable;

/**
 * @author Loqor
 * The purpose of this block is to rip open rifts in rift chunks as opposed to the silly entities.
 * It mimics the Lodestone block from Doctor Who lore as opposed to the usual Lodestone in Minecraft.
 * */
public class ChargedLodestoneBlock extends Block {
    public ChargedLodestoneBlock(Settings settings) {
        super(settings);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        if (!(world instanceof ServerWorld serverWorld)) return;

        RiftChunkManager manager = RiftChunkManager.getInstance(serverWorld);
        ChunkPos chunkPos = new ChunkPos(pos);

        if (!isConsumable(manager, chunkPos)) {
            if (placer != null) {
                placer.sendMessage(Text.translatable("message.ait.riftscanner.info3"));
            }
            world.playSound(null, pos, SoundEvents.BLOCK_BEACON_DEACTIVATE,
                    SoundCategory.BLOCKS, 1, 0.5f);
            return;
        }

        world.playSound(null, pos, SoundEvents.BLOCK_BEACON_ACTIVATE,
                SoundCategory.BLOCKS, 1, 1.5f);

        RiftEntity riftEntity = AITEntityTypes.RIFT_ENTITY.create(world);

        if (riftEntity == null) return;

        // Center of the chunk is startX + 8, startZ + 8
        int centerX = chunkPos.getStartX() + 8;
        int centerZ = chunkPos.getStartZ() + 8;

        // Sample the heightmap at the chunk center, excluding leaves
        Chunk chunk = world.getChunk(pos);
        int topY = chunk.sampleHeightmap(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES,
                centerX & 15, centerZ & 15);

        // Spawn 12 blocks above the top block
        double spawnY = topY + 12;

        riftEntity.refreshPositionAndAngles(centerX + 0.5, spawnY, centerZ + 0.5, 180, -90);
        world.spawnEntity(riftEntity);
    }

    private static boolean isConsumable(RiftChunkManager manager, ChunkPos pos) {
        return manager.isRiftChunk(pos)/* && manager.getArtron(pos) >= 250*/;
    }
}