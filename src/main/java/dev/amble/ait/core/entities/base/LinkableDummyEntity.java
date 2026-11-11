package dev.amble.ait.core.entities.base;

import java.util.Optional;
import java.util.UUID;

import dev.amble.ait.api.tardis.link.v2.TardisRef;
import dev.amble.ait.api.tardis.link.v2.entity.AbstractLinkableEntity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;

/**
 * <h2>IMPORTANT: TARDIS syncing is tied to entity tracking</h2>
 * Whenever a player starts tracking a linked entity, {@link dev.amble.ait.core.tardis.manager.ServerTardisManager}
 * sends that player the <b>entire</b> linked TARDIS. Keep two things in mind when writing or using linkable entities:
 * <ul>
 *     <li><b>Ordering is not guaranteed.</b> The entity's spawn packet can reach the client before its TARDIS data,
 *     so client-side code (renderers, models, sounds, HUDs) must not assume {@code tardis().get()} is non-null or
 *     fully populated on the first ticks or frames after the entity appears.</li>
 *     <li><b>Every tracked linkable entity costs a full TARDIS sync.</b> Spawning many of them, or ones that
 *     players start and stop tracking often, repeatedly resends the whole TARDIS. Avoid that.</li>
 * </ul>
 */
public class LinkableDummyEntity extends DummyEntity implements AbstractLinkableEntity {

    private static final TrackedData<Optional<UUID>> TARDIS = AbstractLinkableEntity
            .register(LinkableDummyEntity.class);

    private TardisRef tardis;

    public LinkableDummyEntity(EntityType<?> type, World world) {
        super(type, world);
    }

    /**
     * Used by {@link AbstractLinkableEntity}, do not remove.
     */
    @Override
    public World getWorld() {
        return super.getWorld();
    }

    /**
     * Used by {@link AbstractLinkableEntity}, do not remove.
     */
    @Override
    public DataTracker getDataTracker() {
        return super.getDataTracker();
    }

    @Override
    public TrackedData<Optional<UUID>> getTracked() {
        return TARDIS;
    }

    @Override
    public TardisRef asRef() {
        return this.tardis;
    }

    @Override
    public void setRef(TardisRef ref) {
        this.tardis = ref;
    }

    @Override
    public void initDataTracker() {
        super.initDataTracker();
        AbstractLinkableEntity.super.initDataTracker();
    }

    @Override
    public void onTrackedDataSet(TrackedData<?> data) {
        super.onTrackedDataSet(data);
        AbstractLinkableEntity.super.onTrackedDataSet(data);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        AbstractLinkableEntity.super.readCustomDataFromNbt(nbt);
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        AbstractLinkableEntity.super.writeCustomDataToNbt(nbt);
    }
}
