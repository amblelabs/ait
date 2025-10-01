package dev.amble.ait.core.tardis.handler.mood.v2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EmotionRegistry {

    public static final List<Emotion.Range> ranges = new ArrayList<>();

    static {
        range(0.5f, Emotion.Type.CONTENT, Emotion.Type.DEPRESSED, Emotion.Type.LONELINESS, Emotion.Type.JEALOUS);

        range(0.75f, Emotion.Type.UPSET, Emotion.Type.CALM, Emotion.Type.JOY);

        group(0.5f, Emotion.Type.UPSET, Emotion.Type.DEPRESSED);
    }

    public static void range(float influence, Emotion.Type... type) {
        ranges.add(Emotion.Range.create(influence, type));
    }

    public static void group(float influence, Emotion.Type... type) {
        range(-influence, type);
    }

    public static EmotionContainer createContainer() {
        Emotion[] emotions = Arrays.stream(Emotion.Type.values()).map(Emotion.Type::asEmotion).toArray(Emotion[]::new);
        return new EmotionContainer(emotions);
    }
}
