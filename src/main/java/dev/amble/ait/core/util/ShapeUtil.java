package dev.amble.ait.core.util;

import dev.amble.ait.data.ShapeMap;
import net.minecraft.block.Block;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;

public class ShapeUtil {

    public static VoxelShape rect(double x, double y, double z, double width, double height, double length) {
        return Block.createCuboidShape(x, y, z, x + width, y + height, z + length);
    }

    public static VoxelShape rotate(Direction from, Direction to, VoxelShape shape) {
        if (from == to)
            return shape;

        var ref = new Object() {
            VoxelShape buffer = VoxelShapes.empty();
        };

        int times = (to.getHorizontal() - from.getHorizontal() + 4) % 4;

        for (int i = 0; i < times; i++) {
            shape.forEachBox((minX, minY, minZ, maxX, maxY, maxZ) -> ref.buffer = VoxelShapes.combine(ref.buffer,
                    VoxelShapes.cuboid(1 - maxZ, minY, minX, 1 - minZ, maxY, maxX), BooleanBiFunction.OR));

            shape = ref.buffer;
            ref.buffer = VoxelShapes.empty();
        }

        return shape;
    }

    public static ShapeMap.Builder rotations(Direction from, VoxelShape shape) {
        ShapeMap.Builder builder = new ShapeMap.Builder();

        for (Direction direction : Direction.values()) {
            builder.add(direction, ShapeUtil.rotate(from, direction, shape));
        }

        return builder;
    }

    public static Box cloneBox(Box box) {
        return new Box(box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ);
    }
}
