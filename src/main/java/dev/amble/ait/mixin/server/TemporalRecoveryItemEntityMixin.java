package dev.amble.ait.mixin.server;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import dev.amble.ait.AITMod;
import dev.amble.ait.api.TemporalItemOwnership;
import dev.amble.ait.core.tardis.manager.TemporalRecoveryState;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;

@Mixin(ItemEntity.class)
public abstract class TemporalRecoveryItemEntityMixin implements TemporalItemOwnership {
    @Unique private static final String TEMPORAL_OWNERS_KEY = "AITTemporalOwners";
    @Unique private static final String TEMPORAL_OWNER_KEY = "Owner";
    @Unique private static final String TEMPORAL_COUNT_KEY = "Count";

    @Shadow @Nullable private UUID thrower;

    @Unique private boolean ait$temporalRecorded;
    @Unique private boolean ait$mergedThisTick;
    @Unique private boolean ait$mergeInProgress;
    @Unique private boolean ait$temporalDestructionSuppressed;
    @Unique private Map<UUID, Integer> ait$temporalOwners;
    @Unique private ItemEntity ait$pendingMergeOther;
    @Unique private int ait$pendingThisCount;
    @Unique private int ait$pendingOtherCount;
    @Unique private Map<UUID, Integer> ait$pendingThisOwners;
    @Unique private Map<UUID, Integer> ait$pendingOtherOwners;

    @Inject(method = "tryMerge(Lnet/minecraft/entity/ItemEntity;)V", at = @At("HEAD"), cancellable = true)
    private void ait$prepareTemporalMerge(ItemEntity other, CallbackInfo ci) {
        ItemEntity entity = (ItemEntity) (Object) this;
        UUID otherThrower = ((ItemEntityAccessor) other).ait$getThrower();
        TemporalItemOwnership otherOwnership = (TemporalItemOwnership) other;
        this.ait$clearPendingMerge();

        if (!this.ait$hasTemporalOwners() && !otherOwnership.ait$hasTemporalOwners()
                && Objects.equals(this.thrower, otherThrower)) {
            this.ait$beginTemporalMerge(otherOwnership);
            return;
        }

        this.ait$prepareMergeState(entity, other, otherThrower);
        if (hasMixedRecoverability(this.ait$pendingThisOwners, this.ait$pendingOtherOwners)) {
            // Recoverable and unattributed units must stay separate. A destruction
            // callback cannot safely restore only part of one merged stack.
            this.ait$clearPendingMerge();
            ci.cancel();
            return;
        }

        this.ait$beginTemporalMerge(otherOwnership);
    }

    @Inject(method = "tryMerge(Lnet/minecraft/entity/ItemEntity;)V", at = @At("RETURN"))
    private void ait$finishTemporalMerge(ItemEntity other, CallbackInfo ci) {
        ItemEntity entity = (ItemEntity) (Object) this;
        this.ait$mergeInProgress = false;
        ((TemporalItemOwnership) other).ait$setTemporalMergeInProgress(false);
        if (other != this.ait$pendingMergeOther || this.ait$pendingThisOwners == null
                || this.ait$pendingOtherOwners == null)
            return;

        int thisCount = entity.isRemoved() ? 0 : entity.getStack().getCount();
        int otherCount = other.isRemoved() ? 0 : other.getStack().getCount();
        int thisGain = thisCount - this.ait$pendingThisCount;
        int otherGain = otherCount - this.ait$pendingOtherCount;

        if (thisGain > 0 && otherGain == -thisGain) {
            appendOwners(this.ait$pendingThisOwners, takeOwners(this.ait$pendingOtherOwners, thisGain));
        } else if (otherGain > 0 && thisGain == -otherGain) {
            appendOwners(this.ait$pendingOtherOwners, takeOwners(this.ait$pendingThisOwners, otherGain));
        } else {
            this.ait$clearPendingMerge();
            return;
        }

        ((TemporalItemOwnership) entity).ait$setTemporalOwners(
                normalizeOwners(this.ait$pendingThisOwners, thisCount));
        ((TemporalItemOwnership) other).ait$setTemporalOwners(
                normalizeOwners(this.ait$pendingOtherOwners, otherCount));
        if (entity.isRemoved())
            ((TemporalItemOwnership) entity).ait$markTemporalMerge();
        if (other.isRemoved())
            ((TemporalItemOwnership) other).ait$markTemporalMerge();

        this.ait$clearPendingMerge();
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void ait$resetTemporalTickState(CallbackInfo ci) {
        this.ait$mergeInProgress = false;
        this.ait$mergedThisTick = false;
    }

    @Inject(method = "tick", at = @At("RETURN"))
    private void ait$recordConfirmedTickRemoval(CallbackInfo ci) {
        ItemEntity entity = (ItemEntity) (Object) this;
        if (!this.ait$mergedThisTick && !this.ait$temporalRecorded && entity.isRemoved()
                && entity.getRemovalReason() != null && entity.getRemovalReason().shouldDestroy()
                && (entity.getItemAge() >= 6_000
                || entity.getY() < entity.getWorld().getBottomY() - 64))
            this.ait$recordTemporal(entity, entity.getStack().copy());
    }

    @Redirect(method = "damage", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/item/ItemStack;onItemEntityDestroyed(Lnet/minecraft/entity/ItemEntity;)V"))
    private void ait$recordBeforeDestructionCallback(ItemStack stack, ItemEntity entity) {
        // A recovered stack keeps its complete NBT. Running the destruction callback
        // as well could spill nested contents and duplicate them on recovery.
        if (!this.ait$recordTemporal(entity, stack.copy()))
            stack.onItemEntityDestroyed(entity);
    }

    @Inject(method = "damage", at = @At("RETURN"))
    private void ait$recordDamagedStack(DamageSource source, float amount,
                                        CallbackInfoReturnable<Boolean> cir) {
        ItemEntity entity = (ItemEntity) (Object) this;
        if (cir.getReturnValueZ() && !this.ait$temporalRecorded && entity.isRemoved()
                && entity.getRemovalReason() != null && entity.getRemovalReason().shouldDestroy())
            this.ait$recordTemporal(entity, entity.getStack().copy());
    }

    @Unique private boolean ait$recordTemporal(ItemEntity entity, ItemStack stack) {
        if (this.ait$temporalRecorded || this.ait$temporalDestructionSuppressed
                || this.ait$mergeInProgress || stack == null || stack.isEmpty()
                || entity.getServer() == null)
            return this.ait$temporalRecorded;

        boolean recorded = false;
        try {
            TemporalRecoveryState state = TemporalRecoveryState.get(entity.getServer());
            Map<UUID, Integer> owners = normalizeOwners(this.ait$temporalOwners, stack.getCount());
            if (owners == null || owners.isEmpty()) {
                recorded = state.recordDestroyed(entity, stack, this.thrower);
                this.ait$temporalRecorded = recorded;
                return recorded;
            }

            int attributed = owners.values().stream().mapToInt(value -> Math.max(0, value)).sum();
            if (attributed != stack.getCount() || owners.containsKey(null))
                return false;

            int remaining = stack.getCount();
            for (Map.Entry<UUID, Integer> entry : owners.entrySet()) {
                int count = Math.min(remaining, Math.max(0, entry.getValue()));
                if (count <= 0 || entry.getKey() == null)
                    continue;

                recorded |= state.recordDestroyed(entity, stack.copyWithCount(count), entry.getKey());
                remaining -= count;
                if (remaining <= 0)
                    break;
            }
        } catch (RuntimeException exception) {
            AITMod.LOGGER.error("Failed to record destroyed temporal item {} for {}", stack, this.thrower,
                    exception);
        }

        this.ait$temporalRecorded = recorded;
        return recorded;
    }

    @Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
    private void ait$writeTemporalOwners(NbtCompound nbt, CallbackInfo ci) {
        Map<UUID, Integer> owners = normalizeOwners(this.ait$temporalOwners,
                ((ItemEntity) (Object) this).getStack().getCount());
        if (owners == null || owners.isEmpty())
            return;

        NbtList list = new NbtList();
        owners.forEach((owner, count) -> {
            if (owner == null || count == null || count <= 0)
                return;

            NbtCompound entry = new NbtCompound();
            entry.putUuid(TEMPORAL_OWNER_KEY, owner);
            entry.putInt(TEMPORAL_COUNT_KEY, count);
            list.add(entry);
        });
        if (!list.isEmpty())
            nbt.put(TEMPORAL_OWNERS_KEY, list);
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
    private void ait$readTemporalOwners(NbtCompound nbt, CallbackInfo ci) {
        NbtList list = nbt.getList(TEMPORAL_OWNERS_KEY, NbtElement.COMPOUND_TYPE);
        if (list.isEmpty()) {
            this.ait$temporalOwners = null;
            return;
        }

        Map<UUID, Integer> owners = new LinkedHashMap<>();
        for (int i = 0; i < list.size(); i++) {
            NbtCompound entry = list.getCompound(i);
            if (entry.containsUuid(TEMPORAL_OWNER_KEY))
                owners.merge(entry.getUuid(TEMPORAL_OWNER_KEY),
                        Math.max(0, entry.getInt(TEMPORAL_COUNT_KEY)), Integer::sum);
        }
        this.ait$temporalOwners = normalizeOwners(owners,
                ((ItemEntity) (Object) this).getStack().getCount());
    }

    @Inject(method = "copy", at = @At("RETURN"))
    private void ait$copyTemporalOwners(CallbackInfoReturnable<ItemEntity> cir) {
        ItemEntity copy = cir.getReturnValue();
        if (copy != null)
            ((TemporalItemOwnership) copy).ait$setTemporalOwners(this.ait$temporalOwners);
    }

    @Override
    public boolean ait$hasTemporalOwners() {
        return this.ait$temporalOwners != null && !this.ait$temporalOwners.isEmpty();
    }

    @Override
    public Map<UUID, Integer> ait$getTemporalOwners() {
        return this.ait$temporalOwners == null ? null : new LinkedHashMap<>(this.ait$temporalOwners);
    }

    @Override
    public void ait$setTemporalOwners(Map<UUID, Integer> owners) {
        this.ait$temporalOwners = owners == null || owners.isEmpty()
                ? null : new LinkedHashMap<>(owners);
    }

    @Override
    public void ait$markTemporalMerge() {
        this.ait$mergedThisTick = true;
        this.ait$temporalRecorded = true;
    }

    @Override
    public void ait$setTemporalMergeInProgress(boolean merging) {
        this.ait$mergeInProgress = merging;
    }

    @Override
    public boolean ait$isTemporalMergeInProgress() {
        return this.ait$mergeInProgress;
    }

    @Override
    public void ait$suppressTemporalDestruction() {
        this.ait$temporalDestructionSuppressed = true;
    }

    @Override
    public boolean ait$recordTemporalDestruction() {
        if (this.ait$temporalDestructionSuppressed || this.ait$mergeInProgress)
            return false;

        ItemEntity entity = (ItemEntity) (Object) this;
        return this.ait$recordTemporal(entity, entity.getStack().copy());
    }

    @Unique private void ait$beginTemporalMerge(TemporalItemOwnership other) {
        this.ait$mergeInProgress = true;
        other.ait$setTemporalMergeInProgress(true);
    }

    @Unique private void ait$prepareMergeState(ItemEntity entity, ItemEntity other, UUID otherThrower) {
        this.ait$pendingMergeOther = other;
        this.ait$pendingThisCount = entity.getStack().getCount();
        this.ait$pendingOtherCount = other.getStack().getCount();
        this.ait$pendingThisOwners = ownershipDistribution(this.ait$temporalOwners, this.thrower,
                this.ait$pendingThisCount);
        this.ait$pendingOtherOwners = ownershipDistribution(
                ((TemporalItemOwnership) other).ait$getTemporalOwners(), otherThrower,
                this.ait$pendingOtherCount);
    }

    @Unique private void ait$clearPendingMerge() {
        this.ait$pendingMergeOther = null;
        this.ait$pendingThisCount = 0;
        this.ait$pendingOtherCount = 0;
        this.ait$pendingThisOwners = null;
        this.ait$pendingOtherOwners = null;
    }

    @Unique private static Map<UUID, Integer> ownershipDistribution(Map<UUID, Integer> stored,
                                                                    UUID fallback, int count) {
        Map<UUID, Integer> result = new LinkedHashMap<>();
        boolean hadStoredDistribution = stored != null && !stored.isEmpty();
        int remaining = count;
        if (stored != null) {
            for (Map.Entry<UUID, Integer> entry : stored.entrySet()) {
                int contribution = Math.min(remaining, Math.max(0, entry.getValue()));
                if (contribution > 0)
                    result.merge(entry.getKey(), contribution, Integer::sum);
                remaining -= contribution;
                if (remaining <= 0)
                    break;
            }
        }

        if (remaining > 0)
            result.merge(hadStoredDistribution || fallback == null ? null : fallback,
                    remaining, Integer::sum);
        return result;
    }

    @Unique private static Map<UUID, Integer> takeOwners(Map<UUID, Integer> source, int count) {
        Map<UUID, Integer> taken = new LinkedHashMap<>();
        var iterator = source.entrySet().iterator();
        int remaining = count;
        while (iterator.hasNext() && remaining > 0) {
            Map.Entry<UUID, Integer> entry = iterator.next();
            int contribution = Math.min(remaining, Math.max(0, entry.getValue()));
            if (contribution > 0)
                taken.merge(entry.getKey(), contribution, Integer::sum);

            int left = entry.getValue() - contribution;
            if (left <= 0)
                iterator.remove();
            else
                entry.setValue(left);
            remaining -= contribution;
        }
        return taken;
    }

    @Unique private static void appendOwners(Map<UUID, Integer> destination, Map<UUID, Integer> source) {
        source.forEach((owner, count) -> {
            if (count != null && count > 0)
                destination.merge(owner, count, Integer::sum);
        });
    }

    @Unique private static boolean hasMixedRecoverability(Map<UUID, Integer> first,
                                                           Map<UUID, Integer> second) {
        return (hasRecoverableUnits(first) || hasRecoverableUnits(second))
                && (hasUnattributedUnits(first) || hasUnattributedUnits(second));
    }

    @Unique private static boolean hasRecoverableUnits(Map<UUID, Integer> owners) {
        return owners != null && owners.entrySet().stream().anyMatch(entry -> entry.getValue() != null
                && entry.getValue() > 0 && entry.getKey() != null);
    }

    @Unique private static boolean hasUnattributedUnits(Map<UUID, Integer> owners) {
        return owners != null && owners.entrySet().stream().anyMatch(entry -> entry.getValue() != null
                && entry.getValue() > 0 && entry.getKey() == null);
    }

    @Unique private static Map<UUID, Integer> normalizeOwners(Map<UUID, Integer> source,
                                                              int expectedCount) {
        if (source == null || source.isEmpty())
            return null;

        int remaining = Math.max(0, expectedCount);
        Map<UUID, Integer> result = new LinkedHashMap<>();
        for (Map.Entry<UUID, Integer> entry : source.entrySet()) {
            int count = Math.min(remaining, Math.max(0, entry.getValue()));
            if (count > 0)
                result.merge(entry.getKey(), count, Integer::sum);
            remaining -= count;
            if (remaining <= 0)
                break;
        }

        return result.isEmpty() ? null : result;
    }
}
