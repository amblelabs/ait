package dev.amble.ait.data.schema.console.variant.coral;

import dev.amble.ait.AITMod;
import dev.amble.ait.data.Loyalty;
import dev.amble.ait.data.schema.console.ConsoleVariantSchema;
import dev.amble.ait.data.schema.console.type.CoralType;
import net.minecraft.util.Identifier;

public class CoralDecayedVariant extends ConsoleVariantSchema {
    public static final Identifier REFERENCE = AITMod.id("console/coral_decayed");

    public CoralDecayedVariant() {
        super(CoralType.REFERENCE, REFERENCE, new Loyalty(Loyalty.Type.COMPANION));
    }
}
