package dev.amble.ait.mixin.client;

import dev.amble.ait.core.devteam.BetaTeam;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.client.gui.CubeMapRenderer;
import net.minecraft.client.gui.RotatingCubeMapRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.text.Text;

import dev.amble.ait.AITMod;
import dev.amble.ait.client.AITModClient;

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

    @Redirect(method = "initWidgetsNormal", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/widget/ButtonWidget$Builder;build()Lnet/minecraft/client/gui/widget/ButtonWidget;", ordinal = 0))
    private ButtonWidget initWidgetsNormal(ButtonWidget.Builder instance) {
        boolean disabled = AITMod.isOfficialBeta() && BetaTeam.isBetaTester(this.client.player.getGameProfile().getId()) == TriState.FALSE;

        if (disabled)
            instance = instance.tooltip(Tooltip.of(Text.translatable("text.ait.not_a_tester")));

        ButtonWidget result = instance.build();
        result.active = !disabled;

        return result;
    }
}
