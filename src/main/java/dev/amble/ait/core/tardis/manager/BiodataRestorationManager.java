package dev.amble.ait.core.tardis.manager;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import dev.amble.ait.AITMod;
import dev.amble.ait.api.tardis.TardisEvents;
import dev.amble.ait.core.engine.DurableSubSystem;
import dev.amble.ait.core.engine.SubSystem;
import dev.amble.ait.core.engine.impl.BiodataRestoration;
import dev.amble.ait.core.tardis.ServerTardis;
import dev.amble.ait.core.tardis.Tardis;
import dev.amble.ait.core.tardis.handler.DoorHandler;
import dev.amble.ait.core.tardis.util.BiodataRestorationEffects;
import dev.amble.ait.core.tardis.util.TardisHomeUtil;
import dev.amble.ait.core.tardis.util.TardisUtil;
import dev.amble.ait.core.world.TardisServerWorld;
import dev.amble.ait.data.Loyalty;
import dev.amble.lib.util.ServerLifecycleHooks;
import dev.drtheo.scheduler.api.TimeUnit;
import dev.drtheo.scheduler.api.common.Scheduler;
import dev.drtheo.scheduler.api.common.TaskStage;
import org.jetbrains.annotations.Nullable;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.random.Random;

/** Server-side selection, payment and consequences for Biodata Restoration. */
public final class BiodataRestorationManager {
    private BiodataRestorationManager() {
    }

    public static void init() {
        TardisEvents.DOOR_USED.register((tardis, player) -> {
            recordEntry(tardis, player);
            return DoorHandler.InteractionResult.CONTINUE;
        });
    }

    public static void recordEntry(Tardis tardis, ServerPlayerEntity player) {
        if (!(tardis instanceof ServerTardis serverTardis) || player == null || player.getServer() == null)
            return;

        BiodataRestorationState.get(player.getServer()).recordVisit(player.getUuid(), serverTardis.getUuid());
    }

    @Nullable public static UUID getLastVisitedTardis(MinecraftServer server, UUID playerId) {
        return server == null || playerId == null
                ? null : BiodataRestorationState.get(server).getLastVisitedTardis(playerId);
    }

    public static void setActive(ServerTardis tardis, boolean active) {
        if (tardis == null || tardis.getUuid() == null)
            return;

        MinecraftServer server = serverFor(tardis);
        if (server == null)
            return;

        BiodataRestorationState state = BiodataRestorationState.get(server);
        boolean enabled = active && AITMod.CONFIG.biodataRestorationAvailable;
        state.setActive(tardis.getUuid(), enabled);
        if (!enabled || !state.needsEligibilityIndex(tardis.getUuid()))
            return;

        Set<UUID> eligible = new HashSet<>();
        tardis.loyalty().data().forEach((playerId, loyalty) -> {
            if (playerId != null && loyalty != null && loyalty.isOf(Loyalty.Type.COMPANION))
                eligible.add(playerId);
        });
        state.setEligiblePlayers(tardis.getUuid(), eligible);
    }

    public static void updateEligibility(ServerTardis tardis, UUID playerId, boolean eligible) {
        if (tardis == null || tardis.getUuid() == null || playerId == null)
            return;

        MinecraftServer server = serverFor(tardis);
        if (server != null)
            BiodataRestorationState.get(server).setEligible(tardis.getUuid(), playerId, eligible);
    }

    public static void removeTardis(MinecraftServer server, UUID tardisId) {
        if (server != null && tardisId != null)
            BiodataRestorationState.get(server).removeTardis(tardisId);
    }

    public static boolean canRescueNow(ServerPlayerEntity player) {
        RescueContext context = prepareRescue(player);
        return context != null && context.canFund(player);
    }

    /**
     * Attempts the complete restoration synchronously from the lethal-damage
     * call. Returning true tells vanilla that death was successfully prevented.
     */
    public static boolean tryRescue(ServerPlayerEntity player) {
        if (player == null || !AITMod.CONFIG.biodataRestorationAvailable)
            return false;

        RescueContext context = prepareRescue(player);
        if (context == null || !context.pay(player))
            return false;

        BiodataRestorationEffects.restore(player);
        applyConsequences(context, player);
        return true;
    }

    @Nullable private static RescueContext prepareRescue(ServerPlayerEntity player) {
        MinecraftServer server = player == null ? null : player.getServer();
        ServerTardisManager manager = ServerTardisManager.getInstance();
        if (server == null || manager == null)
            return null;

        BiodataRestorationState state = BiodataRestorationState.get(server);
        Map<UUID, Long> visits = state.getVisits(player.getUuid());
        List<BiodataRestoration> protectors = new ArrayList<>();

        for (UUID tardisId : state.getEligibleActiveTardises(player.getUuid())) {
            ServerTardis tardis;
            try {
                tardis = manager.demandTardis(server, tardisId);
            } catch (RuntimeException exception) {
                AITMod.LOGGER.error("Failed to load Biodata Restoration TARDIS {} for player {}",
                        tardisId, player.getUuid(), exception);
                continue;
            }

            if (tardis == null || tardis.isRemoved()) {
                state.setActive(tardisId, false);
                continue;
            }

            BiodataRestoration restoration = tardis.subsystems().biodataRestoration();
            if (restoration != null && restoration.canProtect(player)) {
                protectors.add(restoration);
            } else if (restoration == null || !restoration.isInstalled()) {
                state.setActive(tardisId, false);
            } else if (!tardis.loyalty().get(player).isOf(Loyalty.Type.COMPANION)) {
                state.setEligible(tardisId, player.getUuid(), false);
            }
        }

        if (protectors.isEmpty())
            return null;

        Comparator<BiodataRestoration> byVisit = Comparator
                .comparingLong((BiodataRestoration restoration) ->
                        visits.getOrDefault(restoration.tardis().getUuid(), Long.MIN_VALUE))
                .thenComparing(restoration -> restoration.tardis().getUuid().toString());
        BiodataRestoration selected = protectors.stream().max(byVisit).orElse(null);
        return selected == null ? null : new RescueContext(selected, List.copyOf(protectors));
    }

    private static void applyConsequences(RescueContext context, ServerPlayerEntity player) {
        BiodataRestoration selected = context.selected();
        if (!(selected.tardis() instanceof ServerTardis tardis))
            return;

        tryHailMaryTeleport(tardis, player);
        tardis.loyalty().subLevel(player, Math.max(0, AITMod.CONFIG.biodataRestorationRescueLoyaltyCost));

        int minimum = Math.max(0, Math.min(AITMod.CONFIG.biodataRestorationSubsystemDamageMin,
                AITMod.CONFIG.biodataRestorationSubsystemDamageMax));
        int maximum = Math.max(minimum, Math.max(AITMod.CONFIG.biodataRestorationSubsystemDamageMin,
                AITMod.CONFIG.biodataRestorationSubsystemDamageMax));
        Random random = player.getRandom();

        for (SubSystem system : tardis.subsystems()) {
            if (!(system instanceof DurableSubSystem durable))
                continue;

            int damage = minimum == maximum ? minimum : random.nextBetween(minimum, maximum);
            durable.removeDurability(damage);
        }

        int jealousyPenalty = Math.max(0, AITMod.CONFIG.biodataRestorationJealousyPenalty);
        for (BiodataRestoration protector : context.protectors()) {
            if (protector == selected || !(protector.tardis() instanceof ServerTardis jealous))
                continue;

            jealous.loyalty().subLevel(player, jealousyPenalty);
            player.sendMessage(Text.translatable("tardis.message.biodata_restoration.jealousy",
                    jealous.stats().getName()).formatted(Formatting.DARK_RED), false);
        }

        selected.validateNow();
    }

    private static void tryHailMaryTeleport(ServerTardis tardis, ServerPlayerEntity player) {
        if (!TardisHomeUtil.isParkedAtExactHome(tardis) || !tardis.travel().handbrake()
                || !tardis.stats().hailMary().get() || isInside(tardis, player))
            return;

        double cost = Math.max(0, AITMod.CONFIG.biodataRestorationHailMaryTeleportFuelCost);
        if (tardis.fuel().getCurrentFuel() < cost)
            return;

        Scheduler.get().runTaskLater(() -> {
            MinecraftServer server = player.getServer();
            if (server == null || !player.isAlive() || player.isRemoved()
                    || !player.networkHandler.isConnectionOpen()
                    || server.getPlayerManager().getPlayer(player.getUuid()) != player
                    || tardis.isRemoved() || isInside(tardis, player)
                    || tardis.fuel().getCurrentFuel() < cost)
                return;

            TardisUtil.teleportInside(tardis, player);
            if (!isInside(tardis, player))
                return;

            tardis.fuel().removeFuel(cost);
            tardis.stats().hailMary().set(false);
        }, TaskStage.END_SERVER_TICK, TimeUnit.TICKS, 0);
    }

    private static boolean isInside(ServerTardis tardis, ServerPlayerEntity player) {
        return tardis != null && player != null && tardis.getUuid() != null
                && tardis.getUuid().equals(TardisServerWorld.getTardisId(player.getWorld()));
    }

    @Nullable private static MinecraftServer serverFor(ServerTardis tardis) {
        if (tardis != null && tardis.hasWorld())
            return tardis.world().getServer();
        return ServerLifecycleHooks.get();
    }

    private record RescueContext(BiodataRestoration selected, List<BiodataRestoration> protectors) {
        private boolean canFund(ServerPlayerEntity player) {
            if (!(this.selected.tardis() instanceof ServerTardis tardis) || !this.selected.canProtect(player))
                return false;

            double cost = Math.max(0, AITMod.CONFIG.biodataRestorationRescueFuelCost);
            return tardis.fuel().getCurrentFuel() >= cost;
        }

        private boolean pay(ServerPlayerEntity player) {
            if (!this.canFund(player) || !(this.selected.tardis() instanceof ServerTardis tardis))
                return false;

            tardis.fuel().removeFuel(Math.max(0, AITMod.CONFIG.biodataRestorationRescueFuelCost));
            return true;
        }
    }
}
