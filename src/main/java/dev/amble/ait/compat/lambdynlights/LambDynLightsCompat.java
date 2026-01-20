package dev.amble.ait.compat.lambdynlights;

import dev.lambdaurora.lambdynlights.api.DynamicLightsContext;
import dev.lambdaurora.lambdynlights.api.DynamicLightsInitializer;
import net.minecraft.entity.EntityType;

public class LambDynLightsCompat implements DynamicLightsInitializer {

    @Override
    public void onInitializeDynamicLights(DynamicLightsContext context) {
        context.entityLightSourceManager().onRegisterEvent().register(ctx -> {
            ctx.register(EntityType.PLAYER, new PlayerLuminance());
        });
    }

    @Override
    @SuppressWarnings("removal")
    public void onInitializeDynamicLights() { }
}
