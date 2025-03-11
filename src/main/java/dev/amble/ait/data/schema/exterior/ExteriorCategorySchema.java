package dev.amble.ait.data.schema.exterior;

import java.lang.reflect.Type;

import com.google.gson.*;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.amble.ait.registry.v2.AITRegistries;
import dev.amble.ait.registry.v2.ExteriorCategoryRegistry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;

import dev.amble.ait.data.schema.BasicSchema;

/**
 * @author duzo
 */
public class ExteriorCategorySchema extends BasicSchema {

    public static final Codec<ExteriorCategorySchema> CODEC = RecordCodecBuilder.create(instance -> instance
            .group(Identifier.CODEC.fieldOf("id").forGetter(ExteriorCategorySchema::id))
            .apply(instance, ExteriorCategorySchema::new));

    private final Identifier id;

    protected ExteriorCategorySchema(Identifier id) {
        super("exterior");
        this.id = id;
    }

    @Override
    public Text text() {
        if (this.text == null)
            this.text = Text.translatable(this.id().toTranslationKey("exterior"));

        return text;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;

        return o instanceof ExteriorCategorySchema other && id.equals(other.id);
    }

    @Override
    public Identifier id() {
        return this.id;
    }

    /**
     * The default exterior for this category
     */
    public ExteriorVariantSchema getDefaultVariant() {
        return AITRegistries.EXTERIOR_VARIANT.withParent(this).get(0);
    }

    @Override
    public String toString() {
        return this.name();
    }

    public static Object serializer() {
        return new Serializer();
    }

    private static class Serializer
            implements
                JsonSerializer<ExteriorCategorySchema>,
                JsonDeserializer<ExteriorCategorySchema> {

        @Override
        public ExteriorCategorySchema deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            Identifier id;

            try {
                id = new Identifier(json.getAsJsonPrimitive().getAsString());
            } catch (InvalidIdentifierException e) {
                id = ExteriorCategoryRegistry.FALLBACK;
            }

            return AITRegistries.EXTERIOR_CATEGORY.tryGetOr(id, ExteriorCategoryRegistry.FALLBACK);
        }

        @Override
        public JsonElement serialize(ExteriorCategorySchema src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.id().toString());
        }
    }
}
