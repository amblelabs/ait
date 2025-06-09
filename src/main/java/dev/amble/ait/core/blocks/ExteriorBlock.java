package dev.amble.ait.core.blocks;

import dev.amble.ait.api.tardis.TardisComponent;
import dev.amble.ait.compat.DependencyChecker;
import dev.amble.ait.core.AITBlocks;
import dev.amble.ait.core.AITItems;
import dev.amble.ait.core.blockentities.ExteriorBlockEntity;
import dev.amble.ait.core.tardis.Tardis;
import dev.amble.ait.core.tardis.handler.BiomeHandler;
import dev.amble.ait.core.tardis.handler.travel.TravelHandler;
import dev.amble.ait.core.tardis.handler.travel.TravelHandlerBase;
import dev.amble.ait.core.util.ShapeUtil;
import dev.amble.ait.data.schema.exterior.variant.adaptive.AdaptiveVariant;
import dev.amble.ait.module.planet.core.space.planet.Planet;
import dev.amble.ait.module.planet.core.space.planet.PlanetRegistry;
import dev.amble.ait.registry.impl.exterior.ExteriorVariantRegistry;
import dev.amble.lib.api.ICantBreak;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.util.ParticleUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationPropertyHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.ToIntFunction;

@SuppressWarnings("deprecation")
public class ExteriorBlock extends Block implements BlockEntityProvider, ICantBreak, Waterloggable {
    public static final byte MAX_ROTATION_INDEX = (byte) RotationPropertyHelper.getMax();
    private static final int MAX_ROTATIONS = MAX_ROTATION_INDEX + 1;
    public static final IntProperty ROTATION = Properties.ROTATION;
    public static final IntProperty LEVEL_4 = Properties.LEVEL_15;
    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
    public static final ToIntFunction<BlockState> STATE_TO_LUMINANCE = state -> state.get(LEVEL_4);
    public static final VoxelShape LEDGE_DOOM = Block.createCuboidShape(0, 0, -3.5, 16, 1, 16);
    public static final VoxelShape CUBE_NORTH_SHAPE = VoxelShapes.union(
            Block.createCuboidShape(0.0, 0.0, 5.0, 16.0, 32.0, 16.0), Block.createCuboidShape(0, 0, -3.5, 16, 1, 16));
    public static final VoxelShape PORTALS_SHAPE = VoxelShapes.union(
            Block.createCuboidShape(0.0, 0.0, 11.0, 16.0, 32.0, 16.0), Block.createCuboidShape(0, 0, -3.5, 16, 1, 16));

    public static final VoxelShape PORTALS_SHAPE_DIAGONAL = VoxelShapes.union(
            Block.createCuboidShape(11.0, 0.0, 11.0, 16.0, 32.0, 16.0), Block.createCuboidShape(0, 0, -3.5, 16, 1, 16));
    public static final VoxelShape SIEGE_SHAPE = Block.createCuboidShape(4.0, 0.0, 4.0, 12.0, 8.0, 12.0);
    public static final VoxelShape DIAGONAL_SHAPE;

    static {
        VoxelShape shape = VoxelShapes.empty();
        shape = VoxelShapes.combineAndSimplify(shape, VoxelShapes.cuboid(-0.125, 0, -0.125, 0.875, 0.0625, 0.875),
                BooleanBiFunction.OR);
        shape = VoxelShapes.combineAndSimplify(shape, VoxelShapes.cuboid(0.25, 0.0625, 0.25, 0.875, 2, 0.875),
                BooleanBiFunction.OR);
        shape = VoxelShapes.combineAndSimplify(shape, VoxelShapes.cuboid(0.3125, 0.0625, 0.1875, 0.875, 2, 0.25),
                BooleanBiFunction.OR);
        shape = VoxelShapes.combineAndSimplify(shape, VoxelShapes.cuboid(0.1875, 0.0625, 0.3125, 0.25, 2, 0.875),
                BooleanBiFunction.OR);
        shape = VoxelShapes.combineAndSimplify(shape, VoxelShapes.cuboid(0.125, 0.0625, 0.375, 0.1875, 2, 0.875),
                BooleanBiFunction.OR);
        shape = VoxelShapes.combineAndSimplify(shape, VoxelShapes.cuboid(0.4375, 0.0625, 0.0625, 0.875, 2, 0.125),
                BooleanBiFunction.OR);
        shape = VoxelShapes.combineAndSimplify(shape, VoxelShapes.cuboid(0.375, 0.0625, 0.125, 0.875, 2, 0.1875),
                BooleanBiFunction.OR);
        shape = VoxelShapes.combineAndSimplify(shape, VoxelShapes.cuboid(0.5625, 0.0625, -0.0625, 0.875, 2, 0),
                BooleanBiFunction.OR);
        shape = VoxelShapes.combineAndSimplify(shape, VoxelShapes.cuboid(0.5, 0.0625, 0, 0.875, 2, 0.0625),
                BooleanBiFunction.OR);
        shape = VoxelShapes.combineAndSimplify(shape, VoxelShapes.cuboid(0.625, 0.0625, -0.125, 0.875, 2, -0.0625),
                BooleanBiFunction.OR);
        shape = VoxelShapes.combineAndSimplify(shape, VoxelShapes.cuboid(0.0625, 0.0625, 0.4375, 0.125, 2, 0.875),
                BooleanBiFunction.OR);
        shape = VoxelShapes.combineAndSimplify(shape, VoxelShapes.cuboid(0, 0.0625, 0.5, 0.0625, 2, 0.875),
                BooleanBiFunction.OR);
        shape = VoxelShapes.combineAndSimplify(shape, VoxelShapes.cuboid(-0.0625, 0.0625, 0.5625, 0, 2, 0.875),
                BooleanBiFunction.OR);
        shape = VoxelShapes.combineAndSimplify(shape, VoxelShapes.cuboid(-0.125, 0.0625, 0.625, -0.0625, 2, 0.875),
                BooleanBiFunction.OR);
        shape = VoxelShapes.combineAndSimplify(shape, VoxelShapes.cuboid(-0.3125, 0, -0.3125, 0.625, 0.0625, 0.625),
                BooleanBiFunction.OR);

        DIAGONAL_SHAPE = shape;
    }

    public ExteriorBlock(Settings settings) {
        super(settings.nonOpaque());

        this.setDefaultState(
                this.stateManager.getDefaultState().with(ROTATION, 0).with(WATERLOGGED, false).with(LEVEL_4, 4));
    }

    @Override
    public boolean emitsRedstonePower(BlockState state) {
        return true;
    }

    @Override
    public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        return 15;
    }

    @Override
    public boolean isShapeFullCube(BlockState state, BlockView world, BlockPos pos) {
        return false;
    }

    @Nullable @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        FluidState fluidState = ctx.getWorld().getFluidState(ctx.getBlockPos());
        return this.getDefaultState().with(ROTATION, 0).with(WATERLOGGED, fluidState.getFluid() == Fluids.WATER)
                .with(LEVEL_4, 4);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(ROTATION, WATERLOGGED, LEVEL_4);
    }

    @Override
    public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state) {
        return AITItems.TARDIS_ITEM.getDefaultStack();
    }

    public FluidState getFluidState(BlockState state) {
        return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
    }

    public boolean isTransparent(BlockState state, BlockView world, BlockPos pos) {
        return !(Boolean) state.get(WATERLOGGED);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        VoxelShape normal = this.getNormalShape(state, false);

        if (!(blockEntity instanceof ExteriorBlockEntity exterior))
            return normal;

        if (!exterior.isLinked())
            return normal;

        Tardis tardis = exterior.tardis().get();

        if (tardis.siege() == null)
            return normal;

        if (tardis.siege().isActive())
            return SIEGE_SHAPE;

        TravelHandlerBase.State travelState = tardis.travel().getState();

        if (travelState == TravelHandlerBase.State.LANDED || tardis.travel().getAlpha() > 0.75)
            return normal;

        if (DependencyChecker.hasPortals())
            return PORTALS_SHAPE;

        return VoxelShapes.empty();
    }

    @Override
    public VoxelShape getCullingShape(BlockState state, BlockView world, BlockPos pos) {
        return VoxelShapes.empty();
    }

    @Override
    public VoxelShape getRaycastShape(BlockState state, BlockView world, BlockPos pos) {
        return VoxelShapes.empty();
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        BlockEntity blockEntity = world.getBlockEntity(pos);

        if (!(blockEntity instanceof ExteriorBlockEntity exterior) || !exterior.isLinked())
            return getNormalShape(state, false);



        if (!exterior.isLinked())
            return getNormalShape(state, false);

        Tardis tardis = exterior.tardis().get();

        if (tardis.siege().isActive())
            return SIEGE_SHAPE;

        if (tardis.getExterior().getVariant().equals(ExteriorVariantRegistry.DOOM))
            return LEDGE_DOOM;

        if (DependencyChecker.hasPortals() && !tardis.door().isOpen() && tardis.getExterior().getVariant().hasPortals())
            return getNormalShape(state, true);

        if (tardis.getExterior().getVariant() instanceof AdaptiveVariant)
            return VoxelShapes.empty();

        TravelHandler travel = tardis.travel();

        if (travel.getState() == TravelHandlerBase.State.LANDED
                || travel.isHitboxShown())
            return getNormalShape(state, false);

        if (DependencyChecker.hasPortals())
            return PORTALS_SHAPE;

        return VoxelShapes.empty();
    }

    // TODO cache this.
    public VoxelShape getNormalShape(BlockState state, boolean ignorePortals) {
        Direction direction = RotationPropertyHelper.toDirection(state.get(ROTATION))
                .orElse(null);

        VoxelShape shape;

        if (direction == null) {
            shape = DependencyChecker.hasPortals() && !ignorePortals ? PORTALS_SHAPE_DIAGONAL : DIAGONAL_SHAPE;
            direction = approximateDirection(state.get(ROTATION));
        } else {
            shape = DependencyChecker.hasPortals() && !ignorePortals ? PORTALS_SHAPE : CUBE_NORTH_SHAPE;
        }

        return ShapeUtil.rotate(Direction.NORTH, direction, shape);
    }

    public Direction approximateDirection(int rotation) {
        return switch (rotation) {
            default -> Direction.NORTH;
            case 1, 2, 3 -> Direction.EAST;
            case 5, 6, 7 -> Direction.SOUTH;
            case 9, 10, 11 -> Direction.WEST;
        };
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.INVISIBLE;
    }

    @Override
    public VoxelShape getCameraCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        BlockEntity blockEntity = world.getBlockEntity(pos);

        if (!(blockEntity instanceof ExteriorBlockEntity exterior) || !exterior.isLinked())
            return getNormalShape(state, false);

        Tardis tardis = exterior.tardis().get();

        TravelHandlerBase.State travelState = tardis.travel().getState();

        if (travelState == TravelHandlerBase.State.LANDED || tardis.travel().getAlpha() > 0.75)
            return getNormalShape(state, false);

        if (tardis.getExterior().getVariant().equals(ExteriorVariantRegistry.DOOM))
            return LEDGE_DOOM;

        if (DependencyChecker.hasPortals())
            return PORTALS_SHAPE;

        return VoxelShapes.empty();
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand,
                              BlockHitResult hit) {
        if (world.isClient())
            return ActionResult.SUCCESS;

        if (!(world.getBlockEntity(pos) instanceof ExteriorBlockEntity exterior))
            return ActionResult.CONSUME;

        if (exterior.tardis().isEmpty())
            return ActionResult.FAIL;

        if (hit.getSide() != Direction.UP)
            exterior.useOn((ServerWorld) world, player.isSneaking(), player);

        return ActionResult.CONSUME; // Consume the event regardless of the outcome
    }

    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        if (world.isClient())
            return;

        if (world.getBlockEntity(pos) instanceof ExteriorBlockEntity exterior)
            exterior.onEntityCollision(entity);
    }

    @Nullable @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new ExteriorBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull World world, @NotNull BlockState state,
            @NotNull BlockEntityType<T> type) {
        return (world1, blockPos, blockState, ticker) -> {
            if (ticker instanceof ExteriorBlockEntity exterior)
                exterior.tick(world, blockPos, blockState, exterior);
        };
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        Tardis tardis = this.findTardis(world, pos);

        if (tardis == null || tardis.travel().getState() == TravelHandlerBase.State.LANDED)
            this.tryFall(state, world, pos);
    }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        world.scheduleBlockTick(pos, this, 2);
    }

    public void tryFall(BlockState state, ServerWorld world, BlockPos pos) {
        if (!canFallThrough(world, pos.down()))
            return;

        Tardis tardis = this.findTardis(world, pos);

        if (tardis == null || (tardis.travel().antigravs().get() && tardis.fuel().hasPower()))
            return;

        if (tardis.travel().getState() != TravelHandlerBase.State.LANDED)
            return;

        Planet planet = PlanetRegistry.getInstance().get(world);

        if (planet != null && planet.zeroGravity())
            return;

        tardis.flight().onStartFalling(world, state, pos);

        if (state.get(WATERLOGGED))
            state.with(WATERLOGGED, false);
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos,
            boolean notify) {
        super.neighborUpdate(state, world, pos, sourceBlock, sourcePos, notify);

        if (world.isClient())
            return;

        Tardis tardis = this.findTardis(((ServerWorld) world), pos);

        if (tardis == null)
            return;

        tardis.<BiomeHandler>handler(TardisComponent.Id.BIOME).update();
    }

    private static boolean canFallThrough(World world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);

        if (world.getBlockState(pos.down()).getBlock() == AITBlocks.EXTERIOR_BLOCK)
            return false;

        return canFallThrough(state);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);

        if (world.isClient())
            return;

        if (world.getBlockEntity(pos) instanceof ExteriorBlockEntity exterior)
            exterior.validateExteriorPosition();
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        super.onStateReplaced(state, world, pos, newState, moved);

        if (world.isClient())
            return;

        if (world.getBlockEntity(pos) instanceof ExteriorBlockEntity exterior)
            exterior.validateExteriorPosition();
    }

    private static boolean canFallThrough(BlockState state) {
        return state.isAir() || state.isIn(BlockTags.FIRE) || state.isLiquid() || state.isReplaceable();
    }

    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState,
            WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (state.get(WATERLOGGED))
            world.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));

        world.scheduleBlockTick(pos, this, 2);
        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    private Tardis findTardis(ServerWorld world, BlockPos pos) {
        if (world.getBlockEntity(pos) instanceof ExteriorBlockEntity exterior) {
            if (!exterior.isLinked() || exterior.tardis().isEmpty())
                return null;

            return exterior.tardis().get();
        }

        return null;
    }

    public void onLanding(Tardis tardis, ServerWorld world, BlockPos pos) {
        if (tardis == null)
            return;

        tardis.flight().onLanding(world, pos);
        world.scheduleBlockTick(pos, this, 2);
    }

    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        BlockPos blockPos = pos.down();
        if (random.nextInt(16) == 0) {
            if (canFallThrough(world.getBlockState(blockPos))) {
                ParticleUtil.spawnParticle(world, pos, random, ParticleTypes.TOTEM_OF_UNDYING);
            }
        }
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
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof ExteriorBlockEntity exterior) {
            Entity seat = exterior.getSeatEntity(world);
            if (seat != null) {
                seat.remove(Entity.RemovalReason.DISCARDED);
            }
        }
        super.onBreak(world, pos, state, player);
    }

}
