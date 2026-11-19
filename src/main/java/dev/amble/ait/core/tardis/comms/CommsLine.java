package dev.amble.ait.core.tardis.comms;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import dev.amble.ait.compat.svc.CommsHandler;
import dev.amble.ait.core.AITSounds;
import dev.amble.ait.core.blockentities.RotaryPhoneBlockEntity;
import dev.amble.ait.core.tardis.ServerTardis;
import dev.amble.ait.core.tardis.handler.travel.TravelHandlerBase;
import dev.amble.ait.core.tardis.manager.ServerTardisManager;
import dev.amble.lib.data.CachedDirectedGlobalPos;
import org.jetbrains.annotations.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public sealed interface CommsLine {

    double PHONE_HEARING = 8;
    float EXTERIOR_CLOSED_VOLUME = 0.35f;
    float EXTERIOR_OPEN_VOLUME = 0.9f;
    float CONSOLE_VOLUME = 1.5f;
    float PHONE_VOLUME = 1f;
    float PHONE_PITCH = 1.15f;

    UUID id();

    String name();

    boolean accepts();

    void ring();

    void notify(Text text);

    List<UUID> participants(@Nullable UUID player);

    boolean present(@Nullable UUID player);

    @Nullable static CommsLine resolve(MinecraftServer server, UUID id) {
        PhoneDirectory directory = PhoneDirectory.get(server);
        PhoneDirectory.Entry entry = directory.phone(id);

        if (entry == null) {
            ServerTardis tardis = ServerTardisManager.getInstance().demandTardis(server, id);
            return tardis == null ? null : new Tardis(server, tardis);
        }

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
                this.ringInterior(this.tardis.world());

            if (this.tardis.travel().getState() != TravelHandlerBase.State.LANDED)
                return;

            CachedDirectedGlobalPos exterior = this.tardis.travel().position();

            if (exterior != null && exterior.getWorld() != null)
                exterior.getWorld().playSound(null, exterior.getPos(), AITSounds.TELEPHONE, SoundCategory.BLOCKS,
                        this.tardis.door().isOpen() ? EXTERIOR_OPEN_VOLUME : EXTERIOR_CLOSED_VOLUME, 1f);
        }

        private void ringInterior(ServerWorld interior) {
            Set<BlockPos> consoles = this.tardis.getDesktop().getConsolePos();
            Set<BlockPos> phones = RotaryPhones.extensions(interior);
            Set<BlockPos> ringing = new HashSet<>();
            RegistryEntry<SoundEvent> sound = Registries.SOUND_EVENT.getEntry(AITSounds.TELEPHONE);

            for (ServerPlayerEntity player : interior.getPlayers()) {
                BlockPos nearest = null;
                boolean phone = false;
                double best = Double.MAX_VALUE;

                for (BlockPos pos : consoles) {
                    double distance = player.squaredDistanceTo(Vec3d.ofCenter(pos));

                    if (distance < best) {
                        best = distance;
                        nearest = pos;
                        phone = false;
                    }
                }

                for (BlockPos pos : phones) {
                    double distance = player.squaredDistanceTo(Vec3d.ofCenter(pos));

                    if (distance < best) {
                        best = distance;
                        nearest = pos;
                        phone = true;
                    }
                }

                if (nearest == null)
                    continue;

                if (phone)
                    ringing.add(nearest);

                Vec3d at = Vec3d.ofCenter(nearest);
                player.networkHandler.sendPacket(new PlaySoundS2CPacket(sound, SoundCategory.BLOCKS, at.x, at.y, at.z,
                        phone ? PHONE_VOLUME : CONSOLE_VOLUME, phone ? PHONE_PITCH : 1f, interior.getRandom().nextLong()));
            }

            RotaryPhones.ringExtensions(interior, ringing);
        }

        @Override
        public void notify(Text text) {
            CommsMessages.interior(this.tardis, text);
        }

        @Override
        public List<UUID> participants(@Nullable UUID player) {
            TardisPhones.Handset handset = TardisPhones.handsetFor(this.id());

            if (handset != null)
                return handset.dropped() ? List.of() : List.of(handset.holder());

            if (CommsHandler.of(this.tardis).handset().get())
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
            if (TardisPhones.handsetFor(this.id()) != null)
                return true;

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
                this.world.playSound(null, this.entry.pos(), AITSounds.TELEPHONE, SoundCategory.BLOCKS, PHONE_VOLUME, PHONE_PITCH);
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
            TardisPhones.Handset handset = TardisPhones.handsetFor(this.id());
            return handset == null || handset.dropped() ? List.of() : List.of(handset.holder());
        }

        @Override
        public boolean present(@Nullable UUID player) {
            return TardisPhones.handsetFor(this.id()) != null;
        }
    }
}
