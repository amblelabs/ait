package dev.amble.ait.data.schema.exterior.variant.adaptive.client;

import org.joml.Vector3f;

import net.minecraft.util.Identifier;

import dev.amble.ait.AITMod;
import dev.amble.ait.client.models.exteriors.CapsuleExteriorModel;
import dev.amble.ait.client.models.exteriors.SimpleExteriorModel;
import dev.amble.ait.data.datapack.exterior.BiomeOverrides;
import dev.amble.ait.data.schema.exterior.ClientExteriorVariantSchema;

public class ClientAdaptiveVariant extends ClientExteriorVariantSchema {

    protected static final String CATEGORY_PATH = "textures/blockentities/exteriors/adaptive";

    protected static final BiomeOverrides OVERRIDES = BiomeOverrides.EMPTY;

    public ClientAdaptiveVariant() {
        super(AITMod.id("exterior/adaptive"));
    }

    @Override
    public SimpleExteriorModel model() {
        return new CapsuleExteriorModel(CapsuleExteriorModel.getTexturedModelData().createModel());
    }

    @Override
    public Identifier texture() {
        return AITMod.id(CATEGORY_PATH + ".png");
    }

    @Override
    public Identifier emission() {
        return null;
    }

    @Override
    public Vector3f sonicItemTranslations() {
        return new Vector3f(0.5f, 1.2f, 1.15f);
    }

    @Override
    public BiomeOverrides overrides() {
        return OVERRIDES;
    }
}
