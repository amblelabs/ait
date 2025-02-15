package dev.amble.ait.core;

import java.util.function.Supplier;

import com.mojang.brigadier.arguments.ArgumentType;
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;

import net.minecraft.command.argument.serialize.ConstantArgumentSerializer;

import dev.amble.ait.AITMod;
import dev.amble.ait.core.commands.argument.*;

public class AITArgumentTypes {

    public static void register() {
        register("tardis", TardisArgumentType.class, TardisArgumentType::tardis);
        register("wildcard_resource_location", IdentifierWildcardArgumentType.class,
                IdentifierWildcardArgumentType::wildcard);
        register("permission", PermissionArgumentType.class, PermissionArgumentType::permission);
        register("json", JsonElementArgumentType.class, JsonElementArgumentType::jsonElement);
        register("ground_search", GroundSearchArgumentType.class, GroundSearchArgumentType::groundSearch);
    }

    private static <T extends ArgumentType<?>> void register(String name, Class<T> t, Supplier<T> supplier) {
        ArgumentTypeRegistry.registerArgumentType(AITMod.id(name), t,
                ConstantArgumentSerializer.of(supplier));
    }
}
