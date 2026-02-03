package dev.amble.ait.core.tardis.util.network.s2c;

import dev.amble.ait.AITMod;
import dev.loqor.portal.client.ExteriorPortalDataManager;
import dev.loqor.portal.client.InteriorPortalDataManager;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.ChunkDataS2CPacket;

import java.util.UUID;

/**
 * Wrapper packet for ChunkDataS2CPacket to route to portal data managers instead of client world
 */
public class BOTIChunkDataS2CPacket implements FabricPacket {
    public static final PacketType<BOTIChunkDataS2CPacket> TYPE = 
        PacketType.create(AITMod.id("boti_chunk_data"), BOTIChunkDataS2CPacket::new);
    
    private final UUID tardisId;
    private final boolean isExteriorView; // true = viewing interior from exterior, false = viewing exterior from interior
    private final byte[] chunkPacketData;
    
    public BOTIChunkDataS2CPacket(UUID tardisId, boolean isExteriorView, ChunkDataS2CPacket chunkPacket) {
        this.tardisId = tardisId;
        this.isExteriorView = isExteriorView;
        
        // Serialize the chunk packet
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        chunkPacket.write(buf);
        this.chunkPacketData = new byte[buf.readableBytes()];
        buf.readBytes(this.chunkPacketData);
        buf.release();
    }
    
    public BOTIChunkDataS2CPacket(PacketByteBuf buf) {
        this.tardisId = buf.readUuid();
        this.isExteriorView = buf.readBoolean();
        int dataLength = buf.readInt();
        this.chunkPacketData = new byte[dataLength];
        buf.readBytes(this.chunkPacketData);
    }
    
    @Override
    public void write(PacketByteBuf buf) {
        buf.writeUuid(tardisId);
        buf.writeBoolean(isExteriorView);
        buf.writeInt(chunkPacketData.length);
        buf.writeBytes(chunkPacketData);
    }
    
    @Override
    public PacketType<?> getType() {
        return TYPE;
    }
    
    public void handle() {
        MinecraftClient client = MinecraftClient.getInstance();
        
        if (!client.isOnThread()) {
            client.execute(this::handle);
            return;
        }
        
        try {
            // Deserialize the chunk packet
            PacketByteBuf buf = new PacketByteBuf(Unpooled.wrappedBuffer(chunkPacketData));
            ChunkDataS2CPacket chunkPacket = new ChunkDataS2CPacket(buf);
            buf.release();
            
            // Route to the appropriate portal data manager
            if (isExteriorView) {
                // Viewing interior from exterior - use InteriorPortalDataManager
                InteriorPortalDataManager manager = InteriorPortalDataManager.get();
                manager.setTardis(tardisId);
                manager.handle(chunkPacket);
                AITMod.LOGGER.debug("Routed chunk to InteriorPortalDataManager for TARDIS {}", tardisId);
            } else {
                // Viewing exterior from interior - use ExteriorPortalDataManager
                ExteriorPortalDataManager manager = ExteriorPortalDataManager.get();
                manager.setTardis(tardisId);
                manager.handle(chunkPacket);
                AITMod.LOGGER.debug("Routed chunk to ExteriorPortalDataManager for TARDIS {}", tardisId);
            }
        } catch (Exception e) {
            AITMod.LOGGER.error("Failed to handle BOTI chunk packet for TARDIS {}: {}", tardisId, e.getMessage(), e);
        }
    }
}
