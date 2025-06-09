package dev.amble.ait.compat;

import dev.amble.ait.api.AITModInitializer;
import dev.amble.ait.compat.gravity.GravityHandler;
import net.fabricmc.api.ClientModInitializer;

public class Compat implements AITModInitializer, ClientModInitializer {

    @Override
    public void onInitializeAIT() {
        if (DependencyChecker.hasGravity())
            GravityHandler.init();
    }

    @Override
    public void onInitializeClient() {
        if (DependencyChecker.hasGravity())
            GravityHandler.clientInit();
    }
}
