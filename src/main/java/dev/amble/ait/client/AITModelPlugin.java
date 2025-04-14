package dev.amble.ait.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;

import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.util.Identifier;

import dev.amble.ait.AITMod;
import dev.amble.ait.client.models.RoundelModel;

@Environment(EnvType.CLIENT)
public class AITModelPlugin implements ModelLoadingPlugin {
    public static final Identifier ROUNDEL_MODEL = AITMod.id("roundel");
    public static final ModelIdentifier ROUNDEL_MODEL_NORTH = new ModelIdentifier(ROUNDEL_MODEL, "facing=north");
    public static final ModelIdentifier ROUNDEL_MODEL_EAST = new ModelIdentifier(ROUNDEL_MODEL, "facing=east");
    public static final ModelIdentifier ROUNDEL_MODEL_SOUTH = new ModelIdentifier(ROUNDEL_MODEL, "facing=south");
    public static final ModelIdentifier ROUNDEL_MODEL_WEST = new ModelIdentifier(ROUNDEL_MODEL, "facing=west");
    public static final ModelIdentifier ROUNDEL_MODEL_INVENTORY = new ModelIdentifier(ROUNDEL_MODEL, "inventory");

    @Override
    public void onInitializeModelLoader(Context pluginContext) {
        pluginContext.modifyModelOnLoad().register((original, context) -> {
            var id = context.id();
            /*System.out.println("Loading model: " + ROUNDEL_MODEL + ", should match: " + id);*/
            if(id == ROUNDEL_MODEL_NORTH || id == ROUNDEL_MODEL_EAST || id == ROUNDEL_MODEL_SOUTH ||
                    id == ROUNDEL_MODEL_WEST || id == ROUNDEL_MODEL_INVENTORY) {
                return new RoundelModel();
            } else {
                return original;
            }
        });
    }
}