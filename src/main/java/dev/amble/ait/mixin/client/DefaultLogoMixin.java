package dev.amble.ait.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.LogoDrawer;
import net.minecraft.util.Identifier;

import dev.amble.ait.AITMod;
import dev.amble.ait.client.AITModClient;

@Mixin(LogoDrawer.class)
public class DefaultLogoMixin {

    @Unique private static final Identifier AIT_LOGO = AITMod.id("textures/gui/title/ait_logo.png");
    @Unique private final MinecraftClient client = MinecraftClient.getInstance();

    @Redirect(method = "draw(Lnet/minecraft/client/gui/DrawContext;IFI)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawTexture(Lnet/minecraft/util/Identifier;IIFFIIII)V", ordinal = 0))
    private void ait$drawCustomLogo(DrawContext context, Identifier texture, int x, int y, float u, float v, int width,
                                    int height, int textureWidth, int textureHeight) {

        if (!AITModClient.CONFIG.customMenu) {
            context.drawTexture(texture, x, y, u, v, width, height, textureWidth, textureHeight);
            return;
        }

        int screenWidth = MinecraftClient.getInstance().getWindow().getScaledWidth();
        int centerX = screenWidth / 2 - 121;
        int screenHeight = MinecraftClient.getInstance().getWindow().getScaledHeight();
        int centerY = screenHeight / 5 - 21;
        context.drawTexture(AIT_LOGO, centerX, centerY, 0, 0, 242, 42, 242, 42);
    }

    @Redirect(method = "draw(Lnet/minecraft/client/gui/DrawContext;IFI)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawTexture(Lnet/minecraft/util/Identifier;IIFFIIII)V", ordinal = 1))
    private void ait$skipEdition(DrawContext context, Identifier texture, int x, int y, float u, float v, int width,
                                 int height, int textureWidth, int textureHeight) {
        if (!AITModClient.CONFIG.customMenu)
            context.drawTexture(texture, x, y, u, v, width, height, textureWidth, textureHeight);
    }

    @Inject(method = "draw(Lnet/minecraft/client/gui/DrawContext;IFI)V", at = @At("TAIL"))
    private void renderWarningMessage(DrawContext context, int screenWidth, float alpha, int y, CallbackInfo ci) {
        if (AITMod.isUnsafeBranch()) {

            String warningMessage =  "WARNING!: You are using an experimental version (" + AITMod.BRANCH + "), please be cautious when testing!";

            screenWidth = this.client.getWindow().getScaledWidth();
            int textWidth = this.client.textRenderer.getWidth(warningMessage);


            int x = (screenWidth - textWidth) / 2;
            y = 10;
            int padding = 7;


            context.fill(0, y - padding, screenWidth, y + this.client.textRenderer.fontHeight + padding, 0xAA000000);

            context.drawText(this.client.textRenderer, warningMessage, x, y, 0xFFFF0000, true);
        }
    }
}