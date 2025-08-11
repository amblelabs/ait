package dev.amble.ait.core.blocks.decoration;

import dev.amble.ait.core.AITBlockEntityTypes;
import dev.amble.ait.core.blockentities.decoration.AITJukeboxBlockEntity;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.JukeboxBlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MusicDiscItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class AITJukeboxBlock extends BlockWithEntity {
    public static final BooleanProperty HAS_RECORD = Properties.HAS_RECORD;
    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;

    protected static final VoxelShape SHAPE_NORTH = Block.createCuboidShape(
            0.0, 0.0, 8.0,
            16.0, 25.0, 16.0
    );

    protected static final VoxelShape SHAPE_SOUTH = Block.createCuboidShape(
            0.0, 0.0, 0.0,
            16.0, 25.0, 8.0
    );

    protected static final VoxelShape SHAPE_EAST = Block.createCuboidShape(
            0.0, 0.0, 0.0,
            8.0, 25.0, 16.0
    );

    protected static final VoxelShape SHAPE_WEST = Block.createCuboidShape(
            8.0, 0.0, 0.0,
            16.0, 25.0, 16.0
    );


    public AITJukeboxBlock(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState()
                .with(HAS_RECORD, false)
                .with(FACING, Direction.NORTH));
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState()
                .with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return switch (state.get(FACING)) {
            case NORTH -> SHAPE_NORTH;
            case SOUTH -> SHAPE_SOUTH;
            case EAST -> SHAPE_EAST;
            case WEST -> SHAPE_WEST;
            default -> SHAPE_NORTH;
        };
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return getCollisionShape(state, world, pos, context);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(HAS_RECORD, FACING);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);
        NbtCompound nbtCompound = BlockItem.getBlockEntityNbt(itemStack);
        if (nbtCompound != null && nbtCompound.contains("RecordItem")) {
            world.setBlockState(pos, state.with(HAS_RECORD, true), Block.NOTIFY_LISTENERS);
        }
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (state.get(HAS_RECORD) && world.getBlockEntity(pos) instanceof AITJukeboxBlockEntity jukeboxBlockEntity) {
            jukeboxBlockEntity.dropRecord();
            return ActionResult.success(world.isClient);
        }
        return ActionResult.PASS;
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (!state.isOf(newState.getBlock())) {
            if (world.getBlockEntity(pos) instanceof AITJukeboxBlockEntity jukeboxBlockEntity) {
                jukeboxBlockEntity.dropRecord();
            }
            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new AITJukeboxBlockEntity(pos, state);
    }

    @Override
    public boolean emitsRedstonePower(BlockState state) {
        return true;
    }

    @Override
    public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        if (world.getBlockEntity(pos) instanceof AITJukeboxBlockEntity jukeboxBlockEntity && jukeboxBlockEntity.isPlayingRecord()) {
            return 15;
        }
        return 0;
    }

    @Override
    public boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    @Override
    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        if (world.getBlockEntity(pos) instanceof JukeboxBlockEntity jukeboxBlockEntity
                && jukeboxBlockEntity.getStack().getItem() instanceof MusicDiscItem musicDiscItem) {
            return musicDiscItem.getComparatorOutput();
        }
        return 0;
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return state.get(HAS_RECORD) ? checkType(type, AITBlockEntityTypes.JUKEBOX, JukeboxBlockEntity::tick) : null;
    }
}
