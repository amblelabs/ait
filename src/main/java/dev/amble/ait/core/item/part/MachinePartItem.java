package dev.amble.ait.core.item.part;

import java.util.function.Supplier;

import net.minecraft.item.Item;

import dev.amble.ait.core.AITItems;

public class MachinePartItem extends AbstractMachinePartItem<MachinePartItem.Type> {

    public MachinePartItem(Type type, Settings settings) {
        super(type, settings);
    }

    public enum Type {
        ORTHOGONAL_ENGINE_FILTER(() -> AITItems.ORTHOGONAL_ENGINE_FILTER), TRANSWARP_RESONATOR(() -> AITItems.TRANSWARP_RESONATOR), PHOTON_ACCELERATOR(
                () -> AITItems.PHOTON_ACCELERATOR), HYPERION_CORE_SHAFT(() -> AITItems.HYPERION_CORE_SHAFT);

        private final Supplier<Item> toItem;

        Type(Supplier<Item> toItem) {
            this.toItem = toItem;
        }

        public Item item() {
            return this.toItem.get();
        }
    }
}
