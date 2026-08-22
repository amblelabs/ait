package dev.amble.ait.client;

import dev.amble.ait.core.AITItems;
import dev.amble.ait.core.tardis.util.BiodataRestorationNetwork;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

import net.minecraft.client.MinecraftClient;

/** Client-only presentation for a successful Biodata Restoration. */
public final class BiodataRestorationClient {
    private BiodataRestorationClient() {
    }

    public static void init() {
        ClientPlayNetworking.registerGlobalReceiver(BiodataRestorationNetwork.SHOW_TARDIS_ITEM,
                (client, handler, buf, responseSender) -> client.execute(() ->
                        MinecraftClient.getInstance().gameRenderer.showFloatingItem(
                                AITItems.TARDIS_ITEM.getDefaultStack())));
    }
}
