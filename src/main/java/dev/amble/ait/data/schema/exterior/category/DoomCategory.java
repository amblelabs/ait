package dev.amble.ait.data.schema.exterior.category;

import dev.amble.ait.AITMod;
import dev.amble.ait.data.schema.exterior.ExteriorCategorySchema;
import net.minecraft.util.Identifier;

public class DoomCategory extends ExteriorCategorySchema {
    public static final Identifier REFERENCE = AITMod.id("exterior/doom");

    public DoomCategory() {
        super(REFERENCE, "doom");
    }
}
