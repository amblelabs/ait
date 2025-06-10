package dev.amble.ait.data.schema.sonic;

import java.util.Optional;
import java.util.function.Consumer;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.amble.lib.register.unlockable.Unlockable;

import net.minecraft.util.Identifier;

import dev.amble.ait.data.Loyalty;
import dev.amble.ait.data.schema.BasicSchema;

public abstract class SonicSchema extends BasicSchema implements Unlockable {

    private final Identifier id;
    private final Models models;
    private final Rendering rendering;
    private final Loyalty loyalty;

    protected SonicSchema(Identifier id, Models models, Rendering rendering, Optional<Loyalty> loyalty) {
        super("sonic");

        this.id = id;
        this.models = models;
        this.rendering = rendering;
        this.loyalty = loyalty.orElse(null);
    }

    protected SonicSchema(Identifier id, Models models, Rendering rendering) {
        this(id, models, rendering, Optional.empty());
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
        return UnlockType.SONIC;
    }

    public Models models() {
        return models;
    }

    public Rendering rendering() {
        return rendering;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;

        if (o == null)
            return false;

        return o instanceof SonicSchema that && id.equals(that.id);
    }

    public record Models(Identifier inactive, Identifier interaction, Identifier overload, Identifier scanning,
            Identifier tardis) {
        public static final Codec<Models> CODEC = RecordCodecBuilder
                .create(instance -> instance
                        .group(Identifier.CODEC.fieldOf("inactive").forGetter(Models::inactive),
                                Identifier.CODEC.fieldOf("interaction").forGetter(Models::interaction),
                                Identifier.CODEC.fieldOf("overload").forGetter(Models::overload),
                                Identifier.CODEC.fieldOf("scanning").forGetter(Models::scanning),
                                Identifier.CODEC.fieldOf("tardis").forGetter(Models::tardis))
                        .apply(instance, Models::new));

        public void load(Consumer<Identifier> consumer) {
            consumer.accept(inactive);
            consumer.accept(interaction);
            consumer.accept(overload);
            consumer.accept(scanning);
            consumer.accept(tardis);
        }
    }

    public record Rendering(Optional<Offset> positionOffset, Optional<Offset> scaleOffset) {
        public static final Codec<Rendering> CODEC = RecordCodecBuilder.create(instance -> instance
                .group(Offset.CODEC.optionalFieldOf("position").forGetter(Rendering::positionOffset),
                        Offset.CODEC.optionalFieldOf("scale").forGetter(Rendering::scaleOffset))
                .apply(instance, Rendering::new));

        public Rendering() {
            this(Optional.of(new Offset()), Optional.of(new Offset()));
        }

        public record Offset(float x, float y, float z) {
            static final Codec<Offset> CODEC = RecordCodecBuilder.create(instance -> instance
                    .group(Codec.FLOAT.fieldOf("x").forGetter(Offset::x), Codec.FLOAT.fieldOf("y").forGetter(Offset::y),
                            Codec.FLOAT.fieldOf("z").forGetter(Offset::z))
                    .apply(instance, Offset::new));

            public Offset() {
                this(0, 0, 0);
            }
        }

        public Offset getPositionOffset() {
            return positionOffset.orElse(new Offset());
        }

        public Offset getScaleOffset() {
            return scaleOffset.orElse(new Offset());
        }
    }
}
