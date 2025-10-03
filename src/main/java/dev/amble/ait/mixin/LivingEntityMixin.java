package dev.amble.ait.mixin;

import java.util.UUID;

import com.mojang.datafixers.util.Either;
import net.fabricmc.fabric.api.util.TriState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;

import dev.amble.ait.api.ExtraPushableEntity;
import dev.amble.ait.core.AITTags;
import dev.amble.ait.core.tardis.ServerTardis;
import dev.amble.ait.core.tardis.handler.mood.v2.Emotion;
import dev.amble.ait.core.tardis.handler.mood.v2.MoodHandler2;
import dev.amble.ait.core.tardis.manager.ServerTardisManager;
import dev.amble.ait.core.world.TardisServerWorld;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements ExtraPushableEntity {

    @Unique private TriState ait$pushable = TriState.DEFAULT;

    @Shadow public abstract ItemStack getEquippedStack(EquipmentSlot var1);

    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    public void ait$tick(CallbackInfo ci) {
        LivingEntity entity = (LivingEntity) (Object) this;

        if (entity instanceof PlayerEntity player
                && (player.isCreative() || player.isSpectator()))
             return;

        ItemStack stack = entity.getEquippedStack(EquipmentSlot.HEAD);

        if (stack.isIn(AITTags.Items.FULL_RESPIRATORS) || stack.isIn(AITTags.Items.HALF_RESPIRATORS))
            return;

        if (entity.getWorld() instanceof TardisServerWorld tardisWorld && !tardisWorld.getTardis().isGrowth()
                && !tardisWorld.getTardis().subsystems().lifeSupport().isEnabled()) {
            entity.addStatusEffect(new StatusEffectInstance(StatusEffects.WITHER, 1,
                    200, false, false));
            entity.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS,
                    200, 1, false, false));
        }
    }

    @Override
    public void ait$setPushBehaviour(TriState pushable) {
        this.ait$pushable = pushable;
    }

    @Override
    public TriState ait$pushBehaviour() {
        return ait$pushable;
    }

    @Inject(
            method = "eatFood",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/item/ItemStack;decrement(I)V",
                    shift = At.Shift.AFTER
            )
    )
    private void ait$eatFood(World world, ItemStack stack, CallbackInfoReturnable<ItemStack> cir){
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

    @Inject(method = "isPushable", at = @At("RETURN"), cancellable = true)
    public void isPushable(CallbackInfoReturnable<Boolean> cir) {
        boolean pushable = cir.getReturnValueZ();

        if (this.ait$pushable != TriState.DEFAULT)
            pushable = this.ait$pushable.get();

        cir.setReturnValue(pushable);
    }
}
