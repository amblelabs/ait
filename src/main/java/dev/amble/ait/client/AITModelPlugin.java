package dev.amble.ait.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelModifier;

import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.util.Identifier;

import dev.amble.ait.AITMod;
import dev.amble.ait.client.models.RoundelModel;

@Environment(EnvType.CLIENT)
public class AITModelPlugin implements ModelLoadingPlugin, ModelModifier.OnLoad {
    public static final Identifier ROUNDEL_MODEL = AITMod.id("roundel");

    @Override
    public void onInitializeModelLoader(ModelLoadingPlugin.Context pluginContext) {
        pluginContext.modifyModelOnLoad().register(this);
    }

    @Override
    public UnbakedModel modifyModelOnLoad(UnbakedModel model, ModelModifier.OnLoad.Context context) {
        Identifier id = context.id();
        // This is EXCEPTIONALLY stupid. But so is Mojang. So, in conclusion, fuck you. - Loqor
        if (id != null && (
                id.toString().contains("ait:roundel#") && !id.toString().contains("ait:roundel_fabricator"))) {
            return new RoundelModel();
        }
        return model;
    }
}