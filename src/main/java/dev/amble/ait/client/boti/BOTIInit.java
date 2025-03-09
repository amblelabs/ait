package dev.amble.ait.client.boti;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.SimpleFramebuffer;
import net.minecraft.client.util.Window;

public class BOTIInit {
    public Framebuffer afbo;

    public void setupFramebuffer() {
        Window window = MinecraftClient.getInstance().getWindow();

        if (afbo == null || afbo.textureWidth != window.getFramebufferWidth() || afbo.textureHeight != window.getFramebufferHeight()) {
            afbo = new SimpleFramebuffer(window.getFramebufferWidth(), window.getFramebufferHeight(), true, MinecraftClient.IS_SYSTEM_MAC);;
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
