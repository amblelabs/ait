package dev.amble.ait.mixin.fix;

import java.util.Optional;
import java.util.function.Consumer;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.nbt.NbtElement;

@Mixin(LivingEntity.class)
public class LivingEntityBrainFixMixin {

    @Shadow protected Brain<?> brain;

    @Redirect(method = "remove", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ai/brain/Brain;forgetAll()V"))
    public void remove(Brain instance) {
        if (instance != null)
            instance.forgetAll();
    }

    @Redirect(method = "writeCustomDataToNbt", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ai/brain/Brain;encode(Lcom/mojang/serialization/DynamicOps;)Lcom/mojang/serialization/DataResult;"))
    public DataResult<NbtElement> encodeBrain(Brain instance, DynamicOps<?> ops) {
        return null;
    }

    @Redirect(method = "writeCustomDataToNbt", at = @At(value = "INVOKE", target = "Lcom/mojang/serialization/DataResult;resultOrPartial(Ljava/util/function/Consumer;)Ljava/util/Optional;"))
    public Optional<NbtElement> resultOrPartial(DataResult<NbtElement> instance, Consumer<String> onError) {
        if (instance == null)
            return Optional.empty();

        return instance.resultOrPartial(onError);
    }
}
