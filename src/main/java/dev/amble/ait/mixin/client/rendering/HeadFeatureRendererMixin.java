package dev.amble.ait.mixin.client.rendering;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.HeadFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;

import dev.amble.ait.core.item.RenderableArmorItem;

@Mixin(HeadFeatureRenderer.class)
public abstract class HeadFeatureRendererMixin<T extends LivingEntity, M extends EntityModel<T>> extends FeatureRenderer<T, M> {

    public HeadFeatureRendererMixin(FeatureRendererContext<T, M> context) {
        super(context);
    }

    @Inject(method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/LivingEntity;FFFFFF)V", at = @At("TAIL"), cancellable = true)
    public void ait$render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i,
            T livingEntity, float f, float g, float h, float j, float k, float l, CallbackInfo ci) {
        if (livingEntity.getEquippedStack(EquipmentSlot.HEAD).getItem() instanceof RenderableArmorItem armor && armor.hasCustomRendering()) {
            ci.cancel();
        }
    }
}
