package dev.amble.ait.data.schema.console.variant.toyota.client;

import org.joml.Vector3f;

import net.minecraft.util.Identifier;

import dev.amble.ait.AITMod;
import dev.amble.ait.client.models.consoles.SimpleConsoleModel;
import dev.amble.ait.client.models.consoles.ToyotaConsoleModel;
import dev.amble.ait.data.schema.console.ClientConsoleVariantSchema;
import dev.amble.ait.data.schema.console.variant.toyota.ToyotaLegacyVariant;

public class ClientToyotaLegacyVariant extends ClientConsoleVariantSchema {
    public static final Identifier TEXTURE = new Identifier(AITMod.MOD_ID,
            ("textures/blockentities/consoles/toyota_legacy_default.png"));
    public static final Identifier EMISSION = new Identifier(AITMod.MOD_ID,
            ("textures/blockentities/consoles/toyota_legacy_emission.png"));

    public ClientToyotaLegacyVariant() {
        super(ToyotaLegacyVariant.REFERENCE, ToyotaLegacyVariant.REFERENCE);
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
        return new ToyotaConsoleModel(ToyotaConsoleModel.getTexturedModelData().createModel());
    }

    @Override
    public Vector3f sonicItemTranslations() {
        return new Vector3f(-0.5275f, 1.35f, 0.7f);
    }

    @Override
    public float[] sonicItemRotations() {
        return new float[]{-120f, -45f};
    }

    @Override
    public Vector3f handlesTranslations() {
        return new Vector3f(0.05f, 1.75f, 0.36f);
    }

    @Override
    public float[] handlesRotations() {
        return new float[]{60f, 135f};
    }
}
