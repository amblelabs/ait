package dev.drtheo.mcecs;

import dev.drtheo.mcecs.base.system.MSystem;
import dev.drtheo.mcecs.base.system.SystemRegistry;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.CustomValue;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class MCECSUtil {

    public static void collectAndRegister(MSystem.Type type) {
        if (type == MSystem.Type.CLIENT && FabricLoader.getInstance().getEnvironmentType() != EnvType.CLIENT)
            return;

        List<String> systems = new ArrayList<>();
        String category = type.toString().toLowerCase();

        long start = System.currentTimeMillis();
        FabricLoader.getInstance().getAllMods().forEach(modContainer
                -> MCECSUtil.collectSystems(modContainer, category, systems));

        MCECS.LOGGER.info("Collected all {} systems in {}ms", category, System.currentTimeMillis() - start);
        start = System.currentTimeMillis();

        for (String system : systems) {
            MCECSUtil.handleRegister(MSystem.Type.CLIENT, system);
        }

        MCECS.LOGGER.info("Registered & initialized all collected {} systems in {}ms", category, System.currentTimeMillis() - start);
    }

    public static void handleRegister(MSystem.Type type, String className) {
        try {
            Class<?> clazz = Class.forName(className);

            if (!MSystem.class.isAssignableFrom(clazz))
                throw new IllegalArgumentException();

            SystemRegistry.register(type, (MSystem) clazz.getDeclaredConstructor().newInstance());
        } catch (ClassNotFoundException e) {
            MCECS.LOGGER.error("Failed to find system {}", className);
        } catch (IllegalArgumentException e) {
            MCECS.LOGGER.error("System doesn't implement a system class {}", className);
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException |
                 NoSuchMethodException e) {
            MCECS.LOGGER.error("Couldn't run the default constructor for system {}", className);
        } catch (Exception e) {
            MCECS.LOGGER.error("Failed to initialize system {}", className);
        }
    }

    public static void collectSystems(ModContainer mod, String type, List<String> list) {
        CustomValue ecsValue = mod.getMetadata().getCustomValue("ecs");

        if (ecsValue == null)
            return;

        CustomValue.CvObject ecs = ecsValue.getAsObject();
        CustomValue systemsValue = ecs.get("systems");

        if (systemsValue == null)
            return;

        CustomValue.CvObject systems = systemsValue.getAsObject();
        CustomValue value = systems.get(type);

        if (value != null)
            collectSystems(value.getAsArray(), list);
    }

    public static void collectSystems(CustomValue.CvArray array, List<String> ids) {
        array.forEach(value -> {
            try {
                ids.add(value.getAsString());
            } catch (ClassCastException e) {
                MCECS.LOGGER.error("Bad system id: {}", value);
            }
        });
    }
}
