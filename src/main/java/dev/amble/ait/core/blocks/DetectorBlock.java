package dev.amble.ait.core.blocks;

import dev.amble.ait.core.AITBlockEntityTypes;
import dev.amble.ait.core.blockentities.DetectorBlockEntity;
import dev.amble.ait.core.tardis.Tardis;
import dev.amble.ait.core.tardis.handler.TardisCrashHandler;
import dev.amble.ait.core.tardis.handler.travel.TravelHandlerBase;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.enums.WallMountLocation;
import net.minecraft.entity.player.PlayerEntity;
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
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

@SuppressWarnings("deprecation")
public class DetectorBlock extends WallMountedBlock implements BlockEntityProvider {

    public static final BooleanProperty POWERED = Properties.POWERED;
    public static final IntProperty POWER = Properties.POWER;

    public static final EnumProperty<Type> TYPE = EnumProperty.of("type", Type.class);

    protected static final VoxelShape NORTH_WALL_SHAPE = Block.createCuboidShape(5.0, 4.0, 10.0, 11.0, 12.0, 16.0);
    protected static final VoxelShape SOUTH_WALL_SHAPE = Block.createCuboidShape(5.0, 4.0, 0.0, 11.0, 12.0, 6.0);
    protected static final VoxelShape WEST_WALL_SHAPE = Block.createCuboidShape(10.0, 4.0, 5.0, 16.0, 12.0, 11.0);
    protected static final VoxelShape EAST_WALL_SHAPE = Block.createCuboidShape(0.0, 4.0, 5.0, 6.0, 12.0, 11.0);
    protected static final VoxelShape FLOOR_Z_AXIS_SHAPE = Block.createCuboidShape(5.0, 0.0, 4.0, 11.0, 6.0, 12.0);
    protected static final VoxelShape FLOOR_X_AXIS_SHAPE = Block.createCuboidShape(4.0, 0.0, 5.0, 12.0, 6.0, 11.0);
    protected static final VoxelShape CEILING_Z_AXIS_SHAPE = Block.createCuboidShape(5.0, 10.0, 4.0, 11.0, 16.0, 12.0);
    protected static final VoxelShape CEILING_X_AXIS_SHAPE = Block.createCuboidShape(4.0, 10.0, 5.0, 12.0, 16.0, 11.0);

    public DetectorBlock(AbstractBlock.Settings settings) {
        super(settings.emissiveLighting((state, world, pos) -> state.get(POWERED))
                .luminance(value -> value.get(POWERED) ? 9 : 3));
        this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH).with(POWER, 0)
                .with(POWERED, false).with(FACE, WallMountLocation.WALL).with(TYPE, Type.POWER));
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return switch (state.get(FACE)) {
            case FLOOR -> state.get(FACING).getAxis() == Direction.Axis.X ? FLOOR_X_AXIS_SHAPE : FLOOR_Z_AXIS_SHAPE;
            case WALL -> switch (state.get(FACING)) {
                case EAST -> EAST_WALL_SHAPE;
                case WEST -> WEST_WALL_SHAPE;
                case SOUTH -> SOUTH_WALL_SHAPE;
                default -> NORTH_WALL_SHAPE;
            };
            default -> state.get(FACING).getAxis() == Direction.Axis.X ? CEILING_X_AXIS_SHAPE : CEILING_Z_AXIS_SHAPE;
        };
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand,
            BlockHitResult hit) {
        if (!player.canModifyBlocks())
            return super.onUse(state, world, pos, player, hand, hit);

        if (world.isClient())
            return ActionResult.SUCCESS;

        state = state.cycle(TYPE);

        world.setBlockState(pos, state, Block.NO_REDRAW);
        world.emitGameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Emitter.of(player, state));
        return ActionResult.CONSUME;
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (moved || state.isOf(newState.getBlock()))
            return;

        if (state.get(POWERED))
            this.updateNeighbors(state, world, pos);

        super.onStateReplaced(state, world, pos, newState, false);
    }

    @Override
    public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        return state.get(POWER);
    }

    @Override
    public boolean emitsRedstonePower(BlockState state) {
        return true;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACE, FACING, POWERED, POWER, TYPE);
    }

    @Override
    public boolean hasSidedTransparency(BlockState state) {
        return true;
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Nullable public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state,
            BlockEntityType<T> type) {
        return checkType(type, AITBlockEntityTypes.DETECTOR_BLOCK_ENTITY_TYPE, DetectorBlock::tick);
    }

    @Nullable protected static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> checkType(
            BlockEntityType<A> givenType, BlockEntityType<E> expectedType, BlockEntityTicker<? super E> ticker) {
        return expectedType == givenType ? (BlockEntityTicker<A>) ticker : null;
    }

    private void updateNeighbors(BlockState state, World world, BlockPos pos) {
        world.updateNeighborsAlways(pos, this);
        world.updateNeighborsAlways(pos.offset(DetectorBlock.getDirection(state).getOpposite()), this);
    }

    private static void updateState(BlockState state, World world, BlockPos pos, Tardis tardis) {
        int power = state.get(TYPE).getPower(tardis);

        world.setBlockState(pos, state.with(POWER, power).with(POWERED, power != 0), NOTIFY_ALL);
    }

    private static void tick(World world, BlockPos pos, BlockState state, DetectorBlockEntity detector) {
        if (world.isClient() || !detector.isLinked())
            return;

        updateState(state, world, pos, detector.tardis().get());
    }

    @Nullable @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new DetectorBlockEntity(pos, state);
    }

    public enum Type implements StringIdentifiable {
        FLIGHT(tardis -> tardis.travel().getState() != TravelHandlerBase.State.LANDED ? 15 : 0), POWER(
                tardis -> tardis.fuel().hasPower() ? 15 : 0), CRASHED(tardis -> {
                    TardisCrashHandler.State state = tardis.crash().getState();

                    if (state == TardisCrashHandler.State.NORMAL)
                        return 0;

                    return state == TardisCrashHandler.State.UNSTABLE ? 7 : 15;
                }), DOOR_LOCKED(tardis -> tardis.door().locked() ? 15 : 0), DOOR_OPEN(
                        tardis -> tardis.door().isOpen() ? 15 : 0), SONIC(
                                tardis -> tardis.sonic().getConsoleSonic() != null ? 15 : 0), ALARMS(
                                        tardis -> tardis.alarm().enabled().get() ? 15 : 0);

        private final String name;
        private final Function<Tardis, Integer> func;

        Type(Function<Tardis, Integer> func) {
            this.name = this.toString().toLowerCase();
            this.func = func;
        }

        public int getPower(Tardis tardis) {
            return this.func.apply(tardis);
        }

        @Override
        public String asString() {
            return this.name;
        }
    }
}
