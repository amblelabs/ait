package dev.amble.ait.client.renderers.entities;


import dev.amble.ait.core.entities.BOTIPaintingEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public abstract class BOTIPaintingEntityRenderer
        extends EntityRenderer<BOTIPaintingEntity> {
    public BOTIPaintingEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
    }

    @Override
    public Identifier getTexture(BOTIPaintingEntity entity) {
        return null;
    }

}
