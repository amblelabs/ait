package dev.amble.ait.mixin.networking;

import com.mojang.authlib.GameProfile;
import dev.amble.ait.api.tardis.TardisEvents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity {

    public ServerPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile gameProfile) {
        super(world, pos, yaw, gameProfile);
    }

    @Inject(method = "sendUnloadChunkPacket", at = @At("TAIL"))
    public void ait$sendUnloadChunkPacket(ChunkPos chunkPos, CallbackInfo ci) {
        if (this.isAlive())
            TardisEvents.UNLOAD_TARDIS.invoker().unload((ServerPlayerEntity) (Object) this, chunkPos);
    }
}
