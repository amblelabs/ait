package dev.amble.ait.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;

import net.fabricmc.fabric.api.client.model.loading.v1.ModelModifier;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.util.Identifier;

import dev.amble.ait.AITMod;
import dev.amble.ait.client.models.RoundelModel;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class AITModelPlugin implements ModelLoadingPlugin, ModelModifier.AfterBake {
    public static final Identifier ROUNDEL_MODEL = AITMod.id("roundel");
    public static final Identifier ROUNDEL_MODEL_NORTH = new ModelIdentifier(ROUNDEL_MODEL, "facing=north");
    public static final Identifier ROUNDEL_MODEL_EAST = new ModelIdentifier(ROUNDEL_MODEL, "facing=east");
    public static final Identifier ROUNDEL_MODEL_SOUTH = new ModelIdentifier(ROUNDEL_MODEL, "facing=south");
    public static final Identifier ROUNDEL_MODEL_WEST = new ModelIdentifier(ROUNDEL_MODEL, "facing=west");
    public static final Identifier ROUNDEL_MODEL_INVENTORY = new ModelIdentifier(ROUNDEL_MODEL, "inventory");

    @Override
    public void onInitializeModelLoader(ModelLoadingPlugin.Context pluginContext) {
        pluginContext.modifyModelAfterBake().register(this);
    }

    @Override
    public @Nullable BakedModel modifyModelAfterBake(@Nullable BakedModel model, ModelModifier.AfterBake.Context context) {
        Identifier id = context.id();
        if (id != null && (
                id.toString().contains(ROUNDEL_MODEL_NORTH.toString()) ||
                        id.toString().contains(ROUNDEL_MODEL_EAST.toString()) ||
                        id.toString().contains(ROUNDEL_MODEL_SOUTH.toString()) ||
                        id.toString().contains(ROUNDEL_MODEL_WEST.toString()) ||
                        id.toString().contains(ROUNDEL_MODEL_INVENTORY.toString()))) {
            System.out.println(id);
            return new RoundelModel();
        }
        return model;
    }
}