package dev.amble.ait.mixin.client;

import static dev.amble.ait.core.AITItems.isInAdvent;

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
    @Unique private static final Identifier AIT_CHRISTMAS_LOGO = AITMod.id("textures/gui/title/ait_christmas_logo.png");
    @Unique private final MinecraftClient client = MinecraftClient.getInstance();
    @Unique boolean isChristmas = isInAdvent();

    @Redirect(method = "draw(Lnet/minecraft/client/gui/DrawContext;IFI)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawTexture(Lnet/minecraft/util/Identifier;IIFFIIII)V", ordinal = 0))
    private void ait$drawCustomLogo(DrawContext context, Identifier texture, int x, int y, float u, float v, int width,
                                    int height, int textureWidth, int textureHeight) {

        Identifier currentLogo;
        if (isChristmas) {
            currentLogo = AIT_CHRISTMAS_LOGO;
        } else {
            currentLogo = AIT_LOGO;
        }

        if (!AITModClient.CONFIG.customMenu) {
            context.drawTexture(texture, x, y, u, v, width, height, textureWidth, textureHeight);
            return;
        }

        int screenWidth = this.client.getWindow().getScaledWidth();
        int centerX = screenWidth / 2 - 128;
        if (isChristmas) {
            context.drawTexture(currentLogo, centerX, y - 18, 0.0f, 0.0f, 266,  74, 266, 74);
        } else {
            context.drawTexture(currentLogo, centerX, y - 18, 0.0f, 0.0f, 266, 94, 266, 94);
        }
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

            String warningMessage;
            if (isChristmas) {
                warningMessage =  "HO HO HO!: You are using an experimental branch (" + AITMod.BRANCH + "), please be cautious when testing or the grinch will smell you toes!";
            } else {
                warningMessage =  "WARNING!: You are using an experimental version (" + AITMod.BRANCH + "), please be cautious when testing!";
            }

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