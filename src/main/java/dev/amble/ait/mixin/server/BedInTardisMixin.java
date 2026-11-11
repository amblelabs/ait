package dev.amble.ait.mixin.server;

import dev.amble.ait.AITMod;
import dev.amble.ait.client.util.ClientTardisUtil;
import dev.amble.ait.core.AITSounds;
import dev.amble.ait.core.tardis.Tardis;
import dev.amble.ait.core.tardis.handler.LoyaltyHandler;
import dev.amble.ait.core.world.TardisServerWorld;
import dev.amble.ait.data.Loyalty;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.block.BedBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@Mixin(BedBlock.class)
public class BedInTardisMixin {
    @Inject(at = @At("HEAD"), method = "onUse", cancellable = true)
    private void ait$useOn(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand,
                           BlockHitResult hit, CallbackInfoReturnable<ActionResult> cir) {
        if (world.isClient()) {
            this.onClientSleep(player);
            return;
        }

        if (!(world instanceof TardisServerWorld tardisWorld) || !(player instanceof ServerPlayerEntity serverPlayer)
                || AITMod.CONFIG == null || !AITMod.CONFIG.tardisTemperament)
            return;

        Tardis tardis = tardisWorld.getTardis();
        if (tardis == null || tardis.loyalty().get(player).type() != Loyalty.Type.REJECT)
            return;

        LoyaltyHandler.resetRejectedSpawn(serverPlayer, tardis);
        serverPlayer.sendMessage(Text.translatable("tardis.loyalty.message.reject"), false);
        world.playSound(null, pos, AITSounds.GROAN, SoundCategory.PLAYERS, 1f, 1f);
        cir.setReturnValue(ActionResult.CONSUME);
    }

    @Unique @Environment(EnvType.CLIENT)
    private void onClientSleep(PlayerEntity player) {
        Tardis tardis = ClientTardisUtil.getCurrentTardis();
        if (tardis == null)
            return;

        Loyalty loyalty = tardis.loyalty().get(player);
        if (loyalty.type() == Loyalty.Type.REJECT && AITMod.CONFIG != null && AITMod.CONFIG.tardisTemperament)
            return;

        Text message = switch (loyalty.type()) {
            case REJECT -> Text.translatable("tardis.loyalty.message.reject");
            case NEUTRAL -> Text.translatable("tardis.loyalty.message.neutral");
            case COMPANION -> Text.translatable("tardis.loyalty.message.companion");
            case PILOT -> Text.translatable("tardis.loyalty.message.pilot");
            case OWNER -> Text.translatable("tardis.loyalty.message.owner");
        };
        player.sendMessage(message, false);

        SoundEvent sound = switch (loyalty.type()) {
            case OWNER -> AITSounds.OWNER_BED;
            case PILOT -> AITSounds.PILOT_BED;
            case COMPANION -> AITSounds.COMPANION_BED;
            case NEUTRAL -> AITSounds.NEUTRAL_BED;
            case REJECT -> AITSounds.GROAN;
        };

        player.playSound(sound, 1f, 1f);
    }
}
