package dev.amble.ait.core.commands;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

import java.util.Set;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import org.jetbrains.annotations.Nullable;

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

import dev.amble.ait.core.AITEntityTypes;


public class DalekRaidCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("ait")
                .requires(source -> source.hasPermissionLevel(3)).then(literal("dalekraid")
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
        return 1;
    }

    private static int executeSetOmen(ServerCommandSource source, int level) throws CommandSyntaxException {
        Raid raid = DalekRaidCommand.getRaid(source.getPlayerOrThrow());
        if (raid != null) {
            int i = raid.getMaxAcceptableBadOmenLevel();
            if (level > i) {
                source.sendError(Text.literal("Sorry, the max bad omen level you can set is " + i));
            } else {
                int j = raid.getBadOmenLevel();
                raid.setBadOmenLevel(level);
                source.sendFeedback(() -> Text.literal("Changed village's bad omen level from " + j + " to " + level), false);
            }
        } else {
            source.sendError(Text.literal("No raid found here"));
        }
        return 1;
    }

    private static int executeSpawnLeader(ServerCommandSource source) {
        source.sendFeedback(() -> Text.literal("Spawned a raid captain"), false);
        RaiderEntity raiderEntity = AITEntityTypes.DALEK_ENTITY.create(source.getWorld());
        if (raiderEntity == null) {
            source.sendError(Text.literal("Dalek failed to spawn"));
            return 0;
        }
        raiderEntity.setPatrolLeader(true);
        raiderEntity.equipStack(EquipmentSlot.HEAD, Raid.getOminousBanner());
        raiderEntity.setPosition(source.getPosition().x, source.getPosition().y, source.getPosition().z);
        raiderEntity.initialize(source.getWorld(), source.getWorld().getLocalDifficulty(BlockPos.ofFloored(source.getPosition())), SpawnReason.COMMAND, null, null);
        source.getWorld().spawnEntityAndPassengers(raiderEntity);
        return 1;
    }

    private static int executeSound(ServerCommandSource source, @Nullable Text type) {
        if (type != null && type.getString().equals("local")) {
            ServerWorld serverWorld = source.getWorld();
            Vec3d vec3d = source.getPosition().add(5.0, 0.0, 0.0);
            serverWorld.playSound(null, vec3d.x, vec3d.y, vec3d.z, SoundEvents.EVENT_RAID_HORN, SoundCategory.NEUTRAL, 2.0f, 1.0f, serverWorld.random.nextLong());
        }
        return 1;
    }

    private static int executeStart(ServerCommandSource source, int level) throws CommandSyntaxException {
        ServerPlayerEntity serverPlayerEntity = source.getPlayerOrThrow();
        BlockPos blockPos = serverPlayerEntity.getBlockPos();
        if (serverPlayerEntity.getServerWorld().hasRaidAt(blockPos)) {
            source.sendError(Text.literal("Raid already started close by"));
            return -1;
        }
        RaidManager raidManager = serverPlayerEntity.getServerWorld().getRaidManager();
        Raid raid = raidManager.startRaid(serverPlayerEntity);
        if (raid != null) {
            raid.setBadOmenLevel(level);
            raidManager.markDirty();
            source.sendFeedback(() -> Text.literal("Created a raid in your local village"), false);
        } else {
            source.sendError(Text.literal("Failed to create a raid in your local village"));
        }
        return 1;
    }

    private static int executeStop(ServerCommandSource source) throws CommandSyntaxException {
        ServerPlayerEntity serverPlayerEntity = source.getPlayerOrThrow();
        BlockPos blockPos = serverPlayerEntity.getBlockPos();
        Raid raid = serverPlayerEntity.getServerWorld().getRaidAt(blockPos);
        if (raid != null) {
            raid.invalidate();
            source.sendFeedback(() -> Text.literal("Stopped raid"), false);
            return 1;
        }
        source.sendError(Text.literal("No raid here"));
        return -1;
    }

    private static int executeCheck(ServerCommandSource source) throws CommandSyntaxException {
        Raid raid = DalekRaidCommand.getRaid(source.getPlayerOrThrow());
        if (raid != null) {
            source.sendFeedback(() -> Text.literal("Found a started raid! "), false);
            String stringBuilder2 = "Num groups spawned: " +
                    raid.getGroupsSpawned() +
                    " Bad omen level: " +
                    raid.getBadOmenLevel() +
                    " Num mobs: " +
                    raid.getRaiderCount() +
                    " Raid health: " +
                    raid.getCurrentRaiderHealth() +
                    " / " +
                    raid.getTotalHealth();
            source.sendFeedback(() -> Text.literal(stringBuilder2), false);
            return 1;
        }
        source.sendError(Text.literal("Found no started raids"));
        return 0;
    }

    @Nullable private static Raid getRaid(ServerPlayerEntity player) {
        return player.getServerWorld().getRaidAt(player.getBlockPos());
    }
}
