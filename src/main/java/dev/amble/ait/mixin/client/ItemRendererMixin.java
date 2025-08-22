package dev.amble.ait.mixin.client;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import dev.amble.ait.AITMod;
import dev.amble.ait.client.models.decoration.*;
import dev.amble.ait.client.models.items.GeigerCounterModel;
import dev.amble.ait.client.models.items.HandlesModel;
import dev.amble.ait.client.models.items.RiftScannerModel;
import dev.amble.ait.core.AITBlocks;
import dev.amble.ait.core.AITItems;
import dev.amble.ait.core.blocks.decoration.BrassStatueBlock;
import dev.amble.ait.core.blocks.decoration.WoodenSeatBlock;
import dev.amble.ait.module.planet.core.PlanetItems;

@Mixin(ItemRenderer.class)
public class ItemRendererMixin {

    @Unique private final RiftScannerModel riftScannerModel = new RiftScannerModel(RiftScannerModel.getTexturedModelData().createModel());
    @Unique private final GeigerCounterModel geigerCounterModel = new GeigerCounterModel(GeigerCounterModel.getTexturedModelData().createModel());
    @Unique private final HandlesModel handlesModel = new HandlesModel(HandlesModel.getTexturedModelData().createModel());
    @Unique private final WoodenSeatModel chairModel = new WoodenSeatModel(WoodenSeatModel.getTexturedModelData().createModel());
    @Unique private final CoralSeatModel coralSeatModel = new CoralSeatModel(CoralSeatModel.getTexturedModelData().createModel());
    @Unique private final CopperSeatModel copperSeatModel = new CopperSeatModel(CopperSeatModel.getTexturedModelData().createModel());
    @Unique private final ToyotaSeatModel toyotaSeatModel = new ToyotaSeatModel(ToyotaSeatModel.getTexturedModelData().createModel());
    @Unique private final BrassStatueModel brassStatueModel = new BrassStatueModel(BrassStatueModel.getTexturedModelData().createModel());
    @Unique private final CopperRingsModel copperRingsModel = new CopperRingsModel(CopperRingsModel.getTexturedModelData().createModel());

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

        if (stack.isOf(AITBlocks.CORAL_SEAT.asItem())) {
            this.ait$handleCoralChairRendering(entity, stack, renderMode, leftHanded, matrices, vertexConsumers, world, light, overlay, seed, ci);
        }

        if (stack.isOf(AITBlocks.TOYOTA_SEAT.asItem())) {
            this.ait$handleToyotaChairRendering(entity, stack, renderMode, leftHanded, matrices, vertexConsumers, world, light, overlay, seed, ci);
        }

        if (stack.isOf(AITBlocks.COPPER_SEAT.asItem())) {
            this.ait$handleCopperChairRendering(entity, stack, renderMode, leftHanded, matrices, vertexConsumers, world, light, overlay, seed, ci);
        }

        if (stack.isOf(AITBlocks.COPPER_RINGS.asItem())) {
            this.ait$handleCopperRingsRendering(entity, stack, renderMode, leftHanded, matrices, vertexConsumers, world, light, overlay, seed, ci);
        }

        if (stack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof WoodenSeatBlock) {
            this.ait$handleChairRendering(entity, stack, renderMode, leftHanded, matrices, vertexConsumers, world, light, overlay, seed, ci);
        }

        if (stack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof BrassStatueBlock) {
            this.ait$handleBrassStatueRendering(entity, stack, renderMode, leftHanded, matrices, vertexConsumers, world, light, overlay, seed, ci);
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
        if (stack.isOf(AITBlocks.CORAL_SEAT.asItem())) {
            this.ait$handleCoralChairRendering(null, stack, renderMode, leftHanded, matrices, vertexConsumers, null, light, overlay, 0, ci);
        }
        if (stack.isOf(AITBlocks.TOYOTA_SEAT.asItem())) {
            this.ait$handleToyotaChairRendering(null, stack, renderMode, leftHanded, matrices, vertexConsumers, null, light, overlay, 0, ci);
        }
        if (stack.isOf(AITBlocks.COPPER_SEAT.asItem())) {
            this.ait$handleCopperChairRendering(null, stack, renderMode, leftHanded, matrices, vertexConsumers, null, light, overlay, 0, ci);
        }
        if (stack.isOf(AITBlocks.COPPER_RINGS.asItem())) {
            this.ait$handleCopperRingsRendering(null, stack, renderMode, leftHanded, matrices, vertexConsumers, null, light, overlay, 0, ci);
        }
        if (stack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof WoodenSeatBlock seatBlock) {
            this.ait$handleChairRendering(null, stack, renderMode, leftHanded, matrices, vertexConsumers, null, light, overlay, 0, ci);
        }
        if (stack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof BrassStatueBlock seatBlock) {
            this.ait$handleBrassStatueRendering(null, stack, renderMode, leftHanded, matrices, vertexConsumers, null, light, overlay, 0, ci);
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

        handlesModel.setAngles(matrices, renderMode, leftHanded);

        ClientWorld clientWorld = world instanceof ClientWorld ? (ClientWorld) world : null;
        handlesModel.render(clientWorld, entity, stack, matrices, vertexConsumers, light, overlay, seed);

        matrices.pop();
        ci.cancel();
    }

    @Unique private void ait$handleChairRendering(
            LivingEntity entity,
            ItemStack stack,
            ModelTransformationMode renderMode,
            boolean leftHanded,
            MatrixStack matrices,
            VertexConsumerProvider vertexConsumers,
            @Nullable World world,
            int light,
            int overlay,
            int seed,
            CallbackInfo ci
    ) {
        if (!(stack.getItem() instanceof BlockItem blockItem) ||
                !(blockItem.getBlock() instanceof WoodenSeatBlock woodenSeatBlock)) {
            return;
        }

        String variant = woodenSeatBlock.getVariant();

        matrices.push();


        matrices.translate(-0.5f, -0.5f, -0.5f);
        matrices.scale(1.0f, -1.0f, -1.0f);

        chairModel.setAngles(matrices, renderMode, leftHanded);

        Identifier texture = new Identifier(
                AITMod.MOD_ID,
                "textures/blockentities/decoration/wooden_seat/" + variant + ".png"
        );

        VertexConsumer buffer = vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(texture));
        chairModel.render(matrices, buffer, light, overlay, 1.0F, 1.0F, 1.0F, 1.0F);

        matrices.pop();
        ci.cancel();
    }

    @Unique private void ait$handleBrassStatueRendering(
            LivingEntity entity,
            ItemStack stack,
            ModelTransformationMode renderMode,
            boolean leftHanded,
            MatrixStack matrices,
            VertexConsumerProvider vertexConsumers,
            @Nullable World world,
            int light,
            int overlay,
            int seed,
            CallbackInfo ci
    ) {
        if (!(stack.getItem() instanceof BlockItem blockItem) ||
                !(blockItem.getBlock() instanceof BrassStatueBlock statueBlock)) {
            return;
        }

        String variant = statueBlock.getVariant();

        matrices.push();


        matrices.translate(-0.5f, -0.5f, -0.5f);
        matrices.scale(1.0f, -1.0f, -1.0f);

        brassStatueModel.setAngles(matrices, renderMode, leftHanded);

        Identifier texture = new Identifier(
                AITMod.MOD_ID,
                "textures/blockentities/decoration/brass_statue/" + variant + ".png"
        );

        VertexConsumer buffer = vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(texture));
        brassStatueModel.render(matrices, buffer, light, overlay, 1.0F, 1.0F, 1.0F, 1.0F);

        matrices.pop();
        ci.cancel();
    }

    @Unique private void ait$handleCoralChairRendering(
            LivingEntity entity,
            ItemStack stack,
            ModelTransformationMode renderMode,
            boolean leftHanded,
            MatrixStack matrices,
            VertexConsumerProvider vertexConsumers,
            @Nullable World world,
            int light,
            int overlay,
            int seed,
            CallbackInfo ci
    ) {

        matrices.push();


        matrices.translate(-0.5f, -0.5f, -0.5f);
        matrices.scale(1.0f, -1.0f, -1.0f);

        coralSeatModel.setAngles(matrices, renderMode, leftHanded);

        Identifier texture = new Identifier(
                AITMod.MOD_ID,
                "textures/blockentities/decoration/coral_seat.png"
        );

        VertexConsumer buffer = vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(texture));
        coralSeatModel.render(matrices, buffer, light, overlay, 1.0F, 1.0F, 1.0F, 1.0F);

        matrices.pop();
        ci.cancel();
    }

    @Unique private void ait$handleCopperRingsRendering(
            LivingEntity entity,
            ItemStack stack,
            ModelTransformationMode renderMode,
            boolean leftHanded,
            MatrixStack matrices,
            VertexConsumerProvider vertexConsumers,
            @Nullable World world,
            int light,
            int overlay,
            int seed,
            CallbackInfo ci
    ) {

        matrices.push();


        matrices.translate(-0.5f, -0.5f, -0.5f);
        matrices.scale(1.0f, -1.0f, -1.0f);

        copperRingsModel.setAngles(matrices, renderMode, leftHanded);

        Identifier texture = new Identifier(
                AITMod.MOD_ID,
                "textures/blockentities/decoration/copper_rings.png"
        );

        VertexConsumer buffer = vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(texture));
        copperRingsModel.render(matrices, buffer, light, overlay, 1.0F, 1.0F, 1.0F, 1.0F);

        matrices.pop();
        ci.cancel();
    }

    @Unique private void ait$handleCopperChairRendering(
            LivingEntity entity,
            ItemStack stack,
            ModelTransformationMode renderMode,
            boolean leftHanded,
            MatrixStack matrices,
            VertexConsumerProvider vertexConsumers,
            @Nullable World world,
            int light,
            int overlay,
            int seed,
            CallbackInfo ci
    ) {

        matrices.push();


        matrices.translate(-0.5f, -0.5f, -0.5f);
        matrices.scale(1.0f, -1.0f, -1.0f);

        copperSeatModel.setAngles(matrices, renderMode, leftHanded);

        Identifier texture = new Identifier(
                AITMod.MOD_ID,
                "textures/blockentities/decoration/copper_seat.png"
        );

        VertexConsumer buffer = vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(texture));
        copperSeatModel.render(matrices, buffer, light, overlay, 1.0F, 1.0F, 1.0F, 1.0F);

        matrices.pop();
        ci.cancel();
    }

    @Unique private void ait$handleToyotaChairRendering(
            LivingEntity entity,
            ItemStack stack,
            ModelTransformationMode renderMode,
            boolean leftHanded,
            MatrixStack matrices,
            VertexConsumerProvider vertexConsumers,
            @Nullable World world,
            int light,
            int overlay,
            int seed,
            CallbackInfo ci
    ) {

        matrices.push();


        matrices.translate(-0.5f, -0.5f, -0.5f);
        matrices.scale(1.0f, -1.0f, -1.0f);

        toyotaSeatModel.setAngles(matrices, renderMode, leftHanded);

        Identifier texture = new Identifier(
                AITMod.MOD_ID,
                "textures/blockentities/decoration/toyota_seat.png"
        );

        VertexConsumer buffer = vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(texture));
        toyotaSeatModel.render(matrices, buffer, light, overlay, 1.0F, 1.0F, 1.0F, 1.0F);

        matrices.pop();
        ci.cancel();
    }


}
