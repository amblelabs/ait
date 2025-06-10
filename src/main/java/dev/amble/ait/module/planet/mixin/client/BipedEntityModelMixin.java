package dev.amble.ait.module.planet.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;

import dev.amble.ait.module.planet.core.item.SpacesuitItem;

@Mixin(value = BipedEntityModel.class, priority = 1001)
public class BipedEntityModelMixin<T extends LivingEntity> {
    @Inject(method = "setAngles(Lnet/minecraft/entity/LivingEntity;FFFFF)V", at = @At("HEAD"))
    private void ait$setAngles(T livingEntity, float f, float g, float h, float i, float j, CallbackInfo ci) {
        if (livingEntity instanceof AbstractClientPlayerEntity) {
            return;
        }

        BipedEntityModel model = (BipedEntityModel) (Object) this;

        model.head.visible = !(livingEntity.getEquippedStack(EquipmentSlot.HEAD).getItem() instanceof SpacesuitItem);
        model.body.visible = !(livingEntity.getEquippedStack(EquipmentSlot.CHEST).getItem() instanceof SpacesuitItem);
        model.leftArm.visible = !(livingEntity.getEquippedStack(EquipmentSlot.MAINHAND).getItem() instanceof SpacesuitItem);
        model.rightArm.visible = !(livingEntity.getEquippedStack(EquipmentSlot.OFFHAND).getItem() instanceof SpacesuitItem);
        model.leftLeg.visible = !(livingEntity.getEquippedStack(EquipmentSlot.LEGS).getItem() instanceof SpacesuitItem);
        model.rightLeg.visible = !(livingEntity.getEquippedStack(EquipmentSlot.LEGS).getItem() instanceof SpacesuitItem);
    }
}
