package dev.amble.ait.core.entities.daleks;

import dev.amble.lib.register.datapack.SimpleDatapackRegistry;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;

import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;

import dev.amble.ait.AITMod;

public class DalekRegistry extends SimpleDatapackRegistry<Dalek> {
    private static final DalekRegistry instance = new DalekRegistry();
    public static final Identifier TEXTURE = AITMod.id("textures/entity/daleks/imperial/imperial_dalek.png");
    public static final Identifier EMISSION = AITMod.id( "textures/entity/daleks/imperial/imperial_dalek_emission.png");
    public DalekRegistry() {
        super(Dalek::fromInputStream, Dalek.CODEC, "entities/dalek/variants", "entities/dalek/variants", true, AITMod.MOD_ID);
    }

    public static Dalek IMPERIAL;

    @Override
    protected void defaults() {
        IMPERIAL = register(new Dalek(AITMod.id("dalek/imperial"), TEXTURE,
                EMISSION));
    }

    @Override
    public void onCommonInit() {
        super.onCommonInit();
        this.defaults();
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(this);
    }

    @Override
    public Dalek fallback() {
        return IMPERIAL;
    }

    public static DalekRegistry getInstance() {
        return instance;
    }
}
