package dev.amble.ait.client.boti;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.world.ClientWorld;

public class PortalParticleManager extends ParticleManager {

    public PortalParticleManager(ClientWorld world, MinecraftClient client) {
        super(world, client.getTextureManager());

        ParticleManager main = client.particleManager;
        this.factories = main.factories;
        this.spriteAwareFactories = main.spriteAwareFactories;
        this.particleAtlasTexture = main.particleAtlasTexture;
    }
}
