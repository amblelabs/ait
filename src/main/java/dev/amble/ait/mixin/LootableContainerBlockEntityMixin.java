package dev.amble.ait.mixin;


import dev.amble.ait.core.item.SiegeTardisItem;
import dev.amble.lib.data.CachedDirectedGlobalPos;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LootableContainerBlockEntity.class)
public abstract class LootableContainerBlockEntityMixin {
    @Inject(method = "setStack(ILnet/minecraft/item/ItemStack;)V", at = @At("HEAD"))
    private void onSetStack(int slot, ItemStack stack, CallbackInfo ci) {
        if (stack == null || stack.isEmpty()) return;

        LootableContainerBlockEntity be = (LootableContainerBlockEntity)(Object)this;
        World world = be.getWorld();
        if (world == null || world.isClient) return;

        if (stack.getItem() instanceof SiegeTardisItem item) {
            var tardis = item.getTardis(world, stack);
            if (tardis != null) {
                tardis.travel().forcePosition(
                        CachedDirectedGlobalPos.create((ServerWorld)world, be.getPos(), (byte)0)
                );
            }
        }
    }
}

