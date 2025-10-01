package dev.amble.ait.data.schema.exterior.variant.classic.client;

import net.minecraft.util.Identifier;

import dev.amble.ait.AITMod;
import dev.amble.ait.client.models.exteriors.ClassicHudolinExteriorModel;
import dev.amble.ait.client.models.exteriors.SimpleExteriorModel;

public class ClientClassicBoxShalkaVariant extends ClientClassicBoxVariant {
    protected static final Identifier BIOME_IDENTIFIER = new Identifier(AITMod.MOD_ID, CATEGORY_PATH + "/biome" + "/classic_shalka.png");

    public ClientClassicBoxShalkaVariant() {
        super("shalka");
    }

    @Override
    public SimpleExteriorModel model() {
        return new ClassicHudolinExteriorModel(ClassicHudolinExteriorModel.getTexturedModelData().createModel());
    }
}
