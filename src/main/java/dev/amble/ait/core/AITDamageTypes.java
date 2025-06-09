package dev.amble.ait.core;

import dev.amble.ait.AITMod;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.world.World;

public class AITDamageTypes {
    public static final RegistryKey<DamageType> TARDIS_SQUASH_DAMAGE_TYPE = RegistryKey.of(RegistryKeys.DAMAGE_TYPE,
            AITMod.id("tardis_squash_damage_type"));

    public static final RegistryKey<DamageType> INTERIOR_CHANGE = RegistryKey.of(RegistryKeys.DAMAGE_TYPE,
            AITMod.id("interior_change_damage_type"));

    public static DamageSource of(World world, RegistryKey<DamageType> key) {
        return new DamageSource(world.getRegistryManager().get(RegistryKeys.DAMAGE_TYPE).entryOf(key));
    }
}
