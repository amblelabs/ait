package dev.amble.ait.module.planet.mixin.client;

import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;

import dev.amble.ait.core.world.TardisServerWorld;
import dev.amble.ait.module.planet.core.space.planet.Planet;
import dev.amble.ait.module.planet.core.space.planet.PlanetRegistry;

@Mixin(value = WorldRenderer.class, priority = 1001)
public abstract class CloudMixin {

    @Shadow
    private @Nullable ClientWorld world;

    @Inject(method="renderClouds(Lnet/minecraft/client/util/math/MatrixStack;Lorg/joml/Matrix4f;FDDD)V", at = @At("HEAD"), cancellable = true)
    private void ait$renderClouds(MatrixStack matrices, Matrix4f projectionMatrix, float tickDelta, double cameraX, double cameraY, double cameraZ, CallbackInfo ci) {
        // Key the check off the world THIS renderer is drawing, not the player's: the BOTI doorway renders the
        // exterior dimension's clouds through a shadow WorldRenderer while the player stands inside the TARDIS
        // dimension - checking the player's world cancelled those too, so the doorway sky never had clouds. The
        // main renderer's world IS the player's world, so interior/planet behaviour is unchanged, and the shadow
        // renderer now also skips clouds when the exterior is parked on a planet that disables them.
        ClientWorld world = this.world;

        if (world == null)
            return;

        if (TardisServerWorld.isTardisDimension(world)) {
            ci.cancel();
            return;
        }

        Planet planet = PlanetRegistry.getInstance().get(world);

        if (planet == null)
            return;

        if (!planet.render().clouds())
            ci.cancel();
    }
}
