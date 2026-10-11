package dev.amble.ait.api;

import net.minecraft.server.network.ServerPlayerEntity;

/** Tracks discontinuous server-side player movement for proximity checks. */
public interface TeleportAware {

    int ait$getTeleportEpoch();

    void ait$markTeleported();

    static int getTeleportEpoch(ServerPlayerEntity player) {
        return ((TeleportAware) player).ait$getTeleportEpoch();
    }

    static void markTeleported(ServerPlayerEntity player) {
        ((TeleportAware) player).ait$markTeleported();
    }
}
