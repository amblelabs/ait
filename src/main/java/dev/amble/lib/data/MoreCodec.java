package dev.amble.lib.data;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector2d;
import org.joml.Vector3f;

import net.minecraft.util.Util;

public class MoreCodec {

    public static final Codec<Vector3f> VECTOR3F = Codec.FLOAT
            .listOf()
            .comapFlatMap(
                    coordinates -> Util.decodeFixedLengthList(coordinates, 3).map(coords -> new Vector3f(coords.get(0), coords.get(1), coords.get(2))),
                    vec -> List.of(vec.x(), vec.y(), vec.z())
            );

    public static final Codec<Vector2d> VECTOR2D = Codec.DOUBLE
            .listOf()
            .comapFlatMap(
                    coordinates -> Util.decodeFixedLengthList(coordinates, 2).map(coords -> new Vector2d(coords.get(0), coords.get(1))),
                    vec -> List.of(vec.x, vec.y)
            );

    public static final Codec<Vec2f> VEC2F = Codec.FLOAT
            .listOf()
            .comapFlatMap(
                    coordinates -> Util.decodeFixedLengthList(coordinates, 2).map(coords -> new Vec2f(coords.get(0), coords.get(1))),
                    vec -> List.of(vec.x, vec.y)
            );

    public static final Codec<PosRot> POSROT = RecordCodecBuilder.create(instance ->
            instance.group(Vec3d.CODEC.fieldOf("pos").forGetter(PosRot::pos),
                            VEC2F.fieldOf("rot").forGetter(PosRot::rot))
                    .apply(instance, PosRot::new)
    );
}
