package dev.amble.ait.mixin.server;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

import dev.amble.ait.api.AITWorldOptions;
import dev.amble.ait.core.events.WorldSaveEvent;
import dev.amble.ait.core.item.SiegeTardisItem;
import dev.amble.ait.core.tardis.Tardis;
import dev.amble.ait.core.tardis.util.network.BOTIUpdateTracker;

@Mixin(ServerWorld.class)
public class ServerWorldMixin implements AITWorldOptions {

    @Unique private boolean canRiftsSpawn;

    @Inject(method = "saveLevel", at = @At("HEAD"))
    private void saveLevel(CallbackInfo ci) {
        WorldSaveEvent.EVENT.invoker().onWorldSave((ServerWorld) (Object) this);
    }

    @Override
    public void ait$setCanRiftsSpawn(boolean canSpawn) {
        this.canRiftsSpawn = canSpawn;
    }

    @Override
    public boolean ait$canRiftsSpawn() {
        return canRiftsSpawn;
    }

    @Inject(method = "spawnEntity", at = @At("RETURN"))
    public void spawnEntity(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValueZ()) return;
        if (!(entity instanceof ItemEntity itemEntity)) return;

        ItemStack stack = itemEntity.getStack();

        if (stack.getItem() instanceof SiegeTardisItem item) {
            Tardis found = item.getTardis(entity.getWorld(), stack);

            if (found == null)
                return;

            // kill ourselves and place down the exterior
            SiegeTardisItem.placeTardis(found, SiegeTardisItem.fromEntity(entity));
            entity.kill();
        }
    }

    /**
     * Tracks block state changes for BOTI (Bigger On The Inside) rendering updates.
     * When a block changes in a dimension being viewed via BOTI, notifies all viewing clients.
     */
    @Inject(method = "setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;II)Z", 
            at = @At("RETURN"))
    private void onBlockStateChange(BlockPos pos, BlockState state, int flags, int maxUpdateDepth, 
                                   CallbackInfoReturnable<Boolean> cir) {
        // Only notify if the block state actually changed
        if (cir.getReturnValue()) {
            BOTIUpdateTracker.notifyBlockUpdate((ServerWorld)(Object)this, pos, state);
        }
    }
}
