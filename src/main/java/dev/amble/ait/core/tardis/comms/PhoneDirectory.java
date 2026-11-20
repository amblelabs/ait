package dev.amble.ait.core.tardis.comms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.jetbrains.annotations.Nullable;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.PersistentState;
import net.minecraft.world.World;

public class PhoneDirectory extends PersistentState {

    private static final String ID = "ait_phone_directory";

    public static final int DIGITS = 7;
    public static final int MAX_CALLBACKS = 10;

    public record Entry(UUID id, String number, RegistryKey<World> world, BlockPos pos) {
    }

    private final Map<UUID, Entry> phones = new HashMap<>();
    private final Map<UUID, String> tardises = new HashMap<>();
    private final Map<String, UUID> numbers = new HashMap<>();
    private final Map<UUID, List<UUID>> callbacks = new HashMap<>();

    public static PhoneDirectory get(MinecraftServer server) {
        return server.getOverworld().getPersistentStateManager().getOrCreate(PhoneDirectory::read, PhoneDirectory::new, ID);
    }

    @Nullable public Entry phone(UUID id) {
        return this.phones.get(id);
    }

    @Nullable public UUID lookup(String number) {
        return this.numbers.get(normalize(number));
    }

    @Nullable public String number(UUID line) {
        Entry entry = this.phones.get(line);

        if (entry != null)
            return entry.number();

        return this.tardises.get(line);
    }

    public String tardisNumber(UUID tardis, Random random) {
        String number = this.tardises.get(tardis);

        if (number != null)
            return number;

        number = this.generate(random);
        this.tardises.put(tardis, number);
        this.numbers.put(number, tardis);
        this.markDirty();

        return number;
    }

    public Entry register(RegistryKey<World> world, BlockPos pos, Random random) {
        Entry entry = new Entry(UUID.randomUUID(), this.generate(random), world, pos.toImmutable());

        this.phones.put(entry.id(), entry);
        this.numbers.put(entry.number(), entry.id());
        this.markDirty();

        return entry;
    }

    public void unregister(UUID id) {
        Entry entry = this.phones.remove(id);

        if (entry == null)
            return;

        this.numbers.remove(entry.number(), id);
        this.callbacks.values().forEach(list -> list.remove(id));
        this.markDirty();
    }

    public void noteCallback(UUID tardis, UUID phone) {
        List<UUID> list = this.callbacks.computeIfAbsent(tardis, uuid -> new ArrayList<>());

        list.remove(phone);
        list.add(0, phone);

        while (list.size() > MAX_CALLBACKS)
            list.remove(list.size() - 1);

        this.markDirty();
    }

    public List<Entry> callbacks(UUID tardis) {
        List<Entry> result = new ArrayList<>();

        for (UUID id : this.callbacks.getOrDefault(tardis, List.of())) {
            Entry entry = this.phones.get(id);

            if (entry != null)
                result.add(entry);
        }

        return result;
    }

    private String generate(Random random) {
        String number;

        do {
            StringBuilder builder = new StringBuilder();
            builder.append(2 + random.nextInt(8));

            for (int i = 1; i < DIGITS; i++)
                builder.append(random.nextInt(10));

            number = builder.toString();
        } while (this.numbers.containsKey(number));

        return number;
    }

    public static String normalize(String input) {
        StringBuilder builder = new StringBuilder();

        for (char c : input.toCharArray()) {
            if (Character.isDigit(c) && builder.length() < DIGITS)
                builder.append(c);
        }

        return builder.toString();
    }

    public static String format(@Nullable String number) {
        if (number == null || number.length() != DIGITS)
            return number == null ? "" : number;

        return number.substring(0, 3) + "-" + number.substring(3);
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        NbtList phones = new NbtList();

        for (Entry entry : this.phones.values()) {
            NbtCompound tag = new NbtCompound();

            tag.putUuid("Id", entry.id());
            tag.putString("Number", entry.number());
            tag.putString("World", entry.world().getValue().toString());
            tag.put("Pos", NbtHelper.fromBlockPos(entry.pos()));
            phones.add(tag);
        }

        NbtList tardises = new NbtList();

        for (Map.Entry<UUID, String> entry : this.tardises.entrySet()) {
            NbtCompound tag = new NbtCompound();

            tag.putUuid("Id", entry.getKey());
            tag.putString("Number", entry.getValue());
            tardises.add(tag);
        }

        NbtList callbacks = new NbtList();

        for (Map.Entry<UUID, List<UUID>> entry : this.callbacks.entrySet()) {
            NbtCompound tag = new NbtCompound();
            NbtList list = new NbtList();

            for (UUID phone : entry.getValue())
                list.add(NbtHelper.fromUuid(phone));

            tag.putUuid("Tardis", entry.getKey());
            tag.put("Phones", list);
            callbacks.add(tag);
        }

        nbt.put("Phones", phones);
        nbt.put("Tardises", tardises);
        nbt.put("Callbacks", callbacks);

        return nbt;
    }

    private static PhoneDirectory read(NbtCompound nbt) {
        PhoneDirectory directory = new PhoneDirectory();

        for (NbtElement element : nbt.getList("Phones", NbtElement.COMPOUND_TYPE)) {
            NbtCompound tag = (NbtCompound) element;
            Identifier world = Identifier.tryParse(tag.getString("World"));

            if (world == null)
                continue;

            Entry entry = new Entry(tag.getUuid("Id"), tag.getString("Number"),
                    RegistryKey.of(RegistryKeys.WORLD, world), NbtHelper.toBlockPos(tag.getCompound("Pos")));

            directory.phones.put(entry.id(), entry);
            directory.numbers.put(entry.number(), entry.id());
        }

        for (NbtElement element : nbt.getList("Tardises", NbtElement.COMPOUND_TYPE)) {
            NbtCompound tag = (NbtCompound) element;
            UUID id = tag.getUuid("Id");
            String number = tag.getString("Number");

            directory.tardises.put(id, number);
            directory.numbers.put(number, id);
        }

        for (NbtElement element : nbt.getList("Callbacks", NbtElement.COMPOUND_TYPE)) {
            NbtCompound tag = (NbtCompound) element;
            List<UUID> list = new ArrayList<>();

            for (NbtElement phone : tag.getList("Phones", NbtElement.INT_ARRAY_TYPE))
                list.add(NbtHelper.toUuid(phone));

            directory.callbacks.put(tag.getUuid("Tardis"), list);
        }

        return directory;
    }
}
