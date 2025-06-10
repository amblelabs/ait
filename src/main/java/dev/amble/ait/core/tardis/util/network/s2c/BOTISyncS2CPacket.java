package dev.amble.ait.core.tardis.util.network.s2c;

import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PacketType;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import dev.amble.ait.AITMod;
import dev.amble.ait.core.blockentities.ExteriorBlockEntity;
import dev.amble.ait.core.tardis.Tardis;

public class BOTISyncS2CPacket implements FabricPacket {
    public static final PacketType<BOTISyncS2CPacket> TYPE = PacketType.create(AITMod.id("boti_sync"), BOTISyncS2CPacket::new);
    private final BlockPos pos;
    private final RegistryKey<World> targetWorld;
    private final BlockPos targetPos;

    public BOTISyncS2CPacket(BlockPos pos, RegistryKey<World> targetWorld, BlockPos targetPos) {
        this.pos = pos;
        this.targetWorld = targetWorld;
        this.targetPos = targetPos;
    }

    public BOTISyncS2CPacket(PacketByteBuf buf) {
        this.pos = buf.readBlockPos();
        this.targetWorld = buf.readRegistryKey(RegistryKeys.WORLD);
        this.targetPos = buf.readBlockPos();
    }
    @Override
    public void write(PacketByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeRegistryKey(targetWorld);
        buf.writeBlockPos(targetPos);
    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }

    @SuppressWarnings("unchecked")
    public <T> boolean handle(ClientPlayerEntity source, PacketSender response) {
        MinecraftClient client = MinecraftClient.getInstance();
        World world = client.world;
        if (world == null) return false;

        BlockEntity exterior = world.getBlockEntity(this.pos);

        if (exterior instanceof ExteriorBlockEntity exteriorBlockEntity) {
            if (!exteriorBlockEntity.isLinked()) return false;
            Tardis tardis = exteriorBlockEntity.tardis().get();
            // tardis.stats().setTargetWorld(exteriorBlockEntity, this.targetWorld, this.targetPos, false);
        }
        return true;
    }
}
