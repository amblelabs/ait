package dev.amble.ait.core.entities.base;

import java.util.Optional;
import java.util.UUID;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;

import dev.amble.ait.api.tardis.link.v2.TardisRef;
import dev.amble.ait.api.tardis.link.v2.entity.AbstractLinkableEntity;

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
