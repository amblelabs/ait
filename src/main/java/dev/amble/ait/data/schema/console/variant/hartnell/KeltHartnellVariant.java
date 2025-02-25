package dev.amble.ait.data.schema.console.variant.hartnell;

import net.minecraft.util.Identifier;

import dev.amble.ait.AITMod;
import dev.amble.ait.data.Loyalty;
import dev.amble.ait.data.schema.console.ConsoleVariantSchema;
import dev.amble.ait.data.schema.console.type.HartnellType;

public class KeltHartnellVariant extends ConsoleVariantSchema {
    public static final Identifier REFERENCE = AITMod.id("console/hartnell_kelt");

    public KeltHartnellVariant() {
        super(HartnellType.REFERENCE, REFERENCE, new Loyalty(Loyalty.Type.COMPANION));
    }
}
