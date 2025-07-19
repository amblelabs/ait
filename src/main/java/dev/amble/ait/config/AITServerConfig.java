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
    @SerialEntry public boolean minifyJson = false;

    @AutoGen(category = CATEGORY)
    @Boolean(formatter = Boolean.Formatter.YES_NO, colored = true)
    @SerialEntry public boolean ghostMonument = true;

    @AutoGen(category = CATEGORY)
    @Boolean(formatter = Boolean.Formatter.YES_NO, colored = true)
    @SerialEntry public boolean lockDimensions = true;

    @AutoGen(category = CATEGORY)
    @Boolean(formatter = Boolean.Formatter.YES_NO, colored = true)
    @SerialEntry public boolean rwfEnabled = false;

    @AutoGen(category = CATEGORY)
    @Boolean(formatter = Boolean.Formatter.YES_NO, colored = true)
    @SerialEntry public boolean tntCanTeleportThroughDoors = true;

    @AutoGen(category = CATEGORY)
    @Boolean(formatter = Boolean.Formatter.YES_NO, colored = true)
    @SerialEntry public boolean hypercubesEnabled = true;

    @AutoGen(category = CATEGORY)
    @ListGroup(valueFactory = StringListFactory.class, controllerFactory = StringListFactory.class)
    @SerialEntry public List<String> projectorBlacklist = Lists.newArrayList(
            "ait-tardis");

    @AutoGen(category = CATEGORY)
    @ListGroup(valueFactory = StringListFactory.class, controllerFactory = StringListFactory.class)
    @SerialEntry public List<String> travelBlacklist = Lists.newArrayList(
            "ait-tardis", "ait:tardis_dimension_type", AITDimensions.TIME_VORTEX_WORLD.getValue().toString());

    @AutoGen(category = CATEGORY)
    @ListGroup(valueFactory = StringListFactory.class, controllerFactory = StringListFactory.class)
    @SerialEntry public List<String> riftSpawnBlacklist = Lists.newArrayList(
            "ait-tardis", "ait:tardis_dimension_type", AITDimensions.TIME_VORTEX_WORLD.getValue().toString(), "minecraft:the_end");

    @AutoGen(category = CATEGORY)
    @ListGroup(valueFactory = StringListFactory.class, controllerFactory = StringListFactory.class)
    @SerialEntry public List<String> riftDropBlacklist = Lists.newArrayList(
            "ait-tardis", "ait:tardis_dimension_type", AITDimensions.TIME_VORTEX_WORLD.getValue().toString(), "minecraft:the_end");

    @AutoGen(category = CATEGORY)
    @IntField(min = 1)
    @SerialEntry public int travelPerTick = 2;

    @AutoGen(category = CATEGORY)
    @Boolean(formatter = Boolean.Formatter.YES_NO, colored = true)
    @SerialEntry public boolean sendBulk = true;

    @AutoGen(category = CATEGORY)
    @IntField(min = -1)
    @SerialEntry public int maxTardises = -1;

    @AutoGen(category = CATEGORY)
    @Boolean(formatter = Boolean.Formatter.YES_NO, colored = true)
    @SerialEntry public boolean disableSafeguards = false;

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
