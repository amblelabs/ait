package dev.amble.ait.core.tardis.handler;

import dev.amble.ait.api.ArtronHolderItem;
import dev.amble.ait.api.tardis.KeyedTardisComponent;
import dev.amble.ait.api.tardis.TardisTickable;
import dev.amble.ait.core.tardis.ServerTardis;
import dev.amble.ait.data.properties.Property;
import dev.amble.ait.data.properties.Value;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.function.Consumer;

public class ButlerHandler extends KeyedTardisComponent implements ArtronHolderItem, TardisTickable {

    private static final Property<ItemStack> HANDLES = new Property<>(Property.ITEM_STACK, "handles",
            (ItemStack) null);

    private final Value<ItemStack> handles = HANDLES.create(this); // The current handles in the console

    public ButlerHandler() {
        super(Id.BUTLER);
    }

    @Override
    public void onLoaded() {
        handles.of(this, HANDLES);
    }

    public ItemStack getHandles() {
        return this.handles.get();
    }

    public void insertHandles(ItemStack handlesMyBoy, BlockPos consolePos) {
        insertAnyHandles(this.handles, handlesMyBoy,
                stack -> spawnItem(tardis.asServer().world(), consolePos, stack));
    }

    public ItemStack takeHandles() {
        return takeAnyHandles(this.handles);
    }

    private static ItemStack takeAnyHandles(Value<ItemStack> value) {
        ItemStack result = value.get();
        value.set((ItemStack) null);

        return result;
    }

    private static void insertAnyHandles(Value<ItemStack> value, ItemStack handles, Consumer<ItemStack> spawner) {
        value.flatMap(stack -> {
            if (stack != null)
                spawner.accept(stack);

            return handles;
        });
    }

    public static void spawnItem(World world, BlockPos pos, ItemStack handles) {
        ItemEntity entity = new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), handles);
        world.spawnEntity(entity);
    }

    @Override
    public double getMaxFuel(ItemStack stack) {
        return 0;
    }

    @Override
    public void tick(MinecraftServer server) {
        if (server.getTicks() % 10 != 0)
            return;

        ItemStack handlesItem = this.handles.get();

        if (handlesItem != null) {
            if (this.hasMaxFuel(handlesItem))
                return;

            // Safe to get as ^ that method runs the check for us
            ServerTardis tardis = this.tardis.asServer();

            if (!tardis.fuel().hasPower())
                return;

            this.addFuel(10, handlesItem);
            tardis.fuel().removeFuel(10);
        }
    }
}
