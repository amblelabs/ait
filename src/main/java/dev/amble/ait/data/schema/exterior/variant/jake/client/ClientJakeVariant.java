package dev.amble.ait.data.schema.exterior.variant.jake.client;

import org.joml.Vector3f;

import net.minecraft.util.Identifier;

import dev.amble.ait.AITMod;
import dev.amble.ait.client.models.exteriors.SimpleExteriorModel;
import dev.amble.ait.client.models.exteriors.JakeTheDogExteriorModel;
import dev.amble.ait.data.datapack.exterior.BiomeOverrides;
import dev.amble.ait.data.schema.exterior.ClientExteriorVariantSchema;

public abstract class ClientJakeVariant extends ClientExteriorVariantSchema {
    private final String name;
    protected static final String CATEGORY_PATH = "textures/blockentities/exteriors/jake";
    protected static final Identifier CATEGORY_IDENTIFIER = new Identifier(AITMod.MOD_ID,
            CATEGORY_PATH + "/jake.png");
    protected static final String TEXTURE_PATH = CATEGORY_PATH + "/jake_";

    protected static final BiomeOverrides OVERRIDES = BiomeOverrides.of(type -> type.getTexture(CATEGORY_IDENTIFIER));

    protected ClientJakeVariant(String name) {
        super(AITMod.id("exterior/jake/" + name));

        this.name = name;
    }

    @Override
    public SimpleExteriorModel model() {
        return new JakeTheDogExteriorModel(JakeTheDogExteriorModel.getTexturedModelData().createModel());
    }

    @Override
    public Identifier texture() {
        return AITMod.id(TEXTURE_PATH + name + ".png");
    }

    @Override
    public Identifier emission() {
        return AITMod.id(TEXTURE_PATH + name + "_emission" + ".png");
    }

    @Override
    public Vector3f sonicItemTranslations() {
        return new Vector3f(0.50f, 2.04f, 1.9f);
    }

    @Override
    public BiomeOverrides overrides() {
        return OVERRIDES;
    }
}
