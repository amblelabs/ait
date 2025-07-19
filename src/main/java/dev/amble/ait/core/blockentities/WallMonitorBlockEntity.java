package dev.amble.ait.core.blockentities;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import dev.amble.ait.AITMod;
import dev.amble.ait.api.tardis.link.v2.block.InteriorLinkableBlockEntity;
import dev.amble.ait.core.AITBlockEntityTypes;
import dev.amble.ait.core.tardis.Tardis;
import dev.amble.ait.core.tardis.control.impl.SecurityControl;

public class WallMonitorBlockEntity extends InteriorLinkableBlockEntity {

    public WallMonitorBlockEntity(BlockPos pos, BlockState state) {
        super(AITBlockEntityTypes.WALL_MONITOR_BLOCK_ENTITY_TYPE, pos, state);
    }

    public void useOn(World world, boolean sneaking, PlayerEntity player) {
        if (!(player instanceof ServerPlayerEntity serverPlayer))
            return;

        if (this.tardis().isEmpty())
            return;

        Tardis tardis = this.tardis().get();

        if (!tardis.fuel().hasPower())
            return;

        boolean security = tardis.stats().security().get();

        if (security && !SecurityControl.hasMatchingKey(serverPlayer, tardis))
            return;

        player.playSound(SoundEvents.UI_BUTTON_CLICK.value(), 1.0F, 1.0F);
        AITMod.openScreen((ServerPlayerEntity) player, 0, tardis.getUuid()); // we can cast because we know its on
                                                                                // server :p
    }
}
