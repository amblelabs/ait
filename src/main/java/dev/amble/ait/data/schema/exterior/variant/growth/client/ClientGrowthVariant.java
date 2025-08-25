package dev.amble.ait.data.schema.exterior.variant.growth.client;

import org.joml.Vector3f;

import net.minecraft.util.Identifier;

import dev.amble.ait.AITMod;
import dev.amble.ait.client.models.coral.CoralGrowthExteriorModel;
import dev.amble.ait.client.models.exteriors.SimpleExteriorModel;
import dev.amble.ait.client.renderers.coral.CoralRenderer;
import dev.amble.ait.data.datapack.exterior.BiomeOverrides;
import dev.amble.ait.data.schema.exterior.ClientExteriorVariantSchema;

public class ClientGrowthVariant extends ClientExteriorVariantSchema {

    public ClientGrowthVariant() {
        super(AITMod.id("exterior/coral_growth"));
    }

    @Override
    public SimpleExteriorModel model() {
        return new CoralGrowthExteriorModel(CoralGrowthExteriorModel.getTexturedModelData().createModel());
    }

    @Override
    public Identifier texture() {
        return CoralRenderer.CORAL_GROWTH_TEXTURE;
    }

    @Override
    public Identifier emission() {
        return null;
    }

    @Override
    public Vector3f sonicItemTranslations() {
        return new Vector3f(0, 0, 0);
    }

    @Override
    public BiomeOverrides overrides() {
        return null;
    }
}
