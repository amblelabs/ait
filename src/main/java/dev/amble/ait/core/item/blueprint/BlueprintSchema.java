package dev.amble.ait.core.item.blueprint;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.amble.lib.api.Identifiable;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.util.Identifier;

import dev.amble.ait.AITMod;

public record BlueprintSchema(Identifier id, Text text, InputList inputs, ItemStack output) implements Identifiable {
    public static Codec<BlueprintSchema> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ItemStack.CODEC.fieldOf("output").forGetter(BlueprintSchema::output),
            InputList.CODEC.fieldOf("inputs").forGetter(BlueprintSchema::inputs)
    ).apply(instance, BlueprintSchema::new));

    public BlueprintSchema(ItemStack output, InputList inputs) {
        this(Registries.ITEM.getId(output.getItem()), Text.translatable(output.getTranslationKey()), inputs, output);
    }

    public Blueprint create() {
        return new Blueprint(this);
    }

    @Override
    public String toString() {
        return "BlueprintSchema{" +
                "id=" + id +
                ", text=" + text +
                ", inputs=" + inputs +
                ", output=" + output +
                '}';
    }

    public static BlueprintSchema fromInputStream(InputStream stream) {
        return fromJson(JsonParser.parseReader(new InputStreamReader(stream)).getAsJsonObject());
    }

    public static BlueprintSchema fromJson(JsonObject json) {
        AtomicReference<BlueprintSchema> created = new AtomicReference<>();

        CODEC.decode(JsonOps.INSTANCE, json).get().ifLeft(planet -> created.set(planet.getFirst())).ifRight(err -> {
            created.set(null);
            AITMod.LOGGER.error("Error decoding datapack blueprint: {}", err);
        });

        return created.get();
    }

    public static class Input {
        public static Codec<Input> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Identifier.CODEC.fieldOf("item").forGetter(input -> Registries.ITEM.getId(input.item)),
                Codec.INT.fieldOf("minCount").forGetter(Input::minimum),
                Codec.INT.fieldOf("maxCount").forGetter(Input::maximum)
        ).apply(instance, Input::new));

        private final Item item;
        private final int maxCount;
        private final int minCount;

        public Input(Item item, int minCount, int maxCount) {
            this.item = item;
            this.minCount = minCount;
            this.maxCount = maxCount;
        }
        public Input(Item item, int count) {
            this(item, count, count);
        }
        public Input(Item item) {
            this(item, 1);
        }
        public Input(ItemStack stack) {
            this(stack.getItem(), stack.getCount());
        }

        private Input(Identifier item, Integer min, Integer max) {
            this(Registries.ITEM.get(item), min, max);
        }

        public int minimum() {
            return minCount;
        }
        public int maximum() {
            return maxCount;
        }

        /**
         * Converts this input to a stack with a random count between minCount and maxCount
         */
        public ItemStack toStack() {
            return new ItemStack(item, minCount + (int) (Math.random() * (maxCount - minCount)));
        }

        public ItemStack maxCountStack() {
            return new ItemStack(item, maxCount);
        }

        @Override
        public String toString() {
            return "Input{" +
                    "item=" + item +
                    ", minCount=" + minCount +
                    ", maxCount=" + maxCount +
                    '}';
        }

        public Text text() {
            Text countText = minCount == maxCount ? Text.literal(String.valueOf(minCount)) : Text.literal(minCount + "-" + maxCount);

            return Texts.bracketed(Text.translatable(item.getTranslationKey()))
                    .append(" x")
                    .append(countText);
        }
    }
    public static class InputList extends ArrayList<Input> {
        public static Codec<InputList> CODEC = Input.CODEC.listOf().flatXmap(l -> {
            InputList list = new InputList();
            list.addAll(l);
            return DataResult.success(list);
        }, DataResult::success);

        public InputList() {
            super();
        }
        public InputList(Input... inputs) {
            super(List.of(inputs));
        }
        public InputList(ItemStack... stacks) {
            super();
            for (ItemStack stack : stacks) {
                add(new Input(stack));
            }
        }
        public List<ItemStack> toStacks() {
            List<ItemStack> stacks = new ArrayList<>();
            for (Input input : this) {
                stacks.add(input.toStack());
            }
            return stacks;
        }
    }
}
