package dev.amble.ait.core.tardis.comms;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import dev.amble.ait.core.tardis.ServerTardis;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public final class CommsMessages {

    private static final long REPEAT_TICKS = 40;
    private static final int MAX_REMEMBERED = 32;

    private static final Map<UUID, Object2LongMap<String>> SENT = new HashMap<>();

    private CommsMessages() {
    }

    public static void actionBar(ServerPlayerEntity player, Text text) {
        if (player.getServer() == null)
            return;

        long now = player.getServer().getTicks();
        String key = text.getString();
        Object2LongMap<String> sent = SENT.computeIfAbsent(player.getUuid(), uuid -> new Object2LongOpenHashMap<>());

        if (sent.containsKey(key) && now - sent.getLong(key) < REPEAT_TICKS)
            return;

        sent.put(key, now);

        if (sent.size() > MAX_REMEMBERED)
            sent.object2LongEntrySet().removeIf(entry -> now - entry.getLongValue() >= REPEAT_TICKS);

        player.sendMessage(text, true);
    }

    public static void interior(ServerTardis tardis, Text text) {
        if (!tardis.hasWorld())
            return;

        for (ServerPlayerEntity player : tardis.world().getPlayers())
            actionBar(player, text);
    }
}
