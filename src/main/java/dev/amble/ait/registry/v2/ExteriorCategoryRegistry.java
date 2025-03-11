package dev.amble.ait.registry.v2;

import com.mojang.serialization.Codec;
import dev.amble.ait.AITMod;
import dev.amble.ait.data.schema.exterior.ExteriorCategorySchema;
import dev.amble.lib.registry.DatapackAmbleRegistry;
import net.minecraft.registry.entry.RegistryElementCodec;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

public class ExteriorCategoryRegistry extends DatapackAmbleRegistry<ExteriorCategorySchema> {

    //public static final Identifier FALLBACK = AITMod.id("capsule");
    public static final Identifier FALLBACK = AITMod.id("tardim");

    // TODO use "police_box" tag for these
    public static final Identifier POLICE_BOX = AITMod.id("police_box");
    public static final Identifier CLASSIC = AITMod.id("classic");

    // TODO use "special" tag for these
    public static final Identifier CORAL_GROWTH = AITMod.id("coral_growth");
    public static final Identifier DOOM = AITMod.id("doom");

    // TODO remove these fields
    public static final Identifier ADAPTIVE = AITMod.id("adaptive");
    public static final Identifier BOOKSHELF = AITMod.id("bookshelf");
    public static final Identifier BOOTH = AITMod.id("booth");
    public static final Identifier CAPSULE = AITMod.id("capsule");
    public static final Identifier PRESENT = AITMod.id("present");
    public static final Identifier RENEGADE = AITMod.id("renegade");
    public static final Identifier STALLION = AITMod.id("stallion");
    public static final Identifier TARDIM = AITMod.id("tardim");
    public static final Identifier DALEK_MOD = AITMod.id("dalek_mod");
    public static final Identifier EASTER_HEAD = AITMod.id("easter_head");
    public static final Identifier PLINTH = AITMod.id("plinth");
    public static final Identifier PIPE = AITMod.id("pipe");
    public static final Identifier JAKE = AITMod.id("jake");
    public static final Identifier GEOMETRIC = AITMod.id("geometric");

    private final Codec<RegistryEntry<ExteriorCategorySchema>> entryCodec = RegistryElementCodec.of(this.getKey(), ExteriorCategorySchema.CODEC);

    public ExteriorCategoryRegistry() {
        super(AITMod.id("exterior/category"));
    }

    @Override
    protected Codec<ExteriorCategorySchema> codec() {
        return ExteriorCategorySchema.CODEC;
    }

    public Codec<RegistryEntry<ExteriorCategorySchema>> entry() {
        return entryCodec;
    }
}
