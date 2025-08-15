package dev.amble.ait.mixin.client.experimental_screen;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.server.integrated.IntegratedServerLoader;

import dev.amble.ait.client.AITModClient;

@Mixin(value = IntegratedServerLoader.class)
public abstract class WorldOpenFlowsMixin {

    @Shadow protected abstract void start(Screen parent, String levelName, boolean safeMode, boolean canShowBackupPrompt);

    @Inject(method = "start(Lnet/minecraft/client/gui/screen/Screen;Ljava/lang/String;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/integrated/IntegratedServerLoader;start(Lnet/minecraft/client/gui/screen/Screen;Ljava/lang/String;ZZ)V"), cancellable = true)
    private void skipBackupScreen(Screen parent, String levelName, CallbackInfo ci) {
        if (!AITModClient.CONFIG.showExperimentalWarning) {
            this.start(parent, levelName, false, false);
            ci.cancel();
        }
    }
}
