package dev.amble.ait.compat.svc;

import java.util.ArrayList;
import java.util.List;

import dev.amble.ait.AITMod;
import dev.amble.ait.compat.DependencyChecker;
import dev.amble.ait.core.tardis.ServerTardis;
import dev.amble.ait.core.tardis.manager.ServerTardisManager;
import dev.amble.lib.data.CachedDirectedGlobalPos;
import dev.amble.lib.data.DirectedBlockPos;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.RotationPropertyHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public final class AITVoiceDoors {

    public record DoorLink(RegistryKey<World> interiorDim, Vec3d interior, RegistryKey<World> exteriorDim, Vec3d exterior,
                           boolean open, float turn) {

        public Vec3d toExterior(Vec3d point) {
            return this.exterior.add(point.subtract(this.interior).rotateY(-this.turn));
        }

        public Vec3d toInterior(Vec3d point) {
            return this.interior.add(point.subtract(this.exterior).rotateY(this.turn));
        }
    }

    private static volatile List<DoorLink> links = List.of();

    private AITVoiceDoors() {
    }

    public static void init() {
        ServerTickEvents.END_SERVER_TICK.register(AITVoiceDoors::tick);
    }

    public static List<DoorLink> doors() {
        return links;
    }

    private static void tick(MinecraftServer server) {
        List<DoorLink> current = new ArrayList<>();

        ServerTardisManager.getInstance().forEach(tardis -> {
            if (!tardis.hasWorld() || !tardis.travel().isLanded())
                return;

            boolean closed = tardis.door().isClosed();

            if (!closed && hasPortals(tardis))
                return;

            CachedDirectedGlobalPos exterior = tardis.travel().position();
            DirectedBlockPos door = tardis.getDesktop().getDoorPos();

            if (exterior == null || exterior.getWorld() == null || door == null)
                return;

            float turn = (float) Math.toRadians(180 + RotationPropertyHelper.toDegrees(exterior.getRotation())
                    - RotationPropertyHelper.toDegrees(door.getRotation()));

            current.add(new DoorLink(tardis.world().getRegistryKey(), door.getPos().toCenterPos().add(0, 0.5, 0),
                    exterior.getDimension(), exterior.getPos().toCenterPos().add(0, 0.5, 0), !closed, turn));
        });

        links = List.copyOf(current);
    }

    private static boolean hasPortals(ServerTardis tardis) {
        return DependencyChecker.hasPortals() && AITMod.CONFIG.allowPortalsBoti
                && tardis.getExterior().getVariant().hasPortals();
    }
}
