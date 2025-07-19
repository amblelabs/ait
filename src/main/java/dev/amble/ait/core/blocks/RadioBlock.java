package dev.amble.ait.core.blocks;

import org.jetbrains.annotations.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import dev.amble.ait.core.AITBlockEntityTypes;
import dev.amble.ait.core.blockentities.AITRadioBlockEntity;
import dev.amble.ait.core.blocks.types.HorizontalDirectionalBlock;

public class RadioBlock extends HorizontalDirectionalBlock implements BlockEntityProvider {

    public static final VoxelShape X_AXIS_RADIO = Block.createCuboidShape(2.5, 0.0, 0.0, 13.5, 12.0, 16.0);
    public static final VoxelShape PX_AXIS_TUNER = Block.createCuboidShape(1.5, 2.5, 11.5, 3.5, 4.5, 13.5);
    public static final VoxelShape PX_AXIS_VOLUME = Block.createCuboidShape(1.5, 2.5, 2.5, 3.5, 4.5, 4.5);
    public static final VoxelShape NX_AXIS_TUNER = Block.createCuboidShape(12.5, 2.5, 2.5, 14.5, 4.5, 4.5);
    public static final VoxelShape NX_AXIS_VOLUME = Block.createCuboidShape(12.5, 2.5, 11.5, 14.5, 4.5, 13.5);

    // -------------------------------------------------------------------------------------------------------------------------------------------
    public static final VoxelShape Z_AXIS_RADIO = Block.createCuboidShape(0.0, 0.0, 2.5, 16, 12.0, 13.5);
    public static final VoxelShape PZ_AXIS_TUNER = Block.createCuboidShape(2.5, 2.5, 1.5, 4.5, 4.5, 3.5);
    public static final VoxelShape PZ_AXIS_VOLUME = Block.createCuboidShape(11.5, 2.5, 1.5, 13.5, 4.5, 3.5);
    public static final VoxelShape NZ_AXIS_TUNER = Block.createCuboidShape(11.5, 2.5, 12.5, 13.5, 4.5, 14.5);
    public static final VoxelShape NZ_AXIS_VOLUME = Block.createCuboidShape(2.5, 2.5, 12.5, 4.5, 4.5, 14.5);
    private static final VoxelShape PX_AXIS_SHAPE = VoxelShapes.union(X_AXIS_RADIO, PX_AXIS_TUNER, PX_AXIS_VOLUME);
    private static final VoxelShape PZ_AXIS_SHAPE = VoxelShapes.union(Z_AXIS_RADIO, PZ_AXIS_TUNER, PZ_AXIS_VOLUME);
    private static final VoxelShape NX_AXIS_SHAPE = VoxelShapes.union(X_AXIS_RADIO, NX_AXIS_TUNER, NX_AXIS_VOLUME);
    private static final VoxelShape NZ_AXIS_SHAPE = VoxelShapes.union(Z_AXIS_RADIO, NZ_AXIS_TUNER, NZ_AXIS_VOLUME);

    private VoxelShape shape;

    public RadioBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH));
        this.shape = VoxelShapes.empty();
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        Direction direction = state.get(FACING);
        if (direction.getAxis() == Direction.Axis.X) {
            if (direction == Direction.WEST) {
                this.shape = PX_AXIS_SHAPE;
            } else {
                this.shape = NX_AXIS_SHAPE;
            }
        }
        if (direction.getAxis() == Direction.Axis.Z) {
            if (direction == Direction.NORTH) {
                this.shape = PZ_AXIS_SHAPE;
            } else {
                this.shape = NZ_AXIS_SHAPE;
            }
        }
        return this.shape;
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand,
            BlockHitResult hit) {
        if (world.isClient) {
            return ActionResult.SUCCESS;
        }
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof AITRadioBlockEntity radioBlockEntity) {
            radioBlockEntity.useOn(hit, state, player, world, player.isSneaking());
        }
        return ActionResult.CONSUME;
    }

    @Nullable protected static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> checkType(
            BlockEntityType<A> givenType, BlockEntityType<E> expectedType, BlockEntityTicker<? super E> ticker) {
        return expectedType == givenType ? (BlockEntityTicker<A>) ticker : null;
    }

    @Nullable @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state,
            BlockEntityType<T> type) {
        return checkType(type, AITBlockEntityTypes.AIT_RADIO_BLOCK_ENTITY_TYPE,
                (world1, pos, state1, be) -> AITRadioBlockEntity.tick(world1, pos, state1, be));
    }

    @Nullable @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new AITRadioBlockEntity(pos, state);
    }

    @Override
    public BlockState getAppearance(BlockState state, BlockRenderView renderView, BlockPos pos, Direction side,
            @Nullable BlockState sourceState, @Nullable BlockPos sourcePos) {
        return super.getAppearance(state, renderView, pos, side, sourceState, sourcePos);
    }
}
