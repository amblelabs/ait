package dev.loqor.portal.client;

import dev.amble.ait.client.boti.TardisDoorBOTI;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;

public class ExampleClientMod implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // Geometry rebuilds are now driven per-section by the block/light/chunk handlers in PortalData, so we no
        // longer force a full rebuild on every portal packet (entity moves, particles, etc. don't touch geometry).
        ClientPlayConnectionEvents.DISCONNECT.register((clientPlayNetworkHandler, minecraftClient) -> {
            TardisDoorBOTI.cleanup();
        });
    }
}
