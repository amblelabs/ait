package dev.amble.ait.core.tardis.handler.travel;

import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import dev.amble.ait.AITMod;
import dev.amble.ait.core.tardis.Tardis;
import dev.amble.ait.core.tardis.handler.FuelHandler;
import dev.amble.ait.core.tardis.util.AsyncLocatorUtil;
import dev.amble.lib.data.CachedDirectedGlobalPos;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class TravelUtil {

    private static final int BASE_FLIGHT_TICKS = 5 * 20;

    public static void randomPos(Tardis tardis, int limit, int max, Consumer<CachedDirectedGlobalPos> consumer) {
        TravelHandler travel = tardis.travel();

        CompletableFuture<Void> future = CompletableFuture.supplyAsync(() -> {
            CachedDirectedGlobalPos dest = travel.destination();
            ServerWorld world = dest.getWorld();

            int posX = dest.getPos().getX();
            int posZ = dest.getPos().getZ();

            for (int i = 0; i <= limit; i++) {
                dest = dest.pos(
                        world.random.nextBoolean()
                                ? world.random.nextInt(max) == 0 ? posX + 1 : posX + world.random.nextInt(max)
                                : world.random.nextInt(max) == -0 ? posX - 1 : posX - world.random.nextInt(max),
                        dest.getPos().getY(),
                        world.random.nextBoolean()
                                ? world.random.nextInt(max) == 0 ? posZ + 1 : posZ + world.random.nextInt(max)
                                : world.random.nextInt(max) == -0 ? posZ - 1 : posZ - world.random.nextInt(max));
            }

            return dest;
        }).thenAccept(consumer);

        AsyncLocatorUtil.LOCATING_EXECUTOR_SERVICE.submit(() -> future);
    }

    public static void travelTo(Tardis tardis, CachedDirectedGlobalPos pos) {
        TravelHandler travel = tardis.travel();

        travel.autopilot(true);
        travel.destination(pos);

        if (travel.getState() == TravelHandlerBase.State.LANDED)
            travel.dematerialize();
    }

    public static CachedDirectedGlobalPos getPositionFromPercentage(CachedDirectedGlobalPos source,
                                                                    CachedDirectedGlobalPos destination, int percentage) {
        // https://stackoverflow.com/questions/33907276/calculate-point-between-two-coordinates-based-on-a-percentage
        if (percentage == 0)
            return source;

        if (percentage == 100)
            return destination;

        float per = percentage / 100f;
        BlockPos pos = source.getPos();
        BlockPos diff = destination.getPos().subtract(pos);

        return destination
                .pos(pos.add((int) (diff.getX() * per), (int) (diff.getY() * per), (int) (diff.getZ() * per)));
    }

    public static int getFlightDuration(CachedDirectedGlobalPos source, CachedDirectedGlobalPos destination) {
        float distance = MathHelper.sqrt((float) source.getPos().getSquaredDistance(destination.getPos()));

        boolean hasDirChanged = source.getRotation() != destination.getRotation();
        boolean hasDimChanged = !source.getDimension().equals(destination.getDimension());

        if (distance < 128 && !hasDimChanged)
            return 1; // fast travel

        return (int) (BASE_FLIGHT_TICKS + (distance / 10f) + (hasDirChanged ? 100 : 0) + (hasDimChanged ? 600 : 0));
    }

    /**
     * Calculates the nominal fuel cost of the complete {@link TravelHandlerBase.State#FLIGHT FLIGHT} leg.
     * This mirrors the flight progress cadence before any automatic travel shortens its target time and
     * deliberately excludes the fuel consumed during dematerialization and materialization.
     */
    public static double getNominalFlightFuelCost(TravelHandler travel, CachedDirectedGlobalPos source,
                                                   CachedDirectedGlobalPos destination) {
        return getNominalFlightFuelCost(travel, source, destination, Math.max(travel.speed(), 1),
                travel.autopilot());
    }

    public static double getNominalFlightFuelCost(TravelHandler travel, CachedDirectedGlobalPos source,
                                                   CachedDirectedGlobalPos destination, int speed,
                                                   boolean autopilot) {
        int effectiveSpeed = Math.max(speed, 1);
        int instability = travel.instability();
        int progressPerUpdate = AITMod.CONFIG.travelPerTick + instability - 1;
        int updateCadence = Math.max(travel.maxSpeed().get() - effectiveSpeed + 1, 1);
        int updatesRequired = MathHelper.ceil((float) getFlightDuration(source, destination) / progressPerUpdate);
        long nominalFlightTicks = (long) updatesRequired * updateCadence;

        return nominalFlightTicks * FuelHandler.getPerTickFuelCost(effectiveSpeed, instability, autopilot);
    }

    public static CachedDirectedGlobalPos jukePos(CachedDirectedGlobalPos pos, int min, int max, int multiplier) {
        Random random = AITMod.RANDOM;
        multiplier *= random.nextInt(0, 2) == 0 ? 1 : -1;

        return pos.offset(random.nextInt(min, max) * multiplier, 0,
                random.nextInt(min, max) * multiplier);
    }

    public static CachedDirectedGlobalPos jukePos(CachedDirectedGlobalPos pos, int min, int max) {
        return jukePos(pos, min, max, 1);
    }
}
