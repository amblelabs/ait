package dev.amble.ait.mixin.client;

import dev.amble.ait.AITMod;
import dev.amble.ait.client.AITModClient;
import net.minecraft.client.gui.CubeMapRenderer;
import net.minecraft.client.gui.RotatingCubeMapRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import static dev.amble.ait.core.AITItems.isInAdvent;

@Mixin(value = TitleScreen.class, priority = 999)
public abstract class TitleScreenMixin extends Screen {

    protected TitleScreenMixin(Text title) {
        super(title);
    }

    @Unique private static final boolean isChristmas = isInAdvent();

    @Unique private static final RotatingCubeMapRenderer NEWPANO = new RotatingCubeMapRenderer(
            isChristmas
                    ? new CubeMapRenderer(AITMod.id("textures/gui/title/background/advent/panorama"))
                    : new CubeMapRenderer(AITMod.id("textures/gui/title/background/panorama"))
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
}
