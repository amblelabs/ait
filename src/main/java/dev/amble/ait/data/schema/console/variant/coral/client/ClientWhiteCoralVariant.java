package dev.amble.ait.data.schema.console.variant.coral.client;

import org.joml.Vector3f;

import net.minecraft.util.Identifier;

import dev.amble.ait.AITMod;
import dev.amble.ait.client.models.consoles.CoralConsoleModel;
import dev.amble.ait.client.models.consoles.SimpleConsoleModel;
import dev.amble.ait.data.schema.console.ClientConsoleVariantSchema;
import dev.amble.ait.data.schema.console.variant.coral.WhiteCoralVariant;

public class ClientWhiteCoralVariant extends ClientConsoleVariantSchema {
    public static final Identifier TEXTURE = new Identifier(AITMod.MOD_ID,
            ("textures/blockentities/consoles/coral_white.png"));
    public static final Identifier EMISSION = new Identifier(AITMod.MOD_ID,
            ("textures/blockentities/consoles/coral_white_emission.png"));

    public ClientWhiteCoralVariant() {
        super(WhiteCoralVariant.REFERENCE, WhiteCoralVariant.REFERENCE);
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
    public SimpleConsoleModel model() {
        return new CoralConsoleModel(CoralConsoleModel.getTexturedModelData().createModel());
    }

    @Override
    public Vector3f sonicItemTranslations() {
        return new Vector3f(1.15f, 1.2f, 0.5f);
    }

    @Override
    public float[] sonicItemRotations() {
        return new float[]{90f, 135f};
    }
    @Override
    public Vector3f handlesTranslations() {
        return new Vector3f(0.5f, 1.6f, 0.5f);
    }

    @Override
    public float[] handlesRotations() {
        return new float[]{-90f, 135f};
    }
}
