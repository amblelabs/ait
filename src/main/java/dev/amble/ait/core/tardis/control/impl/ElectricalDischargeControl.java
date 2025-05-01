package dev.amble.ait.core.tardis.control.impl;

import dev.drtheo.scheduler.api.Scheduler;
import dev.drtheo.scheduler.api.TimeUnit;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

import dev.amble.ait.AITMod;
import dev.amble.ait.core.AITSounds;
import dev.amble.ait.core.engine.SubSystem;
import dev.amble.ait.core.tardis.Tardis;
import dev.amble.ait.core.tardis.control.Control;

public class ElectricalDischargeControl extends Control {

    private static final int ARTRON_COST = 1250;
    private static final int EFFECT_RADIUS = 2;
    private static final int INITIAL_DELAY = 40;
    private static final int TOTAL_DURATION = 80;

    public ElectricalDischargeControl() {
        super(AITMod.id("electrical_discharge"));
    }

    @Override
    public Result runServer(Tardis tardis, ServerPlayerEntity player, ServerWorld world, BlockPos console, boolean leftClick) {
        super.runServer(tardis, player, world, console, leftClick);

        if (tardis.fuel().getCurrentFuel() < ARTRON_COST) {
            player.sendMessage(Text.translatable("tardis.message.control.electric.fail", ARTRON_COST).formatted(Formatting.RED), true);
            return Result.FAILURE;
        }

        tardis.fuel().removeFuel(ARTRON_COST);

        BlockPos exteriorPos = tardis.travel().position().getPos();
        ServerWorld exteriorWorld = tardis.travel().position().getWorld();

        Scheduler.get().runTaskLater(() -> {
            spreadElectricalEffects(exteriorWorld, exteriorPos);
        }, TimeUnit.TICKS, INITIAL_DELAY);

        Scheduler.get().runTaskLater(() -> {
            world.playSound(null, console, AITSounds.DING, SoundCategory.BLOCKS, 1.0F, 1.0F);
        }, TimeUnit.TICKS, TOTAL_DURATION);

        return Result.SUCCESS;
    }


    private void spreadElectricalEffects(ServerWorld world, BlockPos pos) {
        Box effectBox = new Box(pos).expand(EFFECT_RADIUS);

        world.getEntitiesByClass(LivingEntity.class, effectBox, entity -> true).forEach(entity -> {
            entity.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 600, 1));
            entity.addStatusEffect(new StatusEffectInstance(StatusEffects.POISON, 300, 1));
            entity.addStatusEffect(new StatusEffectInstance(StatusEffects.HUNGER, 200, 1));
            entity.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 600, 1));
            entity.addStatusEffect(new StatusEffectInstance(StatusEffects.MINING_FATIGUE, 275, 1));
            entity.addStatusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, 250, 1));
            entity.addStatusEffect(new StatusEffectInstance(StatusEffects.DARKNESS, 75, 1));
        });

        for (BlockPos targetPos : BlockPos.iterate(pos.add(-EFFECT_RADIUS, -1, -EFFECT_RADIUS), pos.add(EFFECT_RADIUS, 2, EFFECT_RADIUS))) {
            world.spawnParticles(ParticleTypes.ELECTRIC_SPARK, targetPos.getX() + 0.5, targetPos.getY() + 1.0, targetPos.getZ() + 0.5, 5, 0, 0.05, 0, 0.1);
        }
    }

    @Override
    protected SubSystem.IdLike requiredSubSystem() {
        return SubSystem.Id.DESPERATION;
    }

    @Override
    public long getDelayLength() {
        return 800;
    }

    @Override
    public SoundEvent getFallbackSound() {
        return AITSounds.BWEEP;
    }
}
