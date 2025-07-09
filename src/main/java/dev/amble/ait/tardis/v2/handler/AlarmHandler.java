package dev.amble.ait.tardis.v2.handler;

import dev.amble.ait.api.tardis.v2.handler.THandler;
import dev.amble.ait.tardis.v2.Tardis;
import dev.amble.ait.tardis.v2.data.AlarmData;
import dev.amble.ait.tardis.v2.data.RepairData;
import dev.amble.ait.tardis.v2.data.TravelData;
import dev.amble.ait.tardis.v2.event.ServerEvents;
import dev.amble.ait.tardis.v2.event.TardisEvents;
import dev.drtheo.queue.api.ActionQueue;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.Optional;

public class AlarmHandler implements THandler, ServerEvents, TardisEvents {

    @Override
    public void event$repaired(Tardis tardis) {
        this.disable(tardis);
    }

    public Alarm enable(Tardis tardis, Alarm cause) {
        tryStart(cause);

        return cause;
    }

    public Alarm enable(Tardis tardis, Text cause) {
        return enable(tardis, () -> Optional.ofNullable(cause));
    }

    public void disable(Tardis tardis) {
        this.enabled.set(false);

        this.currentAlarm = null;
    }

    public void toggle(Tardis tardis) {
        if (this.enabled.get()) {
            this.disable();
        } else {
            this.enable();
        }
    }

    private void tryStart(Alarm alarm) {
        if (currentAlarm == null || alarm.priority() > currentAlarm.priority()) {
            currentAlarm = alarm;
            return;
        }

        alarms.add(alarm);
    }

    @Override
    public void event$crash(Tardis tardis, int power) {
        this.enable(tardis, AlarmType.CRASHING);
    }

    @Override
    public void event$tardisTick(Tardis tardis, MinecraftServer server) {
        if (!this.enabled().get()) {
            if (this.currentAlarm != null) {
                this.enable();
            }

            return;
        }

        if (tardis.ifHasOrElse(TravelData.ID, t ->
                t.state().get() == TravelData.State.FLIGHT, false))
            return;

        soundCounter++;

        if (soundCounter >= CLOISTER_LENGTH_TICKS) {
            soundCounter = 0;

            this.handle(TardisEvents.alarmToll(tardis, currentAlarm));
        }

        if (currentAlarm != null && currentAlarm.tick()) {
            currentAlarm = null;

            if (!alarms.isEmpty()) {
                currentAlarm = alarms.poll();
            }
        }
    }

    @Override
    public void event$repairTick(Tardis tardis, MinecraftServer server, RepairData repair) {
        tardis.resolve(AlarmData.ID).enabled().set(true);
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
            public Countdown.Builder message(String translation) {
                this.translation = translation;
                return this;
            }

            /**
             * Sets the countdown ticks
             * @param ticks The number of ticks to countdown for
             * @return The builder instance
             */
            public Countdown.Builder ticks(int ticks) {
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
            public Countdown.Builder bellTolls(int count) {
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
