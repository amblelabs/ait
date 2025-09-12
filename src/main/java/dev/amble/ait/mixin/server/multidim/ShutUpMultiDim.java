package dev.amble.ait.mixin.server.multidim;

import dev.drtheo.multidim.MultiDim;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(MultiDim.class)
public class ShutUpMultiDim {

    @Redirect(method = "addOrLoad(Ldev/drtheo/multidim/api/WorldBlueprint;Lnet/minecraft/registry/RegistryKey;Z)Ldev/drtheo/multidim/api/MultiDimServerWorld;", remap = false, at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;getWorld(Lnet/minecraft/registry/RegistryKey;)Lnet/minecraft/server/world/ServerWorld;"))
    public ServerWorld shutUp(MinecraftServer instance, RegistryKey<World> key) {
        return null;
    }
}
