package dev.amble.ait.client.renderers.entities;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.util.Identifier;

import dev.amble.ait.client.models.entities.hostile.daleks.DalekModel;
import dev.amble.ait.client.renderers.entities.feature.DalekLightFeatureRenderer;
import dev.amble.ait.core.entities.DalekEntity;

public class DalekEntityRenderer<T extends DalekEntity> extends MobEntityRenderer<T, DalekModel<T>> {

    private Identifier texture;

    public DalekEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, new DalekModel<>(DalekModel.getTexturedModelData().createModel()), 0.5f);
        this.addFeature(new DalekLightFeatureRenderer<>(this));
    }

    public void updateDalek(DalekEntity dalekEntity) {
        Identifier newTexture = dalekEntity.getDalek().texture();
        if (this.texture != newTexture) {
            this.texture = newTexture;
        }
    }

    @Override
    public Identifier getTexture(DalekEntity entity) {
        this.updateDalek(entity);
        return this.texture;
    }
}
