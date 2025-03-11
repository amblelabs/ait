package dev.amble.lib.registry;

import dev.amble.lib.util.ServerLifecycleHooks;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public abstract class DynamicAmbleRegistry<T> extends AmbleRegistry<T> {

    public DynamicAmbleRegistry(Identifier id) {
        super(id);
    }

    @Override
    public Registry<T> get() {
        return this.get(ServerLifecycleHooks.get().getOverworld());
    }

    public Registry<T> get(World world) {
        return world.getRegistryManager().get(this.getKey());
    }

    @Environment(EnvType.CLIENT)
    public Registry<T> getClient() {
        return this.get(MinecraftClient.getInstance().world);
    }
}
