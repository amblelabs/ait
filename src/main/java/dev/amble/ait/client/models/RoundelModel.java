package dev.amble.ait.client.models;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import net.fabricmc.fabric.api.renderer.v1.Renderer;
import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.fabricmc.fabric.api.renderer.v1.material.BlendMode;
import net.fabricmc.fabric.api.renderer.v1.mesh.*;
import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
import net.fabricmc.fabric.api.renderer.v1.model.ModelHelper;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import org.jetbrains.annotations.Nullable;

import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.block.BlockModels;
import net.minecraft.client.render.model.*;
import net.minecraft.client.render.model.json.ModelOverrideList;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.registry.Registries;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockRenderView;

import dev.amble.ait.AITMod;
import dev.amble.ait.core.AITBlocks;
import dev.amble.ait.core.blockentities.RoundelBlockEntity;
import dev.amble.ait.core.blocks.AbstractRoundelBlock;
import dev.amble.ait.core.item.RoundelItem;
import dev.amble.ait.core.roundels.RoundelPattern;
import dev.amble.ait.core.roundels.RoundelPatterns;
import dev.amble.ait.core.roundels.RoundelType;
import dev.amble.ait.core.world.TardisServerWorld;

public class RoundelModel implements UnbakedModel, BakedModel, FabricBakedModel {
    private static final SpriteIdentifier[] SPRITE_IDS = new SpriteIdentifier[]{
            new SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, new Identifier("block/white_concrete")),
            new SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, new Identifier("block/furnace_top"))
    };
    private final Sprite[] sprites = new Sprite[SPRITE_IDS.length];
    private static final int SPRITE_SIDE = 0;
    private static final int SPRITE_TOP = 1;
    private final BlockModels BLOCK_MODELS;

    public RoundelModel() {
        BLOCK_MODELS = MinecraftClient.getInstance().getBakedModelManager().getBlockModels();
    }

    @Override
    public void emitBlockQuads(BlockRenderView blockRenderView, BlockState blockState, BlockPos blockPos, Supplier<Random> supplier, RenderContext renderContext) {
        if (blockRenderView.getBlockEntity(blockPos) instanceof RoundelBlockEntity roundelBlockEntity) {
            if (roundelBlockEntity.getDynamicTextureBlockState() != null) {
                if (roundelBlockEntity.getDynamicTextureBlockState().getRenderType() != BlockRenderType.INVISIBLE) {
                    for (RoundelType patterns : roundelBlockEntity.getPatterns()) {
                        if (patterns.pattern().equals(RoundelPatterns.BASE)) {
                            int colorForBlock = patterns.color() == DyeColor.WHITE.getSignColor() ? ColorHelper.Argb.getArgb(255, 255, 255, 255)
                            : patterns.color();
                            RoundelModel.emitBlockQuads(
                                    BLOCK_MODELS.getModel(roundelBlockEntity.getDynamicTextureBlockState()),
                                    roundelBlockEntity.getDynamicTextureBlockState(),
                                    supplier,
                                    renderContext,
                                    renderContext.getEmitter(),
                                    colorForBlock);
                        }
                    }
                }
                Renderer renderer = RendererAccess.INSTANCE.getRenderer();
                if (renderer == null) {
                    AITMod.LOGGER.warn("I returned null for some weird reason");
                    return;
                }
                MeshBuilder builder = renderer.meshBuilder();
                QuadEmitter emitter = builder.getEmitter();

                for (RoundelType patterns : roundelBlockEntity.getPatterns()) {
                    if (patterns.pattern().equals(RoundelPatterns.BASE)) {
                        continue;
                    }
                    for (Direction direction : Direction.values()) {
                        emitter.square(direction, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f);
                        Identifier idOf = new Identifier(patterns.pattern().texture().getNamespace(), patterns.pattern().texture().getPath()
                                .substring(9, patterns.pattern().texture().getPath().length() - 4));
                        SpriteIdentifier spriteId = new SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, idOf);
                        emitter.spriteBake(spriteId.getSprite(), MutableQuadView.BAKE_LOCK_UV);
                        int colorOf = patterns.color();
                        emitter.color(colorOf, colorOf, colorOf, colorOf);
                        if (patterns.emissive()) {
                            boolean bl = roundelBlockEntity.tardis() != null && roundelBlockEntity.tardis().get() != null &&
                                    roundelBlockEntity.tardis().get().fuel().hasPower();
                            int colorWithTardis = bl || !TardisServerWorld.isTardisDimension(roundelBlockEntity.getWorld()) ? 0xf000f0 : colorOf / 100;
                            emitter.lightmap(colorWithTardis, colorWithTardis, colorWithTardis, colorWithTardis);
                        }
                        emitter.material(RENDERER.materialFinder().blendMode(BlendMode.TRANSLUCENT).find());
                        emitter.emit();
                    }
                }
                builder.build().outputTo(renderContext.getEmitter());
            }
        }
    }

    private static final Renderer RENDERER = RendererAccess.INSTANCE.getRenderer();

    public static void emitBlockQuads(BakedModel model, @Nullable BlockState state, Supplier<Random> randomSupplier, RenderContext context, QuadEmitter emitter, int color) {
        for (int i = 0; i <= ModelHelper.NULL_FACE_ID; i++) {
            final Direction cullFace = ModelHelper.faceFromIndex(i);

            if (!context.hasTransform() && context.isFaceCulled(cullFace)) {
                continue;
            }

            final List<BakedQuad> quads = model.getQuads(state, cullFace, randomSupplier.get());

            for (final BakedQuad q : quads) {
                emitter.fromVanilla(q, RENDERER.materialFinder().disableColorIndex(true).find(), cullFace).color(color, color, color, color);
                emitter.emit();
            }
        }
    }

    @Override
    public boolean isVanillaAdapter() {
        return false; // False to trigger FabricBakedModel rendering
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction face, Random random) {
        return List.of();
    }

    @Override
    public boolean useAmbientOcclusion() {
        return true;
    }

    @Override
    public boolean hasDepth() {
        return false;
    }

    @Override
    public boolean isBuiltin() {
        return false;
    }

    @Override
    public Sprite getParticleSprite() {
        return MinecraftClient.getInstance().getPaintingManager().getBackSprite();
    }

    @Override
    public Collection<Identifier> getModelDependencies() {
        return Collections.emptyList();
    }

    @Override
    public void setParents(Function<Identifier, UnbakedModel> modelLoader) {

    }

    @Override
    public BakedModel bake(Baker baker, Function<SpriteIdentifier, Sprite> textureGetter, ModelBakeSettings rotationContainer, Identifier modelId) {
        // Get the sprites
        for(int i = 0; i < SPRITE_IDS.length; ++i) {
            sprites[i] = textureGetter.apply(SPRITE_IDS[i]);
        }
        // Build the mesh using the Renderer API
        Renderer renderer = RendererAccess.INSTANCE.getRenderer();
        if (renderer == null) {
            AITMod.LOGGER.warn("I returned null for some weird reason");
            return null;
        }
        MeshBuilder builder = renderer.meshBuilder();
        QuadEmitter emitter = builder.getEmitter();

        for(Direction direction : Direction.values()) {
            // UP and DOWN share the Y axis
            int spriteIdx = direction == Direction.UP || direction == Direction.DOWN ? SPRITE_TOP : SPRITE_SIDE;
            // Add a new face to the mesh
            emitter.square(direction, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f);
            // Set the sprite of the face, must be called after .square()
            // We haven't specified any UV coordinates, so we want to use the whole texture. BAKE_LOCK_UV does exactly that.
            emitter.spriteBake(sprites[spriteIdx], MutableQuadView.BAKE_LOCK_UV);
            // Enable texture usage
            emitter.color(-1, -1, -1, -1);
            // Add the quad to the mesh
            emitter.emit();
        }

        return this;
    }

    @Override
    public ModelTransformation getTransformation() {
        return ModelHelper.MODEL_TRANSFORM_BLOCK;
    }

    @Override
    public ModelOverrideList getOverrides() {
        return ModelOverrideList.EMPTY;
    }

    // We will also implement this method to have the correct lighting in the item rendering. Try to set this to false and you will see the difference.
    @Override
    public boolean isSideLit() {
        return true;
    }

    // Finally, we can implement the item render function
    private final RoundelBlockEntity renderableRoundel = new RoundelBlockEntity(BlockPos.ORIGIN, AITBlocks.ROUNDEL.getDefaultState());
    @Override
    public void emitItemQuads(ItemStack itemStack, Supplier<Random> randomSupplier, RenderContext renderContext) {

        if (itemStack.getItem() instanceof RoundelItem roundelItem) {

            if (!(roundelItem.getBlock() instanceof AbstractRoundelBlock roundelBlock)) return;
            this.renderableRoundel.readFrom(itemStack, roundelBlock.getColor());
            NbtCompound nbt = this.renderableRoundel.createNbt();
            if(BlockItem.getBlockEntityNbt(itemStack) != null) nbt.copyFrom(BlockItem.getBlockEntityNbt(itemStack));
            if (nbt.contains("DynamicTex")) {
                this.renderableRoundel.setDynamicTex(NbtHelper.toBlockState(Registries.BLOCK.getReadOnlyWrapper(), nbt.getCompound("DynamicTex")));
            } else {
                this.renderableRoundel.setDynamicTex(Blocks.WHITE_CONCRETE.getDefaultState());
            }
            List<RoundelType> list = RoundelBlockEntity.getPatternsFromNbt(((RoundelItem) itemStack.getItem()).getColor(), RoundelBlockEntity.getPatternListNbt(itemStack));
            BlockState dynamicTexBlockState = this.renderableRoundel.getDynamicTextureBlockState();
            for (int i = 0; i < list.size() && i < 6; ++i) {
                RoundelType pair = this.renderableRoundel.getPatterns().get(i);
                RoundelPattern roundel = pair.pattern();
                if (roundel == null) continue;
                if (roundel.equals(RoundelPatterns.BASE)) {
                    int colorForBlock = pair.color();
                    RoundelModel.emitItemQuads(BLOCK_MODELS.getModel(dynamicTexBlockState), colorForBlock, dynamicTexBlockState, randomSupplier, renderContext);
                } else {
                    BLOCK_MODELS.getModel(dynamicTexBlockState)
                            .emitItemQuads(itemStack, randomSupplier, renderContext);
                }
            }

            Renderer renderer = RendererAccess.INSTANCE.getRenderer();
            if (renderer == null) {
                AITMod.LOGGER.warn("I returned null for some weird reason");
                return;
            }
            MeshBuilder builder = renderer.meshBuilder();
            QuadEmitter emitter = builder.getEmitter();
            List<BakedQuad> bakedQuadList = new ArrayList<>();
            for (int i = 0; i < this.renderableRoundel.getPatterns().size() && i < 7; ++i) {
                RoundelType pair = this.renderableRoundel.getPatterns().get(i);
                RoundelPattern roundel = pair.pattern();
                Identifier idOf = new Identifier(roundel.texture().getNamespace(), roundel.texture().getPath()
                        .substring(9, roundel.texture().getPath().length() - 4));
                SpriteIdentifier spriteId = new SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, idOf);
                bakedQuadList.add(emitter.toBakedQuad(spriteId.getSprite()));
            }

            for (BakedQuad quad : bakedQuadList) {
                builder.getEmitter().fromVanilla(quad.getVertexData(), 0);
            }

            builder.build().outputTo(builder.getEmitter());
        }
    }

    public static void emitItemQuads(BakedModel model, int color, @Nullable BlockState state, Supplier<Random> randomSupplier, RenderContext context) {
        QuadEmitter emitter = context.getEmitter();

        for (int i = 0; i <= ModelHelper.NULL_FACE_ID; i++) {
            final Direction cullFace = ModelHelper.faceFromIndex(i);
            final List<BakedQuad> quads = model.getQuads(state, cullFace, randomSupplier.get());
            final int count = quads.size();

            for (final BakedQuad q : quads) {
                emitter.fromVanilla(q, RENDERER.materialFinder().disableColorIndex(true).find(), cullFace).color(color, color, color, color);
                emitter.emit();
            }
        }
    }
}
