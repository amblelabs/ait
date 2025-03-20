package dev.drtheo.mcecs;

import dev.drtheo.mcecs.impl.MSystem;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MCECS implements ModInitializer {

    public static final Logger LOGGER = LoggerFactory.getLogger("mc-ecs");

    private static boolean initialized = false;

    @Override
    public void onInitialize() {
        if (initialized)
            return;

        MCECSUtil.collectAndRegister(MSystem.Type.SERVER);
        initialized = true;
    }

    public static Identifier id(String path) {
        return new Identifier(path);
    }
}
