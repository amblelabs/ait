package dev.amble.ait.data.schema.console;

import java.lang.reflect.Type;
import java.util.Optional;

import com.google.gson.*;
import dev.amble.lib.register.unlockable.Unlockable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.joml.Vector3f;

import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;

import dev.amble.ait.AITMod;
import dev.amble.ait.data.Loyalty;
import dev.amble.ait.data.schema.BasicSchema;
import dev.amble.ait.registry.impl.console.ConsoleRegistry;
import dev.amble.ait.registry.impl.console.variant.ClientConsoleVariantRegistry;
import dev.amble.ait.registry.impl.console.variant.ConsoleVariantRegistry;

/**
 * This class is for variants of a {@link ConsoleTypeSchema} and can be changed
 * in game to the players desires <br>
 * <br>
 * It's information should be final and set once on its creation during
 * registration <br>
 * <br>
 * It should be registered in {@link ConsoleVariantRegistry#REGISTRY} otherwise
 * it wont show up in-game <br>
 * <br>
 * It should only be gotten from
 * {@link ConsoleVariantRegistry#REGISTRY#get(Identifier)} using its {@link #id}
 * and only created once <br>
 * <br>
 *
 * @author duzo
 * @see ConsoleVariantRegistry#REGISTRY
 */
public abstract class ConsoleVariantSchema extends BasicSchema implements Unlockable {

    public static final float[] DEFAULT_SONIC_ROTATION = new float[]{120f, 135f};
    public static final Vector3f DEFAULT_SONIC_POS = new Vector3f(0.1f, 1.2f, 0.26f);

    public static final float[] DEFAULT_HANDLES_ROTATION = new float[]{120f, 135f};
    public static final Vector3f DEFAULT_HANDLES_POS = new Vector3f(0.65f, 1.6f, 0.6f);

    private final Identifier parent;
    private final Identifier id;
    private final Loyalty loyalty;

    @Environment(EnvType.CLIENT)
    private ClientConsoleVariantSchema cachedSchema;

    protected ConsoleVariantSchema(Identifier parent, Identifier id, Optional<Loyalty> loyalty) {
        super("console");

        this.parent = parent;
        this.id = id;
        this.loyalty = loyalty.orElse(null);
    }

    protected ConsoleVariantSchema(Identifier parent, Identifier id, Loyalty loyalty) {
        this(parent, id, Optional.of(loyalty));
    }

    protected ConsoleVariantSchema(Identifier parent, Identifier id) {
        this(parent, id, Optional.empty());
    }

    @Override
    public Identifier id() {
        return id;
    }

    @Override
    public Optional<Loyalty> requirement() {
        return Optional.ofNullable(loyalty);
    }

    @Override
    public UnlockType unlockType() {
        return UnlockType.CONSOLE;
    }

    public Identifier parentId() {
        return this.parent;
    }

    /**
     * @implNote If called too early (after datapacks, but before registries), this will return null!
     * @return The parent {@link ConsoleTypeSchema} of this variant
     */
    public ConsoleTypeSchema parent() {
        return ConsoleRegistry.getInstance().get(this.parentId());
    }

    @Environment(EnvType.CLIENT)
    public ClientConsoleVariantSchema getClient() {
        if (this.cachedSchema == null)
            this.cachedSchema = ClientConsoleVariantRegistry.withParent(this);

        return cachedSchema;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;

        return o instanceof ConsoleVariantSchema other && id.equals(other.id);
    }

    public static Object serializer() {
        return new Serializer();
    }

    private static class Serializer
            implements
                JsonSerializer<ConsoleVariantSchema>,
                JsonDeserializer<ConsoleVariantSchema> {

        @Override
        public ConsoleVariantSchema deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            Identifier id;

            try {
                id = new Identifier(json.getAsJsonPrimitive().getAsString());
            } catch (InvalidIdentifierException e) {
                id = AITMod.id("console/borealis");
            }

            return ConsoleVariantRegistry.getInstance().get(id);
        }

        @Override
        public JsonElement serialize(ConsoleVariantSchema src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.id().toString());
        }
    }
}
