package dev.amble.ait.core.tardis.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import dev.amble.ait.AITMod;
import dev.amble.ait.api.TeleportAware;
import dev.amble.ait.api.tardis.KeyedTardisComponent;
import dev.amble.ait.api.tardis.TardisEvents;
import dev.amble.ait.api.tardis.TardisTickable;
import dev.amble.ait.api.tardis.link.v2.TardisRef;
import dev.amble.ait.compat.DependencyChecker;
import dev.amble.ait.compat.gravity.GravityHandler;
import dev.amble.ait.core.AITItems;
import dev.amble.ait.core.AITTags;
import dev.amble.ait.core.engine.block.generic.GenericStructureSystemBlockEntity;
import dev.amble.ait.core.engine.link.tracker.WorldFluidTracker;
import dev.amble.ait.core.item.KeyItem;
import dev.amble.ait.core.tardis.ServerTardis;
import dev.amble.ait.core.tardis.handler.travel.TravelHandler;
import dev.amble.ait.core.tardis.handler.travel.TravelHandlerBase;
import dev.amble.ait.core.tardis.manager.ServerTardisManager;
import dev.amble.ait.core.tardis.util.TardisUtil;
import dev.amble.ait.core.util.StackUtil;
import dev.amble.ait.data.Exclude;
import dev.amble.ait.data.Loyalty;
import dev.amble.ait.data.properties.Property;
import dev.amble.ait.data.properties.Value;
import dev.amble.ait.data.properties.bool.BoolProperty;
import dev.amble.ait.data.properties.bool.BoolValue;
import dev.amble.ait.data.schema.exterior.ExteriorVariantSchema;
import dev.amble.ait.registry.impl.exterior.ExteriorVariantRegistry;
import dev.amble.lib.data.CachedDirectedGlobalPos;
import dev.amble.lib.util.ServerLifecycleHooks;

import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

/**
 * Optional antagonistic behaviours which are only aimed at players the TARDIS
 * explicitly rejects. Expensive exterior checks are staggered and only inspect
 * players in an already-loaded world.
 */
public class TardisTemperamentHandler extends KeyedTardisComponent implements TardisTickable {

    private static final int LIFE_SUPPORT_SEARCH_LIMIT = 2_048;

    private static final Property<CachedDirectedGlobalPos> PENDING_HOP = new Property<>(
            Property.CDIRECTED_GLOBAL_POS, "pending_hop", (CachedDirectedGlobalPos) null);
    private static final Property<UUID> PENDING_HOP_PLAYER = new Property<>(Property.UUID, "pending_hop_player");
    private static final BoolProperty UNSAFE_LANDING = new BoolProperty("unsafe_landing", false);
    private static final Property<UUID> UNSAFE_LANDING_HOSTILE = new Property<>(
            Property.UUID, "unsafe_landing_hostile");
    private static final BoolProperty TEMPORARY_LEFT_BEHIND = new BoolProperty("temporary_left_behind", false);
    private static final BoolProperty TEMPORARY_LEFT_BEHIND_AUTOPILOT = new BoolProperty(
            "temporary_left_behind_autopilot", false);
    private static final BoolProperty REJECT_ENTRY_BLOCKED = new BoolProperty("reject_entry_blocked", false);
    private static final Property<Identifier> PREVIOUS_EXTERIOR = new Property<>(
            Property.IDENTIFIER, "previous_exterior");
    private static final BoolProperty PREVIOUS_CLOAKED = new BoolProperty("previous_cloaked", false);
    private static final BoolProperty PREVIOUS_CLOAK_SILENT = new BoolProperty("previous_cloak_silent", false);

    private final Value<CachedDirectedGlobalPos> pendingHop = PENDING_HOP.create(this);
    private final Value<UUID> pendingHopPlayer = PENDING_HOP_PLAYER.create(this);
    private final BoolValue unsafeLanding = UNSAFE_LANDING.create(this);
    private final Value<UUID> unsafeLandingHostile = UNSAFE_LANDING_HOSTILE.create(this);
    private final BoolValue temporaryLeftBehind = TEMPORARY_LEFT_BEHIND.create(this);
    private final BoolValue temporaryLeftBehindAutopilot = TEMPORARY_LEFT_BEHIND_AUTOPILOT.create(this);
    private final BoolValue rejectEntryBlocked = REJECT_ENTRY_BLOCKED.create(this);
    private final Value<Identifier> previousExterior = PREVIOUS_EXTERIOR.create(this);
    private final BoolValue previousCloaked = PREVIOUS_CLOAKED.create(this);
    private final BoolValue previousCloakSilent = PREVIOUS_CLOAK_SILENT.create(this);

    @Exclude
    private final Map<UUID, ApproachSample> rejectApproachSamples = new HashMap<>();
    @Exclude
    private ApproachSample pendingHopApproach;
    @Exclude
    private final Map<UUID, Long> linkedKeyThreatTicks = new HashMap<>();
    @Exclude(strategy = Exclude.Strategy.NETWORK)
    private Map<UUID, Long> lastCompanionPresence = new HashMap<>();

    static {
        TardisEvents.ENTER_FLIGHT.register(tardis -> tardis.temperament().onEnterFlight());
        TardisEvents.LANDED.register(tardis -> tardis.temperament().onLanded());
        TardisEvents.DOOR_OPEN.register(tardis -> tardis.temperament().rejectEntryBlocked.set(false));
        TardisEvents.ENTER_TARDIS.register((tardis, entity) -> {
            if (!(entity instanceof ServerPlayerEntity player))
                return TardisEvents.Interaction.PASS;
            if (tardis.temperament().preventEntry(player))
                return TardisEvents.Interaction.FAIL;
            tardis.temperament().onConfirmedPlayerEntered(player);
            return TardisEvents.Interaction.PASS;
        });
        TardisEvents.LEAVE_TARDIS.register((tardis, entity) -> {
            if (entity instanceof ServerPlayerEntity player) {
                tardis.temperament().forgetRejectApproach(player.getUuid());
                tardis.temperament().recordCompanionPresence(player);
            }
        });
    }

    public TardisTemperamentHandler() {
        super(Id.TEMPERAMENT);
    }

    @Override
    public void onLoaded() {
        if (this.lastCompanionPresence == null)
            this.lastCompanionPresence = new HashMap<>();
        this.pendingHop.of(this, PENDING_HOP);
        this.pendingHopPlayer.of(this, PENDING_HOP_PLAYER);
        this.unsafeLanding.of(this, UNSAFE_LANDING);
        this.unsafeLandingHostile.of(this, UNSAFE_LANDING_HOSTILE);
        this.temporaryLeftBehind.of(this, TEMPORARY_LEFT_BEHIND);
        this.temporaryLeftBehindAutopilot.of(this, TEMPORARY_LEFT_BEHIND_AUTOPILOT);
        this.rejectEntryBlocked.of(this, REJECT_ENTRY_BLOCKED);
        this.previousExterior.of(this, PREVIOUS_EXTERIOR);
        this.previousCloaked.of(this, PREVIOUS_CLOAKED);
        this.previousCloakSilent.of(this, PREVIOUS_CLOAK_SILENT);
    }

    @Override
    public void postInit(InitContext ctx) {
        if (this.isClient())
            return;

        // A temperament hop is only valid for the continuous exterior approach that
        // queued it, so it must never survive a reload.
        if (this.pendingHop.get() != null || this.pendingHopPlayer.get() != null)
            this.clearPendingHop();

        // ENTER_FLIGHT and its security protocol complete in the same tick. A
        // persisted temporary flag therefore means a save interrupted cleanup.
        if (this.temporaryLeftBehind.get()) {
            this.tardis.travel().leaveBehind().set(false);
            this.temporaryLeftBehind.set(false);
        }

        if (this.temporaryLeftBehindAutopilot.get()) {
            TravelHandlerBase.State state = this.tardis.travel().getState();
            if (state == TravelHandlerBase.State.FLIGHT || state == TravelHandlerBase.State.MAT) {
                this.tardis.travel().autopilot(true);
            } else {
                this.restoreTemporaryLeftBehindAutopilot();
            }
        }
    }

    @Override
    public void tick(MinecraftServer server) {
        if (this.temporaryLeftBehindAutopilot.get() && !this.tardis.travel().isLanded()
                && !this.tardis.travel().autopilot())
            this.tardis.travel().autopilot(true);

        this.tryStartPendingHop();
        this.tickRejectApproaches(server);

        int bucket = Math.floorMod(this.tardis.getUuid().hashCode(), 20);
        if (server.getTicks() % 20 == bucket) {
            long staleBefore = server.getTicks() - 2L;
            this.linkedKeyThreatTicks.values().removeIf(lastTick -> lastTick < staleBefore);
            this.recordCompanionPresence(server);
            this.tickRejectApproach(server);
            this.tickPassiveLoyaltyLoss();
        }

        this.tickHomeOverlap(server);
    }

    public boolean needsTick() {
        return this.isEnabled() || this.pendingHop.get() != null || this.unsafeLanding.get()
                || this.temporaryLeftBehind.get() || this.temporaryLeftBehindAutopilot.get()
                || !this.rejectApproachSamples.isEmpty();
    }

    public boolean isEnabled() {
        return AITMod.CONFIG.tardisTemperament;
    }

    public boolean isReject(UUID player) {
        return player != null && this.tardis.loyalty().get(player).type() == Loyalty.Type.REJECT;
    }

    public boolean isReject(ServerPlayerEntity player) {
        return player != null && this.isReject(player.getUuid());
    }

    public boolean blocksTelepathicUse(ServerPlayerEntity player, ItemStack stack) {
        return this.isEnabled() && player != null && stack != null && !stack.isOf(Items.NETHER_STAR)
                && this.tardis.loyalty().get(player).level() == Loyalty.Type.REJECT.level;
    }

    public void rejectTelepathicUse(ServerPlayerEntity player, ServerWorld world, BlockPos console) {
        Vec3d center = Vec3d.ofCenter(console);
        double x = player.getX() - center.x;
        double z = player.getZ() - center.z;
        double length = Math.sqrt(x * x + z * z);

        if (length < 0.001) {
            double angle = AITMod.RANDOM.nextDouble() * Math.PI * 2;
            x = Math.cos(angle);
            z = Math.sin(angle);
            length = 1;
        }

        double horizontal = AITMod.CONFIG.temperamentConsoleRejectionPushHorizontal;
        player.addVelocity(x / length * horizontal, AITMod.CONFIG.temperamentConsoleRejectionPushVertical,
                z / length * horizontal);
        player.velocityDirty = true;
        player.velocityModified = true;
        world.spawnParticles(ParticleTypes.ANGRY_VILLAGER, center.x, center.y + 0.75, center.z,
                20, 0.8, 0.6, 0.8, 0.05);
    }

    public void onConfirmedPlayerEntered(ServerPlayerEntity player) {
        if (player == null)
            return;

        ItemStack handles = this.tardis.butler().getHandles();
        if (handles != null && !handles.isEmpty())
            this.tryWarnRejectedPlayer(player);

        if (!this.tardis.loyalty().get(player).isOf(Loyalty.Type.COMPANION))
            return;

        MinecraftServer server = player.getServer();
        if (server == null)
            return;

        long now = server.getOverworld().getTime();
        Long previous = this.rememberCompanionPresence(player.getUuid(), now);
        if (!this.isEnabled() || previous == null || now < previous)
            return;

        long delay = Math.max(0L, (long) AITMod.CONFIG.temperamentJealousyAbsenceMinutes) * 60L * 20L;
        if (delay == 0 || now - previous < delay)
            return;

        ServerTardisManager manager = ServerTardisManager.getInstance();
        if (manager == null)
            return;

        int[] totalPenalty = {0};
        manager.forEach(other -> {
            if (other == null || other.isRemoved() || other.getUuid() == null
                    || other.getUuid().equals(this.tardis.getUuid()))
                return;

            Loyalty otherLoyalty = other.loyalty().get(player);
            if (!otherLoyalty.isOf(Loyalty.Type.COMPANION))
                return;

            totalPenalty[0] += Math.max(0, otherLoyalty.level() > this.tardis.loyalty().get(player).level()
                    ? AITMod.CONFIG.temperamentJealousyHigherPenalty
                    : AITMod.CONFIG.temperamentJealousySameOrLowerPenalty);
        });

        if (totalPenalty[0] > 0)
            this.tardis.loyalty().subLevel(player, totalPenalty[0]);
    }

    /**
     * Gives Handles its temperament warning without coupling it to the exterior's home
     * position. Callers are responsible for ensuring Handles is available for the
     * interaction.
     */
    public boolean tryWarnRejectedPlayer(ServerPlayerEntity player) {
        if (player == null)
            return false;

        Loyalty loyalty = this.tardis.loyalty().get(player);
        if (loyalty.type() != Loyalty.Type.REJECT)
            return false;

        int warningChance = loyalty.level() == Loyalty.Type.REJECT.level
                ? AITMod.CONFIG.handlesZeroLoyaltyRejectWarningChance
                : AITMod.CONFIG.handlesRejectWarningChance;
        if (!chance(warningChance))
            return false;

        player.sendMessage(Text.literal("<Handles> ")
                .append(Text.translatable("message.ait.handles.reject_warning")), false);
        return true;
    }

    private void recordCompanionPresence(ServerPlayerEntity player) {
        if (player == null || !this.tardis.loyalty().get(player).isOf(Loyalty.Type.COMPANION))
            return;

        MinecraftServer server = player.getServer();
        if (server != null)
            this.rememberCompanionPresence(player.getUuid(), server.getOverworld().getTime());
    }

    private void recordCompanionPresence(MinecraftServer server) {
        if (!this.tardis.asServer().hasWorld())
            return;

        long now = server.getOverworld().getTime();
        for (ServerPlayerEntity player : this.tardis.asServer().world().getPlayers()) {
            if (this.tardis.loyalty().get(player).isOf(Loyalty.Type.COMPANION))
                this.rememberCompanionPresence(player.getUuid(), now);
        }
    }

    private Long rememberCompanionPresence(UUID player, long time) {
        Long previous = this.lastCompanionPresence.put(player, time);
        if (previous == null || previous.longValue() != time)
            this.markPersistentStateDirty();
        return previous;
    }

    private void markPersistentStateDirty() {
        this.sync();
    }

    private void tickPassiveLoyaltyLoss() {
        if (!this.isEnabled() || !this.tardis.asServer().hasWorld() || !this.isOutsideHomeRadius())
            return;

        for (ServerPlayerEntity player : this.tardis.asServer().world().getPlayers()) {
            Loyalty loyalty = this.tardis.loyalty().get(player);
            if (loyalty.type() != Loyalty.Type.REJECT || loyalty.level() == Loyalty.Type.REJECT.level
                    || this.hasLinkedKey(player))
                continue;

            if (chance(AITMod.CONFIG.temperamentPassiveLoyaltyLossChance))
                this.tardis.loyalty().subLevel(player,
                        Math.max(0, AITMod.CONFIG.temperamentPassiveLoyaltyLoss));
        }
    }

    private boolean hasLinkedKey(ServerPlayerEntity player) {
        if (player == null)
            return false;

        for (int slot = 0; slot < player.getInventory().size(); slot++) {
            ItemStack stack = player.getInventory().getStack(slot);
            if (stack.getItem() instanceof KeyItem key && key.isOf(stack, this.tardis))
                return true;
        }
        return false;
    }

    private void tickHomeOverlap(MinecraftServer server) {
        if (!this.isEnabled() || !this.isAtExactHome() || !this.tardis.travel().isLanded())
            return;

        int interval = Math.max(1, AITMod.CONFIG.temperamentHomeOverlapPenaltyIntervalSeconds) * 20;
        int bucket = Math.floorMod(this.tardis.getUuid().hashCode(), interval);
        if (server.getTicks() % interval != bucket)
            return;

        int penalty = Math.max(0, AITMod.CONFIG.temperamentHomeOverlapLoyaltyPenalty);
        ServerTardisManager manager = ServerTardisManager.getInstance();
        if (penalty == 0 || manager == null)
            return;

        CachedDirectedGlobalPos home = this.tardis.stats().getHome();
        if (home == null || home.getDimension() == null || home.getPos() == null)
            return;

        double radius = homeRadius() * 2.0;
        double radiusSquared = radius * radius;
        manager.forEach(other -> {
            if (other == null || other.isRemoved() || other.getUuid() == null
                    || other.getUuid().equals(this.tardis.getUuid())
                    || this.tardis.getUuid().toString().compareTo(other.getUuid().toString()) >= 0
                    || !other.travel().isLanded() || !isAtExactHome(other))
                return;

            CachedDirectedGlobalPos otherHome = other.stats().getHome();
            if (otherHome == null || otherHome.getDimension() == null || otherHome.getPos() == null
                    || !home.getDimension().equals(otherHome.getDimension())
                    || home.getPos().getSquaredDistance(otherHome.getPos()) > radiusSquared)
                return;

            for (UUID playerId : new ArrayList<>(this.tardis.loyalty().data().keySet())) {
                if (!this.tardis.loyalty().get(playerId).isOf(Loyalty.Type.COMPANION)
                        || !other.loyalty().get(playerId).isOf(Loyalty.Type.COMPANION))
                    continue;

                this.tardis.loyalty().subLevel(playerId, penalty);
                other.loyalty().subLevel(playerId, penalty);
            }
        });
    }

    private void tickRejectApproaches(MinecraftServer server) {
        if (this.rejectApproachSamples.isEmpty())
            return;

        CachedDirectedGlobalPos position = this.getApproachPosition();
        if (!this.isEnabled() || position == null) {
            this.rejectApproachSamples.clear();
            return;
        }

        Iterator<Map.Entry<UUID, ApproachSample>> iterator = this.rejectApproachSamples.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<UUID, ApproachSample> entry = iterator.next();
            ServerPlayerEntity player = server.getPlayerManager().getPlayer(entry.getKey());
            if (!this.isReject(player) || player.getServerWorld() != position.getWorld()
                    || !isWithin(player, position, approachTrackingRadius())) {
                iterator.remove();
                continue;
            }

            ApproachSample current = ApproachSample.create(position, player, server.getTicks());
            if (this.isContinuousApproach(entry.getValue(), current, player)
                    && this.canHop(position) && this.pendingHop.get() == null
                    && chance(AITMod.CONFIG.temperamentHopChance))
                this.queueHopAwayFrom(position, player, current);

            entry.setValue(current);
        }
    }

    private void tickRejectApproach(MinecraftServer server) {
        if (!this.isEnabled()) {
            this.rejectApproachSamples.clear();
            this.clearPendingHop();
            this.restoreTemporaryCamouflage();
            return;
        }

        CachedDirectedGlobalPos position = this.getApproachPosition();
        if (position == null) {
            this.rejectApproachSamples.clear();
            return;
        }

        if (this.hasNearbyLoyalty(position, companionRadius(), Loyalty.Type.COMPANION))
            this.restoreTemporaryCamouflage();

        for (ServerPlayerEntity player : position.getWorld().getPlayers()) {
            if (!this.isReject(player) || !isWithin(player, position, approachTrackingRadius()))
                continue;

            this.rejectApproachSamples.putIfAbsent(player.getUuid(),
                    ApproachSample.create(position, player, server.getTicks()));
        }
    }

    private CachedDirectedGlobalPos getApproachPosition() {
        TravelHandler travel = this.tardis.travel();
        TravelHandlerBase.State state = travel.getState();
        CachedDirectedGlobalPos position = travel.position();
        if ((state != TravelHandlerBase.State.LANDED && state != TravelHandlerBase.State.MAT)
                || position == null || position.getWorld() == null
                || !position.getWorld().isChunkLoaded(position.getPos()))
            return null;

        return position;
    }

    private boolean canHop(CachedDirectedGlobalPos position) {
        TravelHandler travel = this.tardis.travel();
        return !travel.handbrake() && !this.tardis.siege().isActive()
                && this.hasValidExteriorBlock() && !this.hasInteriorPlayers()
                && this.isOutsideHomeRadius()
                && !this.hasNearbyLoyalty(position, companionRadius(), Loyalty.Type.COMPANION);
    }

    private boolean isContinuousApproach(ApproachSample previous, ApproachSample current,
                                         ServerPlayerEntity player) {
        double radius = approachRadius();
        double radiusSquared = radius * radius;
        if (previous.world() != current.world()
                || previous.entityId() != current.entityId()
                || previous.teleportEpoch() != current.teleportEpoch()
                || previous.tick() + 1 != current.tick()
                || !previous.exteriorPos().equals(current.exteriorPos())
                || previous.distanceSquared() <= radiusSquared
                || current.distanceSquared() > radiusSquared
                || current.distanceSquared() >= previous.distanceSquared()
                || player.isInTeleportationState())
            return false;

        Vec3d movement = current.playerPos().subtract(previous.playerPos());
        double maxStep = Math.max(0, AITMod.CONFIG.temperamentMaxApproachStep);
        if (movement.lengthSquared() == 0 || movement.lengthSquared() > maxStep * maxStep)
            return false;

        Vec3d towardExterior = Vec3d.ofCenter(current.exteriorPos()).subtract(previous.playerPos());
        return movement.dotProduct(towardExterior) > 0;
    }

    private void queueHopAwayFrom(CachedDirectedGlobalPos position, ServerPlayerEntity player,
                                  ApproachSample approach) {
        double x = position.getPos().getX() + 0.5 - player.getX();
        double z = position.getPos().getZ() + 0.5 - player.getZ();
        double length = Math.sqrt(x * x + z * z);
        if (length < 0.001) {
            double angle = AITMod.RANDOM.nextDouble() * Math.PI * 2;
            x = Math.cos(angle);
            z = Math.sin(angle);
            length = 1;
        }

        double awayAngle = Math.atan2(z / length, x / length);
        double spread = Math.toRadians(Math.max(0, Math.min(360, AITMod.CONFIG.temperamentHopAngleSpreadDegrees)));
        double angle = awayAngle + (AITMod.RANDOM.nextDouble() - 0.5) * spread;
        int distance = Math.max(1, AITMod.CONFIG.temperamentHopDistance);
        int offsetX = (int) Math.round(Math.cos(angle) * distance);
        int offsetZ = (int) Math.round(Math.sin(angle) * distance);
        BlockPos target = position.getPos().add(offsetX, 0, offsetZ);
        this.pendingHop.set(CachedDirectedGlobalPos.create(position.getWorld(), target, position.getRotation()));
        this.pendingHopPlayer.set(player.getUuid());
        this.pendingHopApproach = approach;
    }

    private void tryStartPendingHop() {
        CachedDirectedGlobalPos target = this.pendingHop.get();
        if (target == null)
            return;

        TravelHandler travel = this.tardis.travel();
        if (!this.isEnabled() || travel.handbrake() || this.tardis.siege().isActive()
                || !this.hasValidExteriorBlock()
                || !this.isOutsideHomeRadius()) {
            this.clearPendingHop();
            return;
        }

        CachedDirectedGlobalPos position = travel.position();
        UUID playerId = this.pendingHopPlayer.get();
        ServerPlayerEntity player = playerId == null || position == null || position.getWorld() == null
                ? null : position.getWorld().getServer().getPlayerManager().getPlayer(playerId);
        if (player == null || !this.isReject(player) || !isWithin(player, position, approachRadius())
                || !this.isSameApproach(player, position)
                || this.hasNearbyLoyalty(position, companionRadius(), Loyalty.Type.COMPANION)) {
            this.clearPendingHop();
            return;
        }

        if (!travel.isLanded()) {
            if (travel.getState() != TravelHandlerBase.State.MAT)
                this.clearPendingHop();
            return;
        }

        if (this.hasInteriorPlayers() || !this.tardis.fuel().hasPower()) {
            this.clearPendingHop();
            return;
        }

        if (this.startTemperamentHop(target)) {
            this.tryTemporaryCamouflage();
            this.clearPendingHop();
        }
    }

    private void clearPendingHop() {
        this.pendingHop.set((CachedDirectedGlobalPos) null);
        this.pendingHopPlayer.set((UUID) null);
        this.pendingHopApproach = null;
    }

    private boolean isSameApproach(ServerPlayerEntity player, CachedDirectedGlobalPos position) {
        return this.pendingHopApproach != null
                && this.pendingHopApproach.entityId() == player.getId()
                && this.pendingHopApproach.teleportEpoch() == TeleportAware.getTeleportEpoch(player)
                && this.pendingHopApproach.world() == position.getWorld()
                && this.pendingHopApproach.exteriorPos().equals(position.getPos());
    }

    private void forgetRejectApproach(UUID player) {
        this.rejectApproachSamples.remove(player);
        if (player.equals(this.pendingHopPlayer.get()))
            this.clearPendingHop();
    }

    private void tryTemporaryCamouflage() {
        if (!this.tardis.subsystems().chameleon().isUsable()
                || !chance(AITMod.CONFIG.temperamentCamouflageChance))
            return;

        if (chance(AITMod.CONFIG.temperamentCloakChance))
            this.enableTemporarySilentCloak();
        else
            this.enableTemporaryAdaptiveExterior();
    }

    private void enableTemporaryAdaptiveExterior() {
        if (ChameleonHandler.isDisguised(this.tardis))
            return;

        this.captureCamouflageState();
        this.forceExteriorUpdate(ExteriorVariantRegistry.ADAPTIVE);
    }

    private void enableTemporarySilentCloak() {
        if (this.tardis.cloak().cloaked().get())
            return;

        this.captureCamouflageState();
        this.tardis.cloak().silent().set(true);
        this.tardis.cloak().cloaked().set(true);
    }

    private void captureCamouflageState() {
        if (this.previousExterior.get() != null)
            return;

        this.previousCloaked.set(this.tardis.cloak().cloaked().get());
        this.previousCloakSilent.set(this.tardis.cloak().silent().get());
        this.previousExterior.set(this.tardis.getExterior().getVariant().id());
    }

    private void restoreTemporaryCamouflage() {
        Identifier exteriorId = this.previousExterior.get();
        if (exteriorId == null)
            return;

        boolean cloaked = this.previousCloaked.get();
        boolean silent = this.previousCloakSilent.get();
        ExteriorVariantSchema exterior = findExterior(exteriorId);
        if (exterior == null) {
            exterior = ExteriorVariantRegistry.getInstance().fallback();
            AITMod.LOGGER.warn("Unable to restore missing exterior {} for TARDIS {}; using {}",
                    exteriorId, this.tardis.getUuid(), exterior.id());
        }
        if (!this.tardis.getExterior().getVariant().equals(exterior))
            this.forceExteriorUpdate(exterior);

        if (this.tardis.cloak().silent().get() != silent)
            this.tardis.cloak().silent().set(silent);
        if (this.tardis.cloak().cloaked().get() != cloaked)
            this.tardis.cloak().cloaked().set(cloaked);
        this.previousCloaked.set(false);
        this.previousCloakSilent.set(false);
        this.previousExterior.set((Identifier) null);
    }

    private static ExteriorVariantSchema findExterior(Identifier id) {
        for (ExteriorVariantSchema exterior : ExteriorVariantRegistry.getInstance().toList()) {
            if (exterior.id().equals(id))
                return exterior;
        }
        return null;
    }


    private boolean hasValidExteriorBlock() {
        var exterior = this.tardis.getExterior().findExteriorBlock();
        if (exterior.isEmpty())
            return false;

        TardisRef reference = exterior.get().tardis();
        return reference != null && this.tardis.getUuid().equals(reference.getId());
    }

    private void forceExteriorUpdate(ExteriorVariantSchema exterior) {
        if (exterior == null)
            return;

        this.tardis.getExterior().setType(exterior.category());
        this.tardis.getExterior().setVariant(exterior);

        CachedDirectedGlobalPos position = this.tardis.travel().position();
        if (position != null && position.getWorld() != null)
            position.getWorld().getChunkManager().markForUpdate(position.getPos());

        TardisEvents.EXTERIOR_CHANGE.invoker().onChange(this.tardis);
    }

    /** Called by {@link KeyItem#inventoryTick}. */
    public boolean tryPunishLinkedKey(ServerPlayerEntity player, ItemStack stack) {
        if (!this.isEnabled() || !this.isReject(player) || stack == null || stack.isEmpty()
                || this.isAtExactHome())
            return false;

        if (!(stack.getItem() instanceof KeyItem key) || key.hasProtocol(KeyItem.Protocols.SKELETON)
                || !key.isOf(stack, this.tardis))
            return false;

        CachedDirectedGlobalPos position = this.tardis.travel().position();
        if (position == null || position.getWorld() != player.getServerWorld()
                || !isWithin(player, position, keyRadius())
                || !this.hasValidExteriorBlock())
            return false;

        Hand hand;
        if (player.getMainHandStack() == stack)
            hand = Hand.MAIN_HAND;
        else if (player.getOffHandStack() == stack)
            hand = Hand.OFF_HAND;
        else
            return false;

        long tick = player.getServer().getTicks();
        Long lastTick = this.linkedKeyThreatTicks.put(player.getUuid(), tick);
        if (lastTick != null && tick - lastTick <= 2)
            return false;

        if (!chance(AITMod.CONFIG.temperamentKeyBurnChance))
            return false;

        ItemStack dropped = stack.copy();
        player.setStackInHand(hand, ItemStack.EMPTY);
        ItemEntity item = player.dropItem(dropped, false, true);
        if (item != null)
            item.setPickupDelay(Math.max(0, AITMod.CONFIG.temperamentKeyPickupDelayTicks));
        player.setOnFireFor(Math.max(0, AITMod.CONFIG.temperamentKeyFireSeconds));
        return true;
    }

    public boolean preventEntry(ServerPlayerEntity player) {
        return this.preventEntry(player, true);
    }

    public boolean preventPortalEntry(ServerPlayerEntity player) {
        return this.preventEntry(player, false);
    }

    private boolean preventEntry(ServerPlayerEntity player, boolean requireExteriorProximity) {
        if (!this.isEnabled()) {
            this.restoreTemporaryCamouflage();
            return false;
        }

        CachedDirectedGlobalPos position = this.tardis.travel().position();
        if (position != null && this.tardis.loyalty().get(player).isOf(Loyalty.Type.COMPANION)
                && isWithin(player, position, companionRadius()))
            this.restoreTemporaryCamouflage();

        if (!this.isReject(player))
            return false;

        if (this.rejectEntryBlocked.get())
            return true;

        if (!this.tardis.door().isOpen() || !this.isOutsideHomeRadius() || position == null
                || (requireExteriorProximity && (position.getWorld() != player.getServerWorld()
                || !isWithin(player, position, Math.max(1, AITMod.CONFIG.temperamentDoorEntryTriggerRadius))))
                || this.hasInsideLoyalty(Loyalty.Type.PILOT)
                || this.hasNearbyLoyalty(position, approachRadius(), Loyalty.Type.PILOT)
                || !chance(AITMod.CONFIG.temperamentDoorCloseChance))
            return false;

        this.rejectEntryBlocked.set(true);
        this.tardis.door().closeDoors();
        return true;
    }

    public boolean isRejectedEntryBlocked(ServerPlayerEntity player) {
        return this.isEnabled() && this.isReject(player) && this.rejectEntryBlocked.get();
    }

    private void onEnterFlight() {
        this.unsafeLanding.set(false);
        this.unsafeLandingHostile.set((UUID) null);
        this.rejectApproachSamples.clear();
        this.clearPendingHop();
        if (!this.isEnabled())
            return;

        TravelHandler travel = this.tardis.travel();
        if (sameLocation(travel.position(), travel.destination()) || this.isDestinationHome()
                || !this.hasRejectPlayerInside() || this.hasInsideLoyalty(Loyalty.Type.PILOT)
                || !chance(AITMod.CONFIG.temperamentLeftBehindChance))
            return;

        if (!travel.leaveBehind().get()) {
            this.temporaryLeftBehind.set(true);
            travel.leaveBehind().set(true);
        }

        if (!travel.autopilot()) {
            this.temporaryLeftBehindAutopilot.set(true);
            travel.autopilot(true);
        }

        List<ServerPlayerEntity> rejects = new ArrayList<>();
        for (ServerPlayerEntity player : this.tardis.asServer().world().getPlayers()) {
            if (this.isReject(player))
                rejects.add(player);
        }
        rejects.forEach(player -> TardisUtil.teleportOutside(this.tardis, player));
    }

    public void prepareLanding(boolean destinationHome) {
        this.unsafeLanding.set(false);
        this.unsafeLandingHostile.set((UUID) null);
        if (!this.isEnabled() || destinationHome)
            return;

        if (this.hasOnlyRejectPlayersInside()
                && chance(AITMod.CONFIG.temperamentUnsafeLandingChance))
            this.unsafeLanding.set(true);
    }

    /** Called after the existing security protocol has handled NEUTRAL players. */
    public void finishDematerialization() {
        if (!this.temporaryLeftBehind.get())
            return;

        this.tardis.travel().leaveBehind().set(false);
        this.temporaryLeftBehind.set(false);
    }

    private void onLanded() {
        boolean pullHostileInside = this.unsafeLanding.get();
        UUID hostileId = this.unsafeLandingHostile.get();
        this.unsafeLanding.set(false);
        this.unsafeLandingHostile.set((UUID) null);
        this.finishDematerialization();
        this.restoreTemporaryLeftBehindAutopilot();
        if (pullHostileInside && hostileId != null)
            this.pullLandingHostileInside(hostileId);
    }

    private void pullLandingHostileInside(UUID hostileId) {
        CachedDirectedGlobalPos position = this.tardis.travel().position();
        if (position == null || position.getWorld() == null)
            return;

        if (position.getWorld().getEntity(hostileId) instanceof HostileEntity selected
                && selected.isAlive() && !selected.isRemoved()
                && !selected.getType().isIn(AITTags.EntityTypes.BOSS)) {
            TardisUtil.teleportInside(this.tardis.asServer(), selected);
            return;
        }

        Vec3d center = Vec3d.ofCenter(position.getPos());
        HostileEntity nearest = null;
        double nearestDistance = Double.MAX_VALUE;
        double captureRadius = Math.max(0.5, AITMod.CONFIG.temperamentUnsafeLandingHostileCaptureRadius);
        for (HostileEntity hostile : position.getWorld().getEntitiesByClass(HostileEntity.class,
                new Box(position.getPos()).expand(captureRadius), entity -> entity.isAlive()
                        && !entity.isRemoved() && !entity.getType().isIn(AITTags.EntityTypes.BOSS))) {
            double distance = hostile.squaredDistanceTo(center);
            if (distance < nearestDistance) {
                nearestDistance = distance;
                nearest = hostile;
            }
        }

        if (nearest != null)
            TardisUtil.teleportInside(this.tardis.asServer(), nearest);
    }

    private void restoreTemporaryLeftBehindAutopilot() {
        if (!this.temporaryLeftBehindAutopilot.get())
            return;

        this.tardis.travel().autopilot(false);
        this.temporaryLeftBehindAutopilot.set(false);
    }

    public boolean useUnsafeLanding() {
        return this.isEnabled() && this.unsafeLanding.get();
    }

    public void unsafeLandingHostile(UUID hostileId) {
        this.unsafeLandingHostile.set(hostileId);
    }

    public void onFlightEventFailed() {
        MinecraftServer server = ServerLifecycleHooks.get();
        if (!this.isEnabled() || server == null || !DependencyChecker.hasGravity()
                || this.tardis.travel().getState() != TravelHandlerBase.State.FLIGHT
                || !this.hasOnlyRejectPlayersInside()
                || !chance(AITMod.CONFIG.temperamentFailedEventGravityChance))
            return;

        GravityHandler.activateTemperamentOverride(this.tardis,
                server.getTicks() + Math.max(0L, AITMod.CONFIG.temperamentGravityDurationSeconds) * 20L);
    }

    public boolean handleHammer(ServerPlayerEntity player, ServerWorld world) {
        if (!this.isEnabled() || !this.isReject(player))
            return false;

        TravelHandler travel = this.tardis.travel();
        if (travel.getState() == TravelHandlerBase.State.FLIGHT) {
            if (this.hasInsideLoyalty(Loyalty.Type.PILOT))
                return false;

            this.tardis.door().setDeadlocked(false);
            this.tardis.door().setLocked(false);
            this.tardis.door().openDoors();
            this.tardis.shields().disableAll();
            return true;
        }

        CachedDirectedGlobalPos position = travel.position();
        if (!travel.isLanded() || !this.isOutsideHomeRadius()
                || this.hasInsideLoyalty(Loyalty.Type.PILOT)
                || this.hasNearbyLoyalty(position, homeRadius(), Loyalty.Type.PILOT))
            return false;

        this.tardis.door().closeDoors();
        this.tardis.door().setDeadlocked(false);
        this.tardis.door().setLocked(true);
        this.tardis.alarm().enable();
        this.ejectLifeSupport(world, player);
        this.degradeRejectKeys();
        return true;
    }

    private void ejectLifeSupport(ServerWorld world, ServerPlayerEntity player) {
        this.tardis.subsystems().lifeSupport().setEnabled(false);
        BlockPos engine = this.tardis.getDesktop().getEnginePos();
        if (engine == null)
            return;

        for (Object link : WorldFluidTracker.bfs(world, engine, LIFE_SUPPORT_SEARCH_LIMIT).values()) {
            if (!(link instanceof GenericStructureSystemBlockEntity block)
                    || block.system() != this.tardis.subsystems().lifeSupport())
                continue;

            // Reuse the generic core's ordinary empty-hand removal path instead of
            // depending on the separate home-system extraction API.
            block.useOn(block.getCachedState(), world, false, player, ItemStack.EMPTY);
            return;
        }
    }

    private void degradeRejectKeys() {
        if (!this.tardis.asServer().hasWorld())
            return;

        for (ServerPlayerEntity player : this.tardis.asServer().world().getPlayers()) {
            if (!this.isReject(player))
                continue;

            for (int slot = 0; slot < player.getInventory().size(); slot++) {
                ItemStack stack = player.getInventory().getStack(slot);
                if (!(stack.getItem() instanceof KeyItem key) || key.hasProtocol(KeyItem.Protocols.SKELETON)
                        || !key.isOf(stack, this.tardis))
                    continue;

                Item item = stack.getItem();
                if (item == AITItems.IRON_KEY) {
                    StackUtil.playBreak(player);
                    player.getInventory().setStack(slot, ItemStack.EMPTY);
                    this.tardis.loyalty().subLevel(player,
                            Math.max(0, AITMod.CONFIG.temperamentBrokenKeyLoyaltyPenalty));
                    continue;
                }

                Item replacement = lowerKeyTier(item);
                if (replacement == null)
                    continue;

                StackUtil.playBreak(player);
                ItemStack degraded = new ItemStack(replacement, stack.getCount());
                if (stack.hasNbt())
                    degraded.setNbt(stack.getNbt().copy());
                player.getInventory().setStack(slot, degraded);
            }
        }
    }

    private static Item lowerKeyTier(Item item) {
        if (item == AITItems.CLASSIC_KEY)
            return AITItems.NETHERITE_KEY;
        if (item == AITItems.NETHERITE_KEY)
            return AITItems.GOLD_KEY;
        if (item == AITItems.GOLD_KEY)
            return AITItems.IRON_KEY;
        return null;
    }

    private boolean startTemperamentHop(CachedDirectedGlobalPos target) {
        if (target == null || target.getWorld() == null || !this.tardis.travel().isLanded()
                || this.tardis.travel().handbrake() || !this.tardis.fuel().hasPower())
            return false;

        TravelHandler travel = this.tardis.travel();
        travel.destination(target);
        travel.autopilot(true);
        travel.speed(Math.max(1, travel.maxSpeed().get()));
        return travel.getState() != TravelHandlerBase.State.LANDED;
    }

    private boolean isAtExactHome() {
        return sameLocation(this.tardis.travel().position(), this.tardis.stats().getHome());
    }

    private static boolean isAtExactHome(ServerTardis tardis) {
        return tardis != null && sameLocation(tardis.travel().position(), tardis.stats().getHome());
    }

    public boolean isDestinationHome() {
        return sameLocation(this.tardis.travel().destination(), this.tardis.stats().getHome());
    }

    private boolean isOutsideHomeRadius() {
        CachedDirectedGlobalPos position = this.tardis.travel().position();
        CachedDirectedGlobalPos home = this.tardis.stats().getHome();
        if (position == null || home == null || position.getDimension() == null || home.getDimension() == null)
            return true;
        if (!position.getDimension().equals(home.getDimension()))
            return true;

        double radius = homeRadius();
        return position.getPos().getSquaredDistance(home.getPos()) > radius * radius;
    }

    private static int homeRadius() {
        return Math.max(1, AITMod.CONFIG.temperamentHomeRadius);
    }

    private boolean hasInteriorPlayers() {
        return this.tardis.asServer().hasWorld() && !this.tardis.asServer().world().getPlayers().isEmpty();
    }

    public boolean hasInsideLoyalty(Loyalty.Type loyalty) {
        if (!this.tardis.asServer().hasWorld())
            return false;

        for (ServerPlayerEntity player : this.tardis.asServer().world().getPlayers()) {
            if (this.tardis.loyalty().get(player).isOf(loyalty))
                return true;
        }
        return false;
    }

    private boolean hasOnlyRejectPlayersInside() {
        if (!this.tardis.asServer().hasWorld())
            return false;

        List<ServerPlayerEntity> players = this.tardis.asServer().world().getPlayers();
        if (players.isEmpty())
            return false;

        for (ServerPlayerEntity player : players) {
            if (!this.isReject(player))
                return false;
        }
        return true;
    }

    private boolean hasRejectPlayerInside() {
        if (!this.tardis.asServer().hasWorld())
            return false;

        for (ServerPlayerEntity player : this.tardis.asServer().world().getPlayers()) {
            if (this.isReject(player))
                return true;
        }
        return false;
    }

    private boolean hasNearbyLoyalty(CachedDirectedGlobalPos position, double radius, Loyalty.Type loyalty) {
        if (position == null || position.getWorld() == null)
            return false;

        for (ServerPlayerEntity player : position.getWorld().getPlayers()) {
            if (this.tardis.loyalty().get(player).isOf(loyalty) && isWithin(player, position, radius))
                return true;
        }
        return false;
    }

    private static int approachRadius() {
        return Math.max(1, AITMod.CONFIG.temperamentApproachRadius);
    }

    private static int approachTrackingRadius() {
        return Math.max(approachRadius(), AITMod.CONFIG.temperamentApproachTrackingRadius);
    }

    private static int companionRadius() {
        return Math.max(1, AITMod.CONFIG.temperamentCompanionSafetyRadius);
    }

    private static int keyRadius() {
        return Math.max(1, AITMod.CONFIG.temperamentKeyRadius);
    }

    private static boolean chance(int percentage) {
        int clamped = Math.max(0, Math.min(100, percentage));
        return clamped == 100 || clamped > 0 && AITMod.RANDOM.nextInt(100) < clamped;
    }

    private static boolean isWithin(ServerPlayerEntity player, CachedDirectedGlobalPos position, double radius) {
        return player.getServerWorld() == position.getWorld()
                && player.squaredDistanceTo(position.getPos().getX() + 0.5,
                position.getPos().getY() + 0.5, position.getPos().getZ() + 0.5) <= radius * radius;
    }

    private static boolean sameLocation(CachedDirectedGlobalPos first, CachedDirectedGlobalPos second) {
        return first != null && second != null && first.getDimension().equals(second.getDimension())
                && first.getPos().equals(second.getPos());
    }

    private record ApproachSample(ServerWorld world, BlockPos exteriorPos, Vec3d playerPos,
                                  double distanceSquared, int entityId, int teleportEpoch, long tick) {

        private static ApproachSample create(CachedDirectedGlobalPos position, ServerPlayerEntity player,
                                             long tick) {
            return new ApproachSample(position.getWorld(), position.getPos().toImmutable(), player.getPos(),
                    player.squaredDistanceTo(Vec3d.ofCenter(position.getPos())), player.getId(),
                    TeleportAware.getTeleportEpoch(player), tick);
        }
    }
}
