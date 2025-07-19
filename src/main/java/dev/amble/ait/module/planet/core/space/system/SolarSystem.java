package dev.amble.ait.module.planet.core.space.system;

import java.util.Arrays;
import java.util.HashSet;

import net.minecraft.text.Text;

import dev.amble.ait.api.Nameable;
import dev.amble.ait.module.planet.core.space.planet.Planet;
import dev.amble.ait.module.planet.core.space.planet.PlanetRegistry;

public class SolarSystem extends HashSet<Planet> implements Nameable {
    protected final Text name;

    public SolarSystem(Text name) {
        this.name = name;
    }
    public SolarSystem(Text name, Planet... planets) {
        this(name);
        this.addAll(Arrays.asList(planets));
    }

    @Override
    public String name() {
        return text().getString();
    }

    @Override
    public Text text() {
        return name;
    }

    /**
     * @return newly created set of all loaded planets
     */
    public static SolarSystem all() {
        SolarSystem system = new SolarSystem(Text.of("AMBLE"));

        system.addAll(PlanetRegistry.getInstance().toList());

        return system;
    }
}
