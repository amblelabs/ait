package dev.amble.ait.data.schema.exterior.variant.doom.client;

import dev.amble.ait.AITMod;
import dev.amble.ait.client.models.exteriors.DoomExteriorModel;
import dev.amble.ait.client.models.exteriors.ExteriorModel;
import dev.amble.ait.client.renderers.exteriors.DoomConstants;
import dev.amble.ait.data.datapack.exterior.BiomeOverrides;
import dev.amble.ait.data.schema.exterior.ClientExteriorVariantSchema;
import net.minecraft.util.Identifier;
import org.joml.Vector3f;

public class ClientDoomVariant extends ClientExteriorVariantSchema {

    public ClientDoomVariant() {
        super(AITMod.id("exterior/doom"));
    }

    @Override
    public ExteriorModel model() {
        return new DoomExteriorModel(DoomExteriorModel.getTexturedModelData().createModel());
    }

    @Override
    public Vector3f sonicItemTranslations() {
        return new Vector3f(0.5f, 1.5f, 0f);
    }

    @Override
    public Identifier texture() {
        return DoomConstants.DOOM_FRONT_BACK;
    }

    @Override
    public Identifier emission() {
        return DoomConstants.DOOM_TEXTURE_EMISSION;
    }

    @Override
    public BiomeOverrides overrides() {
        return null;
    }
}
