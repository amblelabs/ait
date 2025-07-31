package dev.amble.ait.client.renderers.decoration;

import dev.amble.ait.client.models.exteriors.SimpleExteriorModel;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;

import dev.amble.ait.AITMod;
import dev.amble.ait.client.models.decoration.SnowGlobeModel;
import dev.amble.ait.client.renderers.AITRenderLayers;
import dev.amble.ait.core.blockentities.SnowGlobeBlockEntity;
import dev.amble.ait.core.blocks.SnowGlobeBlock;
import dev.amble.ait.core.tardis.handler.BiomeHandler;
import dev.amble.ait.data.schema.exterior.ClientExteriorVariantSchema;
import dev.amble.ait.registry.impl.exterior.ClientExteriorVariantRegistry;

public class SnowGlobeRenderer<T extends SnowGlobeBlockEntity> implements BlockEntityRenderer<T> {
    public static final Identifier SNOW_GLOBE_TEXTURE = new Identifier(AITMod.MOD_ID,
            "textures/blockentities/decoration/advent/snow_globe.png");

    public static final SnowGlobeModel model = new SnowGlobeModel(SnowGlobeModel.getTexturedModelData().createModel());

    public SnowGlobeRenderer(BlockEntityRendererFactory.Context ctx) {}

    @Override
    public void render(SnowGlobeBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        matrices.push();
        matrices.translate(0.5, 1.5f, 0.5);
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180));
        float k = entity.getCachedState().get(SnowGlobeBlock.FACING).asRotation();
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(k));
        model.render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntityCutoutNoCull(SNOW_GLOBE_TEXTURE)), light, overlay, 1, 1, 1, 1);
        matrices.push();
        matrices.translate(0.17, 1.17, -0.2);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(225f));
        matrices.scale(0.055f, 0.055f, 0.055f);
        ClientExteriorVariantSchema schema = ClientExteriorVariantRegistry.BOX_DEFAULT;
        SimpleExteriorModel model = (SimpleExteriorModel) schema.model(); // Please ensure that whatever the variant you change it to is a SimpleExteriorModel

        model.render(matrices, vertexConsumers.getBuffer(AITRenderLayers.getEntityTranslucentCull(schema.texture())), light, overlay, 1, 1, 1, 1);
        model.render(matrices, vertexConsumers.getBuffer(AITRenderLayers.tardisEmissiveCullZOffset(schema.emission(), true)), 0xf000f0, overlay, 1, 1, 1, 1);
        model.render(matrices, vertexConsumers.getBuffer(AITRenderLayers.getEntityCutoutNoCullZOffset(schema.overrides().get(BiomeHandler.BiomeType.SNOWY), false)), light, overlay, 1, 1, 1, 1);

        matrices.pop();
        matrices.pop();
    }
}
