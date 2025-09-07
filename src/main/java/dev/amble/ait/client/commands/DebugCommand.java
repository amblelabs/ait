package dev.amble.ait.client.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import dev.amble.ait.AITMod;
import dev.amble.ait.registry.impl.door.ClientDoorRegistry;
import dev.amble.ait.registry.impl.door.DoorRegistry;
import dev.amble.ait.registry.impl.exterior.ClientExteriorVariantRegistry;
import dev.amble.ait.registry.impl.exterior.ExteriorVariantRegistry;
import dev.amble.lib.api.Identifiable;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class DebugCommand {
    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(literal(AITMod.MOD_ID + "-client").then(literal("debug").executes(context -> {

            context.getSource().sendFeedback(Text.literal("Door registry: " + stringify(DoorRegistry.getInstance().toList())));
            context.getSource().sendFeedback(Text.literal("Client Door registry: " + stringify(ClientDoorRegistry.getInstance().toList())));
            context.getSource().sendFeedback(Text.literal("Exterior registry: " + stringify(ExteriorVariantRegistry.getInstance().toList())));
            context.getSource().sendFeedback(Text.literal("Client Exterior registry: " + stringify(ClientExteriorVariantRegistry.getInstance().toList())));
            DoorRegistry.getInstance().toList();
            return Command.SINGLE_SUCCESS;
        })));
    }

    public static String stringify(List<? extends Identifiable> list) {
        return list.stream().map(idlike -> idlike == null ? null : idlike.id().toString()).toList().toString();
    }
}
