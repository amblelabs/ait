package dev.amble.ait.mixin;

import net.fabricmc.fabric.api.util.TriState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import dev.amble.ait.api.ExtraPushableEntity;
import dev.amble.ait.core.AITDimensions;
import dev.amble.ait.core.AITTags;
import dev.amble.ait.core.util.WorldUtil;
import dev.amble.ait.core.world.TardisServerWorld;
import dev.amble.lib.util.TeleportUtil;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements ExtraPushableEntity {

    @Unique private TriState ait$pushable = TriState.DEFAULT;

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

        ItemStack stack = entity.getEquippedStack(EquipmentSlot.HEAD);

        if (stack.isIn(AITTags.Items.FULL_RESPIRATORS) || stack.isIn(AITTags.Items.HALF_RESPIRATORS))
            return;

        if (entity.getWorld() instanceof TardisServerWorld tardisWorld && !tardisWorld.getTardis().isGrowth()
                && !tardisWorld.getTardis().subsystems().lifeSupport().isEnabled()) {
            entity.addStatusEffect(new StatusEffectInstance(StatusEffects.WITHER, 1,
                    200, false, false));
            entity.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS,
                    200, 1, false, false));
        }
    }

    @Override
    public void ait$setPushBehaviour(TriState pushable) {
        this.ait$pushable = pushable;
    }

    @Override
    public TriState ait$pushBehaviour() {
        return ait$pushable;
    }

    @Inject(method = "isPushable", at = @At("RETURN"), cancellable = true)
    public void isPushable(CallbackInfoReturnable<Boolean> cir) {
        boolean pushable = cir.getReturnValueZ();

        if (this.ait$pushable != TriState.DEFAULT)
            pushable = this.ait$pushable.get();

        cir.setReturnValue(pushable);
    }

    @Inject(method = "tickInVoid", at = @At("HEAD"))
    public void tickVoid(CallbackInfo ci) {
        if (!this.getWorld().isClient() && this.getWorld().getRegistryKey() == AITDimensions.TIME_VORTEX_WORLD) {
            LivingEntity entity = (LivingEntity) (Object) this;
            int worldIndex = this.getWorld().getRandom().nextInt(WorldUtil.RIFT_DROP_WORLDS.size());

            TeleportUtil.teleport(entity, WorldUtil.RIFT_DROP_WORLDS.get(worldIndex),
                    entity.getPos().add(2, -entity.getY(), -2), entity.getYaw());
        }
    }
}
