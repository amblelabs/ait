package dev.amble.ait.data.schema.exterior.category;

import net.minecraft.util.Identifier;

import dev.amble.ait.AITMod;
import dev.amble.ait.data.schema.exterior.ExteriorCategorySchema;
import dev.amble.ait.data.schema.exterior.ExteriorVariantSchema;
import dev.amble.ait.registry.impl.exterior.ExteriorVariantRegistry;

public class PoliceBoxCategory extends ExteriorCategorySchema {
    public static final Identifier REFERENCE = AITMod.id("exterior/police_box");

    public PoliceBoxCategory() {
        super(REFERENCE, "police_box");
    }

    @Override
    public boolean hasPortals() {
        return true;
    }

    @Override
    public ExteriorVariantSchema getDefaultVariant() {
        return ExteriorVariantRegistry.BOX_DEFAULT;
    }
}
