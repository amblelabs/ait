package dev.amble.ait.core.tardis.util.network.c2s;

import dev.loqor.portal.server.BOTIPortalTracker;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import dev.amble.ait.AITMod;

import java.util.UUID;

/**
 * Client-to-Server packet to notify server when player starts watching a BOTI portal
 */
public class StartWatchingPortalC2SPacket implements FabricPacket {
    public static final PacketType<StartWatchingPortalC2SPacket> TYPE = 
        PacketType.create(AITMod.id("start_watching_portal"), StartWatchingPortalC2SPacket::new);
    
    private final UUID tardisId;
    private final boolean isExteriorView;
    private final RegistryKey<World> targetWorld;
    private final BlockPos targetPos;
    
    public StartWatchingPortalC2SPacket(UUID tardisId, boolean isExteriorView, RegistryKey<World> targetWorld, BlockPos targetPos) {
        this.tardisId = tardisId;
        this.isExteriorView = isExteriorView;
        this.targetWorld = targetWorld;
        this.targetPos = targetPos;
    }
    
    public StartWatchingPortalC2SPacket(PacketByteBuf buf) {
        this.tardisId = buf.readUuid();
        this.isExteriorView = buf.readBoolean();
        this.targetWorld = buf.readRegistryKey(RegistryKeys.WORLD);
        this.targetPos = buf.readBlockPos();
    }
    
    @Override
    public void write(PacketByteBuf buf) {
        buf.writeUuid(tardisId);
        buf.writeBoolean(isExteriorView);
        buf.writeRegistryKey(targetWorld);
        buf.writeBlockPos(targetPos);
    }
    
    @Override
    public PacketType<?> getType() {
        return TYPE;
    }
    
    public boolean handle(ServerPlayerEntity player, PacketSender responseSender) {
        if (player == null || player.getServer() == null) {
            return false;
        }
        
        ServerWorld targetServerWorld = player.getServer().getWorld(targetWorld);
        if (targetServerWorld == null) {
            return false;
        }
        
        // Notify the portal tracker on the server thread
        player.getServer().execute(() -> {
            BOTIPortalTracker.getInstance().onPlayerStartWatchingPortal(
                player, tardisId, isExteriorView, targetPos, targetServerWorld
            );
        });
        
        return true;
    }
}
