package dev.amble.ait.core.commands;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import dev.amble.ait.AITMod;
import dev.amble.ait.compat.permissionapi.PermissionAPICompat;
import dev.amble.ait.core.commands.argument.TardisArgumentType;
import dev.amble.ait.core.tardis.ServerTardis;
import dev.amble.ait.core.tardis.handler.FuelHandler;
import dev.amble.ait.core.util.TextUtil;

public class FuelCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher
                .register(literal(AITMod.MOD_ID).then(literal("fuel").requires(source -> PermissionAPICompat.hasPermission(source, "ait.command.fuel", 2))
                        .then(literal("add").then(argument("tardis", TardisArgumentType.tardis())
                                .then(argument("amount", DoubleArgumentType.doubleArg(0, FuelHandler.TARDIS_MAX_FUEL))
                                        .executes(FuelCommand::add))))
                        .then(literal("remove").requires(source -> PermissionAPICompat.hasPermission(source, "ait.command.fuel.remove", 2))
                                .then(argument("tardis", TardisArgumentType.tardis()).then(
                                        argument("amount", DoubleArgumentType.doubleArg(0, FuelHandler.TARDIS_MAX_FUEL))
                                                .executes(FuelCommand::remove))))
                        .then(literal("set").requires(source -> PermissionAPICompat.hasPermission(source, "ait.command.fuel.set", 2))
                                .then(argument("tardis", TardisArgumentType.tardis()).then(
                                        argument("amount", DoubleArgumentType.doubleArg(0, FuelHandler.TARDIS_MAX_FUEL))
                                                .executes(FuelCommand::set))))
                        .then(literal("get").requires(source -> PermissionAPICompat.hasPermission(source, "ait.command.fuel.get", 2))
                                .then(argument("tardis", TardisArgumentType.tardis()).executes(FuelCommand::get)))));
    }

    private static int add(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        ServerTardis tardis = TardisArgumentType.getTardis(context, "tardis");

        double fuel = DoubleArgumentType.getDouble(context, "amount");
        tardis.addFuel(fuel);

        source.sendMessage(
                Text.translatable("message.ait.fuel.add", fuel, TextUtil.forTardis(tardis), tardis.getFuel()));

        return Command.SINGLE_SUCCESS;
    }

    private static int remove(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        ServerTardis tardis = TardisArgumentType.getTardis(context, "tardis");

        double fuel = DoubleArgumentType.getDouble(context, "amount");
        tardis.removeFuel(fuel);

        source.sendMessage(
                Text.translatable("message.ait.fuel.remove", fuel, TextUtil.forTardis(tardis), tardis.getFuel()));

        return Command.SINGLE_SUCCESS;
    }

    private static int set(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        ServerTardis tardis = TardisArgumentType.getTardis(context, "tardis");

        double fuel = DoubleArgumentType.getDouble(context, "amount");

        if (fuel > FuelHandler.TARDIS_MAX_FUEL) {
            source.sendMessage(Text.translatable("message.ait.fuel.max"));
            return 0;
        }

        tardis.setFuelCount(fuel);
        source.sendMessage(Text.translatable("message.ait.fuel.set", TextUtil.forTardis(tardis), fuel));

        return Command.SINGLE_SUCCESS;
    }

    private static int get(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        ServerTardis tardis = TardisArgumentType.getTardis(context, "tardis");

        double fuel = tardis.fuel().getCurrentFuel();
        source.sendMessage(Text.translatable("message.ait.fuel.get", TextUtil.forTardis(tardis), fuel));

        return (int) fuel;
    }
}
