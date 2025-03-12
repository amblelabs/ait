package dev.amble.ait.core.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class AncientManualItem extends Item {

    private static final String BOUND_PLAYER = "BoundPlayerUUID";
    private static final String BURNT_PAGES_KEY = "BurntPages";

    public AncientManualItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);

        // Handle binding the manual to the player if it's not bound yet
        if (!world.isClient && !stack.hasNbt() || !stack.getNbt().contains(BOUND_PLAYER)) {
            bindManualToPlayer(stack, player);
            openManualGui(player, stack, hand);
            return TypedActionResult.success(stack);
        }

        NbtCompound nbt = stack.getNbt();
        if (nbt != null && nbt.contains(BOUND_PLAYER)) {
            String boundPlayerUUID = nbt.getString(BOUND_PLAYER);
            if (boundPlayerUUID.equals(player.getUuid().toString())) {
                openManualGui(player, stack, hand);
            } else {
                player.sendMessage(Text.of("No matter how hard you try, the pages refuse to turn...".formatted(Formatting.GRAY)), true);
            }
        }


        ItemStack mainHand = player.getMainHandStack();
        ItemStack offHand = player.getOffHandStack();

        if (offHand.getItem() instanceof AncientManualItem && mainHand.getItem() instanceof BurntPagesItem) {
            //addBurntPageToManual(offHand, mainHand);
            player.setStackInHand(Hand.MAIN_HAND, ItemStack.EMPTY);
            player.sendMessage(Text.of("A burnt page has been added to the manual...".formatted(Formatting.GRAY)), true);
            return TypedActionResult.success(offHand);
        }

        return TypedActionResult.pass(stack);
    }

    private void bindManualToPlayer(ItemStack manual, PlayerEntity player) {
        NbtCompound nbt = manual.getOrCreateNbt();
        nbt.putString(BOUND_PLAYER, player.getUuid().toString());
        manual.setNbt(nbt);
    }

    private void openManualGui(PlayerEntity player, ItemStack manual, Hand hand) {
        player.useBook(manual, hand);
    }
}
