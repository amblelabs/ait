package dev.amble.ait.mixin.server;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.server.world.ServerWorld;

import dev.amble.ait.api.AITWorldOptions;
import dev.amble.ait.core.events.WorldSaveEvent;

@Mixin(ServerWorld.class)
public class ServerWorldMixin implements AITWorldOptions {

    private boolean canRiftsSpawn;

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
}
