package dev.amble.ait.client.boti;


import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.Framebuffer;


public class AITRenderHelper {

    @Environment(EnvType.CLIENT)
    public static boolean getIsStencilEnabled(Framebuffer renderTarget) {
        return ((StencilFrameBuffer) renderTarget).ait$getIsStencilBufferEnabled();
    }

    @Environment(EnvType.CLIENT)
    public static void setIsStencilEnabled(Framebuffer renderTarget, boolean cond) {
        ((StencilFrameBuffer) renderTarget).ait$setIsStencilBufferEnabledAndReload(cond);
    }

}
