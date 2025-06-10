package dev.amble.ait.mixin.client.rendering;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.client.util.math.MatrixStack;

import dev.amble.ait.client.renderers.LandingRegionRenderer;

@Mixin(DebugRenderer.class)
public class DebugRendererMixin {
    @Inject(method="render", at = @At("TAIL"))
    private void ait$renderWorld(MatrixStack matrices, VertexConsumerProvider.Immediate vertexConsumers, double cameraX, double cameraY, double cameraZ, CallbackInfo ci) {
        LandingRegionRenderer.getInstance().tryRender(matrices, vertexConsumers, cameraX, cameraY, cameraZ);
    }
}
