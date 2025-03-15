package dev.amble.ait.core.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class PsychpaperItem extends Item {

    public PsychpaperItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        return TypedActionResult.pass(user.getStackInHand(hand));
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (world.isClient() || !(entity instanceof PlayerEntity player) || !selected) return;

        player.addStatusEffect(new StatusEffectInstance(
                StatusEffects.HERO_OF_THE_VILLAGE, 200, 0, true, false, false));
    }
}
