package dev.amble.ait.core.item.component;

import dev.amble.ait.AITMod;
import dev.amble.ait.core.AITTags;
import dev.amble.ait.core.item.SonicItem;
import dev.amble.ait.core.item.link.AbstractLinkItem;
import dev.amble.ait.core.item.sonic.SonicMode;
import dev.amble.ait.core.util.StackUtil;
import dev.amble.ait.data.schema.MachineRecipeSchema;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ClickType;
import net.minecraft.util.Identifier;

public class AbstractTardisPart extends Item {

    public static final Identifier DISASSEMBLE = AITMod.id("part_disassemble");
    public static final Identifier ATTACH = AITMod.id("link_attach");
    public static final Identifier UNATTACH = AITMod.id("link_unattach");

    private final AbstractLinkItem.Type[] slots;

    public AbstractTardisPart(Settings settings, AbstractLinkItem.Type... slots) {
        super(settings.maxCount(1));
        this.slots = slots;
    }

    private static void set(ItemStack stack, AbstractLinkItem item, AbstractLinkItem.Type type) {
        NbtCompound nbt = stack.getOrCreateNbt();
        StackUtil.write(nbt, type.toString(), item);
    }

    public static void remove(ItemStack stack, AbstractLinkItem.Type type) {
        AbstractTardisPart.set(stack, null, type);
    }

    public static void remove(ItemStack stack, AbstractLinkItem item) {
        AbstractTardisPart.remove(stack, item.getType());
    }

    public static void set(ItemStack stack, AbstractLinkItem item) {
        AbstractTardisPart.set(stack, item, item.getType());
    }

    public static AbstractLinkItem get(ItemStack stack, AbstractLinkItem.Type type) {
        NbtCompound nbt = stack.getOrCreateNbt();
        Item result = StackUtil.readItem(nbt, type.toString());

        if (result != null)
            return (AbstractLinkItem) result;

        return null;
    }

    public static AbstractLinkItem getAny(ItemStack stack) {
        // i love guessing
        for (AbstractLinkItem.Type type : AbstractLinkItem.Type.values()) {
            AbstractLinkItem item = get(stack, type);

            if (item != null)
                return item;
        }

        return null;
    }

    public static AbstractLinkItem removeAny(ItemStack stack) {
        AbstractLinkItem item = AbstractTardisPart.getAny(stack);

        if (item == null)
            return null;

        AbstractTardisPart.remove(stack, item);
        return item;
    }

    @Override
    public boolean onStackClicked(ItemStack stack, Slot slot, ClickType clickType, PlayerEntity player) { // slot is the
                                                                                                            // machine,
                                                                                                            // stack is
                                                                                                            // the
                                                                                                            // cursor
        ItemStack machine = slot.getStack();

        if (clickType != ClickType.RIGHT)
            return false;

        // Should this be in SonicItem.Mode.INTERACTION?
        if (!stack.getRegistryEntry().isIn(AITTags.Items.SONIC_ITEM))
            return false;

        if (SonicItem.mode(stack) != SonicMode.Modes.INTERACTION)
            return false;

        AbstractTardisPart.disassemble(machine);
        return true;
    }

    @Override
    public boolean onClicked(ItemStack stack, ItemStack otherStack, Slot slot, ClickType clickType, PlayerEntity player,
            StackReference cursor) {
        if (player.getWorld().isClient())
            return false;

        if (!(stack.getItem() instanceof AbstractLinkItem))
            return false;

        if (clickType == ClickType.RIGHT)
            AbstractTardisPart.set(stack, (AbstractLinkItem) StackUtil.take(otherStack).getItem());
        else {
            cursor.set(new ItemStack(StackUtil.orAir(AbstractTardisPart.removeAny(stack))));
        }

        return true;
    }

    public AbstractLinkItem.Type[] getSlots() {
        return slots;
    }

    @Environment(value = EnvType.CLIENT)
    public static void disassemble(ItemStack machine) {
        PacketByteBuf data = PacketByteBufs.create();
        data.writeItemStack(StackUtil.take(machine));

        ClientPlayNetworking.send(DISASSEMBLE, data);
    }

    @Environment(value = EnvType.CLIENT)
    public static void unattach(ItemStack machine, AbstractLinkItem.Type link) {
        PacketByteBuf data = PacketByteBufs.create();
        data.writeItemStack(machine);
        data.writeEnumConstant(link);

        ClientPlayNetworking.send(UNATTACH, data);
    }

    @Environment(value = EnvType.CLIENT)
    public static void attach(ItemStack machine, AbstractLinkItem link) {
        PacketByteBuf data = PacketByteBufs.create();
        data.writeItemStack(machine);
        StackUtil.writeItem(data, link);

        ClientPlayNetworking.send(ATTACH, data);
    }

    @Environment(value = EnvType.SERVER)
    public static void disassemble(ServerPlayerEntity player, ItemStack machine, MachineRecipeSchema recipe) {
        machine.decrement(1);

        for (ItemStack input : recipe.input()) {
            player.dropItem(input, true);
        }
    }

    @Environment(value = EnvType.SERVER)
    public static void unattach(ServerPlayerEntity player, ItemStack machine, AbstractLinkItem.Type type) {
        AbstractTardisPart.remove(machine, type);
    }
}
