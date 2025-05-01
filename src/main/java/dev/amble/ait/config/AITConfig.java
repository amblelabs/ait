package dev.amble.ait.config;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import me.shedaniel.autoconfig.serializer.PartitioningSerializer;

@Config(name = "aitconfig")
public class AITConfig extends PartitioningSerializer.GlobalData {

    @ConfigEntry.Category("server")
    @ConfigEntry.Gui.TransitiveObject
    public AITServerConfig SERVER = new AITServerConfig();

    @ConfigEntry.Category("client")
    @ConfigEntry.Gui.TransitiveObject
    public AITClientConfig CLIENT = new AITClientConfig();

    public static AITConfig createAndLoad() {
        AutoConfig.register(AITConfig.class, PartitioningSerializer.wrap(JanksonConfigSerializer::new));
        return AutoConfig.getConfigHolder(AITConfig.class).getConfig();
    }

    public enum TemperatureType {
        CELCIUS,
        FAHRENHEIT,
        KELVIN
    }
}
