package dev.amble.ait.api.tardis.link.v2.entity;

import java.util.Optional;
import java.util.UUID;

import net.minecraft.entity.Entity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.world.World;

import dev.amble.ait.api.tardis.link.v2.Linkable;
import dev.amble.ait.api.tardis.link.v2.TardisRef;
import dev.amble.ait.core.tardis.Tardis;

public interface AbstractLinkableEntity extends Linkable {

    World getWorld();

    DataTracker getDataTracker();

    TrackedData<Optional<UUID>> getTracked();

    TardisRef asRef();

    void setRef(TardisRef ref);

    @Override
    default void link(UUID id) {
        this.setRef(TardisRef.createAs(this.getWorld(), id));
        this.getDataTracker().set(this.getTracked(), Optional.ofNullable(id));
    }

    @Override
    default void link(Tardis tardis) {
        this.setRef(TardisRef.createAs(this.getWorld(), tardis));
        this.getDataTracker().set(this.getTracked(), Optional.of(tardis.getUuid()));
    }

    @Override
    default TardisRef tardis() {
        TardisRef result = this.asRef();

        if (result == null) {
            this.link(this.getDataTracker().get(this.getTracked()).orElse(null));
            return this.tardis();
        }

        return result;
    }

    default void initDataTracker() {
        this.getDataTracker().startTracking(this.getTracked(), Optional.empty());
    }

    default void onTrackedDataSet(TrackedData<?> data) {
        if (!this.getTracked().equals(data))
            return;

        this.link(this.getDataTracker().get(this.getTracked()).orElse(null));
    }

    default void readCustomDataFromNbt(NbtCompound nbt) {
        NbtElement id = nbt.get("tardis");

        if (id == null)
            return;

        this.link(NbtHelper.toUuid(id));

        if (this.getWorld() == null)
            return;

        this.onLinked();
    }

    default void writeCustomDataToNbt(NbtCompound nbt) {
        TardisRef ref = this.asRef();

        if (ref != null && ref.getId() != null)
            nbt.putUuid("tardis", ref.getId());
    }

    static <T extends Entity & AbstractLinkableEntity> TrackedData<Optional<UUID>> register(Class<T> self) {
        return DataTracker.registerData(self, TrackedDataHandlerRegistry.OPTIONAL_UUID);
    }
}
