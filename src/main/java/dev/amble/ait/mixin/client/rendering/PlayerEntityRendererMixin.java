package dev.amble.ait.mixin.client.rendering;

import dev.amble.ait.client.renderers.wearables.RespiratorFeatureRenderer;
import dev.amble.ait.core.entities.FlightTardisEntity;
import dev.amble.ait.core.item.PsychpaperItem;
import dev.amble.ait.module.planet.client.models.wearables.SpacesuitModel;
import dev.amble.ait.module.planet.client.renderers.wearables.SpacesuitFeatureRenderer;
import dev.amble.ait.module.planet.core.item.SpacesuitItem;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Arm;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(PlayerEntityRenderer.class)
public abstract class PlayerEntityRendererMixin
        extends
            LivingEntityRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> {

    @Shadow
    protected abstract void setModelPose(AbstractClientPlayerEntity player);

    public PlayerEntityRendererMixin(EntityRendererFactory.Context ctx,
            PlayerEntityModel<AbstractClientPlayerEntity> model, float shadowRadius) {
        super(ctx, model, shadowRadius);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void ait$PlayerEntityRenderer(EntityRendererFactory.Context ctx, boolean slim, CallbackInfo ci) {
        PlayerEntityRenderer renderer = (PlayerEntityRenderer) (Object) this;

        this.addFeature(new RespiratorFeatureRenderer<>(renderer, ctx.getModelLoader()));
        this.addFeature(new SpacesuitFeatureRenderer<>(renderer, ctx.getModelLoader()));
        //this.addFeature(new SantaHatFeatureRenderer<>(renderer, ctx.getModelLoader()));
    }

    @Inject(method = "renderArm", at = @At("HEAD"), cancellable = true)
    private void ait$renderArm(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, AbstractClientPlayerEntity player, ModelPart arm, ModelPart sleeve, CallbackInfo ci) {
        if (!(player.getEquippedStack(EquipmentSlot.CHEST).getItem() instanceof SpacesuitItem)) return;
        ci.cancel();

        PlayerEntityModel<AbstractClientPlayerEntity> playerEntityModel = this.getModel();
        this.setModelPose(player);
        playerEntityModel.handSwingProgress = 0.0f;
        playerEntityModel.sneaking = false;
        playerEntityModel.leaningPitch = 0.0f;
        playerEntityModel.setAngles(player, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f);
        arm.pitch = 0.0f;

        SpacesuitModel spacesuitModel = new SpacesuitModel(SpacesuitModel.getTexturedModelData().createModel());

        boolean rightHanded = player.getMainArm() == Arm.RIGHT;

        if (rightHanded) {
            spacesuitModel.RightArm.copyTransform(arm);
            spacesuitModel.RightArm.render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(SpacesuitFeatureRenderer.BLANK_SPACESUIT)), light, OverlayTexture.DEFAULT_UV);
        } else {
            spacesuitModel.LeftArm.copyTransform(arm);
            spacesuitModel.LeftArm.render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(SpacesuitFeatureRenderer.BLANK_SPACESUIT)), light, OverlayTexture.DEFAULT_UV);
        }
    }

    @Inject(method = "render*", at = @At("HEAD"), cancellable = true)
    public void ait$render(AbstractClientPlayerEntity abstractClientPlayerEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci) {
        if (abstractClientPlayerEntity.getVehicle() instanceof FlightTardisEntity) {
            ci.cancel();
        }
    }

    @Inject(method = "renderLabelIfPresent(Lnet/minecraft/client/network/AbstractClientPlayerEntity;Lnet/minecraft/text/Text;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At("HEAD"), cancellable = true)
    public void ait$psychicPaperNaming(AbstractClientPlayerEntity abstractClientPlayerEntity, Text text, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci) {
        List<ItemStack> papers = getItemInInventory(abstractClientPlayerEntity);

        if (!papers.isEmpty() && papers.get(0).hasCustomName()) {
            super.renderLabelIfPresent(abstractClientPlayerEntity, papers.get(0).getName(), matrixStack, vertexConsumerProvider, i);
            ci.cancel();
        }
    }

    @Unique private static List<ItemStack> getItemInInventory(PlayerEntity player) {
        List<ItemStack> items = new ArrayList<>();

        for (ItemStack stack : player.getInventory().main) {
            if (stack != null && stack.getItem() instanceof PsychpaperItem)
                items.add(stack);
        }

        return items;
    }
}
