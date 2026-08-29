package dev.amble.ait.client.boti;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.SimpleFramebuffer;
import net.minecraft.client.util.Window;

import dev.amble.ait.AITMod;

public class BOTIInit {
    public Framebuffer afbo;

    /** One-shot flag: dump the afbo's real stencil attachment state to the log once, to diagnose the Mac smear. */
    private static boolean DIAG_LOGGED = false;

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

        logStencilDiagnostics();
    }

    /**
     * Dumps, exactly once, whether the bound afbo actually has usable stencil storage on this GPU. If the smear is
     * caused by a missing/zero-bit stencil attachment (so glStencilFunc(EQUAL,1) always "passes" and the interior
     * draws over the whole screen), this is where it shows: stencilSize will be 0 or the attachment OBJECT_TYPE NONE.
     */
    private void logStencilDiagnostics() {
        if (DIAG_LOGGED)
            return;
        DIAG_LOGGED = true;

        // afbo is already bound as GL_FRAMEBUFFER via beginWrite above.
        GL11.glGetError(); // clear any pre-existing error so the readings below are attributable

        int status = GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER);

        int stencilType = GL30.glGetFramebufferAttachmentParameteri(GL30.GL_FRAMEBUFFER,
                GL30.GL_STENCIL_ATTACHMENT, GL30.GL_FRAMEBUFFER_ATTACHMENT_OBJECT_TYPE);
        int errType = GL11.glGetError();

        int stencilSize = GL30.glGetFramebufferAttachmentParameteri(GL30.GL_FRAMEBUFFER,
                GL30.GL_STENCIL_ATTACHMENT, GL30.GL_FRAMEBUFFER_ATTACHMENT_STENCIL_SIZE);
        int errSize = GL11.glGetError();

        int depthType = GL30.glGetFramebufferAttachmentParameteri(GL30.GL_FRAMEBUFFER,
                GL30.GL_DEPTH_ATTACHMENT, GL30.GL_FRAMEBUFFER_ATTACHMENT_OBJECT_TYPE);
        int errDepth = GL11.glGetError();

        AITMod.LOGGER.error("[BOTI-DIAG] mac={} vendor={} renderer={} version={}", MinecraftClient.IS_SYSTEM_MAC,
                GL11.glGetString(GL11.GL_VENDOR), GL11.glGetString(GL11.GL_RENDERER), GL11.glGetString(GL11.GL_VERSION));
        AITMod.LOGGER.error("[BOTI-DIAG] afbo.fbo={} fbStatus=0x{} (COMPLETE=0x{}) stencilFlag={}", afbo.fbo,
                Integer.toHexString(status), Integer.toHexString(GL30.GL_FRAMEBUFFER_COMPLETE),
                AITRenderHelper.getIsStencilEnabled(afbo));
        AITMod.LOGGER.error("[BOTI-DIAG] stencilAttachmentType=0x{} (NONE=0x{}) stencilBits={} depthAttachmentType=0x{} errs[type=0x{} size=0x{} depth=0x{}]",
                Integer.toHexString(stencilType), Integer.toHexString(GL11.GL_NONE), stencilSize,
                Integer.toHexString(depthType), Integer.toHexString(errType), Integer.toHexString(errSize),
                Integer.toHexString(errDepth));
    }

    public void endFBO() {
        afbo.clear(MinecraftClient.IS_SYSTEM_MAC);
        afbo.endWrite();
    }

}
