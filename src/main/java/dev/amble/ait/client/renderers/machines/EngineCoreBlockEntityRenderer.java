package dev.amble.ait.client.renderers.machines;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;

import dev.amble.ait.AITMod;
import dev.amble.ait.client.models.decoration.TardisStarModel;
import dev.amble.ait.client.models.machines.EngineCoreModel;
import dev.amble.ait.client.renderers.AITRenderLayers;
import dev.amble.ait.core.blockentities.EngineCoreBlockEntity;
import dev.amble.ait.core.tardis.Tardis;

@Environment(EnvType.CLIENT)
public class EngineCoreBlockEntityRenderer implements BlockEntityRenderer<EngineCoreBlockEntity> {

    public static final Identifier TARDIS_STAR_TEXTURE = new Identifier(AITMod.MOD_ID,
            "textures/environment/tardis_star.png");

    private final EngineCoreModel model;

    public EngineCoreBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
        this.model = new EngineCoreModel(EngineCoreModel.getTexturedModelData().createModel());
    }

    @Override
    public void render(EngineCoreBlockEntity entity, float tickDelta, MatrixStack matrices,
            VertexConsumerProvider vertexConsumers, int light, int overlay) {

        if (!entity.isLinked())
            return;

        Tardis tardis = entity.tardis().get();
        boolean power = tardis.fuel().hasPower();

        matrices.push();
        matrices.translate(0.5f, 0.35f, 0.5f);
        matrices.scale(0.25f, 0.25f, 0.25f);

        if (power && entity.isActive()) {
            matrices.multiply(RotationAxis.NEGATIVE_Y
                    .rotationDegrees(((float) MinecraftClient.getInstance().player.age / 200L) * 360.0f));
        }

        if (entity.isActive()) {
            TardisStarModel.getTexturedModelData().createModel().render(matrices,
                    vertexConsumers.getBuffer(AITRenderLayers.tardisEmissiveCullZOffset(TARDIS_STAR_TEXTURE, true)),
                    LightmapTextureManager.MAX_LIGHT_COORDINATE, OverlayTexture.DEFAULT_UV, 0f, 0f, 0f, 0.5f);
        }

        matrices.scale(0.9f, 0.9f, 0.9f);
        TardisStarModel.getTexturedModelData().createModel().render(matrices,
                vertexConsumers.getBuffer(AITRenderLayers.tardisEmissiveCullZOffset(TARDIS_STAR_TEXTURE, true)),
                LightmapTextureManager.MAX_LIGHT_COORDINATE, OverlayTexture.DEFAULT_UV, 0.13f, 0f, 0.4f, 1f);

        matrices.pop();
    }
}
