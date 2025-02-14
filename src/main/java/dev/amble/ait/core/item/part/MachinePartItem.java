package dev.amble.ait.core.item.part;

import java.util.function.Supplier;

import net.minecraft.item.Item;

import dev.amble.ait.core.AITItems;

public class MachinePartItem extends AbstractMachinePartItem<MachinePartItem.Type> {

    public MachinePartItem(Type type, Settings settings) {
        super(type, settings);
    }

    public enum Type {
        CONDENSER(() -> AITItems.CONDENSER), MANIPULATOR(() -> AITItems.MANIPULATOR), BULB(
                () -> AITItems.BULB), INDUCTOR(() -> AITItems.INDUCTOR);

        private final Supplier<Item> toItem;

        Type(Supplier<Item> toItem) {
            this.toItem = toItem;
        }

        public Item item() {
            return this.toItem.get();
        }
    }
}
