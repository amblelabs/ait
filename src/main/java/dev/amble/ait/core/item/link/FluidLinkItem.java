package dev.amble.ait.core.item.link;

public class FluidLinkItem extends AbstractLinkItem {

    public FluidLinkItem(Type type, Settings settings) {
        super(type, settings);
    }

    @Override
    public float unitsPerTick(Type type) {
        return switch (type) {
            case DATA -> 1.0f;
            case ARTRON -> 1.5f;
            case VORTEX -> 2.0f;
        };
    }
}
