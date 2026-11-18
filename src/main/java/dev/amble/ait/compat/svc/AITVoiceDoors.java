package dev.amble.ait.compat.svc;

import java.util.ArrayList;
import java.util.List;

import dev.amble.ait.core.tardis.manager.ServerTardisManager;
import dev.amble.lib.data.CachedDirectedGlobalPos;
import dev.amble.lib.data.DirectedBlockPos;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public final class AITVoiceDoors {

    public record DoorLink(RegistryKey<World> interiorDim, Vec3d interior, RegistryKey<World> exteriorDim, Vec3d exterior) {
    }

    private static volatile List<DoorLink> closed = List.of();

    private AITVoiceDoors() {
    }

    public static void init() {
        ServerTickEvents.END_SERVER_TICK.register(AITVoiceDoors::tick);
    }

    public static List<DoorLink> closedDoors() {
        return closed;
    }

    private static void tick(MinecraftServer server) {
        List<DoorLink> links = new ArrayList<>();

        ServerTardisManager.getInstance().forEach(tardis -> {
            if (!tardis.hasWorld() || !tardis.travel().isLanded() || !tardis.door().isClosed())
                return;

            CachedDirectedGlobalPos exterior = tardis.travel().position();
            DirectedBlockPos door = tardis.getDesktop().getDoorPos();

            if (exterior == null || exterior.getWorld() == null || door == null)
                return;

            links.add(new DoorLink(tardis.world().getRegistryKey(), door.getPos().toCenterPos().add(0, 0.5, 0),
                    exterior.getDimension(), exterior.getPos().toCenterPos().add(0, 0.5, 0)));
        });

        closed = List.copyOf(links);
    }
}
