package dev.amble.ait.core.tardis.handler.travel;

import dev.amble.lib.data.CachedDirectedGlobalPos;
import dev.drtheo.scheduler.api.Scheduler;
import dev.drtheo.scheduler.api.TimeUnit;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import dev.amble.ait.AITMod;
import dev.amble.ait.api.TardisEvents;
import dev.amble.ait.core.AITBlocks;
import dev.amble.ait.core.AITSounds;
import dev.amble.ait.core.blockentities.ExteriorBlockEntity;
import dev.amble.ait.core.blocks.ExteriorBlock;
import dev.amble.ait.core.lock.LockedDimension;
import dev.amble.ait.core.lock.LockedDimensionRegistry;
import dev.amble.ait.core.sounds.travel.TravelSound;
import dev.amble.ait.core.tardis.animation.ExteriorAnimation;
import dev.amble.ait.core.tardis.control.impl.DirectionControl;
import dev.amble.ait.core.tardis.control.impl.SecurityControl;
import dev.amble.ait.core.tardis.handler.BiomeHandler;
import dev.amble.ait.core.tardis.handler.TardisCrashHandler;
import dev.amble.ait.core.tardis.util.NetworkUtil;
import dev.amble.ait.core.tardis.util.TardisUtil;
import dev.amble.ait.core.util.ForcedChunkUtil;
import dev.amble.ait.core.util.WorldUtil;

public final class TravelHandler extends AnimatedTravelHandler implements CrashableTardisTravel {

    private boolean travelCooldown;

    public static final Identifier CANCEL_DEMAT_SOUND = AITMod.id("cancel_demat_sound");

    static {
        TardisEvents.FINISH_FLIGHT.register(tardis -> { // ghost monument
            if (!AITMod.CONFIG.SERVER.GHOST_MONUMENT)
                return TardisEvents.Interaction.PASS;

            TravelHandler travel = tardis.travel();

            return (TardisUtil.isInteriorEmpty(tardis) && !travel.leaveBehind().get()) || travel.autopilot()
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
        });
    }

    public TravelHandler() {
        super(Id.TRAVEL);
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
        if (this.isServer() && context.created())
            this.placeExterior(true);
    }

    public void deleteExterior() {
        CachedDirectedGlobalPos globalPos = this.position.get();

        ServerWorld world = globalPos.getWorld();
        BlockPos pos = globalPos.getPos();

        world.removeBlock(pos, false);

        ForcedChunkUtil.stopForceLoading(world, pos);
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

        BiomeHandler biome = this.tardis.handler(Id.BIOME);
        biome.update(globalPos);

        if (schedule && !this.antigravs.get())
            world.scheduleBlockTick(pos, AITBlocks.EXTERIOR_BLOCK, 2);

        ForcedChunkUtil.keepChunkLoaded(world, pos);
        return exterior;
    }

    private void runAnimations(ExteriorBlockEntity exterior) {
        State state = this.getState();
        ExteriorAnimation animation = exterior.getAnimation();

        if (animation == null) {
            AITMod.LOGGER.info("Null animation for exterior at {}", exterior.getPos());
            return;
        }

        animation.setupAnimation(state);
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

    public void dematerialize(TravelSound sound) {
        if (this.getState() != State.LANDED)
            return;

        if (!this.tardis.fuel().hasPower())
            return;

        if (this.autopilot()) {
            // fulfill all the prerequisites
            this.tardis.door().closeDoors();
            this.tardis.setRefueling(false);

            if (this.speed() == 0)
                this.increaseSpeed();
        }

        if (TardisEvents.DEMAT.invoker().onDemat(this.tardis) == TardisEvents.Interaction.FAIL || this.travelCooldown) {
            this.failDemat();
            return;
        }

        this.forceDemat(sound);
    }
    public void dematerialize() {
        this.dematerialize(null);
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

    public void forceDemat(TravelSound replacementSound) {
        this.state.set(State.DEMAT);

        SoundEvent sound = tardis.stats().getTravelEffects().get(this.getState()).sound();

        // Play dematerialize sound at the position
        this.position().getWorld().playSound(null, this.position().getPos(), sound, SoundCategory.BLOCKS);

        //System.out.println(tardis.stats().getTravelEffects().get(this.getState()).soundId());
        this.tardis.getDesktop().playSoundAtEveryConsole(sound, SoundCategory.BLOCKS, 2f, 1f);
        this.runAnimations();

        this.startFlight();
    }

    public void forceDemat() {
        this.forceDemat(null);
    }

    public void finishDemat() {
        this.crashing.set(false);
        this.previousPosition.set(this.position);
        this.state.set(State.FLIGHT);

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

        NetworkUtil.sendToInterior(this.tardis.asServer(), CANCEL_DEMAT_SOUND, PacketByteBufs.empty());
    }

    public void rematerialize() {
        if (TardisEvents.MAT.invoker().onMat(tardis.asServer()) == TardisEvents.Interaction.FAIL
                || this.travelCooldown) {
            this.failRemat();
            return;
        }

        this.forceRemat();
    }

    public void forceRemat() {
        if (this.getState() != State.FLIGHT)
            return;

        if (this.tardis.sequence().hasActiveSequence())
            this.tardis.sequence().setActiveSequence(null, true);

        CachedDirectedGlobalPos pos = this.getProgress();
        TardisEvents.Result<CachedDirectedGlobalPos> result = TardisEvents.BEFORE_LAND.invoker().onLanded(this.tardis, pos);

        if (result.type() == TardisEvents.Interaction.FAIL) {
            this.crash();
            return;
        }

        pos = result.result().orElse(pos);

        pos = WorldUtil.locateSafe(pos, this.vGroundSearch.get(), this.hGroundSearch.get());

        this.tardis.door().closeDoors();

        this.state.set(State.MAT);
        SoundEvent sound = tardis.stats().getTravelEffects().get(this.getState()).sound();

        if (this.isCrashing())
            sound = AITSounds.EMERG_MAT;

        this.destination(pos);
        this.forcePosition(this.destination());

        // Play materialize sound at the destination
        this.position().getWorld().playSound(null, this.position().getPos(), sound, SoundCategory.BLOCKS);

        this.tardis.getDesktop().playSoundAtEveryConsole(sound, SoundCategory.BLOCKS, 2f, 1f);
        //System.out.println(sound.getId());
        this.placeExterior(true); // we schedule block update in #finishRemat
    }

    public void finishRemat() {
        if (this.autopilot() && this.speed.get() > 0)
            this.speed.set(0);

        this.state.set(State.LANDED);
        this.resetFlight();

        tardis.door().interactLock(tardis.door().previouslyLocked().get(), null, false);
        TardisEvents.LANDED.invoker().onLanded(this.tardis);
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
