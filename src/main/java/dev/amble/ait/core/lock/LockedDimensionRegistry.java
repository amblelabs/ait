package dev.amble.ait.core.lock;

import java.util.ArrayList;
import java.util.List;

import dev.amble.lib.register.datapack.SimpleDatapackRegistry;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.resource.ResourceType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionTypes;

import dev.amble.ait.AITMod;
import dev.amble.ait.core.tardis.ServerTardis;
import dev.amble.ait.core.tardis.Tardis;
import dev.amble.ait.core.util.WorldUtil;

public class LockedDimensionRegistry extends SimpleDatapackRegistry<LockedDimension> {
    private static final LockedDimensionRegistry instance = new LockedDimensionRegistry();


    public LockedDimensionRegistry() {
        super(LockedDimension::fromInputStream, LockedDimension.CODEC, "locked_dimension", true, AITMod.MOD_ID);
    }

    public static LockedDimension NETHER;

    @Override
    public void onCommonInit() {
        super.onCommonInit();
        this.defaults();
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(this);
    }

    @Override
    protected void defaults() {
        NETHER = register(new LockedDimension(DimensionTypes.THE_NETHER_ID, new ItemStack(Items.BLAZE_ROD)));
        // all others should be in datapack
    }

    @Override
    public LockedDimension fallback() {
        return NETHER;
    }

    public LockedDimension get(World world) {
        return this.get(world.getRegistryKey().getValue());
    }
    public List<LockedDimension> forStack(ItemStack stack) {
        // ow :(
        List<LockedDimension> copy = new ArrayList<>(this.REGISTRY.values());

        copy.removeIf((dim) -> !(dim.stack().getItem().equals(stack.getItem())));

        return copy;
    }

    public static LockedDimensionRegistry getInstance() {
        return instance;
    }

    public static boolean tryUnlockDimension(ServerPlayerEntity player, ItemStack held, ServerTardis tardis) {
        if (held.isEmpty()) return false;
        if (!AITMod.CONFIG.lockDimensions) return false;

        List<LockedDimension> dims = getInstance().forStack(held);

        if (dims.isEmpty()) return false;

        dims.forEach(dim -> {
            tardis.stats().unlock(dim);

            player.sendMessage(dim.text().copy().append(" unlocked!").formatted(Formatting.BOLD, Formatting.ITALIC,
                    Formatting.GOLD), false);
        });
        player.getServerWorld().playSound(null, player.getBlockPos(), SoundEvents.UI_TOAST_CHALLENGE_COMPLETE,
                SoundCategory.PLAYERS, 0.2F, 1.0F);

        held.decrement(1);

        return true;
    }

    public boolean isUnlocked(Tardis tardis, World world) {
        if (!AITMod.CONFIG.lockDimensions)
            return true;

        if (isEnd(world))
            return WorldUtil.isEndDragonDead();

        LockedDimension dim = this.get(world);
        return dim == null || tardis.isUnlocked(dim);
    }
    private boolean isEnd(World world) {
        return world.getRegistryKey().getValue().equals(World.END.getValue());
    }
}
