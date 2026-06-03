package dev.amble.ait.core.blocks;

import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.Nullable;
import dev.amble.ait.core.blockentities.PottedSonicScrewdriverBlockEntity;
import dev.amble.ait.core.item.SonicItem;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public class PottedSonicScrewdriverBlock extends BlockWithEntity {
    public static final int MAX_SONICS = 6;
    protected static final VoxelShape SHAPE = Block.createCuboidShape(5, 0, 5, 11, 6, 11);

    public PottedSonicScrewdriverBlock(Settings settings) {
        super(settings);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new PottedSonicScrewdriverBlockEntity(pos, state);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext ctx) {
        return SHAPE;
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (player.getStackInHand(hand).getItem() instanceof SonicItem)
            return ActionResult.PASS;

        if (!world.isClient && world.getBlockEntity(pos) instanceof PottedSonicScrewdriverBlockEntity pot) {
            ItemStack sonic = pot.removeLast();

            if (!sonic.isEmpty()) {
                if (!player.giveItemStack(sonic))
                    player.dropItem(sonic, false);
            }

            if (pot.count() == 0)
                world.setBlockState(pos, Blocks.FLOWER_POT.getDefaultState(), Block.NOTIFY_ALL);

            world.emitGameEvent(player, GameEvent.BLOCK_CHANGE, pos);
        }

        return ActionResult.success(world.isClient);
    }

    @Override
    public List<ItemStack> getDroppedStacks(BlockState state, LootContextParameterSet.Builder builder) {
        List<ItemStack> drops = new ArrayList<>();
        drops.add(new ItemStack(Items.FLOWER_POT));

        if (builder.getOptional(LootContextParameters.BLOCK_ENTITY) instanceof PottedSonicScrewdriverBlockEntity pot) {
            for (ItemStack sonic : pot.getSonics())
                drops.add(sonic.copy());
        }

        return drops;
    }
}
