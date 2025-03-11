package dev.amble.ait.registry.v2;

import com.mojang.serialization.Codec;
import dev.amble.ait.AITMod;
import dev.amble.ait.data.schema.exterior.ExteriorCategorySchema;
import dev.amble.ait.data.schema.exterior.ExteriorVariantSchema;
import dev.amble.lib.registry.DatapackAmbleRegistry;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.random.Random;

import java.util.ArrayList;
import java.util.List;

public class ExteriorVariantRegistry extends DatapackAmbleRegistry<ExteriorVariantSchema> {

    //public static final Identifier FALLBACK = AITMod.id("capsule_default");
    public static final Identifier FALLBACK = AITMod.id("tardim");

    public static final Identifier CORAL_GROWTH = AITMod.id("coral_growth");
    public static final Identifier DOOM = AITMod.id("doom");

    public static final Identifier TARDIM = AITMod.id("tardim");

    public ExteriorVariantRegistry() {
        super(AITMod.id("exterior/variant"));
    }

    @Override
    protected Codec<ExteriorVariantSchema> codec() {
        return ExteriorVariantSchema.CODEC;
    }

    public List<ExteriorVariantSchema> withParent(ExteriorCategorySchema parent) {
        List<ExteriorVariantSchema> list = new ArrayList<>();

        for (ExteriorVariantSchema schema : this) {
            if (schema.category().value() == parent)
                list.add(schema);
        }

        return list;
    }

    public ExteriorVariantSchema getRandom(ExteriorCategorySchema parent, Random random) {
        return Util.getRandomOrEmpty(this.withParent(parent), random)
                .orElseGet(() -> this.get(FALLBACK));
    }
}
