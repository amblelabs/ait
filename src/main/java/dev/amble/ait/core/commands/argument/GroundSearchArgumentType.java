package dev.amble.ait.core.commands.argument;

import com.mojang.brigadier.context.CommandContext;
import dev.amble.ait.core.util.SafePosSearch;
import net.minecraft.command.argument.EnumArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.StringIdentifiable;

public class GroundSearchArgumentType extends EnumArgumentType<SafePosSearch.Kind> {

    public static final StringIdentifiable.Codec<SafePosSearch.Kind> CODEC = StringIdentifiable
            .createCodec(SafePosSearch.Kind::values);

    protected GroundSearchArgumentType() {
        super(CODEC, SafePosSearch.Kind::values);
    }

    public static GroundSearchArgumentType groundSearch() {
        return new GroundSearchArgumentType();
    }

    public static SafePosSearch.Kind getGroundSearch(CommandContext<ServerCommandSource> context,
            String id) {
        return context.getArgument(id, SafePosSearch.Kind.class);
    }
}
