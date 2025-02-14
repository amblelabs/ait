package dev.amble.ait.data.datapack;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import dev.amble.ait.AITMod;
import dev.amble.ait.data.schema.MachineRecipeSchema;

public class DatapackMachineRecipe extends MachineRecipeSchema {

    public static final Codec<MachineRecipeSchema> CODEC = RecordCodecBuilder
            .create(instance -> instance
                    .group(Identifier.CODEC.fieldOf("id").forGetter(MachineRecipeSchema::id),
                            ItemStack.CODEC.fieldOf("output").forGetter(MachineRecipeSchema::output),
                            Codec.list(ItemStack.CODEC).fieldOf("input")
                                    .forGetter(schema -> new ArrayList<>(schema.input())))
                    .apply(instance, (DatapackMachineRecipe::new)));

    public DatapackMachineRecipe(Identifier id, ItemStack output, List<ItemStack> input) {
        super(id, output, new ArrayList<>(input));
    }

    public static MachineRecipeSchema fromInputStream(InputStream stream) {
        return fromJson(JsonParser.parseReader(new InputStreamReader(stream)).getAsJsonObject());
    }

    public static MachineRecipeSchema fromJson(JsonObject json) {
        AtomicReference<MachineRecipeSchema> created = new AtomicReference<>();

        CODEC.decode(JsonOps.INSTANCE, json).get().ifLeft(recipe -> {
            created.set(recipe.getFirst());
        }).ifRight(err -> {
            created.set(null);
            AITMod.LOGGER.error("Error decoding datapack machine recipe: " + err);
        });

        return created.get();
    }
}
