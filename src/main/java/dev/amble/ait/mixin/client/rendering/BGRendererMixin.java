package dev.amble.ait.mixin.client.rendering;

import dev.amble.ait.client.util.FoggyUtils;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.Camera;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BackgroundRenderer.class)
public class BGRendererMixin {
    @Inject(at = @At("TAIL"), method = "applyFog")
    private static void applyFog(Camera camera, BackgroundRenderer.FogType fogType, float viewDistance,
            boolean thickFog, float tickDelta, CallbackInfo ci) {
        FoggyUtils.overrideFog();
    }
}
