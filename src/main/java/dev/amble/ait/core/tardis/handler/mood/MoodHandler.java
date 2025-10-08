package dev.amble.ait.core.tardis.handler.mood;

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
import dev.amble.ait.data.Exclude;
import dev.amble.ait.registry.impl.MoodEventPoolRegistry;

public class MoodHandler extends TardisComponent implements TardisTickable {

    @Exclude
    public TardisMood.Moods[] priorityMoods;

    @Exclude
    private MoodDictatedEvent moodEvent;

    @Exclude
    private TardisMood winningMood;

    public MoodHandler() {
        super(Id.MOOD);
    }

    @Override
    public void onCreate() {
        if (this.isServer())
            this.randomizePriorityMoods();
    }

    @Override
    public void onLoaded() {
        if (this.isServer())
            this.randomizePriorityMoods();
    }

    @Override
    public void tick(MinecraftServer server) {
        if (this.moodEvent == null || this.winningMood == null) {
            if (server.getTicks() % 20 == 0)
                System.out.println("mood event: " + moodEvent + " / winning: " + winningMood);
            return;
        }

        TardisMood.Alignment moodAlignment = this.moodEvent.getMoodTypeCompatibility();

        if (matchesMood(this.winningMood.alignment(), moodAlignment)) {
            if (this.winningMood.weight() >= this.moodEvent.getCost()) {
                switch (this.winningMood.alignment()) {
                    case NEGATIVE -> handleNegativeMood(moodAlignment);
                    case POSITIVE -> handlePositiveMood(this.winningMood.moods(), moodAlignment);
                    case NEUTRAL -> handleNeutralMood(moodEvent, moodAlignment, this.winningMood.moods());
                }
            }
        } else {
            this.winningMood = null;
        }
    }

    private boolean matchesMood(TardisMood.Alignment winningMoodAlignment, TardisMood.Alignment moodAlignment) {
        return winningMoodAlignment == TardisMood.Alignment.NEUTRAL || winningMoodAlignment == moodAlignment;
    }

    public void rollForMoodDictatedEvent() {
        System.out.println("rolling for mood");
        int rand = AITMod.RANDOM.nextInt(0, MoodEventPoolRegistry.REGISTRY.size());
        MoodDictatedEvent moodEvent = MoodEventPoolRegistry.REGISTRY.get(rand);

        if (moodEvent == null)
            return;

        this.moodEvent = moodEvent;

        if (this.tardis.asServer().hasWorld()) {
            this.tardis.asServer().world().getPlayers().forEach(player -> player
                    .sendMessage(Text.literal(this.moodEvent.id().getPath()).formatted(Formatting.BOLD), true));
        }

        this.raceMoods();
    }

    public void raceMoods() {
        TardisMood.Moods[] moods = priorityMoods.length == 0 ? TardisMood.Moods.VALUES : priorityMoods;
        int[] weights = new int[moods.length];

        int winIndex = 0;

        for (int i = 0; i < weights.length; i++) {
            int weight = 8 + (AITMod.RANDOM.nextInt(0, 11) * 8);
            weights[i] = Math.min(weight, 256);

            if (weight > weights[winIndex]) {
                winIndex = i;
            }
        }

        TardisMood.Moods key = moods[winIndex];

        this.winningMood = TardisMood.fromMoods(key, weights[winIndex]);
        this.tardis.getDesktop().playSoundAtEveryConsole(SoundEvents.BLOCK_AMETHYST_BLOCK_CHIME, SoundCategory.BLOCKS);
    }

    private void handleNegativeMood(TardisMood.Alignment alignment) {
        if (!(this.tardis instanceof ServerTardis serverTardis))
            return;

        if (alignment == TardisMood.Alignment.POSITIVE) {
            this.rollForMoodDictatedEvent();
            return;
        }

        if (alignment == TardisMood.Alignment.NEUTRAL || AITMod.RANDOM.nextInt(0, 15) < 5) {
            this.moodEvent.execute(serverTardis);
            this.updateEvent(null);
            return;
        }

        this.rollForMoodDictatedEvent();
    }

    private void handlePositiveMood(TardisMood.Moods mood, TardisMood.Alignment alignment) {
        if (!(this.tardis instanceof ServerTardis serverTardis))
            return;

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
