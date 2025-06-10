package dev.amble.ait.core.commands;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

import com.google.gson.JsonElement;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;

import net.minecraft.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import dev.amble.ait.AITMod;
import dev.amble.ait.api.tardis.KeyedTardisComponent;
import dev.amble.ait.api.tardis.TardisComponent;
import dev.amble.ait.core.commands.argument.JsonElementArgumentType;
import dev.amble.ait.core.commands.argument.TardisArgumentType;
import dev.amble.ait.core.tardis.ServerTardis;
import dev.amble.ait.core.tardis.Tardis;
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

    private static <T> int runGet(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        ServerTardis tardis = TardisArgumentType.getTardis(context, "tardis");
        Value<T> value = getValue(context, tardis);

        if (value == null)
            return 0;

        T obj = value.get();

        String json = ServerTardisManager.getInstance().getFileGson().toJson(obj);

        source.sendMessage(Text.translatable("command.ait.data.get",
                value.getProperty().getName(), json));

        return Command.SINGLE_SUCCESS;
    }

    @SuppressWarnings("unchecked")
    private static <T> int runSet(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        ServerTardis tardis = TardisArgumentType.getTardis(context, "tardis");

        Value<T> value = getValue(context, tardis);

        if (value == null)
            return 0;

        JsonElement data = JsonElementArgumentType.getJsonElement(context, "data");

        Class<?> classOfT = value.getProperty().getType().getClazz();
        T obj = (T) ServerTardisManager.getInstance().getFileGson().fromJson(data, classOfT);

        value.set(obj);
        source.sendMessage(Text.translatable("command.ait.data.set",
                value.getProperty().getName(), obj.toString()));

        return Command.SINGLE_SUCCESS;
    }

    private static <T> Value<T> getValue(CommandContext<ServerCommandSource> context, Tardis tardis) {
        String valueName = StringArgumentType.getString(context, "value");
        String rawComponent = StringArgumentType.getString(context, "component");

        TardisComponent.IdLike id = TardisComponentRegistry.getInstance().get(rawComponent);

        if (!(tardis.handler(id) instanceof KeyedTardisComponent keyed)) {
            context.getSource().sendMessage(Text.translatable("command.ait.data.fail", valueName, rawComponent));
            return null; // womp womp
        }

        return keyed.getPropertyData().getExact(valueName);
    }
}
