package dev.amble.ait.mixin.server.multidim;

import java.util.List;

import dev.drtheo.multidim.MultiDim;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

@Mixin(MultiDim.class)
public class MultiDimKickFix {

    // each teleport takes the player out of the list kickPlayers is going through
    @Redirect(method = "kickPlayers", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;getPlayers()Ljava/util/List;"))
    public List<ServerPlayerEntity> copyPlayers(ServerWorld world) {
        return List.copyOf(world.getPlayers());
    }
}
