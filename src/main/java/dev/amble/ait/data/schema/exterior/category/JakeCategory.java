package dev.amble.ait.data.schema.exterior.category;

import dev.amble.ait.AITMod;
import dev.amble.ait.data.schema.exterior.ExteriorCategorySchema;
import net.minecraft.util.Identifier;

public class JakeCategory extends ExteriorCategorySchema {
    public static final Identifier REFERENCE = AITMod.id("exterior/jake");

    public JakeCategory() {
        super(REFERENCE, "jake");
    }

    @Override
    public boolean hasPortals() {
        return false;
    }
/*
    @Override
    public ExteriorVariantSchema getDefaultVariant() {
        return ExteriorVariantRegistry.JAKE_DEFAULT;
    }

 */
}