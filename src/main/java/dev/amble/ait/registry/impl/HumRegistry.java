package dev.amble.ait.registry.impl;


import dev.amble.lib.register.datapack.SimpleDatapackRegistry;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.resource.ResourceType;

import dev.amble.ait.AITMod;
import dev.amble.ait.client.sounds.ClientSoundManager;
import dev.amble.ait.core.AITSounds;
import dev.amble.ait.data.hum.DatapackHum;
import dev.amble.ait.data.hum.Hum;

public class HumRegistry extends SimpleDatapackRegistry<Hum> {
    private static final HumRegistry instance = new HumRegistry();

    protected HumRegistry() {
        super(DatapackHum::fromInputStream, DatapackHum.CODEC, "hum", true, AITMod.MOD_ID);
    }

    public static HumRegistry getInstance() {
        return instance;
    }

    public static Hum CORAL;
    public static Hum CHRISTMAS;

    @Override
    public void onCommonInit() {
        super.onCommonInit();
        this.defaults();
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(this);
    }

    @Override
    protected void defaults() {
        CORAL = register(Hum.create(AITMod.MOD_ID, "coral", AITSounds.CORAL_HUM));
        CHRISTMAS = register(Hum.create(AITMod.MOD_ID, "christmas", AITSounds.CHRISTMAS_HUM));
    }

    @Override
    public Hum fallback() {
        return CORAL;
    }

    @Override
    public void readFromServer(PacketByteBuf buf) {
        super.readFromServer(buf);

        ClientSoundManager.getHum().onSynced();
    }
}
