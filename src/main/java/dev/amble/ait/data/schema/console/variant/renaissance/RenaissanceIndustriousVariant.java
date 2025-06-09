package dev.amble.ait.data.schema.console.variant.renaissance;

import dev.amble.ait.AITMod;
import dev.amble.ait.data.Loyalty;
import dev.amble.ait.data.schema.console.ConsoleVariantSchema;
import dev.amble.ait.data.schema.console.type.RenaissanceType;
import net.minecraft.util.Identifier;

public class RenaissanceIndustriousVariant extends ConsoleVariantSchema {
    public static final Identifier REFERENCE = AITMod.id("console/renaissance_industrious");

    public RenaissanceIndustriousVariant() {
        super(RenaissanceType.REFERENCE, REFERENCE, new Loyalty(Loyalty.Type.OWNER));
    }
}
