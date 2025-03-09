package dev.amble.ait.data.schema.exterior.variant.box.client;

import dev.amble.ait.client.models.exteriors.ExteriorModel;
import dev.amble.ait.client.models.exteriors.PoliceBoxCoralModel;
import dev.amble.ait.core.tardis.handler.BiomeHandler;
import dev.amble.ait.data.datapack.exterior.BiomeOverrides;

public class ClientPoliceBoxCoralVariant extends ClientPoliceBoxVariant {
    public ClientPoliceBoxCoralVariant() {
        super("coral");
    }

    private final BiomeOverrides OVERRIDES = BiomeOverrides.builder(ClientPoliceBoxVariant.OVERRIDES)
            .with(type -> type.getTexture(BIOME_IDENTIFIER), BiomeHandler.BiomeType.SANDY).build();

    @Override
    public ExteriorModel model() {
        return new PoliceBoxCoralModel(PoliceBoxCoralModel.getTexturedModelData().createModel());
    }

    @Override
    public BiomeOverrides overrides() {
        return OVERRIDES;
    }
}
