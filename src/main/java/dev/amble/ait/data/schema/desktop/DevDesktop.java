package dev.amble.ait.data.schema.desktop;


import dev.amble.ait.AITMod;
import dev.amble.ait.data.Loyalty;
import dev.amble.ait.data.schema.desktop.textures.DesktopPreviewTexture;

public class DevDesktop extends TardisDesktopSchema {

    public DevDesktop() {
        super(AITMod.id("dev"),
                new DesktopPreviewTexture(DesktopPreviewTexture.pathFromDesktopId(AITMod.id("dev")),
                        800, 800),
                new Loyalty(Loyalty.Type.OWNER));
    }

    @Override
    public boolean freebie() {
        return false;
    }
}
