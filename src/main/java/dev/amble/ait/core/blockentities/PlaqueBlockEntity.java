package dev.amble.ait.core.blockentities;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.JsonParseException;

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

import dev.amble.ait.api.tardis.link.v2.block.InteriorLinkableBlockEntity;
import dev.amble.ait.core.AITBlockEntityTypes;

public class PlaqueBlockEntity extends InteriorLinkableBlockEntity {

    private static final Pattern TT_CAPSULE_TYPE = Pattern.compile("^Type (\\d+) TT Capsule$");

    private Text customPlaqueText = Text.translatableWithFallback("block.ait.plaque.default_text",
            "Type 50 TT Capsule");

    public PlaqueBlockEntity(BlockPos pos, BlockState state) {
        super(AITBlockEntityTypes.PLAQUE_BLOCK_ENTITY_TYPE, pos, state);
    }

    public Text getPlaqueText() {
        return this.customPlaqueText;
    }

    public void setPlaqueText(Text name) {
        this.customPlaqueText = createPlaqueText(name.getString());
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
            this.customPlaqueText = Text.translatableWithFallback("block.ait.plaque.default_text", "Type 50 TT Capsule");
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
            return createPlaqueText(plaqueText);
        }

        return createPlaqueText(plaqueText);
    }

    private static Text createPlaqueText(String plaqueText) {
        Matcher matcher = TT_CAPSULE_TYPE.matcher(plaqueText);

        if (matcher.matches())
            return Text.translatableWithFallback("block.ait.plaque.tt_capsule_type", plaqueText, matcher.group(1));

        return Text.literal(plaqueText);
    }
}
