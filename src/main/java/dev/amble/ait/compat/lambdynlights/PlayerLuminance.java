package dev.amble.ait.compat.lambdynlights;

import dev.amble.ait.core.item.SonicItem;
import dev.lambdaurora.lambdynlights.api.entity.luminance.EntityLuminance;
import dev.lambdaurora.lambdynlights.api.item.ItemLightSourceManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

public class PlayerLuminance implements EntityLuminance {

    @Override
    public @NotNull Type type() {
        return Type.VALUE;
    }

    @Override
    public @Range(from = 0L, to = 15L) int getLuminance(@NotNull ItemLightSourceManager itemLightSourceManager, @NotNull Entity entity) {
        if (entity instanceof PlayerEntity player) {
            ItemStack stack = player.getActiveItem();
            if (stack == null || stack.isEmpty()) return 0;
            if (stack.getItem() instanceof SonicItem sonic) {
                if (sonic.getCurrentFuel(stack) > 0) {
                    double current = sonic.getCurrentFuel(stack);
                    double max = sonic.getMaxFuel(stack);
                    if (max <= 0 || current <= 0) return 0;
                    int lum = (int) Math.round((current / max) * 15.0);
                    return Math.min(15, Math.max(0, lum));
                }
            }
        }
        return 0;
    }
}
