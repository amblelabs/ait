package dev.amble.ait.data.schema.console.variant.steam;

import dev.amble.ait.AITMod;
import dev.amble.ait.data.Loyalty;
import dev.amble.ait.data.schema.console.ConsoleVariantSchema;
import dev.amble.ait.data.schema.console.type.SteamType;
import net.minecraft.util.Identifier;

public class SteamGildedVariant extends ConsoleVariantSchema {
    public static final Identifier REFERENCE = AITMod.id("console/steam_gilded");

    public SteamGildedVariant() {
        super(SteamType.REFERENCE, REFERENCE, new Loyalty(Loyalty.Type.PILOT));
    }
}
