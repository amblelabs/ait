package dev.amble.ait.core.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.amble.ait.AITMod;
import dev.amble.ait.core.commands.argument.TardisArgumentType;
import dev.amble.ait.core.tardis.ServerTardis;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.ParticleEffectArgumentType;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class SetDoorParticleCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess access) {
        dispatcher.register(literal(AITMod.MOD_ID).then(literal("door_particle").requires(source -> source.hasPermissionLevel(2))
                .then(argument("tardis", TardisArgumentType.tardis()).then(argument("particle_type", ParticleEffectArgumentType.particleEffect(access)).executes(SetDoorParticleCommand::execute)))));
    }

    private static int execute(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        ServerTardis tardis = TardisArgumentType.getTardis(context, "tardis");
        ParticleEffect particle = ParticleEffectArgumentType.getParticle(context, "particle_type");

        tardis.door().setDoorParticles(particle);

        source.sendFeedback(
                () -> Text.translatableWithFallback("command.ait.door_particle.done", "Particle of [%s] set to [%s]", tardis.getUuid(), particle.asString()),
                true);

        return Command.SINGLE_SUCCESS;
    }
}
