package dev.amble.ait.data.schema.console.variant.hartnell;

import dev.amble.ait.AITMod;
import dev.amble.ait.data.Loyalty;
import dev.amble.ait.data.schema.console.ConsoleVariantSchema;
import dev.amble.ait.data.schema.console.type.HartnellType;
import net.minecraft.util.Identifier;

public class WoodenHartnellVariant extends ConsoleVariantSchema {
    public static final Identifier REFERENCE = AITMod.id("console/hartnell_wooden");

    public WoodenHartnellVariant() {
        super(HartnellType.REFERENCE, REFERENCE, new Loyalty(Loyalty.Type.NEUTRAL));
    }
}
