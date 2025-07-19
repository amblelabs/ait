package dev.amble.ait.mixin.server.multidim;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

import dev.amble.ait.core.tardis.ServerTardis;
import dev.amble.ait.core.tardis.manager.ServerTardisManager;
import dev.amble.ait.core.world.TardisServerWorld;

@Mixin(PlayerManager.class)
public class MultiDimLoadFix {

    @Redirect(method = "onPlayerConnect", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;getWorld(Lnet/minecraft/registry/RegistryKey;)Lnet/minecraft/server/world/ServerWorld;"))
    public @Nullable ServerWorld getWorld(MinecraftServer instance, RegistryKey<World> key) {
        ServerWorld result = instance.getWorld(key);

        if (result != null)
            return result;

        if (TardisServerWorld.isTardisDimension(key)) {
            return ServerTardisManager.getInstance().loadTardis(instance, TardisServerWorld.getTardisId(key))
                    .map(ServerTardis::world, e -> null);
        }

        return null;
    }
}
