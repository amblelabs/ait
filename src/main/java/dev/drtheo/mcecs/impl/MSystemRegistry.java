package dev.drtheo.mcecs.impl;

import dev.amble.ait.AITMod;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;

public class MSystemRegistry {

    private static final RegistryKey<Registry<MSystem>> SERVER_KEY = RegistryKey.ofRegistry(AITMod.id("system/server"));

    @Environment(EnvType.CLIENT) private static final RegistryKey<Registry<MSystem>> CLIENT_KEY = RegistryKey.ofRegistry(AITMod.id("system/client"));

    @Environment(EnvType.CLIENT)
    public static final Registry<MSystem> CLIENT = FabricRegistryBuilder.createSimple(CLIENT_KEY).buildAndRegister();

    public static final Registry<MSystem> SERVER = FabricRegistryBuilder.createSimple(SERVER_KEY).buildAndRegister();

    public static void registerClient(MSystem system) {
        register(MSystem.Type.CLIENT, system);
    }

    public static void registerServer(MSystem system) {
        register(MSystem.Type.SERVER, system);
    }

    public static void register(MSystem.Type type, MSystem system) {
        if (system.type() != type)
            throw new IllegalArgumentException("Tried to register " + system.id()
                    + " as " + type + " system, but got " + system.type() + " system");

        Registry<MSystem> registry = switch (type) {
            case CLIENT -> CLIENT;
            case SERVER -> SERVER;
        };

        Registry.register(registry, system.id(), system);
    }
}
