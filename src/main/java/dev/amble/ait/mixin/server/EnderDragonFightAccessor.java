package dev.amble.ait.mixin.server;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.entity.boss.dragon.EnderDragonFight;

@Mixin(EnderDragonFight.class)
public interface EnderDragonFightAccessor {
    @Accessor
    boolean getDragonKilled();
}
