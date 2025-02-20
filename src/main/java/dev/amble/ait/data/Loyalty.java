package dev.amble.ait.data;

import java.util.Optional;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record Loyalty(int level, Type type) {

    public static final Codec<Loyalty> CODEC = RecordCodecBuilder.create(instance -> instance
            .group(Codec.STRING.optionalFieldOf("type").forGetter(loyalty -> Optional.of(loyalty.type.toString())),
                    Codec.INT.optionalFieldOf("level").forGetter(loyalty -> Optional.of(loyalty.level)))
            .apply(instance, (Loyalty::deserialize)));

    public Loyalty(Type type) {
        this(type.level, type);
    }

    public Loyalty add(int level) {
        return Loyalty.fromLevel(this.level + level);
    }

    public Loyalty subtract(int level) {
        return Loyalty.fromLevel(this.level - level);
    }

    public boolean greaterOrEqual(Loyalty other) {
        return this.level >= other.level;
    }

    public boolean smallerOrEqual(Loyalty other) {
        return this.level <= other.level;
    }

    public boolean smallerThan(Loyalty other) {
        return this.level < other.level;
    }

    public boolean isOf(Type type) {
        return this.level >= type.level;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;

        return obj instanceof Loyalty other && this.level == other.level;
    }

    public static Loyalty fromLevel(int level) {
        level = Type.normalize(level);
        return new Loyalty(level, Type.get(level));
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static Loyalty deserialize(Type type, Optional<Integer> level) {
        return level.map(Loyalty::fromLevel).orElseGet(() -> new Loyalty(type)); // it's one way or another
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static Loyalty deserialize(Optional<String> type, Optional<Integer> level) {
        return deserialize(type.map(Type::valueOf).orElse(null), level);
    }

    public enum Type {
        REJECT(0), NEUTRAL(125), COMPANION(245), PILOT(450), OWNER(500);

        public final int level;

        Type(int start) {
            this.level = start;
        }

        public static Type get(String id) {
            return Type.valueOf(id.toUpperCase());
        }

        public static Type get(int level) {
            level = Type.normalize(level);

            for (int i = 0; i < values().length - 1; i++) {
                Type current = values()[i];
                Type next = values()[i + 1];

                if (current.level <= level && level < next.level)
                    return current;
            }

            return Type.OWNER;
        }

        public static int normalize(int level) {
            return Math.min(Math.max(level, Type.REJECT.level), Type.OWNER.level);
        }

        public Type next() {
            return switch (this) {
                case REJECT -> NEUTRAL;
                case NEUTRAL -> COMPANION;
                case COMPANION -> PILOT;
                case PILOT -> OWNER;
                case OWNER -> REJECT;
            };
        }
    }
}
