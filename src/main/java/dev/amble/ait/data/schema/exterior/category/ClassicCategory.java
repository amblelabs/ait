package dev.amble.ait.data.schema.exterior.category;

import dev.amble.ait.AITMod;
import dev.amble.ait.data.schema.exterior.ExteriorCategorySchema;
import dev.amble.ait.data.schema.exterior.ExteriorVariantSchema;
import dev.amble.ait.registry.impl.exterior.ExteriorVariantRegistry;
import net.minecraft.util.Identifier;

public class ClassicCategory extends ExteriorCategorySchema {
    public static final Identifier REFERENCE = AITMod.id("exterior/classic");

    public ClassicCategory() {
        super(REFERENCE, "classic");
    }

    @Override
    public boolean hasPortals() {
        return true;
    }

    @Override
    public ExteriorVariantSchema getDefaultVariant() {
        return ExteriorVariantRegistry.YETI;
    }
}
