package dev.amble.ait.client.renderers.wearables;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.client.render.entity.model.ModelWithArms;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import dev.amble.ait.AITMod;
import dev.amble.ait.client.models.wearables.RespiratorModel;
import dev.amble.ait.core.AITItems;

@Environment(value = EnvType.CLIENT)
public class RespiratorFeatureRenderer<T extends LivingEntity, M extends EntityModel<T> & ModelWithArms>
        extends
            FeatureRenderer<T, M> {

    private static final Identifier RESPIRATOR = new Identifier(AITMod.MOD_ID,
            "textures/entity/wearables/respirator.png");
    private static final Identifier FACELESS_RESPIRATOR = new Identifier(AITMod.MOD_ID,
            "textures/entity/wearables/faceless_respirator.png");
    private final RespiratorModel model;

    public RespiratorFeatureRenderer(FeatureRendererContext<T, M> context, EntityModelLoader loader) {
        super(context);
        this.model = new RespiratorModel(RespiratorModel.getTexturedModelData().createModel());
    }

    @Override
    public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, T livingEntity,
            float f, float g, float h, float j, float k, float l) {
        ItemStack stack = livingEntity.getEquippedStack(EquipmentSlot.HEAD);

        if (!(stack.isOf(AITItems.RESPIRATOR) || stack.isOf(AITItems.FACELESS_RESPIRATOR)))
            return;

        if (livingEntity instanceof AbstractClientPlayerEntity || livingEntity instanceof ArmorStandEntity) {

            matrixStack.push();

            this.model.mask.copyTransform(((BipedEntityModel)this.getContextModel()).head);
            this.model.setAngles(livingEntity, f, g, j, k, l);

            VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(RenderLayer.getEntitySmoothCutout(
                    stack.getItem() == AITItems.RESPIRATOR ? RESPIRATOR : FACELESS_RESPIRATOR));
            this.model.render(matrixStack, vertexConsumer, i, OverlayTexture.DEFAULT_UV, 1, 1, 1, 1f);

            matrixStack.pop();
        }
    }
}
