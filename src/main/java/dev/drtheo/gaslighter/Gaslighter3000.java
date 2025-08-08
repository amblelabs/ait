package dev.drtheo.gaslighter;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import dev.drtheo.gaslighter.api.FakeBlockEvents;
import dev.drtheo.gaslighter.api.Twitter;
import dev.drtheo.gaslighter.impl.FakeChunkSection;
import it.unimi.dsi.fastutil.shorts.ShortOpenHashSet;
import it.unimi.dsi.fastutil.shorts.ShortSet;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;

import net.minecraft.block.BlockState;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.ChunkDeltaUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;

public class Gaslighter3000 {

    private final ServerWorld world;
    private final Map<ChunkPos, ChunkHolder> lookup = new HashMap<>();

    public Gaslighter3000(ServerWorld world) {
        this.world = world;
    }

    public void spreadLies(BlockPos pos, BlockState state) {
        FakeBlockEvents.PLACED.invoker().onPlace(this.world, state, pos);

        this.lookup.computeIfAbsent(new ChunkPos(pos), chunkPos
                -> new ChunkHolder(world, chunkPos)).spreadLies(pos, state);
    }

    public void touchGrass(BlockPos pos) {
        ChunkHolder holder = this.lookup.get(new ChunkPos(pos));

        if (holder == null)
            return;

        holder.touchGrass(pos);
    }

    public BlockState getAgenda(BlockPos pos) {
        ChunkHolder holder = this.lookup.get(new ChunkPos(pos));

        if (holder == null)
            return this.world.getBlockState(pos);

        return holder.getAgenda(pos);
    }

    public void touchGrass() {
        this.lookup.values().forEach(ChunkHolder::touchGrass);
    }

    public void tweet() {
        for (ChunkHolder holder : lookup.values()) {
            holder.tweet();
        }
    }

    public void tweet(ServerPlayerEntity player) {
        for (ChunkHolder holder : lookup.values()) {
            holder.tweet(player);
        }
    }

    static class ChunkHolder {

        private final ServerWorld world;
        private final ChunkPos pos;

        private final ShortSet[] blockUpdatesBySection;
        private final FakeChunkSection[] sections;

        public ChunkHolder(ServerWorld world, ChunkPos pos) {
            this.world = world;
            this.pos = pos;

            this.blockUpdatesBySection = new ShortSet[world.countVerticalSections()];
            this.sections = new FakeChunkSection[this.blockUpdatesBySection.length];
        }

        public void spreadLies(BlockPos pos, BlockState state) {
            int i = this.world.getSectionIndex(pos.getY());

            if (this.sections[i] == null)
                this.sections[i] = new FakeChunkSection(this.world);

            this.sections[i].setBlockState(pos.getX() & 0xF, pos.getY() & 0xF, pos.getZ() & 0xF, state);

            if (this.world instanceof Twitter twitter)
                twitter.ait$setFake(pos, true);

            this.markForBlockUpdate(pos, i);
        }

        public void touchGrass(BlockPos pos) {
            int i = this.world.getSectionIndex(pos.getY());

            if (this.sections[i] == null)
                return;

            this.sections[i].setBlockState(pos.getX() & 0xF, pos.getY() & 0xF, pos.getZ() & 0xF,
                    this.world.getBlockState(pos));

            if (this.world instanceof Twitter twitter)
                twitter.ait$setFake(pos, false);

            this.markForBlockUpdate(pos, i);
            FakeBlockEvents.REMOVED.invoker().onRemove(this.world, pos);
        }

        public void touchGrass() {
            for (int i = 0; i < this.sections.length; i++) {
                ShortSet updates = this.blockUpdatesBySection[i];

                if (updates == null)
                    continue;

                int coord = this.world.sectionIndexToCoord(i);
                ChunkSectionPos csp = ChunkSectionPos.from(this.pos, coord);

                for (short update : updates) {
                    this.touchGrass(csp.unpackBlockPos(update));
                }
            }
        }

        public BlockState getAgenda(BlockPos pos) {
            int i = this.world.getSectionIndex(pos.getY());

            if (this.sections[i] == null)
                return this.world.getBlockState(pos);

            return this.sections[i].getBlockState(pos.getX() & 0xF, pos.getY() & 0xF, pos.getZ() & 0xF);
        }

        private void markForBlockUpdate(BlockPos pos, int i) {
            if (this.blockUpdatesBySection[i] == null)
                this.blockUpdatesBySection[i] = new ShortOpenHashSet();

            this.blockUpdatesBySection[i].add(ChunkSectionPos.packLocal(pos));
        }

        private void unmarkForBlockUpdate(BlockPos pos, int i) {
            if (this.blockUpdatesBySection[i] == null)
                return;

            this.blockUpdatesBySection[i].remove(ChunkSectionPos.packLocal(pos));
        }

        private void makeTweetPackets(Consumer<ChunkDeltaUpdateS2CPacket> consumer) {
            for (int i = 0; i < this.blockUpdatesBySection.length; ++i) {
                ShortSet shortSet = this.blockUpdatesBySection[i];

                if (shortSet == null)
                    continue;

                int j = this.world.sectionIndexToCoord(i);
                ChunkSectionPos chunkSectionPos = ChunkSectionPos.from(this.pos, j);

                consumer.accept(new ChunkDeltaUpdateS2CPacket(chunkSectionPos, shortSet, this.sections[i]));
            }
        }

        public void tweet() {
            Collection<ServerPlayerEntity> list = PlayerLookup.tracking(this.world, this.pos);

            if (list.isEmpty())
                return;

            this.makeTweetPackets(packet -> sendPacketToPlayers(list, packet));
        }

        public void tweet(ServerPlayerEntity player) {
            this.makeTweetPackets(packet -> player.networkHandler.sendPacket(packet));
        }

        private static void sendPacketToPlayers(Collection<ServerPlayerEntity> players, Packet<?> packet) {
            players.forEach(player -> player.networkHandler.sendPacket(packet));
        }
    }
}
