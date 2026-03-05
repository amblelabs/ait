package dev.amble.ait.core.blocks;

import dev.amble.ait.core.AITEntityTypes;
import dev.amble.ait.core.entities.RiftEntity;
import dev.drtheo.queue.api.ActionQueue;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

/**
 * @author Loqor
 * The purpose of this block is to rip open rifts in rift chunks as opposed to the silly entities.
 * It mimics the Lodestone block from Doctor Who lore as opposed to the usual Lodestone in Minecraft.
 * */
public class LodestoneBlock extends Block {
    public LodestoneBlock(Settings settings) {
        super(settings);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);
        RiftEntity riftEntity = AITEntityTypes.RIFT_ENTITY.create(world);
        if (riftEntity == null) return;
        riftEntity.setPos(pos.getX(), pos.getY(), pos.getZ());
        world.spawnEntity(riftEntity);
    }
}
