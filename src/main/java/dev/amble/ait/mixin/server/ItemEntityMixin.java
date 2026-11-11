package dev.amble.ait.mixin.server;

import dev.amble.ait.api.HomeCaptureAware;
import dev.amble.ait.core.engine.impl.HomeEntityCapture;
import dev.amble.ait.core.tardis.util.TardisUtil;
import dev.amble.ait.core.world.TardisServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.entity.ItemEntity;
import net.minecraft.world.World;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin implements HomeCaptureAware {
    @Unique private boolean ait$homeCaptureChecked;
    @Unique private boolean ait$homeCaptureExcluded;

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void ait$tick(CallbackInfo ci) {
        ItemEntity entity = (ItemEntity) (Object) this;
        World world = entity.getWorld();

        if (world.isClient())
            return;

        if (!this.ait$homeCaptureChecked) {
            this.ait$homeCaptureChecked = true;
            if (HomeEntityCapture.tryCaptureExistingItem(entity)) {
                ci.cancel();
                return;
            }
        }

        if (entity.getY() < entity.getWorld().getBottomY() && world instanceof TardisServerWorld tardisWorld
                && !tardisWorld.getTardis().interiorChanging().regenerating().get())
            TardisUtil.teleportInside(tardisWorld.getTardis(), entity);
    }

    @Override
    public boolean ait$isHomeCaptureExcluded() {
        return this.ait$homeCaptureExcluded;
    }

    @Override
    public void ait$setHomeCaptureExcluded(boolean excluded) {
        this.ait$homeCaptureExcluded = excluded;
    }
}
