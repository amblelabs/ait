package dev.amble.ait.core.tardis.handler;

import dev.amble.ait.api.tardis.KeyedTardisComponent;
import dev.amble.ait.api.tardis.TardisEvents;
import dev.amble.ait.api.tardis.TardisTickable;
import dev.amble.ait.core.tardis.handler.travel.TravelHandler;
import dev.amble.ait.core.tardis.handler.travel.TravelHandlerBase;
import dev.amble.ait.data.properties.bool.BoolProperty;
import dev.amble.ait.data.properties.bool.BoolValue;
import dev.amble.lib.data.CachedDirectedGlobalPos;
import net.minecraft.block.Blocks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ExteriorEnvironmentHandler extends KeyedTardisComponent implements TardisTickable {

    private static final BoolProperty RAINING = new BoolProperty("raining", false);
    private static final BoolProperty THUNDERING = new BoolProperty("thundering", false);
    private static final BoolProperty LAVA = new BoolProperty("lava", false);

    private final BoolValue raining = RAINING.create(this);
    private final BoolValue thundering = THUNDERING.create(this);
    private final BoolValue lava = LAVA.create(this);

    static {
        TardisEvents.LANDED.register((tdis) -> {
            tdis.<ExteriorEnvironmentHandler>handler(Id.ENVIRONMENT).updateLava();
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
        if (server.getTicks() % 10 != 0)
            return;

        TravelHandler travel = this.tardis.travel();
        World exterior = travel.position().getWorld();

        if (exterior == null || exterior.isClient()) return;

        boolean snowy = tardis.<BiomeHandler>handler(Id.BIOME).getBiomeKey() == BiomeHandler.BiomeType.SNOWY;

        boolean isRaining = exterior.hasRain(travel.position().getPos()) && exterior.isRaining() && !snowy;
        boolean isThundering = exterior.hasRain(travel.position().getPos()) && exterior.isThundering() && !snowy;

        if (travel.getState() != TravelHandlerBase.State.LANDED) {
            isRaining = false;
            isThundering = false;
        }

        if (this.isRaining() != isRaining)
            this.raining.set(isRaining);

        if (this.isThundering() != isThundering)
            this.thundering.set(isThundering);
    }

    private void updateLava() {
        boolean hasLava = this.isInLava();

        if (this.tardis.travel().getState() != TravelHandlerBase.State.LANDED)
            hasLava = false;

        if (this.hasLava() != hasLava)
            this.lava.set(hasLava);
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

    private boolean isInLava() {
        if (this.isClient())
            return false;

        CachedDirectedGlobalPos cached = tardis.travel().position();

        World world = cached.getWorld();
        BlockPos tardisPos = cached.getPos();

        for (int xOffset = -1; xOffset <= 1; xOffset++) {
            for (int zOffset = -1; zOffset <= 1; zOffset++) {
                BlockPos blockPos = tardisPos.add(xOffset, 0, zOffset);

                if (world.getBlockState(blockPos).getBlock() == Blocks.LAVA)
                    return true;
            }
        }

        return false;
    }
}
