package dev.amble.ait.mixin.rwf;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import dev.amble.ait.core.entities.FlightTardisEntity;

/**
 * Stops a player from breaking blocks or interacting with the world while riding a
 * {@link FlightTardisEntity} during real world flight.
 */
@Mixin(ServerPlayerInteractionManager.class)
public class PlayerInteractionManagerMixin {

    @Shadow
    @Final
    protected ServerPlayerEntity player;

    private static boolean ait$isFlying(ServerPlayerEntity player) {
        return player != null && player.getVehicle() instanceof FlightTardisEntity;
    }

    @Inject(method = "processBlockBreakingAction", at = @At("HEAD"), cancellable = true)
    private void ait$blockBreaking(BlockPos pos, PlayerActionC2SPacket.Action action, Direction direction,
            int worldHeight, int sequence, CallbackInfo ci) {
        if (ait$isFlying(this.player))
            ci.cancel();
    }

    @Inject(method = "interactBlock", at = @At("HEAD"), cancellable = true)
    private void ait$interactBlock(ServerPlayerEntity player, World world, ItemStack stack, Hand hand,
            BlockHitResult hitResult, CallbackInfoReturnable<ActionResult> cir) {
        if (ait$isFlying(player))
            cir.setReturnValue(ActionResult.PASS);
    }

    @Inject(method = "interactItem", at = @At("HEAD"), cancellable = true)
    private void ait$interactItem(ServerPlayerEntity player, World world, ItemStack stack, Hand hand,
            CallbackInfoReturnable<ActionResult> cir) {
        if (ait$isFlying(player))
            cir.setReturnValue(ActionResult.PASS);
    }
}
