package dev.amble.ait.core.item;

import dev.amble.ait.core.world.RiftChunkManager;
import dev.amble.ait.core.world.TardisServerWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.function.Consumer;

public class RiftScannerItem extends Item {
    private static final int MAX_ITERATIONS = 32;

    public RiftScannerItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!(world instanceof ServerWorld serverWorld))
            return TypedActionResult.pass(user.getStackInHand(hand));

        if (TardisServerWorld.isTardisDimension(serverWorld))
            return TypedActionResult.fail(user.getStackInHand(hand));

        user.getItemCooldownManager().set(this, 100);
        findNearestRift(serverWorld, new ChunkPos(user.getBlockPos()), (chunk) -> setTarget(user.getStackInHand(hand), chunk));

        user.sendMessage(Text.translatable("riftchunk.ait.tracking"), true);
        return TypedActionResult.success(user.getStackInHand(hand));
    }

    /**
     * Searches for a target block with artron levels > 0 within a range.
     *
     * @param world
     *            The world object.
     * @param source
     *            The current chunk
     */
    public static void findNearestRift(ServerWorld world, ChunkPos source, Consumer<ChunkPos> found) {
        int steps = 1;
        RiftChunkManager manager = RiftChunkManager.getInstance(world);

        for (int i = 0; i < MAX_ITERATIONS; i++) {
            if (steps % 2 != 0) {
                if (trySearch(manager, steps, source, Direction.EAST, found))
                    return;

                if (trySearch(manager, steps, source, Direction.SOUTH, found))
                    return;
            } else {
                if (trySearch(manager, steps, source, Direction.WEST, found))
                    return;

                if (trySearch(manager, steps, source, Direction.NORTH, found))
                    return;
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

        nbt.putInt("X", pos.x);
        nbt.putInt("Z", pos.z);
    }

    public static ChunkPos getTarget(ItemStack stack) {
        NbtCompound nbt = stack.getOrCreateNbt();

        if (!(nbt.contains("X") && nbt.contains("Z")))
            return ChunkPos.ORIGIN;

        return new ChunkPos(nbt.getInt("X"), nbt.getInt("Z"));
    }
}
