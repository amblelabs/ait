package dev.amble.ait.data.schema.console.variant.hudolin.client;

import org.joml.Vector3f;

import net.minecraft.util.Identifier;

import dev.amble.ait.AITMod;
import dev.amble.ait.client.models.consoles.ConsoleModel;
import dev.amble.ait.client.models.consoles.HudolinConsoleModel;
import dev.amble.ait.data.schema.console.ClientConsoleVariantSchema;
import dev.amble.ait.data.schema.console.variant.hudolin.HudolinShalkaVariant;

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
    public ConsoleModel model() {
        return new HudolinConsoleModel(HudolinConsoleModel.getTexturedModelData().createModel());
    }
    @Override
    public Vector3f sonicItemTranslations() {
        return new Vector3f(-0.495f, 1.05f, 0.225f);
    }

    @Override
    public float[] sonicItemRotations() {
        return new float[]{29f, 46.25f};
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
