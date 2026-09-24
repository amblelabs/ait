package dev.amble.ait.mixin.compat.portals;

import dev.amble.ait.api.TeleportAware;
import dev.amble.ait.api.tardis.TardisEvents;
import dev.amble.ait.compat.portal.TardisPortal;
import dev.amble.ait.core.tardis.ServerTardis;
import dev.amble.ait.core.tardis.util.TardisUtil;
import dev.amble.ait.core.world.TardisServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import qouteall.imm_ptl.core.teleportation.ServerTeleportationManager;

import net.minecraft.entity.Entity;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

@Mixin(ServerTeleportationManager.class)
public class ServerTeleportationManagerMixin {

    @Inject(method = "canPlayerTeleport", at = @At("RETURN"), cancellable = true)
    private void preventTemperamentEntry(ServerPlayerEntity player, RegistryKey<World> dimensionBefore, Vec3d posBefore,
                                  Entity portalEntity, CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValueZ() || !(portalEntity instanceof TardisPortal portal)
                || !portal.isExteriorPortal())
            return;

        ServerTardis tardis = portal.getServerTardis();
        if (tardis != null && tardis.temperament().preventPortalEntry(player)) {
            movePlayerInFront(player, portal, posBefore);
            cir.setReturnValue(false);
        }
    }

    private static void movePlayerInFront(ServerPlayerEntity player, TardisPortal portal, Vec3d previousPos) {
        Vec3d normal = portal.getNormal();
        Box box = player.getBoundingBox();
        double halfExtent = 0.5 * (Math.abs(normal.x) * (box.maxX - box.minX)
                + Math.abs(normal.y) * (box.maxY - box.minY)
                + Math.abs(normal.z) * (box.maxZ - box.minZ));
        double offset = Math.max(0, halfExtent + 0.25 - portal.getDistanceToPlane(previousPos));
        Vec3d safePos = previousPos.add(normal.multiply(offset));

        player.refreshPositionAndAngles(safePos.x, safePos.y, safePos.z, player.getYaw(), player.getPitch());

        Vec3d velocity = player.getVelocity();
        double inwardVelocity = velocity.dotProduct(normal);
        if (inwardVelocity < 0)
            player.setVelocity(velocity.subtract(normal.multiply(inwardVelocity)));
        player.networkHandler.sendPacket(new EntityVelocityUpdateS2CPacket(player));
    }

    @Inject(method = "teleportPlayer", at = @At("HEAD"))
    private void markPortalTeleport(ServerPlayerEntity player, RegistryKey<World> dimensionTo, Vec3d newEyePos,
                                    CallbackInfo ci) {
        TeleportAware.markTeleported(player);
    }

    @Inject(method = "changePlayerDimension", at = @At("TAIL"))
    private static void onTeleported(ServerPlayerEntity player, ServerWorld fromWorld, ServerWorld toWorld, Vec3d newEyePos, CallbackInfo ci) {
        if (fromWorld instanceof TardisServerWorld tsw)
            TardisEvents.LEAVE_TARDIS.invoker().onLeave(tsw.getTardis(), player);

        if (toWorld instanceof TardisServerWorld tsw) {
            if (TardisEvents.ENTER_TARDIS.invoker().onEnter(tsw.getTardis(), player) == TardisEvents.Interaction.FAIL) {
                TardisUtil.teleportOutside(tsw.getTardis(), player);
            }
        }
    }
}
