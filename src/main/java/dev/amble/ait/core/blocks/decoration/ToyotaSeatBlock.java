package dev.amble.ait.core.blocks.decoration;

import dev.amble.ait.core.AITEntityTypes;
import dev.amble.ait.core.blockentities.decoration.CoralSeatBlockEntity;
import dev.amble.ait.core.blockentities.decoration.ToyotaSeatBlockEntity;
import dev.amble.ait.core.entities.SeatEntity;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.RotationPropertyHelper;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ToyotaSeatBlock extends BlockWithEntity {

    public static final int MAX_ROTATION_INDEX = RotationPropertyHelper.getMax();
    private static final int MAX_ROTATIONS = MAX_ROTATION_INDEX + 1;
    public static final IntProperty ROTATION = SkullBlock.ROTATION;

    protected static final VoxelShape Y_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 11.0, 16.0);

    public ToyotaSeatBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(ROTATION, 0));
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return Y_SHAPE;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return Y_SHAPE;
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(ROTATION, RotationPropertyHelper.fromYaw(ctx.getPlayerYaw()));
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(ROTATION, rotation.rotate(state.get(ROTATION), MAX_ROTATIONS));
    }

    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.with(ROTATION, mirror.mirror(state.get(ROTATION), MAX_ROTATIONS));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(ROTATION);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos,
                              PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient) {
            if (player.hasVehicle()) {
                return ActionResult.SUCCESS;
            }

            List<SeatEntity> seats = world.getEntitiesByClass(
                    SeatEntity.class,
                    new Box(pos).expand(0.1),
                    seat -> true
            );
            if (!seats.isEmpty()) {
                return ActionResult.SUCCESS;
            }

            int rotIndex = state.get(ROTATION);
            float yaw = RotationPropertyHelper.toDegrees(rotIndex);

            SeatEntity seat = new SeatEntity(AITEntityTypes.SEAT, world);
            seat.refreshPositionAndAngles(
                    pos.getX() + 0.5,
                    pos.getY() - 0.1,
                    pos.getZ() + 0.5,
                    yaw, 0
            );
            world.spawnEntity(seat);
            player.startRiding(seat, false);
        }
        return ActionResult.SUCCESS;
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock()) {
            if (!world.isClient) {
                List<SeatEntity> seats = world.getEntitiesByClass(
                        SeatEntity.class,
                        new Box(pos).expand(0.1),
                        seat -> true
                );
                for (SeatEntity seat : seats) {
                    seat.discard();
                }
            }
            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }


    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new ToyotaSeatBlockEntity(pos, state);
    }
}
