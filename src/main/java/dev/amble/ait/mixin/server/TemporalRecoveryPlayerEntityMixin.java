package dev.amble.ait.mixin.server;

import java.util.Map;

import dev.amble.ait.api.TemporalItemOwnership;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

@Mixin(PlayerEntity.class)
public abstract class TemporalRecoveryPlayerEntityMixin {

    @Inject(method = "dropItem(Lnet/minecraft/item/ItemStack;ZZ)Lnet/minecraft/entity/ItemEntity;",
            at = @At("RETURN"))
    private void ait$rememberTemporalOwner(ItemStack stack, boolean throwRandomly, boolean retainOwnership,
                                           CallbackInfoReturnable<ItemEntity> cir) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        ItemEntity dropped = cir.getReturnValue();
        if (player.getWorld().isClient() || dropped == null || dropped.getStack().isEmpty())
            return;

        ((TemporalItemOwnership) dropped).ait$setTemporalOwners(
                Map.of(player.getUuid(), dropped.getStack().getCount()));
    }
}
