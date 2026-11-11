package dev.amble.ait.api.tardis.link;

import java.util.Optional;
import java.util.UUID;

import dev.amble.ait.api.tardis.link.v2.Linkable;
import dev.amble.ait.api.tardis.link.v2.TardisRef;
import dev.amble.ait.core.tardis.Tardis;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
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
public abstract class LinkableLivingEntity extends LivingEntity implements Linkable {

    public static final TrackedData<Optional<UUID>> TARDIS_ID  = DataTracker.registerData(
            LinkableLivingEntity.class, TrackedDataHandlerRegistry.OPTIONAL_UUID);

    private TardisRef cache;

    protected LinkableLivingEntity(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);

        UUID id = nbt.getUuid("Tardis");

        if (id != null)
            this.link(id);
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);

        this.tardisId().ifPresent(id -> nbt.putUuid("Tardis", id));
    }

    @Override
    public void link(Tardis tardis) {
        this.link(tardis.getUuid());
    }

    @Override
    public void link(UUID id) {
        this.dataTracker.set(TARDIS_ID, Optional.of(id));
        this.createCache(id);
    }

    private void reloadCache() {
        UUID id = this.tardisId().orElse(null);

        if (id == null)
            return;

        this.createCache(id);
    }

    private void createCache(UUID id) {
        this.cache = TardisRef.createAs(this, id);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(TARDIS_ID, Optional.empty());
    }

    @Override
    public void onTrackedDataSet(TrackedData<?> data) {
        super.onTrackedDataSet(data);

        if (TARDIS_ID.equals(data))
            this.reloadCache();
    }

    public TardisRef tardis() {
        if (this.cache != null)
            return cache;

        this.reloadCache();
        return cache;
    }

    private Optional<UUID> tardisId() {
        return this.dataTracker.get(TARDIS_ID);
    }
}
