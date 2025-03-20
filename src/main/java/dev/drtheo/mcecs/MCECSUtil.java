package dev.drtheo.mcecs;

import dev.drtheo.mcecs.base.comp.Component;
import dev.drtheo.mcecs.impl.MComponentRegistry;
import dev.drtheo.mcecs.impl.MSystem;
import dev.drtheo.mcecs.impl.MSystemRegistry;
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

        List<String> components = new ArrayList<>();
        List<String> systems = new ArrayList<>();

        String category = type.toString().toLowerCase();

        long start = System.currentTimeMillis();
        FabricLoader.getInstance().getAllMods().forEach(modContainer
                -> MCECSUtil.collectAll(modContainer, components, category, systems));

        MCECS.LOGGER.info("Collected all {} systems in {}ms", category, System.currentTimeMillis() - start);
        start = System.currentTimeMillis();

        for (String system : systems) {
            MCECSUtil.handleRegisterSystem(type, system);
        }

        for (String component : components) {
            MCECSUtil.handleRegisterComponent(component);
        }

        MCECS.LOGGER.info("Registered & initialized all collected {} systems & components in {}ms", category, System.currentTimeMillis() - start);
    }

    private static void handleRegisterComponent(String className) {
        try {
            Class<?> clazz = Class.forName(className);
            MComponentRegistry.INSTANCE.register((Class<? extends Component<?>>) clazz);
        } catch (ClassNotFoundException e) {
            MCECS.LOGGER.error("Failed to find component {}", className);
        } catch (ClassCastException e) {
            MCECS.LOGGER.error("Component doesn't implement a component class {}", className);
        } catch (Exception e) {
            MCECS.LOGGER.error("Failed to initialize component {}", className);
            e.printStackTrace();
        }
    }

    private static void handleRegisterSystem(MSystem.Type type, String className) {
        try {
            Class<?> clazz = Class.forName(className);

            if (!MSystem.class.isAssignableFrom(clazz))
                throw new IllegalArgumentException();

            MSystemRegistry.register(type, (MSystem) clazz.getDeclaredConstructor().newInstance());
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

    private static void collectAll(ModContainer mod, List<String> components, String type, List<String> systems) {
        CustomValue ecsValue = mod.getMetadata().getCustomValue("ecs");

        if (ecsValue == null)
            return;

        CustomValue.CvObject ecs = ecsValue.getAsObject();

        collectSystems(ecs, type, systems);
        collectComponents(ecs, components);
    }

    private static void collectComponents(CustomValue.CvObject ecs, List<String> components) {
        CustomValue compsValue = ecs.get("components");

        if (compsValue == null)
            return;

        compsValue.getAsArray().forEach(value -> {
            try {
                components.add(value.getAsString());
            } catch (ClassCastException e) {
                MCECS.LOGGER.error("Bad component id: {}", value);
            }
        });
    }

    private static void collectSystems(CustomValue.CvObject ecs, String type, List<String> list) {
        CustomValue systemsValue = ecs.get("systems");

        if (systemsValue == null)
            return;

        CustomValue.CvObject systems = systemsValue.getAsObject();
        CustomValue value = systems.get(type);

        if (value == null)
            return;

        value.getAsArray().forEach(id -> {
            try {
                list.add(id.getAsString());
            } catch (ClassCastException e) {
                MCECS.LOGGER.error("Bad system id: {}", value);
            }
        });
    }
}
