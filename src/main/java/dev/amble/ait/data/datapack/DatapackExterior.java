package dev.amble.ait.data.datapack;

import static dev.amble.ait.data.datapack.DatapackConsole.EMPTY;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import dev.amble.ait.core.tardis.animation.v2.bedrock.BedrockAnimationRegistry;
import dev.amble.ait.core.util.PortalOffsets;
import dev.amble.ait.data.schema.door.AnimatedDoor;
import dev.amble.ait.data.schema.door.DatapackDoor;
import dev.amble.ait.registry.impl.door.DoorRegistry;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;

import dev.amble.ait.AITMod;
import dev.amble.ait.data.Loyalty;
import dev.amble.ait.data.datapack.exterior.BiomeOverrides;
import dev.amble.ait.data.schema.door.DoorSchema;
import dev.amble.ait.data.schema.exterior.ExteriorVariantSchema;
import dev.amble.ait.registry.impl.exterior.ExteriorVariantRegistry;
import org.apache.http.impl.io.IdentityInputStream;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class DatapackExterior extends ExteriorVariantSchema implements AnimatedDoor {

    public static final Identifier DEFAULT_TEXTURE = new Identifier(AITMod.MOD_ID,
            "textures/gui/tardis/desktop/missing_preview.png");

    protected final Identifier parent;
    protected final Identifier texture;
    protected final Identifier emission;
    protected final BiomeOverrides overrides;
    protected final Vec3d seatTranslations;
    protected final boolean initiallyDatapack;
    protected final boolean hasTransparentDoors;
    protected final Identifier model;
    protected final Identifier doorId;
    protected final PortalOffsets portalOffsets;
    protected final BedrockAnimationRegistry.Reference leftAnimation;
    protected final BedrockAnimationRegistry.Reference rightAnimation;
    protected final Vec3d scale;

    public static final Codec<DatapackExterior> CODEC = RecordCodecBuilder.create(instance -> instance
            .group(Identifier.CODEC.fieldOf("id").forGetter(ExteriorVariantSchema::id),
                    Identifier.CODEC.fieldOf("category").forGetter(ExteriorVariantSchema::categoryId),
                    Identifier.CODEC.fieldOf("parent").forGetter(DatapackExterior::getParentId),
                    Identifier.CODEC.fieldOf("texture").forGetter(DatapackExterior::texture),
                    Identifier.CODEC.optionalFieldOf("emission", EMPTY).forGetter(DatapackExterior::emission),
                    Loyalty.CODEC.optionalFieldOf("loyalty").forGetter(DatapackExterior::requirement),
                    BiomeOverrides.CODEC.fieldOf("overrides").orElse(BiomeOverrides.EMPTY)
                            .forGetter(DatapackExterior::overrides),
                    Vec3d.CODEC.optionalFieldOf("seat_translations", new Vec3d(0.5, 1, 0.5)).forGetter(DatapackExterior::seatTranslations),
                    Codec.BOOL.optionalFieldOf("has_transparent_doors", false).forGetter(DatapackExterior::hasTransparentDoors),
                    Identifier.CODEC.optionalFieldOf("model").forGetter(DatapackExterior::model),
                    Identifier.CODEC.optionalFieldOf("door").forGetter(DatapackExterior::getDoorId),
                    PortalOffsets.CODEC.optionalFieldOf("portal_info", new PortalOffsets(1, 2)).forGetter(DatapackExterior::getPortalOffsets),
                    BedrockAnimationRegistry.Reference.CODEC.optionalFieldOf("left_animation").forGetter(DatapackExterior::getLeftAnimation),
                    BedrockAnimationRegistry.Reference.CODEC.optionalFieldOf("right_animation").forGetter(DatapackExterior::getRightAnimation),
                    Vec3d.CODEC.optionalFieldOf("scale", new Vec3d(1, 1, 1)).forGetter(DatapackExterior::getScale),
                    Codec.BOOL.optionalFieldOf("isDatapack", true).forGetter(DatapackExterior::wasDatapack)
            ).apply(instance, DatapackExterior::new)
        );

    public DatapackExterior(Identifier id, Identifier category, Identifier parent, Identifier texture,
                            Identifier emission, Optional<Loyalty> loyalty, BiomeOverrides overrides, Vec3d seatTranslations, boolean hasTransparentDoors, Optional<Identifier> model, Optional<Identifier> door, PortalOffsets offsets, Optional<BedrockAnimationRegistry.Reference> leftAnimation, Optional<BedrockAnimationRegistry.Reference> rightAnimation, Vec3d scale, boolean isDatapack) {
        super(category, id, loyalty);
        this.parent = parent;
        this.texture = texture;
        this.emission = emission;
        this.seatTranslations = seatTranslations;
        this.hasTransparentDoors = hasTransparentDoors;
        this.initiallyDatapack = isDatapack;
        this.overrides = overrides;
        this.model = model.orElse(null);
        this.doorId = door.orElse(null);
        this.portalOffsets = offsets;
        this.leftAnimation = leftAnimation.orElse(null);
        this.rightAnimation = rightAnimation.orElse(null);
        this.scale = scale;
    }

    public static DatapackExterior fromInputStream(InputStream stream) {
        return fromJson(JsonParser.parseReader(new InputStreamReader(stream)).getAsJsonObject());
    }

    public static DatapackExterior fromJson(JsonObject json) {
        AtomicReference<DatapackExterior> created = new AtomicReference<>();

        CODEC.decode(JsonOps.INSTANCE, json).get().ifLeft(var -> created.set(var.getFirst())).ifRight(err -> {
            created.set(null);
            AITMod.LOGGER.error("Error decoding datapack exterior variant: {}", err);
        });

        return created.get();
    }

    public ExteriorVariantSchema getParent() {
        return ExteriorVariantRegistry.getInstance().get(this.getParentId());
    }

    public Identifier getParentId() {
        return this.parent;
    }

    private Optional<Identifier> getDoorId() {
        return Optional.ofNullable(this.door().id());
    }

    @Override
    public DoorSchema door() {
        if (doorId == null) {
            return this.getParent().door();
        }

        return DoorRegistry.getInstance().getOrElse(doorId, this.getParent().door());
    }

    public BiomeOverrides overrides() {
        return overrides;
    }

    @Override
    public Vec3d seatTranslations() {
        return seatTranslations;
    }

    public boolean hasTransparentDoors() {
        return hasTransparentDoors;
    }

    @Override
    public VoxelShape bounding(Direction dir) {
        return this.getParent().bounding(dir);
    }

    @Override
    public boolean hasPortals() {
        if (this.getPortalOffsets() != null) {
            return this.getPortalOffsets().isEnabled();
        }

        return this.getParent().hasPortals();
    }

    @Override
    public Vec3d adjustPortalPos(Vec3d pos, byte direction) {
        if (this.getPortalOffsets() != null) {
            return this.getPortalOffsets().apply(pos, direction);
        }

        return this.getParent().adjustPortalPos(pos, direction);
    }

    @Override
    public double portalWidth() {
        if (this.getPortalOffsets() != null) {
            return this.getPortalOffsets().getWidth();
        }

        return this.getParent().portalWidth();
    }

    @Override
    public double portalHeight() {
        if (this.getPortalOffsets() != null) {
            return this.getPortalOffsets().getHeight();
        }

        return this.getParent().portalHeight();
    }

    public boolean wasDatapack() {
        return this.initiallyDatapack;
    }

    public Identifier texture() {
        return this.texture;
    }

    public Identifier emission() {
        return this.emission;
    }

    /**
     * A possible identifier for a bedrock model in the BedrockModelRegistry.
     */
    public Optional<Identifier> model() {
        return Optional.ofNullable(this.model);
    }

    public PortalOffsets getPortalOffsets() {
        return this.portalOffsets;
    }

    @Override
    public Optional<BedrockAnimationRegistry.Reference> getLeftAnimation() {
        return Optional.ofNullable(this.leftAnimation);
    }

    @Override
    public Optional<BedrockAnimationRegistry.Reference> getRightAnimation() {
        return Optional.ofNullable(this.rightAnimation);
    }

    @Override
    public Vec3d getScale() {
        return scale;
    }
}
