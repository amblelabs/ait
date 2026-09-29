package dev.amble.ait.mixin.server;

import dev.amble.ait.core.events.WorldSaveEvent;
import dev.amble.ait.core.item.SiegeTardisItem;
import dev.amble.ait.core.tardis.Tardis;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;

@Mixin(ServerWorld.class)
public class ServerWorldMixin {

    @Inject(method = "saveLevel", at = @At("HEAD"))
    private void saveLevel(CallbackInfo ci) {
        WorldSaveEvent.EVENT.invoker().onWorldSave((ServerWorld) (Object) this);
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

            if (!found.siege().isActive()) {
                entity.discard();
                return;
            }

            // kill ourselves and place down the exterior
            if (SiegeTardisItem.placeTardis(found, SiegeTardisItem.fromEntity(entity), entity)) {
                entity.kill();
            }
        }
    }
}
