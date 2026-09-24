package dev.amble.ait.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;

class ArtronHolderTest {

    private static final double EPSILON = 0.000_001;

    @Test
    void insertionReportsAcceptedAndRemainderWithoutOverfilling() {
        TestHolder holder = new TestHolder(4900, 5000);

        assertEquals(1400, holder.addFuel(1500), EPSILON);
        assertEquals(5000, holder.getCurrentFuel(), EPSILON);

        holder.setCurrentFuel(4900);
        assertEquals(100, holder.insertFuel(1500), EPSILON);
        assertEquals(5000, holder.getCurrentFuel(), EPSILON);
    }

    @Test
    void boundaryTransfersConserveFuel() {
        TestHolder holder = new TestHolder(0, 100);

        assertEquals(40, holder.insertFuel(40), EPSILON);
        assertEquals(40, holder.getCurrentFuel(), EPSILON);

        assertEquals(0, holder.addFuel(60), EPSILON);
        assertEquals(100, holder.getCurrentFuel(), EPSILON);
        assertEquals(0, holder.insertFuel(1), EPSILON);

        assertEquals(40, holder.extractFuel(40), EPSILON);
        assertEquals(60, holder.getCurrentFuel(), EPSILON);
        assertEquals(60, holder.extractFuel(60), EPSILON);
        assertEquals(0, holder.getCurrentFuel(), EPSILON);
        assertEquals(0, holder.extractFuel(1), EPSILON);
        assertEquals(0, holder.insertFuel(0), EPSILON);
    }

    @Test
    void extractionReportsActualAmountAndKeepsVoidWrapper() {
        TestHolder holder = new TestHolder(20, 5000);

        assertEquals(20, holder.extractFuel(40), EPSILON);
        assertEquals(0, holder.getCurrentFuel(), EPSILON);

        holder.setCurrentFuel(100);
        holder.removeFuel(40);
        assertEquals(60, holder.getCurrentFuel(), EPSILON);
    }

    @Test
    void invalidAmountsAreNoOpsAndLegacyValuesAreNormalized() {
        TestHolder holder = new TestHolder(100, 5000);

        assertEquals(0, holder.insertFuel(-10), EPSILON);
        assertEquals(0, holder.insertFuel(Double.NaN), EPSILON);
        assertEquals(0, holder.extractFuel(Double.POSITIVE_INFINITY), EPSILON);
        assertEquals(100, holder.getCurrentFuel(), EPSILON);

        holder.fuel = 6000;
        assertEquals(0, holder.addFuel(0), EPSILON);
        assertEquals(5000, holder.getCurrentFuel(), EPSILON);
    }

    @Test
    void itemHolderUsesTheSameTransferContract() {
        TestItemHolder holder = new TestItemHolder(5000);
        ItemStack stack = null;
        holder.setCurrentFuel(4900, stack);

        assertEquals(1400, holder.addFuel(1500, stack), EPSILON);
        assertEquals(5000, holder.getCurrentFuel(stack), EPSILON);

        holder.setCurrentFuel(20, stack);
        assertEquals(20, holder.extractFuel(40, stack), EPSILON);
        assertEquals(0, holder.getCurrentFuel(stack), EPSILON);
    }

    @Test
    void itemHolderNormalizesMissingAndInvalidFuelNbt() {
        NbtCompound nbt = new NbtCompound();

        assertFalse(nbt.contains(ArtronHolderItem.FUEL_KEY));
        assertEquals(0, ArtronFuelNbt.read(nbt, ArtronHolderItem.FUEL_KEY, 5000), EPSILON);
        assertTrue(nbt.contains(ArtronHolderItem.FUEL_KEY));

        nbt.putDouble(ArtronHolderItem.FUEL_KEY, 6000);
        assertEquals(5000, ArtronFuelNbt.read(nbt, ArtronHolderItem.FUEL_KEY, 5000), EPSILON);
        assertEquals(5000, nbt.getDouble(ArtronHolderItem.FUEL_KEY), EPSILON);

        nbt.putDouble(ArtronHolderItem.FUEL_KEY, -10);
        assertEquals(0, ArtronFuelNbt.read(nbt, ArtronHolderItem.FUEL_KEY, 5000), EPSILON);

        nbt.putDouble(ArtronHolderItem.FUEL_KEY, Double.NaN);
        assertEquals(0, ArtronFuelNbt.read(nbt, ArtronHolderItem.FUEL_KEY, 5000), EPSILON);

        nbt.putDouble(ArtronHolderItem.FUEL_KEY, Double.POSITIVE_INFINITY);
        assertEquals(0, ArtronFuelNbt.read(nbt, ArtronHolderItem.FUEL_KEY, 5000), EPSILON);
        assertTrue(nbt.contains("fuel"));

        ArtronFuelNbt.write(nbt, ArtronHolderItem.FUEL_KEY, 7500, 5000);
        assertEquals(5000, nbt.getDouble(ArtronHolderItem.FUEL_KEY), EPSILON);
    }

    private static final class TestHolder implements ArtronHolder {
        private double fuel;
        private final double maximum;

        private TestHolder(double fuel, double maximum) {
            this.fuel = fuel;
            this.maximum = maximum;
        }

        @Override
        public double getCurrentFuel() {
            return this.fuel;
        }

        @Override
        public void setCurrentFuel(double fuel) {
            this.fuel = fuel;
        }

        @Override
        public double getMaxFuel() {
            return this.maximum;
        }
    }

    private static final class TestItemHolder implements ArtronHolderItem {
        private final double maximum;
        private double fuel;

        private TestItemHolder(double maximum) {
            this.maximum = maximum;
        }

        @Override
        public double getMaxFuel(ItemStack stack) {
            return this.maximum;
        }

        @Override
        public double getCurrentFuel(ItemStack stack) {
            return this.fuel;
        }

        @Override
        public void setCurrentFuel(double amount, ItemStack stack) {
            this.fuel = amount;
        }
    }
}
