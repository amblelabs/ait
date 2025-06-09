package dev.amble.ait.core.tardis.handler.mood;

import dev.amble.ait.AITMod;
import dev.amble.ait.core.tardis.ServerTardis;
import net.minecraft.util.Identifier;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public interface MoodDictatedEvent {

    default Identifier id() {
        return AITMod.id("mood_dictated_event");
    }

    void execute(ServerTardis tardis);

    int getCost();

    Set<TardisMood.Moods> getMoodsList();

    TardisMood.Alignment getMoodTypeCompatibility();

    class Builder implements MoodDictatedEvent {
        private final Identifier id;
        private final ExecuteMoodEvent execute;
        private final int cost;
        private final Set<TardisMood.Moods> moodsList;
        private final TardisMood.Alignment alignmentCompatibility;

        public Builder(Identifier id, ExecuteMoodEvent execute, int cost, TardisMood.Alignment alignment,
                TardisMood.Moods... moods) {
            this.id = id;
            this.execute = execute;
            this.cost = cost;
            this.moodsList = new HashSet<>(List.of(moods));
            this.alignmentCompatibility = alignment;
        }

        public static MoodDictatedEvent create(Identifier id, ExecuteMoodEvent execute, int cost,
                TardisMood.Alignment alignment, TardisMood.Moods... moods) {
            return new MoodDictatedEvent.Builder(id, execute, cost, alignment, moods);
        }

        @Override
        public Identifier id() {
            return this.id;
        }

        @Override
        public int getCost() {
            return this.cost;
        }

        @Override
        public void execute(ServerTardis tardis) {
            this.execute.run(tardis);
        }

        @Override
        public Set<TardisMood.Moods> getMoodsList() {
            return this.moodsList;
        }

        @Override
        public TardisMood.Alignment getMoodTypeCompatibility() {
            return this.alignmentCompatibility;
        }

        public interface ExecuteMoodEvent {
            void run(ServerTardis tardis);
        }
    }
}
