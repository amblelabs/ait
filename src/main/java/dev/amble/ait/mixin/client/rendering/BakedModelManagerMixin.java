package dev.amble.ait.mixin.client.rendering;

import dev.amble.ait.client.renderers.BakedModelEditor;
import net.fabricmc.fabric.api.client.model.loading.v1.FabricBakedModelManager;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;

@Mixin(BakedModelManager.class)
public abstract class BakedModelManagerMixin implements BakedModelEditor {

    @Shadow
    private Map<Identifier, BakedModel> models;

    @Override
    @Shadow
    public abstract BakedModel getModel(ModelIdentifier id);

    @Override
    public BakedModel ait$getModel(Identifier identifier) {
        return ((FabricBakedModelManager) this).getModel(identifier);
    }

    @Override
    public void ait$setModel(Identifier identifier, BakedModel model) {
        this.models.put(identifier, model);
    }

    @Override
    public void ait$setModel(ModelIdentifier identifier, BakedModel model) {
        this.models.put(identifier, model);
    }
}
