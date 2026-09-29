package dev.drtheo.portal;

import java.util.UUID;

import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

import dev.amble.ait.AITMod;

public record PortalInitS2CPacket(UUID id, RegistryKey<World> dimension,
                                  RegistryKey<DimensionType> dimensionType) implements FabricPacket {

    public static final PacketType<PortalInitS2CPacket> TYPE =
            PacketType.create(AITMod.id("portal_init"), PortalInitS2CPacket::read);

    private static PortalInitS2CPacket read(PacketByteBuf buf) {
        return new PortalInitS2CPacket(buf.readUuid(),
                buf.readRegistryKey(RegistryKeys.WORLD),
                buf.readRegistryKey(RegistryKeys.DIMENSION_TYPE));
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeUuid(this.id);
        buf.writeRegistryKey(this.dimension);
        buf.writeRegistryKey(this.dimensionType);
    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }
}
