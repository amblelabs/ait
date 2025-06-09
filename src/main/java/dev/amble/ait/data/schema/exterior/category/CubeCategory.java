package dev.amble.ait.data.schema.exterior.category;

import dev.amble.ait.AITMod;
import dev.amble.ait.data.schema.exterior.ExteriorCategorySchema;
import net.minecraft.util.Identifier;

public class CubeCategory extends ExteriorCategorySchema {
    public static final Identifier REFERENCE = AITMod.id("exterior/cube");

    public CubeCategory() {
        super(REFERENCE, "cube");
    }
}
