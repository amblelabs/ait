package dev.amble.ait.api;

import net.minecraft.registry.RegistryKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

public interface ServerWithTardis {

    ServerWorld ait$getRealWorld(RegistryKey<World> key);
}
