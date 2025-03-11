package dev.amble.ait.registry.v2;

import com.mojang.serialization.Codec;
import dev.amble.ait.AITMod;
import dev.amble.lib.registry.SimpleAmbleRegistry;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.registry.Registry;

public class AITRegistries {

    public static ExteriorCategoryRegistry EXTERIOR_CATEGORY = new ExteriorCategoryRegistry();
    public static ExteriorAnimationRegistry EXTERIOR_ANIMATION = new ExteriorAnimationRegistry();
    public static ExteriorVariantRegistry EXTERIOR_VARIANT = new ExteriorVariantRegistry();

    public static void init() {
        EXTERIOR_CATEGORY.init();
        EXTERIOR_VARIANT.init();
    }
}
