package dev.amble.ait.client.renderers;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import dev.amble.ait.AITMod;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.renderer.v1.Renderer;
import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.fabricmc.fabric.api.renderer.v1.material.BlendMode;
import net.fabricmc.fabric.api.renderer.v1.material.RenderMaterial;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.renderer.v1.model.ForwardingBakedModel;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.fabricmc.fabric.api.util.TriState;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;

@Environment(EnvType.CLIENT)
public final class EmissiveOverlayModel extends ForwardingBakedModel {

    private static final String INVENTORY = "inventory";
    private static final float NUDGE = 0.0005f;
    private static final Direction[] CULL_FACES = { null, Direction.DOWN, Direction.UP, Direction.NORTH, Direction.SOUTH,
            Direction.WEST, Direction.EAST };

    private static final Map<Identifier, Overlay> OVERLAYS = Map.of(
            AITMod.id("cable_block"), new Overlay(AITMod.id("block/piping"), AITMod.id("block/piping_emission")));

    private record Overlay(Identifier base, Identifier emission) {
    }

    private final Identifier base;
    private final Sprite emission;
    private final RenderMaterial material;

    private EmissiveOverlayModel(BakedModel wrapped, Identifier base, Sprite emission, RenderMaterial material) {
        this.wrapped = wrapped;
        this.base = base;
        this.emission = emission;
        this.material = material;
    }

    public static void init() {
        ModelLoadingPlugin.register(plugin -> plugin.modifyModelAfterBake().register((model, context) -> {
            if (model == null || !(context.id() instanceof ModelIdentifier id) || !INVENTORY.equals(id.getVariant()))
                return model;

            Overlay overlay = OVERLAYS.get(new Identifier(id.getNamespace(), id.getPath()));
            Renderer renderer = RendererAccess.INSTANCE.getRenderer();

            if (overlay == null || renderer == null)
                return model;

            Sprite emission = context.textureGetter().apply(new SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, overlay.emission()));
            RenderMaterial material = renderer.materialFinder()
                    .blendMode(BlendMode.CUTOUT)
                    .emissive(true)
                    .disableDiffuse(true)
                    .ambientOcclusion(TriState.FALSE)
                    .find();

            return new EmissiveOverlayModel(model, overlay.base(), emission, material);
        }));
    }

    @Override
    public boolean isVanillaAdapter() {
        return false;
    }

    @Override
    public void emitItemQuads(ItemStack stack, Supplier<Random> randomSupplier, RenderContext context) {
        super.emitItemQuads(stack, randomSupplier, context);
        this.emitOverlay(null, randomSupplier, context.getEmitter());
    }

    private void emitOverlay(@Nullable BlockState state, Supplier<Random> randomSupplier, QuadEmitter emitter) {
        Vector3f normal = new Vector3f();

        for (Direction face : CULL_FACES) {
            List<BakedQuad> quads = this.wrapped.getQuads(state, face, randomSupplier.get());

            for (BakedQuad quad : quads) {
                Sprite sprite = quad.getSprite();

                if (sprite == null || !this.base.equals(sprite.getContents().getId()))
                    continue;

                emitter.fromVanilla(quad, this.material, face);
                emitter.faceNormal().get(normal);
                normal.mul(NUDGE);

                for (int i = 0; i < 4; i++) {
                    emitter.pos(i, emitter.x(i) + normal.x, emitter.y(i) + normal.y, emitter.z(i) + normal.z);
                    emitter.uv(i, this.emission.getFrameU(sprite.getFrameFromU(emitter.u(i))),
                            this.emission.getFrameV(sprite.getFrameFromV(emitter.v(i))));
                }

                emitter.color(-1, -1, -1, -1);
                emitter.emit();
            }
        }
    }
}
