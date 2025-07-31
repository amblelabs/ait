/*
 * Copyright (C) 2025 AmbleLabs
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * This code is MPL, due to it referencing this code: https://gitlab.com/cable-mc/cobblemon/-/blob/main/common/src/main/kotlin/com/cobblemon/mod/common/client/render/models/blockbench/TexturedModel.kt
 */

package dev.amble.ait.core.tardis.animation.v2.bedrock;

import com.google.gson.*;
import com.google.gson.annotations.SerializedName;
import dev.amble.ait.core.tardis.manager.ServerTardisManager;
import dev.amble.lib.api.Identifiable;
import net.minecraft.client.model.*;
import net.minecraft.util.Identifier;

import java.lang.reflect.Type;
import java.util.*;

public class BedrockModel implements Identifiable {
	@SerializedName("format_version")
	public String version;
	@SerializedName("minecraft:geometry")
	public List<Geometry> geometry;
	private transient Identifier id;

	public static BedrockModel from(JsonObject json, Identifier id) {
		Gson gson = ServerTardisManager.getInstance().getFileGson();

		BedrockModel model = gson.fromJson(json, BedrockModel.class);

		model.id = id;

		return model;
	}

	public TexturedModelData create() {
		ModelData modelData = new ModelData();
		Map<String, ModelPartData> parts = new HashMap<>();
		Map<String, Bone> bones = new HashMap<>();

		try {
			BedrockModel.Geometry geometry = this.geometry.get(0);
			List<BedrockModel.Bone> geometryBones = new ArrayList<>(geometry.bones);

			// Add locator bones and root locator
			for (BedrockModel.Bone bone : geometryBones) {
				if (bone.locators != null) {
					for (Map.Entry<String, BedrockModel.LocatorBone> entry : bone.locators.entrySet()) {
						Bone locatorBone = Bone.empty();
						locatorBone.name = "internal_locator__" + entry.getKey();
						locatorBone.parent = bone.name;
						locatorBone.pivot = entry.getValue().offset;
						locatorBone.rotation = entry.getValue().rotation;
						geometryBones.add(locatorBone);
					}
				}
			}
			Bone rootLocator = Bone.empty();
			rootLocator.name = "internal_locator__" + "root";
			geometryBones.add(rootLocator);

			for (Bone bone : geometryBones) {
				bones.put(bone.name, bone);
				ModelPartData parentPart = (bone.parent != null) ? parts.get(bone.parent) : modelData.getRoot();

				List<Float> boneRotation = bone.rotation;
				ModelTransform modelTransform;
				if (bone.parent == null) {
					modelTransform = ModelTransform.pivot(0F, 0F, 0F);
				} else if (boneRotation != null) {
					modelTransform = ModelTransform.of(
							-(bones.get(bone.parent).pivot.get(0) - bone.pivot.get(0)),
							bones.get(bone.parent).pivot.get(1) - bone.pivot.get(1),
							-(bones.get(bone.parent).pivot.get(2) - bone.pivot.get(2)),
							(float) Math.toRadians(boneRotation.get(0)),
							(float) Math.toRadians(boneRotation.get(1)),
							(float) Math.toRadians(boneRotation.get(2))
					);
				} else {
					modelTransform = ModelTransform.pivot(
							-(bones.get(bone.parent).pivot.get(0) - bone.pivot.get(0)),
							bones.get(bone.parent).pivot.get(1) - bone.pivot.get(1),
							-(bones.get(bone.parent).pivot.get(2) - bone.pivot.get(2))
					);
				}

				ModelPartBuilder modelPart = ModelPartBuilder.create();
				List<ModelPartBuilder> subParts = new ArrayList<>();
				List<ModelTransform> modelTransforms = new ArrayList<>();

				List<BedrockModel.Cube> boneCubes = bone.cubes;
				if (boneCubes != null) {
					for (BedrockModel.Cube cube : boneCubes) {
						ModelPartBuilder subPart = (cube.rotation != null) ? ModelPartBuilder.create() : modelPart;
						List<Float> pivot = (cube.pivot != null) ? cube.pivot : bone.pivot;

						if (cube.uv != null) {
							subPart.uv(
									cube.uv.get(0),
									cube.uv.get(1)
							);
						}
						if (cube.mirror) {
							subPart.mirrored();
						}
						if (cube.size != null && cube.origin != null) {
							subPart.cuboid(
									cube.origin.get(0) - pivot.get(0),
									-(cube.origin.get(1) - pivot.get(1) + cube.size.get(1)),
									cube.origin.get(2) - pivot.get(2),
									cube.size.get(0),
									cube.size.get(1),
									cube.size.get(2),
									new Dilation(cube.inflate)
							);
						}
						if (cube.mirror) {
							subPart.mirrored(false);
						}

						if (subPart != modelPart) {
							modelTransforms.add(ModelTransform.of(
									-(bone.pivot.get(0) - cube.pivot.get(0)),
									bone.pivot.get(1) - cube.pivot.get(1),
									-(bone.pivot.get(2) - cube.pivot.get(2)),
									(float) Math.toRadians(cube.rotation.get(0)),
									(float) Math.toRadians(cube.rotation.get(1)),
									(float) Math.toRadians(cube.rotation.get(2))
							));
							subParts.add(subPart);
						}
					}
				}

				parts.put(bone.name, parentPart.addChild(
						bone.name,
						modelPart,
						modelTransform
				));

				int counter = 0;
				for (int index = 0; index < subParts.size(); index++) {
					parts.get(bone.name).addChild(
							bone.name + (counter++),
							subParts.get(index),
							modelTransforms.get(index)
					);
				}
			}

			return TexturedModelData.of(
					modelData,
					geometry.description.textureWidth,
					geometry.description.textureHeight
			);
		} catch (Exception e) {
			if (geometry != null && !geometry.isEmpty()) {
				throw new IllegalArgumentException("Error creating LayerDefinition with identifier " + geometry.get(0).description.identifier, e);
			} else {
				throw new IllegalArgumentException("Error creating LayerDefinition", e);
			}
		}
	}

	@Override
	public String toString() {
		return "BedrockModel{" +
				"version='" + version + '\'' +
				", geometry=" + geometry +
				'}';
	}

	@Override
	public Identifier id() {
		if (this.id == null) {
			throw new IllegalStateException("Model identifier is not set.");
		}

		return this.id;
	}

	public static class Geometry {
		public Description description;
		public List<Bone> bones;

		@Override
		public String toString() {
			return "Geometry{" +
					"description=" + description +
					", bones=" + bones +
					'}';
		}
	}

	public static class Description {
		public String identifier;
		@SerializedName("texture_width")
		public int textureWidth;
		@SerializedName("texture_height")
		public int textureHeight;
		@SerializedName("visible_bounds_width")
		public float visibleBoundsWidth;
		@SerializedName("visible_bounds_height")
		public float visibleBoundsHeight;
		@SerializedName("visible_bounds_offset")
		public List<Float> visibleBoundsOffset;

		@Override
		public String toString() {
			return "Description{" +
					"identifier='" + identifier + '\'' +
					", textureWidth=" + textureWidth +
					", textureHeight=" + textureHeight +
					", visibleBoundsWidth=" + visibleBoundsWidth +
					", visibleBoundsHeight=" + visibleBoundsHeight +
					", visibleBoundsOffset=" + visibleBoundsOffset +
					'}';
		}
	}

	public static class Bone {
		public String name;
		public String parent;
		public List<Float> pivot;
		public List<Float> rotation;
		public List<Cube> cubes;
		public Map<String, LocatorBone> locators;

		public Bone() {
			this("", null, List.of(), null, null, null);
		}

		public Bone(String name, String parent, List<Float> pivot, List<Float> rotation, List<Cube> cubes, Map<String, LocatorBone> locators) {
			this.name = name;
			this.parent = parent;
			this.pivot = pivot;
			this.rotation = rotation;
			this.cubes = cubes;
			this.locators = locators;
		}

		@Override
		public String toString() {
			return "Bone{" +
					"name='" + name + '\'' +
					", parent='" + parent + '\'' +
					", pivot=" + pivot +
					", rotation=" + rotation +
					", cubes=" + cubes +
					", locators=" + locators +
					'}';
		}

		public static Bone empty() {
			return new Bone();
		}
	}

	public record Cube(
			List<Float> origin,
			List<Float> size,
			List<Float> pivot,
			List<Float> rotation,
			List<Integer> uv,
			float inflate,
			boolean mirror) {
		@Override
		public String toString() {
			return "Cube{" +
					"origin=" + origin +
					", size=" + size +
					", pivot=" + pivot +
					", rotation=" + rotation +
					", uv=" + uv +
					", inflate=" + inflate +
					", mirror=" + mirror +
					'}';
		}
	}

	public static class LocatorBone {
		public List<Float> offset = List.of(0F, 0F, 0F);
		public List<Float> rotation = List.of(0F, 0F, 0F);

		@Override
		public String toString() {
			return "LocatorBone{" +
					"offset=" + offset +
					", rotation=" + rotation +
					'}';
		}

		public static class Adapter implements JsonDeserializer<LocatorBone> {
			@Override
			public LocatorBone deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
				List<Float> offset;
				List<Float> rotation;

				if (json.isJsonArray()) {
					JsonArray arr = json.getAsJsonArray();
					offset = Arrays.asList(arr.get(0).getAsFloat(), arr.get(1).getAsFloat(), arr.get(2).getAsFloat());
					rotation = Arrays.asList(0F, 0F, 0F);
				} else {
					JsonObject obj = json.getAsJsonObject();
					JsonArray offsetArr = obj.has("offset") ? obj.getAsJsonArray("offset") : null;
					JsonArray rotationArr = obj.has("rotation") ? obj.getAsJsonArray("rotation") : null;

					offset = offsetArr != null
							? toFloatList(offsetArr)
							: Arrays.asList(0F, 0F, 0F);
					rotation = rotationArr != null
							? toFloatList(rotationArr)
							: Arrays.asList(0F, 0F, 0F);
				}
				LocatorBone bone = new LocatorBone();
				bone.offset = offset;
				bone.rotation = rotation;
				return bone;
			}

			public static List<Float> toFloatList(JsonArray arr) {
				return Arrays.asList(
						arr.get(0).getAsFloat(),
						arr.get(1).getAsFloat(),
						arr.get(2).getAsFloat()
				);
			}
		}
	}
}
