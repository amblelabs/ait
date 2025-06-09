package dev.amble.ait.client.util;

import dev.amble.ait.AITMod;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.PostEffectProcessor;

import java.io.IOException;

public class ShaderUtils {
    public static MinecraftClient client = MinecraftClient.getInstance();
    public static PostEffectProcessor shader;
    public static boolean enabled = false;

    private static PostEffectProcessor getCurrent() {
        try {
            // "values": [ 0.3, 0.59, 0.11 ]
            return new PostEffectProcessor(client.getTextureManager(), client.getResourceManager(),
                    client.getFramebuffer(), AITMod.id("shaders/post/red_tinted.json"));
        } catch (IOException e) {
            return null;
        }
    }

    public static void load() {
        if (shader != null)
            shader.close();
        shader = getCurrent();
        if (shader != null) {
            shader.setupDimensions(client.getWindow().getFramebufferWidth(), client.getWindow().getFramebufferHeight());
            enabled = true;
            return;
        }
        enabled = false;
    }
}
