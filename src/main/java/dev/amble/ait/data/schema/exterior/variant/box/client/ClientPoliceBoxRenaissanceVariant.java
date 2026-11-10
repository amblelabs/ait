package dev.amble.ait.data.schema.exterior.variant.box.client;

import dev.amble.ait.core.tardis.handler.BiomeHandler;
import dev.amble.ait.data.datapack.exterior.BiomeOverrides;
import org.joml.Vector3f;

public class ClientPoliceBoxRenaissanceVariant extends ClientPoliceBoxVariant {

    private final BiomeOverrides OVERRIDES = BiomeOverrides.builder(ClientPoliceBoxVariant.OVERRIDES)
            .with(type -> type.getTexture(BIOME_IDENTIFIER), BiomeHandler.BiomeType.SNOWY, BiomeHandler.BiomeType.CHORUS,
                    BiomeHandler.BiomeType.SCULK)
            .build();

    public ClientPoliceBoxRenaissanceVariant() {
        super("renaissance");
    }

    @Override
    public Vector3f sonicItemTranslations() {
        return new Vector3f(0.56f, 1.45f, 1.2f);
    }

    @Override
    public BiomeOverrides overrides() {
        return OVERRIDES;
    }
}
