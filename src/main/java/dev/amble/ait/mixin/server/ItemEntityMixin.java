package dev.amble.ait.mixin.server;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.entity.ItemEntity;
import net.minecraft.world.World;

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

        // if entity is in tardis and y is less than the TARDIS' bottom coordinate (currently -64), teleport it to the door
        if (entity.getY() < entity.getWorld().getBottomY() && world instanceof TardisServerWorld tardisWorld
                && !tardisWorld.getTardis().interiorChanging().regenerating().get())
            TardisUtil.teleportInside(tardisWorld.getTardis(), entity);
    }
}
