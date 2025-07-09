package dev.amble.ait.tardis.v2.data;

import dev.amble.ait.api.tardis.v2.data.TData;
import dev.amble.ait.api.tardis.v2.data.TDataHolder;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

import java.lang.ref.WeakReference;
import java.util.Collection;

public class DesktopData implements TData<DesktopData> {

    public static final TDataHolder<DesktopData> ID = null;

    private WeakReference<ServerWorld> world;

    public ServerWorld getServerWorld() {
        return world.get();
    }

    public Collection<BlockPos> consolePos() {

    }
}
