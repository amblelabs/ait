package dev.amble.ait.mixin.server;

import dev.amble.ait.core.item.SiegeTardisItem;
import dev.amble.ait.core.tardis.Tardis;
import dev.amble.ait.core.tardis.util.TardisUtil;
import dev.amble.ait.core.world.TardisServerWorld;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// mmm mixin i love mixin
@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin {

    @Inject(method = "tick", at = @At("HEAD"))
    private void ait$tick(CallbackInfo ci) {
        ItemEntity entity = (ItemEntity) (Object) this;
        ItemStack stack = entity.getStack();

        if (entity.getWorld().isClient())
            return;

        if (stack.getItem() instanceof SiegeTardisItem item) {
            Tardis found = item.getTardis(entity.getWorld(), stack);

            if (found == null)
                return;

            // kill ourselves and place down the exterior
            SiegeTardisItem.placeTardis(found, SiegeTardisItem.fromEntity(entity));
            entity.kill();
        }

        // if entity is in tardis and y is less than -100 save them
        if (entity.getY() <= -100 && entity.getWorld() instanceof TardisServerWorld tardisWorld)
            TardisUtil.teleportInside(tardisWorld.getTardis(), entity);
    }
}
