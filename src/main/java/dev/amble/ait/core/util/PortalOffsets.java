package dev.amble.ait.core.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.util.math.Vec3d;

public record PortalOffsets(boolean enabled, float width, float height, Vec3d offset) {
    public static final Codec<PortalOffsets> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.BOOL.optionalFieldOf("enabled", true).forGetter(PortalOffsets::enabled),
            Codec.FLOAT.optionalFieldOf("width", 1F).forGetter(PortalOffsets::width),
            Codec.FLOAT.optionalFieldOf("height", 2F).forGetter(PortalOffsets::height),
            Vec3d.CODEC.optionalFieldOf("offset", Vec3d.ZERO).forGetter(PortalOffsets::offset)
    ).apply(instance, PortalOffsets::new));

    public static final PortalOffsets DEFAULT = new PortalOffsets(true, 1, 2, Vec3d.ZERO);

}
