package dev.amble.ait.core.entities;

import org.jetbrains.annotations.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

import dev.amble.ait.core.AITItems;


public class GallifreyFallsPaintingEntity extends BOTIPaintingEntity {

    public GallifreyFallsPaintingEntity(EntityType<? extends BOTIPaintingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public ItemStack getPickBlockStack() {
        return new ItemStack(AITItems.GALLIFREY_FALLS_PAINTING);
    }

    @Override
    public void onBreak(@Nullable Entity entity) {
        if (!this.getWorld().getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS)) {
            return;
        }
        this.playSound(SoundEvents.ENTITY_PAINTING_BREAK, 1.0f, 1.0f);
        if (entity instanceof PlayerEntity player && player.isCreative()) {
            return;
        }
        this.dropItem(AITItems.GALLIFREY_FALLS_PAINTING);
    }
}
