package dev.amble.ait.mixin.client;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.client.gui.CubeMapRenderer;
import net.minecraft.client.gui.RotatingCubeMapRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

import dev.amble.ait.AITMod;
import dev.amble.ait.client.AITModClient;
import dev.amble.ait.core.devteam.BetaVerification;

@Mixin(value = TitleScreen.class, priority = 999)
public abstract class TitleScreenMixin extends Screen {

    protected TitleScreenMixin(Text title) {
        super(title);
    }

    @Unique private static final RotatingCubeMapRenderer NEWPANO = new RotatingCubeMapRenderer(
            new CubeMapRenderer(AITMod.id("textures/gui/title/background/panorama"))
    );

    // This modifies the panorama in the background
    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/RotatingCubeMapRenderer;render(FF)V", ordinal = 0))
    private void something(RotatingCubeMapRenderer instance, float delta, float alpha) {
        boolean isConfigEnabled = AITModClient.CONFIG.customMenu;

        if (isConfigEnabled)
            NEWPANO.render(delta, alpha);
        else
            instance.render(delta, alpha);
    }

    @Redirect(method = "initWidgetsNormal", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/widget/ButtonWidget;builder(Lnet/minecraft/text/Text;Lnet/minecraft/client/gui/widget/ButtonWidget$PressAction;)Lnet/minecraft/client/gui/widget/ButtonWidget$Builder;", ordinal = 0))
    private ButtonWidget.Builder initWidgetsNormal1(Text message, ButtonWidget.PressAction onPress) {
        boolean beta = AITMod.isBetaLocked();

        ButtonWidget.Builder instance = new ButtonWidget.Builder(beta ? Text.translatable("text.ait.beta.play") : message, button -> {
            if (BetaVerification.isServerRunning()) {
                // TODO: maybe use whatever minecraft is using? if it isn't using this ig?
                StringSelection stringSelection = new StringSelection(BetaVerification.getAuthUrl());
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                clipboard.setContents(stringSelection, null);
                return;
            }
            if (AITMod.isBetaLocked()) {
                button.setMessage(Text.translatable("text.ait.beta.play.browser"));
                BetaVerification.startAndWaitForToken(valid -> {
                    if (valid) {
                        button.setTooltip(null);
                        button.setMessage(message);
                    } else {
                        button.setMessage(Text.translatable("text.ait.beta.play"));
                    }
                });
                return;
            }

            onPress.onPress(button);
        });

        if (beta)
            instance = instance.tooltip(Tooltip.of(Text.translatable("text.ait.beta.play.tooltip")));

        return instance;
    }
}
