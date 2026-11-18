package dev.amble.ait.core.tardis.comms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import dev.amble.ait.AITMod;
import dev.amble.ait.api.tardis.TardisEvents;
import dev.amble.ait.core.AITSounds;
import dev.amble.ait.core.blockentities.RotaryPhoneBlockEntity;
import dev.amble.ait.core.tardis.ServerTardis;
import dev.amble.ait.core.tardis.control.impl.SecurityControl;
import dev.amble.ait.core.tardis.manager.ServerTardisManager;
import dev.amble.lib.util.ServerLifecycleHooks;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import org.jetbrains.annotations.Nullable;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

public final class TardisComms {

    public static final Identifier REQUEST = AITMod.id("comms_request");
    public static final Identifier STATE = AITMod.id("comms_state");
    public static final Identifier ACTION = AITMod.id("comms_action");
    public static final Identifier DIAL = AITMod.id("comms_dial");

    public static final int RING_TICKS = 600;
    public static final int FRAMES_PER_SECOND = 50;
    public static final int MAX_MESSAGE_FRAMES = 30 * FRAMES_PER_SECOND;
    public static final int MAX_MESSAGES = 10;

    private static final int RING_INTERVAL = 65;
    private static final int RECORDING_TICKS = 32 * 20;

    public enum Action {
        CALL, ANSWER, DECLINE, HANG_UP, PLAY, DELETE, TOGGLE_MODE
    }

    public enum Status {
        IDLE, OUTGOING, INCOMING, CONNECTED, RECORDING
    }

    @FunctionalInterface
    public interface Playback {
        void play(List<UUID> listeners, List<byte[]> frames);
    }

    public record VoiceMessage(UUID id, String from, long time, List<byte[]> frames) {
    }

    private static final class Call {
        private final UUID caller;
        private final UUID target;
        private final UUID callerPlayer;
        private UUID answerer;
        private boolean connected;
        private int ticks;

        private Call(UUID caller, UUID target, UUID callerPlayer) {
            this.caller = caller;
            this.target = target;
            this.callerPlayer = callerPlayer;
        }
    }

    private static final class Recording {
        private final UUID callerLine;
        private final UUID target;
        private final String from;
        private final List<byte[]> frames = Collections.synchronizedList(new ArrayList<>());
        private int ticks;

        private Recording(UUID callerLine, UUID target, String from) {
            this.callerLine = callerLine;
            this.target = target;
            this.from = from;
        }
    }

    public static Playback PLAYBACK = (listeners, frames) -> { };

    private static final Map<UUID, Call> CALLS = new ConcurrentHashMap<>();
    private static final Map<UUID, Recording> RECORDINGS = new ConcurrentHashMap<>();
    private static final Map<UUID, List<VoiceMessage>> MESSAGES = new ConcurrentHashMap<>();
    private static final Map<UUID, List<UUID>> ROUTES = new ConcurrentHashMap<>();
    private static final Map<UUID, List<TardisPhones.Speaker>> SPEAKER_ROUTES = new ConcurrentHashMap<>();

    private TardisComms() {
    }

    public static void init() {
        ServerTickEvents.END_SERVER_TICK.register(TardisComms::tick);

        TardisEvents.RECONFIGURE_DESKTOP.register(tardis -> {
            MinecraftServer server = ServerLifecycleHooks.get();

            if (server != null)
                endFor(server, tardis.getUuid());
        });

        ServerPlayNetworking.registerGlobalReceiver(REQUEST, ServerTardisManager.receiveTardis(
                SecurityControl.withLoyaltyCheck((tardis, server, player, handler, buf, responseSender) ->
                        server.execute(() -> sendState(server, player, tardis)))));

        ServerPlayNetworking.registerGlobalReceiver(ACTION, ServerTardisManager.receiveTardis(
                SecurityControl.withLoyaltyCheck((tardis, server, player, handler, buf, responseSender) -> {
                    Action action = buf.readEnumConstant(Action.class);
                    UUID argument = buf.readBoolean() ? buf.readUuid() : null;

                    server.execute(() -> {
                        handle(server, player, tardis, action, argument);
                        sendState(server, player, tardis);
                    });
                })));

        ServerPlayNetworking.registerGlobalReceiver(DIAL, ServerTardisManager.receiveTardis(
                SecurityControl.withLoyaltyCheck((tardis, server, player, handler, buf, responseSender) -> {
                    String number = buf.readString(32);

                    server.execute(() -> {
                        dialNumber(server, player, tardis, number);
                        sendState(server, player, tardis);
                    });
                })));
    }

    public static List<UUID> routeFor(UUID player) {
        return ROUTES.getOrDefault(player, List.of());
    }

    public static List<TardisPhones.Speaker> speakersFor(UUID player) {
        return SPEAKER_ROUTES.getOrDefault(player, List.of());
    }

    public static void record(UUID player, byte[] opus) {
        Recording recording = RECORDINGS.get(player);

        if (recording == null || opus == null || opus.length == 0 || recording.frames.size() >= MAX_MESSAGE_FRAMES)
            return;

        recording.frames.add(opus.clone());
    }

    private static void handle(MinecraftServer server, ServerPlayerEntity player, ServerTardis tardis, Action action, @Nullable UUID argument) {
        switch (action) {
            case CALL -> call(server, player, new CommsLine.Tardis(server, tardis), argument);
            case ANSWER -> answer(server, player, tardis.getUuid());
            case DECLINE -> decline(server, tardis.getUuid());
            case HANG_UP -> hangUp(server, player, tardis.getUuid());
            case PLAY -> play(server, player, tardis, argument);
            case DELETE -> {
                List<VoiceMessage> messages = MESSAGES.get(tardis.getUuid());

                if (messages != null && argument != null)
                    messages.removeIf(message -> message.id().equals(argument));
            }
            case TOGGLE_MODE -> tardis.stats().commsHandset().toggle();
        }
    }

    private static void dialNumber(MinecraftServer server, ServerPlayerEntity player, ServerTardis tardis, String input) {
        String number = PhoneDirectory.normalize(input);
        UUID target = PhoneDirectory.get(server).lookup(number);

        if (target == null || target.equals(tardis.getUuid())) {
            CommsMessages.actionBar(player, Text.translatable("tardis.message.phone.no_such_number",
                    PhoneDirectory.format(number)).formatted(Formatting.RED));
            return;
        }

        call(server, player, new CommsLine.Tardis(server, tardis), target);
    }

    public static boolean dial(MinecraftServer server, ServerPlayerEntity player, CommsLine from, UUID target) {
        return call(server, player, from, target);
    }

    private static boolean call(MinecraftServer server, ServerPlayerEntity player, CommsLine from, @Nullable UUID targetId) {
        if (targetId == null || targetId.equals(from.id()) || isBusy(from.id()))
            return false;

        CommsLine target = CommsLine.resolve(server, targetId);
        PhoneDirectory directory = PhoneDirectory.get(server);

        if (from instanceof CommsLine.Tardis && target instanceof CommsLine.Phone && !directory.canCallBack(from.id(), targetId)) {
            CommsMessages.actionBar(player, Text.translatable("tardis.message.comms.no_callback").formatted(Formatting.RED));
            return false;
        }

        if (target == null || !target.accepts()) {
            CommsMessages.actionBar(player, Text.translatable("tardis.message.comms.unavailable").formatted(Formatting.RED));
            return false;
        }

        if (isBusy(targetId)) {
            CommsMessages.actionBar(player, Text.translatable("tardis.message.comms.busy").formatted(Formatting.RED));
            return false;
        }

        Call call = new Call(from.id(), targetId, player.getUuid());
        CALLS.put(call.caller, call);
        CALLS.put(call.target, call);

        if (from instanceof CommsLine.Phone && target instanceof CommsLine.Tardis)
            directory.noteCallback(targetId, from.id());

        noteRecents(server, from, target);

        CommsMessages.actionBar(player, Text.translatable("tardis.message.comms.calling", target.name()).formatted(Formatting.GOLD));
        target.ring();
        return true;
    }

    private static void noteRecents(MinecraftServer server, CommsLine from, CommsLine target) {
        if (from instanceof CommsLine.Phone phone)
            addRecent(phone, new RotaryPhoneBlockEntity.Recent(number(server, target), label(target), true));

        if (target instanceof CommsLine.Phone phone)
            addRecent(phone, new RotaryPhoneBlockEntity.Recent(number(server, from), label(from), false));
    }

    private static void addRecent(CommsLine.Phone phone, RotaryPhoneBlockEntity.Recent recent) {
        RotaryPhoneBlockEntity entity = RotaryPhones.blockEntity(phone.world(), phone.entry());

        if (entity != null)
            entity.addRecent(recent);
    }

    private static String number(MinecraftServer server, CommsLine line) {
        PhoneDirectory directory = PhoneDirectory.get(server);

        if (line instanceof CommsLine.Tardis)
            return directory.tardisNumber(line.id());

        String number = directory.number(line.id());
        return number == null ? "" : number;
    }

    private static String label(CommsLine line) {
        return line instanceof CommsLine.Tardis ? line.name() : "";
    }

    public static void answer(MinecraftServer server, ServerPlayerEntity player, UUID line) {
        Call call = CALLS.get(line);

        if (call == null || call.connected || !call.target.equals(line))
            return;

        call.answerer = player.getUuid();
        call.connected = true;

        notifyBoth(server, call, Text.translatable("tardis.message.comms.connected").formatted(Formatting.GREEN));
    }

    private static void decline(MinecraftServer server, UUID line) {
        Call call = CALLS.get(line);

        if (call == null || call.connected || !call.target.equals(line))
            return;

        toVoicemail(server, call);
    }

    public static void endFor(MinecraftServer server, UUID line) {
        for (Map.Entry<UUID, Recording> entry : new HashMap<>(RECORDINGS).entrySet()) {
            if (entry.getValue().callerLine.equals(line))
                finishRecording(server, entry.getKey(), entry.getValue());
        }

        Call call = CALLS.get(line);

        if (call != null)
            end(server, call);
    }

    private static void hangUp(MinecraftServer server, ServerPlayerEntity player, UUID line) {
        Recording recording = RECORDINGS.get(player.getUuid());

        if (recording != null && recording.callerLine.equals(line)) {
            finishRecording(server, player.getUuid(), recording);
            return;
        }

        Call call = CALLS.get(line);

        if (call != null)
            end(server, call);
    }

    private static void play(MinecraftServer server, ServerPlayerEntity player, ServerTardis tardis, @Nullable UUID messageId) {
        List<VoiceMessage> messages = MESSAGES.get(tardis.getUuid());

        if (messages == null || messageId == null)
            return;

        for (VoiceMessage message : messages) {
            if (!message.id().equals(messageId))
                continue;

            List<UUID> listeners = tardis.stats().commsHandset().get()
                    ? List.of(player.getUuid())
                    : new CommsLine.Tardis(server, tardis).interiorPlayers();

            PLAYBACK.play(listeners, message.frames());
            return;
        }
    }

    private static void tick(MinecraftServer server) {
        for (Call call : new HashSet<>(CALLS.values()))
            tickCall(server, call);

        RECORDINGS.entrySet().removeIf(entry -> {
            Recording recording = entry.getValue();

            if (++recording.ticks < RECORDING_TICKS && recording.frames.size() < MAX_MESSAGE_FRAMES
                    && server.getPlayerManager().getPlayer(entry.getKey()) != null)
                return false;

            store(server, recording);
            ServerPlayerEntity caller = server.getPlayerManager().getPlayer(entry.getKey());

            if (caller != null)
                CommsMessages.actionBar(caller, Text.translatable("tardis.message.comms.message_sent").formatted(Formatting.GREEN));

            return true;
        });

        Map<UUID, List<UUID>> routes = new HashMap<>();
        Map<UUID, List<TardisPhones.Speaker>> speakers = new HashMap<>();

        for (Call call : new HashSet<>(CALLS.values())) {
            if (!call.connected)
                continue;

            List<UUID> callerSide = participants(server, call.caller, call.callerPlayer);
            List<UUID> targetSide = participants(server, call.target, call.answerer);

            callerSide.forEach(player -> routes.put(player, targetSide));
            targetSide.forEach(player -> routes.put(player, callerSide));

            TardisPhones.Speaker callerSpeaker = TardisPhones.speakerFor(call.caller);
            TardisPhones.Speaker targetSpeaker = TardisPhones.speakerFor(call.target);

            if (targetSpeaker != null)
                callerSide.forEach(player -> speakers.computeIfAbsent(player, uuid -> new ArrayList<>()).add(targetSpeaker));

            if (callerSpeaker != null)
                targetSide.forEach(player -> speakers.computeIfAbsent(player, uuid -> new ArrayList<>()).add(callerSpeaker));
        }

        ROUTES.keySet().retainAll(routes.keySet());
        ROUTES.putAll(routes);

        SPEAKER_ROUTES.keySet().retainAll(speakers.keySet());
        SPEAKER_ROUTES.putAll(speakers);
    }

    private static void tickCall(MinecraftServer server, Call call) {
        CommsLine caller = CommsLine.resolve(server, call.caller);
        CommsLine target = CommsLine.resolve(server, call.target);

        if (caller == null || target == null || !caller.present(call.callerPlayer)) {
            end(server, call);
            return;
        }

        if (call.connected) {
            if (!target.present(call.answerer))
                end(server, call);

            return;
        }

        if (++call.ticks >= RING_TICKS) {
            toVoicemail(server, call);
            return;
        }

        if (call.ticks % RING_INTERVAL == 0)
            target.ring();
    }

    private static void toVoicemail(MinecraftServer server, Call call) {
        remove(call);

        ServerPlayerEntity caller = server.getPlayerManager().getPlayer(call.callerPlayer);
        CommsLine callerLine = CommsLine.resolve(server, call.caller);
        CommsLine target = CommsLine.resolve(server, call.target);

        if (target != null)
            target.notify(Text.translatable("tardis.message.comms.missed", name(callerLine)).formatted(Formatting.GRAY));

        if (caller == null)
            return;

        if (!(target instanceof CommsLine.Tardis)) {
            CommsMessages.actionBar(caller, Text.translatable("tardis.message.comms.no_answer").formatted(Formatting.GRAY));
            return;
        }

        RECORDINGS.put(call.callerPlayer, new Recording(call.caller, call.target, name(callerLine)));
        CommsMessages.actionBar(caller, Text.translatable("tardis.message.comms.leave_message").formatted(Formatting.GOLD));
        caller.playSound(AITSounds.DING, SoundCategory.PLAYERS, 1f, 1f);
    }

    private static void finishRecording(MinecraftServer server, UUID player, Recording recording) {
        RECORDINGS.remove(player);
        store(server, recording);

        ServerPlayerEntity caller = server.getPlayerManager().getPlayer(player);

        if (caller != null)
            CommsMessages.actionBar(caller, Text.translatable("tardis.message.comms.message_sent").formatted(Formatting.GREEN));
    }

    private static void store(MinecraftServer server, Recording recording) {
        List<byte[]> frames;

        synchronized (recording.frames) {
            frames = List.copyOf(recording.frames);
        }

        if (frames.isEmpty())
            return;

        List<VoiceMessage> messages = MESSAGES.computeIfAbsent(recording.target, uuid -> Collections.synchronizedList(new ArrayList<>()));
        messages.add(new VoiceMessage(UUID.randomUUID(), recording.from, System.currentTimeMillis(), frames));

        while (messages.size() > MAX_MESSAGES)
            messages.remove(0);

        CommsLine target = CommsLine.resolve(server, recording.target);

        if (target != null)
            target.notify(Text.translatable("tardis.message.comms.new_message", recording.from).formatted(Formatting.GOLD));
    }

    private static void end(MinecraftServer server, Call call) {
        if (!remove(call))
            return;

        notifyBoth(server, call, Text.translatable("tardis.message.comms.ended").formatted(Formatting.GRAY));
    }

    private static boolean remove(Call call) {
        boolean caller = CALLS.remove(call.caller, call);
        boolean target = CALLS.remove(call.target, call);
        return caller || target;
    }

    private static void notifyBoth(MinecraftServer server, Call call, Text text) {
        CommsLine caller = CommsLine.resolve(server, call.caller);
        CommsLine target = CommsLine.resolve(server, call.target);

        if (caller != null)
            caller.notify(text);

        if (target != null)
            target.notify(text);
    }

    private static List<UUID> participants(MinecraftServer server, UUID line, @Nullable UUID player) {
        CommsLine resolved = CommsLine.resolve(server, line);
        return resolved == null ? List.of() : resolved.participants(player);
    }

    private static boolean isBusy(UUID line) {
        if (CALLS.containsKey(line))
            return true;

        for (Recording recording : RECORDINGS.values()) {
            if (recording.callerLine.equals(line))
                return true;
        }

        return false;
    }

    public static Set<UUID> ringingLines() {
        Set<UUID> ringing = new HashSet<>();

        for (Call call : CALLS.values()) {
            if (!call.connected)
                ringing.add(call.target);
        }

        return ringing;
    }

    @Nullable public static UUID connectedPartner(UUID line) {
        Call call = CALLS.get(line);

        if (call == null || !call.connected)
            return null;

        return call.caller.equals(line) ? call.target : call.caller;
    }

    public static Status status(UUID line) {
        for (Recording recording : RECORDINGS.values()) {
            if (recording.callerLine.equals(line))
                return Status.RECORDING;
        }

        Call call = CALLS.get(line);

        if (call == null)
            return Status.IDLE;

        if (call.connected)
            return Status.CONNECTED;

        return call.caller.equals(line) ? Status.OUTGOING : Status.INCOMING;
    }

    @Nullable public static UUID other(UUID line) {
        for (Recording recording : RECORDINGS.values()) {
            if (recording.callerLine.equals(line))
                return recording.target;
        }

        Call call = CALLS.get(line);

        if (call == null)
            return null;

        return call.caller.equals(line) ? call.target : call.caller;
    }

    private static void sendState(MinecraftServer server, ServerPlayerEntity player, ServerTardis tardis) {
        PacketByteBuf buf = PacketByteBufs.create();
        UUID id = tardis.getUuid();
        UUID other = other(id);
        PhoneDirectory directory = PhoneDirectory.get(server);

        buf.writeEnumConstant(status(id));
        buf.writeString(other == null ? "" : name(CommsLine.resolve(server, other)));
        buf.writeBoolean(tardis.stats().commsHandset().get());
        buf.writeString(PhoneDirectory.format(directory.tardisNumber(id)));

        List<CommsLine> callable = new ArrayList<>();
        ServerTardisManager.getInstance().forEach(candidate -> {
            if (!candidate.getUuid().equals(id) && candidate.stats().receiveCalls().get())
                callable.add(new CommsLine.Tardis(server, candidate));
        });

        for (PhoneDirectory.Entry entry : directory.callbacks(id)) {
            CommsLine line = CommsLine.resolve(server, entry.id());

            if (line != null)
                callable.add(line);
        }

        buf.writeVarInt(callable.size());

        for (CommsLine candidate : callable) {
            buf.writeUuid(candidate.id());
            buf.writeString(candidate.name());
        }

        List<VoiceMessage> messages = MESSAGES.getOrDefault(id, List.of());
        List<VoiceMessage> snapshot;

        synchronized (messages) {
            snapshot = List.copyOf(messages);
        }

        buf.writeVarInt(snapshot.size());

        for (VoiceMessage message : snapshot) {
            buf.writeUuid(message.id());
            buf.writeString(message.from());
            buf.writeLong(message.time());
            buf.writeVarInt(message.frames().size() / FRAMES_PER_SECOND);
        }

        ServerPlayNetworking.send(player, STATE, buf);
    }

    public static String name(@Nullable CommsLine line) {
        return line == null ? "?" : line.name();
    }
}
