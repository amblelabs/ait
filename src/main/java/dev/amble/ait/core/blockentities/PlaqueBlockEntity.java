package dev.amble.ait.core.blockentities;

import com.google.gson.JsonParseException;
import dev.amble.ait.api.tardis.link.v2.block.InteriorLinkableBlockEntity;
import dev.amble.ait.core.AITBlockEntityTypes;

import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;

public class PlaqueBlockEntity extends InteriorLinkableBlockEntity {

    private Text customPlaqueText = Text.translatable("block.ait.plaque.default_text");

    public PlaqueBlockEntity(BlockPos pos, BlockState state) {
        super(AITBlockEntityTypes.PLAQUE_BLOCK_ENTITY_TYPE, pos, state);
    }

    public Text getPlaqueText() {
        return this.customPlaqueText;
    }

    public void setPlaqueText(Text name) {
        this.customPlaqueText = name.copy();
        markDirty();
        if (this.getWorld() != null && !this.getWorld().isClient) {
            this.getWorld().updateListeners(getPos(), getCachedState(), getCachedState(), 3);
        }
    }

    public boolean onUse(ServerPlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);
        if (stack.getItem() == Items.NAME_TAG && stack.hasCustomName()) {
            this.setPlaqueText(stack.getName());
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
        nbt.putString("CustomPlaqueText", Text.Serializer.toJson(this.customPlaqueText));
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        if (nbt.contains("CustomPlaqueText", NbtElement.STRING_TYPE)) {
            this.customPlaqueText = readPlaqueText(nbt.getString("CustomPlaqueText"));
        }
        if (this.customPlaqueText == null || this.customPlaqueText.getString().isEmpty()) {
            this.customPlaqueText = Text.translatable("block.ait.plaque.default_text");
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

    private static Text readPlaqueText(String plaqueText) {
        try {
            Text text = Text.Serializer.fromJson(plaqueText);
            if (text != null)
                return text;
        } catch (JsonParseException ignored) {
        }

        return Text.literal(plaqueText);
    }
}
