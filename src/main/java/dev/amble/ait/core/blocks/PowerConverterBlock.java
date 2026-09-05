package dev.amble.ait.core.blocks;

import static dev.amble.ait.client.util.TooltipUtil.addShiftHiddenTooltip;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import net.minecraft.block.*;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import dev.amble.ait.api.ConsumableBlock;
import dev.amble.ait.core.AITBlockEntityTypes;
import dev.amble.ait.core.AITSounds;
import dev.amble.ait.core.AITTags;
import dev.amble.ait.core.advancement.TardisCriterions;
import dev.amble.ait.core.engine.link.IFluidSource;
import dev.amble.ait.core.engine.link.block.FluidLinkBlockEntity;
import dev.amble.ait.core.engine.link.block.HorizontalFluidLinkBlock;

public class PowerConverterBlock extends HorizontalFluidLinkBlock implements ConsumableBlock {
    private static final double ARTRON_PER_ITEM = 175;

    public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;
    protected static final VoxelShape Y_SHAPE = Block.createCuboidShape(
            4.0,
            0.0,
            2.5,
            12.0,
            32.0,
            13.5
    );


    public PowerConverterBlock(Settings settings) {
        super(settings);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return Y_SHAPE;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return Y_SHAPE;
    }

    @Override
    public boolean isShapeFullCube(BlockState state, BlockView world, BlockPos pos) {
        return false;
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        ItemStack stack = player.getStackInHand(hand);

        if (world.getBlockEntity(pos) instanceof FluidLinkBlockEntity be) {
            if (world.isClient()) return ActionResult.SUCCESS;
            if (!stack.isIn(AITTags.Items.IS_TARDIS_FUEL) && !stack.getItem().isFood()) return ActionResult.FAIL;
            if (!be.isConnected() || be.source() == null) return ActionResult.FAIL;

            int offered = player.isSneaking() ? stack.getCount() : Math.min(stack.getCount(), 1);
            boolean food = stack.getItem().isFood();
            int consumed = insertWholeItems(be.source(), offered);
            if (consumed <= 0) return ActionResult.FAIL;

            if (!player.isCreative())
                stack.decrement(consumed);

            if (food && !player.isCreative() && player instanceof ServerPlayerEntity serverPlayer) {
                TardisCriterions.FEED_POWER_CONVERTER.trigger(serverPlayer);
            }

            world.playSound(null, pos, AITSounds.POWER_CONVERT, SoundCategory.BLOCKS, 1.0F, 1.0F);

            return ActionResult.SUCCESS;
        }

        return super.onUse(state, world, pos, player, hand, hit);
    }

    @Override
    public boolean canAcceptItem(World world, BlockPos pos, ItemStack stack, Direction from) {
        if (!stack.isIn(AITTags.Items.IS_TARDIS_FUEL)) return false;
        if (!(world.getBlockEntity(pos) instanceof FluidLinkBlockEntity be)) return false;

        return be.isConnected() && be.source() != null && getConsumableItemCount(be.source(), 1) > 0;
    }

    @Override
    public ItemStack insertItem(World world, BlockPos pos, ItemStack stack, Direction from, boolean simulate) {
        if (!(world.getBlockEntity(pos) instanceof FluidLinkBlockEntity be)) return stack;
        if (!stack.isIn(AITTags.Items.IS_TARDIS_FUEL)) return stack;
        if (!be.isConnected() || be.source() == null) return stack;

        int consumable = getConsumableItemCount(be.source(), stack.getCount());
        if (consumable <= 0) return stack;

        if (simulate) return copyWithRemainder(stack, consumable);
        if (world.isClient()) return stack;

        int consumed = insertWholeItems(be.source(), consumable);
        if (consumed <= 0) return stack;

        world.playSound(null, pos, AITSounds.POWER_CONVERT, SoundCategory.BLOCKS, 1.0F, 1.0F);
        return copyWithRemainder(stack, consumed);
    }

    private static int getConsumableItemCount(IFluidSource source, int offered) {
        if (source == null || offered <= 0) return 0;

        double remainingCapacity = source.maxLevel() - source.level();
        if (!Double.isFinite(remainingCapacity) || remainingCapacity < ARTRON_PER_ITEM) return 0;

        long capacityInItems = (long) Math.floor(remainingCapacity / ARTRON_PER_ITEM);
        return (int) Math.min(offered, capacityInItems);
    }

    private static int insertWholeItems(IFluidSource source, int offered) {
        int consumable = getConsumableItemCount(source, offered);
        if (consumable <= 0) return 0;

        double requested = ARTRON_PER_ITEM * consumable;
        double accepted = source.insertLevel(requested);
        if (Double.compare(accepted, requested) == 0) return consumable;

        // Capacity was checked immediately before insertion on the server thread. If a custom
        // source still accepts only part of the request, roll it back rather than creating AU.
        if (Double.isFinite(accepted) && accepted > 0) source.extractLevel(accepted);
        return 0;
    }

    private static ItemStack copyWithRemainder(ItemStack stack, int consumed) {
        ItemStack leftover = stack.copy();
        leftover.decrement(consumed);

        return leftover.isEmpty() ? ItemStack.EMPTY : leftover;
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new BlockEntity(pos, state);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.ENTITYBLOCK_ANIMATED;
    }

    public static class BlockEntity extends FluidLinkBlockEntity {
        public BlockEntity(BlockPos pos, BlockState state) {
            super(AITBlockEntityTypes.POWER_CONVERTER_BLOCK_TYPE, pos, state);
        }
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable BlockView world, List<Text> tooltip, TooltipContext options) {
        super.appendTooltip(stack, world, tooltip, options);


        addShiftHiddenTooltip(stack, tooltip, tooltips -> {
            tooltip.add(Text.translatable("tooltip.ait.power_converter").formatted(Formatting.DARK_GRAY, Formatting.ITALIC));
        });
    }
}
