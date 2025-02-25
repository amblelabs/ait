package dev.amble.ait.data.schema.console.variant.steam;

import net.minecraft.util.Identifier;

import dev.amble.ait.AITMod;
import dev.amble.ait.data.Loyalty;
import dev.amble.ait.data.schema.console.ConsoleVariantSchema;
import dev.amble.ait.data.schema.console.type.SteamType;

public class SteamSteelVariant extends ConsoleVariantSchema {
    public static final Identifier REFERENCE = AITMod.id("console/steam_steel");

    public SteamSteelVariant() {
        super(SteamType.REFERENCE, REFERENCE, new Loyalty(Loyalty.Type.COMPANION));
    }
}
