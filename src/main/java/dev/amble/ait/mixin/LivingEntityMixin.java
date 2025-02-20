package dev.amble.ait.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import dev.amble.ait.core.AITTags;
import dev.amble.ait.core.world.TardisServerWorld;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {

    @Shadow public abstract ItemStack getEquippedStack(EquipmentSlot var1);

    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    public void ait$tick(CallbackInfo ci) {
        LivingEntity entity = (LivingEntity) (Object) this;

        if (entity instanceof PlayerEntity player
                && (player.isCreative() || player.isSpectator()))
             return;

        if (entity.getWorld() instanceof TardisServerWorld tardisWorld
                && !tardisWorld.getTardis().subsystems().lifeSupport().isEnabled()) {
            ItemStack stack = entity.getEquippedStack(EquipmentSlot.HEAD);

            if (!stack.isIn(AITTags.Items.FULL_RESPIRATORS) || !stack.isIn(AITTags.Items.HALF_RESPIRATORS)) {
                entity.addStatusEffect(new StatusEffectInstance(StatusEffects.WITHER, 1,
                        200, false, false));
                entity.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS,
                        200, 1, false, false));
            }
        }
    }
}
