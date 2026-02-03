package dev.amble.ait.data.preset;

import java.util.Optional;

import dev.amble.lib.register.datapack.SimpleDatapackRegistry;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;

import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;

import dev.amble.ait.AITMod;

/**
 * Registry for TARDIS presets that can be loaded via datapacks.
 */
public class TardisPresetRegistry extends SimpleDatapackRegistry<TardisPreset> {

    private static final TardisPresetRegistry instance = new TardisPresetRegistry();

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
        this.defaults();
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(this);
    }

    @Override
    protected void defaults() {
        // Hartnell/Classic preset - the default fallback preset
        HARTNELL = register(new TardisPreset(
                AITMod.id("hartnell"),
                "Hartnell",
                Optional.of(AITMod.id("exterior/police_box/default")),
                Optional.of(AITMod.id("console/hartnell")),
                Optional.of(AITMod.id("prime")),
                Optional.of(AITMod.id("coral")),
                Optional.of(AITMod.id("pulsating_demat")),
                Optional.of(AITMod.id("default")),
                Optional.of(AITMod.id("pulsating_mat")),
                Optional.of(AITMod.id("toyota"))
        ));

        // Crystalline preset
        CRYSTALLINE = register(new TardisPreset(
                AITMod.id("crystalline"),
                "Crystalline",
                Optional.of(AITMod.id("exterior/police_box/renaissance")),
                Optional.of(AITMod.id("console/crystalline")),
                Optional.of(AITMod.id("crystalline")),
                Optional.of(AITMod.id("coral")),
                Optional.empty(), // Default Takeoff
                Optional.empty(), // Default Flight
                Optional.empty(), // Default Landing
                Optional.of(AITMod.id("crystal"))
        ));

        // Toyota preset
        TOYOTA = register(new TardisPreset(
                AITMod.id("toyota"),
                "Toyota",
                Optional.of(AITMod.id("exterior/police_box/default")),
                Optional.of(AITMod.id("console/toyota")),
                Optional.of(AITMod.id("toyota")),
                Optional.of(AITMod.id("toyota")),
                Optional.empty(), // Default Takeoff
                Optional.empty(), // Default Flight
                Optional.empty(), // Default Landing
                Optional.of(AITMod.id("capaldi"))
        ));

        // Copper preset
        COPPER = register(new TardisPreset(
                AITMod.id("copper"),
                "Copper",
                Optional.of(AITMod.id("exterior/police_box/default")),
                Optional.of(AITMod.id("console/copper")),
                Optional.of(AITMod.id("copper")),
                Optional.of(AITMod.id("copper")),
                Optional.empty(), // Default Takeoff
                Optional.empty(), // Default Flight
                Optional.empty(), // Default Landing
                Optional.of(AITMod.id("copper"))
        ));

        // Coral preset
        CORAL = register(new TardisPreset(
                AITMod.id("coral"),
                "Coral",
                Optional.of(AITMod.id("exterior/police_box/coral")),
                Optional.of(AITMod.id("console/coral")),
                Optional.of(AITMod.id("coral")),
                Optional.of(AITMod.id("coral")),
                Optional.empty(), // Default Takeoff
                Optional.empty(), // Default Flight
                Optional.empty(), // Default Landing
                Optional.of(AITMod.id("tennantblue"))
        ));
    }

    @Override
    public TardisPreset fallback() {
        return HARTNELL;
    }
}
