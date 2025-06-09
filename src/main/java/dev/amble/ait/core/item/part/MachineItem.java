package dev.amble.ait.core.item.part;

import dev.amble.ait.AITMod;
import dev.amble.ait.core.AITTags;
import dev.amble.ait.core.item.SonicItem;
import dev.amble.ait.core.item.sonic.SonicMode;
import dev.amble.ait.data.schema.MachineRecipeSchema;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ClickType;
import net.minecraft.util.Identifier;

public class MachineItem extends Item {

    public static final Identifier MACHINE_DISASSEMBLE = AITMod.id("machine_disassemble");

    public MachineItem(Settings settings) {
        super(settings);
    }

    @Override
    public boolean onStackClicked(ItemStack stack, Slot slot, ClickType clickType, PlayerEntity player) {
        if (clickType != ClickType.RIGHT)
            return false;

        ItemStack machine = slot.getStack();

        // Should this be in SonicItem.Mode.INTERACTION?
        if (!stack.getRegistryEntry().isIn(AITTags.Items.SONIC_ITEM))
            return false;

        if (SonicItem.mode(stack) != SonicMode.Modes.INTERACTION)
            return false;

        MachineItem.disassemble(machine);
        return true;
    }

    @Environment(value = EnvType.CLIENT)
    public static void disassemble(ItemStack machine) {
        PacketByteBuf data = PacketByteBufs.create();
        data.writeItemStack(machine.copyWithCount(1));

        ClientPlayNetworking.send(MACHINE_DISASSEMBLE, data);
        machine.decrement(1);
    }

    @Environment(value = EnvType.SERVER)
    public static void disassemble(ServerPlayerEntity player, ItemStack machine, MachineRecipeSchema recipe) {
        machine.decrement(1);

        for (ItemStack input : recipe.input()) {
            player.dropItem(input, true);
        }
    }
}
