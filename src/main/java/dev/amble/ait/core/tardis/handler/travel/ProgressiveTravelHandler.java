package dev.amble.ait.core.tardis.handler.travel;

import dev.amble.ait.AITMod;
import dev.amble.ait.api.tardis.TardisEvents;
import dev.amble.ait.core.AITSounds;
import dev.amble.ait.core.tardis.Tardis;
import dev.amble.ait.core.tardis.control.sequences.SequenceHandler;
import dev.amble.ait.data.properties.bool.BoolProperty;
import dev.amble.ait.data.properties.bool.BoolValue;
import dev.amble.ait.data.properties.integer.IntProperty;
import dev.amble.ait.data.properties.integer.IntValue;
import dev.amble.lib.data.CachedDirectedGlobalPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;

public abstract class ProgressiveTravelHandler extends TravelHandlerBase {

    private static final Random random = Random.create();

    private static final IntProperty FLIGHT_TICKS = new IntProperty("flight_ticks");
    private static final IntProperty TARGET_TICKS = new IntProperty("target_ticks");

    private static final BoolProperty HANDBRAKE = new BoolProperty("handbrake", true);
    private static final BoolProperty AUTOPILOT = new BoolProperty("autopilot", false);

    private final IntValue flightTicks = FLIGHT_TICKS.create(this);
    private final IntValue targetTicks = TARGET_TICKS.create(this);

    protected final BoolValue handbrake = HANDBRAKE.create(this);
    protected final BoolValue autopilot = AUTOPILOT.create(this);

    public ProgressiveTravelHandler(Id id) {
        super(id);
    }

    @Override
    public void onLoaded() {
        super.onLoaded();

        flightTicks.of(this, FLIGHT_TICKS);
        targetTicks.of(this, TARGET_TICKS);

        handbrake.of(this, HANDBRAKE);
        autopilot.of(this, AUTOPILOT);
    }

    private boolean isInFlight() {
        return this.getState() == State.FLIGHT || this.getState() == State.MAT;
    }

    private boolean isFlightTicking() {
        return this.tardis.travel().getState() == State.FLIGHT && this.getTargetTicks() != 0;
    }

    public boolean hasFinishedFlight() {
        return (this.getFlightTicks() >= this.getTargetTicks() || this.getTargetTicks() == 0
                || tardis.travel().isCrashing());
    }

    @Override
    public void forceDestination(CachedDirectedGlobalPos cached) {
        super.forceDestination(cached);
        this.recalculate();
    }

    public void increaseFlightTime(int ticks) {
        this.setTargetTicks(this.getTargetTicks() + ticks);
    }

    public void decreaseFlightTime(int ticks) {
        this.setTargetTicks(this.getTargetTicks() - ticks);
    }

    public int getDurationAsPercentage() {
        if (this.getTargetTicks() == 0 || this.getFlightTicks() == 0)
            return this.getState() == TravelHandlerBase.State.DEMAT ? 0 : 100;

        int target = this.getTargetTicks();
        int flightTicksClamped = MathHelper.clamp(this.getFlightTicks(), 1, target);
        int percentage = (flightTicksClamped * 100) / target;

        return Math.max(0, percentage);
    }

    public CachedDirectedGlobalPos getProgress() {
        return TravelUtil.getPositionFromPercentage(this.position(), this.destination(),
                this.getDurationAsPercentage());
    }

    public void recalculate() {
        this.setTargetTicks(TravelUtil.getFlightDuration(this.position(), this.destination()));
        this.setFlightTicks(this.isInFlight() ? MathHelper.clamp(this.getFlightTicks(), 0, this.getTargetTicks()) : 0);
    }

    protected void startFlight() {
        this.setFlightTicks(0);
        this.setTargetTicks(TravelUtil.getFlightDuration(this.position(), this.destination()));
    }

    protected void resetFlight() {
        this.setFlightTicks(0);
        this.setTargetTicks(0);
    }

    public int getFlightTicks() {
        return this.flightTicks.get();
    }

    public void setFlightTicks(int ticks) {
        this.flightTicks.set(ticks);
    }

    public int getTargetTicks() {
        return this.targetTicks.get();
    }

    protected void setTargetTicks(int ticks) {
        this.targetTicks.set(ticks);
    }

    public void handbrake(boolean value) {
        handbrake.set(value);
    }

    public boolean handbrake() {
        return handbrake.get();
    }

    public boolean autopilot() {
        return autopilot.get();
    }

    public void autopilot(boolean value) {
        this.autopilot.set(value);

        int speed = this.speed.get();
        int expectedSpeed = this.clampSpeed(speed);

        if (expectedSpeed != speed)
            this.speed.set(expectedSpeed);
    }

    public void increaseSpeed() {
        this.speed(this.speed.get() + 1);
    }

    public void decreaseSpeed() {
        if (this.getState() == State.LANDED && this.speed.get() == 1)
            this.tardis.getDesktop().playSoundAtEveryConsole(AITSounds.THROTTLE_SNAP, SoundCategory.AMBIENT);

        this.speed(this.speed.get() - 1);
    }

    @Override
    protected int clampSpeed(int value) {
        int max = this.autopilot() ? 1 : this.maxSpeed.get();
        if (!this.tardis.subsystems().stabilisers().isEnabled()) max = 3;

        return MathHelper.clamp(value, 0, max);
    }

    @Override
    public void tick(MinecraftServer server) {
        super.tick(server);

        if ((this.getTargetTicks() > 0 || this.getFlightTicks() > 0)
                && this.getState() == TravelHandlerBase.State.LANDED)
            this.recalculate();

        if (this.isInFlight() && !this.isCrashing() && this.getTargetTicks() == 0
                && this.getFlightTicks() < this.getTargetTicks())
            this.recalculate();

        if (server.getTicks() % 2 == 0 && !this.tardis().flight().isFlying())
            this.triggerSequencingDuringFlight(tardis);

        if (!this.isFlightTicking())
            return;

        if (this.hasFinishedFlight()) {
            this.tardis.getDesktop().playSoundAtEveryConsole(SoundEvents.BLOCK_BELL_RESONATE);
            this.resetFlight();

            boolean shouldRemat = TardisEvents.FINISH_FLIGHT.invoker().onFinish(tardis.asServer()) == TardisEvents.Interaction.SUCCESS;

            if (shouldRemat)
                this.tardis.travel().rematerialize();

            return;
        }

        if (server.getTicks() % (this.maxSpeed.get() - this.speed() + 1) == 0)
            this.setFlightTicks(this.getFlightTicks() + AITMod.CONFIG.travelPerTick);
    }

    public void triggerSequencingDuringFlight(Tardis tardis) {
        SequenceHandler sequences = tardis.sequence();

        if (!this.autopilot.get() && this.getDurationAsPercentage() < 100
                && this.getState() == TravelHandlerBase.State.FLIGHT && !sequences.hasActiveSequence()
                && !this.position().equals(this.destination()) && this.getTargetTicks() > 100
                && random.nextBetween(0, 110 /*230*/ / (this.speed() == 0 ? 1 : this.speed())) == 7) {
            sequences.triggerRandomSequence(true);
        }
    }
}
