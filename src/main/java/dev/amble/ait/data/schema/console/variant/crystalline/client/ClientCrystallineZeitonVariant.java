package dev.amble.ait.data.schema.console.variant.crystalline.client;


import dev.amble.ait.AITMod;
import dev.amble.ait.client.models.consoles.ConsoleModel;
import dev.amble.ait.client.models.consoles.CrystallineConsoleModel;
import dev.amble.ait.data.schema.console.ClientConsoleVariantSchema;
import dev.amble.ait.data.schema.console.variant.crystalline.CrystallineZeitonVariant;
import net.minecraft.util.Identifier;
import org.joml.Vector3f;

public class ClientCrystallineZeitonVariant extends ClientConsoleVariantSchema {
    public static final Identifier TEXTURE = new Identifier(AITMod.MOD_ID,
            ("textures/blockentities/consoles/crystalline_zeiton.png"));
    public static final Identifier EMISSION = new Identifier(AITMod.MOD_ID,
            ("textures/blockentities/consoles/crystalline_zeiton_emission.png"));

    public ClientCrystallineZeitonVariant() {
        super(CrystallineZeitonVariant.REFERENCE, CrystallineZeitonVariant.REFERENCE);
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
        return new Vector3f(1.55f, 1.05f, 1.105f);
    }

    @Override
    public float[] sonicItemRotations() {
        return new float[]{-60f, -12.5f};
    }
    @Override
    public Vector3f handlesTranslations() {
        return new Vector3f(0.8f, 1.25f, 0.68f);
    }

    @Override
    public float[] handlesRotations() {
        return new float[]{-60f, 120f};
    }
}
