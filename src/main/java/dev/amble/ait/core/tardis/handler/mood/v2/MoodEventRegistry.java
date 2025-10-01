package dev.amble.ait.core.tardis.handler.mood.v2;

import dev.amble.ait.core.tardis.ServerTardis;
import it.unimi.dsi.fastutil.floats.FloatPredicate;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class MoodEventRegistry {

    private static final List<MoodEvent> events = new ArrayList<>();

    static {
        events.add(new MoodEvent(tardis -> tardis.world().getPlayers().forEach(player -> {
            player.sendMessage(Text.literal("yippe, content >0.5!"));
        }), 1, Requires.biggerThan(Emotion.Type.CONTENT, 0.5f, 0.01f)));

        events.add(new MoodEvent(tardis -> tardis.world().getPlayers().forEach(player -> {
            tardis.loyalty().subLevel(player, 1);
        }), 7, Requires.biggerThan(Emotion.Type.UPSET, 0.75f, 0.01f)));

        events.add(new MoodEvent(
                tardis -> tardis.door().setLocked(true),
                tardis -> tardis.travel().isLanded(),
                5, Requires.biggerThan(Emotion.Type.FEAR, 0.75f, 0.5f
        )));

        events.add(new MoodEvent(
                tardis -> tardis.selfDestruct().boom(),
                15, Requires.biggerThan(Emotion.Type.DEPRESSED, 0.99f, 1f)
        ));
    }

    public static MoodEvent poll(ServerTardis tardis, EmotionContainer container, int cost) {
        return events.stream().filter(moodEvent -> canBeApplied(tardis, container, moodEvent, cost))
                .max(Comparator.comparingDouble(MoodEvent::cost))
                .orElse(null);
    }

    private static boolean canBeApplied(ServerTardis tardis, EmotionContainer container, MoodEvent event, int cost) {
        if (event.cost > cost)
            return false;

        if (event.canRun != null && !event.canRun.test(tardis))
            return false;

        for (Requires requires : event.requirements) {
            if (!requires.test(container))
                return false;
        }

        return true;
    }

    public record MoodEvent(Consumer<ServerTardis> consumer, Predicate<ServerTardis> canRun, int cost, Requires... requirements) {

        public MoodEvent(Consumer<ServerTardis> consumer, int cost, Requires... requirements) {
            this(consumer, null, cost, requirements);
        }

        public void execute(ServerTardis tardis) {
            consumer.accept(tardis);
        }
    }

    public record Requires(Emotion.Type type, float penalty, FloatPredicate predicate) {

        public static Requires biggerThan(Emotion.Type type, float f, float penalty) {
            return new Requires(type, penalty, v -> v > f);
        }

        public static Requires inBetween(float e, Emotion.Type type, float start, float end, float penalty) {
            return new Requires(type, penalty, v -> v > start && v < end);
        }

        public static Requires smallerThan(float e, Emotion.Type type, float f) {
            return new Requires(type, e, v -> v < f);
        }

        public boolean test(EmotionContainer container) {
            return predicate.test(container.get(type).value);
        }
    }
}
