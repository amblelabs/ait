package dev.amble.ait.core.tardis.handler;

import net.minecraft.item.ItemStack;

import dev.amble.ait.api.KeyedTardisComponent;
import dev.amble.ait.core.AITItems;
import dev.amble.ait.data.properties.Property;
import dev.amble.ait.data.properties.Value;

public class ExtraHandler extends KeyedTardisComponent {
    private static final Property<ItemStack> SET_REFRESHMENT_ITEM = new Property<>(Property.Type.ITEM_STACK, "set_refreshment_item", (ItemStack) null);

    private final Value<ItemStack> setRefreshmentItemValue = SET_REFRESHMENT_ITEM.create(this);

    public ExtraHandler() {
        super(Id.EXTRAS);
    }

    @Override
    public void onCreate() {
        this.setRefreshmentItem(AITItems.COFFEE.getDefaultStack());
    }

    @Override
    public void onLoaded() {
        setRefreshmentItemValue.of(this, SET_REFRESHMENT_ITEM);
    }

    public ItemStack getRefreshmentItem() {
        ItemStack itemStack = setRefreshmentItemValue.get();
        return itemStack != null ? itemStack : ItemStack.EMPTY;
    }

    public void setRefreshmentItem(ItemStack item) {
        setRefreshmentItemValue.set(item);
    }
}
