package dev.amble.ait.core.tardis;

import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

import com.google.gson.InstanceCreator;
import dev.amble.lib.data.CachedDirectedGlobalPos;

import net.minecraft.server.MinecraftServer;

import dev.amble.ait.api.tardis.TardisComponent;
import dev.amble.ait.core.world.TardisServerWorld;
import dev.amble.ait.data.Exclude;
import dev.amble.ait.data.schema.desktop.TardisDesktopSchema;
import dev.amble.ait.data.schema.exterior.ExteriorVariantSchema;

import dev.drtheo.scheduler.api.TimeUnit;
import dev.drtheo.scheduler.api.common.Scheduler;
import dev.drtheo.scheduler.api.common.TaskStage;

public class ServerTardis extends Tardis {

    @Exclude(strategy = Exclude.Strategy.NETWORK)
    protected int version = 2;

    @Exclude
    private boolean removed;

    @Exclude
    private final Set<TardisComponent> delta = new HashSet<>(32);

    @Exclude
    private TardisServerWorld world;

    @Exclude
    private boolean fullyInitialized;

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
    protected void postInit(TardisComponent.InitContext ctx) {
        Scheduler.get().runTaskLater(() -> {
            this.fullyInitialized = true;
            super.postInit(ctx);
        }, TaskStage.END_SERVER_TICK, TimeUnit.TICKS, 1);
    }

    public void setRemoved(boolean removed) {
        this.removed = removed;
    }

    public boolean isRemoved() {
        return removed;
    }

    public void tick(MinecraftServer server) {
        if (!this.fullyInitialized) return;
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
        if (this.world == null) {
            this.world = TardisServerWorld.load(this);
        }

        return world;
    }

    public boolean shouldTick() {
        if (world == null)
            return false;

        return !this.travel().isLanded() || world.shouldTick() || this.shouldTickExterior();
    }

    public boolean shouldTickExterior() {
        CachedDirectedGlobalPos pos = this.travel().position();
        return pos.getWorld() != null && pos.getWorld()
                .shouldTickEntity(pos.getPos());
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
