package dev.amble.ait.mixin.server;

import dev.amble.ait.client.util.ClientTardisUtil;
import dev.amble.ait.core.AITSounds;
import dev.amble.ait.core.tardis.Tardis;
import dev.amble.ait.data.Loyalty;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BedBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BedBlock.class)
public class BedInTardisMixin {
    @Inject(at = @At("HEAD"), method = "onUse")
    private void ait$useOn(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand,
                           BlockHitResult hit, CallbackInfoReturnable<ActionResult> cir) {
        if (world.isClient()) { this.onClientSleep(player); }
    }

    @Unique @Environment(EnvType.CLIENT)
    private void onClientSleep(PlayerEntity player) {
        Tardis tardis = ClientTardisUtil.getCurrentTardis();
        if (tardis == null) return;

        Loyalty loyalty = tardis.loyalty().get(player);

        Text message = switch (loyalty.type()) {
            case REJECT -> Text.translatable("tardis.loyalty.message.reject");
            case NEUTRAL -> Text.translatable("tardis.loyalty.message.neutral");
            case COMPANION -> Text.translatable("tardis.loyalty.message.companion");
            case PILOT -> Text.translatable("tardis.loyalty.message.pilot");
            case OWNER -> Text.translatable("tardis.loyalty.message.owner");
        };
        player.sendMessage(message, false);

        SoundEvent sound = switch(loyalty.type()) {
            case OWNER -> AITSounds.OWNER_BED;
            case PILOT -> AITSounds.PILOT_BED;
            case COMPANION -> AITSounds.COMPANION_BED;
            case NEUTRAL -> AITSounds.NEUTRAL_BED;
            case REJECT -> AITSounds.GROAN;
        };

        // todo ClientScheduler with delay of 1sec was here, but was removed for addon compat
        // Mixin transformation of net.minecraft.block.BedBlock failed
        // Cannot load ClientScheduler
        player.playSound(sound, 1f, 1f);
    }
}