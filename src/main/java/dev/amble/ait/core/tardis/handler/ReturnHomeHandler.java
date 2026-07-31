package dev.amble.ait.core.tardis.handler;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import dev.amble.ait.AITMod;
import dev.amble.ait.api.tardis.KeyedTardisComponent;
import dev.amble.ait.api.tardis.TardisEvents;
import dev.amble.ait.api.tardis.TardisTickable;
import dev.amble.ait.core.AITTags;
import dev.amble.ait.core.engine.impl.EmergencyPower;
import dev.amble.ait.core.item.SiegeInventoryUtil;
import dev.amble.ait.core.item.SiegeTardisItem;
import dev.amble.ait.core.lock.LockedDimensionRegistry;
import dev.amble.ait.core.tardis.control.impl.EngineOverloadControl;
import dev.amble.ait.core.tardis.handler.travel.TravelHandler;
import dev.amble.ait.core.tardis.handler.travel.TravelHandlerBase;
import dev.amble.ait.core.tardis.handler.travel.TravelUtil;
import dev.amble.ait.core.tardis.manager.ServerTardisManager;
import dev.amble.ait.core.tardis.util.TardisUtil;
import dev.amble.ait.core.util.SafePosSearch;
import dev.amble.ait.core.world.TardisServerWorld;
import dev.amble.ait.data.Exclude;
import dev.amble.ait.data.Loyalty;
import dev.amble.ait.data.properties.Property;
import dev.amble.ait.data.properties.Value;
import dev.amble.ait.data.properties.bool.BoolProperty;
import dev.amble.ait.data.properties.bool.BoolValue;
import dev.amble.ait.data.properties.dbl.DoubleProperty;
import dev.amble.ait.data.properties.dbl.DoubleValue;
import dev.amble.lib.data.CachedDirectedGlobalPos;
import dev.amble.lib.util.ServerLifecycleHooks;
import org.jetbrains.annotations.Nullable;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.inventory.Inventory;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ChunkTicketType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

/**
 * Coordinates automatic safety actions which return a TARDIS to its configured
 * home. Long-running state lives here so it survives world saves and reloads.
 */
public class ReturnHomeHandler extends KeyedTardisComponent implements TardisTickable {
    private static final Identifier ZWIP_DEMAT = AITMod.id("zwip_demat");
    private static final Identifier ZWIP_MAT = AITMod.id("zwip_mat");
    private static final long SIEGE_LOCATOR_TRANSFER_GRACE_TICKS = 220L;
    private static final long SIEGE_CONFLICT_VALIDATION_COOLDOWN_TICKS = 20L;
    private static final int MAX_SIEGE_ITEM_EXTRA_LOCATORS = 256;
    private static final int BOSS_TICKET_RADIUS = 0;
    private static final ChunkTicketType<String> BOSS_ESCAPE_TICKET = ChunkTicketType.<String>create(
            "ait_boss_escape", Comparator.naturalOrder());

    private static final Property<AutomaticTravel> AUTOMATIC_TRAVEL = Property.forEnum(
            "automatic_travel", AutomaticTravel.class, AutomaticTravel.NONE);
    private static final BoolProperty AUTOMATIC_SETUP_COMPLETE = new BoolProperty("automatic_setup_complete", false);
    private static final BoolProperty AUTOMATIC_PREVIOUS_AUTOPILOT = new BoolProperty(
            "automatic_previous_autopilot", false);
    private static final BoolProperty HOME_LANDING = new BoolProperty("home_landing", false);
    private static final BoolProperty SHUTDOWN_ON_ARRIVAL = new BoolProperty("shutdown_on_arrival", false);
    private static final BoolProperty CRASH_ON_ARRIVAL = new BoolProperty("crash_on_arrival", false);
    private static final DoubleProperty DEFERRED_FLIGHT_COST = new DoubleProperty("deferred_flight_cost", 0);
    private static final BoolProperty PENDING_FUEL_RETURN = new BoolProperty("pending_fuel_return", false);
    private static final BoolProperty WAITING_FOR_REFUEL = new BoolProperty("waiting_for_refuel", false);
    private static final BoolProperty HAIL_MARY_PENDING_LANDING = new BoolProperty("hail_mary_pending_landing", false);
    private static final BoolProperty HAIL_MARY_AWAITING_ENTRY = new BoolProperty("hail_mary_awaiting_entry", false);
    private static final Property<Long> HAIL_MARY_DEADLINE = new Property<>(Property.LONG, "hail_mary_deadline", 0L);
    private static final Property<UUID> HAIL_MARY_TARGET_PLAYER = new Property<>(Property.UUID,
            "hail_mary_target_player");
    private static final BoolProperty HAIL_MARY_EXACT_LANDING = new BoolProperty(
            "hail_mary_exact_landing", false);
    private static final BoolProperty HAIL_MARY_REMOVE_LEVITATION = new BoolProperty(
            "hail_mary_remove_levitation", false);
    private static final Property<Long> HAIL_MARY_RESCUE_PULL_DEADLINE = new Property<>(Property.LONG,
            "hail_mary_rescue_pull_deadline", 0L);
    private static final BoolProperty HAIL_MARY_TRAVEL_OVERRIDE = new BoolProperty(
            "hail_mary_travel_override", false);
    private static final BoolProperty HAIL_MARY_PREVIOUS_AUTOPILOT = new BoolProperty(
            "hail_mary_previous_autopilot", false);
    private static final Property<Identifier> HAIL_MARY_PREVIOUS_DEMAT = new Property<>(Property.IDENTIFIER,
            "hail_mary_previous_demat");
    private static final Property<Identifier> HAIL_MARY_PREVIOUS_MAT = new Property<>(Property.IDENTIFIER,
            "hail_mary_previous_mat");
    private static final Property<UUID> FORCED_ENTRY_PLAYER = new Property<>(Property.UUID, "forced_entry_player");
    private static final Property<Long> GHOST_ABSENT_SINCE = new Property<>(Property.LONG, "ghost_absent_since", 0L);
    private static final Property<Long> SIEGE_SINCE = new Property<>(Property.LONG, "siege_since", 0L);
    private static final Property<CachedDirectedGlobalPos> SIEGE_ITEM_CONTAINER = new Property<>(
            Property.CDIRECTED_GLOBAL_POS, "siege_item_container", (CachedDirectedGlobalPos) null);
    private static final Property<CachedDirectedGlobalPos> SIEGE_ITEM_CONFLICT_CONTAINER = new Property<>(
            Property.CDIRECTED_GLOBAL_POS, "siege_item_conflict_container", (CachedDirectedGlobalPos) null);
    private static final Property<UUID> SIEGE_ITEM_CONFLICT_ENTITY = new Property<>(Property.UUID,
            "siege_item_conflict_entity");
    private static final Property<CachedDirectedGlobalPos> SIEGE_ITEM_OVERFLOW_CONTAINER = new Property<>(
            Property.CDIRECTED_GLOBAL_POS, "siege_item_overflow_container", (CachedDirectedGlobalPos) null);
    private static final Property<UUID> SIEGE_ITEM_OVERFLOW_ENTITY = new Property<>(Property.UUID,
            "siege_item_overflow_entity");
    private static final Property<HashSet<String>> SIEGE_ITEM_EXTRA_LOCATORS = new Property<HashSet<String>>(
            Property.STR_SET, "siege_item_extra_locators",
            (KeyedTardisComponent ignored) -> new HashSet<>());
    private static final BoolProperty SIEGE_ITEM_CONFLICT_OVERFLOW = new BoolProperty(
            "siege_item_conflict_overflow", false);
    private static final BoolProperty SIEGE_ITEM_UNTRACKED_OVERFLOW = new BoolProperty(
            "siege_item_untracked_overflow", false);
    private static final Property<Long> SIEGE_ITEM_LOCATOR_UPDATED = new Property<>(
            Property.LONG, "siege_item_locator_updated", 0L);
    private static final Property<Long> SIEGE_ITEM_TRANSFER_UNTIL = new Property<>(
            Property.LONG, "siege_item_transfer_until", 0L);
    private static final Property<UUID> LAST_CONTROL_USER = new Property<>(Property.UUID, "last_control_user");
    private static final Property<UUID> LAST_PILOT = new Property<>(Property.UUID, "last_pilot");
    private static final Property<Long> NETHER_RETURN_DEADLINE = new Property<>(
            Property.LONG, "nether_return_deadline", 0L);
    private static final Property<BossEscapeState> BOSS_ESCAPE_STATE = Property.forEnum(
            "boss_escape_state", BossEscapeState.class, BossEscapeState.NONE);
    private static final Property<CachedDirectedGlobalPos> BOSS_ESCAPE_ORIGIN = new Property<>(
            Property.CDIRECTED_GLOBAL_POS, "boss_escape_origin", (CachedDirectedGlobalPos) null);
    private static final Property<HashSet<String>> BOSS_ESCAPE_TARGETS = new Property<HashSet<String>>(
            Property.STR_SET, "boss_escape_targets",
            (KeyedTardisComponent ignored) -> new HashSet<>());

    private final Value<AutomaticTravel> automaticTravel = AUTOMATIC_TRAVEL.create(this);
    private final BoolValue automaticSetupComplete = AUTOMATIC_SETUP_COMPLETE.create(this);
    private final BoolValue automaticPreviousAutopilot = AUTOMATIC_PREVIOUS_AUTOPILOT.create(this);
    private final BoolValue homeLanding = HOME_LANDING.create(this);
    private final BoolValue shutdownOnArrival = SHUTDOWN_ON_ARRIVAL.create(this);
    private final BoolValue crashOnArrival = CRASH_ON_ARRIVAL.create(this);
    private final DoubleValue deferredFlightCost = DEFERRED_FLIGHT_COST.create(this);
    private final BoolValue pendingFuelReturn = PENDING_FUEL_RETURN.create(this);
    private final BoolValue waitingForRefuel = WAITING_FOR_REFUEL.create(this);
    private final BoolValue hailMaryPendingLanding = HAIL_MARY_PENDING_LANDING.create(this);
    private final BoolValue hailMaryAwaitingEntry = HAIL_MARY_AWAITING_ENTRY.create(this);
    private final Value<Long> hailMaryDeadline = HAIL_MARY_DEADLINE.create(this);
    private final Value<UUID> hailMaryTargetPlayer = HAIL_MARY_TARGET_PLAYER.create(this);
    private final BoolValue hailMaryExactLanding = HAIL_MARY_EXACT_LANDING.create(this);
    private final BoolValue hailMaryRemoveLevitation = HAIL_MARY_REMOVE_LEVITATION.create(this);
    private final Value<Long> hailMaryRescuePullDeadline = HAIL_MARY_RESCUE_PULL_DEADLINE.create(this);
    private final BoolValue hailMaryTravelOverride = HAIL_MARY_TRAVEL_OVERRIDE.create(this);
    private final BoolValue hailMaryPreviousAutopilot = HAIL_MARY_PREVIOUS_AUTOPILOT.create(this);
    private final Value<Identifier> hailMaryPreviousDemat = HAIL_MARY_PREVIOUS_DEMAT.create(this);
    private final Value<Identifier> hailMaryPreviousMat = HAIL_MARY_PREVIOUS_MAT.create(this);
    private final Value<UUID> forcedEntryPlayer = FORCED_ENTRY_PLAYER.create(this);
    private final Value<Long> ghostAbsentSince = GHOST_ABSENT_SINCE.create(this);
    private final Value<Long> siegeSince = SIEGE_SINCE.create(this);
    private final Value<CachedDirectedGlobalPos> siegeItemContainer = SIEGE_ITEM_CONTAINER.create(this);
    private final Value<CachedDirectedGlobalPos> siegeItemConflictContainer = SIEGE_ITEM_CONFLICT_CONTAINER.create(this);
    private final Value<UUID> siegeItemConflictEntity = SIEGE_ITEM_CONFLICT_ENTITY.create(this);
    private final Value<CachedDirectedGlobalPos> siegeItemOverflowContainer = SIEGE_ITEM_OVERFLOW_CONTAINER.create(this);
    private final Value<UUID> siegeItemOverflowEntity = SIEGE_ITEM_OVERFLOW_ENTITY.create(this);
    @Exclude(strategy = Exclude.Strategy.NETWORK)
    private final Value<HashSet<String>> siegeItemExtraLocators = SIEGE_ITEM_EXTRA_LOCATORS.create(this);
    private final BoolValue siegeItemConflictOverflow = SIEGE_ITEM_CONFLICT_OVERFLOW.create(this);
    @Exclude(strategy = Exclude.Strategy.NETWORK)
    private final BoolValue siegeItemUntrackedOverflow = SIEGE_ITEM_UNTRACKED_OVERFLOW.create(this);
    private final Value<Long> siegeItemLocatorUpdated = SIEGE_ITEM_LOCATOR_UPDATED.create(this);
    @Exclude(strategy = Exclude.Strategy.NETWORK)
    private final Value<Long> siegeItemTransferUntil = SIEGE_ITEM_TRANSFER_UNTIL.create(this);
    private final Value<UUID> lastControlUser = LAST_CONTROL_USER.create(this);
    private final Value<UUID> lastPilot = LAST_PILOT.create(this);
    private final Value<Long> netherReturnDeadline = NETHER_RETURN_DEADLINE.create(this);
    private final Value<BossEscapeState> bossEscapeState = BOSS_ESCAPE_STATE.create(this);
    private final Value<CachedDirectedGlobalPos> bossEscapeOrigin = BOSS_ESCAPE_ORIGIN.create(this);
    private final Value<HashSet<String>> bossEscapeTargets = BOSS_ESCAPE_TARGETS.create(this);

    @Exclude
    private boolean siegeRelocationPending;
    @Exclude
    private long nextSiegeConflictValidation;
    @Exclude
    private boolean exteriorVerified;
    @Exclude
    private final Map<UUID, ChunkPos> bossTicketChunks = new HashMap<>();
    @Exclude
    private final Map<UUID, Long> bossMissingSince = new HashMap<>();
    @Exclude
    private ServerWorld bossTicketWorld;

    static {
        TardisEvents.FORCED_ENTRY.register(ReturnHomeHandler::onForcedEntry);
        TardisEvents.LANDED.register(tardis -> tardis.returnHome().onLanded());
        TardisEvents.TOGGLE_SIEGE.register((tardis, active) -> tardis.returnHome().onSiegeToggled(active));
        TardisEvents.USE_CONTROL.register((control, tardis, player, world, console, leftClick) ->
                tardis.returnHome().recordControlUse(player));
        TardisEvents.ENTER_FLIGHT.register(tardis -> tardis.returnHome().onEnterFlight());
    }

    public ReturnHomeHandler() {
        super(Id.RETURN_HOME);
    }

    @Override
    public void onLoaded() {
        automaticTravel.of(this, AUTOMATIC_TRAVEL);
        automaticSetupComplete.of(this, AUTOMATIC_SETUP_COMPLETE);
        automaticPreviousAutopilot.of(this, AUTOMATIC_PREVIOUS_AUTOPILOT);
        homeLanding.of(this, HOME_LANDING);
        shutdownOnArrival.of(this, SHUTDOWN_ON_ARRIVAL);
        crashOnArrival.of(this, CRASH_ON_ARRIVAL);
        deferredFlightCost.of(this, DEFERRED_FLIGHT_COST);
        pendingFuelReturn.of(this, PENDING_FUEL_RETURN);
        waitingForRefuel.of(this, WAITING_FOR_REFUEL);
        hailMaryPendingLanding.of(this, HAIL_MARY_PENDING_LANDING);
        hailMaryAwaitingEntry.of(this, HAIL_MARY_AWAITING_ENTRY);
        hailMaryDeadline.of(this, HAIL_MARY_DEADLINE);
        hailMaryTargetPlayer.of(this, HAIL_MARY_TARGET_PLAYER);
        hailMaryExactLanding.of(this, HAIL_MARY_EXACT_LANDING);
        hailMaryRemoveLevitation.of(this, HAIL_MARY_REMOVE_LEVITATION);
        hailMaryRescuePullDeadline.of(this, HAIL_MARY_RESCUE_PULL_DEADLINE);
        hailMaryTravelOverride.of(this, HAIL_MARY_TRAVEL_OVERRIDE);
        hailMaryPreviousAutopilot.of(this, HAIL_MARY_PREVIOUS_AUTOPILOT);
        hailMaryPreviousDemat.of(this, HAIL_MARY_PREVIOUS_DEMAT);
        hailMaryPreviousMat.of(this, HAIL_MARY_PREVIOUS_MAT);
        forcedEntryPlayer.of(this, FORCED_ENTRY_PLAYER);
        ghostAbsentSince.of(this, GHOST_ABSENT_SINCE);
        siegeSince.of(this, SIEGE_SINCE);
        siegeItemContainer.of(this, SIEGE_ITEM_CONTAINER);
        siegeItemConflictContainer.of(this, SIEGE_ITEM_CONFLICT_CONTAINER);
        siegeItemConflictEntity.of(this, SIEGE_ITEM_CONFLICT_ENTITY);
        siegeItemOverflowContainer.of(this, SIEGE_ITEM_OVERFLOW_CONTAINER);
        siegeItemOverflowEntity.of(this, SIEGE_ITEM_OVERFLOW_ENTITY);
        siegeItemExtraLocators.of(this, SIEGE_ITEM_EXTRA_LOCATORS);
        siegeItemConflictOverflow.of(this, SIEGE_ITEM_CONFLICT_OVERFLOW);
        siegeItemUntrackedOverflow.of(this, SIEGE_ITEM_UNTRACKED_OVERFLOW);
        siegeItemLocatorUpdated.of(this, SIEGE_ITEM_LOCATOR_UPDATED);
        siegeItemTransferUntil.of(this, SIEGE_ITEM_TRANSFER_UNTIL);
        lastControlUser.of(this, LAST_CONTROL_USER);
        lastPilot.of(this, LAST_PILOT);
        netherReturnDeadline.of(this, NETHER_RETURN_DEADLINE);
        bossEscapeState.of(this, BOSS_ESCAPE_STATE);
        bossEscapeOrigin.of(this, BOSS_ESCAPE_ORIGIN);
        bossEscapeTargets.of(this, BOSS_ESCAPE_TARGETS);
    }

    @Override
    public void postInit(InitContext ctx) {
        if (this.isClient())
            return;

        MinecraftServer server = ServerLifecycleHooks.get();
        if (server == null)
            return;

        this.siegeItemContainer.ifPresent(container -> container.init(server), false);
        this.siegeItemConflictContainer.ifPresent(container -> container.init(server), false);
        this.siegeItemOverflowContainer.ifPresent(container -> container.init(server), false);
        this.migrateLegacyOverflowSiegeItemLocator();
        this.bossEscapeOrigin.ifPresent(origin -> origin.init(server), false);

        if (this.bossEscapeState.get() == BossEscapeState.FLEEING_HOME
                || this.bossEscapeState.get() == BossEscapeState.WAITING_AT_HOME)
            this.restoreBossTickets(server);

        if (this.hailMaryTravelOverride.get() && this.tardis.travel().isLanded())
            this.restoreHailMaryTravelSettings();
    }

    @Override
    public void dispose() {
        if (this.isServer())
            this.releaseBossTickets();

        super.dispose();
    }

    @Override
    public void tick(MinecraftServer server) {
        long time = server.getOverworld().getTime();

        this.tickAutomaticTravel();
        this.tickFuelReturn();
        this.tickHailMary(time);

        BossEscapeState bossState = this.bossEscapeState.get();
        if ((bossState == BossEscapeState.FLEEING_HOME || bossState == BossEscapeState.WAITING_AT_HOME)
                && Math.floorMod(server.getTicks(), 20) == Math.floorMod(this.tardis.getUuid().hashCode(), 20)) {
            if (bossState == BossEscapeState.WAITING_AT_HOME)
                this.tickBossEscapeWait(server);
            else
                this.refreshBossTargets(server);
        }

        if (server.getTicks() % 20 == 0)
            this.tickGhostMonument(time);
    }

    public boolean needsTick() {
        boolean activeFuelReturn = this.pendingFuelReturn.get()
                && !this.hailMaryPendingLanding.get() && !this.hailMaryAwaitingEntry.get();
        return this.isAutomaticTravel() || activeFuelReturn || this.hailMaryPendingLanding.get()
                || this.hailMaryAwaitingEntry.get() || this.hailMaryDeadline.get() > 0
                || this.hailMaryRescuePullDeadline.get() > 0
                || this.ghostAbsentSince.get() > 0
                || this.bossEscapeState.get() == BossEscapeState.FLEEING_HOME
                || this.bossEscapeState.get() == BossEscapeState.WAITING_AT_HOME;
    }

    /**
     * Whether a TARDIS loaded only for the cold-start audit still has a dormant
     * workflow which requires subsequent server ticks.
     */
    public boolean requiresDormantResidency() {
        return this.needsTick() || this.siegeRelocationPending || this.netherReturnDeadline.get() > 0;
    }

    /** Whether an unloaded TARDIS still needs periodic external threat audits. */
    public boolean needsDormantThreatAudits() {
        if (this.bossEscapeState.get() != BossEscapeState.NONE || !this.canStartExternalSafetyAction())
            return false;

        CachedDirectedGlobalPos position = this.tardis.travel().position();
        return position != null && position.getWorld() != null && !this.hasInteriorPlayers()
                && this.shouldReturnHome(true);
    }

    /** Returns the server tick when an unloaded siege TARDIS next needs auditing. */
    public long nextDormantSiegeAuditTick(MinecraftServer server, long serverTick) {
        long delayTicks = minutesToTicks(AITMod.CONFIG.siegeReturnHomeDelayMinutes);
        if (server == null || delayTicks == 0 || !this.tardis.siege().isActive()
                || !this.shouldReturnHome(false))
            return Long.MAX_VALUE;

        long since = this.siegeSince.get();
        if (since == 0)
            return Math.max(0, serverTick);

        long worldTime = server.getOverworld().getTime();
        if (hasElapsed(worldTime, since, delayTicks))
            return Math.max(0, serverTick);

        long remaining = since > worldTime ? delayTicks : delayTicks - (worldTime - since);
        return serverTick > Long.MAX_VALUE - remaining ? Long.MAX_VALUE : serverTick + remaining;
    }

    public boolean isAutomaticTravel() {
        return this.automaticTravel.get() != AutomaticTravel.NONE;
    }

    public boolean isHomeLanding() {
        return this.homeLanding.get();
    }

    public static boolean protectsFromVoidDuringHailMary(ServerPlayerEntity player) {
        if (player == null)
            return false;

        boolean[] protectedPlayer = { false };
        ServerTardisManager manager = ServerTardisManager.getInstance();
        if (manager == null)
            return false;

        manager.forEach(tardis -> {
            if (!protectedPlayer[0] && !tardis.isRemoved()
                    && tardis.returnHome().protectsHailMaryTarget(player))
                protectedPlayer[0] = true;
        });
        return protectedPlayer[0];
    }

    private boolean protectsHailMaryTarget(ServerPlayerEntity player) {
        if (!this.hailMaryRemoveLevitation.get() || !this.tardis.alarm().isEnabled()
                || !Objects.equals(this.hailMaryTargetPlayer.get(), player.getUuid()))
            return false;

        return this.hailMaryPendingLanding.get()
                || this.hailMaryAwaitingEntry.get()
                && this.hailMaryRescuePullDeadline.get() > this.currentTime();
    }

    public void tickDormant(MinecraftServer server) {
        int ticks = server.getTicks();
        int siegeBucket = Math.floorMod(this.tardis.getUuid().hashCode(), 10);
        if ((ticks / 20) % 10 == siegeBucket)
            this.tickSiege(server.getOverworld().getTime(), server);

        int threatInterval = Math.max(1, AITMod.CONFIG.automaticThreatCheckIntervalSeconds);
        int threatBucket = Math.floorMod(this.tardis.getUuid().hashCode(), threatInterval);
        if ((ticks / 20) % threatInterval == threatBucket)
            this.tickExternalThreats(server);

        int exteriorInterval = Math.max(1, AITMod.CONFIG.missingExteriorCheckIntervalSeconds);
        int auditBucket = Math.floorMod(this.tardis.getUuid().hashCode(), exteriorInterval);
        if ((ticks / 20) % exteriorInterval == auditBucket)
            this.restoreMissingExterior();
    }

    /** Runs due cold audits without relying on resident hash buckets. */
    public void auditDormant(MinecraftServer server, boolean siege, boolean threats,
                             boolean exterior, boolean forceExteriorChunk) {
        if (siege)
            this.tickSiege(server.getOverworld().getTime(), server);
        if (threats)
            this.tickExternalThreats(server);
        if (exterior)
            this.restoreMissingExterior(forceExteriorChunk);
    }

    public boolean skipsFlightFuelCost() {
        if (this.hailMaryPendingLanding.get())
            return true;

        return switch (this.automaticTravel.get()) {
            case INSTANT, FREE, DEFERRED_NORMAL, ARTRON_DUMP -> true;
            case NONE, NORMAL, HAIL_MARY_RETURN -> false;
        };
    }

    public boolean isHailMaryPendingLanding() {
        return this.hailMaryPendingLanding.get();
    }

    public boolean isHailMaryExactLanding() {
        return this.hailMaryExactLanding.get();
    }

    public boolean isPreparingHailMaryTakeoff() {
        return this.hailMaryTravelOverride.get() && this.tardis.travel().isLanded();
    }

    public boolean canReturnHome() {
        return this.shouldReturnHome(false);
    }

    public boolean isOutsideHomeRadius() {
        return this.shouldReturnHome(true);
    }

    public boolean isAtExactHome() {
        CachedDirectedGlobalPos position = this.tardis.travel().position();
        CachedDirectedGlobalPos home = this.tardis.stats().getHome();
        return sameLocation(position, home);
    }

    public boolean isParkedAtExactHome() {
        return this.tardis.travel().isLanded() && !this.tardis.flight().isFlying()
                && !this.tardis.flight().falling().get() && this.isAtExactHome();
    }

    public static int homeRadius() {
        return Math.max(1, AITMod.CONFIG.homeRadius);
    }

    public boolean isDestinationHome() {
        return sameLocation(this.tardis.travel().destination(), this.tardis.stats().getHome());
    }

    public boolean isLastPilotReject() {
        UUID pilot = this.lastPilot.get();
        return pilot != null && this.tardis.loyalty().get(pilot).type() == Loyalty.Type.REJECT;
    }

    public boolean startHailMary(ServerPlayerEntity player, CachedDirectedGlobalPos target,
                                 boolean exactLanding) {
        if (player == null || target == null || this.isAutomaticTravel()
                || this.isHailMaryBusy() || this.tardis.siege().isActive())
            return false;

        MinecraftServer server = ServerLifecycleHooks.get();
        if (server != null)
            target.init(server);
        if (target.getWorld() == null)
            return false;
        if (!LockedDimensionRegistry.getInstance().isUnlocked(this.tardis, target.getWorld()))
            return false;

        TravelHandler travel = this.tardis.travel();
        if (!travel.isLanded())
            return false;

        this.hailMaryPreviousAutopilot.set(travel.autopilot());
        this.hailMaryPreviousDemat.set(travel.getAnimationIdFor(TravelHandlerBase.State.DEMAT));
        this.hailMaryPreviousMat.set(travel.getAnimationIdFor(TravelHandlerBase.State.MAT));
        this.hailMaryTravelOverride.set(true);

        travel.setAnimationFor(TravelHandlerBase.State.DEMAT, ZWIP_DEMAT);
        travel.setAnimationFor(TravelHandlerBase.State.MAT, ZWIP_MAT);
        travel.autopilot(true);
        this.tardis.door().closeDoors();

        if (travel.dematerializeForHailMary().isEmpty()
                || travel.getState() != TravelHandlerBase.State.DEMAT) {
            this.restoreHailMaryTravelSettings();
            return false;
        }

        travel.forceDestination(target);
        travel.decreaseFlightTime(Math.max(1, AITMod.CONFIG.automaticFlightTimeReduction));

        this.hailMaryPendingLanding.set(true);
        this.hailMaryAwaitingEntry.set(false);
        this.hailMaryDeadline.set(0L);
        this.hailMaryTargetPlayer.set(player.getUuid());
        this.hailMaryExactLanding.set(exactLanding);
        this.hailMaryRemoveLevitation.set(exactLanding);
        return true;
    }

    private boolean isHailMaryBusy() {
        return this.hailMaryPendingLanding.get() || this.hailMaryAwaitingEntry.get()
                || this.hailMaryDeadline.get() > 0 || this.hailMaryTravelOverride.get()
                || this.automaticTravel.get() == AutomaticTravel.HAIL_MARY_RETURN;
    }

    /**
     * Runs before the normal emergency-power and shutdown handling.
     *
     * @return {@code true} when this handler took ownership of the depletion.
     */
    public boolean handleFuelDepletion() {
        if (this.isAutomaticTravel()) {
            this.shutdownOnArrival.set(true);
            return true;
        }

        if (this.hailMaryPendingLanding.get() || this.hailMaryAwaitingEntry.get()) {
            this.pendingFuelReturn.set(true);
            this.waitingForRefuel.set(false);
            return true;
        }

        if (!this.shouldReturnHome(false))
            return false;

        EmergencyPower emergency = this.tardis.subsystems().emergency();
        boolean hasEmergencySystem = emergency.isEnabled();

        this.pendingFuelReturn.set(true);
        this.waitingForRefuel.set(!hasEmergencySystem);

        if (hasEmergencySystem) {
            double emergencyFuel = emergency.getCurrentFuel();
            emergency.setCurrentFuel(0);
            this.tardis.fuel().setCurrentFuelSilently(emergencyFuel);
            this.tryStartPendingFuelReturn();
        } else {
            this.tardis.setRefueling(true);
            this.landForRefueling();
            this.tardis.fuel().disablePower();
        }

        return true;
    }

    public boolean startAutomaticHome(AutomaticTravel type, boolean crash) {
        if (type == AutomaticTravel.NONE || !this.shouldReturnHome(false))
            return false;

        CachedDirectedGlobalPos home = this.tardis.stats().getHome();
        if (home == null)
            return false;

        return this.startAutomaticTravel(home, type, true, crash);
    }

    public boolean startAutomaticTravel(CachedDirectedGlobalPos target, AutomaticTravel type,
                                        boolean homeLanding, boolean crash) {
        if (target == null || type == AutomaticTravel.NONE || this.isAutomaticTravel()
                || this.tardis.siege().isActive())
            return false;

        MinecraftServer server = ServerLifecycleHooks.get();
        if (server != null)
            target.init(server);
        if (target.getWorld() == null)
            return false;

        TravelHandler travel = this.tardis.travel();
        TravelHandlerBase.State initialState = travel.getState();
        if (initialState == TravelHandlerBase.State.MAT)
            return false;

        this.automaticTravel.set(type);
        this.automaticSetupComplete.set(false);
        this.automaticPreviousAutopilot.set(travel.autopilot());
        this.homeLanding.set(homeLanding);
        this.crashOnArrival.set(crash);
        this.shutdownOnArrival.set(this.tardis.fuel().isOutOfFuel());
        this.deferredFlightCost.set(0d);
        this.tardis.sequence().cancelActiveSequence();

        if (initialState == TravelHandlerBase.State.FLIGHT) {
            travel.stopHere();
            travel.setFlightTicks(0);
        } else {
            travel.queueFor(TravelHandlerBase.State.FLIGHT).thenRun(this::applyAutomaticTravelSetup);
        }

        travel.forceDestination(target);
        travel.autopilot(true);

        if (initialState == TravelHandlerBase.State.LANDED) {
            this.tardis.setRefueling(false);
            boolean failedDemat = (type == AutomaticTravel.HAIL_MARY_RETURN
                    ? travel.dematerializeForHailMary() : travel.dematerialize()).isEmpty();
            if (failedDemat && travel.isLanded()) {
                this.tardis.door().closeDoors();
                travel.forceDemat();
            }

            if (travel.speed() == 0)
                travel.increaseSpeed();
        } else if (travel.speed() == 0) {
            travel.increaseSpeed();
        }

        if (initialState == TravelHandlerBase.State.FLIGHT)
            this.applyAutomaticTravelSetup();

        boolean started = travel.getState() == TravelHandlerBase.State.DEMAT
                || travel.getState() == TravelHandlerBase.State.FLIGHT;
        if (!started)
            this.clearAutomaticTravel();

        return started;
    }

    public void restoreMissingExterior() {
        this.restoreMissingExterior(true);
    }

    private void restoreMissingExterior(boolean loadPositionChunk) {
        if (this.tardis.asServer().isRemoved())
            return;

        TravelHandler travel = this.tardis.travel();
        if (!travel.isLanded())
            return;

        if (this.tardis.flight().falling().get() || this.tardis.flight().isFlying())
            return;

        CachedDirectedGlobalPos position = travel.position();
        if (position == null || position.getWorld() == null)
            return;

        if (this.hasStoredSiegeItem(position.getWorld().getServer()))
            return;

        if (!position.getWorld().isChunkLoaded(position.getPos())) {
            if (this.exteriorVerified || !loadPositionChunk)
                return;

            position.getWorld().getChunk(position.getPos());
        }

        if (this.tardis.getExterior().hasValidExteriorBlock()) {
            this.exteriorVerified = true;
            return;
        }

        this.relocateDirectlyToHome();
    }

    /** Called from the landed event before ghost monumenting takes off again. */
    public void onGhostLanded() {
        long delayTicks = minutesToTicks(AITMod.CONFIG.ghostMonumentReturnHomeDelayMinutes);
        if (delayTicks == 0) {
            this.ghostAbsentSince.set(0L);
            return;
        }

        if (!this.isGhostMonumenting() || !this.shouldReturnHome(true))
            return;

        long since = this.ghostAbsentSince.get();
        long now = this.currentTime();
        if (hasElapsed(now, since, delayTicks)) {
            this.relocateDirectlyToHome();
            this.ghostAbsentSince.set(now);
        }
    }

    private void tickAutomaticTravel() {
        if (!this.isAutomaticTravel() || this.automaticSetupComplete.get())
            return;

        if (this.tardis.travel().getState() != TravelHandlerBase.State.FLIGHT)
            return;

        this.applyAutomaticTravelSetup();
    }

    private void applyAutomaticTravelSetup() {
        if (!this.isAutomaticTravel() || this.automaticSetupComplete.get())
            return;

        TravelHandler travel = this.tardis.travel();
        AutomaticTravel type = this.automaticTravel.get();
        this.automaticSetupComplete.set(true);

        if (type.chargesNominalCost()) {
            double cost = TravelUtil.getNominalFlightFuelCost(travel, travel.position(), travel.destination());
            this.consumeAutomaticTravelFuel(cost);
        }

        if (type.defersNominalCost()) {
            double cost = TravelUtil.getNominalFlightFuelCost(travel, travel.position(), travel.destination());
            this.deferredFlightCost.set(Math.max(cost, 0));
        }

        if (type.isInstant())
            travel.decreaseFlightTime(Math.max(1, AITMod.CONFIG.automaticFlightTimeReduction));

        if (type == AutomaticTravel.ARTRON_DUMP)
            travel.decreaseFlightTime(999_999_999);

        if (type == AutomaticTravel.ARTRON_DUMP) {
            this.tardis.fuel().setCurrentFuelSilently(0);
            this.shutdownOnArrival.set(true);
        }

        if (this.crashOnArrival.get())
            travel.crash(false);

        if (type == AutomaticTravel.ARTRON_DUMP)
            EngineOverloadControl.damageSystemsForArtronDump(this.tardis,
                    EngineOverloadControl.FULL_ARTRON_DUMP_DAMAGE);
    }

    private void consumeAutomaticTravelFuel(double cost) {
        double available = this.tardis.getFuel();
        if (cost >= available) {
            this.tardis.fuel().setCurrentFuelSilently(0);
            this.shutdownOnArrival.set(true);
            return;
        }

        this.tardis.fuel().setCurrentFuelSilently(available - cost);
    }

    private boolean consumeDeferredFlightFuel() {
        double cost = this.deferredFlightCost.get();
        this.deferredFlightCost.set(0d);
        if (cost <= 0)
            return false;

        double available = this.tardis.getFuel();
        if (cost >= available) {
            this.tardis.fuel().setCurrentFuelSilently(0);
            this.shutdownOnArrival.set(true);
            return true;
        }

        this.tardis.fuel().setCurrentFuelSilently(available - cost);
        return false;
    }

    private void tickFuelReturn() {
        if (!this.pendingFuelReturn.get() || this.isAutomaticTravel()
                || this.hailMaryPendingLanding.get() || this.hailMaryAwaitingEntry.get()
                || this.hailMaryDeadline.get() > 0)
            return;

        this.tryStartPendingFuelReturn();
    }

    private void tryStartPendingFuelReturn() {
        if (!this.pendingFuelReturn.get())
            return;

        if (this.hailMaryPendingLanding.get() || this.hailMaryAwaitingEntry.get()
                || this.hailMaryDeadline.get() > 0)
            return;

        if (!this.shouldReturnHome(false)) {
            this.cancelPendingFuelReturn(false);
            return;
        }

        TravelHandler travel = this.tardis.travel();
        if (!this.waitingForRefuel.get() && travel.getState() != TravelHandlerBase.State.MAT) {
            if (this.startAutomaticHome(AutomaticTravel.INSTANT, false))
                this.pendingFuelReturn.set(false);
            return;
        }

        if (!travel.isLanded())
            return;

        if (this.waitingForRefuel.get()) {
            double minimum = Math.max(0, AITMod.CONFIG.automaticRefuelMinimum);
            if (this.tardis.getFuel() < minimum)
                return;

            if (!this.tardis.fuel().hasPower())
                this.tardis.fuel().enablePower(false);

            CachedDirectedGlobalPos home = this.tardis.stats().getHome();
            double cost = TravelUtil.getNominalFlightFuelCost(travel, travel.position(), home, 1, true);
            if (this.tardis.getFuel() < Math.min(cost, FuelHandler.TARDIS_MAX_FUEL))
                return;
        }

        if (this.startAutomaticHome(AutomaticTravel.INSTANT, false)) {
            this.tardis.setRefueling(false);
            this.pendingFuelReturn.set(false);
            this.waitingForRefuel.set(false);
        }
    }

    private void landForRefueling() {
        TravelHandler travel = this.tardis.travel();
        switch (travel.getState()) {
            case DEMAT -> travel.cancelDemat();
            case FLIGHT -> {
                travel.forceDestination(travel.getProgress());
                travel.forceRemat();
            }
            case LANDED, MAT -> {
            }
        }
    }

    private void tickExternalThreats(MinecraftServer server) {
        BossEscapeState state = this.bossEscapeState.get();
        if (state == BossEscapeState.WAITING_AT_HOME) {
            this.clearNetherReturnDeadline();
            return;
        }

        if (state != BossEscapeState.NONE || !this.canStartExternalSafetyAction()) {
            this.clearNetherReturnDeadline();
            return;
        }

        CachedDirectedGlobalPos position = this.tardis.travel().position();
        if (position == null || position.getWorld() == null || this.hasInteriorPlayers()) {
            this.clearNetherReturnDeadline();
            return;
        }

        if (position.getWorld().isChunkLoaded(position.getPos()) && this.shouldReturnHome(true)) {
            List<LivingEntity> bosses = this.findBossesNear(position);
            if (!bosses.isEmpty()) {
                this.clearNetherReturnDeadline();
                this.startBossEscape(position, bosses);
                return;
            }
        }

        this.tryStartNetherReturn(position, server.getOverworld().getTime());
    }

    private boolean canStartExternalSafetyAction() {
        TravelHandler travel = this.tardis.travel();
        return travel.isLanded() && !travel.handbrake() && this.tardis.fuel().hasPower()
                && !this.tardis.siege().isActive() && !this.tardis.flight().isFlying()
                && !this.tardis.flight().falling().get() && !this.isAutomaticTravel()
                && !this.pendingFuelReturn.get() && !this.hailMaryPendingLanding.get()
                && !this.hailMaryAwaitingEntry.get() && this.hailMaryDeadline.get() <= 0
                && this.forcedEntryPlayer.get() == null;
    }

    private void tryStartNetherReturn(CachedDirectedGlobalPos position, long time) {
        CachedDirectedGlobalPos home = this.tardis.stats().getHome();
        UUID pilot = this.lastPilot.get();
        if (home == null || pilot == null || !position.getDimension().equals(World.NETHER)
                || home.getDimension().equals(World.NETHER)) {
            this.clearNetherReturnDeadline();
            return;
        }

        if (this.tardis.loyalty().get(pilot).isOf(Loyalty.Type.PILOT)) {
            this.clearNetherReturnDeadline();
            return;
        }

        long deadline = this.netherReturnDeadline.get();
        if (deadline <= 0) {
            this.netherReturnDeadline.set(nextNetherReturnDeadline(time));
            return;
        }

        if (time < deadline)
            return;

        if (!position.getWorld().isChunkLoaded(position.getPos()))
            position.getWorld().getChunk(position.getPos());

        if (this.startAutomaticTravel(home, AutomaticTravel.DEFERRED_NORMAL, true, false)) {
            this.clearNetherReturnDeadline();
        } else {
            this.netherReturnDeadline.set(nextNetherReturnDeadline(time));
        }
    }

    private static long nextNetherReturnDeadline(long time) {
        long configuredMin = minutesToTicks(AITMod.CONFIG.netherReturnMinDelayMinutes);
        long configuredMax = minutesToTicks(AITMod.CONFIG.netherReturnMaxDelayMinutes);
        long minimum = Math.min(configuredMin, configuredMax);
        long maximum = Math.max(configuredMin, configuredMax);
        long range = maximum - minimum;
        return time + minimum + (range == 0 ? 0 : AITMod.RANDOM.nextInt((int) range + 1));
    }

    private void clearNetherReturnDeadline() {
        if (this.netherReturnDeadline.get() != 0)
            this.netherReturnDeadline.set(0L);
    }

    private void startBossEscape(CachedDirectedGlobalPos position, List<LivingEntity> bosses) {
        CachedDirectedGlobalPos origin = CachedDirectedGlobalPos.create(position.getWorld(),
                position.getPos(), position.getRotation());
        this.bossEscapeOrigin.set(origin);
        this.setBossTargets(position.getWorld(), bosses);
        this.bossEscapeState.set(BossEscapeState.FLEEING_HOME);

        CachedDirectedGlobalPos home = this.tardis.stats().getHome();
        if (!this.startAutomaticTravel(home, AutomaticTravel.DEFERRED_NORMAL, true, false))
            this.clearBossEscape();
    }

    private void tickBossEscapeWait(MinecraftServer server) {
        if (this.hasInteriorPlayers()) {
            this.cancelBossEscapeWait();
            return;
        }

        CachedDirectedGlobalPos origin = this.bossEscapeOrigin.get();
        if (origin == null) {
            this.cancelBossEscapeWait();
            return;
        }

        origin.init(server);
        if (this.refreshBossTargets(server) || this.bossEscapeState.get() == BossEscapeState.NONE)
            return;

        if (!this.tardis.fuel().hasPower()) {
            if (this.tardis.getFuel() < Math.max(0, AITMod.CONFIG.automaticRefuelMinimum))
                return;

            this.tardis.fuel().enablePower(false);
            if (!this.tardis.fuel().hasPower())
                return;
        }

        this.tardis.setRefueling(false);
        this.tardis.travel().handbrake(false);
        this.clearBossTargets();
        this.bossEscapeState.set(BossEscapeState.RETURNING_TO_ORIGIN);
        if (!this.startAutomaticTravel(origin, AutomaticTravel.DEFERRED_NORMAL, false, false)) {
            this.bossEscapeState.set(BossEscapeState.WAITING_AT_HOME);
            this.tardis.travel().handbrake(true);
            this.tardis.setRefueling(true);
        }
    }

    private List<LivingEntity> findBossesNear(CachedDirectedGlobalPos location) {
        ServerWorld world = location.getWorld();
        if (world == null || !world.isChunkLoaded(location.getPos()))
            return List.of();

        BlockPos pos = location.getPos();
        double radius = Math.max(1, AITMod.CONFIG.bossDetectionRadius);
        double radiusSquared = radius * radius;
        Box box = new Box(pos).expand(radius);
        return world.getEntitiesByClass(LivingEntity.class, box, entity -> entity.isAlive()
                && entity.getType().isIn(AITTags.EntityTypes.BOSS)
                && entity.squaredDistanceTo(pos.getX() + 0.5, pos.getY() + 0.5,
                pos.getZ() + 0.5) <= radiusSquared);
    }

    private void setBossTargets(ServerWorld world, List<LivingEntity> bosses) {
        this.clearBossTargets();

        Map<UUID, ChunkPos> targets = new LinkedHashMap<>();
        for (LivingEntity boss : bosses) {
            if (boss.isAlive() && boss.getType().isIn(AITTags.EntityTypes.BOSS))
                targets.put(boss.getUuid(), boss.getChunkPos());
        }

        this.writeBossTargets(targets);
        this.reconcileBossTickets(world, targets);
    }

    /**
     * Keeps only the chunks containing the bosses which triggered the escape
     * loaded. The grace period applies whenever a boss is no longer detected in
     * the origin radius, including asynchronous entity loading after a reload.
     *
     * @return whether at least one tracked boss may still be inside the origin
     *         radius
     */
    private boolean refreshBossTargets(MinecraftServer server) {
        CachedDirectedGlobalPos origin = this.bossEscapeOrigin.get();
        if (origin == null) {
            this.clearBossEscape();
            return false;
        }

        origin.init(server);
        ServerWorld world = origin.getWorld();
        if (world == null) {
            this.clearBossEscape();
            return false;
        }

        Map<UUID, ChunkPos> targets = this.readBossTargets();
        this.reconcileBossTickets(world, targets);

        BlockPos originPos = origin.getPos();
        double radius = Math.max(1, AITMod.CONFIG.bossDetectionRadius);
        double radiusSquared = radius * radius;
        long now = server.getOverworld().getTime();

        for (Map.Entry<UUID, ChunkPos> entry : List.copyOf(targets.entrySet())) {
            UUID bossId = entry.getKey();
            ChunkPos lastChunk = entry.getValue();
            if (!world.getChunkManager().isChunkLoaded(lastChunk.x, lastChunk.z)) {
                this.bossMissingSince.remove(bossId);
                continue;
            }

            Entity loaded = world.getEntity(bossId);
            boolean stillDetected = loaded instanceof LivingEntity boss && boss.isAlive()
                    && boss.getType().isIn(AITTags.EntityTypes.BOSS)
                    && boss.squaredDistanceTo(originPos.getX() + 0.5, originPos.getY() + 0.5,
                    originPos.getZ() + 0.5) <= radiusSquared;
            if (!stillDetected) {
                long missingSince = this.bossMissingSince.computeIfAbsent(bossId, ignored -> now);
                long graceTicks = Math.max(0L, AITMod.CONFIG.bossMissingGraceSeconds) * 20L;
                if (now >= missingSince && now - missingSince < graceTicks)
                    continue;

                targets.remove(bossId);
                this.removeBossTicket(bossId);
                this.bossMissingSince.remove(bossId);
                continue;
            }

            this.bossMissingSince.remove(bossId);
            LivingEntity boss = (LivingEntity) loaded;

            ChunkPos currentChunk = boss.getChunkPos();
            if (!currentChunk.equals(lastChunk)) {
                targets.put(bossId, currentChunk);
                this.setBossTicket(world, bossId, currentChunk);
            }
        }

        this.writeBossTargets(targets);
        return !targets.isEmpty();
    }

    private Map<UUID, ChunkPos> readBossTargets() {
        Map<UUID, ChunkPos> targets = new LinkedHashMap<>();
        HashSet<String> encodedTargets = this.bossEscapeTargets.get();
        if (encodedTargets == null)
            return targets;

        for (String encoded : encodedTargets) {
            BossTarget target = BossTarget.decode(encoded);
            if (target != null)
                targets.put(target.entityId(), target.chunk());
        }

        return targets;
    }

    private void writeBossTargets(Map<UUID, ChunkPos> targets) {
        HashSet<String> encoded = new HashSet<>();
        for (Map.Entry<UUID, ChunkPos> target : targets.entrySet())
            encoded.add(new BossTarget(target.getKey(), target.getValue()).encode());

        if (!Objects.equals(this.bossEscapeTargets.get(), encoded))
            this.bossEscapeTargets.set(encoded);
    }

    private void restoreBossTickets(MinecraftServer server) {
        CachedDirectedGlobalPos origin = this.bossEscapeOrigin.get();
        if (origin == null)
            return;

        origin.init(server);
        if (origin.getWorld() != null)
            this.reconcileBossTickets(origin.getWorld(), this.readBossTargets());
    }

    private void reconcileBossTickets(ServerWorld world, Map<UUID, ChunkPos> targets) {
        for (UUID bossId : List.copyOf(this.bossTicketChunks.keySet())) {
            if (!targets.containsKey(bossId))
                this.removeBossTicket(bossId);
        }

        for (Map.Entry<UUID, ChunkPos> target : targets.entrySet())
            this.setBossTicket(world, target.getKey(), target.getValue());
    }

    private void setBossTicket(ServerWorld world, UUID bossId, ChunkPos chunk) {
        if (this.bossTicketWorld != null && this.bossTicketWorld != world)
            this.releaseBossTickets();

        this.bossTicketWorld = world;
        ChunkPos previous = this.bossTicketChunks.get(bossId);
        if (chunk.equals(previous))
            return;

        world.getChunkManager().addTicket(BOSS_ESCAPE_TICKET, chunk, BOSS_TICKET_RADIUS,
                this.bossTicketKey(bossId));
        this.bossTicketChunks.put(bossId, chunk);

        if (previous != null)
            world.getChunkManager().removeTicket(BOSS_ESCAPE_TICKET, previous, BOSS_TICKET_RADIUS,
                    this.bossTicketKey(bossId));
    }

    private void removeBossTicket(UUID bossId) {
        ChunkPos chunk = this.bossTicketChunks.remove(bossId);
        if (chunk == null || this.bossTicketWorld == null)
            return;

        this.bossTicketWorld.getChunkManager().removeTicket(BOSS_ESCAPE_TICKET, chunk,
                BOSS_TICKET_RADIUS, this.bossTicketKey(bossId));
    }

    private void clearBossTargets() {
        this.releaseBossTickets();
        this.bossMissingSince.clear();
        if (this.bossEscapeTargets.get() == null || !this.bossEscapeTargets.get().isEmpty())
            this.bossEscapeTargets.set(new HashSet<>());
    }

    private void releaseBossTickets() {
        if (this.bossTicketWorld != null) {
            for (Map.Entry<UUID, ChunkPos> ticket : this.bossTicketChunks.entrySet()) {
                this.bossTicketWorld.getChunkManager().removeTicket(BOSS_ESCAPE_TICKET, ticket.getValue(),
                        BOSS_TICKET_RADIUS, this.bossTicketKey(ticket.getKey()));
            }
        }

        this.bossTicketChunks.clear();
        this.bossMissingSince.clear();
        this.bossTicketWorld = null;
    }

    private String bossTicketKey(UUID bossId) {
        return this.tardis.getUuid() + ":" + bossId;
    }

    private boolean hasInteriorPlayers() {
        return this.tardis.asServer().hasWorld() && !this.tardis.asServer().world().getPlayers().isEmpty();
    }

    private void cancelBossEscapeWait() {
        this.tardis.setRefueling(false);
        this.tardis.travel().handbrake(false);
        this.clearBossEscape();
    }

    public boolean recoverInterruptedBossReturn() {
        if (!this.isAutomaticTravel()
                || this.bossEscapeState.get() != BossEscapeState.RETURNING_TO_ORIGIN)
            return false;

        this.clearAutomaticTravel();
        this.bossEscapeState.set(BossEscapeState.WAITING_AT_HOME);
        this.tardis.travel().handbrake(true);
        this.tardis.setRefueling(true);
        return true;
    }

    private void clearBossEscape() {
        this.clearBossTargets();
        this.bossEscapeState.set(BossEscapeState.NONE);
        this.bossEscapeOrigin.set((CachedDirectedGlobalPos) null);
    }

    public void recordControlUse(ServerPlayerEntity player) {
        if (player == null)
            return;

        if (this.isAutomaticTravel())
            return;

        this.lastControlUser.set(player.getUuid());
        if (this.tardis.travel().inFlight())
            this.lastPilot.set(player.getUuid());
    }

    private void onEnterFlight() {
        this.clearNetherReturnDeadline();
        if (!this.isAutomaticTravel())
            this.lastPilot.set(this.lastControlUser.get());
    }

    private void tickHailMary(long time) {
        if ((this.hailMaryPendingLanding.get() || this.hailMaryAwaitingEntry.get()
                || this.hailMaryDeadline.get() > 0) && !this.tardis.alarm().isEnabled()) {
            this.clearHailMaryReturn();
            if (this.tardis.fuel().isOutOfFuel())
                this.tardis.fuel().disablePower();
            return;
        }

        this.tickHailMaryRescuePull(time);

        long deadline = this.hailMaryDeadline.get();
        if (deadline <= 0)
            return;

        long remaining = deadline - time;
        if (remaining <= 0) {
            if (this.isAtExactHome()) {
                this.tardis.alarm().disable();
                this.clearHailMaryReturn();
                if (this.tardis.fuel().isOutOfFuel())
                    this.tardis.fuel().disablePower();
            } else if (this.tardis.travel().handbrake()
                    || this.tardis.stats().getHome() == null) {
                this.clearHailMaryReturn();
            } else if (this.startHailMaryHomeReturn()) {
                this.clearHailMaryReturn();
            }
            return;
        }

        if (time % 20 == 0) {
            long seconds = (long) Math.ceil(remaining / 20d);
            Text message = Text.translatable("tardis.message.hail_mary.return_home_countdown", seconds)
                    .formatted(Formatting.RED);
            TardisUtil.sendMessageToInterior(this.tardis.asServer(), message);
        }
    }

    private void onLanded() {
        this.exteriorVerified = false;

        boolean finishedHailMaryLanding = this.hailMaryPendingLanding.get();
        if (this.hailMaryTravelOverride.get())
            this.restoreHailMaryTravelSettings();

        if (finishedHailMaryLanding) {
            this.hailMaryPendingLanding.set(false);
            if (this.hailMaryRemoveLevitation.get()) {
                long pullTicks = Math.max(1L, secondsToTicks(AITMod.CONFIG.hailMaryLevitationSeconds));
                this.hailMaryRescuePullDeadline.set(this.currentTime() + pullTicks);
            }
            if (this.hailMaryDeadline.get() <= 0) {
                if (this.isHailMaryTargetInside())
                    this.startHailMaryCountdown();
                else
                    this.hailMaryAwaitingEntry.set(true);
            }
        }

        if (this.isAutomaticTravel()) {
            AutomaticTravel completedTravel = this.automaticTravel.get();
            boolean reachedDestination = this.hasReachedAutomaticDestination();
            if (!reachedDestination) {
                this.clearAutomaticTravel();
                this.clearBossEscape();
            } else {
                this.consumeDeferredFlightFuel();
                boolean shutdown = this.shutdownOnArrival.get();
                BossEscapeState bossState = this.bossEscapeState.get();
                this.clearAutomaticTravel();

                if (bossState == BossEscapeState.FLEEING_HOME) {
                    this.tardis.travel().handbrake(true);
                    this.tardis.setRefueling(true);
                    this.bossEscapeState.set(BossEscapeState.WAITING_AT_HOME);
                    if (shutdown)
                        this.tardis.fuel().disablePower();
                } else {
                    if (bossState == BossEscapeState.RETURNING_TO_ORIGIN)
                        this.clearBossEscape();
                    if (shutdown)
                        this.tardis.fuel().disablePower();
                }
            }

            if (completedTravel == AutomaticTravel.HAIL_MARY_RETURN) {
                this.hailMaryExactLanding.set(false);
                if (reachedDestination)
                    this.tardis.alarm().disable();
            }
        }

        if (this.pendingFuelReturn.get())
            this.tryStartPendingFuelReturn();
    }

    private void onPlayerEntered(Entity entity) {
        if (!(entity instanceof ServerPlayerEntity player))
            return;

        UUID targetPlayer = this.hailMaryTargetPlayer.get();
        boolean isTargetPlayer = targetPlayer == null || Objects.equals(targetPlayer, player.getUuid());
        if (isTargetPlayer && this.hailMaryRemoveLevitation.get()) {
            player.removeStatusEffect(StatusEffects.LEVITATION);
            this.hailMaryRemoveLevitation.set(false);
            this.hailMaryRescuePullDeadline.set(0L);
        }

        boolean preservePowerForHailReturn = (this.hailMaryPendingLanding.get()
                || this.hailMaryAwaitingEntry.get() || this.hailMaryDeadline.get() > 0)
                && this.tardis.alarm().isEnabled();

        if (this.pendingFuelReturn.get() && !this.isAutomaticTravel())
            this.cancelPendingFuelReturn(preservePowerForHailReturn);

        if (!isTargetPlayer || this.hailMaryDeadline.get() > 0
                || (!this.hailMaryPendingLanding.get() && !this.hailMaryAwaitingEntry.get()))
            return;

        this.hailMaryAwaitingEntry.set(false);
        if (!this.tardis.alarm().isEnabled()) {
            this.clearHailMaryReturn();
            return;
        }

        this.startHailMaryCountdown();
    }

    public void onConfirmedPlayerEntered(ServerPlayerEntity player) {
        if (player == null)
            return;


        this.clearNetherReturnDeadline();
        this.onPlayerEntered(player);

        if (this.bossEscapeState.get() == BossEscapeState.WAITING_AT_HOME) {
            this.cancelBossEscapeWait();
            return;
        }

        UUID forcedEntryPlayer = this.forcedEntryPlayer.get();
        if (forcedEntryPlayer == null)
            return;

        this.forcedEntryPlayer.set((UUID) null);
        if (!Objects.equals(forcedEntryPlayer, player.getUuid()))
            return;

        if (!this.canReturnHome())
            return;

        int forcedFuel = Math.max(0, AITMod.CONFIG.forcedEntryArtronDumpFuel);
        if (this.tardis.getFuel() < forcedFuel)
            this.tardis.fuel().setCurrentFuelSilently(this.tardis.getFuel() + forcedFuel);

        this.tardis.fuel().enablePower(false);
        this.startAutomaticHome(AutomaticTravel.ARTRON_DUMP, true);
    }

    public boolean hasReachedAutomaticDestination() {
        CachedDirectedGlobalPos position = this.tardis.travel().position();
        if (position == null)
            return false;

        if (this.automaticTravel.get() == AutomaticTravel.HAIL_MARY_RETURN
                && this.hailMaryExactLanding.get())
            return sameLocation(position, this.tardis.stats().getHome());

        if (this.homeLanding.get())
            return !this.shouldReturnHome(true);

        return sameLocation(position, this.tardis.travel().destination());
    }

    private void startHailMaryCountdown() {
        long delayTicks = Math.max(1L, secondsToTicks(AITMod.CONFIG.hailMaryReturnDelaySeconds));
        this.hailMaryAwaitingEntry.set(false);
        this.hailMaryDeadline.set(this.currentTime() + delayTicks);
    }

    private boolean isHailMaryTargetInside() {
        if (!this.tardis.asServer().hasWorld())
            return false;

        UUID targetPlayer = this.hailMaryTargetPlayer.get();
        return this.tardis.asServer().world().getPlayers().stream()
                .anyMatch(player -> targetPlayer == null || Objects.equals(targetPlayer, player.getUuid()));
    }

    private void restoreHailMaryTravelSettings() {
        if (!this.hailMaryTravelOverride.get())
            return;

        TravelHandler travel = this.tardis.travel();
        Identifier previousDemat = this.hailMaryPreviousDemat.get();
        Identifier previousMat = this.hailMaryPreviousMat.get();
        travel.setAnimationFor(TravelHandlerBase.State.DEMAT, previousDemat);
        travel.setAnimationFor(TravelHandlerBase.State.MAT, previousMat);
        travel.autopilot(this.hailMaryPreviousAutopilot.get());

        this.hailMaryTravelOverride.set(false);
        this.hailMaryPreviousAutopilot.set(false);
        this.hailMaryPreviousDemat.set((Identifier) null);
        this.hailMaryPreviousMat.set((Identifier) null);
        this.hailMaryExactLanding.set(false);
    }

    private void clearHailMaryReturn() {
        this.hailMaryPendingLanding.set(false);
        this.hailMaryAwaitingEntry.set(false);
        this.hailMaryDeadline.set(0L);
        this.hailMaryTargetPlayer.set((UUID) null);
        this.hailMaryRemoveLevitation.set(false);
        this.hailMaryRescuePullDeadline.set(0L);
    }

    private void tickHailMaryRescuePull(long time) {
        long deadline = this.hailMaryRescuePullDeadline.get();
        if (deadline <= 0)
            return;

        if (time >= deadline || !this.hailMaryAwaitingEntry.get()
                || !this.tardis.travel().isLanded()) {
            this.hailMaryRescuePullDeadline.set(0L);
            return;
        }

        ServerPlayerEntity player = this.getHailMaryRescueTarget();
        CachedDirectedGlobalPos position = this.tardis.travel().position();
        if (player == null || position == null || position.getWorld() != player.getServerWorld())
            return;

        BlockPos exteriorPos = position.getPos();
        Box rescueBox = new Box(exteriorPos.getX() - 0.15, exteriorPos.getY() - 0.15,
                exteriorPos.getZ() - 0.15, exteriorPos.getX() + 1.15,
                exteriorPos.getY() + 2.15, exteriorPos.getZ() + 1.15);
        if (player.getBoundingBox().intersects(rescueBox)) {
            TardisUtil.teleportInside(this.tardis.asServer(), player);
            return;
        }

        double strength = Math.min(1, Math.max(0, AITMod.CONFIG.hailMaryRescuePullStrength));
        if (strength <= 0)
            return;

        Vec3d offset = position.getPos().toCenterPos().add(0, 0.5, 0).subtract(player.getPos());
        if (offset.lengthSquared() < 0.0001)
            return;

        Vec3d velocity = player.getVelocity().multiply(0.6).add(offset.normalize().multiply(strength));
        player.setVelocity(velocity);
        player.velocityDirty = true;
        player.velocityModified = true;
        player.fallDistance = 0;
    }

    public boolean tryCompleteHailMaryRescue(Entity entity) {
        if (!(entity instanceof ServerPlayerEntity player)
                || this.hailMaryRescuePullDeadline.get() <= this.currentTime()
                || player != this.getHailMaryRescueTarget())
            return false;

        CachedDirectedGlobalPos position = this.tardis.travel().position();
        if (!this.tardis.travel().isLanded() || position == null
                || position.getWorld() != player.getServerWorld())
            return false;

        TardisUtil.teleportInside(this.tardis.asServer(), player);
        return true;
    }

    private @Nullable ServerPlayerEntity getHailMaryRescueTarget() {
        UUID target = this.hailMaryTargetPlayer.get();
        MinecraftServer server = ServerLifecycleHooks.get();
        if (target == null || server == null)
            return null;

        ServerPlayerEntity player = server.getPlayerManager().getPlayer(target);
        return player != null && player.isAlive() && !player.isRemoved() ? player : null;
    }

    private void tickGhostMonument(long time) {
        if (minutesToTicks(AITMod.CONFIG.ghostMonumentReturnHomeDelayMinutes) == 0) {
            if (this.ghostAbsentSince.get() != 0)
                this.ghostAbsentSince.set(0L);
            return;
        }

        if (!this.isGhostMonumenting() || this.hasNearbyPlayer()) {
            this.ghostAbsentSince.set(0L);
            return;
        }

        if (this.ghostAbsentSince.get() == 0)
            this.ghostAbsentSince.set(time);
    }

    boolean isGhostMonumenting() {
        TravelHandler travel = this.tardis.travel();
        return AITMod.CONFIG.ghostMonument && TardisUtil.isInteriorEmpty(this.tardis.asServer())
                && !travel.leaveBehind().get() && !travel.autopilot() && travel.speed() > 0 && !travel.handbrake();
    }

    private boolean hasNearbyPlayer() {
        CachedDirectedGlobalPos position = this.tardis.travel().position();
        if (position == null || position.getWorld() == null)
            return false;

        BlockPos pos = position.getPos();
        for (ServerPlayerEntity player : position.getWorld().getPlayers()) {
            int radius = homeRadius();
            if (player.squaredDistanceTo(pos.getX(), pos.getY(), pos.getZ()) < radius * radius)
                return true;
        }

        return false;
    }

    private void onSiegeToggled(boolean active) {
        this.clearNetherReturnDeadline();
        long delayTicks = minutesToTicks(AITMod.CONFIG.siegeReturnHomeDelayMinutes);
        this.siegeSince.set(active && delayTicks > 0 ? this.currentTime() : 0L);
        if (!active)
            this.clearSiegeItemLocators();
    }

    private void tickSiege(long time, MinecraftServer server) {
        long delayTicks = minutesToTicks(AITMod.CONFIG.siegeReturnHomeDelayMinutes);
        if (delayTicks == 0) {
            if (this.siegeSince.get() != 0)
                this.siegeSince.set(0L);
            return;
        }

        if (!this.tardis.siege().isActive()) {
            if (this.siegeSince.get() != 0)
                this.siegeSince.set(0L);
            return;
        }

        if (this.siegeSince.get() == 0) {
            this.siegeSince.set(time);
            return;
        }

        if (this.siegeRelocationPending || !hasElapsed(time, this.siegeSince.get(), delayTicks)
                || !this.shouldReturnHome(false))
            return;

        this.hasStoredSiegeItem(server);

        this.relocateSiegeToHome(server);
    }

    private void relocateSiegeToHome(MinecraftServer server) {
        CachedDirectedGlobalPos home = this.tardis.stats().getHome();
        if (home == null)
            return;

        home.init(server);
        if (home.getWorld() == null)
            return;

        this.siegeRelocationPending = true;
        int radius = homeRadius();
        SafePosSearch.wrapSafe(home, this.tardis.travel().verticalSearch().get(), true, radius, result -> {
            this.siegeRelocationPending = false;
            if (this.tardis.asServer().isRemoved())
                return;

            long delayTicks = minutesToTicks(AITMod.CONFIG.siegeReturnHomeDelayMinutes);
            if (!this.tardis.siege().isActive()
                    || !hasElapsed(this.currentTime(), this.siegeSince.get(), delayTicks)
                    || !this.shouldReturnHome(false)) return;
            if (!this.reconcileSiegeItemLocatorsForRelocation(server))
                return;

            boolean withinHomeRadius = result.foundSafePosition()
                    && result.position().getPos().getSquaredDistance(home.getPos()) <= radius * radius;
            CachedDirectedGlobalPos target = withinHomeRadius ? result.position() : home;
            if (!withinHomeRadius)
                this.tardis.travel().clearLandingObstructions(target);

            CachedDirectedGlobalPos source = this.tardis.travel().position();
            if (!SiegeTardisItem.placeTardis(this.tardis, target)) {
                this.siegeSince.set(this.currentTime());
                AITMod.LOGGER.warn("Failed to relocate siege TARDIS {} to {}; the carrier was preserved",
                        this.tardis.getUuid(), target);
                return;
            }

            this.chargeDirectAutomaticTravel(source, target);
            this.tardis.travel().forceDestination(target);
            this.exteriorVerified = true;
            this.clearSiegeItemLocators();
            this.siegeSince.set(this.currentTime());
        });
    }

    private static long minutesToTicks(int minutes) {
        return Math.max(0L, (long) minutes) * 60L * 20L;
    }

    private static long secondsToTicks(int seconds) {
        return Math.max(0L, (long) seconds) * 20L;
    }

    private static boolean hasElapsed(long now, long since, long delayTicks) {
        return delayTicks > 0 && since > 0 && now >= since && now - since >= delayTicks;
    }

    private boolean removeSiegeItem(MinecraftServer server) {
        if (!this.reconcileSiegeItemLocators(server))
            return false;

        CachedDirectedGlobalPos container = this.siegeItemContainer.get();
        if (container != null)
            return this.removeStoredSiegeItem(server, container);

        UUID holder = this.tardis.siege().getHeldPlayerUUID();
        if (holder == null)
            return this.tardis.getExterior().hasValidExteriorBlock();

        Entity entity = this.findSiegeItemEntity(server, holder, true);
        return entity != null && SiegeInventoryUtil.remove(entity, this.tardis.getUuid());
    }

    private boolean removeStoredSiegeItem(MinecraftServer server, CachedDirectedGlobalPos container) {
        container.init(server);
        ServerWorld world = container.getWorld();
        if (world == null)
            return false;

        world.getChunk(container.getPos());
        BlockEntity blockEntity = world.getBlockEntity(container.getPos());
        if (!(blockEntity instanceof Inventory inventory))
            return false;

        if (SiegeInventoryUtil.remove(inventory, this.tardis.getUuid())) {
            this.siegeItemContainer.set((CachedDirectedGlobalPos) null);
            this.siegeItemLocatorUpdated.set(0L);
            return true;
        }

        return false;
    }

    private void cancelPendingFuelReturn(boolean preservePower) {
        this.pendingFuelReturn.set(false);
        this.waitingForRefuel.set(false);
        this.tardis.setRefueling(false);

        if (!preservePower && this.tardis.fuel().isOutOfFuel())
            this.tardis.fuel().disablePower();
    }

    private boolean hasStoredSiegeItem(MinecraftServer server) {
        if (!this.reconcileSiegeItemLocators(server))
            return true;

        CachedDirectedGlobalPos container = this.siegeItemContainer.get();
        long now = server.getOverworld().getTime();

        if (container != null) {
            container.init(server);
            ServerWorld world = container.getWorld();
            if (world == null || !world.isChunkLoaded(container.getPos()))
                return true;

            BlockEntity blockEntity = world.getBlockEntity(container.getPos());
            if (blockEntity instanceof Inventory inventory
                    && SiegeInventoryUtil.scan(inventory, this.tardis.getUuid()).mayContain()) {
                this.siegeItemLocatorUpdated.set(now);
                return true;
            }

            if (this.isWithinSiegeTransferGrace(now))
                return true;

            // A nested siege item can leave this inventory before the destination
            // carrier's coalesced scan runs. Keep recovery fail-closed for one full
            // carrier scan window instead of briefly treating the item as destroyed.
            this.markSiegeItemTransferPending(server);
            this.clearPrimarySiegeItemLocator();
            return true;
        }

        UUID holder = this.tardis.siege().getHeldPlayerUUID();
        if (holder == null) {
            this.siegeItemLocatorUpdated.set(0L);
            return false;
        }

        Entity entity = this.findSiegeItemEntity(server, holder, false);
        if (entity != null && SiegeInventoryUtil.scan(entity, this.tardis.getUuid()).mayContain()) {
            this.siegeItemLocatorUpdated.set(now);
            return true;
        }

        CachedDirectedGlobalPos position = this.tardis.travel().position();
        if (entity == null && position != null) {
            position.init(server);
            ServerWorld world = position.getWorld();
            if (world == null || !world.isChunkLoaded(position.getPos()))
                return true;
        }

        if (this.isWithinSiegeTransferGrace(now))
            return true;

        this.markSiegeItemTransferPending(server);
        this.clearPrimarySiegeItemLocator();
        return true;
    }

    private boolean isWithinSiegeTransferGrace(long now) {
        long updated = this.siegeItemLocatorUpdated.get();
        if (updated <= 0 || now < updated) {
            this.siegeItemLocatorUpdated.set(now);
            return true;
        }

        return now - updated <= SIEGE_LOCATOR_TRANSFER_GRACE_TICKS;
    }

    private void markSiegeItemTransferPending(MinecraftServer server) {
        if (server == null)
            return;

        long now = server.getOverworld().getTime();
        long until = now > Long.MAX_VALUE - SIEGE_LOCATOR_TRANSFER_GRACE_TICKS
                ? Long.MAX_VALUE : now + SIEGE_LOCATOR_TRANSFER_GRACE_TICKS;
        this.siegeItemTransferUntil.setPersistent(until);
    }

    private boolean isSiegeItemTransferPending(MinecraftServer server) {
        long until = this.siegeItemTransferUntil.get();
        if (server == null || until <= 0)
            return false;

        if (server.getOverworld().getTime() < until)
            return true;

        this.siegeItemTransferUntil.setPersistent(0L);
        return false;
    }

    private void clearSiegeItemTransferPending() {
        this.siegeItemTransferUntil.setPersistent(0L);
    }

    private boolean observeSiegeItemLocator(MinecraftServer server, SiegeItemLocator candidate) {
        return this.observeSiegeItemLocator(server, candidate, null);
    }

    private boolean observeSiegeItemLocator(MinecraftServer server, SiegeItemLocator candidate,
                                            @Nullable Entity knownCarrier) {
        if (server == null || candidate == null)
            return false;

        SiegeItemLocator observedCurrent = this.primarySiegeItemLocator();
        SiegeItemLocator observedConflict = this.conflictingSiegeItemLocator();
        long now = server.getOverworld().getTime();
        if (this.siegeItemConflictOverflow.get())
            return this.tryRecoverSiegeConflictOverflow(server, candidate, knownCarrier);

        boolean knownCandidate = sameSiegeItemLocator(candidate, observedCurrent)
                || sameSiegeItemLocator(candidate, observedConflict);
        if (!this.siegeItemConflictOverflow.get() && observedConflict != null && knownCandidate
                && now < this.nextSiegeConflictValidation) {
            if (sameSiegeItemLocator(candidate, observedCurrent))
                this.refreshPrimarySiegeItemPosition(server, candidate, knownCarrier);
            return false;
        }
        if (observedConflict != null)
            this.nextSiegeConflictValidation = now + SIEGE_CONFLICT_VALIDATION_COOLDOWN_TICKS;

        if (!this.reconcileSiegeItemLocators(server)) {
            if (this.siegeItemConflictOverflow.get())
                return false;

            SiegeItemLocator current = this.primarySiegeItemLocator();
            SiegeItemLocator conflict = this.conflictingSiegeItemLocator();

            if (sameSiegeItemLocator(candidate, current)) {
                this.refreshPrimarySiegeItemPosition(server, candidate, knownCarrier);
                return conflict == null;
            }
            if (sameSiegeItemLocator(candidate, conflict))
                return false;

            if (current == null) {
                this.setPrimarySiegeItemLocator(server, candidate);
                return true;
            }

            this.rememberConflictingSiegeItemLocator(candidate);
            return false;
        }

        SiegeItemLocator current = this.primarySiegeItemLocator();
        if (current == null) {
            this.setPrimarySiegeItemLocator(server, candidate);
            return true;
        }
        if (sameSiegeItemLocator(candidate, current)) {
            this.refreshPrimarySiegeItemPosition(server, candidate, knownCarrier);
            return true;
        }

        if (this.validateSiegeItemLocator(server, current) == SiegeLocatorState.NOT_FOUND) {
            this.clearPrimarySiegeItemLocator();
            this.setPrimarySiegeItemLocator(server, candidate);
            return true;
        }

        this.rememberConflictingSiegeItemLocator(candidate);
        return false;
    }

    private boolean tryRecoverSiegeConflictOverflow(MinecraftServer server, SiegeItemLocator candidate,
                                                    @Nullable Entity knownCarrier) {
        if (!sameSiegeItemLocator(candidate, this.primarySiegeItemLocator())
                && !sameSiegeItemLocator(candidate, this.conflictingSiegeItemLocator())
                && !sameSiegeItemLocator(candidate, this.overflowSiegeItemLocator())
                && !this.addExtraSiegeItemLocator(candidate))
            return false;
        this.siegeItemConflictOverflow.set(true);
        return this.reconcileSiegeItemLocators(server, candidate, knownCarrier);
    }

    private static void addUniqueSiegeLocator(List<SiegeItemLocator> locators, SiegeItemLocator candidate) {
        if (candidate == null)
            return;
        for (SiegeItemLocator existing : locators) {
            if (sameSiegeItemLocator(existing, candidate))
                return;
        }
        locators.add(candidate);
    }

    private void refreshPrimarySiegeItemPosition(MinecraftServer server, SiegeItemLocator locator,
                                                 @Nullable Entity knownCarrier) {
        if (locator.container() != null) {
            this.updateTrackedSiegePosition(locator.container());
            return;
        }

        Entity entity = knownCarrier != null && !knownCarrier.isRemoved()
                && Objects.equals(knownCarrier.getUuid(), locator.entity())
                ? knownCarrier : this.findSiegeItemEntity(server, locator.entity(), false);
        if (entity == null || !(entity.getWorld() instanceof ServerWorld world))
            return;

        CachedDirectedGlobalPos position = CachedDirectedGlobalPos.create(world,
                BlockPos.ofFloored(entity.getPos()), (byte) 0);
        this.updateTrackedSiegePosition(position);
    }

    private void updateTrackedSiegePosition(CachedDirectedGlobalPos position) {
        // The locator still has to follow nested containers inside the TARDIS so
        // the siege item can be removed later, but an exterior can never use its
        // own interior dimension as its physical position.
        if (this.isOwnInteriorPosition(position)
                || sameLocation(this.tardis.travel().position(), position))
            return;

        this.tardis.travel().forcePosition(position);
    }

    private boolean reconcileSiegeItemLocators(MinecraftServer server) {
        return this.reconcileSiegeItemLocators(server, null, null);
    }

    private boolean reconcileSiegeItemLocatorsForRelocation(MinecraftServer server) {
        return this.reconcileSiegeItemLocators(server, null, null, true);
    }

    private boolean reconcileSiegeItemLocators(MinecraftServer server, @Nullable SiegeItemLocator observed,
                                                @Nullable Entity knownCarrier) {
        return this.reconcileSiegeItemLocators(server, observed, knownCarrier, false);
    }

    private boolean reconcileSiegeItemLocators(MinecraftServer server, @Nullable SiegeItemLocator observed,
                                                @Nullable Entity knownCarrier, boolean loadCarrierChunks) {
        if (server == null)
            return false;
        if (this.isSiegeItemTransferPending(server))
            return false;
        if (this.siegeItemUntrackedOverflow.get()) {
            if (!this.siegeItemConflictOverflow.get())
                this.siegeItemConflictOverflow.set(true);
            return false;
        }

        if (this.siegeItemConflictOverflow.get()) {
            SiegeItemLocator legacyOverflow = this.overflowSiegeItemLocator();
            HashSet<String> extras = this.siegeItemExtraLocators.get();
            if ((extras == null || extras.isEmpty()) && legacyOverflow == null)
                // Older development saves may contain an unrepresentable overflow marker.
                // Keep those fail-closed rather than risk materializing a duplicate exterior.
                return false;

            SiegeItemLocator current = this.primarySiegeItemLocator();
            SiegeItemLocator conflict = this.conflictingSiegeItemLocator();
            List<SiegeItemLocator> tracked = new ArrayList<>();
            boolean malformedLocator = false;
            addUniqueSiegeLocator(tracked, current);
            addUniqueSiegeLocator(tracked, conflict);
            addUniqueSiegeLocator(tracked, legacyOverflow);

            if (extras != null) {
                for (String encoded : extras) {
                    SiegeItemLocator decoded = decodeSiegeItemLocator(encoded);
                    if (decoded == null) {
                        malformedLocator = true;
                        continue;
                    }
                    addUniqueSiegeLocator(tracked, decoded);
                }
            }

            addUniqueSiegeLocator(tracked, observed);
            List<SiegeItemLocator> confirmed = new ArrayList<>(tracked.size());
            Map<String, SiegeLocatorState> states = new HashMap<>(tracked.size());
            boolean unresolved = false;
            for (SiegeItemLocator locator : tracked) {
                SiegeLocatorState state = sameSiegeItemLocator(locator, observed)
                        ? SiegeLocatorState.FOUND
                        : this.validateSiegeItemLocator(server, locator, loadCarrierChunks);
                states.put(encodeSiegeItemLocator(locator), state);
                if (state == SiegeLocatorState.UNAVAILABLE || state == SiegeLocatorState.INCOMPLETE)
                    unresolved = true;
                if (state == SiegeLocatorState.FOUND)
                    addUniqueSiegeLocator(confirmed, locator);
            }

            if (extras != null && !extras.isEmpty()) {
                HashSet<String> retained = new HashSet<>(extras);
                retained.removeIf(encoded -> states.get(encoded) == SiegeLocatorState.NOT_FOUND);
                if (!retained.equals(extras))
                    this.siegeItemExtraLocators.setPersistent(retained);
            }
            if (states.get(encodeSiegeItemLocator(current)) == SiegeLocatorState.NOT_FOUND)
                this.clearPrimarySiegeItemLocator();
            if (states.get(encodeSiegeItemLocator(conflict)) == SiegeLocatorState.NOT_FOUND)
                this.clearConflictingSiegeItemLocator();
            if (states.get(encodeSiegeItemLocator(legacyOverflow)) == SiegeLocatorState.NOT_FOUND)
                this.clearOverflowSiegeItemLocator();

            if (malformedLocator || unresolved || this.siegeItemUntrackedOverflow.get()
                    || confirmed.size() > 2)
                return false;

            this.clearSiegeItemLocators();
            if (!confirmed.isEmpty())
                this.setPrimarySiegeItemLocator(server, confirmed.get(0));
            if (confirmed.size() == 2) {
                this.rememberConflictingSiegeItemLocator(confirmed.get(1));
                return false;
            }

            if (!confirmed.isEmpty() && sameSiegeItemLocator(observed, confirmed.get(0)))
                this.refreshPrimarySiegeItemPosition(server, observed, knownCarrier);
        }

        SiegeItemLocator conflict = this.conflictingSiegeItemLocator();
        if (conflict == null)
            return true;

        SiegeItemLocator current = this.primarySiegeItemLocator();
        SiegeLocatorState conflictState = this.validateSiegeItemLocator(server, conflict, loadCarrierChunks);
        if (current == null) {
            if (conflictState == SiegeLocatorState.NOT_FOUND) {
                this.clearConflictingSiegeItemLocator();
                return true;
            }
            if (conflictState == SiegeLocatorState.FOUND) {
                this.promoteConflictingSiegeItemLocator(server, conflict);
                return true;
            }
            return false;
        }

        SiegeLocatorState currentState = this.validateSiegeItemLocator(server, current, loadCarrierChunks);
        if (currentState == SiegeLocatorState.NOT_FOUND) {
            this.clearPrimarySiegeItemLocator();
            if (conflictState == SiegeLocatorState.NOT_FOUND) {
                this.clearConflictingSiegeItemLocator();
                return true;
            }
            if (conflictState == SiegeLocatorState.FOUND) {
                this.promoteConflictingSiegeItemLocator(server, conflict);
                return true;
            }
            return false;
        }
        if (conflictState == SiegeLocatorState.NOT_FOUND) {
            this.clearConflictingSiegeItemLocator();
            return true;
        }

        return false;
    }

    private void promoteConflictingSiegeItemLocator(MinecraftServer server, SiegeItemLocator conflict) {
        this.clearPrimarySiegeItemLocator();
        this.clearConflictingSiegeItemLocator();
        this.setPrimarySiegeItemLocator(server, conflict);
    }

    private SiegeLocatorState validateSiegeItemLocator(MinecraftServer server, SiegeItemLocator locator) {
        return this.validateSiegeItemLocator(server, locator, false);
    }

    private SiegeLocatorState validateSiegeItemLocator(MinecraftServer server, SiegeItemLocator locator,
                                                        boolean loadCarrierChunk) {
        if (locator == null)
            return SiegeLocatorState.NOT_FOUND;

        if (locator.container() != null) {
            CachedDirectedGlobalPos container = locator.container();
            container.init(server);
            ServerWorld world = container.getWorld();
            if (world == null)
                return SiegeLocatorState.UNAVAILABLE;
            if (!world.isChunkLoaded(container.getPos())) {
                if (!loadCarrierChunk)
                    return SiegeLocatorState.UNAVAILABLE;
                world.getChunk(container.getPos());
            }

            BlockEntity blockEntity = world.getBlockEntity(container.getPos());
            if (!(blockEntity instanceof Inventory inventory))
                return SiegeLocatorState.NOT_FOUND;

            return fromScanResult(SiegeInventoryUtil.scan(inventory, this.tardis.getUuid()));
        }

        Entity entity = this.findSiegeItemEntity(server, locator.entity(), loadCarrierChunk);
        return entity == null ? SiegeLocatorState.UNAVAILABLE
                : fromScanResult(SiegeInventoryUtil.scan(entity, this.tardis.getUuid()));
    }

    private static SiegeLocatorState fromScanResult(SiegeInventoryUtil.ScanResult result) {
        return switch (result) {
            case FOUND -> SiegeLocatorState.FOUND;
            case NOT_FOUND -> SiegeLocatorState.NOT_FOUND;
            case INCOMPLETE, INCOMPLETE_HINTED -> SiegeLocatorState.INCOMPLETE;
        };
    }

    private SiegeItemLocator primarySiegeItemLocator() {
        CachedDirectedGlobalPos container = this.siegeItemContainer.get();
        if (container != null)
            return new SiegeItemLocator(container, null);

        UUID entity = this.tardis.siege().getHeldPlayerUUID();
        return entity == null ? null : new SiegeItemLocator(null, entity);
    }

    private SiegeItemLocator conflictingSiegeItemLocator() {
        CachedDirectedGlobalPos container = this.siegeItemConflictContainer.get();
        if (container != null)
            return new SiegeItemLocator(container, null);

        UUID entity = this.siegeItemConflictEntity.get();
        return entity == null ? null : new SiegeItemLocator(null, entity);
    }

    private SiegeItemLocator overflowSiegeItemLocator() {
        CachedDirectedGlobalPos container = this.siegeItemOverflowContainer.get();
        if (container != null)
            return new SiegeItemLocator(container, null);

        UUID entity = this.siegeItemOverflowEntity.get();
        return entity == null ? null : new SiegeItemLocator(null, entity);
    }

    private void setPrimarySiegeItemLocator(MinecraftServer server, SiegeItemLocator locator) {
        long now = server.getOverworld().getTime();
        this.siegeItemLocatorUpdated.set(now);
        if (locator.container() != null) {
            this.siegeItemContainer.set(locator.container());
            this.tardis.siege().setSiegeBeingHeld(null);
            this.updateTrackedSiegePosition(locator.container());
            return;
        }

        this.siegeItemContainer.set((CachedDirectedGlobalPos) null);
        this.tardis.siege().setSiegeBeingHeld(locator.entity());
        Entity entity = this.findSiegeItemEntity(server, locator.entity(), false);
        if (entity != null && entity.getWorld() instanceof ServerWorld world)
            this.updateTrackedSiegePosition(CachedDirectedGlobalPos.create(world,
                    BlockPos.ofFloored(entity.getPos()), (byte) 0));
    }

    private void rememberConflictingSiegeItemLocator(SiegeItemLocator locator) {
        SiegeItemLocator conflict = this.conflictingSiegeItemLocator();
        if (sameSiegeItemLocator(locator, conflict))
            return;
        if (conflict != null) {
            this.addExtraSiegeItemLocator(locator);
            this.siegeItemConflictOverflow.set(true);
            AITMod.LOGGER.warn("More than two siege item carriers were observed for TARDIS {}; "
                    + "automatic siege placement is blocked until only one remains", this.tardis.getUuid());
            return;
        }

        if (locator.container() != null)
            this.siegeItemConflictContainer.set(locator.container());
        else
            this.siegeItemConflictEntity.set(locator.entity());
        this.nextSiegeConflictValidation = 0;
        AITMod.LOGGER.warn("Conflicting siege item carriers were observed for TARDIS {}; "
                + "automatic siege placement is blocked until one locator is proven empty", this.tardis.getUuid());
    }

    private static boolean sameSiegeItemLocator(SiegeItemLocator first, SiegeItemLocator second) {
        if (first == null || second == null)
            return first == second;
        if (first.entity() != null || second.entity() != null)
            return Objects.equals(first.entity(), second.entity());
        return first.container().getDimension().equals(second.container().getDimension())
                && first.container().getPos().equals(second.container().getPos());
    }

    private void clearPrimarySiegeItemLocator() {
        this.siegeItemContainer.set((CachedDirectedGlobalPos) null);
        this.tardis.siege().setSiegeBeingHeld(null);
        this.siegeItemLocatorUpdated.set(0L);
    }

    private void clearConflictingSiegeItemLocator() {
        this.siegeItemConflictContainer.set((CachedDirectedGlobalPos) null);
        this.siegeItemConflictEntity.set((UUID) null);
        this.nextSiegeConflictValidation = 0;
    }

    private void clearOverflowSiegeItemLocator() {
        this.siegeItemOverflowContainer.set((CachedDirectedGlobalPos) null);
        this.siegeItemOverflowEntity.set((UUID) null);
    }

    private void migrateLegacyOverflowSiegeItemLocator() {
        SiegeItemLocator legacy = this.overflowSiegeItemLocator();
        if (legacy == null)
            return;

        this.addExtraSiegeItemLocator(legacy);
        this.siegeItemConflictOverflow.setPersistent(true);
    }

    private boolean addExtraSiegeItemLocator(SiegeItemLocator locator) {
        String encoded = encodeSiegeItemLocator(locator);
        if (encoded == null)
            return false;

        HashSet<String> updated = this.siegeItemExtraLocators.get() == null
                ? new HashSet<>() : new HashSet<>(this.siegeItemExtraLocators.get());
        if (updated.contains(encoded))
            return true;
        if (updated.size() >= MAX_SIEGE_ITEM_EXTRA_LOCATORS) {
            if (!this.siegeItemUntrackedOverflow.get())
                AITMod.LOGGER.error("Too many siege item carriers were observed for TARDIS {}; "
                        + "automatic siege placement remains fail-closed", this.tardis.getUuid());
            this.siegeItemUntrackedOverflow.setPersistent(true);
            return false;
        }

        updated.add(encoded);
        this.siegeItemExtraLocators.setPersistent(updated);
        return true;
    }

    private boolean removeExtraSiegeItemLocator(SiegeItemLocator locator) {
        String encoded = encodeSiegeItemLocator(locator);
        HashSet<String> current = this.siegeItemExtraLocators.get();
        if (encoded == null || current == null || !current.contains(encoded))
            return false;

        HashSet<String> updated = new HashSet<>(current);
        updated.remove(encoded);
        this.siegeItemExtraLocators.setPersistent(updated);
        if (updated.isEmpty() && this.overflowSiegeItemLocator() == null
                && !this.siegeItemUntrackedOverflow.get())
            this.siegeItemConflictOverflow.set(false);
        return true;
    }

    private static @Nullable String encodeSiegeItemLocator(@Nullable SiegeItemLocator locator) {
        if (locator == null)
            return null;
        if (locator.entity() != null)
            return "entity|" + locator.entity();
        if (locator.container() == null)
            return null;

        CachedDirectedGlobalPos container = locator.container();
        BlockPos pos = container.getPos();
        return "container|" + container.getDimension().getValue() + "|" + pos.getX() + "|"
                + pos.getY() + "|" + pos.getZ();
    }

    private static @Nullable SiegeItemLocator decodeSiegeItemLocator(@Nullable String encoded) {
        if (encoded == null)
            return null;

        String[] parts = encoded.split("\\|", -1);
        try {
            if (parts.length == 2 && "entity".equals(parts[0]))
                return new SiegeItemLocator(null, UUID.fromString(parts[1]));
            if (parts.length != 5 || !"container".equals(parts[0]))
                return null;

            Identifier dimensionId = Identifier.tryParse(parts[1]);
            if (dimensionId == null)
                return null;
            RegistryKey<World> dimension = RegistryKey.of(RegistryKeys.WORLD, dimensionId);
            BlockPos pos = new BlockPos(Integer.parseInt(parts[2]), Integer.parseInt(parts[3]),
                    Integer.parseInt(parts[4]));
            return new SiegeItemLocator(CachedDirectedGlobalPos.create(dimension, pos, (byte) 0), null);
        } catch (IllegalArgumentException exception) {
            return null;
        }
    }

    private void clearSiegeItemLocators() {
        this.clearPrimarySiegeItemLocator();
        this.clearConflictingSiegeItemLocator();
        this.clearOverflowSiegeItemLocator();
        this.siegeItemExtraLocators.setPersistent(new HashSet<>());
        this.siegeItemConflictOverflow.set(false);
        this.siegeItemUntrackedOverflow.setPersistent(false);
        this.clearSiegeItemTransferPending();
    }

    public void trackSiegeItemContainer(ServerWorld world, BlockPos pos) {
        if (world == null || pos == null)
            return;

        CachedDirectedGlobalPos location = CachedDirectedGlobalPos.create(world, pos, (byte) 0);
        if (this.observeSiegeItemLocator(world.getServer(), new SiegeItemLocator(location, null)))
            this.clearSiegeItemTransferPending();
    }

    public void trackSiegeItemEntity(Entity entity) {
        if (!(entity.getWorld() instanceof ServerWorld world)
                || entity.isRemoved() && entity.getRemovalReason() != null
                && entity.getRemovalReason().shouldDestroy())
            return;

        if (this.observeSiegeItemLocator(world.getServer(), new SiegeItemLocator(null, entity.getUuid()), entity))
            this.clearSiegeItemTransferPending();
    }

    public void forgetSiegeItemContainer(ServerWorld world, BlockPos pos) {
        if (world == null || pos == null)
            return;

        this.forgetSiegeItemLocator(world.getServer(),
                new SiegeItemLocator(CachedDirectedGlobalPos.create(world, pos, (byte) 0), null));
    }

    public void forgetSiegeItemEntity(Entity entity) {
        if (!(entity.getWorld() instanceof ServerWorld world))
            return;

        this.forgetSiegeItemEntity(world, entity.getUuid());
    }

    public void forgetSiegeItemEntity(ServerWorld world, UUID entityId) {
        if (world == null || entityId == null)
            return;

        this.forgetSiegeItemLocator(world.getServer(), new SiegeItemLocator(null, entityId));
    }

    private void forgetSiegeItemLocator(MinecraftServer server, SiegeItemLocator locator) {
        if (server == null || locator == null)
            return;

        SiegeItemLocator current = this.primarySiegeItemLocator();
        SiegeItemLocator conflict = this.conflictingSiegeItemLocator();
        SiegeItemLocator overflow = this.overflowSiegeItemLocator();
        boolean removedExtra = this.removeExtraSiegeItemLocator(locator);
        boolean tracked = removedExtra || sameSiegeItemLocator(locator, current)
                || sameSiegeItemLocator(locator, conflict) || sameSiegeItemLocator(locator, overflow);
        if (tracked)
            this.markSiegeItemTransferPending(server);
        if (sameSiegeItemLocator(locator, current)) {
            this.clearPrimarySiegeItemLocator();
            if (this.siegeItemConflictOverflow.get()) {
                this.reconcileSiegeItemLocators(server);
            } else if (conflict != null
                    && this.validateSiegeItemLocator(server, conflict) == SiegeLocatorState.FOUND)
                this.promoteConflictingSiegeItemLocator(server, conflict);
        } else if (sameSiegeItemLocator(locator, conflict)) {
            this.clearConflictingSiegeItemLocator();
            if (this.siegeItemConflictOverflow.get())
                this.reconcileSiegeItemLocators(server);
        } else if (sameSiegeItemLocator(locator, overflow)) {
            this.clearOverflowSiegeItemLocator();
            HashSet<String> extras = this.siegeItemExtraLocators.get();
            if ((extras == null || extras.isEmpty()) && !this.siegeItemUntrackedOverflow.get())
                this.siegeItemConflictOverflow.set(false);
            this.reconcileSiegeItemLocators(server);
        } else if (removedExtra) {
            this.reconcileSiegeItemLocators(server);
        }
    }

    public boolean isTrackedSiegeItemContainer(ServerWorld world, BlockPos pos) {
        CachedDirectedGlobalPos current = this.siegeItemContainer.get();
        return world != null && pos != null && current != null
                && current.getDimension().equals(world.getRegistryKey()) && current.getPos().equals(pos);
    }

    public void clearSiegeItemContainer() {
        this.siegeItemContainer.set((CachedDirectedGlobalPos) null);
        this.siegeItemLocatorUpdated.set(0L);
    }

    public boolean canCreateSiegeItem(MinecraftServer server) {
        if (this.siegeItemUntrackedOverflow.get())
            return false;
        if (!this.reconcileSiegeItemLocators(server))
            return false;

        SiegeItemLocator current = this.primarySiegeItemLocator();
        if (current == null)
            return true;

        if (this.validateSiegeItemLocator(server, current) != SiegeLocatorState.NOT_FOUND)
            return false;

        this.clearPrimarySiegeItemLocator();
        return true;
    }

    public boolean canMaterializeSiegeExterior(MinecraftServer server) {
        return server != null && !this.siegeItemUntrackedOverflow.get()
                && this.reconcileSiegeItemLocators(server)
                && this.primarySiegeItemLocator() == null && this.conflictingSiegeItemLocator() == null
                && !this.siegeItemConflictOverflow.get();
    }

    public @Nullable CachedDirectedGlobalPos resolveSiegeExteriorPlacement(
            @Nullable CachedDirectedGlobalPos requested) {
        if (!this.isOwnInteriorPosition(requested))
            return requested;

        CachedDirectedGlobalPos home = this.tardis.stats().getHome();
        return this.isOwnInteriorPosition(home) ? null : home;
    }

    public boolean isOwnInteriorPosition(@Nullable CachedDirectedGlobalPos position) {
        return position != null && TardisServerWorld.isTardisDimension(position.getDimension())
                && this.tardis.getUuid().toString().equals(position.getDimension().getValue().getPath());
    }

    public boolean prepareSiegeExteriorPlacement(MinecraftServer server, Entity carrier) {
        if (server == null || carrier == null || !(carrier.getWorld() instanceof ServerWorld))
            return false;

        if (this.observeSiegeItemLocator(server, new SiegeItemLocator(null, carrier.getUuid()), carrier))
            this.clearSiegeItemTransferPending();
        return this.prepareSiegeExteriorPlacement(server, false);
    }

    public boolean prepareSiegeExteriorPlacement(MinecraftServer server) {
        return this.prepareSiegeExteriorPlacement(server,
                this.tardis.getExterior().hasValidExteriorBlock());
    }

    public boolean prepareSiegeExteriorPlacement(MinecraftServer server, boolean exteriorExistedBeforePlacement) {
        if (server == null || !this.reconcileSiegeItemLocators(server))
            return false;

        if (server != null)
            this.hasStoredSiegeItem(server);

        boolean hasItemLocator = this.siegeItemContainer.get() != null
                || this.tardis.siege().getHeldPlayerUUID() != null;
        if (hasItemLocator) {
            if (server == null || !this.removeSiegeItem(server))
                return false;

            this.clearSiegeItemLocators();
            return true;
        }

        return exteriorExistedBeforePlacement && this.tardis.getExterior().hasValidExteriorBlock();
    }

    private Entity findSiegeItemEntity(MinecraftServer server, UUID entityId, boolean loadPositionChunk) {
        if (server == null || entityId == null)
            return null;

        ServerPlayerEntity player = server.getPlayerManager().getPlayer(entityId);
        if (player != null)
            return player;

        for (ServerWorld world : server.getWorlds()) {
            Entity entity = world.getEntity(entityId);
            if (entity != null && !entity.isRemoved())
                return entity;
        }

        CachedDirectedGlobalPos position = this.tardis.travel().position();
        if (position == null)
            return null;

        position.init(server);
        ServerWorld world = position.getWorld();
        if (world == null)
            return null;

        if (!world.isChunkLoaded(position.getPos())) {
            if (!loadPositionChunk)
                return null;
            world.getChunk(position.getPos());
        }
        Entity entity = world.getEntity(entityId);
        return entity == null || entity.isRemoved() ? null : entity;
    }

    private void chargeDirectAutomaticTravel(CachedDirectedGlobalPos source, CachedDirectedGlobalPos destination) {
        double cost = TravelUtil.getNominalFlightFuelCost(this.tardis.travel(), source, destination);
        double available = this.tardis.getFuel();
        this.tardis.fuel().setCurrentFuelSilently(Math.max(available - cost, 0));

        if (cost >= available)
            this.tardis.fuel().disablePower();
    }

    private void relocateDirectlyToHome() {
        MinecraftServer server = ServerLifecycleHooks.get();
        if (server == null || !this.reconcileSiegeItemLocators(server))
            return;

        CachedDirectedGlobalPos home = this.tardis.stats().getHome();
        if (home == null)
            return;

        home.init(server);
        ServerWorld world = home.getWorld();
        if (world == null || !world.isInBuildLimit(home.getPos())
                || !world.isInBuildLimit(home.getPos().up()))
            return;

        TravelHandler travel = this.tardis.travel();
        boolean movingExterior = this.tardis.getExterior().hasValidExteriorBlock();
        if (this.tardis.siege().isActive()
                && !this.tardis.returnHome().canMaterializeSiegeExterior(server))
            return;

        travel.clearLandingObstructions(home);
        TravelHandler.ProvisionalExterior provisional = travel.placeProvisionalExterior(home);
        if (provisional == null)
            return;
        if (movingExterior && !travel.tryDeleteExterior()) {
            provisional.rollback();
            return;
        }

        travel.forceDestination(home);
        travel.forcePosition(home);
        this.exteriorVerified = true;
    }

    private boolean startHailMaryHomeReturn() {
        CachedDirectedGlobalPos home = this.tardis.stats().getHome();
        if (home == null)
            return false;

        this.hailMaryExactLanding.set(true);
        boolean started = this.startAutomaticTravel(home, AutomaticTravel.HAIL_MARY_RETURN,
                true, false);
        if (!started)
            this.hailMaryExactLanding.set(false);

        return started;
    }

    private boolean shouldReturnHome(boolean ignoreHandbrake) {
        TravelHandler travel = this.tardis.travel();
        CachedDirectedGlobalPos position = travel.position();
        CachedDirectedGlobalPos home = this.tardis.stats().getHome();

        if (position == null || home == null || (!ignoreHandbrake && travel.handbrake()))
            return false;

        if (!position.getDimension().equals(home.getDimension()))
            return true;

        int radius = homeRadius();
        return position.getPos().getSquaredDistance(home.getPos()) > radius * radius;
    }

    private long currentTime() {
        MinecraftServer server = ServerLifecycleHooks.get();
        return server == null ? 0L : server.getOverworld().getTime();
    }

    private void clearAutomaticTravel() {
        boolean previousAutopilot = this.automaticPreviousAutopilot.get();
        this.automaticTravel.set(AutomaticTravel.NONE);
        this.automaticSetupComplete.set(false);
        this.automaticPreviousAutopilot.set(false);
        this.homeLanding.set(false);
        this.shutdownOnArrival.set(false);
        this.crashOnArrival.set(false);
        this.deferredFlightCost.set(0d);
        this.tardis.travel().autopilot(previousAutopilot);
    }

    private static boolean sameLocation(CachedDirectedGlobalPos first, CachedDirectedGlobalPos second) {
        return first != null && second != null && first.getDimension().equals(second.getDimension())
                && first.getPos().equals(second.getPos());
    }

    private static void onForcedEntry(dev.amble.ait.core.tardis.Tardis tardis, Entity entity) {
        if (!(entity instanceof ServerPlayerEntity player) || tardis.fuel().hasPower()
                || tardis.loyalty().get(player).isOf(Loyalty.Type.PILOT))
            return;

        if (!tardis.returnHome().canReturnHome())
            return;

        tardis.returnHome().forcedEntryPlayer.set(player.getUuid());
    }

    public enum AutomaticTravel {
        NONE(false, false, false),
        INSTANT(true, false, true),
        FREE(true, false, false),
        NORMAL(false, false, false),
        HAIL_MARY_RETURN(false, false, false),
        DEFERRED_NORMAL(false, false, true),
        ARTRON_DUMP(false, false, false);

        private final boolean instant;
        private final boolean chargesNominalCost;
        private final boolean defersNominalCost;

        AutomaticTravel(boolean instant, boolean chargesNominalCost, boolean defersNominalCost) {
            this.instant = instant;
            this.chargesNominalCost = chargesNominalCost;
            this.defersNominalCost = defersNominalCost;
        }

        public boolean isInstant() {
            return instant;
        }

        public boolean chargesNominalCost() {
            return chargesNominalCost;
        }

        public boolean defersNominalCost() {
            return defersNominalCost;
        }
    }

    private record SiegeItemLocator(CachedDirectedGlobalPos container, UUID entity) {
    }

    private enum SiegeLocatorState {
        FOUND,
        NOT_FOUND,
        INCOMPLETE,
        UNAVAILABLE
    }

    private record BossTarget(UUID entityId, ChunkPos chunk) {
        private static final char SEPARATOR = ';';

        private String encode() {
            return this.entityId + String.valueOf(SEPARATOR) + this.chunk.toLong();
        }

        private static BossTarget decode(String encoded) {
            if (encoded == null)
                return null;

            int separator = encoded.lastIndexOf(SEPARATOR);
            if (separator <= 0 || separator >= encoded.length() - 1)
                return null;

            try {
                UUID entityId = UUID.fromString(encoded.substring(0, separator));
                long chunk = Long.parseLong(encoded.substring(separator + 1));
                return new BossTarget(entityId, new ChunkPos(chunk));
            } catch (IllegalArgumentException ignored) {
                return null;
            }
        }
    }

    private enum BossEscapeState {
        NONE,
        FLEEING_HOME,
        WAITING_AT_HOME,
        RETURNING_TO_ORIGIN
    }
}
