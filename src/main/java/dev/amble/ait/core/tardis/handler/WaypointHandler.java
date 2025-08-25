package dev.amble.ait.core.tardis.handler;

import java.util.Optional;

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

    public void setHasCartridge() {
        hasCartridge.set(true);
    }

    private void clearCartridge() {
        hasCartridge.set(false);
    }

    /**
     * Sets the new waypoint
     *
     * @return The optional of the previous waypoint
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
        this.clearCartridge();
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
                console.getZ(), createWaypointItem(waypoint));

        tardis.asServer().world().spawnEntity(entity);
    }

    public static ItemStack createWaypointItem(Waypoint waypoint) {
        return WaypointItem.create(waypoint);
    }
}
