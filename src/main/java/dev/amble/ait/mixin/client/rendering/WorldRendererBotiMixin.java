package dev.amble.ait.mixin.client.rendering;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.util.math.Vec3d;

import dev.loqor.portal.client.WorldGeometryRenderer;

@Mixin(WorldRenderer.class)
public abstract class WorldRendererBotiMixin {

    @Redirect(method = "renderSky(Lnet/minecraft/client/util/math/MatrixStack;Lorg/joml/Matrix4f;FLnet/minecraft/client/render/Camera;ZLjava/lang/Runnable;)V",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/network/ClientPlayerEntity;getCameraPosVec(F)Lnet/minecraft/util/math/Vec3d;"))
    private Vec3d ait$botiVoidPlaneEye(ClientPlayerEntity player, float tickDelta) {
        Vec3d portalEye = WorldGeometryRenderer.getPortalSkyCameraPos();
        return portalEye != null ? portalEye : player.getCameraPosVec(tickDelta);
    }

    @Redirect(method = "renderSky(Lnet/minecraft/client/util/math/MatrixStack;Lorg/joml/Matrix4f;FLnet/minecraft/client/render/Camera;ZLjava/lang/Runnable;)V",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/render/Camera;getPos()Lnet/minecraft/util/math/Vec3d;"))
    private Vec3d ait$botiSkyColorPos(Camera camera) {
        Vec3d portalEye = WorldGeometryRenderer.getPortalSkyCameraPos();
        return portalEye != null ? portalEye : camera.getPos();
    }
}
