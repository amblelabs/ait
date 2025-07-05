package dev.amble.ait.tardis.v2.handler;

import dev.amble.ait.api.tardis.v2.handler.THandler;
import dev.amble.ait.core.blocks.ExteriorBlock;
import dev.amble.ait.tardis.v2.Tardis;
import dev.amble.ait.tardis.v2.data.TravelData;
import dev.amble.ait.tardis.v2.event.TardisEvents;
import dev.amble.lib.data.CachedDirectedGlobalPos;
import net.minecraft.block.BlockState;
import net.minecraft.world.World;

public class TravelHandler implements THandler, TardisEvents {

    public void crash() {
        throw new RuntimeException("Unimplemented.");
    }

    @Override
    public void event$disablePower(Tardis tardis) {
        TravelData travel = tardis.resolve(TravelData.ID);
        travel.antigravs().set(false);

        this.updateExteriorState(tardis, false);
    }

    @Override
    public void event$enablePower(Tardis tardis) {
        this.updateExteriorState(tardis, true);
    }

    private void updateExteriorState(Tardis tardis, boolean power) {
        TravelData travel = tardis.resolve(TravelData.ID);

        if (travel.getState() != TravelHandler.State.LANDED)
            return;

        CachedDirectedGlobalPos pos = travel.position().get();
        World world = pos.getWorld();

        if (world == null)
            return;

        BlockState state = world.getBlockState(pos.getPos());

        if (!(state.getBlock() instanceof ExteriorBlock))
            return;

        world.setBlockState(pos.getPos(),
                state.with(ExteriorBlock.LEVEL_4, power ? 4 : 0));
    }
}
