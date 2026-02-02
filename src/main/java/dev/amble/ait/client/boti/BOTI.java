package dev.amble.ait.client.boti;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import com.mojang.blaze3d.platform.GlConst;
import com.mojang.blaze3d.platform.GlStateManager;

import dev.loqor.portal.client.WorldGeometryRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import dev.amble.ait.client.AITModClient;
import dev.amble.ait.compat.DependencyChecker;
import dev.amble.ait.core.blockentities.DoorBlockEntity;
import dev.amble.ait.core.blockentities.ExteriorBlockEntity;
import dev.amble.ait.core.entities.BOTIPaintingEntity;
import dev.amble.ait.core.entities.RiftEntity;

public class BOTI {
    public static final Queue<RiftEntity> RIFT_RENDERING_QUEUE = new LinkedList<>();
    public static BOTIInit BOTI_HANDLER = new BOTIInit();
    public static AITBufferBuilderStorage AIT_BUF_BUILDER_STORAGE = new AITBufferBuilderStorage();
    public static List<DoorBlockEntity> DOOR_RENDER_QUEUE = new CopyOnWriteArrayList<>();
    public static List<BOTIPaintingEntity> GALLIFREYAN_RENDER_QUEUE = new CopyOnWriteArrayList<>();
    public static List<BOTIPaintingEntity> TRENZALORE_PAINTING_QUEUE = new CopyOnWriteArrayList<>();
    public static List<ExteriorBlockEntity> EXTERIOR_RENDER_QUEUE = new CopyOnWriteArrayList<>();
    private static boolean HAS_BEEN_WARNED = false;
    
    // Per-TARDIS renderer maps for multi-instance support
    private static final Map<UUID, WorldGeometryRenderer> INTERIOR_RENDERERS = new ConcurrentHashMap<>();
    private static final Map<UUID, WorldGeometryRenderer> EXTERIOR_RENDERERS = new ConcurrentHashMap<>();
    private static final int MAX_RENDERERS = 10; // Limit to prevent memory issues

    /**
     * Gets or creates an interior renderer for a specific TARDIS
     */
    public static WorldGeometryRenderer getInteriorRenderer(UUID tardisId) {
        return INTERIOR_RENDERERS.computeIfAbsent(tardisId, id -> {
            // Check if we've hit the limit
            if (INTERIOR_RENDERERS.size() >= MAX_RENDERERS) {
                // Remove oldest renderer (simple LRU - remove first entry)
                Iterator<Map.Entry<UUID, WorldGeometryRenderer>> iterator = INTERIOR_RENDERERS.entrySet().iterator();
                if (iterator.hasNext()) {
                    Map.Entry<UUID, WorldGeometryRenderer> entry = iterator.next();
                    entry.getValue().close();
                    iterator.remove();
                }
            }
            
            MinecraftClient client = MinecraftClient.getInstance();
            WorldGeometryRenderer renderer = new WorldGeometryRenderer(25);
            if (client.getWindow() != null) {
                float aspect = (float) client.getWindow().getFramebufferWidth() / (float) client.getWindow().getFramebufferHeight();
                renderer.setPerspectiveProjection(
                    client.options.getFov().getValue() * client.player.getFovMultiplier(),
                    aspect, 0.05f, 2000.0f
                );
            }
            return renderer;
        });
    }

    /**
     * Gets or creates an exterior renderer for a specific TARDIS
     */
    public static WorldGeometryRenderer getExteriorRenderer(UUID tardisId) {
        return EXTERIOR_RENDERERS.computeIfAbsent(tardisId, id -> {
            // Check if we've hit the limit
            if (EXTERIOR_RENDERERS.size() >= MAX_RENDERERS) {
                // Remove oldest renderer (simple LRU - remove first entry)
                Iterator<Map.Entry<UUID, WorldGeometryRenderer>> iterator = EXTERIOR_RENDERERS.entrySet().iterator();
                if (iterator.hasNext()) {
                    Map.Entry<UUID, WorldGeometryRenderer> entry = iterator.next();
                    entry.getValue().close();
                    iterator.remove();
                }
            }
            
            MinecraftClient client = MinecraftClient.getInstance();
            WorldGeometryRenderer renderer = new WorldGeometryRenderer(25);
            if (client.getWindow() != null) {
                float aspect = (float) client.getWindow().getFramebufferWidth() / (float) client.getWindow().getFramebufferHeight();
                renderer.setPerspectiveProjection(
                    client.options.getFov().getValue() * client.player.getFovMultiplier(),
                    aspect, 0.05f, 2000.0f
                );
            }
            return renderer;
        });
    }

    /**
     * Removes and cleans up a TARDIS renderer when it's no longer needed
     */
    public static void removeRenderer(UUID tardisId) {
        WorldGeometryRenderer interiorRenderer = INTERIOR_RENDERERS.remove(tardisId);
        if (interiorRenderer != null) {
            interiorRenderer.close();
        }
        
        WorldGeometryRenderer exteriorRenderer = EXTERIOR_RENDERERS.remove(tardisId);
        if (exteriorRenderer != null) {
            exteriorRenderer.close();
        }
    }

    /**
     * Cleans up all renderers - call on disconnect
     */
    public static void cleanupAllRenderers() {
        INTERIOR_RENDERERS.values().forEach(WorldGeometryRenderer::close);
        INTERIOR_RENDERERS.clear();
        
        EXTERIOR_RENDERERS.values().forEach(WorldGeometryRenderer::close);
        EXTERIOR_RENDERERS.clear();
    }

    /**
     * Updates projection for all renderers - call on window resize
     */
    public static void updateAllRendererProjections() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.getWindow() == null) return;
        
        float aspect = (float) client.getWindow().getFramebufferWidth() / (float) client.getWindow().getFramebufferHeight();
        float fov = client.options.getFov().getValue();
        if (client.player != null) {
            fov *= client.player.getFovMultiplier();
        }
        
        final float finalFov = fov;
        INTERIOR_RENDERERS.values().forEach(renderer -> 
            renderer.setPerspectiveProjection(finalFov, aspect, 0.05f, 2000.0f)
        );
        EXTERIOR_RENDERERS.values().forEach(renderer ->
            renderer.setPerspectiveProjection(finalFov, aspect, 0.05f, 2000.0f)
        );
    }

    public static void copyFramebuffer(Framebuffer src, Framebuffer dest) {
        GlStateManager._glBindFramebuffer(GlConst.GL_READ_FRAMEBUFFER, src.fbo);
        GlStateManager._glBindFramebuffer(GlConst.GL_DRAW_FRAMEBUFFER, dest.fbo);
        GlStateManager._glBlitFrameBuffer(0, 0, src.textureWidth, src.textureHeight, 0, 0, dest.textureWidth, dest.textureHeight, GlConst.GL_DEPTH_BUFFER_BIT | GlConst.GL_COLOR_BUFFER_BIT, GlConst.GL_NEAREST);
    }

    public static void copyColor(Framebuffer src, Framebuffer dest) {
        GlStateManager._glBindFramebuffer(GlConst.GL_READ_FRAMEBUFFER, src.fbo);
        GlStateManager._glBindFramebuffer(GlConst.GL_DRAW_FRAMEBUFFER, dest.fbo);
        GlStateManager._glBlitFrameBuffer(0, 0, src.textureWidth, src.textureHeight, 0, 0, dest.textureWidth, dest.textureHeight, GlConst.GL_COLOR_BUFFER_BIT, GlConst.GL_NEAREST);
    }

    public static void copyDepth(Framebuffer src, Framebuffer dest) {
        GlStateManager._glBindFramebuffer(GlConst.GL_READ_FRAMEBUFFER, src.fbo);
        GlStateManager._glBindFramebuffer(GlConst.GL_DRAW_FRAMEBUFFER, dest.fbo);
        GlStateManager._glBlitFrameBuffer(0, 0, src.textureWidth, src.textureHeight, 0, 0, dest.textureWidth, dest.textureHeight, GlConst.GL_DEPTH_BUFFER_BIT, GlConst.GL_NEAREST);
    }

    public static void setFramebufferColor(Framebuffer src, float r, float g, float b, float a) {
        src.setClearColor(r, g, b, a);
    }

    /**
     * Warns the user if they are missing Indium and have a non-Nvidia card.
     */
    public static void tryWarn(MinecraftClient client) {
        if (HAS_BEEN_WARNED)
            return;

        if (warn(client)) AITModClient.CONFIG.enableTardisBOTI = false;

        HAS_BEEN_WARNED = true;
    }

    /**
     * @return {@code true} if successfully warned the player, {@code false} otherwise
     */
    private static boolean warn(MinecraftClient client) {
        if (DependencyChecker.hasMacOs()) {
            tryWarnMac(client);
            return true;
        }

        if (DependencyChecker.hasIndium())
            return false;

        if (!DependencyChecker.hasNvidiaCard()) {
            tryWarnAmd(client);
            return true;
        }

        return false;
    }

    private static void tryWarnMac(MinecraftClient client) {
        client.player.sendMessage(Text.literal("You appear to be playing on a Mac. Indium is required, but is not found. This may cause issues with the mod - BOTI has been disabled!").formatted(Formatting.RED), false);
    }

    private static void tryWarnAmd(MinecraftClient client) {
        client.player.sendMessage(Text.literal("You appear to have an AMD GPU. Indium is required, but is not found. This may cause issues with the mod - BOTI has been disabled!").formatted(Formatting.RED), false);
    }
}
