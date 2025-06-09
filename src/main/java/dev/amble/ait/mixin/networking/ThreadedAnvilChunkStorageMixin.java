package dev.amble.ait.mixin.networking;

import dev.amble.ait.api.tardis.TardisEvents;
import net.minecraft.network.packet.s2c.play.ChunkDataS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ThreadedAnvilChunkStorage;
import net.minecraft.world.chunk.WorldChunk;
import org.apache.commons.lang3.mutable.MutableObject;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ThreadedAnvilChunkStorage.class, priority = 1001)
public abstract class ThreadedAnvilChunkStorageMixin {

    @Inject(method = "sendChunkDataPackets", at = @At("RETURN"))
    public void sendChunkDataPackets(ServerPlayerEntity player, MutableObject<ChunkDataS2CPacket> cachedDataPacket, WorldChunk chunk, CallbackInfo ci) {
        TardisEvents.SYNC_TARDIS.invoker().sync(player, chunk);
    }
}
