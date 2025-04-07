package dev.amble.ait.client.renderers.entities.feature;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.feature.EyesFeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.entity.Entity;

import dev.amble.ait.client.models.entities.mobs.NightmareCybermanModel;
import dev.amble.ait.client.renderers.entities.mobs.NightmareCybermanRenderer;

public class CybermanLightFeatureRenderer<T extends Entity, M extends NightmareCybermanModel<T>>
        extends EyesFeatureRenderer<T, M> {
    private static final RenderLayer EMISSIVE = RenderLayer.getEyes(NightmareCybermanRenderer.getEmissionTexture());

    public CybermanLightFeatureRenderer(FeatureRendererContext<T, M> featureRendererContext) {
        super(featureRendererContext);
    }

    @Override
    public RenderLayer getEyesTexture() {
        return EMISSIVE;
    }
}