package dev.amble.ait.core.tardis.handler;

import dev.amble.lib.data.CachedDirectedGlobalPos;

import net.minecraft.block.BlockState;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import dev.amble.ait.api.ArtronHolder;
import dev.amble.ait.api.tardis.KeyedTardisComponent;
import dev.amble.ait.api.tardis.TardisEvents;
import dev.amble.ait.api.tardis.TardisTickable;
import dev.amble.ait.core.AITSounds;
import dev.amble.ait.core.blocks.ExteriorBlock;
import dev.amble.ait.core.engine.impl.EmergencyPower;
import dev.amble.ait.core.engine.impl.EngineSystem;
import dev.amble.ait.core.item.KeyItem;
import dev.amble.ait.core.tardis.handler.travel.TravelHandler;
import dev.amble.ait.core.tardis.handler.travel.TravelHandlerBase;
import dev.amble.ait.core.tardis.util.TardisUtil;
import dev.amble.ait.core.world.RiftChunkManager;
import dev.amble.ait.core.world.TardisServerWorld;
import dev.amble.ait.data.properties.bool.BoolProperty;
import dev.amble.ait.data.properties.bool.BoolValue;
import dev.amble.ait.data.properties.dbl.DoubleProperty;
import dev.amble.ait.data.properties.dbl.DoubleValue;

public class FuelHandler extends KeyedTardisComponent implements ArtronHolder, TardisTickable {
    public static final double TARDIS_MAX_FUEL = 50000;

    private static final DoubleProperty FUEL = new DoubleProperty("fuel", 1000d);
    private static final BoolProperty REFUELING = new BoolProperty("refueling", false);
    private static final BoolProperty POWER = new BoolProperty("power", false);

    private final DoubleValue fuel = FUEL.create(this);
    private final BoolValue refueling = REFUELING.create(this);
    private final BoolValue power = POWER.create(this);

    static {
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

    public FuelHandler() {
        super(Id.FUEL);
    }

    @Override
    public void onLoaded() {
        refueling.of(this, REFUELING);
        fuel.of(this, FUEL);
        power.of(this, POWER);
    }

    @Override
    public void tick(MinecraftServer server) {
        if (server.getTicks() % 20 != 0)
            return;

        TravelHandler travel = this.tardis().travel();
        TravelHandlerBase.State state = travel.getState();

        switch (state) {
            case LANDED -> this.tickIdle();
            case FLIGHT -> this.tickFlight();
            case MAT, DEMAT -> this.tickMat();
        }
    }

    @Override
    public double getCurrentFuel() {
        return fuel.get();
    }

    @Override
    public void setCurrentFuel(double fuel) {
        double prev = this.getCurrentFuel();
        this.fuel.set(MathHelper.clamp(fuel, 0, this.getMaxFuel()));

        if (this.isOutOfFuel() && prev != 0) {
            EmergencyPower backup = this.tardis().subsystems().emergency();
            if (backup.hasBackupPower()) {
                this.setCurrentFuel(backup.getCurrentFuel());
                backup.setCurrentFuel(0);
                TardisEvents.USE_BACKUP_POWER.invoker().onUse(this.tardis(), this.getCurrentFuel());
                return;
            }

            TardisEvents.OUT_OF_FUEL.invoker().onNoFuel(this.tardis);
        }
    }

    @Override
    public double addFuel(double var) {
        EmergencyPower backup = this.tardis().subsystems().emergency();
        if (backup.isEnabled() && !backup.isFull()) {
            return backup.addFuel(var);
        }

        return ArtronHolder.super.addFuel(var);
    }

    @Override
    public double getMaxFuel() {
        return TARDIS_MAX_FUEL;
    }

    private void tickMat() {
        if (tardis.isGrowth())
            return;

        this.removeFuel(20 * 5 * tardis.travel().instability());
    }

    public static double getPerTickFuelCost(int speed, int instability) {
        return speed + instability - 1;
    }

    public static double getPerTickFuelCost(TravelHandler travel){
        return getPerTickFuelCost(Math.max(travel.speed(), 1), travel.instability());
    }

    private void tickFlight() {
        if (tardis.isGrowth())
            return;

        TravelHandler travel = this.tardis.travel();
        this.removeFuel(20 * FuelHandler.getPerTickFuelCost(travel));

        if (!tardis.fuel().hasPower())
            travel.crash();
    }

    private void tickIdle() {
        if (this.refueling().get() && this.getCurrentFuel() < FuelHandler.TARDIS_MAX_FUEL) {
            TravelHandler travel = tardis.travel();

            CachedDirectedGlobalPos pos = travel.position();
            ServerWorld world = pos.getWorld();

            RiftChunkManager manager = RiftChunkManager.getInstance(world);
            ChunkPos chunk = new ChunkPos(pos.getPos());

            double toAdd = 7;

            if (manager.getArtron(chunk) > 0 && !TardisServerWorld.isTardisDimension(world)) {
                manager.removeFuel(chunk, 2);
                toAdd += 2;
            }

            this.addFuel(20 * toAdd);
        }

        if (!this.refueling().get() && tardis.fuel().hasPower() && !tardis.isGrowth()) {
            double instability = tardis.travel().instability();
            this.removeFuel(20d * 0.25d * instability < 1 ? 1 : instability);
        }
    }

    public BoolValue refueling() {
        return refueling;
    }

    public boolean hasPower() {
        return power.get();
    }

    public void togglePower() {
        if (this.power.get()) {
            this.disablePower();
        } else {
            this.enablePower();
        }
    }

    public void disablePower() {
        if (!this.power.get())
            return;

        this.power.set(false);
        this.updateExteriorState();

        TardisEvents.LOSE_POWER.invoker().onLosePower(this.tardis);
        this.disableProtocols();
    }
    private void disableProtocols() {
        tardis.getDesktop().playSoundAtEveryConsole(AITSounds.SHUTDOWN, SoundCategory.AMBIENT, 10f, 1f);

        // disabling protocols
        tardis.travel().antigravs().set(false);
        tardis.stats().hailMary().set(false);
        tardis.<HadsHandler>handler(Id.HADS).enabled().set(false);
    }

    public void enablePower(boolean requiresEngine) {
        if (this.power.get())
            return;

        if (this.tardis.getFuel() <= (0.01 * FuelHandler.TARDIS_MAX_FUEL))
            return; // cant enable power if not enough fuel
        if (this.tardis.siege().isActive()) return;
        if (requiresEngine && !EngineSystem.hasEngine(tardis)) return;

        this.power.set(true);
        this.updateExteriorState();

        this.tardis.getDesktop().playSoundAtEveryConsole(AITSounds.POWERUP, SoundCategory.AMBIENT, 10f, 1f);
        this.tardis.getDesktop().playSoundAtEveryConsole(AITSounds.CONSOLE_BOOTUP, SoundCategory.AMBIENT, 0.15f, 1f);
        TardisEvents.REGAIN_POWER.invoker().onRegainPower(this.tardis);
    }
    public void enablePower() {
        this.enablePower(true);
    }

    // why is this in the engine handler? - Loqor
    // idk, but i moved it here still - duzo
    private void updateExteriorState() {
        TravelHandler travel = this.tardis.travel();

        if (travel.getState() != TravelHandler.State.LANDED)
            return;

        CachedDirectedGlobalPos pos = travel.position();
        World world = pos.getWorld();

        if (world == null)
            return;
        BlockState state = world.getBlockState(pos.getPos());
        if (!(state.getBlock() instanceof ExteriorBlock))
            return;

        world.setBlockState(pos.getPos(),
                state.with(ExteriorBlock.LEVEL_4, this.power.get() ? 4 : 0));
    }
}
