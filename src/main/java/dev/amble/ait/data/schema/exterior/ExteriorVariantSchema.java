package dev.amble.ait.data.schema.exterior;

import java.lang.reflect.Type;

import com.google.gson.*;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.amble.ait.registry.v2.*;
import dev.amble.lib.data.MoreCodec;
import dev.amble.ait.data.datapack.exterior.BiomeOverrides;
import dev.amble.lib.data.PosRot;
import dev.amble.lib.register.unlockable.Unlockable;
import dev.amble.lib.registry.SimpleRegistryElementCodec;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.registry.entry.RegistryElementCodec;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

import dev.amble.ait.core.tardis.animation.ExteriorAnimation;
import dev.amble.ait.data.Loyalty;
import dev.amble.ait.data.schema.BasicSchema;
import dev.amble.ait.data.schema.door.DoorSchema;
import org.joml.Vector2d;

import static dev.amble.ait.data.datapack.DatapackConsole.EMPTY;

/**
 * A variant for a {@link ExteriorCategorySchema} which provides a model,
 * texture, emission, {@link ExteriorAnimation} and {@link DoorSchema} <br>
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
public class ExteriorVariantSchema extends BasicSchema implements Unlockable {

    public static final Codec<ExteriorVariantSchema> CODEC = RecordCodecBuilder.create(instance -> instance
            .group(Identifier.CODEC.fieldOf("id").forGetter(ExteriorVariantSchema::id),
                    AITRegistries.EXTERIOR_CATEGORY.entry().fieldOf("category").forGetter(ExteriorVariantSchema::category),
                    Identifier.CODEC.fieldOf("model").forGetter(ExteriorVariantSchema::modelId),
                    Identifier.CODEC.fieldOf("door").forGetter(ExteriorVariantSchema::doorId),
                    SimpleRegistryElementCodec.or("animation", AITRegistries.EXTERIOR_ANIMATION.entry(), ExteriorAnimationRegistry.PULSATING).forGetter(ExteriorVariantSchema::animation),
                    Identifier.CODEC.fieldOf("texture").forGetter(ExteriorVariantSchema::texture),
                    Identifier.CODEC.optionalFieldOf("emission", EMPTY).forGetter(ExteriorVariantSchema::emission),
                    Unlockable.optionalRequirement("loyalty"),
                    BiomeOverrides.CODEC.fieldOf("overrides").orElse(BiomeOverrides.EMPTY)
                            .forGetter(ExteriorVariantSchema::overrides),
                    Vec3d.CODEC.optionalFieldOf("seat_translations", new Vec3d(0.5, 1, 0.5)).forGetter(ExteriorVariantSchema::seatTranslations),
                    MoreCodec.POSROT.optionalFieldOf("sonic", new PosRot(new Vec3d(0.5, 1, 0.5), new Vec2f(0, 45)))
                            .forGetter(ExteriorVariantSchema::sonicTransform),
                    Codec.BOOL.optionalFieldOf("has_portal", false).forGetter(ExteriorVariantSchema::hasPortal),
                    MoreCodec.VECTOR2D.optionalFieldOf("portal_size", new Vector2d(1, 2)).forGetter(ExteriorVariantSchema::portalSize),
                    Vec3d.CODEC.optionalFieldOf("diagonal_portal_offset", null).forGetter(ExteriorVariantSchema::diagonalPortalOffset),
                    Vec3d.CODEC.optionalFieldOf("straight_portal_offset", null).forGetter(ExteriorVariantSchema::straightPortalOffset))
            .apply(instance, ExteriorVariantSchema::create));

    private static ExteriorVariantSchema create(Identifier id, RegistryEntry<ExteriorCategorySchema> category, Identifier modelId,
                               Identifier doorId, RegistryEntry<ExteriorAnimationRegistry.AnimationCreator> animation,
                               Identifier texture, Identifier emission, Loyalty loyalty, BiomeOverrides overrides, Vec3d seatTranslations,
                               PosRot sonicTransform, boolean hasPortal, Vector2d portalSize, Vec3d straightPortalOffset, Vec3d diagonalPortalOffset) {
        ExteriorVariantSchema schema = new ExteriorVariantSchema(id, category, modelId, doorId, animation, texture, emission, loyalty, overrides,
                seatTranslations, sonicTransform, hasPortal, portalSize, straightPortalOffset, diagonalPortalOffset);

        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT)
            schema = new ClientExteriorVariantSchema(schema);

        return schema;
    }

    protected final Identifier id;

    protected final RegistryEntry<ExteriorCategorySchema> category;

    protected final Identifier modelId;
    protected final Identifier doorId;
    protected final RegistryEntry<ExteriorAnimationRegistry.AnimationCreator> animation;

    protected final Identifier texture;
    protected final Identifier emission;

    protected final BiomeOverrides overrides;

    protected final Vec3d seatTranslations;
    protected final PosRot sonicTransform;

    protected final boolean hasPortal;
    protected final Vector2d portalSize;
    protected final Vec3d straightPortalOffset;
    protected final Vec3d diagonalPortalOffset;

    protected final Loyalty loyalty;

    public static final double DEFAULT_SEAT_FORWARD_TRANSLATION = 0.5;

    protected ExteriorVariantSchema(Identifier id, RegistryEntry<ExteriorCategorySchema> category, Identifier modelId,
                                    Identifier doorId, RegistryEntry<ExteriorAnimationRegistry.AnimationCreator> animation,
                                    Identifier texture, Identifier emission, Loyalty loyalty, BiomeOverrides overrides, Vec3d seatTranslations,
                                    PosRot sonicTransform, boolean hasPortal, Vector2d portalSize, Vec3d straightPortalOffset, Vec3d diagonalPortalOffset) {
        super("exterior");
        this.id = id;

        this.category = category;

        this.modelId = modelId;
        this.doorId = doorId;
        this.animation = animation;

        this.texture = texture;
        this.emission = emission;

        this.overrides = overrides;

        this.seatTranslations = seatTranslations;
        this.sonicTransform = sonicTransform;

        this.hasPortal = hasPortal;
        this.portalSize = portalSize;
        this.straightPortalOffset = straightPortalOffset;
        this.diagonalPortalOffset = diagonalPortalOffset;

        this.loyalty = loyalty;
    }

    @Override
    public Identifier id() {
        return id;
    }

    public RegistryEntry<ExteriorCategorySchema> category() {
        return category;
    }

    public Identifier modelId() {
        return modelId;
    }

    public Identifier doorId() {
        return doorId;
    }

    public RegistryEntry<ExteriorAnimationRegistry.AnimationCreator> animation() {
        return animation;
    }

    public Identifier texture() {
        return texture;
    }

    public Identifier emission() {
        return emission;
    }

    public BiomeOverrides overrides() {
        return overrides;
    }

    public Vec3d seatTranslations() {
        return seatTranslations;
    }

    public PosRot sonicTransform() {
        return sonicTransform;
    }

    public boolean hasPortal() {
        return hasPortal;
    }

    public Vector2d portalSize() {
        return portalSize;
    }

    public Vec3d diagonalPortalOffset() {
        return diagonalPortalOffset;
    }

    public Vec3d straightPortalOffset() {
        return straightPortalOffset;
    }

    public double seatForwardTranslation() {
        return DEFAULT_SEAT_FORWARD_TRANSLATION;
    }

    public Vec3d adjustPortalPos(Vec3d pos, byte direction) {
        if (direction % 4 == 0) {
            if (this.straightPortalOffset != null)
                return adjustStraightPortalPos(pos, direction);
        } else if (this.diagonalPortalOffset != null) {
            return adjustDiagonalPortalPos(pos, direction);
        }

        return pos;
    }

    private Vec3d adjustDiagonalPortalPos(Vec3d pos, byte direction) {
        double x = this.diagonalPortalOffset.getX();
        double y = this.diagonalPortalOffset.getY();
        double z = this.diagonalPortalOffset.getZ();

        return switch (direction) {
            case 1, 2, 3 -> pos.add(x, y, -z); // NORTH EAST p n
            case 5, 6, 7 -> pos.add(x, y, z); // SOUTH EAST p p
            case 9, 10, 11 -> pos.add(-x, y, z); // SOUTH WEST n p
            case 13, 14, 15 -> pos.add(-x, y, -z); // NORTH WEST n n
            default -> throw new IllegalStateException("Unexpected value: " + direction);
        };
    }

    private Vec3d adjustStraightPortalPos(Vec3d pos, byte direction) {
        double x = this.straightPortalOffset.getX();
        double y = this.straightPortalOffset.getY();
        double z = this.straightPortalOffset.getZ();

        return switch (direction) {
            case 0 -> pos.add(x, y, z); // NORTH x, y, z
            case 4 -> pos.add(-z, y, x); // EAST   -z, y, x
            case 8 -> pos.add(x, y, -z); // SOUTH  x, y, -z
            case 12 -> pos.add(z, y, x); // WEST z, y, x
            default -> throw new IllegalStateException("Unexpected value: " + direction);
        };
    }

    @Environment(EnvType.CLIENT)
    public ClientExteriorVariantSchema asClient() {
        return (ClientExteriorVariantSchema) this;
    }

    @Override
    public UnlockType unlockType() {
        return UnlockType.EXTERIOR;
    }

    @Override
    public Loyalty requirement() {
        return loyalty;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;

        return o instanceof ExteriorVariantSchema other && id.equals(other.id);
    }

    public static Object serializer() {
        return new Serializer();
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
                id = ExteriorVariantRegistry.FALLBACK;
            }

            return AITRegistries.EXTERIOR_VARIANT.tryGetOr(id, ExteriorVariantRegistry.FALLBACK);
        }

        @Override
        public JsonElement serialize(ExteriorVariantSchema src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.id().toString());
        }
    }
}
