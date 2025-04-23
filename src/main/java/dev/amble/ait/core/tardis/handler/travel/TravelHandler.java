package dev.amble.ait.core.tardis.handler.travel;

import java.util.EnumMap;
import java.util.Optional;

import dev.amble.lib.data.CachedDirectedGlobalPos;
import dev.drtheo.queue.api.ActionQueue;
import dev.drtheo.scheduler.api.Scheduler;
import dev.drtheo.scheduler.api.TimeUnit;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.loader.api.FabricLoader;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import dev.amble.ait.AITMod;
import dev.amble.ait.api.tardis.TardisEvents;
import dev.amble.ait.client.tardis.ClientTardis;
import dev.amble.ait.client.util.ClientTardisUtil;
import dev.amble.ait.core.AITBlocks;
import dev.amble.ait.core.AITSounds;
import dev.amble.ait.core.blockentities.ExteriorBlockEntity;
import dev.amble.ait.core.blocks.ExteriorBlock;
import dev.amble.ait.core.lock.LockedDimension;
import dev.amble.ait.core.lock.LockedDimensionRegistry;
import dev.amble.ait.core.sounds.travel.TravelSound;
import dev.amble.ait.core.tardis.control.impl.DirectionControl;
import dev.amble.ait.core.tardis.control.impl.SecurityControl;
import dev.amble.ait.core.tardis.handler.TardisCrashHandler;
import dev.amble.ait.core.tardis.util.NetworkUtil;
import dev.amble.ait.core.tardis.util.TardisUtil;
import dev.amble.ait.core.util.SafePosSearch;
import dev.amble.ait.core.util.WorldUtil;
import dev.amble.ait.core.world.RiftChunkManager;
import dev.amble.ait.data.Exclude;

public final class TravelHandler extends AnimatedTravelHandler implements CrashableTardisTravel {

    @Exclude
    private boolean travelCooldown;

    @Exclude
    private boolean waiting;

    @Exclude
    private EnumMap<State, ActionQueue> travelQueue;

    public static final Identifier CANCEL_DEMAT_SOUND = AITMod.id("cancel_demat_sound");

    static {
        TardisEvents.FINISH_FLIGHT.register(tardis -> { // ghost monument
            if (!AITMod.CONFIG.SERVER.GHOST_MONUMENT)
                return TardisEvents.Interaction.PASS;

            TravelHandler travel = tardis.travel();

            return (TardisUtil.isInteriorEmpty(tardis) && !travel.leaveBehind().get()) || travel.autopilot() || travel.speed() == 0
                    ? TardisEvents.Interaction.SUCCESS : TardisEvents.Interaction.PASS;
        });

        TardisEvents.MAT.register(tardis -> { // end check - wait, shouldn't this be done in the other locked method? this confuses me
            if (!AITMod.CONFIG.SERVER.LOCK_DIMENSIONS)
                return TardisEvents.Interaction.PASS;

            boolean isEnd = tardis.travel().destination().getDimension().equals(World.END);
            if (!isEnd) return TardisEvents.Interaction.PASS;

            return WorldUtil.isEndDragonDead() ? TardisEvents.Interaction.PASS : TardisEvents.Interaction.FAIL;
        });

        TardisEvents.MAT.register(tardis -> {
            if (!AITMod.CONFIG.SERVER.LOCK_DIMENSIONS)
                return TardisEvents.Interaction.PASS;

            LockedDimension dim = LockedDimensionRegistry.getInstance().get(tardis.travel().destination().getWorld());
            boolean success = dim == null || tardis.isUnlocked(dim);

            if (!success) return TardisEvents.Interaction.FAIL;

            return TardisEvents.Interaction.PASS;
        });

        TardisEvents.LANDED.register(tardis -> {
            if (AITMod.CONFIG.SERVER.GHOST_MONUMENT) {
                tardis.travel().tryFly();
            }
            if (tardis.travel().autopilot())
                tardis.getDesktop().playSoundAtEveryConsole(AITSounds.NAV_NOTIFICATION, SoundCategory.BLOCKS, 2f, 1f);
            if (RiftChunkManager.isRiftChunk(tardis.travel().position())) {
                TardisUtil.sendMessageToInterior(tardis.asServer(), Text.translatable("riftchunk.ait.found").formatted(Formatting.YELLOW, Formatting.ITALIC));
                tardis.getDesktop().playSoundAtEveryConsole(AITSounds.BWEEP, SoundCategory.BLOCKS, 2f, 1f);
            }
            if (tardis.travel().isCrashing())
                tardis.travel().setCrashing(false);
        });

        if (EnvType.CLIENT == FabricLoader.getInstance().getEnvironmentType()) initializeClient();
    }

    @Environment(EnvType.CLIENT)
    private static void initializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(TravelHandler.CANCEL_DEMAT_SOUND, (client, handler, buf,
                                                                                       responseSender) -> {
            ClientTardis tardis = ClientTardisUtil.getCurrentTardis();

            if (tardis == null)
                return;

            client.getSoundManager().stopSounds(tardis.travel().getAnimationIdFor(TravelHandlerBase.State.DEMAT), SoundCategory.BLOCKS);
        });
    }

    public TravelHandler() {
        super(Id.TRAVEL);
    }

    @Override
    public boolean shouldTickAnimation() {
        return !this.waiting && this.getState().animated();
    }

    @Override
    public void speed(int value) {
        super.speed(value);
        this.tryFly();
    }

    @Override
    public void handbrake(boolean value) {
        super.handbrake(value);

        if (this.getState() == TravelHandlerBase.State.DEMAT && value) {
            this.cancelDemat();
            return;
        }

        this.tryFly();
    }

    private void tryFly() {
        int speed = this.speed();

        if (speed > 0 && this.getState() == State.LANDED && !this.handbrake()
                && this.tardis.sonic().getExteriorSonic() == null) {
            this.dematerialize();
            return;
        }

        if (speed != 0 || this.getState() != State.FLIGHT || this.tardis.flight().isFlying())
            return;

        if (this.tardis.crash().getState() == TardisCrashHandler.State.UNSTABLE)
            this.forceDestination(cached -> TravelUtil.jukePos(cached, 1, 10));

        this.rematerialize();
    }

    @Override
    protected void onEarlyInit(InitContext ctx) {
        if (ctx.created() && ctx.pos() != null)
            this.initPos(ctx.pos());
    }

    @Override
    public void postInit(InitContext context) {
        super.postInit(context);

        if (this.isServer() && context.created())
            this.placeExterior(true);
    }

    public void deleteExterior() {
        CachedDirectedGlobalPos globalPos = this.position.get();

        ServerWorld world = globalPos.getWorld();
        BlockPos pos = globalPos.getPos();

        world.removeBlock(pos, false);
    }

    /**
     * Places an exterior, animates it if `animate` is true and schedules a block
     * update.
     */
    public ExteriorBlockEntity placeExterior(boolean animate) {
        return placeExterior(animate, true);
    }

    public ExteriorBlockEntity placeExterior(boolean animate, boolean schedule) {
        return placeExterior(this.position(), animate, schedule);
    }

    private ExteriorBlockEntity placeExterior(CachedDirectedGlobalPos globalPos, boolean animate, boolean schedule) {
        ServerWorld world = globalPos.getWorld();
        BlockPos pos = globalPos.getPos();

        boolean hasPower = this.tardis.fuel().hasPower();

        BlockState blockState = AITBlocks.EXTERIOR_BLOCK.getDefaultState()
                .with(ExteriorBlock.ROTATION, (int) DirectionControl.getGeneralizedRotation(globalPos.getRotation()))
                .with(ExteriorBlock.LEVEL_4, hasPower ? 4 : 0);

        world.setBlockState(pos, blockState);

        ExteriorBlockEntity exterior = new ExteriorBlockEntity(pos, blockState, this.tardis);
        world.addBlockEntity(exterior);

        if (animate)
            this.runAnimations(exterior);

        if (schedule && !this.antigravs.get())
            world.scheduleBlockTick(pos, AITBlocks.EXTERIOR_BLOCK, 2);

        return exterior;
    }

    private void runAnimations(ExteriorBlockEntity exterior) {
        State state = this.getState();
        this.getAnimations().onStateChange(state);
    }

    public void runAnimations() {
        CachedDirectedGlobalPos globalPos = this.position();

        ServerWorld level = globalPos.getWorld();
        BlockEntity blockEntity = level.getBlockEntity(globalPos.getPos());

        if (blockEntity instanceof ExteriorBlockEntity exterior)
            this.runAnimations(exterior);
    }

    /**
     * Sets the current position to the destination progress one.
     */
    public void stopHere() {
        if (this.getState() != State.FLIGHT)
            return;

        this.forcePosition(this.getProgress());
    }

    private void createCooldown() {
        this.travelCooldown = true;

        Scheduler.get().runTaskLater(() -> this.travelCooldown = false, TimeUnit.SECONDS, 5);
    }

    public Optional<ActionQueue> dematerialize(TravelSound sound) {
        if (this.getState() != State.LANDED)
            return Optional.empty();

        if (!this.tardis.fuel().hasPower())
            return Optional.empty();

        if (this.autopilot()) {
            // fulfill all the prerequisites
            this.tardis.door().closeDoors();
            this.tardis.setRefueling(false);

            if (this.speed() == 0)
                this.increaseSpeed();
        }

        if (TardisEvents.DEMAT.invoker().onDemat(this.tardis) == TardisEvents.Interaction.FAIL || this.travelCooldown) {
            this.failDemat();
            return Optional.empty();
        }

        return Optional.of(this.forceDemat(sound));
    }

    public Optional<ActionQueue> dematerialize() {
        return this.dematerialize(null);
    }

    private void failDemat() {
        // demat will be cancelled
        this.position().getWorld().playSound(null, this.position().getPos(), AITSounds.FAIL_DEMAT, SoundCategory.BLOCKS,
                2f, 1f);

        this.tardis.getDesktop().playSoundAtEveryConsole(AITSounds.FAIL_DEMAT, SoundCategory.BLOCKS, 2f, 1f);
        this.createCooldown();
    }

    private void failRemat() {
        // Play failure sound at the current position
        this.position().getWorld().playSound(null, this.position().getPos(), AITSounds.FAIL_MAT, SoundCategory.BLOCKS,
                2f, 1f);

        // Play failure sound at the Tardis console position if the interior is not
        // empty
        this.tardis.getDesktop().playSoundAtEveryConsole(AITSounds.FAIL_MAT, SoundCategory.BLOCKS, 2f, 1f);

        // Create materialization delay and return
        this.createCooldown();
    }

    public ActionQueue forceDemat(TravelSound replacementSound) {
        this.setState(State.DEMAT);

        SoundEvent sound = this.getAnimationFor(this.getState()).getSound();
        this.tardis.getDesktop().playSoundAtEveryConsole(sound, SoundCategory.BLOCKS, 2f, 1f);

        this.runAnimations();

        this.startFlight();

        return this.queueFor(State.FLIGHT);
    }

    public void forceDemat() {
        this.forceDemat(null);
    }

    public void finishDemat() {
        this.crashing.set(false);
        this.previousPosition.set(this.position);
        this.setState(State.FLIGHT);

        TardisEvents.ENTER_FLIGHT.invoker().onFlight(this.tardis);
        this.deleteExterior();

        if (tardis.stats().security().get())
            SecurityControl.runSecurityProtocols(this.tardis);
    }

    public void cancelDemat() {
        if (this.getState() != State.DEMAT)
            return;

        this.finishRemat();

        this.position().getWorld().playSound(null, this.position().getPos(), AITSounds.LAND_CRASH,
                SoundCategory.AMBIENT);
        this.tardis.getDesktop().playSoundAtEveryConsole(AITSounds.ABORT_FLIGHT, SoundCategory.AMBIENT);

        // TODO - cancel for subscribed players instead
        NetworkUtil.sendToInterior(this.tardis.asServer(), CANCEL_DEMAT_SOUND, PacketByteBufs.empty());
    }

    public Optional<ActionQueue> rematerialize() {
        if (TardisEvents.MAT.invoker().onMat(tardis.asServer()) == TardisEvents.Interaction.FAIL
                || this.travelCooldown) {
            this.failRemat();
            return Optional.empty();
        }

        return this.forceRemat();
    }

    public Optional<ActionQueue> forceRemat() {
        if (this.getState() != State.FLIGHT)
            return Optional.empty();

        if (this.tardis.sequence().hasActiveSequence())
            this.tardis.sequence().setActiveSequence(null, true);

        CachedDirectedGlobalPos initialPos = this.getProgress();
        TardisEvents.Result<CachedDirectedGlobalPos> result = TardisEvents.BEFORE_LAND.invoker()
                .onLanded(this.tardis, initialPos);

        if (result.type() == TardisEvents.Interaction.FAIL) {
            this.crash();
            return Optional.of(this.queueFor(State.LANDED));
        }

        final CachedDirectedGlobalPos finalPos = result.value().orElse(initialPos);

        this.setState(State.MAT);
        this.waiting = true;

        SafePosSearch.wrapSafe(finalPos, this.vGroundSearch.get(),
                this.hGroundSearch.get(), this::finishForceRemat);

        return Optional.of(this.queueFor(State.LANDED));
    }

    private void finishForceRemat(CachedDirectedGlobalPos pos) {
        this.waiting = false;
        this.tardis.door().closeDoors();

        SoundEvent sound = this.getAnimationFor(this.getState()).getSound();

        if (this.isCrashing())
            sound = AITSounds.EMERG_MAT;

        this.tardis.getDesktop().playSoundAtEveryConsole(sound, SoundCategory.BLOCKS, 2f, 1f);

        this.destination(pos);
        this.forcePosition(this.destination());

        this.placeExterior(true); // we schedule block update in #finishRemat
    }

    public void finishRemat() {
        if (this.autopilot() && this.speed.get() > 0)
            this.speed.set(0);

        this.setState(State.LANDED);
        this.resetFlight();

        tardis.door().interactLock(tardis.door().previouslyLocked().get(), null, false);
        TardisEvents.LANDED.invoker().onLanded(this.tardis);
    }

    private void executeQueue(State state) {
        if (this.travelQueue == null)
            this.travelQueue = new EnumMap<>(State.class);

        ActionQueue queue = this.travelQueue.computeIfAbsent(state, k -> new ActionQueue());

        queue.execute();
    }

    /**
     * Returns the queue of actions to be ran when the TARDIS next reaches a specific state
     * Please avoid calling "execute" or "finish" directly.
     * @param state the state to enqueue the action for
     * @return the action queue for the state
     */
    public ActionQueue queueFor(State state) {
        if (this.travelQueue == null)
            this.travelQueue = new EnumMap<>(State.class);

        return this.travelQueue.computeIfAbsent(state, k -> new ActionQueue());
    }

    @Override
    protected void setState(State state) {
        super.setState(state);

        this.executeQueue(state);
    }

    public void initPos(CachedDirectedGlobalPos cached) {
        cached.init(TravelHandlerBase.server());

        if (this.position.get() == null)
            this.position.set(cached);

        if (this.destination.get() == null)
            this.destination.set(cached);

        if (this.previousPosition.get() == null)
            this.previousPosition.set(cached);
    }

    public boolean isLanded() {
        return this.getState() == State.LANDED;
    }

    public boolean inFlight() {
        return this.getState() == State.FLIGHT;
    }
}
