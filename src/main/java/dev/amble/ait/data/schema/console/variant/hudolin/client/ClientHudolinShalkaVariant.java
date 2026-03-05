
package dev.amble.ait.data.schema.console.variant.hudolin.client;


import dev.amble.ait.AITMod;
import dev.amble.ait.client.models.consoles.HudolinConsoleModel;
import dev.amble.ait.client.models.consoles.SimpleConsoleModel;
import dev.amble.ait.data.schema.console.ClientConsoleVariantSchema;
import dev.amble.ait.data.schema.console.variant.hudolin.HudolinShalkaVariant;
import dev.amble.ait.data.schema.console.variant.hudolin.HudolinShortVariant;
import net.minecraft.util.Identifier;
import org.joml.Vector3f;

public class ClientHudolinShalkaVariant extends ClientConsoleVariantSchema {
    public static final Identifier TEXTURE = new Identifier(AITMod.MOD_ID,
            ("textures/blockentities/consoles/hudolin_console_shalka.png"));
    public static final Identifier EMISSION = new Identifier(AITMod.MOD_ID,
            ("textures/blockentities/consoles/hudolin_console_shalka_emission.png"));

    public ClientHudolinShalkaVariant() {
        super(HudolinShalkaVariant.REFERENCE, HudolinShalkaVariant.REFERENCE);
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
        return new HudolinConsoleModel(HudolinConsoleModel.getTexturedModelData().createModel());
    }
    @Override
    public Vector3f sonicItemTranslations() {
        return new Vector3f(-0.055f, 1.03f, -0.09f);
    }

    @Override
    public float[] sonicItemRotations() {
        return new float[]{120f, 170f};
    }
    @Override
    public Vector3f handlesTranslations() {
        return new Vector3f(-0.305f, 0.45f, -0.125f);
    }

    @Override
    public float[] handlesRotations() {
        return new float[]{29f, 46.25f};
    }
}
