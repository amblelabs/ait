package dev.amble.ait.config;

import java.util.List;

import com.google.common.collect.Lists;
import dev.amble.ait.AITMod;
import dev.amble.ait.core.AITDimensions;
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

public class AITServerConfig {

    public static final String CATEGORY = "server";
    public static final String HOME_CATEGORY = "tardis_home";
    public static final String HOMEWARD_PROTOCOLS_CATEGORY = "homeward_protocols";

    public static final ConfigClassHandler<AITServerConfig> INSTANCE = ConfigClassHandler.createBuilder(AITServerConfig.class)
            .id(YACLPlatform.rl(AITMod.MOD_ID, "server"))
            .serializer(config -> GsonConfigSerializerBuilder.create(config)
                    .setPath(YACLPlatform.getConfigDir().resolve("ait-server.json5"))
                    .setJson5(true)
                    .build())
            .build();

    public void normalizeLinkedRanges() {
        if (this.netherReturnMinDelayMinutes == -1 || this.netherReturnMaxDelayMinutes == -1) {
            this.netherReturnMinDelayMinutes = 0;
            this.netherReturnMaxDelayMinutes = 0;
            return;
        }

        this.netherReturnMinDelayMinutes = Math.max(0, this.netherReturnMinDelayMinutes);
        this.netherReturnMaxDelayMinutes = Math.max(this.netherReturnMinDelayMinutes,
                this.netherReturnMaxDelayMinutes);
    }

    @AutoGen(category = CATEGORY)
    @Boolean(formatter = Boolean.Formatter.YES_NO, colored = true)
    @SerialEntry public boolean minifyJson = false;

    @AutoGen(category = CATEGORY)
    @MasterTickBox({"ghostMonumentReturnHomeDelayMinutes"})
    @SerialEntry public boolean ghostMonument = true;


    @AutoGen(category = HOMEWARD_PROTOCOLS_CATEGORY)
    @IntField(min = 0, max = 525_600)
    @SerialEntry public int ghostMonumentReturnHomeDelayMinutes = 5;

    @AutoGen(category = HOMEWARD_PROTOCOLS_CATEGORY)
    @IntField(min = 0, max = 525_600)
    @SerialEntry public int siegeReturnHomeDelayMinutes = 1980;

    @AutoGen(category = HOMEWARD_PROTOCOLS_CATEGORY)
    @IntField(min = 1, max = 256)
    @SerialEntry public int homeRadius = 100;

    @AutoGen(category = HOMEWARD_PROTOCOLS_CATEGORY)
    @IntField(min = 1, max = 2_000_000)
    @SerialEntry public int automaticFlightTimeReduction = 500_000;

    @AutoGen(category = HOMEWARD_PROTOCOLS_CATEGORY)
    @IntField(min = 0, max = 25_000)
    @SerialEntry public int automaticRefuelMinimum = 500;

    @AutoGen(category = HOMEWARD_PROTOCOLS_CATEGORY)
    @IntField(min = 0, max = 25_000)
    @SerialEntry public int forcedEntryArtronDumpFuel = 500;

    @AutoGen(category = HOMEWARD_PROTOCOLS_CATEGORY)
    @IntField(min = 0, max = 3600)
    @SerialEntry public int hailMaryReturnDelaySeconds = 30;

    @AutoGen(category = HOMEWARD_PROTOCOLS_CATEGORY)
    @IntField(min = -1, max = 1440)
    @SerialEntry public int netherReturnMinDelayMinutes = 4;

    @AutoGen(category = HOMEWARD_PROTOCOLS_CATEGORY)
    @IntField(min = -1, max = 1440)
    @SerialEntry public int netherReturnMaxDelayMinutes = 6;

    @AutoGen(category = HOMEWARD_PROTOCOLS_CATEGORY)
    @IntField(min = 1, max = 256)
    @SerialEntry public int bossDetectionRadius = 100;

    @AutoGen(category = HOMEWARD_PROTOCOLS_CATEGORY)
    @IntField(min = 1, max = 300)
    @SerialEntry public int automaticThreatCheckIntervalSeconds = 10;

    @AutoGen(category = HOMEWARD_PROTOCOLS_CATEGORY)
    @IntField(min = 0, max = 300)
    @SerialEntry public int bossMissingGraceSeconds = 60;

    @AutoGen(category = HOMEWARD_PROTOCOLS_CATEGORY)
    @IntField(min = 1, max = 3600)
    @SerialEntry public int missingExteriorCheckIntervalSeconds = 60;

    @AutoGen(category = HOMEWARD_PROTOCOLS_CATEGORY)
    @MasterTickBox({"hailMaryFallRescueRange", "hailMaryLevitationSeconds", "hailMaryRescuePullStrength"})
    @SerialEntry public boolean hailMaryFallAndVoidRescue = false;

    @AutoGen(category = HOMEWARD_PROTOCOLS_CATEGORY)
    @IntField(min = 1, max = 64)
    @SerialEntry public int hailMaryFallRescueRange = 10;

    @AutoGen(category = HOMEWARD_PROTOCOLS_CATEGORY)
    @IntField(min = 1, max = 300)
    @SerialEntry public int hailMaryLevitationSeconds = 10;

    @AutoGen(category = HOMEWARD_PROTOCOLS_CATEGORY)
    @FloatField(min = 0.01f, max = 1)
    @SerialEntry public float hailMaryRescuePullStrength = 0.2f;

    @AutoGen(category = HOMEWARD_PROTOCOLS_CATEGORY)
    @Boolean(formatter = Boolean.Formatter.YES_NO, colored = true)
    @SerialEntry public boolean keepHailMaryActive = false;

    @AutoGen(category = HOMEWARD_PROTOCOLS_CATEGORY)
    @Boolean(formatter = Boolean.Formatter.YES_NO, colored = true)
    @SerialEntry public boolean hailMaryActivatesDespiteTotems = true;

    @AutoGen(category = CATEGORY)
    @Boolean(formatter = Boolean.Formatter.YES_NO, colored = true)
    @SerialEntry public boolean lockDimensions = true;

    @AutoGen(category = CATEGORY)
    @Boolean(formatter = Boolean.Formatter.YES_NO, colored = true)
    @SerialEntry public boolean rwfEnabled = false;

    @AutoGen(category = CATEGORY)
    @Boolean(formatter = Boolean.Formatter.YES_NO, colored = true)
    @SerialEntry public boolean allowPortalsBoti = true;

    @AutoGen(category = CATEGORY)
    @Boolean(formatter = Boolean.Formatter.YES_NO, colored = true)
    @SerialEntry public boolean tntCanTeleportThroughDoors = true;

    @AutoGen(category = CATEGORY)
    @Boolean(formatter = Boolean.Formatter.YES_NO, colored = true)
    @SerialEntry public boolean hypercubesEnabled = true;

    @AutoGen(category = CATEGORY)
    @IntField(min = 0)
    @CustomDescription(value = "The levenshtein distance allows for typos when using handles. Distances lower to 0 are stricter while higher values like 5 are more lenient.")
    @CustomImage(value = "textures/yacl3/server/levenshtein.webp", width = 1909, height = 349)
    @SerialEntry public int handlesLevenshteinDistance = 2;

    @AutoGen(category = CATEGORY)
    @CustomDescription(value = "Dimensions listed here will be excluded. Ignored when the whitelist has entries.")
    @ListGroup(valueFactory = StringListFactory.class, controllerFactory = StringListFactory.class)
    @SerialEntry public List<String> projectorBlacklist = Lists.newArrayList(
            "ait-tardis");

    @AutoGen(category = CATEGORY)
    @CustomDescription(value = "When populated, only these dimensions will be allowed and the blacklist is ignored.")
    @ListGroup(valueFactory = StringListFactory.class, controllerFactory = StringListFactory.class)
    @SerialEntry public List<String> projectorWhitelist = Lists.newArrayList();

    @AutoGen(category = CATEGORY)
    @CustomDescription(value = "Dimensions listed here will be excluded. Ignored when the whitelist has entries.")
    @ListGroup(valueFactory = StringListFactory.class, controllerFactory = StringListFactory.class)
    @SerialEntry public List<String> travelBlacklist = Lists.newArrayList(
            "ait-tardis", "ait:tardis_dimension_type", AITDimensions.TIME_VORTEX_WORLD.getValue().toString(), "ait:space");

    @AutoGen(category = CATEGORY)
    @CustomDescription(value = "When populated, only these dimensions will be allowed and the blacklist is ignored.")
    @ListGroup(valueFactory = StringListFactory.class, controllerFactory = StringListFactory.class)
    @SerialEntry public List<String> travelWhitelist = Lists.newArrayList();

    @AutoGen(category = CATEGORY)
    @IntField(min = 1)
    @SerialEntry public int travelPerTick = 2;

    @AutoGen(category = CATEGORY)
    @IntField(min = 256, max = 65536)
    @SerialEntry public int astralMapBiomeLocatorRange = 6400;

    @AutoGen(category = CATEGORY)
    @Boolean(formatter = Boolean.Formatter.YES_NO, colored = true)
    @SerialEntry public boolean sendBulk = true;

    @AutoGen(category = CATEGORY)
    @IntField(min = -1)
    @SerialEntry public int maxTardises = -1;

    @AutoGen(category = CATEGORY)
    @Boolean(formatter = Boolean.Formatter.YES_NO, colored = true)
    @SerialEntry public boolean disableSafeguards = false;

    @AutoGen(category = CATEGORY)
    @FloatSlider(min = 0, max = 16, step = 0.1f)
    @SerialEntry public float crashSoundVolume = 1f;

    @AutoGen(category = CATEGORY)
    @FloatSlider(min = 0, max = 16, step = 0.1f)
    @SerialEntry public float flightSoundVolume = 2f;

    @AutoGen(category = CATEGORY)
    @IntSlider(min = 1, max = 128, step = 1)
    @SerialEntry public int maxStabilizedSpeed = 4;

    @AutoGen(category = HOME_CATEGORY)
    @IntField(min = 0, max = 525_600)
    @SerialEntry public int homeRelocationCooldownMinutes = 60;

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
