package dev.amble.ait.client.commands;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import me.shedaniel.autoconfig.AutoConfig;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;

import dev.amble.ait.AITMod;
import dev.amble.ait.config.AITConfig;


public class ConfigCommand {
    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(literal(AITMod.MOD_ID + "-config").executes(context -> {
            MinecraftClient client = MinecraftClient.getInstance();

            Screen screen = AutoConfig.getConfigScreen(AITConfig.class, client.currentScreen).get();
            client.send(() -> client.setScreen(screen));
            return Command.SINGLE_SUCCESS;
        }));
    }
}
