package dev.amble.ait.module.gun.core.item;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.ClickType;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;

import dev.amble.ait.api.ArtronHolderItem;
import dev.amble.ait.core.item.ZeitonShardItem;
import dev.amble.ait.module.gun.core.entity.GunEntityTypes;
import dev.amble.ait.module.gun.core.entity.StaserBoltEntity;

public class StaserBoltMagazine extends Item implements ArtronHolderItem {
    public StaserBoltMagazine(Settings settings) {
        super(settings);
    }

    public static final double MAX_FUEL = 64;

    public PersistentProjectileEntity createStaserbolt(World world, ItemStack stack, LivingEntity shooter) {
        StaserBoltEntity staserBoltEntity = new StaserBoltEntity(GunEntityTypes.STASER_BOLT_ENTITY_TYPE, world);
        return staserBoltEntity.createFromConstructor(world, shooter);
    }
    @Override
    public ItemStack getDefaultStack() {
        ItemStack stack = new ItemStack(this);
        NbtCompound nbt = stack.getOrCreateNbt();

        nbt.putDouble(FUEL_KEY, MAX_FUEL);

        return stack;
    }

    @Override
    public boolean onClicked(ItemStack stack, ItemStack otherStack, Slot slot, ClickType clickType, PlayerEntity player, StackReference cursorStackReference) {
        if (otherStack.getItem() instanceof ZeitonShardItem) {
            int shardCount = otherStack.getCount();
            if (stack.getItem() instanceof StaserBoltMagazine mag) {
                double ammo = mag.getCurrentFuel(stack);
                if (clickType == ClickType.RIGHT && mag.getCurrentFuel(stack) < mag.getMaxFuel(stack)) {
                    int residual = (int) ((ammo + shardCount) - mag.getCurrentFuel(stack));
                    mag.setCurrentFuel(ammo + shardCount, stack);
                    otherStack.decrement(residual);
                    return true;
                }
            }
        }
        return super.onClicked(stack, otherStack, slot, clickType, player, cursorStackReference);
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
}
