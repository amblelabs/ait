package dev.amble.ait.module.gun.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.util.Hand;

import dev.amble.ait.module.gun.core.item.BaseGunItem;

@Mixin(value = PlayerEntityRenderer.class, priority = 1002)
public abstract class PlayerEntityRendererMixin
        extends
        LivingEntityRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> {

    public PlayerEntityRendererMixin(EntityRendererFactory.Context ctx,
                                     PlayerEntityModel<AbstractClientPlayerEntity> model, float shadowRadius) {
        super(ctx, model, shadowRadius);
    }

    @Inject(at = @At("RETURN"), method = "getArmPose", cancellable = true)
    private static void ait$getArmPose(AbstractClientPlayerEntity player, Hand hand, CallbackInfoReturnable<BipedEntityModel.ArmPose> cir) {
        if (player == null) return;
        if (player.getMainHandStack().getItem() instanceof BaseGunItem) {
            if (MinecraftClient.getInstance().options.useKey.isPressed()) {
                cir.setReturnValue(BipedEntityModel.ArmPose.CROSSBOW_CHARGE);
            }
            cir.setReturnValue(BipedEntityModel.ArmPose.CROSSBOW_HOLD);
        }
    }
}
