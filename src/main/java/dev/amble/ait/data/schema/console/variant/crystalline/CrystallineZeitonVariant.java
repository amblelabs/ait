package dev.amble.ait.data.schema.console.variant.crystalline;

import dev.amble.ait.AITMod;
import dev.amble.ait.data.Loyalty;
import dev.amble.ait.data.schema.console.ConsoleVariantSchema;
import dev.amble.ait.data.schema.console.type.CrystallineType;
import net.minecraft.util.Identifier;

public class CrystallineZeitonVariant extends ConsoleVariantSchema {
    public static final Identifier REFERENCE = AITMod.id("console/crystalline_zeiton");

    public CrystallineZeitonVariant() {
        super(CrystallineType.REFERENCE, REFERENCE, new Loyalty(Loyalty.Type.OWNER));
    }
}
