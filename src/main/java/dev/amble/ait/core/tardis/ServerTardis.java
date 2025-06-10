package dev.amble.ait.core.tardis;

import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

import com.google.gson.InstanceCreator;
import dev.amble.lib.util.ServerLifecycleHooks;
import dev.drtheo.multidim.MultiDim;

import net.minecraft.server.MinecraftServer;

import dev.amble.ait.api.tardis.TardisComponent;
import dev.amble.ait.core.tardis.handler.travel.TravelHandler;
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
    private TardisServerWorld world;

    public ServerTardis(UUID uuid, TardisDesktopSchema schema, ExteriorVariantSchema variantType) {
        super(uuid, new TardisDesktop(schema), new TardisExterior(variantType));
    }

    private ServerTardis() {
        super();
    }

    @Override
    public void onCreate() {
        this.world = TardisServerWorld.create(this);
    }

    @Override
    public void onLoaded() {
        this.world = TardisServerWorld.load(this);
    }

    public void setRemoved(boolean removed) {
        this.removed = removed;
    }

    public boolean isRemoved() {
        return removed;
    }

    public void tick(MinecraftServer server) {
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

    public TardisServerWorld world() {
        return world;
    }

    public boolean shouldTick() {
        if (!MultiDim.get(ServerLifecycleHooks.get()).isWorldUnloaded(world))
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
