package dev.amble.ait.core.blockentities;

import dev.amble.ait.api.tardis.link.v2.block.InteriorLinkableBlockEntity;
import dev.amble.ait.core.AITBlockEntityTypes;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.EnumSet;

public class SkyBlockEntity extends InteriorLinkableBlockEntity {
	private final EnumSet<Direction> renderingFaces;

	public SkyBlockEntity(BlockPos pos, BlockState state) {
		super(AITBlockEntityTypes.SKY_BLOCK, pos, state);

		this.renderingFaces = EnumSet.allOf(Direction.class);
	}

	public boolean shouldRenderFace(Direction direction) {
		return this.renderingFaces.contains(direction);
	}

	public void onNeighbourChange() {
		this.recalculateRenderingFaces();
	}

	private void recalculateRenderingFaces() {
		this.renderingFaces.clear();

		BlockPos pos = this.getPos();
		for (Direction direction : Direction.values()) {
			boolean shouldRender = Block.shouldDrawSide(this.getCachedState(), this.getWorld(), pos, direction, pos.offset(direction));

			if (shouldRender) {
				this.renderingFaces.add(direction);
			}
		}
	}
}
