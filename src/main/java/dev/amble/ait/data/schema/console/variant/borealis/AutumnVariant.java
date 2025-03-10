package dev.amble.ait.data.schema.console.variant.borealis;

import net.minecraft.util.Identifier;

import dev.amble.ait.AITMod;
import dev.amble.ait.data.Loyalty;
import dev.amble.ait.data.schema.console.ConsoleVariantSchema;
import dev.amble.ait.data.schema.console.type.BorealisType;

public class AutumnVariant extends ConsoleVariantSchema {
    public static final Identifier REFERENCE = AITMod.id("console/autumn");

    public AutumnVariant() {
        super(BorealisType.REFERENCE, REFERENCE, new Loyalty(Loyalty.Type.COMPANION));
    }
}
