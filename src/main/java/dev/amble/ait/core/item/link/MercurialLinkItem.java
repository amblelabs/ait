package dev.amble.ait.core.item.link;

public class MercurialLinkItem extends FluidLinkItem {

    public MercurialLinkItem(Type type, Settings settings) {
        super(type, settings);
    }

    @Override
    public float unitsPerTick(Type type) {
        float multiplier = switch (type) {
            case DATA -> 5;
            case ARTRON -> 1.5f;
            case VORTEX -> 2;
        };

        return super.unitsPerTick(type) * multiplier;
    }
}
