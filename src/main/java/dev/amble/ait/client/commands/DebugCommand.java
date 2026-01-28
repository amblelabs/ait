package dev.amble.ait.client.commands;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

import java.util.List;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import dev.amble.ait.client.boti.ProxyWorldManager;
import dev.amble.ait.client.boti.TardisDoorBOTI;
import dev.amble.ait.client.boti.TardisExteriorBOTI;
import dev.amble.lib.api.Identifiable;
import dev.loqor.client.ProxyClientWorld;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

import net.minecraft.text.Text;

import dev.amble.ait.AITMod;
import dev.amble.ait.registry.impl.door.ClientDoorRegistry;
import dev.amble.ait.registry.impl.door.DoorRegistry;
import dev.amble.ait.registry.impl.exterior.ClientExteriorVariantRegistry;
import dev.amble.ait.registry.impl.exterior.ExteriorVariantRegistry;

public class DebugCommand {
    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(literal(AITMod.MOD_ID + "-client").then(literal("debug").executes(context -> {

            context.getSource().sendFeedback(Text.literal("Door registry: " + stringify(DoorRegistry.getInstance().toList())));
            context.getSource().sendFeedback(Text.literal("Client Door registry: " + stringify(ClientDoorRegistry.getInstance().toList())));
            context.getSource().sendFeedback(Text.literal("Exterior registry: " + stringify(ExteriorVariantRegistry.getInstance().toList())));
            context.getSource().sendFeedback(Text.literal("Client Exterior registry: " + stringify(ClientExteriorVariantRegistry.getInstance().toList())));
            DoorRegistry.getInstance().toList();
            return Command.SINGLE_SUCCESS;
        })).then(literal("boti").executes(context -> {
            // BOTI Debug Information
            context.getSource().sendFeedback(Text.literal("=== BOTI Debug Information ==="));
            
            // ProxyWorldManager stats
            ProxyWorldManager manager = ProxyWorldManager.getInstance();
            context.getSource().sendFeedback(Text.literal("Active Proxy Worlds: " + manager.getActiveWorldCount()));
            
            // Individual proxy world stats
            for (var entry : manager.getAllProxyWorlds().entrySet()) {
                ProxyClientWorld world = entry.getValue();
                context.getSource().sendFeedback(Text.literal("  Dimension: " + entry.getKey().getValue()));
                context.getSource().sendFeedback(Text.literal("    Cached Chunks: " + world.getCachedChunkCount()));
                context.getSource().sendFeedback(Text.literal("    Pending Requests: " + world.getPendingRequestCount()));
            }
            
            context.getSource().sendFeedback(Text.literal("=== End BOTI Debug ==="));
            return Command.SINGLE_SUCCESS;
        })).then(literal("boti-clear").executes(context -> {
            // Clear all BOTI caches
            context.getSource().sendFeedback(Text.literal("Clearing all BOTI caches..."));
            
            ProxyWorldManager manager = ProxyWorldManager.getInstance();
            int worldCount = manager.getActiveWorldCount();
            manager.clearAll();
            
            // Mark renderers as dirty to force rebuild
            TardisDoorBOTI.markDirty();
            TardisExteriorBOTI.markDirty();
            
            context.getSource().sendFeedback(Text.literal("Cleared " + worldCount + " proxy worlds and marked renderers dirty."));
            return Command.SINGLE_SUCCESS;
        })));
    }

    public static String stringify(List<? extends Identifiable> list) {
        return list.stream().map(idlike -> idlike == null ? null : idlike.id().toString()).toList().toString();
    }
}
