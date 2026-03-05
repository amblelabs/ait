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
import net.minecraft.world.World;
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

        world.playSound(null, pos, SoundEvents.BLOCK_BEACON_DEACTIVATE,
                SoundCategory.BLOCKS, 1, 0.5f);

        if (world instanceof ServerWorld serverWorld) {

            RiftChunkManager manager = RiftChunkManager.getInstance(serverWorld);

            if (!isConsumable(manager, new ChunkPos(pos))) {
                if (placer != null) {
                    placer.sendMessage(Text.literal("No rift chunk here!"));
                }
                world.playSound(null, pos, SoundEvents.BLOCK_BEACON_DEACTIVATE,
                        SoundCategory.BLOCKS, 1, 0.5f);
                return;
            }

            world.playSound(null, pos, SoundEvents.BLOCK_BEACON_ACTIVATE,
                    SoundCategory.BLOCKS, 1, 1.5f);
        }

        RiftEntity riftEntity = AITEntityTypes.RIFT_ENTITY.create(world);

        // if (riftEntity == null) return;

        world.spawnEntity(riftEntity);

        riftEntity.setPos(pos.getX(), pos.getY(), pos.getZ());

        // super.onPlaced(world, pos, state, placer, itemStack);
    }

    private static boolean isConsumable(RiftChunkManager manager, ChunkPos pos) {
        return manager.isRiftChunk(pos)/* && manager.getArtron(pos) >= 250*/;
    }
}
