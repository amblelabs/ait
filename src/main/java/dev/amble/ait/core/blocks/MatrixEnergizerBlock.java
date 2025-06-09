package dev.amble.ait.core.blocks;

import dev.amble.ait.AITMod;
import dev.amble.ait.core.AITBlockEntityTypes;
import dev.amble.ait.core.AITBlocks;
import dev.amble.ait.core.advancement.TardisCriterions;
import dev.amble.ait.core.blockentities.MatrixEnergizerBlockEntity;
import dev.amble.ait.core.item.PersonalityMatrixItem;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.SculkShriekerBlockEntity;
import net.minecraft.block.enums.SculkSensorPhase;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.event.Vibrations;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static dev.amble.ait.client.util.TooltipUtil.addShiftHiddenTooltip;

public class MatrixEnergizerBlock extends Block implements BlockEntityProvider {
    private final VoxelShape DEFAULT = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 11.0, 16.0);
    public static final EnumProperty<SculkSensorPhase> SENSOR_PHASE = Properties.SCULK_SENSOR_PHASE;
    public static final IntProperty AGE = Properties.AGE_3;
    public static final BooleanProperty HAS_POWER = BooleanProperty.of("has_power");
    public static final BooleanProperty SILENT = BooleanProperty.of("silent");
    public MatrixEnergizerBlock(Settings settings) {
        super(settings);

        this.setDefaultState(
                this.getDefaultState().with(AGE, 0).with(HAS_POWER, false)
                        .with(SENSOR_PHASE, SculkSensorPhase.INACTIVE).with(SILENT, true)
        );
    }

    public static boolean isInactive(BlockState blockState) {
        return blockState.get(SENSOR_PHASE) == SculkSensorPhase.INACTIVE;
    }

    protected IntProperty getAgeProperty() {
        return AGE;
    }

    public int getMaxAge() {
        return 3;
    }

    public int getAge(BlockState state) {
        return state.get(this.getAgeProperty());
    }

    public final boolean isMature(BlockState blockState) {
        return this.getAge(blockState) >= this.getMaxAge();
    }
    public static boolean hasPower(BlockState state) {
        return state.get(HAS_POWER);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        ItemStack stack = player.getStackInHand(hand);
        if (world.isClient()) return ActionResult.SUCCESS;
        if (stack.isOf(Items.NETHER_STAR) && !hasPower(state)) {

            world.setBlockState(pos, state.with(HAS_POWER, true));
            stack.decrement(1);

            world.playSound(null, pos, SoundEvents.BLOCK_BEACON_POWER_SELECT, SoundCategory.BLOCKS, 1.0F, 0.6F);

            return ActionResult.SUCCESS;
        }

        return super.onUse(state, world, pos, player, hand, hit);
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (MatrixEnergizerBlock.getPhase(state) != SculkSensorPhase.ACTIVE) {
            if (MatrixEnergizerBlock.getPhase(state) == SculkSensorPhase.COOLDOWN) {
                world.setBlockState(pos, state.with(SENSOR_PHASE, SculkSensorPhase.INACTIVE), Block.NOTIFY_ALL);
            }
            return;
        }
        MatrixEnergizerBlock.setCooldown(world, pos, state);
    }

    public static SculkSensorPhase getPhase(BlockState state) {
        return state.get(SENSOR_PHASE);
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        super.randomDisplayTick(state, world, pos, random);

        Vec3d centre = pos.up().toCenterPos();
        if (hasPower(state)) {
            for (int i = 0; i < getAge(state); i++) {
                double offsetX = AITMod.RANDOM.nextGaussian() * getAge(state) * 0.01f;
                double offsetY = AITMod.RANDOM.nextGaussian() * getAge(state) * 0.01f;
                double offsetZ = AITMod.RANDOM.nextGaussian() * getAge(state) * 0.01f;
                world.addParticle(AITMod.CORAL_PARTICLE, centre.getX(), centre.getY() - 0.65f, centre.getZ(), offsetX, offsetY, offsetZ);
                world.addParticle(ParticleTypes.SCULK_SOUL, centre.getX(), centre.getY() - 0.65f, centre.getZ(), offsetX, offsetY, offsetZ);
                world.addParticle(ParticleTypes.SOUL_FIRE_FLAME, centre.getX(), centre.getY() - 0.65f, centre.getZ(), offsetX, offsetY, offsetZ);


            }
        }
    }

    @Override
    public boolean hasRandomTicks(BlockState state) {
        return true;
    }

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        tryCreate(world, pos, state);
        shriekerShrieks(state, world, pos);
    }

    public void shriekerShrieks(BlockState thisState, World world, BlockPos pos) {
        if (world.isClient()) return;
        if (!(world.getBlockState(pos.down()).getBlock() instanceof SculkShriekerBlock) || !world.getBlockState(pos.down())
                .get(SculkShriekerBlock.CAN_SUMMON)) {
            return;
        }

        if (!world.getBlockState(pos.down()).get(SculkShriekerBlock.SHRIEKING)) {
            return;
        }

        if (!hasPower(world.getBlockState(pos))) return;
        BlockEntity be = world.getBlockEntity(pos);
        BlockState state = world.getBlockState(pos.down());
        if (be instanceof MatrixEnergizerBlockEntity mbe) {
            if (mbe.getVibrationCallback().accepts((ServerWorld) world, pos, GameEvent.SHRIEK, GameEvent.Emitter.of(thisState))) {
                mbe.getEventListener().forceListen((ServerWorld) world, GameEvent.SHRIEK, GameEvent.Emitter.of(state),
                        new Vec3d(pos.down().getX(), pos.down().getY(), pos.down().getZ()));
                int i = this.getAge(thisState);
                if (i < this.getMaxAge()) {
                    world.setBlockState(pos, thisState.with(AGE, i + 1), 2);
                } else {
                    tryCreate(world, pos, thisState);
                }
            }
        }
    }

    @Override
    public void onBroken(WorldAccess world, BlockPos pos, BlockState state) {
        if (hasPower(state) && this.getAge(state) == this.getMaxAge()) {
            ItemStack pmStack = PersonalityMatrixItem.randomize();
            dropStack((World) world, pos, pmStack);
        }
        super.onBroken(world, pos, state);
    }

    private boolean tryCreate(World world, BlockPos pos, BlockState state) {
        if (world.isClient()) return false;
        if (this.isMature(state) && hasPower(state)) {
            world.playSound(null, pos, SoundEvents.BLOCK_SCULK_CATALYST_BLOOM, SoundCategory.BLOCKS, 1.0F, 1.0F);
            ItemStack pmStack = PersonalityMatrixItem.randomize();
            ItemEntity matrix = new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), pmStack);
            world.spawnEntity(matrix);
            if (world.getBlockEntity(pos.down()) instanceof SculkShriekerBlockEntity) {
                LargeEntitySpawnHelper.trySpawnAt(EntityType.WARDEN, SpawnReason.TRIGGERED, (ServerWorld) world,
                        pos.down(), 20, 5, 6,
                        LargeEntitySpawnHelper.Requirements.WARDEN).isPresent();
            }
            world.breakBlock(pos, false);

            return true;
        }

        return false;
    }
    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer,
            ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);

        if (!(placer instanceof ServerPlayerEntity player)) {
            return;
        }

        state.with(SILENT, true);

        if (!(world.getBlockState(pos.down()).getBlock() instanceof SculkShriekerBlock)) {
            world.breakBlock(pos, false);
            if (!player.isCreative()) dropStack(world, pos, AITBlocks.MATRIX_ENERGIZER.asItem().getDefaultStack());
            return;
        }

        if (world.getBlockEntity(pos) instanceof MatrixEnergizerBlockEntity) {
            TardisCriterions.PLACE_ENERGIZER.trigger(player);
        }
    }
    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return DEFAULT;
    }

    @Override
    public VoxelShape getRaycastShape(BlockState state, BlockView world, BlockPos pos) {
        return DEFAULT;
    }

    @Override
    public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state) {
        return AITBlocks.MATRIX_ENERGIZER.asItem().getDefaultStack();
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(AGE).add(HAS_POWER).add(SENSOR_PHASE).add(SILENT);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable BlockView world, List<Text> tooltip, TooltipContext options) {
        super.appendTooltip(stack, world, tooltip, options);

        addShiftHiddenTooltip(stack, tooltip, tooltips -> {
            tooltip.add(Text.translatable("tooltip.ait.matrix_energizer").formatted(Formatting.DARK_GRAY, Formatting.ITALIC));
        });
    }

    @Override
    @Nullable public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        if (!world.isClient) {
            return MatrixEnergizerBlock.checkType(type, AITBlockEntityTypes.MATRIX_ENERGIZER_BLOCK_ENTITY_TYPE,
                    (worldx, pos, statex, blockEntity) -> {
                        Vibrations.Ticker.tick(worldx,
                                blockEntity.getVibrationListenerData(), blockEntity.getVibrationCallback());
                                shriekerShrieks(statex, worldx, pos);
                    });
        }
        return null;
    }

    @Nullable protected static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> checkType(BlockEntityType<A> givenType, BlockEntityType<E> expectedType, BlockEntityTicker<? super E> ticker) {
        return expectedType == givenType ? (BlockEntityTicker<A>) ticker : null;
    }

    public static void setCooldown(World world, BlockPos pos, BlockState state) {
        world.setBlockState(pos, state.with(SENSOR_PHASE, SculkSensorPhase.COOLDOWN), Block.NOTIFY_ALL);
        world.scheduleBlockTick(pos, state.getBlock(), 10);
        MatrixEnergizerBlock.updateNeighbors(world, pos, state);
    }

    private static void updateNeighbors(World world, BlockPos pos, BlockState state) {
        Block block = state.getBlock();
        world.updateNeighborsAlways(pos, block);
        world.updateNeighborsAlways(pos.down(), block);
        state.with(SILENT, true);
    }

    public int getCooldownTime() {
        return 15;
    }

    @Nullable @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new MatrixEnergizerBlockEntity(pos, state);
    }

    public void setActive(World world, BlockPos pos, BlockState state, int frequency) {
        world.setBlockState(pos,
                state.with(SENSOR_PHASE, SculkSensorPhase.ACTIVE), Block.NOTIFY_ALL);
        world.scheduleBlockTick(pos, state.getBlock(), this.getCooldownTime());
        MatrixEnergizerBlock.updateNeighbors(world, pos, state);
    }
}
