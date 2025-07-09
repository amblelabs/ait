package dev.amble.ait.tardis.v2.handler;

import dev.amble.ait.AITMod;
import dev.amble.ait.api.ArtronHolderItem;
import dev.amble.ait.api.tardis.v2.handler.Resolve;
import dev.amble.ait.api.tardis.v2.handler.THandler;
import dev.amble.ait.core.item.SonicItem;
import dev.amble.ait.api.tardis.v2.data.properties.Value;
import dev.amble.ait.registry.impl.SonicRegistry;
import dev.amble.ait.tardis.handler.ExtraHandler;
import dev.amble.ait.tardis.manager.ServerTardisManager;
import dev.amble.ait.tardis.v2.Tardis;
import dev.amble.ait.tardis.v2.data.*;
import dev.amble.ait.tardis.v2.event.ServerEvents;
import dev.amble.lib.data.CachedDirectedGlobalPos;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import java.util.function.Consumer;

public class SonicHandler implements THandler, ArtronHolderItem, ServerEvents {

    public static final Identifier CHANGE_SONIC = AITMod.id("change_sonic");

    @Resolve
    private final FuelHandler fuelHandler = handler();

    @Resolve
    private final RepairHandler repairHandler = handler();

    public SonicHandler() {
        ServerPlayNetworking.registerGlobalReceiver(CHANGE_SONIC,
                ServerTardisManager.receiveTardis((tardis, server, player, handler, buf, responseSender) -> {
                    Identifier id = buf.readIdentifier();
                    if (!tardis.isUnlocked(SonicRegistry.getInstance().get(id))) return;

                    SonicItem.setSchema(tardis.sonic().getConsoleSonic(), id);
                }));
    }

    public void insertConsoleSonic(Tardis tardis, ItemStack sonic, BlockPos consolePos) {
        SonicData sonics = tardis.resolve(SonicData.ID);
        DesktopData desktop = tardis.resolve(DesktopData.ID);

        insertAnySonic(sonics.consoleSonic(), sonic, stack ->
                ExtraHandler.spawnItem(desktop.getServerWorld(), consolePos, stack));
    }

    public void insertExteriorSonic(Tardis tardis, ItemStack sonic) {
        SonicData sonics = tardis.resolve(SonicData.ID);
        TravelData travel = tardis.resolve(TravelData.ID);

        CachedDirectedGlobalPos pos = travel.position().get();

        insertAnySonic(sonics.exteriorSonic(), sonic, stack ->
                ExtraHandler.spawnItem(pos.getWorld(), pos.getPos(), stack));
    }

    public ItemStack takeConsoleSonic(Tardis tardis) {
        SonicData sonics = tardis.resolve(SonicData.ID);
        return takeAnySonic(sonics.consoleSonic());
    }

    public ItemStack takeExteriorSonic(Tardis tardis) {
        SonicData sonics = tardis.resolve(SonicData.ID);
        return takeAnySonic(sonics.exteriorSonic());
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

    @Override
    public void event$tardisTick(Tardis tardis, MinecraftServer server) {
        if (server.getTicks() % 10 != 0)
            return;

        SonicData sonics = tardis.resolve(SonicData.ID);

        ItemStack consoleSonic = sonics.consoleSonic().get();
        ItemStack exteriorSonic = sonics.exteriorSonic().get();

        if (consoleSonic != null) {
            if (this.hasMaxFuel(consoleSonic))
                return;

            if (!tardis.ifHasOrElse(FuelData.ID,
                    f -> f.power().get(), true))
                return;

            this.addFuel(10, consoleSonic);
            fuelHandler.removeFuel(tardis, 10);
        }

        if (exteriorSonic != null) {
            RepairData repair = tardis.resolve(RepairData.ID);

            int repairTicks = repairHandler.getRepairTicks();

            if (repair.state().get() != RepairData.State.NORMAL)
                return;

            repairHandler.setRepairTicks(tardis, repairTicks <= 0 ? 0 : repairTicks - 5);
            this.removeFuel(10, exteriorSonic);
        }
    }

    @Override
    public double getMaxFuel(ItemStack stack) {
        return SonicItem.MAX_FUEL;
    }
}
