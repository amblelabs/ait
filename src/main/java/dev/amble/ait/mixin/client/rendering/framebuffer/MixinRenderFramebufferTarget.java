package dev.amble.ait.mixin.client.rendering.framebuffer;

import static org.lwjgl.opengl.GL11.GL_DEPTH_COMPONENT;
import static org.lwjgl.opengl.GL30.GL_DEPTH24_STENCIL8;
import static org.lwjgl.opengl.GL30.GL_DEPTH32F_STENCIL8;
import static org.lwjgl.opengl.GL30.GL_FLOAT_32_UNSIGNED_INT_24_8_REV;
import static org.lwjgl.opengl.GL30.GL_UNSIGNED_INT_24_8;

import java.util.Objects;

import com.mojang.blaze3d.platform.GlStateManager;
import dev.amble.ait.client.boti.StencilFrameBuffer;
import org.lwjgl.opengl.ARBFramebufferObject;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL30C;
import org.spongepowered.asm.mixin.Final;
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

@Mixin(Framebuffer.class)
public abstract class MixinRenderFramebufferTarget implements StencilFrameBuffer {

    @Unique private boolean isStencilBufferEnabled;
    @Unique private boolean useFloatDepth;
    @Unique private int depthType;

    @Shadow
    public int viewportWidth;
    @Shadow
    public int viewportHeight;
    @Shadow
    @Final
    public boolean useDepthAttachment;
    @Shadow
    protected int depthAttachment;


    @Shadow
    public abstract void resize(int width, int height, boolean clearError);

    @Inject(method = "<init>", at = @At("RETURN"))
    private void ait$Init(boolean useDepth, CallbackInfo ci) {
        isStencilBufferEnabled = false;
        depthType = -1;
    }

    @Inject(method = "initFbo", at = @At("RETURN"))
    private void ait$resetDepthType(int width, int height, boolean getError, CallbackInfo ci) {
        depthType = -1;
    }

    @ModifyArgs(method = "initFbo", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/platform/GlStateManager;_texImage2D(IIIIIIIILjava/nio/IntBuffer;)V", remap = false)
    )
    private void ait$modifyTexImage2D(Args args) {
        if (Objects.equals(args.get(2), GL_DEPTH_COMPONENT)) {
            if (isStencilBufferEnabled) {
                if (useFloatDepth) {
                    args.set(2, GL_DEPTH32F_STENCIL8);
                    args.set(6, ARBFramebufferObject.GL_DEPTH_STENCIL);
                    args.set(7, GL_FLOAT_32_UNSIGNED_INT_24_8_REV);
                } else {
                    args.set(2, GL_DEPTH24_STENCIL8);
                    args.set(6, ARBFramebufferObject.GL_DEPTH_STENCIL);
                    args.set(7, GL_UNSIGNED_INT_24_8);
                }
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

    @Override
    public boolean ait$hasFloatDepth() {
        if (!useDepthAttachment || depthAttachment <= 0)
            return false;

        if (depthType == -1) {
            GlStateManager._bindTexture(depthAttachment);
            depthType = GlStateManager._getTexLevelParameter(GL11.GL_TEXTURE_2D, 0, GL30.GL_TEXTURE_DEPTH_TYPE);
            GlStateManager._bindTexture(0);
        }

        return depthType == GL11.GL_FLOAT;
    }

    @Override
    public void ait$setFloatDepthAndReload(boolean cond) {
        if (useFloatDepth != cond) {
            useFloatDepth = cond;
            resize(viewportWidth, viewportHeight, MinecraftClient.IS_SYSTEM_MAC);
        }
    }
}
