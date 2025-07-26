package dev.amble.ait.mixin.client.rendering;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemModels;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import dev.amble.ait.AITMod;
import dev.amble.ait.core.AITItems;

@Mixin(ItemRenderer.class)
public class ItemRendererMixin {

    @Unique private static final Identifier HAND_MODEL = new Identifier(AITMod.MOD_ID, "tardis_item");

    @Unique private static final ModelIdentifier HAND_MODEL_ID = new ModelIdentifier(HAND_MODEL, "inventory");

    @Final
    @Shadow
    private ItemModels models;


    @Inject(method = "getModel(Lnet/minecraft/item/ItemStack;Lnet/minecraft/world/World;Lnet/minecraft/entity/LivingEntity;I)Lnet/minecraft/client/render/model/BakedModel;", at = @At("HEAD"), cancellable = true)
    private void ait$getModel(ItemStack stack, World world, LivingEntity entity, int seed, CallbackInfoReturnable<BakedModel> cir) {
        if (stack.isOf(AITItems.TARDIS_ITEM)) {
            BakedModel bakedModel = this.models.getModelManager().getModel(HAND_MODEL_ID);
            ClientWorld clientWorld = world instanceof ClientWorld ? (ClientWorld) world : null;
            BakedModel bakedModel2 = bakedModel.getOverrides().apply(bakedModel, stack, clientWorld, entity, seed);
            cir.setReturnValue(bakedModel2 == null ? this.models.getModelManager().getMissingModel() : bakedModel2);
        }
    }

    @ModifyVariable(method = "renderItem*", at = @At(value = "HEAD"), argsOnly = true)
    public BakedModel useItem(BakedModel value, ItemStack stack, ModelTransformationMode renderMode, boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        if (stack.isOf(AITItems.TARDIS_ITEM) && renderMode != ModelTransformationMode.GUI && renderMode != ModelTransformationMode.GROUND) {
            return this.models.getModelManager().getModel(HAND_MODEL_ID);
        }
        return value;
    }
}
