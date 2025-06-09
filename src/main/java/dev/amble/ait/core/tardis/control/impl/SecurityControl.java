package dev.amble.ait.core.tardis.control.impl;

import dev.amble.ait.AITMod;
import dev.amble.ait.core.AITItems;
import dev.amble.ait.core.AITSounds;
import dev.amble.ait.core.item.KeyItem;
import dev.amble.ait.core.tardis.Tardis;
import dev.amble.ait.core.tardis.control.Control;
import dev.amble.ait.core.tardis.util.TardisUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SecurityControl extends Control {

    public SecurityControl() {
        // ⨷ ?
        super(AITMod.id("protocol_19"));
    }

    @Override
    public Result runServer(Tardis tardis, ServerPlayerEntity player, ServerWorld world, BlockPos console, boolean leftClick) {
        super.runServer(tardis, player, world, console, leftClick);

        if (!hasMatchingKey(player, tardis))
            return Result.FAILURE;

        boolean security = tardis.stats().security().get();
        tardis.stats().security().set(!security);
        return security ? Result.SUCCESS : Result.SUCCESS_ALT;
    }

    public static void runSecurityProtocols(Tardis tardis) {
        boolean security = tardis.stats().security().get();
        boolean leaveBehind = tardis.travel().leaveBehind().get();

        if (!security)
            return;

        List<ServerPlayerEntity> forRemoval = new ArrayList<>();

        if (leaveBehind) {
            for (ServerPlayerEntity player : TardisUtil.getPlayersInsideInterior(tardis.asServer())) {
                if (!hasMatchingKey(player, tardis)) {
                    forRemoval.add(player);
                }
            }

            for (ServerPlayerEntity player : forRemoval) {
                TardisUtil.teleportOutside(tardis, player);
            }
        }
    }

    public static boolean hasMatchingKey(ServerPlayerEntity player, Tardis tardis) {
        if (player.hasPermissionLevel(2))
            return true;

        if (!tardis.loyalty().get(player).isOf(tardis.permissions().p19Loyalty().get()))
            return false;

        if (!KeyItem.isKeyInInventory(player))
            return false;

        Collection<ItemStack> keys = KeyItem.getKeysInInventory(player);

        for (ItemStack stack : keys) {
            Tardis found = KeyItem.getTardisStatic(player.getWorld(), stack);

            if (stack.getItem() == AITItems.SKELETON_KEY)
                return true;

            if (found == tardis)
                return true;
        }

        return false;
    }

    @Override
    public SoundEvent getFallbackSound() {
        return AITSounds.PROTOCOL_19;
    }

    @Override
    public long getDelayLength() {
        return 50;
    }
}
