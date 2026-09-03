package dev.amble.ait.core.blocks;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import dev.amble.ait.core.blockentities.UntemperedSchismBlockEntity;
import dev.amble.ait.core.engine.link.block.FluidLinkBlockEntity;
import dev.amble.ait.core.engine.link.block.HorizontalFluidLinkBlock;
import dev.amble.ait.core.world.RiftChunkManager;

/**
 * @author Loqor
 * The purpose of this block is to rip open rifts in rift chunks as opposed to the silly entities.
 * */
@SuppressWarnings("deprecation")
public class UntemperedSchismBlock extends HorizontalFluidLinkBlock implements BlockEntityProvider {

    // 10 seconds = 200 ticks. We tick every 2 ticks, so 100 steps.
    public static final int TOTAL_STEPS = 30;
    public static final int TICK_INTERVAL = 2;
    public static final int ARTRON_PER_TICK = 10;

    public static final IntProperty CHARGE_TICK = IntProperty.of("charge_tick", 0, TOTAL_STEPS);
    public static final BooleanProperty ENABLED = Properties.ENABLED;

    public static final Vector3f COLOR_FROM = new Vector3f(1.0f, 0.85f, 0.0f); // bright gold
    public static final Vector3f COLOR_TO = new Vector3f(1.0f, 1.0f, 0.6f);    // pale yellow

    public UntemperedSchismBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.getStateManager().getDefaultState().with(CHARGE_TICK, 0).with(HorizontalFacingBlock.FACING, Direction.NORTH).with(ENABLED, false));
    }

    @Nullable @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(HorizontalFacingBlock.FACING, ctx.getHorizontalPlayerFacing().getOpposite()).with(ENABLED, false);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(CHARGE_TICK).add(HorizontalFacingBlock.FACING).add(ENABLED);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull World world, @NotNull BlockState state,
                                                                  @NotNull BlockEntityType<T> type) {
        return (world1, blockPos, blockState, ticker) -> {
            if (ticker instanceof UntemperedSchismBlockEntity ripper) {
                ripper.tick(world, blockPos, blockState, ripper);
            }
        };
    }

    @Override
    public VoxelShape getCullingShape(BlockState state, BlockView world, BlockPos pos) {
        return VoxelShapes.empty();
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        if (!(world instanceof ServerWorld serverWorld)) return;

        RiftChunkManager manager = RiftChunkManager.getInstance(serverWorld);
        ChunkPos chunkPos = new ChunkPos(pos);

        if (!isConsumable(manager, chunkPos)) {
            if (placer != null) {
                placer.sendMessage(Text.translatable("message.ait.riftscanner.info3"));
            }
            world.playSound(null, pos, SoundEvents.BLOCK_BEACON_DEACTIVATE,
                    SoundCategory.BLOCKS, 1, 0.5f);
            return;
        }

        world.playSound(null, pos, SoundEvents.BLOCK_BEACON_ACTIVATE,
                SoundCategory.BLOCKS, 1, 1.5f);
    }

    private static boolean isConsumable(RiftChunkManager manager, ChunkPos pos) {
        return manager.isRiftChunk(pos);
    }

    @Override
    public FluidLinkBlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new UntemperedSchismBlockEntity(pos, state);
    }
}