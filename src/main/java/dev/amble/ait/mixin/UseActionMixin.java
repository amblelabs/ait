package dev.amble.ait.mixin;

import java.util.ArrayList;
import java.util.List;

import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.util.UseAction;

import dev.amble.ait.api.AITUseActions;

@Mixin(UseAction.class)
public class UseActionMixin implements AITUseActions {

    @Shadow
    @Final
    @Mutable
    private static UseAction[] field_8948;

    private static final UseAction SONIC = register("SONIC");

    @Invoker("<init>")
    private static UseAction init(String name, int ordinal) {
        throw new AssertionError();
    }

    @Override
    public UseAction ait$sonic() {
        return SONIC;
    }

    @Unique private static UseAction register(String name) {
        UseAction result = init(name, UseAction.values().length);

        List<UseAction> actions = new ArrayList<>(List.of(field_8948));
        actions.add(result);

        field_8948 = actions.toArray(new UseAction[0]);
        return result;
    }
}
