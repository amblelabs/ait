package dev.amble.ait.client.renderers.entities;

import dev.amble.ait.api.tardis.TardisComponent;
import dev.amble.ait.client.models.exteriors.ExteriorModel;
import dev.amble.ait.client.models.exteriors.SiegeModeModel;
import dev.amble.ait.client.renderers.AITRenderLayers;
import dev.amble.ait.compat.DependencyChecker;
import dev.amble.ait.core.blocks.ExteriorBlock;
import dev.amble.ait.core.entities.FallingTardisEntity;
import dev.amble.ait.core.tardis.Tardis;
import dev.amble.ait.core.tardis.TardisExterior;
import dev.amble.ait.core.tardis.handler.BiomeHandler;
import dev.amble.ait.data.schema.exterior.ClientExteriorVariantSchema;
import dev.amble.ait.registry.impl.exterior.ClientExteriorVariantRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.RotationPropertyHelper;

public class FallingTardisRenderer extends EntityRenderer<FallingTardisEntity> {

    public FallingTardisRenderer(EntityRendererFactory.Context context) {
        super(context);
    }

    @Override
    public void render(FallingTardisEntity entity, float yaw, float tickDelta, MatrixStack matrices,
            VertexConsumerProvider vertexConsumers, int light) {
        Tardis tardis = entity.tardis().get();

        if (tardis == null)
            return;

        TardisExterior tardisExterior = tardis.getExterior();
        ClientExteriorVariantSchema exteriorVariant = tardisExterior.getVariant().getClient();

        if (exteriorVariant == null)
            return;

        if (MinecraftClient.getInstance().player == null)
            return;

        Identifier texture = exteriorVariant.texture();
        Identifier emission = exteriorVariant.emission();
        ExteriorModel model = exteriorVariant.model();

        if (model == null)
            return;

        matrices.push();
        int k = entity.getBlockState().get(ExteriorBlock.ROTATION);
        float h = RotationPropertyHelper.toDegrees(k);

        matrices.multiply(
                RotationAxis.NEGATIVE_Y.rotationDegrees(!exteriorVariant.equals(ClientExteriorVariantRegistry.DOOM)
                        ? 180f + h
                        : MinecraftClient.getInstance().player.getHeadYaw() + 180f));
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180f));

        boolean siege = tardis.siege().isActive();

        if (siege) {
            model = new SiegeModeModel(SiegeModeModel.getTexturedModelData().createModel());
            texture = tardis.siege().texture().get();
        }

        model.renderEntity(entity, model.getPart(), matrices,
                vertexConsumers.getBuffer(AITRenderLayers.getEntityTranslucentCull(texture)), light,
                OverlayTexture.DEFAULT_UV, 1, 1, 1, 1);

        if (siege) {
            matrices.pop();
            return;
        }

        if (emission != null)
            model.renderEntity(entity, model.getPart(), matrices,
                    vertexConsumers.getBuffer(DependencyChecker.hasIris() ? AITRenderLayers.tardisEmissiveCullZOffset(emission, true) : AITRenderLayers.getTextPolygonOffset(emission)),
                    0xf000f0, OverlayTexture.DEFAULT_UV, 1, 1, 1, 1);

        if (!exteriorVariant.equals(ClientExteriorVariantRegistry.CORAL_GROWTH)) {
            BiomeHandler handler = tardis.handler(TardisComponent.Id.BIOME);
            Identifier biomeTexture = handler.getBiomeKey().get(exteriorVariant.overrides());

            if (biomeTexture != null && !exteriorVariant.texture().equals(biomeTexture)) {
                model.renderEntity(entity, model.getPart(), matrices,
                        vertexConsumers.getBuffer(AITRenderLayers.getEntityTranslucentCull(biomeTexture)), light,
                        OverlayTexture.DEFAULT_UV, 1, 1, 1, 1);
            }
        }

        matrices.pop();
    }

    @Override
    public Identifier getTexture(FallingTardisEntity entity) {
        if (entity.tardis().get() == null)
            return SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE; // random texture just so i dont crash

        return entity.tardis().get().getExterior().getVariant().getClient().texture();
    }
}
