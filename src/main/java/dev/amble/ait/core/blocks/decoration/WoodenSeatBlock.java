package dev.amble.ait.core.blocks.decoration;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.RotationPropertyHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class WoodenSeatBlock extends BlockWithEntity {

    public static final int MAX_ROTATION_INDEX = RotationPropertyHelper.getMax();
    private static final int MAX_ROTATIONS = MAX_ROTATION_INDEX + 1;
    public static final IntProperty ROTATION = Properties.ROTATION;

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos,
                              PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient) {
            if (player.hasVehicle()) {
                return ActionResult.SUCCESS;
            }

            int rotIndex = state.get(ROTATION);
            float yaw = RotationPropertyHelper.toDegrees(rotIndex);

            ArmorStandEntity seat = new ArmorStandEntity(world,
                    pos.getX() + 0.5,
                    pos.getY(),
                    pos.getZ() + 0.5);
            seat.setInvisible(true);
            seat.setNoGravity(true);
            seat.setYaw(yaw);
            seat.setCustomName(Text.literal("seat_temp"));
            seat.setCustomNameVisible(false);

            world.spawnEntity(seat);
            player.startRiding(seat, false);
        }
        return ActionResult.SUCCESS;
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock()) {
            if (!world.isClient) {
                // Remove any temporary seat ArmorStands at this position
                List<ArmorStandEntity> stands = world.getEntitiesByClass(
                        ArmorStandEntity.class,
                        new Box(pos).expand(0.5),
                        stand -> stand.hasCustomName() && "seat_temp".equals(stand.getName().getString())
                );
                for (ArmorStandEntity stand : stands) {
                    stand.discard();
                }
            }
            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }



    protected WoodenSeatBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(ROTATION, 0));
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

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return null;
    }
}
