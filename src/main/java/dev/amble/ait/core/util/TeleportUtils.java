package dev.amble.ait.core.util;

import java.util.*;

import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import dev.amble.ait.core.AITDimensions;

public class TeleportUtils {
    public static void checkPlayerTeleportation(ServerWorld world) {
        List<ServerPlayerEntity> playersToTeleport = new ArrayList<>();

        for (ServerPlayerEntity player : world.getPlayers()) {
            if (!(player.getWorld().getRegistryKey() == AITDimensions.SPACE)) {
                continue;
            }

            Vec3d playerPos = player.getPos();

            Map<Vec3d, PlanetInfo> planetData = Map.of(
                    new Vec3d(0, 0, 0), new PlanetInfo("minecraft:overworld", 900, 200), // Overworld
                    new Vec3d(2000, 0, 0), new PlanetInfo(AITDimensions.MOON.toString(), 150, 100), // Moon
                    new Vec3d(-2500, 300, 0), new PlanetInfo(AITDimensions.MARS.toString(), 500, 150) // Mars
            );

            for (Map.Entry<Vec3d, PlanetInfo> entry : planetData.entrySet()) {
                Vec3d planetPos = entry.getKey();
                PlanetInfo planetInfo = entry.getValue();
                String dimension = planetInfo.getDimension();
                double planetRadius = planetInfo.getRadius();
                double suctionRadius = planetInfo.getSuctionRadius();

                double distance = planetPos.distanceTo(playerPos);

                if (distance < planetRadius) {
                    playersToTeleport.add(player);
                    break;
                } else if (distance < suctionRadius) {
                    applySuction(player, planetPos);
                }
            }
        }

        for (ServerPlayerEntity player : playersToTeleport) {
            teleportPlayer(player);
        }
    }

    private static void applySuction(ServerPlayerEntity player, Vec3d planetPos) {
        Vec3d playerPos = player.getPos();

        // Amosphere succ goes here
    }

    private static void teleportPlayer(ServerPlayerEntity player) {
        MinecraftServer server = player.getServer();
        if (server != null) {
            String dimension = getDimensionForPlayer(player);
            if (dimension == null) return;

            String[] parts = dimension.split(":");
            if (parts.length != 2) {
                System.err.println("Invalid dimension format! Use 'namespace:dimension' or AITDimensions.<dimension>.getString()");
                return;
            }

            String namespace = parts[0];
            String dimName = parts[1];

            RegistryKey<World> targetWorldKey = RegistryKey.of(RegistryKeys.WORLD, new Identifier(namespace, dimName));
            ServerWorld targetWorld = server.getWorld(targetWorldKey);

            if (targetWorld != null) {
                player.teleport(targetWorld, player.getX(), player.getY(), player.getZ(), player.getYaw(), player.getPitch());
            } else {
                System.err.println("Dimension " + dimension + " not found!");
            }
        }
    }

    private static String getDimensionForPlayer(ServerPlayerEntity player) {
        Vec3d playerPos = player.getPos();
        Map<Vec3d, PlanetInfo> planetData = Map.of(
                new Vec3d(0, 0, 0), new PlanetInfo("minecraft:overworld", 900, 200),
                new Vec3d(2000, 0, 0), new PlanetInfo(AITDimensions.MOON.toString(), 150, 100),
                new Vec3d(-2500, 300, 0), new PlanetInfo(AITDimensions.MARS.toString(), 500, 150)
        );

        for (Map.Entry<Vec3d, PlanetInfo> entry : planetData.entrySet()) {
            Vec3d planetPos = entry.getKey();
            PlanetInfo planetInfo = entry.getValue();
            String dimension = planetInfo.getDimension();
            double planetRadius = planetInfo.getRadius();

            double distance = planetPos.distanceTo(playerPos);
            if (distance < planetRadius) {
                return dimension;
            }
        }
        return null;
    }

    private static class PlanetInfo {
        private final String dimension;
        private final double radius;
        private final double suctionRadius;

        public PlanetInfo(String dimension, double radius, double suctionRadius) {
            this.dimension = dimension;
            this.radius = radius;
            this.suctionRadius = suctionRadius;
        }

        public String getDimension() {
            return dimension;
        }

        public double getRadius() {
            return radius;
        }

        public double getSuctionRadius() {
            return suctionRadius;
        }
    }
}
