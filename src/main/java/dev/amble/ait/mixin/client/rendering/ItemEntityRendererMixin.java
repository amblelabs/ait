package dev.amble.ait.mixin.client.rendering;

import dev.amble.ait.client.renderers.PhoneCableRenderer;
import dev.amble.ait.core.item.PhoneItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.ItemEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.ItemEntity;

@Mixin(ItemEntityRenderer.class)
public class ItemEntityRendererMixin {

    @Inject(method = "render(Lnet/minecraft/entity/ItemEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At("TAIL"))
    private void ait$renderPhoneCable(ItemEntity item, float yaw, float tickDelta, MatrixStack matrices,
                                      VertexConsumerProvider consumers, int light, CallbackInfo ci) {
        if (item.getStack().getItem() instanceof PhoneItem)
            PhoneCableRenderer.renderDropped(item, tickDelta, matrices, consumers);
    }
}
