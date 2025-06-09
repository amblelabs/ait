package dev.amble.ait.mixin.client;

import dev.amble.ait.client.models.items.GeigerCounterModel;
import dev.amble.ait.client.models.items.HandlesModel;
import dev.amble.ait.client.models.items.RiftScannerModel;
import dev.amble.ait.core.AITItems;
import dev.amble.ait.module.planet.core.PlanetItems;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemRenderer.class)
public class ItemRendererMixin {

    @Unique private final RiftScannerModel riftScannerModel = new RiftScannerModel(RiftScannerModel.getTexturedModelData().createModel());
    @Unique private final GeigerCounterModel geigerCounterModel = new GeigerCounterModel(GeigerCounterModel.getTexturedModelData().createModel());
    @Unique private final HandlesModel handlesModel = new HandlesModel(HandlesModel.getTexturedModelData().createModel());

    @Inject(method = "renderItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformationMode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/world/World;III)V", at = @At("HEAD"), cancellable = true)
    public void renderItem(LivingEntity entity, ItemStack stack, ModelTransformationMode renderMode, boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumers, @Nullable World world, int light, int overlay, int seed, CallbackInfo ci) {
        if (stack.isEmpty()) return;

        if (stack.isOf(AITItems.RIFT_SCANNER)) {
            this.ait$handleRiftScannerRendering(entity, stack, renderMode, leftHanded, matrices, vertexConsumers, world, light, overlay, seed, ci);
        }

        if (stack.isOf(AITItems.GEIGER_COUNTER)) {
            this.ait$handleGeigerCounterRendering(entity, stack, renderMode, leftHanded, matrices, vertexConsumers, world, light, overlay, seed, ci);
        }

        if (stack.isOf(PlanetItems.HANDLES)) {
            this.ait$handleHandlesRendering(entity, stack, renderMode, leftHanded, matrices, vertexConsumers, world, light, overlay, seed, ci);
        }
    }

    @Inject(method = "renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformationMode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IILnet/minecraft/client/render/model/BakedModel;)V", at = @At("HEAD"), cancellable = true)
    private void renderItem(ItemStack stack, ModelTransformationMode renderMode, boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, BakedModel model, CallbackInfo ci) {
        if (stack.isEmpty()) return;

        if (stack.isOf(AITItems.RIFT_SCANNER)) {
            this.ait$handleRiftScannerRendering(null, stack, renderMode, leftHanded, matrices, vertexConsumers, null, light, overlay, 0, ci);
        }

        if (stack.isOf(AITItems.GEIGER_COUNTER)) {
            this.ait$handleGeigerCounterRendering(null, stack, renderMode, leftHanded, matrices, vertexConsumers, null, light, overlay, 0, ci);
        }
        if (stack.isOf(PlanetItems.HANDLES)) {
            this.ait$handleHandlesRendering(null, stack, renderMode, leftHanded, matrices, vertexConsumers, null, light, overlay, 0, ci);
        }
    }

    @Unique private void ait$handleRiftScannerRendering(LivingEntity entity, ItemStack stack, ModelTransformationMode renderMode, boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumers, @Nullable World world, int light, int overlay, int seed, CallbackInfo ci) {
        if (!stack.isOf(AITItems.RIFT_SCANNER))
            return;

        matrices.push();

        matrices.translate(-0.5f, -0.5f, -0.5f);
        matrices.scale(1.0f, -1.0f, -1.0f);

        // render model here
        riftScannerModel.setAngles(matrices, renderMode, leftHanded);

        ClientWorld clientWorld = world instanceof ClientWorld ? (ClientWorld) world : null;
        riftScannerModel.render(clientWorld, entity, stack, matrices, vertexConsumers, light, overlay, seed);

        matrices.pop();
        ci.cancel();
    }

    @Unique private void ait$handleGeigerCounterRendering(LivingEntity entity, ItemStack stack, ModelTransformationMode renderMode, boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumers, @Nullable World world, int light, int overlay, int seed, CallbackInfo ci) {
        if (!stack.isOf(AITItems.GEIGER_COUNTER))
            return;

        matrices.push();

        matrices.translate(-0.5f, -0.5f, -0.5f);
        matrices.scale(1.0f, -1.0f, -1.0f);

        // render model here
        geigerCounterModel.setAngles(matrices, renderMode, leftHanded);

        ClientWorld clientWorld = world instanceof ClientWorld ? (ClientWorld) world : null;
        geigerCounterModel.render(clientWorld, entity, stack, matrices, vertexConsumers, light, overlay, seed);

        matrices.pop();
        ci.cancel();
    }

    @Unique private void ait$handleHandlesRendering(LivingEntity entity, ItemStack stack, ModelTransformationMode renderMode, boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumers, @Nullable World world, int light, int overlay, int seed, CallbackInfo ci) {
        if (!stack.isOf(PlanetItems.HANDLES))
            return;

        matrices.push();

        matrices.translate(-0.5f, -0.5f, -0.5f);
        matrices.scale(1.0f, -1.0f, -1.0f);

        // render model here
        handlesModel.setAngles(matrices, renderMode, leftHanded);

        ClientWorld clientWorld = world instanceof ClientWorld ? (ClientWorld) world : null;
        handlesModel.render(clientWorld, entity, stack, matrices, vertexConsumers, light, overlay, seed);

        matrices.pop();
        ci.cancel();
    }
}
