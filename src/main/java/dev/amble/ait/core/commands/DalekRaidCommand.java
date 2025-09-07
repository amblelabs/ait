package dev.amble.ait.core.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.amble.ait.AITMod;
import dev.amble.ait.core.AITEntityTypes;
import net.minecraft.command.argument.TextArgumentType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.raid.RaiderEntity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.village.raid.Raid;
import net.minecraft.village.raid.RaidManager;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;


public class DalekRaidCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("ait")
                .then(literal("dalekraid")
                        .requires(source -> source.hasPermissionLevel(3))
                .then(literal("start").then(argument("omenlvl", IntegerArgumentType.integer(0)).executes(context ->
                                DalekRaidCommand.executeStart(context.getSource(), IntegerArgumentType.
                                        getInteger(context, "omenlvl"))))).then(literal("stop").
                executes(context -> DalekRaidCommand.executeStop(context.getSource()))).
                then(literal("check").executes(context -> DalekRaidCommand.executeCheck(
                        context.getSource()))).then(literal("sound").then(argument("type",
                                TextArgumentType.text()).executes(context -> DalekRaidCommand.executeSound
                                (context.getSource(), TextArgumentType.getTextArgument(context, "type")))))
                .then(literal("spawnleader").executes(context -> DalekRaidCommand.executeSpawnLeader(
                        context.getSource()))).then(literal("setomen").then(
                argument("level", IntegerArgumentType.integer(0)).executes(context -> DalekRaidCommand
                        .executeSetOmen(context.getSource(), IntegerArgumentType.getInteger(context, "level")))))
                .then(literal("glow").executes(context -> DalekRaidCommand.executeGlow(context.getSource())))));
    }

    private static int executeGlow(ServerCommandSource source) throws CommandSyntaxException {
        Raid raid = DalekRaidCommand.getRaid(source.getPlayerOrThrow());
        if (raid != null) {
            Set<RaiderEntity> set = raid.getAllRaiders();
            for (RaiderEntity raiderEntity : set) {
                raiderEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, 1000, 1));
            }
        }
        return Command.SINGLE_SUCCESS;
    }

    private static int executeSetOmen(ServerCommandSource source, int level) throws CommandSyntaxException {
        Raid raid = DalekRaidCommand.getRaid(source.getPlayerOrThrow());
        if (raid != null) {
            int i = raid.getMaxAcceptableBadOmenLevel();
            if (level > i) {
                source.sendError(Text.translatable("message." + AITMod.MOD_ID + ".dalek_raid.command_omen_error", i));
            } else {
                int j = raid.getBadOmenLevel();
                raid.setBadOmenLevel(level);
                source.sendFeedback(() -> Text.translatable("message." + AITMod.MOD_ID + ".dalek_raid.command_omen_success", j, level), false);
            }
        } else {
            source.sendError(Text.translatable("message." + AITMod.MOD_ID + ".dalek_raid.command_omen_missing"));
        }
        return Command.SINGLE_SUCCESS;
    }

    private static int executeSpawnLeader(ServerCommandSource source) {
        source.sendFeedback(() -> Text.translatable("message." + AITMod.MOD_ID + ".dalek_raid.command_leader_success"), false);
        RaiderEntity raiderEntity = AITEntityTypes.DALEK_ENTITY.create(source.getWorld());
        if (raiderEntity == null) {
            source.sendError(Text.translatable("message." + AITMod.MOD_ID + ".dalek_raid.command_leader_error"));
            return 0;
        }
        raiderEntity.setPatrolLeader(true);
        raiderEntity.equipStack(EquipmentSlot.HEAD, Raid.getOminousBanner());
        raiderEntity.setPosition(source.getPosition().x, source.getPosition().y, source.getPosition().z);
        raiderEntity.initialize(source.getWorld(), source.getWorld().getLocalDifficulty(BlockPos.ofFloored(source.getPosition())), SpawnReason.COMMAND, null, null);
        source.getWorld().spawnEntityAndPassengers(raiderEntity);
        return Command.SINGLE_SUCCESS;
    }

    private static int executeSound(ServerCommandSource source, @Nullable Text type) {
        if (type != null && type.getString().equals("local")) {
            ServerWorld serverWorld = source.getWorld();
            Vec3d vec3d = source.getPosition().add(5.0, 0.0, 0.0);
            serverWorld.playSound(null, vec3d.x, vec3d.y, vec3d.z, SoundEvents.EVENT_RAID_HORN, SoundCategory.NEUTRAL, 2.0f, 1.0f, serverWorld.random.nextLong());
        }
        return Command.SINGLE_SUCCESS;
    }

    private static int executeStart(ServerCommandSource source, int level) throws CommandSyntaxException {
        ServerPlayerEntity serverPlayerEntity = source.getPlayerOrThrow();
        BlockPos blockPos = serverPlayerEntity.getBlockPos();
        if (serverPlayerEntity.getServerWorld().hasRaidAt(blockPos)) {
            source.sendError(Text.translatable("message." + AITMod.MOD_ID + ".dalek_raid.command_start_error_close"));
            return -Command.SINGLE_SUCCESS;
        }
        RaidManager raidManager = serverPlayerEntity.getServerWorld().getRaidManager();
        Raid raid = raidManager.startRaid(serverPlayerEntity);
        if (raid != null) {
            raid.setBadOmenLevel(level);
            raidManager.markDirty();
            source.sendFeedback(() -> Text.translatable("message." + AITMod.MOD_ID + ".dalek_raid.command_start_success"), false);
        } else {
            source.sendError(Text.translatable("message." + AITMod.MOD_ID + ".dalek_raid.command_start_error"));
        }
        return Command.SINGLE_SUCCESS;
    }

    private static int executeStop(ServerCommandSource source) throws CommandSyntaxException {
        ServerPlayerEntity serverPlayerEntity = source.getPlayerOrThrow();
        BlockPos blockPos = serverPlayerEntity.getBlockPos();
        Raid raid = serverPlayerEntity.getServerWorld().getRaidAt(blockPos);
        if (raid != null) {
            raid.invalidate();
            source.sendFeedback(() -> Text.translatable("message." + AITMod.MOD_ID + ".dalek_raid.command_stop_success"), false);
            return Command.SINGLE_SUCCESS;
        }
        source.sendError(Text.translatable("message." + AITMod.MOD_ID + ".dalek_raid.command_stop_error"));
        return -Command.SINGLE_SUCCESS;
    }

    private static int executeCheck(ServerCommandSource source) throws CommandSyntaxException {
        Raid raid = DalekRaidCommand.getRaid(source.getPlayerOrThrow());
        if (raid != null) {
            source.sendFeedback(() -> Text.translatable("message." + AITMod.MOD_ID + ".dalek_raid.command_check_found"), false);
            source.sendFeedback(() -> Text.translatable("message." + AITMod.MOD_ID + ".dalek_raid.command_check_info", raid.getGroupsSpawned(), raid.getBadOmenLevel(), raid.getRaiderCount(), raid.getCurrentRaiderHealth(), raid.getTotalHealth()), false);
            return Command.SINGLE_SUCCESS;
        }
        source.sendFeedback(() -> Text.translatable("message." + AITMod.MOD_ID + ".dalek_raid.command_check_error"), false);
        return 0;
    }

    @Nullable private static Raid getRaid(ServerPlayerEntity player) {
        return player.getServerWorld().getRaidAt(player.getBlockPos());
    }
}
