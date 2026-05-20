package dev.amble.ait.mixin;

import dev.amble.ait.core.advancement.ChatUtils;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerAdvancementTracker.class)
public abstract class PlayerAdvancementTrackerMixin {

    @Shadow private ServerPlayerEntity owner;
    @Shadow public abstract AdvancementProgress getProgress(Advancement advancement);

    @Inject(method = "grantCriterion", at = @At("HEAD"))
    private void onAdvancementGrantedCheck(Advancement advancement, String criterionName, CallbackInfoReturnable<Boolean> cir) {
        if (this.owner == null || advancement == null) return;

        String targetAdvancementId = "minecraft:ait/root";

        if (advancement.getId().toString().equals(targetAdvancementId)) {
            // 1. Get the player's progress for this specific advancement before changes are applied
            AdvancementProgress progress = this.getProgress(advancement);

            // 2. If the advancement is ALREADY done, this is just a world-load sync or duplicate event. Exit immediately.
            if (progress.isDone()) {
                return;
            }

            // 3. Optional safeguard: Check if this specific criterion was already obtained
            if (progress.getCriterionProgress(criterionName) != null && progress.getCriterionProgress(criterionName).isObtained()) {
                return;
            }

            // 4. If we pass both checks, this is the genuine first-time completion event!
            ChatUtils.sendLinkMessage(this.owner);
        }
    }
}