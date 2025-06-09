package dev.amble.ait.data.codec;

import com.mojang.serialization.Codec;
import net.minecraft.util.Util;
import org.joml.Vector3f;

import java.util.List;

public class MoreCodec {

    public static final Codec<Vector3f> VECTOR3F = Codec.FLOAT
            .listOf()
            .comapFlatMap(
                    coordinates -> Util.decodeFixedLengthList(coordinates, 3).map(coords -> new Vector3f(coords.get(0), coords.get(1), coords.get(2))),
                    vec -> List.of(vec.x(), vec.y(), vec.z())
            );
}
