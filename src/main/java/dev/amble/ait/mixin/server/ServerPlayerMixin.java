package dev.amble.ait.mixin.server;

import dev.amble.ait.core.AITItems;
import dev.amble.ait.core.item.SiegeTardisItem;
import dev.amble.ait.core.tardis.manager.ServerTardisManager;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.server.network.ServerPlayerEntity;

import dev.amble.ait.core.entities.FlightTardisEntity;
import dev.amble.ait.core.tardis.util.TardisUtil;
import dev.amble.ait.core.world.TardisServerWorld;

import java.util.Objects;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerMixin {

    @Inject(method = "tick", at = @At("TAIL"))
    private void ait$tick(CallbackInfo ci) {
        ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;

        // if player is in tardis and y is less than -100 save them
        if (player.getY() <= -100 && player.getServerWorld() instanceof TardisServerWorld tardisWorld) {
            TardisUtil.teleportInside(tardisWorld.getTardis(), player);
            player.fallDistance = 0;
        }
    }

    @Inject(method = "attack", at = @At("HEAD"), cancellable = true)
    public void ait$attack(CallbackInfo ci) {
        ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;
        if (player.hasVehicle() && player.getVehicle() instanceof FlightTardisEntity)
            ci.cancel();
    }

    @Inject(method = "onDeath", at = @At("HEAD"))
    public void ait$onDeath(DamageSource damageSource, CallbackInfo ci) {
        ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;

        ServerTardisManager.getInstance().forEach(tardis -> {
            if (!tardis.siege().isActive())
                return;

            if (!Objects.equals(tardis.siege().getHeldPlayerUUID(), player.getUuid()))
                return;

            for (ItemStack itemStack : player.getInventory().main) {
                if (itemStack.isOf(AITItems.SIEGE_ITEM)) {
                    if (tardis.getUuid().equals(SiegeTardisItem.getTardisIdStatic(itemStack))) {
                        player.getInventory().setStack(player.getInventory().getSlotWithStack(itemStack), Items.AIR.getDefaultStack());
                    }
                }
            }
            SiegeTardisItem.placeTardis(tardis, SiegeTardisItem.fromEntity(player));
        });
    }
}
