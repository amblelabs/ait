package dev.amble.ait.core.tardis.animation.v2.bedrock;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.amble.ait.AITMod;
import dev.amble.lib.AmbleKit;
import dev.amble.lib.register.datapack.DatapackRegistry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.apache.commons.lang3.NotImplementedException;

import java.io.InputStream;
import java.io.InputStreamReader;

@Environment(EnvType.CLIENT)
public class BedrockModelRegistry extends DatapackRegistry<BedrockModel> implements SimpleSynchronousResourceReloadListener {
	private static final BedrockModelRegistry INSTANCE = new BedrockModelRegistry();

	private BedrockModelRegistry() {
		ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(this);
	}

	@Override
	public BedrockModel fallback() {
		throw new NotImplementedException();
	}

	@Override
	public Identifier getFabricId() {
		return AITMod.id("bedrock_model");
	}

	@Override
	public void reload(ResourceManager manager) {
		clearCache();

		for (Identifier rawId : manager.findResources("bedrock", filename -> filename.getPath().endsWith("geo.json")).keySet()) {
			try (InputStream stream = manager.getResource(rawId).get().getInputStream()) {
				String path = rawId.getPath();
				// remove "bedrock/" prefix and ".geo.json" suffix
				String idPath = path.substring("bedrock/".length(), path.length() - ".geo.json".length());
				Identifier id = Identifier.of(rawId.getNamespace(), idPath);

				JsonObject json = JsonParser.parseReader(new InputStreamReader(stream)).getAsJsonObject();
				BedrockModel model = BedrockModel.from(json, id);

				register(model);

				AmbleKit.LOGGER.info("Loaded bedrock model {} {}", id, model);
			} catch (Exception e) {
				AmbleKit.LOGGER.error("Error occurred while loading resource json {}", rawId.toString(), e);
			}
		}
	}

	@Override
	public void syncToClient(ServerPlayerEntity player) {
		throw new UnsupportedOperationException("Client-side only registry");
	}

	@Override
	public void readFromServer(PacketByteBuf buf) {
		throw new UnsupportedOperationException("Client-side only registry");
	}

	public static BedrockModelRegistry getInstance() {
		return INSTANCE;
	}
}
