package dev.amble.ait.mixin.server;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.entity.mob.PatrolEntity;

@Mixin(PatrolEntity.class)
public interface RaidAccessor {

    @Accessor
    boolean getPatrolling();
}
