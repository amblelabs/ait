package dev.amble.ait.mixin.client.sonic;

import com.mojang.blaze3d.systems.RenderSystem;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.render.item.ItemModels;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import dev.amble.ait.core.AITItems;
import dev.amble.ait.core.item.SonicItem;
import dev.amble.ait.core.item.sonic.SonicMode;
import dev.amble.ait.data.schema.sonic.SonicSchema;


@Mixin(ItemRenderer.class)
public class SonicItemRendererMixin {

    @Shadow @Final private ItemModels models;

    @Inject(method = "getModel", at = @At("HEAD"), cancellable = true)
    public void getModel(ItemStack stack, World world, LivingEntity entity, int seed, CallbackInfoReturnable<BakedModel> cir) {
        if (!stack.isOf(AITItems.SONIC_SCREWDRIVER))
            return;

        SonicSchema.Models models = SonicItem.schema(stack).models();
        BakedModel model;

        if (entity == null || !(entity.getActiveItem() == stack && entity.isUsingItem())) {
            model = this.getOrMissing(models.inactive());
        } else {
            model = this.getOrMissing(SonicItem.mode(stack).model(models));
        }

        model.getOverrides().apply(model, stack, (ClientWorld) world, entity, seed);
        cir.setReturnValue(model);
    }

    @Unique private BakedModel getOrMissing(Identifier id) {
        BakedModel model = this.models.getModelManager().getModel(
                id
        );

        if (model == null)
            return this.models.getModelManager().getMissingModel();

        return model;
    }

    @Inject(method = "renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformationMode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IILnet/minecraft/client/render/model/BakedModel;)V", at = @At(value = "TAIL"))
    private void ait$renderEmission(ItemStack stack, ModelTransformationMode renderMode, boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, BakedModel model, CallbackInfo ci) {
        if (stack.isOf(AITItems.SONIC_SCREWDRIVER)) {

            SonicMode mode = SonicItem.mode(stack);

            if (mode == null) return;

            boolean inactive = mode == SonicMode.Modes.INACTIVE;

            if (inactive) return;

            SonicSchema schema = SonicItem.schema(stack);

            if (schema == null) return;

            Identifier sonicId = schema.id();

            String modeName = mode.name();

            Identifier texture = Identifier.of(sonicId.getNamespace(), "textures/item/sonic_tools/" + sonicId.getPath() + "_" + modeName + "_emission.png");

            if (texture == null) return;

            VertexConsumer textured = ItemRenderer.getDirectItemGlintConsumer(vertexConsumers,
                    RenderLayer.getEyes(texture), true, false);

            RenderSystem.enablePolygonOffset();
            RenderSystem.polygonOffset(1, 1);
            matrices.push();
            matrices.translate(0, 1, 0);
            MinecraftClient.getInstance().getItemRenderer().renderBakedItemModel(model, AITItems.HAMMER.getDefaultStack(), 0xf000f0, overlay, matrices, lightVertexConsumer(textured));
            matrices.pop();
            RenderSystem.disablePolygonOffset();
        }
    }

    @Unique private VertexConsumer lightVertexConsumer(VertexConsumer vertexConsumer) {
        return new VertexConsumer() {
            @Override
            public VertexConsumer vertex(double x, double y, double z) {
                return vertexConsumer.vertex(x, y, z);
            }

            @Override
            public VertexConsumer color(int red, int green, int blue, int alpha) {
                return vertexConsumer.color(red, green, blue, alpha);
            }

            @Override
            public VertexConsumer texture(float u, float v) {
                return vertexConsumer.texture(u, v);
            }

            @Override
            public VertexConsumer overlay(int u, int v) {
                return vertexConsumer.overlay(u, v);
            }

            @Override
            public VertexConsumer light(int u, int v) {
                return vertexConsumer.light(0xf000f0, 0xf000f0);
            }

            @Override
            public VertexConsumer normal(float x, float y, float z) {
                return vertexConsumer.normal(x, y, z);
            }

            @Override
            public void next() {
                vertexConsumer.next();
            }

            @Override
            public void fixedColor(int red, int green, int blue, int alpha) {
                vertexConsumer.fixedColor(red, green, blue, alpha);
            }

            @Override
            public void unfixColor() {
                vertexConsumer.unfixColor();
            }
        };
    }
}
