/*
 * Copyright (C) 2025 AmbleLabs
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * This code is MPL, due to it referencing this code: https://gitlab.com/cable-mc/cobblemon/-/blob/main/common/src/main/kotlin/com/cobblemon/mod/common/client/render/models/blockbench/bedrock/animation/BedrockAnimationRepository.kt
 */


package dev.amble.ait.core.tardis.animation.v2.bedrock;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.serialization.Codec;
import dev.amble.ait.AITMod;
import dev.amble.ait.core.tardis.manager.ServerTardisManager;
import dev.amble.lib.AmbleKit;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class BedrockAnimationRegistry implements SimpleSynchronousResourceReloadListener {
	private static final BedrockAnimationRegistry INSTANCE = new BedrockAnimationRegistry();

	private final Map<String, BedrockAnimation.Group> groups = new HashMap<>();

	public BedrockAnimationRegistry() {
		ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(this);
	}

	public record Reference(String fileName, String animationName) {
		public static Codec<Reference> CODEC = Identifier.CODEC.xmap(
				Reference::parse,
				ref -> Identifier.of(ref.fileName, ref.animationName)
		);

		public Optional<BedrockAnimation> get() {
			BedrockAnimation animation = BedrockAnimationRegistry.getInstance().get(this);
			return Optional.ofNullable(animation);
		}

		public static Reference parse(Identifier id) {
			return new Reference(id.getNamespace(), id.getPath());
		}
	}

	public BedrockAnimation get(String fileName, String animationName) {
		BedrockAnimation.Group group = groups.get(fileName);
		if (group == null) {
			return null;
		}
		return group.animations.get(animationName);
	}

	public BedrockAnimation get(Reference data) {
		return get(data.fileName, data.animationName);
	}

	@Override
	public Identifier getFabricId() {
		return AITMod.id("bedrock_animation");
	}

	@Override
	public void reload(ResourceManager manager) {
		int animationCount = 0;
		groups.clear();

		for (Identifier rawId : manager.findResources("bedrock", filename -> filename.getPath().endsWith(".animation.json")).keySet()) {
			try (InputStream stream = manager.getResource(rawId).get().getInputStream()) {
				JsonObject json = JsonParser.parseReader(new InputStreamReader(stream)).getAsJsonObject();
				BedrockAnimation.Group group = ServerTardisManager.getInstance().getFileGson().fromJson(json, BedrockAnimation.Group.class);

				group.animations.forEach((name, animation) -> animation.name = name);

				String groupName = rawId.getPath().substring(rawId.getPath().lastIndexOf("/") + 1).replace(".animation.json", "");
				groups.put(groupName, group);
				animationCount += group.animations.size();
			} catch (Exception e) {
				AmbleKit.LOGGER.error("Error occurred while loading resource json {}", rawId.toString(), e);
			}
		}

		AmbleKit.LOGGER.info("Loaded {} animations from {} groups", animationCount, groups.size());
	}

	public static BedrockAnimationRegistry getInstance() {
		return INSTANCE;
	}
}
