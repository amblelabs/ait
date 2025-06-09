package dev.amble.ait.core.tardis.handler.mood;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.Optional;

public record TardisMood(Moods moods, Alignment alignment, int weight) {

    public static final Codec<TardisMood> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.optionalFieldOf("mood").forGetter(tardismood -> Optional.of(tardismood.moods.toString())),
            Codec.STRING.optionalFieldOf("type")
                    .forGetter(tardismood -> Optional.of(tardismood.moods.alignment().toString())),
            Codec.INT.optionalFieldOf("weight").forGetter(tardisMood -> Optional.of(tardisMood.weight)))
            .apply(instance, (TardisMood::deserialize)));

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static TardisMood deserialize(Moods mood, Optional<Integer> weight) {
        return new TardisMood(mood, mood.alignment(), weight.orElse(mood.weight()));
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static TardisMood deserialize(Optional<String> mood, Optional<String> type, Optional<Integer> level) {
        return deserialize(type.map(Moods::valueOf).orElse(null), level);
    }

    /**
     * @author Loqor, Saturn
     * @implNote Simplified version of what Saturn said: - Each mood will have a tug
     *           of war with the other depending on their weights - which are
     *           arbitrary values that get set whenever an event happens - This tug
     *           of war will result in moods having the highest weight - and
     *           whichever mood has the highest weight at the end of some arbitrary
     *           "race" between each competing mood, it has the ability to randomly
     *           select with "weighted points" a triggered action - for example,
     *           changing dimensions randomly, or going to a random structure (which
     *           we'll have to implement but that's okay) - When that weighted mood
     *           selects an action (each action will have a cost and reduce the
     *           mood's weight by that cost) the mood events are completely over,
     *           and the values aren't reset, but set to what they were before that
     *           race happened + some arbitrary amount. An aside: Each mood will
     *           have a predefined positive, neutral, or negative value which can be
     *           overriden if necessary. The reasoning behind this is the higher the
     *           loyalty of a given player, the cheaper a positive action at the end
     *           of the race will be, resulting in more wins for positive moods.
     *           However, the opposite is possible. If multiple negative emotions
     *           are present, the additional points will be rejected and the
     *           positive mood will be reverted to its original value.
     */
    public enum Moods {
        // 10 negative moods
        ANGRY(0, Alignment.NEGATIVE), HATEFUL(0, Alignment.NEGATIVE), FEARFUL(0, Alignment.NEGATIVE), DEPRESSED(0,
                Alignment.NEGATIVE), HURT(0, Alignment.NEGATIVE), UPSET(0, Alignment.NEGATIVE), FRUSTRATED(0,
                        Alignment.NEGATIVE), JEALOUS(0, Alignment.NEGATIVE), DISAPPOINTED(0,
                                Alignment.NEGATIVE), ANXIOUS(0, Alignment.NEGATIVE),

        // 10 neutral moods
        TOLERANT(0, Alignment.NEUTRAL, -20), BORED(0, Alignment.NEUTRAL, -20), IMPASSIVE(0, Alignment.NEUTRAL,
                -20), CALM(0, Alignment.NEUTRAL, 20), MANIC(0, Alignment.NEUTRAL, -20), CURIOUS(0, Alignment.NEUTRAL,
                        20), LONELY(0, Alignment.NEUTRAL, -20), INDIFFERENT(0, Alignment.NEUTRAL,
                                0), MEDIOCRE(0, Alignment.NEUTRAL, 0), TIRED(0, Alignment.NEUTRAL, 10),

        // 10 positive moods
        HAPPY(0, Alignment.POSITIVE), EXCITED(0, Alignment.POSITIVE), COOPERATIVE(0, Alignment.POSITIVE), HOPEFUL(0,
                Alignment.POSITIVE), GRATEFUL(0, Alignment.POSITIVE), ENTHUSIASTIC(0, Alignment.POSITIVE), RELIEVED(0,
                        Alignment.POSITIVE), CONTENT(0,
                                Alignment.POSITIVE), OPTIMISTIC(0, Alignment.POSITIVE), JOYFUL(0, Alignment.POSITIVE);

        public static final Moods[] VALUES = Moods.values();

        public final int weight;
        public final Alignment type;
        public final int swayWeight;

        Moods(int weight, Alignment type, int swayWeight) {
            this.weight = weight;
            this.type = type;
            this.swayWeight = swayWeight;
        }

        Moods(int weight, Alignment type) {
            this(weight, type, 0);
        }

        public int weight() {
            return this.weight;
        }

        public Alignment alignment() {
            return type;
        }

        public int swayWeight() {
            return swayWeight;
        }
    }

    public enum Alignment {
        POSITIVE, NEUTRAL, NEGATIVE
    }
}
