package dev.amble.ait.core.tardis.util;

import dev.amble.ait.AITMod;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

/** Packets used exclusively for Biodata Restoration presentation. */
public final class BiodataRestorationNetwork {
    public static final Identifier SHOW_TARDIS_ITEM = AITMod.id("biodata_restoration_show_tardis");

    private BiodataRestorationNetwork() {
    }

    public static void init() {
        // The server only sends this packet; no receiver registration is required.
    }

    public static void showTardisItem(ServerPlayerEntity player) {
        if (player != null)
            ServerPlayNetworking.send(player, SHOW_TARDIS_ITEM, PacketByteBufs.empty());
    }
}
