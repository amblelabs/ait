package dev.amble.ait.compat.iris;

import java.util.function.BooleanSupplier;

import net.irisshaders.iris.api.v0.IrisApi;

public class IrisCompat {

    private static BooleanSupplier SHADOW_PASS = () -> false;

    public static void clientInit() {
        SHADOW_PASS = () -> IrisApi.getInstance().isRenderingShadowPass();
    }

    public static boolean isRenderingShadowPass() {
        return SHADOW_PASS.getAsBoolean();
    }
}
