package dev.amble.ait.mixin.server;

import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.entity.ItemEntity;

import dev.amble.ait.core.tardis.util.TardisUtil;
import dev.amble.ait.core.world.TardisServerWorld;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin {

    @Inject(method = "tick", at = @At("HEAD"))
    private void ait$tick(CallbackInfo ci) {
        ItemEntity entity = (ItemEntity) (Object) this;
        World world = entity.getWorld();

        if (world.isClient())
            return;

        // if entity is in tardis and y is less than -100 save them
        if (entity.getY() < -entity.getWorld().getBottomY() && world instanceof TardisServerWorld tardisWorld
                && !tardisWorld.getTardis().interiorChanging().regenerating().get())
            TardisUtil.teleportInside(tardisWorld.getTardis(), entity);
    }
}
