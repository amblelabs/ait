package dev.amble.ait.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;

import dev.amble.ait.client.util.SeamlessDoorTransition;

@Mixin(ClientPlayNetworkHandler.class)
public class SeamlessDoorNetworkMixin {

    @WrapOperation(method = "onPlayerRespawn", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/MinecraftClient;setScreen(Lnet/minecraft/client/gui/screen/Screen;)V",
            ordinal = 0))
    private void ait$skipTerrainScreen(MinecraftClient client, Screen screen, Operation<Void> original) {
        if (!SeamlessDoorTransition.isActive())
            original.call(client, screen);
    }

    @Inject(method = "onPlayerPositionLook", at = @At("TAIL"))
    private void ait$finishDoorTransition(PlayerPositionLookS2CPacket packet, CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();

        if (client.player != null)
            SeamlessDoorTransition.onPositioned(client.player);
    }
}
