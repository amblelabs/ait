package dev.drtheo.mcecs;

import net.minecraft.util.Identifier;

public abstract class MSystem {

    private final Identifier id;

    protected MSystem(Identifier id) {
        this.id = id;
    }

    public void init() {

    }

    public void onLoad() {

    }

    public Identifier id() {
        return id;
    }

    public abstract Type type();

    public enum Type {
        CLIENT,
        SERVER,
    }
}
