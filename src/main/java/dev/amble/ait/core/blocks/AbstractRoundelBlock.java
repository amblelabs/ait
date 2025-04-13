package dev.amble.ait.core.blocks;

import org.jetbrains.annotations.Nullable;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import dev.amble.ait.core.AITBlockEntityTypes;
import dev.amble.ait.core.blockentities.RoundelBlockEntity;

public abstract class AbstractRoundelBlock
        extends BlockWithEntity {
    private final DyeColor color;

    protected AbstractRoundelBlock(DyeColor color, AbstractBlock.Settings settings) {
        super(settings);
        this.color = color;
    }

    @Override
    public boolean canMobSpawnInside(BlockState state) {
        return false;
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new RoundelBlockEntity(pos, state, this.color);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        if (world.isClient) {
            world.getBlockEntity(pos, AITBlockEntityTypes.ROUNDEL_BLOCK_ENTITY_TYPE).ifPresent(blockEntity -> blockEntity.readFrom(itemStack));
        } else if (itemStack.hasCustomName()) {
            world.getBlockEntity(pos, AITBlockEntityTypes.ROUNDEL_BLOCK_ENTITY_TYPE).ifPresent(blockEntity -> blockEntity.setCustomName(itemStack.getName()));
        }
    }

    @Override
    public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof RoundelBlockEntity) {
            return ((RoundelBlockEntity)blockEntity).getPickStack();
        }
        return super.getPickStack(world, pos, state);
    }

    public DyeColor getColor() {
        return this.color;
    }
}
