package dev.amble.ait.tardis.v2.data;

import dev.amble.ait.AITMod;
import dev.amble.ait.api.tardis.v2.data.TData;
import dev.amble.ait.api.tardis.v2.data.TDataHolder;
import dev.amble.ait.api.tardis.v2.data.properties.Property;
import dev.amble.ait.api.tardis.v2.data.properties.Value;
import net.minecraft.item.ItemStack;

public class SonicData extends TData.Props<SonicData> {

    public static final TDataHolder<SonicData> ID = new TDataHolder.PropertyBacked<>(
            AITMod.id("sonic"), SonicData.class);

    private static final Property<ItemStack> CONSOLE_SONIC = new Property<>(Property.ITEM_STACK, "console_sonic");
    private static final Property<ItemStack> EXTERIOR_SONIC = new Property<>(Property.ITEM_STACK, "exterior_sonic");

    private final Value<ItemStack> consoleSonic = CONSOLE_SONIC.create(this); // The current sonic in the console
    private final Value<ItemStack> exteriorSonic = EXTERIOR_SONIC.create(this); // The current sonic in the exterior's

    @Override
    public void onAttach() {
       super.onAttach();

       consoleSonic.of(this, CONSOLE_SONIC);
       exteriorSonic.of(this, EXTERIOR_SONIC);
    }

    public Value<ItemStack> consoleSonic() {
        return consoleSonic;
    }

    public Value<ItemStack> exteriorSonic() {
        return exteriorSonic;
    }

    @Override
    public TDataHolder<SonicData> holder() {
        return ID;
    }
}
