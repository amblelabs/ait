package dev.amble.ait.core.blocks;

import dev.amble.ait.AITMod;
import dev.amble.ait.core.ars.ArsRegistry;
import dev.amble.ait.core.ars.ArsStructure;
import dev.amble.ait.core.item.SonicItem;
import dev.amble.ait.core.item.sonic.SonicMode;
import dev.amble.ait.core.tardis.ServerTardis;
import dev.amble.ait.core.tardis.manager.ServerTardisManager;
import dev.amble.ait.core.world.TardisServerWorld;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.UUID;

public class ArsPlacerBlock extends Block {
	public static final Identifier REQUEST_PLACE = AITMod.id("request_place_ars");

	public ArsPlacerBlock(Settings settings) {
		super(settings);
	}

	static {
		ServerPlayNetworking.registerGlobalReceiver(REQUEST_PLACE, (server, player, handler, buf, responseSender) -> {
			Identifier structureId = buf.readIdentifier();
			World world = player.getWorld();
			BlockPos pos = buf.readBlockPos();

			if (!(world.getBlockState(pos).getBlock() instanceof ArsPlacerBlock)) {
				AITMod.LOGGER.warn("Received place request for ArsPlacerBlock at {} from {}, but block is not an ArsPlacerBlock", pos, player.getName().getString());
				return;
			}

			if (!(TardisServerWorld.isTardisDimension(world))) {
				AITMod.LOGGER.warn("Received place request for ArsPlacerBlock at {} from {}, but world is not a TARDIS dimension", pos, player.getName().getString());
				return;
			}
			UUID tardisId = TardisServerWorld.getTardisId(world);
			assert tardisId != null : "TARDIS ID should not be null in TARDIS dimension";
			ArsStructure structure = ArsRegistry.getInstance().getOrFallback(structureId);

			ServerTardisManager.getInstance().getTardis(server, tardisId, tardis -> {
				structure.placeInWorld(tardis, pos, player.getHorizontalFacing()).ifPresent(queue -> {
					tardis.fuel().removeFuel(structure.getFuelCost());
					queue.execute();
				});
			});
		});
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		if (world.isClient()) {
			return ActionResult.SUCCESS;
		}

		ItemStack handStack = player.getStackInHand(hand);
		if (!(handStack.getItem() instanceof SonicItem)) {
			return ActionResult.FAIL;
		}

		SonicMode mode = SonicItem.mode(handStack);
		if (mode != SonicMode.Modes.INTERACTION) return ActionResult.FAIL;

		if (!TardisServerWorld.isTardisDimension(world)) return ActionResult.FAIL;

		UUID tardisId = TardisServerWorld.getTardisId(world);
		AITMod.openScreen((ServerPlayerEntity) player, 3, tardisId, pos); // id 3 is for Ars Placer GUI

		return ActionResult.SUCCESS;
	}
}
