package dev.amble.ait.client.config;

import dev.isxander.yacl3.api.NameableEnum;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.autogen.*;
import dev.isxander.yacl3.config.v2.api.autogen.Boolean;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import dev.isxander.yacl3.gui.ValueFormatters;
import dev.isxander.yacl3.platform.YACLPlatform;

import net.minecraft.text.Text;

import dev.amble.ait.AITMod;

public class AITClientConfig {

    public static final String CATEGORY = "client";

    public static final ConfigClassHandler<AITClientConfig> INSTANCE = ConfigClassHandler.createBuilder(AITClientConfig.class)
            .id(YACLPlatform.rl(AITMod.MOD_ID, "client"))
            .serializer(config -> GsonConfigSerializerBuilder.create(config)
                    .setPath(YACLPlatform.getConfigDir().resolve("ait-client.json5"))
                    .setJson5(true)
                    .build())
            .build();

    @AutoGen(category = CATEGORY)
    @CustomFormat(ValueFormatters.PercentFormatter.class)
    @FloatSlider(min = 0f, max = 1f, step = 0.05f)
    @SerialEntry public float interiorHumVolume = 0.5f;

    @AutoGen(category = CATEGORY)
    @Boolean(formatter = Boolean.Formatter.YES_NO, colored = true)
    @SerialEntry public boolean customMenu = true;

    @AutoGen(category = CATEGORY)
    @Boolean(formatter = Boolean.Formatter.YES_NO, colored = true)
    @SerialEntry public boolean showExperimentalWarning = false;

    @AutoGen(category = CATEGORY)
    @Boolean(formatter = Boolean.Formatter.YES_NO, colored = true)
    @SerialEntry public boolean environmentProjector = true;

    @AutoGen(category = CATEGORY)
    @Boolean(formatter = Boolean.Formatter.YES_NO, colored = true)
    @SerialEntry public boolean enableTardisBOTI = true;

    @AutoGen(category = CATEGORY)
    @Boolean(formatter = Boolean.Formatter.YES_NO, colored = true)
    @SerialEntry public boolean shouldRenderBOTIInterior = false;

    @AutoGen(category = CATEGORY)
    @Boolean(formatter = Boolean.Formatter.YES_NO, colored = true)
    @SerialEntry public boolean greenScreenBOTI = false;

    @AutoGen(category = CATEGORY)
    @Boolean(formatter = Boolean.Formatter.YES_NO, colored = true)
    @SerialEntry public boolean showControlHitboxes = false;

    @AutoGen(category = CATEGORY)
    @Boolean(formatter = Boolean.Formatter.YES_NO, colored = true)
    @SerialEntry public boolean showConsoleMonitorText = true;

    @AutoGen(category = CATEGORY)
    @Boolean(formatter = Boolean.Formatter.YES_NO, colored = true)
    @SerialEntry public boolean showCRTMonitorText = true;

    @AutoGen(category = CATEGORY)
    @Boolean(formatter = Boolean.Formatter.YES_NO, colored = true)
    @SerialEntry public boolean renderDematParticles = true;

    @AutoGen(category = CATEGORY)
    @Boolean(formatter = Boolean.Formatter.YES_NO, colored = true)
    @SerialEntry public boolean animateConsole = true;

    @AutoGen(category = CATEGORY)
    @Boolean(formatter = Boolean.Formatter.YES_NO, colored = true)
    @SerialEntry public boolean animateDoors = true;

    @AutoGen(category = CATEGORY)
    @EnumCycler
    @SerialEntry public TemperatureType temperatureType = TemperatureType.CELSIUS;

    public enum TemperatureType implements NameableEnum {
        CELSIUS,
        FAHRENHEIT,
        KELVIN;

        private final String key;

        TemperatureType() {
            String key1;
            key1 = this.toString().toLowerCase();
            key1 = key1.substring(0, 1).toUpperCase() + key1.substring(1);
            key1 = switch (key1) {
                case "Celsius" -> key1 + " (°C)";
                case "Fahrenheit" -> key1 + " (°F)";
                case "Kelvin" -> key1 + " (K)";
                default -> key1;
            };
            this.key = key1;
        }

        @Override
        public Text getDisplayName() {
            return Text.translatable(key);
        }
    }
}
