package dev.amble.ait.mixin.client.rendering;

import dev.amble.ait.api.AITUseActions;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;

@Mixin(BipedEntityModel.class)
public class SonicPointingMixin<T extends LivingEntity> {

    @Shadow
    @Final
    public ModelPart head;

    @Shadow
    @Final
    public ModelPart rightArm;

    @Shadow
    @Final
    public ModelPart leftArm;

    @Inject(method = "setAngles(Lnet/minecraft/entity/LivingEntity;FFFFF)V", at = @At("TAIL"))
    private void ait$pointSonic(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw,
                                float headPitch, CallbackInfo ci) {
        if (!entity.isUsingItem() || entity.getActiveItem().getUseAction() != AITUseActions.SONIC)
            return;

        Arm arm = entity.getActiveHand() == Hand.MAIN_HAND ? entity.getMainArm() : entity.getMainArm().getOpposite();
        ModelPart part = arm == Arm.RIGHT ? this.rightArm : this.leftArm;

        part.pitch = this.head.pitch - MathHelper.HALF_PI;
        part.yaw = this.head.yaw;
        part.roll = 0;
    }
}
