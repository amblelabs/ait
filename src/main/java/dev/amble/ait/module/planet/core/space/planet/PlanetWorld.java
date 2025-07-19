package dev.amble.ait.module.planet.core.space.planet;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import net.minecraft.world.World;

/**
 * @apiNote Do NOT use this interface unless you know what you're doing.
 * This interface is made for **caching** the planet instance.
 * Use a proper method to check if the world is actually a planet or not.
 * @see PlanetRegistry#get(World)
 */
@ApiStatus.Internal
public interface PlanetWorld {
    boolean ait_planet$isAPlanet();

    @Nullable Planet ait_planet$getPlanet();

    void ait_planet$setPlanet(Planet planet);

    void ait_planet$setIsAPlanet(boolean isAPlanet);
}
