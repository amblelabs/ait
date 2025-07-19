package dev.amble.ait.client.models.json;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.*;
import com.mojang.datafixers.util.Either;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.render.model.Baker;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.client.render.model.json.ModelElement;
import net.minecraft.client.render.model.json.ModelOverride;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.Direction;

public class JsonEntityModel {

    static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(JsonEntityModel.class, new JsonEntityModel.Deserializer())
            /*.registerTypeAdapter(ModelElement.class, new ModelElement.Deserializer())
            .registerTypeAdapter(ModelElementFace.class, new ModelElementFace.Deserializer())
            .registerTypeAdapter(ModelElementTexture.class, new ModelElementTexture.Deserializer())
            .registerTypeAdapter(Transformation.class, new Transformation.Deserializer())
            .registerTypeAdapter(ModelTransformation.class, new ModelTransformation.Deserializer())
            .registerTypeAdapter(ModelOverride.class, new ModelOverride.Deserializer())*/
            .create();

    private final List<ModelElement> elements;

    @Nullable private final JsonUnbakedModel.GuiLight guiLight;
    @Nullable private final Boolean ambientOcclusion;

    private final ModelTransformation transformations;
    private final List<ModelOverride> overrides;
    public String id = "";

    protected final Map<String, Either<SpriteIdentifier, String>> textureMap;

    @Nullable protected JsonEntityModel parent;
    @Nullable protected Identifier parentId;

    public JsonEntityModel(@Nullable Identifier parentId, List<ModelElement> elements, Map<String, Either<SpriteIdentifier, String>> textureMap, @Nullable Boolean ambientOcclusion, @Nullable JsonUnbakedModel.GuiLight guiLight, ModelTransformation transformations, List<ModelOverride> overrides) {
        this.elements = elements;
        this.ambientOcclusion = ambientOcclusion;
        this.guiLight = guiLight;
        this.textureMap = textureMap;
        this.parentId = parentId;
        this.transformations = transformations;
        this.overrides = overrides;
    }

    public JsonEntityModel getRootModel() {
        return this.parent == null ? this : this.parent.getRootModel();
    }

    public List<ModelElement> getElements() {
        if (this.elements.isEmpty() && this.parent != null) {
            return this.parent.getElements();
        }
        return this.elements;
    }

    public JsonUnbakedModel.GuiLight getGuiLight() {
        if (this.guiLight != null) {
            return this.guiLight;
        }
        if (this.parent != null) {
            return this.parent.getGuiLight();
        }
        return JsonUnbakedModel.GuiLight.BLOCK;
    }

    public void bake(Baker baker, JsonUnbakedModel parent, Function<SpriteIdentifier, Sprite> textureGetter, ModelBakeSettings settings, Identifier id, boolean hasDepth) {
        /*ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();

        Sprite sprite = textureGetter.apply(this.resolveSprite(PARTICLE_KEY));
        BasicBakedModel.Builder builder = new BasicBakedModel.Builder(this, this.compileOverrides(baker, parent), hasDepth).setParticle(sprite);

        int counter = 0;
        int face = 0;
        for (ModelElement modelElement : this.elements) {


            ModelTransform transform = getModelTransform(modelElement);




            for (Direction direction : modelElement.faces.keySet()) {
                ModelElementFace modelElementFace = modelElement.faces.get(direction);
                modelElementFace.textureData;

                modelPartData.addChild(counter + "_" + face, ModelPartBuilder.create().cuboid(
                                modelElement.from.x, modelElement.from.y, modelElement.from.z,
                                modelElement.to.x, modelElement.to.y, modelElement.to.z, modelElement.faces.keySet()
                        ).uv(modelElementFace.textureData.getU(0), modelElementFace.textureData.getU(1)), transform
                );

                Sprite sprite2 = textureGetter.apply(this.resolveSprite(modelElementFace.textureId));
                if (modelElementFace.cullFace == null) {
                    builder.addQuad(JsonUnbakedModel.createQuad(modelElement, modelElementFace, sprite2, direction, settings, id));
                    continue;
                }
                builder.addQuad(Direction.transform(settings.getRotation().getMatrix(), modelElementFace.cullFace), JsonUnbakedModel.createQuad(modelElement, modelElementFace, sprite2, direction, settings, id));


                face++;
            }

            face = 0;
            counter++;
        }*/
    }

    private static @NotNull ModelTransform getModelTransform(ModelElement modelElement) {
        Vector3f origin = modelElement.rotation.origin();
        float angle = modelElement.rotation.angle();
        Direction.Axis axis = modelElement.rotation.axis();

        ModelTransform transform = switch (axis) {
            case X -> ModelTransform.of(origin.x, origin.y, origin.z, angle, 0, 0);
            case Y -> ModelTransform.of(origin.x, origin.y, origin.z, 0, angle, 0);
            case Z -> ModelTransform.of(origin.x, origin.y, origin.z, 0, 0, angle);
        };
        return transform;
    }

    @Environment(EnvType.CLIENT)
    public static class Deserializer
            implements JsonDeserializer<JsonEntityModel> {
        @Override
        public JsonEntityModel deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            List<ModelElement> list = this.elementsFromJson(context, jsonObject);
            String string = this.parentFromJson(jsonObject);
            Map<String, Either<SpriteIdentifier, String>> map = this.texturesFromJson(jsonObject);
            Boolean boolean_ = this.ambientOcclusionFromJson(jsonObject);
            ModelTransformation modelTransformation = ModelTransformation.NONE;
            if (jsonObject.has("display")) {
                JsonObject jsonObject2 = JsonHelper.getObject(jsonObject, "display");
                modelTransformation = context.deserialize(jsonObject2, ModelTransformation.class);
            }
            List<ModelOverride> list2 = this.overridesFromJson(context, jsonObject);
            JsonUnbakedModel.GuiLight guiLight = null;
            if (jsonObject.has("gui_light")) {
                guiLight = JsonUnbakedModel.GuiLight.byName(JsonHelper.getString(jsonObject, "gui_light"));
            }
            Identifier identifier = string.isEmpty() ? null : new Identifier(string);
            return new JsonEntityModel(identifier, list, map, boolean_, guiLight, modelTransformation, list2);
        }

        protected List<ModelOverride> overridesFromJson(JsonDeserializationContext context, JsonObject object) {
            ArrayList<ModelOverride> list = Lists.newArrayList();
            if (object.has("overrides")) {
                JsonArray jsonArray = JsonHelper.getArray(object, "overrides");
                for (JsonElement jsonElement : jsonArray) {
                    list.add(context.deserialize(jsonElement, ModelOverride.class));
                }
            }
            return list;
        }

        private Map<String, Either<SpriteIdentifier, String>> texturesFromJson(JsonObject object) {
            Identifier identifier = SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE;
            HashMap<String, Either<SpriteIdentifier, String>> map = Maps.newHashMap();
            if (object.has("textures")) {
                JsonObject jsonObject = JsonHelper.getObject(object, "textures");
                for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
                    map.put(entry.getKey(), Deserializer.resolveReference(identifier, entry.getValue().getAsString()));
                }
            }
            return map;
        }

        private static Either<SpriteIdentifier, String> resolveReference(Identifier id, String name) {
            if (name.charAt(0) == '#')
                return Either.right(name.substring(1));

            Identifier identifier = Identifier.tryParse(name);

            if (identifier == null)
                throw new JsonParseException(name + " is not valid resource location");

            return Either.left(new SpriteIdentifier(id, identifier));
        }

        private String parentFromJson(JsonObject json) {
            return JsonHelper.getString(json, "parent", "");
        }

        @Nullable protected Boolean ambientOcclusionFromJson(JsonObject json) {
            if (json.has("ambientocclusion")) {
                return JsonHelper.getBoolean(json, "ambientocclusion");
            }
            return null;
        }

        protected List<ModelElement> elementsFromJson(JsonDeserializationContext context, JsonObject json) {
            ArrayList<ModelElement> list = Lists.newArrayList();
            if (json.has("elements")) {
                for (JsonElement jsonElement : JsonHelper.getArray(json, "elements")) {
                    list.add(context.deserialize(jsonElement, ModelElement.class));
                }
            }
            return list;
        }
    }
}
