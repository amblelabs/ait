package dev.amble.ait.client.renderers.machines;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import dev.amble.ait.AITMod;
import dev.amble.ait.client.renderers.AITRenderLayers;
import dev.amble.ait.core.engine.link.block.CableBlockEntity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class CableEmissionRenderer<T extends CableBlockEntity> implements BlockEntityRenderer<T> {

    public static final Identifier BASE = AITMod.id("block/piping");
    public static final Identifier EMISSION = AITMod.id("block/piping_emission");

    private static final Direction[] FACES = { null, Direction.DOWN, Direction.UP, Direction.NORTH, Direction.SOUTH,
            Direction.WEST, Direction.EAST };
    private static final int VERTEX_STRIDE = 8;
    private static final int U_OFFSET = 4;
    private static final int V_OFFSET = 5;

    private static final Map<BakedModel, List<List<BakedQuad>>> CACHE = new IdentityHashMap<>();
    private static Sprite cachedSprite;

    private final Random random = Random.create();

    public CableEmissionRenderer(BlockEntityRendererFactory.Context context) {
    }

    @Override
    public void render(T entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        World world = entity.getWorld();

        if (world == null)
            return;

        BlockState state = entity.getCachedState();
        BakedModel model = MinecraftClient.getInstance().getBlockRenderManager().getModel(state);
        List<List<BakedQuad>> quads = emissionQuads(model, state);

        if (quads.isEmpty())
            return;

        BlockPos pos = entity.getPos();
        VertexConsumer buffer = vertexConsumers.getBuffer(AITRenderLayers.tardisEmissiveCullZOffset(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE));
        MatrixStack.Entry entry = matrices.peek();

        for (int i = 0; i < FACES.length; i++) {
            Direction face = FACES[i];

            if (face != null && !Block.shouldDrawSide(state, world, pos, face, pos.offset(face)))
                continue;

            for (BakedQuad quad : quads.get(i))
                buffer.quad(entry, quad, 1f, 1f, 1f, LightmapTextureManager.MAX_LIGHT_COORDINATE, overlay);
        }
    }

    private List<List<BakedQuad>> emissionQuads(BakedModel model, BlockState state) {
        Sprite emission = emissionSprite();

        if (emission != cachedSprite) {
            CACHE.clear();
            cachedSprite = emission;
        }

        return CACHE.computeIfAbsent(model, baked -> build(baked, state, emission));
    }

    private List<List<BakedQuad>> build(BakedModel model, BlockState state, Sprite emission) {
        List<List<BakedQuad>> result = new ArrayList<>(FACES.length);
        boolean any = false;

        for (Direction face : FACES) {
            List<BakedQuad> converted = new ArrayList<>();

            for (BakedQuad quad : model.getQuads(state, face, this.random)) {
                Sprite sprite = quad.getSprite();

                if (sprite == null || !BASE.equals(sprite.getContents().getId()))
                    continue;

                converted.add(remap(quad, sprite, emission));
            }

            any |= !converted.isEmpty();
            result.add(converted);
        }

        return any ? result : List.of();
    }

    private static BakedQuad remap(BakedQuad quad, Sprite from, Sprite to) {
        int[] data = Arrays.copyOf(quad.getVertexData(), quad.getVertexData().length);

        for (int vertex = 0; vertex < data.length / VERTEX_STRIDE; vertex++) {
            int base = vertex * VERTEX_STRIDE;
            float u = Float.intBitsToFloat(data[base + U_OFFSET]);
            float v = Float.intBitsToFloat(data[base + V_OFFSET]);

            data[base + U_OFFSET] = Float.floatToRawIntBits(to.getFrameU(from.getFrameFromU(u)));
            data[base + V_OFFSET] = Float.floatToRawIntBits(to.getFrameV(from.getFrameFromV(v)));
        }

        return new BakedQuad(data, quad.getColorIndex(), quad.getFace(), to, false);
    }

    private static Sprite emissionSprite() {
        SpriteAtlasTexture atlas = MinecraftClient.getInstance().getBakedModelManager().getAtlas(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE);
        return atlas.getSprite(EMISSION);
    }
}
