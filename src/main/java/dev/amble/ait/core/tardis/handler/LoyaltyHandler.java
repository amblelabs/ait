package dev.amble.ait.core.tardis.handler;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

import dev.amble.ait.AITMod;
import dev.amble.ait.api.Nameable;
import dev.amble.ait.api.tardis.TardisComponent;
import dev.amble.ait.api.tardis.TardisTickable;
import dev.amble.ait.core.AITSounds;
import dev.amble.ait.core.advancement.TardisCriterions;
import dev.amble.ait.core.likes.ItemOpinion;
import dev.amble.ait.core.likes.ItemOpinionRegistry;
import dev.amble.ait.core.tardis.ServerTardis;
import dev.amble.ait.core.tardis.Tardis;
import dev.amble.ait.core.tardis.manager.ServerTardisManager;
import dev.amble.ait.core.world.TardisServerWorld;
import dev.amble.ait.data.Loyalty;
import dev.amble.ait.data.schema.console.ConsoleVariantSchema;
import dev.amble.ait.data.schema.desktop.TardisDesktopSchema;
import dev.amble.ait.data.schema.exterior.ExteriorVariantSchema;
import dev.amble.ait.data.schema.sonic.SonicSchema;
import dev.amble.ait.registry.impl.DesktopRegistry;
import dev.amble.ait.registry.impl.SonicRegistry;
import dev.amble.ait.registry.impl.console.variant.ConsoleVariantRegistry;
import dev.amble.ait.registry.impl.exterior.ExteriorVariantRegistry;
import dev.amble.lib.util.ServerLifecycleHooks;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;

public class LoyaltyHandler extends TardisComponent implements TardisTickable {
    private final Map<UUID, Loyalty> data;
    private boolean messageEnabled = true;

    static {
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) ->
                resetRejectedSpawnOnJoin(handler.getPlayer(), server));
    }

    /** Ensures the connection callback is registered during common initialization. */
    public static void init() {
    }

    public boolean isMessageEnabled() {
        return messageEnabled;
    }

    public void setMessageEnabled(boolean messageEnabled) {
        this.messageEnabled = messageEnabled;
    }

    public LoyaltyHandler(HashMap<UUID, Loyalty> data) {
        super(Id.LOYALTY);
        this.data = data;
    }

    public LoyaltyHandler() {
        this(new HashMap<>());
    }

    public Map<UUID, Loyalty> data() {
        return this.data;
    }

    public Loyalty get(PlayerEntity player) {
        return player == null ? new Loyalty(Loyalty.Type.NEUTRAL) : this.get(player.getUuid());
    }

    public Loyalty get(UUID playerId) {
        return playerId == null
                ? new Loyalty(Loyalty.Type.NEUTRAL)
                : this.data.getOrDefault(playerId, new Loyalty(Loyalty.Type.NEUTRAL));
    }

    public Loyalty set(ServerPlayerEntity player, Loyalty loyalty) {
        if (player == null || loyalty == null)
            return new Loyalty(Loyalty.Type.NEUTRAL);

        Loyalty previous = this.get(player);
        this.data.put(player.getUuid(), loyalty);
        this.unlock(player, loyalty);
        if (previous.type() != Loyalty.Type.REJECT && loyalty.type() == Loyalty.Type.REJECT)
            resetRejectedSpawn(player, this.tardis);

        this.sync();
        return loyalty;
    }

    public void setRejected(UUID playerId) {
        if (playerId == null)
            return;

        Loyalty rejected = new Loyalty(Loyalty.Type.REJECT);
        Loyalty previous = this.data.put(playerId, rejected);
        if (rejected.equals(previous))
            return;

        MinecraftServer server = ServerLifecycleHooks.get();
        ServerPlayerEntity player = server == null ? null : server.getPlayerManager().getPlayer(playerId);
        if (player != null)
            resetRejectedSpawn(player, this.tardis);
        this.sync();
    }

    @Override
    public void tick(MinecraftServer server) {
        if (server.getTicks() % 40 != 0)
            return;

        for (ServerPlayerEntity player : tardis.asServer().world().getPlayers()) {
            Loyalty loyalty = this.get(player);

            if (!loyalty.isOf(Loyalty.Type.NEUTRAL))
                continue;

            if (ItemOpinionRegistry.getInstance().get(player.getMainHandStack()).isPresent()) {
                ItemOpinion opinion = ItemOpinionRegistry.getInstance().get(player.getMainHandStack()).get();
                tardis.opinions().contains(opinion);
                player.sendMessage(Text.translatable("ait.tardis.likes_item", true));
            }

            if (AITMod.RANDOM.nextInt(0, 20) != 14)
                continue;

            this.addLevel(player, 1);
        }
    }

    public void update(ServerPlayerEntity player, Function<Loyalty, Loyalty> consumer) {
        Loyalty current = this.get(player);
        current = consumer.apply(current);

        this.set(player, current);
    }

    public void unlock(ServerPlayerEntity player, Loyalty loyalty) {
        ServerTardis tardis = (ServerTardis) this.tardis;

        boolean playSound = messageEnabled;

        if (playSound) {
            playSound = ConsoleVariantRegistry.getInstance().tryUnlock(tardis, loyalty,
                    schema -> this.playUnlockEffects(player, schema));
            playSound = DesktopRegistry.getInstance().tryUnlock(tardis, loyalty,
                    schema -> this.playUnlockEffects(player, schema)) || playSound;
            playSound = ExteriorVariantRegistry.getInstance().tryUnlock(tardis, loyalty,
                    schema -> this.playUnlockEffects(player, schema)) || playSound;
            playSound = SonicRegistry.getInstance().tryUnlock(tardis, loyalty,
                    schema -> this.playUnlockEffects(player, schema)) || playSound;
        }

        if (playSound)
            player.getServerWorld().playSound(null, player.getBlockPos(), AITSounds.LOYALTY_UP,
                    SoundCategory.PLAYERS, 0.2F, 1.0F);

        if (loyalty.isOf(Loyalty.Type.OWNER))
            TardisCriterions.REACH_OWNER.trigger(player);
        else if (loyalty.isOf(Loyalty.Type.PILOT))
            TardisCriterions.REACH_PILOT.trigger(player);
    }

    private void playUnlockEffects(ServerPlayerEntity player, Nameable nameable) {
        Text nameText = nameable.text().copy().formatted(Formatting.GREEN);

        Text unlockedMessage;
        if (nameable instanceof SonicSchema) {
            unlockedMessage = Text.translatable("message.ait.unlocked_sonic", nameText).formatted(Formatting.WHITE);
        } else if (nameable instanceof ConsoleVariantSchema) {
            unlockedMessage = Text.translatable("message.ait.unlocked_console", nameText).formatted(Formatting.WHITE);
        } else if (nameable instanceof TardisDesktopSchema) {
            unlockedMessage = Text.translatable("message.ait.unlocked_interior", nameText).formatted(Formatting.WHITE);
        } else if (nameable instanceof ExteriorVariantSchema) {
            unlockedMessage = Text.translatable("message.ait.unlocked_exterior", nameText).formatted(Formatting.WHITE);
        } else {
            unlockedMessage = Text.translatable("message.ait.unlocked", nameText).formatted(Formatting.WHITE);
        }

        player.sendMessage(unlockedMessage, false);
    }


    public void addLevel(ServerPlayerEntity player, int level) {
        this.update(player, loyalty -> loyalty.add(level));
    }

    public void subLevel(ServerPlayerEntity player, int level) {
        this.addLevel(player, -level);
    }

    public void addLevel(UUID playerId, int level) {
        if (playerId == null || level == 0)
            return;

        Loyalty current = this.get(playerId);
        Loyalty updated = current.add(level);
        if (updated.equals(current))
            return;

        this.data.put(playerId, updated);
        MinecraftServer server = ServerLifecycleHooks.get();
        ServerPlayerEntity player = server == null ? null : server.getPlayerManager().getPlayer(playerId);
        if (player != null) {
            if (level > 0)
                this.unlock(player, updated);
            if (current.type() != Loyalty.Type.REJECT && updated.type() == Loyalty.Type.REJECT)
                resetRejectedSpawn(player, this.tardis);
        }
        this.sync();
    }

    public void subLevel(UUID playerId, int level) {
        this.addLevel(playerId, -level);
    }

    public static void resetRejectedSpawn(ServerPlayerEntity player, Tardis tardis) {
        if (player == null || tardis == null || AITMod.CONFIG == null || !AITMod.CONFIG.tardisTemperament
                || tardis.loyalty().get(player).type() != Loyalty.Type.REJECT
                || player.getSpawnPointPosition() == null
                || !TardisServerWorld.isTardisDimension(player.getSpawnPointDimension()))
            return;

        UUID spawnTardis;
        try {
            spawnTardis = TardisServerWorld.getTardisId(player.getSpawnPointDimension());
        } catch (IllegalArgumentException exception) {
            return;
        }

        if (spawnTardis.equals(tardis.getUuid()))
            player.setSpawnPoint(World.OVERWORLD, null, 0, false, false);
    }

    private static void resetRejectedSpawnOnJoin(ServerPlayerEntity player, MinecraftServer server) {
        if (player == null || server == null || AITMod.CONFIG == null || !AITMod.CONFIG.tardisTemperament
                || player.getSpawnPointPosition() == null
                || !TardisServerWorld.isTardisDimension(player.getSpawnPointDimension()))
            return;

        UUID tardisId;
        try {
            tardisId = TardisServerWorld.getTardisId(player.getSpawnPointDimension());
        } catch (IllegalArgumentException exception) {
            return;
        }

        ServerTardisManager manager = ServerTardisManager.getInstance();
        if (manager == null)
            return;

        ServerTardis tardis = manager.demandTardis(server, tardisId);
        if (tardis != null)
            resetRejectedSpawn(player, tardis);
    }

    public ServerPlayerEntity getLoyalPlayerInside() {
        if (!(this.tardis instanceof ServerTardis serverTardis))
            return null;

        ServerPlayerEntity highest = null;
        int highestLoyalty = 0;

        for (ServerPlayerEntity player : serverTardis.world().getPlayers()) {
            if (highest == null) {
                highest = player;
                highestLoyalty = this.get(highest).level();
                continue;
            }

            int found = this.get(player).level();

            if (found > highestLoyalty) {
                highest = player;
                highestLoyalty = found;
            }
        }

        return highest;
    }

    public void sendMessageToPilot(Text text) {
        ServerPlayerEntity player = this.getLoyalPlayerInside();

        if (player == null)
            return;

        player.sendMessage(text, true);
    }
}
