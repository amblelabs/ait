package dev.amble.ait.core.tardis.control.impl.pos;

import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public enum PosType implements StringIdentifiable {
    X() {
        @Override
        public BlockPos add(BlockPos pos, int amount) {
            return pos.add(amount, 0, 0);
        }
    },
    Y() {
        @Override
        public BlockPos add(BlockPos pos, int amount, World world) {
            return PosType.clamp(pos, amount, world);
        }

        @Override
        public BlockPos add(BlockPos pos, int amount) {
            return pos.add(0, amount, 0);
            // @TODO in the nether, search below 128 and above 0.
        }
    },
    Z() {
        @Override
        public BlockPos add(BlockPos pos, int amount) {
            return pos.add(0, 0, amount);
        }
    };

    // Clamps the Y coordinate of a position depending on the world.
    public static BlockPos clamp(BlockPos pos, int amount, World world) {
        if (world.getRegistryKey() == World.NETHER) {
            return pos.withY(MathHelper.clamp(pos.getY() + amount, world.getBottomY(), 120));
        }

        return pos.withY(MathHelper.clamp(pos.getY() + amount, world.getBottomY(), world.getTopY() - 1));
    }

    // adds an amount to a blockpos based on this type
    public abstract BlockPos add(BlockPos pos, int amount);

    public BlockPos add(BlockPos pos, int amount, World world) {
        return add(pos, amount);
    }

    @Override
    public String asString() {
        return this.name().toLowerCase();
    }
}
