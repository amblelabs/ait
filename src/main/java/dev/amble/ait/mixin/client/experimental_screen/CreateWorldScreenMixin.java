package dev.amble.ait.mixin.client.experimental_screen;

import com.mojang.serialization.Lifecycle;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.world.GeneratorOptionsHolder;
import net.minecraft.registry.CombinedDynamicRegistries;
import net.minecraft.registry.ServerDynamicRegistryType;
import net.minecraft.world.dimension.DimensionOptionsRegistryHolder;
import net.minecraft.world.level.LevelProperties;

import dev.amble.ait.client.AITModClient;

@SuppressWarnings("deprecation")
@Mixin(value = CreateWorldScreen.class)
public abstract class CreateWorldScreenMixin {
    @Shadow
    private boolean recreated;

    @Shadow
    protected abstract void startServer(LevelProperties.SpecialProperty specialProperty,
            CombinedDynamicRegistries<ServerDynamicRegistryType> combinedDynamicRegistries, Lifecycle lifecycle);

    @Inject(method = "createLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/integrated/IntegratedServerLoader;tryLoad(Lnet/minecraft/client/MinecraftClient;Lnet/minecraft/client/gui/screen/world/CreateWorldScreen;Lcom/mojang/serialization/Lifecycle;Ljava/lang/Runnable;Z)V"), locals = LocalCapture.CAPTURE_FAILSOFT, cancellable = true)
    private void onCreate(CallbackInfo ci, GeneratorOptionsHolder holder,
            DimensionOptionsRegistryHolder.DimensionsConfig config,
            CombinedDynamicRegistries<ServerDynamicRegistryType> registries, Lifecycle lifecycle, Lifecycle lifecycle2,
            Lifecycle lifecycle3, boolean showWarnings) {
        if (this.recreated)
            return;

        if (!AITModClient.CONFIG.showExperimentalWarning) {
            this.startServer(config.specialWorldProperty(), registries, lifecycle3);
            ci.cancel();
        }
    }
}
