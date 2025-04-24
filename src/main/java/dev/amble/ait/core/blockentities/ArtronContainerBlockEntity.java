package dev.amble.ait.core.blockentities;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;

import dev.amble.ait.core.AITBlockEntityTypes;
import dev.amble.ait.core.AITBlocks;
import dev.amble.ait.core.AITItems;
import dev.amble.ait.core.engine.link.IFluidSource;
import dev.amble.ait.core.engine.link.block.FluidLinkBlockEntity;
import dev.amble.ait.core.item.ArtronCollectorItem;
import dev.amble.ait.core.item.ChargedZeitonCrystalItem;
import dev.amble.ait.core.world.RiftChunkManager;
import dev.amble.ait.module.gun.core.item.StaserBoltMagazine;

public class ArtronContainerBlockEntity extends FluidLinkBlockEntity implements BlockEntityTicker<ArtronContainerBlockEntity>, IFluidSource {

    public double artronAmount = ArtronCollectorItem.COLLECTOR_MAX_FUEL;

    public ArtronContainerBlockEntity(BlockPos pos, BlockState state) {
        super(AITBlockEntityTypes.ARTRON_CONTAINER_BLOCK_ENTITY_TYPE, pos, state);
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putDouble("artronAmount", this.artronAmount);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        if (nbt.contains("artronAmount"))
            this.setLevel(nbt.getDouble("artronAmount"));
        super.readNbt(nbt);
    }

    public void useOn(World world, boolean sneaking, PlayerEntity player) {
        if (!world.isClient()) {
            player.sendMessage(Text.literal(this.level() + "/" + this.max())
                    .formatted(Formatting.GOLD));
            ItemStack stack = player.getMainHandStack();
            if (stack.getItem() instanceof ArtronCollectorItem) {
                double residual = ArtronCollectorItem.addFuel(stack, this.level());
                this.setLevel(residual);
            } else if (stack.getItem() instanceof ChargedZeitonCrystalItem crystal) {
                double residual = crystal.addFuel(this.level(), stack);
                this.setLevel(residual);
            } else if (stack.getItem() instanceof StaserBoltMagazine magazine) {
                double residual = magazine.addFuel(this.level(), stack);
                this.setLevel(residual);
            }
            if (stack.isOf(AITBlocks.ZEITON_CLUSTER.asItem())) {
                if (sneaking) {
                    player.getInventory().setStack(player.getInventory().selectedSlot,
                            new ItemStack(AITItems.CHARGED_ZEITON_CRYSTAL));
                    return;
                }

                // todo - instead of zeiton cluster for fuel, check for the TARDIS_FUEL tag
                this.addLevel(15);
                stack.decrement(1);
            }
        }
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        NbtCompound nbtCompound = super.toInitialChunkDataNbt();
        nbtCompound.putDouble("artronAmount", this.artronAmount);
        return nbtCompound;
    }

    @Override
    public void tick(World world, BlockPos pos, BlockState state, ArtronContainerBlockEntity blockEntity) {
        if (world.isClient())
            return;

        if (world.getServer().getTicks() % 3 == 0)
            return;

        /*RiftChunkManager manager = RiftChunkManager.getInstance((ServerWorld) this.world);
        ChunkPos chunk = new ChunkPos(pos);

        if (shouldDrain(manager, chunk)) {
            manager.removeFuel(chunk, 3);
            this.addFuel(3);

            this.updateListeners(state);
        }*/
    }

    private boolean shouldDrain(RiftChunkManager manager, ChunkPos pos) {
        return this.level() < this.max()
                && manager.getArtron(pos) >= 3;
    }

    private void updateListeners(BlockState state) {
        this.markDirty();

        if (!this.hasWorld())
            return;

        this.world.updateListeners(this.getPos(), this.getCachedState(), state, Block.NOTIFY_ALL);
    }

    @Override
    public double level() {
        return this.artronAmount;
    }

    @Override
    public void setLevel(double level) {
        this.artronAmount = level;
    }

    @Override
    public double max() {
        return ArtronCollectorItem.COLLECTOR_MAX_FUEL;
    }
}
