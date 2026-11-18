package dev.amble.ait.core.tardis.comms;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import dev.amble.ait.api.tardis.TardisEvents;
import dev.amble.ait.core.entities.ConsoleControlEntity;
import dev.amble.ait.core.item.PhoneItem;
import dev.amble.ait.core.tardis.ServerTardis;
import dev.amble.ait.core.tardis.control.impl.CommsControl;
import dev.amble.lib.util.ServerLifecycleHooks;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import org.jetbrains.annotations.Nullable;

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

    public record Handset(UUID session, UUID line, UUID anchorId, UUID holder, RegistryKey<World> world, Vec3d anchor) {
    }

    private static final Map<UUID, Handset> BY_ANCHOR = new ConcurrentHashMap<>();
    private static final Map<UUID, Handset> BY_SESSION = new ConcurrentHashMap<>();

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
        for (Handset handset : BY_SESSION.values()) {
            if (handset.line().equals(line))
                return handset.holder();
        }

        return null;
    }

    public static Set<UUID> liftedLines() {
        Set<UUID> lines = new HashSet<>();

        for (Handset handset : BY_SESSION.values())
            lines.add(handset.line());

        return lines;
    }

    public static void lift(ServerPlayerEntity player, ServerTardis tardis, ServerWorld world, BlockPos console) {
        ConsoleControlEntity control = findControl(world, console, player);

        if (control == null)
            return;

        if (give(player, new Handset(UUID.randomUUID(), tardis.getUuid(), control.getUuid(), player.getUuid(),
                world.getRegistryKey(), control.getPos().add(0, control.getHeight() / 2, 0)), control.getBlockPos()))
            tardis.stats().commsHandset().set(true);
    }

    public static boolean liftPhone(ServerPlayerEntity player, ServerWorld world, PhoneDirectory.Entry entry) {
        Vec3d anchor = Vec3d.ofBottomCenter(entry.pos()).add(0, 0.3, 0);
        return give(player, new Handset(UUID.randomUUID(), entry.id(), entry.id(), player.getUuid(), world.getRegistryKey(), anchor),
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

        release(player.getServer(), handset, endCall);
        player.getServerWorld().playSound(null, BlockPos.ofFloored(handset.anchor()), SoundEvents.ITEM_ARMOR_EQUIP_CHAIN,
                SoundCategory.BLOCKS, 0.8f, 0.9f);
        return true;
    }

    public static void releaseLine(MinecraftServer server, UUID line) {
        for (Handset handset : BY_SESSION.values()) {
            if (handset.line().equals(line))
                release(server, handset, true);
        }
    }

    private static void tick(MinecraftServer server) {
        for (Handset handset : BY_SESSION.values()) {
            ServerPlayerEntity holder = server.getPlayerManager().getPlayer(handset.holder());

            if (holder == null || holder.isDead() || holder.getServerWorld().getRegistryKey() != handset.world()
                    || !hasPhone(holder, handset.session())) {
                release(server, handset, true);
                continue;
            }

            Vec3d offset = handset.anchor().subtract(holder.getPos());
            double distance = offset.length();

            if (distance > SNAP_LENGTH) {
                release(server, handset, true);
                continue;
            }

            if (distance > CABLE_LENGTH) {
                Vec3d pull = offset.normalize().multiply(Math.min((distance - CABLE_LENGTH) * PULL_STRENGTH, MAX_PULL));

                holder.addVelocity(pull.x, pull.y * 0.5, pull.z);
                holder.velocityModified = true;
            }
        }
    }

    private static void release(MinecraftServer server, Handset handset, boolean endCall) {
        BY_ANCHOR.remove(handset.anchorId(), handset);
        BY_SESSION.remove(handset.session(), handset);

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

        ServerTardis tardis = CommsLine.findTardis(handset.line());

        if (tardis != null)
            tardis.stats().commsHandset().set(false);

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

    @Nullable private static ConsoleControlEntity findControl(ServerWorld world, BlockPos console, ServerPlayerEntity player) {
        return world.getEntitiesByClass(ConsoleControlEntity.class, new Box(console).expand(4),
                        entity -> entity.getControl() instanceof CommsControl && console.equals(entity.getConsoleBlockPos()))
                .stream()
                .min((a, b) -> Double.compare(a.squaredDistanceTo(player), b.squaredDistanceTo(player)))
                .orElse(null);
    }
}
