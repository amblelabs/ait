package dev.amble.ait.core.tardis.handler.mood.v2;

import it.unimi.dsi.fastutil.ints.*;
import net.minecraft.util.math.MathHelper;

public class Emotion {

    public final Type type;
    public float value;

    public Emotion(Type type) {
        this.type = type;
    }

    public float get() {
        return MathHelper.clamp(value, 0f, 1f);
    }

    public record Range(IntSet emotions, float influence) {

        public static Range create(float influence, Type... types) {
            IntSet set = new IntOpenHashSet();

            for (Type type : types) {
                set.add(type.ordinal());
            }

            return new Range(set, influence);
        }

        public void add(EmotionContainer container, Type type, float amount) {
            if (!emotions.contains(type.ordinal())) return;

            Emotion emotion = container.get(type);
            emotion.value = norm(emotion.value + amount);

            for (int emotionId : emotions) {
                if (emotionId == type.ordinal()) continue;

                emotion = container.get(emotionId);
                emotion.value = norm(emotion.value - amount * influence);
            }
        }
    }

    static float norm(float f) {
        return MathHelper.clamp(f, 0, 1);
    }

    public enum Type {
        TOLERANT,
        BOREDOM,
        CALM,
        LONELINESS,
        TIRED,

        ANGER,
        FEAR,
        DEPRESSED,
        UPSET,
        JEALOUS,

        CONTENT,
        JOY,
        HOPE,
        RELIEVED,
        COZY,
        GRATEFUL;

        public Emotion asEmotion() {
            return new Emotion(this);
        }
    }
}
