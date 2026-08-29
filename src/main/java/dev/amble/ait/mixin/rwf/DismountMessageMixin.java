package dev.amble.ait.mixin.rwf;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import dev.amble.ait.core.entities.FlightTardisEntity;

@Mixin(ClientPlayNetworkHandler.class)
public class DismountMessageMixin {

    @Redirect(method = "onEntityPassengersSet", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/text/Text;translatable(Ljava/lang/String;[Ljava/lang/Object;)Lnet/minecraft/text/MutableText;"))
    private MutableText ait$dismountMessage(String key, Object[] args) {
        MinecraftClient client = MinecraftClient.getInstance();

        if (client.player != null && client.player.getVehicle() instanceof FlightTardisEntity)
            return Text.translatable("overlay.ait.rwf.dismount", args);

        return Text.translatable(key, args);
    }
}