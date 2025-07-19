package dev.amble.ait.data.schema.console.variant.toyota;

import net.minecraft.util.Identifier;

import dev.amble.ait.AITMod;
import dev.amble.ait.data.Loyalty;
import dev.amble.ait.data.schema.console.ConsoleVariantSchema;
import dev.amble.ait.data.schema.console.type.ToyotaType;

public class ToyotaLegacyVariant extends ConsoleVariantSchema {
    public static final Identifier REFERENCE = AITMod.id("console/toyota_legacy");

    public ToyotaLegacyVariant() {
        super(ToyotaType.REFERENCE, REFERENCE, new Loyalty(Loyalty.Type.OWNER));
    }
}
