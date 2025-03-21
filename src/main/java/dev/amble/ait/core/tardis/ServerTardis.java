package dev.amble.ait.core.tardis;

import java.lang.reflect.Type;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

import com.google.gson.InstanceCreator;
import org.jetbrains.annotations.ApiStatus;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import dev.amble.ait.api.tardis.TardisComponent;
import dev.amble.ait.core.world.TardisServerWorld;
import dev.amble.ait.data.Exclude;
import dev.amble.ait.data.schema.desktop.TardisDesktopSchema;
import dev.amble.ait.data.schema.exterior.ExteriorVariantSchema;
import dev.amble.ait.registry.impl.TardisComponentRegistry;

public class ServerTardis extends Tardis {

    @Exclude(strategy = Exclude.Strategy.NETWORK)
    protected int version = 3;

    @Exclude
    private boolean removed;

    // maybe use sparseset?
    @Exclude
    private final Set<TardisComponent> delta = TardisComponentRegistry.createDelta();

    @Exclude
    private ServerWorld world;

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
        this.getHandlers().tick(server);

        // tell interior players how to fix growth every 10 seconds
        if (this.isGrowth() && server.getTicks() % 200 == 0 && !this.interiorChangingHandler().queued().get()) {
            if (this.interiorChangingHandler().hasEnoughPlasmicMaterial())
                this.getInteriorWorld().getPlayers().forEach(player -> player.sendMessage(Text.translatable("tardis.message.growth.hint").formatted(Formatting.DARK_GRAY,Formatting.ITALIC), true));
        }
    }

    @ApiStatus.Internal
    public void markDirty(TardisComponent component) {
        if (component == null)
            return;

        if (component.tardis() != this)
            return;

        this.delta.add(component);
    }

    @ApiStatus.Internal
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

    public ServerWorld getInteriorWorld() {
        if (this.world == null)
            this.world = TardisServerWorld.get(this);

        if (this.world == null)
            this.world = TardisServerWorld.create(this);

        return this.world;
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
