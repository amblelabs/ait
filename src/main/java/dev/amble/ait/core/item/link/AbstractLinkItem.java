package dev.amble.ait.core.item.link;

import net.minecraft.item.Item;

public abstract class AbstractLinkItem extends Item {

    private final Type linkType;

    public AbstractLinkItem(Type type, Settings settings) {
        super(settings);
        this.linkType = type;
    }

    public Type getType() {
        return linkType;
    }

    public abstract float unitsPerTick(Type type);

    public enum Type {
        ARTRON, DATA, VORTEX
    }
}
