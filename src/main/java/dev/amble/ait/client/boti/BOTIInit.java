package dev.amble.ait.client.boti;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.SimpleFramebuffer;
import net.minecraft.client.util.Window;

public class BOTIInit {
    public Framebuffer afbo;

    public void setupFramebuffer() {
        Window window = MinecraftClient.getInstance().getWindow();
        int width = window.getFramebufferWidth();
        int height = window.getFramebufferHeight();

        if (afbo == null) {
            afbo = new SimpleFramebuffer(width, height, true, MinecraftClient.IS_SYSTEM_MAC);
        } else if (afbo.textureWidth != width || afbo.textureHeight != height) {
            // resize() reinitialises the GL textures in place (deleting the old ones). Allocating a fresh
            // SimpleFramebuffer here instead - as before - orphaned the previous FBO + depth-stencil texture on
            // every window resize.
            afbo.resize(width, height, MinecraftClient.IS_SYSTEM_MAC);
        }

        afbo.beginWrite(false);
        afbo.checkFramebufferStatus();

        if (!AITRenderHelper.getIsStencilEnabled(afbo)) {
            AITRenderHelper.setIsStencilEnabled(afbo, true);
        }
    }

    public void endFBO() {
        afbo.clear(MinecraftClient.IS_SYSTEM_MAC);
        afbo.endWrite();
    }

}
