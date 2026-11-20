package dev.amble.ait.core.tardis.comms;

import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import dev.amble.ait.api.tardis.TardisEvents;
import dev.amble.ait.compat.svc.CommsHandler;
import dev.amble.ait.core.item.PhoneItem;
import dev.amble.ait.core.tardis.ServerTardis;
import dev.amble.lib.util.ServerLifecycleHooks;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import org.jetbrains.annotations.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public final class TardisPhones {

    public static final double CABLE_LENGTH = 8;

    private static final double SNAP_LENGTH = CABLE_LENGTH * 2;
    private static final double PULL_STRENGTH = 0.15;
    private static final double MAX_PULL = 1.0;
    private static final double STRAY_SEARCH = 48;

    public record Handset(UUID session, UUID line, UUID anchorId, UUID holder, RegistryKey<World> world, Vec3d anchor,
                          boolean dropped) {

        private Handset held(UUID holder) {
            return new Handset(this.session, this.line, this.anchorId, holder, this.world, this.anchor, false);
        }

        private Handset onGround() {
            return new Handset(this.session, this.line, this.anchorId, this.holder, this.world, this.anchor, true);
        }
    }

    public record Speaker(RegistryKey<World> world, Vec3d pos) {
    }

    private static final Map<UUID, Handset> BY_ANCHOR = new ConcurrentHashMap<>();
    private static final Map<UUID, Handset> BY_SESSION = new ConcurrentHashMap<>();
    private static final Map<UUID, UUID> ITEMS = new ConcurrentHashMap<>();
    private static final Map<UUID, Speaker> SPEAKERS = new ConcurrentHashMap<>();

    private TardisPhones() {
    }

    public static void init() {
        ServerTickEvents.END_SERVER_TICK.register(TardisPhones::tick);

        TardisEvents.RECONFIGURE_DESKTOP.register(tardis -> {
            MinecraftServer server = ServerLifecycleHooks.get();

            for (Handset handset : BY_SESSION.values()) {
                if (handset.line().equals(tardis.getUuid()))
                    release(server, handset, true);
            }
        });
    }

    public static boolean isActive(UUID session, UUID holder) {
        Handset handset = BY_SESSION.get(session);
        return handset != null && handset.holder().equals(holder);
    }

    @Nullable public static Handset get(UUID session) {
        return BY_SESSION.get(session);
    }

    @Nullable public static UUID holderFor(UUID line) {
        Handset handset = handsetFor(line);
        return handset == null ? null : handset.holder();
    }

    @Nullable public static Handset handsetFor(UUID line) {
        for (Handset handset : BY_SESSION.values()) {
            if (handset.line().equals(line))
                return handset;
        }

        return null;
    }

    @Nullable public static Speaker speakerFor(UUID line) {
        return SPEAKERS.get(line);
    }

    public static void claim(UUID session, ServerPlayerEntity player) {
        Handset handset = BY_SESSION.get(session);

        if (handset == null || (handset.holder().equals(player.getUuid()) && !handset.dropped()))
            return;

        replace(handset, handset.held(player.getUuid()));
    }

    private static void replace(Handset old, Handset updated) {
        BY_SESSION.put(updated.session(), updated);
        BY_ANCHOR.replace(updated.anchorId(), old, updated);
    }

    public static Set<UUID> liftedLines() {
        Set<UUID> lines = new HashSet<>();

        for (Handset handset : BY_SESSION.values())
            lines.add(handset.line());

        return lines;
    }

    public static void lift(ServerPlayerEntity player, ServerTardis tardis, ServerWorld world, BlockPos console, @Nullable Entity control) {
        UUID anchorId = control != null ? control.getUuid()
                : UUID.nameUUIDFromBytes(("control:" + world.getRegistryKey().getValue() + ":" + console.asLong()).getBytes(StandardCharsets.UTF_8));
        Vec3d anchor = control != null ? control.getPos().add(0, control.getHeight() / 2, 0) : Vec3d.ofCenter(console);
        BlockPos soundPos = control != null ? control.getBlockPos() : console;

        if (give(player, new Handset(UUID.randomUUID(), tardis.getUuid(), anchorId, player.getUuid(),
                world.getRegistryKey(), anchor, false), soundPos))
            lifted(tardis);
    }

    public static void liftExtension(ServerPlayerEntity player, ServerTardis tardis, ServerWorld world, BlockPos pos) {
        if (give(player, new Handset(UUID.randomUUID(), tardis.getUuid(), extensionAnchor(world, pos), player.getUuid(),
                world.getRegistryKey(), Vec3d.ofBottomCenter(pos).add(0, 0.3, 0), false), pos))
            lifted(tardis);
    }

    public static UUID extensionAnchor(ServerWorld world, BlockPos pos) {
        return UUID.nameUUIDFromBytes(("extension:" + world.getRegistryKey().getValue() + ":" + pos.asLong()).getBytes(StandardCharsets.UTF_8));
    }

    public static boolean anchored(UUID anchorId) {
        return BY_ANCHOR.containsKey(anchorId);
    }

    private static void lifted(ServerTardis tardis) {
        CommsHandler.of(tardis).handset().set(true);
        TardisComms.markDirty(tardis.getUuid());
    }

    public static boolean liftPhone(ServerPlayerEntity player, ServerWorld world, PhoneDirectory.Entry entry) {
        Vec3d anchor = Vec3d.ofBottomCenter(entry.pos()).add(0, 0.3, 0);
        return give(player, new Handset(UUID.randomUUID(), entry.id(), entry.id(), player.getUuid(), world.getRegistryKey(), anchor, false),
                entry.pos());
    }

    private static boolean give(ServerPlayerEntity player, Handset handset, BlockPos soundPos) {
        Handset existing = BY_ANCHOR.get(handset.anchorId());

        if (existing != null) {
            ServerPlayerEntity holder = player.getServer().getPlayerManager().getPlayer(existing.holder());
            Text name = holder == null ? Text.literal("?") : holder.getDisplayName();

            CommsMessages.actionBar(player, Text.translatable("tardis.message.comms.handset_taken", name).formatted(Formatting.GRAY));
            return false;
        }

        ItemStack phone = PhoneItem.create(handset);

        if (player.getMainHandStack().isEmpty()) {
            player.setStackInHand(Hand.MAIN_HAND, phone);
        } else if (!player.getInventory().insertStack(phone)) {
            CommsMessages.actionBar(player, Text.translatable("tardis.message.comms.no_room").formatted(Formatting.RED));
            return false;
        }

        BY_ANCHOR.put(handset.anchorId(), handset);
        BY_SESSION.put(handset.session(), handset);

        player.getServerWorld().playSound(null, soundPos, SoundEvents.ITEM_ARMOR_EQUIP_CHAIN, SoundCategory.BLOCKS, 0.8f, 1.4f);
        return true;
    }

    public static boolean hangUp(ServerPlayerEntity player, ItemStack stack, boolean endCall) {
        UUID session = PhoneItem.session(stack);
        Handset handset = session == null ? null : BY_SESSION.get(session);

        if (handset == null || !handset.holder().equals(player.getUuid()))
            return false;

        boolean ending = endCall && TardisComms.status(handset.line()) != TardisComms.Status.IDLE;
        BlockPos cradle = BlockPos.ofFloored(handset.anchor());

        release(player.getServer(), handset, endCall);
        player.getServerWorld().playSound(null, cradle, SoundEvents.ITEM_ARMOR_EQUIP_CHAIN, SoundCategory.BLOCKS, 0.8f, 0.9f);

        if (ending)
            player.getServerWorld().playSound(null, cradle, SoundEvents.BLOCK_LEVER_CLICK, SoundCategory.BLOCKS, 0.6f, 1.6f);
        return true;
    }

    public static void releaseAnchor(MinecraftServer server, UUID anchorId) {
        Handset handset = BY_ANCHOR.get(anchorId);

        if (handset != null)
            release(server, handset, false);
    }

    public static void releaseLine(MinecraftServer server, UUID line) {
        for (Handset handset : BY_SESSION.values()) {
            if (handset.line().equals(line))
                release(server, handset, true);
        }
    }

    private static void tick(MinecraftServer server) {
        for (Handset handset : BY_SESSION.values()) {
            ServerWorld world = server.getWorld(handset.world());

            if (world == null) {
                release(server, handset, true);
                continue;
            }

            ServerPlayerEntity holder = server.getPlayerManager().getPlayer(handset.holder());

            if (holder == null || !hasPhone(holder, handset.session()))
                holder = findCarrier(server, handset);

            if (holder != null) {
                SPEAKERS.remove(handset.line());
                ITEMS.remove(handset.session());

                if (holder.getServerWorld() != world) {
                    release(server, handset, true);
                    continue;
                }

                if (handset.dropped() || !handset.holder().equals(holder.getUuid()))
                    replace(handset, handset.held(holder.getUuid()));

                if (tether(server, handset, holder))
                    holder.velocityModified = true;

                continue;
            }

            ItemEntity item = findItem(world, handset);

            if (item == null) {
                release(server, handset, true);
                continue;
            }

            if (!handset.dropped())
                replace(handset, handset.onGround());

            item.setNeverDespawn();
            ITEMS.put(handset.session(), item.getUuid());
            SPEAKERS.put(handset.line(), new Speaker(handset.world(), item.getPos().add(0, 0.25, 0)));

            if (tether(server, handset, item))
                item.velocityModified = true;
        }
    }

    private static boolean tether(MinecraftServer server, Handset handset, Entity entity) {
        Vec3d offset = handset.anchor().subtract(entity.getPos());
        double distance = offset.length();

        if (distance > SNAP_LENGTH) {
            release(server, handset, true);
            return false;
        }

        if (distance <= CABLE_LENGTH)
            return false;

        Vec3d pull = offset.normalize().multiply(Math.min((distance - CABLE_LENGTH) * PULL_STRENGTH, MAX_PULL));
        entity.addVelocity(pull.x, pull.y * 0.5, pull.z);
        return true;
    }

    @Nullable private static ServerPlayerEntity findCarrier(MinecraftServer server, Handset handset) {
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            if (hasPhone(player, handset.session()))
                return player;
        }

        return null;
    }

    @Nullable private static ItemEntity findItem(ServerWorld world, Handset handset) {
        UUID known = ITEMS.get(handset.session());

        if (known != null && world.getEntity(known) instanceof ItemEntity item && !item.isRemoved()
                && handset.session().equals(PhoneItem.session(item.getStack())))
            return item;

        Box area = new Box(BlockPos.ofFloored(handset.anchor())).expand(STRAY_SEARCH);

        return world.getEntitiesByClass(ItemEntity.class, area,
                candidate -> handset.session().equals(PhoneItem.session(candidate.getStack()))).stream().findFirst().orElse(null);
    }

    private static void release(MinecraftServer server, Handset stale, boolean endCall) {
        Handset handset = BY_SESSION.getOrDefault(stale.session(), stale);

        BY_SESSION.remove(handset.session());
        BY_ANCHOR.computeIfPresent(handset.anchorId(), (id, current) -> current.session().equals(handset.session()) ? null : current);
        ITEMS.remove(handset.session());
        SPEAKERS.remove(handset.line());

        if (server == null)
            return;

        ServerPlayerEntity holder = server.getPlayerManager().getPlayer(handset.holder());

        if (holder != null) {
            removePhones(holder.getInventory(), handset.session());

            if (handset.session().equals(PhoneItem.session(holder.currentScreenHandler.getCursorStack())))
                holder.currentScreenHandler.setCursorStack(ItemStack.EMPTY);
        }

        ServerWorld world = server.getWorld(handset.world());

        if (world != null) {
            Box area = new Box(BlockPos.ofFloored(handset.anchor())).expand(STRAY_SEARCH);

            world.getEntitiesByClass(ItemEntity.class, area,
                    item -> handset.session().equals(PhoneItem.session(item.getStack()))).forEach(ItemEntity::discard);
        }

        if (CommsLine.resolve(server, handset.line()) instanceof CommsLine.Tardis line) {
            ServerTardis tardis = line.tardis();

            CommsHandler.of(tardis).handset().set(false);
            TardisComms.markDirty(tardis.getUuid());
        }

        if (endCall)
            TardisComms.endFor(server, handset.line());
    }

    private static boolean hasPhone(ServerPlayerEntity player, UUID session) {
        if (session.equals(PhoneItem.session(player.currentScreenHandler.getCursorStack())))
            return true;

        PlayerInventory inventory = player.getInventory();

        for (int i = 0; i < inventory.size(); i++) {
            if (session.equals(PhoneItem.session(inventory.getStack(i))))
                return true;
        }

        return false;
    }

    private static void removePhones(PlayerInventory inventory, UUID session) {
        for (int i = 0; i < inventory.size(); i++) {
            if (session.equals(PhoneItem.session(inventory.getStack(i))))
                inventory.setStack(i, ItemStack.EMPTY);
        }
    }
}
