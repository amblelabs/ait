package dev.amble.ait.client.sonic;

import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceFinder;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


// TODO: when finished, move to SonicRendering
public class SonicModelLoader {

    public static List<Identifier> toLoad;

    public static void init() {
        ModelLoadingPlugin.register(context -> {
            context.addModels(toLoad);
        });
    }

    public static void fromMap(ResourceFinder finder, Map<Identifier, Resource> map) {
        List<Identifier> result = new ArrayList<>();

        map.forEach((identifier, resource) -> {
            result.add(finder.toResourceId(identifier));
        });

        toLoad = result;
    }
}
