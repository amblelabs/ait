package dev.amble.ait.core.item;

import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;

public class RenderableArmorItem extends ArmorItem {

    private final boolean hasCustomRendering;

    public RenderableArmorItem(ArmorMaterial material, Type type, Settings settings, boolean hasCustomRendering) {
        super(material, type, settings);
        this.hasCustomRendering = hasCustomRendering;
    }

    public boolean hasCustomRendering() {
        return hasCustomRendering;
    }
}
