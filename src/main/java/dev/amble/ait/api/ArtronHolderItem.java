package dev.amble.ait.api;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;

public interface ArtronHolderItem {
    String FUEL_KEY = "fuel";

    default double getCurrentFuel(ItemStack stack) {
        NbtCompound nbt = stack.getOrCreateNbt();

        if (!nbt.contains(this.getFuelKey())) {
            this.setCurrentFuel(0, stack);
            return 0;
        }

        return nbt.getDouble(this.getFuelKey());
    }

    default void setCurrentFuel(double var, ItemStack stack) {
        NbtCompound nbt = stack.getOrCreateNbt();

        nbt.putDouble(this.getFuelKey(), var <= 0 ? 0 : var);
    }

    default double addFuel(double val, ItemStack stack) {
        double current = this.getCurrentFuel(stack);
        double max = this.getMaxFuel(stack);

        this.setCurrentFuel(Math.min(current + val, max), stack);
        return 0;
    }

    default void removeFuel(double var, ItemStack stack) {
        double current = this.getCurrentFuel(stack);

        if (current - var < 0) {
            this.setCurrentFuel(0, stack);
            return;
        }
        this.setCurrentFuel(current - var, stack);
    }

    double getMaxFuel(ItemStack stack);

    default boolean isOutOfFuel(ItemStack stack) {
        return this.getCurrentFuel(stack) <= 0;
    }

    default boolean hasMaxFuel(ItemStack stack) {
        return this.getCurrentFuel(stack) >= this.getMaxFuel(stack);
    }

    default String getFuelKey() {
        return FUEL_KEY;
    }
}
