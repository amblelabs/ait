package dev.amble.ait.core.commands.argument;

import com.mojang.brigadier.context.CommandContext;

import net.minecraft.command.argument.EnumArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.StringIdentifiable;

import dev.amble.ait.core.tardis.handler.travel.TravelHandlerBase;

public class GroundSearchArgumentType extends EnumArgumentType<TravelHandlerBase.GroundSearch> {

    public static final StringIdentifiable.Codec<TravelHandlerBase.GroundSearch> CODEC = StringIdentifiable
            .createCodec(TravelHandlerBase.GroundSearch::values);

    protected GroundSearchArgumentType() {
        super(CODEC, TravelHandlerBase.GroundSearch::values);
    }

    public static GroundSearchArgumentType groundSearch() {
        return new GroundSearchArgumentType();
    }

    public static TravelHandlerBase.GroundSearch getGroundSearch(CommandContext<ServerCommandSource> context,
            String id) {
        return context.getArgument(id, TravelHandlerBase.GroundSearch.class);
    }
}
