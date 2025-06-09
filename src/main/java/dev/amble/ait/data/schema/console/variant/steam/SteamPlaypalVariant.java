package dev.amble.ait.data.schema.console.variant.steam;

import dev.amble.ait.AITMod;
import dev.amble.ait.data.Loyalty;
import dev.amble.ait.data.schema.console.ConsoleVariantSchema;
import dev.amble.ait.data.schema.console.type.SteamType;
import net.minecraft.util.Identifier;

public class SteamPlaypalVariant extends ConsoleVariantSchema {
    public static final Identifier REFERENCE = AITMod.id("console/steam_playpal");

    public SteamPlaypalVariant() {
        super(SteamType.REFERENCE, REFERENCE, new Loyalty(Loyalty.Type.OWNER));
    }
}
