package dev.amble.ait.client.sonic;

import java.util.Map;

import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceFinder;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

public class SonicResourceFinder extends ResourceFinder {

    private final ResourceFinder parent;

    public SonicResourceFinder(ResourceFinder parent, String directoryName, String fileExtension) {
        super(directoryName, fileExtension);
        this.parent = parent;
    }

    @Override
    public Identifier toResourcePath(Identifier id) {
        return parent.toResourcePath(id);
    }

    @Override
    public Identifier toResourceId(Identifier path) {
        return parent.toResourceId(path);
    }

    @Override
    public Map<Identifier, Resource> findResources(ResourceManager resourceManager) {
        Map<Identifier, Resource> map = parent.findResources(resourceManager);
        SonicModelLoader.fromMap(this, super.findResources(resourceManager));
        return map;
    }
}
