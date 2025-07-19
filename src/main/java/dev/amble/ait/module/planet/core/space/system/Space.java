package dev.amble.ait.module.planet.core.space.system;

import java.util.HashSet;

import dev.amble.ait.module.planet.core.space.planet.Planet;

public class Space { // todo - there is only one "Space" dimension, so i guess a registry of solar systems is not necessary yet :(
    public final HashSet<SolarSystem> systems;

    protected Space() {
        this.systems = new HashSet<>();

        // temporary
        this.systems.add(SolarSystem.all());
    }

    public HashSet<Planet> getPlanets() {
        HashSet<Planet> planets = new HashSet<>();

        for (SolarSystem system : systems) {
            planets.addAll(system);
        }

        return planets;
    }

    private static Space INSTANCE;
    public static Space getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Space();
        }

        return INSTANCE;
    }
}
