package dev.amble.ait.api.tardis;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.minecraft.client.MinecraftClient;
import net.minecraft.server.MinecraftServer;

import dev.amble.ait.core.tardis.Tardis;

/**
 * An interface for something that can be ticked by a tardis Make sure to add
 * whatever it is that needs ticking to {@link Tardis}
 */
public interface TardisTickable {
    default void tick(MinecraftServer server) { }

    @Environment(EnvType.CLIENT)
    default void tick(MinecraftClient client) { }
}
