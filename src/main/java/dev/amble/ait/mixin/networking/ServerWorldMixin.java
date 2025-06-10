package dev.amble.ait.mixin.networking;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import net.minecraft.server.world.ServerWorld;

import dev.amble.ait.api.tardis.WorldWithTardis;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin implements WorldWithTardis {

    @Unique private Lookup tardisLookup;

    @Override
    public Lookup ait$lookup() {
        if (tardisLookup == null)
            tardisLookup = new Lookup();

        return tardisLookup;
    }

    @Override
    public boolean ait$hasLookup() {
        return this.tardisLookup != null;
    }
}
