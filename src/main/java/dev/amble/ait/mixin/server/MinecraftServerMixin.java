package dev.amble.ait.mixin.server;

import dev.amble.ait.AITMod;
import dev.amble.ait.core.events.ServerCrashEvent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.crash.CrashReport;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin {
    @Inject(method = "setCrashReport", at = @At("TAIL"))
    private void ait$setCrashReport(CrashReport report, CallbackInfo info) {
        AITMod.LOGGER.error("Crash Detected - nice one m8");
        ServerCrashEvent.EVENT.invoker().onServerCrash((MinecraftServer) (Object) this, report);
    }
}
