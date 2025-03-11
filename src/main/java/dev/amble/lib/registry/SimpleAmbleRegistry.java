package dev.amble.lib.registry;

import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class SimpleAmbleRegistry<T> extends AmbleRegistry<T> {

    private final Registry<T> registry;

    public SimpleAmbleRegistry(Identifier id) {
        super(id);

        this.registry = FabricRegistryBuilder.createSimple(this.getKey()).buildAndRegister();
    }

    public SimpleAmbleRegistry(Identifier id, Identifier def) {
        super(id);

        this.registry = FabricRegistryBuilder.createDefaulted(this.getKey(), def).buildAndRegister();
    }

    @Override
    public Registry<T> get() {
        return this.registry;
    }
}
