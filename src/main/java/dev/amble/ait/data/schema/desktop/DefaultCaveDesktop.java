package dev.amble.ait.data.schema.desktop;


import dev.amble.ait.AITMod;
import dev.amble.ait.data.schema.desktop.textures.DesktopPreviewTexture;

public class DefaultCaveDesktop extends TardisDesktopSchema {

    public DefaultCaveDesktop() {
        super(AITMod.id("default_cave"), new DesktopPreviewTexture(
                DesktopPreviewTexture.pathFromDesktopId(AITMod.id("cave"))));
    }

    @Override
    public boolean freebie() {
        return false;
    }
}
