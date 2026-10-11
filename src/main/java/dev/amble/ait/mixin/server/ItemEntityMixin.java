package dev.amble.ait.mixin.server;

import java.util.Objects;
import java.util.UUID;

import dev.amble.ait.AITMod;
import dev.amble.ait.core.tardis.ServerTardis;
import dev.amble.ait.core.tardis.util.TardisUtil;
import dev.amble.ait.core.world.TardisServerWorld;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.entity.ItemEntity;
import net.minecraft.world.World;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin {

    @Shadow @Nullable private UUID thrower;

    @Inject(method = "tryMerge(Lnet/minecraft/entity/ItemEntity;)V", at = @At("HEAD"), cancellable = true)
    private void ait$preventRejectedItemMerge(ItemEntity other, CallbackInfo ci) {
        ItemEntity entity = (ItemEntity) (Object) this;
        if (!(entity.getWorld() instanceof TardisServerWorld tardisWorld)
                || AITMod.CONFIG == null || !AITMod.CONFIG.tardisTemperament)
            return;

        UUID otherThrower = ((ItemEntityAccessor) other).ait$getThrower();
        ServerTardis tardis = tardisWorld.getTardis();
        if (!Objects.equals(this.thrower, otherThrower)
                && (tardis.temperament().isReject(this.thrower)
                || tardis.temperament().isReject(otherThrower)))
            ci.cancel();
    }

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void ait$tick(CallbackInfo ci) {
        ItemEntity entity = (ItemEntity) (Object) this;
        World world = entity.getWorld();

        if (world.isClient() || entity.getY() >= world.getBottomY()
                || !(world instanceof TardisServerWorld tardisWorld)
                || tardisWorld.getTardis().interiorChanging().regenerating().get())
            return;

        ServerTardis tardis = tardisWorld.getTardis();
        if (this.thrower != null && AITMod.CONFIG != null && AITMod.CONFIG.tardisTemperament
                && tardis.temperament().isReject(this.thrower)) {
            entity.discard();
            ci.cancel();
            return;
        }

        TardisUtil.teleportInside(tardis, entity);
    }
}
