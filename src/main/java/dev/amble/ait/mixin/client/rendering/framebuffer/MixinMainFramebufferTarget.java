package dev.amble.ait.mixin.client.rendering.framebuffer;

import dev.amble.ait.client.boti.StencilFrameBuffer;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.WindowFramebuffer;
import org.lwjgl.opengl.ARBFramebufferObject;
import org.lwjgl.opengl.GL30;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import static org.lwjgl.opengl.GL30.GL_DEPTH32F_STENCIL8;
import static org.lwjgl.opengl.GL30.GL_FLOAT_32_UNSIGNED_INT_24_8_REV;

@Mixin(WindowFramebuffer.class)
public abstract class MixinMainFramebufferTarget extends Framebuffer {

    public MixinMainFramebufferTarget(boolean useDepth) {
        super(useDepth);
        throw new RuntimeException();
    }

    @ModifyArgs(method = "supportsDepth", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/platform/GlStateManager;_texImage2D(IIIIIIIILjava/nio/IntBuffer;)V", remap = false))
    private void ait$modifyTexImage2D(Args args) {
        boolean isStencilBufferEnabled = ((StencilFrameBuffer) this).ait$getIsStencilBufferEnabled();

        if (isStencilBufferEnabled) {
            args.set(2, GL_DEPTH32F_STENCIL8);
            args.set(6, ARBFramebufferObject.GL_DEPTH_STENCIL);
            args.set(7, GL_FLOAT_32_UNSIGNED_INT_24_8_REV);
        }
    }

    @ModifyArgs(method = "init", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/platform/GlStateManager;_glFramebufferTexture2D(IIIII)V", remap = false))
    private void ait$modifyFrameBufferTexture2d(Args args) {
        boolean isStencilBufferEnabled = ((StencilFrameBuffer) this).ait$getIsStencilBufferEnabled();

        if (isStencilBufferEnabled) {
            if ((int) args.get(1) == GL30.GL_DEPTH_ATTACHMENT) {
                args.set(1, GL30.GL_DEPTH_STENCIL_ATTACHMENT);
            }
        }
    }

}
