package dev.amble.ait.data.codec;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.joml.Vector3f;

public class MoreCodec {

    public static final Codec<Vector3f> VECTOR3F = RecordCodecBuilder.create(instance -> instance
            .group(Codec.FLOAT.fieldOf("x").forGetter(Vector3f::x),
                    Codec.FLOAT.fieldOf("y").forGetter(Vector3f::y),
                    Codec.FLOAT.fieldOf("z").forGetter(Vector3f::z))
            .apply(instance, Vector3f::new));
}
