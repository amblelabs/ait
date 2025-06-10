package dev.amble.ait.mixin.server;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.block.BellBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import dev.amble.ait.core.AITSounds;
import dev.amble.ait.core.item.SonicItem;
import dev.amble.ait.core.item.sonic.SonicMode;

@Mixin(value = BellBlock.class)
public class BellBlockMixin {

    @Inject(method = "ring(Lnet/minecraft/entity/Entity;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/Direction;)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;playSound(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/sound/SoundEvent;Lnet/minecraft/sound/SoundCategory;FF)V"), cancellable = true)
    public void ait$playSound(Entity entity, World world, BlockPos pos, Direction direction, CallbackInfoReturnable<Boolean> cir) {

        // sonic summon used on bell (locator feature) -> play cloister sound
        if (!world.isClient() && entity instanceof PlayerEntity player) {
            ItemStack itemInHand = player.getMainHandStack();

            if (itemInHand.getItem() instanceof SonicItem && SonicItem.mode(itemInHand) == SonicMode.Modes.SCANNING) {
                world.playSound(null, pos, AITSounds.CLOISTER, SoundCategory.BLOCKS, 2.0F, 1.0F);
                itemInHand.use(world, player, player.getActiveHand());
                cir.cancel();
            }
        }
    }
}
