package dev.amble.ait.core.util;

import dev.amble.ait.AITMod;
import dev.amble.ait.core.AITDimensions;
import dev.amble.ait.core.entities.FlightTardisEntity;
import dev.amble.ait.module.planet.core.space.planet.Planet;
import dev.amble.ait.module.planet.core.space.planet.PlanetRenderInfo;
import dev.amble.ait.module.planet.core.space.system.Space;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.Entity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

// todo - all this code is very sucky
public class SpaceUtils {
    static {
        ServerTickEvents.END_WORLD_TICK.register(SpaceUtils::onWorldTick);
    }

    public static void init() {

    }

    private static void onWorldTick(ServerWorld world) {
        checkPlayerTeleportation(world);
    }

    public static void checkPlayerTeleportation(ServerWorld world) {
        List<ServerPlayerEntity> playersToTeleport = new ArrayList<>();
        List<ServerPlayerEntity> playersToSuck = new ArrayList<>();

        for (ServerPlayerEntity player : world.getPlayers()) {
            if (!(player.getWorld().getRegistryKey() == AITDimensions.SPACE)) {
                continue;
            }

            Vec3d playerPos = player.getPos();

            for (Planet planet : Space.getInstance().getPlanets()) {
                PlanetRenderInfo planetInfo = planet.render();
                Vec3d planetPos = planetInfo.position();
                double planetRadius = planetInfo.radius();
                double suctionRadius = planetInfo.suctionRadius();

                double distance = planetPos.distanceTo(playerPos);

                if (distance < suctionRadius) {
                    playersToSuck.add(player);
                    applySuction(playersToSuck, planetPos);
                }
                if (distance < planetRadius) {
                    playersToSuck.remove(player);
                    playersToTeleport.add(player);
                    break;
                }
            }
        }

        for (ServerPlayerEntity player : playersToTeleport) {
            teleportPlayerToTouchingPlanet(player);
        }

        /*for (Entity entity : world.iterateEntities()) {
            if (!(entity instanceof LivingEntity)) continue;

            // get planet
            Planet planet = PlanetRegistry.getInstance().get(entity.getWorld());
            if (planet == null) continue;

            // get transitions
            PlanetTransition transition = planet.transition();

            if (entity.getBlockPos().getY() < transition.height()) continue;

            transition.run((LivingEntity) entity);
        }*/
    }

    private static void applySuction(List<ServerPlayerEntity> player, Vec3d planetPos) {
        player.forEach(entity -> {
            if (entity.isSpectator())
                return;
            if (entity.getVehicle() instanceof FlightTardisEntity tardis) {
                if (tardis.tardis() != null && tardis.tardis().get().travel().antigravs().get()) return;
                Vec3d motion = planetPos.subtract(tardis.getPos()).normalize().multiply(0.1f);
                tardis.setVelocity(tardis.getVelocity().add(motion));
                tardis.velocityDirty = true;
                tardis.velocityModified = true;
            } else {
                Vec3d motion = planetPos.subtract(entity.getPos()).normalize().multiply(0.1f);
                entity.setVelocity(entity.getVelocity().add(motion));
                entity.velocityDirty = true;
                entity.velocityModified = true;
            }
        });
    }

    private static void teleportPlayerToTouchingPlanet(ServerPlayerEntity player) {
        MinecraftServer server = player.getServer();
        if (server == null) return;

        Identifier target = getTouchingPlanet(player).orElse(null);
        if (target == null) return;

        RegistryKey<World> targetWorldKey = RegistryKey.of(RegistryKeys.WORLD, target);
        ServerWorld targetWorld = server.getWorld(targetWorldKey);

        if (targetWorld != null) {
            player.teleport(targetWorld, player.getX(), targetWorld.getTopY(), player.getZ(), Set.of(), player.getYaw(), player.getPitch());
        } else {
            AITMod.LOGGER.error("Teleporting to planets -> Dimension {} not found!", target);
        }
    }


    /**
     * @param entity The entity to check
     * @return The dimension of the planet the entity is touching
     */
    private static Optional<Identifier> getTouchingPlanet(Entity entity) {
        for (Planet planet : Space.getInstance().getPlanets()) {
            PlanetRenderInfo planetInfo = planet.render();
            Vec3d planetPos = planetInfo.position();
            double planetRadius = planetInfo.radius();

            double distance = planetPos.distanceTo(entity.getPos());

            if (distance < planetRadius && planet.hasLandableSurface()) {
                return Optional.of(planet.dimension());
            }
        }

        return Optional.empty();
    }
}
