package dev.amble.ait.core.blocks;

import dev.amble.ait.core.blockentities.WaypointBankBlockEntity;
import dev.amble.ait.core.blocks.types.HorizontalDirectionalBlock;
import dev.amble.ait.core.util.WorldUtil;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

@SuppressWarnings("deprecation")
public class WaypointBankBlock extends HorizontalDirectionalBlock implements BlockEntityProvider {

    public static final EnumProperty<DoubleBlockHalf> HALF = Properties.DOUBLE_BLOCK_HALF;

    public static final int MAX_COUNT = 16;

    public WaypointBankBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.getStateManager().getDefaultState().with(FACING, Direction.NORTH).with(HALF,
                DoubleBlockHalf.LOWER));
    }

    private static int getSlotForHitPos(Vec2f hitPos, DoubleBlockHalf half) {
        int column = hitPos.x >= 0.5f ? 1 : 0;

        if (half == DoubleBlockHalf.UPPER)
            return column * (MAX_COUNT / 2);

        int row = (int) ((1 - hitPos.y) * MAX_COUNT / 2);
        row = MathHelper.clamp(row, 0, 6);

        return row + column * (MAX_COUNT / 2) + 1;
    }

    private static Optional<Vec2f> getHitPos(BlockHitResult hit, Direction facing) {
        Direction direction = hit.getSide();

        if (facing != direction)
            return Optional.empty();

        BlockPos blockPos = hit.getBlockPos().offset(direction);
        Vec3d vec3d = hit.getPos().subtract(blockPos.getX(), blockPos.getY(), blockPos.getZ());

        double x = vec3d.getX();
        double y = vec3d.getY();
        double z = vec3d.getZ();

        return switch (direction) {
            case NORTH -> Optional.of(new Vec2f((float) (1.0 - x), (float) y));
            case SOUTH -> Optional.of(new Vec2f((float) x, (float) y));
            case WEST -> Optional.of(new Vec2f((float) z, (float) y));
            case EAST -> Optional.of(new Vec2f((float) (1.0 - z), (float) y));
            case DOWN, UP -> Optional.empty();
        };
    }

    @Override
    public void onBlockBreakStart(BlockState state, World world, BlockPos pos, PlayerEntity player) {
        if (world.getBlockEntity(pos) instanceof WaypointBankBlockEntity bank)
            bank.unselect();
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand,
            BlockHitResult hit) {
        Optional<Vec2f> hitPos = getHitPos(hit, state.get(HorizontalFacingBlock.FACING));

        if (hitPos.isEmpty())
            return ActionResult.PASS;

        if (state.get(HALF) == DoubleBlockHalf.UPPER)
            pos = pos.down();

        if (!(world.getBlockEntity(pos) instanceof WaypointBankBlockEntity bank))
            return ActionResult.PASS;

        int slot = getSlotForHitPos(hitPos.get(), state.get(HALF));
        return bank.onUse(world, state, player, hand, slot);
    }

    @Override
    public long getRenderingSeed(BlockState state, BlockPos pos) {
        return MathHelper.hashCode(pos.getX(), pos.down(state.get(HALF) == DoubleBlockHalf.LOWER ? 0 : 1).getY(),
                pos.getZ());
    }

    @Nullable @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        if (state.get(HALF) == DoubleBlockHalf.UPPER)
            return null;

        return new WaypointBankBlockEntity(pos, state);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, HALF);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
        world.setBlockState(pos.up(), state.with(HALF, DoubleBlockHalf.UPPER), Block.NOTIFY_ALL);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState,
            WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        DoubleBlockHalf half = state.get(HALF);

        if (direction.getAxis() == Direction.Axis.Y && half == DoubleBlockHalf.LOWER == (direction == Direction.UP))
            return neighborState.isOf(this) && neighborState.get(HALF) != half
                    ? state.with(FACING, neighborState.get(FACING))
                    : Blocks.AIR.getDefaultState();

        return half == DoubleBlockHalf.LOWER && direction == Direction.DOWN && !state.canPlaceAt(world, pos)
                ? Blocks.AIR.getDefaultState()
                : super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    @Override
    @Nullable public BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockPos blockPos = ctx.getBlockPos();
        World world = ctx.getWorld();

        if (blockPos.getY() < world.getTopY() - 1 && world.getBlockState(blockPos.up()).canReplace(ctx))
            return super.getPlacementState(ctx).with(HALF, DoubleBlockHalf.LOWER);

        return null;
    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (!world.isClient() && player.isCreative())
            WorldUtil.onBreakHalfInCreative(world, pos, state, player);

        super.onBreak(world, pos, state, player);
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.isOf(newState.getBlock()))
            return;

        if (world.getBlockEntity(pos) instanceof WaypointBankBlockEntity bank) {
            bank.dropItems();
            world.updateComparators(pos, this);
        }

        super.onStateReplaced(state, world, pos, newState, moved);
    }
}
