package dev.amble.ait.data.schema.console.variant.renaissance.client;

import org.joml.Vector3f;

import net.minecraft.util.Identifier;

import dev.amble.ait.AITMod;
import dev.amble.ait.client.models.consoles.ConsoleModel;
import dev.amble.ait.client.models.consoles.RenaissanceConsoleModel;
import dev.amble.ait.data.schema.console.ClientConsoleVariantSchema;
import dev.amble.ait.data.schema.console.variant.renaissance.RenaissanceFireVariant;

public class ClientRenaissanceFireVariant extends ClientConsoleVariantSchema {
    public static final Identifier TEXTURE = new Identifier(AITMod.MOD_ID,
            ("textures/blockentities/consoles/renaissance_fire.png"));
    public static final Identifier EMISSION = new Identifier(AITMod.MOD_ID,
            ("textures/blockentities/consoles/renaissance_fire_emission.png"));

    public ClientRenaissanceFireVariant() {
        super(RenaissanceFireVariant.REFERENCE, RenaissanceFireVariant.REFERENCE);
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
        return new RenaissanceConsoleModel(RenaissanceConsoleModel.getTexturedModelData().createModel());
    }

    @Override
    public Vector3f sonicItemTranslations() {
        return new Vector3f(-0.013f, 1.2f, -0.895f);
    }

    @Override
    public float[] sonicItemRotations() {
        return new float[]{-180f, -30f};
    }

    @Override
    public Vector3f handlesTranslations() {
        return new Vector3f(-0.01f, 1.45f, -0.04f);
    }

    @Override
    public float[] handlesRotations() {
        return new float[]{-180f, 120f};
    }
}
