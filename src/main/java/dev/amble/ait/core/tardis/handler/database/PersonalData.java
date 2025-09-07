package dev.amble.ait.core.tardis.handler.database;

import com.mojang.authlib.GameProfile;
import dev.amble.lib.data.DirectedGlobalPos;
import net.minecraft.text.Text;

import java.util.UUID;
import java.util.function.Supplier;

public record PersonalData(UUID uuid, Text currentName, DangerLevel dangerLevel, DirectedGlobalPos lastKnownPosition) {
    public PersonalData(UUID uuid, Text currentName, DangerLevel dangerLevel, DirectedGlobalPos lastKnownPosition) {
        this.uuid = uuid;
        this.currentName = currentName;
        this.dangerLevel = dangerLevel;
        this.lastKnownPosition = lastKnownPosition;
    }

    public PersonalData(UUID uuid, Text currentName) {
        this(uuid, currentName, DangerLevel.NONE, null);
    }

    public PersonalData(UUID uuid, Text currentName, DangerLevel dangerLevel) {
        this(uuid, currentName, dangerLevel, null);
    }

    public PersonalData(PersonalData data, DangerLevel dangerLevel) {
        this(data.uuid, data.currentName, dangerLevel, data.lastKnownPosition);
    }

    public Supplier<GameProfile> profile() {
        return () -> new GameProfile(uuid, currentName.getString());
    }
}
