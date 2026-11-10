package dev.amble.ait.mixin;

import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;
import java.util.UUID;

import dev.amble.ait.api.ExtraPushableEntity;
import dev.amble.ait.api.SiegeInventoryProvider;
import dev.amble.ait.core.item.SiegeInventoryUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.Entity;
import net.minecraft.inventory.Inventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;

@Mixin(Entity.class)
public class EntityMixin implements SiegeInventoryProvider {

    @Unique private Set<Inventory> ait$siegeInventories;
    @Unique private Set<UUID> ait$trackedSiegeItems = Set.of();

    @Inject(method = "tick", at = @At("TAIL"))
    private void ait$trackInventory(CallbackInfo ci) {
        Entity entity = (Entity) (Object) this;
        if (!(entity instanceof ServerPlayerEntity) && !entity.getWorld().isClient()
                && SiegeInventoryUtil.shouldTrack(entity)
                && Math.floorMod(entity.age + entity.getUuid().hashCode(), 200) == 0)
            SiegeInventoryUtil.track(entity);
    }

    @Override
    public Collection<? extends Inventory> ait$getSiegeInventories() {
        return this.ait$siegeInventories == null ? Collections.emptySet() : this.ait$siegeInventories;
    }

    @Override
    public void ait$registerSiegeInventory(Inventory inventory) {
        if (inventory != null) {
            if (this.ait$siegeInventories == null)
                this.ait$siegeInventories = Collections.newSetFromMap(new IdentityHashMap<>());
            this.ait$siegeInventories.add(inventory);
        }
    }

    @Override
    public void ait$unregisterSiegeInventory(Inventory inventory) {
        if (inventory != null && this.ait$siegeInventories != null) {
            this.ait$siegeInventories.remove(inventory);
            if (this.ait$siegeInventories.isEmpty())
                this.ait$siegeInventories = null;
        }
    }

    @Override
    public Set<UUID> ait$getTrackedSiegeItems() {
        return this.ait$trackedSiegeItems;
    }

    @Override
    public void ait$setTrackedSiegeItems(Set<UUID> tardisIds) {
        this.ait$trackedSiegeItems = SiegeInventoryUtil.limitTrackedIds(tardisIds);
    }

    @Inject(method = "writeNbt", at = @At("RETURN"))
    private void ait$writeTrackedSiegeItems(NbtCompound nbt, CallbackInfoReturnable<NbtCompound> cir) {
        NbtCompound written = cir.getReturnValue();
        SiegeInventoryUtil.writeTrackedCarrierIds(written == null ? nbt : written,
                this.ait$trackedSiegeItems);
    }

    @Inject(method = "readNbt", at = @At("TAIL"))
    private void ait$readTrackedSiegeItems(NbtCompound nbt, CallbackInfo ci) {
        this.ait$trackedSiegeItems = SiegeInventoryUtil.readTrackedCarrierIds(nbt);
    }

    @Inject(method = "copyFrom", at = @At("TAIL"))
    public void copyFrom(Entity original, CallbackInfo ci) {
        if (this instanceof ExtraPushableEntity extra && original instanceof ExtraPushableEntity other)
            extra.ait$setPushBehaviour(other.ait$pushBehaviour());
    }
}
