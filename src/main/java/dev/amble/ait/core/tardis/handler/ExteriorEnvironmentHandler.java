package dev.amble.ait.core.tardis.handler;

import dev.amble.ait.api.MojangYoinkySploinky;
import dev.amble.lib.data.CachedDirectedGlobalPos;

import net.minecraft.block.Blocks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import dev.amble.ait.api.tardis.KeyedTardisComponent;
import dev.amble.ait.api.tardis.TardisEvents;
import dev.amble.ait.api.tardis.TardisTickable;
import dev.amble.ait.core.tardis.handler.travel.TravelHandler;
import dev.amble.ait.core.tardis.handler.travel.TravelHandlerBase;
import dev.amble.ait.data.properties.bool.BoolProperty;
import dev.amble.ait.data.properties.bool.BoolValue;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

public class ExteriorEnvironmentHandler extends KeyedTardisComponent implements TardisTickable {

    private static final BoolProperty RAINING = new BoolProperty("raining", false);
    private static final BoolProperty THUNDERING = new BoolProperty("thundering", false);
    private static final BoolProperty LAVA = new BoolProperty("lava", false);

    private final BoolValue raining = RAINING.create(this);
    private final BoolValue thundering = THUNDERING.create(this);
    private final BoolValue lava = LAVA.create(this);

    static {
        TardisEvents.LANDED.register(tdis -> {
            tdis.<ExteriorEnvironmentHandler>handler(Id.ENVIRONMENT).updateLava();
        });

        TardisEvents.DEMAT.register(tdis -> {
            tdis.<ExteriorEnvironmentHandler>handler(Id.ENVIRONMENT).updateLava();
            return TardisEvents.Interaction.PASS;
        });
    }

    public ExteriorEnvironmentHandler() {
        super(Id.ENVIRONMENT);
    }

    @Override
    public void onLoaded() {
        this.raining.of(this, RAINING);
        this.thundering.of(this, THUNDERING);
        this.lava.of(this, LAVA);
    }

    @Override
    public void tick(MinecraftServer server) {
        if (server.getTicks() % 20 != 0)
            return;

        TravelHandler travel = this.tardis.travel();
        World exterior = travel.position().getWorld();

        if (exterior == null) return;

        boolean isRaining = false;
        boolean isThundering = false;
        
        if (travel.getState() == TravelHandlerBase.State.LANDED) {
            boolean snowy = tardis.<BiomeHandler>handler(Id.BIOME).getBiomeKey() == BiomeHandler.BiomeType.SNOWY;

            isRaining = !snowy && exterior.isRaining();
            isThundering = !snowy && exterior.isThundering();

            if (isRaining || isThundering) {
                boolean hasRain = exterior.hasRain(travel.position().getPos());
                
                isRaining = isRaining && hasRain;
                isThundering = isThundering && hasRain;
            }
        }

        this.raining.set(isRaining);
        this.thundering.set(isThundering);
    }

    private void updateLava() {
        this.lava.set(false);

        if (this.tardis.travel().getState() == TravelHandlerBase.State.LANDED)
            this.performLavaCheck();
    }

    public boolean isRaining() {
        return this.raining.get();
    }

    public boolean isThundering() {
        return this.thundering.get();
    }

    public boolean hasLava() {
        return this.lava.get();
    }

    private void performLavaCheck() {
        if (this.isClient())
            return;

        CachedDirectedGlobalPos cached = tardis.travel().position();

        ServerWorld world = cached.getWorld();
        BlockPos tardisPos = cached.getPos();

        Iterable<BlockPos> positions = BlockPos.iterate(tardisPos.add(-1, 0, -1), tardisPos.add(1, 0, 1));
        CompletableFuture<Boolean>[] futures = new CompletableFuture[9];

        AtomicBoolean done = new AtomicBoolean(false);

        int i = 0;
        for (BlockPos pos : positions) {
            futures[i] = MojangYoinkySploinky.getBlockState(world, pos)
                    .thenApply(blockState -> blockState.getBlock() == Blocks.LAVA);

            i++;
        }

        for (CompletableFuture<Boolean> f : futures) {
            if (f == null) break;

            f.whenComplete((value, ex) -> {
                if (done.get()) return;
                if (ex == null) {
                    if (done.compareAndSet(false, true)) {
                        this.lava.set(true);
                    }
                }
            });
        }
    }
}
