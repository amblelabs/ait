package dev.amble.ait.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = "ait-client")
public class AITClientConfig implements ConfigData {

    public float INTERIOR_HUM_VOLUME = 0.5f;

    public boolean CUSTOM_MENU = true;
    public boolean SHOW_EXPERIMENTAL_WARNING = false;
    public boolean ENVIRONMENT_PROJECTOR = true;
    public boolean DISABLE_LOYALTY_FOG = false;
    public boolean DISABLE_LOYALTY_BED_MESSAGE = false;
    public boolean ENABLE_TARDIS_BOTI = true;
    public boolean SHOULD_RENDER_BOTI_INTERIOR = false;
    public boolean GREEN_SCREEN_BOTI = false;
    public boolean SHOW_CONTROL_HITBOXES = false;
    public boolean RENDER_DEMAT_PARTICLES = true;
    public boolean ANIMATE_CONSOLE = true;
    public boolean ANIMATE_DOORS = true;
    /*public int DOOR_ANIMATION_SPEED = 2;*/

    @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
    public AITConfig.TemperatureType TEMPERATURE_TYPE = AITConfig.TemperatureType.CELCIUS;
}
