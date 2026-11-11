package dev.amble.ait.mixin.boti;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ThreadedAnvilChunkStorage;

@Mixin(ThreadedAnvilChunkStorage.class)
public interface ProxyChunkStorageAccessor {

    @Invoker
    void invokeHandlePlayerAddedOrRemoved(ServerPlayerEntity player, boolean added);
}
