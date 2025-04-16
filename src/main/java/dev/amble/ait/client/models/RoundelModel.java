package dev.amble.ait.client.models;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import com.mojang.datafixers.util.Pair;
import net.fabricmc.fabric.api.renderer.v1.Renderer;
import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.fabricmc.fabric.api.renderer.v1.material.RenderMaterial;
import net.fabricmc.fabric.api.renderer.v1.mesh.*;
import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
import net.fabricmc.fabric.api.renderer.v1.model.ModelHelper;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.fabricmc.fabric.api.util.TriState;
import org.jetbrains.annotations.Nullable;

import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.block.BlockModels;
import net.minecraft.client.render.model.*;
import net.minecraft.client.render.model.json.ModelOverrideList;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockRenderView;

import dev.amble.ait.AITMod;
import dev.amble.ait.core.blockentities.RoundelBlockEntity;
import dev.amble.ait.core.roundels.RoundelPattern;
import dev.amble.ait.core.roundels.RoundelPatterns;
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
    private Mesh mesh;

    public RoundelModel() {
        BLOCK_MODELS = MinecraftClient.getInstance().getBakedModelManager().getBlockModels();
    }

    @Override
    public void emitBlockQuads(BlockRenderView blockRenderView, BlockState blockState, BlockPos blockPos, Supplier<Random> supplier, RenderContext renderContext) {
        if (blockRenderView.getBlockEntity(blockPos) instanceof RoundelBlockEntity roundelBlockEntity) {
            if (roundelBlockEntity.getDynamicTextureBlockState() != null) {
                if (roundelBlockEntity.getDynamicTextureBlockState().getRenderType() != BlockRenderType.INVISIBLE) {
                    for (Pair<RoundelPattern, DyeColor> patterns : roundelBlockEntity.getPatterns()) {
                        if (patterns.getFirst().equals(RoundelPatterns.BASE)) {
                            int colorForBlock = patterns.getSecond().equals(DyeColor.WHITE) ? ColorHelper.Argb.getArgb(255, 255, 255, 255)
                            : ColorHelper.Argb.getArgb(255, (int) (255f * patterns.getSecond().getColorComponents()[0]), (int)
                                    (255f * patterns.getSecond().getColorComponents()[1]), (int) (255f * patterns.getSecond().getColorComponents()[2]));
                            RoundelModel.emitBlockQuads(
                                    BLOCK_MODELS.getModel(roundelBlockEntity.getDynamicTextureBlockState()),
                                    roundelBlockEntity.getDynamicTextureBlockState(),
                                    supplier,
                                    renderContext,
                                    renderContext.getEmitter(),
                                    colorForBlock);
                        }
                    }
                    /*BLOCK_MODELS.getModel(roundelBlockEntity.getDynamicTextureBlockState())
                            .emitBlockQuads(blockRenderView, roundelBlockEntity.getDynamicTextureBlockState(), blockPos, supplier, renderContext);*/
                }
                Renderer renderer = RendererAccess.INSTANCE.getRenderer();
                if (renderer == null) {
                    System.out.println("I returned null for some weird reason");
                    return;
                }
                MeshBuilder builder = renderer.meshBuilder();
                QuadEmitter emitter = builder.getEmitter();

                for (Pair<RoundelPattern, DyeColor> patterns : roundelBlockEntity.getPatterns()) {
                    if (patterns.getFirst().equals(RoundelPatterns.BASE)) {
                        continue;
                    }
                    for (Direction direction : Direction.values()) {
                        // UP and DOWN share the Y axis
                        int spriteIdx = direction == Direction.UP || direction == Direction.DOWN ? SPRITE_TOP : SPRITE_SIDE;
                        // Add a new face to the mesh
                        emitter.square(direction, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f);
                        // Set the sprite of the face, must be called after .square()
                        // We haven't specified any UV coordinates, so we want to use the whole texture. BAKE_LOCK_UV does exactly that.
                        Identifier idOf = AITMod.id(patterns.getFirst().texture().getPath()
                                .substring(9, patterns.getFirst().texture().getPath().length() - 4));
                        SpriteIdentifier spriteId = new SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, idOf);
                        //System.out.println(spriteId);
                        emitter.spriteBake(spriteId.getSprite(), MutableQuadView.BAKE_LOCK_UV);
                        // Enable texture usage
                        int colorOf = ColorHelper.Argb.getArgb(255, (int) (255f * patterns.getSecond().getColorComponents()[0]), (int)
                                (255f * patterns.getSecond().getColorComponents()[1]), (int) (255f * patterns.getSecond().getColorComponents()[2]));
                        emitter.color(colorOf, colorOf, colorOf, colorOf);
                        if (patterns.getFirst().emissive()) {
                            boolean bl = roundelBlockEntity.tardis() != null && roundelBlockEntity.tardis().get() != null &&
                                    roundelBlockEntity.tardis().get().fuel().hasPower();
                            int colorWithTardis = bl || !TardisServerWorld.isTardisDimension(roundelBlockEntity.getWorld()) ? 0xf000f0 : colorOf / 100;
                            emitter.lightmap(colorWithTardis, colorWithTardis, colorWithTardis, colorWithTardis);
                        }
                        // Add the quad to the mesh
                        emitter.emit();
                    }
                }
                builder.build().outputTo(renderContext.getEmitter());
            }
        }

        /*if (mesh != null) {
            mesh.outputTo(renderContext.getEmitter());
        }*/
    }

    private static final Renderer RENDERER = RendererAccess.INSTANCE.getRenderer();
    private static final RenderMaterial MATERIAL_STANDARD = RENDERER.materialFinder().find();
    private static final RenderMaterial MATERIAL_NO_AO = RENDERER.materialFinder().ambientOcclusion(TriState.FALSE).find();

    public static void emitBlockQuads(BakedModel model, @Nullable BlockState state, Supplier<Random> randomSupplier, RenderContext context, QuadEmitter emitter, int color) {
        //final RenderMaterial defaultMaterial = model.useAmbientOcclusion() ? MATERIAL_STANDARD : MATERIAL_NO_AO;

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
        System.out.println("AM I PRINTING");
        for(int i = 0; i < SPRITE_IDS.length; ++i) {
            sprites[i] = textureGetter.apply(SPRITE_IDS[i]);
        }
        // Build the mesh using the Renderer API
        Renderer renderer = RendererAccess.INSTANCE.getRenderer();
        if (renderer == null) {
            System.out.println("I returned null for some weird reason");
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
        mesh = builder.build();

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
    @Override
    public void emitItemQuads(ItemStack itemStack, Supplier<Random> randomSupplier, RenderContext renderContext) {
        /*if (itemStack.getItem() instanceof RoundelItem roundelItem) {
            if (RoundelItem.getBlockEntityNbt(itemStack) != null) {
                NbtCompound nbtCompound = itemStack.getOrCreateNbt();
                NbtList nbtList = nbtCompound.getList("Patterns", NbtElement.COMPOUND_TYPE);
                NbtCompound dynamicTex = nbtCompound.getCompound("DynamicTex");
                BlockState dynamicTexBlockState = NbtHelper.toBlockState(MinecraftClient.getInstance()
                                .world.createCommandRegistryWrapper(RegistryKeys.BLOCK),
                        dynamicTex);
                if (dynamicTexBlockState.getRenderType() != BlockRenderType.INVISIBLE) {
                    for (int i = 0; i < nbtList.size() && i < 6; ++i) {
                        NbtCompound nbtCompound2 = nbtList.getCompound(i);
                        DyeColor dyeColor = DyeColor.byId(nbtCompound2.getInt("Color"));
                        RoundelPattern roundel = RoundelPatterns.getInstance().get(Identifier.tryParse(nbtCompound2.getString("Pattern")));
                        if (roundel == null) continue;
                        if (roundel.equals(RoundelPatterns.BASE)) {
                            int colorForBlock = ColorHelper.Argb.getArgb(255, (int) (255f * dyeColor.getColorComponents()[0]), (int)
                                    (255f * dyeColor.getColorComponents()[1]), (int) (255f * dyeColor.getColorComponents()[2]));
                            RoundelModel.emitBlockQuads(
                                    BLOCK_MODELS.getModel(dynamicTexBlockState),
                                    dynamicTexBlockState,
                                    randomSupplier,
                                    renderContext,
                                    renderContext.getEmitter(),
                                    colorForBlock);
                        }
                    }
                    *//*BLOCK_MODELS.getModel(roundelBlockEntity.getDynamicTextureBlockState())
                            .emitBlockQuads(blockRenderView, roundelBlockEntity.getDynamicTextureBlockState(), blockPos, supplier, renderContext);*//*
                }
                Renderer renderer = RendererAccess.INSTANCE.getRenderer();
                if (renderer == null) {
                    System.out.println("I returned null for some weird reason");
                    return;
                }
                MeshBuilder builder = renderer.meshBuilder();
                QuadEmitter emitter = builder.getEmitter();

                for (int i = 0; i < nbtList.size() && i < 6; ++i) {
                    NbtCompound nbtCompound2 = nbtList.getCompound(i);
                    DyeColor dyeColor = DyeColor.byId(nbtCompound2.getInt("Color"));
                    RoundelPattern roundel = RoundelPatterns.getInstance().get(Identifier.tryParse(nbtCompound2.getString("Pattern")));
                    if (roundel == null) continue;
                    if (roundel.equals(RoundelPatterns.BASE)) {
                        continue;
                    }
                    for (Direction direction : Direction.values()) {
                        // UP and DOWN share the Y axis
                        int spriteIdx = direction == Direction.UP || direction == Direction.DOWN ? SPRITE_TOP : SPRITE_SIDE;
                        // Add a new face to the mesh
                        emitter.square(direction, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f);
                        // Set the sprite of the face, must be called after .square()
                        // We haven't specified any UV coordinates, so we want to use the whole texture. BAKE_LOCK_UV does exactly that.
                        Identifier idOf = AITMod.id(roundel.texture().getPath()
                                .substring(9, roundel.texture().getPath().length() - 4));
                        SpriteIdentifier spriteId = new SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, idOf);
                        //System.out.println(spriteId);
                        emitter.spriteBake(spriteId.getSprite(), MutableQuadView.BAKE_LOCK_UV);
                        // Enable texture usage
                        int colorOf = ColorHelper.Argb.getArgb(255, (int) (255f * dyeColor.getColorComponents()[0]), (int)
                                (255f * dyeColor.getColorComponents()[1]), (int) (255f * dyeColor.getColorComponents()[2]));
                        emitter.color(colorOf, colorOf, colorOf, colorOf);
                        if (roundel.emissive()) {
                            emitter.lightmap(colorOf, colorOf, colorOf, colorOf);
                        }
                        // Add the quad to the mesh
                        emitter.emit();
                    }
                }
                builder.build().outputTo(renderContext.getEmitter());
            }
        }*/

        mesh.outputTo(renderContext.getEmitter());
    }
}
