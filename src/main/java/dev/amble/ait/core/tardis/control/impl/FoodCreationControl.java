package dev.amble.ait.core.tardis.control.impl;

import dev.amble.ait.AITMod;
import dev.amble.ait.core.AITSounds;
import dev.amble.ait.core.tardis.Tardis;
import dev.amble.ait.core.tardis.control.Control;
import dev.drtheo.scheduler.api.TimeUnit;
import dev.drtheo.scheduler.api.common.Scheduler;
import dev.drtheo.scheduler.api.common.TaskStage;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class FoodCreationControl extends Control {

    public FoodCreationControl() {
        super(AITMod.id("food_creation"));
    }

    @Override
    public Result runServer(Tardis tardis, ServerPlayerEntity player, ServerWorld world, BlockPos console,
                             boolean leftClick) {
        super.runServer(tardis, player, world, console, leftClick);
        if (tardis.fuel().getCurrentFuel() < 500)
            return Result.FAILURE;

        Scheduler.get().runTaskLater(() -> {
            if (world.getBlockState(console).isAir())
                return;

            ItemStack coffeeItem = tardis.extra().getRefreshmentItem();

            Vec3d spawnPosition = Vec3d.ofCenter(console).add(0, 1.5, 1);
            ItemEntity coffeeEntity = new ItemEntity(world, spawnPosition.x, spawnPosition.y, spawnPosition.z, coffeeItem);

            tardis.removeFuel(500);
            world.spawnEntity(coffeeEntity);
        }, TaskStage.END_SERVER_TICK, TimeUnit.TICKS, 45);

        return Result.SUCCESS;
    }

    @Override
    public SoundEvent getFallbackSound() {
        return AITSounds.COFFEE_MACHINE;
    }
}
