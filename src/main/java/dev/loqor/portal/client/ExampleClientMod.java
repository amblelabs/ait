package dev.loqor.portal.client;

import dev.amble.ait.client.boti.TardisDoorBOTI;
import dev.loqor.portal.WrappedPacketS2CPacket;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.*;

public class ExampleClientMod implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(WrappedPacketS2CPacket.TYPE, (wrapped, player, packetSender) -> {
            Packet<?> packet = wrapped.packet();
            PortalDataManager manager = PortalDataManager.get();

            manager.handle(packet);

            WorldGeometryRenderer renderer = TardisDoorBOTI.getInteriorRenderer();

            if (renderer != null)
                MinecraftClient.getInstance().executeSync(renderer::markDirty);
        });

        ClientPlayConnectionEvents.DISCONNECT.register((clientPlayNetworkHandler, minecraftClient) -> {
            TardisDoorBOTI.cleanup();
            PortalDataManager.get().reset();
        });
    }
}
