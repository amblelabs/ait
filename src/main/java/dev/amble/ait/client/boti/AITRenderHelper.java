package dev.amble.ait.client.boti;

import static org.lwjgl.opengl.GL11.GL_NO_ERROR;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.gl.Framebuffer;

import dev.amble.ait.AITMod;

public class AITRenderHelper {

    public static void checkGlError() {
        int errorCode = GL11.glGetError();
        if (errorCode != GL_NO_ERROR) {
            AITMod.LOGGER.warn("OpenGL Error" + errorCode);
            new Throwable().printStackTrace();
        }
    }

    @Environment(EnvType.CLIENT)
    public static boolean getIsStencilEnabled(Framebuffer renderTarget) {
        return ((StencilFrameBuffer) renderTarget).ait$getIsStencilBufferEnabled();
    }

    @Environment(EnvType.CLIENT)
    public static void setIsStencilEnabled(Framebuffer renderTarget, boolean cond) {
        ((StencilFrameBuffer) renderTarget).ait$setIsStencilBufferEnabledAndReload(cond);
    }

}
