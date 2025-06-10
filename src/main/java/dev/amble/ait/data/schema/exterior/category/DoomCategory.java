package dev.amble.ait.data.schema.exterior.category;

import net.minecraft.util.Identifier;

import dev.amble.ait.AITMod;
import dev.amble.ait.data.schema.exterior.ExteriorCategorySchema;

public class DoomCategory extends ExteriorCategorySchema {
    public static final Identifier REFERENCE = AITMod.id("exterior/doom");

    public DoomCategory() {
        super(REFERENCE, "doom");
    }
}
