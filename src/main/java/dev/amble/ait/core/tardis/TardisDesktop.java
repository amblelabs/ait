package dev.amble.ait.core.tardis;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import dev.amble.ait.AITMod;
import dev.amble.ait.api.tardis.TardisComponent;
import dev.amble.ait.api.tardis.TardisEvents;
import dev.amble.ait.core.AITBlocks;
import dev.amble.ait.core.AITSounds;
import dev.amble.ait.core.blockentities.ConsoleBlockEntity;
import dev.amble.ait.core.blockentities.ConsoleGeneratorBlockEntity;
import dev.amble.ait.core.blockentities.DoorBlockEntity;
import dev.amble.ait.core.blockentities.EngineBlockEntity;
import dev.amble.ait.core.tardis.manager.ServerTardisManager;
import dev.amble.ait.core.tardis.util.NetworkUtil;
import dev.amble.ait.core.tardis.util.TardisUtil;
import dev.amble.ait.core.world.QueuedTardisStructureTemplate;
import dev.amble.ait.data.Corners;
import dev.amble.ait.data.schema.desktop.TardisDesktopSchema;
import dev.amble.lib.data.DirectedBlockPos;
import dev.drtheo.queue.api.ActionQueue;
import dev.drtheo.queue.api.util.block.ChunkEraser;
import dev.drtheo.queue.api.util.structure.QueuedStructureTemplate;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.AbstractDecorationEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.world.ChunkTicketType;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.ChunkSerializer;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkStatus;

public class TardisDesktop extends TardisComponent {

    private static final StructurePlacementData SETTINGS = new StructurePlacementData().setUpdateNeighbors(false);
    public static final Identifier CACHE_CONSOLE = AITMod.id("cache_console");
    private TardisDesktopSchema schema;
    private DirectedBlockPos doorPos;
    private BlockPos enginePos;
    private final Corners corners;
    private final Set<BlockPos> consolePos;
    public static final int RADIUS = 500;
    private static final ChunkTicketType<ChunkPos> CHANGING_TICKET = ChunkTicketType.create("ait_desktop_change", Comparator.comparingLong(ChunkPos::toLong));
    private static final Corners CORNERS;

    static {
        BlockPos first = new BlockPos(RADIUS, 0, RADIUS);
        CORNERS = new Corners(first.multiply(-1), first);

        ServerPlayNetworking.registerGlobalReceiver(TardisDesktop.CACHE_CONSOLE,
                ServerTardisManager.receiveTardis((tardis, server, player, handler, buf, responseSender) -> {
                    BlockPos console = buf.readBlockPos();

                    server.execute(() -> {
                        if (!(player.getWorld().getBlockEntity(console) instanceof ConsoleBlockEntity consoleBlockEntity)) return;

                        if (tardis == null)
                            return;

                        if (consoleBlockEntity.isLinked() && consoleBlockEntity.getSonicScrewdriver() != null && !consoleBlockEntity.getSonicScrewdriver().isEmpty()) {
                            player.getWorld().playSound(null, player.getBlockPos(), AITSounds.BWEEP,
                                    SoundCategory.PLAYERS, 1f, 1f);
                            player.sendMessage(Text.translatable("tardis.message.console.has_sonic_in_port"), true);
                            return;
                        }

                        tardis.getDesktop().cacheConsole(console);
                    });
                }));
    }

    private boolean changingDesktop = false;
    private transient List<ChunkPos> heldChunks;

    public TardisDesktop(TardisDesktopSchema schema) {
        super(Id.DESKTOP);
        this.schema = schema;

        this.corners = CORNERS;
        this.consolePos = new HashSet<>();
    }

    @Override
    public void postInit(InitContext ctx) {
        if (!ctx.created())
            return;

        // must be done in postInit, because it accesses door and alarm handlers
        this.changeInterior(schema, false, false).execute();
    }

    public TardisDesktopSchema getSchema() {
        return schema;
    }

    public void setDoorPos(DoorBlockEntity door) {
        if (door == null || door.getWorld() == null || door.getWorld().isClient())
            return;

        DirectedBlockPos pos = door.getDirectedPos();

        if (this.doorPos != null && this.doorPos.equals(pos))
            return;

        this.doorPos = pos;
        TardisEvents.DOOR_MOVE.invoker().onMove(tardis.asServer(), pos, this.doorPos);
    }

    public void setEnginePos(EngineBlockEntity engine) {
        if (engine == null || engine.getWorld() == null || engine.getWorld().isClient())
            return;

        BlockPos pos = engine.getPos();

        if (pos.equals(this.enginePos))
            return;

        this.enginePos = pos;
        TardisEvents.ENGINE_MOVE.invoker().onMove(tardis.asServer(), pos, this.enginePos);
    }

    public void removeDoor(DoorBlockEntity door) {
        if (this.doorPos == null)
            return;

        if (!this.doorPos.equals(door.getDirectedPos()))
            return;

        this.doorPos = null;
        TardisEvents.BREAK_DOOR.invoker().onBreak(this.tardis, doorPos);
    }

    public DirectedBlockPos getDoorPos() {
        if (this.doorPos == null) {
            // womp womp
            for (BlockPos consolePos : this.consolePos) {
                return DirectedBlockPos.create(consolePos, (byte) 0);
            }

            // oh no this this cant be
            return DirectedBlockPos.create(BlockPos.ORIGIN, (byte) 0);
        }

        return doorPos;
    }

    public BlockPos getEnginePos() {
        return enginePos;
    }

    // TODO this is strictly for clearing the interior now
    @Deprecated(forRemoval = true, since = "1.1.0")
    public Corners getCorners() {
        return corners;
    }

    public Optional<ActionQueue> createInteriorChangeQueue(TardisDesktopSchema schema, boolean sendEvent) {
        long start = System.currentTimeMillis();
        this.schema = schema;

        if (sendEvent)
            TardisEvents.RECONFIGURE_DESKTOP.invoker().reconfigure(this.tardis);

        ServerTardis tardis = this.tardis.asServer();
        ServerWorld world = tardis.world();

        Optional<StructureTemplate> optional = this.schema.findTemplate();

        if (optional.isEmpty()) {
            AITMod.LOGGER.error("Failed to find template for {}", this.schema.id());
            return Optional.empty();
        }

        QueuedStructureTemplate template = new QueuedTardisStructureTemplate(optional.get(), tardis);

        Optional<ActionQueue> optionalQueue = template.place(world, BlockPos.ofFloored(corners.getBox().getCenter()),
                BlockPos.ofFloored(corners.getBox().getCenter()), SETTINGS, world.getRandom(), Block.FORCE_STATE);

        optionalQueue.ifPresentOrElse(queue -> queue.thenRun(
                        () -> AITMod.LOGGER.warn("Time taken to generate interior: {}ms",
                                System.currentTimeMillis() - start)),
                () -> AITMod.LOGGER.error("Failed to generate interior for {}",
                        this.tardis.getUuid())
        );

        return optionalQueue;
    }

    public ActionQueue createDesktopClearQueue() {
        ServerTardis tardis = this.tardis.asServer();
        ServerWorld world = tardis.world();
        int chunkRadius = ChunkSectionPos.getSectionCoord(RADIUS);

        TardisUtil.getEntitiesInBox(AbstractDecorationEntity.class, world, corners.getBox(), frame -> true)
                .forEach(frame -> frame.remove(Entity.RemovalReason.DISCARDED));

        int[] bounds = {Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE};

        return new ActionQueue().thenRun(done -> {
            ServerChunkManager chunks = world.getChunkManager();
            List<CompletableFuture<?>> reads = new ArrayList<>();

            for (int x = -chunkRadius; x <= chunkRadius; x++) {
                for (int z = -chunkRadius; z <= chunkRadius; z++) {
                    ChunkPos pos = new ChunkPos(x, z);

                    if (chunks.isChunkLoaded(x, z)) {
                        include(bounds, pos);
                        continue;
                    }

                    reads.add(chunks.threadedAnvilChunkStorage.getNbt(pos).thenAccept(nbt -> nbt
                            .filter(chunk -> ChunkSerializer.getChunkType(chunk) == ChunkStatus.ChunkType.LEVELCHUNK)
                            .ifPresent(chunk -> include(bounds, pos))));
                }
            }

            CompletableFuture.allOf(reads.toArray(CompletableFuture[]::new))
                    .whenComplete((v, e) -> world.getServer().execute(done::finish));
        }).thenRun(done -> {
            if (bounds[0] > bounds[2]) {
                done.finish();
                return;
            }

            ServerChunkManager chunks = world.getChunkManager();
            this.heldChunks = new ArrayList<>();

            for (int x = bounds[0]; x <= bounds[2]; x++) {
                for (int z = bounds[1]; z <= bounds[3]; z++) {
                    ChunkPos pos = new ChunkPos(x, z);
                    chunks.addTicket(CHANGING_TICKET, pos, 0, pos);
                    this.heldChunks.add(pos);
                }
            }

            new ChunkEraser.Builder().withFlags(Block.FORCE_STATE).build(world, bounds[0], bounds[1], bounds[2] + 1, bounds[3] + 1)
                    .thenRun(done::finish).execute();
        }).thenRun(() -> {
            this.consolePos.clear();
            this.doorPos = null;
        });
    }

    private static void include(int[] bounds, ChunkPos pos) {
        synchronized (bounds) {
            bounds[0] = Math.min(bounds[0], pos.x);
            bounds[1] = Math.min(bounds[1], pos.z);
            bounds[2] = Math.max(bounds[2], pos.x);
            bounds[3] = Math.max(bounds[3], pos.z);
        }
    }

    public void startQueue(boolean interact) {
        if (interact) // we use this for the SFX
            this.tardis.door().interactLock(true, null, false);

        this.tardis.door().setDeadlocked(true);
        this.tardis.alarm().enable();
    }

    private void completeQueue() {
        if (this.heldChunks != null) {
            ServerChunkManager chunks = this.tardis.asServer().world().getChunkManager();
            this.heldChunks.forEach(pos -> chunks.removeTicket(CHANGING_TICKET, pos, 0, pos));
            this.heldChunks = null;
        }

        this.tardis.door().setLocked(false);
        this.tardis.door().setDeadlocked(false);
        this.tardis.alarm().disable();

        this.changingDesktop = false;
    }

    public ActionQueue changeInterior(TardisDesktopSchema schema, boolean clear, boolean sendEvent) {
        this.changingDesktop = true;
        ActionQueue queue = new ActionQueue()
                .thenRun(() -> this.startQueue(sendEvent));

        if (clear)
            queue.thenRun(this.createDesktopClearQueue());

        return queue.thenRun(createInteriorChangeQueue(schema, sendEvent))
                .thenRun(this::completeQueue);
    }

    public void cacheConsole(BlockPos consolePos) {
        World dim = this.tardis.asServer().world();
        dim.playSound(null, consolePos, SoundEvents.BLOCK_BEACON_DEACTIVATE, SoundCategory.BLOCKS, 0.5f, 1.0f);

        if (dim.getBlockEntity(consolePos) instanceof ConsoleBlockEntity entity) {
            ConsoleGeneratorBlockEntity generator = new ConsoleGeneratorBlockEntity(consolePos,
                    AITBlocks.CONSOLE_GENERATOR.getDefaultState(), entity.getTypeSchema().id(), entity.getVariant().id());

            entity.onBroken();

            dim.removeBlock(consolePos, false);
            dim.removeBlockEntity(consolePos);

            dim.setBlockState(consolePos, AITBlocks.CONSOLE_GENERATOR.getDefaultState(), Block.NOTIFY_ALL);

            dim.addBlockEntity(generator);
        }
    }

    public static void playSoundAtConsole(World dim, BlockPos console, SoundEvent sound, SoundCategory category, float volume,
                                          float pitch) {
        dim.playSound(null, console, sound, category, volume, pitch);
    }

    public void playSoundAtEveryConsole(SoundEvent sound, SoundCategory category, float volume, float pitch) {
        if (!this.isServer()) return;

        ServerWorld world = this.tardis.asServer().world();

        this.getConsolePos().forEach(consolePos ->
                playSoundAtConsole(world, consolePos, sound, category, volume, pitch));
    }

    public void forcePlaySoundAtEveryConsole(Identifier soundId, SoundCategory category) {
        if (!this.isServer()) return;

        RegistryKey<World> worldKey = this.tardis.asServer().world().getRegistryKey();
        this.getConsolePos().forEach(consolePos -> {
            NetworkUtil.playSound(worldKey, consolePos, soundId, category, 1);
        });
    }

    public void playSoundAtEveryConsole(SoundEvent sound, SoundCategory category) {
        this.playSoundAtEveryConsole(sound, category, 1f, 1f);
    }

    public void playSoundAtEveryConsole(SoundEvent sound) {
        this.playSoundAtEveryConsole(sound, SoundCategory.BLOCKS);
    }

    public Set<BlockPos> getConsolePos() {
        return consolePos;
    }

    public boolean isChanging() {
        return changingDesktop;
    }
}
