package dev.amble.ait.core.blocks;

import java.util.function.ToIntFunction;

import dev.amble.ait.AITMod;
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
import dev.amble.ait.data.ShapeMap;
import dev.amble.ait.module.planet.core.space.planet.Planet;
import dev.amble.ait.module.planet.core.space.planet.PlanetRegistry;
import dev.amble.ait.registry.impl.exterior.ExteriorVariantRegistry;
import dev.amble.lib.api.ICantBreak;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationPropertyHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

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

    public static final VoxelShape SIEGE_SHAPE = Block.createCuboidShape(4.0, 0.0, 4.0, 12.0, 8.0, 12.0);

    private static final ShapeMap CUBE_SHAPES = ShapeUtil.rotations(Direction.NORTH, CUBE_NORTH_SHAPE).build();
    private static final ShapeMap PORTALS_SHAPES = ShapeUtil.rotations(Direction.NORTH, PORTALS_SHAPE).build();
    private static final VoxelShape[] TURNED_SHAPES = turnedShapes(5);
    // a turned mob reaches further ahead with its box corner, 3px deeper lets a pig reach the portal like it does straight
    private static final VoxelShape[] TURNED_PORTALS_SHAPES = turnedShapes(14);

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
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof ExteriorBlockEntity exterior && exterior.isLinked()) {
            Tardis tardis = exterior.tardis().get();
            if (tardis != null && tardis.fuel().hasPower()) {
                return 15;
            }
        }
        return 0;
    }

    @Override
    public int getStrongRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        return getWeakRedstonePower(state, world, pos, direction);
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
        VoxelShape normal = this.getNormalShape(state, true);

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

        if (DependencyChecker.hasPortals() && AITMod.CONFIG.allowPortalsBoti)
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

        if (DependencyChecker.hasPortals() && AITMod.CONFIG.allowPortalsBoti && !tardis.door().isOpen() && tardis.getExterior().getVariant().hasPortals())
            return getNormalShape(state, true);

        if (tardis.chameleon().isApplied())
            return VoxelShapes.empty();

        TravelHandler travel = tardis.travel();

        if (travel.getState() == TravelHandlerBase.State.LANDED
                || travel.isHitboxShown())
            return getNormalShape(state, false);

        if (DependencyChecker.hasPortals() && AITMod.CONFIG.allowPortalsBoti)
            return PORTALS_SHAPE;

        return VoxelShapes.empty();
    }

    public VoxelShape getNormalShape(BlockState state, boolean ignorePortals) {
        int rotation = state.get(ROTATION);
        Direction direction = RotationPropertyHelper.toDirection(rotation).orElse(null);
        boolean portals = DependencyChecker.hasPortals() && AITMod.CONFIG.allowPortalsBoti && !ignorePortals;

        if (direction == null)
            return portals ? TURNED_PORTALS_SHAPES[rotation] : TURNED_SHAPES[rotation];

        return (portals ? PORTALS_SHAPES : CUBE_SHAPES).get(direction);
    }

    // shapes are axis aligned, so the turned body and ledge are filled in 1px columns
    private static VoxelShape[] turnedShapes(double recess) {
        VoxelShape[] shapes = new VoxelShape[MAX_ROTATIONS];

        for (int rotation = 0; rotation < MAX_ROTATIONS; rotation++) {
            if (RotationPropertyHelper.toDirection(rotation).isPresent())
                continue;

            float rad = RotationPropertyHelper.toDegrees(rotation) * MathHelper.RADIANS_PER_DEGREE;
            double cos = MathHelper.cos(rad);
            double sin = MathHelper.sin(rad);
            VoxelShape shape = VoxelShapes.empty();

            for (int x = -8; x < 24; x++) {
                int from = 24, to = -8, bodyFrom = 24, bodyTo = -8;

                for (int z = -8; z < 24; z++) {
                    double dx = (x + 0.5) / 16 - 0.5;
                    double dz = (z + 0.5) / 16 - 0.5;
                    double across = dx * cos + dz * sin + 0.5;
                    double depth = dz * cos - dx * sin + 0.5;

                    if (across < 0 || across > 1 || depth < -3.5 / 16 || depth > 1)
                        continue;

                    from = Math.min(from, z);
                    to = Math.max(to, z + 1);

                    if (depth >= recess / 16) {
                        bodyFrom = Math.min(bodyFrom, z);
                        bodyTo = Math.max(bodyTo, z + 1);
                    }
                }

                if (to > from)
                    shape = VoxelShapes.combine(shape, Block.createCuboidShape(x, 0, from, x + 1, 1, to), BooleanBiFunction.OR);

                if (bodyTo > bodyFrom)
                    shape = VoxelShapes.combine(shape, Block.createCuboidShape(x, 0, bodyFrom, x + 1, 32, bodyTo), BooleanBiFunction.OR);
            }

            shapes[rotation] = shape.simplify();
        }

        return shapes;
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

        if (DependencyChecker.hasPortals() && AITMod.CONFIG.allowPortalsBoti)
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

        if (world.getBlockEntity(pos) instanceof ExteriorBlockEntity exterior && !exterior.isWallHit(entity))
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
        //FIXME: re-enable this block after the exterior falling issues are resolved :(
        /*
        Tardis tardis = this.findTardis(world, pos);

        if (tardis == null)
            return;

        if (tardis.travel().getState() != TravelHandlerBase.State.LANDED
                || !canFallThrough(world, pos.down())) {
            tardis.flight().shouldFall().set(false);
            return;
        }

        tardis.flight().shouldFall().set(true);

        if (tardis.travel().antigravs().get() && tardis.fuel().hasPower())
            return;

        tardis.flight().onStartFalling(world, state, pos);

        if (state.get(WATERLOGGED))
            state.with(WATERLOGGED, false);
        */
    }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        world.scheduleBlockTick(pos, this, 2);
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
        Planet planet = PlanetRegistry.getInstance().get(world);

        if (planet != null && planet.zeroGravity())
            return false;

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

                if (world.getBlockEntity(pos) instanceof ExteriorBlockEntity exterior && exterior.isLinked()) {
                    Tardis tardis = exterior.tardis().get();
                    if (tardis.cloak().silent().get())
                        return;
                }
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
