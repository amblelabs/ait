package dev.amble.ait.core.tardis.comms;

import java.util.List;
import java.util.UUID;

import dev.amble.ait.core.AITSounds;
import dev.amble.ait.core.blockentities.RotaryPhoneBlockEntity;
import dev.amble.ait.core.tardis.ServerTardis;
import dev.amble.ait.core.tardis.handler.travel.TravelHandlerBase;
import dev.amble.ait.core.tardis.manager.ServerTardisManager;
import dev.amble.lib.data.CachedDirectedGlobalPos;
import org.jetbrains.annotations.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;

public sealed interface CommsLine {

    double PHONE_HEARING = 8;
    float EXTERIOR_CLOSED_VOLUME = 0.35f;
    float EXTERIOR_OPEN_VOLUME = 0.9f;

    UUID id();

    String name();

    boolean accepts();

    void ring();

    void notify(Text text);

    List<UUID> participants(@Nullable UUID player);

    boolean present(@Nullable UUID player);

    @Nullable static CommsLine resolve(MinecraftServer server, UUID id) {
        ServerTardis tardis = findTardis(id);

        if (tardis != null)
            return new Tardis(server, tardis);

        PhoneDirectory directory = PhoneDirectory.get(server);
        PhoneDirectory.Entry entry = directory.phone(id);

        if (entry == null)
            return null;

        ServerWorld world = server.getWorld(entry.world());

        if (world == null) {
            directory.unregister(id);
            return null;
        }

        if (world.isChunkLoaded(entry.pos()) && !(world.getBlockEntity(entry.pos()) instanceof RotaryPhoneBlockEntity phone
                && id.equals(phone.line()))) {
            directory.unregister(id);
            return null;
        }

        return new Phone(server, world, entry);
    }

    @Nullable static ServerTardis findTardis(UUID uuid) {
        ServerTardis[] found = new ServerTardis[1];

        ServerTardisManager.getInstance().forEach(tardis -> {
            if (tardis.getUuid().equals(uuid))
                found[0] = tardis;
        });

        return found[0];
    }

    record Tardis(MinecraftServer server, ServerTardis tardis) implements CommsLine {

        @Override
        public UUID id() {
            return this.tardis.getUuid();
        }

        @Override
        public String name() {
            String name = this.tardis.stats().getName();
            return name == null || name.isBlank() ? this.tardis.getUuid().toString().substring(0, 8) : name;
        }

        @Override
        public boolean accepts() {
            return this.tardis.stats().receiveCalls().get();
        }

        @Override
        public void ring() {
            if (this.tardis.hasWorld())
                this.tardis.getDesktop().playSoundAtEveryConsole(AITSounds.TELEPHONE, SoundCategory.BLOCKS, 1.5f, 1f);

            if (this.tardis.travel().getState() != TravelHandlerBase.State.LANDED)
                return;

            CachedDirectedGlobalPos exterior = this.tardis.travel().position();

            if (exterior != null && exterior.getWorld() != null)
                exterior.getWorld().playSound(null, exterior.getPos(), AITSounds.TELEPHONE, SoundCategory.BLOCKS,
                        this.tardis.door().isOpen() ? EXTERIOR_OPEN_VOLUME : EXTERIOR_CLOSED_VOLUME, 1f);
        }

        @Override
        public void notify(Text text) {
            CommsMessages.interior(this.tardis, text);
        }

        @Override
        public List<UUID> participants(@Nullable UUID player) {
            UUID phone = TardisPhones.holderFor(this.id());

            if (phone != null)
                return List.of(phone);

            if (this.tardis.stats().commsHandset().get())
                return player == null ? List.of() : List.of(player);

            return this.interiorPlayers();
        }

        public List<UUID> interiorPlayers() {
            if (!this.tardis.hasWorld())
                return List.of();

            return this.tardis.world().getPlayers().stream().map(Entity::getUuid).toList();
        }

        @Override
        public boolean present(@Nullable UUID player) {
            if (player == null || !this.tardis.hasWorld())
                return false;

            ServerPlayerEntity entity = this.server.getPlayerManager().getPlayer(player);
            return entity != null && entity.getServerWorld() == this.tardis.world();
        }
    }

    record Phone(MinecraftServer server, ServerWorld world, PhoneDirectory.Entry entry) implements CommsLine {

        @Override
        public UUID id() {
            return this.entry.id();
        }

        @Override
        public String name() {
            return PhoneDirectory.format(this.entry.number());
        }

        @Override
        public boolean accepts() {
            return true;
        }

        @Override
        public void ring() {
            if (this.world.isChunkLoaded(this.entry.pos()))
                this.world.playSound(null, this.entry.pos(), AITSounds.TELEPHONE, SoundCategory.BLOCKS, 1f, 1.15f);
        }

        @Override
        public void notify(Text text) {
            UUID holder = TardisPhones.holderFor(this.id());

            if (holder != null) {
                ServerPlayerEntity player = this.server.getPlayerManager().getPlayer(holder);

                if (player != null)
                    CommsMessages.actionBar(player, text);

                return;
            }

            Vec3d center = Vec3d.ofCenter(this.entry.pos());

            for (ServerPlayerEntity player : this.world.getPlayers()) {
                if (player.squaredDistanceTo(center) <= PHONE_HEARING * PHONE_HEARING)
                    CommsMessages.actionBar(player, text);
            }
        }

        @Override
        public List<UUID> participants(@Nullable UUID player) {
            UUID holder = TardisPhones.holderFor(this.id());
            return holder == null ? List.of() : List.of(holder);
        }

        @Override
        public boolean present(@Nullable UUID player) {
            return player != null && player.equals(TardisPhones.holderFor(this.id()));
        }
    }
}
