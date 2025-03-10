package dev.amble.ait.core.tardis.handler;

import net.minecraft.item.ItemStack;

import dev.amble.ait.AITMod;
import dev.amble.ait.api.KeyedTardisComponent;
import dev.amble.ait.core.AITItems;
import dev.amble.ait.core.drinks.Drink;
import dev.amble.ait.core.drinks.DrinkRegistry;
import dev.amble.ait.core.drinks.DrinkUtil;
import dev.amble.ait.data.properties.Property;
import dev.amble.ait.data.properties.Value;

public class ExtraHandler extends KeyedTardisComponent {
    private static final Property<ItemStack> SET_REFRESHMENT_ITEM = new Property<>(Property.Type.ITEM_STACK, "set_refreshment_item", (ItemStack) null);
    private static final Property<ItemStack> INSERTED_DISC = new Property<>(Property.Type.ITEM_STACK, "inserted_disc", (ItemStack) null);

    private final Value<ItemStack> setRefreshmentItemValue = SET_REFRESHMENT_ITEM.create(this);
    private final Value<ItemStack> setInsertedDiscValue = INSERTED_DISC.create(this);

    public ExtraHandler() {
        super(Id.EXTRAS);
    }

    @Override
    public void onCreate() {
        Drink drink = DrinkRegistry.getInstance().get(AITMod.id("coffee"));
        ItemStack stack = new ItemStack(AITItems.MUG);
        DrinkUtil.setDrink(stack, drink);
    }

    @Override
    public void onLoaded() {
        setRefreshmentItemValue.of(this, SET_REFRESHMENT_ITEM);
        setInsertedDiscValue.of(this, INSERTED_DISC);
    }

    public ItemStack getRefreshmentItem() {
        ItemStack itemStack = setRefreshmentItemValue.get();
        return itemStack != null ? itemStack : ItemStack.EMPTY;
    }

    public void setRefreshmentItem(ItemStack item) {
        setRefreshmentItemValue.set(item);
    }

    public ItemStack getInsertedDisc() {
        ItemStack itemStack = setInsertedDiscValue.get();
        return itemStack != null ? itemStack : ItemStack.EMPTY;
    }

    public void setInsertedDisc(ItemStack item) {
        setInsertedDiscValue.set(item);
    }

}
