package dev.amble.ait.mixin.boti;

import java.util.List;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.drtheo.portal.PacketProxyPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.Packet;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

@Mixin(ServerWorld.class)
public abstract class ProxyEventsMixin {

    // block and world events go through the player manager, which doesn't know the proxies
    @WrapOperation(method = {"processSyncedBlockEvents", "syncWorldEvent"}, at = @At(value = "INVOKE",
            target = "Lnet/minecraft/server/PlayerManager;sendToAround(Lnet/minecraft/entity/player/PlayerEntity;DDDDLnet/minecraft/registry/RegistryKey;Lnet/minecraft/network/packet/Packet;)V"))
    private void ait$sendToProxies(PlayerManager manager, PlayerEntity except, double x, double y, double z, double distance,
                                  RegistryKey<World> world, Packet<?> packet, Operation<Void> original) {
        original.call(manager, except, x, y, z, distance, world, packet);

        for (PacketProxyPlayer proxy : PacketProxyPlayer.attached((ServerWorld) (Object) this)) {
            if (proxy.squaredDistanceTo(x, y, z) < distance * distance)
                proxy.networkHandler.sendPacket(packet);
        }
    }

    @ModifyExpressionValue(method = "createExplosion(Lnet/minecraft/entity/Entity;Lnet/minecraft/entity/damage/DamageSource;Lnet/minecraft/world/explosion/ExplosionBehavior;DDDFZLnet/minecraft/world/World$ExplosionSourceType;)Lnet/minecraft/world/explosion/Explosion;",
            at = @At(value = "FIELD", target = "Lnet/minecraft/server/world/ServerWorld;players:Ljava/util/List;"))
    private List<ServerPlayerEntity> ait$explosionWithProxies(List<ServerPlayerEntity> players) {
        return PacketProxyPlayer.withProxies((ServerWorld) (Object) this, players);
    }
}
