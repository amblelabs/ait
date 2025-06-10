package dev.amble.ait.data.schema.console.variant.crystalline.client;

import org.joml.Vector3f;

import net.minecraft.util.Identifier;

import dev.amble.ait.AITMod;
import dev.amble.ait.client.models.consoles.ConsoleModel;
import dev.amble.ait.client.models.consoles.CrystallineConsoleModel;
import dev.amble.ait.data.schema.console.ClientConsoleVariantSchema;
import dev.amble.ait.data.schema.console.variant.crystalline.CrystallineVariant;

public class ClientCrystallineVariant extends ClientConsoleVariantSchema {
    public static final Identifier TEXTURE = new Identifier(AITMod.MOD_ID,
            ("textures/blockentities/consoles/crystalline.png"));
    public static final Identifier EMISSION = new Identifier(AITMod.MOD_ID,
            ("textures/blockentities/consoles/crystalline_emission.png"));

    public ClientCrystallineVariant() {
        super(CrystallineVariant.REFERENCE, CrystallineVariant.REFERENCE);
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
        return new CrystallineConsoleModel(CrystallineConsoleModel.getTexturedModelData().createModel());
    }

    @Override
    public Vector3f sonicItemTranslations() {
        return new Vector3f(1.6f, 1.0f, 0.5f);
    }

    @Override
    public float[] sonicItemRotations() {
        return new float[]{-90f, -22.5f};
    }

    @Override
    public Vector3f handlesTranslations() {
        return new Vector3f(0.75f, 1.25f, 0.5f);
    }

    @Override
    public float[] handlesRotations() {
        return new float[]{-90f, 120f};
    }
}
