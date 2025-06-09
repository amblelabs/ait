package dev.amble.ait.core.blockentities;

import dev.amble.ait.api.tardis.link.v2.block.InteriorLinkableBlockEntity;
import dev.amble.ait.core.AITBlockEntityTypes;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;

public class PlaqueBlockEntity extends InteriorLinkableBlockEntity {

    private String customPlaqueText = "Type 50 TT Capsule";

    public PlaqueBlockEntity(BlockPos pos, BlockState state) {
        super(AITBlockEntityTypes.PLAQUE_BLOCK_ENTITY_TYPE, pos, state);
    }

    public String getPlaqueText() {
        return this.customPlaqueText;
    }

    public void setPlaqueText(String name) {
        this.customPlaqueText = name;
        markDirty();
        if (this.getWorld() != null && !this.getWorld().isClient) {
            this.getWorld().updateListeners(getPos(), getCachedState(), getCachedState(), 3);
        }
    }

    public boolean onUse(ServerPlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);
        if (stack.getItem() == Items.NAME_TAG && stack.hasCustomName()) {
            this.setPlaqueText(stack.getName().getString());
            if (!player.isCreative()) {
                stack.decrement(1);
            }
            return true;
        }
        return false;
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putString("CustomPlaqueText", this.customPlaqueText);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        if (nbt.contains("CustomPlaqueText")) {
            this.customPlaqueText = nbt.getString("CustomPlaqueText");
        }
        if (this.customPlaqueText == null || this.customPlaqueText.isEmpty()) {
            this.customPlaqueText = "Type 50 TT Capsule";
        }
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }

    @Override
    public Packet toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }
}
