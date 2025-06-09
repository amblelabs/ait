package dev.amble.ait.core.tardis.manager;

import dev.amble.ait.AITMod;
import dev.amble.ait.api.tardis.TardisComponent;
import dev.amble.ait.core.tardis.ServerTardis;
import dev.amble.ait.core.tardis.Tardis;
import dev.amble.ait.core.tardis.handler.LoyaltyHandler;
import dev.amble.ait.core.tardis.handler.StatsHandler;
import dev.amble.ait.core.world.TardisServerWorld;
import dev.amble.ait.data.Loyalty;
import dev.amble.ait.data.schema.desktop.TardisDesktopSchema;
import dev.amble.ait.data.schema.exterior.ExteriorVariantSchema;
import dev.amble.ait.registry.impl.DesktopRegistry;
import dev.amble.ait.registry.impl.exterior.ExteriorVariantRegistry;
import dev.amble.lib.data.CachedDirectedGlobalPos;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public class TardisBuilder {

    private final UUID uuid;
    private CachedDirectedGlobalPos pos;
    private TardisDesktopSchema desktop;
    private ExteriorVariantSchema exterior;

    private final List<Consumer<ServerTardis>> postInit = new ArrayList<>();

    public TardisBuilder(UUID uuid) {
        this.uuid = uuid;
    }

    public TardisBuilder() {
        this(UUID.randomUUID());
    }

    public TardisBuilder at(CachedDirectedGlobalPos pos) {
        this.pos = pos;
        return this;
    }

    public TardisBuilder desktop(TardisDesktopSchema desktop) {
        this.desktop = desktop;
        return this;
    }

    public TardisBuilder exterior(ExteriorVariantSchema exterior) {
        this.exterior = exterior;
        return this;
    }

    public <T extends TardisComponent> TardisBuilder with(TardisComponent.Id id, Consumer<T> consumer) {
        this.postInit.add(tardis -> {
            T t = tardis.handler(id);
            consumer.accept(t);
        });

        return this;
    }

    public TardisBuilder owner(ServerPlayerEntity player) {
        return this.<StatsHandler>with(TardisComponent.Id.STATS, stats -> {
            stats.setPlayerCreatorName(player.getName().getString());
            stats.markPlayerCreatorName();
        }).<LoyaltyHandler>with(TardisComponent.Id.LOYALTY,
                loyalty -> loyalty.set(player, new Loyalty(Loyalty.Type.COMPANION)));
    }

    private void validate() {
        if (this.pos == null)
            throw new IllegalStateException("Tried to create a TARDIS at null position!");

        if (this.desktop == null) {
            AITMod.LOGGER.warn("No desktop schema supplied for TardisBuilder, choosing a random one!");
            this.desktop = DesktopRegistry.getInstance().getRandom();
        }

        if (this.exterior == null) {
            AITMod.LOGGER.warn("No exterior variant schema supplied for TardisBuilder, choosing a random one!");
            this.exterior = ExteriorVariantRegistry.getInstance().getRandom();
        }
    }

    public ServerTardis build() {
        long start = System.currentTimeMillis();
        this.validate();

        ServerTardis tardis = new ServerTardis(this.uuid, this.desktop, this.exterior);

        long worldStart = System.currentTimeMillis();
        ServerWorld world = TardisServerWorld.create(tardis);
        AITMod.LOGGER.info("Created world {} in {}ms", world, System.currentTimeMillis() - worldStart);

        Tardis.init(tardis, TardisComponent.InitContext.createdAt(this.pos));

        for (Consumer<ServerTardis> consumer : this.postInit) {
            consumer.accept(tardis);
        }

        AITMod.LOGGER.info("Built {} in {}ms", tardis, System.currentTimeMillis() - start);
        return tardis;
    }
}
