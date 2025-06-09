package dev.amble.ait.module.planet.core.blockentities;

import dev.amble.ait.core.AITStatusEffects;
import dev.amble.ait.module.planet.core.PlanetBlockEntities;
import dev.amble.ait.module.planet.core.space.planet.Planet;
import dev.amble.ait.module.planet.core.space.planet.PlanetRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

import java.util.function.Predicate;

public class OxygenatorBlockEntity extends BlockEntity {
    public OxygenatorBlockEntity(BlockPos pos, BlockState state) {
        super(PlanetBlockEntities.OXYGENATOR_BLOCK_ENTITY_TYPE, pos, state);
    }

    public void tick(World world, BlockPos blockPos, BlockState blockState, OxygenatorBlockEntity oxygenatorBlockEntity) {
        if (world.isClient()) return;
        Planet planet = PlanetRegistry.getInstance().get(world);
        if (planet == null) return;
        if (planet.hasOxygen()) return;
        Predicate<Entity> predicate = EntityPredicates.EXCEPT_CREATIVE_OR_SPECTATOR
                .and(EntityPredicates.VALID_LIVING_ENTITY);
        world.getOtherEntities(null, new Box(blockPos).expand(20), predicate).forEach(entity -> {
            if  (entity instanceof LivingEntity livingEntity) {
                livingEntity.addStatusEffect(new StatusEffectInstance(AITStatusEffects.OXYGENATED, 20, 1, true, false));
            }
        });
    }
}
