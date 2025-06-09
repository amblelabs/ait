package dev.amble.ait.core.bind;

import dev.amble.ait.AITMod;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

import java.util.function.Consumer;

public class KeyBind {

    protected final String name;
    protected final String category;

    protected final InputUtil.Type type;
    protected final int code;

    protected KeyBinding self;
    private final Consumer<MinecraftClient> consumer;

    public KeyBind(String name, String category, InputUtil.Type type, int code, Consumer<MinecraftClient> consumer) {
        this.name = name;
        this.category = category;

        this.type = type;
        this.code = code;

        this.consumer = consumer;
    }

    public void tick(MinecraftClient client) {
        if (this.shouldTrigger(client))
            this.trigger(client);
    }

    public void register() {
        this.self = KeyBindingHelper.registerKeyBinding(new KeyBinding("key." + AITMod.MOD_ID + "." + name, this.type,
                this.code, "category." + AITMod.MOD_ID + "." + category));
    }

    protected boolean shouldTrigger(MinecraftClient client) {
        return this.self.isPressed();
    }

    protected void trigger(MinecraftClient client) {
        this.consumer.accept(client);
    }

    public static class Held extends KeyBind {

        private boolean held;

        public Held(String name, String category, InputUtil.Type type, int code, Consumer<MinecraftClient> consumer) {
            super(name, category, type, code, consumer);
        }

        @Override
        protected boolean shouldTrigger(MinecraftClient client) {
            ClientPlayerEntity player = client.player;

            if (player == null)
                return false;

            if (!this.self.isPressed()) {
                this.held = false;
                return false;
            }

            if (this.held)
                return false;

            this.held = true;
            return true;
        }
    }
}
