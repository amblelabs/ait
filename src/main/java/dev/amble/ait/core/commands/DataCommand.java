package dev.amble.ait.core.commands;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

import com.google.gson.JsonElement;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;

import net.minecraft.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import dev.amble.ait.AITMod;
import dev.amble.ait.api.KeyedTardisComponent;
import dev.amble.ait.api.TardisComponent;
import dev.amble.ait.core.commands.argument.JsonElementArgumentType;
import dev.amble.ait.core.commands.argument.TardisArgumentType;
import dev.amble.ait.core.tardis.ServerTardis;
import dev.amble.ait.core.tardis.manager.ServerTardisManager;
import dev.amble.ait.data.properties.Value;
import dev.amble.ait.registry.impl.TardisComponentRegistry;

public class DataCommand {

    public static final SuggestionProvider<ServerCommandSource> COMPONENT_SUGGESTION = (context,
            builder) -> CommandSource.suggestMatching(
                    TardisComponentRegistry.getInstance().getValues().stream().map(TardisComponent.IdLike::name),
                    builder);

    public static final SuggestionProvider<ServerCommandSource> VALUE_SUGGESTION = (context, builder) -> {
        ServerTardis tardis = TardisArgumentType.getTardis(context, "tardis");
        String rawComponent = StringArgumentType.getString(context, "component");

        TardisComponent.IdLike id = TardisComponentRegistry.getInstance().get(rawComponent);

        if (!(tardis.handler(id) instanceof KeyedTardisComponent keyed))
            return builder.buildFuture(); // womp womp

        return CommandSource.suggestMatching(
                keyed.getPropertyData().values().stream().map(value -> value.getProperty().getName()), builder);
    };

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal(AITMod.MOD_ID).then(literal("data").requires(source -> source.hasPermissionLevel(2))
                .then(argument("tardis", TardisArgumentType.tardis()).then(argument("component",
                        StringArgumentType.word())
                        .suggests(COMPONENT_SUGGESTION)
                        .then(argument("value", StringArgumentType.word()).suggests(VALUE_SUGGESTION)
                                .then(literal("set").then(argument("data", JsonElementArgumentType.jsonElement())
                                        .executes(DataCommand::runSet)))
                                .then(literal("get").executes(DataCommand::runGet)))))));
    }

    private static <T> int runGet(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        ServerTardis tardis = TardisArgumentType.getTardis(context, "tardis");

        String rawComponent = StringArgumentType.getString(context, "component");
        String valueName = StringArgumentType.getString(context, "value");

        TardisComponent.IdLike id = TardisComponentRegistry.getInstance().get(rawComponent);

        if (!(tardis.handler(id) instanceof KeyedTardisComponent keyed)) {
            source.sendMessage(Text.translatable("command.ait.data.fail", valueName, rawComponent));
            return 0; // womp womp
        }

        Value<T> value = keyed.getPropertyData().getExact(valueName);
        T obj = value.get();

        String json = ServerTardisManager.getInstance().getFileGson().toJson(obj);

        source.sendMessage(Text.translatable("command.ait.data.get", valueName, json));
        return Command.SINGLE_SUCCESS;
    }

    private static <T> int runSet(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        ServerTardis tardis = TardisArgumentType.getTardis(context, "tardis");

        String rawComponent = StringArgumentType.getString(context, "component");
        String valueName = StringArgumentType.getString(context, "value");

        JsonElement data = JsonElementArgumentType.getJsonElement(context, "data");
        TardisComponent.IdLike id = TardisComponentRegistry.getInstance().get(rawComponent);

        if (!(tardis.handler(id) instanceof KeyedTardisComponent keyed)) {
            source.sendMessage(Text.translatable("command.ait.data.fail", valueName, rawComponent));
            return 0; // womp womp
        }

        Value<T> value = keyed.getPropertyData().getExact(valueName);
        Class<?> classOfT = value.getProperty().getType().getClazz();

        T obj = (T) ServerTardisManager.getInstance().getFileGson().fromJson(data.toString(), classOfT);

        value.set(obj);
        source.sendMessage(Text.translatable("command.ait.data.set", valueName, obj.toString()));

        return Command.SINGLE_SUCCESS;
    }
}
