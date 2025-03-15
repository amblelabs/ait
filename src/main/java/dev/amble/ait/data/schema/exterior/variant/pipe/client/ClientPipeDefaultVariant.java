package dev.amble.ait.data.schema.exterior.variant.pipe.client;

import dev.amble.ait.data.datapack.exterior.BiomeOverrides;

public class ClientPipeDefaultVariant extends ClientPipeVariant {
    public ClientPipeDefaultVariant() {
        super("default");
    }

    @Override
    public BiomeOverrides overrides() {
        return null;
    }
}
