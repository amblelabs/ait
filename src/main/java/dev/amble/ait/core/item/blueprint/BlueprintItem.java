package dev.amble.ait.core.item.blueprint;

import static dev.amble.ait.client.util.TooltipUtil.addShiftHiddenTooltip;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import dev.amble.ait.AITMod;
import dev.amble.ait.core.AITItems;

public class BlueprintItem extends Item {

    public BlueprintItem(Settings settings) {
        super(settings.maxCount(1));
    }

    @Override
    public ItemStack getDefaultStack() {
        ItemStack stack = super.getDefaultStack();
        BlueprintSchema blueprint = BlueprintRegistry.getInstance().getRandom();

        if (blueprint != null) {
            NbtCompound nbt = stack.getOrCreateNbt();
            nbt.putString("Blueprint", blueprint.id().toString());
        }

        return stack;
    }

    @Override
    public void postProcessNbt(NbtCompound nbt) {
        super.postProcessNbt(nbt);

        if (!nbt.contains("Blueprint")) {
            nbt.putString("Blueprint", BlueprintRegistry.getInstance().getRandom().id().toString());
        }
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);

        addShiftHiddenTooltip(stack, tooltip, tooltips -> {
            BlueprintSchema blueprint = getSchema(stack);
            if (blueprint == null) return;

            tooltip.add(Text.translatable("ait.blueprint.tooltip").formatted(Formatting.BLUE)
                    .append(blueprint.text().copy().formatted(Formatting.GRAY)));

            for (int i = blueprint.inputs().size() - 1; i >= 0; i--) {
                tooltip.add(blueprint.inputs().get(i).text().copy().formatted(Formatting.DARK_GRAY));
            }
        });

    }

    public static BlueprintSchema getSchema(ItemStack stack) {
        NbtCompound nbt = stack.getOrCreateNbt();
        NbtElement element = nbt.get("Blueprint");

        if (element == null) {
            AITMod.LOGGER.warn("Blueprint item has no blueprint data!");

            BlueprintSchema schema = BlueprintRegistry.getInstance().getRandom();
            nbt.putString("Blueprint", schema.id().toString());
            return schema;
        }

        Identifier id = Identifier.tryParse(element.asString());

        if (id == null) {
            AITMod.LOGGER.warn("Couldn't parse blueprint id: '{}'", element.asString());
            return null;
        }

        BlueprintSchema schema = BlueprintRegistry.getInstance().get(id);

        if (schema == null) {
            AITMod.LOGGER.warn("Couldn't find blueprint with id: '{}'!", id);
            return null;
        }

        return schema;
    }

    public static ItemStack createStack(BlueprintSchema schema) {
        ItemStack stack = new ItemStack(AITItems.BLUEPRINT);

        setSchema(stack, schema);

        return stack;
    }
    public static ItemStack setSchema(ItemStack stack, BlueprintSchema schema) {
        NbtCompound nbt = stack.getOrCreateNbt();
        nbt.putString("Blueprint", schema.id().toString());
        return stack;
    }
}
