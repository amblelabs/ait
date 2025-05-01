package dev.amble.ait.core.tardis;

import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

import com.google.gson.InstanceCreator;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;

import dev.amble.ait.api.tardis.TardisComponent;
import dev.amble.ait.core.tardis.handler.travel.TravelHandler;
import dev.amble.ait.core.util.Lazy;
import dev.amble.ait.core.world.TardisServerWorld;
import dev.amble.ait.data.Exclude;
import dev.amble.ait.data.schema.desktop.TardisDesktopSchema;
import dev.amble.ait.data.schema.exterior.ExteriorVariantSchema;

public class ServerTardis extends Tardis {

    @Exclude(strategy = Exclude.Strategy.NETWORK)
    protected int version = 2;

    @Exclude
    private boolean removed;

    @Exclude
    private final Set<TardisComponent> delta = new HashSet<>(32);

    @Exclude
    private final Lazy<ServerWorld> world = new Lazy<>(this::getOrCreateWorld);

    public ServerTardis(UUID uuid, TardisDesktopSchema schema, ExteriorVariantSchema variantType) {
        super(uuid, new TardisDesktop(schema), new TardisExterior(variantType));
    }

    private ServerTardis() {
        super();
    }

    public void setRemoved(boolean removed) {
        this.removed = removed;
    }

    public boolean isRemoved() {
        return removed;
    }

    public void tick(MinecraftServer server) {
        this.world.get(); // force load the world
        this.getHandlers().tick(server);
    }

    public void markDirty(TardisComponent component) {
        if (component == null)
            return;

        if (component.tardis() != this)
            return;

        this.delta.add(component);
    }

    public void consumeDelta(Consumer<TardisComponent> consumer) {
        if (this.delta.isEmpty())
            return;

        for (TardisComponent component : this.delta) {
            consumer.accept(component);
        }

        this.delta.clear();
    }

    public boolean hasDelta() {
        return !this.delta.isEmpty();
    }

    public int getDeltaSize() {
        return this.delta.size();
    }

    public Lazy<ServerWorld> worldRef() {
        return world;
    }

    private ServerWorld getOrCreateWorld() {
        ServerWorld world = TardisServerWorld.get(this);

        // If its still null, It's likely to be pre-1.2.0, meaning we should create a new one.
        if (world == null)
            world = TardisServerWorld.create(this);

        return world;
    }

    public boolean shouldTick() {
        if (this.world.isCached() && !this.world.get().getPlayers().isEmpty())
            return true;

        TravelHandler travel = this.travel();

        if (travel == null)
            return false;

        return travel.position().getWorld().shouldTickEntity(travel.position().getPos());
    }

    public static Object creator() {
        return new ServerTardisCreator();
    }

    static class ServerTardisCreator implements InstanceCreator<ServerTardis> {

        @Override
        public ServerTardis createInstance(Type type) {
            return new ServerTardis();
        }
    }
}
