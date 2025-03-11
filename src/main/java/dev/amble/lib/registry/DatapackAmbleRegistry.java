package dev.amble.lib.registry;

import com.mojang.serialization.Codec;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.impl.registry.sync.DynamicRegistriesImpl;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public abstract class DatapackAmbleRegistry<T> extends DynamicAmbleRegistry<T> {

    private final boolean shouldSync;

    public DatapackAmbleRegistry(Identifier id) {
        this(id, true);
    }

    public DatapackAmbleRegistry(Identifier id, boolean shouldSync) {
        super(id);

        this.shouldSync = shouldSync;
    }

    public void init() {
        if (shouldSync) {
            DynamicRegistries.registerSynced(this.getKey(), this.codec(), this.networkCodec(), DynamicRegistries.SyncOption.SKIP_WHEN_EMPTY);
        } else {
            DynamicRegistries.register(this.getKey(), this.codec());
        }
    }

    protected abstract Codec<T> codec();

    protected Codec<T> networkCodec() {
        return codec();
    }
}
