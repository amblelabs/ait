package dev.amble.ait.tardis.v2.handler;

import dev.amble.ait.api.tardis.v2.handler.Resolve;
import dev.amble.ait.api.tardis.v2.handler.THandler;
import dev.amble.ait.core.item.KeyItem;
import dev.amble.ait.core.world.RiftChunkManager;
import dev.amble.ait.core.world.TardisServerWorld;
import dev.amble.ait.tardis.util.TardisUtil;
import dev.amble.ait.tardis.v2.Tardis;
import dev.amble.ait.tardis.v2.data.*;
import dev.amble.ait.tardis.v2.event.ServerEvents;
import dev.amble.ait.tardis.v2.event.TardisEvents;
import dev.amble.lib.data.CachedDirectedGlobalPos;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;

public class FuelHandler implements THandler, ServerEvents {

    @Resolve
    private final TravelHandler travelHandler = handler();

    public FuelHandler() {
        TardisEvents.DEMAT.register(tardis -> tardis.fuel().refueling().get() ? TardisEvents.Interaction.FAIL : TardisEvents.Interaction.PASS);

        TardisEvents.USE_DOOR.register((tardis, interior, world, player, pos) -> {
            if (tardis.fuel().hasPower() || !tardis.door().locked() || player == null)
                return DoorHandler.InteractionResult.CONTINUE;

            ItemStack stack = player.getStackInHand(Hand.MAIN_HAND);

            // if holding a key and in siege mode and have an empty interior, disable siege
            // mode !!
            if (stack.getItem() instanceof KeyItem key && tardis.siege().isActive() && key.isOf(stack, tardis)
                    && TardisUtil.isInteriorEmpty(tardis.asServer())) {
                player.swingHand(Hand.MAIN_HAND);
                tardis.siege().setActive(false);

                tardis.door().interactLock(false, player, true);
            }

            // if holding an axe then break open the door RAHHH
            if (stack.getItem() instanceof AxeItem) {
                if (tardis.siege().isActive())
                    return DoorHandler.InteractionResult.CANCEL;

                player.swingHand(Hand.MAIN_HAND);
                stack.setDamage(stack.getDamage() - 1);

                if (pos != null)
                    world.playSound(null, pos, SoundEvents.ENTITY_ZOMBIE_BREAK_WOODEN_DOOR, SoundCategory.BLOCKS, 1f,
                            1f);

                interior.playSound(null, tardis.getDesktop().getDoorPos().getPos(),
                        SoundEvents.ENTITY_ZOMBIE_BREAK_WOODEN_DOOR, SoundCategory.BLOCKS);

                // forcefully unlock the tardis
                tardis.door().interactLock(false, player, true);
                tardis.door().openDoors();

                TardisEvents.FORCED_ENTRY.invoker().onForcedEntry(tardis, player);
                return DoorHandler.InteractionResult.SUCCESS;
            }

            return DoorHandler.InteractionResult.KNOCK;
        });
    }

    @Override
    public void event$tardisTick(Tardis tardis, MinecraftServer server) {
        if (server.getTicks() % 20 != 0)
            return;

        if (tardis.has(GrowthData.ID))
            return;

        TravelData data = tardis.resolve(TravelData.ID);

        switch (data.state().get()) {
            case LANDED -> this.tickIdle(tardis);
            case FLIGHT -> this.tickFlight(tardis);
            case MAT, DEMAT -> this.tickMat(tardis);
        }
    }

    public boolean isOutOfFuel(Tardis tardis) {
        return this.getFuel(tardis) <= 0;
    }

    public boolean isFull(Tardis tardis) {
        return this.getFuel(tardis) >= FuelData.MAX_FUEL;
    }

    public void removeFuel(Tardis tardis, double var) {
        double toRemove = this.getFuel(tardis) - var;

        if (toRemove < 0) {
            this.setFuel(tardis, 0);
            return;
        }

        this.setFuel(tardis, toRemove);
    }

    private double getFuel(Tardis tardis) {
        return tardis.resolve(FuelData.ID).fuel().get();
    }

    public void setFuel(Tardis tardis, double amount) {
        FuelData data = tardis.resolve(FuelData.ID);
        double prev = data.fuel().get();

        data.fuel().set(MathHelper.clamp(amount, 0, FuelData.MAX_FUEL));

        if (!this.isOutOfFuel(tardis) || prev == 0) return;

        if (tardis.has(EmergencyPowerData.ID)) {
            EmergencyPowerData emergencyPower = tardis.resolve(EmergencyPowerData.ID);

            if (!emergencyPower.isOutOfFuel()) {
                double fuel = emergencyPower.getCurrentFuel();
                emergencyPower.setCurrentFuel(0);

                this.setFuel(tardis, fuel);
                this.handle(TardisEvents.backupPowerUsed(tardis, fuel));
                return;
            }
        }

        this.handle(TardisEvents.outOfFuel(tardis));
        this.disablePower(tardis);
    }

    public double addFuel(Tardis tardis, double var) {
        if (tardis.has(EmergencyPowerData.ID)) {
            EmergencyPowerData powerData = tardis.resolve(EmergencyPowerData.ID);

            if (!powerData.isFull())
                return powerData.addFuel(var);
        }

        double previousFuel = this.getFuel(tardis);
        double toAdd = var + previousFuel;

        if (toAdd >= FuelData.MAX_FUEL) {
            this.setFuel(tardis, FuelData.MAX_FUEL);
            return toAdd - FuelData.MAX_FUEL;
        }

        this.setFuel(tardis, toAdd);
        return 0;
    }

    private void tickMat(Tardis tardis) {
        TravelData data = tardis.resolve(TravelData.ID);
        this.removeFuel(tardis, 20 * 5 * data.instability().get());
    }

    private void tickFlight(Tardis tardis) {
        FuelData fuel = tardis.resolve(FuelData.ID);
        TravelData travel = tardis.resolve(TravelData.ID);

        double cost = 20 * Math.pow(4, travel.speed().get()) * travel.instability().get();
        this.removeFuel(tardis, cost);

        if (!fuel.power().get())
            travelHandler.crash();
    }

    private void tickIdle(Tardis tardis) {
        FuelData fuelData = tardis.resolve(FuelData.ID);
        boolean refueling = fuelData.refueling().get();

        TravelData travel = tardis.resolve(TravelData.ID);

        if (refueling && this.getFuel(tardis) < FuelData.MAX_FUEL) {
            CachedDirectedGlobalPos pos = travel.position().get();
            ServerWorld world = pos.getWorld();

            RiftChunkManager manager = RiftChunkManager.getInstance(world);
            ChunkPos chunk = new ChunkPos(pos.getPos());

            double toAdd = 7;

            if (manager.getArtron(chunk) > 0 && !TardisServerWorld.isTardisDimension(world)) {
                manager.removeFuel(chunk, 2);
                toAdd += 2;
            }

            this.addFuel(tardis, 20 * toAdd);
        }

        if (!refueling && fuelData.power().get()) {
            double instability = travel.instability().get();
            this.removeFuel(tardis, 20d * 0.25d * instability < 1 ? 1 : instability);
        }
    }

    public void togglePower(Tardis tardis) {
        FuelData data = tardis.resolve(FuelData.ID);

        if (data.power().get()) {
            this.disablePower(tardis);
        } else {
            this.enablePower(tardis);
        }
    }

    public void disablePower(Tardis tardis) {
        FuelData data = tardis.resolve(FuelData.ID);

        if (!data.power().get())
            return;

        data.power().set(false);
        this.handle(TardisEvents.disablePower(tardis));
    }

    public void enablePower(Tardis tardis) {
        FuelData fuel = tardis.resolve(FuelData.ID);

        if (fuel.power().get())
            return;

        if (this.getFuel(tardis) <= (0.01 * FuelData.MAX_FUEL))
            return; // cant enable power if not enough fuel

        // check if there's siege data. if there is, check if it's active,
        //      otherwise default to false
        if (tardis.ifHasOrElse(SiegeData.ID, SiegeData::enabled, false))
            return;

        // checks if there's an engine system. if there is, then check if the engine exists,
        //      otherwise assume that tardis comes with "batteries included".
        if (!tardis.ifHasOrElse(EngineData.ID, EngineData::exists, true))
            return;

        fuel.power().set(true);
        this.handle(TardisEvents.enablePower(tardis));
    }
}
