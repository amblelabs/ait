package dev.amble.ait.data.schema.console.variant.hartnell.client;

import net.minecraft.util.Identifier;

import dev.amble.ait.AITMod;
import dev.amble.ait.client.models.consoles.ConsoleModel;
import dev.amble.ait.client.models.consoles.HartnellConsoleModel;
import dev.amble.ait.data.schema.console.ClientConsoleVariantSchema;
import dev.amble.ait.data.schema.console.variant.hartnell.KeltHartnellVariant;

public class ClientKeltHartnellVariant extends ClientConsoleVariantSchema {
    public static final Identifier TEXTURE = new Identifier(AITMod.MOD_ID,
            ("textures/blockentities/consoles/hartnell_kelt_console.png"));
    public static final Identifier EMISSION = new Identifier(AITMod.MOD_ID,
            ("textures/blockentities/consoles/hartnell_console_emission.png"));

    public ClientKeltHartnellVariant() {
        super(KeltHartnellVariant.REFERENCE, KeltHartnellVariant.REFERENCE);
    }

    @Override
    public Identifier texture() {
        return TEXTURE;
    }

    @Override
    public Identifier emission() {
        return EMISSION;
    }

    @Override
    public ConsoleModel model() {
        return new HartnellConsoleModel(HartnellConsoleModel.getTexturedModelData().createModel());
    }
}
