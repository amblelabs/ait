package dev.amble.ait.api.tardis.link.v2;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

import dev.amble.ait.api.tardis.Disposable;
import dev.amble.ait.core.tardis.Tardis;
import dev.amble.ait.core.tardis.TardisManager;

@SuppressWarnings({"deprecation", "unused"})
public class TardisRef implements Disposable {

    private final LoadFunc load;
    private UUID id;

    private Tardis cached;

    public TardisRef(UUID id, LoadFunc load) {
        this.id = id;
        this.load = load;
    }

    public TardisRef(Tardis tardis, LoadFunc load) {
        if (tardis != null)
            this.id = tardis.getUuid();

        this.load = load;
        this.cached = tardis;
    }

    public static TardisRef createAs(Entity entity, Tardis tardis) {
        return new TardisRef(tardis, real -> TardisManager.with(entity, (o, manager) -> manager.demandTardis(o, real)));
    }

    public static TardisRef createAs(Entity entity, UUID uuid) {
        return new TardisRef(uuid, real -> TardisManager.with(entity, (o, manager) -> manager.demandTardis(o, real)));
    }

    public static TardisRef createAs(BlockEntity blockEntity, Tardis tardis) {
        return new TardisRef(tardis,
                real -> TardisManager.with(blockEntity, (o, manager) -> manager.demandTardis(o, real)));
    }

    public static TardisRef createAs(BlockEntity blockEntity, UUID uuid) {
        return new TardisRef(uuid,
                real -> TardisManager.with(blockEntity, (o, manager) -> manager.demandTardis(o, real)));
    }

    public static TardisRef createAs(World world, Tardis tardis) {
        return new TardisRef(tardis, real -> TardisManager.with(world, (o, manager) -> manager.demandTardis(o, real)));
    }

    public static TardisRef createAs(World world, UUID uuid) {
        return new TardisRef(uuid, real -> TardisManager.with(world, (o, manager) -> manager.demandTardis(o, real)));
    }

    public Tardis get() {
        if (this.cached != null && !this.shouldInvalidate())
            return this.cached;

        if (this.id == null)
            return null;

        this.cached = this.load.apply(this.id);
        return this.cached;
    }

    private boolean shouldInvalidate() {
        return this.cached instanceof Disposable disposable && disposable.isAged();
    }

    public UUID getId() {
        return id;
    }

    public boolean isPresent() {
        return this.get() != null;
    }

    public boolean isEmpty() {
        return this.get() == null;
    }

    /**
     * @return the result of the function, {@literal null} otherwise.
     */
    public <T> Optional<T> apply(Function<Tardis, T> consumer) {
        if (this.isPresent())
            return Optional.of(consumer.apply(this.cached));

        return Optional.empty();
    }

    public void ifPresent(Consumer<Tardis> consumer) {
        if (this.isPresent())
            consumer.accept(this.get());
    }

    @Override
    public void dispose() {
        this.cached = null;
    }

    public boolean contains(Tardis tardis) {
        return this.get() == tardis;
    }

    public interface LoadFunc extends Function<UUID, Tardis> {
    }
}
