package dev.amble.ait.mixin.compat.portals;

import dev.drtheo.multidim.MultiDim;
import dev.drtheo.multidim.api.MultiDimServer;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import qouteall.q_misc_util.api.DimensionAPI;

@Mixin(MultiDim.class)
public class MultiDimMixin {

    @Shadow(remap = false) @Final protected MinecraftServer server;

    @Redirect(method = "remove", at = @At(value = "INVOKE", target = "Ldev/drtheo/multidim/api/MultiDimServer;multidim$removeWorld(Lnet/minecraft/registry/RegistryKey;)Lnet/minecraft/server/world/ServerWorld;"))
    public ServerWorld remove(MultiDimServer instance, RegistryKey<World> key) {
        ServerWorld world = server.getWorld(key);

        if (world == null)
            return null;

        DimensionAPI.removeDimensionDynamically(world);
        return world;
    }
}
