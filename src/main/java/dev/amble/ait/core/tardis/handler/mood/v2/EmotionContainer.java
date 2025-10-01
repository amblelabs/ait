package dev.amble.ait.core.tardis.handler.mood.v2;

public class EmotionContainer {

    private final Emotion[] emotions;

    public EmotionContainer(Emotion[] emotions) {
        this.emotions = emotions;
    }

    public void add(Emotion.Type type, float amount) {
        for (Emotion.Range range : EmotionRegistry.ranges) {
            range.add(this, type, amount);
        }
    }

    public Emotion get(Emotion.Type type) {
        return emotions[type.ordinal()];
    }

    public Emotion get(int i) {
        return emotions[i];
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        builder.append("{\n");

        for (Emotion emotion : emotions) {
            builder.append('\t').append(emotion.type).append(": ").append(emotion.value).append('\n');
        }

        return builder.append('}').toString();
    }
}
