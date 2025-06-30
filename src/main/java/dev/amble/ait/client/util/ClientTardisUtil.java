package dev.amble.ait.client.util;

import static dev.amble.ait.core.tardis.util.TardisUtil.*;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import dev.amble.ait.api.ClientWorldEvents;
import dev.amble.ait.api.tardis.link.v2.TardisRef;
import dev.amble.ait.client.tardis.ClientTardis;
import dev.amble.ait.client.tardis.manager.ClientTardisManager;
import dev.amble.ait.core.tardis.Tardis;
import dev.amble.ait.core.tardis.TardisExterior;
import dev.amble.ait.core.tardis.handler.SonicHandler;
import dev.amble.ait.core.world.TardisServerWorld;
import dev.amble.ait.data.schema.sonic.SonicSchema;

@Environment(EnvType.CLIENT)
public class ClientTardisUtil {

    public static final int MAX_POWER_DELTA_TICKS = 3 * 20;
    public static final int MAX_ALARM_DELTA_TICKS = 60;

    private static int alarmDeltaTick;
    private static boolean alarmDeltaDirection; // true for increasing false for decreasing
    private static int powerDeltaTick;

    private static TardisRef currentTardis;

    static {
        ClientWorldEvents.CHANGE_WORLD.register((client, world) -> {
            UUID id = TardisServerWorld.getTardisId(world);
            currentTardis = new TardisRef(id, uuid -> ClientTardisManager.getInstance().demandTardis(uuid));
        });
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            UUID id = TardisServerWorld.getTardisId(client.world);
            currentTardis = new TardisRef(id, uuid -> ClientTardisManager.getInstance().demandTardis(uuid));
        });
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            currentTardis = null;
        });
    }

    public static void changeExteriorWithScreen(UUID uuid, Identifier variant, boolean variantchange) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeUuid(uuid);
        buf.writeBoolean(variantchange);
        buf.writeIdentifier(variant);
        ClientPlayNetworking.send(TardisExterior.CHANGE_EXTERIOR, buf);
    }

    public static void changeExteriorWithScreen(ClientTardis tardis, Identifier variant, boolean variantchange) {
        changeExteriorWithScreen(tardis.getUuid(), variant, variantchange);
    }

    public static void changeSonicWithScreen(UUID uuid, SonicSchema schema, BlockPos consolePos) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeUuid(uuid);
        buf.writeIdentifier(schema.id());
        buf.writeBlockPos(consolePos);
        ClientPlayNetworking.send(SonicHandler.CHANGE_SONIC, buf);
    }

    public static void snapToOpenDoors(Tardis tardis) {
        snapToOpenDoors(tardis.getUuid());
    }

    public static void snapToOpenDoors(UUID uuid) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeUuid(uuid);

        ClientPlayNetworking.send(SNAP, buf);
    }

    public static void flyingSpeedPacket(Tardis tardis, String direction) {
        flyingSpeedPacket(tardis.getUuid(), direction);
    }

    public static void flyingSpeedPacket(UUID uuid, String direction) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeUuid(uuid);
        buf.writeString(direction);

        ClientPlayNetworking.send(FLYING_SPEED, buf);
    }

    public static void toggleAntigravs(Tardis tardis) {
        toggleAntigravs(tardis.getUuid());
    }

    public static void toggleAntigravs(UUID uuid) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeUuid(uuid);

        ClientPlayNetworking.send(TOGGLE_ANTIGRAVS, buf);
    }

    public static boolean isPlayerInATardis() {
        return currentTardis != null;
    }

    /**
     * Gets the tardis the player is currently inside
     */
    public static ClientTardis getCurrentTardis() {
        if (currentTardis == null)
            return null;

        return (ClientTardis) currentTardis.get();
    }

    public static Optional<ClientTardis> getNearestTardis(double radius) {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;

        if (player == null)
            return Optional.empty();

        BlockPos pos = player.getBlockPos();
        RegistryKey<World> dimension = player.getWorld().getRegistryKey();

        // doesnt find nearest, only finds if within radius.
        // could be more performant though
        /*
        return ClientTardisManager.getInstance().find(tardis -> {
            if (!tardis.travel().position().getDimension().equals(dimension))
                return false;

            BlockPos tPos = tardis.travel().position().getPos();
            double distance = Math.sqrt(pos.getSquaredDistance(tPos));

            return distance < radius;
        });
        */

        AtomicReference<ClientTardis> nearestTardis = new AtomicReference<>();
        AtomicReference<Double> nearestDistance = new AtomicReference<>(Double.MAX_VALUE);

        ClientTardisManager.getInstance().forEach(tardis -> {
            if (!tardis.travel().position().getDimension().equals(dimension))
                return;

            BlockPos tPos = tardis.travel().position().getPos();
            double distance = Math.sqrt(pos.getSquaredDistance(tPos));

            if (distance < radius && distance < nearestDistance.get()) {
                nearestDistance.set(distance);
                nearestTardis.set(tardis);
            }
        });

        return Optional.ofNullable(nearestTardis.get());
    }

    public static double distanceFromConsole() {
        if (!isPlayerInATardis())
            return 0;

        ClientPlayerEntity player = MinecraftClient.getInstance().player;

        if (player == null)
            return 0;

        Tardis tardis = getCurrentTardis();

        if (tardis == null)
            return 0;

        BlockPos pos = player.getBlockPos();
        double lowest = Double.MAX_VALUE;

        for (BlockPos console : tardis.getDesktop().getConsolePos()) {
            double distance = Math.sqrt(pos.getSquaredDistance(console));

            if (distance < lowest)
                lowest = distance;
        }

        return lowest;
    }

    public static BlockPos getNearestConsole() {
        if (!isPlayerInATardis())
            return BlockPos.ORIGIN;

        ClientPlayerEntity player = MinecraftClient.getInstance().player;

        if (player == null)
            return BlockPos.ORIGIN;

        Tardis tardis = getCurrentTardis();

        if (tardis == null)
            return BlockPos.ORIGIN;

        BlockPos pos = player.getBlockPos();
        double lowest = Double.MAX_VALUE;
        BlockPos nearest = BlockPos.ORIGIN;

        for (BlockPos console : tardis.getDesktop().getConsolePos()) {
            double distance = Math.sqrt(pos.getSquaredDistance(console));

            if (distance < lowest) {
                lowest = distance;
                nearest = console;
            }
        }

        return nearest;
    }

    public static void tickPowerDelta() {
        Tardis tardis = getCurrentTardis();

        if (tardis == null) {
            powerDeltaTick = MAX_POWER_DELTA_TICKS;
            return;
        }

        if (tardis.fuel().hasPower() && getPowerDelta() < MAX_POWER_DELTA_TICKS) {
            powerDeltaTick++;
        } else if (!tardis.fuel().hasPower() && getPowerDelta() > 0) {
            powerDeltaTick--;
        }
    }

    public static int getPowerDelta() {
        return currentTardis != null ? powerDeltaTick : 0;
    }

    public static float getPowerDeltaForLerp() {
        return (float) getPowerDelta() / MAX_POWER_DELTA_TICKS;
    }

    public static void tickAlarmDelta() {
        Tardis tardis = getCurrentTardis();

        if (tardis == null || !tardis.alarm().isEnabled()) {
            alarmDeltaTick = MAX_ALARM_DELTA_TICKS;
            return;
        }

        if (alarmDeltaTick < MAX_ALARM_DELTA_TICKS && alarmDeltaDirection) {
            alarmDeltaTick++;
        } else if (alarmDeltaTick > 0 && !alarmDeltaDirection) {
            alarmDeltaTick--;
        }

        if (alarmDeltaTick >= MAX_ALARM_DELTA_TICKS)
            alarmDeltaDirection = false;

        if (alarmDeltaTick == 0)
            alarmDeltaDirection = true;
    }

    public static int getAlarmDelta() {
        if (!isPlayerInATardis())
            return 0;

        return alarmDeltaTick;
    }

    public static float getAlarmDeltaForLerp() {
        return (float) getAlarmDelta() / MAX_ALARM_DELTA_TICKS;
    }

    public static float[] getPartyColors() {
        final int m = 25;
        final PlayerEntity player = MinecraftClient.getInstance().player;

        int n = player.age / m + player.getId();
        int o = DyeColor.values().length;
        int p = n % o;
        int q = (n + 1) % o;
        float r = ((float)(player.age % m)) / m;
        float[] fs = SheepEntity.getRgbColor(DyeColor.byId(p));
        float[] gs = SheepEntity.getRgbColor(DyeColor.byId(q));

        float s = fs[0] * (1f - r) + gs[0] * r;
        float t = fs[1] * (1f - r) + gs[1] * r;
        float u = fs[2] * (1f - r) + gs[2] * r;

        return new float[] { s, t, u };
    }
}
