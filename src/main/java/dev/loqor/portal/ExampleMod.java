package dev.loqor.portal;

import dev.amble.ait.AITMod;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

public class ExampleMod implements ModInitializer {

    private static PacketProxyPlayer proxy;

    @Override
    public void onInitialize() {
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            ServerWorld world = server.getOverworld();

            proxy = new PacketProxyPlayer(world);
            proxy.setPos(0, 64, 0);
            world.spawnEntity(proxy);

            proxy.setPacketListener(packet -> {
                //AITMod.LOGGER.info("Proxied a packet: {}", packet);
                FabricPacket wrapped = new WrappedPacketS2CPacket(packet);

                for (ServerPlayerEntity player : world.getPlayers()) {
                    if (player instanceof PacketProxyPlayer) continue;

                    ServerPlayNetworking.send(player, wrapped);
                }
            });
        });

        ServerLifecycleEvents.SERVER_STOPPING.register(server -> {
            server.getOverworld().removePlayer(proxy, Entity.RemovalReason.DISCARDED);
        });
    }
}
