package dev.amble.ait.core.tardis.handler;

import dev.amble.ait.AITMod;
import dev.amble.ait.api.ArtronHolderItem;
import dev.amble.ait.api.tardis.KeyedTardisComponent;
import dev.amble.ait.api.tardis.TardisTickable;
import dev.amble.ait.core.item.SonicItem;
import dev.amble.ait.core.tardis.ServerTardis;
import dev.amble.ait.core.tardis.manager.ServerTardisManager;
import dev.amble.ait.data.properties.Property;
import dev.amble.ait.data.properties.Value;
import dev.amble.ait.registry.impl.SonicRegistry;
import dev.amble.lib.data.CachedDirectedGlobalPos;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.function.Consumer;

public class SonicHandler extends KeyedTardisComponent implements ArtronHolderItem, TardisTickable {

    public static final Identifier CHANGE_SONIC = AITMod.id("change_sonic");

    private static final Property<ItemStack> CONSOLE_SONIC = new Property<>(Property.ITEM_STACK, "console_sonic");
    private static final Property<ItemStack> EXTERIOR_SONIC = new Property<>(Property.ITEM_STACK, "exterior_sonic");

    private final Value<ItemStack> consoleSonic = CONSOLE_SONIC.create(this); // The current sonic in the console
    private final Value<ItemStack> exteriorSonic = EXTERIOR_SONIC.create(this); // The current sonic in the exterior's
                                                                                // keyhole
    static {
        ServerPlayNetworking.registerGlobalReceiver(CHANGE_SONIC,
                ServerTardisManager.receiveTardis((tardis, server, player, handler, buf, responseSender) -> {
                    Identifier id = buf.readIdentifier();
                    if (!tardis.isUnlocked(SonicRegistry.getInstance().get(id))) return;

                    SonicItem.setSchema(tardis.sonic().getConsoleSonic(), id);
                }));
    }

    public SonicHandler() {
        super(Id.SONIC);
    }

    @Override
    public void onLoaded() {
        consoleSonic.of(this, CONSOLE_SONIC);
        exteriorSonic.of(this, EXTERIOR_SONIC);
    }

    public ItemStack getConsoleSonic() {
        return this.consoleSonic.get();
    }

    public ItemStack getExteriorSonic() {
        return this.exteriorSonic.get();
    }

    public void insertConsoleSonic(ItemStack sonic, BlockPos consolePos) {
        insertAnySonic(this.consoleSonic, sonic,
                stack -> spawnItem(tardis.asServer().world(), consolePos, stack));
    }

    public void insertExteriorSonic(ItemStack sonic) {
        insertAnySonic(this.exteriorSonic, sonic, stack -> spawnItem(this.tardis.travel().position(), stack));
    }

    public ItemStack takeConsoleSonic() {
        return takeAnySonic(this.consoleSonic);
    }

    public ItemStack takeExteriorSonic() {
        return takeAnySonic(this.exteriorSonic);
    }

    private static ItemStack takeAnySonic(Value<ItemStack> value) {
        ItemStack result = value.get();
        value.set((ItemStack) null);

        return result;
    }

    private static void insertAnySonic(Value<ItemStack> value, ItemStack sonic, Consumer<ItemStack> spawner) {
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

    public static void spawnItem(CachedDirectedGlobalPos cached, ItemStack sonic) {
        spawnItem(cached.getWorld(), cached.getPos(), sonic);
    }

    @Override
    public double getMaxFuel(ItemStack stack) {
        return SonicItem.MAX_FUEL;
    }

    @Override
    public void tick(MinecraftServer server) {
        if (server.getTicks() % 10 != 0)
            return;

        ItemStack consoleSonic = this.consoleSonic.get();
        ItemStack exteriorSonic = this.exteriorSonic.get();

        if (consoleSonic != null) {
            if (this.hasMaxFuel(consoleSonic))
                return;

            // Safe to get as ^ that method runs the check for us
            ServerTardis tardis = this.tardis.asServer();

            if (!tardis.fuel().hasPower())
                return;

            this.addFuel(10, consoleSonic);
            tardis.fuel().removeFuel(10);
        }

        if (exteriorSonic != null) {
            ServerTardis tardis = this.tardis.asServer();

            TardisCrashHandler crash = tardis.crash();
            boolean isToxic = crash.isToxic();
            boolean isUnstable = crash.isUnstable();
            int repairTicks = crash.getRepairTicks();

            if (!isToxic && !isUnstable)
                return;

            crash.setRepairTicks(repairTicks <= 0 ? 0 : repairTicks - 5);

            // TODO
            /*
            if () {
                tardis.travel().position().getWorld().playSound(null, tardis.travel().position().getPos(),
                        AITSounds.SONIC_USE, SoundCategory.BLOCKS, 0.5f, 1f);
            }
             */

            this.removeFuel(10, exteriorSonic);
        }
    }
}
