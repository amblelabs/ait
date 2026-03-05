package dev.amble.ait.core.item;

import java.awt.*;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.text.Style;
import net.minecraft.util.Colors;
import org.jetbrains.annotations.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;

import dev.amble.ait.api.ArtronHolderItem;
import dev.amble.ait.core.AITBlocks;

public class ChargedZeitonCrystalItem extends Item implements ArtronHolderItem {
    public static final double MAX_FUEL = 5000;

    public ChargedZeitonCrystalItem(Settings settings) {
        super(settings);
    }

    @Override
    public ItemStack getDefaultStack() {
        ItemStack stack = new ItemStack(this);
        NbtCompound nbt = stack.getOrCreateNbt();

        nbt.putDouble(FUEL_KEY, getMaxFuel(stack));

        return stack;
    }

    @Override
    public void onCraft(ItemStack stack, World world, PlayerEntity player) {
        super.onCraft(stack, world, player);
        NbtCompound nbt = stack.getOrCreateNbt();
        nbt.putDouble(FUEL_KEY, 0);
    }

    @Override
    public double getMaxFuel(ItemStack stack) {
        return MAX_FUEL;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        int currentFuel = (int) Math.round(this.getCurrentFuel(stack));
        Formatting fuelColor = currentFuel > (MAX_FUEL / 4) ? Formatting.GREEN : Formatting.RED;

        tooltip.add(
                Text.translatable("message.ait.artron_units", currentFuel)
                        .formatted(fuelColor)
                        .append(Text.literal(" / ").formatted(Formatting.GRAY))
                        .append(Text.literal(String.valueOf(MAX_FUEL)).formatted(Formatting.GRAY))
        );

        super.appendTooltip(stack, world, tooltip, context);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        BlockState state = context.getWorld().getBlockState(context.getBlockPos());
        PlayerEntity player = context.getPlayer();

        if (player == null) return ActionResult.PASS; // This may mess with some automation (like Create) but I don't care - Loqor

        if (state.isOf(AITBlocks.ZEITON_COBBLE)) {
            context.getWorld().setBlockState(context.getBlockPos(), AITBlocks.COMPACT_ZEITON.getDefaultState());
            context.getStack().decrement(1);
            return ActionResult.SUCCESS;
        }

        if (state.isOf(Blocks.LODESTONE)) {
            ItemStack stack = context.getStack();
            if (!this.hasMaxFuel(stack)) {
                player.sendMessage(Text.translatable("ait.charged_zeiton_crystal.not_max_fuel").append(
                        Text.of(" " + this.getCurrentFuel(stack) + "/" + this.getMaxFuel(stack))), true);
                return ActionResult.PASS;
            }
            Block block = AITBlocks.LODESTONE;
            context.getWorld().setBlockState(context.getBlockPos(), block.getDefaultState());
            AITBlocks.LODESTONE.onPlaced(player.getWorld(), context.getBlockPos(), block.getDefaultState(), player, stack);
            if (!player.isCreative()) context.getStack().decrement(1);
            return ActionResult.SUCCESS;
        }

        return super.useOnBlock(context);
    }
}
