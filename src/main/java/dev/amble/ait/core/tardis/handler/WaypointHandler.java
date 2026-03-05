package dev.amble.ait.core.tardis.handler;

import java.util.Optional;

import dev.amble.ait.api.tardis.TardisEvents;
import dev.amble.ait.core.item.ControlDiscItem;
import dev.amble.lib.data.CachedDirectedGlobalPos;

import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

import dev.amble.ait.api.tardis.KeyedTardisComponent;
import dev.amble.ait.core.item.WaypointItem;
import dev.amble.ait.core.world.TardisServerWorld;
import dev.amble.ait.data.Waypoint;
import dev.amble.ait.data.properties.bool.BoolProperty;
import dev.amble.ait.data.properties.bool.BoolValue;

public class WaypointHandler extends KeyedTardisComponent {
    public static final BoolProperty HAS_CARTRIDGE = new BoolProperty("has_cartridge", false);
    private final BoolValue hasCartridge = HAS_CARTRIDGE.create(this);
    public static final BoolProperty IS_DISC = new BoolProperty("is_disc", false);
    private final BoolValue iSDisc = IS_DISC.create(this);
    public static final BoolProperty CAN_CONTAIN_PLAYERS = new BoolProperty("can_contain_players", true);
    public final BoolValue leaveBehindOnLoad = CAN_CONTAIN_PLAYERS.create(this);

    private Waypoint current; // The current waypoint in the slot ( tried to make it optional, but that
    // caused a
    // gson crash )

    static {
        // I don't remember if we're not supposed to use static initializers in handlers or not but oh well! - Loqor
        TardisEvents.LANDED.register((tardis) -> {
            if (tardis.waypoint().isDisc()) {
                tardis.waypoint().clear(null, false);
                tardis.waypoint().clearCanContainPlayers();
            }
        });
    }

    public WaypointHandler() {
        super(Id.WAYPOINTS);
    }

    @Override
    public void onLoaded() {
        hasCartridge.of(this, HAS_CARTRIDGE);
        iSDisc.of(this, IS_DISC);
        leaveBehindOnLoad.of(this, CAN_CONTAIN_PLAYERS);
    }

    public boolean hasCartridge() {
        return hasCartridge.get();
    }

    public void setHasCartridge() {
        hasCartridge.set(true);
    }

    private void clearCartridge() {
        hasCartridge.set(false);
    }

    public boolean isDisc() {
        return iSDisc.get();
    }

    public void setIsDisc() {
        iSDisc.set(true);
    }

    private void clearIsDisc() {
        iSDisc.set(false);
    }

    public boolean canContainPlayers() {
        return leaveBehindOnLoad.get();
    }

    public void setCanContainPlayers(boolean bool) {
        leaveBehindOnLoad.set(bool);
    }

    private void clearCanContainPlayers() {
        leaveBehindOnLoad.set(true);
    }

    /**
     * Sets the new waypoint
     *
     * @return The optional of the previous waypoint
     */
    public Optional<Waypoint> set(Waypoint var, BlockPos console, boolean spawnItem) {
        Optional<Waypoint> prev = Optional.ofNullable(this.current);

        // Insurance so discs don't get overwritten since they're only one-time use and one-time write (so technically it's
        // DVD-ROM but don't tell anyone that) - Loqor
        if (this.isDisc()) {
            this.current = var;
            return prev;
        }

        if (spawnItem && this.current != null)
            this.spawnItem(console, prev.get());

        this.current = var;
        return prev;
    }

    public Waypoint get() {
        return this.current;
    }

    public boolean hasWaypoint() {
        return this.current != null;
    }

    public void clear(BlockPos console, boolean spawnItem) {
        this.set(null, console, spawnItem);
        this.clearCartridge();
        if (isDisc()) {
            this.clearIsDisc();
        }
    }

    public boolean loadWaypoint() {
        if (!this.hasWaypoint())
            return false;

        CachedDirectedGlobalPos cachedPos = this.get().getPos();

        if (cachedPos == null || !this.hasWaypoint())
            return false;

        if (cachedPos.getWorld() instanceof TardisServerWorld) {
            cachedPos = CachedDirectedGlobalPos.create(TardisServerWorld.OVERWORLD, cachedPos.getPos(), cachedPos.getRotation());
        }

        tardis.travel().destination(cachedPos);
        return true;
    }

    public void spawnItem(BlockPos console) {
        if (!this.hasWaypoint())
            return;

        this.spawnItem(console, this.get());
        this.clear(console, false);
    }

    public void spawnItem(BlockPos console, Waypoint waypoint) {
        if (!this.hasCartridge())
            return;

        ItemEntity entity = new ItemEntity(tardis.asServer().world(), console.getX(), console.getY(),
                console.getZ(), isDisc() ? createDiscItem(waypoint) : createWaypointItem(waypoint));

        tardis.asServer().world().spawnEntity(entity);
    }

    public static ItemStack createWaypointItem(Waypoint waypoint) {
        return WaypointItem.create(waypoint);
    }

    public static ItemStack createDiscItem(Waypoint waypoint) {
        return ControlDiscItem.create(waypoint);
    }
}
