package loqor.ait.mixin.networking;

import loqor.ait.api.WorldWithTardis;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin implements WorldWithTardis {

    @Unique
    private Lookup tardisLookup;

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
