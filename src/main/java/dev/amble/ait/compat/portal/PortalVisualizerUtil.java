package dev.amble.ait.compat.portal;

import dev.amble.ait.AITMod;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.SimpleFramebuffer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.joml.Matrix4f;
import qouteall.imm_ptl.core.CHelper;
import qouteall.imm_ptl.core.ClientWorldLoader;
import qouteall.imm_ptl.core.api.PortalAPI;
import qouteall.imm_ptl.core.chunk_loading.ChunkLoader;
import qouteall.imm_ptl.core.chunk_loading.DimensionalChunkPos;
import qouteall.imm_ptl.core.render.GuiPortalRendering;
import qouteall.imm_ptl.core.render.MyRenderHelper;
import qouteall.imm_ptl.core.render.context_management.WorldRenderInfo;
import qouteall.q_misc_util.my_util.DQuaternion;

import java.util.Optional;
import java.util.WeakHashMap;

public class PortalVisualizerUtil {

    public static final Identifier OPEN_VISUALIZER = AITMod.id("ip/visualizer/open");
    public static final Identifier CLOSE_VISUALIZER = AITMod.id("ip/visualizer/close");

    /**
     * The Framebuffer that the GUI portal is going to render onto
     */
    @Environment(EnvType.CLIENT)
    private static Framebuffer frameBuffer;
    
    private static final WeakHashMap<ServerPlayerEntity, ChunkLoader>
        chunkLoaderMap = new WeakHashMap<>();

    public static void init() {
        PortalsAPI.VISUALIZER = Optional.of(PortalVisualizerUtil::open);

        ServerPlayNetworking.registerGlobalReceiver(CLOSE_VISUALIZER, (server, player, handler, buf, sender) -> {
            server.execute(() -> removeChunkLoaderFor(player));
        });
    }

    @Environment(EnvType.CLIENT)
    public static void clientInit() {
        ClientPlayNetworking.registerGlobalReceiver(OPEN_VISUALIZER, (client, handler, buf, sender) -> {
            RegistryKey<World> dim = buf.readRegistryKey(RegistryKeys.WORLD);
            BlockPos pos = buf.readBlockPos();

            client.execute(() -> {
                if (frameBuffer == null) frameBuffer = new SimpleFramebuffer(2, 2, true, true);

                client.setScreen(new GuiPortalScreen(dim, pos.toCenterPos()));
            });
        });
    }
    
    private static void removeChunkLoaderFor(ServerPlayerEntity player) {
        ChunkLoader chunkLoader = chunkLoaderMap.remove(player);
        if (chunkLoader != null) {
            PortalAPI.removeChunkLoaderForPlayer(player, chunkLoader);
        }
    }
    
    public static void open(ServerPlayerEntity player, ServerWorld world, BlockPos pos) {
        removeChunkLoaderFor(player);
        
        ChunkLoader chunkLoader = new ChunkLoader(
            new DimensionalChunkPos(
                world.getRegistryKey(), new ChunkPos(pos)
            ),
            8
        );
        
        // Add the per-player additional chunk loader
        PortalAPI.addChunkLoaderForPlayer(player, chunkLoader);
        chunkLoaderMap.put(player, chunkLoader);
        
        // Tell the client to open the screen
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeRegistryKey(world.getRegistryKey());
        buf.writeBlockPos(pos);

        ServerPlayNetworking.send(player, OPEN_VISUALIZER, buf);
    }

    @Environment(EnvType.CLIENT)
    public static class GuiPortalScreen extends Screen {

        private static final Identifier TEXTURE = AITMod.id("textures/gui/tardis/monitor/visualizer_menu.png");
        private static final Identifier OVERLAY = AITMod.id("textures/gui/tardis/monitor/visualizer_overlay.png");

        private static final int bgHeight = 154;
        private static final int bgWidth = 256;

        private static final int bgBorder = 9;

        private final RegistryKey<World> viewingDimension;
        
        private final Vec3d viewingPosition;
        
        public GuiPortalScreen(RegistryKey<World> viewingDimension, Vec3d viewingPosition) {
            super(Text.translatable("screen.ait.visualizer.title"));

            this.viewingDimension = viewingDimension;
            this.viewingPosition = viewingPosition;
        }
        
        @Override
        public void close() {
            super.close();
            
            ClientPlayNetworking.send(CLOSE_VISUALIZER, PacketByteBufs.create());
        }

        @Override
        public void render(DrawContext context, int mouseX, int mouseY, float delta) {
            int top = (this.height - bgHeight) / 2;
            int left = (this.width - bgWidth) / 2;

            context.drawTexture(TEXTURE, left, top, 0, 0, bgWidth, bgHeight);

            double t1 = CHelper.getSmoothCycles(503);

            // Determine the camera transformation
            Matrix4f cameraTransformation = new Matrix4f();
            cameraTransformation.identity();
            cameraTransformation.mul(
                    DQuaternion.rotationByDegrees(
                            new Vec3d(0, 1, 0).normalize(),
                            t1 * 360
                    ).toMatrix()
            );

            // Create the world render info
            WorldRenderInfo worldRenderInfo = new WorldRenderInfo.Builder()
                    .setWorld(ClientWorldLoader.getWorld(viewingDimension))
                    .setCameraPos(viewingPosition)
                    .setCameraTransformation(cameraTransformation)
                    .setOverwriteCameraTransformation(true) // do not apply camera transformation to existing player camera transformation
                    .setDescription(null)
                    .setRenderDistance(client.options.getClampedViewDistance())
                    .setDoRenderHand(false)
                    .setEnableViewBobbing(false)
                    .setDoRenderSky(false)
                    .setHasFog(false)
                    .build();

            // Ask it to render the world into the framebuffer the next frame
            GuiPortalRendering.submitNextFrameRendering(worldRenderInfo, frameBuffer);

            float scale = (float) client.getWindow().getScaleFactor();

            // Draw the framebuffer
            MyRenderHelper.drawFramebuffer(
                    frameBuffer,
                    true, // enable alpha blend
                    false, // don't modify alpha
                    (left + bgBorder) * scale, (left + bgWidth - bgBorder) * scale,
                    (top + bgBorder) * scale, (top + bgHeight - bgBorder) * scale
            );

            context.drawTexture(OVERLAY, left, top, 0, 0, bgWidth, bgHeight);
        }

        @Override
        public boolean shouldPause() {
            return false;
        }

        // close when E is pressed
        @Override
        public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
            if (super.keyPressed(keyCode, scanCode, modifiers)) {
                return true;
            }
            
            if (client.options.inventoryKey.matchesKey(keyCode, scanCode)) {
                this.close();
                return true;
            }
            
            return false;
        }
    }
}