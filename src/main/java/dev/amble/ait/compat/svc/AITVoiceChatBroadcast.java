package dev.amble.ait.compat.svc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import dev.amble.ait.core.tardis.ServerTardis;
import dev.amble.ait.core.tardis.comms.CommsMessages;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public final class AITVoiceChatBroadcast {

    private static final Map<UUID, ServerTardis> BROADCASTERS = new ConcurrentHashMap<>();
    private static final Map<UUID, List<UUID>> LISTENERS = new ConcurrentHashMap<>();

    private AITVoiceChatBroadcast() {
    }

    public static void init() {
        ServerTickEvents.END_SERVER_TICK.register(AITVoiceChatBroadcast::tick);
    }

    public static void toggle(ServerPlayerEntity player, ServerTardis tardis) {
        if (BROADCASTERS.remove(player.getUuid()) != null) {
            CommsMessages.actionBar(player, Text.translatable("tardis.message.comms.broadcast_off").formatted(Formatting.GRAY));
            return;
        }

        if (!tardis.hasWorld() || player.getServerWorld() != tardis.world())
            return;

        BROADCASTERS.put(player.getUuid(), tardis);
        refresh(tardis);
        CommsMessages.actionBar(player, Text.translatable("tardis.message.comms.broadcast_on").formatted(Formatting.GOLD));
    }

    public static void stop(UUID player) {
        BROADCASTERS.remove(player);
    }

    public static void clear() {
        BROADCASTERS.clear();
        LISTENERS.clear();
    }

    public static List<UUID> listenersFor(UUID broadcaster) {
        ServerTardis tardis = BROADCASTERS.get(broadcaster);

        if (tardis == null)
            return List.of();

        return LISTENERS.getOrDefault(tardis.getUuid(), List.of());
    }

    private static void tick(MinecraftServer server) {
        if (BROADCASTERS.isEmpty()) {
            LISTENERS.clear();
            return;
        }

        Map<UUID, ServerTardis> active = new HashMap<>();

        BROADCASTERS.entrySet().removeIf(entry -> {
            ServerPlayerEntity player = server.getPlayerManager().getPlayer(entry.getKey());
            ServerTardis tardis = entry.getValue();

            if (player == null || tardis.isRemoved() || !tardis.hasWorld() || player.getServerWorld() != tardis.world()) {
                if (player != null)
                    CommsMessages.actionBar(player, Text.translatable("tardis.message.comms.broadcast_off").formatted(Formatting.GRAY));

                return true;
            }

            active.put(tardis.getUuid(), tardis);
            return false;
        });

        LISTENERS.keySet().retainAll(active.keySet());
        active.values().forEach(AITVoiceChatBroadcast::refresh);
    }

    private static void refresh(ServerTardis tardis) {
        LISTENERS.put(tardis.getUuid(), tardis.world().getPlayers().stream().map(Entity::getUuid).toList());
    }
}
