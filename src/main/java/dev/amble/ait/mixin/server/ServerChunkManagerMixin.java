package dev.amble.ait.mixin.server;

import dev.amble.ait.AITMod;
import dev.loqor.portal.server.BOTIPortalTracker;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerChunkManager.class)
public abstract class ServerChunkManagerMixin {
    
    @Shadow @Final ServerWorld world;
    
    // Thread-local flag to prevent re-entry and infinite recursion
    private static final ThreadLocal<Boolean> isProcessing = ThreadLocal.withInitial(() -> false);
    
    /**
     * Hook into chunk loading to notify BOTI portal tracker
     * Using getChunk method which is the actual chunk loading method in MC 1.20.1
     */
    @Inject(method = "getChunk(IILnet/minecraft/world/chunk/ChunkStatus;Z)Lnet/minecraft/world/chunk/Chunk;", at = @At("RETURN"))
    private void onChunkLoad(int x, int z, ChunkStatus status, boolean create, CallbackInfoReturnable<Chunk> cir) {
        // Prevent re-entry to avoid infinite recursion
        if (isProcessing.get()) {
            return;
        }
        
        Chunk chunk = cir.getReturnValue();
        if (chunk instanceof WorldChunk) {
            try {
                isProcessing.set(true);
                ChunkPos pos = new ChunkPos(x, z);
                BOTIPortalTracker.getInstance().onChunkLoad(world, pos);
            } catch (Exception e) {
                AITMod.LOGGER.error("Error notifying BOTI tracker of chunk load", e);
            } finally {
                isProcessing.set(false);
            }
        }
    }
}
