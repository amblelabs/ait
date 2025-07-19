package dev.drtheo.gaslighter.mixin;

import dev.drtheo.gaslighter.api.FakeBlockEvents;
import dev.drtheo.gaslighter.api.Twitter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.block.BlockState;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

@Mixin(ServerPlayNetworkHandler.class)
public abstract class ServerPlayNetworkHandlerMixin {

    @Shadow public ServerPlayerEntity player;

    @Redirect(method = "onPlayerInteractBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayNetworkHandler;sendPacket(Lnet/minecraft/network/packet/Packet;)V"))
    public void onPlayerInteractBlock(ServerPlayNetworkHandler instance, Packet<?> packet) { }

    @Inject(method = "onPlayerInteractBlock", at = @At("TAIL"))
    public void onPlayerInteractBlock(PlayerInteractBlockC2SPacket packet, CallbackInfo ci) {
        ServerWorld serverWorld = this.player.getServerWorld();

        BlockHitResult blockHitResult = packet.getBlockHitResult();
        BlockPos blockPos = blockHitResult.getBlockPos();

        Direction direction = blockHitResult.getSide();
        BlockState state = serverWorld.getBlockState(blockPos);

        if (serverWorld instanceof Twitter twitter && twitter.ait$isFake(blockPos)) {
            FakeBlockEvents.CHECK.invoker().check(player, state, blockPos);
            return;
        }

        this.ait$sendPacket(new BlockUpdateS2CPacket(blockPos, state));
        this.ait$sendPacket(new BlockUpdateS2CPacket(serverWorld, blockPos.offset(direction)));
    }

    @Unique private void ait$sendPacket(Packet<?> packet) {
        this.player.networkHandler.sendPacket(packet);
    }
}
