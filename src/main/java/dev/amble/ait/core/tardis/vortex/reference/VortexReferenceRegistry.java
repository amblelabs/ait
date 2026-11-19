package dev.amble.ait.core.tardis.vortex.reference;

import dev.amble.ait.AITMod;
import dev.amble.lib.register.unlockable.UnlockableRegistry;

public class VortexReferenceRegistry extends UnlockableRegistry<VortexReference> {
    private static final VortexReferenceRegistry instance = new VortexReferenceRegistry();

    public VortexReferenceRegistry() {
        super(VortexReference::fromInputStream, VortexReference.CODEC, "fx/vortex", true, AITMod.MOD_ID);
    }

    public static VortexReference TOYOTA;
    public static VortexReference TENNANT_BLUE;

    @Override
    protected void defaults() {
        TOYOTA = register(new VortexReference(AITMod.id("toyota"), AITMod.id("textures/vortex/toyota.png")));
        TENNANT_BLUE = register(new VortexReference(AITMod.id("tennantblue"), AITMod.id("textures/vortex/tennant.png")));
    }

    @Override
    public VortexReference fallback() {
        return TENNANT_BLUE;
    }

    public static VortexReferenceRegistry getInstance() {
        return instance;
    }
}
