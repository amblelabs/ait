package dev.amble.ait.core.blockentities;

import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

import dev.amble.ait.api.tardis.link.v2.block.InteriorLinkableBlockEntity;
import dev.amble.ait.core.AITBlockEntityTypes;
import dev.amble.ait.core.drinks.DrinkRegistry;

public class FoodMachineBlockEntity extends InteriorLinkableBlockEntity {
    private Mode currentMode = Mode.FOOD_CUBES;
    private int currentIndex = 0;
    private long lastDrinkTime = 0;
    private ItemStack selectedItem;

    public FoodMachineBlockEntity(BlockPos pos, BlockState state) {
        super(AITBlockEntityTypes.FOOD_MACHINE_BLOCK_ENTITY_TYPE, pos, state);
    }

    public enum Mode {
        FOOD_CUBES(139),
        DRINKS(193),
        OVERCHARGED_FOOD_CUBES(437);

        private final int fuelCost;

        Mode(int fuelCost) {
            this.fuelCost = fuelCost;
        }

        public int getFuelCost(){
            return fuelCost;
        }
    }

    public void eatFuel() {
        if (!this.isLinked()) return;
        this.tardis().get().fuel().removeFuel(this.getMode().getFuelCost());
    }

    public void setMode(Mode mode) {
        this.currentMode = mode;
    }

    public Mode getMode() {
        return currentMode;
    }

    public int getCurrentIndex(){
        return this.currentIndex;
    }

    public void setCurrentIndex(int value) {
        int size = DrinkRegistry.getInstance().size();
        if (size == 0) {
            this.currentIndex = 0;
        } else {
            this.currentIndex = value % size;
        }
    }

    public boolean isPoweredOn(){
        return this.isLinked() && this.tardis().get().fuel().hasPower();
    }
    public ItemStack getSelectedItem() {
        return this.selectedItem;
    }
    public void setSelectedItem(ItemStack item){
        this.selectedItem = item;
    }

    public void setLastDrinkTime(long value){
        this.lastDrinkTime = value;
    }

    public long getLastDrinkTime(){
        return this.lastDrinkTime;
    }
}
