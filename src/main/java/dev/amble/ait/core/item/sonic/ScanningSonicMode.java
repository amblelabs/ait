package dev.amble.ait.core.item.sonic;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;

import dev.amble.ait.core.AITSounds;
import dev.amble.ait.core.item.SonicItem;
import dev.amble.ait.core.tardis.Tardis;
import dev.amble.ait.core.world.LandingPadManager;
import dev.amble.ait.core.world.RiftChunkManager;
import dev.amble.ait.core.world.TardisServerWorld;
import dev.amble.ait.data.landing.LandingPadRegion;
import dev.amble.ait.data.landing.LandingPadSpot;
import dev.amble.ait.data.schema.sonic.SonicSchema;

public class ScanningSonicMode extends SonicMode {
    private static final Text RIFT_FOUND = Text.translatable("message.ait.sonic.riftfound").formatted(Formatting.AQUA)
            .formatted(Formatting.BOLD);
    private static final Text RIFT_NOT_FOUND = Text.translatable("message.ait.sonic.riftnotfound").formatted(Formatting.AQUA)
            .formatted(Formatting.BOLD);

    protected ScanningSonicMode(int index) {
        super(index);
    }

    @Override
    public Text text() {
        return Text.translatable("sonic.ait.mode.scanning").formatted(Formatting.YELLOW, Formatting.BOLD);
    }

    @Override
    public int maxTime() {
        return 5 * 60 * 20;
    }

    @Override
    public boolean startUsing(ItemStack stack, World world, PlayerEntity user, Hand hand) {
        if (world.isClient()) return false;

        return this.process(stack, world, user);
    }

    public boolean process(ItemStack stack, World world, PlayerEntity user) {
        HitResult hitResult = SonicMode.getHitResult(user);

        if (hitResult instanceof BlockHitResult blockHit)
            return this.scanRegion(stack, world, user, blockHit.getBlockPos());

        return false;
    }

    public boolean scanRegion(ItemStack stack, World world, PlayerEntity user, BlockPos pos) {
        if (world.isClient())
            return true;

        if (user == null)
            return false;

        LandingPadRegion region = LandingPadManager.getInstance((ServerWorld) world).getRegionAt(pos);
        if (region != null) {
            if (world.getBlockState(pos).isAir()) return true;

            boolean wasSpotCreated = modifyRegion(null, (ServerWorld) world, pos.up(), user, stack, region);

            float pitch = wasSpotCreated ? 1.1f : 0.75f;
            world.playSound(null, pos, AITSounds.SONIC_SWITCH, SoundCategory.PLAYERS, 1f, pitch);

            return true;
        }

        if (!TardisServerWorld.isTardisDimension(world)) {
            sendRiftInfo(null, (ServerWorld) world, pos, user, stack);
            return true;
        }

        Tardis tardis = SonicItem.getTardisStatic(world, stack);

        if (tardis == null)
            return false;

        if (TardisServerWorld.isTardisDimension(world)) {
            sendTardisInfo(tardis, (ServerWorld) world, pos, user, stack);
            return true;
        }

        return false;
    }

    private static boolean modifyRegion(Tardis tardis, ServerWorld world, BlockPos pos, PlayerEntity player, ItemStack stack, LandingPadRegion region) {
        LandingPadSpot spot = region.getSpotAt(pos).orElse(null);

        if (spot == null) {
            addSpot(region, pos);

            syncRegion(world, pos);
            return true;
        }

        removeSpot(region, pos);
        syncRegion(world, pos);

        return false;
    }
    private static void addSpot(LandingPadRegion region, BlockPos pos) {
        region.createSpotAt(pos);
    }
    private static void removeSpot(LandingPadRegion region, BlockPos pos) {
        region.removeSpotAt(pos);
    }
    private static void syncRegion(ServerWorld world, BlockPos pos) {
        LandingPadManager.Network.syncTracked(LandingPadManager.Network.Action.ADD, world, new ChunkPos(pos));
    }

    private static void sendRiftInfo(Tardis tardis, ServerWorld world, BlockPos pos, PlayerEntity player, ItemStack stack) {
        boolean isRift = RiftChunkManager.isRiftChunk(world, pos);

        player.sendMessage(isRift ? RIFT_FOUND : RIFT_NOT_FOUND, true);

        if (!isRift) return;

        int artronValue = (int) RiftChunkManager.getInstance(world).getArtron(new ChunkPos(pos));
        player.sendMessage(
                Text.translatable("message.ait.artron_units", artronValue)
                        .formatted(Formatting.GOLD)
        );
    }
    private static void sendTardisInfo(Tardis tardis, ServerWorld world, BlockPos pos, PlayerEntity player, ItemStack stack) {
        if (tardis == null)
            return;

        if (tardis.crash().isUnstable() || tardis.crash().isToxic()) {
            player.sendMessage(Text.literal("Repair time: " + tardis.crash().getRepairTicks())
                    .formatted(Formatting.DARK_RED, Formatting.ITALIC), true);
            return;
        }

        player.sendMessage(
                Text.translatable("message.ait.artron_units", tardis.fuel().getCurrentFuel()).formatted(Formatting.GOLD), true);
    }

    @Override
    public Identifier model(SonicSchema.Models models) {
        return models.scanning();
    }
}
