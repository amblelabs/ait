package dev.amble.ait.core.tardis.control;

import java.util.List;

import net.minecraft.item.Item;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

import dev.amble.ait.core.AITItems;
import dev.amble.ait.core.AITSounds;
import dev.amble.ait.core.tardis.Tardis;

public class LightManipulatorControl extends Control {

    private final List<Item> itemList = List.of(
            AITItems.COFFEE,
            AITItems.TEA,
            AITItems.LATTE,
            AITItems.MILK,
            AITItems.WATER,
            AITItems.ICE_COFFEE,
            AITItems.COCO_MILK
    );
    private int currentIndex = 0;

    public LightManipulatorControl() {
        super("light_manipulator");
    }

    @Override
    public boolean runServer(Tardis tardis, ServerPlayerEntity player, ServerWorld world, BlockPos console, boolean leftClick) {
        if (world.isClient()) {
            return false;
        }

        currentIndex = (currentIndex + 1) % itemList.size();
        Item selectedItem = itemList.get(currentIndex);

        tardis.extra().setRefreshmentItem(selectedItem.getDefaultStack());
        player.sendMessage(Text.literal("Refreshment set to: " + selectedItem.getName().getString() + "!"), true);
        return true;
    }

    @Override
    public boolean requiresPower() {
        return true;
    }

    @Override
    public SoundEvent getSound() {
        return AITSounds.ALARM;
    }
}
