package dev.amble.ait.mixin.client.rendering.framebuffer;

import static org.lwjgl.opengl.GL11.GL_DEPTH_COMPONENT;
import static org.lwjgl.opengl.GL30.GL_DEPTH24_STENCIL8;
import static org.lwjgl.opengl.GL30.GL_UNSIGNED_INT_24_8;

import java.util.Objects;

import org.lwjgl.opengl.ARBFramebufferObject;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL30C;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;

import dev.amble.ait.client.boti.StencilFrameBuffer;

@Mixin(Framebuffer.class)
public abstract class MixinRenderFramebufferTarget implements StencilFrameBuffer {

    @Unique private boolean isStencilBufferEnabled;

    @Shadow
    public int viewportWidth;
    @Shadow
    public int viewportHeight;


    @Shadow
    public abstract void resize(int width, int height, boolean clearError);

    @Inject(method = "<init>", at = @At("RETURN"))
    private void ait$Init(boolean useDepth, CallbackInfo ci) {
        isStencilBufferEnabled = false;
    }

    @ModifyArgs(method = "initFbo", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/platform/GlStateManager;_texImage2D(IIIIIIIILjava/nio/IntBuffer;)V", remap = false)
    )
    private void ait$modifyTexImage2D(Args args) {
        if (Objects.equals(args.get(2), GL_DEPTH_COMPONENT)) {
            if (isStencilBufferEnabled) {
                args.set(2, GL_DEPTH24_STENCIL8);
                args.set(6, ARBFramebufferObject.GL_DEPTH_STENCIL);
                args.set(7, GL_UNSIGNED_INT_24_8);
            }
        }
    }

    @ModifyArgs(method = "initFbo", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/platform/GlStateManager;_glFramebufferTexture2D(IIIII)V", remap = false))
    private void ait$modifyFrameBufferTexture2D(Args args) {
        if (Objects.equals(args.get(1), GL30C.GL_DEPTH_ATTACHMENT)) {
            if (isStencilBufferEnabled) {
                args.set(1, GL30.GL_DEPTH_STENCIL_ATTACHMENT);
            }
        }
    }

    /*@Inject(method = "copyDepthFrom", at = @At("RETURN"))
    private void onCopiedDepthFrom(Framebuffer framebuffer, CallbackInfo ci) {
        AITModClient.doCheckGlError();
    }*/

    @Override
    public boolean ait$getIsStencilBufferEnabled() {
        return isStencilBufferEnabled;
    }

    @Override
    public void ait$setIsStencilBufferEnabledAndReload(boolean cond) {
        if (isStencilBufferEnabled != cond) {
            isStencilBufferEnabled = cond;
            resize(viewportWidth, viewportHeight, MinecraftClient.IS_SYSTEM_MAC);
        }
    }
}
