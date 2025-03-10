package dev.amble.ait.data.schema.exterior.variant.bookshelf.client;

import dev.amble.ait.data.datapack.exterior.BiomeOverrides;

public class ClientBookshelfDefaultVariant extends ClientBookshelfVariant {
    public ClientBookshelfDefaultVariant() {
        super("default");
    }

    @Override
    public BiomeOverrides overrides() {
        return null;
    }
}
