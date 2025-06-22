package dev.amble.ait.core.tardis.handler.database;

import java.util.*;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

import dev.amble.ait.api.tardis.TardisComponent;
import dev.amble.ait.api.tardis.TardisTickable;
import dev.amble.ait.core.tardis.ServerTardis;
import dev.amble.ait.core.tardis.util.TardisUtil;
import dev.amble.ait.module.gun.core.item.BaseGunItem;

public class DatabaseHandler extends TardisComponent implements TardisTickable {
    private final Map<UUID, PersonalData> personalData;
    public DatabaseHandler(HashMap<UUID, PersonalData> data) {
        super(Id.DATABASE);
        personalData = data;
    }

    public DatabaseHandler() {
        this(new HashMap<>());
    }

    public Map<UUID, PersonalData> data() {
        return personalData;
    }

    public PersonalData get(PlayerEntity player) {
        return this.personalData.getOrDefault(player.getUuid(), new PersonalData(
                player.getUuid(), player.getName()));
    }

    public PersonalData set(ServerPlayerEntity player, PersonalData data) {
        this.personalData.put(player.getUuid(), data);

        this.sync();
        return data;
    }

    @Override
    public void tick(MinecraftServer server) {
        // Only run every 10 seconds (200 ticks)
        if (server.getTicks() % 200 != 0)
            return;

        for (ServerPlayerEntity player : TardisUtil.getPlayersInsideInterior((ServerTardis) tardis)) {
            this.checkPlayer(player, false);
        }
    }

    /**
     * Checks the player for any updates or changes in their personal data.
     * This method can be overridden to implement specific checks.
     *
     * @author Loqor
     * @param player The player to check.
     * @param shouldCheckTopBar If true, checks the top bar for updates.
     *  This is so players can sneak items through in the top bar that won't be picked up by the database.
     */
    private boolean checkPlayer(ServerPlayerEntity player, boolean shouldCheckTopBar) {
        Collection<ItemStack> stackCollection = getItemsInInventory(player, shouldCheckTopBar);
        if (stackCollection.size() > 4) {
            this.setDangerLevel(player, DangerLevel.EXTREME);
        } else {
            this.setDangerLevel(player, DangerLevel.values()[stackCollection.size()]);
        }

        return !stackCollection.isEmpty();
    }

    public void setDangerLevel(ServerPlayerEntity player, DangerLevel level) {
        PersonalData data = this.get(player);
        PersonalData newData = new PersonalData(data, level);
        this.set(player, newData);
    }

    private static Collection<ItemStack> getItemsInInventory(PlayerEntity player, boolean shouldCheckTopBar) {
        List<ItemStack> items = new ArrayList<>();
        PlayerInventory inv = player.getInventory();

        // The top bar is the 27-36 slots in the PlayerInventory
        for (int i = 0; i < inv.main.size(); i++) {
            ItemStack stack = inv.main.get(i);
            boolean isTopBar = i < 27;
            if (stack.isIn(ItemTags.SWORDS) || stack.isIn(ItemTags.AXES) || stack.getItem() instanceof BaseGunItem) { // TODO Replace with AITTags.Items.DANGER_ITEMS
                // You can use `isHotbar` here to check if the item is in the top bar
                if (!shouldCheckTopBar && !isTopBar) {
                    items.add(stack);
                }
            }
        }

        return items;
    }

    /*public void update(ServerPlayerEntity player, Function<PersonalData, PersonalData> consumer) {
        PersonalData current = this.get(player);
        current = consumer.apply(current);

        this.set(player, current);
    }*/
}
