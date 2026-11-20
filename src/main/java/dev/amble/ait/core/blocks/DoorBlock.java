package dev.amble.ait.core.blocks;

import java.util.EnumMap;
import java.util.Map;

import dev.amble.ait.core.AITBlockEntityTypes;
import dev.amble.ait.core.blockentities.DoorBlockEntity;
import dev.amble.ait.core.blocks.types.HorizontalDirectionalBlock;
import dev.amble.ait.core.util.ShapeUtil;
import dev.amble.ait.data.ShapeMap;
import org.jetbrains.annotations.Nullable;

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

@SuppressWarnings("deprecation")
public class DoorBlock extends HorizontalDirectionalBlock implements BlockEntityProvider, Waterloggable {

    public static final VoxelShape NORTH_SHAPE = Block.createCuboidShape(0.0, 0.0, 12.1, 16.0, 32.0, 16.0);
    private static final ShapeMap SHAPES = ShapeUtil.rotations(Direction.NORTH, NORTH_SHAPE).build();
    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
    public static final IntProperty LEVEL_4 = ExteriorBlock.LEVEL_4;
    public static final EnumProperty<Shift> SHIFT = EnumProperty.of("shift", Shift.class);
    private static final Map<Shift, Map<Direction, VoxelShape>> SHIFTED_SHAPES = new EnumMap<>(Shift.class);

    static {
        for (Shift shift : Shift.values()) {
            Map<Direction, VoxelShape> shapes = new EnumMap<>(Direction.class);

            for (Direction facing : Direction.Type.HORIZONTAL) {
                Vec3d offset = shift.offset(facing);
                shapes.put(facing, SHAPES.get(facing).offset(offset.x, offset.y, offset.z));
            }

            SHIFTED_SHAPES.put(shift, shapes);
        }
    }

    public enum Shift implements StringIdentifiable {
        NONE("none", 0),
        LEFT("left", -0.5),
        RIGHT("right", 0.5);

        private final String name;
        private final double amount;

        Shift(String name, double amount) {
            this.name = name;
            this.amount = amount;
        }

        public Vec3d offset(Direction facing) {
            Direction side = facing.rotateYClockwise();
            return new Vec3d(side.getOffsetX() * this.amount, 0, side.getOffsetZ() * this.amount);
        }

        @Override
        public String asString() {
            return this.name;
        }
    }

    public static Vec3d shift(BlockState state) {
        if (!state.contains(SHIFT) || !state.contains(FACING))
            return Vec3d.ZERO;

        return state.get(SHIFT).offset(state.get(FACING));
    }

    @Override
    public float getBlastResistance() {
        return 10000f;
    }

    public DoorBlock(Settings settings) {
        super(settings);

        this.setDefaultState(this.getStateManager().getDefaultState()
                .with(FACING, Direction.NORTH)
                .with(WATERLOGGED, false)
                .with(LEVEL_4, 0)
                .with(SHIFT, Shift.NONE));
    }

    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState,
            WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (state.get(WATERLOGGED))
            world.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));

        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        if (world.getBlockEntity(pos) instanceof DoorBlockEntity door && door.isLinked() &&
                door.tardis().get().siege() != null && door.tardis().get().siege().isActive())
            return VoxelShapes.empty();

        return SHIFTED_SHAPES.get(state.get(SHIFT)).get(state.get(FACING));
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer,
            ItemStack itemStack) {

        super.onPlaced(world, pos, state, placer, itemStack);
    }

    @Override
    public boolean isShapeFullCube(BlockState state, BlockView world, BlockPos pos) {
        return false;
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand,
            BlockHitResult hit) {
        if (world.isClient())
            return ActionResult.SUCCESS;

        if (world.getBlockEntity(pos) instanceof DoorBlockEntity door)
            door.useOn(world, player.isSneaking(), player);

        return ActionResult.CONSUME;
    }

    @Nullable @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state,
            BlockEntityType<T> type) {
        return type == AITBlockEntityTypes.DOOR_BLOCK_ENTITY_TYPE ? DoorBlockEntity::tick : null;
    }

    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        if (world.isClient())
            return;

        if (!(world.getBlockEntity(pos) instanceof DoorBlockEntity door))
            return;

        if (!door.isLinked())
            return;

        if (door.tardis().get().siege().isActive())
            return;

//        if (door.tardis().get().stats().getYScale() == 0)
//            return;

        if (reaches(entity, this.getOutlineShape(state, world, pos, ShapeContext.of(entity)).getBoundingBox().offset(pos)))
            door.onEntityCollision(entity);
    }

    public static boolean reaches(Entity entity, Box door) {
        Vec3d expansionBehind = new Vec3d(entity.prevX, entity.prevY, entity.prevZ).subtract(entity.getPos());
        Vec3d expansionForward = entity.getVelocity();

        Box entityBox = entity.getBoundingBox().stretch(expansionForward.multiply(1.2)).stretch(expansionBehind);

        double insideBlockExpanded = 1.0E-7D;

        Box biggerEntityBox = entityBox.expand(insideBlockExpanded);
        Box biggerDoorShape = door.expand(insideBlockExpanded);

        return biggerEntityBox.intersects(biggerDoorShape);
    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        super.onBreak(world, pos, state, player);

        if (world.isClient())
            return;

        if (!(world.getBlockEntity(pos) instanceof DoorBlockEntity door))
            return;

        door.onBreak();
    }

    @Nullable @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        FluidState fluidState = ctx.getWorld().getFluidState(ctx.getBlockPos());
        Direction facing = ctx.getHorizontalPlayerFacing().getOpposite();

        return this.getDefaultState().with(FACING, facing).with(WATERLOGGED,
                fluidState.getFluid() == Fluids.WATER).with(SHIFT, placementShift(ctx, facing));
    }

    private static Shift placementShift(ItemPlacementContext ctx, Direction facing) {
        if (ctx.getPlayer() == null || !ctx.getPlayer().isSneaking())
            return Shift.NONE;

        Direction side = facing.rotateYClockwise();
        Vec3d fromCenter = ctx.getHitPos().subtract(ctx.getBlockPos().toCenterPos());
        double along = fromCenter.x * side.getOffsetX() + fromCenter.z * side.getOffsetZ();

        return along < 0 ? Shift.LEFT : Shift.RIGHT;
    }

    @Nullable @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new DoorBlockEntity(pos, state);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, WATERLOGGED, LEVEL_4, SHIFT);
    }

    public FluidState getFluidState(BlockState state) {
        return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
    }

    public boolean isTransparent(BlockState state, BlockView world, BlockPos pos) {
        return !(Boolean) state.get(WATERLOGGED);
    }
}
