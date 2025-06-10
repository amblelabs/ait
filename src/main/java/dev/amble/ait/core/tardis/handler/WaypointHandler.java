package dev.amble.ait.core.tardis.handler;

import java.util.Optional;

import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

import dev.amble.ait.api.tardis.KeyedTardisComponent;
import dev.amble.ait.core.item.WaypointItem;
import dev.amble.ait.core.tardis.handler.travel.TravelUtil;
import dev.amble.ait.data.Waypoint;
import dev.amble.ait.data.properties.bool.BoolProperty;
import dev.amble.ait.data.properties.bool.BoolValue;

public class WaypointHandler extends KeyedTardisComponent {
    public static final BoolProperty HAS_CARTRIDGE = new BoolProperty("has_cartridge", false);
    private final BoolValue hasCartridge = HAS_CARTRIDGE.create(this);
    private Waypoint current; // The current waypoint in the slot ( tried to make it optional, but that
    // caused a
    // gson crash )

    public WaypointHandler() {
        super(Id.WAYPOINTS);
    }

    @Override
    public void onLoaded() {
        hasCartridge.of(this, HAS_CARTRIDGE);
    }

    public boolean hasCartridge() {
        return hasCartridge.get();
    }

    public void markHasCartridge() {
        hasCartridge.set(true);
    }

    private void clearCartridge() {
        hasCartridge.set(false);
    }

    /**
     * Sets the new waypoint
     *
     * @return The optional of the previous waypoiint
     */
    public Optional<Waypoint> set(Waypoint var, BlockPos console, boolean spawnItem) {
        Optional<Waypoint> prev = Optional.ofNullable(this.current);

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
    }

    public void gotoWaypoint() {
        if (!this.hasWaypoint())
            return; // todo move this check to the DEMAT event so the fail to takeoff happens

        //this.tardis.travel().autopilot(true);
        TravelUtil.travelTo(tardis, this.get().getPos());
    }

    public void setDestination() {
        if (!this.hasWaypoint())
            return;

        this.tardis.travel().forceDestination(this.get().getPos());

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
                console.getZ(), createWaypointItem(waypoint));

        tardis.asServer().world().spawnEntity(entity);
        this.clearCartridge();
    }

    public static ItemStack createWaypointItem(Waypoint waypoint) {
        return WaypointItem.create(waypoint);
    }
}
