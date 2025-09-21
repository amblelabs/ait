package dev.amble.ait.mixin.server.multidim;

import java.util.UUID;

import com.mojang.datafixers.util.Either;
import dev.amble.lib.data.CachedDirectedGlobalPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

import dev.amble.ait.core.tardis.ServerTardis;
import dev.amble.ait.core.tardis.handler.travel.TravelHandler;
import dev.amble.ait.core.tardis.manager.ServerTardisManager;
import dev.amble.ait.core.world.TardisServerWorld;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecraftServer.class)
public class MultiDimLoadFix {

    @Inject(method = "getWorld", at = @At("RETURN"), cancellable = true)
    public void getWorld(RegistryKey<World> key, CallbackInfoReturnable<ServerWorld> cir) {
        // we only override the default behaviour if we couldn't find an already loaded world 
        //  and the world we're trying to load is indeed a tardis dim
        if (cir.getReturnValue() != null || !TardisServerWorld.isTardisDimension(key))
            return;

        cir.setReturnValue(ait$loadTardisFromWorld((MinecraftServer) (Object) this, key));
    }

    @Unique
    public ServerWorld ait$loadTardisFromWorld(MinecraftServer server, RegistryKey<World> key) {
        ServerTardisManager manager = ServerTardisManager.getInstance();
        UUID id = TardisServerWorld.getTardisId(key);

        Either<ServerTardis, ?> either = manager.lookup().get(id);

        if (either == null)
            either = manager.loadTardis(server, id);

        ServerTardis tardis = either.map(t -> t, o -> null);

        if (tardis == null)
            return null;

        TravelHandler travel = tardis.travel();
        CachedDirectedGlobalPos pos = travel.position();

        // reads & loads the world, lv is used in handling recursion later on
        ServerWorld loadedWorld = TardisServerWorld.load(server, tardis);
        
        // handles situations where a tardis is inside a tardis
        if (TardisServerWorld.isTardisDimension(pos.getDimension())) {
            ServerWorld targetWorld;
            if (pos.getDimension().equals(key)) {
                targetWorld = loadedWorld;
            } else {
                targetWorld = this.ait$loadTardisFromWorld(
                    server, pos.getDimension());
            }

            if (targetWorld != null) {
                pos.init(server);
            }
        }

        // makes sure the tardis actually saves the world instance
        return tardis.world();
    }
}
