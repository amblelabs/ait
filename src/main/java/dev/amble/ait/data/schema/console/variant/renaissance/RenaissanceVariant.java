package dev.amble.ait.data.schema.console.variant.renaissance;

import dev.amble.ait.AITMod;
import dev.amble.ait.data.Loyalty;
import dev.amble.ait.data.schema.console.ConsoleVariantSchema;
import dev.amble.ait.data.schema.console.type.RenaissanceType;
import net.minecraft.util.Identifier;

public class RenaissanceVariant extends ConsoleVariantSchema {
    public static final Identifier REFERENCE = AITMod.id("console/renaissance");

    public RenaissanceVariant() {
        super(RenaissanceType.REFERENCE, REFERENCE, new Loyalty(Loyalty.Type.PILOT));
    }
}
