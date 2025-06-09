package dev.amble.ait.registry.impl.responses;

import dev.amble.ait.AITMod;
import dev.amble.ait.core.handles.HandlesResponse;
import dev.amble.ait.core.handles.HandlesSound;
import dev.amble.ait.core.tardis.ServerTardis;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class TakeOffResponse implements HandlesResponse {
    @Override
    public boolean run(ServerPlayerEntity player, HandlesSound source, ServerTardis tardis) {
        return false;
    }

    @Override
    public List<String> getCommandWords() {
        List<String> list = new ArrayList<>();
        // Will add them later im lazy as fuck
        return list;
    }

    @Override
    public Identifier id() {
        return AITMod.id("take_off");
    }
}
