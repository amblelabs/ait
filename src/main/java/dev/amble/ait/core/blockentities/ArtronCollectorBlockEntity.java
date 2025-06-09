package dev.amble.ait.core.blockentities;

import dev.amble.ait.api.ArtronHolder;
import dev.amble.ait.core.AITBlockEntityTypes;
import dev.amble.ait.core.AITBlocks;
import dev.amble.ait.core.AITItems;
import dev.amble.ait.core.item.ArtronCollectorItem;
import dev.amble.ait.core.item.ChargedZeitonCrystalItem;
import dev.amble.ait.core.world.RiftChunkManager;
import dev.amble.ait.module.gun.core.item.StaserBoltMagazine;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class ArtronCollectorBlockEntity extends BlockEntity implements BlockEntityTicker<ArtronCollectorBlockEntity>, ArtronHolder {

    public double artronAmount = 0;

    public ArtronCollectorBlockEntity(BlockPos pos, BlockState state) {
        super(AITBlockEntityTypes.ARTRON_COLLECTOR_BLOCK_ENTITY_TYPE, pos, state);
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
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

                // todo - instead of zeiton cluster for fuel, check for the TARDIS_FUEL tag
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

    @Nullable @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        NbtCompound nbtCompound = super.toInitialChunkDataNbt();
        nbtCompound.putDouble("artronAmount", this.artronAmount);
        return nbtCompound;
    }

    @Override
    public void tick(World world, BlockPos pos, BlockState state, ArtronCollectorBlockEntity blockEntity) {
        if (world.isClient())
            return;

        if (world.getServer().getTicks() % 3 == 0)
            return;

        RiftChunkManager manager = RiftChunkManager.getInstance((ServerWorld) this.world);
        ChunkPos chunk = new ChunkPos(pos);

        if (shouldDrain(manager, chunk)) {
            manager.removeFuel(chunk, 3);
            this.addFuel(3);

            this.updateListeners(state);
        }
    }

    private boolean shouldDrain(RiftChunkManager manager, ChunkPos pos) {
        return this.getCurrentFuel() < ArtronCollectorItem.COLLECTOR_MAX_FUEL
                && manager.getArtron(pos) >= 3;
    }

    private void updateListeners(BlockState state) {
        this.markDirty();

        if (!this.hasWorld())
            return;

        this.world.updateListeners(this.getPos(), this.getCachedState(), state, Block.NOTIFY_ALL);
    }
}
