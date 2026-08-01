package dev.amble.ait.core.item;

import java.util.Optional;
import java.util.function.Consumer;

import org.jetbrains.annotations.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import dev.amble.ait.core.AITSounds;
import dev.amble.ait.core.world.RiftChunkManager;
import dev.amble.ait.core.world.TardisServerWorld;

public class RiftScannerItem extends Item {
    private static final int MAX_ITERATIONS = 32;
    private static final double MIN_TRACKABLE_ARTRON = 250;

    private static final String NBT_X = "chunkX";
    private static final String NBT_Z = "chunkZ";
    private static final String NBT_DIMENSION = "dimension";
    private static final String NBT_DINGED = "dinged";

    // Read-once compatibility with scanners created before targets became dimensional.
    private static final String LEGACY_NBT_X = "X";
    private static final String LEGACY_NBT_Z = "Z";
    private static final String LEGACY_NBT_DINGED = "Dinged";

    public RiftScannerItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!(world instanceof ServerWorld serverWorld))
            return TypedActionResult.pass(user.getStackInHand(hand));

        if (TardisServerWorld.isTardisDimension(serverWorld))
            return TypedActionResult.fail(user.getStackInHand(hand));

        ItemStack stack = user.getStackInHand(hand);
        user.getItemCooldownManager().set(this, 100);

        Optional<ChunkPos> found = findNearestRiftTarget(serverWorld, new ChunkPos(user.getBlockPos()));

        if (found.isEmpty()) {
            clearTarget(stack);
            user.sendMessage(Text.translatable("message.ait.riftscanner.info3"), true);
            return TypedActionResult.fail(stack);
        }

        setTarget(stack, serverWorld.getRegistryKey(), found.get());
        user.sendMessage(Text.translatable("riftchunk.ait.tracking"), true);
        return TypedActionResult.success(stack);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (world.isClient) return;

        migrateLegacyTarget(stack, world);

        ChunkPos target = getTrackedTarget(stack);
        boolean hasDinged = stack.getOrCreateNbt().getBoolean(NBT_DINGED);

        if (target == null || !isTargetIn(stack, world)) {
            if (hasDinged)
                stack.getOrCreateNbt().putBoolean(NBT_DINGED, false);
            return;
        }

        if (entity.getChunkPos().equals(target)) {
            if (!hasDinged) {
                // Bling sound is quiet, so retain the intentionally high volume.
                world.playSound(null, entity.getBlockPos(), AITSounds.TARDIS_BLING, SoundCategory.PLAYERS, 3f, 1f);
                stack.getOrCreateNbt().putBoolean(NBT_DINGED, true);
            }
        } else if (hasDinged) {
            stack.getOrCreateNbt().putBoolean(NBT_DINGED, false);
        }
    }

    /**
     * Compatibility callback API used by the emergency-power autopilot.
     */
    public static void findNearestRift(ServerWorld world, ChunkPos source, Consumer<ChunkPos> found) {
        findNearestRiftTarget(world, source).ifPresent(found);
    }

    public static Optional<ChunkPos> findNearestRiftTarget(ServerWorld world, ChunkPos source) {
        RiftChunkManager manager = RiftChunkManager.getInstance(world);

        for (int radius = 0; radius <= MAX_ITERATIONS; radius++) {
            if (radius == 0) {
                if (isConsumable(manager, source))
                    return Optional.of(source);
                continue;
            }

            int minX = source.x - radius;
            int maxX = source.x + radius;
            int minZ = source.z - radius;
            int maxZ = source.z + radius;

            for (int x = minX; x <= maxX; x++) {
                ChunkPos north = new ChunkPos(x, minZ);
                if (isConsumable(manager, north)) return Optional.of(north);

                ChunkPos south = new ChunkPos(x, maxZ);
                if (isConsumable(manager, south)) return Optional.of(south);
            }

            for (int z = minZ + 1; z < maxZ; z++) {
                ChunkPos west = new ChunkPos(minX, z);
                if (isConsumable(manager, west)) return Optional.of(west);

                ChunkPos east = new ChunkPos(maxX, z);
                if (isConsumable(manager, east)) return Optional.of(east);
            }
        }

        return Optional.empty();
    }

    private static boolean isConsumable(RiftChunkManager manager, ChunkPos pos) {
        if (!manager.isRiftChunk(pos))
            return false;

        Chunk loaded = manager.getLoadedChunk(pos);

        // Rift identity is deterministic. Do not generate a candidate merely to inspect its
        // attachment; only apply the energy threshold when its data is already loaded.
        return loaded == null || manager.getArtron(loaded) >= MIN_TRACKABLE_ARTRON;
    }

    private static void setTarget(ItemStack stack, RegistryKey<World> dimension, ChunkPos pos) {
        NbtCompound nbt = stack.getOrCreateNbt();
        nbt.putInt(NBT_X, pos.x);
        nbt.putInt(NBT_Z, pos.z);
        nbt.putString(NBT_DIMENSION, dimension.getValue().toString());
        nbt.putBoolean(NBT_DINGED, false);
        clearLegacyTarget(nbt);
    }

    private static void clearTarget(ItemStack stack) {
        NbtCompound nbt = stack.getOrCreateNbt();
        nbt.remove(NBT_X);
        nbt.remove(NBT_Z);
        nbt.remove(NBT_DIMENSION);
        nbt.putBoolean(NBT_DINGED, false);
        clearLegacyTarget(nbt);
    }

    private static void migrateLegacyTarget(ItemStack stack, World currentWorld) {
        NbtCompound nbt = stack.getOrCreateNbt();

        if (!(nbt.contains(NBT_X) && nbt.contains(NBT_Z))
                && nbt.contains(LEGACY_NBT_X) && nbt.contains(LEGACY_NBT_Z)) {
            nbt.putInt(NBT_X, nbt.getInt(LEGACY_NBT_X));
            nbt.putInt(NBT_Z, nbt.getInt(LEGACY_NBT_Z));
        }

        if (nbt.contains(NBT_X) && nbt.contains(NBT_Z) && !nbt.contains(NBT_DIMENSION)) {
            nbt.putString(NBT_DIMENSION, currentWorld.getRegistryKey().getValue().toString());
            nbt.putBoolean(NBT_DINGED, false);
            clearLegacyTarget(nbt);
        }
    }

    private static void clearLegacyTarget(NbtCompound nbt) {
        nbt.remove(LEGACY_NBT_X);
        nbt.remove(LEGACY_NBT_Z);
        nbt.remove(LEGACY_NBT_DINGED);
    }

    /**
     * Legacy accessor retained for addons. New code must use {@link #getTrackedTarget(ItemStack)}
     * because {@link ChunkPos#ORIGIN} is also a valid target.
     */
    @Deprecated
    public static ChunkPos getTarget(ItemStack stack) {
        ChunkPos target = getTrackedTarget(stack);
        return target == null ? ChunkPos.ORIGIN : target;
    }

    @Nullable public static ChunkPos getTrackedTarget(ItemStack stack) {
        NbtCompound nbt = stack.getOrCreateNbt();

        if (nbt.contains(NBT_X) && nbt.contains(NBT_Z))
            return new ChunkPos(nbt.getInt(NBT_X), nbt.getInt(NBT_Z));

        if (nbt.contains(LEGACY_NBT_X) && nbt.contains(LEGACY_NBT_Z))
            return new ChunkPos(nbt.getInt(LEGACY_NBT_X), nbt.getInt(LEGACY_NBT_Z));

        return null;
    }

    @Nullable public static RegistryKey<World> getTargetDimension(ItemStack stack) {
        NbtCompound nbt = stack.getOrCreateNbt();

        if (!nbt.contains(NBT_DIMENSION))
            return null;

        Identifier id = Identifier.tryParse(nbt.getString(NBT_DIMENSION));
        return id == null ? null : RegistryKey.of(RegistryKeys.WORLD, id);
    }

    public static boolean isTargetIn(ItemStack stack, World world) {
        return getTrackedTarget(stack) != null && world.getRegistryKey().equals(getTargetDimension(stack));
    }
}
