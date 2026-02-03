package dev.amble.ait.data.preset;

import dev.amble.lib.register.datapack.SimpleDatapackRegistry;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;

import net.minecraft.resource.ResourceType;

import dev.amble.ait.AITMod;

/**
 * Registry for TARDIS presets that can be loaded via datapacks.
 * Presets are defined in data/[namespace]/preset/*.json files.
 */
public class TardisPresetRegistry extends SimpleDatapackRegistry<TardisPreset> {

    private static final TardisPresetRegistry instance = new TardisPresetRegistry();

    // Static references to default presets (populated after datapack loading)
    public static TardisPreset HARTNELL;
    public static TardisPreset CRYSTALLINE;
    public static TardisPreset TOYOTA;
    public static TardisPreset COPPER;
    public static TardisPreset CORAL;

    protected TardisPresetRegistry() {
        super(TardisPreset::fromInputStream, TardisPreset.CODEC, "preset", true, AITMod.MOD_ID);
    }

    public static TardisPresetRegistry getInstance() {
        return instance;
    }

    @Override
    public void onCommonInit() {
        super.onCommonInit();
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(this);
    }

    /**
     * Called after datapacks are loaded to populate static references.
     * This allows code to reference presets without needing to call getInstance().get() every time.
     */
    public void populateStaticReferences() {
        HARTNELL = this.get(AITMod.id("hartnell"));
        CRYSTALLINE = this.get(AITMod.id("crystalline"));
        TOYOTA = this.get(AITMod.id("toyota"));
        COPPER = this.get(AITMod.id("copper"));
        CORAL = this.get(AITMod.id("coral"));
    }

    @Override
    protected void defaults() {
        // Presets are loaded from datapacks (data/ait/preset/*.json)
        // No programmatic registration needed - this avoids double registration
    }

    @Override
    public TardisPreset fallback() {
        // Return Hartnell preset as fallback, or create a minimal fallback if not yet loaded
        if (HARTNELL != null) {
            return HARTNELL;
        }
        // Fallback in case presets haven't loaded yet
        return this.get(AITMod.id("hartnell"));
    }
}
