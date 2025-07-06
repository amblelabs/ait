package dev.amble.ait.client.util;

import dev.amble.ait.AITMod;
import dev.amble.ait.client.AITModClient;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.*;
import net.minecraft.util.Identifier;

import static dev.amble.ait.AITMod.id;

@Environment(EnvType.CLIENT)
public class ResourcePackUtil {

    public static boolean isPixelConsistentPackActive() {
        ResourcePackManager manager = MinecraftClient.getInstance().getResourcePackManager();

        for (ResourcePackProfile profile : manager.getEnabledProfiles()) {
            if (profile.getName().equals("ait:pixel-consistent-ait")) {
                return true;
            }
        }
        return false;
    }

    public static boolean isResourcePresent(String namespace, String path) {
        Identifier id = new Identifier(namespace, path);
        return MinecraftClient.getInstance().getResourceManager().getResource(id).isPresent();
    }

    public static void registerResourcePacks() {
        FabricLoader.getInstance().getModContainer("ait").ifPresent(modContainer -> {
            ResourceManagerHelper.registerBuiltinResourcePack(id("pixel-consistent-ait"), modContainer, ResourcePackActivationType.NORMAL);
        });
    }

}