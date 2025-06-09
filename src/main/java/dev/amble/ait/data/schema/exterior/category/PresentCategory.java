package dev.amble.ait.data.schema.exterior.category;

import dev.amble.ait.AITMod;
import dev.amble.ait.data.schema.exterior.ExteriorCategorySchema;
import dev.amble.ait.data.schema.exterior.ExteriorVariantSchema;
import net.minecraft.util.Identifier;

public class PresentCategory extends ExteriorCategorySchema {
    public static final Identifier REFERENCE = AITMod.id("exterior/present");

    public PresentCategory() {
        super(REFERENCE, "present");
    }

    @Override
    public boolean hasPortals() {
        return false;
    }

    @Override
    public ExteriorVariantSchema getDefaultVariant() {
        //return ExteriorVariantRegistry.PRESENT_DEFAULT;
        return null;
    }


}