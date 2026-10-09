package dev.amble.ait.mixin.boti;

import com.llamalad7.mixinextras.sugar.Local;
import dev.drtheo.portal.PacketProxyPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.ParticleS2CPacket;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

@Mixin(ServerWorld.class)
public abstract class ProxyParticlesMixin {

    @Shadow
    protected abstract boolean sendToPlayerIfNearby(ServerPlayerEntity player, boolean force, double x, double y, double z, Packet<?> packet);

    // the vanilla loop only goes over the world's player list
    @Inject(method = "spawnParticles(Lnet/minecraft/particle/ParticleEffect;DDDIDDDD)I", at = @At("RETURN"))
    private <T extends ParticleEffect> void ait$sendToProxies(T particle, double x, double y, double z, int count, double deltaX, double deltaY,
                                                             double deltaZ, double speed, CallbackInfoReturnable<Integer> cir, @Local ParticleS2CPacket packet) {
        for (PacketProxyPlayer proxy : PacketProxyPlayer.attached((ServerWorld) (Object) this))
            this.sendToPlayerIfNearby(proxy, false, x, y, z, packet);
    }
}
