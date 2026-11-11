package dev.amble.ait.mixin.boti;

import java.util.List;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import dev.drtheo.portal.PacketProxyPlayer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.server.world.ThreadedAnvilChunkStorage;

@Mixin(ThreadedAnvilChunkStorage.class)
public abstract class ProxyChunkStorageMixin {

    @Shadow @Final ServerWorld world;

    // the proxies aren't in the world's player list, entity tracking still has to reach them
    @ModifyExpressionValue(method = {"tickEntityMovement", "loadEntity", "updatePosition"}, at = @At(value = "INVOKE",
            target = "Lnet/minecraft/server/world/ServerWorld;getPlayers()Ljava/util/List;", ordinal = 0))
    private List<ServerPlayerEntity> ait$withProxies(List<ServerPlayerEntity> players) {
        return PacketProxyPlayer.withProxies(this.world, players);
    }
}
