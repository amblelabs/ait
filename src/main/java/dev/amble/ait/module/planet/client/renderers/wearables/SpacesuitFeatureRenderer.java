package dev.amble.ait.module.planet.client.renderers.wearables;

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
import dev.amble.ait.module.planet.client.models.wearables.SpacesuitModel;
import dev.amble.ait.module.planet.core.item.SpacesuitItem;

@Environment(value = EnvType.CLIENT)
public class SpacesuitFeatureRenderer<T extends LivingEntity, M extends EntityModel<T> & ModelWithArms>
        extends
            FeatureRenderer<T, M> {

    public static final Identifier BLANK_SPACESUIT = AITMod.id(
            "textures/entity/wearables/spacesuit/nasa/blank_spacesuit.png");
    private final SpacesuitModel model;

    public SpacesuitFeatureRenderer(FeatureRendererContext<T, M> context, EntityModelLoader loader) {
        super(context);
        this.model = new SpacesuitModel(SpacesuitModel.getTexturedModelData().createModel());
    }

    @Override
    public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, T livingEntity,
                       float f, float g, float h, float j, float k, float l) {

        if (!(livingEntity instanceof AbstractClientPlayerEntity || livingEntity instanceof ArmorStandEntity))
            return;

        matrixStack.push();
        matrixStack.translate(0, -1.5f, 0);

        // god bless america
        for (BodyParts part : BodyParts.values()) {
            ItemStack stack = getModelForSlot(livingEntity, part);
            if (stack.getItem() instanceof SpacesuitItem) {
                enablePart(model, part);
            } else {
                disablePart(model, part);
            }
        }

        this.model.Head.copyTransform(((BipedEntityModel) getContextModel()).head);
        this.model.Body.copyTransform(((BipedEntityModel) getContextModel()).body);
        this.model.LeftArm.copyTransform(((BipedEntityModel) getContextModel()).leftArm);
        this.model.RightArm.copyTransform(((BipedEntityModel) getContextModel()).rightArm);
        this.model.LeftLeg.copyTransform(((BipedEntityModel) getContextModel()).leftLeg);
        this.model.RightLeg.copyTransform(((BipedEntityModel) getContextModel()).rightLeg);
        this.model.setAngles(livingEntity, f, g, j, k, l);

        VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(RenderLayer.getEntityCutoutNoCullZOffset(BLANK_SPACESUIT));
        this.model.render(matrixStack, vertexConsumer, i, OverlayTexture.DEFAULT_UV, 1, 1, 1, 1f);

        matrixStack.pop();
    }

    public static void enablePart(SpacesuitModel model, BodyParts part) {
        switch (part) {
            case HEAD:
                model.Head.visible = true;
                break;
            case CHEST:
                model.Body.visible = true;
                model.LeftArm.visible = true;
                model.RightArm.visible = true;
                break;
            case LEGS:
                model.LeftLeg.getChild("left_leg_pant").visible = true;
                model.RightLeg.getChild("right_leg_pant").visible = true;

                break;
            case FEET:
                model.LeftFoot.visible = true;
                model.RightFoot.visible = true;
                break;
        }
    }

    public static void disablePart(SpacesuitModel model, BodyParts part) {
        switch (part) {
            case HEAD:
                model.Head.visible = false;
                break;
            case CHEST:
                model.Body.visible = false;
                model.LeftArm.visible = false;
                model.RightArm.visible = false;
                break;
            case LEGS:
                model.LeftLeg.getChild("left_leg_pant").visible = false;
                model.RightLeg.getChild("right_leg_pant").visible = false;
                break;
            case FEET:
                model.LeftFoot.visible = false;
                model.RightFoot.visible = false;
                break;
        }
    }

    public static ItemStack getModelForSlot(LivingEntity entity, BodyParts parts) {
        return switch(parts) {
            default -> entity.getEquippedStack(EquipmentSlot.HEAD);
            case CHEST -> entity.getEquippedStack(EquipmentSlot.CHEST);
            case LEGS -> entity.getEquippedStack(EquipmentSlot.LEGS);
            case FEET -> entity.getEquippedStack(EquipmentSlot.FEET);
        };
    }


    public enum BodyParts {
        HEAD,
        CHEST,
        LEGS,
        FEET
    }
}
