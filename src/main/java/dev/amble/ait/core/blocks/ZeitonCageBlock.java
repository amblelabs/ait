package dev.amble.ait.core.blocks;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import dev.amble.ait.core.AITBlocks;
import dev.amble.ait.core.AITSounds;
import dev.amble.ait.core.AITTags;
import dev.amble.ait.core.blockentities.ZeitonCageBlockEntity;
import dev.amble.ait.core.world.RiftChunkManager;

public class ZeitonCageBlock extends Block implements BlockEntityProvider {
    public static final int REQUIRED_FUEL = 500;
    public static final IntProperty FUEL = IntProperty.of("fuel", 0, REQUIRED_FUEL);

    public ZeitonCageBlock(Settings settings) {
        super(settings);

        this.setDefaultState(
                this.getStateManager().getDefaultState().with(FUEL, 0)
        );
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);

        builder.add(FUEL);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        ItemStack stack = player.getStackInHand(hand);

        if (!stack.isIn(AITTags.Items.IS_TARDIS_FUEL)) return ActionResult.FAIL; // Only allow fuel items
        if (hasReachedMaxFuel(state)) { // If already full, play a sound and return
            world.playSound(null, pos, AITSounds.CREAK_SEVEN, SoundCategory.BLOCKS, 1.0F, 1.0F);
            return ActionResult.FAIL;
        }
        if (world.isClient()) return ActionResult.SUCCESS; // Don't run on client
        if (!(RiftChunkManager.isRiftChunk((ServerWorld) world, pos))) {
            world.playSound(null, pos, AITSounds.SIEGE_ENABLE, SoundCategory.BLOCKS, 1.0F, 2.0F);

            return ActionResult.FAIL; // Only allow in rift chunks
        }
        stack.decrement(1);
        state = addFuel(state, 10);
        world.setBlockState(pos, state);

        world.playSound(null, pos, AITSounds.BWEEP, SoundCategory.BLOCKS, 1.0F, 1.0F);

        if (hasReachedMaxFuel(state)) {
            world.playSound(null, pos, AITSounds.DING, SoundCategory.BLOCKS, 1.0F, 1.0F);
        }

        return ActionResult.SUCCESS;
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);

        if (world.isClient()) return;
        if (!(isCageItem(itemStack))) return;

        world.setBlockState(pos, state.with(FUEL, getFuel(itemStack)));
    }

    @Override
    public List<ItemStack> getDroppedStacks(BlockState state, LootContextParameterSet.Builder builder) {
        ItemStack stack = AITBlocks.ZEITON_CAGE.asItem().getDefaultStack();

        stack.getOrCreateNbt().putInt("Fuel", state.get(FUEL));

        return List.of(stack);
    }

    @Override
    public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state) {
        ItemStack stack = AITBlocks.ZEITON_CAGE.asItem().getDefaultStack();

        stack.getOrCreateNbt().putInt("Fuel", state.get(FUEL));

        return stack;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable BlockView world, List<Text> tooltip, TooltipContext options) {
        super.appendTooltip(stack, world, tooltip, options);

        tooltip.add(Text.translatable("message.ait.tooltips.artron_units").formatted(Formatting.GOLD).append(
                Text.literal("" + getFuel(stack)).formatted(hasReachedMaxFuel(stack) ? Formatting.GREEN : Formatting.RED)));
        if (hasReachedMaxFuel(stack)) {
            tooltip.add(Text.translatable("message.ait.cage.full").formatted(Formatting.LIGHT_PURPLE));
            tooltip.add(Text.translatable("message.ait.cage.void_hint").formatted(Formatting.DARK_GRAY, Formatting.ITALIC));
        }
        if (getFuel(stack) == 0) {
            tooltip.add(Text.translatable("message.ait.cage.empty").formatted(Formatting.DARK_GRAY, Formatting.ITALIC));
        }
    }

    public static void onVoid(ItemStack stack, @Nullable ServerPlayerEntity nearest) {
        if (nearest == null) return;
        ServerWorld world = nearest.getServerWorld();

        if (!(hasReachedMaxFuel(stack))) {
            nearest.getInventory().insertStack(stack);
            world.playSound(null, nearest.getBlockPos(), AITSounds.CREAK_SIX, SoundCategory.BLOCKS, 1.0F, 1.0F);
            return;
        }

        nearest.getInventory().insertStack(AITBlocks.ENGINE_CORE_BLOCK.asItem().getDefaultStack());
        world.playSound(null, nearest.getBlockPos(), AITSounds.CREAK_SEVEN, SoundCategory.BLOCKS, 1.0F, 1.0F);
    }

    public static boolean isCageItem(ItemStack stack) {
        return stack.getItem().equals(AITBlocks.ZEITON_CAGE.asItem()) || getFuel(stack) != 0;
    }
    public static int getFuel(ItemStack stack) {
        if (!(stack.getOrCreateNbt().contains("Fuel"))) return 0;

        return stack.getNbt().getInt("Fuel");
    }
    public static boolean hasReachedMaxFuel(ItemStack stack) {
        return getFuel(stack) >= REQUIRED_FUEL;
    }

    public static boolean hasReachedMaxFuel(BlockState state) {
        return state.get(FUEL) >= REQUIRED_FUEL;
    }

    public static BlockState addFuel(BlockState state, int fuel) {
        int current = state.get(FUEL);

        int newFuel = Math.min(current + fuel, REQUIRED_FUEL);
        return state.with(FUEL, newFuel);
    }

    @Nullable @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new ZeitonCageBlockEntity(pos, state);
    }
}
