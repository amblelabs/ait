package dev.amble.ait.mixin.compat.portals;

import dev.amble.ait.core.tardis.ServerTardis;
import dev.amble.ait.core.world.TardisServerWorld;
import dev.drtheo.multidim.MultiDim;
import dev.drtheo.multidim.api.MultiDimServerWorld;
import dev.drtheo.multidim.api.WorldBlueprint;
import net.minecraft.network.packet.Packet;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import qouteall.q_misc_util.MiscNetworking;
import qouteall.q_misc_util.api.DimensionAPI;
import qouteall.q_misc_util.dimension.DimensionIdManagement;

@Mixin(TardisServerWorld.class)
public class TardisServerWorldMixin {

    @Inject(method = "create", at = @At("RETURN"), remap = false)
    private static void create(ServerTardis tardis, CallbackInfoReturnable<TardisServerWorld> cir) {
        aitportals$handleWorld(cir.getReturnValue());
    }

    // makes sure that we don't handle the world twice in case it gets created
    @Redirect(method = "load", at = @At(value = "INVOKE", target = "Ldev/drtheo/multidim/MultiDim;load(Ldev/drtheo/multidim/api/WorldBlueprint;Lnet/minecraft/registry/RegistryKey;)Ldev/drtheo/multidim/api/MultiDimServerWorld;"))
    private static MultiDimServerWorld load(MultiDim instance, WorldBlueprint blueprint, RegistryKey<World> id) {
        return aitportals$handleWorld(instance.load(blueprint, id));
    }

    @Unique
    private static MultiDimServerWorld aitportals$handleWorld(MultiDimServerWorld world) {
        // may happen if this is called during #load and the world doesn't exist!
        if (world == null)
            return null;

        MinecraftServer server = world.getServer();
        RegistryKey<World> key = world.getRegistryKey();

        // An excerpt from `qouteall.q_misc_util.dimension.DynamicDimensionsImpl#addDimensionDynamically`
        // FIXME: this line MAY be causing issues with the relogs
        //  basically, the game crashes because it's trying to load a tardis world
        //  which shouldn't have been loaded, but fails miserably because it finds
        //  out that it's a normal server world, resulting in a cast exception.
        //  if commenting out this line wont work (still have to hear back from QA),
        //  then we should mixin into wherever it saves the world metadata and lobotomize it
        //    - Theo

        // UPDATE: commenting out this line DOES NOT fix the issue stated above.?
        // maybe? im lost.
        //DimensionAPI.saveDimensionConfiguration(key);
        DimensionIdManagement.updateAndSaveServerDimIdRecord();

        Packet<?> dimSyncPacket = MiscNetworking.createDimSyncPacket();

        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            player.networkHandler.sendPacket(dimSyncPacket);
        }

        DimensionAPI.serverDimensionDynamicUpdateEvent.invoker().run(server.getWorldRegistryKeys());
        return world;
    }
}
