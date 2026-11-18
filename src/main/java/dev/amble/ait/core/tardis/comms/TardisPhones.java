package dev.amble.ait.core.tardis.comms;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import dev.amble.ait.core.entities.ConsoleControlEntity;
import dev.amble.ait.core.item.PhoneItem;
import dev.amble.ait.core.tardis.ServerTardis;
import dev.amble.ait.core.tardis.control.impl.CommsControl;
import dev.amble.ait.core.tardis.manager.ServerTardisManager;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import org.jetbrains.annotations.Nullable;

import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
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

public final class TardisPhones {

    public static final double CABLE_LENGTH = 8;

    private static final double SNAP_LENGTH = CABLE_LENGTH * 2;
    private static final double PULL_STRENGTH = 0.15;
    private static final double MAX_PULL = 1.0;
    private static final double STRAY_SEARCH = 48;

    public record Handset(UUID session, UUID tardis, UUID control, UUID holder, Vec3d anchor) {
    }

    private static final Map<UUID, Handset> BY_CONTROL = new ConcurrentHashMap<>();
    private static final Map<UUID, Handset> BY_SESSION = new ConcurrentHashMap<>();

    private TardisPhones() {
    }

    public static void init() {
        ServerTickEvents.END_SERVER_TICK.register(TardisPhones::tick);
    }

    public static boolean isActive(UUID session, UUID holder) {
        Handset handset = BY_SESSION.get(session);
        return handset != null && handset.holder().equals(holder);
    }

    @Nullable public static Handset get(UUID session) {
        return BY_SESSION.get(session);
    }

    @Nullable public static UUID holderFor(UUID tardis) {
        for (Handset handset : BY_SESSION.values()) {
            if (handset.tardis().equals(tardis))
                return handset.holder();
        }

        return null;
    }

    public static void lift(ServerPlayerEntity player, ServerTardis tardis, ServerWorld world, BlockPos console) {
        ConsoleControlEntity control = findControl(world, console, player);

        if (control == null)
            return;

        Handset existing = BY_CONTROL.get(control.getUuid());

        if (existing != null) {
            ServerPlayerEntity holder = world.getServer().getPlayerManager().getPlayer(existing.holder());
            Text name = holder == null ? Text.literal("?") : holder.getDisplayName();

            player.sendMessage(Text.translatable("tardis.message.comms.handset_taken", name).formatted(Formatting.GRAY), true);
            return;
        }

        Handset handset = new Handset(UUID.randomUUID(), tardis.getUuid(), control.getUuid(), player.getUuid(),
                control.getPos().add(0, control.getHeight() / 2, 0));
        ItemStack phone = PhoneItem.create(handset);

        if (player.getMainHandStack().isEmpty()) {
            player.setStackInHand(Hand.MAIN_HAND, phone);
        } else if (!player.getInventory().insertStack(phone)) {
            player.sendMessage(Text.translatable("tardis.message.comms.no_room").formatted(Formatting.RED), true);
            return;
        }

        BY_CONTROL.put(handset.control(), handset);
        BY_SESSION.put(handset.session(), handset);

        world.playSound(null, control.getBlockPos(), SoundEvents.ITEM_ARMOR_EQUIP_CHAIN, SoundCategory.BLOCKS, 0.8f, 1.4f);
    }

    public static boolean hangUp(ServerPlayerEntity player, ItemStack stack) {
        UUID session = PhoneItem.session(stack);
        Handset handset = session == null ? null : BY_SESSION.get(session);

        if (handset == null || !handset.holder().equals(player.getUuid()))
            return false;

        release(player.getServer(), handset);
        player.getServerWorld().playSound(null, BlockPos.ofFloored(handset.anchor()), SoundEvents.ITEM_ARMOR_EQUIP_CHAIN,
                SoundCategory.BLOCKS, 0.8f, 0.9f);
        return true;
    }

    private static void tick(MinecraftServer server) {
        for (Handset handset : BY_SESSION.values()) {
            ServerPlayerEntity holder = server.getPlayerManager().getPlayer(handset.holder());
            ServerTardis tardis = find(handset.tardis());

            if (holder == null || holder.isDead() || tardis == null || !tardis.hasWorld()
                    || holder.getServerWorld() != tardis.world() || !hasPhone(holder, handset.session())) {
                release(server, handset);
                continue;
            }

            Vec3d offset = handset.anchor().subtract(holder.getPos());
            double distance = offset.length();

            if (distance > SNAP_LENGTH) {
                release(server, handset);
                continue;
            }

            if (distance > CABLE_LENGTH) {
                Vec3d pull = offset.normalize().multiply(Math.min((distance - CABLE_LENGTH) * PULL_STRENGTH, MAX_PULL));

                holder.addVelocity(pull.x, pull.y * 0.5, pull.z);
                holder.velocityModified = true;
            }
        }
    }

    private static void release(MinecraftServer server, Handset handset) {
        BY_CONTROL.remove(handset.control(), handset);
        BY_SESSION.remove(handset.session(), handset);

        if (server == null)
            return;

        ServerPlayerEntity holder = server.getPlayerManager().getPlayer(handset.holder());

        if (holder != null) {
            removePhones(holder.getInventory(), handset.session());

            if (handset.session().equals(PhoneItem.session(holder.currentScreenHandler.getCursorStack())))
                holder.currentScreenHandler.setCursorStack(ItemStack.EMPTY);
        }

        ServerTardis tardis = find(handset.tardis());

        if (tardis == null)
            return;

        if (tardis.hasWorld()) {
            Box area = new Box(BlockPos.ofFloored(handset.anchor())).expand(STRAY_SEARCH);

            tardis.world().getEntitiesByClass(ItemEntity.class, area,
                    item -> handset.session().equals(PhoneItem.session(item.getStack()))).forEach(ItemEntity::discard);
        }

        TardisComms.endFor(server, handset.tardis());
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

    @Nullable private static ServerTardis find(UUID uuid) {
        ServerTardis[] found = new ServerTardis[1];

        ServerTardisManager.getInstance().forEach(tardis -> {
            if (tardis.getUuid().equals(uuid))
                found[0] = tardis;
        });

        return found[0];
    }
}
