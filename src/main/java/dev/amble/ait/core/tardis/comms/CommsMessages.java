package dev.amble.ait.core.tardis.comms;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import dev.amble.ait.core.tardis.ServerTardis;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public final class CommsMessages {

    private static final long REPEAT_TICKS = 40;
    private static final int MAX_REMEMBERED = 32;

    private static final Map<UUID, Map<String, Long>> SENT = new ConcurrentHashMap<>();

    private CommsMessages() {
    }

    public static void actionBar(ServerPlayerEntity player, Text text) {
        if (player.getServer() == null)
            return;

        long now = player.getServer().getTicks();
        String key = text.getString();
        Map<String, Long> sent = SENT.computeIfAbsent(player.getUuid(), uuid -> new ConcurrentHashMap<>());
        Long last = sent.get(key);

        if (last != null && now - last < REPEAT_TICKS)
            return;

        sent.put(key, now);

        if (sent.size() > MAX_REMEMBERED)
            sent.values().removeIf(time -> now - time >= REPEAT_TICKS);

        player.sendMessage(text, true);
    }

    public static void interior(ServerTardis tardis, Text text) {
        if (!tardis.hasWorld())
            return;

        for (ServerPlayerEntity player : tardis.world().getPlayers())
            actionBar(player, text);
    }
}
