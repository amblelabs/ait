package dev.amble.ait.core.util;

import dev.amble.ait.core.tardis.Tardis;
import dev.amble.ait.core.world.TardisServerWorld;
import dev.amble.ait.data.Loyalty;
import dev.amble.ait.module.planet.core.space.planet.Planet;
import dev.amble.ait.module.planet.core.space.planet.PlanetRegistry;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

public final class FallDamageUtil {

    private FallDamageUtil() {
    }

    /** Returns whether an AIT mechanic would cancel this entity's fall damage. */
    public static boolean isPreventedByAIT(LivingEntity entity) {
        if (entity == null)
            return false;

        World world = entity.getWorld();
        Planet planet = PlanetRegistry.getInstance().get(world);
        if (planet != null)
            return planet.hasNoFallDamage();

        if (!(world instanceof TardisServerWorld tardisWorld) || !(entity instanceof PlayerEntity player))
            return false;

        Tardis tardis = tardisWorld.getTardis();
        return tardis != null && tardis.subsystems().lifeSupport().isUsable()
                && tardis.loyalty().get(player).isOf(Loyalty.Type.OWNER);
    }
}
