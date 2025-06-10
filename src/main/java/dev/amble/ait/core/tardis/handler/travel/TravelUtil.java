package dev.amble.ait.core.tardis.handler.travel;

import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import dev.amble.lib.data.CachedDirectedGlobalPos;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

import dev.amble.ait.AITMod;
import dev.amble.ait.core.tardis.Tardis;
import dev.amble.ait.core.tardis.util.AsyncLocatorUtil;

public class TravelUtil {

    private static final int BASE_FLIGHT_TICKS = 5 * 20;

    public static void randomPos(Tardis tardis, int limit, int max, Consumer<CachedDirectedGlobalPos> consumer) {
        TravelHandler travel = tardis.travel();

        CompletableFuture<Void> future = CompletableFuture.supplyAsync(() -> {
            CachedDirectedGlobalPos dest = travel.destination();
            ServerWorld world = dest.getWorld();

            for (int i = 0; i <= limit; i++) {
                dest = dest.pos(
                        world.random.nextBoolean()
                                ? world.random.nextInt(max) == 0 ? 1 : world.random.nextInt(max)
                                : world.random.nextInt(max) == -0 ? -1 : -world.random.nextInt(max),
                        dest.getPos().getY(),
                        world.random.nextBoolean()
                                ? world.random.nextInt(max) == 0 ? 1 : world.random.nextInt(max)
                                : world.random.nextInt(max) == -0 ? -1 : -world.random.nextInt(max));
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
        boolean hasDirChanged = !(source.getRotation() == destination.getRotation());
        boolean hasDimChanged = !(source.getDimension().equals(destination.getDimension()));

        return (int) (BASE_FLIGHT_TICKS + (distance / 10f) + (hasDirChanged ? 100 : 0) + (hasDimChanged ? 600 : 0));
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
