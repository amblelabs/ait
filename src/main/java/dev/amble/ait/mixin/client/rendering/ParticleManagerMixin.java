package dev.amble.ait.mixin.client.rendering;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.util.Identifier;

import dev.amble.ait.client.boti.PortalParticleManager;

@Mixin(ParticleManager.class)
public class ParticleManagerMixin {

    @Redirect(method = "<init>", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/texture/TextureManager;registerTexture(Lnet/minecraft/util/Identifier;Lnet/minecraft/client/texture/AbstractTexture;)V"))
    private void ait$skipAtlasRegistration(TextureManager textureManager, Identifier id, AbstractTexture texture) {
        if (!((Object) this instanceof PortalParticleManager))
            textureManager.registerTexture(id, texture);
    }

    @Inject(method = "registerDefaultFactories", at = @At("HEAD"), cancellable = true)
    private void ait$skipDefaultFactories(CallbackInfo ci) {
        if ((Object) this instanceof PortalParticleManager)
            ci.cancel();
    }
}
