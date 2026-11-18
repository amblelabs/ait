package dev.amble.ait.core.blocks;

import dev.amble.ait.core.AITBlockEntityTypes;
import dev.amble.ait.core.blockentities.RotaryPhoneBlockEntity;
import dev.amble.ait.core.tardis.comms.RotaryPhones;
import org.jetbrains.annotations.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class RotaryPhoneBlock extends BlockWithEntity {

    public static final int ROTATIONS = 8;
    public static final IntProperty ROTATION = IntProperty.of("rotation", 0, ROTATIONS - 1);

    private static final VoxelShape SHAPE = Block.createCuboidShape(2, 0, 2, 14, 8, 14);

    public RotaryPhoneBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.getDefaultState().with(ROTATION, 0));
    }

    public static float degrees(BlockState state) {
        return state.get(ROTATION) * (360f / ROTATIONS);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(ROTATION);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        int rotation = MathHelper.floor((180f + ctx.getPlayerYaw()) * ROTATIONS / 360f + 0.5) & (ROTATIONS - 1);
        return this.getDefaultState().with(ROTATION, rotation);
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(ROTATION, rotation.rotate(state.get(ROTATION), ROTATIONS));
    }

    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.with(ROTATION, mirror.mirror(state.get(ROTATION), ROTATIONS));
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (hand != Hand.MAIN_HAND)
            return ActionResult.PASS;

        if (world.isClient())
            return ActionResult.SUCCESS;

        if (player instanceof ServerPlayerEntity serverPlayer && world.getBlockEntity(pos) instanceof RotaryPhoneBlockEntity phone)
            RotaryPhones.use(serverPlayer, (ServerWorld) world, phone);

        return ActionResult.CONSUME;
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (!state.isOf(newState.getBlock()) && world instanceof ServerWorld serverWorld
                && world.getBlockEntity(pos) instanceof RotaryPhoneBlockEntity phone)
            RotaryPhones.removed(serverWorld, pos, phone.line());

        super.onStateReplaced(state, world, pos, newState, moved);
    }

    @Nullable @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new RotaryPhoneBlockEntity(pos, state);
    }

    @Nullable @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return world.isClient() ? checkType(type, AITBlockEntityTypes.ROTARY_PHONE_BLOCK_ENTITY_TYPE, RotaryPhoneBlockEntity::tick) : null;
    }
}
