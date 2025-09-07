package dev.amble.ait.config;

import java.util.List;

import com.google.common.collect.Lists;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.controller.ControllerBuilder;
import dev.isxander.yacl3.api.controller.StringControllerBuilder;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.ConfigField;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.autogen.*;
import dev.isxander.yacl3.config.v2.api.autogen.Boolean;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import dev.isxander.yacl3.platform.YACLPlatform;

import dev.amble.ait.AITMod;
import dev.amble.ait.core.AITDimensions;

public class AITServerConfig {

    public static final String CATEGORY = "server";

    public static final ConfigClassHandler<AITServerConfig> INSTANCE = ConfigClassHandler.createBuilder(AITServerConfig.class)
            .id(YACLPlatform.rl(AITMod.MOD_ID, "server"))
            .serializer(config -> GsonConfigSerializerBuilder.create(config)
                    .setPath(YACLPlatform.getConfigDir().resolve("ait-server.json5"))
                    .setJson5(true)
                    .build())
            .build();

    @AutoGen(category = CATEGORY)
    @Boolean(formatter = Boolean.Formatter.YES_NO, colored = true)
    @CustomDescription(value = "Shrinks down the JSON exported from TARDIS Data")
    @SerialEntry public boolean minifyJson = false;

    @AutoGen(category = CATEGORY)
    @Boolean(formatter = Boolean.Formatter.YES_NO, colored = true)
    @CustomDescription(value = "Allows the TARDIS to constantly dematerialise and rematerialize in the same spot when taking off without a pilot inside.")
    @CustomImage(value = "textures/yacl3/server/tardis-demat.webp", width = 854, height = 480)
    @SerialEntry public boolean ghostMonument = true;

    @AutoGen(category = CATEGORY)
    @Boolean(formatter = Boolean.Formatter.YES_NO, colored = true)
    @CustomDescription(value = "Adds more progression into the game by locking dimensions until you get an specific item or by being inside the dimension to unlock it.")
    @CustomImage(value = "textures/yacl3/server/locked-dim.webp", width = 854, height = 480)
    @SerialEntry public boolean lockDimensions = true;

    @AutoGen(category = CATEGORY)
    @Boolean(formatter = Boolean.Formatter.YES_NO, colored = true)
    @CustomDescription(value = "Allows the TARDIS feature of Real World Flight where you can travel manually. [BETA FEATURE]")
    @CustomImage(value = "textures/yacl3/server/rwf.webp", width = 854, height = 480)
    @SerialEntry public boolean rwfEnabled = false;

    @AutoGen(category = CATEGORY)
    @Boolean(formatter = Boolean.Formatter.YES_NO, colored = true)
    @CustomDescription(value = "Allow the TNT (when ignited) to be able to enter through the TARDIS Doors.")
    @CustomImage(value = "textures/yacl3/server/disable-tnt.webp", width = 854, height = 480)
    @SerialEntry public boolean tntCanTeleportThroughDoors = true;

    @AutoGen(category = CATEGORY)
    @Boolean(formatter = Boolean.Formatter.YES_NO, colored = true)
    @CustomDescription(value = "Allows the Hypercube Item to be used.")
    @CustomImage(value = "textures/yacl3/server/distress-cube.webp", width = 854, height = 480)
    @SerialEntry public boolean hypercubesEnabled = true;

    @AutoGen(category = CATEGORY)
    @ListGroup(valueFactory = StringListFactory.class, controllerFactory = StringListFactory.class)
    @CustomDescription(value = "Blacklist dimensions that the Environment Projector can display.")
    @SerialEntry public List<String> projectorBlacklist = Lists.newArrayList(
            "ait-tardis");

    @AutoGen(category = CATEGORY)
    @ListGroup(valueFactory = StringListFactory.class, controllerFactory = StringListFactory.class)
    @CustomDescription(value = "Blacklist dimensions that the TARDIS can travel to.")
    @SerialEntry public List<String> travelBlacklist = Lists.newArrayList(
            "ait-tardis", "ait:tardis_dimension_type", AITDimensions.TIME_VORTEX_WORLD.getValue().toString(), "ait:space");

    @AutoGen(category = CATEGORY)
    @ListGroup(valueFactory = StringListFactory.class, controllerFactory = StringListFactory.class)
    @CustomDescription(value = "Blacklist dimensions that Rifts can spawn in.")
    @SerialEntry public List<String> riftSpawnBlacklist = Lists.newArrayList(
            "ait-tardis", "ait:tardis_dimension_type", AITDimensions.TIME_VORTEX_WORLD.getValue().toString(), "minecraft:the_end", "ait:space");

    @AutoGen(category = CATEGORY)
    @ListGroup(valueFactory = StringListFactory.class, controllerFactory = StringListFactory.class)
    @CustomDescription(value = "Blacklist dimensions that Rifts can drop.")
    @SerialEntry public List<String> riftDropBlacklist = Lists.newArrayList(
            "ait-tardis", "ait:tardis_dimension_type", AITDimensions.TIME_VORTEX_WORLD.getValue().toString(), "minecraft:the_end", "ait:space");

    @AutoGen(category = CATEGORY)
    @IntField(min = 1)
    @CustomDescription(value = "How fast the TARDIS can travel per tick.")
    @CustomImage(value = "textures/yacl3/server/tardis-speed.webp", width = 854, height = 480)
    @SerialEntry public int travelPerTick = 2;

    @AutoGen(category = CATEGORY)
    @Boolean(formatter = Boolean.Formatter.YES_NO, colored = true)
    @CustomDescription(value = "Sends AIT packets in bulk.")
    @SerialEntry public boolean sendBulk = true;

    @AutoGen(category = CATEGORY)
    @IntField(min = -1)
    @CustomDescription(value = "The maximum TARDIS allowed in a world.")
    @CustomImage(value = "textures/yacl3/server/multi-tardis.webp", width = 854, height = 480)
    @SerialEntry public int maxTardises = -1;

    @AutoGen(category = CATEGORY)
    @Boolean(formatter = Boolean.Formatter.YES_NO, colored = true)
    @CustomDescription(value = "Turns off safeguards for when running the mod.")
    @SerialEntry public boolean disableSafeguards = false;

    @AutoGen(category = CATEGORY)
    @FloatSlider(min = 0, max = 16, step = 0.1f)
    @SerialEntry public float crashSoundVolume = 1f;

    @AutoGen(category = CATEGORY)
    @FloatSlider(min = 0, max = 16, step = 0.1f)
    @SerialEntry public float flightSoundVolume = 2f;

    public static class StringListFactory implements ListGroup.ValueFactory<String>, ListGroup.ControllerFactory<String> {

        // used by the reflections
        public StringListFactory() { }

        @Override
        public ControllerBuilder<String> createController(ListGroup annotation, ConfigField<List<String>> field, OptionAccess storage, Option<String> option) {
            return StringControllerBuilder.create(option);
        }

        @Override
        public String provideNewValue() {
            return "";
        }
    }
}
