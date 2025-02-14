package dev.amble.ait.data.schema.console.variant.hartnell;

import net.minecraft.util.Identifier;

import dev.amble.ait.AITMod;
import dev.amble.ait.data.schema.console.ConsoleVariantSchema;
import dev.amble.ait.data.schema.console.type.HartnellType;

public class HartnellVariant extends ConsoleVariantSchema {
    public static final Identifier REFERENCE = AITMod.id("console/hartnell");

    public HartnellVariant() {
        super(HartnellType.REFERENCE, REFERENCE);
    }
}
