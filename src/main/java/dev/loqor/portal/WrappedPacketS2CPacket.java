package dev.loqor.portal;

import dev.amble.ait.AITMod;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.NetworkState;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.Packet;

public record WrappedPacketS2CPacket(Packet<?> packet) implements FabricPacket, ProxiedPacket {

    public static final PacketType<WrappedPacketS2CPacket> TYPE = PacketType.create(AITMod.id("wrapped"), WrappedPacketS2CPacket::read);

    private static WrappedPacketS2CPacket read(PacketByteBuf buf) {
        int packetId = buf.readVarInt();
        Packet<?> packet = NetworkState.PLAY.getPacketHandler(NetworkSide.CLIENTBOUND, packetId, buf);

        return new WrappedPacketS2CPacket(packet);
    }

    @Override
    public void write(PacketByteBuf buf) {
        int packetId = NetworkState.PLAY.getPacketId(NetworkSide.CLIENTBOUND, this.packet);

        buf.writeVarInt(packetId);
        this.packet.write(buf);
    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }
}
