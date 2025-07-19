
package dev.amble.ait.core.loot;

import java.util.Random;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSyntaxException;

import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.function.ConditionalLootFunction;
import net.minecraft.loot.function.LootFunctionType;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

import dev.amble.ait.AITMod;
import dev.amble.ait.core.item.blueprint.BlueprintItem;
import dev.amble.ait.core.item.blueprint.BlueprintRegistry;
import dev.amble.ait.core.item.blueprint.BlueprintSchema;

public class SetBlueprintLootFunction extends ConditionalLootFunction {
    final BlueprintSchema blueprint;
    public Random random = AITMod.RANDOM;

    SetBlueprintLootFunction(LootCondition[] conditions, BlueprintSchema blueprint) {
        super(conditions);
        this.blueprint = blueprint;
    }

    @Override
    public LootFunctionType getType() {
        return BlueprintRegistry.BLUEPRINT_TYPE;
    }

    @Override
    public ItemStack process(ItemStack stack, LootContext context) {
        BlueprintItem.setSchema(stack, this.blueprint);
        return stack;
    }

    public static ConditionalLootFunction.Builder<?> builder(BlueprintSchema blueprint) {
        return SetBlueprintLootFunction
                .builder((LootCondition[] conditions) -> new SetBlueprintLootFunction(conditions, blueprint));
    }

    public static class Serializer extends ConditionalLootFunction.Serializer<SetBlueprintLootFunction> {
        @Override
        public void toJson(JsonObject jsonObject, SetBlueprintLootFunction setBlueprintLootFunction,
                JsonSerializationContext jsonSerializationContext) {
            super.toJson(jsonObject, setBlueprintLootFunction, jsonSerializationContext);
            jsonObject.addProperty("id",
                    setBlueprintLootFunction.blueprint.id().toString());
        }

        @Override
        public SetBlueprintLootFunction fromJson(JsonObject jsonObject,
                                                 JsonDeserializationContext jsonDeserializationContext, LootCondition[] lootConditions) {
            String string = JsonHelper.getString(jsonObject, "id");
            BlueprintSchema blueprint = BlueprintRegistry.getInstance().getOptional(Identifier.tryParse(string))
                    .orElseThrow(() -> new JsonSyntaxException("Unknown blueprint '" + string + "'"));
            return new SetBlueprintLootFunction(lootConditions, blueprint);
        }
    }
}
