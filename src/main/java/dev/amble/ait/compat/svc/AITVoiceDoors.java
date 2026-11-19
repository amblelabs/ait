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

    private static volatile List<VoiceLink> links = List.of();

    private AITVoiceDoors() {
    }

    public static void init() {
        ServerTickEvents.END_SERVER_TICK.register(AITVoiceDoors::tick);
    }

    public static List<VoiceLink> links() {
        return links;
    }

    private static void tick(MinecraftServer server) {
        List<VoiceLink> current = new ArrayList<>();

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

            double turn = Math.toRadians(180 + RotationPropertyHelper.toDegrees(exterior.getRotation())
                    - RotationPropertyHelper.toDegrees(door.getRotation()));

            RegistryKey<World> interiorDim = tardis.world().getRegistryKey();
            RegistryKey<World> exteriorDim = exterior.getDimension();
            Vec3d inside = door.getPos().toCenterPos().add(0, 0.5, 0);
            Vec3d outside = exterior.getPos().toCenterPos().add(0, 0.5, 0);

            if (closed) {
                current.add(new VoiceLink(interiorDim, inside, exteriorDim, point -> outside, true));
                current.add(new VoiceLink(exteriorDim, outside, interiorDim, point -> inside, true));
                return;
            }

            current.add(new VoiceLink(interiorDim, inside, exteriorDim,
                    point -> outside.add(point.subtract(inside).rotateY((float) -turn)), false));
            current.add(new VoiceLink(exteriorDim, outside, interiorDim,
                    point -> inside.add(point.subtract(outside).rotateY((float) turn)), false));
        });

        links = List.copyOf(current);
    }

    private static boolean hasPortals(ServerTardis tardis) {
        return DependencyChecker.hasPortals() && AITMod.CONFIG.allowPortalsBoti
                && tardis.getExterior().getVariant().hasPortals();
    }
}
