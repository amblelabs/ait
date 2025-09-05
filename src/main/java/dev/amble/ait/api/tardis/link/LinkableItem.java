package dev.amble.ait.api.tardis.link;

import java.util.List;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;

import dev.amble.ait.client.tardis.manager.ClientTardisManager;
import dev.amble.ait.core.tardis.Tardis;
import dev.amble.ait.core.tardis.TardisManager;

public abstract class LinkableItem extends Item {

    private final boolean showTooltip;
    private final String path;

    public LinkableItem(Settings settings, boolean showTooltip) {
        this(settings, "tardis", showTooltip);
    }

    public LinkableItem(Settings settings, String path, boolean showTooltip) {
        super(settings);

        this.path = path;
        this.showTooltip = showTooltip;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        this.handleTooltip(stack, tooltip);
        super.appendTooltip(stack, world, tooltip, context);
    }

    private void handleTooltip(ItemStack stack, List<Text> tooltip) {
        if (!showTooltip)
            return;

        UUID id = this.getTardisId(stack);

        if (id == null)
            return;

        if (!Screen.hasShiftDown()) {
            tooltip.add(Text.translatable("tooltip.ait.remoteitem.holdformoreinfo").formatted(Formatting.GRAY)
                    .formatted(Formatting.ITALIC));
            return;
        }

        ClientTardisManager.getInstance().getTardis(id, tardis -> {
            if (tardis != null) {
                tooltip.add(Text.literal("TARDIS: ").formatted(Formatting.BLUE));
                tooltip.add(Text.literal("> " + tardis.stats().getName()));
                tooltip.add(Text.literal("> " + tardis.getUuid().toString().substring(0, 8))
                        .formatted(Formatting.DARK_GRAY));
            }
        });
    }

    public void link(ItemStack stack, Tardis tardis) {
        this.link(stack, tardis.getUuid());
    }

    public void link(ItemStack stack, UUID uuid) {
        stack.getOrCreateNbt().putUuid(this.path, uuid);
    }

    public void unlink(ItemStack stack) {
        stack.getOrCreateNbt().remove(this.path);
    }

    public boolean isLinked(ItemStack stack) {
        return stack.getOrCreateNbt().contains(this.path);
    }

    public boolean isOf(ItemStack stack, Tardis tardis) {
        if (tardis == null)
            return false;

        return tardis.getUuid().equals(this.getTardisId(stack));
    }

    public UUID getTardisId(ItemStack stack) {
        NbtCompound nbt = stack.getOrCreateNbt();
        NbtElement element = nbt.get(path);

        if (element == null)
            return null;

        // convert old string data
        if (element.getType() == NbtElement.STRING_TYPE) {
            UUID converted = UUID.fromString(element.asString());

            nbt.putUuid(path, converted);
            return converted;
        }

        return NbtHelper.toUuid(element);
    }

    public Tardis getTardis(World world, ItemStack stack) {
        if (world == null)
            return null;

        UUID tardisId = this.getTardisId(stack);

        if (tardisId == null)
            return null;

        return TardisManager.with(world, (o, manager) ->
                manager.demandTardis(o, tardisId));
    }

    public static <T> T apply(ItemStack stack, BiFunction<LinkableItem, ItemStack, T> f) {
        if (!(stack.getItem() instanceof LinkableItem linkable))
            throw new IllegalArgumentException("Not a linkable!");

        return f.apply(linkable, stack);
    }

    public static void accept(ItemStack stack, BiConsumer<LinkableItem, ItemStack> c) {
        if (!(stack.getItem() instanceof LinkableItem linkable))
            throw new IllegalArgumentException("Not a linkable!");

        c.accept(linkable, stack);
    }

    public static void linkStatic(ItemStack stack, Tardis tardis) {
        accept(stack, (i, s) -> i.link(s, tardis));
    }

    public static void linkStatic(ItemStack stack, UUID id) {
        accept(stack, (i, s) -> i.link(s, id));
    }

    public static boolean isLinkedStatic(ItemStack stack) {
        return apply(stack, LinkableItem::isLinked);
    }

    public static boolean isOfStatic(ItemStack stack, Tardis tardis) {
        return apply(stack, (i, s) -> i.isOf(s, tardis));
    }

    public static UUID getTardisIdStatic(ItemStack stack) {
        return apply(stack, LinkableItem::getTardisId);
    }

    public static Tardis getTardisStatic(World world, ItemStack stack) {
        return apply(stack, (i, s) -> i.getTardis(world, s));
    }
}
