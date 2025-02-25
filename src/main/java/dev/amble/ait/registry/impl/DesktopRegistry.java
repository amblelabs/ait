package dev.amble.ait.registry.impl;

import dev.amble.lib.register.unlockable.UnlockableRegistry;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;

import net.minecraft.resource.ResourceType;

import dev.amble.ait.data.datapack.DatapackDesktop;
import dev.amble.ait.data.schema.desktop.DefaultCaveDesktop;
import dev.amble.ait.data.schema.desktop.DevDesktop;
import dev.amble.ait.data.schema.desktop.TardisDesktopSchema;

public class DesktopRegistry extends UnlockableRegistry<TardisDesktopSchema> {

    private static final DesktopRegistry INSTANCE = new DesktopRegistry();

    protected DesktopRegistry() {
        super(DatapackDesktop::fromInputStream, DatapackDesktop.CODEC, "desktop", true);
    }

    @Override
    public void onCommonInit() {
        super.onCommonInit();
        this.defaults();
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(this);
    }

    @Override
    public TardisDesktopSchema fallback() {
        throw new UnsupportedOperationException("No desktops registered!");
    }

    public static DesktopRegistry getInstance() {
        return INSTANCE;
    }

    public static TardisDesktopSchema DEFAULT_CAVE;
    public static TardisDesktopSchema DEV;

    @Override
    protected void defaults() {
        DEFAULT_CAVE = register(new DefaultCaveDesktop());
        DEV = register(new DevDesktop());
    }
}
