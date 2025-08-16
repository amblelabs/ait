package dev.amble.ait.module.planet.core.space.planet;

import dev.amble.lib.register.datapack.SimpleDatapackRegistry;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;

import net.minecraft.resource.ResourceType;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionTypes;

import dev.amble.ait.AITMod;

public class PlanetRegistry extends SimpleDatapackRegistry<Planet> {

    private static final PlanetRegistry instance = new PlanetRegistry();

    public PlanetRegistry() {
        super(Planet::fromInputStream, Planet.CODEC, "planet", true, AITMod.MOD_ID);
    }

    public static Planet OVERWORLD;
    public static Planet THE_NETHER;
    public static Planet THE_END;

    @Override
    protected void defaults() {
              THE_NETHER = register(new Planet(DimensionTypes.THE_NETHER_ID, -1, true, true, 548, PlanetRenderInfo.EMPTY, PlanetTransition.EMPTY));
        THE_END = register(new Planet(DimensionTypes.THE_END_ID, -1, true, true, 548, PlanetRenderInfo.EMPTY, PlanetTransition.EMPTY));// -1f means dont change gravity btw
    }

    @Override
    public void onCommonInit() {
        super.onCommonInit();
        this.defaults();
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(this);
    }

    @Override
    public Planet fallback() {
        return OVERWORLD;
    }

    /**
     * @implNote O(N) - worst scenario (cache miss), O(1) otherwise.
     */
    public Planet get(World world) {
        // all worlds implement PlanetWorld
        if (!(world instanceof PlanetWorld planetWorld))
            return null;

        if (planetWorld.ait_planet$isAPlanet())
            return planetWorld.ait_planet$getPlanet();

        Planet planet = this.get(world.getRegistryKey().getValue());

        planetWorld.ait_planet$setPlanet(planet);
        planetWorld.ait_planet$setIsAPlanet(planet != null);
        return planet;
    }

    public static PlanetRegistry getInstance() {
        return instance;
    }
}
