package dev.drtheo.gaslighter.mixin;

import dev.drtheo.gaslighter.api.FakeBlockEvents;
import dev.drtheo.gaslighter.api.Twitter;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.block.BlockState;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;

@Mixin(value = ServerPlayNetworkHandler.class, priority = 1001)
public abstract class ServerPlayNetworkHandlerMixin {

    @Shadow public ServerPlayerEntity player;

    @Redirect(method = "onPlayerInteractBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerInteractionManager;interactBlock(Lnet/minecraft/server/network/ServerPlayerEntity;Lnet/minecraft/world/World;Lnet/minecraft/item/ItemStack;Lnet/minecraft/util/Hand;Lnet/minecraft/util/hit/BlockHitResult;)Lnet/minecraft/util/ActionResult;"))
    public ActionResult onPlayerInteractBlock(ServerPlayerInteractionManager instance, ServerPlayerEntity player, World world, ItemStack stack, Hand hand, BlockHitResult hitResult) {
        ServerWorld serverWorld = this.player.getServerWorld();
        BlockPos blockPos = hitResult.getBlockPos();

        if (serverWorld instanceof Twitter twitter && twitter.ait$isFake(blockPos)) {
            FakeBlockEvents.Action action = FakeBlockEvents.INTERACT.invoker().check(player, hand, hitResult.getBlockPos());

            if (action.shouldRemove())
                return ActionResult.PASS;
        }

        return instance.interactBlock(player, world, stack, hand, hitResult);
    }

    @Inject(method = "onPlayerInteractBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayNetworkHandler;sendPacket(Lnet/minecraft/network/packet/Packet;)V"), cancellable = true)
    public void onPlayerInteractBlock(PlayerInteractBlockC2SPacket packet, CallbackInfo ci) {
        ServerWorld serverWorld = this.player.getServerWorld();

        BlockHitResult blockHitResult = packet.getBlockHitResult();
        BlockPos blockPos = blockHitResult.getBlockPos();

        BlockState state = serverWorld.getBlockState(blockPos);

        if (serverWorld instanceof Twitter twitter && twitter.ait$isFake(blockPos)) {
            FakeBlockEvents.Action action = FakeBlockEvents.CHECK.invoker().check(player, packet.getHand(), state, blockPos);

            if (!action.shouldRemove())
                ci.cancel();
        }
    }
}
