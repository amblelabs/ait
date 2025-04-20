package dev.amble.ait.core.tardis.handler;

import java.util.function.Consumer;

import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import dev.amble.ait.AITMod;
import dev.amble.ait.api.tardis.KeyedTardisComponent;
import dev.amble.ait.core.AITItems;
import dev.amble.ait.core.drinks.Drink;
import dev.amble.ait.core.drinks.DrinkRegistry;
import dev.amble.ait.core.drinks.DrinkUtil;
import dev.amble.ait.data.properties.Property;
import dev.amble.ait.data.properties.Value;

public class ExtraHandler extends KeyedTardisComponent {
    private static final Property<ItemStack> SET_REFRESHMENT_ITEM = new Property<>(Property.Type.ITEM_STACK, "set_refreshment_item", (ItemStack) null);
    private static final Property<ItemStack> INSERTED_DISC = new Property<>(Property.Type.ITEM_STACK, "inserted_disc", (ItemStack) null);
    private static final Property<ItemStack> CONSOLE_HAMMER = new Property<>(Property.Type.ITEM_STACK, "console_hammer",
            (ItemStack) null);

    private final Value<ItemStack> consoleHammer = CONSOLE_HAMMER.create(this);
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
        consoleHammer.of(this, CONSOLE_HAMMER);
    }

    public ItemStack getConsoleHammer() {
        return this.consoleHammer.get();
    }

    public ItemStack takeConsoleHammer() {
        return takeAnyHammer(this.consoleHammer);
    }
    public boolean consoleHammerInserted() {
        return this.consoleHammer.get() != null && !this.consoleHammer.get().isEmpty();
    }

    public void insertConsoleHammer(ItemStack sonic, BlockPos consolePos) {
        insertAnyHammer(this.consoleHammer, sonic,
                stack -> spawnItem(tardis.asServer().getInteriorWorld(), consolePos, stack));
    }

    private static ItemStack takeAnyHammer(Value<ItemStack> value) {
        ItemStack result = value.get();
        value.set((ItemStack) null);

        return result;
    }

    private static void insertAnyHammer(Value<ItemStack> value, ItemStack sonic, Consumer<ItemStack> spawner) {
        value.flatMap(stack -> {
            if (stack != null)
                spawner.accept(stack);

            return sonic;
        });
    }

    public static void spawnItem(World world, BlockPos pos, ItemStack sonic) {
        ItemEntity entity = new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), sonic);
        world.spawnEntity(entity);
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
