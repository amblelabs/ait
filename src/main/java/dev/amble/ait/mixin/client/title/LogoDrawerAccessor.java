package dev.amble.ait.mixin.client.title;

import net.minecraft.client.gui.LogoDrawer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LogoDrawer.class)
public interface LogoDrawerAccessor {
    @Accessor
    boolean getIgnoreAlpha();
}
