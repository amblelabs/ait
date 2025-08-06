package dev.amble.ait.mixin.client;

import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.registry.RegistryKey;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Set;

@Mixin(ClientPlayNetworkHandler.class)
public interface ClientPlayNetworkHandlerMixin {
	@Accessor("worldKeys")
	void setWorldKeys(Set<RegistryKey<World>> worldKeys);
}
