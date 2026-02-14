package dev.amble.ait.data;

import dev.amble.ait.core.item.AbstractCoordinateModifierItem;
import dev.amble.ait.core.item.ControlDiscItem;
import dev.amble.lib.data.CachedDirectedGlobalPos;

import net.minecraft.item.ItemStack;

import dev.amble.ait.core.item.WaypointItem;

public class Waypoint {

    private String name;
    private final CachedDirectedGlobalPos pos;

    public Waypoint(String name, CachedDirectedGlobalPos pos) {
        this.name = name;
        this.pos = pos;
    }

    private Waypoint(CachedDirectedGlobalPos pos) {
        this(null, pos);
    }

    public Waypoint withName(String name) {
        this.name = name;
        return this;
    }

    public String name() {
        return this.name;
    }

    public boolean hasName() {
        return this.name != null;
    }

    public CachedDirectedGlobalPos getPos() {
        return pos;
    }

    public static Waypoint fromPos(CachedDirectedGlobalPos pos) {
        return new Waypoint(pos);
    }

    // This is stupid :) - Loqor
    public static Waypoint fromStack(ItemStack stack) {
        if (stack.getItem() instanceof WaypointItem) {
            return new Waypoint(stack.getName().getString(), WaypointItem.getPos(stack));
        }
        if (stack.getItem() instanceof ControlDiscItem) {
            return new Waypoint(stack.getName().getString(), ControlDiscItem.getPos(stack));
        }
        return null;
    }
}
