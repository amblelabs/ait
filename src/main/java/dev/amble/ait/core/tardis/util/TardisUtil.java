package dev.amble.ait.core.tardis.util;

import java.util.*;
import java.util.function.Predicate;

import dev.amble.lib.data.CachedDirectedGlobalPos;
import dev.amble.lib.data.DirectedBlockPos;
import dev.amble.lib.util.TeleportUtil;
import dev.drtheo.scheduler.api.TimeUnit;
import dev.drtheo.scheduler.api.common.Scheduler;
import dev.drtheo.scheduler.api.common.TaskStage;
import it.unimi.dsi.fastutil.longs.LongBidirectionalIterator;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.util.TriState;
import org.jetbrains.annotations.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.TntEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.function.LazyIterationConsumer;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import net.minecraft.world.entity.EntityLike;
import net.minecraft.world.entity.EntityTrackingSection;

import dev.amble.ait.AITMod;
import dev.amble.ait.api.ExtraPushableEntity;
import dev.amble.ait.api.tardis.TardisComponent;
import dev.amble.ait.api.tardis.TardisEvents;
import dev.amble.ait.core.AITSounds;
import dev.amble.ait.core.AITTags;
import dev.amble.ait.core.blockentities.DoorBlockEntity;
import dev.amble.ait.core.entities.FlightTardisEntity;
import dev.amble.ait.core.tardis.ServerTardis;
import dev.amble.ait.core.tardis.Tardis;
import dev.amble.ait.core.tardis.TardisDesktop;
import dev.amble.ait.core.tardis.handler.permissions.PermissionHandler;
import dev.amble.ait.core.tardis.manager.ServerTardisManager;
import dev.amble.ait.core.util.WorldUtil;
import dev.amble.ait.core.world.TardisServerWorld;
import dev.amble.ait.data.Loyalty;
import dev.amble.ait.mixin.lookup.EntityTrackingSectionAccessor;
import dev.amble.ait.mixin.lookup.SectionedEntityCacheAccessor;
import dev.amble.ait.mixin.lookup.SimpleEntityLookupAccessor;
import dev.amble.ait.mixin.lookup.WorldInvoker;

@SuppressWarnings("unused")
public class TardisUtil {

    public static final Identifier REGION_LANDING_CODE = AITMod.id("region_landing_code");
    public static final Identifier SNAP = AITMod.id("snap");
    public static final Identifier FLYING_SPEED = AITMod.id("flying_speed");
    public static final Identifier TOGGLE_ANTIGRAVS = AITMod.id("toggle_antigravs");
    public static final Identifier FIND_PLAYER = AITMod.id("find_player");

    public static void init() {
        ServerPlayNetworking.registerGlobalReceiver(SNAP, (server, player, handler, buf, responseSender) -> {
            UUID uuid = buf.readUuid();
            ServerTardisManager.getInstance().getTardis(server, uuid, tardis -> {
                PermissionHandler permissions = tardis.handler(TardisComponent.Id.PERMISSIONS);

                if (tardis.flight().isFlying()) {
                    if (!player.isSneaking()) {
                        tardis.door().interactAllDoors(player.getServerWorld(), null, player, true);
                    } else {
                        tardis.door().interactToggleLock(player);
                    }
                    return;
                }

                if (!tardis.loyalty().get(player).isOf(Loyalty.Type.PILOT))
                    return;

                player.getWorld().playSound(null, player.getBlockPos(), AITSounds.SNAP, SoundCategory.PLAYERS, 4f, 1f);

                BlockPos exteriorPos = tardis.travel().position().getPos();

                BlockPos pos = TardisServerWorld.isTardisDimension(player.getServerWorld())
                        ? tardis.getDesktop().getDoorPos().getPos()
                        : exteriorPos;

                if ((player.squaredDistanceTo(exteriorPos.getX(), exteriorPos.getY(), exteriorPos.getZ())) > 200
                        && player.getWorld() != tardis.world())
                    return;

                if (!player.isSneaking()) {
                    tardis.door().interact(player.getServerWorld(), null, player);
                } else {
                    boolean isLocked = tardis.door().locked();
                    tardis.door().interactToggleLock(player);
                    player.getWorld().playSound(
                            null,
                            pos,
                            isLocked ? AITSounds.REMOTE_UNLOCK : AITSounds.REMOTE_LOCK,
                            SoundCategory.BLOCKS,
                            1.0F,
                            1.0F
                    );
                }


            });
        });

        ServerPlayNetworking.registerGlobalReceiver(FLYING_SPEED, (server, player, handler, buf, responseSender) -> {
            UUID uuid = buf.readUuid();
            String direction = buf.readString();
            ServerTardisManager.getInstance().getTardis(server, uuid, tardis -> {
                if (!tardis.flight().isFlying()) return;
                switch (direction) {
                    case "up":
                        tardis.travel().increaseSpeed();
                        break;
                    case "down":
                        tardis.travel().decreaseSpeed();
                        break;
                }
            });
        });
        ServerPlayNetworking.registerGlobalReceiver(TOGGLE_ANTIGRAVS, (server, player, handler, buf, responseSender) -> {
            UUID uuid = buf.readUuid();
            ServerTardisManager.getInstance().getTardis(server, uuid, tardis -> {
                if (!tardis.flight().isFlying()) return;
                tardis.travel().antigravs().toggle();
            });
        });

        ServerPlayNetworking.registerGlobalReceiver(FIND_PLAYER,
                (server, currentPlayer, handler, buf, responseSender) -> {
                    UUID tardisId = buf.readUuid();
                    UUID playerUuid = buf.readUuid();

                    ServerTardisManager.getInstance().getTardis(server, tardisId, tardis -> {
                        ServerPlayerEntity serverPlayer = server.getPlayerManager().getPlayer(playerUuid);

                        if (serverPlayer == null) {
                            tardis.getDesktop().playSoundAtEveryConsole(SoundEvents.BLOCK_SCULK_SHRIEKER_BREAK,
                                    SoundCategory.BLOCKS, 3f, 1f);
                            return;
                        }

                        tardis.travel()
                                .forceDestination(CachedDirectedGlobalPos.create((ServerWorld) serverPlayer.getWorld(),
                                        serverPlayer.getBlockPos(),
                                        (byte) RotationPropertyHelper.fromYaw(serverPlayer.getBodyYaw())));

                        tardis.getDesktop().playSoundAtEveryConsole(SoundEvents.BLOCK_AMETHYST_BLOCK_CHIME,
                                SoundCategory.BLOCKS, 3f, 1f);
                    });
                });
    }

    public static boolean inBox(Box a, Box b) {
        return a.minX < b.maxX && a.maxX > b.minX && a.minZ < b.maxZ && a.maxZ > b.minZ;
    }

    public static Vec3d offsetInteriorDoorPosition(Tardis tardis) {
        return TardisUtil.offsetInteriorDoorPosition(tardis.getDesktop());
    }

    public static Vec3d offsetInteriorDoorPosition(TardisDesktop desktop) {
        return TardisUtil.offsetInteriorDoorPos(desktop.getDoorPos());
    }

    public static Vec3d offsetDoorPosition(Vec3d pos, byte rotation) {
        return switch (rotation) {
            case 1, 2, 3 -> new Vec3d(pos.getX() + 1.1f, pos.getY(), pos.getZ() - 0.5f);
            case 4 -> new Vec3d(pos.getX() + 1.5f, pos.getY(), pos.getZ() + 0.5f);
            case 5, 6, 7 -> new Vec3d(pos.getX() + 1.5f, pos.getY(), pos.getZ() + 1.1f);
            case 8 -> new Vec3d(pos.getX() + 0.5f, pos.getY(), pos.getZ() + 1.5f);
            case 9, 10, 11 -> new Vec3d(pos.getX(), pos.getY(), pos.getZ() + 1.5f);
            case 12 -> new Vec3d(pos.getX() - 0.5f, pos.getY(), pos.getZ() + 0.5f);
            case 13, 14, 15 -> new Vec3d(pos.getX() - 0.3f, pos.getY(), pos.getZ() - 0.5f);
            default -> new Vec3d(pos.getX() + 0.5f, pos.getY(), pos.getZ() - 0.5f);
        };
    }

    public static Vec3d offsetDoorPosition(DirectedBlockPos directed) {
        return offsetDoorPosition(new Vec3d(directed.getPos().getX(), directed.getPos().getY(), directed.getPos().getZ()), directed.getRotation());
    }

    public static Vec3d offsetInteriorDoorPos(DirectedBlockPos directed) {
        BlockPos pos = directed.getPos();

        return switch (directed.getRotation()) {
            case 4 -> new Vec3d(pos.getX() + 0.4f, pos.getY(), pos.getZ() + 0.5f);
            case 8 -> new Vec3d(pos.getX() + 0.5f, pos.getY(), pos.getZ() + 0.4f);
            case 12 -> new Vec3d(pos.getX() + 0.6f, pos.getY(), pos.getZ() + 0.5f);
            default -> new Vec3d(pos.getX() + 0.5f, pos.getY(), pos.getZ() + 0.6f);
        };
    }

    // TODO - move to amblekit
    public static Vec3d offsetPos(DirectedBlockPos directed, float value) {
        BlockPos pos = directed.getPos();

        return new Vec3d(pos.getX() + value * (double) directed.getVector().getX(),
                pos.getY() + value * (double) directed.getVector().getY(),
                pos.getZ() + value * (double) directed.getVector().getZ());
    }

    public static void teleportOutside(Tardis tardis, Entity entity) {
        TardisEvents.LEAVE_TARDIS.invoker().onLeave(tardis, entity);
        TardisUtil.teleportWithDoorOffset(tardis.travel().position().getWorld(), entity,
                tardis.travel().position().toPos());
    }

    public static void dropOutside(Tardis tardis, Entity entity) {
        TardisEvents.LEAVE_TARDIS.invoker().onLeave(tardis, entity);

        if (!(entity instanceof LivingEntity living))
            return;

        CachedDirectedGlobalPos percentageOfDestination = tardis.travel().getProgress();
        Scheduler scheduler = Scheduler.get();

        ServerWorld vortexWorld = WorldUtil.getTimeVortex();

        if (vortexWorld == null)
            return;

        TeleportUtil.teleport(living, vortexWorld, new Vec3d(vortexWorld.getRandom().nextBetween(0, 256), 0, vortexWorld.getRandom().nextBetween(0, 256)), living.getBodyYaw());

        scheduler.runTaskLater(() -> {
            if (living.getWorld() == vortexWorld) {
                TeleportUtil.teleport(living, tardis.travel().destination().getWorld(),
                        percentageOfDestination.getPos().toCenterPos(), living.getBodyYaw());
            }
        }, TaskStage.END_SERVER_TICK, TimeUnit.SECONDS, 4);
    }

    public static void teleportInside(ServerTardis tardis, Entity entity) {
        TardisEvents.ENTER_TARDIS.invoker().onEnter(tardis, entity);
        TardisUtil.teleportWithDoorOffset(tardis.world(), entity, tardis.getDesktop().getDoorPos());
    }

    public static void teleportToInteriorPosition(ServerTardis tardis, Entity entity, BlockPos pos) {
        if (entity instanceof ServerPlayerEntity player) {
            TardisEvents.ENTER_TARDIS.invoker().onEnter(tardis, entity);

            WorldUtil.teleportToWorld(player, tardis.world(),
                    new Vec3d(pos.getX(), pos.getY(), pos.getZ()), entity.getYaw(), player.getPitch());

            player.networkHandler.sendPacket(new EntityVelocityUpdateS2CPacket(player));
        }
    }

    private static void teleportWithDoorOffset(ServerWorld world, Entity entity, DirectedBlockPos directed) {
        if (!AITMod.CONFIG.tntCanTeleportThroughDoors && entity instanceof TntEntity) {
            return;
        }

        if (entity instanceof ExtraPushableEntity pushable && pushable.ait$pushBehaviour() == TriState.FALSE)
            return;

        BlockPos pos = directed.getPos();
        boolean isDoor = world.getBlockEntity(pos) instanceof DoorBlockEntity;

        Vec3d vec = isDoor
                ? TardisUtil.offsetInteriorDoorPos(directed)
                : TardisUtil.offsetDoorPosition(directed).add(0, 0.125, 0);

        world.getServer().execute(() -> {
            if (entity.getVehicle() instanceof FlightTardisEntity)
                return;

            if (entity instanceof ExtraPushableEntity pushable)
                pushable.ait$setPushBehaviour(TriState.FALSE);

            if (entity instanceof ServerPlayerEntity player) {
                WorldUtil.teleportToWorld(player, world, vec,
                        RotationPropertyHelper.toDegrees(directed.getRotation()) + (isDoor ? 0 : 180f),
                        player.getPitch());

                player.networkHandler.sendPacket(new EntityVelocityUpdateS2CPacket(player));
            } else {
                if (entity.getType().isIn(AITTags.EntityTypes.BOSS))
                    return;

                if (entity.getWorld().getRegistryKey() == world.getRegistryKey()) {
                    entity.refreshPositionAndAngles(offset(vec, directed, -0.5f).x, vec.y,
                            offset(vec, directed, -0.5f).z,
                            RotationPropertyHelper.toDegrees(directed.getRotation()) + (isDoor ? 0 : 180f),
                            entity.getPitch());
                } else {
                    entity.teleport(world, offset(vec, directed, -0.5f).x, vec.y, offset(vec, directed, -0.5f).z,
                            Set.of(),
                            RotationPropertyHelper.toDegrees(directed.getRotation()) + (isDoor ? 0 : 180f),
                            entity.getPitch());
                }
            }
            if (entity instanceof ExtraPushableEntity pushable)
                Scheduler.get().runTaskLater(() -> pushable.ait$setPushBehaviour(TriState.DEFAULT),
                        TaskStage.END_SERVER_TICK, TimeUnit.SECONDS, 3);
        });
    }

    public static Vec3d offset(Vec3d vec, DirectedBlockPos direction, double value) {
        Vec3i vec3i = direction.getVector();

        return new Vec3d(vec.x + value * (double) vec3i.getX(), vec.y + value * (double) vec3i.getY(),
                vec.z + value * (double) vec3i.getZ());
    }

    public static void giveEffectToInteriorPlayers(ServerTardis tardis, StatusEffectInstance effect) {
        for (PlayerEntity player : getPlayersInsideInterior(tardis)) {
            player.addStatusEffect(effect);
        }
    }

    public static @Nullable PlayerEntity getAnyPlayerInsideInterior(ServerWorld world) {
        for (PlayerEntity player : world.getPlayers()) {
            return player;
        }
        return null;
    }

    /**
     * @deprecated Use the {@link ServerTardis#world()} instead.
     */
    @Deprecated(forRemoval = true)
    public static List<ServerPlayerEntity> getPlayersInsideInterior(ServerTardis tardis) {
        return tardis.world().getPlayers();
    }

    public static <T extends Entity> List<T> getEntitiesInBox(Class<T> clazz, World world, Box box,
            Predicate<T> predicate) {
        return fastFlatLookup(clazz, world, box, predicate);
    }

    private static <T extends EntityLike> void forEachInFlatBox(SectionedEntityCacheAccessor<T> accessor, Box box,
            LazyIterationConsumer<EntityTrackingSection<T>> consumer) {
        int j = ChunkSectionPos.getSectionCoord(box.minX - 2.0);
        int l = ChunkSectionPos.getSectionCoord(box.minZ - 2.0);

        int m = ChunkSectionPos.getSectionCoord(box.maxX + 2.0);
        int o = ChunkSectionPos.getSectionCoord(box.maxZ + 2.0);

        for (int p = j; p <= m; p++) {
            long q = ChunkSectionPos.asLong(p, 0, 0);
            long r = ChunkSectionPos.asLong(p, -1, -1);

            LongBidirectionalIterator longIterator = accessor.getTrackedPositions().subSet(q, r + 1L).iterator();

            while (longIterator.hasNext()) {
                long s = longIterator.nextLong();
                int u = ChunkSectionPos.unpackZ(s);

                if (u < l || u > o)
                    continue;

                EntityTrackingSection<T> section = accessor.getTrackingSections().get(s);

                if (section == null || section.isEmpty() || !section.getStatus().shouldTrack()
                        || !consumer.accept(section).shouldAbort())
                    continue;

                return;
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static <U extends T, T extends EntityLike> void forEachIntersects(SectionedEntityCacheAccessor<T> cache,
            TypeFilter<T, U> filter, Box box, LazyIterationConsumer<U> consumer) {
        TardisUtil.forEachInFlatBox(cache, box, section -> {
            EntityTrackingSectionAccessor<T> accessor = (EntityTrackingSectionAccessor<T>) section;
            Collection<T> collection = accessor.getCollection().getAllOfType((Class<T>) filter.getBaseClass());

            if (collection.isEmpty())
                return LazyIterationConsumer.NextIteration.CONTINUE;

            for (T entityLike : collection) {
                U downcast = filter.downcast(entityLike);

                if (downcast == null || !TardisUtil.inBox(entityLike.getBoundingBox(), box)
                        || !consumer.accept(downcast).shouldAbort())
                    continue;

                return LazyIterationConsumer.NextIteration.ABORT;
            }

            return LazyIterationConsumer.NextIteration.CONTINUE;
        });
    }

    @SuppressWarnings("unchecked")
    private static <T extends Entity> List<T> fastFlatLookup(Class<T> clazz, World world, Box box,
            Predicate<T> predicate) {
        List<T> result = new ArrayList<>();
        world.getProfiler().visit("getEntities");

        SectionedEntityCacheAccessor<T> cache = (SectionedEntityCacheAccessor<T>) ((SimpleEntityLookupAccessor<T>) ((WorldInvoker) world)
                .getEntityLookup()).getCache();

        TardisUtil.forEachIntersects(cache, TypeFilter.instanceOf(clazz), box, entity -> {
            if (predicate.test(entity))
                result.add(entity);

            return LazyIterationConsumer.NextIteration.CONTINUE;
        });

        return result;
    }

    public static List<LivingEntity> getLivingEntitiesInInterior(Tardis tardis, int area) {
        DirectedBlockPos directedPos = tardis.getDesktop().getDoorPos();

        if (directedPos == null)
            return List.of();

        BlockPos pos = tardis.getDesktop().getDoorPos().getPos();

        return tardis.asServer().world().getEntitiesByClass(LivingEntity.class,
                new Box(pos.north(area).east(area).up(area), pos.south(area).west(area).down(area)), (e) -> true);
    }

    public static List<Entity> getEntitiesInInterior(Tardis tardis, int area) {
        DirectedBlockPos directedPos = tardis.getDesktop().getDoorPos();

        if (directedPos == null)
            return List.of();

        BlockPos pos = directedPos.getPos();

        return tardis.asServer().world().getEntitiesByClass(Entity.class,
                new Box(pos.north(area).east(area).up(area), pos.south(area).west(area).down(area)), e -> true);
    }

    public static List<LivingEntity> getLivingEntitiesInInterior(ServerTardis tardis) {
        return getLivingEntitiesInInterior(tardis, 20);
    }

    public static boolean isInteriorEmpty(ServerTardis tardis) {
        return tardis.world().getPlayers().isEmpty();
    }

    public static void sendMessageToInterior(ServerTardis tardis, Text text) {
        for (ServerPlayerEntity player : getPlayersInsideInterior(tardis)) {
            player.sendMessage(text, true);
        }
    }

    public static void sendMessageToLinked(ServerTardis tardis, Text message) {
        NetworkUtil.getLinkedPlayers(tardis).forEach(player -> player.sendMessage(message, true));
    }

    public static Optional<ServerPlayerEntity> findNearestPlayer(CachedDirectedGlobalPos position) {
        ServerWorld world = position.getWorld();
        BlockPos pos = position.getPos();
        ServerPlayerEntity nearestPlayer = null;
        double nearestDistance = Double.MAX_VALUE;

        for (ServerPlayerEntity player : world.getPlayers()) {
            double distance = player.squaredDistanceTo(pos.getX(), pos.getY(), pos.getZ());
            if (distance < nearestDistance) {
                nearestDistance = distance;
                nearestPlayer = player;
            }
        }

        return Optional.ofNullable(nearestPlayer);
    }

    public static boolean isNearTardis(PlayerEntity player, Tardis tardis, double radius) {
        return radius >= distanceFromTardis(player, tardis);
    }

    public static double distanceFromTardis(PlayerEntity player, Tardis tardis) {
        BlockPos pPos = player.getBlockPos();
        BlockPos tPos = tardis.travel().position().getPos();
        return Math.sqrt(tPos.getSquaredDistance(pPos));
    }
}
