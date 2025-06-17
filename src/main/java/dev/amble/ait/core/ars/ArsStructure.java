package dev.amble.ait.core.ars;

import dev.amble.ait.AITMod;
import dev.amble.ait.api.Nameable;
import dev.amble.ait.core.AITBlocks;
import dev.amble.ait.core.tardis.ServerTardis;
import dev.amble.ait.core.tardis.handler.FuelHandler;
import dev.amble.ait.core.util.WorldUtil;
import dev.amble.ait.core.world.QueuedTardisStructureTemplate;
import dev.amble.ait.data.schema.desktop.textures.StructurePreviewTexture;
import dev.amble.lib.api.Identifiable;
import dev.drtheo.queue.api.ActionQueue;
import dev.drtheo.queue.api.util.structure.QueuedStructureTemplate;
import net.minecraft.block.Block;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.text.Text;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.*;

public interface ArsStructure extends Identifiable, Nameable {
	Identifier structureId();
	StructurePreviewTexture previewTexture();

	/**
	 * Returns this structure's identifier in path form
	 * eg "example:mod"
	 * becomes "example:structures/ars/mod.nbt"
	 * @return the structure path identifier
	 */
	default Identifier getStructureLocation() {
		return structureId().withPrefixedPath("ars/");
	}

	default Optional<ActionQueue> placeInWorld(ServerTardis tardis, BlockPos pos, Direction facing) {
		long start = System.currentTimeMillis();
		Optional<StructureTemplate> optional = this.findTemplate();

		if (optional.isEmpty()) {
			AITMod.LOGGER.error("Failed to find ARS template for {}", this.id());
			return Optional.empty();
		}

		ServerWorld world = tardis.world();

		if (tardis.fuel().getCurrentFuel() < this.getFuelCost()) {
			world.getPlayers().forEach(player -> {
				player.sendMessage(
						Text.literal("Not enough fuel for structure. Requires " + this.getFuelCost() + "AU").formatted(Formatting.RED),
						true);
			});

			return Optional.empty();
		}

		QueuedStructureTemplate template = new QueuedTardisStructureTemplate(optional.get(), tardis);
		Optional<ActionQueue> optionalQueue = template.place(world, pos, pos, new StructurePlacementData(), world.getRandom(), Block.REDRAW_ON_MAIN_THREAD);

		optionalQueue.ifPresentOrElse(queue -> queue.thenRun(
						() -> AITMod.LOGGER.warn("Time taken to place ARS structure: {}ms",
								System.currentTimeMillis() - start)),
				() -> AITMod.LOGGER.error("Failed to generate ARS structure for {}",
						tardis.getUuid())
		);

		return optionalQueue;
	}

	default BlockPos findEntryPosition(BlockPos placementPos, Direction facing) {
		StructureTemplate template = findTemplate().orElse(null);
		if (template == null) return BlockPos.ORIGIN;

		Map<Block, List<BlockPos>> result = findTargetInTemplate(template, placementPos, facing, AITBlocks.ARS_PLACER_BLOCK, AITBlocks.DOOR_BLOCK);
		return result.values().stream().findFirst().get().stream().findFirst().get(); // wow
	}

	default Optional<StructureTemplate> findTemplate() {
		return WorldUtil.getOverworld().getStructureTemplateManager()
				.getTemplate(this.getStructureLocation());
	}

	default int getFuelCost() {
		Optional<StructureTemplate> optional = this.findTemplate();

		if (optional.isEmpty()) {
			AITMod.LOGGER.error("Failed to find ARS template for {}", this.id());
			return 5000;
		}

		// return the size of the template in blocks * 10 as the fuel cost
		StructureTemplate template = optional.get();
		int volume = template.getSize().getX() * template.getSize().getY() * template.getSize().getZ();
		return (int) Math.min(volume, FuelHandler.TARDIS_MAX_FUEL);
	}

	default Optional<Identifier> structureIdOptional() {
		return Optional.ofNullable(structureId());
	}

	static Map<Block, List<BlockPos>> findTargetInTemplate(StructureTemplate template, BlockPos pos, Direction direction, Block... blocks) {
		HashMap<Block, List<BlockPos>> result = new HashMap<>();

		for (Block block : blocks) {
			List<StructureTemplate.StructureBlockInfo> list = template.getInfosForBlock(
					pos, new StructurePlacementData().setRotation(directionToRotation(direction)), block);

			if (list.isEmpty()) {
				continue;
			}

			result.put(block, list.stream().map(StructureTemplate.StructureBlockInfo::pos).toList());
		}

		return result;
	}

	static BlockRotation directionToRotation(Direction direction) {
		return switch (direction) {
			case NORTH -> BlockRotation.CLOCKWISE_180;
			case EAST -> BlockRotation.COUNTERCLOCKWISE_90;
			case WEST -> BlockRotation.CLOCKWISE_90;
			default -> BlockRotation.NONE;
		};
	}
}
