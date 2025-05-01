package dev.amble.ait.core.tardis.handler.mood;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import org.jetbrains.annotations.Nullable;

import net.minecraft.server.MinecraftServer;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import dev.amble.ait.AITMod;
import dev.amble.ait.api.tardis.TardisComponent;
import dev.amble.ait.api.tardis.TardisTickable;
import dev.amble.ait.core.tardis.ServerTardis;
import dev.amble.ait.core.tardis.util.TardisUtil;
import dev.amble.ait.data.Exclude;
import dev.amble.ait.registry.impl.MoodEventPoolRegistry;

public class MoodHandler extends TardisComponent implements TardisTickable {

    @Exclude
    public TardisMood.Moods[] priorityMoods;

    @Exclude
    private MoodDictatedEvent moodEvent;

    @Exclude
    private TardisMood winningMood;

    /**
     * Do NOT under any circumstances run logic in this constructor. Default field
     * values should be inlined. All logic should be done in an appropriate init
     * method.
     *
     * @implNote The {@link TardisComponent#tardis()} will always be null at the
     *           time this constructor gets called.
     */
    public MoodHandler() {
        super(Id.MOOD);
    }

    @Override
    public void onCreate() {
        this.randomizePriorityMoods();
    }

    @Nullable public MoodDictatedEvent getEvent() {
        return moodEvent;
    }

    @Override
    public void tick(MinecraftServer world) {
        if (this.moodEvent == null || this.winningMood == null)
            return;

        TardisMood.Alignment moodAlignment = this.moodEvent.getMoodTypeCompatibility();

        if (matchesMood(this.winningMood.alignment(), moodAlignment)) {
            if (this.winningMood.weight() >= this.moodEvent.getCost()) {
                handleMoodDictatedEvent(this.winningMood.alignment(), moodAlignment, this.moodEvent);
            }
        } else {
            this.winningMood = null;
        }
    }

    private boolean matchesMood(TardisMood.Alignment winningMoodAlignment, TardisMood.Alignment moodAlignment) {
        return winningMoodAlignment == TardisMood.Alignment.NEUTRAL || winningMoodAlignment == moodAlignment;
    }

    private void handleMoodDictatedEvent(TardisMood.Alignment winningMoodAlignment, TardisMood.Alignment moodAlignment,
            MoodDictatedEvent moodEvent) {
        switch (winningMoodAlignment) {
            case NEGATIVE -> handleNegativeMood(moodAlignment);
            case POSITIVE -> handlePositiveMood(this.winningMood.moods(), moodAlignment);
            case NEUTRAL -> handleNeutralMood(moodEvent, moodAlignment, this.winningMood.moods());
        }
    }

    public void rollForMoodDictatedEvent() {
        int rand = AITMod.RANDOM.nextInt(0, MoodEventPoolRegistry.REGISTRY.size());
        MoodDictatedEvent moodEvent = MoodEventPoolRegistry.REGISTRY.get(rand);

        if (moodEvent == null)
            return;

        this.moodEvent = moodEvent;
        TardisUtil.getPlayersInsideInterior(this.tardis.asServer()).forEach(player -> player
                .sendMessage(Text.literal(this.moodEvent.id().getPath()).formatted(Formatting.BOLD), true));

        raceMoods();
    }

    public void raceMoods() {
        Map<TardisMood.Moods, Integer> moodWeights = new HashMap<>();

        TardisMood.Moods[] moods = priorityMoods.length == 0 ? TardisMood.Moods.VALUES : priorityMoods;

        for (TardisMood.Moods mood : moods) {
            int weight = 8 + (AITMod.RANDOM.nextInt(0, 11) * 8);
            moodWeights.put(mood, Math.min(weight, 256));
        }

        Map.Entry<TardisMood.Moods, Integer> moodWin = moodWeights.entrySet().stream()
                .max(Comparator.comparingInt(Map.Entry::getValue)).orElse(null);

        TardisMood.Moods key = moodWin.getKey();

        this.winningMood = new TardisMood(key, moodWin.getKey().alignment(), moodWin.getValue());
        this.tardis.getDesktop().playSoundAtEveryConsole(SoundEvents.BLOCK_AMETHYST_BLOCK_CHIME, SoundCategory.BLOCKS, 1, 1);
    }

    private void handleNegativeMood(TardisMood.Alignment alignment) {
        if (!(this.tardis instanceof ServerTardis serverTardis))
            return;

        System.out.print(alignment);
        if (alignment == TardisMood.Alignment.NEGATIVE) {
            this.moodEvent.execute(serverTardis);
            this.updateEvent(null);
        } else if (alignment == TardisMood.Alignment.POSITIVE) {
            this.rollForMoodDictatedEvent();
        } else if (alignment == TardisMood.Alignment.NEUTRAL) {
            if (AITMod.RANDOM.nextInt(0, 10) < 5) {
                this.moodEvent.execute(serverTardis);
                this.updateEvent(null);
            } else {
                this.rollForMoodDictatedEvent();
            }
        }
    }

    private void handlePositiveMood(TardisMood.Moods mood, TardisMood.Alignment alignment) {
        if (!(this.tardis instanceof ServerTardis serverTardis))
            return;

        System.out.print(mood + " | " + alignment);
        if (alignment == TardisMood.Alignment.POSITIVE) {
            this.moodEvent.execute(serverTardis);
            this.updateEvent(null);
        } else if (alignment == TardisMood.Alignment.NEGATIVE) {
            this.rollForMoodDictatedEvent();
        } else if (alignment == TardisMood.Alignment.NEUTRAL) {
            if (mood.swayWeight() == 0 || (mood.swayWeight() < 0 && AITMod.RANDOM.nextInt(0, 10) < 5)) {
                this.moodEvent.execute(serverTardis);
                this.updateEvent(null);
            } else {
                this.rollForMoodDictatedEvent();
            }
        }
    }

    private void handleNeutralMood(MoodDictatedEvent mDE, TardisMood.Alignment alignment,
            TardisMood.Moods winningMood) {
        if (!(this.tardis instanceof ServerTardis serverTardis))
            return;

        //System.out.println(mDE + " | " + alignment + " | " + winningMood);
        if (alignment == TardisMood.Alignment.NEUTRAL) {
            if (winningMood.weight() + winningMood.swayWeight() >= mDE.getCost()) {
                this.moodEvent.execute(serverTardis);
                this.updateEvent(null);
            } else {
                this.rollForMoodDictatedEvent();
            }
        } else {
            this.moodEvent.execute(serverTardis);
            this.updateEvent(null);
        }
    }

    public void updateEvent(@Nullable MoodDictatedEvent moodDictatedEvent) {
        this.moodEvent = moodDictatedEvent;
        this.winningMood = null;
    }

    public void randomizePriorityMoods() {
        TardisMood.Moods[] moods = new TardisMood.Moods[3];

        for (int i = 0; i < 3; i++) {
            moods[i] = TardisMood.Moods.VALUES[AITMod.RANDOM.nextInt(TardisMood.Moods.VALUES.length)];
        }

        priorityMoods = moods;
    }
}
