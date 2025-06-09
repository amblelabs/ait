package dev.amble.ait.core.item;

import dev.amble.ait.api.ArtronHolderItem;
import dev.amble.ait.core.AITBlocks;
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
import org.jetbrains.annotations.Nullable;

import java.util.List;

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

        if (state.isOf(AITBlocks.ZEITON_COBBLE)) {
            context.getWorld().setBlockState(context.getBlockPos(), AITBlocks.COMPACT_ZEITON.getDefaultState());
            context.getStack().decrement(1);
            return ActionResult.SUCCESS;
        }

        return super.useOnBlock(context);
    }
}
