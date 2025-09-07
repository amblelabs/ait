package dev.amble.ait.data.schema.exterior;

import java.lang.reflect.Type;
import java.util.Optional;

import com.google.gson.*;
import dev.amble.lib.register.unlockable.Unlockable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;

import dev.amble.ait.AITMod;
import dev.amble.ait.data.Loyalty;
import dev.amble.ait.data.schema.BasicSchema;
import dev.amble.ait.data.schema.door.DoorSchema;
import dev.amble.ait.registry.impl.CategoryRegistry;
import dev.amble.ait.registry.impl.exterior.ClientExteriorVariantRegistry;
import dev.amble.ait.registry.impl.exterior.ExteriorVariantRegistry;

/**
 * A variant for a {@link ExteriorCategorySchema} which provides a model,
 * texture, emission, and {@link DoorSchema} <br>
 * <br>
 * This should be registered in {@link ExteriorVariantRegistry} <br>
 * <br>
 * This should <b>ONLY</b> be created once in registry, you should grab the
 * class via {@link ExteriorVariantRegistry#get(Identifier)}, the identifier
 * being this variants id variable. <br>
 * <br>
 * It is recommended for implementations of this class to have a static
 * "REFERENCE" {@link Identifier} variable which other things can use to get
 * this from the {@link ExteriorVariantRegistry}
 *
 * @author duzo
 * @see ExteriorVariantRegistry
 */
public abstract class ExteriorVariantSchema extends BasicSchema implements Unlockable {
    private final Identifier category;
    private final Identifier id;
    private final Loyalty loyalty;

    public static final double DEFAULT_SEAT_FORWARD_TRANSLATION = 0.5;
    public static final Vec3d DEFAULT_SEAT_POS = new Vec3d(0.5, 1, 0.5);
    @Environment(EnvType.CLIENT)
    private ClientExteriorVariantSchema cachedSchema;

    protected ExteriorVariantSchema(Identifier category, Identifier id, Optional<Loyalty> loyalty) {
        super("exterior");
        this.category = category;

        this.id = id;
        this.loyalty = loyalty.orElse(null);
    }

    protected ExteriorVariantSchema(Identifier category, Identifier id, Loyalty loyalty) {
        this(category, id, Optional.of(loyalty));
    }
    protected ExteriorVariantSchema(Identifier category, Identifier id) {
        this(category, id, Optional.empty());
    }

    public static Object serializer() {
        return new Serializer();
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
        return UnlockType.EXTERIOR;
    }

    public Identifier categoryId() {
        return this.category;
    }

    public abstract Vec3d seatTranslations();

    public double seatForwardTranslation() {
        return DEFAULT_SEAT_FORWARD_TRANSLATION;
    }

    public ExteriorCategorySchema category() {
        return CategoryRegistry.getInstance().get(this.categoryId());
    }

    @Environment(EnvType.CLIENT)
    public ClientExteriorVariantSchema getClient() {
        if (this.cachedSchema == null)
            this.cachedSchema = ClientExteriorVariantRegistry.withParent(this);

        return cachedSchema;
    }

    public VoxelShape bounding(Direction dir) {
        return null;
    }

    public abstract DoorSchema door();

    public boolean hasPortals() {
        return this.category().hasPortals();
    }

    public Vec3d adjustPortalPos(Vec3d pos, byte direction) {
        return pos; // just cus some dont have portals
    }

    public double portalWidth() {
        return 1d;
    }

    public double portalHeight() {
        return 2d;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;

        return o instanceof ExteriorVariantSchema other && id.equals(other.id);
    }

    private static class Serializer
            implements
            JsonSerializer<ExteriorVariantSchema>,
            JsonDeserializer<ExteriorVariantSchema> {

        @Override
        public ExteriorVariantSchema deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            Identifier id;

            try {
                id = new Identifier(json.getAsJsonPrimitive().getAsString());
            } catch (InvalidIdentifierException e) {
                id = AITMod.id("capsule_default");
            }

            return ExteriorVariantRegistry.getInstance().get(id);
        }

        @Override
        public JsonElement serialize(ExteriorVariantSchema src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.id().toString());
        }
    }
}
