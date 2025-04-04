package dev.amble.ait.client.renderers.entities.feature;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.feature.EyesFeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.entity.Entity;

import dev.amble.ait.client.models.entities.hostile.daleks.DalekModel;
import dev.amble.ait.client.renderers.entities.DalekEntityRenderer;

public class DalekLightFeatureRenderer<T extends Entity, M extends DalekModel<T>>
        extends EyesFeatureRenderer<T, M> {
    private static final RenderLayer EMISSIVE = RenderLayer.getEyes(DalekEntityRenderer.EMISSION);

    public DalekLightFeatureRenderer(FeatureRendererContext<T, M> featureRendererContext) {
        super(featureRendererContext);
    }

    @Override
    public RenderLayer getEyesTexture() {
        return EMISSIVE;
    }
}
