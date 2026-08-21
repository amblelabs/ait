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

import dev.amble.ait.api.ArtronHolderItem;
import dev.amble.ait.core.blockentities.ConsoleBlockEntity;
import dev.amble.ait.core.blockentities.ExteriorBlockEntity;
import dev.amble.ait.core.tardis.Tardis;

public class ArtronCollectorItem extends Item implements ArtronHolderItem {
    public static final String AU_LEVEL = "au_level";
    public static final String UUID_KEY = "uuid";
    public static final Integer COLLECTOR_MAX_FUEL = 1500;

    public ArtronCollectorItem(Settings settings) {
        super(settings);
    }

    @Override
    public ItemStack getDefaultStack() {
        ItemStack stack = new ItemStack(this);
        this.setCurrentFuel(0, stack);
        return stack;
    }

    public static UUID getUuid(ItemStack stack) {
        NbtCompound nbt = stack.getOrCreateNbt();

        if (nbt.contains(UUID_KEY))
            return nbt.getUuid(UUID_KEY);
        nbt.putUuid(UUID_KEY, UUID.randomUUID());
        return nbt.getUuid(UUID_KEY);
    }

    public static double getFuel(ItemStack stack) {
        if (stack.getItem() instanceof ArtronCollectorItem collector)
            return collector.getCurrentFuel(stack);

        return getFallbackFuel(stack);
    }

    public static double addFuel(ItemStack stack, double fuel) {
        if (stack.getItem() instanceof ArtronCollectorItem collector)
            return collector.addFuel(fuel, stack);

        double requested = sanitizeAmount(fuel);
        NbtCompound nbt = stack.getOrCreateNbt();
        double currentFuel = getFuel(stack);
        double accepted = Math.min(requested, COLLECTOR_MAX_FUEL - currentFuel);

        nbt.putDouble(AU_LEVEL, currentFuel + accepted);
        return requested - accepted;
    }

    @Override
    public double getMaxFuel(ItemStack stack) {
        return COLLECTOR_MAX_FUEL;
    }

    @Override
    public String getFuelKey() {
        return AU_LEVEL;
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        PlayerEntity player = context.getPlayer();
        World world = context.getWorld();
        BlockPos clickedPos = context.getBlockPos();
        ItemStack cellItemStack = context.getStack();
        if (player == null || !player.isSneaking()) return ActionResult.FAIL;

        Tardis tardis;
        if (world.getBlockEntity(clickedPos) instanceof ExteriorBlockEntity exterior) {
            if (exterior.tardis().isEmpty()) return ActionResult.FAIL;
            tardis = exterior.tardis().get();
        } else if (world.getBlockEntity(clickedPos) instanceof ConsoleBlockEntity console) {
            if (console.tardis().isEmpty()) return ActionResult.FAIL;
            tardis = console.tardis().get();
        } else {
            return ActionResult.FAIL;
        }

        // The authoritative fuel capacity only exists on the server. PASS keeps the interaction
        // flowing without rendering a successful transfer before the server has accepted any AU.
        if (world.isClient()) return ActionResult.PASS;

        double offered = this.getCurrentFuel(cellItemStack);
        if (offered <= 0) return ActionResult.FAIL;

        double returned = tardis.addFuel(offered);
        double residual = Double.isFinite(returned) ? Math.min(Math.max(returned, 0), offered) : offered;
        if (offered - residual <= 0) return ActionResult.FAIL;

        this.setCurrentFuel(residual, cellItemStack);
        return ActionResult.CONSUME;

    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        String text = String.valueOf(this.getCurrentFuel(stack));
        tooltip.add(Text.literal(text + " / " + COLLECTOR_MAX_FUEL + ".0").formatted(Formatting.BLUE));
    }

    private static double getFallbackFuel(ItemStack stack) {
        NbtCompound nbt = stack.getOrCreateNbt();
        double stored = nbt.contains(AU_LEVEL) ? nbt.getDouble(AU_LEVEL) : 0;
        double current = Double.isFinite(stored) ? Math.min(Math.max(stored, 0), COLLECTOR_MAX_FUEL) : 0;

        if (!nbt.contains(AU_LEVEL) || Double.compare(stored, current) != 0)
            nbt.putDouble(AU_LEVEL, current);

        return current;
    }

    private static double sanitizeAmount(double amount) {
        return Double.isFinite(amount) && amount > 0 ? amount : 0;
    }
}
