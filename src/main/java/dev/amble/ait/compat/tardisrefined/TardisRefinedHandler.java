package dev.amble.ait.compat.tardisrefined;

import java.util.Set;

import com.google.common.collect.Sets;
import whocraft.tardis_refined.common.util.DimensionUtil;
import whocraft.tardis_refined.registry.TRDimensionTypes;

import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

public class TardisRefinedHandler {
    public static boolean isPlayerInTardis(ServerPlayerEntity player) {
        MinecraftServer server = player.getServer();
        if (server == null) return false;

        // Get all registered TARDIS dimensions
        Set<RegistryKey<World>> tardisLevels = DimensionUtil.getTardisLevels(server);

        // Check if the player's current world is one of them
        return tardisLevels.contains(player.getWorld().getRegistryKey());
    }

    public static boolean isInTardis(ServerPlayerEntity player) {
        MinecraftServer server = player.getServer();
        if (server == null) return false;

        // Get all registered TARDIS dimensions
        Set<RegistryKey<World>> tardisLevels = DimensionUtil.getTardisLevels(server);

        // Check if the player's current world is one of them
        return tardisLevels.contains(player.getWorld().getRegistryKey());
    }

    public static Set<RegistryKey<World>> getTardisLevels(MinecraftServer server) {
        Set<RegistryKey<World>> set = Sets.newHashSet();

        for(ServerWorld level : server.getWorlds()) {
            if (level.getDimensionKey() == TRDimensionTypes.TARDIS) {
                set.add(level.getRegistryKey());
            }
        }

        return set;
    }
}
