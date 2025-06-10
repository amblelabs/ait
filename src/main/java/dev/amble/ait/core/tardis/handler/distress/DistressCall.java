package dev.amble.ait.core.tardis.handler.distress;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import dev.amble.lib.data.CachedDirectedGlobalPos;
import dev.amble.lib.util.ServerLifecycleHooks;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.jetbrains.annotations.Nullable;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RotationPropertyHelper;
import net.minecraft.util.math.Vec3d;

import dev.amble.ait.api.tardis.link.v2.TardisRef;
import dev.amble.ait.core.AITSounds;
import dev.amble.ait.core.item.HypercubeItem;
import dev.amble.ait.core.tardis.ServerTardis;
import dev.amble.ait.core.tardis.Tardis;
import dev.amble.ait.core.tardis.manager.ServerTardisManager;
import dev.amble.ait.core.tardis.util.TardisUtil;
import dev.amble.ait.core.util.TextUtil;
import dev.amble.ait.core.world.TardisServerWorld;

public record DistressCall(Sender sender, String message, int lifetime, int creationTime, boolean isSourceCall) {
    private static final int DEFAULT_LIFETIME = 120 * 20; // 2 minute default lifetime

    private static DistressCall createTardis(UUID tardis, String message, MinecraftServer server, int lifetime, boolean isSourceCall) {
        return new DistressCall(new TardisSender(tardis), message, lifetime, server.getTicks(), isSourceCall);
    }
    private static DistressCall createTardis(UUID tardis, String message, boolean isSourceCall) {
        return createTardis(tardis, message, ServerLifecycleHooks.get(), DEFAULT_LIFETIME, isSourceCall);
    }
    public static DistressCall create(Tardis tardis, String message, boolean isSourceCall){
        return createTardis(tardis.getUuid(), message, isSourceCall);
    }

    private static DistressCall createPlayer(UUID player, String message, MinecraftServer server, int lifetime, boolean isSourceCall) {
        return new DistressCall(new PlayerSender(player), message, lifetime, server.getTicks(), isSourceCall);
    }
    public static DistressCall create(PlayerEntity player, String message, boolean isSourceCall) {
        return createPlayer(player.getUuid(), message, ServerLifecycleHooks.get(), DEFAULT_LIFETIME, isSourceCall);
    }
    public static DistressCall copyForSend(DistressCall call, int ticks) {
        return new DistressCall(
                call.sender(),
                call.message(),
                call.lifetime(),
                ticks,
                false
        );
    }

    public void summon(Tardis tardis, @Nullable ItemStack held) {
        CachedDirectedGlobalPos target = this.sender().position();

        tardis.travel().destination(target, true);

        tardis.getDesktop().playSoundAtEveryConsole(AITSounds.WAYPOINT_ACTIVATE);
        this.sender().playSoundAt(AITSounds.WAYPOINT_ACTIVATE);

        if (held != null && (held.getItem() instanceof HypercubeItem)) {
            held.setCount(0);
        }
    }

    public boolean isValid(int ticks) {
        return (this.creationTime + this.lifetime) > ticks;
    }
    private boolean isValid(MinecraftServer server) {
        return this.isValid(server.getTicks());
    }
    public boolean isValid() {
        return this.isValid(ServerLifecycleHooks.get());
    }
    public int getTimeLeft(int ticks) {
        if (this.isSourceCall()) {
            return this.lifetime;
        }

        int time = (this.creationTime + this.lifetime) - ticks;

        return Math.max(time, 0);
    }
    private int getTimeLeft(MinecraftServer server) {
        return this.getTimeLeft(server.getTicks());
    }
    public int getTimeLeft() {
        return this.getTimeLeft(ServerLifecycleHooks.get());
    }

    public boolean canSend(UUID source) {
        return this.isSourceCall() && this.isSource(source) && this.sender().canSend();
    }
    public boolean send(UUID source, @Nullable ItemStack held) {
        Sender sender = this.sender();

        if (!this.canSend(source)) {
            sender.playSoundAt(AITSounds.GROAN, 1.1F);

            return false;
        }

        // send off call
        this.send();

        if (held != null && (held.getItem() instanceof HypercubeItem)) {
            held.setCount(0);
        }

        sender.playSoundAt(AITSounds.BWEEP, 0.75F);

        return true;
    }
    /**
     * sends this distress call to all tardises
     */
    private void send() {
        CompletableFuture<Void> future = CompletableFuture.supplyAsync(() -> {
            ServerTardisManager.getInstance().forEach(this::send);

            return null;
        });
    }
    private void send(ServerTardis target) {
        if (this.isSource(target.getUuid())) return; // dont send to self
        if (!target.stats().receiveCalls().get()) return; // ignore if doesnt want to receive

        // spawn distress item at door
        ServerWorld world = target.world();

        Vec3d pos = TardisUtil.offsetInteriorDoorPosition(target);

        ItemStack created = HypercubeItem.create(copyForSend(this, world.getServer().getTicks()));
        ItemEntity entity = new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), created);

        world.spawnEntity(entity);

        world.playSound(null, BlockPos.ofFloored(pos), AITSounds.DING, SoundCategory.PLAYERS, 1.0F, 1F);
    }
    public boolean isSource(UUID uuid) {
        return uuid.equals(this.sender().getUuid());
    }

    public NbtCompound toNbt() {
        NbtCompound data = new NbtCompound();

        data.put("Sender", this.sender.toNbt());
        data.putString("Message", this.message);
        data.putInt("Lifetime", this.lifetime);
        data.putInt("CreationTime", this.creationTime);
        data.putBoolean("IsSourceCall", this.isSourceCall);

        return data;
    }
    private static DistressCall fromNbt(Sender send, NbtCompound data, int ticks) {
        return new DistressCall(
                send,
                data.getString("Message"),
                data.getInt("Lifetime"),
                Math.min(ticks, data.getInt("CreationTime")), // todo blehh
                data.getBoolean("IsSourceCall")
        );
    }
    public static DistressCall fromNbt(NbtCompound data, int ticks) {
        NbtCompound sender = data.getCompound("Sender");

        if (sender.contains("Tardis")) return fromTardisNbt(data, ticks);
        if (sender.contains("Player")) return fromPlayerNbt(data, ticks);

        throw new IllegalStateException();
    }
    public static DistressCall fromTardisNbt(NbtCompound data, int ticks) {
        return fromNbt(new TardisSender(data.getCompound("Sender").getUuid("Tardis")), data, ticks);
    }
    public static DistressCall fromPlayerNbt(NbtCompound data, int ticks) {
        return fromNbt(new PlayerSender(data.getCompound("Sender").getUuid("Player")), data, ticks);
    }

    public interface Sender {
        NbtCompound toNbt();
        UUID getUuid();
        CachedDirectedGlobalPos position();
        Text getTooltip();
        void playSoundAt(SoundEvent event, float pitch);
        default void playSoundAt(SoundEvent event) {
            this.playSoundAt(event, 1f);
        }
        boolean canSend();
    }
    public static class TardisSender implements Sender {
        private final UUID id;
        private final TardisRef ref;

        public TardisSender(UUID id) {
            this.id = id;
            this.ref = TardisRef.createAs(ServerLifecycleHooks.get().getOverworld(), this.getUuid());
        }
        public TardisSender(Tardis tardis) {
            this(tardis.getUuid());
        }

        public TardisRef tardis() {
            return this.ref;
        }

        @Override
        public NbtCompound toNbt() {
            NbtCompound data = new NbtCompound();

            data.putUuid("Tardis", this.id);

            return data;
        }

        @Override
        public UUID getUuid() {
            return this.id;
        }

        @Override
        public CachedDirectedGlobalPos position(){
            if (this.tardis().isEmpty()) return null;

            return this.tardis().get().travel().position();
        }

        @Override
        public Text getTooltip() {
            return TextUtil.forTardis(this.tardis().getId());
        }

        @Override
        public void playSoundAt(SoundEvent event, float pitch) {
            if (this.tardis().isEmpty()) return;

            this.tardis().get().getDesktop().playSoundAtEveryConsole(event, SoundCategory.BLOCKS, 1f, pitch);
        }

        @Override
        public boolean canSend() {
            return this.tardis().isPresent() && this.tardis().get().travel().isLanded();
        }
    }
    public static class PlayerSender implements Sender {
        private final UUID id;
        private PlayerEntity playerCache;

        public PlayerSender(UUID id) {
            this.id = id;
        }
        public PlayerSender(PlayerEntity player) {
            this(player.getUuid());
        }

        @Override
        public NbtCompound toNbt() {
            NbtCompound data = new NbtCompound();

            data.putUuid("Player", this.id);

            return data;
        }

        @Override
        public UUID getUuid() {
            return this.id;
        }

        @Override
        public CachedDirectedGlobalPos position() {
            if (this.player() == null) return CachedDirectedGlobalPos.create(
                    ServerLifecycleHooks.get().getOverworld(),
                    BlockPos.ORIGIN,
                    (byte)0
            );

            if (this.player().getWorld() instanceof TardisServerWorld tardisWorld)
                return tardisWorld.getTardis().travel().position();

            return CachedDirectedGlobalPos.create(
                    (ServerWorld) this.player().getWorld(),
                    this.player().getBlockPos(),
                    (byte) RotationPropertyHelper.fromYaw(this.player().getYaw())
            );
        }

        @Override
        public Text getTooltip() {
            MutableText text = (this.player() == null) ? Text.literal("???") : this.player().getDisplayName().copy();

            return text.formatted(Formatting.BOLD, Formatting.GREEN);
        }

        @Override
        public void playSoundAt(SoundEvent event, float pitch) {
            this.position().getWorld().playSound(null, this.position().getPos(), event, SoundCategory.PLAYERS, 1.0F, pitch);
        }

        @Override
        public boolean canSend() {
            return true;
        }

        public PlayerEntity player() {
            if (this.playerCache == null) {
                this.playerCache = this.findPlayer();
            }

            return this.playerCache;
        }
        private PlayerEntity findPlayer() {
            if (ServerLifecycleHooks.isServer())
                return ServerLifecycleHooks.get().getPlayerManager().getPlayer(this.getUuid());

            return this.findClientPlayer();
        }

        @Environment(EnvType.CLIENT)
        private PlayerEntity findClientPlayer() {
            return MinecraftClient.getInstance().world.getPlayerByUuid(this.getUuid());
        }
    }
}
