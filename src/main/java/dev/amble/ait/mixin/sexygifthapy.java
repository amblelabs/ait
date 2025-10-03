package dev.amble.ait.mixin;

import com.mojang.datafixers.util.Either;
import dev.amble.ait.core.tardis.ServerTardis;
import dev.amble.ait.core.tardis.handler.mood.v2.Emotion;
import dev.amble.ait.core.tardis.handler.mood.v2.MoodHandler2;
import dev.amble.ait.core.tardis.manager.ServerTardisManager;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

@Mixin(LivingEntity.class)
public class sexygifthapy {
    @Inject(
            method = "eatFood",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/item/ItemStack;decrement(I)V",
                    shift = At.Shift.AFTER
            )
    )
    private void ifforcookie(World world, ItemStack stack, CallbackInfoReturnable<ItemStack> cir){
        NbtCompound cookietag = stack.getNbt();
        boolean hasTagWithData = cookietag != null && cookietag.contains("Identifier") && cookietag.contains("Data", 10);

        if (stack.getItem() == Items.COOKIE && hasTagWithData){
            NbtCompound data = cookietag.getCompound("Data");
            UUID uuid = data.getUuid("Uuid");

            MinecraftServer server = world.getServer();
            if (server != null) {
                Either<ServerTardis, ?> either = ServerTardisManager.getInstance().lookup().get(uuid);
                if (either != null) {
                    ServerTardis tardis = either.map(t -> t, o -> null);
                    if (tardis != null) {
                        MoodHandler2 mood = new MoodHandler2();
                        mood.add(Emotion.Type.CONTENT, data.getFloat("Multiplier"));
                    }
                }
            }
        }
    }
}
