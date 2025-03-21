package dev.amble.ait.config;

import java.util.List;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

import dev.amble.ait.core.AITDimensions;

@Config(name = "ait-server")
public class AITServerConfig implements ConfigData {

    public boolean MINIFY_JSON = false;
    public boolean GHOST_MONUMENT = true;
    public boolean LOCK_DIMENSIONS = true;
    public boolean RWF_ENABLED = true;
    public boolean TNT_CAN_TELEPORT_THROUGH_DOOR = true;

    @ConfigEntry.Gui.RequiresRestart
    public List<String> WORLDS_BLACKLIST = List.of();

    @ConfigEntry.Gui.RequiresRestart
    public List<String> TRAVEL_BLACKLIST = List.of(
            AITDimensions.TIME_VORTEX_WORLD.getValue().toString());

    public int TRAVEL_PER_TICK = 2;

    public int BULK_SIZE = 8;
    public int MAX_TARDISES = -1;
}
