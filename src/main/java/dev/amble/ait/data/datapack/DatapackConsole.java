package dev.amble.ait.data.datapack;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.amble.ait.core.tardis.animation.v2.bedrock.BedrockAnimation;
import dev.amble.ait.core.tardis.animation.v2.bedrock.BedrockAnimationRegistry;
import dev.amble.ait.core.tardis.control.ControlTypes;
import dev.amble.ait.core.tardis.handler.travel.TravelHandlerBase;
import dev.amble.ait.data.schema.console.ConsoleTypeSchema;
import dev.amble.ait.registry.impl.console.ConsoleRegistry;
import dev.amble.ait.registry.impl.console.variant.ConsoleVariantRegistry;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3f;

import net.minecraft.util.Identifier;

import dev.amble.ait.AITMod;
import dev.amble.ait.data.codec.MoreCodec;
import dev.amble.ait.data.schema.console.ConsoleVariantSchema;

import javax.management.openmbean.SimpleType;

// Example usage
/*
{
  "id": "ait:white_alnico",
  "parent": "ait:console/alnico",
  "texture": "ait:textures/console/alnico.png",
  "emission": "ait:textures/console/alnico_emission.png"
}
 */
public class DatapackConsole extends ConsoleVariantSchema {
    public static final Identifier EMPTY = AITMod.id("intentionally_empty");

    protected final Identifier texture;
    protected final Identifier emission;
    protected final Identifier id;
    protected final List<Float> sonicRotation;
    protected final Vector3f sonicTranslation;
    protected final List<Float> handlesRotation;
    protected final Vector3f handlesTranslation;
    protected final Identifier model;
    protected final Vec3d scale;
    protected final Vec3d offset;
    protected final AnimationMap animations;
    protected boolean initiallyDatapack;

    public static final Codec<DatapackConsole> CODEC = RecordCodecBuilder.create(instance -> instance
            .group(Identifier.CODEC.fieldOf("id").forGetter(ConsoleVariantSchema::id),
                    Identifier.CODEC.optionalFieldOf("parent").forGetter(c -> Optional.ofNullable(c.parentId())),
                    Identifier.CODEC.fieldOf("texture").forGetter(DatapackConsole::texture),
                    Identifier.CODEC.optionalFieldOf("emission", EMPTY).forGetter(DatapackConsole::emission),
                    Codec.list(Codec.FLOAT).optionalFieldOf("sonic_rotation", List.of())
                            .forGetter(DatapackConsole::sonicRotation),
                    MoreCodec.VECTOR3F.optionalFieldOf("sonic_translation", new Vector3f()).forGetter(DatapackConsole::sonicTranslation),
                    Codec.list(Codec.FLOAT).optionalFieldOf("handles_rotation", List.of())
                            .forGetter(DatapackConsole::handlesRotation),
                    MoreCodec.VECTOR3F.optionalFieldOf("handles_translation", new Vector3f()).forGetter(DatapackConsole::handlesTranslation),
                    Identifier.CODEC.optionalFieldOf("model").forGetter(DatapackConsole::model),
                    Vec3d.CODEC.optionalFieldOf("scale", new Vec3d(1, 1, 1)).forGetter(DatapackConsole::getScale),
                    Vec3d.CODEC.optionalFieldOf("offset", new Vec3d(0, 0, 0)).forGetter(DatapackConsole::getOffset),
                    AnimationMap.CODEC.optionalFieldOf("animations", new AnimationMap())
                            .forGetter(DatapackConsole::getAnimations),
                    SimpleType.CODEC.optionalFieldOf("type").forGetter(DatapackConsole::getCustomType),
                    Codec.BOOL.optionalFieldOf("isDatapack", true).forGetter(DatapackConsole::wasDatapack))
            .apply(instance, DatapackConsole::new));

    public DatapackConsole(Identifier id,
                           Optional<Identifier> category,
                           Identifier texture,
                           Identifier emission,
                           List<Float> sonicRot,
                           Vector3f sonicTranslation,
                           List<Float> handlesRot,
                           Vector3f handlesTranslation,
                           Optional<Identifier> model,
                           Vec3d scale,
                           Vec3d offset,
                           AnimationMap animations,
                           Optional<SimpleType> type,
                           boolean isDatapack) {
        super(resolveParentId(category, type), id);
        this.id = id;
        this.texture = texture;
        this.emission = emission;
        this.initiallyDatapack = isDatapack;
        this.sonicRotation = sonicRot;
        this.sonicTranslation = sonicTranslation;
        this.handlesRotation = handlesRot;
        this.handlesTranslation = handlesTranslation;
        this.model = model.orElse(null);
        this.scale = scale;
        this.offset = offset;
        this.animations = animations != null ? animations : new AnimationMap();
    }

    private static Identifier resolveParentId(Optional<Identifier> parent, Optional<SimpleType> type) {
        System.out.println(parent);
        System.out.println(type);

        if (parent.isPresent()) {
            return parent.get();
        } else if (type.isPresent()) {
            type.get().register();
            return type.get().id();
        } else {
            throw new IllegalArgumentException("DatapackConsole must have a parent or a type defined");
        }
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

    public Identifier id() {
        return this.id;
    }

    public List<Float> sonicRotation() {
        return this.sonicRotation;
    }
    public Vector3f sonicTranslation() {
        return this.sonicTranslation;
    }

    public List<Float> handlesRotation() {
        return this.handlesRotation;
    }
    public Vector3f handlesTranslation() {
        return this.handlesTranslation;
    }

    public Optional<Identifier> model() {
        return Optional.ofNullable(model);
    }

    public Vec3d getScale() {
        return scale;
    }

    public Vec3d getOffset() {
        return offset;
    }

    public AnimationMap getAnimations() {
        return animations;
    }

    public Optional<SimpleType> getCustomType() {
        if (this.parent() instanceof SimpleType simpleType) {
            return Optional.of(simpleType);
        }
        return Optional.empty();
    }

    public static DatapackConsole fromInputStream(InputStream stream) {
        return fromJson(JsonParser.parseReader(new InputStreamReader(stream)).getAsJsonObject());
    }

    public static DatapackConsole fromJson(JsonObject json) {
        AtomicReference<DatapackConsole> created = new AtomicReference<>();

        CODEC.decode(JsonOps.INSTANCE, json).get().ifLeft(var -> created.set(var.getFirst())).ifRight(err -> {
            created.set(null);
            AITMod.LOGGER.error("Error decoding datapack console variant: {}", err);
        });

        return created.get();
    }

    public static class AnimationMap extends EnumMap<TravelHandlerBase.State, BedrockAnimationRegistry.Reference> {
        public static Codec<AnimationMap> CODEC = Codec.unboundedMap(TravelHandlerBase.State.CODEC, BedrockAnimationRegistry.Reference.CODEC)
                .xmap(AnimationMap::new, map -> map);

        public AnimationMap() {
            super(TravelHandlerBase.State.class);
        }

        public AnimationMap(Map<TravelHandlerBase.State, BedrockAnimationRegistry.Reference> map) {
            this();

            this.putAll(map);
        }

        public BedrockAnimation getAnimation(TravelHandlerBase.State state) {
            BedrockAnimationRegistry.Reference ref = this.get(state);
            if (ref == null) {
                return null; // No animation registered for this state
            }

            return ref.get().orElse(null);
        }
    }

    public static class SimpleType extends ConsoleTypeSchema {
        public static final Codec<SimpleType> CODEC = RecordCodecBuilder.create(instance -> instance
                .group(Identifier.CODEC.fieldOf("id").forGetter(SimpleType::id),
                        Codec.STRING.fieldOf("name").forGetter(SimpleType::name),
                        ControlTypes.CODEC.listOf().fieldOf("controls").forGetter(c -> c.controls))
                .apply(instance, SimpleType::new));

        private final List<ControlTypes> controls;

        protected SimpleType(Identifier id, String name, List<ControlTypes> controls) {
            super(id, name);

            this.controls = controls;
        }

        @Override
        public ControlTypes[] getControlTypes() {
            return controls.toArray(new ControlTypes[0]);
        }

        public void register() {
            ConsoleRegistry.getInstance().register(this);
        }
    }
}
