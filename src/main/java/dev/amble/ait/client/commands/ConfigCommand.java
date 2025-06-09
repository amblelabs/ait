package dev.amble.ait.client.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import dev.amble.ait.AITMod;
import dev.amble.ait.client.config.AITConfigScreen;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;


public class ConfigCommand {
    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(literal(AITMod.MOD_ID + "-config").executes(context -> {
            MinecraftClient client = MinecraftClient.getInstance();
            client.send(() -> client.setScreen(AITConfigScreen.create(null)));
            return Command.SINGLE_SUCCESS;
        }));
    }
}
