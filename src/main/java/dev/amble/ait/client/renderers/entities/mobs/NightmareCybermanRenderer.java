package dev.amble.ait.client.renderers.entities.mobs;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

import dev.amble.ait.AITMod;
import dev.amble.ait.client.models.entities.mobs.NightmareCybermanModel;
import dev.amble.ait.client.renderers.AITModelLayers;
import dev.amble.ait.client.renderers.entities.feature.CybermanLightFeatureRenderer;
import dev.amble.ait.core.entities.CybermanEntity;

public class NightmareCybermanRenderer extends MobEntityRenderer<CybermanEntity, NightmareCybermanModel<CybermanEntity>> {
    private static final Identifier TEXTURE =  AITMod.id("textures/entity/mobs/cyberman/nightmare_cyberman.png");
    private static final Identifier EMISSION_TEXTURE =  AITMod.id("textures/entity/mobs/cyberman/nightmare_cyberman_emission.png");

    public NightmareCybermanRenderer(EntityRendererFactory.Context context) {
        super(context, new NightmareCybermanModel<>(context.getPart(AITModelLayers.NIGHTMARE_CYBERMAN)), 0.6f);
        this.addFeature(new CybermanLightFeatureRenderer<>(this));
    }

    @Override
    public Identifier getTexture(CybermanEntity entity) {
        return TEXTURE;
    }

    public static Identifier getEmissionTexture() {
        return EMISSION_TEXTURE;
    }

    @Override
    public void render(CybermanEntity mobEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        super.render(mobEntity, f, g, matrixStack, vertexConsumerProvider, i);
    }
}
