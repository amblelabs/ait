package dev.amble.ait.core.item;

import java.util.List;
import java.util.UUID;

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import dev.amble.ait.core.blockentities.ConsoleBlockEntity;
import dev.amble.ait.core.blockentities.ExteriorBlockEntity;

public class ArtronCollectorItem extends Item {
    public static final String AU_LEVEL = "au_level";
    public static final String UUID_KEY = "uuid";
    public static final Integer COLLECTOR_MAX_FUEL = 1500;

    public ArtronCollectorItem(Settings settings) {
        super(settings);
    }

    @Override
    public ItemStack getDefaultStack() {
        ItemStack stack = new ItemStack(this);
        NbtCompound nbt = stack.getOrCreateNbt();
        nbt.putDouble(AU_LEVEL, 0);
        return super.getDefaultStack();
    }

    public static UUID getUuid(ItemStack stack) {
        NbtCompound nbt = stack.getOrCreateNbt();

        if (nbt.contains(UUID_KEY))
            return nbt.getUuid(UUID_KEY);
        nbt.putUuid(UUID_KEY, UUID.randomUUID());
        return nbt.getUuid(UUID_KEY);
    }

    public static double getFuel(ItemStack stack) {
        NbtCompound nbt = stack.getOrCreateNbt();

        if (nbt.contains(AU_LEVEL))
            return nbt.getDouble(AU_LEVEL);
        nbt.putDouble(AU_LEVEL, 0);
        return 0d;
    }

    public static double addFuel(ItemStack stack, double fuel) {
        NbtCompound nbt = stack.getOrCreateNbt();
        double currentFuel = getFuel(stack);
        nbt.putDouble(AU_LEVEL, getFuel(stack) <= COLLECTOR_MAX_FUEL ? getFuel(stack) + fuel : COLLECTOR_MAX_FUEL);
        if (getFuel(stack) > COLLECTOR_MAX_FUEL)
            nbt.putDouble(AU_LEVEL, COLLECTOR_MAX_FUEL);
        if (getFuel(stack) == COLLECTOR_MAX_FUEL)
            return fuel - (COLLECTOR_MAX_FUEL - currentFuel);
        return 0;
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        PlayerEntity player = context.getPlayer();
        World world = context.getWorld();
        BlockPos clickedPos = context.getBlockPos();
        ItemStack cellItemStack = context.getStack();
        NbtCompound nbt = cellItemStack.getOrCreateNbt();

        if (world.isClient())
            return ActionResult.SUCCESS;

        if (player.isSneaking()) {
            if (world.getBlockEntity(clickedPos) instanceof ExteriorBlockEntity exterior) {
                if (exterior.tardis().isEmpty())
                    return ActionResult.FAIL;

                double residual = exterior.tardis().get().addFuel(nbt.getDouble(AU_LEVEL));
                nbt.putDouble(AU_LEVEL, residual);
                return ActionResult.CONSUME;
            } else if (world.getBlockEntity(clickedPos) instanceof ConsoleBlockEntity console) {
                if (console.tardis().isEmpty())
                    return ActionResult.FAIL;

                double residual = console.tardis().get().addFuel(nbt.getDouble(AU_LEVEL));
                nbt.putDouble(AU_LEVEL, residual);
                return ActionResult.CONSUME;
            }
            return ActionResult.FAIL;
        }

        return ActionResult.FAIL;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        NbtCompound tag = stack.getOrCreateNbt();
        String text = tag.contains(AU_LEVEL) ? "" + tag.getDouble(AU_LEVEL) : "0.0";
        tooltip.add(Text.literal(text + " / " + COLLECTOR_MAX_FUEL + ".0").formatted(Formatting.BLUE));
    }
}
