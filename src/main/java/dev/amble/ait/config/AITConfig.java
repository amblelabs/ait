package dev.amble.ait.config;

import java.util.List;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;

import dev.amble.ait.core.AITDimensions;

@Config(name = "aitconfig")
public class AITConfig implements ConfigData {

    @ConfigEntry.Category("server")
    @ConfigEntry.Gui.TransitiveObject
    public Server SERVER = new Server();

    @ConfigEntry.Category("client")
    @ConfigEntry.Gui.TransitiveObject
    public Client CLIENT = new Client();

    public static AITConfig createAndLoad() {
        AutoConfig.register(AITConfig.class, JanksonConfigSerializer::new);
        return AutoConfig.getConfigHolder(AITConfig.class).getConfig();
    }

    public static class Server {
        public boolean MINIFY_JSON = false;
        public boolean GHOST_MONUMENT = true;
        public boolean LOCK_DIMENSIONS = true;
        public boolean RWF_ENABLED = true;

        @ConfigEntry.Gui.RequiresRestart
        public List<String> WORLDS_BLACKLIST = List.of(
                AITDimensions.TIME_VORTEX_WORLD.getValue().toString());

        public int TRAVEL_PER_TICK = 2;

        public boolean SEND_BULK = true;
        public int MAX_TARDISES = -1;
    }

    public static class Client {
        public float INTERIOR_HUM_VOLUME = 0.5f;

        public boolean CUSTOM_MENU = true;
        public boolean SHOW_EXPERIMENTAL_WARNING = false;
        public boolean ENVIRONMENT_PROJECTOR = true;
        public boolean DISABLE_LOYALTY_FOG = false;
        public boolean DISABLE_LOYALTY_SLEEPING_ACTIONBAR = false;
        public boolean ENABLE_TARDIS_BOTI = true;
        public boolean SHOULD_RENDER_BOTI_INTERIOR = false;
        public boolean SHOW_CONTROL_HITBOXES = false;
        public boolean RENDER_DEMAT_PARTICLES = true;
        public boolean ANIMATE_CONSOLE = true;
        public boolean ANIMATE_DOORS = true;
        /*public int DOOR_ANIMATION_SPEED = 2;*/

        @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
        public TemperatureType TEMPERATURE_TYPE = TemperatureType.CELCIUS;
    }

    public enum TemperatureType {
        CELCIUS,
        FAHRENHEIT,
        KELVIN
    }
}
