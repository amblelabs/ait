package loqor.ait.api.link;

import java.util.Optional;
import java.util.UUID;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;

import loqor.ait.api.link.v2.Linkable;
import loqor.ait.api.link.v2.TardisRef;
import loqor.ait.core.tardis.ServerTardis;

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

    public void link(ServerTardis tardis) {
        this.link(tardis.getUuid());
    }

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
