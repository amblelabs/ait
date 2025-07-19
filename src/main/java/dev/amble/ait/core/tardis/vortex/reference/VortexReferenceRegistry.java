package dev.amble.ait.core.tardis.vortex.reference;

import dev.amble.lib.register.datapack.SimpleDatapackRegistry;

import dev.amble.ait.AITMod;

public class VortexReferenceRegistry extends SimpleDatapackRegistry<VortexReference> {
    private static final VortexReferenceRegistry instance = new VortexReferenceRegistry();

    public VortexReferenceRegistry() {
        super(VortexReference::fromInputStream, VortexReference.CODEC, "fx/vortex", true, AITMod.MOD_ID);
    }

    public static VortexReference TOYOTA;

    @Override
    protected void defaults() {
        TOYOTA = register(new VortexReference(AITMod.id("toyota"), AITMod.id("textures/vortex/toyota.png")));
    }

    @Override
    public VortexReference fallback() {
        return TOYOTA;
    }

    public static VortexReferenceRegistry getInstance() {
        return instance;
    }
}
