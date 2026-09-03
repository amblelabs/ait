package dev.amble.ait.core.item;

import java.util.function.Consumer;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import dev.amble.ait.core.AITSounds;
import dev.amble.ait.core.world.RiftChunkManager;
import dev.amble.ait.core.world.TardisServerWorld;

public class RiftScannerItem extends Item {
    private static final int MAX_ITERATIONS = 32;
    private static final String NBT_X = "X";
    private static final String NBT_Z = "Z";
    private static final String NBT_DINGED = "Dinged";

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

        findNearestRift(serverWorld, new ChunkPos(user.getBlockPos()), (chunk) -> setTarget(stack, chunk));

        user.sendMessage(Text.translatable("riftchunk.ait.tracking"), true);
        return TypedActionResult.success(stack);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (world.isClient) return;

        ChunkPos target = getTarget(stack);
        if (target == null || target.equals(ChunkPos.ORIGIN)) return;

        boolean hasDinged = stack.getOrCreateNbt().getBoolean(NBT_DINGED);

        if (entity.getChunkPos().equals(target)) {
            if (!hasDinged) {
                // Bling sound is kinda quiet so it should be set to about a volume of 3
                world.playSound(null, entity.getBlockPos(), AITSounds.TARDIS_BLING, SoundCategory.PLAYERS, 3f, 1f);
                stack.getOrCreateNbt().putBoolean(NBT_DINGED, true);
            }
        } else {
            if (hasDinged) {
                stack.getOrCreateNbt().putBoolean(NBT_DINGED, false);
            }
        }
    }

    public static void findNearestRift(ServerWorld world, ChunkPos source, Consumer<ChunkPos> found) {
        int steps = 1;
        RiftChunkManager manager = RiftChunkManager.getInstance(world);

        for (int i = 0; i < MAX_ITERATIONS; i++) {
            if (steps % 2 != 0) {
                if (trySearch(manager, steps, source, Direction.EAST, found)) return;
                if (trySearch(manager, steps, source, Direction.SOUTH, found)) return;
            } else {
                if (trySearch(manager, steps, source, Direction.WEST, found)) return;
                if (trySearch(manager, steps, source, Direction.NORTH, found)) return;
            }
            steps++;
        }
    }

    private static boolean trySearch(RiftChunkManager manager, int limit, ChunkPos source, Direction direction, Consumer<ChunkPos> found) {
        for (int b = 0; b <= limit; b++) {
            source = getChunkInDirection(source, direction);
            if (isConsumable(manager, source)) {
                found.accept(source);
                return true;
            }
        }
        return false;
    }

    private static boolean isConsumable(RiftChunkManager manager, ChunkPos pos) {
        return manager.isRiftChunk(pos) && manager.getArtron(pos) >= 250;
    }

    private static ChunkPos getChunkInDirection(ChunkPos pos, Direction dir) {
        return new ChunkPos(pos.x + (dir.getOffsetX()), pos.z + (dir.getOffsetZ()));
    }

    private static void setTarget(ItemStack stack, ChunkPos pos) {
        NbtCompound nbt = stack.getOrCreateNbt();
        nbt.putInt(NBT_X, pos.x);
        nbt.putInt(NBT_Z, pos.z);
        nbt.putBoolean(NBT_DINGED, false);
    }

    public static ChunkPos getTarget(ItemStack stack) {
        NbtCompound nbt = stack.getOrCreateNbt();
        if (!(nbt.contains(NBT_X) && nbt.contains(NBT_Z)))
            return ChunkPos.ORIGIN;
        return new ChunkPos(nbt.getInt(NBT_X), nbt.getInt(NBT_Z));
    }
}