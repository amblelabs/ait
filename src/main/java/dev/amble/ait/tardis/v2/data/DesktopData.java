package dev.amble.ait.tardis.v2.data;

import dev.amble.ait.api.tardis.v2.data.TData;
import net.minecraft.server.world.ServerWorld;

import java.lang.ref.WeakReference;

public class DesktopData implements TData<DesktopData> {

    private WeakReference<ServerWorld> world;

    public ServerWorld getServerWorld() {
        return world.get();
    }
}
