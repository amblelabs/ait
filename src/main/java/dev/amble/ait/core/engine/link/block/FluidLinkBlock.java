package dev.amble.ait.core.engine.link.block;

import dev.amble.ait.core.engine.link.IFluidLink;
import dev.amble.ait.core.engine.link.IFluidSource;
import dev.amble.ait.core.world.TardisServerWorld;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public abstract class FluidLinkBlock extends BlockWithEntity implements IFluidLink {
    public FluidLinkBlock(Settings settings) {
        super(settings);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);

        if (!TardisServerWorld.isTardisDimension(world)) return;

        if (world.getBlockEntity(pos) instanceof FluidLinkBlockEntity be) {
            be.onPlaced(world, pos, placer);
        }
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock()) { // on break
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof FluidLinkBlockEntity be) {
                be.markRemoved();
                be.onBroken(world, pos);
            }
        }

        super.onStateReplaced(state, world, pos, newState, moved);
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        super.neighborUpdate(state, world, pos, sourceBlock, sourcePos, notify);

        if (world.getBlockEntity(pos) instanceof FluidLinkBlockEntity be) {
            be.onNeighborUpdate(world, pos, sourceBlock, sourcePos);
        }
    }

    @Override
    public abstract FluidLinkBlockEntity createBlockEntity(BlockPos pos, BlockState state);

    @Override
    public IFluidSource source(boolean search) {
        throw new UnsupportedOperationException("FluidLinkBlock does not support this operation, did you mean to use FluidLinkBlockEntity?");
    }

    @Override
    public void setSource(IFluidSource source) {
        throw new UnsupportedOperationException("FluidLinkBlock does not support this operation, did you mean to use FluidLinkBlockEntity?");
    }

    @Override
    public IFluidLink last() {
        throw new UnsupportedOperationException("FluidLinkBlock does not support this operation, did you mean to use FluidLinkBlockEntity?");
    }

    @Override
    public void setLast(IFluidLink last) {
        throw new UnsupportedOperationException("FluidLinkBlock does not support this operation, did you mean to use FluidLinkBlockEntity?");
    }
}
