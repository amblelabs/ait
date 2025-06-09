package dev.amble.ait.api;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class ClientWorldEvents {
    public static final Event<ChangeWorld> CHANGE_WORLD = EventFactory.createArrayBacked(ChangeWorld.class,
            callbacks -> (client, world) -> {
                for (ChangeWorld callback : callbacks) {
                    callback.onChange(client, world);
                }
            });

    @FunctionalInterface
    public interface ChangeWorld {
        void onChange(MinecraftClient client, @Nullable ClientWorld world);
    }
}
