package dev.amble.ait.core.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class PortalOffsets {
	public static final Codec<PortalOffsets> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.BOOL.optionalFieldOf("enabled", true).forGetter(PortalOffsets::isEnabled),
			Codec.FLOAT.optionalFieldOf("width", 1F).forGetter(PortalOffsets::getWidth),
			Codec.FLOAT.optionalFieldOf("height", 2F).forGetter(PortalOffsets::getHeight),
			Vec3d.CODEC.optionalFieldOf("north", Vec3d.ZERO).forGetter(map -> map.get(Direction.NORTH)),
			Vec3d.CODEC.optionalFieldOf("south", Vec3d.ZERO).forGetter(map -> map.get(Direction.SOUTH)),
			Vec3d.CODEC.optionalFieldOf("east", Vec3d.ZERO).forGetter(map -> map.get(Direction.EAST)),
			Vec3d.CODEC.optionalFieldOf("west", Vec3d.ZERO).forGetter(map -> map.get(Direction.WEST)),
			Vec3d.CODEC.optionalFieldOf("north_east", Vec3d.ZERO).forGetter(map -> map.get((byte) 1)),
			Vec3d.CODEC.optionalFieldOf("south_east", Vec3d.ZERO).forGetter(map -> map.get((byte) 5)),
			Vec3d.CODEC.optionalFieldOf("south_west", Vec3d.ZERO).forGetter(map -> map.get((byte) 9)),
			Vec3d.CODEC.optionalFieldOf("north_west", Vec3d.ZERO).forGetter(map -> map.get((byte) 13))
	).apply(instance, PortalOffsets::new));

	private final Vec3d[] lookup;
	private final float width;
	private final float height;
	private boolean enabled;

	public PortalOffsets(float width, float height) {
		super();
		this.lookup = new Vec3d[16];
		this.width = width;
		this.height = height;
	}

	public PortalOffsets(boolean enabled, float width, float height, Vec3d north, Vec3d south, Vec3d east, Vec3d west,
			Vec3d northEast, Vec3d southEast, Vec3d southWest, Vec3d northWest) {
		this(width, height);
		this.put(Direction.NORTH, north);
		this.put(Direction.SOUTH, south);
		this.put(Direction.EAST, east);
		this.put(Direction.WEST, west);

		for (byte i = 1; i <= 3; i++) this.put(i, northEast);
		for (byte i = 5; i <= 7; i++) this.put(i, southEast);
		for (byte i = 9; i <= 11; i++) this.put(i, southWest);
		for (byte i = 13; i <= 15; i++) this.put(i, northWest);

		this.enabled = enabled;
	}

	public float getWidth() {
		return width;
	}

	public float getHeight() {
		return height;
	}

	public boolean isEnabled() {
		return this.enabled;
	}

	public Vec3d get(byte direction) {
		if (direction < 0 || direction > 15) {
			throw new IllegalArgumentException("Direction must be between 0 and 15, inclusive.");
		}
		return this.lookup[direction];
	}

	public Vec3d get(Direction dir) {
		return switch (dir) {
			case NORTH -> this.get((byte) 0);
			case SOUTH -> this.get((byte) 8);
			case EAST -> this.get((byte) 4);
			case WEST -> this.get((byte) 12);
			default -> null;
		};
	}

	public void put(byte direction, Vec3d offset) {
		if (direction < 0 || direction > 15) {
			throw new IllegalArgumentException("Direction must be between 0 and 15, inclusive.");
		}
		this.lookup[direction] = offset;
	}

	public void put(Direction dir, Vec3d offset) {
		switch (dir) {
			case NORTH -> this.put((byte) 0, offset);
			case SOUTH -> this.put((byte) 8, offset);
			case EAST -> this.put((byte) 4, offset);
			case WEST -> this.put((byte) 12, offset);
			default -> throw new IllegalArgumentException("Invalid direction for portal offsets: " + dir);
		}
	}

	public Vec3d apply(Direction dir, Vec3d pos) {
		Vec3d offset = this.get(dir);
		if (offset == null) {
			return pos;
		}
		return pos.add(offset);
	}

	public Vec3d apply(Vec3d pos, byte direction) {
		Vec3d offset = this.get(direction);
		if (offset == null) {
			return pos;
		}
		return pos.add(offset);
	}
}
