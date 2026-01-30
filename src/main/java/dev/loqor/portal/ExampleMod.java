package dev.loqor.portal;

import dev.amble.ait.AITMod;
import dev.amble.ait.core.blockentities.ExteriorBlockEntity;
import dev.amble.ait.core.tardis.ServerTardis;
import dev.amble.ait.core.tardis.Tardis;
import dev.amble.ait.core.world.TardisServerWorld;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.ChunkRenderDistanceCenterS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.server.world.ThreadedAnvilChunkStorage;
import net.minecraft.world.ForcedChunkState;

public class ExampleMod implements ModInitializer {

    private static PacketProxyPlayer proxy;

    @Override
    public void onInitialize() {
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            ServerWorld world = server.getOverworld();

            proxy = new PacketProxyPlayer(world);
            proxy.setPos(0, 64, 0);
            world.spawnEntity(proxy);
            proxy.onChunkEntered();

            proxy.setPacketListener(packet -> {
                //AITMod.LOGGER.info("Proxied a packet: {}", packet);
                FabricPacket wrapped = new WrappedPacketS2CPacket(packet);

                for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                    if (player instanceof PacketProxyPlayer) continue;

                    ServerPlayNetworking.send(player, wrapped);
                    //ServerPlayNetworking.send(player, center);
                }

                /*if (world.getBlockEntity(proxy.getBlockPos()) instanceof ExteriorBlockEntity exteriorBlockEntity) {

                    if(!exteriorBlockEntity.isLinked()) return;

                    ServerTardis tardis = exteriorBlockEntity.tardis().get().asServer();

                    try {
                        TardisServerWorld tardisServerWorld = tardis.world();

                        for (ServerPlayerEntity player : tardisServerWorld.getPlayers()) {
                            if (player instanceof PacketProxyPlayer) continue;

                            ServerPlayNetworking.send(player, wrapped);
                            //ServerPlayNetworking.send(player, center);
                        }
                    } catch (Exception ignored) {}
                }*/
            });
        });

        ServerLifecycleEvents.SERVER_STOPPING.register(server -> {
            server.getOverworld().removePlayer(proxy, Entity.RemovalReason.DISCARDED);
        });
    }
}
