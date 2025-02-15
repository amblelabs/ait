package dev.amble.ait.client.sonic;

import static dev.amble.ait.AITMod.LOGGER;

import java.util.Collection;

import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;

import net.minecraft.util.Identifier;

import dev.amble.ait.registry.impl.SonicRegistry;


// TODO: when finished, move to SonicRendering
public class SonicModels {



    public static void init() {
        ModelLoadingPlugin.register(context -> {
            Collection<Identifier> ids = SonicRegistry.getInstance().models();

            LOGGER.info("Loading {}", ids);
            context.addModels(ids.toArray(new Identifier[0]));
        });

        /*ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new IdentifiableResourceReloadListener() {
            @Override
            public Identifier getFabricId() {
                return ResourceReloadListenerKeys.MODELS;
            }

            @Override
            public CompletableFuture<Void> reload(Synchronizer synchronizer, ResourceManager manager, Profiler prepareProfiler, Profiler applyProfiler, Executor prepareExecutor, Executor applyExecutor) {
                return CompletableFuture.supplyAsync(() -> manager.findResources("models/sonic", identifier -> identifier.getPath().endsWith(".json")), prepareExecutor)
                        .thenCompose(map -> {
                            ArrayList<CompletableFuture<Void>> list = new ArrayList<>(map.size());
                            for (Map.Entry<Identifier, Resource> entry : map.entrySet()) {
                                list.add(CompletableFuture.runAsync(() -> {
                                    try (BufferedReader ignored = entry.getValue().getReader()) {
                                        LOGGER.info("found potential sonic: {}", entry.getKey());
                                        // Process the model as needed here
                                    } catch (Exception exception) {
                                        LOGGER.error("Failed to load model {}", entry.getKey(), exception);
                                    }
                                }, applyExecutor));
                            }
                            return CompletableFuture.allOf(list.toArray(new CompletableFuture[0]));
                        }).thenApply(v -> null);
            }

        });*/
    }
}
