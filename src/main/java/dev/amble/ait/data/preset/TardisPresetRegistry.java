package dev.amble.ait.data.preset;

import dev.amble.lib.register.datapack.SimpleDatapackRegistry;

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
        // Return Hartnell preset as fallback
        if (HARTNELL != null) {
            return HARTNELL;
        }
        // Try to get from registry
        TardisPreset hartnell = this.get(AITMod.id("hartnell"));
        if (hartnell != null) {
            return hartnell;
        }
        // Return first available preset, or null if registry is empty
        var list = this.toList();
        return list.isEmpty() ? null : list.get(0);
    }
}
