package dev.amble.ait.client.boti.codec;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.network.PacketByteBuf;

/**
 * Fast BlockState <-> int encoding using raw state IDs.
 * This avoids the overhead of NBT serialization used in the old system.
 */
public class BlockStateCodec {
    
    /**
     * Encodes a BlockState to a PacketByteBuf using raw state ID (varint).
     * This is much more compact than NBT encoding.
     * 
     * @param buf The buffer to write to
     * @param state The BlockState to encode
     */
    public static void encode(PacketByteBuf buf, BlockState state) {
        // Use raw state ID from Block.STATE_IDS - this is a compact integer representation
        int rawId = Block.getRawIdFromState(state);
        buf.writeVarInt(rawId);
    }
    
    /**
     * Decodes a BlockState from a PacketByteBuf.
     * 
     * @param buf The buffer to read from
     * @return The decoded BlockState
     */
    public static BlockState decode(PacketByteBuf buf) {
        int rawId = buf.readVarInt();
        return Block.getStateFromRawId(rawId);
    }
}
