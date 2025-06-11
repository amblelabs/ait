package dev.amble.ait.core.blocks;

import static dev.amble.ait.client.util.TooltipUtil.addShiftHiddenTooltip;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import net.minecraft.block.*;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
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
import dev.amble.ait.core.engine.link.block.DirectionalFluidLinkBlock;
import dev.amble.ait.core.engine.link.block.FluidLinkBlockEntity;

public class PowerConverterBlock extends DirectionalFluidLinkBlock implements ConsumableBlock {

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
            if (!(be.isPowered())) return ActionResult.FAIL;
            if (!stack.isIn(AITTags.Items.IS_TARDIS_FUEL)) return ActionResult.FAIL;

            if (!player.isSneaking()) {
                be.source().addLevel(175);
                stack.decrement(1);
            } else {
                int count = stack.getCount();

                be.source().addLevel(175 * count);
                stack.decrement(count);
            }

            world.playSound(null, pos, AITSounds.POWER_CONVERT, SoundCategory.BLOCKS, 1.0F, 1.0F);

            return ActionResult.SUCCESS;
        }

        return super.onUse(state, world, pos, player, hand, hit);
    }

    @Override
    public boolean canAcceptItem(World world, BlockPos pos, ItemStack stack, Direction from) {
        return stack.isIn(AITTags.Items.IS_TARDIS_FUEL);
    }

    @Override
    public ItemStack insertItem(World world, BlockPos pos, ItemStack stack, Direction from, boolean simulate) {
        if (!(world.getBlockEntity(pos) instanceof FluidLinkBlockEntity be)) return stack;

        if (!be.isPowered()) return stack;

        if (!simulate && !world.isClient) {
            be.source().addLevel(175);
            world.playSound(null, pos, AITSounds.POWER_CONVERT, SoundCategory.BLOCKS, 1.0F, 1.0F);
        }

        ItemStack leftover = stack.copy();
        leftover.decrement(1);

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
