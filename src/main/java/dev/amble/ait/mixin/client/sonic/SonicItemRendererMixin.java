package dev.amble.ait.mixin.client.sonic;

import dev.amble.ait.core.AITItems;
import dev.amble.ait.core.item.SonicItem;
import dev.amble.ait.data.schema.sonic.SonicSchema;
import net.minecraft.client.render.item.ItemModels;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemRenderer.class)
public class SonicItemRendererMixin {

    @Shadow @Final private ItemModels models;

    @Inject(method = "getModel", at = @At("HEAD"), cancellable = true)
    public void getModel(ItemStack stack, World world, LivingEntity entity, int seed, CallbackInfoReturnable<BakedModel> cir) {
        if (!stack.isOf(AITItems.SONIC_SCREWDRIVER))
            return;

        SonicSchema.Models models = SonicItem.schema(stack).models();
        BakedModel model;

        if (entity == null || !(entity.getActiveItem() == stack && entity.isUsingItem())) {
            model = this.getOrMissing(models.inactive());
        } else {
            model = this.getOrMissing(SonicItem.mode(stack).model(models));
        }

        model.getOverrides().apply(model, stack, (ClientWorld) world, entity, seed);
        cir.setReturnValue(model);
    }

    @Unique private BakedModel getOrMissing(Identifier id) {
        BakedModel model = this.models.getModelManager().getModel(
                id
        );

        if (model == null)
            return this.models.getModelManager().getMissingModel();

        return model;
    }
}
