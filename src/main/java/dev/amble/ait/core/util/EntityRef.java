package dev.amble.ait.core.util;

import dev.amble.ait.data.Exclude;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;

import java.lang.ref.WeakReference;
import java.util.UUID;

/**
 * Beloved TardisRefs now available for entities!
 */
public class EntityRef<T extends Entity> {

    @Exclude
    private ServerWorld world;

    @Exclude
    private WeakReference<T> ref;

    private final UUID id;

    public EntityRef(ServerWorld world, T entity) {
        this.id = entity.getUuid();

        this.world = world;
        this.ref = new WeakReference<>(entity);
    }

    public void setWorld(ServerWorld world) {
        this.world = world;
    }

    public T get() {
        T portal = this.ref != null ? this.ref.get() : null;

        if (portal != null || this.id == null)
            return portal;

        portal = (T) this.world.getEntity(this.id);
        this.ref = new WeakReference<>(portal);

        return portal;
    }
}
