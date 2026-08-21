package dev.amble.ait.core.blockentities;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import dev.amble.ait.api.ArtronHolder;
import dev.amble.ait.api.ArtronHolderItem;
import dev.amble.ait.core.AITBlockEntityTypes;
import dev.amble.ait.core.AITBlocks;
import dev.amble.ait.core.AITItems;
import dev.amble.ait.core.engine.link.block.FluidLinkBlockEntity;
import dev.amble.ait.core.item.ArtronCollectorItem;
import dev.amble.ait.core.world.RiftChunkManager;

public class ArtronCollectorBlockEntity extends FluidLinkBlockEntity implements BlockEntityTicker<ArtronCollectorBlockEntity>, ArtronHolder {

    public static final int FLOW_AMOUNT = 3;

    public double artronAmount = 0;

    public ArtronCollectorBlockEntity(BlockPos pos, BlockState state) {
        super(AITBlockEntityTypes.ARTRON_COLLECTOR_BLOCK_ENTITY_TYPE, pos, state);
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putDouble("artronAmount", this.artronAmount);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        if (nbt.contains("artronAmount"))
            this.setCurrentFuel(nbt.getDouble("artronAmount"));
        super.readNbt(nbt);
    }

    /**
     * Compatibility overload for addons compiled against the original main-hand-only method.
     */
    public void useOn(World world, boolean sneaking, PlayerEntity player) {
        this.useOn(world, sneaking, player, Hand.MAIN_HAND);
    }

    public void useOn(World world, boolean sneaking, PlayerEntity player, Hand hand) {
        if (!world.isClient()) {
            player.sendMessage(Text.literal(this.getCurrentFuel() + "/" + ArtronCollectorItem.COLLECTOR_MAX_FUEL)
                    .formatted(Formatting.GOLD));
            ItemStack stack = player.getStackInHand(hand);

            if (stack.getItem() instanceof ArtronHolderItem holder) {
                double accepted = holder.insertFuel(this.getCurrentFuel(), stack);
                this.extractFuel(accepted);
            }

            if (stack.isOf(AITBlocks.ZEITON_CLUSTER.asItem())) {
                if (sneaking) {
                    this.convertCluster(player, hand, stack);
                    return;
                }

                if (this.getMaxFuel() - this.getCurrentFuel() < 15)
                    return;

                if (this.insertFuel(15) == 15 && !player.isCreative())
                    stack.decrement(1);
            }
        }
    }

    private void convertCluster(PlayerEntity player, Hand hand, ItemStack clusters) {
        ItemStack crystal = new ItemStack(AITItems.CHARGED_ZEITON_CRYSTAL);

        if (!player.isCreative() && clusters.getCount() == 1) {
            player.setStackInHand(hand, crystal);
            return;
        }

        if (!player.isCreative())
            clusters.decrement(1);

        player.getInventory().offerOrDrop(crystal);
    }

    @Override
    public void setCurrentFuel(double artronAmount) {
        double effective = Double.isFinite(artronAmount)
                ? MathHelper.clamp(artronAmount, 0, this.getMaxFuel())
                : 0;

        if (Double.compare(this.artronAmount, effective) == 0)
            return;

        this.artronAmount = effective;
        this.updateListeners();
    }

    @Override
    public double getMaxFuel() {
        return ArtronCollectorItem.COLLECTOR_MAX_FUEL;
    }

    @Override
    public double getCurrentFuel() {
        return this.artronAmount;
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        NbtCompound nbtCompound = super.toInitialChunkDataNbt();
        nbtCompound.putDouble("artronAmount", this.artronAmount);
        return nbtCompound;
    }

    @Override
    public void tick(World world, BlockPos pos, BlockState state, ArtronCollectorBlockEntity blockEntity) {
        if (!(world instanceof ServerWorld serverWorld))
            return;

        if (serverWorld.getServer().getTicks() % 3 != 0)
            return;

        Chunk chunk = serverWorld.getChunk(pos);
        RiftChunkManager manager = RiftChunkManager.getInstance(serverWorld);

        double chunkRequest = Math.min(FLOW_AMOUNT, this.getMaxFuel() - this.getCurrentFuel());
        if (chunkRequest > 0) {
            double extracted = manager.extractFuel(chunk, chunkRequest);
            double remainder = this.addFuel(extracted);

            if (remainder > 0)
                manager.insertFuel(chunk, remainder);
        }

        if (blockEntity.source() == null)
            return;

        double sourceCapacity = Math.max(0, blockEntity.source().maxLevel() - blockEntity.source().level());
        double transferable = Math.min(FLOW_AMOUNT, Math.min(this.getCurrentFuel(), sourceCapacity));
        double collectorExtracted = this.extractFuel(transferable);
        double accepted = blockEntity.source().insertLevel(collectorExtracted);

        if (accepted < collectorExtracted)
            this.addFuel(collectorExtracted - accepted);
    }

    private void updateListeners() {
        this.markDirty();

        if (!this.hasWorld())
            return;

        this.world.updateListeners(this.getPos(), this.getCachedState(), this.getCachedState(), Block.NOTIFY_LISTENERS);
    }
}
