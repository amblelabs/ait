package dev.amble.ait.core.util;

import net.minecraft.util.StringIdentifiable;

public enum MonitorStateUtil implements StringIdentifiable {
    DEFAULT("default"),
    BLAZE("blaze");

    private final String name;

    private MonitorStateUtil(String name) {
        this.name = name;
    }

    public String toString() {
        return this.name;
    }

    public String asString() {
        return this.name;
    }
}