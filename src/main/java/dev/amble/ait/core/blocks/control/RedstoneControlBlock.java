package dev.amble.ait.core.blocks.control;

import org.jetbrains.annotations.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import dev.amble.ait.core.AITItems;
import dev.amble.ait.core.AITSounds;
import dev.amble.ait.core.blockentities.control.RedstoneControlBlockEntity;
import dev.amble.ait.core.tardis.Tardis;

public class RedstoneControlBlock extends ControlBlock {
    private static final BooleanProperty POWERED = Properties.POWERED;
    private static final IntProperty MODE = IntProperty.of("mode", 0, Mode.values().length - 1);

    public RedstoneControlBlock(Settings settings) {
        super(settings);

        this.setDefaultState(
                this.getStateManager().getDefaultState().with(POWERED, false).with(MODE, 0)
        );
    }

    @Nullable @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new RedstoneControlBlockEntity(pos, state);
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        if (!(world.getBlockEntity(pos) instanceof RedstoneControlBlockEntity entity))
            return;

        if (world.isClient())
            return;

        if (!entity.isLinked())
            return;

        Tardis tardis = entity.tardis().get();
        PlayerEntity user = tardis.loyalty().getLoyalPlayerInside();

        if (user == null)
            return;

        boolean wasPowered = state.get(POWERED);
        boolean powered = world.isReceivingRedstonePower(pos) || world.isReceivingRedstonePower(pos.up());

        if (wasPowered == powered)
            return;

        state = state.with(POWERED, powered);
        world.setBlockState(pos, state, Block.NOTIFY_ALL);

        if (!powered)
            return;

        entity.run((ServerPlayerEntity) user, Mode.get(state));
    }

    @Override
    public Item asItem() {
        return AITItems.REDSTONE_CONTROL;
    }


    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);

        builder.add(POWERED, MODE);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.isClient())
            return ActionResult.SUCCESS;

        if (isHoldingScanningSonic(player)) {
            sendSonicMessage((ServerPlayerEntity) player, (RedstoneControlBlockEntity) world.getBlockEntity(pos));
            return ActionResult.SUCCESS;
        }

        world.setBlockState(pos, Mode.set(state, Mode.get(state).next())); // set to next mode
        world.playSound(null, pos, AITSounds.REDSTONE_CONTROL_SWITCHAROO, SoundCategory.BLOCKS, 0.1f, 0f);

        return ActionResult.SUCCESS;
    }

    @Override
    public void onBlockBreakStart(BlockState state, World world, BlockPos pos, PlayerEntity player) {
        // dont run control
    }

    @Override
    public @Nullable BlockState getPlacementState(ItemPlacementContext ctx) {
        return super.getPlacementState(ctx).with(FACING, Direction.NORTH); // i cba
    }

    public enum Mode {
        USE,
        PUNCH;

        Mode next() {
            return Mode.values()[(this.ordinal() + 1) % Mode.values().length];
        }

        static Mode get(BlockState state) {
            return Mode.values()[state.get(MODE)];
        }
        static BlockState set(BlockState state, Mode mode) {
            return state.with(MODE, mode.ordinal());
        }
    }
}
