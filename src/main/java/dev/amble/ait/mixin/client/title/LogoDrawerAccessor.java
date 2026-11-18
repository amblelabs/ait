package dev.amble.ait.mixin.client.title;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.gui.LogoDrawer;

@Mixin(LogoDrawer.class)
public interface LogoDrawerAccessor {
    @Accessor
    boolean getIgnoreAlpha();
}
