package dev.amble.ait.data.schema.exterior.variant.classic.client;

import net.minecraft.util.Identifier;

import dev.amble.ait.AITMod;
import dev.amble.ait.core.tardis.handler.BiomeHandler;
import dev.amble.ait.data.datapack.exterior.BiomeOverrides;

public class ClientClassicBoxYetiVariant extends ClientClassicBoxVariant {
    protected static final Identifier BIOME_IDENTIFIER = new Identifier(AITMod.MOD_ID, CATEGORY_PATH + "/biome" + "/classic_yeti.png");

    private final BiomeOverrides OVERRIDES = BiomeOverrides.builder(ClientClassicBoxVariant.OVERRIDES)
            .with(type -> type.getTexture(BIOME_IDENTIFIER), BiomeHandler.BiomeType.CHERRY, BiomeHandler.BiomeType.CHORUS,
                    BiomeHandler.BiomeType.SNOWY, BiomeHandler.BiomeType.SCULK)
            .build();

    public ClientClassicBoxYetiVariant() {
        super("yeti");
    }

    @Override
    public BiomeOverrides overrides() {
        return OVERRIDES;
    }
}
