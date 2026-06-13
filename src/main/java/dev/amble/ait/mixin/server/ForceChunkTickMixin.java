package dev.amble.ait.mixin.server;

import dev.amble.ait.api.ForcedTickableWorld;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerWorld.class)
public class ForceChunkTickMixin implements ForcedTickableWorld {

    @Unique private int forced = 0;

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;getForcedChunks()Lit/unimi/dsi/fastutil/longs/LongSet;", ordinal = 0))
    private LongSet tick(ServerWorld instance) {
        LongSet forcedChunks = instance.getForcedChunks();
        return forcedChunks.isEmpty() && forced > 0 ? LongSet.of(0L) : forcedChunks;
    }

    @Override
    public void setForcedTicked() {
        forced++;
    }

    @Override
    public void unsetForcedTicked() {
        forced--;
    }
}
