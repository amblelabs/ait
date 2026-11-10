package dev.amble.ait.mixin.client;

import dev.amble.ait.api.ClientWorldEvents;
import dev.amble.ait.client.util.SeamlessDoorTransition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.world.ClientWorld;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {

    @Shadow
    private void render(boolean tick) {
    }

    @Inject(method = "setWorld", at = @At("TAIL"))
    public void setWorld(ClientWorld world, CallbackInfo ci) {
        ClientWorldEvents.CHANGE_WORLD.invoker().onChange((MinecraftClient) (Object) this, world);
    }

    @Redirect(method = "reset", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/MinecraftClient;setScreen(Lnet/minecraft/client/gui/screen/Screen;)V"))
    private void ait$seamlessResetScreen(MinecraftClient client, Screen screen) {
        if (!SeamlessDoorTransition.isActive())
            client.setScreen(screen);
    }

    @Redirect(method = "reset", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/MinecraftClient;render(Z)V"))
    private void ait$seamlessResetRender(MinecraftClient client, boolean tick) {
        if (!SeamlessDoorTransition.isActive())
            this.render(tick);
    }
}
