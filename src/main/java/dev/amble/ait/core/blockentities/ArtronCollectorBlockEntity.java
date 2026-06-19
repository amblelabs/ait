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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;

import dev.amble.ait.api.ArtronHolder;
import dev.amble.ait.api.ArtronHolderItem;
import dev.amble.ait.core.AITBlockEntityTypes;
import dev.amble.ait.core.AITBlocks;
import dev.amble.ait.core.AITItems;
import dev.amble.ait.core.engine.link.block.FluidLinkBlockEntity;
import dev.amble.ait.core.item.ArtronCollectorItem;
import dev.amble.ait.core.item.ChargedZeitonCrystalItem;
import dev.amble.ait.core.world.RiftChunkManager;
import dev.amble.ait.module.gun.core.item.StaserBoltMagazine;

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

    public void useOn(World world, boolean sneaking, PlayerEntity player) {
        if (!world.isClient()) {
            player.sendMessage(Text.literal(this.getCurrentFuel() + "/" + ArtronCollectorItem.COLLECTOR_MAX_FUEL)
                    .formatted(Formatting.GOLD));
            ItemStack stack = player.getMainHandStack();
            if (stack.getItem() instanceof ArtronCollectorItem) {
                double residual = ArtronCollectorItem.addFuel(stack, this.getCurrentFuel());
                this.setCurrentFuel(residual);
            } else if (stack.getItem() instanceof ArtronHolderItem artronHolderItem) {
                double residual = artronHolderItem.addFuel(this.getCurrentFuel(), stack);
                this.setCurrentFuel(residual);
            } else if (stack.getItem() instanceof ChargedZeitonCrystalItem crystal) {
                double residual = crystal.addFuel(this.getCurrentFuel(), stack);
                this.setCurrentFuel(residual);
            } else if (stack.getItem() instanceof StaserBoltMagazine magazine) {
                double residual = magazine.addFuel(this.getCurrentFuel(), stack);
                this.setCurrentFuel(residual);
            }
            if (stack.isOf(AITBlocks.ZEITON_CLUSTER.asItem())) {
                if (sneaking) {
                    player.getInventory().setStack(player.getInventory().selectedSlot,
                            new ItemStack(AITItems.CHARGED_ZEITON_CRYSTAL));
                    return;
                }

                this.addFuel(15);
                stack.decrement(1);
            }
        }
    }

    @Override
    public void setCurrentFuel(double artronAmount) {
        this.artronAmount = artronAmount;
        this.updateListeners(this.getCachedState());
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

        if (serverWorld.getServer().getTicks() % 3 == 0)
            return;

        ChunkPos chunk = new ChunkPos(pos);
        RiftChunkManager manager = RiftChunkManager.getInstance(serverWorld);

        if (shouldDrainChunk(manager, chunk)) {
            manager.removeFuel(chunk, FLOW_AMOUNT);
            this.addFuel(FLOW_AMOUNT);

            this.updateListeners(state);
        }

        if (shouldDrawFluid()) {
            this.removeFuel(FLOW_AMOUNT);
            blockEntity.source().addLevel(FLOW_AMOUNT);
            this.updateListeners(state);
        }
    }

    private boolean shouldDrainChunk(RiftChunkManager manager, ChunkPos pos) {
        return this.getCurrentFuel() < ArtronCollectorItem.COLLECTOR_MAX_FUEL
                && manager.getArtron(pos) >= FLOW_AMOUNT;
    }

    private boolean shouldDrawFluid() {
        return this.getCurrentFuel() >= FLOW_AMOUNT && source() != null && !source().isLevelFull();
    }

    private void updateListeners(BlockState state) {
        this.markDirty();

        if (!this.hasWorld())
            return;

        this.world.updateListeners(this.getPos(), this.getCachedState(), state, Block.NOTIFY_ALL);
    }
}
