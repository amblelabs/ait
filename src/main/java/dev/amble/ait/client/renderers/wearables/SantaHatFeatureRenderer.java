package dev.amble.ait.client.renderers.wearables;

import dev.amble.ait.AITMod;
import dev.amble.ait.client.models.wearables.SantaHatModel;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

@Environment(value = EnvType.CLIENT)
public class SantaHatFeatureRenderer<T extends LivingEntity, M extends PlayerEntityModel<T>>
        extends
            FeatureRenderer<T, M> {

    private static final Identifier SANTA_HAT = new Identifier(AITMod.MOD_ID,
            "textures/entity/wearables/santa_hat.png");
    private final SantaHatModel model;

    public SantaHatFeatureRenderer(FeatureRendererContext<T, M> context, EntityModelLoader loader) {
        super(context);
        this.model = new SantaHatModel(SantaHatModel.getTexturedModelData().createModel());
    }

    @Override
    public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, T livingEntity,
            float f, float g, float h, float j, float k, float l) {
        ItemStack stack = livingEntity.getEquippedStack(EquipmentSlot.HEAD);

        /*if (!(stack.isOf(AITItems.SANTA_HAT)))
            return;*/

        if (!(livingEntity instanceof AbstractClientPlayerEntity))
            return;

        matrixStack.push();



        this.model.hat.copyTransform(this.getContextModel().head);
        this.model.setAngles(livingEntity, f, g, j, k, l);

        VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(RenderLayer.getEntitySmoothCutout(SANTA_HAT));
        this.model.render(matrixStack, vertexConsumer, i, OverlayTexture.DEFAULT_UV, 1, 1, 1, 1f);

        matrixStack.pop();
    }
}
