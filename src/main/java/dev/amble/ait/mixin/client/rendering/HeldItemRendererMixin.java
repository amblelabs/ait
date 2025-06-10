package dev.amble.ait.mixin.client.rendering;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.UseAction;

import dev.amble.ait.api.AITUseActions;
import dev.amble.ait.core.AITTags;

@Mixin(HeldItemRenderer.class)
public class HeldItemRendererMixin {

    @Shadow
    @Final
    private MinecraftClient client;

    @Inject(method = "applyEquipOffset", at = @At("HEAD"), cancellable = true)
    private void applyEquipProgress(MatrixStack matrices, Arm arm, float equipProgress, CallbackInfo ci) {
        if (this.client.player == null)
            return;

        PlayerEntity player = this.client.player;
        ItemStack stack = player.getActiveItem();

        if (noBop(stack)) {
            int i = arm == Arm.RIGHT ? 1 : -1;
            matrices.translate((float) i * 0.56F, -0.52F, -0.72F);

            ci.cancel();
        }
    }

    @Redirect(method = "renderFirstPersonItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getUseAction()Lnet/minecraft/util/UseAction;"))
    private UseAction getUseAction(ItemStack instance) {
        UseAction result = instance.getUseAction();

        return result == ((AITUseActions) (Object) UseAction.NONE).ait$sonic()
                ? UseAction.NONE : result;
    }

    @Unique private static boolean noBop(ItemStack stack) {
        return stack.getRegistryEntry().isIn(AITTags.Items.NO_BOP);
    }
}
