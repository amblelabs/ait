package dev.amble.ait.data.schema.console.variant.borealis;

import dev.amble.ait.AITMod;
import dev.amble.ait.data.Loyalty;
import dev.amble.ait.data.schema.console.ConsoleVariantSchema;
import dev.amble.ait.data.schema.console.type.BorealisType;
import net.minecraft.util.Identifier;

public class BorealisVariant extends ConsoleVariantSchema {
    public static final Identifier REFERENCE = AITMod.id("console/borealis");

    public BorealisVariant() {
        super(BorealisType.REFERENCE, REFERENCE, new Loyalty(Loyalty.Type.COMPANION));
    }
}
