package dev.amble.ait.data.schema.exterior.category;

import dev.amble.ait.AITMod;
import dev.amble.ait.data.schema.exterior.ExteriorCategorySchema;
import dev.amble.ait.data.schema.exterior.ExteriorVariantSchema;
import dev.amble.ait.registry.impl.exterior.ExteriorVariantRegistry;
import net.minecraft.util.Identifier;

public class EasterHeadCategory extends ExteriorCategorySchema {

    public static final Identifier REFERENCE = AITMod.id("exterior/easter_head");

    public EasterHeadCategory() {
        super(REFERENCE, "easter_head");
    }

    @Override
    public ExteriorVariantSchema getDefaultVariant() {
        return ExteriorVariantRegistry.HEAD_DEFAULT;
    }
}
