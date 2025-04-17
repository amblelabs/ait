package dev.amble.ait.core.blocks;

import java.util.function.ToIntFunction;

import org.jetbrains.annotations.Nullable;

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import dev.amble.ait.core.AITBlockEntityTypes;
import dev.amble.ait.core.blockentities.RoundelBlockEntity;

public abstract class AbstractRoundelBlock
        extends Block implements BlockEntityProvider {
    public static final IntProperty LEVEL_15 = Properties.LEVEL_15;
    public static final ToIntFunction<BlockState> STATE_TO_LUMINANCE = state -> state.get(LEVEL_15);
    private final DyeColor color;

    protected AbstractRoundelBlock(DyeColor color, AbstractBlock.Settings settings) {
        super(settings);
        this.color = color;
        this.setDefaultState(this.stateManager.getDefaultState().with(LEVEL_15, 11));
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
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
            world.setBlockState(pos, state, Block.REDRAW_ON_MAIN_THREAD);
        } else if (itemStack.hasCustomName()) {
            world.getBlockEntity(pos, AITBlockEntityTypes.ROUNDEL_BLOCK_ENTITY_TYPE).ifPresent(blockEntity -> blockEntity.setCustomName(itemStack.getName()));
        }
        if (world.isClient) return;
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof RoundelBlockEntity roundelBlockEntity) {
            if (roundelBlockEntity.tardis() != null && roundelBlockEntity.tardis().get() != null) {
                int lightOf = roundelBlockEntity.tardis().get().fuel().hasPower() ? 11 : 0;
                world.setBlockState(pos, state.with(LEVEL_15, lightOf), Block.NOTIFY_LISTENERS | Block.REDRAW_ON_MAIN_THREAD);
            } else {
                world.setBlockState(pos, state.with(LEVEL_15, 11), Block.NOTIFY_LISTENERS | Block.REDRAW_ON_MAIN_THREAD);
            }
            roundelBlockEntity.markDirty();
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

    @Nullable @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(LEVEL_15, 11);
    }

    public DyeColor getColor() {
        return this.color;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(LEVEL_15);
    }
}
