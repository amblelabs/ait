package dev.amble.ait.core.blocks;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

import dev.amble.ait.core.AITBlockEntityTypes;
import dev.amble.ait.core.blockentities.EngineCoreBlockEntity;
import dev.amble.ait.core.world.TardisServerWorld;

@SuppressWarnings("deprecation")
public class EngineCoreBlock extends BlockWithEntity implements Waterloggable {

    public static final BooleanProperty WATERLOGGED;
    protected static final VoxelShape SHAPE;

    public EngineCoreBlock(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(WATERLOGGED, true));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(WATERLOGGED);
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new EngineCoreBlockEntity(pos, state);
    }

    @Nullable public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state,
            BlockEntityType<T> type) {
        return checkType(type, AITBlockEntityTypes.ENGINE_CORE_BLOCK_ENTITY_TYPE,
                world.isClient ? EngineCoreBlockEntity::clientTick : EngineCoreBlockEntity::serverTick);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState,
            WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (state.get(WATERLOGGED))
            world.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));

        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer,
            ItemStack itemStack) {
        if (!(world.isClient()) && !TardisServerWorld.isTardisDimension(world)) {
            world.breakBlock(pos, !((PlayerEntity) placer).isCreative());
            return;
        }

        super.onPlaced(world, pos, state, placer, itemStack);
    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (world.getBlockEntity(pos) instanceof EngineCoreBlockEntity engineCoreBlockEntity)
            engineCoreBlockEntity.onBreak(world, pos, state, player);

        super.onBreak(world, pos, state, player);
    }

    @Nullable public BlockState getPlacementState(ItemPlacementContext ctx) {
        FluidState fluidState = ctx.getWorld().getFluidState(ctx.getBlockPos());
        return this.getDefaultState().with(WATERLOGGED, fluidState.isIn(FluidTags.WATER) && fluidState.getLevel() == 8);
    }

    @Override
    public boolean canPathfindThrough(BlockState state, BlockView world, BlockPos pos, NavigationType type) {
        return false;
    }

    static {
        WATERLOGGED = Properties.WATERLOGGED;
        SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 16.0, 16.0);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable BlockView world, List<Text> tooltip, TooltipContext options) {
        super.appendTooltip(stack, world, tooltip, options);

        tooltip.add(Text.translatable("tooltip.ait.singularity").formatted(Formatting.DARK_GRAY, Formatting.ITALIC));
    }
}
