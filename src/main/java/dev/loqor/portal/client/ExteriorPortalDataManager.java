package dev.loqor.portal.client;

import dev.amble.ait.AITMod;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.packet.s2c.play.ChunkRenderDistanceCenterS2CPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Difficulty;

import java.util.UUID;

/**
 * Manages fake world and chunk data for viewing exterior world from interior
 */
public class ExteriorPortalDataManager extends PortalDataManager {
    private static ExteriorPortalDataManager instance;
    private UUID currentTardisId;
    private BlockPos currentCenter = BlockPos.ORIGIN;

    private ExteriorPortalDataManager(ClientWorld world, WorldRenderer worldRenderer) {
        super(world, worldRenderer, false); // Don't init center in parent
    }

    public static ExteriorPortalDataManager get() {
        if (instance != null) return instance;

        MinecraftClient client = MinecraftClient.getInstance();
        ClientWorld oldWorld = client.world;

        if (oldWorld == null) throw new IllegalStateException("Couldn't initialize a fake client world for exterior");

        WorldRenderer worldRenderer = new WorldRenderer(
                client,
                client.getEntityRenderDispatcher(),
                client.getBlockEntityRenderDispatcher(),
                client.getBufferBuilders()
        );

        // Create fake world for exterior view
        ClientWorld world = new ClientWorld(client.getNetworkHandler(), new ClientWorld.Properties(Difficulty.NORMAL,
                false, false), oldWorld.getRegistryKey(),
                oldWorld.getDimensionEntry(),
                12, client.world.getSimulationDistance(), client::getProfiler, worldRenderer,
                client.world.isDebugWorld(), client.world.getBiomeAccess().seed);

        worldRenderer.setWorld(world);

        AITMod.LOGGER.info("Created ExteriorPortalDataManager");
        return instance = new ExteriorPortalDataManager(world, worldRenderer);
    }

    /**
     * Update the center position for chunk loading (where the fake player is)
     */
    public void updateCenter(BlockPos center) {
        if (!center.equals(this.currentCenter)) {
            this.currentCenter = center;
            this.onChunkRenderDistanceCenter(new ChunkRenderDistanceCenterS2CPacket(center.getX() >> 4, center.getZ() >> 4));
            AITMod.LOGGER.debug("Updated exterior portal center to {}", center);
        }
    }

    /**
     * Set which TARDIS we're currently viewing
     */
    public void setTardis(UUID tardisId) {
        this.currentTardisId = tardisId;
    }

    public UUID getCurrentTardisId() {
        return currentTardisId;
    }

    @Override
    public void reset() {
        instance = null;
        super.reset();
    }
}
