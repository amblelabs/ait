package dev.amble.ait.core.tardis.handler;

import java.util.ArrayDeque;
import java.util.Optional;
import java.util.Queue;

import dev.drtheo.queue.api.ActionQueue;

import net.minecraft.entity.Entity;
import net.minecraft.entity.TntEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import dev.amble.ait.api.tardis.KeyedTardisComponent;
import dev.amble.ait.api.tardis.TardisTickable;
import dev.amble.ait.core.AITSounds;
import dev.amble.ait.core.tardis.handler.travel.TravelHandlerBase;
import dev.amble.ait.core.tardis.util.TardisUtil;
import dev.amble.ait.data.Exclude;
import dev.amble.ait.data.Loyalty;
import dev.amble.ait.data.properties.bool.BoolProperty;
import dev.amble.ait.data.properties.bool.BoolValue;

// use this as reference for starting other looping sounds on the exterior
public class ServerAlarmHandler extends KeyedTardisComponent implements TardisTickable {

    @Exclude
    public static final int CLOISTER_LENGTH_TICKS = 3 * 20;

    @Exclude
    private int soundCounter = 0;

    @Exclude
    private Queue<Alarm> alarms;

    @Exclude
    private Alarm currentAlarm;

    private static final BoolProperty ENABLED = new BoolProperty("enabled", false);
    private static final BoolProperty HOSTILE_PRESENCE = new BoolProperty("hostile_presence", true);

    private final BoolValue enabled = ENABLED.create(this);
    private final BoolValue hostilePresence = HOSTILE_PRESENCE.create(this);

    public ServerAlarmHandler() {
        super(Id.ALARMS);
    }

    @Override
    public void onLoaded() {
        enabled.of(this, ENABLED);
        hostilePresence.of(this, HOSTILE_PRESENCE);
    }

    @Override
    public void postInit(InitContext ctx) {
        super.postInit(ctx);

        if (this.isServer()) {
            alarms = new ArrayDeque<>();
        }
    }

    /**
     * @deprecated The {@link #enable()} and {@link #disable()} methods have logic in them and should be used instead.
     * @return The enabled state of the alarm
     */
    @Deprecated(forRemoval = true, since = "1.3.0")
    public BoolValue enabled() {
        return enabled;
    }

    public boolean isEnabled() {
        return this.enabled.get();
    }

    public void enable() {
        this.enabled.set(true);
    }

    public Alarm enable(Alarm cause) {
        tryStart(cause);

        return cause;
    }

    public Alarm enable(Text cause) {
        return enable(() -> Optional.ofNullable(cause));
    }

    public void disable() {
        this.enabled.set(false);

        this.currentAlarm = null;
    }

    public BoolValue hostilePresence() {
        return hostilePresence;
    }

    public void toggle() {
        if (this.enabled.get()) {
            this.disable();
        } else {
            this.enable();
        }
    }

    private boolean isDoorOpen() {
        return tardis.door().isOpen();
    }

    private void tryStart(Alarm alarm) {
        if (currentAlarm == null || alarm.priority() > currentAlarm.priority()) {
            currentAlarm = alarm;
            return;
        }

        alarms.add(alarm);
    }

    @Override
    public void tick(MinecraftServer server) {
        if (server.getTicks() % 20 == 0 && !this.enabled().get() && this.hostilePresence().get()) {
            for (Entity entity : TardisUtil.getEntitiesInInterior(tardis, 200)) {
                if (entity instanceof TntEntity || (entity instanceof HostileEntity && !entity.hasCustomName())
                        || entity instanceof ServerPlayerEntity player
                        && tardis.loyalty().get(player).level() == Loyalty.Type.REJECT.level) {
                    tardis.alarm().enable();
                }
            }

            return;
        }

        if (!this.enabled().get()) {
            if (this.currentAlarm != null) {
                this.enable();
            }

            return;
        }
        if (tardis.travel().getState() == TravelHandlerBase.State.FLIGHT)
            return;

        soundCounter++;

        if (soundCounter >= CLOISTER_LENGTH_TICKS) {
            soundCounter = 0;

            float volume = isDoorOpen() ? 1.0f : 0.3f;
            float pitch = isDoorOpen() ? 1f : 0.2f;

            tardis.travel().position().getWorld().playSound(null, tardis.travel().position().getPos(),
                    AITSounds.CLOISTER, SoundCategory.AMBIENT, volume, pitch);

            if (currentAlarm != null) {
                tardis.asServer().world().getPlayers().forEach(player -> {
                    currentAlarm.sendMessage(player);
                });
            }
        }

        if (currentAlarm != null && currentAlarm.tick()) {
            currentAlarm = null;

            if (!alarms.isEmpty()) {
                currentAlarm = alarms.poll();
            }
        }
    }

    public interface Alarm {
        default boolean tick() {
            return false;
        }

        default int priority() {
            return 0;
        }

        default void sendMessage(ServerPlayerEntity player) {
            getAlarmText().ifPresent(text -> player.sendMessage(text, true));
        }

        Optional<Text> getAlarmText();
    }

    public static class Countdown implements Alarm {
        private final String translation;
        private final ActionQueue onFinished;
        private int ticks;

        public Countdown(String translation, int ticks) {
            this.translation = translation;
            this.ticks = ticks;

            this.onFinished = new ActionQueue();
        }

        @Override
        public int priority() {
            return 1;
        }

        public ActionQueue onFinished() {
            return onFinished;
        }

        public Countdown thenRun(Runnable action) {
            this.onFinished.thenRun(action);
            return this;
        }

        public Countdown thenRun(ActionQueue action) {
            this.onFinished.thenRun(action);
            return this;
        }

        public boolean tick() {
            if (ticks > 0) {
                ticks--;
                return false;
            }

            onFinished.execute();
            return true;
        }

        @Override
        public Optional<Text> getAlarmText() {
            if (translation == null) return Optional.empty();

            return Optional.of(Text.translatable(this.translation, Math.ceil(this.ticks / 20F)).formatted(Formatting.RED));
        }

        public static class Builder {
            private String translation;
            private int ticks;

            /**
             * @param translation The translation key of the message to send every 1 seconds
             * @return The builder instance
             */
            public Builder message(String translation) {
                this.translation = translation;
                return this;
            }

            /**
             * Sets the countdown ticks
             * @param ticks The number of ticks to countdown for
             * @return The builder instance
             */
            public Builder ticks(int ticks) {
                if (ticks <= 0) {
                    throw new IllegalArgumentException("Ticks must be greater than 0");
                }

                this.ticks = ticks;
                return this;
            }

            /**
             * Sets the countdown to the number of ticks in a bell toll
             * @param count The number of bell tolls to countdown for
             * @return The builder instance
             */
            public Builder bellTolls(int count) {
                if (count <= 0) {
                    throw new IllegalArgumentException("Bell tolls must be greater than 0");
                }

                return this.ticks((count * CLOISTER_LENGTH_TICKS));
            }

            /**
             * Builds the countdown and then adds an action to be ran on its completion
             * @param action The action to run when the countdown is finished
             * @return The created countdown instance
             */
            public Countdown thenRun(Runnable action) {
                return this.build().thenRun(action);
            }

            /**
             * Builds the countdown and then adds an action to be ran on its completion
             * @param action The action to run when the countdown is finished
             * @return The created countdown instance
             */
            public Countdown thenRun(ActionQueue action) {
                return this.build().thenRun(action);
            }

            /**
             * Builds the countdown instance
             * @return The created countdown instance
             */
            public Countdown build() {
                return new Countdown(translation, ticks);
            }
        }
    }

    public enum AlarmType implements Alarm {
        CRASHING,
        HAIL_MARY("tardis.message.protocol_813.travel");

        private final String translation;

        AlarmType() {
            this.translation = "tardis.message.alarm." + this.name().toLowerCase();
        }

        AlarmType(String translation) {
            this.translation = translation;
        }

        @Override
        public Optional<Text> getAlarmText() {
            return Optional.of(Text.translatable(this.translation).formatted(Formatting.RED));
        }
    }
}
