package dev.amble.ait.mixin.boti;

import dev.amble.ait.core.tardis.util.network.BOTIUpdateTracker;
import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * CRITICAL: Intercepts ALL block state changes to send BOTI updates.
 * 
 * This mixin targets World.class (not ServerWorld) because setBlockState is defined in World.
 * We filter to only process server-side worlds inside the injection.
 */
@Mixin(World.class)
public abstract class WorldBOTIMixin {
    
    /**
     * Intercepts setBlockState to notify BOTI viewers about block changes.
     * 
     * This fires for EVERY block change in ANY world, so we:
     * 1. Check if it's server-side (not client)
     * 2. Check if the block actually changed (cir.getReturnValue())
     * 3. Check if anyone is viewing this dimension via BOTI
     * 4. Send update packet to those viewers
     */
    @Inject(
        method = "setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;II)Z",
        at = @At("RETURN")
    )
    private void ait$onBlockStateChange(BlockPos pos, BlockState state, int flags, int maxUpdateDepth,
                                        CallbackInfoReturnable<Boolean> cir) {
        // Only process on server side
        World world = (World)(Object)this;
        if (!(world instanceof ServerWorld serverWorld)) {
            return;
        }
        
        // Only notify if the block state actually changed
        if (!cir.getReturnValue()) {
            return;
        }
        
        // Performance optimization: Check if dimension has viewers before processing
        if (BOTIUpdateTracker.hasViewers(serverWorld.getRegistryKey())) {
            BOTIUpdateTracker.notifyBlockUpdate(serverWorld, pos, state);
        }
    }
}
