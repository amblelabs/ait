package dev.amble.ait.data.schema.console.variant.steam.client;

import org.joml.Vector3f;

import net.minecraft.util.Identifier;

import dev.amble.ait.AITMod;
import dev.amble.ait.client.models.consoles.SimpleConsoleModel;
import dev.amble.ait.client.models.consoles.SteamConsoleModel;
import dev.amble.ait.data.schema.console.ClientConsoleVariantSchema;
import dev.amble.ait.data.schema.console.variant.steam.SteamGildedVariant;

public class ClientSteamGildedVariant extends ClientConsoleVariantSchema {
    public static final Identifier TEXTURE = new Identifier(AITMod.MOD_ID,
            ("textures/blockentities/consoles/steam_console_gilded.png"));
    public static final Identifier EMISSION = new Identifier(AITMod.MOD_ID,
            ("textures/blockentities/consoles/steam_console_gilded_emission.png"));

    public ClientSteamGildedVariant() {
        super(SteamGildedVariant.REFERENCE, SteamGildedVariant.REFERENCE);
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
        return new SteamConsoleModel(SteamConsoleModel.getTexturedModelData().createModel());
    }

    @Override
    public Vector3f sonicItemTranslations() {
        return new Vector3f(0.9f, 1.125f, -0.19f);
    }

    @Override
    public float[] sonicItemRotations() {
        return new float[]{30f, 120f};
    }
    @Override
    public Vector3f handlesTranslations() {
        return new Vector3f(0.5f, 1.25f, 0.5f);
    }

    @Override
    public float[] handlesRotations() {
        return new float[]{210f, 120f};
    }
}
