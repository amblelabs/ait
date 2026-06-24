package dev.amble.ait.data.schema.exterior.variant.classic.client;

import dev.amble.ait.data.datapack.exterior.BiomeOverrides;

public class ClientClassicBoxExileVariant extends ClientClassicBoxVariant {
    public ClientClassicBoxExileVariant() {
        super("exile");
    }

    @Override
    public BiomeOverrides overrides() {
        return BiomeOverrides.EMPTY; // Requires an artist to make biome textures.
    }
}
