package dev.amble.ait.mixin.client.rendering;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.util.math.Vec3d;

import dev.loqor.portal.client.PortalDataManager;
import dev.loqor.portal.client.WorldGeometryRenderer;

@Mixin(WorldRenderer.class)
public abstract class WorldRendererBotiMixin {

    @Shadow @Final private MinecraftClient client;

    // immersive portals redirects the same eye call, a wrap stacks on top of it
    @WrapOperation(method = "renderSky(Lnet/minecraft/client/util/math/MatrixStack;Lorg/joml/Matrix4f;FLnet/minecraft/client/render/Camera;ZLjava/lang/Runnable;)V",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/network/ClientPlayerEntity;getCameraPosVec(F)Lnet/minecraft/util/math/Vec3d;"))
    private Vec3d ait$botiVoidPlaneEye(ClientPlayerEntity player, float tickDelta, Operation<Vec3d> original) {
        Vec3d portalEye = WorldGeometryRenderer.getPortalSkyCameraPos();
        return portalEye != null ? portalEye : original.call(player, tickDelta);
    }

    @WrapOperation(method = "renderSky(Lnet/minecraft/client/util/math/MatrixStack;Lorg/joml/Matrix4f;FLnet/minecraft/client/render/Camera;ZLjava/lang/Runnable;)V",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/render/Camera;getPos()Lnet/minecraft/util/math/Vec3d;"))
    private Vec3d ait$botiSkyColorPos(Camera camera, Operation<Vec3d> original) {
        Vec3d portalEye = WorldGeometryRenderer.getPortalSkyCameraPos();
        return portalEye != null ? portalEye : original.call(camera);
    }

    // boti particles sit in the portal's world, the main camera is in another one
    @WrapOperation(method = "spawnParticle(Lnet/minecraft/particle/ParticleEffect;ZZDDDDDD)Lnet/minecraft/client/particle/Particle;",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/render/Camera;getPos()Lnet/minecraft/util/math/Vec3d;"))
    private Vec3d ait$botiParticleOrigin(Camera camera, Operation<Vec3d> original) {
        Vec3d eye = (Object) this != this.client.worldRenderer ? PortalDataManager.particleOrigin((WorldRenderer) (Object) this) : null;
        return eye != null ? eye : original.call(camera);
    }
}
