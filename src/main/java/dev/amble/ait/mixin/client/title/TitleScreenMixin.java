package dev.amble.ait.mixin.client.title;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.amble.ait.client.screens.widget.AITLogoDrawer;
import dev.amble.ait.core.devteam.ClientDevTeam;
import dev.amble.verification.BetaVerification;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.gui.LogoDrawer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin extends Screen {

    @Shadow @Final @Mutable private LogoDrawer logoDrawer;
    @Unique private static final Tooltip VERIFY_TOOLTIP = Tooltip.of(ClientDevTeam.Verify.TEXT_TOOLTIP);

    protected TitleScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At("TAIL"))
    public void init(CallbackInfo ci) {
        boolean customMenu = this.client.getResourcePackManager().getEnabledNames().contains("ait:menu");

        if (customMenu && !(this.logoDrawer instanceof AITLogoDrawer))
            this.logoDrawer = new AITLogoDrawer(this.logoDrawer);
        else if (!customMenu && this.logoDrawer instanceof AITLogoDrawer customLogo)
            this.logoDrawer = customLogo.getParent();
    }

    @WrapOperation(method = "initWidgetsNormal", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/widget/ButtonWidget;builder(Lnet/minecraft/text/Text;Lnet/minecraft/client/gui/widget/ButtonWidget$PressAction;)Lnet/minecraft/client/gui/widget/ButtonWidget$Builder;", ordinal = 0))
    private ButtonWidget.Builder initWidgetsNormal(Text message, ButtonWidget.PressAction onPress, Operation<ButtonWidget.Builder> original) {
        if (!ClientDevTeam.VERIFY.isLocked()) return original.call(message, onPress);

        return new ButtonWidget.Builder(ClientDevTeam.Verify.TEXT_PLAY, button -> {
            if (BetaVerification.isAttemptingAuth()) {
                // TODO: maybe use whatever minecraft is using? if it isn't using this ig?
                StringSelection stringSelection = new StringSelection(ClientDevTeam.VERIFY.getAuthUrl());
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                clipboard.setContents(stringSelection, null);
                return;
            }

            if (ClientDevTeam.VERIFY.isLocked()) {
                button.setMessage(ClientDevTeam.Verify.TEXT_BROWSER);
                ClientDevTeam.VERIFY.startAndWaitForToken(valid -> {
                    if (valid) {
                        button.setTooltip(null);
                        button.setMessage(message);
                    } else {
                        button.setMessage(ClientDevTeam.Verify.TEXT_PLAY);
                    }
                });

                return;
            }

            onPress.onPress(button);
        }).tooltip(VERIFY_TOOLTIP);
    }
}
