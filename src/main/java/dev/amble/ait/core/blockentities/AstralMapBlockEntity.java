package dev.amble.ait.core.blockentities;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import dev.amble.ait.AITMod;
import dev.amble.ait.core.AITBlockEntityTypes;

public class AstralMapBlockEntity extends BlockEntity {
    public AstralMapBlockEntity(BlockPos pos, BlockState state) {
        super(AITBlockEntityTypes.ASTRAL_MAP, pos, state);
    }

    public void useOn(World world, boolean sneaking, PlayerEntity player) {
        if (!(player instanceof ServerPlayerEntity serverPlayer))
            return;

        player.playSound(SoundEvents.UI_BUTTON_CLICK.value(), 1.0F, 1.0F);
        AITMod.openScreen(serverPlayer, 2);
    }
}
