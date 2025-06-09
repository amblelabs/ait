package dev.amble.ait.data.schema.console.variant.alnico.client;

import dev.amble.ait.AITMod;
import dev.amble.ait.client.models.consoles.AlnicoConsoleModel;
import dev.amble.ait.client.models.consoles.ConsoleModel;
import dev.amble.ait.data.schema.console.ClientConsoleVariantSchema;
import dev.amble.ait.data.schema.console.variant.alnico.BlueAlnicoVariant;
import net.minecraft.util.Identifier;
import org.joml.Vector3f;

public class ClientBlueAlnicoVariant extends ClientConsoleVariantSchema {
    public static final Identifier TEXTURE = new Identifier(AITMod.MOD_ID,
            ("textures/blockentities/consoles/alnico_blue.png"));
    public static final Identifier EMISSION = new Identifier(AITMod.MOD_ID,
            ("textures/blockentities/consoles/alnico_blue_emission.png"));

    public ClientBlueAlnicoVariant() {
        super(BlueAlnicoVariant.REFERENCE, BlueAlnicoVariant.REFERENCE);
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
        return new AlnicoConsoleModel(AlnicoConsoleModel.getTexturedModelData().createModel());
    }

    @Override
    public Vector3f sonicItemTranslations() {
        return new Vector3f(-0.55f, 1.1f, -0.1f);
    }

    @Override
    public Vector3f handlesTranslations() {
        return new Vector3f(0.05f, 1.4f, 0.23f);
    }
}
