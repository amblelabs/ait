package dev.amble.ait.data.schema.exterior.variant.exclusive.persona.client;

import dev.amble.ait.AITMod;
import dev.amble.ait.client.models.exteriors.ExteriorModel;
import dev.amble.ait.data.datapack.exterior.BiomeOverrides;
import dev.amble.ait.data.schema.exterior.ClientExteriorVariantSchema;
import net.minecraft.util.Identifier;
import org.joml.Vector3f;

public class ClientVelvetDoorVariant extends ClientExteriorVariantSchema {

    protected static final String CATEGORY_PATH = "textures/blockentities/exteriors/exclusive/persona";
    protected static final String TEXTURE_PATH = CATEGORY_PATH + "/four.png";
    protected static final String EMISSIVE_TEXTURE_PATH = CATEGORY_PATH + "/four_emissive.png";

    public ClientVelvetDoorVariant() {
        super(AITMod.id("exterior/exclusive/persona"));
    }

    @Override
    public ExteriorModel model() {
        return new PersonaExteriorModel();
    }

    @Override
    public Identifier texture() {
        return AITMod.id(TEXTURE_PATH);
    }

    @Override
    public Identifier emission() {
        return null;
    }

    @Override
    public Vector3f sonicItemTranslations() {
        return new Vector3f(0.845f, 1.125f, 1.05f);
    }

    @Override
    public BiomeOverrides overrides() {
        return null;
    }
}
