package dev.amble.ait.core.tardis.handler.database;

import java.util.UUID;

import com.mojang.authlib.GameProfile;
import dev.amble.lib.data.DirectedGlobalPos;
import dev.amble.lib.util.ServerLifecycleHooks;

import net.minecraft.text.Text;

public record PersonalData(UUID uuid, GameProfile gameProfile, Text currentName, DangerLevel dangerLevel, DirectedGlobalPos lastKnownPosition) {
    public PersonalData(UUID uuid, GameProfile gameProfile, Text currentName, DangerLevel dangerLevel, DirectedGlobalPos lastKnownPosition) {
        this.uuid = uuid;
        this.gameProfile = gameProfile;
        this.currentName = currentName;
        this.dangerLevel = dangerLevel;
        this.lastKnownPosition = lastKnownPosition;
    }

    public PersonalData(UUID uuid, Text currentName) {
        this(uuid, ServerLifecycleHooks.get().getPlayerManager().getPlayer(uuid).getGameProfile(), currentName, DangerLevel.NONE, null);
    }

    public PersonalData(UUID uuid, Text currentName, DangerLevel dangerLevel) {
        this(uuid, ServerLifecycleHooks.get().getPlayerManager().getPlayer(uuid).getGameProfile(), currentName, dangerLevel, null);
    }

    public PersonalData(PersonalData data, DangerLevel dangerLevel) {
        this(data.uuid, data.gameProfile, data.currentName, dangerLevel, data.lastKnownPosition);
    }
}
