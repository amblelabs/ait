package dev.amble.ait.data.schema.exterior.category;

import net.minecraft.util.Identifier;

import dev.amble.ait.AITMod;
import dev.amble.ait.data.schema.exterior.ExteriorCategorySchema;
import dev.amble.ait.data.schema.exterior.ExteriorVariantSchema;
import dev.amble.ait.registry.impl.exterior.ExteriorVariantRegistry;

public class AdaptiveCategory extends ExteriorCategorySchema {
    public static final Identifier REFERENCE = AITMod.id("exterior/adaptive");

    public AdaptiveCategory() {
        super(REFERENCE, "adaptive");
    }

    @Override
    public boolean hasPortals() {
        return true;
    }

    @Override
    public ExteriorVariantSchema getDefaultVariant() {
        return ExteriorVariantRegistry.ADAPTIVE;
    }
}
