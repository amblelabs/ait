package dev.amble.ait.core.tardis.comms;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import dev.amble.ait.AITMod;
import dev.amble.ait.compat.DependencyChecker;
import dev.amble.ait.core.blockentities.RotaryPhoneBlockEntity;
import dev.amble.ait.core.item.PhoneItem;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import org.jetbrains.annotations.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public final class RotaryPhones {

    public static final Identifier OPEN = AITMod.id("rotary_phone_open");
    public static final Identifier DIAL = AITMod.id("rotary_phone_dial");

    public static final int CALL_COOLDOWN = 200;

    private static final double REACH = 8;

    private static final Map<UUID, Long> LAST_CALL = new ConcurrentHashMap<>();
    private static Set<UUID> displayed = new HashSet<>();

    private RotaryPhones() {
    }

    public static void init() {
        ServerTickEvents.END_SERVER_TICK.register(RotaryPhones::tick);

        ServerPlayNetworking.registerGlobalReceiver(DIAL, (server, player, handler, buf, responseSender) -> {
            BlockPos pos = buf.readBlockPos();
            String number = buf.readString(32);

            server.execute(() -> dial(player, pos, number));
        });
    }

    public static void use(ServerPlayerEntity player, ServerWorld world, RotaryPhoneBlockEntity phone) {
        PhoneDirectory.Entry entry = lineFor(world, phone);

        if (player.isSneaking()) {
            CommsMessages.actionBar(player, Text.translatable("tardis.message.phone.number",
                    PhoneDirectory.format(entry.number())).formatted(Formatting.GOLD));
            return;
        }

        if (!DependencyChecker.hasVoiceChat()) {
            CommsMessages.actionBar(player, Text.translatable("tardis.message.phone.no_voice_chat").formatted(Formatting.RED));
            return;
        }

        ItemStack held = player.getMainHandStack();
        UUID session = PhoneItem.session(held);
        TardisPhones.Handset handset = session == null ? null : TardisPhones.get(session);

        if (handset != null && handset.line().equals(entry.id())) {
            TardisPhones.hangUp(player, held, true);
            return;
        }

        if (TardisComms.status(entry.id()) == TardisComms.Status.INCOMING) {
            if (TardisPhones.liftPhone(player, world, entry))
                TardisComms.answer(world.getServer(), player, entry.id());

            return;
        }

        UUID holder = TardisPhones.holderFor(entry.id());

        if (holder != null || TardisComms.status(entry.id()) != TardisComms.Status.IDLE) {
            ServerPlayerEntity other = holder == null ? null : world.getServer().getPlayerManager().getPlayer(holder);
            Text name = other == null ? Text.literal("?") : other.getDisplayName();

            CommsMessages.actionBar(player, Text.translatable("tardis.message.comms.handset_taken", name).formatted(Formatting.GRAY));
            return;
        }

        open(player, phone, entry);
    }

    private static void open(ServerPlayerEntity player, RotaryPhoneBlockEntity phone, PhoneDirectory.Entry entry) {
        PacketByteBuf buf = PacketByteBufs.create();

        buf.writeBlockPos(phone.getPos());
        buf.writeString(entry.number());
        buf.writeVarInt(cooldown(player.getServer(), entry.id()));
        buf.writeVarInt(phone.recents().size());

        for (RotaryPhoneBlockEntity.Recent recent : phone.recents()) {
            buf.writeString(recent.number());
            buf.writeString(recent.label());
            buf.writeBoolean(recent.outgoing());
        }

        ServerPlayNetworking.send(player, OPEN, buf);
    }

    private static void dial(ServerPlayerEntity player, BlockPos pos, String input) {
        ServerWorld world = player.getServerWorld();
        MinecraftServer server = world.getServer();

        if (player.squaredDistanceTo(Vec3d.ofCenter(pos)) > REACH * REACH
                || !(world.getBlockEntity(pos) instanceof RotaryPhoneBlockEntity phone))
            return;

        PhoneDirectory.Entry entry = lineFor(world, phone);

        if (TardisComms.status(entry.id()) != TardisComms.Status.IDLE || TardisPhones.holderFor(entry.id()) != null)
            return;

        int cooldown = cooldown(server, entry.id());

        if (cooldown > 0) {
            CommsMessages.actionBar(player, Text.translatable("tardis.message.phone.cooldown",
                    (cooldown + 19) / 20).formatted(Formatting.RED));
            return;
        }

        UUID target = PhoneDirectory.get(server).lookup(input);

        if (target == null || target.equals(entry.id())) {
            CommsMessages.actionBar(player, Text.translatable("tardis.message.phone.no_such_number",
                    PhoneDirectory.format(PhoneDirectory.normalize(input))).formatted(Formatting.RED));
            return;
        }

        if (!TardisPhones.liftPhone(player, world, entry))
            return;

        CommsLine line = CommsLine.resolve(server, entry.id());

        if (line != null && TardisComms.dial(server, player, line, target))
            LAST_CALL.put(entry.id(), (long) server.getTicks());
    }

    private static int cooldown(MinecraftServer server, UUID line) {
        Long last = LAST_CALL.get(line);

        if (last == null)
            return 0;

        return (int) Math.max(0, CALL_COOLDOWN - (server.getTicks() - last));
    }

    public static PhoneDirectory.Entry lineFor(ServerWorld world, RotaryPhoneBlockEntity phone) {
        PhoneDirectory directory = PhoneDirectory.get(world.getServer());
        UUID id = phone.line();
        PhoneDirectory.Entry entry = id == null ? null : directory.phone(id);

        if (entry != null && entry.world().equals(world.getRegistryKey()) && entry.pos().equals(phone.getPos()))
            return entry;

        entry = directory.register(world.getRegistryKey(), phone.getPos());
        phone.setLine(entry.id());

        return entry;
    }

    public static void removed(ServerWorld world, BlockPos pos, @Nullable UUID line) {
        if (line == null)
            return;

        MinecraftServer server = world.getServer();
        PhoneDirectory directory = PhoneDirectory.get(server);
        PhoneDirectory.Entry entry = directory.phone(line);

        if (entry == null || !entry.world().equals(world.getRegistryKey()) || !entry.pos().equals(pos))
            return;

        TardisPhones.releaseLine(server, line);
        TardisComms.endFor(server, line);
        directory.unregister(line);
        LAST_CALL.remove(line);
    }

    @Nullable public static RotaryPhoneBlockEntity blockEntity(ServerWorld world, PhoneDirectory.Entry entry) {
        if (!world.isChunkLoaded(entry.pos()))
            return null;

        return world.getBlockEntity(entry.pos()) instanceof RotaryPhoneBlockEntity phone && entry.id().equals(phone.line())
                ? phone : null;
    }

    private static void tick(MinecraftServer server) {
        Set<UUID> ringing = TardisComms.ringingLines();
        Set<UUID> lifted = TardisPhones.liftedLines();
        Set<UUID> active = new HashSet<>(ringing);
        active.addAll(lifted);

        Set<UUID> touched = new HashSet<>(displayed);
        touched.addAll(active);

        PhoneDirectory directory = PhoneDirectory.get(server);

        for (UUID id : touched) {
            PhoneDirectory.Entry entry = directory.phone(id);

            if (entry == null)
                continue;

            ServerWorld world = server.getWorld(entry.world());
            RotaryPhoneBlockEntity phone = world == null ? null : blockEntity(world, entry);

            if (phone != null)
                phone.setDisplay(ringing.contains(id), lifted.contains(id));
        }

        displayed = active;

        if (server.getTicks() % 1200 == 0)
            LAST_CALL.values().removeIf(time -> server.getTicks() - time >= CALL_COOLDOWN);
    }
}
