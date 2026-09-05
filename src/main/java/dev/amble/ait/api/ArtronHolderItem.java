package dev.amble.ait.api;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;

public interface ArtronHolderItem {
    String FUEL_KEY = "fuel";

    default double getCurrentFuel(ItemStack stack) {
        return ArtronFuelNbt.read(stack.getOrCreateNbt(), this.getFuelKey(), this.getMaxFuel(stack));
    }

    default void setCurrentFuel(double amount, ItemStack stack) {
        ArtronFuelNbt.write(stack.getOrCreateNbt(), this.getFuelKey(), amount, this.getMaxFuel(stack));
    }

    /**
     * Inserts as much of {@code amount} as possible.
     *
     * @return the amount that did not fit
     */
    default double addFuel(double amount, ItemStack stack) {
        double requested = sanitizeAmount(amount);
        double current = this.getCurrentFuel(stack);
        double maximum = sanitizeCapacity(this.getMaxFuel(stack));
        double accepted = Math.min(requested, maximum - current);

        if (accepted > 0)
            this.setCurrentFuel(current + accepted, stack);

        return requested - accepted;
    }

    /**
     * Inserts as much of {@code amount} as possible.
     *
     * @return the amount actually inserted
     */
    default double insertFuel(double amount, ItemStack stack) {
        double requested = sanitizeAmount(amount);
        double remainder = clampFuel(this.addFuel(requested, stack), requested);

        return requested - remainder;
    }

    /**
     * Extracts up to {@code amount}.
     *
     * @return the amount actually extracted
     */
    default double extractFuel(double amount, ItemStack stack) {
        double requested = sanitizeAmount(amount);
        double current = this.getCurrentFuel(stack);
        double extracted = Math.min(requested, current);

        if (extracted > 0)
            this.setCurrentFuel(current - extracted, stack);

        return extracted;
    }

    /**
     * Compatibility wrapper for callers compiled against the original void method.
     */
    default void removeFuel(double amount, ItemStack stack) {
        this.extractFuel(amount, stack);
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

    private static double sanitizeAmount(double amount) {
        return Double.isFinite(amount) && amount > 0 ? amount : 0;
    }

    private static double sanitizeCapacity(double capacity) {
        return Double.isFinite(capacity) && capacity > 0 ? capacity : 0;
    }

    private static double clampFuel(double amount, double maximum) {
        if (!Double.isFinite(amount) || amount <= 0)
            return 0;

        return Math.min(amount, maximum);
    }
}

/**
 * Package-private NBT boundary shared by the item API and its plain-JVM tests. Keeping registry
 * bootstrapping outside this class also makes malformed legacy values deterministic.
 */
final class ArtronFuelNbt {
    private ArtronFuelNbt() {
    }

    static double read(NbtCompound nbt, String key, double capacity) {
        double stored = nbt.contains(key) ? nbt.getDouble(key) : 0;
        double current = clamp(stored, capacity);

        if (!nbt.contains(key) || Double.compare(stored, current) != 0)
            nbt.putDouble(key, current);

        return current;
    }

    static void write(NbtCompound nbt, String key, double amount, double capacity) {
        nbt.putDouble(key, clamp(amount, capacity));
    }

    private static double clamp(double amount, double capacity) {
        double maximum = Double.isFinite(capacity) && capacity > 0 ? capacity : 0;

        if (!Double.isFinite(amount) || amount <= 0)
            return 0;

        return Math.min(amount, maximum);
    }
}
