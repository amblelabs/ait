package dev.amble.ait.data.schema.exterior.variant.classic.client;

import dev.amble.ait.client.models.exteriors.ClassicHudolinExteriorModel;
import dev.amble.ait.client.models.exteriors.ExteriorModel;

public class ClientClassicBoxShalkaVariant extends ClientClassicBoxVariant {

    public ClientClassicBoxShalkaVariant() {
        super("shalka");
    }

    @Override
    public ExteriorModel model() {
        return new ClassicHudolinExteriorModel(ClassicHudolinExteriorModel.getTexturedModelData().createModel());
    }
}
