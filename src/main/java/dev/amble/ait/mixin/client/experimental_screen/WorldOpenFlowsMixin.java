package dev.amble.ait.mixin.client.experimental_screen;

import com.mojang.serialization.Lifecycle;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.server.integrated.IntegratedServerLoader;

import dev.amble.ait.AITMod;

@Mixin(value = IntegratedServerLoader.class)
public class WorldOpenFlowsMixin {

    @Inject(method = "tryLoad", at = @At(value = "INVOKE_ASSIGN", target = "Lcom/mojang/serialization/Lifecycle;experimental()Lcom/mojang/serialization/Lifecycle;", remap = false), cancellable = true)
    private static void confirmWorldCreation(MinecraftClient client, CreateWorldScreen parent, Lifecycle lifecycle,
            Runnable loader, boolean bypassWarnings, CallbackInfo ci) {
        if (!AITMod.CONFIG.CLIENT.SHOW_EXPERIMENTAL_WARNING) {
            loader.run();
            ci.cancel();
        }
    }
}
