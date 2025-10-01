package dev.amble.ait.core.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class GlintItem extends Item {
    public GlintItem(Settings settings) {
        super(settings);
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return true;
    }
}
